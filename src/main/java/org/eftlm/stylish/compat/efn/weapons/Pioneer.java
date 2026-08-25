package org.eftlm.stylish.compat.efn.weapons;

import org.eftlm.stylish.compat.efn.EfnAnim;
import org.eftlm.stylish.compat.efn.EfnWeaponKit;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

import java.util.List;

/**
 * 先锋剑（Sword of Pioneer）行为表：技能表 EFN_SKILLS.md §9。
 * <p>
 * 普攻：AUTO1~4；空中 JC：AIRSLASH；
 * 大招：技能一段（nf_sword_skill1），资源门控满层释放。
 */
public final class Pioneer {

    public static CombatBehaviors.Builder<HumanoidMobPatch<?>> Instance;

    static {
        Instance = CombatBehaviors.<HumanoidMobPatch<?>>builder()
                .newBehaviorSeries(EfnWeaponKit.parry())
                .newBehaviorSeries(EfnWeaponKit.dodge())
                .newBehaviorSeries(EfnWeaponKit.airSlash(EfnAnim.byKey("biped/nf_sword/nf_sword_airslash")))
                .newBehaviorSeries(EfnWeaponKit.melee(List.of(
                        EfnAnim.byKey("biped/nf_sword/nf_sword_auto1"),
                        EfnAnim.byKey("biped/nf_sword/nf_sword_auto2"),
                        EfnAnim.byKey("biped/nf_sword/nf_sword_auto3"),
                        EfnAnim.byKey("biped/nf_sword/nf_sword_auto4"),
                        EfnAnim.byKey("biped/nf_sword/nf_sword_dash"))))
                .newBehaviorSeries(EfnWeaponKit.ultimate(
                        EfnAnim.byKey("biped/nf_sword/nf_sword_skill1"), 60));
    }

    private Pioneer() {
    }
}
