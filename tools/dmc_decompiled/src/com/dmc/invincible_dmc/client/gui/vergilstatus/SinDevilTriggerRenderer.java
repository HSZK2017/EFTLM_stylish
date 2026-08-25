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

public class SinDevilTriggerRenderer {
   public static final ResourceLocation SDT_FILL = ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/gui/hud/vergil_sdt.png");
   public static final ResourceLocation SDT_GLOW = ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/gui/hud/vergil_sdt_glow.png");
   public static final float TEXTURE_LEFT = 0.0F;
   public static final float TEXTURE_RIGHT = 512.0F;
   public static final float SOLID_LEFT = 95.0F;
   public static final float SOLID_RIGHT = 211.0F;
   public static final float BAR_HEIGHT = 512.0F;
   public static final float FILL_WIDTH = 116.0F;
   public static final float LEFT_GLOW_WIDTH = 95.0F;
   public static final float RIGHT_GLOW_WIDTH = 301.0F;
   private static final float SECOND_CENTER = 152.0F;
   private static final float FEATHER_PX = 2.0F;

   public static void render(
      GuiGraphics g,
      float xOffset,
      float yOffset,
      float scale,
      float sdtProgress,
      float secondProgress,
      int phase,
      float firstFlash,
      float secondFlash,
      float pulseTime,
      float activeTransition
   ) {
      g.m_280262_();
      g.m_280168_().m_85836_();
      Matrix4f matrix = g.m_280168_().m_85850_().m_252922_();
      RenderSystem.setShader(GameRenderer::m_172814_);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShaderTexture(0, SDT_GLOW);
      if (activeTransition > 0.0F && phase != 4 && phase != 3) {
         renderGlow(matrix, xOffset, yOffset, scale, sdtProgress);
         RenderSystem.setShaderTexture(0, SDT_FILL);
         renderTransitionActiveState(matrix, xOffset, yOffset, scale, sdtProgress, pulseTime, activeTransition);
         renderFlowOverlay(matrix, xOffset, yOffset, scale, 95.0F, 95.0F + 116.0F * sdtProgress, pulseTime, 3, activeTransition);
      } else {
         switch (phase) {
            case 1:
               renderGlow(matrix, xOffset, yOffset, scale, sdtProgress);
               RenderSystem.setShaderTexture(0, SDT_FILL);
               renderFirstCharge(matrix, xOffset, yOffset, scale, sdtProgress);
               break;
            case 2:
               renderGlow(matrix, xOffset, yOffset, scale, 1.0F);
               RenderSystem.setShaderTexture(0, SDT_FILL);
               renderFirstCharge(matrix, xOffset, yOffset, scale, 1.0F);
               renderSecondCharge(matrix, xOffset, yOffset, scale, secondProgress);
               float halfWidth = 116.0F * secondProgress / 2.0F;
               float currentLeft = 152.0F - halfWidth;
               float currentRight = 152.0F + halfWidth;
               renderFlowOverlay(matrix, xOffset, yOffset, scale, currentLeft, currentRight, pulseTime, 1, 1.0F);
               break;
            case 3:
               renderGlow(matrix, xOffset, yOffset, scale, 1.0F);
               RenderSystem.setShaderTexture(0, SDT_FILL);
               renderReadyState(matrix, xOffset, yOffset, scale, pulseTime);
               renderFlowOverlay(matrix, xOffset, yOffset, scale, 95.0F, 211.0F, pulseTime, 2, 1.0F);
               break;
            case 4:
               renderGlow(matrix, xOffset, yOffset, scale, sdtProgress);
               RenderSystem.setShaderTexture(0, SDT_FILL);
               renderActiveState(matrix, xOffset, yOffset, scale, sdtProgress, pulseTime);
               renderFlowOverlay(matrix, xOffset, yOffset, scale, 95.0F, 95.0F + 116.0F * sdtProgress, pulseTime, 3, 1.0F);
               renderLightningEffect(matrix, xOffset, yOffset, scale, sdtProgress, pulseTime);
               break;
            default:
               if (sdtProgress > 0.0F) {
                  renderGlow(matrix, xOffset, yOffset, scale, sdtProgress);
                  RenderSystem.setShaderTexture(0, SDT_FILL);
                  renderFirstCharge(matrix, xOffset, yOffset, scale, sdtProgress);
               }
         }
      }

      g.m_280262_();
      if (firstFlash > 0.0F) {
         renderSweepHighlight(matrix, xOffset, yOffset, scale, firstFlash, 0.3F, 0.75F, 1.0F);
      }

      if (secondFlash > 0.0F) {
         renderSweepHighlight(matrix, xOffset, yOffset, scale, secondFlash, 0.85F, 0.2F, 1.0F);
      }

      g.m_280168_().m_85849_();
      g.m_280262_();
   }

   private static float getSlantedX(float p, float y, float xOffset, float scale) {
      float xBase = 95.0F + p * 110.0F;
      float tY = (y - 34.0F) / 6.0F;
      float shift = (1.0F - tY) * 6.0F;
      return xOffset + (xBase + shift) * scale;
   }

   private static void renderFlowOverlay(
      Matrix4f matrix, float xOffset, float yOffset, float scale, float leftBoundary, float rightBoundary, float pulseTime, int stateType, float alphaScale
   ) {
      if (!(leftBoundary >= rightBoundary) && !(alphaScale <= 0.0F)) {
         RenderSystem.setShader(GameRenderer::m_172811_);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
         RenderSystem.setShaderTexture(0, 0);
         float pStart = Mth.m_14036_((leftBoundary - 95.0F) / 116.0F, 0.0F, 1.0F);
         float pEnd = Mth.m_14036_((rightBoundary - 95.0F) / 116.0F, 0.0F, 1.0F);
         float width = (pEnd - pStart) * 110.0F;
         int segments = Mth.m_14045_((int)(width / (1.5F * scale)), 16, 80);
         Tesselator tesselator = Tesselator.m_85913_();
         BufferBuilder buffer = tesselator.m_85915_();
         buffer.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85815_);

         for (int i = 0; i < segments; i++) {
            float p1 = pStart + (pEnd - pStart) * ((float)i / (float)segments);
            float p2 = pStart + (pEnd - pStart) * ((float)(i + 1) / (float)segments);
            float rx1 = p1 * 110.0F;
            float rx2 = p2 * 110.0F;
            float w1_1 = Mth.m_14031_(rx1 * 0.09F - pulseTime * 4.2F);
            float w1_2 = Mth.m_14031_(rx2 * 0.09F - pulseTime * 4.2F);
            float w2_1 = Mth.m_14089_(rx1 * 0.22F + pulseTime * 2.8F);
            float w2_2 = Mth.m_14089_(rx2 * 0.22F + pulseTime * 2.8F);
            float w3_1 = Mth.m_14031_(rx1 * 0.04F - pulseTime * 1.2F);
            float w3_2 = Mth.m_14031_(rx2 * 0.04F - pulseTime * 1.2F);
            float intensity1 = (w1_1 * 0.45F + w2_1 * 0.3F + w3_1 * 0.25F + 1.0F) * 0.5F;
            float intensity2 = (w1_2 * 0.45F + w2_2 * 0.3F + w3_2 * 0.25F + 1.0F) * 0.5F;
            int r1;
            int g1;
            int b1;
            int a1;
            int r2;
            int g2;
            int b2;
            int a2;
            if (stateType == 1) {
               r1 = (int)Mth.m_14179_(intensity1, 70.0F, 180.0F);
               g1 = (int)Mth.m_14179_(intensity1, 20.0F, 90.0F);
               b1 = (int)Mth.m_14179_(intensity1, 160.0F, 240.0F);
               a1 = (int)(45.0F * intensity1 * alphaScale);
               r2 = (int)Mth.m_14179_(intensity2, 70.0F, 180.0F);
               g2 = (int)Mth.m_14179_(intensity2, 20.0F, 90.0F);
               b2 = (int)Mth.m_14179_(intensity2, 160.0F, 240.0F);
               a2 = (int)(45.0F * intensity2 * alphaScale);
            } else if (stateType == 2) {
               r1 = (int)Mth.m_14179_(intensity1, 140.0F, 230.0F);
               g1 = (int)Mth.m_14179_(intensity1, 30.0F, 160.0F);
               b1 = (int)Mth.m_14179_(intensity1, 180.0F, 255.0F);
               a1 = (int)(60.0F * intensity1 * alphaScale);
               r2 = (int)Mth.m_14179_(intensity2, 140.0F, 230.0F);
               g2 = (int)Mth.m_14179_(intensity2, 30.0F, 160.0F);
               b2 = (int)Mth.m_14179_(intensity2, 180.0F, 255.0F);
               a2 = (int)(60.0F * intensity2 * alphaScale);
            } else {
               r1 = (int)Mth.m_14179_(intensity1, 100.0F, 220.0F);
               g1 = (int)Mth.m_14179_(intensity1, 10.0F, 50.0F);
               b1 = (int)Mth.m_14179_(intensity1, 90.0F, 180.0F);
               a1 = (int)(50.0F * intensity1 * alphaScale);
               r2 = (int)Mth.m_14179_(intensity2, 100.0F, 220.0F);
               g2 = (int)Mth.m_14179_(intensity2, 10.0F, 50.0F);
               b2 = (int)Mth.m_14179_(intensity2, 90.0F, 180.0F);
               a2 = (int)(50.0F * intensity2 * alphaScale);
            }

            float xTL = getSlantedX(p1, 34.0F, xOffset, scale);
            float xBL = getSlantedX(p1, 37.0F, xOffset, scale);
            float xBR = getSlantedX(p2, 37.0F, xOffset, scale);
            float xTR = getSlantedX(p2, 34.0F, xOffset, scale);
            buffer.m_252986_(matrix, xTL, yOffset + 34.0F * scale, 0.0F).m_6122_(r1, g1, b1, (int)((float)a1 * 0.3F)).m_5752_();
            buffer.m_252986_(matrix, xBL, yOffset + 37.0F * scale, 0.0F).m_6122_(r1, g1, b1, a1).m_5752_();
            buffer.m_252986_(matrix, xBR, yOffset + 37.0F * scale, 0.0F).m_6122_(r2, g2, b2, a2).m_5752_();
            buffer.m_252986_(matrix, xTR, yOffset + 34.0F * scale, 0.0F).m_6122_(r2, g2, b2, (int)((float)a2 * 0.3F)).m_5752_();
            float xTL2 = getSlantedX(p1, 37.0F, xOffset, scale);
            float xBL2 = getSlantedX(p1, 40.0F, xOffset, scale);
            float xBR2 = getSlantedX(p2, 40.0F, xOffset, scale);
            float xTR2 = getSlantedX(p2, 37.0F, xOffset, scale);
            buffer.m_252986_(matrix, xTL2, yOffset + 37.0F * scale, 0.0F).m_6122_(r1, g1, b1, a1).m_5752_();
            buffer.m_252986_(matrix, xBL2, yOffset + 40.0F * scale, 0.0F).m_6122_(r1, g1, b1, (int)((float)a1 * 0.3F)).m_5752_();
            buffer.m_252986_(matrix, xBR2, yOffset + 40.0F * scale, 0.0F).m_6122_(r2, g2, b2, (int)((float)a2 * 0.3F)).m_5752_();
            buffer.m_252986_(matrix, xTR2, yOffset + 37.0F * scale, 0.0F).m_6122_(r2, g2, b2, a2).m_5752_();
         }

         tesselator.m_85914_();
         float currentWidthPx = (pEnd - pStart) * 116.0F;
         float marginPx = Math.min(9.0F, currentWidthPx * 0.15F);
         float pMargin = marginPx / 116.0F;
         float veinPStart = pStart + pMargin;
         float veinPEnd = pEnd - pMargin;
         if (veinPStart < veinPEnd) {
            float veinWidth = (veinPEnd - veinPStart) * 110.0F;
            int veinSegments = Mth.m_14045_((int)(veinWidth / (1.5F * scale)), 12, 60);
            buffer.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85815_);
            float baseCenterY = 37.0F;

            for (int i = 0; i < veinSegments; i++) {
               float p1 = veinPStart + (veinPEnd - veinPStart) * ((float)i / (float)veinSegments);
               float p2 = veinPStart + (veinPEnd - veinPStart) * ((float)(i + 1) / (float)veinSegments);
               float rx1 = p1 * 110.0F;
               float rx2 = p2 * 110.0F;
               float yOffset1 = getVeinOffset(rx1, pulseTime, stateType);
               float yOffset2 = getVeinOffset(rx2, pulseTime, stateType);
               float yMid1 = baseCenterY + yOffset1;
               float yMid2 = baseCenterY + yOffset2;
               float thickness;
               if (stateType == 1) {
                  thickness = 1.0F + 0.3F * Mth.m_14031_(rx1 * 0.2F + pulseTime * 2.0F);
               } else {
                  thickness = 1.3F + 0.4F * Mth.m_14031_(rx1 * 0.25F + pulseTime * 2.5F);
               }

               float yTop1 = yMid1 - thickness / 2.0F;
               float yBot1 = yMid1 + thickness / 2.0F;
               float yTop2 = yMid2 - thickness / 2.0F;
               float yBot2 = yMid2 + thickness / 2.0F;
               float xTL = getSlantedX(p1, yTop1, xOffset, scale);
               float xBL = getSlantedX(p1, yBot1, xOffset, scale);
               float xBR = getSlantedX(p2, yBot2, xOffset, scale);
               float xTR = getSlantedX(p2, yTop2, xOffset, scale);
               float edgeFade = 1.0F;
               float ratio = (float)i / (float)veinSegments;
               if (ratio < 0.15F) {
                  edgeFade = ratio / 0.15F;
               } else if (ratio > 0.85F) {
                  edgeFade = (1.0F - ratio) / 0.15F;
               }

               int[] col = getVeinColor(stateType, alphaScale * edgeFade, pulseTime, rx1);
               buffer.m_252986_(matrix, xTL, yOffset + yTop1 * scale, 0.0F).m_6122_(col[0], col[1], col[2], col[3]).m_5752_();
               buffer.m_252986_(matrix, xBL, yOffset + yBot1 * scale, 0.0F).m_6122_(col[0], col[1], col[2], col[3]).m_5752_();
               buffer.m_252986_(matrix, xBR, yOffset + yBot2 * scale, 0.0F).m_6122_(col[0], col[1], col[2], col[3]).m_5752_();
               buffer.m_252986_(matrix, xTR, yOffset + yTop2 * scale, 0.0F).m_6122_(col[0], col[1], col[2], col[3]).m_5752_();
            }

            tesselator.m_85914_();
         }

         RenderSystem.defaultBlendFunc();
         RenderSystem.setShaderTexture(0, SDT_FILL);
      }
   }

   private static float getVeinOffset(float rx, float pulseTime, int stateType) {
      float stepWidth = 15.0F;
      float val = rx / stepWidth;
      float stepVal = (float)Mth.m_14143_(val);
      float frac = val - stepVal;
      float transition = Mth.m_14036_((frac - 0.8F) / 0.2F, 0.0F, 1.0F);
      transition = transition * transition * (3.0F - 2.0F * transition);
      float h1 = sinNoise(stepVal, 0);
      float h2 = sinNoise(stepVal + 1.0F, 0);
      float speed = pulseTime * 2.5F;
      float jitter1 = Mth.m_14031_(speed + stepVal * 1.5F) * 0.2F;
      float jitter2 = Mth.m_14031_(speed + (stepVal + 1.0F) * 1.5F) * 0.2F;
      h1 += jitter1;
      h2 += jitter2;
      float microNoise = Mth.m_14031_(rx * 0.5F - pulseTime * 4.0F) * 0.1F;
      return Mth.m_14179_(transition, h1, h2) * 1.1F + microNoise;
   }

   private static float sinNoise(float step, int seed) {
      return Mth.m_14031_(step * 2.69F + (float)seed * 13.4F) * 0.7F + Mth.m_14089_(step * 1.23F - (float)seed * 5.7F) * 0.3F;
   }

   private static int[] getVeinColor(int stateType, float alphaScale, float pulseTime, float rx) {
      float breathe = 0.65F + 0.35F * Mth.m_14031_(pulseTime * 1.8F);
      float pulse = 0.75F + 0.25F * Mth.m_14031_(pulseTime * 2.2F - rx * 0.08F);
      int r;
      int g;
      int b;
      int a;
      if (stateType == 1) {
         r = (int)(210.0F * pulse);
         g = (int)(90.0F * pulse);
         b = (int)(255.0F * pulse);
         a = (int)(230.0F * alphaScale * breathe);
      } else if (stateType == 2) {
         r = (int)(255.0F * pulse);
         g = (int)(180.0F * pulse);
         b = (int)(255.0F * pulse);
         a = (int)(255.0F * alphaScale * breathe);
      } else {
         r = (int)Mth.m_14179_(pulse, 160.0F, 255.0F);
         g = (int)Mth.m_14179_(pulse, 15.0F, 235.0F);
         b = (int)Mth.m_14179_(pulse, 25.0F, 140.0F);
         a = (int)(255.0F * alphaScale * breathe);
      }

      return new int[]{r, g, b, a};
   }

   private static void renderFirstCharge(Matrix4f matrix, float xOffset, float yOffset, float scale, float progress) {
      if (!(progress <= 0.0F)) {
         float currentLeft = 95.0F;
         float currentRight = 95.0F + 116.0F * progress;
         float xLeft = xOffset + currentLeft * scale;
         float xRight = xOffset + currentRight * scale;
         float yBottom = yOffset + 512.0F * scale;
         float uMin = currentLeft / 512.0F;
         float uMax = currentRight / 512.0F;
         float vMin = 0.0F;
         float vMax = 1.0F;
         Tesselator tesselator = Tesselator.m_85913_();
         BufferBuilder buffer = tesselator.m_85915_();
         buffer.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85818_);
         if (progress < 1.0F) {
            float fillWidth = currentRight - currentLeft;
            float rightFeather = Math.min(2.0F * scale, fillWidth * scale);
            float xRightFeatherStart = xRight - rightFeather;
            float uRightFeatherStart = uMax - rightFeather / scale / 512.0F;
            buffer.m_252986_(matrix, xLeft, yOffset, 0.0F).m_6122_(255, 255, 255, 255).m_7421_(uMin, vMin).m_5752_();
            buffer.m_252986_(matrix, xLeft, yBottom, 0.0F).m_6122_(255, 255, 255, 255).m_7421_(uMin, vMax).m_5752_();
            buffer.m_252986_(matrix, xRightFeatherStart, yBottom, 0.0F).m_6122_(255, 255, 255, 255).m_7421_(uRightFeatherStart, vMax).m_5752_();
            buffer.m_252986_(matrix, xRightFeatherStart, yOffset, 0.0F).m_6122_(255, 255, 255, 255).m_7421_(uRightFeatherStart, vMin).m_5752_();
            if (rightFeather > 0.0F) {
               buffer.m_252986_(matrix, xRightFeatherStart, yOffset, 0.0F).m_6122_(255, 255, 255, 255).m_7421_(uRightFeatherStart, vMin).m_5752_();
               buffer.m_252986_(matrix, xRightFeatherStart, yBottom, 0.0F).m_6122_(255, 255, 255, 255).m_7421_(uRightFeatherStart, vMax).m_5752_();
               buffer.m_252986_(matrix, xRight, yBottom, 0.0F).m_6122_(255, 255, 255, 0).m_7421_(uMax, vMax).m_5752_();
               buffer.m_252986_(matrix, xRight, yOffset, 0.0F).m_6122_(255, 255, 255, 0).m_7421_(uMax, vMin).m_5752_();
            }
         } else {
            buffer.m_252986_(matrix, xLeft, yOffset, 0.0F).m_6122_(255, 255, 255, 255).m_7421_(uMin, vMin).m_5752_();
            buffer.m_252986_(matrix, xLeft, yBottom, 0.0F).m_6122_(255, 255, 255, 255).m_7421_(uMin, vMax).m_5752_();
            buffer.m_252986_(matrix, xRight, yBottom, 0.0F).m_6122_(255, 255, 255, 255).m_7421_(uMax, vMax).m_5752_();
            buffer.m_252986_(matrix, xRight, yOffset, 0.0F).m_6122_(255, 255, 255, 255).m_7421_(uMax, vMin).m_5752_();
         }

         tesselator.m_85914_();
      }
   }

   private static void renderSecondCharge(Matrix4f matrix, float xOffset, float yOffset, float scale, float progress) {
      if (!(progress <= 0.0F)) {
         float halfWidth = 116.0F * progress / 2.0F;
         float currentLeft = 152.0F - halfWidth;
         float currentRight = 152.0F + halfWidth;
         float xLeft = xOffset + currentLeft * scale;
         float xRight = xOffset + currentRight * scale;
         float yBottom = yOffset + 512.0F * scale;
         float uMin = currentLeft / 512.0F;
         float uMax = currentRight / 512.0F;
         float vMin = 0.0F;
         float vMax = 1.0F;
         int r = 130;
         int g = 80;
         int b = 255;
         int a = 230;
         Tesselator tesselator = Tesselator.m_85913_();
         BufferBuilder buffer = tesselator.m_85915_();
         buffer.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85818_);
         if (progress < 1.0F) {
            float fillWidth = currentRight - currentLeft;
            float rightFeather = Math.min(2.0F * scale, fillWidth * scale);
            float xRightFeatherStart = xRight - rightFeather;
            float uRightFeatherStart = uMax - rightFeather / scale / 512.0F;
            buffer.m_252986_(matrix, xLeft, yOffset, 0.0F).m_6122_(r, g, b, a).m_7421_(uMin, vMin).m_5752_();
            buffer.m_252986_(matrix, xLeft, yBottom, 0.0F).m_6122_(r, g, b, a).m_7421_(uMin, vMax).m_5752_();
            buffer.m_252986_(matrix, xRightFeatherStart, yBottom, 0.0F).m_6122_(r, g, b, a).m_7421_(uRightFeatherStart, vMax).m_5752_();
            buffer.m_252986_(matrix, xRightFeatherStart, yOffset, 0.0F).m_6122_(r, g, b, a).m_7421_(uRightFeatherStart, vMin).m_5752_();
            if (rightFeather > 0.0F) {
               buffer.m_252986_(matrix, xRightFeatherStart, yOffset, 0.0F).m_6122_(r, g, b, a).m_7421_(uRightFeatherStart, vMin).m_5752_();
               buffer.m_252986_(matrix, xRightFeatherStart, yBottom, 0.0F).m_6122_(r, g, b, a).m_7421_(uRightFeatherStart, vMax).m_5752_();
               buffer.m_252986_(matrix, xRight, yBottom, 0.0F).m_6122_(r, g, b, 0).m_7421_(uMax, vMax).m_5752_();
               buffer.m_252986_(matrix, xRight, yOffset, 0.0F).m_6122_(r, g, b, 0).m_7421_(uMax, vMin).m_5752_();
            }
         } else {
            buffer.m_252986_(matrix, xLeft, yOffset, 0.0F).m_6122_(r, g, b, a).m_7421_(uMin, vMin).m_5752_();
            buffer.m_252986_(matrix, xLeft, yBottom, 0.0F).m_6122_(r, g, b, a).m_7421_(uMin, vMax).m_5752_();
            buffer.m_252986_(matrix, xRight, yBottom, 0.0F).m_6122_(r, g, b, a).m_7421_(uMax, vMax).m_5752_();
            buffer.m_252986_(matrix, xRight, yOffset, 0.0F).m_6122_(r, g, b, a).m_7421_(uMax, vMin).m_5752_();
         }

         tesselator.m_85914_();
      }
   }

   private static void renderReadyState(Matrix4f matrix, float xOffset, float yOffset, float scale, float pulseTime) {
      float pulse = 0.95F + 0.05F * Mth.m_14031_(pulseTime * 1.2F);
      int r = (int)(255.0F * pulse);
      int g = (int)(110.0F * pulse);
      int b = (int)(255.0F * pulse);
      renderFullBarWithColor(matrix, xOffset, yOffset, scale, r, g, b, 255);
   }

   private static void renderActiveState(Matrix4f matrix, float xOffset, float yOffset, float scale, float progress, float pulseTime) {
      renderTransitionActiveState(matrix, xOffset, yOffset, scale, progress, pulseTime, 1.0F);
   }

   private static void renderTransitionActiveState(
      Matrix4f matrix, float xOffset, float yOffset, float scale, float progress, float pulseTime, float activeTransition
   ) {
      if (!(progress <= 0.0F)) {
         float flow = Mth.m_14031_(pulseTime * 0.8F);
         int targetR = (int)(120.0F + 10.0F * flow);
         int targetG = (int)(45.0F + 5.0F * Mth.m_14089_(pulseTime * 0.5F));
         int targetB = (int)(200.0F + 15.0F * flow);
         float intensity = 0.99F + 0.01F * Mth.m_14031_(pulseTime * 1.0F);
         targetR = (int)((float)targetR * intensity);
         targetG = (int)((float)targetG * intensity);
         targetB = (int)((float)targetB * intensity);
         int r = (int)Mth.m_14179_(activeTransition, 255.0F, (float)targetR);
         int g = (int)Mth.m_14179_(activeTransition, 255.0F, (float)targetG);
         int b = (int)Mth.m_14179_(activeTransition, 255.0F, (float)targetB);
         float currentLeft = 95.0F;
         float currentRight = 95.0F + 116.0F * progress;
         float xLeft = xOffset + currentLeft * scale;
         float xRight = xOffset + currentRight * scale;
         float yBottom = yOffset + 512.0F * scale;
         float uMin = currentLeft / 512.0F;
         float uMax = currentRight / 512.0F;
         float vMin = 0.0F;
         float vMax = 1.0F;
         Tesselator tesselator = Tesselator.m_85913_();
         BufferBuilder buffer = tesselator.m_85915_();
         buffer.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85818_);
         if (progress < 1.0F) {
            float fillWidth = currentRight - currentLeft;
            float rightFeather = Math.min(2.0F * scale, fillWidth * scale);
            float xRightFeatherStart = xRight - rightFeather;
            float uRightFeatherStart = uMax - rightFeather / scale / 512.0F;
            buffer.m_252986_(matrix, xLeft, yOffset, 0.0F).m_6122_(r, g, b, 255).m_7421_(uMin, vMin).m_5752_();
            buffer.m_252986_(matrix, xLeft, yBottom, 0.0F).m_6122_(r, g, b, 255).m_7421_(uMin, vMax).m_5752_();
            buffer.m_252986_(matrix, xRightFeatherStart, yBottom, 0.0F).m_6122_(r, g, b, 255).m_7421_(uRightFeatherStart, vMax).m_5752_();
            buffer.m_252986_(matrix, xRightFeatherStart, yOffset, 0.0F).m_6122_(r, g, b, 255).m_7421_(uRightFeatherStart, vMin).m_5752_();
            if (rightFeather > 0.0F) {
               buffer.m_252986_(matrix, xRightFeatherStart, yOffset, 0.0F).m_6122_(r, g, b, 255).m_7421_(uRightFeatherStart, vMin).m_5752_();
               buffer.m_252986_(matrix, xRightFeatherStart, yBottom, 0.0F).m_6122_(r, g, b, 255).m_7421_(uRightFeatherStart, vMax).m_5752_();
               buffer.m_252986_(matrix, xRight, yBottom, 0.0F).m_6122_(r, g, b, 0).m_7421_(uMax, vMax).m_5752_();
               buffer.m_252986_(matrix, xRight, yOffset, 0.0F).m_6122_(r, g, b, 0).m_7421_(uMax, vMin).m_5752_();
            }
         } else {
            buffer.m_252986_(matrix, xLeft, yOffset, 0.0F).m_6122_(r, g, b, 255).m_7421_(uMin, vMin).m_5752_();
            buffer.m_252986_(matrix, xLeft, yBottom, 0.0F).m_6122_(r, g, b, 255).m_7421_(uMin, vMax).m_5752_();
            buffer.m_252986_(matrix, xRight, yBottom, 0.0F).m_6122_(r, g, b, 255).m_7421_(uMax, vMax).m_5752_();
            buffer.m_252986_(matrix, xRight, yOffset, 0.0F).m_6122_(r, g, b, 255).m_7421_(uMax, vMin).m_5752_();
         }

         tesselator.m_85914_();
      }
   }

   private static void renderGlow(Matrix4f matrix, float xOffset, float yOffset, float scale, float progress) {
      if (!(progress <= 0.0F)) {
         float currentLeftGlow = Math.min(95.0F, 301.0F * progress);
         float currentRightGlow = 301.0F * progress;
         float glowLeft = 95.0F - currentLeftGlow;
         float glowRight = 211.0F + currentRightGlow;
         float xLeft = xOffset + glowLeft * scale;
         float xRight = xOffset + glowRight * scale;
         float yBottom = yOffset + 512.0F * scale;
         float uMin = glowLeft / 512.0F;
         float uMax = glowRight / 512.0F;
         float vMin = 0.0F;
         float vMax = 1.0F;
         Tesselator tesselator = Tesselator.m_85913_();
         BufferBuilder buffer = tesselator.m_85915_();
         buffer.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85818_);
         buffer.m_252986_(matrix, xLeft, yOffset, 0.0F).m_6122_(255, 255, 255, 255).m_7421_(uMin, vMin).m_5752_();
         buffer.m_252986_(matrix, xLeft, yBottom, 0.0F).m_6122_(255, 255, 255, 255).m_7421_(uMin, vMax).m_5752_();
         buffer.m_252986_(matrix, xRight, yBottom, 0.0F).m_6122_(255, 255, 255, 255).m_7421_(uMax, vMax).m_5752_();
         buffer.m_252986_(matrix, xRight, yOffset, 0.0F).m_6122_(255, 255, 255, 255).m_7421_(uMax, vMin).m_5752_();
         tesselator.m_85914_();
      }
   }

   private static void renderSweepHighlight(Matrix4f matrix, float xOffset, float yOffset, float scale, float flashVal, float r, float g, float b) {
      if (!(flashVal <= 0.0F)) {
         RenderSystem.setShader(GameRenderer::m_172811_);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
         RenderSystem.setShaderTexture(0, 0);
         float t = 1.0F - flashVal;
         float alpha;
         float lengthScale;
         float thicknessScale;
         if (t < 0.05F) {
            float p = t / 0.05F;
            lengthScale = p;
            thicknessScale = 0.3F + 0.7F * p;
            alpha = p;
         } else {
            float p = (t - 0.05F) / 0.95F;
            lengthScale = 1.0F - 0.15F * p;
            alpha = (float)Math.pow((double)(1.0F - p), 1.6);
            thicknessScale = (float)Math.pow((double)(1.0F - p), 2.0);
         }

         float xCenter = xOffset + 152.0F * scale;
         float yCenter = yOffset + 38.0F * scale;
         float maxHorizDist = 145.0F * lengthScale * scale;
         float maxVertDist = 24.0F * lengthScale * scale;
         int ir = (int)(r * 255.0F);
         int ig = (int)(g * 255.0F);
         int ib = (int)(b * 255.0F);
         int coreAlpha = (int)(alpha * 255.0F);
         Tesselator tesselator = Tesselator.m_85913_();
         BufferBuilder buffer = tesselator.m_85915_();
         buffer.m_166779_(Mode.TRIANGLES, DefaultVertexFormat.f_85815_);
         float bgGlowHeight = 22.0F * scale * thicknessScale;
         int bgGlowAlpha = (int)(alpha * 110.0F);
         drawSharpNeedle(matrix, buffer, xCenter, yCenter, maxHorizDist, bgGlowHeight, ir, ig, ib, bgGlowAlpha);
         float bladeHeight = 8.0F * scale * thicknessScale;
         int bladeAlpha = (int)(alpha * 220.0F);
         drawSharpNeedle(matrix, buffer, xCenter, yCenter, maxHorizDist * 0.9F, bladeHeight, ir, ig, ib, bladeAlpha);
         float coreHeight = 2.2F * scale * thicknessScale;
         drawSharpNeedle(matrix, buffer, xCenter, yCenter, maxHorizDist * 0.75F, coreHeight, 255, 255, 255, coreAlpha);
         float flareWidth = 7.0F * scale * thicknessScale;
         int flareGlowAlpha = (int)(alpha * 130.0F);
         drawSharpNeedle(matrix, buffer, xCenter, yCenter, flareWidth, maxVertDist, ir, ig, ib, flareGlowAlpha);
         float flareNeedleWidth = 3.0F * scale * thicknessScale;
         int flareNeedleAlpha = (int)(alpha * 230.0F);
         drawSharpNeedle(matrix, buffer, xCenter, yCenter, flareNeedleWidth, maxVertDist * 0.8F, ir, ig, ib, flareNeedleAlpha);
         float flareCoreWidth = 1.2F * scale * thicknessScale;
         drawSharpNeedle(matrix, buffer, xCenter, yCenter, flareCoreWidth, maxVertDist * 0.6F, 255, 255, 255, coreAlpha);
         float outerSparkSize = 12.0F * scale * thicknessScale;
         int sparkAlpha = (int)(alpha * 220.0F);
         drawSharpNeedle(matrix, buffer, xCenter, yCenter, outerSparkSize, outerSparkSize, ir, ig, ib, sparkAlpha);
         float innerSparkSize = 5.0F * scale * thicknessScale;
         drawSharpNeedle(matrix, buffer, xCenter, yCenter, innerSparkSize, innerSparkSize, 255, 255, 255, coreAlpha);
         tesselator.m_85914_();
         RenderSystem.defaultBlendFunc();
         RenderSystem.setShaderTexture(0, SDT_FILL);
      }
   }

   private static void drawSharpNeedle(
      Matrix4f matrix, BufferBuilder buffer, float xCenter, float yCenter, float halfLengthX, float halfLengthY, int r, int g, int b, int a
   ) {
      buffer.m_252986_(matrix, xCenter, yCenter, 0.0F).m_6122_(r, g, b, a).m_5752_();
      buffer.m_252986_(matrix, xCenter, yCenter - halfLengthY, 0.0F).m_6122_(r, g, b, 0).m_5752_();
      buffer.m_252986_(matrix, xCenter - halfLengthX, yCenter, 0.0F).m_6122_(r, g, b, 0).m_5752_();
      buffer.m_252986_(matrix, xCenter, yCenter, 0.0F).m_6122_(r, g, b, a).m_5752_();
      buffer.m_252986_(matrix, xCenter - halfLengthX, yCenter, 0.0F).m_6122_(r, g, b, 0).m_5752_();
      buffer.m_252986_(matrix, xCenter, yCenter + halfLengthY, 0.0F).m_6122_(r, g, b, 0).m_5752_();
      buffer.m_252986_(matrix, xCenter, yCenter, 0.0F).m_6122_(r, g, b, a).m_5752_();
      buffer.m_252986_(matrix, xCenter, yCenter + halfLengthY, 0.0F).m_6122_(r, g, b, 0).m_5752_();
      buffer.m_252986_(matrix, xCenter + halfLengthX, yCenter, 0.0F).m_6122_(r, g, b, 0).m_5752_();
      buffer.m_252986_(matrix, xCenter, yCenter, 0.0F).m_6122_(r, g, b, a).m_5752_();
      buffer.m_252986_(matrix, xCenter + halfLengthX, yCenter, 0.0F).m_6122_(r, g, b, 0).m_5752_();
      buffer.m_252986_(matrix, xCenter, yCenter - halfLengthY, 0.0F).m_6122_(r, g, b, 0).m_5752_();
   }

   private static void renderFullBarWithColor(Matrix4f matrix, float xOffset, float yOffset, float scale, int r, int g, int b, int a) {
      float xLeft = xOffset + 0.0F * scale;
      float xRight = xOffset + 512.0F * scale;
      float yBottom = yOffset + 512.0F * scale;
      float uMin = 0.0F;
      float uMax = 1.0F;
      float vMin = 0.0F;
      float vMax = 1.0F;
      Tesselator tesselator = Tesselator.m_85913_();
      BufferBuilder buffer = tesselator.m_85915_();
      buffer.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85818_);
      buffer.m_252986_(matrix, xLeft, yOffset, 0.0F).m_6122_(r, g, b, a).m_7421_(uMin, vMin).m_5752_();
      buffer.m_252986_(matrix, xLeft, yBottom, 0.0F).m_6122_(r, g, b, a).m_7421_(uMin, vMax).m_5752_();
      buffer.m_252986_(matrix, xRight, yBottom, 0.0F).m_6122_(r, g, b, a).m_7421_(uMax, vMax).m_5752_();
      buffer.m_252986_(matrix, xRight, yOffset, 0.0F).m_6122_(r, g, b, a).m_7421_(uMax, vMin).m_5752_();
      tesselator.m_85914_();
   }

   private static void renderLightningEffect(Matrix4f matrix, float xOffset, float yOffset, float scale, float progress, float pulseTime) {
      if (!(progress <= 0.0F)) {
         RenderSystem.setShader(GameRenderer::m_172811_);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
         RenderSystem.setShaderTexture(0, 0);
         float barLeft = 95.0F;
         float barRight = 95.0F + 116.0F * progress;
         float barWidth = barRight - barLeft;
         float barCenterY = 37.0F;
         Tesselator tesselator = Tesselator.m_85913_();
         BufferBuilder buffer = tesselator.m_85915_();

         for (int bolt = 0; bolt < 2; bolt++) {
            float flicker = computeHighVoltageFlicker(pulseTime, bolt);
            if (!(flicker < 0.08F)) {
               int segments = Mth.m_14045_((int)(barWidth / (2.2F * scale)), 16, 60);
               float segWidth = barWidth / (float)segments;
               float[] pathX = new float[segments + 1];
               float[] pathY = new float[segments + 1];

               for (int i = 0; i <= segments; i++) {
                  pathX[i] = barLeft + segWidth * (float)i;
                  pathY[i] = barCenterY + generateRazorJitter(i, barWidth, (float)bolt * 13.7F, pulseTime);
               }

               float glowThick = (1.1F + 0.5F * Mth.m_14031_(pulseTime * 18.0F + (float)bolt * 3.0F)) * scale;
               int glowA = (int)(flicker * 0.45F * 255.0F);
               renderSharpBoltPath(buffer, matrix, xOffset, yOffset, scale, pathX, pathY, glowThick, 60, 130, 255, glowA);
               tesselator.m_85914_();
               float midThick = (0.6F + 0.3F * Mth.m_14031_(pulseTime * 24.0F + (float)bolt * 1.5F)) * scale;
               int midA = (int)(flicker * 0.85F * 255.0F);
               renderSharpBoltPath(buffer, matrix, xOffset, yOffset, scale, pathX, pathY, midThick, 160, 220, 255, midA);
               tesselator.m_85914_();
               float coreThick = (0.3F + 0.1F * Mth.m_14031_(pulseTime * 30.0F + (float)bolt)) * scale;
               int coreA = (int)(flicker * 255.0F);
               renderSharpBoltPath(buffer, matrix, xOffset, yOffset, scale, pathX, pathY, coreThick, 255, 255, 255, coreA);
               tesselator.m_85914_();
               if (flicker > 0.6F) {
                  renderBladeSparks(buffer, matrix, xOffset, yOffset, scale, pathX, pathY, flicker, pulseTime, bolt);
               }
            }
         }

         int strayCount = barWidth > 40.0F ? 2 : 1;

         for (int s = 0; s < strayCount; s++) {
            float strayFlicker = computeHighVoltageFlicker(pulseTime * 1.3F, s + 10);
            if (!(strayFlicker < 0.25F)) {
               float pStart = Mth.m_14187_(pulseTime * (0.4F + (float)s * 0.2F) + (float)s * 0.5F);
               float lengthRatio = 0.25F + 0.25F * Mth.m_14031_((float)s + pulseTime);
               float pEnd = Mth.m_14036_(pStart + lengthRatio, 0.0F, 1.0F);
               float sLeft = barLeft + barWidth * pStart;
               float sRight = barLeft + barWidth * pEnd;
               float sWidth = sRight - sLeft;
               if (!(sWidth < 8.0F)) {
                  int sSegs = Mth.m_14045_((int)(sWidth / (2.5F * scale)), 8, 24);
                  float[] sX = new float[sSegs + 1];
                  float[] sY = new float[sSegs + 1];

                  for (int i = 0; i <= sSegs; i++) {
                     sX[i] = sLeft + sWidth / (float)sSegs * (float)i;
                     sY[i] = barCenterY + generateRazorJitter(i + s * 15, sWidth, (float)s * 9.3F, pulseTime * 1.5F) * 1.2F;
                  }

                  float sThick = 0.5F * scale;
                  renderSharpBoltPath(buffer, matrix, xOffset, yOffset, scale, sX, sY, sThick * 1.5F, 110, 60, 255, (int)(strayFlicker * 140.0F));
                  tesselator.m_85914_();
                  renderSharpBoltPath(buffer, matrix, xOffset, yOffset, scale, sX, sY, sThick, 200, 180, 255, (int)(strayFlicker * 220.0F));
                  tesselator.m_85914_();
               }
            }
         }

         RenderSystem.defaultBlendFunc();
         RenderSystem.setShaderTexture(0, SDT_FILL);
      }
   }

   private static float computeHighVoltageFlicker(float pulseTime, int bolt) {
      float seed = (float)bolt * 13.37F;
      float fastPulse = Mth.m_14031_(pulseTime * 28.0F + seed) * 0.35F;
      float hyperPulse = Mth.m_14089_(pulseTime * 45.0F - seed * 2.1F) * 0.25F;
      float base = 0.6F + fastPulse + hyperPulse;
      float breakdown = Mth.m_14031_(pulseTime * 14.2F + seed * 3.7F);
      if (breakdown > 0.7F) {
         base = 1.0F;
      } else if (breakdown < -0.6F) {
         base *= 0.15F;
      }

      return Mth.m_14036_(base, 0.0F, 1.0F);
   }

   private static float generateRazorJitter(int segIndex, float barWidth, float seed, float pulseTime) {
      float x = (float)segIndex * 1.35F;
      float j1 = Mth.m_14031_(x * 0.65F + seed + pulseTime * 18.0F) * 1.1F;
      float j2 = Mth.m_14089_(x * 1.45F - seed * 1.5F - pulseTime * 24.0F) * 0.55F;
      float j3 = Mth.m_14031_(x * 2.8F + pulseTime * 35.0F) * 0.25F;
      float jitter = j1 + j2 + j3;
      return Mth.m_14036_(jitter, -1.7F, 1.7F);
   }

   private static void renderSharpBoltPath(
      BufferBuilder buffer,
      Matrix4f matrix,
      float xOffset,
      float yOffset,
      float scale,
      float[] pathX,
      float[] pathY,
      float thickness,
      int r,
      int g,
      int b,
      int a
   ) {
      int n = pathX.length;
      if (n >= 2) {
         buffer.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85815_);

         for (int i = 0; i < n - 1; i++) {
            float x1 = pathX[i];
            float y1 = pathY[i];
            float x2 = pathX[i + 1];
            float y2 = pathY[i + 1];
            float dx = x2 - x1;
            float dy = y2 - y1;
            float len = (float)Math.sqrt((double)(dx * dx + dy * dy));
            if (!(len < 0.001F)) {
               float nx = -dy / len * thickness / 2.0F;
               float ny = dx / len * thickness / 2.0F;
               float nodeNoise = Mth.m_14031_((float)i * 1.7F + x1 * 0.9F);
               float segAlphaRatio = nodeNoise < -0.65F ? 0.15F : (nodeNoise > 0.7F ? 1.0F : 0.85F);
               int sa = Mth.m_14045_((int)((float)a * segAlphaRatio), 0, 255);
               float xTL = xOffset + (x1 + nx) * scale;
               float yTL = yOffset + (y1 + ny) * scale;
               float xBL = xOffset + (x1 - nx) * scale;
               float yBL = yOffset + (y1 - ny) * scale;
               float xBR = xOffset + (x2 - nx) * scale;
               float yBR = yOffset + (y2 - ny) * scale;
               float xTR = xOffset + (x2 + nx) * scale;
               float yTR = yOffset + (y2 + ny) * scale;
               buffer.m_252986_(matrix, xTL, yTL, 0.0F).m_6122_(r, g, b, sa).m_5752_();
               buffer.m_252986_(matrix, xBL, yBL, 0.0F).m_6122_(r, g, b, sa).m_5752_();
               buffer.m_252986_(matrix, xBR, yBR, 0.0F).m_6122_(r, g, b, sa).m_5752_();
               buffer.m_252986_(matrix, xTR, yTR, 0.0F).m_6122_(r, g, b, sa).m_5752_();
            }
         }
      }
   }

   private static void renderBladeSparks(
      BufferBuilder buffer, Matrix4f matrix, float xOffset, float yOffset, float scale, float[] pathX, float[] pathY, float flicker, float pulseTime, int bolt
   ) {
      int sparkCount = Mth.m_14045_((int)((float)pathX.length * 0.08F), 1, 4);

      for (int s = 0; s < sparkCount; s++) {
         float sparkSeed = (float)bolt * 11.0F + (float)s * 5.3F;
         int idx = (int)((float)pathX.length * ((Mth.m_14031_(sparkSeed + pulseTime * 3.5F) + 1.0F) / 2.0F));
         idx = Mth.m_14045_(idx, 2, pathX.length - 3);
         float sx = pathX[idx];
         float sy = pathY[idx];
         float sparkAlpha = flicker * (0.5F + 0.5F * Mth.m_14031_(pulseTime * 30.0F + sparkSeed));
         if (!(sparkAlpha < 0.35F)) {
            float hLen = (5.0F + 7.0F * sparkAlpha) * scale;
            float vLen = (0.8F + 1.2F * sparkAlpha) * scale;
            int a = (int)(sparkAlpha * 255.0F);
            buffer.m_166779_(Mode.TRIANGLES, DefaultVertexFormat.f_85815_);
            drawBladeSparkNeedle(buffer, matrix, xOffset, yOffset, scale, sx, sy, hLen, vLen, 80, 160, 255, (int)((float)a * 0.6F));
            Tesselator.m_85913_().m_85914_();
            buffer.m_166779_(Mode.TRIANGLES, DefaultVertexFormat.f_85815_);
            drawBladeSparkNeedle(buffer, matrix, xOffset, yOffset, scale, sx, sy, hLen * 0.55F, vLen * 0.5F, 255, 255, 255, a);
            Tesselator.m_85913_().m_85914_();
         }
      }
   }

   private static void drawBladeSparkNeedle(
      BufferBuilder buffer, Matrix4f matrix, float xOffset, float yOffset, float scale, float cx, float cy, float hLen, float vLen, int r, int g, int b, int a
   ) {
      float scx = xOffset + cx * scale;
      float scy = yOffset + cy * scale;
      buffer.m_252986_(matrix, scx, scy, 0.0F).m_6122_(r, g, b, a).m_5752_();
      buffer.m_252986_(matrix, scx - hLen, scy, 0.0F).m_6122_(r, g, b, 0).m_5752_();
      buffer.m_252986_(matrix, scx, scy - vLen * 0.3F, 0.0F).m_6122_(r, g, b, 0).m_5752_();
      buffer.m_252986_(matrix, scx, scy, 0.0F).m_6122_(r, g, b, a).m_5752_();
      buffer.m_252986_(matrix, scx, scy + vLen * 0.3F, 0.0F).m_6122_(r, g, b, 0).m_5752_();
      buffer.m_252986_(matrix, scx - hLen, scy, 0.0F).m_6122_(r, g, b, 0).m_5752_();
      buffer.m_252986_(matrix, scx, scy, 0.0F).m_6122_(r, g, b, a).m_5752_();
      buffer.m_252986_(matrix, scx, scy - vLen * 0.3F, 0.0F).m_6122_(r, g, b, 0).m_5752_();
      buffer.m_252986_(matrix, scx + hLen, scy, 0.0F).m_6122_(r, g, b, 0).m_5752_();
      buffer.m_252986_(matrix, scx, scy, 0.0F).m_6122_(r, g, b, a).m_5752_();
      buffer.m_252986_(matrix, scx + hLen, scy, 0.0F).m_6122_(r, g, b, 0).m_5752_();
      buffer.m_252986_(matrix, scx, scy + vLen * 0.3F, 0.0F).m_6122_(r, g, b, 0).m_5752_();
      buffer.m_252986_(matrix, scx, scy, 0.0F).m_6122_(r, g, b, a).m_5752_();
      buffer.m_252986_(matrix, scx - hLen * 0.15F, scy, 0.0F).m_6122_(r, g, b, 0).m_5752_();
      buffer.m_252986_(matrix, scx, scy - vLen, 0.0F).m_6122_(r, g, b, 0).m_5752_();
      buffer.m_252986_(matrix, scx, scy, 0.0F).m_6122_(r, g, b, a).m_5752_();
      buffer.m_252986_(matrix, scx, scy - vLen, 0.0F).m_6122_(r, g, b, 0).m_5752_();
      buffer.m_252986_(matrix, scx + hLen * 0.15F, scy, 0.0F).m_6122_(r, g, b, 0).m_5752_();
      buffer.m_252986_(matrix, scx, scy, 0.0F).m_6122_(r, g, b, a).m_5752_();
      buffer.m_252986_(matrix, scx + hLen * 0.15F, scy, 0.0F).m_6122_(r, g, b, 0).m_5752_();
      buffer.m_252986_(matrix, scx, scy + vLen, 0.0F).m_6122_(r, g, b, 0).m_5752_();
      buffer.m_252986_(matrix, scx, scy, 0.0F).m_6122_(r, g, b, a).m_5752_();
      buffer.m_252986_(matrix, scx, scy + vLen, 0.0F).m_6122_(r, g, b, 0).m_5752_();
      buffer.m_252986_(matrix, scx - hLen * 0.15F, scy, 0.0F).m_6122_(r, g, b, 0).m_5752_();
   }
}
