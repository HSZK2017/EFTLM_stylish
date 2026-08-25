package com.pla.annoyingvillagers.gameasset;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import reascer.wom.animation.attacks.BasicMultipleAttackAnimation;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.Animations.ReusableSources;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.damagesource.StunType;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.MOD
)
public class AnimsEpicFightSanji {
   public static AnimationAccessor<BasicMultipleAttackAnimation> SANJI_DIABLE;
   public static AnimationAccessor<BasicMultipleAttackAnimation> SANJI_CONCASSER;

   public static void build(AnimationBuilder builder) {
      ArmatureAccessor<HumanoidArmature> humanoidArmature = Armatures.BIPED;
      SANJI_DIABLE = builder.nextAccessor(
         "biped/epicsanji/sanji_disable",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.1F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 1.95F, 2.15F, 3.0F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).rootJoint, AVCollider.SANJI_SPIN)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(1.0F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(12.0F))
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(20.0F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.f_11705_)
               .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.05F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.55F, ReusableSources.PLAY_SOUND, Side.SERVER).params((SoundEvent)AVSounds.SWORD_WHOOSH.get()),
                     InTimeEvent.create(0.65F, ReusableSources.PLAY_SOUND, Side.SERVER).params((SoundEvent)AVSounds.SWORD_WHOOSH.get()),
                     InTimeEvent.create(0.75F, ReusableSources.PLAY_SOUND, Side.SERVER).params((SoundEvent)AVSounds.SWORD_WHOOSH.get()),
                     InTimeEvent.create(0.85F, ReusableSources.PLAY_SOUND, Side.SERVER).params((SoundEvent)AVSounds.SWORD_WHOOSH.get()),
                     InTimeEvent.create(0.95F, ReusableSources.PLAY_SOUND, Side.SERVER).params((SoundEvent)AVSounds.SWORD_WHOOSH.get()),
                     InTimeEvent.create(1.05F, ReusableSources.PLAY_SOUND, Side.SERVER).params((SoundEvent)AVSounds.SWORD_WHOOSH.get()),
                     InTimeEvent.create(1.15F, ReusableSources.PLAY_SOUND, Side.SERVER).params((SoundEvent)AVSounds.SWORD_WHOOSH.get()),
                     InTimeEvent.create(1.25F, ReusableSources.PLAY_SOUND, Side.SERVER).params((SoundEvent)AVSounds.SWORD_WHOOSH.get()),
                     InTimeEvent.create(1.35F, ReusableSources.PLAY_SOUND, Side.SERVER).params((SoundEvent)AVSounds.SWORD_WHOOSH.get()),
                     InTimeEvent.create(1.45F, ReusableSources.PLAY_SOUND, Side.SERVER).params((SoundEvent)AVSounds.SWORD_WHOOSH.get()),
                     InTimeEvent.create(1.55F, ReusableSources.PLAY_SOUND, Side.SERVER).params((SoundEvent)AVSounds.SWORD_WHOOSH.get()),
                     InTimeEvent.create(1.65F, ReusableSources.PLAY_SOUND, Side.SERVER).params((SoundEvent)AVSounds.SWORD_WHOOSH.get())
                  }
               )
      );
      SANJI_CONCASSER = builder.nextAccessor(
         "biped/epicsanji/sanji_concasser",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.1F, 1.15F, 1.9F, 2.35F, AVCollider.SANJI_SPIN, ((HumanoidArmature)humanoidArmature.get()).rootJoint, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.2F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(8.0F))
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 3.05F)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, false)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.05F, ReusableSources.PLAY_SOUND, Side.SERVER).params((SoundEvent)AVSounds.SWORD_WHOOSH.get()),
                     InTimeEvent.create(0.15F, ReusableSources.PLAY_SOUND, Side.SERVER).params((SoundEvent)AVSounds.SWORD_WHOOSH.get()),
                     InTimeEvent.create(0.25F, ReusableSources.PLAY_SOUND, Side.SERVER).params((SoundEvent)AVSounds.SWORD_WHOOSH.get()),
                     InTimeEvent.create(0.35F, ReusableSources.PLAY_SOUND, Side.SERVER).params((SoundEvent)AVSounds.SWORD_WHOOSH.get()),
                     InTimeEvent.create(0.45F, ReusableSources.PLAY_SOUND, Side.SERVER).params((SoundEvent)AVSounds.SWORD_WHOOSH.get()),
                     InTimeEvent.create(0.55F, ReusableSources.PLAY_SOUND, Side.SERVER).params((SoundEvent)AVSounds.SWORD_WHOOSH.get())
                  }
               )
      );
   }
}
