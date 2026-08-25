package com.dmc.invincible_dmc.network.client;

import com.dmc.invincible_dmc.capability.DMCPlayer;
import com.dmc.invincible_dmc.capability.DMCPlayerCapabilityProvider;
import com.dmc.invincible_dmc.network.DMCNetwork;
import com.dmc.invincible_dmc.network.server.SPInstantJudgementCutEndState;
import com.dmc.invincible_dmc.skill.skill_book.Instant_Judgement_Cut_EndSkill;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkEvent.Context;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public record CPInstantJudgementCutEndToggle(boolean update, boolean enabled) {
   public static CPInstantJudgementCutEndToggle query() {
      return new CPInstantJudgementCutEndToggle(false, true);
   }

   public static CPInstantJudgementCutEndToggle set(boolean enabled) {
      return new CPInstantJudgementCutEndToggle(true, enabled);
   }

   public static void encode(CPInstantJudgementCutEndToggle packet, FriendlyByteBuf buffer) {
      buffer.writeBoolean(packet.update);
      buffer.writeBoolean(packet.enabled);
   }

   public static CPInstantJudgementCutEndToggle decode(FriendlyByteBuf buffer) {
      return new CPInstantJudgementCutEndToggle(buffer.readBoolean(), buffer.readBoolean());
   }

   public static void handle(CPInstantJudgementCutEndToggle packet, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(
         () -> {
            ServerPlayer sender = context.getSender();
            if (sender != null) {
               ServerPlayerPatch playerPatch = EpicFightCapabilities.getServerPlayerPatch(sender);
               boolean learned = Instant_Judgement_Cut_EndSkill.isLearned(playerPatch);
               DMCPlayer playerData = DMCPlayerCapabilityProvider.get(sender);
               if (packet.update && learned) {
                  playerData.setInstantJudgementCutEndEnabled(packet.enabled);
               }

               DMCNetwork.INSTANCE
                  .send(PacketDistributor.PLAYER.with(() -> sender), new SPInstantJudgementCutEndState(learned, playerData.isInstantJudgementCutEndEnabled()));
            }
         }
      );
      context.setPacketHandled(true);
   }
}
