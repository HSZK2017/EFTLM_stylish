package com.pla.annoyingvillagers.network;

import com.pla.annoyingvillagers.client.engine.ClientPacketHandlers;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

public record ClientboundMuteExplosionAtPos(BlockPos pos, int lifetimeTicks) {
   public static void encode(ClientboundMuteExplosionAtPos msg, FriendlyByteBuf buf) {
      buf.m_130064_(msg.pos);
      buf.m_130130_(msg.lifetimeTicks);
   }

   public static ClientboundMuteExplosionAtPos decode(FriendlyByteBuf buf) {
      return new ClientboundMuteExplosionAtPos(buf.m_130135_(), buf.m_130242_());
   }

   public static void handle(ClientboundMuteExplosionAtPos msg, Supplier<Context> ctx) {
      Context c = ctx.get();
      c.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandlers.handleMuteExplosionAtPos(msg)));
      c.setPacketHandled(true);
   }
}
