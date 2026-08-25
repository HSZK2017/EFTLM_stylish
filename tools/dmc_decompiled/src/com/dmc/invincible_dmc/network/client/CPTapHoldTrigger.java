package com.dmc.invincible_dmc.network.client;

import com.dmc.invincible_dmc.skill.weapon_innate.ComboBasicAttack;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class CPTapHoldTrigger {
   public static CPTapHoldTrigger fromBytes(FriendlyByteBuf buf) {
      return new CPTapHoldTrigger();
   }

   public static void toBytes(CPTapHoldTrigger msg, FriendlyByteBuf buf) {
   }

   public static void handle(CPTapHoldTrigger msg, Supplier<Context> ctx) {
      ctx.get()
         .enqueueWork(() -> EpicFightCapabilities.getUnparameterizedEntityPatch(ctx.get().getSender(), ServerPlayerPatch.class).ifPresent(playerPatch -> {
               SkillContainer container = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
               if (container.getSkill() instanceof ComboBasicAttack comboBasicAttack) {
                  comboBasicAttack.triggerTapHold(container);
               }
            }));
      ctx.get().setPacketHandled(true);
   }
}
