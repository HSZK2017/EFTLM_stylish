package com.Yujin.onegradefixer.epicmoonmod;

import com.Yujin.onegradefixer.epicmoonmod.comboevents.packet.ModNetwork;
import com.Yujin.onegradefixer.epicmoonmod.config.EMConfig;
import com.Yujin.onegradefixer.epicmoonmod.effect.EMeffects;
import com.Yujin.onegradefixer.epicmoonmod.item.EpicmoonItems;
import com.Yujin.onegradefixer.epicmoonmod.item.EpicmoonTabs;
import com.Yujin.onegradefixer.epicmoonmod.particle.EMparticles;
import com.Yujin.onegradefixer.epicmoonmod.skill.EMSkillDataKeys;
import com.Yujin.onegradefixer.epicmoonmod.sound.EMsounds;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod("epicmoonmod")
public class Epicmoonmod {
   public static final String MOD_ID = "epicmoonmod";
   private static final Logger LOGGER = LogUtils.getLogger();

   public Epicmoonmod(FMLJavaModLoadingContext context) {
      ModLoadingContext.get().registerConfig(Type.COMMON, EMConfig.SPEC, "epicmoonmod-common.toml");
      IEventBus modEventBus = context.getModEventBus();
      ModNetwork.register();
      modEventBus.addListener(this::commonSetup);
      EpicmoonItems.register(modEventBus);
      EpicmoonTabs.register(modEventBus);
      EMsounds.register(modEventBus);
      EMeffects.register(modEventBus);
      EMparticles.register(modEventBus);
      EMSkillDataKeys.DATA_KEYS.register(modEventBus);
      MinecraftForge.EVENT_BUS.register(this);
      modEventBus.addListener(this::addCreative);
   }

   private void commonSetup(FMLCommonSetupEvent event) {
   }

   private void addCreative(BuildCreativeModeTabContentsEvent event) {
   }

   @SubscribeEvent
   public void onServerStarting(ServerStartingEvent event) {
   }

   @EventBusSubscriber(
      modid = "epicmoonmod",
      bus = Bus.MOD,
      value = {Dist.CLIENT}
   )
   public static class ClientModEvents {
      @SubscribeEvent
      public static void onClientSetup(FMLClientSetupEvent event) {
      }
   }
}
