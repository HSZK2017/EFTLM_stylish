package com.dmc.invincible_dmc.network.server;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

public class S2CSdtEffectPacket {
   public static final byte EFFECT_ENTER = 0;
   public static final byte EFFECT_EXIT = 1;
   public static final byte EFFECT_CHARGE1_TICK = 2;
   public static final byte EFFECT_CHARGE1_COMPLETE = 3;
   public static final byte EFFECT_CHARGE2_COMPLETE = 4;
   public static final byte EFFECT_ACTIVE_TICK = 5;
   public static final byte EFFECT_CHARGE2_START = 6;
   public static final byte EFFECT_CHARGE2_END = 7;
   private final int playerId;
   private final byte effectType;

   public S2CSdtEffectPacket(int playerId, byte effectType) {
      this.playerId = playerId;
      this.effectType = effectType;
   }

   public static void encode(S2CSdtEffectPacket msg, FriendlyByteBuf buf) {
      buf.m_130130_(msg.playerId);
      buf.writeByte(msg.effectType);
   }

   public static S2CSdtEffectPacket decode(FriendlyByteBuf buf) {
      return new S2CSdtEffectPacket(buf.m_130242_(), buf.readByte());
   }

   public static void handle(S2CSdtEffectPacket msg, Supplier<Context> ctxSupplier) {
      Context ctx = ctxSupplier.get();
      if (!isValidEffectType(msg.effectType)) {
         ctx.setPacketHandled(true);
      } else {
         ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> S2CSdtEffectHandler.handle(msg)));
         ctx.setPacketHandled(true);
      }
   }

   private static boolean isValidEffectType(byte effectType) {
      return effectType >= 0 && effectType <= 7;
   }

   public int playerId() {
      return this.playerId;
   }

   public byte effectType() {
      return this.effectType;
   }
}
