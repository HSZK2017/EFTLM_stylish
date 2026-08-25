#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
EFTLM 教官 AI（P3）：针对模型薄弱点生成敌对标靶训练建议
=================================================
分析最新采集的轨迹，量化战斗薄弱点，输出课程配置建议：
1. 指标统计（逐轨迹，输出均值）：
   - 受击率：reward == -30（V4 受击惩罚）步占比
   - 击倒率：状态 s[9] > 0.5 步占比
   - 远程占比：action == 10（切远程）步占比
   - 被拒率：状态 s[17]（执行被拒反馈）步占比
   - 动作熵：动作分布香农熵（多样性）
   - 平均轨迹步数（存活时长代理）＋ 击杀信号（reward >= 100）
2. 薄弱点判定（阈值可配）→ 课程建议（arena 标靶/参数覆盖）。
3. 输出 course_config.json（config/eftlm_stylish/course.json），
   P4 起由 AutoArena 读取做自动对手调度；当前版本作为训练报告输出。

用法：
    python train/instructor.py --data <轨迹目录> --out course_config.json \
        [--window 200] [--thresholds '{"hurt":0.2,"knockdown":0.05,"ranged":0.3,"rejected":0.2,"entropy":2.0}']
"""
import argparse
import glob
import json
import os
import struct
from collections import Counter

import numpy as np

# 薄弱点 → 建议标靶（arena.properties entity 逗号分隔）/ 参数覆盖
WEAKNESS_PRESETS = {
    "hurt_high": {
        "reason": "受击率高：躲避/弹反不足",
        "suggested_entities": ["annoyingvillagers:null", "annoyingvillagers:swordsman_herobrine"],
        "arena_overrides": {"entity": "annoyingvillagers:swordsman_herobrine"},
        "coach_note": "优先练习弹道规避与前摇闪避（reactive_projectile_* / reactive_windup_*）",
    },
    "knockdown_high": {
        "reason": "被击倒率高：起身与范围规避不足",
        "suggested_entities": ["annoyingvillagers:angry_steve", "annoyingvillagers:sledgehammer_herobrine"],
        "arena_overrides": {"entity": "annoyingvillagers:sledgehammer_herobrine"},
        "coach_note": "练习前摇闪避（跃起时刻）与起身防御窗口（recovery_guard_start）",
    },
    "ranged_high": {
        "reason": "远程依赖高：近战贴脸不足",
        "suggested_entities": ["annoyingvillagers:angry_steve", "annoyingvillagers:glaive_herobrine"],
        "arena_overrides": {"entity": "annoyingvillagers:angry_steve", "cage_radius": 6},
        "coach_note": "缩小斗兽场逼战（cage_radius 覆盖）",
    },
    "rejected_high": {
        "reason": "行动被拒率高：择时/冷却管理不足",
        "suggested_entities": ["annoyingvillagers:alex"],
        "arena_overrides": {"entity": "annoyingvillagers:alex"},
        "coach_note": "检查 /rl dump 的 res 列（BUSY 占比）与 Commitment 帧数据",
    },
    "entropy_low": {
        "reason": "动作多样性低：连段单一",
        "suggested_entities": ["annoyingvillagers:armored_herobrine"],
        "arena_overrides": {"entity": "annoyingvillagers:armored_herobrine"},
        "coach_note": "增加课程对手类型轮换（多标靶轮流生成）",
    },
    "kill_low": {
        "reason": "击杀效率低：输出窗口利用不足",
        "suggested_entities": ["annoyingvillagers:glaive_herobrine", "annoyingvillagers:swordsman_herobrine"],
        "arena_overrides": {"entity": "annoyingvillagers:glaive_herobrine"},
        "coach_note": "练习后摇窗口输出与技能宏连段（切换连携）",
    },
}


def load_bin(path):
    """v1/v2 轨迹读取（与 train_ppo.load_bin 一致），损坏/截断返回 None。"""
    try:
        with open(path, "rb") as f:
            first = struct.unpack(">i", f.read(4))[0]
            if first in (1, 2):
                version = first
                n, sd, na = struct.unpack(">iii", f.read(12))
            else:
                version = 1
                n, sd, na = first, *struct.unpack(">ii", f.read(8))
            if version >= 2:
                n_labels = struct.unpack(">i", f.read(4))[0]
                for _ in range(n_labels):
                    (ln,) = struct.unpack(">h", f.read(2))
                    f.read(ln)
            states = np.frombuffer(f.read(n * sd * 4), dtype=">f4").reshape(n, sd).astype(np.float32)
            actions = np.frombuffer(f.read(n * 4), dtype=">i4").astype(np.int64)
            rewards = np.frombuffer(f.read(n * 4), dtype=">f4").astype(np.float32)
        return states, actions, rewards
    except Exception:
        return None


def analyze(data_dir, min_steps=30):
    """统计全部轨迹的聚合指标。"""
    files = sorted(glob.glob(os.path.join(data_dir, "*.bin")))
    if not files:
        raise FileNotFoundError(f"no trajectory files in {data_dir}")
    agg = {"steps": 0, "hurt": 0, "knockdown": 0, "ranged": 0, "rejected": 0,
           "kill": 0, "trajs": 0, "action_counter": Counter()}
    for f in files:
        parsed = load_bin(f)
        if parsed is None:
            continue
        s, a, r = parsed
        if len(s) < min_steps:
            continue
        agg["trajs"] += 1
        agg["steps"] += len(r)
        agg["hurt"] += int((r == -30).sum())
        if s.shape[1] > 9:
            agg["knockdown"] += int((s[:, 9] > 0.5).sum())
        agg["ranged"] += int((a == 10).sum())
        if s.shape[1] > 17:
            agg["rejected"] += int((s[:, 17] > 0.5).sum())
        agg["kill"] += int((r >= 100).sum())
        agg["action_counter"].update(a.tolist())
    if agg["trajs"] == 0 or agg["steps"] == 0:
        raise RuntimeError("no usable trajectories")
    n = agg["steps"]
    metrics = {
        "trajectories": agg["trajs"],
        "steps": n,
        "hurt_rate": agg["hurt"] / n,
        "knockdown_rate": agg["knockdown"] / n,
        "ranged_ratio": agg["ranged"] / n,
        "rejected_rate": agg["rejected"] / n,
        "kill_rate": agg["kill"] / n,
        "avg_steps": n / agg["trajs"],
        # 动作熵（多样性）
        "action_entropy": _entropy(agg["action_counter"]),
    }
    return metrics


def _entropy(counter):
    total = sum(counter.values())
    if total <= 0:
        return 0.0
    return -sum((c / total) * np.log2(c / total) for c in counter.values())


def judge(metrics, thresholds):
    """薄弱点判定 → 课程建议。"""
    weakness = []
    notes = []
    t = thresholds
    if metrics["hurt_rate"] > t.get("hurt", 0.2):
        weakness.append("hurt_high")
        notes.append(WEAKNESS_PRESETS["hurt_high"]["coach_note"])
    if metrics["knockdown_rate"] > t.get("knockdown", 0.05):
        weakness.append("knockdown_high")
        notes.append(WEAKNESS_PRESETS["knockdown_high"]["coach_note"])
    if metrics["ranged_ratio"] > t.get("ranged", 0.3):
        weakness.append("ranged_high")
        notes.append(WEAKNESS_PRESETS["ranged_high"]["coach_note"])
    if metrics["rejected_rate"] > t.get("rejected", 0.2):
        weakness.append("rejected_high")
        notes.append(WEAKNESS_PRESETS["rejected_high"]["coach_note"])
    if metrics["action_entropy"] < t.get("entropy", 2.0):
        weakness.append("entropy_low")
        notes.append(WEAKNESS_PRESETS["entropy_low"]["coach_note"])
    if metrics["kill_rate"] < t.get("kill", 0.01):
        weakness.append("kill_low")
        notes.append(WEAKNESS_PRESETS["kill_low"]["coach_note"])
    if not weakness:
        weakness.append("balanced")
        notes.append("全面达标：进入自我博弈课程（自适应对手女仆登场，L6）")
    return weakness, notes


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--data", default=None)
    ap.add_argument("--out", default="course_config.json")
    ap.add_argument("--min-steps", type=int, default=30)
    ap.add_argument("--thresholds", default=None, help='JSON：{"hurt":0.2,...}')
    args = ap.parse_args()
    if args.data is None:
        args.data = os.path.join(os.path.expanduser("~"), "AppData", "Roaming", ".minecraft",
                                 "config", "eftlm_stylish", "trajectories")
    thresholds = json.loads(args.thresholds) if args.thresholds else {}

    metrics = analyze(args.data, args.min_steps)
    weakness, notes = judge(metrics, thresholds)
    print("[instructor] metrics:", json.dumps(metrics, indent=2))
    print(f"[instructor] weakness: {weakness}")

    config = {
        "generated_at": __import__("datetime").datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
        "metrics": metrics,
        "weakness": weakness,
        "notes": notes,
        "suggested_entities": [],
        "arena_overrides": {},
    }
    for w in weakness:
        preset = WEAKNESS_PRESETS.get(w)
        if preset:
            config["suggested_entities"].extend(preset["suggested_entities"])
            config["arena_overrides"].update(preset["arena_overrides"])
    config["suggested_entities"] = list(dict.fromkeys(config["suggested_entities"]))
    # P5.6 课程完成信号：全面达标（balanced）→ 全部课程训练结束，自我博弈自适应对手女仆登场
    # （AutoArena 读到 selfplay=true 后生成模仿者规则 AI 女仆作为对手，女仆 vs 女仆）
    config["selfplay"] = "balanced" in weakness
    with open(args.out, "w", encoding="utf-8") as f:
        json.dump(config, f, ensure_ascii=False, indent=2)
    print(f"[instructor] course config written: {args.out} (selfplay={config['selfplay']})")


if __name__ == "__main__":
    main()
