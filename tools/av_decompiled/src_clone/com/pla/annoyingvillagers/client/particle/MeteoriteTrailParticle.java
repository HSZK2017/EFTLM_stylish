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
public class MeteoriteTrailParticle extends TextureSheetParticle {
   private final SpriteSet spriteSet;
   private float angularVelocity;
   private final float angularAcceleration;

   public static MeteoriteTrailParticle.MeteoriteTrailParticleProvider provider(SpriteSet spriteset) {
      return new MeteoriteTrailParticle.MeteoriteTrailParticleProvider(spriteset);
   }

   protected MeteoriteTrailParticle(ClientLevel clientlevel, double d0, double d1, double d2, double d3, double d4, double d5, SpriteSet spriteset) {
      super(clientlevel, d0, d1, d2);
      this.spriteSet = spriteset;
      this.m_107250_(0.2F, 0.2F);
      this.f_107663_ *= 16.0F;
      this.f_107225_ = 15;
      this.f_107226_ = 0.0F;
      this.f_107219_ = false;
      this.f_107215_ = d3;
      this.f_107216_ = d4;
      this.f_107217_ = d5;
      this.angularVelocity = 0.0F;
      this.angularAcceleration = 0.03F;
      this.m_108339_(spriteset);
   }

   public int m_6355_(float f) {
      return 15728880;
   }

   @NotNull
   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107432_;
   }

   public void m_5989_() {
      super.m_5989_();
      this.f_107204_ = this.f_107231_;
      this.f_107231_ = this.f_107231_ + this.angularVelocity;
      this.angularVelocity = this.angularVelocity + this.angularAcceleration;
      if (!this.f_107220_) {
         this.m_108337_(this.spriteSet.m_5819_(this.f_107224_ / 2 % 8 + 1, 8));
      }
   }

   public static class MeteoriteTrailParticleProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public MeteoriteTrailParticleProvider(SpriteSet spriteset) {
         this.spriteSet = spriteset;
      }

      public Particle createParticle(
         @NotNull SimpleParticleType simpleParticleType, @NotNull ClientLevel clientLevel, double d0, double d1, double d2, double d3, double d4, double d5
      ) {
         return new MeteoriteTrailParticle(clientLevel, d0, d1, d2, d3, d4, d5, this.spriteSet);
      }
   }
}
