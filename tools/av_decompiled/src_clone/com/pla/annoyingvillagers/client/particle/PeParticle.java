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

@OnlyIn(Dist.CLIENT)
public class PeParticle extends TextureSheetParticle {
   private final SpriteSet spriteSet;

   public static PeParticle.PeParticleProvider provider(SpriteSet spriteset) {
      return new PeParticle.PeParticleProvider(spriteset);
   }

   protected PeParticle(ClientLevel clientlevel, double d0, double d1, double d2, double d3, double d4, double d5, SpriteSet spriteset) {
      super(clientlevel, d0, d1, d2);
      this.spriteSet = spriteset;
      this.m_107250_(0.3F, 0.3F);
      this.f_107663_ *= 1.7F;
      this.f_107225_ = Math.max(1, 60 + (this.f_107223_.m_188503_(10) - 5));
      this.f_107226_ = 0.0F;
      this.f_107219_ = false;
      this.f_107215_ = d3 * 0.0;
      this.f_107216_ = d4 * 0.0;
      this.f_107217_ = d5 * 0.0;
      this.m_108339_(spriteset);
   }

   public int m_6355_(float f) {
      return 15728880;
   }

   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107432_;
   }

   public void m_5989_() {
      super.m_5989_();
      if (!this.f_107220_) {
         this.m_108337_(this.spriteSet.m_5819_(this.f_107224_ / 1 % 27 + 1, 27));
      }
   }

   public static class PeParticleProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public PeParticleProvider(SpriteSet spriteset) {
         this.spriteSet = spriteset;
      }

      public Particle createParticle(
         SimpleParticleType simpleparticletype, ClientLevel clientlevel, double d0, double d1, double d2, double d3, double d4, double d5
      ) {
         return new PeParticle(clientlevel, d0, d1, d2, d3, d4, d5, this.spriteSet);
      }
   }
}
