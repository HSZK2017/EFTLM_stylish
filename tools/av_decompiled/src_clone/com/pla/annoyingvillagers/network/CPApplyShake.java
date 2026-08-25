package com.pla.annoyingvillagers.network;

import com.pla.annoyingvillagers.client.engine.CameraEngine;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class CPApplyShake {
   private final int time;
   private final float strength;
   private final float frequency;
   private final int decay_time;

   public CPApplyShake(int time, float strength, float frequency, int decay_time) {
      this.time = time;
      this.strength = strength;
      this.frequency = frequency;
      this.decay_time = decay_time;
   }

   public CPApplyShake(FriendlyByteBuf buf) {
      this.time = buf.readInt();
      this.strength = buf.readFloat();
      this.frequency = buf.readFloat();
      this.decay_time = buf.readInt();
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(this.time);
      buf.writeFloat(this.strength);
      buf.writeFloat(this.frequency);
      buf.writeInt(this.decay_time);
   }

   public void handle(Supplier<Context> context) {
      Context ctx = context.get();
      ctx.enqueueWork(() -> {
         CameraEngine engine = CameraEngine.getInstance();
         if (engine != null) {
            engine.shakeCamera(this.strength, this.time, this.frequency, this.decay_time);
         }
      });
      ctx.setPacketHandled(true);
   }
}
