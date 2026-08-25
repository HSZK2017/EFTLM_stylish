package com.dmc.invincible_dmc.entity.summonedsword.motion;

import com.dmc.invincible_dmc.entity.summonedsword.DMCSummonedSwordEntity;
import com.dmc.invincible_dmc.entity.summonedsword.SpiralBladesEntity;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public final class SpiralOrbitMotion implements SummonedSwordMotionController.Motion {
   @Nullable
   @Override
   public SummonedSwordTransform sample(SummonedSwordMotionController controller, DMCSummonedSwordEntity sword, boolean previous) {
      if (controller instanceof SpiralBladesEntity spiral) {
         LivingEntity owner = spiral.getOwner();
         int index = sword.getFormationIndex();
         int total = spiral.getTotalSwords();
         if (owner != null && index >= 0 && index < total) {
            long tick = spiral.getMotionTick(previous);
            Vec3 base = previous ? new Vec3(owner.f_19854_, owner.f_19855_, owner.f_19856_) : owner.m_20182_();
            double angle = (double)index * ((Math.PI * 2) / (double)total) + (double)tick * Math.toRadians((double)spiral.getMotionRotationSpeed());
            Vec3 position = base.m_82520_(
               spiral.getMotionRadius() * Math.cos(angle), (double)owner.m_20206_() * 0.6, spiral.getMotionRadius() * Math.sin(angle)
            );
            float yaw = (float)Math.toDegrees(Mth.m_14136_(Math.sin(angle), Math.cos(angle))) - 90.0F;
            return new SummonedSwordTransform(position, yaw, 0.0F);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }
}
