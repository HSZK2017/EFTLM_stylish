#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
The Crucible —— 高华丽度回放清洗
==================================
遍历轨迹数据，挑出那些"无意中打出复杂连招段落（如近战砍两下接开枪接上挑再接枪）
且并未掉血"的片段，输出为预训练数据集（V7 行为克隆先验）。

片段判定（滑动窗口）：
  1. 华丽度：窗口内出现 >=4 种不同动作，且包含"组合"特征——
     - 近战类（1/2/3/4）与远程类（10）或切换类（9）混用
     - 包含防守/特技类动作（5 弹反 / 4 JC / 8 翻滚 / 6 格挡）
  2. 存活：窗口内所有步 reward >= 0（无受击 -30 / 被击倒 -30 惩罚）

用法：
    python train/crucible.py --data <轨迹目录> --out <预训练数据.npz>
"""
import argparse
import glob
import os
import struct

import numpy as np

STATE_DIM = 18
NUM_ACTIONS = 64

WINDOW = 15  # 片段长度
MIN_DISTINCT = 4  # 窗口内最少动作种类
MELEE = {1, 2, 3, 4} | set(range(11, 64))  # 近战/攻击/大招/JC + 技能槽
RANGED = {10}             # 切远程/射击
SWITCH = {9}              # 切换近战武器
STYLE = {4, 5, 6, 8} | set(range(11, 64))  # JC/弹反/格挡/翻滚 + 技能槽（特技类）
GENERIC_MAX = 10


def load_bin(path: str):
    with open(path, "rb") as f:
        n, sd, na = struct.unpack(">iii", f.read(12))
        states = np.frombuffer(f.read(n * sd * 4), dtype=">f4").reshape(n, sd).astype(np.float32)
        actions = np.frombuffer(f.read(n * 4), dtype=">i4").astype(np.int64)
        rewards = np.frombuffer(f.read(n * 4), dtype=">f4").astype(np.float32)
    return states, actions, rewards, na


def is_fancy_segment(actions: np.ndarray, rewards: np.ndarray, start: int):
    """判断窗口 [start, start+WINDOW) 是否为'高华丽且存活'片段。"""
    seg_a = actions[start:start + WINDOW]
    seg_r = rewards[start:start + WINDOW]
    # 1. 存活：无负奖励（受击 -30 / 被击倒 -30 / 滥用 -8 视为危险或低质量）
    if np.any(seg_r < 0):
        return False
    distinct = set(seg_a.tolist())
    if len(distinct) < MIN_DISTINCT:
        return False
    # 2. 组合特征：近战与远程/切换混用
    has_melee = bool(distinct & MELEE)
    has_ranged = bool(distinct & RANGED) or bool(distinct & SWITCH)
    if not (has_melee and has_ranged):
        return False
    # 3. 特技点缀：包含 JC/弹反/格挡/翻滚 至少一种
    if not (distinct & STYLE):
        return False
    return True


def crucible(data_dir: str, out_path: str, min_steps=30):
    files = sorted(glob.glob(os.path.join(data_dir, "*.bin")))
    if not files:
        raise FileNotFoundError(f"no trajectory files in {data_dir}")
    all_s, all_a = [], []
    total_windows = 0
    picked_windows = 0
    for f in files:
        s, a, r, na = load_bin(f)
        if na == 27:
            # 旧 v13 布局：动态技能槽语义无法映射，仅保留 generic 0-10
            keep = a <= GENERIC_MAX
            s, a, r = s[keep], a[keep], r[keep]
        elif na != 64:
            continue  # v12 及更早不支持
        if len(s) < min_steps:
            continue
        for start in range(0, len(s) - WINDOW):
            total_windows += 1
            if is_fancy_segment(a, r, start):
                picked_windows += 1
                all_s.append(s[start:start + WINDOW])
                all_a.append(a[start:start + WINDOW])
    if not all_s:
        raise RuntimeError("no fancy segments found!")
    states = np.concatenate(all_s)
    actions = np.concatenate(all_a)
    print(f"[crucible] windows checked: {total_windows}, picked: {picked_windows} ({picked_windows / max(1, total_windows) * 100:.2f}%)")
    print(f"[crucible] pretrain samples: {len(actions)}")
    np.savez_compressed(out_path, states=states, actions=actions)
    print(f"[crucible] saved: {out_path}")

    # 统计
    counts = np.bincount(actions, minlength=NUM_ACTIONS)
    total = len(actions)
    print("[crucible] action distribution:")
    for a in range(NUM_ACTIONS):
        if counts[a] > 0:
            print(f"  action {a}: {counts[a]} ({counts[a] / total * 100:.1f}%)")


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--data", default=None, help="轨迹目录（Boss 战）")
    ap.add_argument("--out", default="pretrain_data.npz")
    args = ap.parse_args()
    if args.data is None:
        args.data = os.path.join(os.path.expanduser("~"), "AppData", "Roaming", ".minecraft",
                                 "config", "eftlm_stylish", "trajectories")
    crucible(args.data, args.out)


if __name__ == "__main__":
    main()
