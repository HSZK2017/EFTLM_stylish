package com.pla.annoyingvillagers.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class BigSplashParticle extends TextureSheetParticle {
   private final SpriteSet spriteSet;

   public static BigSplashParticle.BigSplashParticleProvider provider(SpriteSet spriteset) {
      return new BigSplashParticle.BigSplashParticleProvider(spriteset);
   }

   protected BigSplashParticle(ClientLevel clientlevel, double d0, double d1, double d2, double d3, double d4, double d5, SpriteSet spriteset) {
      super(clientlevel, d0, d1, d2);
      this.spriteSet = spriteset;
      this.m_107250_(0.2F, 0.2F);
      this.f_107225_ = Math.max(1, 30 + (this.f_107223_.m_188503_(40) - 20));
      this.f_107226_ = 0.7F;
      this.f_107219_ = false;
      this.f_107215_ = d3;
      this.f_107216_ = d4;
      this.f_107217_ = d5;
      this.m_108339_(spriteset);
   }

   @NotNull
   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107431_;
   }

   public float m_5902_(float f) {
      return (float)((double)(super.m_5902_(f) * 19.0F) + Math.sin((double)this.f_107224_ * 0.2) * 2.0);
   }

   public void m_5989_() {
      super.m_5989_();
      if (!this.f_107220_) {
         this.m_108337_(this.spriteSet.m_5819_(this.f_107224_ / 6 % 11 + 1, 11));
      }
   }

   public static class BigSplashParticleProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public BigSplashParticleProvider(SpriteSet spriteset) {
         this.spriteSet = spriteset;
      }

      public Particle createParticle(
         @NotNull SimpleParticleType simpleParticleType, @NotNull ClientLevel clientLevel, double d0, double d1, double d2, double d3, double d4, double d5
      ) {
         return new BigSplashParticle(clientLevel, d0, d1, d2, d3, d4, d5, this.spriteSet);
      }
   }
}
