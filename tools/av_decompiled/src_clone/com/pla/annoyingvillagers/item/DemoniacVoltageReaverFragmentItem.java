package com.pla.annoyingvillagers.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class DemoniacVoltageReaverFragmentItem extends Item {
   public DemoniacVoltageReaverFragmentItem() {
      super(new Properties().m_41487_(64).m_41497_(Rarity.COMMON));
   }

   public void m_7373_(ItemStack itemstack, Level level, List<Component> list, TooltipFlag tooltipflag) {
      super.m_7373_(itemstack, level, list, tooltipflag);
      list.add(Component.m_237115_("tooltip.annoyingvillagers.demoniac_voltage_reaver_fragment"));
   }
}
