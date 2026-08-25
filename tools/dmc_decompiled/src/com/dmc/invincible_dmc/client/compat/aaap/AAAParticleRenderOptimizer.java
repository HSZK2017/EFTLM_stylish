package com.dmc.invincible_dmc.client.compat.aaap;

import java.util.Collection;
import java.util.Map.Entry;
import mod.chloeprime.aaaparticles.api.client.EffectDefinition;
import mod.chloeprime.aaaparticles.api.client.EffectHolder;
import mod.chloeprime.aaaparticles.api.client.EffectRegistry;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter.Type;
import net.minecraft.resources.ResourceLocation;

public final class AAAParticleRenderOptimizer {
   private AAAParticleRenderOptimizer() {
   }

   public static boolean shouldRender(Type type) {
      Collection<Entry<ResourceLocation, EffectHolder>> entries = EffectRegistry.entries();
      return hasEmitters(entries, type);
   }

   public static boolean hasHandEmitters() {
      Collection<Entry<ResourceLocation, EffectHolder>> entries = EffectRegistry.entries();
      return hasEmitters(entries, Type.FIRST_PERSON_MAINHAND) || hasEmitters(entries, Type.FIRST_PERSON_OFFHAND);
   }

   public static int countEmitters(Type type) {
      int count = 0;

      for (Entry<ResourceLocation, EffectHolder> entry : EffectRegistry.entries()) {
         EffectDefinition definition = (EffectDefinition)entry.getValue().lazyGet().orElse(null);
         if (definition != null) {
            count += ((AAAPEffectDefinitionAccess)definition).invincibleDmc$getEmitterCount(type);
         }
      }

      return count;
   }

   public static EffectDefinition findLoadedDefinition() {
      for (Entry<ResourceLocation, EffectHolder> entry : EffectRegistry.entries()) {
         EffectDefinition definition = (EffectDefinition)entry.getValue().lazyGet().orElse(null);
         if (definition != null) {
            return definition;
         }
      }

      return null;
   }

   private static boolean hasEmitters(Collection<Entry<ResourceLocation, EffectHolder>> entries, Type type) {
      for (Entry<ResourceLocation, EffectHolder> entry : entries) {
         EffectDefinition definition = (EffectDefinition)entry.getValue().lazyGet().orElse(null);
         if (definition != null && ((AAAPEffectDefinitionAccess)definition).invincibleDmc$hasEmitters(type)) {
            return true;
         }
      }

      return false;
   }
}
