package com.dmc.invincible_dmc.api.collider;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;

public class ScalableOBBCollider extends OBBCollider {
   private Vec3 scaleMultiplier = new Vec3(1.0, 1.0, 1.0);
   private Vec3 centerOffset = Vec3.f_82478_;
   private final Vec3 baseHalfExtents;

   public ScalableOBBCollider(double vertexX, double vertexY, double vertexZ, double centerX, double centerY, double centerZ) {
      super(vertexX, vertexY, vertexZ, centerX, centerY, centerZ);
      this.baseHalfExtents = new Vec3(vertexX, vertexY, vertexZ);
   }

   public void setScale(double x, double y, double z) {
      this.scaleMultiplier = new Vec3(x, y, z);
   }

   public Vec3 getScaleMultiplier() {
      return this.scaleMultiplier;
   }

   public void resetScale() {
      this.scaleMultiplier = new Vec3(1.0, 1.0, 1.0);
   }

   public void setCenterOffset(double x, double y, double z) {
      this.centerOffset = new Vec3(x, y, z);
   }

   public Vec3 getCenterOffset() {
      return this.centerOffset;
   }

   public void resetCenterOffset() {
      this.centerOffset = Vec3.f_82478_;
   }

   public void transform(OpenMatrix4f modelMatrix) {
      super.transform(modelMatrix);
      double sx = this.scaleMultiplier.f_82479_;
      double sy = this.scaleMultiplier.f_82480_;
      double sz = this.scaleMultiplier.f_82481_;
      if (sx != 1.0 || sy != 1.0 || sz != 1.0) {
         for (int i = 0; i < this.rotatedVertices.length; i++) {
            Vec3 v = this.rotatedVertices[i];
            this.rotatedVertices[i] = new Vec3(v.f_82479_ * sx, v.f_82480_ * sy, v.f_82481_ * sz);
         }

         this.scale = new Vec3f(this.scale.x * (float)sx, this.scale.y * (float)sy, this.scale.z * (float)sz);
      }

      double ox = this.centerOffset.f_82479_;
      double oy = this.centerOffset.f_82480_;
      double oz = this.centerOffset.f_82481_;
      if (ox != 0.0 || oy != 0.0 || oz != 0.0) {
         this.worldCenter = new Vec3(this.worldCenter.f_82479_ + ox, this.worldCenter.f_82480_ + oy, this.worldCenter.f_82481_ + oz);
      }
   }

   public OBBCollider deepCopy() {
      ScalableOBBCollider copy = new ScalableOBBCollider(
         this.baseHalfExtents.f_82479_,
         this.baseHalfExtents.f_82480_,
         this.baseHalfExtents.f_82481_,
         this.modelCenter.f_82479_,
         this.modelCenter.f_82480_,
         this.modelCenter.f_82481_
      );
      copy.setScale(this.scaleMultiplier.f_82479_, this.scaleMultiplier.f_82480_, this.scaleMultiplier.f_82481_);
      copy.setCenterOffset(this.centerOffset.f_82479_, this.centerOffset.f_82480_, this.centerOffset.f_82481_);
      return copy;
   }

   @OnlyIn(Dist.CLIENT)
   public void drawInternal(
      PoseStack poseStack, VertexConsumer vertexConsumer, Armature armature, Joint joint, Pose pose1, Pose pose2, float partialTicks, int color
   ) {
      Pose interpolatedPose = Pose.interpolatePose(pose1, pose2, partialTicks);
      OpenMatrix4f poseMatrix;
      if (armature.rootJoint.equals(joint)) {
         JointTransform jt = interpolatedPose.orElseEmpty("Root");
         jt.rotation().x = 0.0F;
         jt.rotation().y = 0.0F;
         jt.rotation().z = 0.0F;
         jt.rotation().w = 1.0F;
         poseMatrix = jt.getAnimationBoundMatrix(armature.rootJoint, new OpenMatrix4f()).removeTranslation();
      } else {
         poseMatrix = armature.getBoundTransformFor(interpolatedPose, joint);
      }

      poseStack.m_85836_();
      MathUtils.mulStack(poseStack, poseMatrix);
      Matrix4f matrix = poseStack.m_85850_().m_252922_();
      Vec3 vec = this.modelVertices[1];
      double sx = this.scaleMultiplier.f_82479_;
      double sy = this.scaleMultiplier.f_82480_;
      double sz = this.scaleMultiplier.f_82481_;
      float cx = (float)(this.modelCenter.f_82479_ + this.centerOffset.f_82479_);
      float cy = (float)(this.modelCenter.f_82480_ + this.centerOffset.f_82480_);
      float cz = (float)(this.modelCenter.f_82481_ + this.centerOffset.f_82481_);
      float maxX = (float)((double)cx + vec.f_82479_ * sx);
      float maxY = (float)((double)cy + vec.f_82480_ * sy);
      float maxZ = (float)((double)cz + vec.f_82481_ * sz);
      float minX = (float)((double)cx - vec.f_82479_ * sx);
      float minY = (float)((double)cy - vec.f_82480_ * sy);
      float minZ = (float)((double)cz - vec.f_82481_ * sz);
      vertexConsumer.m_252986_(matrix, minX, maxY, minZ).m_193479_(color).m_5601_(0.0F, 0.0F, 1.0F).m_5752_();
      vertexConsumer.m_252986_(matrix, minX, maxY, maxZ).m_193479_(color).m_5601_(0.0F, 0.0F, 1.0F).m_5752_();
      vertexConsumer.m_252986_(matrix, minX, maxY, maxZ).m_193479_(color).m_5601_(1.0F, 0.0F, 0.0F).m_5752_();
      vertexConsumer.m_252986_(matrix, maxX, maxY, maxZ).m_193479_(color).m_5601_(1.0F, 0.0F, 0.0F).m_5752_();
      vertexConsumer.m_252986_(matrix, maxX, maxY, maxZ).m_193479_(color).m_5601_(0.0F, 0.0F, -1.0F).m_5752_();
      vertexConsumer.m_252986_(matrix, maxX, maxY, minZ).m_193479_(color).m_5601_(0.0F, 0.0F, -1.0F).m_5752_();
      vertexConsumer.m_252986_(matrix, maxX, maxY, minZ).m_193479_(color).m_5601_(-1.0F, 0.0F, 0.0F).m_5752_();
      vertexConsumer.m_252986_(matrix, minX, maxY, minZ).m_193479_(color).m_5601_(-1.0F, 0.0F, 0.0F).m_5752_();
      vertexConsumer.m_252986_(matrix, maxX, maxY, maxZ).m_193479_(color).m_5601_(0.0F, -1.0F, 0.0F).m_5752_();
      vertexConsumer.m_252986_(matrix, maxX, minY, maxZ).m_193479_(color).m_5601_(0.0F, -1.0F, 0.0F).m_5752_();
      vertexConsumer.m_252986_(matrix, minX, maxY, maxZ).m_193479_(color).m_5601_(0.0F, -1.0F, 0.0F).m_5752_();
      vertexConsumer.m_252986_(matrix, minX, minY, maxZ).m_193479_(color).m_5601_(0.0F, -1.0F, 0.0F).m_5752_();
      vertexConsumer.m_252986_(matrix, maxX, maxY, minZ).m_193479_(color).m_5601_(0.0F, -1.0F, 0.0F).m_5752_();
      vertexConsumer.m_252986_(matrix, maxX, minY, minZ).m_193479_(color).m_5601_(0.0F, -1.0F, 0.0F).m_5752_();
      vertexConsumer.m_252986_(matrix, minX, maxY, minZ).m_193479_(color).m_5601_(0.0F, -1.0F, 0.0F).m_5752_();
      vertexConsumer.m_252986_(matrix, minX, minY, minZ).m_193479_(color).m_5601_(0.0F, -1.0F, 0.0F).m_5752_();
      vertexConsumer.m_252986_(matrix, minX, minY, minZ).m_193479_(color).m_5601_(0.0F, 0.0F, 1.0F).m_5752_();
      vertexConsumer.m_252986_(matrix, minX, minY, maxZ).m_193479_(color).m_5601_(0.0F, 0.0F, 1.0F).m_5752_();
      vertexConsumer.m_252986_(matrix, minX, minY, maxZ).m_193479_(color).m_5601_(1.0F, 0.0F, 0.0F).m_5752_();
      vertexConsumer.m_252986_(matrix, maxX, minY, maxZ).m_193479_(color).m_5601_(1.0F, 0.0F, 0.0F).m_5752_();
      vertexConsumer.m_252986_(matrix, maxX, minY, maxZ).m_193479_(color).m_5601_(0.0F, 0.0F, -1.0F).m_5752_();
      vertexConsumer.m_252986_(matrix, maxX, minY, minZ).m_193479_(color).m_5601_(0.0F, 0.0F, -1.0F).m_5752_();
      vertexConsumer.m_252986_(matrix, maxX, minY, minZ).m_193479_(color).m_5601_(-1.0F, 0.0F, 0.0F).m_5752_();
      vertexConsumer.m_252986_(matrix, minX, minY, minZ).m_193479_(color).m_5601_(-1.0F, 0.0F, 0.0F).m_5752_();
      poseStack.m_85849_();
   }
}
