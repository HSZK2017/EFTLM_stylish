package com.pla.annoyingvillagers.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

public class WoopieTheSwordItem extends SwordItem {
   public WoopieTheSwordItem() {
      super(new Tier() {
         public int m_6609_() {
            return 1850;
         }

         public float m_6624_() {
            return 8.0F;
         }

         public float m_6631_() {
            return 3.5F;
         }

         public int m_6604_() {
            return 3;
         }

         public int m_6601_() {
            return 10;
         }

         public Ingredient m_6282_() {
            return Ingredient.m_43927_(new ItemStack[]{new ItemStack(Items.f_42415_)});
         }
      }, 3, -2.8F, new Properties().m_41486_());
   }

   public void m_7373_(ItemStack itemstack, Level level, List<Component> list, TooltipFlag tooltipflag) {
      super.m_7373_(itemstack, level, list, tooltipflag);
      list.add(Component.m_237115_("tooltip.annoyingvillagers.woopie_the_sword"));
   }
}
