package com.dmc.invincible_dmc.client.effeks;

import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GroundEffek {
   public static final ResourceLocation GROUND_EFFEK = new ResourceLocation("invincible_dmc", "yamato_ground");

   public static void playGround(GroundEffek.Type type, Level level, double x, double y, double z, float radius) {
      if (EffekConfig.isEnabled("ground")) {
         ParticleEmitterInfo info = ParticleEmitterInfo.create(level, type.effekId()).position(x, y, z).scale(radius / type.intrinsicRadius());
         AAALevel.addParticle(level, true, info);
      }
   }

   public static record Type(ResourceLocation effekId, float intrinsicRadius) {
      public static final GroundEffek.Type LEVEL1 = new GroundEffek.Type(GroundEffek.GROUND_EFFEK, 1.0F);
   }
}
