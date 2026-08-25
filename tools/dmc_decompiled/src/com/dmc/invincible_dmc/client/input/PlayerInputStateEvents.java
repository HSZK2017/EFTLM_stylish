package com.dmc.invincible_dmc.client.input;

import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "invincible_dmc",
   bus = Bus.FORGE
)
public final class PlayerInputStateEvents {
   private PlayerInputStateEvents() {
   }

   @SubscribeEvent
   public static void onPlayerClone(Clone event) {
      PlayerInputState.remove(event.getOriginal());
      PlayerInputState.remove(event.getEntity());
   }

   @SubscribeEvent
   public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
      PlayerInputState.remove(event.getEntity());
   }
}
