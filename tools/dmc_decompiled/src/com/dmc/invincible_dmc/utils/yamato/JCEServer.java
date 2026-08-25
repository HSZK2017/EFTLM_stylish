package com.dmc.invincible_dmc.utils.yamato;

import com.dmc.invincible_dmc.capability.doppelganger.DoppelgangerCapabilityEvents;
import com.dmc.invincible_dmc.network.DMCNetwork;
import com.dmc.invincible_dmc.network.server.S2CSdtEffectPacket;
import com.dmc.invincible_dmc.particle.DMCParticles;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class JCEServer {
   public static void prev(LivingEntityPatch<?> ep) {
      if (ep instanceof ServerPlayerPatch serverPlayerPatch) {
         DoppelgangerCapabilityEvents.discardPlayersDoppel((ServerPlayer)serverPlayerPatch.getOriginal());
      }
   }

   private static void broadcastSDT(ServerPlayer player, byte effectType) {
      DMCNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), new S2CSdtEffectPacket(player.m_19879_(), effectType));
   }

   public static void onSDTEnterServer(Player player) {
      if (player instanceof ServerPlayer sp) {
         broadcastSDT(sp, (byte)0);
      }
   }

   public static void onSDTExitServer(Player player) {
      if (player instanceof ServerPlayer sp) {
         broadcastSDT(sp, (byte)1);
      }
   }

   public static void onSdtCharge1TickServer(ServerPlayer player) {
      broadcastSDT(player, (byte)2);
   }

   public static void onSdtCharge2TickServer(ServerPlayer player) {
   }

   public static void onSdtCharge1CompleteServer(ServerPlayer player) {
      broadcastSDT(player, (byte)3);
   }

   public static void onSdtCharge2CompleteServer(ServerPlayer player) {
      broadcastSDT(player, (byte)4);
      Vec3 center = player.m_20191_().m_82399_();
      player.m_284548_()
         .m_8767_((SimpleParticleType)DMCParticles.SDT_PHASE2_CHROMATIC.get(), center.f_82479_, center.f_82480_ + 0.2, center.f_82481_, 1, 0.0, 0.0, 0.0, 0.0);
   }

   public static void onSdtActivatedServer(ServerPlayer player) {
   }

   public static void onSdtActiveTickServer(ServerPlayer player) {
      broadcastSDT(player, (byte)5);
   }

   public static void onSdtCharge2StartServer(ServerPlayer player) {
      broadcastSDT(player, (byte)6);
   }

   public static void onSdtCharge2EndServer(ServerPlayer player) {
      broadcastSDT(player, (byte)7);
   }
}
