#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
服务器启动 6 小时提醒小程序（常驻）：
  1. 轮询检测生产 MC 服务器进程（命令行特征 'win_args.txt'，不依赖 server_pid.txt）
  2. 检测到一次新的服务器启动（进程 PID / 创建时间变化）→ 以该启动时刻重新计时
  3. 服务器持续运行满 6 小时 → Windows 桌面弹窗提醒（置顶，点击确定关闭）
  4. 每次启动只提醒一次；服务器重启 / 崩溃重启后自动重新计时

用法:
    python tools/server_reminder.py [--interval 30] [--hours 6] [--test]
      --test  立即弹出一个 5 秒自动消失的测试弹窗后退出（验证弹窗可用）
"""
import argparse
import json
import os
import subprocess
import sys
import time
from datetime import datetime

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
STATE_FILE = os.path.join(SCRIPT_DIR, "reminder_state.json")
LOG_FILE = os.path.join(SCRIPT_DIR, "reminder.log")


def log(msg):
    line = f"[{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}] {msg}"
    print(line, flush=True)
    try:
        with open(LOG_FILE, "a", encoding="utf-8") as f:
            f.write(line + "\n")
    except Exception:
        pass


def _ps(cmd):
    try:
        r = subprocess.run(['powershell', '-NoProfile', '-Command', cmd], capture_output=True, timeout=30)
        # 字节级解码 + 容错：中文 Windows 的本地化输出可能非 UTF-8，避免解码异常
        return (r.stdout or b"").decode("utf-8", errors="replace")
    except Exception:
        return ""


def find_server():
    """精确定位生产服务器 java 进程，返回 [(pid, 创建时刻 epoch), ...]。

    防误伤其他 java 服务器（如 F 盘飞行模拟服）：
    1. 优先按监听端口归属（RCON 25575 → 游戏 25565）；
    2. 服务器启动期端口未监听时回退命令行特征 'win_args.txt'，且仅唯一匹配才返回。
    """
    out = _ps("Get-CimInstance Win32_Process -Filter \"Name='java.exe'\" "
              "| Select-Object ProcessId, CreationDate | ConvertTo-Csv -NoTypeInformation")
    created_by_pid = {}
    for line in out.splitlines()[1:]:
        parts = line.split(",")
        if len(parts) < 2:
            continue
        try:
            pid = int(parts[0].strip('"'))
        except ValueError:
            continue
        created = 0.0
        cim = parts[1].strip('"')
        # 兼容两种格式：CIM 原始格式 yyyyMMddHHmmss... 与本地化显示格式 yyyy/M/d HH:mm:ss
        try:
            if len(cim) >= 14 and cim[4].isdigit() and cim[8].isdigit():
                created = datetime.strptime(cim[:14], "%Y%m%d%H%M%S").timestamp()
            else:
                created = datetime.strptime(cim[:19], "%Y/%m/%d %H:%M:%S").timestamp()
        except ValueError:
            created = 0.0
        created_by_pid[pid] = created if created else time.time()

    # 1) 端口归属优先
    for port in (25575, 25565):
        owners = _ps(f"Get-NetTCPConnection -LocalPort {port} -State Listen -ErrorAction SilentlyContinue "
                     "| Select-Object -ExpandProperty OwningProcess")
        pids = [int(x) for x in owners.split() if x.isdigit() and int(x) in created_by_pid]
        if pids:
            return [(p, created_by_pid[p]) for p in pids]
    # 2) 回退：命令行特征唯一匹配（排除 F 盘 maid_pilot_train 训练服，避免误识别）
    cands_out = _ps("Get-CimInstance Win32_Process -Filter \"Name='java.exe'\" "
                    "| Where-Object { $_.CommandLine -like '*win_args.txt*' -and $_.CommandLine -notlike '*-Dmaid_pilot_train*' } "
                    "| Select-Object -ExpandProperty ProcessId")
    cands = [int(x) for x in cands_out.split() if x.isdigit() and int(x) in created_by_pid]
    return [(cands[0], created_by_pid[cands[0]])] if len(cands) == 1 else []


def popup(title, text, seconds=0):
    """桌面弹窗：信息图标 + 置顶；seconds>0 则自动消失（测试用），0 = 等用户点击确定。

    用 WScript.Shell.Popup（用户会话内直接显示桌面弹窗）；不阻塞主循环（后台进程弹出）。
    """
    # 单引号字符串内直接嵌入换行即可（PowerShell 单引号字符串支持多行）
    ps = f"(New-Object -ComObject WScript.Shell).Popup('{text}',{seconds},'{title}',0x1040)"
    try:
        subprocess.Popen(["powershell", "-NoProfile", "-Command", ps],
                         cwd=SCRIPT_DIR, stdin=subprocess.DEVNULL)
        return True
    except Exception as e:
        log(f"popup launch failed: {e}")
        return False


def load_state():
    if os.path.exists(STATE_FILE):
        try:
            with open(STATE_FILE, encoding="utf-8") as f:
                return json.load(f)
        except Exception:
            pass
    return {"pid": None, "start": None, "reminded": False, "seen": False}


def save_state(state):
    with open(STATE_FILE, "w", encoding="utf-8") as f:
        json.dump(state, f, indent=2)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--interval", type=int, default=30, help="轮询间隔（秒）")
    ap.add_argument("--hours", type=float, default=6.0, help="提醒时长（小时）")
    ap.add_argument("--test", action="store_true", help="立即弹出 5 秒测试弹窗后退出")
    args = ap.parse_args()

    if args.test:
        popup("服务器启动提醒（测试）", "弹窗功能正常！\n5 秒后自动关闭。", seconds=5)
        print("test popup fired (auto-close in 5s)")
        return 0

    state = load_state()
    log(f"reminder started: interval={args.interval}s hours={args.hours} "
        f"(state: pid={state.get('pid')} reminded={state.get('reminded')})")
    last_down_log = 0.0

    while True:
        try:
            servers = find_server()
            if servers:
                pid, created = servers[0]
                if state.get("pid") != pid:
                    # 检测到新的服务器启动（新 PID / 重启）
                    start = created if created else time.time()
                    state = {"pid": pid, "start": start, "reminded": False, "seen": True}
                    save_state(state)
                    log(f"new server start detected: pid={pid} start={datetime.fromtimestamp(start).strftime('%H:%M:%S')}"
                        f" (remind at {datetime.fromtimestamp(start + args.hours * 3600).strftime('%H:%M:%S')})")
                elif not state.get("reminded") and state.get("start") \
                        and time.time() - state["start"] >= args.hours * 3600:
                    # 满 6 小时 → 弹窗提醒（每次启动一次）
                    st = datetime.fromtimestamp(state["start"]).strftime("%Y-%m-%d %H:%M:%S")
                    popup("服务器启动 6 小时提醒",
                          f"服务器已持续运行 {args.hours:.0f} 小时（启动于 {st}）。\n\n"
                          "训练迭代窗口已到：iterate 将归档轨迹并训练新模型。\n"
                          "请检查训练进度 / 模型版本。")
                    state["reminded"] = True
                    save_state(state)
                    log(f"reminder shown (server start {st})")
            else:
                if state.get("seen") and time.time() - last_down_log > 60:
                    log("server not running (waiting for next start)")
                    last_down_log = time.time()
            time.sleep(args.interval)
        except KeyboardInterrupt:
            log("reminder interrupted, exiting")
            return 0
        except Exception as e:
            log(f"loop exception: {e!r}")
            time.sleep(args.interval)


if __name__ == "__main__":
    sys.exit(main())
