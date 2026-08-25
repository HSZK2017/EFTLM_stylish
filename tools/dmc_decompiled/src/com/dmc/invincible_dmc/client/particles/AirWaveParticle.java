package com.dmc.invincible_dmc.client.particles;

import com.dmc.invincible_dmc.client.render.IDRenderType;
import com.google.common.collect.Queues;
import com.guhao.vix.util.RenderUtils;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Queue;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class AirWaveParticle extends NoRenderParticle {
   private final float tar_r = 14.0F;
   int count;
   Queue<AirWaveParticle.Wave> waves = Queues.newConcurrentLinkedQueue();

   public AirWaveParticle(ClientLevel level, double x, double y, double z, int waveCount, int lifetime) {
      super(level, x, y, z);
      this.f_107225_ = lifetime;
      this.count = waveCount;
      this.waves.add(new AirWaveParticle.Wave(14.0F, 0.2F, 30));
   }

   public void m_5989_() {
      if (this.f_107224_++ >= this.f_107225_ && this.waves.isEmpty()) {
         this.m_107274_();
      } else if (this.f_107224_ < this.f_107225_ && this.f_107224_ % 3 == 0) {
         this.waves.add(new AirWaveParticle.Wave(14.0F, 0.2F, 40));
      }

      this.waves.forEach(wave -> {
         wave.tick();
         wave.pushParticle(this.f_107208_, this.f_107212_, this.f_107213_, this.f_107214_, this.f_107223_);
      });
      this.waves.removeIf(AirWaveParticle.Wave::isEnd);
   }

   public boolean shouldCull() {
      return false;
   }

   public static class AirParticle extends Particle {
      static IDRenderType.IDQuadParticleRenderType renderType = IDRenderType.getRenderTypeByTexture(IDRenderType.GetTexture("particle/fire"));
      float alphaO;

      public AirParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, int lifetime) {
         super(level, x, y, z, xd, yd, zd);
         this.f_107215_ = xd;
         this.f_107216_ = yd;
         this.f_107217_ = zd;
         this.alphaO = this.f_107230_;
         this.f_107219_ = false;
         this.f_107225_ = lifetime;
      }

      public void m_5989_() {
         this.f_107209_ = this.f_107212_;
         this.f_107210_ = this.f_107213_;
         this.f_107211_ = this.f_107214_;
         this.alphaO = this.f_107230_;
         if (this.f_107224_++ >= this.f_107225_) {
            this.m_107274_();
         } else {
            this.f_107212_ = this.f_107212_ + this.f_107215_;
            this.f_107213_ = this.f_107213_ + this.f_107216_;
            this.f_107214_ = this.f_107214_ + this.f_107217_;
         }

         this.f_107230_ = Math.min(0.5F, 0.5F * (float)(this.f_107225_ - this.f_107224_) / (float)this.f_107225_);
         this.f_107230_ = Math.max(this.f_107230_, 0.0F);
         this.m_107264_(this.f_107212_, this.f_107213_, this.f_107214_);
      }

      float getAlpha(float pt) {
         return Mth.m_14179_(pt, this.alphaO, this.f_107230_);
      }

      public void m_5744_(@NotNull VertexConsumer vertexConsumer, @NotNull Camera camera, float pt) {
         float alp = this.getAlpha(pt);
         float t_ = ((float)(this.f_107224_ % 10) + pt) / 9.0F;
         if (t_ <= 0.5F) {
            t_ = 4.0F * t_ - 1.0F;
         } else {
            t_ = -4.0F * t_ + 3.0F;
         }

         float sz = (0.5F + 0.1F * t_) * alp * 3.5F;
         RenderUtils.RenderQuadFaceOnCamera2(
            vertexConsumer,
            camera,
            (float)Mth.m_14139_((double)pt, this.f_107209_, this.f_107212_),
            (float)Mth.m_14139_((double)pt, this.f_107210_, this.f_107213_),
            (float)Mth.m_14139_((double)pt, this.f_107211_, this.f_107214_),
            this.f_107227_,
            this.f_107228_,
            this.f_107229_,
            alp,
            sz
         );
      }

      @NotNull
      public ParticleRenderType m_7556_() {
         return renderType;
      }

      public boolean shouldCull() {
         return false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Provider(SpriteSet spriteSet) {
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new AirWaveParticle(worldIn, x, y, z, 2, 5);
      }
   }

   static class Wave {
      float r = 0.2F;
      float rO = 0.2F;
      float targetR;
      float smooth;
      int lifetime;
      int age = 0;

      public Wave(float targetR, float smooth, int lft) {
         this.targetR = targetR;
         this.smooth = smooth;
         this.lifetime = lft;
      }

      public void tick() {
         this.rO = this.r;
         this.r = Mth.m_14179_(this.smooth, this.r, this.targetR);
         this.age++;
      }

      public boolean isEnd() {
         return this.age >= this.lifetime;
      }

      public void pushParticle(ClientLevel level, double x, double y, double z, RandomSource random) {
         int inter = Math.max(1, (int)((this.r - this.rO) / 0.2F));
         float perR = (this.r - this.rO) / (float)inter;
         float perYAdder = 0.5F / (float)inter;

         for (int j = 0; j < inter; j++) {
            int cnt = Math.max(8, (int)(Math.PI * (double)(this.rO + perR * (float)j) * 2.0 / 0.2));
            double perAng = (Math.PI * 2) / (double)cnt;

            for (int i = 0; i < cnt; i++) {
               double x_ = Math.cos(perAng * (double)i) * (double)(this.rO + perR * (float)j);
               double z_ = Math.sin(perAng * (double)i) * (double)(this.rO + perR * (float)j);
               RenderUtils.AddParticle(
                  level,
                  new AirWaveParticle.AirParticle(
                     level,
                     x_ + x + Mth.m_216263_(random, -0.2, 0.2),
                     y + Mth.m_216263_(random, -0.2, 0.2) + (double)(perYAdder * (float)j),
                     z_ + z + Mth.m_216263_(random, -0.2, 0.2),
                     Mth.m_216263_(random, -0.05, 0.05),
                     0.25,
                     Mth.m_216263_(random, -0.05, 0.05),
                     (int)(6.0F / (float)inter * (float)(j + 1))
                  )
               );
            }
         }
      }
   }
}
