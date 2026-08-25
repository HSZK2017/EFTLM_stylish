package com.Yujin.onegradefixer.epicmoonmod.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class StarParticle extends TextureSheetParticle {
   private final int spriteIndex = 0;
   private final SpriteSet sprites;

   protected StarParticle(ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteSet sprites) {
      super(level, x, y, z, velocityX, velocityY, velocityZ);
      this.sprites = sprites;
      this.f_107215_ = velocityX * 0.1;
      this.f_107216_ = velocityY * 0.1;
      this.f_107217_ = velocityZ * 0.1;
      this.f_107225_ = 10 + this.f_107223_.m_188503_(4);
      this.f_107663_ = 0.12F;
      this.f_172258_ = 0.92F;
      this.f_107226_ = 0.0F;
      this.f_107227_ = 1.0F;
      this.f_107228_ = 1.0F;
      this.f_107229_ = 1.0F;
      this.f_107230_ = 1.0F;
      int roll = this.f_107223_.m_188503_(100);
      int spriteIndex;
      if (roll < 40) {
         spriteIndex = 3;
      } else if (roll < 80) {
         spriteIndex = 0;
      } else if (roll < 90) {
         spriteIndex = 1;
      } else {
         spriteIndex = 2;
      }

      this.m_108337_(sprites.m_5819_(spriteIndex, 4));
      switch (spriteIndex) {
         case 0:
            this.f_107663_ *= 0.3F;
            break;
         case 1:
            this.f_107663_ *= 1.0F;
            break;
         case 2:
            this.f_107663_ *= 1.0F;
            break;
         case 3:
            this.f_107663_ *= 0.3F;
      }
   }

   public void m_5989_() {
      super.m_5989_();
      float life = 1.0F - (float)this.f_107224_ / (float)this.f_107225_;
      this.f_107663_ *= 0.96F;
      this.f_107230_ = life;
   }

   public int m_6355_(float partialTick) {
      return 15728880;
   }

   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107431_;
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet sprites) {
         this.sprites = sprites;
      }

      public Particle createParticle(
         SimpleParticleType type, ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ
      ) {
         return new StarParticle(level, x, y, z, velocityX, velocityY, velocityZ, this.sprites);
      }
   }
}
