package com.dmc.invincible_dmc.client.effeks;

import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Door2Effek {
   public static final ResourceLocation DOOREFFEK = new ResourceLocation("invincible_dmc", "door2");

   public static void playDoor(Door2Effek.Type type, Level level, double x, double y, double z, float rx, float ry, float rz, float radius) {
      if (EffekConfig.isEnabled("door")) {
         ParticleEmitterInfo info = ParticleEmitterInfo.create(level, type.effekId())
            .position(x, y, z)
            .rotation(rx, ry, rz)
            .scale(radius / type.intrinsicRadius());
         AAALevel.addParticle(level, true, info);
      }
   }

   public static record Type(ResourceLocation effekId, float intrinsicRadius) {
      public static final Door2Effek.Type LEVEL1 = new Door2Effek.Type(Door2Effek.DOOREFFEK, 1.0F);
   }
}
