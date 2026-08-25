package com.dmc.invincible_dmc.mixin.compat.citadel;

import com.dmc.invincible_dmc.gameassets.DMCEffects;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {AnimationHandler.class},
   remap = false
)
public class AnimationHandlerMixin {
   @Inject(
      method = {"updateAnimations"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public <T extends Entity & IAnimatedEntity> void updateAnimations(T entity, CallbackInfo ci) {
      if (entity instanceof LivingEntity livingEntity) {
         if (livingEntity.m_21023_((MobEffect)DMCEffects.STOP.get())) {
            ci.cancel();
         } else if (livingEntity.m_21023_((MobEffect)DMCEffects.SLOW.get()) && entity.f_19797_ % 10 != 0) {
            ci.cancel();
         }
      }
   }
}
