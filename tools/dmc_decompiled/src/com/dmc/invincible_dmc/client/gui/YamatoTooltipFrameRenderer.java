package com.dmc.invincible_dmc.client.gui;

import com.dmc.invincible_dmc.item.YamatoItem;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent.Color;
import net.minecraftforge.client.event.RenderTooltipEvent.GatherComponents;
import net.minecraftforge.client.event.RenderTooltipEvent.Pre;

public class YamatoTooltipFrameRenderer {
   private static final ResourceLocation V_TEXTURE = ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/gui/hud/v.png");
   private static final int V_TEX_SIZE = 256;
   private final Map<YamatoTooltipFrameRenderer.YamatoVariant, TooltipStarFX> starFxCache = new HashMap<>();
   private final Map<YamatoTooltipFrameRenderer.YamatoVariant, VergilArtFX> vergilFxCache = new HashMap<>();
   private int lastTooltipX = -1;
   private int lastTooltipY = -1;
   private int lastTooltipW = -1;
   private int lastTooltipH = -1;

   public static YamatoTooltipFrameRenderer.YamatoVariant getVariant(ItemStack stack) {
      if (!(stack.m_41720_() instanceof YamatoItem)) {
         return null;
      } else {
         String path = stack.m_41720_().m_204114_().m_205785_().m_135782_().m_135815_();
         if (path.contains("dmc5_bd") || path.contains("dmc5bd")) {
            return YamatoTooltipFrameRenderer.YamatoVariant.DMC5_BD;
         } else if (path.contains("devil_sword_vergil")) {
            return YamatoTooltipFrameRenderer.YamatoVariant.DMC5_SDT;
         } else if (path.contains("dmc5")) {
            return YamatoTooltipFrameRenderer.YamatoVariant.DMC5;
         } else {
            return path.contains("dmc4") ? YamatoTooltipFrameRenderer.YamatoVariant.DMC4 : null;
         }
      }
   }

   public void onPreRenderTooltip(Pre event) {
   }

   public void onGatherTooltipComponents(GatherComponents event) {
      if (getVariant(event.getItemStack()) != null) {
         int screenWidth = event.getScreenWidth();
         int screenHeight = event.getScreenHeight();
         int horizontalMargin = screenWidth < 500 ? 16 : 32;
         int maximumScreenWidth = Math.max(160, screenWidth - horizontalMargin);
         int preferredWidth = Mth.m_14045_((int)((float)screenWidth * 0.72F), 280, 560);
         if (screenHeight < 320) {
            preferredWidth = Math.max(preferredWidth, (int)((float)screenWidth * 0.86F));
         }

         event.setMaxWidth(Math.min(maximumScreenWidth, preferredWidth));
      }
   }

   public void onTooltipColor(Color event) {
      ItemStack stack = event.getItemStack();
      YamatoTooltipFrameRenderer.YamatoVariant variant = getVariant(stack);
      if (variant != null) {
         event.setBackground(-267382246);
         event.setBorderStart(variant.borderPrimary);
         event.setBorderEnd(-15194563);
         if (useDecorativeFrame()) {
            int x = event.getX();
            int y = event.getY();
            Font font = event.getFont();
            List<ClientTooltipComponent> components = event.getComponents();
            int width = 0;

            for (ClientTooltipComponent comp : components) {
               width = Math.max(width, comp.m_142069_(font));
            }

            int lineCount = components.size();
            int height = lineCount > 0 ? lineCount * (9 + 1) - 1 : 9;
            GuiGraphics g = event.getGraphics();
            int border = 4;
            int bx = x - border;
            int by = y - border;
            int bw = width + border * 2;
            int vTop = 0;
            int vBot = Math.min(40, height);
            int bh = height + border * 2 + vTop + vBot;
            float partialTick = Minecraft.m_91087_().m_91296_();
            float time = getGameTime(partialTick);
            float borderPulse = (Mth.m_14031_(time * 0.05F) + 1.0F) * 0.5F;
            g.m_280168_().m_85836_();
            g.m_280168_().m_252880_(0.0F, 0.0F, 400.0F);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableDepthTest();
            float bgAlpha = 0.3F + borderPulse * 0.3F;
            this.drawPanelShadow(g, bx, by, bw, bh, variant);
            this.drawVShapedPanel(g, bx, by, bw, bh, variant, bgAlpha, vTop, vBot);
            this.drawEnergyFlowBackground(g, bx, by, bw, bh, variant, time);
            this.drawCentralV(g, bx, by, bw, bh, variant, time);
            this.drawPanelBorder(g, bx, by, bw, bh, variant, borderPulse, vTop, vBot);
            this.drawTitleSeparator(g, bx, by, bw, 9 + 4, variant, time);
            this.drawCornerSlashDecorations(g, bx, by, bw, bh, variant, time);
            VergilArtFX vergilFx = this.getVergilFx(variant);
            vergilFx.regenerate(bx, by, bw, bh);
            vergilFx.render(g, partialTick);
            this.renderStars(g, bx, by, bw, bh, variant, partialTick);
            if (variant == YamatoTooltipFrameRenderer.YamatoVariant.DMC5_SDT) {
               this.drawSdtOuterGlow(g, bx, by, bw, bh, variant, time, borderPulse, vTop, vBot);
               this.drawSdtCornerSpikes(g, bx, by, bw, bh, variant, time, borderPulse, vTop, vBot);
            }

            g.m_280168_().m_85849_();
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
         }
      }
   }

   private static boolean useDecorativeFrame() {
      return false;
   }

   private void drawPanelShadow(GuiGraphics g, int x, int y, int w, int h, YamatoTooltipFrameRenderer.YamatoVariant variant) {
      int baseRgb = variant.borderPrimary & 16777215;
      int rectH = h - Math.min(24, h / 3);
      g.m_280509_(x + 3, y + 3, x + w + 3, y + rectH + 3, 1426063360 | baseRgb);
      g.m_280509_(x + 5, y + 5, x + w + 5, y + rectH + 5, 855638016 | baseRgb);
   }

   private void drawVShapedPanel(GuiGraphics g, int x, int y, int w, int h, YamatoTooltipFrameRenderer.YamatoVariant variant, float alpha, int vTop, int vBot) {
      int a = (int)(alpha * 255.0F);
      int topColor = a << 24 | variant.bgGradientTop & 16777215;
      int botColor = a << 24 | variant.bgGradientBottom & 16777215;
      int rectY = y + vTop;
      int rectH = h - vTop - vBot;
      g.m_280024_(x, y, x + w, rectY, topColor, topColor);
      if (rectH > 0) {
         g.m_280024_(x, rectY, x + w, rectY + rectH, topColor, botColor);
      }

      for (int row = 0; row < vBot; row++) {
         float t = (float)row / (float)vBot;
         float tSmooth = (float)Math.pow((double)t, 1.3F);
         int inset = (int)((float)w / 2.0F * tSmooth);
         int rowY = rectY + rectH + row;
         float colorT = (float)(vTop + rectH + row) / (float)h;
         g.m_280509_(x + inset, rowY, x + w - inset, rowY + 1, blendColor(topColor, botColor, colorT));
      }
   }

   private static int blendColor(int c1, int c2, float t) {
      int a = (int)((float)(c1 >> 24 & 0xFF) * (1.0F - t) + (float)(c2 >> 24 & 0xFF) * t);
      int r = (int)((float)(c1 >> 16 & 0xFF) * (1.0F - t) + (float)(c2 >> 16 & 0xFF) * t);
      int g = (int)((float)(c1 >> 8 & 0xFF) * (1.0F - t) + (float)(c2 >> 8 & 0xFF) * t);
      int b = (int)((float)(c1 & 0xFF) * (1.0F - t) + (float)(c2 & 0xFF) * t);
      return a << 24 | r << 16 | g << 8 | b;
   }

   private void drawGradientPanel(GuiGraphics g, int x, int y, int w, int h, YamatoTooltipFrameRenderer.YamatoVariant variant, float alpha) {
      int a = (int)(alpha * 255.0F);
      int top = a << 24 | variant.bgGradientTop & 16777215;
      int bot = a << 24 | variant.bgGradientBottom & 16777215;
      g.m_280024_(x, y, x + w, y + h, top, bot);
   }

   private void drawCentralV(GuiGraphics g, int x, int y, int w, int h, YamatoTooltipFrameRenderer.YamatoVariant variant, float time) {
      float baseSize = (float)Math.min(w, h) * 0.625F;
      float breath = (Mth.m_14031_(time * 0.025F) + 1.0F) * 0.5F;
      float scale = 0.88F + breath * 0.12F;
      float alpha = 0.64F + breath * 0.22F;
      int drawSize = (int)(baseSize * scale);
      int cx = x + w / 2 - drawSize / 2;
      int cy = y + h / 2 - drawSize / 2;
      g.m_280168_().m_85836_();
      g.m_280168_().m_252880_((float)x + (float)w / 2.0F, (float)y + (float)h / 2.0F, 0.0F);
      float rot = Mth.m_14031_(time * 0.018F) * 3.0F;
      g.m_280168_().m_252781_(Axis.f_252403_.m_252977_(rot));
      g.m_280168_().m_252880_((float)(-drawSize) / 2.0F, (float)(-drawSize) / 2.0F, 0.0F);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      g.m_280411_(V_TEXTURE, 0, 0, drawSize, drawSize, 0.0F, 0.0F, 256, 256, 256, 256);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      g.m_280168_().m_85849_();
   }

   private void drawEnergyFlowBackground(GuiGraphics g, int x, int y, int w, int h, YamatoTooltipFrameRenderer.YamatoVariant variant, float time) {
      int baseRgb = variant.borderPrimary & 16777215;
      int flowSpacing = 14;

      for (int i = -h; i < w + h; i += flowSpacing) {
         float t = (float)i / (float)(w + h);
         float alphaBase = (Mth.m_14031_(t * 8.0F + time * 0.03F) + 1.0F) * 0.5F;
         int alpha = (int)(alphaBase * 10.0F);
         if (alpha >= 3) {
            int color = alpha << 24 | baseRgb;
            int x1 = Math.max(x, x + i - h);
            int y1 = Math.max(y, y + i - w);
            int x2 = Math.min(x + w, x + i);
            int y2 = Math.min(y + h, y + i);
            if (x2 > x1) {
               drawSlashLine(g, x1, y1, x2, y2, color);
            }
         }
      }
   }

   private void drawPanelBorder(GuiGraphics g, int x, int y, int w, int h, YamatoTooltipFrameRenderer.YamatoVariant variant, float pulse, int vTop, int vBot) {
      int outerAlpha = (int)(204.0F + pulse * 51.0F);
      int outerColor = Math.min(outerAlpha, 255) << 24 | variant.borderPrimary & 16777215;
      int rectY = y + vTop;
      int rectH = h - vTop - vBot;
      if (vTop > 0) {
         g.m_280509_(x, y, x + 1, y + vTop, outerColor);
         g.m_280509_(x + w - 1, y, x + w, y + vTop, outerColor);
      }

      g.m_280509_(x, rectY, x + 1, rectY + rectH, outerColor);
      g.m_280509_(x + w - 1, rectY, x + w, rectY + rectH, outerColor);

      for (int row = 0; row < vBot; row++) {
         float t = (float)row / (float)vBot;
         float tSmooth = (float)Math.pow((double)t, 1.3F);
         int inset = (int)((float)w / 2.0F * tSmooth);
         int rowY = rectY + rectH + row;
         g.m_280509_(x + inset, rowY, x + inset + 1, rowY + 1, outerColor);
         g.m_280509_(x + w - inset - 1, rowY, x + w - inset, rowY + 1, outerColor);
      }

      int in = 2;
      int inRectY = rectY + in;
      int inRectH = rectH - in * 2;
      if (inRectH > 0) {
         g.m_280509_(x + in, inRectY, x + in + 1, inRectY + inRectH, variant.accentColor);
         g.m_280509_(x + w - in - 1, inRectY, x + w - in, inRectY + inRectH, variant.accentColor);
      }

      int hlIn = 3;
      int hlAlpha = (int)(34.0F + pulse * 30.0F);
      int hlColor = hlAlpha << 24 | variant.borderPrimary & 16777215;
      int hlRectY = rectY + hlIn;
      int hlRectH = rectH - hlIn * 2;
      if (hlRectH > 0) {
         g.m_280509_(x + hlIn, hlRectY, x + hlIn + 1, hlRectY + hlRectH, hlColor);
         g.m_280509_(x + w - hlIn - 1, hlRectY, x + w - hlIn, hlRectY + hlRectH, hlColor);
      }
   }

   private static void drawRectOutline(GuiGraphics g, int x, int y, int w, int h, int color) {
      g.m_280509_(x, y, x + w, y + 1, color);
      g.m_280509_(x, y + h - 1, x + w, y + h, color);
      g.m_280509_(x, y, x + 1, y + h, color);
      g.m_280509_(x + w - 1, y, x + w, y + h, color);
   }

   private void drawTitleSeparator(GuiGraphics g, int x, int y, int w, int titleH, YamatoTooltipFrameRenderer.YamatoVariant variant, float time) {
      int lineY = y + titleH;
      int margin = 6;
      int lineW = w - margin * 2;
      int accent = variant.accentColor & 16777215;
      int lineThickness = 2;

      for (int i = 0; i < lineW; i++) {
         float t = (float)i / (float)lineW;
         float alphaFactor = 1.0F - 2.0F * Math.abs(t - 0.5F);
         alphaFactor *= 0.7F + 0.3F * Mth.m_14031_(t * 4.0F + time * 0.02F);
         int a = (int)(128.0F * alphaFactor);
         if (a >= 10) {
            g.m_280509_(x + margin + i, lineY, x + margin + i + 1, lineY + lineThickness, a << 24 | accent);
         }
      }
   }

   private void drawCornerSlashDecorations(GuiGraphics g, int x, int y, int w, int h, YamatoTooltipFrameRenderer.YamatoVariant variant, float time) {
      float breath = (Mth.m_14031_(time * 0.04F + 1.5F) + 1.0F) * 0.5F;
      int alpha = (int)(112.0F + breath * 64.0F);
      int color = Math.min(alpha, 255) << 24 | variant.borderPrimary & 16777215;
      int cornerLen = 8;
      int t = 2;
      g.m_280509_(x, y, x + cornerLen, y + t, color);
      g.m_280509_(x, y, x + t, y + cornerLen, color);

      for (int i = 1; i <= 3; i++) {
         int ox = x + i * 2;
         int oy = y + i * 2;
         drawSlashLine(g, ox + cornerLen - 2, oy, ox, oy + cornerLen - 2, color);
      }

      g.m_280509_(x + w - cornerLen, y, x + w, y + t, color);
      g.m_280509_(x + w - t, y, x + w, y + cornerLen, color);

      for (int i = 1; i <= 3; i++) {
         int ox = x + w - i * 2;
         int oy = y + i * 2;
         drawSlashLine(g, ox - cornerLen + 2, oy, ox, oy + cornerLen - 2, color);
      }

      g.m_280509_(x, y + h - t, x + cornerLen, y + h, color);
      g.m_280509_(x, y + h - cornerLen, x + t, y + h, color);

      for (int i = 1; i <= 3; i++) {
         int ox = x + i * 2;
         int oy = y + h - i * 2;
         drawSlashLine(g, ox + cornerLen - 2, oy, ox, oy - cornerLen + 2, color);
      }

      g.m_280509_(x + w - cornerLen, y + h - t, x + w, y + h, color);
      g.m_280509_(x + w - t, y + h - cornerLen, x + w, y + h, color);

      for (int i = 1; i <= 3; i++) {
         int ox = x + w - i * 2;
         int oy = y + h - i * 2;
         drawSlashLine(g, ox - cornerLen + 2, oy, ox, oy - cornerLen + 2, color);
      }
   }

   private void renderStars(GuiGraphics g, int x, int y, int w, int h, YamatoTooltipFrameRenderer.YamatoVariant variant, float partialTick) {
      boolean changed = x != this.lastTooltipX || y != this.lastTooltipY || w != this.lastTooltipW || h != this.lastTooltipH;
      if (changed) {
         this.lastTooltipX = x;
         this.lastTooltipY = y;
         this.lastTooltipW = w;
         this.lastTooltipH = h;
      }

      TooltipStarFX fx = this.starFxCache.computeIfAbsent(variant, v -> new TooltipStarFX(v.starColor, v.starDensity));
      if (changed) {
         fx.regenerate(x, y, w, h);
      }

      fx.render(g, partialTick);
   }

   private VergilArtFX getVergilFx(YamatoTooltipFrameRenderer.YamatoVariant variant) {
      return this.vergilFxCache.computeIfAbsent(variant, v -> new VergilArtFX(v.vergilConfig));
   }

   private void drawSdtOuterGlow(
      GuiGraphics g, int x, int y, int w, int h, YamatoTooltipFrameRenderer.YamatoVariant variant, float time, float pulse, int vTop, int vBot
   ) {
      int rectY = y + vTop;
      int rectH = h - vTop - vBot;
      int blueRgb = variant.borderPrimary & 16777215;
      int purpleRgb = variant.accentColor & 16777215;
      int pR = Math.min(255, (purpleRgb >> 16 & 0xFF) + 64);
      int pG = Math.min(255, (purpleRgb >> 8 & 0xFF) + 32);
      int pB = Math.min(255, (purpleRgb & 0xFF) + 48);
      purpleRgb = pR << 16 | pG << 8 | pB;
      float dualPulse = (Mth.m_14031_(time * 0.055F + 0.8F) + 1.0F) * 0.5F;
      float purplePhase = (Mth.m_14031_(time * 0.045F + 2.5F) + 1.0F) * 0.5F;
      int glow1 = 6 + (int)(pulse * 4.0F);
      int alpha1 = (int)(20.0F + pulse * 24.0F + dualPulse * 12.0F);
      int color1 = Math.min(alpha1, 255) << 24 | blueRgb;
      if (rectH > 0) {
         g.m_280509_(x - glow1, rectY, x, rectY + rectH, color1);
         g.m_280509_(x + w, rectY, x + w + glow1, rectY + rectH, color1);
      }

      if (vTop > 0) {
         g.m_280509_(x - glow1, y, x, y + vTop, color1);
         g.m_280509_(x + w, y, x + w + glow1, y + vTop, color1);
      }

      g.m_280509_(x - glow1, y - glow1 / 2, x + w + glow1, y, color1);
      g.m_280509_(x - glow1, y + h, x + w + glow1, y + h + glow1 / 2, color1);
      int glow2 = 3 + (int)(purplePhase * 3.0F);
      int alpha2 = (int)(12.0F + purplePhase * 26.0F);
      if (alpha2 > 8) {
         int color2 = Math.min(alpha2, 255) << 24 | purpleRgb;
         if (rectH > 0) {
            g.m_280509_(x - glow2, rectY, x, rectY + rectH, color2);
            g.m_280509_(x + w, rectY, x + w + glow2, rectY + rectH, color2);
         }

         if (vTop > 0) {
            g.m_280509_(x - glow2, y, x, y + vTop, color2);
            g.m_280509_(x + w, y, x + w + glow2, y + vTop, color2);
         }
      }

      float orbPulse = (Mth.m_14031_(time * 0.07F) + 1.0F) * 0.5F;
      int orbAlpha = (int)(32.0F + orbPulse * 48.0F);
      int orbColor = Math.min(orbAlpha, 255) << 24 | blueRgb;
      int orbR = 5 + (int)(orbPulse * 3.0F);
      int[][] corners = new int[][]{{x, y}, {x + w, y}, {x, y + h}, {x + w, y + h}};

      for (int[] c : corners) {
         for (int layer = 2; layer >= 0; layer--) {
            int lr = orbR + layer * 2;
            int la = (int)((float)orbAlpha * (0.25F + (float)layer * 0.25F));
            int lc = Math.min(la, 255) << 24 | blueRgb;
            g.m_280509_(c[0] - lr, c[1] - lr, c[0] + lr + 1, c[1] + lr + 1, lc);
         }
      }
   }

   private void drawSdtCornerSpikes(
      GuiGraphics g, int x, int y, int w, int h, YamatoTooltipFrameRenderer.YamatoVariant variant, float time, float pulse, int vTop, int vBot
   ) {
      int blueRgb = variant.borderPrimary & 16777215;
      int purpleRgb = variant.accentColor & 16777215;
      int sR = Math.min(255, (purpleRgb >> 16 & 0xFF) + 80);
      int sG = Math.min(255, (purpleRgb >> 8 & 0xFF) + 48);
      int sB = Math.min(255, (purpleRgb & 0xFF) + 64);
      int spikeColor = -587202560 | sR << 16 | sG << 8 | sB;
      float spikePulse = (Mth.m_14031_(time * 0.065F + 0.5F) + 1.0F) * 0.5F;
      int spikeLen = 8 + (int)(spikePulse * 3.0F);
      int spikeAlpha = (int)(170.0F + spikePulse * 85.0F);
      int[][] cornerDirs = new int[][]{{x, y, -1, -1}, {x + w, y, 1, -1}, {x, y + h, -1, 1}, {x + w, y + h, 1, 1}};

      for (int[] cd : cornerDirs) {
         int cx = cd[0];
         int cy = cd[1];
         int dx = cd[2];
         int dy = cd[3];

         for (int i = 0; i < spikeLen; i++) {
            float t = (float)i / (float)spikeLen;
            int width = Math.max(1, (int)((1.0F - t) * 4.0F));
            int sx = cx + dx * i;
            int sy = cy + dy * i;
            float tipBrightness = 1.0F - t * 0.6F;
            int sa = (int)((float)spikeAlpha * tipBrightness);
            int sc = Math.min(sa, 255) << 24 | blueRgb;
            if (t > 0.7F) {
               sc = Math.min(sa, 255) << 24 | 16777215;
            }

            g.m_280509_(sx, sy, sx + width, sy + 1, sc);
            if (dy != 0) {
               g.m_280509_(sx, sy, sx + 1, sy + width * Math.abs(dy), sc);
            }
         }

         for (int side = -1; side <= 1; side += 2) {
            int subLen = spikeLen / 2 + (int)(spikePulse * 2.0F);

            for (int i = 1; i < subLen; i++) {
               float tx = (float)i / (float)subLen;
               int sxx = cx + dx * i + side * (i / 2);
               int syx = cy + dy * i + side * (i / 2);
               int sax = (int)((float)spikeAlpha * 0.7F * (1.0F - tx));
               if (sax >= 15) {
                  int scx = Math.min(sax, 255) << 24 | spikeColor;
                  g.m_280509_(sxx, syx, sxx + 1, syx + 1, scx);
               }
            }
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
      int maxSteps = Math.min(Math.max(dx, -dy) + 1, 30);

      for (int i = 0; i < maxSteps; i++) {
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

   private static float getGameTime(float partialTick) {
      long gameTime = Minecraft.m_91087_().f_91073_ != null ? Minecraft.m_91087_().f_91073_.m_46467_() : System.currentTimeMillis() / 50L;
      return (float)gameTime + partialTick;
   }

   public static enum YamatoVariant {
      DMC4("dmc4", -12285748, -536540112, -536209336, 22, -10053121, -872397688, 0, 0, 0, 0, VergilArtFX.Config.dmc4()),
      DMC5("dmc5", -3351041, -536342472, -535814072, 25, -3351041, -869051017, 0, 0, 0, 0, VergilArtFX.Config.dmc5()),
      DMC5_BD("dmc5_bd", -3351041, -536342472, -535814072, 25, -3351041, -869051017, 0, 0, 0, 0, VergilArtFX.Config.dmc5()),
      DMC5_SDT("dmc5_sdt", -6697729, -536672744, -536474584, 32, -4460801, -870169481, 0, 0, 0, 0, VergilArtFX.Config.dmc5sdt());

      public final String id;
      public final int borderPrimary;
      public final int bgGradientTop;
      public final int bgGradientBottom;
      public final int starDensity;
      public final int starColor;
      public final int accentColor;
      public final int vanillaBgTop;
      public final int vanillaBgBottom;
      public final int vanillaBorderStart;
      public final int vanillaBorderEnd;
      public final VergilArtFX.Config vergilConfig;

      private YamatoVariant(
         String id,
         int borderPrimary,
         int bgTop,
         int bgBottom,
         int starDensity,
         int starColor,
         int accentColor,
         int vanillaBgTop,
         int vanillaBgBottom,
         int vanillaBorderStart,
         int vanillaBorderEnd,
         VergilArtFX.Config vergilConfig
      ) {
         this.id = id;
         this.borderPrimary = borderPrimary;
         this.bgGradientTop = bgTop;
         this.bgGradientBottom = bgBottom;
         this.starDensity = starDensity;
         this.starColor = starColor;
         this.accentColor = accentColor;
         this.vanillaBgTop = vanillaBgTop;
         this.vanillaBgBottom = vanillaBgBottom;
         this.vanillaBorderStart = vanillaBorderStart;
         this.vanillaBorderEnd = vanillaBorderEnd;
         this.vergilConfig = vergilConfig;
      }
   }
}
