package com.dmc.invincible_dmc.mixin.client;

import com.dmc.invincible_dmc.client.render.cinematic.CinematicBarsWorldRenderer;
import com.dmc.invincible_dmc.client.render.cinematic.CinematicYamatoBreakoutRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraftforge.fml.ModList;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LevelRenderer.class})
public abstract class LevelRendererCinematicBarsMixin {
   @Inject(
      method = {"renderLevel"},
      at = {@At("RETURN")}
   )
   private void invincibleDmc$renderCinematicBarsWithoutAaa(
      PoseStack poseStack,
      float partialTick,
      long finishTimeNano,
      boolean renderBlockOutline,
      Camera camera,
      GameRenderer gameRenderer,
      LightTexture lightTexture,
      Matrix4f projectionMatrix,
      CallbackInfo ci
   ) {
      if (!ModList.get().isLoaded("aaa_particles")) {
         CinematicBarsWorldRenderer.renderBeforeLateWorldEffects();
         CinematicYamatoBreakoutRenderer.renderAfterCinematicBars();
      }
   }
}
