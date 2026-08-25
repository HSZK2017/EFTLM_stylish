package com.dmc.invincible_dmc.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class VergilArtFX {
   private final VergilArtFX.Config config;
   private final List<VergilArtFX.EnergyWisp> wisps = new ArrayList<>();
   private final RandomSource random = RandomSource.m_216327_();
   private int lastX = -1;
   private int lastY = -1;
   private int lastW = -1;
   private int lastH = -1;

   public VergilArtFX(VergilArtFX.Config config) {
      this.config = config;
   }

   public void regenerate(int x, int y, int w, int h) {
      if (x != this.lastX || y != this.lastY || w != this.lastW || h != this.lastH) {
         this.lastX = x;
         this.lastY = y;
         this.lastW = w;
         this.lastH = h;
         this.wisps.clear();
         this.random.m_188584_((long)x * 131L + (long)y * 97L + (long)w * 43L + (long)h * 29L + 370530208076L);
         int margin = 3;

         for (int i = 0; i < this.config.wispCount; i++) {
            int side = this.random.m_188503_(4);
            float wx;
            float wy;
            switch (side) {
               case 0:
                  wx = (float)(x - margin) + this.random.m_188501_() * 4.0F;
                  wy = (float)y + this.random.m_188501_() * (float)h;
                  break;
               case 1:
                  wx = (float)(x + w + margin) - this.random.m_188501_() * 4.0F;
                  wy = (float)y + this.random.m_188501_() * (float)h;
                  break;
               case 2:
                  wx = (float)x + this.random.m_188501_() * (float)w;
                  wy = (float)(y - margin) + this.random.m_188501_() * 4.0F;
                  break;
               default:
                  wx = (float)x + this.random.m_188501_() * (float)w;
                  wy = (float)(y + h + margin) - this.random.m_188501_() * 4.0F;
            }

            float phase = this.random.m_188501_() * (float) (Math.PI * 2);
            float speed = 0.015F + this.random.m_188501_() * 0.03F;
            float ampX = 2.0F + this.random.m_188501_() * 4.0F;
            float ampY = 2.0F + this.random.m_188501_() * 5.0F;
            float size = 0.6F + this.random.m_188501_() * 1.2F;
            this.wisps.add(new VergilArtFX.EnergyWisp(wx, wy, phase, speed, ampX, ampY, size));
         }
      }
   }

   public void render(GuiGraphics g, float partialTick) {
      float time = getTime(partialTick);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableDepthTest();
      this.renderScanLines(g, time);
      this.renderShadowTendrils(g, time);
      this.renderWisps(g, time);
      this.renderSpiritRing(g, time);
      this.renderPulseAura(g, time);
      this.renderCornerOrbs(g, time);
      this.renderDemonicVeins(g, time);
      this.renderGaugeDots(g, time);
      this.renderCornerSlashes(g, time);
      this.renderVMotifs(g, time);
      this.renderFlashSlash(g, time);
      this.renderEnergySurge(g, time);
      RenderSystem.enableDepthTest();
      RenderSystem.disableBlend();
   }

   private void renderWisps(GuiGraphics g, float time) {
      for (VergilArtFX.EnergyWisp wisp : this.wisps) {
         wisp.update(time);
         if (!(wisp.alpha < 0.08F)) {
            int a = (int)(wisp.alpha * 200.0F);
            int color = a << 24 | this.config.wispColor & 16777215;
            float s = wisp.size * (0.8F + 0.4F * wisp.alpha);
            float outerA = wisp.alpha * 0.25F;
            int oa = (int)(outerA * 255.0F);
            if (oa > 5) {
               int oc = oa << 24 | this.config.wispColor & 16777215;
               g.m_280509_((int)(wisp.x - s * 2.5F), (int)(wisp.y - s * 2.5F), (int)(wisp.x + s * 2.5F + 1.0F), (int)(wisp.y + s * 2.5F + 1.0F), oc);
            }

            float midA = wisp.alpha * 0.5F;
            int ma = (int)(midA * 255.0F);
            if (ma > 8) {
               int mc = ma << 24 | this.config.wispColor & 16777215;
               g.m_280509_((int)(wisp.x - s * 1.3F), (int)(wisp.y - s * 1.3F), (int)(wisp.x + s * 1.3F + 1.0F), (int)(wisp.y + s * 1.3F + 1.0F), mc);
            }

            g.m_280509_((int)(wisp.x - s * 0.5F), (int)(wisp.y - s * 0.5F), (int)(wisp.x + s * 0.5F + 1.0F), (int)(wisp.y + s * 0.5F + 1.0F), color);
         }
      }
   }

   private void renderPulseAura(GuiGraphics g, float time) {
      if (this.config.auraColor != 0) {
         float pulse = (Mth.m_14031_(time * 0.04F) + 1.0F) * 0.5F;
         int alpha1 = (int)(24.0F + pulse * 48.0F);
         int c1 = alpha1 << 24 | this.config.auraColor & 16777215;
         int ox = this.lastX - 2;
         int oy = this.lastY - 2;
         int ow = this.lastW + 4;
         int oh = this.lastH + 4;
         drawHollowRect(g, ox, oy, ow, oh, c1);
         float pulse2 = (Mth.m_14031_(time * 0.04F + (float) Math.PI) + 1.0F) * 0.5F;
         int alpha2 = (int)(8.0F + pulse2 * 28.0F);
         int c2 = alpha2 << 24 | this.config.auraColor & 16777215;
         int ox2 = this.lastX - 3;
         int oy2 = this.lastY - 3;
         int ow2 = this.lastW + 6;
         int oh2 = this.lastH + 6;
         drawHollowRect(g, ox2, oy2, ow2, oh2, c2);
      }
   }

   private void renderCornerSlashes(GuiGraphics g, float time) {
      if (this.config.slashColor != 0) {
         float breath = (Mth.m_14031_(time * 0.03F) + 1.0F) * 0.5F;
         int alpha = (int)(85.0F + breath * 85.0F);
         int color = Math.min(alpha, 255) << 24 | this.config.slashColor & 16777215;
         int len = 12;
         int gap = 4;
         int x = this.lastX;
         int y = this.lastY;
         int w = this.lastW;
         int h = this.lastH;

         for (int i = 0; i < 3; i++) {
            int ox = x + 3;
            int oy = y + 3;
            drawSlashLine(g, ox + i * gap, oy + i * gap, ox + i * gap + len, oy + i * gap + len, color);
         }

         for (int i = 0; i < 3; i++) {
            int ox = x + w - 3;
            int oy = y + 3;
            drawSlashLine(g, ox - i * gap, oy + i * gap, ox - i * gap - len, oy + i * gap + len, color);
         }

         for (int i = 0; i < 3; i++) {
            int ox = x + 3;
            int oy = y + h - 3;
            drawSlashLine(g, ox + i * gap, oy - i * gap, ox + i * gap + len, oy - i * gap - len, color);
         }

         for (int i = 0; i < 3; i++) {
            int ox = x + w - 3;
            int oy = y + h - 3;
            drawSlashLine(g, ox - i * gap, oy - i * gap, ox - i * gap - len, oy - i * gap - len, color);
         }
      }
   }

   private static void drawSlashLine(GuiGraphics g, int x1, int y1, int x2, int y2, int color) {
      int dx = Math.abs(x2 - x1);
      int dy = -Math.abs(y2 - y1);
      int sx = x1 < x2 ? 1 : -1;
      int sy = y1 < y2 ? 1 : -1;
      int err = dx + dy;
      int x = x1;
      int y = y1;

      for (int i = 0; i < Math.max(dx, -dy) + 1 && i < 20; i++) {
         g.m_280509_(x, y, x + 1, y + 1, color);
         if (x == x2 && y == y2) {
            break;
         }

         int e2 = 2 * err;
         if (e2 >= dy) {
            err += dy;
            x += sx;
         }

         if (e2 <= dx) {
            err += dx;
            y += sy;
         }
      }
   }

   private void renderVMotifs(GuiGraphics g, float time) {
      if (this.config.motifColor != 0) {
         float breath = (Mth.m_14031_(time * 0.035F + 1.0F) + 1.0F) * 0.5F;
         int alpha = (int)(48.0F + breath * 64.0F);
         int color = alpha << 24 | this.config.motifColor & 16777215;
         int mx = this.lastX + this.lastW / 2;
         int my = this.lastY + this.lastH / 2;
         drawVMotif(g, mx, this.lastY + 1, 0, 1, 3, color);
         drawVMotif(g, mx, this.lastY + this.lastH - 1, 0, -1, 3, color);
         drawVMotif(g, this.lastX + 1, my, 1, 0, 3, color);
         drawVMotif(g, this.lastX + this.lastW - 1, my, -1, 0, 3, color);
      }
   }

   private static void drawVMotif(GuiGraphics g, int cx, int cy, int dx, int dy, int size, int color) {
      if (dx != 0) {
         int dir = dx > 0 ? 1 : -1;

         for (int i = 0; i < size; i++) {
            g.m_280509_(cx + dir * i, cy - i, cx + dir * i + 1, cy - i + 1, color);
            g.m_280509_(cx + dir * i, cy + i, cx + dir * i + 1, cy + i + 1, color);
         }
      } else {
         int dir = dy > 0 ? 1 : -1;

         for (int i = 0; i < size; i++) {
            g.m_280509_(cx - i, cy + dir * i, cx - i + 1, cy + dir * i + 1, color);
            g.m_280509_(cx + i, cy + dir * i, cx + i + 1, cy + dir * i + 1, color);
         }
      }
   }

   private void renderScanLines(GuiGraphics g, float time) {
      if (this.config.scanlineColor != 0) {
         int lineSpacing = 6;
         float t0 = time * 0.5F;

         for (int ly = this.lastY; ly < this.lastY + this.lastH; ly += lineSpacing) {
            float posT = (float)(ly - this.lastY) / (float)this.lastH;
            float af = (Mth.m_14031_(posT * 10.0F + t0) + 1.0F) * 0.5F;
            int alpha = (int)(af * 12.0F);
            if (alpha >= 2) {
               int color = alpha << 24 | this.config.scanlineColor & 16777215;
               g.m_280509_(this.lastX + 5, ly, this.lastX + this.lastW - 5, ly + 1, color);
            }
         }
      }
   }

   private void renderSpiritRing(GuiGraphics g, float time) {
      if (this.config.ringColor != 0 && this.config.ringDots != 0) {
         int n = this.config.ringDots;
         float speed = 0.015F;
         float offset = time * speed * (float) (Math.PI * 2);
         int x = this.lastX;
         int y = this.lastY;
         int w = this.lastW;
         int h = this.lastH;
         int rx = x - 4;
         int ry = y - 4;
         int rw = w + 8;
         int rh = h + 8;

         for (int i = 0; i < n; i++) {
            float t = (float)i / (float)n;
            float angle = t * (float) (Math.PI * 2) + offset;
            float px = 0.0F;
            float py = 0.0F;
            float perimeter = (float)(2 * (rw + rh));
            float dist = t * perimeter;
            if (dist < (float)rw) {
               px = (float)rx + dist;
               py = (float)ry;
            } else if (dist < (float)(rw + rh)) {
               px = (float)(rx + rw);
               py = (float)ry + (dist - (float)rw);
            } else if (dist < (float)(2 * rw + rh)) {
               px = (float)(rx + rw) - (dist - (float)rw - (float)rh);
               py = (float)(ry + rh);
            } else {
               px = (float)rx;
               py = (float)(ry + rh) - (dist - (float)(2 * rw) - (float)rh);
            }

            float dotPhase = angle % (float) (Math.PI * 2);
            float brightness = (Mth.m_14031_(dotPhase * 2.0F) + 1.0F) * 0.3F + 0.4F;
            int alpha = (int)(brightness * 204.0F);
            if (alpha >= 12) {
               int color = Math.min(alpha, 255) << 24 | this.config.ringColor & 16777215;
               int dotSize = (int)(1.5F + brightness * 1.5F);
               if (brightness > 0.6F) {
                  int glowA = (int)(brightness * 48.0F);
                  int glowC = glowA << 24 | this.config.ringColor & 16777215;
                  g.m_280509_((int)px - 2, (int)py - 2, (int)px + 3, (int)py + 3, glowC);
               }

               g.m_280509_((int)px, (int)py, (int)px + dotSize, (int)py + dotSize, color);
            }
         }
      }
   }

   private void renderCornerOrbs(GuiGraphics g, float time) {
      if (this.config.orbColor != 0) {
         float pulse = (Mth.m_14031_(time * 0.06F) + 1.0F) * 0.5F;
         int size = 4 + (int)(pulse * 3.0F);
         int x = this.lastX;
         int y = this.lastY;
         int w = this.lastW;
         int h = this.lastH;
         int[][] corners = new int[][]{{x, y}, {x + w, y}, {x, y + h}, {x + w, y + h}};

         for (int[] c : corners) {
            int cx = c[0];
            int cy = c[1];
            int outerA = (int)(16.0F + pulse * 24.0F);
            int outerC = outerA << 24 | this.config.orbColor & 16777215;
            g.m_280509_(cx - size - 2, cy - size - 2, cx + size + 3, cy + size + 3, outerC);
            int midA = (int)(48.0F + pulse * 48.0F);
            int midC = midA << 24 | this.config.orbColor & 16777215;
            g.m_280509_(cx - size, cy - size, cx + size + 1, cy + size + 1, midC);
            int coreA = (int)(170.0F + pulse * 85.0F);
            int coreSize = Math.max(1, size / 2);
            int coreC = Math.min(coreA, 255) << 24 | 16777215;
            g.m_280509_(cx - coreSize, cy - coreSize, cx + coreSize + 1, cy + coreSize + 1, coreC);
         }
      }
   }

   private void renderGaugeDots(GuiGraphics g, float time) {
      if (this.config.gaugeColor != 0 && this.config.gaugeDots != 0) {
         int n = this.config.gaugeDots;
         int x = this.lastX;
         int y = this.lastY;
         int w = this.lastW;
         int startX = x + 6;
         int endX = x + w - 6;
         int spacing = Math.max(4, (endX - startX) / Math.max(1, n - 1));
         int dotY = y - 2;
         float cycleSpeed = 0.06F;

         for (int i = 0; i < n; i++) {
            float individualPhase = time * cycleSpeed + (float)i / (float)n * (float) (Math.PI * 2) * 0.5F;
            float brightness = (Mth.m_14031_(individualPhase) + 1.0F) * 0.5F;
            brightness = brightness < 0.3F ? brightness * 0.3F : (brightness - 0.3F) / 0.7F;
            int alpha = (int)(brightness * 221.0F);
            if (alpha >= 15) {
               int dotX = startX + i * spacing;
               if (dotX > endX) {
                  dotX = endX;
               }

               int color = Math.min(alpha, 255) << 24 | this.config.gaugeColor & 16777215;
               int ds = brightness > 0.7F ? 2 : 1;
               if (brightness > 0.65F) {
                  int ga = (int)(brightness * 64.0F);
                  int gc = ga << 24 | this.config.gaugeColor & 16777215;
                  g.m_280509_(dotX - 2, dotY - 2, dotX + 3, dotY + 3, gc);
               }

               g.m_280509_(dotX, dotY, dotX + ds, dotY + ds, color);
            }
         }
      }
   }

   private void renderFlashSlash(GuiGraphics g, float time) {
      if (this.config.flashColor != 0 && this.lastW > 0) {
         float flashPhase = time * 0.007F % 1.0F;
         if (!(flashPhase > 0.15F)) {
            float brightness;
            if (flashPhase < 0.05F) {
               brightness = flashPhase / 0.05F;
            } else if (flashPhase < 0.1F) {
               brightness = 1.0F;
            } else {
               brightness = 1.0F - (flashPhase - 0.1F) / 0.05F;
            }

            int alpha = (int)(brightness * 102.0F);
            if (alpha >= 5) {
               float linePos = (float)this.lastY + (float)this.lastH * (0.1F + flashPhase * 5.0F);
               if (linePos > (float)(this.lastY + this.lastH)) {
                  linePos = (float)(this.lastY + this.lastH);
               }

               int color = alpha << 24 | this.config.flashColor & 16777215;
               int margin = 4;
               g.m_280509_(this.lastX + margin, (int)linePos, this.lastX + this.lastW - margin, (int)linePos + 1, color);
               if (brightness > 0.5F) {
                  int edgeA = (int)(brightness * 68.0F);
                  int edgeC = edgeA << 24 | this.config.flashColor & 16777215;
                  g.m_280509_(this.lastX, (int)linePos, this.lastX + this.lastW, (int)linePos + 1, edgeC);
               }
            }
         }
      }
   }

   private void renderDemonicVeins(GuiGraphics g, float time) {
      if (this.config.demonicVeinColor != 0 && this.config.demonicVeinCount != 0) {
         int n = this.config.demonicVeinCount;
         int x = this.lastX;
         int y = this.lastY;
         int w = this.lastW;
         int h = this.lastH;
         int veinColor = this.config.demonicVeinColor & 16777215;

         for (int i = 0; i < n; i++) {
            int corner = i % 4;
            int originX;
            int originY;
            int dirX;
            int dirY;
            switch (corner) {
               case 0:
                  originX = x;
                  originY = y;
                  dirX = 1;
                  dirY = 1;
                  break;
               case 1:
                  originX = x + w;
                  originY = y;
                  dirX = -1;
                  dirY = 1;
                  break;
               case 2:
                  originX = x;
                  originY = y + h;
                  dirX = 1;
                  dirY = -1;
                  break;
               default:
                  originX = x + w;
                  originY = y + h;
                  dirX = -1;
                  dirY = -1;
            }

            float seed = (float)i * 1.7F + (float)corner * 0.3F;
            float pulse = (Mth.m_14031_(time * 0.045F + seed * 2.1F) + 1.0F) * 0.5F;
            int alpha = (int)(48.0F + pulse * 80.0F);
            if (alpha >= 12) {
               int color = Math.min(alpha, 255) << 24 | veinColor;
               int maxLen = Math.min(w, h) / 3 + i * 4;
               int len = maxLen / 2 + (int)(pulse * (float)maxLen / 2.0F);

               for (int step = 0; step < len; step += 2) {
                  int jitter = step % 6 < 3 ? 1 : -1;
                  int curX;
                  int curY;
                  if (corner < 2) {
                     curX = originX + dirX * step;
                     curY = originY + dirY * (step / 3) + jitter * (step / 2 % 2);
                  } else {
                     curX = originX + dirX * step;
                     curY = originY + dirY * (step / 3) - jitter * (step / 2 % 2);
                  }

                  if (curX >= x && curX <= x + w && curY >= y && curY <= y + h) {
                     int glowA = (int)((float)alpha * 0.3F);
                     int glowC = glowA << 24 | veinColor;
                     g.m_280509_(curX - 1, curY - 1, curX + 2, curY + 2, glowC);
                     g.m_280509_(curX, curY, curX + 1, curY + 1, color);
                  }
               }
            }
         }
      }
   }

   private void renderShadowTendrils(GuiGraphics g, float time) {
      if (this.config.shadowTendrilColor != 0 && this.config.shadowTendrilCount != 0) {
         int n = this.config.shadowTendrilCount;
         int x = this.lastX;
         int y = this.lastY;
         int w = this.lastW;
         int h = this.lastH;
         int tendrilRgb = this.config.shadowTendrilColor & 16777215;

         for (int i = 0; i < n; i++) {
            float seed = (float)i * 0.77F;
            float pos = ((float)i / (float)n + time * 0.008F * (1.0F + (float)(i % 3) * 0.3F)) % 1.0F;
            float perimeter = (float)(2 * (w + h));
            float dist = pos * perimeter;
            float px;
            float py;
            if (dist < (float)w) {
               px = (float)x + dist;
               py = (float)(y - 2);
            } else if (dist < (float)(w + h)) {
               px = (float)(x + w + 2);
               py = (float)y + (dist - (float)w);
            } else if (dist < (float)(2 * w + h)) {
               px = (float)(x + w) - (dist - (float)w - (float)h);
               py = (float)(y + h + 2);
            } else {
               px = (float)(x - 2);
               py = (float)(y + h) - (dist - (float)(2 * w) - (float)h);
            }

            float wave = Mth.m_14031_(time * 0.06F + seed * 3.0F) * 4.0F;
            float tendrilLen = 6.0F + (Mth.m_14031_(time * 0.04F + seed * 5.0F) + 1.0F) * 8.0F;
            int alpha = (int)(24.0F + (Mth.m_14031_(time * 0.05F + seed * 2.0F) + 1.0F) * 32.0F);
            if (alpha >= 6) {
               int color = Math.min(alpha, 255) << 24 | tendrilRgb;
               int steps = (int)(tendrilLen / 1.5F);
               float tx = px;
               float ty = py;

               for (int s = 0; s < steps; s++) {
                  float t = (float)s / (float)steps;
                  float sway = Mth.m_14031_(t * 3.0F + time * 0.07F + seed) * wave * (1.0F - t);
                  tx += sway * 0.4F;
                  if (px < (float)x) {
                     tx += 0.8F;
                  } else if (px > (float)(x + w)) {
                     tx -= 0.8F;
                  }

                  if (py < (float)y) {
                     ty += 0.8F;
                  } else if (py > (float)(y + h)) {
                     ty -= 0.8F;
                  }

                  int ta = (int)((float)alpha * (1.0F - t * 0.7F));
                  if (ta > 4) {
                     int tc = ta << 24 | tendrilRgb;
                     g.m_280509_((int)tx, (int)ty, (int)tx + 1, (int)ty + 1, tc);
                  }
               }
            }
         }
      }
   }

   private void renderEnergySurge(GuiGraphics g, float time) {
      if (this.config.energySurgeColor != 0) {
         int x = this.lastX;
         int y = this.lastY;
         int w = this.lastW;
         int h = this.lastH;
         int surgeRgb = this.config.energySurgeColor & 16777215;
         float mainPhase = time * 0.01F % 1.0F;
         float secondaryPhase = (time * 0.015F + 0.5F) % 1.0F;
         this.renderSurgeWave(g, x, y, w, h, surgeRgb, mainPhase, 1.0F, time);
         this.renderSurgeWave(g, x, y, w, h, surgeRgb, secondaryPhase, 0.5F, time);
      }
   }

   private void renderSurgeWave(GuiGraphics g, int x, int y, int w, int h, int rgb, float phase, float intensity, float time) {
      if (!(phase > 0.4F)) {
         float brightness;
         if (phase < 0.08F) {
            brightness = phase / 0.08F;
         } else if (phase < 0.15F) {
            brightness = 1.0F;
         } else if (phase < 0.3F) {
            brightness = 1.0F - (phase - 0.15F) / 0.15F;
         } else {
            brightness = Math.max(0.0F, 1.0F - (phase - 0.3F) / 0.1F) * 0.3F;
         }

         brightness *= intensity;
         int alpha = (int)(brightness * 85.0F);
         if (alpha >= 8) {
            float diagLen = (float)Math.sqrt((double)(w * w + h * h));
            float wavePos = phase * 2.5F * diagLen - diagLen * 0.3F;
            int bandWidth = 4 + (int)(brightness * 8.0F);

            for (int d = -bandWidth; d <= bandWidth; d++) {
               float distAlpha = 1.0F - Math.abs((float)d / (float)bandWidth);
               int bandA = (int)((float)alpha * distAlpha * distAlpha);
               if (bandA >= 4) {
                  int color = Math.min(bandA, 255) << 24 | rgb;
                  int steps = Math.max(w, h) * 2;

                  for (int s = 0; s < steps; s++) {
                     float t = (float)s / (float)steps;
                     int sx = x + (int)(t * (float)w);
                     int sy = y + (int)(t * (float)h);
                     float offset = (float)s - wavePos + (float)d;
                     if (!(Math.abs(offset) > (float)(bandWidth * 2))) {
                        float angle = (float)Math.atan2((double)h, (double)w);
                        float perpX = -Mth.m_14031_(angle) * offset;
                        float perpY = Mth.m_14089_(angle) * offset;
                        int px = (int)((float)sx + perpX);
                        int py = (int)((float)sy + perpY);
                        if (px >= x - 2 && px <= x + w + 2 && py >= y - 2 && py <= y + h + 2) {
                           g.m_280509_(px, py, px + 1, py + 1, color);
                        }
                     }
                  }
               }
            }

            if (brightness > 0.7F) {
               int flashA = (int)((brightness - 0.7F) / 0.3F * 136.0F);
               int flashC = flashA << 24 | 16777215;
               int[][] corners = new int[][]{{x, y}, {x + w, y}, {x, y + h}, {x + w, y + h}};

               for (int[] c : corners) {
                  int flashSize = 3 + (int)(brightness * 4.0F);
                  g.m_280509_(c[0] - flashSize, c[1] - flashSize, c[0] + flashSize + 1, c[1] + flashSize + 1, flashC);
               }
            }
         }
      }
   }

   private static void drawHollowRect(GuiGraphics g, int x, int y, int w, int h, int color) {
      g.m_280509_(x, y, x + w, y + 1, color);
      g.m_280509_(x, y + h - 1, x + w, y + h, color);
      g.m_280509_(x, y, x + 1, y + h, color);
      g.m_280509_(x + w - 1, y, x + w, y + h, color);
   }

   private static float getTime(float partialTick) {
      long gameTime = Minecraft.m_91087_().f_91073_ != null ? Minecraft.m_91087_().f_91073_.m_46467_() : System.currentTimeMillis() / 50L;
      return (float)gameTime + partialTick;
   }

   public static class Config {
      public int wispColor = -12285697;
      public int wispCount = 8;
      public int auraColor = -12285748;
      public int slashColor = -1436116481;
      public int motifColor = -2004304897;
      public int scanlineColor = 184549375;
      public int ringColor = -7816193;
      public int ringDots = 16;
      public int orbColor = -7811841;
      public int gaugeColor = -5579265;
      public int flashColor = -1;
      public int gaugeDots = 7;
      public int demonicVeinColor = 0;
      public int demonicVeinCount = 0;
      public int energySurgeColor = 0;
      public int shadowTendrilColor = 0;
      public int shadowTendrilCount = 0;

      public static VergilArtFX.Config dmc4() {
         VergilArtFX.Config c = new VergilArtFX.Config();
         c.wispColor = -12285697;
         c.auraColor = -12285748;
         c.slashColor = -1436116481;
         c.motifColor = -2004304897;
         c.scanlineColor = 184549375;
         c.ringColor = -10053121;
         c.ringDots = 14;
         c.orbColor = -10053121;
         c.gaugeColor = -7816193;
         c.flashColor = -1;
         c.gaugeDots = 7;
         c.wispCount = 10;
         return c;
      }

      public static VergilArtFX.Config dmc5() {
         VergilArtFX.Config c = new VergilArtFX.Config();
         c.wispColor = -7811841;
         c.auraColor = -5579265;
         c.slashColor = -1432761089;
         c.motifColor = -1999839745;
         c.scanlineColor = 218103807;
         c.ringColor = -6697729;
         c.ringDots = 18;
         c.orbColor = -5579265;
         c.gaugeColor = -3351041;
         c.flashColor = -1;
         c.gaugeDots = 9;
         c.wispCount = 12;
         return c;
      }

      public static VergilArtFX.Config dmc5bd() {
         VergilArtFX.Config c = new VergilArtFX.Config();
         c.wispColor = -4495617;
         c.auraColor = -6732596;
         c.slashColor = -1429436161;
         c.motifColor = -1999861505;
         c.scanlineColor = 251645183;
         c.ringColor = -3372801;
         c.ringDots = 20;
         c.orbColor = -3372801;
         c.gaugeColor = -2249985;
         c.flashColor = -1;
         c.gaugeDots = 10;
         c.wispCount = 14;
         return c;
      }

      public static VergilArtFX.Config dmc5sdt() {
         VergilArtFX.Config c = new VergilArtFX.Config();
         c.wispColor = -6697729;
         c.wispCount = 16;
         c.auraColor = -7816193;
         c.slashColor = -1430532865;
         c.motifColor = -2002067969;
         c.scanlineColor = 268435455;
         c.ringColor = -5583617;
         c.ringDots = 22;
         c.orbColor = -5579265;
         c.gaugeColor = -3346689;
         c.flashColor = -1;
         c.gaugeDots = 11;
         c.demonicVeinColor = -10074966;
         c.demonicVeinCount = 6;
         c.energySurgeColor = -11171636;
         c.shadowTendrilColor = -13430426;
         c.shadowTendrilCount = 8;
         return c;
      }
   }

   public static class EnergyWisp {
      float x;
      float y;
      float baseX;
      float baseY;
      float phaseOffset;
      float driftSpeed;
      float driftAmpX;
      float driftAmpY;
      float size;
      float alpha;

      EnergyWisp(float x, float y, float phaseOffset, float driftSpeed, float driftAmpX, float driftAmpY, float size) {
         this.x = this.baseX = x;
         this.y = this.baseY = y;
         this.phaseOffset = phaseOffset;
         this.driftSpeed = driftSpeed;
         this.driftAmpX = driftAmpX;
         this.driftAmpY = driftAmpY;
         this.size = size;
      }

      void update(float time) {
         float phase = time * this.driftSpeed + this.phaseOffset;
         this.x = this.baseX + Mth.m_14031_(phase * 0.7F) * this.driftAmpX;
         this.y = this.baseY + Mth.m_14089_(phase * 1.1F) * this.driftAmpY * 0.5F - Mth.m_14031_(phase * 0.3F) * this.driftAmpY;
         this.alpha = (Mth.m_14031_(phase * 1.3F) + 1.0F) * 0.5F;
         this.alpha = this.alpha * this.alpha;
      }
   }
}
