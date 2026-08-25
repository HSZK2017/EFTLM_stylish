package com.dmc.invincible_dmc.api.skill;

public enum JudgementCutChargePhase {
   IDLE(0, false),
   ARMED(1, false),
   CHARGING(2, true),
   READY(3, true),
   SUSPENDED_READY(4, true);

   private final int networkId;
   private final boolean charging;

   private JudgementCutChargePhase(int networkId, boolean charging) {
      this.networkId = networkId;
      this.charging = charging;
   }

   public int networkId() {
      return this.networkId;
   }

   public boolean isCharging() {
      return this.charging;
   }

   public static JudgementCutChargePhase byNetworkId(int networkId) {
      for (JudgementCutChargePhase phase : values()) {
         if (phase.networkId == networkId) {
            return phase;
         }
      }

      return IDLE;
   }
}
