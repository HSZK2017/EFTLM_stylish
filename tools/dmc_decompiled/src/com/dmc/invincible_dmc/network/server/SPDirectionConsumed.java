package com.dmc.invincible_dmc.network.server;

import com.dmc.invincible_dmc.client.input.DMComboEngine;
import com.dmc.invincible_dmc.client.input.IComboExecutor;
import com.dmc.invincible_dmc.utils.DMCLog;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class SPDirectionConsumed {
   public static void encode(SPDirectionConsumed msg, FriendlyByteBuf buf) {
   }

   public static SPDirectionConsumed decode(FriendlyByteBuf buf) {
      return new SPDirectionConsumed();
   }

   public static void handle(SPDirectionConsumed msg, Supplier<Context> ctx) {
      ctx.get().enqueueWork(() -> {
         IComboExecutor dispatcher = DMComboEngine.getLocalPlayerDispatcher();
         if (dispatcher != null) {
            dispatcher.getDirectionTracker().consume(DMComboEngine.engineTick);
            DMCLog.debug(DMCLog.Category.DIRECTION, "[DirSeq] Consumed direction history (server-confirmed feedback)");
         }
      });
      ctx.get().setPacketHandled(true);
   }
}
