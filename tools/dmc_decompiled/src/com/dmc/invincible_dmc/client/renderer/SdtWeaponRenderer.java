package com.dmc.invincible_dmc.client.renderer;

import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.client.render.EnchantedWeaponOutlineRenderer;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public final class SdtWeaponRenderer {
   private static final String MODID = "invincible_dmc";
   private static final ResourceLocation TEX_BASE = ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/item/weapon/devil_sword_vergil.png");
   private static final ResourceLocation TEX_GLOW = ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/item/weapon/devil_sword_vergil_l.png");
   private static final MeshAccessor<SkinnedMesh> MESH_BLADE = MeshAccessor.create(
      "invincible_dmc", "weapon/devil_sword/devil_sword_vergil_blade", loader -> loader.loadSkinnedMesh(SkinnedMesh::new)
   );
   private static final MeshAccessor<SkinnedMesh> MESH_JC = MeshAccessor.create(
      "invincible_dmc", "weapon/devil_sword/devil_sword_vergil_jc", loader -> loader.loadSkinnedMesh(SkinnedMesh::new)
   );
   private static final MeshAccessor<SkinnedMesh> MESH_ALL = MeshAccessor.create(
      "invincible_dmc", "weapon/devil_sword/devil_sword_vergil_all", loader -> loader.loadSkinnedMesh(SkinnedMesh::new)
   );
   private static final MeshAccessor<SkinnedMesh> MESH_IN_SHEATH = MeshAccessor.create(
      "invincible_dmc", "weapon/devil_sword/devil_sword_vergil_in_sheath", loader -> loader.loadSkinnedMesh(SkinnedMesh::new)
   );

   private SdtWeaponRenderer() {
   }

   public static void preload() {
      for (MeshAccessor<?> accessor : new MeshAccessor[]{MESH_BLADE, MESH_JC, MESH_ALL, MESH_IN_SHEATH}) {
         accessor.get().initialize();
      }
   }

   public static void render(
      LivingEntityPatch<?> entityPatch,
      InteractionHand hand,
      Armature armature,
      OpenMatrix4f[] poses,
      MultiBufferSource buffer,
      PoseStack poseStack,
      int packedLight,
      float partialTicks
   ) {
      AnimationPlayer animPlayer = entityPatch.getClientAnimator().baseLayer.animationPlayer;
      if (animPlayer != null) {
         DynamicAnimation animation = DMCAnimationUtils.getCurrentAnimation(animPlayer);
         DynamicAnimation realAnim = DMCAnimationUtils.getRealAnimation(animation);
         float elapsedTime = animPlayer.getElapsedTime();
         SdtWeaponRenderer.MeshResult result = resolveMesh(animation, animPlayer, realAnim, elapsedTime);
         if (result.mesh != null) {
            drawMesh(((LivingEntity)entityPatch.getOriginal()).m_21120_(hand), result.mesh, poseStack, buffer, packedLight, armature, poses);
         }
      }
   }

   public static float getTargetScale(LivingEntityPatch<?> entityPatch) {
      AnimationPlayer animPlayer = entityPatch.getClientAnimator().baseLayer.animationPlayer;
      if (animPlayer == null) {
         return 1.0F;
      } else {
         DynamicAnimation animation = DMCAnimationUtils.getCurrentAnimation(animPlayer);
         DynamicAnimation realAnim = DMCAnimationUtils.getRealAnimation(animation);
         float elapsedTime = animPlayer.getElapsedTime();
         return resolveMesh(animation, animPlayer, realAnim, elapsedTime).targetScale;
      }
   }

   private static SdtWeaponRenderer.MeshResult resolveMesh(DynamicAnimation animation, AnimationPlayer animPlayer, DynamicAnimation realAnim, float elapsedTime) {
      Optional<TimePairList> hideOpt = realAnim.getProperty(YamatoAttackAnimation.SDT_HIDE_WEAPON_TIME);
      if (hideOpt.isPresent() && hideOpt.get().isTimeInPairs(elapsedTime)) {
         return new SdtWeaponRenderer.MeshResult(null, 1.0F);
      } else {
         Optional<TimePairList> inSheathOpt = realAnim.getProperty(YamatoAttackAnimation.SDT_IN_SHEATH_TIME);
         if (inSheathOpt.isPresent() && inSheathOpt.get().isTimeInPairs(elapsedTime)) {
            return new SdtWeaponRenderer.MeshResult((SkinnedMesh)MESH_IN_SHEATH.get(), 1.05F);
         } else {
            Optional<TimePairList> sheathingOpt = realAnim.getProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME);
            if (sheathingOpt.isPresent() && sheathingOpt.get().isTimeInPairs(elapsedTime)) {
               return new SdtWeaponRenderer.MeshResult((SkinnedMesh)MESH_ALL.get(), 1.05F);
            } else if (isUseMesh3(animation, animPlayer)) {
               return new SdtWeaponRenderer.MeshResult((SkinnedMesh)MESH_JC.get(), 1.05F);
            } else {
               return isUseMesh2(animation, animPlayer)
                  ? new SdtWeaponRenderer.MeshResult((SkinnedMesh)MESH_BLADE.get(), 1.05F)
                  : new SdtWeaponRenderer.MeshResult(null, 1.0F);
            }
         }
      }
   }

   private static void drawMesh(
      ItemStack stack, SkinnedMesh mesh, PoseStack poseStack, MultiBufferSource buffer, int packedLight, Armature armature, OpenMatrix4f[] poses
   ) {
      EnchantedWeaponOutlineRenderer.renderBeforeBase(stack, mesh, TEX_BASE, poseStack, buffer, packedLight, armature, poses);
      mesh.draw(poseStack, buffer, RenderType.m_110473_(TEX_BASE), packedLight, 1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.f_118083_, armature, poses);
      mesh.draw(poseStack, buffer, RenderType.m_234338_(TEX_GLOW), packedLight, 1.0F, 1.0F, 1.0F, 0.9F, OverlayTexture.f_118083_, armature, poses);
      EnchantedWeaponOutlineRenderer.render(stack, mesh, TEX_BASE, poseStack, buffer, packedLight, armature, poses);
   }

   private static boolean isUseMesh2(DynamicAnimation dynamicAnimation, AnimationPlayer animPlayer) {
      DynamicAnimation realAnim = DMCAnimationUtils.getRealAnimation(dynamicAnimation);
      Optional<TimePairList> unsheathTime = realAnim.getProperty(YamatoAttackAnimation.UNSHEATH_TIME);
      if (unsheathTime.isEmpty()) {
         return false;
      } else {
         TimePairList tp = unsheathTime.get();
         return dynamicAnimation.isLinkAnimation() && !realAnim.getProperty(YamatoAttackAnimation.USE_MESH2_IN_LINK).orElse(false)
            ? false
            : tp.isTimeInPairs(animPlayer.getElapsedTime());
      }
   }

   private static boolean isUseMesh3(DynamicAnimation dynamicAnimation, AnimationPlayer animPlayer) {
      if (dynamicAnimation.isLinkAnimation()) {
         return false;
      } else {
         DynamicAnimation realAnim = DMCAnimationUtils.getRealAnimation(dynamicAnimation);
         Optional<TimePairList> spModelTime = realAnim.getProperty(YamatoAttackAnimation.SP_MODEL_TIME);
         if (spModelTime.isPresent()) {
            return spModelTime.get().isTimeInPairs(animPlayer.getElapsedTime());
         } else {
            ResourceLocation realName = DMCAnimationUtils.getRealAnimationAccessor(dynamicAnimation).registryName();
            if (!realName.equals(YamatoAnimations.YAMATO_JUDGEMENT_CUT_GROUND.registryName())
               && !realName.equals(YamatoAnimations.YAMATO_JUDGEMENT_CUT_GROUND_FS.registryName())
               && !realName.equals(YamatoAnimations.YAMATO_JUDGEMENT_CUT_AIR.registryName())
               && !realName.equals(YamatoAnimations.YAMATO_JUDGEMENT_CUT_AIR_FS.registryName())) {
               return false;
            } else {
               Optional<TimePairList> unsheathTime = realAnim.getProperty(YamatoAttackAnimation.UNSHEATH_TIME);
               return unsheathTime.<Boolean>map(t -> t.isTimeInPairs(animPlayer.getElapsedTime())).orElse(true);
            }
         }
      }
   }

   private static record MeshResult(@Nullable SkinnedMesh mesh, float targetScale) {
   }
}
