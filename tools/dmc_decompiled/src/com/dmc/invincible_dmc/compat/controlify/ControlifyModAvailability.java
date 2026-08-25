package com.dmc.invincible_dmc.compat.controlify;

public final class ControlifyModAvailability {
   private static boolean isModInstalled;

   private ControlifyModAvailability() {
   }

   public static boolean isModInstalled() {
      return isModInstalled;
   }

   public static void setIsModInstalled(boolean isModInstalled) {
      ControlifyModAvailability.isModInstalled = isModInstalled;
   }
}
