package com.pla.annoyingvillagers.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.pla.annoyingvillagers.util.HookUtil;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;

final class HookItemRenderTransforms {
   private static final float SWORD_PROJECTILE_ROLL = -45.0F;
   private static final float CUSTOM_3D_FIXED_POSITIVE_Y_ROLL = -45.0F;
   private static final float CUSTOM_3D_FIXED_NEGATIVE_Y_ROLL = -135.0F;
   private static final float PICKAXE_HOE_ALIGNMENT_ROLL = -90.0F;
   private static final float PICKAXE_HOE_ALIGNMENT_PITCH = -45.0F;
   private static final float AXE_ALIGNMENT_YAW = 45.0F;
   private static final float SHOVEL_ALIGNMENT_ROLL = -45.0F;
   private static final float HOOK_GUN_ATTACHMENT_ROLL = -90.0F;
   private static final double HOOK_GUN_ATTACHMENT_X = 0.9;
   private static final double HOOK_GUN_ATTACHMENT_Y = 0.1;
   private static final double HOOK_GUN_ATTACHMENT_Z = 0.55;
   private static final float HOOK_GUN_ATTACHMENT_SCALE = 0.65F;
   private static final double SHIELD_HOOK_GUN_ATTACHMENT_Z_OFFSET = 0.25;
   private static final float HOOK_GUN_PROJECTILE_SCALE = 0.5F;
   private static final float SHIELD_HOOK_GUN_PROJECTILE_SCALE = 1.0F;
   private static final double ITEM_DISPLAY_TRANSLATION_SCALE = 16.0;

   private HookItemRenderTransforms() {
   }

   static void applyProjectileFacing(PoseStack poseStack, ItemStack stack, float yaw, float pitch) {
      applyProjectileFacing(poseStack, stack, null, yaw, pitch);
   }

   static void applyProjectileFacing(PoseStack poseStack, ItemStack stack, @Nullable BakedModel model, float yaw, float pitch) {
      poseStack.m_252781_(Axis.f_252436_.m_252977_(yaw - 90.0F));
      poseStack.m_252781_(Axis.f_252403_.m_252977_(-pitch));
      if (HookUtil.shouldAlignSharpEdge(stack)) {
         applySharpModelAlignment(poseStack, stack, model);
      }
   }

   static ItemDisplayContext getProjectileDisplayContext(ItemStack stack) {
      return getProjectileDisplayContext(stack, null);
   }

   static ItemDisplayContext getProjectileDisplayContext(ItemStack stack, @Nullable BakedModel model) {
      if (isCustom3DSharpModel(stack, model)) {
         return ItemDisplayContext.FIXED;
      } else {
         return HookUtil.shouldAlignSharpEdge(stack) ? ItemDisplayContext.NONE : ItemDisplayContext.FIXED;
      }
   }

   static boolean shouldUseDisplayAttachmentRenderer(ItemStack stack, ItemDisplayContext context) {
      return true;
   }

   static ItemDisplayContext getHookGunAttachmentDisplayContext(ItemStack stack, ItemDisplayContext context) {
      return HookUtil.shouldUseShieldFacing(stack) ? ItemDisplayContext.NONE : ItemDisplayContext.GUI;
   }

   static void applyHookGunAttachment(PoseStack poseStack, ItemStack stack, ItemDisplayContext context) {
      double zOffset = HookUtil.shouldUseShieldFacing(stack) ? 0.25 : 0.0;
      applyHookGunAttachmentTransform(poseStack, zOffset);
   }

   static ItemDisplayContext getHookGunProjectileDisplayContext(ItemStack stack, @Nullable BakedModel model) {
      return HookUtil.shouldUseShieldFacing(stack) ? ItemDisplayContext.NONE : getProjectileDisplayContext(stack, model);
   }

   static float getHookGunProjectileScale(ItemStack stack) {
      return HookUtil.shouldUseShieldFacing(stack) ? 1.0F : 0.5F;
   }

   static void applyShieldProjectileTransform(PoseStack poseStack, @Nullable BakedModel model) {
      if (model != null && model.m_7442_().m_269504_(ItemDisplayContext.FIXED)) {
         ItemTransform fixedTransform = model.m_7442_().m_269404_(ItemDisplayContext.FIXED);
         poseStack.m_85837_(
            (double)(-fixedTransform.f_111756_.x()) / 16.0, (double)(-fixedTransform.f_111756_.y()) / 16.0, (double)(-fixedTransform.f_111756_.z()) / 16.0
         );
      }
   }

   private static void applyHookGunAttachmentTransform(PoseStack poseStack, double zOffset) {
      poseStack.m_85837_(0.9, 0.1, 0.55 + zOffset);
      poseStack.m_252781_(Axis.f_252403_.m_252977_(-90.0F));
      poseStack.m_85841_(0.65F, 0.65F, 0.65F);
   }

   private static void applySharpModelAlignment(PoseStack poseStack, ItemStack stack, @Nullable BakedModel model) {
      if (isCustom3DSharpModel(stack, model)) {
         poseStack.m_252781_(Axis.f_252403_.m_252977_(getCustom3DFixedModelRoll(model)));
      } else if (isPickaxeLike(stack) || isHoeLike(stack)) {
         poseStack.m_252781_(Axis.f_252403_.m_252977_(-90.0F));
         poseStack.m_252781_(Axis.f_252529_.m_252977_(-45.0F));
      } else if (isAxeLike(stack)) {
         poseStack.m_252781_(Axis.f_252436_.m_252977_(45.0F));
      } else if (isShovelLike(stack)) {
         poseStack.m_252781_(Axis.f_252403_.m_252977_(-45.0F));
      } else {
         poseStack.m_252781_(Axis.f_252403_.m_252977_(-45.0F));
      }
   }

   private static boolean isCustom3DSharpModel(ItemStack stack, @Nullable BakedModel model) {
      return model != null && model.m_7539_() && model.m_7442_().m_269504_(ItemDisplayContext.FIXED) && HookUtil.shouldAlignSharpEdge(stack);
   }

   private static float getCustom3DFixedModelRoll(BakedModel model) {
      ItemTransform fixedTransform = model.m_7442_().m_269404_(ItemDisplayContext.FIXED);
      return !(fixedTransform.f_111755_.y() < 0.0F) && !(Math.abs(fixedTransform.f_111755_.z()) >= 135.0F) ? -45.0F : -135.0F;
   }

   private static boolean isPickaxeLike(ItemStack stack) {
      return !stack.m_41619_() && (stack.m_41720_() instanceof PickaxeItem || stack.canPerformAction(ToolActions.PICKAXE_DIG));
   }

   private static boolean isAxeLike(ItemStack stack) {
      return !stack.m_41619_()
         && !(stack.m_41720_() instanceof SwordItem)
         && (stack.m_41720_() instanceof AxeItem || stack.canPerformAction(ToolActions.AXE_DIG));
   }

   private static boolean isHoeLike(ItemStack stack) {
      return !stack.m_41619_() && (stack.m_41720_() instanceof HoeItem || stack.canPerformAction(ToolActions.HOE_DIG));
   }

   private static boolean isShovelLike(ItemStack stack) {
      return !stack.m_41619_() && (stack.m_41720_() instanceof ShovelItem || stack.canPerformAction(ToolActions.SHOVEL_DIG));
   }
}
