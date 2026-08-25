package com.pla.annoyingvillagers.mixin;

import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ItemCooldowns.class})
public class ItemCooldownsMixin {
   @Inject(
      method = {"isOnCooldown"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ignoreCooldownForAegis(Item item, CallbackInfoReturnable<Boolean> cir) {
      if (item == AnnoyingVillagersModItems.ENDER_AEGIS.get()) {
         cir.setReturnValue(false);
      }
   }
}
