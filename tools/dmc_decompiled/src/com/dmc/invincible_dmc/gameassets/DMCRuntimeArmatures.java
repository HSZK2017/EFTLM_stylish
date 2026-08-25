package com.dmc.invincible_dmc.gameassets;

import com.dmc.invincible_dmc.api.animation.RuntimeArmatureProfile;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.gameasset.Armatures;

public final class DMCRuntimeArmatures {
   public static final ResourceLocation BIPED_ID = ResourceLocation.fromNamespaceAndPath("invincible_dmc", "biped");
   private static final Map<ResourceLocation, RuntimeArmatureProfile> PROFILES = new LinkedHashMap<>();
   public static final RuntimeArmatureProfile BIPED = register(new RuntimeArmatureProfile(BIPED_ID, Armatures.BIPED));

   private DMCRuntimeArmatures() {
   }

   public static synchronized RuntimeArmatureProfile register(RuntimeArmatureProfile profile) {
      RuntimeArmatureProfile previous = PROFILES.putIfAbsent(profile.id(), profile);
      if (previous != null) {
         throw new IllegalStateException("Duplicate runtime armature profile: " + profile.id());
      } else {
         return profile;
      }
   }

   public static Optional<RuntimeArmatureProfile> get(ResourceLocation id) {
      return Optional.ofNullable(PROFILES.get(id));
   }

   public static boolean contains(ResourceLocation id) {
      return PROFILES.containsKey(id);
   }

   public static Map<ResourceLocation, RuntimeArmatureProfile> profiles() {
      return Collections.unmodifiableMap(PROFILES);
   }
}
