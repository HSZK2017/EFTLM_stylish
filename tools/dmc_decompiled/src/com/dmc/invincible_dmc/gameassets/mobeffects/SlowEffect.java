package com.dmc.invincible_dmc.gameassets.mobeffects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class SlowEffect extends MobEffect {
   public SlowEffect() {
      super(MobEffectCategory.NEUTRAL, 16777215);
   }

   public boolean m_6584_(int duration, int lv) {
      return true;
   }
}
