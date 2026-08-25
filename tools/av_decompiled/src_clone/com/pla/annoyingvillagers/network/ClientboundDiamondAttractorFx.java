package com.pla.annoyingvillagers.network;

import com.pla.annoyingvillagers.client.engine.ClientPacketHandlers;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

public record ClientboundDiamondAttractorFx(int entityId) {
   public ClientboundDiamondAttractorFx(Entity entity) {
      this(entity.m_19879_());
   }

   public static void encode(ClientboundDiamondAttractorFx msg, FriendlyByteBuf buf) {
      buf.m_130130_(msg.entityId);
   }

   public static ClientboundDiamondAttractorFx decode(FriendlyByteBuf buf) {
      return new ClientboundDiamondAttractorFx(buf.m_130242_());
   }

   public static void handle(ClientboundDiamondAttractorFx msg, Supplier<Context> ctx) {
      Context c = ctx.get();
      c.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandlers.handleDiamondAttractor(msg)));
      c.setPacketHandled(true);
   }
}
