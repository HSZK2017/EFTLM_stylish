package com.dmc.invincible_dmc.mixin.epicfight;

import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;

@Mixin({AnimationManager.class})
public interface AnimationManagerAccessor {
   @Accessor(
      value = "animationByName",
      remap = false
   )
   Map<ResourceLocation, AnimationAccessor<? extends StaticAnimation>> getAnimationByName();

   @Accessor(
      value = "animationById",
      remap = false
   )
   Map<Integer, AnimationAccessor<? extends StaticAnimation>> getAnimationById();

   @Accessor(
      value = "animations",
      remap = false
   )
   Map<AnimationAccessor<? extends StaticAnimation>, StaticAnimation> getAnimations();
}
