package com.hm.efn.mixin;

import com.hm.efn.animations.types.EFNGuardAnimation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin({ActionAnimation.class})
public class ActionAnimationMixin {
   @Redirect(
      method = {"begin"},
      at = @At(
         value = "INVOKE",
         target = "Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;cancelItemUse()V"
      ),
      remap = false
   )
   private void onCancelItemUse(LivingEntityPatch<?> instance) {
      if (!((ActionAnimation)this instanceof EFNGuardAnimation)) {
         instance.cancelItemUse();
      }
   }
}
