package com.dmc.invincible_dmc.client.render.custom;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.client.render.PostPasses;
import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.guhao.vix.client.targets.TargetManager;
import com.guhao.vix.util.OjangUtils;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

public class ChromaticAberrationRenderType extends ConfigurablePostParticleRenderType {
   public static final com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline ppl = new ChromaticAberrationRenderType.Pipeline(
      OjangUtils.newRL("invincible_dmc", "chromatic_aberration_o"), 100
   );
   private final float strength;
   private final float speed;
   private final float directionX;
   private final float directionY;
   private final float bladeLength;

   public ChromaticAberrationRenderType(
      ResourceLocation name, float strength, float speed, float directionX, float directionY, float bladeLength, ResourceLocation texture
   ) {
      super(name, texture, DMConfig.VIX_PARTICLE_CHROMATIC_ABERRATION::get);
      this.strength = strength;
      this.speed = speed;
      this.directionX = directionX;
      this.directionY = directionY;
      this.bladeLength = bladeLength;
      this.priority = 1000;
   }

   protected ShaderInstance getShader() {
      return GameRenderer.f_172586_;
   }

   public void setupBufferBuilder(BufferBuilder bufferBuilder) {
      bufferBuilder.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85813_);
   }

   public com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline getPipeline() {
      return ppl;
   }

   public static class Pipeline extends com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline {
      private static final ResourceLocation tmpTarget = OjangUtils.newRL("invincible_dmc", "air_disturbance_tmp");
      private float currentTime = 0.0F;
      private float progress = 0.0F;
      private boolean animationStarted = false;

      public Pipeline(ResourceLocation name, int priority) {
         super(name);
         this.priority = priority;
      }

      public void start() {
         if (this.started) {
            if (PostEffectPipelines.isActive()) {
               this.bufferTarget.m_83947_(false);
            }
         } else {
            if (this.bufferTarget == null) {
               this.bufferTarget = TargetManager.getTarget(this.name);
               this.bufferTarget.m_83954_(Minecraft.f_91002_);
            }

            RenderTarget main = PostEffectPipelines.getSource();
            if (PostEffectPipelines.isActive()) {
               this.bufferTarget.m_83945_(main);
               PostEffectPipelines.PostEffectQueue.add(this);
               this.bufferTarget.m_83947_(false);
               this.started = true;
               if (!this.animationStarted) {
                  this.currentTime = 0.0F;
                  this.progress = 0.0F;
                  this.animationStarted = true;
               }
            }
         }
      }

      public void suspend() {
         if (PostEffectPipelines.isActive()) {
            this.bufferTarget.m_83970_();
            this.bufferTarget.m_83963_();
            RenderTarget rt = PostEffectPipelines.getSource();
            rt.m_83947_(false);
         } else {
            PostEffectPipelines.getSource().m_83947_(false);
         }
      }

      void handleDisturbanceEffect(RenderTarget src) {
         RenderTarget tmp = TargetManager.getTarget(tmpTarget);
         RenderTarget main = Minecraft.m_91087_().m_91385_();
         this.updateAnimation();
         float strength = 0.3F;
         float speed = 3.0F;
         float directionX = 1.0F;
         float directionY = 0.0F;
         float bladeLength = 0.5F;
         float partialTick = Minecraft.m_91087_().m_91296_();
         float smoothTime = this.currentTime + partialTick * 0.05F;
         float smoothProgress = this.calculateProgressAt(smoothTime);
         PostPasses.chromatic_aberration
            .process(main, src, tmp, strength * this.getCurrentStrength(), smoothTime, smoothProgress, directionX, directionY, bladeLength);
         PostPasses.blit.process(tmp, main);
         TargetManager.ReleaseTarget(tmpTarget);
      }

      private void updateAnimation() {
         if (this.animationStarted) {
            this.currentTime += 0.05F;
            this.progress = this.calculateProgress();
            if (this.progress <= 0.0F) {
               this.animationStarted = false;
            }
         }
      }

      private float calculateProgress() {
         return this.calculateProgressAt(this.currentTime);
      }

      private float calculateProgressAt(float time) {
         float duration = 2.0F;
         float normalizedTime = time / duration;
         if (normalizedTime < 0.3F) {
            return normalizedTime / 0.3F;
         } else {
            return normalizedTime < 1.0F ? 1.0F - (normalizedTime - 0.3F) / 0.7F : 0.0F;
         }
      }

      private float getCurrentStrength() {
         float normalizedTime = this.currentTime / 2.0F;
         if (normalizedTime > 0.5F) {
            float fade = 1.0F - (normalizedTime - 0.5F) / 0.5F;
            return fade * fade;
         } else {
            return 1.0F;
         }
      }

      public void PostEffectHandler() {
         this.handleDisturbanceEffect(this.bufferTarget);
      }
   }
}
