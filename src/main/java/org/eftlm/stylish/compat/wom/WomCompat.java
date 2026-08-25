package org.eftlm.stylish.compat.wom;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reascer.wom.world.item.WOMItems;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

import java.util.Map;

/**
 * 奇迹武器（WOM）行为注册（移植自 EFTLM_WOM 的武器行为，适配 EFTLM 1.3 API）。
 * <p>
 * 女仆主手持有 WOM 武器时，EpicFight 的 AnimatedAttackGoal 按物品查表
 * 自动使用对应的连招行为；技能条目用主手物品冷却模拟技能 CD。
 */
public final class WomCompat {

    private static final Logger LOGGER = LogManager.getLogger("eftlm_stylish");

    private WomCompat() {
    }

    public static void trySetWeaponMotions(Map<Item, CombatBehaviors.Builder<HumanoidMobPatch<?>>> itemAttackMotions,
                                           Map<Item, Map<Style, CombatBehaviors.Builder<HumanoidMobPatch<?>>>> itemStyleAttackMotions,
                                           Map<Item, HumanoidArmature> itemArmatures) {
        if (!WomSkillChecks.LoadedWOM()) {
            return;
        }
        try {
            registerAll(itemAttackMotions, itemStyleAttackMotions, itemArmatures);
            LOGGER.info("[DIAG] WOM behaviors registered: {} items, {} style-mapped items",
                    itemAttackMotions.size(), itemStyleAttackMotions.size());
        } catch (Throwable t) {
            LOGGER.error("[DIAG] WOM behavior register FAILED!", t);
        }
    }

    private static void registerAll(Map<Item, CombatBehaviors.Builder<HumanoidMobPatch<?>>> itemAttackMotions,
                                    Map<Item, Map<Style, CombatBehaviors.Builder<HumanoidMobPatch<?>>>> itemStyleAttackMotions,
                                    Map<Item, HumanoidArmature> itemArmatures) {
        itemAttackMotions.put(WOMItems.MOONLESS.get(), Moonless.Instance);
        itemAttackMotions.put(WOMItems.RUINE.get(), Ruine.Instance);
        registerItemStyle(itemStyleAttackMotions, WOMItems.ENDER_BLASTER.get(), ImmutableMap.of(
                Styles.ONE_HAND, Ender_Blaster_OneHand.Instance,
                Styles.TWO_HAND, Ender_Blaster_TwoHand.Instance));
        itemAttackMotions.put(WOMItems.WOODEN_STAFF.get(), Staff.Instance);
        itemAttackMotions.put(WOMItems.STONE_STAFF.get(), Staff.Instance);
        itemAttackMotions.put(WOMItems.IRON_STAFF.get(), Staff.Instance);
        itemAttackMotions.put(WOMItems.GOLDEN_STAFF.get(), Staff.Instance);
        itemAttackMotions.put(WOMItems.DIAMOND_STAFF.get(), Staff.Instance);
        itemAttackMotions.put(WOMItems.NETHERITE_STAFF.get(), Staff.Instance);
        itemAttackMotions.put(WOMItems.HERRSCHER.get(), Herrscher.Instance);
        itemAttackMotions.put(WOMItems.SOLAR.get(), Solar.Instance);
        itemAttackMotions.put(WOMItems.AGONY.get(), Agony.Instance);
        itemAttackMotions.put(WOMItems.TORMENTED_MIND.get(), Torment.Instance);
        itemAttackMotions.put(WOMItems.SATSUJIN.get(), Satsujin.Instance);
        itemAttackMotions.put(WOMItems.ANTITHEUS.get(), Antitheus.Instance);
        itemAttackMotions.put(WOMItems.NAPOLEON.get(), Napoleon.Instance);
        itemAttackMotions.put(WOMItems.BLACKSTAR.get(), Blackstar.Instance);
        itemAttackMotions.put(WOMItems.ORBIT.get(), Orbit.Instance);
        registerItemStyle(itemStyleAttackMotions, WOMItems.NOVA.get(), ImmutableMap.of(
                Styles.ONE_HAND, Nova_OneHand.Instance,
                Styles.TWO_HAND, Nova_TwoHand.Instance));
        itemAttackMotions.put(WOMItems.EVIL_TACHI.get(), Evil_Tachi.Instance);
        itemAttackMotions.put(WOMItems.CELERITY_BRACELET.get(), Clawed_Gauntle.Instance);
    }

    /**
     * 以可变 Map 作为 value 注册物品风格行为并整体替换已有条目。
     * 兼容 EFTLM 1.3.2 的 computeIfAbsent+putAll 合并逻辑（value 必须可变）。
     */
    private static void registerItemStyle(Map<Item, Map<Style, CombatBehaviors.Builder<HumanoidMobPatch<?>>>> map,
                                          Item item, Map<Style, CombatBehaviors.Builder<HumanoidMobPatch<?>>> motions) {
        map.put(item, new java.util.HashMap<>(motions));
    }
}
