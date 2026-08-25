package com.pla.annoyingvillagers.clazz;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class ThrowableSpearItem extends SwordItem {
   private static final int THROW_THRESHOLD_TIME = 10;
   private static final float SHOOT_POWER = 2.5F;

   protected ThrowableSpearItem(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
      super(tier, attackDamageModifier, attackSpeedModifier, properties);
   }

   protected AbstractArrow createThrownProjectile(Level level, Player player, ItemStack stack) {
      return new ThrownTrident(level, player, stack);
   }

   @NotNull
   public UseAnim m_6164_(@NotNull ItemStack stack) {
      return UseAnim.SPEAR;
   }

   public int m_8105_(@NotNull ItemStack stack) {
      return 72000;
   }

   @NotNull
   public InteractionResultHolder<ItemStack> m_7203_(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
      ItemStack stack = player.m_21120_(hand);
      if (stack.m_41773_() >= stack.m_41776_() - 1) {
         return InteractionResultHolder.m_19100_(stack);
      } else if (EnchantmentHelper.m_44932_(stack) > 0 && !player.m_20070_()) {
         return InteractionResultHolder.m_19100_(stack);
      } else {
         player.m_6672_(hand);
         return InteractionResultHolder.m_19096_(stack);
      }
   }

   public void m_5551_(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity, int timeLeft) {
      if (livingEntity instanceof Player player) {
         int useTicks = this.m_8105_(stack) - timeLeft;
         if (useTicks >= 10) {
            int riptide = EnchantmentHelper.m_44932_(stack);
            if (riptide <= 0 || player.m_20070_()) {
               if (riptide == 0) {
                  playEpicFightShotAnimation(player);
               }

               if (!level.m_5776_()) {
                  stack.m_41622_(1, player, owner -> owner.m_21190_(livingEntity.m_7655_()));
                  if (riptide == 0) {
                     AbstractArrow thrownProjectile = this.createThrownProjectile(level, player, stack);
                     thrownProjectile.m_37251_(player, player.m_146909_(), player.m_146908_(), 0.0F, 2.5F, 1.0F);
                     if (player.m_150110_().f_35937_) {
                        thrownProjectile.f_36705_ = Pickup.CREATIVE_ONLY;
                     }

                     level.m_7967_(thrownProjectile);
                     level.m_6269_(null, thrownProjectile, SoundEvents.f_12520_, SoundSource.PLAYERS, 1.0F, 1.0F);
                     if (!player.m_150110_().f_35937_) {
                        player.m_150109_().m_36057_(stack);
                     }
                  }
               }

               player.m_36246_(Stats.f_12982_.m_12902_(this));
               if (riptide > 0) {
                  launchRiptide(player, level, riptide);
               }
            }
         }
      }
   }

   public boolean canApplyAtEnchantingTable(@NotNull ItemStack stack, @NotNull Enchantment enchantment) {
      return enchantment.f_44672_.m_7454_(Items.f_42713_) || super.canApplyAtEnchantingTable(stack, enchantment);
   }

   private static void playEpicFightShotAnimation(Player player) {
      LivingEntityPatch<?> playerPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(player, LivingEntityPatch.class);
      if (playerPatch != null) {
         playerPatch.playShootingAnimation();
      }
   }

   private static void launchRiptide(Player player, Level level, int riptide) {
      float yRot = player.m_146908_();
      float xRot = player.m_146909_();
      float motionX = -Mth.m_14031_(yRot * (float) (Math.PI / 180.0)) * Mth.m_14089_(xRot * (float) (Math.PI / 180.0));
      float motionY = -Mth.m_14031_(xRot * (float) (Math.PI / 180.0));
      float motionZ = Mth.m_14089_(yRot * (float) (Math.PI / 180.0)) * Mth.m_14089_(xRot * (float) (Math.PI / 180.0));
      float motionLength = Mth.m_14116_(motionX * motionX + motionY * motionY + motionZ * motionZ);
      float riptideStrength = 3.0F * (1.0F + (float)riptide) / 4.0F;
      motionX *= riptideStrength / motionLength;
      motionY *= riptideStrength / motionLength;
      motionZ *= riptideStrength / motionLength;
      player.m_5997_((double)motionX, (double)motionY, (double)motionZ);
      player.m_204079_(20);
      if (player.m_20096_()) {
         player.m_6478_(MoverType.SELF, new Vec3(0.0, 1.1999999, 0.0));
      }

      SoundEvent soundEvent;
      if (riptide >= 3) {
         soundEvent = SoundEvents.f_12519_;
      } else if (riptide == 2) {
         soundEvent = SoundEvents.f_12518_;
      } else {
         soundEvent = SoundEvents.f_12517_;
      }

      level.m_6269_(null, player, soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F);
   }
}
