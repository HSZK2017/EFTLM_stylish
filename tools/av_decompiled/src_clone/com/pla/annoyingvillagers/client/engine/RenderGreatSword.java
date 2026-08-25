package com.pla.annoyingvillagers.client.engine;

import com.google.gson.JsonElement;
import com.mojang.blaze3d.vertex.PoseStack;
import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class RenderGreatSword extends RenderItemBase {
   public RenderGreatSword(JsonElement json) {
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
         OpenMatrix4f openmatrix4f = new OpenMatrix4f(this.getCorrectionMatrix(livingEntityPatch, InteractionHand.MAIN_HAND, poses));
         AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(livingEntityPatch.getAnimator().getPlayerFor(null))
            .getRealAnimation();
         if (dynamicAnimation != AnimsPugilistSteve.HARD_GREATSWORD_GUARD_SKILL && !dynamicAnimation.equals(AnimsPugilistSteve.HARD_GREATSWORD_GUARD)) {
            ItemStack itemstack = new ItemStack((ItemLike)AnnoyingVillagersModItems.GREAT_SWORD.get());
            poseStack.m_85836_();
            MathUtils.mulStack(poseStack, openmatrix4f);
            Minecraft.m_91087_()
               .m_91291_()
               .m_269128_(
                  itemstack,
                  ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
                  packedLight,
                  OverlayTexture.f_118083_,
                  poseStack,
                  buffer,
                  ((LivingEntity)livingEntityPatch.getOriginal()).m_9236_(),
                  0
               );
            poseStack.m_85849_();
         } else {
            ItemStack itemstack = new ItemStack((ItemLike)AnnoyingVillagersModItems.GREAT_SWORD_SKILL.get());
            poseStack.m_85836_();
            MathUtils.mulStack(poseStack, openmatrix4f);
            Minecraft.m_91087_()
               .m_91291_()
               .m_269128_(
                  itemstack,
                  ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
                  packedLight,
                  OverlayTexture.f_118083_,
                  poseStack,
                  buffer,
                  ((LivingEntity)livingEntityPatch.getOriginal()).m_9236_(),
                  0
               );
            poseStack.m_85849_();
         }
      }
   }
}
