package com.pla.annoyingvillagers.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.pla.annoyingvillagers.client.engine.PhotonClientFxUtil;
import com.pla.annoyingvillagers.config.AnnoyingVillagersClientConfig;
import com.pla.annoyingvillagers.entity.PortalEntity;
import com.pla.annoyingvillagers.util.AAAParticlesUtil;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class PortalEntityRenderer extends EntityRenderer<PortalEntity> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/portal.png");
   private static final String PHOTON_PORTAL_EFFECT = "snakeportal";
   private static final int PHOTON_PORTAL_LIFETIME_TICKS = 12;
   private static final int AAA_PORTAL_REFRESH_TICKS = 10;
   private static final Map<Integer, Long> LAST_AAA_PORTAL_PLAY_TICK = new HashMap<>();

   public PortalEntityRenderer(Context context) {
      super(context);
   }

   public void render(
      @NotNull PortalEntity portal, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight
   ) {
      if (shouldRenderWithPhoton() && playPhotonPortalVisual(portal)) {
         super.m_7392_(portal, entityYaw, partialTicks, poseStack, buffer, packedLight);
      } else if (shouldRenderWithAaa() && playAaaPortalVisual(portal)) {
         super.m_7392_(portal, entityYaw, partialTicks, poseStack, buffer, packedLight);
      } else {
         poseStack.m_85836_();
         float yaw = Mth.m_14179_(partialTicks, portal.f_19859_, portal.m_146908_());
         poseStack.m_252781_(Axis.f_252436_.m_252977_(-yaw));
         VertexConsumer consumer = buffer.m_6299_(RenderType.m_110473_(this.getTextureLocation(portal)));
         Matrix4f matrix = poseStack.m_85850_().m_252922_();
         Matrix3f normal = poseStack.m_85850_().m_252943_();
         int light = 15728880;
         float halfWidth = 1.1F;
         float height = 3.0F;
         this.drawVertex(consumer, matrix, normal, -halfWidth, 0.0F, 0.0F, 0.0F, 1.0F, light, 1.0F);
         this.drawVertex(consumer, matrix, normal, halfWidth, 0.0F, 0.0F, 1.0F, 1.0F, light, 1.0F);
         this.drawVertex(consumer, matrix, normal, halfWidth, height, 0.0F, 1.0F, 0.0F, light, 1.0F);
         this.drawVertex(consumer, matrix, normal, -halfWidth, height, 0.0F, 0.0F, 0.0F, light, 1.0F);
         this.drawVertex(consumer, matrix, normal, -halfWidth, height, 0.0F, 0.0F, 0.0F, light, -1.0F);
         this.drawVertex(consumer, matrix, normal, halfWidth, height, 0.0F, 1.0F, 0.0F, light, -1.0F);
         this.drawVertex(consumer, matrix, normal, halfWidth, 0.0F, 0.0F, 1.0F, 1.0F, light, -1.0F);
         this.drawVertex(consumer, matrix, normal, -halfWidth, 0.0F, 0.0F, 0.0F, 1.0F, light, -1.0F);
         poseStack.m_85849_();
         super.m_7392_(portal, entityYaw, partialTicks, poseStack, buffer, packedLight);
      }
   }

   private static boolean shouldRenderWithPhoton() {
      return AnnoyingVillagersClientConfig.shouldUsePhotonWhenAvailable(AnnoyingVillagersClientConfig.VfxEffect.TELEPORT_PORTAL);
   }

   private static boolean playPhotonPortalVisual(PortalEntity portal) {
      return portal.m_9236_().f_46443_ && !portal.m_213877_()
         ? PhotonClientFxUtil.followPortal(
            "teleport_portal:" + portal.m_19879_(),
            portal.m_9236_(),
            "snakeportal",
            () -> portal.m_213877_() ? null : portal.getPortalCenter(),
            () -> portal.m_213877_() ? null : portal.getNormal(),
            12
         )
         : false;
   }

   private static boolean shouldRenderWithAaa() {
      return AnnoyingVillagersClientConfig.shouldUseAaaParticles(AnnoyingVillagersClientConfig.VfxEffect.TELEPORT_PORTAL);
   }

   private static boolean playAaaPortalVisual(PortalEntity portal) {
      if (portal.m_9236_().f_46443_ && !portal.m_213877_()) {
         long gameTime = portal.m_9236_().m_46467_();
         Long lastPlayTick = LAST_AAA_PORTAL_PLAY_TICK.get(portal.m_19879_());
         if (lastPlayTick == null || gameTime < lastPlayTick || gameTime - lastPlayTick >= 10L) {
            if (!AAAParticlesUtil.sendTeleportPortal(portal.m_9236_(), portal.getPortalCenter(), portal.getNormal())) {
               LAST_AAA_PORTAL_PLAY_TICK.remove(portal.m_19879_());
               return false;
            }

            LAST_AAA_PORTAL_PLAY_TICK.put(portal.m_19879_(), gameTime);
         }

         return true;
      } else {
         LAST_AAA_PORTAL_PLAY_TICK.remove(portal.m_19879_());
         return false;
      }
   }

   private void drawVertex(
      VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, float x, float y, float z, float u, float v, int packedLight, float normalZ
   ) {
      consumer.m_252986_(matrix, x, y, z)
         .m_85950_(1.0F, 1.0F, 1.0F, 0.9F)
         .m_7421_(u, v)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(packedLight)
         .m_252939_(normal, 0.0F, 0.0F, normalZ)
         .m_5752_();
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull PortalEntity portal) {
      return TEXTURE;
   }
}
