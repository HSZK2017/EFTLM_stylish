package com.dmc.invincible_dmc.client.renderer;

import com.dmc.invincible_dmc.gameassets.DMCRuntimeArmatures;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;

public final class RuntimePlayerMeshProfiles {
   private static final Map<ResourceLocation, Function<AbstractClientPlayerPatch<AbstractClientPlayer>, AssetAccessor<HumanoidMesh>>> PROVIDERS = new LinkedHashMap<>();

   private RuntimePlayerMeshProfiles() {
   }

   public static synchronized void register(
      ResourceLocation profileId, Function<AbstractClientPlayerPatch<AbstractClientPlayer>, AssetAccessor<HumanoidMesh>> provider
   ) {
      if (!DMCRuntimeArmatures.contains(profileId)) {
         throw new IllegalArgumentException("No runtime armature profile registered for mesh profile " + profileId);
      } else {
         Function<AbstractClientPlayerPatch<AbstractClientPlayer>, AssetAccessor<HumanoidMesh>> previous = PROVIDERS.putIfAbsent(profileId, provider);
         if (previous != null) {
            throw new IllegalStateException("Duplicate runtime player mesh profile: " + profileId);
         }
      }
   }

   public static Optional<AssetAccessor<HumanoidMesh>> resolve(ResourceLocation profileId, AbstractClientPlayerPatch<AbstractClientPlayer> patch) {
      Function<AbstractClientPlayerPatch<AbstractClientPlayer>, AssetAccessor<HumanoidMesh>> provider = PROVIDERS.get(profileId);
      return provider == null ? Optional.empty() : Optional.ofNullable(provider.apply(patch));
   }
}
