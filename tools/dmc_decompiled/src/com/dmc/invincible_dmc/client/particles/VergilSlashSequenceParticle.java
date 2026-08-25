package com.dmc.invincible_dmc.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public final class VergilSlashSequenceParticle extends AbstractVergilSlashSequenceParticle {
   private static final int FRAME_COUNT = 22;
   private static SpriteSet sprites;

   private VergilSlashSequenceParticle(ClientLevel level, double x, double y, double z, float rollX, float rollY, float rollZ, float size, SpriteSet sprites) {
      super(level, x, y, z, rollX, rollY, rollZ, size, 22, sprites);
   }

   public static Particle create(ClientLevel level, double x, double y, double z, float rollX, float rollY, float rollZ, float size) {
      SpriteSet currentSprites = sprites;
      return currentSprites == null ? null : new VergilSlashSequenceParticle(level, x, y, z, rollX, rollY, rollZ, size, currentSprites);
   }

   public static final class Provider implements ParticleProvider<SimpleParticleType> {
      public Provider(SpriteSet sprites) {
         VergilSlashSequenceParticle.sprites = sprites;
      }

      public Particle createParticle(
         SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
      ) {
         return new VergilSlashSequenceParticle(level, x, y, z, (float)xSpeed, (float)ySpeed, (float)zSpeed, 3.0F, VergilSlashSequenceParticle.sprites);
      }
   }
}
