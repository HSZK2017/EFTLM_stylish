package com.pla.annoyingvillagers.compat.p1nero_bow;

import com.p1nero.epicfightbow.gameassets.EFBowColliders;
import com.pla.annoyingvillagers.animations.BowAttackAnimation;
import com.pla.annoyingvillagers.gameasset.AVAnimations;
import com.pla.annoyingvillagers.util.BowFunction;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.damagesource.StunType;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.MOD
)
public class AnimsP1neroEpicBow {
   public static AnimationAccessor<BowAttackAnimation> P1NERO_MOB_BOW_AUTO1;
   public static AnimationAccessor<BowAttackAnimation> P1NERO_MOB_BOW_AUTO2;
   public static AnimationAccessor<BowAttackAnimation> P1NERO_MOB_BOW_AUTO3;
   public static AnimationAccessor<AttackAnimation> P1NERO_MOB_BOW_DASH_ATTACK;
   public static AnimationAccessor<BowAttackAnimation> P1NERO_MOB_BOW_JUMP_ATTACK;

   public static void build(AnimationBuilder builder) {
      P1NERO_MOB_BOW_AUTO1 = builder.nextAccessor(
         "biped/p1nero_bow_clone/bow_auto1",
         accessor -> (BowAttackAnimation)new BowAttackAnimation(
                  0.15F,
                  0.0F,
                  0.15F,
                  1.0833334F,
                  1.0833334F,
                  InteractionHand.MAIN_HAND,
                  null,
                  ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                  accessor,
                  Armatures.BIPED
               )
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.5F, (livingEntityPatch, assetAccessor, objects) -> BowFunction.bowShoot(livingEntityPatch), Side.BOTH),
                     InTimeEvent.create(1.0F, (livingEntityPatch, assetAccessor, objects) -> BowFunction.bowShoot(livingEntityPatch), Side.BOTH),
                     InTimeEvent.create(1.1F, (livingEntityPatch, self, p) -> {
                        if (!livingEntityPatch.isLogicalClient()) {
                           livingEntityPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
                        }
                     }, Side.SERVER)
                  }
               )
      );
      P1NERO_MOB_BOW_AUTO2 = builder.nextAccessor(
         "biped/p1nero_bow_clone/bow_auto2",
         accessor -> (BowAttackAnimation)new BowAttackAnimation(
                  0.15F,
                  0.0F,
                  0.15F,
                  1.0833334F,
                  1.0833334F,
                  InteractionHand.MAIN_HAND,
                  null,
                  ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                  accessor,
                  Armatures.BIPED
               )
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.5F, (livingEntityPatch, assetAccessor, objects) -> BowFunction.bowShoot(livingEntityPatch), Side.BOTH),
                     InTimeEvent.create(1.0F, (livingEntityPatch, assetAccessor, objects) -> BowFunction.bowShoot(livingEntityPatch), Side.BOTH),
                     InTimeEvent.create(1.1F, (livingEntityPatch, self, p) -> {
                        if (!livingEntityPatch.isLogicalClient()) {
                           livingEntityPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
                        }
                     }, Side.SERVER)
                  }
               )
      );
      P1NERO_MOB_BOW_AUTO3 = builder.nextAccessor(
         "biped/p1nero_bow_clone/bow_auto3",
         accessor -> (BowAttackAnimation)new BowAttackAnimation(
                  0.15F,
                  0.0F,
                  0.15F,
                  1.6666666F,
                  2.0F,
                  InteractionHand.MAIN_HAND,
                  null,
                  ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                  accessor,
                  Armatures.BIPED
               )
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(1.3333334F, (livingEntityPatch, assetAccessor, objects) -> BowFunction.bowShoot(livingEntityPatch), Side.BOTH),
                     InTimeEvent.create(1.5F, (livingEntityPatch, assetAccessor, objects) -> BowFunction.bowShoot(livingEntityPatch), Side.BOTH),
                     InTimeEvent.create(1.6666666F, (livingEntityPatch, assetAccessor, objects) -> BowFunction.bowShoot(livingEntityPatch), Side.BOTH),
                     InTimeEvent.create(1.72F, (livingEntityPatch, self, p) -> {
                        if (!livingEntityPatch.isLogicalClient()) {
                           livingEntityPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
                        }
                     }, Side.SERVER)
                  }
               )
      );
      P1NERO_MOB_BOW_DASH_ATTACK = builder.nextAccessor(
         "biped/p1nero_bow_clone/bow_dash_attack",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.15F, 0.0F, 0.0F, 0.6666667F, 1.0F, EFBowColliders.BOW_DASH, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, accessor, Armatures.BIPED
               )
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(1.0F, (livingEntityPatch, self, p) -> {
                  if (!livingEntityPatch.isLogicalClient()) {
                     livingEntityPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
                  }
               }, Side.SERVER)})
      );
      P1NERO_MOB_BOW_JUMP_ATTACK = builder.nextAccessor(
         "biped/p1nero_bow_clone/bow_jump_attack",
         accessor -> (BowAttackAnimation)new BowAttackAnimation(
                  0.15F,
                  0.0F,
                  0.15F,
                  0.33333334F,
                  1.3333334F,
                  InteractionHand.MAIN_HAND,
                  null,
                  ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                  accessor,
                  Armatures.BIPED
               )
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.25F, (livingEntityPatch, assetAccessor, objects) -> BowFunction.bowShoot(livingEntityPatch), Side.BOTH),
                     InTimeEvent.create(0.33333334F, (livingEntityPatch, assetAccessor, objects) -> BowFunction.bowShoot(livingEntityPatch), Side.BOTH),
                     InTimeEvent.create(1.0F, (livingEntityPatch, self, p) -> {
                        if (!livingEntityPatch.isLogicalClient()) {
                           livingEntityPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
                        }
                     }, Side.SERVER)
                  }
               )
      );
   }
}
