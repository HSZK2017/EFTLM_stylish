package com.dmc.invincible_dmc.mixin.epicfight;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.AnimationManager;

@Mixin(
   value = {AnimationManager.class},
   remap = false
)
public abstract class AnimationManagerMixin {
   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void invincible_dmc$fixAnimationsConcurrency(CallbackInfo ci) {
      try {
         Field field = AnimationManager.class.getDeclaredField("animations");
         field.setAccessible(true);
         Field modifiersField = Field.class.getDeclaredField("modifiers");
         modifiersField.setAccessible(true);
         modifiersField.setInt(field, field.getModifiers() & -17);
         field.set(this, new ConcurrentHashMap());
      } catch (Exception var4) {
      }
   }
}
