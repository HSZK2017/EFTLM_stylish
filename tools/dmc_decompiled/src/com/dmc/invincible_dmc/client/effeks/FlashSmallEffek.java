package com.dmc.invincible_dmc.client.effeks;

import com.guhao.vix.particles.AAAEffekParticle;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FlashSmallEffek {
   public static final ResourceLocation FLASHEFFEK = new ResourceLocation("invincible_dmc", "flash_small");

   public static void playFlashSmall(FlashSmallEffek.Type type, Level level, double x, double y, double z, float radius) {
      if (EffekConfig.isEnabled("flash_small")) {
         ParticleEmitterInfo info = ParticleEmitterInfo.create(level, type.effekId()).position(x, y, z).scale(radius / type.intrinsicRadius());
         AAALevel.addParticle(level, true, info);
      }
   }

   public static Particle createParticleWrapper(
      FlashSmallEffek.Type type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, float radius
   ) {
      float scale = radius / type.intrinsicRadius();
      AAAEffekParticle particle = new AAAEffekParticle(level, type.effekId(), x, y, z, dx, dy, dz);
      if (particle.getEmitter().isPresent()) {
         ((ParticleEmitter)particle.getEmitter().get()).setScale(scale, scale, scale);
      }

      return particle;
   }

   public static record Type(ResourceLocation effekId, float intrinsicRadius) {
      public static final FlashSmallEffek.Type LEVEL1 = new FlashSmallEffek.Type(FlashSmallEffek.FLASHEFFEK, 1.0F);
      public static final FlashSmallEffek.Type LEVEL2 = new FlashSmallEffek.Type(FlashSmallEffek.FLASHEFFEK, 0.8F);
      public static final FlashSmallEffek.Type LEVEL3 = new FlashSmallEffek.Type(FlashSmallEffek.FLASHEFFEK, 0.36F);
   }
}
