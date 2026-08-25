package com.dmc.invincible_dmc.client.effeks;

import com.dmc.invincible_dmc.DMConfig;
import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FastSlashEffek {
   public static final ResourceLocation SLASHEFFEK = new ResourceLocation("invincible_dmc", "fast_slash");

   public static void playSlash(FastSlashEffek.Type type, Level level, double x, double y, double z, float rx, float ry, float rz, float radius) {
      if (EffekConfig.isEnabled("tier0_slash")) {
         ParticleEmitterInfo info = ParticleEmitterInfo.create(level, type.effekId())
            .position(x, y, z)
            .rotation(rx, ry, rz)
            .scale(radius / type.intrinsicRadius());
         AAALevel.addParticle(level, true, info);
         FlashPointEffek.playFlash(FlashPointEffek.Type.LEVEL1, level, x, y, z, ((Double)DMConfig.FLASH_POINT_SCALE_FACTOR.get()).floatValue());
      }
   }

   public static record Type(ResourceLocation effekId, float intrinsicRadius) {
      public static final FastSlashEffek.Type LEVEL1 = new FastSlashEffek.Type(FastSlashEffek.SLASHEFFEK, 1.0F);
   }
}
