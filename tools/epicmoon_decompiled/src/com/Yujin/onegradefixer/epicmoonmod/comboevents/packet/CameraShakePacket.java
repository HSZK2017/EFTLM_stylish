package com.Yujin.onegradefixer.epicmoonmod.comboevents.packet;

import com.merlin204.avalon.client.CameraShake;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class CameraShakePacket {
   private final int duration;
   private final float intensity;
   private final float frequency;

   public CameraShakePacket(int duration, float intensity, float frequency) {
      this.duration = duration;
      this.intensity = intensity;
      this.frequency = frequency;
   }

   public static void encode(CameraShakePacket msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.duration);
      buf.writeFloat(msg.intensity);
      buf.writeFloat(msg.frequency);
   }

   public static CameraShakePacket decode(FriendlyByteBuf buf) {
      return new CameraShakePacket(buf.readInt(), buf.readFloat(), buf.readFloat());
   }

   public static void handle(CameraShakePacket msg, Supplier<Context> ctx) {
      Context context = ctx.get();
      context.enqueueWork(() -> {
         Minecraft mc = Minecraft.m_91087_();
         if (mc.f_91074_ != null) {
            CameraShake.shake(msg.duration, msg.intensity, msg.frequency, mc.f_91074_.m_20182_(), 200.0F);
         }
      });
      context.setPacketHandled(true);
   }
}
