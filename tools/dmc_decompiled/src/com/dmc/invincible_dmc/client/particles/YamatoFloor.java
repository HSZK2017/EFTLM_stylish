package com.dmc.invincible_dmc.client.particles;

import com.dmc.invincible_dmc.api.IDParticleRenderTypes;
import com.dmc.invincible_dmc.client.model.IDMeshes;
import com.dmc.invincible_dmc.client.util.ParticleMeshDestroyer;
import com.guhao.vix.particles.TexturedSkinModelParticle;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class YamatoFloor extends TexturedSkinModelParticle {
   private LivingEntityPatch<?> caster;
   private final AssetAccessor<SkinnedMesh> meshAccessor;
   private boolean isDestroyed = false;

   public YamatoFloor(
      ClientLevel level, double x, double y, double z, double xd, double yd, double zd, AssetAccessor<SkinnedMesh> particleMesh, ResourceLocation texture
   ) {
      super(level, x, y, z, xd, yd, zd, particleMesh, texture);
      this.meshAccessor = particleMesh;
      ParticleMeshDestroyer.onParticleCreated((MeshAccessor<SkinnedMesh>)particleMesh);
      this.f_107225_ = 8;
      this.f_107219_ = false;
      this.f_107231_ = (float)xd;
      this.pitch = (float)zd;
      this.f_107230_ = 0.36F;
      Entity entity = level.m_6815_((int)Double.doubleToLongBits(yd));
      if (entity != null) {
         this.caster = (LivingEntityPatch<?>)entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
      }
   }

   public void m_5989_() {
      super.m_5989_();
      if (this.f_107224_ >= this.f_107225_) {
         this.m_107274_();
      }
   }

   public void m_107274_() {
      if (!this.isDestroyed) {
         this.isDestroyed = true;
         ParticleMeshDestroyer.onParticleDestroyed((MeshAccessor<SkinnedMesh>)this.meshAccessor);
      }

      super.m_107274_();
   }

   public boolean shouldCull() {
      return false;
   }

   @NotNull
   public ParticleRenderType m_7556_() {
      return IDParticleRenderTypes.ID_PARTICLE_MODEL_NO_NORMAL;
   }

   public void m_5744_(@NotNull VertexConsumer vertexBuffer, Camera camera, float pt) {
      super.m_5744_(vertexBuffer, camera, pt);
      this.yaw += 0.05F;
      this.scale = this.scale + (float)Math.max(30 - this.f_107224_, 0) * 0.2F;
      this.f_107230_ = (float)(this.f_107225_ - this.f_107224_) / (float)this.f_107225_ * 2.0F;
      if (this.caster != null && this.caster.getStunShield() <= 0.0F) {
         this.m_107274_();
      }
   }

   protected void setupPoseStack(PoseStack poseStack, Camera camera, float partialTicks) {
      float yaw = Mth.m_14179_(partialTicks, this.yawO, this.yaw);
      Vec3 vec3 = camera.m_90583_();
      float x = (float)(Mth.m_14139_((double)partialTicks, this.f_107209_, this.f_107212_) - vec3.m_7096_());
      float y = (float)(Mth.m_14139_((double)partialTicks, this.f_107210_, this.f_107213_) - vec3.m_7098_());
      float z = (float)(Mth.m_14139_((double)partialTicks, this.f_107211_, this.f_107214_) - vec3.m_7094_());
      float scale = Mth.m_14179_(partialTicks, this.scaleO, this.scale);
      poseStack.m_252880_(x, y, z);
      poseStack.m_252781_(QuaternionUtils.XP.rotationDegrees(this.pitch));
      poseStack.m_252781_(QuaternionUtils.ZP.rotationDegrees(this.f_107231_));
      poseStack.m_252781_(QuaternionUtils.YP.rotationDegrees(yaw));
      poseStack.m_85841_(scale, scale, scale);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Provider(SpriteSet spriteSet) {
      }

      public Particle createParticle(
         @NotNull SimpleParticleType typeIn, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
      ) {
         return new YamatoFloor(
            level,
            x,
            y,
            z,
            xSpeed,
            ySpeed,
            zSpeed,
            IDMeshes.YAMATO_FLOOR,
            new ResourceLocation("invincible_dmc", "textures/models/particle/yamato_particle.png")
         );
      }
   }
}
