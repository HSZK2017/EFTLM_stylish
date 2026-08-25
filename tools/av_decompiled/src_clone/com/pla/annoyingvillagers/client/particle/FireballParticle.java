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

public class FireballParticle extends TextureSheetParticle {
   private final SpriteSet sprites;
   boolean important;

   public static FireballParticle.FireballParticleProvider provider(SpriteSet spriteset) {
      return new FireballParticle.FireballParticleProvider(spriteset);
   }

   FireballParticle(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider, double velX, double velY, double velZ) {
      super(world, x, y, z);
      this.sprites = spriteProvider;
      this.f_107225_ = (int)(9.0 + Math.floor(velX / 5.0));
      this.f_107663_ = (float)velX;
      this.important = velY == 1.0;
      this.m_172260_(0.0, 0.0, 0.0);
      this.m_108339_(spriteProvider);
   }

   public void m_5989_() {
      this.f_107209_ = this.f_107212_;
      this.f_107210_ = this.f_107213_;
      this.f_107211_ = this.f_107214_;
      if (this.f_107224_++ >= this.f_107225_) {
         this.m_107274_();
      } else {
         this.f_107216_ = this.f_107216_ - (double)this.f_107226_;
         this.m_6257_(this.f_107215_, this.f_107216_, this.f_107217_);
         this.m_108339_(this.sprites);
      }
   }

   protected int m_6355_(float pPartialTick) {
      return 15728880;
   }

   @NotNull
   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107431_;
   }

   @OnlyIn(Dist.CLIENT)
   public static class FireballParticleProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public FireballParticleProvider(SpriteSet spriteSet) {
         this.sprites = spriteSet;
      }

      public Particle createParticle(
         @NotNull SimpleParticleType particleType, @NotNull ClientLevel level, double x, double y, double z, double dx, double dy, double dz
      ) {
         return new FireballParticle(level, x, y, z, this.sprites, dx, dy, dz);
      }
   }
}
