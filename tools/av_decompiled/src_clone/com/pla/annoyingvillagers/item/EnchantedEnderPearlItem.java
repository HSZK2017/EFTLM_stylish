package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.entity.EnchantedEnderPearlEntity;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class EnchantedEnderPearlItem extends Item {
   public EnchantedEnderPearlItem() {
      super(new Properties().m_41487_(1).m_41503_(100));
   }

   public boolean m_8120_(@NotNull ItemStack stack) {
      return false;
   }

   public boolean canApplyAtEnchantingTable(@NotNull ItemStack stack, @NotNull Enchantment enchantment) {
      return false;
   }

   public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
      return false;
   }

   public int m_6473_() {
      return 0;
   }

   @NotNull
   public InteractionResultHolder<ItemStack> m_7203_(@NotNull Level level, Player player, @NotNull InteractionHand interactionhand) {
      player.m_6672_(interactionhand);
      return new InteractionResultHolder(InteractionResult.SUCCESS, player.m_21120_(interactionhand));
   }

   public void m_7373_(@NotNull ItemStack itemstack, Level level, @NotNull List<Component> list, @NotNull TooltipFlag tooltipflag) {
      super.m_7373_(itemstack, level, list, tooltipflag);
      list.add(Component.m_237115_("tooltip.annoyingvillagers.enchanted_ender_pearl"));
   }

   @NotNull
   public UseAnim m_6164_(@NotNull ItemStack itemstack) {
      return UseAnim.BOW;
   }

   public int m_8105_(@NotNull ItemStack itemstack) {
      return 72000;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean m_5812_(@NotNull ItemStack itemstack) {
      return true;
   }

   public void m_5551_(@NotNull ItemStack itemstack, Level level, @NotNull LivingEntity livingentity, int i) {
      if (!level.m_5776_() && livingentity instanceof ServerPlayer serverPlayer) {
         EnchantedEnderPearlEntity enchantedEnderPearl = EnchantedEnderPearlEntity.shoot(level, serverPlayer, RandomSource.m_216327_(), 1.3F, 0.0, 0);
         itemstack.m_41622_(1, serverPlayer, serverplayer1 -> serverplayer1.m_21190_(serverPlayer.m_7655_()));
         enchantedEnderPearl.f_36705_ = Pickup.DISALLOWED;
         serverPlayer.m_36335_().m_41524_(itemstack.m_41720_(), 20);
      }
   }
}
