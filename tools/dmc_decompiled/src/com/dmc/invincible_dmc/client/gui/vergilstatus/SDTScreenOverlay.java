package com.dmc.invincible_dmc.client.gui.vergilstatus;

import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.joml.Matrix4f;

public class SDTScreenOverlay implements IGuiOverlay {
   public static final SDTScreenOverlay INSTANCE = new SDTScreenOverlay();
   private float currentBarRatio = 0.0F;
   private float ratioVelocity = 0.0F;
   private float currentColorR = 0.0F;
   private float rVelocity = 0.0F;
   private float currentColorG = 0.0F;
   private float gVelocity = 0.0F;
   private float currentColorB = 0.0F;
   private float bVelocity = 0.0F;
   private float currentAlpha = 0.0F;
   private float alphaVelocity = 0.0F;
   private float pulseTime = 0.0F;
   private long lastSystemTime = 0L;
   private final List<SDTScreenOverlay.BubbleParticle> particles = new ArrayList<>();
   private final Random random = new Random();
   private int currentPhase = 0;
   private float sdtProgress = 0.0F;
   private float secondProgress = 0.0F;
   private boolean wasActive = false;

   public void updateState(int phase, float sdtProg, float secondProg) {
      this.currentPhase = phase;
      this.sdtProgress = sdtProg;
      this.secondProgress = secondProg;
   }

   private float calculateDeltaTime() {
      long now = System.nanoTime();
      if (this.lastSystemTime == 0L) {
         this.lastSystemTime = now;
      }

      float deltaTime = (float)(now - this.lastSystemTime) / 1.0E9F;
      this.lastSystemTime = now;
      return Mth.m_14036_(deltaTime, 0.001F, 0.1F);
   }

   public void render(ForgeGui gui, GuiGraphics g, float partialTick, int screenWidth, int screenHeight) {
      LocalPlayer player = gui.getMinecraft().f_91074_;
      if (player != null && !VergilSkill.NotHoldingYamato(player)) {
         float deltaTime = this.calculateDeltaTime();
         this.pulseTime += deltaTime;
         if (this.currentPhase == 0) {
            this.resetParticles();
         } else {
            float targetRatio = 0.0F;
            float targetR = 0.0F;
            float targetG = 0.0F;
            float targetB = 0.0F;
            float targetAlpha = 0.0F;
            boolean isActive = this.currentPhase == 4;
            float particleSpawnRate = 0.0F;
            int particleType = 0;
            if (isActive) {
               targetRatio = 0.065F;
               targetR = 0.23529412F;
               targetG = 0.05882353F;
               targetB = 0.8627451F;
               targetAlpha = 0.6F;
            } else if (this.currentPhase != 3 && (this.currentPhase != 2 || !(this.secondProgress > 0.0F))) {
               if (this.currentPhase == 1 || this.sdtProgress > 0.0F) {
                  targetRatio = Mth.m_14179_(this.sdtProgress, 0.0F, 0.05F);
                  targetR = 0.078431375F;
                  targetG = 0.15686275F;
                  targetB = 0.7058824F;
                  targetAlpha = Mth.m_14179_(this.sdtProgress, 0.0F, 0.25F);
                  particleSpawnRate = 0.05F + 0.1F * this.sdtProgress;
                  particleType = 0;
               }
            } else {
               targetRatio = Mth.m_14179_(this.secondProgress, 0.05F, 0.065F);
               targetR = Mth.m_14179_(this.secondProgress, 0.078431375F, 0.3529412F);
               targetG = Mth.m_14179_(this.secondProgress, 0.15686275F, 0.078431375F);
               targetB = Mth.m_14179_(this.secondProgress, 0.7058824F, 0.8627451F);
               targetAlpha = Mth.m_14179_(this.secondProgress, 0.25F, 0.4F);
               particleSpawnRate = 0.15F + 0.2F * this.secondProgress;
               particleType = 1;
            }

            if (isActive && !this.wasActive) {
               this.triggerParticleBurst();
            }

            this.wasActive = isActive;
            float smoothTime = 0.15F;
            this.currentBarRatio = this.smoothDamp(this.currentBarRatio, targetRatio, smoothTime, 5.0F, deltaTime, 0);
            this.currentColorR = this.smoothDamp(this.currentColorR, targetR, smoothTime, 5.0F, deltaTime, 1);
            this.currentColorG = this.smoothDamp(this.currentColorG, targetG, smoothTime, 5.0F, deltaTime, 2);
            this.currentColorB = this.smoothDamp(this.currentColorB, targetB, smoothTime, 5.0F, deltaTime, 3);
            this.currentAlpha = this.smoothDamp(this.currentAlpha, targetAlpha, smoothTime, 5.0F, deltaTime, 4);
            if (!(this.currentBarRatio < 0.001F) || !this.particles.isEmpty()) {
               int barHeight = (int)((float)screenHeight * this.currentBarRatio);
               g.m_280168_().m_85836_();
               g.m_280168_().m_252880_(0.0F, 0.0F, -300.0F);
               RenderSystem.disableDepthTest();
               if (barHeight > 0) {
                  this.drawWavyCurtains(
                     g.m_280168_().m_85850_().m_252922_(),
                     screenWidth,
                     screenHeight,
                     barHeight,
                     this.currentColorR,
                     this.currentColorG,
                     this.currentColorB,
                     this.currentAlpha
                  );
                  if (this.currentAlpha > 0.1F) {
                     this.renderDemonicFlow(
                        g.m_280168_().m_85850_().m_252922_(),
                        screenWidth,
                        screenHeight,
                        barHeight,
                        this.currentColorR,
                        this.currentColorG,
                        this.currentColorB,
                        this.currentAlpha,
                        isActive
                     );
                  }
               }

               if (!isActive && this.random.nextFloat() < particleSpawnRate) {
                  boolean isTop = this.random.nextBoolean();
                  float px = this.random.nextFloat() * (float)screenWidth;
                  float baseEdge = isTop ? (float)barHeight * 0.8F : (float)screenHeight - (float)barHeight * 0.8F;
                  float py = baseEdge + (this.random.nextFloat() - 0.5F) * 10.0F;
                  this.particles.add(new SDTScreenOverlay.BubbleParticle(px, py, isTop, particleType));
               }

               this.renderBubbleParticles(g, barHeight, screenHeight, isActive);
               RenderSystem.enableDepthTest();
               g.m_280168_().m_85849_();
            }
         }
      } else {
         this.resetParticles();
      }
   }

   private void drawWavyCurtains(Matrix4f matrix, int width, int height, int barHeight, float r, float g, float b, float a) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(GameRenderer::m_172811_);
      RenderSystem.setShaderTexture(0, 0);
      int solidA = (int)(a * 255.0F);
      int ir = (int)(r * 255.0F);
      int ig = (int)(g * 255.0F);
      int ib = (int)(b * 255.0F);
      Tesselator tesselator = Tesselator.m_85913_();
      BufferBuilder buffer = tesselator.m_85915_();
      buffer.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85815_);
      int segments = Math.max(10, width / 20);
      float segWidth = (float)width / (float)segments;
      float waveAmp = (float)barHeight * 0.15F;

      for (int i = 0; i < segments; i++) {
         float x1 = (float)i * segWidth;
         float x2 = (float)(i + 1) * segWidth;
         float wave1 = Mth.m_14031_(x1 * 0.015F + this.pulseTime * 1.5F) * waveAmp + Mth.m_14089_(x1 * 0.005F - this.pulseTime) * waveAmp * 0.5F;
         float wave2 = Mth.m_14031_(x2 * 0.015F + this.pulseTime * 1.5F) * waveAmp + Mth.m_14089_(x2 * 0.005F - this.pulseTime) * waveAmp * 0.5F;
         float topY1 = (float)barHeight + wave1;
         float topY2 = (float)barHeight + wave2;
         buffer.m_252986_(matrix, x1, 0.0F, 0.0F).m_6122_(ir, ig, ib, solidA).m_5752_();
         buffer.m_252986_(matrix, x1, topY1, 0.0F).m_6122_(ir, ig, ib, 0).m_5752_();
         buffer.m_252986_(matrix, x2, topY2, 0.0F).m_6122_(ir, ig, ib, 0).m_5752_();
         buffer.m_252986_(matrix, x2, 0.0F, 0.0F).m_6122_(ir, ig, ib, solidA).m_5752_();
         float botY1 = (float)(height - barHeight) - wave1;
         float botY2 = (float)(height - barHeight) - wave2;
         buffer.m_252986_(matrix, x1, botY1, 0.0F).m_6122_(ir, ig, ib, 0).m_5752_();
         buffer.m_252986_(matrix, x1, (float)height, 0.0F).m_6122_(ir, ig, ib, solidA).m_5752_();
         buffer.m_252986_(matrix, x2, (float)height, 0.0F).m_6122_(ir, ig, ib, solidA).m_5752_();
         buffer.m_252986_(matrix, x2, botY2, 0.0F).m_6122_(ir, ig, ib, 0).m_5752_();
      }

      tesselator.m_85914_();
   }

   private void renderDemonicFlow(Matrix4f matrix, int width, int height, int barHeight, float r, float g, float b, float alphaScale, boolean isActive) {
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
      RenderSystem.setShader(GameRenderer::m_172811_);
      Tesselator tesselator = Tesselator.m_85913_();
      BufferBuilder buffer = tesselator.m_85915_();
      buffer.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85815_);
      int segments = Math.max(20, width / 15);
      float segWidth = (float)width / (float)segments;

      for (int i = 0; i < segments; i++) {
         float x1 = (float)i * segWidth;
         float x2 = (float)(i + 1) * segWidth;
         float w1_1 = Mth.m_14031_(x1 * 0.009F - this.pulseTime * 2.2F);
         float w1_2 = Mth.m_14031_(x2 * 0.009F - this.pulseTime * 2.2F);
         float w2_1 = Mth.m_14089_(x1 * 0.022F + this.pulseTime * 1.8F);
         float w2_2 = Mth.m_14089_(x2 * 0.022F + this.pulseTime * 1.8F);
         float intensity1 = (w1_1 * 0.5F + w2_1 * 0.5F + 1.0F) * 0.5F;
         float intensity2 = (w1_2 * 0.5F + w2_2 * 0.5F + 1.0F) * 0.5F;
         int r1 = (int)Mth.m_14036_(r * 255.0F * (1.0F + intensity1), 0.0F, 255.0F);
         int g1 = (int)Mth.m_14036_(g * 255.0F * (1.0F + intensity1), 0.0F, 255.0F);
         int b1 = (int)Mth.m_14036_(b * 255.0F * (1.0F + intensity1), 0.0F, 255.0F);
         int a1 = (int)(40.0F * intensity1 * alphaScale * (isActive ? 1.1F : 1.0F));
         int r2 = (int)Mth.m_14036_(r * 255.0F * (1.0F + intensity2), 0.0F, 255.0F);
         int g2 = (int)Mth.m_14036_(g * 255.0F * (1.0F + intensity2), 0.0F, 255.0F);
         int b2 = (int)Mth.m_14036_(b * 255.0F * (1.0F + intensity2), 0.0F, 255.0F);
         int a2 = (int)(40.0F * intensity2 * alphaScale * (isActive ? 1.1F : 1.0F));
         float yTopCenter = (float)barHeight * 0.5F;
         float thickTop1 = (float)barHeight * (0.15F + 0.12F * intensity1);
         float thickTop2 = (float)barHeight * (0.15F + 0.12F * intensity2);
         this.drawFlowQuad(
            buffer,
            matrix,
            x1,
            x2,
            yTopCenter - thickTop1,
            yTopCenter - thickTop2,
            yTopCenter + thickTop1,
            yTopCenter + thickTop2,
            r1,
            g1,
            b1,
            a1,
            r2,
            g2,
            b2,
            a2
         );
         float yBotCenter = (float)height - (float)barHeight * 0.5F;
         float thickBot1 = (float)barHeight * (0.15F + 0.12F * intensity1);
         float thickBot2 = (float)barHeight * (0.15F + 0.12F * intensity2);
         this.drawFlowQuad(
            buffer,
            matrix,
            x1,
            x2,
            yBotCenter - thickBot1,
            yBotCenter - thickBot2,
            yBotCenter + thickBot1,
            yBotCenter + thickBot2,
            r1,
            g1,
            b1,
            a1,
            r2,
            g2,
            b2,
            a2
         );
      }

      tesselator.m_85914_();
      RenderSystem.defaultBlendFunc();
   }

   private void drawFlowQuad(
      BufferBuilder buffer,
      Matrix4f matrix,
      float x1,
      float x2,
      float yTop1,
      float yTop2,
      float yBot1,
      float yBot2,
      int r1,
      int g1,
      int b1,
      int a1,
      int r2,
      int g2,
      int b2,
      int a2
   ) {
      buffer.m_252986_(matrix, x1, yTop1, 0.0F).m_6122_(r1, g1, b1, 0).m_5752_();
      buffer.m_252986_(matrix, x1, yBot1, 0.0F).m_6122_(r1, g1, b1, a1).m_5752_();
      buffer.m_252986_(matrix, x2, yBot2, 0.0F).m_6122_(r2, g2, b2, a2).m_5752_();
      buffer.m_252986_(matrix, x2, yTop2, 0.0F).m_6122_(r2, g2, b2, 0).m_5752_();
   }

   private void triggerParticleBurst() {
      for (SDTScreenOverlay.BubbleParticle p : this.particles) {
         p.isBursting = true;
         p.life = Math.min(p.life, 20);
      }
   }

   private void renderBubbleParticles(GuiGraphics g, int barHeight, int screenHeight, boolean isActive) {
      Iterator<SDTScreenOverlay.BubbleParticle> iter = this.particles.iterator();

      while (iter.hasNext()) {
         SDTScreenOverlay.BubbleParticle p = iter.next();
         if (p.isBursting) {
            p.x = p.x + (this.random.nextFloat() - 0.5F) * 8.0F;
            p.y = p.y + (float)(p.isTop ? 1 : -1) * 4.0F;
         } else {
            p.y = p.y + p.vy;
            p.x = p.initialX + Mth.m_14031_(this.pulseTime * 3.0F + p.timeOffset) * 2.5F;
         }

         p.life--;
         if (p.life <= 0) {
            iter.remove();
         } else {
            float fade = 1.0F;
            if (p.life > p.maxLife - 10) {
               fade = (float)(p.maxLife - p.life) / 10.0F;
            } else if (p.life < 20) {
               fade = (float)p.life / 20.0F;
            }

            int a = (int)(fade * 140.0F) << 24;
            int color = p.type == 0 ? a | 14737663 : a | 11167487;
            g.m_280509_((int)p.x, (int)p.y, (int)p.x + p.size, (int)p.y + p.size, color);
         }
      }
   }

   private void resetParticles() {
      this.particles.clear();
      this.wasActive = false;
      this.lastSystemTime = 0L;
   }

   private float smoothDamp(float current, float target, float smoothTime, float maxSpeed, float deltaTime, int type) {
      smoothTime = Math.max(1.0E-4F, smoothTime);
      float omega = 2.0F / smoothTime;
      float x = omega * deltaTime;
      float exp = 1.0F / (1.0F + x + 0.48F * x * x + 0.235F * x * x * x);
      float change = current - target;
      float maxChange = maxSpeed * smoothTime;
      change = Mth.m_14036_(change, -maxChange, maxChange);
      target = current - change;

      float velocity = switch (type) {
         case 0 -> this.ratioVelocity;
         case 1 -> this.rVelocity;
         case 2 -> this.gVelocity;
         case 3 -> this.bVelocity;
         default -> this.alphaVelocity;
      };
      float temp = (velocity + omega * change) * deltaTime;
      float newVelocity = (velocity - omega * temp) * exp;
      switch (type) {
         case 0:
            this.ratioVelocity = newVelocity;
            break;
         case 1:
            this.rVelocity = newVelocity;
            break;
         case 2:
            this.gVelocity = newVelocity;
            break;
         case 3:
            this.bVelocity = newVelocity;
            break;
         default:
            this.alphaVelocity = newVelocity;
      }

      float output = target + (change + temp) * exp;
      if (target - current > 0.0F == output > target) {
         output = target;
         switch (type) {
            case 0:
               this.ratioVelocity = 0.0F;
               break;
            case 1:
               this.rVelocity = 0.0F;
               break;
            case 2:
               this.gVelocity = 0.0F;
               break;
            case 3:
               this.bVelocity = 0.0F;
               break;
            default:
               this.alphaVelocity = 0.0F;
         }
      }

      return output;
   }

   private class BubbleParticle {
      float initialX;
      float x;
      float y;
      float vy;
      float timeOffset;
      boolean isTop;
      int type;
      int life;
      int maxLife;
      int size;
      boolean isBursting = false;

      BubbleParticle(float x, float y, boolean isTop, int type) {
         this.initialX = x;
         this.x = x;
         this.y = y;
         this.isTop = isTop;
         this.type = type;
         this.timeOffset = SDTScreenOverlay.this.random.nextFloat() * 10.0F;
         this.vy = isTop ? 0.1F + SDTScreenOverlay.this.random.nextFloat() * 0.15F : -(0.1F + SDTScreenOverlay.this.random.nextFloat() * 0.15F);
         this.maxLife = 80 + SDTScreenOverlay.this.random.nextInt(40);
         this.life = this.maxLife;
         this.size = SDTScreenOverlay.this.random.nextFloat() > 0.7F ? 2 : 1;
      }
   }
}
