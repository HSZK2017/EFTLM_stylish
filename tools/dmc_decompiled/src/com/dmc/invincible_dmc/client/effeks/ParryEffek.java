package com.dmc.invincible_dmc.client.effeks;

import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParryEffek {
   public static final ResourceLocation PARRYEFFEK = new ResourceLocation("invincible_dmc", "parry");

   public static void playParry(ParryEffek.Type type, Level level, double x, double y, double z, float radius) {
      if (EffekConfig.isEnabled("parry")) {
         ParticleEmitterInfo info = ParticleEmitterInfo.create(level, type.effekId()).position(x, y, z).scale(radius / type.intrinsicRadius());
         AAALevel.addParticle(level, true, info);
      }
   }

   public static record Type(ResourceLocation effekId, float intrinsicRadius) {
      public static final ParryEffek.Type LEVEL1 = new ParryEffek.Type(ParryEffek.PARRYEFFEK, 1.0F);
   }
}
