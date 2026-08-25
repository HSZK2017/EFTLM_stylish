package org.eftlm.stylish.rl;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.EFTLM.EF.Capability.MaidPatch;
import net.minecraft.world.entity.LivingEntity;
import org.eftlm.stylish.strategy.StyleState;
import org.eftlm.stylish.strategy.WeaponArsenal;
import org.eftlm.stylish.util.AnimKit;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

/**
 * RL 状态采集：把女仆战斗态势编码为 32 维归一化特征向量。
 * <p>
 * 索引约定（训练脚本与推理共用）：
 * <pre>
 *  0  最近目标距离（/16 截断；无目标 = 1.0）
 *  1  最近目标血量比例（无目标 = 0）
 *  2  女仆血量比例
 *  3  女仆耐力比例
 *  4  华丽度 /100
 *  5  风格（0 剑圣 / 1 枪神）
 *  6  武器类型（0 近战 / 1 弓弩 / 2 枪械 / 3 无）
 *  7  最近目标是否攻击中（0/1）
 *  8  最近目标是否浮空（0/1）
 *  9  是否被击倒（0/1）
 *  10 是否有箭矢即将命中（0/1）
 *  11 连段命中计数 /6
 *  12 技能层数 /5
 *  13 背包是否有可用远程武器（0/1）
 *  14 主手技能冷却中（0/1）
 *  15 距上次武器切换（/30 截断）
 *  16 上次行动成功执行（0/1，10 tick 窗口）——RL 执行反馈
 *  17 上次行动被拒（0/1，10 tick 窗口）——RL 执行反馈
 *  --- V46 多目标追踪聚合特征 ---
 *  18 攻击目标数量 /4
 *  19 第二近目标距离 /16
 *  20 第二近目标血量比例
 *  21 是否存在任意目标正在攻击（0/1）
 *  22 是否存在任意目标处于危险范围（0/1）
 *  23 是否需要闪避/格挡（最近危险目标）
 *  24 是否存在低血量目标（0/1）
 *  25 是否存在带增益效果的目标（0/1）
 *  26 是否可末影珍珠/阎魔刀瞬移接近（0/1）
 *  27 总威胁度归一化
 *  28 最近目标护甲值 /20
 *  29 最近目标技能阶段 /4
 *  30 最近目标是否正在使用技能（0/1）
 *  31 是否存在任意目标伤害技能覆盖女仆位置（0/1）
 * </pre>
 */
public final class RlState {

    /** V46：18 维基础态势 + 2 执行反馈 + 14 维多目标追踪特征 */
    public static final int STATE_DIM = 32;
    /** 旧版无执行反馈的状态维度（模型兼容判定用） */
    public static final int LEGACY_STATE_DIM = 16;
    /** V13~V45 的 18 维模型（基础态势 + 执行反馈，无多目标特征） */
    public static final int OLD_STATE_DIM_18 = 18;

    private RlState() {
    }

    public static float[] collect(MaidPatch<?> patch) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        float[] s = new float[STATE_DIM];

        LivingEntity target = patch.getTarget();
        // 双通道目标获取：patch.getTarget()（EpicFight 战斗目标）恒为 null 时回退到 TLM brain 目标
        if (target == null || !target.isAlive()) {
            target = maid.getTarget();
        }
        // 逻辑目标兜底：brain 目标被 TLM 每 tick 清除时，直接取最近标靶（决策状态保持真实战场）
        if (target == null || !target.isAlive()) {
            target = org.eftlm.stylish.arena.AutoArena.findNearestTarget(maid);
        }
        boolean hasTarget = target != null && target.isAlive();

        // 0 目标距离
        double dist = hasTarget ? maid.distanceTo(target) : 16.0;
        s[0] = (float) Math.min(1.0, dist / 16.0);
        // 1 目标血量比例
        s[1] = hasTarget ? target.getHealth() / Math.max(1.0F, target.getMaxHealth()) : 0.0F;
        // 2 女仆血量比例
        s[2] = maid.getHealth() / Math.max(1.0F, maid.getMaxHealth());
        // 3 耐力比例
        float maxStamina = patch.getMaxStamina();
        s[3] = maxStamina > 0 ? patch.getStamina() / maxStamina : 0.0F;
        // 4 华丽度
        s[4] = StyleState.getFlair(maid) / 100.0F;
        // 5 风格
        s[5] = StyleState.getStyle(maid) == AnimKit.STYLE_GUNSLINGER ? 1.0F : 0.0F;
        // 6 武器类型
        WeaponArsenal.Kind kind = WeaponArsenal.classify(maid.getMainHandItem());
        s[6] = switch (kind) {
            case MELEE -> 0.0F;
            case BOW, CROSSBOW -> 1.0F;
            case GUN -> 2.0F;
            default -> 3.0F;
        };
        // 7 目标攻击中
        if (hasTarget) {
            LivingEntityPatch<?> targetPatch = EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
            if (targetPatch != null) {
                int phase = targetPatch.getEntityState().getLevel();
                s[7] = phase > 0 && phase < 3 ? 1.0F : 0.0F;
            } else {
                s[7] = target.swingTime > 0 || target.isUsingItem() ? 1.0F : 0.0F;
            }
        }
        // 8 目标浮空
        s[8] = hasTarget && !target.onGround() ? 1.0F : 0.0F;
        // 9 被击倒
        s[9] = patch.getEntityState().knockDown() ? 1.0F : 0.0F;
        // 10 箭矢即将命中（由箭矢反应模块在最近 10 tick 内触发过反应视为有威胁）
        s[10] = maid.tickCount - StyleState.getTick(maid, StyleState.LAST_ARROW_REACT) <= 10 ? 1.0F : 0.0F;
        // 11 连段命中计数
        s[11] = Math.min(1.0F, StyleState.getInt(maid, StyleState.HIT_COUNT, 0) / (float) StyleState.HIT_COUNT_SWAP);
        // 12 技能层数
        s[12] = StyleState.getSkillStack(maid) / (float) StyleState.MAX_SKILL_STACK;
        // 13 背包可用远程武器
        s[13] = WeaponArsenal.hasUsableRanged(maid) ? 1.0F : 0.0F;
        // 14 主手冷却
        var stack = maid.getMainHandItem();
        s[14] = !stack.isEmpty() && maid.getCooldowns().isOnCooldown(stack.getItem()) ? 1.0F : 0.0F;
        // 15 距上次武器切换
        s[15] = Math.min(1.0F, (maid.tickCount - StyleState.getTick(maid, StyleState.LAST_MELEE_SWAP)) / 30.0F);
        // 16 上次行动成功执行（10 tick 窗口）——RL 执行反馈（闭环：执行结果 → 状态输入）
        s[16] = maid.tickCount - StyleState.getTick(maid, StyleState.LAST_EXEC_OK) <= 10 ? 1.0F : 0.0F;
        // 17 上次行动被拒（忙/无效/失败，10 tick 窗口）——RL 执行反馈
        s[17] = maid.tickCount - StyleState.getTick(maid, StyleState.LAST_EXEC_REJECTED) <= 10 ? 1.0F : 0.0F;

        // 18..31 多目标追踪聚合特征（由 TargetTracker 每帧维护）
        float[] multi = TargetTracker.collectFeatures(maid);
        System.arraycopy(multi, 0, s, 18, multi.length);

        return s;
    }
}
