package com.dmc.invincible_dmc.client.gui.vergilstatus;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class DevilTriggerRenderer {
   private static final ResourceLocation[] TEXTURE_CELLS = new ResourceLocation[10];
   private static final float[] cellScales;
   private static final float[] cellVelocities;
   private static final float[] cellAlphas;
   private static final float[] cellAlphaVelocities;
   private static final float[] cellColorsR;
   private static final float[] cellColorsG;
   private static final float[] cellColorsB;
   private static final float[] cellColorVelR;
   private static final float[] cellColorVelG;
   private static final float[] cellColorVelB;
   private static float fillValue;
   private static float prevFillValue;
   private static int doppelMode;
   private static final float[] popTimers;
   private static final float POP_BOOST_DURATION = 0.3F;
   private static final float POP_SCALE_BOOST = 180.0F;
   private static final float POP_ALPHA_BOOST = 60.0F;
   private static final float POP_COLOR_BOOST = 40.0F;
   private static final float[] downTimers;
   private static final float DOWN_BOOST_DURATION = 0.2F;
   private static final float DOWN_SCALE_BOOST = -120.0F;
   private static final float DOWN_ALPHA_BOOST = -50.0F;
   private static final float TIER_67 = 0.67F;
   private static final float TIER_34 = 0.34F;
   private static final float ALPHA_FULL = 1.0F;
   private static final float ALPHA_67 = 0.55F;
   private static final float ALPHA_34 = 0.3F;
   private static final float ALPHA_LOW = 0.12F;
   private static final float R_FULL = 1.0F;
   private static final float G_FULL = 1.0F;
   private static final float B_FULL = 1.0F;
   private static final float R_67 = 0.85F;
   private static final float G_67 = 0.8F;
   private static final float B_67 = 1.0F;
   private static final float R_34 = 0.5F;
   private static final float G_34 = 0.4F;
   private static final float B_34 = 0.9F;
   private static final float R_LOW = 0.25F;
   private static final float G_LOW = 0.15F;
   private static final float B_LOW = 0.6F;

   private static void targetColor(float cellFill, float[] out) {
      if (cellFill >= 1.0F) {
         out[0] = 1.0F;
         out[1] = 1.0F;
         out[2] = 1.0F;
      } else if (cellFill >= 0.67F) {
         out[0] = 0.85F;
         out[1] = 0.8F;
         out[2] = 1.0F;
      } else if (cellFill >= 0.34F) {
         out[0] = 0.5F;
         out[1] = 0.4F;
         out[2] = 0.9F;
      } else {
         out[0] = 0.25F;
         out[1] = 0.15F;
         out[2] = 0.6F;
      }
   }

   private static float targetAlpha(float cellFill) {
      if (cellFill >= 1.0F) {
         return 1.0F;
      } else if (cellFill >= 0.67F) {
         return 0.55F;
      } else {
         return cellFill >= 0.34F ? 0.3F : 0.12F;
      }
   }

   public static void update(float fillValue, float deltaTime, int doppelMode) {
      DevilTriggerRenderer.doppelMode = doppelMode;
      float newValue = Mth.m_14036_(fillValue, 0.0F, 10.0F);
      int prevStack = (int)prevFillValue;
      int newStack = (int)newValue;
      if (newStack > prevStack && prevStack >= 0 && prevStack < 10) {
         popTimers[prevStack] = 0.3F;
      }

      if (newStack < prevStack && newStack >= 0 && newStack < 10) {
         downTimers[newStack] = 0.2F;
      }

      DevilTriggerRenderer.fillValue = newValue;
      prevFillValue = newValue;
      float stiffness = 220.0F;
      float damping = 14.0F;
      float alphaStiffness = 300.0F;
      float alphaDamping = 15.0F;
      float colorStiffness = 280.0F;
      float colorDamping = 14.0F;
      float[] targetRGB = new float[3];

      for (int i = 0; i < 10; i++) {
         float targetScale = DevilTriggerRenderer.fillValue > (float)i ? 1.0F : 0.0F;
         float cellFill = DevilTriggerRenderer.fillValue - (float)i;
         float targetAlpha = targetScale > 0.0F ? targetAlpha(cellFill) : 0.0F;
         targetColor(cellFill, targetRGB);
         float tR;
         float tG;
         float tB;
         if (targetScale <= 0.0F) {
            tR = 0.0F;
            tG = 0.0F;
            tB = 0.0F;
         } else if (doppelMode == 0) {
            tR = targetRGB[0] * 0.3F;
            tG = targetRGB[1] * 0.5F;
            tB = 1.0F;
         } else if (doppelMode == 2) {
            tR = 1.0F;
            tG = targetRGB[1] * 0.3F;
            tB = targetRGB[2] * 0.3F;
         } else {
            tR = targetRGB[0];
            tG = targetRGB[1];
            tB = targetRGB[2];
         }

         if (popTimers[i] > 0.0F) {
            float boostStrength = popTimers[i] / 0.3F;
            cellVelocities[i] = cellVelocities[i] + 180.0F * boostStrength * deltaTime;
            cellAlphaVelocities[i] = cellAlphaVelocities[i] + 60.0F * boostStrength * deltaTime;
            cellColorVelR[i] = cellColorVelR[i] + 40.0F * boostStrength * deltaTime;
            cellColorVelG[i] = cellColorVelG[i] + 40.0F * boostStrength * deltaTime;
            cellColorVelB[i] = cellColorVelB[i] + 40.0F * boostStrength * deltaTime;
            popTimers[i] = popTimers[i] - deltaTime;
         }

         if (downTimers[i] > 0.0F) {
            float boostStrength = downTimers[i] / 0.2F;
            cellVelocities[i] = cellVelocities[i] + -120.0F * boostStrength * deltaTime;
            cellAlphaVelocities[i] = cellAlphaVelocities[i] + -50.0F * boostStrength * deltaTime;
            downTimers[i] = downTimers[i] - deltaTime;
         }

         float displacement = cellScales[i] - targetScale;
         float springForce = -stiffness * displacement;
         float damperForce = -damping * cellVelocities[i];
         float acceleration = springForce + damperForce;
         cellVelocities[i] = cellVelocities[i] + acceleration * deltaTime;
         cellScales[i] = cellScales[i] + cellVelocities[i] * deltaTime;
         cellScales[i] = Mth.m_14036_(cellScales[i], 0.0F, 1.8F);
         float alphaDisp = cellAlphas[i] - targetAlpha;
         float alphaSpring = -alphaStiffness * alphaDisp;
         float alphaDamp = -alphaDamping * cellAlphaVelocities[i];
         float alphaAccel = alphaSpring + alphaDamp;
         cellAlphaVelocities[i] = cellAlphaVelocities[i] + alphaAccel * deltaTime;
         cellAlphas[i] = cellAlphas[i] + cellAlphaVelocities[i] * deltaTime;
         cellAlphas[i] = Mth.m_14036_(cellAlphas[i], 0.0F, 1.2F);
         float dr = cellColorsR[i] - tR;
         float dg = cellColorsG[i] - tG;
         float db = cellColorsB[i] - tB;
         float sr = -colorStiffness * dr - colorDamping * cellColorVelR[i];
         float sg = -colorStiffness * dg - colorDamping * cellColorVelG[i];
         float sb = -colorStiffness * db - colorDamping * cellColorVelB[i];
         cellColorVelR[i] = cellColorVelR[i] + sr * deltaTime;
         cellColorVelG[i] = cellColorVelG[i] + sg * deltaTime;
         cellColorVelB[i] = cellColorVelB[i] + sb * deltaTime;
         cellColorsR[i] = cellColorsR[i] + cellColorVelR[i] * deltaTime;
         cellColorsG[i] = cellColorsG[i] + cellColorVelG[i] * deltaTime;
         cellColorsB[i] = cellColorsB[i] + cellColorVelB[i] * deltaTime;
         cellColorsR[i] = Mth.m_14036_(cellColorsR[i], 0.0F, 1.2F);
         cellColorsG[i] = Mth.m_14036_(cellColorsG[i], 0.0F, 1.2F);
         cellColorsB[i] = Mth.m_14036_(cellColorsB[i], 0.0F, 1.2F);
      }
   }

   public static void reset() {
      for (int i = 0; i < 10; i++) {
         cellScales[i] = 0.0F;
         cellVelocities[i] = 0.0F;
         cellAlphas[i] = 0.0F;
         cellAlphaVelocities[i] = 0.0F;
         cellColorsR[i] = 0.0F;
         cellColorsG[i] = 0.0F;
         cellColorsB[i] = 0.0F;
         cellColorVelR[i] = 0.0F;
         cellColorVelG[i] = 0.0F;
         cellColorVelB[i] = 0.0F;
         popTimers[i] = 0.0F;
         downTimers[i] = 0.0F;
      }

      prevFillValue = 0.0F;
   }

   public static void render(GuiGraphics g, int xOffset, int yOffset, int width, int height, float layoutScale) {
      for (int i = 0; i < 10; i++) {
         float cellScale = cellScales[i];
         if (!(cellScale <= 0.001F)) {
            float rawCenterX = 103.0F + (float)i * 9.0F + (i >= 3 ? 1.0F : 0.0F);
            float rawCenterY = i < 3 ? 53.0F : 51.0F;
            float centerX = (float)xOffset + rawCenterX * layoutScale;
            float centerY = (float)yOffset + rawCenterY * layoutScale;
            g.m_280168_().m_85836_();
            g.m_280168_().m_252880_(centerX, centerY, 0.0F);
            g.m_280168_().m_85841_(cellScale, cellScale, 1.0F);
            g.m_280168_().m_252880_(-centerX, -centerY, 0.0F);
            g.m_280246_(cellColorsR[i], cellColorsG[i], cellColorsB[i], cellAlphas[i]);
            g.m_280411_(TEXTURE_CELLS[i], xOffset, yOffset, width, height, 0.0F, 0.0F, 300, 110, 512, 512);
            g.m_280168_().m_85849_();
         }
      }

      g.m_280246_(1.0F, 1.0F, 1.0F, 1.0F);
   }

   static {
      for (int i = 0; i < 10; i++) {
         TEXTURE_CELLS[i] = ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/gui/hud/vergil_dt/vergil_dt_" + (i + 1) + ".png");
      }

      cellScales = new float[10];
      cellVelocities = new float[10];
      cellAlphas = new float[10];
      cellAlphaVelocities = new float[10];
      cellColorsR = new float[10];
      cellColorsG = new float[10];
      cellColorsB = new float[10];
      cellColorVelR = new float[10];
      cellColorVelG = new float[10];
      cellColorVelB = new float[10];
      fillValue = 0.0F;
      prevFillValue = 0.0F;
      doppelMode = -1;
      popTimers = new float[10];
      downTimers = new float[10];
   }
}
