package org.eftlm.stylish.compat.efn.weapons;

import org.eftlm.stylish.compat.efn.EfnAnim;
import org.eftlm.stylish.compat.efn.EfnWeaponKit;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

import java.util.List;

/**
 * 以太暮光双刀（Aetherial Dusk Dual Sword）行为表：技能表 EFN_SKILLS.md §7。
 * <p>
 * 普攻：AUTO1~4；空中 JC：AIRSLASH；
 * 大招：乱舞（nf_dual_skill），资源门控满层释放。
 */
public final class Aetherialdusk {

    public static CombatBehaviors.Builder<HumanoidMobPatch<?>> Instance;

    static {
        Instance = CombatBehaviors.<HumanoidMobPatch<?>>builder()
                .newBehaviorSeries(EfnWeaponKit.parry())
                .newBehaviorSeries(EfnWeaponKit.dodge())
                .newBehaviorSeries(EfnWeaponKit.airSlash(EfnAnim.byKey("biped/nf_dual/nf_dual_airslash")))
                .newBehaviorSeries(EfnWeaponKit.melee(List.of(
                        EfnAnim.byKey("biped/nf_dual/nf_dual_auto1"),
                        EfnAnim.byKey("biped/nf_dual/nf_dual_auto2"),
                        EfnAnim.byKey("biped/nf_dual/nf_dual_auto3"),
                        EfnAnim.byKey("biped/nf_dual/nf_dual_auto4"),
                        EfnAnim.byKey("biped/nf_dual/nf_dual_dash"))))
                .newBehaviorSeries(EfnWeaponKit.ultimate(
                        EfnAnim.byKey("biped/nf_dual/nf_dual_skill"), 60));
    }

    private Aetherialdusk() {
    }
}
