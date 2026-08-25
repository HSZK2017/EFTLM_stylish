package com.dmc.invincible_dmc.compat.combat_evolution;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public final class CombatEvolutionDamageTypeTags {
   public static final TagKey<DamageType> EXECUTION = create("execution");
   public static final TagKey<DamageType> EXECUTION_FINISHED = create("execution_finished");

   private CombatEvolutionDamageTypeTags() {
   }

   private static TagKey<DamageType> create(String path) {
      return TagKey.m_203882_(Registries.f_268580_, ResourceLocation.fromNamespaceAndPath("combat_evolution", path));
   }
}
