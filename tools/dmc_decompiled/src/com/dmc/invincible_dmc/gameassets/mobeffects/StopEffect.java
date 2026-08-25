package com.dmc.invincible_dmc.gameassets.mobeffects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class StopEffect extends MobEffect {
   public StopEffect() {
      super(MobEffectCategory.NEUTRAL, 16777215);
   }

   public boolean m_6584_(int duration, int lv) {
      return true;
   }

   public void m_6742_(LivingEntity owner, int lv) {
      owner.m_20256_(Vec3.f_82478_);
      owner.m_6034_(owner.f_19790_, owner.f_19791_, owner.f_19792_);
      owner.m_7910_(0.0F);
   }
}
