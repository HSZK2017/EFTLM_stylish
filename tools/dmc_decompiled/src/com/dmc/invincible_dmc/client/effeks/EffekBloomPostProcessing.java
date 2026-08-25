package com.dmc.invincible_dmc.client.effeks;

import com.dmc.invincible_dmc.DMConfig;
import com.guhao.vix.client.aaaeffect.AAAEffectBloomPostProcessor;
import com.guhao.vix.client.aaaeffect.AAAEffectPostProcessContext;
import com.guhao.vix.client.aaaeffect.AAAEffectPostProcessRegistry;
import com.guhao.vix.client.aaaeffect.AAAEffectPostProcessor;
import com.guhao.vix.client.aaaeffect.AAAEffectPostProcessor.OriginalRenderMode;
import com.mojang.blaze3d.pipeline.RenderTarget;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class EffekBloomPostProcessing {
   private static final AAAEffectPostProcessor BLOOM = createBloom(() -> EffekConfig.isEnabled("bloom_post_processing", DMConfig.AAA_EFFECT_BLOOM));
   private static final AAAEffectPostProcessor SPARK_BLOOM = createBloom(
      () -> EffekConfig.isEnabled("bloom_post_processing", DMConfig.AAA_EFFECT_BLOOM) && (Boolean)DMConfig.AAA_EFFECT_SPARK_BLOOM.get()
   );
   private static final AAAEffectPostProcessor SDT_SPARK_BLOOM = createBloom(
      () -> EffekConfig.isEnabled("bloom_post_processing", DMConfig.AAA_EFFECT_BLOOM) && (Boolean)DMConfig.AAA_EFFECT_SDT_SPARK_BLOOM.get()
   );
   private static final List<ResourceLocation> BLOOM_EFFECTS = List.of(
      ComboSlashEffek.SLASHEFFEK,
      Door1Effek.DOOREFFEK,
      Door2Effek.DOOREFFEK,
      DanceBSlashEffek.SLASHEFFEK,
      RushSlashEffek.SLASHEFFEK,
      FastSlashEffek.SLASHEFFEK,
      FlashEffek.FLASHEFFEK,
      FlashPointEffek.FLASHPOINTEFFEK,
      FlashSmallEffek.FLASHEFFEK,
      LightSlashEffek.LIGHTSLASHEFFEK,
      MeteorEffek.METEOREFFEK,
      NormalAdjustSlashEffek.SLASHEFFEK,
      NormalSlashEffek.SLASHEFFEK,
      SlowerSlashEffek.SLASHEFFEK,
      SlowSlashEffek.SLASHEFFEK,
      VoidSlashEffek.SLASHEFFEK,
      DanceBSlashEffek.SLASHEFFEK,
      RushSlashEffek.SLASHEFFEK
   );

   private EffekBloomPostProcessing() {
   }

   public static void register() {
      BLOOM_EFFECTS.forEach(effectId -> AAAEffectPostProcessRegistry.register(effectId, BLOOM));
      AAAEffectPostProcessRegistry.register(SparkEffek.SPARKEFFEK, SPARK_BLOOM);
      AAAEffectPostProcessRegistry.register(SDT_SparkEffek.SDT_FIRE1_EFFEK, SDT_SPARK_BLOOM);
   }

   private static AAAEffectPostProcessor createBloom(BooleanSupplier enabled) {
      return new EffekBloomPostProcessing.ConfigurableBloomPostProcessor(
         new AAAEffectBloomPostProcessor()
            .threshold(0.85F)
            .softKnee(2.0F)
            .intensity(2.5F)
            .blurRadius(5)
            .bloomBase(0.08F)
            .downsampleScale(4)
            .bloomThresholds(0.2F, 1.0F),
         enabled
      );
   }

   private static record ConfigurableBloomPostProcessor(AAAEffectBloomPostProcessor delegate, BooleanSupplier enabled) implements AAAEffectPostProcessor {
      public OriginalRenderMode originalRenderMode() {
         return this.delegate.originalRenderMode();
      }

      public boolean isEnabled() {
         return this.enabled.getAsBoolean();
      }

      public void process(AAAEffectPostProcessContext context, RenderTarget isolatedTarget, RenderTarget sceneTarget) {
         this.delegate.process(context, isolatedTarget, sceneTarget);
      }

      public void onResourceReload(ResourceManager resourceManager) {
         this.delegate.onResourceReload(resourceManager);
      }

      public void close() {
         this.delegate.close();
      }
   }
}
