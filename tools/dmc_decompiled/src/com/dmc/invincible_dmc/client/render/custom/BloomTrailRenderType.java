package com.dmc.invincible_dmc.client.render.custom;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

public class BloomTrailRenderType extends BloomParticleRenderType {
   public BloomTrailRenderType(ResourceLocation renderTypeID, ResourceLocation tex) {
      super(renderTypeID, tex);
   }

   protected ShaderInstance getShader() {
      return GameRenderer.f_172586_;
   }

   public void setupBufferBuilder(BufferBuilder bufferBuilder) {
      bufferBuilder.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85813_);
   }
}
