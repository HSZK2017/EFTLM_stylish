package com.dmc.invincible_dmc.client.render.screenshader;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.client.render.PostPasses;
import com.dmc.invincible_dmc.utils.DMCLog;
import com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline;
import com.guhao.vix.client.screeneffect.ScreenEffectBase;
import com.guhao.vix.client.screeneffect.ScreenEffectBase.SE_Pipeline;
import com.guhao.vix.client.targets.TargetManager;
import com.guhao.vix.util.OjangUtils;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class ScreenFlashEffect extends ScreenEffectBase {
   static ResourceLocation screen_flash = OjangUtils.newRL("invincible_dmc", "screen_flash");
   public final ScreenFlashEffect.FlashPipeline pipeline;
   public float maxBrightness = 1.5F;
   public float glowIntensity = 0.8F;
   public float coldIntensity = 0.9F;
   public float coldColdness = 0.6F;
   public float coldGrayness = 0.95F;

   public ScreenFlashEffect() {
      super(screen_flash, Vec3.f_82478_);
      this.pipeline = new ScreenFlashEffect.FlashPipeline(this);
      this.lifetime = 8;
   }

   public ScreenFlashEffect(int lifetime, float maxBrightness, float glowIntensity) {
      super(screen_flash, Vec3.f_82478_);
      this.pipeline = new ScreenFlashEffect.FlashPipeline(this);
      this.lifetime = lifetime;
      this.maxBrightness = maxBrightness;
      this.glowIntensity = glowIntensity;
   }

   public ScreenFlashEffect(int lifetime, float maxBrightness, float glowIntensity, float coldIntensity, float coldColdness, float coldGrayness) {
      super(screen_flash, Vec3.f_82478_);
      this.pipeline = new ScreenFlashEffect.FlashPipeline(this);
      this.lifetime = lifetime;
      this.maxBrightness = maxBrightness;
      this.glowIntensity = glowIntensity;
      this.coldIntensity = coldIntensity;
      this.coldColdness = coldColdness;
      this.coldGrayness = coldGrayness;
   }

   public boolean shouldPost(Camera camera, Frustum clippingHelper) {
      return (Boolean)DMConfig.VIX_SCREEN_FLASH.get();
   }

   public Pipeline getPipeline() {
      return this.pipeline;
   }

   public static class FlashPipeline extends SE_Pipeline<ScreenFlashEffect> {
      private static final ResourceLocation tempTarget = OjangUtils.newRL("invincible_dmc", "flash_temp");
      private boolean targetCreationFailed;

      public FlashPipeline(ScreenFlashEffect effect) {
         super(ScreenFlashEffect.screen_flash, effect);
         this.priority = 200;
      }

      public void PostEffectHandler() {
         if (!this.targetCreationFailed) {
            RenderTarget mainTarget = Minecraft.m_91087_().m_91385_();

            RenderTarget temp;
            try {
               temp = TargetManager.getTarget(tempTarget);
            } catch (RuntimeException var10) {
               DMCLog.warn(DMCLog.Category.RENDER, "ScreenFlashEffect: Failed to create temp target, effect disabled.", var10);
               this.targetCreationFailed = true;
               return;
            }

            PostPasses.blit.process(mainTarget, temp);
            float partialTick = Minecraft.m_91087_().m_91296_();
            float progress = ((float)((ScreenFlashEffect)this.effect).age + partialTick) / (float)((ScreenFlashEffect)this.effect).lifetime;
            progress = Math.min(progress, 1.0F);
            float brightness = ((ScreenFlashEffect)this.effect).maxBrightness - (((ScreenFlashEffect)this.effect).maxBrightness - 1.0F) * progress;
            brightness = Math.max(brightness, 1.0F);
            float glowStrength = (float)Math.pow(1.0 - (double)progress, 2.0);
            float glow = ((ScreenFlashEffect)this.effect).glowIntensity * glowStrength;
            float coldFade;
            if (progress < 0.8F) {
               coldFade = 1.0F;
            } else {
               float coldProgress = (progress - 0.8F) / 0.2F;
               coldFade = (float)Math.pow(1.0 - (double)coldProgress, 2.0);
            }

            PostPasses.screen_flash
               .process(
                  temp,
                  mainTarget,
                  brightness,
                  glow,
                  coldFade,
                  ((ScreenFlashEffect)this.effect).coldIntensity,
                  ((ScreenFlashEffect)this.effect).coldColdness,
                  ((ScreenFlashEffect)this.effect).coldGrayness
               );
         }
      }
   }
}
