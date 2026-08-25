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

public class ImpactBlurEffect extends ScreenEffectBase {
   private static final ResourceLocation IMPACT_BLUR = OjangUtils.newRL("invincible_dmc", "impact_blur");
   public final ImpactBlurEffect.ImpactBlurPipeline pipeline = new ImpactBlurEffect.ImpactBlurPipeline(this);
   public float strength;
   public int samples;
   public float centerX;
   public float centerY;

   public ImpactBlurEffect(float strength, int lifetime) {
      this(Vec3.f_82478_, lifetime, strength * 0.1F, 10, 0.5F, 0.5F);
   }

   public ImpactBlurEffect(Vec3 pos, int lifetime, float strength, int samples, float centerX, float centerY) {
      super(IMPACT_BLUR, pos);
      this.lifetime = lifetime;
      this.strength = strength;
      this.samples = samples;
      this.centerX = centerX;
      this.centerY = centerY;
   }

   public boolean shouldPost(Camera camera, Frustum clippingHelper) {
      return (Boolean)DMConfig.VIX_IMPACT_BLUR.get();
   }

   public Pipeline getPipeline() {
      return this.pipeline;
   }

   public static class ImpactBlurPipeline extends SE_Pipeline<ImpactBlurEffect> {
      private static final ResourceLocation TEMP_TARGET = OjangUtils.newRL("invincible_dmc", "impact_blur_temp");
      private boolean targetCreationFailed;

      public ImpactBlurPipeline(ImpactBlurEffect effect) {
         super(ImpactBlurEffect.IMPACT_BLUR, effect);
         this.priority = 180;
      }

      public void PostEffectHandler() {
         if (!this.targetCreationFailed) {
            RenderTarget mainTarget = Minecraft.m_91087_().m_91385_();

            RenderTarget tempTarget;
            try {
               tempTarget = TargetManager.getTarget(TEMP_TARGET);
            } catch (RuntimeException var4) {
               DMCLog.warn(DMCLog.Category.RENDER, "ImpactBlurEffect: Failed to create temp target, effect disabled.", var4);
               this.targetCreationFailed = true;
               return;
            }

            PostPasses.blit.process(mainTarget, tempTarget);
            float intensity = Math.min(((ImpactBlurEffect)this.effect).getNormalizedAgeWithPartialTicks(), 1.0F);
            PostPasses.impact_blur
               .process(
                  tempTarget,
                  mainTarget,
                  ((ImpactBlurEffect)this.effect).centerX,
                  ((ImpactBlurEffect)this.effect).centerY,
                  intensity,
                  ((ImpactBlurEffect)this.effect).strength,
                  ((ImpactBlurEffect)this.effect).samples
               );
         }
      }
   }
}
