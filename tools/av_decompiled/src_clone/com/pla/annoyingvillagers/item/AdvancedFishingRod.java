package com.pla.annoyingvillagers.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

public class AdvancedFishingRod extends FishingRodItem {
   public AdvancedFishingRod() {
      super(new Properties().m_41487_(1).m_41503_(250));
   }

   public boolean m_8120_(ItemStack stack) {
      return false;
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return false;
   }

   public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
      return false;
   }

   public int m_6473_() {
      return 0;
   }

   public void m_7373_(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
      super.m_7373_(stack, level, tooltip, flag);
      tooltip.add(Component.m_237115_("tooltip.annoyingvillagers.advanced_fishing_rod"));
   }

   public void m_6883_(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
      super.m_6883_(stack, level, entity, slot, selected);
      FishingRodGrappleUtil.inventoryTick(stack, level, entity);
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, InteractionHand hand) {
      return FishingRodGrappleUtil.use(this, level, player, hand);
   }
}
