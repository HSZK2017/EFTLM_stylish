package com.dmc.invincible_dmc.entity.summonedsword.motion;

import com.dmc.invincible_dmc.entity.summonedsword.DMCSummonedSwordEntity;
import com.dmc.invincible_dmc.entity.summonedsword.HeavyRainBladesEntity;
import javax.annotation.Nullable;
import net.minecraft.world.phys.Vec3;

public final class HeavyRainFormationMotion implements SummonedSwordMotionController.Motion {
   @Nullable
   @Override
   public SummonedSwordTransform sample(SummonedSwordMotionController controller, DMCSummonedSwordEntity sword, boolean previous) {
      if (controller instanceof HeavyRainBladesEntity heavyRain) {
         Vec3 controllerPosition = heavyRain.m_20182_();
         return new SummonedSwordTransform(controllerPosition.m_82549_(sword.getMotionOffset()), 0.0F, 90.0F);
      } else {
         return null;
      }
   }
}
