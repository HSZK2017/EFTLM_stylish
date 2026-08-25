package com.dmc.invincible_dmc.client.particles;

import com.dmc.invincible_dmc.client.effeks.JudgementCutEffek;
import com.dmc.invincible_dmc.particle.DMCParticles;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class JudgementCutSequenceParticle extends TextureSheetParticle {
   private static final float SOURCE_HEIGHT = 636.0F;
   private static final float WIDTH_SCALE = 0.6163522F;
   private static final float HEIGHT_SCALE = 0.7044025F;
   private final SpriteSet sprites;
   private final Quaternionf renderRotation = new Quaternionf();

   protected JudgementCutSequenceParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
      super(level, x, y, z);
      this.sprites = sprites;
      this.f_107225_ = 15;
      this.f_107663_ = 4.5F;
      this.m_108337_(sprites.m_5819_(0, this.f_107225_));
      level.m_7106_((ParticleOptions)DMCParticles.COLOR_SHADER_PARTICLE.get(), x, y, z, 0.0, 0.0, 0.0);
      JudgementCutEffek.playJC(JudgementCutEffek.Type.LEVEL1, this.f_107208_, this.f_107212_, this.f_107213_, this.f_107214_, 0.3456F);
   }

   public void m_5989_() {
      this.f_107209_ = this.f_107212_;
      this.f_107210_ = this.f_107213_;
      this.f_107211_ = this.f_107214_;
      if (this.f_107224_++ >= this.f_107225_) {
         this.m_107274_();
      }
   }

   public void m_5744_(VertexConsumer buffer, Camera camera, float partialTick) {
      Vec3 camPos = camera.m_90583_();
      float px = (float)(Mth.m_14139_((double)partialTick, this.f_107209_, this.f_107212_) - camPos.m_7096_());
      float py = (float)(Mth.m_14139_((double)partialTick, this.f_107210_, this.f_107213_) - camPos.m_7098_());
      float pz = (float)(Mth.m_14139_((double)partialTick, this.f_107211_, this.f_107214_) - camPos.m_7094_());
      float ageWithPartial = Math.min((float)this.f_107224_ + partialTick, (float)this.f_107225_);
      int virtualAge = (int)(ageWithPartial * 1000.0F);
      int virtualLifetime = (int)((float)this.f_107225_ * 1000.0F);
      this.m_108337_(this.sprites.m_5819_(virtualAge, virtualLifetime));
      this.renderRotation.set(camera.m_253121_());
      if (this.f_107231_ != 0.0F) {
         this.renderRotation.mul(Axis.f_252403_.m_252961_(Mth.m_14179_(partialTick, this.f_107204_, this.f_107231_)));
      }

      Vector3f[] v = new Vector3f[]{
         new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
      };
      float size = this.m_5902_(partialTick);
      float halfWidth = size * 0.6163522F;
      float halfHeight = size * 0.7044025F;

      for (int i = 0; i < 4; i++) {
         v[i].mul(halfWidth, halfHeight, 1.0F);
         v[i].rotate(this.renderRotation);
         v[i].add(px, py, pz);
      }

      float u0 = this.m_5970_();
      float u1 = this.m_5952_();
      float v0 = this.m_5951_();
      float v1 = this.m_5950_();
      int light = this.m_6355_(partialTick);
      buffer.m_5483_((double)v[0].x(), (double)v[0].y(), (double)v[0].z())
         .m_7421_(u1, v1)
         .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_)
         .m_85969_(light)
         .m_5752_();
      buffer.m_5483_((double)v[1].x(), (double)v[1].y(), (double)v[1].z())
         .m_7421_(u1, v0)
         .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_)
         .m_85969_(light)
         .m_5752_();
      buffer.m_5483_((double)v[2].x(), (double)v[2].y(), (double)v[2].z())
         .m_7421_(u0, v0)
         .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_)
         .m_85969_(light)
         .m_5752_();
      buffer.m_5483_((double)v[3].x(), (double)v[3].y(), (double)v[3].z())
         .m_7421_(u0, v1)
         .m_85950_(this.f_107227_, this.f_107228_, this.f_107229_, this.f_107230_)
         .m_85969_(light)
         .m_5752_();
   }

   @NotNull
   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107431_;
   }

   public int m_6355_(float partialTick) {
      return 15728880;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet sprites) {
         this.sprites = sprites;
      }

      public Particle createParticle(
         SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
      ) {
         return new JudgementCutSequenceParticle(level, x, y, z, this.sprites);
      }
   }
}
