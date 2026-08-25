package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.entity.EnderAegisProjectile;
import java.util.Random;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EnderAegisProjectileItem extends Item {
   public EnderAegisProjectileItem() {
      super(new Properties().m_41503_(100));
   }

   @NotNull
   public InteractionResultHolder<ItemStack> m_7203_(@NotNull Level level, Player player, @NotNull InteractionHand interactionhand) {
      player.m_6672_(interactionhand);
      return new InteractionResultHolder(InteractionResult.SUCCESS, player.m_21120_(interactionhand));
   }

   @NotNull
   public UseAnim m_6164_(@NotNull ItemStack itemstack) {
      return UseAnim.SPEAR;
   }

   public int m_8105_(@NotNull ItemStack itemstack) {
      return 72000;
   }

   public void m_5551_(@NotNull ItemStack itemstack, Level level, @NotNull LivingEntity livingentity, int i) {
      if (!level.m_5776_() && livingentity instanceof ServerPlayer serverPlayer && serverPlayer.m_6084_()) {
         EnderAegisProjectile enderAegisProjectile = EnderAegisProjectile.shoot(level, serverPlayer, new Random(), 1.0F, 18.0, 7);
         itemstack.m_41622_(1, serverPlayer, serverplayer1 -> serverplayer1.m_21190_(serverPlayer.m_7655_()));
         enderAegisProjectile.f_36705_ = Pickup.DISALLOWED;
      }
   }
}
