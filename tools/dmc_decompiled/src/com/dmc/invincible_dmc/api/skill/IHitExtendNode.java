package com.dmc.invincible_dmc.api.skill;

import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.asset.AssetAccessor;

public interface IHitExtendNode {
   int DEFAULT_MINIMUM_HOLD_TICKS = 3;

   @Nullable
   SubComboNode getBase();

   @Nullable
   SubComboNode getExtend();

   default int getMinimumHoldTicks() {
      return 3;
   }

   default boolean shouldStabilizeContact() {
      return false;
   }

   default boolean matchesBaseAnimation(@Nullable AssetAccessor<?> animation) {
      SubComboNode base = this.getBase();
      AssetAccessor<?> baseAnimation = base != null ? base.getAnimationAccessor() : null;
      return baseAnimation == animation
         ? baseAnimation != null
         : baseAnimation != null && animation != null && baseAnimation.registryName() != null && baseAnimation.registryName().equals(animation.registryName());
   }
}
