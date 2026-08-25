package com.pla.annoyingvillagers.capabilities;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.UseAnim;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Builder;

public class AVBowCapability extends WeaponCapability {
   public AVBowCapability(Builder builder) {
      super(builder);
   }

   public UseAnim getUseAnimation(LivingEntityPatch<?> livingEntityPatch) {
      return UseAnim.BOW;
   }

   public LivingMotion getLivingMotion(LivingEntityPatch<?> livingEntityPatch, InteractionHand hand) {
      LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
      return (LivingMotion)(livingEntity.m_6117_() && livingEntity.m_21211_().m_41780_() == UseAnim.BOW
         ? LivingMotions.AIM
         : super.getLivingMotion(livingEntityPatch, hand));
   }
}
