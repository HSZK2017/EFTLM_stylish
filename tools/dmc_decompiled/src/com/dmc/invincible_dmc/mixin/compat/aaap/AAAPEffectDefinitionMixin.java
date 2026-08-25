package com.dmc.invincible_dmc.mixin.compat.aaap;

import com.dmc.invincible_dmc.client.compat.aaap.AAAPEffectDefinitionAccess;
import com.dmc.invincible_dmc.client.compat.aaap.AAAParticlePerformanceBudget;
import com.dmc.invincible_dmc.client.compat.aaap.AAAParticleSimulationController;
import com.mojang.blaze3d.pipeline.RenderTarget;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import mod.chloeprime.aaaparticles.api.client.EffectDefinition;
import mod.chloeprime.aaaparticles.api.client.effekseer.EffekseerManager;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter.Type;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {EffectDefinition.class},
   remap = false
)
public abstract class AAAPEffectDefinitionMixin implements AAAPEffectDefinitionAccess {
   @Shadow
   @Final
   private EnumMap<Type, Set<ParticleEmitter>> oneShotEmitters;
   @Shadow
   @Final
   private EnumMap<Type, Map<ResourceLocation, ParticleEmitter>> namedEmitters;

   @Inject(
      method = {"draw"},
      at = {@At("HEAD")}
   )
   private static void invincibleDmc$prepareSimulationStep(
      Type type,
      Vector3f front,
      Vector3f position,
      int width,
      int height,
      float[] camera,
      float[] projection,
      float deltaFrames,
      float partialTicks,
      RenderTarget background,
      CallbackInfo ci
   ) {
      AAAParticleSimulationController.prepare(type, deltaFrames);
   }

   @Redirect(
      method = {"draw"},
      at = @At(
         value = "INVOKE",
         target = "Lmod/chloeprime/aaaparticles/api/client/effekseer/EffekseerManager;update(F)V"
      )
   )
   private static void invincibleDmc$limitManagerSimulationRate(EffekseerManager manager, float deltaFrames) {
      Type type = AAAParticleSimulationController.currentType();
      if (type == null) {
         manager.update(deltaFrames);
      } else {
         if (AAAParticleSimulationController.shouldUpdate(type)) {
            manager.update(AAAParticleSimulationController.updateDelta(type));
         }
      }
   }

   @Inject(
      method = {"draw"},
      at = {@At("RETURN")}
   )
   private static void invincibleDmc$clearSimulationStep(
      Type type,
      Vector3f front,
      Vector3f position,
      int width,
      int height,
      float[] camera,
      float[] projection,
      float deltaFrames,
      float partialTicks,
      RenderTarget background,
      CallbackInfo ci
   ) {
      AAAParticleSimulationController.clear();
   }

   @Override
   public boolean invincibleDmc$hasEmitters(Type type) {
      Set<ParticleEmitter> oneShot = this.oneShotEmitters.get(type);
      Map<ResourceLocation, ParticleEmitter> named = this.namedEmitters.get(type);
      return oneShot != null && !oneShot.isEmpty() || named != null && !named.isEmpty();
   }

   @Override
   public int invincibleDmc$getEmitterCount(Type type) {
      Set<ParticleEmitter> oneShot = this.oneShotEmitters.get(type);
      Map<ResourceLocation, ParticleEmitter> named = this.namedEmitters.get(type);
      return (oneShot == null ? 0 : oneShot.size()) + (named == null ? 0 : named.size());
   }

   @Inject(
      method = {"play(Lmod/chloeprime/aaaparticles/api/client/effekseer/ParticleEmitter$Type;)Lmod/chloeprime/aaaparticles/api/client/effekseer/ParticleEmitter;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void invincibleDmc$limitConcurrentOneShotEffects(Type type, CallbackInfoReturnable<ParticleEmitter> cir) {
      if (AAAParticlePerformanceBudget.shouldRejectOneShot((EffectDefinition)this, type)) {
         cir.setReturnValue(ParticleEmitter.dummy(type));
      }
   }
}
