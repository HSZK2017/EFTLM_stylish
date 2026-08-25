package org.eftlm.stylish.compat.efn;

import net.EFTLM.EF.Capability.MaidPatch;
import org.eftlm.stylish.strategy.CombatActions;
import org.eftlm.stylish.strategy.SkillGate;
import org.eftlm.stylish.strategy.StylishConditions;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

import java.util.List;

/**
 * EFN 武器行为表公共构件：弹反 / 防守 / 空中 JC / 地面连段 / 技能大招系列，
 * 由各 EFN 武器行为类（weapons 包）按技能表组合使用。
 * <p>
 * 大招复用 {@link SkillGate} 的资源型门控（学习技能 + 满层 + 物品冷却），
 * 与 8 类原版武器的武器技能大招行为一致。
 * <p>
 * 健壮性：任何动画缺失时返回"永不触发"兜底系列而非 null——
 * 避免 null 系列进入 build() 导致 EFTLM MaidPatch 构造失败（渲染 / 技能全部失效）。
 */
public final class EfnWeaponKit {

    private static final double MELEE_RANGE = 3.5D;
    private static final double AIR_RANGE = 4.0D;

    private EfnWeaponKit() {
    }

    /**
     * 弹反系列：敌人攻击中、近身、华丽度低于 C 级 → 弹反挽回评价。
     */
    public static CombatBehaviors.BehaviorSeries.Builder<HumanoidMobPatch<?>> parry() {
        return CombatBehaviors.BehaviorSeries.<HumanoidMobPatch<?>>builder()
                .cooldown(6).weight(80.0F).canBeInterrupted(false).looping(false)
                .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder()
                        .custom(p -> StylishConditions.enemyAttackingLowFlair((MaidPatch<?>) p))
                        .behavior(p -> CombatActions.parry((MaidPatch<?>) p)));
    }

    /**
     * 防守系列：敌人攻击中且近身 → 闪避或格挡。
     */
    public static CombatBehaviors.BehaviorSeries.Builder<HumanoidMobPatch<?>> dodge() {
        return CombatBehaviors.BehaviorSeries.<HumanoidMobPatch<?>>builder()
                .cooldown(10).weight(40.0F).canBeInterrupted(false).looping(false)
                .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder()
                        .custom(p -> StylishConditions.enemyAttackingNear((MaidPatch<?>) p))
                        .behavior(p -> CombatActions.dodgeOrBlock((MaidPatch<?>) p)));
    }

    /**
     * 空中 JC 系列：目标浮空且近身 → 空中攻击连段。
     */
    public static CombatBehaviors.BehaviorSeries.Builder<HumanoidMobPatch<?>> airSlash(
            AnimationManager.AnimationAccessor<? extends StaticAnimation> air) {
        return guard(air, CombatBehaviors.BehaviorSeries.<HumanoidMobPatch<?>>builder()
                .cooldown(30).weight(100.0F).canBeInterrupted(false).looping(false)
                .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder()
                        .custom(p -> StylishConditions.targetAirborne((MaidPatch<?>) p))
                        .animationBehavior(air).withinDistance(0.0D, AIR_RANGE)));
    }

    /**
     * 地面连段系列：目标在近战距离 → 依次播放武器专属普攻动画。
     * 动画为 null 的条目自动跳过；全部无效时返回永不触发系列。
     */
    public static CombatBehaviors.BehaviorSeries.Builder<HumanoidMobPatch<?>> melee(
            List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> autos) {
        CombatBehaviors.BehaviorSeries.Builder<HumanoidMobPatch<?>> series =
                CombatBehaviors.BehaviorSeries.<HumanoidMobPatch<?>>builder()
                        .cooldown(6).weight(100.0F).canBeInterrupted(true).looping(false);
        boolean first = true;
        for (AnimationManager.AnimationAccessor<? extends StaticAnimation> auto : autos) {
            if (auto == null) {
                continue;
            }
            CombatBehaviors.Behavior.Builder<HumanoidMobPatch<?>> behavior =
                    CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder()
                            .animationBehavior(auto).withinDistance(0.0D, MELEE_RANGE);
            if (first) {
                behavior.custom(p -> StylishConditions.inMelee((MaidPatch<?>) p));
                first = false;
            }
            series.nextBehavior(behavior);
        }
        return first ? never() : series;
    }

    /**
     * 武器技能大招系列：已学习技能 + 满层 + 物品不在冷却 → 播放专属技能动画。
     */
    public static CombatBehaviors.BehaviorSeries.Builder<HumanoidMobPatch<?>> ultimate(
            AnimationManager.AnimationAccessor<? extends StaticAnimation> anim, int cooldownTicks) {
        return guard(anim, CombatBehaviors.BehaviorSeries.<HumanoidMobPatch<?>>builder()
                .cooldown(60).weight(25.0F).canBeInterrupted(false).looping(false)
                .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder()
                        .custom(p -> SkillGate.canUltimate((MaidPatch<?>) p))
                        .behavior(p -> SkillGate.useSkill((MaidPatch<?>) p, anim, cooldownTicks))));
    }

    /**
     * 动画缺失时用永不触发系列替换（用于 weapons 包内联的自定义系列）。
     */
    public static CombatBehaviors.BehaviorSeries.Builder<HumanoidMobPatch<?>> guard(
            AnimationManager.AnimationAccessor<? extends StaticAnimation> anim,
            CombatBehaviors.BehaviorSeries.Builder<HumanoidMobPatch<?>> series) {
        return anim != null ? series : never();
    }

    /**
     * 永不触发系列：谓词恒 false，安全占位（防 null 进入 build()）。
     */
    private static CombatBehaviors.BehaviorSeries.Builder<HumanoidMobPatch<?>> never() {
        return CombatBehaviors.BehaviorSeries.<HumanoidMobPatch<?>>builder()
                .cooldown(1).weight(1.0F).canBeInterrupted(false).looping(false)
                .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder()
                        .custom(p -> false));
    }
}
