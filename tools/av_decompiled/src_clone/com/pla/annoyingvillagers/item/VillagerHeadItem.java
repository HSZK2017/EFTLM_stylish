package com.pla.annoyingvillagers.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class VillagerHeadItem extends Item implements Equipable {
   public VillagerHeadItem() {
      super(new Properties().m_41487_(1).m_41497_(Rarity.COMMON));
   }

   public void m_7373_(@NotNull ItemStack itemStack, Level level, @NotNull List<Component> list, @NotNull TooltipFlag tooltipFlag) {
      super.m_7373_(itemStack, level, list, tooltipFlag);
      list.add(Component.m_237115_("tooltip.annoyingvillagers.villager_head"));
   }

   @NotNull
   public InteractionResultHolder<ItemStack> m_7203_(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand interactionHand) {
      return this.m_269277_(this, level, player, interactionHand);
   }

   public EquipmentSlot m_40402_() {
      return EquipmentSlot.HEAD;
   }
}
