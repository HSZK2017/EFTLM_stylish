package com.dmc.invincible_dmc.api.events;

import com.dmc.invincible_dmc.capability.DMCPlayerCapabilityProvider;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class TimePeriodEvent {
   private final float start;
   private final float end;
   private final BaseConsumer event;
   private final Side side;

   public TimePeriodEvent(float start, float end, BaseConsumer consumer) {
      this.event = consumer;
      this.start = start;
      this.end = end;
      this.side = Side.SERVER;
   }

   public TimePeriodEvent(float start, float end, BaseConsumer consumer, Side side) {
      this.event = consumer;
      this.start = start;
      this.end = end;
      this.side = side;
   }

   public void testAndExecute(PlayerPatch<?> playerPatch, float elapsed) {
      if (elapsed >= this.start && elapsed < this.end && this.side.test(playerPatch.getOriginal())) {
         this.event.accept(playerPatch, playerPatch.getTarget(), DMCPlayerCapabilityProvider.get((Player)playerPatch.getOriginal()));
      }
   }
}
