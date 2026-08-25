package com.dmc.invincible_dmc.client.particles.spark;

import com.dmc.invincible_dmc.client.render.IDRenderType;
import com.dmc.invincible_dmc.client.render.custom.BloomParticleRenderType;
import com.dmc.invincible_dmc.particle.DMCParticles;
import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class SparkParticle extends TextureSheetParticle {
   private static final ResourceLocation SPARK1_TEX = IDRenderType.GetTexture("particle/spark1");
   private static final ResourceLocation SPARK2_TEX = IDRenderType.GetTexture("particle/spark2");
   private static final BloomParticleRenderType BLOOM_RT_SPARK1 = IDRenderType.getBloomRenderTypeByTexture(SPARK1_TEX);
   private static final BloomParticleRenderType BLOOM_RT_SPARK2 = IDRenderType.getBloomRenderTypeByTexture(SPARK2_TEX);
   private final SparkParticle.PhysicsType physicsType;
   private final float baseSize;
   private final float rotationSpeed;
   private final float initialR;
   private final float initialG;
   private final float initialB;
   private final BloomParticleRenderType bloomRenderType;

   public SparkParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SparkParticle.PhysicsType physicsType) {
      super(level, x, y, z);
      this.f_107212_ = x;
      this.f_107213_ = y;
      this.f_107214_ = z;
      float amp = 0.75F + this.f_107223_.m_188501_() * 0.25F;
      this.f_107227_ = amp * (0.65F + this.f_107223_.m_188501_() * 0.3F);
      this.f_107228_ = amp * (0.72F + this.f_107223_.m_188501_() * 0.23F);
      this.f_107229_ = amp * (0.88F + this.f_107223_.m_188501_() * 0.12F);
      this.initialR = this.f_107227_;
      this.initialG = this.f_107228_;
      this.initialB = this.f_107229_;
      this.baseSize = physicsType == SparkParticle.PhysicsType.NORMAL
         ? (this.f_107223_.m_188501_() * 0.02F + 0.01F) * 0.4F
         : (this.f_107223_.m_188501_() * 0.03F + 0.02F) * 0.4F;
      this.f_107663_ = this.baseSize;
      this.f_107225_ = (physicsType == SparkParticle.PhysicsType.NORMAL ? 20 : 5) + this.f_107223_.m_188503_(10);
      this.f_107219_ = true;
      this.f_107226_ = physicsType == SparkParticle.PhysicsType.NORMAL ? 9.8F : (physicsType == SparkParticle.PhysicsType.EXPANSIVE ? 0.4F : 1.2F);
      this.f_107231_ = this.f_107223_.m_188501_() * 360.0F;
      this.f_107204_ = this.f_107231_;
      this.rotationSpeed = (this.f_107223_.m_188501_() - 0.5F) * 0.5F;
      Vec3 deltaMovement = physicsType.function.getDeltaMovement(xd, yd, zd);
      this.f_107215_ = deltaMovement.f_82479_ * (0.7 + this.f_107223_.m_188500_() * 0.6);
      this.f_107216_ = deltaMovement.f_82480_ * (0.7 + this.f_107223_.m_188500_() * 0.6);
      this.f_107217_ = deltaMovement.f_82481_ * (0.7 + this.f_107223_.m_188500_() * 0.6);
      this.physicsType = physicsType;
      this.bloomRenderType = physicsType == SparkParticle.PhysicsType.NORMAL ? BLOOM_RT_SPARK2 : BLOOM_RT_SPARK1;
   }

   public boolean shouldCull() {
      return false;
   }

   public void m_5744_(@NotNull VertexConsumer vertexConsumer, @NotNull Camera camera, float partialTick) {
      if (PostEffectPipelines.isActive()) {
         BloomParticleRenderType.markBloomDrawn();
         this.bloomRenderType.callPipeline();
         Vec3 cameraPos = camera.m_90583_();
         float x = (float)(Mth.m_14139_((double)partialTick, this.f_107209_, this.f_107212_) - cameraPos.m_7096_());
         float y = (float)(Mth.m_14139_((double)partialTick, this.f_107210_, this.f_107213_) - cameraPos.m_7098_());
         float z = (float)(Mth.m_14139_((double)partialTick, this.f_107211_, this.f_107214_) - cameraPos.m_7094_());
         Quaternionf rotation;
         if (this.f_107231_ == 0.0F) {
            rotation = camera.m_253121_();
         } else {
            rotation = new Quaternionf(camera.m_253121_());
            rotation.rotateZ(Mth.m_14179_(partialTick, this.f_107204_, this.f_107231_));
         }

         Vector3f[] vertices = new Vector3f[]{
            new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
         };
         float size = this.f_107663_;

         for (int i = 0; i < 4; i++) {
            vertices[i].rotate(rotation);
            vertices[i].mul(size);
            vertices[i].add(x, y, z);
         }

         float u0 = this.m_5970_();
         float u1 = this.m_5952_();
         float v0 = this.m_5951_();
         float v1 = this.m_5950_();
         int light = this.m_6355_(partialTick);
         vertexConsumer.m_5483_((double)vertices[0].x(), (double)vertices[0].y(), (double)vertices[0].z())
            .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_)
            .m_7421_(u1, v1)
            .m_85969_(light)
            .m_5752_();
         vertexConsumer.m_5483_((double)vertices[1].x(), (double)vertices[1].y(), (double)vertices[1].z())
            .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_)
            .m_7421_(u1, v0)
            .m_85969_(light)
            .m_5752_();
         vertexConsumer.m_5483_((double)vertices[2].x(), (double)vertices[2].y(), (double)vertices[2].z())
            .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_)
            .m_7421_(u0, v0)
            .m_85969_(light)
            .m_5752_();
         vertexConsumer.m_5483_((double)vertices[3].x(), (double)vertices[3].y(), (double)vertices[3].z())
            .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_)
            .m_7421_(u0, v1)
            .m_85969_(light)
            .m_5752_();
      }
   }

   @NotNull
   public ParticleRenderType m_7556_() {
      return this.bloomRenderType;
   }

   public void m_5989_() {
      super.m_5989_();
      this.f_107204_ = this.f_107231_;
      this.f_107231_ = this.f_107231_ + this.rotationSpeed;
      float lifeProgress = (float)this.f_107224_ / (float)this.f_107225_;
      if (this.physicsType == SparkParticle.PhysicsType.EXPANSIVE) {
         this.f_107663_ = this.baseSize * (1.0F + lifeProgress * 0.8F);
      } else {
         this.f_107663_ = this.baseSize * (1.0F - lifeProgress * 0.5F);
      }

      float fade = 1.0F - lifeProgress * 0.85F;
      this.f_107227_ = this.initialR * fade;
      this.f_107228_ = this.initialG * fade;
      this.f_107229_ = this.initialB * fade;
      if (this.physicsType == SparkParticle.PhysicsType.EXPANSIVE) {
         this.f_107215_ *= 0.88;
         this.f_107216_ *= 0.88;
         this.f_107217_ *= 0.88;
      } else if (this.physicsType == SparkParticle.PhysicsType.CONTRACTIVE) {
         this.f_107215_ *= 0.95;
         this.f_107216_ *= 0.95;
         this.f_107217_ *= 0.95;
      } else {
         this.f_107215_ *= 0.92;
         this.f_107216_ *= 0.92;
         this.f_107217_ *= 0.92;
      }

      if (this.f_107223_.m_188503_(8) == 0) {
         this.f_107215_ = this.f_107215_ + (this.f_107223_.m_188500_() - 0.5) * 0.015;
         this.f_107216_ = this.f_107216_ + (this.f_107223_.m_188500_() - 0.5) * 0.015;
         this.f_107217_ = this.f_107217_ + (this.f_107223_.m_188500_() - 0.5) * 0.015;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class ContractiveDustProvider implements ParticleProvider<SimpleParticleType> {
      protected SpriteSet sprite;

      public ContractiveDustProvider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         SparkParticle SparkParticle = new SparkParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, SparkParticle.PhysicsType.CONTRACTIVE);
         SparkParticle.m_108335_(this.sprite);
         return SparkParticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class ContractiveMetaParticle extends NoRenderParticle {
      private final double radius;
      private final int density;

      public ContractiveMetaParticle(ClientLevel level, double x, double y, double z, double radius, int lifetime, int density) {
         super(level, x, y, z);
         this.radius = radius;
         this.f_107225_ = lifetime;
         this.density = (int)((double)density * 1.3);
      }

      public void m_5989_() {
         super.m_5989_();

         for (int x = -1; x <= 1; x += 2) {
            for (int y = -1; y <= 1; y += 2) {
               for (int z = -1; z <= 1; z += 2) {
                  for (int i = 0; i < this.density; i++) {
                     Vec3 rand = new Vec3(Math.random() * (double)x, Math.random() * (double)y * 0.8, Math.random() * (double)z)
                        .m_82541_()
                        .m_82490_(this.radius);
                     this.f_107208_
                        .m_7106_(
                           (ParticleOptions)DMCParticles.SPARK_CONTRACTILE.get(),
                           this.f_107212_ + rand.f_82479_,
                           this.f_107213_ + rand.f_82480_,
                           this.f_107214_ + rand.f_82481_,
                           -rand.f_82479_,
                           -rand.f_82480_,
                           -rand.f_82481_
                        );
                  }
               }
            }
         }
      }

      @OnlyIn(Dist.CLIENT)
      public static class Provider implements ParticleProvider<SimpleParticleType> {
         public Particle createParticle(
            SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
         ) {
            return new SparkParticle.ContractiveMetaParticle(
               worldIn, x, y, z, xSpeed, (int)Double.doubleToLongBits(ySpeed), (int)Double.doubleToLongBits(zSpeed)
            );
         }
      }
   }

   @FunctionalInterface
   interface DeltaMovementFunction {
      Vec3 getDeltaMovement(double var1, double var3, double var5);
   }

   @OnlyIn(Dist.CLIENT)
   public static class ExpansiveDustProvider implements ParticleProvider<SimpleParticleType> {
      protected SpriteSet sprite;

      public ExpansiveDustProvider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         SparkParticle SparkParticle = new SparkParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, SparkParticle.PhysicsType.EXPANSIVE);
         SparkParticle.m_108335_(this.sprite);
         return SparkParticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class ExpansiveMetaParticle extends NoRenderParticle {
      public ExpansiveMetaParticle(ClientLevel level, double x, double y, double z, double radius, int density) {
         super(level, x, y, z);
         int newDensity = (int)((double)density * 1.3);

         for (int vx = -1; vx <= 1; vx += 2) {
            for (int vz = -1; vz <= 1; vz += 2) {
               for (int i = 0; i < newDensity; i++) {
                  Vec3 rand = new Vec3(Math.random() * (double)vx, Math.random() * 0.8, Math.random() * (double)vz).m_82541_().m_82490_(radius);
                  level.m_7106_((ParticleOptions)DMCParticles.SPARK_EXPANSIVE.get(), x, y, z, rand.f_82479_, rand.f_82480_, rand.f_82481_);
               }
            }
         }
      }

      @OnlyIn(Dist.CLIENT)
      public static class Provider implements ParticleProvider<SimpleParticleType> {
         public Particle createParticle(
            SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
         ) {
            return new SparkParticle.ExpansiveMetaParticle(worldIn, x, y, z, xSpeed, (int)Double.doubleToLongBits(ySpeed));
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class NormalDustProvider implements ParticleProvider<SimpleParticleType> {
      protected SpriteSet sprite;

      public NormalDustProvider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         SparkParticle SparkParticle = new SparkParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, SparkParticle.PhysicsType.NORMAL);
         SparkParticle.m_108335_(this.sprite);
         return SparkParticle;
      }
   }

   public static enum PhysicsType {
      EXPANSIVE(Vec3::new),
      CONTRACTIVE((dx, dy, dz) -> new Vec3(dx * 0.02, dy * 0.02, dz * 0.02)),
      NORMAL(Vec3::new);

      final SparkParticle.DeltaMovementFunction function;

      private PhysicsType(SparkParticle.DeltaMovementFunction function) {
         this.function = function;
      }
   }
}
