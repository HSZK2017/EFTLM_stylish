package com.pla.annoyingvillagers.combatbehaviour;

import java.util.Arrays;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.Behavior;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.Builder;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public final class AvNpcCombatBehaviorBuilder {
   private static final int WEAPON_ENDER_PEARL_AWAY_COOLDOWN = 60;

   private AvNpcCombatBehaviorBuilder() {
   }

   @SafeVarargs
   public static Builder<MobPatch<?>> weapon(AnimationAccessor<? extends StaticAnimation>[] opener, AnimationAccessor<? extends StaticAnimation>[]... groups) {
      return CECombatBehaviors.builder()
         .newBehaviorRoot(CombatBehaviourTemplates.executionRoot())
         .newBehaviorRoot(CombatBehaviourTemplates.eatingRoot(CombatCommon::usesStepMoveset, Animations.BIPED_STEP_BACKWARD))
         .newBehaviorRoot(CombatBehaviourTemplates.eatingRoot(CombatCommon::usesRollMoveset, Animations.BIPED_ROLL_BACKWARD))
         .newBehaviorRoot(CombatBehaviourTemplates.swapToBowRoot(CombatCommon::usesStepMoveset, Animations.BIPED_STEP_BACKWARD, Animations.BIPED_STEP_FORWARD))
         .newBehaviorRoot(CombatBehaviourTemplates.swapToBowRoot(CombatCommon::usesRollMoveset, Animations.BIPED_ROLL_BACKWARD, Animations.BIPED_ROLL_FORWARD))
         .newBehaviorRoot(CombatBehaviourTemplates.enderPearlToTargetRoot())
         .newBehaviorRoot(CombatBehaviourTemplates.combatFishingRodRoot())
         .newBehaviorRoot(CombatBehaviourTemplates.villagerKnightLavaBucketRoot())
         .newBehaviorRoot(combatRoot(CombatCommon::usesStepMoveset, CombatCommon.stepAnimations(), CombatCommon.kickAnimations(), opener, groups))
         .newBehaviorRoot(combatRoot(CombatCommon::usesRollMoveset, CombatCommon.rollAnimations(), CombatCommon.kickAnimations(), opener, groups))
         .newBehaviorRoot(CombatBehaviourTemplates.combatFishingRodEscapeRoot())
         .newBehaviorRoot(CombatBehaviourTemplates.enderPearlAwayRoot(60, false))
         .newBehaviorRoot(CombatBehaviourTemplates.guardRoot())
         .newBehaviorRoot(CombatBehaviourTemplates.jumpRoot());
   }

   @SafeVarargs
   public static Builder<MobPatch<?>> fist(AnimationAccessor<? extends StaticAnimation>[] opener, AnimationAccessor<? extends StaticAnimation>[]... groups) {
      return CECombatBehaviors.builder()
         .newBehaviorRoot(CombatBehaviourTemplates.executionRoot())
         .newBehaviorRoot(escapeRunAwayRoot(CombatCommon::usesStepMoveset, Animations.BIPED_STEP_BACKWARD))
         .newBehaviorRoot(escapeRunAwayRoot(CombatCommon::usesRollMoveset, Animations.BIPED_ROLL_BACKWARD))
         .newBehaviorRoot(CombatBehaviourTemplates.eatingRoot(CombatCommon::usesStepMoveset, Animations.BIPED_STEP_BACKWARD))
         .newBehaviorRoot(CombatBehaviourTemplates.eatingRoot(CombatCommon::usesRollMoveset, Animations.BIPED_ROLL_BACKWARD))
         .newBehaviorRoot(
            CombatBehaviourTemplates.swapToBowRoot(
               CombatCommon::usesStepMoveset,
               Animations.BIPED_STEP_RIGHT,
               Animations.BIPED_STEP_LEFT,
               Animations.BIPED_STEP_BACKWARD,
               Animations.BIPED_STEP_FORWARD
            )
         )
         .newBehaviorRoot(CombatBehaviourTemplates.swapToBowRoot(CombatCommon::usesRollMoveset, Animations.BIPED_ROLL_BACKWARD, Animations.BIPED_ROLL_FORWARD))
         .newBehaviorRoot(CombatBehaviourTemplates.enderPearlToTargetRoot(true))
         .newBehaviorRoot(CombatBehaviourTemplates.combatFishingRodRoot())
         .newBehaviorRoot(CombatBehaviourTemplates.villagerKnightLavaBucketRoot())
         .newBehaviorRoot(combatRoot(CombatCommon::usesStepMoveset, CombatCommon.stepAnimations(), CombatCommon.fistKickAnimations(), opener, groups))
         .newBehaviorRoot(combatRoot(CombatCommon::usesRollMoveset, CombatCommon.rollAnimations(), CombatCommon.fistKickAnimations(), opener, groups))
         .newBehaviorRoot(CombatBehaviourTemplates.combatFishingRodEscapeRoot())
         .newBehaviorRoot(CombatBehaviourTemplates.enderPearlAwayRoot(true))
         .newBehaviorRoot(CombatBehaviourTemplates.jumpRoot());
   }

   @SafeVarargs
   private static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> combatRoot(
      CombatCommon.MobPatchCondition movesetCondition,
      AnimationAccessor<? extends StaticAnimation>[] movementAnimations,
      AnimationAccessor<? extends StaticAnimation>[] kickAnimations,
      AnimationAccessor<? extends StaticAnimation>[] opener,
      AnimationAccessor<? extends StaticAnimation>[]... groups
   ) {
      return CombatCommon.addRandomCombatChains(
         BehaviorRoot.builder().priority(1.0).weight(40.0).maxCooldown(20),
         CombatCommon.conditions(movesetCondition),
         opener,
         appendMovement(groups, kickAnimations, movementAnimations)
      );
   }

   private static AnimationAccessor<? extends StaticAnimation>[][] appendMovement(
      AnimationAccessor<? extends StaticAnimation>[][] groups,
      AnimationAccessor<? extends StaticAnimation>[] kickAnimations,
      AnimationAccessor<? extends StaticAnimation>[] movementAnimations
   ) {
      AnimationAccessor<? extends StaticAnimation>[][] result = Arrays.copyOf(groups, groups.length + 2);
      result[groups.length] = kickAnimations;
      result[groups.length + 1] = movementAnimations;
      return result;
   }

   private static net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot.Builder<MobPatch<?>> escapeRunAwayRoot(
      CombatCommon.MobPatchCondition movesetCondition, AnimationAccessor<? extends StaticAnimation> animation
   ) {
      return BehaviorRoot.builder()
         .priority(3.0)
         .weight(1000.0)
         .maxCooldown(0)
         .addFirstBehavior(
            Behavior.builder()
               .custom(movesetCondition)
               .custom(CombatCommon::canEscape)
               .withinDistance(0.0, 8.0)
               .animationBehavior(animation, 0.0F)
               .addExBehavior(CombatCommon::performEscapeRunAway)
         )
         .addFirstBehavior(
            Behavior.builder()
               .custom(movesetCondition)
               .custom(CombatCommon::canAttackWhileNotHealing)
               .custom(CombatCommon::canEscape)
               .withinDistance(0.0, 48.0)
               .animationBehavior(Animations.BIPED_SNEAK, 0.0F)
               .addExBehavior(CombatCommon::swapToMelee)
         );
   }
}
