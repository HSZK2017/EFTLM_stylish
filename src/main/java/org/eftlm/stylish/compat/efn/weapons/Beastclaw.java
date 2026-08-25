package org.eftlm.stylish.compat.efn.weapons;

import org.eftlm.stylish.compat.efn.EfnAnim;
import org.eftlm.stylish.compat.efn.EfnWeaponKit;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

import java.util.List;

/**
 * 兽爪（Beast Claw）行为表：技能表 EFN_SKILLS.md §14。
 * <p>
 * 普攻：AUTO1~3；空中 JC：AIRSLASH；
 * 大招：兽吼（beastroar，原版 1 层，施加爪强化 30s + 眩晕免疫 10s）。
 */
public final class Beastclaw {

    public static CombatBehaviors.Builder<HumanoidMobPatch<?>> Instance;

    static {
        Instance = CombatBehaviors.<HumanoidMobPatch<?>>builder()
                .newBehaviorSeries(EfnWeaponKit.parry())
                .newBehaviorSeries(EfnWeaponKit.dodge())
                .newBehaviorSeries(EfnWeaponKit.airSlash(EfnAnim.byKey("biped/nf_claw/nf_claw_airslash")))
                .newBehaviorSeries(EfnWeaponKit.melee(List.of(
                        EfnAnim.byKey("biped/nf_claw/nf_claw_auto1"),
                        EfnAnim.byKey("biped/nf_claw/nf_claw_auto2"),
                        EfnAnim.byKey("biped/nf_claw/nf_claw_auto3"),
                        EfnAnim.byKey("biped/nf_claw/nf_claw_dash"))))
                .newBehaviorSeries(EfnWeaponKit.ultimate(
                        EfnAnim.byKey("biped/nf_claw/nf_claw_beastroar"), 80));
    }

    private Beastclaw() {
    }
}
