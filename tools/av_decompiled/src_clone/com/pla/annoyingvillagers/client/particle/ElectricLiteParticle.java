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
public class ElectricLiteParticle extends TextureSheetParticle {
   private final SpriteSet spriteSet;

   public static ElectricLiteParticle.ElectricLiteParticleProvider provider(SpriteSet spriteSet) {
      return new ElectricLiteParticle.ElectricLiteParticleProvider(spriteSet);
   }

   protected ElectricLiteParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
      super(world, x, y, z);
      this.spriteSet = spriteSet;
      this.m_107250_(0.05F, 0.07F);
      this.f_107225_ = 8;
      this.f_107226_ = 0.2F * (world.f_46441_.m_188501_() - 0.5F);
      this.f_107219_ = true;
      this.f_107215_ = vx * 1.0;
      this.f_107216_ = vy * 1.0;
      this.f_107217_ = vz * 1.0;
      this.m_108339_(spriteSet);
   }

   public int m_6355_(float partialTick) {
      return 15728880;
   }

   @NotNull
   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107432_;
   }

   public void m_5989_() {
      super.m_5989_();
      if (!this.f_107220_) {
         this.m_108337_(this.spriteSet.m_5819_((int)((float)this.f_107224_ / 8.0F * 6.0F) % 6, 6));
      }
   }

   public static class ElectricLiteParticleProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public ElectricLiteParticleProvider(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new SparkParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
      }
   }
}
