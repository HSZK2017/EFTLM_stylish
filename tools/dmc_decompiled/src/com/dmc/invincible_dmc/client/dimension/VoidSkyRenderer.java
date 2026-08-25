package com.dmc.invincible_dmc.client.dimension;

import com.dmc.invincible_dmc.InvincibleMod_DMC;
import com.dmc.invincible_dmc.event.VoidEvents;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.client.event.ViewportEvent.RenderFog;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.joml.Matrix4f;

@EventBusSubscriber(
   modid = "invincible_dmc",
   value = {Dist.CLIENT},
   bus = Bus.FORGE
)
public final class VoidSkyRenderer {
   private static final ResourceLocation[] PANORAMA = new ResourceLocation[]{
      InvincibleMod_DMC.rl("textures/sky/panorama_0.png"),
      InvincibleMod_DMC.rl("textures/sky/panorama_1.png"),
      InvincibleMod_DMC.rl("textures/sky/panorama_2.png"),
      InvincibleMod_DMC.rl("textures/sky/panorama_3.png"),
      InvincibleMod_DMC.rl("textures/sky/panorama_4.png"),
      InvincibleMod_DMC.rl("textures/sky/panorama_5.png")
   };

   private VoidSkyRenderer() {
   }

   @SubscribeEvent
   public static void onRenderLevelStage(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_SKY) {
         Level level = Minecraft.m_91087_().f_91073_;
         if (isVoidDimension(level)) {
            renderStaticSkybox(event);
         }
      }
   }

   @SubscribeEvent
   public static void onRenderFog(RenderFog event) {
      Level level = Minecraft.m_91087_().f_91073_;
      if (isVoidDimension(level) && event.getType() == FogType.NONE) {
         event.setCanceled(true);
      }
   }

   private static void renderStaticSkybox(RenderLevelStageEvent event) {
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.disableCull();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(GameRenderer::m_172817_);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      Tesselator tesselator = Tesselator.m_85913_();
      BufferBuilder builder = tesselator.m_85915_();
      float distance = 100.0F;
      PoseStack modelViewStack = RenderSystem.getModelViewStack();
      modelViewStack.m_85836_();
      modelViewStack.m_85850_().m_252922_().identity();
      RenderSystem.applyModelViewMatrix();
      Matrix4f cameraMatrix = new Matrix4f(event.getPoseStack().m_85850_().m_252922_());
      cameraMatrix.setTranslation(0.0F, 0.0F, 0.0F);
      drawFace(
         tesselator,
         builder,
         cameraMatrix,
         PANORAMA[3],
         -distance,
         -distance,
         -distance,
         0.0F,
         0.0F,
         -distance,
         distance,
         -distance,
         0.0F,
         1.0F,
         distance,
         distance,
         -distance,
         1.0F,
         1.0F,
         distance,
         -distance,
         -distance,
         1.0F,
         0.0F
      );
      drawFace(
         tesselator,
         builder,
         cameraMatrix,
         PANORAMA[4],
         -distance,
         distance,
         -distance,
         1.0F,
         1.0F,
         -distance,
         distance,
         distance,
         1.0F,
         0.0F,
         distance,
         distance,
         distance,
         0.0F,
         0.0F,
         distance,
         distance,
         -distance,
         0.0F,
         1.0F
      );
      drawFace(
         tesselator,
         builder,
         cameraMatrix,
         PANORAMA[5],
         -distance,
         -distance,
         distance,
         1.0F,
         1.0F,
         -distance,
         -distance,
         -distance,
         1.0F,
         0.0F,
         distance,
         -distance,
         -distance,
         0.0F,
         0.0F,
         distance,
         -distance,
         distance,
         0.0F,
         1.0F
      );
      drawFace(
         tesselator,
         builder,
         cameraMatrix,
         PANORAMA[2],
         distance,
         -distance,
         -distance,
         0.0F,
         0.0F,
         distance,
         distance,
         -distance,
         0.0F,
         1.0F,
         distance,
         distance,
         distance,
         1.0F,
         1.0F,
         distance,
         -distance,
         distance,
         1.0F,
         0.0F
      );
      drawFace(
         tesselator,
         builder,
         cameraMatrix,
         PANORAMA[1],
         distance,
         -distance,
         distance,
         0.0F,
         0.0F,
         distance,
         distance,
         distance,
         0.0F,
         1.0F,
         -distance,
         distance,
         distance,
         1.0F,
         1.0F,
         -distance,
         -distance,
         distance,
         1.0F,
         0.0F
      );
      drawFace(
         tesselator,
         builder,
         cameraMatrix,
         PANORAMA[0],
         -distance,
         -distance,
         distance,
         0.0F,
         0.0F,
         -distance,
         distance,
         distance,
         0.0F,
         1.0F,
         -distance,
         distance,
         -distance,
         1.0F,
         1.0F,
         -distance,
         -distance,
         -distance,
         1.0F,
         0.0F
      );
      modelViewStack.m_85849_();
      RenderSystem.applyModelViewMatrix();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }

   private static boolean isVoidDimension(Level level) {
      return level != null && level.m_46472_().equals(VoidEvents.VOID_KEY);
   }

   private static void drawFace(
      Tesselator tesselator,
      BufferBuilder builder,
      Matrix4f cameraMatrix,
      ResourceLocation texture,
      float x0,
      float y0,
      float z0,
      float u0,
      float v0,
      float x1,
      float y1,
      float z1,
      float u1,
      float v1,
      float x2,
      float y2,
      float z2,
      float u2,
      float v2,
      float x3,
      float y3,
      float z3,
      float u3,
      float v3
   ) {
      RenderSystem.setShaderTexture(0, texture);
      builder.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85817_);
      builder.m_252986_(cameraMatrix, x0, y0, z0).m_7421_(u0, v0).m_5752_();
      builder.m_252986_(cameraMatrix, x1, y1, z1).m_7421_(u1, v1).m_5752_();
      builder.m_252986_(cameraMatrix, x2, y2, z2).m_7421_(u2, v2).m_5752_();
      builder.m_252986_(cameraMatrix, x3, y3, z3).m_7421_(u3, v3).m_5752_();
      tesselator.m_85914_();
   }
}
