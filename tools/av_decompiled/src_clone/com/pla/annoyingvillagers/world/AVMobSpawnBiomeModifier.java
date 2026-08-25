package com.pla.annoyingvillagers.world;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.BiomeModifier.Phase;
import net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries.Keys;

public final class AVMobSpawnBiomeModifier implements BiomeModifier {
   private static final RegistryObject<Codec<? extends BiomeModifier>> SERIALIZER = RegistryObject.create(
      ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "av_mob_spawns"), Keys.BIOME_MODIFIER_SERIALIZERS, "annoyingvillagers"
   );

   public void modify(Holder<Biome> biomeHolder, Phase phase, Builder builder) {
      if (phase == Phase.ADD) {
         if (biomeHolder.m_203656_(BiomeTags.f_215817_)) {
            AVWorldSpawns.addBiomeSpawns(builder);
         }
      }
   }

   public Codec<? extends BiomeModifier> codec() {
      return (Codec<? extends BiomeModifier>)SERIALIZER.get();
   }

   public static Codec<AVMobSpawnBiomeModifier> makeCodec() {
      return Codec.unit(AVMobSpawnBiomeModifier::new);
   }
}
