package com.pla.annoyingvillagers.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class IronCleaverItem extends SwordItem {
   public IronCleaverItem() {
      super(new Tier() {
         public int m_6609_() {
            return 250;
         }

         public float m_6624_() {
            return 6.0F;
         }

         public float m_6631_() {
            return 2.4F;
         }

         public int m_6604_() {
            return 3;
         }

         public int m_6601_() {
            return 20;
         }

         @NotNull
         public Ingredient m_6282_() {
            return Ingredient.m_43927_(new ItemStack[]{new ItemStack(Items.f_42416_)});
         }
      }, 3, -3.2F, new Properties());
   }
}
