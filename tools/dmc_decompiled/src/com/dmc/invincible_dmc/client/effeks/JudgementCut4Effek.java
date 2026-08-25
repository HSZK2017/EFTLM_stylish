package com.dmc.invincible_dmc.client.effeks;

import com.guhao.vix.particles.AAAEffekParticle;
import java.util.Random;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JudgementCut4Effek {
   public static final ResourceLocation JCEFFEK = new ResourceLocation("invincible_dmc", "judgement_cut_4");

   public static void playJC(JudgementCut4Effek.Type type, Level level, double x, double y, double z, float radius) {
      if (EffekConfig.isEnabled("judgement_cut")) {
         Random random = new Random();
         ParticleEmitterInfo info = ParticleEmitterInfo.create(level, type.effekId())
            .position(x, y, z)
            .rotation(0.0F, random.nextFloat(-90.0F, 90.0F), 0.0F)
            .scale(radius / type.intrinsicRadius());
         AAALevel.addParticle(level, true, info);
      }
   }

   public static Particle createParticleWrapper(
      JudgementCut4Effek.Type type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, float radius
   ) {
      float scale = radius / type.intrinsicRadius();
      AAAEffekParticle particle = new AAAEffekParticle(level, type.effekId(), x, y, z, dx, dy, dz);
      if (particle.getEmitter().isPresent()) {
         ((ParticleEmitter)particle.getEmitter().get()).setScale(scale, scale, scale);
      }

      return particle;
   }

   public static record Type(ResourceLocation effekId, float intrinsicRadius) {
      public static final JudgementCut4Effek.Type LEVEL1 = new JudgementCut4Effek.Type(JudgementCut4Effek.JCEFFEK, 1.0F);
      public static final JudgementCut4Effek.Type LEVEL2 = new JudgementCut4Effek.Type(JudgementCut4Effek.JCEFFEK, 0.8F);
      public static final JudgementCut4Effek.Type LEVEL3 = new JudgementCut4Effek.Type(JudgementCut4Effek.JCEFFEK, 0.36F);
   }
}
