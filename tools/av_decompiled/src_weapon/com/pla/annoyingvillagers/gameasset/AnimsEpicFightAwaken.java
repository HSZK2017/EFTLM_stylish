package com.pla.annoyingvillagers.gameasset;

import com.pla.annoyingvillagers.gameasset.AVAnimations.ReuseableEvents;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
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
import yesman.epicfight.api.animation.property.MoveCoordFunctions.MoveCoordSetter;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.BasicAttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.AttackAnimation.JointColliderPair;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.gameasset.Animations.ReusableSources;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.MOD
)
public class AnimsEpicFightAwaken {
   public static AnimationAccessor<AttackAnimation> CUT_DP_AIR_ATTACK;
   public static AnimationAccessor<AttackAnimation> CUT_HOOK_SPIN_SLASH_AIR;
   public static AnimationAccessor<AttackAnimation> DP_THROW_BLADE_AUTO_1;
   public static AnimationAccessor<AttackAnimation> DP_THROW_BLADE_AUTO_2;
   public static AnimationAccessor<AttackAnimation> THROW_HOOK_SLASH_AIR;
   public static AnimationAccessor<AttackAnimation> DP_AUTO_1;
   public static AnimationAccessor<AttackAnimation> DP_AUTO_2;
   public static AnimationAccessor<AttackAnimation> DP_AUTO_3;
   public static AnimationAccessor<AttackAnimation> CUT_LEFT_DP_AUTO_3;
   public static AnimationAccessor<AttackAnimation> DP_AUTO_4;
   public static AnimationAccessor<AttackAnimation> DP_HEAVY_AUTO_1;
   public static AnimationAccessor<AttackAnimation> DP_HEAVY_AUTO_2;
   public static AnimationAccessor<AttackAnimation> DP_HEAVY_AUTO_3;
   public static AnimationAccessor<AttackAnimation> DP_HEAVY_AUTO_4;
   public static AnimationAccessor<AttackAnimation> DP_DASH;
   public static AnimationAccessor<AttackAnimation> CUT_LEFT_DP_DASH;
   public static AnimationAccessor<AttackAnimation> HOOK_SLASH_GROUND;
   public static AnimationAccessor<AttackAnimation> HOOK_SLASH_AIR;
   public static AnimationAccessor<AttackAnimation> DP_NIGHT_FALL;
   public static AnimationAccessor<AttackAnimation> CUT_LEFT_DP_DUSK_REAVER_2;
   public static AnimationAccessor<AttackAnimation> DP_HEAVY_AUTO_4_SPC;
   public static AnimationAccessor<AttackAnimation> DP_SHADOW_LUNGE_1;
   public static AnimationAccessor<AttackAnimation> DP_SHADOW_LUNGE_2;
   public static AnimationAccessor<AttackAnimation> DP_SHADOW_LUNGE_3;
   public static AnimationAccessor<AttackAnimation> CUT_LEFT_DP_SHADOW_LUNGE_1;
   public static AnimationAccessor<AttackAnimation> CUT_LEFT_DP_SHADOW_LUNGE_2;
   public static AnimationAccessor<AttackAnimation> CUT_LEFT_DP_SHADOW_LUNGE_3;
   public static AnimationAccessor<AttackAnimation> CUT_LEFT_DP_PHANTOM_DANCE_END_1_ENHANCED;
   public static AnimationAccessor<AttackAnimation> DP_PHANTOM_DANCE_END_2_ENHANCED;
   public static AnimationAccessor<AttackAnimation> DP_FALLING_SHADOW;
   public static AnimationAccessor<BasicAttackAnimation> HOOK_AIR;
   public static AnimationAccessor<BasicAttackAnimation> HOOK_GROUND;

   public static void build(AnimationBuilder builder) {
      ArmatureAccessor<HumanoidArmature> humanoidArmature = Armatures.BIPED;
      CUT_DP_AIR_ATTACK = builder.nextAccessor(
         "biped/epicfight_awaken/cut_dp_airattack",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(
                        0.0F, 0.167F, 0.167F, 0.38F, 1.0F, Float.MAX_VALUE, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolL, null
                     )
                  }
               )
               .addProperty(AttackAnimationProperty.REMOVE_DELTA_MOVEMENT, false)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
               .newTimePair(0.0F, 0.3F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
               .newTimePair(0.3F, 10.0F)
      );
      CUT_HOOK_SPIN_SLASH_AIR = builder.nextAccessor(
         "biped/epicfight_awaken/cut_hook_spin_slash_air",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.15F,
                  accessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(
                           0.0F,
                           0.0F,
                           0.8F,
                           0.9F,
                           0.9F,
                           0.9F,
                           InteractionHand.MAIN_HAND,
                           new JointColliderPair[]{
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                           }
                        )
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.05F))
                        .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL),
                     new Phase(
                           0.9F,
                           0.9F,
                           0.95F,
                           1.05F,
                           1.05F,
                           1.05F,
                           InteractionHand.MAIN_HAND,
                           new JointColliderPair[]{
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                           }
                        )
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.05F))
                        .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL),
                     new Phase(
                           1.05F,
                           1.05F,
                           1.15F,
                           1.25F,
                           10.0F,
                           Float.MAX_VALUE,
                           InteractionHand.MAIN_HAND,
                           new JointColliderPair[]{
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                           }
                        )
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.05F))
                        .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
                  }
               )
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> 2.0F
               )
               .addProperty(ActionAnimationProperty.AFFECT_SPEED, true)
               .addProperty(AttackAnimationProperty.STOP_MOVEMENT, false)
               .newTimePair(0.0F, 1.45F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .newTimePair(0.0F, 1.95F)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
      );
      DP_THROW_BLADE_AUTO_1 = builder.nextAccessor(
         "biped/epicfight_awaken/throw_blade_auto_1",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.15F, 0.33F, 0.33F, 1.33F, 1.33F, ColliderPreset.FIST, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, accessor, Armatures.BIPED
               )
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.15F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(0.15F, ReuseableEvents.THROW_TRIDENT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(0.23F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(0.23F, ReuseableEvents.THROW_TRIDENT_HAND_RIGHT, Side.SERVER)
                  }
               )
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> 2.0F
               )
               .newTimePair(0.0F, 0.6F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .newTimePair(0.0F, 0.83F)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
      );
      DP_THROW_BLADE_AUTO_2 = builder.nextAccessor(
         "biped/epicfight_awaken/throw_blade_auto_2",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.15F, 0.53F, 0.53F, 1.2F, 1.2F, ColliderPreset.FIST, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, accessor, Armatures.BIPED
               )
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.43F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(0.43F, ReuseableEvents.THROW_TRIDENT_HAND_LEFT_LIGHTNING, Side.SERVER),
                     InTimeEvent.create(0.43F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(0.43F, ReuseableEvents.THROW_TRIDENT_HAND_RIGHT_LIGHTNING, Side.SERVER)
                  }
               )
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> 1.5F
               )
               .newTimePair(0.0F, 0.76F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .newTimePair(0.0F, 1.0F)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
      );
      THROW_HOOK_SLASH_AIR = builder.nextAccessor(
         "biped/epicfight_awaken/throw_hook_slash_air",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.15F,
                  accessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(
                           0.0F,
                           0.0F,
                           0.33F,
                           0.46F,
                           0.46F,
                           0.46F,
                           InteractionHand.MAIN_HAND,
                           new JointColliderPair[]{
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                           }
                        )
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.1F)),
                     new Phase(
                           0.46F,
                           0.46F,
                           0.47F,
                           0.6F,
                           10.0F,
                           Float.MAX_VALUE,
                           InteractionHand.MAIN_HAND,
                           new JointColliderPair[]{
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                           }
                        )
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.1F))
                  }
               )
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> 2.0F
               )
               .addProperty(ActionAnimationProperty.AFFECT_SPEED, true)
               .addProperty(AttackAnimationProperty.STOP_MOVEMENT, false)
               .newTimePair(0.0F, 0.85F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .newTimePair(0.0F, 1.35F)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.2F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(0.2F, ReuseableEvents.THROW_TRIDENT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(0.2F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(0.2F, ReuseableEvents.THROW_TRIDENT_HAND_RIGHT, Side.SERVER)
                  }
               )
      );
      DP_AUTO_1 = builder.nextAccessor(
         "biped/epicfight_awaken/dp_auto_1",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.15F,
                  animationaccessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(0.0F, 0.33F, 0.33F, 0.43F, 0.53F, 0.53F, InteractionHand.OFF_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolL, null),
                     new Phase(0.53F, 0.53F, 0.53F, 0.63F, 0.8F, 0.8F, InteractionHand.OFF_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolL, null),
                     new Phase(0.8F, 0.8F, 0.8F, 0.93F, 1.5F, Float.MAX_VALUE, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .newTimePair(0.0F, 1.0F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .newTimePair(0.0F, 1.16F)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
               .newTimePair(0.43F, 10.0F)
      );
      DP_AUTO_2 = builder.nextAccessor(
         "biped/epicfight_awaken/dp_auto_2",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.05F,
                  animationaccessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(0.0F, 0.23F, 0.23F, 0.38F, 0.38F, 0.38F, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                     new Phase(
                        0.38F, 0.38F, 0.38F, 0.5F, 1.33F, Float.MAX_VALUE, InteractionHand.OFF_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolL, null
                     )
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .newTimePair(0.0F, 0.56F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .newTimePair(0.0F, 0.83F)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
               .newTimePair(0.38F, 10.0F)
      );
      DP_AUTO_3 = builder.nextAccessor(
         "biped/epicfight_awaken/dp_auto_3",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.05F,
                  animationaccessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(0.0F, 0.3F, 0.3F, 0.4F, 0.5F, 0.5F, InteractionHand.OFF_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD),
                     new Phase(
                           0.5F, 0.5F, 0.5F, 0.67F, 1.43F, Float.MAX_VALUE, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                        )
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .newTimePair(0.0F, 0.7F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .newTimePair(0.0F, 1.0F)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
      );
      CUT_LEFT_DP_AUTO_3 = builder.nextAccessor(
         "biped/epicfight_awaken/cut_left_dp_auto_3",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.05F,
                  animationaccessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(
                           0.0F, 0.5F, 0.5F, 0.67F, 1.43F, Float.MAX_VALUE, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                        )
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .newTimePair(0.0F, 0.7F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .newTimePair(0.0F, 1.0F)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
      );
      DP_AUTO_4 = builder.nextAccessor(
         "biped/epicfight_awaken/dp_auto_4",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.2F,
                  animationaccessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(0.0F, 0.3F, 0.3F, 0.46F, 0.7F, 0.7F, InteractionHand.OFF_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolL, null),
                     new Phase(0.7F, 0.7F, 0.7F, 0.8F, 0.8F, 0.8F, InteractionHand.OFF_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.25F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.25F)),
                     new Phase(0.8F, 0.8F, 0.8F, 0.9F, 2.06F, Float.MAX_VALUE, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.5F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.5F))
                        .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                        .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .newTimePair(0.0F, 1.0F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .newTimePair(0.0F, 1.0F)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
      );
      DP_HEAVY_AUTO_1 = builder.nextAccessor(
         "biped/epicfight_awaken/dp_heavy_auto_1",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.15F,
                  animationaccessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(0.0F, 0.6F, 0.6F, 0.7F, 0.7F, 0.7F, InteractionHand.OFF_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.65F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(60.0F)),
                     new Phase(0.7F, 0.7F, 0.7F, 0.8F, 1.16F, Float.MAX_VALUE, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.65F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(60.0F))
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .newTimePair(0.0F, 0.8F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .newTimePair(0.0F, 0.9F)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
      );
      DP_HEAVY_AUTO_2 = builder.nextAccessor(
         "biped/epicfight_awaken/dp_heavy_auto_2",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.1F,
                  animationaccessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(0.0F, 0.4F, 0.4F, 0.56F, 0.56F, 0.56F, InteractionHand.OFF_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.85F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.5F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(60.0F)),
                     new Phase(
                           0.56F, 0.8F, 0.8F, 1.0F, 1.67F, Float.MAX_VALUE, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                        )
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.85F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.5F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(60.0F))
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .newTimePair(0.0F, 1.08F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .newTimePair(0.0F, 1.16F)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
      );
      DP_HEAVY_AUTO_3 = builder.nextAccessor(
         "biped/epicfight_awaken/dp_heavy_auto_3",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.05F,
                  animationaccessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(
                           0.0F,
                           0.5F,
                           0.5F,
                           0.63F,
                           1.5F,
                           Float.MAX_VALUE,
                           InteractionHand.MAIN_HAND,
                           new JointColliderPair[]{
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                           }
                        )
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.8F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(4.0F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(60.0F))
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .newTimePair(0.0F, 0.66F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .newTimePair(0.0F, 0.76F)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
      );
      DP_HEAVY_AUTO_4 = builder.nextAccessor(
         "biped/epicfight_awaken/dp_heavy_auto_4",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.05F,
                  animationaccessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(
                           0.0F,
                           0.5F,
                           0.5F,
                           0.67F,
                           1.3F,
                           Float.MAX_VALUE,
                           InteractionHand.MAIN_HAND,
                           new JointColliderPair[]{
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                           }
                        )
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(4.5F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(5.0F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(80.0F))
                        .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                        .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .newTimePair(0.0F, 0.73F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .newTimePair(0.0F, 0.83F)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
      );
      DP_DASH = builder.nextAccessor(
         "biped/epicfight_awaken/dp_dash",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.05F,
                  animationaccessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(0.0F, 0.4F, 0.4F, 0.56F, 0.7F, 0.7F, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.8F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.8F)),
                     new Phase(0.7F, 0.7F, 0.7F, 0.76F, 2.26F, Float.MAX_VALUE, InteractionHand.OFF_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.8F))
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .addProperty(AttackAnimationProperty.REACH, 0.3F)
               .newTimePair(0.0F, 0.8F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
      );
      CUT_LEFT_DP_DASH = builder.nextAccessor(
         "biped/epicfight_awaken/cut_left_dp_dash",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.05F,
                  animationaccessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(0.0F, 0.4F, 0.4F, 0.56F, 0.7F, 0.7F, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.8F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.8F))
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .addProperty(AttackAnimationProperty.REACH, 0.3F)
               .newTimePair(0.0F, 0.8F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
      );
      HOOK_SLASH_GROUND = builder.nextAccessor(
         "biped/epicfight_awaken/hook_slash_ground",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.15F,
                  animationaccessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(
                           0.0F,
                           0.0F,
                           0.1F,
                           0.2F,
                           0.2F,
                           0.2F,
                           InteractionHand.MAIN_HAND,
                           new JointColliderPair[]{
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                           }
                        )
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.25F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(50.0F)),
                     new Phase(
                           0.2F,
                           0.0F,
                           0.2F,
                           0.3F,
                           0.3F,
                           0.3F,
                           InteractionHand.MAIN_HAND,
                           new JointColliderPair[]{
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                           }
                        )
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.25F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(50.0F)),
                     new Phase(
                           0.3F,
                           0.0F,
                           0.3F,
                           0.36F,
                           0.36F,
                           0.36F,
                           InteractionHand.MAIN_HAND,
                           new JointColliderPair[]{
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                           }
                        )
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.25F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(50.0F)),
                     new Phase(
                           0.36F,
                           0.0F,
                           0.46F,
                           0.56F,
                           1.33F,
                           1.33F,
                           InteractionHand.MAIN_HAND,
                           new JointColliderPair[]{
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                           }
                        )
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.5F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(50.0F))
                        .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .addProperty(ActionAnimationProperty.AFFECT_SPEED, true)
               .addProperty(AttackAnimationProperty.STOP_MOVEMENT, false)
               .addProperty(AttackAnimationProperty.REACH, 2.0F)
               .newTimePair(0.0F, 0.2F)
               .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
               .newTimePair(0.3F, Float.MAX_VALUE)
               .addState(EntityState.TURNING_LOCKED, true)
               .newTimePair(0.0F, 0.66F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .newTimePair(0.0F, 1.33F)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
               .newTimePair(0.0F, Float.MAX_VALUE)
               .addState(
                  EntityState.ATTACK_RESULT,
                  (Function<DamageSource, ResultType>)damagesource -> damagesource.m_269533_(DamageTypeTags.f_268524_) ? ResultType.MISSED : ResultType.SUCCESS
               )
      );
      HOOK_SLASH_AIR = builder.nextAccessor(
         "biped/epicfight_awaken/hook_slash_air",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.15F,
                  animationaccessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(
                           0.0F,
                           0.0F,
                           0.33F,
                           0.46F,
                           0.46F,
                           0.46F,
                           InteractionHand.MAIN_HAND,
                           new JointColliderPair[]{
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                           }
                        )
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.1F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(50.0F)),
                     new Phase(
                           0.46F,
                           0.46F,
                           0.47F,
                           0.6F,
                           10.0F,
                           Float.MAX_VALUE,
                           InteractionHand.MAIN_HAND,
                           new JointColliderPair[]{
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                           }
                        )
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.1F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(50.0F))
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .addProperty(ActionAnimationProperty.AFFECT_SPEED, true)
               .addProperty(AttackAnimationProperty.STOP_MOVEMENT, false)
               .newTimePair(0.0F, 0.85F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .newTimePair(0.0F, 1.35F)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
               .newTimePair(0.0F, Float.MAX_VALUE)
               .addState(
                  EntityState.ATTACK_RESULT,
                  (Function<DamageSource, ResultType>)damagesource -> !damagesource.m_276093_(DamageTypes.f_268671_)
                           && !damagesource.m_269533_(DamageTypeTags.f_268524_)
                        ? ResultType.SUCCESS
                        : ResultType.MISSED
               )
      );
      DP_NIGHT_FALL = builder.nextAccessor(
         "biped/epicfight_awaken/dp_night_fall",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.05F,
                  animationaccessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(
                           0.0F,
                           0.1F,
                           0.1F,
                           0.18F,
                           0.28F,
                           0.28F,
                           InteractionHand.MAIN_HAND,
                           new JointColliderPair[]{
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                           }
                        )
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.4F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.2F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(50.0F)),
                     new Phase(
                           0.28F,
                           0.43F,
                           0.43F,
                           0.567F,
                           1.3F,
                           Float.MAX_VALUE,
                           InteractionHand.MAIN_HAND,
                           new JointColliderPair[]{
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                           }
                        )
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.8F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.5F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(50.0F))
                        .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                        .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
                  }
               )
               .addProperty(AttackAnimationProperty.RESET_PLAYER_COMBO_COUNTER, false)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .addProperty(AttackAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(AttackAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.567F}))
               .newTimePair(0.0F, 0.75F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
      );
      CUT_LEFT_DP_DUSK_REAVER_2 = builder.nextAccessor(
         "biped/epicfight_awaken/cut_left_dp_dusk_reaver_2",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.05F,
                  animationaccessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(
                           0.0F,
                           0.33F,
                           0.23F,
                           0.33F,
                           2.0F,
                           Float.MAX_VALUE,
                           InteractionHand.MAIN_HAND,
                           new JointColliderPair[]{
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).rootJoint, null),
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                           }
                        )
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.4F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.4F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(80.0F))
                        .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
                        .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                        .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
                  }
               )
               .addProperty(AttackAnimationProperty.RESET_PLAYER_COMBO_COUNTER, false)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, false)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, false)
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, (MoveCoordSetter)(dynamicanimation, livingentitypatch, transformsheet) -> {
                  if (!dynamicanimation.isLinkAnimation()) {
                     transformsheet.readFrom(dynamicanimation.getCoord().copyAll().extendsZCoord(2.5F, 2, 4));
                  } else {
                     MoveCoordFunctions.RAW_COORD.set(dynamicanimation, livingentitypatch, transformsheet);
                  }
               })
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, null)
               .newTimePair(0.0F, 0.67F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
               .newTimePair(0.33F, 10.0F)
      );
      DP_HEAVY_AUTO_4_SPC = builder.nextAccessor(
         "biped/epicfight_awaken/dp_heavy_auto_4_spc",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.05F,
                  animationaccessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(
                           0.0F,
                           0.06F,
                           0.06F,
                           0.16F,
                           0.16F,
                           0.16F,
                           InteractionHand.MAIN_HAND,
                           new JointColliderPair[]{
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                           }
                        )
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.8F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(10.0F)),
                     new Phase(
                           0.16F,
                           0.16F,
                           0.16F,
                           0.33F,
                           1.3F,
                           Float.MAX_VALUE,
                           InteractionHand.MAIN_HAND,
                           new JointColliderPair[]{
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                           }
                        )
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.8F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(10.0F))
                  }
               )
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .newTimePair(0.0F, 0.67F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .newTimePair(0.0F, 0.67F)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
      );
      DP_SHADOW_LUNGE_1 = builder.nextAccessor(
         "biped/epicfight_awaken/dp_shadow_lunge_1",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.05F,
                  animationaccessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(0.0F, 0.267F, 0.267F, 0.43F, 0.43F, 0.43F, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.4F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.5F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(60.0F)),
                     new Phase(
                           0.43F, 0.43F, 0.467F, 0.63F, 1.33F, Float.MAX_VALUE, InteractionHand.OFF_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolL, null
                        )
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.4F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(60.0F))
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .addProperty(AttackAnimationProperty.REACH, 0.3F)
               .newTimePair(0.0F, 0.63F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .newTimePair(0.0F, 0.63F)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
      );
      DP_SHADOW_LUNGE_2 = builder.nextAccessor(
         "biped/epicfight_awaken/dp_shadow_lunge_2",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.05F,
                  animationaccessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(0.0F, 0.267F, 0.267F, 0.367F, 0.43F, 0.43F, InteractionHand.OFF_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.8F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.5F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(60.0F)),
                     new Phase(
                           0.43F, 0.43F, 0.43F, 0.6F, 1.33F, Float.MAX_VALUE, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                        )
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.8F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.5F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(60.0F))
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .addProperty(AttackAnimationProperty.REACH, 0.3F)
               .newTimePair(0.0F, 1.1F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .newTimePair(0.0F, 1.1F)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
      );
      DP_SHADOW_LUNGE_3 = builder.nextAccessor(
         "biped/epicfight_awaken/dp_shadow_lunge_3",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.05F,
                  animationaccessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(0.0F, 0.0F, 0.2F, 0.33F, 0.33F, 0.33F, InteractionHand.OFF_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.4F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.8F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(60.0F)),
                     new Phase(
                           0.33F, 0.33F, 0.33F, 0.43F, 1.33F, Float.MAX_VALUE, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                        )
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(4.0F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(3.0F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(60.0F))
                        .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                        .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .addProperty(AttackAnimationProperty.REACH, 0.3F)
               .newTimePair(0.0F, 0.53F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .newTimePair(0.0F, 1.0F)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
      );
      CUT_LEFT_DP_SHADOW_LUNGE_1 = builder.nextAccessor(
         "biped/epicfight_awaken/cut_left_dp_shadow_lunge_1",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.05F,
                  animationaccessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(0.0F, 0.267F, 0.267F, 0.43F, 0.43F, 0.43F, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.4F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.5F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(60.0F))
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .addProperty(AttackAnimationProperty.REACH, 0.3F)
               .newTimePair(0.0F, 0.63F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .newTimePair(0.0F, 0.63F)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
      );
      CUT_LEFT_DP_SHADOW_LUNGE_2 = builder.nextAccessor(
         "biped/epicfight_awaken/cut_left_dp_shadow_lunge_2",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.05F,
                  animationaccessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(
                           0.43F, 0.43F, 0.43F, 0.6F, 1.33F, Float.MAX_VALUE, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                        )
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.8F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.5F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(60.0F))
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .addProperty(AttackAnimationProperty.REACH, 0.3F)
               .newTimePair(0.0F, 1.1F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .newTimePair(0.0F, 1.1F)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
      );
      CUT_LEFT_DP_SHADOW_LUNGE_3 = builder.nextAccessor(
         "biped/epicfight_awaken/cut_left_dp_shadow_lunge_3",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.05F,
                  animationaccessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(
                           0.33F, 0.33F, 0.33F, 0.43F, 1.33F, Float.MAX_VALUE, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                        )
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(4.0F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(3.0F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(60.0F))
                        .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                        .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .addProperty(AttackAnimationProperty.REACH, 0.3F)
               .newTimePair(0.0F, 0.53F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .newTimePair(0.0F, 1.0F)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
      );
      CUT_LEFT_DP_PHANTOM_DANCE_END_1_ENHANCED = builder.nextAccessor(
         "biped/epicfight_awaken/cut_left_dp_phantom_dance_end_1_enhanced",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.2F,
                  animationaccessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(
                           0.0F,
                           0.83F,
                           0.83F,
                           1.03F,
                           1.33F,
                           1.33F,
                           InteractionHand.MAIN_HAND,
                           new JointColliderPair[]{
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                           }
                        )
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.5F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(5.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(100.0F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(100.0F))
                        .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.EVISCERATE.get())
                        .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL),
                     new Phase(1.33F, 1.33F, 1.52F, 1.58F, 1.58F, 1.58F, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, null)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.35F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.25F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(100.0F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(100.0F)),
                     new Phase(1.58F, 1.58F, 1.58F, 1.64F, 1.64F, 1.64F, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, null)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.35F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.25F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(100.0F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(100.0F)),
                     new Phase(1.64F, 1.64F, 1.64F, 1.7F, 1.7F, 1.7F, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, null)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.35F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.25F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(100.0F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(100.0F)),
                     new Phase(
                           1.7F, 1.7F, 1.7F, 1.8F, 2.3F, Float.MAX_VALUE, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, null
                        )
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.35F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.25F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(100.0F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(100.0F))
                  }
               )
               .addProperty(AttackAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(AttackAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 1.03F}))
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .newTimePair(0.0F, 1.8F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
               .newTimePair(1.8F, 10.0F)
               .newTimePair(0.0F, Float.MAX_VALUE)
               .addState(
                  EntityState.ATTACK_RESULT,
                  (Function<DamageSource, ResultType>)damagesource -> damagesource.m_269533_(DamageTypeTags.f_268738_) ? ResultType.SUCCESS : ResultType.MISSED
               )
      );
      DP_PHANTOM_DANCE_END_2_ENHANCED = builder.nextAccessor(
         "biped/epicfight_awaken/dp_phantom_dance_end_2_enhanced",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.2F,
                  animationaccessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(
                           0.0F,
                           0.83F,
                           0.83F,
                           1.03F,
                           1.33F,
                           1.33F,
                           InteractionHand.MAIN_HAND,
                           new JointColliderPair[]{
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                           }
                        )
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.5F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(5.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(100.0F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(100.0F))
                        .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.EVISCERATE.get())
                        .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL),
                     new Phase(1.33F, 1.33F, 1.52F, 1.58F, 1.58F, 1.58F, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, null)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.35F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.25F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(100.0F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(100.0F)),
                     new Phase(1.58F, 1.58F, 1.58F, 1.64F, 1.64F, 1.64F, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, null)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.35F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.25F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(100.0F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(100.0F)),
                     new Phase(1.64F, 1.64F, 1.64F, 1.7F, 1.7F, 1.7F, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, null)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.35F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.25F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(100.0F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(100.0F)),
                     new Phase(
                           1.7F, 1.7F, 1.7F, 1.8F, 2.3F, Float.MAX_VALUE, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, null
                        )
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.35F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.25F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(100.0F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(100.0F))
                  }
               )
               .addProperty(AttackAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(AttackAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 1.03F}))
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .newTimePair(0.0F, 1.8F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
               .newTimePair(0.0F, Float.MAX_VALUE)
               .addState(
                  EntityState.ATTACK_RESULT,
                  (Function<DamageSource, ResultType>)damagesource -> damagesource.m_269533_(DamageTypeTags.f_268738_) ? ResultType.SUCCESS : ResultType.MISSED
               )
      );
      DP_FALLING_SHADOW = builder.nextAccessor(
         "biped/epicfight_awaken/dp_falling_shadow",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.05F,
                  animationaccessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(0.0F, 0.1F, 0.1F, 0.267F, 0.267F, 0.267F, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.5F))
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(40.0F))
                        .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                        .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL),
                     new Phase(
                           0.267F,
                           0.9F,
                           0.9F,
                           1.0F,
                           1.0F,
                           1.0F,
                           InteractionHand.MAIN_HAND,
                           new JointColliderPair[]{
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null),
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                           }
                        )
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(40.0F)),
                     new Phase(
                           1.0F,
                           1.0F,
                           1.0F,
                           1.1F,
                           1.1F,
                           1.1F,
                           InteractionHand.MAIN_HAND,
                           new JointColliderPair[]{
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null),
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                           }
                        )
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(40.0F)),
                     new Phase(
                           1.1F,
                           1.1F,
                           1.1F,
                           1.2F,
                           1.2F,
                           1.2F,
                           InteractionHand.MAIN_HAND,
                           new JointColliderPair[]{
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null),
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                           }
                        )
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(40.0F)),
                     new Phase(
                           1.2F,
                           1.2F,
                           1.2F,
                           1.33F,
                           2.67F,
                           Float.MAX_VALUE,
                           InteractionHand.MAIN_HAND,
                           new JointColliderPair[]{
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null),
                              JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                           }
                        )
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                        .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(40.0F))
                  }
               )
               .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(DamageTypeTags.f_268467_))
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .addProperty(AttackAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.1F, 0.9F}))
               .addProperty(AttackAnimationProperty.MOVE_VERTICAL, true)
               .newTimePair(0.0F, 1.8F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .newTimePair(0.0F, 2.0F)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
               .newTimePair(0.0F, Float.MAX_VALUE)
               .addState(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damagesource -> {
                  if (damagesource instanceof EpicFightDamageSource epicfightdamagesource && epicfightdamagesource.getStunType() != StunType.NEUTRALIZE) {
                     epicfightdamagesource.setStunType(StunType.NONE);
                  }

                  return damagesource.m_276093_(DamageTypes.f_268671_) ? ResultType.MISSED : ResultType.SUCCESS;
               })
      );
      HOOK_AIR = builder.nextAccessor(
         "biped/epicfight_awaken/hook_air",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.1F, 0.7F, 1.4F, 1.0F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.STOP_MOVEMENT, false)
               .newTimePair(0.0F, 0.25F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
      );
      HOOK_GROUND = builder.nextAccessor(
         "biped/epicfight_awaken/hook_ground",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.1F, 0.7F, 0.8F, 1.0F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.STOP_MOVEMENT, false)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .newTimePair(0.0F, 0.25F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
      );
   }
}
