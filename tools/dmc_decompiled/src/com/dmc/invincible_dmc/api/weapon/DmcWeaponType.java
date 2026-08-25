package com.dmc.invincible_dmc.api.weapon;

import java.util.Locale;

public enum DmcWeaponType {
   YAMATO(0, "yamato");

   private final int networkId;
   private final String serializedName;

   private DmcWeaponType(int networkId, String serializedName) {
      this.networkId = networkId;
      this.serializedName = serializedName;
   }

   public int networkId() {
      return this.networkId;
   }

   public String serializedName() {
      return this.serializedName;
   }

   public DmcWeaponType next() {
      return YAMATO;
   }

   public static DmcWeaponType byNetworkId(int networkId) {
      for (DmcWeaponType type : values()) {
         if (type.networkId == networkId) {
            return type;
         }
      }

      return YAMATO;
   }

   public static DmcWeaponType bySerializedName(String name) {
      if (name != null && !name.isBlank()) {
         String normalized = name.toLowerCase(Locale.ROOT);

         for (DmcWeaponType type : values()) {
            if (type.serializedName.equals(normalized)) {
               return type;
            }
         }

         return YAMATO;
      } else {
         return YAMATO;
      }
   }
}
