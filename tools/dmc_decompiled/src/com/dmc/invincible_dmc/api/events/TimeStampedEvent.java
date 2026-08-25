package com.dmc.invincible_dmc.api.events;

import com.dmc.invincible_dmc.capability.DMCPlayerCapabilityProvider;
import java.util.function.Consumer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class TimeStampedEvent implements Comparable<TimeStampedEvent> {
   private final float time;
   private final BaseConsumer event;
   private final Side side;

   public TimeStampedEvent(float time, BaseConsumer event) {
      this.time = time;
      this.event = event;
      this.side = Side.SERVER;
   }

   public TimeStampedEvent(float time, BaseConsumer event, Side side) {
      this.time = time;
      this.event = event;
      this.side = side;
   }

   @Deprecated
   public TimeStampedEvent(float time, Consumer<PlayerPatch<?>> event) {
      this.time = time;
      this.event = (playerPatch, target, invinciblePlayer) -> event.accept(playerPatch);
      this.side = Side.SERVER;
   }

   public static TimeStampedEvent createTimeCommandEvent(float time, String command, boolean isTarget) {
      BaseConsumer event = (entityPatch, target, invinciblePlayer) -> {
         Level server = ((Player)entityPatch.getOriginal()).m_9236_();
         CommandSourceStack css = ((Player)entityPatch.getOriginal()).m_20203_().m_81325_(2).m_81324_();
         if (isTarget && entityPatch.getTarget() != null) {
            css = css.m_81329_(entityPatch.getTarget());
         }

         if (server.m_7654_() != null && entityPatch.getOriginal() != null) {
            server.m_7654_().m_129892_().m_230957_(css, command);
         }
      };
      return new TimeStampedEvent(time, event);
   }

   public void testAndExecute(PlayerPatch<?> playerPatch, float prevElapsed, float elapsed) {
      if (this.time >= prevElapsed && this.time < elapsed && this.side.test(playerPatch.getOriginal())) {
         this.event.accept(playerPatch, playerPatch.getTarget(), DMCPlayerCapabilityProvider.get((Player)playerPatch.getOriginal()));
      }
   }

   public int compareTo(@NotNull TimeStampedEvent event) {
      if (this.time == event.time) {
         return 0;
      } else {
         return this.time > event.time ? 1 : -1;
      }
   }
}
