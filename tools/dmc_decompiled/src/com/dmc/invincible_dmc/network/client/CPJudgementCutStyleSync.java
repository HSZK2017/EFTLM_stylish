package com.dmc.invincible_dmc.network.client;

import com.dmc.invincible_dmc.entity.judgementcut.JudgementCutStyleSync;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public record CPJudgementCutStyleSync(byte style) {
   public static void encode(CPJudgementCutStyleSync packet, FriendlyByteBuf buffer) {
      buffer.writeByte(packet.style);
   }

   public static CPJudgementCutStyleSync decode(FriendlyByteBuf buffer) {
      return new CPJudgementCutStyleSync(buffer.readByte());
   }

   public static void handle(CPJudgementCutStyleSync packet, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      ServerPlayer sender = context.getSender();
      context.enqueueWork(() -> {
         if (sender != null) {
            JudgementCutStyleSync.setPlayerStyle(sender, packet.style);
         }
      });
      context.setPacketHandled(true);
   }
}
