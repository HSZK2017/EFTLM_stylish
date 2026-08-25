package com.dmc.invincible_dmc.client.effeks;

import com.guhao.vix.particles.AAAEffekParticle;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SDT1ChargeEffek {
   public static final ResourceLocation SDT1_EFFEK = new ResourceLocation("invincible_dmc", "sdt1");

   public static void playSDT1Charge(SDT1ChargeEffek.Type type, Level level, double x, double y, double z, float radius, Entity entity) {
      if (EffekConfig.isEnabled("sdt1_charge")) {
         ParticleEmitterInfo info = ParticleEmitterInfo.create(level, type.effekId())
            .position(x, y, z)
            .bindOnEntity(entity)
            .scale(radius / type.intrinsicRadius());
         AAALevel.addParticle(level, true, info);
      }
   }

   public static Particle createParticleWrapper(
      SDT1ChargeEffek.Type type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, float radius
   ) {
      float scale = radius / type.intrinsicRadius();
      AAAEffekParticle particle = new AAAEffekParticle(level, type.effekId(), x, y, z, dx, dy, dz);
      if (particle.getEmitter().isPresent()) {
         ((ParticleEmitter)particle.getEmitter().get()).setScale(scale, scale, scale);
      }

      return particle;
   }

   public static record Type(ResourceLocation effekId, float intrinsicRadius) {
      public static final SDT1ChargeEffek.Type LEVEL1 = new SDT1ChargeEffek.Type(SDT1ChargeEffek.SDT1_EFFEK, 1.0F);
   }
}
