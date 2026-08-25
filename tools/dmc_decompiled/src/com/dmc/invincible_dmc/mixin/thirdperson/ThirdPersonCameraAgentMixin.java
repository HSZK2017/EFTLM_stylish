package com.dmc.invincible_dmc.mixin.thirdperson;

import com.dmc.invincible_dmc.client.render.shake.CameraShakeManager;
import com.github.leawind.thirdperson.api.client.event.ThirdPersonCameraSetupEvent;
import com.github.leawind.thirdperson.core.CameraAgent;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.client.camera.EpicFightCameraAPI;

@Mixin(
   value = {CameraAgent.class},
   remap = false
)
public abstract class ThirdPersonCameraAgentMixin {
   @Inject(
      method = {"onCameraSetup"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void invincible_dmc$skipCameraSetupIfTps(ThirdPersonCameraSetupEvent event, CallbackInfo ci) {
      if (EpicFightCameraAPI.getInstance().isTPSMode()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"onCameraSetup"},
      at = {@At("RETURN")}
   )
   private void invincible_dmc$applyShakeAfterCameraSetup(ThirdPersonCameraSetupEvent event, CallbackInfo ci) {
      if (!EpicFightCameraAPI.getInstance().isTPSMode() && event.pos != null) {
         Camera camera = Minecraft.m_91087_().f_91063_.m_109153_();
         Vec3 shakeOffset = CameraShakeManager.getAccumulatedOffset(camera, event.partialTick);
         if (shakeOffset != Vec3.f_82478_) {
            event.setPosition(event.pos.m_82549_(shakeOffset));
         }
      }
   }

   @Inject(
      method = {"onRenderTickStart"},
      at = {@At("TAIL")}
   )
   private void invincible_dmc$syncRelativeRotationForLockOn(double now, double period, float partialTick, CallbackInfo ci) {
      EpicFightCameraAPI api = EpicFightCameraAPI.getInstance();
      if (api.isLockingOnTarget() && !api.isTPSMode()) {
         CameraAgent self = (CameraAgent)this;
         float interpXRot = Mth.m_14189_(partialTick, api.getCameraXRotO(), api.getCameraXRot());
         float interpYRot = Mth.m_14189_(partialTick, api.getCameraYRotO(), api.getCameraYRot());
         float targetXRot = -interpXRot;
         float targetYRot = interpYRot - 180.0F;
         Vector2d current = self.getRelativeRotation();
         float xDiff = Math.abs(Mth.m_14177_(targetXRot - (float)current.x));
         float yDiff = Math.abs(Mth.m_14177_(targetYRot - (float)current.y));
         float xFactor = Math.min(xDiff / 60.0F, 1.0F) * 0.7F + 0.15F;
         float yFactor = Math.min(yDiff / 60.0F, 1.0F) * 0.7F + 0.15F;
         float newXRot = Mth.m_14189_(xFactor, (float)current.x, targetXRot);
         float newYRot = Mth.m_14189_(yFactor, (float)current.y, targetYRot);
         self.getRelativeRotation().set((double)newXRot, (double)newYRot);
      }
   }
}
