package com.dmc.invincible_dmc.client.effeks;

import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SDT_Fire1Effek {
   public static final ResourceLocation SDT_FIRE1_EFFEK = new ResourceLocation("invincible_dmc", "sdt_fire");

   public static void playSDT_Fire1(SDT_Fire1Effek.Type type, Level level, double x, double y, double z, float radius, Entity entity) {
      if (EffekConfig.isEnabled("sdt_fire1")) {
         ParticleEmitterInfo info = ParticleEmitterInfo.create(level, type.effekId())
            .position(x, y, z)
            .bindOnEntity(entity)
            .scale(radius / type.intrinsicRadius());
         AAALevel.addParticle(level, true, info);
      }
   }

   public static record Type(ResourceLocation effekId, float intrinsicRadius) {
      public static final SDT_Fire1Effek.Type LEVEL1 = new SDT_Fire1Effek.Type(SDT_Fire1Effek.SDT_FIRE1_EFFEK, 1.0F);
   }
}
