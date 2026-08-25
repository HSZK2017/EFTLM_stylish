package com.pla.annoyingvillagers.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.pla.annoyingvillagers.entity.FloatingLookBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class FloatingLookBlockRenderer extends EntityRenderer<FloatingLookBlockEntity> {
   public FloatingLookBlockRenderer(Context context) {
      super(context);
      this.f_114477_ = 0.35F;
   }

   public void render(
      FloatingLookBlockEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight
   ) {
      BlockState blockState = entity.getCarriedBlock();
      if (!blockState.m_60795_() && blockState.m_60799_() == RenderShape.MODEL) {
         poseStack.m_85836_();
         float age = (float)entity.f_19797_ + partialTicks;
         if (entity.getPhase() == 1) {
            poseStack.m_252781_(Axis.f_252436_.m_252977_(age * 2.5F));
         } else {
            poseStack.m_252781_(Axis.f_252436_.m_252977_(age * 1.0F));
         }

         poseStack.m_85837_(-0.5, -0.5, -0.5);
         Minecraft.m_91087_().m_91289_().m_110912_(blockState, poseStack, buffer, packedLight, OverlayTexture.f_118083_);
         poseStack.m_85849_();
         super.m_7392_(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
      }
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull FloatingLookBlockEntity entity) {
      return TextureAtlas.f_118259_;
   }
}
