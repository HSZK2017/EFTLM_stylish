package org.eftlm.stylish.compat.efn.weapons;

import org.eftlm.stylish.compat.efn.EfnAnim;
import org.eftlm.stylish.compat.efn.EfnWeaponKit;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

import java.util.List;

/**
 * 圣剑（Exsiliumgladius）行为表：技能表 EFN_SKILLS.md §8。
 * <p>
 * 普攻：A 系 4 段（单手持握时沿用 EpicFight 剑类原版行为）；
 * 空中 JC：AIRATK；大招：ABBB 重斩（2.0×，双持形态的派生终结技）。
 */
public final class Exsiliumgladius {

    public static CombatBehaviors.Builder<HumanoidMobPatch<?>> Instance;

    static {
        Instance = CombatBehaviors.<HumanoidMobPatch<?>>builder()
                .newBehaviorSeries(EfnWeaponKit.parry())
                .newBehaviorSeries(EfnWeaponKit.dodge())
                .newBehaviorSeries(EfnWeaponKit.airSlash(EfnAnim.byKey("biped/exsiliumgladius_reborn/exsiliumgladius_airatk")))
                .newBehaviorSeries(EfnWeaponKit.melee(List.of(
                        EfnAnim.byKey("biped/exsiliumgladius_reborn/exsiliumgladius_a"),
                        EfnAnim.byKey("biped/exsiliumgladius_reborn/exsiliumgladius_aa"),
                        EfnAnim.byKey("biped/exsiliumgladius_reborn/exsiliumgladius_aaa"),
                        EfnAnim.byKey("biped/exsiliumgladius_reborn/exsiliumgladius_aaaa"))))
                .newBehaviorSeries(EfnWeaponKit.ultimate(
                        EfnAnim.byKey("biped/exsiliumgladius_reborn/exsiliumgladius_abbb"), 60));
    }

    private Exsiliumgladius() {
    }
}
