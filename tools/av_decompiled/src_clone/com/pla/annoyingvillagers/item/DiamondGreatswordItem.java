package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

public class DiamondGreatswordItem extends SwordItem {
   public DiamondGreatswordItem() {
      super(new Tier() {
         public int m_6609_() {
            return 1561;
         }

         public float m_6624_() {
            return 6.0F;
         }

         public float m_6631_() {
            return 3.0F;
         }

         public int m_6604_() {
            return 3;
         }

         public int m_6601_() {
            return 20;
         }

         @NotNull
         public Ingredient m_6282_() {
            return Ingredient.m_43927_(new ItemStack[]{new ItemStack((ItemLike)AnnoyingVillagersModItems.COMPRESSED_DIAMOND.get())});
         }
      }, 3, -2.5F, new Properties());
   }
}
