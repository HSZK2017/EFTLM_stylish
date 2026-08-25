package com.dmc.invincible_dmc.client.render.custom;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.client.render.PostPasses;
import com.guhao.vix.client.pipeline.PostParticleRenderType;
import com.guhao.vix.client.targets.ScaledTarget;
import com.guhao.vix.util.OjangUtils;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class BloomTrailParticleRenderType extends ConfigurablePostParticleRenderType {
   public static final com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline ppl = new BloomTrailParticleRenderType.Pipeline(
      OjangUtils.newRL("invincible_dmc", "bloom_particle")
   );

   public BloomTrailParticleRenderType(ResourceLocation renderTypeID, ResourceLocation tex) {
      super(renderTypeID, tex, DMConfig.VIX_PARTICLE_BLOOM::get);
   }

   private static int NumMul(int a, float b) {
      return (int)((float)a * Math.max(Math.min(b, 1.5F), 0.8F));
   }

   public com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline getPipeline() {
      return ppl;
   }

   public static class Pipeline extends com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline {
      RenderTarget[] blur;
      RenderTarget[] blur_;
      RenderTarget temp;

      public Pipeline(ResourceLocation name) {
         super(name);
      }

      void handlePasses(RenderTarget src) {
         RenderSystem.texParameter(3553, 10242, 33071);
         RenderSystem.texParameter(3553, 10243, 33071);
         RenderSystem.texParameter(3553, 10240, 9729);
         RenderSystem.texParameter(3553, 10241, 9729);
         PostPasses.downSampler.process(src, this.blur[0]);
         PostPasses.downSampler.process(this.blur[0], this.blur[1]);
         PostPasses.downSampler.process(this.blur[1], this.blur[2]);
         PostPasses.downSampler.process(this.blur[2], this.blur[3]);
         PostPasses.downSampler.process(this.blur[3], this.blur[4]);
         PostPasses.upSampler.process(this.blur[4], this.blur_[3], this.blur[3]);
         PostPasses.upSampler.process(this.blur_[3], this.blur_[2], this.blur[2]);
         PostPasses.upSampler.process(this.blur_[2], this.blur_[1], this.blur[1]);
         PostPasses.upSampler.process(this.blur_[1], this.blur_[0], this.blur[0]);
         PostPasses.unity_composite.process(this.blur_[0], this.temp, src, Minecraft.m_91087_().m_91385_());
         PostPasses.blit.process(this.temp, Minecraft.m_91087_().m_91385_());
      }

      void initTargets() {
         int cnt = 5;
         if (this.blur == null) {
            this.blur = new RenderTarget[cnt];
            float s = 1.0F;

            for (int i = 0; i < this.blur.length; i++) {
               s /= 2.0F;
               this.blur[i] = new ScaledTarget(s, s, this.bufferTarget.f_83915_, this.bufferTarget.f_83916_, false, Minecraft.f_91002_);
               this.blur[i].m_83931_(0.0F, 0.0F, 0.0F, 0.0F);
               this.blur[i].m_83954_(Minecraft.f_91002_);
               if (this.bufferTarget.isStencilEnabled()) {
                  this.blur[i].enableStencil();
               }
            }
         }

         if (this.blur_ == null) {
            this.blur_ = new RenderTarget[cnt - 1];
            float s = 1.0F;

            for (int ix = 0; ix < this.blur_.length; ix++) {
               s /= 2.0F;
               this.blur_[ix] = new ScaledTarget(s, s, this.bufferTarget.f_83915_, this.bufferTarget.f_83916_, false, Minecraft.f_91002_);
               this.blur_[ix].m_83931_(0.0F, 0.0F, 0.0F, 0.0F);
               this.blur_[ix].m_83954_(Minecraft.f_91002_);
               if (this.bufferTarget.isStencilEnabled()) {
                  this.blur[ix].enableStencil();
               }
            }
         }

         if (this.temp == null) {
            this.temp = PostParticleRenderType.createTempTarget(this.bufferTarget);
         }

         if (this.temp.f_83915_ != this.bufferTarget.f_83915_ || this.temp.f_83916_ != this.bufferTarget.f_83916_) {
            for (int ixx = 0; ixx < this.blur.length; ixx++) {
               this.blur[ixx].m_83941_(this.bufferTarget.f_83915_, this.bufferTarget.f_83916_, Minecraft.f_91002_);
            }

            for (int ixx = 0; ixx < this.blur_.length; ixx++) {
               this.blur_[ixx].m_83941_(this.bufferTarget.f_83915_, this.bufferTarget.f_83916_, Minecraft.f_91002_);
            }

            this.temp.m_83941_(this.bufferTarget.f_83915_, this.bufferTarget.f_83916_, Minecraft.f_91002_);
         }
      }

      public void PostEffectHandler() {
         this.initTargets();
         this.handlePasses(this.bufferTarget);
      }
   }
}
