#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""质量曲线细分工具（2026-09-10）：按时间桶给出"训练数据质量 + 场上表现"的对照曲线。

比 report_round.py 的小时级更细（默认 30 分钟），并补充三个代理指标：

  * 平均轨迹步数   —— 生存代理（死亡会立即落盘，越短说明死得越频繁；上限 600）
  * 动作 top1 占比 —— 多样性/退化代理（门禁阈值 0.55）
  * 击杀 / 丢命    —— 用日志时间戳归桶（击杀 = [SKILL] KILL detected，丢命 = 药水复活）

用法：
    python train/quality_curve.py --hours 5 --bucket 30
"""
from __future__ import annotations

import argparse
import glob
import os
import re
import sys
import time

for _s in (sys.stdout, sys.stderr):
    try:
        _s.reconfigure(encoding="utf-8", errors="replace")
    except (AttributeError, ValueError):
        pass

DEFAULT_SERVER = os.environ.get(
    "EFTLM_SERVER_DIR", r"E:\program\JAVA\touhou little maid - unknow sky area\prod_server")
DEFAULT_REPO = os.environ.get("EFTLM_REPO_DIR", r"E:\program\JAVA\EFTLM-example")


def _decode_lines(raw: bytes):
    """按候选编码解码日志：Minecraft 服务器日志在中文 Windows 上是 GBK（日期里的"月/日"），
    直接用 UTF-8 读会把这些字节替换成 U+FFFD，导致时间戳解析失败（2026-09-10 实测）。"""
    for enc in ("utf-8", "gbk", "cp936", "latin-1"):
        try:
            text = raw.decode(enc)
        except (UnicodeDecodeError, LookupError):
            continue
        lines = text.splitlines()
        # 用"含时间戳的行能否解析出年份"来判定编码是否正确
        for line in lines[:2000]:
            if "INFO" in line or "WARN" in line or "ERROR" in line:
                ts = line_ts(line + "\n")
                if ts is not None and 2020 <= time.localtime(ts).tm_year <= 2100:
                    return lines
    return raw.decode("utf-8", errors="replace").splitlines()


def log_lines(log_dir):
    """按时间顺序拼接可用日志（含 .gz 轮转）。"""
    import gzip
    files = []
    for pat in ("*.log.gz", "*.log"):
        files += glob.glob(os.path.join(log_dir, pat))
    files.sort(key=os.path.getmtime)
    out = []
    for p in files:
        try:
            if p.endswith(".gz"):
                with gzip.open(p, "rb") as f:
                    raw = f.read()
            else:
                with open(p, "rb") as f:
                    raw = f.read()
        except (OSError, EOFError):
            continue
        out += [l + "\n" for l in _decode_lines(raw)]
    return out


TS_PATTERNS = (
    # MC 服务器日志格式：[109月2026 17:46:39.749] = 日(10) + 月(9) + 年 + 时间（注意是"日+月"倒序）
    re.compile(r"\[(\d{1,2})(\d{1,2})月(\d{4}) (\d{2}):(\d{2}):(\d{2})"),
    re.compile(r"\[(\d{4})-(\d{2})-(\d{2}) (\d{2}):(\d{2}):(\d{2})"),
)


def line_ts(line):
    for i, rx in enumerate(TS_PATTERNS):
        m = rx.match(line)
        if not m:
            continue
        g = m.groups()
        try:
            if i == 0:
                d, mo, y, hh, mm, ss = g          # [DDM月YYYY ...]
            else:
                y, mo, d, hh, mm, ss = g          # [YYYY-MM-DD ...]
            return time.mktime((int(y), int(mo), int(d), int(hh), int(mm), int(ss), 0, 0, -1))
        except (ValueError, OverflowError):
            return None
    return None


def bucket_key(ts, bucket_sec):
    return int(ts // bucket_sec) * bucket_sec


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--server-dir", default=DEFAULT_SERVER)
    ap.add_argument("--project-dir", default=DEFAULT_REPO)
    ap.add_argument("--hours", type=float, default=5.0)
    ap.add_argument("--bucket", type=int, default=30, help="桶宽（分钟）")
    args = ap.parse_args()

    now = time.time()
    since = now - args.hours * 3600
    bsec = args.bucket * 60

    sys.path.insert(0, os.path.join(args.project_dir, "train"))
    from traj_io import load as load_traj

    traj_dir = os.path.join(args.server_dir, "config", "eftlm_stylish", "trajectories")
    buckets = {}
    files = [f for f in glob.glob(os.path.join(traj_dir, "*.bin"))
             if os.path.getmtime(f) > since]
    for f in files:
        k = bucket_key(os.path.getmtime(f), bsec)
        b = buckets.setdefault(k, {"files": 0, "steps": 0, "sum": 0.0, "pos": 0, "hits": 0,
                                   "acts": {}, "trajs": 0})
        t = load_traj(f)
        if t is None:
            continue
        b["files"] += 1
        b["trajs"] += 1
        b["steps"] += len(t.rewards)
        b["sum"] += float(t.rewards.sum())
        b["pos"] += int((t.rewards > 0).sum())
        b["hits"] += int((t.rewards >= 30).sum())
        for a in t.actions:
            ai = int(a)
            b["acts"][ai] = b["acts"].get(ai, 0) + 1

    # 日志侧：击杀 / 丢命 归桶
    kills, deaths = {}, {}
    for line in log_lines(os.path.join(args.server_dir, "logs")):
        if "[SKILL] KILL detected" in line or "maid REVIVED by elixir" in line:
            ts = line_ts(line)
            if ts is None or ts < since:
                continue
            k = bucket_key(ts, bsec)
            if "[SKILL] KILL detected" in line:
                kills[k] = kills.get(k, 0) + 1
            else:
                deaths[k] = deaths.get(k, 0) + 1

    keys = sorted(set(buckets) | set(kills) | set(deaths))
    if not keys:
        print("窗口内无数据")
        return 0
    print(f"[curve] 每桶 {args.bucket} 分钟，窗口 {args.hours}h；命中=奖励≥+30 的近战命中步")
    print(f"{'时间桶':<14}{'轨迹':>6}{'步数':>9}{'均值':>7}{'正%':>7}{'命中%':>7}"
          f"{'均步/轨迹':>10}{'top1%':>7}{'杀':>5}{'丢命':>6}")
    for k in keys:
        b = buckets.get(k)
        label = time.strftime("%m-%d %H:%M", time.localtime(k))
        if b and b["steps"]:
            st = b["steps"]
            top1 = max(b["acts"].values()) / st * 100.0
            print(f"{label:<14}{b['files']:>6}{st:>9}{b['sum']/st:>7.1f}"
                  f"{100.0*b['pos']/st:>7.1f}{100.0*b['hits']/st:>7.1f}"
                  f"{st/max(1,b['trajs']):>10.0f}{top1:>7.1f}"
                  f"{kills.get(k,0):>5}{deaths.get(k,0):>6}")
        else:
            print(f"{label:<14}{'-':>6}{'-':>9}{'-':>7}{'-':>7}{'-':>7}{'-':>10}{'-':>7}"
                  f"{kills.get(k,0):>5}{deaths.get(k,0):>6}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
