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
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class BlackWhiteFlashEffect extends ScreenEffectBase {
   static ResourceLocation bw_contrast = OjangUtils.newRL("invincible_dmc", "bw_contrast");
   public final BlackWhiteFlashEffect.BWC_Pipeline ppl;
   public boolean should_render = true;
   private final BlackWhiteFlashEffect.ImpactMode impactMode;
   private final float contrast;
   private final float brightness;

   public BlackWhiteFlashEffect(Vec3 pos) {
      this(pos, BlackWhiteFlashEffect.ImpactMode.HEAVY);
   }

   public BlackWhiteFlashEffect(Vec3 pos, BlackWhiteFlashEffect.ImpactMode mode) {
      this(pos, mode, 2.4F, 0.68F);
   }

   public BlackWhiteFlashEffect(Vec3 pos, BlackWhiteFlashEffect.ImpactMode mode, float contrast, float brightness) {
      super(bw_contrast, pos);
      this.impactMode = mode;
      this.contrast = contrast;
      this.brightness = brightness;
      this.ppl = new BlackWhiteFlashEffect.BWC_Pipeline(this);
      this.lifetime = (int)Math.ceil((double)mode.durationSec * 20.0) + 1;
   }

   public void tick() {
      if (++this.age > this.lifetime) {
         this.should_render = false;
      }
   }

   public boolean shouldPost(Camera camera, Frustum clippingHelper) {
      boolean enabled = this.should_render && (Boolean)DMConfig.VIX_BLACK_WHITE_FLASH.get();
      if (enabled) {
         this.ppl.captureFocalUV(camera);
      }

      return enabled;
   }

   public Pipeline getPipeline() {
      return this.ppl;
   }

   public static class BWC_Pipeline extends SE_Pipeline<BlackWhiteFlashEffect> {
      static ResourceLocation bw_tmp = OjangUtils.newRL("invincible_dmc", "bw_tmp");
      private long startNanos = 0L;
      private final float[] focalUV = new float[]{0.5F, 0.5F};
      private final Matrix4f viewProjection = new Matrix4f();
      private final Vector4f clipPosition = new Vector4f();
      private float focalVisibility;
      private boolean targetCreationFailed;

      public BWC_Pipeline(BlackWhiteFlashEffect effect) {
         super(BlackWhiteFlashEffect.bw_contrast, effect);
         this.priority = 9999;
      }

      private void captureFocalUV(Camera camera) {
         this.focalUV[0] = 0.5F;
         this.focalUV[1] = 0.5F;
         this.focalVisibility = 0.0F;
         if (camera != null && ((BlackWhiteFlashEffect)this.effect).pos != null) {
            Vec3 cameraPosition = camera.m_90583_();
            this.viewProjection
               .set(RenderSystem.getProjectionMatrix())
               .rotateX((float)Math.toRadians((double)camera.m_90589_()))
               .rotateY((float)Math.toRadians((double)(camera.m_90590_() + 180.0F)));
            this.clipPosition
               .set(
                  (float)(((BlackWhiteFlashEffect)this.effect).pos.f_82479_ - cameraPosition.f_82479_),
                  (float)(((BlackWhiteFlashEffect)this.effect).pos.f_82480_ - cameraPosition.f_82480_),
                  (float)(((BlackWhiteFlashEffect)this.effect).pos.f_82481_ - cameraPosition.f_82481_),
                  1.0F
               );
            this.viewProjection.transform(this.clipPosition);
            float clipW = this.clipPosition.w;
            if (clipW > 1.0E-4F && Float.isFinite(this.clipPosition.x) && Float.isFinite(this.clipPosition.y) && Float.isFinite(clipW)) {
               float ndcX = this.clipPosition.x / clipW;
               float ndcY = this.clipPosition.y / clipW;
               float maximumNdc = Math.max(Math.abs(ndcX), Math.abs(ndcY));
               if (Float.isFinite(maximumNdc) && !(maximumNdc > 4.0F)) {
                  this.focalUV[0] = ndcX * 0.5F + 0.5F;
                  this.focalUV[1] = ndcY * 0.5F + 0.5F;
                  this.focalVisibility = 1.0F - Math.max(0.0F, Math.min(1.0F, (maximumNdc - 1.0F) * 0.5F));
               }
            }
         }
      }

      public void PostEffectHandler() {
         if (!this.targetCreationFailed
            && ((BlackWhiteFlashEffect)this.effect).should_render
            && PostPasses.blit != null
            && PostPasses.black_white_contrast != null) {
            long now = System.nanoTime();
            if (this.startNanos == 0L) {
               this.startNanos = now;
            }

            BlackWhiteFlashEffect.ImpactMode mode = ((BlackWhiteFlashEffect)this.effect).impactMode;
            double elapsedSec = (double)(now - this.startNanos) / 1.0E9;
            float time = (float)Math.min(elapsedSec / (double)mode.durationSec, 1.0);
            if (time >= 1.0F) {
               ((BlackWhiteFlashEffect)this.effect).should_render = false;
            } else {
               RenderTarget mainTarget = Minecraft.m_91087_().m_91385_();

               RenderTarget tmp;
               try {
                  tmp = TargetManager.getTarget(bw_tmp);
               } catch (RuntimeException var10) {
                  DMCLog.warn(DMCLog.Category.RENDER, "BlackWhiteFlashEffect: Failed to create temp target, effect disabled.", var10);
                  this.targetCreationFailed = true;
                  return;
               }

               PostPasses.blit.process(mainTarget, tmp);
               PostPasses.black_white_contrast
                  .process(
                     tmp,
                     mainTarget,
                     ((BlackWhiteFlashEffect)this.effect).contrast,
                     ((BlackWhiteFlashEffect)this.effect).brightness,
                     time,
                     mode.intensity,
                     mode.speed,
                     mode.modeValue,
                     mode.impactThreshold,
                     mode.impactThresholdLerp,
                     this.focalUV[0],
                     this.focalUV[1],
                     this.focalVisibility,
                     mode.chromaticStrength,
                     mode.lensDistortStrength
                  );
            }
         }
      }
   }

   public static enum ImpactMode {
      LIGHT(0.18F, 0.0F, 1.35F, 1.05F, 0.39F, 0.1F, 0.012F, -0.34F),
      HEAVY(0.25F, 1.0F, 1.05F, 1.15F, 0.43F, 0.1F, 0.01F, -0.3F);

      final float durationSec;
      final float modeValue;
      final float speed;
      final float intensity;
      final float impactThreshold;
      final float impactThresholdLerp;
      final float chromaticStrength;
      final float lensDistortStrength;

      private ImpactMode(
         float durationSec,
         float modeValue,
         float speed,
         float intensity,
         float impactThreshold,
         float impactThresholdLerp,
         float chromaticStrength,
         float lensDistortStrength
      ) {
         this.durationSec = durationSec;
         this.modeValue = modeValue;
         this.speed = speed;
         this.intensity = intensity;
         this.impactThreshold = impactThreshold;
         this.impactThresholdLerp = impactThresholdLerp;
         this.chromaticStrength = chromaticStrength;
         this.lensDistortStrength = lensDistortStrength;
      }
   }
}
