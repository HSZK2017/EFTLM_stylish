package com.pla.annoyingvillagers.gameasset;

import java.util.Set;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.BasicAttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.StunType;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.MOD
)
public class AnimsEpicFightGuandao {
   public static AnimationAccessor<StaticAnimation> FALCHION_IDLE;
   public static AnimationAccessor<BasicAttackAnimation> FALCHION_AUTO1;
   public static AnimationAccessor<BasicAttackAnimation> FALCHION_AUTO2;
   public static AnimationAccessor<BasicAttackAnimation> FALCHION_AUTO3;
   public static AnimationAccessor<AttackAnimation> FALCHION_FORWARD;
   public static AnimationAccessor<AttackAnimation> FALCHION_BACKWARD;
   public static AnimationAccessor<AttackAnimation> FALCHION_SIDE;

   public static void build(AnimationBuilder builder) {
      ArmatureAccessor<HumanoidArmature> humanoidArmature = Armatures.BIPED;
      FALCHION_IDLE = builder.nextAccessor("biped/falchion/falchion_idle", animationaccessor -> new StaticAnimation(true, animationaccessor, humanoidArmature));
      FALCHION_AUTO1 = builder.nextAccessor(
         "biped/falchion/falchion_auto1",
         animationaccessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.1F, 0.25F, 0.317F, 0.5F, null, ((HumanoidArmature)humanoidArmature.get()).toolR, animationaccessor, humanoidArmature
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F)
      );
      FALCHION_AUTO2 = builder.nextAccessor(
         "biped/falchion/falchion_auto2",
         animationaccessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.05F, 0.167F, 0.233F, 0.5F, null, ((HumanoidArmature)humanoidArmature.get()).toolR, animationaccessor, humanoidArmature
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F)
      );
      FALCHION_AUTO3 = builder.nextAccessor(
         "biped/falchion/falchion_auto3",
         animationaccessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.05F, 0.333F, 0.383F, 0.95F, null, ((HumanoidArmature)humanoidArmature.get()).toolR, animationaccessor, humanoidArmature
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F)
      );
      FALCHION_FORWARD = builder.nextAccessor(
         "biped/falchion/falchion_forward",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.05F,
                  animationaccessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.6F, 0.667F, 0.75F, 0.75F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.75F, 0.85F, 0.9F, 1.35F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(4.0F))
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.25F))
                  }
               )
               .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE))
               .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.95F)
               .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F)
      );
      FALCHION_BACKWARD = builder.nextAccessor(
         "biped/falchion/falchion_backward",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.05F,
                  animationaccessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.5F, 0.6F, 1.0F, 1.0F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(5.0F)),
                     new Phase(1.0F, 1.63F, 1.7F, 2.33F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.25F))
                  }
               )
               .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE))
               .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.85F)
               .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F)
      );
      FALCHION_SIDE = builder.nextAccessor(
         "biped/falchion/falchion_side",
         animationaccessor -> (AttackAnimation)new AttackAnimation(
                  0.1F, 0.25F, 0.58F, 0.667F, 1.0F, null, ((HumanoidArmature)humanoidArmature.get()).toolR, animationaccessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE))
               .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.75F))
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
               .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F)
      );
   }
}
