#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""训练产物保留策略与轨迹归档（P2-5，2026-09-11）。

背景（2026-09-11 实测，E: 盘仅剩 38 GB）——数据被存了两份且只进不出：

    <prod>/config/eftlm_stylish/trajectories      102,591 文件 / 4.07 GB   ← 活跃，仍在增长
    train/backup/data/session_*                   92 个会话 / 479k 文件 / 4.9 GB
    train/models/*.npz                            217 个 / 1.8 GB
    train/backup/pause_*|shutdown_*|world_*       575 MB + ...

两个具体危害：

  1. 每轮 `iterate.snapshot()` 都要 `glob` 整个活跃目录（10 万条目）才能挑出新文件；
  2. 479k 文件的 `train/backup/data` 让 git / 资源管理器 / 备份全部变慢（审查报告 P3 仓库治理同源）。

策略（每类独立 keep 数；**默认干跑**）：

    live       已确认存在于某个 session 快照中的活跃轨迹 → 删除（数据在快照里有备份，不丢）
    sessions   保留最新 N 个；更旧的用 ZIP_STORED 归档（浮点数据压缩无收益）后删目录
    npz        按前缀分组各保留最新 N 个
    models     rl_model_v*.bin（含 .critic.npz 边车）保留最新 N 个 + 受保护清单
    trash      trajectories_trash 中超过 1 小时的内容

安全规则（硬约束，不可绕过）：

  * 默认干跑，必须显式 `--apply`；
  * `--grace-hours`（默认 2h）内修改过的任何文件/目录一律跳过（可能正在写）；
  * 只允许操作白名单根目录下的路径；
  * 跳过 reparse point（符号链接 / junction）；
  * live 轨迹逐条核验"同名文件存在于某个 session 快照"才删，未命中记为孤儿并保留；
  * session 归档先写 `.tmp` → 校验 zip 条目数 → 再删源目录；
  * 受保护清单：`rl_model.bin`、`model_pool/` 内的版本、最新 2 个 `rl_model_prev_deployed_*`、
    任何被 `train/models/*.json` 引用的 `.npz`/`.bin`；
  * 所有动作追加审计日志 `train/models/retention_audit.log`。

用法：

    python train/retention.py                          # 干跑，只打印计划
    python train/retention.py --apply                   # 执行（sessions 仍只报告）
    python train/retention.py --apply --archive-sessions # 连 session 也归档
    python train/retention.py --only live --apply        # 只清理活跃轨迹
    python train/retention.py --json report.json
"""
from __future__ import annotations

import argparse
import json
import os
import re
import shutil
import stat
import sys
import time
import zipfile
from datetime import datetime

for _s in (sys.stdout, sys.stderr):
    try:
        _s.reconfigure(encoding="utf-8", errors="replace")
    except (AttributeError, ValueError):
        pass

REPO = os.environ.get("EFTLM_REPO_DIR", r"E:\program\JAVA\EFTLM-example")
SERVER = os.environ.get("EFTLM_SERVER_DIR", r"E:\program\JAVA\touhou little maid - unknow sky area\prod_server")

MODELS_DIR = os.path.join(REPO, "train", "models")
BACKUP_DATA = os.path.join(REPO, "train", "backup", "data")
ARCHIVE_DIR = os.path.join(REPO, "train", "backup", "data_archive")
SNAPSHOT_STATE = os.path.join(MODELS_DIR, "snapshot_state.txt")
AUDIT_LOG = os.path.join(MODELS_DIR, "retention_audit.log")

CONFIG_DIR = os.path.join(SERVER, "config", "eftlm_stylish")
LIVE_TRAJ = os.path.join(CONFIG_DIR, "trajectories")
TRASH_TRAJ = os.path.join(CONFIG_DIR, "trajectories_trash")

ALLOWED_ROOTS = [MODELS_DIR, BACKUP_DATA, ARCHIVE_DIR, LIVE_TRAJ, TRASH_TRAJ]

NPZ_GROUP_RE = re.compile(r"^([a-z_]+?)_(\d{8})_\d{6}\.npz$", re.I)
REF_RE = re.compile(r"[\w./\\-]+\.(?:npz|bin)")
AUDIT_STAMP = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

# 逐类统计：name -> {candidates, kept, count, bytes, notes[]}
STATS: dict[str, dict] = {}
PLAN: list[tuple[str, str, str]] = []   # (cls, action, path)  action: delete|zip|skip|orphan


def cls_stat(name: str) -> dict:
    return STATS.setdefault(name, {"candidates": 0, "count": 0, "bytes": 0, "notes": []})


def human(n: int) -> str:
    for unit in ("B", "KB", "MB", "GB"):
        if n < 1024 or unit == "GB":
            return f"{n:.1f} {unit}" if unit != "B" else f"{n} B"
        n /= 1024.0
    return f"{n:.1f} GB"


def is_reparse(path: str) -> bool:
    try:
        return bool(os.lstat(path).st_file_attributes & stat.FILE_ATTRIBUTE_REPARSE_POINT)
    except (OSError, AttributeError):
        return False


def path_allowed(path: str) -> bool:
    """硬约束：只允许白名单根目录下的路径，且必须比根更深一层。"""
    ap = os.path.abspath(path)
    for root in ALLOWED_ROOTS:
        ar = os.path.abspath(root)
        if ap == ar:
            return False
        if ap.startswith(ar + os.sep):
            return True
    return False


def dir_size(path: str) -> tuple[int, int]:
    """返回 (文件数, 字节数)；用 scandir 逐层走，避开 pathlib 的开销。"""
    n = 0
    total = 0
    stack = [path]
    while stack:
        cur = stack.pop()
        try:
            with os.scandir(cur) as it:
                for e in it:
                    try:
                        if e.is_dir(follow_symlinks=False):
                            stack.append(e.path)
                        elif e.is_file(follow_symlinks=False):
                            n += 1
                            total += e.stat(follow_symlinks=False).st_size
                    except OSError:
                        continue
        except OSError:
            continue
    return n, total


def record(cls: str, action: str, path: str, size: int = 0, note: str = "") -> None:
    st = cls_stat(cls)
    st["candidates"] += 1
    if action in ("delete", "zip"):
        st["count"] += 1
        st["bytes"] += size
    if note and note not in st["notes"] and len(st["notes"]) < 8:
        st["notes"].append(note)
    PLAN.append((cls, action, path))


def protected_names() -> set[str]:
    """受保护文件名集合：模型池引用、最新 2 个 prev_deployed、以及被任何 models/*.json 引用的产物。"""
    keep: set[str] = {"rl_model.bin", "snapshot_state.txt", "prev_metrics.json", "metrics.json"}
    # 任何 JSON 里出现的 npz/bin 引用都保护（含当前轮 train_args/data 路径）
    try:
        for f in os.listdir(MODELS_DIR):
            if not f.endswith(".json"):
                continue
            try:
                raw = open(os.path.join(MODELS_DIR, f), encoding="utf-8", errors="replace").read()
            except OSError:
                continue
            for m in REF_RE.findall(raw):
                keep.add(os.path.basename(m.replace("\\", "/")))
    except OSError:
        pass
    pool = os.path.join(CONFIG_DIR, "model_pool")
    if os.path.isdir(pool):
        try:
            keep.update(os.listdir(pool))
        except OSError:
            pass
    prev = sorted([f for f in os.listdir(MODELS_DIR)
                   if f.startswith("rl_model_prev_deployed_")], reverse=True)[:2]
    keep.update(prev)
    return keep


def action_delete(path: str, size: int, apply: bool, cls: str, note: str = "") -> None:
    if not path_allowed(path):
        record(cls, "skip", path, note="NOT-ALLOWED")
        return
    if not apply:
        record(cls, "delete", path, size, note)
        return
    try:
        if os.path.isdir(path):
            shutil.rmtree(path)
        else:
            os.remove(path)
        record(cls, "delete", path, size, note)
        audit("DELETE", path, size)
    except OSError as e:
        record(cls, "skip", path, note=f"rmtree failed: {e}")


def audit(action: str, path: str, size: int = 0) -> None:
    try:
        with open(AUDIT_LOG, "a", encoding="utf-8") as fh:
            fh.write(f"[{AUDIT_STAMP}] {action} {human(size):>9} {path}\n")
    except OSError:
        pass


# ----------------------------------------------------------------------
# 各类策略
# ----------------------------------------------------------------------

def policy_live(args, apply: bool) -> None:
    """活跃轨迹：已在 session 快照里有同名备份的，删除。"""
    cls = "live"
    if not os.path.isdir(LIVE_TRAJ):
        cls_stat(cls)["notes"].append("live 目录不存在")
        return
    # 1) 建立 session 快照的文件名索引（只做一次；479k 名字）
    index: set[str] = set()
    if os.path.isdir(BACKUP_DATA):
        for d in os.scandir(BACKUP_DATA):
            if not d.is_dir(follow_symlinks=False):
                continue
            try:
                with os.scandir(d.path) as it:
                    for e in it:
                        if e.is_file(follow_symlinks=False):
                            index.add(e.name)
            except OSError:
                continue
    st = cls_stat(cls)
    st["notes"].append(f"session 快照索引 {len(index)} 个文件名")
    now = time.time()
    grace = args.grace_hours * 3600.0
    orphans = 0
    for e in os.scandir(LIVE_TRAJ):
        try:
            if not e.is_file(follow_symlinks=False) or not e.name.endswith(".bin"):
                continue
            fi = e.stat(follow_symlinks=False)
        except OSError:
            continue
        if now - fi.st_mtime < grace:
            continue                      # 可能正在写
        if e.name in index:
            action_delete(e.path, fi.st_size, apply, cls, note="已存在于 session 快照")
        else:
            orphans += 1
            record(cls, "orphan", e.path)
    if orphans:
        cls_stat(cls)["notes"].append(f"孤儿（快照中无副本，已保留）: {orphans}")


def policy_sessions(args, apply: bool) -> None:
    """session 快照：保留最新 N 个；更旧的 zip 归档后删除。"""
    cls = "sessions"
    if not os.path.isdir(BACKUP_DATA):
        return
    sessions = []
    for d in os.scandir(BACKUP_DATA):
        if d.is_dir(follow_symlinks=False) and d.name.startswith("session_"):
            try:
                sessions.append((d.stat(follow_symlinks=False).st_mtime, d.path, d.name))
            except OSError:
                continue
    sessions.sort(reverse=True)
    now = time.time()
    grace = args.grace_hours * 3600.0
    st = cls_stat(cls)
    st["notes"].append(f"共 {len(sessions)} 个会话，保留最新 {args.keep_sessions}")
    for mtime, path, name in sessions[args.keep_sessions:]:
        if now - mtime < grace or is_reparse(path) or not path_allowed(path):
            continue
        n, size = dir_size(path)
        if not args.archive_sessions:
            record(cls, "report", path, note=f"{name}: {n} 文件 / {human(size)}（--archive-sessions 才归档）")
            st["count"] += 1
            st["bytes"] += size
            continue
        if not apply:
            record(cls, "zip", path, size, note=f"{name}: {n} 文件")
            continue
        os.makedirs(ARCHIVE_DIR, exist_ok=True)
        dst = os.path.join(ARCHIVE_DIR, f"{name}.zip")
        tmp = dst + ".tmp"
        try:
            with zipfile.ZipFile(tmp, "w", zipfile.ZIP_STORED, allowZip64=True) as z:
                for e in os.scandir(path):
                    if e.is_file(follow_symlinks=False):
                        z.write(e.path, e.name)
            with zipfile.ZipFile(tmp) as z:
                got = len(z.namelist())
            if got != n:
                raise RuntimeError(f"zip 条目数不符: {got} != {n}")
            os.replace(tmp, dst)
            shutil.rmtree(path)
            record(cls, "zip", path, size, note=f"{name}: {n} 文件 → {os.path.basename(dst)}")
            audit("ARCHIVE", dst, size)
        except Exception as ex:                      # noqa: BLE001 — 归档失败必须保留源目录
            if os.path.exists(tmp):
                try:
                    os.remove(tmp)
                except OSError:
                    pass
            record(cls, "skip", path, note=f"{name}: 归档失败 {ex}")


def policy_npz(args, apply: bool) -> None:
    """npz 训练样本：按前缀分组各保留最新 N 个。"""
    cls = "npz"
    keep_prot = protected_names()
    groups: dict[str, list[tuple[float, str, int]]] = {}
    for e in os.scandir(MODELS_DIR):
        try:
            if not e.is_file(follow_symlinks=False) or not e.name.endswith(".npz"):
                continue
            fi = e.stat(follow_symlinks=False)
        except OSError:
            continue
        m = NPZ_GROUP_RE.match(e.name)
        group = m.group(1) if m else "other"
        groups.setdefault(group, []).append((fi.st_mtime, e.path, fi.st_size))
    now = time.time()
    grace = args.grace_hours * 3600.0
    st = cls_stat(cls)
    for group, items in sorted(groups.items()):
        items.sort(reverse=True)
        st["notes"].append(f"{group}: {len(items)} 个，保留最新 {args.keep_npz}")
        for mtime, path, size in items[args.keep_npz:]:
            if os.path.basename(path) in keep_prot:
                record(cls, "skip", path, note="受 JSON 引用保护")
                continue
            if now - mtime < grace:
                continue
            action_delete(path, size, apply, cls, note=f"{group} 旧样本")


def policy_models(args, apply: bool) -> None:
    """模型：rl_model_v*.bin + .critic.npz 边车保留最新 N 个。"""
    cls = "models"
    keep_prot = protected_names()
    items = []
    for e in os.scandir(MODELS_DIR):
        try:
            if not e.is_file(follow_symlinks=False):
                continue
            if not (e.name.endswith(".bin") or e.name.endswith(".critic.npz")):
                continue
            fi = e.stat(follow_symlinks=False)
        except OSError:
            continue
        items.append((fi.st_mtime, e.path, fi.st_size, e.name))
    items.sort(reverse=True)
    now = time.time()
    grace = args.grace_hours * 3600.0
    st = cls_stat(cls)
    st["notes"].append(f"共 {len(items)} 个模型产物，保留最新 {args.keep_models}")
    for mtime, path, size, name in items[args.keep_models:]:
        if name in keep_prot or "prev_deployed" in name or is_reparse(path):
            record(cls, "skip", path, note="受保护（部署/池/JSON 引用）")
            continue
        if now - mtime < grace:
            continue
        action_delete(path, size, apply, cls)


def policy_trash(args, apply: bool) -> None:
    """trajectories_trash：超过 1 小时的内容删除。"""
    cls = "trash"
    if not os.path.isdir(TRASH_TRAJ):
        return
    now = time.time()
    for e in os.scandir(TRASH_TRAJ):
        try:
            if not e.is_file(follow_symlinks=False):
                continue
            fi = e.stat(follow_symlinks=False)
        except OSError:
            continue
        if now - fi.st_mtime < 3600:
            continue
        action_delete(e.path, fi.st_size, apply, cls)


POLICIES = {
    "live": policy_live,
    "sessions": policy_sessions,
    "npz": policy_npz,
    "models": policy_models,
    "trash": policy_trash,
}


def main() -> int:
    ap = argparse.ArgumentParser(description="训练产物保留策略（默认干跑）")
    ap.add_argument("--apply", action="store_true", help="真正执行删除/归档（默认只打印计划）")
    ap.add_argument("--only", action="append", choices=sorted(POLICIES),
                    help="只处理指定类，可重复；默认全部")
    ap.add_argument("--keep-sessions", type=int, default=3, help="保留最新 N 个 session 快照（默认 3）")
    ap.add_argument("--keep-npz", type=int, default=4, help="每类 npz 保留最新 N 个（默认 4）")
    ap.add_argument("--keep-models", type=int, default=20, help="保留最新 N 个模型产物（默认 20）")
    ap.add_argument("--archive-sessions", action="store_true",
                    help="把超出 keep 的 session 归档为 zip（默认只报告，不归档）")
    ap.add_argument("--grace-hours", type=float, default=2.0, help="多少小时内修改过的产物跳过（默认 2）")
    ap.add_argument("--json", metavar="PATH", help="把报告写成 JSON")
    ap.add_argument("--show", type=int, default=5, help="每类打印前 N 条明细（默认 5）")
    args = ap.parse_args()

    classes = args.only or sorted(POLICIES)
    print(f"=== 训练产物保留策略 | {'APPLY（会真删）' if args.apply else 'DRY-RUN（只报告）'} ===")
    print(f"仓库 {REPO}\n服务端 {SERVER}")
    print(f"保留: sessions={args.keep_sessions} npz/组={args.keep_npz} models={args.keep_models} "
          f"宽限={args.grace_hours}h 归档session={'开' if args.archive_sessions else '关'}")
    print()

    for name in classes:
        before = len(PLAN)
        try:
            POLICIES[name](args, args.apply)
        except Exception as ex:                                  # noqa: BLE001
            cls_stat(name)["notes"].append(f"策略异常: {ex!r}")
        st = STATS.get(name, {})
        print(f"[{name}] 候选 {st.get('candidates', 0)} | 可回收 {st.get('count', 0)} 项 / "
              f"{human(int(st.get('bytes', 0)))}")
        for note in st.get("notes", []):
            print(f"    · {note}")
        detail = [p for p in PLAN[before:] if p[0] == name and p[1] in ("delete", "zip", "report")]
        for _, act, path in detail[:args.show]:
            print(f"    - {act:<7} {path}")
        if len(detail) > args.show:
            print(f"    … 其余 {len(detail) - args.show} 条明细见 --json / 审计日志")
        print()

    total_items = sum(s["count"] for s in STATS.values())
    total_bytes = sum(s["bytes"] for s in STATS.values())
    print(f"=== 合计：{total_items} 项 / {human(int(total_bytes))} "
          f"{'（已执行）' if args.apply else '（干跑，未改动任何文件）'} ===")
    if not args.apply:
        print("执行：python train/retention.py --apply"
              + ("  --archive-sessions" if not args.archive_sessions else ""))
    elif args.apply:
        audit("SUMMARY", f"{total_items} items / {human(int(total_bytes))}")

    if args.json:
        report = {
            "time": AUDIT_STAMP,
            "applied": bool(args.apply),
            "keep": {"sessions": args.keep_sessions, "npz": args.keep_npz, "models": args.keep_models},
            "classes": {k: {**v} for k, v in STATS.items()},
            "total": {"items": total_items, "bytes": total_bytes},
            "plan": [{"cls": c, "action": a, "path": p} for c, a, p in PLAN],
        }
        try:
            with open(args.json, "w", encoding="utf-8") as fh:
                json.dump(report, fh, ensure_ascii=False, indent=2)
            print(f"报告已写出: {args.json}")
        except OSError as e:
            print(f"报告写入失败: {e}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
