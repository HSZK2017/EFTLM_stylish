package com.pla.annoyingvillagers.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class JevBookItem extends Item {
   public JevBookItem() {
      super(new Properties().m_41487_(1).m_41486_().m_41497_(Rarity.EPIC));
   }

   public boolean m_8096_(BlockState blockstate) {
      return true;
   }

   public void m_7373_(ItemStack itemstack, Level level, List<Component> list, TooltipFlag tooltipflag) {
      super.m_7373_(itemstack, level, list, tooltipflag);
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, InteractionHand interactionhand) {
      return super.m_7203_(level, player, interactionhand);
   }

   public void m_6883_(ItemStack itemstack, Level level, Entity entity, int i, boolean flag) {
      super.m_6883_(itemstack, level, entity, i, flag);
   }
}
