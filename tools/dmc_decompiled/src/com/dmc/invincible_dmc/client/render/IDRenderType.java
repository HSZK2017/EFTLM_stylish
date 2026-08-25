package com.dmc.invincible_dmc.client.render;

import com.dmc.invincible_dmc.client.render.custom.BloomParticleRenderType;
import com.dmc.invincible_dmc.client.render.custom.BloomTrailRenderType;
import com.dmc.invincible_dmc.client.render.custom.ChromaticAberrationEnhancedRenderType;
import com.dmc.invincible_dmc.client.render.custom.ChromaticAberrationRenderType;
import com.dmc.invincible_dmc.client.render.custom.EdgeGlowParticleRenderType;
import com.dmc.invincible_dmc.client.render.custom.SpaceBrokenRenderType;
import com.dmc.invincible_dmc.client.render.custom.StaticAirDisturbanceRenderType;
import com.google.common.collect.Maps;
import com.guhao.vix.util.OjangUtils;
import com.guhao.vix.util.RenderUtils;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.HashMap;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class IDRenderType {
   public static final HashMap<ResourceLocation, BloomParticleRenderType> BloomRenderTypes = Maps.newHashMap();
   public static final HashMap<ResourceLocation, BloomTrailRenderType> BloomRenderTrailTypes = Maps.newHashMap();
   public static final HashMap<ResourceLocation, EdgeGlowParticleRenderType> EdgeGlowRenderTypes = Maps.newHashMap();
   public static final HashMap<ResourceLocation, ChromaticAberrationRenderType> ChromaticRenderTypes = Maps.newHashMap();
   public static final HashMap<ResourceLocation, ChromaticAberrationEnhancedRenderType> EnhancedChromaticRenderTypes = Maps.newHashMap();
   public static final HashMap<ResourceLocation, StaticAirDisturbanceRenderType> StaticAirRenderTypes = Maps.newHashMap();
   public static final HashMap<ResourceLocation, IDRenderType.IDQuadParticleRenderType> QuadRenderTypes = Maps.newHashMap();
   public static SpaceBrokenRenderType SpaceBroken1 = new SpaceBrokenRenderType(OjangUtils.newRL("invincible_dmc", "space_broken"), 0);
   public static SpaceBrokenRenderType SpaceBroken2 = new SpaceBrokenRenderType(OjangUtils.newRL("invincible_dmc", "space_broken"), 1);
   public static SpaceBrokenRenderType SpaceBrokenEnd = new SpaceBrokenRenderType(
      OjangUtils.newRL("invincible_dmc", "space_broken_end"), GetTexture("particle/glass"), 0, 4
   );
   private static int bloomIdx = 0;
   private static int edgeGlowIdx = 0;
   private static int quadIdx = 0;
   private static int triangleIdx = 0;

   public static ResourceLocation GetTexture(String path) {
      return ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/" + path + ".png");
   }

   public static BloomParticleRenderType getBloomRenderTypeByTexture(ResourceLocation texture) {
      if (BloomRenderTypes.containsKey(texture)) {
         return BloomRenderTypes.get(texture);
      } else {
         BloomParticleRenderType bloomType = new BloomParticleRenderType(OjangUtils.newRL("invincible_dmc", "bp_" + bloomIdx++), texture);
         BloomRenderTypes.put(texture, bloomType);
         return bloomType;
      }
   }

   public static BloomTrailRenderType getBloomTrailRT(ResourceLocation texture) {
      if (BloomRenderTrailTypes.containsKey(texture)) {
         return BloomRenderTrailTypes.get(texture);
      } else {
         BloomTrailRenderType bloomType = new BloomTrailRenderType(OjangUtils.newRL("invincible_dmc", "bt_" + bloomIdx++), texture);
         BloomRenderTrailTypes.put(texture, bloomType);
         return bloomType;
      }
   }

   public static EdgeGlowParticleRenderType getEdgeGlowRenderType(ResourceLocation texture, float edgeIntensity, float glowIntensity, float glowRadius) {
      if (EdgeGlowRenderTypes.containsKey(texture)) {
         return EdgeGlowRenderTypes.get(texture);
      } else {
         EdgeGlowParticleRenderType type = new EdgeGlowParticleRenderType(
            OjangUtils.newRL("invincible_dmc", "eg_" + edgeGlowIdx++), texture, edgeIntensity, glowIntensity, glowRadius
         );
         EdgeGlowRenderTypes.put(texture, type);
         return type;
      }
   }

   public static EdgeGlowParticleRenderType getEdgeGlowRenderType(ResourceLocation texture) {
      return getEdgeGlowRenderType(texture, 0.8F, 0.9F, 4.0F);
   }

   public static IDRenderType.IDQuadParticleRenderType getRenderTypeByTexture(ResourceLocation texture) {
      if (QuadRenderTypes.containsKey(texture)) {
         return QuadRenderTypes.get(texture);
      } else {
         IDRenderType.IDQuadParticleRenderType rdt = new IDRenderType.IDQuadParticleRenderType("invincible_dmc:quad_particle_" + quadIdx++, texture);
         QuadRenderTypes.put(texture, rdt);
         return rdt;
      }
   }

   public static ChromaticAberrationRenderType ChromaticAberrationRenderType(ResourceLocation resourceLocation) {
      return ChromaticRenderTypes.computeIfAbsent(
         resourceLocation,
         texture -> new ChromaticAberrationRenderType(OjangUtils.newRL("invincible_dmc", "chromatic_aberration"), 0.18F, 0.15F, 1.0F, 0.5F, 0.5F, texture)
      );
   }

   public static ChromaticAberrationEnhancedRenderType enhancedChromaticAberrationRenderType(ResourceLocation resourceLocation) {
      return EnhancedChromaticRenderTypes.computeIfAbsent(resourceLocation, texture -> new ChromaticAberrationEnhancedRenderType(texture, texture));
   }

   public static StaticAirDisturbanceRenderType staticAirDisturbanceRenderType(ResourceLocation texture) {
      return StaticAirRenderTypes.computeIfAbsent(
         texture, value -> new StaticAirDisturbanceRenderType(OjangUtils.newRL("invincible_dmc", "static_air_trail"), value)
      );
   }

   public static class IDQuadParticleRenderType implements ParticleRenderType {
      private final ResourceLocation Texture;
      private final String Name;

      public IDQuadParticleRenderType(String name, ResourceLocation tex) {
         this.Texture = tex;
         this.Name = name;
      }

      public void m_6505_(@NotNull BufferBuilder p_107448_, @NotNull TextureManager p_107449_) {
         RenderSystem.enableBlend();
         RenderSystem.disableCull();
         RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(true);
         RenderSystem.setShader(GameRenderer::m_172829_);
         if (this.Texture != null) {
            RenderUtils.GLSetTexture(this.Texture);
         }

         p_107448_.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85813_);
      }

      public void m_6294_(Tesselator tesselator) {
         tesselator.m_85915_().m_277127_(VertexSorting.f_276633_);
         tesselator.m_85914_();
         RenderSystem.disableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.enableCull();
      }

      @Override
      public String toString() {
         return this.Name;
      }
   }
}
