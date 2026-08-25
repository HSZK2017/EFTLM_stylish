package com.dmc.invincible_dmc.entity.summonedsword.motion;

public enum SummonedSwordMotionMode {
   SPIRAL_ORBIT(new SpiralOrbitMotion()),
   STORM_ORBIT(new StormOrbitMotion()),
   SPINE_ATTACHED(new SpineAttachedMotion()),
   TRIPLE_FORMATION(new TripleFormationMotion()),
   PROVOCATION_FORMATION(new ProvocationFormationMotion()),
   HEAVY_RAIN_FORMATION(new HeavyRainFormationMotion()),
   BLISTERING_FORMATION(new BlisteringFormationMotion());

   private final SummonedSwordMotionController.Motion motion;

   private SummonedSwordMotionMode(SummonedSwordMotionController.Motion motion) {
      this.motion = motion;
   }

   SummonedSwordMotionController.Motion motion() {
      return this.motion;
   }
}
