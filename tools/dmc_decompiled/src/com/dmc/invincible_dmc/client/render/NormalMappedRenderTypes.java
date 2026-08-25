package com.dmc.invincible_dmc.client.render;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.client.render.shader.DMCCoreShaders;
import com.dmc.invincible_dmc.compat.oculus.OculusCompat;
import com.guhao.vix.client.compat.oculus.OculusShaderCompat;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderStateShard.TexturingStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.resources.ResourceLocation;

public final class NormalMappedRenderTypes extends RenderType {
   private static final int BUFFER_SIZE = 256;
   private static final Function<NormalMappedRenderTypes.PbrRenderKey, RenderType> PBR_ENTITY = Util.m_143827_(NormalMappedRenderTypes::createPbrEntity);

   private NormalMappedRenderTypes(
      String name, VertexFormat format, Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState
   ) {
      super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
   }

   public static RenderType pbrEntity(ResourceLocation baseTexture, PbrMaterialTextures material) {
      if (!isPbrEnabled(material) || OculusShaderCompat.isShadowPass()) {
         return RenderType.m_110458_(baseTexture);
      } else if (OculusShaderCompat.shouldUseShaderPackPipeline()) {
         ResourceLocation shaderPackTexture = material.shaderPackTexture();
         return RenderType.m_110458_(shaderPackTexture == null ? baseTexture : shaderPackTexture);
      } else {
         return DMCCoreShaders.getNormalMappedEntity() == null
            ? RenderType.m_110458_(baseTexture)
            : PBR_ENTITY.apply(new NormalMappedRenderTypes.PbrRenderKey(baseTexture, material));
      }
   }

   public static boolean isPbrEnabled(PbrMaterialTextures material) {
      return (Boolean)DMConfig.YAMATO_DMC5_BD_PBR.get()
         && material != null
         && material.isComplete()
         && (OculusCompat.isShaderActive() || (Boolean)DMConfig.YAMATO_DMC5_BD_PBR_WITHOUT_SHADER_PACK.get());
   }

   private static RenderType createPbrEntity(NormalMappedRenderTypes.PbrRenderKey key) {
      PbrMaterialTextures material = key.material();
      TexturingStateShard pbrTextureState = new TexturingStateShard("invincible_dmc_pbr_textures", () -> {
         bindTexture(3, material.normal() == null ? material.diffuse() : material.normal());
         bindTexture(4, material.diffuse());
         bindTexture(5, material.ambientOcclusion() == null ? material.diffuse() : material.ambientOcclusion());
         bindTexture(6, material.height() == null ? material.diffuse() : material.height());
         if (material.packedMer() != null) {
            bindTexture(7, material.packedMer());
            bindTexture(8, material.packedMer());
         } else {
            bindTexture(7, material.metallic());
            bindTexture(8, material.roughness());
         }

         setMaterialUniforms(material);
      }, () -> {
         for (int slot = 3; slot <= 8; slot++) {
            RenderSystem.setShaderTexture(slot, 0);
         }

         setMaterialUniforms(null);
      });
      CompositeState state = CompositeState.m_110628_()
         .m_173292_(new ShaderStateShard(DMCCoreShaders::getNormalMappedEntity))
         .m_173290_(new TextureStateShard(key.baseTexture(), false, false))
         .m_110685_(f_110134_)
         .m_110661_(f_110110_)
         .m_110671_(f_110152_)
         .m_110677_(f_110154_)
         .m_110683_(pbrTextureState)
         .m_110691_(true);
      return RenderType.m_173215_("invincible_dmc_pbr_entity", DefaultVertexFormat.f_85812_, Mode.QUADS, 256, true, false, state);
   }

   private static void bindTexture(int slot, ResourceLocation texture) {
      Minecraft.m_91087_().m_91097_().m_118506_(texture).m_117960_(false, false);
      RenderSystem.setShaderTexture(slot, texture);
   }

   private static void setMaterialUniforms(PbrMaterialTextures material) {
      ShaderInstance shader = DMCCoreShaders.getNormalMappedEntity();
      if (shader != null) {
         AbstractUniform packedMerUniform = shader.m_173356_("UsePackedMer");
         if (packedMerUniform != null) {
            packedMerUniform.m_142617_(material != null && material.packedMer() != null ? 1 : 0);
         }

         AbstractUniform normalMapUniform = shader.m_173356_("HasNormalMap");
         if (normalMapUniform != null) {
            normalMapUniform.m_142617_(material != null && material.normal() != null ? 1 : 0);
         }

         AbstractUniform heightMapUniform = shader.m_173356_("HasHeightMap");
         if (heightMapUniform != null) {
            heightMapUniform.m_142617_(material != null && material.height() != null ? 1 : 0);
         }

         AbstractUniform ambientOcclusionUniform = shader.m_173356_("HasAmbientOcclusion");
         if (ambientOcclusionUniform != null) {
            ambientOcclusionUniform.m_142617_(material != null && material.ambientOcclusion() != null ? 1 : 0);
         }
      }
   }

   private static record PbrRenderKey(ResourceLocation baseTexture, PbrMaterialTextures material) {
   }
}
