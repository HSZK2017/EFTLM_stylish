package com.pla.annoyingvillagers.network;

import com.pla.annoyingvillagers.client.engine.ClientPacketHandlers;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

public record ClientboundTeleportPortalFx(Vec3 pos, Vec3 normal) {
   public static void encode(ClientboundTeleportPortalFx msg, FriendlyByteBuf buf) {
      buf.writeDouble(msg.pos.f_82479_);
      buf.writeDouble(msg.pos.f_82480_);
      buf.writeDouble(msg.pos.f_82481_);
      buf.writeDouble(msg.normal.f_82479_);
      buf.writeDouble(msg.normal.f_82480_);
      buf.writeDouble(msg.normal.f_82481_);
   }

   public static ClientboundTeleportPortalFx decode(FriendlyByteBuf buf) {
      Vec3 pos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
      Vec3 normal = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
      return new ClientboundTeleportPortalFx(pos, normal);
   }

   public static void handle(ClientboundTeleportPortalFx msg, Supplier<Context> ctx) {
      Context context = ctx.get();
      context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandlers.handleTeleportPortalFx(msg)));
      context.setPacketHandled(true);
   }
}
