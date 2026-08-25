package com.dmc.invincible_dmc.client.effeks;

import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SlowSlashDisorderEffek {
   public static final ResourceLocation SLASHEFFEK = new ResourceLocation("invincible_dmc", "slow_slash_disorder");

   public static void playSlash(SlowSlashDisorderEffek.Type type, Level level, double x, double y, double z, float rx, float ry, float rz, float radius) {
      if (EffekConfig.isEnabled("tier0_slash")) {
         ParticleEmitterInfo info = ParticleEmitterInfo.create(level, type.effekId())
            .position(x, y, z)
            .rotation(rx, ry, rz)
            .scale(radius / type.intrinsicRadius());
         AAALevel.addParticle(level, true, info);
      }
   }

   public static record Type(ResourceLocation effekId, float intrinsicRadius) {
      public static final SlowSlashDisorderEffek.Type LEVEL1 = new SlowSlashDisorderEffek.Type(SlowSlashDisorderEffek.SLASHEFFEK, 1.0F);
   }
}
