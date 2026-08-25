package com.pla.annoyingvillagers.gameasset;

import com.pla.annoyingvillagers.gameasset.AVAnimations.ReuseableEvents;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import reascer.wom.animation.attacks.BasicMultipleAttackAnimation;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.damagesource.StunType;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.MOD
)
public class AnimsEpicFightInfernalGainer {
   public static AnimationAccessor<BasicMultipleAttackAnimation> INFERNAL_AUTO_1;
   public static AnimationAccessor<BasicMultipleAttackAnimation> INFERNAL_AUTO_2;
   public static AnimationAccessor<BasicMultipleAttackAnimation> INFERNAL_AUTO_3;
   public static AnimationAccessor<BasicMultipleAttackAnimation> OBSIDIAN_INFERNAL_AUTO_1;
   public static AnimationAccessor<BasicMultipleAttackAnimation> OBSIDIAN_INFERNAL_AUTO_2;

   public static void build(AnimationBuilder builder) {
      ArmatureAccessor<HumanoidArmature> humanoidArmature = Armatures.BIPED;
      INFERNAL_AUTO_1 = builder.nextAccessor(
         "biped/epicfight_infernal_gainer/infernal_auto_1",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.1F, 0.3F, 0.4F, 0.5F, ColliderPreset.FIST, ((HumanoidArmature)humanoidArmature.get()).toolL, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F))
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.4F)
      );
      INFERNAL_AUTO_2 = builder.nextAccessor(
         "biped/epicfight_infernal_gainer/infernal_auto_2",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.2F, 0.1F, 0.2F, 0.25F, ColliderPreset.FIST, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.5F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.4F)
      );
      INFERNAL_AUTO_3 = builder.nextAccessor(
         "biped/epicfight_infernal_gainer/infernal_auto_3",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.05F, 0.35F, 0.45F, 0.5F, ColliderPreset.FIST, ((HumanoidArmature)humanoidArmature.get()).legL, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F))
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.4F)
      );
      OBSIDIAN_INFERNAL_AUTO_1 = builder.nextAccessor(
         "biped/epicfight_infernal_gainer/obsidian_infernal_auto_1",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.1F, 0.3F, 0.4F, 0.5F, AVCollider.SHADOW_OBSIDIAN_PILLAR, ((HumanoidArmature)humanoidArmature.get()).toolL, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F))
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 5.4F)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.2F, ReuseableEvents.SUMMON_6_OBSIDIAN_HAND_LEFT, Side.SERVER)})
      );
      OBSIDIAN_INFERNAL_AUTO_2 = builder.nextAccessor(
         "biped/epicfight_infernal_gainer/obsidian_infernal_auto_2",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.2F, 0.1F, 0.2F, 0.25F, ColliderPreset.FIST, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.5F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 5.4F)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.1F, ReuseableEvents.SUMMON_OBSIDIAN_WALL, Side.SERVER)})
      );
   }
}
