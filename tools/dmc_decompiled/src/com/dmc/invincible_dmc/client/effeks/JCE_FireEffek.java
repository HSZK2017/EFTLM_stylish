package com.dmc.invincible_dmc.client.effeks;

import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JCE_FireEffek {
   public static final ResourceLocation JCE_FIRE1_EFFEK = new ResourceLocation("invincible_dmc", "jce_fire");

   public static void playJCE_Fire(JCE_FireEffek.Type type, Level level, double x, double y, double z, float radius, Entity entity) {
      if (EffekConfig.isEnabled("jce_fire")) {
         ParticleEmitterInfo info = ParticleEmitterInfo.create(level, type.effekId())
            .position(x, y, z)
            .bindOnEntity(entity)
            .scale(radius / type.intrinsicRadius());
         AAALevel.addParticle(level, true, info);
      }
   }

   public static record Type(ResourceLocation effekId, float intrinsicRadius) {
      public static final JCE_FireEffek.Type LEVEL1 = new JCE_FireEffek.Type(JCE_FireEffek.JCE_FIRE1_EFFEK, 1.0F);
   }
}
