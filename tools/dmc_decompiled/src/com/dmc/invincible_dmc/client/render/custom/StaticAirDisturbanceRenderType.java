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

public class StaticAirDisturbanceRenderType extends ConfigurablePostParticleRenderType {
   static final com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline ppl = new StaticAirDisturbanceRenderType.Pipeline(
      OjangUtils.newRL("invincible_dmc", "static_air_disturbance"), 150
   );

   public StaticAirDisturbanceRenderType(ResourceLocation name, ResourceLocation location) {
      super(name, location, DMConfig.AIR_TRAIL::get);
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
      private static final ResourceLocation tmpTarget = OjangUtils.newRL("invincible_dmc", "static_air_disturbance_tmp");
      private float lastStrength = 0.0F;

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

      void handleDisturbanceEffect(RenderTarget src) {
         RenderTarget tmp = TargetManager.getTarget(tmpTarget);
         RenderTarget main = Minecraft.m_91087_().m_91385_();
         float baseStrength = 0.007F;
         float speedFactor = 1.0F;
         if (Minecraft.m_91087_().f_91074_ != null) {
            float speed = (float)Minecraft.m_91087_().f_91074_.m_20184_().m_82553_();
            speedFactor = 0.6F + Math.min(0.8F, speed * 1.5F);
         }

         float targetStrength = baseStrength * 1.0F * speedFactor;
         this.lastStrength = this.lastStrength * 0.7F + targetStrength * 0.3F;
         float directionX = 1.0F;
         float directionY = 0.3F;
         if (Minecraft.m_91087_().f_91074_ != null) {
            float yaw = Minecraft.m_91087_().f_91074_.m_146908_();
            directionX = (float)Math.cos(Math.toRadians((double)yaw));
            directionY = (float)Math.sin(Math.toRadians((double)yaw)) * 0.5F;
         }

         PostPasses.static_air_disturbance.process(main, src, tmp, this.lastStrength, directionX, directionY);
         PostPasses.blit.process(tmp, main);
         TargetManager.ReleaseTarget(tmpTarget);
      }

      public void PostEffectHandler() {
         this.handleDisturbanceEffect(this.bufferTarget);
      }
   }
}
