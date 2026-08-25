package com.dmc.invincible_dmc.network.client;

import com.dmc.invincible_dmc.api.weapon.DmcWeaponType;
import com.dmc.invincible_dmc.capability.doppelganger.DoppelgangerBindingService;
import com.dmc.invincible_dmc.capability.weapon.DmcWeaponManager;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public record CPWeaponSwitch(int targetWeaponId, boolean suppressEntryTransition) {
   public CPWeaponSwitch(DmcWeaponType targetWeapon, boolean suppressEntryTransition) {
      this(targetWeapon.networkId(), suppressEntryTransition);
   }

   public static void encode(CPWeaponSwitch packet, FriendlyByteBuf buffer) {
      buffer.m_130130_(packet.targetWeaponId);
      buffer.writeBoolean(packet.suppressEntryTransition);
   }

   public static CPWeaponSwitch decode(FriendlyByteBuf buffer) {
      return new CPWeaponSwitch(buffer.m_130242_(), buffer.readBoolean());
   }

   public static void handle(CPWeaponSwitch packet, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null) {
            DoppelgangerBindingService.cancelPendingSummon(sender);
            DmcWeaponManager.switchWeapon(sender, DmcWeaponType.byNetworkId(packet.targetWeaponId), packet.suppressEntryTransition);
         }
      });
      context.setPacketHandled(true);
   }
}
