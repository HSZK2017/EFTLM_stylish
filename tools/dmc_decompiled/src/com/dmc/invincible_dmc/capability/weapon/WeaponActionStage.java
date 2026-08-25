package com.dmc.invincible_dmc.capability.weapon;

public enum WeaponActionStage {
   STARTUP,
   TRANSITION,
   LOOP,
   RELEASE_REQUESTED,
   RELEASE,
   FINISH,
   COMPLETED,
   INTERRUPTED,
   CANCELLED;

   public boolean isTerminal() {
      return this == COMPLETED || this == INTERRUPTED || this == CANCELLED;
   }
}
