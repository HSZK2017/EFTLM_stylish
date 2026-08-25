package com.pla.annoyingvillagers.gameasset;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import reascer.wom.animation.attacks.BasicMultipleAttackAnimation;
import reascer.wom.animation.attacks.SpecialAttackAnimation;
import reascer.wom.gameasset.ReuseableEvents;
import reascer.wom.gameasset.colliders.WOMWeaponColliders;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.damagesource.StunType;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.MOD
)
public class AnimsEpicFightDualGreatsword {
   public static AnimationAccessor<BasicMultipleAttackAnimation> GREATSWORD_TWOHAND_AUTO_1;
   public static AnimationAccessor<BasicMultipleAttackAnimation> GREATSWORD_TWOHAND_AUTO_2;
   public static AnimationAccessor<BasicMultipleAttackAnimation> GREATSWORD_DUAL_AUTO_1;
   public static AnimationAccessor<BasicMultipleAttackAnimation> GREATSWORD_DUAL_AUTO_2;
   public static AnimationAccessor<BasicMultipleAttackAnimation> GREATSWORD_DUAL_AUTO_3;
   public static AnimationAccessor<BasicMultipleAttackAnimation> GREATSWORD_DUAL_AUTO_4;
   public static AnimationAccessor<BasicMultipleAttackAnimation> GREATSWORD_DUAL_DASH;
   public static AnimationAccessor<BasicMultipleAttackAnimation> GREATSWORD_DUAL_AIRSLASH;
   public static AnimationAccessor<SpecialAttackAnimation> GREATSWORD_DUAL_EARTHQUAKE;
   public static AnimationAccessor<BasicMultipleAttackAnimation> SHADOW_OBSIDIAN_SWORD_GREATSWORD_TWOHAND_AUTO_1;
   public static AnimationAccessor<BasicMultipleAttackAnimation> SHADOW_OBSIDIAN_SWORD_GREATSWORD_TWOHAND_AUTO_2;
   public static AnimationAccessor<BasicMultipleAttackAnimation> SHADOW_OBSIDIAN_SWORD_GREATSWORD_DUAL_AUTO_3;
   public static AnimationAccessor<BasicMultipleAttackAnimation> SHADOW_OBSIDIAN_SWORD_GREATSWORD_DUAL_AIRSLASH;
   public static AnimationAccessor<SpecialAttackAnimation> SHADOW_OBSIDIAN_SWORD_GREATSWORD_DUAL_EARTHQUAKE;
   public static AnimationAccessor<SpecialAttackAnimation> SHADOW_OBSIDIAN_SWORD_GREATSWORD_DUAL_EARTHQUAKE_PILLAR;

   public static void build(AnimationBuilder builder) {
      ArmatureAccessor<HumanoidArmature> humanoidArmature = Armatures.BIPED;
      GREATSWORD_TWOHAND_AUTO_1 = builder.nextAccessor(
         "biped/epicfight_dual_greatsword/greatsword_twohand_auto_1",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.25F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.2F, 0.4F, 0.45F, 0.45F, InteractionHand.OFF_HAND, ((HumanoidArmature)humanoidArmature.get()).toolL, null),
                     new Phase(0.45F, 0.5F, 0.7F, 0.8F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7F))
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7F), 1)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.55F)
      );
      GREATSWORD_TWOHAND_AUTO_2 = builder.nextAccessor(
         "biped/epicfight_dual_greatsword/greatsword_twohand_auto_2",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.15F, 0.35F, 0.85F, 0.85F, ColliderPreset.DUAL_SWORD, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.7F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.05F)
      );
      GREATSWORD_DUAL_AUTO_1 = builder.nextAccessor(
         "biped/epicfight_dual_greatsword/greatsword_dual_auto_1",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.25F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.2F, 0.4F, 0.45F, 0.45F, InteractionHand.OFF_HAND, ((HumanoidArmature)humanoidArmature.get()).toolL, null),
                     new Phase(0.45F, 0.5F, 0.7F, 0.8F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7F))
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7F), 1)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.05F)
      );
      GREATSWORD_DUAL_AUTO_2 = builder.nextAccessor(
         "biped/epicfight_dual_greatsword/greatsword_dual_auto_2",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.15F, 0.35F, 0.85F, 0.85F, ColliderPreset.DUAL_SWORD, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.5F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.8F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.05F)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.85F, ReuseableEvents.SOLAR_GROUNDSLAM_SMALL, Side.CLIENT)})
      );
      GREATSWORD_DUAL_AUTO_3 = builder.nextAccessor(
         "biped/epicfight_dual_greatsword/greatsword_dual_auto_3",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.15F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.2F, 0.4F, 0.45F, 0.45F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.45F, 0.55F, 0.7F, 0.7F, Float.MAX_VALUE, InteractionHand.OFF_HAND, ((HumanoidArmature)humanoidArmature.get()).toolL, null)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F), 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.5F), 1)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.05F)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.45F, ReuseableEvents.SOLAR_GROUNDSLAM_SMALL, Side.CLIENT)})
      );
      GREATSWORD_DUAL_AUTO_4 = builder.nextAccessor(
         "biped/epicfight_dual_greatsword/greatsword_dual_auto_4",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.1F,
                  0.8F,
                  1.0F,
                  1.25F,
                  InteractionHand.OFF_HAND,
                  ColliderPreset.DUAL_SWORD,
                  ((HumanoidArmature)humanoidArmature.get()).rootJoint,
                  accessor,
                  humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.8F))
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.75F)
      );
      GREATSWORD_DUAL_DASH = builder.nextAccessor(
         "biped/epicfight_dual_greatsword/greatsword_dual_dash",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.05F, 0.1F, 0.4F, 0.4F, ColliderPreset.DUAL_SWORD, ((HumanoidArmature)humanoidArmature.get()).rootJoint, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.2F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.5F))
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT.get())
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, false)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.05F)
      );
      GREATSWORD_DUAL_AIRSLASH = builder.nextAccessor(
         "biped/epicfight_dual_greatsword/greatsword_dual_airslash",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.05F,
                  0.25F,
                  0.4F,
                  0.45F,
                  InteractionHand.OFF_HAND,
                  WOMWeaponColliders.TORMENT_AIRSLAM,
                  ((HumanoidArmature)humanoidArmature.get()).rootJoint,
                  accessor,
                  humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.8F))
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.05F)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.2F}))
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.4F, ReuseableEvents.SOLAR_GROUNDSLAM_SMALL, Side.CLIENT)})
      );
      GREATSWORD_DUAL_EARTHQUAKE = builder.nextAccessor(
         "biped/epicfight_dual_greatsword/greatsword_dual_earthquake",
         accessor -> (SpecialAttackAnimation)new SpecialAttackAnimation(
                  0.15F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 1.1F, 1.1F, 1.25F, 1.25F, ((HumanoidArmature)humanoidArmature.get()).toolR, ColliderPreset.DUAL_SWORD),
                     new Phase(1.25F, 1.3F, 1.4F, 1.5F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).rootJoint, ColliderPreset.DUAL_SWORD)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F))
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.4F), 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F), 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.SHORT, 1)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.05F)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(1.25F, ReuseableEvents.SOLAR_GROUNDSLAM_SMALL, Side.CLIENT)})
      );
      SHADOW_OBSIDIAN_SWORD_GREATSWORD_TWOHAND_AUTO_1 = builder.nextAccessor(
         "biped/epicfight_dual_greatsword/shadow_obsidian_sword_greatsword_twohand_auto_1",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.25F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.2F, 0.4F, 0.45F, 0.45F, InteractionHand.OFF_HAND, ((HumanoidArmature)humanoidArmature.get()).toolL, null),
                     new Phase(0.45F, 0.5F, 0.7F, 0.8F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7F))
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7F), 1)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.55F)
      );
      SHADOW_OBSIDIAN_SWORD_GREATSWORD_TWOHAND_AUTO_2 = builder.nextAccessor(
         "biped/epicfight_dual_greatsword/shadow_obsidian_sword_greatsword_twohand_auto_2",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.15F, 0.35F, 0.85F, 0.85F, ColliderPreset.DUAL_SWORD, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.7F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.05F)
      );
      SHADOW_OBSIDIAN_SWORD_GREATSWORD_DUAL_AUTO_3 = builder.nextAccessor(
         "biped/epicfight_dual_greatsword/shadow_obsidian_sword_greatsword_dual_auto_3",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.15F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.2F, 0.4F, 0.45F, 0.45F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(
                        0.45F,
                        0.55F,
                        0.7F,
                        0.7F,
                        Float.MAX_VALUE,
                        InteractionHand.OFF_HAND,
                        ((HumanoidArmature)humanoidArmature.get()).toolL,
                        AVCollider.SHADOW_OBSIDIAN_PILLAR
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F), 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.5F), 1)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.05F)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.45F, ReuseableEvents.SOLAR_GROUNDSLAM_SMALL, Side.CLIENT)})
      );
      SHADOW_OBSIDIAN_SWORD_GREATSWORD_DUAL_AIRSLASH = builder.nextAccessor(
         "biped/epicfight_dual_greatsword/shadow_obsidian_sword_greatsword_dual_airslash",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.05F,
                  0.25F,
                  0.4F,
                  0.45F,
                  InteractionHand.OFF_HAND,
                  AVCollider.SHADOW_OBSIDIAN_PILLAR,
                  ((HumanoidArmature)humanoidArmature.get()).toolR,
                  accessor,
                  humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.8F))
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.05F)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.2F}))
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.4F, ReuseableEvents.SOLAR_GROUNDSLAM_SMALL, Side.CLIENT)})
      );
      SHADOW_OBSIDIAN_SWORD_GREATSWORD_DUAL_EARTHQUAKE = builder.nextAccessor(
         "biped/epicfight_dual_greatsword/shadow_obsidian_sword_greatsword_dual_earthquake",
         accessor -> (SpecialAttackAnimation)new SpecialAttackAnimation(
                  0.15F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 1.1F, 1.1F, 1.25F, 1.25F, ((HumanoidArmature)humanoidArmature.get()).toolR, AVCollider.SHADOW_OBSIDIAN_PILLAR),
                     new Phase(1.25F, 1.3F, 1.4F, 1.5F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolL, AVCollider.SHADOW_OBSIDIAN_PILLAR)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F))
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.4F), 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F), 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.SHORT, 1)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.05F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(1.25F, ReuseableEvents.SOLAR_GROUNDSLAM_SMALL, Side.CLIENT),
                     InTimeEvent.create(1.25F, com.pla.annoyingvillagers.gameasset.AVAnimations.ReuseableEvents.SUMMON_OBSIDIAN_CIRCLE, Side.SERVER)
                  }
               )
      );
      SHADOW_OBSIDIAN_SWORD_GREATSWORD_DUAL_EARTHQUAKE_PILLAR = builder.nextAccessor(
         "biped/epicfight_dual_greatsword/shadow_obsidian_sword_greatsword_dual_earthquake_pillar",
         accessor -> (SpecialAttackAnimation)new SpecialAttackAnimation(
                  0.15F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 1.1F, 1.1F, 1.25F, 1.25F, ((HumanoidArmature)humanoidArmature.get()).toolR, AVCollider.SHADOW_OBSIDIAN_PILLAR),
                     new Phase(1.25F, 1.3F, 1.4F, 1.5F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolL, AVCollider.SHADOW_OBSIDIAN_PILLAR)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F))
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.4F), 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F), 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.SHORT, 1)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.05F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(1.25F, ReuseableEvents.SOLAR_GROUNDSLAM_SMALL, Side.CLIENT),
                     InTimeEvent.create(1.25F, com.pla.annoyingvillagers.gameasset.AVAnimations.ReuseableEvents.SUMMON_OBSIDIAN_CIRCLE, Side.SERVER)
                  }
               )
      );
   }
}
