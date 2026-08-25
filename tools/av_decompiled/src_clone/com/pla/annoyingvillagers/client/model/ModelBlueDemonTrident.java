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

public class ModelBlueDemonTrident<T extends Entity> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "modelbluedemontrident"), "main"
   );
   public final ModelPart trident;

   public ModelBlueDemonTrident(ModelPart modelpart) {
      this.trident = modelpart.m_171324_("trident");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.m_171576_();
      partdefinition.m_171599_(
         "trident",
         CubeListBuilder.m_171558_()
            .m_171514_(0, 0)
            .m_171488_(-0.51F, -28.01F, -0.51F, 1.02F, 31.02F, 1.02F, new CubeDeformation(0.0F))
            .m_171514_(4, 0)
            .m_171488_(-1.5F, -24.0F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .m_171514_(4, 3)
            .m_171488_(-2.5F, -27.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .m_171514_(4, 3)
            .m_171488_(1.5F, -27.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.m_171423_(0.0F, 24.0F, 0.0F, 0.0F, 1.5708F, -3.1416F)
      );
      return LayerDefinition.m_171565_(meshdefinition, 32, 32);
   }

   public void m_7695_(PoseStack posestack, VertexConsumer vertexconsumer, int i, int j, float f, float f1, float f2, float f3) {
      this.trident.m_104306_(posestack, vertexconsumer, i, j, f, f1, f2, f3);
   }

   public void m_6973_(T t0, float f, float f1, float f2, float f3, float f4) {
   }
}
