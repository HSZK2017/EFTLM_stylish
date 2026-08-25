package com.dmc.invincible_dmc.client.renderer.patched.layer;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.client.effeks.EffekConfig;
import com.dmc.invincible_dmc.client.render.EnchantedWeaponOutlineRenderer;
import com.dmc.invincible_dmc.client.render.NormalMappedRenderTypes;
import com.dmc.invincible_dmc.client.render.PbrMaterialTextures;
import com.dmc.invincible_dmc.item.DMCItems;
import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.layer.UniqueLayer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class TorsoMountLayer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<E>> extends UniqueLayer<E, T, M> {
   private static final TorsoMountLayer.TorsoMountDef[] MOUNTS = new TorsoMountLayer.TorsoMountDef[]{
      new TorsoMountLayer.TorsoMountDef((Item)DMCItems.YAMATO_DMC4.get(), "yamato_dmc4"),
      new TorsoMountLayer.TorsoMountDef((Item)DMCItems.YAMATO_DMC5_BD.get(), "yamato_dmc5_bd", yamatoDmc5BdBaseMaterial(), yamatoDmc5BdLightMaterial()),
      new TorsoMountLayer.TorsoMountDef((Item)DMCItems.DEVIL_SWORD_VERGIL.get(), "devil_sword/devil_sword_vergil"),
      new TorsoMountLayer.TorsoMountDef((Item)DMCItems.YAMATO_DMC5.get(), "yamato_dmc5"),
      new TorsoMountLayer.TorsoMountDef((Item)DMCItems.YAMATO_DMC5_MINI.get(), "yamato_dmc5_mini")
   };

   protected void renderLayer(
      T entityPatch,
      E entity,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      OpenMatrix4f[] poses,
      float bob,
      float yRot,
      float xRot,
      float partialTicks
   ) {
      if (entity instanceof Player player) {
         TorsoMountLayer.MountedWeapon mountedWeapon = findMount(player, entityPatch);
         if (mountedWeapon != null) {
            TorsoMountLayer.TorsoMountDef mount = mountedWeapon.mount();
            SkinnedMesh mesh = (SkinnedMesh)mount.mesh.get();
            if (mesh != null) {
               poseStack.m_85836_();
               entityPatch.getArmature().setPose(entityPatch.getAnimator().getPose(partialTicks));
               boolean materialEnabled = mount.item != DMCItems.YAMATO_DMC5_BD.get() || (Boolean)DMConfig.YAMATO_DMC5_BD_PBR.get();
               boolean pbrEnabled = materialEnabled && NormalMappedRenderTypes.isPbrEnabled(mount.material);
               ResourceLocation baseTexture = !pbrEnabled && mount.material != null && mount.material.legacyTexture() != null
                  ? mount.material.legacyTexture()
                  : mount.texture;
               RenderType baseRenderType = !pbrEnabled ? RenderType.m_110473_(baseTexture) : NormalMappedRenderTypes.pbrEntity(mount.texture, mount.material);
               EnchantedWeaponOutlineRenderer.renderBeforeBase(
                  mountedWeapon.stack(),
                  mesh,
                  baseTexture,
                  poseStack,
                  buffer,
                  packedLight,
                  entityPatch.getArmature(),
                  entityPatch.getArmature().getPoseMatrices()
               );
               mesh.draw(
                  poseStack,
                  buffer,
                  baseRenderType,
                  packedLight,
                  1.0F,
                  1.0F,
                  1.0F,
                  1.0F,
                  OverlayTexture.f_118083_,
                  entityPatch.getArmature(),
                  entityPatch.getArmature().getPoseMatrices()
               );
               if (pbrEnabled && NormalMappedRenderTypes.isPbrEnabled(mount.lightMaterial)) {
                  mesh.draw(
                     poseStack,
                     buffer,
                     NormalMappedRenderTypes.pbrEntity(mount.textureL, mount.lightMaterial),
                     packedLight,
                     1.0F,
                     1.0F,
                     1.0F,
                     1.0F,
                     OverlayTexture.f_118083_,
                     entityPatch.getArmature(),
                     entityPatch.getArmature().getPoseMatrices()
                  );
               } else {
                  mesh.draw(
                     poseStack,
                     buffer,
                     RenderType.m_234338_(mount.textureL),
                     packedLight,
                     1.0F,
                     1.0F,
                     1.0F,
                     0.9F,
                     OverlayTexture.f_118083_,
                     entityPatch.getArmature(),
                     entityPatch.getArmature().getPoseMatrices()
                  );
               }

               EnchantedWeaponOutlineRenderer.render(
                  mountedWeapon.stack(),
                  mesh,
                  baseTexture,
                  poseStack,
                  buffer,
                  packedLight,
                  entityPatch.getArmature(),
                  entityPatch.getArmature().getPoseMatrices()
               );
               poseStack.m_85849_();
            }
         }
      }
   }

   @Nullable
   private static TorsoMountLayer.MountedWeapon findMount(Player player, LivingEntityPatch<?> entityPatch) {
      if (entityPatch != null) {
         LivingMotion motion = entityPatch.currentLivingMotion;
         if (motion == LivingMotions.CLIMB || motion == LivingMotions.SIT) {
            ItemStack mainHandStack = player.m_21205_();

            for (TorsoMountLayer.TorsoMountDef def : MOUNTS) {
               if (mainHandStack.m_150930_(def.item)) {
                  return new TorsoMountLayer.MountedWeapon(def, mainHandStack);
               }
            }
         }
      }

      if (!EffekConfig.isEnabled("torso_storage.enabled", DMConfig.TORSO_STORAGE_ENABLED)) {
         return null;
      } else {
         for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = player.m_150109_().m_8020_(slot);

            for (TorsoMountLayer.TorsoMountDef defx : MOUNTS) {
               if (stack.m_150930_(defx.item) && !player.m_21205_().m_150930_(defx.item) && !player.m_21206_().m_150930_(defx.item)) {
                  return new TorsoMountLayer.MountedWeapon(defx, stack);
               }
            }
         }

         return null;
      }
   }

   private static PbrMaterialTextures yamatoDmc5BdBaseMaterial() {
      return PbrMaterialTextures.fromPackedMer(
         null,
         weaponTexture("yamato.png"),
         null,
         weaponTexture("yamato_height.png"),
         weaponTexture("yamato_mer.png"),
         weaponTexture("yamato.png"),
         weaponTexture("yamato_dmc5_bd.png")
      );
   }

   private static PbrMaterialTextures yamatoDmc5BdLightMaterial() {
      return PbrMaterialTextures.fromPackedMer(
         weaponTexture("yamato_dmc5_bd_l_normal.png"),
         weaponTexture("yamato_dmc5_bd_l.png"),
         null,
         null,
         weaponTexture("yamato_dmc5_bd_l_mer.png"),
         weaponTexture("yamato_dmc5_bd_l.png"),
         weaponTexture("yamato_dmc5_bd_l.png")
      );
   }

   private static ResourceLocation weaponTexture(String fileName) {
      return ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/item/weapon/" + fileName);
   }

   private static record MountedWeapon(TorsoMountLayer.TorsoMountDef mount, ItemStack stack) {
   }

   private static class TorsoMountDef {
      final Item item;
      final AssetAccessor<SkinnedMesh> mesh;
      final ResourceLocation texture;
      final ResourceLocation textureL;
      final PbrMaterialTextures material;
      final PbrMaterialTextures lightMaterial;

      TorsoMountDef(Item item, String modelPath) {
         this(item, modelPath, null, null);
      }

      TorsoMountDef(Item item, String modelPath, boolean hasPbrMaterial) {
         this(
            item,
            modelPath,
            hasPbrMaterial
               ? PbrMaterialTextures.fromTextureBase("invincible_dmc", "textures/item/weapon/" + modelPath.substring(modelPath.lastIndexOf(47) + 1))
               : null,
            null
         );
      }

      TorsoMountDef(Item item, String modelPath, @Nullable PbrMaterialTextures material, @Nullable PbrMaterialTextures lightMaterial) {
         this.item = item;
         this.mesh = MeshAccessor.create("invincible_dmc", "weapon/" + modelPath + "_on_torso", loader -> loader.loadSkinnedMesh(SkinnedMesh::new));
         String fileName = modelPath.substring(modelPath.lastIndexOf(47) + 1);
         String texPath = "invincible_dmc:textures/item/weapon/" + fileName;
         this.texture = ResourceLocation.parse(texPath + ".png");
         this.textureL = ResourceLocation.parse(texPath + "_l.png");
         this.material = material;
         this.lightMaterial = lightMaterial;
      }
   }
}
