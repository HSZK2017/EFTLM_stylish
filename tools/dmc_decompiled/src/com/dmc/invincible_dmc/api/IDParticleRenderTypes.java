package com.dmc.invincible_dmc.api;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import org.jetbrains.annotations.NotNull;

public interface IDParticleRenderTypes {
   ParticleRenderType ID_PARTICLE_MODEL_NO_NORMAL = new ParticleRenderType() {
      public void m_6505_(BufferBuilder bufferBuilder, @NotNull TextureManager textureManager) {
         RenderSystem.disableCull();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.depthMask(true);
         RenderSystem.setShader(GameRenderer::m_172829_);
         bufferBuilder.m_166779_(Mode.TRIANGLES, DefaultVertexFormat.f_85813_);
      }

      public void m_6294_(Tesselator tesselator) {
         tesselator.m_85914_();
         RenderSystem.disableBlend();
         RenderSystem.enableCull();
      }

      @Override
      public String toString() {
         return "ID_PARTICLE_MODEL_NO_NORMAL";
      }
   };
}
