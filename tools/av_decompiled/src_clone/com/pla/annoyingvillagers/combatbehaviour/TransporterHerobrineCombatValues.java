package com.pla.annoyingvillagers.combatbehaviour;

public final class TransporterHerobrineCombatValues {
   public static final int TICKS_PER_SECOND = 20;
   public static final int ESCAPE_DURATION_TICKS = 70;
   public static final int ESCAPE_RETRY_COOLDOWN_TICKS = 80;
   public static final double LOW_HEALTH_ESCAPE_RATIO = 0.1;
   public static final double SUPPORT_AVOID_SEARCH_RADIUS = 32.0;
   public static final double SUPPORT_AVOID_TRIGGER_DISTANCE_SQR = 324.0;
   public static final double SUPPORT_AVOID_SAFE_DISTANCE_SQR = 196.0;
   public static final double SUPPORT_AVOID_MIN_DISTANCE = 12.0;
   public static final double SUPPORT_AVOID_MAX_DISTANCE = 20.0;
   public static final int SUPPORT_AVOID_REPATH_TICKS = 15;
   public static final double SUPPORT_AVOID_MOVE_SPEED = 1.15;

   private TransporterHerobrineCombatValues() {
   }
}
