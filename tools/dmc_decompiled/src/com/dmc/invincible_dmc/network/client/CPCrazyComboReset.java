package com.dmc.invincible_dmc.network.client;

import com.dmc.invincible_dmc.capability.DMCPlayer;
import com.dmc.invincible_dmc.capability.DMCPlayerCapabilityProvider;
import com.dmc.invincible_dmc.skill.weapon_innate.ComboBasicAttack;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class CPCrazyComboReset {
   public static void toBytes(CPCrazyComboReset msg, FriendlyByteBuf buf) {
   }

   public static CPCrazyComboReset fromBytes(FriendlyByteBuf buf) {
      return new CPCrazyComboReset();
   }

   public static void handle(CPCrazyComboReset msg, Supplier<Context> ctx) {
      ctx.get().enqueueWork(() -> {
         ServerPlayer sender = ctx.get().getSender();
         if (sender != null) {
            ServerPlayerPatch serverPlayerPatch = EpicFightCapabilities.getServerPlayerPatch(ctx.get().getSender());
            if (serverPlayerPatch != null) {
               SkillContainer container = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE);
               if (container.getSkill() instanceof ComboBasicAttack comboBasicAttack) {
                  comboBasicAttack.resetCombo(container);
               }

               DMCPlayer ip = DMCPlayerCapabilityProvider.get(sender);
               ip.clear();
            }
         }
      });
      ctx.get().setPacketHandled(true);
   }
}
