package com.pla.annoyingvillagers.config;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public final class AnnoyingVillagersSpawnConfig {
   private static final int WEIGHT_MIN = 0;
   private static final int WEIGHT_MAX = 1000;
   private static final int COUNT_MIN = 1;
   private static final int COUNT_MAX = 64;
   public static final List<AnnoyingVillagersSpawnConfig.Entry> ENTRIES = List.of(
      configurableGroupEntry("player_npc", 6, 2, 4, "Player NPC"),
      configurableGroupEntry("villager_scout", 4, 1, 4, "Villager Scout"),
      configurableGroupEntry("villager_scout_captain", 3, 1, 2, "Villager Scout Captain"),
      configurableGroupEntry("purple_villager_knight", 3, 1, 2, "Purple Villager Knight"),
      configurableGroupEntry("red_villager_knight", 3, 1, 2, "Red Villager Knight"),
      configurableGroupEntry("blue_villager_knight", 3, 1, 2, "Blue Villager Knight"),
      configurableGroupEntry("green_villager_knight", 3, 1, 2, "Green Villager Knight"),
      fixedGroupEntry("steve", 1, "Steve"),
      fixedGroupEntry("alex", 1, "Alex"),
      fixedGroupEntry("chris", 1, "Chris"),
      fixedGroupEntry("blue_demon", 1, "Blue Demon"),
      fixedGroupEntry("low_shadow_herobrine_clone", 1, "Netherite Herobrine"),
      fixedGroupEntry("herobrine_clone", 1, "Herobrine Clone"),
      fixedGroupEntry("shadow_herobrine_clone", 1, "Shadow Herobrine Clone"),
      fixedGroupEntry("transporter_herobrine_clone", 1, "Transporter Herobrine Clone"),
      fixedGroupEntry("armored_herobrine", 1, "Armored Herobrine"),
      fixedGroupEntry("herobrine_7", 1, "Herobrine 7"),
      fixedGroupEntry("herobrine_chris", 1, "Herobrine Chris"),
      fixedGroupEntry("herobrine_greg", 1, "Herobrine Greg")
   );
   public static final ForgeConfigSpec SPEC;
   private static final Map<String, AnnoyingVillagersSpawnConfig.Entry> entryByEntityId = new HashMap<>();
   private static final Map<String, IntValue> weightValueByEntityId = new HashMap<>();
   private static final Map<String, ConfigValue<List<? extends Number>>> tripleValueByEntityId = new HashMap<>();

   public static AnnoyingVillagersSpawnConfig.SpawnConfig getSpawnConfig(String entityId) {
      AnnoyingVillagersSpawnConfig.Entry entry = entryByEntityId.get(entityId);
      if (entry == null) {
         return new AnnoyingVillagersSpawnConfig.SpawnConfig(0, 1, 1);
      } else if (!entry.groupSizeConfigurable()) {
         IntValue weightValue = weightValueByEntityId.get(entityId);
         int weight = weightValue != null ? (Integer)weightValue.get() : entry.defaultConfig().weight();
         return new AnnoyingVillagersSpawnConfig.SpawnConfig(weight, 1, 1);
      } else {
         ConfigValue<List<? extends Number>> tripleValue = tripleValueByEntityId.get(entityId);
         return tripleValue == null ? entry.defaultConfig() : parseTripleOrDefault((List<? extends Number>)tripleValue.get(), entry.defaultConfig());
      }
   }

   private static AnnoyingVillagersSpawnConfig.SpawnConfig parseTripleOrDefault(
      List<? extends Number> rawValues, AnnoyingVillagersSpawnConfig.SpawnConfig defaultConfig
   ) {
      if (rawValues != null && rawValues.size() == 3) {
         Integer parsedWeight = toExactInteger(rawValues.get(0));
         Integer parsedMinCount = toExactInteger(rawValues.get(1));
         Integer parsedMaxCount = toExactInteger(rawValues.get(2));
         if (parsedWeight == null || parsedMinCount == null || parsedMaxCount == null) {
            return defaultConfig;
         } else if (parsedWeight < 0 || parsedWeight > 1000) {
            return defaultConfig;
         } else if (parsedMinCount < 1 || parsedMinCount > 64) {
            return defaultConfig;
         } else if (parsedMaxCount < 1 || parsedMaxCount > 64) {
            return defaultConfig;
         } else {
            return parsedMaxCount < parsedMinCount ? defaultConfig : new AnnoyingVillagersSpawnConfig.SpawnConfig(parsedWeight, parsedMinCount, parsedMaxCount);
         }
      } else {
         return defaultConfig;
      }
   }

   private static Integer toExactInteger(Number number) {
      if (number == null) {
         return null;
      } else {
         double valueAsDouble = number.doubleValue();
         long roundedValue = Math.round(valueAsDouble);
         if (Math.abs(valueAsDouble - (double)roundedValue) > 1.0E-9) {
            return null;
         } else {
            return roundedValue >= -2147483648L && roundedValue <= 2147483647L ? (int)roundedValue : null;
         }
      }
   }

   private static List<? extends Number> toDefaultList(AnnoyingVillagersSpawnConfig.SpawnConfig defaultConfig) {
      return List.of(defaultConfig.weight(), defaultConfig.minCount(), defaultConfig.maxCount());
   }

   private static AnnoyingVillagersSpawnConfig.Entry fixedGroupEntry(String entityId, int defaultWeight, String name) {
      String configKey = "spawn_" + entityId;
      String comment = String.format(
         Locale.ROOT,
         "Spawn config for %s. Format: weight (int). Weight is added to the spawn pool in each biome. Group size is fixed at 1 and is NOT configurable",
         name
      );
      return new AnnoyingVillagersSpawnConfig.Entry(entityId, configKey, new AnnoyingVillagersSpawnConfig.SpawnConfig(defaultWeight, 1, 1), false, comment);
   }

   private static AnnoyingVillagersSpawnConfig.Entry configurableGroupEntry(
      String entityId, int defaultWeight, int defaultMinCount, int defaultMaxCount, String name
   ) {
      String configKey = "spawn_" + entityId;
      String comment = String.format(
         Locale.ROOT,
         "Spawn config for %s. Format: [weight, minCount, maxCount]. Weight is added to the spawn pool in each biome (higher = more common, 0 = disable). minCount/maxCount control group size",
         name
      );
      return new AnnoyingVillagersSpawnConfig.Entry(
         entityId, configKey, new AnnoyingVillagersSpawnConfig.SpawnConfig(defaultWeight, defaultMinCount, defaultMaxCount), true, comment
      );
   }

   static {
      Builder configBuilder = new Builder();
      configBuilder.push("spawning");

      for (AnnoyingVillagersSpawnConfig.Entry entry : ENTRIES) {
         entryByEntityId.put(entry.entityId(), entry);
         if (entry.groupSizeConfigurable()) {
            ConfigValue<List<? extends Number>> tripleValue = configBuilder.comment(entry.comment())
               .defineList(entry.configKey(), toDefaultList(entry.defaultConfig()), element -> element instanceof Number);
            tripleValueByEntityId.put(entry.entityId(), tripleValue);
         } else {
            IntValue weightValue = configBuilder.comment(entry.comment()).defineInRange(entry.configKey(), entry.defaultConfig().weight(), 0, 1000);
            weightValueByEntityId.put(entry.entityId(), weightValue);
         }
      }

      configBuilder.pop();
      SPEC = configBuilder.build();
   }

   public static record Entry(
      String entityId, String configKey, AnnoyingVillagersSpawnConfig.SpawnConfig defaultConfig, boolean groupSizeConfigurable, String comment
   ) {
   }

   public static record SpawnConfig(int weight, int minCount, int maxCount) {
   }
}
