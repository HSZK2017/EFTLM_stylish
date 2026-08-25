package org.eftlm.stylish.compat.efn;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.EFTLM.EF.Animation.CombatBehavior.BehaviorsBuild;
import net.EFTLM.EF.Capability.MaidPatch;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eftlm.stylish.strategy.StyleState;
import org.eftlm.stylish.strategy.StylishConditions;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * EFN 武器技能目录（运行时注册表）：
 * <ul>
 *     <li>数据源：config/eftlm_stylish/skills.json（存在时优先），否则 jar 内默认
 *         /eftlm_stylish/skills.json——由 tools/extract_efn_skills.ps1 从 EFN jar
 *         动画资产提取，无需反编译 EFN；</li>
 *     <li>运行时校验：每个技能的动画键经 AnimationManager 解析，未注册的动画
 *         （EFN 未安装 / 升级后键名变化）自动不可用；</li>
 *     <li>门控：EFT LM 有 WeaponInnateSkill 的武器走 BehaviorsBuild 层数，
 *         其余走本模组 StyleState 层数；</li>
 *     <li>释放条件：本模组战斗谓词（MELEE / MID_RANGE / AIRBORNE），与 EFN
 *         玩家按键输入条件无关。</li>
 * </ul>
 */
public final class EfnSkillCatalog {

    private static final Logger LOGGER = LogManager.getLogger("eftlm_stylish");
    private static final String DEFAULT_RESOURCE = "/eftlm_stylish/skills.json";

    private static volatile EfnSkillCatalog INSTANCE;

    /** 每个女仆的技能冷却（技能 id -> 上次释放 tick，RL 与规则兜底共用） */
    private static final Map<java.util.UUID, Map<String, Integer>> SKILL_COOLDOWNS = new HashMap<>();

    /** 武器目录名 -> 条目 */
    private final Map<String, WeaponEntry> byDir;
    /** 物品注册路径 -> 条目 */
    private final Map<String, WeaponEntry> byItem;
    /** 原版 EF 武器类别名（CapabilityItem.WeaponCategories 枚举名）-> 条目 */
    private final Map<String, WeaponEntry> byCategory;

    public static final class WeaponEntry {
        private final String dir;
        private final List<SkillSpec> skills;

        WeaponEntry(String dir, List<SkillSpec> skills) {
            this.dir = dir;
            this.skills = skills;
        }

        public String dir() {
            return dir;
        }

        public List<SkillSpec> skills() {
            return skills;
        }
    }

    private EfnSkillCatalog(Map<String, WeaponEntry> byDir, Map<String, WeaponEntry> byItem, Map<String, WeaponEntry> byCategory) {
        this.byDir = byDir;
        this.byItem = byItem;
        this.byCategory = byCategory;
    }

    public static EfnSkillCatalog get() {
        EfnSkillCatalog c = INSTANCE;
        if (c == null) {
            synchronized (EfnSkillCatalog.class) {
                if (INSTANCE == null) {
                    INSTANCE = load();
                }
                c = INSTANCE;
            }
        }
        return c;
    }

    /** 重载配置（RCON / 命令用） */
    public static void reload() {
        synchronized (EfnSkillCatalog.class) {
            EfnSkillCatalog old = INSTANCE;
            EfnSkillCatalog next = load();
            INSTANCE = next;
            org.eftlm.stylish.rl.CommitmentCatalog.invalidate(); // 技能目录变化 → 帧数据缓存失效
            LOGGER.info("[SKILLS] catalog reloaded: {} weapons, {} skills (was {})",
                    next.byDir.size(), next.totalSkills(),
                    old == null ? "none" : old.totalSkills());
        }
    }

    private int totalSkills() {
        return byDir.values().stream().mapToInt(e -> e.skills.size()).sum();
    }

    private static EfnSkillCatalog load() {
        Path config = FMLPaths.CONFIGDIR.get().resolve("eftlm_stylish").resolve("skills.json");
        try (Reader reader = Files.exists(config)
                ? Files.newBufferedReader(config, StandardCharsets.UTF_8)
                : new java.io.InputStreamReader(
                        EfnSkillCatalog.class.getResourceAsStream(DEFAULT_RESOURCE), StandardCharsets.UTF_8)) {
            JsonObject root = new Gson().fromJson(reader, JsonObject.class);
            EfnSkillCatalog catalog = parse(root);
            LOGGER.info("[SKILLS] catalog loaded: {} weapons, {} skills (source={})",
                    catalog.byDir.size(), catalog.totalSkills(),
                    Files.exists(config) ? config : "jar:" + DEFAULT_RESOURCE);
            return catalog;
        } catch (Exception e) {
            LOGGER.error("[SKILLS] failed to load skills.json, catalog empty", e);
            return new EfnSkillCatalog(Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
        }
    }

    private static EfnSkillCatalog parse(JsonObject root) {
        Map<String, WeaponEntry> byDir = new HashMap<>();
        Map<String, WeaponEntry> byItem = new HashMap<>();
        Map<String, WeaponEntry> byCategory = new HashMap<>();
        if (root != null && root.has("weapons")) {
            JsonObject weapons = root.getAsJsonObject("weapons");
            for (Map.Entry<String, JsonElement> e : weapons.entrySet()) {
                String dir = e.getKey();
                try {
                    JsonObject w = e.getValue().getAsJsonObject();
                    List<SkillSpec> skills = parseSkills(w);
                    WeaponEntry entry = new WeaponEntry(dir, skills);
                    byDir.put(dir, entry);
                    // items 字段两种格式并存：裸字符串（"items": "agony"）或数组
                    // （"items": ["crescent_moon", ...]）。此前无条件 getAsJsonArray
                    // 遇到字符串即抛 ClassCastException，导致整个目录加载失败。
                    if (w.has("items")) {
                        JsonElement itemsEl = w.get("items");
                        if (itemsEl.isJsonArray()) {
                            for (JsonElement it : itemsEl.getAsJsonArray()) {
                                byItem.put(it.getAsString(), entry);
                            }
                        } else if (itemsEl.isJsonPrimitive()) {
                            byItem.put(itemsEl.getAsString(), entry);
                        }
                    }
                } catch (Exception ex) {
                    // 单条目容错：一个武器条目损坏不应清空整个目录
                    LOGGER.error("[SKILLS] failed to parse weapon entry '{}', skipped", dir, ex);
                }
            }
        }
        if (root != null && root.has("categories")) {
            JsonObject categories = root.getAsJsonObject("categories");
            for (Map.Entry<String, JsonElement> e : categories.entrySet()) {
                try {
                    JsonObject w = e.getValue().getAsJsonObject();
                    byCategory.put(e.getKey(), new WeaponEntry(e.getKey(), parseSkills(w)));
                } catch (Exception ex) {
                    LOGGER.error("[SKILLS] failed to parse category entry '{}', skipped", e.getKey(), ex);
                }
            }
        }
        return new EfnSkillCatalog(byDir, byItem, byCategory);
    }

    private static List<SkillSpec> parseSkills(JsonObject w) {
        List<SkillSpec> skills = new ArrayList<>();
        if (w.has("skills")) {
            JsonArray arr = w.getAsJsonArray("skills");
            for (JsonElement se : arr) {
                JsonObject o = se.getAsJsonObject();
                String id = str(o, "id");
                String anim = str(o, "anim");
                if (id.isEmpty() || anim.isEmpty()) {
                    continue;
                }
                skills.add(new SkillSpec(id, anim, i(o, "cd", 120), i(o, "cost", 1),
                        SkillSpec.Condition.of(str(o, "cond")),
                        SkillSpec.Gate.of(str(o, "resource"))));
            }
        }
        return skills;
    }

    private static String str(JsonObject o, String key) {
        JsonElement e = o.get(key);
        return e == null || e.isJsonNull() ? "" : e.getAsString();
    }

    private static int i(JsonObject o, String key, int def) {
        JsonElement e = o.get(key);
        try {
            return e == null || e.isJsonNull() ? def : e.getAsInt();
        } catch (Exception ex) {
            return def;
        }
    }

    // ------------------------------------------------------------------
    // 查询
    // ------------------------------------------------------------------

    /** 主手物品对应的武器条目（非 EFN 武器返回 null） */
    public WeaponEntry entryFor(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        ResourceLocation rl = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (rl == null) {
            return null;
        }
        // 全名（含命名空间）优先，兼容历史 path-only 键
        WeaponEntry entry = byItem.get(rl.toString());
        return entry != null ? entry : byItem.get(rl.getPath());
    }

    /** 按武器目录名查条目（测试模式 / 诊断用） */
    public WeaponEntry entryByDir(String dir) {
        return byDir.get(dir);
    }

    /** 主手物品的全部技能（含动画未注册的条目，调用方用 resolve() 校验） */
    public List<SkillSpec> skillsFor(ItemStack stack) {
        WeaponEntry entry = entryFor(stack);
        return entry == null ? Collections.emptyList() : entry.skills;
    }

    /**
     * 女仆当前主手的技能集合：物品目录条目优先；无条目时回退原版 EF 武器类别
     * （minecraft 原版剑/斧等按 EpicFight 类别挂载天赋技能）。两者皆无 → 空列表
     * （默认置空：不猜测、不回退其它武器的技能，避免技能与武器不匹配）。
     */
    public static List<SkillSpec> skillsOf(MaidPatch<?> patch) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        List<SkillSpec> s = get().skillsFor(maid.getMainHandItem());
        if (!s.isEmpty()) {
            return s;
        }
        yesman.epicfight.world.capabilities.item.WeaponCategory category =
                org.eftlm.stylish.util.AnimKit.categoryOf(patch);
        if (category instanceof yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories c) {
            WeaponEntry entry = get().byCategory.get(c.name());
            if (entry != null) {
                return entry.skills;
            }
        }
        return Collections.emptyList();
    }

    public Map<String, WeaponEntry> entries() {
        return Collections.unmodifiableMap(byDir);
    }

    // ------------------------------------------------------------------
    // 运行时解析 / 门控 / 释放
    // ------------------------------------------------------------------

    /** 技能槽位排序（决策布局与规则兜底共用）：突进 > 浮空 > 近战 > 无条件 */
    public static int rank(SkillSpec spec) {
        return switch (spec.condition()) {
            case MID_RANGE -> 0;
            case AIRBORNE -> 1;
            case MELEE -> 2;
            default -> 3;
        };
    }

    /** 技能冷却查询（RL 与规则兜底共用同一冷却表，防止双驱动重复释放） */
    public static boolean isCooling(EntityMaid maid, SkillSpec spec, int tick) {
        Map<String, Integer> m = SKILL_COOLDOWNS.get(maid.getUUID());
        if (m == null) {
            return false;
        }
        Integer last = m.get(spec.id());
        return last != null && last + spec.cooldownTicks() > tick;
    }

    /** 记录技能释放时间（进入冷却） */
    public static void markUsed(EntityMaid maid, SkillSpec spec, int tick) {
        SKILL_COOLDOWNS.computeIfAbsent(maid.getUUID(), k -> new HashMap<>()).put(spec.id(), tick);
    }

    /** 女仆被击杀 / 移除时释放冷却记录 */
    public static void forgetMaid(java.util.UUID id) {
        SKILL_COOLDOWNS.remove(id);
    }

    /**
     * 决策点可用技能布局：主手武器的目录技能中「动画已注册 + 条件满足 + 门控充足 +
     * 不在冷却」者，按 {@link #rank} 排序后截断到 {@code RlActEvent.MAX_SKILL_SLOTS}。
     * RL 技能槽位行动 11..10+N 依次对应本列表。
     */
    public static List<SkillSpec> availableSkills(MaidPatch<?> patch) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        int tick = maid.tickCount;
        List<SkillSpec> out = new ArrayList<>();
        for (SkillSpec s : skillsOf(patch)) {
            if (resolve(s) == null) {
                continue;
            }
            if (isCooling(maid, s, tick)) {
                continue;
            }
            if (!matches(patch, s)) {
                continue;
            }
            if (!canRelease(patch, s)) {
                continue;
            }
            out.add(s);
        }
        out.sort(java.util.Comparator.comparingInt(EfnSkillCatalog::rank).thenComparing(SkillSpec::id));
        if (out.size() > org.eftlm.stylish.rl.RlActEvent.MAX_SKILL_SLOTS) {
            return new ArrayList<>(out.subList(0, org.eftlm.stylish.rl.RlActEvent.MAX_SKILL_SLOTS));
        }
        return out;
    }

    /** 动画键 -> AnimationManager 解析（未注册返回 null） */
    public static AnimationManager.AnimationAccessor<? extends StaticAnimation> resolve(SkillSpec spec) {
        return EfnAnim.byFullKey(spec.animKey());
    }

    /** 战斗条件谓词匹配（本模组传感器） */
    public static boolean matches(MaidPatch<?> patch, SkillSpec spec) {
        return switch (spec.condition()) {
            case MID_RANGE -> StylishConditions.inMidRange(patch);
            case AIRBORNE -> StylishConditions.targetAirborne(patch);
            case MELEE -> StylishConditions.inMelee(patch);
            default -> true;
        };
    }

    /** 资源门控：动画已注册且层数/充能充足 */
    public static boolean canRelease(MaidPatch<?> patch, SkillSpec spec) {
        if (resolve(spec) == null) {
            return false;
        }
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        return switch (spec.gate()) {
            case OWN_STACK -> StyleState.getSkillStack(maid) >= spec.cost();
            case EFTLM_STACK -> BehaviorsBuild.getStack(patch) >= spec.cost();
            default -> true;
        };
    }

    /**
     * 释放技能：播放动画（EFN 动画自带伤害 / 位移事件，播放即结算）并消耗资源。
     *
     * @return 是否成功释放
     */
    public static boolean release(MaidPatch<?> patch, SkillSpec spec) {
        AnimationManager.AnimationAccessor<? extends StaticAnimation> anim = resolve(spec);
        if (anim == null || !canRelease(patch, spec)) {
            return false;
        }
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        patch.playAnimationSynchronized(anim, 0F);
        switch (spec.gate()) {
            case OWN_STACK -> StyleState.setSkillStack(maid, StyleState.getSkillStack(maid) - spec.cost());
            case EFTLM_STACK -> BehaviorsBuild.setStack(patch, Math.max(0, BehaviorsBuild.getStack(patch) - spec.cost()));
            default -> {
            }
        }
        return true;
    }

    // ------------------------------------------------------------------
    // P2 稳定槽位布局（槽位语义 = 技能身份，与可用性无关；可用性走掩码）
    // ------------------------------------------------------------------

    /**
     * P2 稳定槽位布局：当前主手武器的<b>全部</b>技能按 rank+id 稳定排序占位
     * （与冷却/条件/资源等可用性<b>无关</b>），截断到 {@code RlActEvent.MAX_SKILL_SLOTS}。
     * <p>
     * 修复两类槽位漂移（报告 3.4.2/附录）：
     * ① 冷却/条件不满足时剔除技能导致后续技能前移（同一槽位随时间指向不同技能）；
     * ② 同武器内技能顺序始终确定（rank+id 字典序），模型可学习排序规律。
     * 可用性由 {@link #isAvailable} 判定，决策布局中以 null 槽位（掩码置零）表达。
     */
    public static List<SkillSpec> stableSkills(MaidPatch<?> patch) {
        List<SkillSpec> out = new ArrayList<>(skillsOf(patch));
        out.sort(java.util.Comparator.comparingInt(EfnSkillCatalog::rank).thenComparing(SkillSpec::id));
        if (out.size() > org.eftlm.stylish.rl.RlActEvent.MAX_SKILL_SLOTS) {
            return new ArrayList<>(out.subList(0, org.eftlm.stylish.rl.RlActEvent.MAX_SKILL_SLOTS));
        }
        return out;
    }

    /** P2 稳定槽位可用性掩码（动画注册 + 冷却 + 条件 + 资源门控） */
    public static boolean isAvailable(MaidPatch<?> patch, SkillSpec spec) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        int tick = maid.tickCount;
        return resolve(spec) != null
                && !isCooling(maid, spec, tick)
                && matches(patch, spec)
                && canRelease(patch, spec);
    }

    // ------------------------------------------------------------------
    // 运行时动画清单 dump（替代反编译的侦察工具）
    // ------------------------------------------------------------------

    /**
     * 把运行时 AnimationManager 中注册的全部 EFN / Enhance / WOM 动画键写入 JSON，
     * 供核对 skills.json 分类与发现缺失技能（不触碰任何模组字节码）。
     */
    public static void dump(Path out) {
        try {
            Files.createDirectories(out.getParent());
            Map<ResourceLocation, AnimationManager.AnimationAccessor<? extends StaticAnimation>> anims =
                    AnimationManager.getInstance().getAnimations(a -> {
                        try {
                            String ns = a.registryName().getNamespace();
                            return ns.equals("efn") || ns.equals("efn_enhance") || ns.equals("wom")
                                    || ns.equals("annoyingvillagers") || ns.equals("epicfight");
                        } catch (Exception e) {
                            return false;
                        }
                    });
            TreeMap<String, String> sorted = new TreeMap<>();
            for (ResourceLocation rl : anims.keySet()) {
                sorted.put(rl.toString(), anims.get(rl).isPresent() ? "ok" : "missing");
            }
            EfnSkillCatalog catalog = get();
            int covered = 0;
            int missing = 0;
            for (WeaponEntry entry : catalog.entries().values()) {
                for (SkillSpec spec : entry.skills()) {
                    if (anims.containsKey(ResourceLocation.parse(spec.animKey()))) {
                        covered++;
                    } else {
                        missing++;
                    }
                }
            }
            JsonObject root = new JsonObject();
            JsonObject stats = new JsonObject();
            stats.addProperty("registered", anims.size());
            stats.addProperty("catalog_skills", catalog.totalSkills());
            stats.addProperty("catalog_covered", covered);
            stats.addProperty("catalog_missing", missing);
            root.add("stats", stats);
            JsonArray keys = new JsonArray();
            sorted.keySet().forEach(keys::add);
            root.add("animations", keys);
            try (Writer w = Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {
                new Gson().toJson(root, w);
            }
            LOGGER.info("[SKILLS] animation dump written: {} registered ({} efn/wom), catalog covered={} missing={} -> {}",
                    anims.size(), anims.size(), covered, missing, out);
        } catch (Exception e) {
            LOGGER.error("[SKILLS] dump failed", e);
        }
    }
}
