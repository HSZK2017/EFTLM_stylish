package com.dmc.invincible_dmc.network.client;

import com.dmc.invincible_dmc.entity.portal.PortalStyleSync;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public record CPPortalStyleSync(byte style) {
   public static void encode(CPPortalStyleSync packet, FriendlyByteBuf buffer) {
      buffer.writeByte(packet.style);
   }

   public static CPPortalStyleSync decode(FriendlyByteBuf buffer) {
      return new CPPortalStyleSync(buffer.readByte());
   }

   public static void handle(CPPortalStyleSync packet, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      ServerPlayer sender = context.getSender();
      context.enqueueWork(() -> {
         if (sender != null) {
            PortalStyleSync.setPlayerStyle(sender, packet.style);
         }
      });
      context.setPacketHandled(true);
   }
}
