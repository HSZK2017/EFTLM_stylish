package org.eftlm.stylish.compat.efn.weapons;

import org.eftlm.stylish.compat.efn.EfnAnim;
import org.eftlm.stylish.compat.efn.EfnWeaponKit;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

import java.util.List;

/**
 * 短刀（Short Sword）行为表：技能表 EFN_SKILLS.md §11。
 * <p>
 * 普攻：6 连击（AUTO1~6）；空中 JC：AIRSLASH；
 * 大招：短刀技能（nf_shortsword_skill），资源门控满层释放。
 */
public final class Shortsword {

    public static CombatBehaviors.Builder<HumanoidMobPatch<?>> Instance;

    static {
        Instance = CombatBehaviors.<HumanoidMobPatch<?>>builder()
                .newBehaviorSeries(EfnWeaponKit.parry())
                .newBehaviorSeries(EfnWeaponKit.dodge())
                .newBehaviorSeries(EfnWeaponKit.airSlash(EfnAnim.byKey("biped/nf_shortsword/nf_shortsword_airslash")))
                .newBehaviorSeries(EfnWeaponKit.melee(List.of(
                        EfnAnim.byKey("biped/nf_shortsword/nf_shortsword_auto1"),
                        EfnAnim.byKey("biped/nf_shortsword/nf_shortsword_auto2"),
                        EfnAnim.byKey("biped/nf_shortsword/nf_shortsword_auto3"),
                        EfnAnim.byKey("biped/nf_shortsword/nf_shortsword_auto4"),
                        EfnAnim.byKey("biped/nf_shortsword/nf_shortsword_auto5"),
                        EfnAnim.byKey("biped/nf_shortsword/nf_shortsword_auto6"),
                        EfnAnim.byKey("biped/nf_shortsword/nf_shortsword_dash"))))
                .newBehaviorSeries(EfnWeaponKit.ultimate(
                        EfnAnim.byKey("biped/nf_shortsword/nf_shortsword_skill"), 60));
    }

    private Shortsword() {
    }
}
