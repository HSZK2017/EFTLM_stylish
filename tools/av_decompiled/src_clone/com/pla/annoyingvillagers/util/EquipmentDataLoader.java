package com.pla.annoyingvillagers.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;

public class EquipmentDataLoader extends SimpleJsonResourceReloadListener {
   private static final Gson GSON = new Gson();
   private static final Random RANDOM = new Random();
   private static final Map<String, List<String>> EQUIP_ITEMS = new HashMap<>();
   private static final Logger LOGGER = LogManager.getLogger();
   private static final float MIN_ARMOR_SET_MATCH_CHANCE = 0.3F;
   private static final float MAX_ARMOR_SET_MATCH_CHANCE = 0.5F;
   private static final String ANNOYING_VILLAGERS = "annoyingvillagers";
   private static final String EPIC_FIGHT = "epicfight";
   private static final String MINECRAFT = "minecraft";
   private static final String WOM = "wom";
   private static final String SHIELD_ITEM_ID = "minecraft:shield";
   private static final List<String> EQUIPMENT_SLOTS = List.of("MAINHAND", "OFFHAND", "HEAD", "CHEST", "LEGS", "FEET");
   private static final Map<String, List<String>> ARMOR_SLOT_SUFFIXES = Map.of(
      "HEAD", List.of("helmet"), "CHEST", List.of("chestplate"), "LEGS", List.of("leggings", "legging"), "FEET", List.of("boots", "boot")
   );
   private static final List<String> LONGSWORD_OFFHAND_POOL = List.of(
      avItem("diamond_longsword"),
      avItem("golden_longsword"),
      avItem("iron_longsword"),
      epicFightItem("stone_longsword"),
      epicFightItem("iron_longsword"),
      epicFightItem("golden_longsword"),
      epicFightItem("diamond_longsword"),
      epicFightItem("netherite_longsword")
   );
   private static final List<String> FALCHION_OFFHAND_POOL = List.of(
      avItem("diamond_falchion"),
      avItem("diamond_great_falchion"),
      avItem("netherite_falchion"),
      epicFightItem("wooden_tachi"),
      epicFightItem("stone_tachi"),
      epicFightItem("iron_tachi"),
      epicFightItem("golden_tachi"),
      epicFightItem("diamond_tachi"),
      epicFightItem("netherite_tachi"),
      epicFightItem("wooden_falchion"),
      epicFightItem("stone_falchion"),
      epicFightItem("iron_falchion"),
      epicFightItem("golden_falchion"),
      epicFightItem("diamond_falchion"),
      epicFightItem("netherite_falchion")
   );
   private static final List<String> SWORD_OFFHAND_POOL = List.of(
      avItem("jade_sword"),
      avItem("red_diamond_sword"),
      minecraftItem("golden_sword"),
      minecraftItem("stone_sword"),
      minecraftItem("diamond_sword"),
      minecraftItem("netherite_sword"),
      minecraftItem("iron_sword")
   );
   private static final List<String> DAGGER_OFFHAND_POOL = List.of(
      avItem("knife"),
      avItem("netherite_knife"),
      avItem("diamond_knife"),
      epicFightItem("iron_dagger"),
      epicFightItem("stone_dagger"),
      epicFightItem("golden_dagger"),
      epicFightItem("diamond_dagger"),
      epicFightItem("netherite_dagger")
   );
   private static final List<String> DUAL_AXE_OFFHAND_POOL = List.of(
      minecraftItem("wooden_axe"), minecraftItem("stone_axe"), minecraftItem("golden_axe"), minecraftItem("iron_axe"), minecraftItem("diamond_axe")
   );
   private static final List<String> DUAL_GREATSWORD_OFFHAND_POOL = List.of(
      epicFightItem("wooden_greatsword"),
      epicFightItem("stone_greatsword"),
      epicFightItem("golden_greatsword"),
      epicFightItem("iron_greatsword"),
      epicFightItem("diamond_greatsword"),
      womItem("wooden_greataxe"),
      womItem("iron_greataxe"),
      womItem("stone_greataxe"),
      womItem("golden_greataxe"),
      womItem("diamond_greataxe")
   );
   private static final Map<String, List<String>> BOUND_OFFHAND_WEAPONS = Map.ofEntries(
      Map.entry(avItem("exterminator_battleaxe"), List.of(avItem("exterminator_battleaxe"), avItem("exterminator_battleaxe_green"))),
      Map.entry(avItem("exterminator_battleaxe_green"), List.of(avItem("exterminator_battleaxe"), avItem("exterminator_battleaxe_green"))),
      Map.entry(avItem("diamond_mace"), List.of(avItem("diamond_mace"), avItem("golden_mace"))),
      Map.entry(avItem("golden_mace"), List.of(avItem("diamond_mace"), avItem("golden_mace"))),
      Map.entry(avItem("diamond_armblade"), List.of(avItem("diamond_armblade"))),
      Map.entry(avItem("diamond_moon_blade"), List.of(avItem("diamond_moon_blade"), avItem("golden_moon_blade"))),
      Map.entry(avItem("golden_moon_blade"), List.of(avItem("diamond_moon_blade"), avItem("golden_moon_blade"))),
      Map.entry(avItem("twin_diamond_spear"), List.of(avItem("twin_diamond_spear"))),
      Map.entry(avItem("diamond_longsword"), LONGSWORD_OFFHAND_POOL),
      Map.entry(avItem("golden_longsword"), LONGSWORD_OFFHAND_POOL),
      Map.entry(avItem("iron_longsword"), LONGSWORD_OFFHAND_POOL),
      Map.entry(avItem("diamond_falchion"), FALCHION_OFFHAND_POOL),
      Map.entry(avItem("diamond_great_falchion"), FALCHION_OFFHAND_POOL),
      Map.entry(avItem("netherite_falchion"), FALCHION_OFFHAND_POOL)
   );
   private static final Set<String> RANDOM_OFFHAND_WEAPON_BLACKLIST = Set.of(
      avItem("diamond_moon_blade"),
      avItem("golden_moon_blade"),
      avItem("diamond_armblade"),
      avItem("diamond_claw"),
      avItem("iron_cleaver"),
      avItem("diamond_sabre"),
      avItem("netherite_sabre"),
      avItem("blackscratcher"),
      avItem("diamond_warblade"),
      avItem("diamond_laevateinn")
   );

   public EquipmentDataLoader() {
      super(GSON, "mobs_equipment");
   }

   protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller profiler) {
      EQUIP_ITEMS.clear();

      for (Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
         ResourceLocation fileId = entry.getKey();
         JsonObject root = GsonHelper.m_13918_(entry.getValue(), "equipment");
         String modId = fileId.m_135815_().replace(".json", "");
         if (ModList.get().isLoaded(modId)) {
            for (String slot : EQUIPMENT_SLOTS) {
               if (root.has(slot)) {
                  JsonArray array = root.getAsJsonArray(slot);
                  List<String> items = EQUIP_ITEMS.computeIfAbsent(slot, k -> new ArrayList<>());

                  for (JsonElement el : array) {
                     String itemName = el.getAsString();
                     items.add(modId + ":" + itemName);
                  }
               }
            }
         }
      }
   }

   private static boolean addMoreDualCap(ItemStack stack, WeaponCapability weaponCapability) {
      return isAnnoyingVillagersItem(stack)
         ? false
         : ModList.get().isLoaded("dualaxes") && weaponCapability.getWeaponCategory() == WeaponCategories.AXE
            || ModList.get().isLoaded("dualgreatswords") && weaponCapability.getWeaponCategory() == WeaponCategories.GREATSWORD;
   }

   private static boolean addMoreShieldCap(WeaponCapability weaponCapability) {
      return false;
   }

   private static String avItem(String path) {
      return "annoyingvillagers:" + path;
   }

   private static String epicFightItem(String path) {
      return "epicfight:" + path;
   }

   private static String minecraftItem(String path) {
      return "minecraft:" + path;
   }

   private static String womItem(String path) {
      return "wom:" + path;
   }

   private static String getItemId(ItemStack stack) {
      ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.m_41720_());
      return key == null ? "" : key.toString();
   }

   private static boolean isAnnoyingVillagersItem(ItemStack stack) {
      ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.m_41720_());
      return key != null && "annoyingvillagers".equals(key.m_135827_());
   }

   private static boolean itemExists(String itemId) {
      String[] parts = itemId.split(":", 2);
      return parts.length != 2 ? false : ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(parts[0], parts[1])) != null;
   }

   private static Optional<String> getRandomExistingItem(List<String> itemIds) {
      List<String> validItems = itemIds.stream().filter(EquipmentDataLoader::itemExists).toList();
      return validItems.isEmpty() ? Optional.empty() : Optional.of(validItems.get(RANDOM.nextInt(validItems.size())));
   }

   private static boolean isArmorSlot(String slot) {
      return ARMOR_SLOT_SUFFIXES.containsKey(slot);
   }

   private static boolean shouldMatchPreviousArmorSet() {
      float chance = 0.3F + RANDOM.nextFloat() * 0.19999999F;
      return RANDOM.nextFloat() < chance;
   }

   private static Optional<String> getArmorSetPrefix(String itemId) {
      String[] parts = itemId.split(":", 2);
      if (parts.length != 2) {
         return Optional.empty();
      } else {
         String path = parts[1];

         for (List<String> suffixes : ARMOR_SLOT_SUFFIXES.values()) {
            for (String suffix : suffixes) {
               String suffixPattern = "_" + suffix;
               if (path.endsWith(suffixPattern) && path.length() > suffixPattern.length()) {
                  return Optional.of(path.substring(0, path.length() - suffixPattern.length()));
               }
            }
         }

         return Optional.empty();
      }
   }

   private static Optional<String> getMatchingArmorItem(String slot, List<String> pool, String previousArmorItemId) {
      if (previousArmorItemId != null && shouldMatchPreviousArmorSet()) {
         String[] parts = previousArmorItemId.split(":", 2);
         if (parts.length != 2) {
            return Optional.empty();
         } else {
            Optional<String> prefix = getArmorSetPrefix(previousArmorItemId);
            if (prefix.isEmpty()) {
               return Optional.empty();
            } else {
               for (String suffix : ARMOR_SLOT_SUFFIXES.getOrDefault(slot, List.of())) {
                  String candidate = parts[0] + ":" + prefix.get() + "_" + suffix;
                  if (pool.contains(candidate) && itemExists(candidate)) {
                     return Optional.of(candidate);
                  }
               }

               return Optional.empty();
            }
         }
      } else {
         return Optional.empty();
      }
   }

   private static Optional<String> getArmorItemForSlot(String slot, List<String> pool, String previousArmorItemId) {
      return getMatchingArmorItem(slot, pool, previousArmorItemId).or(() -> getRandomExistingItem(pool));
   }

   private static Optional<String> getBoundOffhandWeapon(ItemStack mainHandStack) {
      List<String> offhandItems = BOUND_OFFHAND_WEAPONS.get(getItemId(mainHandStack));
      return offhandItems != null && !offhandItems.isEmpty() ? getRandomExistingItem(offhandItems) : Optional.empty();
   }

   private static boolean isRandomOffhandWeaponBlacklisted(ItemStack stack) {
      String itemId = getItemId(stack);
      return RANDOM_OFFHAND_WEAPON_BLACKLIST.contains(itemId) || BOUND_OFFHAND_WEAPONS.containsKey(itemId);
   }

   private static boolean isAnnoyingVillagersSpear(ItemStack stack, WeaponCapability weaponCapability) {
      if (!isAnnoyingVillagersItem(stack)) {
         return false;
      } else {
         ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.m_41720_());
         return key.m_135815_().contains("spear") || weaponCapability.getWeaponCategory() == WeaponCategories.SPEAR;
      }
   }

   public static boolean canUseShield(ItemStack stack) {
      if (EpicFightCapabilities.getItemStackCapability(stack) instanceof WeaponCapability weaponCapability) {
         return isAnnoyingVillagersSpear(stack, weaponCapability)
            ? false
            : weaponCapability.getWeaponCategory() == WeaponCategories.SWORD
               || weaponCapability.getWeaponCategory() == WeaponCategories.LONGSWORD
               || weaponCapability.getWeaponCategory() == WeaponCategories.SPEAR
               || addMoreShieldCap(weaponCapability);
      } else {
         return false;
      }
   }

   public static boolean canTwoHand(ItemStack stack) {
      if (isRandomOffhandWeaponBlacklisted(stack)) {
         return false;
      } else {
         return !(EpicFightCapabilities.getItemStackCapability(stack) instanceof WeaponCapability weaponCapability)
            ? false
            : weaponCapability.getWeaponCategory() == WeaponCategories.SWORD
               || weaponCapability.getWeaponCategory() == WeaponCategories.FIST
               || weaponCapability.getWeaponCategory() == WeaponCategories.DAGGER
               || addMoreDualCap(stack, weaponCapability);
      }
   }

   private static Optional<String> getLegacyRandomOffhandWeapon(ItemStack mainHandStack) {
      if (isRandomOffhandWeaponBlacklisted(mainHandStack)) {
         return Optional.empty();
      } else {
         if (EpicFightCapabilities.getItemStackCapability(mainHandStack) instanceof WeaponCapability weaponCapability) {
            if (weaponCapability.getWeaponCategory() == WeaponCategories.SWORD) {
               return getRandomExistingItem(SWORD_OFFHAND_POOL);
            }

            if (weaponCapability.getWeaponCategory() == WeaponCategories.DAGGER) {
               return getRandomExistingItem(DAGGER_OFFHAND_POOL);
            }
         }

         return Optional.empty();
      }
   }

   private static String getRandomOffhandOrMainHand(String mainHandItemId, List<String> itemIds) {
      return RANDOM.nextBoolean() ? mainHandItemId : getRandomExistingItem(itemIds).orElse(mainHandItemId);
   }

   private static Optional<String> getDualModOffhandWeapon(String mainHandItemId, ItemStack mainHandStack) {
      if (!(EpicFightCapabilities.getItemStackCapability(mainHandStack) instanceof WeaponCapability weaponCapability) || isAnnoyingVillagersItem(mainHandStack)
         )
       {
         return Optional.empty();
      }

      if (ModList.get().isLoaded("dualaxes") && weaponCapability.getWeaponCategory() == WeaponCategories.AXE) {
         return Optional.of(getRandomOffhandOrMainHand(mainHandItemId, DUAL_AXE_OFFHAND_POOL));
      } else {
         return ModList.get().isLoaded("dualgreatswords") && weaponCapability.getWeaponCategory() == WeaponCategories.GREATSWORD
            ? Optional.of(getRandomOffhandOrMainHand(mainHandItemId, DUAL_GREATSWORD_OFFHAND_POOL))
            : Optional.empty();
      }
   }

   private static String getTwoHandOffhandWeapon(String mainHandItemId, ItemStack mainHandStack) {
      return getLegacyRandomOffhandWeapon(mainHandStack).or(() -> getDualModOffhandWeapon(mainHandItemId, mainHandStack)).orElse(mainHandItemId);
   }

   private static Optional<String> getGeneratedOffhandItem(String mainHandItemId, ItemStack mainHandStack) {
      Optional<String> boundOffhandWeapon = getBoundOffhandWeapon(mainHandStack);
      if (boundOffhandWeapon.isPresent()) {
         return boundOffhandWeapon;
      } else {
         if (RANDOM.nextBoolean()) {
            if (canTwoHand(mainHandStack)) {
               return Optional.of(getTwoHandOffhandWeapon(mainHandItemId, mainHandStack));
            }

            if (canUseShield(mainHandStack)) {
               return Optional.of("minecraft:shield");
            }
         } else {
            if (canUseShield(mainHandStack)) {
               return Optional.of("minecraft:shield");
            }

            if (canTwoHand(mainHandStack)) {
               return Optional.of(getTwoHandOffhandWeapon(mainHandItemId, mainHandStack));
            }
         }

         return Optional.empty();
      }
   }

   public static int getRandomDamage(ItemStack itemStack) {
      int maxDamage = itemStack.m_41776_();
      int min = maxDamage / 3;
      int max = maxDamage * 3 / 4;
      return RANDOM.nextInt(max - min + 1) + min;
   }

   public static List<String> getEquipCommands(float equipChanceArmor, Entity entity) {
      List<String> cmds = new ArrayList<>();
      String generatedOffhandItem = null;
      String previousArmorItemId = null;

      for (String slot : EQUIPMENT_SLOTS) {
         List<String> pool = EQUIP_ITEMS.getOrDefault(slot, List.of());
         if (!pool.isEmpty()) {
            boolean alwaysEquip = slot.equals("MAINHAND") || slot.equals("OFFHAND");
            if (alwaysEquip || !(RANDOM.nextFloat() > equipChanceArmor)) {
               String itemId;
               if (isArmorSlot(slot)) {
                  Optional<String> armorItem = getArmorItemForSlot(slot, pool, previousArmorItemId);
                  if (armorItem.isEmpty()) {
                     continue;
                  }

                  itemId = armorItem.get();
               } else if (slot.equals("OFFHAND") && generatedOffhandItem != null) {
                  if (!(RANDOM.nextFloat() < 0.25F)) {
                     continue;
                  }

                  itemId = generatedOffhandItem;
               } else {
                  itemId = pool.get(RANDOM.nextInt(pool.size()));
               }

               String[] parts = itemId.split(":", 2);
               String namespace = parts[0];
               String path = parts[1];
               Item item = (Item)ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(namespace, path));
               if (item != null) {
                  int damage = 0;
                  if (item.m_41465_()) {
                     damage = getRandomDamage(new ItemStack(item));
                  }

                  cmds.add(String.format("item replace entity @s %s with %s{Damage:%d}", mapSlot(slot), itemId, damage));
                  ItemStack itemStack = new ItemStack(item);
                  if (slot.equals("MAINHAND")) {
                     generatedOffhandItem = getGeneratedOffhandItem(itemId, itemStack).orElse(null);
                  }

                  if (isArmorSlot(slot)) {
                     previousArmorItemId = itemId;
                  }
               }
            }
         }
      }

      return cmds;
   }

   public static Optional<String> getRandomSpecificSlot(String slot) {
      List<String> pool = EQUIP_ITEMS.getOrDefault(slot, List.of());
      if (pool.isEmpty()) {
         return Optional.empty();
      } else {
         String itemId = pool.get(RANDOM.nextInt(pool.size()));
         String[] parts = itemId.split(":", 2);
         String namespace = parts[0];
         String path = parts[1];
         Item item = (Item)ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(namespace, path));
         if (item == null) {
            return Optional.empty();
         } else {
            int damage = 0;
            if (item.m_41465_()) {
               int maxDamage = new ItemStack(item).m_41776_();
               int min = maxDamage / 3;
               int max = maxDamage * 3 / 4;
               damage = RANDOM.nextInt(max - min + 1) + min;
            }

            String command = String.format("item replace entity @s %s with %s{Damage:%d}", mapSlot(slot), itemId, damage);
            return Optional.of(command);
         }
      }
   }

   private static String mapSlot(String slot) {
      return switch (slot) {
         case "MAINHAND" -> "weapon.mainhand";
         case "OFFHAND" -> "weapon.offhand";
         case "HEAD" -> "armor.head";
         case "CHEST" -> "armor.chest";
         case "LEGS" -> "armor.legs";
         case "FEET" -> "armor.feet";
         default -> slot.toLowerCase();
      };
   }
}
