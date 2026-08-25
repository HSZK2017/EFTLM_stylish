package com.dmc.invincible_dmc.client.input.judegementCut;

import com.dmc.invincible_dmc.api.skill.JudgementCutChargePhase;

final class JudgementCutChargeStateMachine {
   private JudgementCutChargePhase phase = JudgementCutChargePhase.IDLE;
   private long pressedAtMs = -1L;
   private long requiredChargeTimeMs = -1L;
   private long readyAtMs = -1L;
   private long suspendedAtMs = -1L;
   private boolean perfectReleaseWindowEndEmitted;

   void begin(long nowMs) {
      this.phase = JudgementCutChargePhase.ARMED;
      this.pressedAtMs = nowMs;
      this.requiredChargeTimeMs = -1L;
      this.readyAtMs = -1L;
      this.suspendedAtMs = -1L;
      this.perfectReleaseWindowEndEmitted = false;
   }

   JudgementCutChargeStateMachine.Update advance(long nowMs, long holdThresholdMs, long resolvedChargeTimeMs, long perfectReleaseWindowMs) {
      if (this.phase == JudgementCutChargePhase.IDLE) {
         return JudgementCutChargeStateMachine.Update.NONE;
      } else {
         boolean chargeStarted = false;
         boolean chargeReady = false;
         boolean perfectReleaseWindowEnded = false;
         long elapsedMs = this.elapsedMs(nowMs);
         if (this.phase == JudgementCutChargePhase.ARMED && elapsedMs >= holdThresholdMs) {
            this.phase = JudgementCutChargePhase.CHARGING;
            this.requiredChargeTimeMs = Math.max(0L, resolvedChargeTimeMs);
            chargeStarted = true;
         }

         if (this.phase == JudgementCutChargePhase.CHARGING && elapsedMs >= this.requiredChargeTimeMs) {
            this.phase = JudgementCutChargePhase.READY;
            this.readyAtMs = this.pressedAtMs + this.requiredChargeTimeMs;
            chargeReady = true;
         }

         if (this.readyAtMs >= 0L && !this.perfectReleaseWindowEndEmitted && nowMs - this.readyAtMs > perfectReleaseWindowMs) {
            this.perfectReleaseWindowEndEmitted = true;
            perfectReleaseWindowEnded = true;
         }

         return !chargeStarted && !chargeReady && !perfectReleaseWindowEnded
            ? JudgementCutChargeStateMachine.Update.NONE
            : new JudgementCutChargeStateMachine.Update(chargeStarted, chargeReady, perfectReleaseWindowEnded);
      }
   }

   void suspend(long nowMs) {
      if (this.phase == JudgementCutChargePhase.READY) {
         this.phase = JudgementCutChargePhase.SUSPENDED_READY;
         this.suspendedAtMs = nowMs;
      }
   }

   void rebindRequiredChargeTime(long requiredChargeTimeMs) {
      if (this.phase == JudgementCutChargePhase.CHARGING) {
         this.requiredChargeTimeMs = Math.max(0L, requiredChargeTimeMs);
         this.readyAtMs = -1L;
         this.perfectReleaseWindowEndEmitted = false;
      }
   }

   void forceReady(long nowMs, long requiredChargeTimeMs) {
      long lockedChargeTimeMs = Math.max(0L, requiredChargeTimeMs);
      this.phase = JudgementCutChargePhase.READY;
      this.pressedAtMs = nowMs - lockedChargeTimeMs - 1000L;
      this.requiredChargeTimeMs = lockedChargeTimeMs;
      this.readyAtMs = this.pressedAtMs + lockedChargeTimeMs;
      this.suspendedAtMs = -1L;
      this.perfectReleaseWindowEndEmitted = true;
   }

   void reset() {
      this.phase = JudgementCutChargePhase.IDLE;
      this.pressedAtMs = -1L;
      this.requiredChargeTimeMs = -1L;
      this.readyAtMs = -1L;
      this.suspendedAtMs = -1L;
      this.perfectReleaseWindowEndEmitted = false;
   }

   JudgementCutChargePhase phase() {
      return this.phase;
   }

   boolean isActive() {
      return this.phase != JudgementCutChargePhase.IDLE;
   }

   boolean isCharging() {
      return this.phase.isCharging();
   }

   boolean isReady() {
      return this.phase == JudgementCutChargePhase.READY || this.phase == JudgementCutChargePhase.SUSPENDED_READY;
   }

   boolean isSuspended() {
      return this.phase == JudgementCutChargePhase.SUSPENDED_READY;
   }

   long pressedAtMs() {
      return this.pressedAtMs;
   }

   long requiredChargeTimeMs() {
      return this.requiredChargeTimeMs;
   }

   long readyAtMs() {
      return this.readyAtMs;
   }

   long suspendedAtMs() {
      return this.suspendedAtMs;
   }

   long elapsedMs(long nowMs) {
      return this.pressedAtMs >= 0L ? Math.max(0L, nowMs - this.pressedAtMs) : 0L;
   }

   static record Update(boolean chargeStarted, boolean chargeReady, boolean perfectReleaseWindowEnded) {
      private static final JudgementCutChargeStateMachine.Update NONE = new JudgementCutChargeStateMachine.Update(false, false, false);
   }
}
