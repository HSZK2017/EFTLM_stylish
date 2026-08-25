package com.pla.annoyingvillagers.capabilities;

import com.pla.annoyingvillagers.clazz.ThrowableSpearItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.UseAnim;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Builder;

public class AVThrowableSpearCapability extends WeaponCapability {
   public AVThrowableSpearCapability(Builder builder) {
      super(builder);
   }

   public UseAnim getUseAnimation(LivingEntityPatch<?> livingEntityPatch) {
      return UseAnim.SPEAR;
   }

   public LivingMotion getLivingMotion(LivingEntityPatch<?> livingEntityPatch, InteractionHand hand) {
      LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
      return (LivingMotion)(livingEntity.m_6117_() && livingEntity.m_7655_() == hand && livingEntity.m_21120_(hand).m_41720_() instanceof ThrowableSpearItem
         ? LivingMotions.AIM
         : super.getLivingMotion(livingEntityPatch, hand));
   }
}
