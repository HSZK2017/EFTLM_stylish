package com.dmc.invincible_dmc.client.particles;

import com.dmc.invincible_dmc.api.IDParticleRenderTypes;
import com.dmc.invincible_dmc.client.config.AAAPPerformanceClientConfig;
import com.dmc.invincible_dmc.client.model.IDMeshes;
import com.dmc.invincible_dmc.client.util.ParticleMeshDestroyer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Mesh;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Mesh.DrawingFunction;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class YamatoLastSphere extends Particle {
   private static final float TEXTURE_FLOW_U = 0.005F;
   private static final float TEXTURE_FLOW_V = 0.005F;
   private static final float ROT_SPEED_YAW = 0.005F;
   private static final float ROT_SPEED_PITCH = 0.003F;
   private static final float ROT_SPEED_ROLL = 0.002F;
   private LivingEntityPatch<?> caster;
   private final AssetAccessor<? extends Mesh> meshAccessor;
   private final MeshAccessor<SkinnedMesh> managedSkinnedMesh;
   private final ResourceLocation texture;
   private boolean destroyed;
   private float pitch;
   private float pitchO;
   private float yaw;
   private float yawO;
   private float scale = 1.0F;
   private float scaleO = 1.0F;
   private float uvOffsetU;
   private float uvOffsetV;

   public YamatoLastSphere(
      ClientLevel level,
      double x,
      double y,
      double z,
      double xSpeed,
      double ySpeed,
      double zSpeed,
      AssetAccessor<? extends Mesh> meshAccessor,
      ResourceLocation texture,
      MeshAccessor<SkinnedMesh> managedSkinnedMesh
   ) {
      super(level, x, y, z, xSpeed, ySpeed, zSpeed);
      this.meshAccessor = meshAccessor;
      this.managedSkinnedMesh = managedSkinnedMesh;
      this.texture = texture;
      if (managedSkinnedMesh != null) {
         ParticleMeshDestroyer.onParticleCreated(managedSkinnedMesh);
      }

      this.f_107225_ = 44;
      this.f_107219_ = false;
      this.f_107231_ = (float)xSpeed;
      this.pitch = (float)zSpeed;
      this.scale = 12.0F;
      this.f_107230_ = 0.36F;
      Entity entity = level.m_6815_((int)Double.doubleToLongBits(ySpeed));
      if (entity != null) {
         this.caster = (LivingEntityPatch<?>)entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
      }
   }

   public void m_5989_() {
      if (this.f_107224_++ >= this.f_107225_) {
         this.m_107274_();
      } else {
         this.pitchO = this.pitch;
         this.yawO = this.yaw;
         this.f_107204_ = this.f_107231_;
         this.scaleO = this.scale;
      }
   }

   public void m_107274_() {
      if (!this.destroyed) {
         this.destroyed = true;
         if (this.managedSkinnedMesh != null) {
            ParticleMeshDestroyer.onParticleDestroyed(this.managedSkinnedMesh);
         }
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

   public void m_5744_(@NotNull VertexConsumer vertexBuffer, Camera camera, float partialTick) {
      this.yaw += 0.005F;
      this.pitch += 0.003F;
      this.f_107231_ += 0.002F;
      this.uvOffsetU += 0.005F;
      this.uvOffsetV += 0.005F;
      if (this.scale <= 85.0F) {
         this.scale = this.scale + (float)Math.max(30 - this.f_107224_, 0) * 0.12F;
      }

      float remainingLife = (float)(this.f_107225_ - this.f_107224_) / (float)this.f_107225_;
      this.f_107230_ = Math.min(remainingLife, 0.36F);
      PoseStack poseStack = new PoseStack();
      poseStack.m_85836_();
      this.setupPoseStack(poseStack, camera, partialTick);
      RenderSystem.setShaderTexture(0, this.texture);
      RenderSystem.enableBlend();
      float uOffset = this.uvOffsetU;
      float vOffset = this.uvOffsetV;
      DrawingFunction flowDrawing = (builder, posX, posY, posZ, normX, normY, normZ, packedLight, red, green, blue, alpha, u, v, overlay) -> DrawingFunction.POSITION_TEX_COLOR_LIGHTMAP
            .draw(builder, posX, posY, posZ, normX, normY, normZ, packedLight, red, green, blue, alpha, u + uOffset, v + vOffset, overlay);
      ((Mesh)this.meshAccessor.get())
         .draw(
            poseStack,
            vertexBuffer,
            flowDrawing,
            this.m_6355_(partialTick),
            this.f_107227_,
            this.f_107228_,
            this.f_107229_,
            this.f_107230_,
            OverlayTexture.f_118083_
         );
      poseStack.m_85849_();
      if (this.caster != null && this.caster.getStunShield() <= 0.0F) {
         this.m_107274_();
      }
   }

   private void setupPoseStack(PoseStack poseStack, Camera camera, float partialTick) {
      float interpolatedYaw = Mth.m_14179_(partialTick, this.yawO, this.yaw);
      float interpolatedPitch = Mth.m_14179_(partialTick, this.pitchO, this.pitch);
      float interpolatedRoll = Mth.m_14179_(partialTick, this.f_107204_, this.f_107231_);
      Vec3 cameraPosition = camera.m_90583_();
      float x = (float)(Mth.m_14139_((double)partialTick, this.f_107209_, this.f_107212_) - cameraPosition.m_7096_());
      float y = (float)(Mth.m_14139_((double)partialTick, this.f_107210_, this.f_107213_) - cameraPosition.m_7098_());
      float z = (float)(Mth.m_14139_((double)partialTick, this.f_107211_, this.f_107214_) - cameraPosition.m_7094_());
      float interpolatedScale = Mth.m_14179_(partialTick, this.scaleO, this.scale);
      poseStack.m_252880_(x, y, z);
      poseStack.m_252781_(QuaternionUtils.XP.rotationDegrees(interpolatedPitch));
      poseStack.m_252781_(QuaternionUtils.ZP.rotationDegrees(interpolatedRoll));
      poseStack.m_252781_(QuaternionUtils.YP.rotationDegrees(interpolatedYaw));
      float time = ((float)this.f_107224_ + partialTick) * 0.1F;
      float intensity = 0.05F;
      float distortX = 1.0F + Mth.m_14031_(time * 1.3F + 0.5F) * intensity;
      float distortY = 1.0F + Mth.m_14031_(time * 0.9F) * intensity;
      float distortZ = 1.0F + Mth.m_14089_(time * 1.1F + 1.0F) * intensity;
      poseStack.m_85841_(interpolatedScale * distortX, interpolatedScale * distortY, interpolatedScale * distortZ);
   }

   public int m_6355_(float partialTick) {
      int light = super.m_6355_(partialTick);
      int skyLight = light >> 16 & 0xFF;
      return 240 | skyLight << 16;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Provider(SpriteSet spriteSet) {
      }

      public Particle createParticle(
         @NotNull SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
      ) {
         boolean useStaticMesh = AAAPPerformanceClientConfig.isEnabled(AAAPPerformanceClientConfig.USE_STATIC_YAMATO_LAST_SPHERE);
         return new YamatoLastSphere(
            level,
            x,
            y,
            z,
            xSpeed,
            ySpeed,
            zSpeed,
            useStaticMesh ? IDMeshes.YAMATO_LAST_SPHERE : IDMeshes.YAMATO_SPHERE,
            new ResourceLocation("invincible_dmc", "textures/models/particle/yamato.png"),
            useStaticMesh ? null : IDMeshes.YAMATO_SPHERE
         );
      }
   }
}
