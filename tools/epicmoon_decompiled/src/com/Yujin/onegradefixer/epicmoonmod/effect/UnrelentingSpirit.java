package com.Yujin.onegradefixer.epicmoonmod.effect;

import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class UnrelentingSpirit extends MobEffect {
   public static final ResourceLocation a = new ResourceLocation("epicmoonmod", "goki");

   public UnrelentingSpirit(MobEffectCategory pCategory, int pColor) {
      super(pCategory, pColor);
   }

   public void m_6742_(LivingEntity pLivingEntity, int pAmplifier) {
      if (pLivingEntity.m_9236_().f_46443_) {
         Minecraft mc = Minecraft.m_91087_();
         if (mc.f_91074_ == null || pLivingEntity != mc.f_91074_ || !mc.f_91066_.m_92176_().m_90612_()) {
            ParticleEmitterInfo info = ParticleEmitterInfo.create(pLivingEntity.m_9236_(), a)
               .position(pLivingEntity.m_20185_(), pLivingEntity.m_20186_(), pLivingEntity.m_20189_())
               .rotation(0.0F, -((float)Math.toRadians((double)pLivingEntity.m_6080_())), 0.0F);
            AAALevel.addParticle(pLivingEntity.m_9236_(), info);
         }
      }
   }

   public boolean m_6584_(int p_19455_, int p_19456_) {
      return true;
   }
}
