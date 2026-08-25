package com.dmc.invincible_dmc.client.render.cinematic;

import com.dmc.invincible_dmc.utils.vfx.CinematicBarsUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;

public final class CinematicBarsWorldRenderer {
   private static long renderedFrame = Long.MIN_VALUE;
   private static boolean commandMaskEnabled;
   private static float commandMaskHalfWidthRatio = 0.012F;

   private CinematicBarsWorldRenderer() {
   }

   public static void enableCommandMask(float sizeRatio) {
      commandMaskEnabled = true;
      commandMaskHalfWidthRatio = Math.min(0.025F, Math.max(0.006F, sizeRatio * 0.18F));
      renderedFrame = Long.MIN_VALUE;
   }

   public static void disableCommandMask() {
      commandMaskEnabled = false;
      renderedFrame = Long.MIN_VALUE;
   }

   public static boolean isCommandMaskEnabled() {
      return commandMaskEnabled;
   }

   public static float getCommandMaskHalfWidthRatio() {
      return commandMaskHalfWidthRatio;
   }

   public static boolean hasVisibleMask() {
      return commandMaskEnabled || CinematicBarsUtils.isVisible() || CinematicBarsUtils.getTargetLetterbox() > 0.0F;
   }

   public static void renderBeforeLateWorldEffects() {
      Minecraft minecraft = Minecraft.m_91087_();
      long frame = minecraft.m_261169_();
      if (renderedFrame != frame) {
         renderedFrame = frame;
         CinematicBarsUtils.updateAnimation();
         float heightRatio = CinematicBarsUtils.getRenderedHeightRatio();
         if (!(heightRatio <= 0.0F) || commandMaskEnabled) {
            float innerY = 1.0F - heightRatio * 2.0F;
            float featherRatio = heightRatio * 0.37F;
            float solidInnerY = 1.0F - (heightRatio - featherRatio) * 2.0F;
            float verticalHalfWidth = commandMaskHalfWidthRatio * 2.0F;
            float leftCenter = -0.4F;
            float rightCenter = 0.4F;
            PoseStack modelViewStack = RenderSystem.getModelViewStack();
            RenderSystem.backupProjectionMatrix();
            modelViewStack.m_85836_();

            try {
               modelViewStack.m_166856_();
               RenderSystem.applyModelViewMatrix();
               RenderSystem.setProjectionMatrix(new Matrix4f().identity(), VertexSorting.f_276633_);
               RenderSystem.setShader(GameRenderer::m_172811_);
               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
               RenderSystem.disableDepthTest();
               RenderSystem.depthMask(false);
               RenderSystem.enableBlend();
               RenderSystem.defaultBlendFunc();
               Tesselator tesselator = Tesselator.m_85913_();
               BufferBuilder buffer = tesselator.m_85915_();
               buffer.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85815_);
               if (heightRatio > 0.0F) {
                  solidQuad(buffer, -1.0F, solidInnerY, 1.0F, 1.0F);
                  gradientQuad(buffer, -1.0F, innerY, 1.0F, solidInnerY, 0, 255);
                  gradientQuad(buffer, -1.0F, -solidInnerY, 1.0F, -innerY, 255, 0);
                  solidQuad(buffer, -1.0F, -1.0F, 1.0F, -solidInnerY);
               }

               if (commandMaskEnabled) {
                  solidQuad(buffer, leftCenter - verticalHalfWidth, -1.0F, leftCenter + verticalHalfWidth, 1.0F);
                  solidQuad(buffer, rightCenter - verticalHalfWidth, -1.0F, rightCenter + verticalHalfWidth, 1.0F);
               }

               tesselator.m_85914_();
            } finally {
               RenderSystem.depthMask(true);
               RenderSystem.enableDepthTest();
               RenderSystem.disableBlend();
               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
               modelViewStack.m_85849_();
               RenderSystem.applyModelViewMatrix();
               RenderSystem.restoreProjectionMatrix();
            }
         }
      }
   }

   private static void solidQuad(BufferBuilder buffer, float left, float bottom, float right, float top) {
      vertex(buffer, left, bottom, 255);
      vertex(buffer, right, bottom, 255);
      vertex(buffer, right, top, 255);
      vertex(buffer, left, top, 255);
   }

   private static void gradientQuad(BufferBuilder buffer, float left, float bottom, float right, float top, int bottomAlpha, int topAlpha) {
      vertex(buffer, left, bottom, bottomAlpha);
      vertex(buffer, right, bottom, bottomAlpha);
      vertex(buffer, right, top, topAlpha);
      vertex(buffer, left, top, topAlpha);
   }

   private static void vertex(BufferBuilder buffer, float x, float y, int alpha) {
      buffer.m_5483_((double)x, (double)y, 0.0).m_6122_(0, 0, 0, alpha).m_5752_();
   }
}
