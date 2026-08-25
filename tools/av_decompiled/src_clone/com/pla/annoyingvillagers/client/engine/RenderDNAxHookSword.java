package com.pla.annoyingvillagers.client.engine;

import com.google.gson.JsonElement;
import com.mojang.blaze3d.vertex.PoseStack;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFight;
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
public class RenderDNAxHookSword extends RenderItemBase {
   public RenderDNAxHookSword(JsonElement json) {
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
         ItemStack heldStack = ((LivingEntity)livingEntityPatch.getOriginal()).m_21120_(hand);
         if (isDNAxHookSword(heldStack)) {
            AnimationPlayer animationPlayer = livingEntityPatch.getAnimator().getPlayerFor(null);
            if (animationPlayer == null) {
               this.renderStack(heldStack, livingEntityPatch, hand, poses, buffer, poseStack, packedLight);
            } else {
               AssetAccessor<? extends StaticAnimation> currentAnimation = animationPlayer.getRealAnimation();
               boolean sweepingEdge = currentAnimation == AnimsEpicFight.DNAX_HOOK_SWEEPING_EDGE;
               boolean dancingEdge = currentAnimation == AnimsEpicFight.DNAX_HOOK_DANCING_EDGE;
               ItemStack renderStack;
               if (!sweepingEdge && !dancingEdge) {
                  renderStack = heldStack.m_41777_();
               } else {
                  renderStack = new ItemStack((ItemLike)AnnoyingVillagersModItems.DNAX_HOOKED_SWORD_ABILITY.get());
                  if (heldStack.m_41793_()) {
                     renderStack.m_41784_().m_128379_("foil", true);
                  }
               }

               this.renderStack(renderStack, livingEntityPatch, hand, poses, buffer, poseStack, packedLight);
            }
         }
      }
   }

   private static boolean isDNAxHookSword(ItemStack stack) {
      return stack.m_150930_((Item)AnnoyingVillagersModItems.DNAX_HOOKED_SWORD.get());
   }

   private void renderStack(
      ItemStack renderStack,
      LivingEntityPatch<?> livingEntityPatch,
      InteractionHand hand,
      OpenMatrix4f[] poses,
      MultiBufferSource buffer,
      PoseStack poseStack,
      int packedLight
   ) {
      OpenMatrix4f correctionMatrix = new OpenMatrix4f(this.getCorrectionMatrix(livingEntityPatch, hand, poses));
      ItemDisplayContext displayContext = hand == InteractionHand.MAIN_HAND
         ? ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
         : ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
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
