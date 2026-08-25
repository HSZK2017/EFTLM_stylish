package com.dmc.invincible_dmc.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public final class NullParticle extends NoRenderParticle {
   private NullParticle(ClientLevel level, double x, double y, double z) {
      super(level, x, y, z);
   }

   @OnlyIn(Dist.CLIENT)
   public static final class Provider implements ParticleProvider<SimpleParticleType> {
      public Particle createParticle(
         @NotNull SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
      ) {
         return new NullParticle(level, x, y, z);
      }
   }
}
