package com.dmc.invincible_dmc.client.effeks;

import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AttackEffek {
   public static final ResourceLocation ATTACKEFFEK = new ResourceLocation("invincible_dmc", "attack");

   public static void playAttack(AttackEffek.Type type, Level level, double x, double y, double z, float radius) {
      if (EffekConfig.isEnabled("attack")) {
         ParticleEmitterInfo info = ParticleEmitterInfo.create(level, type.effekId()).position(x, y, z).scale(radius / type.intrinsicRadius());
         AAALevel.addParticle(level, true, info);
      }
   }

   public static record Type(ResourceLocation effekId, float intrinsicRadius) {
      public static final AttackEffek.Type LEVEL1 = new AttackEffek.Type(AttackEffek.ATTACKEFFEK, 1.0F);
   }
}
