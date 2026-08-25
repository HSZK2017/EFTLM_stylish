package com.dmc.invincible_dmc.api.animation.types.yamato;

import com.merlin204.avalon.epicfight.animations.AvalonMovementAnimation;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;

public class YamatoMovementAnimation extends AvalonMovementAnimation {
   public YamatoMovementAnimation(
      boolean isRepeat, AnimationAccessor<? extends AvalonMovementAnimation> accessor, AssetAccessor<? extends Armature> armature, float speed
   ) {
      super(isRepeat, accessor, armature, speed);
   }

   public YamatoMovementAnimation(
      float transitionTime,
      boolean isRepeat,
      AnimationAccessor<? extends AvalonMovementAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float speed
   ) {
      super(transitionTime, isRepeat, accessor, armature, speed);
   }

   public YamatoMovementAnimation(float transitionTime, boolean isRepeat, String path, AssetAccessor<? extends Armature> armature, float speed) {
      super(transitionTime, isRepeat, path, armature, speed);
   }
}
