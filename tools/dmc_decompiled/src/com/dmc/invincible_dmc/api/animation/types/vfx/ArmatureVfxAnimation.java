package com.dmc.invincible_dmc.api.animation.types.vfx;

import com.merlin204.avalon.epicfight.animations.AutoDiscardAttackAnimation;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation.AvalonPhase;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.BasicAttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class ArmatureVfxAnimation extends AutoDiscardAttackAnimation {
   public ArmatureVfxAnimation(
      float transitionTime,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      @Nullable Collider collider,
      Joint colliderJoint,
      AnimationAccessor<? extends BasicAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float play_speed,
      float damageMulti
   ) {
      super(transitionTime, antic, preDelay, contact, recovery, collider, colliderJoint, accessor, armature, play_speed, damageMulti);
   }

   public ArmatureVfxAnimation(
      float transitionTime,
      AnimationAccessor<? extends BasicAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float play_speed,
      float damageMulti,
      AvalonPhase... phases
   ) {
      super(transitionTime, accessor, armature, play_speed, damageMulti, phases);
   }

   public ArmatureVfxAnimation(
      float transitionTime,
      AnimationAccessor<? extends BasicAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float damageMulti,
      AvalonPhase... phases
   ) {
      super(transitionTime, accessor, armature, damageMulti, phases);
   }

   public ArmatureVfxAnimation(
      float transitionTime, AnimationAccessor<? extends BasicAttackAnimation> accessor, AssetAccessor<? extends Armature> armature, AvalonPhase... phases
   ) {
      super(transitionTime, accessor, armature, phases);
   }

   public ArmatureVfxAnimation(
      float convertTime, String path, AssetAccessor<? extends Armature> armature, float play_speed, float damageMulti, AvalonPhase... phases
   ) {
      super(convertTime, path, armature, play_speed, damageMulti, phases);
   }

   protected void move(LivingEntityPatch<?> livingEntityPatch, AssetAccessor<? extends DynamicAnimation> animation) {
   }

   protected boolean validateMovement(LivingEntityPatch<?> livingEntityPatch, AssetAccessor<? extends DynamicAnimation> animation) {
      return true;
   }

   public void end(LivingEntityPatch<?> entityPatch, AssetAccessor<? extends DynamicAnimation> nextAnimation, boolean isEnd) {
      if (!entityPatch.getEntityState().inaction() && !entityPatch.isLogicalClient()) {
         ((LivingEntity)entityPatch.getOriginal()).m_146870_();
      }

      super.end(entityPatch, nextAnimation, isEnd);
   }
}
