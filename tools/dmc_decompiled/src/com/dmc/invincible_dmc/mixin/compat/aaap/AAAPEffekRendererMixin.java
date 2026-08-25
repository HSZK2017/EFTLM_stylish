package com.dmc.invincible_dmc.mixin.compat.aaap;

import com.dmc.invincible_dmc.client.compat.aaap.AAAParticlePerformanceBudget;
import com.dmc.invincible_dmc.client.compat.aaap.AAAParticleRenderRequirements;
import com.dmc.invincible_dmc.client.particles.JudgementCutDMC4ParticleLayer;
import com.dmc.invincible_dmc.client.render.cinematic.CinematicBarsWorldRenderer;
import com.dmc.invincible_dmc.client.render.cinematic.CinematicYamatoBreakoutRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter.Type;
import mod.chloeprime.aaaparticles.client.render.EffekRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.ItemInHandRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {EffekRenderer.class},
   remap = false
)
public abstract class AAAPEffekRendererMixin {
   @Inject(
      method = {"renderWorldEffeks(FLcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;Lnet/minecraft/client/Camera;)V"},
      at = {@At("HEAD")}
   )
   private static void invincibleDmc$renderCinematicBarsBeforeWorldEffeks(
      float partialTick, PoseStack pose, Matrix4f projection, Camera camera, CallbackInfo ci
   ) {
      CinematicBarsWorldRenderer.renderBeforeLateWorldEffects();
      CinematicYamatoBreakoutRenderer.renderAfterCinematicBars();
   }

   @Inject(
      method = {"renderWorldEffeks(FZLnet/minecraft/client/renderer/ItemInHandRenderer;)V"},
      at = {@At("HEAD")}
   )
   private static void invincibleDmc$beginWorldFrame(float partialTick, boolean renderHand, ItemInHandRenderer itemInHandRenderer, CallbackInfo ci) {
      AAAParticlePerformanceBudget.beginWorldFrame();
   }

   @Inject(
      method = {"draw"},
      at = {@At("HEAD")}
   )
   private static void invincibleDmc$beginRenderContext(Type type, float partialTick, PoseStack pose, Matrix4f projection, Camera camera, CallbackInfo ci) {
      AAAParticleRenderRequirements.beginContext(type);
   }

   @Inject(
      method = {"draw"},
      at = {@At(
         value = "INVOKE",
         target = "Lmod/chloeprime/aaaparticles/client/render/RenderUtil;runPixelStoreCodeSafely(Ljava/lang/Runnable;)V",
         shift = Shift.AFTER
      )}
   )
   private static void invincibleDmc$renderJudgementCutDmc4ParticlesAfterWorldEffeks(
      Type type, float partialTick, PoseStack pose, Matrix4f projection, Camera camera, CallbackInfo ci
   ) {
      if (type == Type.WORLD) {
         JudgementCutDMC4ParticleLayer.renderAfterEffeks(partialTick, pose, projection, camera);
      }
   }

   @Inject(
      method = {"draw"},
      at = {@At("RETURN")}
   )
   private static void invincibleDmc$clearRenderRequirements(Type type, float partialTick, PoseStack pose, Matrix4f projection, Camera camera, CallbackInfo ci) {
      AAAParticleRenderRequirements.endContext();
   }
}
