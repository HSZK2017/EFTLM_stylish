package com.dmc.invincible_dmc.client.input;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.api.skill.ComboType;
import com.dmc.invincible_dmc.client.input.judegementCut.ClientJudgementCutController;
import com.dmc.invincible_dmc.conditions.LongPressCondition;
import com.dmc.invincible_dmc.utils.DMCLog;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import yesman.epicfight.data.conditions.Condition;

public final class ComboIntentResolver {
   static long lastAnyInputTimestamp;
   static long lastPressIntervalMs;
   private static final Set<ComboType> FIRED_COMPOSITES_THIS_WINDOW = new HashSet<>();
   private static final int COMPOSITE_WINDOW_TICKS = 3;
   private static final List<ComboIntentResolver.ComboInputIntent> carryoverIntents = new ArrayList<>();
   private static final Set<ComboType>[] freshlyPressedHistory = new Set[3];
   private static int freshlyPressedCursor = 0;

   private static boolean isFreshlyPressedInWindow(ComboType type) {
      for (Set<ComboType> set : freshlyPressedHistory) {
         if (set.contains(type)) {
            return true;
         }
      }

      return false;
   }

   private static boolean isKey13Only(List<ComboType> subs) {
      return subs.stream().allMatch(t -> t == ComboNode.ComboTypes.KEY_1 || t == ComboNode.ComboTypes.KEY_3);
   }

   public static List<ComboIntentResolver.ComboInputIntent> detectPressRelease(
      ComboInputSampler.ComboInputState[] inputStates,
      ComboType[] statesToType,
      long currentTick,
      @Nullable ComboNode currentNode,
      DirectionTracker directionTracker,
      boolean isReserved,
      boolean suppressKey13Composite
   ) {
      long now = InputClock.nowMillis();
      List<ComboIntentResolver.ComboInputIntent> intents = new ArrayList<>();
      Set<ComboType> freshlyPressedTypes = new HashSet<>();
      List<ComboIntentResolver.ComboInputIntent> replayedIntents = new ArrayList<>(carryoverIntents);
      carryoverIntents.clear();

      for (int i = 0; i < inputStates.length; i++) {
         ComboInputSampler.ComboInputState state = inputStates[i];
         if (state.keyMapping != null) {
            ComboType type = statesToType[i];
            List<ComboInputSampler.InputEventRecord> events = new ArrayList<>();

            ComboInputSampler.InputEventRecord event;
            while ((event = state.eventQueue.poll()) != null) {
               events.add(event);
            }

            if (events.isEmpty()) {
               if (state.curDown) {
                  state.pressedTicks++;
                  handleContinuousHold(state, type, now, currentTick, currentNode, directionTracker, intents);
               }
            } else {
               for (ComboInputSampler.InputEventRecord record : events) {
                  boolean prevDown = state.curDown;
                  state.curDown = record.isDown();
                  if (!prevDown && state.curDown) {
                     state.pressedTicks = 1;
                     state.pressStartTimeMs = record.timestampMs();
                     state.cycleExecuted = false;
                     long interval = lastAnyInputTimestamp > 0L ? record.timestampMs() - lastAnyInputTimestamp : Long.MAX_VALUE;
                     lastAnyInputTimestamp = record.timestampMs();
                     lastPressIntervalMs = interval;
                     if (!hasLongPressConflict(currentNode, type)) {
                        state.cycleExecuted = true;
                        freshlyPressedTypes.add(type);
                        DMCLog.info(
                           DMCLog.Category.COMBO_ENGINE, "[Intent] SHORT_PRESS from=EVENT_PRESS type={} interval={} tick={}", type, interval, currentTick
                        );
                        intents.add(createSnapshotIntent(type, ComboIntentResolver.ComboIntentType.SHORT_PRESS, 1, interval, currentTick, directionTracker));
                     }
                  } else if (prevDown && !state.curDown) {
                     int heldTicks = state.pressedTicks;
                     freshlyPressedTypes.remove(type);
                     FIRED_COMPOSITES_THIS_WINDOW.removeIf(ct -> ct.getSubTypes().contains(type));
                     if (!state.cycleExecuted) {
                        DMCLog.info(
                           DMCLog.Category.COMBO_ENGINE,
                           "[Intent] SHORT_PRESS from=EVENT_RELEASE type={} heldTicks={} interval={} tick={}",
                           type,
                           heldTicks,
                           lastPressIntervalMs,
                           currentTick
                        );
                        intents.add(
                           createSnapshotIntent(
                              type, ComboIntentResolver.ComboIntentType.SHORT_PRESS, heldTicks, lastPressIntervalMs, currentTick, directionTracker
                           )
                        );
                     }

                     state.pressedTicks = 0;
                     state.pressStartTimeMs = 0L;
                     state.cycleExecuted = false;
                  }
               }

               if (state.curDown && !state.cycleExecuted) {
                  handleContinuousHold(state, type, now, currentTick, currentNode, directionTracker, intents);
               }
            }
         }
      }

      List<ComboIntentResolver.ComboInputIntent> deferredIntents = new ArrayList<>();

      for (ComboType compositeType : ComboType.ENUM_MANAGER.universalValues()) {
         List<ComboType> subs = compositeType.getSubTypes();
         if (!subs.isEmpty()
            && isKey13Only(subs)
            && (!suppressKey13Composite || compositeType != ComboNode.ComboTypes.KEY_1_3)
            && ComboRoutePlanner.getNextNode(currentNode, compositeType) != null) {
            boolean anySubFresh = subs.stream().anyMatch(sub -> freshlyPressedTypes.contains(sub) || isFreshlyPressedInWindow(sub));
            if (anySubFresh) {
               Iterator<ComboIntentResolver.ComboInputIntent> it = intents.iterator();

               while (it.hasNext()) {
                  ComboIntentResolver.ComboInputIntent intent = it.next();
                  if (subs.contains(intent.type()) && (intent.type() != ComboNode.ComboTypes.KEY_3 || !ClientJudgementCutController.isAnyJCCharging())) {
                     deferredIntents.add(intent);
                     it.remove();
                  }
               }
            }
         }
      }

      for (ComboType compositeTypex : ComboType.ENUM_MANAGER.universalValues()) {
         List<ComboType> subs = compositeTypex.getSubTypes();
         if (!subs.isEmpty() && isKey13Only(subs)) {
            if (suppressKey13Composite && compositeTypex == ComboNode.ComboTypes.KEY_1_3) {
               if (subs.stream().allMatch(ComboInputSampler::isPressed)) {
                  FIRED_COMPOSITES_THIS_WINDOW.add(compositeTypex);
               } else {
                  FIRED_COMPOSITES_THIS_WINDOW.remove(compositeTypex);
               }
            } else if (!subs.stream().allMatch(ComboInputSampler::isPressed)) {
               FIRED_COMPOSITES_THIS_WINDOW.remove(compositeTypex);
            } else {
               boolean anyFreshlyPressed = subs.stream().anyMatch(sub -> freshlyPressedTypes.contains(sub) || isFreshlyPressedInWindow(sub));
               if (anyFreshlyPressed && FIRED_COMPOSITES_THIS_WINDOW.add(compositeTypex)) {
                  int minDuration = subs.stream().mapToInt(sub -> {
                     int idx = ComboInputSampler.findStateIndex(sub);
                     return idx >= 0 ? ComboInputSampler.INPUT_STATES[idx].pressedTicks : 0;
                  }).min().orElse(1);
                  DMCLog.info(
                     DMCLog.Category.COMBO_ENGINE, "[Intent] COMPOSITE type={} subs={} minDuration={} tick={}", compositeTypex, subs, minDuration, currentTick
                  );
                  intents.add(
                     createSnapshotIntent(
                        compositeTypex, ComboIntentResolver.ComboIntentType.SHORT_PRESS, minDuration, lastPressIntervalMs, currentTick, directionTracker
                     )
                  );
               }
            }
         }
      }

      if (!deferredIntents.isEmpty()) {
         Set<ComboType> compositeTypesFormed = new HashSet<>();

         for (ComboIntentResolver.ComboInputIntent intent : intents) {
            if (!intent.type().getSubTypes().isEmpty()) {
               compositeTypesFormed.add(intent.type());
            }
         }

         for (ComboIntentResolver.ComboInputIntent def : deferredIntents) {
            boolean subsumed = false;

            for (ComboType formed : compositeTypesFormed) {
               if (formed.getSubTypes().contains(def.type())) {
                  subsumed = true;
                  break;
               }
            }

            if (!subsumed) {
               carryoverIntents.add(def);
            }
         }
      }

      freshlyPressedHistory[freshlyPressedCursor] = freshlyPressedTypes;
      freshlyPressedCursor = (freshlyPressedCursor + 1) % 3;
      Set<ComboType> formedCompositeTypes = new HashSet<>();

      for (ComboIntentResolver.ComboInputIntent ix : intents) {
         if (!ix.type().getSubTypes().isEmpty()) {
            formedCompositeTypes.add(ix.type());
         }
      }

      for (ComboIntentResolver.ComboInputIntent replayed : replayedIntents) {
         boolean covered = false;

         for (ComboType formedx : formedCompositeTypes) {
            if (formedx.getSubTypes().contains(replayed.type())) {
               covered = true;
               break;
            }
         }

         if (!covered) {
            intents.add(replayed);
         }
      }

      return intents;
   }

   private static void handleContinuousHold(
      ComboInputSampler.ComboInputState state,
      ComboType type,
      long now,
      long currentTick,
      @Nullable ComboNode currentNode,
      DirectionTracker directionTracker,
      List<ComboIntentResolver.ComboInputIntent> intents
   ) {
      if (!state.cycleExecuted) {
         int thresholdTicks = getLongPressThreshold(currentNode, type);
         long thresholdMs = (long)thresholdTicks * 50L;
         long heldMs = state.pressStartTimeMs > 0L ? now - state.pressStartTimeMs : (long)state.pressedTicks * 50L;
         if (heldMs >= thresholdMs) {
            state.cycleExecuted = true;
            DMCLog.info(DMCLog.Category.COMBO_ENGINE, "[Intent] LONG_PRESS from=HOLD_CONTINUE type={} heldMs={} tick={}", type, heldMs, currentTick);
            intents.add(
               createSnapshotIntent(
                  type, ComboIntentResolver.ComboIntentType.LONG_PRESS, state.pressedTicks, lastPressIntervalMs, currentTick, directionTracker
               )
            );
         }
      }
   }

   public static ComboIntentResolver.ComboInputIntent createSnapshotIntent(
      ComboType type, ComboIntentResolver.ComboIntentType intentType, int duration, long interval, long currentTick, DirectionTracker directionTracker
   ) {
      int directionMask = directionTracker.getMatchedSequencesMask(
         (long)((Integer)DMConfig.DIRECTION_SEQUENCE_MATCH_WINDOW.get()).intValue(),
         (long)((Integer)DMConfig.DIRECTION_SEQUENCE_ACTIVATION_WINDOW.get()).intValue(),
         currentTick
      );
      List<DirectionTracker.DirectionEvent> directionEvents = directionTracker.exportRecentEvents();
      return new ComboIntentResolver.ComboInputIntent(type, intentType, duration, interval, directionMask, directionEvents, currentTick);
   }

   static int getLongPressThreshold(@Nullable ComboNode currentNode, ComboType comboType) {
      int global = ComboInputSampler.isControllerActive()
         ? (Integer)DMConfig.CONTROLLER_LONG_PRESS_THRESHOLD.get()
         : (Integer)DMConfig.LONG_PRESS_THRESHOLD.get();
      if (currentNode != null) {
         ComboNode next = currentNode.getNext(comboType);
         if (next != null) {
            return next.resolveLongPressThreshold(global);
         }
      }

      return global;
   }

   static boolean hasLongPressConflict(@Nullable ComboNode currentNode, ComboType type) {
      if (currentNode == null) {
         return false;
      } else {
         ComboNode next = currentNode.getNext(type);
         return next == null ? false : next.isAllowLongPress() || containsLongPressCondition(next);
      }
   }

   private static boolean containsLongPressCondition(ComboNode node) {
      return containsLongPressCondition(node, new HashSet<>());
   }

   private static boolean containsLongPressCondition(ComboNode node, Set<ComboNode> visited) {
      if (node != null && visited.add(node)) {
         for (Condition condition : node.getConditions()) {
            if (condition instanceof LongPressCondition) {
               return true;
            }
         }

         for (ComboNode conditionNode : new ArrayList<>(node.getConditionNodes())) {
            for (Condition conditionx : conditionNode.getConditions()) {
               if (conditionx instanceof LongPressCondition) {
                  return true;
               }
            }

            if (containsLongPressCondition(conditionNode, visited)) {
               return true;
            }
         }

         for (ComboNode child : new ArrayList<>(node.getChildren())) {
            if (containsLongPressCondition(child, visited)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   static void resetTimestamps() {
      lastAnyInputTimestamp = 0L;
      lastPressIntervalMs = 0L;
      FIRED_COMPOSITES_THIS_WINDOW.clear();
      carryoverIntents.clear();

      for (Set<ComboType> set : freshlyPressedHistory) {
         set.clear();
      }
   }

   static {
      for (int i = 0; i < 3; i++) {
         freshlyPressedHistory[i] = new HashSet<>();
      }
   }

   public static record ComboInputIntent(
      ComboType type,
      ComboIntentResolver.ComboIntentType intentType,
      int pressDuration,
      long pressIntervalMs,
      int directionMask,
      List<DirectionTracker.DirectionEvent> directionEvents,
      long captureTick
   ) {
      public boolean isLongPress() {
         return this.intentType == ComboIntentResolver.ComboIntentType.LONG_PRESS;
      }
   }

   public static enum ComboIntentType {
      SHORT_PRESS,
      LONG_PRESS;
   }
}
