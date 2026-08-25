package com.pla.annoyingvillagers.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import org.jetbrains.annotations.NotNull;

public class GiantRedAxeItem extends Item {
   public GiantRedAxeItem() {
      super(new Properties().m_41487_(1).m_41497_(Rarity.UNCOMMON));
   }

   public boolean m_5812_(@NotNull ItemStack pStack) {
      return true;
   }
}
