package com.dmc.invincible_dmc.mixin.epicfight.client;

import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.entity.soul.SoulEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;

@Mixin(
   value = {PatchedLivingEntityRenderer.class},
   remap = false
)
public abstract class PatchedLivingEntityRendererMixin {
   @Unique
   private static final float TINT_R = 0.039215688F;
   @Unique
   private static final float TINT_G = 0.69803923F;
   @Unique
   private static final float TINT_B = 0.9529412F;
   @Unique
   private static final float SOUL_TINT_R = 0.6666667F;
   @Unique
   private static final float SOUL_TINT_G = 0.6666667F;
   @Unique
   private static final float SOUL_TINT_B = 0.6666667F;

   @WrapOperation(
      method = {"render*"},
      at = {@At(
         value = "INVOKE",
         target = "Lyesman/epicfight/api/client/model/SkinnedMesh;draw(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;IFFFFILyesman/epicfight/api/model/Armature;[Lyesman/epicfight/api/utils/math/OpenMatrix4f;)V",
         ordinal = 0
      )},
      require = 1
   )
   private void invincible_dmc$wrapMainMeshDraw(
      SkinnedMesh mesh,
      PoseStack poseStack,
      MultiBufferSource buffer,
      RenderType renderType,
      int packedLight,
      float r,
      float g,
      float b,
      float a,
      int overlay,
      Armature armature,
      OpenMatrix4f[] poses,
      Operation<Void> original,
      LivingEntity entity
   ) {
      if (entity instanceof DoppelgangerEntity doppelganger) {
         float alpha = doppelganger.getRenderAlpha();
         float tintIntensity = doppelganger.getColorTintIntensity();
         if (alpha < 1.0F || tintIntensity > 0.0F) {
            float rMul = 1.0F - tintIntensity * 0.9607843F;
            float gMul = 1.0F - tintIntensity * 0.30196077F;
            float bMul = 1.0F - tintIntensity * 0.04705882F;
            r *= rMul;
            g *= gMul;
            b *= bMul;
            a *= alpha;
         }
      }

      if (entity instanceof SoulEntity soulEntity) {
         float alpha = soulEntity.getRenderAlpha();
         float tintIntensity = soulEntity.getColorTintIntensity();
         if (alpha < 1.0F || tintIntensity > 0.0F) {
            float rMul = 1.0F - tintIntensity * 0.3333333F;
            float gMul = 1.0F - tintIntensity * 0.3333333F;
            float bMul = 1.0F - tintIntensity * 0.3333333F;
            r *= rMul;
            g *= gMul;
            b *= bMul;
            a *= alpha;
         }
      }

      original.call(new Object[]{mesh, poseStack, buffer, renderType, packedLight, r, g, b, a, overlay, armature, poses});
   }
}
