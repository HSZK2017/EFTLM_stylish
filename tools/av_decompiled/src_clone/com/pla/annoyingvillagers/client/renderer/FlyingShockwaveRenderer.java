package com.pla.annoyingvillagers.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.pla.annoyingvillagers.client.model.ModelFlyingShockwave;
import com.pla.annoyingvillagers.entity.FlyingShockwaveProjectile;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class FlyingShockwaveRenderer extends EntityRenderer<FlyingShockwaveProjectile> {
   private final Model model;

   public FlyingShockwaveRenderer(Context pContext) {
      super(pContext);
      this.model = new ModelFlyingShockwave(pContext.m_174023_(ModelFlyingShockwave.LAYER_LOCATION));
   }

   public void render(
      FlyingShockwaveProjectile pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight
   ) {
      pPoseStack.m_85836_();
      float yaw = Mth.m_14179_(pPartialTick, pEntity.f_19859_, pEntity.m_146908_());
      float pitch = Mth.m_14179_(pPartialTick, pEntity.f_19860_, pEntity.m_146909_());
      pPoseStack.m_252781_(Axis.f_252436_.m_252977_(yaw - 90.0F));
      pPoseStack.m_252781_(Axis.f_252529_.m_252977_(pitch + 35.0F));
      pPoseStack.m_252781_(Axis.f_252403_.m_252977_(pitch + 90.0F));
      pPoseStack.m_85837_(0.0, 0.0, -2.0);
      VertexConsumer vertexConsumer = ItemRenderer.m_115222_(pBuffer, this.model.m_103119_(this.getTextureLocation(pEntity)), false, pEntity.isFoil());
      this.model.m_7695_(pPoseStack, vertexConsumer, pPackedLight, OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, 1.0F);
      pPoseStack.m_85849_();
      super.m_7392_(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull FlyingShockwaveProjectile flyingShockwaveProjectile) {
      return ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/flying_shockwave.png");
   }
}
