package com.dmc.invincible_dmc.advancements;

import com.dmc.invincible_dmc.client.sound.DMCSounds;
import java.util.Set;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AdvancementEvent.AdvancementEarnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.particle.EpicFightParticles;

public class DMCAchievements {
   private static final Set<String> YAMATO_ITEM_IDS = Set.of("invincible_dmc:yamato_dmc4", "invincible_dmc:yamato_dmc5", "invincible_dmc:yamato_dmc5_bd");
   private static final String YAMATO_ADVANCEMENT_ID = "invincible_dmc:obtain_any_yamato";

   public static void init() {
      MinecraftForge.EVENT_BUS.register(DMCAchievements.class);
   }

   @SubscribeEvent
   public static void onItemPickup(ItemPickupEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         checkItemAchievements(player, event.getStack());
      }
   }

   @SubscribeEvent
   public static void onItemCrafted(ItemCraftedEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         checkItemAchievements(player, event.getCrafting());
      }
   }

   @SubscribeEvent
   public static void onAdvancementEarned(AdvancementEarnEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         ResourceLocation earnedId = event.getAdvancement().m_138327_();
         if (earnedId.toString().equals("invincible_dmc:obtain_any_yamato")) {
            onYamatoAchievementGranted(player);
         }
      }
   }

   private static void checkItemAchievements(ServerPlayer player, ItemStack stack) {
      ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.m_41720_());
      if (itemId != null) {
         String fullId = itemId.toString();
         if (YAMATO_ITEM_IDS.contains(fullId)) {
            grantYamatoAdvancement(player);
         }

         if (itemId.m_135827_().equals("invincible_dmc")) {
            String path = "efn:obtain_" + itemId.m_135815_();
            grantAdvancement(player, path, stack);
         }
      }
   }

   private static void grantYamatoAdvancement(ServerPlayer player) {
      Advancement advancement = player.f_8924_.m_129889_().m_136041_(new ResourceLocation("invincible_dmc:obtain_any_yamato"));
      if (advancement != null && !player.m_8960_().m_135996_(advancement).m_8193_()) {
         player.m_8960_().m_135988_(advancement, "obtained");
      }
   }

   public static void onYamatoAchievementGranted(ServerPlayer player) {
      player.m_9236_().m_5594_(null, player.m_20183_(), (SoundEvent)DMCSounds.YAMATO.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
      player.m_284548_()
         .m_8767_(
            (SimpleParticleType)EpicFightParticles.FORCE_FIELD_END.get(), player.m_20185_(), player.m_20186_() + 1.0, player.m_20189_(), 1, 0.0, 0.0, 0.0, 0.0
         );
      player.m_284548_()
         .m_8767_(
            (SimpleParticleType)EpicFightParticles.FORCE_FIELD.get(), player.m_20185_(), player.m_20186_() + 1.0, player.m_20189_(), 1, 0.0, 0.0, 0.0, 0.0
         );
   }

   private static void grantAdvancement(ServerPlayer player, String id, ItemStack stack) {
      Advancement advancement = player.f_8924_.m_129889_().m_136041_(new ResourceLocation(id));
      if (advancement != null && !player.m_8960_().m_135996_(advancement).m_8193_()) {
         player.m_8960_().m_135988_(advancement, "obtained");
      }
   }

   public static Builder createObtainAdvancement(ItemStack icon, String id) {
      return Builder.m_138353_()
         .m_138396_(new ResourceLocation("minecraft:story/root"))
         .m_138386_("obtain", TriggerInstance.m_43199_(new ItemLike[]{icon.m_41720_()}))
         .m_138354_(net.minecraft.advancements.AdvancementRewards.Builder.m_10005_(10))
         .m_138362_(
            icon,
            Component.m_237115_("advancement.invincible_dmc." + id + ".title"),
            Component.m_237115_("advancement.invincible_dmc." + id + ".description"),
            null,
            FrameType.TASK,
            true,
            true,
            false
         );
   }
}
