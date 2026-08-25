package com.Yujin.onegradefixer.epicmoonmod.renderer;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class EMparticlerendertype {
   public static final Function<ResourceLocation, ParticleRenderType> TRAIL_EFFECT_ADDITIVE = Util.m_143827_(textureLocation -> new ParticleRenderType() {
         public void m_6505_(BufferBuilder buffer, TextureManager textureManager) {
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(SourceFactor.ONE, DestFactor.ONE);
            RenderSystem.depthMask(false);
            RenderSystem.setShader(GameRenderer::m_172829_);
            RenderSystem.setShaderTexture(0, textureLocation);
            buffer.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85813_);
         }

         public void m_6294_(Tesselator tesselator) {
            tesselator.m_85914_();
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
         }

         @Override
         public String toString() {
            return "epicmoonmod:TRAIL_EFFECT_ADDITIVE";
         }
      });
   public static final Function<ResourceLocation, ParticleRenderType> TRAIL_EFFECT_ADDITIVE2 = Util.m_143827_(textureLocation -> new ParticleRenderType() {
         public void m_6505_(BufferBuilder buffer, TextureManager textureManager) {
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
            RenderSystem.depthMask(false);
            RenderSystem.setShader(GameRenderer::m_172829_);
            RenderSystem.setShaderTexture(0, textureLocation);
            buffer.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85813_);
         }

         public void m_6294_(Tesselator tesselator) {
            tesselator.m_85914_();
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
         }

         @Override
         public String toString() {
            return "epicmoonmod:TRAIL_EFFECT_ADDITIVE2";
         }
      });
}
