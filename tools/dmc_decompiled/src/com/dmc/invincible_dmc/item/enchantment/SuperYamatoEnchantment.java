package com.dmc.invincible_dmc.item.enchantment;

import com.dmc.invincible_dmc.item.YamatoItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantment.Rarity;

public final class SuperYamatoEnchantment extends Enchantment {
   private static final EnchantmentCategory YAMATO = EnchantmentCategory.create("invincible_dmc_super_yamato", item -> item instanceof YamatoItem);

   public SuperYamatoEnchantment() {
      super(Rarity.VERY_RARE, YAMATO, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
   }

   public boolean m_6081_(ItemStack stack) {
      return stack.m_41720_() instanceof YamatoItem;
   }

   public boolean m_6591_() {
      return true;
   }

   public int m_6586_() {
      return 1;
   }
}
