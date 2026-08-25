package com.dmc.invincible_dmc.gameassets.animations.yamato;

import com.dmc.invincible_dmc.api.animation.types.customStun.CustomStunAnimationUtils;
import com.dmc.invincible_dmc.api.animation.types.customStun.CustomStunAttackAnimation;
import com.dmc.invincible_dmc.api.animation.types.customStun.CustomStunPhase;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.client.render.afterimage.PerFrameAfterimageRenderer;
import com.dmc.invincible_dmc.client.render.shake.CameraShakeManager;
import com.dmc.invincible_dmc.client.sound.DMCSounds;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerPatch;
import com.dmc.invincible_dmc.entity.judgementcut.JudgementCutEntity;
import com.dmc.invincible_dmc.entity.summonedsword.SummonedSwordSpawner;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import java.util.List;
import java.util.Set;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
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
import yesman.epicfight.api.animation.types.BasicAttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.StunType;

final class YamatoSpecialAttackAnimationBuilder {
   private YamatoSpecialAttackAnimationBuilder() {
   }

   static void build(AnimationBuilder builder) {
      YamatoAnimations.YAMATO_RAPIDSLASH = builder.nextAccessor(
         "biped/yamato/attack/yamato_rapidslash",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_RAPIDSLASH",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        2, 24, 30, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.RAPIDSLASH
                     ),
                     CustomStunAnimationUtils.createCustomStunPhase(
                        31,
                        41,
                        130,
                        InteractionHand.MAIN_HAND,
                        1.0F,
                        1.0F,
                        ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                        YamatoAnimations.RAPIDSLASH_END
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.SLOW_PERSISTENT), 0)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_4.get(), 0)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get(), 1)
                  .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.RAPID_SLASH, YamatoAnimations.CRAZY_COMBO_FINISH), 0)
                  .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.CRAZY_COMBO_FINISH), 1)
                  .addProperty(AttackAnimationProperty.REACH, 1.0F)
                  .addProperty(ActionAnimationProperty.AFFECT_SPEED, false)
                  .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoord(1.15F, 1.0F))
                  .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
                  .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
                  .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
                  .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
                  .addProperty(ActionAnimationProperty.MOVE_ON_LINK, true)
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.45F, 1.6666666F}))
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{1.4F, 1.6666666F}))
                  .addProperty(YamatoAttackAnimation.SDT_IN_SHEATH_TIME, TimePairList.create(new float[]{1.6666666F, 1.8333334F}))
                  .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> {
                     if (livingEntityPatch instanceof PlayerPatch<?> playerPatch && SinDevilTriggerManager.isPlayerInSDT((Player)playerPatch.getOriginal())) {
                        return 1.025F;
                     }

                     if (livingEntityPatch instanceof DoppelgangerPatch doppelgangerPatch) {
                        PlayerPatch<?> ownerPatch = doppelgangerPatch.getOwnerPatch();
                        if (ownerPatch != null && SinDevilTriggerManager.isPlayerInSDT((Player)ownerPatch.getOriginal())) {
                           return 1.025F;
                        }
                     }

                     return 1.0F;
                  })
                  .addProperty(CustomStunAttackAnimation.HOLD_LEDGE, true)
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 1.0F)
                  .addProperty(AttackAnimationProperty.SYNC_CAMERA, true)
                  .addProperty(YamatoAttackAnimation.CORRECT_YROT_TO_CAMERA, true)
                  .addProperty(ActionAnimationProperty.MOVE_ON_LINK, false)
                  .newTimePair(0.0F, 2.1666667F)
                  .addStateRemoveOld(EntityState.LOCKON_ROTATE, false)
                  .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
                  .addEvents(
                     new AnimationEvent[]{
                        InTimeEvent.create(0.38333333F, (livingEntityPatch, assetAccessor, animationParameters) -> {
                           if (livingEntityPatch instanceof PlayerPatch<?> playerPatch) {
                              CameraShakeManager.addShake(((Player)playerPatch.getOriginal()).m_146892_(), 8.0F, 3, 4.0F);
                           }
                        }, Side.LOCAL_CLIENT),
                        YamatoVfxUtils.summonRapidSlash(2),
                        YamatoVfxUtils.summonRapidSlash(9),
                        YamatoVfxUtils.summonRapidSlash(17),
                        PerFrameAfterimageRenderer.inPeriodEvent(0.0F, 0.38333333F, PerFrameAfterimageRenderer::dash, Side.CLIENT),
                        PerFrameAfterimageRenderer.stopInTimeEvent(0.38333333F, Side.CLIENT)
                     }
                  )
                  .addEvents(
                     ActionAnimationProperty.ON_END_EVENTS,
                     new AnimationEvent[]{
                        SimpleEvent.create((livingEntityPatch, staticAnimation, objects) -> PerFrameAfterimageRenderer.stop(livingEntityPatch), Side.CLIENT)
                     }
                  )
                  .addEvents(
                     ActionAnimationProperty.ON_END_EVENTS,
                     new AnimationEvent[]{
                        SimpleEvent.create(
                           (livingEntityPatch, staticAnimation, objects) -> JudgementCutEntity.discardAllOwnedBy((LivingEntity)livingEntityPatch.getOriginal()),
                           Side.SERVER
                        )
                     }
                  )
                  .addEvents(
                     ActionAnimationProperty.ON_BEGIN_EVENTS,
                     new AnimationEvent[]{
                        SimpleEvent.create(
                           (livingEntityPatch, staticAnimation, objects) -> {
                              if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch
                                 && SinDevilTriggerManager.isPlayerInSDT((Player)serverPlayerPatch.getOriginal())) {
                                 SummonedSwordSpawner.summonImpale(serverPlayerPatch);
                              }

                              JudgementCutEntity.discardAllOwnedBy((LivingEntity)livingEntityPatch.getOriginal());
                           },
                           Side.SERVER
                        )
                     }
                  )
         )
      );
      YamatoAnimations.YAMATO_RAPIDSLASH_RE = builder.nextAccessor(
         "biped/yamato/attack/yamato_rapidslash_re",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_RAPIDSLASH_RE",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        0, 15, 21, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.RAPIDSLASH
                     ),
                     CustomStunAnimationUtils.createCustomStunPhase(
                        22,
                        32,
                        121,
                        InteractionHand.MAIN_HAND,
                        1.0F,
                        1.0F,
                        ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                        YamatoAnimations.RAPIDSLASH_END
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.SLOW_PERSISTENT), 0)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_4.get(), 0)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get(), 1)
                  .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.RAPID_SLASH, YamatoAnimations.CRAZY_COMBO_FINISH), 0)
                  .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.CRAZY_COMBO_FINISH), 1)
                  .addProperty(AttackAnimationProperty.REACH, 1.0F)
                  .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoord(1.15F, 1.0F))
                  .addProperty(ActionAnimationProperty.AFFECT_SPEED, false)
                  .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
                  .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
                  .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
                  .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
                  .addProperty(ActionAnimationProperty.MOVE_ON_LINK, true)
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.3F, 1.5166667F}))
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{1.25F, 1.5166667F}))
                  .addProperty(YamatoAttackAnimation.SDT_IN_SHEATH_TIME, TimePairList.create(new float[]{1.5166667F, 1.6833333F}))
                  .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> {
                     if (livingEntityPatch instanceof PlayerPatch<?> playerPatch && SinDevilTriggerManager.isPlayerInSDT((Player)playerPatch.getOriginal())) {
                        return 1.025F;
                     }

                     if (livingEntityPatch instanceof DoppelgangerPatch doppelgangerPatch) {
                        PlayerPatch<?> ownerPatch = doppelgangerPatch.getOwnerPatch();
                        if (ownerPatch != null && SinDevilTriggerManager.isPlayerInSDT((Player)ownerPatch.getOriginal())) {
                           return 1.025F;
                        }
                     }

                     return 1.0F;
                  })
                  .addProperty(CustomStunAttackAnimation.HOLD_LEDGE, true)
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 0.8666667F)
                  .addProperty(ActionAnimationProperty.MOVE_ON_LINK, false)
                  .newTimePair(0.0F, 2.0166667F)
                  .addStateRemoveOld(EntityState.LOCKON_ROTATE, false)
                  .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
                  .addEvents(
                     new AnimationEvent[]{
                        InTimeEvent.create(0.23333333F, (livingEntityPatch, assetAccessor, animationParameters) -> {
                           if (livingEntityPatch instanceof PlayerPatch<?> playerPatch) {
                              CameraShakeManager.addShake(((Player)playerPatch.getOriginal()).m_146892_(), 8.0F, 3, 4.0F);
                           }
                        }, Side.LOCAL_CLIENT),
                        YamatoVfxUtils.summonRapidSlash(0),
                        YamatoVfxUtils.summonRapidSlash(8),
                        PerFrameAfterimageRenderer.inPeriodEvent(0.0F, 0.23333333F, PerFrameAfterimageRenderer::dash, Side.CLIENT),
                        PerFrameAfterimageRenderer.stopInTimeEvent(0.23333333F, Side.CLIENT)
                     }
                  )
                  .addEvents(
                     ActionAnimationProperty.ON_END_EVENTS,
                     new AnimationEvent[]{
                        SimpleEvent.create((livingEntityPatch, staticAnimation, objects) -> PerFrameAfterimageRenderer.stop(livingEntityPatch), Side.CLIENT)
                     }
                  )
                  .addEvents(
                     ActionAnimationProperty.ON_END_EVENTS,
                     new AnimationEvent[]{
                        SimpleEvent.create(
                           (livingEntityPatch, staticAnimation, objects) -> JudgementCutEntity.discardAllOwnedBy((LivingEntity)livingEntityPatch.getOriginal()),
                           Side.SERVER
                        )
                     }
                  )
                  .addEvents(
                     ActionAnimationProperty.ON_BEGIN_EVENTS,
                     new AnimationEvent[]{
                        SimpleEvent.create(
                           (livingEntityPatch, staticAnimation, objects) -> JudgementCutEntity.discardAllOwnedBy((LivingEntity)livingEntityPatch.getOriginal()),
                           Side.SERVER
                        )
                     }
                  )
         )
      );
      YamatoAnimations.YAMATO_RAPIDSLASH_AIR = builder.nextAccessor(
         "biped/yamato/attack/yamato_rapidslash_air",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_RAPIDSLASH_AIR",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        10, 25, 80, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.RAPIDSLASH
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.SLOW_PERSISTENT), 0)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_4.get(), 0)
                  .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.RAPID_SLASH), 0)
                  .addProperty(AttackAnimationProperty.REACH, 1.0F)
                  .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
                  .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 1.5F}))
                  .addProperty(ActionAnimationProperty.AFFECT_SPEED, false)
                  .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoord(0.5F, 1.0F))
                  .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
                  .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
                  .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
                  .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
                  .addProperty(ActionAnimationProperty.MOVE_ON_LINK, true)
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.083333336F, 1.3333334F}))
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{1.0F, 1.3333334F}))
                  .addProperty(YamatoAttackAnimation.SDT_IN_SHEATH_TIME, TimePairList.create(new float[]{0.083333336F, 1.3333334F}))
                  .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> {
                     if (livingEntityPatch instanceof PlayerPatch<?> playerPatch && SinDevilTriggerManager.isPlayerInSDT((Player)playerPatch.getOriginal())) {
                        return 1.025F;
                     }

                     if (livingEntityPatch instanceof DoppelgangerPatch doppelgangerPatch) {
                        PlayerPatch<?> ownerPatch = doppelgangerPatch.getOwnerPatch();
                        if (ownerPatch != null && SinDevilTriggerManager.isPlayerInSDT((Player)ownerPatch.getOriginal())) {
                           return 1.025F;
                        }
                     }

                     return 1.0F;
                  })
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 0.6666667F)
                  .addProperty(AttackAnimationProperty.SYNC_CAMERA, true)
                  .addProperty(YamatoAttackAnimation.CORRECT_YROT_TO_CAMERA, true)
                  .addProperty(ActionAnimationProperty.MOVE_ON_LINK, false)
                  .newTimePair(0.0F, 2.0F)
                  .addStateRemoveOld(EntityState.LOCKON_ROTATE, false)
                  .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
                  .addEvents(
                     new AnimationEvent[]{
                        YamatoVfxUtils.summonJudgementCut(2, false),
                        InTimeEvent.create(
                           0.05F, (patch, anim, params) -> patch.playSound((SoundEvent)DMCSounds.JUDGEMENT_CUT_JUST.get(), 1.0F, 1.0F), Side.SERVER
                        )
                     }
                  )
         )
      );
      YamatoAnimations.YAMATO_RISINGSTAR = builder.nextAccessor(
         "biped/yamato/attack/yamato_rising_star",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_RISINGSTAR",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.1F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        0, 7, 18, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.RISINGSTAR
                     ),
                     CustomStunAnimationUtils.createCustomStunPhase(
                        19, 31, 73, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.RISINGSTAR
                     )
                  )
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_16.get(), 0)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_17.get(), 1)
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.0F, 1.2166667F}))
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{1.0F, 1.2166667F}))
                  .addProperty(AttackAnimationProperty.REACH, 1.0F)
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
                  .addProperty(ActionAnimationProperty.MOVE_ON_LINK, false)
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 1.0F)
                  .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
                  .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 1.2166667F}))
                  .addProperty(YamatoAttackAnimation.YAMATO_NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 1.2166667F}))
                  .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoord(1.0F, 0.8F))
                  .addEvents(
                     ActionAnimationProperty.ON_BEGIN_EVENTS,
                     new AnimationEvent[]{
                        SimpleEvent.create(
                           (livingEntityPatch, staticAnimation, objects) -> JudgementCutEntity.discardAllOwnedBy((LivingEntity)livingEntityPatch.getOriginal()),
                           Side.SERVER
                        )
                     }
                  )
                  .newTimePair(0.0F, 1.0F)
                  .addStateRemoveOld(EntityState.LOCKON_ROTATE, false)
         )
      );
      YamatoAnimations.YAMATO_VOID_SLASH = builder.nextAccessor(
         "biped/yamato/attack/yamato_voidslash",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_VOID_SLASH",
            accessor -> {
               final CustomStunPhase voidSlashPhase = CustomStunAnimationUtils.createCustomStunPhase(
                  50, 67, 180, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.VOID_SLASH
               );
               return (YamatoAttackAnimation)(new YamatoAttackAnimation(0.05F, accessor, Armatures.BIPED, 1.0F, 1.0F, new CustomStunPhase[]{voidSlashPhase}) {
                     protected void attackTick(LivingEntityPatch<?> entitypatch, AssetAccessor<? extends DynamicAnimation> animation) {
                        AnimationPlayer player = DMCAnimationUtils.getPlayerFor(entitypatch, this.getAccessor());
                        float previousTime = 0.0F;
                        if (player != null) {
                           previousTime = player.getPrevElapsedTime();
                        }

                        float elapsedTime = 0.0F;
                        if (player != null) {
                           elapsedTime = player.getElapsedTime();
                        }

                        super.attackTick(entitypatch, animation);
                        if (YamatoSpecialAttackAnimationBuilder.isInSinDevilTrigger(entitypatch)
                           && !entitypatch.isLogicalClient()
                           && !(previousTime >= voidSlashPhase.preDelay)
                           && !(elapsedTime < voidSlashPhase.preDelay)) {
                           DynamicAnimation dynamicAnimation = (DynamicAnimation)animation.get();
                           EntityState previousState = dynamicAnimation.getState(entitypatch, previousTime);
                           EntityState currentState = dynamicAnimation.getState(entitypatch, elapsedTime);
                           if (previousState.attacking() || currentState.attacking() || previousState.getLevel() <= 2 && currentState.getLevel() > 2) {
                              List<Entity> triedEntitiesBeforeExtra = List.copyOf(entitypatch.getCurrentlyAttackTriedEntities());
                              List<LivingEntity> hitEntitiesBeforeExtra = List.copyOf(entitypatch.getCurrentlyActuallyHitEntities());
                              entitypatch.removeHurtEntities();
                              voidSlashPhase.resetAttackRecord(entitypatch);
                              this.hurtCollidingEntities(entitypatch, previousTime, elapsedTime, previousState, currentState, voidSlashPhase);
                              triedEntitiesBeforeExtra.forEach(entity -> {
                                 if (!entitypatch.getCurrentlyAttackTriedEntities().contains(entity)) {
                                    entitypatch.getCurrentlyAttackTriedEntities().add(entity);
                                 }
                              });
                              hitEntitiesBeforeExtra.forEach(entity -> {
                                 if (!entitypatch.getCurrentlyActuallyHitEntities().contains(entity)) {
                                    entitypatch.getCurrentlyActuallyHitEntities().add(entity);
                                 }
                              });
                           }
                        }
                     }
                  })
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get())
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                  .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.CRAZY_COMBO_FINISH), 0)
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.21666667F, 2.8333333F}))
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{0.16666667F, 0.8333333F, 2.3F, 2.8333333F}))
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
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 2.5F)
                  .addProperty(AttackAnimationProperty.SYNC_CAMERA, true)
                  .addProperty(YamatoAttackAnimation.USE_MESH2_IN_LINK, false)
                  .addProperty(ActionAnimationProperty.MOVE_ON_LINK, false)
                  .addEvents(
                     new AnimationEvent[]{
                        InTimeEvent.create(
                           0.2F,
                           (livingEntityPatch, assetAccessor, animationParameters) -> livingEntityPatch.playSound(
                                 (SoundEvent)DMCSounds.YAMATO_VOIDSLASH_1.get(), 1.0F, 1.0F, 1.0F
                              ),
                           Side.SERVER
                        ),
                        InTimeEvent.create(
                           2.9833333F,
                           (livingEntityPatch, assetAccessor, animationParameters) -> livingEntityPatch.playSound(SoundEvents.f_11983_, 1.3F, 1.0F, 1.0F),
                           Side.SERVER
                        )
                     }
                  );
            }
         )
      );
      YamatoAnimations.YAMATO_UPPERSLASH_1 = builder.nextAccessor(
         "biped/yamato/attack/yamato_upperslash_1",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_UPPERSLASH_1",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        21, 33, 70, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, YamatoAnimations.BLADE_COLLIDER_EX
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_3.get())
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                  .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.CRAZY_COMBO_FINISH), 0)
                  .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.31666666F, 1.7833333F}))
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{0.9166667F, 1.7833333F}))
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
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 0.95F)
                  .addProperty(AttackAnimationProperty.SYNC_CAMERA, true)
                  .addProperty(ActionAnimationProperty.MOVE_ON_LINK, false)
                  .addEvents(
                     new AnimationEvent[]{
                        InTimeEvent.create(
                           0.38333333F,
                           (livingEntityPatch, assetAccessor, animationParameters) -> CameraShakeManager.addShake(
                                 ((LivingEntity)livingEntityPatch.getOriginal()).m_146892_(), 6.0F, 2, 1.0F
                              ),
                           Side.LOCAL_CLIENT
                        )
                     }
                  )
         )
      );
      YamatoAnimations.YAMATO_UPPERSLASH_2 = builder.nextAccessor(
         "biped/yamato/attack/yamato_upperslash_2",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_UPPERSLASH_2",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        0, 9, 36, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, YamatoAnimations.BLADE_COLLIDER_EX
                     )
                  )
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.CRAZY_COMBO_FINISH), 0)
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.0F, 0.46666667F}))
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{0.26666668F, 0.46666667F}))
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
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 0.33333334F)
                  .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
                  .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.65F}))
                  .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoord(1.0F, 1.25F))
                  .addProperty(YamatoAttackAnimation.USE_MESH2_IN_LINK, true)
                  .addEvents(
                     new AnimationEvent[]{
                        InTimeEvent.create(
                           0.033333335F,
                           (livingEntityPatch, assetAccessor, animationParameters) -> CameraShakeManager.addShake(
                                 ((LivingEntity)livingEntityPatch.getOriginal()).m_146892_(), 6.0F, 2, 1.0F
                              ),
                           Side.LOCAL_CLIENT
                        )
                     }
                  )
                  .addProperty(
                     StaticAnimationProperty.ON_BEGIN_EVENTS,
                     List.of(SimpleEvent.create((ep, anim, objs) -> ep.playSound((SoundEvent)DMCSounds.YAMATO_SWING_3.get(), 1.0F, 1.0F, 1.0F), Side.SERVER))
                  )
         )
      );
   }

   private static boolean isInSinDevilTrigger(LivingEntityPatch<?> entitypatch) {
      if (entitypatch instanceof PlayerPatch<?> playerPatch) {
         return SinDevilTriggerManager.isPlayerInSDT((Player)playerPatch.getOriginal());
      } else if (!(entitypatch instanceof DoppelgangerPatch doppelgangerPatch)) {
         return false;
      } else {
         PlayerPatch<?> ownerPatch = doppelgangerPatch.getOwnerPatch();
         return ownerPatch != null && SinDevilTriggerManager.isPlayerInSDT((Player)ownerPatch.getOriginal());
      }
   }
}
