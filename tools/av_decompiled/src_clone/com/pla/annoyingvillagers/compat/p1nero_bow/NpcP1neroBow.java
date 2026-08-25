package com.pla.annoyingvillagers.compat.p1nero_bow;

import com.pla.annoyingvillagers.combatbehaviour.CombatCommon;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFightACG;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFightIronSpell;
import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.Behavior;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.Builder;
import net.shelmarow.combat_evolution.ai.condition.HealthCheck.Comparator;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class NpcP1neroBow {
   public static final Builder<MobPatch<?>> BOW = CECombatBehaviors.builder()
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(2.0)
            .weight(100.0)
            .maxCooldown(0)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::isNotRiding)
                  .withinDistance(0.0, 5.0)
                  .animationBehavior(Animations.BIPED_STEP_FORWARD, 0.0F)
                  .addExBehavior(CombatCommon::swapToMelee)
            )
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::isNotRiding)
                  .withinDistance(0.0, 5.0)
                  .animationBehavior(Animations.BIPED_STEP_BACKWARD, 0.0F)
                  .addExBehavior(CombatCommon::swapToMelee)
            )
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::hasClearBowShot)
                  .custom(CombatCommon::isNotRiding)
                  .withinDistance(0.0, 5.0)
                  .animationBehavior(AnimsP1neroEpicBow.P1NERO_MOB_BOW_JUMP_ATTACK, 0.0F)
                  .addExBehavior(CombatCommon::shortPillarJump)
            )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(2.0)
            .weight(100.0)
            .maxCooldown(0)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::isRiding)
                  .withinDistance(0.0, 5.0)
                  .animationBehavior(AnimsPugilistSteve.KNIFE_CHECK, 0.0F)
                  .addExBehavior(CombatCommon::swapToMelee)
            )
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::isRiding)
                  .withinDistance(0.0, 5.0)
                  .animationBehavior(AnimsPugilistSteve.KNIFE_CHECK, 0.0F)
                  .addExBehavior(CombatCommon::swapToMelee)
            )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(2.0)
            .weight(70.0)
            .maxCooldown(0)
            .addFirstBehavior(
               Behavior.builder()
                  .health(0.6666667F, Comparator.LESS_RATIO_CONTAIN)
                  .custom(CombatCommon::canPerformEating)
                  .animationBehavior(Animations.BIPED_STEP_RIGHT, 0.0F)
                  .addExBehavior(CombatCommon::performEatingAnimation)
            )
            .addFirstBehavior(
               Behavior.builder()
                  .health(0.6666667F, Comparator.LESS_RATIO_CONTAIN)
                  .custom(CombatCommon::canPerformEating)
                  .animationBehavior(Animations.BIPED_STEP_LEFT, 0.0F)
                  .addExBehavior(CombatCommon::performEatingAnimation)
            )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(40.0)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::hasClearBowShot)
                  .withinDistance(7.0, 14.0)
                  .animationBehavior(AnimsP1neroEpicBow.P1NERO_MOB_BOW_AUTO1, 0.0F)
            )
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::hasClearBowShot)
                  .withinDistance(7.0, 14.0)
                  .animationBehavior(AnimsP1neroEpicBow.P1NERO_MOB_BOW_AUTO2, 0.0F)
            )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(20.0)
            .maxCooldown(100)
            .addFirstBehavior(
               Behavior.builder().custom(CombatCommon::hasClearBowShot).withinDistance(7.0, 14.0).animationBehavior(AnimsEpicFightACG.BOW_AUTO_2, 0.0F)
            )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(10.0)
            .maxCooldown(100)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::isNotRiding)
                  .withinDistance(20.0, 25.0)
                  .animationBehavior(AnimsP1neroEpicBow.P1NERO_MOB_BOW_DASH_ATTACK, 0.0F)
            )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(40.0)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::hasClearBowShot)
                  .custom(CombatCommon::isTargetingHerobrineDragon)
                  .withinDistance(7.0, 80.0)
                  .animationBehavior(AnimsP1neroEpicBow.P1NERO_MOB_BOW_AUTO1, 0.0F)
            )
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::hasClearBowShot)
                  .custom(CombatCommon::isTargetingHerobrineDragon)
                  .withinDistance(7.0, 80.0)
                  .animationBehavior(AnimsP1neroEpicBow.P1NERO_MOB_BOW_AUTO2, 0.0F)
            )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(20.0)
            .maxCooldown(100)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::hasClearBowShot)
                  .custom(CombatCommon::isTargetingHerobrineDragon)
                  .custom(CombatCommon::isNotRiding)
                  .withinDistance(7.0, 80.0)
                  .animationBehavior(AnimsEpicFightACG.BOW_AUTO_2, 0.0F)
            )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(10.0)
            .addFirstBehavior(
               Behavior.builder().custom(CombatCommon::isNotRiding).withinDistance(7.0, 14.0).animationBehavior(Animations.BIPED_STEP_FORWARD, 0.0F)
            )
            .addFirstBehavior(
               Behavior.builder().custom(CombatCommon::isNotRiding).withinDistance(7.0, 14.0).animationBehavior(Animations.BIPED_STEP_BACKWARD, 0.0F)
            )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(60.0)
            .maxCooldown(120)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::isNotRiding)
                  .custom(CombatCommon::canThrowEnderPearl)
                  .withinDistance(7.0, 14.0)
                  .animationBehavior(AnimsEpicFightIronSpell.CASTING_ONE_HAND_TOP, 0.0F)
                  .addExBehavior(CombatCommon::performEnderPearlToTarget)
            )
      );
}
