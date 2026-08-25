package com.pla.annoyingvillagers.network;

import com.pla.annoyingvillagers.client.engine.ClientPacketHandlers;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

public record ClientboundBlackFireFx(int entityId) {
   public ClientboundBlackFireFx(Entity entity) {
      this(entity.m_19879_());
   }

   public static void encode(ClientboundBlackFireFx msg, FriendlyByteBuf buf) {
      buf.m_130130_(msg.entityId);
   }

   public static ClientboundBlackFireFx decode(FriendlyByteBuf buf) {
      return new ClientboundBlackFireFx(buf.m_130242_());
   }

   public static void handle(ClientboundBlackFireFx msg, Supplier<Context> ctx) {
      Context c = ctx.get();
      c.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandlers.handleBlackFire(msg)));
      c.setPacketHandled(true);
   }
}
