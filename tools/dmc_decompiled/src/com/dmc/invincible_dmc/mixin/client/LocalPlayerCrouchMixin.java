package com.dmc.invincible_dmc.mixin.client;

import com.dmc.invincible_dmc.capability.item.WeaponMovementMechanics;
import com.dmc.invincible_dmc.client.DMCKeyMappings;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({LocalPlayer.class})
public abstract class LocalPlayerCrouchMixin {
   @ModifyExpressionValue(
      method = {"aiStep"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/LocalPlayer;isShiftKeyDown()Z"
      )}
   )
   private boolean invincible$preventWeaponCrouchState(boolean shiftKeyDown) {
      LocalPlayer player = (LocalPlayer)this;
      return shiftKeyDown && (!DMCKeyMappings.isLockOnBoundToCrouch() || !WeaponMovementMechanics.preventsCrouching(player));
   }
}
