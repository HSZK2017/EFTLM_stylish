package com.dmc.invincible_dmc.client.particles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Mesh.DrawingFunction;
import yesman.epicfight.api.utils.EntitySnapshot;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.particle.CustomModelParticle;
import yesman.epicfight.client.particle.EpicFightParticleRenderTypes;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class TransparentEntityAfterImageParticle extends CustomModelParticle<SkinnedMesh> {
   protected final EntitySnapshot<?> entitySnapshot;
   protected float alphaO;

   public TransparentEntityAfterImageParticle(
      ClientLevel level, double x, double y, double z, double xd, double yd, double zd, EntitySnapshot<?> entitySnapshot
   ) {
      super(level, x, y, z, xd, yd, zd, null);
      this.f_107225_ = 20;
      this.f_107227_ = 1.0F;
      this.f_107228_ = 1.0F;
      this.f_107229_ = 1.0F;
      this.alphaO = 0.3F;
      this.f_107230_ = 0.3F;
      this.entitySnapshot = entitySnapshot;
      this.yawO = entitySnapshot.getYRot();
      this.yaw = entitySnapshot.getYRot();
   }

   public void m_5989_() {
      super.m_5989_();
      this.alphaO = this.f_107230_;
      this.f_107230_ = (float)(this.f_107225_ - this.f_107224_) / (float)this.f_107225_ * 0.8F;
   }

   public void m_5744_(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
      float alpha = this.alphaO + (this.f_107230_ - this.alphaO) * partialTicks;
      int lightColor = this.m_6355_(partialTicks);
      PoseStack poseStack = new PoseStack();
      this.setupPoseStack(poseStack, camera, partialTicks);
      BufferSource buffers = Minecraft.m_91087_().m_91269_().m_110104_();
      this.entitySnapshot
         .renderTextured(poseStack, buffers, EpicFightRenderTypes::entityAfterimageStencil, DrawingFunction.POSITION_TEX, 0, 0.0F, 0.0F, 0.0F, 1.0F);
      this.entitySnapshot.renderItems(poseStack, buffers, EpicFightRenderTypes.itemAfterimageStencil(), DrawingFunction.POSITION_TEX, lightColor, 1.0F);
      buffers.m_173043_();
      this.entitySnapshot
         .renderTextured(
            poseStack,
            buffers,
            EpicFightRenderTypes::entityAfterimageTranslucent,
            DrawingFunction.NEW_ENTITY,
            lightColor,
            this.f_107227_,
            this.f_107228_,
            this.f_107229_,
            alpha
         );
      this.entitySnapshot.renderItems(poseStack, buffers, EpicFightRenderTypes.itemAfterimageTranslucent(), DrawingFunction.NEW_ENTITY, lightColor, alpha);
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
      Quaternionf rotation = new Quaternionf(0.0F, 0.0F, 0.0F, 1.0F);
      float roll = Mth.m_14189_(partialTicks, this.f_107204_, this.f_107231_);
      float pitch = Mth.m_14189_(partialTicks, this.pitchO, this.pitch);
      float yaw = Mth.m_14189_(partialTicks, this.yawO, this.yaw);
      rotation.mul(QuaternionUtils.YP.rotationDegrees(180.0F - yaw));
      rotation.mul(QuaternionUtils.XP.rotationDegrees(pitch));
      rotation.mul(QuaternionUtils.ZP.rotationDegrees(roll));
      poseStack.m_252781_(rotation);
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

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Particle createParticle(
         @NotNull SimpleParticleType typeIn, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
      ) {
         Entity entity = level.m_6815_((int)Double.doubleToLongBits(xSpeed));
         LivingEntityPatch<?> entityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
         if (entityPatch != null && ClientEngine.getInstance().renderEngine.hasRendererFor(entityPatch.getOriginal())) {
            EntitySnapshot<?> entitySnapshot = entityPatch.captureEntitySnapshot();
            if (entitySnapshot != null) {
               return new TransparentEntityAfterImageParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, entitySnapshot);
            }
         }

         return null;
      }
   }
}
