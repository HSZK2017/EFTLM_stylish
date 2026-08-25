package org.eftlm.stylish.strategy;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.EFTLM.EF.Capability.MaidPatch;
import net.minecraft.world.entity.LivingEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eftlm.stylish.compat.efn.EfnSkillCatalog;
import org.eftlm.stylish.compat.efn.SkillSpec;
import org.eftlm.stylish.rl.RlDataRecorder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 按键类战技程序化模块（V12 → 目录驱动版）：
 * <p>
 * EFN 武器在玩家手中通过技能键释放的 innate 战技（突刺 / 次元斩 / 上挑 / 空中横扫 /
 * 火山等），女仆没有按键机制、EFTLM 行为表也未完整收录 —— 本模块把 {@link EfnSkillCatalog}
 * 枚举出的技能（skills.json，从 EFN jar 动画资产提取）条件化到女仆的每 tick 状态机中：
 * 满足条件即直接播放对应动画（EFN 动画自带伤害 / 位移事件，播放即结算）。
 * <p>
 * 两种模式：
 * <ul>
 *     <li><b>测试模式</b>（arena.properties test_skills=true）：按目录顺序轮流强制释放全部战技，
 *         命中反馈经 {@link #onHit} 记录，日志逐条输出 play=ok / HIT dmg，用于验证战技可用性。</li>
 *     <li><b>战斗模式</b>：按目录条件触发（mid_range 突进优先、airborne 浮空技次之、
 *         melee 其余），冷却与资源门控内不重复。</li>
 * </ul>
 */
public final class AutoSkill {

    private static final Logger LOGGER = LogManager.getLogger("eftlm_stylish");

    /** 测试间隔 tick（每个战技的观察窗口） */
    private static final int TEST_INTERVAL = 100;
    /** 命中判定窗口：战技释放后 N tick 内的命中归为该战技 */
    private static final int HIT_WINDOW = 50;

    private static int testCursor = 0;
    private static long lastTestTick = 0;
    private static volatile String currentTest = null;
    private static volatile int currentTestTick = 0;
    private static final Map<String, Integer> TEST_HITS = new HashMap<>();
    private static final Map<String, Integer> TEST_PLAYED = new HashMap<>();

    private AutoSkill() {
    }

    public static boolean isTestMode() {
        return org.eftlm.stylish.arena.AutoArena.isTestSkills();
    }

    public static void setTestMode(boolean enabled) {
        testCursor = 0;
        lastTestTick = 0;
        currentTest = null;
        TEST_HITS.clear();
        TEST_PLAYED.clear();
        LOGGER.info("[SKILL] test mode {}", enabled ? "ON" : "OFF");
    }

    /**
     * 每 tick 状态机入口（StylishCombatSkill.MaidTick 调用）。
     * <p>
     * 竞技场女仆：训练用，模型加载时技能由 RL 事件总线驱动，本模块仅测试模式 /
     * 无模型规则兜底；非竞技场女仆：需已学习本模组技能书，由规则兜底驱动目录技能
     * （技能书 = 更换为目录驱动的战斗逻辑）。
     */
    public static void tick(MaidPatch<?> patch, EntityMaid maid, int tick) {
        boolean arena = org.eftlm.stylish.arena.AutoArena.isArenaMaid(maid);
        if (!arena && !SkillGate.hasLearnedSkill(patch)) {
            return; // 普通女仆未学习技能书 → 保持原版战斗逻辑
        }
        if (isTestMode()) {
            testTick(patch, maid, tick);
        } else {
            // 模型加载后竞技场女仆技能由 RL 经事件总线驱动；无模型或非竞技场女仆走规则兜底
            if (arena && org.eftlm.stylish.rl.RlBrain.isModelLoaded()) {
                return;
            }
            combatTick(patch, maid, tick);
        }
    }

    // ------------------------------------------------------------------
    // 测试模式：按目录顺序轮流强制释放全部战技，验证可用性
    // ------------------------------------------------------------------

    /** 测试顺序：只使用主手武器固有的目录技能（跨武器播放不匹配技能会白播且伤害不结算） */
    private static List<SkillSpec> testOrder(MaidPatch<?> patch) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        List<SkillSpec> own = EfnSkillCatalog.skillsOf(patch);
        if (own.isEmpty() && maid.tickCount % 200 == 0) {
            LOGGER.info("[SKILL] test: held weapon has no catalog skills ({})",
                    maid.getMainHandItem().isEmpty() ? "empty" : maid.getMainHandItem().getDescriptionId());
        }
        return own;
    }

    private static void testTick(MaidPatch<?> patch, EntityMaid maid, int tick) {
        // 战技播放期间（50 tick 窗口）每 tick 维持目标锁定：brain 目标被 TLM 每 tick 清除，
        // 不维持则 EFN 攻击动画的伤害判定落在无目标上（播放但 0 伤害）
        maintainTarget(patch, maid);
        if (tick - lastTestTick < TEST_INTERVAL) {
            return;
        }
        // inaction() = 正在执行动作（EpicFight "takingAction"）；忙时跳过，等上一个动画播完
        if (patch.getEntityState().inaction()) {
            return;
        }
        List<SkillSpec> order = testOrder(patch);
        if (order.isEmpty()) {
            return;
        }
        SkillSpec spec = order.get(testCursor % order.size());
        testCursor++;
        lastTestTick = tick; // 节流：释放后重置计时，防止同 tick 连放
        testRelease(patch, maid, spec, tick);
    }

    /** 维持目标锁定：patch 无目标时锁定最近标靶（brain 写入 + setTarget） */
    private static void maintainTarget(MaidPatch<?> patch, EntityMaid maid) {
        if (patch.getTarget() != null && patch.getTarget().isAlive()) {
            return;
        }
        LivingEntity nearest = org.eftlm.stylish.arena.AutoArena.findNearestTarget(maid);
        if (nearest != null) {
            maid.setTarget(nearest);
            maid.getBrain().setMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.ATTACK_TARGET, nearest);
        }
    }

    /**
     * 释放战技：播放动画（EFN 动画自带伤害 / 位移事件）。null / 异常均记录 FAILED。
     * 释放前强制锁定最近标靶——EFN 攻击动画的伤害判定依赖 patch 目标，目标缺失时动画
     * 只播放不结算（命中反馈 0）。
     */
    private static void testRelease(MaidPatch<?> patch, EntityMaid maid, SkillSpec spec, int tick) {
        LivingEntity target = patch.getTarget();
        if (target == null || !target.isAlive()) {
            target = org.eftlm.stylish.arena.AutoArena.findNearestTarget(maid);
            if (target != null) {
                maid.setTarget(target);
                maid.getBrain().setMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.ATTACK_TARGET, target);
            }
        }
        if (EfnSkillCatalog.resolve(spec) == null) {
            LOGGER.info("[SKILL] test: {} play=FAILED (anim {} not found)", spec.id(), spec.animKey());
            return;
        }
        try {
            patch.playAnimationSynchronized(EfnSkillCatalog.resolve(spec), 0F);
            currentTest = spec.id();
            currentTestTick = tick;
            TEST_PLAYED.merge(spec.id(), 1, Integer::sum);
            LOGGER.info("[SKILL] test: {} play=ok anim={} target={}", spec.id(), spec.animKey(),
                    target != null ? target.getType().getDescriptionId() : "null");
        } catch (Throwable t) {
            LOGGER.info("[SKILL] test: {} play=FAILED exception={}", spec.id(), t.toString());
        }
    }

    /**
     * 命中反馈：战技释放后 {@value #HIT_WINDOW} tick 内的命中归为该战技（onHurtTargetPost 调用）。
     */
    public static void onHit(EntityMaid maid, int tick, float amount) {
        if (!isTestMode() || currentTest == null) {
            return;
        }
        if (tick - currentTestTick <= HIT_WINDOW) {
            TEST_HITS.merge(currentTest, 1, Integer::sum);
            LOGGER.info("[SKILL] test: {} HIT dmg={}", currentTest, String.format("%.1f", amount));
        }
    }

    /**
     * 测试汇总（RCON / 日志查询用）：输出每个战技播放与命中次数。
     */
    public static String testSummary() {
        StringBuilder sb = new StringBuilder("[SKILL] test summary:");
        List<String> names = new java.util.ArrayList<>(TEST_PLAYED.keySet());
        Collections.sort(names);
        for (String name : names) {
            int played = TEST_PLAYED.getOrDefault(name, 0);
            int hits = TEST_HITS.getOrDefault(name, 0);
            sb.append("\n  ").append(name).append(": played=").append(played).append(" hits=").append(hits);
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------
    // 战斗模式：目录条件触发
    // ------------------------------------------------------------------

    private static void combatTick(MaidPatch<?> patch, EntityMaid maid, int tick) {
        // 模型加载后技能释放由 RL 经事件总线驱动（动态行动空间槽位），本状态机仅作无模型规则兜底
        if (org.eftlm.stylish.rl.RlBrain.isModelLoaded()) {
            return;
        }
        // 战斗模式同样维持目标锁定（brain 目标被清时行为表无目标不攻击）
        maintainTarget(patch, maid);
        List<SkillSpec> skills = EfnSkillCatalog.skillsOf(patch);
        if (skills.isEmpty()) {
            return; // 主手无目录技能（WOM / AV 等无技能武器或未知武器：默认置空，不猜测）
        }
        // 战技优先级：位移突进（mid_range）> 浮空技（airborne）> 近战其余
        SkillSpec best = null;
        int bestRank = Integer.MAX_VALUE;
        for (SkillSpec spec : skills) {
            if (EfnSkillCatalog.isCooling(maid, spec, tick)) {
                continue;
            }
            int rank = EfnSkillCatalog.rank(spec);
            if (rank >= bestRank) {
                continue;
            }
            if (!EfnSkillCatalog.matches(patch, spec)) {
                continue;
            }
            if (!EfnSkillCatalog.canRelease(patch, spec)) {
                continue;
            }
            best = spec;
            bestRank = rank;
        }
        if (best == null) {
            return;
        }
        // inaction() = 正在执行动作；忙时不打断，等上一个动画播完
        if (patch.getEntityState().inaction()) {
            return;
        }
        if (EfnSkillCatalog.release(patch, best)) {
            EfnSkillCatalog.markUsed(maid, best, tick);
            if (best.id().contains("drive")) {
                RlDataRecorder.addReward(maid, 10); // 突进使用鼓励（Gap-Closing 行为先验）
            }
        }
    }
}
