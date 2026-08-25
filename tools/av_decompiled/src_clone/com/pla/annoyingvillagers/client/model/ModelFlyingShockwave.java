package com.pla.annoyingvillagers.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.pla.annoyingvillagers.entity.FlyingShockwaveProjectile;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ModelFlyingShockwave<T extends FlyingShockwaveProjectile> extends EntityModel<T> {
   private final ModelPart bb_main;
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "flying_shockwave"), "main"
   );

   public ModelFlyingShockwave(ModelPart root) {
      this.bb_main = root.m_171324_("bb_main");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.m_171576_();
      PartDefinition Core = partdefinition.m_171599_("bb_main", CubeListBuilder.m_171558_(), PartPose.m_171423_(25.375F, 2.0708F, 0.5F, 0.0F, 0.5236F, 0.0F));
      PartDefinition cube_r1 = Core.m_171599_(
         "cube_r1",
         CubeListBuilder.m_171558_()
            .m_171514_(30, 0)
            .m_171488_(1.0F, -3.0F, 0.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .m_171514_(24, 27)
            .m_171488_(0.0F, -9.0F, 0.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.m_171423_(24.625F, 9.6792F, -0.5F, 0.0F, 0.0F, -0.4363F)
      );
      PartDefinition cube_r2 = Core.m_171599_(
         "cube_r2",
         CubeListBuilder.m_171558_().m_171514_(12, 20).m_171488_(-1.0F, -9.0F, 0.0F, 5.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.m_171423_(22.0116F, 3.0054F, -0.5F, 0.0F, 0.0F, -0.9163F)
      );
      PartDefinition cube_r3 = Core.m_171599_(
         "cube_r3",
         CubeListBuilder.m_171558_().m_171514_(16, 10).m_171488_(-2.0F, -9.0F, 0.0F, 6.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.m_171423_(15.7758F, -1.9514F, -0.5F, 0.0F, 0.0F, -1.1781F)
      );
      PartDefinition cube_r4 = Core.m_171599_(
         "cube_r4",
         CubeListBuilder.m_171558_().m_171514_(0, 10).m_171488_(-3.0F, -9.0F, 0.0F, 7.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.m_171423_(8.9916F, -5.091F, -0.5F, 0.0F, 0.0F, -1.5708F)
      );
      PartDefinition cube_r5 = Core.m_171599_(
         "cube_r5",
         CubeListBuilder.m_171558_().m_171514_(0, 0).m_171488_(-4.0F, -9.0F, 0.0F, 7.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.m_171423_(-8.9916F, -5.091F, -0.5F, 0.0F, 0.0F, 1.5708F)
      );
      PartDefinition cube_r6 = Core.m_171599_(
         "cube_r6",
         CubeListBuilder.m_171558_().m_171514_(16, 0).m_171488_(-4.0F, -9.0F, 0.0F, 6.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.m_171423_(-15.7758F, -1.9514F, -0.5F, 0.0F, 0.0F, 1.1781F)
      );
      PartDefinition cube_r7 = Core.m_171599_(
         "cube_r7",
         CubeListBuilder.m_171558_().m_171514_(0, 20).m_171488_(-4.0F, -9.0F, 0.0F, 5.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.m_171423_(-22.0116F, 3.0054F, -0.5F, 0.0F, 0.0F, 0.9163F)
      );
      PartDefinition cube_r8 = Core.m_171599_(
         "cube_r8",
         CubeListBuilder.m_171558_()
            .m_171514_(0, 30)
            .m_171488_(-4.0F, -3.0F, 0.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .m_171514_(24, 20)
            .m_171488_(-4.0F, -9.0F, 0.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.m_171423_(-24.625F, 9.6792F, -0.5F, 0.0F, 0.0F, 0.4363F)
      );
      return LayerDefinition.m_171565_(meshdefinition, 64, 64);
   }

   public void m_7695_(
      @NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha
   ) {
      this.bb_main.m_104306_(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
   }

   public void setupAnim(T t, float v, float v1, float v2, float v3, float v4) {
   }
}
