package com.pla.annoyingvillagers.client.model;

import com.pla.annoyingvillagers.client.animation.HerobrineWardenAnimations;
import com.pla.annoyingvillagers.entity.HerobrineWardenEntity;
import net.minecraft.client.model.WardenModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class ModelHerobrineWarden extends WardenModel<HerobrineWardenEntity> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "modelherobrinewarden"), "main"
   );
   private final ModelPart left_ribcage_extra;
   private final ModelPart right_ribcage_extra;

   public ModelHerobrineWarden(ModelPart root) {
      super(root);
      ModelPart body = root.m_171324_("bone").m_171324_("body");
      this.left_ribcage_extra = body.m_171324_("left_ribcage").m_171324_("left_ribs_extra");
      this.right_ribcage_extra = body.m_171324_("right_ribcage").m_171324_("right_ribs_extra");
   }

   public static LayerDefinition m_233537_() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.m_171576_();
      PartDefinition bone = root.m_171599_("bone", CubeListBuilder.m_171558_(), PartPose.m_171419_(0.0F, 24.0F, 0.0F));
      PartDefinition body = bone.m_171599_(
         "body", CubeListBuilder.m_171558_().m_171514_(0, 0).m_171481_(-9.0F, -13.0F, -4.0F, 18.0F, 21.0F, 11.0F), PartPose.m_171419_(0.0F, -21.0F, 0.0F)
      );
      PartDefinition right_ribcage = body.m_171599_(
         "right_ribcage",
         CubeListBuilder.m_171558_().m_171514_(90, 11).m_171481_(-2.0F, -11.0F, -0.1F, 9.0F, 21.0F, 0.0F),
         PartPose.m_171419_(-7.0F, -2.0F, -4.0F)
      );
      PartDefinition left_ribcage = body.m_171599_(
         "left_ribcage",
         CubeListBuilder.m_171558_().m_171514_(90, 11).m_171480_().m_171481_(-7.0F, -11.0F, -0.1F, 9.0F, 21.0F, 0.0F).m_171555_(false),
         PartPose.m_171419_(7.0F, -2.0F, -4.0F)
      );
      PartDefinition head = body.m_171599_(
         "head", CubeListBuilder.m_171558_().m_171514_(0, 32).m_171481_(-8.0F, -16.0F, -5.0F, 16.0F, 16.0F, 10.0F), PartPose.m_171419_(0.0F, -13.0F, 0.0F)
      );
      head.m_171599_(
         "right_tendril",
         CubeListBuilder.m_171558_().m_171514_(52, 32).m_171481_(-16.0F, -13.0F, 0.0F, 16.0F, 16.0F, 0.0F),
         PartPose.m_171419_(-8.0F, -12.0F, 0.0F)
      );
      head.m_171599_(
         "left_tendril", CubeListBuilder.m_171558_().m_171514_(58, 0).m_171481_(0.0F, -13.0F, 0.0F, 16.0F, 16.0F, 0.0F), PartPose.m_171419_(8.0F, -12.0F, 0.0F)
      );
      body.m_171599_(
         "right_arm", CubeListBuilder.m_171558_().m_171514_(44, 50).m_171481_(-4.0F, 0.0F, -4.0F, 8.0F, 28.0F, 8.0F), PartPose.m_171419_(-13.0F, -13.0F, 1.0F)
      );
      body.m_171599_(
         "left_arm", CubeListBuilder.m_171558_().m_171514_(0, 58).m_171481_(-4.0F, 0.0F, -4.0F, 8.0F, 28.0F, 8.0F), PartPose.m_171419_(13.0F, -13.0F, 1.0F)
      );
      bone.m_171599_(
         "right_leg", CubeListBuilder.m_171558_().m_171514_(76, 48).m_171481_(-3.1F, 0.0F, -3.0F, 6.0F, 13.0F, 6.0F), PartPose.m_171419_(-5.9F, -13.0F, 0.0F)
      );
      bone.m_171599_(
         "left_leg", CubeListBuilder.m_171558_().m_171514_(76, 76).m_171481_(-2.9F, 0.0F, -3.0F, 6.0F, 13.0F, 6.0F), PartPose.m_171419_(5.9F, -13.0F, 0.0F)
      );
      left_ribcage.m_171599_(
         "left_ribs_extra",
         CubeListBuilder.m_171558_()
            .m_171514_(11, 71)
            .m_171488_(-7.0F, -15.0F, -5.1F, 7.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .m_171514_(11, 71)
            .m_171488_(-7.0F, -19.0F, -5.1F, 7.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .m_171514_(11, 71)
            .m_171488_(-7.0F, -23.0F, -5.1F, 7.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .m_171514_(11, 71)
            .m_171488_(-7.0F, -27.0F, -5.1F, 7.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.m_171423_(0.0F, 21.0F, 3.0F, 0.0F, -0.3491F, 0.0F)
      );
      right_ribcage.m_171599_(
         "right_ribs_extra",
         CubeListBuilder.m_171558_()
            .m_171514_(11, 71)
            .m_171488_(-7.0F, -15.0F, -5.1F, 7.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .m_171514_(11, 71)
            .m_171488_(-7.0F, -19.0F, -5.1F, 7.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .m_171514_(11, 71)
            .m_171488_(-7.0F, -23.0F, -5.1F, 7.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .m_171514_(11, 71)
            .m_171488_(-7.0F, -27.0F, -5.1F, 7.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.m_171423_(7.0F, 21.0F, 1.0F, 0.0F, 0.3491F, 0.0F)
      );
      return LayerDefinition.m_171565_(mesh, 128, 128);
   }

   public void setupAnim(HerobrineWardenEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
      super.m_6973_(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
      this.m_233381_(pEntity.idleAnimationState, HerobrineWardenAnimations.HEROBRINE_WARDEN_IDLE, pAgeInTicks);
      this.m_233381_(pEntity.eatingAnimationState, HerobrineWardenAnimations.HEROBRINE_WARDEN_EATING, pAgeInTicks);
   }
}
