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

public class ChromaticAberrationEnhancedRenderType extends ConfigurablePostParticleRenderType {
   public static final com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline ppl = new ChromaticAberrationEnhancedRenderType.Pipeline(
      OjangUtils.newRL("invincible_dmc", "chromatic_aberration_enhanced_o"), 100
   );

   public ChromaticAberrationEnhancedRenderType(ResourceLocation name, ResourceLocation texture) {
      super(name, texture, DMConfig.VIX_PARTICLE_CHROMATIC_ABERRATION_ENHANCED::get);
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
      private static final ResourceLocation tmpTarget = OjangUtils.newRL("invincible_dmc", "chromatic_enhanced_tmp");
      private static final float DURATION = 0.45F;
      private long startTimeMs = 0L;
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
                  this.startTimeMs = System.currentTimeMillis();
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
         if (this.animationStarted) {
            RenderTarget tmp = TargetManager.getTarget(tmpTarget);
            RenderTarget main = Minecraft.m_91087_().m_91385_();
            float elapsed = (float)(System.currentTimeMillis() - this.startTimeMs) / 1000.0F;
            if (elapsed >= 0.45F) {
               this.animationStarted = false;
               TargetManager.ReleaseTarget(tmpTarget);
            } else {
               float progress = this.calculateProgressAt(elapsed);
               float strength = 1.2F;
               PostPasses.chromatic_aberration_enhanced.process(main, src, tmp, strength, progress);
               PostPasses.blit.process(tmp, main);
               TargetManager.ReleaseTarget(tmpTarget);
            }
         }
      }

      private float calculateProgressAt(float elapsed) {
         float t = elapsed / 0.45F;
         if (t >= 1.0F) {
            return 0.0F;
         } else if (t < 0.7F) {
            return 1.0F;
         } else {
            float p = (t - 0.7F) / 0.3F;
            float q = 1.0F - p;
            return q * q;
         }
      }

      public void PostEffectHandler() {
         this.handleDisturbanceEffect(this.bufferTarget);
      }
   }
}
