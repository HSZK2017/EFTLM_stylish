package com.dmc.invincible_dmc.client.renderer.entity;

import com.dmc.invincible_dmc.entity.portal.PortalEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class PortalEntityRenderer extends EntityRenderer<PortalEntity> {
   private static final RenderType PORTAL_RENDER_TYPE = RenderType.m_173239_();

   public PortalEntityRenderer(Context context) {
      super(context);
      this.f_114477_ = 0.0F;
   }

   public void render(PortalEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
      float scale = entity.getScale();
      float halfWidth = 2.0F * scale;
      float height = 2.7F * scale;
      poseStack.m_85836_();
      if (entity.isClosing()) {
         float closeProgress = 1.0F - ((float)(entity.f_19797_ % 6) + partialTicks) / 6.0F;
         if (closeProgress < 0.0F) {
            closeProgress = 0.0F;
         }

         poseStack.m_85841_(closeProgress, closeProgress, closeProgress);
      }

      poseStack.m_252781_(Axis.f_252436_.m_252977_(180.0F - entityYaw));
      Matrix4f pose = poseStack.m_85850_().m_252922_();
      VertexConsumer consumer = buffer.m_6299_(PORTAL_RENDER_TYPE);
      this.drawQuad(consumer, pose, -halfWidth, 0.0F, 0.0F, halfWidth, height, 0.0F);
      this.drawQuad(consumer, pose, halfWidth, 0.0F, 0.0F, -halfWidth, height, 0.0F);
      poseStack.m_85849_();
      super.m_7392_(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
   }

   private void drawQuad(VertexConsumer consumer, Matrix4f pose, float x1, float y1, float z1, float x2, float y2, float z2) {
      consumer.m_252986_(pose, x1, y1, z1).m_5752_();
      consumer.m_252986_(pose, x2, y1, z2).m_5752_();
      consumer.m_252986_(pose, x2, y2, z2).m_5752_();
      consumer.m_252986_(pose, x1, y2, z1).m_5752_();
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull PortalEntity entity) {
      return ResourceLocation.parse("minecraft:textures/entity/end_portal.png");
   }
}
