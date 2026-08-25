package com.dmc.invincible_dmc.network.client;

import com.dmc.invincible_dmc.entity.portal.PortalDestinationType;
import com.dmc.invincible_dmc.entity.portal.PortalEntity;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent.Context;

public record CPPortalDestinationChosen(
   PortalDestinationType type, UUID waystoneUid, ResourceKey<Level> dimension, BlockPos pos, int customCoordinateMask, int portalId
) {
   public CPPortalDestinationChosen(PortalDestinationType type, UUID waystoneUid, ResourceKey<Level> dimension, BlockPos pos, int portalId) {
      this(type, waystoneUid, dimension, pos, 0, portalId);
   }

   public static void toBytes(CPPortalDestinationChosen msg, FriendlyByteBuf buf) {
      buf.m_130068_(msg.type);
      buf.writeInt(msg.portalId);
      switch (msg.type) {
         case WAYSTONE:
            buf.m_130077_(msg.waystoneUid);
            break;
         case XAERO_WAYPOINT:
         case FTB_CHUNKS:
         case JOURNEYMAP:
            buf.m_130085_(msg.dimension.m_135782_());
            buf.m_130064_(msg.pos);
            break;
         case CUSTOM_COORDINATES:
            buf.writeByte(msg.customCoordinateMask);
            buf.writeInt(msg.pos.m_123341_());
            buf.writeInt(msg.pos.m_123342_());
            buf.writeInt(msg.pos.m_123343_());
      }
   }

   public static CPPortalDestinationChosen fromBytes(FriendlyByteBuf buf) {
      PortalDestinationType type = (PortalDestinationType)buf.m_130066_(PortalDestinationType.class);
      int portalId = buf.readInt();
      UUID waystoneUid = null;
      ResourceKey<Level> dimension = Level.f_46428_;
      BlockPos pos = BlockPos.f_121853_;
      int customCoordinateMask = 0;
      switch (type) {
         case WAYSTONE:
            waystoneUid = buf.m_130259_();
            break;
         case XAERO_WAYPOINT:
         case FTB_CHUNKS:
         case JOURNEYMAP:
            dimension = ResourceKey.m_135785_(Registries.f_256858_, buf.m_130281_());
            pos = buf.m_130135_();
            break;
         case CUSTOM_COORDINATES:
            customCoordinateMask = buf.readUnsignedByte();
            pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
      }

      return new CPPortalDestinationChosen(type, waystoneUid, dimension, pos, customCoordinateMask, portalId);
   }

   public static void handle(CPPortalDestinationChosen msg, Supplier<Context> ctxSupplier) {
      Context context = ctxSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer player = context.getSender();
         if (player != null) {
            if (player.m_9236_().m_6815_(msg.portalId) instanceof PortalEntity portal) {
               portal.onDestinationChosen(player, msg);
            }
         }
      });
      context.setPacketHandled(true);
   }
}
