package com.pla.annoyingvillagers.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class CraftingTableItem extends SwordItem {
   public CraftingTableItem() {
      super(new Tier() {
         public int m_6609_() {
            return 20;
         }

         public float m_6624_() {
            return 4.0F;
         }

         public float m_6631_() {
            return 2.0F;
         }

         public int m_6604_() {
            return 1;
         }

         public int m_6601_() {
            return 4;
         }

         @NotNull
         public Ingredient m_6282_() {
            return Ingredient.m_43927_(new ItemStack[]{new ItemStack(Blocks.f_50091_)});
         }
      }, 1, -2.8F, new Properties());
   }

   public void m_7373_(@NotNull ItemStack itemstack, Level level, @NotNull List<Component> list, @NotNull TooltipFlag tooltipflag) {
      super.m_7373_(itemstack, level, list, tooltipflag);
      list.add(Component.m_237115_("tooltip.annoyingvillagers.crafting_table"));
   }
}
