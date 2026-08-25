package com.pla.annoyingvillagers.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class SpriteArrowRenderer extends EntityRenderer<AbstractArrow> {
   private final ItemRenderer renderer;

   public SpriteArrowRenderer(Context context) {
      super(context);
      this.renderer = context.m_174025_();
   }

   public void render(AbstractArrow abstractArrow, float pEntityYaw, float pPartialTicks, PoseStack poseStack, MultiBufferSource buffer, int pPackedLight) {
      poseStack.m_85836_();
      poseStack.m_252781_(Axis.f_252436_.m_252977_(Mth.m_14179_(pPartialTicks, abstractArrow.f_19859_, abstractArrow.m_146908_()) - 90.0F));
      poseStack.m_252781_(Axis.f_252403_.m_252977_(Mth.m_14179_(pPartialTicks, abstractArrow.f_19860_, abstractArrow.m_146909_())));
      poseStack.m_85837_(-0.2, 0.0, 0.0);
      poseStack.m_252781_(Axis.f_252403_.m_252977_(-45.0F));
      poseStack.m_85841_(1.5F, 1.5F, 1.5F);
      ItemStack pickupItem = abstractArrow.m_7941_();
      if (pickupItem.m_150930_(Items.f_42412_) && abstractArrow instanceof Arrow arrow) {
         int color = arrow.m_36889_();
         if (color != -1) {
            pickupItem = Items.f_42738_.m_7968_();
            pickupItem.m_41784_().m_128405_("CustomPotionColor", color);
         }
      }

      this.renderer
         .m_269128_(
            pickupItem, ItemDisplayContext.GROUND, pPackedLight, OverlayTexture.f_118083_, poseStack, buffer, abstractArrow.m_9236_(), abstractArrow.m_19879_()
         );
      poseStack.m_85849_();
      super.m_7392_(abstractArrow, pEntityYaw, pPartialTicks, poseStack, buffer, pPackedLight);
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull AbstractArrow entity) {
      return TippableArrowRenderer.f_116132_;
   }
}
