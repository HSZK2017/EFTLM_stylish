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

public class ScreenVignetteEffect extends ScreenEffectBase {
   static ResourceLocation screen_vignette = OjangUtils.newRL("invincible_dmc", "screen_vignette");
   public final ScreenVignetteEffect.VignettePipeline pipeline;
   public float maxDarkness = 0.82F;
   public float radius = 0.34F;
   public float softness = 0.4F;

   public ScreenVignetteEffect() {
      this(50);
   }

   public ScreenVignetteEffect(int lifetime) {
      this(lifetime, 0.82F, 0.24F, 0.5F);
   }

   public ScreenVignetteEffect(int lifetime, float maxDarkness, float radius, float softness) {
      super(screen_vignette, Vec3.f_82478_);
      this.pipeline = new ScreenVignetteEffect.VignettePipeline(this);
      this.lifetime = Math.max(1, lifetime);
      this.maxDarkness = Math.max(0.0F, maxDarkness);
      this.radius = Math.max(0.0F, radius);
      this.softness = Math.max(0.001F, softness);
   }

   public boolean shouldPost(Camera camera, Frustum clippingHelper) {
      return (Boolean)DMConfig.VIX_SCREEN_VIGNETTE.get();
   }

   public Pipeline getPipeline() {
      return this.pipeline;
   }

   public static float calcVignetteCurve(float progress) {
      float clampedProgress = Math.max(0.0F, Math.min(progress, 1.0F));
      if (clampedProgress <= 0.14F) {
         return 0.0F;
      } else {
         return clampedProgress < 0.95F ? smoothStep((clampedProgress - 0.14F) / 0.56F) : 1.0F - smoothStep((clampedProgress - 0.95F) / 0.3F);
      }
   }

   private static float smoothStep(float value) {
      float clampedValue = Math.max(0.0F, Math.min(value, 1.0F));
      return clampedValue * clampedValue * (3.0F - 2.0F * clampedValue);
   }

   public static class VignettePipeline extends SE_Pipeline<ScreenVignetteEffect> {
      private static final ResourceLocation tempTarget = OjangUtils.newRL("invincible_dmc", "screen_vignette_temp");
      private boolean targetCreationFailed;

      public VignettePipeline(ScreenVignetteEffect effect) {
         super(ScreenVignetteEffect.screen_vignette, effect);
         this.priority = 225;
      }

      public void PostEffectHandler() {
         if (!this.targetCreationFailed && PostPasses.blit != null && PostPasses.screen_vignette != null) {
            float partialTick = Minecraft.m_91087_().m_91296_();
            float progress = Math.min(
               ((float)((ScreenVignetteEffect)this.effect).age + partialTick) / (float)Math.max(((ScreenVignetteEffect)this.effect).lifetime, 1), 1.0F
            );
            float intensity = ScreenVignetteEffect.calcVignetteCurve(progress) * ((ScreenVignetteEffect)this.effect).maxDarkness;
            if (!(intensity <= 1.0E-4F)) {
               RenderTarget mainTarget = Minecraft.m_91087_().m_91385_();

               RenderTarget temp;
               try {
                  temp = TargetManager.getTarget(tempTarget);
               } catch (RuntimeException var7) {
                  DMCLog.warn(DMCLog.Category.RENDER, "ScreenVignetteEffect: Failed to create temp target, effect disabled.", var7);
                  this.targetCreationFailed = true;
                  return;
               }

               PostPasses.blit.process(mainTarget, temp);
               PostPasses.screen_vignette
                  .process(temp, mainTarget, intensity, ((ScreenVignetteEffect)this.effect).radius, ((ScreenVignetteEffect)this.effect).softness);
            }
         }
      }
   }
}
