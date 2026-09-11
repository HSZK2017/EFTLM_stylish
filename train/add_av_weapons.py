# -*- coding: utf-8 -*-
"""向 skills.json 添加 AV 武器条目（子代理 B 提取的 EpicFight 动画 id）。"""
import json, io, sys

path = r"E:\program\JAVA\EFTLM-example\src\main\resources\data\eftlm_stylish\skills.json"
import os
# 定位实际路径
for root, dirs, files in os.walk(r"E:\program\JAVA\EFTLM-example\src\main\resources"):
    for f in files:
        if f == "skills.json":
            path = os.path.join(root, f)
            break
print("skills.json at:", path)

with io.open(path, "r", encoding="utf-8") as f:
    data = json.load(f)

weapons = data["weapons"]

new_entries = {
    "av_thunder_diamond_blade": {
        "items": ["annoyingvillagers:thunder_diamond_blade"],
        "skills": [
            {"id": "av_tdb_combo1", "anim": "annoyingvillagers:biped/epicfight_awaken/dp_auto_1", "cd": 10, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_tdb_combo2", "anim": "annoyingvillagers:biped/epicfight_awaken/dp_auto_2", "cd": 10, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_tdb_combo3", "anim": "annoyingvillagers:biped/epicfight_awaken/dp_auto_3", "cd": 10, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_tdb_combo4", "anim": "annoyingvillagers:biped/epicfight_awaken/dp_auto_4", "cd": 10, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_tdb_dash", "anim": "annoyingvillagers:biped/epicfight_awaken/dp_dash", "cd": 40, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_tdb_nightfall", "anim": "annoyingvillagers:biped/epicfight_awaken/dp_night_fall", "cd": 100, "cost": 2, "cond": "melee", "resource": "own"},
            {"id": "av_tdb_onehand_combo", "anim": "annoyingvillagers:biped/epicfight_awaken/cut_left_dp_auto_3", "cd": 10, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_tdb_dual_combo", "anim": "annoyingvillagers:biped/pugilist_steve/dual_sword_auto2", "cd": 10, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_tdb_herrscher", "anim": "wom:biped/combat/herrscher_auto_1", "cd": 60, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_tdb_sweeping_edge", "anim": "annoyingvillagers:biped/epicfight_clone/thunder_sweeping_edge", "cd": 120, "cost": 2, "cond": "melee", "resource": "own"},
            {"id": "av_tdb_dancing_edge", "anim": "annoyingvillagers:biped/epicfight_clone/thunder_dancing_edge", "cd": 160, "cost": 2, "cond": "melee", "resource": "own"},
        ],
    },
    "av_ender_glaive": {
        "items": ["annoyingvillagers:ender_glaive"],
        "skills": [
            {"id": "av_ender_glaive_combo1", "anim": "annoyingvillagers:biped/wom_clone/ender_glaive_napoleon_auto_1", "cd": 10, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_ender_glaive_combo2", "anim": "annoyingvillagers:biped/wom_clone/ender_glaive_napoleon_auto_2", "cd": 10, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_ender_glaive_heavy", "anim": "annoyingvillagers:biped/wom_clone/ender_glaive_napoleon_austerlitz", "cd": 40, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_ender_glaive_dash", "anim": "annoyingvillagers:biped/wom_clone/ender_glaive_agony_auto_1", "cd": 60, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_ender_glaive_blast", "anim": "annoyingvillagers:biped/wom_clone/ender_glaive_napoleon_shoot_3", "cd": 120, "cost": 2, "cond": "mid_range", "resource": "own"},
            {"id": "av_ender_glaive_guard", "anim": "annoyingvillagers:biped/wom_clone/glowing_agony_guard", "cd": 5, "cost": 0, "cond": "melee", "resource": "own"},
        ],
    },
    "av_shadow_obsidian_sword": {
        "items": ["annoyingvillagers:shadow_obsidian_sword"],
        "skills": [
            {"id": "av_sos_combo1", "anim": "annoyingvillagers:biped/epicfight_dual_greatsword/shadow_obsidian_sword_greatsword_twohand_auto_1", "cd": 10, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_sos_combo2", "anim": "annoyingvillagers:biped/epicfight_dual_greatsword/shadow_obsidian_sword_greatsword_twohand_auto_2", "cd": 10, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_sos_heavy", "anim": "annoyingvillagers:biped/pugilist_steve/sword_heavy_auto1", "cd": 30, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_sos_slam", "anim": "annoyingvillagers:biped/wom_clone/shadow_obsidian_sword_torment_airslam", "cd": 40, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_sos_berserk", "anim": "annoyingvillagers:biped/wom_clone/shadow_obsidian_sword_torment_berserk_dash", "cd": 60, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_sos_dual_combo", "anim": "annoyingvillagers:biped/wom_clone/shadow_obsidian_sword_gezets_auto_2", "cd": 10, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_sos_dual_earthquake", "anim": "annoyingvillagers:biped/epicfight_dual_greatsword/shadow_obsidian_sword_greatsword_dual_earthquake", "cd": 90, "cost": 2, "cond": "melee", "resource": "own"},
        ],
    },
    "av_shadow_obsidian_weapon": {
        "items": ["annoyingvillagers:shadow_obsidian_weapon"],
        "skills": [
            {"id": "av_sow_combo1", "anim": "annoyingvillagers:biped/epicfight_clone/obsidian_fist_auto1", "cd": 8, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_sow_combo2", "anim": "annoyingvillagers:biped/epicfight_clone/obsidian_fist_auto2", "cd": 8, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_sow_combo3", "anim": "annoyingvillagers:biped/epicfight_clone/obsidian_fist_auto3", "cd": 8, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_sow_landing", "anim": "annoyingvillagers:biped/epicfight_clone/obsidian_landing", "cd": 40, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_sow_punch", "anim": "annoyingvillagers:biped/wom_clone/obsidian_strong_punch", "cd": 30, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_sow_tishnaw", "anim": "annoyingvillagers:biped/wom_clone/obsidian_enderblaster_twohand_tishnaw", "cd": 50, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_sow_wall", "anim": "annoyingvillagers:biped/epicfight_clone/obsidian_zombie_attack3", "cd": 70, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_sow_deathfall", "anim": "annoyingvillagers:biped/wom_clone/obsidian_antitheus_ascended_deathfall", "cd": 100, "cost": 2, "cond": "melee", "resource": "own"},
        ],
    },
    "av_shadow_obsidian_pillar": {
        "items": ["annoyingvillagers:shadow_obsidian_pillar"],
        "skills": [
            {"id": "av_sop_combo1", "anim": "annoyingvillagers:biped/epicfight_clone/obsidian_fist_auto1", "cd": 8, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_sop_combo2", "anim": "annoyingvillagers:biped/epicfight_clone/obsidian_fist_auto2", "cd": 8, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_sop_combo3", "anim": "annoyingvillagers:biped/epicfight_clone/obsidian_fist_auto3", "cd": 8, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_sop_landing", "anim": "annoyingvillagers:biped/epicfight_clone/obsidian_landing", "cd": 40, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_sop_ochs_combo", "anim": "annoyingvillagers:biped/epicfight_clone/shadow_obsidian_fist_auto1", "cd": 8, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_sop_ochs_rapid", "anim": "annoyingvillagers:biped/epicfight_infernal_gainer/obsidian_infernal_auto_1", "cd": 20, "cost": 1, "cond": "melee", "resource": "own"},
            {"id": "av_sop_pillar_quake", "anim": "annoyingvillagers:biped/epicfight_dual_greatsword/shadow_obsidian_sword_greatsword_dual_earthquake_pillar", "cd": 90, "cost": 2, "cond": "melee", "resource": "own"},
        ],
    },
}

added = []
for k, v in new_entries.items():
    if k in weapons:
        print(f"SKIP (exists): {k}")
        continue
    weapons[k] = v
    added.append(k)

data["weapons"] = weapons
with io.open(path, "w", encoding="utf-8") as f:
    json.dump(data, f, ensure_ascii=False, separators=(",", ":"))
print("added:", added)
print("total weapons:", len(weapons))
