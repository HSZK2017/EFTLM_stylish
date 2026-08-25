package com.pla.annoyingvillagers.combatbehaviour;

import com.pla.annoyingvillagers.gameasset.AnimsEpicFightIronSpell;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.Behavior;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.Behavior.Builder;
import net.shelmarow.combat_evolution.ai.condition.HealthCheck.Comparator;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public final class CombatBehaviourTemplates {
   private CombatBehaviourTemplates() {
   }

   private static Builder<MobPatch<?>> withCondition(Builder<MobPatch<?>> behavior, CombatCommon.MobPatchCondition condition) {
      return condition == null ? behavior : behavior.custom(condition);
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> executionRoot() {
      return executionRoot(4.0);
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> executionRoot(double priority) {
      return BehaviorRoot.builder()
         .priority(priority)
         .weight(1000.0)
         .maxCooldown(0)
         .addFirstBehavior(
            Behavior.builder()
               .custom(CombatCommon::canExecute)
               .withinDistance(0.0, 5.0)
               .animationBehavior(Animations.BIPED_SNEAK, 0.0F)
               .addExBehavior(CombatCommon::performExecute)
         );
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> escapeWithGuardRoot() {
      return escapeWithGuardRoot(Animations.BIPED_STEP_BACKWARD);
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> escapeWithGuardRoot(
      AnimationAccessor<? extends StaticAnimation> escapeAnimation
   ) {
      return escapeWithGuardRoot(3.0, escapeAnimation, false);
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> escapeWithGuardRoot(
      CombatCommon.MobPatchCondition movesetCondition, AnimationAccessor<? extends StaticAnimation> escapeAnimation
   ) {
      return escapeWithGuardRoot(3.0, movesetCondition, escapeAnimation, false);
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> escapeWithGuardRoot(
      AnimationAccessor<? extends StaticAnimation> escapeAnimation, boolean requireNormalAttackLogic
   ) {
      return escapeWithGuardRoot(3.0, escapeAnimation, requireNormalAttackLogic);
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> escapeWithGuardRoot(
      CombatCommon.MobPatchCondition movesetCondition, AnimationAccessor<? extends StaticAnimation> escapeAnimation, boolean requireNormalAttackLogic
   ) {
      return escapeWithGuardRoot(3.0, movesetCondition, escapeAnimation, requireNormalAttackLogic);
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> escapeWithGuardRoot(
      double priority, AnimationAccessor<? extends StaticAnimation> escapeAnimation, boolean requireNormalAttackLogic
   ) {
      return escapeWithGuardRoot(priority, null, escapeAnimation, requireNormalAttackLogic);
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> escapeWithGuardRoot(
      double priority,
      CombatCommon.MobPatchCondition movesetCondition,
      AnimationAccessor<? extends StaticAnimation> escapeAnimation,
      boolean requireNormalAttackLogic
   ) {
      Builder<MobPatch<?>> escapeBehavior = withCondition(Behavior.builder(), movesetCondition);
      if (requireNormalAttackLogic) {
         escapeBehavior = escapeBehavior.custom(CombatCommon::canPerformNormalAttackLogic);
      }

      return BehaviorRoot.builder()
         .priority(priority)
         .weight(1000.0)
         .maxCooldown(0)
         .addFirstBehavior(
            escapeBehavior.custom(CombatCommon::canEscape)
               .withinDistance(0.0, 8.0)
               .animationBehavior(escapeAnimation, 0.0F)
               .addExBehavior(CombatCommon::swapToBlockToEscape)
         )
         .addFirstBehavior(withCondition(Behavior.builder(), movesetCondition).custom(CombatCommon::canEscape).withinDistance(0.0, 48.0).guard(40));
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> escapeWithAnimationRoot(
      AnimationAccessor<? extends StaticAnimation> escapeAnimation, AnimationAccessor<? extends StaticAnimation> followUpAnimation
   ) {
      return escapeWithAnimationRoot(3.0, escapeAnimation, followUpAnimation, false);
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> escapeWithAnimationRoot(
      double priority,
      AnimationAccessor<? extends StaticAnimation> escapeAnimation,
      AnimationAccessor<? extends StaticAnimation> followUpAnimation,
      boolean requireNormalAttackLogic
   ) {
      return escapeWithAnimationRoot(priority, null, escapeAnimation, followUpAnimation, requireNormalAttackLogic);
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> escapeWithAnimationRoot(
      CombatCommon.MobPatchCondition movesetCondition,
      AnimationAccessor<? extends StaticAnimation> escapeAnimation,
      AnimationAccessor<? extends StaticAnimation> followUpAnimation
   ) {
      return escapeWithAnimationRoot(3.0, movesetCondition, escapeAnimation, followUpAnimation, false);
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> escapeWithAnimationRoot(
      double priority,
      CombatCommon.MobPatchCondition movesetCondition,
      AnimationAccessor<? extends StaticAnimation> escapeAnimation,
      AnimationAccessor<? extends StaticAnimation> followUpAnimation,
      boolean requireNormalAttackLogic
   ) {
      Builder<MobPatch<?>> escapeBehavior = withCondition(Behavior.builder(), movesetCondition);
      if (requireNormalAttackLogic) {
         escapeBehavior = escapeBehavior.custom(CombatCommon::canPerformNormalAttackLogic);
      }

      return BehaviorRoot.builder()
         .priority(priority)
         .weight(1000.0)
         .maxCooldown(0)
         .addFirstBehavior(
            escapeBehavior.custom(CombatCommon::canEscape)
               .withinDistance(0.0, 8.0)
               .animationBehavior(escapeAnimation, 0.0F)
               .addExBehavior(CombatCommon::swapToBlockToEscape)
         )
         .addFirstBehavior(
            withCondition(Behavior.builder(), movesetCondition)
               .custom(CombatCommon::canEscape)
               .withinDistance(0.0, 48.0)
               .animationBehavior(followUpAnimation, 0.0F)
         );
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> eatingRoot() {
      return eatingRoot(Animations.BIPED_STEP_BACKWARD);
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> eatingRoot(
      AnimationAccessor<? extends StaticAnimation> animation
   ) {
      return eatingRoot(null, animation);
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> eatingRoot(
      CombatCommon.MobPatchCondition movesetCondition, AnimationAccessor<? extends StaticAnimation> animation
   ) {
      return BehaviorRoot.builder()
         .priority(2.0)
         .weight(70.0)
         .maxCooldown(0)
         .addFirstBehavior(
            withCondition(Behavior.builder(), movesetCondition)
               .health(0.6666667F, Comparator.LESS_RATIO_CONTAIN)
               .custom(CombatCommon::canPerformEating)
               .animationBehavior(animation, 0.0F)
               .addExBehavior(CombatCommon::performEatingAnimation)
         );
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> swapToBowRoot() {
      return swapToBowRoot(Animations.BIPED_STEP_BACKWARD, Animations.BIPED_STEP_FORWARD);
   }

   @SafeVarargs
   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> swapToBowRoot(
      AnimationAccessor<? extends StaticAnimation>... animations
   ) {
      return swapToBowRoot(null, animations);
   }

   @SafeVarargs
   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> swapToBowRoot(
      CombatCommon.MobPatchCondition movesetCondition, AnimationAccessor<? extends StaticAnimation>... animations
   ) {
      net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> root = BehaviorRoot.builder()
         .priority(2.0)
         .weight(100.0)
         .maxCooldown(120);

      for (AnimationAccessor<? extends StaticAnimation> animation : animations) {
         root = root.addFirstBehavior(
            withCondition(Behavior.builder(), movesetCondition)
               .custom(CombatCommon::canPerformNormalAttackLogic)
               .custom(CombatCommon::canSwapToBow)
               .withinDistance(7.0, 14.0)
               .animationBehavior(animation, 0.0F)
               .addExBehavior(CombatCommon::swapToBow)
         );
      }

      return root;
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> enderPearlToTargetRoot() {
      return enderPearlToTargetRoot(false);
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> combatFishingRodRoot() {
      return BehaviorRoot.builder()
         .priority(2.0)
         .weight(55.0)
         .maxCooldown(35)
         .addFirstBehavior(
            Behavior.builder()
               .custom(CombatCommon::canPerformNormalAttackLogic)
               .custom(CombatCommon::canUseNpcCombatFishingRod)
               .withinDistance(0.0, 32.0)
               .animationBehavior(AnimsEpicFightIronSpell.CASTING_ONE_HAND_TOP, 0.0F)
               .addExBehavior(CombatCommon::performNpcCombatFishingRod)
         );
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> combatFishingRodEscapeRoot() {
      return BehaviorRoot.builder()
         .priority(3.0)
         .weight(1000.0)
         .maxCooldown(35)
         .addFirstBehavior(
            Behavior.builder()
               .custom(CombatCommon::canEscape)
               .custom(CombatCommon::canUseNpcCombatFishingRodEscape)
               .withinDistance(0.0, 32.0)
               .animationBehavior(AnimsEpicFightIronSpell.CASTING_ONE_HAND_TOP, 0.0F)
               .addExBehavior(CombatCommon::performNpcCombatFishingRodEscape)
         );
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> villagerKnightLavaBucketRoot() {
      return BehaviorRoot.builder()
         .priority(2.0)
         .weight(30.0)
         .maxCooldown(120)
         .addFirstBehavior(
            Behavior.builder()
               .custom(CombatCommon::canPerformNormalAttackLogic)
               .custom(CombatCommon::isGeneral)
               .custom(CombatCommon::canUseVillagerKnightLavaBucket)
               .withinDistance(0.0, 5.0)
               .animationBehavior(AnimsEpicFightIronSpell.CASTING_ONE_HAND_TOP, 0.0F)
               .addExBehavior(CombatCommon::performVillagerKnightLavaBucket)
         );
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> enderPearlToTargetRoot(
      boolean requireAttackWhileNotHealing
   ) {
      Builder<MobPatch<?>> behavior = Behavior.builder().custom(CombatCommon::canPerformNormalAttackLogic).custom(CombatCommon::canThrowEnderPearl);
      if (requireAttackWhileNotHealing) {
         behavior = behavior.custom(CombatCommon::canAttackWhileNotHealing);
      }

      return BehaviorRoot.builder()
         .priority(2.0)
         .weight(80.0)
         .maxCooldown(120)
         .addFirstBehavior(
            behavior.withinDistance(7.0, 48.0)
               .animationBehavior(AnimsEpicFightIronSpell.CASTING_ONE_HAND_TOP, 0.0F)
               .addExBehavior(CombatCommon::performEnderPearlToTarget)
         );
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> enderPearlAwayRoot(boolean requireAttackWhileNotHealing) {
      return enderPearlAwayRoot(40, requireAttackWhileNotHealing);
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> enderPearlAwayRoot(
      CombatCommon.MobPatchCondition movesetCondition, boolean requireAttackWhileNotHealing
   ) {
      return enderPearlAwayRoot(40, movesetCondition, requireAttackWhileNotHealing);
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> enderPearlAwayRoot(
      int maxCooldown, boolean requireAttackWhileNotHealing
   ) {
      return enderPearlAwayRoot(maxCooldown, null, requireAttackWhileNotHealing);
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> enderPearlAwayRoot(
      int maxCooldown, CombatCommon.MobPatchCondition movesetCondition, boolean requireAttackWhileNotHealing
   ) {
      Builder<MobPatch<?>> behavior = withCondition(Behavior.builder(), movesetCondition)
         .custom(CombatCommon::canPerformNormalAttackLogic)
         .withinDistance(0.0, 3.0)
         .custom(CombatCommon::canThrowEnderPearl);
      if (requireAttackWhileNotHealing) {
         behavior = behavior.custom(CombatCommon::canAttackWhileNotHealing);
      }

      return BehaviorRoot.builder()
         .priority(1.0)
         .weight(10.0)
         .maxCooldown(maxCooldown)
         .addFirstBehavior(behavior.animationBehavior(AnimsEpicFightIronSpell.CASTING_ONE_HAND_TOP, 0.0F).addExBehavior(CombatCommon::performEnderPearlAway));
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> guardRoot() {
      return guardRoot(0.0, CombatCommon::canPerformGuarding);
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> guardRoot(double minDistance) {
      return guardRoot(minDistance, CombatCommon::canPerformGuarding);
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> guardRoot(CombatCommon.MobPatchCondition guardCondition) {
      return guardRoot(0.0, guardCondition);
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> guardRoot(
      double minDistance, CombatCommon.MobPatchCondition guardCondition
   ) {
      return guardRoot(minDistance, 3.0, guardCondition);
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> guardRoot(
      double minDistance, double maxDistance, CombatCommon.MobPatchCondition guardCondition
   ) {
      return BehaviorRoot.builder()
         .priority(1.0)
         .weight(15.0)
         .addFirstBehavior(
            Behavior.builder().custom(CombatCommon::canPerformNormalAttackLogic).withinDistance(minDistance, maxDistance).custom(guardCondition).guard(40)
         );
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> jumpRoot() {
      return BehaviorRoot.builder()
         .priority(1.0)
         .weight(40.0)
         .maxCooldown(160)
         .addFirstBehavior(
            Behavior.builder()
               .custom(CombatCommon::canPerformNormalAttackLogic)
               .custom(CombatCommon::canJump)
               .withinDistance(5.0, 14.0)
               .animationBehavior(Animations.BIPED_JUMP, 0.0F)
               .addExBehavior(CombatCommon::jump)
         );
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> herobrineHealingRoot() {
      return BehaviorRoot.builder()
         .priority(2.0)
         .weight(70.0)
         .maxCooldown(0)
         .addFirstBehavior(
            Behavior.builder()
               .custom(CombatCommon::canPerformNormalAttackLogic)
               .health(0.6666667F, Comparator.LESS_RATIO_CONTAIN)
               .custom(HerobrineCommon::canPerformHealing)
               .animationBehavior(AnimsEpicFightIronSpell.CASTING_ONE_HAND_BUFF, 0.0F)
               .addExBehavior(HerobrineCommon::performHealingAnimation)
         );
   }

   public static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> herobrineJumpRoot() {
      return BehaviorRoot.builder()
         .priority(1.0)
         .weight(20.0)
         .maxCooldown(160)
         .addFirstBehavior(
            Behavior.builder()
               .custom(CombatCommon::canPerformNormalAttackLogic)
               .custom(HerobrineCommon::canJump)
               .withinDistance(5.0, 14.0)
               .animationBehavior(Animations.BIPED_JUMP, 0.0F)
               .addExBehavior(HerobrineCommon::jump)
         );
   }
}
