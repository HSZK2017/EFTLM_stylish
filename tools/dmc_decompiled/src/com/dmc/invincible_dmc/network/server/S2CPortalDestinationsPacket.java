package com.dmc.invincible_dmc.network.server;

import com.dmc.invincible_dmc.client.gui.PortalDestinationScreen;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

public record S2CPortalDestinationsPacket(
   boolean hasRespawnPoint,
   ResourceKey<Level> respawnDimension,
   BlockPos respawnPos,
   BlockPos worldSpawnPos,
   ResourceKey<Level> currentDimension,
   List<S2CPortalDestinationsPacket.WaystoneEntry> waystones,
   int portalId
) {
   public static void encode(S2CPortalDestinationsPacket msg, FriendlyByteBuf buf) {
      buf.writeBoolean(msg.hasRespawnPoint);
      buf.m_130085_(msg.respawnDimension.m_135782_());
      buf.writeBoolean(msg.respawnPos != null);
      if (msg.respawnPos != null) {
         buf.m_130064_(msg.respawnPos);
      }

      buf.m_130064_(msg.worldSpawnPos);
      buf.m_130085_(msg.currentDimension.m_135782_());
      buf.m_236828_(msg.waystones, (b, w) -> w.write(b));
      buf.writeInt(msg.portalId);
   }

   public static S2CPortalDestinationsPacket decode(FriendlyByteBuf buf) {
      boolean hasRespawn = buf.readBoolean();
      ResourceKey<Level> respawnDim = ResourceKey.m_135785_(Registries.f_256858_, buf.m_130281_());
      BlockPos respawnPos = buf.readBoolean() ? buf.m_130135_() : null;
      BlockPos worldSpawn = buf.m_130135_();
      ResourceKey<Level> currentDim = ResourceKey.m_135785_(Registries.f_256858_, buf.m_130281_());
      List<S2CPortalDestinationsPacket.WaystoneEntry> waystones = buf.m_236845_(S2CPortalDestinationsPacket.WaystoneEntry::read);
      int portalId = buf.readInt();
      return new S2CPortalDestinationsPacket(hasRespawn, respawnDim, respawnPos, worldSpawn, currentDim, waystones, portalId);
   }

   public static void handle(S2CPortalDestinationsPacket msg, Supplier<Context> ctxSupplier) {
      Context context = ctxSupplier.get();
      context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PortalDestinationScreen.openOrAutoResolve(msg)));
      context.setPacketHandled(true);
   }

   public static record WaystoneEntry(UUID uid, String name, ResourceKey<Level> dimension, BlockPos pos, boolean isGlobal) {
      public void write(FriendlyByteBuf buf) {
         buf.m_130077_(this.uid);
         buf.m_130070_(this.name);
         buf.m_130085_(this.dimension.m_135782_());
         buf.m_130064_(this.pos);
         buf.writeBoolean(this.isGlobal);
      }

      public static S2CPortalDestinationsPacket.WaystoneEntry read(FriendlyByteBuf buf) {
         return new S2CPortalDestinationsPacket.WaystoneEntry(
            buf.m_130259_(), buf.m_130277_(), ResourceKey.m_135785_(Registries.f_256858_, buf.m_130281_()), buf.m_130135_(), buf.readBoolean()
         );
      }
   }
}
