package com.dmc.invincible_dmc.client.particles;

import com.dmc.invincible_dmc.client.effeks.AttackEffek;
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
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.particle.EpicFightParticles;

@OnlyIn(Dist.CLIENT)
public class AttackMainParticle extends NoRenderParticle {
   public AttackMainParticle(ClientLevel world, double x, double y, double z, double sizeScale, double rotationBias) {
      super(world, x, y, z);
      double baseWidth = 0.95 * sizeScale;
      double baseHeight = 0.55 * sizeScale;
      this.f_107212_ = x + (this.f_107223_.m_188500_() - 0.5) * baseWidth;
      this.f_107213_ = y + (this.f_107223_.m_188500_() - 0.5) * baseHeight;
      this.f_107214_ = z + (this.f_107223_.m_188500_() - 0.5) * baseWidth;
      double d = 0.2F;
      this.f_107208_
         .m_7106_((ParticleOptions)DMCParticles.ATTACK_MAIN_RENDER.get(), this.f_107212_, this.f_107213_, this.f_107214_, sizeScale, rotationBias, 0.0);
      Random r = new Random();
      AttackEffek.playAttack(AttackEffek.Type.LEVEL1, this.f_107208_, x, y, z, r.nextFloat(0.3F, 0.5F));

      for (int i = 0; i < 8; i++) {
         double particleMotionX = this.f_107223_.m_188500_() * d;
         d *= this.f_107223_.m_188499_() ? 1.0 : -1.0;
         double particleMotionZ = this.f_107223_.m_188500_() * d;
         d *= this.f_107223_.m_188499_() ? 1.0 : -1.0;
         this.f_107208_
            .m_7106_((ParticleOptions)EpicFightParticles.BLOOD.get(), this.f_107212_, this.f_107213_, this.f_107214_, particleMotionX, 0.0, particleMotionZ);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Particle createParticle(
         @NotNull SimpleParticleType typeIn, @NotNull ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
      ) {
         return new AttackMainParticle(worldIn, x, y, z, xSpeed, ySpeed);
      }
   }
}
