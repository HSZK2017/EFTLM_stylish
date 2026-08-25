package org.eftlm.stylish.compat.efn;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eftlm.stylish.compat.efn.weapons.Aetherialdusk;
import org.eftlm.stylish.compat.efn.weapons.Beastclaw;
import org.eftlm.stylish.compat.efn.weapons.Bloodlust;
import org.eftlm.stylish.compat.efn.weapons.BroadBlade;
import org.eftlm.stylish.compat.efn.weapons.CrescentMoon;
import org.eftlm.stylish.compat.efn.weapons.Exsiliumgladius;
import org.eftlm.stylish.compat.efn.weapons.HfBlade;
import org.eftlm.stylish.compat.efn.weapons.Kusabimaru;
import org.eftlm.stylish.compat.efn.weapons.Meenlance;
import org.eftlm.stylish.compat.efn.weapons.Murasama;
import org.eftlm.stylish.compat.efn.weapons.Pioneer;
import org.eftlm.stylish.compat.efn.weapons.Ruinsgreatsword;
import org.eftlm.stylish.compat.efn.weapons.Scythe;
import org.eftlm.stylish.compat.efn.weapons.Shortsword;
import org.eftlm.stylish.compat.efn.weapons.Thornwheel;
import org.eftlm.stylish.compat.efn.weapons.Yamato;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

import java.util.Map;

/**
 * EFN（史诗战斗·夜幕）武器行为注册。
 * <p>
 * 编译期不依赖 EFN：物品按注册名从 ForgeRegistries 运行时查找，
 * 动画经 {@link EfnAnim} 从 AnimationManager 运行时查找；
 * 未安装 EFN（或物品 / 动画缺失）时自动跳过，不影响其他武器行为。
 * <p>
 * 技能表见项目根目录 EFN_SKILLS.md；大招统一复用 {@link org.eftlm.stylish.strategy.SkillGate}
 * 的资源型门控（学习技能 + 满层 + 物品冷却），与 8 类原版武器一致。
 */
public final class EfnCompat {

    private static final Logger LOGGER = LogManager.getLogger("eftlm_stylish");

    private EfnCompat() {
    }

    public static boolean LoadedEFN() {
        return ModList.get().isLoaded("efn");
    }

    public static void trySetWeaponMotions(Map<Item, CombatBehaviors.Builder<HumanoidMobPatch<?>>> itemAttackMotions,
                                           Map<Item, Map<Style, CombatBehaviors.Builder<HumanoidMobPatch<?>>>> itemStyleAttackMotions,
                                           Map<Item, HumanoidArmature> itemArmatures) {
        if (!LoadedEFN()) {
            return;
        }
        try {
            registerAll(itemAttackMotions);
            LOGGER.info("[DIAG] EFN behaviors registered: {} items", itemAttackMotions.size());
        } catch (Throwable t) {
            LOGGER.error("[DIAG] EFN behavior register FAILED!", t);
        }
    }

    private static Item item(String name) {
        return ForgeRegistries.ITEMS.getValue(
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("efn", name));
    }

    private static void putItem(Map<Item, CombatBehaviors.Builder<HumanoidMobPatch<?>>> map, String name,
                                CombatBehaviors.Builder<HumanoidMobPatch<?>> builder) {
        Item item = item(name);
        if (item != null && item != Items.AIR && builder != null) {
            map.put(item, builder);
        }
    }

    private static void registerAll(Map<Item, CombatBehaviors.Builder<HumanoidMobPatch<?>>> itemAttackMotions) {
        // ============ EFTLM 原生 EFN 行为表（含武器技能释放 hasStack 系列） ============
        // V11 修复：此前自定义简版行为表覆盖了 EFTLM 原生注册（同 key 后写覆盖），
        // 导致女仆失去武器技能（blast sword / flarecut 等 hasStack 技能行为）。
        // 现在直接复用 EFTLM 原生行为表（public 类/Instance），保证技能释放链路完整。
        putItem(itemAttackMotions, "yamato_dmc", net.EFTLM.EF.Animation.CombatBehavior.EFN.Yamato.getInstance());
        putItem(itemAttackMotions, "yamato_dmc4", net.EFTLM.EF.Animation.CombatBehavior.EFN.Yamato.getInstance());
        putItem(itemAttackMotions, "yamato_dmc_in_sheath", net.EFTLM.EF.Animation.CombatBehavior.EFN.Yamato.getInstance());
        putItem(itemAttackMotions, "yamato_dmc4_in_sheath", net.EFTLM.EF.Animation.CombatBehavior.EFN.Yamato.getInstance());
        putItem(itemAttackMotions, "hf_murasama", net.EFTLM.EF.Animation.CombatBehavior.EFN.HF_Murasama.Instance);
        putItem(itemAttackMotions, "hf_blade", net.EFTLM.EF.Animation.CombatBehavior.EFN.HF_Blade.Instance);
        putItem(itemAttackMotions, "ruinsgreatsword", net.EFTLM.EF.Animation.CombatBehavior.EFN.Ruins_GreatSword.Instance);
        putItem(itemAttackMotions, "meen_spear", net.EFTLM.EF.Animation.CombatBehavior.EFN.MeenSpear.Instance);
        putItem(itemAttackMotions, "nf_dual_sword", net.EFTLM.EF.Animation.CombatBehavior.EFN.Aetherial_Dusk.Instance);
        putItem(itemAttackMotions, "broadblade", net.EFTLM.EF.Animation.CombatBehavior.EFN.BroadBlade.Instance);
        putItem(itemAttackMotions, "nf_claw", net.EFTLM.EF.Animation.CombatBehavior.EFN.Claw.Instance);
        putItem(itemAttackMotions, "air_tachi", net.EFTLM.EF.Animation.CombatBehavior.EFN.BloodLust.Instance);
        putItem(itemAttackMotions, "air_tachi_e", net.EFTLM.EF.Animation.CombatBehavior.EFN.BloodLust.Instance);
        putItem(itemAttackMotions, "co_tachi", net.EFTLM.EF.Animation.CombatBehavior.EFN.BloodLust.Instance);
        putItem(itemAttackMotions, "sword_of_pioneer", net.EFTLM.EF.Animation.CombatBehavior.EFN.Pioneer.Instance);
        putItem(itemAttackMotions, "nf_shortsword", net.EFTLM.EF.Animation.CombatBehavior.EFN.ShortSword.Instance);
        putItem(itemAttackMotions, "nf_shortsword_2", net.EFTLM.EF.Animation.CombatBehavior.EFN.ShortSword.Instance);
        putItem(itemAttackMotions, "crimson_moon", net.EFTLM.EF.Animation.CombatBehavior.EFN.Scythe.Instance);
        putItem(itemAttackMotions, "crescent_moon", net.EFTLM.EF.Animation.CombatBehavior.EFN.Crescent.Instance);
        putItem(itemAttackMotions, "kusabimaru", net.EFTLM.EF.Animation.CombatBehavior.EFN.Kusabimaru.Instance);
        // ============ 自定义兜底：EFTLM 原生未覆盖的强化/变体武器 ============
        putItem(itemAttackMotions, "meen_spear_e", Meenlance.Instance);
        putItem(itemAttackMotions, "exsiliumgladius", Exsiliumgladius.Instance);
        putItem(itemAttackMotions, "exsiliumgladius_e", Exsiliumgladius.Instance);
        putItem(itemAttackMotions, "fire_exsiliumgladius", Exsiliumgladius.Instance);
        putItem(itemAttackMotions, "fire_exsiliumgladius_e", Exsiliumgladius.Instance);
        putItem(itemAttackMotions, "nf_shortsword_e", Shortsword.Instance);
        putItem(itemAttackMotions, "nf_shortsword_2_e", Shortsword.Instance);
        putItem(itemAttackMotions, "crimson_moon_e", Scythe.Instance);
        putItem(itemAttackMotions, "crescent_moon_e", CrescentMoon.Instance);
        putItem(itemAttackMotions, "flag_bearer", CrescentMoon.Instance);
        putItem(itemAttackMotions, "flag_bearer_e", CrescentMoon.Instance);
        putItem(itemAttackMotions, "thornwheel", Thornwheel.Instance);
        // 不注册：arc_tachi（示例测试能力）、excalibur（EFN 无能力表，由 epic_fight_avalon 兼容处理）
    }
}
