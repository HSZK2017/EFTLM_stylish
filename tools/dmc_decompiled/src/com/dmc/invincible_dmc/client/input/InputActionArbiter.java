package com.dmc.invincible_dmc.client.input;

import com.dmc.invincible_dmc.client.DMCKeyMappings;
import com.mojang.blaze3d.platform.InputConstants.Key;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.input.EpicFightKeyMappings;

@OnlyIn(Dist.CLIENT)
public final class InputActionArbiter {
   private InputActionArbiter() {
   }

   public static InputActionArbiter.Action resolve(Key key) {
      if (matches(DMCKeyMappings.DOPPEL_CONTROL, key)) {
         return InputActionArbiter.Action.DOPPELGANGER_CONTROL;
      } else if (matches(DMCKeyMappings.DOPPEL_FAST, key) || matches(DMCKeyMappings.DOPPEL_MEDIUM, key) || matches(DMCKeyMappings.DOPPEL_SLOW, key)) {
         return InputActionArbiter.Action.DOPPELGANGER_SPEED;
      } else if (matches(DMCKeyMappings.DOPPEL_DISCARD, key)) {
         return InputActionArbiter.Action.DOPPELGANGER_DISCARD;
      } else {
         return !matches(DMCKeyMappings.DMC_LOCK_ON, key) && !matches(EpicFightKeyMappings.LOCK_ON, key)
            ? InputActionArbiter.Action.NONE
            : InputActionArbiter.Action.LOCK_ON;
      }
   }

   private static boolean matches(KeyMapping mapping, Key key) {
      return mapping != null && mapping.isActiveAndMatches(key);
   }

   public static enum Action {
      DOPPELGANGER_CONTROL,
      DOPPELGANGER_SPEED,
      DOPPELGANGER_DISCARD,
      LOCK_ON,
      NONE;
   }
}
