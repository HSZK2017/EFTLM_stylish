package com.hm.efn.mixin;

import com.hm.efn.registries.EFNMobEffectRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(
   value = {AnimationPlayer.class},
   remap = false
)
public abstract class AnimationPlayerMixin {
   @Shadow
   protected float elapsedTime;
   @Shadow
   protected float prevElapsedTime;

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void tick(LivingEntityPatch<?> entitypatch, CallbackInfo ci) {
      try {
         if (entitypatch == null || entitypatch.getOriginal() == null) {
            return;
         }

         if (((LivingEntity)entitypatch.getOriginal()).m_21023_((MobEffect)EFNMobEffectRegistry.STOP.get())) {
            ci.cancel();
            this.prevElapsedTime = this.elapsedTime;
         }
      } catch (Exception var4) {
      }
   }
}
