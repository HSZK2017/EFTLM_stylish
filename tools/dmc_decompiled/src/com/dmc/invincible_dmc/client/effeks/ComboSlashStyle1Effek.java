package com.dmc.invincible_dmc.client.effeks;

import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ComboSlashStyle1Effek {
   public static final ResourceLocation SLASHEFFEK = new ResourceLocation("invincible_dmc", "combo_slash_style1");

   public static void playSlash(ComboSlashStyle1Effek.Type type, Level level, double x, double y, double z, float rx, float ry, float rz, float radius) {
      if (EffekConfig.isEnabled("tier1plus_slash")) {
         ParticleEmitterInfo info = ParticleEmitterInfo.create(level, type.effekId())
            .position(x, y, z)
            .rotation(rx, ry, rz)
            .scale(radius / type.intrinsicRadius());
         AAALevel.addParticle(level, true, info);
      }
   }

   public static record Type(ResourceLocation effekId, float intrinsicRadius) {
      public static final ComboSlashStyle1Effek.Type LEVEL1 = new ComboSlashStyle1Effek.Type(ComboSlashStyle1Effek.SLASHEFFEK, 1.0F);
   }
}
