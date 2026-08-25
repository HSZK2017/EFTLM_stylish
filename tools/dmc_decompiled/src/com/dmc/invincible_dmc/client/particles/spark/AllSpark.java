package com.dmc.invincible_dmc.client.particles.spark;

import com.dmc.invincible_dmc.particle.DMCParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AllSpark extends NoRenderParticle {
   public AllSpark(ClientLevel world, double x, double y, double z) {
      super(world, x, y, z);

      for (int i = 0; i < 90; i++) {
         Vec3 direction = new Vec3(
               (this.f_107208_.f_46441_.m_188500_() - 0.5) * 2.0,
               (this.f_107208_.f_46441_.m_188500_() - 0.5) * 1.4,
               (this.f_107208_.f_46441_.m_188500_() - 0.5) * 2.0
            )
            .m_82541_();
         Vec3 pos = new Vec3(x, y, z).m_82549_(direction.m_82490_(0.12));
         Vec3 velocity = direction.m_82490_(0.05 + this.f_107208_.f_46441_.m_188500_() * 0.25 * 3.2);
         this.f_107208_
            .m_7106_(
               (ParticleOptions)DMCParticles.SPARK_EXPANSIVE.get(),
               pos.f_82479_,
               pos.f_82480_,
               pos.f_82481_,
               velocity.f_82479_,
               velocity.f_82480_,
               velocity.f_82481_
            );
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new AllSpark(worldIn, x, y, z);
      }
   }
}
