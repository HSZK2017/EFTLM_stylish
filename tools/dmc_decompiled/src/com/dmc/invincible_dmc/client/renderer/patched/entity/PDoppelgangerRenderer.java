package com.dmc.invincible_dmc.client.renderer.patched.entity;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.client.renderer.SdtWeaponRenderer;
import com.dmc.invincible_dmc.client.renderer.SlashRenderStates;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.item.DMCItems;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.entity.PHumanoidRenderer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class PDoppelgangerRenderer
   extends PHumanoidRenderer<Mob, LivingEntityPatch<Mob>, HumanoidModel<Mob>, HumanoidMobRenderer<Mob, HumanoidModel<Mob>>, HumanoidMesh> {
   private static final ResourceLocation[] SDT_WHITE_GLOW_FRAMES = new ResourceLocation[]{
      ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/mesh/sin_devil/sin_devil_white_1.png"),
      ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/mesh/sin_devil/sin_devil_white_2.png"),
      ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/mesh/sin_devil/sin_devil_white_3.png"),
      ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/mesh/sin_devil/sin_devil_white_4.png"),
      ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/mesh/sin_devil/sin_devil_white_5.png")
   };

   public PDoppelgangerRenderer(Context context, EntityType<?> entityType) {
      super(Meshes.BIPED, context, entityType);
   }

   public AssetAccessor<HumanoidMesh> getMeshProvider(LivingEntityPatch<Mob> mobLivingEntityPatch) {
      if (mobLivingEntityPatch.getOriginal() instanceof DoppelgangerEntity doppelgangerEntity) {
         UUID ownerUuid = doppelgangerEntity.getOwnerUUID();
         if (ownerUuid != null
            && Minecraft.m_91087_().f_91073_ != null
            && Minecraft.m_91087_().f_91073_.m_46003_(ownerUuid) instanceof AbstractClientPlayer clientPlayer
            && "slim".equals(clientPlayer.m_108564_())) {
            return Meshes.ALEX;
         }
      }

      return Meshes.BIPED;
   }

   public AssetAccessor<HumanoidMesh> getDefaultMesh() {
      return Meshes.BIPED;
   }

   private static boolean shouldUseSdtModel(DoppelgangerEntity entity) {
      DMConfig.DoppelModelMode mode = (DMConfig.DoppelModelMode)DMConfig.DOPPEL_MODEL.get();

      return switch (mode) {
         case ALWAYS_SDT -> true;
         case PLAYER -> false;
         case AUTO -> {
            if (entity.getOwner() instanceof AbstractClientPlayer player && SinDevilTriggerManager.isPlayerInSDT(player)) {
               yield true;
            }

            yield false;
         }
      };
   }

   public void render(
      Mob entity,
      LivingEntityPatch<Mob> entityPatch,
      HumanoidMobRenderer<Mob, HumanoidModel<Mob>> renderer,
      MultiBufferSource buffer,
      PoseStack poseStack,
      int packedLight,
      float partialTicks
   ) {
      if (entity instanceof DoppelgangerEntity doppelganger && shouldUseSdtModel(doppelganger)) {
         HumanoidMesh sdtMesh = (HumanoidMesh)PSdtPlayerRenderer.SIN_DEVIL_ARMOR.get();
         if (sdtMesh == null) {
            super.render(entity, entityPatch, renderer, buffer, poseStack, packedLight, partialTicks);
            return;
         }

         float alpha = doppelganger.getRenderAlpha();
         float tintIntensity = doppelganger.getColorTintIntensity();
         float TR = 0.039215688F;
         float TG = 0.69803923F;
         float TB = 0.9529412F;
         float r = 1.0F;
         float g = 1.0F;
         float b = 1.0F;
         float a = 1.0F;
         if (alpha < 1.0F || tintIntensity > 0.0F) {
            r = 1.0F - tintIntensity * (1.0F - TR);
            g = 1.0F - tintIntensity * (1.0F - TG);
            b = 1.0F - tintIntensity * (1.0F - TB);
            a = alpha;
         }

         boolean silhouette = (Boolean)DMConfig.DOPPEL_SILHOUETTE.get();
         Armature armature = entityPatch.getArmature();
         int frame = (int)(System.currentTimeMillis() / 70L % 5L);
         RenderType baseType = silhouette
            ? (
               DMConfig.DOPPEL_SILHOUETTE_EMISSIVE.get()
                  ? SlashRenderStates.getEntityTranslucentEmissiveDepthWrite(
                     ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/mesh/sin_devil/sin_devil_white.png")
                  )
                  : SlashRenderStates.getEntityTranslucentDepthWrite(
                     ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/mesh/sin_devil/sin_devil_white.png")
                  )
            )
            : RenderType.m_110473_(PSdtPlayerRenderer.SDT_BASE);
         poseStack.m_85836_();
         poseStack.m_85841_(1.1F, 1.1F, 1.1F);
         this.mulPoseStack(poseStack, armature, entity, entityPatch, partialTicks);
         this.setArmaturePose(entityPatch, armature, partialTicks);
         sdtMesh.draw(poseStack, buffer, baseType, packedLight, r, g, b, a, OverlayTexture.f_118083_, armature, armature.getPoseMatrices());
         ResourceLocation glowTex = silhouette ? SDT_WHITE_GLOW_FRAMES[frame] : PSdtPlayerRenderer.SDT_GLOW_FRAMES[frame];
         RenderType glowType = RenderType.m_234338_(glowTex);
         sdtMesh.draw(poseStack, buffer, glowType, packedLight, r, g, b, a * 0.9F, OverlayTexture.f_118083_, armature, armature.getPoseMatrices());
         if (DMConfig.DOPPEL_WEAPON_STRATEGY.get() == DMConfig.DoppelWeaponStrategy.LEGACY_OWNER_COPY) {
            SdtWeaponRenderer.render(entityPatch, InteractionHand.MAIN_HAND, armature, armature.getPoseMatrices(), buffer, poseStack, packedLight, partialTicks);
         } else {
            ItemStack yamatoStack = doppelganger.m_21205_();
            if (!yamatoStack.m_150930_((Item)DMCItems.YAMATO_DMC5.get())) {
               yamatoStack = doppelganger.createFixedWeaponStack();
            }

            ClientEngine.getInstance()
               .renderEngine
               .getItemRenderer(yamatoStack)
               .renderItemInHand(yamatoStack, entityPatch, InteractionHand.MAIN_HAND, armature.getPoseMatrices(), buffer, poseStack, packedLight, partialTicks);
         }

         poseStack.m_85849_();
         return;
      }

      super.render(entity, entityPatch, renderer, buffer, poseStack, packedLight, partialTicks);
   }
}
