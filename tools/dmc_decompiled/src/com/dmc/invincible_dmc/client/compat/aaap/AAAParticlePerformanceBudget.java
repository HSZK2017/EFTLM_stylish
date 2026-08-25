package com.dmc.invincible_dmc.client.compat.aaap;

import com.dmc.invincible_dmc.client.config.AAAPPerformanceClientConfig;
import com.dmc.invincible_dmc.client.effeks.JudgementCutEffectBudget;
import com.mojang.logging.LogUtils;
import java.util.concurrent.atomic.AtomicInteger;
import mod.chloeprime.aaaparticles.api.client.EffectDefinition;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter.Type;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public final class AAAParticlePerformanceBudget {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final long STATISTICS_INTERVAL_NANOS = 5000000000L;
   private static final double BUDGET_REFERENCE_HZ = 60.0;
   private static final long MAX_REFILL_GAP_NANOS = 5000000000L;
   private static final int TYPE_COUNT = Type.values().length;
   private static final ResourceLocation SPARK_EFFECT = ResourceLocation.fromNamespaceAndPath("invincible_dmc", "spark");
   private static final ResourceLocation SDT_SPARK_EFFECT = ResourceLocation.fromNamespaceAndPath("invincible_dmc", "sdt_spark");
   private static final AtomicInteger COLLISION_RAYCASTS = new AtomicInteger();
   private static final AtomicInteger COLLISION_TOKENS = new AtomicInteger();
   private static final int[] frameEmitterBaselines = new int[TYPE_COUNT];
   private static final int[] frameInstanceBaselines = new int[TYPE_COUNT];
   private static final int[] acceptedEmitterStarts = new int[TYPE_COUNT];
   private static final int[] reservedInstances = new int[TYPE_COUNT];
   private static final int[] peakEmitters = new int[TYPE_COUNT];
   private static final int[] peakInstances = new int[TYPE_COUNT];
   private static final double[] emitterStartTokens = new double[TYPE_COUNT];
   private static final double[] newInstanceTokens = new double[TYPE_COUNT];
   private static int previousFrameCollisionRaycasts;
   private static int rejectedByEmitterLimit;
   private static int rejectedByInstanceLimit;
   private static int rejectedByJudgementCutLimit;
   private static int rejectedBySparkLimit;
   private static int rejectedByFrameBudget;
   private static int acceptedSparkStarts;
   private static double sparkStartTokens;
   private static double collisionTokenRemainder;
   private static long lastBudgetRefillNanos;
   private static boolean timeBudgetsInitialized;
   private static long lastStatisticsNanos;

   private AAAParticlePerformanceBudget() {
   }

   public static void beginWorldFrame() {
      refillTimeBudgets(System.nanoTime());
      previousFrameCollisionRaycasts = COLLISION_RAYCASTS.getAndSet(0);
      acceptedSparkStarts = 0;
      if (isFrameTrackingRequired()) {
         EffectDefinition definition = AAAParticleRenderOptimizer.findLoadedDefinition();

         for (Type type : Type.values()) {
            int index = type.ordinal();
            int currentEmitters = AAAParticleRenderOptimizer.countEmitters(type);
            int currentInstances = getCurrentInstances(definition, type);
            peakEmitters[index] = Math.max(peakEmitters[index], currentEmitters);
            peakInstances[index] = Math.max(peakInstances[index], currentInstances);
            frameEmitterBaselines[index] = currentEmitters;
            frameInstanceBaselines[index] = currentInstances;
            acceptedEmitterStarts[index] = 0;
            reservedInstances[index] = 0;
         }

         logStatisticsIfNeeded(definition);
      }
   }

   public static boolean allowCollisionRaycast() {
      if (!AAAPPerformanceClientConfig.isEnabled(AAAPPerformanceClientConfig.LIMIT_COLLISION_RAYCASTS)) {
         return true;
      } else {
         ensureTimeBudgetsInitialized();
         int limit = (Integer)AAAPPerformanceClientConfig.COLLISION_RAYCASTS_PER_FRAME.get();
         if (!useTimeNormalizedBudgets()) {
            return COLLISION_RAYCASTS.getAndIncrement() < limit;
         } else {
            COLLISION_RAYCASTS.incrementAndGet();

            int available;
            do {
               available = COLLISION_TOKENS.get();
               if (available <= 0) {
                  return false;
               }
            } while (!COLLISION_TOKENS.compareAndSet(available, available - 1));

            return true;
         }
      }
   }

   public static boolean shouldRejectOneShot(EffectDefinition definition, Type type) {
      if (!(Boolean)AAAPPerformanceClientConfig.ENABLED.get()) {
         return false;
      } else {
         ensureTimeBudgetsInitialized();
         int index = type.ordinal();
         boolean jceLimitActive = isJudgementCutLimitActive(type);
         boolean sparkEffect = isLimitedSparkEffect(definition, type);
         boolean emitterLimitEnabled = (Boolean)AAAPPerformanceClientConfig.LIMIT_EMITTER_COUNT.get() || jceLimitActive;
         boolean instanceLimitEnabled = (Boolean)AAAPPerformanceClientConfig.LIMIT_INSTANCE_COUNT.get() || jceLimitActive;
         boolean frameLimitEnabled = (Boolean)AAAPPerformanceClientConfig.ENABLE_SOFT_BUDGETS.get()
            && (Boolean)AAAPPerformanceClientConfig.LIMIT_FRAME_ADDITIONS.get();
         if (!emitterLimitEnabled && !instanceLimitEnabled && !sparkEffect && !frameLimitEnabled) {
            return false;
         } else if (sparkEffect && exceedsSparkLimit(definition, type)) {
            rejectedBySparkLimit++;
            return true;
         } else {
            int currentEmitters = AAAParticleRenderOptimizer.countEmitters(type);
            int projectedEmitters = Math.max(currentEmitters, frameEmitterBaselines[index] + acceptedEmitterStarts[index]);
            int emitterLimit = getEmitterLimit(type);
            if (emitterLimitEnabled && projectedEmitters >= emitterLimit) {
               if (jceLimitActive) {
                  rejectedByJudgementCutLimit++;
               } else {
                  rejectedByEmitterLimit++;
               }

               return true;
            } else {
               int estimatedInstancesPerEmitter = getEstimatedInstancesPerEmitter(type);
               int currentInstances = definition.getManager(type).getImpl().GetTotalInstanceCount();
               int observedFrameInstances = Math.max(0, currentInstances - frameInstanceBaselines[index]);
               int estimatedFrameInstances = Math.max(observedFrameInstances, reservedInstances[index]);
               int reservation = AAAPPerformanceClientConfig.RESERVE_BURST_INSTANCES.get() ? estimatedInstancesPerEmitter : 0;
               int projectedInstancesBeforeNew = Math.max(
                  currentInstances, frameInstanceBaselines[index] + (AAAPPerformanceClientConfig.RESERVE_BURST_INSTANCES.get() ? reservedInstances[index] : 0)
               );
               long projectedInstancesWithNew = (long)projectedInstancesBeforeNew + (long)reservation;
               if (instanceLimitEnabled) {
                  int instanceLimit = getInstanceLimit(type);
                  boolean exceedsLimit = reservation > 0 ? projectedInstancesWithNew > (long)instanceLimit : projectedInstancesBeforeNew >= instanceLimit;
                  if (exceedsLimit) {
                     rejectedByInstanceLimit++;
                     return true;
                  }
               }

               boolean softBudgetActive = frameLimitEnabled
                  && exceedsSoftLimit(
                     type,
                     projectedEmitters + 1,
                     (int)Math.min(
                        2147483647L,
                        Math.max(
                           (long)currentInstances + (long)estimatedInstancesPerEmitter,
                           (long)frameInstanceBaselines[index] + (long)reservedInstances[index] + (long)estimatedInstancesPerEmitter
                        )
                     )
                  );
               if (softBudgetActive) {
                  int emitterStartsPerFrame = type == Type.WORLD
                     ? (Integer)AAAPPerformanceClientConfig.MAX_WORLD_EMITTER_STARTS_PER_FRAME.get()
                     : (Integer)AAAPPerformanceClientConfig.MAX_HAND_EMITTER_STARTS_PER_FRAME.get();
                  int newInstancesPerFrame = type == Type.WORLD
                     ? (Integer)AAAPPerformanceClientConfig.MAX_WORLD_NEW_INSTANCES_PER_FRAME.get()
                     : (Integer)AAAPPerformanceClientConfig.MAX_HAND_NEW_INSTANCES_PER_FRAME.get();
                  boolean exceedsBurstBudget = useTimeNormalizedBudgets()
                     ? emitterStartTokens[index] < 1.0 || newInstanceTokens[index] + 1.0E-6 < (double)estimatedInstancesPerEmitter
                     : acceptedEmitterStarts[index] >= emitterStartsPerFrame
                        || (long)estimatedFrameInstances + (long)estimatedInstancesPerEmitter > (long)newInstancesPerFrame;
                  if (exceedsBurstBudget) {
                     rejectedByFrameBudget++;
                     return true;
                  }
               }

               acceptedEmitterStarts[index]++;
               reservedInstances[index] = reservedInstances[index] + estimatedInstancesPerEmitter;
               if (softBudgetActive && useTimeNormalizedBudgets()) {
                  emitterStartTokens[index] = Math.max(0.0, emitterStartTokens[index] - 1.0);
                  newInstanceTokens[index] = Math.max(0.0, newInstanceTokens[index] - (double)estimatedInstancesPerEmitter);
               }

               if (sparkEffect) {
                  if (useTimeNormalizedBudgets()) {
                     sparkStartTokens = Math.max(0.0, sparkStartTokens - 1.0);
                  } else {
                     acceptedSparkStarts++;
                  }
               }

               return false;
            }
         }
      }
   }

   private static boolean isLimitedSparkEffect(EffectDefinition definition, Type type) {
      if (type == Type.WORLD && (Boolean)AAAPPerformanceClientConfig.LIMIT_SPARK_EFFECTS.get() && definition != null) {
         ResourceLocation effectId = definition.getId();
         return SPARK_EFFECT.equals(effectId) || SDT_SPARK_EFFECT.equals(effectId);
      } else {
         return false;
      }
   }

   private static boolean exceedsSparkLimit(EffectDefinition definition, Type type) {
      ResourceLocation effectId = definition.getId();
      int emitterLimit = SPARK_EFFECT.equals(effectId)
         ? (Integer)AAAPPerformanceClientConfig.MAX_SPARK_EMITTERS.get()
         : (Integer)AAAPPerformanceClientConfig.MAX_SDT_SPARK_EMITTERS.get();
      int currentEmitters = ((AAAPEffectDefinitionAccess)definition).invincibleDmc$getEmitterCount(type);
      return currentEmitters >= emitterLimit
         || (useTimeNormalizedBudgets() ? sparkStartTokens < 1.0 : acceptedSparkStarts >= (Integer)AAAPPerformanceClientConfig.MAX_SPARK_STARTS_PER_FRAME.get());
   }

   private static void refillTimeBudgets(long now) {
      if (!useTimeNormalizedBudgets()) {
         timeBudgetsInitialized = false;
         lastBudgetRefillNanos = now;
      } else if (timeBudgetsInitialized && lastBudgetRefillNanos != 0L && now >= lastBudgetRefillNanos && now - lastBudgetRefillNanos <= 5000000000L) {
         double elapsedSeconds = (double)(now - lastBudgetRefillNanos) / 1.0E9;
         lastBudgetRefillNanos = now;

         for (Type type : Type.values()) {
            int index = type.ordinal();
            int emitterCapacity = getEmitterStartCapacity(type);
            int instanceCapacity = getNewInstanceCapacity(type);
            emitterStartTokens[index] = Math.min((double)emitterCapacity, emitterStartTokens[index] + elapsedSeconds * 60.0 * (double)emitterCapacity);
            newInstanceTokens[index] = Math.min((double)instanceCapacity, newInstanceTokens[index] + elapsedSeconds * 60.0 * (double)instanceCapacity);
         }

         int sparkCapacity = (Integer)AAAPPerformanceClientConfig.MAX_SPARK_STARTS_PER_FRAME.get();
         sparkStartTokens = Math.min((double)sparkCapacity, sparkStartTokens + elapsedSeconds * 60.0 * (double)sparkCapacity);
         int collisionCapacity = (Integer)AAAPPerformanceClientConfig.COLLISION_RAYCASTS_PER_FRAME.get();
         double collisionRefill = elapsedSeconds * 60.0 * (double)collisionCapacity + collisionTokenRemainder;
         int wholeCollisionTokens = (int)Math.floor(collisionRefill);
         collisionTokenRemainder = collisionRefill - (double)wholeCollisionTokens;
         if (wholeCollisionTokens > 0) {
            COLLISION_TOKENS.getAndUpdate(current -> Math.min(collisionCapacity, current + wholeCollisionTokens));
         } else if (COLLISION_TOKENS.get() > collisionCapacity) {
            COLLISION_TOKENS.set(collisionCapacity);
         }
      } else {
         resetTimeBudgets();
         lastBudgetRefillNanos = now;
         timeBudgetsInitialized = true;
      }
   }

   private static void ensureTimeBudgetsInitialized() {
      if (useTimeNormalizedBudgets() && !timeBudgetsInitialized) {
         refillTimeBudgets(System.nanoTime());
      }
   }

   private static void resetTimeBudgets() {
      for (Type type : Type.values()) {
         int index = type.ordinal();
         emitterStartTokens[index] = (double)getEmitterStartCapacity(type);
         newInstanceTokens[index] = (double)getNewInstanceCapacity(type);
      }

      sparkStartTokens = (double)((Integer)AAAPPerformanceClientConfig.MAX_SPARK_STARTS_PER_FRAME.get()).intValue();
      COLLISION_TOKENS.set((Integer)AAAPPerformanceClientConfig.COLLISION_RAYCASTS_PER_FRAME.get());
      collisionTokenRemainder = 0.0;
   }

   private static int getEmitterStartCapacity(Type type) {
      return type == Type.WORLD
         ? (Integer)AAAPPerformanceClientConfig.MAX_WORLD_EMITTER_STARTS_PER_FRAME.get()
         : (Integer)AAAPPerformanceClientConfig.MAX_HAND_EMITTER_STARTS_PER_FRAME.get();
   }

   private static int getNewInstanceCapacity(Type type) {
      return type == Type.WORLD
         ? (Integer)AAAPPerformanceClientConfig.MAX_WORLD_NEW_INSTANCES_PER_FRAME.get()
         : (Integer)AAAPPerformanceClientConfig.MAX_HAND_NEW_INSTANCES_PER_FRAME.get();
   }

   private static boolean useTimeNormalizedBudgets() {
      return AAAPPerformanceClientConfig.isEnabled(AAAPPerformanceClientConfig.NORMALIZE_TIME_BUDGETS);
   }

   private static int getEmitterLimit(Type type) {
      int limit = AAAPPerformanceClientConfig.LIMIT_EMITTER_COUNT.get()
         ? type == Type.WORLD ? (Integer)AAAPPerformanceClientConfig.MAX_WORLD_EMITTERS.get() : (Integer)AAAPPerformanceClientConfig.MAX_HAND_EMITTERS.get()
         : Integer.MAX_VALUE;
      if (isJudgementCutLimitActive(type)) {
         limit = Math.min(limit, (Integer)AAAPPerformanceClientConfig.MAX_JCE_WORLD_EMITTERS.get());
      }

      return limit;
   }

   private static int getInstanceLimit(Type type) {
      int limit = type == Type.WORLD
         ? (Integer)AAAPPerformanceClientConfig.MAX_WORLD_INSTANCES.get()
         : (Integer)AAAPPerformanceClientConfig.MAX_HAND_INSTANCES.get();
      if (isJudgementCutLimitActive(type)) {
         limit = Math.min(limit, (Integer)AAAPPerformanceClientConfig.MAX_JCE_WORLD_INSTANCES.get());
      }

      return limit;
   }

   private static int getEstimatedInstancesPerEmitter(Type type) {
      return type == Type.WORLD
         ? (Integer)AAAPPerformanceClientConfig.WORLD_INSTANCE_RESERVATION_PER_EMITTER.get()
         : (Integer)AAAPPerformanceClientConfig.HAND_INSTANCE_RESERVATION_PER_EMITTER.get();
   }

   private static boolean exceedsSoftLimit(Type type, int projectedEmitters, int projectedInstances) {
      int emitterLimit = type == Type.WORLD
         ? (Integer)AAAPPerformanceClientConfig.SOFT_WORLD_EMITTERS.get()
         : (Integer)AAAPPerformanceClientConfig.SOFT_HAND_EMITTERS.get();
      int instanceLimit = type == Type.WORLD
         ? (Integer)AAAPPerformanceClientConfig.SOFT_WORLD_INSTANCES.get()
         : (Integer)AAAPPerformanceClientConfig.SOFT_HAND_INSTANCES.get();
      if (isJudgementCutLimitActive(type)) {
         emitterLimit = Math.min(emitterLimit, (Integer)AAAPPerformanceClientConfig.SOFT_JCE_WORLD_EMITTERS.get());
         instanceLimit = Math.min(instanceLimit, (Integer)AAAPPerformanceClientConfig.SOFT_JCE_WORLD_INSTANCES.get());
      }

      return projectedEmitters > emitterLimit || projectedInstances > instanceLimit;
   }

   private static boolean isJudgementCutLimitActive(Type type) {
      return type == Type.WORLD && (Boolean)AAAPPerformanceClientConfig.LIMIT_JCE_BURSTS.get() && JudgementCutEffectBudget.isBurstActive();
   }

   private static boolean isFrameTrackingRequired() {
      return (Boolean)AAAPPerformanceClientConfig.LOG_STATISTICS.get()
         || (Boolean)AAAPPerformanceClientConfig.ENABLED.get()
            && (
               (Boolean)AAAPPerformanceClientConfig.LIMIT_EMITTER_COUNT.get()
                  || (Boolean)AAAPPerformanceClientConfig.LIMIT_INSTANCE_COUNT.get()
                  || (Boolean)AAAPPerformanceClientConfig.LIMIT_JCE_BURSTS.get()
                  || (Boolean)AAAPPerformanceClientConfig.LIMIT_SPARK_EFFECTS.get()
                  || (Boolean)AAAPPerformanceClientConfig.ENABLE_SOFT_BUDGETS.get() && (Boolean)AAAPPerformanceClientConfig.LIMIT_FRAME_ADDITIONS.get()
            );
   }

   private static int getCurrentInstances(EffectDefinition definition, Type type) {
      return definition == null ? 0 : definition.getManager(type).getImpl().GetTotalInstanceCount();
   }

   private static void logStatisticsIfNeeded(EffectDefinition definition) {
      if ((Boolean)AAAPPerformanceClientConfig.LOG_STATISTICS.get()) {
         long now = System.nanoTime();
         if (now - lastStatisticsNanos >= 5000000000L) {
            lastStatisticsNanos = now;
            Type world = Type.WORLD;
            Type mainHand = Type.FIRST_PERSON_MAINHAND;
            Type offHand = Type.FIRST_PERSON_OFFHAND;
            int worldEmitters = AAAParticleRenderOptimizer.countEmitters(world);
            int mainHandEmitters = AAAParticleRenderOptimizer.countEmitters(mainHand);
            int offHandEmitters = AAAParticleRenderOptimizer.countEmitters(offHand);
            int worldInstances = getCurrentInstances(definition, world);
            int mainHandInstances = getCurrentInstances(definition, mainHand);
            int offHandInstances = getCurrentInstances(definition, offHand);
            LOGGER.info(
               "[AAAP_PERF] emitters={}/{}/{} peakEmitters={}/{}/{} instances={}/{}/{} peakInstances={}/{}/{} collisionRaycasts={} rejectedEmitter={} rejectedInstances={} rejectedJce={} rejectedSpark={} rejectedFrame={} jceBurst={}",
               new Object[]{
                  worldEmitters,
                  mainHandEmitters,
                  offHandEmitters,
                  peakEmitters[world.ordinal()],
                  peakEmitters[mainHand.ordinal()],
                  peakEmitters[offHand.ordinal()],
                  worldInstances,
                  mainHandInstances,
                  offHandInstances,
                  peakInstances[world.ordinal()],
                  peakInstances[mainHand.ordinal()],
                  peakInstances[offHand.ordinal()],
                  previousFrameCollisionRaycasts,
                  rejectedByEmitterLimit,
                  rejectedByInstanceLimit,
                  rejectedByJudgementCutLimit,
                  rejectedBySparkLimit,
                  rejectedByFrameBudget,
                  JudgementCutEffectBudget.isBurstActive()
               }
            );
            rejectedByEmitterLimit = 0;
            rejectedByInstanceLimit = 0;
            rejectedByJudgementCutLimit = 0;
            rejectedBySparkLimit = 0;
            rejectedByFrameBudget = 0;

            for (int index = 0; index < TYPE_COUNT; index++) {
               peakEmitters[index] = 0;
               peakInstances[index] = 0;
            }
         }
      }
   }
}
