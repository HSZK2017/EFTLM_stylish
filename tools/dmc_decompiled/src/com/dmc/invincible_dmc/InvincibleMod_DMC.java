package com.dmc.invincible_dmc;

import com.dmc.invincible_dmc.advancements.DMCAchievements;
import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.api.skill.ComboType;
import com.dmc.invincible_dmc.block.DMCBlocks;
import com.dmc.invincible_dmc.client.config.AAAPPerformanceClientConfig;
import com.dmc.invincible_dmc.client.config.YamatoClientConfig;
import com.dmc.invincible_dmc.client.gui.vergilstatus.VergilStatusConfig;
import com.dmc.invincible_dmc.command.arguments.DMCommandArgumentTypes;
import com.dmc.invincible_dmc.compat.ICompatModule;
import com.dmc.invincible_dmc.compat.armourers_workshop.ArmourersWorkshopCompat;
import com.dmc.invincible_dmc.compat.figura.FiguraCompat;
import com.dmc.invincible_dmc.compat.ftbchunks.FTBChunksCompat;
import com.dmc.invincible_dmc.compat.journeymap.JourneyMapCompat;
import com.dmc.invincible_dmc.compat.oculus.OculusCompat;
import com.dmc.invincible_dmc.compat.tacz.TaczCompat;
import com.dmc.invincible_dmc.compat.waystones.WaystonesCompat;
import com.dmc.invincible_dmc.compat.xaero.XaeroMinimapCompat;
import com.dmc.invincible_dmc.entity.DMCEntities;
import com.dmc.invincible_dmc.entity.DMCEntityEvents;
import com.dmc.invincible_dmc.gameassets.DMCAnimationVariableKeys;
import com.dmc.invincible_dmc.gameassets.DMCConditions;
import com.dmc.invincible_dmc.gameassets.DMCEffects;
import com.dmc.invincible_dmc.gameassets.DMCSkillDataKeys;
import com.dmc.invincible_dmc.gameassets.DMCSkillSlots;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.item.DMCItems;
import com.dmc.invincible_dmc.item.DMCreativeTabs;
import com.dmc.invincible_dmc.item.enchantment.DMCEnchantments;
import com.dmc.invincible_dmc.network.DMCNetwork;
import com.dmc.invincible_dmc.particle.DMCParticles;
import com.dmc.invincible_dmc.utils.DMCLog;
import com.merlin204.avalon.main.AvalonMOD;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.client.renderer.shader.compute.loader.ComputeShaderProvider;
import yesman.epicfight.config.ClientConfig;

@Mod("invincible_dmc")
public class InvincibleMod_DMC {
   public static final String MOD_ID = "invincible_dmc";
   private static final Collection<SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

   public InvincibleMod_DMC(FMLJavaModLoadingContext context) {
      AvalonMOD.beMerlin = false;
      IEventBus modEventBus = context.getModEventBus();
      DMCNetwork.registerPackets();
      DMCEntities.ENTITIES.register(modEventBus);
      DMCItems.ITEMS.register(modEventBus);
      DMCEnchantments.ENCHANTMENTS.register(modEventBus);
      DMCBlocks.BLOCKS.register(modEventBus);
      DMCreativeTabs.TABS.register(modEventBus);
      DMCConditions.CONDITIONS.register(modEventBus);
      DMCAnimationVariableKeys.KEYS.register(modEventBus);
      DMCSkillDataKeys.DATA_KEYS.register(modEventBus);
      DMCParticles.PARTICLES.register(modEventBus);
      DMCEffects.EFFECTS.register(modEventBus);
      DMCommandArgumentTypes.COMMAND_ARGUMENT_TYPES.register(modEventBus);
      modEventBus.addListener(this::commonSetup);
      modEventBus.addListener(this::onClientSetup);
      ComboType.ENUM_MANAGER.registerEnumCls("invincible_dmc", ComboNode.ComboTypes.class);
      DMCSkillSlots.init();
      context.registerConfig(Type.COMMON, DMConfig.SPEC);
      context.registerConfig(Type.CLIENT, VergilStatusConfig.SPEC);
      context.registerConfig(Type.CLIENT, YamatoClientConfig.SPEC, "invincible_dmc-yamato-client.toml");
      context.registerConfig(Type.CLIENT, AAAPPerformanceClientConfig.SPEC, "invincible_dmc-aaap-performance.toml");
      if (ModList.get().isLoaded("tacz")) {
         ICompatModule.loadCompatModule(context, TaczCompat.class);
      }

      if (ModList.get().isLoaded("combat_evolution")) {
      }

      if (ModList.get().isLoaded("waystones")) {
         ICompatModule.loadCompatModule(context, WaystonesCompat.class);
      }

      if (FMLEnvironment.dist == Dist.CLIENT && ModList.get().isLoaded("oculus")) {
         ICompatModule.loadCompatModule(context, OculusCompat.class);
      }

      if (FMLEnvironment.dist == Dist.CLIENT && ModList.get().isLoaded("xaerominimap")) {
         ICompatModule.loadCompatModule(context, XaeroMinimapCompat.class);
      }

      if (FMLEnvironment.dist == Dist.CLIENT && ModList.get().isLoaded("ftbchunks")) {
         ICompatModule.loadCompatModule(context, FTBChunksCompat.class);
      }

      if (FMLEnvironment.dist == Dist.CLIENT && ModList.get().isLoaded("journeymap")) {
         ICompatModule.loadCompatModule(context, JourneyMapCompat.class);
      }

      if (FMLEnvironment.dist == Dist.CLIENT && ModList.get().isLoaded("armourers_workshop")) {
         ICompatModule.loadCompatModule(context, ArmourersWorkshopCompat.class);
      }

      if (FMLEnvironment.dist == Dist.CLIENT && ModList.get().isLoaded("figura")) {
         ICompatModule.loadCompatModule(context, FiguraCompat.class);
      }

      DMCAchievements.init();
      modEventBus.addListener(this::onClientSetup);
      MinecraftForge.EVENT_BUS.register(this);
   }

   @NotNull
   public static ResourceLocation rl(@NotNull String path) {
      return ResourceLocation.fromNamespaceAndPath("invincible_dmc", path);
   }

   private void commonSetup(FMLCommonSetupEvent event) {
      event.enqueueWork(DMCommandArgumentTypes::registerArgumentTypes);
      event.enqueueWork(DMCEntityEvents::registerArmatures);
      event.enqueueWork(DMCLog::loadFromConfig);
   }

   private void onClientSetup(FMLClientSetupEvent event) {
      YamatoAnimations.LoadCamAnims();
      event.enqueueWork(() -> {
         if (ComputeShaderProvider.supportComputeShader()) {
            ClientConfig.ACTIVATE_COMPUTE_SHADER.set(true);
         }
      });
   }

   public static void queueServerWork(int tick, Runnable action) {
      if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) {
         workQueue.add(new SimpleEntry<>(action, tick));
      }
   }

   public static TagKey<DamageType> createDamageType(String name) {
      return TagKey.m_203882_(Registries.f_268580_, ResourceLocation.fromNamespaceAndPath("invincible_dmc", name));
   }

   @SubscribeEvent
   public void tick(ServerTickEvent event) {
      if (event.phase == Phase.END) {
         this.processWorkQueue();
      }
   }

   private void processWorkQueue() {
      List<SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
      workQueue.forEach(work -> {
         work.setValue(work.getValue() - 1);
         if (work.getValue() == 0) {
            actions.add((SimpleEntry<Runnable, Integer>)work);
         }
      });
      actions.forEach(e -> e.getKey().run());
      workQueue.removeAll(actions);
   }
}
