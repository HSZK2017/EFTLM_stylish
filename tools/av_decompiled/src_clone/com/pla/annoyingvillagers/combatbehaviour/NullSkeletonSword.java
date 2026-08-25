package com.pla.annoyingvillagers.combatbehaviour;

import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.Builder;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class NullSkeletonSword {
   public static final Builder<MobPatch<?>> AV_SWORD = CECombatBehaviors.builder()
      .newBehaviorRoot(CombatBehaviourTemplates.executionRoot(2.0))
      .newBehaviorRoot(
         CombatCommon.addRandomCombatChains(
            BehaviorRoot.builder().priority(1.0).weight(40.0).maxCooldown(20),
            CombatCommon.animations(Animations.SWORD_AUTO1, Animations.SWORD_AUTO2, Animations.SWORD_AUTO3),
            CombatCommon.animations(AnimsPugilistSteve.SWORD_HEAVY_AUTO_1, AnimsPugilistSteve.SWORD_HEAVY_AUTO_2, AnimsPugilistSteve.SWORD_HEAVY_AUTO_3),
            CombatCommon.animations(Animations.SWORD_DASH, Animations.SWORD_AIR_SLASH, Animations.SWEEPING_EDGE),
            CombatCommon.kickAnimations(),
            CombatCommon.rollStepAnimations()
         )
      )
      .newBehaviorRoot(CombatBehaviourTemplates.guardRoot())
      .newBehaviorRoot(CombatBehaviourTemplates.jumpRoot());
}
