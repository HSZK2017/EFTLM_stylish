package com.pla.annoyingvillagers.gameasset;

import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.types.LongHitAnimation;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.model.armature.HumanoidArmature;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.MOD
)
public class AnimsTacticalImbuements {
   ArmatureAccessor<HumanoidArmature> humanoidArmature = Armatures.BIPED;
   public static AnimationAccessor<LongHitAnimation> ZAP;
   public static AnimationAccessor<LongHitAnimation> ZAP_LONG;

   public static void build(AnimationBuilder builder) {
      ArmatureAccessor<HumanoidArmature> humanoidArmature = Armatures.BIPED;
      ZAP = builder.nextAccessor(
         "biped/tactical_imbuements/zap",
         accessor -> (LongHitAnimation)new LongHitAnimation(0.1F, accessor, humanoidArmature)
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
      );
      ZAP_LONG = builder.nextAccessor(
         "biped/tactical_imbuements/zap_long",
         accessor -> (LongHitAnimation)new LongHitAnimation(0.1F, accessor, humanoidArmature)
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
      );
   }
}
