package org.merlin204.mimic.network;

import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.merlin204.mimic.network.packet.BasePacket;
import org.merlin204.mimic.network.packet.client.SyncBossBarPacket;

public class PacketHandler {
   private static final String PROTOCOL_VERSION = "1";
   public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
      ResourceLocation.fromNamespaceAndPath("mimic", "main"), () -> "1", "1"::equals, "1"::equals
   );
   private static int index;

   public static synchronized void register() {
      register(SyncBossBarPacket.class, SyncBossBarPacket::decode);
   }

   private static <MSG extends BasePacket> void register(Class<MSG> packet, Function<FriendlyByteBuf, MSG> decoder) {
      INSTANCE.messageBuilder(packet, index++).encoder(BasePacket::encode).decoder(decoder).consumerMainThread(BasePacket::handle).add();
   }
}
