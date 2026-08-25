package com.dmc.invincible_dmc.network.client;

import com.dmc.invincible_dmc.api.skill.ITapHoldNode;
import com.dmc.invincible_dmc.api.skill.SubComboNode;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerPatch;
import com.dmc.invincible_dmc.utils.DMCLog;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent.Context;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

public class CPDoppelgangerTapHold {
   private final boolean trigger;

   public CPDoppelgangerTapHold(boolean trigger) {
      this.trigger = trigger;
   }

   public static void toBytes(CPDoppelgangerTapHold msg, FriendlyByteBuf buf) {
      buf.writeBoolean(msg.trigger);
   }

   public static CPDoppelgangerTapHold fromBytes(FriendlyByteBuf buf) {
      return new CPDoppelgangerTapHold(buf.readBoolean());
   }

   public static void handle(CPDoppelgangerTapHold msg, Supplier<Context> ctx) {
      ctx.get()
         .enqueueWork(
            () -> {
               ServerPlayer sender = ctx.get().getSender();
               if (sender != null) {
                  DoppelgangerPatch doppelPatch = findTapHoldDoppelganger(sender);
                  if (doppelPatch != null) {
                     DoppelgangerEntity doppel = (DoppelgangerEntity)doppelPatch.getOriginal();
                     if (msg.trigger) {
                        ITapHoldNode activeTH = doppelPatch.comboState.getActiveTapHoldNode();
                        if (activeTH == null) {
                           DMCLog.info(
                              DMCLog.Category.DOPPEL_NET,
                              "[TapHold-DoppelSvr] HOLD_IGNORED doppel={} reason=activeTapHoldNodeNull",
                              doppel.m_7755_().getString()
                           );
                           return;
                        }

                        doppelPatch.comboState.setActiveTapHoldNode(null);
                        doppel.setTapHoldActive(false);
                        SubComboNode holdSub = activeTH.getHold();
                        DMCLog.info(
                           DMCLog.Category.DOPPEL_NET,
                           "[TapHold-DoppelSvr] HOLD_TRIGGER doppel={} holdAnim={}",
                           doppel.m_7755_().getString(),
                           holdSub != null ? holdSub.getAnimationAccessor() : "null"
                        );
                        if (holdSub != null && holdSub.getAnimationAccessor() != null) {
                           doppelPatch.playAnimationSynchronized(holdSub.getAnimationAccessor(), holdSub.getConvertTime());
                        }
                     } else {
                        DMCLog.info(DMCLog.Category.DOPPEL_NET, "[TapHold-DoppelSvr] CANCEL doppel={}", doppel.m_7755_().getString());
                        doppelPatch.comboState.setActiveTapHoldNode(null);
                        doppel.setTapHoldActive(false);
                     }
                  }
               }
            }
         );
      ctx.get().setPacketHandled(true);
   }

   private static DoppelgangerPatch findTapHoldDoppelganger(ServerPlayer player) {
      for (Entity entity : player.m_284548_().m_142646_().m_142273_()) {
         if (entity instanceof DoppelgangerEntity doppel && player.m_20148_().equals(doppel.getOwnerUUID()) && doppel.m_6084_() && doppel.isTapHoldActive()) {
            return (DoppelgangerPatch)EpicFightCapabilities.getEntityPatch(doppel, DoppelgangerPatch.class);
         }
      }

      return null;
   }
}
