package com.dmc.invincible_dmc.client.renderer.patched.entity;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.client.compat.CosmeticRenderCompat;
import com.dmc.invincible_dmc.event.SdtArmorHandler;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.MinecraftForge;
import yesman.epicfight.api.client.forgeevent.PrepareModelEvent;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.entity.PPlayerRenderer;
import yesman.epicfight.client.renderer.patched.layer.EmptyLayer;
import yesman.epicfight.client.renderer.patched.layer.PatchedLayer;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;
import yesman.epicfight.mixin.client.MixinLivingEntityRenderer;

public class PSdtPlayerRenderer extends PPlayerRenderer {
   public static final float SDT_MODEL_SCALE = 1.1F;
   private static final EmptyLayer<AbstractClientPlayer, AbstractClientPlayerPatch<AbstractClientPlayer>, PlayerModel<AbstractClientPlayer>> HIDDEN_SDT_LAYER = new EmptyLayer();
   public static final ResourceLocation SDT_BASE = ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/mesh/sin_devil/sin_devil.png");
   public static final ResourceLocation[] SDT_GLOW_FRAMES = new ResourceLocation[]{
      ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/mesh/sin_devil/sin_devil_s_1.png"),
      ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/mesh/sin_devil/sin_devil_s_2.png"),
      ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/mesh/sin_devil/sin_devil_s_3.png"),
      ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/mesh/sin_devil/sin_devil_s_4.png"),
      ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/mesh/sin_devil/sin_devil_s_5.png")
   };
   public static MeshAccessor<HumanoidMesh> SIN_DEVIL_ARMOR = MeshAccessor.create(
      "invincible_dmc", "entity/sin_devil_armor", jsonModelLoader -> (HumanoidMesh)jsonModelLoader.loadSkinnedMesh(HumanoidMesh::new)
   );

   public static ResourceLocation[] getAllSdtTextures() {
      ResourceLocation[] all = new ResourceLocation[1 + SDT_GLOW_FRAMES.length];
      all[0] = SDT_BASE;
      System.arraycopy(SDT_GLOW_FRAMES, 0, all, 1, SDT_GLOW_FRAMES.length);
      return all;
   }

   private static boolean isSdtRendererEnabled() {
      return (Boolean)DMConfig.SDT_PLAYER_RENDERER.get();
   }

   public static boolean shouldRenderSdtMesh(AbstractClientPlayer entity) {
      return isSdtRendererEnabled()
         && (SinDevilTriggerManager.isPlayerInSDT(entity) || SdtArmorHandler.isSdtArmorActive(entity) || SdtRenderTransitionManager.shouldRenderSdt(entity));
   }

   public PSdtPlayerRenderer(Context context, EntityType<?> entityType) {
      super(context, entityType);
   }

   protected void renderLayer(
      LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer,
      AbstractClientPlayerPatch<AbstractClientPlayer> entityPatch,
      AbstractClientPlayer entity,
      OpenMatrix4f[] poses,
      MultiBufferSource buffer,
      PoseStack poseStack,
      int packedLight,
      float partialTicks
   ) {
      if (isSdtRendererEnabled()) {
         if (!SinDevilTriggerManager.isPlayerInSDT(entity) && !SdtArmorHandler.isWearingSdtArmor(entity)) {
            super.renderLayer(renderer, entityPatch, entity, poses, buffer, poseStack, packedLight, partialTicks);
         } else {
            PatchedLayer<AbstractClientPlayer, AbstractClientPlayerPatch<AbstractClientPlayer>, PlayerModel<AbstractClientPlayer>, ? extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>> prevArmor = (PatchedLayer<AbstractClientPlayer, AbstractClientPlayerPatch<AbstractClientPlayer>, PlayerModel<AbstractClientPlayer>, ? extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>>)this.patchedLayers
               .put(HumanoidArmorLayer.class, HIDDEN_SDT_LAYER);
            PatchedLayer<AbstractClientPlayer, AbstractClientPlayerPatch<AbstractClientPlayer>, PlayerModel<AbstractClientPlayer>, ? extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>> prevElytra = (PatchedLayer<AbstractClientPlayer, AbstractClientPlayerPatch<AbstractClientPlayer>, PlayerModel<AbstractClientPlayer>, ? extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>>)this.patchedLayers
               .put(ElytraLayer.class, HIDDEN_SDT_LAYER);
            PatchedLayer<AbstractClientPlayer, AbstractClientPlayerPatch<AbstractClientPlayer>, PlayerModel<AbstractClientPlayer>, ? extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>> prevCape = (PatchedLayer<AbstractClientPlayer, AbstractClientPlayerPatch<AbstractClientPlayer>, PlayerModel<AbstractClientPlayer>, ? extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>>)this.patchedLayers
               .put(CapeLayer.class, HIDDEN_SDT_LAYER);
            PatchedLayer<AbstractClientPlayer, AbstractClientPlayerPatch<AbstractClientPlayer>, PlayerModel<AbstractClientPlayer>, ? extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>> prevArrow = (PatchedLayer<AbstractClientPlayer, AbstractClientPlayerPatch<AbstractClientPlayer>, PlayerModel<AbstractClientPlayer>, ? extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>>)this.patchedLayers
               .put(ArrowLayer.class, HIDDEN_SDT_LAYER);
            PatchedLayer<AbstractClientPlayer, AbstractClientPlayerPatch<AbstractClientPlayer>, PlayerModel<AbstractClientPlayer>, ? extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>> prevBee = (PatchedLayer<AbstractClientPlayer, AbstractClientPlayerPatch<AbstractClientPlayer>, PlayerModel<AbstractClientPlayer>, ? extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>>)this.patchedLayers
               .put(BeeStingerLayer.class, HIDDEN_SDT_LAYER);

            try {
               super.renderLayer(renderer, entityPatch, entity, poses, buffer, poseStack, packedLight, partialTicks);
            } finally {
               this.restoreLayer(HumanoidArmorLayer.class, prevArmor);
               this.restoreLayer(ElytraLayer.class, prevElytra);
               this.restoreLayer(CapeLayer.class, prevCape);
               this.restoreLayer(ArrowLayer.class, prevArrow);
               this.restoreLayer(BeeStingerLayer.class, prevBee);
            }
         }
      } else {
         super.renderLayer(renderer, entityPatch, entity, poses, buffer, poseStack, packedLight, partialTicks);
      }
   }

   private void restoreLayer(
      Class<?> layerClass, PatchedLayer<AbstractClientPlayer, AbstractClientPlayerPatch<AbstractClientPlayer>, PlayerModel<AbstractClientPlayer>, ?> previous
   ) {
      if (previous == null) {
         this.patchedLayers.remove(layerClass);
      } else {
         this.patchedLayers.put(layerClass, previous);
      }
   }

   protected void prepareModel(
      HumanoidMesh mesh, AbstractClientPlayer entity, AbstractClientPlayerPatch<AbstractClientPlayer> entitypatch, PlayerRenderer renderer
   ) {
      super.prepareModel(mesh, entity, entitypatch, renderer);
      if (isSdtRendererEnabled()) {
         if (SinDevilTriggerManager.isPlayerInSDT(entity)) {
            mesh.head.setHidden(true);
            mesh.hat.setHidden(true);
            mesh.jacket.setHidden(true);
            mesh.torso.setHidden(true);
            mesh.leftArm.setHidden(true);
            mesh.leftLeg.setHidden(true);
            mesh.leftPants.setHidden(true);
            mesh.leftSleeve.setHidden(true);
            mesh.rightArm.setHidden(true);
            mesh.rightLeg.setHidden(true);
            mesh.rightPants.setHidden(true);
            mesh.rightSleeve.setHidden(true);
         }
      }
   }

   public void render(
      AbstractClientPlayer entity,
      AbstractClientPlayerPatch<AbstractClientPlayer> entityPatch,
      PlayerRenderer renderer,
      MultiBufferSource buffer,
      PoseStack poseStack,
      int packedLight,
      float partialTicks
   ) {
      if (!isSdtRendererEnabled()) {
         super.render(entity, entityPatch, renderer, buffer, poseStack, packedLight, partialTicks);
      } else {
         SdtRenderTransitionManager.update(entity);
         boolean isInSdt = SinDevilTriggerManager.isPlayerInSDT(entity) || SdtArmorHandler.isSdtArmorActive(entity);
         boolean shouldRenderSdt = SdtRenderTransitionManager.shouldRenderSdt(entity);
         boolean hasFiguraAvatar = CosmeticRenderCompat.hasFiguraAvatar(entity);
         if (hasFiguraAvatar && !isInSdt) {
            super.render(entity, entityPatch, renderer, buffer, poseStack, packedLight, partialTicks);
         } else if (!isInSdt && !shouldRenderSdt) {
            super.render(entity, entityPatch, renderer, buffer, poseStack, packedLight, partialTicks);
         } else {
            float sdtAlpha = SdtRenderTransitionManager.getSdtAlpha(entity);
            float r = SdtRenderTransitionManager.getRed(entity);
            float g = SdtRenderTransitionManager.getGreen(entity);
            float b = SdtRenderTransitionManager.getBlue(entity);
            if (hasFiguraAvatar) {
               sdtAlpha = 1.0F;
            }

            boolean showUnderlay = !isInSdt || sdtAlpha < 1.0F;
            if (showUnderlay) {
               poseStack.m_85836_();
               super.render(entity, entityPatch, renderer, buffer, poseStack, packedLight, partialTicks);
               poseStack.m_85849_();
            }

            this.renderSdtModel(entity, entityPatch, renderer, buffer, poseStack, packedLight, partialTicks, sdtAlpha, r, g, b);
         }
      }
   }

   private void renderSdtModel(
      AbstractClientPlayer entity,
      AbstractClientPlayerPatch<AbstractClientPlayer> entityPatch,
      PlayerRenderer renderer,
      MultiBufferSource buffer,
      PoseStack poseStack,
      int packedLight,
      float partialTicks,
      float sdtAlpha,
      float r,
      float g,
      float b
   ) {
      Minecraft mc = Minecraft.m_91087_();
      MixinLivingEntityRenderer accessor = (MixinLivingEntityRenderer)renderer;
      boolean isVisible = accessor.invokeIsBodyVisible(entity);
      boolean isVisibleToPlayer = !isVisible && mc.f_91074_ != null && !entity.m_20177_(mc.f_91074_);
      HumanoidMesh mesh = (HumanoidMesh)SIN_DEVIL_ARMOR.get();
      Armature armature = entityPatch.getArmature();
      int frame = (int)(System.currentTimeMillis() / 70L % 5L);
      RenderType baseType = RenderType.m_110473_(SDT_BASE);
      RenderType glowType = RenderType.m_234338_(SDT_GLOW_FRAMES[frame]);
      poseStack.m_85836_();
      poseStack.m_85841_(1.1F, 1.1F, 1.1F);
      this.mulPoseStack(poseStack, armature, entity, entityPatch, partialTicks);
      this.prepareVanillaModel(entity, (PlayerModel)renderer.m_7200_(), renderer, partialTicks);
      this.setArmaturePose(entityPatch, armature, partialTicks);
      PrepareModelEvent event = new PrepareModelEvent(this, mesh, entityPatch, buffer, poseStack, packedLight, partialTicks);
      if (!MinecraftForge.EVENT_BUS.post(event)) {
         float visibilityAlpha = isVisibleToPlayer ? 0.15F : 1.0F;
         float finalAlpha = sdtAlpha * visibilityAlpha;
         mesh.draw(poseStack, buffer, baseType, packedLight, r, g, b, finalAlpha, OverlayTexture.f_118083_, armature, armature.getPoseMatrices());
         poseStack.m_85836_();
         mesh.draw(poseStack, buffer, glowType, packedLight, r, g, b, finalAlpha * 0.9F, OverlayTexture.f_118083_, armature, armature.getPoseMatrices());
         poseStack.m_85849_();
      }

      if (!entity.m_5833_()) {
         this.renderLayer(renderer, entityPatch, entity, armature.getPoseMatrices(), buffer, poseStack, packedLight, partialTicks);
      }

      if (Minecraft.m_91087_().m_91290_().m_114377_()) {
         entityPatch.getClientAnimator().renderDebuggingInfoForAllLayers(poseStack, buffer, partialTicks);
      }

      poseStack.m_85849_();
   }
}
