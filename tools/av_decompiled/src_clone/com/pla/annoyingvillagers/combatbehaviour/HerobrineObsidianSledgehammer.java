package com.pla.annoyingvillagers.combatbehaviour;

import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import com.pla.annoyingvillagers.gameasset.AnimsWom;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.Behavior;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.Builder;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.gameasset.animations.weapons.AnimsAgony;
import reascer.wom.gameasset.animations.weapons.AnimsEnderblaster;
import reascer.wom.gameasset.animations.weapons.AnimsRuine;
import reascer.wom.gameasset.animations.weapons.AnimsSolar;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class HerobrineObsidianSledgehammer {
   public static final Builder<MobPatch<?>> OBSIDIAN_SLEDGEHAMMER = CECombatBehaviors.builder()
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
                  .animationBehavior(WOMAnimations.ENDERSTEP_BACKWARD, 0.0F)
                  .addExBehavior(HerobrineCommon::performEscapeRunAwayWithLowClone)
            )
            .addFirstBehavior(Behavior.builder().custom(CombatCommon::canEscape).withinDistance(0.0, 48.0).guard(40))
      )
      .newBehaviorRoot(CombatBehaviourTemplates.herobrineHealingRoot())
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(30.0)
            .maxCooldown(120)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::canPerformNormalAttackLogic)
                  .withinDistance(7.0, 48.0)
                  .animationBehavior(AnimsWom.HEROBRINE_MOB_ENDERSTEP_OBSCURIS, 0.0F)
                  .addExBehavior(HerobrineCommon::giveSlowFalling)
            )
      )
      .newBehaviorRoot(
         CombatCommon.addRandomCombatChains(
            BehaviorRoot.builder().priority(1.0).weight(40.0).maxCooldown(20),
            CombatCommon.animations(
               AnimsWom.DEMONIAC_RUINE_AUTO_1, AnimsWom.DEMONIAC_RUINE_AUTO_2, WOMAnimations.TORMENT_AUTO_4, AnimsSolar.SOLAR_AUTO_4, AnimsSolar.SOLAR_AUTO_2
            ),
            CombatCommon.animations(AnimsEnderblaster.ENDERBLASTER_TWOHAND_TISHNAW),
            CombatCommon.animations(
               WOMAnimations.TORMENT_BERSERK_DASH,
               WOMAnimations.TORMENT_AIRSLAM,
               WOMAnimations.TORMENT_AUTO_2,
               WOMAnimations.TORMENT_CHARGED_ATTACK_3,
               AnimsPugilistSteve.LEGENDARY_SWORD_AUTO_4,
               AnimsRuine.RUINE_CHATIMENT,
               AnimsWom.DEMONIAC_RUINE_AUTO_4,
               WOMAnimations.TORMENT_AUTO_1,
               WOMAnimations.TORMENT_AUTO_3,
               WOMAnimations.TORMENT_BERSERK_AUTO_1,
               WOMAnimations.TORMENT_BERSERK_AUTO_2,
               WOMAnimations.TORMENT_BERSERK_AIRSLAM,
               AnimsSolar.SOLAR_OBSCURIDAD_AUTO_4,
               AnimsAgony.AGONY_RIPPING_FANGS,
               AnimsAgony.AGONY_AUTO_3,
               Animations.GREATSWORD_AUTO1,
               Animations.GREATSWORD_AUTO2,
               WOMAnimations.TORMENT_CHARGED_ATTACK_1,
               Animations.THE_GUILLOTINE,
               AnimsWom.DEMONIAC_TORMENT_CHARGED_ATTACK_2
            ),
            CombatCommon.enderStepRollAnimations()
         )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(30.0)
            .maxCooldown(40)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::canPerformNormalAttackLogic)
                  .withinDistance(0.0, 5.0)
                  .animationBehavior(WOMAnimations.TORMENT_BERSERK_DASH, 0.0F)
                  .addNextBehavior(
                     Behavior.builder()
                        .custom(CombatCommon::canPerformNormalAttackLogic)
                        .withinDistance(0.0, 5.0)
                        .animationBehavior(WOMAnimations.TORMENT_BERSERK_DASH, 0.0F)
                        .addNextBehavior(
                           Behavior.builder()
                              .custom(CombatCommon::canPerformNormalAttackLogic)
                              .withinDistance(0.0, 5.0)
                              .animationBehavior(WOMAnimations.TORMENT_BERSERK_DASH, 0.0F)
                              .addNextBehavior(
                                 Behavior.builder()
                                    .custom(CombatCommon::canPerformNormalAttackLogic)
                                    .withinDistance(0.0, 5.0)
                                    .animationBehavior(WOMAnimations.TORMENT_BERSERK_DASH, 0.0F)
                              )
                        )
                  )
            )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(10.0)
            .maxCooldown(600)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::canPerformNormalAttackLogic)
                  .custom(HerobrineCommon::canChangeToSecondForm)
                  .withinDistance(0.0, 32.0)
                  .animationBehavior(AnimsPugilistSteve.POSE_UP, 0.0F)
                  .addExBehavior(HerobrineCommon::changeToSecondForm)
            )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(25.0)
            .maxCooldown(300)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::canPerformNormalAttackLogic)
                  .withinDistance(5.0, 12.0)
                  .custom(HerobrineCommon::canPlaySecondFormAnimation)
                  .animationBehavior(AnimsWom.SLEDGEHAMMER_TORMENT_BERSERK_AUTO_1, 0.0F)
                  .addExBehavior(HerobrineCommon::playSecondFormAnimation)
                  .addNextBehavior(
                     Behavior.builder()
                        .custom(CombatCommon::canPerformNormalAttackLogic)
                        .withinDistance(5.0, 12.0)
                        .animationBehavior(AnimsWom.SLEDGEHAMMER_TORMENT_BERSERK_AUTO_2, 0.0F)
                        .addNextBehavior(
                           Behavior.builder()
                              .custom(CombatCommon::canPerformNormalAttackLogic)
                              .withinDistance(5.0, 12.0)
                              .animationBehavior(AnimsWom.SLEDGEHAMMER_TORMENT_BERSERK_AUTO_1, 0.0F)
                              .addNextBehavior(
                                 Behavior.builder()
                                    .custom(CombatCommon::canPerformNormalAttackLogic)
                                    .withinDistance(5.0, 12.0)
                                    .animationBehavior(AnimsWom.SLEDGEHAMMER_TORMENT_BERSERK_AUTO_2, 0.0F)
                                    .addNextBehavior(
                                       Behavior.builder()
                                          .custom(CombatCommon::canPerformNormalAttackLogic)
                                          .withinDistance(5.0, 12.0)
                                          .animationBehavior(AnimsWom.SLEDGEHAMMER_TORMENT_BERSERK_AUTO_1, 0.0F)
                                          .addNextBehavior(
                                             Behavior.builder()
                                                .custom(CombatCommon::canPerformNormalAttackLogic)
                                                .withinDistance(5.0, 12.0)
                                                .animationBehavior(AnimsWom.SLEDGEHAMMER_TORMENT_BERSERK_AUTO_2, 0.0F)
                                                .addNextBehavior(
                                                   Behavior.builder()
                                                      .custom(CombatCommon::canPerformNormalAttackLogic)
                                                      .withinDistance(5.0, 12.0)
                                                      .animationBehavior(AnimsWom.SLEDGEHAMMER_TORMENT_BERSERK_AUTO_1, 0.0F)
                                                      .addNextBehavior(
                                                         Behavior.builder()
                                                            .custom(CombatCommon::canPerformNormalAttackLogic)
                                                            .withinDistance(5.0, 12.0)
                                                            .animationBehavior(AnimsWom.SLEDGEHAMMER_TORMENT_BERSERK_AUTO_2, 0.0F)
                                                            .addNextBehavior(
                                                               Behavior.builder()
                                                                  .custom(CombatCommon::canPerformNormalAttackLogic)
                                                                  .withinDistance(5.0, 12.0)
                                                                  .animationBehavior(AnimsWom.SLEDGEHAMMER_TORMENT_BERSERK_AUTO_1, 0.0F)
                                                                  .addNextBehavior(
                                                                     Behavior.builder()
                                                                        .custom(CombatCommon::canPerformNormalAttackLogic)
                                                                        .withinDistance(5.0, 12.0)
                                                                        .animationBehavior(AnimsWom.SLEDGEHAMMER_TORMENT_BERSERK_AUTO_2, 0.0F)
                                                                  )
                                                            )
                                                      )
                                                )
                                          )
                                    )
                              )
                        )
                  )
            )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(25.0)
            .maxCooldown(300)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::canPerformNormalAttackLogic)
                  .withinDistance(5.0, 12.0)
                  .custom(HerobrineCommon::canPlaySecondFormAnimation)
                  .animationBehavior(AnimsWom.SLEDGEHAMMER_SOLAR_AUTO_3, 0.0F)
                  .addExBehavior(HerobrineCommon::playSecondFormSpecialAnimation)
                  .addNextBehavior(
                     Behavior.builder()
                        .custom(CombatCommon::canPerformNormalAttackLogic)
                        .withinDistance(5.0, 12.0)
                        .animationBehavior(AnimsWom.SLEDGEHAMMER_TORMENT_BERSERK_AUTO_1, 0.0F)
                        .addNextBehavior(
                           Behavior.builder()
                              .custom(CombatCommon::canPerformNormalAttackLogic)
                              .withinDistance(5.0, 12.0)
                              .animationBehavior(AnimsWom.SLEDGEHAMMER_TORMENT_BERSERK_AUTO_2, 0.0F)
                              .addNextBehavior(
                                 Behavior.builder()
                                    .custom(CombatCommon::canPerformNormalAttackLogic)
                                    .withinDistance(5.0, 12.0)
                                    .animationBehavior(AnimsWom.SLEDGEHAMMER_SOLAR_AUTO_3, 0.0F)
                                    .addNextBehavior(
                                       Behavior.builder()
                                          .custom(CombatCommon::canPerformNormalAttackLogic)
                                          .withinDistance(5.0, 12.0)
                                          .animationBehavior(AnimsWom.SLEDGEHAMMER_TORMENT_BERSERK_AUTO_1, 0.0F)
                                          .addNextBehavior(
                                             Behavior.builder()
                                                .custom(CombatCommon::canPerformNormalAttackLogic)
                                                .withinDistance(5.0, 12.0)
                                                .animationBehavior(AnimsWom.SLEDGEHAMMER_TORMENT_BERSERK_AUTO_2, 0.0F)
                                          )
                                    )
                              )
                        )
                  )
            )
      )
      .newBehaviorRoot(CombatBehaviourTemplates.guardRoot(0.0, 5.0, HerobrineCommon::canPerformGuarding))
      .newBehaviorRoot(CombatBehaviourTemplates.herobrineJumpRoot());
}
