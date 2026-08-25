package com.dmc.invincible_dmc.client.input;

public final class HoldToActionHelper {
   private final int longPressTicks;
   private boolean down;
   private int pressedTicks;
   private boolean cycleDone;

   public HoldToActionHelper(int longPressTicks) {
      this.longPressTicks = longPressTicks;
   }

   public void press() {
      this.down = true;
      this.pressedTicks = 0;
      this.cycleDone = false;
   }

   public void tick(boolean keyStillHeld, Runnable onLongPress, Runnable onShortPress) {
      if (this.down) {
         if (!keyStillHeld) {
            if (!this.cycleDone && onShortPress != null) {
               onShortPress.run();
            }

            this.reset();
         } else if (!this.cycleDone) {
            this.pressedTicks++;
            if (this.pressedTicks >= this.longPressTicks) {
               this.cycleDone = true;
               if (onLongPress != null) {
                  onLongPress.run();
               }
            }
         }
      }
   }

   public void reset() {
      this.down = false;
      this.pressedTicks = 0;
      this.cycleDone = false;
   }

   public boolean isDown() {
      return this.down;
   }
}
