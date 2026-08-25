package com.dmc.invincible_dmc.mixin.thirdperson;

import com.github.leawind.thirdperson.ThirdPersonEvents;
import com.github.leawind.thirdperson.api.client.event.CalculateMoveImpulseEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.client.camera.EpicFightCameraAPI;

@Mixin(
   value = {ThirdPersonEvents.class},
   remap = false
)
public class ThirdPersonEventsMixin {
   @Inject(
      method = {"onCalculateMoveImpulse"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void invincible_dmc$skipRemapIfEFActive(CalculateMoveImpulseEvent event, CallbackInfo ci) {
      EpicFightCameraAPI api = EpicFightCameraAPI.getInstance();
      if (api.isLockingOnTarget() || api.isTPSMode()) {
         ci.cancel();
      }
   }
}
