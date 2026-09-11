# -*- coding: utf-8 -*-
"""对比 2x 与 8x 加速时段下的轨迹积累速度与数据质量。

用法: python train\\analyze_traj.py
时段:
  - 2x 桶: 2026-08-27 07:18:30 ~ 08:29:20 (服务器运行, AutoTicker 稳定 2.0x)
  - 8x 桶: 2026-08-27 08:51:00 ~ 现在 (稳定 8.0x; 08:33-08:50 为爬升期, 排除)
输出: 每桶积累速率(文件/小时, 步数/小时, MB/小时) + 抽样质量指标(长度分布,
      动作熵, 标签熵, reward 统计, 标签 top)。
"""
import os, struct, glob, datetime, random, statistics, collections, math, sys
import numpy as np
import train_ppo

if hasattr(sys.stdout, "reconfigure"):
    sys.stdout.reconfigure(encoding="utf-8")

TRAJ_DIR = r"E:\program\JAVA\touhou little maid - unknow sky area\prod_server\config\eftlm_stylish\trajectories"
T_2X0 = datetime.datetime(2026, 8, 27, 7, 18, 30)
T_2X1 = datetime.datetime(2026, 8, 27, 8, 29, 20)
T_8X0 = datetime.datetime(2026, 8, 27, 8, 51, 0)
NOW = datetime.datetime.now()

def epoch_ms(dt):
    return int(dt.timestamp() * 1000)

def head_n(path):
    try:
        with open(path, "rb") as f:
            b = f.read(16)
        if len(b) < 12:
            return None
        v = struct.unpack(">i", b[0:4])[0]
        if v in (1, 2):
            n = struct.unpack(">i", b[4:8])[0]
        else:
            n = v
        return n if 0 < n <= 100000 else None
    except OSError:
        return None

def entropy(counts):
    tot = float(sum(counts))
    if tot <= 0:
        return 0.0
    return -sum((c / tot) * math.log(c / tot) for c in counts if c > 0)

def summarize(tag, epochs, paths):
    if not paths:
        print(f"\n[{tag}] 无轨迹")
        return
    dt_h = (epochs[-1] - epochs[0]) / 1000.0 / 3600.0
    if dt_h <= 0:
        dt_h = 1e-6
    n_files = len(paths)
    total_bytes = sum(os.path.getsize(p) for p in paths)
    steps = [s for s in (head_n(p) for p in paths) if s is not None]
    total_steps = sum(steps)
    print(f"\n=== {tag} ===")
    print(f"  时间跨度: {datetime.datetime.fromtimestamp(epochs[0]/1000):%H:%M:%S} ~ {datetime.datetime.fromtimestamp(epochs[-1]/1000):%H:%M:%S}  ({dt_h:.2f} h)")
    print(f"  轨迹文件: {n_files}  总字节: {total_bytes/1e6:.1f} MB  总步数: {total_steps}")
    print(f"  积累速率: {n_files/dt_h:.0f} 文件/h | {total_steps/dt_h:.0f} 步/h | {total_bytes/1e6/dt_h:.1f} MB/h")
    if steps:
        print(f"  单轨迹步数: mean={statistics.mean(steps):.0f} median={statistics.median(steps):.0f} p90={sorted(steps)[int(len(steps)*0.9)-1]:.0f} max={max(steps)}")

    # ---- 质量抽样 ----
    rng = random.Random(42)
    sample = rng.sample(paths, min(300, len(paths)))
    lens, act_counts, lab_counts, rew_all = [], collections.Counter(), collections.Counter(), []
    lab_top = collections.Counter()
    for p in sample:
        r = train_ppo.load_bin(p)
        if r is None:
            continue
        states, actions, rewards, labels = r
        lens.append(len(actions))
        act_counts.update(actions.tolist())
        rew_all.extend(rewards.tolist())
        for lb in labels:
            if lb is not None:
                lab_counts[lb] += 1
    if lens:
        print(f"  [质量抽样 {len(sample)} 文件]")
        print(f"    长度: mean={statistics.mean(lens):.1f} median={statistics.median(lens):.0f} p90={sorted(lens)[int(len(lens)*0.9)-1]:.0f} min={min(lens)} max={max(lens)}")
        if act_counts:
            n_act = sum(act_counts.values())
            ent = entropy(list(act_counts.values()))
            print(f"    动作: 唯一动作数={len(act_counts)}/{64} 熵={ent:.3f} (max={math.log(64):.3f}) top5={act_counts.most_common(5)}")
        if lab_counts:
            n_lab = sum(lab_counts.values())
            lent = entropy(list(lab_counts.values()))
            print(f"    标签: 唯一标签数={len(lab_counts)} 熵={lent:.3f} top5={lab_counts.most_common(5)}")
        if rew_all:
            r = np.array(rew_all)
            print(f"    reward: mean={r.mean():.4f} std={r.std():.4f} min={r.min():.4f} max={r.max():.4f} 正样本率={(r>0).mean()*100:.1f}% 零样本率={(r==0).mean()*100:.1f}%")

def main():
    files = []
    for p in glob.glob(os.path.join(TRAJ_DIR, "traj_*.bin")):
        name = os.path.basename(p)
        try:
            epoch = int(name.rsplit("_", 1)[1].split(".")[0])
        except (ValueError, IndexError):
            continue
        files.append((epoch, p))
    files.sort()
    e0, e1 = epoch_ms(T_2X0), epoch_ms(T_2X1)
    e2 = epoch_ms(T_8X0)
    e_now = epoch_ms(NOW)
    two_x = [(e, p) for e, p in files if e0 <= e <= e1]
    eight_x = [(e, p) for e, p in files if e2 <= e <= e_now]
    print(f"总轨迹文件: {len(files)}")
    summarize("2.0x 时段 (07:18:30~08:29:20)", [e for e, _ in two_x], [p for _, p in two_x])
    summarize("8.0x 时段 (08:51:00~now)", [e for e, _ in eight_x], [p for _, p in eight_x])

if __name__ == "__main__":
    main()
