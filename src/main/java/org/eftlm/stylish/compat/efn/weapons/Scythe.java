package org.eftlm.stylish.compat.efn.weapons;

import org.eftlm.stylish.compat.efn.EfnAnim;
import org.eftlm.stylish.compat.efn.EfnWeaponKit;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

import java.util.List;

/**
 * 血月镰刀（Scythe）行为表：技能表 EFN_SKILLS.md §15。
 * <p>
 * 普攻：AUTO1~5；空中 JC：AIRSLASH；
 * 大招：血色终结（scythe_scarlet_end），资源门控满层释放。
 */
public final class Scythe {

    public static CombatBehaviors.Builder<HumanoidMobPatch<?>> Instance;

    static {
        Instance = CombatBehaviors.<HumanoidMobPatch<?>>builder()
                .newBehaviorSeries(EfnWeaponKit.parry())
                .newBehaviorSeries(EfnWeaponKit.dodge())
                .newBehaviorSeries(EfnWeaponKit.airSlash(EfnAnim.byKey("biped/scythe/combat/scythe_airslash")))
                .newBehaviorSeries(EfnWeaponKit.melee(List.of(
                        EfnAnim.byKey("biped/scythe/combat/scythe_auto1"),
                        EfnAnim.byKey("biped/scythe/combat/scythe_auto2"),
                        EfnAnim.byKey("biped/scythe/combat/scythe_auto3"),
                        EfnAnim.byKey("biped/scythe/combat/scythe_auto4"),
                        EfnAnim.byKey("biped/scythe/combat/scythe_auto5"),
                        EfnAnim.byKey("biped/scythe/combat/scythe_dash"))))
                .newBehaviorSeries(EfnWeaponKit.ultimate(
                        EfnAnim.byKey("biped/scythe/skill/scythe_scarlet_end"), 80));
    }

    private Scythe() {
    }
}
