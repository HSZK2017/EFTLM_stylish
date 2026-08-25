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

NUM_ACTIONS = 64
GENERIC = 11
HIDDEN = 64
GAMMA = 0.99
LAMBDA = 0.95
AWR_TEMP = 1.0  # AWR 温度：advantage 权重 exp(adv/T)


# ----------------------------------------------------------------------
# 轨迹解析（v1/v2 自适应，大端，与 RlDataRecorder 一致）
# ----------------------------------------------------------------------
def read_exact(f, nbytes):
    """读取恰好 nbytes 字节；EOF 提前（半写/截断文件）抛 EOFError。"""
    data = f.read(nbytes)
    if len(data) != nbytes:
        raise EOFError("truncated trajectory file")
    return data


def load_bin(path):
    """返回 (states, actions, rewards, labels) 或 None（损坏/不支持的版本）。"""
    try:
        with open(path, "rb") as f:
            first = struct.unpack(">i", read_exact(f, 4))[0]
            if first == 1 or first == 2:
                version = first
                n, sd, na = struct.unpack(">iii", read_exact(f, 12))
            else:
                # v1 旧格式：首个 int 是 numSteps（>=1 且远小于合理 version）
                version = 1
                n = first
                sd, na = struct.unpack(">ii", read_exact(f, 8))
            if n <= 0 or sd <= 0 or na <= 0 or n > 100000:
                return None
            labels = None
            if version >= 2:
                n_labels = struct.unpack(">i", read_exact(f, 4))[0]
                if n_labels < 0 or n_labels > 10000:
                    return None
                dict_list = []
                for _ in range(n_labels):
                    (ln,) = struct.unpack(">h", read_exact(f, 2))
                    if ln < 0 or ln > 256:
                        return None
                    dict_list.append(read_exact(f, ln).decode("utf-8", "replace"))
            states = np.frombuffer(read_exact(f, n * sd * 4), dtype=">f4").reshape(n, sd).astype(np.float32)
            actions = np.frombuffer(read_exact(f, n * 4), dtype=">i4").astype(np.int64)
            rewards = np.frombuffer(read_exact(f, n * 4), dtype=">f4").astype(np.float32)
            if version >= 2:
                idx = np.frombuffer(read_exact(f, n * 2), dtype=">i2").astype(np.int64)
                labels = [dict_list[i] if 0 <= i < len(dict_list) else None for i in idx]
        return states, actions, rewards, labels
    except (EOFError, OSError, ValueError, struct.error):
        return None


def collect_data(data_dir, min_steps=30, layout=None, stat=None):
    """读取轨迹目录，返回 (states, actions, rewards) 列表（逐轨迹隔离）。

    layout: {generic: [labels], skills: {slot: skill_id}, defense: {...}} 或 None。
    标签重映射规则：
      - generic 标签（idle/swordmaster_atk/...）→ 0..10 固定索引；
      - 技能 id 标签 → layout.skills 中的槽位（参考布局）；
      - 无法映射 / 无标签的样本：动作 > GENERIC 时丢弃（技能槽语义不可靠）。
    """
    files = sorted(glob.glob(os.path.join(data_dir, "*.bin")))
    if not files:
        raise FileNotFoundError(f"no trajectory files in {data_dir}")
    trajs = []
    skipped = 0
    stats = {"v1": 0, "v2": 0, "dropped_skill": 0, "kept": 0}
    for f in files:
        parsed = load_bin(f)
        if parsed is None:
            skipped += 1
            continue
        s, a, r, labels = parsed
        stats["v2" if labels is not None else "v1"] += 1
        if len(s) < min_steps:
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
        stats["kept"] += len(a)
        trajs.append((s, a, r))
    print(f"  {len(files)} files, skipped {skipped}, stats={stats}")
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
    with open(path, "rb") as f:
        num_layers = struct.unpack(">i", f.read(4))[0]
        sizes = struct.unpack(f">{num_layers + 1}i", f.read(4 * (num_layers + 1)))
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
          batch_size=256, seed=42, init_info=None, eval_data=None):
    torch.manual_seed(seed)
    np.random.seed(seed)
    device = "cuda" if torch.cuda.is_available() else "cpu"
    print(f"[train] device={device}, samples={len(states)}")

    in_dim = states.shape[1]
    actor = PolicyNet(in_dim).to(device)
    critic = ValueNet(in_dim).to(device)
    if init_info is not None:
        init_actor_from_bin(actor, init_info, in_dim)
        print(f"[train] actor initialized from previous model (input {init_info['sizes'][0]} -> {in_dim})")

    st = torch.tensor(states, device=device)
    ac = torch.tensor(actions, device=device)
    adv = torch.tensor(advs, device=device)
    ret = torch.tensor(returns, device=device)
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

    # ---- 2. BC 预热：行为克隆（学习规则策略/旧数据分布） ----
    print("[train] BC warmup...")
    for epoch in range(bc_epochs):
        perm = torch.randperm(n, device=device)
        total = 0.0
        for i in range(0, n, batch_size):
            idx = perm[i:i + batch_size]
            logits = actor(st[idx])
            loss = F.cross_entropy(logits, ac[idx])
            a_opt.zero_grad()
            loss.backward()
            a_opt.step()
            total += loss.item() * len(idx)
        if epoch % 5 == 0 or epoch == bc_epochs - 1:
            acc = (actor(st).argmax(1) == ac).float().mean().item()
            print(f"  bc epoch {epoch}: loss={total / n:.4f} acc={acc:.3f}")

    # ---- 3. AWR：优势加权回归（离线数据上稳健的策略优化） ----
    # loss = -log π(a|s) × exp(adv / T)；熵正则保持多样性
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
            weight = torch.clamp(torch.exp(adv[idx] / AWR_TEMP), max=10.0)
            loss = -(logp_a * weight).mean() - 0.05 * entropy
            a_opt.zero_grad()
            loss.backward()
            a_opt.step()
            total += loss.item() * len(idx)
        acc = (actor(st).argmax(1) == ac).float().mean().item()
        print(f"  awr epoch {epoch}: loss={total / n:.4f} acc={acc:.3f}")

    # ---- 验证集评估（部署门禁输入） ----
    metrics = {"acc": acc, "nll": float(total / n)}
    if eval_data is not None:
        es, ea, _eadv, _eret = eval_data
        est = torch.tensor(es, device=device)
        eac = torch.tensor(ea, device=device)
        with torch.no_grad():
            logits = actor(est)
            eval_acc = (logits.argmax(1) == eac).float().mean().item()
            eval_nll = F.cross_entropy(logits, eac).item()
        metrics = {"acc": eval_acc, "nll": eval_nll}
        print(f"[eval] held-out trajectories: acc={eval_acc:.3f} nll={eval_nll:.4f}")
    return actor, metrics


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

    actor, metrics = train(states, actions, advs, returns, init_info=init_info, eval_data=eval_data)

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
        "eval_acc": f"{metrics['acc']:.3f}",
        "eval_nll": f"{metrics['nll']:.4f}",
        "elapsed_sec": f"{time.time() - t0:.1f}",
    })
    if args.metrics:
        with open(args.metrics, "w", encoding="utf-8") as f:
            json.dump({"eval_acc": metrics["acc"], "eval_nll": metrics["nll"],
                       "samples": len(states), "time": datetime.now().strftime("%Y-%m-%d %H:%M:%S")}, f, indent=2)
        print(f"[main] metrics written: {args.metrics}")


if __name__ == "__main__":
    main()
