package com.dmc.invincible_dmc.api.animation.types.yamato;

import com.dmc.invincible_dmc.entity.summonedsword.DMCSummonedSwordEntity;
import com.merlin204.avalon.epicfight.animations.AutoDiscardActionAnimation;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class SummonedSwordAnimation extends AutoDiscardActionAnimation {
   public SummonedSwordAnimation(
      float transitionTime, AnimationAccessor<? extends ActionAnimation> accessor, AssetAccessor<? extends Armature> armature, float playSpeed
   ) {
      super(transitionTime, accessor, armature, playSpeed);
   }

   public SummonedSwordAnimation(
      float transitionTime, float postDelay, AnimationAccessor<? extends ActionAnimation> accessor, AssetAccessor<? extends Armature> armature, float playSpeed
   ) {
      super(transitionTime, postDelay, accessor, armature, playSpeed);
   }

   public SummonedSwordAnimation(float transitionTime, float postDelay, String path, AssetAccessor<? extends Armature> armature, float playSpeed) {
      super(transitionTime, postDelay, path, armature, playSpeed);
   }

   protected void move(LivingEntityPatch<?> livingEntityPatch, AssetAccessor<? extends DynamicAnimation> animation) {
   }

   protected boolean validateMovement(LivingEntityPatch<?> livingEntityPatch, AssetAccessor<? extends DynamicAnimation> animation) {
      return true;
   }

   public boolean isRepeat() {
      return true;
   }

   public void end(LivingEntityPatch<?> entitypatch, AssetAccessor<? extends DynamicAnimation> nextAnimation, boolean isEnd) {
      if (entitypatch.getOriginal() instanceof DMCSummonedSwordEntity sword && sword.isInStandby() && sword.isSpine()) {
         return;
      }

      super.end(entitypatch, nextAnimation, isEnd);
   }
}
