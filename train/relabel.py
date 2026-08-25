#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Replay Buffer 清洗与事后奖励重标注（V7 约束）
================================================
V7 轨迹中 88-94% 是"远程龟缩"偏好数据（有毒），不能全量喂养。
本脚本：
  1. PER（Priority Experience Replay）：
     - 近战/弹反/高连段突进片段（动作组合多样）→ 高优先级权重
     - 龟缩打手枪（连续远程）→ 低权重
  2. Hindsight Reward Relabeling：
     - 整局以远程偷死 Boss 的轨迹：击杀等胜利奖励调低（+100 → +30）
     - 偶尔用近战组合打出高输出的片段：奖励人工调高（×1.5）
  3. 输出带权重的数据（npz：states/actions/rewards/weights），供 PER 采样训练。

用法：
    python train/relabel.py --data <轨迹目录> --out <relabeled.npz>
"""
import argparse
import glob
import os
import struct

import numpy as np

STATE_DIM = 18
NUM_ACTIONS = 64

MELEE = {1, 2, 3, 4} | set(range(11, 64))  # 近战/大招/JC + 技能槽
STYLE = {4, 5, 6, 7, 8} | set(range(11, 64))  # JC/弹反/格挡/闪避/翻滚 + 技能槽（特技）
SWITCH = {9}
RANGED = {10}
GENERIC_MAX = 10
WINDOW = 15
FANCY_DISTINCT = 4


def load_bin(path: str):
    with open(path, "rb") as f:
        n, sd, na = struct.unpack(">iii", f.read(12))
        states = np.frombuffer(f.read(n * sd * 4), dtype=">f4").reshape(n, sd).astype(np.float32)
        actions = np.frombuffer(f.read(n * 4), dtype=">i4").astype(np.int64)
        rewards = np.frombuffer(f.read(n * 4), dtype=">f4").astype(np.float32)
    return states, actions, rewards, na


def per_weight(actions: np.ndarray, start: int) -> float:
    """PER 优先级：窗口内动作越多样、近战/特技越多 → 权重越高。"""
    seg = set(actions[start:start + WINDOW].tolist())
    distinct = len(seg)
    if distinct < 3:
        return 0.05  # 单调龟缩
    base = 0.2 + 0.2 * (distinct - 3)
    melee_ratio = np.isin(actions[start:start + WINDOW], list(MELEE | STYLE)).mean()
    base += 2.0 * melee_ratio
    if seg & MELEE and (seg & RANGED or seg & SWITCH):
        base += 1.0  # 近战+远程/切换组合连招
    return base


def relabel(data_dir: str, out_path: str, min_steps=30):
    files = sorted(glob.glob(os.path.join(data_dir, "*.bin")))
    if not files:
        raise FileNotFoundError(f"no trajectory files in {data_dir}")
    all_s, all_a, all_r, all_w = [], [], [], []
    relabel_count = 0
    for f in files:
        s, a, r, na = load_bin(f)
        if na == 27:
            # 旧 v13 布局：动态技能槽(11-26)语义无法映射到 64 布局，仅保留 generic 0-10
            keep = a <= GENERIC_MAX
            s, a, r = s[keep], a[keep], r[keep]
        elif na != 64:
            continue  # v12 及更早不支持
        if len(s) < min_steps:
            continue
        # 轨迹级统计
        ranged_ratio = (a == 10).mean()
        total_reward = r.sum()
        # 1) Hindsight：整局远程偷死 → 胜利奖励贬值
        if ranged_ratio > 0.7 and total_reward >= 100:
            # 将击杀类大奖励(>=100)降到 30
            r = np.where(r >= 100, 30.0, r)
            relabel_count += 1
        # 2) Hindsight：近战组合高输出片段 → 奖励上调
        # 修复：步长 1 的重叠滑窗若直接对窗口整体 *=1.5，同一步最多会被 15 个窗口
        # 重复放大（×1.5^15 ≈ ×437），奖励指数级膨胀。改为按步聚合命中标记后一次性缩放。
        r = r.copy().astype(np.float32)
        boost = np.zeros(len(r), dtype=bool)
        for start in range(0, len(r) - WINDOW):
            seg_a = a[start:start + WINDOW]
            seg_r = r[start:start + WINDOW]
            melee_count = np.isin(seg_a, list(MELEE | STYLE)).sum()
            if melee_count >= 4 and seg_r.sum() > 0:
                boost[start:start + WINDOW] = True
        r[boost] *= 1.5
        # 3) PER 权重（每个决策步）
        w = np.zeros(len(a), dtype=np.float32)
        for start in range(0, len(a) - WINDOW + 1, 5):
            w[start:start + WINDOW] = np.maximum(w[start:start + WINDOW], per_weight(a, start))
        w[w <= 0] = 0.05
        all_s.append(s)
        all_a.append(a)
        all_r.append(r)
        all_w.append(w)
    states = np.concatenate(all_s)
    actions = np.concatenate(all_a)
    rewards = np.concatenate(all_r)
    weights = np.concatenate(all_w)
    print(f"[relabel] files={len(files)} samples={len(actions)} relabeled(win-depreciated)={relabel_count}")
    print(f"[relabel] reward: mean={rewards.mean():.2f} max={rewards.max():.1f} min={rewards.min():.1f}")
    print(f"[relabel] weight: mean={weights.mean():.3f} max={weights.max():.1f}")
    # 动作分布
    counts = np.bincount(actions, minlength=NUM_ACTIONS)
    total = len(actions)
    for a in range(NUM_ACTIONS):
        if counts[a] > 0:
            print(f"  action {a}: {counts[a]} ({counts[a] / total * 100:.1f}%)")
    np.savez_compressed(out_path, states=states, actions=actions, rewards=rewards, weights=weights)
    print(f"[relabel] saved: {out_path}")


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--data", default=None)
    ap.add_argument("--out", default="relabeled.npz")
    args = ap.parse_args()
    if args.data is None:
        args.data = os.path.join(os.path.expanduser("~"), "AppData", "Roaming", ".minecraft",
                                 "config", "eftlm_stylish", "trajectories")
    relabel(args.data, args.out)


if __name__ == "__main__":
    main()
