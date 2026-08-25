package org.eftlm.stylish.compat.efn.weapons;

import net.EFTLM.EF.Capability.MaidPatch;
import org.eftlm.stylish.compat.efn.EfnAnim;
import org.eftlm.stylish.compat.efn.EfnWeaponKit;
import org.eftlm.stylish.strategy.StylishConditions;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

import java.util.List;

/**
 * 苍刃（HF_BLADE）行为表：技能表 EFN_SKILLS.md §3。
 * <p>
 * 与真村雨同构：X 系 4 段普攻；Air_X 空中 JC；Y 蓄力拔刀斩大招；
 * 额外：敌人攻击中近身时插入弹反反击（counter）。
 */
public final class HfBlade {

    public static CombatBehaviors.Builder<HumanoidMobPatch<?>> Instance;

    static {
        var counter = EfnAnim.byKey("biped/hf_blade/combat/hf_blade_counter");
        Instance = CombatBehaviors.<HumanoidMobPatch<?>>builder()
                .newBehaviorSeries(EfnWeaponKit.parry())
                .newBehaviorSeries(EfnWeaponKit.dodge())
                .newBehaviorSeries(EfnWeaponKit.airSlash(EfnAnim.byKey("biped/hf_blade/combat/hf_blade_air_x")))
                .newBehaviorSeries(EfnWeaponKit.melee(List.of(
                        EfnAnim.byKey("biped/hf_blade/combat/hf_blade_x"),
                        EfnAnim.byKey("biped/hf_blade/combat/hf_blade_xx"),
                        EfnAnim.byKey("biped/hf_blade/combat/hf_blade_xxx"),
                        EfnAnim.byKey("biped/hf_blade/combat/hf_blade_xxxx"))))
                .newBehaviorSeries(EfnWeaponKit.guard(counter,
                        CombatBehaviors.BehaviorSeries.<HumanoidMobPatch<?>>builder()
                                .cooldown(40).weight(30.0F).canBeInterrupted(false).looping(false)
                                .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder()
                                        .custom(p -> StylishConditions.enemyAttackingNear((MaidPatch<?>) p))
                                        .animationBehavior(counter)
                                        .withinDistance(0.0D, 3.5D))))
                .newBehaviorSeries(EfnWeaponKit.ultimate(
                        EfnAnim.byKey("biped/hf_blade/skill/hf_blade_y_charge"), 60));
    }

    private HfBlade() {
    }
}
