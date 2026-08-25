package com.pla.annoyingvillagers.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pla.annoyingvillagers.accessors.ModelPartAccess;
import com.pla.annoyingvillagers.client.animation.DragonAnimator;
import com.pla.annoyingvillagers.client.engine.ModelPartProxy;
import com.pla.annoyingvillagers.entity.HerobrineDragonEntity;
import java.util.NoSuchElementException;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class ModelHerobrineDragon extends EntityModel<HerobrineDragonEntity> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "modelherobrinedragon"), "main"
   );
   public static final int NECK_SIZE = 10;
   public static final int TAIL_SIZE = 10;
   public static final int VERTS_NECK = 7;
   public static final int VERTS_TAIL = 12;
   public static final int HEAD_OFS = -16;
   public final ModelPart head;
   public final ModelPart neck;
   public final ModelPart neckScale;
   public final ModelPart tail;
   public final ModelPart tailHornLeft;
   public final ModelPart tailHornRight;
   public final ModelPart jaw;
   public final ModelPart body;
   public final ModelPart back;
   public final ModelPart[][] legs = new ModelPart[4][4];
   public final ModelPart[] wingArms;
   public final ModelPart[] wingForearms;
   public final ModelPart[][] wingFingers = new ModelPart[2][4];
   public final ModelPartProxy[] neckProxy = new ModelPartProxy[7];
   public final ModelPartProxy[] tailProxy = new ModelPartProxy[12];
   public float size;

   public ModelHerobrineDragon(ModelPart root) {
      super(RenderType::m_110452_);
      this.body = root.m_171324_("body");
      this.back = this.body.m_171324_("back");
      this.neck = root.m_171324_("neck");
      this.neckScale = this.neck.m_171324_("neck_scale");
      this.head = root.m_171324_("head");
      this.jaw = this.head.m_171324_("jaw");
      this.tail = root.m_171324_("tail");
      this.tailHornRight = getNullableChild(this.tail, "right_tail_spike");
      this.tailHornLeft = getNullableChild(this.tail, "left_tail_spike");
      ModelPart rightWingArm = root.m_171324_("right_wing_arm");
      ModelPart leftWingArm = root.m_171324_("left_wing_arm");
      ModelPart rightWingForearm = rightWingArm.m_171324_("right_wing_forearm");
      ModelPart leftWingForearm = leftWingArm.m_171324_("left_wing_forearm");
      this.wingArms = new ModelPart[]{rightWingArm, leftWingArm};
      this.wingForearms = new ModelPart[]{rightWingForearm, leftWingForearm};

      for (int i = 1; i < 5; i++) {
         this.wingFingers[0][i - 1] = rightWingForearm.m_171324_("right_wing_finger_" + i);
         this.wingFingers[1][i - 1] = leftWingForearm.m_171324_("left_wing_finger_" + i);
      }

      for (int i = 0; i < this.legs.length; i++) {
         boolean right = i < 2;
         String dirName = right ? "right_" : "left_";
         String type = i % 2 == 0 ? "fore_" : "hind_";
         String[] parts = new String[]{"thigh", "crus", "foot", "toe"};
         ModelPart parent = root;

         for (int j = 0; j < parts.length; j++) {
            parent = this.legs[i][j] = parent.m_171324_(dirName + type + parts[j]);
         }
      }

      for (int i = 0; i < this.neckProxy.length; i++) {
         this.neckProxy[i] = new ModelPartProxy(this.neck);
      }

      for (int i = 0; i < this.tailProxy.length; i++) {
         this.tailProxy[i] = new ModelPartProxy(this.tail);
      }

      if (this.tailHornRight != null) {
         this.tailHornRight.f_104207_ = this.tailHornLeft.f_104207_ = false;
      }
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.m_171576_();
      buildBody(root);
      buildNeck(root);
      buildHead(root);
      buildTail(root, ModelHerobrineDragon.Properties.STANDARD);
      buildWings(root);
      buildLegs(root, ModelHerobrineDragon.Properties.STANDARD);
      return LayerDefinition.m_171565_(mesh, 256, 256);
   }

   private static void buildBody(PartDefinition root) {
      PartDefinition body = root.m_171599_(
         "body",
         CubeListBuilder.m_171558_()
            .m_171514_(0, 0)
            .m_171481_(-12.0F, 0.0F, -16.0F, 24.0F, 24.0F, 64.0F)
            .m_171514_(0, 32)
            .m_171481_(-1.0F, -6.0F, 10.0F, 2.0F, 6.0F, 12.0F)
            .m_171481_(-1.0F, -6.0F, 30.0F, 2.0F, 6.0F, 12.0F),
         PartPose.m_171419_(0.0F, 4.0F, 8.0F)
      );
      body.m_171599_("back", CubeListBuilder.m_171558_().m_171514_(0, 32).m_171481_(-1.0F, -6.0F, -10.0F, 2.0F, 6.0F, 12.0F), PartPose.f_171404_);
   }

   private static void buildNeck(PartDefinition root) {
      PartDefinition neck = root.m_171599_(
         "neck", CubeListBuilder.m_171558_().m_171514_(112, 88).m_171481_(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F), PartPose.f_171404_
      );
      neck.m_171599_("neck_scale", CubeListBuilder.m_171558_().m_171514_(0, 0).m_171481_(-1.0F, -7.0F, -3.0F, 2.0F, 4.0F, 6.0F), PartPose.f_171404_);
   }

   private static void buildHead(PartDefinition root) {
      PartDefinition head = root.m_171599_(
         "head",
         CubeListBuilder.m_171558_()
            .m_171514_(56, 88)
            .m_171481_(-6.0F, -1.0F, -24.0F, 12.0F, 5.0F, 16.0F)
            .m_171514_(0, 0)
            .m_171481_(-8.0F, -8.0F, -10.0F, 16.0F, 16.0F, 16.0F)
            .m_171514_(48, 0)
            .m_171481_(-5.0F, -3.0F, -22.0F, 2.0F, 2.0F, 4.0F)
            .m_171480_()
            .m_171481_(3.0F, -3.0F, -22.0F, 2.0F, 2.0F, 4.0F),
         PartPose.f_171404_
      );
      addHorns(head);
      head.m_171599_(
         "jaw", CubeListBuilder.m_171558_().m_171514_(0, 88).m_171481_(-6.0F, 0.0F, -16.0F, 12.0F, 4.0F, 16.0F), PartPose.m_171419_(0.0F, 4.0F, -8.0F)
      );
   }

   private static void addHorns(PartDefinition head) {
      int hornThick = 3;
      int hornLength = 12;
      float hornOfs = -((float)hornThick / 2.0F);
      float hornPosX = -5.0F;
      float hornPosY = -8.0F;
      float hornPosZ = 0.0F;
      float hornRotX = 0.523599F;
      float hornRotY = -0.523599F;
      float hornRotZ = 0.0F;
      head.m_171599_(
         "horn1",
         CubeListBuilder.m_171558_().m_171514_(28, 32).m_171481_(hornOfs, hornOfs, hornOfs, (float)hornThick, (float)hornThick, (float)hornLength),
         PartPose.m_171423_(hornPosX, hornPosY, hornPosZ, hornRotX, hornRotY, hornRotZ)
      );
      head.m_171599_(
         "horn2",
         CubeListBuilder.m_171558_().m_171514_(28, 32).m_171480_().m_171481_(hornOfs, hornOfs, hornOfs, (float)hornThick, (float)hornThick, (float)hornLength),
         PartPose.m_171423_(hornPosX * -1.0F, hornPosY, hornPosZ, hornRotX, hornRotY * -1.0F, hornRotZ)
      );
   }

   private static void buildTail(PartDefinition root, ModelHerobrineDragon.Properties properties) {
      PartDefinition tail = root.m_171599_(
         "tail", CubeListBuilder.m_171558_().m_171514_(152, 88).m_171481_(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F), PartPose.f_171404_
      );
      CubeListBuilder tailSpikeCube = CubeListBuilder.m_171558_().m_171514_(0, 0).m_171481_(-1.0F, -8.0F, -3.0F, 2.0F, 4.0F, 6.0F);
      if (properties.middleTailScales()) {
         tail.m_171599_("middle_tail_scale", tailSpikeCube, PartPose.f_171404_);
      } else {
         tail.m_171599_("left_tail_scale", tailSpikeCube, PartPose.m_171430_(0.0F, 0.0F, 0.785398F));
         tail.m_171599_("right_tail_scale", tailSpikeCube, PartPose.m_171430_(0.0F, 0.0F, -0.785398F));
      }

      if (properties.tailHorns()) {
         addTailSpikes(tail);
      }
   }

   private static void addTailSpikes(PartDefinition tail) {
      int hornThick = 3;
      int hornLength = 32;
      float hornOfs = -((float)hornThick / 2.0F);
      float hornPosX = 0.0F;
      float hornPosZ = 5.0F;
      float hornRotX = -0.261799F;
      float hornRotY = -2.53073F;
      float hornRotZ = 0.0F;
      tail.m_171599_(
         "right_tail_spike",
         CubeListBuilder.m_171558_().m_171514_(0, 117).m_171481_(hornOfs, hornOfs, hornOfs, (float)hornThick, (float)hornThick, (float)hornLength),
         PartPose.m_171423_(hornPosX, hornOfs, hornPosZ, hornRotX, hornRotY, hornRotZ)
      );
      tail.m_171599_(
         "left_tail_spike",
         CubeListBuilder.m_171558_().m_171514_(0, 117).m_171480_().m_171481_(hornOfs, hornOfs, hornOfs, (float)hornThick, (float)hornThick, (float)hornLength),
         PartPose.m_171423_(hornPosX * -1.0F, hornOfs, hornPosZ, hornRotX, hornRotY * -1.0F, hornRotZ)
      );
   }

   private static void buildWings(PartDefinition root) {
      buildWing(root, false);
      buildWing(root, true);
   }

   private static void buildWing(PartDefinition root, boolean mirror) {
      String direction = mirror ? "left_" : "right_";
      CubeListBuilder wingArmCube = CubeListBuilder.m_171558_().m_171555_(mirror);
      centerMirroredBox(wingArmCube.m_171514_(0, 152), mirror, -28.0F, -3.0F, -3.0F, 28.0F, 6.0F, 6.0F);
      centerMirroredBox(wingArmCube.m_171514_(116, 232), mirror, -28.0F, 0.0F, 2.0F, 28.0F, 0.0F, 24.0F);
      CubeListBuilder foreArmCube = centerMirroredBox(
         CubeListBuilder.m_171558_().m_171555_(mirror).m_171514_(0, 164), mirror, -48.0F, -2.0F, -2.0F, 48.0F, 4.0F, 4.0F
      );
      CubeListBuilder shortSkinCube = CubeListBuilder.m_171558_().m_171555_(mirror);
      centerMirroredBox(shortSkinCube.m_171514_(0, 172), mirror, -70.0F, -1.0F, -1.0F, 70.0F, 2.0F, 2.0F);
      centerMirroredBox(shortSkinCube.m_171514_(-49, 176), mirror, -70.0F, 0.0F, 1.0F, 70.0F, 0.0F, 48.0F);
      PartPose shortSkinPos = mirrorXPos(-47.0F, 0.0F, 0.0F, mirror);
      CubeListBuilder lastFingerCube = CubeListBuilder.m_171558_().m_171555_(mirror);
      centerMirroredBox(lastFingerCube.m_171514_(0, 172), mirror, -70.0F, -1.0F, -1.0F, 70.0F, 2.0F, 2.0F);
      centerMirroredBox(lastFingerCube.m_171514_(-32, 224), mirror, -70.0F, 0.0F, 1.0F, 70.0F, 0.0F, 32.0F);
      PartDefinition arm = root.m_171599_(direction + "wing_arm", wingArmCube, mirrorXPos(-10.0F, 5.0F, 4.0F, mirror));
      PartDefinition foreArm = arm.m_171599_(direction + "wing_forearm", foreArmCube, mirrorXPos(-28.0F, 0.0F, 0.0F, mirror));

      for (int j = 1; j < 4; j++) {
         foreArm.m_171599_(direction + "wing_finger_" + j, shortSkinCube, shortSkinPos);
      }

      foreArm.m_171599_(direction + "wing_finger_4", lastFingerCube, shortSkinPos);
   }

   private static void buildLegs(PartDefinition root, ModelHerobrineDragon.Properties properties) {
      buildLeg(root, false, properties.thinLegs(), false);
      buildLeg(root, true, properties.thinLegs(), false);
      buildLeg(root, false, properties.thinLegs(), true);
      buildLeg(root, true, properties.thinLegs(), true);
   }

   private static void buildLeg(PartDefinition root, boolean hind, boolean thin, boolean mirror) {
      float baseLength = 26.0F;
      String baseName = (mirror ? "left_" : "right_") + (hind ? "hind_" : "fore_");
      float thighPosX = -11.0F;
      float thighPosY = 18.0F;
      float thighPosZ = 4.0F;
      int thighThick = 9 - (thin ? 2 : 0);
      int thighLength = (int)(baseLength * (hind ? 0.9F : 0.77F));
      if (hind) {
         thighThick++;
         thighPosY -= 5.0F;
      }

      float thighOfs = -((float)thighThick / 2.0F);
      PartDefinition thigh = root.m_171599_(
         baseName + "thigh",
         CubeListBuilder.m_171558_()
            .m_171514_(112, hind ? 29 : 0)
            .m_171481_(thighOfs, thighOfs, thighOfs, (float)thighThick, (float)thighLength, (float)thighThick),
         mirrorXPos(thighPosX, thighPosY, thighPosZ, mirror)
      );
      float crusPosX = 0.0F;
      float crusPosY = (float)thighLength + thighOfs;
      float crusPosZ = 0.0F;
      int crusThick = thighThick - 2;
      int crusLength = (int)(baseLength * (hind ? 0.7F : 0.8F));
      if (hind) {
         crusThick--;
         crusLength -= 2;
      }

      float crusOfs = -((float)crusThick / 2.0F);
      PartDefinition crus = thigh.m_171599_(
         baseName + "crus",
         CubeListBuilder.m_171558_()
            .m_171514_(hind ? 152 : 148, hind ? 29 : 0)
            .m_171481_(crusOfs, crusOfs, crusOfs, (float)crusThick, (float)crusLength, (float)crusThick),
         mirrorXPos(crusPosX, crusPosY, crusPosZ, mirror)
      );
      float footPosX = 0.0F;
      float footPosY = (float)crusLength + crusOfs / 2.0F;
      float footPosZ = 0.0F;
      int footWidth = crusThick + 2 + (thin ? 2 : 0);
      int footHeight = 4;
      int footLength = (int)(baseLength * (hind ? 0.67F : 0.34F));
      float footOfsX = -((float)footWidth / 2.0F);
      float footOfsY = -((float)footHeight / 2.0F);
      float footOfsZ = (float)footLength * -0.75F;
      PartDefinition foot = crus.m_171599_(
         baseName + "foot",
         CubeListBuilder.m_171558_()
            .m_171514_(hind ? 180 : 210, hind ? 29 : 0)
            .m_171481_(footOfsX, footOfsY, footOfsZ, (float)footWidth, (float)footHeight, (float)footLength),
         mirrorXPos(footPosX, footPosY, footPosZ, mirror)
      );
      int toeLength = (int)(baseLength * (hind ? 0.27F : 0.33F));
      float toePosX = 0.0F;
      float toePosY = 0.0F;
      float toePosZ = footOfsZ - footOfsY / 2.0F;
      float toeOfsX = -((float)footWidth / 2.0F);
      float toeOfsY = -((float)footHeight / 2.0F);
      float toeOfsZ = (float)(-toeLength);
      foot.m_171599_(
         baseName + "toe",
         CubeListBuilder.m_171558_()
            .m_171514_(hind ? 215 : 176, hind ? 29 : 0)
            .m_171481_(toeOfsX, toeOfsY, toeOfsZ, (float)footWidth, (float)footHeight, (float)toeLength),
         mirrorXPos(toePosX, toePosY, toePosZ, mirror)
      );
   }

   public void prepareMobModel(HerobrineDragonEntity dragon, float pLimbSwing, float pLimbSwingAmount, float pPartialTick) {
      this.size = Math.min(dragon.m_6134_(), 1.0F);
      dragon.getAnimator().setPartialTicks(pPartialTick);
   }

   public void setupAnim(HerobrineDragonEntity dragon, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
      DragonAnimator animator = dragon.getAnimator();
      animator.setLook(pNetHeadYaw, pHeadPitch);
      animator.setMovement(pLimbSwing, pLimbSwingAmount * dragon.m_6134_());
      dragon.getAnimator().animate(this);
   }

   public void m_7695_(PoseStack ps, VertexConsumer vertices, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
      this.body.m_104306_(ps, vertices, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
      this.renderHead(ps, vertices, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

      for (ModelPartProxy proxy : this.neckProxy) {
         proxy.render(ps, vertices, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
      }

      for (ModelPartProxy proxy : this.tailProxy) {
         proxy.render(ps, vertices, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
      }

      this.renderWings(ps, vertices, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
      this.renderLegs(ps, vertices, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
   }

   protected void renderHead(PoseStack ps, VertexConsumer vertices, int packedLight, int packedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
      float headScale = 1.4F / (this.size + 0.4F);
      ((ModelPartAccess)this.head).setRenderScale(headScale, headScale, headScale);
      this.head.m_104306_(ps, vertices, packedLight, packedOverlay, pRed, pGreen, pBlue, pAlpha);
   }

   public void renderWings(PoseStack ps, VertexConsumer vertices, int packedLight, int packedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
      ps.m_85836_();
      ps.m_85841_(1.1F, 1.1F, 1.1F);
      this.wingArms[0].m_104306_(ps, vertices, packedLight, packedOverlay, pRed, pGreen, pBlue, pAlpha);
      this.wingArms[1].m_104306_(ps, vertices, packedLight, packedOverlay, pRed, pGreen, pBlue, pAlpha);
      ps.m_85849_();
   }

   protected void renderLegs(PoseStack ps, VertexConsumer vertices, int packedLight, int packedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
      for (ModelPart[] leg : this.legs) {
         leg[0].m_104306_(ps, vertices, packedLight, packedOverlay, pRed, pGreen, pBlue, pAlpha);
      }
   }

   private static CubeListBuilder centerMirroredBox(
      CubeListBuilder builder, boolean mirror, float pOriginX, float pOriginY, float pOriginZ, float pDimensionX, float pDimensionY, float pDimensionZ
   ) {
      if (mirror) {
         pOriginX = 0.0F;
      }

      return builder.m_171481_(pOriginX, pOriginY, pOriginZ, pDimensionX, pDimensionY, pDimensionZ);
   }

   private static PartPose mirrorXPos(float x, float y, float z, boolean mirror) {
      if (mirror) {
         x = -x;
      }

      return PartPose.m_171419_(x, y, z);
   }

   @Nullable
   private static ModelPart getNullableChild(ModelPart parent, String child) {
      try {
         return parent.m_171324_(child);
      } catch (NoSuchElementException var3) {
         return null;
      }
   }

   public static record Properties(boolean middleTailScales, boolean tailHorns, boolean thinLegs) {
      public static final ModelHerobrineDragon.Properties STANDARD = new ModelHerobrineDragon.Properties(true, false, false);
      public static final Codec<ModelHerobrineDragon.Properties> CODEC = RecordCodecBuilder.create(
         func -> func.group(
                  Codec.BOOL.optionalFieldOf("middle_tail_scales", true).forGetter(ModelHerobrineDragon.Properties::middleTailScales),
                  Codec.BOOL.optionalFieldOf("tail_horns", false).forGetter(ModelHerobrineDragon.Properties::tailHorns),
                  Codec.BOOL.optionalFieldOf("thin_legs", false).forGetter(ModelHerobrineDragon.Properties::thinLegs)
               )
               .apply(func, ModelHerobrineDragon.Properties::new)
      );
   }
}
