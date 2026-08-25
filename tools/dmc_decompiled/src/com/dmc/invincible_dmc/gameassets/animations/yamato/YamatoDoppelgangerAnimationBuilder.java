package com.dmc.invincible_dmc.gameassets.animations.yamato;

import com.dmc.invincible_dmc.capability.doppelganger.DoppelgangerBindingService;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.gameasset.Armatures;

final class YamatoDoppelgangerAnimationBuilder {
   private YamatoDoppelgangerAnimationBuilder() {
   }

   static void build(AnimationBuilder builder) {
      YamatoAnimations.YAMATO_SUMMON_DOPPELGANGER_GROUND = builder.nextAccessor(
         "biped/yamato/doppelganger/summon_doppelganger_ground",
         accessor -> (ActionAnimation)new ActionAnimation(0.1F, Float.MAX_VALUE, accessor, Armatures.BIPED)
               .newTimePair(0.5F, 1.1F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
               .newTimePair(0.0F, 2.1474836E9F)
               .addState(EntityState.ATTACK_RESULT, YamatoAnimations.INVINCIBLE_SOURCE_VALIDATOR)
               .addProperty(ActionAnimationProperty.MOVE_ON_LINK, false)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.95F, (livingEntityPatch, assetAccessor, animationParameters) -> {
                  if (livingEntityPatch.getOriginal() instanceof ServerPlayer sp) {
                     DoppelgangerBindingService.consumePendingSummon(sp);
                     SinDevilTriggerManager.applySdtBurstDamageSmall((LivingEntity)livingEntityPatch.getOriginal());
                  }
               }, Side.SERVER)})
      );
      YamatoAnimations.YAMATO_SUMMON_DOPPELGANGER_AIR = builder.nextAccessor(
         "biped/yamato/doppelganger/summon_doppelganger_air",
         accessor -> (ActionAnimation)new ActionAnimation(0.1F, 1.3F, accessor, Armatures.BIPED)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.MOVE_ON_LINK, false)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 1.3F}))
               .newTimePair(0.5F, 1.1F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
               .newTimePair(0.0F, 2.1474836E9F)
               .addState(EntityState.ATTACK_RESULT, YamatoAnimations.INVINCIBLE_SOURCE_VALIDATOR)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.85F, (livingEntityPatch, assetAccessor, animationParameters) -> {
                  if (livingEntityPatch.getOriginal() instanceof ServerPlayer sp) {
                     DoppelgangerBindingService.consumePendingSummon(sp);
                     SinDevilTriggerManager.applySdtBurstDamageSmallAir((LivingEntity)livingEntityPatch.getOriginal());
                  }
               }, Side.SERVER)})
      );
   }
}
