package com.dmc.invincible_dmc.gameassets.animations.yamato;

import com.dmc.invincible_dmc.api.animation.types.customStun.CustomStunAnimationUtils;
import com.dmc.invincible_dmc.api.animation.types.customStun.CustomStunAttackAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.client.sound.DMCSounds;
import com.dmc.invincible_dmc.entity.DMCEntities;
import com.dmc.invincible_dmc.entity.portal.PortalEntity;
import com.dmc.invincible_dmc.entity.summonedsword.SummonedSwordSpawner;
import com.dmc.invincible_dmc.utils.vfx.CameraFovUtil;
import com.dmc.invincible_dmc.utils.vfx.CinematicBarsUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.StunType;

final class YamatoProvocationAnimationBuilder {
   private YamatoProvocationAnimationBuilder() {
   }

   static void build(AnimationBuilder builder) {
      YamatoAnimations.YAMATO_PROVOCATION_A = builder.nextAccessor(
         "biped/yamato/provocation/yamato_provocation_a",
         accessor -> (ActionAnimation)new ActionAnimation(0.1F, Float.MAX_VALUE, accessor, Armatures.BIPED)
               .addProperty(ActionAnimationProperty.MOVE_ON_LINK, false)
               .addProperty(YamatoAttackAnimation.NOT_USE_SUMMON_DOPPELGANGER_ANIM, true)
               .addProperty(YamatoAttackAnimation.JUMP_CANCEL_TIME, TimePairList.create(new float[]{0.1F, Float.MAX_VALUE}))
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
               .addProperty(ActionAnimationProperty.REMOVE_DELTA_MOVEMENT, true)
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
               .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(3.6666667F, (ep, anim, objs) -> {
                  if (ep instanceof ServerPlayerPatch serverPlayerPatch) {
                     SummonedSwordSpawner.summonImpale(serverPlayerPatch, 5);
                  }
               }, Side.SERVER)})
      );
      YamatoAnimations.YAMATO_PROVOCATION_A_AERIAL = builder.nextAccessor(
         "biped/yamato/provocation/yamato_provocation_a_aerial",
         accessor -> (ActionAnimation)new ActionAnimation(0.1F, Float.MAX_VALUE, accessor, Armatures.BIPED)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.MOVE_ON_LINK, true)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 1.5F}))
               .addProperty(ActionAnimationProperty.MOVE_ON_LINK, false)
               .addProperty(YamatoAttackAnimation.NOT_USE_SUMMON_DOPPELGANGER_ANIM, true)
               .addProperty(YamatoAttackAnimation.JUMP_CANCEL_TIME, TimePairList.create(new float[]{0.8333333F, Float.MAX_VALUE}))
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
               .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
               .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoordWithInput(0.04F))
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, CustomStunAttackAnimation.AERIALRAVE_COMB_DIRECTION_MODIFIER)
               .newTimePair(0.0F, 0.5F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.4F, (ep, anim, objs) -> SummonedSwordSpawner.provocation(((LivingEntity)ep.getOriginal()).m_9236_(), ep.getOriginal()), Side.SERVER
                     )
                  }
               )
      );
      YamatoAnimations.YAMATO_PROVOCATION_B = builder.nextAccessor(
         "biped/yamato/provocation/yamato_provocation_b",
         accessor -> (ActionAnimation)new ActionAnimation(0.1F, Float.MAX_VALUE, accessor, Armatures.BIPED)
               .addProperty(ActionAnimationProperty.MOVE_ON_LINK, false)
               .addProperty(YamatoAttackAnimation.NOT_USE_SUMMON_DOPPELGANGER_ANIM, true)
               .addProperty(YamatoAttackAnimation.JUMP_CANCEL_TIME, TimePairList.create(new float[]{0.1F, Float.MAX_VALUE}))
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
               .addProperty(ActionAnimationProperty.REMOVE_DELTA_MOVEMENT, true)
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
               .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
      );
      YamatoAnimations.YAMATO_PROVOCATION_SPINE_BLADE = builder.nextAccessor(
         "biped/yamato/provocation/yamato_provocation_spine_blade",
         accessor -> (ActionAnimation)new ActionAnimation(0.1F, Float.MAX_VALUE, accessor, Armatures.BIPED)
               .addProperty(ActionAnimationProperty.MOVE_ON_LINK, false)
               .addProperty(YamatoAttackAnimation.NOT_USE_SUMMON_DOPPELGANGER_ANIM, true)
               .addProperty(YamatoAttackAnimation.JUMP_CANCEL_TIME, TimePairList.create(new float[]{0.1F, Float.MAX_VALUE}))
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
               .addProperty(ActionAnimationProperty.REMOVE_DELTA_MOVEMENT, true)
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
               .addProperty(AttackAnimationProperty.SYNC_CAMERA, true)
               .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.9F, (ep, anim, objs) -> SummonedSwordSpawner.spine(ep.getOriginal()), Side.SERVER)})
               .newTimePair(0.0F, 4.0F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, true)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, true)
      );
      YamatoAnimations.YAMATO_PROVOCATION_B_AERIAL = builder.nextAccessor(
         "biped/yamato/provocation/yamato_provocation_b_aerial",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_PROVOCATION_B_AERIAL",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.05F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        7, 11, 60, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.VOID_SLASH
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.JUDGEMENT_CUT_SWING.get())
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.1F, 0.33333334F}))
                  .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 0.33333334F)
                  .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
                  .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.75F}))
                  .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoordWithInput(0.04F))
                  .addProperty(YamatoAttackAnimation.YAMATO_NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.75F}))
                  .addProperty(AttackAnimationProperty.SYNC_CAMERA, true)
                  .addProperty(YamatoAttackAnimation.USE_MESH2_IN_LINK, false)
                  .addProperty(StaticAnimationProperty.POSE_MODIFIER, CustomStunAttackAnimation.AERIALRAVE_COMB_DIRECTION_MODIFIER)
                  .setResourceLocation("invincible_dmc", "biped/yamato/attack/yamato_judgement_cut_air")
                  .addEvents(
                     ActionAnimationProperty.ON_BEGIN_EVENTS,
                     new AnimationEvent[]{
                        SimpleEvent.create((ep, anim, objs) -> ep.playSound((SoundEvent)DMCSounds.JUDGEMENT_CUT_BEGIN.get(), 1.0F, 1.0F), Side.SERVER)
                     }
                  )
         )
      );
      YamatoAnimations.YAMATO_PROVOCATION_C = builder.nextAccessor(
         "biped/yamato/provocation/yamato_provocation_c",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_PROVOCATION_C",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.1F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        811, 823, 1024, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.VOID_SLASH
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
                  .addProperty(YamatoAttackAnimation.SDT_IN_SHEATH_TIME, TimePairList.create(new float[]{1.4166666F, 4.9166665F}))
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{4.9333334F, 16.833334F}))
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{15.65F, 16.833334F}))
                  .addProperty(YamatoAttackAnimation.USE_MESH2_IN_LINK, false)
                  .addProperty(YamatoAttackAnimation.NOT_USE_SUMMON_DOPPELGANGER_ANIM, true)
                  .addProperty(YamatoAttackAnimation.JUMP_CANCEL_TIME, TimePairList.create(new float[]{0.1F, 0.8F}))
                  .addProperty(YamatoAttackAnimation.JC_RELEASE_BLOCK_TIME, TimePairList.create(new float[]{0.0F, 16.833334F}))
                  .addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
                  .addProperty(ActionAnimationProperty.REMOVE_DELTA_MOVEMENT, true)
                  .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
                  .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
                  .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
                  .newTimePair(0.0F, Float.MAX_VALUE)
                  .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
                  .addStateRemoveOld(EntityState.LOCKON_ROTATE, false)
                  .addStateRemoveOld(EntityState.MOVEMENT_LOCKED, true)
                  .addEvents(ActionAnimationProperty.ON_END_EVENTS, new AnimationEvent[]{SimpleEvent.create((livingEntityPatch, staticAnimation, objects) -> {
                     CinematicBarsUtils.close();
                     CameraFovUtil.stopZoom();
                  }, Side.LOCAL_CLIENT)})
                  .addEvents(
                     new AnimationEvent[]{
                        InPeriodEvent.create(13.516666F, 13.716666F, (patch, anim, params) -> YamatoAnimations.scanTntInFront(patch), Side.SERVER)
                     }
                  )
         )
      );
      YamatoAnimations.YAMATO_PROVOCATION_D = builder.nextAccessor(
         "biped/yamato/provocation/yamato_provocation_d",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                  0.1F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  CustomStunAnimationUtils.createCustomStunPhase(
                     78, 89, 451, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               )
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{1.2333333F, 6.1F}))
               .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{1.2333333F, 6.1F}))
               .addProperty(YamatoAttackAnimation.USE_MESH2_IN_LINK, false)
               .addProperty(YamatoAttackAnimation.NOT_USE_SUMMON_DOPPELGANGER_ANIM, true)
               .addProperty(YamatoAttackAnimation.JUMP_CANCEL_TIME, TimePairList.create(new float[]{0.1F, 0.8F}))
               .addProperty(YamatoAttackAnimation.JC_RELEASE_BLOCK_TIME, TimePairList.create(new float[]{0.0F, 5.6666665F}))
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
               .addProperty(ActionAnimationProperty.REMOVE_DELTA_MOVEMENT, true)
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
               .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
               .newTimePair(0.0F, Float.MAX_VALUE)
               .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
               .addStateRemoveOld(EntityState.LOCKON_ROTATE, false)
               .addStateRemoveOld(EntityState.MOVEMENT_LOCKED, true)
      );
      YamatoAnimations.YAMATO_PROVOCATION_PORTAL = builder.nextAccessor(
         "biped/yamato/provocation/yamato_provocation_portal",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_PROVOCATION_PORTAL",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.1F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        49, 57, 90, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                     ),
                     CustomStunAnimationUtils.createCustomStunPhase(
                        91, 108, 280, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.DOOR1.get(), 0)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.DOOR2.get(), 1)
                  .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{0.1F, 0.26666668F}))
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.28333333F, 4.1F}))
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{3.3333333F, 4.1F}))
                  .addProperty(YamatoAttackAnimation.JC_RELEASE_BLOCK_TIME, TimePairList.create(new float[]{0.0F, 4.05F}))
                  .addProperty(YamatoAttackAnimation.USE_MESH2_IN_LINK, false)
                  .addProperty(YamatoAttackAnimation.NOT_USE_SUMMON_DOPPELGANGER_ANIM, true)
                  .addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
                  .addProperty(ActionAnimationProperty.REMOVE_DELTA_MOVEMENT, true)
                  .newTimePair(0.0F, Float.MAX_VALUE)
                  .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
                  .addStateRemoveOld(EntityState.LOCKON_ROTATE, true)
                  .addStateRemoveOld(EntityState.MOVEMENT_LOCKED, true)
                  .addEvents(
                     new AnimationEvent[]{
                        InTimeEvent.create(2.6666667F, (patch, anim, params) -> patch.playSound(SoundEvents.f_11860_, 1.0F, 1.0F, 1.0F), Side.CLIENT),
                        InTimeEvent.create(2.65F, (patch, anim, params) -> {
                           LivingEntity entity = (LivingEntity)patch.getOriginal();
                           float yawRad = (float)Math.toRadians((double)(-patch.getYRot()));
                           Vec3 lookDir = new Vec3(Math.sin((double)yawRad), 0.0, Math.cos((double)yawRad));
                           Vec3 feetPos = entity.m_20182_();
                           double spawnY = feetPos.f_82480_ + (double)entity.m_20192_() * 0.7 / 2.0 - 0.35;
                           Vec3 pos = new Vec3(feetPos.f_82479_, spawnY, feetPos.f_82481_).m_82549_(lookDir.m_82490_(1.8));
                           ServerLevel serverLevel = (ServerLevel)entity.m_9236_();
                           PortalEntity portal = new PortalEntity((EntityType<?>)DMCEntities.PORTAL.get(), serverLevel);
                           portal.setOwner(entity);
                           portal.m_146884_(pos);
                           portal.m_146922_(patch.getYRot());
                           serverLevel.m_7967_(portal);
                        }, Side.SERVER)
                     }
                  )
         )
      );
   }
}
