package com.dmc.invincible_dmc.client.compat.aaap;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import mod.chloeprime.aaaparticles.api.client.EffectDefinition;
import mod.chloeprime.aaaparticles.api.client.EffectHolder;
import mod.chloeprime.aaaparticles.api.client.EffectRegistry;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter.Type;
import net.minecraft.resources.ResourceLocation;

public final class AAAParticleRenderRequirements {
   private static final String MOD_NAMESPACE = "invincible_dmc";
   private static final AAAParticleRenderRequirements.Requirements SAFE_DEFAULT = new AAAParticleRenderRequirements.Requirements(true, true);
   private static final AAAParticleRenderRequirements.Requirements INVINCIBLE_DEFAULT = new AAAParticleRenderRequirements.Requirements(true, false);
   private static final Map<ResourceLocation, AAAParticleRenderRequirements.Requirements> OVERRIDES = new ConcurrentHashMap<>();
   private static final ThreadLocal<AAAParticleRenderRequirements.ContextRequirements> CURRENT_CONTEXT = new ThreadLocal<>();

   private AAAParticleRenderRequirements() {
   }

   public static void register(ResourceLocation effectId, boolean requiresDepth, boolean requiresBackground) {
      OVERRIDES.put(effectId, new AAAParticleRenderRequirements.Requirements(requiresDepth, requiresBackground));
   }

   public static void beginContext(Type type) {
      boolean requiresDepth = false;
      boolean requiresBackground = false;

      for (Entry<ResourceLocation, EffectHolder> entry : EffectRegistry.entries()) {
         EffectDefinition definition = (EffectDefinition)entry.getValue().lazyGet().orElse(null);
         if (definition != null && ((AAAPEffectDefinitionAccess)definition).invincibleDmc$hasEmitters(type)) {
            AAAParticleRenderRequirements.Requirements requirements = resolve(entry.getKey());
            requiresDepth |= requirements.requiresDepth();
            requiresBackground |= requirements.requiresBackground();
            if (requiresDepth && requiresBackground) {
               break;
            }
         }
      }

      CURRENT_CONTEXT.set(new AAAParticleRenderRequirements.ContextRequirements(requiresDepth, requiresBackground));
   }

   public static void endContext() {
      CURRENT_CONTEXT.remove();
   }

   private static AAAParticleRenderRequirements.Requirements resolve(ResourceLocation effectId) {
      AAAParticleRenderRequirements.Requirements override = OVERRIDES.get(effectId);
      if (override != null) {
         return override;
      } else if (!"invincible_dmc".equals(effectId.m_135827_())) {
         return SAFE_DEFAULT;
      } else {
         String path = effectId.m_135815_();
         return !path.contains("disorder") && !path.contains("distortion") && !path.equals("demonic_domain") ? INVINCIBLE_DEFAULT : SAFE_DEFAULT;
      }
   }

   private static record ContextRequirements(boolean requiresDepth, boolean requiresBackground) {
   }

   private static record Requirements(boolean requiresDepth, boolean requiresBackground) {
   }
}
