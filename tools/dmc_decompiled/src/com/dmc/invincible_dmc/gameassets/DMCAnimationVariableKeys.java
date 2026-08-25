package com.dmc.invincible_dmc.gameassets;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.api.animation.SynchedAnimationVariableKey;
import yesman.epicfight.api.animation.SynchedAnimationVariableKey.SynchedIndependentAnimationVariableKey;
import yesman.epicfight.api.utils.PacketBufferCodec;

public final class DMCAnimationVariableKeys {
   public static final DeferredRegister<SynchedAnimationVariableKey<?>> KEYS = DeferredRegister.create(
      ResourceLocation.fromNamespaceAndPath("epicfight", "synched_animation_variable_keys"), "invincible_dmc"
   );
   public static final RegistryObject<SynchedIndependentAnimationVariableKey<Integer>> YAMATO_IDLE_STATE = KEYS.register(
      "yamato_idle_state", () -> SynchedAnimationVariableKey.independent(animator -> 0, true, PacketBufferCodec.INTEGER)
   );

   private DMCAnimationVariableKeys() {
   }
}
