package com.dmc.invincible_dmc.api.skill.crazycombo;

public enum CrazyComboPhase {
   IDLE(0, false, false),
   STARTUP(1, true, false),
   CHASE_REQUESTED(2, true, true),
   CHASE_ACTIVE(3, true, true),
   STOP_CONFIRMING(4, true, true),
   FINISH_ARMED(5, true, true),
   FINISH_REQUESTED(6, false, false);

   private final int networkId;
   private final boolean acceptingInput;
   private final boolean chase;

   private CrazyComboPhase(int networkId, boolean acceptingInput, boolean chase) {
      this.networkId = networkId;
      this.acceptingInput = acceptingInput;
      this.chase = chase;
   }

   public int networkId() {
      return this.networkId;
   }

   public boolean acceptsInput() {
      return this.acceptingInput;
   }

   public boolean isChase() {
      return this.chase;
   }

   public static CrazyComboPhase byNetworkId(int networkId) {
      for (CrazyComboPhase phase : values()) {
         if (phase.networkId == networkId) {
            return phase;
         }
      }

      return IDLE;
   }
}
