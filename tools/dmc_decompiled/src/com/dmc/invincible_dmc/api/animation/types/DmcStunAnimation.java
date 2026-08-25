package com.dmc.invincible_dmc.api.animation.types;

import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class DmcStunAnimation extends ActionAnimation {
   public DmcStunAnimation(float transitionTime, AnimationAccessor<? extends ActionAnimation> accessor, AssetAccessor<? extends Armature> armature) {
      super(transitionTime, accessor, armature);
      this.configureStunState(0.25F);
   }

   public DmcStunAnimation(
      float transitionTime, float stunTime, AnimationAccessor<? extends ActionAnimation> accessor, AssetAccessor<? extends Armature> armature
   ) {
      super(transitionTime, stunTime, accessor, armature);
      this.configureStunState(stunTime);
   }

   public DmcStunAnimation(float transitionTime, float stunTime, String path, AssetAccessor<? extends Armature> armature) {
      super(transitionTime, stunTime, path, armature);
      this.configureStunState(stunTime);
   }

   private void configureStunState(float skillLockTime) {
      this.stateSpectrumBlueprint
         .clear()
         .newTimePair(0.0F, skillLockTime)
         .addState(EntityState.CAN_SKILL_EXECUTION, false)
         .newTimePair(0.0F, Float.MAX_VALUE)
         .addState(EntityState.HURT_LEVEL, 1)
         .addState(EntityState.CAN_BASIC_ATTACK, false)
         .addState(EntityState.MOVEMENT_LOCKED, true)
         .addState(EntityState.TURNING_LOCKED, true)
         .addState(EntityState.UPDATE_LIVING_MOTION, false)
         .addState(EntityState.INACTION, true);
      this.addProperty(StaticAnimationProperty.FIXED_HEAD_ROTATION, true);
   }

   public void tick(LivingEntityPatch<?> entitypatch) {
      ((LivingEntity)entitypatch.getOriginal()).m_20334_(0.0, ((LivingEntity)entitypatch.getOriginal()).m_20184_().f_82480_, 0.0);
      super.tick(entitypatch);
   }

   public void linkTick(LivingEntityPatch<?> entitypatch, AssetAccessor<? extends DynamicAnimation> linkAnimation) {
      ((LivingEntity)entitypatch.getOriginal()).m_20334_(0.0, ((LivingEntity)entitypatch.getOriginal()).m_20184_().f_82480_, 0.0);
      super.linkTick(entitypatch, linkAnimation);
   }
}
