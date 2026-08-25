package com.dmc.invincible_dmc.item;

import com.dmc.invincible_dmc.block.DMCBlocks;
import com.dmc.invincible_dmc.entity.DMCEntities;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.world.item.WeaponItem;

public class DMCItems {
   public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "invincible_dmc");
   public static final RegistryObject<Item> DEBUG = ITEMS.register("debug", () -> new SwordItem(Tiers.WOOD, 3, -2.4F, new Properties()));
   public static final RegistryObject<Item> DEBUG_YAMATO = ITEMS.register("debug_yamato", () -> new SwordItem(Tiers.WOOD, 3, -2.4F, new Properties()));
   public static final RegistryObject<Item> CUSTOM_COMBO_DEMO = ITEMS.register("custom_combo_demo", () -> new SwordItem(Tiers.WOOD, 3, -2.4F, new Properties()));
   public static final RegistryObject<Item> CUSTOM_SKILL_DEMO = ITEMS.register("custom_skill_demo", () -> new SwordItem(Tiers.WOOD, 3, -2.4F, new Properties()));
   public static final RegistryObject<Item> POWER_CHAIR = ITEMS.register(
      "power_chair", () -> new BlockItem((Block)DMCBlocks.POWER_CHAIR.get(), new Properties())
   );
   public static final RegistryObject<WeaponItem> YAMATO_DMC4 = ITEMS.register(
      "yamato_dmc4", () -> new YamatoItem(Tiers.NETHERITE, -2, -3.0F, new Properties().m_41497_(Rarity.EPIC).m_41486_())
   );
   public static final RegistryObject<WeaponItem> YAMATO_DMC5 = ITEMS.register(
      "yamato_dmc5", () -> new YamatoItem(Tiers.NETHERITE, -2, -3.0F, new Properties().m_41497_(Rarity.EPIC).m_41486_())
   );
   public static final RegistryObject<WeaponItem> YAMATO_DMC5_MINI = ITEMS.register(
      "yamato_dmc5_mini", () -> new YamatoItem(Tiers.NETHERITE, -2, -3.0F, new Properties().m_41497_(Rarity.EPIC).m_41486_())
   );
   public static final RegistryObject<WeaponItem> YAMATO_DMC5_BD = ITEMS.register(
      "yamato_dmc5_bd", () -> new YamatoItem(Tiers.NETHERITE, -2, -3.0F, new Properties().m_41497_(Rarity.EPIC).m_41486_())
   );
   public static final RegistryObject<WeaponItem> DEVIL_SWORD_VERGIL = ITEMS.register(
      "devil_sword_vergil", () -> new YamatoItem(Tiers.NETHERITE, -1, -3.0F, new Properties().m_41497_(Rarity.EPIC).m_41486_())
   );
   public static final RegistryObject<Item> DUMMY_SPAWN_EGG = ITEMS.register(
      "dummy_spawn_egg", () -> new ForgeSpawnEggItem(DMCEntities.DUMMY, 7969893, 2771738, new Properties())
   );
   public static final RegistryObject<Item> DOPPELGANGER_SPAWN_EGG = ITEMS.register(
      "doppelganger_spawn_egg", () -> new DoppelgangerSpawnEggItem(DMCEntities.DOPPELGANGER, 13158, 3377356, new Properties())
   );
   public static final RegistryObject<SdtArmorItem> SDT_ARMOR = ITEMS.register("sdt_armor", SdtArmorItem::new);
}
