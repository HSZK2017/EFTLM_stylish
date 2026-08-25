package com.pla.annoyingvillagers;

import com.mojang.serialization.Codec;
import com.pla.annoyingvillagers.capabilities.AVWeaponCapabilityPresets;
import com.pla.annoyingvillagers.client.engine.CameraEngine;
import com.pla.annoyingvillagers.client.engine.SpriteArrowsCommonEntrypoint;
import com.pla.annoyingvillagers.config.AnnoyingVillagersClientConfig;
import com.pla.annoyingvillagers.config.AnnoyingVillagersConfig;
import com.pla.annoyingvillagers.config.AnnoyingVillagersSpawnConfig;
import com.pla.annoyingvillagers.event.NpcGearLoadEvent;
import com.pla.annoyingvillagers.gameasset.AVSkillDataKeys;
import com.pla.annoyingvillagers.gameasset.AVSounds;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModBlockEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModBlocks;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModCapabilities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModCreativeTabs;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModMobEffects;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModParticleTypes;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.item.FishingRodGrappleUtil;
import com.pla.annoyingvillagers.item.HookGunItem;
import com.pla.annoyingvillagers.network.CPApplyShake;
import com.pla.annoyingvillagers.network.ClientboundBlackFireFx;
import com.pla.annoyingvillagers.network.ClientboundBlueDemonEffectFx;
import com.pla.annoyingvillagers.network.ClientboundDiamondAttractorFx;
import com.pla.annoyingvillagers.network.ClientboundEliteHerobrineFx;
import com.pla.annoyingvillagers.network.ClientboundEnderAegisSparkFx;
import com.pla.annoyingvillagers.network.ClientboundGlaiveExplosionFx;
import com.pla.annoyingvillagers.network.ClientboundHerobrineAssistanceFx;
import com.pla.annoyingvillagers.network.ClientboundHerobrinePortalFx;
import com.pla.annoyingvillagers.network.ClientboundMuteExplosionAtPos;
import com.pla.annoyingvillagers.network.ClientboundTeleportPortalFx;
import com.pla.annoyingvillagers.network.ClientboundWoopieSwordWindFx;
import com.pla.annoyingvillagers.world.AVMobSpawnBiomeModifier;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yesman.epicfight.gameasset.Armatures;

@Mod("annoyingvillagers")
public class AnnoyingVillagers {
   public static final Logger LOGGER = LogManager.getLogger(AnnoyingVillagers.class);
   public static final String MODID = "annoyingvillagers";
   public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(
      ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "main"), () -> "1", "1"::equals, "1"::equals
   );
   private static int messageID = 0;

   public AnnoyingVillagers(FMLJavaModLoadingContext context) {
      IEventBus modEventBus = context.getModEventBus();
      modEventBus.addListener(this::commonSetup);
      AnnoyingVillagersModBlocks.REGISTRY.register(modEventBus);
      AnnoyingVillagersModBlockEntities.REGISTRY.register(modEventBus);
      AnnoyingVillagersModItems.REGISTRY.register(modEventBus);
      modEventBus.addListener(AVWeaponCapabilityPresets::register);
      AnnoyingVillagersModEntities.REGISTRY.register(modEventBus);
      AnnoyingVillagersModMobEffects.REGISTRY.register(modEventBus);
      AnnoyingVillagersModParticleTypes.REGISTRY.register(modEventBus);
      MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, AnnoyingVillagersModCapabilities::attachEntityCapability);
      modEventBus.addListener(AnnoyingVillagersModCapabilities::registerCapabilities);
      AnnoyingVillagersModCreativeTabs.register(modEventBus);
      AnnoyingVillagersModSounds.register(modEventBus);
      AVSounds.SOUNDS.register(modEventBus);
      MinecraftForge.EVENT_BUS.register(new NpcGearLoadEvent());
      context.registerConfig(Type.COMMON, AnnoyingVillagersConfig.SPEC, "annoyingvillagers-server.toml");
      DeferredRegister<Codec<? extends BiomeModifier>> biomeModifiers = DeferredRegister.create(Keys.BIOME_MODIFIER_SERIALIZERS, "annoyingvillagers");
      biomeModifiers.register(modEventBus);
      biomeModifiers.register("av_mob_spawns", AVMobSpawnBiomeModifier::makeCodec);
      context.registerConfig(Type.COMMON, AnnoyingVillagersSpawnConfig.SPEC, "annoyingvillagers-spawns.toml");
      AVSkillDataKeys.DATA_KEYS.register(modEventBus);
      if (FMLEnvironment.dist.isClient()) {
         context.registerConfig(Type.CLIENT, AnnoyingVillagersClientConfig.SPEC, "annoyingvillagers-client.toml");
         modEventBus.addListener(this::clientSetup);
         modEventBus.addListener(EventPriority.LOWEST, AnnoyingVillagers.ClassLoadingProtection::listen);
      }
   }

   public static <T> void addNetworkMessage(
      Class<T> oclass, BiConsumer<T, FriendlyByteBuf> biconsumer, Function<FriendlyByteBuf, T> function, BiConsumer<T, Supplier<Context>> biconsumer1
   ) {
      PACKET_HANDLER.registerMessage(messageID, oclass, biconsumer, function, biconsumer1);
      messageID++;
   }

   private void clientSetup(FMLClientSetupEvent event) {
      new CameraEngine();
   }

   private void commonSetup(FMLCommonSetupEvent event) {
      event.enqueueWork(AnnoyingVillagers::registerArmatures);
   }

   public static void registerArmatures() {
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.PLAYER_NPC.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.VILLAGER_SCOUT.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.VILLAGER_SCOUT_CAPTAIN.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.RED_VILLAGER_KNIGHT.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.BLUE_VILLAGER_KNIGHT.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.GREEN_VILLAGER_KNIGHT.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.PURPLE_VILLAGER_KNIGHT.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.STEVE.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.ANGRY_STEVE.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.ALEX.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.JEV.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.CHRIS.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.LOW_HEROBRINE_CLONE.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.LOW_SHADOW_HEROBRINE_CLONE.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.AEGIS_HEROBRINE.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.SWORDSMAN_HEROBRINE.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.GLAIVE_HEROBRINE.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.SLEDGEHAMMER_HEROBRINE.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.REAPER_HEROBRINE.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.NULL_SWORD.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.NULL_SHOVEL.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.NULL_AXE.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.NULL_PICKAXE.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.NULL_HOE.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.NULL_SKELETON.get(), Armatures.SKELETON);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.NULL.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.HEROBRINE_CLONE.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.SHADOW_HEROBRINE_CLONE.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.HEROBRINE_CHRIS.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.ARMORED_HEROBRINE.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.HEROBRINE_7.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.SHADOW_HEROBRINE.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.BLUE_DEMON.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.TRANSPORTER_HEROBRINE_CLONE.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.INFECTED_CHRIS.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.INFECTED_PLAYER_NPC.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.INFECTED_THEMOSTMOISTBURRIT0.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.HEROBRINE_GREG.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)AnnoyingVillagersModEntities.ELITE_HEROBRINE_KNOCKED.get(), Armatures.BIPED);
   }

   private static class ClassLoadingProtection {
      private static void listen(FMLClientSetupEvent event) {
         event.enqueueWork(SpriteArrowsCommonEntrypoint::replace);
      }
   }

   @EventBusSubscriber(
      modid = "annoyingvillagers",
      bus = Bus.MOD,
      value = {Dist.CLIENT}
   )
   public static class ClientModEvents {
      @SubscribeEvent
      public static void onClientSetup(FMLClientSetupEvent event) {
         event.enqueueWork(
            () -> {
               ItemProperties.register(
                  (Item)AnnoyingVillagersModItems.DEMONIAC_VOLTAGE_REAVER.get(),
                  ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "second_form"),
                  (stack, level, entity, seed) -> stack.m_41782_() && stack.m_41783_().m_128471_("SecondForm") ? 1.0F : 0.0F
               );
               ItemProperties.register(
                  (Item)AnnoyingVillagersModItems.DEMONIAC_VOLTAGE_REAVER.get(),
                  ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "snake_animation"),
                  (stack, level, entity, seed) -> stack.m_41782_() && stack.m_41783_().m_128471_("SnakeAnimation") ? 1.0F : 0.0F
               );
               ItemProperties.register(
                  (Item)AnnoyingVillagersModItems.DEMONIAC_VOLTAGE_REAVER.get(),
                  ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "snake_animation_ready"),
                  (stack, level, entity, seed) -> stack.m_41782_() && stack.m_41783_().m_128451_("HitCount") == 5 ? 1.0F : 0.0F
               );
               ItemProperties.register(
                  (Item)AnnoyingVillagersModItems.ENDER_AEGIS.get(),
                  ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "second_form"),
                  (stack, level, entity, seed) -> stack.m_41782_() && stack.m_41783_().m_128471_("SecondForm") ? 1.0F : 0.0F
               );
               ItemProperties.register(
                  (Item)AnnoyingVillagersModItems.ADVANCED_FISHING_ROD.get(),
                  ResourceLocation.fromNamespaceAndPath("minecraft", "cast"),
                  (stack, level, entity, seed) -> FishingRodGrappleUtil.getCastProperty(stack, entity)
               );
               ItemProperties.register(
                  (Item)AnnoyingVillagersModItems.TONY_THE_FISHING_ROD.get(),
                  ResourceLocation.fromNamespaceAndPath("minecraft", "cast"),
                  (stack, level, entity, seed) -> FishingRodGrappleUtil.getCastProperty(stack, entity)
               );
               ItemProperties.register(
                  (Item)AnnoyingVillagersModItems.HOOK_GUN.get(),
                  ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "hook"),
                  (stack, level, entity, seed) -> stack.m_41782_() && stack.m_41783_().m_128441_("hook") ? 1.0F : 0.0F
               );
               ItemProperties.register(
                  (Item)AnnoyingVillagersModItems.HOOK_GUN.get(),
                  ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "attached"),
                  (stack, level, entity, seed) -> entity != null && HookGunItem.hasAttachedHook(entity.m_9236_(), entity) ? 1.0F : 0.0F
               );

               for (Item item : ForgeRegistries.ITEMS.getValues()) {
                  if (item instanceof BowItem) {
                     ItemProperties.register(item, ResourceLocation.fromNamespaceAndPath("minecraft", "pulling"), (stack, level, entity, seed) -> {
                        if (stack.m_41782_() && stack.m_41783_() != null && stack.m_41783_().m_128441_("Pulling")) {
                           return 1.0F;
                        } else if (entity == null) {
                           return 0.0F;
                        } else {
                           return entity.m_6117_() && entity.m_21211_() == stack ? 1.0F : 0.0F;
                        }
                     });
                     ItemProperties.register(item, ResourceLocation.fromNamespaceAndPath("minecraft", "pull"), (stack, level, entity, seed) -> {
                        if (stack.m_41782_() && stack.m_41783_() != null && stack.m_41783_().m_128441_("Pulling")) {
                           return stack.m_41783_().m_128457_("Pulling");
                        } else if (entity == null) {
                           return 0.0F;
                        } else if (entity.m_21211_() != stack) {
                           return 0.0F;
                        } else {
                           float used = (float)(stack.m_41779_() - entity.m_21212_());
                           return used / 20.0F;
                        }
                     });
                  }
               }
            }
         );
      }
   }

   @EventBusSubscriber(
      bus = Bus.MOD
   )
   public static class initer {
      @SubscribeEvent
      public static void init(FMLCommonSetupEvent fmlCommonSetupEvent) {
         AnnoyingVillagers.addNetworkMessage(
            ClientboundGlaiveExplosionFx.class,
            ClientboundGlaiveExplosionFx::encode,
            ClientboundGlaiveExplosionFx::decode,
            ClientboundGlaiveExplosionFx::handle
         );
         AnnoyingVillagers.addNetworkMessage(
            ClientboundMuteExplosionAtPos.class,
            ClientboundMuteExplosionAtPos::encode,
            ClientboundMuteExplosionAtPos::decode,
            ClientboundMuteExplosionAtPos::handle
         );
         AnnoyingVillagers.addNetworkMessage(
            ClientboundHerobrinePortalFx.class,
            ClientboundHerobrinePortalFx::encode,
            ClientboundHerobrinePortalFx::decode,
            ClientboundHerobrinePortalFx::handle
         );
         AnnoyingVillagers.addNetworkMessage(
            ClientboundWoopieSwordWindFx.class,
            ClientboundWoopieSwordWindFx::encode,
            ClientboundWoopieSwordWindFx::decode,
            ClientboundWoopieSwordWindFx::handle
         );
         AnnoyingVillagers.addNetworkMessage(
            ClientboundBlackFireFx.class, ClientboundBlackFireFx::encode, ClientboundBlackFireFx::decode, ClientboundBlackFireFx::handle
         );
         AnnoyingVillagers.addNetworkMessage(CPApplyShake.class, CPApplyShake::encode, CPApplyShake::new, CPApplyShake::handle);
         AnnoyingVillagers.addNetworkMessage(
            ClientboundDiamondAttractorFx.class,
            ClientboundDiamondAttractorFx::encode,
            ClientboundDiamondAttractorFx::decode,
            ClientboundDiamondAttractorFx::handle
         );
         AnnoyingVillagers.addNetworkMessage(
            ClientboundHerobrineAssistanceFx.class,
            ClientboundHerobrineAssistanceFx::encode,
            ClientboundHerobrineAssistanceFx::decode,
            ClientboundHerobrineAssistanceFx::handle
         );
         AnnoyingVillagers.addNetworkMessage(
            ClientboundEnderAegisSparkFx.class,
            ClientboundEnderAegisSparkFx::encode,
            ClientboundEnderAegisSparkFx::decode,
            ClientboundEnderAegisSparkFx::handle
         );
         AnnoyingVillagers.addNetworkMessage(
            ClientboundEliteHerobrineFx.class, ClientboundEliteHerobrineFx::encode, ClientboundEliteHerobrineFx::decode, ClientboundEliteHerobrineFx::handle
         );
         AnnoyingVillagers.addNetworkMessage(
            ClientboundBlueDemonEffectFx.class,
            ClientboundBlueDemonEffectFx::encode,
            ClientboundBlueDemonEffectFx::decode,
            ClientboundBlueDemonEffectFx::handle
         );
         AnnoyingVillagers.addNetworkMessage(
            ClientboundTeleportPortalFx.class, ClientboundTeleportPortalFx::encode, ClientboundTeleportPortalFx::decode, ClientboundTeleportPortalFx::handle
         );
      }
   }
}
