package com.dmc.invincible_dmc.client.render.shaderpasses;

import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.guhao.vix.client.shaderpasses.PostPassBase;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import net.minecraft.server.packs.resources.ResourceManager;

public class RiftAttractionPass extends PostPassBase {
   public RiftAttractionPass(String resourceLocation, ResourceManager resourceManager) throws IOException {
      super(resourceLocation, resourceManager);
   }

   public void process(
      RenderTarget input,
      RenderTarget output,
      float startU,
      float startV,
      float endU,
      float endV,
      float strength,
      float radius,
      float intensity,
      float startTipFade,
      float endTipFade
   ) {
      this.prevProcess(input, output);
      input.m_83970_();
      RenderSystem.viewport(0, 0, output.f_83915_, output.f_83916_);
      this.effect.m_108954_("DiffuseSampler", input::m_83975_);
      this.effect.m_108960_("ProjMat").m_5679_(PostEffectPipelines.shaderOrthoMatrix);
      this.effect.m_108960_("OutSize").m_7971_((float)output.f_83915_, (float)output.f_83916_);
      this.effect.m_108960_("LineStart").m_7971_(startU, startV);
      this.effect.m_108960_("LineEnd").m_7971_(endU, endV);
      this.effect.m_108960_("Strength").m_5985_(strength);
      this.effect.m_108960_("Radius").m_5985_(radius);
      this.effect.m_108960_("Intensity").m_5985_(intensity);
      this.effect.m_108960_("StartTipFade").m_5985_(startTipFade);
      this.effect.m_108960_("EndTipFade").m_5985_(endTipFade);
      this.effect.m_108966_();
      this.pushVertex(input, output);
      this.effect.m_108965_();
      output.m_83970_();
      input.m_83963_();
   }
}
