package com.dmc.invincible_dmc.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class TooltipStarFX {
   private final List<TooltipStarFX.Star> stars = new ArrayList<>();
   private final RandomSource random = RandomSource.m_216327_();
   private final int starColor;
   private final int starDensity;

   public TooltipStarFX(int starColor, int starDensity) {
      this.starColor = starColor;
      this.starDensity = starDensity;
   }

   public void regenerate(int x, int y, int width, int height) {
      this.stars.clear();
      this.random.m_188584_((long)x * 31L + (long)y * 17L + (long)width * 13L + (long)height * 7L);
      int margin = 5;
      int sx = x - margin;
      int sy = y - margin;
      int sw = width + margin * 2;
      int sh = height + margin * 2;

      for (int i = 0; i < this.starDensity; i++) {
         int starX = sx + this.random.m_188503_(sw);
         int starY = sy + this.random.m_188503_(sh);
         float phase = this.random.m_188501_() * (float) (Math.PI * 2);
         float speed = 0.015F + this.random.m_188501_() * 0.05F;
         float baseSize = 0.3F + this.random.m_188501_() * 1.2F;
         int typeRoll = this.random.m_188503_(100);
         int type;
         if (typeRoll < 60) {
            type = 0;
         } else if (typeRoll < 85) {
            type = 1;
         } else {
            type = 2;
         }

         boolean warmShift = this.random.m_188503_(100) < 25;
         this.stars.add(new TooltipStarFX.Star(starX, starY, phase, speed, baseSize, type, warmShift));
      }
   }

   public void render(GuiGraphics g, float partialTick) {
      long gameTime = Minecraft.m_91087_().f_91073_ != null ? Minecraft.m_91087_().f_91073_.m_46467_() : System.currentTimeMillis() / 50L;
      float time = (float)gameTime + partialTick;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableDepthTest();

      for (TooltipStarFX.Star star : this.stars) {
         float phase = (time * star.speed + star.phaseOffset) % (float) (Math.PI * 2);
         float raw = (Mth.m_14031_(phase) + 1.0F) * 0.5F;
         float alpha = raw * raw;
         alpha = (float)Math.pow((double)alpha, 1.5);
         if (star.type == 2) {
            float flareBoost = (Mth.m_14031_(phase * 3.0F) + 1.0F) * 0.5F;
            flareBoost *= flareBoost;
            if (flareBoost > 0.85F) {
               alpha = Math.min(1.0F, alpha + flareBoost * 0.5F);
            }
         }

         float size = star.baseSize * (0.5F + 0.5F * alpha);
         int a = (int)(alpha * 255.0F);
         if (a >= 8) {
            int baseRgb = this.starColor & 16777215;
            int r = baseRgb >> 16 & 0xFF;
            int gv = baseRgb >> 8 & 0xFF;
            int b = baseRgb & 0xFF;
            if (star.warmShift) {
               r = Math.min(255, r + 50);
               gv = Math.min(255, gv + 40);
            }

            int c = a << 24 | r << 16 | gv << 8 | b;
            switch (star.type) {
               case 0:
                  this.drawCrossStar(g, (float)star.x, (float)star.y, size, c, alpha);
                  break;
               case 1:
                  this.drawDiamondStar(g, (float)star.x, (float)star.y, size, c, alpha);
                  break;
               case 2:
                  this.drawFlareStar(g, (float)star.x, (float)star.y, size, c, alpha);
            }
         }
      }

      RenderSystem.enableDepthTest();
      RenderSystem.disableBlend();
   }

   private void drawCrossStar(GuiGraphics g, float cx, float cy, float size, int color, float alpha) {
      int a = color >> 24 & 0xFF;
      int rgb = color & 16777215;
      float hw = size * 2.5F;
      float hh = size * 0.55F;
      g.m_280509_((int)(cx - hw), (int)(cy - hh), (int)(cx + hw + 1.0F), (int)(cy + hh + 1.0F), color);
      float vw = size * 0.55F;
      float vh = size * 2.5F;
      g.m_280509_((int)(cx - vw), (int)(cy - vh), (int)(cx + vw + 1.0F), (int)(cy + vh + 1.0F), color);
      if (size > 0.6F && alpha > 0.5F) {
         int diagA = (int)((float)a * 0.5F);
         int diagC = diagA << 24 | rgb;
         float dl = size * 1.6F;

         for (int i = -((int)dl); i <= (int)dl; i++) {
            g.m_280509_((int)(cx + (float)i), (int)(cy + (float)i), (int)(cx + (float)i + 1.0F), (int)(cy + (float)i + 1.0F), diagC);
         }

         for (int i = -((int)dl); i <= (int)dl; i++) {
            g.m_280509_((int)(cx + (float)i), (int)(cy - (float)i), (int)(cx + (float)i + 1.0F), (int)(cy - (float)i + 1.0F), diagC);
         }
      }

      float core = size * 0.6F;
      int brightA = Math.min(255, a + 80);
      int brightC = brightA << 24 | 16777215;
      g.m_280509_((int)(cx - core), (int)(cy - core), (int)(cx + core + 1.0F), (int)(cy + core + 1.0F), brightC);
   }

   private void drawDiamondStar(GuiGraphics g, float cx, float cy, float size, int color, float alpha) {
      int a = color >> 24 & 0xFF;
      int rgb = color & 16777215;
      float d = size * 1.8F;

      for (int y = -((int)d); y <= (int)d; y++) {
         float rowHalf = d - (float)Math.abs(y);
         if (!(rowHalf < 0.3F)) {
            int rowAlpha = (int)((float)a * (1.0F - (float)Math.abs(y) / d * 0.5F));
            int rowColor = rowAlpha << 24 | rgb;
            int rx1 = (int)(cx - rowHalf);
            int rx2 = (int)(cx + rowHalf + 1.0F);
            g.m_280509_(rx1, (int)(cy + (float)y), rx2, (int)(cy + (float)y + 1.0F), rowColor);
         }
      }

      if (alpha > 0.4F) {
         int bc = Math.min(255, a + 70) << 24 | 16777215;
         g.m_280509_((int)(cx - 1.0F), (int)(cy - 1.0F), (int)(cx + 2.0F), (int)(cy + 2.0F), bc);
      }
   }

   private void drawFlareStar(GuiGraphics g, float cx, float cy, float size, int color, float alpha) {
      int a = color >> 24 & 0xFF;
      int rgb = color & 16777215;
      float spokeLen = size * 3.5F;

      for (int s = 0; s < 6; s++) {
         float angle = (float)s / 6.0F * (float) (Math.PI * 2);
         float dx = Mth.m_14089_(angle);
         float dy = Mth.m_14031_(angle);

         for (int i = 1; i <= (int)spokeLen; i++) {
            float fade = 1.0F - (float)i / spokeLen;
            int sa = (int)((float)a * fade * 0.7F);
            if (sa >= 5) {
               int sc = sa << 24 | rgb;
               int sx = (int)(cx + dx * (float)i);
               int sy = (int)(cy + dy * (float)i);
               g.m_280509_(sx, sy, sx + 1, sy + 1, sc);
            }
         }
      }

      int glowA = (int)((float)a * 0.2F);
      int glowC = glowA << 24 | rgb;
      int glowR = (int)(size * 2.5F);
      g.m_280509_((int)(cx - (float)glowR), (int)(cy - (float)glowR), (int)(cx + (float)glowR + 1.0F), (int)(cy + (float)glowR + 1.0F), glowC);
      int midA = (int)((float)a * 0.45F);
      int midC = midA << 24 | rgb;
      int midR = (int)(size * 1.2F);
      g.m_280509_((int)(cx - (float)midR), (int)(cy - (float)midR), (int)(cx + (float)midR + 1.0F), (int)(cy + (float)midR + 1.0F), midC);
      int coreA = Math.min(255, a + 100);
      g.m_280509_((int)(cx - 1.0F), (int)(cy - 1.0F), (int)(cx + 2.0F), (int)(cy + 2.0F), coreA << 24 | 16777215);
   }

   public boolean hasStars() {
      return !this.stars.isEmpty();
   }

   private static class Star {
      final int x;
      final int y;
      final float phaseOffset;
      final float speed;
      final float baseSize;
      final int type;
      final boolean warmShift;

      Star(int x, int y, float phaseOffset, float speed, float baseSize, int type, boolean warmShift) {
         this.x = x;
         this.y = y;
         this.phaseOffset = phaseOffset;
         this.speed = speed;
         this.baseSize = baseSize;
         this.type = type;
         this.warmShift = warmShift;
      }
   }
}
