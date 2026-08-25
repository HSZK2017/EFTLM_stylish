package com.dmc.invincible_dmc.network.server;

import com.dmc.invincible_dmc.client.render.shake.CameraShakeManager;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

public class S2CCameraShakePacket {
   private final Vec3 targetPos;
   private final float intensity;
   private final int duration;
   private final float frequency;

   public S2CCameraShakePacket(Vec3 targetPos, float intensity, int duration, float frequency) {
      this.targetPos = targetPos;
      this.intensity = intensity;
      this.duration = duration;
      this.frequency = frequency;
   }

   public static void encode(S2CCameraShakePacket msg, FriendlyByteBuf buf) {
      buf.writeDouble(msg.targetPos.f_82479_);
      buf.writeDouble(msg.targetPos.f_82480_);
      buf.writeDouble(msg.targetPos.f_82481_);
      buf.writeFloat(msg.intensity);
      buf.writeInt(msg.duration);
      buf.writeFloat(msg.frequency);
   }

   public static S2CCameraShakePacket decode(FriendlyByteBuf buf) {
      return new S2CCameraShakePacket(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()), buf.readFloat(), buf.readInt(), buf.readFloat());
   }

   public static void handle(S2CCameraShakePacket msg, Supplier<Context> ctxSupplier) {
      Context context = ctxSupplier.get();
      context.enqueueWork(
         () -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CameraShakeManager.addShake(msg.targetPos, msg.intensity, msg.duration, msg.frequency))
      );
      context.setPacketHandled(true);
   }
}
