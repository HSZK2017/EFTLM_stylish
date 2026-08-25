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

public class ModelHerobrineObsidianDiamondChestplate<T extends Entity> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "modelherobrineobsidiandiamondchestplate"), "main"
   );
   public final ModelPart Body;
   public final ModelPart RightArm;
   public final ModelPart LeftArm;

   public ModelHerobrineObsidianDiamondChestplate(ModelPart modelpart) {
      this.Body = modelpart.m_171324_("Body");
      this.RightArm = modelpart.m_171324_("RightArm");
      this.LeftArm = modelpart.m_171324_("LeftArm");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.m_171576_();
      PartDefinition partdefinition1 = partdefinition.m_171599_(
         "Body",
         CubeListBuilder.m_171558_()
            .m_171514_(16, 16)
            .m_171488_(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
            .m_171514_(54, 7)
            .m_171488_(-8.2385F, 1.609F, 1.8F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.m_171419_(0.0F, 0.0F, 0.0F)
      );
      partdefinition1.m_171599_(
         "cube_r1",
         CubeListBuilder.m_171558_().m_171514_(51, 4).m_171488_(-1.909F, -7.2615F, 16.3F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.m_171423_(2.0F, -15.0F, -2.0F, -1.4835F, 0.0873F, 0.0F)
      );
      partdefinition1.m_171599_(
         "cube_r2",
         CubeListBuilder.m_171558_().m_171514_(51, 4).m_171488_(-3.909F, -8.2615F, -20.7F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.m_171423_(2.0F, -15.0F, -2.0F, 1.6552F, 0.3049F, -0.0119F)
      );
      partdefinition1.m_171599_(
         "cube_r3",
         CubeListBuilder.m_171558_().m_171514_(56, 2).m_171488_(-1.909F, -6.2615F, -18.7F, 2.0F, 7.0F, 2.0F, new CubeDeformation(-0.1F)),
         PartPose.m_171423_(2.0F, -15.0F, -2.0F, 1.4835F, 0.1745F, 0.0F)
      );
      partdefinition1.m_171599_(
         "cube_r4",
         CubeListBuilder.m_171558_().m_171514_(51, 4).m_171488_(5.4F, 19.2F, 4.1F, 5.0F, 2.1F, 1.9F, new CubeDeformation(0.0F)),
         PartPose.m_171423_(10.0F, -15.0F, -10.0F, 0.0F, -1.9199F, 0.0F)
      );
      partdefinition1.m_171599_(
         "cube_r5",
         CubeListBuilder.m_171558_().m_171514_(51, 4).m_171488_(11.4F, 18.2F, 9.1F, 5.0F, 2.1F, 1.9F, new CubeDeformation(0.0F)),
         PartPose.m_171423_(10.0F, -15.0F, -10.0F, 0.0F, -1.5708F, 0.0F)
      );
      partdefinition1.m_171599_(
         "cube_r6",
         CubeListBuilder.m_171558_().m_171514_(52, 1).m_171488_(-4.2385F, 19.609F, 3.8F, 2.0F, 2.0F, 4.0F, new CubeDeformation(-0.1F)),
         PartPose.m_171423_(2.0F, -15.0F, -2.0F, 0.0F, -0.0873F, 0.0F)
      );
      PartDefinition partdefinition2 = partdefinition.m_171599_(
         "RightArm",
         CubeListBuilder.m_171558_()
            .m_171514_(40, 16)
            .m_171488_(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
            .m_171514_(54, 7)
            .m_171488_(-3.2385F, -0.391F, 1.8F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.m_171419_(-5.0F, 2.0F, 0.0F)
      );
      partdefinition2.m_171599_(
         "cube_r7",
         CubeListBuilder.m_171558_().m_171514_(51, 4).m_171488_(-12.6F, 16.2F, -18.9F, 5.0F, 2.1F, 1.9F, new CubeDeformation(0.0F)),
         PartPose.m_171423_(15.0F, -17.0F, -10.0F, 2.1639F, 1.4137F, 2.1562F)
      );
      partdefinition2.m_171599_(
         "cube_r8",
         CubeListBuilder.m_171558_().m_171514_(51, 4).m_171488_(-9.909F, -12.2615F, -16.7F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.m_171423_(7.0F, -17.0F, -2.0F, 1.7301F, 0.3982F, -0.0424F)
      );
      partdefinition2.m_171599_(
         "cube_r9",
         CubeListBuilder.m_171558_().m_171514_(49, 10).m_171488_(4.4F, 14.2F, 15.1F, 5.0F, 2.1F, 1.9F, new CubeDeformation(0.0F)),
         PartPose.m_171423_(15.0F, -17.0F, -10.0F, -0.81F, -1.2543F, 0.9694F)
      );
      partdefinition.m_171599_(
         "LeftArm",
         CubeListBuilder.m_171558_()
            .m_171514_(40, 16)
            .m_171480_()
            .m_171488_(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
            .m_171555_(false),
         PartPose.m_171419_(5.0F, 2.0F, 0.0F)
      );
      return LayerDefinition.m_171565_(meshdefinition, 64, 32);
   }

   public void m_7695_(PoseStack posestack, VertexConsumer vertexconsumer, int i, int j, float f, float f1, float f2, float f3) {
      this.Body.m_104306_(posestack, vertexconsumer, i, j, f, f1, f2, f3);
      this.RightArm.m_104306_(posestack, vertexconsumer, i, j, f, f1, f2, f3);
      this.LeftArm.m_104306_(posestack, vertexconsumer, i, j, f, f1, f2, f3);
   }

   public void m_6973_(T t0, float f, float f1, float f2, float f3, float f4) {
      this.RightArm.f_104203_ = Mth.m_14089_(f * 0.6662F + (float) Math.PI) * f1;
      this.LeftArm.f_104203_ = Mth.m_14089_(f * 0.6662F) * f1;
   }
}
