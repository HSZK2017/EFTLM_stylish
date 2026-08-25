package com.dmc.invincible_dmc.mixin.client;

import com.dmc.invincible_dmc.client.dimension.VoidColorGradeRenderer;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GameRenderer.class})
public abstract class GameRendererJudgementCutParticleMixin {
   @Inject(
      method = {"render"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;bindWrite(Z)V",
         ordinal = 0,
         shift = Shift.AFTER
      )}
   )
   private void invincibleDmc$renderVoidColorGradeAfterShaderPack(float partialTick, long nanoTime, boolean renderLevel, CallbackInfo ci) {
      VoidColorGradeRenderer.renderAfterShaderPack(partialTick);
   }
}
