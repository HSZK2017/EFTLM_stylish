package com.dmc.invincible_dmc.network.client;

import com.dmc.invincible_dmc.capability.doppelganger.DoppelgangerCapability;
import com.dmc.invincible_dmc.input.PlayerMovementFrame;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public record CPMovementInputPacket(PlayerMovementFrame frame) {
   public static void toBytes(CPMovementInputPacket msg, FriendlyByteBuf buf) {
      msg.frame.write(buf);
   }

   public static CPMovementInputPacket fromBytes(FriendlyByteBuf buf) {
      return new CPMovementInputPacket(PlayerMovementFrame.read(buf));
   }

   public static void handle(CPMovementInputPacket msg, Supplier<Context> ctx) {
      ctx.get().enqueueWork(() -> {
         ServerPlayer sender = ctx.get().getSender();
         if (sender != null) {
            sender.getCapability(DoppelgangerCapability.INSTANCE).ifPresent(cap -> cap.setLastMovementFrame(msg.frame));
         }
      });
      ctx.get().setPacketHandled(true);
   }
}
