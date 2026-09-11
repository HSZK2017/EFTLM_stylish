package org.eftlm.stylish.arena;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import net.EFTLM.EF.Capability.MaidPatch;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.event.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eftlm.stylish.EFTLMStylish;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 自动对战竞技场（服务器端 RL 数据采集）：
 * <ul>
 *     <li>服务器启动时清理世界中的野生女仆残留，生成竞技场女仆（EnderBlaster + 近战）</li>
 *     <li>强制加载中心区块，女仆固定在竞技场范围（restrict），脱离加载区自动拉回</li>
 *     <li>周期补充训练标靶（AnnoyingVillagers Boss 或原版敌对生物）</li>
 * </ul>
 * 配置：config/eftlm_stylish/arena.properties（entity 逗号分隔多标靶轮流生成）。
 */
@Mod.EventBusSubscriber(modid = EFTLMStylish.MODID)
public class AutoArena {

    private static final Logger LOGGER = LogManager.getLogger("eftlm_stylish");

    private static boolean enabled = true;
    /**
     * 默认标靶（P0 修复 2026-09-10）：**不再包含 `annoyingvillagers:alex`**。
     * 该实体在 V54 事故中实证会导致 `BowLineOfSightGoal.findClearShotPosition` →
     * 全图寻路 → `WalkNodeEvaluator` 节点缓存爆炸（单 tick 4290 秒）→ ServerWatchdog 强杀。
     * 旧默认值把它放在列表首位、而黑名单默认为空，等于"开箱即复现已实证的炸服路径"。
     */
    private static final String[] DEFAULT_ENTITY_IDS = {
            "annoyingvillagers:angry_steve",
            "annoyingvillagers:aegis_herobrine"
    };
    /** 默认黑名单：仅 alex（要重新启用需显式把它从 entity_blacklist 移除） */
    private static final String[] DEFAULT_ENTITY_BLACKLIST = {
            "annoyingvillagers:alex"
    };
    private static String[] entityIds = DEFAULT_ENTITY_IDS.clone();
    /**
     * 目标实体黑名单（arena.properties entity_blacklist，逗号分隔）：
     * V54 追加（2026-08-28 09:23 crash 实证）：AV 弓箭手（alex）的
     * BowLineOfSightGoal.findClearShotPosition → repath 全图寻路 → WalkNodeEvaluator
     * 节点缓存计算爆炸（单 tick 4290 秒）→ ServerWatchdog 强杀。
     * 黑名单目标不生成（course.json suggested_entities 同样过滤）。
     */
    private static String[] entityBlacklist = DEFAULT_ENTITY_BLACKLIST.clone();
    /**
     * 是否"本存档归竞技场所有"（arena.properties own_world，**默认 false**）：
     * 只有它为 true 时才执行启动清场（discard 出生点周围全部女仆与生物）。
     * P0 修复 2026-09-10：该清场逻辑对训练专用服是"清理残留"，但对普通存档是**不可恢复的丢档**，
     * 因此默认关闭；训练服请在 arena.properties 显式写 own_world=true。
     */
    private static boolean ownWorld = false;

    /** 标靶类型判定缓存（键 = EntityType；entityIds 变化时清空）。P1：热路径避免每个实体每 tick 注册表查找 */
    private static final Map<net.minecraft.world.entity.EntityType<?>, Boolean> ARENA_TARGET_CACHE =
            new java.util.concurrent.ConcurrentHashMap<>();

    /** 统一设置标靶列表（P1 修复 2026-09-10：任何赋值都经此，保证类型判定缓存失效） */
    private static void setEntityIds(String[] ids) {
        entityIds = ids;
        ARENA_TARGET_CACHE.clear();
    }

    /**
     * 过滤黑名单目标（保留非空且不在黑名单中的 id）。
     * P0 修复（2026-09-10）：过滤后若为空（例如 entity 与 entity_blacklist 写了同一个 id），
     * 旧实现会把空数组交给下游，`spawnTarget` 的 `entityIds[targetCursor % entityIds.length]`
     * 随即除零 → 异常抛进 ServerTickEvent → 崩服。现在回退默认列表并 ERROR 告警。
     */
    private static String[] filterBlacklist(String[] ids) {
        if (ids == null || ids.length == 0) {
            return ids;
        }
        var out = new java.util.ArrayList<String>();
        for (String id : ids) {
            id = id.trim();
            if (id.isEmpty()) {
                continue;
            }
            boolean banned = false;
            for (String b : entityBlacklist) {
                if (b != null && b.trim().equals(id)) {
                    banned = true;
                    break;
                }
            }
            if (!banned) {
                out.add(id);
            }
        }
        if (out.isEmpty()) {
            LOGGER.error("[Arena] entity list is EMPTY after blacklist filter (entity=[{}] blacklist=[{}]) "
                            + "→ fallback to default targets {} (check arena.properties)",
                    String.join(",", ids), String.join(",", entityBlacklist),
                    String.join(",", DEFAULT_ENTITY_IDS));
            return DEFAULT_ENTITY_IDS.clone();
        }
        return out.toArray(new String[0]);
    }
    private static int count = 1;
    private static int interval = 600;
    private static int spawnDistance = 12;
    private static String maidMain = "wom:ender_blaster";
    private static String maidMelee = "epicfight:uchigatana";
    private static String maidMelee2 = "epicfight:greatsword";
    /** 背包远程兜底（如 EnderBlaster）：距离切换时会被 RL/规则策略发现使用；可空 */
    private static String maidRanged = "wom:ender_blaster";
    /** P5.7 开局多样化开关：主女仆 spawn 时随机副手武器/额外增益/金苹果/黑曜石（增加状态多样性） */
    private static boolean randomOpenings = true;
    /** 竞技场中心（女仆活动范围中心） */
    private static int centerX = 0;
    private static int centerY = -60;
    private static int centerZ = 0;
    /** 女仆活动半径（超过则拉回；flat 模式放大） */
    private static final int ARENA_RADIUS = 30;
    /** P4 超平坦模式活动半径（格） */
    private static final int FLAT_ARENA_RADIUS = 60;
    private static int arenaRadius = ARENA_RADIUS;
    /** P4 世界模式：platform=虚空平台（默认）/ flat=超平坦开阔地面 */
    private static String worldMode = "platform";

    // ---- V9：Cage Box（斗兽场）----
    /** 斗兽场开关：训练阶段缩小战场，逼 AI 近战 */
    private static boolean cageEnabled = true;
    /** 斗兽场半径（格，初始 = 房间 1/4 大小） */
    private static int cageRadius = 8;
    /** 边界扩大间隔（分钟）：达标后每轮 +5% */
    private static int cageGrowthMinutes = 30;
    /** 斗兽场半径上限 */
    private static final int CAGE_MAX_RADIUS = 20;
    /** course.json 单次下发标靶数量上限与斗兽场半径上限（P1：外部输入边界） */
    private static final int MAX_COURSE_ENTITIES = 16;
    private static final int MAX_CAGE_RADIUS = 64;
    /** 按键战技测试模式（V12：验证 EFN 键技在女仆身上可用） */
    private static boolean testSkills = false;

    public static boolean isTestSkills() {
        return testSkills;
    }
    /** 击杀后立即刷新标志（女仆击杀 → 下个 tick 直接补标靶，避免 20 秒空窗） */
    private static boolean spawnNow = false;
    /** 墙高（格，从地面起） */
    private static final int CAGE_WALL_HEIGHT = 5;
    /** 墙厚度（格） */
    private static final int CAGE_WALL_THICK = 2;
    /** 当前实际半径（随生长变化） */
    private static double cageCurrentRadius = 8.0;
    private static long cageGrowthTick = 0;

    private static int arenaMaidId = -1;
    /** 当前竞技场女仆的 UUID（RL 数据采集只处理竞技场女仆，排除残留女仆污染） */
    private static java.util.UUID arenaUuid = null;
    /** P5 影子女仆（shadow_model_file 配置时生成，与主女仆 A/B 对比评估） */
    private static int shadowMaidId = -1;
    private static java.util.UUID shadowUuid = null;
    private static int shadowRespawnTimer = 0;
    /** P5.6 影子女仆 AI 模式（arena.properties shadow_ai）：model=影子模型评估；adaptive=自适应规则 AI */
    private static String shadowAi = "model";
    /** P5.6 自我博弈 自适应对手（课程全部结束后登场：course.json selfplay=true 或 arena.properties selfplay=true） */
    private static boolean selfplayEnabled = false;
    private static int adaptiveMaidId = -1;
    private static java.util.UUID adaptiveUuid = null;
    private static int adaptiveRespawnTimer = 0;
    /** P5 胜率统计：击杀 / 死亡（绀珠药复活=死亡）/ 复活次数 */
    private static int statsKills = 0;
    private static int statsDeaths = 0;
    private static int statsRevives = 0;
    // P1 修复（2026-09-10）：胜率按角色分桶。旧实现 kills 计入"任意女仆击杀"（含影子/对手），
    // deaths 只由绀珠之药耐久推断 → 未触发复活的死亡不计入分母，win_rate 同时被高估与低估。
    private static int statsKillsMain = 0;      // 主女仆击杀
    private static int statsDeathsMain = 0;     // 主女仆真实死亡（LivingDeathEvent）
    private static int respawnTimer = 0;
    private static int targetCursor = 0;
    /** 绀珠之药剩余耐久（6=满，减少 = 女仆死过一次被复活） */
    private static int lastElixirDur = 6;
    private static final Random RANDOM = new Random();

    private AutoArena() {
    }

    /**
     * 全虚空超平坦世界（V12 方案）：世界生成 = 纯空气层 + the_void 生物群系（无任何方块），
     * 竞技场以出生点为中心 fill 石头平台（初始半宽 {@link #platformHalf}，单层）——
     * 平台外全是虚空，任何瞬移 / 逃逸实体必掉落虚空受虚空伤害死亡（自然回收），
     * 彻底杜绝瞬移出加载区的逃逸循环。扩圈时平台半宽 +1，向外补一圈方块。
     */
    private static int platformHalf = 10;
    /** 平台方块层 y（石头层；实体站立面 y = 该层 + 1） */
    private static final int PLATFORM_Y = -60;
    /** 扩圈上限（半宽） */
    private static final int PLATFORM_MAX_HALF = 32;

    private static void buildPlatform(ServerLevel level) {
        long t0 = System.currentTimeMillis();
        net.minecraft.world.level.block.state.BlockState bedrock = net.minecraft.world.level.block.Blocks.BEDROCK.defaultBlockState();
        for (int x = -platformHalf; x <= platformHalf; x++) {
            for (int z = -platformHalf; z <= platformHalf; z++) {
                level.setBlockAndUpdate(new BlockPos(centerX + x, PLATFORM_Y, centerZ + z), bedrock);
            }
        }
        // 清除旧版玻璃围墙残留（V17：玻璃墙已移除，边界由世界边界承担）
        for (int x = -CAGE_MAX_RADIUS - CAGE_WALL_THICK; x <= CAGE_MAX_RADIUS + CAGE_WALL_THICK; x++) {
            for (int z = -CAGE_MAX_RADIUS - CAGE_WALL_THICK; z <= CAGE_MAX_RADIUS + CAGE_WALL_THICK; z++) {
                for (int h = 0; h < CAGE_WALL_HEIGHT; h++) {
                    BlockPos p = new BlockPos(centerX + x, PLATFORM_Y + h, centerZ + z);
                    if (level.getBlockState(p).is(net.minecraft.world.level.block.Blocks.GLASS)) {
                        level.setBlockAndUpdate(p, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
                    }
                }
            }
        }
        // 世界边界 = 平台边缘：阻挡非瞬移实体越界（女仆被击退/突刺位移、Boss 被击退），
        // 瞬移仍可穿越边界 → 掉虚空死亡回收（边界外 5 格起受边界伤害，双保险）
        level.getWorldBorder().setCenter(centerX, centerZ);
        level.getWorldBorder().setSize(platformHalf * 2 + 1);
        LOGGER.info("[Arena] platform built: {}x{} bedrock at y={}, world border {}x{} ({}ms)",
                platformHalf * 2 + 1, platformHalf * 2 + 1, PLATFORM_Y,
                platformHalf * 2 + 1, platformHalf * 2 + 1, System.currentTimeMillis() - t0);
    }

    /** 平台扩圈：半宽 +1 并补新圈方块（cage 生长时同步调用） */
    private static void growPlatform(ServerLevel level) {
        if (platformHalf >= PLATFORM_MAX_HALF) {
            return;
        }
        int newHalf = platformHalf + 1;
        net.minecraft.world.level.block.state.BlockState bedrock = net.minecraft.world.level.block.Blocks.BEDROCK.defaultBlockState();
        int placed = 0;
        for (int x = -newHalf; x <= newHalf; x++) {
            for (int z = -newHalf; z <= newHalf; z++) {
                if (Math.abs(x) <= platformHalf && Math.abs(z) <= platformHalf) {
                    continue; // 只补外圈
                }
                level.setBlockAndUpdate(new BlockPos(centerX + x, PLATFORM_Y, centerZ + z), bedrock);
                placed++;
            }
        }
        platformHalf = newHalf;
        level.getWorldBorder().setSize(platformHalf * 2 + 1); // 世界边界同步扩大
        LOGGER.info("[Arena] platform grown to half {} (+{} blocks, border {})", platformHalf, placed, platformHalf * 2 + 1);
    }

    /** 击杀后请求立即补标靶（onKillTarget 调用，避免击杀空窗） */
    public static void requestSpawn() {
        spawnNow = true;
    }

    /**
     * 击杀上报（2026-09-11 新增）：由 {@code StylishCombatSkill.onKillTarget}（TLM 的
     * MaidKilledEvent，可靠）调用，按角色计入胜率统计。
     * <p>
     * 为什么需要它：原先只在 {@code LivingDeathEvent} 里按"伤害来源实体"归属击杀，
     * 而技能/弹道伤害的来源常常不是女仆实体本身 → 统计严重低估（实测同一时段日志
     * {@code [SKILL] KILL detected} 约 150 次，而 {@code /arena stats} 只记到 8 次，差 ~19 倍）。
     */
    public static void reportMaidKill(EntityMaid killer) {
        if (killer == null) {
            return;
        }
        statsKills++;
        if (isArenaMaid(killer)) {
            statsKillsMain++;
        }
    }

    /** P5 胜率统计命令：/arena stats（权限 2） */
    @SubscribeEvent
    public static void onRegisterCommands(net.minecraftforge.event.RegisterCommandsEvent event) {
        event.getDispatcher().register(
                net.minecraft.commands.Commands.literal("arena")
                        .requires(src -> src.hasPermission(2))
                        .then(net.minecraft.commands.Commands.literal("stats").executes(ctx -> {
                            // 角色口径胜率（主女仆击杀 / 主女仆死亡）；旧聚合口径保留在括号里对照
                            float winRateMain = statsKillsMain + statsDeathsMain > 0
                                    ? 100.0F * statsKillsMain / (statsKillsMain + statsDeathsMain) : 0.0F;
                            float winRateAll = statsKills + statsDeaths > 0
                                    ? 100.0F * statsKills / (statsKills + statsDeaths) : 0.0F;
                            ctx.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal(
                                    "[arena] main: kills=" + statsKillsMain + " deaths=" + statsDeathsMain
                                            + String.format(" win_rate=%.1f%%", winRateMain)
                                            + " | all-maids: kills=" + statsKills + " deaths=" + statsDeaths
                                            + " revives=" + statsRevives
                                            + String.format(" win_rate=%.1f%%", winRateAll)
                                            + " shadow=" + (shadowMaidId >= 0 ? "on" : "off")
                                            + " shadow_ai=" + shadowAi
                                            + " selfplay=" + (selfplayEnabled ? "on" : "off")
                                            + " course=" + String.join(",", entityIds)), false);
                            return 1;
                        })));
    }

    /** Boss 死亡监听（含虚空掉落 / 女仆击杀）：任何死亡立即补标靶，消除空窗 */
    @SubscribeEvent
    public static void onBossDeath(net.minecraftforge.event.entity.living.LivingDeathEvent event) {        if (!(event.getEntity().level() instanceof net.minecraft.server.level.ServerLevel)) {
            return;
        }
        String id = net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.getKey(event.getEntity().getType()).toString();
        // 标靶集合判定（而非前缀）：标靶列表含原版实体（如 minecraft:warden）时同样即时补刷
        boolean isTarget = false;
        for (String eid : entityIds) {
            if (eid.equals(id)) {
                isTarget = true;
                break;
            }
        }
        // P1 修复：主女仆真实死亡计数（与"绀珠之药复活次数"分开）；同时清理游离计时表，防无界增长
        if (event.getEntity() instanceof EntityMaid deadMaid) {
            STRAY_TICKS.remove(deadMaid.getUUID());
            if (isArenaMaid(deadMaid)) {
                statsDeathsMain++;
            }
        }
        STRAY_TICKS.remove(event.getEntity().getUUID());
        if (isTarget) {
            spawnNow = true;
            // P5 胜率统计：标靶死亡且击杀者为主/影子女仆 → kills++（角色分桶见 statsKillsMain）
            if (event.getSource() != null && event.getSource().getEntity() instanceof EntityMaid killerMaid) {
                statsKills++;
                if (isArenaMaid(killerMaid)) {
                    statsKillsMain++;
                }
            }
            String src = event.getSource() != null && event.getSource().getEntity() != null
                    ? event.getSource().getEntity().getType().getDescriptionId() : "null";
            LOGGER.info("[SKILL] DEATH: {} killed by source={} msg={}", id, src,
                    event.getSource() != null ? event.getSource().getMsgId() : "null");
        }
        // P5.6 自我博弈：自适应对手被主女仆击杀 → 胜率统计 + 立即重生（双方都是女仆，
        // 需在此判定而非标靶列表——自适应对手不在 entityIds 中）
        if (selfplayEnabled && event.getEntity() instanceof EntityMaid killed
                && isAdaptiveMaid(killed)) {
            spawnNow = true;
            if (event.getSource() != null && event.getSource().getEntity() instanceof EntityMaid killerMaid) {
                statsKills++;
                if (isArenaMaid(killerMaid)) {
                    statsKillsMain++;
                }
            }
            LOGGER.info("[Arena] selfplay: adaptive maid killed by {}",
                    event.getSource() != null && event.getSource().getEntity() != null
                            ? event.getSource().getEntity().getType().getDescriptionId() : "null");
        }
    }

    /** RL 数据采集过滤：是否当前竞技场女仆（残留女仆不参与采集，避免污染训练数据） */
    public static boolean isArenaMaid(EntityMaid maid) {
        return arenaUuid != null && maid.getUUID().equals(arenaUuid);
    }

    /**
     * P4 影子评估：是否竞技场影子女仆（shadow_model_file 配置时由 spawn 流程标记；
     * 影子女仆使用影子模型决策，与主女仆 A/B 对比）。
     */
    public static boolean isShadowMaid(EntityMaid maid) {
        return shadowMaidId >= 0 && maid.getId() == shadowMaidId;
    }

    /** P5.6 影子 AI 模式（arena.properties shadow_ai：model/adaptive） */
    public static String shadowAiMode() {
        return shadowAi;
    }

    /**
     * P5.6 adaptive 影子模式：shadow_ai=adaptive 的影子女仆——不推理 RL 模型，
     * 由 {@link CombatLibrary} 完整驱动（学习敌方动画 + 复刻出招 + 连招链 + 增益窃取）。
     */
    public static boolean isAdaptiveShadow(EntityMaid maid) {
        return "adaptive".equals(shadowAi) && isShadowMaid(maid);
    }

    /** P5.6 自我博弈模式状态（course.json selfplay=true 或 arena.properties selfplay=true） */
    public static boolean selfplayMode() {
        return selfplayEnabled;
    }

    /**
     * P5.6 自我博弈 自适应对手判定：课程全部结束后登场的自适应规则 AI 对手
     * （不推理 RL，由 {@link CombatLibrary} 按经验调度自身技能库，与主女仆对打）。
     */
    public static boolean isAdaptiveMaid(EntityMaid maid) {
        return selfplayEnabled && adaptiveMaidId >= 0 && maid.getId() == adaptiveMaidId;
    }

    /**
     * P4 课程模式：读取教官 AI（train/instructor.py）输出的 course.json
     * （config/eftlm_stylish/course.json），覆盖标靶列表与斗兽场参数。
     * 每 5 分钟重读（训练迭代自动生效，无需重启）。
     */
    private static void loadCourse() {
        try {
            var path = net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get()
                    .resolve("eftlm_stylish").resolve("course.json");
            if (!java.nio.file.Files.exists(path)) {
                return;
            }
            String raw = new String(java.nio.file.Files.readAllBytes(path), java.nio.charset.StandardCharsets.UTF_8);
            var root = new com.google.gson.Gson().fromJson(raw, com.google.gson.JsonObject.class);
            boolean changed = false;
            if (root != null && root.has("suggested_entities")) {
                var arr = root.getAsJsonArray("suggested_entities");
                if (arr.size() > 0) {
                    // P1 修复（2026-09-10）：course.json 由训练脚本写入，属外部输入——
                    // 逐项校验 id 合法性（非法 id 会在热路径反复抛解析异常）、限制条目数。
                    int n = Math.min(arr.size(), MAX_COURSE_ENTITIES);
                    java.util.List<String> ids = new java.util.ArrayList<>(n);
                    for (int i = 0; i < n; i++) {
                        String rawId = arr.get(i).getAsString();
                        if (net.minecraft.resources.ResourceLocation.tryParse(rawId) == null) {
                            LOGGER.warn("[Arena] course: skip invalid entity id '{}'", rawId);
                            continue;
                        }
                        ids.add(rawId);
                    }
                    if (arr.size() > MAX_COURSE_ENTITIES) {
                        LOGGER.warn("[Arena] course: suggested_entities truncated {} -> {}",
                                arr.size(), MAX_COURSE_ENTITIES);
                    }
                    String[] next = ids.toArray(new String[0]);
                    next = filterBlacklist(next); // V54：黑名单目标（如 AV 弓箭手）不加入课程
                    if (next.length > 0 && !java.util.Arrays.equals(next, entityIds)) {
                        setEntityIds(next);
                        changed = true;
                        LOGGER.info("[Arena] course: entities -> {}", String.join(",", entityIds));
                    }
                }
            }
            if (root != null && root.has("arena_overrides")) {
                var ov = root.getAsJsonObject("arena_overrides");
                // 2026-09-10：实现 arena_overrides.entity（course.json 一直在写这个键，但此前只支持
                // cage_radius → 教官想换对手的意图被静默忽略）。非法 id 拒绝并告警。
                if (ov.has("entity")) {
                    String overrideId = ov.get("entity").getAsString();
                    if (net.minecraft.resources.ResourceLocation.tryParse(overrideId) == null) {
                        LOGGER.warn("[Arena] course: invalid arena_overrides.entity '{}', ignored", overrideId);
                    } else {
                        String[] next = filterBlacklist(new String[]{overrideId});
                        if (!java.util.Arrays.equals(next, entityIds)) {
                            setEntityIds(next);
                            changed = true;
                            LOGGER.info("[Arena] course: arena_overrides.entity -> {}", String.join(",", entityIds));
                        }
                    }
                }
                if (ov.has("cage_radius")) {
                    int r = Math.max(4, Math.min(MAX_CAGE_RADIUS, ov.get("cage_radius").getAsInt()));
                    if (r != cageRadius) {
                        cageRadius = r;
                        cageCurrentRadius = r;
                        changed = true;
                        LOGGER.info("[Arena] course: cage_radius -> {}", r);
                    }
                }
            }
            // P5.6 课程完成信号：instructor 判定全面达标（weakness=balanced）时写 selfplay=true
            // → 自我博弈模式：自适应对手（自适应规则 AI）作为对手登场（女仆 vs 女仆）
            if (root != null && root.has("selfplay")) {
                boolean sp = root.get("selfplay").getAsBoolean();
                if (sp != selfplayEnabled) {
                    selfplayEnabled = sp;
                    changed = true;
                    LOGGER.info("[Arena] selfplay -> {}", selfplayEnabled);
                }
            }
            if (changed) {
                spawnNow = true; // 立即按新课补刷标靶
            }
        } catch (Exception e) {
            LOGGER.error("[Arena] failed to load course.json", e);
        }
    }

    /**
     * 解析配置/课程下发的实体 id（P0 修复 2026-09-10）。
     * 旧代码在 9 处直接 `ForgeRegistries.ENTITY_TYPES.getValue(ResourceLocation.parse(id))`：
     * id 含大写/空格/非法字符时 parse 抛 ResourceLocationException，而其中多数调用点位于
     * tick / 决策热路径，异常会直接打断服务器 tick 或行为表。统一改为 tryParse + null 跳过。
     */
    private static EntityType<?> resolveType(String id) {
        var rl = ResourceLocation.tryParse(id);
        return rl == null ? null : ForgeRegistries.ENTITY_TYPES.getValue(rl);
    }

    /** 返回距离女仆最近的有效标靶（决策/状态采集的逻辑目标兜底，不限距离 64 格内） */
    public static LivingEntity findNearestTarget(EntityMaid maid) {
        if (!(maid.level() instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            return null;
        }
        LivingEntity nearest = null;
        double best = Double.MAX_VALUE;
        for (String id : entityIds) {
            // P0 修复（2026-09-10）：配置/课程下发的 id 可能非法（大写、空格、拼错）——
            // ResourceLocation.parse 会抛 ResourceLocationException，而本方法处于
            // 决策热路径（每 tick / 每决策点都会被调用），异常会直接打断行为表与 RL 链。
            var rl = ResourceLocation.tryParse(id);
            if (rl == null) {
                continue;
            }
            EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(rl);
            if (type == null) {
                continue;
            }
            for (Entity e : serverLevel.getEntities(type, e -> e.isAlive())) {
                if (!(e instanceof LivingEntity)) {
                    continue; // 防 CCE：配置成非生物实体时跳过
                }
                double d = maid.distanceToSqr(e);
                if (d < best) {
                    best = d;
                    nearest = (LivingEntity) e;
                }
            }
        }
        return nearest != null && best < 64.0 * 64.0 ? nearest : null;
    }

    /** 是否为当前竞技场配置的标靶实体（供多目标追踪器识别敌对生物） */
    public static boolean isArenaTargetEntity(Entity entity) {
        if (entity == null) {
            return false;
        }
        // P0 修复（2026-09-10）：getKey 对未注册实体类型返回 null，旧实现对结果直接 toString()
        // 会 NPE——而本方法在 TargetTracker 的扫描谓词里对每个实体每 tick 调用一次。
        // P1 修复：按 EntityType 缓存判定结果（本方法在 TargetTracker 扫描谓词里对每个实体每 tick 调用）
        Boolean cached = ARENA_TARGET_CACHE.get(entity.getType());
        if (cached != null) {
            return cached;
        }
        var key = net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        boolean hit = false;
        if (key != null) {
            String id = key.toString();
            for (String eid : entityIds) {
                if (eid.equals(id)) {
                    hit = true;
                    break;
                }
            }
        }
        ARENA_TARGET_CACHE.put(entity.getType(), hit);
        return hit;
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        loadConfig();
        loadCourse(); // P4 课程模式（教官 AI 输出）
        if (!enabled) {
            LOGGER.info("[Arena] disabled");
            return;
        }
        MinecraftServer server = event.getServer();
        ServerLevel level = server.overworld();
        BlockPos spawn = level.getSharedSpawnPos();
        centerX = spawn.getX();
        // 中心 Y 取平台站立面（PLATFORM_Y + 1）而非出生点 Y：拉回判定与斗兽场包围盒
        // 均以竞技场中心为基准，出生点 Y 与平台 Y 不一致时女仆会被误判"出界"并反复传送
        centerY = platformY();
        centerZ = spawn.getZ();

        // 清理竞技场附近所有女仆残留（竞技场专用服：无玩家，女仆全部为残留/竞技场生成）
        // P0 修复（2026-09-10）：改为仅在 own_world=true（arena.properties）时执行。
        // 旧行为是"enabled=true（默认）就无条件 discard 出生点 1024×128×1024 内的全部女仆与生物"，
        // 对训练专用服是清理残留，但对任何普通存档都是不可恢复的丢档（玩家的女仆与生物被删除）。
        if (!ownWorld) {
            LOGGER.warn("[Arena] 跳过启动清场（own_world=false，默认）：不会 discard 任何实体。"
                    + "若这是竞技场专用存档且需要清理残留女仆/生物，请在 config/eftlm_stylish/arena.properties 写 own_world=true");
        } else {
            net.minecraft.world.phys.AABB area = new net.minecraft.world.phys.AABB(
                    centerX - 512, centerY - 64, centerZ - 512,
                    centerX + 512, centerY + 64, centerZ + 512);
            List<EntityMaid> leftovers = level.getEntitiesOfClass(EntityMaid.class, area);
            for (EntityMaid maid : leftovers) {
                maid.discard();
            }
            // 清理竞技场内所有敌对生物（重置标靶，防止旧 Boss 残留导致不刷新）
            int clearedMobs = 0;
            for (net.minecraft.world.entity.Mob mob : level.getEntitiesOfClass(net.minecraft.world.entity.Mob.class, area)) {
                if (!(mob instanceof EntityMaid)) {
                    mob.discard();
                    clearedMobs++;
                }
            }
            LOGGER.info("[Arena] own_world=true: cleared {} leftover maids, {} leftover mobs", leftovers.size(), clearedMobs);
        }

        // 强制加载中心区块（保证竞技场实体持续 tick）：±8 区块（128 格）
        // 覆盖空岛平台 + 坑区（瞬移 Boss 掉坑后仍在强制加载内，可被拉回）
        for (int dx = -8; dx <= 8; dx++) {
            for (int dz = -8; dz <= 8; dz++) {
                level.setChunkForced((centerX >> 4) + dx, (centerZ >> 4) + dz, true);
            }
        }
        // 全虚空世界 + 石头平台（V12 方案）：平台外全是虚空，瞬移 Boss 必掉虚空死亡，
        // 杜绝瞬移出加载区的逃逸循环（扩圈 = 平台外补一圈方块）
        buildPlatform(level);
        LOGGER.info("[Arena] enabled: center=({},{},{}) entities={} count={} interval={}",
                centerX, centerY, centerZ, String.join(",", entityIds), count, interval);
    }

    /**
     * 关服时释放强制加载的区块（P1 修复 2026-09-10）。
     * 旧实现只在启动时 {@code setChunkForced(true)}（289 个区块）、从无对应释放：
     * 该状态会写入存档 ForcedChunksSavedData，即使之后 arena 关闭也继续生效。
     */
    @SubscribeEvent
    public static void onServerStopping(net.minecraftforge.event.server.ServerStoppingEvent event) {
        if (!enabled) {
            return;
        }
        try {
            ServerLevel level = event.getServer().overworld();
            if (level == null) {
                return;
            }
            int released = 0;
            for (int dx = -8; dx <= 8; dx++) {
                for (int dz = -8; dz <= 8; dz++) {
                    if (level.setChunkForced((centerX >> 4) + dx, (centerZ >> 4) + dz, false)) {
                        released++;
                    }
                }
            }
            LOGGER.info("[Arena] forced chunks released on server stop: {}", released);
        } catch (Throwable t) {
            LOGGER.warn("[Arena] failed to release forced chunks", t);
        }
    }

    private static void loadConfig() {
        try {
            var path = net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get().resolve("eftlm_stylish").resolve("arena.properties");
            if (java.nio.file.Files.exists(path)) {
                for (String line : java.nio.file.Files.readAllLines(path)) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    String[] kv = line.split("=", 2);
                    if (kv.length != 2) continue;
                    String k = kv[0].trim();
                    String v = kv[1].trim();
                    switch (k) {
                        case "enabled" -> enabled = Boolean.parseBoolean(v);
                        case "own_world" -> ownWorld = Boolean.parseBoolean(v);
                        case "entity" -> setEntityIds(filterBlacklist(v.split(",")));
                        case "entity_blacklist" -> {
                            entityBlacklist = v.split(",");
                            setEntityIds(filterBlacklist(entityIds));
                        }
                        case "count" -> count = Math.max(1, Integer.parseInt(v));
                        case "interval" -> interval = Math.max(100, Integer.parseInt(v));
                        case "spawn_distance" -> spawnDistance = Math.max(4, Integer.parseInt(v));
                        case "maid_main" -> maidMain = v;
                        case "maid_melee" -> maidMelee = v;
                        case "maid_melee2" -> maidMelee2 = v;
                        case "maid_ranged" -> maidRanged = v;
                        case "cage_enabled" -> cageEnabled = Boolean.parseBoolean(v);
                        case "cage_radius" -> {
                            cageRadius = Math.max(4, Integer.parseInt(v));
                            cageCurrentRadius = cageRadius;
                        }
                        case "cage_growth_minutes" -> cageGrowthMinutes = Math.max(5, Integer.parseInt(v));
                        case "test_skills" -> {
                            testSkills = Boolean.parseBoolean(v);
                            org.eftlm.stylish.strategy.AutoSkill.setTestMode(testSkills);
                        }
                        case "world_mode" -> {
                            worldMode = v;
                            // flat 模式：初始大平台（97×97 地面）+ 大活动半径
                            if ("flat".equals(worldMode)) {
                                platformHalf = 48;
                                arenaRadius = FLAT_ARENA_RADIUS;
                            }
                        }
                        case "shadow_ai" -> shadowAi = v;
                        case "selfplay" -> selfplayEnabled = Boolean.parseBoolean(v);
                        case "random_openings" -> randomOpenings = Boolean.parseBoolean(v);
                        default -> {
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("[Arena] failed to load config", e);
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !enabled) {
            return;
        }
        MinecraftServer server = event.getServer();
        ServerLevel level = server.overworld();
        if (level == null) {
            return;
        }
        int tick = server.getTickCount();
        if (tick % 20 != 0) {
            return;
        }
        // P4 课程模式：每 5 分钟重读 course.json（教官 AI 更新自动生效）
        if (tick % 6000 == 0) {
            loadCourse();
        }

        // ---- 女仆维护：死亡 / 消失后重生；跑出竞技场拉回 ----
        EntityMaid maid = getArenaMaid(level);
        if (maid == null) {
            if (respawnTimer <= 0) {
                spawnMaid(level);
                respawnTimer = 100;
            } else {
                respawnTimer -= 20;
            }
        } else {
            respawnTimer = 0;            // 强制目标锁定：TLM brain 感知（NEAREST_VISIBLE_LIVING_ENTITIES）不更新时
            // FightModeTask 永远找不到目标 → 女仆全程发呆。每 20 tick 兜底锁定墙内最近 Boss。
            if (maid.getTarget() == null) {
                LivingEntity nearest = null;
                double best = Double.MAX_VALUE;
                for (String id : entityIds) {
                    EntityType<?> type = resolveType(id);
                    if (type == null) {
                        continue;
                    }
                    for (Entity e : level.getEntities(type, e -> e.isAlive())) {
                        if (!(e instanceof LivingEntity)) {
                            continue;
                        }
                        double d = maid.distanceToSqr(e);
                        if (d < best) {
                            best = d;
                            nearest = (LivingEntity) e;
                        }
                    }
                }
                if (nearest != null && best < 64.0 * 64.0) {
                    maid.setTarget(nearest);
                    maid.getBrain().setMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.ATTACK_TARGET, nearest);
                    // 2026-09-10：日志节流——此前的 INFO 每 20 tick 一条（实测 3 分钟 33 条，
                    // 因为 TLM 会持续清空 getTarget），改为每 600 tick 一条，保留可见性但不再刷屏。
                    if (maid.tickCount % 600 == 0) {
                        LivingEntity t = maid.getTarget();
                        if (t != null) {
                            LOGGER.info("[Arena] target lock active: {} at {} blocks",
                                    nearest.getType().getDescriptionId(), String.format("%.1f", Math.sqrt(best)));
                        } else {
                            LOGGER.info("[Arena] target lock FAILED: brain memory write did not stick");
                        }
                    }
                }
            }
            // Boss 强制仇恨女仆：AnnoyingVillagers Boss 默认只仇恨玩家，
            // 无玩家时不打女仆 → 女仆(只能受击反击)不攻击 → Boss despawn 循环无击杀。
            // 让 Boss 每 20 tick 锁定女仆为攻击目标，激活女仆的反击链。
            for (String id : entityIds) {
                EntityType<?> type = resolveType(id);
                if (type == null) {
                    continue;
                }
                for (Entity e : level.getEntities(type, e -> e.isAlive() && e.distanceToSqr(maid) < 4096.0)) {
                    if (e instanceof net.minecraft.world.entity.Mob mob && !maid.equals(mob.getTarget())) {
                        mob.setTarget(maid);
                    }
                }
            }
            // 拉回脱离竞技场的女仆（未加载区块导致不 tick 的问题）
            if (maid.distanceToSqr(centerX, centerY, centerZ) > (long) arenaRadius * arenaRadius
                    || maid.getY() < PLATFORM_Y - 1) {
                // 掉出平台（虚空）也立即拉回：女仆被击退/突刺位移出平台时免于虚空死亡
                maid.teleportTo(centerX + 0.5, platformY(), centerZ + 0.5);
                LOGGER.info("[Arena] maid pulled back to platform center (y={})", String.format("%.1f", maid.getY()));
            }
            // 绀珠之药耐久检测：耐久减少 = 女仆被致死伤害复活（死亡计数）
            ItemStack elixir = maid.getMaidBauble().getStackInSlot(0);
            if (!elixir.isEmpty()
                    && elixir.getItem() == com.github.tartaricacid.touhoulittlemaid.init.InitItems.ULTRAMARINE_ORB_ELIXIR.get()) {
                int dur = elixir.getMaxDamage() - elixir.getDamageValue();
                if (dur < lastElixirDur) {
                    LOGGER.info("[Arena] maid REVIVED by elixir: durability {}/{} (revived {} times this life)",
                            dur, elixir.getMaxDamage(), lastElixirDur - dur);
                    lastElixirDur = dur;
                    // P5 胜率统计：致死死亡 + 复活
                    statsDeaths++;
                    statsRevives++;
                }
            }
        }

        // ---- P5 影子女仆维护（shadow_model_file 或 shadow_ai=adaptive 时）：死亡重生（独立计时） ----
        boolean shadowEnabled = !org.eftlm.stylish.rl.RlConfig.shadowModelFile.isEmpty()
                || "adaptive".equals(shadowAi);
        if (shadowEnabled) {
            EntityMaid shadow = getShadowMaid(level);
            if (shadow == null) {
                if (shadowRespawnTimer <= 0) {
                    spawnShadowMaid(level);
                    shadowRespawnTimer = 200;
                } else {
                    shadowRespawnTimer -= 20;
                }
            } else {
                shadowRespawnTimer = 0;
            }
        }

        // ---- P5.6 自我博弈 自适应对手维护（course.json selfplay=true 时）：登场 + 重生 + 互相仇恨 ----
        if (selfplayEnabled) {
            EntityMaid adaptive = getAdaptiveMaid(level);
            if (adaptive == null) {
                if (adaptiveRespawnTimer <= 0) {
                    spawnAdaptiveMaid(level);
                    adaptiveRespawnTimer = 200;
                } else {
                    adaptiveRespawnTimer -= 20;
                }
            } else {
                adaptiveRespawnTimer = 0;
                // 互相仇恨：自适应对手锁主女仆，主女仆锁 自适应对手（自我博弈）
                EntityMaid main = getArenaMaid(level);
                if (main != null && main.isAlive()) {
                    if (adaptive.getTarget() != main) {
                        adaptive.setTarget(main);
                        adaptive.getBrain().setMemory(
                                net.minecraft.world.entity.ai.memory.MemoryModuleType.ATTACK_TARGET, main);
                    }
                    if (main.getTarget() != adaptive) {
                        main.setTarget(adaptive);
                        main.getBrain().setMemory(
                                net.minecraft.world.entity.ai.memory.MemoryModuleType.ATTACK_TARGET, adaptive);
                    }
                }
            }
        }

        // ---- 标靶维护：数量不足时补充；竞技场内 Boss 堆积（召唤物同为 Boss 类型）时清理多余 ----
        int alive = countAliveTargets(level);
        if (alive > count) {
            // 只保留 count 个，其余清掉（防止 Boss 召唤物堆积围殴女仆）
            net.minecraft.world.phys.AABB arenaBox = new net.minecraft.world.phys.AABB(
                    centerX - 40, centerY - 64, centerZ - 40,
                    centerX + 40, centerY + 64, centerZ + 40);
            int excess = alive - count;
            for (String id : entityIds) {
                EntityType<?> type = resolveType(id);
                if (type == null) {
                    continue;
                }
                for (Entity e : level.getEntities(type, e -> e.isAlive() && arenaBox.contains(e.position()))) {
                    if (excess <= 0) {
                        break;
                    }
                    e.discard();
                    excess--;
                }
            }
            LOGGER.info("[Arena] trimmed {} excess bosses in arena", alive - count);
        } else if (alive < count && (spawnNow || tick % interval == 0)) {
            for (int i = alive; i < count; i++) {
                spawnTarget(level, maid);
            }
            spawnNow = false;
            LOGGER.info("[Arena] targets refilled: {} alive -> {} spawned", alive, count - alive);
        }

        // ---- 标靶拉近（V17.1 修复）：标靶距女仆 >10 格且持续 ≥5 秒 → 拉回女仆 2-6 格 ----
        // 防 AV 标靶 AI 卡位（平台扩至 29x29 后标靶不主动追击 / TLM brain 目标不稳定致女仆不追击）
        // 导致长时间零命中僵持（层数 0 → 技能锁死 → 训练数据退化为远程空挥）
        pullStrayTargets(level, maid, tick);

        // ---- 周期清理竞技场外的 Boss 残留（瞬移/跑远堆积，避免资源浪费） ----
        if (tick % 1200 == 0) {
            net.minecraft.world.phys.AABB arenaBox = new net.minecraft.world.phys.AABB(
                    centerX - 40, centerY - 64, centerZ - 40,
                    centerX + 40, centerY + 64, centerZ + 40);
            int cleared = 0;
            for (String id : entityIds) {
                EntityType<?> type = resolveType(id);
                if (type == null) {
                    continue;
                }
                for (Entity e : level.getEntities(type, e -> e.isAlive() && !arenaBox.contains(e.position()))) {
                    e.discard();
                    cleared++;
                }
            }
            if (cleared > 0) {
                LOGGER.info("[Arena] cleaned {} stray bosses outside arena", cleared);
            }
        }

        // ---- V9：Cage Box 维护（每 20 tick 修补墙体 + 边界生长 + 关内生物拉回） ----
        if (cageEnabled) {
            maintainCage(level, tick);
        }
    }

    // ------------------------------------------------------------------
    // V9：Cage Box（斗兽场）
    // ------------------------------------------------------------------

    /**
     * 斗兽场维护（V17：玻璃墙已移除，边界由世界边界承担）：
     * <ul>
     *     <li>每 {@link #cageGrowthMinutes} 分钟以 5% 幅度扩大半径（上限 {@link #CAGE_MAX_RADIUS}）——
     *         等 AI 在狭小空间形成近战肌肉记忆后再逐步放宽</li>
     *     <li>女仆与标靶全部拉回界内（防止瞬移逃逸）</li>
     * </ul>
     */
    private static void maintainCage(ServerLevel level, int tick) {
        int r = (int) Math.round(cageCurrentRadius);
        int y = platformY();

        // 边界生长：每 cageGrowthMinutes 分钟 +5%（平台同步扩一圈，保证界内始终有地面）
        if (tick - cageGrowthTick >= cageGrowthMinutes * 60L * 20L) {
            cageGrowthTick = tick;
            if (cageCurrentRadius < CAGE_MAX_RADIUS) {
                cageCurrentRadius = Math.min(CAGE_MAX_RADIUS, cageCurrentRadius * 1.05);
                growPlatform(level); // 平台半宽 +1 补外圈
                LOGGER.info("[Arena] cage grown to radius {}", String.format("%.1f", cageCurrentRadius));
            }
        }

        // 关内实体拉回（女仆 + 标靶：瞬移/逃逸回界内）——±120 格内全部拉回，
        // 防 Herobrine 系 Boss 瞬移逃逸导致刷怪循环无击杀
        net.minecraft.world.phys.AABB cageBox = new net.minecraft.world.phys.AABB(
                centerX - r, centerY - 64, centerZ - r,
                centerX + r, centerY + 64, centerZ + r);
        net.minecraft.world.phys.AABB pullBox = new net.minecraft.world.phys.AABB(
                centerX - 120, centerY - 64, centerZ - 120,
                centerX + 120, centerY + 64, centerZ + 120);
        EntityMaid maid = getArenaMaid(level);
        if (maid != null && !cageBox.contains(maid.position())) {
            maid.teleportTo(centerX + 0.5, y, centerZ + 0.5);
            LOGGER.info("[Arena] cage: maid pulled back inside (r={})", r);
        }
        for (String id : entityIds) {
            EntityType<?> type = resolveType(id);
            if (type == null) {
                continue;
            }
            for (Entity e : level.getEntities(type, e -> e.isAlive() && !cageBox.contains(e.position()) && pullBox.contains(e.position()))) {
                e.teleportTo(centerX + level.random.nextDouble() * 2 - 1, y, centerZ + level.random.nextDouble() * 2 - 1);
            }
        }
    }

    // ------------------------------------------------------------------
    // 女仆
    // ------------------------------------------------------------------

    private static EntityMaid getArenaMaid(ServerLevel level) {
        if (arenaMaidId < 0) {
            return null;
        }
        Entity e = level.getEntity(arenaMaidId);
        return e instanceof EntityMaid maid && !maid.isDeadOrDying() ? maid : null;
    }

    /** P5 影子女仆获取（未启用/未生成返回 null） */
    private static EntityMaid getShadowMaid(ServerLevel level) {
        if (shadowMaidId < 0) {
            return null;
        }
        Entity e = level.getEntity(shadowMaidId);
        return e instanceof EntityMaid maid && !maid.isDeadOrDying() ? maid : null;
    }

    /** P5.6 自我博弈 自适应对手获取（未启用/未生成返回 null） */
    private static EntityMaid getAdaptiveMaid(ServerLevel level) {
        if (adaptiveMaidId < 0) {
            return null;
        }
        Entity e = level.getEntity(adaptiveMaidId);
        return e instanceof EntityMaid maid && !maid.isDeadOrDying() ? maid : null;
    }

    private static void spawnMaid(ServerLevel level) {
        spawnMaidInternal(level, 0);
    }

    /** P5 影子女仆生成（shadow_model_file / shadow_ai=adaptive 配置时由 tick 调用；不参与 RL 数据采集） */
    private static void spawnShadowMaid(ServerLevel level) {
        spawnMaidInternal(level, 1);
    }

    /** P5.6 自我博弈 自适应对手生成（course.json selfplay=true 时登场；自适应规则 AI，不参与 RL 采集） */
    private static void spawnAdaptiveMaid(ServerLevel level) {
        spawnMaidInternal(level, 2);
    }

    private static void spawnMaidInternal(ServerLevel level, int role) {
        boolean shadow = role == 1;
        boolean adaptive = role == 2;
        int y = platformY();
        BlockPos pos = new BlockPos(centerX, y, centerZ);
        EntityMaid maid = InitEntities.MAID.get().create(level);
        if (maid == null) {
            LOGGER.error("[Arena] failed to create maid{}", adaptive ? " (adaptive)" : shadow ? " (shadow)" : "");
            return;
        }
        maid.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, level.random.nextFloat() * 360, 0);
        level.addFreshEntity(maid);
        if (shadow) {
            shadowMaidId = maid.getId();
            shadowUuid = maid.getUUID();
        } else if (adaptive) {
            adaptiveMaidId = maid.getId();
            adaptiveUuid = maid.getUUID();
        } else {
            arenaMaidId = maid.getId();
            arenaUuid = maid.getUUID();
        }
        // 活动范围：FightModeTask.farAway 用 restrictRadius 判目标有效性，
        // 无主人女仆默认 -1 → 任何目标都被判"太远"清除 → 强制锁定也立刻失效
        // V54 教训：restrictTo(200) 过宽——AV 实体瞬移到平台外虚空时女仆寻路到虚空
        // （A* 无限扩展）→ PathFinder 卡死 80 秒+（watchdog 强杀，06:28 事故实证）。
        // 限制在平台内（CAGE_MAX_RADIUS+4）：目标出平台即判无效 → 不寻路 → 拉回机制接管。
        // （当前线上组合：v52 + 弱目标 + restrictTo 24 已稳定，勿回退 200）
        maid.restrictTo(new BlockPos(centerX, centerY, centerZ), CAGE_MAX_RADIUS + 4);

        // P5.8 审查修复①：全天工作模式（默认 DAY 夜间不工作会浪费一半采集时间，
        // 7× 加速下昼夜循环飞快——必须显式 ALL）
        maid.setSchedule(com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.MaidSchedule.ALL);
        // P5.8 审查修复②：大背包 36 槽（默认 EmptyBackpack 仅 6 槽，
        // 武器 0/1/2 + 补给 3/4 后几乎无余量；BigBackpack 保证武器库/补给轮换空间）
        maid.setMaidBackpackType(new com.github.tartaricacid.touhoulittlemaid.entity.backpack.BigBackpack());

        // P5.7 开局多样化（仅主女仆，shadow/adaptive 保持固定配置作对照）：
        // 副手武器/额外增益/金苹果/黑曜石随机化 → 增加状态与行为多样性（entropy_low 对策）。
        // 主手保持 yamato（技能槽布局以主手为锚，训练标签重映射不受影响）。
        String melee = maidMelee;
        String melee2 = maidMelee2;
        java.util.List<net.minecraft.world.effect.MobEffect> extraEffects = new java.util.ArrayList<>();
        int goldenApples = 0;
        int obsidian = 0;
        if (!shadow && !adaptive && randomOpenings) {
            var ids = new java.util.ArrayList<>(org.eftlm.stylish.compat.efn.EfnSkillCatalog.allItemIds());
            ids.remove(maidMain);
            if (ids.size() >= 2) {
                java.util.Collections.shuffle(ids);
                melee = ids.get(0);
                melee2 = ids.get(1);
            }
            var effects = new java.util.ArrayList<net.minecraft.world.effect.MobEffect>();
            effects.add(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED);
            effects.add(net.minecraft.world.effect.MobEffects.JUMP);
            effects.add(net.minecraft.world.effect.MobEffects.ABSORPTION);
            effects.add(net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE);
            effects.add(net.minecraft.world.effect.MobEffects.HEALTH_BOOST);
            java.util.Collections.shuffle(effects);
            int n = level.random.nextInt(3); // 0~2 个额外增益
            for (int i = 0; i < n; i++) {
                extraEffects.add(effects.get(i));
            }
            goldenApples = level.random.nextInt(4);   // 0~3 个金苹果
            obsidian = level.random.nextInt(17);      // 0~16 个黑曜石（放墙战技使用）
        }

        // 主手：WOM EnderBlaster（唯一真枪：远程射击 + 近战体术）
        ItemStack main = item(maidMain);
        if (!main.isEmpty()) {
            maid.setItemInHand(InteractionHand.MAIN_HAND, main);
        }
        // 钻石护甲：Boss 战生存（训练数据更持久）
        maid.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,
                new ItemStack(net.minecraft.world.item.Items.DIAMOND_HELMET));
        maid.setItemSlot(net.minecraft.world.entity.EquipmentSlot.CHEST,
                new ItemStack(net.minecraft.world.item.Items.DIAMOND_CHESTPLATE));
        maid.setItemSlot(net.minecraft.world.entity.EquipmentSlot.LEGS,
                new ItemStack(net.minecraft.world.item.Items.DIAMOND_LEGGINGS));
        maid.setItemSlot(net.minecraft.world.entity.EquipmentSlot.FEET,
                new ItemStack(net.minecraft.world.item.Items.DIAMOND_BOOTS));
        // 背包：近战轮换武器 + 远程兜底（EnderBlaster 视情况被距离切换发现使用）
        var backpack = maid.getAvailableBackpackInv();
        ItemStack melee1 = item(melee);
        if (!melee1.isEmpty()) backpack.setStackInSlot(0, melee1);
        ItemStack melee2s = item(melee2);
        if (!melee2s.isEmpty()) backpack.setStackInSlot(1, melee2s);
        ItemStack ranged = item(maidRanged);
        if (!ranged.isEmpty()) backpack.setStackInSlot(2, ranged);
        // P5.7 开局补给：金苹果（受击回血）与黑曜石（放墙战技）随机数量
        if (!shadow && !adaptive && randomOpenings && backpack.getSlots() > 3) {
            if (goldenApples > 0) {
                backpack.setStackInSlot(3, new ItemStack(net.minecraft.world.item.Items.GOLDEN_APPLE, goldenApples));
            }
            if (obsidian > 0 && backpack.getSlots() > 4) {
                backpack.setStackInSlot(4, new ItemStack(net.minecraft.world.item.Items.OBSIDIAN, obsidian));
            }
        }

        // 切换到战斗模式（FightModeTask）
        IMaidTask fightTask = TaskManager.findTask(ResourceLocation.fromNamespaceAndPath("ef_tlm", "fight_mode_task")).orElse(null);
        if (fightTask != null) {
            maid.setTask(fightTask);
        }

        // 学习华丽连段技能（RL 决策生效的前提）
        MaidPatch<?> patch = EpicFightCapabilities.getEntityPatch(maid, MaidPatch.class);
        if (patch != null) {
            patch.addLearnedSkill(ResourceLocation.fromNamespaceAndPath(EFTLMStylish.MODID, "stylish_combat"));
        }

        // V54 根治（2026-08-28 crash 实证）：TLM 呼吸任务（MaidBreathAirTask）在虚空平台
        // 反复触发（氧气<100 即启动）→ findAirPosition → canPathReach 全图寻路
        // （PathFinder BFS + VoxelShape 碰撞）→ 单 tick 490 秒卡死（07:39:43 ServerWatchdog FATAL）。
        // 无限水下呼吸效果 → MobEffectUtil.hasWaterBreathing 恒 true → 呼吸任务永不启动。
        // 无限时长（tick=-1）不受 tick 消耗影响；生成路径每次生效，复活（绀珠药原地复活）不重建实体。
        maid.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.WATER_BREATHING, -1, 0, false, false));
        // 力量 II：打破与 Boss 的僵持（Boss 金苹果回血 / 高血量，无加成打不死）
        maid.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.DAMAGE_BOOST, 20 * 1800, 1, false, false));
        maid.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.REGENERATION, 20 * 1800, 1, false, false));
        // 抗性 II（长期）：V9 斗兽场 Boss 贴脸，短期抗性 III 撑不住学习期
        maid.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE, 20 * 1800, 1, false, false));
        // P5.7 开局多样化：随机 0~2 个额外增益（在基础三件套之上）
        for (net.minecraft.world.effect.MobEffect effect : extraEffects) {
            maid.addEffect(new net.minecraft.world.effect.MobEffectInstance(effect, 20 * 1800, 1, false, false));
        }
        // 出生无敌保护标记（技能 MaidAttack 中 200 tick 内免伤，防 Boss 秒杀循环）
        // 存储值必须 > 0（MaidAttack 以 spawnTick > 0 判定保护是否启用，新建实体 tickCount=0）
        maid.getPersistentData().putInt("eftlm_stylish:spawn_tick", Math.max(1, maid.tickCount));
        // 饰品栏：绀珠之药（6 耐久 = 6 条命，致死时消耗 1 耐久满血复活）
        var bauble = maid.getMaidBauble();
        bauble.setStackInSlot(0, new ItemStack(com.github.tartaricacid.touhoulittlemaid.init.InitItems.ULTRAMARINE_ORB_ELIXIR.get()));
        if (!shadow) {
            lastElixirDur = 6;
        }

        java.util.List<String> extraNames = new java.util.ArrayList<>();
        for (net.minecraft.world.effect.MobEffect e : extraEffects) {
            extraNames.add(e.getDescriptionId());
        }
        LOGGER.info("[Arena] maid{} spawned at {} id={} main={} melee={}/{} task={} patch={} extras={} apples={} obsidian={}",
                adaptive ? " (adaptive)" : shadow ? " (shadow)" : "",
                pos, adaptive ? adaptiveMaidId : shadow ? shadowMaidId : arenaMaidId, maidMain, melee, melee2,
                fightTask != null ? fightTask.getUid() : "NULL",
                patch != null ? "OK" : "NULL",
                String.join(",", extraNames), goldenApples, obsidian);
    }

    // ------------------------------------------------------------------
    // 标靶
    // ------------------------------------------------------------------

    /** 标靶距女仆超过该距离视为"游离"（格） */
    private static final int PULL_DIST = 8;
    /** 持续游离超过该 tick 数（3 秒）才拉回（避免打断 Boss 正常走位/动画；V46 缩短以应对快速位移敌人） */
    private static final int PULL_AFTER = 60;
    /** 拉回后距女仆的距离范围（2-6 格，近战接触但不重叠） */
    private static final int PULL_MIN_DIST = 2;
    private static final int PULL_MAX_DIST = 6;
    /** 标靶 UUID -> 首次游离的服务器 tick（拉回后重置） */
    private static final Map<java.util.UUID, Integer> STRAY_TICKS = new HashMap<>();

    /**
     * 标靶拉近（V17.1）：标靶长期游离在女仆近战范围外时拉回身边，
     * 保证战斗接触（命中 → 层数 → 技能链路）持续有效。
     */
    private static void pullStrayTargets(ServerLevel level, EntityMaid maid, int tick) {
        if (maid == null) {
            return; // 女仆未生成（启动首 tick / 重生间隙）：无可拉对象
        }
        for (String id : entityIds) {
            EntityType<?> type = resolveType(id);
            if (type == null) {
                continue;
            }
            for (Entity e : level.getEntities(type, e -> e.isAlive() && e != maid)) {
                double d = maid.distanceTo(e);
                java.util.UUID uid = e.getUUID();
                if (d > PULL_DIST) {
                    int first = STRAY_TICKS.getOrDefault(uid, tick);
                    STRAY_TICKS.put(uid, first);
                    if (tick - first >= PULL_AFTER) {
                        int r = PULL_MIN_DIST + RANDOM.nextInt(PULL_MAX_DIST - PULL_MIN_DIST + 1);
                        double ang = RANDOM.nextDouble() * Math.PI * 2;
                        BlockPos p = maid.blockPosition().offset(
                                (int) Math.round(Math.cos(ang) * r), 0, (int) Math.round(Math.sin(ang) * r));
                        e.teleportTo(p.getX() + 0.5, p.getY(), p.getZ() + 0.5);
                        STRAY_TICKS.put(uid, tick); // 拉回后重置计时，避免连续拉
                        LOGGER.info("[Arena] target pulled close: {} at {} blocks (was {} blocks)",
                                id, r, String.format("%.1f", d));
                    }
                } else {
                    STRAY_TICKS.remove(uid);
                }
            }
        }
    }

    private static int countAliveTargets(ServerLevel level) {
        // 只统计竞技场中心 40 格内的存活标靶（远处残留 Boss 不计入，避免永不刷新）
        net.minecraft.world.phys.AABB arenaBox = new net.minecraft.world.phys.AABB(
                centerX - 40, centerY - 64, centerZ - 40,
                centerX + 40, centerY + 64, centerZ + 40);
        int alive = 0;
        for (String id : entityIds) {
            EntityType<?> type = resolveType(id);
            if (type == null) {
                continue;
            }
            alive += level.getEntities(type, e -> e.isAlive() && arenaBox.contains(e.position())).size();
        }
        return alive;
    }

    private static void spawnTarget(ServerLevel level, EntityMaid maid) {
        // P0 修复（2026-09-10）：空列表保护——旧实现在 entityIds 为空时
        // `targetCursor % entityIds.length` 直接 ArithmeticException 打进 tick 事件。
        if (entityIds.length == 0) {
            LOGGER.error("[Arena] entityIds is empty, skip target spawn (check arena.properties entity/entity_blacklist)");
            return;
        }
        String id = entityIds[targetCursor % entityIds.length];
        targetCursor++;
        EntityType<?> type = resolveType(id);
        if (type == null) {
            LOGGER.warn("[Arena] unknown entity type: {}", id);
            return;
        }
        // 斗兽场开启时：Boss 生成在墙内（半径 r-2），避免生成到墙外再被拉回打断 AI
        int limit = cageEnabled ? Math.max(2, (int) Math.round(cageCurrentRadius) - 2) : spawnDistance;
        int ox = RANDOM.nextInt(limit * 2 + 1) - limit;
        int oz = RANDOM.nextInt(limit * 2 + 1) - limit;
        int y = platformY();
        BlockPos pos = new BlockPos(centerX + ox, y, centerZ + oz);
        Entity target = type.create(level);
        if (target == null) {
            return;
        }
        // 防 despawn：专用服务器无玩家，Boss 会按原版 despawn 规则在 ~20 秒内消失（刷怪循环无击杀）
        if (target instanceof net.minecraft.world.entity.Mob mob) {
            mob.setPersistenceRequired();
            // AV 召回机制：HerobrineMob.recallTicks 倒计时到点"传送召回"（Boss 6 秒消失的根因）。
            // 反射置 neverRecall=true（该字段无 setter / NBT 读取为 protected），彻底禁用召回
            try {
                Class<?> hb = Class.forName("com.pla.annoyingvillagers.clazz.HerobrineMob");
                if (hb.isAssignableFrom(target.getClass())) {
                    java.lang.reflect.Field f = hb.getDeclaredField("neverRecall");
                    f.setAccessible(true);
                    f.setBoolean(target, true);
                }
            } catch (Throwable t) {
                LOGGER.warn("[Arena] NeverRecall patch failed: {}", t.toString());
            }
        }
        target.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, level.random.nextFloat() * 360, 0);
        level.addFreshEntity(target);
        LOGGER.info("[Arena] spawned target: {} at {}", id, pos);
    }

    // ------------------------------------------------------------------
    // 工具
    // ------------------------------------------------------------------

    private static ItemStack item(String id) {
        var item = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(id));
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    /** 实体站立面 y（石头平台层上方一格） */
    private static int platformY() {
        return PLATFORM_Y + 1;
    }
}
