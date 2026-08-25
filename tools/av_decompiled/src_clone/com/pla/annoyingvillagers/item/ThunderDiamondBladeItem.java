package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

public class ThunderDiamondBladeItem extends SwordItem {
   public ThunderDiamondBladeItem() {
      super(new Tier() {
         public int m_6609_() {
            return 2561;
         }

         public float m_6624_() {
            return 6.0F;
         }

         public float m_6631_() {
            return 4.0F;
         }

         public int m_6604_() {
            return 5;
         }

         public int m_6601_() {
            return 21;
         }

         @NotNull
         public Ingredient m_6282_() {
            return Ingredient.m_43927_(new ItemStack[]{new ItemStack((ItemLike)AnnoyingVillagersModItems.COMPRESSED_DIAMOND.get())});
         }
      }, 3, -2.0F, new Properties());
   }

   public boolean m_5812_(@NotNull ItemStack pStack) {
      return true;
   }
}
