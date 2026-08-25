package com.dmc.invincible_dmc.network.client;

import com.dmc.invincible_dmc.capability.doppelganger.DoppelgangerBindingService;
import com.dmc.invincible_dmc.capability.yamato.YamatoPlayerStateProvider;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import com.dmc.invincible_dmc.utils.DMCLog;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class CPDoppelgangerDelayMode {
   private final int mode;

   public CPDoppelgangerDelayMode(int mode) {
      this.mode = mode;
   }

   public static void toBytes(CPDoppelgangerDelayMode msg, FriendlyByteBuf buf) {
      buf.writeByte(msg.mode);
   }

   public static CPDoppelgangerDelayMode fromBytes(FriendlyByteBuf buf) {
      return new CPDoppelgangerDelayMode(buf.readByte());
   }

   public static void handle(CPDoppelgangerDelayMode msg, Supplier<Context> ctx) {
      ctx.get()
         .enqueueWork(
            () -> {
               ServerPlayer sender = ctx.get().getSender();
               if (sender != null) {
                  DoppelgangerBindingService.reconcile(sender);
                  int mode = Math.max(0, Math.min(2, msg.mode));
                  DoppelgangerEntity doppel = DoppelgangerBindingService.findBoundEntity(sender);
                  if (doppel != null) {
                     if (mode == doppel.getDoppelDelayMode()) {
                        DoppelgangerEntity.recallDoppelganger(doppel);
                     } else {
                        doppel.setDoppelDelayMode(mode);
                        DoppelgangerBindingService.bindActive(sender, doppel);
                     }
                  } else {
                     ServerPlayerPatch patch = EpicFightCapabilities.getServerPlayerPatch(sender);
                     if (patch != null) {
                        SkillContainer container = patch.getSkill(SkillSlots.WEAPON_INNATE);
                        int stack = VergilSkill.getAuthoritativeDtStack(patch);
                        if (stack < 3 && !sender.m_7500_()) {
                           DMCLog.info(
                              DMCLog.Category.DOPPEL_NET, "[DoppelDelay] CREATE fallback blocked: stack={} player={}", stack, sender.m_7755_().getString()
                           );
                        } else {
                           if (sender.m_7500_() && stack < 3) {
                              YamatoPlayerStateProvider.get(sender).setDtStack(5);
                              if (container != null && !container.isEmpty()) {
                                 container.getSkill().setStackSynchronize(container, 5);
                              }
                           }

                           DoppelgangerBindingService.spawnImmediate(sender, mode);
                           DMCLog.info(
                              DMCLog.Category.DOPPEL_NET,
                              "[DoppelDelay] stale client state repaired by CREATE player={} mode={}",
                              sender.m_7755_().getString(),
                              mode
                           );
                        }
                     }
                  }
               }
            }
         );
      ctx.get().setPacketHandled(true);
   }
}
