package com.pla.annoyingvillagers.gameasset;

import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.model.armature.HumanoidArmature;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.MOD
)
public class AnimsSculkSteve {
   public static AnimationAccessor<StaticAnimation> PLAYER_HEROBRINE_POSSESSION;
   public static AnimationAccessor<StaticAnimation> LEGENDARY_SWORD_IDLE;
   public static AnimationAccessor<StaticAnimation> HEROBRINE_SACRIFICING;
   public static AnimationAccessor<StaticAnimation> HEROBRINE_ASSISTANCE;
   public static AnimationAccessor<StaticAnimation> HEROBRINE_STAGE_CHANGE;
   public static AnimationAccessor<ActionAnimation> PORTAL_SUMMON;

   public static void build(AnimationBuilder builder) {
      ArmatureAccessor<HumanoidArmature> humanoidArmature = Armatures.BIPED;
      PLAYER_HEROBRINE_POSSESSION = builder.nextAccessor(
         "biped/sculk_steve/player_herobrine_possession", accessor -> new StaticAnimation(false, accessor, humanoidArmature)
      );
      LEGENDARY_SWORD_IDLE = builder.nextAccessor("biped/sculk_steve/legendary_sword_idle", accessor -> new StaticAnimation(true, accessor, humanoidArmature));
      HEROBRINE_SACRIFICING = builder.nextAccessor("biped/sculk_steve/herobrine_sacrificing", accessor -> new StaticAnimation(true, accessor, humanoidArmature));
      HEROBRINE_ASSISTANCE = builder.nextAccessor("biped/sculk_steve/herobrine_assistance", accessor -> new StaticAnimation(true, accessor, humanoidArmature));
      HEROBRINE_STAGE_CHANGE = builder.nextAccessor(
         "biped/sculk_steve/herobrine_stage_change", accessor -> new StaticAnimation(true, accessor, humanoidArmature)
      );
      PORTAL_SUMMON = builder.nextAccessor(
         "biped/sculk_steve/portal_summon",
         accessor -> (ActionAnimation)new ActionAnimation(0.05F, Float.MAX_VALUE, accessor, humanoidArmature)
               .addState(EntityState.MOVEMENT_LOCKED, true)
               .addState(EntityState.CAN_BASIC_ATTACK, false)
               .addState(EntityState.CAN_SKILL_EXECUTION, false)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
      );
   }
}
