package com.Yujin.onegradefixer.epicmoonmod.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class TremorScorch extends MobEffect {
   public TremorScorch(MobEffectCategory pCategory, int pColor) {
      super(pCategory, pColor);
   }

   public void m_6742_(LivingEntity pLivingEntity, int pAmplifier) {
      super.m_6742_(pLivingEntity, pAmplifier);
   }

   public boolean m_6584_(int p_19455_, int p_19456_) {
      return true;
   }
}
