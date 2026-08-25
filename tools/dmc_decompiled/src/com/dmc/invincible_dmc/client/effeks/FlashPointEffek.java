package com.dmc.invincible_dmc.client.effeks;

import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FlashPointEffek {
   public static final ResourceLocation FLASHPOINTEFFEK = new ResourceLocation("invincible_dmc", "flash_point");

   public static void playFlash(FlashPointEffek.Type type, Level level, double x, double y, double z, float radius) {
      if (EffekConfig.isEnabled("flash_point")) {
         ParticleEmitterInfo info = ParticleEmitterInfo.create(level, type.effekId()).position(x, y, z).scale(radius / type.intrinsicRadius());
         AAALevel.addParticle(level, true, info);
      }
   }

   public static record Type(ResourceLocation effekId, float intrinsicRadius) {
      public static final FlashPointEffek.Type LEVEL1 = new FlashPointEffek.Type(FlashPointEffek.FLASHPOINTEFFEK, 1.0F);
      public static final FlashPointEffek.Type LEVEL2 = new FlashPointEffek.Type(FlashPointEffek.FLASHPOINTEFFEK, 0.8F);
      public static final FlashPointEffek.Type LEVEL3 = new FlashPointEffek.Type(FlashPointEffek.FLASHPOINTEFFEK, 0.36F);
   }
}
