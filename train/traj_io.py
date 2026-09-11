"""轨迹文件统一读取（唯一实现）。

背景（P0 修复 2026-09-10）
-------------------------
本模块取代原先散落在 5 个脚本里的 `load_bin` 副本（train_ppo / instructor / crucible /
relabel / extract_melee）。它们对损坏文件的边界校验强度不一致，且**都忽略了一个契约问题**：

    轨迹文件 v1/v2 的 `rewards[i]` 实际是 `actions[i-1]` 的后果（Java 侧 RlDataRecorder
    旧实现把"上次决策以来累计的奖励"写进本步），而训练侧 GAE
    （delta = r_t + γV_{t+1} − V_t）假设 `rewards[t]` 是 `actions[t]` 的后果
    → 奖励与动作整体错位一步。

Java 侧已修（v3 = 正确对齐：后果奖励回填上一步，塑形奖励记本步）。本模块对**旧格式**做一次
一步平移近似补偿，使新旧数据可以混用：

    v1/v2:  r_new[i] = r_old[i+1]  (i < T-1)；  r_new[T-1] = r_old[T-1]

    近似说明（三条都要知道）：
      1. 旧语义 r_old[i] = 后果(a[i-1]) + 塑形(a[i])，最后一步 r_old[T-1] 已经在 flush 时吸收了
         尾步后果，因此最后一步不需平移；
      2. 中间步的塑形分量无法无损分离，平移后误差 = 塑形(a[i]) − 塑形(a[i+1])，
         量级远小于主奖励（命中 +30 / 击杀 +100 / 受击 −30）；
      3. 末步信用会被计入两次（r_old[T-1] 同时作为 new[T-2] 与 new[T-1]）——单步占 600 步轨迹的
         0.17%，且 v3 起不再存在该问题；若需要严格口径，只用 v3 轨迹训练。

文件格式（大端）：
    int32 version(1/2/3) int32 numSteps int32 stateDim int32 numActions
    [v2+] int32 numLabels + (int16 len + utf8) × numLabels
    float32 states[n*sd] int32 actions[n] float32 rewards[n] [v2+] int16 labelIdx[n]
"""

from __future__ import annotations

import os
import struct
from typing import List, NamedTuple, Optional

import numpy as np

#: 当前 Java 侧写出的轨迹版本（奖励已按动作对齐）
CURRENT_VERSION = 3
#: 单条轨迹最大步数上限（Java 侧 MAX_STEPS=600；留 100x 余量做损坏文件防护）
MAX_STEPS = 100_000
#: 状态/动作维度上限（防损坏文件触发超大 reshape 内存爆炸）
MAX_STATE_DIM = 512
MAX_ACTIONS = 4096
#: 标签字典上限
MAX_LABELS = 10_000
MAX_LABEL_LEN = 256


class Trajectory(NamedTuple):
    """一条轨迹（奖励已按 `rewards[i] ↔ actions[i]` 对齐）。"""

    states: np.ndarray          # (T, stateDim) float32
    actions: np.ndarray         # (T,) int64
    rewards: np.ndarray         # (T,) float32
    labels: Optional[List[Optional[str]]]  # (T,) 语义标签；v1 为 None
    version: int
    state_dim: int
    num_actions: int
    legacy_reward_shifted: bool  # 是否做过一步平移补偿（v1/v2 = True）


def _read_exact(f, nbytes: int) -> bytes:
    """读取恰好 nbytes 字节；EOF 提前（半写/截断文件）抛 EOFError。"""
    data = f.read(nbytes)
    if len(data) != nbytes:
        raise EOFError("truncated trajectory file")
    return data


#: 拒绝原因计数（P2-4）：`load()` 返回 None 时**必须**留下可观测的痕迹。
#: 原实现所有失败路径都静默 `return None`，调用方只把它们计成 "skipped"——
#: 格式事故会表现为"样本量悄悄变少"，而不是报错（审查报告风险 H）。
REJECT_STATS: "dict[str, int]" = {}


def _reject(reason: str) -> None:
    REJECT_STATS[reason] = REJECT_STATS.get(reason, 0) + 1
    return None


def reject_summary() -> str:
    """把拒绝原因汇总成一行（供 train_ppo / relabel 等调用方打印）。"""
    if not REJECT_STATS:
        return "no rejects"
    items = sorted(REJECT_STATS.items(), key=lambda kv: -kv[1])
    return ", ".join(f"{k}={v}" for k, v in items)


#: 历史状态维度（16 → 18 → 32；40 为 P2-3 规划值）
LEGACY_STATE_DIMS = (16, 18, 32, 40)
#: 历史动作维度。**实测（2026-09-11 扫全部轨迹目录的文件头）**：
#: 最早期 = 11（只有通用动作，`NUM_ACTIONS`，无技能槽），其后 27（16 技能池时代），当前 64。
#: 教训：这里原本按代码注释推断写成 (27, 64)，结果 690 条 `trajectories_zombie`
#: 的 11 维历史轨迹被当成"未知版本"全数拒绝 → `collect_data` 抛 FileNotFoundError，
#: 2026-09-11 19:04 那轮训练直接失败。**不要按注释推断契约，扫真实文件头。**
LEGACY_ACTION_DIMS = (11, 27, 64)


def _plausible_header(sd: int, na: int) -> bool:
    """头部是否落在已知历史契约内（用于消解 v1 前导 int 的二义性）。"""
    return (sd in LEGACY_STATE_DIMS and na in LEGACY_ACTION_DIMS
            and 0 < sd <= MAX_STATE_DIM and 0 < na <= MAX_ACTIONS)


def load(path: str) -> Optional[Trajectory]:
    """读取一条轨迹；损坏/不支持的版本返回 None（绝不抛异常到调用方）。

    拒绝原因计入模块级 `REJECT_STATS`（P2-4）——调用方可用 `reject_summary()`
    打印，避免"格式事故 = 样本量静默减少"。
    """
    try:
        with open(path, "rb") as f:
            # 头部只有两种历史写法，且都是大端 int：
            #   ① [version, numSteps, sd, na]  —— v1/v2/v3 正式写法（4 个 int）
            #   ② [numSteps, sd, na]           —— 最早期无版本号写法（3 个 int）
            # numSteps 恰好等于 1/2/3 的早期文件与①二义，只能靠"维度是否落在历史契约内"判定；
            # 判定为②时必须把指针退回 12（①多读的 4 字节会吃掉状态数组的开头）。
            first, a, b = struct.unpack(">iii", _read_exact(f, 12))
            version = 0
            n = sd = na = 0
            if first in (1, 2, 3):
                na_extra = struct.unpack(">i", _read_exact(f, 4))[0]
                if _plausible_header(b, na_extra):
                    version, n, sd, na = first, a, b, na_extra
                elif _plausible_header(a, b):
                    f.seek(12)  # 回退到写法②的起点
                    version, n, sd, na = 1, first, a, b
            elif _plausible_header(a, b):
                version, n, sd, na = 1, first, a, b
            if version == 0:
                return _reject(f"unknown-version(first={first},a={a},b={b})")
            if n <= 0 or sd <= 0 or na <= 0:
                return _reject("nonpositive-header")
            if n > MAX_STEPS or sd > MAX_STATE_DIM or na > MAX_ACTIONS:
                return _reject("header-over-limit")
            labels: Optional[List[Optional[str]]] = None
            if version >= 2:
                n_labels = struct.unpack(">i", _read_exact(f, 4))[0]
                if n_labels < 0 or n_labels > MAX_LABELS:
                    return _reject("label-count-over-limit")
                dict_list: List[str] = []
                for _ in range(n_labels):
                    (ln,) = struct.unpack(">h", _read_exact(f, 2))
                    if ln < 0 or ln > MAX_LABEL_LEN:
                        return _reject("label-len-over-limit")
                    dict_list.append(_read_exact(f, ln).decode("utf-8", "replace"))
            states = np.frombuffer(_read_exact(f, n * sd * 4), dtype=">f4").reshape(n, sd).astype(np.float32)
            actions = np.frombuffer(_read_exact(f, n * 4), dtype=">i4").astype(np.int64)
            rewards = np.frombuffer(_read_exact(f, n * 4), dtype=">f4").astype(np.float32)
            if version >= 2:
                idx = np.frombuffer(_read_exact(f, n * 2), dtype=">i2").astype(np.int64)
                labels = [dict_list[i] if 0 <= i < len(dict_list) else None for i in idx]
    except (EOFError, OSError, ValueError, struct.error) as e:
        return _reject(f"parse-error({type(e).__name__})")

    shifted = False
    if version < CURRENT_VERSION and len(rewards) > 1:
        # 旧格式：rewards[i] 是 actions[i-1] 的后果 → 左移一位补偿（见模块 docstring）
        rewards = np.concatenate([rewards[1:], rewards[-1:]]).astype(np.float32)
        shifted = True
    return Trajectory(states, actions, rewards, labels, version, sd, na, shifted)


def load_bin(path: str):
    """向后兼容旧签名：返回 (states, actions, rewards, labels)；损坏文件返回 None。

    （analyze_traj.py 等旧调用方仍可用；新代码请直接用 load() 以获得 version 等信息。）
    """
    t = load(path)
    if t is None:
        return None
    return t.states, t.actions, t.rewards, t.labels


def load_npz_samples(path: str, state_dim: int, num_actions: int):
    """加载预训练/重标注 npz（train/extract_melee.py、crucible.py、relabel.py 的产物）。

    返回 (states, actions, weights) 或 None；weights 为样本权重（无 `weights` 键时全 1）。
    用途：**仅参与 BC（行为克隆）阶段**——这些 npz 是"窗口/片段"的拼接，没有轨迹边界，
    无法计算 GAE，因此不参与 AWR/优势估计（train_ppo.train 的 bc_extra 参数）。
    """
    if not path or not os.path.exists(path):
        print(f"[traj_io] npz not found: {path}")
        return None
    try:
        with np.load(path, allow_pickle=False) as z:  # allow_pickle=False：防 pickle 反序列化
            if "states" not in z.files or "actions" not in z.files:
                print(f"[traj_io] npz missing states/actions: {path} (keys={z.files})")
                return None
            states = np.asarray(z["states"], dtype=np.float32)
            actions = np.asarray(z["actions"], dtype=np.int64).reshape(-1)
            weights = np.asarray(z["weights"], dtype=np.float32).reshape(-1) if "weights" in z.files else None
    except (OSError, ValueError, KeyError) as e:
        print(f"[traj_io] failed to load npz {path}: {e}")
        return None
    if states.ndim != 2 or len(states) == 0 or len(states) != len(actions):
        print(f"[traj_io] npz shape mismatch: states={states.shape} actions={actions.shape}")
        return None
    if weights is not None and len(weights) != len(actions):
        print("[traj_io] npz weights length mismatch, ignored")
        weights = None
    # 状态维度对齐（历史 npz 可能是 16/18 维；补零/截断到当前维度）
    if states.shape[1] < state_dim:
        states = np.pad(states, ((0, 0), (0, state_dim - states.shape[1])), mode="constant")
    elif states.shape[1] > state_dim:
        states = states[:, :state_dim]
    # 动作合法性过滤（越界动作会让 cross_entropy 直接 IndexError）
    keep = (actions >= 0) & (actions < num_actions)
    dropped = int((~keep).sum())
    if dropped:
        print(f"[traj_io] npz dropped {dropped} out-of-range actions")
        states, actions = states[keep], actions[keep]
        if weights is not None:
            weights = weights[keep]
    if len(actions) == 0:
        return None
    if weights is None:
        weights = np.ones(len(actions), dtype=np.float32)
    # 权重归一化到均值 1 并裁剪（relabel 的 PER 权重分布未知，防单样本主导）
    weights = np.clip(weights, 0.0, 50.0).astype(np.float32)
    mean = float(weights.mean())
    if mean > 1e-6:
        weights = (weights / mean).astype(np.float32)
    return states, actions, weights
