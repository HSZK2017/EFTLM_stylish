package com.dmc.invincible_dmc.client.render.shaderpasses;

import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.guhao.vix.client.shaderpasses.PostPassBase;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.server.packs.resources.ResourceManager;

public class Blur extends PostPassBase {
   public Blur(ResourceManager rsmgr) throws IOException {
      super(new EffectInstance(rsmgr, "invincible_dmc:blur"));
   }

   public void process(RenderTarget inTarget, RenderTarget outTarget, float blurDirX, float blurDirY, int radius) {
      inTarget.m_83970_();
      RenderSystem.viewport(0, 0, outTarget.f_83915_, outTarget.f_83916_);
      this.effect.m_108954_("DiffuseSampler", inTarget::m_83975_);
      this.effect.m_108960_("ProjMat").m_5679_(PostEffectPipelines.shaderOrthoMatrix);
      this.effect.m_108960_("OutSize").m_7971_((float)outTarget.f_83915_, (float)outTarget.f_83916_);
      this.effect.m_108960_("BlurDir").m_7971_(blurDirX, blurDirY);
      this.effect.m_108960_("Radius").m_142617_(radius);
      this.effect.m_108966_();
      outTarget.m_83954_(Minecraft.f_91002_);
      outTarget.m_83947_(false);
      RenderSystem.depthFunc(519);
      BufferBuilder bufferbuilder = Tesselator.m_85913_().m_85915_();
      bufferbuilder.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85814_);
      bufferbuilder.m_5483_(0.0, 0.0, 700.0).m_5752_();
      bufferbuilder.m_5483_((double)inTarget.f_83915_, 0.0, 700.0).m_5752_();
      bufferbuilder.m_5483_((double)inTarget.f_83915_, (double)inTarget.f_83916_, 700.0).m_5752_();
      bufferbuilder.m_5483_(0.0, (double)inTarget.f_83916_, 700.0).m_5752_();
      RenderSystem.depthFunc(515);
      this.effect.m_108965_();
      outTarget.m_83970_();
      inTarget.m_83963_();
   }
}
