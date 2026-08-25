#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
EFTLM-Stylish 训练迭代守护（V13）
==================================
循环（默认 6 小时/轮）：
  1. 等待采集时长（生产服务器后台持续采集轨迹）
  2. 归档 trajectories 快照（只取 60 秒前写完的文件，避免拷贝半写文件）
  3. 清洗：extract_melee.py（近战连招预训练 npz）+ crucible.py（高华丽回放）+ relabel.py（PER 重标注）
  4. 训练：train_ppo.py --init 上一版模型续训 + --pretrain 近战 npz + --relabel → train/models/rl_model_vN.bin
  5. 部署：备份生产旧模型 → 替换 config/eftlm_stylish/rl_model.bin → 写版本登记 → RCON `/rl reload` 热重载
  6. 记录 iterate.log，失败重试一次，继续下一轮

计时锚点：
  --anchor script（默认）：脚本启动时刻 + N×hours
  --anchor server：服务器启动时刻 + N×hours（服务器重启自动重新锚定、轮次归零）

用法:
    python train/iterate.py --server-dir <prod_server> --project-dir <EFTLM-example> \
        [--hours 6] [--anchor server] [--immediate] [--once]
"""
import argparse
import glob
import json
import os
import shutil
import socket
import struct
import subprocess
import sys
import time
from datetime import datetime

# ----------------------------------------------------------------------
# RCON 客户端（复用 rltrain/scripts/rcon_test.py 的实现）
# ----------------------------------------------------------------------
class RCON:
    def __init__(self, host, port, password, timeout=10):
        self.sock = socket.create_connection((host, port), timeout=timeout)
        self.send(3, password.encode())
        resp, _ = self.recv()
        if resp != 2:
            raise RuntimeError("RCON 认证失败")

    def send(self, ptype, payload):
        data = struct.pack("<ii", len(payload) + 10, 0) + struct.pack("<i", ptype) + payload + b"\x00\x00"
        self.sock.sendall(data)

    def recv(self):
        header = self._recv_exact(12)
        length, req_id, ptype = struct.unpack("<iii", header)
        body = self._recv_exact(length - 8)
        return ptype, body

    def _recv_exact(self, n):
        buf = b""
        while len(buf) < n:
            chunk = self.sock.recv(n - len(buf))
            if not chunk:
                raise RuntimeError("连接关闭")
            buf += chunk
        return buf

    def cmd(self, command):
        self.send(2, command.encode())
        _, body = self.recv()
        return body.decode("utf-8", "replace").strip()

    def close(self):
        try:
            self.sock.close()
        except Exception:
            pass


def log(msg):
    line = f"[{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}] {msg}"
    print(line, flush=True)


def rcon_cmd(cmd, host, port, password):
    try:
        r = RCON(host, port, password)
        out = r.cmd(cmd)
        r.close()
        return out
    except Exception as e:
        log(f"  [rcon] FAILED ({e})")
        return None


# ----------------------------------------------------------------------
# 服务器启动时刻检测（端口归属识别，防误伤其他 java 服务器，如 F 盘飞行模拟服）
# ----------------------------------------------------------------------
def _ps_out(cmd):
    try:
        r = subprocess.run(['powershell', '-NoProfile', '-Command', cmd], capture_output=True, timeout=30)
        # 字节级解码 + 容错：中文 Windows 的本地化输出可能非 UTF-8，避免解码异常
        return (r.stdout or b"").decode("utf-8", errors="replace")
    except Exception:
        return ""


def find_server_start():
    """返回生产服务器进程的启动时刻 epoch；服务器不在时返回 None。

    优先按监听端口归属（RCON 25575 → 游戏 25565）定位，再取该进程 CreationDate
    （CSV 序列化输出 ASCII 日期，避免本地化中文日期导致解析失败）。
    """
    for port in (25575, 25565):
        out = _ps_out(f"Get-NetTCPConnection -LocalPort {port} -State Listen "
                      "-ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess")
        pids = [int(x) for x in out.split() if x.isdigit()]
        if not pids:
            continue
        csv = _ps_out(f"Get-CimInstance Win32_Process -Filter \"ProcessId={pids[0]}\" "
                      "| Select-Object ProcessId, CreationDate | ConvertTo-Csv -NoTypeInformation")
        for line in csv.splitlines()[1:]:
            parts = line.split(",")
            if len(parts) < 2:
                continue
            created = parts[1].strip().strip('"')
            try:
                # CIM 原始格式 yyyyMMddHHmmss... / CSV 序列化格式 yyyy/M/d HH:mm:ss
                if len(created) >= 14 and created[4].isdigit() and created[8].isdigit():
                    return datetime.strptime(created[:14], "%Y%m%d%H%M%S").timestamp()
                return datetime.strptime(created[:19], "%Y/%m/%d %H:%M:%S").timestamp()
            except ValueError:
                continue
    return None


def load_anchor(state_file, server_start):
    """读取/初始化锚定状态：{start, rounds}。服务器重启（启动时刻变化）则重置轮次。"""
    st = None
    if os.path.exists(state_file):
        try:
            st = json.load(open(state_file, encoding="utf-8"))
        except Exception:
            st = None
    if st is None or server_start is None or abs(st.get("start", 0) - server_start) > 30:
        st = {"start": server_start if server_start else 0.0, "rounds": 0}
        if server_start:
            save_anchor(state_file, st)
    return st


def save_anchor(state_file, st):
    os.makedirs(os.path.dirname(state_file), exist_ok=True)
    with open(state_file, "w", encoding="utf-8") as f:
        json.dump(st, f, indent=2)


def wait_hours(hours, log_dir):
    deadline = time.time() + hours * 3600.0
    log(f"waiting {hours}h for data collection (deadline {datetime.fromtimestamp(deadline).strftime('%H:%M:%S')})")
    while time.time() < deadline:
        time.sleep(60)
        log(f"  collecting... remaining {max(0.0, deadline - time.time()) / 60.0:.0f} min")
    log("collection window complete")


def snapshot(traj_dir, session_dir, state_file, min_age_sec=60):
    """把"上一轮快照之后、且已写完（mtime 超过 min_age_sec）"的轨迹拷贝到 session 快照目录。

    state_file 记录上一轮快照的最大 mtime：只拷贝其后新产生的文件，避免每轮重复
    训练同一批旧数据（首轮部署前的初始快照由部署流程写入同一状态文件）。
    """
    files = sorted(glob.glob(os.path.join(traj_dir, "*.bin")))
    last = 0.0
    if os.path.exists(state_file):
        try:
            last = float(open(state_file, encoding="utf-8").read().strip())
        except Exception:
            pass
    os.makedirs(session_dir, exist_ok=True)
    copied = 0
    newest = last
    now = time.time()
    for f in files:
        m = os.path.getmtime(f)
        if now - m < min_age_sec or m <= last:
            continue
        shutil.copy2(f, os.path.join(session_dir, os.path.basename(f)))
        copied += 1
        newest = max(newest, m)
    if copied:
        with open(state_file, "w", encoding="utf-8") as fh:
            fh.write(str(newest))
    log(f"snapshot: {copied}/{len(files)} new trajectories -> {session_dir}")
    return copied


def run_step(script, args, train_dir, timeout=None):
    cmd = [sys.executable, script] + args
    log(f"run: {os.path.relpath(script, train_dir)} {' '.join(args)}")
    r = subprocess.run(cmd, cwd=train_dir, capture_output=True, text=True, timeout=timeout)
    tail = (r.stdout or "").strip().splitlines()[-12:]
    for line in tail:
        log(f"  | {line}")
    if r.returncode != 0:
        err_tail = (r.stderr or "").strip().splitlines()[-8:]
        for line in err_tail:
            log(f"  ! {line}")
    return r.returncode


def next_version(models_dir):
    ver_file = os.path.join(models_dir, "rl_model_version.txt")
    ver = 0
    if os.path.exists(ver_file):
        try:
            for line in open(ver_file, encoding="utf-8"):
                if line.startswith("version="):
                    ver = int(line.split("=", 1)[1].strip())
        except Exception:
            pass
    if ver <= 0:
        for f in glob.glob(os.path.join(models_dir, "rl_model_v*.bin")):
            base = os.path.basename(f)
            try:
                num = int(base[len("rl_model_v"):base.rindex("_")]) if "_" in base[len("rl_model_v"):] else \
                    int(base[len("rl_model_v"):base.rindex(".")])
                ver = max(ver, num)
            except ValueError:
                pass
    return ver + 1


def deploy_model(models_dir, config_dir, version, rcon_host, rcon_port, rcon_password):
    model_file = os.path.join(models_dir, f"rl_model_v{version}.bin")
    if not os.path.exists(model_file):
        log(f"deploy: model file missing {model_file}")
        return False
    target = os.path.join(config_dir, "rl_model.bin")
    # 备份生产旧模型
    if os.path.exists(target):
        ts = datetime.now().strftime("%Y%m%d_%H%M%S")
        backup = os.path.join(models_dir, f"rl_model_prev_deployed_{ts}.bin")
        shutil.copy2(target, backup)
        log(f"deploy: previous deployed model backed up -> {backup}")
    shutil.copy2(model_file, target)
    # 版本登记（models 目录 + 生产 config 目录各一份）
    ver_file = os.path.join(models_dir, "rl_model_version.txt")
    with open(ver_file, "w", encoding="utf-8") as f:
        f.write(f"version={version}\nfile=rl_model_v{version}.bin\ntime={datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n")
    with open(os.path.join(config_dir, "rl_model_version.txt"), "w", encoding="utf-8") as f:
        f.write(f"version={version}\nfile=rl_model_v{version}.bin\ntime={datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n")
    log(f"deploy: {model_file} -> {target} (version={version})")
    # RCON 热重载（服务器离线时跳过，启动时会自动加载）
    out = rcon_cmd("/rl reload", rcon_host, rcon_port, rcon_password)
    log(f"deploy: /rl reload -> {out if out is not None else 'server offline, will load on restart'}")
    return True


def iterate_once(args, models_dir, config_dir, ts):
    traj_dir = os.path.join(config_dir, "trajectories")
    session_dir = os.path.join(args.project_dir, "train", "backup", "data", f"session_{ts}")
    train_dir = os.path.join(args.project_dir, "train")
    snap_state = os.path.join(models_dir, "snapshot_state.txt")

    # 1. 轨迹快照（只取上一轮快照之后的新文件）
    if snapshot(traj_dir, session_dir, snap_state) == 0:
        log("iterate: no new trajectories, abort this round")
        return False

    # 2. 清洗
    #    近战预训练数据用全量轨迹（含旧 v13 轨迹，脚本自动按 actionDim 过滤 generic 动作）；
    #    主训练（crucible/relabel）只用本轮 session 快照（64 布局新轨迹）
    melee_npz = os.path.join(models_dir, f"melee_pretrain_{ts}.npz")
    fancy_npz = os.path.join(models_dir, f"pretrain_fancy_{ts}.npz")
    relabel_npz = os.path.join(models_dir, f"relabeled_{ts}.npz")
    ok = run_step(os.path.join(train_dir, "extract_melee.py"), ["--data", traj_dir, "--out", melee_npz], train_dir) == 0
    if ok and not os.path.exists(melee_npz):
        ok = False
    if ok:
        # 华丽片段预训练(fancy)为可选增强数据(暂未被训练使用): 失败仅警告, 不中止主流程
        if run_step(os.path.join(train_dir, "crucible.py"), ["--data", session_dir, "--out", fancy_npz], train_dir) != 0:
            log("iterate: crucible (fancy) step failed, continuing without fancy data")
    if ok:
        ok = run_step(os.path.join(train_dir, "relabel.py"), ["--data", session_dir, "--out", relabel_npz], train_dir) == 0
    if not ok:
        log("iterate: cleaning step failed")
        return False

    # 3. 版本与续训
    version = next_version(models_dir)
    prev = os.path.join(models_dir, f"rl_model_v{version - 1}.bin")
    # P3：参考布局拉取（/rl layout → layout.json，标签语义对齐依据）
    layout_file = os.path.join(models_dir, "layout.json")
    fetch_layout(args, layout_file)
    train_args = ["--data", session_dir, "--out", os.path.join(models_dir, f"rl_model_v{version}.bin")]
    if os.path.exists(layout_file):
        train_args += ["--layout", layout_file]
    metrics_file = os.path.join(models_dir, f"metrics_v{version}.json")
    train_args += ["--metrics", metrics_file]
    if args.zombie_dir and os.path.isdir(args.zombie_dir):
        train_args += ["--zombie_dir", args.zombie_dir, "--mix_ratio", str(args.mix_ratio)]
    if os.path.exists(prev):
        train_args += ["--init", prev]
    if os.path.exists(melee_npz):
        train_args += ["--pretrain", melee_npz]
    if os.path.exists(relabel_npz):
        train_args += ["--relabel", relabel_npz]
    if run_step(os.path.join(train_dir, "train_ppo.py"), train_args, train_dir, timeout=6 * 3600) != 0:
        log("iterate: training failed")
        return False

    # 3.5 P3 部署门禁：验证集 acc 相对上一版不得明显回退（首次通过）
    if not gate_pass(models_dir, metrics_file, version):
        log("iterate: gate rejected, keeping previous model")
        return False

    # 3.6 P3 教官 AI：薄弱点分析 → 课程配置（P4 AutoArena 读取）
    course_file = os.path.join(config_dir, "course.json")
    if run_step(os.path.join(train_dir, "instructor.py"),
                ["--data", session_dir, "--out", course_file], train_dir, timeout=600) == 0:
        log(f"iterate: instructor course config -> {course_file}")

    # 4. 部署 + 热重载
    ok = deploy_model(models_dir, config_dir, version, args.rcon_host, args.rcon_port, args.rcon_password)
    if ok and os.path.exists(metrics_file):
        # 记录本次门禁基线（下一轮对比）
        shutil.copy2(metrics_file, os.path.join(models_dir, "prev_metrics.json"))
    return ok


# ----------------------------------------------------------------------
# P3：参考布局拉取 / 部署门禁
# ----------------------------------------------------------------------

def fetch_layout(args, out_file):
    """RCON /rl layout → layout.json（{generic:[...], skills:{slot:skill_id}}）。

    解析失败仅警告（v1 轨迹退化为仅 generic 段训练）。
    """
    out = rcon_cmd("/rl layout", args.rcon_host, args.rcon_port, args.rcon_password)
    if not out:
        log("  [layout] RCON failed, training without layout (generic-only)")
        return
    generic = []
    skills = {}
    for line in out.splitlines():
        line = line.strip()
        if line.startswith("generic:"):
            for tok in line[len("generic:"):].split():
                if "=" in tok:
                    generic.append(tok.split("=", 1)[1])
        elif line.startswith("skills:"):
            for tok in line[len("skills:"):].split():
                if "=" in tok:
                    slot, sid = tok.split("=", 1)
                    try:
                        skills[str(int(slot))] = sid
                    except ValueError:
                        pass
    if not skills:
        log("  [layout] parsed empty skills, training generic-only")
        return
    with open(out_file, "w", encoding="utf-8") as f:
        json.dump({"generic": generic, "skills": skills}, f, ensure_ascii=False, indent=2)
    log(f"  [layout] saved {len(skills)} skill slots -> {out_file}")


def gate_pass(models_dir, metrics_file, version):
    """部署门禁：验证集 acc 相对上一版不得回退超过 0.02（无上一版基线时通过）。"""
    if not os.path.exists(metrics_file):
        log("  [gate] metrics missing, reject")
        return False
    try:
        with open(metrics_file, encoding="utf-8") as f:
            new = json.load(f)
    except Exception as e:
        log(f"  [gate] metrics parse failed ({e}), reject")
        return False
    prev_file = os.path.join(models_dir, "prev_metrics.json")
    if not os.path.exists(prev_file):
        log(f"  [gate] v{version} acc={new.get('eval_acc')} (first baseline, pass)")
        return True
    try:
        with open(prev_file, encoding="utf-8") as f:
            prev = json.load(f)
    except Exception as e:
        log(f"  [gate] prev metrics parse failed ({e}), pass")
        return True
    prev_acc = prev.get("eval_acc")
    new_acc = new.get("eval_acc")
    if prev_acc is None or new_acc is None:
        log("  [gate] missing eval_acc, pass")
        return True
    ok = new_acc >= prev_acc - 0.02
    log(f"  [gate] v{version} acc={new_acc:.3f} vs prev {prev_acc:.3f} -> {'PASS' if ok else 'REJECT'}")
    return ok


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--server-dir", required=True, help="生产服务器目录（prod_server）")
    ap.add_argument("--project-dir", required=True, help="项目目录（EFTLM-example）")
    ap.add_argument("--hours", type=float, default=6.0, help="每轮采集时长（小时）")
    ap.add_argument("--anchor", choices=["script", "server"], default="script",
                    help="计时锚点：script=脚本启动时刻（旧行为）；server=服务器启动时刻"
                         "（启动 + N×hours 触发迭代，服务器重启后自动重新锚定）")
    ap.add_argument("--immediate", action="store_true", help="启动即执行第一轮（否则先等待采集窗口）")
    ap.add_argument("--once", action="store_true", help="只跑一轮后退出")
    ap.add_argument("--zombie-dir", default=None, help="基础怪物轨迹目录（默认 <server>/config/eftlm_stylish/trajectories_zombie）")
    ap.add_argument("--mix-ratio", type=float, default=0.1)
    ap.add_argument("--rcon-host", default="127.0.0.1")
    ap.add_argument("--rcon-port", type=int, default=25575)
    ap.add_argument("--rcon-password", default="maidpilot123")
    args = ap.parse_args()

    config_dir = os.path.join(args.server_dir, "config", "eftlm_stylish")
    models_dir = os.path.join(args.project_dir, "train", "models")
    log_dir = os.path.join(args.project_dir, "train")
    os.makedirs(models_dir, exist_ok=True)
    if not args.zombie_dir:
        args.zombie_dir = os.path.join(config_dir, "trajectories_zombie")
    log(f"iterate: server={args.server_dir} hours={args.hours} zombie={args.zombie_dir}")
    log(f"iterate: models={models_dir} config={config_dir}")

    anchor_state_file = os.path.join(models_dir, "anchor_state.json")

    def run_round():
        ts = datetime.now().strftime("%Y%m%d_%H%M%S")
        ok = iterate_once(args, models_dir, config_dir, ts)
        if not ok:
            log("iterate: round failed, retry once after 10 min")
            time.sleep(600)
            ts = datetime.now().strftime("%Y%m%d_%H%M%S")
            ok = iterate_once(args, models_dir, config_dir, ts)
            if not ok:
                log("iterate: retry failed, skipping this round")
        return ok

    first = True
    while True:
        try:
            if args.anchor == "server":
                # 锚定服务器启动时刻：启动 + (rounds+1) × hours 触发迭代；
                # 服务器重启（启动时刻变化）自动重新锚定、轮次归零
                if first and args.immediate:
                    run_round()
                    st = load_anchor(anchor_state_file, find_server_start())
                    st["rounds"] += 1
                    save_anchor(anchor_state_file, st)
                    first = False
                st = None
                while True:
                    st = load_anchor(anchor_state_file, find_server_start())
                    if st["start"] <= 0:
                        log("  anchor: server not detected yet, retry in 60s")
                        time.sleep(60)
                        continue
                    target = st["start"] + (st["rounds"] + 1) * args.hours * 3600
                    if time.time() >= target:
                        break
                    log(f"  collecting... next iteration at {datetime.fromtimestamp(target).strftime('%Y-%m-%d %H:%M:%S')}"
                        f" (server start {datetime.fromtimestamp(st['start']).strftime('%m-%d %H:%M')}, round {st['rounds'] + 1})")
                    time.sleep(60)
                run_round()
                st["rounds"] += 1
                save_anchor(anchor_state_file, st)
                if args.once:
                    log("iterate: --once, exiting")
                    return 0
            else:
                if first and args.immediate:
                    first = False
                else:
                    wait_hours(args.hours, log_dir)
                run_round()
                if args.once:
                    log("iterate: --once, exiting")
                    return 0
        except KeyboardInterrupt:
            log("iterate: interrupted, exiting")
            return 0
        except Exception as e:
            log(f"iterate: round exception {e!r}, continue next round")
            time.sleep(300)


if __name__ == "__main__":
    sys.exit(main())
