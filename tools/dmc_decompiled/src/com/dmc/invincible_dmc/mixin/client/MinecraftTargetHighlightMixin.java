package com.dmc.invincible_dmc.mixin.client;

import com.dmc.invincible_dmc.entity.summonedsword.DMCSummonedSwordEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {Minecraft.class},
   priority = 2000
)
public abstract class MinecraftTargetHighlightMixin {
   @Inject(
      method = {"shouldEntityAppearGlowing"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void invincibleDmc$disableSummonedSwordHighlight(Entity entity, CallbackInfoReturnable<Boolean> callbackInfo) {
      if (entity instanceof DMCSummonedSwordEntity) {
         callbackInfo.setReturnValue(false);
         callbackInfo.cancel();
      }
   }
}
