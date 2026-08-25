package com.dmc.invincible_dmc.mixin;

import com.dmc.invincible_dmc.capability.item.WeaponMovementMechanics;
import com.dmc.invincible_dmc.client.DMCKeyMappings;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({Player.class})
public abstract class PlayerCrouchPoseMixin {
   @ModifyExpressionValue(
      method = {"updatePlayerPose"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;isShiftKeyDown()Z"
      )}
   )
   private boolean invincible$preventWeaponCrouchPose(boolean shiftKeyDown) {
      Player player = (Player)this;
      return shiftKeyDown && (!DMCKeyMappings.isLockOnBoundToCrouch() || !WeaponMovementMechanics.preventsCrouching(player));
   }
}
