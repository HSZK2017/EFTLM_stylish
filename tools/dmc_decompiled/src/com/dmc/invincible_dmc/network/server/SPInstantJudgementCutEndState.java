package com.dmc.invincible_dmc.network.server;

import com.dmc.invincible_dmc.client.InstantJudgementCutEndClientState;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

public record SPInstantJudgementCutEndState(boolean learned, boolean enabled) {
   public static void encode(SPInstantJudgementCutEndState packet, FriendlyByteBuf buffer) {
      buffer.writeBoolean(packet.learned);
      buffer.writeBoolean(packet.enabled);
   }

   public static SPInstantJudgementCutEndState decode(FriendlyByteBuf buffer) {
      return new SPInstantJudgementCutEndState(buffer.readBoolean(), buffer.readBoolean());
   }

   public static void handle(SPInstantJudgementCutEndState packet, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> InstantJudgementCutEndClientState.apply(packet.learned, packet.enabled)));
      context.setPacketHandled(true);
   }
}
