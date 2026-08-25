package com.dmc.invincible_dmc.damagesource;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public interface DMCDamageTypeTags {
   TagKey<DamageType> NOT_CHARGE = create("not_charge");
   TagKey<DamageType> NO_KNOCKBACK = create("no_knockback");

   private static TagKey<DamageType> create(String tagName) {
      return TagKey.m_203882_(Registries.f_268580_, ResourceLocation.fromNamespaceAndPath("invincible_dmc", tagName));
   }
}
