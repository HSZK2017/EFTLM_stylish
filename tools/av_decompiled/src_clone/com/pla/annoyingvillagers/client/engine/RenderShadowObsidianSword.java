package com.pla.annoyingvillagers.client.engine;

import com.google.gson.JsonElement;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFight;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFightDualGreatsword;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFightInfernalGainer;
import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import com.pla.annoyingvillagers.gameasset.AnimsWom;
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
import net.shelmarow.combat_evolution.gameassets.animation.ExecutionAttackAnimation;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class RenderShadowObsidianSword extends RenderItemBase {
   public RenderShadowObsidianSword(JsonElement json) {
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
         if (hand == InteractionHand.MAIN_HAND
            && ((LivingEntity)livingEntityPatch.getOriginal()).m_21205_().m_41720_().equals(AnnoyingVillagersModItems.SHADOW_OBSIDIAN_SWORD.get())) {
            OpenMatrix4f openmatrix4f = new OpenMatrix4f(this.getCorrectionMatrix(livingEntityPatch, InteractionHand.MAIN_HAND, poses));
            AnimationPlayer animationPlayer = Objects.requireNonNull(livingEntityPatch.getAnimator().getPlayerFor(null));
            AssetAccessor<? extends StaticAnimation> dynamicAnimation = animationPlayer.getRealAnimation();
            float elapsedTimeFloat = animationPlayer.getElapsedTime();
            EntityState entityState = ((StaticAnimation)dynamicAnimation.get()).getState(livingEntityPatch, elapsedTimeFloat);
            if (dynamicAnimation == AnimsPugilistSteve.OBSIDIAN_FIST_DASH && entityState.getLevel() > 1) {
               ItemStack itemstack = ItemStack.f_41583_;
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
            } else if ((
                  dynamicAnimation != AnimsPugilistSteve.SHADOW_OBSIDIAN_SWORD_ONEHAND_LONG
                        && dynamicAnimation != AnimsEpicFight.SHADOW_OBSIDIAN_FIST_AIR_SLASH
                        && dynamicAnimation != AnimsPugilistSteve.SHADOW_OBSIDIAN_SWORD_DUAL_SWORD_AUTO4
                        && dynamicAnimation != AnimsPugilistSteve.SHADOW_OBSIDIAN_SWORD_DUAL_SWORD_AUTO5
                        && !(dynamicAnimation.get() instanceof ExecutionAttackAnimation)
                     || entityState.getLevel() <= 1
               )
               && dynamicAnimation != AnimsWom.SHADOW_OBSIDIAN_SWORD_TORMENT_AIRSLAM
               && dynamicAnimation != AnimsWom.SHADOW_OBSIDIAN_SWORD_TORMENT_BERSERK_DASH
               && dynamicAnimation != AnimsEpicFightDualGreatsword.SHADOW_OBSIDIAN_SWORD_GREATSWORD_DUAL_AIRSLASH
               && dynamicAnimation != AnimsEpicFightDualGreatsword.SHADOW_OBSIDIAN_SWORD_GREATSWORD_DUAL_EARTHQUAKE
               && dynamicAnimation != AnimsEpicFightDualGreatsword.SHADOW_OBSIDIAN_SWORD_GREATSWORD_DUAL_EARTHQUAKE_PILLAR) {
               ItemStack itemstack = new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_SWORD.get());
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
               ItemStack itemstack = new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_STRAIGHT.get());
               if (itemstack.m_41783_() != null) {
                  itemstack.m_41783_().m_128379_("foil", ((LivingEntity)livingEntityPatch.getOriginal()).m_21205_().m_41793_());
               }

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

         if (hand == InteractionHand.OFF_HAND
            && ((LivingEntity)livingEntityPatch.getOriginal()).m_21206_().m_41720_().equals(AnnoyingVillagersModItems.SHADOW_OBSIDIAN_SWORD.get())) {
            OpenMatrix4f openmatrix4f = new OpenMatrix4f(this.getCorrectionMatrix(livingEntityPatch, InteractionHand.OFF_HAND, poses));
            AnimationPlayer animationPlayer = Objects.requireNonNull(livingEntityPatch.getAnimator().getPlayerFor(null));
            AssetAccessor<? extends StaticAnimation> dynamicAnimation = animationPlayer.getRealAnimation();
            float elapsedTimeFloat = animationPlayer.getElapsedTime();
            EntityState entityState = ((StaticAnimation)dynamicAnimation.get()).getState(livingEntityPatch, elapsedTimeFloat);
            if ((
                  dynamicAnimation != AnimsEpicFightInfernalGainer.OBSIDIAN_INFERNAL_AUTO_1
                        && dynamicAnimation != AnimsWom.OBSIDIAN_STRONG_PUNCH
                        && dynamicAnimation != AnimsEpicFight.SHADOW_OBSIDIAN_FIST_AUTO1
                        && dynamicAnimation != AnimsEpicFight.SHADOW_OBSIDIAN_FIST_AUTO3
                     || entityState.getLevel() <= 1
               )
               && (dynamicAnimation != AnimsWom.SHADOW_OBSIDIAN_SWORD_GESETZ_AUTO_3 || entityState.getLevel() <= 2)) {
               if ((
                     dynamicAnimation != AnimsPugilistSteve.SHADOW_OBSIDIAN_SWORD_DUAL_SWORD_AUTO4
                           && dynamicAnimation != AnimsPugilistSteve.SHADOW_OBSIDIAN_SWORD_DUAL_SWORD_AUTO5
                           && !(dynamicAnimation.get() instanceof ExecutionAttackAnimation)
                        || entityState.getLevel() <= 1
                  )
                  && dynamicAnimation != AnimsEpicFightDualGreatsword.SHADOW_OBSIDIAN_SWORD_GREATSWORD_DUAL_AUTO_3
                  && dynamicAnimation != AnimsEpicFightDualGreatsword.SHADOW_OBSIDIAN_SWORD_GREATSWORD_DUAL_AIRSLASH
                  && dynamicAnimation != AnimsEpicFightDualGreatsword.SHADOW_OBSIDIAN_SWORD_GREATSWORD_DUAL_EARTHQUAKE
                  && dynamicAnimation != AnimsEpicFightDualGreatsword.SHADOW_OBSIDIAN_SWORD_GREATSWORD_DUAL_EARTHQUAKE_PILLAR) {
                  ItemStack itemstack = new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_SWORD.get());
                  poseStack.m_85836_();
                  MathUtils.mulStack(poseStack, openmatrix4f);
                  poseStack.m_252781_(Axis.f_252436_.m_252977_(45.0F));
                  Minecraft.m_91087_()
                     .m_91291_()
                     .m_269128_(
                        itemstack,
                        ItemDisplayContext.THIRD_PERSON_LEFT_HAND,
                        packedLight,
                        OverlayTexture.f_118083_,
                        poseStack,
                        buffer,
                        ((LivingEntity)livingEntityPatch.getOriginal()).m_9236_(),
                        0
                     );
                  poseStack.m_85849_();
               } else {
                  ItemStack itemstack = new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_STRAIGHT.get());
                  if (itemstack.m_41783_() != null) {
                     itemstack.m_41783_().m_128379_("foil", ((LivingEntity)livingEntityPatch.getOriginal()).m_21206_().m_41793_());
                  }

                  poseStack.m_85836_();
                  MathUtils.mulStack(poseStack, openmatrix4f);
                  Minecraft.m_91087_()
                     .m_91291_()
                     .m_269128_(
                        itemstack,
                        ItemDisplayContext.THIRD_PERSON_LEFT_HAND,
                        packedLight,
                        OverlayTexture.f_118083_,
                        poseStack,
                        buffer,
                        ((LivingEntity)livingEntityPatch.getOriginal()).m_9236_(),
                        0
                     );
                  poseStack.m_85849_();
               }
            } else {
               ItemStack itemstack = ItemStack.f_41583_;
               poseStack.m_85836_();
               MathUtils.mulStack(poseStack, openmatrix4f);
               Minecraft.m_91087_()
                  .m_91291_()
                  .m_269128_(
                     itemstack,
                     ItemDisplayContext.THIRD_PERSON_LEFT_HAND,
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
}
