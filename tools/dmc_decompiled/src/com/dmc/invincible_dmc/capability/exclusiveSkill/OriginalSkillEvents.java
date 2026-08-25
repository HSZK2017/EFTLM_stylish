package com.dmc.invincible_dmc.capability.exclusiveSkill;

import com.dmc.epicarclib.api.exclusive.ExclusiveSkillStateApi;
import java.util.Locale;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

@EventBusSubscriber(
   modid = "invincible_dmc",
   bus = Bus.FORGE
)
public class OriginalSkillEvents {
   private static final SkillSlot[] MIGRATED_SLOTS = new SkillSlot[]{SkillSlots.DODGE, SkillSlots.GUARD, SkillSlots.IDENTITY, SkillSlots.MOVER};
   private static final ResourceLocation CAPABILITY_ID = ResourceLocation.fromNamespaceAndPath("invincible_dmc", "original_skill_memory");

   @SubscribeEvent
   public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
      if (event.getObject() instanceof Player) {
         OriginalSkillCapability.Provider provider = new OriginalSkillCapability.Provider();
         event.addCapability(CAPABILITY_ID, provider);
         event.addListener(provider::invalidate);
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void onPlayerClone(Clone event) {
      Player oldPlayer = event.getOriginal();
      Player newPlayer = event.getEntity();
      oldPlayer.reviveCaps();
      oldPlayer.getCapability(OriginalSkillCapability.INSTANCE)
         .ifPresent(oldCap -> newPlayer.getCapability(OriginalSkillCapability.INSTANCE).ifPresent(newCap -> newCap.copyFrom(oldCap)));
      oldPlayer.invalidateCaps();
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.phase == Phase.END && event.player instanceof ServerPlayer player) {
         ServerPlayerPatch playerPatch = EpicFightCapabilities.getServerPlayerPatch(player);
         if (playerPatch != null) {
            player.getCapability(OriginalSkillCapability.INSTANCE)
               .ifPresent(
                  memory -> {
                     for (SkillSlot slot : MIGRATED_SLOTS) {
                        String slotName = slot.toString().toLowerCase(Locale.ROOT);
                        OriginalSkillCapability.SkillSnapshot snapshot = memory.getSnapshot(slotName);
                        if (snapshot != null) {
                           ResourceLocation replacementSkill = snapshot.replacementSkill();
                           if (replacementSkill == null) {
                              Skill currentSkill = playerPatch.getSkill(slot).getSkill();
                              replacementSkill = currentSkill == null ? null : currentSkill.getRegistryName();
                           }

                           if (ExclusiveSkillStateApi.importLegacySnapshot(
                              player, slot, snapshot.originalSkill(), snapshot.originalDisabled(), replacementSkill, snapshot.sourceItem()
                           )) {
                              memory.removeSnapshot(slotName);
                           }
                        }
                     }
                  }
               );
         }
      }
   }

   @EventBusSubscriber(
      modid = "invincible_dmc",
      bus = Bus.MOD
   )
   public static class ModBusEvents {
      @SubscribeEvent
      public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
         event.register(OriginalSkillCapability.IOriginalSkillMemory.class);
      }
   }
}
