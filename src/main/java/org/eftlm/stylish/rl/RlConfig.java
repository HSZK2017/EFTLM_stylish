package org.eftlm.stylish.rl;

import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * RL 运行时配置（config/eftlm_stylish/rl.properties，懒加载一次）：
 * <pre>
 *   enable_all_maids = true   # 学习技能书且战斗模式的女仆全部由 RL 决策（轨迹采集仍仅竞技场）
 *   epsilon         = 0.08    # ε-greedy 探索率
 *   arbitration     = hybrid  # hybrid: 模型加载时行为表进攻系列让位 RL；none: 共存（旧行为）
 *   model_file      = rl_model.bin  # 模型文件名（相对 config/eftlm_stylish/，也支持绝对路径）
 *   trace_enabled   = true    # P0 观测：决策链路追踪（/rl dump 导出 CSV）
 *   shadow_mode     = false   # P0 观测：模型只推理不执行（行为表接管），记录模型动作对照
 *   slot_stable     = true    # P2：稳定技能槽（槽位语义=技能身份+掩码，修复槽位漂移）
 *   item_block_parry = true   # P2.5：受击放方块格挡（仿 AVNpc Steve/Alex，默认概率 0.4）
 *   block_parry_chance = 0.4  # 放方块格挡触发概率（Steve=1.0 / Alex=0.7 的折中）
 *   item_block_weapon = true  # P5.5：主手方块=方块武器（放置技能放主手方块不消耗）
 *   adaptive_learn = true     # P5.6：自适应学习——观察敌方攻击节奏（EMA 间隔统计）
 *   adaptive_hitgrid = true   # P5.6：命中经验掩码（极坐标扇区桶：有经验且目标不在桶内的技能置 0）
 *   buff_steal = auto         # P5.6：增益窃取（auto=仅竞技场训练采集时启用/on/off，加快训练速度）
 *   tick_multiplier = 1.0     # P5.6：游戏刻加速倍率（1.0=20TPS 原速；1.5=30TPS；服务端生效）
 *   auto_tickrate = false     # P5.7：智能性能调度（空闲自动加速/负载自动降速/看门狗）
 *   auto_tickrate_min = 1.0   # P5.7：自动调度倍率下限（1.0=原速，不低于此）
 *   auto_tickrate_max = 8.0   # P5.7：自动调度倍率上限（8.0=160TPS；本机实测最佳稳定档，9.0 会震荡）
 *   auto_tickrate_step = 0.5  # P5.7：每周期倍率步长
 *   auto_tickrate_cpu_low = 0.5  # P5.7：系统 CPU < 此值视为空闲（可升档）
 *   auto_tickrate_cpu_high = 0.8 # P5.7：系统 CPU > 此值视为负载（降档）
 *   auto_tickrate_tick_low_ms = 25  # P5.7：tick 95%ile 低于此可升档
 *   auto_tickrate_tick_high_ms = 40 # P5.7：tick 95%ile 高于此降档
 *   auto_tickrate_interval = 5      # P5.7：采样决策周期（秒）
 *   auto_tickrate_watchdog_stall = 30 # P5.7：tick 停滞秒数（看门狗强制降速阈值）
 *   item_water_extinguish = true  # P2.5：着火水桶灭火（仿 AV tryPerformAvNpcWaterBucketSelfExtinguish）
 *   item_pearl_counter = true     # P2.5：受击末影珍珠反击（仿 AV doSteveStyleEnderPearlCounter）
 *   shadow_model_file =       # P4：影子评估模型（竞技场影子女仆使用；空=关闭影子评估）
 * </pre>
 * 解析模式与 {@code arena.properties} 一致（# 注释、k=v）。
 */
public final class RlConfig {

    private static final Logger LOGGER = LogManager.getLogger("eftlm_stylish");

    public static boolean enableAllMaids = true;
    public static float epsilon = 0.08F;
    /** hybrid：模型加载时行为表进攻系列让位 RL；none：共存 */
    public static String arbitration = "hybrid";
    public static String modelFile = "rl_model.bin";
    /** P0 观测：决策链路追踪开关（默认开，/rl trace 可切） */
    public static boolean traceEnabled = true;
    /** P0 观测：影子模式——模型推理照常记录，但动作不执行（行为表/规则接管） */
    public static boolean shadowMode = false;
    /** P2：稳定技能槽布局（默认开；关闭回到 V46 动态布局） */
    public static boolean slotStable = true;
    /** P2.5：受击放方块格挡（仿 AVNpc Steve/Alex） */
    public static boolean itemBlockParry = true;
    /** 放方块格挡触发概率（Steve=1.0 / Alex=0.7 折中 0.4） */
    public static float blockParryChance = 0.4F;
    /** P5.5：主手方块=方块武器（放置技能放主手方块且不消耗） */
    public static boolean itemBlockWeapon = true;
    /** P5.6：自适应学习——观察敌方攻击节奏（EMA 间隔统计 + 命中经验数据源） */
    public static boolean adaptiveLearn = true;
    /** P5.6：命中经验掩码（极坐标扇区桶：有经验且目标不在桶内的技能置 0，减少无效出招） */
    public static boolean adaptiveHitgrid = true;
    /** P5.6：增益窃取（auto=仅竞技场训练采集时启用 / on / off） */
    public static String buffSteal = "auto";
    /** P5.6：游戏刻加速倍率（1.0=20TPS 原速；服务端生效，/rl tickrate 热调） */
    public static float tickMultiplier = 1.0F;
    /** P5.7：智能性能调度总开关（AutoTicker：空闲自动加速/负载自动降速/看门狗） */
    public static boolean autoTickrate = false;
    /** P5.7：自动调度倍率下限（1.0=原速） */
    public static float autoTickrateMin = 1.0F;
    /** P5.7：自动调度倍率上限（2.0=40TPS） */
    public static float autoTickrateMax = 2.0F;
    /** P5.7：每周期倍率步长 */
    public static float autoTickrateStep = 0.5F;
    /** P5.7：系统 CPU 低于此视为空闲（可升档） */
    public static double autoTickrateCpuLow = 0.5;
    /** P5.7：系统 CPU 高于此视为负载（降档） */
    public static double autoTickrateCpuHigh = 0.8;
    /** P5.7：tick 95%ile 低于此可升档（ms） */
    public static double autoTickrateTickLowMs = 25.0;
    /** P5.7：tick 95%ile 高于此降档（ms） */
    public static double autoTickrateTickHighMs = 40.0;
    /** P5.7：采样决策周期（秒） */
    public static long autoTickrateInterval = 5L;
    /** P5.7：tick 停滞秒数（看门狗强制降速阈值） */
    public static long autoTickrateWatchdogStall = 30L;
    /** P2.5：着火水桶灭火（仿 AV tryPerformAvNpcWaterBucketSelfExtinguish） */
    public static boolean itemWaterExtinguish = true;
    /** P2.5：受击末影珍珠反击（仿 AV doSteveStyleEnderPearlCounter） */
    public static boolean itemPearlCounter = true;
    /** P4：影子评估模型文件名（竞技场影子女仆专用；空=关闭） */
    public static String shadowModelFile = "";
    /**
     * 距离惩罚封顶值（rl.properties prox_penalty_max，默认 -16 = 历史口径）。
     * 2026-09-10 新增：距离惩罚在"打不过"阶段会长期占满（实测 p50 = 封顶值），
     * 提供该键以便在不改代码的情况下做消融（例如 -8 或 0=关闭）。
     */
    public static int proxPenaltyMax = -16;

    /**
     * P2-6 命中经验作用域：{@code maid}=按女仆隔离（默认）；{@code global}=进程内共享（旧行为）。
     * <p>
     * 命中经验（`CombatLibrary.HIT_EXP`）驱动动作掩码与规则调度排序，共享时一个女仆的
     * 经验会改写另一个女仆的动作空间，影子 A/B 对照因此不成立（审查报告 AR-9）。
     */
    public static String hitExpScope = "maid";

    /** P5.8 selfplay 对手策略：latest=当前模型 / history=模型池随机（默认）/ champion=池中最新 / random=随机动作注入 */
    public static String selfplayOpponent = "history";
    /** P5.8 随机对手注入比例（selfplay_opponent=random 时生效）：0~1，该比例决策对手走纯随机 */
    public static double selfplayRandomRatio = 0.2;

    private static volatile boolean loaded = false;

    private RlConfig() {
    }

    public static void ensureLoaded() {
        if (loaded) {
            return;
        }
        synchronized (RlConfig.class) {
            if (!loaded) {
                load();
                loaded = true;
            }
        }
    }

    private static void load() {
        try {
            Path path = FMLPaths.CONFIGDIR.get().resolve("eftlm_stylish").resolve("rl.properties");
            if (!Files.exists(path)) {
                LOGGER.info("[RL] no rl.properties found at {}, using defaults", path);
                return;
            }
            int lineNo = 0;
            for (String raw : Files.readAllLines(path)) {
                lineNo++;
                String line = raw.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] kv = line.split("=", 2);
                if (kv.length != 2) {
                    continue;
                }
                String k = kv[0].trim();
                String v = kv[1].trim();
                // P1 修复（2026-09-10）：逐键容错。旧实现把整个文件放在一个 try 里，
                // 任何一个键写错（如 epsilon=0.0x）都会中断后续所有键的解析，
                // 其余配置静默退回默认值且只留一条笼统错误。现在逐键捕获并报告行号。
                try {
                    applyKey(k, v);
                } catch (RuntimeException ex) {
                    LOGGER.error("[RL] rl.properties:{} bad value '{}={}' ({}), key ignored",
                            lineNo, k, v, ex.toString());
                }
            }
            LOGGER.info("[RL] config loaded: enable_all_maids={} epsilon={} arbitration={} model_file={} trace={} shadow={} slot_stable={} item_block_parry={}({}%) block_weapon={} water={} pearl={} adaptive_learn={} adaptive_hitgrid={} buff_steal={} tick_multiplier={} autotick={}({}~{} step={} cpu{}/{} tick{}ms/{}ms) shadow_model={}",
                    enableAllMaids, epsilon, arbitration, modelFile, traceEnabled, shadowMode, slotStable,
                    itemBlockParry, Math.round(blockParryChance * 100), itemBlockWeapon, itemWaterExtinguish, itemPearlCounter,
                    adaptiveLearn, adaptiveHitgrid, buffSteal, tickMultiplier,
                    autoTickrate, autoTickrateMin, autoTickrateMax, autoTickrateStep,
                    autoTickrateCpuLow, autoTickrateCpuHigh, autoTickrateTickLowMs, autoTickrateTickHighMs,
                    shadowModelFile.isEmpty() ? "off" : shadowModelFile);
        } catch (Exception e) {
            LOGGER.error("[RL] failed to load rl.properties, using defaults", e);
        }
    }

    /** 单个配置键的应用（数值键在此抛出的解析异常由 {@link #load()} 按键捕获并报行号） */
    private static void applyKey(String k, String v) {
        switch (k) {
            case "enable_all_maids" -> enableAllMaids = Boolean.parseBoolean(v);
            case "epsilon" -> epsilon = Math.max(0.0F, Math.min(1.0F, Float.parseFloat(v)));
            case "arbitration" -> arbitration = v;
            case "model_file" -> modelFile = v;
            case "trace_enabled" -> traceEnabled = Boolean.parseBoolean(v);
            case "shadow_mode" -> shadowMode = Boolean.parseBoolean(v);
            case "slot_stable" -> slotStable = Boolean.parseBoolean(v);
            case "item_block_parry" -> itemBlockParry = Boolean.parseBoolean(v);
            case "block_parry_chance" -> blockParryChance = Math.max(0.0F, Math.min(1.0F, Float.parseFloat(v)));
            case "item_block_weapon" -> itemBlockWeapon = Boolean.parseBoolean(v);
            case "adaptive_learn" -> adaptiveLearn = Boolean.parseBoolean(v);
            case "adaptive_hitgrid" -> adaptiveHitgrid = Boolean.parseBoolean(v);
            case "buff_steal" -> buffSteal = v;
            case "tick_multiplier" -> tickMultiplier = Math.max(0.1F, Math.min(10.0F, Float.parseFloat(v)));
            case "auto_tickrate" -> autoTickrate = Boolean.parseBoolean(v);
            case "auto_tickrate_min" -> autoTickrateMin = Math.max(0.1F, Math.min(10.0F, Float.parseFloat(v)));
            case "auto_tickrate_max" -> autoTickrateMax = Math.max(autoTickrateMin, Math.min(10.0F, Float.parseFloat(v)));
            case "auto_tickrate_step" -> autoTickrateStep = Math.max(0.1F, Math.min(5.0F, Float.parseFloat(v)));
            case "auto_tickrate_cpu_low" -> autoTickrateCpuLow = Math.max(0.0, Math.min(1.0, Double.parseDouble(v)));
            case "auto_tickrate_cpu_high" -> autoTickrateCpuHigh = Math.max(0.0, Math.min(1.0, Double.parseDouble(v)));
            case "auto_tickrate_tick_low_ms" -> autoTickrateTickLowMs = Math.max(1.0, Double.parseDouble(v));
            case "auto_tickrate_tick_high_ms" -> autoTickrateTickHighMs = Math.max(autoTickrateTickLowMs, Double.parseDouble(v));
            case "auto_tickrate_interval" -> autoTickrateInterval = Math.max(2L, Math.min(120L, Long.parseLong(v)));
            case "auto_tickrate_watchdog_stall" -> autoTickrateWatchdogStall = Math.max(10L, Math.min(600L, Long.parseLong(v)));
            case "selfplay_opponent" -> {
                String mode = v.trim();
                if (mode.equals("latest") || mode.equals("history") || mode.equals("champion") || mode.equals("random")) {
                    selfplayOpponent = mode;
                } else {
                    LOGGER.warn("[RL] unknown selfplay_opponent '{}' (keep {})", mode, selfplayOpponent);
                }
            }
            case "selfplay_random_ratio" -> selfplayRandomRatio = Math.max(0.0, Math.min(1.0, Double.parseDouble(v)));
            case "item_water_extinguish" -> itemWaterExtinguish = Boolean.parseBoolean(v);
            case "item_pearl_counter" -> itemPearlCounter = Boolean.parseBoolean(v);
            case "shadow_model_file" -> shadowModelFile = v;
            case "prox_penalty_max" -> proxPenaltyMax = Math.max(-64, Math.min(0, Integer.parseInt(v)));
            case "hit_exp_scope" -> {
                // P2-6：命中经验作用域。maid=按女仆隔离（默认，使影子 A/B 成为真正的
                // 对照实验）；global=进程内共享（旧行为，复现旧实验/对照组用）。
                String mode = v.trim().toLowerCase(java.util.Locale.ROOT);
                if ("maid".equals(mode) || "global".equals(mode)) {
                    hitExpScope = mode;
                } else {
                    LOGGER.warn("[RL] unknown hit_exp_scope '{}' (keep {})", v, hitExpScope);
                }
            }
            default -> LOGGER.warn("[RL] unknown rl.properties key: {}", k);
        }
    }

    /** 模型文件路径（相对路径基于 config/eftlm_stylish/） */
    public static Path modelPath() {
        ensureLoaded();
        Path p = Paths.get(modelFile);
        if (p.isAbsolute()) {
            return p;
        }
        return FMLPaths.CONFIGDIR.get().resolve("eftlm_stylish").resolve(modelFile);
    }

    /** 影子评估模型路径（shadow_model_file 空 = 关闭，返回 null） */
    public static Path shadowModelPath() {
        ensureLoaded();
        if (shadowModelFile.isEmpty()) {
            return null;
        }
        Path p = Paths.get(shadowModelFile);
        if (p.isAbsolute()) {
            return p;
        }
        return FMLPaths.CONFIGDIR.get().resolve("eftlm_stylish").resolve(shadowModelFile);
    }

    /**
     * hybrid 仲裁：模型已加载时，行为表进攻系列（连段/大招/点射）让位给 RL 决策，
     * 防守/浮空系列保留兜底。由 {@code StylishConditions} / {@code SkillGate} 谓词调用。
     * <p>
     * P0 扩展：shadow 模式下视为"未加载"——行为表恢复接管（模型只推理不执行）。
     */
    public static boolean rlDrivesAttacks() {
        ensureLoaded();
        return "hybrid".equals(arbitration) && RlBrain.isModelLoaded() && !shadowMode;
    }
}
