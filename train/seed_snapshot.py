#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""可验证的快照播种 / 水位线自检（P2-5 配套，2026-09-11）。

## 为什么需要它

`iterate.snapshot()` 用 `train/models/snapshot_state.txt` 记录"已快照到的最大 mtime"
水位线，每轮只拷贝 `mtime > 水位线` 的活跃轨迹。这个设计的**隐含前提**是：
水位线之前的所有文件都已经在某个 session 快照里了。

而"首轮部署前的初始快照由部署流程写入同一状态文件"（iterate.py 模块 docstring）——
部署流程一旦**先把水位线写成 now、再/或不完整地拷贝**，两者之间就出现永久漏洞：
那些文件的 mtime ≤ 水位线，此后**任何一轮都不会再拷贝它们**，且没有任何报错。

实测（2026-09-11）：`config/eftlm_stylish/trajectories` 有 1,155 条
09-09/09-10 的轨迹不在任何 session 快照中 —— 静默漏采。

## 用法

    # 自检：水位线之前是否还有"没进过任何 session 快照"的文件（只读）
    python train/seed_snapshot.py --verify

    # 播种：把活跃目录里所有文件（除 grace 窗口内的新文件）拷入指定 session，
    #       逐条核验，然后才把水位线推进到最大 mtime（全有或全无）
    python train/seed_snapshot.py --session-dir train/backup/data/session_20260911_1620

退出码：0 = 一致 / 播种成功；1 = 发现漏采或核验失败（不推进水位线）。
"""
from __future__ import annotations

import argparse
import os
import shutil
import sys
import time

for _s in (sys.stdout, sys.stderr):
    try:
        _s.reconfigure(encoding="utf-8", errors="replace")
    except (AttributeError, ValueError):
        pass

REPO = os.environ.get("EFTLM_REPO_DIR", r"E:\program\JAVA\EFTLM-example")
SERVER = os.environ.get("EFTLM_SERVER_DIR", r"E:\program\JAVA\touhou little maid - unknow sky area\prod_server")
MODELS = os.path.join(REPO, "train", "models")
BACKUP_DATA = os.path.join(REPO, "train", "backup", "data")
LIVE = os.path.join(SERVER, "config", "eftlm_stylish", "trajectories")
STATE = os.path.join(MODELS, "snapshot_state.txt")


def read_watermark() -> float:
    try:
        return float(open(STATE, encoding="utf-8").read().strip())
    except (OSError, ValueError):
        return 0.0


def session_index() -> set[str]:
    """所有 session 快照里的文件名集合。"""
    names: set[str] = set()
    if not os.path.isdir(BACKUP_DATA):
        return names
    for d in os.scandir(BACKUP_DATA):
        if not d.is_dir(follow_symlinks=False):
            continue
        try:
            with os.scandir(d.path) as it:
                for e in it:
                    if e.is_file(follow_symlinks=False):
                        names.add(e.name)
        except OSError:
            continue
    return names


def live_files():
    for e in os.scandir(LIVE):
        try:
            if e.is_file(follow_symlinks=False) and e.name.endswith(".bin"):
                yield e.path, e.name, e.stat(follow_symlinks=False).st_mtime
        except OSError:
            continue


def classify(grace_hours: float) -> dict:
    """把活跃目录里的文件分成三类（**判据是 2026-09-11 事故的教训**）：

      pending   mtime > 水位线 —— **待快照**，下一轮就会被正常拷贝。绝不是孤儿！
      orphan    mtime ≤ 水位线 且不在任何 session 快照里 —— 真漏采（永久不会被拷贝）。
      copied    mtime ≤ 水位线 且已在某个 session 快照里 —— 已被消费，活跃目录里的冗余副本。

    原实现只按"不在任何 session 里 + 超过 grace"判孤儿，**漏掉了水位线这个判据** →
    把 16:33–18:05 写的 186 条"待快照"轨迹当成孤儿隔离掉了（2026-09-11 事故）：
    那批数据本该被 19:04 那轮收进训练集。判据错在"没进过快照"≠"永远不会进快照"。
    """
    wm = read_watermark()
    idx = session_index()
    now = time.time()
    out = {"pending": [], "orphan": [], "copied": [], "too_young": 0}
    for path, name, mtime in live_files():
        if now - mtime < grace_hours * 3600.0:
            out["too_young"] += 1
            continue
        if mtime > wm:
            out["pending"].append((path, mtime))
        elif name in idx:
            out["copied"].append((path, mtime))
        else:
            out["orphan"].append((path, mtime))
    return out


def _buckets(items) -> dict:
    b: dict[str, int] = {}
    for _p, m in items:
        day = time.strftime("%Y-%m-%d", time.localtime(m))
        b[day] = b.get(day, 0) + 1
    return b


def verify(grace_hours: float) -> int:
    wm = read_watermark()
    cls = classify(grace_hours)
    pending, orphans, copied = cls["pending"], cls["orphan"], cls["copied"]
    print(f"水位线: {wm:.0f} ({time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(wm)) if wm else 'n/a'})")
    print(f"session 快照索引: {len(session_index())} 个文件名")
    print(f"活跃目录: 待快照(pending)={len(pending)}  真漏采(orphan)={len(orphans)}  "
          f"已消费冗余(copied)={len(copied)}  太新跳过={cls['too_young']}")
    if copied:
        print("  提示：copied 是已进过快照的冗余副本，可用 train/retention.py --only live --apply 清理")
    if orphans:
        for day, k in sorted(_buckets(orphans).items()):
            print(f"  [漏采] {day}: {k} 条（mtime ≤ 水位线且不在任何 session 中 → 此后不会被任何一轮拷贝）")
        print("处理方式二选一：")
        print("  · 补进训练集：python train/seed_snapshot.py --session-dir <新 session 目录>")
        print("  · 移出活跃目录保留数据：python train/seed_snapshot.py --quarantine")
        return 1
    print("OK: 水位线之前没有漏采文件")
    return 0


def quarantine(grace_hours: float) -> int:
    """只隔离**真漏采**（mtime ≤ 水位线且不在任何 session 中）；pending 一律不动。

    2026-09-11 事故：原实现把 pending 也当孤儿移走，导致 186 条待训练轨迹被隔离。
    """
    cls = classify(grace_hours)
    orphans = cls["orphan"]
    dst = os.path.join(os.path.dirname(LIVE), "trajectories_quarantine")
    os.makedirs(dst, exist_ok=True)
    moved = 0
    for path, _mtime in orphans:
        name = os.path.basename(path)
        try:
            shutil.move(path, os.path.join(dst, name))
            moved += 1
        except OSError as e:
            print(f"  move failed: {name}: {e}")
    print(f"隔离 {moved} 条真漏采 → {dst}")
    print(f"（未触碰：待快照 {len(cls['pending'])} 条、已消费冗余 {len(cls['copied'])} 条、"
          f"太新 {cls['too_young']} 条）")
    print("（数据未删除；如需用于训练，先用 train/relabel.py 按当前语义重标注）")
    return 0


def restore(since: float, grace_hours: float) -> int:
    """把隔离区里 mtime > `since` 的文件搬回活跃目录（撤销误隔离）。

    `since` 应传**误隔离当时的快照水位线**（不是当前水位线——它早已被后续成功轮次推进，
    见 2026-09-11 事故）。用 `--since <epoch 秒>` 指定；`--since 0` 表示全部恢复。
    """
    src = os.path.join(os.path.dirname(LIVE), "trajectories_quarantine")
    if not os.path.isdir(src):
        print("隔离区不存在")
        return 0
    moved = 0
    for e in os.scandir(src):
        try:
            if not e.is_file(follow_symlinks=False) or not e.name.endswith(".bin"):
                continue
            m = e.stat(follow_symlinks=False).st_mtime
        except OSError:
            continue
        if m <= since:
            continue  # 真漏采（属于旧语义），不恢复
        target = os.path.join(LIVE, e.name)
        if os.path.exists(target):
            continue
        shutil.move(e.path, target)
        moved += 1
    print(f"从隔离区恢复 {moved} 条（mtime > {since:.0f}）→ {LIVE}")
    print("注意：若快照水位线已经推进到这些文件的 mtime 之上，它们仍不会被下一轮拷贝；")
    print("      要么把水位线回退到 since，要么用 --session-dir 直接补进某个 session。")
    return 0


def seed(session_dir: str, grace_hours: float) -> int:
    if not os.path.isabs(session_dir):
        session_dir = os.path.join(REPO, session_dir)
    os.makedirs(session_dir, exist_ok=True)
    wm = read_watermark()
    now = time.time()
    existing = set(os.listdir(session_dir))
    copied = 0
    skipped_young = 0
    newest = wm
    for path, name, mtime in live_files():
        if now - mtime < grace_hours * 3600.0:
            skipped_young += 1
            continue
        if name in existing:
            newest = max(newest, mtime)
            continue
        shutil.copy2(path, os.path.join(session_dir, name))
        copied += 1
        newest = max(newest, mtime)
    # 全有或全无：核验 session 目录里的文件数 == 活跃目录中 grace 之外的文件数
    got = len([n for n in os.listdir(session_dir) if n.endswith(".bin")])
    want = sum(1 for _p, _n, m in live_files() if now - m >= grace_hours * 3600.0)
    print(f"session: {session_dir}")
    print(f"新拷贝 {copied} 条（跳过 grace 内新文件 {skipped_young} 条）")
    print(f"核验: session 目录 {got} 条 / 活跃目录应有 {want} 条")
    if got < want:
        print("FAILED: 拷贝不完整，**不推进水位线**（避免制造新的永久漏采）")
        return 1
    with open(STATE, "w", encoding="utf-8") as fh:
        fh.write(str(newest))
    print(f"OK: 水位线推进到 {newest:.0f} "
          f"({time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(newest))})")
    return 0


def main() -> int:
    ap = argparse.ArgumentParser(description="快照播种 / 水位线漏采自检")
    ap.add_argument("--verify", action="store_true", help="只检查水位线之前是否还有漏采文件")
    ap.add_argument("--session-dir", help="把活跃目录全部文件拷入该 session 后再推进水位线")
    ap.add_argument("--quarantine", action="store_true",
                    help="把真漏采轨迹移入 trajectories_quarantine/（pending 不动、不推进水位线）")
    ap.add_argument("--restore", action="store_true",
                    help="把隔离区中 mtime > --since 的文件搬回活跃目录（撤销误隔离）")
    ap.add_argument("--since", type=float, default=None,
                    help="--restore 的时间下界（epoch 秒；默认取当前水位线）")
    ap.add_argument("--grace-hours", type=float, default=0.05,
                    help="多长时间内修改过的文件视为在写、跳过（默认 3 分钟）")
    args = ap.parse_args()
    if args.restore:
        return restore(read_watermark() if args.since is None else args.since, args.grace_hours)
    if args.quarantine:
        return quarantine(args.grace_hours)
    if args.session_dir:
        return seed(args.session_dir, args.grace_hours)
    return verify(args.grace_hours)


if __name__ == "__main__":
    sys.exit(main())
