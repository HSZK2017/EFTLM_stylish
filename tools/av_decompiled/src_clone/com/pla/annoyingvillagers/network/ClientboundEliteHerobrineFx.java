package com.pla.annoyingvillagers.network;

import com.pla.annoyingvillagers.client.engine.ClientPacketHandlers;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

public record ClientboundEliteHerobrineFx(int entityId, int tickCount, Vec3 pos, boolean extraParticle) {
   public static void encode(ClientboundEliteHerobrineFx msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.entityId);
      buf.writeInt(msg.tickCount);
      buf.writeDouble(msg.pos.f_82479_);
      buf.writeDouble(msg.pos.f_82480_);
      buf.writeDouble(msg.pos.f_82481_);
      buf.writeBoolean(msg.extraParticle);
   }

   public static ClientboundEliteHerobrineFx decode(FriendlyByteBuf buf) {
      int entityId = buf.readInt();
      int tickCount = buf.readInt();
      Vec3 pos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
      return new ClientboundEliteHerobrineFx(entityId, tickCount, pos, buf.readBoolean());
   }

   public static void handle(ClientboundEliteHerobrineFx msg, Supplier<Context> ctx) {
      Context c = ctx.get();
      c.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandlers.handleEliteHerobrineFx(msg)));
      c.setPacketHandled(true);
   }
}
