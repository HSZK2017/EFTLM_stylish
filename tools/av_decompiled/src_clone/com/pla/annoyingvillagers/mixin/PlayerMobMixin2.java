package com.pla.annoyingvillagers.mixin;

import com.pla.annoyingvillagers.entity.PlayerNpcEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import se.gory_moon.player_mobs.entity.PlayerMobEntity;

@Mixin(
   value = {PlayerMobEntity.class},
   remap = false
)
public class PlayerMobMixin2 {
   @Inject(
      method = {"setCombatTask"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void cancelCombatTaskForPlayerNpc(CallbackInfo ci) {
      if (this instanceof PlayerNpcEntity) {
         ci.cancel();
      }
   }
}
