package com.pla.annoyingvillagers.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.pla.annoyingvillagers.client.engine.ThunderRender;
import com.pla.annoyingvillagers.entity.BlueDemonThunderBeamEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class BlueDemonThunderBeamRenderer extends EntityRenderer<BlueDemonThunderBeamEntity> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/dragon_beam.png");
   private final ThunderRender thunderRender = new ThunderRender();

   public BlueDemonThunderBeamRenderer(Context pContext) {
      super(pContext);
   }

   @NotNull
   public Vec3 getRenderOffset(BlueDemonThunderBeamEntity dragonBeam, float p_114484_) {
      return new Vec3(
         dragonBeam.m_9236_().f_46441_.m_188583_() * 0.03, dragonBeam.m_9236_().f_46441_.m_188583_() * 0.03, dragonBeam.m_9236_().f_46441_.m_188583_() * 0.03
      );
   }

   public void render(
      @NotNull BlueDemonThunderBeamEntity blueDemonThunderBeamEntity,
      float entityYaw,
      float partialTicks,
      @NotNull PoseStack poseStack,
      @NotNull MultiBufferSource buffer,
      int packedLight
   ) {
      super.m_7392_(blueDemonThunderBeamEntity, entityYaw, partialTicks, poseStack, buffer, packedLight);
      if (blueDemonThunderBeamEntity.isSetUseNoVfxThunder()) {
         poseStack.m_85836_();
         Vec3 from = blueDemonThunderBeamEntity.getStartPos();
         Vec3 to = blueDemonThunderBeamEntity.getEndPos();
         ThunderRender.ThunderData bolt = new ThunderRender.ThunderData(ThunderRender.ThunderData.ThunderRenderInfo.BLUE_DEMON_THUNDER, from, to, 15)
            .size(0.1F)
            .lifespan(4)
            .spawn(ThunderRender.ThunderData.SpawnFunction.delay(1.0F));
         this.thunderRender.update(null, bolt, partialTicks);
         poseStack.m_85837_(-blueDemonThunderBeamEntity.m_20185_(), -blueDemonThunderBeamEntity.m_20186_(), -blueDemonThunderBeamEntity.m_20189_());
         this.thunderRender.render(partialTicks, poseStack, buffer);
         poseStack.m_85849_();
      }
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull BlueDemonThunderBeamEntity dragonBeam) {
      return TEXTURE;
   }

   public void drawVertex(
      Matrix4f matrix,
      Matrix3f normals,
      VertexConsumer vertexBuilder,
      float offsetX,
      float offsetY,
      float offsetZ,
      float textureX,
      float textureY,
      float alpha,
      int packedLightIn
   ) {
      vertexBuilder.m_252986_(matrix, offsetX, offsetY, offsetZ)
         .m_85950_(1.0F, 1.0F, 1.0F, alpha)
         .m_7421_(textureX, textureY)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(packedLightIn)
         .m_252939_(normals, 0.0F, 1.0F, 0.0F)
         .m_5752_();
   }
}
