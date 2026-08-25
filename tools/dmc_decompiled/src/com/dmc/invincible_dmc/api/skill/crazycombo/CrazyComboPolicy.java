package com.dmc.invincible_dmc.api.skill.crazycombo;

public record CrazyComboPolicy(
   int baseRequiredPresses,
   int chaseRequiredPresses,
   int rapidMaxIntervalTicks,
   float inputWindowStart,
   int finishMinPhase,
   int startupFinishNoChasePhase,
   boolean resetCombo
) {
   public static final CrazyComboPolicy DEFAULT = new CrazyComboPolicy(3, 2, 4, 0.0F, 2, -1, true);

   public CrazyComboPolicy(
      int baseRequiredPresses,
      int chaseRequiredPresses,
      int rapidMaxIntervalTicks,
      float inputWindowStart,
      int finishMinPhase,
      int startupFinishNoChasePhase,
      boolean resetCombo
   ) {
      baseRequiredPresses = Math.max(1, baseRequiredPresses);
      chaseRequiredPresses = Math.max(1, chaseRequiredPresses);
      rapidMaxIntervalTicks = Math.max(1, rapidMaxIntervalTicks);
      inputWindowStart = Math.max(0.0F, Math.min(1.0F, inputWindowStart));
      finishMinPhase = Math.max(0, finishMinPhase);
      startupFinishNoChasePhase = Math.max(-1, startupFinishNoChasePhase);
      this.baseRequiredPresses = baseRequiredPresses;
      this.chaseRequiredPresses = chaseRequiredPresses;
      this.rapidMaxIntervalTicks = rapidMaxIntervalTicks;
      this.inputWindowStart = inputWindowStart;
      this.finishMinPhase = finishMinPhase;
      this.startupFinishNoChasePhase = startupFinishNoChasePhase;
      this.resetCombo = resetCombo;
   }

   public CrazyComboPolicy withBaseRequiredPresses(int value) {
      return new CrazyComboPolicy(
         value,
         this.chaseRequiredPresses,
         this.rapidMaxIntervalTicks,
         this.inputWindowStart,
         this.finishMinPhase,
         this.startupFinishNoChasePhase,
         this.resetCombo
      );
   }

   public CrazyComboPolicy withChaseRequiredPresses(int value) {
      return new CrazyComboPolicy(
         this.baseRequiredPresses,
         value,
         this.rapidMaxIntervalTicks,
         this.inputWindowStart,
         this.finishMinPhase,
         this.startupFinishNoChasePhase,
         this.resetCombo
      );
   }

   public CrazyComboPolicy withRapidMaxIntervalTicks(int value) {
      return new CrazyComboPolicy(
         this.baseRequiredPresses,
         this.chaseRequiredPresses,
         value,
         this.inputWindowStart,
         this.finishMinPhase,
         this.startupFinishNoChasePhase,
         this.resetCombo
      );
   }

   public CrazyComboPolicy withInputWindowStart(float value) {
      return new CrazyComboPolicy(
         this.baseRequiredPresses,
         this.chaseRequiredPresses,
         this.rapidMaxIntervalTicks,
         value,
         this.finishMinPhase,
         this.startupFinishNoChasePhase,
         this.resetCombo
      );
   }

   public CrazyComboPolicy withFinishMinPhase(int value) {
      return new CrazyComboPolicy(
         this.baseRequiredPresses,
         this.chaseRequiredPresses,
         this.rapidMaxIntervalTicks,
         this.inputWindowStart,
         value,
         this.startupFinishNoChasePhase,
         this.resetCombo
      );
   }

   public CrazyComboPolicy withStartupFinishNoChasePhase(int value) {
      return new CrazyComboPolicy(
         this.baseRequiredPresses, this.chaseRequiredPresses, this.rapidMaxIntervalTicks, this.inputWindowStart, this.finishMinPhase, value, this.resetCombo
      );
   }

   public CrazyComboPolicy withResetCombo(boolean value) {
      return new CrazyComboPolicy(
         this.baseRequiredPresses,
         this.chaseRequiredPresses,
         this.rapidMaxIntervalTicks,
         this.inputWindowStart,
         this.finishMinPhase,
         this.startupFinishNoChasePhase,
         value
      );
   }
}
