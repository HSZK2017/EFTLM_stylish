package com.dmc.invincible_dmc.api.collider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@FunctionalInterface
public interface ColliderProvider {
   Map<AnimationAccessor<?>, ColliderProvider> REGISTRY = new ConcurrentHashMap<>();

   @Nullable
   Collider resolve(LivingEntityPatch<?> var1, AttackAnimation var2, Phase var3, Joint var4, float var5);

   static void register(AnimationAccessor<?> accessor, ColliderProvider provider) {
      REGISTRY.put(accessor, provider);
   }

   static void unregister(AnimationAccessor<?> accessor) {
      REGISTRY.remove(accessor);
   }

   @Nullable
   static ColliderProvider get(AnimationAccessor<?> accessor) {
      return REGISTRY.get(accessor);
   }

   static void clearAll() {
      REGISTRY.clear();
   }

   @Nullable
   static ColliderProvider resolveFrom(DynamicAnimation animation) {
      AnimationAccessor<?> accessor = animation.getAccessor();
      if (accessor != null) {
         ColliderProvider provider = REGISTRY.get(accessor);
         if (provider != null) {
            return provider;
         }
      }

      return animation instanceof ColliderProvider.ProviderHolder holder ? holder.invincible_dmc$getColliderProvider() : null;
   }

   public interface ProviderHolder {
      @Nullable
      ColliderProvider invincible_dmc$getColliderProvider();
   }
}
