package com.dmc.invincible_dmc.client.effeks;

import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LightRingEffek {
   public static final ResourceLocation LIGHTRINGEFFEK = new ResourceLocation("invincible_dmc", "light_ring");

   public static void playLightRing(LightRingEffek.Type type, Level level, double x, double y, double z, float radius) {
      if (EffekConfig.isEnabled("light_ring")) {
         ParticleEmitterInfo info = ParticleEmitterInfo.create(level, type.effekId()).position(x, y, z).scale(radius / type.intrinsicRadius());
         AAALevel.addParticle(level, true, info);
      }
   }

   public static record Type(ResourceLocation effekId, float intrinsicRadius) {
      public static final LightRingEffek.Type LEVEL1 = new LightRingEffek.Type(LightRingEffek.LIGHTRINGEFFEK, 1.0F);
   }
}
