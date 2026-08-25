package org.merlin204.mimic.worldgen;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.BiomeBuilder;
import net.minecraft.world.level.biome.MobSpawnSettings.Builder;

public class WraithonBiomes {
   public static final ResourceKey<Biome> THE_LETHEAN_SEA = register("the_lethean_sea");

   public static ResourceKey<Biome> register(String name) {
      return ResourceKey.m_135785_(Registries.f_256952_, ResourceLocation.fromNamespaceAndPath("mimic", name));
   }

   public static void boostrap(BootstapContext<Biome> context) {
      Builder spawnBuilder = new Builder();
      net.minecraft.world.level.biome.BiomeGenerationSettings.Builder biomeBuilder = new net.minecraft.world.level.biome.BiomeGenerationSettings.Builder(
         context.m_255420_(Registries.f_256988_), context.m_255420_(Registries.f_257003_)
      );
      context.m_255272_(
         THE_LETHEAN_SEA,
         new BiomeBuilder()
            .m_264558_(false)
            .m_47611_(0.0F)
            .m_47609_(2.0F)
            .m_47601_(biomeBuilder.m_255380_())
            .m_47605_(spawnBuilder.m_48381_())
            .m_47603_(
               new net.minecraft.world.level.biome.BiomeSpecialEffects.Builder()
                  .m_48034_(9109504)
                  .m_48037_(9109504)
                  .m_48019_(16711680)
                  .m_48040_(0)
                  .m_48029_(new AmbientParticleSettings(ParticleTypes.f_123762_, 0.01428F))
                  .m_48023_(SoundEvents.f_12431_)
                  .m_48027_(new AmbientMoodSettings(SoundEvents.f_12484_, 6000, 8, 2.0))
                  .m_48025_(new AmbientAdditionsSettings(SoundEvents.f_12378_, 0.0111))
                  .m_48021_(Musics.m_263184_(SoundEvents.f_12157_))
                  .m_48018_()
            )
            .m_47592_()
      );
   }
}
