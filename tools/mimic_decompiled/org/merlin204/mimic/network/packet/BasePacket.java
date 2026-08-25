package org.merlin204.mimic.network.packet;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent.Context;
import org.jetbrains.annotations.Nullable;

public interface BasePacket {
   void encode(FriendlyByteBuf var1);

   default boolean handle(Supplier<Context> context) {
      if (context == null) {
         return false;
      } else {
         Context networkContext = context.get();
         if (networkContext == null) {
            return false;
         } else {
            networkContext.enqueueWork(() -> this.execute(networkContext.getSender()));
            networkContext.setPacketHandled(true);
            return true;
         }
      }
   }

   void execute(@Nullable Player var1);
}
