package com.dmc.invincible_dmc.mixin.compat.aaap;

import com.dmc.invincible_dmc.client.compat.aaap.AAAParticleSimulationController;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {ParticleEmitter.class},
   remap = false
)
public abstract class AAAPParticleEmitterMixin {
   @Inject(
      method = {"internalUpdateProgress"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void invincibleDmc$skipDeferredSimulationProgress(float deltaFrames, CallbackInfo ci) {
      ParticleEmitter emitter = (ParticleEmitter)this;
      if (AAAParticleSimulationController.hasActiveContext() && !AAAParticleSimulationController.shouldUpdate(emitter.type)) {
         ci.cancel();
      }
   }

   @ModifyVariable(
      method = {"internalUpdateProgress"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private float invincibleDmc$useAccumulatedSimulationDelta(float deltaFrames) {
      ParticleEmitter emitter = (ParticleEmitter)this;
      return !AAAParticleSimulationController.hasActiveContext() ? deltaFrames : AAAParticleSimulationController.updateDelta(emitter.type);
   }
}
