package com.dmc.invincible_dmc.gameassets.animations.yamato;

import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoDodgeAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoDownDodgeAnimation;
import com.dmc.invincible_dmc.client.sound.DMCSounds;
import net.minecraft.sounds.SoundEvent;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationEvent.SimpleEvent;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.gameasset.Armatures;

final class YamatoDodgeAnimationBuilder {
   private YamatoDodgeAnimationBuilder() {
   }

   static void build(AnimationBuilder builder) {
      YamatoAnimations.YAMATO_STEP_F = builder.nextAccessor(
         "biped/yamato/dodge/yamato_dodge_forward",
         accessor -> (YamatoDodgeAnimation)new YamatoDodgeAnimation(0.01F, 0.4F, accessor, 0.6F, 1.65F, Armatures.BIPED)
               .addProperty(YamatoAttackAnimation.INVISIBLE_TIME, TimePairList.create(new float[]{0.0F, 0.16666667F}))
               .addEvents(
                  ActionAnimationProperty.ON_BEGIN_EVENTS,
                  new AnimationEvent[]{
                     SimpleEvent.create(
                        (livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound((SoundEvent)DMCSounds.DODGE.get(), 1.0F, 1.0F),
                        Side.SERVER
                     )
                  }
               )
      );
      YamatoAnimations.YAMATO_STEP_B = builder.nextAccessor(
         "biped/yamato/dodge/yamato_dodge_backward",
         accessor -> (YamatoDodgeAnimation)new YamatoDodgeAnimation(0.01F, 0.4F, accessor, 0.6F, 1.65F, Armatures.BIPED)
               .addProperty(YamatoAttackAnimation.INVISIBLE_TIME, TimePairList.create(new float[]{0.033333335F, 0.083333336F}))
               .addEvents(
                  ActionAnimationProperty.ON_BEGIN_EVENTS,
                  new AnimationEvent[]{
                     SimpleEvent.create(
                        (livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound((SoundEvent)DMCSounds.DODGE_EX.get(), 1.0F, 1.0F),
                        Side.SERVER
                     )
                  }
               )
               .addProperty(YamatoAttackAnimation.JUMP_CANCEL_TIME, TimePairList.create(new float[]{0.1F, Float.MAX_VALUE}))
               .newTimePair(0.0F, 0.1F)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
      );
      YamatoAnimations.YAMATO_STEP_L = builder.nextAccessor(
         "biped/yamato/dodge/yamato_dodge_left",
         accessor -> (YamatoDodgeAnimation)new YamatoDodgeAnimation(0.01F, 0.4F, accessor, 0.6F, 1.65F, Armatures.BIPED)
               .addProperty(YamatoAttackAnimation.INVISIBLE_TIME, TimePairList.create(new float[]{0.0F, 0.18333334F}))
               .addEvents(
                  ActionAnimationProperty.ON_BEGIN_EVENTS,
                  new AnimationEvent[]{
                     SimpleEvent.create(
                        (livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound((SoundEvent)DMCSounds.DODGE.get(), 1.0F, 1.0F),
                        Side.SERVER
                     )
                  }
               )
      );
      YamatoAnimations.YAMATO_STEP_R = builder.nextAccessor(
         "biped/yamato/dodge/yamato_dodge_right",
         accessor -> (YamatoDodgeAnimation)new YamatoDodgeAnimation(0.01F, 0.4F, accessor, 0.6F, 1.65F, Armatures.BIPED)
               .addProperty(YamatoAttackAnimation.INVISIBLE_TIME, TimePairList.create(new float[]{0.0F, 0.18333334F}))
               .addEvents(
                  ActionAnimationProperty.ON_BEGIN_EVENTS,
                  new AnimationEvent[]{
                     SimpleEvent.create(
                        (livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound((SoundEvent)DMCSounds.DODGE.get(), 1.0F, 1.0F),
                        Side.SERVER
                     )
                  }
               )
      );
      YamatoAnimations.YAMATO_STEP_D = builder.nextAccessor(
         "biped/yamato/dodge/yamato_dodge_down",
         accessor -> (YamatoDodgeAnimation)new YamatoDownDodgeAnimation(0.01F, 0.4F, accessor, 0.6F, 1.65F, Armatures.BIPED)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.1F}))
               .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoord(0.8F, 1.0F))
               .addProperty(YamatoAttackAnimation.INVISIBLE_TIME, TimePairList.create(new float[]{0.05F, 0.1F}))
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addEvents(
                  ActionAnimationProperty.ON_BEGIN_EVENTS,
                  new AnimationEvent[]{
                     SimpleEvent.create(
                        (livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound((SoundEvent)DMCSounds.DODGE_EX.get(), 1.0F, 1.0F),
                        Side.SERVER
                     )
                  }
               )
               .newTimePair(0.0F, 0.1F)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
      );
      YamatoAnimations.YAMATO_STEP_U = builder.nextAccessor(
         "biped/yamato/dodge/yamato_dodge_up",
         accessor -> (YamatoDodgeAnimation)new YamatoDodgeAnimation(0.01F, 0.5F, accessor, 0.6F, 1.65F, Armatures.BIPED)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 1.0F}))
               .addProperty(YamatoAttackAnimation.INVISIBLE_TIME, TimePairList.create(new float[]{0.1F, 0.18333334F}))
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addEvents(
                  ActionAnimationProperty.ON_BEGIN_EVENTS,
                  new AnimationEvent[]{
                     SimpleEvent.create(
                        (livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound((SoundEvent)DMCSounds.DODGE_EX.get(), 1.0F, 1.0F),
                        Side.SERVER
                     )
                  }
               )
               .newTimePair(0.0F, 0.15F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
      );
      YamatoAnimations.YAMATO_STEP_L_SHORT = builder.nextAccessor(
         "biped/yamato/dodge/yamato_dodge_left_short",
         accessor -> (YamatoDodgeAnimation)new YamatoDodgeAnimation(0.01F, 0.2F, accessor, 0.6F, 1.65F, Armatures.BIPED)
               .setResourceLocation("invincible_dmc", "biped/yamato/dodge/yamato_dodge_left")
               .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoord(0.95F, 1.0F))
               .addProperty(YamatoAttackAnimation.JUMP_CANCEL_TIME, TimePairList.create(new float[]{0.1F, Float.MAX_VALUE}))
               .addEvents(
                  ActionAnimationProperty.ON_BEGIN_EVENTS,
                  new AnimationEvent[]{
                     SimpleEvent.create(
                        (livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound((SoundEvent)DMCSounds.DODGE.get(), 1.0F, 1.0F),
                        Side.SERVER
                     )
                  }
               )
      );
      YamatoAnimations.YAMATO_STEP_R_SHORT = builder.nextAccessor(
         "biped/yamato/dodge/yamato_dodge_right_short",
         accessor -> (YamatoDodgeAnimation)new YamatoDodgeAnimation(0.01F, 0.2F, accessor, 0.6F, 1.65F, Armatures.BIPED)
               .setResourceLocation("invincible_dmc", "biped/yamato/dodge/yamato_dodge_right")
               .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoord(0.95F, 1.0F))
               .addProperty(YamatoAttackAnimation.JUMP_CANCEL_TIME, TimePairList.create(new float[]{0.1F, Float.MAX_VALUE}))
               .addEvents(
                  ActionAnimationProperty.ON_BEGIN_EVENTS,
                  new AnimationEvent[]{
                     SimpleEvent.create(
                        (livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound((SoundEvent)DMCSounds.DODGE.get(), 1.0F, 1.0F),
                        Side.SERVER
                     )
                  }
               )
      );
      YamatoAnimations.YAMATO_STEP_L_COMBAT = builder.nextAccessor(
         "biped/yamato/dodge/yamato_dodge_left_combat",
         accessor -> (YamatoDodgeAnimation)new YamatoDodgeAnimation(0.01F, 0.8333333F, accessor, 0.6F, 1.65F, Armatures.BIPED)
               .addProperty(YamatoAttackAnimation.JUMP_CANCEL_TIME, TimePairList.create(new float[]{0.1F, Float.MAX_VALUE}))
               .addEvents(
                  ActionAnimationProperty.ON_BEGIN_EVENTS,
                  new AnimationEvent[]{
                     SimpleEvent.create(
                        (livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound((SoundEvent)DMCSounds.DODGE.get(), 1.0F, 1.0F),
                        Side.SERVER
                     )
                  }
               )
               .newTimePair(0.0F, 0.15F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
      );
      YamatoAnimations.YAMATO_STEP_R_COMBAT = builder.nextAccessor(
         "biped/yamato/dodge/yamato_dodge_right_combat",
         accessor -> (YamatoDodgeAnimation)new YamatoDodgeAnimation(0.01F, 0.8333333F, accessor, 0.6F, 1.65F, Armatures.BIPED)
               .addProperty(YamatoAttackAnimation.JUMP_CANCEL_TIME, TimePairList.create(new float[]{0.1F, Float.MAX_VALUE}))
               .addEvents(
                  ActionAnimationProperty.ON_BEGIN_EVENTS,
                  new AnimationEvent[]{
                     SimpleEvent.create(
                        (livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound((SoundEvent)DMCSounds.DODGE.get(), 1.0F, 1.0F),
                        Side.SERVER
                     )
                  }
               )
               .newTimePair(0.0F, 0.15F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
      );
   }
}
