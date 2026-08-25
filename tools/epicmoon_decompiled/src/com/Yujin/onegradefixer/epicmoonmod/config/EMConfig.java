package com.Yujin.onegradefixer.epicmoonmod.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;

public final class EMConfig {
   public static final ForgeConfigSpec SPEC;
   public static final DoubleValue TS_PARAMETER_GAIN_MULTIPLIER;
   public static final DoubleValue DUAL_PARAMETER_GAIN_MULTIPLIER;
   public static final DoubleValue AUTO_DODGE_MAX_DAMAGE;

   private EMConfig() {
   }

   public static float getTsParameterGainMultiplier() {
      return ((Double)TS_PARAMETER_GAIN_MULTIPLIER.get()).floatValue();
   }

   public static float getDualParameterGainMultiplier() {
      return ((Double)DUAL_PARAMETER_GAIN_MULTIPLIER.get()).floatValue();
   }

   public static float getAutoDodgeMaxDamage() {
      return ((Double)AUTO_DODGE_MAX_DAMAGE.get()).floatValue();
   }

   static {
      Builder builder = new Builder();
      builder.push("weapon_parameter");
      TS_PARAMETER_GAIN_MULTIPLIER = builder.comment(
            new String[]{"Tiantui Star's Sword parameter gain multiplier.", "Default 0.10 keeps the current behavior.", "Set to 0 to disable parameter gain."}
         )
         .translation("config.epicmoonmod.ts_parameter_gain_multiplier")
         .defineInRange("tsParameterGainMultiplier", 0.1, 0.0, 100.0);
      DUAL_PARAMETER_GAIN_MULTIPLIER = builder.comment(
            new String[]{"La Spada di Palermo parameter gain multiplier.", "Default 0.03 keeps the current behavior.", "Set to 0 to disable parameter gain."}
         )
         .translation("config.epicmoonmod.dual_parameter_gain_multiplier")
         .defineInRange("dualParameterGainMultiplier", 0.03, 0.0, 100.0);
      builder.pop();
      builder.push("automatic_dodge");
      AUTO_DODGE_MAX_DAMAGE = builder.comment(
            new String[]{
               "Maximum final damage that can trigger the automatic dodge.",
               "The value is checked after armor, armor toughness,",
               "and protection enchantment reductions.",
               "Default 8.0 keeps the current behavior.",
               "Set to 0 to disable automatic dodge by damage threshold."
            }
         )
         .translation("config.epicmoonmod.precognition_eye_auto_dodge_max_damage")
         .defineInRange("maxDamage", 8.0, 0.0, 1000000.0);
      builder.pop();
      SPEC = builder.build();
   }
}
