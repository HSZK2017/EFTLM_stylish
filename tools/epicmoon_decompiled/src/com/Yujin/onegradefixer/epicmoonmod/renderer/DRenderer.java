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
public class DRenderer extends RenderItemBase {
   private final ResourceLocation mesh1Texture;
   private final ResourceLocation mesh2Texture;
   private final ResourceLocation mesh3Texture;
   private final ResourceLocation mesh3TextureGlow;
   private final ResourceLocation mesh5Texture;
   private final ResourceLocation mesh5TextureGlow;
   private final AssetAccessor<? extends SkinnedMesh> mesh1;
   private final AssetAccessor<? extends SkinnedMesh> mesh2;
   private final AssetAccessor<? extends SkinnedMesh> mesh3;
   private final AssetAccessor<? extends SkinnedMesh> mesh4;
   private final AssetAccessor<? extends SkinnedMesh> mesh5;

   public DRenderer(JsonElement json) {
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

      if (jsonObj.has("mesh3_texture")) {
         this.mesh3Texture = ResourceLocation.parse(jsonObj.get("mesh3_texture").getAsString());
      } else {
         this.mesh3Texture = null;
      }

      if (jsonObj.has("mesh3_glowtexture")) {
         this.mesh3TextureGlow = ResourceLocation.parse(jsonObj.get("mesh3_glowtexture").getAsString());
      } else {
         this.mesh3TextureGlow = null;
      }

      if (jsonObj.has("mesh5_texture")) {
         this.mesh5Texture = ResourceLocation.parse(jsonObj.get("mesh5_texture").getAsString());
      } else {
         this.mesh5Texture = null;
      }

      if (jsonObj.has("mesh5_glowtexture")) {
         this.mesh5TextureGlow = ResourceLocation.parse(jsonObj.get("mesh5_glowtexture").getAsString());
      } else {
         this.mesh5TextureGlow = null;
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

      if (jsonObj.has("mesh3")) {
         String meshLoc = jsonObj.get("mesh3").getAsString();
         ResourceLocation resLoc = ResourceLocation.parse(meshLoc);
         this.mesh3 = MeshAccessor.create(resLoc.m_135827_(), resLoc.m_135815_(), loader -> loader.loadSkinnedMesh(SkinnedMesh::new));
      } else {
         this.mesh3 = null;
      }

      if (jsonObj.has("mesh4")) {
         String meshLoc = jsonObj.get("mesh4").getAsString();
         ResourceLocation resLoc = ResourceLocation.parse(meshLoc);
         this.mesh4 = MeshAccessor.create(resLoc.m_135827_(), resLoc.m_135815_(), loader -> loader.loadSkinnedMesh(SkinnedMesh::new));
      } else {
         this.mesh4 = null;
      }

      if (jsonObj.has("mesh5")) {
         String meshLoc = jsonObj.get("mesh5").getAsString();
         ResourceLocation resLoc = ResourceLocation.parse(meshLoc);
         this.mesh5 = MeshAccessor.create(resLoc.m_135827_(), resLoc.m_135815_(), loader -> loader.loadSkinnedMesh(SkinnedMesh::new));
      } else {
         this.mesh5 = null;
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
         Armature armature = EMAnimations.ARMATURE2.get();
         poseStack.m_85836_();
         armature.setPose(entityPatch.getAnimator().getPose(partialTicks));
         Player player = (Player)entityPatch.getOriginal();
         CompoundTag compoundTag = player.m_21205_().m_41784_();
         SkinnedMesh renderMesh;
         ResourceLocation texture;
         if (compoundTag.m_128451_("weapon_mode") == 1) {
            renderMesh = hand == InteractionHand.MAIN_HAND ? (SkinnedMesh)this.mesh2.get() : (SkinnedMesh)this.mesh2.get();
            texture = this.mesh2Texture;
         } else if (compoundTag.m_128451_("weapon_mode") == 2) {
            renderMesh = hand == InteractionHand.MAIN_HAND ? (SkinnedMesh)this.mesh3.get() : (SkinnedMesh)this.mesh3.get();
            texture = this.mesh3Texture;
         } else if (compoundTag.m_128451_("weapon_mode") == 3) {
            renderMesh = hand == InteractionHand.MAIN_HAND ? (SkinnedMesh)this.mesh4.get() : (SkinnedMesh)this.mesh4.get();
            texture = this.mesh3Texture;
         } else if (compoundTag.m_128451_("weapon_mode") == 4) {
            renderMesh = hand == InteractionHand.MAIN_HAND ? (SkinnedMesh)this.mesh5.get() : (SkinnedMesh)this.mesh5.get();
            texture = this.mesh5Texture;
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
            if (compoundTag.m_128451_("weapon_mode") == 2 && this.mesh3TextureGlow != null) {
               renderMesh.draw(
                  poseStack,
                  buffer,
                  RenderType.m_110488_(this.mesh3TextureGlow),
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

            if (compoundTag.m_128451_("weapon_mode") == 3 && this.mesh3TextureGlow != null) {
               renderMesh.draw(
                  poseStack,
                  buffer,
                  RenderType.m_110488_(this.mesh3TextureGlow),
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

            if (compoundTag.m_128451_("weapon_mode") == 4 && this.mesh5TextureGlow != null) {
               renderMesh.draw(
                  poseStack,
                  buffer,
                  RenderType.m_110488_(this.mesh5TextureGlow),
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
