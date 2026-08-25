package com.dmc.invincible_dmc.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderStateShard.TransparencyStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.resources.ResourceLocation;

public class SlashRenderStates extends RenderType {
   private static final Map<ResourceLocation, RenderType> SLASH_COLOR_WRITE_CACHE = new ConcurrentHashMap<>();
   private static final Map<ResourceLocation, RenderType> SLASH_COLOR_WRITE_OCULUS_CACHE = new ConcurrentHashMap<>();
   private static final Map<ResourceLocation, RenderType> SLASH_LUMINOUS_CACHE = new ConcurrentHashMap<>();
   private static final Map<ResourceLocation, RenderType> SLASH_LUMINOUS_OCULUS_CACHE = new ConcurrentHashMap<>();
   private static final Map<ResourceLocation, RenderType> ENTITY_TRANSLUCENT_DEPTH_WRITE_CACHE = new ConcurrentHashMap<>();
   private static final TransparencyStateShard SLASH_NORMAL_TRANSPARENCY = new TransparencyStateShard("invincible_slash_normal_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   private static final TransparencyStateShard SLASH_ADDITIVE_TRANSPARENCY = new TransparencyStateShard("invincible_slash_additive_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ONE, DestFactor.ZERO);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   private static final Map<ResourceLocation, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_DEPTH_WRITE_CACHE = new ConcurrentHashMap<>();

   public SlashRenderStates(
      String pName,
      VertexFormat pFormat,
      Mode pMode,
      int pBufferSize,
      boolean pAffectsCrumbling,
      boolean pSortOnUpload,
      Runnable pSetupState,
      Runnable pClearState
   ) {
      super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
   }

   public static RenderType getSlashColorWrite(ResourceLocation texture) {
      return SLASH_COLOR_WRITE_CACHE.computeIfAbsent(
         texture,
         tex -> RenderType.m_173215_(
               "invincible_slash_color_write_" + tex,
               DefaultVertexFormat.f_85812_,
               Mode.TRIANGLES,
               512,
               false,
               true,
               CompositeState.m_110628_()
                  .m_173292_(f_234323_)
                  .m_173290_(new TextureStateShard(tex, false, true))
                  .m_110685_(SLASH_NORMAL_TRANSPARENCY)
                  .m_110687_(f_110115_)
                  .m_110661_(f_110110_)
                  .m_110671_(f_110152_)
                  .m_110677_(f_110154_)
                  .m_110675_(f_110129_)
                  .m_110691_(false)
            )
      );
   }

   public static RenderType getSlashColorWriteOculus(ResourceLocation texture) {
      return SLASH_COLOR_WRITE_OCULUS_CACHE.computeIfAbsent(
         texture,
         tex -> RenderType.m_173215_(
               "invincible_slash_color_write_oculus_" + tex,
               DefaultVertexFormat.f_85812_,
               Mode.TRIANGLES,
               512,
               false,
               true,
               CompositeState.m_110628_()
                  .m_173292_(f_234323_)
                  .m_173290_(new TextureStateShard(tex, false, true))
                  .m_110685_(SLASH_NORMAL_TRANSPARENCY)
                  .m_110687_(f_110115_)
                  .m_110661_(f_110158_)
                  .m_110671_(f_110152_)
                  .m_110677_(f_110154_)
                  .m_110675_(f_110129_)
                  .m_110691_(false)
            )
      );
   }

   public static RenderType getSlashLuminous(ResourceLocation texture) {
      return SLASH_LUMINOUS_CACHE.computeIfAbsent(
         texture,
         tex -> RenderType.m_173215_(
               "invincible_slash_luminous_" + tex,
               DefaultVertexFormat.f_85812_,
               Mode.TRIANGLES,
               512,
               false,
               true,
               CompositeState.m_110628_()
                  .m_173292_(f_234323_)
                  .m_173290_(new TextureStateShard(tex, true, true))
                  .m_110685_(SLASH_ADDITIVE_TRANSPARENCY)
                  .m_110687_(RenderStateShard.f_110115_)
                  .m_110661_(f_110110_)
                  .m_110671_(f_110152_)
                  .m_110677_(f_110154_)
                  .m_110675_(f_110129_)
                  .m_110691_(false)
            )
      );
   }

   public static RenderType getSlashLuminousOculus(ResourceLocation texture) {
      return SLASH_LUMINOUS_OCULUS_CACHE.computeIfAbsent(
         texture,
         tex -> RenderType.m_173215_(
               "invincible_slash_luminous_oculus_" + tex,
               DefaultVertexFormat.f_85812_,
               Mode.TRIANGLES,
               512,
               false,
               true,
               CompositeState.m_110628_()
                  .m_173292_(f_173066_)
                  .m_173290_(new TextureStateShard(tex, true, true))
                  .m_110685_(SLASH_ADDITIVE_TRANSPARENCY)
                  .m_110687_(RenderStateShard.f_110114_)
                  .m_110661_(f_110158_)
                  .m_110671_(f_110152_)
                  .m_110677_(f_110154_)
                  .m_110675_(f_110129_)
                  .m_110691_(false)
            )
      );
   }

   public static RenderType getEntityTranslucentDepthWrite(ResourceLocation texture) {
      return ENTITY_TRANSLUCENT_DEPTH_WRITE_CACHE.computeIfAbsent(
         texture,
         tex -> RenderType.m_173215_(
               "invincible_dmc_entity_translucent_depth_write",
               DefaultVertexFormat.f_85812_,
               Mode.TRIANGLES,
               256,
               true,
               true,
               CompositeState.m_110628_()
                  .m_173292_(f_173066_)
                  .m_173290_(new TextureStateShard(tex, false, false))
                  .m_110685_(f_110139_)
                  .m_110687_(f_110114_)
                  .m_110661_(f_110158_)
                  .m_110671_(f_110152_)
                  .m_110677_(f_110154_)
                  .m_110691_(false)
            )
      );
   }

   public static RenderType getEntityTranslucentEmissiveDepthWrite(ResourceLocation texture) {
      return ENTITY_TRANSLUCENT_EMISSIVE_DEPTH_WRITE_CACHE.computeIfAbsent(
         texture,
         tex -> RenderType.m_173215_(
               "invincible_dmc_entity_translucent_emissive_depth_write",
               DefaultVertexFormat.f_85812_,
               Mode.TRIANGLES,
               256,
               true,
               true,
               CompositeState.m_110628_()
                  .m_173292_(f_234323_)
                  .m_173290_(new TextureStateShard(tex, false, false))
                  .m_110685_(f_110139_)
                  .m_110687_(f_110114_)
                  .m_110661_(f_110158_)
                  .m_110677_(f_110154_)
                  .m_110691_(false)
            )
      );
   }
}
