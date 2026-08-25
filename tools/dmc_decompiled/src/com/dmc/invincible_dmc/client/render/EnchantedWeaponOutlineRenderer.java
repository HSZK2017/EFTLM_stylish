package com.dmc.invincible_dmc.client.render;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.client.input.judegementCut.ClientJudgementCutController;
import com.dmc.invincible_dmc.client.render.shader.DMCCoreShaders;
import com.dmc.invincible_dmc.item.YamatoItem;
import com.guhao.vix.client.compat.oculus.OculusShaderCompat;
import com.guhao.vix.client.model.ShaderSkinnedMesh;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard.CullStateShard;
import net.minecraft.client.renderer.RenderStateShard.LightmapStateShard;
import net.minecraft.client.renderer.RenderStateShard.OverlayStateShard;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderStateShard.TexturingStateShard;
import net.minecraft.client.renderer.RenderStateShard.TransparencyStateShard;
import net.minecraft.client.renderer.RenderStateShard.WriteMaskStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;

@OnlyIn(Dist.CLIENT)
public final class EnchantedWeaponOutlineRenderer {
   private static final String RENDER_TYPE_NAME = "invincible_dmc_enchanted_weapon_outline";
   private static final float OUTLINE_RED = 0.53F;
   private static final float OUTLINE_GREEN = 0.08F;
   private static final float OUTLINE_BLUE = 1.0F;
   private static final float OUTLINE_LEVEL = 0.72F;
   private static final float SHADER_PACK_OUTLINE_SCALE = 1.02133F;
   private static final float CHARGING_BLUE_RED = 0.05F;
   private static final float CHARGING_BLUE_GREEN = 0.3F;
   private static final float CHARGING_BLUE_BLUE = 1.0F;
   private static final float CHARGING_OUTLINE_LEVEL = 1.0F;
   private static final float CHARGING_SHADER_PACK_OUTLINE_SCALE = 1.027F;
   private static final long ENCHANTED_CHARGING_COLOR_CYCLE_MILLIS = 120L;
   private static final int SHADER_PACK_BUFFER_SIZE = 256;
   private static final EnchantedWeaponOutlineRenderer.OutlineVisual ENCHANTED_OUTLINE = new EnchantedWeaponOutlineRenderer.OutlineVisual(
      0.53F, 0.08F, 1.0F, 0.72F, 1.02133F
   );
   private static final EnchantedWeaponOutlineRenderer.OutlineVisual CHARGING_BLUE_OUTLINE = new EnchantedWeaponOutlineRenderer.OutlineVisual(
      0.05F, 0.3F, 1.0F, 1.0F, 1.027F
   );
   private static final ShaderStateShard SHADER_PACK_OUTLINE_SHADER = new ShaderStateShard(GameRenderer::m_234223_);
   private static final TransparencyStateShard ADDITIVE_TRANSPARENCY = new TransparencyStateShard(
      "invincible_dmc_enchanted_weapon_outline_additive_transparency", () -> {
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ZERO, DestFactor.ONE);
      }, () -> {
         RenderSystem.disableBlend();
         RenderSystem.defaultBlendFunc();
      }
   );
   private static final CullStateShard CULL = new CullStateShard(true);
   private static final LightmapStateShard NO_LIGHTMAP = new LightmapStateShard(false);
   private static final OverlayStateShard OVERLAY = new OverlayStateShard(true);
   private static final WriteMaskStateShard COLOR_WRITE = new WriteMaskStateShard(true, false);
   private static final TexturingStateShard FRONT_FACE_CULLING = new TexturingStateShard("invincible_dmc_enchanted_weapon_outline_front_face_culling", () -> {
      RenderSystem.enableCull();
      GL11.glCullFace(1028);
   }, () -> GL11.glCullFace(1029));
   private static final TexturingStateShard BACK_FACE_CULLING = new TexturingStateShard("invincible_dmc_enchanted_weapon_outline_back_face_culling", () -> {
      RenderSystem.enableCull();
      GL11.glCullFace(1029);
   }, () -> GL11.glCullFace(1029));
   private static final Map<SkinnedMesh, ShaderSkinnedMesh> OUTLINE_MESHES = Collections.synchronizedMap(new IdentityHashMap<>());
   private static final Map<SkinnedMesh, ShaderSkinnedMesh> MIRRORED_OUTLINE_MESHES = Collections.synchronizedMap(new IdentityHashMap<>());
   private static final Map<ResourceLocation, RenderType> OUTLINE_RENDER_TYPES = new ConcurrentHashMap<>();
   private static final Map<ResourceLocation, RenderType> MIRRORED_OUTLINE_RENDER_TYPES = new ConcurrentHashMap<>();
   private static final Map<ResourceLocation, RenderType> SHADER_PACK_OUTLINE_RENDER_TYPES = new ConcurrentHashMap<>();
   private static final Map<ResourceLocation, RenderType> MIRRORED_SHADER_PACK_OUTLINE_RENDER_TYPES = new ConcurrentHashMap<>();

   private EnchantedWeaponOutlineRenderer() {
   }

   public static void render(
      ItemStack stack,
      SkinnedMesh mesh,
      ResourceLocation texture,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      Armature armature,
      OpenMatrix4f[] poses
   ) {
      render(stack, mesh, texture, poseStack, buffer, packedLight, armature, poses, EnchantedWeaponOutlineRenderer.OutlineProfile.STANDARD);
   }

   public static void render(
      ItemStack stack,
      SkinnedMesh mesh,
      ResourceLocation texture,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      Armature armature,
      OpenMatrix4f[] poses,
      boolean allowJudgementCutOutline
   ) {
      render(
         stack,
         mesh,
         texture,
         poseStack,
         buffer,
         packedLight,
         armature,
         poses,
         allowJudgementCutOutline ? EnchantedWeaponOutlineRenderer.OutlineProfile.STANDARD : EnchantedWeaponOutlineRenderer.OutlineProfile.NO_JUDGEMENT_CUT
      );
   }

   public static void render(
      ItemStack stack,
      SkinnedMesh mesh,
      ResourceLocation texture,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      Armature armature,
      OpenMatrix4f[] poses,
      EnchantedWeaponOutlineRenderer.OutlineProfile outlineProfile
   ) {
      if (stack != null
         && mesh != null
         && texture != null
         && armature != null
         && poses != null
         && isOutlineEnabled(stack, outlineProfile)
         && !OculusShaderCompat.isShadowPass()) {
         if (!OculusShaderCompat.shouldUseShaderPackPipeline()) {
            boolean mirrored = false;
            if (mirrored ? DMCCoreShaders.getEnchantedWeaponOutlineMirrored() != null : DMCCoreShaders.getEnchantedWeaponOutline() != null) {
               EnchantedWeaponOutlineRenderer.OutlineVisual outline = resolveOutlineVisual(stack.m_41790_(), outlineProfile);
               if (outline != null) {
                  Map<SkinnedMesh, ShaderSkinnedMesh> outlineMeshes = mirrored ? MIRRORED_OUTLINE_MESHES : OUTLINE_MESHES;
                  ShaderSkinnedMesh outlineMesh;
                  synchronized (outlineMeshes) {
                     outlineMesh = outlineMeshes.computeIfAbsent(
                        mesh,
                        sourceMesh -> new ShaderSkinnedMesh(
                              sourceMesh, mirrored ? DMCCoreShaders::getEnchantedWeaponOutlineMirrored : DMCCoreShaders::getEnchantedWeaponOutline
                           )
                     );
                  }

                  Map<ResourceLocation, RenderType> outlineRenderTypes = mirrored ? MIRRORED_OUTLINE_RENDER_TYPES : OUTLINE_RENDER_TYPES;
                  RenderType renderType = outlineRenderTypes.computeIfAbsent(
                     texture, mirrored ? EnchantedWeaponOutlineRenderer::createMirroredRenderType : EnchantedWeaponOutlineRenderer::createRenderType
                  );
                  outlineMesh.draw(
                     poseStack,
                     buffer,
                     renderType,
                     packedLight,
                     outline.red(),
                     outline.green(),
                     outline.blue(),
                     outline.level(),
                     OverlayTexture.f_118083_,
                     armature,
                     poses
                  );
               }
            }
         }
      }
   }

   public static void renderBeforeBase(
      ItemStack stack,
      SkinnedMesh mesh,
      ResourceLocation texture,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      Armature armature,
      OpenMatrix4f[] poses
   ) {
      renderBeforeBase(stack, mesh, texture, poseStack, buffer, packedLight, armature, poses, EnchantedWeaponOutlineRenderer.OutlineProfile.STANDARD);
   }

   public static void renderBeforeBase(
      ItemStack stack,
      SkinnedMesh mesh,
      ResourceLocation texture,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      Armature armature,
      OpenMatrix4f[] poses,
      boolean allowJudgementCutOutline
   ) {
      renderBeforeBase(
         stack,
         mesh,
         texture,
         poseStack,
         buffer,
         packedLight,
         armature,
         poses,
         allowJudgementCutOutline ? EnchantedWeaponOutlineRenderer.OutlineProfile.STANDARD : EnchantedWeaponOutlineRenderer.OutlineProfile.NO_JUDGEMENT_CUT
      );
   }

   public static void renderBeforeBase(
      ItemStack stack,
      SkinnedMesh mesh,
      ResourceLocation texture,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      Armature armature,
      OpenMatrix4f[] poses,
      EnchantedWeaponOutlineRenderer.OutlineProfile outlineProfile
   ) {
      if (stack != null
         && mesh != null
         && texture != null
         && armature != null
         && poses != null
         && isOutlineEnabled(stack, outlineProfile)
         && OculusShaderCompat.shouldUseShaderPackPipeline()
         && !OculusShaderCompat.isShadowPass()) {
         EnchantedWeaponOutlineRenderer.OutlineVisual outline = resolveOutlineVisual(stack.m_41790_(), outlineProfile);
         if (outline != null) {
            boolean mirrored = false;
            Map<ResourceLocation, RenderType> renderTypes = mirrored ? MIRRORED_SHADER_PACK_OUTLINE_RENDER_TYPES : SHADER_PACK_OUTLINE_RENDER_TYPES;
            RenderType renderType = renderTypes.computeIfAbsent(
               texture,
               mirrored ? EnchantedWeaponOutlineRenderer::createMirroredShaderPackRenderType : EnchantedWeaponOutlineRenderer::createShaderPackRenderType
            );
            poseStack.m_85836_();

            try {
               poseStack.m_85841_(outline.shaderPackScale(), outline.shaderPackScale(), outline.shaderPackScale());
               mesh.draw(
                  poseStack, buffer, renderType, 15728880, outline.red(), outline.green(), outline.blue(), 1.0F, OverlayTexture.f_118083_, armature, poses
               );
            } finally {
               poseStack.m_85849_();
            }
         }
      }
   }

   public static void clearCaches() {
      synchronized (OUTLINE_MESHES) {
         OUTLINE_MESHES.clear();
      }

      synchronized (MIRRORED_OUTLINE_MESHES) {
         MIRRORED_OUTLINE_MESHES.clear();
      }

      OUTLINE_RENDER_TYPES.clear();
      MIRRORED_OUTLINE_RENDER_TYPES.clear();
      SHADER_PACK_OUTLINE_RENDER_TYPES.clear();
      MIRRORED_SHADER_PACK_OUTLINE_RENDER_TYPES.clear();
   }

   private static EnchantedWeaponOutlineRenderer.OutlineVisual resolveOutlineVisual(
      boolean hasFoil, EnchantedWeaponOutlineRenderer.OutlineProfile outlineProfile
   ) {
      boolean judgementCutCharging = outlineProfile == EnchantedWeaponOutlineRenderer.OutlineProfile.STANDARD && ClientJudgementCutController.isAnyJCCharging();
      if (judgementCutCharging) {
         return CHARGING_BLUE_OUTLINE;
      } else {
         return hasFoil ? ENCHANTED_OUTLINE : null;
      }
   }

   private static float fastPurpleBlend() {
      float phase = (float)(System.currentTimeMillis() % 120L) / 120.0F;
      return 0.5F + 0.5F * (float)Math.sin((double)phase * Math.PI * 2.0);
   }

   private static float interpolate(float start, float end, float progress) {
      return start + (end - start) * progress;
   }

   private static RenderType createRenderType(ResourceLocation texture) {
      return ShaderSkinnedMesh.createRenderType("invincible_dmc_enchanted_weapon_outline", texture, DMCCoreShaders::getEnchantedWeaponOutline, true);
   }

   private static boolean isOutlineEnabled(ItemStack stack, EnchantedWeaponOutlineRenderer.OutlineProfile outlineProfile) {
      return stack.m_41720_() instanceof YamatoItem ? (Boolean)DMConfig.YAMATO_MODEL_OUTLINE.get() : true;
   }

   private static RenderType createMirroredRenderType(ResourceLocation texture) {
      return ShaderSkinnedMesh.createRenderType(
         "invincible_dmc_enchanted_weapon_outline_mirrored", texture, DMCCoreShaders::getEnchantedWeaponOutlineMirrored, true
      );
   }

   private static RenderType createShaderPackRenderType(ResourceLocation texture) {
      return EnchantedWeaponOutlineRenderer.ShaderPackOutlineRenderType.create(texture, false);
   }

   private static RenderType createMirroredShaderPackRenderType(ResourceLocation texture) {
      return EnchantedWeaponOutlineRenderer.ShaderPackOutlineRenderType.create(texture, true);
   }

   public static enum OutlineProfile {
      STANDARD,
      NO_JUDGEMENT_CUT;
   }

   private static record OutlineVisual(float red, float green, float blue, float level, float shaderPackScale) {
   }

   private static final class ShaderPackOutlineRenderType extends RenderType {
      private ShaderPackOutlineRenderType(
         String name, VertexFormat format, Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState
      ) {
         super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
      }

      private static RenderType create(ResourceLocation texture, boolean mirrored) {
         CompositeState state = CompositeState.m_110628_()
            .m_173290_(new TextureStateShard(texture, false, false))
            .m_173292_(EnchantedWeaponOutlineRenderer.SHADER_PACK_OUTLINE_SHADER)
            .m_110685_(f_110135_)
            .m_110661_(f_110158_)
            .m_110671_(f_110153_)
            .m_110677_(f_110154_)
            .m_110675_(f_110129_)
            .m_110687_(f_110115_)
            .m_110683_(mirrored ? EnchantedWeaponOutlineRenderer.BACK_FACE_CULLING : EnchantedWeaponOutlineRenderer.FRONT_FACE_CULLING)
            .m_110691_(false);
         return RenderType.m_173215_("invincible_dmc_enchanted_weapon_outline_shader_pack", DefaultVertexFormat.f_85812_, Mode.QUADS, 256, false, true, state);
      }
   }
}
