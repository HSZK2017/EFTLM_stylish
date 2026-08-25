package com.dmc.invincible_dmc;

import com.dmc.invincible_dmc.client.effeks.EffekConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "invincible_dmc",
   bus = Bus.MOD
)
public class DMConfig {
   private static final Builder BUILDER = new Builder();
   public static final IntValue EFFECT_TICK = BUILDER.comment(
         new String[]{"Resets dodge/parry success window duration", "How long after successful dodge/parry before auto-resetting basic attack combo."}
      )
      .defineInRange("effect_tick", 20, 0, Integer.MAX_VALUE);
   public static final IntValue RESET_TICK = BUILDER.comment(
         new String[]{"Combo reset time", "How long after action ends before auto-resetting basic attack combo."}
      )
      .defineInRange("reset_tick", 16, 0, Integer.MAX_VALUE);
   public static final IntValue INPUT_BUFFER_DURATION_TICKS = BUILDER.comment(
         new String[]{
            "Pre-input window duration (ticks)",
            "Key presses retry sending during this time when skill slot unavailable.",
            "~8 ticks = 0.4s. Overridable in ComboBasicAttack.Builder."
         }
      )
      .defineInRange("input_buffer_duration_ticks", 8, 0, Integer.MAX_VALUE);
   public static final IntValue INPUT_BUFFER_CAPACITY = BUILDER.comment(new String[]{"Pre-input buffer max capacity", "Max key entries in ring buffer."})
      .defineInRange("input_buffer_capacity", 16, 1, Integer.MAX_VALUE);
   public static final IntValue LONG_PRESS_THRESHOLD = BUILDER.comment(
         new String[]{"Long-press threshold (ticks)", "When held duration reaches this, treated as long-press.", "~4 ticks = 0.2s."}
      )
      .defineInRange("long_press_threshold", 4, 1, 20);
   public static final IntValue CONTROLLER_LONG_PRESS_THRESHOLD = BUILDER.comment(
         new String[]{"Controller long-press threshold (ticks)", "Separate from keyboard.", "Default 6 ticks (0.3s)."}
      )
      .defineInRange("controller_long_press_threshold", 6, 1, 30);
   public static final IntValue SHORT_PRESS_WINDOW = BUILDER.comment(new String[]{"Short-press window (ticks)", "Release within this time forces short-press."})
      .defineInRange("short_press_window", 1, 0, 10);
   public static final IntValue DIRECTION_SEQUENCE_MATCH_WINDOW = BUILDER.comment(
         new String[]{"Direction sequence match window (ticks)", "Max interval between two direction keys for a valid sequence."}
      )
      .defineInRange("direction_sequence_match_window", 5, 1, 30);
   public static final IntValue DIRECTION_SEQUENCE_ACTIVATION_WINDOW = BUILDER.comment(
         new String[]{"Direction sequence activation window (ticks)", "After sequence formed, attack key still valid within this time."}
      )
      .defineInRange("direction_sequence_activation_window", 9, 1, 60);
   public static final BooleanValue DOPPEL_MIRROR_CONTROL_ENABLED = BUILDER.comment(
         "Enable mirrored direction input for Doppelganger positioning. Disabled by default."
      )
      .define("doppel.mirror_control_enabled", false);
   public static final BooleanValue LOG_COMBO_ENGINE = BUILDER.comment("Combo engine: input sampling, intent resolution").define("log.combo_engine", false);
   public static final BooleanValue LOG_COMBO_EXECUTE = BUILDER.comment("Combo execution: dispatch, reserve, netsend").define("log.combo_execute", false);
   public static final BooleanValue LOG_COMBO_SERVER = BUILDER.comment("Server combo: condition matching, node execution").define("log.combo_server", false);
   public static final BooleanValue LOG_DOPPEL_INPUT = BUILDER.comment("Doppel input dispatch").define("log.doppel_input", false);
   public static final BooleanValue LOG_DOPPEL_COMBO = BUILDER.comment("Doppel combo executor and state").define("log.doppel_combo", false);
   public static final BooleanValue LOG_DOPPEL_CC = BUILDER.comment("Doppel/player CC state machine").define("log.doppel_cc", false);
   public static final BooleanValue LOG_DOPPEL_GENERAL = BUILDER.comment("Doppel patch, script, manager").define("log.doppel_general", false);
   public static final BooleanValue LOG_DOPPEL_NET = BUILDER.comment("Doppel network: CPDoppelganger*").define("log.doppel_net", false);
   public static final BooleanValue LOG_JC = BUILDER.comment("Judgement Cut controller").define("log.jc", false);
   public static final BooleanValue LOG_SWORD = BUILDER.comment("Summoned Sword controller").define("log.sword", false);
   public static final BooleanValue LOG_YAMATO = BUILDER.comment("Yamato skill, animations, SDT").define("log.yamato", false);
   public static final BooleanValue LOG_NETWORK = BUILDER.comment("Generic network packets").define("log.network", false);
   public static final BooleanValue LOG_RENDER = BUILDER.comment("Screen shaders, bloom, particles").define("log.render", false);
   public static final BooleanValue LOG_COMPAT = BUILDER.comment("Compat: Waystones, Xaero, Oculus, etc").define("log.compat", false);
   public static final BooleanValue LOG_DIRECTION = BUILDER.comment("Directional sequence conditions").define("log.direction", false);
   public static final BooleanValue LOG_CAPABILITY = BUILDER.comment("Capability events").define("log.capability", false);
   public static final BooleanValue LOG_STUN = BUILDER.comment("Custom stun pipeline: resolve, apply, interrupt, cleanup").define("log.stun", false);
   public static final BooleanValue SPACE_BROKEN_SHRINK_ENABLED = BUILDER.comment("Enable JCE space-to-close effect.")
      .define("space_broken.shrink_enabled", false);
   public static final DoubleValue SPACE_BROKEN_SHRINK_START = BUILDER.comment("start point.(%)").defineInRange("space_broken.shrink_start", 0.74, 0.0, 1.0);
   public static final DoubleValue SPACE_BROKEN_SHRINK_END = BUILDER.comment("end point,(%)").defineInRange("space_broken.shrink_end", 1.0, 0.0, 1.0);
   public static final BooleanValue SDT_WEAPON_RENDERER = BUILDER.comment("Enable SDT weapon mesh/texture replacement via SdtWeaponRenderer.")
      .define("sdt.weapon_renderer", true);
   public static final BooleanValue SDT_PLAYER_RENDERER = BUILDER.comment("Enable SDT player mesh/texture replacement via PSdtPlayerRenderer.")
      .define("sdt.player_renderer", true);
   public static final BooleanValue SDT_CHARGE_WEAPON_SWAP = BUILDER.comment(
         "Enable devil sword weapon skin swap after SDT first charge completes (via RenderYamato, not SdtWeaponRenderer)."
      )
      .define("sdt.charge_weapon_swap", true);
   public static final BooleanValue SDT_AFTERIMAGE = BUILDER.comment("Enable the SDT preview and transformation afterimage transitions.")
      .define("sdt.afterimage", true);
   public static final BooleanValue TORSO_STORAGE_ENABLED = BUILDER.comment(
         "When enabled, Yamato weapons in the hotbar (not in hand) are displayed on the player's torso."
      )
      .define("torso_storage.enabled", true);
   public static final IntValue METEOR_SHOWER_COUNT = BUILDER.comment("Void meteor shower: max meteors per wave (random 1~max).")
      .defineInRange("meteor_shower.count", 4, 1, 200);
   public static final IntValue METEOR_SHOWER_INTERVAL = BUILDER.comment("Void meteor shower: interval between waves in ticks (20 = 1s).")
      .defineInRange("meteor_shower.interval", 200, 20, 72000);
   public static final DoubleValue METEOR_SHOWER_MIN_SCALE = BUILDER.comment("Void meteor shower: minimum meteor scale.")
      .defineInRange("meteor_shower.min_scale", 0.75, 0.1, 10.0);
   public static final DoubleValue METEOR_SHOWER_MAX_SCALE = BUILDER.comment("Void meteor shower: maximum meteor scale.")
      .defineInRange("meteor_shower.max_scale", 1.25, 0.1, 10.0);
   public static final DoubleValue FLASH_POINT_SCALE_FACTOR = BUILDER.comment("Flash point scale factor.(*)")
      .defineInRange("flash_point_scale_point", 0.3, 1.0E-8, 9999999.0);
   public static final EnumValue<EffekConfig.Preset> EFFECT_PRESET = BUILDER.comment(
         new String[]{
            "Global visual-effect preset.",
            "Selecting NONE/LOW/MEDIUM/EXTREME writes all registered effect toggles.",
            "The current registered toggle combination shows its matching preset, or CUSTOM if none matches."
         }
      )
      .defineEnum("effeks.preset", EffekConfig.Preset.EXTREME);
   public static final BooleanValue AAA_EFFECT_BLOOM = BUILDER.comment("Apply VIX bloom post-processing to supported AAA particle effects.")
      .define("effeks.bloom_post_processing", true);
   public static final BooleanValue AAA_EFFECT_SPARK_BLOOM = BUILDER.comment("Apply VIX bloom post-processing to regular Spark effects.")
      .define("effeks.spark_bloom_post_processing", false);
   public static final BooleanValue AAA_EFFECT_SDT_SPARK_BLOOM = BUILDER.comment("Apply VIX bloom post-processing to SDT Spark effects.")
      .define("effeks.sdt_spark_bloom_post_processing", false);
   public static final BooleanValue AAA_EFFECT_CHROMATIC_ABERRATION = BUILDER.comment(
         "Apply VIX chromatic-aberration post-processing to supported AAA particle effects."
      )
      .define("effeks.chromatic_aberration_post_processing", true);
   public static final BooleanValue VIX_BLACK_WHITE_FLASH = BUILDER.comment("Enable the VIX black-and-white impact-frame screen effect.")
      .define("render.vix.black_white_flash", true);
   public static final BooleanValue VIX_COLD_GRAY = BUILDER.comment("Enable the VIX cold-gray full-screen effect.").define("render.vix.cold_gray", true);
   public static final BooleanValue VIX_COLOR_RADIAL_BLUR = BUILDER.comment("Enable the VIX colored radial-blur screen effect.")
      .define("render.vix.color_radial_blur", true);
   public static final BooleanValue VIX_IMPACT_BLUR = BUILDER.comment("Enable the VIX impact-blur screen effect.").define("render.vix.impact_blur", true);
   public static final BooleanValue VIX_PURE_CHROMATIC_ABERRATION = BUILDER.comment("Enable the VIX pure chromatic-aberration screen effect.")
      .define("render.vix.pure_chromatic_aberration", true);
   public static final BooleanValue VIX_SCREEN_DISTORTION = BUILDER.comment("Enable the VIX full-screen distortion effect.")
      .define("render.vix.screen_distortion", true);
   public static final BooleanValue VIX_SCREEN_FLASH = BUILDER.comment("Enable the VIX full-screen flash effect.").define("render.vix.screen_flash", true);
   public static final BooleanValue VIX_SCREEN_VIGNETTE = BUILDER.comment("Enable the VIX full-screen vignette effect.")
      .define("render.vix.screen_vignette", true);
   public static final BooleanValue VIX_PARTICLE_BLOOM = BUILDER.comment(
         "Enable the built-in VIX bloom pipeline for DMC particles and trails. Does not control AAA/Effekseer bloom or particle visibility."
      )
      .define("render.vix.particle_bloom", true);
   public static final BooleanValue VIX_PARTICLE_CHROMATIC_ABERRATION = BUILDER.comment(
         "Enable the standard VIX chromatic-aberration pipeline for DMC particles. Does not control AAA/Effekseer chromatic aberration."
      )
      .define("render.vix.particle_chromatic_aberration", true);
   public static final BooleanValue VIX_PARTICLE_CHROMATIC_ABERRATION_ENHANCED = BUILDER.comment(
         "Enable the enhanced VIX chromatic-aberration pipeline for DMC particles."
      )
      .define("render.vix.particle_chromatic_aberration_enhanced", true);
   public static final BooleanValue VIX_PARTICLE_EDGE_GLOW = BUILDER.comment("Enable the VIX edge-glow pipeline for DMC particles.")
      .define("render.vix.particle_edge_glow", true);
   public static final BooleanValue VIX_SPACE_BROKEN = BUILDER.comment(
         "Enable VIX post-processing for space-broken particles. The existing shrink option only controls their closing animation."
      )
      .define("render.vix.space_broken", true);
   public static final BooleanValue YAMATO_BLOOM_TRAIL = BUILDER.comment("Enable Yamato bloom trail").define("yamato_bloom_trail", false);
   public static final BooleanValue AIR_TRAIL = BUILDER.comment("Enable EpicFight air trail").define("air_trail", true);
   public static final BooleanValue FLOWING_TRAIL = BUILDER.comment("Enable EpicFight flowing trail").define("flowing_trail", true);
   public static final BooleanValue MODEL_FACE_CULLING = BUILDER.comment(
         "Enable back-face culling for entity models. When ON, only front-facing surfaces are rendered, improving GPU performance."
      )
      .define("render.model_face_culling", true);
   public static final BooleanValue YAMATO_MODEL_OUTLINE = BUILDER.comment("Enable the Yamato model outline render pass.")
      .define("render.yamato_model_outline", true);
   public static final BooleanValue HIDE_UI_DURING_JCE = BUILDER.comment(
         "Hide the HUD while Judgement Cut End animations are playing. Restores the previous HUD state afterward and is not changed by visual-effect presets."
      )
      .define("render.hide_ui_during_jce", true);
   public static final BooleanValue CINEMATIC_BARS_ENABLED = BUILDER.comment("Enable cinematic letterbox bars used by supported skills and visual effects.")
      .define("render.cinematic_bars_enabled", true);
   public static final BooleanValue YAMATO_JC_PREV_CAMERA_SHAKE = BUILDER.comment("Enable the camera shake during the Judgement Cut End startup effect.")
      .define("camera_shake.yamato_judgement_cut_prev", true);
   public static final BooleanValue YAMATO_JC_PREV2_CAMERA_SHAKE = BUILDER.comment(
         "Enable the camera shake during the alternate Judgement Cut End startup effect."
      )
      .define("camera_shake.yamato_judgement_cut_prev2", true);
   public static final BooleanValue YAMATO_JC_EXECUTION_CAMERA_SHAKE = BUILDER.comment("Enable the camera shake during the Judgement Cut execution impact.")
      .define("camera_shake.yamato_judgement_cut_execution", true);
   public static final BooleanValue YAMATO_SDT_CAMERA_SHAKE = BUILDER.comment("Enable the camera shake when entering Yamato SDT.")
      .define("camera_shake.yamato_sdt_enter", true);
   public static final BooleanValue HIT_ENTITY_CAMERA_SHAKE = BUILDER.comment(
         "Enable the short camera shake when the player hits an entity with an Epic Fight attack."
      )
      .define("camera_shake.hit_entity", true);
   public static final BooleanValue YAMATO_DMC5_BD_PBR = BUILDER.comment(
         "Enable the Yamato DMC5 BD packed PBR material. With an active Oculus/Iris shader pack, the shader-pack PBR pipeline is used. The MER texture uses red for metallic, green for emissive, and blue for roughness."
      )
      .define("render.yamato_dmc5_bd_pbr", true);
   public static final BooleanValue YAMATO_DMC5_BD_PBR_WITHOUT_SHADER_PACK = BUILDER.comment(
         "Enable the built-in PBR shader for Yamato DMC5 BD when no Oculus/Iris shader pack is active. Requires render.yamato_dmc5_bd_pbr. Disabled by default and not changed by visual-effect presets."
      )
      .define("render.yamato_dmc5_bd_pbr_without_shader_pack", false);
   public static final BooleanValue SUMMONED_SWORD_SHADER = BUILDER.comment("Enable the turbulence and UV distortion shader for Vergil Summoned Swords.")
      .define("render.summoned_sword_shader", true);
   public static final BooleanValue DOPPEL_SILHOUETTE = BUILDER.comment(
         "Render Doppelganger as a solid-color silhouette (sky-blue outline) instead of showing the player's skin texture."
      )
      .define("render.doppel_silhouette", true);
   public static final BooleanValue DOPPEL_SILHOUETTE_EMISSIVE = BUILDER.comment(
         "When silhouette mode is ON: enable self-illumination (emissive, unaffected by lighting). Turn OFF for standard lighting."
      )
      .define("render.doppel_silhouette_emissive", true);
   public static final EnumValue<DMConfig.DoppelModelMode> DOPPEL_MODEL = BUILDER.comment(
         "Doppelganger model: ALWAYS_SDT = always Sin Devil Trigger model, PLAYER = always player model, AUTO = SDT model only when owner is in SDT form."
      )
      .defineEnum("render.doppel_model", DMConfig.DoppelModelMode.ALWAYS_SDT);
   public static final EnumValue<DMConfig.DoppelWeaponStrategy> DOPPEL_WEAPON_STRATEGY = BUILDER.comment(
         "Doppelganger weapon strategy: FIXED_DMC5 always uses Yamato DMC5 while copying the owner's Yamato NBT; LEGACY_OWNER_COPY copies the owner's main-hand item and uses the dedicated SDT weapon renderer with the SDT model."
      )
      .defineEnum("render.doppel_weapon_strategy", DMConfig.DoppelWeaponStrategy.FIXED_DMC5);
   static final ForgeConfigSpec SPEC = BUILDER.build();

   static {
      EffekConfig.registerPreset("render.yamato_dmc5_bd_pbr", YAMATO_DMC5_BD_PBR, EffekConfig.Preset.EXTREME);
      EffekConfig.build(BUILDER);
   }

   public static enum DoppelModelMode {
      ALWAYS_SDT,
      PLAYER,
      AUTO;
   }

   public static enum DoppelWeaponStrategy {
      FIXED_DMC5,
      LEGACY_OWNER_COPY;
   }
}
