package com.Yujin.onegradefixer.epicmoonmod.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EpicmoonItems {
   public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "epicmoonmod");
   public static final RegistryObject<Item> THUMB_GUNPOWDER = ITEMS.register("thumb_gunpowder", () -> new Item(new Properties()));
   public static final RegistryObject<Item> CARTRIDGE_CASE = ITEMS.register("cartridge_case", () -> new Item(new Properties()));
   public static final RegistryObject<Item> TIGERMARK_ROUND = ITEMS.register("tigermark_round", () -> new Ammoitems(1, new Properties()));
   public static final RegistryObject<Item> SAVAGE_TIGERMARK_ROUND = ITEMS.register("savage_tigermark_round", () -> new Ammoitems(2, new Properties()));
   public static final RegistryObject<Item> Accel_ROUND = ITEMS.register("accel_round", () -> new Ammoitems(3, new Properties()));
   public static final RegistryObject<Item> TENTAI_SEITOU = ITEMS.register(
      "tentai_seitou", () -> new Tentai_Seitouitems(Tiers.NETHERITE, 10, -2.8F, new Properties().m_41497_(Rarity.EPIC))
   );
   public static final RegistryObject<Item> VALENCINA_DUAL_SWORDS = ITEMS.register(
      "valencina_dual_swords", () -> new DualSwordsItem(Tiers.NETHERITE, 3, -2.0F, new Properties().m_41497_(Rarity.EPIC))
   );
   public static final RegistryObject<Item> LEIHENG_HAT = ITEMS.register(
      "leiheng_hat", () -> new LeiHengArmorItem(EMArmorMaterials.LEIHENG, Type.HELMET, new Properties().m_41497_(Rarity.EPIC))
   );
   public static final RegistryObject<Item> LEIHENG_SUIT = ITEMS.register(
      "leiheng_suit", () -> new LeiHengArmorItem(EMArmorMaterials.LEIHENG, Type.CHESTPLATE, new Properties().m_41497_(Rarity.EPIC))
   );
   public static final RegistryObject<Item> LEIHENG_PANTS = ITEMS.register(
      "leiheng_pants", () -> new LeiHengArmorItem(EMArmorMaterials.LEIHENG, Type.LEGGINGS, new Properties().m_41497_(Rarity.EPIC))
   );
   public static final RegistryObject<Item> LEIHENG_SHOES = ITEMS.register(
      "leiheng_shoes", () -> new LeiHengArmorItem(EMArmorMaterials.LEIHENG, Type.BOOTS, new Properties().m_41497_(Rarity.EPIC))
   );

   public static void register(IEventBus eventBus) {
      ITEMS.register(eventBus);
   }
}
