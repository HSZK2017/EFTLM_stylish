package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.clazz.AVNpc;
import com.pla.annoyingvillagers.entity.ItemProjectile;
import com.pla.annoyingvillagers.entity.PlayerNpcEntity;
import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import com.pla.annoyingvillagers.util.CommonUtil;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class DiamondAttractorSwordItem extends SwordItem {
   public DiamondAttractorSwordItem() {
      super(new Tier() {
         public int m_6609_() {
            return 1561;
         }

         public float m_6624_() {
            return 6.0F;
         }

         public float m_6631_() {
            return 2.4F;
         }

         public int m_6604_() {
            return 5;
         }

         public int m_6601_() {
            return 21;
         }

         @NotNull
         public Ingredient m_6282_() {
            return Ingredient.m_43927_(new ItemStack[]{new ItemStack(Items.f_42415_)});
         }
      }, 3, -2.8F, new Properties());
   }

   public static void pullWeapons(LivingEntity owner) {
      if (owner.m_9236_() instanceof ServerLevel level) {
         double radius = 7.0;
         AABB area = owner.m_20191_().m_82400_(radius);
         pullDroppedWeapons(level, owner, area);
         pullHeldWeapons(level, owner, area);
      }
   }

   private static void pullDroppedWeapons(ServerLevel level, LivingEntity owner, AABB area) {
      for (ItemEntity itemEntity : level.m_6443_(
         ItemEntity.class, area, itemEntityx -> itemEntityx.m_6084_() && CommonUtil.isPullableWeapon(itemEntityx.m_32055_())
      )) {
         ItemStack stackInWorld = itemEntity.m_32055_();
         if (!stackInWorld.m_41619_()) {
            if (CommonUtil.isBlacklistedWeapon(stackInWorld)) {
               CommonUtil.fallBackOnBlackListWeapon(owner, itemEntity, stackInWorld);
            } else {
               ItemStack pulledStack = stackInWorld.m_41777_();
               pulledStack.m_41764_(1);
               ItemStack remainder = stackInWorld.m_41777_();
               remainder.m_41774_(1);
               if (remainder.m_41619_()) {
                  itemEntity.m_146870_();
               } else {
                  itemEntity.m_32045_(remainder);
               }

               spawnPulledWeapon(level, owner, pulledStack, itemEntity.m_20182_());
            }
         }
      }
   }

   private static void pullHeldWeapons(ServerLevel level, LivingEntity owner, AABB area) {
      for (LivingEntity target : level.m_6443_(LivingEntity.class, area, entity -> entity.m_6084_() && entity != owner)) {
         CommonUtil.pullEntityTowardCaster(target, owner);
         LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
         if (targetPatch != null) {
            AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(targetPatch.getAnimator().getPlayerFor(null)).getRealAnimation();
            if (!EpicfightUtil.isLongHitAnimation(dynamicAnimation, targetPatch)) {
               targetPatch.playAnimationSynchronized(AnimsPugilistSteve.KNOCKDOWN_FORWARD, 0.0F);
            }
         }

         if (CommonUtil.entityCanBeDisarmed(target)) {
            List<InteractionHand> candidateHands = new ArrayList<>(2);
            ItemStack mainHand = target.m_21205_();
            ItemStack offHand = target.m_21206_();
            if (CommonUtil.isPullableWeapon(mainHand)) {
               candidateHands.add(InteractionHand.MAIN_HAND);
            }

            if (CommonUtil.isPullableWeapon(offHand)) {
               candidateHands.add(InteractionHand.OFF_HAND);
            }

            if (!candidateHands.isEmpty()) {
               InteractionHand chosenHand = candidateHands.get(level.f_46441_.m_188503_(candidateHands.size()));
               ItemStack chosenStack = target.m_21120_(chosenHand);
               if (CommonUtil.isBlacklistedWeapon(chosenStack)) {
                  CommonUtil.fallBackOnBlackListWeapon(owner, target, chosenStack);
               } else {
                  ItemStack pulledStack = chosenStack.m_41777_();
                  target.m_21008_(chosenHand, ItemStack.f_41583_);
                  if (target instanceof AVNpc avNpc) {
                     if (chosenHand == InteractionHand.MAIN_HAND) {
                        avNpc.setMainWeaponItem(ItemStack.f_41583_);
                     } else {
                        avNpc.setOffWeaponItem(ItemStack.f_41583_);
                     }
                  }

                  if (target instanceof PlayerNpcEntity playerNpcEntity) {
                     if (chosenHand == InteractionHand.MAIN_HAND) {
                        playerNpcEntity.setMainWeaponItem(ItemStack.f_41583_);
                     } else {
                        playerNpcEntity.setOffWeaponItem(ItemStack.f_41583_);
                     }
                  }

                  Vec3 spawnPos = getHeldWeaponSpawnPos(target);
                  spawnPulledWeapon(level, owner, pulledStack, spawnPos);
               }
            }
         }
      }
   }

   private static Vec3 getHeldWeaponSpawnPos(LivingEntity target) {
      return target.m_146892_().m_82492_(0.0, 0.25, 0.0);
   }

   private static void spawnPulledWeapon(ServerLevel level, LivingEntity owner, ItemStack stack, Vec3 spawnPos) {
      if (!stack.m_41619_()) {
         ItemProjectile projectile = new ItemProjectile(level, owner, stack, spawnPos.m_82520_(0.0, 0.15, 0.0));
         level.m_7967_(projectile);
      }
   }
}
