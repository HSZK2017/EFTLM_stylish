package com.pla.annoyingvillagers.config;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class AnnoyingVillagersConfig {
   public static final Builder BUILDER = new Builder();
   public static final ForgeConfigSpec SPEC = BUILDER.build();
   public static ConfigValue<Double> HEROBRINE_POSSESS_RATE = BUILDER.comment("Chance for Herobrine possess another player npc into Low Herobrine Clone")
      .defineInRange("herobrinePossessRate", 0.5, 0.0, 1.0);
   public static ConfigValue<Integer> HEROBRINE_RECALL_MIN_TIME = BUILDER.comment(
         "The minimum value (in minutes) for Herobrine's random recall time. This value should be lower than or equal the maximum. After a random time between min and max, Herobrine will vanish and return to the Herobrine dimension."
      )
      .defineInRange("herobrineRecallMinTime", 60, 1, 10080);
   public static ConfigValue<Integer> HEROBRINE_RECALL_MAX_TIME = BUILDER.comment(
         "The maximum value (in minutes) for Herobrine's random recall time. This value should be greater than or equal to the minimum. After a random time between min and max, Herobrine will vanish and return to the Herobrine dimension."
      )
      .defineInRange("herobrineRecallMaxTime", 300, 1, 10080);
   public static ConfigValue<Double> ANGRY_STEVE_CHANCE = BUILDER.comment("Chance for Steve to be Angry after getting killed")
      .defineInRange("angrySteveChance", 0.2, 0.0, 1.0);
   public static ConfigValue<Integer> ANGRY_STEVE_LEAVE_MIN_TIME = BUILDER.comment(
         "The minimum value (in minutes) for Angry Steve's random leave time. This value should be lower than or equal the maximum. After a random time between min and max, Angry Steve will feel exhausted and leave the game."
      )
      .defineInRange("angrySteveLeaveMinTime", 60, 1, 10080);
   public static ConfigValue<Integer> ANGRY_STEVE_LEAVE_MAX_TIME = BUILDER.comment(
         "The maximum value (in minutes) for Angry Steve's random leave time. This value should be greater than or equal to the minimum. After a random time between min and max, Angry Steve will feel exhausted and leave the game."
      )
      .defineInRange("angrySteveLeaveMaxTime", 300, 1, 10080);
   public static ConfigValue<Integer> BLUE_DEMON_LEAVE_MIN_TIME = BUILDER.comment(
         "The minimum value (in minutes) for Blue Demon's random leave time. This value should be lower than or equal the maximum. After a random time between min and max, Blue Demon will feel bored and go away.\""
      )
      .defineInRange("blueDemonLeaveMinTime", 60, 1, 10080);
   public static ConfigValue<Integer> BLUE_DEMON_LEAVE_MAX_TIME = BUILDER.comment(
         "The maximum value (in minutes) for Blue Demon's random leave time. This value should be greater than or equal to the minimum. After a random time between min and max, Blue Demon will feel bored and go away."
      )
      .defineInRange("blueDemonLeaveMaxTime", 300, 1, 10080);
   public static ConfigValue<Boolean> TRIDENT_FESTIVAL_CAN_BREAK_BLOCK = BUILDER.comment("Make Trident Festival can break block")
      .define("tridentFestivalCanBreakBlock", true);
   public static ConfigValue<Double> MOB_GUARD_BREAK_WAKE_UP_MIN_CHANCE = BUILDER.comment(
         "[ONLY WORK WHEN EpicFight: KickSkill is installed] Min chance for mob can wake up automatically on guard break"
      )
      .defineInRange("mobGuardBreakWakeUpMinChance", 0.05, 0.0, 1.0);
   public static ConfigValue<Double> MOB_GUARD_BREAK_WAKE_UP_MAX_CHANCE = BUILDER.comment(
         "[ONLY WORK WHEN EpicFight: KickSkill is installed] Max chance for mob can wake up automatically on guard break"
      )
      .defineInRange("mobGuardBreakWakeUpMaxChance", 0.4, 0.0, 1.0);
   public static ConfigValue<Boolean> TURN_ON_NPC_CHAT = BUILDER.comment("Turn on all chatting for NPC").define("turnOnNpcChat", true);
   public static ConfigValue<Boolean> TURN_ON_NPC_VOICE = BUILDER.comment("Turn on all voice for NPC").define("turnOnNpcVoice", true);
   public static ConfigValue<Boolean> ARROW_CAN_BREAK_BLOCK = BUILDER.comment("Make arrow can break block").define("arrowCanBreakBlock", true);
   public static ConfigValue<Boolean> CAN_EXECUTE_AV_MOB = BUILDER.comment("Make all of AV NPCs and Mobs can be executed").define("canExecuteAvMob", true);
   public static ConfigValue<Boolean> AV_MOB_CAN_EXECUTE = BUILDER.comment("Enable execute ability for all of Av NPCs and Mobs")
      .define("AvMobCanExecute", true);
   public static ConfigValue<Boolean> AV_MOB_CAN_BURN_ITEM = BUILDER.comment("Enable burning items ability for all of Av NPCs and Mobs")
      .define("AvMobCanBurnItem", true);
   public static ConfigValue<Boolean> VANILLA_MOB_CAN_DRINK_HEALING_POTION = BUILDER.comment("Enable drinking healing potion ability for Zombie and Skeleton")
      .define("VanillaMobCanDrinkHealingPotion", true);
   public static ConfigValue<Integer> WEAPON_BREAKING_MECHANISM_VALUE = BUILDER.comment(
         "The value of durability lose of a weapon when Av Bosses blocking or clashing a dangerous animation or when Player clashing a dangerous animation by Av Bosses"
      )
      .defineInRange("weaponBreakingMechanismValue", 10, 0, 10000);
   public static ConfigValue<List<? extends String>> WEAPON_DISARMS_AFFECTED_ENTITY_TYPES = BUILDER.comment(
         "Living entity types whose held weapons can be disarmed"
      )
      .defineListAllowEmpty(
         "affectedEntityTypes",
         List.of(
            "minecraft:player",
            "minecraft:zombie",
            "minecraft:skeleton",
            "annoyingvillagers:player_npc",
            "annoyingvillagers:low_herobrine_clone",
            "annoyingvillagers:low_shadow_herobrine_clone",
            "annoyingvillagers:villager_scout",
            "annoyingvillagers:villager_scout_captain",
            "annoyingvillagers:blue_villager_knight",
            "annoyingvillagers:green_villager_knight",
            "annoyingvillagers:red_villager_knight",
            "annoyingvillagers:purple_villager_knight"
         ),
         AnnoyingVillagersConfig::validResourceOrTagOrNamespace
      );
   public static ConfigValue<List<? extends String>> WEAPON_DISARMS_BLACKLIST = BUILDER.comment("Weapons/items that cannot be disarmed")
      .defineListAllowEmpty(
         "weaponBlacklist",
         List.of(
            "minecraft:wooden_sword",
            "minecraft:wooden_shovel",
            "minecraft:wooden_pickaxe",
            "minecraft:wooden_axe",
            "minecraft:wooden_hoe",
            "minecraft:stone_sword",
            "minecraft:stone_shovel",
            "minecraft:stone_pickaxe",
            "minecraft:stone_axe",
            "minecraft:stone_hoe",
            "efn",
            "cataclysm",
            "wom:agony",
            "wom:tormented_mind",
            "wom:ruine",
            "wom:ender_blaster",
            "wom:antitheus",
            "wom:satsujin",
            "wom:herrscher",
            "wom:gesetz",
            "wom:moonless",
            "wom:solar",
            "wom:napoleon",
            "wom:evil_tachi",
            "wom:hollow_longsword",
            "wom:jabberwocky",
            "wom:nova",
            "wom:orbit",
            "wom:blackstar",
            "wom:nova",
            "wom:orbit",
            "wom:wooden_staff",
            "wom:stone_staff",
            "cdmoveset:yamato",
            "cdmoveset:s_wooden_spear",
            "cdmoveset:s_stone_spear",
            "cdmoveset:s_wooden_tachi",
            "cdmoveset:s_stone_tachi",
            "cdmoveset:s_wooden_sword",
            "cdmoveset:s_stone_sword",
            "cdmoveset:s_wooden_longsword",
            "cdmoveset:s_stone_longsword",
            "cdmoveset:s_wooden_greatsword",
            "cdmoveset:s_stone_greatsword",
            "epicfight:wooden_spear",
            "epicfight:stone_spear",
            "epicfight:wooden_tachi",
            "epicfight:stone_tachi",
            "epicfight:wooden_dagger",
            "epicfight:stone_dagger",
            "epicfight:wooden_greatsword",
            "epicfight:stone_greatsword",
            "annoyingvillagers:legendary_sword",
            "annoyingvillagers:demoniac_voltage_reaver",
            "annoyingvillagers:shadow_obsidian_weapon",
            "annoyingvillagers:obsidian_weapon",
            "annoyingvillagers:bedrock_weapon",
            "annoyingvillagers:crafting_table",
            "annoyingvillagers:wooden_door",
            "annoyingvillagers:ladder",
            "annoyingvillagers:trapdoor",
            "annoyingvillagers:null_weapon",
            "annoyingvillagers:shadow_obsidian_pillar",
            "annoyingvillagers:ender_glaive",
            "annoyingvillagers:ender_slayer_scythe",
            "annoyingvillagers:demoniac_voltage_reaver",
            "annoyingvillagers:obsidian_sledgehammer",
            "annoyingvillagers:shadow_obsidian_sword",
            "annoyingvillagers:warden_axe",
            "annoyingvillagers:blue_demon_trident",
            "annoyingvillagers:darkness_sword",
            "annoyingvillagers:red_steel_axe",
            "annoyingvillagers:holy_llama_hammer",
            "annoyingvillagers:woopie_the_sword",
            "annoyingvillagers:jessica_the_dark_shield"
         ),
         AnnoyingVillagersConfig::validResourceOrTagOrNamespace
      );
   public static ConfigValue<Double> NPC_TARGET_WEIGHT_MONSTER_HUNTER = BUILDER.comment("Weight for Player NPC to target and attack all monsters in the world")
      .defineInRange("npcTargetWeightMonsterHunter", 1.0, 0.0, 10.0);
   public static ConfigValue<Double> NPC_TARGET_WEIGHT_VILLAGER_HUNTER = BUILDER.comment(
         "Weight for Player NPC to target and attack all villagers in the world"
      )
      .defineInRange("npcTargetWeightVillagerHunter", 1.0, 0.0, 10.0);
   public static ConfigValue<Double> NPC_TARGET_WEIGHT_PLAYER_HUNTER = BUILDER.comment(
         "Weight for Player NPC to target and attack players and other Player NPCs"
      )
      .defineInRange("npcTargetWeightPlayerHunter", 1.0, 0.0, 10.0);
   public static ConfigValue<Double> NPC_TARGET_WEIGHT_HOSTILE_HUNTER = BUILDER.comment("Weight for Player NPC to target and attack everything in the world")
      .defineInRange("npcTargetWeightHostileHunter", 1.0, 0.0, 10.0);
   public static ConfigValue<Double> NPC_TARGET_WEIGHT_PASSIVE_HUNTER = BUILDER.comment("Weight for Player NPC to run away from everything in the world")
      .defineInRange("npcTargetWeightPassiveHunter", 1.0, 0.0, 10.0);
   public static ConfigValue<Double> NPC_TARGET_WEIGHT_ANIMAL_HUNTER = BUILDER.comment("Weight for Player NPC to target and attack all animals in the world")
      .defineInRange("npcTargetWeightAnimalHunter", 1.0, 0.0, 10.0);

   private static boolean validResourceOrTagOrNamespace(Object object) {
      if (object instanceof String value) {
         if (value.isBlank()) {
            return false;
         } else if (value.startsWith("#")) {
            return ResourceLocation.m_135820_(value.substring(1)) != null;
         } else if (value.endsWith(":*")) {
            String namespace = value.substring(0, value.length() - 2);
            return ResourceLocation.m_135843_(namespace);
         } else {
            return !value.contains(":") ? ResourceLocation.m_135843_(value) : ResourceLocation.m_135820_(value) != null;
         }
      } else {
         return false;
      }
   }

   static {
      BUILDER.comment("==== NPC Behaviour ====").push("npcBehaviour");
      BUILDER.pop();
   }
}
