package org.eftlm.stylish.compat.efn.weapons;

import net.EFTLM.EF.Capability.MaidPatch;
import org.eftlm.stylish.compat.efn.EfnAnim;
import org.eftlm.stylish.compat.efn.EfnWeaponKit;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

import java.util.List;

/**
 * 阔刃（Broad Blade）行为表：技能表 EFN_SKILLS.md §10。
 * <p>
 * 普攻：8 段连击；空中 JC：AIR_SLASH；
 * 大招：弹反反击（broadblade_counter，原版 ParrySuccess 触发）；
 * 额外：目标血量低于 15% 时执行处决（broadblade_executed）。
 */
public final class BroadBlade {

    public static CombatBehaviors.Builder<HumanoidMobPatch<?>> Instance;

    static {
        var execute = EfnAnim.byKey("biped/broadblade/broadblade_executed");
        Instance = CombatBehaviors.<HumanoidMobPatch<?>>builder()
                .newBehaviorSeries(EfnWeaponKit.parry())
                .newBehaviorSeries(EfnWeaponKit.dodge())
                .newBehaviorSeries(EfnWeaponKit.airSlash(EfnAnim.byKey("biped/broadblade/broadblade_air_slash")))
                .newBehaviorSeries(EfnWeaponKit.melee(List.of(
                        EfnAnim.byKey("biped/broadblade/broadblade_auto1"),
                        EfnAnim.byKey("biped/broadblade/broadblade_auto2"),
                        EfnAnim.byKey("biped/broadblade/broadblade_auto3"),
                        EfnAnim.byKey("biped/broadblade/broadblade_auto4"),
                        EfnAnim.byKey("biped/broadblade/broadblade_auto5"),
                        EfnAnim.byKey("biped/broadblade/broadblade_auto6"),
                        EfnAnim.byKey("biped/broadblade/broadblade_auto7"),
                        EfnAnim.byKey("biped/broadblade/broadblade_auto8"),
                        EfnAnim.byKey("biped/broadblade/broadblade_dash_slash"))))
                // 处决：目标低血量（原版 EFNTargetHealthCondition）
                .newBehaviorSeries(EfnWeaponKit.guard(execute,
                        CombatBehaviors.BehaviorSeries.<HumanoidMobPatch<?>>builder()
                                .cooldown(120).weight(80.0F).canBeInterrupted(false).looping(false)
                                .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder()
                                        .custom(p -> {
                                            var target = ((MaidPatch<?>) p).getTarget();
                                            return target != null && !target.isDeadOrDying()
                                                    && target.getHealth() / target.getMaxHealth() < 0.15F;
                                        })
                                        .animationBehavior(execute)
                                        .withinDistance(0.0D, 3.5D))))
                .newBehaviorSeries(EfnWeaponKit.ultimate(
                        EfnAnim.byKey("biped/broadblade/broadblade_counter"), 40));
    }

    private BroadBlade() {
    }
}
