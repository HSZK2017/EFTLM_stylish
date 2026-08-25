package com.dmc.invincible_dmc.mixin.client;

import com.dmc.invincible_dmc.client.render.weather.JudgementCutRainFreezeController;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LevelRenderer.class})
public abstract class LevelRendererRainFreezeMixin {
   @Shadow
   private int f_109477_;

   @WrapMethod(
      method = {"renderSnowAndRain"}
   )
   private void invincibleDmc$freezeRainFall(
      LightTexture lightTexture, float partialTick, double cameraX, double cameraY, double cameraZ, Operation<Void> original
   ) {
      ClientLevel level = Minecraft.m_91087_().f_91073_;
      if (level == null) {
         original.call(new Object[]{lightTexture, partialTick, cameraX, cameraY, cameraZ});
      } else {
         double visualTime = JudgementCutRainFreezeController.updateVisualTime(
            level, new Vec3(cameraX, cameraY, cameraZ), (double)((float)this.f_109477_ + partialTick)
         );
         int originalTicks = this.f_109477_;
         int visualTicks = Mth.m_14107_(visualTime);
         this.f_109477_ = visualTicks;

         try {
            original.call(new Object[]{lightTexture, (float)(visualTime - (double)visualTicks), cameraX, cameraY, cameraZ});
         } finally {
            this.f_109477_ = originalTicks;
         }
      }
   }

   @Inject(
      method = {"tickRain"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void invincibleDmc$pauseRainImpacts(Camera camera, CallbackInfo ci) {
      ClientLevel level = Minecraft.m_91087_().f_91073_;
      if (level != null && JudgementCutRainFreezeController.shouldFreeze(level, camera.m_90583_())) {
         ci.cancel();
      }
   }
}
