package com.pla.annoyingvillagers.event;

import com.pla.annoyingvillagers.entity.EnchantedEnderPearlEntity;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFightIronSpell;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import java.util.Objects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class ThrowingPearlKeyPressedEvent {
   private static Vec3 getFrontLeftPos(Entity entity) {
      Vec3 base = entity instanceof LivingEntity le ? le.m_20299_(1.0F) : entity.m_20182_().m_82520_(0.0, (double)entity.m_20206_() * 0.85, 0.0);
      base = base.m_82520_(0.0, -0.1, 0.0);
      Vec3 forward = entity.m_20154_();
      Vec3 forwardH = new Vec3(forward.f_82479_, 0.0, forward.f_82481_);
      if (forwardH.m_82556_() < 1.0E-6) {
         forwardH = entity.m_20156_();
         forwardH = new Vec3(forwardH.f_82479_, 0.0, forwardH.f_82481_);
      }

      forwardH = forwardH.m_82541_();
      Vec3 left = new Vec3(0.0, 1.0, 0.0).m_82537_(forwardH);
      if (left.m_82556_() < 1.0E-6) {
         left = new Vec3(1.0, 0.0, 0.0);
      } else {
         left = left.m_82541_();
      }

      return base.m_82549_(forwardH.m_82490_(0.35)).m_82549_(left.m_82490_(0.25));
   }

   public static void execute(Entity entity) {
      if (entity != null) {
         if (!(entity.m_9236_() instanceof ServerLevel)) {
            return;
         }

         LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
         if (livingEntityPatch == null) {
            return;
         }

         AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(livingEntityPatch.getAnimator().getPlayerFor(null))
            .getRealAnimation();
         if (EpicfightUtil.isLongHitAnimation(dynamicAnimation, livingEntityPatch)) {
            return;
         }

         if (dynamicAnimation != Animations.EMPTY_ANIMATION) {
            return;
         }

         if (entity instanceof Player player) {
            boolean used = player.m_150109_()
               .f_35974_
               .stream()
               .filter(s -> !s.m_41619_() && s.m_150930_((Item)AnnoyingVillagersModItems.ENCHANTED_ENDER_PEARL.get()))
               .findFirst()
               .map(
                  stack -> {
                     livingEntityPatch.playAnimationSynchronized(AnimsEpicFightIronSpell.CASTING_ONE_HAND_TOP, 0.0F);
                     Level levelx = entity.m_9236_();
                     EnchantedEnderPearlEntity projectilex = new EnchantedEnderPearlEntity(
                        (EntityType<? extends EnchantedEnderPearlEntity>)AnnoyingVillagersModEntities.ENCHANTED_ENDER_PEARL_PROJECTILE.get(), levelx
                     );
                     projectilex.m_5602_(entity);
                     Vec3 handPosx = getFrontLeftPos(entity);
                     projectilex.m_6034_(handPosx.f_82479_, handPosx.f_82480_, handPosx.f_82481_);
                     projectilex.m_6686_(entity.m_20154_().f_82479_, entity.m_20154_().f_82480_, entity.m_20154_().f_82481_, 1.5F, 0.0F);
                     levelx.m_7967_(projectilex);
                     entity.m_9236_()
                        .m_6263_(
                           null,
                           entity.m_20185_(),
                           entity.m_20186_(),
                           entity.m_20189_(),
                           SoundEvents.f_11857_,
                           SoundSource.NEUTRAL,
                           0.5F,
                           0.4F / (entity.m_9236_().m_213780_().m_188501_() * 0.4F + 0.8F)
                        );
                     stack.m_41622_(1, player, p -> {
                     });
                     return true;
                  }
               )
               .orElse(false);
            if (used) {
               return;
            }
         }

         if (entity instanceof Player playerx && playerx.m_150109_().m_36063_(new ItemStack(Items.f_42584_))) {
            livingEntityPatch.playAnimationSynchronized(AnimsEpicFightIronSpell.CASTING_ONE_HAND_TOP, 0.0F);
            Level level = entity.m_9236_();
            Projectile projectile = new ThrownEnderpearl(EntityType.f_20484_, level);
            projectile.m_5602_(entity);
            Vec3 handPos = getFrontLeftPos(entity);
            projectile.m_6034_(handPos.f_82479_, handPos.f_82480_, handPos.f_82481_);
            projectile.m_6686_(entity.m_20154_().f_82479_, entity.m_20154_().f_82480_, entity.m_20154_().f_82481_, 1.5F, 0.0F);
            level.m_7967_(projectile);
            entity.m_9236_()
               .m_6263_(
                  null,
                  entity.m_20185_(),
                  entity.m_20186_(),
                  entity.m_20189_(),
                  SoundEvents.f_11857_,
                  SoundSource.NEUTRAL,
                  0.5F,
                  0.4F / (entity.m_9236_().m_213780_().m_188501_() * 0.4F + 0.8F)
               );
            Player player2 = (Player)entity;
            ItemStack itemStack = new ItemStack(Items.f_42584_);
            player2.m_150109_().m_36022_(stack -> itemStack.m_41720_() == stack.m_41720_(), 1, player2.f_36095_.m_39730_());
         }
      }
   }
}
