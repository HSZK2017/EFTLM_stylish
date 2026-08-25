package com.dmc.invincible_dmc.capability.item;

import net.minecraft.world.entity.player.Player;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

public final class WeaponMovementMechanics {
   private WeaponMovementMechanics() {
   }

   public static boolean preventsCrouching(Player player) {
      if (EpicFightCapabilities.getItemStackCapability(player.m_21205_()) instanceof AdvanceWeaponCapability capability && capability.preventsCrouching()) {
         return true;
      }

      return false;
   }
}
