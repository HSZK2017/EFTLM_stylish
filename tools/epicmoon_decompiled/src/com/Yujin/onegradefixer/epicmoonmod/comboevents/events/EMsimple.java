package com.Yujin.onegradefixer.epicmoonmod.comboevents.events;

import com.Yujin.onegradefixer.epicmoonmod.util.skillparameter;
import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.animation.property.AnimationEvent.E0;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationEvent.SimpleEvent;

public class EMsimple {
   public static SimpleEvent<E0> clearSparkleTrail() {
      return SimpleEvent.create(
         (E0)(livingEntityPatch, animation, params) -> skillparameter.PREVIOUS_SPARK_POS.remove(((LivingEntity)livingEntityPatch.getOriginal()).m_20148_()),
         Side.CLIENT
      );
   }
}
