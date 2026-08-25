package com.dmc.invincible_dmc.client.render.shaderpasses;

import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.guhao.vix.client.shaderpasses.PostPassBase;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import net.minecraft.server.packs.resources.ResourceManager;

public class BlackWhiteContrastSimple extends PostPassBase {
   public BlackWhiteContrastSimple(String resourceLocation, ResourceManager resmgr) throws IOException {
      super(resourceLocation, resmgr);
   }

   public void process(
      RenderTarget inTarget,
      RenderTarget outTarget,
      float contrast,
      float brightness,
      float time,
      float intensity,
      float speed,
      float mode,
      float impactThreshold,
      float impactThresholdLerp,
      float focalU,
      float focalV,
      float focalVisibility,
      float chromaticStrength,
      float lensDistortStrength
   ) {
      this.prevProcess(inTarget, outTarget);
      inTarget.m_83970_();
      RenderSystem.viewport(0, 0, outTarget.f_83915_, outTarget.f_83916_);
      this.effect.m_108954_("DiffuseSampler", inTarget::m_83975_);
      this.effect.m_108960_("ProjMat").m_5679_(PostEffectPipelines.shaderOrthoMatrix);
      this.effect.m_108960_("OutSize").m_7971_((float)outTarget.f_83915_, (float)outTarget.f_83916_);
      this.effect.m_108960_("Contrast").m_5985_(contrast);
      this.effect.m_108960_("Brightness").m_5985_(brightness);
      this.effect.m_108960_("Time").m_5985_(time);
      this.effect.m_108960_("Intensity").m_5985_(intensity);
      this.effect.m_108960_("Speed").m_5985_(speed);
      this.effect.m_108960_("Mode").m_5985_(mode);
      this.effect.m_108960_("ImpactThreshold").m_5985_(impactThreshold);
      this.effect.m_108960_("ImpactThresholdLerp").m_5985_(impactThresholdLerp);
      this.effect.m_108960_("FocalUV").m_7971_(focalU, focalV);
      this.effect.m_108960_("FocalVisibility").m_5985_(focalVisibility);
      this.effect.m_108960_("ChromaticStrength").m_5985_(chromaticStrength);
      this.effect.m_108960_("LensDistortStrength").m_5985_(lensDistortStrength);
      this.effect.m_108966_();
      this.pushVertex(inTarget, outTarget);
      this.effect.m_108965_();
      outTarget.m_83970_();
      inTarget.m_83963_();
   }

   public void process(
      RenderTarget inTarget,
      RenderTarget outTarget,
      float contrast,
      float brightness,
      float time,
      float intensity,
      float speed,
      float mode,
      float impactThreshold,
      float impactThresholdLerp,
      float focalU,
      float focalV
   ) {
      this.process(
         inTarget, outTarget, contrast, brightness, time, intensity, speed, mode, impactThreshold, impactThresholdLerp, focalU, focalV, 1.0F, 0.003F, -0.25F
      );
   }

   public void process(RenderTarget inTarget, RenderTarget outTarget, float contrast, float brightness, float time) {
      this.process(inTarget, outTarget, contrast, brightness, time, 0.8F, 1.0F, 1.0F, 0.45F, 0.15F, 0.5F, 0.5F);
   }

   public void process(RenderTarget inTarget, RenderTarget outTarget, float contrast, float brightness) {
      this.process(inTarget, outTarget, contrast, brightness, 0.0F);
   }
}
