package com.dmc.invincible_dmc.client.renderer.patched.entity;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.client.effeks.EffekConfig;
import com.dmc.invincible_dmc.client.render.custom.SummonedSwordBloomPipeline;
import com.dmc.invincible_dmc.client.render.shader.DMCCoreShaders;
import com.dmc.invincible_dmc.entity.summonedsword.DMCSummonedSwordEntity;
import com.guhao.vix.client.compat.oculus.OculusShaderCompat;
import com.guhao.vix.client.model.ShaderSkinnedMesh;
import com.merlin204.avalon.entity.client.renderer.patch.entity.AvalonRendererPatch;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderStateShard.TransparencyStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Mesh.DrawingFunction;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public final class PSummonedSwordRenderer extends AvalonRendererPatch {
   private static final String RENDER_TYPE_NAME = "invincible_dmc_summoned_sword_turbulence";
   private static final int SHADER_BUFFER_SIZE = 131072;
   private static final float SHADER_LAYER_ALPHA = 0.66F;
   private static final float SHADER_LAYER_SCALE = 1.05F;
   private final BufferSource shaderBufferSource = MultiBufferSource.m_109898_(new BufferBuilder(131072));
   @Nullable
   private AssetAccessor<? extends SkinnedMesh> meshAssetAccessor;
   @Nullable
   private SkinnedMesh sourceMesh;
   @Nullable
   private ShaderSkinnedMesh shaderMesh;
   @Nullable
   private ShaderSkinnedMesh bloomMesh;
   @Nullable
   private ResourceLocation cachedTexture;
   @Nullable
   private RenderType backboneRenderType;
   @Nullable
   private RenderType bloomRenderType;
   @Nullable
   private RenderType shaderRenderType;
   @Nullable
   private RenderType shaderCompatRenderType;
   private boolean cachedShaderPackPipeline;

   public PSummonedSwordRenderer(Context context, EntityType<?> entityType) {
      super(context, entityType);
   }

   public void render(
      LivingEntity entity,
      LivingEntityPatch entityPatch,
      LivingEntityRenderer renderer,
      MultiBufferSource buffer,
      PoseStack poseStack,
      int packedLight,
      float partialTicks
   ) {
      if (entity instanceof DMCSummonedSwordEntity summonedSword) {
         if (!EffekConfig.isEnabled("render.summoned_sword_shader", DMConfig.SUMMONED_SWORD_SHADER)) {
            super.render(entity, entityPatch, renderer, buffer, poseStack, packedLight, partialTicks);
         } else {
            Armature armature = entityPatch.getArmature();
            AssetAccessor<? extends SkinnedMesh> meshAccessor = summonedSword.getMesh();
            ResourceLocation texture = summonedSword.getTexture();
            if (armature != null && meshAccessor != null && texture != null) {
               SkinnedMesh mesh = (SkinnedMesh)meshAccessor.get();
               if (mesh != null) {
                  this.meshAssetAccessor = meshAccessor;
                  this.prepareShaderMesh(mesh, texture);
                  if (this.shaderMesh != null
                     && this.bloomMesh != null
                     && this.backboneRenderType != null
                     && this.bloomRenderType != null
                     && this.shaderRenderType != null
                     && this.shaderCompatRenderType != null) {
                     poseStack.m_85836_();
                     this.mulPoseStack(poseStack, armature, entity, entityPatch, partialTicks);
                     this.setArmaturePose(entityPatch, armature, partialTicks);
                     if (!OculusShaderCompat.isShadowPass()) {
                        boolean shaderPackPipeline = OculusShaderCompat.shouldUseShaderPackPipeline();
                        RenderType activeRenderType = shaderPackPipeline ? this.shaderCompatRenderType : this.shaderRenderType;
                        float phaseSeed = shaderPackPipeline ? 1.0F : (float)(summonedSword.m_19879_() * 37 & 0xFF) / 255.0F;
                        poseStack.m_85836_();
                        poseStack.m_85841_(1.05F, 1.05F, 1.05F);
                        if (!shaderPackPipeline) {
                           this.applyLocalSpaceUniforms(poseStack);
                        }

                        this.shaderMesh
                           .draw(
                              poseStack,
                              this.shaderBufferSource,
                              activeRenderType,
                              DrawingFunction.NEW_ENTITY,
                              packedLight,
                              phaseSeed,
                              1.0F,
                              1.0F,
                              0.66F,
                              OverlayTexture.f_118083_,
                              armature,
                              armature.getPoseMatrices()
                           );
                        this.shaderBufferSource.m_109911_();
                        poseStack.m_85849_();
                     }

                     poseStack.m_85836_();
                     mesh.draw(
                        poseStack,
                        this.shaderBufferSource,
                        this.backboneRenderType,
                        DrawingFunction.NEW_ENTITY,
                        packedLight,
                        1.0F,
                        1.0F,
                        1.0F,
                        1.0F,
                        OverlayTexture.f_118083_,
                        armature,
                        armature.getPoseMatrices()
                     );
                     SummonedSwordBloomPipeline.queue(this.bloomMesh, this.bloomRenderType, poseStack, packedLight, armature, armature.getPoseMatrices());
                     poseStack.m_85849_();
                     this.shaderBufferSource.m_109911_();
                     this.renderLayer(renderer, entityPatch, entity, armature.getPoseMatrices(), buffer, poseStack, packedLight, partialTicks);
                     if (Minecraft.m_91087_().m_91290_().m_114377_()) {
                        entityPatch.getClientAnimator().renderDebuggingInfoForAllLayers(poseStack, buffer, partialTicks);
                     }

                     poseStack.m_85849_();
                  }
               }
            }
         }
      } else {
         super.render(entity, entityPatch, renderer, buffer, poseStack, packedLight, partialTicks);
      }
   }

   private void applyLocalSpaceUniforms(PoseStack poseStack) {
      ShaderInstance shader = DMCCoreShaders.getSummonedSwordTurbulence();
      if (shader != null) {
         Matrix4f poseMatrix = new Matrix4f(poseStack.m_85850_().m_252922_());
         Matrix4f inversePoseMatrix = new Matrix4f(poseMatrix).invert();
         AbstractUniform poseUniform = shader.m_173356_("PoseMat");
         if (poseUniform != null) {
            poseUniform.m_5679_(poseMatrix);
         }

         AbstractUniform inversePoseUniform = shader.m_173356_("InversePoseMat");
         if (inversePoseUniform != null) {
            inversePoseUniform.m_5679_(inversePoseMatrix);
         }
      }
   }

   private void prepareShaderMesh(SkinnedMesh mesh, ResourceLocation texture) {
      boolean shaderPackPipeline = OculusShaderCompat.shouldUseShaderPackPipeline();
      if (mesh != this.sourceMesh || !texture.equals(this.cachedTexture) || shaderPackPipeline != this.cachedShaderPackPipeline) {
         this.sourceMesh = mesh;
         this.cachedTexture = texture;
         this.cachedShaderPackPipeline = shaderPackPipeline;
         this.shaderMesh = new ShaderSkinnedMesh(mesh, DMCCoreShaders::getSummonedSwordTurbulence);
         this.bloomMesh = new ShaderSkinnedMesh(mesh, DMCCoreShaders::getSummonedSwordBloomMask);
         this.backboneRenderType = RenderType.m_110473_(texture);
         this.bloomRenderType = PSummonedSwordRenderer.SwordRenderType.createBloom(texture);
         this.shaderRenderType = PSummonedSwordRenderer.SwordRenderType.createNative(texture);
         this.shaderCompatRenderType = PSummonedSwordRenderer.SwordRenderType.createCompat(texture);
      }
   }

   public AssetAccessor<SkinnedMesh> getDefaultMesh() {
      return (AssetAccessor<SkinnedMesh>)(this.meshAssetAccessor != null && this.meshAssetAccessor.get() != null ? this.meshAssetAccessor : Meshes.BOOTS);
   }

   private static final class SwordRenderType extends RenderType {
      private static final TransparencyStateShard ADDITIVE_TRANSPARENCY = new TransparencyStateShard(
         "invincible_dmc_summoned_sword_turbulence_transparency", () -> {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ONE, DestFactor.ZERO);
         }, () -> {
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
         }
      );

      private SwordRenderType(
         String name, VertexFormat format, Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState
      ) {
         super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
      }

      private static RenderType createNative(ResourceLocation texture) {
         return create(
            "invincible_dmc_summoned_sword_turbulence", new ShaderStateShard(DMCCoreShaders::getSummonedSwordTurbulence), texture, ADDITIVE_TRANSPARENCY, false
         );
      }

      private static RenderType createCompat(ResourceLocation texture) {
         return create("invincible_dmc_summoned_sword_turbulence_compat", f_173066_, texture, ADDITIVE_TRANSPARENCY, false);
      }

      private static RenderType createBloom(ResourceLocation texture) {
         return create("invincible_dmc_summoned_sword_bloom_mask", new ShaderStateShard(DMCCoreShaders::getSummonedSwordBloomMask), texture, f_110139_, true);
      }

      private static RenderType create(
         String name, ShaderStateShard shaderState, ResourceLocation texture, TransparencyStateShard transparencyState, boolean sortOnUpload
      ) {
         CompositeState state = CompositeState.m_110628_()
            .m_173290_(new TextureStateShard(texture, false, false))
            .m_173292_(shaderState)
            .m_110685_(transparencyState)
            .m_110687_(f_110115_)
            .m_110671_(f_110152_)
            .m_110677_(f_110154_)
            .m_110661_(f_110110_)
            .m_110691_(false);
         return RenderType.m_173215_(name, DefaultVertexFormat.f_85812_, Mode.QUADS, 131072, false, sortOnUpload, state);
      }
   }
}
