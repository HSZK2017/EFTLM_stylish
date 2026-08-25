package com.pla.annoyingvillagers.combatbehaviour;

import com.pla.annoyingvillagers.gameasset.AnimsEpicFight;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFightDualGreatsword;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFightInfernalGainer;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFightIronSpell;
import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import com.pla.annoyingvillagers.gameasset.AnimsWom;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.Behavior;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.Builder;
import reascer.wom.gameasset.WOMAnimations;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class HerobrineShadowObsidianSword {
   public static final Builder<MobPatch<?>> SHADOW_OBSIDIAN_SWORD = CECombatBehaviors.builder()
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
         CombatCommon.addRandomCombatChains(
            BehaviorRoot.builder().priority(1.0).weight(40.0).maxCooldown(20),
            CombatCommon.animations(
               AnimsEpicFightDualGreatsword.SHADOW_OBSIDIAN_SWORD_GREATSWORD_TWOHAND_AUTO_1,
               AnimsEpicFightDualGreatsword.SHADOW_OBSIDIAN_SWORD_GREATSWORD_TWOHAND_AUTO_2,
               AnimsPugilistSteve.SHADOW_OBSIDIAN_SWORD_ONEHAND_LONG,
               AnimsEpicFight.SHADOW_OBSIDIAN_FIST_AIR_SLASH
            ),
            CombatCommon.animations(
               AnimsPugilistSteve.OBSIDIAN_FIST_DASH,
               AnimsPugilistSteve.SWORD_HEAVY_AUTO_1,
               AnimsWom.SHADOW_OBSIDIAN_SWORD_TORMENT_BERSERK_DASH,
               AnimsWom.SHADOW_OBSIDIAN_SWORD_TORMENT_AIRSLAM,
               AnimsEpicFightInfernalGainer.OBSIDIAN_INFERNAL_AUTO_2
            ),
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
                  .withinDistance(0.0, 5.0)
                  .animationBehavior(AnimsWom.SHADOW_OBSIDIAN_SWORD_TORMENT_BERSERK_DASH, 0.0F)
                  .addNextBehavior(
                     Behavior.builder()
                        .custom(CombatCommon::canPerformNormalAttackLogic)
                        .withinDistance(0.0, 6.0)
                        .animationBehavior(AnimsWom.SHADOW_OBSIDIAN_SWORD_TORMENT_BERSERK_DASH, 0.0F)
                  )
            )
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
   public static final Builder<MobPatch<?>> SHADOW_OBSIDIAN_DUAL_SWORD = CECombatBehaviors.builder()
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
         CombatCommon.addRandomCombatChains(
            BehaviorRoot.builder().priority(1.0).weight(40.0).maxCooldown(20),
            CombatCommon.animations(
               AnimsWom.SHADOW_OBSIDIAN_SWORD_GESETZ_AUTO_2,
               AnimsPugilistSteve.SHADOW_OBSIDIAN_SWORD_DUAL_SWORD_AUTO4,
               AnimsPugilistSteve.SHADOW_OBSIDIAN_SWORD_DUAL_SWORD_AUTO5,
               AnimsEpicFightDualGreatsword.GREATSWORD_DUAL_AUTO_2,
               AnimsEpicFightDualGreatsword.SHADOW_OBSIDIAN_SWORD_GREATSWORD_DUAL_AUTO_3
            ),
            CombatCommon.animations(
               WOMAnimations.TORMENT_DASH,
               AnimsWom.SHADOW_OBSIDIAN_SWORD_GESETZ_AUTO_3,
               AnimsEpicFightDualGreatsword.SHADOW_OBSIDIAN_SWORD_GREATSWORD_DUAL_AIRSLASH
            ),
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
