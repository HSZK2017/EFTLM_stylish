package com.pla.annoyingvillagers.combatbehaviour;

import com.pla.annoyingvillagers.gameasset.AnimsEpicFight;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFightIronSpell;
import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import com.pla.annoyingvillagers.gameasset.AnimsWom;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.Behavior;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.Builder;
import reascer.wom.gameasset.animations.weapons.AnimsEnderblaster;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class HerobrineObsidianWeapon {
   public static final Builder<MobPatch<?>> OBSIDIAN_WEAPON = CECombatBehaviors.builder()
      .newBehaviorRoot(CombatBehaviourTemplates.executionRoot(5.0))
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(4.0)
            .weight(1000.0)
            .maxCooldown(0)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::canEscape)
                  .withinDistance(0.0, 8.0)
                  .animationBehavior(Animations.BIPED_STEP_BACKWARD, 0.0F)
                  .addExBehavior(CombatCommon::performEscapeRunAway)
            )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(3.0)
            .weight(100.0)
            .maxCooldown(120)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::canPerformNormalAttackLogic)
                  .custom(CombatCommon::canSwitchWeapon)
                  .animationBehavior(Animations.BIPED_ROLL_BACKWARD, 0.0F)
                  .addExBehavior(CombatCommon::switchWeapon)
            )
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::canPerformNormalAttackLogic)
                  .custom(CombatCommon::canSwitchWeapon)
                  .animationBehavior(Animations.BIPED_ROLL_FORWARD, 0.0F)
                  .addExBehavior(CombatCommon::switchWeapon)
            )
      )
      .newBehaviorRoot(CombatBehaviourTemplates.herobrineHealingRoot())
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(2.0)
            .weight(70.0)
            .maxCooldown(100)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::canPerformNormalAttackLogic)
                  .custom(HerobrineCommon::canSummonDarkOb)
                  .animationBehavior(AnimsEpicFightIronSpell.CASTING_ONE_HAND_BUFF, 0.0F)
                  .addExBehavior(HerobrineCommon::performSummonDarkOb)
            )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(15.0)
            .maxCooldown(600)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::canPerformNormalAttackLogic)
                  .custom(HerobrineCommon::canChangeToSecondForm)
                  .withinDistance(0.0, 8.0)
                  .animationBehavior(AnimsEnderblaster.ENDERBLASTER_ONEHAND_RELOAD, 0.0F)
                  .addExBehavior(HerobrineCommon::changeToSecondForm)
            )
      )
      .newBehaviorRoot(
         CombatCommon.addRandomCombatChains(
            BehaviorRoot.builder().priority(1.0).weight(40.0).maxCooldown(20),
            CombatCommon.animations(
               AnimsEpicFight.OBSIDIAN_FIST_AUTO1,
               AnimsEpicFight.OBSIDIAN_FIST_AUTO2,
               AnimsEpicFight.OBSIDIAN_FIST_AUTO3,
               AnimsEpicFight.OBSIDIAN_FIST_AIR_SLASH,
               AnimsEpicFight.OBSIDIAN_BIPED_LANDING
            ),
            CombatCommon.animations(AnimsPugilistSteve.OBSIDIAN_FIST_DASH, AnimsWom.OBSIDIAN_STRONG_PUNCH, AnimsWom.OBSIDIAN_ENDERBLASTER_TWOHAND_TISHNAW),
            CombatCommon.basicKickAnimations(),
            CombatCommon.rollStepAnimations()
         )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(20.0)
            .maxCooldown(100)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::canPerformNormalAttackLogic)
                  .custom(HerobrineCommon::canShootDarkOb)
                  .withinDistance(5.0, 10.0)
                  .animationBehavior(AnimsEpicFightIronSpell.CASTING_ONE_HAND_TOP, 0.0F)
                  .addExBehavior(HerobrineCommon::performShootDarkOb)
            )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(15.0)
            .maxCooldown(200)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::canPerformNormalAttackLogic)
                  .custom(HerobrineCommon::canPlayObsidianMachine)
                  .animationBehavior(Animations.BIPED_STEP_BACKWARD, 0.0F)
                  .addExBehavior(HerobrineCommon::performObsidianMachine)
            )
      )
      .newBehaviorRoot(CombatBehaviourTemplates.guardRoot(HerobrineCommon::canPerformGuarding))
      .newBehaviorRoot(CombatBehaviourTemplates.herobrineJumpRoot());
}
