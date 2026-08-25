package org.merlin204.mimic.worldgen;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

public class WraithonWorldGenProvider extends DatapackBuiltinEntriesProvider {
   public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
      .m_254916_(Registries.f_256787_, WraithonDimensions::bootstrapType)
      .m_254916_(Registries.f_256932_, WraithonNoiseSettings::bootstrap)
      .m_254916_(Registries.f_256952_, WraithonBiomes::boostrap)
      .m_254916_(Registries.f_256862_, WraithonDimensions::bootstrapStem);

   public WraithonWorldGenProvider(PackOutput output, CompletableFuture<Provider> registries) {
      super(output, registries, BUILDER, Set.of("mimic"));
   }
}
