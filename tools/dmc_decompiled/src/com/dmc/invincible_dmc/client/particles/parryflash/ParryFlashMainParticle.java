package com.dmc.invincible_dmc.client.particles.parryflash;

import com.dmc.invincible_dmc.client.effeks.ParryEffek;
import com.dmc.invincible_dmc.particle.DMCParticles;
import java.util.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParryFlashMainParticle extends NoRenderParticle {
   public ParryFlashMainParticle(ClientLevel world, double x, double y, double z, double sizeScale, double rotationBias, double _null) {
      super(world, x, y, z);
      double baseWidth = 0.95 * sizeScale;
      double baseHeight = 0.55 * sizeScale;
      this.f_107212_ = x + (this.f_107223_.m_188500_() - 0.5) * baseWidth;
      this.f_107213_ = y + (this.f_107223_.m_188500_() - 0.5) * baseHeight;
      this.f_107214_ = z + (this.f_107223_.m_188500_() - 0.5) * baseWidth;
      this.f_107208_
         .m_7106_((ParticleOptions)DMCParticles.PARRY_FLASH_MAIN_RENDER.get(), this.f_107212_, this.f_107213_, this.f_107214_, sizeScale, rotationBias, 0.0);
      Random r = new Random();
      ParryEffek.playParry(ParryEffek.Type.LEVEL1, this.f_107208_, x, y, z, r.nextFloat(0.36F, 0.56F));
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ParryFlashMainParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
      }
   }
}
