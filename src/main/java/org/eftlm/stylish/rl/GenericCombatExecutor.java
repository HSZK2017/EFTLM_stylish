package org.eftlm.stylish.rl;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.EFTLM.EF.Capability.MaidPatch;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.eftlm.stylish.strategy.CombatActions;
import org.eftlm.stylish.strategy.WeaponArsenal;
import org.eftlm.stylish.util.AnimKit;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用战斗行动执行器：拥有固定行动段 0..{@link RlActEvent#NUM_ACTIONS}-1
 * （待机 / 剑圣攻击 / 枪神攻击 / 大招 / JC / 弹反 / 格挡 / 闪避 / 翻滚 / 轮换近战 / 切远程）。
 * 原 RlActHandler 的 switch 分发逻辑迁移至此。
 */
public final class GenericCombatExecutor implements RlActionExecutor {

    public static final String ID = "generic";

    /** 通用行动标签（P3 /rl layout 导出用） */
    public static final String[] LABELS = {
            "idle", "swordmaster_atk", "gunslinger_atk", "ultimate", "jc",
            "parry", "block", "dodge", "roll", "cycle_melee", "ranged"
    };

    @Override
    public String id() {
        return ID;
    }

    /**
     * 通用行动可用性（2026-09-10 修复：履行 {@link RlActionExecutor#available} 的契约）。
     * <p>
     * 旧实现无条件返回全部 11 个通用行动，于是模型会反复选择"冷却中/耐力不足/目标不满足"的行动，
     * 被执行器拒绝（现场实测：dodge 100%、roll 99%、cycle_melee 100%、idle 268/269 被拒）。
     * 现在不可用的行动以 {@code null} 占位（**必须占位**：{@code RlActionRegistry.buildLayout}
     * 按顺序写入固定段，跳过条目会让行动编号整体错位、破坏模型契约）。
     * <p>
     * IDLE 恒可用：无操作在任何时刻都是合法选择，否则"忙"时连等待都会被记为拒绝。
     */
    @Override
    public List<RlActionSlot> available(MaidPatch<?> patch, int tick) {
        List<RlActionSlot> slots = new ArrayList<>(RlActEvent.NUM_ACTIONS);
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        net.minecraft.world.entity.LivingEntity target = patch.getTarget();
        boolean hasTarget = target != null && target.isAlive();
        // 无目标时不提供防守动作（没有对手可防）；IDLE 仍是合法选择
        boolean defensible = hasTarget;
        WeaponArsenal.Kind kind = WeaponArsenal.classify(maid.getMainHandItem());
        boolean holdingRanged = kind == WeaponArsenal.Kind.GUN || kind == WeaponArsenal.Kind.BOW
                || kind == WeaponArsenal.Kind.CROSSBOW;
        for (int i = 0; i < RlActEvent.NUM_ACTIONS; i++) {
            boolean usable = switch (i) {
                case RlActEvent.ACT_IDLE -> true;                       // 恒可用（等待）
                case RlActEvent.ACT_SWORDMASTER_ATK, RlActEvent.ACT_GUNSLINGER_ATK -> hasTarget;
                case RlActEvent.ACT_ULTIMATE -> true;                   // 大招动画始终可播（层数由奖励引导）
                case RlActEvent.ACT_JC -> hasTarget && !target.onGround();
                case RlActEvent.ACT_PARRY -> defensible && CombatActions.canParry(maid); // 15t 冷却
                case RlActEvent.ACT_BLOCK -> defensible && CombatActions.canBlock(maid);
                case RlActEvent.ACT_DODGE -> CombatActions.canDodge(patch);
                case RlActEvent.ACT_ROLL -> CombatActions.canRoll(patch);
                case RlActEvent.ACT_CYCLE_MELEE -> CombatActions.canCycleWeapon(patch);
                case RlActEvent.ACT_RANGED -> holdingRanged && !RlBrain.isRangedStreakDead(maid);
                default -> false;
            };
            slots.add(usable ? RlActionSlot.generic(i, LABELS[i]) : null);
        }
        return slots;
    }

    @Override
    public boolean canExecute(MaidPatch<?> patch, RlActionSlot slot) {
        // IDLE = 无操作：任何时刻合法（含"自己正在播不可打断动画"），否则等待也会被记为拒绝
        if (slot.localId() == RlActEvent.ACT_IDLE) {
            return true;
        }
        // P2：Commitment 门控（CommitmentCatalog.canExecuteNow）——空闲可执行；
        // 动画即将结束（剩余 ≤2 tick）/受击倒地动画可执行；紧急行动（翻滚/闪避/弹反）直放；
        // 其余长动画活性帧拒绝（RL 反馈 s[17] 学习择时）。
        boolean urgent = slot.localId() == RlActEvent.ACT_ROLL
                || slot.localId() == RlActEvent.ACT_DODGE
                || slot.localId() == RlActEvent.ACT_PARRY;
        return CommitmentCatalog.canExecuteNow(patch, urgent);
    }

    @Override
    public RlExecResult execute(MaidPatch<?> patch, RlActionSlot slot) {
        switch (slot.localId()) {
            case RlActEvent.ACT_IDLE -> {
                // 无操作：行为表（AnimatedAttackGoal）继续自动连段
                return RlExecResult.NOOP;
            }
            // P1 修复（2026-09-10）：这些分支改为返回真实结果——旧实现是无条件 EXECUTED，
            // 于是"无目标空挥 / 动画池为空"也被记为成功，污染 s[16] 与训练奖励。
            case RlActEvent.ACT_SWORDMASTER_ATK -> {
                return swordmasterAttack(patch) ? RlExecResult.EXECUTED : RlExecResult.REJECTED_INVALID;
            }
            case RlActEvent.ACT_GUNSLINGER_ATK -> {
                return gunslingerAttack(patch) ? RlExecResult.EXECUTED : RlExecResult.REJECTED_INVALID;
            }
            case RlActEvent.ACT_ULTIMATE -> {
                return CombatActions.releaseUltimate(patch) ? RlExecResult.EXECUTED : RlExecResult.REJECTED_INVALID;
            }
            case RlActEvent.ACT_JC -> {
                return jcAttack(patch) ? RlExecResult.EXECUTED : RlExecResult.REJECTED_INVALID;
            }
            case RlActEvent.ACT_PARRY -> {
                return CombatActions.parry(patch) ? RlExecResult.EXECUTED : RlExecResult.REJECTED_INVALID;
            }
            case RlActEvent.ACT_BLOCK -> {
                return CombatActions.block(patch) ? RlExecResult.EXECUTED : RlExecResult.REJECTED_INVALID;
            }
            case RlActEvent.ACT_DODGE -> {
                // 耐力不足时 dodgeRandom 返回 false（P1 修复：不再谎报成功）
                return CombatActions.dodgeRandom(patch) ? RlExecResult.EXECUTED : RlExecResult.REJECTED_BUSY;
            }
            case RlActEvent.ACT_ROLL -> {
                return CombatActions.rollRecovery(patch) ? RlExecResult.EXECUTED : RlExecResult.REJECTED_BUSY;
            }
            case RlActEvent.ACT_CYCLE_MELEE -> {
                return CombatActions.cycleWeapon(patch) ? RlExecResult.EXECUTED : RlExecResult.REJECTED_BUSY;
            }
            case RlActEvent.ACT_RANGED -> {
                // V18：主手非远程（枪/弓弩）不自动换枪射击（未持枪射击 = 行为不匹配）；
                // 连续射击 ≥4 次进入冷却（逼模型分散策略，防纯远程流劣化多样性）
                EntityMaid m = (EntityMaid) patch.getOriginal();
                WeaponArsenal.Kind kind = WeaponArsenal.classify(m.getMainHandItem());
                if (kind != WeaponArsenal.Kind.GUN && kind != WeaponArsenal.Kind.BOW
                        && kind != WeaponArsenal.Kind.CROSSBOW) {
                    return RlExecResult.REJECTED_INVALID;
                }
                if (org.eftlm.stylish.rl.RlBrain.isRangedStreakDead(m)) {
                    return RlExecResult.REJECTED_BUSY;
                }
                return CombatActions.startRangedAim(patch) ? RlExecResult.EXECUTED : RlExecResult.REJECTED_INVALID;
            }
            default -> {
                return RlExecResult.REJECTED_INVALID;
            }
        }
    }

    /** @return 是否真的播放了招式（无目标 / 无可用动画返回 false） */
    private static boolean swordmasterAttack(MaidPatch<?> patch) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        // 无有效目标不攻击（V17：执行侧双保险，杜绝空挥）
        if (patch.getTarget() == null || !patch.getTarget().isAlive()) {
            return false;
        }
        ItemStack mainHand = maid.getMainHandItem();
        // 枪械（EnderBlaster 等）：近战攻击使用枪械自身的近战体术（AUTO/DASH），而非通用剑招
        if (WeaponArsenal.isGun(mainHand)) {
            List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> melee =
                    AnimKit.gunMeleeMoves(CombatActions.isTwoHand(patch, mainHand));
            return playFirst(patch, melee);
        }
        WeaponCategory category = AnimKit.categoryOf(patch);
        return playFirst(patch, AnimKit.swordmasterMoves(category));
    }

    /** @return 是否真的播放了招式 */
    private static boolean gunslingerAttack(MaidPatch<?> patch) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        // 无有效目标不攻击（V17：执行侧双保险，杜绝空挥）
        if (patch.getTarget() == null || !patch.getTarget().isAlive()) {
            return false;
        }
        ItemStack mainHand = maid.getMainHandItem();
        WeaponCategory category = AnimKit.categoryOf(patch);
        List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> pool;
        if (WeaponArsenal.isGun(mainHand)) {
            pool = AnimKit.gunMoves(CombatActions.isTwoHand(patch, mainHand));
        } else {
            pool = AnimKit.gunslingerMoves(category);
        }
        if (pool.isEmpty()) {
            pool = AnimKit.gunslingerMoves(category); // 兜底：类别池为空时退回通用枪神招式
        }
        return playFirst(patch, pool);
    }

    /** @return 是否真的播放了招式（目标未浮空不空挥） */
    private static boolean jcAttack(MaidPatch<?> patch) {
        LivingEntity target = patch.getTarget();
        if (target == null || target.onGround()) {
            return false;
        }
        WeaponCategory category = AnimKit.categoryOf(patch);
        patch.playAnimationSynchronized(AnimKit.airSlash(category), 0F);
        // 注意：不伪造 LAST_HIT——播放 JC 动画不代表命中，伪造会使连击熔断计时
        // 与华丽度衰减被"空挥"续命，训练数据与真实命中脱钩
        return true;
    }

    /** @return 是否真的播放了动画（池为空返回 false，不再谎报成功） */
    private static boolean playFirst(MaidPatch<?> patch, List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> pool) {
        if (pool.isEmpty()) {
            return false;
        }
        patch.playAnimationSynchronized(pool.get(0), 0F);
        return true;
    }
}
