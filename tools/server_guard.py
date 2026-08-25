#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
生产服务器崩溃监控 Hook（常驻，15 秒轮询）：
  1. 进程检测按命令行特征（'win_args.txt'，不依赖陈旧的 server_pid.txt）
  2. 异常终止判定：进程消失且无优雅标记 / crash-reports 新文件 / latest.log FATAL
  3. 触发后收集：crash-reports、latest/debug/server.log 尾部、jstack → guard_reports/<ts>/
  4. 自动分析异常签名（内置已知签名库）→ report.md
  5. 保守自愈：
     - 模型损坏签名 → 回退上一版本模型（rl_model_v*.bin）
     - 同类崩溃连续 2 次 → arena.properties enabled=false（备份后改写）
  6. 自动重启（start.bat）继续训练
  7. 全程写 guard.log / guard_state.json

用法:
    python tools/server_guard.py --server-dir <prod_server> [--models-dir <train/models>] [--interval 15]
"""
import argparse
import glob
import json
import os
import shutil
import subprocess
import sys
import time
from datetime import datetime

GRACE_FLAG = "guard_grace.stop"
STATE_FILE = "guard_state.json"

# 已知异常签名库：(匹配片段, 名称, 建议动作)
SIGNATURES = [
    ("invalid dist DEDICATED_SERVER", "client_class_on_server",
     "专用服务器加载了客户端类（Nightfall 粒子 / EpicFight 动画等），检查是否已是最新版 mod 组合"),
    ("ExceptionInInitializerError", "class_init_error",
     "类静态初始化失败，通常伴随上述客户端类问题"),
    ("OutOfMemoryError", "oom",
     "内存不足，考虑在 user_jvm_args.txt 增加 -Xmx"),
    ("[RL] failed to load model", "model_corrupt",
     "RL 模型文件损坏或格式错误，已尝试回退上一版本模型"),
    ("RlModel", "model_corrupt",
     "RL 模型加载/推理异常，已尝试回退上一版本模型"),
    ("Exception in server tick loop", "tick_loop_exception",
     "服务器 tick 循环异常（通用崩溃）"),
    ("StackOverflowError", "stack_overflow",
     "栈溢出（递归过深）"),
]


def log(msg, server_dir):
    line = f"[{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}] {msg}"
    print(line, flush=True)
    try:
        with open(os.path.join(server_dir, "guard", "guard.log"), "a", encoding="utf-8") as f:
            f.write(line + "\n")
    except Exception:
        pass


def load_state(server_dir):
    path = os.path.join(server_dir, "guard", STATE_FILE)
    if os.path.exists(path):
        try:
            with open(path, encoding="utf-8") as f:
                return json.load(f)
        except Exception:
            pass
    return {"seen_running": False, "crash_counts": {}, "last_crash_scan": 0.0}


def save_state(server_dir, state):
    path = os.path.join(server_dir, "guard", STATE_FILE)
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        json.dump(state, f, indent=2)


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


def grace_flag_recent(server_dir, window_sec=3600):
    path = os.path.join(server_dir, GRACE_FLAG)
    try:
        t = float(open(path, encoding="utf-8").read().strip())
        return time.time() - t < window_sec
    except Exception:
        return False


def tail(path, lines=3000):
    try:
        with open(path, "r", encoding="utf-8", errors="replace") as f:
            data = f.readlines()
        return "".join(data[-lines:])
    except Exception as e:
        return f"(read failed: {e})\n"


def collect_report(server_dir, ts, pids, last_scan=0.0):
    """收集崩溃证据 → guard_reports/<ts>/，返回收集目录。

    崩溃报告只拷贝 last_scan 之后的新文件——避免每次崩溃都把历史崩溃报告
    重新拷入并分析（会误判"同类崩溃重复"并触发错误的自愈）。
    """
    out_dir = os.path.join(server_dir, "guard", "guard_reports", ts)
    os.makedirs(out_dir, exist_ok=True)
    # 崩溃报告（仅新增）
    cr_dir = os.path.join(server_dir, "crash-reports")
    if os.path.isdir(cr_dir):
        for f in glob.glob(os.path.join(cr_dir, "*")):
            if os.path.getmtime(f) > last_scan:
                shutil.copy2(f, os.path.join(out_dir, os.path.basename(f)))
    # 日志
    for name in ["latest.log", "debug.log"]:
        p = os.path.join(server_dir, "logs", name)
        if os.path.exists(p):
            shutil.copy2(p, os.path.join(out_dir, name))
    p = os.path.join(server_dir, "server.log")
    if os.path.exists(p):
        with open(os.path.join(out_dir, "server.log.tail"), "w", encoding="utf-8", errors="replace") as f:
            f.write(tail(p))
    # 线程转储（进程若仍在）
    for pid in pids:
        for jstack in [r"E:\Program Files\Java\jdk-17\bin\jstack.exe"]:
            if os.path.exists(jstack):
                try:
                    out = subprocess.run([jstack, str(pid)], capture_output=True, text=True, timeout=60)
                    with open(os.path.join(out_dir, f"jstack_{pid}.txt"), "w", encoding="utf-8", errors="replace") as f:
                        f.write(out.stdout or out.stderr or "")
                except Exception as e:
                    log(f"jstack failed: {e}", server_dir)
    return out_dir


def analyze(server_dir, out_dir):
    """扫描收集到的证据，匹配签名，返回 (signatures, 摘要文本)。"""
    text = ""
    for f in os.listdir(out_dir):
        p = os.path.join(out_dir, f)
        if os.path.isfile(p) and f.endswith((".txt", ".log", ".tail")):
            text += tail(p, 5000)
    matched = []
    for needle, name, advice in SIGNATURES:
        if needle in text:
            matched.append({"signature": name, "needle": needle, "advice": advice})
    report = ["# 服务器崩溃分析报告", "", f"时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}", ""]
    report.append("## 匹配签名")
    if matched:
        for m in matched:
            report.append(f"- **{m['signature']}** (`{m['needle']}`): {m['advice']}")
    else:
        report.append("- 未匹配已知签名（需人工查看崩溃报告首帧）")
    report.append("")
    report.append("## 收集的文件")
    for f in sorted(os.listdir(out_dir)):
        report.append(f"- {f}")
    with open(os.path.join(out_dir, "report.md"), "w", encoding="utf-8") as f:
        f.write("\n".join(report))
    return matched


def heal(server_dir, models_dir, state, matched, out_dir):
    """保守自愈：模型损坏回退；同类崩溃连续 2 次停用竞技场。"""
    sigs = {m["signature"] for m in matched}
    config_dir = os.path.join(server_dir, "config", "eftlm_stylish")
    # 1) 模型损坏 → 回退上一版本
    if "model_corrupt" in sigs:
        candidates = glob.glob(os.path.join(config_dir, "rl_model_v*.bin"))
        if models_dir:
            candidates += glob.glob(os.path.join(models_dir, "rl_model_v*.bin"))
        candidates = [c for c in candidates if "prev_deployed" not in c]
        if candidates:
            candidates.sort(key=os.path.getmtime, reverse=True)
            target = os.path.join(config_dir, "rl_model.bin")
            shutil.copy2(candidates[0], target)
            log(f"[heal] model rolled back to {candidates[0]}", server_dir)
        else:
            log("[heal] no backup model found, keep current", server_dir)
    # 2) 同类崩溃连续 2 次 → 停用竞技场
    for m in matched:
        key = m["signature"]
        state["crash_counts"][key] = state["crash_counts"].get(key, 0) + 1
        if state["crash_counts"][key] >= 2:
            arena = os.path.join(config_dir, "arena.properties")
            if os.path.exists(arena):
                bak = arena + f".disabled_{datetime.now().strftime('%Y%m%d_%H%M%S')}"
                shutil.copy2(arena, bak)
                lines = []
                with open(arena, encoding="utf-8") as f:
                    for line in f:
                        if line.strip().startswith("enabled="):
                            lines.append("enabled=false\n")
                        else:
                            lines.append(line)
                with open(arena, "w", encoding="utf-8") as f:
                    f.writelines(lines)
                log(f"[heal] same crash twice ({key}), arena disabled (backup: {bak})", server_dir)


def restart(server_dir):
    log("restarting server (start.bat)...", server_dir)
    flags = 0x00000008 | 0x00000200  # DETACHED_PROCESS | CREATE_NEW_PROCESS_GROUP
    try:
        subprocess.Popen(["cmd", "/c", "start.bat"], cwd=server_dir,
                         creationflags=flags,
                         stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL,
                         stdin=subprocess.DEVNULL, close_fds=True)
        return True
    except Exception as e:
        log(f"restart failed: {e}", server_dir)
        return False


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--server-dir", required=True)
    ap.add_argument("--models-dir", default=None, help="训练模型备份目录（train/models），自愈回退用")
    ap.add_argument("--interval", type=int, default=15)
    args = ap.parse_args()

    os.makedirs(os.path.join(args.server_dir, "guard"), exist_ok=True)
    state = load_state(args.server_dir)
    if state.get("last_crash_scan", 0.0) <= 0.0:
        # 首次启动：以当前时间为基线，历史崩溃报告不再触发处理（避免重启后重复归档）
        state["last_crash_scan"] = time.time()
        save_state(args.server_dir, state)
    log(f"guard started: server={args.server_dir} interval={args.interval}s", args.server_dir)

    while True:
        try:
            pids = find_server_pids()
            if pids:
                state["seen_running"] = True
                save_state(args.server_dir, state)
                # 新崩溃报告出现（服务器可能即将/已经崩溃）
                cr_dir = os.path.join(args.server_dir, "crash-reports")
                if os.path.isdir(cr_dir):
                    new_crs = [f for f in glob.glob(os.path.join(cr_dir, "*"))
                               if os.path.getmtime(f) > state["last_crash_scan"]]
                    if new_crs:
                        log(f"crash report detected while server alive: {[os.path.basename(f) for f in new_crs]}", args.server_dir)
                        ts = datetime.now().strftime("%Y%m%d_%H%M%S")
                        out_dir = collect_report(args.server_dir, ts, pids, state["last_crash_scan"])
                        matched = analyze(args.server_dir, out_dir)
                        # 等待进程退出确认（最长 90s）
                        deadline = time.time() + 90
                        while time.time() < deadline and find_server_pids():
                            time.sleep(5)
                        if not find_server_pids():
                            log("server exited after crash report", args.server_dir)
                            heal(args.server_dir, args.models_dir, state, matched, out_dir)
                            save_state(args.server_dir, state)
                            restart(args.server_dir)
                        else:
                            log("server survived (report archived only)", args.server_dir)
                        state["last_crash_scan"] = time.time()
                        save_state(args.server_dir, state)
            else:
                # 进程不在：区分优雅停服与崩溃
                if grace_flag_recent(args.server_dir):
                    if state.get("expected_down") is None:
                        log("graceful stop observed (grace flag present), monitoring for restart", args.server_dir)
                    state["expected_down"] = True
                    save_state(args.server_dir, state)
                elif not state["seen_running"]:
                    log("server not running yet, waiting...", args.server_dir)
                else:
                    log("SERVER DOWN WITHOUT GRACE FLAG — treating as crash", args.server_dir)
                    ts = datetime.now().strftime("%Y%m%d_%H%M%S")
                    out_dir = collect_report(args.server_dir, ts, [], state["last_crash_scan"])
                    matched = analyze(args.server_dir, out_dir)
                    heal(args.server_dir, args.models_dir, state, matched, out_dir)
                    save_state(args.server_dir, state)
                    restart(args.server_dir)
            time.sleep(args.interval)
        except KeyboardInterrupt:
            log("guard interrupted, exiting", args.server_dir)
            return 0
        except Exception as e:
            log(f"guard loop exception: {e!r}", args.server_dir)
            time.sleep(args.interval)


if __name__ == "__main__":
    sys.exit(main())
