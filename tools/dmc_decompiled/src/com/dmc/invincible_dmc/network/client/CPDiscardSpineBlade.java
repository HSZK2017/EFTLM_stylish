package com.dmc.invincible_dmc.network.client;

import com.dmc.invincible_dmc.entity.summonedsword.SpineBladeEntity;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public record CPDiscardSpineBlade(UUID controllerUUID) {
   public static void encode(CPDiscardSpineBlade message, FriendlyByteBuf buffer) {
      buffer.m_130077_(message.controllerUUID);
   }

   public static CPDiscardSpineBlade decode(FriendlyByteBuf buffer) {
      return new CPDiscardSpineBlade(buffer.m_130259_());
   }

   public static void handle(CPDiscardSpineBlade message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer player = context.getSender();
         if (player != null) {
            SpineBladeEntity.discardOwnedBy(player, message.controllerUUID);
         }
      });
      context.setPacketHandled(true);
   }
}
