package org.merlin204.mimic.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MimicMerlinSuperGG extends Item {
   public MimicMerlinSuperGG(Properties pProperties) {
      super(pProperties);
   }

   @NotNull
   public InteractionResultHolder<ItemStack> m_7203_(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand pUsedHand) {
      return super.m_7203_(level, player, pUsedHand);
   }
}
