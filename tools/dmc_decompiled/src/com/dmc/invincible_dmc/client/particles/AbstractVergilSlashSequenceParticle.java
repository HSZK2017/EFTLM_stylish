package com.dmc.invincible_dmc.client.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

abstract class AbstractVergilSlashSequenceParticle extends TextureSheetParticle {
   private static final int FRAME_TIME_MILLIS = 20;
   private final SpriteSet sprites;
   private final Quaternionf renderRotation;
   private final int sequenceDurationMillis;

   protected AbstractVergilSlashSequenceParticle(
      ClientLevel level, double x, double y, double z, float rollX, float rollY, float rollZ, float size, int frameCount, SpriteSet sprites
   ) {
      super(level, x, y, z);
      this.sprites = sprites;
      this.sequenceDurationMillis = frameCount * 20;
      this.f_107225_ = (this.sequenceDurationMillis + 49) / 50;
      this.f_107663_ = size;
      this.f_107219_ = false;
      this.renderRotation = new Quaternionf().rotateY(rollY).rotateX(rollX).rotateZ(-rollZ);
      this.m_108337_(sprites.m_5819_(0, this.sequenceDurationMillis));
   }

   public void m_5989_() {
      this.f_107209_ = this.f_107212_;
      this.f_107210_ = this.f_107213_;
      this.f_107211_ = this.f_107214_;
      if (this.f_107224_++ >= this.f_107225_) {
         this.m_107274_();
      }
   }

   public void m_5744_(@NotNull VertexConsumer buffer, @NotNull Camera camera, float partialTick) {
      Vec3 cameraPosition = camera.m_90583_();
      float x = (float)(Mth.m_14139_((double)partialTick, this.f_107209_, this.f_107212_) - cameraPosition.m_7096_());
      float y = (float)(Mth.m_14139_((double)partialTick, this.f_107210_, this.f_107213_) - cameraPosition.m_7098_());
      float z = (float)(Mth.m_14139_((double)partialTick, this.f_107211_, this.f_107214_) - cameraPosition.m_7094_());
      int elapsedMillis = Mth.m_14045_((int)(((float)this.f_107224_ + partialTick) * 50.0F), 0, this.sequenceDurationMillis - 1);
      this.m_108337_(this.sprites.m_5819_(elapsedMillis, this.sequenceDurationMillis));
      Vector3f[] vertices = new Vector3f[]{
         new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
      };
      float size = this.m_5902_(partialTick);

      for (Vector3f vertex : vertices) {
         vertex.mul(size);
         vertex.rotate(this.renderRotation);
         vertex.add(x, y, z);
      }

      float minU = this.m_5970_();
      float maxU = this.m_5952_();
      float minV = this.m_5951_();
      float maxV = this.m_5950_();
      int light = this.m_6355_(partialTick);
      buffer.m_5483_((double)vertices[0].x(), (double)vertices[0].y(), (double)vertices[0].z())
         .m_7421_(maxU, maxV)
         .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_)
         .m_85969_(light)
         .m_5752_();
      buffer.m_5483_((double)vertices[1].x(), (double)vertices[1].y(), (double)vertices[1].z())
         .m_7421_(maxU, minV)
         .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_)
         .m_85969_(light)
         .m_5752_();
      buffer.m_5483_((double)vertices[2].x(), (double)vertices[2].y(), (double)vertices[2].z())
         .m_7421_(minU, minV)
         .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_)
         .m_85969_(light)
         .m_5752_();
      buffer.m_5483_((double)vertices[3].x(), (double)vertices[3].y(), (double)vertices[3].z())
         .m_7421_(minU, maxV)
         .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_)
         .m_85969_(light)
         .m_5752_();
   }

   public boolean shouldCull() {
      return false;
   }

   @NotNull
   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107431_;
   }

   public int m_6355_(float partialTick) {
      return 15728880;
   }
}
