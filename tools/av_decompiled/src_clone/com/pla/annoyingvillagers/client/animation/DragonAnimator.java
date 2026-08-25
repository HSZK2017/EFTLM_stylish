package com.pla.annoyingvillagers.client.animation;

import com.pla.annoyingvillagers.accessors.ModelPartAccess;
import com.pla.annoyingvillagers.client.model.ModelHerobrineDragon;
import com.pla.annoyingvillagers.entity.HerobrineDragonEntity;
import java.util.Arrays;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class DragonAnimator {
   private static final int JAW_OPENING_TIME_FOR_ATTACK = 5;
   private final HerobrineDragonEntity dragon;
   private float partialTicks;
   private float moveTime;
   private float moveSpeed;
   private float lookYaw;
   private float lookPitch;
   private double prevRenderYawOffset;
   private double yawAbs;
   private float animBase;
   private float cycleOfs;
   private float anim;
   private float ground;
   private float flutter;
   private float walk;
   private float sit;
   private float jaw;
   private float speed;
   private final DragonAnimator.LerpedFloat animTimer = new DragonAnimator.LerpedFloat();
   private final DragonAnimator.LerpedFloat groundTimer = new DragonAnimator.LerpedFloat.Clamped(1.0F, 0.0F, 1.0F);
   private final DragonAnimator.LerpedFloat flutterTimer = DragonAnimator.LerpedFloat.unit();
   private final DragonAnimator.LerpedFloat walkTimer = DragonAnimator.LerpedFloat.unit();
   private final DragonAnimator.LerpedFloat sitTimer = DragonAnimator.LerpedFloat.unit();
   private final DragonAnimator.LerpedFloat jawTimer = DragonAnimator.LerpedFloat.unit();
   private final DragonAnimator.LerpedFloat speedTimer = new DragonAnimator.LerpedFloat.Clamped(1.0F, 0.0F, 1.0F);
   private boolean initTrails = false;
   private final DragonAnimator.CircularBuffer yTrail = new DragonAnimator.CircularBuffer(8);
   private final DragonAnimator.CircularBuffer yawTrail = new DragonAnimator.CircularBuffer(16);
   private final DragonAnimator.CircularBuffer pitchTrail = new DragonAnimator.CircularBuffer(16);
   private boolean onGround;
   private boolean openJaw;
   private boolean wingsDown;
   private final float[] wingArm = new float[3];
   private final float[] wingForearm = new float[3];
   private final float[] wingArmFlutter = new float[3];
   private final float[] wingForearmFlutter = new float[3];
   private final float[] wingArmGlide = new float[3];
   private final float[] wingForearmGlide = new float[3];
   private final float[] wingArmGround = new float[3];
   private final float[] wingForearmGround = new float[3];
   private final float[] xGround = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
   private final float[][] xGroundStand = new float[][]{{0.8F, -1.5F, 1.3F, 0.0F}, {-0.3F, 1.5F, -0.2F, 0.0F}};
   private final float[][] xGroundSit = new float[][]{{0.3F, -1.8F, 1.8F, 0.0F}, {-0.8F, 1.8F, -0.9F, 0.0F}};
   private final float[][][] xGroundWalk = new float[][][]{
      {{0.4F, -1.4F, 1.3F, 0.0F}, {0.1F, 1.2F, -0.5F, 0.0F}},
      {{1.2F, -1.6F, 1.3F, 0.0F}, {-0.3F, 2.1F, -0.9F, 0.6F}},
      {{0.9F, -2.1F, 1.8F, 0.6F}, {-0.7F, 1.4F, -0.2F, 0.0F}}
   };
   private final float[] xGroundWalk2 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
   private final float[] yGroundStand = new float[]{-0.25F, 0.25F};
   private final float[] yGroundSit = new float[]{0.1F, 0.35F};
   private final float[] yGroundWalk = new float[]{-0.1F, 0.1F};
   private final float[][] xAirAll = new float[][]{{0.0F, 0.0F, 0.0F, 0.0F}, {0.0F, 0.0F, 0.0F, 0.0F}};
   private final float[] yAirAll = new float[]{-0.1F, 0.1F};
   private static final float[][] CR = new float[][]{
      {-0.5F, 1.5F, -1.5F, 0.5F}, {1.0F, -2.5F, 2.0F, -0.5F}, {-0.5F, 0.0F, 0.5F, 0.0F}, {0.0F, 1.0F, 0.0F, 0.0F}
   };

   public DragonAnimator(HerobrineDragonEntity dragon) {
      this.dragon = dragon;
   }

   public void setPartialTicks(float partialTicks) {
      this.partialTicks = partialTicks;
   }

   public void setMovement(float moveTime, float moveSpeed) {
      this.moveTime = moveTime;
      this.moveSpeed = moveSpeed;
   }

   public void setLook(float lookYaw, float lookPitch) {
      this.lookYaw = Mth.m_14036_(lookYaw, -120.0F, 120.0F);
      this.lookPitch = Mth.m_14036_(lookPitch, -90.0F, 90.0F);
   }

   public void animate(ModelHerobrineDragon model) {
      this.anim = this.animTimer.get(this.partialTicks);
      this.ground = this.groundTimer.get(this.partialTicks);
      this.flutter = this.flutterTimer.get(this.partialTicks);
      this.walk = this.walkTimer.get(this.partialTicks);
      this.sit = this.sitTimer.get(this.partialTicks);
      this.jaw = this.jawTimer.get(this.partialTicks);
      this.speed = this.speedTimer.get(this.partialTicks);
      this.animBase = this.anim * (float) Math.PI * 2.0F;
      this.cycleOfs = Mth.m_14031_(this.animBase - 1.0F) + 1.0F;
      boolean newWingsDown = this.cycleOfs > 1.0F;
      if (newWingsDown && !this.wingsDown && this.flutter != 0.0F) {
         this.dragon.onWingsDown(this.speed);
      }

      this.wingsDown = newWingsDown;
      model.back.f_104207_ = true;
      this.cycleOfs = (this.cycleOfs * this.cycleOfs + this.cycleOfs * 2.0F) * 0.05F;
      this.cycleOfs = this.cycleOfs * Mth.m_144920_(0.5F, 1.0F, this.flutter);
      this.cycleOfs = this.cycleOfs * Mth.m_144920_(1.0F, 0.5F, this.ground);
      this.animHeadAndNeck(model);
      this.animTail(model);
      this.animWings(model);
      this.animLegs(model);
   }

   public void tick() {
      this.setOnGround(!this.dragon.m_29443_());
      if (!this.initTrails) {
         this.yTrail.fill((float)this.dragon.m_20186_());
         this.yawTrail.fill(this.dragon.f_20883_);
         this.pitchTrail.fill(this.getModelPitch());
         this.initTrails = true;
      }

      if (this.dragon.m_21223_() <= 0.0F) {
         this.animTimer.sync();
         this.groundTimer.sync();
         this.flutterTimer.sync();
         this.walkTimer.sync();
         this.sitTimer.sync();
         this.jawTimer.sync();
      } else {
         float speedMax = 0.05F;
         float xD = (float)this.dragon.m_20185_() - (float)this.dragon.f_19854_;
         float yD = (float)this.dragon.m_20186_() - (float)this.dragon.f_19855_;
         float zD = (float)this.dragon.m_20189_() - (float)this.dragon.f_19856_;
         float speedEnt = xD * xD + zD * zD;
         float speedMulti = Mth.m_14036_(speedEnt / speedMax, 0.0F, 1.0F);
         float animAdd = 0.035F;
         if (!this.onGround) {
            animAdd += (1.0F - speedMulti) * animAdd;
         }

         this.animTimer.add(animAdd);
         float groundVal = this.groundTimer.get();
         if (this.onGround) {
            groundVal *= 0.95F;
            groundVal += 0.08F;
         } else {
            groundVal -= 0.1F;
         }

         this.groundTimer.set(groundVal);
         boolean flutterFlag = !this.onGround && (this.dragon.f_19862_ || (double)yD > -0.1 || speedEnt < speedMax);
         this.flutterTimer.add(flutterFlag ? 0.1F : -0.1F);
         boolean walkFlag = (double)this.moveSpeed > 0.1 && !this.dragon.m_21825_();
         float walkVal = 0.1F;
         this.walkTimer.add(walkFlag ? walkVal : -walkVal);
         float sitVal = this.sitTimer.get();
         sitVal += this.dragon.m_21825_() ? 0.1F : -0.1F;
         sitVal *= 0.95F;
         this.sitTimer.set(sitVal);
         boolean speedFlag = speedEnt > speedMax || this.dragon.isNearGround();
         float speedValue = 0.05F;
         this.speedTimer.add(speedFlag ? speedValue : -speedValue);
         double yawDiff = (double)this.dragon.f_20883_ - this.prevRenderYawOffset;
         this.prevRenderYawOffset = (double)this.dragon.f_20883_;
         if (yawDiff < 180.0 && yawDiff > -180.0) {
            this.yawAbs += yawDiff;
         }

         this.yTrail.update((float)this.dragon.m_20186_());
         this.yawTrail.update((float)(-this.yawAbs));
         this.pitchTrail.update(this.getModelPitch());
      }
   }

   protected void animHeadAndNeck(ModelHerobrineDragon model) {
      model.neck.m_104227_(0.0F, 14.0F, -8.0F);
      model.neck.m_171327_(0.0F, 0.0F, 0.0F);
      float health = this.dragon.getHealthFraction();

      for (int i = 0; i < model.neckProxy.length; i++) {
         float vertMulti = (float)(i + 1) / (float)model.neckProxy.length;
         float baseRotX = Mth.m_14089_((float)i * 0.45F + this.animBase) * 0.15F;
         baseRotX *= Mth.m_144920_(0.2F, 1.0F, this.flutter);
         baseRotX *= Mth.m_144920_(1.0F, 0.2F, this.sit);
         float ofsRotX = Mth.m_14031_(vertMulti * (float) Math.PI * 0.9F) * 0.75F;
         model.neck.f_104203_ = baseRotX;
         model.neck.f_104203_ = model.neck.f_104203_ * terpSmoothStep(1.0F, 0.5F, this.walk);
         model.neck.f_104203_ = model.neck.f_104203_ + (1.0F - this.speed) * vertMulti;
         model.neck.f_104203_ = model.neck.f_104203_ - Mth.m_144920_(0.0F, ofsRotX, this.ground * health);
         model.neck.f_104204_ = (float)Math.toRadians((double)this.lookYaw) * vertMulti * this.speed;
         float v = Mth.m_144920_(1.6F, 1.0F, vertMulti);
         ((ModelPartAccess)model.neck).setRenderScale(v, v, 0.6F);
         model.neckScale.f_104207_ = i % 2 != 0 || i == 0;
         model.neckProxy[i].update();
         float neckSize = 10.0F * ((ModelPartAccess)model.neck).getZScale() - 1.4F;
         model.neck.f_104200_ = model.neck.f_104200_ - Mth.m_14031_(model.neck.f_104204_) * Mth.m_14089_(model.neck.f_104203_) * neckSize;
         model.neck.f_104201_ = model.neck.f_104201_ + Mth.m_14031_(model.neck.f_104203_) * neckSize;
         model.neck.f_104202_ = model.neck.f_104202_ - Mth.m_14089_(model.neck.f_104204_) * Mth.m_14089_(model.neck.f_104203_) * neckSize;
      }

      model.head.f_104203_ = (float)Math.toRadians((double)this.lookPitch) + (1.0F - this.speed);
      model.head.f_104204_ = model.neck.f_104204_;
      model.head.f_104205_ = model.neck.f_104205_ * 0.2F;
      model.head.f_104200_ = model.neck.f_104200_;
      model.head.f_104201_ = model.neck.f_104201_;
      model.head.f_104202_ = model.neck.f_104202_;
      model.jaw.f_104203_ = this.jaw * 0.75F;
      model.jaw.f_104203_ = model.jaw.f_104203_ + (1.0F - Mth.m_14031_(this.animBase)) * 0.1F * this.flutter;
   }

   protected void animWings(ModelHerobrineDragon model) {
      float aSpeed = this.sit > 0.0F ? 0.6F : 1.0F;
      float a1 = this.animBase * aSpeed * 0.35F;
      float a2 = this.animBase * aSpeed * 0.5F;
      float a3 = this.animBase * aSpeed * 0.75F;
      if (this.ground < 1.0F) {
         this.wingArmFlutter[0] = 0.125F - Mth.m_14089_(this.animBase) * 0.2F;
         this.wingArmFlutter[1] = 0.25F;
         this.wingArmFlutter[2] = (Mth.m_14031_(this.animBase) + 0.125F) * 0.8F;
         this.wingForearmFlutter[0] = 0.0F;
         this.wingForearmFlutter[1] = -this.wingArmFlutter[1] * 2.0F;
         this.wingForearmFlutter[2] = -(Mth.m_14031_(this.animBase + 2.0F) + 0.5F) * 0.75F;
         this.wingArmGlide[0] = -0.25F - Mth.m_14089_(this.animBase * 2.0F) * Mth.m_14089_(this.animBase * 1.5F) * 0.04F;
         this.wingArmGlide[1] = 0.25F;
         this.wingArmGlide[2] = 0.35F + Mth.m_14031_(this.animBase) * 0.05F;
         this.wingForearmGlide[0] = 0.0F;
         this.wingForearmGlide[1] = -this.wingArmGlide[1] * 2.0F;
         this.wingForearmGlide[2] = -0.25F + (Mth.m_14031_(this.animBase + 2.0F) + 0.5F) * 0.05F;
      }

      if (this.ground > 0.0F) {
         this.wingArmGround[0] = 0.0F;
         this.wingArmGround[1] = 1.4F - Mth.m_14031_(a1) * Mth.m_14031_(a2) * 0.02F;
         this.wingArmGround[2] = 0.8F + Mth.m_14031_(a2) * Mth.m_14031_(a3) * 0.05F;
         this.wingArmGround[1] = this.wingArmGround[1] + Mth.m_14031_(this.moveTime * 0.5F) * 0.02F * this.walk;
         this.wingArmGround[2] = this.wingArmGround[2] + Mth.m_14089_(this.moveTime * 0.5F) * 0.05F * this.walk;
         this.wingForearmGround[0] = 0.0F;
         this.wingForearmGround[1] = -this.wingArmGround[1] * 2.0F;
         this.wingForearmGround[2] = 0.0F;
      }

      slerpArrays(this.wingArmGlide, this.wingArmFlutter, this.wingArm, this.flutter);
      slerpArrays(this.wingForearmGlide, this.wingForearmFlutter, this.wingForearm, this.flutter);
      slerpArrays(this.wingArm, this.wingArmGround, this.wingArm, this.ground);
      slerpArrays(this.wingForearm, this.wingForearmGround, this.wingForearm, this.ground);
      mirrorRotate(model.wingArms[0], model.wingArms[1], this.wingArm[0], this.wingArm[1], this.wingArm[2]);
      mirrorRotate(model.wingForearms[0], model.wingForearms[1], this.wingForearm[0], this.wingForearm[1], this.wingForearm[2]);
      float[] yFold = new float[]{2.7F, 2.8F, 2.9F, 3.0F};
      float[] yUnfold = new float[]{0.1F, 0.9F, 1.7F, 2.5F};
      float rotX = 0.0F;
      float rotYOfs = Mth.m_14031_(a1) * Mth.m_14031_(a2) * 0.03F;
      float rotYMulti = 1.0F;

      for (int i = 0; i < model.wingFingers[0].length; i++) {
         mirrorRotate(
            model.wingFingers[0][i], model.wingFingers[1][i], rotX += 0.005F, terpSmoothStep(yUnfold[i], yFold[i] + rotYOfs * rotYMulti, this.ground), 0.0F
         );
         rotYMulti -= 0.2F;
      }
   }

   protected void animTail(ModelHerobrineDragon model) {
      model.tail.f_104200_ = 0.0F;
      model.tail.f_104201_ = 16.0F;
      model.tail.f_104202_ = 62.0F;
      model.tail.f_104203_ = 0.0F;
      model.tail.f_104204_ = 0.0F;
      model.tail.f_104205_ = 0.0F;
      float rotXStand = 0.0F;
      float rotYStand = 0.0F;
      float rotXSit = 0.0F;
      float rotYSit = 0.0F;
      float rotXAir = 0.0F;
      float rotYAir = 0.0F;

      for (int i = 0; i < model.tailProxy.length; i++) {
         float vertMulti = (float)(i + 1) / (float)model.tailProxy.length;
         float amp = 0.1F + (float)i / ((float)model.tailProxy.length * 2.0F);
         rotXStand = ((float)i - (float)model.tailProxy.length * 0.6F) * -amp * 0.4F;
         rotXStand += (Mth.m_14031_(this.animBase * 0.2F) * Mth.m_14031_(this.animBase * 0.37F) * 0.4F * amp - 0.1F) * (1.0F - this.sit);
         rotXSit = rotXStand * 0.8F;
         rotYStand = (rotYStand + Mth.m_14031_((float)i * 0.45F + this.animBase * 0.5F)) * amp * 0.4F;
         rotYSit = Mth.m_14031_(vertMulti * (float) Math.PI) * (float) Math.PI * 1.2F - 0.5F;
         rotXAir -= Mth.m_14031_((float)i * 0.45F + this.animBase) * 0.04F * Mth.m_144920_(0.3F, 1.0F, this.flutter);
         model.tail.f_104203_ = Mth.m_144920_(rotXStand, rotXSit, this.sit);
         model.tail.f_104204_ = Mth.m_144920_(rotYStand, rotYSit, this.sit);
         model.tail.f_104203_ = Mth.m_144920_(rotXAir, model.tail.f_104203_, this.ground);
         model.tail.f_104204_ = Mth.m_144920_(rotYAir, model.tail.f_104204_, this.ground);
         float angleLimit = 160.0F * vertMulti;
         float yawOfs = Mth.m_14036_(this.yawTrail.get(this.partialTicks, 0, i + 1) * 2.0F, -angleLimit, angleLimit);
         float pitchOfs = Mth.m_14036_(this.pitchTrail.get(this.partialTicks, 0, i + 1) * 2.0F, -angleLimit, angleLimit);
         model.tail.f_104203_ = model.tail.f_104203_ + (float)Math.toRadians((double)pitchOfs);
         model.tail.f_104203_ = model.tail.f_104203_ - (1.0F - this.speed) * vertMulti * 2.0F;
         model.tail.f_104204_ = model.tail.f_104204_ + (float)Math.toRadians((double)(180.0F - yawOfs));
         if (model.tailHornRight != null) {
            boolean atIndex = i > model.tailProxy.length - 7 && i < model.tailProxy.length - 3;
            model.tailHornLeft.f_104207_ = model.tailHornRight.f_104207_ = atIndex;
         }

         float neckScale = Mth.m_144920_(1.5F, 0.3F, vertMulti);
         ((ModelPartAccess)model.tail).setRenderScale(neckScale, neckScale, neckScale);
         model.tailProxy[i].update();
         float tailSize = 10.0F * ((ModelPartAccess)model.tail).getZScale() - 0.7F;
         model.tail.f_104201_ = model.tail.f_104201_ + Mth.m_14031_(model.tail.f_104203_) * tailSize;
         model.tail.f_104202_ = model.tail.f_104202_ - Mth.m_14089_(model.tail.f_104204_) * Mth.m_14089_(model.tail.f_104203_) * tailSize;
         model.tail.f_104200_ = model.tail.f_104200_ - Mth.m_14031_(model.tail.f_104204_) * Mth.m_14089_(model.tail.f_104203_) * tailSize;
      }
   }

   protected void animLegs(ModelHerobrineDragon model) {
      if (this.ground < 1.0F) {
         float footAirOfs = this.cycleOfs * 0.1F;
         float footAirX = 0.75F + this.cycleOfs * 0.1F;
         this.xAirAll[0][0] = 1.3F + footAirOfs;
         this.xAirAll[0][1] = -(0.7F * this.speed + 0.1F + footAirOfs);
         this.xAirAll[0][2] = footAirX;
         this.xAirAll[0][3] = footAirX * 0.5F;
         this.xAirAll[1][0] = footAirOfs + 0.6F;
         this.xAirAll[1][1] = footAirOfs + 0.8F;
         this.xAirAll[1][2] = footAirX;
         this.xAirAll[1][3] = footAirX * 0.5F;
      }

      for (int i = 0; i < model.legs.length; i++) {
         ModelPart thigh = model.legs[i][0];
         ModelPart crus = model.legs[i][1];
         ModelPart foot = model.legs[i][2];
         ModelPart toe = model.legs[i][3];
         thigh.f_104202_ = i % 2 == 0 ? 4.0F : 46.0F;
         float[] xAir = this.xAirAll[i % 2];
         slerpArrays(this.xGroundStand[i % 2], this.xGroundSit[i % 2], this.xGround, this.sit);
         this.xGround[3] = -(this.xGround[0] + this.xGround[1] + this.xGround[2]);
         if (this.walk > 0.0F) {
            splineArrays(this.moveTime * 0.2F, i > 1, this.xGroundWalk2, this.xGroundWalk[0][i % 2], this.xGroundWalk[1][i % 2], this.xGroundWalk[2][i % 2]);
            this.xGroundWalk2[3] = this.xGroundWalk2[3] - (this.xGroundWalk2[0] + this.xGroundWalk2[1] + this.xGroundWalk2[2]);
            slerpArrays(this.xGround, this.xGroundWalk2, this.xGround, this.walk);
         }

         float yAir = this.yAirAll[i % 2];
         float yGround = terpSmoothStep(this.yGroundStand[i % 2], this.yGroundSit[i % 2], this.sit);
         yGround = terpSmoothStep(yGround, this.yGroundWalk[i % 2], this.walk);
         thigh.f_104204_ = terpSmoothStep(yAir, yGround, this.ground);
         thigh.f_104203_ = terpSmoothStep(xAir[0], this.xGround[0], this.ground);
         crus.f_104203_ = terpSmoothStep(xAir[1], this.xGround[1], this.ground);
         foot.f_104203_ = terpSmoothStep(xAir[2], this.xGround[2], this.ground);
         toe.f_104203_ = terpSmoothStep(xAir[3], this.xGround[3], this.ground);
         if (i > 1) {
            thigh.f_104204_ *= -1.0F;
         }
      }
   }

   public float getModelPitch() {
      return this.getModelPitch(this.partialTicks);
   }

   public float getModelPitch(float pt) {
      float pitchMovingMax = 90.0F;
      float pitchMoving = Mth.m_14036_(this.yTrail.get(pt, 5, 0) * 10.0F, -pitchMovingMax, pitchMovingMax);
      float pitchHover = 60.0F;
      return terpSmoothStep(pitchHover, pitchMoving, this.speed);
   }

   public float getModelOffsetX() {
      return 0.0F;
   }

   public float getModelOffsetY() {
      return 1.5F + -this.sit * 0.6F;
   }

   public float getModelOffsetZ() {
      return -1.5F;
   }

   public void setOnGround(boolean onGround) {
      this.onGround = onGround;
   }

   public void setOpenJaw(boolean openJaw) {
      this.openJaw = openJaw;
   }

   private static void mirrorRotate(ModelPart rightLimb, ModelPart leftLimb, float xRot, float yRot, float zRot) {
      rightLimb.f_104203_ = xRot;
      rightLimb.f_104204_ = yRot;
      rightLimb.f_104205_ = zRot;
      leftLimb.f_104203_ = xRot;
      leftLimb.f_104204_ = -yRot;
      leftLimb.f_104205_ = -zRot;
   }

   private static void slerpArrays(float[] a, float[] b, float[] c, float x) {
      if (a.length != b.length || b.length != c.length) {
         throw new IllegalArgumentException();
      } else if (x <= 0.0F) {
         System.arraycopy(a, 0, c, 0, a.length);
      } else if (x >= 1.0F) {
         System.arraycopy(b, 0, c, 0, a.length);
      } else {
         for (int i = 0; i < c.length; i++) {
            c[i] = terpSmoothStep(a[i], b[i], x);
         }
      }
   }

   private static float terpSmoothStep(float a, float b, float x) {
      if (x <= 0.0F) {
         return a;
      } else if (x >= 1.0F) {
         return b;
      } else {
         x = x * x * (3.0F - 2.0F * x);
         return a * (1.0F - x) + b * x;
      }
   }

   private static void splineArrays(float x, boolean shift, float[] result, float[]... nodes) {
      int i1 = (int)x % nodes.length;
      int i2 = (i1 + 1) % nodes.length;
      int i3 = (i1 + 2) % nodes.length;
      float[] a1 = nodes[i1];
      float[] a2 = nodes[i2];
      float[] a3 = nodes[i3];
      float xn = x % (float)nodes.length - (float)i1;
      if (shift) {
         terpCatmullRomSpline(xn, result, a2, a3, a1, a2);
      } else {
         terpCatmullRomSpline(xn, result, a1, a2, a3, a1);
      }
   }

   private static void terpCatmullRomSpline(float x, float[] result, float[]... knots) {
      int nknots = knots.length;
      int nspans = nknots - 3;
      int knot = 0;
      if (nspans < 1) {
         throw new IllegalArgumentException("Spline has too few knots");
      } else {
         x = Mth.m_14036_(x, 0.0F, 0.9999F) * (float)nspans;
         int span = (int)x;
         if (span >= nknots - 3) {
            span = nknots - 3;
         }

         x -= (float)span;
         knot += span;
         int dimension = result.length;

         for (int i = 0; i < dimension; i++) {
            float knot0 = knots[knot][i];
            float knot1 = knots[knot + 1][i];
            float knot2 = knots[knot + 2][i];
            float knot3 = knots[knot + 3][i];
            float c3 = CR[0][0] * knot0 + CR[0][1] * knot1 + CR[0][2] * knot2 + CR[0][3] * knot3;
            float c2 = CR[1][0] * knot0 + CR[1][1] * knot1 + CR[1][2] * knot2 + CR[1][3] * knot3;
            float c1 = CR[2][0] * knot0 + CR[2][1] * knot1 + CR[2][2] * knot2 + CR[2][3] * knot3;
            float c0 = CR[3][0] * knot0 + CR[3][1] * knot1 + CR[3][2] * knot2 + CR[3][3] * knot3;
            result[i] = ((c3 * x + c2) * x + c1) * x + c0;
         }
      }
   }

   public static class CircularBuffer {
      private final float[] buffer;
      private int index = 0;

      public CircularBuffer(int size) {
         this.buffer = new float[size];
      }

      public void fill(float value) {
         Arrays.fill(this.buffer, value);
      }

      public void update(float value) {
         this.index++;
         this.index = this.index % this.buffer.length;
         this.buffer[this.index] = value;
      }

      public float get(float x, int offset) {
         int i = this.index - offset;
         int len = this.buffer.length - 1;
         return Mth.m_144920_(this.buffer[i - 1 & len], this.buffer[i & len], x);
      }

      public float get(float x, int offset1, int offset2) {
         return this.get(x, offset2) - this.get(x, offset1);
      }
   }

   public static class LerpedFloat {
      protected float current;
      protected float previous;

      public LerpedFloat() {
         this.current = this.previous = 0.0F;
      }

      public LerpedFloat(float start) {
         this.current = this.previous = start;
      }

      public float get(float x) {
         return Mth.m_144920_(this.previous, this.current, x);
      }

      public float get() {
         return this.current;
      }

      public void set(float value) {
         this.sync();
         this.current = value;
      }

      public void add(float value) {
         this.sync();
         this.current += value;
      }

      public void sync() {
         this.previous = this.current;
      }

      public float getPrevious() {
         return this.previous;
      }

      public static DragonAnimator.LerpedFloat.Clamped unit() {
         return new DragonAnimator.LerpedFloat.Clamped(0.0F, 1.0F);
      }

      public static class Clamped extends DragonAnimator.LerpedFloat {
         private final float min;
         private final float max;

         public Clamped(float start, float min, float max) {
            super(Mth.m_14036_(start, min, max));
            this.min = min;
            this.max = max;
         }

         public Clamped(float min, float max) {
            this(0.0F, min, max);
         }

         @Override
         public void set(float value) {
            super.set(Mth.m_14036_(value, this.min, this.max));
         }

         @Override
         public void add(float value) {
            super.add(value);
            this.current = Mth.m_14036_(this.current, this.min, this.max);
         }

         public float getMin() {
            return this.min;
         }

         public float getMax() {
            return this.max;
         }
      }
   }
}
