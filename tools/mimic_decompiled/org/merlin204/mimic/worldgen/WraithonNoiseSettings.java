package org.merlin204.mimic.worldgen;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class WraithonNoiseSettings {
   public static final ResourceKey<NoiseGeneratorSettings> AIR = createNoiseGeneratorKey("air");

   private static ResourceKey<NoiseGeneratorSettings> createNoiseGeneratorKey(String name) {
      return ResourceKey.m_135785_(Registries.f_256932_, ResourceLocation.fromNamespaceAndPath("mimic", name));
   }

   public static void bootstrap(BootstapContext<NoiseGeneratorSettings> context) {
      context.m_255272_(
         AIR,
         new NoiseGeneratorSettings(
            new NoiseSettings(16, 16, 1, 1),
            Blocks.f_50016_.m_49966_(),
            Blocks.f_50016_.m_49966_(),
            new NoiseRouter(
               DensityFunctions.m_208263_(),
               DensityFunctions.m_208263_(),
               DensityFunctions.m_208263_(),
               DensityFunctions.m_208263_(),
               DensityFunctions.m_208263_(),
               DensityFunctions.m_208263_(),
               DensityFunctions.m_208263_(),
               DensityFunctions.m_208263_(),
               DensityFunctions.m_208263_(),
               DensityFunctions.m_208263_(),
               DensityFunctions.m_208263_(),
               DensityFunctions.m_208263_(),
               DensityFunctions.m_208263_(),
               DensityFunctions.m_208263_(),
               DensityFunctions.m_208263_()
            ),
            SurfaceRules.m_189390_(Blocks.f_50016_.m_49966_()),
            List.of(),
            0,
            true,
            false,
            false,
            true
         )
      );
   }
}
