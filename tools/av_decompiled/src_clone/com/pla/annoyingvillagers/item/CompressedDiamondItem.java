package com.pla.annoyingvillagers.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CompressedDiamondItem extends Item {
   public CompressedDiamondItem() {
      super(new Properties().m_41487_(64).m_41497_(Rarity.EPIC));
   }

   public void m_7373_(@NotNull ItemStack itemStack, Level level, @NotNull List<Component> list, @NotNull TooltipFlag tooltipFlag) {
      super.m_7373_(itemStack, level, list, tooltipFlag);
      list.add(Component.m_237115_("tooltip.annoyingvillagers.compressessed_diamond"));
   }
}
