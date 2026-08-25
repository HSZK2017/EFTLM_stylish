package com.dmc.invincible_dmc.client.particles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Mesh.DrawingFunction;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.EntitySnapshot;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.particle.CustomModelParticle;
import yesman.epicfight.client.particle.EpicFightParticleRenderTypes;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class DynamicEntityAfterImgParticle extends CustomModelParticle<SkinnedMesh> {
   private static final Quaternionf IDENTITY_QUATERNION = new Quaternionf(0.0F, 0.0F, 0.0F, 1.0F);
   protected final EntitySnapshot<?> entitySnapshot;
   protected final OpenMatrix4f[] poseMatrices;
   protected final Matrix4f modelMatrix;
   protected final Armature armature;
   protected final ResourceLocation texture;
   protected float alphaO;

   public DynamicEntityAfterImgParticle(
      ClientLevel level,
      double x,
      double y,
      double z,
      double xd,
      double yd,
      double zd,
      int lifetime,
      AssetAccessor<SkinnedMesh> particleMesh,
      OpenMatrix4f[] matrices,
      Matrix4f modelMatrix,
      EntitySnapshot<?> entitySnapshot,
      Armature armature,
      ResourceLocation texture
   ) {
      super(level, x, y, z, xd, yd, zd, particleMesh);
      this.poseMatrices = matrices;
      this.modelMatrix = modelMatrix;
      this.f_107225_ = lifetime;
      this.entitySnapshot = entitySnapshot;
      this.armature = armature;
      this.texture = texture;
      this.f_107219_ = false;
      this.f_107227_ = 1.0F;
      this.f_107228_ = 1.0F;
      this.f_107229_ = 1.0F;
      this.f_107230_ = 0.5F;
      this.alphaO = 0.5F;
      this.yaw = (float)((double)Vec3f.getAngleBetween(new Vec3f((float)xd, 0.0F, (float)(-zd)), Vec3f.X_AXIS) / Math.PI * 180.0);
      if (zd > 0.0) {
         this.yaw = -this.yaw;
      }

      this.yaw += 90.0F;
      this.yawO = this.yaw;
      this.pitch = -(
         (float)((double)Vec3f.getAngleBetween(new Vec3f((float)xd, 0.0F, (float)(-zd)), new Vec3f((float)xd, (float)yd, (float)(-zd))) / Math.PI * 180.0)
      );
      this.pitch += 10.0F;
      this.pitchO = this.pitch;
      this.f_107215_ = xd;
      this.f_107216_ = yd;
      this.f_107217_ = zd;
   }

   @NotNull
   public static DynamicEntityAfterImgParticle create(
      LivingEntityPatch<?> entitypatch,
      AssetAccessor<? extends StaticAnimation> animation,
      double x,
      double y,
      double z,
      double xd,
      double yd,
      double zd,
      int lifeTime,
      float animTime
   ) {
      LivingEntityRenderer vanillaRenderer = (LivingEntityRenderer)Minecraft.m_91087_().m_91290_().m_114382_((LivingEntity)entitypatch.getOriginal());
      ResourceLocation texture = vanillaRenderer.m_5478_(entitypatch.getOriginal());
      return create(entitypatch, animation, x, y, z, xd, yd, zd, lifeTime, animTime, texture, 1.0F, 1.0F, 1.0F, 0.5F);
   }

   @NotNull
   public static DynamicEntityAfterImgParticle create(
      LivingEntityPatch<?> entitypatch,
      AssetAccessor<? extends StaticAnimation> animation,
      double x,
      double y,
      double z,
      double xd,
      double yd,
      double zd,
      int lifeTime,
      float animTime,
      ResourceLocation texture,
      float red,
      float green,
      float blue,
      float alpha
   ) {
      PatchedEntityRenderer renderer = ClientEngine.getInstance().renderEngine.getEntityRenderer(entitypatch.getOriginal());
      Armature armature = entitypatch.getArmature();
      Pose pose = ((StaticAnimation)animation.get()).getPoseByTime(entitypatch, animTime, 0.0F);
      renderer.setJointTransforms(entitypatch, armature, pose, 1.0F);
      OpenMatrix4f[] matrices = armature.getPoseAsTransformMatrix(pose, true);
      AssetAccessor mesh = renderer.getMeshProvider(entitypatch);
      EntitySnapshot<?> snapshot = new EntitySnapshot(entitypatch);
      Matrix4f modelMat = OpenMatrix4f.exportToMojangMatrix(snapshot.getModelMatrix());
      DynamicEntityAfterImgParticle particle = new DynamicEntityAfterImgParticle(
         (ClientLevel)((LivingEntity)entitypatch.getOriginal()).m_9236_(), x, y, z, xd, yd, zd, lifeTime, mesh, matrices, modelMat, snapshot, armature, texture
      );
      particle.f_107227_ = red;
      particle.f_107228_ = green;
      particle.f_107229_ = blue;
      particle.f_107230_ = alpha;
      particle.alphaO = alpha;
      return particle;
   }

   public void m_5989_() {
      this.f_107209_ = this.f_107212_;
      this.f_107210_ = this.f_107213_;
      this.f_107211_ = this.f_107214_;
      if (this.f_107224_++ >= this.f_107225_) {
         this.m_107274_();
      } else {
         this.pitchO = this.pitch;
         this.yawO = this.yaw;
         this.f_107204_ = this.f_107231_;
         this.scaleO = this.scale;
         this.alphaO = this.f_107230_;
         this.f_107212_ = this.f_107212_ + this.f_107215_;
         this.f_107213_ = this.f_107213_ + this.f_107216_;
         this.f_107214_ = this.f_107214_ + this.f_107217_;
      }
   }

   public void m_5744_(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
      float alpha = Mth.m_14179_(partialTicks, this.alphaO, this.f_107230_);
      int lightColor = this.m_6355_(partialTicks);
      PoseStack poseStack = new PoseStack();
      this.setupPoseStack(poseStack, camera, partialTicks);
      BufferSource buffers = Minecraft.m_91087_().m_91269_().m_110104_();
      SkinnedMesh mesh = (SkinnedMesh)this.particleMeshProvider.get();
      mesh.initialize();
      mesh.draw(
         poseStack,
         buffers,
         EpicFightRenderTypes.entityAfterimageStencil(this.texture),
         DrawingFunction.POSITION_TEX,
         0,
         0.0F,
         0.0F,
         0.0F,
         1.0F,
         OverlayTexture.f_118083_,
         this.armature,
         this.poseMatrices
      );
      buffers.m_173043_();
      mesh.draw(
         poseStack,
         buffers,
         EpicFightRenderTypes.entityAfterimageTranslucent(this.texture),
         DrawingFunction.NEW_ENTITY,
         lightColor,
         this.f_107227_,
         this.f_107228_,
         this.f_107229_,
         alpha,
         OverlayTexture.f_118083_,
         this.armature,
         this.poseMatrices
      );
      buffers.m_173043_();
      this.revert(poseStack);
   }

   protected void setupPoseStack(PoseStack poseStack, Camera camera, float partialTicks) {
      poseStack.m_85836_();
      poseStack.m_252931_(RenderSystem.getModelViewStack().m_85850_().m_252922_());
      RenderSystem.getModelViewStack().m_85836_();
      RenderSystem.getModelViewStack().m_166856_();
      RenderSystem.applyModelViewMatrix();
      Vec3 cameraPosition = camera.m_90583_();
      float x = (float)(Mth.m_14139_((double)partialTicks, this.f_107209_, this.f_107212_) - cameraPosition.m_7096_());
      float y = (float)(Mth.m_14139_((double)partialTicks, this.f_107210_, this.f_107213_) - cameraPosition.m_7098_());
      float z = (float)(Mth.m_14139_((double)partialTicks, this.f_107211_, this.f_107214_) - cameraPosition.m_7094_());
      poseStack.m_252880_(x, y, z);
      Quaternionf rotation = new Quaternionf(IDENTITY_QUATERNION);
      float roll = Mth.m_14189_(partialTicks, this.f_107204_, this.f_107231_);
      float pitch = Mth.m_14189_(partialTicks, this.pitchO, this.pitch);
      float yaw = Mth.m_14189_(partialTicks, this.yawO, this.yaw);
      rotation.mul(QuaternionUtils.YP.rotationDegrees(180.0F - yaw));
      rotation.mul(QuaternionUtils.XP.rotationDegrees(pitch));
      rotation.mul(QuaternionUtils.ZP.rotationDegrees(roll));
      poseStack.m_252781_(rotation);
      poseStack.m_252931_(this.modelMatrix);
      float scale = Mth.m_14179_(partialTicks, this.scaleO, this.scale);
      poseStack.m_252880_(0.0F, this.entitySnapshot.getHeightHalf(), 0.0F);
      poseStack.m_85841_(scale, scale, scale);
      poseStack.m_252880_(0.0F, -this.entitySnapshot.getHeightHalf(), 0.0F);
   }

   protected void revert(PoseStack poseStack) {
      poseStack.m_85849_();
      RenderSystem.getModelViewStack().m_85849_();
      RenderSystem.applyModelViewMatrix();
   }

   @NotNull
   public ParticleRenderType m_7556_() {
      return EpicFightParticleRenderTypes.ENTITY_PARTICLE;
   }

   public boolean shouldCull() {
      return false;
   }
}
