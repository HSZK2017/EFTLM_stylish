package com.dmc.invincible_dmc.entity.summonedsword.motion;

import com.dmc.invincible_dmc.entity.summonedsword.DMCSummonedSwordEntity;
import com.dmc.invincible_dmc.entity.summonedsword.SpineBladeEntity;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public final class SpineAttachedMotion implements SummonedSwordMotionController.Motion {
   @Nullable
   @Override
   public SummonedSwordTransform sample(SummonedSwordMotionController controller, DMCSummonedSwordEntity sword, boolean previous) {
      if (controller instanceof SpineBladeEntity spine) {
         LivingEntity owner = spine.getOwner();
         if (owner == null) {
            return null;
         } else {
            Vec3 position = SpineBladeEntity.getHandLJointWorldPos(owner, previous ? 0.0F : 1.0F);
            float yaw = (float)spine.getMotionTick(previous) * spine.getMotionSpinSpeed();
            return new SummonedSwordTransform(position, yaw, 0.0F);
         }
      } else {
         return null;
      }
   }
}
