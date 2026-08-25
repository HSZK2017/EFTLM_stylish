package com.dmc.invincible_dmc.network.server;

import com.dmc.invincible_dmc.client.input.DMComboEngine;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class SPCrazyComboReset {
   public static void toBytes(SPCrazyComboReset msg, FriendlyByteBuf buf) {
   }

   public static SPCrazyComboReset fromBytes(FriendlyByteBuf buf) {
      return new SPCrazyComboReset();
   }

   public static void handle(SPCrazyComboReset msg, Supplier<Context> ctx) {
      ctx.get().enqueueWork(() -> {
         if (DMComboEngine.getLocalPlayerDispatcher() != null) {
            DMComboEngine.getLocalPlayerDispatcher().resetCrazyCombo();
         }
      });
      ctx.get().setPacketHandled(true);
   }
}
