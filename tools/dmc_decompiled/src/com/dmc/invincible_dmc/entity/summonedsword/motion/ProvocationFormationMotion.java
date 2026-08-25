package com.dmc.invincible_dmc.entity.summonedsword.motion;

import com.dmc.invincible_dmc.entity.summonedsword.DMCSummonedSwordEntity;
import com.dmc.invincible_dmc.entity.summonedsword.ProvocationBladesEntity;
import javax.annotation.Nullable;
import net.minecraft.world.phys.Vec3;

public final class ProvocationFormationMotion implements SummonedSwordMotionController.Motion {
   @Nullable
   @Override
   public SummonedSwordTransform sample(SummonedSwordMotionController controller, DMCSummonedSwordEntity sword, boolean previous) {
      if (controller instanceof ProvocationBladesEntity provocation) {
         Vec3 controllerPosition = provocation.m_20182_();
         return new SummonedSwordTransform(controllerPosition.m_82549_(sword.getMotionOffset()), sword.getMotionYaw(), sword.getMotionPitch());
      } else {
         return null;
      }
   }
}
