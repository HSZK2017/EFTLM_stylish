package com.dmc.invincible_dmc.client.compat.aaap;

import com.dmc.invincible_dmc.client.config.AAAPPerformanceClientConfig;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter.Type;

public final class AAAParticleSimulationController {
   private static final float MAX_CATCH_UP_FRAMES = 3.0F;
   private static final float[] ACCUMULATED_FRAMES = new float[Type.values().length];
   private static final float[] UPDATE_DELTAS = new float[Type.values().length];
   private static final boolean[] UPDATE_THIS_DRAW = new boolean[Type.values().length];
   private static final ThreadLocal<Type> CURRENT_TYPE = new ThreadLocal<>();

   private AAAParticleSimulationController() {
   }

   public static void prepare(Type type, float deltaFrames) {
      CURRENT_TYPE.set(type);
      int index = type.ordinal();
      if (!AAAPPerformanceClientConfig.isEnabled(AAAPPerformanceClientConfig.LIMIT_SIMULATION_RATE)) {
         ACCUMULATED_FRAMES[index] = 0.0F;
         UPDATE_DELTAS[index] = deltaFrames;
         UPDATE_THIS_DRAW[index] = true;
      } else {
         float stepFrames = 60.0F / (float)((Integer)AAAPPerformanceClientConfig.SIMULATION_RATE_LIMIT_HZ.get()).intValue();
         float accumulator = Math.min(3.0F, ACCUMULATED_FRAMES[index] + Math.max(0.0F, deltaFrames));
         int completedSteps = (int)Math.floor((double)((accumulator + 1.0E-5F) / stepFrames));
         if (completedSteps <= 0) {
            ACCUMULATED_FRAMES[index] = accumulator;
            UPDATE_DELTAS[index] = 0.0F;
            UPDATE_THIS_DRAW[index] = false;
         } else {
            float updateDelta = Math.min(3.0F, (float)completedSteps * stepFrames);
            ACCUMULATED_FRAMES[index] = Math.max(0.0F, accumulator - updateDelta);
            UPDATE_DELTAS[index] = updateDelta;
            UPDATE_THIS_DRAW[index] = true;
         }
      }
   }

   public static void clear() {
      CURRENT_TYPE.remove();
   }

   public static boolean hasActiveContext() {
      return CURRENT_TYPE.get() != null;
   }

   public static Type currentType() {
      return CURRENT_TYPE.get();
   }

   public static boolean shouldUpdate(Type type) {
      return UPDATE_THIS_DRAW[type.ordinal()];
   }

   public static float updateDelta(Type type) {
      return UPDATE_DELTAS[type.ordinal()];
   }
}
