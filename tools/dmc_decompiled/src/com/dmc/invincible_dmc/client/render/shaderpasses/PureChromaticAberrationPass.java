package com.dmc.invincible_dmc.client.render.shaderpasses;

import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.guhao.vix.client.shaderpasses.PostPassBase;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import net.minecraft.server.packs.resources.ResourceManager;

public class PureChromaticAberrationPass extends PostPassBase {
   public PureChromaticAberrationPass(String resourceLocation, ResourceManager resmgr) throws IOException {
      super(resourceLocation, resmgr);
   }

   public void process(RenderTarget inTarget, RenderTarget outTarget, float centerX, float centerY, float chromaIntensity, float chromaProgress) {
      this.prevProcess(inTarget, outTarget);
      inTarget.m_83970_();
      RenderSystem.viewport(0, 0, outTarget.f_83915_, outTarget.f_83916_);
      this.effect.m_108954_("DiffuseSampler", inTarget::m_83975_);
      this.effect.m_108960_("ProjMat").m_5679_(PostEffectPipelines.shaderOrthoMatrix);
      this.effect.m_108960_("OutSize").m_7971_((float)outTarget.f_83915_, (float)outTarget.f_83916_);
      this.effect.m_108960_("Center").m_7971_(centerX, centerY);
      this.effect.m_108960_("ChromaIntensity").m_5985_(chromaIntensity);
      this.effect.m_108960_("ChromaProgress").m_5985_(chromaProgress);
      this.effect.m_108966_();
      this.pushVertex(inTarget, outTarget);
      this.effect.m_108965_();
      outTarget.m_83970_();
      inTarget.m_83963_();
   }
}
