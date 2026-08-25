package org.eftlm.stylish.strategy;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.EFTLM.EF.Capability.MaidPatch;
import org.eftlm.stylish.util.AnimKit;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

/**
 * 华丽连段行为表条件（CombatBehaviors Behavior.custom 谓词）。
 */
public final class StylishConditions {

    /** 防守触发距离 */
    private static final double DEFENSE_RANGE = 3.4;
    /** 近战攻击距离 */
    private static final double MELEE_RANGE = 3.5;
    /** 浮空 JC 距离 */
    private static final double AIR_RANGE = 4.0;
    /** 枪神点射距离 */
    private static final double GUNSLINGER_RANGED_DIST = 5.0;

    private StylishConditions() {
    }

    private static EntityMaid maid(MaidPatch<?> patch) {
        return (EntityMaid) patch.getOriginal();
    }

    /**
     * hybrid 仲裁：RL 模型已加载时，行为表进攻系列（连段/大招/点射）让位给 RL 决策，
     * 防守/浮空系列保留兜底。rl.properties arbitration=none 可恢复共存（旧行为）。
     */
    private static boolean rlDrivesAttacks() {
        return org.eftlm.stylish.rl.RlConfig.rlDrivesAttacks();
    }

    /**
     * 敌人是否正在攻击（EpicFight 相位 1 前摇 / 2 攻击中 / 3 收招）。
     */
    public static boolean isTargetAttacking(MaidPatch<?> patch) {
        var target = patch.getTarget();
        if (target == null) {
            return false;
        }
        LivingEntityPatch<?> targetPatch = EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
        if (targetPatch != null) {
            int phase = targetPatch.getEntityState().getLevel();
            return phase > 0 && phase < 3;
        }
        return target.swingTime > 0 || target.isUsingItem();
    }

    /**
     * 敌人是否浮空（离地且下落中或明显高于女仆）。
     */
    public static boolean isTargetAirborne(MaidPatch<?> patch) {
        var target = patch.getTarget();
        if (target == null || target.onGround()) {
            return false;
        }
        EntityMaid maid = maid(patch);
        return target.getDeltaMovement().y < -0.03 || target.getY() > maid.getY() + 0.5;
    }

    /**
     * 弹反条件：敌人攻击中、近身、且华丽度评价下降（低于 C 级）→ 插入弹反挽回评价。
     */
    public static boolean enemyAttackingLowFlair(MaidPatch<?> patch) {
        if (!isTargetAttacking(patch)) {
            return false;
        }
        EntityMaid maid = maid(patch);
        if (patch.getTarget() == null || maid.distanceTo(patch.getTarget()) >= DEFENSE_RANGE) {
            return false;
        }
        return StyleState.getFlair(maid) < StyleState.FLAIR_C;
    }

    /**
     * 防守条件：敌人攻击中且近身。
     */
    public static boolean enemyAttackingNear(MaidPatch<?> patch) {
        if (!isTargetAttacking(patch)) {
            return false;
        }
        return patch.getTarget() != null && maid(patch).distanceTo(patch.getTarget()) < DEFENSE_RANGE;
    }

    /**
     * 浮空且可切换武器（换武器冷却已过）。
     */
    public static boolean targetAirborneAndCanSwap(MaidPatch<?> patch) {
        if (!isTargetAirborne(patch)) {
            return false;
        }
        EntityMaid maid = maid(patch);
        if (patch.getTarget() == null || maid.distanceTo(patch.getTarget()) >= AIR_RANGE) {
            return false;
        }
        return maid.tickCount - StyleState.getTick(maid, StyleState.LAST_MELEE_SWAP) >= WeaponArsenal.SWAP_COOLDOWN;
    }

    /**
     * 浮空 JC 条件：目标浮空且近身。
     */
    public static boolean targetAirborne(MaidPatch<?> patch) {
        if (!isTargetAirborne(patch)) {
            return false;
        }
        return patch.getTarget() != null && maid(patch).distanceTo(patch.getTarget()) < AIR_RANGE;
    }

    /**
     * 中距离条件（突进战技使用：目标在 2~8 格，位移突进的最佳起手距离）。
     */
    public static boolean inMidRange(MaidPatch<?> patch) {
        var target = patch.getTarget();
        if (target == null || !target.isAlive()) {
            target = org.eftlm.stylish.arena.AutoArena.findNearestTarget(maid(patch));
        }
        if (target == null) {
            return false;
        }
        double d = maid(patch).distanceTo(target);
        return d >= 2.0 && d <= 8.0;
    }

    /**
     * 地面连段条件（不区分风格，EFN 兼容武器使用）。
     */
    public static boolean inMelee(MaidPatch<?> patch) {
        EntityMaid maid = maid(patch);
        return patch.getTarget() != null && maid.distanceTo(patch.getTarget()) < MELEE_RANGE;
    }

    /**
     * 剑圣风格地面连段条件。
     */
    public static boolean swordmasterInMelee(MaidPatch<?> patch) {
        if (rlDrivesAttacks()) {
            return false; // RL 接管进攻决策
        }
        EntityMaid maid = maid(patch);
        if (StyleState.getStyle(maid) != AnimKit.STYLE_SWORDMASTER) {
            return false;
        }
        return patch.getTarget() != null && maid.distanceTo(patch.getTarget()) < MELEE_RANGE;
    }

    /**
     * 枪神风格地面连段条件（近战回退）。
     */
    public static boolean gunslingerInMelee(MaidPatch<?> patch) {
        if (rlDrivesAttacks()) {
            return false; // RL 接管进攻决策
        }
        EntityMaid maid = maid(patch);
        if (StyleState.getStyle(maid) != AnimKit.STYLE_GUNSLINGER) {
            return false;
        }
        return patch.getTarget() != null && maid.distanceTo(patch.getTarget()) < MELEE_RANGE;
    }

    /**
     * 枪神风格远程点射条件：目标较远且背包有可用远程武器。
     */
    public static boolean gunslingerRangedReady(MaidPatch<?> patch) {
        if (rlDrivesAttacks()) {
            return false; // RL 接管进攻决策
        }
        EntityMaid maid = maid(patch);
        if (StyleState.getStyle(maid) != AnimKit.STYLE_GUNSLINGER) {
            return false;
        }
        if (patch.getTarget() == null || maid.distanceTo(patch.getTarget()) < GUNSLINGER_RANGED_DIST) {
            return false;
        }
        return WeaponArsenal.hasUsableRanged(maid);
    }
}
