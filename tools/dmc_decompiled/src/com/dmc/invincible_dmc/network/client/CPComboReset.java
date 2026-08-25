package com.dmc.invincible_dmc.network.client;

import com.dmc.invincible_dmc.skill.weapon_innate.ComboBasicAttack;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class CPComboReset {
   private final SkillSlot skillSlot;

   public CPComboReset(SkillSlot skillSlot) {
      this.skillSlot = skillSlot;
   }

   public static CPComboReset fromBytes(FriendlyByteBuf buf) {
      return new CPComboReset((SkillSlot)SkillSlot.ENUM_MANAGER.getOrThrow(buf.readInt()));
   }

   public static void toBytes(CPComboReset msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.skillSlot.universalOrdinal());
   }

   public static void handle(CPComboReset msg, Supplier<Context> ctx) {
      ctx.get().enqueueWork(() -> {
         ServerPlayerPatch serverPlayerPatch = EpicFightCapabilities.getServerPlayerPatch(ctx.get().getSender());
         if (serverPlayerPatch != null) {
            SkillContainer container = serverPlayerPatch.getSkill(msg.skillSlot);
            if (container.getSkill() instanceof ComboBasicAttack comboBasicAttack) {
               comboBasicAttack.resetCombo(container);
            }
         }
      });
      ctx.get().setPacketHandled(true);
   }
}
