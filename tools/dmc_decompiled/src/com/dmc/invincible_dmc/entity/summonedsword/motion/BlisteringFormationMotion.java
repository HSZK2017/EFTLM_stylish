package com.dmc.invincible_dmc.entity.summonedsword.motion;

import com.dmc.invincible_dmc.entity.summonedsword.BlisteringBladesEntity;
import com.dmc.invincible_dmc.entity.summonedsword.DMCSummonedSwordEntity;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public final class BlisteringFormationMotion implements SummonedSwordMotionController.Motion {
   @Nullable
   @Override
   public SummonedSwordTransform sample(SummonedSwordMotionController controller, DMCSummonedSwordEntity sword, boolean previous) {
      if (controller instanceof BlisteringBladesEntity blistering) {
         LivingEntity owner = blistering.getOwner();
         int index = sword.getFormationIndex();
         int total = blistering.getTotalSwords();
         if (owner != null && index >= 0 && index < total) {
            int totalRows = (int)Math.ceil((double)total / 2.0);
            boolean left = index < totalRows;
            int verticalIndex = index % totalRows;
            Vec3 base = previous ? new Vec3(owner.f_19854_, owner.f_19855_, owner.f_19856_) : owner.m_20182_();
            float yaw = previous ? owner.f_20886_ : owner.m_6080_();
            float pitch = previous ? owner.f_19860_ : owner.m_146909_();
            Vec3 position = calculatePosition(owner, left, verticalIndex, totalRows, base, yaw, pitch);
            LivingEntity target = blistering.getTarget();
            if (target != null && target.m_6084_()) {
               Vec3 targetPosition = previous
                  ? new Vec3(target.f_19854_, target.f_19855_ + (double)target.m_20206_() * 0.7, target.f_19856_)
                  : target.m_20182_().m_82520_(0.0, (double)target.m_20206_() * 0.7, 0.0);
               Vec3 direction = targetPosition.m_82546_(position).m_82541_();
               float targetYaw = (float)Math.toDegrees(Mth.m_14136_(direction.f_82481_, direction.f_82479_)) - 90.0F;
               float targetPitch = (float)(-Math.toDegrees(Mth.m_14136_(direction.f_82480_, direction.m_165924_())));
               return new SummonedSwordTransform(position, targetYaw, targetPitch);
            } else {
               return new SummonedSwordTransform(position, yaw, pitch);
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static Vec3 calculatePosition(LivingEntity owner, boolean left, int verticalIndex, int totalRows, Vec3 base, float yaw, float pitch) {
      double sideOffset = left ? -0.95 : 0.95;
      double middle = (double)(totalRows - 1) / 2.0;
      double verticalOffset = ((double)verticalIndex - middle) * 0.35;
      double forwardOffset = (middle - (double)verticalIndex) * 0.2;
      float yawRadians = (float)Math.toRadians((double)yaw);
      Vec3 right = new Vec3(-Math.cos((double)yawRadians), 0.0, -Math.sin((double)yawRadians));
      float pitchRadians = (float)Math.toRadians((double)pitch);
      Vec3 look = new Vec3(
         -Math.sin((double)yawRadians) * Math.cos((double)pitchRadians),
         -Math.sin((double)pitchRadians),
         Math.cos((double)yawRadians) * Math.cos((double)pitchRadians)
      );
      return base.m_82549_(right.m_82490_(sideOffset))
         .m_82520_(0.0, (double)owner.m_20192_() + verticalOffset - 0.4, 0.0)
         .m_82549_(look.m_82490_(forwardOffset - 0.85));
   }
}
