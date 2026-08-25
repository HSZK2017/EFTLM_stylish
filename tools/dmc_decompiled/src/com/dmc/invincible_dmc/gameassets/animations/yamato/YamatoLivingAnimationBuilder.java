package com.dmc.invincible_dmc.gameassets.animations.yamato;

import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoIdleSelectiveAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoLivingAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoMovementAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoMovingLivingAnimation;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationEvent.SimpleEvent;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.PlaybackSpeedModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.DirectStaticAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.GuardAnimation;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

final class YamatoLivingAnimationBuilder {
   private YamatoLivingAnimationBuilder() {
   }

   static void build(AnimationBuilder builder) {
      YamatoAnimations.YAMATO_IDLE = builder.nextAccessor(
         "biped/yamato/living/yamato_idle",
         accessor -> new YamatoIdleSelectiveAnimation(
               accessor, createDefaultIdleAnimation(), YamatoAnimations.YAMATO_IDLE_2_START, YamatoAnimations.YAMATO_IDLE_2, YamatoAnimations.YAMATO_IDLE_3
            )
      );
      YamatoAnimations.YAMATO_IDLE_2_START = builder.nextAccessor(
         "biped/yamato/living/yamato_idle_2_start",
         accessor -> (YamatoMovingLivingAnimation)new YamatoMovingLivingAnimation(0.1F, accessor, Armatures.BIPED)
               .newTimePair(0.0F, 2.1474836E9F)
               .addStateRemoveOld(EntityState.UPDATE_LIVING_MOTION, true)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, true)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, true)
               .addStateRemoveOld(EntityState.INACTION, false)
      );
      YamatoAnimations.YAMATO_IDLE_2 = builder.nextAccessor(
         "biped/yamato/living/yamato_idle_2",
         accessor -> (YamatoLivingAnimation)new YamatoLivingAnimation(0.15F, true, accessor, Armatures.BIPED)
               .addProperty(StaticAnimationProperty.FIXED_HEAD_ROTATION, true)
               .newTimePair(0.0F, 2.1474836E9F)
               .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
               .addStateRemoveOld(EntityState.LOCKON_ROTATE, false)
      );
      YamatoAnimations.YAMATO_IDLE_3 = builder.nextAccessor(
         "biped/yamato/living/yamato_idle_3",
         accessor -> (YamatoLivingAnimation)new YamatoLivingAnimation(0.15F, false, accessor, Armatures.BIPED)
               .addProperty(StaticAnimationProperty.FIXED_HEAD_ROTATION, true)
               .newTimePair(0.0F, 2.1474836E9F)
               .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
               .addStateRemoveOld(EntityState.LOCKON_ROTATE, false)
      );
      YamatoAnimations.YAMATO_BLOCK_RANGE = builder.nextAccessor(
         "biped/yamato/living/yamato_block_range", accessor -> new YamatoLivingAnimation(0.15F, false, accessor, Armatures.BIPED)
      );
      YamatoAnimations.YAMATO_GUARD = builder.nextAccessor(
         "biped/yamato/living/yamato_guard", accessor -> new YamatoLivingAnimation(0.15F, true, accessor, Armatures.BIPED)
      );
      YamatoAnimations.YAMATO_GUARD_HIT = builder.nextAccessor(
         "biped/yamato/living/yamato_guard_hit", accessor -> new GuardAnimation(0.05F, accessor, Armatures.BIPED)
      );
      YamatoAnimations.YAMATO_PARRY_RIGHT = builder.nextAccessor(
         "biped/yamato/living/yamato_parry_right", accessor -> new GuardAnimation(0.05F, 0.2F, accessor, Armatures.BIPED)
      );
      YamatoAnimations.YAMATO_PARRY_LEFT = builder.nextAccessor(
         "biped/yamato/living/yamato_parry_left", accessor -> new GuardAnimation(0.05F, 0.2F, accessor, Armatures.BIPED)
      );
      YamatoAnimations.YAMATO_SIN_DEVIL_TRIGGER = builder.nextAccessor(
         "biped/yamato/living/yamato_sin_devil_trigger",
         accessor -> (YamatoLivingAnimation)new YamatoLivingAnimation(0.05F, false, accessor, Armatures.BIPED)
               .newTimePair(0.0F, 2.1474836E9F)
               .addState(EntityState.ATTACK_RESULT, YamatoAnimations.INVINCIBLE_SOURCE_VALIDATOR)
               .addEvents(ActionAnimationProperty.ON_BEGIN_EVENTS, new AnimationEvent[]{SimpleEvent.create((livingEntityPatch, staticAnimation, objects) -> {
                  if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                     SinDevilTriggerManager.applySdtBurstDamage((LivingEntity)serverPlayerPatch.getOriginal());
                  }
               }, Side.SERVER)})
      );
      YamatoAnimations.YAMATO_SIN_DEVIL_TRIGGER_BACK = builder.nextAccessor(
         "biped/yamato/living/yamato_sin_devil_trigger_back", accessor -> new YamatoLivingAnimation(0.05F, false, accessor, Armatures.BIPED)
      );
      YamatoAnimations.TEST = builder.nextAccessor(
         "biped/yamato/living/yamato_test", accessor -> new YamatoLivingAnimation(0.15F, true, accessor, Armatures.BIPED)
      );
      YamatoAnimations.YAMATO_JUMP = builder.nextAccessor(
         "biped/yamato/living/yamato_jump", accessor -> new YamatoLivingAnimation(0.01F, false, accessor, Armatures.BIPED)
      );
      YamatoAnimations.YAMATO_WALK = builder.nextAccessor(
         "biped/yamato/living/yamato_walk", accessor -> new YamatoMovementAnimation(0.15F, true, accessor, Armatures.BIPED, 1.7F)
      );
      YamatoAnimations.YAMATO_RUN = builder.nextAccessor(
         "biped/yamato/living/yamato_run", accessor -> new YamatoMovementAnimation(0.15F, true, accessor, Armatures.BIPED, 0.85F)
      );
      YamatoAnimations.YAMATO_KNEEL = builder.nextAccessor(
         "biped/yamato/living/yamato_kneel", accessor -> new YamatoLivingAnimation(0.15F, true, accessor, Armatures.BIPED)
      );
      YamatoAnimations.YAMATO_SNEAK = builder.nextAccessor(
         "biped/yamato/living/yamato_sneak",
         accessor -> (YamatoLivingAnimation)new YamatoLivingAnimation(0.1F, true, accessor, Armatures.BIPED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.6F)
      );
      YamatoAnimations.YAMATO_ENEMY_STEP_FORWARD = builder.nextAccessor(
         "biped/yamato/living/yamato_enemy_step_forward",
         accessor -> (ActionAnimation)new ActionAnimation(0.05F, 0.7F, accessor, Armatures.BIPED)
               .setResourceLocation("epicfight", "biped/skill/phantom_ascent_forward")
               .addStateRemoveOld(EntityState.MOVEMENT_LOCKED, false)
               .newTimePair(0.0F, 0.3F)
               .addStateRemoveOld(EntityState.INACTION, true)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
               .addProperty(YamatoAttackAnimation.INPUT_BUFFER_DURATION_TICKS, 3)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.25F)
               .addEvents(
                  StaticAnimationProperty.ON_BEGIN_EVENTS,
                  new AnimationEvent[]{
                     SimpleEvent.create(
                        (entitypatch, animation, params) -> {
                           Vec3 pos = ((LivingEntity)entitypatch.getOriginal()).m_20182_();
                           entitypatch.playSound((SoundEvent)EpicFightSounds.TUMBLE.get(), 0.0F, 0.0F);
                           ((LivingEntity)entitypatch.getOriginal())
                              .m_9236_()
                              .m_7107_(
                                 (ParticleOptions)EpicFightParticles.AIR_BURST.get(),
                                 pos.f_82479_,
                                 pos.f_82480_ + (double)((LivingEntity)entitypatch.getOriginal()).m_20206_() * 0.5,
                                 pos.f_82481_,
                                 0.0,
                                 -1.0,
                                 2.0
                              );
                        },
                        Side.CLIENT
                     )
                  }
               )
      );
      YamatoAnimations.YAMATO_ENEMY_STEP_BACKWARD = builder.nextAccessor(
         "biped/yamato/living/yamato_enemy_step_backward",
         accessor -> (ActionAnimation)new ActionAnimation(0.05F, 0.7F, accessor, Armatures.BIPED)
               .setResourceLocation("epicfight", "biped/skill/phantom_ascent_backward")
               .addStateRemoveOld(EntityState.MOVEMENT_LOCKED, false)
               .newTimePair(0.0F, 0.3F)
               .addStateRemoveOld(EntityState.INACTION, true)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
               .addProperty(YamatoAttackAnimation.INPUT_BUFFER_DURATION_TICKS, 3)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.25F)
               .addEvents(
                  StaticAnimationProperty.ON_BEGIN_EVENTS,
                  new AnimationEvent[]{
                     SimpleEvent.create(
                        (entitypatch, animation, params) -> {
                           Vec3 pos = ((LivingEntity)entitypatch.getOriginal()).m_20182_();
                           entitypatch.playSound((SoundEvent)EpicFightSounds.TUMBLE.get(), 0.0F, 0.0F);
                           ((LivingEntity)entitypatch.getOriginal())
                              .m_9236_()
                              .m_7107_(
                                 (ParticleOptions)EpicFightParticles.AIR_BURST.get(),
                                 pos.f_82479_,
                                 pos.f_82480_ + (double)((LivingEntity)entitypatch.getOriginal()).m_20206_() * 0.5,
                                 pos.f_82481_,
                                 0.0,
                                 -1.0,
                                 2.0
                              );
                        },
                        Side.CLIENT
                     )
                  }
               )
      );
   }

   private static DirectStaticAnimation createDefaultIdleAnimation() {
      DirectStaticAnimation animation = new DirectStaticAnimation(
         ResourceLocation.fromNamespaceAndPath("invincible_dmc", "animmodels/animations/biped/yamato/living/yamato_idle.json"),
         0.15F,
         true,
         "invincible_dmc:biped/yamato/living/yamato_idle_default",
         Armatures.BIPED
      );
      animation.setAccessor(animation);
      return animation;
   }
}
