package com.dmc.invincible_dmc.mixin.compat.aaap;

import com.dmc.invincible_dmc.client.compat.aaap.AAAParticlePerformanceBudget;
import mod.chloeprime.aaaparticles.client.internal.CollisionCallbackSupport;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {CollisionCallbackSupport.class},
   remap = false
)
public abstract class AAAPCollisionCallbackMixin {
   @Inject(
      method = {"trace"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void invincibleDmc$limitCollisionRaycasts(
      Vector3d start, Vector3d end, Vector3d outCollisionPosition, Vector3d outCollisionNormal, CallbackInfoReturnable<Boolean> cir
   ) {
      if (!AAAParticlePerformanceBudget.allowCollisionRaycast()) {
         outCollisionPosition.set(end);
         outCollisionNormal.set(0.0, 1.0, 0.0);
         cir.setReturnValue(false);
      }
   }
}
