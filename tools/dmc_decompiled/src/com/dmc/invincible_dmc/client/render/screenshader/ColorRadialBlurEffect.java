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

public class ColorRadialBlurEffect extends ScreenEffectBase {
   static ResourceLocation color_radial_blur = OjangUtils.newRL("invincible_dmc", "color_radial_blur");
   public final ColorRadialBlurEffect.RadialBlurPipeline pipeline;
   public float blurIntensity = 0.5F;
   public float chromaIntensity = 1.0F;
   public int samples = 8;
   public float centerX = 0.5F;
   public float centerY = 0.5F;

   public ColorRadialBlurEffect(Vec3 pos) {
      this(pos, 60, 0.5F, 1.0F, 8, 0.5F, 0.5F);
   }

   public ColorRadialBlurEffect(Vec3 pos, int lifetime, float blurIntensity, float chromaIntensity, int samples, float centerX, float centerY) {
      super(color_radial_blur, pos);
      this.pipeline = new ColorRadialBlurEffect.RadialBlurPipeline(this);
      this.lifetime = lifetime;
      this.blurIntensity = blurIntensity;
      this.chromaIntensity = chromaIntensity;
      this.samples = samples;
      this.centerX = centerX;
      this.centerY = centerY;
   }

   public boolean shouldPost(Camera camera, Frustum clippingHelper) {
      return (Boolean)DMConfig.VIX_COLOR_RADIAL_BLUR.get();
   }

   public Pipeline getPipeline() {
      return this.pipeline;
   }

   public static float calcBlurCurve(float progress) {
      float t = 1.0F - progress;
      return t * t;
   }

   public static float calcChromaCurve(float progress) {
      if (progress < 0.2F) {
         float t = progress / 0.2F;
         return t * t;
      } else if (progress < 0.8F) {
         return 1.0F;
      } else {
         float t = (progress - 0.8F) / 0.2F;
         return (1.0F - t) * (1.0F - t);
      }
   }

   public static class RadialBlurPipeline extends SE_Pipeline<ColorRadialBlurEffect> {
      private static final ResourceLocation tempTarget = OjangUtils.newRL("invincible_dmc", "color_radial_blur_temp");
      private boolean targetCreationFailed;

      public RadialBlurPipeline(ColorRadialBlurEffect effect) {
         super(ColorRadialBlurEffect.color_radial_blur, effect);
         this.priority = 170;
      }

      public void PostEffectHandler() {
         if (!this.targetCreationFailed) {
            RenderTarget mainTarget = Minecraft.m_91087_().m_91385_();

            RenderTarget temp;
            try {
               temp = TargetManager.getTarget(tempTarget);
            } catch (RuntimeException var7) {
               DMCLog.warn(DMCLog.Category.RENDER, "ColorRadialBlurEffect: Failed to create temp target, effect disabled.", var7);
               this.targetCreationFailed = true;
               return;
            }

            PostPasses.blit.process(mainTarget, temp);
            float partialTick = Minecraft.m_91087_().m_91296_();
            float progress = ((float)((ColorRadialBlurEffect)this.effect).age + partialTick) / (float)((ColorRadialBlurEffect)this.effect).lifetime;
            progress = Math.min(progress, 1.0F);
            float blurValue = ColorRadialBlurEffect.calcBlurCurve(progress) * ((ColorRadialBlurEffect)this.effect).blurIntensity;
            float chromaValue = ColorRadialBlurEffect.calcChromaCurve(progress) * ((ColorRadialBlurEffect)this.effect).chromaIntensity;
            PostPasses.color_radial_blur
               .process(
                  temp,
                  mainTarget,
                  ((ColorRadialBlurEffect)this.effect).centerX,
                  ((ColorRadialBlurEffect)this.effect).centerY,
                  blurValue,
                  ((ColorRadialBlurEffect)this.effect).samples,
                  chromaValue
               );
         }
      }
   }
}
