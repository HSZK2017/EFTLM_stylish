package com.pla.annoyingvillagers.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.pla.annoyingvillagers.entity.BlockProjectileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockProjectileRenderer extends EntityRenderer<BlockProjectileEntity> {
   public BlockProjectileRenderer(Context ctx) {
      super(ctx);
   }

   public void render(BlockProjectileEntity entity, float yaw, float partialTicks, PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
      BlockState block = entity.getCarriedBlock();
      poseStack.m_85836_();
      poseStack.m_85841_(1.0F, 1.0F, 1.0F);
      poseStack.m_85837_(-0.5, -0.5, -0.5);
      float age = (float)entity.f_19797_ + partialTicks;
      poseStack.m_252781_(Axis.f_252529_.m_252977_(entity.getRotX() * age));
      poseStack.m_252781_(Axis.f_252436_.m_252977_(entity.getRotY() * age));
      poseStack.m_252781_(Axis.f_252403_.m_252977_(entity.getRotZ() * age));
      Minecraft.m_91087_().m_91289_().m_110912_(block, poseStack, buffer, packedLight, OverlayTexture.f_118083_);
      poseStack.m_85849_();
      super.m_7392_(entity, yaw, partialTicks, poseStack, buffer, packedLight);
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull BlockProjectileEntity blockProjectileEntity) {
      return TextureAtlas.f_118259_;
   }
}
