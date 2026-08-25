package com.dmc.invincible_dmc.api.animation.types.yamato;

import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;

public class YamatoLivingAnimation extends StaticAnimation {
   public YamatoLivingAnimation() {
   }

   public YamatoLivingAnimation(boolean isRepeat, AnimationAccessor<? extends StaticAnimation> accessor, AssetAccessor<? extends Armature> armature) {
      super(isRepeat, accessor, armature);
   }

   public YamatoLivingAnimation(
      float transitionTime, boolean isRepeat, AnimationAccessor<? extends StaticAnimation> accessor, AssetAccessor<? extends Armature> armature
   ) {
      super(transitionTime, isRepeat, accessor, armature);
   }

   public YamatoLivingAnimation(float transitionTime, boolean isRepeat, String path, AssetAccessor<? extends Armature> armature) {
      super(transitionTime, isRepeat, path, armature);
   }

   public YamatoLivingAnimation(
      ResourceLocation fileLocation, float transitionTime, boolean isRepeat, String registryName, AssetAccessor<? extends Armature> armature
   ) {
      super(fileLocation, transitionTime, isRepeat, registryName, armature);
   }
}
