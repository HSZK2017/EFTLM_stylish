package com.Yujin.onegradefixer.epicmoonmod.renderer;

import com.Yujin.onegradefixer.epicmoonmod.animations.EMAnimations;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class TSRenderer extends RenderItemBase {
   private final ResourceLocation mesh1Texture;
   private final ResourceLocation mesh2Texture;
   private final ResourceLocation mesh2TextureGlow;
   private final AssetAccessor<? extends SkinnedMesh> mesh1;
   private final AssetAccessor<? extends SkinnedMesh> mesh2;

   public TSRenderer(JsonElement json) {
      super(json);
      JsonObject jsonObj = json.getAsJsonObject();
      if (jsonObj.has("mesh1_texture")) {
         this.mesh1Texture = ResourceLocation.parse(jsonObj.get("mesh1_texture").getAsString());
      } else {
         this.mesh1Texture = null;
      }

      if (jsonObj.has("mesh2_texture")) {
         this.mesh2Texture = ResourceLocation.parse(jsonObj.get("mesh2_texture").getAsString());
      } else {
         this.mesh2Texture = null;
      }

      if (jsonObj.has("mesh2_glowtexture")) {
         this.mesh2TextureGlow = ResourceLocation.parse(jsonObj.get("mesh2_glowtexture").getAsString());
      } else {
         this.mesh2TextureGlow = null;
      }

      if (jsonObj.has("mesh1")) {
         String meshLoc = jsonObj.get("mesh1").getAsString();
         ResourceLocation resLoc = ResourceLocation.parse(meshLoc);
         this.mesh1 = MeshAccessor.create(resLoc.m_135827_(), resLoc.m_135815_(), loader -> loader.loadSkinnedMesh(SkinnedMesh::new));
      } else {
         this.mesh1 = null;
      }

      if (jsonObj.has("mesh2")) {
         String meshLoc = jsonObj.get("mesh2").getAsString();
         ResourceLocation resLoc = ResourceLocation.parse(meshLoc);
         this.mesh2 = MeshAccessor.create(resLoc.m_135827_(), resLoc.m_135815_(), loader -> loader.loadSkinnedMesh(SkinnedMesh::new));
      } else {
         this.mesh2 = null;
      }
   }

   public void renderItemInHand(
      ItemStack stack,
      LivingEntityPatch<?> entityPatch,
      InteractionHand hand,
      OpenMatrix4f[] poses,
      MultiBufferSource buffer,
      PoseStack poseStack,
      int packedLight,
      float partialTicks
   ) {
      if (entityPatch != null) {
         Armature armature = EMAnimations.ARMATURE.get();
         poseStack.m_85836_();
         armature.setPose(entityPatch.getAnimator().getPose(partialTicks));
         Player player = (Player)entityPatch.getOriginal();
         CompoundTag compoundTag = player.m_21205_().m_41784_();
         SkinnedMesh renderMesh;
         ResourceLocation texture;
         if (compoundTag.m_128451_("weapon_mode") == 1) {
            renderMesh = hand == InteractionHand.MAIN_HAND ? (SkinnedMesh)this.mesh2.get() : (SkinnedMesh)this.mesh2.get();
            texture = this.mesh2Texture;
         } else {
            renderMesh = hand == InteractionHand.MAIN_HAND ? (SkinnedMesh)this.mesh1.get() : (SkinnedMesh)this.mesh1.get();
            texture = this.mesh1Texture;
         }

         if (renderMesh != null && texture != null) {
            renderMesh.draw(
               poseStack,
               buffer,
               RenderType.m_110431_(texture),
               packedLight,
               1.0F,
               1.0F,
               1.0F,
               1.0F,
               OverlayTexture.f_118083_,
               armature,
               armature.getPoseMatrices()
            );
            if (compoundTag.m_128451_("weapon_mode") == 1 && this.mesh2TextureGlow != null) {
               renderMesh.draw(
                  poseStack,
                  buffer,
                  RenderType.m_110488_(this.mesh2TextureGlow),
                  packedLight,
                  1.0F,
                  1.0F,
                  1.0F,
                  1.0F,
                  OverlayTexture.f_118083_,
                  armature,
                  armature.getPoseMatrices()
               );
            }
         }

         poseStack.m_85849_();
      }
   }
}
