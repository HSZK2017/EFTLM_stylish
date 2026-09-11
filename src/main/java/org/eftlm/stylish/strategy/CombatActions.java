package org.eftlm.stylish.strategy;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.EFTLM.EF.Capability.MaidPatch;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.eftlm.stylish.util.AnimKit;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

import java.util.List;
import java.util.Random;

/**
 * 华丽连段动作库：CombatBehaviors 行为表回调与技能状态机共用的战斗动作。
 */
public final class CombatActions {

    private static final Random RANDOM = new Random();

    private CombatActions() {
    }

    // ==================================================================
    // 防守
    // ==================================================================

    /**
     * 弹反：播放 ACTIVE 格挡动画并记录弹反窗口（窗口内命中全额取消伤害）。
     */
    public static boolean parry(MaidPatch<?> patch) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        WeaponCategory category = AnimKit.categoryOf(patch);
        patch.playAnimationSynchronized(AnimKit.parry(category), 0F);
        StyleState.setTick(maid, StyleState.LAST_PARRY, maid.tickCount);
        StyleState.setTick(maid, StyleState.LAST_HIT, maid.tickCount);
        maid.getNavigation().stop();
        maid.getMoveControl().strafe(-1.0F, 0.0F);
        return true;
    }

    /**
     * 闪避或防御：有耐力时概率闪避（带无敌帧），否则举盾格挡。
     */
    public static void dodgeOrBlock(MaidPatch<?> patch) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        LivingEntity target = patch.getTarget();
        AttributeInstance weight = maid.getAttribute(EpicFightAttributes.WEIGHT.get());
        float cost = weight == null ? 2.0F : (float) (weight.getValue() * 0.1F);
        if (target != null && patch.getStamina() >= cost && RANDOM.nextFloat() < 0.35F) {
            dodge(patch, maid, target, cost);
        } else {
            block(patch, maid);
        }
    }

    private static void dodge(MaidPatch<?> patch, EntityMaid maid, LivingEntity target, float cost) {
        List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> dodges = AnimKit.dodgeMoves();
        float targetYaw = target.getYRot();
        Vec3 attackDir = new Vec3(-Math.sin(Math.toRadians(targetYaw)), 0, Math.cos(Math.toRadians(targetYaw)));
        Vec3 toMaid = maid.position().subtract(target.position()).multiply(1, 0, 1).normalize();
        double dot = attackDir.dot(toMaid);
        double crossY = attackDir.cross(toMaid).y;
        int index;
        if (Math.abs(dot) > Math.abs(crossY)) {
            index = dot > 0 ? 1 : 0; // 迎面攻击向后撤，背身攻击向前闪
        } else {
            index = crossY > 0 ? 3 : 2; // 攻击来自右侧向左闪
        }
        patch.setStamina(patch.getStamina() - cost);
        patch.playAnimationSynchronized(dodges.get(index), 0F);
        // 闪避不产生格挡窗口
        StyleState.setTick(maid, StyleState.BLOCK_START, maid.tickCount - 100);
    }

    /**
     * 随机方向闪避（RL 行动用），消耗耐力。
     */
    public static boolean dodgeRandom(MaidPatch<?> patch) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        AttributeInstance weight = maid.getAttribute(EpicFightAttributes.WEIGHT.get());
        float cost = weight == null ? 2.0F : (float) (weight.getValue() * 0.1F);
        if (patch.getStamina() < cost) {
            return false; // P1 修复：耐力不足时如实返回失败（旧实现仍被记为 EXECUTED）
        }
        List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> dodges = AnimKit.dodgeMoves();
        int index = RANDOM.nextBoolean() ? 2 : 3; // 左右
        patch.setStamina(patch.getStamina() - cost);
        patch.playAnimationSynchronized(dodges.get(index), 0F);
        StyleState.setTick(maid, StyleState.BLOCK_START, maid.tickCount - 100);
        StyleState.setTick(maid, StyleState.LAST_DODGE, maid.tickCount);
        return true;
    }

    /**
     * 参考 EFTLM“灵动步伐”：根据敌方攻击朝向选择前/后/左/右闪避。
     * 供自动闪避逻辑（敌方攻击前摇检测）使用。
     */
    public static boolean dodgeFromAttack(MaidPatch<?> patch) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        LivingEntity target = patch.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        AttributeInstance weight = maid.getAttribute(EpicFightAttributes.WEIGHT.get());
        float cost = weight == null ? 2.0F : (float) (weight.getValue() * 0.1F);
        if (patch.getStamina() < cost) {
            return false;
        }
        dodge(patch, maid, target, cost);
        StyleState.setTick(maid, StyleState.LAST_DODGE, maid.tickCount);
        return true;
    }

    /**
     * 举盾格挡（RL 行动用）：播放格挡动画并开启格挡窗口。
     *
     * @return 是否已播放格挡动画（当前实现恒 true，保留返回值以便调用方如实上报执行结果）
     */
    public static boolean block(MaidPatch<?> patch) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        return block(patch, maid);
    }

    /**
     * 释放武器技能大招（RL 行动用）：播放类别大招动画、清空技能层数、设置物品冷却。
     */
    public static boolean releaseUltimate(MaidPatch<?> patch) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        WeaponCategory category = AnimKit.categoryOf(patch);
        var pool = AnimKit.swordmasterMoves(category);
        if (pool.isEmpty()) {
            return false; // P1 修复：无可用大招动画时如实返回失败
        }
        patch.playAnimationSynchronized(pool.get(0), 0F);
        StyleState.setSkillStack(maid, 0);
        ItemStack stack = maid.getMainHandItem();
        if (!stack.isEmpty()) {
            maid.getCooldowns().addCooldown(stack.getItem(), 60);
        }
        return true;
    }

    /** 唯一实现（P1 修复 2026-09-10：此前 public 版与 private 版是两份重复代码） */
    /**
     * 可用性判定（2026-09-10 新增）：与 {@link #dodgeRandom} 使用同一套资源条件，
     * 供 {@code GenericCombatExecutor.available()} 决定"是否把该行动提供给 RL"。
     * 否则模型会反复选择耐力不足/冷却中的行动，被执行器拒绝（实测 dodge 100%、roll 99% 被拒），
     * 而这些步仍按"模型选择"进入训练——instructor 的 rejected_high 弱点的另一半根因。
     */
    public static boolean canDodge(MaidPatch<?> patch) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        AttributeInstance weight = maid.getAttribute(EpicFightAttributes.WEIGHT.get());
        float cost = weight == null ? 2.0F : (float) (weight.getValue() * 0.1F);
        return patch.getStamina() >= cost;
    }

    /**
     * 弹反/格挡冷却（tick）。2026-09-10 新增：这两个动作此前**没有任何冷却**，
     * 于是策略可以每 5 tick 重播一次防守动画形成"原地弹反"吸收态——现场实测
     * action=5(parry) 占比 ~100%、奖励恒 0（无命中/无受伤/无距离惩罚）、
     * 正是"打不出命中"的另一种表现。加冷却后同一动作最多每 3 个决策点一次，
     * 反滥用窗口（50 决策 >8 次）重新生效，迫使策略在防守之外做出进攻选择。
     */
    public static final int PARRY_COOLDOWN = 15;
    public static final int BLOCK_COOLDOWN = 15;

    /** 弹反可用性（供 available() 使用；反应层直放不受限） */
    public static boolean canParry(EntityMaid maid) {
        return maid.tickCount - StyleState.getTick(maid, StyleState.LAST_PARRY) >= PARRY_COOLDOWN;
    }

    /** 格挡可用性（格挡以 BLOCK_START 计时） */
    public static boolean canBlock(EntityMaid maid) {
        return maid.tickCount - StyleState.getTick(maid, StyleState.BLOCK_START) >= BLOCK_COOLDOWN;
    }

    /** 翻滚可用性：被击倒时恒可（起身是硬约束），否则需脱离冷却且耐力足够 */
    public static boolean canRoll(MaidPatch<?> patch) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        if (patch.getEntityState().knockDown()) {
            return true;
        }
        if (maid.tickCount - StyleState.getTick(maid, StyleState.LAST_ROLL) < 40) {
            return false;
        }
        return canDodge(patch);
    }

    /** 轮换武器可用性：脱离换装冷却且背包里存在可换的近战候选 */
    public static boolean canCycleWeapon(MaidPatch<?> patch) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        if (maid.tickCount - StyleState.getTick(maid, StyleState.LAST_MELEE_SWAP) < WeaponArsenal.SWAP_COOLDOWN) {
            return false;
        }
        ItemStack current = maid.getMainHandItem();
        var backpack = maid.getAvailableBackpackInv();
        for (int i = 0; i < backpack.getSlots(); i++) {
            ItemStack stack = backpack.getStackInSlot(i);
            if (!stack.isEmpty() && WeaponArsenal.classify(stack) == WeaponArsenal.Kind.MELEE
                    && WeaponArsenal.isEfWeapon(stack) && !ItemStack.isSameItem(stack, current)) {
                return true;
            }
        }
        return false;
    }

    private static boolean block(MaidPatch<?> patch, EntityMaid maid) {
        WeaponCategory category = AnimKit.categoryOf(patch);
        patch.playAnimationSynchronized(AnimKit.guardHit(category), 0F);
        StyleState.setTick(maid, StyleState.BLOCK_START, maid.tickCount);
        maid.getNavigation().stop();
        maid.getMoveControl().strafe(-1.0F, 0.0F);
        return true;
    }

    /**
     * 被击倒翻滚起身（仿玩家按闪避键起身）。
     */
    public static boolean rollRecovery(MaidPatch<?> patch) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        boolean knocked = patch.getEntityState().knockDown();
        // 被击倒属于紧急起身：忽略冷却和耐力限制，保证一定能翻滚起来；
        // 非倒地（RL 主动翻滚）仍保留冷却/耐力消耗，避免滥用。
        if (!knocked && maid.tickCount - StyleState.getTick(maid, StyleState.LAST_ROLL) < 40) {
            return false; // P1 修复：冷却中如实返回失败
        }
        AttributeInstance weight = maid.getAttribute(EpicFightAttributes.WEIGHT.get());
        float cost = weight == null ? 2.0F : (float) (weight.getValue() * 0.1F);
        if (!knocked && patch.getStamina() < cost) {
            return false; // P1 修复：耐力不足如实返回失败
        }
        LivingEntity target = patch.getTarget();
        AnimationManager.AnimationAccessor<? extends StaticAnimation> roll = Animations.BIPED_ROLL_FORWARD;
        if (target != null) {
            Vec3 toTarget = target.position().subtract(maid.position()).multiply(1, 0, 1);
            double dot = maid.getLookAngle().multiply(1, 0, 1).normalize().dot(toTarget.normalize());
            roll = dot >= 0 ? Animations.BIPED_ROLL_BACKWARD : Animations.BIPED_ROLL_FORWARD;
        }
        if (!knocked) {
            patch.setStamina(patch.getStamina() - cost);
        }
        patch.playAnimationSynchronized(roll, 0F);
        StyleState.setTick(maid, StyleState.LAST_ROLL, maid.tickCount);
        StyleState.addFlair(maid, 4.0F);
        return true;
    }

    // ==================================================================
    // 进攻辅助
    // ==================================================================

    /**
     * 连段结束回调：切换风格并标记连段结束（技能据此轮换武器 / 触发枪神收尾）。
     */
    public static void comboEnd(MaidPatch<?> patch) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        StyleState.toggleStyle(maid);
        StyleState.setInt(maid, StyleState.COMBO_END, 1);
    }

    /**
     * 轮换近战武器（带 30 tick 冷却，避免频繁换装触发 AI 重建）。
     * 选"与主手不同、且不是上次换下的"背包中评分最高的 EF 能力近战武器。
     *
     * @return 是否实际完成了换装（冷却中 / 无候选 / 换装失败返回 false）
     */
    public static boolean cycleWeapon(MaidPatch<?> patch) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        if (maid.tickCount - StyleState.getTick(maid, StyleState.LAST_MELEE_SWAP) < WeaponArsenal.SWAP_COOLDOWN) {
            return false;
        }
        ItemStack current = maid.getMainHandItem();
        String lastCycled = maid.getPersistentData().getString(StyleState.LAST_CYCLE);

        ItemStack best = ItemStack.EMPTY;
        double bestPower = -1;
        int bestSlot = -1;
        var backpack = maid.getAvailableBackpackInv();
        for (int i = 0; i < backpack.getSlots(); i++) {
            ItemStack stack = backpack.getStackInSlot(i);
            if (WeaponArsenal.classify(stack) != WeaponArsenal.Kind.MELEE || !WeaponArsenal.isEfWeapon(stack)) {
                continue;
            }
            if (ItemStack.isSameItem(stack, current)) {
                continue;
            }
            String key = ForgeRegistries.ITEMS.getKey(stack.getItem()).toString();
            if (key.equals(lastCycled)) {
                continue;
            }
            double power = WeaponArsenal.powerOf(stack);
            if (power > bestPower) {
                bestPower = power;
                best = stack;
                bestSlot = i;
            }
        }
        if (bestSlot < 0) {
            return false;
        }
        if (!WeaponArsenal.swapBackpackToHand(maid, bestSlot, InteractionHand.MAIN_HAND)) {
            return false;
        }
        // 记录"换上的武器"（而非换下的）：下一轮轮换时跳过它，
        // 配合 isSameItem(主手) 检查，实现 A→B→C→A 的循环轮换而非来回横跳
        if (!best.isEmpty()) {
            maid.getPersistentData().putString(StyleState.LAST_CYCLE,
                    ForgeRegistries.ITEMS.getKey(best.getItem()).toString());
        }
        StyleState.setTick(maid, StyleState.LAST_MELEE_SWAP, maid.tickCount);
        return true;
    }

    /**
     * 枪神收尾：把远程武器换到主手（主手本就是枪械则不换）并进入收尾状态。
     * 瞄准动画由技能在换手重置完成后播放。
     *
     * @return 是否成功进入收尾状态（无可用远程武器 / 换装失败返回 false）
     */
    public static boolean startRangedAim(MaidPatch<?> patch) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        boolean swapped = false;
        ItemStack mainHand = maid.getMainHandItem();
        if (!WeaponArsenal.isGun(mainHand)) {
            ItemStack ranged = WeaponArsenal.findRanged(maid);
            if (ranged.isEmpty()) {
                return false;
            }
            if (!WeaponArsenal.forceHand(maid, ranged)) {
                return false;
            }
            swapped = true;
        }
        maid.getPersistentData().putBoolean(StyleState.FINISHER_SWAPPED, swapped);
        StyleState.setInt(maid, StyleState.FINISHER, 1);
        StyleState.setTick(maid, StyleState.FINISHER_START, maid.tickCount);
        StyleState.setTick(maid, StyleState.LAST_SWAP, maid.tickCount);
        return true;
    }

    /**
     * 当前主手是否双手持握。
     */
    public static boolean isTwoHand(MaidPatch<?> patch, ItemStack stack) {
        CapabilityItem cap = EpicFightCapabilities.getItemStackCapability(stack);
        return cap != null && cap.getStyle(patch) == CapabilityItem.Styles.TWO_HAND;
    }
}
