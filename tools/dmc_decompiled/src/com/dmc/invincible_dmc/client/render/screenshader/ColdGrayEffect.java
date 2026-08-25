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

public class ColdGrayEffect extends ScreenEffectBase {
   static ResourceLocation cold_gray = OjangUtils.newRL("invincible_dmc", "cold_gray");
   public final ColdGrayEffect.ColdGrayPipeline pipeline;
   public float intensity = 0.9F;
   public float coldness = 0.6F;
   public float grayness = 0.95F;

   public ColdGrayEffect(Vec3 pos) {
      this(pos, 63, 0.9F, 0.86F, 1.0F);
   }

   public ColdGrayEffect(Vec3 pos, int lifetime, float intensity, float coldness, float grayness) {
      super(cold_gray, pos);
      this.pipeline = new ColdGrayEffect.ColdGrayPipeline(this);
      this.lifetime = lifetime;
      this.intensity = intensity;
      this.coldness = coldness;
      this.grayness = grayness;
   }

   public boolean shouldPost(Camera camera, Frustum clippingHelper) {
      return (Boolean)DMConfig.VIX_COLD_GRAY.get();
   }

   public Pipeline getPipeline() {
      return this.pipeline;
   }

   public static class ColdGrayPipeline extends SE_Pipeline<ColdGrayEffect> {
      private static final ResourceLocation tempTarget = OjangUtils.newRL("invincible_dmc", "cold_gray_temp");
      private boolean targetCreationFailed;

      public ColdGrayPipeline(ColdGrayEffect effect) {
         super(ColdGrayEffect.cold_gray, effect);
         this.priority = 180;
      }

      public void PostEffectHandler() {
         if (!this.targetCreationFailed) {
            RenderTarget mainTarget = Minecraft.m_91087_().m_91385_();

            RenderTarget temp;
            try {
               temp = TargetManager.getTarget(tempTarget);
            } catch (RuntimeException var5) {
               DMCLog.warn(DMCLog.Category.RENDER, "ColdGrayEffect: Failed to create temp target, effect disabled.", var5);
               this.targetCreationFailed = true;
               return;
            }

            PostPasses.blit.process(mainTarget, temp);
            float partialTick = Minecraft.m_91087_().m_91296_();
            float progress = ((float)((ColdGrayEffect)this.effect).age + partialTick) / (float)((ColdGrayEffect)this.effect).lifetime;
            progress = Math.min(progress, 1.0F);
            PostPasses.cold_gray
               .process(
                  temp,
                  mainTarget,
                  ((ColdGrayEffect)this.effect).intensity,
                  ((ColdGrayEffect)this.effect).coldness,
                  ((ColdGrayEffect)this.effect).grayness,
                  progress
               );
         }
      }
   }
}
