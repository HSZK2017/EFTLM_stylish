package com.pla.annoyingvillagers.combatbehaviour;

import com.pla.annoyingvillagers.gameasset.AnimsEpicFightIronSpell;
import com.pla.annoyingvillagers.gameasset.AnimsSculkSteve;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.Behavior;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.Builder;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class GregFist {
   public static final Builder<MobPatch<?>> GREG_FIST = CECombatBehaviors.builder()
      .newBehaviorRoot(CombatBehaviourTemplates.executionRoot(5.0))
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(4.0)
            .weight(1000.0)
            .maxCooldown(0)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::canEscape)
                  .custom(HerobrineCommon::canPerformPortalEscapeStepBack)
                  .withinDistance(0.0, 48.0)
                  .animationBehavior(AnimsEpicFightIronSpell.CASTING_ONE_HAND_TOP, 0.0F)
                  .addExBehavior(HerobrineCommon::performPortalEscapeStepBack)
            )
            .addFirstBehavior(Behavior.builder().custom(CombatCommon::canEscape).withinDistance(0.0, 48.0).guard(20))
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(45.0)
            .maxCooldown(10)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(HerobrineCommon::isSupportingHerobrineEscaping)
                  .animationBehavior(AnimsSculkSteve.PORTAL_SUMMON, 0.0F)
                  .addExBehavior(HerobrineCommon::summonSupportingHerobrineEscapePortal)
            )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(35.0)
            .maxCooldown(10)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(HerobrineCommon::isSupportingHerobrineGettingShot)
                  .animationBehavior(AnimsEpicFightIronSpell.CASTING_ONE_HAND_TOP, 0.0F)
                  .addExBehavior(HerobrineCommon::summonSupportCounterPortal)
            )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(30.0)
            .maxCooldown(10)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(HerobrineCommon::canDo6Portal)
                  .animationBehavior(AnimsSculkSteve.PORTAL_SUMMON, 0.0F)
                  .addExBehavior(HerobrineCommon::do6Portal)
            )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(30.0)
            .maxCooldown(10)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(HerobrineCommon::canSummonLowCloneSupport)
                  .animationBehavior(AnimsSculkSteve.PORTAL_SUMMON, 0.0F)
                  .addExBehavior(HerobrineCommon::summonLowCloneSupport)
            )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(25.0)
            .maxCooldown(10)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(HerobrineCommon::canSummon2Portal)
                  .animationBehavior(AnimsEpicFightIronSpell.CASTING_ONE_HAND_TOP, 0.0F)
                  .addExBehavior(HerobrineCommon::summon2Portal)
            )
      );
}
