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
public class EnderParticle extends TextureSheetParticle {
   private final SpriteSet spriteSet;

   public static EnderParticle.EnderParticleProvider provider(SpriteSet spriteset) {
      return new EnderParticle.EnderParticleProvider(spriteset);
   }

   protected EnderParticle(ClientLevel clientlevel, double d0, double d1, double d2, double d3, double d4, double d5, SpriteSet spriteset) {
      super(clientlevel, d0, d1, d2);
      this.spriteSet = spriteset;
      this.m_107250_(0.4F, 0.4F);
      this.f_107663_ *= 0.7F;
      this.f_107225_ = Math.max(1, 20 + (this.f_107223_.m_188503_(12) - 6));
      this.f_107226_ = -0.1F;
      this.f_107219_ = false;
      this.f_107215_ = d3;
      this.f_107216_ = d4;
      this.f_107217_ = d5;
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
      if (!this.f_107220_) {
         this.m_108337_(this.spriteSet.m_5819_(this.f_107224_ % 8 + 1, 8));
      }
   }

   public static class EnderParticleProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public EnderParticleProvider(SpriteSet spriteset) {
         this.spriteSet = spriteset;
      }

      public Particle createParticle(
         @NotNull SimpleParticleType simpleparticletype, @NotNull ClientLevel clientlevel, double d0, double d1, double d2, double d3, double d4, double d5
      ) {
         return new EnderParticle(clientlevel, d0, d1, d2, d3, d4, d5, this.spriteSet);
      }
   }
}
