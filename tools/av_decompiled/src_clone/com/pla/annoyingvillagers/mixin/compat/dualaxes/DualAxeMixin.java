package com.pla.annoyingvillagers.mixin.compat.dualaxes;

import M6FGR.dualaxes.gameassets.DualAxesAnimations;
import M6FGR.dualaxes.gameassets.DualAxesSkills;
import java.util.Set;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.api.forgeevent.SkillBuildEvent.ModRegistryWorker;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.skill.weaponinnate.SimpleWeaponInnateSkill;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;

@Mixin(
   value = {DualAxesSkills.class},
   remap = false
)
public abstract class DualAxeMixin {
   @Inject(
      method = {"buildSkillEvent"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void buildSkillEvent(SkillBuildEvent build, CallbackInfo ci) {
      ModRegistryWorker modRegistry = build.createRegistryWorker("dualaxes");
      WeaponInnateSkill spinning_death = (WeaponInnateSkill)modRegistry.build(
         "spinning_death",
         SimpleWeaponInnateSkill::new,
         SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(DualAxesAnimations.AXE_SPINNING_DEATH)
      );
      spinning_death.newProperty()
         .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1.0F))
         .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
         .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(20.0F))
         .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.6F))
         .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      DualAxesSkills.SPINNING_DEATH = spinning_death;
      ci.cancel();
   }
}
