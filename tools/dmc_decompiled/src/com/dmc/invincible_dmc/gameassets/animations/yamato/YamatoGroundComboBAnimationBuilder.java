package com.dmc.invincible_dmc.gameassets.animations.yamato;

import com.dmc.invincible_dmc.api.animation.types.customStun.CustomStunAnimationUtils;
import com.dmc.invincible_dmc.api.animation.types.customStun.CustomStunAttackAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.client.sound.DMCSounds;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerPatch;
import com.dmc.invincible_dmc.entity.summonedsword.SummonedSwordSpawner;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
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
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.StunType;

final class YamatoGroundComboBAnimationBuilder {
   private YamatoGroundComboBAnimationBuilder() {
   }

   static void build(AnimationBuilder builder) {
      YamatoAnimations.YAMATO_COMBO_B_1 = builder.nextAccessor(
         "biped/yamato/attack/yamato_combo_b_1",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_COMBO_B_1",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.15F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        24, 30, 61, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, YamatoAnimations.COMBO_B
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_3.get())
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                  .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.35F, 2.1666667F}))
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{1.3333334F, 2.1666667F}))
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
                  .addEvents(new AnimationEvent[]{InTimeEvent.create(0.45F, (ep, anim, objs) -> {
                     if (ep instanceof ServerPlayerPatch serverPlayerPatch) {
                        SummonedSwordSpawner.summonImpale(serverPlayerPatch);
                     }
                  }, Side.SERVER)})
         )
      );
      YamatoAnimations.YAMATO_COMBO_B_2_SDT = builder.nextAccessor(
         "biped/yamato/attack/yamato_combo_b_2_sdt",
         YamatoAnimationEffectManager.withEffects(
            "YAMATO_COMBO_B_2_SDT",
            accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
                     0.1F,
                     accessor,
                     Armatures.BIPED,
                     1.0F,
                     1.0F,
                     CustomStunAnimationUtils.createCustomStunPhase(
                        17, 23, 136, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, YamatoAnimations.BLADE_COLLIDER_EX
                     )
                  )
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.YAMATO_SWING_18.get())
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                  .addProperty(YamatoAttackAnimation.UNSHEATH_TIME, TimePairList.create(new float[]{0.0F, 2.6666667F}))
                  .addProperty(YamatoAttackAnimation.SDT_SHEATHING_TIME, TimePairList.create(new float[]{2.25F, 2.6666667F}))
                  .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
                  .addProperty(CustomStunAttackAnimation.HOLD_LEDGE, true)
                  .addProperty(YamatoAttackAnimation.CAN_BASIC_ATTACK_START, 0.6666667F)
                  .addProperty(YamatoAttackAnimation.USE_MESH2_IN_LINK, true)
                  .addEvents(
                     ActionAnimationProperty.ON_BEGIN_EVENTS,
                     new AnimationEvent[]{
                        SimpleEvent.create(
                           (livingEntityPatch, staticAnimation, objects) -> {
                              if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch
                                 && SinDevilTriggerManager.isPlayerInSDT((Player)serverPlayerPatch.getOriginal())) {
                                 SummonedSwordSpawner.triple(((ServerPlayer)serverPlayerPatch.getOriginal()).m_284548_(), serverPlayerPatch.getOriginal());
                              }
                           },
                           Side.SERVER
                        )
                     }
                  )
         )
      );
   }
}
