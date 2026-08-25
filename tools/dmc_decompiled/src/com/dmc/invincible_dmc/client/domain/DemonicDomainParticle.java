package com.dmc.invincible_dmc.client.domain;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class DemonicDomainParticle extends Particle {
   private final float baseAlpha;
   private final float pulseOffset;
   private final float particleSize;
   private int activationDelay;

   public DemonicDomainParticle(ClientLevel level, double x, double y, double z, float r, float g, float b, float alpha) {
      this(level, x, y, z, r, g, b, alpha, 0, 0.1F, 18);
   }

   private DemonicDomainParticle(
      ClientLevel level, double x, double y, double z, float r, float g, float b, float alpha, int activationDelay, float particleSize, int lifetime
   ) {
      super(level, x, y, z);
      this.baseAlpha = alpha;
      this.activationDelay = activationDelay;
      this.particleSize = particleSize;
      this.f_107227_ = r;
      this.f_107228_ = g;
      this.f_107229_ = b;
      this.f_107225_ = lifetime;
      this.f_107224_ = 0;
      this.f_107226_ = 0.0F;
      this.f_107215_ = 0.0;
      this.f_107216_ = 0.02 + (double)this.f_107223_.m_188501_() * 0.04;
      this.f_107217_ = 0.0;
      this.pulseOffset = this.f_107223_.m_188501_() * 6.28318F;
      this.f_107230_ = 0.0F;
   }

   public void m_5989_() {
      if (this.activationDelay > 0) {
         this.f_107209_ = this.f_107212_;
         this.f_107210_ = this.f_107213_;
         this.f_107211_ = this.f_107214_;
         this.activationDelay--;
         this.f_107230_ = 0.0F;
      } else {
         super.m_5989_();
         float progress = (float)this.f_107224_ / (float)this.f_107225_;
         float pulse = (float)Math.sin((double)progress * Math.PI * 4.0 + (double)this.pulseOffset) * 0.15F + 0.85F;
         float fadeInProgress = Mth.m_14036_(progress / 0.16F, 0.0F, 1.0F);
         float fadeOutProgress = Mth.m_14036_((progress - 0.68F) / 0.32F, 0.0F, 1.0F);
         float fadeIn = fadeInProgress * fadeInProgress * (3.0F - 2.0F * fadeInProgress);
         float fadeOutSmooth = fadeOutProgress * fadeOutProgress * (3.0F - 2.0F * fadeOutProgress);
         float fadeOut = 1.0F - fadeOutSmooth;
         this.f_107230_ = this.baseAlpha * pulse * fadeIn * fadeOut;
      }
   }

   public void m_5744_(VertexConsumer buffer, Camera camera, float partialTicks) {
      if (!(this.f_107230_ < 0.01F)) {
         Vec3 camPos = camera.m_90583_();
         float x = (float)(Mth.m_14139_((double)partialTicks, this.f_107209_, this.f_107212_) - camPos.m_7096_());
         float y = (float)(Mth.m_14139_((double)partialTicks, this.f_107210_, this.f_107213_) - camPos.m_7098_());
         float z = (float)(Mth.m_14139_((double)partialTicks, this.f_107211_, this.f_107214_) - camPos.m_7094_());
         float halfSize = this.particleSize * 0.5F;
         Quaternionf rotation = camera.m_253121_();
         int packedLight = 15728880;
         Vector3f vertex = new Vector3f();
         this.renderCorner(buffer, vertex, rotation, x, y, z, -halfSize, -halfSize, 0.0F, 0.0F, packedLight);
         this.renderCorner(buffer, vertex, rotation, x, y, z, halfSize, -halfSize, 1.0F, 0.0F, packedLight);
         this.renderCorner(buffer, vertex, rotation, x, y, z, halfSize, halfSize, 1.0F, 1.0F, packedLight);
         this.renderCorner(buffer, vertex, rotation, x, y, z, -halfSize, halfSize, 0.0F, 1.0F, packedLight);
      }
   }

   private void renderCorner(
      VertexConsumer buffer, Vector3f vertex, Quaternionf rotation, float x, float y, float z, float offsetX, float offsetY, float u, float v, int packedLight
   ) {
      vertex.set(offsetX, offsetY, 0.0F).rotate(rotation).add(x, y, z);
      buffer.m_5483_((double)vertex.x(), (double)vertex.y(), (double)vertex.z())
         .m_7421_(u, v)
         .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_)
         .m_85969_(packedLight)
         .m_5752_();
   }

   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107431_;
   }

   public static void spawnSphere(ClientLevel level, Vec3 center, float radius, int count) {
      ParticleEngine engine = Minecraft.m_91087_().f_91061_;

      for (int i = 0; i < count; i++) {
         double theta = (Math.PI * 2) * Math.random();
         double phi = Math.acos(2.0 * Math.random() - 1.0);
         double r = (double)radius * (0.45 + 0.5 * Math.random());
         double dx = r * Math.sin(phi) * Math.cos(theta);
         double dy = r * Math.sin(phi) * Math.sin(theta);
         double dz = r * Math.cos(phi);
         float colorVar = (float)Math.random();
         float rCol = 0.04F + colorVar * 0.18F;
         float gCol = 0.18F + colorVar * 0.3F;
         float bCol = 0.55F + colorVar * 0.4F;
         float alpha = 0.08F + (float)Math.random() * 0.18F;
         float normalizedRadius = (float)(r / (double)radius);
         int activationDelay = Math.max(0, Math.round(normalizedRadius * 15.0F) - 3);
         float size = 0.055F + (float)Math.random() * 0.055F;
         int lifetime = 15 + level.f_46441_.m_188503_(5);
         DemonicDomainParticle particle = new DemonicDomainParticle(
            level, center.f_82479_ + dx, center.f_82480_ + dy, center.f_82481_ + dz, rCol, gCol, bCol, alpha, activationDelay, size, lifetime
         );
         double horizontalLength = Math.max(Math.sqrt(dx * dx + dz * dz), 0.001);
         particle.f_107215_ = -dx * 0.003 - dz / horizontalLength * 0.01;
         particle.f_107217_ = -dz * 0.003 + dx / horizontalLength * 0.01;
         particle.f_107216_ = 0.008 + Math.random() * 0.018;
         engine.m_107344_(particle);
      }
   }

   public static void spawnRing(ClientLevel level, Vec3 center, float radius, int count, float yOffset) {
      spawnRing(level, center, radius, count, yOffset, 0);
   }

   public static void spawnRing(ClientLevel level, Vec3 center, float radius, int count, float yOffset, int activationDelay) {
      ParticleEngine engine = Minecraft.m_91087_().f_91061_;

      for (int i = 0; i < count; i++) {
         double angle = (Math.PI * 2) * (double)i / (double)count;
         double dx = Math.cos(angle) * (double)radius;
         double dz = Math.sin(angle) * (double)radius;
         double dy = (double)yOffset + (Math.random() - 0.5) * 0.4;
         float rCol = 0.12F + (float)Math.random() * 0.15F;
         float gCol = 0.35F + (float)Math.random() * 0.22F;
         float bCol = 0.78F + (float)Math.random() * 0.2F;
         float alpha = 0.16F + (float)Math.random() * 0.2F;
         int delayedStart = activationDelay + level.f_46441_.m_188503_(3);
         float size = 0.07F + (float)Math.random() * 0.05F;
         int lifetime = 16 + level.f_46441_.m_188503_(5);
         DemonicDomainParticle particle = new DemonicDomainParticle(
            level, center.f_82479_ + dx, center.f_82480_ + dy, center.f_82481_ + dz, rCol, gCol, bCol, alpha, delayedStart, size, lifetime
         );
         particle.f_107215_ = -dx * 0.0035 - Math.sin(angle) * 0.012;
         particle.f_107217_ = -dz * 0.0035 + Math.cos(angle) * 0.012;
         particle.f_107216_ = 0.012 + Math.random() * 0.025;
         engine.m_107344_(particle);
      }
   }
}
