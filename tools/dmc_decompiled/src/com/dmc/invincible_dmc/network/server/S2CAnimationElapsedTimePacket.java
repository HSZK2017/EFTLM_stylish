package com.dmc.invincible_dmc.network.server;

import com.dmc.invincible_dmc.client.network.ClientAnimationElapsedTimeHandler;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

public record S2CAnimationElapsedTimePacket(int entityId, int animationId, float elapsedTime) {
   public static void encode(S2CAnimationElapsedTimePacket packet, FriendlyByteBuf buffer) {
      buffer.m_130130_(packet.entityId);
      buffer.m_130130_(packet.animationId);
      buffer.writeFloat(packet.elapsedTime);
   }

   public static S2CAnimationElapsedTimePacket decode(FriendlyByteBuf buffer) {
      return new S2CAnimationElapsedTimePacket(buffer.m_130242_(), buffer.m_130242_(), buffer.readFloat());
   }

   public static void handle(S2CAnimationElapsedTimePacket packet, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientAnimationElapsedTimeHandler.apply(packet)));
      context.setPacketHandled(true);
   }
}
