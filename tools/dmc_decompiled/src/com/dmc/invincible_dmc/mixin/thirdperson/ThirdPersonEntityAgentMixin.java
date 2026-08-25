package com.dmc.invincible_dmc.mixin.thirdperson;

import com.github.leawind.thirdperson.core.EntityAgent;
import net.minecraft.client.player.LocalPlayer;
import org.joml.Vector2d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.client.camera.EpicFightCameraAPI;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

@Mixin(
   value = {EntityAgent.class},
   remap = false
)
public abstract class ThirdPersonEntityAgentMixin {
   @Inject(
      method = {"setRawRotation"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void invincible_dmc$skipSetRawRotationIfEFCameraActive(Vector2d rot, CallbackInfo ci) {
      EpicFightCameraAPI api = EpicFightCameraAPI.getInstance();
      if (!api.isLockingOnTarget() && !api.isTPSMode()) {
         EntityAgent self = (EntityAgent)this;
         LocalPlayer player = self.getRawPlayerEntity();
         LocalPlayerPatch patch = EpicFightCapabilities.getLocalPlayerPatch(player);
         if (patch != null && patch.getEntityState().turningLocked()) {
            ci.cancel();
         }
      } else {
         ci.cancel();
      }
   }
}
