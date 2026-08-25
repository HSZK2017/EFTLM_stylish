package com.dmc.invincible_dmc.entity.summonedsword.motion;

import com.dmc.invincible_dmc.entity.summonedsword.DMCSummonedSwordEntity;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public interface SummonedSwordMotionController {
   SummonedSwordMotionMode getMotionMode();

   @Nullable
   default SummonedSwordTransform sampleSwordTransform(DMCSummonedSwordEntity sword, boolean previous) {
      return this.getMotionMode().motion().sample(this, sword, previous);
   }

   default void bindSwordMotion(DMCSummonedSwordEntity sword, int formationIndex) {
      if (this instanceof Entity controllerEntity) {
         sword.bindMotionController(controllerEntity, formationIndex);
         sword.snapToManagedMotion();
      }
   }

   default void bindSwordMotion(DMCSummonedSwordEntity sword, int formationIndex, Vec3 offset) {
      sword.setMotionOffset(offset);
      this.bindSwordMotion(sword, formationIndex);
   }

   default void releaseSwordMotion(DMCSummonedSwordEntity sword) {
      sword.snapToManagedMotion();
      sword.detachMotionController();
   }

   @FunctionalInterface
   public interface Motion {
      @Nullable
      SummonedSwordTransform sample(SummonedSwordMotionController var1, DMCSummonedSwordEntity var2, boolean var3);
   }
}
