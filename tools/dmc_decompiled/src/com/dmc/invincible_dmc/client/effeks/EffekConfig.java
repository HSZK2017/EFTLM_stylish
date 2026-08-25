package com.dmc.invincible_dmc.client.effeks;

import com.dmc.invincible_dmc.DMConfig;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;

public final class EffekConfig {
   public static final Map<String, BooleanValue> ENABLED = new LinkedHashMap<>();
   private static final EffekConfig.Preset[] MATCHABLE_PRESETS = new EffekConfig.Preset[]{
      EffekConfig.Preset.NONE, EffekConfig.Preset.LOW, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME
   };
   private static final Map<String, EnumSet<EffekConfig.Preset>> ENABLED_PRESETS = new LinkedHashMap<>();
   private static final Map<String, BooleanValue> PRESET_VALUES = new LinkedHashMap<>();
   private static final Map<String, BooleanValue> CUSTOM_VALUES = new LinkedHashMap<>();
   private static BooleanValue CUSTOM_PRESET_SAVED;

   private EffekConfig() {
   }

   static BooleanValue define(Builder builder, String key, boolean defaultValue, EffekConfig.Preset... enabledPresets) {
      BooleanValue value = builder.define("effeks." + key, defaultValue);
      ENABLED.put(key, value);
      registerPreset(key, value, enabledPresets);
      return value;
   }

   public static void registerPreset(String key, BooleanValue value, EffekConfig.Preset... enabledPresets) {
      EnumSet<EffekConfig.Preset> presets = EnumSet.noneOf(EffekConfig.Preset.class);

      for (EffekConfig.Preset preset : enabledPresets) {
         if (preset != EffekConfig.Preset.NONE && preset != EffekConfig.Preset.CUSTOM) {
            presets.add(preset);
         }
      }

      ENABLED_PRESETS.put(key, presets);
      PRESET_VALUES.put(key, value);
   }

   public static void build(Builder builder) {
      builder.comment(
         new String[]{
            "Effek particle toggles - each key controls one visual effect.",
            "Selecting a global preset writes all registered toggles.",
            "The current toggle combination is labeled with its matching preset, or CUSTOM if none matches."
         }
      );
      EffekPresetDefinitions.build(builder);
      defineCustomPresetStorage(builder);
   }

   public static boolean isEnabled(String key) {
      BooleanValue value = ENABLED.get(key);
      return value == null || (Boolean)value.get();
   }

   public static boolean isEnabled(String key, BooleanValue value) {
      return (Boolean)value.get();
   }

   public static void applyPreset(EffekConfig.Preset preset) {
      if (preset == EffekConfig.Preset.CUSTOM) {
         restoreCustomPreset();
      } else {
         if (DMConfig.EFFECT_PRESET.get() == EffekConfig.Preset.CUSTOM) {
            captureCustomPreset();
         }

         PRESET_VALUES.forEach((key, value) -> {
            EnumSet<EffekConfig.Preset> enabledPresets = ENABLED_PRESETS.get(key);
            value.set(enabledPresets != null && enabledPresets.contains(preset));
         });
         DMConfig.EFFECT_PRESET.set(preset);
      }
   }

   public static boolean isPresetManaged(String key) {
      return PRESET_VALUES.containsKey(key);
   }

   public static boolean hasCustomPresetSnapshot() {
      return CUSTOM_PRESET_SAVED != null && (Boolean)CUSTOM_PRESET_SAVED.get();
   }

   public static void refreshPresetFromValues() {
      EffekConfig.Preset current = (EffekConfig.Preset)DMConfig.EFFECT_PRESET.get();
      if (current == EffekConfig.Preset.CUSTOM || !matchesPreset(current)) {
         for (EffekConfig.Preset preset : MATCHABLE_PRESETS) {
            if (matchesPreset(preset)) {
               if (current != preset) {
                  DMConfig.EFFECT_PRESET.set(preset);
               }

               return;
            }
         }

         captureCustomPreset();
         DMConfig.EFFECT_PRESET.set(EffekConfig.Preset.CUSTOM);
      }
   }

   private static void captureCustomPreset() {
      PRESET_VALUES.forEach((key, value) -> {
         BooleanValue customValue = CUSTOM_VALUES.get(key);
         if (customValue != null) {
            customValue.set((Boolean)value.get());
         }
      });
      if (CUSTOM_PRESET_SAVED != null) {
         CUSTOM_PRESET_SAVED.set(true);
      }
   }

   private static void restoreCustomPreset() {
      if (hasCustomPresetSnapshot()) {
         PRESET_VALUES.forEach((key, value) -> {
            BooleanValue customValue = CUSTOM_VALUES.get(key);
            if (customValue != null) {
               value.set((Boolean)customValue.get());
            }
         });
         DMConfig.EFFECT_PRESET.set(EffekConfig.Preset.CUSTOM);
      }
   }

   private static void defineCustomPresetStorage(Builder builder) {
      builder.push("effeks");
      builder.push("custom_preset");
      CUSTOM_PRESET_SAVED = builder.comment("Whether a restorable custom visual-effect preset has been saved.").define("saved", false);
      builder.push("values");
      PRESET_VALUES.forEach((key, value) -> {
         boolean defaultValue = (Boolean)value.getDefault();
         CUSTOM_VALUES.put(key, builder.comment("Saved custom value for " + key + ".").define(key, defaultValue));
      });
      builder.pop(3);
   }

   private static boolean matchesPreset(EffekConfig.Preset preset) {
      for (Entry<String, BooleanValue> entry : PRESET_VALUES.entrySet()) {
         EnumSet<EffekConfig.Preset> enabledPresets = ENABLED_PRESETS.get(entry.getKey());
         boolean expected = enabledPresets != null && enabledPresets.contains(preset);
         if ((Boolean)entry.getValue().get() != expected) {
            return false;
         }
      }

      return true;
   }

   public static enum Preset {
      NONE,
      LOW,
      MEDIUM,
      EXTREME,
      CUSTOM;
   }
}
