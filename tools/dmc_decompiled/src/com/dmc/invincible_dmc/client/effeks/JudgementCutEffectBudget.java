package com.dmc.invincible_dmc.client.effeks;

public final class JudgementCutEffectBudget {
   private static final long BURST_TIMEOUT_NANOS = 8000000000L;
   private static long burstExpiresAtNanos;
   private static int activeBursts;

   private JudgementCutEffectBudget() {
   }

   public static void markBurstStarted() {
      activeBursts++;
      refreshBurst();
   }

   public static void refreshBurst() {
      burstExpiresAtNanos = System.nanoTime() + 8000000000L;
   }

   public static void markBurstEnded() {
      if (activeBursts > 0) {
         activeBursts--;
      }

      if (activeBursts == 0) {
         burstExpiresAtNanos = 0L;
      }
   }

   public static boolean isBurstActive() {
      if (activeBursts > 0 && System.nanoTime() >= burstExpiresAtNanos) {
         activeBursts = 0;
         burstExpiresAtNanos = 0L;
      }

      return activeBursts > 0;
   }
}
