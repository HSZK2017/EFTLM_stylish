package com.dmc.invincible_dmc.client.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public final class AAAPPerformanceClientConfig {
   public static final ForgeConfigSpec SPEC;
   public static final BooleanValue ENABLED;
   public static final BooleanValue LIMIT_EMITTER_COUNT;
   public static final BooleanValue LIMIT_INSTANCE_COUNT;
   public static final BooleanValue RESERVE_BURST_INSTANCES;
   public static final BooleanValue ENABLE_SOFT_BUDGETS;
   public static final BooleanValue LIMIT_FRAME_ADDITIONS;
   public static final BooleanValue NORMALIZE_TIME_BUDGETS;
   public static final BooleanValue LIMIT_SIMULATION_RATE;
   public static final BooleanValue LIMIT_COLLISION_RAYCASTS;
   public static final BooleanValue LIMIT_JCE_BURSTS;
   public static final BooleanValue LIMIT_SPARK_EFFECTS;
   public static final BooleanValue DISABLE_VIX_POST_PROCESSING_DURING_JCE;
   public static final BooleanValue ADAPTIVE_VIX_SINGLE_PASS;
   public static final BooleanValue USE_STATIC_YAMATO_LAST_SPHERE;
   public static final IntValue MAX_WORLD_EMITTERS;
   public static final IntValue MAX_WORLD_INSTANCES;
   public static final IntValue SOFT_WORLD_EMITTERS;
   public static final IntValue SOFT_WORLD_INSTANCES;
   public static final IntValue WORLD_INSTANCE_RESERVATION_PER_EMITTER;
   public static final IntValue MAX_JCE_WORLD_EMITTERS;
   public static final IntValue MAX_JCE_WORLD_INSTANCES;
   public static final IntValue SOFT_JCE_WORLD_EMITTERS;
   public static final IntValue SOFT_JCE_WORLD_INSTANCES;
   public static final IntValue MAX_WORLD_EMITTER_STARTS_PER_FRAME;
   public static final IntValue MAX_WORLD_NEW_INSTANCES_PER_FRAME;
   public static final IntValue VIX_SINGLE_PASS_INSTANCE_THRESHOLD;
   public static final IntValue MAX_SPARK_EMITTERS;
   public static final IntValue MAX_SDT_SPARK_EMITTERS;
   public static final IntValue MAX_SPARK_STARTS_PER_FRAME;
   public static final IntValue MAX_HAND_EMITTERS;
   public static final IntValue MAX_HAND_INSTANCES;
   public static final IntValue SOFT_HAND_EMITTERS;
   public static final IntValue SOFT_HAND_INSTANCES;
   public static final IntValue HAND_INSTANCE_RESERVATION_PER_EMITTER;
   public static final IntValue MAX_HAND_EMITTER_STARTS_PER_FRAME;
   public static final IntValue MAX_HAND_NEW_INSTANCES_PER_FRAME;
   public static final IntValue COLLISION_RAYCASTS_PER_FRAME;
   public static final IntValue SIMULATION_RATE_LIMIT_HZ;
   public static final BooleanValue LOG_STATISTICS;

   private AAAPPerformanceClientConfig() {
   }

   public static boolean isEnabled(BooleanValue option) {
      return (Boolean)ENABLED.get() && (Boolean)option.get();
   }

   static {
      Builder builder = new Builder();
      builder.comment("AAAParticles client performance limits").push("aaaparticles");
      ENABLED = builder.comment("Master switch for all Invincible DMC AAAParticles optimizations.").define("enabled", true);
      LIMIT_EMITTER_COUNT = builder.comment("Drop new anonymous one-shot effects when the configured emitter limit is reached.")
         .define("limit_emitter_count", true);
      LIMIT_INSTANCE_COUNT = builder.comment("Drop new anonymous one-shot effects when the configured native instance limit is reached.")
         .define("limit_instance_count", true);
      RESERVE_BURST_INSTANCES = builder.comment("Reserve estimated instance capacity immediately for same-frame emitter bursts.")
         .define("reserve_burst_instances", true);
      ENABLE_SOFT_BUDGETS = builder.comment("Use soft emitter and instance thresholds before applying per-frame burst throttles.")
         .define("enable_soft_budgets", true);
      LIMIT_FRAME_ADDITIONS = builder.comment("After a soft threshold is crossed, limit accepted emitter starts and estimated new instances per frame.")
         .define("limit_frame_additions", true);
      NORMALIZE_TIME_BUDGETS = builder.comment(
            "Normalize collision, Spark and burst budgets to a 60 Hz reference rate instead of resetting full capacity every rendered frame."
         )
         .define("normalize_time_budgets", true);
      LIMIT_SIMULATION_RATE = builder.comment(
            "Decouple Effekseer simulation updates from rendering and cap simulation frequency while continuing to draw every frame."
         )
         .define("limit_simulation_rate", true);
      LIMIT_COLLISION_RAYCASTS = builder.comment("Limit AAAP collision raycasts per rendered world frame.").define("limit_collision_raycasts", true);
      LIMIT_JCE_BURSTS = builder.comment("Use a stricter world emitter limit during Judgement Cut End bursts.").define("limit_jce_bursts", true);
      LIMIT_SPARK_EFFECTS = builder.comment("Apply dedicated concurrency and per-frame start limits to Spark effects.").define("limit_spark_effects", true);
      DISABLE_VIX_POST_PROCESSING_DURING_JCE = builder.comment("Use one original AAAP draw during Judgement Cut End bursts.")
         .define("disable_vix_post_processing_during_jce", false);
      ADAPTIVE_VIX_SINGLE_PASS = builder.comment(
            "Draw AAAP effects once when the native instance count is high instead of running repeated VIX post-processing passes."
         )
         .define("adaptive_vix_single_pass", true);
      USE_STATIC_YAMATO_LAST_SPHERE = builder.comment("Render the Judgement Cut End sphere with a static mesh instead of a skinned mesh.")
         .define("use_static_yamato_last_sphere", true);
      MAX_WORLD_EMITTERS = builder.comment(
            new String[]{
               "Hard maximum concurrent AAAP world emitters before new anonymous one-shot effects are dropped.",
               "Named emitters are not dropped because they are generally lifecycle-controlled effects."
            }
         )
         .defineInRange("max_world_emitters", 112, 1, 1024);
      MAX_WORLD_INSTANCES = builder.comment("Hard maximum native Effekseer world instances before new one-shot effects are dropped.")
         .defineInRange("max_world_instances", 48000, 1000, 200000);
      SOFT_WORLD_EMITTERS = builder.comment("World emitter count that activates per-frame burst throttling before the hard limit.")
         .defineInRange("soft_world_emitters", 72, 1, 1024);
      SOFT_WORLD_INSTANCES = builder.comment("World instance count that activates per-frame burst throttling before the hard limit.")
         .defineInRange("soft_world_instances", 28000, 1000, 200000);
      WORLD_INSTANCE_RESERVATION_PER_EMITTER = builder.comment(
            new String[]{
               "Estimated native instance cost reserved immediately for each accepted world emitter.",
               "This prevents same-frame effect bursts from passing the instance limit before Effekseer expands them."
            }
         )
         .defineInRange("world_instance_reservation_per_emitter", 192, 0, 10000);
      MAX_JCE_WORLD_EMITTERS = builder.comment("Hard world emitter limit used while Judgement Cut End is active.")
         .defineInRange("max_jce_world_emitters", 64, 1, 1024);
      MAX_JCE_WORLD_INSTANCES = builder.comment("Hard native Effekseer world instance limit while Judgement Cut End is active.")
         .defineInRange("max_jce_world_instances", 28000, 1000, 200000);
      SOFT_JCE_WORLD_EMITTERS = builder.comment("Judgement Cut End emitter count that activates per-frame burst throttling.")
         .defineInRange("soft_jce_world_emitters", 48, 1, 1024);
      SOFT_JCE_WORLD_INSTANCES = builder.comment("Judgement Cut End instance count that activates per-frame burst throttling.")
         .defineInRange("soft_jce_world_instances", 20000, 1000, 200000);
      MAX_WORLD_EMITTER_STARTS_PER_FRAME = builder.comment("Maximum world emitter starts accepted in one rendered frame after a soft threshold is crossed.")
         .defineInRange("max_world_emitter_starts_per_frame", 12, 1, 256);
      MAX_WORLD_NEW_INSTANCES_PER_FRAME = builder.comment(
            "Maximum estimated new world instances accepted in one rendered frame after a soft threshold is crossed."
         )
         .defineInRange("max_world_new_instances_per_frame", 6000, 0, 100000);
      VIX_SINGLE_PASS_INSTANCE_THRESHOLD = builder.comment("Native world instance count that activates the VIX single-pass fallback.")
         .defineInRange("vix_single_pass_instance_threshold", 28000, 1000, 200000);
      MAX_SPARK_EMITTERS = builder.comment("Maximum concurrent regular Spark emitters.").defineInRange("max_spark_emitters", 24, 0, 128);
      MAX_SDT_SPARK_EMITTERS = builder.comment("Maximum concurrent SDT Spark emitters.").defineInRange("max_sdt_spark_emitters", 16, 0, 128);
      MAX_SPARK_STARTS_PER_FRAME = builder.comment("Maximum combined Spark emitter starts per rendered world frame.")
         .defineInRange("max_spark_starts_per_frame", 8, 0, 64);
      MAX_HAND_EMITTERS = builder.comment("Hard maximum concurrent first-person emitters per hand context.").defineInRange("max_hand_emitters", 24, 1, 128);
      MAX_HAND_INSTANCES = builder.comment("Hard maximum native Effekseer instances per first-person hand context.")
         .defineInRange("max_hand_instances", 6000, 100, 50000);
      SOFT_HAND_EMITTERS = builder.comment("First-person emitter count per hand that activates per-frame burst throttling.")
         .defineInRange("soft_hand_emitters", 18, 1, 128);
      SOFT_HAND_INSTANCES = builder.comment("First-person instance count per hand that activates per-frame burst throttling.")
         .defineInRange("soft_hand_instances", 4500, 100, 50000);
      HAND_INSTANCE_RESERVATION_PER_EMITTER = builder.comment("Estimated native instance cost reserved immediately for each accepted first-person emitter.")
         .defineInRange("hand_instance_reservation_per_emitter", 64, 0, 5000);
      MAX_HAND_EMITTER_STARTS_PER_FRAME = builder.comment(
            "Maximum first-person emitter starts per hand context in one frame after a soft threshold is crossed."
         )
         .defineInRange("max_hand_emitter_starts_per_frame", 6, 1, 128);
      MAX_HAND_NEW_INSTANCES_PER_FRAME = builder.comment(
            "Maximum estimated new first-person instances per hand context in one frame after a soft threshold is crossed."
         )
         .defineInRange("max_hand_new_instances_per_frame", 1500, 0, 50000);
      COLLISION_RAYCASTS_PER_FRAME = builder.comment(
            new String[]{
               "Maximum AAAP particle collision raycasts per 60 Hz reference slice when time normalization is enabled.",
               "Extra collision requests are treated as misses. Set to 0 to disable AAAP collision raycasts."
            }
         )
         .defineInRange("collision_raycasts_per_frame", 256, 0, 4096);
      SIMULATION_RATE_LIMIT_HZ = builder.comment("Maximum Effekseer simulation update frequency. Rendering can continue above this rate.")
         .defineInRange("simulation_rate_limit_hz", 60, 20, 60);
      LOG_STATISTICS = builder.comment("Log AAAP emitter, native instance, collision and dropped-effect statistics every five seconds.")
         .define("log_statistics", false);
      builder.pop();
      SPEC = builder.build();
   }
}
