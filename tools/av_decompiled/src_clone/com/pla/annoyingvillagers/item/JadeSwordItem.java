package com.pla.annoyingvillagers.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class JadeSwordItem extends SwordItem {
   public JadeSwordItem() {
      super(new Tier() {
         public int m_6609_() {
            return 685;
         }

         public float m_6624_() {
            return 4.0F;
         }

         public float m_6631_() {
            return 2.5F;
         }

         public int m_6604_() {
            return 1;
         }

         public int m_6601_() {
            return 18;
         }

         @NotNull
         public Ingredient m_6282_() {
            return Ingredient.m_43927_(new ItemStack[]{new ItemStack(Items.f_42616_)});
         }
      }, 3, -2.5F, new Properties());
   }
}
