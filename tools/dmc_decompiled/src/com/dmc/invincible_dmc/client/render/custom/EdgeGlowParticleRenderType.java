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

public class EdgeGlowParticleRenderType extends ConfigurablePostParticleRenderType {
   public static final com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline ppl = new EdgeGlowParticleRenderType.Pipeline(
      OjangUtils.newRL("invincible_dmc", "edge_glow_particle"), 80
   );
   private final float edgeIntensity;
   private final float glowIntensity;
   private final float glowRadius;

   public EdgeGlowParticleRenderType(ResourceLocation renderTypeID, ResourceLocation tex, float edgeIntensity, float glowIntensity, float glowRadius) {
      super(renderTypeID, tex, DMConfig.VIX_PARTICLE_EDGE_GLOW::get);
      this.edgeIntensity = edgeIntensity;
      this.glowIntensity = glowIntensity;
      this.glowRadius = glowRadius;
      this.priority = 1000;
      EdgeGlowParticleRenderType.Pipeline.setParams(edgeIntensity, glowIntensity, glowRadius);
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
      private static final ResourceLocation tmpTarget = OjangUtils.newRL("invincible_dmc", "edge_glow_tmp");
      private static float sharedEdgeIntensity = 0.8F;
      private static float sharedGlowIntensity = 0.9F;
      private static float sharedGlowRadius = 4.0F;
      private float accumulatedTime = 0.0F;

      public Pipeline(ResourceLocation name, int priority) {
         super(name);
         this.priority = priority;
      }

      static void setParams(float edgeIntensity, float glowIntensity, float glowRadius) {
         sharedEdgeIntensity = edgeIntensity;
         sharedGlowIntensity = glowIntensity;
         sharedGlowRadius = glowRadius;
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

      void handleEdgeGlowEffect(RenderTarget src) {
         if (PostPasses.edge_glow != null && PostPasses.blit != null) {
            RenderTarget tmp = TargetManager.getTarget(tmpTarget);
            RenderTarget main = Minecraft.m_91087_().m_91385_();
            float partialTick = Minecraft.m_91087_().m_91296_();
            this.accumulatedTime += partialTick * 0.05F;
            if (this.accumulatedTime > 1000.0F) {
               this.accumulatedTime -= 1000.0F;
            }

            PostPasses.edge_glow.process(main, src, tmp, sharedEdgeIntensity, sharedGlowIntensity, sharedGlowRadius, this.accumulatedTime);
            PostPasses.blit.process(tmp, main);
            TargetManager.ReleaseTarget(tmpTarget);
         }
      }

      public void PostEffectHandler() {
         if (this.bufferTarget != null) {
            this.handleEdgeGlowEffect(this.bufferTarget);
         }
      }
   }
}
