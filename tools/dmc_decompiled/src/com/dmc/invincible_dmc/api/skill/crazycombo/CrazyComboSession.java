package com.dmc.invincible_dmc.api.skill.crazycombo;

import org.jetbrains.annotations.Nullable;

public final class CrazyComboSession {
   private static final float HIGH_FREQUENCY_INTERVAL_TICKS = 3.0F;
   private static final float STOP_CONFIRM_INTERVAL_MULTIPLIER = 1.25F;
   private static final int MIN_STOP_CONFIRM_TICKS = 2;
   private static final int MAX_STOP_CONFIRM_TICKS = 4;
   private static final int FINISH_PHASE_DELAY = 2;
   private static final int FINISH_RETRY_INTERVAL_TICKS = 2;
   private static final float CHASE_RESTART_EPSILON = 0.008333334F;
   private CrazyComboPhase phase = CrazyComboPhase.IDLE;
   private int sourceNodeId = -1;
   private int inputKeyIndex = -1;
   private long authoritySessionId;
   @Nullable
   private CrazyComboPolicy policy;
   private int loopCount;
   private int pressCount;
   private int startupAcceptedPresses;
   private long lastPressTick = Long.MIN_VALUE;
   private long lastAcceptedInputTick = Long.MIN_VALUE;
   private int lastAcceptedInputPhaseOrder = -1;
   private float recentInputIntervalTicks = -1.0F;
   private int recentInputIntervalSamples;
   private long chaseAnimationStartedTick = Long.MIN_VALUE;
   private int chaseAnimationStartedPhaseOrder = -1;
   private boolean chaseAnimationRestartRequired;
   private float chaseRequestElapsedTime = -1.0F;
   private long stopConfirmDeadlineTick = Long.MIN_VALUE;
   private int stopConfirmPhaseOrder = -1;
   private CrazyComboSession.FinishMode finishMode = CrazyComboSession.FinishMode.NONE;
   private int requiredFinishPhaseOrder = -1;
   private long lastFinishRequestTick = Long.MIN_VALUE;

   public void begin(int sourceNodeId, int inputKeyIndex, long authoritySessionId, CrazyComboPolicy policy) {
      this.reset();
      this.phase = CrazyComboPhase.STARTUP;
      this.sourceNodeId = sourceNodeId;
      this.inputKeyIndex = inputKeyIndex;
      this.authoritySessionId = Math.max(0L, authoritySessionId);
      this.policy = policy;
   }

   public CrazyComboSession.Decision advance(CrazyComboSession.TickInput input) {
      if (this.phase != CrazyComboPhase.IDLE && this.policy != null && this.phase != CrazyComboPhase.FINISH_REQUESTED) {
         this.expirePressBurst(input.tick());
         this.recordAcceptedInput(input);

         return switch (this.phase) {
            case STARTUP -> this.advanceStartup(input);
            case CHASE_REQUESTED -> CrazyComboSession.Decision.NONE;
            case CHASE_ACTIVE -> this.advanceChase(input);
            case STOP_CONFIRMING -> this.advanceStopConfirmation(input);
            case FINISH_ARMED -> this.finishDecisionIfReady(input.currentPhaseOrder());
            case IDLE, FINISH_REQUESTED -> CrazyComboSession.Decision.NONE;
         };
      } else {
         return CrazyComboSession.Decision.NONE;
      }
   }

   private CrazyComboSession.Decision advanceStartup(CrazyComboSession.TickInput input) {
      if (this.startupAcceptedPresses == 0
         && input.hasFinishNoChase()
         && input.startupFinishNoChasePhase() >= 0
         && input.currentPhaseOrder() >= input.startupFinishNoChasePhase()) {
         this.requiredFinishPhaseOrder = input.startupFinishNoChasePhase();
         this.finishMode = CrazyComboSession.FinishMode.NO_CHASE;
         return CrazyComboSession.Decision.FINISH_NO_CHASE;
      } else if (!input.canBasicAttack()) {
         return CrazyComboSession.Decision.NONE;
      } else if (this.pressCount >= this.policy.baseRequiredPresses()) {
         this.clearFinishIntent();
         return CrazyComboSession.Decision.CHASE;
      } else if (input.hasFinishNoChase()) {
         this.finishMode = CrazyComboSession.FinishMode.NO_CHASE;
         this.requiredFinishPhaseOrder = -1;
         return CrazyComboSession.Decision.FINISH_NO_CHASE;
      } else {
         return this.pressCount > 0 ? CrazyComboSession.Decision.RELEASE_TO_NORMAL_COMBO : CrazyComboSession.Decision.CANCEL;
      }
   }

   private CrazyComboSession.Decision advanceChase(CrazyComboSession.TickInput input) {
      if (!input.holdFinish() || !input.canBasicAttack() && !input.finishGate()) {
         if (this.shouldArmStoppedFinish(input)) {
            int stopPhase = this.lastAcceptedInputPhaseOrder >= 0 ? this.lastAcceptedInputPhaseOrder : this.chaseAnimationStartedPhaseOrder;
            this.armDelayedFinish(stopPhase >= 0 ? stopPhase : input.currentPhaseOrder(), input.currentPhaseCount());
            return this.finishDecisionIfReady(input.currentPhaseOrder());
         } else if (!input.canBasicAttack() || this.pressCount < this.policy.chaseRequiredPresses()) {
            return CrazyComboSession.Decision.NONE;
         } else if (this.loopCount >= input.maxChases()) {
            this.armImmediateFinish(CrazyComboSession.FinishMode.NORMAL);
            return CrazyComboSession.Decision.FINISH;
         } else if (this.shouldFinishStoppedHighFrequencyAtGate(input)) {
            int stopPhase = this.lastAcceptedInputPhaseOrder >= 0 ? this.lastAcceptedInputPhaseOrder : input.currentPhaseOrder();
            this.armDelayedFinish(stopPhase, input.currentPhaseCount());
            return this.finishDecisionIfReady(input.currentPhaseOrder());
         } else {
            this.clearFinishIntent();
            return CrazyComboSession.Decision.CHASE;
         }
      } else {
         this.armImmediateFinish(CrazyComboSession.FinishMode.NORMAL);
         return CrazyComboSession.Decision.FINISH;
      }
   }

   private CrazyComboSession.Decision advanceStopConfirmation(CrazyComboSession.TickInput input) {
      if (!input.inputDown() && input.clicks() <= 0) {
         if (input.tick() < this.stopConfirmDeadlineTick) {
            return CrazyComboSession.Decision.NONE;
         } else {
            this.armDelayedFinish(this.stopConfirmPhaseOrder >= 0 ? this.stopConfirmPhaseOrder : input.currentPhaseOrder(), input.currentPhaseCount());
            return this.finishDecisionIfReady(input.currentPhaseOrder());
         }
      } else {
         this.phase = CrazyComboPhase.CHASE_ACTIVE;
         this.clearStopConfirmation();
         this.clearFinishIntent();
         return CrazyComboSession.Decision.CHASE;
      }
   }

   private boolean shouldArmStoppedFinish(CrazyComboSession.TickInput input) {
      if (this.pressCount >= this.policy.chaseRequiredPresses()) {
         return false;
      } else {
         long stopReferenceTick = this.lastAcceptedInputTick != Long.MIN_VALUE ? this.lastAcceptedInputTick : this.chaseAnimationStartedTick;
         boolean inputStopped = stopReferenceTick != Long.MIN_VALUE && input.tick() - stopReferenceTick > (long)this.policy.rapidMaxIntervalTicks();
         return inputStopped || input.canBasicAttack();
      }
   }

   private CrazyComboSession.Decision finishDecisionIfReady(int currentPhaseOrder) {
      if (currentPhaseOrder < this.requiredFinishPhaseOrder) {
         return CrazyComboSession.Decision.NONE;
      } else {
         return this.finishMode == CrazyComboSession.FinishMode.NO_CHASE ? CrazyComboSession.Decision.FINISH_NO_CHASE : CrazyComboSession.Decision.FINISH;
      }
   }

   private void recordAcceptedInput(CrazyComboSession.TickInput input) {
      if (input.inInputWindow() && input.clicks() > 0) {
         this.pressCount = this.pressCount + input.clicks();
         this.lastPressTick = input.tick();
         if (this.phase == CrazyComboPhase.STARTUP) {
            this.startupAcceptedPresses = this.startupAcceptedPresses + input.clicks();
         }

         if (this.phase.isChase() && this.lastAcceptedInputTick != Long.MIN_VALUE && input.tick() > this.lastAcceptedInputTick) {
            long interval = input.tick() - this.lastAcceptedInputTick;
            if (interval <= (long)this.policy.rapidMaxIntervalTicks()) {
               this.recentInputIntervalTicks = this.recentInputIntervalSamples == 0
                  ? (float)interval
                  : this.recentInputIntervalTicks * 0.6F + (float)interval * 0.4F;
               this.recentInputIntervalSamples++;
            } else {
               this.recentInputIntervalTicks = -1.0F;
               this.recentInputIntervalSamples = 0;
            }
         }

         this.lastAcceptedInputTick = input.tick();
         if (this.phase != CrazyComboPhase.CHASE_REQUESTED) {
            this.lastAcceptedInputPhaseOrder = Math.max(0, input.currentPhaseOrder());
         }
      }
   }

   public void commitChase(boolean restartRequired, float requestElapsedTime) {
      if (this.phase != CrazyComboPhase.IDLE && this.phase != CrazyComboPhase.FINISH_REQUESTED) {
         this.phase = CrazyComboPhase.CHASE_REQUESTED;
         this.loopCount++;
         this.clearCycleInput();
         this.clearStopConfirmation();
         this.clearFinishIntent();
         this.chaseAnimationRestartRequired = restartRequired;
         this.chaseRequestElapsedTime = requestElapsedTime;
      }
   }

   public boolean canConfirmChaseAnimation(boolean playingChaseAnimation, float elapsedTime) {
      if (this.phase != CrazyComboPhase.CHASE_REQUESTED || !playingChaseAnimation) {
         return false;
      } else {
         return !this.chaseAnimationRestartRequired
            ? true
            : elapsedTime >= 0.0F && this.chaseRequestElapsedTime >= 0.0F && elapsedTime + 0.008333334F < this.chaseRequestElapsedTime;
      }
   }

   public void markChaseAnimationStarted(long tick, int phaseOrder) {
      if (this.phase == CrazyComboPhase.CHASE_REQUESTED) {
         this.phase = CrazyComboPhase.CHASE_ACTIVE;
         this.chaseAnimationStartedTick = tick;
         this.chaseAnimationStartedPhaseOrder = Math.max(0, phaseOrder);
         if (this.pressCount > 0) {
            this.lastAcceptedInputPhaseOrder = this.chaseAnimationStartedPhaseOrder;
         }

         this.clearChaseRequest();
      }
   }

   public void restoreChase(int loopCount, long tick) {
      if (this.phase != CrazyComboPhase.IDLE) {
         this.phase = CrazyComboPhase.CHASE_ACTIVE;
         this.loopCount = Math.max(0, loopCount);
         this.clearCycleInput();
         this.clearStopConfirmation();
         this.clearFinishIntent();
         this.chaseAnimationStartedTick = tick;
         this.chaseAnimationStartedPhaseOrder = 0;
         this.clearChaseRequest();
      }
   }

   public void beginFinish() {
      if (this.phase != CrazyComboPhase.IDLE) {
         this.phase = CrazyComboPhase.FINISH_REQUESTED;
         this.clearCycleInput();
         this.clearStopConfirmation();
         this.clearChaseRequest();
         this.lastFinishRequestTick = Long.MIN_VALUE;
      }
   }

   public void bindAuthoritySession(long sessionId) {
      if (this.phase != CrazyComboPhase.IDLE && sessionId > 0L) {
         this.authoritySessionId = sessionId;
      }
   }

   public boolean isBoundTo(int nodeId) {
      return this.phase != CrazyComboPhase.IDLE && this.sourceNodeId == nodeId;
   }

   public boolean acceptsCrazyComboInput() {
      return this.phase.acceptsInput();
   }

   public void reset() {
      this.phase = CrazyComboPhase.IDLE;
      this.sourceNodeId = -1;
      this.inputKeyIndex = -1;
      this.authoritySessionId = 0L;
      this.policy = null;
      this.loopCount = 0;
      this.startupAcceptedPresses = 0;
      this.clearCycleInput();
      this.clearStopConfirmation();
      this.clearChaseRequest();
      this.clearFinishIntent();
   }

   private void expirePressBurst(long tick) {
      if (this.lastPressTick != Long.MIN_VALUE && tick - this.lastPressTick > (long)this.policy.rapidMaxIntervalTicks()) {
         this.pressCount = 0;
         this.lastPressTick = Long.MIN_VALUE;
      }
   }

   private void clearCycleInput() {
      this.pressCount = 0;
      this.lastPressTick = Long.MIN_VALUE;
      this.lastAcceptedInputTick = Long.MIN_VALUE;
      this.lastAcceptedInputPhaseOrder = -1;
      this.recentInputIntervalTicks = -1.0F;
      this.recentInputIntervalSamples = 0;
      this.chaseAnimationStartedTick = Long.MIN_VALUE;
      this.chaseAnimationStartedPhaseOrder = -1;
   }

   private void armDelayedFinish(int stopPhaseOrder, int phaseCount) {
      this.phase = CrazyComboPhase.FINISH_ARMED;
      this.finishMode = CrazyComboSession.FinishMode.NORMAL;
      this.requiredFinishPhaseOrder = Math.max(0, stopPhaseOrder) + 2;
      if (phaseCount >= 0) {
         this.requiredFinishPhaseOrder = Math.min(this.requiredFinishPhaseOrder, phaseCount);
      }

      this.clearStopConfirmation();
   }

   private void armImmediateFinish(CrazyComboSession.FinishMode mode) {
      this.finishMode = mode;
      this.requiredFinishPhaseOrder = -1;
   }

   private void clearFinishIntent() {
      this.finishMode = CrazyComboSession.FinishMode.NONE;
      this.requiredFinishPhaseOrder = -1;
      this.lastFinishRequestTick = Long.MIN_VALUE;
   }

   private boolean shouldConfirmHighFrequencyStop() {
      return this.recentInputIntervalSamples > 0 && this.recentInputIntervalTicks <= 3.0F;
   }

   private boolean shouldFinishStoppedHighFrequencyAtGate(CrazyComboSession.TickInput input) {
      return this.shouldConfirmHighFrequencyStop() && !input.inputDown() && input.clicks() <= 0 && this.lastAcceptedInputTick != Long.MIN_VALUE
         ? input.tick() - this.lastAcceptedInputTick > (long)this.getStopConfirmTicks()
         : false;
   }

   private int getStopConfirmTicks() {
      int ticks = (int)Math.ceil((double)(this.recentInputIntervalTicks * 1.25F));
      return Math.max(2, Math.min(4, ticks));
   }

   private void clearStopConfirmation() {
      this.stopConfirmDeadlineTick = Long.MIN_VALUE;
      this.stopConfirmPhaseOrder = -1;
   }

   private void clearChaseRequest() {
      this.chaseAnimationRestartRequired = false;
      this.chaseRequestElapsedTime = -1.0F;
   }

   public CrazyComboPhase phase() {
      return this.phase;
   }

   public CrazyComboSession.Stage stage() {
      return switch (this.phase) {
         case STARTUP -> CrazyComboSession.Stage.STARTUP;
         case IDLE -> CrazyComboSession.Stage.IDLE;
         case FINISH_REQUESTED -> CrazyComboSession.Stage.FINISH;
         default -> CrazyComboSession.Stage.CHASE;
      };
   }

   public int sourceNodeId() {
      return this.sourceNodeId;
   }

   public int inputKeyIndex() {
      return this.inputKeyIndex;
   }

   public long authoritySessionId() {
      return this.authoritySessionId;
   }

   public int loopCount() {
      return this.loopCount;
   }

   public int pressCount() {
      return this.pressCount;
   }

   public boolean waitingForChaseAnimation() {
      return this.phase == CrazyComboPhase.CHASE_REQUESTED;
   }

   public boolean delayedFinishArmed() {
      return this.phase == CrazyComboPhase.FINISH_ARMED;
   }

   public int requiredFinishPhaseOrder() {
      return this.requiredFinishPhaseOrder;
   }

   public boolean chaseCommitPending() {
      return this.phase == CrazyComboPhase.STOP_CONFIRMING;
   }

   public boolean isFinishNoChase() {
      return this.finishMode == CrazyComboSession.FinishMode.NO_CHASE;
   }

   public boolean shouldSendFinishRequest(long tick) {
      return this.phase == CrazyComboPhase.FINISH_REQUESTED && (this.lastFinishRequestTick == Long.MIN_VALUE || tick - this.lastFinishRequestTick >= 2L);
   }

   public void markFinishRequestSent(long tick) {
      if (this.phase == CrazyComboPhase.FINISH_REQUESTED) {
         this.lastFinishRequestTick = tick;
      }
   }

   public static enum Decision {
      NONE,
      CHASE,
      FINISH,
      FINISH_NO_CHASE,
      RELEASE_TO_NORMAL_COMBO,
      CANCEL;
   }

   private static enum FinishMode {
      NONE,
      NORMAL,
      NO_CHASE;
   }

   public static enum Stage {
      IDLE,
      STARTUP,
      CHASE,
      FINISH;
   }

   public static record TickInput(
      long tick,
      int clicks,
      boolean inInputWindow,
      int currentPhaseOrder,
      int currentPhaseCount,
      boolean canBasicAttack,
      boolean finishGate,
      boolean holdFinish,
      boolean hasFinishNoChase,
      int maxChases,
      int startupFinishNoChasePhase,
      boolean inputDown
   ) {
   }
}
