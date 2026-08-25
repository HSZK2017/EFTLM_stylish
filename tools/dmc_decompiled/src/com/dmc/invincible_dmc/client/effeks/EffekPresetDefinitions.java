package com.dmc.invincible_dmc.client.effeks;

import com.dmc.invincible_dmc.DMConfig;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;

public final class EffekPresetDefinitions {
   private EffekPresetDefinitions() {
   }

   static void build(Builder builder) {
      preset("render.summoned_sword_shader", DMConfig.SUMMONED_SWORD_SHADER, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      preset("air_trail", DMConfig.AIR_TRAIL, EffekConfig.Preset.LOW, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      preset("flowing_trail", DMConfig.FLOWING_TRAIL, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      preset("bloom_post_processing", DMConfig.AAA_EFFECT_BLOOM, EffekConfig.Preset.EXTREME);
      preset("chromatic_aberration_post_processing", DMConfig.AAA_EFFECT_CHROMATIC_ABERRATION, EffekConfig.Preset.EXTREME);
      preset("render.vix.black_white_flash", DMConfig.VIX_BLACK_WHITE_FLASH, EffekConfig.Preset.LOW, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      preset("render.vix.cold_gray", DMConfig.VIX_COLD_GRAY, EffekConfig.Preset.LOW, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      preset("render.vix.color_radial_blur", DMConfig.VIX_COLOR_RADIAL_BLUR, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      preset("render.vix.impact_blur", DMConfig.VIX_IMPACT_BLUR, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      preset(
         "render.vix.pure_chromatic_aberration",
         DMConfig.VIX_PURE_CHROMATIC_ABERRATION,
         EffekConfig.Preset.LOW,
         EffekConfig.Preset.MEDIUM,
         EffekConfig.Preset.EXTREME
      );
      preset("render.vix.screen_distortion", DMConfig.VIX_SCREEN_DISTORTION, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      preset("render.vix.screen_flash", DMConfig.VIX_SCREEN_FLASH, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      preset("render.vix.screen_vignette", DMConfig.VIX_SCREEN_VIGNETTE, EffekConfig.Preset.LOW, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      preset("render.vix.particle_bloom", DMConfig.VIX_PARTICLE_BLOOM, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      preset("render.vix.particle_chromatic_aberration", DMConfig.VIX_PARTICLE_CHROMATIC_ABERRATION, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      preset(
         "render.vix.particle_chromatic_aberration_enhanced",
         DMConfig.VIX_PARTICLE_CHROMATIC_ABERRATION_ENHANCED,
         EffekConfig.Preset.MEDIUM,
         EffekConfig.Preset.EXTREME
      );
      preset("render.vix.particle_edge_glow", DMConfig.VIX_PARTICLE_EDGE_GLOW, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      preset("render.vix.space_broken", DMConfig.VIX_SPACE_BROKEN, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "dirt_2", true, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "stone_2", true, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "tier0_slash", true, EffekConfig.Preset.LOW, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "door", true, EffekConfig.Preset.LOW, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "flash", true, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "flash_small", true, EffekConfig.Preset.LOW, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "sheath", true, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "ground", true, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "power_floor", true, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "spark", true, EffekConfig.Preset.LOW, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "jce_fire", true, EffekConfig.Preset.EXTREME);
      def(builder, "judgement_cut", true, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "jce_disorder", true, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "light_slash", true, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "light_ring", true, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "tier1plus_slash", true, EffekConfig.Preset.LOW, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "rush", true, EffekConfig.Preset.LOW, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "rush_disorder", true, EffekConfig.Preset.LOW, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "dance_b", true, EffekConfig.Preset.LOW, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "dance_b_disorder", true, EffekConfig.Preset.LOW, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "parry", true, EffekConfig.Preset.LOW, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "sdt1_done", true, EffekConfig.Preset.LOW, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "sdt1_charge", true, EffekConfig.Preset.LOW, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "sdt2_done", true, EffekConfig.Preset.LOW, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "sdt_fire1", true, EffekConfig.Preset.LOW, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "sdt_fire2", true, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "sdt_spark", true, EffekConfig.Preset.LOW, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "sdt", true, EffekConfig.Preset.LOW, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "sdt_mini", true, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "sdt_out", true, EffekConfig.Preset.LOW, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "shock_wave", true, EffekConfig.Preset.LOW, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "void_slash", true, EffekConfig.Preset.LOW, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "meteor", true, EffekConfig.Preset.EXTREME);
      def(builder, "attack", true, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "execute", true, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "flash_point", false, EffekConfig.Preset.LOW, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
      def(builder, "demonic_domain", true, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME);
   }

   private static void preset(String key, BooleanValue value, EffekConfig.Preset... enabledPresets) {
      EffekConfig.registerPreset(key, value, enabledPresets);
   }

   private static BooleanValue def(Builder builder, String key, boolean defaultValue, EffekConfig.Preset... enabledPresets) {
      return EffekConfig.define(builder, key, defaultValue, enabledPresets);
   }
}
