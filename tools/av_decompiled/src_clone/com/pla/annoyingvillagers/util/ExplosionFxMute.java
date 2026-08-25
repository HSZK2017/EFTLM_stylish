package com.pla.annoyingvillagers.util;

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;

public final class ExplosionFxMute {
   private static final Long2LongOpenHashMap muteUntilTick = new Long2LongOpenHashMap();

   public static void mark(long key, long untilTick) {
      muteUntilTick.put(key, untilTick);
   }

   public static boolean shouldMute(long key, long currentTick) {
      long until = muteUntilTick.getOrDefault(key, Long.MIN_VALUE);
      if (until == Long.MIN_VALUE) {
         return false;
      } else if (currentTick <= until) {
         return true;
      } else {
         muteUntilTick.remove(key);
         return false;
      }
   }
}
