package com.pla.annoyingvillagers.init;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;

public class AnnoyingVillagersModDamageTypes {
   public static final ResourceKey<DamageType> IMPACT_EXPLOSION = ResourceKey.m_135785_(
      Registries.f_268580_, ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "impact_explosion")
   );

   public static class Sources {
      private static Reference<DamageType> getHolder(RegistryAccess access, ResourceKey<DamageType> key) {
         return access.m_175515_(Registries.f_268580_).m_246971_(key);
      }

      public static DamageSource impactExplosion(RegistryAccess access, Entity directEntity) {
         return new DamageSource(getHolder(access, AnnoyingVillagersModDamageTypes.IMPACT_EXPLOSION), directEntity, null);
      }
   }
}
