package com.dmc.invincible_dmc.client.render.custom;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.client.render.IDRenderType;
import com.dmc.invincible_dmc.client.render.PostPasses;
import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.guhao.vix.client.targets.TargetManager;
import com.guhao.vix.util.OjangUtils;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class SpaceBrokenRenderType extends ConfigurablePostParticleRenderType {
   static final com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline ppl1 = new SpaceBrokenRenderType.Pipeline(
      OjangUtils.newRL("invincible_dmc", "space_broken_0"), 10
   );
   static final com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline ppl2 = new SpaceBrokenRenderType.Pipeline(
      OjangUtils.newRL("invincible_dmc", "space_broken_1"), 11
   );
   final int layer;
   final int vertex;

   public SpaceBrokenRenderType(ResourceLocation name, ResourceLocation texture, int layer, int vertexCount) {
      super(name, texture, DMConfig.VIX_SPACE_BROKEN::get);
      this.layer = layer;
      this.vertex = vertexCount;
      this.priority = 1000;
   }

   public SpaceBrokenRenderType(ResourceLocation name, int layer) {
      super(name, IDRenderType.GetTexture("particle/sparks"), DMConfig.VIX_SPACE_BROKEN::get);
      this.layer = layer;
      this.vertex = 3;
      this.priority = 1000;
   }

   public void setupBufferBuilder(BufferBuilder bufferBuilder) {
      bufferBuilder.m_166779_(this.vertex == 3 ? Mode.TRIANGLES : Mode.QUADS, DefaultVertexFormat.f_85820_);
   }

   public com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline getPipeline() {
      return this.layer == 0 ? ppl1 : ppl2;
   }

   public static class Pipeline extends com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline {
      private static final ResourceLocation tmpTarget = OjangUtils.newRL("invincible_dmc", "space_broken_tmp");

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

      void handlePasses(RenderTarget src) {
         RenderTarget tmp = TargetManager.getTarget(tmpTarget);
         RenderTarget main = Minecraft.m_91087_().m_91385_();
         PostPasses.space_broken.process(main, src, tmp);
         PostPasses.blit.process(tmp, main);
         TargetManager.ReleaseTarget(tmpTarget);
      }

      public void PostEffectHandler() {
         this.handlePasses(this.bufferTarget);
      }
   }
}
