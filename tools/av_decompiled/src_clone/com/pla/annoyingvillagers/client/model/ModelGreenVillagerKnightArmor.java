package com.pla.annoyingvillagers.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class ModelGreenVillagerKnightArmor<T extends Entity> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "modelgreenvillagerknightarmor"), "main"
   );
   public final ModelPart Body;
   public final ModelPart RightArm;
   public final ModelPart LeftArm;
   public final ModelPart RightLeg;
   public final ModelPart LeftLeg;

   public ModelGreenVillagerKnightArmor(ModelPart modelpart) {
      this.Body = modelpart.m_171324_("Body");
      this.RightArm = modelpart.m_171324_("RightArm");
      this.LeftArm = modelpart.m_171324_("LeftArm");
      this.RightLeg = modelpart.m_171324_("RightLeg");
      this.LeftLeg = modelpart.m_171324_("LeftLeg");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.m_171576_();
      PartDefinition partdefinition1 = partdefinition.m_171599_(
         "Body",
         CubeListBuilder.m_171558_().m_171514_(18, 16).m_171488_(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F)),
         PartPose.m_171419_(0.0F, 0.0F, 0.0F)
      );
      partdefinition1.m_171599_(
         "cube_r1",
         CubeListBuilder.m_171558_().m_171514_(0, 0).m_171488_(-5.0F, 0.2F, 3.0F, 10.0F, 19.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.m_171423_(0.0F, 0.0F, 0.0F, 0.3054F, 0.0F, 0.0F)
      );
      partdefinition.m_171599_(
         "RightArm",
         CubeListBuilder.m_171558_().m_171514_(28, 32).m_171488_(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
         PartPose.m_171423_(-5.0F, 2.0F, 0.0F, -0.1745F, 0.0F, 0.0F)
      );
      partdefinition.m_171599_(
         "LeftArm",
         CubeListBuilder.m_171558_().m_171514_(12, 32).m_171488_(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
         PartPose.m_171423_(5.0F, 2.0F, 0.0F, 0.2094F, 0.0F, 0.0F)
      );
      partdefinition.m_171599_(
         "RightLeg",
         CubeListBuilder.m_171558_().m_171514_(22, 0).m_171488_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
         PartPose.m_171423_(-1.9F, 12.0F, 0.0F, 0.192F, 0.0F, 0.0349F)
      );
      partdefinition.m_171599_(
         "LeftLeg",
         CubeListBuilder.m_171558_().m_171514_(0, 20).m_171488_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
         PartPose.m_171423_(1.9F, 12.0F, 0.0F, -0.1745F, 0.0F, -0.0349F)
      );
      return LayerDefinition.m_171565_(meshdefinition, 64, 64);
   }

   public void m_7695_(PoseStack posestack, VertexConsumer vertexconsumer, int i, int j, float f, float f1, float f2, float f3) {
      this.Body.m_104306_(posestack, vertexconsumer, i, j, f, f1, f2, f3);
      this.RightArm.m_104306_(posestack, vertexconsumer, i, j, f, f1, f2, f3);
      this.LeftArm.m_104306_(posestack, vertexconsumer, i, j, f, f1, f2, f3);
      this.RightLeg.m_104306_(posestack, vertexconsumer, i, j, f, f1, f2, f3);
      this.LeftLeg.m_104306_(posestack, vertexconsumer, i, j, f, f1, f2, f3);
   }

   public void m_6973_(T t0, float f, float f1, float f2, float f3, float f4) {
      this.RightArm.f_104203_ = Mth.m_14089_(f * 0.6662F + (float) Math.PI) * f1;
      this.LeftLeg.f_104203_ = Mth.m_14089_(f * 1.0F) * -1.0F * f1;
      this.LeftArm.f_104203_ = Mth.m_14089_(f * 0.6662F) * f1;
      this.RightLeg.f_104203_ = Mth.m_14089_(f * 1.0F) * 1.0F * f1;
   }
}
