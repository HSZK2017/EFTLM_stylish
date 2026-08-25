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
    /** P2.5：着火水桶灭火（仿 AV tryPerformAvNpcWaterBucketSelfExtinguish） */
    public static boolean itemWaterExtinguish = true;
    /** P2.5：受击末影珍珠反击（仿 AV doSteveStyleEnderPearlCounter） */
    public static boolean itemPearlCounter = true;
    /** P4：影子评估模型文件名（竞技场影子女仆专用；空=关闭） */
    public static String shadowModelFile = "";

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
            for (String line : Files.readAllLines(path)) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] kv = line.split("=", 2);
                if (kv.length != 2) {
                    continue;
                }
                String k = kv[0].trim();
                String v = kv[1].trim();
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
                    case "item_water_extinguish" -> itemWaterExtinguish = Boolean.parseBoolean(v);
                    case "item_pearl_counter" -> itemPearlCounter = Boolean.parseBoolean(v);
                    case "shadow_model_file" -> shadowModelFile = v;
                    default -> LOGGER.warn("[RL] unknown rl.properties key: {}", k);
                }
            }
            LOGGER.info("[RL] config loaded: enable_all_maids={} epsilon={} arbitration={} model_file={} trace={} shadow={} slot_stable={} item_block_parry={}({}%) block_weapon={} water={} pearl={} adaptive_learn={} adaptive_hitgrid={} buff_steal={} shadow_model={}",
                    enableAllMaids, epsilon, arbitration, modelFile, traceEnabled, shadowMode, slotStable,
                    itemBlockParry, Math.round(blockParryChance * 100), itemBlockWeapon, itemWaterExtinguish, itemPearlCounter,
                    adaptiveLearn, adaptiveHitgrid, buffSteal,
                    shadowModelFile.isEmpty() ? "off" : shadowModelFile);
        } catch (Exception e) {
            LOGGER.error("[RL] failed to load rl.properties, using defaults", e);
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
