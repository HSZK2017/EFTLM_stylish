#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
近战风格轨迹清洗（V14 预训练数据）
==================================
按用户定义的标准，从轨迹中筛选"高价值近战风格"轨迹作为预训练数据：
  1. 角色与怪物平均距离较近：状态第 0 维 = 目标距离/16，轨迹均值 < 阈值（默认 0.25 = 4 格内）
  2. 枪械使用频次 < 近战频次：action=10（切远程/枪）次数 < 近战类动作次数
输出预训练 npz（states/actions），供行为克隆预训练使用。

V14 变更：
- 动作空间 64（11 通用 + 53 技能槽）；近战统计包含技能槽（11-63）
- 自动检测轨迹 actionDim：
  - 64（新布局）→ 全部动作直接作标签
  - 27（旧 v13 布局）→ 仅保留 generic 动作(0-10, 语义一致)；旧 11-26 是动态技能槽，
    语义无法映射到 64 布局，从预训练标签中剔除（过滤样本）
  - 其他（v12 16 维等）→ 跳过

用法：
    python train/extract_melee.py --data <轨迹目录> --out <melee_pretrain.npz>
"""
import argparse
import glob
import os
import struct

import numpy as np

from traj_io import load as load_trajectory

NUM_ACTIONS = 64
MELEE = set(range(1, 5)) | set(range(11, 64))  # 近战攻击 + 技能槽
RANGED = {10}
GENERIC_MAX = 10          # generic 动作上限（0-10 语义一致）
MIN_STEPS = 30


def load_bin(path: str):
    """统一轨迹读取（P0 修复 2026-09-10：转发到 traj_io 唯一实现）。

    返回 (states, actions, rewards, numActions)；损坏/不支持的文件返回 None。
    奖励已由 traj_io 完成 v1/v2 → v3 的一步平移补偿，与 train_ppo 口径一致。
    """
    t = load_trajectory(path)
    if t is None:
        return None
    return t.states, t.actions, t.rewards, t.num_actions


def extract(data_dir: str, out_path: str, dist_thresh=0.25):
    files = sorted(glob.glob(os.path.join(data_dir, "*.bin")))
    if not files:
        raise FileNotFoundError(f"no trajectory files in {data_dir}")
    all_s, all_a = [], []
    kept = 0
    skipped = 0
    for f in files:
        parsed = load_bin(f)
        if parsed is None:
            continue
        s, a, r, na = parsed
        if len(s) < MIN_STEPS:
            continue
        if na == 27:
            # 旧 v13 布局：剔除动态技能槽样本，仅保留 generic 0-10（语义一致）
            keep = a <= GENERIC_MAX
            s, a = s[keep], a[keep]
            if len(s) < MIN_STEPS:
                skipped += 1
                continue
        elif na != 64:
            skipped += 1
            continue  # v12 及更早（16 维/11 动作）不支持
        # 1. 平均距离近：状态第 0 维（目标距离/16）均值低于阈值
        mean_dist = s[:, 0].mean()
        # 2. 枪械频次 < 近战频次（近战含技能槽）
        ranged_cnt = int((a == 10).sum())
        melee_cnt = int(np.isin(a, list(MELEE)).sum())
        if mean_dist < dist_thresh and ranged_cnt < melee_cnt:
            all_s.append(s)
            all_a.append(a)
            kept += 1
    if not all_s:
        raise RuntimeError("no melee-style trajectories found!")
    # 维度归一化：历史 v1（18 维）与 v2（32 维）轨迹并存，统一补零到最大维度
    max_sd = max(s.shape[1] for s in all_s)
    if any(s.shape[1] != max_sd for s in all_s):
        all_s = [np.pad(s, ((0, 0), (0, max_sd - s.shape[1])), mode="constant") if s.shape[1] < max_sd else s for s in all_s]
        print(f"[extract] padded trajectory states to dim {max_sd}")
    states = np.concatenate(all_s)
    actions = np.concatenate(all_a)
    print(f"[extract] files checked: {len(files)}, kept melee-style: {kept}, skipped(unsupported/too-short): {skipped}")
    print(f"[extract] samples: {len(actions)}, avg dist={states[:, 0].mean():.3f} ({states[:, 0].mean() * 16:.1f} blocks)")
    counts = np.bincount(actions, minlength=NUM_ACTIONS)
    total = len(actions)
    for a in range(NUM_ACTIONS):
        if counts[a] > 0:
            print(f"  action {a}: {counts[a]} ({counts[a] / total * 100:.1f}%)")
    np.savez_compressed(out_path, states=states, actions=actions)
    print(f"[extract] saved: {out_path}")


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--data", default=None)
    ap.add_argument("--out", default="melee_pretrain.npz")
    ap.add_argument("--dist_thresh", type=float, default=0.25, help="平均距离阈值（状态0，0.25=4格）")
    args = ap.parse_args()
    if args.data is None:
        args.data = os.path.join(os.path.expanduser("~"), "AppData", "Roaming", ".minecraft",
                                 "config", "eftlm_stylish", "trajectories")
    extract(args.data, args.out, args.dist_thresh)


if __name__ == "__main__":
    main()
