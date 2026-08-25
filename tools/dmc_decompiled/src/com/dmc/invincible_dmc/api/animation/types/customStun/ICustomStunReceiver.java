package com.dmc.invincible_dmc.api.animation.types.customStun;

import javax.annotation.Nullable;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;

public interface ICustomStunReceiver {
   void invincible$setPendingCustomStunAnimation(@Nullable AnimationAccessor<? extends StaticAnimation> var1);

   @Nullable
   AnimationAccessor<? extends StaticAnimation> invincible$getPendingCustomStunAnimation();

   void invincible$clearPendingCustomStunAnimation();

   void invincible$setPendingAttackerPos(@Nullable Vec3 var1);

   @Nullable
   Vec3 invincible$getPendingAttackerPos();

   void invincible$setPendingVerticalOffset(float var1);

   float invincible$getPendingVerticalOffset();
}
