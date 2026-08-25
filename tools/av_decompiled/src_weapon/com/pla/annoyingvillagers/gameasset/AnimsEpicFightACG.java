package com.pla.annoyingvillagers.gameasset;

import com.pla.annoyingvillagers.animations.BowAttackAnimation;
import com.pla.annoyingvillagers.util.BowFunction;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationProperty.PlaybackSpeedModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.model.armature.HumanoidArmature;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.MOD
)
public class AnimsEpicFightACG {
   public static AnimationAccessor<BowAttackAnimation> BOW_AUTO_1;
   public static AnimationAccessor<BowAttackAnimation> BOW_AUTO_2;
   public static AnimationAccessor<BowAttackAnimation> BOW_AUTO_3;
   public static AnimationAccessor<BowAttackAnimation> BOW_AUTO_4;
   public static AnimationAccessor<BowAttackAnimation> BOW_AUTO_5;

   public static void build(AnimationBuilder builder) {
      ArmatureAccessor<HumanoidArmature> humanoidArmature = Armatures.BIPED;
      BOW_AUTO_1 = builder.nextAccessor(
         "biped/epic_agc/bow_auto1",
         accessor -> (BowAttackAnimation)new BowAttackAnimation(
                  0.1F,
                  0.0F,
                  0.62F,
                  0.8333F,
                  1.2F,
                  InteractionHand.MAIN_HAND,
                  null,
                  ((HumanoidArmature)humanoidArmature.get()).rootJoint,
                  accessor,
                  humanoidArmature
               )
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.4F, (livingEntityPatch, assetAccessor, objects) -> BowFunction.bowShoot(livingEntityPatch), Side.BOTH)
                  }
               )
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(anim, entity, a, b, c) -> 3.0F)
      );
      BOW_AUTO_2 = builder.nextAccessor(
         "biped/epic_agc/bow_auto2",
         accessor -> (BowAttackAnimation)new BowAttackAnimation(
                  0.1F,
                  0.0F,
                  0.7F,
                  0.98F,
                  1.2F,
                  InteractionHand.MAIN_HAND,
                  null,
                  ((HumanoidArmature)humanoidArmature.get()).rootJoint,
                  accessor,
                  humanoidArmature
               )
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.6F, (livingEntityPatch, assetAccessor, objects) -> BowFunction.bowShoot(livingEntityPatch), Side.BOTH)
                  }
               )
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(anim, entity, a, b, c) -> 3.0F)
      );
      BOW_AUTO_3 = builder.nextAccessor(
         "biped/epic_agc/bow_auto3",
         accessor -> (BowAttackAnimation)new BowAttackAnimation(
                  0.1F,
                  0.0F,
                  0.88F,
                  1.03F,
                  1.3F,
                  InteractionHand.MAIN_HAND,
                  null,
                  ((HumanoidArmature)humanoidArmature.get()).rootJoint,
                  accessor,
                  humanoidArmature
               )
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(anim, entity, a, b, c) -> 3.0F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.84F, (livingEntityPatch, assetAccessor, objects) -> BowFunction.bowShoot(livingEntityPatch), Side.BOTH)
                  }
               )
      );
      BOW_AUTO_4 = builder.nextAccessor(
         "biped/epic_agc/bow_auto4",
         accessor -> (BowAttackAnimation)new BowAttackAnimation(
                  0.05F,
                  0.0F,
                  2.12F,
                  2.733F,
                  1.2F,
                  InteractionHand.MAIN_HAND,
                  null,
                  ((HumanoidArmature)humanoidArmature.get()).rootJoint,
                  accessor,
                  humanoidArmature
               )
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(1.2083F, (livingEntityPatch, assetAccessor, objects) -> BowFunction.bowShoot(livingEntityPatch), Side.BOTH),
                     InTimeEvent.create(1.7916F, (livingEntityPatch, assetAccessor, objects) -> BowFunction.bowShoot(livingEntityPatch), Side.BOTH),
                     InTimeEvent.create(2.0416F, (livingEntityPatch, assetAccessor, objects) -> BowFunction.bowShoot(livingEntityPatch), Side.BOTH)
                  }
               )
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(anim, entity, a, b, c) -> 3.0F)
      );
      BOW_AUTO_5 = builder.nextAccessor(
         "biped/epic_agc/bow_auto5",
         accessor -> (BowAttackAnimation)new BowAttackAnimation(
                  0.02F,
                  0.0F,
                  0.2F,
                  1.51F,
                  1.2F,
                  InteractionHand.MAIN_HAND,
                  null,
                  ((HumanoidArmature)humanoidArmature.get()).rootJoint,
                  accessor,
                  humanoidArmature
               )
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(anim, entity, a, b, c) -> 3.0F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.7083F, (livingEntityPatch, assetAccessor, objects) -> BowFunction.bowShoot(livingEntityPatch), Side.BOTH)
                  }
               )
      );
   }
}
