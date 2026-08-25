package com.dmc.invincible_dmc.mixin;

import com.dmc.invincible_dmc.gameassets.DMCEffects;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {KeyboardHandler.class},
   priority = 99999999
)
public class MixinKeyboardHandler {
   @Inject(
      at = {@At("HEAD")},
      method = {"keyPress(JIIII)V"},
      cancellable = true
   )
   public void dmc$keyPress(long screen, int key, int scanCode, int action, int modifier, CallbackInfo callback) {
      Minecraft mc = Minecraft.m_91087_();
      if (mc.f_91080_ == null) {
         LocalPlayer player = mc.f_91074_;
         if (player != null && DMCEffects.STOP.isPresent() && player.m_21023_((MobEffect)DMCEffects.STOP.get())) {
            callback.cancel();
         }
      }
   }
}
