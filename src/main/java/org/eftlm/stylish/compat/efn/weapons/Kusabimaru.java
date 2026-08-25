package org.eftlm.stylish.compat.efn.weapons;

import net.EFTLM.EF.Capability.MaidPatch;
import org.eftlm.stylish.compat.efn.EfnAnim;
import org.eftlm.stylish.compat.efn.EfnWeaponKit;
import org.eftlm.stylish.strategy.StylishConditions;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

import java.util.List;

/**
 * 楔丸（Kusabimaru）行为表：技能表 EFN_SKILLS.md §13。
 * <p>
 * 普攻：AUTO1~5；空中 JC：太刀空斩；
 * 大招：龙闪（dragon_flash，原版需 2 层）；
 * 额外：目标浮空时樱花舞（sakura_dance，原版空中 2 层派生）。
 */
public final class Kusabimaru {

    public static CombatBehaviors.Builder<HumanoidMobPatch<?>> Instance;

    static {
        var sakuraDance = EfnAnim.byKey("biped/sekiro/kusabimaru/sakura_dance");
        Instance = CombatBehaviors.<HumanoidMobPatch<?>>builder()
                .newBehaviorSeries(EfnWeaponKit.parry())
                .newBehaviorSeries(EfnWeaponKit.dodge())
                .newBehaviorSeries(EfnWeaponKit.airSlash(EfnAnim.byKey("biped/nf_tachi/nf_tachi_airslash")))
                .newBehaviorSeries(EfnWeaponKit.melee(List.of(
                        EfnAnim.byKey("biped/sekiro/kusabimaru/kusabimaru_auto1"),
                        EfnAnim.byKey("biped/sekiro/kusabimaru/kusabimaru_auto2"),
                        EfnAnim.byKey("biped/sekiro/kusabimaru/kusabimaru_auto3"),
                        EfnAnim.byKey("biped/sekiro/kusabimaru/kusabimaru_auto4"),
                        EfnAnim.byKey("biped/sekiro/kusabimaru/kusabimaru_auto5"))))
                // 樱花舞：目标浮空时（原版空中 + 2 层堆叠派生）
                .newBehaviorSeries(EfnWeaponKit.guard(sakuraDance,
                        CombatBehaviors.BehaviorSeries.<HumanoidMobPatch<?>>builder()
                                .cooldown(40).weight(40.0F).canBeInterrupted(false).looping(false)
                                .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder()
                                        .custom(p -> StylishConditions.targetAirborne((MaidPatch<?>) p))
                                        .animationBehavior(sakuraDance)
                                        .withinDistance(0.0D, 4.0D))))
                .newBehaviorSeries(EfnWeaponKit.ultimate(
                        EfnAnim.byKey("biped/sekiro/kusabimaru/dragon_flash"), 80));
    }

    private Kusabimaru() {
    }
}
