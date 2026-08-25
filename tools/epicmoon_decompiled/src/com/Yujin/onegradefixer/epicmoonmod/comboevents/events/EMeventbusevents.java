package com.Yujin.onegradefixer.epicmoonmod.comboevents.events;

import com.Yujin.onegradefixer.epicmoonmod.particle.EM_Trail;
import com.Yujin.onegradefixer.epicmoonmod.particle.EM_Trail10;
import com.Yujin.onegradefixer.epicmoonmod.particle.EM_Trail2;
import com.Yujin.onegradefixer.epicmoonmod.particle.EM_Trail3;
import com.Yujin.onegradefixer.epicmoonmod.particle.EM_Trail4;
import com.Yujin.onegradefixer.epicmoonmod.particle.EM_Trail5;
import com.Yujin.onegradefixer.epicmoonmod.particle.EM_Trail6;
import com.Yujin.onegradefixer.epicmoonmod.particle.EM_Trail7;
import com.Yujin.onegradefixer.epicmoonmod.particle.EM_Trail8;
import com.Yujin.onegradefixer.epicmoonmod.particle.EM_Trail9;
import com.Yujin.onegradefixer.epicmoonmod.particle.EM_Trail_Light;
import com.Yujin.onegradefixer.epicmoonmod.particle.EM_Trail_Light2;
import com.Yujin.onegradefixer.epicmoonmod.particle.EMparticles;
import com.Yujin.onegradefixer.epicmoonmod.particle.Image;
import com.Yujin.onegradefixer.epicmoonmod.particle.StarParticle;
import com.Yujin.onegradefixer.epicmoonmod.renderer.DRenderer;
import com.Yujin.onegradefixer.epicmoonmod.renderer.TSRenderer;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent.RegisterItemRenderer;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(
   modid = "epicmoonmod",
   value = {Dist.CLIENT},
   bus = Bus.MOD
)
public class EMeventbusevents {
   @SubscribeEvent
   public static void onParticleRegistry(RegisterParticleProvidersEvent event) {
      event.registerSpriteSet((ParticleType)EMparticles.EFFEK_TRAIL.get(), EM_Trail.Provider::new);
      event.registerSpriteSet((ParticleType)EMparticles.EFFEK_TRAIL2.get(), EM_Trail2.Provider::new);
      event.registerSpriteSet((ParticleType)EMparticles.EFFEK_TRAIL3.get(), EM_Trail3.Provider::new);
      event.registerSpriteSet((ParticleType)EMparticles.EFFEK_TRAIL4.get(), EM_Trail4.Provider::new);
      event.registerSpriteSet((ParticleType)EMparticles.EFFEK_TRAIL5.get(), EM_Trail5.Provider::new);
      event.registerSpriteSet((ParticleType)EMparticles.EFFEK_TRAIL6.get(), EM_Trail6.Provider::new);
      event.registerSpriteSet((ParticleType)EMparticles.EFFEK_TRAIL7.get(), EM_Trail7.Provider::new);
      event.registerSpriteSet((ParticleType)EMparticles.EFFEK_TRAIL8.get(), EM_Trail8.Provider::new);
      event.registerSpriteSet((ParticleType)EMparticles.EFFEK_TRAIL9.get(), EM_Trail9.Provider::new);
      event.registerSpriteSet((ParticleType)EMparticles.EFFEK_TRAIL10.get(), EM_Trail10.Provider::new);
      event.registerSpriteSet((ParticleType)EMparticles.LIGHT_TRAIL.get(), EM_Trail_Light.Provider::new);
      event.registerSpriteSet((ParticleType)EMparticles.LIGHT_TRAIL2.get(), EM_Trail_Light2.Provider::new);
      event.registerSpecial((ParticleType)EMparticles.IMAGE.get(), new Image());
      event.registerSpriteSet((ParticleType)EMparticles.STAR.get(), StarParticle.Provider::new);
   }

   @SubscribeEvent
   public static void registerItemRenderer(RegisterItemRenderer event) {
      event.addItemRenderer(new ResourceLocation("epicmoonmod", "tentai_seitou_renderer"), TSRenderer::new);
      event.addItemRenderer(new ResourceLocation("epicmoonmod", "dual_renderer"), DRenderer::new);
   }
}
