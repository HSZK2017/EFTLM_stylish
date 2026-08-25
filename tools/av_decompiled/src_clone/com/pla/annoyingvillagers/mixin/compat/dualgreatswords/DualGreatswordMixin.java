package com.pla.annoyingvillagers.mixin.compat.dualgreatswords;

import M6FGR.dualgreatswords.gameassets.DualGreatSwordsAnimations;
import M6FGR.dualgreatswords.gameassets.DualGreatSwordsSkills;
import M6FGR.dualgreatswords.skill.weaponinnate.EarthQuakeSkill;
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
import yesman.epicfight.world.damagesource.StunType;

@Mixin(
   value = {DualGreatSwordsSkills.class},
   remap = false
)
public abstract class DualGreatswordMixin {
   @Inject(
      method = {"buildSkillEvent"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void buildSkillEvent(SkillBuildEvent build, CallbackInfo ci) {
      ModRegistryWorker modRegistry = build.createRegistryWorker("dualgreatswords");
      WeaponInnateSkill EarthQuake = (WeaponInnateSkill)modRegistry.build(
         "earthquake",
         EarthQuakeSkill::new,
         SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(DualGreatSwordsAnimations.GREATSWORD_EARTH_QUAKE)
      );
      EarthQuake.newProperty()
         .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1.0F))
         .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(20.0F))
         .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.6F))
         .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
         .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      DualGreatSwordsSkills.EARTHQUAKE = EarthQuake;
      ci.cancel();
   }
}
