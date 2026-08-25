package com.pla.annoyingvillagers.event;

import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.FORGE
)
public class NullWeaponCleanupEvent {
   private static final String NBT_SPENT_STACKS = "AV_NullWeaponSpentStacks";
   private static final List<String> NULL_WEAPON_KEYS = List.of("NullSwordUUID", "NullAxeUUID", "NullPickaxeUUID", "NullHoeUUID", "NullShovelUUID");

   @SubscribeEvent
   public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         CompoundTag data = player.getPersistentData();
         data.m_128473_("AV_NullWeaponSpentStacks");

         for (String key : NULL_WEAPON_KEYS) {
            data.m_128473_(key);
         }
      }
   }
}
