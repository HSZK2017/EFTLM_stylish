package org.eftlm.stylish.compat.efn.weapons;

import net.EFTLM.EF.Capability.MaidPatch;
import org.eftlm.stylish.compat.efn.EfnAnim;
import org.eftlm.stylish.compat.efn.EfnWeaponKit;
import org.eftlm.stylish.strategy.StylishConditions;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

import java.util.List;

/**
 * 真村雨（HF_MURASAMA）行为表：技能表 EFN_SKILLS.md §2。
 * <p>
 * 普攻：X 系 4 段；空中 JC：Air_X；大招：Y 蓄力拔刀斩（y_charge）；
 * 额外：敌人攻击中近身时插入弹反反击（counter）。
 */
public final class Murasama {

    public static CombatBehaviors.Builder<HumanoidMobPatch<?>> Instance;

    static {
        var counter = EfnAnim.byKey("biped/hf_murasama/combat/hf_murasama_counter");
        Instance = CombatBehaviors.<HumanoidMobPatch<?>>builder()
                .newBehaviorSeries(EfnWeaponKit.parry())
                .newBehaviorSeries(EfnWeaponKit.dodge())
                .newBehaviorSeries(EfnWeaponKit.airSlash(EfnAnim.byKey("biped/hf_murasama/combat/hf_murasama_air_x")))
                .newBehaviorSeries(EfnWeaponKit.melee(List.of(
                        EfnAnim.byKey("biped/hf_murasama/combat/hf_murasama_x"),
                        EfnAnim.byKey("biped/hf_murasama/combat/hf_murasama_xx"),
                        EfnAnim.byKey("biped/hf_murasama/combat/hf_murasama_xxx"),
                        EfnAnim.byKey("biped/hf_murasama/combat/hf_murasama_xxxx"))))
                .newBehaviorSeries(EfnWeaponKit.guard(counter,
                        CombatBehaviors.BehaviorSeries.<HumanoidMobPatch<?>>builder()
                                .cooldown(40).weight(30.0F).canBeInterrupted(false).looping(false)
                                .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder()
                                        .custom(p -> StylishConditions.enemyAttackingNear((MaidPatch<?>) p))
                                        .animationBehavior(counter)
                                        .withinDistance(0.0D, 3.5D))))
                .newBehaviorSeries(EfnWeaponKit.ultimate(
                        EfnAnim.byKey("biped/hf_murasama/skill/hf_murasama_y_charge"), 60));
    }

    private Murasama() {
    }
}
