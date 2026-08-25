package com.dmc.invincible_dmc.api.animation.types.customStun;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;

public interface ICustomStunDamageSource {
   void invincible$setCustomStunAnimations(
      Supplier<? extends StaticAnimation> var1,
      @Nullable Supplier<? extends StaticAnimation> var2,
      Supplier<? extends StaticAnimation> var3,
      @Nullable Supplier<? extends StaticAnimation> var4
   );

   void invincible$setCustomStunAnimationsSDT(
      @Nullable Supplier<? extends StaticAnimation> var1,
      @Nullable Supplier<? extends StaticAnimation> var2,
      @Nullable Supplier<? extends StaticAnimation> var3,
      @Nullable Supplier<? extends StaticAnimation> var4
   );

   boolean invincible$hasCustomStunAnimations();

   boolean invincible$hasCustomStunAnimationsSDT();

   void invincible$setCustomStunVerticalOffset(float var1);

   float invincible$getCustomStunVerticalOffset();

   void invincible$setCustomStunSourceYRot(@Nullable Float var1);

   @Nullable
   Float invincible$getCustomStunSourceYRot();

   @Nullable
   AnimationAccessor<? extends StaticAnimation> invincible$resolveCustomStunAnimation(LivingEntity var1, boolean var2);

   @Nullable
   default AnimationAccessor<? extends StaticAnimation> invincible$resolveCustomStunAnimation(LivingEntity target) {
      return this.invincible$resolveCustomStunAnimation(target, false);
   }
}
