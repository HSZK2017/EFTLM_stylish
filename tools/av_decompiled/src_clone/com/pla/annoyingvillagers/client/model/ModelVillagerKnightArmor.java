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
import net.minecraft.world.entity.Entity;

public class ModelVillagerKnightArmor<T extends Entity> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "modelvillagerknightarmor"), "main"
   );
   public final ModelPart Head;

   public ModelVillagerKnightArmor(ModelPart modelpart) {
      this.Head = modelpart.m_171324_("Head");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.m_171576_();
      partdefinition.m_171599_(
         "Head",
         CubeListBuilder.m_171558_()
            .m_171514_(26, 14)
            .m_171488_(-5.0F, -2.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .m_171514_(28, 0)
            .m_171488_(-5.0F, -10.0F, -5.0F, 10.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
            .m_171514_(11, 12)
            .m_171488_(-4.0F, -11.0F, -5.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .m_171514_(11, 9)
            .m_171488_(-5.0F, -6.0F, -5.5F, 10.0F, 1.0F, 0.5F, new CubeDeformation(0.0F))
            .m_171514_(0, 1)
            .m_171488_(-5.0F, -5.0F, -5.5F, 1.0F, 1.0F, 0.5F, new CubeDeformation(0.0F))
            .m_171514_(5, 11)
            .m_171488_(-1.0F, -4.0F, -6.0F, 2.0F, 1.0F, 0.5F, new CubeDeformation(0.0F))
            .m_171514_(0, 0)
            .m_171488_(4.0F, -5.0F, -5.5F, 1.0F, 1.0F, 0.5F, new CubeDeformation(0.0F))
            .m_171514_(11, 14)
            .m_171488_(-4.0F, -4.0F, -5.5F, 8.0F, 1.0F, 0.5F, new CubeDeformation(0.0F))
            .m_171514_(22, 24)
            .m_171488_(-1.0F, -11.7F, -5.0F, 2.0F, 1.0F, 9.0F, new CubeDeformation(0.0F))
            .m_171514_(0, 0)
            .m_171488_(-5.0F, -11.0F, -4.0F, 10.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
            .m_171514_(11, 10)
            .m_171488_(-4.0F, -11.0F, 4.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .m_171514_(0, 29)
            .m_171488_(-5.0F, -3.0F, -5.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .m_171514_(33, 12)
            .m_171488_(-5.0F, -3.0F, -4.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .m_171514_(33, 8)
            .m_171488_(-5.0F, -3.0F, 1.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .m_171514_(22, 15)
            .m_171488_(-5.0F, -2.0F, 2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .m_171514_(15, 15)
            .m_171488_(-5.0F, -1.0F, 3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .m_171514_(5, 25)
            .m_171488_(3.0F, -3.0F, -5.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .m_171514_(32, 34)
            .m_171488_(-5.0F, -2.0F, -5.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
            .m_171514_(22, 9)
            .m_171488_(-0.5F, -13.0F, -6.0F, 1.0F, 6.0F, 9.0F, new CubeDeformation(0.0F))
            .m_171514_(33, 8)
            .m_171488_(-0.5F, -14.0F, -6.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
            .m_171514_(0, 25)
            .m_171488_(-0.5F, -15.0F, -6.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .m_171514_(7, 28)
            .m_171488_(4.0F, -2.0F, -5.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
            .m_171514_(11, 16)
            .m_171488_(-5.0F, -10.0F, -4.0F, 1.0F, 7.0F, 9.0F, new CubeDeformation(0.0F))
            .m_171514_(5, 13)
            .m_171488_(4.0F, -1.0F, 3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .m_171514_(11, 15)
            .m_171488_(4.0F, -2.0F, 2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .m_171514_(0, 13)
            .m_171488_(4.0F, -3.0F, 1.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .m_171514_(0, 9)
            .m_171488_(4.0F, -10.0F, -4.0F, 1.0F, 7.0F, 9.0F, new CubeDeformation(0.0F))
            .m_171514_(0, 32)
            .m_171488_(-4.0F, -10.0F, 4.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
            .m_171514_(18, 34)
            .m_171488_(-3.0F, -2.0F, 4.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .m_171514_(0, 6)
            .m_171488_(-1.0F, -1.0F, 4.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .m_171514_(0, 9)
            .m_171488_(4.0F, -3.0F, -4.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .m_171514_(5, 9)
            .m_171488_(4.0F, -2.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .m_171514_(0, 0)
            .m_171488_(-1.0F, -3.0F, -6.5F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.m_171419_(0.0F, 0.0F, 0.0F)
      );
      return LayerDefinition.m_171565_(meshdefinition, 64, 64);
   }

   public void m_7695_(PoseStack posestack, VertexConsumer vertexconsumer, int i, int j, float f, float f1, float f2, float f3) {
      this.Head.m_104306_(posestack, vertexconsumer, i, j, f, f1, f2, f3);
   }

   public void m_6973_(T t0, float f, float f1, float f2, float f3, float f4) {
      this.Head.f_104204_ = f3 / (180.0F / (float)Math.PI);
      this.Head.f_104203_ = f4 / (180.0F / (float)Math.PI);
   }
}
