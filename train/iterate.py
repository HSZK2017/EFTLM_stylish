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
import threading
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


# ----------------------------------------------------------------------
# dsh-inject：训练迭代完成自动唤起 agent（外部程序向 Harness 会话注入提示词）
# ----------------------------------------------------------------------
def _inject_once(prompt, inject_script, session_id):
    cmd = [sys.executable, inject_script, prompt, "--autolunch"]
    if session_id:
        cmd += ["--session", session_id]
    try:
        r = subprocess.run(cmd, capture_output=True, text=True, timeout=30,
                           encoding="utf-8", errors="replace")
        if r.returncode == 0:
            return True, r.stdout.strip()[:120]
        return False, r.stderr.strip()[:200]
    except Exception as e:
        return False, repr(e)


def notify_agent(prompt, inject_script, session_id):
    """调用 dsh-inject 把提示词注入 Harness 会话并自动发送（autolunch）。

    失败不阻断训练流程；后台线程按对数（指数）间隔 1/2/4/8/16 分钟重试，
    最多 5 次后放弃（V54 追加：发送失败自动重试，避免通知丢失）。
    inject_script 为空串时禁用。"""
    if not inject_script:
        return
    ok, msg = _inject_once(prompt, inject_script, session_id)
    if ok:
        log(f"notify: agent injected ({msg})")
        return
    log(f"notify: agent inject failed ({msg}), retry scheduled (1/2/4/8/16 min, max 5)")

    def _retry_loop():
        interval = 60
        for i in range(1, 6):
            time.sleep(interval)
            ok2, msg2 = _inject_once(prompt, inject_script, session_id)
            if ok2:
                log(f"notify: agent inject retry OK (try #{i})")
                return
            log(f"notify: agent inject retry #{i} failed ({msg2})")
            interval *= 2
        log("notify: agent inject retry exhausted (5 tries), dropping")

    threading.Thread(target=_retry_loop, daemon=True).start()


def build_iteration_notice(ok, models_dir, config_dir, ts):
    """构造迭代完成通知：成功=指标摘要（含 instructor 弱点）；失败=原因提示。"""
    if not ok:
        return (f"[训练流水线自动通知] 第 {ts} 轮迭代失败/未产生新模型\n"
                f"可能原因：无新轨迹 / 清洗失败 / 训练失败 / 部署门禁拒绝（详见 iterate.log）\n"
                f"请检查训练流水线状态与服务器采集情况。")
    version = 0
    metrics_file = None
    for m in glob.glob(os.path.join(models_dir, "metrics_v*.json")):
        try:
            v = int(os.path.basename(m).split("_v")[1].split(".")[0])
        except (ValueError, IndexError):
            continue
        if v > version:
            version, metrics_file = v, m
    acc = nll = samples = "?"
    if metrics_file and os.path.exists(metrics_file):
        try:
            with open(metrics_file, encoding="utf-8") as f:
                md = json.load(f)
            acc = f"{md['eval_acc']:.4f}" if isinstance(md.get("eval_acc"), (int, float)) else md.get("eval_acc", "?")
            nll = f"{md['eval_nll']:.4f}" if isinstance(md.get("eval_nll"), (int, float)) else md.get("eval_nll", "?")
            samples = md.get("samples", "?")
        except Exception:
            pass
    # 2026-09-11 修复：通知必须带上 course.json 的生成时刻，并对"陈旧课程"显式告警。
    # 原实现只取 weakness 字段 → instructor 崩了两天期间，通知一直汇报 09-09 的陈旧弱点
    # （rejected_high/kill_low），而真实指标早已是 balanced，读通知的人无法察觉。
    weakness = "n/a"
    course = os.path.join(config_dir, "course.json")
    course_stamp = "unknown"
    if os.path.exists(course):
        try:
            with open(course, encoding="utf-8") as f:
                cd = json.load(f)
            weakness = ",".join(cd.get("weakness", [])) or "n/a"
            course_stamp = str(cd.get("generated_at", "unknown"))
        except Exception:
            pass
    stale_note = ""
    try:
        round_time = datetime.strptime(ts, "%Y%m%d_%H%M%S")
        course_time = datetime.strptime(course_stamp, "%Y-%m-%d %H:%M:%S")
        if (round_time - course_time).total_seconds() > 6 * 3600:
            stale_note = (f"  [STALE: 课程生成于 {course_stamp}，早于本轮 6h 以上 —— "
                          f"该弱点不是本轮数据算出来的，勿据此调课程]")
    except Exception:
        stale_note = "  [课程时间戳无法解析，弱点的时效性未知]"
    return (f"[训练流水线自动通知] 第 {ts} 轮迭代完成并部署（v{version if version else '?'}）\n"
            f"- eval_acc={acc} eval_nll={nll} samples={samples}\n"
            f"- instructor 弱点: {weakness}（课程生成于 {course_stamp}）{stale_note}\n"
            f"请进行强化学习检查：核对指标趋势（对比 metrics_v*.json 历史）、课程调整是否合理、是否需干预。")


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
    """把"上一轮快照之后、且已写完（mtime 超过 min_age_sec）"的轨迹拷进 session 快照目录。

    返回 (copied, newest_mtime)。**不再直接写 state_file** —— 见 commit_snapshot 的说明：
    水位线必须等本轮真正产出并部署了模型之后再推进。
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
    log(f"snapshot: {copied}/{len(files)} new trajectories -> {session_dir}")
    return copied, newest


def commit_snapshot(state_file, newest, ok):
    """只有本轮**成功产出并部署**模型时才推进快照水位线（2026-09-11 修复）。

    原实现（`snapshot()` 内直接写）有个静默数据损失：水位线在**拷贝时**就推进了，
    而拷贝发生在训练**之前** —— 所以一轮失败（清洗/训练报错、门禁拒绝、崩溃）之后，
    那批轨迹既进了 session 目录（没被训练），又因为水位线已过而**永远不会被再拷贝**。
    实测代价（2026-09-11）：19:04 轮训练失败 → `session_20260911_190449`(157) 与
    `session_20260911_191501`(180) 共 337 条轨迹从未进入任何训练集；同一天另有 186 条
    被误隔离 —— 合计约 16 万步采集数据被烧掉。

    现在：失败时保留水位线 → 下一轮会连同新数据一起重新快照（数据不丢，只是换个
    session 目录），代价仅是多占一点磁盘（由 train/retention.py 兜底）。
    """
    if ok and newest > 0:
        try:
            with open(state_file, "w", encoding="utf-8") as fh:
                fh.write(str(newest))
            log(f"snapshot: watermark committed -> {newest:.0f}")
        except OSError as e:
            log(f"snapshot: watermark write FAILED ({e}); next round will re-snapshot this data")
    elif newest > 0:
        log("snapshot: watermark NOT advanced (round produced no model) "
            "-> this batch will be re-snapshotted next round")


def run_step(script, args, train_dir, timeout=None):
    cmd = [sys.executable, script] + args
    log(f"run: {os.path.relpath(script, train_dir)} {' '.join(args)}")
    # P1 修复（2026-09-10）：显式指定编码。子脚本把 stdout 设为 UTF-8（见 main 的
    # sys.stdout.reconfigure），而 subprocess 默认用系统 ANSI(GBK) 解码 →
    # 中文 Windows 上抛 UnicodeDecodeError，异常逃过返回值判断、被外层宽 except 吞掉，
    # 表现为"本轮迭代静默跳过且子进程变孤儿"。
    r = subprocess.run(cmd, cwd=train_dir, capture_output=True, text=True,
                       encoding="utf-8", errors="replace", timeout=timeout)
    tail = (r.stdout or "").strip().splitlines()[-12:]
    for line in tail:
        log(f"  | {line}")
    if r.returncode != 0:
        err_tail = (r.stderr or "").strip().splitlines()[-8:]
        for line in err_tail:
            log(f"  ! {line}")
    return r.returncode


def next_version(models_dir):
    """下一版本号 = 已存在的 rl_model_v*.bin 最大版本 + 1（扫描法）。

    V52 教训：不要依赖 rl_model_version.txt（门禁拒绝时不更新 → 版本不递增 →
    下轮覆盖失败轮的文件）。扫描法保证每次训练产物文件名唯一。
    """
    ver = 0
    for f in glob.glob(os.path.join(models_dir, "rl_model_v*.bin")):
        base = os.path.basename(f)
        try:
            num = int(base[len("rl_model_v"):base.rindex(".")])
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
    # 2026-09-11：登记"真正部署过"的版本，供自我博弈对手池筛选（见 maintained pool 注释）
    try:
        dv = os.path.join(models_dir, "deployed_versions.txt")
        known = set()
        if os.path.exists(dv):
            known = {ln.strip() for ln in open(dv, encoding="utf-8") if ln.strip()}
        known.add(str(version))
        with open(dv, "w", encoding="utf-8") as fh:
            fh.write("\n".join(sorted(known, key=lambda x: int(x) if x.isdigit() else 0)) + "\n")
    except Exception as e:
        log(f"deploy: deployed_versions.txt update failed: {e!r}")
    with open(os.path.join(config_dir, "rl_model_version.txt"), "w", encoding="utf-8") as f:
        f.write(f"version={version}\nfile=rl_model_v{version}.bin\ntime={datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n")
    log(f"deploy: {model_file} -> {target} (version={version})")
    # RCON 热重载（服务器离线时跳过，启动时会自动加载）
    out = rcon_cmd("/rl reload", rcon_host, rcon_port, rcon_password)
    log(f"deploy: /rl reload -> {out if out is not None else 'server offline, will load on restart'}")
    return True


def iterate_once(args, models_dir, config_dir, ts, reuse_session=None):
    traj_dir = os.path.join(config_dir, "trajectories")
    session_dir = os.path.join(args.project_dir, "train", "backup", "data", f"session_{ts}")
    if reuse_session and os.path.isdir(reuse_session):
        # V51 教训：重试复用首轮 session（快照仅 10 分钟增量会样本过少）
        shutil.copytree(reuse_session, session_dir, dirs_exist_ok=True)
        log(f"iterate: retry reusing previous session data ({session_dir})")
    train_dir = os.path.join(args.project_dir, "train")
    snap_state = os.path.join(models_dir, "snapshot_state.txt")

    # 0. 契约预检（P1 修复 2026-09-10）：把"Java/Python 契约漂移"挡在 6 小时训练之前。
    #    selftest_contract = 技能目录唯一性 + Java↔Python 常量 + 模型二进制长度公式；
    #    selftest_p0      = 轨迹奖励对齐 + npz 载入。两者都不联网、不改文件。
    if not getattr(args, "skip_selftest", False):
        for name in ("selftest_contract.py", "selftest_p0.py"):
            rc = run_step(os.path.join(train_dir, name), [], train_dir, timeout=300)
            if rc != 0:
                log(f"iterate: preflight {name} FAILED (contract drift?) — abort this round")
                return False

    # 1. 轨迹快照（只取上一轮快照之后的新文件；水位线在**本轮成功部署后**才推进）
    copied, snap_newest = snapshot(traj_dir, session_dir, snap_state)
    if copied == 0:
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
        commit_snapshot(snap_state, snap_newest, False)
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
        commit_snapshot(snap_state, snap_newest, False)
        return False

    # 3.5 P3 部署门禁：验证集 acc 相对上一版不得明显回退（首次通过）
    if not gate_pass(models_dir, metrics_file, version):
        log("iterate: gate rejected, keeping previous model")
        return False

    # 3.6 P3 教官 AI：薄弱点分析 → 课程配置（P4 AutoArena 读取）
    #     2026-09-11 修复：教员失败原来**完全静默**（只在成功时记一行）——instructor.py
    #     因 load_bin 4 元组解包错误崩了两天，course.json 冻结在 09-09 的"96% 拒绝率时代"，
    #     而每轮完成通知仍读这份陈旧文件汇报"弱点"，看起来一切正常。非致命步骤也必须留痕。
    course_file = os.path.join(config_dir, "course.json")
    if run_step(os.path.join(train_dir, "instructor.py"),
                ["--data", session_dir, "--out", course_file], train_dir, timeout=600) == 0:
        log(f"iterate: instructor course config -> {course_file}")
    else:
        stale = ""
        if os.path.exists(course_file):
            try:
                _c = json.load(open(course_file, encoding="utf-8"))
                stale = f" (course.json still from {_c.get('generated_at', 'unknown')})"
            except Exception:
                stale = " (course.json unreadable)"
        log(f"WARN: instructor FAILED — course config NOT updated{stale}; "
            f"weakness advice in the round notice may be stale")
    # 课程保持开关（2026-09-11）：存在 <config>/course_hold.txt 时，教员跑完后把
    # selfplay 强制回 false —— 用于"课程判定达标、但我想先稳住当前语义纪元再进自博弈"
    # 的场景。教员每轮都会重写 course.json，所以人工改文件按不住；必须放在流程里。
    # 解除：删除 course_hold.txt 即可（下一轮自动放开）。
    hold_file = os.path.join(config_dir, "course_hold.txt")
    if os.path.exists(hold_file):
        try:
            _cd = {}
            if os.path.exists(course_file):
                _cd = json.load(open(course_file, encoding="utf-8"))
            if _cd.get("selfplay"):
                _cd["selfplay"] = False
                _cd["_held"] = ("selfplay forced false by course_hold.txt "
                                f"({datetime.now().strftime('%Y-%m-%d %H:%M:%S')}); "
                                "delete the file to release")
                with open(course_file, "w", encoding="utf-8") as fh:
                    json.dump(_cd, fh, ensure_ascii=False, indent=2)
                log("iterate: course HOLD active — instructor wanted selfplay=true, "
                    "forced back to false (delete course_hold.txt to release)")
            else:
                log("iterate: course HOLD active (selfplay already false)")
        except Exception as e:
            log(f"WARN: course hold failed: {e!r}")

    # 4. 部署 + 热重载
    ok = deploy_model(models_dir, config_dir, version, args.rcon_host, args.rcon_port, args.rcon_password)
    # 快照水位线：只有真正部署了模型才算"这批数据被消费"（失败/被拒 → 下轮重新快照，数据不丢）
    commit_snapshot(snap_state, snap_newest, ok)
    if ok and os.path.exists(metrics_file):
        # 记录本次门禁基线（下一轮对比）
        shutil.copy2(metrics_file, os.path.join(models_dir, "prev_metrics.json"))
    # P5.8 selfplay 历史策略池维护：把历史版本模型拷入 config/model_pool/（保留最近 3 版）
    # 2026-09-11 修复：候选版本原来按"版本号区间 version-3..version-1"取，**不看该版本是否
    # 真的被部署过** → 池里混进了被门禁拒绝的退化模型（实测 v79/v80：label_top1 0.61/0.597，
    # 单动作坍缩）。课程一旦判定 balanced 就自动转入自我博弈（selfplay_opponent=history），
    # 那时训练对手就是这些退化策略 → 教出坏习惯。现在只从 deployed_versions.txt 里取。
    try:
        pool_dir = os.path.join(config_dir, "model_pool")
        os.makedirs(pool_dir, exist_ok=True)
        dv_file = os.path.join(models_dir, "deployed_versions.txt")
        deployed = []
        if os.path.exists(dv_file):
            for ln in open(dv_file, encoding="utf-8"):
                ln = ln.strip()
                if ln.isdigit() and int(ln) != version:
                    deployed.append(int(ln))
        deployed.sort()
        candidates = deployed[-3:]  # 最近 3 个"已部署"版本（不含刚部署的这个）
        if not candidates:
            log("iterate: WARN model_pool: no deployed history recorded "
                "(deployed_versions.txt missing/empty) — pool left empty rather than "
                "seeding rejected models")
        for v in candidates:
            src = os.path.join(models_dir, f"rl_model_v{v}.bin")
            if os.path.exists(src):
                dst = os.path.join(pool_dir, f"rl_model_v{v}.bin")
                if not os.path.exists(dst) or os.path.getsize(dst) != os.path.getsize(src):
                    shutil.copy2(src, dst)
        keep = {f"rl_model_v{v}.bin" for v in candidates}
        removed = []
        for f in os.listdir(pool_dir):
            if f.endswith(".bin") and f not in keep:
                os.remove(os.path.join(pool_dir, f))
                removed.append(f)
        if removed:
            log(f"iterate: model_pool removed non-deployed/stale: {removed}")
        log(f"iterate: model_pool maintained (latest 3 versions: {sorted(os.listdir(pool_dir))})")
    except Exception as e:
        log(f"iterate: model_pool maintenance failed: {e!r}")
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
    """部署门禁：验证集 acc 相对上一版不得回退超过 0.02（无上一版基线时通过）。

    V52 教训：标签修正/开局多样化会系统性改变标签分布 → acc 口径不可跨分布比较
    （v50 时代 65% 单动作 → acc 0.94；新分布 4 动作分散 → acc ~0.86 是正常水平）。
    因此当本轮与上一版的标签分布指纹（label_top1_ratio）差异超过阈值时，
    门禁判定为"分布切换"，放行并记录（后续轮次以新分布基线比较）。
    """
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
    # V68 教训（2026-08-31）：连续拒绝后的下一轮快照可能只有 10 分钟增量（2.1 万样本）——
    # 小样本训练 acc 虚高（0.9043/nll 0.5315 假象，V51 同型坑）且门禁误放行部署。
    # 样本量下限：少于 10 万步的轮次拒绝（正常 6h 采集 50 万+；下限远低于正常值）。
    # 2026-09-11：下限从 10 万下调到 6 万。原因是本模组两处变化把"每轮样本量"系统性压低了：
    #   ① 2.5 小时轮次（宿舍 23:15 停服约束，原为 6 小时）；
    #   ② 强制等待步不再入轨迹（动作窗口关闭时的 idle 不是策略选择），实测样本率 ~37k/小时。
    # V68 立这条下限是为了拒绝"10 分钟重试轮"（约 2 万样本、acc 虚高）——6 万仍能拦住它。
    # 2026-09-11 第二次校准（10 万 → 6 万 → 3 万）：本轮（16:33→19:15，2.7h）session 实测只有
    # 180 条轨迹 / 50,336 步（清洗后 kept=45,278）→ **~18.6k 步/小时**，2.5h 轮次约 46k；
    # 6 万下限会让每一轮都被拒（训练白跑，日志只显示 tiny sample）。3 万 ≈ 1.6 小时采集量，
    # 仍能把"10 分钟重试轮"（约 3k 步）拦在门外（10 倍余量）。改此值必须同时核对实测采集率。
    if new.get("samples", 0) < 30000:
        log(f"  [gate] v{version} REJECT: tiny sample (samples={new.get('samples')} < 30000, "
            f"likely short-window retry round), keeping previous model")
        return False
    # V56/V61 教训（2026-08-29/30）：分布豁免曾放行退化模型——v56 top1=0.82（cycle_melee
    # 82.5%）、v61 top1=0.62（idle 集中）→ 行为退化（不攻击/熵降至 2.15）。退化硬上限：
    # top1 > 0.55 视为标签单一化，无论 acc 提升与分布切换一律拒绝（v50 时代 65% 单动作
    # acc 0.94 假象的同型问题；v55 top1=0.58、v61 top1=0.62 均在 0.55~0.65 区间漏放，
    # 故阈值从 0.65 收紧至 0.55；拒绝后保持上一版，训练数据随行为恢复后自然回落）。
    new_top1 = new.get("label_top1_ratio")
    if new_top1 is not None and new_top1 > 0.55:
        log(f"  [gate] v{version} REJECT: label degenerate (top1={new_top1:.2f} > 0.55, "
            f"single-action collapse), keeping previous model")
        return False
    # 语义纪元检测（2026-09-11）：轨迹"记录语义"变化时（如强制等待步被排除、奖励重对齐），
    # 标签分布与 acc 口径都会整体平移，用旧基线比 acc 必然误杀。此时建立新基线并放行一次。
    new_epoch = new.get("semantics_epoch")
    prev_epoch = prev.get("semantics_epoch")
    if new_epoch is not None and new_epoch != prev_epoch:
        log(f"  [gate] v{version} SEMANTICS EPOCH {prev_epoch} -> {new_epoch}: "
            f"acc={new_acc:.3f} accepted as NEW BASELINE (distribution not comparable to previous)")
        return True
    # 分布切换检测：标签 top1 占比变化 > 0.15 → 放行（acc 口径不可比）
    prev_top1 = prev.get("label_top1_ratio")
    if prev_top1 is not None and new_top1 is not None:
        if abs(prev_top1 - new_top1) > 0.15:
            log(f"  [gate] v{version} acc={new_acc:.3f} vs prev {prev_acc:.3f} but LABEL DISTRIBUTION SHIFT "
                f"(top1 {prev_top1:.2f} -> {new_top1:.2f}), gate waived, adopt new baseline")
            return True
    ok = new_acc >= prev_acc - 0.02
    log(f"  [gate] v{version} acc={new_acc:.3f} vs prev {prev_acc:.3f} -> {'PASS' if ok else 'REJECT'}")
    return ok


def main():
    # V63 教训（2026-08-31）：Windows GBK 控制台无法编码 dsh-inject 返回的
    # 非 GBK 字符（如 '\u05e2'）→ print 抛 UnicodeEncodeError → iterate 崩溃退出
    # （01:49 事故：训练流水线停摆 16h）。stdout/stderr 统一 UTF-8 + replace 容错。
    try:
        sys.stdout.reconfigure(encoding="utf-8", errors="replace")
        sys.stderr.reconfigure(encoding="utf-8", errors="replace")
    except Exception:
        pass
    ap = argparse.ArgumentParser()
    ap.add_argument("--server-dir", required=True, help="生产服务器目录（prod_server）")
    ap.add_argument("--project-dir", required=True, help="项目目录（EFTLM-example）")
    ap.add_argument("--hours", type=float, default=6.0, help="每轮采集时长（小时）")
    ap.add_argument("--anchor", choices=["script", "server"], default="script",
                    help="计时锚点：script=脚本启动时刻（旧行为）；server=服务器启动时刻"
                         "（启动 + N×hours 触发迭代，服务器重启后自动重新锚定）")
    ap.add_argument("--immediate", action="store_true", help="启动即执行第一轮（否则先等待采集窗口）")
    ap.add_argument("--skip-selftest", action="store_true",
                    help="跳过每轮训练前的契约预检（selftest_contract.py / selftest_p0.py）")
    ap.add_argument("--once", action="store_true", help="只跑一轮后退出")
    ap.add_argument("--zombie-dir", default=None, help="基础怪物轨迹目录（默认 <server>/config/eftlm_stylish/trajectories_zombie）")
    ap.add_argument("--mix-ratio", type=float, default=0.1)
    ap.add_argument("--rcon-host", default="127.0.0.1")
    ap.add_argument("--rcon-port", type=int, default=25575)
    ap.add_argument("--rcon-password", default=None,
                    help="RCON 密码（默认：环境变量 EFTLM_RCON_PASSWORD → tools/rcon_password.txt；不再硬编码）")
    ap.add_argument("--inject-script", default=r"F:\Deepseek Harness\dsh-easy-use\dsh-inject\dsh-inject.py",
                    help="dsh-inject 脚本路径（每轮迭代完成自动唤起 agent；空串=禁用）")
    ap.add_argument("--agent-session", default="session-013a71d2-5c4f-4f4e-b126-0d447596db21",
                    help="目标会话 ID（空=注入当前打开的 Harness 会话）")
    args = ap.parse_args()

    # RCON 凭据（P0 修复 2026-09-10）：不再硬编码密码。
    # 解析顺序：CLI > 环境变量 EFTLM_RCON_PASSWORD > tools/rcon_password.txt；缺失即退出（fail closed）。
    sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "tools"))
    from rcon_password import require as _require_rcon_password
    args.rcon_password = _require_rcon_password(
        args.rcon_password, os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "tools"))

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
            # V51 教训：重试快照只有 10 分钟增量（样本过少会污染 metrics/门禁对比）。
            # 重试复用第一轮 session 数据（若存在），保证样本量与首轮一致。
            ts2 = datetime.now().strftime("%Y%m%d_%H%M%S")
            session1 = os.path.join(args.project_dir, "train", "backup", "data", f"session_{ts}")
            ok2 = iterate_once(args, models_dir, config_dir, ts2, reuse_session=session1 if os.path.isdir(session1) else None)
            if not ok2:
                log("iterate: retry failed, skipping this round")
            else:
                log("iterate: retry succeeded")
            ok = ok2  # P1 修复：返回/通知都用最终结果（旧实现重试成功后仍上报失败）
        notify_agent(build_iteration_notice(ok, models_dir, config_dir, ts),
                     args.inject_script, args.agent_session)
        return ok

    first = True
    while True:
        try:
            if args.anchor == "server":
                # 锚定服务器启动时刻：启动 + (rounds+1) × hours 触发迭代；
                # 服务器重启（启动时刻变化）自动重新锚定、轮次归零
                if first and args.immediate:
                    # P1 修复（2026-09-10）：旧实现在这里 run_round()，随后等待循环结束又会
                    # run_round() → `--immediate --once` 实际执行两轮完整迭代（双份快照/清洗/训练/部署）。
                    # 现在：立即执行首轮并推进 anchor，然后 --once 直接退出；非 --once 进入等待循环。
                    log("iterate: --immediate, running first round now")
                    first = False
                    run_round()
                    st = load_anchor(anchor_state_file, find_server_start())
                    st["rounds"] += 1
                    save_anchor(anchor_state_file, st)
                    if args.once:
                        log("iterate: --once, exiting")
                        return 0
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
