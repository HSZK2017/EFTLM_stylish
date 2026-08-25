package com.dmc.invincible_dmc.client.effeks;

import com.dmc.invincible_dmc.DMConfig;
import com.guhao.vix.client.aaaeffect.AAAEffectChromaticAberrationPostProcessor;
import com.guhao.vix.client.aaaeffect.AAAEffectPostProcessContext;
import com.guhao.vix.client.aaaeffect.AAAEffectPostProcessRegistry;
import com.guhao.vix.client.aaaeffect.AAAEffectPostProcessor;
import com.guhao.vix.client.aaaeffect.AAAEffectPostProcessor.OriginalRenderMode;
import com.mojang.blaze3d.pipeline.RenderTarget;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class EffekChromaticAberrationPostProcessing {
   private static final AAAEffectPostProcessor CHROMATIC_ABERRATION = new EffekChromaticAberrationPostProcessing.ConfigurableChromaticAberrationPostProcessor(
      new AAAEffectChromaticAberrationPostProcessor().offset(12.0F).intensity(2.0F).radialStrength(0.8F)
   );
   private static final List<ResourceLocation> CHROMATIC_ABERRATION_EFFECTS = List.of(LightRingEffek.LIGHTRINGEFFEK, ExecuteEffek.EXECUTE_EFFEK);

   private EffekChromaticAberrationPostProcessing() {
   }

   public static void register() {
      CHROMATIC_ABERRATION_EFFECTS.forEach(effectId -> AAAEffectPostProcessRegistry.register(effectId, CHROMATIC_ABERRATION));
   }

   private static record ConfigurableChromaticAberrationPostProcessor(AAAEffectChromaticAberrationPostProcessor delegate) implements AAAEffectPostProcessor {
      public OriginalRenderMode originalRenderMode() {
         return this.delegate.originalRenderMode();
      }

      public boolean batchAcrossEffectDefinitions() {
         return false;
      }

      public boolean isEnabled() {
         return EffekConfig.isEnabled("chromatic_aberration_post_processing", DMConfig.AAA_EFFECT_CHROMATIC_ABERRATION);
      }

      public void process(AAAEffectPostProcessContext context, RenderTarget isolatedTarget, RenderTarget sceneTarget) {
         this.delegate.process(context, isolatedTarget, sceneTarget);
      }

      public void processScene(
         AAAEffectPostProcessContext context,
         RenderTarget sceneBeforeTarget,
         RenderTarget sceneAfterTarget,
         RenderTarget isolatedTarget,
         RenderTarget sceneTarget
      ) {
         this.delegate.processScene(context, sceneBeforeTarget, sceneAfterTarget, isolatedTarget, sceneTarget);
      }

      public void onResourceReload(ResourceManager resourceManager) {
         this.delegate.onResourceReload(resourceManager);
      }

      public void close() {
         this.delegate.close();
      }
   }
}
