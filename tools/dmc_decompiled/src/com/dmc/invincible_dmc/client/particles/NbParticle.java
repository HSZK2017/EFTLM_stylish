package com.dmc.invincible_dmc.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public final class NbParticle extends NoRenderParticle {
   private NbEffectShowcase showcase;

   private NbParticle(ClientLevel level, double x, double y, double z) {
      super(level, x, y, z);
   }

   public void m_5989_() {
      if (this.showcase == null) {
         this.showcase = NbEffectShowcase.create(this.f_107208_, new Vec3(this.f_107212_, this.f_107213_, this.f_107214_));
      }

      if (!this.showcase.tick()) {
         this.m_107274_();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static final class Provider implements ParticleProvider<SimpleParticleType> {
      public Particle createParticle(
         @NotNull SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
      ) {
         return new NbParticle(level, x, y, z);
      }
   }
}
