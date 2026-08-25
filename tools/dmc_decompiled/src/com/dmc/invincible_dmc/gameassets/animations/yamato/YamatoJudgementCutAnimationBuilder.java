package com.dmc.invincible_dmc.gameassets.animations.yamato;

import com.dmc.invincible_dmc.api.animation.types.customStun.CustomStunAnimationUtils;
import com.dmc.invincible_dmc.api.animation.types.customStun.CustomStunAttackAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.JudgementCutEndAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.client.sound.DMCSounds;
import com.dmc.invincible_dmc.entity.util.DMCDodgeLocationIndicator;
import com.dmc.invincible_dmc.gameassets.DMCEffects;
import com.dmc.invincible_dmc.utils.yamato.Dmc3JudgementCutStorm;
import com.dmc.invincible_dmc.utils.yamato.JCEServer;
import com.dmc.invincible_dmc.utils.yamato.TeleportGroundUtils;
import java.util.List;
import java.util.Set;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationEvent.SimpleEvent;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.PlaybackSpeedModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.DodgeAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.StunType;

final class YamatoJudgementCutAnimationBuilder {
   private YamatoJudgementCutAnimationBuilder() {
   }

   static void build(AnimationBuilder builder) {
      YamatoAnimations.YAMATO_JUDGEMENT_CUT_GROUND = builder.nextAccessor(
         "biped/yamato/attack/yamato_judgement_cut_ground",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_JUDGEMENT_CUT_GROUND",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        47, 55, 110, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.RAPIDSLASH
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.JUDGEMENT_CUT.get())
                  .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.JUDGEMENT_CUT))
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.78333336F, 1.1666666F}))
                  .addProperty(YamatoAttackAnimation.SDT_HIDE_WEAPON_TIME, TimePairList.create(new float[]{0.0F, 2.1474836E9F}))
                  .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
                  .addProperty(CustomStunAttackAnimation.HOLD_LEDGE, true)
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 1.0333333F)
                  .addProperty(AttackAnimationProperty.SYNC_CAMERA, true)
                  .addProperty(YamatoAttackAnimation.USE_MESH2_IN_LINK, false)
                  .addEvents(
                     new AnimationEvent[]{
                        YamatoVfxUtils.summonJudgementCut(47, false),
                        InTimeEvent.create(
                           0.016666668F, (ep, anim, objs) -> ((LivingEntity)ep.getOriginal()).m_9236_().m_7967_(new DMCDodgeLocationIndicator(ep)), Side.SERVER
                        )
                     }
                  )
                  .newTimePair(0.0F, 0.41666666F)
                  .addState(EntityState.ATTACK_RESULT, DodgeAnimation.DODGEABLE_SOURCE_VALIDATOR)
                  .addState(EntityState.PROJECTILE_IMPACT_RESULT, DodgeAnimation.IGNORE_ALL_PROJECTILES)
                  .addEvents(
                     ActionAnimationProperty.ON_BEGIN_EVENTS,
                     new AnimationEvent[]{
                        SimpleEvent.create((ep, anim, objs) -> ep.playSound((SoundEvent)DMCSounds.JUDGEMENT_CUT_BEGIN.get(), 1.0F, 1.0F), Side.SERVER)
                     }
                  )
         )
      );
      YamatoAnimations.YAMATO_JUDGEMENT_CUT_GROUND_FS = builder.nextAccessor(
         "biped/yamato/attack/yamato_judgement_cut_ground_fs",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_JUDGEMENT_CUT_GROUND_FS",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        4, 14, 35, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.RAPIDSLASH
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.JUDGEMENT_CUT_JUST.get())
                  .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.JUDGEMENT_CUT))
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.016666668F, 0.45F}))
                  .addProperty(YamatoAttackAnimation.SDT_HIDE_WEAPON_TIME, TimePairList.create(new float[]{0.0F, 2.1474836E9F}))
                  .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 0.46666667F)
                  .addProperty(AttackAnimationProperty.SYNC_CAMERA, true)
                  .addProperty(YamatoAttackAnimation.USE_MESH2_IN_LINK, false)
                  .addEvents(new AnimationEvent[]{YamatoVfxUtils.summonJudgementCut(4)})
                  .addEvents(
                     ActionAnimationProperty.ON_BEGIN_EVENTS,
                     new AnimationEvent[]{
                        SimpleEvent.create((ep, anim, objs) -> ep.playSound((SoundEvent)DMCSounds.JUDGEMENT_CUT_BEGIN.get(), 1.0F, 1.0F), Side.SERVER)
                     }
                  )
         )
      );
      YamatoAnimations.YAMATO_JUDGEMENT_CUT_AIR = builder.nextAccessor(
         "biped/yamato/attack/yamato_judgement_cut_air",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_JUDGEMENT_CUT_AIR",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        7, 11, 60, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.RAPIDSLASH
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.JUDGEMENT_CUT.get())
                  .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.JUDGEMENT_CUT))
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.1F, 0.33333334F}))
                  .addProperty(YamatoAttackAnimation.SDT_HIDE_WEAPON_TIME, TimePairList.create(new float[]{0.0F, 2.1474836E9F}))
                  .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 0.75F)
                  .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
                  .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.75F}))
                  .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoordWithInput(0.04F))
                  .addProperty(YamatoAttackAnimation.YAMATO_NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.75F}))
                  .addProperty(AttackAnimationProperty.SYNC_CAMERA, true)
                  .addProperty(YamatoAttackAnimation.USE_MESH2_IN_LINK, false)
                  .addEvents(new AnimationEvent[]{YamatoVfxUtils.summonJudgementCut(6, false)})
                  .addEvents(
                     ActionAnimationProperty.ON_BEGIN_EVENTS,
                     new AnimationEvent[]{
                        SimpleEvent.create((ep, anim, objs) -> ep.playSound((SoundEvent)DMCSounds.JUDGEMENT_CUT_BEGIN.get(), 1.0F, 1.0F), Side.SERVER)
                     }
                  )
         )
      );
      YamatoAnimations.YAMATO_JUDGEMENT_CUT_AIR_FS = builder.nextAccessor(
         "biped/yamato/attack/yamato_judgement_cut_air_fs",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_JUDGEMENT_CUT_AIR_FS",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        4, 14, 60, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.RAPIDSLASH
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.JUDGEMENT_CUT_JUST.get())
                  .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.JUDGEMENT_CUT))
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.016666668F, 0.4F}))
                  .addProperty(YamatoAttackAnimation.SDT_HIDE_WEAPON_TIME, TimePairList.create(new float[]{0.0F, 2.1474836E9F}))
                  .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 0.5F)
                  .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
                  .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.6666667F}))
                  .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoordWithInput(0.04F))
                  .addProperty(YamatoAttackAnimation.YAMATO_NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.6666667F}))
                  .addProperty(AttackAnimationProperty.SYNC_CAMERA, true)
                  .addProperty(YamatoAttackAnimation.USE_MESH2_IN_LINK, false)
                  .addEvents(new AnimationEvent[]{YamatoVfxUtils.summonJudgementCut(4)})
                  .addEvents(
                     ActionAnimationProperty.ON_BEGIN_EVENTS,
                     new AnimationEvent[]{
                        SimpleEvent.create((ep, anim, objs) -> ep.playSound((SoundEvent)DMCSounds.JUDGEMENT_CUT_BEGIN.get(), 1.0F, 1.0F), Side.SERVER)
                     }
                  )
         )
      );
      YamatoAnimations.YAMATO_JUDGEMENT_CUT_END = builder.nextAccessor(
         "biped/yamato/attack/yamato_judgement_cut_end",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_JUDGEMENT_CUT_END",
            accessor -> (JudgementCutEndAnimation)new JudgementCutEndAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     4.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                           294,
                           310,
                           320,
                           InteractionHand.MAIN_HAND,
                           1.0F,
                           1.0F,
                           ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                           YamatoAnimations.JUDGEMENT_CUT_END
                        )
                        .addProperty(AttackPhaseProperty.SWING_SOUND, null)
                        .addProperty(
                           AttackPhaseProperty.SOURCE_TAG,
                           Set.of(
                              EpicFightDamageTypeTags.WEAPON_INNATE,
                              EpicFightDamageTypeTags.UNBLOCKALBE,
                              EpicFightDamageTypeTags.GUARD_PUNCTURE,
                              EpicFightDamageTypeTags.IS_MELEE
                           )
                        ),
                     JudgementCutEndAnimation.createSyncOnlyPhase(
                        107,
                        113,
                        120,
                        InteractionHand.MAIN_HAND,
                        0.0F,
                        1.0F,
                        ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                        YamatoAnimations.JUDGEMENT_CUT_END,
                        (attacker, target, result) -> {
                           if (target instanceof LivingEntity livingEntity) {
                              livingEntity.m_7292_(new MobEffectInstance((MobEffect)DMCEffects.STOP.get(), 63, 63));
                           }
                        }
                     )
                  )
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get(), 0)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get(), 1)
                  .addProperty(
                     AttackPhaseProperty.SOURCE_TAG,
                     Set.of(
                        EpicFightDamageTypeTags.WEAPON_INNATE,
                        EpicFightDamageTypeTags.UNBLOCKALBE,
                        EpicFightDamageTypeTags.GUARD_PUNCTURE,
                        EpicFightDamageTypeTags.IS_MELEE,
                        YamatoAnimations.JUDGEMENT_CUT
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, null)
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{3.75F, 4.883333F}))
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{1.8333334F, 4.883333F}))
                  .addProperty(YamatoAttackAnimation.JC_RELEASE_BLOCK_TIME, TimePairList.create(new float[]{0.0F, 4.6666665F}))
                  .addProperty(JudgementCutEndAnimation.RAIN_FREEZE_TIME, TimePairList.create(new float[]{1.6833333F, 4.9F}))
                  .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
                  .addProperty(YamatoAttackAnimation.USE_MESH2_IN_LINK, false)
                  .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 5.333F}))
                  .addProperty(JudgementCutEndAnimation.MOVE_ROOT_PHASE, new JudgementCutEndAnimation.SpecialPhase(0.0F, 5.333F))
                  .newTimePair(1.7666667F, 2.1474836E9F)
                  .addState(EntityState.ATTACK_RESULT, YamatoAnimations.INVINCIBLE_SOURCE_VALIDATOR)
                  .addProperty(StaticAnimationProperty.ON_BEGIN_EVENTS, List.of(SimpleEvent.create((ep, anim, objs) -> JCEServer.prev(ep), Side.SERVER)))
                  .newTimePair(0.0F, Float.MAX_VALUE)
                  .addStateRemoveOld(EntityState.MOVEMENT_LOCKED, true)
                  .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
                  .addStateRemoveOld(EntityState.LOCKON_ROTATE, false)
                  .addStateRemoveOld(EntityState.CAN_USE_ITEM, false)
         )
      );
      YamatoAnimations.YAMATO_JUDGEMENT_CUT_END_INSTANT = builder.nextAccessor(
         "biped/yamato/attack/yamato_judgement_cut_end_instant",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_JUDGEMENT_CUT_END_INSTANT",
            accessor -> (JudgementCutEndAnimation)new JudgementCutEndAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     4.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                           194,
                           210,
                           220,
                           InteractionHand.MAIN_HAND,
                           1.0F,
                           1.0F,
                           ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                           YamatoAnimations.JUDGEMENT_CUT_END
                        )
                        .addProperty(AttackPhaseProperty.SWING_SOUND, null)
                        .addProperty(
                           AttackPhaseProperty.SOURCE_TAG,
                           Set.of(
                              EpicFightDamageTypeTags.WEAPON_INNATE,
                              EpicFightDamageTypeTags.UNBLOCKALBE,
                              EpicFightDamageTypeTags.GUARD_PUNCTURE,
                              EpicFightDamageTypeTags.IS_MELEE
                           )
                        ),
                     JudgementCutEndAnimation.createSyncOnlyPhase(
                        7,
                        13,
                        20,
                        InteractionHand.MAIN_HAND,
                        0.0F,
                        1.0F,
                        ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                        YamatoAnimations.JUDGEMENT_CUT_END,
                        (attacker, target, result) -> {
                           if (target instanceof LivingEntity livingEntity) {
                              livingEntity.m_7292_(new MobEffectInstance((MobEffect)DMCEffects.STOP.get(), 63, 63));
                           }
                        }
                     )
                  )
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get(), 0)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get(), 1)
                  .addProperty(
                     AttackPhaseProperty.SOURCE_TAG,
                     Set.of(
                        EpicFightDamageTypeTags.WEAPON_INNATE,
                        EpicFightDamageTypeTags.UNBLOCKALBE,
                        EpicFightDamageTypeTags.GUARD_PUNCTURE,
                        EpicFightDamageTypeTags.IS_MELEE,
                        YamatoAnimations.JUDGEMENT_CUT
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, null)
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{2.0833333F, 3.2166667F}))
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.16666667F, 3.2166667F}))
                  .addProperty(YamatoAttackAnimation.JC_RELEASE_BLOCK_TIME, TimePairList.create(new float[]{0.0F, 3.0F}))
                  .addProperty(JudgementCutEndAnimation.RAIN_FREEZE_TIME, TimePairList.create(new float[]{0.11666667F, 3.2166667F}))
                  .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
                  .addProperty(YamatoAttackAnimation.USE_MESH2_IN_LINK, false)
                  .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 5.333F}))
                  .addProperty(JudgementCutEndAnimation.MOVE_ROOT_PHASE, new JudgementCutEndAnimation.SpecialPhase(0.0F, 5.333F))
                  .newTimePair(0.1F, 2.1474836E9F)
                  .addState(EntityState.ATTACK_RESULT, YamatoAnimations.INVINCIBLE_SOURCE_VALIDATOR)
                  .addProperty(StaticAnimationProperty.ON_BEGIN_EVENTS, List.of(SimpleEvent.create((ep, anim, objs) -> JCEServer.prev(ep), Side.SERVER)))
                  .newTimePair(0.0F, Float.MAX_VALUE)
                  .addStateRemoveOld(EntityState.MOVEMENT_LOCKED, true)
                  .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
                  .addStateRemoveOld(EntityState.LOCKON_ROTATE, false)
                  .addStateRemoveOld(EntityState.CAN_USE_ITEM, false)
         )
      );
      YamatoAnimations.YAMATO_JUDGEMENT_CUT_END_DMC3 = builder.nextAccessor(
         "biped/yamato/attack/yamato_judgement_cut_end_dmc3",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_JUDGEMENT_CUT_END_DMC3",
            accessor -> (JudgementCutEndAnimation)new JudgementCutEndAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     4.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                           194,
                           210,
                           220,
                           InteractionHand.MAIN_HAND,
                           1.0F,
                           1.0F,
                           ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                           YamatoAnimations.JUDGEMENT_CUT_END
                        )
                        .addProperty(AttackPhaseProperty.SWING_SOUND, null)
                        .addProperty(
                           AttackPhaseProperty.SOURCE_TAG,
                           Set.of(
                              EpicFightDamageTypeTags.WEAPON_INNATE,
                              EpicFightDamageTypeTags.UNBLOCKALBE,
                              EpicFightDamageTypeTags.GUARD_PUNCTURE,
                              EpicFightDamageTypeTags.IS_MELEE
                           )
                        ),
                     JudgementCutEndAnimation.createSyncOnlyPhase(
                        7,
                        13,
                        20,
                        InteractionHand.MAIN_HAND,
                        0.0F,
                        1.0F,
                        ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                        YamatoAnimations.JUDGEMENT_CUT_END,
                        (attacker, target, result) -> {
                           if (target instanceof LivingEntity livingEntity) {
                              livingEntity.m_7292_(new MobEffectInstance((MobEffect)DMCEffects.STOP.get(), 63, 63));
                           }
                        }
                     )
                  )
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get(), 0)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get(), 1)
                  .addProperty(
                     AttackPhaseProperty.SOURCE_TAG,
                     Set.of(
                        EpicFightDamageTypeTags.WEAPON_INNATE,
                        EpicFightDamageTypeTags.UNBLOCKALBE,
                        EpicFightDamageTypeTags.GUARD_PUNCTURE,
                        EpicFightDamageTypeTags.IS_MELEE,
                        YamatoAnimations.JUDGEMENT_CUT
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NEUTRALIZE)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, null)
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{2.0833333F, 3.2166667F}))
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.16666667F, 3.2166667F}))
                  .addProperty(YamatoAttackAnimation.JC_RELEASE_BLOCK_TIME, TimePairList.create(new float[]{0.0F, 3.0F}))
                  .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
                  .addProperty(YamatoAttackAnimation.USE_MESH2_IN_LINK, false)
                  .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 5.333F}))
                  .addProperty(JudgementCutEndAnimation.MOVE_ROOT_PHASE, new JudgementCutEndAnimation.SpecialPhase(0.0F, 5.333F))
                  .newTimePair(0.1F, 2.1474836E9F)
                  .addState(EntityState.ATTACK_RESULT, YamatoAnimations.INVINCIBLE_SOURCE_VALIDATOR)
                  .addEvents(new AnimationEvent[]{Dmc3JudgementCutStorm.createEvent(), TeleportGroundUtils.create(99, -0.1F)})
                  .setResourceLocation("invincible_dmc", "biped/yamato/attack/yamato_judgement_cut_end_instant")
                  .newTimePair(0.1F, 2.1474836E9F)
                  .addState(EntityState.ATTACK_RESULT, YamatoAnimations.INVINCIBLE_SOURCE_VALIDATOR)
                  .newTimePair(0.0F, Float.MAX_VALUE)
                  .addStateRemoveOld(EntityState.MOVEMENT_LOCKED, true)
                  .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
                  .addStateRemoveOld(EntityState.LOCKON_ROTATE, false)
                  .addStateRemoveOld(EntityState.CAN_USE_ITEM, false)
         )
      );
   }
}
