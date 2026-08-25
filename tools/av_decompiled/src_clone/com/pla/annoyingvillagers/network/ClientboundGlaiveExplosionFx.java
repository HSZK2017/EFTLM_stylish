package com.pla.annoyingvillagers.network;

import com.pla.annoyingvillagers.client.engine.ClientPacketHandlers;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

public record ClientboundGlaiveExplosionFx(Vec3 from, Vec3 to) {
   public static void encode(ClientboundGlaiveExplosionFx msg, FriendlyByteBuf buf) {
      buf.writeDouble(msg.from.f_82479_);
      buf.writeDouble(msg.from.f_82480_);
      buf.writeDouble(msg.from.f_82481_);
      buf.writeDouble(msg.to.f_82479_);
      buf.writeDouble(msg.to.f_82480_);
      buf.writeDouble(msg.to.f_82481_);
   }

   public static ClientboundGlaiveExplosionFx decode(FriendlyByteBuf buf) {
      Vec3 f = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
      Vec3 t = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
      return new ClientboundGlaiveExplosionFx(f, t);
   }

   public static void handle(ClientboundGlaiveExplosionFx msg, Supplier<Context> ctx) {
      Context c = ctx.get();
      c.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandlers.handleGlaiveExplosion(msg)));
      c.setPacketHandled(true);
   }
}
