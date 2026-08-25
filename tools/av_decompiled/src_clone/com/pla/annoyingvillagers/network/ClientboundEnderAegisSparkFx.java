package com.pla.annoyingvillagers.network;

import com.pla.annoyingvillagers.client.engine.ClientPacketHandlers;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

public record ClientboundEnderAegisSparkFx(Vec3 from, Vec3 to) {
   public static void encode(ClientboundEnderAegisSparkFx msg, FriendlyByteBuf buf) {
      buf.writeDouble(msg.from.f_82479_);
      buf.writeDouble(msg.from.f_82480_);
      buf.writeDouble(msg.from.f_82481_);
      buf.writeDouble(msg.to.f_82479_);
      buf.writeDouble(msg.to.f_82480_);
      buf.writeDouble(msg.to.f_82481_);
   }

   public static ClientboundEnderAegisSparkFx decode(FriendlyByteBuf buf) {
      Vec3 from = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
      Vec3 to = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
      return new ClientboundEnderAegisSparkFx(from, to);
   }

   public static void handle(ClientboundEnderAegisSparkFx msg, Supplier<Context> ctx) {
      Context c = ctx.get();
      c.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandlers.handleEnderAegisSparkFx(msg)));
      c.setPacketHandled(true);
   }
}
