package com.dmc.invincible_dmc.entity.summonedsword.motion;

import com.dmc.invincible_dmc.entity.summonedsword.DMCSummonedSwordEntity;
import com.dmc.invincible_dmc.entity.summonedsword.StormBladesEntity;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public final class StormOrbitMotion implements SummonedSwordMotionController.Motion {
   @Nullable
   @Override
   public SummonedSwordTransform sample(SummonedSwordMotionController controller, DMCSummonedSwordEntity sword, boolean previous) {
      if (controller instanceof StormBladesEntity storm) {
         LivingEntity target = storm.getTarget();
         int index = sword.getFormationIndex();
         int total = storm.getTotalSwords();
         if (target != null && index >= 0 && index < total) {
            long tick = storm.getMotionTick(previous);
            Vec3 base = previous ? new Vec3(target.f_19854_, target.f_19855_, target.f_19856_) : target.m_20182_();
            double angle = (double)index * ((Math.PI * 2) / (double)total) + (double)tick * Math.toRadians((double)storm.getMotionRotationSpeed());
            Vec3 position = base.m_82520_(
               storm.getMotionRadius() * Math.cos(angle), (double)target.m_20206_() * 0.65, storm.getMotionRadius() * Math.sin(angle)
            );
            Vec3 direction = base.m_82520_(0.0, (double)target.m_20206_() * 0.5, 0.0).m_82546_(position).m_82541_();
            float yaw = (float)Math.toDegrees(Mth.m_14136_(direction.f_82481_, direction.f_82479_)) - 90.0F;
            return new SummonedSwordTransform(position, yaw, 0.0F);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }
}
