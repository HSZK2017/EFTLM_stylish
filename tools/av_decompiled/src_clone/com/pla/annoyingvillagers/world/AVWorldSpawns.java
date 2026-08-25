package com.pla.annoyingvillagers.world;

import com.pla.annoyingvillagers.config.AnnoyingVillagersSpawnConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class AVWorldSpawns {
   private static final Logger LOGGER = LogManager.getLogger();

   private AVWorldSpawns() {
   }

   public static void addBiomeSpawns(Builder builder) {
      for (AnnoyingVillagersSpawnConfig.Entry entry : AnnoyingVillagersSpawnConfig.ENTRIES) {
         AnnoyingVillagersSpawnConfig.SpawnConfig spawnConfig = AnnoyingVillagersSpawnConfig.getSpawnConfig(entry.entityId());
         addSpawn(builder, ResourceLocation.fromNamespaceAndPath("annoyingvillagers", entry.entityId()), spawnConfig);
      }
   }

   private static void addSpawn(Builder builder, ResourceLocation entityId, AnnoyingVillagersSpawnConfig.SpawnConfig spawnConfig) {
      if (spawnConfig.weight() > 0) {
         EntityType<?> rawType = (EntityType<?>)ForgeRegistries.ENTITY_TYPES.getValue(entityId);
         if (rawType == null) {
            LOGGER.warn("Spawn config refers to missing entity type: {}", entityId);
         } else {
            builder.getMobSpawnSettings()
               .getSpawner(rawType.m_20674_())
               .add(new SpawnerData(rawType, spawnConfig.weight(), spawnConfig.minCount(), spawnConfig.maxCount()));
         }
      }
   }
}
