package com.dmc.invincible_dmc.client.render.shaderpasses;

import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.guhao.vix.client.shaderpasses.PostPassBase;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import net.minecraft.server.packs.resources.ResourceManager;

public class ImpactBlurPass extends PostPassBase {
   public ImpactBlurPass(String resourceLocation, ResourceManager resmgr) throws IOException {
      super(resourceLocation, resmgr);
   }

   public void process(RenderTarget inTarget, RenderTarget outTarget, float centerX, float centerY, float intensity, float strength, int samples) {
      this.prevProcess(inTarget, outTarget);
      inTarget.m_83970_();
      RenderSystem.viewport(0, 0, outTarget.f_83915_, outTarget.f_83916_);
      this.effect.m_108954_("DiffuseSampler", inTarget::m_83975_);
      this.effect.m_108960_("ProjMat").m_5679_(PostEffectPipelines.shaderOrthoMatrix);
      this.effect.m_108960_("OutSize").m_7971_((float)outTarget.f_83915_, (float)outTarget.f_83916_);
      this.effect.m_108960_("center").m_7971_(centerX, centerY);
      this.effect.m_108960_("intensity").m_5985_(intensity);
      this.effect.m_108960_("strength").m_5985_(strength);
      this.effect.m_108960_("samples").m_142617_(samples);
      this.effect.m_108966_();
      this.pushVertex(inTarget, outTarget);
      this.effect.m_108965_();
      outTarget.m_83970_();
      inTarget.m_83963_();
   }
}
