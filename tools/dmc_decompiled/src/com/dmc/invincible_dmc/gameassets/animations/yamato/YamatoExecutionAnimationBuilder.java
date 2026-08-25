package com.dmc.invincible_dmc.gameassets.animations.yamato;

import com.dmc.invincible_dmc.api.animation.types.customStun.CustomStunAnimationUtils;
import com.dmc.invincible_dmc.api.animation.types.customStun.CustomStunAttackAnimation;
import com.dmc.invincible_dmc.api.animation.types.customStun.CustomStunPhase;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoExecutionAnimation;
import com.dmc.invincible_dmc.api.stun.StrongStunController;
import com.dmc.invincible_dmc.client.effeks.ExecuteEffek;
import com.dmc.invincible_dmc.client.effeks.LightRingEffek;
import com.dmc.invincible_dmc.client.render.screenshader.ImpactBlurEffect;
import com.dmc.invincible_dmc.client.render.shake.CameraShakeManager;
import com.dmc.invincible_dmc.compat.combat_evolution.CombatEvolutionDamageTypeTags;
import com.dmc.invincible_dmc.gameassets.animations.stun.CustomStunAnimations;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.dmc.invincible_dmc.utils.vfx.CameraFovUtil;
import com.dmc.invincible_dmc.utils.vfx.CinematicBarsUtils;
import com.dmc.invincible_dmc.utils.vfx.LocalScreenEffectGate;
import com.dmc.invincible_dmc.utils.yamato.CameraLockUtil;
import java.util.Set;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.property.AnimationEvent.InPeriodEvent;
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
import yesman.epicfight.api.utils.HitEntityList.Priority;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.StunType;

public class YamatoExecutionAnimationBuilder {
   static void build(AnimationBuilder builder) {
      YamatoAnimations.YAMATO_EXECUTION_ALL = builder.nextAccessor(
         "biped/yamato/attack/yamato_execution_all",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                  0.1F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  CustomStunAnimationUtils.createCustomStunPhase(
                     47, 52, 196, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  ),
                  CustomStunAnimationUtils.createCustomStunPhase(
                     197, 206, 347, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               )
               .<CustomStunAttackAnimation>addStrongStunAnimation(0, CustomStunAnimations.HIT_EXECUTED_BEGIN, CustomStunAnimations.HIT_EXECUTED_BEGIN)
               .<CustomStunAttackAnimation>addStrongStunAnimation(1, CustomStunAnimations.HIT_EXECUTED_END, CustomStunAnimations.HIT_EXECUTED_END)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get(), 0)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.EVISCERATE.get(), 1)
               .addProperty(AttackAnimationProperty.REACH, 1.0F)
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
               .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addProperty(YamatoAttackAnimation.JC_RELEASE_BLOCK_TIME, TimePairList.create(new float[]{0.0F, 5.3333335F}))
               .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.16666667F, 5.7833333F}))
               .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{5.3F, 5.7833333F}))
               .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 5.7833333F)
               .addProperty(YamatoAttackAnimation.USE_MESH2_IN_LINK, true)
               .addEvents(
                  ActionAnimationProperty.ON_END_EVENTS,
                  new AnimationEvent[]{
                     SimpleEvent.create(
                        (livingEntityPatch, staticAnimation, objects) -> StrongStunController.finishOwnedTargets(
                              (LivingEntity)livingEntityPatch.getOriginal(), "execution_all_finished"
                           ),
                        Side.SERVER
                     )
                  }
               )
               .addEvents(
                  new AnimationEvent[]{
                     InPeriodEvent.create(
                        0.75F, 1.1666666F, (livingEntityPatch, assetAccessor, animationParameters) -> playExecutionLightRing(livingEntityPatch), Side.CLIENT
                     )
                  }
               )
      );
      YamatoAnimations.YAMATO_EXECUTION_DASH = builder.nextAccessor(
         "biped/yamato/attack/yamato_execution_dash",
         accessor -> (YamatoExecutionAnimation)new YamatoExecutionAnimation(
                  0.1F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  YamatoExecutionAnimation.Stage.DASH_GRAB,
                  YamatoAnimations.EXECUTION_LINE_COLLIDER,
                  () -> YamatoAnimations.YAMATO_EXECUTION_END,
                  CustomStunAnimationUtils.createCustomStunPhase(
                     55, 80, 158, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.EXECUTION_DASH
                  )
               )
               .<CustomStunAttackAnimation>addStrongStunAnimation(0, CustomStunAnimations.HIT_EXECUTED_BEGIN, CustomStunAnimations.HIT_EXECUTED_BEGIN)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get(), 0)
               .addProperty(
                  AttackPhaseProperty.SOURCE_TAG,
                  Set.of(
                     EpicFightDamageTypeTags.FINISHER,
                     EpicFightDamageTypeTags.UNBLOCKALBE,
                     EpicFightDamageTypeTags.GUARD_PUNCTURE,
                     CombatEvolutionDamageTypeTags.EXECUTION
                  ),
                  0
               )
               .addProperty(AttackPhaseProperty.HIT_PRIORITY, Priority.TARGET)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1.0F))
               .addProperty(ActionAnimationProperty.REMOVE_DELTA_MOVEMENT, true)
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
               .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addProperty(CustomStunAttackAnimation.HOLD_LEDGE, true)
               .addProperty(YamatoAttackAnimation.JC_RELEASE_BLOCK_TIME, TimePairList.create(new float[]{0.0F, 2.1666667F}))
               .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.3F, 2.6333334F}))
               .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{1.8666667F, 2.6333334F}))
               .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 1.5833334F)
               .addProperty(YamatoAttackAnimation.USE_MESH2_IN_LINK, true)
               .addEvents(
                  StaticAnimationProperty.ON_BEGIN_EVENTS,
                  new AnimationEvent[]{
                     SimpleEvent.create((livingEntityPatch, assetAccessor, animationParameters) -> CameraLockUtil.startLockOn(), Side.LOCAL_CLIENT)
                  }
               )
               .addEvents(
                  StaticAnimationProperty.ON_BEGIN_EVENTS,
                  new AnimationEvent[]{
                     SimpleEvent.create(
                        (livingEntityPatch, assetAccessor, animationParameters) -> livingEntityPatch.playSound(SoundEvents.f_12554_, 1.0F, 1.0F), Side.SERVER
                     )
                  }
               )
               .addEvents(ActionAnimationProperty.ON_END_EVENTS, new AnimationEvent[]{SimpleEvent.create((livingEntityPatch, staticAnimation, objects) -> {
                  CinematicBarsUtils.close();
                  CameraFovUtil.stopZoom();
                  CameraLockUtil.endLockOn();
               }, Side.LOCAL_CLIENT)})
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.3F,
                        (livingEntityPatch, assetAccessor, animationParameters) -> {
                           LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
                           ExecuteEffek.playExecute(
                              ExecuteEffek.Type.LEVEL1,
                              livingEntity.m_9236_(),
                              livingEntity.m_20185_(),
                              livingEntity.m_20186_() + 0.1,
                              livingEntity.m_20189_(),
                              0.75F
                           );
                        },
                        Side.CLIENT
                     ),
                     InTimeEvent.create(0.8F, (livingEntityPatch, assetAccessor, animationParameters) -> CameraLockUtil.endLockOn(), Side.LOCAL_CLIENT),
                     InTimeEvent.create(0.95F, (livingEntityPatch, assetAccessor, animationParameters) -> {
                        if (livingEntityPatch instanceof PlayerPatch<?> playerPatch) {
                           LivingEntity player = (LivingEntity)playerPatch.getOriginal();
                           CameraShakeManager.addShake(player.m_146892_(), 8.0F, 3, 4.0F);
                        }
                     }, Side.LOCAL_CLIENT),
                     InPeriodEvent.create(
                        0.75F, 1.1666666F, (livingEntityPatch, assetAccessor, animationParameters) -> playExecutionLightRing(livingEntityPatch), Side.CLIENT
                     )
                  }
               )
      );
      YamatoAnimations.YAMATO_EXECUTION_END = builder.nextAccessor(
         "biped/yamato/attack/yamato_execution_end",
         accessor -> {
            CustomStunPhase executionPhase = CustomStunAnimationUtils.createCustomStunPhase(
               149, 159, 295, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.EXECUTION_FINISHER
            );
            return (YamatoExecutionAnimation)new YamatoExecutionAnimation(
                  0.05F, accessor, Armatures.BIPED, 1.0F, 1.0F, YamatoExecutionAnimation.Stage.FINISH_WITHDRAWAL, null, null, executionPhase
               )
               .<CustomStunAttackAnimation>addStrongStunAnimation(0, CustomStunAnimations.HIT_EXECUTED_END, CustomStunAnimations.HIT_EXECUTED_END)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.EVISCERATE.get(), 0)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1.0F))
               .addProperty(
                  AttackPhaseProperty.SOURCE_TAG,
                  Set.of(
                     EpicFightDamageTypeTags.FINISHER,
                     EpicFightDamageTypeTags.UNBLOCKALBE,
                     EpicFightDamageTypeTags.GUARD_PUNCTURE,
                     CombatEvolutionDamageTypeTags.EXECUTION,
                     CombatEvolutionDamageTypeTags.EXECUTION_FINISHED
                  ),
                  0
               )
               .addProperty(ActionAnimationProperty.REMOVE_DELTA_MOVEMENT, true)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addProperty(YamatoAttackAnimation.JC_RELEASE_BLOCK_TIME, TimePairList.create(new float[]{0.0F, 4.5F}))
               .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.0F, 4.9166665F}))
               .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{4.4666667F, 4.9166665F}))
               .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 3.0F)
               .addProperty(YamatoAttackAnimation.CAN_DODGE_TIME, TimePairList.create(new float[]{2.4833333F, 2.1474836E9F}))
               .addProperty(YamatoAttackAnimation.USE_MESH2_IN_LINK, true)
               .newTimePair(0.0F, 4.9166665F)
               .addStateRemoveOld(EntityState.LOCKON_ROTATE, false)
               .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, YamatoAnimations.INVINCIBLE_SOURCE_VALIDATOR)
               .addEvents(ActionAnimationProperty.ON_BEGIN_EVENTS, new AnimationEvent[]{SimpleEvent.create((livingEntityPatch, staticAnimation, objects) -> {
               }, Side.LOCAL_CLIENT)})
               .addStateRemoveOld(EntityState.PROJECTILE_IMPACT_RESULT, DodgeAnimation.IGNORE_ALL_PROJECTILES)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.083333336F, (livingEntityPatch, assetAccessor, animationParameters) -> {
                        LocalScreenEffectGate.pushNearbyAdditive(livingEntityPatch, 24.0, new ImpactBlurEffect(2.0F, 50));
                        CinematicBarsUtils.openFor(2.5F, 2.0F, 4.0F, 0.09F);
                        CameraFovUtil.triggerZoom(4, 40, 4, 0.55F, CameraFovUtil.EaseType.SINE, CameraFovUtil.EaseType.INSTANT, 5);
                     }, Side.LOCAL_CLIENT),
                     InTimeEvent.create(0.083333336F, (livingEntityPatch, assetAccessor, animationParameters) -> {
                        YamatoExecutionAnimation anim = DMCAnimationUtils.asAnimation(
                           DMCAnimationUtils.getAnimation(assetAccessor), YamatoExecutionAnimation.class
                        );
                        if (anim != null) {
                           anim.onSecondaryPositionCorrection(livingEntityPatch);
                        }
                     }, Side.SERVER),
                     InTimeEvent.create(
                        2.0833333F,
                        (livingEntityPatch, assetAccessor, animationParameters) -> livingEntityPatch.playSound(SoundEvents.f_215762_, 1.0F, 1.0F),
                        Side.SERVER
                     ),
                     InTimeEvent.create(2.5333333F, (livingEntityPatch, assetAccessor, animationParameters) -> {
                        if (livingEntityPatch instanceof PlayerPatch<?> playerPatch) {
                           CameraShakeManager.addShake(((Player)playerPatch.getOriginal()).m_146892_(), 8.0F, 3, 4.0F);
                           LocalScreenEffectGate.pushNearbyAdditive(livingEntityPatch, 24.0, new ImpactBlurEffect(7.0F, 15));
                        }
                     }, Side.LOCAL_CLIENT)
                  }
               )
               .addEvents(ActionAnimationProperty.ON_END_EVENTS, new AnimationEvent[]{SimpleEvent.create((livingEntityPatch, staticAnimation, objects) -> {
                  CinematicBarsUtils.close();
                  CameraFovUtil.stopZoom();
               }, Side.LOCAL_CLIENT)})
               .newTimePair(0.9166667F, 1.3333334F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, YamatoAnimations.INVINCIBLE_SOURCE_VALIDATOR)
               .addStateRemoveOld(EntityState.PROJECTILE_IMPACT_RESULT, DodgeAnimation.IGNORE_ALL_PROJECTILES);
         }
      );
   }

   private static void playExecutionLightRing(LivingEntityPatch<?> livingEntityPatch) {
      LivingEntity entity = (LivingEntity)livingEntityPatch.getOriginal();
      Vec3 boundingBoxCenter = entity.m_20191_().m_82399_();
      LightRingEffek.playLightRing(
         LightRingEffek.Type.LEVEL1, entity.m_9236_(), boundingBoxCenter.f_82479_, boundingBoxCenter.f_82480_, boundingBoxCenter.f_82481_, 1.0F
      );
   }
}
