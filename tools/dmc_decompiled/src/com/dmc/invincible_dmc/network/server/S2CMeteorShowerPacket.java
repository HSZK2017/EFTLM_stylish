package com.dmc.invincible_dmc.network.server;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

public class S2CMeteorShowerPacket {
   private static final int FLOATS_PER_METEOR = 7;
   private static final int MAX_METEORS_PER_PACKET = 64;
   private final float[] data;

   public S2CMeteorShowerPacket(float[] data) {
      this.data = data;
   }

   public static void encode(S2CMeteorShowerPacket msg, FriendlyByteBuf buf) {
      buf.m_130130_(msg.data.length);

      for (float f : msg.data) {
         buf.writeFloat(f);
      }
   }

   public static S2CMeteorShowerPacket decode(FriendlyByteBuf buf) {
      int len = buf.m_130242_();
      if (len >= 0 && len % 7 == 0 && len <= 448) {
         float[] data = new float[len];

         for (int i = 0; i < len; i++) {
            data[i] = buf.readFloat();
         }

         return new S2CMeteorShowerPacket(data);
      } else {
         throw new IllegalArgumentException("Invalid meteor effect payload length: " + len);
      }
   }

   public static void handle(S2CMeteorShowerPacket msg, Supplier<Context> ctx) {
      ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> S2CMeteorShowerHandler.handle(msg)));
      ctx.get().setPacketHandled(true);
   }

   public float[] data() {
      return this.data;
   }
}
