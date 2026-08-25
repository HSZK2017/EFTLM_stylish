package com.dmc.invincible_dmc.mixin;

import com.dmc.invincible_dmc.gameassets.DMCEffects;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {KeyMapping.class},
   priority = 99999999
)
public class MixinKeyMapping {
   @Shadow
   boolean f_90817_;

   @Inject(
      at = {@At("HEAD")},
      method = {"isDown()Z"},
      cancellable = true
   )
   public void dmc$isDown(CallbackInfoReturnable<Boolean> callback) {
      Minecraft mc = Minecraft.m_91087_();
      LocalPlayer player = mc.f_91074_;
      if (player != null && DMCEffects.STOP.isPresent() && player.m_21023_((MobEffect)DMCEffects.STOP.get())) {
         callback.setReturnValue(false);
         callback.cancel();
         if (this.f_90817_) {
            this.f_90817_ = false;
         }
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"consumeClick()Z"},
      cancellable = true
   )
   public void dmc$consumeClick(CallbackInfoReturnable<Boolean> callback) {
      Minecraft mc = Minecraft.m_91087_();
      LocalPlayer player = mc.f_91074_;
      if (player != null && DMCEffects.STOP.isPresent() && player.m_21023_((MobEffect)DMCEffects.STOP.get())) {
         callback.setReturnValue(false);
         callback.cancel();
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"matches"},
      cancellable = true
   )
   public void dmc$matches(int key, int scancode, CallbackInfoReturnable<Boolean> callback) {
      Minecraft mc = Minecraft.m_91087_();
      LocalPlayer player = mc.f_91074_;
      if (player != null && DMCEffects.STOP.isPresent() && player.m_21023_((MobEffect)DMCEffects.STOP.get())) {
         callback.setReturnValue(false);
         callback.cancel();
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"matchesMouse"},
      cancellable = true
   )
   public void dmc$matchesMouse(int button, CallbackInfoReturnable<Boolean> callback) {
      Minecraft mc = Minecraft.m_91087_();
      LocalPlayer player = mc.f_91074_;
      if (player != null && DMCEffects.STOP.isPresent() && player.m_21023_((MobEffect)DMCEffects.STOP.get())) {
         callback.setReturnValue(false);
         callback.cancel();
      }
   }
}
