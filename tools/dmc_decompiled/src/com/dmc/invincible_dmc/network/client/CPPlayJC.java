package com.dmc.invincible_dmc.network.client;

import com.dmc.invincible_dmc.api.weapon.DmcWeaponType;
import com.dmc.invincible_dmc.capability.DMCPlayerCapabilityProvider;
import com.dmc.invincible_dmc.capability.weapon.DmcWeaponManager;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public record CPPlayJC(boolean isPerfect, int chainCount, boolean inAir) {
   public static void encode(CPPlayJC msg, FriendlyByteBuf buf) {
      buf.writeBoolean(msg.isPerfect);
      buf.writeInt(msg.chainCount);
      buf.writeBoolean(msg.inAir);
   }

   public static CPPlayJC decode(FriendlyByteBuf buf) {
      return new CPPlayJC(buf.readBoolean(), buf.readInt(), buf.readBoolean());
   }

   public static void handle(CPPlayJC msg, Supplier<Context> ctxSupplier) {
      Context context = ctxSupplier.get();
      context.enqueueWork(
         () -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
               ServerPlayerPatch spp = (ServerPlayerPatch)EpicFightCapabilities.getEntityPatch(player, ServerPlayerPatch.class);
               if (spp != null) {
                  SkillContainer container = spp.getSkill(SkillSlots.WEAPON_INNATE);
                  if (DmcWeaponManager.isActiveWeapon(player, DmcWeaponType.YAMATO) && container.getSkill() instanceof VergilSkill jcSkill) {
                     player.getCapability(DMCPlayerCapabilityProvider.DMC_PLAYER)
                        .ifPresent(ip -> jcSkill.executeAuthoritative(container, ip, msg.isPerfect(), msg.chainCount(), msg.inAir()));
                  }
               }
            }
         }
      );
      context.setPacketHandled(true);
   }
}
