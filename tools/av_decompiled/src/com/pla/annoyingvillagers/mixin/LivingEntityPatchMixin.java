package com.pla.annoyingvillagers.mixin;

import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import java.util.Objects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.StunType;

@Mixin(
   value = {LivingEntityPatch.class},
   remap = false
)
public abstract class LivingEntityPatchMixin {
   @Inject(
      method = {"applyStun"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void preventStunOveride(StunType stunType, float stunTime, CallbackInfoReturnable<Boolean> cir) {
      LivingEntityPatch<?> self = (LivingEntityPatch<?>)this;
      AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(self.getAnimator().getPlayerFor(null)).getRealAnimation();
      if ((stunType == StunType.SHORT || stunType == StunType.LONG || stunType == StunType.HOLD)
         && (
            dynamicAnimation == AnimsPugilistSteve.HIT_BACKWARD
               || dynamicAnimation == AnimsPugilistSteve.HIT_LEFT
               || dynamicAnimation == AnimsPugilistSteve.HIT_RIGHT
         )) {
         ((LivingEntity)self.getOriginal()).f_20900_ = 0.0F;
         ((LivingEntity)self.getOriginal()).f_20901_ = 0.0F;
         ((LivingEntity)self.getOriginal()).f_20902_ = 0.0F;
         ((LivingEntity)self.getOriginal()).m_20334_(0.0, 0.0, 0.0);
         cir.setReturnValue(true);
         cir.cancel();
      }
   }
}
