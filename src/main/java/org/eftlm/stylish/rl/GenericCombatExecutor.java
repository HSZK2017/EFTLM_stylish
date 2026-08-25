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

    @Override
    public List<RlActionSlot> available(MaidPatch<?> patch, int tick) {
        List<RlActionSlot> slots = new ArrayList<>(RlActEvent.NUM_ACTIONS);
        for (int i = 0; i < RlActEvent.NUM_ACTIONS; i++) {
            slots.add(RlActionSlot.generic(i, LABELS[i]));
        }
        return slots;
    }

    @Override
    public boolean canExecute(MaidPatch<?> patch, RlActionSlot slot) {
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
            case RlActEvent.ACT_SWORDMASTER_ATK -> swordmasterAttack(patch);
            case RlActEvent.ACT_GUNSLINGER_ATK -> gunslingerAttack(patch);
            case RlActEvent.ACT_ULTIMATE -> CombatActions.releaseUltimate(patch);
            case RlActEvent.ACT_JC -> jcAttack(patch);
            case RlActEvent.ACT_PARRY -> CombatActions.parry(patch);
            case RlActEvent.ACT_BLOCK -> CombatActions.block(patch);
            case RlActEvent.ACT_DODGE -> CombatActions.dodgeRandom(patch);
            case RlActEvent.ACT_ROLL -> CombatActions.rollRecovery(patch);
            case RlActEvent.ACT_CYCLE_MELEE -> CombatActions.cycleWeapon(patch);
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
                CombatActions.startRangedAim(patch);
            }
            default -> {
                return RlExecResult.REJECTED_INVALID;
            }
        }
        return RlExecResult.EXECUTED;
    }

    private static void swordmasterAttack(MaidPatch<?> patch) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        // 无有效目标不攻击（V17：执行侧双保险，杜绝空挥）
        if (patch.getTarget() == null || !patch.getTarget().isAlive()) {
            return;
        }
        ItemStack mainHand = maid.getMainHandItem();
        // 枪械（EnderBlaster 等）：近战攻击使用枪械自身的近战体术（AUTO/DASH），而非通用剑招
        if (WeaponArsenal.isGun(mainHand)) {
            List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> melee =
                    AnimKit.gunMeleeMoves(CombatActions.isTwoHand(patch, mainHand));
            playFirst(patch, melee);
            return;
        }
        WeaponCategory category = AnimKit.categoryOf(patch);
        List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> pool = AnimKit.swordmasterMoves(category);
        playFirst(patch, pool);
    }

    private static void gunslingerAttack(MaidPatch<?> patch) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        // 无有效目标不攻击（V17：执行侧双保险，杜绝空挥）
        if (patch.getTarget() == null || !patch.getTarget().isAlive()) {
            return;
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
            pool = AnimKit.gunslingerMoves(category);
        }
        playFirst(patch, pool);
    }

    private static void jcAttack(MaidPatch<?> patch) {
        LivingEntity target = patch.getTarget();
        if (target == null || target.onGround()) {
            return; // 目标未浮空不空挥
        }
        WeaponCategory category = AnimKit.categoryOf(patch);
        patch.playAnimationSynchronized(AnimKit.airSlash(category), 0F);
        // 注意：不伪造 LAST_HIT——播放 JC 动画不代表命中，伪造会使连击熔断计时
        // 与华丽度衰减被"空挥"续命，训练数据与真实命中脱钩
    }

    private static void playFirst(MaidPatch<?> patch, List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> pool) {
        if (pool.isEmpty()) {
            return;
        }
        patch.playAnimationSynchronized(pool.get(0), 0F);
    }
}
