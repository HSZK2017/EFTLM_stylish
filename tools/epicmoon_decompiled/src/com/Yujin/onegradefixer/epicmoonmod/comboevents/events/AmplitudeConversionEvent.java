package com.Yujin.onegradefixer.epicmoonmod.comboevents.events;

import com.Yujin.onegradefixer.epicmoonmod.effect.EMeffects;
import com.p1nero.invincible.api.events.HitEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class AmplitudeConversionEvent {
   public static HitEvent amplitudeconversion() {
      return new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
         if (entity instanceof LivingEntity) {
            MobEffectInstance effect = ((LivingEntity)entity).m_21124_((MobEffect)EMeffects.TREMOR.get());
            MobEffectInstance effect2 = ((LivingEntity)entity).m_21124_((MobEffect)EMeffects.TREMOR_SCORCH.get());
            if (effect2 != null) {
               int duration2 = effect2.m_19557_();
               ((LivingEntity)entity).m_7292_(new MobEffectInstance((MobEffect)EMeffects.TREMOR_SCORCH.get(), duration2 + 50, 0, true, false));
            } else if (effect != null) {
               int duration = effect.m_19557_();
               ((LivingEntity)entity).m_7292_(new MobEffectInstance((MobEffect)EMeffects.TREMOR_SCORCH.get(), duration, 0, true, false));
               ((LivingEntity)entity).m_21195_((MobEffect)EMeffects.TREMOR.get());
            } else {
               ((LivingEntity)entity).m_7292_(new MobEffectInstance((MobEffect)EMeffects.TREMOR.get(), 50, 0, true, false));
            }
         }
      });
   }
}
