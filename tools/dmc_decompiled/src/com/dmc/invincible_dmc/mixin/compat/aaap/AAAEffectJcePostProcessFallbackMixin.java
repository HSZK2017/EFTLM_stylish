package com.dmc.invincible_dmc.mixin.compat.aaap;

import com.dmc.invincible_dmc.client.config.AAAPPerformanceClientConfig;
import com.dmc.invincible_dmc.client.effeks.JudgementCutEffectBudget;
import com.guhao.vix.client.aaaeffect.AAAEffectPostProcessDispatcher;
import com.guhao.vix.client.aaaeffect.AAAEffectPostProcessDispatcher.DrawOperation;
import java.util.List;
import mod.chloeprime.aaaparticles.api.client.effekseer.EffekseerManager;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {AAAEffectPostProcessDispatcher.class},
   remap = false
)
public abstract class AAAEffectJcePostProcessFallbackMixin {
   @Inject(
      method = {"draw(Lmod/chloeprime/aaaparticles/api/client/effekseer/EffekseerManager;Ljava/util/List;Lcom/guhao/vix/client/aaaeffect/AAAEffectPostProcessDispatcher$DrawOperation;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void invincibleDmc$disablePostProcessingDuringJudgementCutEnd(
      EffekseerManager manager, List<ParticleEmitter> activeEmitters, DrawOperation original, CallbackInfo ci
   ) {
      if (AAAPPerformanceClientConfig.isEnabled(AAAPPerformanceClientConfig.DISABLE_VIX_POST_PROCESSING_DURING_JCE) && JudgementCutEffectBudget.isBurstActive()
         )
       {
         original.call(manager);
         ci.cancel();
      }
   }
}
