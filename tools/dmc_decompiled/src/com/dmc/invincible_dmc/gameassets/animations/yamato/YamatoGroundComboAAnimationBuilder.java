package com.dmc.invincible_dmc.gameassets.animations.yamato;

import com.dmc.invincible_dmc.api.animation.types.customStun.CustomStunAnimationUtils;
import com.dmc.invincible_dmc.api.animation.types.customStun.CustomStunAttackAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.client.render.afterimage.PerFrameAfterimageRenderer;
import com.dmc.invincible_dmc.client.sound.DMCSounds;
import com.dmc.invincible_dmc.conditions.GroundedCondition;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerPatch;
import com.dmc.invincible_dmc.entity.util.DMCDodgeLocationIndicator;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import com.dmc.invincible_dmc.utils.vfx.CameraFovUtil;
import com.dmc.invincible_dmc.utils.yamato.TargetTeleportUtils;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
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
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.StunType;

final class YamatoGroundComboAAnimationBuilder {
   private YamatoGroundComboAAnimationBuilder() {
   }

   static void build(AnimationBuilder builder) {
      YamatoAnimations.YAMATO_COMBO_A_1 = builder.nextAccessor(
         "biped/yamato/attack/yamato_combo_a_1",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_COMBO_A_1",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        10, 15, 57, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, YamatoAnimations.SHEATH_COLLIDER
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.AIR_BURST)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.SHEATH_ATTACK1.get())
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT.get())
                  .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> {
                     if (livingEntityPatch instanceof PlayerPatch<?> playerPatch && SinDevilTriggerManager.isPlayerInSDT((Player)playerPatch.getOriginal())) {
                        return 1.075F;
                     }

                     if (livingEntityPatch instanceof DoppelgangerPatch doppelgangerPatch) {
                        PlayerPatch<?> ownerPatch = doppelgangerPatch.getOwnerPatch();
                        if (ownerPatch != null && SinDevilTriggerManager.isPlayerInSDT((Player)ownerPatch.getOriginal())) {
                           return 1.075F;
                        }
                     }

                     return 1.0F;
                  })
                  .addProperty(CustomStunAttackAnimation.HOLD_LEDGE, true)
                  .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
                  .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
                  .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 0.3F)
                  .addProperty(AttackAnimationProperty.SYNC_CAMERA, true)
         )
      );
      YamatoAnimations.YAMATO_COMBO_A_2 = builder.nextAccessor(
         "biped/yamato/attack/yamato_combo_a_2",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_COMBO_A_2",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        7, 16, 65, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, YamatoAnimations.SHEATH_COLLIDER
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.AIR_BURST)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.SHEATH_ATTACK2.get())
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT.get())
                  .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> {
                     if (livingEntityPatch instanceof PlayerPatch<?> playerPatch && SinDevilTriggerManager.isPlayerInSDT((Player)playerPatch.getOriginal())) {
                        return 1.075F;
                     }

                     if (livingEntityPatch instanceof DoppelgangerPatch doppelgangerPatch) {
                        PlayerPatch<?> ownerPatch = doppelgangerPatch.getOwnerPatch();
                        if (ownerPatch != null && SinDevilTriggerManager.isPlayerInSDT((Player)ownerPatch.getOriginal())) {
                           return 1.075F;
                        }
                     }

                     return 1.0F;
                  })
                  .addProperty(CustomStunAttackAnimation.HOLD_LEDGE, true)
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 0.33333334F)
         )
      );
      YamatoAnimations.YAMATO_COMBO_A_3 = builder.nextAccessor(
         "biped/yamato/attack/yamato_combo_a_3",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_COMBO_A_3",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        12, 17, 21, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                     ),
                     CustomStunAnimationUtils.createCustomStunPhase(
                        23, 28, 65, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                     )
                  )
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_7.get(), 0)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_8.get(), 1)
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.16666667F, 2.85F}))
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{1.8333334F, 2.85F}))
                  .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> {
                     if (livingEntityPatch instanceof PlayerPatch<?> playerPatch && SinDevilTriggerManager.isPlayerInSDT((Player)playerPatch.getOriginal())) {
                        return 1.075F;
                     }

                     if (livingEntityPatch instanceof DoppelgangerPatch doppelgangerPatch) {
                        PlayerPatch<?> ownerPatch = doppelgangerPatch.getOwnerPatch();
                        if (ownerPatch != null && SinDevilTriggerManager.isPlayerInSDT((Player)ownerPatch.getOriginal())) {
                           return 1.075F;
                        }
                     }

                     return 1.0F;
                  })
                  .addProperty(CustomStunAttackAnimation.HOLD_LEDGE, true)
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 0.53333336F)
         )
      );
      YamatoAnimations.YAMATO_DODGE_COUNTER = builder.nextAccessor(
         "biped/yamato/attack/yamato_dodge_counter",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_DODGE_COUNTER",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        12, 17, 21, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                     ),
                     CustomStunAnimationUtils.createCustomStunPhase(
                        23, 28, 65, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                     )
                  )
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_7.get(), 0)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_8.get(), 1)
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.16666667F, 2.85F}))
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{1.8333334F, 2.85F}))
                  .addProperty(AttackAnimationProperty.SYNC_CAMERA, true)
                  .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> {
                     if (livingEntityPatch instanceof PlayerPatch<?> playerPatch && SinDevilTriggerManager.isPlayerInSDT((Player)playerPatch.getOriginal())) {
                        return 1.075F;
                     }

                     if (livingEntityPatch instanceof DoppelgangerPatch doppelgangerPatch) {
                        PlayerPatch<?> ownerPatch = doppelgangerPatch.getOwnerPatch();
                        if (ownerPatch != null && SinDevilTriggerManager.isPlayerInSDT((Player)ownerPatch.getOriginal())) {
                           return 1.075F;
                        }
                     }

                     return 1.0F;
                  })
                  .addProperty(CustomStunAttackAnimation.HOLD_LEDGE, true)
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 0.53333336F)
                  .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
                  .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
                  .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
                  .addEvents(
                     new AnimationEvent[]{
                        PerFrameAfterimageRenderer.inPeriodEvent(0.0F, 0.2F, PerFrameAfterimageRenderer::dash, Side.CLIENT),
                        PerFrameAfterimageRenderer.stopInTimeEvent(0.2F, Side.CLIENT)
                     }
                  )
                  .addEvents(
                     ActionAnimationProperty.ON_END_EVENTS,
                     new AnimationEvent[]{
                        SimpleEvent.create((livingEntityPatch, staticAnimation, objects) -> PerFrameAfterimageRenderer.stop(livingEntityPatch), Side.CLIENT)
                     }
                  )
                  .newTimePair(0.0F, 0.46666667F)
                  .addState(EntityState.ATTACK_RESULT, YamatoAnimations.INVINCIBLE_SOURCE_VALIDATOR)
                  .addState(EntityState.PROJECTILE_IMPACT_RESULT, DodgeAnimation.IGNORE_ALL_PROJECTILES)
         )
      );
      YamatoAnimations.YAMATO_STRIKE = builder.nextAccessor(
         "biped/yamato/attack/yamato_strike",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_STRIKE",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        4, 9, 10, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.RAPIDSLASH
                     ),
                     CustomStunAnimationUtils.createCustomStunPhase(
                        11, 18, 92, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.RAPIDSLASH
                     )
                  )
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_4.get(), 0)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get(), 1)
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(YamatoAttackAnimation.YAMATO_FIXED_MOVE_DISTANCE, true)
                  .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoord(0.33F, 1.0F))
                  .addProperty(AttackAnimationProperty.SYNC_CAMERA, true)
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.06666667F, 1.2833333F}))
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{1.0F, 1.2833333F}))
                  .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> {
                     if (livingEntityPatch instanceof PlayerPatch<?> playerPatch && SinDevilTriggerManager.isPlayerInSDT((Player)playerPatch.getOriginal())) {
                        return 1.075F;
                     }

                     if (livingEntityPatch instanceof DoppelgangerPatch doppelgangerPatch) {
                        PlayerPatch<?> ownerPatch = doppelgangerPatch.getOwnerPatch();
                        if (ownerPatch != null && SinDevilTriggerManager.isPlayerInSDT((Player)ownerPatch.getOriginal())) {
                           return 1.075F;
                        }
                     }

                     return 1.0F;
                  })
                  .addProperty(CustomStunAttackAnimation.HOLD_LEDGE, true)
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 0.38333333F)
                  .addEvents(
                     new AnimationEvent[]{
                        YamatoVfxUtils.summonJudgementCut(12, false),
                        InTimeEvent.create(
                           0.21666667F, (patch, anim, params) -> patch.playSound((SoundEvent)DMCSounds.JUDGEMENT_CUT_JUST.get(), 1.0F, 1.0F), Side.SERVER
                        )
                     }
                  )
                  .addEvents(
                     ActionAnimationProperty.ON_BEGIN_EVENTS, new AnimationEvent[]{SimpleEvent.create((livingEntityPatch, staticAnimation, objects) -> {
                        ((LivingEntity)livingEntityPatch.getOriginal()).m_9236_().m_7967_(new DMCDodgeLocationIndicator(livingEntityPatch));
                        if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                           LivingEntity target = serverPlayerPatch.getTarget();
                           if (target != null && GroundedCondition.check(target)) {
                              TargetTeleportUtils.ExecuteYamatoTricker(serverPlayerPatch, null, target, false);
                           }
                        }
                     }, Side.SERVER)}
                  )
                  .newTimePair(0.0F, 0.3F)
                  .addState(EntityState.ATTACK_RESULT, YamatoAnimations.INVINCIBLE_SOURCE_VALIDATOR)
                  .addState(EntityState.PROJECTILE_IMPACT_RESULT, DodgeAnimation.IGNORE_ALL_PROJECTILES)
         )
      );
      YamatoAnimations.YAMATO_COMBO_A_4 = builder.nextAccessor(
         "biped/yamato/attack/yamato_combo_a_4",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_COMBO_A_4",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        26, 31, 63, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, YamatoAnimations.BLADE_COLLIDER_EX
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_9.get(), 0)
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                  .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> {
                     if (livingEntityPatch instanceof PlayerPatch<?> playerPatch && SinDevilTriggerManager.isPlayerInSDT((Player)playerPatch.getOriginal())) {
                        return 1.075F;
                     }

                     if (livingEntityPatch instanceof DoppelgangerPatch doppelgangerPatch) {
                        PlayerPatch<?> ownerPatch = doppelgangerPatch.getOwnerPatch();
                        if (ownerPatch != null && SinDevilTriggerManager.isPlayerInSDT((Player)ownerPatch.getOriginal())) {
                           return 1.075F;
                        }
                     }

                     return 1.0F;
                  })
                  .addProperty(CustomStunAttackAnimation.HOLD_LEDGE, true)
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.0F, 2.4666667F}))
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 0.8333333F)
                  .addProperty(YamatoAttackAnimation.USE_MESH2_IN_LINK, true)
         )
      );
      YamatoAnimations.YAMATO_COMBO_A_4_SDT = builder.nextAccessor(
         "biped/yamato/attack/yamato_combo_a_4_sdt",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_COMBO_A_4_SDT",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.15F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        30, 36, 48, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.SDT_A4
                     ),
                     CustomStunAnimationUtils.createCustomStunPhase(
                        48, 56, 85, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.SDT_A4
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_18.get(), 0)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_17.get(), 1)
                  .addProperty(AttackAnimationProperty.REACH, 0.9F)
                  .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
                  .addProperty(CustomStunAttackAnimation.HOLD_LEDGE, true)
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.0F, 2.7333333F}))
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{1.8333334F, 2.7333333F}))
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 1.1666666F)
                  .addProperty(YamatoAttackAnimation.USE_MESH2_IN_LINK, true)
                  .newTimePair(0.0F, 1.1666666F)
                  .addStateRemoveOld(EntityState.LOCKON_ROTATE, false)
                  .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
         )
      );
      YamatoAnimations.YAMATO_COMBO_A_5_SDT = builder.nextAccessor(
         "biped/yamato/attack/yamato_combo_a_5_sdt",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_COMBO_A_5_SDT",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.1F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        47, 65, 177, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.SDT_A5
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_2.get())
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                  .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
                  .addProperty(CustomStunAttackAnimation.HOLD_LEDGE, true)
                  .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.0F, 4.0833335F}))
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{2.75F, 4.0833335F}))
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 1.75F)
                  .addProperty(YamatoAttackAnimation.USE_MESH2_IN_LINK, true)
                  .addEvents(
                     ActionAnimationProperty.ON_END_EVENTS,
                     new AnimationEvent[]{SimpleEvent.create((livingEntityPatch, staticAnimation, objects) -> CameraFovUtil.stopZoom(), Side.CLIENT)}
                  )
                  .newTimePair(0.0F, 1.0833334F)
                  .addStateRemoveOld(EntityState.LOCKON_ROTATE, false)
                  .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
         )
      );
   }
}
