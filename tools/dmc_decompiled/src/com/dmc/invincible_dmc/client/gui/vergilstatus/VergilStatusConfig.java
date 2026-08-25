package com.dmc.invincible_dmc.client.gui.vergilstatus;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;

public final class VergilStatusConfig {
   public static final ForgeConfigSpec SPEC;
   public static final DoubleValue POS_X;
   public static final DoubleValue POS_Y;
   public static final DoubleValue SCALE;
   static final double DEFAULT_X = 5.0;
   static final double DEFAULT_Y = 5.0;
   static final double DEFAULT_SCALE = 0.7;

   public static void resetToDefaults() {
      POS_X.set(5.0);
      POS_Y.set(5.0);
      SCALE.set(0.7);
      SPEC.save();
   }

   static {
      Builder builder = new Builder();
      builder.comment("Vergil Status HUD Configuration").push("hud");
      POS_X = builder.comment("Horizontal offset from left edge (pixels)").defineInRange("pos_x", 5.0, -2048.0, 4096.0);
      POS_Y = builder.comment("Vertical offset from top edge (pixels)").defineInRange("pos_y", 5.0, -2048.0, 4096.0);
      SCALE = builder.comment("Scale multiplier (1.0 = default)").defineInRange("scale", 0.7, 0.1, 5.0);
      builder.pop();
      SPEC = builder.build();
   }
}
