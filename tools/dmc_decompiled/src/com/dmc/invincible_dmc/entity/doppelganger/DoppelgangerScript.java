package com.dmc.invincible_dmc.entity.doppelganger;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class DoppelgangerScript {
   private final ScriptInstruction[] groundInstructions;
   private final ScriptInstruction[] airInstructions;
   private ScriptInstruction[] activeInstructions;
   private int currentIndex = -1;
   private int loopRemaining = 0;
   private int loopStartIndex = -1;
   private boolean active = false;

   private DoppelgangerScript(ScriptInstruction[] ground, ScriptInstruction[] air) {
      this.groundInstructions = ground;
      this.airInstructions = air;
   }

   public static DoppelgangerScript.Builder create() {
      return new DoppelgangerScript.Builder();
   }

   public ScriptInstruction[] groundInstructions() {
      return this.groundInstructions;
   }

   @Nullable
   public ScriptInstruction[] airInstructions() {
      return this.airInstructions;
   }

   public int currentIndex() {
      return this.currentIndex;
   }

   public int loopRemaining() {
      return this.loopRemaining;
   }

   public int loopStartIndex() {
      return this.loopStartIndex;
   }

   public boolean isActive() {
      return this.active;
   }

   public void setCurrentIndex(int idx) {
      this.currentIndex = idx;
   }

   public void setLoopRemaining(int rem) {
      this.loopRemaining = rem;
   }

   public void setLoopStartIndex(int idx) {
      this.loopStartIndex = idx;
   }

   public void activate(boolean grounded) {
      this.active = true;
      this.activeInstructions = !grounded && this.airInstructions != null ? this.airInstructions : this.groundInstructions;
   }

   public void deactivate() {
      this.active = false;
   }

   @Nullable
   public ScriptInstruction peekNext() {
      return this.active && this.currentIndex + 1 < this.activeInstructions.length ? this.activeInstructions[this.currentIndex + 1] : null;
   }

   @Nullable
   public ScriptInstruction next() {
      return this.currentIndex + 1 >= this.activeInstructions.length ? null : this.activeInstructions[++this.currentIndex];
   }

   public void jumpToLoopStart() {
      this.currentIndex = this.loopStartIndex - 1;
   }

   public void clear() {
      this.active = false;
      this.currentIndex = -1;
      this.loopRemaining = 0;
      this.loopStartIndex = -1;
   }

   public static class Builder {
      private final List<ScriptInstruction> ground = new ArrayList<>();
      private final List<ScriptInstruction> air = new ArrayList<>();

      public DoppelgangerScript.Builder ground(ScriptInstruction... insts) {
         this.ground.addAll(List.of(insts));
         return this;
      }

      public DoppelgangerScript.Builder air(ScriptInstruction... insts) {
         this.air.addAll(List.of(insts));
         return this;
      }

      public DoppelgangerScript build() {
         return new DoppelgangerScript(this.ground.toArray(ScriptInstruction[]::new), this.air.toArray(ScriptInstruction[]::new));
      }
   }
}
