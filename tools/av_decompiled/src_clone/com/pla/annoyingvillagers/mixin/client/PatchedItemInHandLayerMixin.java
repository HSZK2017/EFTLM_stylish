package com.pla.annoyingvillagers.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.pla.annoyingvillagers.client.renderer.HookGunItemRenderer;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.item.FishingRodGrappleUtil;
import com.pla.annoyingvillagers.item.HookGunItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(
   value = {PatchedItemInHandLayer.class},
   remap = false
)
public abstract class PatchedItemInHandLayerMixin {
   @WrapOperation(
      method = {"renderLayer(Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/layers/RenderLayer;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I[Lyesman/epicfight/api/utils/math/OpenMatrix4f;FFFF)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;isOffhandItemValid()Z"
      )}
   )
   private boolean annoyingVillagers$forceOffhandUtilityItemRender(LivingEntityPatch<?> entityPatch, Operation<Boolean> original) {
      if ((Boolean)original.call(new Object[]{entityPatch})) {
         return true;
      } else {
         if (entityPatch.getOriginal() instanceof LivingEntity livingEntity
            && (
               FishingRodGrappleUtil.shouldForceOffhandFishingRodRender(livingEntity)
                  || HookGunItem.shouldForceOffhandHookGunRender(livingEntity)
                  || annoyingVillagers$shouldForceOffhandTransporterFragmentRender(livingEntity)
                  || annoyingVillagers$shouldForceOffhandBucketRender(livingEntity)
            )) {
            return true;
         }

         return false;
      }
   }

   @WrapOperation(
      method = {"renderLayer(Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/layers/RenderLayer;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I[Lyesman/epicfight/api/utils/math/OpenMatrix4f;FFFF)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lyesman/epicfight/client/renderer/patched/item/RenderItemBase;renderItemInHand(Lnet/minecraft/world/item/ItemStack;Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lnet/minecraft/world/InteractionHand;Lyesman/epicfight/model/armature/HumanoidArmature;[Lyesman/epicfight/api/utils/math/OpenMatrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/PoseStack;I)V"
      )}
   )
   private void annoyingVillagers$renderHookGunWithHandContext(
      RenderItemBase renderer,
      ItemStack stack,
      LivingEntityPatch<?> entityPatch,
      InteractionHand hand,
      HumanoidArmature armature,
      OpenMatrix4f[] poses,
      MultiBufferSource buffer,
      PoseStack poseStack,
      int packedLight,
      Operation<Void> original
   ) {
      if (entityPatch.getOriginal() instanceof LivingEntity livingEntity) {
         HookGunItemRenderer.setRenderedHandContext(livingEntity, hand);
      }

      try {
         original.call(new Object[]{renderer, stack, entityPatch, hand, armature, poses, buffer, poseStack, packedLight});
      } finally {
         HookGunItemRenderer.clearRenderedHandContext();
      }
   }

   private static boolean annoyingVillagers$shouldForceOffhandBucketRender(LivingEntity livingEntity) {
      ItemStack offhand = livingEntity.m_21206_();
      if (offhand.m_41619_()) {
         return false;
      } else {
         Item item = offhand.m_41720_();
         String itemPath = BuiltInRegistries.f_257033_.m_7981_(item).m_135815_();
         return item instanceof BucketItem || offhand.m_150930_(Items.f_42446_) || itemPath.endsWith("_bucket") || itemPath.equals("bucket");
      }
   }

   private static boolean annoyingVillagers$shouldForceOffhandTransporterFragmentRender(LivingEntity livingEntity) {
      return livingEntity.m_21206_().m_150930_((Item)AnnoyingVillagersModItems.TRANSPORTER_FRAGMENT.get());
   }
}
