package com.dmc.invincible_dmc.mixin;

import com.dmc.invincible_dmc.gameassets.DMCEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {MouseHandler.class},
   priority = 99999999
)
public class MixinMouseHandler {
   @Inject(
      at = {@At("HEAD")},
      method = {"onPress"},
      cancellable = true
   )
   private void onPress(long screen, int button, int action, int mods, CallbackInfo callback) {
      Minecraft mc = Minecraft.m_91087_();
      if (mc.f_91080_ == null) {
         LocalPlayer player = mc.f_91074_;
         if (player != null && DMCEffects.STOP.isPresent() && player.m_21023_((MobEffect)DMCEffects.STOP.get())) {
            callback.cancel();
         }
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"onMove"},
      cancellable = true
   )
   public void onMove(long p_91562_, double p_91563_, double p_91564_, CallbackInfo callback) {
      Minecraft mc = Minecraft.m_91087_();
      LocalPlayer player = mc.f_91074_;
      if (player != null && mc.f_91080_ == null && DMCEffects.STOP.isPresent() && player.m_21023_((MobEffect)DMCEffects.STOP.get())) {
         callback.cancel();
      }
   }
}
