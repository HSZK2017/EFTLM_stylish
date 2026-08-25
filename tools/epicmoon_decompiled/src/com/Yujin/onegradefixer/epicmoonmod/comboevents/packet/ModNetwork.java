package com.Yujin.onegradefixer.epicmoonmod.comboevents.packet;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
   private static final String PROTOCOL_VERSION = "1";
   public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
      new ResourceLocation("epicmoonmod", "main"), () -> "1", "1"::equals, "1"::equals
   );
   private static int id = 0;

   public static void register() {
      CHANNEL.messageBuilder(CameraShakePacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
         .encoder(CameraShakePacket::encode)
         .decoder(CameraShakePacket::decode)
         .consumerMainThread(CameraShakePacket::handle)
         .add();
   }

   public static void sendToPlayer(Object packet, ServerPlayer player) {
      CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
   }
}
