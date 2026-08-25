package com.dmc.invincible_dmc.client.render.custom;

import com.guhao.vix.client.pipeline.PostParticleRenderType;
import com.guhao.vix.util.RenderUtils;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexSorting;
import java.util.function.BooleanSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public abstract class ConfigurablePostParticleRenderType extends PostParticleRenderType {
   private final BooleanSupplier postProcessingEnabled;
   private boolean directRendering;

   protected ConfigurablePostParticleRenderType(ResourceLocation renderTypeId, ResourceLocation texture, BooleanSupplier postProcessingEnabled) {
      super(renderTypeId, texture);
      this.postProcessingEnabled = postProcessingEnabled;
   }

   protected final boolean isPostProcessingEnabled() {
      return this.postProcessingEnabled.getAsBoolean();
   }

   public void m_6505_(@NotNull BufferBuilder bufferBuilder, @NotNull TextureManager textureManager) {
      this.directRendering = !this.isPostProcessingEnabled();
      if (!this.directRendering) {
         super.m_6505_(bufferBuilder, textureManager);
      } else {
         RenderSystem.enableBlend();
         RenderSystem.disableCull();
         Minecraft.m_91087_().f_91063_.m_109154_().m_109896_();
         RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(true);
         RenderSystem.setShader(() -> this.getShader());
         if (this.texture != null) {
            RenderUtils.GLSetTexture(this.texture);
         }

         this.setupBufferBuilder(bufferBuilder);
      }
   }

   public void m_6294_(@NotNull Tesselator tesselator) {
      if (!this.directRendering) {
         super.m_6294_(tesselator);
      } else {
         tesselator.m_85915_().m_277127_(VertexSorting.f_276633_);
         tesselator.m_85914_();
         RenderSystem.depthMask(false);
         RenderSystem.disableDepthTest();
         RenderSystem.disableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.enableCull();
      }
   }

   public void callPipeline() {
      if (this.isPostProcessingEnabled()) {
         super.callPipeline();
      }
   }
}
