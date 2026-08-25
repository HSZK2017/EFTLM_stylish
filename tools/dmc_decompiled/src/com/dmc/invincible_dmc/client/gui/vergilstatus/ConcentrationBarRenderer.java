package com.dmc.invincible_dmc.client.gui.vergilstatus;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class ConcentrationBarRenderer {
   public static final ResourceLocation TEXTURE_LEFT = ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/gui/hud/vergil_concentration_left.png");
   public static final ResourceLocation TEXTURE_RIGHT = ResourceLocation.fromNamespaceAndPath(
      "invincible_dmc", "textures/gui/hud/vergil_concentration_right.png"
   );
   private static final float ROTATION_CENTER_X = 55.0F;
   private static final float ROTATION_CENTER_Y = 34.0F;
   private static final float RADIAL_RADIUS = 45.0F;
   private static final float BAR_START_X = 39.0F;
   private static final float BAR_END_X = 243.0F;
   public static final float RADIAL_THRESHOLD = 0.461F;
   public static final float RADIAL_CONNECT_DEG = 222.2F;
   private static final float RADIAL_FEATHER_DEG = 6.0F;
   private static final float LINEAR_FEATHER_WIDTH = 8.0F;

   public static void render(GuiGraphics g, float xOffset, float yOffset, float scale, float progress, float flashAlpha) {
      if (progress >= 0.999F) {
         progress = 1.0F;
      } else if (progress <= 1.0E-4F) {
         if (flashAlpha <= 0.0F) {
            return;
         }

         progress = 0.0F;
      }

      g.m_280262_();
      g.m_280168_().m_85836_();
      Matrix4f matrix = g.m_280168_().m_85850_().m_252922_();
      float radialAngleDeg = Math.min(360.0F, progress * 481.99567F);
      renderRadialCrest(matrix, xOffset, yOffset, scale, radialAngleDeg);
      g.m_280262_();
      if (progress > 0.461F) {
         float barProgress = (progress - 0.461F) / 0.53900003F;
         renderLinearBar(matrix, xOffset, yOffset, scale, barProgress);
      }

      if (flashAlpha > 0.0F) {
         renderFlashOverlay(matrix, xOffset, yOffset, scale, progress, flashAlpha);
      }

      g.m_280168_().m_85849_();
   }

   private static void renderRadialCrest(Matrix4f matrix, float xOffset, float yOffset, float scale, float sweepAngleDeg) {
      if (!(sweepAngleDeg <= 0.0F)) {
         RenderSystem.setShader(GameRenderer::m_172814_);
         RenderSystem.setShaderTexture(0, TEXTURE_LEFT);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         float centerX = xOffset + 55.0F * scale;
         float centerY = yOffset + 34.0F * scale;
         float radius = 45.0F * scale;
         Tesselator tesselator = Tesselator.m_85913_();
         BufferBuilder buffer = tesselator.m_85915_();
         buffer.m_166779_(Mode.TRIANGLES, DefaultVertexFormat.f_85818_);
         int segments = 64;
         float startAngle = 0.0F;
         float sweepAngleRad = sweepAngleDeg * (float) (Math.PI / 180.0);
         float currentFeather = Math.min(6.0F, 222.2F - sweepAngleDeg);

         for (int i = 0; i < segments; i++) {
            float angle1 = startAngle + (float)i / (float)segments * sweepAngleRad;
            float angle2 = startAngle + (float)(i + 1) / (float)segments * sweepAngleRad;
            float deg1 = angle1 * (180.0F / (float)Math.PI);
            float deg2 = angle2 * (180.0F / (float)Math.PI);
            int alpha1 = 255;
            int alpha2 = 255;
            if (currentFeather > 0.0F) {
               if (deg1 > sweepAngleDeg - currentFeather) {
                  float ratio = (deg1 - (sweepAngleDeg - currentFeather)) / currentFeather;
                  alpha1 = (int)((1.0F - Mth.m_14036_(ratio, 0.0F, 1.0F)) * 255.0F);
               }

               if (deg2 > sweepAngleDeg - currentFeather) {
                  float ratio = (deg2 - (sweepAngleDeg - currentFeather)) / currentFeather;
                  alpha2 = (int)((1.0F - Mth.m_14036_(ratio, 0.0F, 1.0F)) * 255.0F);
               }
            }

            int centerAlpha = (alpha1 + alpha2) / 2;
            float cos1 = Mth.m_14089_(angle1);
            float sin1 = Mth.m_14031_(angle1);
            float cos2 = Mth.m_14089_(angle2);
            float sin2 = Mth.m_14031_(angle2);
            buffer.m_252986_(matrix, centerX, centerY, 0.0F).m_6122_(255, 255, 255, centerAlpha).m_7421_(0.107421875F, 0.06640625F).m_5752_();
            buffer.m_252986_(matrix, centerX + radius * cos1, centerY + radius * sin1, 0.0F)
               .m_6122_(255, 255, 255, alpha1)
               .m_7421_((55.0F + 45.0F * cos1) / 512.0F, (34.0F + 45.0F * sin1) / 512.0F)
               .m_5752_();
            buffer.m_252986_(matrix, centerX + radius * cos2, centerY + radius * sin2, 0.0F)
               .m_6122_(255, 255, 255, alpha2)
               .m_7421_((55.0F + 45.0F * cos2) / 512.0F, (34.0F + 45.0F * sin2) / 512.0F)
               .m_5752_();
         }

         tesselator.m_85914_();
      }
   }

   private static void renderLinearBar(Matrix4f matrix, float xOffset, float yOffset, float scale, float progress) {
      if (!(progress <= 0.0F)) {
         RenderSystem.setShader(GameRenderer::m_172814_);
         RenderSystem.setShaderTexture(0, TEXTURE_RIGHT);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         float maxBarWidth = 204.0F;
         float currentBarWidth = progress >= 1.0F ? maxBarWidth : maxBarWidth * progress;
         float currentX = 39.0F + currentBarWidth;
         float featherWidth = progress >= 1.0F ? 0.0F : 8.0F;
         float actualFeather = Math.min(featherWidth, currentBarWidth);
         float midX = currentX - actualFeather;
         float xMid = xOffset + midX * scale;
         float xEnd = xOffset + currentX * scale;
         float bottom = yOffset + 512.0F * scale;
         float uMin = 0.0F;
         float uMid = midX / 512.0F;
         float uMax = currentX / 512.0F;
         float vMin = 0.0F;
         float vMax = 1.0F;
         Tesselator tesselator = Tesselator.m_85913_();
         BufferBuilder buffer = tesselator.m_85915_();
         buffer.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85818_);
         if (actualFeather > 0.0F) {
            buffer.m_252986_(matrix, xOffset, yOffset, 0.0F).m_6122_(255, 255, 255, 255).m_7421_(uMin, vMin).m_5752_();
            buffer.m_252986_(matrix, xOffset, bottom, 0.0F).m_6122_(255, 255, 255, 255).m_7421_(uMin, vMax).m_5752_();
            buffer.m_252986_(matrix, xMid, bottom, 0.0F).m_6122_(255, 255, 255, 255).m_7421_(uMid, vMax).m_5752_();
            buffer.m_252986_(matrix, xMid, yOffset, 0.0F).m_6122_(255, 255, 255, 255).m_7421_(uMid, vMin).m_5752_();
            buffer.m_252986_(matrix, xMid, yOffset, 0.0F).m_6122_(255, 255, 255, 255).m_7421_(uMid, vMin).m_5752_();
            buffer.m_252986_(matrix, xMid, bottom, 0.0F).m_6122_(255, 255, 255, 255).m_7421_(uMid, vMax).m_5752_();
            buffer.m_252986_(matrix, xEnd, bottom, 0.0F).m_6122_(255, 255, 255, 0).m_7421_(uMax, vMax).m_5752_();
            buffer.m_252986_(matrix, xEnd, yOffset, 0.0F).m_6122_(255, 255, 255, 0).m_7421_(uMax, vMin).m_5752_();
         } else {
            buffer.m_252986_(matrix, xOffset, yOffset, 0.0F).m_6122_(255, 255, 255, 255).m_7421_(uMin, vMin).m_5752_();
            buffer.m_252986_(matrix, xOffset, bottom, 0.0F).m_6122_(255, 255, 255, 255).m_7421_(uMin, vMax).m_5752_();
            buffer.m_252986_(matrix, xEnd, bottom, 0.0F).m_6122_(255, 255, 255, 255).m_7421_(uMax, vMax).m_5752_();
            buffer.m_252986_(matrix, xEnd, yOffset, 0.0F).m_6122_(255, 255, 255, 255).m_7421_(uMax, vMin).m_5752_();
         }

         tesselator.m_85914_();
      }
   }

   private static void renderFlashOverlay(Matrix4f matrix, float xOffset, float yOffset, float scale, float progress, float flashAlpha) {
      RenderSystem.setShader(GameRenderer::m_172814_);
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      float alpha = flashAlpha * 0.55F;
      int colorAlpha = (int)(alpha * 255.0F);
      float cx = xOffset + 55.0F * scale;
      float cy = yOffset + 34.0F * scale;
      float radius = 45.0F * scale * 1.15F;
      RenderSystem.setShaderTexture(0, TEXTURE_LEFT);
      Tesselator tesselator = Tesselator.m_85913_();
      BufferBuilder buffer = tesselator.m_85915_();
      float fullSweepDeg = 481.99567F;
      float flashSweepDeg = progress > 0.461F
         ? Math.min(360.0F, progress * fullSweepDeg)
         : (progress > 1.0E-4F ? Math.min(360.0F, progress * fullSweepDeg) : fullSweepDeg * 0.15F);
      int segments = 64;
      float sweepAngleRad = flashSweepDeg * (float) (Math.PI / 180.0);
      buffer.m_166779_(Mode.TRIANGLES, DefaultVertexFormat.f_85818_);

      for (int i = 0; i < segments; i++) {
         float angle1 = (float)i / (float)segments * sweepAngleRad;
         float angle2 = (float)(i + 1) / (float)segments * sweepAngleRad;
         float cos1 = Mth.m_14089_(angle1);
         float sin1 = Mth.m_14031_(angle1);
         float cos2 = Mth.m_14089_(angle2);
         float sin2 = Mth.m_14031_(angle2);
         float uCenter = 0.107421875F;
         float vCenter = 0.06640625F;
         float u1 = (55.0F + 45.0F * cos1 * 1.15F) / 512.0F;
         float v1 = (34.0F + 45.0F * sin1 * 1.15F) / 512.0F;
         float u2 = (55.0F + 45.0F * cos2 * 1.15F) / 512.0F;
         float v2 = (34.0F + 45.0F * sin2 * 1.15F) / 512.0F;
         buffer.m_252986_(matrix, cx, cy, 0.0F).m_6122_(255, 255, 255, colorAlpha).m_7421_(uCenter, vCenter).m_5752_();
         buffer.m_252986_(matrix, cx + radius * cos1, cy + radius * sin1, 0.0F).m_6122_(255, 255, 255, colorAlpha).m_7421_(u1, v1).m_5752_();
         buffer.m_252986_(matrix, cx + radius * cos2, cy + radius * sin2, 0.0F).m_6122_(255, 255, 255, colorAlpha).m_7421_(u2, v2).m_5752_();
      }

      tesselator.m_85914_();
      if (progress > 0.461F) {
         float barProgress = (progress - 0.461F) / 0.53900003F;
         float maxBarWidth = 204.0F;
         float currentBarWidth = barProgress >= 1.0F ? maxBarWidth : maxBarWidth * barProgress;
         float xEnd = xOffset + (39.0F + currentBarWidth) * scale;
         RenderSystem.setShaderTexture(0, TEXTURE_RIGHT);
         buffer.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85818_);
         buffer.m_252986_(matrix, xOffset + 39.0F * scale, yOffset, 0.0F).m_6122_(255, 255, 255, colorAlpha).m_7421_(0.0F, 0.0F).m_5752_();
         buffer.m_252986_(matrix, xOffset + 39.0F * scale, yOffset + 512.0F * scale, 0.0F).m_6122_(255, 255, 255, colorAlpha).m_7421_(0.0F, 1.0F).m_5752_();
         buffer.m_252986_(matrix, xEnd, yOffset + 512.0F * scale, 0.0F).m_6122_(255, 255, 255, colorAlpha).m_7421_(currentBarWidth / 512.0F, 1.0F).m_5752_();
         buffer.m_252986_(matrix, xEnd, yOffset, 0.0F).m_6122_(255, 255, 255, colorAlpha).m_7421_(currentBarWidth / 512.0F, 0.0F).m_5752_();
         tesselator.m_85914_();
      }

      RenderSystem.defaultBlendFunc();
   }
}
