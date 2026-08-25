package com.dmc.invincible_dmc.client.input;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.conditions.DirectionalSequenceCondition;
import com.dmc.invincible_dmc.utils.DMCLog;
import java.util.ArrayList;
import java.util.List;

public final class DirectionTracker {
   private static final int CAPACITY = 32;
   private static final DirectionalSequenceCondition.Sequence[] ALL_SEQUENCES = DirectionalSequenceCondition.Sequence.values();
   private static final int TAP_MAX_HOLD_TICKS = 6;
   private final DirectionTracker.InternalDirEvent[] buffer = new DirectionTracker.InternalDirEvent[32];
   private int head = 0;
   private int size = 0;
   private final DirectionTracker.InternalDirEvent[] currentHolds = new DirectionTracker.InternalDirEvent[4];

   public void update(boolean up, boolean down, boolean left, boolean right, long currentTick) {
      this.handleState(DirectionalSequenceCondition.Direction.UP, up, currentTick);
      this.handleState(DirectionalSequenceCondition.Direction.DOWN, down, currentTick);
      this.handleState(DirectionalSequenceCondition.Direction.LEFT, left, currentTick);
      this.handleState(DirectionalSequenceCondition.Direction.RIGHT, right, currentTick);
   }

   private void handleState(DirectionalSequenceCondition.Direction dir, boolean isDown, long tick) {
      int idx = dir.ordinal();
      DirectionTracker.InternalDirEvent active = this.currentHolds[idx];
      if (isDown && active == null) {
         DirectionTracker.InternalDirEvent e = new DirectionTracker.InternalDirEvent(dir, tick);
         this.pushEvent(e);
         this.currentHolds[idx] = e;
      } else if (!isDown && active != null) {
         active.releaseTick = tick;
         this.currentHolds[idx] = null;
      }
   }

   private void pushEvent(DirectionTracker.InternalDirEvent e) {
      this.buffer[this.head] = e;
      this.head = (this.head + 1) % 32;
      if (this.size < 32) {
         this.size++;
      }
   }

   private DirectionTracker.InternalDirEvent getEvent(int indexFromOldest) {
      int actualIndex = (this.head - this.size + indexFromOldest + 32) % 32;
      return this.buffer[actualIndex];
   }

   private boolean isTap(DirectionTracker.InternalDirEvent e, long currentTick) {
      return e.releaseTick >= 0L ? e.releaseTick - e.pressTick <= 6L : currentTick - e.pressTick <= 6L;
   }

   public void tickExpiration(long currentTick) {
      int matchWindow = (Integer)DMConfig.DIRECTION_SEQUENCE_MATCH_WINDOW.get();
      int activationWindow = (Integer)DMConfig.DIRECTION_SEQUENCE_ACTIVATION_WINDOW.get();
      long lifetime = (long)Math.max(matchWindow, activationWindow);
      int kept = 0;
      int oldest = (this.head - this.size + 32) % 32;

      for (int i = 0; i < this.size; i++) {
         int src = (oldest + i) % 32;
         DirectionTracker.InternalDirEvent e = this.buffer[src];
         if (currentTick - e.pressTick <= lifetime) {
            if (i != kept) {
               this.buffer[(oldest + kept) % 32] = e;
            }

            kept++;
         }
      }

      this.head = (oldest + kept) % 32;
      this.size = kept;
   }

   public void clear() {
      this.size = 0;
      this.head = 0;

      for (int i = 0; i < 4; i++) {
         this.currentHolds[i] = null;
      }
   }

   public void clearForDodge(long currentTick, boolean up, boolean down, boolean left, boolean right) {
      this.size = 0;
      this.head = 0;
      this.currentHolds[0] = up ? new DirectionTracker.InternalDirEvent(DirectionalSequenceCondition.Direction.UP, currentTick) : null;
      this.currentHolds[1] = down ? new DirectionTracker.InternalDirEvent(DirectionalSequenceCondition.Direction.DOWN, currentTick) : null;
      this.currentHolds[2] = left ? new DirectionTracker.InternalDirEvent(DirectionalSequenceCondition.Direction.LEFT, currentTick) : null;
      this.currentHolds[3] = right ? new DirectionTracker.InternalDirEvent(DirectionalSequenceCondition.Direction.RIGHT, currentTick) : null;
   }

   public void consume(long currentTick) {
      DMCLog.debug(DMCLog.Category.DIRECTION, "[DirTrack] consume at T{}", currentTick);
      this.size = 0;
      this.head = 0;

      for (int i = 0; i < 4; i++) {
         DirectionTracker.InternalDirEvent held = this.currentHolds[i];
         if (held != null) {
            DirectionTracker.InternalDirEvent fresh = new DirectionTracker.InternalDirEvent(held.direction, held.pressTick);
            this.pushEvent(fresh);
            this.currentHolds[i] = fresh;
         }
      }
   }

   public int getMatchedSequencesMask(long matchWindowTicks, long activationWindowTicks, long currentTick) {
      if (this.size == 0) {
         return 0;
      } else {
         int mask = 0;

         for (DirectionalSequenceCondition.Sequence seq : ALL_SEQUENCES) {
            long completionTick = this.matchSequence(seq, matchWindowTicks, activationWindowTicks, currentTick);
            if (completionTick >= 0L) {
               mask |= 1 << seq.ordinal();
            }
         }

         return mask;
      }
   }

   private long matchSequence(DirectionalSequenceCondition.Sequence seq, long matchWindow, long activationWindow, long currentTick) {
      List<DirectionalSequenceCondition.Direction> dirs = seq.getDirections();
      if (!dirs.isEmpty() && this.size >= dirs.size()) {
         int seqIdx = dirs.size() - 1;
         int bufIdx = this.size - 1;
         DirectionTracker.InternalDirEvent nextEvent = null;

         long completionTick;
         for (completionTick = -1L; bufIdx >= 0; bufIdx--) {
            DirectionTracker.InternalDirEvent e = this.getEvent(bufIdx);
            if (e.direction == dirs.get(seqIdx) && this.isTap(e, currentTick) && currentTick - e.pressTick <= activationWindow) {
               nextEvent = e;
               completionTick = e.pressTick;
               break;
            }
         }

         if (nextEvent == null) {
            return -1L;
         } else {
            seqIdx--;
            bufIdx--;

            while (seqIdx >= 0) {
               DirectionalSequenceCondition.Direction targetDir = dirs.get(seqIdx);

               boolean found;
               for (found = false; bufIdx >= 0; bufIdx--) {
                  DirectionTracker.InternalDirEvent e = this.getEvent(bufIdx);
                  if (e.direction == targetDir
                     && this.isTap(e, currentTick)
                     && nextEvent.pressTick - e.pressTick > 0L
                     && nextEvent.pressTick - e.pressTick <= matchWindow) {
                     found = true;
                     nextEvent = e;
                     seqIdx--;
                     bufIdx--;
                     break;
                  }
               }

               if (!found) {
                  return -1L;
               }
            }

            return completionTick;
         }
      } else {
         return -1L;
      }
   }

   public List<DirectionTracker.DirectionEvent> exportRecentEvents() {
      List<DirectionTracker.DirectionEvent> out = new ArrayList<>(this.size);

      for (int i = 0; i < this.size; i++) {
         DirectionTracker.InternalDirEvent internal = this.getEvent(i);
         out.add(new DirectionTracker.DirectionEvent(internal.direction, internal.pressTick));
      }

      return out;
   }

   public static record DirectionEvent(DirectionalSequenceCondition.Direction direction, long tick) {
   }

   private static class InternalDirEvent {
      final DirectionalSequenceCondition.Direction direction;
      final long pressTick;
      long releaseTick = -1L;

      InternalDirEvent(DirectionalSequenceCondition.Direction dir, long pressTick) {
         this.direction = dir;
         this.pressTick = pressTick;
      }
   }
}
