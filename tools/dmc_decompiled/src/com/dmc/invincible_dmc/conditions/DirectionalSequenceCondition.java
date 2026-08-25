package com.dmc.invincible_dmc.conditions;

import com.dmc.invincible_dmc.client.input.DirectionTracker;
import com.dmc.invincible_dmc.utils.DMCLog;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class DirectionalSequenceCondition implements Condition<ServerPlayerPatch> {
   private DirectionalSequenceCondition.Sequence sequence = DirectionalSequenceCondition.Sequence.BACK_FORWARD;
   private int matchWindowTicks = -1;
   private int activationWindowTicks = -1;

   public DirectionalSequenceCondition() {
   }

   public DirectionalSequenceCondition(DirectionalSequenceCondition.Sequence sequence, int matchWindowTicks, int activationWindowTicks) {
      this.sequence = sequence;
      this.matchWindowTicks = matchWindowTicks;
      this.activationWindowTicks = activationWindowTicks;
   }

   public DirectionalSequenceCondition(DirectionalSequenceCondition.Sequence sequence) {
      this.sequence = sequence;
   }

   public Condition<ServerPlayerPatch> read(CompoundTag tag) {
      if (tag.m_128441_("sequence")) {
         DirectionalSequenceCondition.Sequence parsed = DirectionalSequenceCondition.Sequence.byName(tag.m_128461_("sequence"));
         if (parsed == null) {
            DMCLog.warn(DMCLog.Category.DIRECTION, "[DirSeq] Unknown sequence '{}', keeping default {}", tag.m_128461_("sequence"), this.sequence);
         } else {
            this.sequence = parsed;
         }
      }

      if (tag.m_128441_("match_window_ticks")) {
         this.matchWindowTicks = tag.m_128451_("match_window_ticks");
      }

      if (tag.m_128441_("activation_window_ticks")) {
         this.activationWindowTicks = tag.m_128451_("activation_window_ticks");
      }

      return this;
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      tag.m_128359_("sequence", this.sequence.name().toLowerCase());
      if (this.matchWindowTicks >= 0) {
         tag.m_128405_("match_window_ticks", this.matchWindowTicks);
      }

      if (this.activationWindowTicks >= 0) {
         tag.m_128405_("activation_window_ticks", this.activationWindowTicks);
      }

      return tag;
   }

   public boolean predicate(ServerPlayerPatch p) {
      return true;
   }

   public DirectionalSequenceCondition.Sequence getSequence() {
      return this.sequence;
   }

   public int getMatchWindowTicks() {
      return this.matchWindowTicks;
   }

   public int getActivationWindowTicks() {
      return this.activationWindowTicks;
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }

   public static boolean check(DirectionalSequenceCondition dsc, DirectionTracker tracker, long matchWindowTicks, long activationWindowTicks, long currentTick) {
      int mask = tracker.getMatchedSequencesMask(matchWindowTicks, activationWindowTicks, currentTick);
      return (mask & 1 << dsc.sequence.ordinal()) != 0;
   }

   public static boolean check(
      DirectionalSequenceCondition dsc, int matchedSequencesMask, List<DirectionTracker.DirectionEvent> directionEvents, long attackTick
   ) {
      return (matchedSequencesMask & 1 << dsc.sequence.ordinal()) != 0;
   }

   public static enum Direction {
      UP,
      DOWN,
      LEFT,
      RIGHT;
   }

   public static enum Sequence {
      BACK_FORWARD(DirectionalSequenceCondition.Direction.DOWN, DirectionalSequenceCondition.Direction.UP),
      FORWARD_BACK(DirectionalSequenceCondition.Direction.UP, DirectionalSequenceCondition.Direction.DOWN),
      LEFT_RIGHT(DirectionalSequenceCondition.Direction.LEFT, DirectionalSequenceCondition.Direction.RIGHT),
      RIGHT_LEFT(DirectionalSequenceCondition.Direction.RIGHT, DirectionalSequenceCondition.Direction.LEFT),
      BACK_BACK(DirectionalSequenceCondition.Direction.DOWN, DirectionalSequenceCondition.Direction.DOWN),
      FORWARD_FORWARD(DirectionalSequenceCondition.Direction.UP, DirectionalSequenceCondition.Direction.UP),
      LEFT_LEFT(DirectionalSequenceCondition.Direction.LEFT, DirectionalSequenceCondition.Direction.LEFT),
      RIGHT_RIGHT(DirectionalSequenceCondition.Direction.RIGHT, DirectionalSequenceCondition.Direction.RIGHT);

      private static final Map<String, DirectionalSequenceCondition.Sequence> BY_NAME = new HashMap<>(11);
      private final List<DirectionalSequenceCondition.Direction> directions;

      private Sequence(DirectionalSequenceCondition.Direction... dirs) {
         this.directions = List.of(dirs);
      }

      public static DirectionalSequenceCondition.Sequence byName(String name) {
         return BY_NAME.get(name);
      }

      public List<DirectionalSequenceCondition.Direction> getDirections() {
         return this.directions;
      }

      static {
         for (DirectionalSequenceCondition.Sequence s : values()) {
            BY_NAME.put(s.name().toLowerCase(), s);
         }
      }
   }
}
