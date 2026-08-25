package org.eftlm.stylish.compat.wom;

import org.eftlm.stylish.compat.wom.WomSkillChecks;
import reascer.wom.gameasset.animations.weapons.AnimsMoonless;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors.Behavior;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors.BehaviorSeries;



public class Moonless {
    public static CombatBehaviors.Builder<HumanoidMobPatch<?>> Instance;

    public Moonless() {
    }

    static {
        if (WomSkillChecks.LoadedWOM()) {
            Instance = CombatBehaviors.<HumanoidMobPatch<?>>builder().newBehaviorSeries(BehaviorSeries.<HumanoidMobPatch<?>>builder().cooldown(20).weight(100.0F).canBeInterrupted(false).looping(false).nextBehavior(Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(AnimsMoonless.MOONLESS_AUTO_1).withinDistance(0.0, 5.0)).nextBehavior(Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(AnimsMoonless.MOONLESS_AUTO_2_VERSO).withinDistance(0.0, 5.0)).nextBehavior(Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(AnimsMoonless.MOONLESS_AUTO_3).withinDistance(0.0, 5.0)).nextBehavior(Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(AnimsMoonless.MOONLESS_AUTO_3).withinDistance(0.0, 5.0)))
            .newBehaviorSeries(BehaviorSeries.<HumanoidMobPatch<?>>builder().cooldown(20).weight(100.0F).canBeInterrupted(false).looping(false).nextBehavior(Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(AnimsMoonless.MOONLESS_AUTO_1_VERSO).withinDistance(0.0, 5.0)).nextBehavior(Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(AnimsMoonless.MOONLESS_BYPASS).withinDistance(0.0, 5.0)).nextBehavior(Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(AnimsMoonless.MOONLESS_AUTO_2).withinDistance(0.0, 5.0)).nextBehavior(Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(AnimsMoonless.MOONLESS_AUTO_1).withinDistance(0.0, 5.0)))
            .newBehaviorSeries(BehaviorSeries.<HumanoidMobPatch<?>>builder().cooldown(20).weight(100.0F).canBeInterrupted(false).looping(false).nextBehavior(Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(AnimsMoonless.MOONLESS_AUTO_2_VERSO).withinDistance(0.0, 5.0)).nextBehavior(Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(AnimsMoonless.MOONLESS_AUTO_1).withinDistance(0.0, 5.0)).nextBehavior(Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(AnimsMoonless.MOONLESS_AUTO_1_VERSO).withinDistance(0.0, 5.0)))
            .newBehaviorSeries(BehaviorSeries.<HumanoidMobPatch<?>>builder().cooldown(20).weight(100.0F).canBeInterrupted(false).looping(false).nextBehavior(Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(AnimsMoonless.MOONLESS_AUTO_2).withinDistance(0.0, 5.0)).nextBehavior(Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(AnimsMoonless.MOONLESS_AUTO_1).withinDistance(0.0, 5.0)).nextBehavior(Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(AnimsMoonless.MOONLESS_AUTO_3).withinDistance(0.0, 5.0)));
        }
    }
}
