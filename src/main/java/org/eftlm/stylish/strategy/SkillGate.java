package org.eftlm.stylish.strategy;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.EFTLM.EF.Capability.MaidPatch;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;

/**
 * 武器技能大招门控（资源型技能循环）：
 * 学习本模组技能 + 命中攒满层数 + 主手物品不在冷却中 → 释放大招动画、
 * 清空层数并给主手物品加冷却。EFTLM 8 类原版武器行为与 EFN 兼容武器共用。
 */
public final class SkillGate {

    private static final net.minecraft.resources.ResourceLocation SKILL_ID =
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                    org.eftlm.stylish.EF.Skill.StylishCombatSkill.MODID,
                    org.eftlm.stylish.EF.Skill.StylishCombatSkill.SKILL_PATH);

    private SkillGate() {
    }

    /**
     * 女仆是否已学习本模组技能（技能书解锁大招）。
     */
    public static boolean hasLearnedSkill(MaidPatch<?> patch) {
        return patch.hasLearnedSkill(SKILL_ID);
    }

    /**
     * 技能层数是否已攒满（资源门控：满层才释放大招）。
     */
    public static boolean hasFullStack(MaidPatch<?> patch) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        return StyleState.getSkillStack(maid) >= StyleState.MAX_SKILL_STACK;
    }

    /**
     * 技能可用：主手物品不在原版物品冷却中（技能 CD 用物品冷却模拟）。
     */
    public static boolean canUseSkill(MaidPatch<?> patch) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        ItemStack stack = maid.getMainHandItem();
        return !stack.isEmpty() && !maid.getCooldowns().isOnCooldown(stack.getItem());
    }

    /**
     * 大招完整门控：已学习 + 满层 + 物品不在冷却。
     * hybrid 仲裁：RL 模型加载时行为表大招系列让位（大招由 RL ACT_ULTIMATE 决策）。
     */
    public static boolean canUltimate(MaidPatch<?> patch) {
        if (org.eftlm.stylish.rl.RlConfig.rlDrivesAttacks()) {
            return false;
        }
        return hasLearnedSkill(patch) && hasFullStack(patch) && canUseSkill(patch);
    }

    /**
     * 释放武器技能大招：播放技能动画、清空技能层数、设置物品冷却。
     */
    public static void useSkill(MaidPatch<?> patch,
                                AnimationManager.AnimationAccessor<? extends StaticAnimation> anim,
                                int cooldownTicks) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        patch.playAnimationSynchronized(anim, 0F);
        // 清空技能层数（资源已消费）
        StyleState.setSkillStack(maid, 0);
        ItemStack stack = maid.getMainHandItem();
        if (!stack.isEmpty()) {
            maid.getCooldowns().addCooldown(stack.getItem(), cooldownTicks);
        }
    }
}
