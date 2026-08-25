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

public class PureChromaticAberrationEffect extends ScreenEffectBase {
   static ResourceLocation pure_chromatic_aberration = OjangUtils.newRL("invincible_dmc", "pure_chromatic_aberration");
   public final PureChromaticAberrationEffect.PureCAPipeline pipeline;
   public float chromaIntensity = 0.5F;
   public float centerX = 0.5F;
   public float centerY = 0.5F;

   public PureChromaticAberrationEffect(Vec3 pos) {
      this(pos, 60, 0.5F, 0.5F, 0.5F);
   }

   public PureChromaticAberrationEffect(Vec3 pos, int lifetime, float chromaIntensity, float centerX, float centerY) {
      super(pure_chromatic_aberration, pos);
      this.pipeline = new PureChromaticAberrationEffect.PureCAPipeline(this);
      this.lifetime = lifetime;
      this.chromaIntensity = chromaIntensity;
      this.centerX = centerX;
      this.centerY = centerY;
   }

   public boolean shouldPost(Camera camera, Frustum clippingHelper) {
      return (Boolean)DMConfig.VIX_PURE_CHROMATIC_ABERRATION.get();
   }

   public Pipeline getPipeline() {
      return this.pipeline;
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

   public static class PureCAPipeline extends SE_Pipeline<PureChromaticAberrationEffect> {
      private static final ResourceLocation tempTarget = OjangUtils.newRL("invincible_dmc", "pure_chromatic_aberration_temp");
      private boolean targetCreationFailed;

      public PureCAPipeline(PureChromaticAberrationEffect effect) {
         super(PureChromaticAberrationEffect.pure_chromatic_aberration, effect);
         this.priority = 175;
      }

      public void PostEffectHandler() {
         if (!this.targetCreationFailed) {
            RenderTarget mainTarget = Minecraft.m_91087_().m_91385_();

            RenderTarget temp;
            try {
               temp = TargetManager.getTarget(tempTarget);
            } catch (RuntimeException var6) {
               DMCLog.warn(DMCLog.Category.RENDER, "PureChromaticAberrationEffect: Failed to create temp target, effect disabled.", var6);
               this.targetCreationFailed = true;
               return;
            }

            PostPasses.blit.process(mainTarget, temp);
            float partialTick = Minecraft.m_91087_().m_91296_();
            float progress = ((float)((PureChromaticAberrationEffect)this.effect).age + partialTick)
               / (float)((PureChromaticAberrationEffect)this.effect).lifetime;
            progress = Math.min(progress, 1.0F);
            float chromaProgress = PureChromaticAberrationEffect.calcChromaCurve(progress) * ((PureChromaticAberrationEffect)this.effect).chromaIntensity;
            PostPasses.pure_chromatic_aberration
               .process(
                  temp,
                  mainTarget,
                  ((PureChromaticAberrationEffect)this.effect).centerX,
                  ((PureChromaticAberrationEffect)this.effect).centerY,
                  ((PureChromaticAberrationEffect)this.effect).chromaIntensity,
                  chromaProgress
               );
         }
      }
   }
}
