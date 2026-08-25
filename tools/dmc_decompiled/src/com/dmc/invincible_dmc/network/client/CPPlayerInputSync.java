package com.dmc.invincible_dmc.network.client;

import com.dmc.invincible_dmc.client.input.PlayerInputState;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public record CPPlayerInputSync(short keyMask, long sequence) {
   public CPPlayerInputSync(short keyMask) {
      this(keyMask, 0L);
   }

   public static void toBytes(CPPlayerInputSync msg, FriendlyByteBuf buf) {
      buf.writeShort(msg.keyMask);
      buf.writeLong(msg.sequence);
   }

   public static CPPlayerInputSync fromBytes(FriendlyByteBuf buf) {
      return new CPPlayerInputSync(buf.readShort(), buf.readLong());
   }

   public static void handle(CPPlayerInputSync msg, Supplier<Context> ctx) {
      Context context = ctx.get();
      context.enqueueWork(() -> {
         ServerPlayer player = context.getSender();
         if (player != null) {
            PlayerInputState.updateRemote(player, msg.keyMask, msg.sequence);
         }
      });
      context.setPacketHandled(true);
   }
}
