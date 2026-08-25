package com.pla.annoyingvillagers.network;

import com.pla.annoyingvillagers.client.engine.ClientPacketHandlers;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

public record ClientboundWoopieSwordWindFx(Vec3 from) {
   public static void encode(ClientboundWoopieSwordWindFx msg, FriendlyByteBuf buf) {
      buf.writeDouble(msg.from.f_82479_);
      buf.writeDouble(msg.from.f_82480_);
      buf.writeDouble(msg.from.f_82481_);
   }

   public static ClientboundWoopieSwordWindFx decode(FriendlyByteBuf buf) {
      Vec3 f = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
      return new ClientboundWoopieSwordWindFx(f);
   }

   public static void handle(ClientboundWoopieSwordWindFx msg, Supplier<Context> ctx) {
      Context c = ctx.get();
      c.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandlers.handleWoopieSwordWind(msg)));
      c.setPacketHandled(true);
   }
}
