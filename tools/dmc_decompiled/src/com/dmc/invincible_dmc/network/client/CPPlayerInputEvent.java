package com.dmc.invincible_dmc.network.client;

import com.dmc.invincible_dmc.api.skill.ComboType;
import com.dmc.invincible_dmc.client.input.ComboIntentResolver;
import com.dmc.invincible_dmc.client.input.DirectionTracker;
import com.dmc.invincible_dmc.conditions.DirectionalSequenceCondition;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerInputHandler;
import com.dmc.invincible_dmc.network.DMCNetwork;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class CPPlayerInputEvent {
   private final int typeOrdinal;
   private final int pressedTime;
   private final long pressIntervalMs;
   private final boolean isLongPress;
   private final int directionMask;
   private final List<DirectionTracker.DirectionEvent> directionEvents;
   private final long engineTick;

   public CPPlayerInputEvent(
      int typeOrdinal,
      int pressedTime,
      long pressIntervalMs,
      boolean isLongPress,
      int directionMask,
      List<DirectionTracker.DirectionEvent> directionEvents,
      long engineTick
   ) {
      this.typeOrdinal = typeOrdinal;
      this.pressedTime = pressedTime;
      this.pressIntervalMs = pressIntervalMs;
      this.isLongPress = isLongPress;
      this.directionMask = directionMask;
      this.directionEvents = directionEvents;
      this.engineTick = engineTick;
   }

   public static void send(ComboIntentResolver.ComboInputIntent intent) {
      DMCNetwork.sendToServer(
         new CPPlayerInputEvent(
            intent.type().universalOrdinal(),
            intent.pressDuration(),
            intent.pressIntervalMs(),
            intent.isLongPress(),
            intent.directionMask(),
            intent.directionEvents(),
            intent.captureTick()
         )
      );
   }

   public static void toBytes(CPPlayerInputEvent msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.typeOrdinal);
      buf.writeInt(msg.pressedTime);
      buf.writeLong(msg.pressIntervalMs);
      buf.writeBoolean(msg.isLongPress);
      buf.writeInt(msg.directionMask);
      int count = Math.min(msg.directionEvents.size(), 16);
      buf.writeByte(count);

      for (int i = 0; i < count; i++) {
         DirectionTracker.DirectionEvent e = msg.directionEvents.get(i);
         buf.writeByte(e.direction().ordinal());
         buf.writeLong(e.tick());
      }

      buf.writeLong(msg.engineTick);
   }

   public static CPPlayerInputEvent fromBytes(FriendlyByteBuf buf) {
      int typeOrdinal = buf.readInt();
      int pressedTime = buf.readInt();
      long pressIntervalMs = buf.readLong();
      boolean isLongPress = buf.readBoolean();
      int directionMask = buf.readInt();
      int eventCount = buf.readByte();
      List<DirectionTracker.DirectionEvent> events = new ArrayList<>(eventCount);

      for (int i = 0; i < eventCount; i++) {
         byte dirOrdinal = buf.readByte();
         long tick = buf.readLong();
         events.add(new DirectionTracker.DirectionEvent(DirectionalSequenceCondition.Direction.values()[dirOrdinal], tick));
      }

      long engineTick = buf.readLong();
      return new CPPlayerInputEvent(typeOrdinal, pressedTime, pressIntervalMs, isLongPress, directionMask, events, engineTick);
   }

   public static void handle(CPPlayerInputEvent msg, Supplier<Context> ctx) {
      ctx.get()
         .enqueueWork(
            () -> {
               ServerPlayer sender = ctx.get().getSender();
               if (sender != null) {
                  ComboType type = (ComboType)ComboType.ENUM_MANAGER.get(msg.typeOrdinal);
                  if (type != null) {
                     DoppelgangerInputHandler.dispatch(
                        sender, type, msg.pressedTime, msg.pressIntervalMs, msg.isLongPress, msg.directionMask, msg.directionEvents, msg.engineTick
                     );
                  }
               }
            }
         );
      ctx.get().setPacketHandled(true);
   }
}
