package com.dmc.invincible_dmc.network.client;

import com.dmc.invincible_dmc.event.YamatoSheathServerHandler;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public record CPYamatoSheath(ResourceLocation animation) {
   public static void encode(CPYamatoSheath packet, FriendlyByteBuf buffer) {
      buffer.m_130085_(packet.animation);
   }

   public static CPYamatoSheath decode(FriendlyByteBuf buffer) {
      return new CPYamatoSheath(buffer.m_130281_());
   }

   public static void handle(CPYamatoSheath packet, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null) {
            YamatoSheathServerHandler.handleNotification(sender, packet.animation);
         }
      });
      context.setPacketHandled(true);
   }
}
