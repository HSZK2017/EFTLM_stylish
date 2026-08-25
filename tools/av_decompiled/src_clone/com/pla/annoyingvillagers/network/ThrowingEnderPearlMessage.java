package com.pla.annoyingvillagers.network;

import com.pla.annoyingvillagers.AnnoyingVillagers;
import com.pla.annoyingvillagers.event.ThrowingPearlKeyPressedEvent;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent.Context;

@EventBusSubscriber(
   bus = Bus.MOD
)
public class ThrowingEnderPearlMessage {
   int type;
   int pressedms;

   public ThrowingEnderPearlMessage(int i, int j) {
      this.type = i;
      this.pressedms = j;
   }

   public ThrowingEnderPearlMessage(FriendlyByteBuf friendlybytebuf) {
      this.type = friendlybytebuf.readInt();
      this.pressedms = friendlybytebuf.readInt();
   }

   public static void buffer(ThrowingEnderPearlMessage throwingEnderPearlMessage, FriendlyByteBuf friendlybytebuf) {
      friendlybytebuf.writeInt(throwingEnderPearlMessage.type);
      friendlybytebuf.writeInt(throwingEnderPearlMessage.pressedms);
   }

   public static void handler(ThrowingEnderPearlMessage throwingEnderPearlMessage, Supplier<Context> supplier) {
      Context context = supplier.get();
      context.enqueueWork(() -> pressAction(context.getSender(), throwingEnderPearlMessage.type, throwingEnderPearlMessage.pressedms));
      context.setPacketHandled(true);
   }

   public static void pressAction(Player player, int i, int j) {
      Level level = player.m_9236_();
      if (level.m_46805_(player.m_20183_()) && !level.m_5776_() && i == 0) {
         ThrowingPearlKeyPressedEvent.execute(player);
      }
   }

   @SubscribeEvent
   public static void registerMessage(FMLCommonSetupEvent fmlcommonsetupevent) {
      AnnoyingVillagers.addNetworkMessage(
         ThrowingEnderPearlMessage.class, ThrowingEnderPearlMessage::buffer, ThrowingEnderPearlMessage::new, ThrowingEnderPearlMessage::handler
      );
   }
}
