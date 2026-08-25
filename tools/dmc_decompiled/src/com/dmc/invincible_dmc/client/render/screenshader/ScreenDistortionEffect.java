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
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ScreenDistortionEffect extends ScreenEffectBase {
   static ResourceLocation screen_distortion = OjangUtils.newRL("invincible_dmc", "screen_distortion");
   public final SE_Pipeline ppl;
   public float maxDistortion = 0.1F;
   public float frequency = 5.0F;
   public float distortionSpeed = 2.0F;

   public ScreenDistortionEffect(Vec3 pos) {
      this(pos, 60, 0.1F, 5.0F, 2.0F);
   }

   public ScreenDistortionEffect(Vec3 pos, int lifetime, float maxDistortion, float frequency, float distortionSpeed) {
      super(screen_distortion, pos);
      this.ppl = new ScreenDistortionEffect.Distortion_Pipeline(this);
      this.lifetime = lifetime;
      this.maxDistortion = maxDistortion;
      this.frequency = frequency;
      this.distortionSpeed = distortionSpeed;
   }

   public Pipeline getPipeline() {
      return this.ppl;
   }

   public boolean shouldPost(Camera camera, Frustum clippingHelper) {
      return (Boolean)DMConfig.VIX_SCREEN_DISTORTION.get();
   }

   public static class Distortion_Pipeline extends SE_Pipeline<ScreenDistortionEffect> {
      static ResourceLocation distortion_tmp = OjangUtils.newRL("invincible_dmc", "distortion_tmp");
      private boolean targetCreationFailed;

      public Distortion_Pipeline(ScreenDistortionEffect effect) {
         super(ScreenDistortionEffect.screen_distortion, effect);
         this.priority = 100;
      }

      public void PostEffectHandler() {
         if (!this.targetCreationFailed) {
            RenderTarget tmp;
            try {
               tmp = TargetManager.getTarget(distortion_tmp);
            } catch (RuntimeException var5) {
               DMCLog.warn(DMCLog.Category.RENDER, "ScreenDistortionEffect: Failed to create temp target, effect disabled.", var5);
               this.targetCreationFailed = true;
               return;
            }

            PostPasses.blit.process(Minecraft.m_91087_().m_91385_(), tmp);
            float progress = this.getDistortionProgress();
            float currentDistortion = ((ScreenDistortionEffect)this.effect).maxDistortion * this.getDistortionIntensity(progress);
            float time = (float)((ScreenDistortionEffect)this.effect).age * 0.05F * ((ScreenDistortionEffect)this.effect).distortionSpeed;
            PostPasses.screen_distortion
               .process(tmp, Minecraft.m_91087_().m_91385_(), currentDistortion, ((ScreenDistortionEffect)this.effect).frequency, time, progress);
         }
      }

      private float getDistortionProgress() {
         float normalizedAge = ((ScreenDistortionEffect)this.effect).getNormalizedAgeWithPartialTicks();
         float baseProgress = Mth.m_14031_(normalizedAge * (float) Math.PI);
         if (normalizedAge > 0.8F) {
            float endFade = 1.0F - (normalizedAge - 0.7F) / 0.3F;
            endFade = endFade * endFade * (3.0F - 2.0F * endFade);
            baseProgress *= endFade;
         }

         return baseProgress;
      }

      private float getDistortionIntensity(float progress) {
         float normalizedAge = ((ScreenDistortionEffect)this.effect).getNormalizedAgeWithPartialTicks();
         float baseIntensity = 4.0F * progress * (1.0F - progress);
         if (normalizedAge > 0.75F) {
            float endFade = 1.0F - (normalizedAge - 0.6F) / 0.4F;
            endFade = Mth.m_14116_(endFade);
            baseIntensity *= endFade;
         }

         return baseIntensity;
      }
   }
}
