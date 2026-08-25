package org.eftlm.stylish.compat.efn.weapons;

import org.eftlm.stylish.compat.efn.EfnAnim;
import org.eftlm.stylish.compat.efn.EfnWeaponKit;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

import java.util.List;

/**
 * 新月（Crescent Moon）行为表：技能表 EFN_SKILLS.md §16
 * （crescent_moon / flag_bearer 及其强化变体共用）。
 * <p>
 * 普攻：AUTO1~3；空中 JC：AIRSLASH；
 * 大招：弯刃技能（falchion_skill，原版 1 层 + 冷却 600tick）。
 */
public final class CrescentMoon {

    public static CombatBehaviors.Builder<HumanoidMobPatch<?>> Instance;

    static {
        Instance = CombatBehaviors.<HumanoidMobPatch<?>>builder()
                .newBehaviorSeries(EfnWeaponKit.parry())
                .newBehaviorSeries(EfnWeaponKit.dodge())
                .newBehaviorSeries(EfnWeaponKit.airSlash(EfnAnim.byKey("biped/falchion/falchion_airslash")))
                .newBehaviorSeries(EfnWeaponKit.melee(List.of(
                        EfnAnim.byKey("biped/falchion/falchion_auto1"),
                        EfnAnim.byKey("biped/falchion/falchion_auto2"),
                        EfnAnim.byKey("biped/falchion/falchion_auto3"),
                        EfnAnim.byKey("biped/falchion/falchion_dash"))))
                .newBehaviorSeries(EfnWeaponKit.ultimate(
                        EfnAnim.byKey("biped/falchion/falchion_skill"), 60));
    }

    private CrescentMoon() {
    }
}
