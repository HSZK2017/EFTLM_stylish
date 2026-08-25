package org.merlin204.mimic.entity.ai;

import java.util.function.Consumer;
import org.merlin204.mimic.entity.MimicPatch;

public class MimicConsumers {
   public static final Consumer<MimicPatch> PLAY_RANDOM_COPY = mimicPatch -> {
      if (mimicPatch != null) {
         mimicPatch.playRandomAnimation();
      }
   };
   public static final Consumer<MimicPatch> PLAY_RANDOM_COPY_WITHOUT_HIT = mimicPatch -> {
      if (mimicPatch != null) {
         mimicPatch.playRandomAnimationWithoutCanHit();
      }
   };
   public static final Consumer<MimicPatch> PLAY_CAN_HIT_COPY = mimicPatch -> {
      if (mimicPatch != null) {
         mimicPatch.playCanHitAnimation();
      }
   };
   public static final Consumer<MimicPatch> TRY_PLAY_COMBO = mimicPatch -> {
      if (mimicPatch != null) {
         mimicPatch.tryPlayCombo();
      }
   };
}
