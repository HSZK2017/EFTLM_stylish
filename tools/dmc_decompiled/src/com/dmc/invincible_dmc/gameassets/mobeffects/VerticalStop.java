package com.dmc.invincible_dmc.gameassets.mobeffects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class VerticalStop extends MobEffect {
   public VerticalStop() {
      super(MobEffectCategory.NEUTRAL, 16777215);
   }

   public boolean m_6584_(int duration, int amplifier) {
      return true;
   }

   public void m_6742_(LivingEntity entity, int amplifier) {
      Vec3 currentMotion = entity.m_20184_();
      Vec3 newMotion = new Vec3(currentMotion.m_7096_(), 0.0, currentMotion.m_7094_());
      entity.m_20256_(newMotion);
      entity.m_6034_(entity.m_20185_(), entity.f_19791_, entity.m_20189_());
   }
}
