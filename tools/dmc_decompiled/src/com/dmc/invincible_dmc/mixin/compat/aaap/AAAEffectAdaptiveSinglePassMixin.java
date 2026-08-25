package com.dmc.invincible_dmc.mixin.compat.aaap;

import com.dmc.invincible_dmc.client.config.AAAPPerformanceClientConfig;
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
public abstract class AAAEffectAdaptiveSinglePassMixin {
   @Inject(
      method = {"draw(Lmod/chloeprime/aaaparticles/api/client/effekseer/EffekseerManager;Ljava/util/List;Lcom/guhao/vix/client/aaaeffect/AAAEffectPostProcessDispatcher$DrawOperation;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void invincibleDmc$useSinglePassWhenOverloaded(
      EffekseerManager manager, List<ParticleEmitter> activeEmitters, DrawOperation original, CallbackInfo ci
   ) {
      if (AAAPPerformanceClientConfig.isEnabled(AAAPPerformanceClientConfig.ADAPTIVE_VIX_SINGLE_PASS)
         && manager.getImpl().GetTotalInstanceCount() >= (Integer)AAAPPerformanceClientConfig.VIX_SINGLE_PASS_INSTANCE_THRESHOLD.get()) {
         original.call(manager);
         ci.cancel();
      }
   }
}
