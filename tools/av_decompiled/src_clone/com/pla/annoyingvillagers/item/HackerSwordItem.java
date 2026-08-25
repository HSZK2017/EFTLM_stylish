package com.pla.annoyingvillagers.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class HackerSwordItem extends SwordItem {
   public HackerSwordItem() {
      super(new Tier() {
         public int m_6609_() {
            return 250;
         }

         public float m_6624_() {
            return 6.0F;
         }

         public float m_6631_() {
            return 2.2F;
         }

         public int m_6604_() {
            return 5;
         }

         public int m_6601_() {
            return 21;
         }

         @NotNull
         public Ingredient m_6282_() {
            return Ingredient.m_43927_(new ItemStack[]{new ItemStack(Items.f_42416_)});
         }
      }, 3, -2.4F, new Properties());
   }
}
