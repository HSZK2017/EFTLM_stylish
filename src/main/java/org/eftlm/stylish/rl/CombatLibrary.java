package org.eftlm.stylish.rl;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.EFTLM.EF.Capability.MaidPatch;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eftlm.stylish.compat.efn.EfnSkillCatalog;
import org.eftlm.stylish.compat.efn.SkillSpec;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * P5.6 自适应战斗学习（经验驱动的招式调度器，规则层）。
 * <p>
 * 设计目标：让女仆在对抗中<b>积累三项可泛化的战斗经验</b>，并据此动态调度自身武器技能库：
 * <ol>
 *     <li><b>命中经验（极坐标扇区桶）</b>：每次命中把"出招起点→目标位置"的距离与相对朝向角
 *         量化到 (距离档 × 角度扇区) 桶（{@link #recordHit}）；出招前查桶
 *         （{@link #canReach}）——目标当前处于历史命中过的桶才判定"能命中"。
 *         相比静态技能射程，这是对当前对手移动习惯的经验射程，越打越准；
 *         桶为空 = 无经验 = 不限制（探索期全开放）。</li>
 *     <li><b>敌方节奏统计（攻击间隔 EMA）</b>：观察目标攻击动画的切换时刻，用指数滑动平均
 *         估计其连段间隔（{@link #isEnemyCombing} = 距上次攻击 ≤ 1.5×平均间隔 且 连段计数 ≥2；
 *         {@link #isEnemyComboEnded} = 距上次攻击 ≥ 2×平均间隔 的空窗）。节奏估计随时间
 *         自适应（敌方换风格自动收敛），供决策避让与反击窗口判断。</li>
 *     <li><b>热度衰减调度</b>：自适应规则模式（影子女仆 / 自我博弈对手）不推理 RL，
 *         按综合分动态安排<b>自身武器技能库</b>：综合分 = 经验分（命中桶丰富度 + 当前可命中
 *         + 空窗大技能加成）× 热度系数（越久未用越高——旧招自然复活；刚用过则降权——
 *         天然成链不重复）。</li>
 *     <li><b>增益窃取（时长截断）</b>：仅复制目标<b>剩余时长足够</b>（≥100t）且<b>不降级自身</b>
 *         的 BENEFICIAL 效果（自身同类效果剩余更长则跳过）——可配置
 *         {@code buff_steal=auto|on|off}，默认 auto 仅竞技场训练采集时启用（加快训练速度）。</li>
 * </ol>
 * 训练契约保持：调度出招仅自适应规则模式（不进 RL 决策/轨迹标签）；命中经验只作用推理期
 * 动作掩码（不进 32 维状态）；节奏统计/窃取只产生 trace 事件
 * （{@code copy_learn} / {@code hitgrid} / {@code buff_steal} / {@code adaptive_dispatch}）。
 */
public final class CombatLibrary {

    private static final Logger LOGGER = LogManager.getLogger("eftlm_stylish");

    /** 命中经验桶：距离档（格，0..{@value #MAX_DIST_BIN}）× 角度扇区（0..{@value #SECTORS}-1，每 45°） */
    public record HitBin(int dist, int sector, int hits) {
    }

    /** 角度扇区数（45° 每扇） */
    private static final int SECTORS = 8;
    /** 距离档上限（格；超出按最远档计） */
    private static final int MAX_DIST_BIN = 8;
    /** 命中判定 Y 差容忍（格） */
    private static final double HIT_Y_TOLERANCE = 2.0;
    /** 命中经验：动画注册名 → 命中桶列表（去重计数） */
    private static final Map<ResourceLocation, List<HitBin>> HIT_EXP = new HashMap<>();

    /** 节奏观察节流（tick） */
    private static final int OBSERVE_INTERVAL = 10;
    /** 增益窃取节流（tick） */
    private static final int BUFF_INTERVAL = 20;
    /** 规则模式调度出招节流（tick） */
    private static final int DISPATCH_INTERVAL = 20;
    /** 节奏 EMA 衰减系数（新样本权重） */
    private static final float EMA_ALPHA = 0.3F;
    /** 节奏样本有效窗口（tick；间隔过大视为脱离战斗，不更新 EMA） */
    private static final int TEMPO_WINDOW = 200;
    /** 连段判定：距上次攻击 ≤ 平均间隔 × 该系数 */
    private static final float COMBO_GAP_FACTOR = 1.5F;
    /** 空窗判定：距上次攻击 ≥ 平均间隔 × 该系数 */
    private static final float COMBO_END_FACTOR = 2.0F;
    /** 连段计数达到该值才判定"连段中" */
    private static final int COMBO_MIN_STREAK = 2;
    /** 连段中掩码的长后摇技能阈值（秒） */
    private static final float MASK_RECOVERY_SEC = 0.8F;
    /** 窃取时长门槛（tick）：剩余更短的效果不偷 */
    private static final int STEAL_MIN_DURATION = 100;
    /** 热度衰减：未使用超过该秒数后热度收益封顶 */
    private static final int HEAT_SATURATE_SEC = 30;
    /** 刚使用过该技能（tick 内）降权系数 */
    private static final float FRESH_PENALTY = 0.5F;
    /** 刚使用判定窗口（tick） */
    private static final int FRESH_WINDOW = 5;

    /** 每女仆状态 */
    private static final class MaidState {
        // 出招起点（命中结算用）
        Vec3 attackStartPos;
        float attackStartYRot;
        // 节奏统计
        long lastAttackTick = -1;
        float avgGap = 0.0F;
        int streak = 0;
        ResourceLocation lastAnim;
        // 调度热度
        final Map<ResourceLocation, Long> lastUsed = new HashMap<>();
        // 节流
        int observeUntil = 0;
        int buffUntil = 0;
        int dispatchUntil = 0;
    }

    private static final Map<UUID, MaidState> STATES = new HashMap<>();

    private CombatLibrary() {
    }

    /** 每 tick 调用（技能 MaidTick）：节奏统计 / 增益窃取 / 规则模式调度出招（各自节流） */
    public static void tick(EntityMaid maid, MaidPatch<?> patch) {
        if (!isRuleControlled(maid) && !RlConfig.adaptiveLearn) {
            return; // 常规女仆仅在学习开启时做节奏统计与窃取
        }
        int tick = maid.tickCount;
        MaidState st = STATES.computeIfAbsent(maid.getUUID(), k -> new MaidState());
        if (tick >= st.observeUntil) {
            st.observeUntil = tick + OBSERVE_INTERVAL;
            observeTempo(maid, patch);
        }
        if (tick >= st.buffUntil) {
            st.buffUntil = tick + BUFF_INTERVAL;
            stealBuffs(maid, patch);
        }
        if (isRuleControlled(maid) && tick >= st.dispatchUntil) {
            st.dispatchUntil = tick + DISPATCH_INTERVAL;
            tryDispatchSkill(maid, patch);
        }
    }

    /** 规则控制判定：自适应影子女仆（shadow_ai=adaptive）或自我博弈对手女仆 */
    public static boolean isRuleControlled(EntityMaid maid) {
        return org.eftlm.stylish.arena.AutoArena.isAdaptiveShadow(maid)
                || org.eftlm.stylish.arena.AutoArena.isAdaptiveMaid(maid);
    }

    // ------------------------------------------------------------------
    // 敌方节奏统计（攻击间隔 EMA，随对手风格自适应）
    // ------------------------------------------------------------------

    private static void observeTempo(EntityMaid maid, MaidPatch<?> patch) {
        LivingEntity target = patch.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        try {
            LivingEntityPatch<?> tp = EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
            if (tp == null) {
                return;
            }
            var player = tp.getAnimator().getPlayerFor(null);
            if (player == null) {
                return;
            }
            var real = player.getRealAnimation();
            if (real == null || real.get() == null || !(real.get() instanceof AttackAnimation attack)) {
                return; // 只统计攻击动画
            }
            ResourceLocation key = attack.getRegistryName();
            if (key == null) {
                return;
            }
            MaidState st = STATES.computeIfAbsent(maid.getUUID(), k -> new MaidState());
            if (key.equals(st.lastAnim)) {
                return; // 同一动画连续播放 = 同一次攻击，不重复采样
            }
            int tick = maid.tickCount;
            if (st.lastAttackTick >= 0) {
                long gap = tick - st.lastAttackTick;
                if (gap <= TEMPO_WINDOW) {
                    st.avgGap = st.avgGap <= 0.0F ? gap : EMA_ALPHA * gap + (1 - EMA_ALPHA) * st.avgGap;
                    st.streak = gap <= st.avgGap * COMBO_GAP_FACTOR ? st.streak + 1 : 1;
                }
            }
            st.lastAttackTick = tick;
            st.lastAnim = key;
            RlTrace.event(maid, "copy_learn",
                    "enemy_anim=" + key + " avg_gap=" + String.format("%.0f", st.avgGap)
                            + "t streak=" + st.streak + " combing=" + isEnemyCombing(maid));
        } catch (Throwable ignored) {
            // 第三方动画结构异常不影响战斗
        }
    }

    /** 敌方连段中：连段计数达标且距上次攻击 ≤ 平均间隔 × 系数 */
    public static boolean isEnemyCombing(EntityMaid maid) {
        MaidState st = STATES.get(maid.getUUID());
        if (st == null || st.lastAttackTick < 0 || st.streak < COMBO_MIN_STREAK || st.avgGap <= 0.0F) {
            return false;
        }
        return maid.tickCount - st.lastAttackTick <= st.avgGap * COMBO_GAP_FACTOR;
    }

    /** 敌方连段空窗：距上次攻击 ≥ 平均间隔 × 系数（后摇结束的反击窗口） */
    public static boolean isEnemyComboEnded(EntityMaid maid) {
        MaidState st = STATES.get(maid.getUUID());
        if (st == null || st.lastAttackTick < 0 || st.avgGap <= 0.0F) {
            return false;
        }
        long since = maid.tickCount - st.lastAttackTick;
        return since >= st.avgGap * COMBO_END_FACTOR && since <= TEMPO_WINDOW;
    }

    // ------------------------------------------------------------------
    // 增益窃取（时长截断 + 不降级自身）
    // ------------------------------------------------------------------

    private static void stealBuffs(EntityMaid maid, MaidPatch<?> patch) {
        if (!buffStealActive(maid)) {
            return;
        }
        LivingEntity target = patch.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        boolean stole = false;
        for (MobEffectInstance effect : target.getActiveEffects()) {
            if (effect.getEffect().getCategory() != MobEffectCategory.BENEFICIAL
                    || effect.getDuration() < STEAL_MIN_DURATION) {
                continue; // 只偷"值得偷"的增益
            }
            MobEffectInstance own = maid.getEffect(effect.getEffect());
            if (own != null && own.getDuration() >= effect.getDuration()) {
                continue; // 自身同类效果剩余更长 → 不降级
            }
            MobEffectInstance copy = new MobEffectInstance(effect.getEffect(),
                    effect.getDuration(), effect.getAmplifier(), false, false);
            if (maid.addEffect(copy)) {
                stole = true;
            }
        }
        if (stole) {
            RlTrace.event(maid, "buff_steal", "stole buffs from "
                    + target.getType().getDescriptionId());
        }
    }

    /** buff_steal=on 恒开；auto（默认）= 仅竞技场训练采集女仆启用（加快训练速度） */
    private static boolean buffStealActive(EntityMaid maid) {
        String mode = RlConfig.buffSteal;
        if ("on".equals(mode)) {
            return true;
        }
        return "auto".equals(mode) && org.eftlm.stylish.arena.AutoArena.isArenaMaid(maid);
    }

    // ------------------------------------------------------------------
    // 命中经验（极坐标扇区桶）
    // ------------------------------------------------------------------

    /** 出招标记：执行器/调度出招播放动画前记录起点（命中回调据此结算） */
    public static void markAttack(EntityMaid maid) {
        MaidState st = STATES.computeIfAbsent(maid.getUUID(), k -> new MaidState());
        st.attackStartPos = maid.position();
        st.attackStartYRot = maid.getYRot();
    }

    /** 命中回调（onHurtTargetPost）：把本次命中量化到 (距离档, 角度扇区) 桶 */
    public static void onHit(EntityMaid maid, LivingEntity target) {
        if (!RlConfig.adaptiveLearn || target == null) {
            return;
        }
        MaidState st = STATES.get(maid.getUUID());
        if (st == null || st.attackStartPos == null) {
            return;
        }
        try {
            MaidPatch<?> patch = EpicFightCapabilities.getEntityPatch(maid, MaidPatch.class);
            if (patch == null) {
                return;
            }
            var player = patch.getAnimator().getPlayerFor(null);
            if (player == null) {
                return;
            }
            var real = player.getRealAnimation();
            if (real == null || real.get() == null || real.get().getRegistryName() == null) {
                return;
            }
            ResourceLocation key = real.get().getRegistryName();
            boolean added = recordHit(key, st.attackStartPos, st.attackStartYRot, target.position());
            if (added) {
                RlTrace.event(maid, "hitgrid",
                        "anim=" + key + " bins=" + HIT_EXP.get(key).size());
            }
            st.attackStartPos = null; // 同一出招只结算一次
        } catch (Throwable ignored) {
            // 动画结构异常不影响战斗
        }
    }

    /** 命中点 → 桶（去重计数）：距离档 = 水平距离取整；扇区 = 相对朝向角 / 45° */
    private static boolean recordHit(ResourceLocation key, Vec3 startPos, float startYRot, Vec3 targetPos) {
        double dx = targetPos.x - startPos.x;
        double dz = targetPos.z - startPos.z;
        int dist = (int) Math.round(Math.sqrt(dx * dx + dz * dz));
        if (dist > MAX_DIST_BIN) {
            dist = MAX_DIST_BIN;
        }
        // 相对朝向角：atan2(目标相对自身朝向的方位, 前后分量)，归一化到 [0, 360)
        double yawRad = Math.toRadians(startYRot);
        double forwardX = -Math.sin(yawRad);
        double forwardZ = Math.cos(yawRad);
        double rightX = forwardZ;
        double rightZ = -forwardX;
        double along = dx * forwardX + dz * forwardZ;
        double lateral = dx * rightX + dz * rightZ;
        double angle = Math.toDegrees(Math.atan2(lateral, along));
        if (angle < 0) {
            angle += 360.0;
        }
        int sector = (int) (angle / (360.0 / SECTORS)) % SECTORS;
        List<HitBin> bins = HIT_EXP.computeIfAbsent(key, k -> new ArrayList<>());
        for (HitBin b : bins) {
            if (b.dist() == dist && b.sector() == sector) {
                return false; // 已有该桶
            }
        }
        bins.add(new HitBin(dist, sector, 1));
        return true;
    }

    /** 目标当前位置是否落在该动画的历史命中桶（Y 差 ≤2；桶为空 = 无经验，不限制） */
    public static boolean canReach(EntityMaid maid, ResourceLocation key, LivingEntity target) {
        List<HitBin> bins = HIT_EXP.get(key);
        if (bins == null || bins.isEmpty() || target == null) {
            return true;
        }
        if (Math.abs(target.getY() - maid.getY()) > HIT_Y_TOLERANCE) {
            return false;
        }
        double dx = target.getX() - maid.getX();
        double dz = target.getZ() - maid.getZ();
        int dist = (int) Math.round(Math.sqrt(dx * dx + dz * dz));
        if (dist > MAX_DIST_BIN) {
            dist = MAX_DIST_BIN;
        }
        double yawRad = Math.toRadians(maid.getYRot());
        double forwardX = -Math.sin(yawRad);
        double forwardZ = Math.cos(yawRad);
        double rightX = forwardZ;
        double rightZ = -forwardX;
        double along = dx * forwardX + dz * forwardZ;
        double lateral = dx * rightX + dz * rightZ;
        double angle = Math.toDegrees(Math.atan2(lateral, along));
        if (angle < 0) {
            angle += 360.0;
        }
        int sector = (int) (angle / (360.0 / SECTORS)) % SECTORS;
        for (HitBin b : bins) {
            if (b.dist() == dist && b.sector() == sector) {
                return true;
            }
        }
        return false;
    }

    /**
     * 推理期动作掩码：
     * <ol>
     *     <li>技能动画已有命中经验但目标不在任何历史命中桶 → 置 0（经验射程外）；</li>
     *     <li>敌方连段中且技能后摇过长（≥ {@value #MASK_RECOVERY_SEC}s）→ 置 0
     *         （连段中放长后摇技能易被打断）；</li>
     *     <li>敌方空窗 → 不额外掩码（反击窗口开放）。</li>
     * </ol>
     */
    public static boolean shouldMaskSlot(EntityMaid maid, RlActionSlot slot, LivingEntity target) {
        if (!RlConfig.adaptiveHitgrid || slot.skill() == null || target == null || !target.isAlive()) {
            return false;
        }
        try {
            ResourceLocation key = ResourceLocation.parse(slot.skill().animKey());
            if (!canReach(maid, key, target)) {
                return true;
            }
            if (isEnemyCombing(maid)) {
                var frame = org.eftlm.stylish.rl.CommitmentCatalog.of(slot.skill());
                if (frame != null && frame.recoverySec() >= MASK_RECOVERY_SEC) {
                    return true;
                }
            }
            return false;
        } catch (Throwable ignored) {
            return false; // 非法动画键不掩码
        }
    }

    // ------------------------------------------------------------------
    // 规则模式调度（热度衰减加权，自身武器技能库）
    // ------------------------------------------------------------------

    /** 经验分：命中桶丰富度 + 当前可命中 + 空窗大技能加成 */
    private static int expScore(EntityMaid maid, SkillSpec spec, LivingEntity target) {
        int score = 0;
        try {
            ResourceLocation key = ResourceLocation.parse(spec.animKey());
            List<HitBin> bins = HIT_EXP.get(key);
            if (bins != null) {
                score += Math.min(bins.size(), 5); // 桶丰富度 0~5
            }
            if (target != null && canReach(maid, key, target)) {
                score += 3;
            }
        } catch (Throwable ignored) {
        }
        if (isEnemyComboEnded(maid)) {
            var frame = org.eftlm.stylish.rl.CommitmentCatalog.of(spec);
            if (frame != null && frame.recoverySec() >= MASK_RECOVERY_SEC) {
                score += 2; // 空窗反击：长后摇大技能
            }
        }
        return score;
    }

    /** 热度系数：越久未用越高（旧招复活）；刚用过降权（防连打同一招） */
    private static float heatFactor(MaidState st, ResourceLocation key, int tick) {
        Long used = st.lastUsed.get(key);
        if (used == null) {
            return 1.0F; // 从未用过：正常权重
        }
        long idle = (tick - used) / 20L; // 秒
        if (idle < 1L) {
            return FRESH_PENALTY; // 1 秒内刚用过：降权，鼓励换招
        }
        float boost = (float) Math.min(idle, HEAT_SATURATE_SEC) / HEAT_SATURATE_SEC * 0.5F;
        return 1.0F + boost;
    }

    /** 规则模式调度出招：综合分 = 经验分 × 热度系数，最高者播放（不进 RL 决策，trace 观测） */
    private static void tryDispatchSkill(EntityMaid maid, MaidPatch<?> patch) {
        if (!patch.getEntityState().canBasicAttack()) {
            return;
        }
        LivingEntity target = patch.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        List<SkillSpec> candidates = new ArrayList<>();
        for (SkillSpec spec : EfnSkillCatalog.stableSkills(patch)) {
            if (EfnSkillCatalog.isAvailable(patch, spec)) {
                candidates.add(spec);
            }
        }
        if (candidates.isEmpty()) {
            return;
        }
        int tick = maid.tickCount;
        MaidState st = STATES.computeIfAbsent(maid.getUUID(), k -> new MaidState());
        Comparator<SkillSpec> byScore = Comparator.comparingDouble((SkillSpec s) -> {
            ResourceLocation key = ResourceLocation.parse(s.animKey());
            return expScore(maid, s, target) * heatFactor(st, key, tick);
        }).reversed();
        candidates.sort(byScore);
        SkillSpec play = candidates.get(0);
        if (EfnSkillCatalog.release(patch, play)) {
            EfnSkillCatalog.markUsed(maid, play, tick);
            st.lastUsed.put(ResourceLocation.parse(play.animKey()), (long) tick);
            markAttack(maid);
            RlTrace.event(maid, "adaptive_dispatch",
                    "skill=" + play.id()
                            + " exp=" + expScore(maid, play, target)
                            + " enemy_combing=" + isEnemyCombing(maid)
                            + " enemy_ended=" + isEnemyComboEnded(maid));
        }
    }

    // ------------------------------------------------------------------
    // 运维
    // ------------------------------------------------------------------

    /** /rl adaptive 状态查询 */
    public static String status() {
        StringBuilder sb = new StringBuilder("[rl] adaptive:");
        sb.append("\n  learn=").append(RlConfig.adaptiveLearn)
                .append(" hitgrid=").append(RlConfig.adaptiveHitgrid)
                .append(" buff_steal=").append(RlConfig.buffSteal)
                .append(" shadow_ai=").append(org.eftlm.stylish.arena.AutoArena.shadowAiMode())
                .append(" selfplay=").append(org.eftlm.stylish.arena.AutoArena.selfplayMode());
        sb.append("\n  hit_bins=").append(HIT_EXP.size());
        int total = 0;
        for (List<HitBin> bins : HIT_EXP.values()) {
            total += bins.size();
        }
        sb.append(" anims, bins=").append(total);
        return sb.toString();
    }

    /** 女仆移除时清理状态 */
    public static void forget(UUID id) {
        STATES.remove(id);
    }

    /** 清空全局命中经验与状态（调试用） */
    public static synchronized void reset() {
        HIT_EXP.clear();
        STATES.clear();
    }
}
