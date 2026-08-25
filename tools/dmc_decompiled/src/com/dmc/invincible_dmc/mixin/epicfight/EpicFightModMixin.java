package com.dmc.invincible_dmc.mixin.epicfight;

import com.dmc.invincible_dmc.api.forgeevent.DuplicateAnimationRegistryEvent;
import java.util.Comparator;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.main.EpicFightMod;

@Mixin({EpicFightMod.class})
public class EpicFightModMixin {
   @Inject(
      method = {"constructMod"},
      at = {@At("TAIL")},
      remap = false
   )
   private void invincible_dmc$constructMod(FMLConstructModEvent event, CallbackInfo ci) {
      event.enqueueWork(
         () -> {
            DuplicateAnimationRegistryEvent registryEvent = new DuplicateAnimationRegistryEvent();
            ModLoader.get().postEvent(registryEvent);
            registryEvent.getBuilders()
               .stream()
               .sorted(Comparator.comparing(DuplicateAnimationRegistryEvent.DuplicateAnimationBuilder::namespace))
               .forEach(builder -> builder.task().accept(builder));
         }
      );
   }
}
