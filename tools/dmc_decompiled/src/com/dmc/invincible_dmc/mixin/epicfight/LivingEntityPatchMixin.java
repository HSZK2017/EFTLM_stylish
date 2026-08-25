package com.dmc.invincible_dmc.mixin.epicfight;

import com.dmc.invincible_dmc.api.animation.ArmatureCompatibilityValidator;
import com.dmc.invincible_dmc.api.animation.RuntimeArmaturePatch;
import com.dmc.invincible_dmc.api.animation.RuntimeArmatureProfile;
import com.dmc.invincible_dmc.api.animation.types.customStun.ICustomStunReceiver;
import com.dmc.invincible_dmc.api.stun.StrongStunController;
import com.dmc.invincible_dmc.gameassets.DMCRuntimeArmatures;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.model.armature.types.ToolHolderArmature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.StunType;

@Mixin(
   value = {LivingEntityPatch.class},
   remap = false,
   priority = 1918
)
public abstract class LivingEntityPatchMixin implements RuntimeArmaturePatch {
   @Shadow
   protected Armature armature;
   @Unique
   private ResourceLocation invincible$runtimeArmatureProfile = DMCRuntimeArmatures.BIPED_ID;
   @Unique
   private boolean invincible$forceZeroTransition;

   @Shadow
   public abstract void setParentJointOfHand(InteractionHand var1, Joint var2);

   @Inject(
      method = {"applyStun"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void invincible$blockStunDuringExecution(StunType stunType, float stunTime, CallbackInfoReturnable<Boolean> callbackInfo) {
      LivingEntityPatch<?> self = (LivingEntityPatch<?>)this;
      LivingEntity target = (LivingEntity)self.getOriginal();
      if (StrongStunController.isActive(target)) {
         StrongStunController.freeze(target);
         if (self instanceof ICustomStunReceiver receiver) {
            receiver.invincible$clearPendingCustomStunAnimation();
         }

         callbackInfo.setReturnValue(false);
      }
   }

   @WrapOperation(
      method = {"applyStun"},
      at = {@At(
         value = "INVOKE",
         target = "Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;getHitAnimation(Lyesman/epicfight/world/damagesource/StunType;)Lyesman/epicfight/api/asset/AssetAccessor;"
      )}
   )
   private AssetAccessor invincible$wrapGetHitAnimation(LivingEntityPatch<?> self, StunType stunType, Operation<AssetAccessor> original) {
      if (self instanceof ICustomStunReceiver receiver) {
         AnimationAccessor<? extends StaticAnimation> pending = receiver.invincible$getPendingCustomStunAnimation();
         if (pending != null) {
            Vec3 attackerPos = receiver.invincible$getPendingAttackerPos();
            float verticalOffset = receiver.invincible$getPendingVerticalOffset();
            receiver.invincible$clearPendingCustomStunAnimation();
            LivingEntity entity = (LivingEntity)self.getOriginal();
            if (verticalOffset != 0.0F) {
               entity.m_6478_(MoverType.SELF, new Vec3(0.0, (double)verticalOffset, 0.0));
            }

            if (attackerPos != null && !(entity instanceof Player)) {
               entity.m_7618_(Anchor.FEET, attackerPos);
            }

            this.invincible$forceZeroTransition = true;
            return pending;
         }
      }

      this.invincible$forceZeroTransition = false;
      return (AssetAccessor)original.call(new Object[]{self, stunType});
   }

   @ModifyExpressionValue(
      method = {"applyStun"},
      at = {@At(
         value = "INVOKE",
         target = "Lyesman/epicfight/world/damagesource/StunType;hasFixedStunTime()Z"
      )}
   )
   private boolean invincible$forceZeroTransitionTime(boolean original) {
      return this.invincible$forceZeroTransition || original;
   }

   @Override
   public ResourceLocation invincible$getRuntimeArmatureProfile() {
      return this.invincible$runtimeArmatureProfile;
   }

   @Override
   public void invincible$applyRuntimeArmatureProfile(RuntimeArmatureProfile profile, boolean force) {
      if (force || !profile.id().equals(this.invincible$runtimeArmatureProfile)) {
         Armature nextArmature = ((Armature)profile.armature().get()).deepCopy();
         ArmatureCompatibilityValidator.validate(this.armature, nextArmature);
         this.armature = nextArmature;
         this.invincible$runtimeArmatureProfile = profile.id();
         if (nextArmature instanceof ToolHolderArmature toolHolderArmature) {
            this.setParentJointOfHand(InteractionHand.MAIN_HAND, toolHolderArmature.rightToolJoint());
            this.setParentJointOfHand(InteractionHand.OFF_HAND, toolHolderArmature.leftToolJoint());
         } else {
            this.setParentJointOfHand(InteractionHand.MAIN_HAND, nextArmature.rootJoint);
            this.setParentJointOfHand(InteractionHand.OFF_HAND, nextArmature.rootJoint);
         }
      }
   }
}
