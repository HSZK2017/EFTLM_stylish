package com.dmc.invincible_dmc.gameassets.animations.yamato;

import com.dmc.invincible_dmc.api.animation.types.customStun.CustomStunAnimationUtils;
import com.dmc.invincible_dmc.api.animation.types.customStun.CustomStunAttackAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.client.sound.DMCSounds;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerPatch;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import com.dmc.invincible_dmc.utils.yamato.TeleportGroundUtils;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationEvent.SimpleEvent;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.PlaybackSpeedModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.gameasset.Animations.ReusableSources;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.damagesource.StunType;

final class YamatoAerialComboAnimationBuilder {
   private YamatoAerialComboAnimationBuilder() {
   }

   static void build(AnimationBuilder builder) {
      YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_1 = builder.nextAccessor(
         "biped/yamato/attack/yamato_aerialrave_combo_a_1",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_AERIALRAVE_COMBO_A_1",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        6, 10, 43, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, YamatoAnimations.AERIALRAVE
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.WHOOSH_LIGHT_1.get())
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
                  .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
                  .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
                  .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoordWithInput(0.04F))
                  .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 0.16666667F)
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.083333336F, 0.71666664F}))
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{0.6166667F, 0.71666664F}))
                  .addProperty(AttackAnimationProperty.SYNC_CAMERA, true)
                  .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
                  .addProperty(YamatoAttackAnimation.YAMATO_NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.71666664F}))
                  .addProperty(StaticAnimationProperty.POSE_MODIFIER, CustomStunAttackAnimation.AERIALRAVE_COMB_DIRECTION_MODIFIER)
         )
      );
      YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_2 = builder.nextAccessor(
         "biped/yamato/attack/yamato_aerialrave_combo_a_2",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_AERIALRAVE_COMBO_A_2",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        3, 8, 62, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, YamatoAnimations.AERIALRAVE
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.WHOOSH_LIGHT_2.get())
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
                  .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
                  .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
                  .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoordWithInput(0.04F))
                  .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 0.13333334F)
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.0F, 1.0333333F}))
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{0.8666667F, 1.0333333F}))
                  .addProperty(AttackAnimationProperty.SYNC_CAMERA, true)
                  .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
                  .addProperty(YamatoAttackAnimation.YAMATO_NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 1.0333333F}))
                  .addProperty(StaticAnimationProperty.POSE_MODIFIER, CustomStunAttackAnimation.AERIALRAVE_COMB_DIRECTION_MODIFIER)
                  .addProperty(YamatoAttackAnimation.USE_MESH2_IN_LINK, true)
         )
      );
      YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_3 = builder.nextAccessor(
         "biped/yamato/attack/yamato_aerialrave_combo_a_3",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_AERIALRAVE_COMBO_A_3",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        11, 16, 55, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, YamatoAnimations.AERIALRAVE
                     )
                  )
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_19.get())
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
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
                  .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
                  .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
                  .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoordWithInput(0.04F))
                  .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 0.75F)
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.0F, 0.9166667F}))
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{0.81666666F, 0.9166667F}))
                  .addProperty(AttackAnimationProperty.SYNC_CAMERA, true)
                  .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
                  .addProperty(YamatoAttackAnimation.YAMATO_NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.5833333F}))
                  .addProperty(StaticAnimationProperty.POSE_MODIFIER, CustomStunAttackAnimation.AERIALRAVE_COMB_DIRECTION_MODIFIER)
                  .addProperty(YamatoAttackAnimation.USE_MESH2_IN_LINK, true)
         )
      );
      YamatoAnimations.YAMATO_AERIALRAVE_COMBO_B_1 = builder.nextAccessor(
         "biped/yamato/attack/yamato_aerialrave_combo_b_1",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_AERIALRAVE_COMBO_B_1",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        9, 13, 14, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, YamatoAnimations.AERIALRAVE_B
                     ),
                     CustomStunAnimationUtils.createCustomStunPhase(
                        16, 20, 21, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, YamatoAnimations.AERIALRAVE_B
                     ),
                     CustomStunAnimationUtils.createCustomStunPhase(
                        23, 27, 68, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, YamatoAnimations.AERIALRAVE_B
                     )
                  )
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_11.get(), 0)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_8.get(), 1)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get(), 2)
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.0F, 1.1333333F}))
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{0.13333334F, 1.1333333F}))
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
                  .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoordWithInput(0.04F))
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 0.41666666F)
                  .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
                  .addProperty(YamatoAttackAnimation.YAMATO_NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 1.1333333F}))
                  .addProperty(StaticAnimationProperty.POSE_MODIFIER, CustomStunAttackAnimation.AERIALRAVE_COMB_DIRECTION_MODIFIER)
                  .addProperty(YamatoAttackAnimation.USE_MESH2_IN_LINK, true)
         )
      );
      YamatoAnimations.YAMATO_AERIALRAVE_COMBO_B_2 = builder.nextAccessor(
         "biped/yamato/attack/yamato_aerialrave_combo_b_2",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_AERIALRAVE_COMBO_B_2",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        27, 32, 80, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, YamatoAnimations.AERIALRAVE_B
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_19.get())
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
                  .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
                  .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
                  .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoordWithInput(0.04F))
                  .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 1.0833334F)
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.0F, 1.3333334F}))
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{1.2166667F, 1.3333334F}))
                  .addProperty(AttackAnimationProperty.SYNC_CAMERA, true)
                  .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
                  .addProperty(YamatoAttackAnimation.YAMATO_NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 1.3333334F}))
                  .addProperty(StaticAnimationProperty.POSE_MODIFIER, CustomStunAttackAnimation.AERIALRAVE_COMB_DIRECTION_MODIFIER)
                  .addProperty(YamatoAttackAnimation.USE_MESH2_IN_LINK, true)
         )
      );
      YamatoAnimations.YAMATO_AERIALCLEAVE = builder.nextAccessor(
         "biped/yamato/attack/yamato_aerialcleave",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_AERIALCLEAVE",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        36, 49, 96, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.AERIALCLEAVE
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_18.get())
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                  .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
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
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 1.1666666F)
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.6F, 2.25F}))
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{1.7333333F, 2.25F}))
                  .addProperty(AttackAnimationProperty.SYNC_CAMERA, true)
                  .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
                  .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
                  .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.75F}))
                  .addProperty(ActionAnimationProperty.MOVE_ON_LINK, false)
                  .addEvents(new AnimationEvent[]{TeleportGroundUtils.create(36, -0.3F)})
                  .addEvents(StaticAnimationProperty.ON_END_EVENTS, new AnimationEvent[]{SimpleEvent.create(ReusableSources.RESTORE_BOUNDING_BOX, Side.BOTH)})
         )
      );
      YamatoAnimations.YAMATO_AERIALCLEAVE_DASH = builder.nextAccessor(
         "biped/yamato/attack/yamato_aerialcleave_dash",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_AERIALCLEAVE_DASH",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        22, 30, 96, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.RAPIDSLASH_END
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_18.get())
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                  .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
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
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 0.9166667F)
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.33333334F, 2.0F}))
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{1.4333333F, 2.0F}))
                  .addProperty(AttackAnimationProperty.SYNC_CAMERA, true)
                  .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
                  .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
                  .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.5F}))
                  .addProperty(ActionAnimationProperty.MOVE_ON_LINK, false)
                  .addEvents(new AnimationEvent[]{YamatoVfxUtils.summonJudgementCut(21, false), YamatoVfxUtils.summonRapidSlash(22)})
                  .newTimePair(0.0F, 1.1666666F)
                  .addStateRemoveOld(EntityState.LOCKON_ROTATE, false)
                  .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
         )
      );
      YamatoAnimations.YAMATO_AERIALCLEAVE_FAST = builder.nextAccessor(
         "biped/yamato/attack/yamato_aerialcleave_fast",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_AERIALCLEAVE_FAST",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        27, 39, 86, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.AERIALCLEAVE
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_18.get())
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                  .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
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
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 1.0F)
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.43333334F, 2.0833333F}))
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{1.5833334F, 2.0833333F}))
                  .addProperty(AttackAnimationProperty.SYNC_CAMERA, true)
                  .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
                  .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
                  .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.5833333F}))
                  .addProperty(ActionAnimationProperty.MOVE_ON_LINK, false)
                  .addEvents(new AnimationEvent[]{TeleportGroundUtils.create(25, -0.2F)})
                  .addEvents(StaticAnimationProperty.ON_END_EVENTS, new AnimationEvent[]{SimpleEvent.create(ReusableSources.RESTORE_BOUNDING_BOX, Side.BOTH)})
         )
      );
   }
}
