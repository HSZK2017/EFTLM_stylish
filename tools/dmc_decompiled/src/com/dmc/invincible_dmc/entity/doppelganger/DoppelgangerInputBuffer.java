package com.dmc.invincible_dmc.entity.doppelganger;

import javax.annotation.Nullable;

public class DoppelgangerInputBuffer {
   private DoppelgangerInputEvent activeInput;
   private long ingressWorldTick;
   private int ttlTicks = 8;

   public void accept(DoppelgangerInputEvent event, long currentWorldTick) {
      this.activeInput = event;
      this.ingressWorldTick = currentWorldTick;
   }

   @Nullable
   public DoppelgangerInputEvent peekValid(long currentWorldTick) {
      if (this.activeInput == null) {
         return null;
      } else if (currentWorldTick - this.ingressWorldTick > (long)this.ttlTicks) {
         this.activeInput = null;
         return null;
      } else {
         return this.activeInput;
      }
   }

   public void consume() {
      this.activeInput = null;
   }

   public boolean isEmpty() {
      return this.activeInput == null;
   }

   public void setTtlTicks(int ttlTicks) {
      this.ttlTicks = Math.max(1, ttlTicks);
   }
}
