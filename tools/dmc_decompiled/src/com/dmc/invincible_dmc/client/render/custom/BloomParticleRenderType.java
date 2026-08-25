package com.dmc.invincible_dmc.client.render.custom;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.client.render.PostPasses;
import com.dmc.invincible_dmc.utils.DMCLog;
import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.guhao.vix.client.pipeline.PostParticleRenderType;
import com.guhao.vix.client.targets.ScaledTarget;
import com.guhao.vix.util.OjangUtils;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class BloomParticleRenderType extends ConfigurablePostParticleRenderType {
   public static final com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline ppl = new BloomParticleRenderType.Pipeline(
      OjangUtils.newRL("invincible_dmc", "bloom_particle")
   );

   public BloomParticleRenderType(ResourceLocation renderTypeID, ResourceLocation tex) {
      super(renderTypeID, tex, DMConfig.VIX_PARTICLE_BLOOM::get);
   }

   public com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline getPipeline() {
      return ppl;
   }

   public static void markBloomDrawn() {
      if ((Boolean)DMConfig.VIX_PARTICLE_BLOOM.get() && PostEffectPipelines.isActive()) {
         if (ppl instanceof BloomParticleRenderType.Pipeline pipeline) {
            pipeline.bloomDrawn = true;
         }
      }
   }

   public static class Pipeline extends com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline {
      private RenderTarget[] sharedBlur;
      private RenderTarget[] sharedBlur_;
      private RenderTarget sharedTemp;
      private int lastWidth = -1;
      private int lastHeight = -1;
      private boolean bloomDrawn;

      public Pipeline(ResourceLocation name) {
         super(name);
      }

      void handlePasses(RenderTarget src) {
         if (this.sharedBlur != null && this.sharedBlur_ != null && this.sharedTemp != null) {
            RenderSystem.texParameter(3553, 10242, 33071);
            RenderSystem.texParameter(3553, 10243, 33071);
            RenderSystem.texParameter(3553, 10240, 9729);
            RenderSystem.texParameter(3553, 10241, 9729);
            PostPasses.downSampler.process(src, this.sharedBlur[0]);
            PostPasses.downSampler.process(this.sharedBlur[0], this.sharedBlur[1]);
            PostPasses.downSampler.process(this.sharedBlur[1], this.sharedBlur[2]);
            PostPasses.downSampler.process(this.sharedBlur[2], this.sharedBlur[3]);
            PostPasses.downSampler.process(this.sharedBlur[3], this.sharedBlur[4]);
            PostPasses.upSampler.process(this.sharedBlur[4], this.sharedBlur_[3], this.sharedBlur[3]);
            PostPasses.upSampler.process(this.sharedBlur_[3], this.sharedBlur_[2], this.sharedBlur[2]);
            PostPasses.upSampler.process(this.sharedBlur_[2], this.sharedBlur_[1], this.sharedBlur[1]);
            PostPasses.upSampler.process(this.sharedBlur_[1], this.sharedBlur_[0], this.sharedBlur[0]);
            PostPasses.unity_composite.process(this.sharedBlur_[0], this.sharedTemp, src, Minecraft.m_91087_().m_91385_());
            PostPasses.blit.process(this.sharedTemp, Minecraft.m_91087_().m_91385_());
         }
      }

      void initTargets() {
         int cnt = 5;
         boolean needsResize = this.bufferTarget != null && (this.bufferTarget.f_83915_ != this.lastWidth || this.bufferTarget.f_83916_ != this.lastHeight);
         if (needsResize) {
            this.destroyBlurTargets();
         }

         try {
            if (this.sharedBlur == null) {
               this.sharedBlur = new RenderTarget[cnt];
               float s = 0.5F;

               for (int i = 0; i < this.sharedBlur.length; i++) {
                  this.sharedBlur[i] = new ScaledTarget(s, s, this.bufferTarget.f_83915_, this.bufferTarget.f_83916_, false, Minecraft.f_91002_);
                  this.sharedBlur[i].m_83931_(0.0F, 0.0F, 0.0F, 0.0F);
                  this.sharedBlur[i].m_83954_(Minecraft.f_91002_);
                  if (this.bufferTarget.isStencilEnabled()) {
                     this.sharedBlur[i].enableStencil();
                  }

                  s /= 2.0F;
               }
            }

            if (this.sharedBlur_ == null) {
               this.sharedBlur_ = new RenderTarget[cnt - 1];
               float s = 0.5F;

               for (int i = 0; i < this.sharedBlur_.length; i++) {
                  this.sharedBlur_[i] = new ScaledTarget(s, s, this.bufferTarget.f_83915_, this.bufferTarget.f_83916_, false, Minecraft.f_91002_);
                  this.sharedBlur_[i].m_83931_(0.0F, 0.0F, 0.0F, 0.0F);
                  this.sharedBlur_[i].m_83954_(Minecraft.f_91002_);
                  if (this.bufferTarget.isStencilEnabled()) {
                     this.sharedBlur_[i].enableStencil();
                  }

                  s /= 2.0F;
               }
            }

            if (this.sharedTemp == null) {
               this.sharedTemp = PostParticleRenderType.createTempTarget(this.bufferTarget);
            }
         } catch (RuntimeException var5) {
            DMCLog.warn(DMCLog.Category.RENDER, "BloomParticleRenderType: Failed to create render targets, bloom disabled.", var5);
            this.destroyBlurTargets();
            return;
         }

         if (this.bufferTarget != null) {
            this.lastWidth = this.bufferTarget.f_83915_;
            this.lastHeight = this.bufferTarget.f_83916_;
         }
      }

      private void destroyBlurTargets() {
         if (this.sharedBlur != null) {
            for (RenderTarget rt : this.sharedBlur) {
               if (rt != null) {
                  rt.m_83930_();
               }
            }

            this.sharedBlur = null;
         }

         if (this.sharedBlur_ != null) {
            for (RenderTarget rtx : this.sharedBlur_) {
               if (rtx != null) {
                  rtx.m_83930_();
               }
            }

            this.sharedBlur_ = null;
         }

         if (this.sharedTemp != null) {
            this.sharedTemp.m_83930_();
            this.sharedTemp = null;
         }

         this.lastWidth = -1;
         this.lastHeight = -1;
      }

      public void PostEffectHandler() {
         try {
            if (this.bufferTarget != null && this.bloomDrawn) {
               this.initTargets();
               this.handlePasses(this.bufferTarget);
               return;
            }
         } finally {
            this.bloomDrawn = false;
         }
      }

      public static void releaseCachedTargets() {
         if (BloomParticleRenderType.ppl instanceof BloomParticleRenderType.Pipeline p) {
            p.destroyBlurTargets();
         }
      }
   }
}
