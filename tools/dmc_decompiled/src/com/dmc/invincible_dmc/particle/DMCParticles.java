package com.dmc.invincible_dmc.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.particle.HitParticleType;

public class DMCParticles {
   public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "invincible_dmc");
   public static final RegistryObject<SimpleParticleType> TRANSPARENT_AFTER_IMAGE = PARTICLES.register(
      "transparent_after_image", () -> new SimpleParticleType(true)
   );
   public static final RegistryObject<SimpleParticleType> YAMATO_SPHERE = PARTICLES.register("yamato_sphere", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> YAMATO_LAST_SPHERE = PARTICLES.register("yamato_last_sphere", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> YAMATO_FLOOR = PARTICLES.register("yamato_floor", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> COLOR_SHADER_PARTICLE = PARTICLES.register(
      "color_shader_particle", () -> new SimpleParticleType(true)
   );
   public static final RegistryObject<SimpleParticleType> JUDGEMENT_CUT_SEQUENCE = PARTICLES.register(
      "judgement_cut_sequence", () -> new SimpleParticleType(true)
   );
   public static final RegistryObject<SimpleParticleType> JUDGEMENT_CUT_SEQUENCE_NORMAL = PARTICLES.register(
      "judgement_cut_sequence_normal", () -> new SimpleParticleType(true)
   );
   public static final RegistryObject<SimpleParticleType> JUDGEMENT_CUT_SEQUENCE_DMC4 = PARTICLES.register(
      "judgement_cut_sequence_dmc4", () -> new SimpleParticleType(true)
   );
   public static final RegistryObject<SimpleParticleType> JUDGEMENT_CUT_SEQUENCE_NORMAL_DMC4 = PARTICLES.register(
      "judgement_cut_sequence_normal_dmc4", () -> new SimpleParticleType(true)
   );
   public static final RegistryObject<SimpleParticleType> JUDGEMENT_CUT_END_DMC4 = PARTICLES.register(
      "judgement_cut_end_dmc4", () -> new SimpleParticleType(true)
   );
   public static final RegistryObject<SimpleParticleType> VERGIL_SLASH_SEQUENCE = PARTICLES.register(
      "vergil_slash_sequence", () -> new SimpleParticleType(true)
   );
   public static final RegistryObject<SimpleParticleType> VERGIL_SLASH_SEQUENCE_ALT = PARTICLES.register(
      "vergil_slash_sequence_alt", () -> new SimpleParticleType(true)
   );
   public static final RegistryObject<SimpleParticleType> JUDGEMENT_CUT_PARTICLE = PARTICLES.register(
      "judgement_cut_particle", () -> new SimpleParticleType(true)
   );
   public static final RegistryObject<SimpleParticleType> BLOOM_TRAIL = PARTICLES.register("bloom_trail", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> FLOWING_ANIMATION_TRAIL = PARTICLES.register("flowing_trail", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> BLOOM_TRAIL_SWORD = PARTICLES.register("bloom_trail_sword", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> STATIC_AIR_TRAIL = PARTICLES.register("static_air_trail", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> PORTAL = PARTICLES.register("portal", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> PROCEDURAL_END_PORTAL = PARTICLES.register(
      "procedural_end_portal", () -> new SimpleParticleType(true)
   );
   public static final RegistryObject<HitParticleType> PARRY_FLASH_MAIN = PARTICLES.register("parry_flash_main", () -> new HitParticleType(true));
   public static final RegistryObject<HitParticleType> ATTACK_MAIN = PARTICLES.register(
      "attack_main", () -> new HitParticleType(true, HitParticleType.RANDOM_WITHIN_BOUNDING_BOX, HitParticleType.ZERO)
   );
   public static final RegistryObject<SimpleParticleType> PARRY_FLASH_MAIN_RENDER = PARTICLES.register(
      "parry_flash_main_render", () -> new SimpleParticleType(true)
   );
   public static final RegistryObject<SimpleParticleType> ATTACK_MAIN_RENDER = PARTICLES.register("attack_main_render", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> SPARK_EXPANSIVE = PARTICLES.register("spark_expansive", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> SPARK_CONTRACTILE = PARTICLES.register("spark_contractive", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> NORMAL_SPARK = PARTICLES.register("spark_normal", () -> new SimpleParticleType(true));
   public static final RegistryObject<HitParticleType> ALL_SPARK = PARTICLES.register(
      "all_spark", () -> new HitParticleType(true, HitParticleType.RANDOM_WITHIN_BOUNDING_BOX, HitParticleType.ZERO)
   );
   public static final RegistryObject<SimpleParticleType> SDT_PHASE2_CHROMATIC = PARTICLES.register("sdt_phase2_chromatic", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> NB = PARTICLES.register("nb", () -> new SimpleParticleType(true));
   public static final RegistryObject<HitParticleType> NULL = PARTICLES.register("null", () -> new HitParticleType(true));
}
