package com.pla.annoyingvillagers.combatbehaviour;

import com.pla.annoyingvillagers.gameasset.AnimsEpicFightACG;
import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.Behavior;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.Builder;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class PlayerNpcBow {
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
                  .animationBehavior(Animations.BIPED_ROLL_BACKWARD, 0.0F)
                  .addExBehavior(CombatCommon::swapToMelee)
            )
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::isNotRiding)
                  .withinDistance(0.0, 5.0)
                  .animationBehavior(Animations.BIPED_ROLL_FORWARD, 0.0F)
                  .addExBehavior(CombatCommon::swapToMelee)
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
      .newBehaviorRoot(CombatBehaviourTemplates.eatingRoot(Animations.BIPED_ROLL_BACKWARD))
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(40.0)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::hasClearBowShot)
                  .withinDistance(7.0, 14.0)
                  .animationBehavior(AnimsEpicFightACG.BOW_AUTO_1, 0.0F)
                  .addNextBehavior(
                     Behavior.builder()
                        .custom(CombatCommon::hasClearBowShot)
                        .withinDistance(7.0, 14.0)
                        .animationBehavior(AnimsEpicFightACG.BOW_AUTO_1, 0.0F)
                        .addNextBehavior(
                           Behavior.builder()
                              .custom(CombatCommon::hasClearBowShot)
                              .withinDistance(7.0, 14.0)
                              .animationBehavior(AnimsEpicFightACG.BOW_AUTO_1, 0.0F)
                        )
                  )
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
                  .custom(CombatCommon::isNotRiding)
                  .withinDistance(7.0, 14.0)
                  .animationBehavior(AnimsEpicFightACG.BOW_AUTO_2, 0.0F)
            )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(10.0)
            .maxCooldown(100)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::hasClearBowShot)
                  .custom(CombatCommon::isNotRiding)
                  .withinDistance(7.0, 14.0)
                  .animationBehavior(AnimsEpicFightACG.BOW_AUTO_3, 0.0F)
            )
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::hasClearBowShot)
                  .custom(CombatCommon::isNotRiding)
                  .withinDistance(7.0, 14.0)
                  .animationBehavior(AnimsEpicFightACG.BOW_AUTO_5, 0.0F)
            )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(10.0)
            .maxCooldown(100)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::hasClearBowShot)
                  .custom(CombatCommon::isNotRiding)
                  .withinDistance(7.0, 14.0)
                  .animationBehavior(AnimsEpicFightACG.BOW_AUTO_3, 0.0F)
            )
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::hasClearBowShot)
                  .custom(CombatCommon::isNotRiding)
                  .withinDistance(7.0, 14.0)
                  .animationBehavior(AnimsEpicFightACG.BOW_AUTO_5, 0.0F)
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
                  .animationBehavior(AnimsEpicFightACG.BOW_AUTO_1, 0.0F)
                  .addNextBehavior(
                     Behavior.builder()
                        .custom(CombatCommon::hasClearBowShot)
                        .custom(CombatCommon::isTargetingHerobrineDragon)
                        .withinDistance(7.0, 80.0)
                        .animationBehavior(AnimsEpicFightACG.BOW_AUTO_1, 0.0F)
                        .addNextBehavior(
                           Behavior.builder()
                              .custom(CombatCommon::hasClearBowShot)
                              .custom(CombatCommon::isTargetingHerobrineDragon)
                              .withinDistance(7.0, 80.0)
                              .animationBehavior(AnimsEpicFightACG.BOW_AUTO_1, 0.0F)
                        )
                  )
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
            .maxCooldown(100)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::hasClearBowShot)
                  .custom(CombatCommon::isTargetingHerobrineDragon)
                  .custom(CombatCommon::isNotRiding)
                  .withinDistance(7.0, 80.0)
                  .animationBehavior(AnimsEpicFightACG.BOW_AUTO_3, 0.0F)
            )
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::hasClearBowShot)
                  .custom(CombatCommon::isTargetingHerobrineDragon)
                  .custom(CombatCommon::isNotRiding)
                  .withinDistance(7.0, 80.0)
                  .animationBehavior(AnimsEpicFightACG.BOW_AUTO_5, 0.0F)
            )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(10.0)
            .addFirstBehavior(
               Behavior.builder().custom(CombatCommon::isNotRiding).withinDistance(7.0, 14.0).animationBehavior(Animations.BIPED_ROLL_BACKWARD, 0.0F)
            )
            .addFirstBehavior(
               Behavior.builder().custom(CombatCommon::isNotRiding).withinDistance(7.0, 14.0).animationBehavior(Animations.BIPED_ROLL_FORWARD, 0.0F)
            )
      )
      .newBehaviorRoot(CombatBehaviourTemplates.enderPearlToTargetRoot());
}
