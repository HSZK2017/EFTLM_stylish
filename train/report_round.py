#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""训练/运行状态一键盘点（2026-09-10）。

用途：宿舍作息（23:30 断电断网）下无法守着 6 小时轮次等结果——随时跑这一条命令即可看到
"修复是否生效、这一轮训练/部署结果如何、有没有异常"，无需 RCON、无需在线。

    python train/report_round.py                      # 默认读 prod_server（可用 EFTLM_SERVER_DIR 覆盖）
    python train/report_round.py --server-dir <path> --hours 12
    python train/report_round.py --no-arena           # 跳过 RCON 现场查询（服务器未开时）

输出分 8 段：服务与守护 / 启动自检 / 决策质量 / 轨迹质量曲线 / 战果 / 训练轮 / 异常 / 断电记录。
所有读取失败都降级为 "n/a"，脚本本身不会抛异常。
"""
from __future__ import annotations

import argparse
import glob
import gzip
import json
import os
import re
import sys
import time
from collections import Counter

try:
    import numpy as np
except ImportError:  # pragma: no cover
    np = None

for _s in (sys.stdout, sys.stderr):
    try:
        _s.reconfigure(encoding="utf-8", errors="replace")
    except (AttributeError, ValueError):
        pass

DEFAULT_SERVER = os.environ.get(
    "EFTLM_SERVER_DIR", r"E:\program\JAVA\touhou little maid - unknow sky area\prod_server")
DEFAULT_REPO = os.environ.get("EFTLM_REPO_DIR", r"E:\program\JAVA\EFTLM-example")


def section(title):
    print(f"\n=== {title} ===")


def tail_lines(path, n, encoding="utf-8"):
    """读文件尾部 n 行；.gz 自动解压；失败返回 []。"""
    try:
        if path.endswith(".gz"):
            with gzip.open(path, "rt", encoding=encoding, errors="replace") as f:
                return f.readlines()[-n:]
        with open(path, encoding=encoding, errors="replace") as f:
            return f.readlines()[-n:]
    except (OSError, EOFError):
        return []


def read_text(path, encoding="utf-8"):
    try:
        with open(path, encoding=encoding, errors="replace") as f:
            return f.read()
    except OSError:
        return ""


def newest_logs(log_dir, limit=6):
    """按时间倒序返回最近的日志文件（含轮转后的 .gz）。"""
    files = []
    for pat in ("*.log", "*.log.gz"):
        files += glob.glob(os.path.join(log_dir, pat))
    files.sort(key=lambda p: os.path.getmtime(p), reverse=True)
    return files[:limit]


def grep_lines(paths, pattern, max_hits=5):
    rx = re.compile(pattern)
    hits = []
    for p in paths:
        for line in tail_lines(p, 20000):
            if rx.search(line):
                hits.append((os.path.getmtime(p), line.rstrip()))
    hits.sort(key=lambda kv: kv[0])
    return [h[1] for h in hits[-max_hits:]]


def parse_ts(line):
    m = re.match(r"\[(\d{2})(\d{1,2})月(\d{4}) (\d{2}):(\d{2}):(\d{2})", line)
    if m:
        mo, d, y, hh, mm, ss = m.groups()
        try:
            return time.mktime((int(y), int(mo), int(d), int(hh), int(mm), int(ss), 0, 0, -1))
        except (ValueError, OverflowError):
            return None
    m = re.match(r"\[(\d{4})-(\d{2})-(\d{2}) (\d{2}):(\d{2}):(\d{2})", line)
    if m:
        y, mo, d, hh, mm, ss = m.groups()
        try:
            return time.mktime((int(y), int(mo), int(d), int(hh), int(mm), int(ss), 0, 0, -1))
        except (ValueError, OverflowError):
            return None
    return None


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--server-dir", default=DEFAULT_SERVER)
    ap.add_argument("--project-dir", default=DEFAULT_REPO)
    ap.add_argument("--hours", type=float, default=12.0, help="统计窗口（小时）")
    ap.add_argument("--no-arena", action="store_true", help="跳过 RCON 现场查询")
    args = ap.parse_args()

    server = args.server_dir
    repo = args.project_dir
    cfg = os.path.join(server, "config", "eftlm_stylish")
    logs_dir = os.path.join(server, "logs")
    traj_dir = os.path.join(cfg, "trajectories")
    models_dir = os.path.join(repo, "train", "models")
    now = time.time()
    print(f"[report] server={server}")
    print(f"[report] 时间={time.strftime('%Y-%m-%d %H:%M:%S')}  窗口={args.hours}h")

    # ---------- 1. 服务与守护 ----------
    section("1. 服务与守护")
    latest = os.path.join(logs_dir, "latest.log")
    mtime = os.path.getmtime(latest) if os.path.exists(latest) else 0
    print(f"  latest.log 最后写入: {time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(mtime)) if mtime else 'n/a'}"
          f"  ({'%.0f 分钟前' % ((now - mtime) / 60) if mtime else 'n/a'})")
    state = read_text(os.path.join(server, "guardian", "guardian.state")).strip()
    print(f"  guardian.state: {state if state else 'n/a'}")
    power = tail_lines(os.path.join(server, "guardian", "power.log"), 6)
    for line in power[-4:]:
        print(f"  power.log | {line.rstrip()}")

    # ---------- 2. 启动自检 ----------
    section("2. 启动自检（[SELFCHECK]）")
    logs = newest_logs(logs_dir)
    sc = grep_lines(logs, r"\[SELFCHECK\]", max_hits=6)
    print("\n".join("  " + l.split("]: ")[-1] for l in sc) if sc else "  n/a（本次启动未打印）")

    # ---------- 3. 决策质量（reject_rate） ----------
    section("3. 决策质量（心跳里的 reject_rate，目标 <5%）")
    hb = grep_lines(logs, r"\[RL\] heartbeat", max_hits=12)
    if hb:
        rates = [int(m.group(1)) for l in hb if (m := re.search(r"reject_rate=(\d+)%", l))]
        for l in hb[-4:]:
            print("  " + l.split("heartbeat: ")[-1])
        if rates:
            print(f"  → 近 {len(rates)} 次采样 reject_rate: 均值 {sum(rates)/len(rates):.1f}%  最大 {max(rates)}%")
    else:
        print("  n/a")

    # ---------- 4. 轨迹质量曲线（按小时分桶） ----------
    section("4. 轨迹质量曲线（每小时：步数 / 奖励均值 / 正奖励% / 命中%≥30）")
    if np is None:
        print("  n/a（缺 numpy）")
    elif not os.path.isdir(traj_dir):
        print("  n/a（无轨迹目录）")
    else:
        sys.path.insert(0, os.path.join(repo, "train"))
        try:
            from traj_io import load as load_traj
        except ImportError:
            load_traj = None
        buckets = {}
        files = [f for f in glob.glob(os.path.join(traj_dir, "*.bin"))
                 if os.path.getmtime(f) > now - args.hours * 3600]
        for f in files:
            b = time.strftime("%m-%d %H:00", time.localtime(os.path.getmtime(f)))
            buckets.setdefault(b, []).append(f)
        if not buckets and load_traj:
            print("  n/a（窗口内无轨迹）")
        for b in sorted(buckets):
            steps = pos = hits = 0
            total = 0.0
            for f in buckets[b]:
                t = load_traj(f) if load_traj else None
                if t is None:
                    continue
                steps += len(t.rewards)
                total += float(t.rewards.sum())
                pos += int((t.rewards > 0).sum())
                hits += int((t.rewards >= 30).sum())
            if steps:
                print(f"  {b}  文件 {len(buckets[b]):>4}  步数 {steps:>7}  均值 {total/steps:>6.1f}  "
                      f"正 {100.0*pos/steps:>5.1f}%  命中 {100.0*hits/steps:>4.1f}%")

    # ---------- 5. 战果 ----------
    section("5. 战果（本日志周期）")
    kills = len(grep_lines(logs, r"\[SKILL\] KILL detected", max_hits=100000)) if logs else 0
    revives = len(grep_lines(logs, r"maid REVIVED by elixir", max_hits=100000)) if logs else 0
    print(f"  击杀 {kills}  丢命(药水复活) {revives}")
    if not args.no_arena:
        try:
            sys.path.insert(0, os.path.join(repo, "tools"))
            import socket
            import struct
            from rcon_password import resolve
            pw = resolve(None, os.path.join(repo, "tools"))
            if not pw:
                print("  RCON: 未配置密码（跳过）")
            else:
                def _pkt(t, p):
                    b = p.encode()
                    return struct.pack("<ii", len(b) + 10, 0) + struct.pack("<i", t) + b + b"\x00\x00"

                def _read(s):
                    hdr = b""
                    while len(hdr) < 12:
                        c = s.recv(12 - len(hdr))
                        if not c:
                            return None
                        hdr += c
                    ln, _i, ty = struct.unpack("<iii", hdr)
                    body = b""
                    while len(body) < max(0, ln - 8):
                        c = s.recv(max(0, ln - 8) - len(body))
                        if not c:
                            break
                        body += c
                    return ty, body.decode("utf-8", "replace").strip("\x00")

                s = socket.create_connection(("127.0.0.1", 25575), timeout=6)
                s.sendall(_pkt(3, pw))
                _read(s)
                for cmd in ("/arena stats", "/rl autotick status"):
                    s.sendall(_pkt(2, cmd))
                    _t, body = _read(s)
                    print(f"  {cmd}: {body}")
                s.close()
        except (OSError, ImportError) as e:
            print(f"  RCON: 不可用（{e}）")

    # ---------- 6. 训练轮 ----------
    section("6. 训练轮与部署门禁")
    ver = read_text(os.path.join(models_dir, "rl_model_version.txt")).strip().replace("\n", " | ")
    print(f"  当前版本: {ver if ver else 'n/a'}")
    metas = sorted(glob.glob(os.path.join(models_dir, "metrics_v*.json")),
                   key=os.path.getmtime, reverse=True)[:2]
    for m in metas:
        try:
            with open(m, encoding="utf-8") as f:
                d = json.load(f)
            keys = ("samples", "eval_acc", "eval_nll", "label_top1_ratio", "action_entropy",
                    "pretrain", "relabel", "bc_extra_samples")
            print(f"  {os.path.basename(m)} ({time.strftime('%m-%d %H:%M', time.localtime(os.path.getmtime(m)))}): "
                  + "  ".join(f"{k}={d.get(k)}" for k in keys if k in d))
        except (OSError, ValueError):
            print(f"  {os.path.basename(m)}: 解析失败")
    it = tail_lines(os.path.join(models_dir, "iterate_auto.log"), 8)
    for line in it[-5:]:
        print(f"  iterate | {line.rstrip()}")

    # ---------- 7. 异常 ----------
    section("7. 异常扫描")
    patterns = {
        "WATCHDOG 停滞": r"WATCHDOG",
        "反应层异常": r"reactive layer threw",
        "执行器异常": r"executor .* threw",
        "模型维度不符": r"model dim mismatch",
        "course.json 解析失败": r"failed to load course\.json",
        "id 重复告警": r"duplicate skill id",
        "技能槽截断": r"skill slots truncated",
        "采样不可用": r"sampling unavailable",
        "服务端落后告警": r"Can't keep up",
    }
    for name, pat in patterns.items():
        n = len(grep_lines(logs, pat, max_hits=100000))
        flag = "" if n == 0 else "  ← 需关注" if name not in ("id 重复告警",) else "  (预期 36 条)"
        print(f"  {name:<18} {n}{flag}")
    alarm = os.path.join(cfg, "watchdog_alarm.txt")
    if os.path.exists(alarm):
        print(f"  watchdog_alarm.txt: {read_text(alarm).strip()[:200]}")

    # ---------- 8. 断电/停服记录 ----------
    section("8. 停服与断电记录")
    stops = grep_lines(logs, r"Stopping server|Thread RCON Listener stopped|Saving worlds", max_hits=4)
    print("\n".join("  " + l for l in stops) if stops else "  n/a")
    print("\n[report] 完成。提示：门禁拒绝了模型时训练数据仍会累积，下一轮会重新尝试部署。")
    return 0


if __name__ == "__main__":
    sys.exit(main())
