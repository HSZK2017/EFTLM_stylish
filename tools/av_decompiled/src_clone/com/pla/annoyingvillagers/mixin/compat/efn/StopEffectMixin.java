package com.pla.annoyingvillagers.mixin.compat.efn;

import com.hm.efn.mobeffects.StopEffect;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.pla.annoyingvillagers.util.CommonUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {StopEffect.class},
   remap = false
)
public abstract class StopEffectMixin {
   @Inject(
      method = {"applyEffectTick"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void makeAvMobUnstoppable(LivingEntity owner, int lv, CallbackInfo ci) {
      if (CommonUtil.isAvRunawayJudgementCutEndMob(owner)) {
         if (!owner.m_9236_().m_5776_()) {
            owner.m_21195_((MobEffect)EFNMobEffectRegistry.STOP.get());
         }

         ci.cancel();
      }
   }
}
