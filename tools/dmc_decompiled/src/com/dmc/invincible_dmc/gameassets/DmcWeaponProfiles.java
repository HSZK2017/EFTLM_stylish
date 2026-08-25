package com.dmc.invincible_dmc.gameassets;

import com.dmc.invincible_dmc.api.weapon.DmcWeaponType;
import com.dmc.invincible_dmc.api.weapon.WeaponCombatProfile;
import java.util.EnumMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public final class DmcWeaponProfiles {
   private static final Map<DmcWeaponType, WeaponCombatProfile> PROFILES = new EnumMap<>(DmcWeaponType.class);

   private DmcWeaponProfiles() {
   }

   public static void register(WeaponCombatProfile profile) {
      WeaponCombatProfile previous = PROFILES.putIfAbsent(profile.type(), profile);
      if (previous != null && previous != profile) {
         throw new IllegalStateException("Duplicate DMC weapon profile: " + profile.type());
      }
   }

   @Nullable
   public static WeaponCombatProfile get(DmcWeaponType type) {
      return PROFILES.get(type);
   }
}
