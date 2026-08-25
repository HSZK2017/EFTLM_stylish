package com.dmc.invincible_dmc.entity.summonedsword.motion;

import com.dmc.invincible_dmc.entity.summonedsword.DMCSummonedSwordEntity;
import com.dmc.invincible_dmc.entity.summonedsword.TripleBladesEntity;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public final class TripleFormationMotion implements SummonedSwordMotionController.Motion {
   @Nullable
   @Override
   public SummonedSwordTransform sample(SummonedSwordMotionController controller, DMCSummonedSwordEntity sword, boolean previous) {
      if (controller instanceof TripleBladesEntity triple) {
         Vec3 controllerPosition = triple.m_20182_();
         Vec3 position = controllerPosition.m_82549_(sword.getMotionOffset());
         LivingEntity target = triple.getTarget();
         if (target != null && target.m_6084_()) {
            Vec3 targetPosition = previous
               ? new Vec3(target.f_19854_, target.f_19855_ + (double)target.m_20206_() * 0.7, target.f_19856_)
               : target.m_20182_().m_82520_(0.0, (double)target.m_20206_() * 0.7, 0.0);
            Vec3 direction = targetPosition.m_82546_(position).m_82541_();
            float yaw = (float)Math.toDegrees(Mth.m_14136_(direction.f_82481_, direction.f_82479_)) - 90.0F;
            float pitch = (float)(-Math.toDegrees(Mth.m_14136_(direction.f_82480_, direction.m_165924_())));
            return new SummonedSwordTransform(position, yaw, pitch);
         } else {
            return new SummonedSwordTransform(position, sword.getMotionYaw(), sword.getMotionPitch());
         }
      } else {
         return null;
      }
   }
}
