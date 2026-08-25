package org.eftlm.stylish.compat.efn.weapons;

import org.eftlm.stylish.compat.efn.EfnAnim;
import org.eftlm.stylish.compat.efn.EfnWeaponKit;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

import java.util.List;

/**
 * 冥枪（Meen Lance）行为表：技能表 EFN_SKILLS.md §6。
 * <p>
 * 普攻：AUTO1~4；空中 JC：AIRSLASH；
 * 大招：终结技（skill_max，原版需 10 层充能，此处改为资源门控满层释放）。
 */
public final class Meenlance {

    public static CombatBehaviors.Builder<HumanoidMobPatch<?>> Instance;

    static {
        Instance = CombatBehaviors.<HumanoidMobPatch<?>>builder()
                .newBehaviorSeries(EfnWeaponKit.parry())
                .newBehaviorSeries(EfnWeaponKit.dodge())
                .newBehaviorSeries(EfnWeaponKit.airSlash(EfnAnim.byKey("biped/nf_meen/nf_meen_airslash")))
                .newBehaviorSeries(EfnWeaponKit.melee(List.of(
                        EfnAnim.byKey("biped/nf_meen/nf_meen_auto1"),
                        EfnAnim.byKey("biped/nf_meen/nf_meen_auto2"),
                        EfnAnim.byKey("biped/nf_meen/nf_meen_auto3"),
                        EfnAnim.byKey("biped/nf_meen/nf_meen_auto4"),
                        EfnAnim.byKey("biped/nf_meen/nf_meen_dash"))))
                .newBehaviorSeries(EfnWeaponKit.ultimate(
                        EfnAnim.byKey("biped/nf_meen/nf_meen_skill_max"), 80));
    }

    private Meenlance() {
    }
}
