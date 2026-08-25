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
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public final class RiftAttractionEffect extends ScreenEffectBase {
   private static final ResourceLocation EFFECT_ID = OjangUtils.newRL("invincible_dmc", "rift_attraction");
   private static final int LIFETIME = 15;
   private static final float CLIP_MARGIN = 0.16F;
   private static final float MIN_CLIP_W = 1.0E-4F;
   private final Vec3 worldStart;
   private final Vec3 worldEnd;
   private final RiftAttractionEffect.RiftAttractionPipeline pipeline;

   public RiftAttractionEffect(Vec3 center, Vec3 worldStart, Vec3 worldEnd) {
      super(EFFECT_ID, center);
      this.worldStart = worldStart;
      this.worldEnd = worldEnd;
      this.lifetime = 15;
      this.pipeline = new RiftAttractionEffect.RiftAttractionPipeline(this);
   }

   public Pipeline getPipeline() {
      return this.pipeline;
   }

   public boolean shouldPost(Camera camera, Frustum frustum) {
      return (Boolean)DMConfig.VIX_SCREEN_DISTORTION.get() && this.pipeline.captureProjectedSegment(camera);
   }

   private static final class RiftAttractionPipeline extends SE_Pipeline<RiftAttractionEffect> {
      private static final ResourceLocation TEMP_TARGET = OjangUtils.newRL("invincible_dmc", "rift_attraction_tmp");
      private final Matrix4f viewProjection = new Matrix4f();
      private final Vector4f startClip = new Vector4f();
      private final Vector4f endClip = new Vector4f();
      private final float[] lineStart = new float[2];
      private final float[] lineEnd = new float[2];
      private float startTipFade;
      private float endTipFade;
      private boolean projected;
      private boolean targetCreationFailed;

      private RiftAttractionPipeline(RiftAttractionEffect effect) {
         super(RiftAttractionEffect.EFFECT_ID, effect);
         this.priority = 110;
      }

      private boolean captureProjectedSegment(Camera camera) {
         this.projected = false;
         if (camera == null) {
            return false;
         } else {
            Vec3 cameraPosition = camera.m_90583_();
            this.viewProjection
               .set(RenderSystem.getProjectionMatrix())
               .rotateX((float)Math.toRadians((double)camera.m_90589_()))
               .rotateY((float)Math.toRadians((double)(camera.m_90590_() + 180.0F)));
            this.transformWorldPoint(((RiftAttractionEffect)this.effect).worldStart, cameraPosition, this.startClip);
            this.transformWorldPoint(((RiftAttractionEffect)this.effect).worldEnd, cameraPosition, this.endClip);
            if (!clipAgainstNearPlane(this.startClip, this.endClip)) {
               return false;
            } else {
               float startU = this.startClip.x / this.startClip.w * 0.5F + 0.5F;
               float startV = this.startClip.y / this.startClip.w * 0.5F + 0.5F;
               float endU = this.endClip.x / this.endClip.w * 0.5F + 0.5F;
               float endV = this.endClip.y / this.endClip.w * 0.5F + 0.5F;
               if (!finite(startU, startV, endU, endV)) {
                  return false;
               } else {
                  float minimum = -0.16F;
                  float maximum = 1.16F;
                  this.startTipFade = inside(startU, startV, minimum, maximum) ? 1.0F : 0.0F;
                  this.endTipFade = inside(endU, endV, minimum, maximum) ? 1.0F : 0.0F;
                  float[] clipped = new float[]{startU, startV, endU, endV};
                  if (!clipToScreen(clipped, minimum, maximum)) {
                     return false;
                  } else {
                     float deltaU = clipped[2] - clipped[0];
                     float deltaV = clipped[3] - clipped[1];
                     if (deltaU * deltaU + deltaV * deltaV < 4.0E-6F) {
                        return false;
                     } else {
                        this.lineStart[0] = clipped[0];
                        this.lineStart[1] = clipped[1];
                        this.lineEnd[0] = clipped[2];
                        this.lineEnd[1] = clipped[3];
                        this.projected = true;
                        return true;
                     }
                  }
               }
            }
         }
      }

      private void transformWorldPoint(Vec3 point, Vec3 cameraPosition, Vector4f destination) {
         destination.set(
            (float)(point.f_82479_ - cameraPosition.f_82479_),
            (float)(point.f_82480_ - cameraPosition.f_82480_),
            (float)(point.f_82481_ - cameraPosition.f_82481_),
            1.0F
         );
         this.viewProjection.transform(destination);
      }

      public void PostEffectHandler() {
         if (this.projected && !this.targetCreationFailed && PostPasses.blit != null && PostPasses.rift_attraction != null) {
            RenderTarget mainTarget = Minecraft.m_91087_().m_91385_();

            RenderTarget temporary;
            try {
               temporary = TargetManager.getTarget(TEMP_TARGET);
            } catch (RuntimeException var4) {
               DMCLog.warn(DMCLog.Category.RENDER, "RiftAttractionEffect: Failed to create temp target, effect disabled.", var4);
               this.targetCreationFailed = true;
               return;
            }

            float intensity = animationIntensity(((RiftAttractionEffect)this.effect).getAgeWithPartialTicks());
            if (!(intensity <= 0.001F)) {
               PostPasses.blit.process(mainTarget, temporary);
               PostPasses.rift_attraction
                  .process(
                     temporary,
                     mainTarget,
                     this.lineStart[0],
                     this.lineStart[1],
                     this.lineEnd[0],
                     this.lineEnd[1],
                     0.0095F,
                     0.06F,
                     intensity,
                     this.startTipFade,
                     this.endTipFade
                  );
            }
         }
      }

      private static float animationIntensity(float elapsed) {
         if (elapsed < 1.25F) {
            return smootherStep(elapsed / 1.25F);
         } else {
            return elapsed < 9.0F ? 1.0F : 1.0F - smootherStep((elapsed - 9.0F) / 6.0F);
         }
      }

      private static float smootherStep(float value) {
         float clamped = Mth.m_14036_(value, 0.0F, 1.0F);
         return clamped * clamped * clamped * (clamped * (clamped * 6.0F - 15.0F) + 10.0F);
      }

      private static boolean clipAgainstNearPlane(Vector4f start, Vector4f end) {
         boolean startVisible = start.w > 1.0E-4F;
         boolean endVisible = end.w > 1.0E-4F;
         if (!startVisible && !endVisible) {
            return false;
         } else if (startVisible == endVisible) {
            return finite(start.x, start.y, start.w, end.x, end.y, end.w);
         } else {
            Vector4f behind = startVisible ? end : start;
            Vector4f visible = startVisible ? start : end;
            float denominator = visible.w - behind.w;
            if (Math.abs(denominator) < 1.0E-6F) {
               return false;
            } else {
               float interpolation = (1.0E-4F - behind.w) / denominator;
               behind.lerp(visible, Mth.m_14036_(interpolation, 0.0F, 1.0F));
               behind.w = 1.0E-4F;
               return finite(start.x, start.y, start.w, end.x, end.y, end.w);
            }
         }
      }

      private static boolean clipToScreen(float[] line, float minimum, float maximum) {
         float deltaU = line[2] - line[0];
         float deltaV = line[3] - line[1];
         float[] range = new float[]{0.0F, 1.0F};
         if (clipBoundary(-deltaU, line[0] - minimum, range)
            && clipBoundary(deltaU, maximum - line[0], range)
            && clipBoundary(-deltaV, line[1] - minimum, range)
            && clipBoundary(deltaV, maximum - line[1], range)) {
            float start = range[0];
            float end = range[1];
            line[2] = line[0] + deltaU * end;
            line[3] = line[1] + deltaV * end;
            line[0] += deltaU * start;
            line[1] += deltaV * start;
            return true;
         } else {
            return false;
         }
      }

      private static boolean clipBoundary(float direction, float distance, float[] range) {
         if (Math.abs(direction) < 1.0E-6F) {
            return distance >= 0.0F;
         } else {
            float ratio = distance / direction;
            if (direction < 0.0F) {
               if (ratio > range[1]) {
                  return false;
               }

               range[0] = Math.max(range[0], ratio);
            } else {
               if (ratio < range[0]) {
                  return false;
               }

               range[1] = Math.min(range[1], ratio);
            }

            return true;
         }
      }

      private static boolean inside(float u, float v, float minimum, float maximum) {
         return u >= minimum && u <= maximum && v >= minimum && v <= maximum;
      }

      private static boolean finite(float... values) {
         for (float value : values) {
            if (!Float.isFinite(value)) {
               return false;
            }
         }

         return true;
      }
   }
}
