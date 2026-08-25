package com.pla.annoyingvillagers.client.engine;

import com.google.gson.JsonElement;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class RenderDiamondBolt extends RenderItemBase {
   private static final double AIM_HAND_X_OFFSET = 0.0;
   private static final double AIM_HAND_Y_OFFSET = -1.5;
   private static final double AIM_HAND_Z_OFFSET = 0.0;

   public RenderDiamondBolt(JsonElement json) {
      super(json);
   }

   public void renderItemInHand(
      ItemStack stack,
      LivingEntityPatch<?> livingEntityPatch,
      InteractionHand hand,
      OpenMatrix4f[] poses,
      MultiBufferSource buffer,
      PoseStack poseStack,
      int packedLight,
      float partialTicks
   ) {
      if (livingEntityPatch != null) {
         LivingEntity entity = (LivingEntity)livingEntityPatch.getOriginal();
         OpenMatrix4f correctionMatrix = this.getCorrectionMatrix(livingEntityPatch, hand, poses);
         ItemDisplayContext displayContext = hand == InteractionHand.MAIN_HAND
            ? ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
            : ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
         poseStack.m_85836_();
         MathUtils.mulStack(poseStack, correctionMatrix);
         if (isAimingDiamondBolt(stack, entity, hand)) {
            poseStack.m_85837_(0.0, -1.5, 0.0);
            BakedModel model = itemRenderer.m_174264_(stack, entity.m_9236_(), entity, entity.m_19879_());
            model = ForgeHooksClient.handleCameraTransforms(poseStack, model, displayContext, hand == InteractionHand.OFF_HAND);
            poseStack.m_252781_(Axis.f_252436_.m_252977_(180.0F));
            itemRenderer.m_115143_(stack, ItemDisplayContext.NONE, false, poseStack, buffer, packedLight, OverlayTexture.f_118083_, model);
         } else {
            itemInHandRenderer.m_269530_(entity, stack, displayContext, hand == InteractionHand.OFF_HAND, poseStack, buffer, packedLight);
         }

         poseStack.m_85849_();
      }
   }

   private static boolean isAimingDiamondBolt(ItemStack stack, LivingEntity entity, InteractionHand hand) {
      return stack.m_150930_((Item)AnnoyingVillagersModItems.DIAMOND_BOLT.get()) && entity.m_6117_() && entity.m_7655_() == hand;
   }
}
