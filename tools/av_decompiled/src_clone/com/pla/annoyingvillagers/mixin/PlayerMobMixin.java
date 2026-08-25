package com.pla.annoyingvillagers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import se.gory_moon.player_mobs.entity.PlayerMobEntity;

@Mixin(
   value = {PlayerMobEntity.class},
   remap = true
)
public class PlayerMobMixin {
   @Inject(
      method = {"isBaby"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void forceIsNotBaby(CallbackInfoReturnable<Boolean> cir) {
      cir.setReturnValue(false);
   }

   @Inject(
      method = {"setBaby"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void blockSetBaby(boolean isChild, CallbackInfo ci) {
      ci.cancel();
   }
}
