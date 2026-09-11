#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
EFTLM 华丽连段 RL 离线训练脚本 V2（P3 训练流水线重构）
=================================================
V2 相对 V1（V13~V46 时代）的变更：

1. **Actor-Critic**：新增价值头（64-64-1），GAE 优势用 critic baseline
   （V1 的 GAE baseline=0 是方差爆炸的根源——V1 实际是"奖励加权克隆"）。
2. **轨迹格式自适应**：支持 v1（旧 16/18/32 维无标签）与 v2（带动作标签字典）。
3. **标签语义对齐**：P2 稳定槽位后，技能槽动作索引跨布局/武器漂移——
   v2 轨迹记录 slot.label()（技能 id / generic 名），训练时经 layout.json
   （/rl layout 导出）把标签重映射到参考布局槽位；无法映射的样本丢弃。
4. **AWR 训练**：BC 预热（学习规则策略分布）→ Advantage Weighted Regression
   （离线数据上最稳健的策略优化，天然避免 V1 RW-BC 的分布塌缩）。
5. **验证集门禁**：留出 10% 轨迹（按轨迹隔离，防泄漏），训练后评估
   acc/NLL，写 metrics.json 供 iterate.py 做部署门禁。

旧数据可用性（审查结论，报告 P3）：
- 旧 v1 轨迹：状态维度兼容（32 维新状态与 v46 一致）；generic 动作 0..10
  语义未变 → **可复用**；技能槽 11..63 标签按旧动态布局，无法映射 →
  **仅 generic 段参与监督**（技能槽样本丢弃）。
- 旧模型 v13~v46：网络结构一致（64-64），可 --init 续训（V1 已支持，
  输出层 27→64 已处理；V2 增加 critic 头从零初始化）。

用法:
    python train/train_ppo.py --data <轨迹目录> --out <模型> \
        [--layout layout.json] [--zombie_dir ...] [--pretrain npz] [--relabel npz] \
        [--init prev.bin] [--eval 0.1] [--gate prev_metrics.json]
"""
import argparse
import glob
import json
import os
import struct
import time
from datetime import datetime

import numpy as np
import torch
import torch.nn as nn
import torch.nn.functional as F

# 轨迹读取唯一实现（含 v1/v2 → v3 的奖励一步平移补偿，见 traj_io 模块 docstring）
from traj_io import CURRENT_VERSION, load as load_trajectory
from traj_io import load_bin as _load_bin_compat
from traj_io import load_npz_samples
import traj_io  # P2-4：拒绝原因统计（reject_summary）

NUM_ACTIONS = 64
GENERIC = 11
# 轨迹/训练"语义纪元"：数据集定义变化时递增，供部署门禁识别"不可与旧基线比 acc"的轮次。
#   1 = v2/v3（含强制等待步、奖励已对齐）
#   2 = 2026-09-11 起：强制等待步排除出训练集（动作窗口关闭时不记录决策）
SEMANTICS_EPOCH = 3
HIDDEN = 64
GAMMA = 0.99
LAMBDA = 0.95
AWR_TEMP = 1.0  # AWR 温度：advantage 权重 exp(adv/T)


# ----------------------------------------------------------------------
# 轨迹解析（转发到 traj_io：唯一实现，v1/v2/v3 自适应 + 奖励对齐补偿）
# ----------------------------------------------------------------------
def load_bin(path):
    """兼容旧签名（analyze_traj.py 仍 import 本函数）：返回 (states, actions, rewards, labels)。"""
    return _load_bin_compat(path)


def collect_data(data_dir, min_steps=30, layout=None, stat=None, max_same_ratio=0.95, reward_clip=100.0):
    """读取轨迹目录，返回 (states, actions, rewards) 列表（逐轨迹隔离）。

    layout: {generic: [labels], skills: {slot: skill_id}, defense: {...}} 或 None。
    标签重映射规则：
      - generic 标签（idle/swordmaster_atk/...）→ 0..10 固定索引；
      - 技能 id 标签 → layout.skills 中的槽位（参考布局）；
      - 无法映射 / 无标签的样本：动作 > GENERIC 时丢弃（技能槽语义不可靠）。

    P5.8 严格数据过滤（自我博弈/训练稳定性关键）：
      - max_same_ratio：单动作占比超阈值（默认 95%）的轨迹丢弃（无信息量）；
      - reward_clip：异常奖励峰值裁剪到 ±reward_clip（防离群样本拉偏策略）。
    """
    files = sorted(glob.glob(os.path.join(data_dir, "*.bin")))
    if not files:
        raise FileNotFoundError(f"no trajectory files in {data_dir}")
    trajs = []
    skipped = 0
    stats = {"v1": 0, "v2": 0, "v3": 0, "legacy_reward_shifted": 0,
             "bad_action": 0, "dim_mismatch": 0, "dropped_skill": 0, "kept": 0, "low_info": 0}
    for f in files:
        traj = load_trajectory(f)
        if traj is None:
            skipped += 1
            continue
        s, a, r, labels = traj.states, traj.actions, traj.rewards, traj.labels
        stats[f"v{traj.version}"] = stats.get(f"v{traj.version}", 0) + 1
        if traj.legacy_reward_shifted:
            # v1/v2：奖励整体滞后一步 → traj_io 已做一步平移补偿（P0 修复）
            stats["legacy_reward_shifted"] += 1
        if traj.num_actions != NUM_ACTIONS:
            # P1 修复：动作维度与当前契约不符（旧 27 动作模型时代的数据）——记录并告警一次
            stats["dim_mismatch"] += 1
            if stats["dim_mismatch"] == 1:
                print(f"  [WARN] {os.path.basename(f)}: numActions={traj.num_actions} != {NUM_ACTIONS} "
                      f"(legacy action space; skill slots will be dropped by layout remap)")
        if traj.state_dim > 32:
            stats["state_dim_gt32"] = stats.get("state_dim_gt32", 0) + 1
        # 动作越界防护（负值/超界会让 np.bincount 抛 ValueError、cross_entropy 抛 IndexError）
        valid = (a >= 0) & (a < NUM_ACTIONS)
        if not valid.all():
            stats["bad_action"] += int((~valid).sum())
            s, a, r = s[valid], a[valid], r[valid]
            if labels is not None:
                labels = [lab for lab, ok in zip(labels, valid) if ok]
        if len(s) == 0:
            skipped += 1
            continue
        if len(s) < min_steps:
            skipped += 1
            continue
        # P5.8 低信息量轨迹过滤：单动作占比 > max_same_ratio → 丢弃（如全 idle 的挂机段）
        if len(a) > 0:
            dominant = np.bincount(a).max() / len(a)
            if dominant > max_same_ratio:
                stats["low_info"] += 1
                skipped += 1
                continue
        # 标签重映射（v2 且 layout 可用时）
        if labels is not None and layout is not None:
            new_a = []
            keep = []
            for i, (act, lab) in enumerate(zip(a, labels)):
                if act < GENERIC:
                    new_a.append(act)
                    keep.append(i)
                elif lab is not None and lab in layout.get("skills", {}):
                    new_a.append(layout["skills"][lab])
                    keep.append(i)
                else:
                    stats["dropped_skill"] += 1
            if not keep:
                skipped += 1
                continue
            a = np.array(new_a, dtype=np.int64)
            s = s[keep]
            r = r[keep]
        else:
            # v1 或无 layout：只保留 generic 段（技能槽语义不可靠）
            mask = a < GENERIC
            if mask.sum() < min_steps:
                skipped += 1
                continue
            s, a, r = s[mask], a[mask], r[mask]
        # P5.8 奖励峰值裁剪（异常样本防护）
        r = np.clip(r, -reward_clip, reward_clip)
        stats["kept"] += len(a)
        trajs.append((s, a, r))
    print(f"  {len(files)} files, skipped {skipped}, stats={stats}")
    # P2-4：把 traj_io 的拒绝原因打出来——原实现只报 "skipped N"，格式事故
    # （未知版本头、截断文件、维度越界）会表现为样本量悄悄变少而无任何线索。
    if skipped:
        print(f"  [reject] {traj_io.reject_summary()}")
    if not trajs:
        raise FileNotFoundError(f"no usable trajectories in {data_dir}")
    return trajs


# ----------------------------------------------------------------------
# 优势估计（GAE with critic）
# ----------------------------------------------------------------------
def gae_returns(rewards, values, gamma=GAMMA, lam=LAMBDA):
    """对单条轨迹计算 GAE 优势（critic baseline）。"""
    T = len(rewards)
    adv = np.zeros(T, dtype=np.float32)
    last_gae = 0.0
    for t in reversed(range(T)):
        next_v = values[t + 1] if t + 1 < T else 0.0
        delta = rewards[t] + gamma * next_v - values[t]
        last_gae = delta + gamma * lam * last_gae
        adv[t] = last_gae
    return adv


def concat_with_returns(trajs, values=None):
    """逐轨迹计算优势后拼接（轨迹边界归零，无跨轨迹泄漏）。"""
    s_list, a_list, adv_list, ret_list = [], [], [], []
    for i, (s, a, r) in enumerate(trajs):
        v = values[i] if values is not None else np.zeros(len(r), dtype=np.float32)
        adv = gae_returns(r, v)
        ret = adv + v  # 回报估计 = 优势 + baseline
        s_list.append(s)
        a_list.append(a)
        adv_list.append(adv)
        ret_list.append(ret)
    return (np.concatenate(s_list), np.concatenate(a_list),
            np.concatenate(adv_list), np.concatenate(ret_list))


def pad_trajs(trajs, target=None):
    """维度归一化：历史轨迹 16/18/32 维混用（V12~V46 数据并存），统一补零到目标维度。
    target=None 时取本列表最大维度；train/eval 需传同一 target 保证网络输入一致。"""
    max_sd = target if target is not None else max(t[0].shape[1] for t in trajs)
    if all(t[0].shape[1] == max_sd for t in trajs):
        return trajs
    out = []
    for s, a, r in trajs:
        if s.shape[1] < max_sd:
            s = np.pad(s, ((0, 0), (0, max_sd - s.shape[1])), mode="constant")
        out.append((s, a, r))
    print(f"[main] padded trajectory states to dim {max_sd}")
    return out


# ----------------------------------------------------------------------
# 网络（Actor-Critic；导出只写 Actor 头，与 Java RlModel 兼容）
# ----------------------------------------------------------------------
class PolicyNet(nn.Module):
    def __init__(self, input_dim, num_actions=NUM_ACTIONS, hidden=HIDDEN):
        super().__init__()
        self.input_dim = input_dim
        self.fc = nn.Sequential(
            nn.Linear(input_dim, hidden), nn.ReLU(),
            nn.Linear(hidden, hidden), nn.ReLU(),
            nn.Linear(hidden, num_actions),
        )

    def forward(self, x):
        return self.fc(x)


class ValueNet(nn.Module):
    def __init__(self, input_dim, hidden=HIDDEN):
        super().__init__()
        self.fc = nn.Sequential(
            nn.Linear(input_dim, hidden), nn.ReLU(),
            nn.Linear(hidden, hidden), nn.ReLU(),
            nn.Linear(hidden, 1),
        )

    def forward(self, x):
        return self.fc(x).squeeze(-1)


# ----------------------------------------------------------------------
# 模型读写（Actor 头与 Java RlModel 一致，大端）
# ----------------------------------------------------------------------
def load_model_bin(path):
    """读取 Java 侧同格式的模型文件（大端）。

    P2-4 加固：原实现零校验——`--init` 指向被截断/错版的文件时，`f.read()` 短读会让
    np.frombuffer 抛 ValueError（或更糟：静默读出垃圾权重），而 Java 侧 `RlModel`
    对同一文件是**一边倒**的尺寸校验（只拒 actual < expected）。这里补齐与
    `RlModel.load` 对齐的检查，让"续训接错文件"在 Python 侧就响亮失败。
    """
    size = os.path.getsize(path)
    if size < 8:
        raise ValueError(f"model file too small: {path} ({size} B)")
    with open(path, "rb") as f:
        num_layers = struct.unpack(">i", f.read(4))[0]
        if num_layers < 1 or num_layers > 8:
            raise ValueError(f"invalid numLayers {num_layers} in {path}")
        sizes = struct.unpack(f">{num_layers + 1}i", f.read(4 * (num_layers + 1)))
        if any(s < 1 or s > 4096 for s in sizes):
            raise ValueError(f"invalid sizes {sizes} in {path}")
        expected = 4 + 4 * (num_layers + 1) + sum(
            4 * sizes[i] * sizes[i + 1] + 4 * sizes[i + 1] for i in range(num_layers))
        if size < expected:
            raise ValueError(f"truncated model: file={size} B, header expects {expected} B ({path})")
        if size > expected:
            # 允许尾部扩展（未来加 schemaVersion 尾巴时 Java 侧也能读），但要说出来
            print(f"[model] note: {os.path.basename(path)} has {size - expected} trailing byte(s) "
                  f"(ignored; reserved for future schema metadata)")
        weights, biases = [], []
        for i in range(num_layers):
            w = np.frombuffer(f.read(sizes[i] * sizes[i + 1] * 4), dtype=">f4") \
                .reshape(sizes[i + 1], sizes[i]).astype(np.float32)
            b = np.frombuffer(f.read(sizes[i + 1] * 4), dtype=">f4").astype(np.float32)
            weights.append(w)
            biases.append(b)
    return {"sizes": sizes, "weights": weights, "biases": biases}


def init_actor_from_bin(net, info, input_dim):
    sizes = info["sizes"]
    old_out = sizes[-1]
    if old_out > NUM_ACTIONS:
        raise ValueError(f"init model output dim {old_out} > {NUM_ACTIONS}")
    if len(sizes) != 4 or sizes[1] != HIDDEN or sizes[2] != HIDDEN:
        raise ValueError(f"init model architecture {sizes} unsupported")
    if sizes[0] > input_dim:
        raise ValueError(f"init model input dim {sizes[0]} > {input_dim}")
    for i in range(2):
        w = info["weights"][i].copy()
        b = info["biases"][i].copy()
        if i == 0 and w.shape[1] != input_dim:
            w = np.concatenate([w, np.zeros((w.shape[0], input_dim - w.shape[1]), dtype=np.float32)], axis=1)
        with torch.no_grad():
            net.fc[i * 2].weight.copy_(torch.from_numpy(w))
            net.fc[i * 2].bias.copy_(torch.from_numpy(b))
    old_w, old_b = info["weights"][2], info["biases"][2]
    with torch.no_grad():
        if old_out < NUM_ACTIONS:
            n_copy = min(old_out, GENERIC)  # generic 0-10 语义一致
            net.fc[4].weight[:n_copy].copy_(torch.from_numpy(old_w[:n_copy]))
            net.fc[4].bias[:n_copy].copy_(torch.from_numpy(old_b[:n_copy]))
            print(f"[train] output extended {old_out} -> {NUM_ACTIONS} (generic copied, skill slots random)")
        else:
            net.fc[4].weight.copy_(torch.from_numpy(old_w))
            net.fc[4].bias.copy_(torch.from_numpy(old_b))


# ----------------------------------------------------------------------
# 训练
# ----------------------------------------------------------------------
def train(states, actions, advs, returns, bc_epochs=15, awr_epochs=3, lr=1e-3,
          batch_size=256, seed=42, init_info=None, eval_data=None,
          kl_beta=0.01, ent_beta_bc=0.01, bc_extra=None, init_critic=None):
    """训练入口。

    P5.8 稳定性约束：
      - KL 正则（kl_beta）：BC/AWR 更新时对"训练起点策略快照"加反向 KL 惩罚，
        强制新策略不偏离旧策略太远（防策略剧变/崩溃）；
      - 熵正则：AWR 已有 0.05 熵项；BC 加 ent_beta_bc 小熵项防过早收敛。

    bc_extra（P0 修复 2026-09-10）：`--pretrain` / `--relabel` 提供的 BC 增强样本
    （states, actions, weights）。这些 npz 是窗口/片段拼接、没有轨迹边界，无法计算 GAE，
    因此**只参与 BC 阶段**（带样本权重，relabel 的 PER 权重即作用于此），不参与 AWR。
    """
    torch.manual_seed(seed)
    np.random.seed(seed)
    device = "cuda" if torch.cuda.is_available() else "cpu"
    print(f"[train] device={device}, samples={len(states)}, kl_beta={kl_beta}")

    in_dim = states.shape[1]
    actor = PolicyNet(in_dim).to(device)
    critic = ValueNet(in_dim).to(device)
    if init_info is not None:
        init_actor_from_bin(actor, init_info, in_dim)
        print(f"[train] actor initialized from previous model (input {init_info['sizes'][0]} -> {in_dim})")
    if init_critic:
        # P1 修复：critic 从上轮 sidecar 恢复（存在且结构匹配时），避免每轮价值函数冷启
        load_critic_sidecar(critic, init_critic)
    # P5.8 KL 参考策略：训练起点快照（init 模型或初始随机）——本轮回溯锚点
    actor_ref = PolicyNet(in_dim).to(device)
    actor_ref.load_state_dict(actor.state_dict())
    actor_ref.eval()
    for p in actor_ref.parameters():
        p.requires_grad_(False)

    st = torch.tensor(states, device=device)
    ac = torch.tensor(actions, device=device)
    adv = torch.tensor(advs, device=device)
    ret = torch.tensor(returns, device=device)
    # BC 增强样本（--pretrain / --relabel）：只进 BC 阶段，带样本权重
    bc_extra_t = None
    if bc_extra is not None:
        ex_s, ex_a, ex_w = bc_extra
        if len(ex_s) > 0:
            bc_extra_t = (torch.tensor(ex_s, device=device),
                          torch.tensor(ex_a, device=device),
                          torch.tensor(ex_w, device=device))
            print(f"[train] bc_extra: +{len(ex_s)} samples (weight mean={float(ex_w.mean()):.3f}, "
                  f"max={float(ex_w.max()):.2f}) → BC only (no GAE/AWR)")
    # 优势标准化（缓解稀疏奖励）
    adv = (adv - adv.mean()) / (adv.std() + 1e-8)

    a_opt = torch.optim.Adam(actor.parameters(), lr=lr)
    c_opt = torch.optim.Adam(critic.parameters(), lr=lr)
    n = len(st)

    # ---- 1. Critic 预训练：回归回报 ----
    print("[train] critic warmup...")
    for epoch in range(10):
        perm = torch.randperm(n, device=device)
        total = 0.0
        for i in range(0, n, batch_size):
            idx = perm[i:i + batch_size]
            v = critic(st[idx])
            loss = F.mse_loss(v, ret[idx])
            c_opt.zero_grad()
            loss.backward()
            c_opt.step()
            total += loss.item() * len(idx)
        if epoch % 5 == 0 or epoch == 9:
            print(f"  critic epoch {epoch}: mse={total / n:.4f}")

    # ---- 2. BC 预热：行为克隆（学习规则策略/旧数据分布）+ KL/熵约束 ----
    print("[train] BC warmup...")
    for epoch in range(bc_epochs):
        perm = torch.randperm(n, device=device)
        total = 0.0
        for i in range(0, n, batch_size):
            idx = perm[i:i + batch_size]
            logits = actor(st[idx])
            logp = F.log_softmax(logits, dim=1)
            loss = F.cross_entropy(logits, ac[idx])
            # P5.8 熵正则（BC 小项）+ 反向 KL 约束（对起点快照）
            entropy = -(logp.exp() * logp).sum(1).mean()
            with torch.no_grad():
                ref_logp = F.log_softmax(actor_ref(st[idx]), dim=1)
            kl = (logp.exp() * (logp - ref_logp)).sum(1).mean()
            loss = loss - ent_beta_bc * entropy + kl_beta * kl
            a_opt.zero_grad()
            loss.backward()
            a_opt.step()
            total += loss.item() * len(idx)
        # BC 增强样本（--pretrain / --relabel）：带权重的额外一轮（P0 修复：此前这两个参数
        # 只注册不读取，近战预训练与 PER 重标注数据从未参与训练）
        if bc_extra_t is not None:
            ex_s, ex_a, ex_w = bc_extra_t
            m = len(ex_s)
            perm_e = torch.randperm(m, device=device)
            for i in range(0, m, batch_size):
                idx = perm_e[i:i + batch_size]
                logits = actor(ex_s[idx])
                logp = F.log_softmax(logits, dim=1)
                ce = F.cross_entropy(logits, ex_a[idx], reduction="none")
                loss = (ce * ex_w[idx]).mean()
                entropy = -(logp.exp() * logp).sum(1).mean()
                with torch.no_grad():
                    ref_logp = F.log_softmax(actor_ref(ex_s[idx]), dim=1)
                kl = (logp.exp() * (logp - ref_logp)).sum(1).mean()
                loss = loss - ent_beta_bc * entropy + kl_beta * kl
                a_opt.zero_grad()
                loss.backward()
                a_opt.step()
        if epoch % 5 == 0 or epoch == bc_epochs - 1:
            acc = (actor(st).argmax(1) == ac).float().mean().item()
            print(f"  bc epoch {epoch}: loss={total / n:.4f} acc={acc:.3f}")

    # ---- 3. AWR：优势加权回归（离线数据上稳健的策略优化） ----
    # loss = -log π(a|s) × exp(adv / T)；熵正则保持多样性 + KL 约束防偏离起点
    print("[train] AWR...")
    for epoch in range(awr_epochs):
        perm = torch.randperm(n, device=device)
        total = 0.0
        for i in range(0, n, batch_size):
            idx = perm[i:i + batch_size]
            logits = actor(st[idx])
            logp = F.log_softmax(logits, dim=1)
            logp_a = logp.gather(1, ac[idx].unsqueeze(1)).squeeze(1)
            entropy = -(logp.exp() * logp).sum(1).mean()
            with torch.no_grad():
                ref_logp = F.log_softmax(actor_ref(st[idx]), dim=1)
            kl = (logp.exp() * (logp - ref_logp)).sum(1).mean()
            weight = torch.clamp(torch.exp(adv[idx] / AWR_TEMP), max=10.0)
            loss = -(logp_a * weight).mean() - 0.05 * entropy + kl_beta * kl
            a_opt.zero_grad()
            loss.backward()
            a_opt.step()
            total += loss.item() * len(idx)
        acc = (actor(st).argmax(1) == ac).float().mean().item()
        print(f"  awr epoch {epoch}: loss={total / n:.4f} acc={acc:.3f}")

    # ---- 验证集评估（部署门禁输入） ----
    metrics = {"acc": acc, "nll": float(total / n), "critic_restored": bool(init_critic),
               "semantics_epoch": SEMANTICS_EPOCH}
    if eval_data is not None:
        es, ea, _eadv, _eret = eval_data
        est = torch.tensor(es, device=device)
        eac = torch.tensor(ea, device=device)
        with torch.no_grad():
            logits = actor(est)
            eval_acc = (logits.argmax(1) == eac).float().mean().item()
            eval_nll = F.cross_entropy(logits, eac).item()
        # 2026-09-11 修复（P2-4）：原实现是 `metrics = {...}` 重新绑定 → 把
        # semantics_epoch / critic_restored 覆盖掉，而 main() 永远传 eval_data，
        # 于是这两个键**从未进入 metrics.json**，iterate.py 的"语义纪元变更 → 重建基线"
        # 分支（`new.get("semantics_epoch")` 恒为 None）永远不可能触发 —— 相当于该门禁
        # 是个摆设：语义变更（强制等待步排除、奖励重对齐）后仍拿旧 acc 基线比较，
        # 必然误杀。改为 update 合并。
        metrics.update({"acc": eval_acc, "nll": eval_nll,
                        "eval_acc": eval_acc, "eval_nll": eval_nll})
        print(f"[eval] held-out trajectories: acc={eval_acc:.3f} nll={eval_nll:.4f}")
    return actor, metrics, critic


# ----------------------------------------------------------------------
# 导出（只写 Actor 头，与 Java RlModel 兼容）+ 版本元信息
# ----------------------------------------------------------------------
def export(net, out_path, meta=None):
    layers = [net.input_dim, HIDDEN, HIDDEN, NUM_ACTIONS]
    with open(out_path, "wb") as f:
        f.write(struct.pack(">i", len(layers) - 1))
        f.write(struct.pack(f">{len(layers)}i", *layers))
        for i in range(len(layers) - 1):
            w = net.fc[i * 2].weight.detach().cpu().numpy()
            b = net.fc[i * 2].bias.detach().cpu().numpy()
            f.write(w.astype(">f4").tobytes())
            f.write(b.astype(">f4").tobytes())
    print(f"[export] model written: {out_path} ({os.path.getsize(out_path)} bytes)")
    if meta:
        meta_path = out_path + ".meta.txt"
        with open(meta_path, "w", encoding="utf-8") as f:
            for k, v in meta.items():
                f.write(f"{k}={v}\n")
        print(f"[export] meta written: {meta_path}")


def export_critic(critic, out_path):
    """把 critic 权重另存为 <model>.critic.npz（P1 修复 2026-09-10）。

    背景：`--init` 此前只恢复 Actor，critic/Adam 全部冷启 → 每轮 6h 迭代的价值函数都从零学，
    GAE 优势在前若干 epoch 系统性偏差。sidecar 不影响 Java 侧（RlModel 只读 actor 部分）。
    """
    path = out_path + ".critic.npz"
    try:
        sd = {k: v.detach().cpu().numpy() for k, v in critic.state_dict().items()}
        np.savez_compressed(path, **sd)
        print(f"[main] critic sidecar saved: {path}")
    except (OSError, ValueError) as e:
        print(f"[main] WARN: failed to save critic sidecar: {e}")


def load_critic_sidecar(critic, init_path):
    """尝试从 <init 模型>.critic.npz 恢复 critic；不可用时返回 False（冷启）。"""
    path = init_path + ".critic.npz"
    if not os.path.exists(path):
        print(f"[main] no critic sidecar at {path} → critic cold-start")
        return False
    try:
        with np.load(path, allow_pickle=False) as z:
            sd = {k: torch.tensor(z[k]) for k in z.files}
        critic.load_state_dict(sd)
    except (OSError, ValueError, KeyError, RuntimeError) as e:
        print(f"[main] critic sidecar unusable ({e}) → cold-start")
        return False
    print(f"[main] critic restored from {path}")
    return True


def load_layout(path):
    """layout.json：{generic: [...], skills: {slot: skill_id}}（/rl layout 解析产物）。"""
    if not path or not os.path.exists(path):
        return None
    with open(path, "r", encoding="utf-8") as f:
        return json.load(f)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--data", default=None)
    ap.add_argument("--layout", default=None, help="参考布局 json（/rl layout 解析产物，标签重映射用）")
    ap.add_argument("--zombie_dir", default=None)
    ap.add_argument("--mix_ratio", type=float, default=0.1)
    ap.add_argument("--pretrain", default=None)
    ap.add_argument("--relabel", default=None)
    ap.add_argument("--init", default=None)
    ap.add_argument("--eval", type=float, default=0.1, help="留出验证轨迹比例")
    ap.add_argument("--max-samples", type=int, default=0, help="截断样本数（0=全部；闭环验证限时用）")
    ap.add_argument("--bc-extra-ratio", type=float, default=0.25,
                    help="每个 BC 增强来源（--pretrain/--relabel）的样本上限 = 该比例 × 主数据样本数"
                         "（0=不限；2026-09-11 加入：960 万样本的近战预训练曾把 BC 梯度带偏导致门禁连续拒绝）")
    ap.add_argument("--out", default=None)
    ap.add_argument("--metrics", default=None, help="metrics.json 输出路径（部署门禁）")
    args = ap.parse_args()

    if args.data is None:
        args.data = os.path.join(os.path.expanduser("~"), "AppData", "Roaming", ".minecraft",
                                 "config", "eftlm_stylish", "trajectories")
    if args.out is None:
        args.out = os.path.join(os.path.dirname(args.data), "rl_model.bin")

    t0 = time.time()
    layout = load_layout(args.layout)
    print(f"[main] layout: {'loaded' if layout else 'NONE (v1 轨迹仅 generic 段可用)'}")

    trajs = collect_data(args.data, layout=layout)
    # 按轨迹留出验证集（轨迹隔离，防泄漏）
    n_eval = max(1, int(len(trajs) * args.eval))
    rng = np.random.RandomState(42)
    perm = rng.permutation(len(trajs))
    eval_trajs = [trajs[i] for i in perm[:n_eval]]
    train_trajs = [trajs[i] for i in perm[n_eval:]]
    print(f"[main] trajs={len(trajs)} train={len(train_trajs)} eval={len(eval_trajs)}")
    # 维度归一化（历史 16/18/32 维轨迹混用；train/eval 统一到同一维度保证网络输入一致）
    all_max = max(max(t[0].shape[1] for t in train_trajs),
                  max(t[0].shape[1] for t in eval_trajs) if eval_trajs else 0)
    train_trajs = pad_trajs(train_trajs, all_max)
    eval_trajs = pad_trajs(eval_trajs, all_max)

    states, actions, advs, returns = concat_with_returns(train_trajs)
    eval_data = concat_with_returns(eval_trajs) if eval_trajs else None

    # 僵尸数据混合（V1 保留；旧格式仅 generic 段）
    if args.zombie_dir and os.path.isdir(args.zombie_dir):
        ztrajs = collect_data(args.zombie_dir, layout=layout)
        ztrajs = pad_trajs(ztrajs)
        zs, za, zadv, zret = concat_with_returns(ztrajs)
        if zs.shape[1] != states.shape[1]:
            zs = np.pad(zs, ((0, 0), (0, states.shape[1] - zs.shape[1])), mode="constant")
        z_target = int(len(states) * args.mix_ratio / (1.0 - args.mix_ratio))
        if len(zs) > z_target:
            pick = np.random.choice(len(zs), size=z_target, replace=False)
            zs, za, zadv, zret = zs[pick], za[pick], zadv[pick], zret[pick]
        states = np.concatenate([states, zs])
        actions = np.concatenate([actions, za])
        advs = np.concatenate([advs, zadv])
        returns = np.concatenate([returns, zret])
        print(f"[main] zombie mixed: +{len(zs)}")

    if args.max_samples and len(states) > args.max_samples:
        pick = np.random.RandomState(0).choice(len(states), args.max_samples, replace=False)
        states, actions, advs, returns = states[pick], actions[pick], advs[pick], returns[pick]
        print(f"[main] samples truncated to {args.max_samples}")

    print(f"[main] DEBUG train shapes: states={states.shape} actions={actions.shape} "
          f"eval={None if eval_data is None else eval_data[0].shape}")

    init_info = None
    if args.init and os.path.exists(args.init):
        print(f"[main] loading init model: {args.init}")
        init_info = load_model_bin(args.init)

    # ---- BC 增强数据（P0 修复 2026-09-10）----
    # 此前 --pretrain / --relabel 只注册、从不读取：iterate.py 每轮生成 melee_pretrain_*.npz
    # 与 relabeled_*.npz 并传入，但训练侧完全忽略 → 近战预训练与 PER 重标注从未生效。
    # 现在：两者都作为 BC 阶段的带权样本参与（片段无轨迹边界，故不进 GAE/AWR）。
    #
    # 2026-09-11 修正（v79/v80 被门禁连续拒绝的根因）：
    #   melee_pretrain 实测 **960 万样本**，是本轮主数据（~55 万）的 ~17 倍 —— 无限制喂入会让
    #   BC 梯度被这份"近战窗口子集"主导（eval_acc 0.878 → 0.63、label_top1_ratio 升到 0.60/0.61
    #   > 门禁 0.55 → 两轮连续被拒、模型无法部署，每轮训练也从 ~15 分钟涨到 46~50 分钟）。
    #   现在每个来源各自限流到 main 样本数的 --bc-extra-ratio 倍（默认 0.25，合计 ≤50%）。
    bc_extra = None
    for tag, npz_path in (("pretrain", args.pretrain), ("relabel", args.relabel)):
        if not npz_path:
            continue
        loaded = load_npz_samples(npz_path, int(states.shape[1]), NUM_ACTIONS)
        if loaded is None:
            print(f"[main] WARN: --{tag} {npz_path} 不可用，已跳过（见上方 traj_io 日志）")
            continue
        ex_s, ex_a, ex_w = loaded
        raw_n = len(ex_s)
        cap = int(len(states) * args.bc_extra_ratio) if args.bc_extra_ratio > 0 else 0
        if cap > 0 and raw_n > cap:
            pick = np.random.RandomState(0).choice(raw_n, size=cap, replace=False)
            ex_s, ex_a, ex_w = ex_s[pick], ex_a[pick], ex_w[pick]
            print(f"[main] --{tag}: {raw_n} samples → subsampled to {cap} "
                  f"(bc_extra_ratio={args.bc_extra_ratio} x main={len(states)})")
        else:
            print(f"[main] --{tag}: {npz_path} → {raw_n} samples (dim={ex_s.shape[1]})")
        if bc_extra is None:
            bc_extra = (ex_s, ex_a, ex_w)
        else:
            bc_extra = (np.concatenate([bc_extra[0], ex_s]),
                        np.concatenate([bc_extra[1], ex_a]),
                        np.concatenate([bc_extra[2], ex_w]))
    if bc_extra is not None:
        print(f"[main] bc_extra total: {len(bc_extra[0])} samples "
              f"(ratio to main = {len(bc_extra[0]) / max(1, len(states)):.2f}, BC-only)")

    actor, metrics, critic = train(states, actions, advs, returns, init_info=init_info,
                                   eval_data=eval_data, bc_extra=bc_extra,
                                   init_critic=args.init if args.init else None)
    export_critic(critic, args.out)

    os.makedirs(os.path.dirname(args.out), exist_ok=True)
    export(actor, args.out, meta={
        "file": os.path.basename(args.out),
        "time": datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
        "state_dim": states.shape[1],
        "action_dim": NUM_ACTIONS,
        "samples": len(states),
        "layout": args.layout or "",
        "data": args.data,
        "zombie_dir": args.zombie_dir or "",
        "init": args.init or "",
        "pretrain": args.pretrain or "",
        "relabel": args.relabel or "",
        "bc_extra_samples": 0 if bc_extra is None else int(len(bc_extra[0])),
        "traj_version_current": CURRENT_VERSION,
        "eval_acc": f"{metrics['acc']:.3f}",
        "eval_nll": f"{metrics['nll']:.4f}",
        "elapsed_sec": f"{time.time() - t0:.1f}",
    })
    if args.metrics:
        # V52 教训：记录标签分布指纹（top1 动作占比）供门禁判断"分布变化"——
        # 标签修正/开局多样化会系统性改变 acc 口径，跨分布 acc 对比会误伤。
        top1 = 0.0
        if len(actions) > 0:
            cnt = np.bincount(actions)
            top1 = float(cnt.max()) / len(actions)
        with open(args.metrics, "w", encoding="utf-8") as f:
            # 2026-09-11 修复（P2-4）：这里才是 iterate.py 真正读到的那份 JSON
            # （429 行那份 dict 只是进程内中间量）。原实现只写 5 个键 →
            # semantics_epoch / critic_restored / 契约版本全部丢失，门禁的
            # "语义纪元变更重建基线"分支永远不触发。契约标记同时供
            # selftest_contract.py 与 report_round.py 做跨语言一致性核对。
            json.dump({"eval_acc": metrics["acc"], "eval_nll": metrics["nll"],
                       "samples": len(states), "label_top1_ratio": round(top1, 4),
                       "semantics_epoch": SEMANTICS_EPOCH,
                       "critic_restored": bool(metrics.get("critic_restored", False)),
                       "traj_version": CURRENT_VERSION,
                       "state_dim": int(states.shape[1]) if len(states) else 0,
                       "num_actions": NUM_ACTIONS,
                       "time": datetime.now().strftime("%Y-%m-%d %H:%M:%S")}, f, indent=2)
        print(f"[main] metrics written: {args.metrics}")


if __name__ == "__main__":
    main()
