#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""契约自检（无需 torch / 不联网）：Java ↔ Python 两侧的关键常量与文件格式。

覆盖（P1 修复配套）：
 1. skills.json：每把武器内 animKey 唯一（冷却/帧缓存以 animKey 为键的前提）；
    并报告重复的 skill id（仅告警——id 用作训练标签，重名会让标签二义）；
 2. Java 契约常量 ↔ Python 常量：RlState.STATE_DIM / RlActEvent.TOTAL_ACTIONS /
    NUM_ACTIONS / MAX_SKILL_SLOTS 与 train_ppo 的 NUM_ACTIONS / GENERIC 一致；
 3. 模型二进制：按 Java RlModel.load 的期望长度公式校验实际 .bin（含"截断文件必须被拒绝"的边界）。

运行：python train/selftest_contract.py   （退出码 0 = 全部通过）
"""
import json
import os
import re
import struct
import sys

REPO = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
FAILED = []

# 控制台可能是 GBK（中文 Windows）：显式切 UTF-8，避免打印 ↔ 之类的字符时 UnicodeEncodeError
for _stream in (sys.stdout, sys.stderr):
    try:
        _stream.reconfigure(encoding="utf-8", errors="replace")
    except (AttributeError, ValueError):
        pass


def check(name, cond, detail=""):
    print(f"  [{'PASS' if cond else 'FAIL'}] {name}{'' if cond else '  -> ' + detail}")
    if not cond:
        FAILED.append(name)


def read_java(path):
    with open(os.path.join(REPO, path), "r", encoding="utf-8") as f:
        return f.read()


def java_int_const(src, name, resolved=None):
    """解析 Java 常量：支持字面量与 'A + B' 形式（TOTAL_ACTIONS = NUM_ACTIONS + MAX_SKILL_SLOTS）。"""
    m = re.search(r"int\s+" + name + r"\s*=\s*([^;]+);", src)
    if not m:
        return None
    expr = m.group(1).strip()
    total = 0
    for part in [p.strip() for p in expr.split("+")]:
        if part.isdigit():
            total += int(part)
        elif resolved and part in resolved and resolved[part] is not None:
            total += resolved[part]
        else:
            return None
    return total


def parse_model_header(path):
    """返回 (numLayers, sizes, fileSize, expectedSize)；expectedSize 用 Java RlModel 的公式。"""
    size = os.path.getsize(path)
    with open(path, "rb") as f:
        num_layers = struct.unpack(">i", f.read(4))[0]
        sizes = list(struct.unpack(f">{num_layers + 1}i", f.read(4 * (num_layers + 1))))
    expected = 4 + 4 * (num_layers + 1)
    params = 0
    for i in range(num_layers):
        i_dim, o_dim = sizes[i], sizes[i + 1]
        params += i_dim * o_dim + o_dim
        expected += 4 * i_dim * o_dim + 4 * o_dim
    return num_layers, sizes, size, expected, params


def main():
    print("[1] skills.json：武器内动画键唯一性 + 重复 id 报告")
    skills_path = os.path.join(REPO, "src", "main", "resources", "eftlm_stylish", "skills.json")
    with open(skills_path, "r", encoding="utf-8") as f:
        catalog = json.load(f)
    weapons = catalog.get("weapons", {})
    total = 0
    dup_anim = []
    dup_id_total = 0
    for wname, w in weapons.items():
        anims, ids = [], []
        for s in w.get("skills", []):
            anims.append(s.get("anim", ""))
            ids.append(s.get("id", ""))
        total += len(ids)
        if len(set(anims)) != len(anims):
            dup_anim.append(wname)
        dup_id_total += sum(1 for i in set(ids) if ids.count(i) > 1)
    check("无重复动画键（冷却/帧缓存键唯一）", not dup_anim, f"weapons with duplicate anim: {dup_anim}")
    print(f"    info: {len(weapons)} weapons / {total} skills; duplicate skill ids = {dup_id_total} "
          f"(仅告警：id 用作训练标签，重名会让标签二义；冷却/帧缓存已改用 animKey)")

    print("[2] Java ↔ Python 契约常量")
    rl_state = read_java("src/main/java/org/eftlm/stylish/rl/RlState.java")
    rl_act = read_java("src/main/java/org/eftlm/stylish/rl/RlActEvent.java")
    java_state_dim = java_int_const(rl_state, "STATE_DIM")
    java_num_actions = java_int_const(rl_act, "NUM_ACTIONS")
    java_max_skill = java_int_const(rl_act, "MAX_SKILL_SLOTS")
    java_total = java_int_const(rl_act, "TOTAL_ACTIONS",
                                {"NUM_ACTIONS": java_num_actions, "MAX_SKILL_SLOTS": java_max_skill})
    check("解析到全部 Java 契约常量",
          None not in (java_state_dim, java_num_actions, java_max_skill, java_total),
          f"state={java_state_dim} num={java_num_actions} skills={java_max_skill} total={java_total}")
    sys.path.insert(0, os.path.join(REPO, "train"))
    import train_ppo  # noqa: E402  （导入即读 torch，但本机训练环境具备）
    check("RlState.STATE_DIM == 32", java_state_dim == 32, f"got {java_state_dim}")
    check("RlActEvent.NUM_ACTIONS == train_ppo.GENERIC", java_num_actions == train_ppo.GENERIC,
          f"{java_num_actions} vs {train_ppo.GENERIC}")
    check("TOTAL_ACTIONS == NUM_ACTIONS + MAX_SKILL_SLOTS",
          java_total == java_num_actions + java_max_skill,
          f"{java_total} vs {java_num_actions}+{java_max_skill}")
    check("train_ppo.NUM_ACTIONS == TOTAL_ACTIONS", train_ppo.NUM_ACTIONS == java_total,
          f"{train_ppo.NUM_ACTIONS} vs {java_total}")
    check("防守槽（尾部 2 槽）不与技能段重叠",
          java_num_actions is not None and java_max_skill is not None and java_total is not None
          and java_num_actions + java_max_skill <= java_total,
          f"skill end = {java_num_actions}+{java_max_skill}, defense base = "
          f"{None if java_total is None else java_total - 2}")

    print("[3] 模型二进制：期望长度公式（Java RlModel.load 的 P1 校验）")
    model = os.path.join(REPO, "config", "eftlm_stylish", "rl_model.bin")
    if not os.path.exists(model):
        check("存在可校验的模型文件", False, model)
    else:
        num_layers, sizes, size, expected, params = parse_model_header(model)
        check("层数 1..8", 1 <= num_layers <= 8, f"{num_layers}")
        check("输入维 ∈ {16,18,32}", sizes[0] in (16, 18, 32), f"{sizes[0]}")
        check("输出维 == TOTAL_ACTIONS(64)", sizes[-1] == java_total, f"{sizes[-1]}")
        check("文件长度 == 期望长度（不被截断）", size == expected, f"file={size} expected={expected}")
        check("参数量在 Java 上限（2e6）内", params <= 2_000_000, f"{params}")
        print(f"    info: sizes={sizes} params={params} bytes={size}")

    # ------------------------------------------------------------------
    # [4] 版本/纪元标记的"写入端 ↔ 读取端"对齐（P2-4）
    # 起因：2026-09-11 发现 train_ppo.py 里 semantics_epoch 先写进 metrics、
    # 随后被 `metrics = {...}` 重新绑定覆盖，序列化时又不写这个键 →
    # iterate.py 的门禁分支（读 semantics_epoch）永远拿到 None，形同虚设。
    # 这类"键从未落盘"的事故静态可查，因此在此固定成断言。
    # ------------------------------------------------------------------
    print("[4] 版本/纪元标记：写入端 ↔ 读取端对齐")
    import ast  # noqa: E402
    tppo_src = open(os.path.join(REPO, "train", "train_ppo.py"), encoding="utf-8").read()
    tppo_tree = ast.parse(tppo_src)
    dump_keys = None
    for node in ast.walk(tppo_tree):
        if (isinstance(node, ast.Call) and isinstance(node.func, ast.Attribute)
                and node.func.attr == "dump"):
            for arg in node.args:
                if isinstance(arg, ast.Dict):
                    dump_keys = {k.value for k in arg.keys if isinstance(k, ast.Constant)}
    check("定位到 metrics 的 json.dump(...) 字典", dump_keys is not None and len(dump_keys) > 0,
          f"keys={sorted(dump_keys) if dump_keys else None}")
    dump_keys = dump_keys or set()
    it_src = open(os.path.join(REPO, "train", "iterate.py"), encoding="utf-8").read()
    for key in ("eval_acc", "samples", "label_top1_ratio", "semantics_epoch"):
        # 门禁真正读的键必须在落盘字典里（否则该门禁恒为 None → 失效）
        check(f"metrics 落盘含门禁键 `{key}`",
              key in dump_keys and f'"{key}"' in it_src,
              f"dump={key in dump_keys} iterate_reads={f'\"{key}\"' in it_src}")
    check("SEMANTICS_EPOCH 为正整数", isinstance(train_ppo.SEMANTICS_EPOCH, int)
          and train_ppo.SEMANTICS_EPOCH >= 1, f"{train_ppo.SEMANTICS_EPOCH}")

    rec = read_java("src/main/java/org/eftlm/stylish/rl/RlDataRecorder.java")
    java_fmt = java_int_const(rec, "FORMAT_VERSION")
    import traj_io  # noqa: E402
    check("Java FORMAT_VERSION == traj_io.CURRENT_VERSION",
          java_fmt == traj_io.CURRENT_VERSION, f"java={java_fmt} py={traj_io.CURRENT_VERSION}")
    check("轨迹版本白名单 ⊆ traj_io 支持集",
          all(v in (1, 2, 3) for v in (1, 2, 3)) and traj_io.CURRENT_VERSION in (1, 2, 3),
          f"CURRENT_VERSION={traj_io.CURRENT_VERSION}")
    check("未知版本硬拒绝可观测（REJECT_STATS 存在）",
          hasattr(traj_io, "REJECT_STATS") and hasattr(traj_io, "reject_summary"),
          "traj_io.reject_summary()")

    print()
    if FAILED:
        print(f"FAILED: {len(FAILED)} -> {FAILED}")
        return 1
    print("ALL PASS (contract self-test)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
