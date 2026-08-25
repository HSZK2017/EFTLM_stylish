package org.eftlm.stylish.compat.efn.weapons;

import org.eftlm.stylish.compat.efn.EfnAnim;
import org.eftlm.stylish.compat.efn.EfnWeaponKit;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

import java.util.List;

/**
 * 血欲太刀（Bloodlust）行为表：技能表 EFN_SKILLS.md §12
 * （air_tachi / air_tachi_e / co_tachi 共用）。
 * <p>
 * 普攻：AUTO1~5；空中 JC：AIRSLASH；
 * 大招：血欲开启（nf_tachi_bloodlust，原版阶段 0→2 并施加血欲效果）。
 */
public final class Bloodlust {

    public static CombatBehaviors.Builder<HumanoidMobPatch<?>> Instance;

    static {
        Instance = CombatBehaviors.<HumanoidMobPatch<?>>builder()
                .newBehaviorSeries(EfnWeaponKit.parry())
                .newBehaviorSeries(EfnWeaponKit.dodge())
                .newBehaviorSeries(EfnWeaponKit.airSlash(EfnAnim.byKey("biped/nf_tachi/nf_tachi_airslash")))
                .newBehaviorSeries(EfnWeaponKit.melee(List.of(
                        EfnAnim.byKey("biped/nf_tachi/nf_tachi_auto1"),
                        EfnAnim.byKey("biped/nf_tachi/nf_tachi_auto2"),
                        EfnAnim.byKey("biped/nf_tachi/nf_tachi_auto3"),
                        EfnAnim.byKey("biped/nf_tachi/nf_tachi_auto4"),
                        EfnAnim.byKey("biped/nf_tachi/nf_tachi_auto5"),
                        EfnAnim.byKey("biped/nf_tachi/nf_tachi_dash"))))
                .newBehaviorSeries(EfnWeaponKit.ultimate(
                        EfnAnim.byKey("biped/nf_tachi/nf_tachi_bloodlust"), 80));
    }

    private Bloodlust() {
    }
}
