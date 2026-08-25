package com.dmc.invincible_dmc.client.effeks;

import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SDT_Fire2Effek {
   public static final ResourceLocation SDT_FIRE2_EFFEK = new ResourceLocation("invincible_dmc", "sdt_fire2");

   public static void playSDT_Fire2(SDT_Fire2Effek.Type type, Level level, double x, double y, double z, float radius) {
      if (EffekConfig.isEnabled("sdt_fire2")) {
         ParticleEmitterInfo info = ParticleEmitterInfo.create(level, type.effekId()).position(x, y, z).scale(radius / type.intrinsicRadius());
         AAALevel.addParticle(level, true, info);
      }
   }

   public static record Type(ResourceLocation effekId, float intrinsicRadius) {
      public static final SDT_Fire2Effek.Type LEVEL1 = new SDT_Fire2Effek.Type(SDT_Fire2Effek.SDT_FIRE2_EFFEK, 1.0F);
   }
}
