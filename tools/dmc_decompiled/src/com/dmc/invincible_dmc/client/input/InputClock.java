package com.dmc.invincible_dmc.client.input;

import net.minecraft.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class InputClock {
   private static long currentTick = -1L;
   private static long sequence;

   private InputClock() {
   }

   static void beginTick(long tick) {
      currentTick = tick;
   }

   static long currentTick() {
      return currentTick;
   }

   static long nextSequence() {
      return ++sequence;
   }

   static long nowMillis() {
      return Util.m_137550_();
   }

   static void reset() {
      currentTick = -1L;
   }
}
