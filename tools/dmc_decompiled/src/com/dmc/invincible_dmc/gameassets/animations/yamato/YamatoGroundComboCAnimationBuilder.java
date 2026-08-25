package com.dmc.invincible_dmc.gameassets.animations.yamato;

import com.dmc.invincible_dmc.api.animation.types.customStun.CustomStunAnimationUtils;
import com.dmc.invincible_dmc.api.animation.types.customStun.CustomStunAttackAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.client.render.shake.CameraShakeManager;
import com.dmc.invincible_dmc.client.sound.DMCSounds;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerPatch;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import java.util.Set;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.PlaybackSpeedModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.YRotProvider;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.damagesource.StunType;

final class YamatoGroundComboCAnimationBuilder {
   private YamatoGroundComboCAnimationBuilder() {
   }

   static void build(AnimationBuilder builder) {
      YamatoAnimations.YAMATO_COMBO_C_START = builder.nextAccessor(
         "biped/yamato/attack/yamato_combo_c_start",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_COMBO_C_START",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        33, 36, 40, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.COMBO_C
                     ),
                     CustomStunAnimationUtils.createCustomStunPhase(
                        41, 44, 49, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.COMBO_C
                     ),
                     CustomStunAnimationUtils.createCustomStunPhase(
                        50, 53, 55, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.COMBO_C
                     ),
                     CustomStunAnimationUtils.createCustomStunPhase(
                        57, 60, 64, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.COMBO_C
                     ),
                     CustomStunAnimationUtils.createCustomStunPhase(
                        65, 69, 82, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.COMBO_C
                     )
                  )
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_4.get(), 0)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get(), 1)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get(), 2)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_4.get(), 3)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get(), 4)
                  .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.CRAZY_COMBO), 0)
                  .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.CRAZY_COMBO), 1)
                  .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.CRAZY_COMBO), 2)
                  .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.CRAZY_COMBO), 3)
                  .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.CRAZY_COMBO), 4)
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get(), 0)
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get(), 1)
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get(), 2)
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get(), 3)
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get(), 4)
                  .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE, 0)
                  .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE, 1)
                  .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE, 2)
                  .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE, 3)
                  .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE, 4)
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackAnimationProperty.REACH, 0.9F)
                  .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoord(0.63F, 1.0F))
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.0F, 0.083333336F}))
                  .addProperty(YamatoAttackAnimation.SDT_HIDE_WEAPON_TIME, TimePairList.create(new float[]{0.1F, 0.53333336F}))
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.53333336F, Float.MAX_VALUE}))
                  .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
                  .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
                  .addProperty(
                     ActionAnimationProperty.ENTITY_YROT_PROVIDER,
                     (YRotProvider)(animation, entityPatch) -> entityPatch instanceof DoppelgangerPatch
                           ? entityPatch.getYRot()
                           : MoveCoordFunctions.LOOK_DEST.get(animation, entityPatch)
                  )
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
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 1.1833333F)
                  .newTimePair(0.0F, 1.4166666F)
                  .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
                  .addStateRemoveOld(EntityState.LOCKON_ROTATE, true)
         )
      );
      YamatoAnimations.YAMATO_COMBO_C_LOOP = builder.nextAccessor(
         "biped/yamato/attack/yamato_combo_c_loop",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_COMBO_C_LOOP",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.01F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        1, 4, 7, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.COMBO_C
                     ),
                     CustomStunAnimationUtils.createCustomStunPhase(
                        8, 11, 15, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.COMBO_C
                     ),
                     CustomStunAnimationUtils.createCustomStunPhase(
                        17, 20, 22, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.COMBO_C
                     ),
                     CustomStunAnimationUtils.createCustomStunPhase(
                        24, 27, 30, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.COMBO_C
                     ),
                     CustomStunAnimationUtils.createCustomStunPhase(
                        33, 36, 50, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.COMBO_C
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.CRAZY_COMBO), 0)
                  .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.CRAZY_COMBO), 1)
                  .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.CRAZY_COMBO), 2)
                  .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.CRAZY_COMBO), 3)
                  .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.CRAZY_COMBO), 4)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_4.get(), 0)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get(), 1)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get(), 2)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_4.get(), 3)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get(), 4)
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get(), 0)
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get(), 1)
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get(), 2)
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get(), 3)
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get(), 4)
                  .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE, 0)
                  .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE, 1)
                  .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE, 2)
                  .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE, 3)
                  .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE, 4)
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.0F, Float.MAX_VALUE}))
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
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 0.6666667F)
                  .newTimePair(0.0F, 1.0F)
                  .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
                  .addStateRemoveOld(EntityState.LOCKON_ROTATE, true)
         )
      );
      YamatoAnimations.YAMATO_COMBO_C_END = builder.nextAccessor(
         "biped/yamato/attack/yamato_combo_c_end",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_COMBO_C_END",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        21, 32, 123, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.COMBO_C_END
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                  .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.CRAZY_COMBO_FINISH), 0)
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.31666666F, 1.5333333F}))
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{1.1666666F, 1.5333333F}))
                  .addProperty(YamatoAttackAnimation.SDT_IN_SHEATH_TIME, TimePairList.create(new float[]{1.5333333F, 1.7F}))
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
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 0.9166667F)
                  .addProperty(YamatoAttackAnimation.CORRECT_YROT_TO_CAMERA, true)
                  .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoord(0.3F))
                  .newTimePair(0.0F, 1.6166667F)
                  .addStateRemoveOld(EntityState.LOCKON_ROTATE, false)
                  .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
                  .addEvents(new AnimationEvent[]{InTimeEvent.create(0.35F, (livingEntityPatch, assetAccessor, animationParameters) -> {
                     if (livingEntityPatch instanceof PlayerPatch<?> playerPatch) {
                        CameraShakeManager.addShake(((Player)playerPatch.getOriginal()).m_146892_(), 8.0F, 3, 4.0F);
                     }
                  }, Side.LOCAL_CLIENT)})
         )
      );
   }
}
