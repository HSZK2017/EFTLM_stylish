package com.dmc.invincible_dmc.mixin.epicfight;

import com.dmc.invincible_dmc.api.animation.types.customStun.ICustomStunReceiver;
import com.dmc.invincible_dmc.utils.DMCLog;
import javax.annotation.Nullable;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.entitypatch.HurtableEntityPatch;

@Mixin(
   value = {HurtableEntityPatch.class},
   remap = false
)
public class HurtableEntityPatchMixin implements ICustomStunReceiver {
   @Unique
   private AnimationAccessor<? extends StaticAnimation> invincible$pendingCustomStunAnimation;
   @Unique
   private Vec3 invincible$pendingAttackerPos;
   @Unique
   private float invincible$pendingVerticalOffset;

   @Override
   public void invincible$setPendingCustomStunAnimation(@Nullable AnimationAccessor<? extends StaticAnimation> animation) {
      this.invincible$pendingCustomStunAnimation = animation;
      DMCLog.debug(
         DMCLog.Category.STUN,
         "[Stun] RECEIVER_PENDING_SET target={} animation={}",
         ((HurtableEntityPatch)this).getOriginal(),
         animation != null ? animation.registryName() : null
      );
   }

   @Nullable
   @Override
   public AnimationAccessor<? extends StaticAnimation> invincible$getPendingCustomStunAnimation() {
      return this.invincible$pendingCustomStunAnimation;
   }

   @Override
   public void invincible$clearPendingCustomStunAnimation() {
      if (this.invincible$pendingCustomStunAnimation != null) {
         DMCLog.debug(
            DMCLog.Category.STUN,
            "[Stun] RECEIVER_PENDING_CLEAR target={} animation={}",
            ((HurtableEntityPatch)this).getOriginal(),
            this.invincible$pendingCustomStunAnimation.registryName()
         );
      }

      this.invincible$pendingCustomStunAnimation = null;
      this.invincible$pendingAttackerPos = null;
      this.invincible$pendingVerticalOffset = 0.0F;
   }

   @Override
   public void invincible$setPendingAttackerPos(@Nullable Vec3 pos) {
      this.invincible$pendingAttackerPos = pos;
   }

   @Nullable
   @Override
   public Vec3 invincible$getPendingAttackerPos() {
      return this.invincible$pendingAttackerPos;
   }

   @Override
   public void invincible$setPendingVerticalOffset(float verticalOffset) {
      this.invincible$pendingVerticalOffset = verticalOffset;
   }

   @Override
   public float invincible$getPendingVerticalOffset() {
      return this.invincible$pendingVerticalOffset;
   }
}
