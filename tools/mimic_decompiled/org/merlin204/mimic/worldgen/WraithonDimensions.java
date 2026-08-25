package org.merlin204.mimic.worldgen;

import java.util.OptionalLong;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.dimension.DimensionType.MonsterSettings;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class WraithonDimensions {
   public static final ResourceKey<Level> THE_LETHEAN_SEA_LEVEL_KEY = ResourceKey.m_135785_(
      Registries.f_256858_, ResourceLocation.fromNamespaceAndPath("mimic", "the_lethean_sea")
   );
   public static final ResourceKey<LevelStem> THE_LETHEAN_SEA_KEY = ResourceKey.m_135785_(
      Registries.f_256862_, ResourceLocation.fromNamespaceAndPath("mimic", "the_lethean_sea")
   );
   public static final ResourceKey<DimensionType> THE_LETHEAN_SEA_DIM_TYPE = ResourceKey.m_135785_(
      Registries.f_256787_, ResourceLocation.fromNamespaceAndPath("mimic", "the_lethean_sea_type")
   );

   public static void bootstrapType(BootstapContext<DimensionType> context) {
      context.m_255272_(
         THE_LETHEAN_SEA_DIM_TYPE,
         new DimensionType(
            OptionalLong.of(6000L),
            true,
            false,
            false,
            false,
            1.0,
            false,
            true,
            0,
            320,
            256,
            BlockTags.f_13058_,
            BuiltinDimensionTypes.f_223542_,
            1.0F,
            new MonsterSettings(false, false, ConstantInt.m_146483_(0), 0)
         )
      );
   }

   public static void bootstrapStem(BootstapContext<LevelStem> context) {
      HolderGetter<Biome> biomeRegistry = context.m_255420_(Registries.f_256952_);
      HolderGetter<DimensionType> dimTypes = context.m_255420_(Registries.f_256787_);
      HolderGetter<NoiseGeneratorSettings> noiseGenSettings = context.m_255420_(Registries.f_256932_);
      NoiseBasedChunkGenerator chunkGenerator = new NoiseBasedChunkGenerator(
         new FixedBiomeSource(biomeRegistry.m_255043_(WraithonBiomes.THE_LETHEAN_SEA)), noiseGenSettings.m_255043_(WraithonNoiseSettings.AIR)
      );
      LevelStem levelStem = new LevelStem(dimTypes.m_255043_(THE_LETHEAN_SEA_DIM_TYPE), chunkGenerator);
      context.m_255272_(THE_LETHEAN_SEA_KEY, levelStem);
   }
}
