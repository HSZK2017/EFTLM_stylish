package com.dmc.invincible_dmc.client.input;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record InputFrame(long tick, long sequence, long capturedAtMs, short mask) {
   public boolean isDown(int bit) {
      return (this.mask & 1 << bit) != 0;
   }
}
