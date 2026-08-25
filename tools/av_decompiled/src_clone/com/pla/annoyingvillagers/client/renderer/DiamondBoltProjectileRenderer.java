package com.pla.annoyingvillagers.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.pla.annoyingvillagers.entity.DiamondBoltProjectileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DiamondBoltProjectileRenderer extends EntityRenderer<DiamondBoltProjectileEntity> {
   private final ItemRenderer itemRenderer;

   public DiamondBoltProjectileRenderer(Context context) {
      super(context);
      this.itemRenderer = context.m_174025_();
      this.f_114477_ = 0.15F;
   }

   public void render(
      DiamondBoltProjectileEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight
   ) {
      ItemStack stack = entity.m_7941_();
      if (!stack.m_41619_()) {
         poseStack.m_85836_();
         poseStack.m_252781_(Axis.f_252436_.m_252977_(Mth.m_14179_(partialTick, entity.f_19859_, entity.m_146908_()) - 90.0F));
         poseStack.m_252781_(Axis.f_252403_.m_252977_(Mth.m_14179_(partialTick, entity.f_19860_, entity.m_146909_())));
         poseStack.m_85837_(-0.2, 0.0, 0.0);
         poseStack.m_252781_(Axis.f_252403_.m_252977_(-45.0F));
         poseStack.m_85841_(1.5F, 1.5F, 1.5F);
         this.itemRenderer
            .m_269128_(stack, ItemDisplayContext.GROUND, packedLight, OverlayTexture.f_118083_, poseStack, buffer, entity.m_9236_(), entity.m_19879_());
         poseStack.m_85849_();
      }

      super.m_7392_(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull DiamondBoltProjectileEntity entity) {
      return TextureAtlas.f_118259_;
   }
}
