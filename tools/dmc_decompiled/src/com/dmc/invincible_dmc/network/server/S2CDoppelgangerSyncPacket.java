package com.dmc.invincible_dmc.network.server;

import com.dmc.invincible_dmc.capability.doppelganger.DoppelgangerCapability;
import com.dmc.invincible_dmc.client.DoppelgangerClientBindingSync;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;
import org.jetbrains.annotations.Nullable;

public record S2CDoppelgangerSyncPacket(
   int ownerEntityId,
   UUID ownerUUID,
   int doppelgangerEntityId,
   @Nullable UUID doppelgangerUUID,
   long generation,
   DoppelgangerCapability.BindingState state,
   String dimension,
   int delayMode
) {
   public static void encode(S2CDoppelgangerSyncPacket packet, FriendlyByteBuf buffer) {
      buffer.m_130130_(packet.ownerEntityId);
      buffer.m_130077_(packet.ownerUUID);
      buffer.m_130130_(packet.doppelgangerEntityId);
      buffer.writeBoolean(packet.doppelgangerUUID != null);
      if (packet.doppelgangerUUID != null) {
         buffer.m_130077_(packet.doppelgangerUUID);
      }

      buffer.m_130103_(packet.generation);
      buffer.m_130068_(packet.state);
      buffer.m_130070_(packet.dimension);
      buffer.writeByte(packet.delayMode);
   }

   public static S2CDoppelgangerSyncPacket decode(FriendlyByteBuf buffer) {
      int ownerEntityId = buffer.m_130242_();
      UUID ownerUUID = buffer.m_130259_();
      int doppelgangerEntityId = buffer.m_130242_();
      UUID uuid = buffer.readBoolean() ? buffer.m_130259_() : null;
      long generation = buffer.m_130258_();
      DoppelgangerCapability.BindingState state = (DoppelgangerCapability.BindingState)buffer.m_130066_(DoppelgangerCapability.BindingState.class);
      String dimension = buffer.m_130277_();
      int delayMode = buffer.readByte();
      return new S2CDoppelgangerSyncPacket(ownerEntityId, ownerUUID, doppelgangerEntityId, uuid, generation, state, dimension, delayMode);
   }

   public static void handle(S2CDoppelgangerSyncPacket packet, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> DoppelgangerClientBindingSync.applyOrQueue(packet)));
      context.setPacketHandled(true);
   }
}
