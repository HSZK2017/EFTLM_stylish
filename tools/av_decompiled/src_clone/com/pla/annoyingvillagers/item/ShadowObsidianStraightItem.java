package com.pla.annoyingvillagers.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ShadowObsidianStraightItem extends Item {
   public ShadowObsidianStraightItem() {
      super(new Properties().m_41487_(1).m_41486_().m_41497_(Rarity.EPIC));
   }

   public boolean m_5812_(@NotNull ItemStack stack) {
      return stack.m_41782_() && stack.m_41783_() != null && stack.m_41783_().m_128471_("foil");
   }

   public boolean m_8096_(@NotNull BlockState blockstate) {
      return true;
   }
}
