#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""P0 修复自检（无需 torch / 不联网）：轨迹奖励对齐 + npz 载入。

覆盖：
 1. traj_io.load 对 v1/v2 的"奖励一步平移补偿"（P0-2）；
 2. v3（Java 新格式）不被平移；
 3. 损坏/截断文件返回 None；
 4. load_npz_samples 的维度对齐、越界动作过滤、权重归一化（P0-3 的数据入口）；
 5. 三整数历史写法（无版本号）+ 未知版本拒绝（2026-09-11 生产事故回归，见 [5] 注释）。

运行：python train/selftest_p0.py   （退出码 0 = 全部通过）
"""
import os
import struct
import sys
import tempfile

import numpy as np

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from traj_io import CURRENT_VERSION, load, load_npz_samples, REJECT_STATS  # noqa: E402
import traj_io  # noqa: E402

FAILED = []


def traj_io_reject_summary():
    return traj_io.reject_summary()


def check(name, cond, detail=""):
    print(f"  [{'PASS' if cond else 'FAIL'}] {name}{'' if cond else '  -> ' + detail}")
    if not cond:
        FAILED.append(name)


def write_traj(path, version, states, actions, rewards, labels=None):
    """按 Java RlDataRecorder 的大端格式写一条轨迹（用于自检）。"""
    n, sd = states.shape
    na = 64
    with open(path, "wb") as f:
        f.write(struct.pack(">iii", version, n, sd))
        f.write(struct.pack(">i", na))
        if version >= 2:
            labels = labels or [None] * n
            dict_list, idx = [], []
            for lab in labels:
                if lab is None:
                    idx.append(-1)
                else:
                    if lab not in dict_list:
                        dict_list.append(lab)
                    idx.append(dict_list.index(lab))
            f.write(struct.pack(">i", len(dict_list)))
            for lab in dict_list:
                b = lab.encode("utf-8")
                f.write(struct.pack(">h", len(b)))
                f.write(b)
        for row in states.astype(">f4"):
            f.write(row.tobytes())
        f.write(actions.astype(">i4").tobytes())
        f.write(rewards.astype(">f4").tobytes())
        if version >= 2:
            f.write(np.asarray(idx, dtype=">i2").tobytes())


def main():
    tmp = tempfile.mkdtemp(prefix="eftlm_p0_selftest_")
    rng = np.random.RandomState(0)
    states = rng.rand(4, 32).astype(np.float32)
    actions = np.array([1, 2, 3, 4], dtype=np.int64)

    print("[1] v2 轨迹：奖励应整体左移一位（P0-2 补偿）")
    v2_path = os.path.join(tmp, "v2.bin")
    write_traj(v2_path, 2, states, actions, np.array([10, -3, 5, 100], dtype=np.float32),
               labels=["idle", "swordmaster_atk", "swordmaster_atk", "ultimate"])
    t2 = load(v2_path)
    check("v2 载入成功", t2 is not None)
    check("v2 version=2", t2 is not None and t2.version == 2, f"got {None if t2 is None else t2.version}")
    check("v2 legacy_shifted=True", t2 is not None and t2.legacy_reward_shifted)
    check("v2 奖励 = [-3, 5, 100, 100]（左移一位，尾步保留）",
          t2 is not None and list(t2.rewards) == [-3.0, 5.0, 100.0, 100.0],
          f"got {None if t2 is None else list(t2.rewards)}")
    check("v2 标签保持对齐（不随奖励平移）",
          t2 is not None and t2.labels[0] == "idle" and t2.labels[3] == "ultimate")

    print("[2] v3 轨迹：奖励不动")
    v3_path = os.path.join(tmp, "v3.bin")
    write_traj(v3_path, CURRENT_VERSION, states, actions, np.array([10, -3, 5, 100], dtype=np.float32))
    t3 = load(v3_path)
    check("v3 载入成功且 version=3", t3 is not None and t3.version == 3)
    check("v3 legacy_shifted=False", t3 is not None and not t3.legacy_reward_shifted)
    check("v3 奖励原样 = [10, -3, 5, 100]",
          t3 is not None and list(t3.rewards) == [10.0, -3.0, 5.0, 100.0],
          f"got {None if t3 is None else list(t3.rewards)}")

    print("[3] 截断 / 损坏文件安全返回 None")
    bad = os.path.join(tmp, "bad.bin")
    with open(v2_path, "rb") as src, open(bad, "wb") as dst:
        dst.write(src.read()[:30])
    check("截断文件 -> None", load(bad) is None)
    with open(os.path.join(tmp, "empty.bin"), "wb") as f:
        pass
    check("空文件 -> None", load(os.path.join(tmp, "empty.bin")) is None)

    print("[4] npz 载入（--pretrain / --relabel 的数据入口）")
    p = os.path.join(tmp, "relabeled.npz")
    s18 = rng.rand(5, 18).astype(np.float32)
    np.savez_compressed(p, states=s18,
                        actions=np.array([0, 11, 999, -1, 5], dtype=np.int64),
                        rewards=np.zeros(5, dtype=np.float32),
                        weights=np.array([2, 2, 2, 2, 2], dtype=np.float32))
    out = load_npz_samples(p, state_dim=32, num_actions=64)
    check("npz 载入成功", out is not None)
    if out is not None:
        es, ea, ew = out
        check("越界动作被过滤（5 -> 3）", len(ea) == 3, f"got {len(ea)}")
        check("状态补零到 32 维", es.shape[1] == 32, f"got {es.shape}")
        check("权重归一化到均值 1", abs(float(ew.mean()) - 1.0) < 1e-5, f"got {float(ew.mean())}")
    p2 = os.path.join(tmp, "pretrain.npz")
    np.savez_compressed(p2, states=rng.rand(3, 32).astype(np.float32),
                        actions=np.array([1, 2, 3], dtype=np.int64))
    out2 = load_npz_samples(p2, state_dim=32, num_actions=64)
    check("无 weights 键 -> 全 1 权重", out2 is not None and abs(float(out2[2].mean()) - 1.0) < 1e-6)
    check("缺失 states 键 -> None",
          load_npz_samples(os.path.join(tmp, "nonexistent.npz"), 32, 64) is None)

    # ------------------------------------------------------------------
    # [5] 三整数历史写法（无版本号）与未知版本拒绝
    #
    # 事故背景（2026-09-11 19:04 轮训练直接失败）：P2-4 给"未知首整数"加了历史契约白名单，
    # 但白名单里的动作维按代码注释推断写成 (27, 64)，而真实历史数据是 **11**（只有通用动作）→
    # `trajectories_zombie` 的 690 条 11 维轨迹被全数拒绝 → `collect_data` 抛
    # FileNotFoundError → 整轮失败。当时本自检**全绿**——因为它只测 v1/v2/v3 的正式写法
    # （[version, numSteps, sd, na]），从没测过 `[numSteps, sd, na]` 这种 3 整数开头的老格式。
    # 教训：契约白名单必须有真实产物样本做回归，不能只按注释推断。
    # ------------------------------------------------------------------
    print("[5] 三整数历史写法 + 未知版本拒绝（2026-09-11 生产事故回归）")

    def write_legacy(path, n, sd, na, actions):
        """早期写法：[numSteps, sd, na] + states + actions + rewards（无版本号、无标签）。"""
        with open(path, "wb") as f:
            f.write(struct.pack(">iii", n, sd, na))
            f.write(np.asarray(rng.rand(n, sd), dtype=">f4").tobytes())
            f.write(np.asarray(actions, dtype=">i4").tobytes())
            f.write(np.asarray(rng.rand(n), dtype=">f4").tobytes())

    legacy = os.path.join(tmp, "legacy_600_16_11.bin")
    act = [i % 11 for i in range(600)]
    write_legacy(legacy, 600, 16, 11, act)
    t = load(legacy)
    check("3 整数历史写法（600/16/11）解析成功", t is not None)
    if t is not None:
        check("  -> version=1", t.version == 1, f"got {t.version}")
        check("  -> state_dim=16", t.state_dim == 16, f"got {t.state_dim}")
        check("  -> num_actions=11", t.num_actions == 11, f"got {t.num_actions}")
        check("  -> 步数=600 且动作未被指针错位破坏",
              len(t.actions) == 600 and int(t.actions[0]) == 0 and int(t.actions[5]) == 5,
              f"len={len(t.actions)} a0={int(t.actions[0])} a5={int(t.actions[5])}")

    # numSteps 恰为 1/2/3 时与"版本号"二义：必须靠维度契约判定为老格式
    amb = os.path.join(tmp, "legacy_1_16_11.bin")
    write_legacy(amb, 1, 16, 11, [7])
    t2 = load(amb)
    check("二义样本（numSteps=1）判为老格式而非 v1",
          t2 is not None and t2.version == 1 and t2.state_dim == 16 and t2.num_actions == 11
          and len(t2.actions) == 1,
          "None" if t2 is None else
          f"v{t2.version} sd={t2.state_dim} na={t2.num_actions} len={len(t2.actions)}")

    # 未来版本的头部必须被明确拒绝（而不是当成老格式解析出垃圾）
    fut = os.path.join(tmp, "future_v4.bin")
    with open(fut, "wb") as f:
        f.write(struct.pack(">iii", 4, 600, 40))   # version=4, numSteps=600, sd=40
        f.write(b"\x00" * (600 * 40 * 4 + 600 * 4 + 600 * 4 + 600 * 1))
    check("未知版本头（4/600/40）-> None", load(fut) is None)
    check("拒绝原因被记录（可观测）",
          any("unknown-version" in k for k in REJECT_STATS), traj_io_reject_summary())

    print()
    if FAILED:
        print(f"FAILED: {len(FAILED)} -> {FAILED}")
        return 1
    print("ALL PASS (P0 self-test)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
