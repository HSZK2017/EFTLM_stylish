package com.pla.annoyingvillagers.client.engine;

import com.google.gson.JsonElement;
import com.mojang.blaze3d.vertex.PoseStack;
import com.pla.annoyingvillagers.gameasset.AVAnimations;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class RenderBlackscratcher extends RenderItemBase {
   public RenderBlackscratcher(JsonElement json) {
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
      if (livingEntityPatch != null && hand == InteractionHand.MAIN_HAND) {
         ItemStack heldStack = ((LivingEntity)livingEntityPatch.getOriginal()).m_21205_();
         if (heldStack.m_150930_((Item)AnnoyingVillagersModItems.BLACKSCRATCHER.get())) {
            AnimationPlayer animationPlayer = livingEntityPatch.getAnimator().getPlayerFor(null);
            AssetAccessor<? extends StaticAnimation> currentAnimation = animationPlayer == null ? null : animationPlayer.getRealAnimation();
            if (currentAnimation != AVAnimations.BLACKSCRATCHER_IDLE && currentAnimation != AVAnimations.BLACKSCRATCHER_ATTACK) {
               this.renderStack(
                  heldStack, livingEntityPatch, InteractionHand.MAIN_HAND, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, poses, buffer, poseStack, packedLight
               );
            } else {
               this.renderStack(
                  new ItemStack((ItemLike)AnnoyingVillagersModItems.BLACKSCRATCHER_TOP.get()),
                  livingEntityPatch,
                  InteractionHand.MAIN_HAND,
                  ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
                  poses,
                  buffer,
                  poseStack,
                  packedLight
               );
               this.renderStack(
                  new ItemStack((ItemLike)AnnoyingVillagersModItems.BLACKSCRATCHER_BOTTOM.get()),
                  livingEntityPatch,
                  InteractionHand.OFF_HAND,
                  ItemDisplayContext.THIRD_PERSON_LEFT_HAND,
                  poses,
                  buffer,
                  poseStack,
                  packedLight
               );
            }
         }
      }
   }

   private void renderStack(
      ItemStack renderStack,
      LivingEntityPatch<?> livingEntityPatch,
      InteractionHand hand,
      ItemDisplayContext displayContext,
      OpenMatrix4f[] poses,
      MultiBufferSource buffer,
      PoseStack poseStack,
      int packedLight
   ) {
      OpenMatrix4f correctionMatrix = new OpenMatrix4f(this.getCorrectionMatrix(livingEntityPatch, hand, poses));
      poseStack.m_85836_();
      MathUtils.mulStack(poseStack, correctionMatrix);
      Minecraft.m_91087_()
         .m_91291_()
         .m_269128_(
            renderStack, displayContext, packedLight, OverlayTexture.f_118083_, poseStack, buffer, ((LivingEntity)livingEntityPatch.getOriginal()).m_9236_(), 0
         );
      poseStack.m_85849_();
   }
}
