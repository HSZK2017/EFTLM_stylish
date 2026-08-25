package com.pla.annoyingvillagers.config;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModList;

public final class AnnoyingVillagersClientConfig {
   private static final String PHOTON_MOD_ID = "photon";
   private static final String AAA_PARTICLES_MOD_ID = "aaa_particles";
   private static final Map<AnnoyingVillagersClientConfig.VfxEffect, ConfigValue<String>> VFX_VALUES = new EnumMap<>(
      AnnoyingVillagersClientConfig.VfxEffect.class
   );
   public static final ForgeConfigSpec SPEC;

   private AnnoyingVillagersClientConfig() {
   }

   public static AnnoyingVillagersClientConfig.VfxMode getMode(AnnoyingVillagersClientConfig.VfxEffect effect) {
      ConfigValue<String> value = VFX_VALUES.get(effect);
      if (value == null) {
         return AnnoyingVillagersClientConfig.VfxMode.DEFAULT;
      } else {
         AnnoyingVillagersClientConfig.VfxMode mode = parseMode(value.get(), effect.supportsAaa());
         return mode == null ? AnnoyingVillagersClientConfig.VfxMode.DEFAULT : mode;
      }
   }

   public static boolean isAaaParticlesLoaded() {
      return ModList.get().isLoaded("aaa_particles");
   }

   public static boolean isPhotonModLoaded() {
      return ModList.get().isLoaded("photon");
   }

   public static boolean shouldUseAaaParticles(AnnoyingVillagersClientConfig.VfxEffect effect) {
      return effect != null && effect.supportsAaa() && isAaaParticlesLoaded() ? getMode(effect) != AnnoyingVillagersClientConfig.VfxMode.VANILLA : false;
   }

   public static boolean shouldUsePhotonWhenAvailable(AnnoyingVillagersClientConfig.VfxEffect effect) {
      if (!isPhotonModLoaded()) {
         return false;
      } else {
         AnnoyingVillagersClientConfig.VfxMode mode = getMode(effect);
         if (mode == AnnoyingVillagersClientConfig.VfxMode.VANILLA) {
            return false;
         } else {
            return mode != AnnoyingVillagersClientConfig.VfxMode.AAA_PARTICLE ? true : !effect.supportsAaa() || !isAaaParticlesLoaded();
         }
      }
   }

   private static boolean isValidMode(Object rawValue, boolean supportsAaa) {
      return parseMode(rawValue, supportsAaa) != null;
   }

   private static AnnoyingVillagersClientConfig.VfxMode parseMode(Object rawValue, boolean supportsAaa) {
      if (rawValue instanceof String value) {
         try {
            AnnoyingVillagersClientConfig.VfxMode mode = AnnoyingVillagersClientConfig.VfxMode.valueOf(value.trim().toUpperCase(Locale.ROOT));
            return mode == AnnoyingVillagersClientConfig.VfxMode.AAA_PARTICLE && !supportsAaa ? null : mode;
         } catch (IllegalArgumentException var4) {
            return null;
         }
      } else {
         return null;
      }
   }

   private static String allowedValuesComment(boolean supportsAaa) {
      return supportsAaa ? "[DEFAULT, PHOTON, AAA_PARTICLE, VANILLA]" : "[DEFAULT, PHOTON, VANILLA]";
   }

   static {
      Builder builder = new Builder();
      builder.comment(
            new String[]{
               "Client VFX selection.",
               "DEFAULT keeps the old priority: photon > aaa_particles > vanilla.",
               "If the selected option is unavailable at runtime, the effect falls back to DEFAULT behavior. Example: choosing AAA_PARTICLE without aaa_particles installed will use DEFAULT routing.",
               "All options default to DEFAULT."
            }
         )
         .push("vfx");

      for (AnnoyingVillagersClientConfig.VfxEffect effect : AnnoyingVillagersClientConfig.VfxEffect.values()) {
         VFX_VALUES.put(
            effect,
            builder.comment(new String[]{effect.displayName(), "Allowed values: " + allowedValuesComment(effect.supportsAaa())})
               .define(effect.configKey(), AnnoyingVillagersClientConfig.VfxMode.DEFAULT.name(), value -> isValidMode(value, effect.supportsAaa()))
         );
      }

      builder.pop();
      SPEC = builder.build();
   }

   public static enum VfxEffect {
      GLAIVE_EXPLOSION("glaiveExplosion", "Ender Glaive explosion", true),
      HEROBRINE_PORTAL("herobrinePortal", "Herobrine portal", true),
      HEROBRINE_ASSISTANCE("herobrineAssistance", "Herobrine assistance", true),
      ENDER_AEGIS_SPARK("enderAegisSpark", "Ender Aegis spark", false),
      ELITE_HEROBRINE("spawnEliteEffect", "spawnEliteEffect / Elite Herobrine lightning", false),
      BLUE_DEMON_LIGHTNING("blueDemonLightning", "Blue Demon lightning", false),
      WOOPIE_SWORD_WIND("woopieSwordWind", "Woopie Sword wind", true),
      BLACK_FIRE("blackFire", "Black Fire", true),
      DIAMOND_ATTRACTOR("diamondAttractor", "Diamond Attractor", true),
      TELEPORT_PORTAL("teleportPortal", "Teleport Portal (Photon: snakeportal)", true),
      DRAGON_BEAM("dragonBeam", "Herobrine Dragon beam", true),
      DRAGON_BEAM_HIT("dragonBeamHit", "Herobrine Dragon beam hit", true),
      BLUE_DEMON_THUNDER_BEAM("blueDemonThunderBeam", "Blue Demon thunder beam", true);

      private final String configKey;
      private final String displayName;
      private final boolean supportsAaa;

      private VfxEffect(String configKey, String displayName, boolean supportsAaa) {
         this.configKey = configKey;
         this.displayName = displayName;
         this.supportsAaa = supportsAaa;
      }

      public String configKey() {
         return this.configKey;
      }

      public String displayName() {
         return this.displayName;
      }

      public boolean supportsAaa() {
         return this.supportsAaa;
      }
   }

   public static enum VfxMode {
      DEFAULT,
      PHOTON,
      AAA_PARTICLE,
      VANILLA;
   }
}
