package com.pla.annoyingvillagers.init;

import com.pla.annoyingvillagers.client.particle.BigSplashParticle;
import com.pla.annoyingvillagers.client.particle.BlueSparkParticle;
import com.pla.annoyingvillagers.client.particle.DragonSparkParticle;
import com.pla.annoyingvillagers.client.particle.ElectricLiteParticle;
import com.pla.annoyingvillagers.client.particle.ElectricSpark2Particle;
import com.pla.annoyingvillagers.client.particle.ElectricSparkParticle;
import com.pla.annoyingvillagers.client.particle.EnderParticle;
import com.pla.annoyingvillagers.client.particle.FireballParticle;
import com.pla.annoyingvillagers.client.particle.FullCowlParticle;
import com.pla.annoyingvillagers.client.particle.GlowingEyesParticle;
import com.pla.annoyingvillagers.client.particle.GreenSparkParticle;
import com.pla.annoyingvillagers.client.particle.LightParticle;
import com.pla.annoyingvillagers.client.particle.MeteoriteTrailParticle;
import com.pla.annoyingvillagers.client.particle.PeParticle;
import com.pla.annoyingvillagers.client.particle.RedSparkParticle;
import com.pla.annoyingvillagers.client.particle.SparkParticle;
import net.minecraft.client.particle.SmokeParticle.Provider;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class AnnoyingVillagersModParticles {
   @SubscribeEvent
   public static void registerParticles(RegisterParticleProvidersEvent event) {
      event.registerSpriteSet((ParticleType)AnnoyingVillagersModParticleTypes.RED_SPARK.get(), RedSparkParticle::provider);
      event.registerSpriteSet((ParticleType)AnnoyingVillagersModParticleTypes.ELECTRIC_SPARK.get(), ElectricSparkParticle::provider);
      event.registerSpriteSet((ParticleType)AnnoyingVillagersModParticleTypes.DRAGON_SPARK.get(), DragonSparkParticle::provider);
      event.registerSpriteSet((ParticleType)AnnoyingVillagersModParticleTypes.ELECTRIC_SPARK_2.get(), ElectricSpark2Particle::provider);
      event.registerSpriteSet((ParticleType)AnnoyingVillagersModParticleTypes.SPARK.get(), SparkParticle::provider);
      event.registerSpriteSet((ParticleType)AnnoyingVillagersModParticleTypes.PE.get(), PeParticle::provider);
      event.registerSpriteSet((ParticleType)AnnoyingVillagersModParticleTypes.GLOWINGEYES.get(), GlowingEyesParticle::provider);
      event.registerSpriteSet((ParticleType)AnnoyingVillagersModParticleTypes.LIGHT.get(), LightParticle::provider);
      event.registerSpriteSet((ParticleType)AnnoyingVillagersModParticleTypes.BLUESPARK.get(), BlueSparkParticle::provider);
      event.registerSpriteSet((ParticleType)AnnoyingVillagersModParticleTypes.GREENSPARK.get(), GreenSparkParticle::provider);
      event.registerSpriteSet((ParticleType)AnnoyingVillagersModParticleTypes.ENDER.get(), EnderParticle::provider);
      event.registerSpriteSet((ParticleType)AnnoyingVillagersModParticleTypes.NULL.get(), Provider::new);
      event.registerSpriteSet((ParticleType)AnnoyingVillagersModParticleTypes.FULL_COWL.get(), FullCowlParticle::provider);
      event.registerSpriteSet((ParticleType)AnnoyingVillagersModParticleTypes.METEORITE_TRAIL.get(), MeteoriteTrailParticle::provider);
      event.registerSpriteSet((ParticleType)AnnoyingVillagersModParticleTypes.BIG_SPLASH.get(), BigSplashParticle::provider);
      event.registerSpriteSet((ParticleType)AnnoyingVillagersModParticleTypes.FIREBALL.get(), FireballParticle::provider);
      event.registerSpriteSet((ParticleType)AnnoyingVillagersModParticleTypes.ELECTRIC_LITE.get(), ElectricLiteParticle::provider);
   }
}
