#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
优雅停止生产 Minecraft 服务器：
  1. 按命令行特征定位 java 服务器进程（不依赖陈旧的 server_pid.txt）
  2. 写优雅标记 guard_grace.stop（server_guard 据此区分"正常停服"与"崩溃"）
  3. RCON 发送 stop
  4. 超时未退出则 taskkill /F 强制结束

用法:
    python tools/stop_server.py --server-dir <prod_server> [--timeout 180]
"""
import argparse
import os
import socket
import struct
import subprocess
import sys
import time

GRACE_FLAG = "guard_grace.stop"


class RCON:
    def __init__(self, host, port, password, timeout=10):
        self.sock = socket.create_connection((host, port), timeout=timeout)
        self.send(3, password.encode())
        resp, _ = self.recv()
        if resp != 2:
            raise RuntimeError("RCON 认证失败")

    def send(self, ptype, payload):
        data = struct.pack("<ii", len(payload) + 10, 0) + struct.pack("<i", ptype) + payload + b"\x00\x00"
        self.sock.sendall(data)

    def recv(self):
        header = self._recv_exact(12)
        length, req_id, ptype = struct.unpack("<iii", header)
        return ptype, self._recv_exact(length - 8)

    def _recv_exact(self, n):
        buf = b""
        while len(buf) < n:
            chunk = self.sock.recv(n - len(buf))
            if not chunk:
                raise RuntimeError("连接关闭")
            buf += chunk
        return buf

    def cmd(self, command):
        self.send(2, command.encode())
        _, body = self.recv()
        return body.decode("utf-8", "replace").strip()

    def close(self):
        try:
            self.sock.close()
        except Exception:
            pass


def _ps(cmd):
    try:
        r = subprocess.run(['powershell', '-NoProfile', '-Command', cmd], capture_output=True, timeout=30)
        return (r.stdout or b"").decode("utf-8", errors="replace")
    except Exception:
        return ""


def _java_pids():
    out = _ps("Get-CimInstance Win32_Process -Filter \"Name='java.exe'\" "
              "| Select-Object -ExpandProperty ProcessId")
    return {int(x) for x in out.split() if x.isdigit()}


def _port_owner_pids(port):
    out = _ps(f"Get-NetTCPConnection -LocalPort {port} -State Listen -ErrorAction SilentlyContinue "
              "| Select-Object -ExpandProperty OwningProcess")
    return {int(x) for x in out.split() if x.isdigit()}


def find_server_pids():
    """精确定位生产服务器 java 进程，防误伤其他 java 服务器（如 F 盘飞行模拟服）：

    1. 优先按监听端口归属：RCON 25575 → 游戏 25565（生产服务器独占这两个端口）；
    2. 服务器启动期端口尚未监听时，回退命令行特征 'win_args.txt'，
       且仅在恰好匹配到一个 java 进程时返回（多个候选 = 无法区分，返回空保安全）。
    """
    java = _java_pids()
    for port in (25575, 25565):
        owners = _port_owner_pids(port) & java
        if owners:
            return sorted(owners)
    # 回退：命令行特征唯一匹配（排除 F 盘 maid_pilot_train 训练服，避免误识别）
    out = _ps("Get-CimInstance Win32_Process -Filter \"Name='java.exe'\" "
              "| Where-Object { $_.CommandLine -like '*win_args.txt*' -and $_.CommandLine -notlike '*-Dmaid_pilot_train*' } "
              "| Select-Object -ExpandProperty ProcessId")
    cands = sorted({int(x) for x in out.split() if x.isdigit()})
    return cands if len(cands) == 1 else []


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--server-dir", required=True)
    ap.add_argument("--host", default="127.0.0.1")
    ap.add_argument("--port", type=int, default=25575)
    ap.add_argument("--password", default="maidpilot123")
    ap.add_argument("--timeout", type=int, default=180)
    args = ap.parse_args()

    pids = find_server_pids()
    if not pids:
        print("[stop] no server process found (already stopped?)")
        return 0
    print(f"[stop] server process(es): {pids}")

    # 优雅标记：guard 据此判定为人工停服而非崩溃
    flag_path = os.path.join(args.server_dir, GRACE_FLAG)
    with open(flag_path, "w", encoding="utf-8") as f:
        f.write(f"{time.time()}\n")
    print(f"[stop] grace flag written: {flag_path}")

    # RCON 优雅停服
    try:
        r = RCON(args.host, args.port, args.password)
        out = r.cmd("stop")
        print(f"[stop] rcon stop -> {out!r}")
        r.close()
    except Exception as e:
        print(f"[stop] rcon failed ({e}), will force kill after timeout")

    deadline = time.time() + args.timeout
    while time.time() < deadline:
        if not find_server_pids():
            print("[stop] server exited gracefully")
            return 0
        time.sleep(5)

    # 超时强制结束
    alive = find_server_pids()
    for pid in alive:
        subprocess.run(["taskkill", "/F", "/PID", str(pid)], capture_output=True, text=True)
        print(f"[stop] force killed pid {pid}")
    print("[stop] done")
    return 0


if __name__ == "__main__":
    sys.exit(main())
