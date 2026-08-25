package com.dmc.invincible_dmc.mixin.compat.geckolib;

import com.dmc.invincible_dmc.gameassets.DMCEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoModel;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationProcessor;
import software.bernie.geckolib.core.animation.AnimationState;

@Mixin(
   value = {AnimationProcessor.class},
   remap = false
)
public class AnimationProcessorMixin<T extends GeoAnimatable> {
   @Inject(
      method = {"tickAnimation"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void tickAnimation(
      T animatable,
      CoreGeoModel<T> model,
      AnimatableManager<T> animatableManager,
      double animTime,
      AnimationState<T> state,
      boolean crashWhenCantFindBone,
      CallbackInfo ci
   ) {
      if (animatable instanceof GeoEntity geoEntity && geoEntity instanceof LivingEntity livingEntity) {
         if (livingEntity.m_21023_((MobEffect)DMCEffects.STOP.get())) {
            ci.cancel();
         } else if (livingEntity.m_21023_((MobEffect)DMCEffects.SLOW.get()) && livingEntity.f_19797_ % 10 != 0) {
            ci.cancel();
         }
      }
   }
}
