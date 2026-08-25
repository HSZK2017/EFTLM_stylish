package com.dmc.invincible_dmc.client;

import com.dmc.invincible_dmc.api.weapon.DmcWeaponType;
import com.dmc.invincible_dmc.capability.DMCPlayer;
import com.dmc.invincible_dmc.capability.DMCPlayerCapabilityProvider;
import com.dmc.invincible_dmc.capability.yamato.YamatoPlayerState;
import com.dmc.invincible_dmc.capability.yamato.YamatoPlayerStateProvider;
import com.dmc.invincible_dmc.client.input.DMComboEngine;
import com.dmc.invincible_dmc.network.server.SPWeaponState;
import com.dmc.invincible_dmc.skill.weapon_innate.ComboBasicAttack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@OnlyIn(Dist.CLIENT)
public final class ClientWeaponStateHandler {
   private ClientWeaponStateHandler() {
   }

   public static void handle(SPWeaponState packet) {
      Minecraft minecraft = Minecraft.m_91087_();
      if (minecraft.f_91073_ != null && minecraft.f_91073_.m_6815_(packet.entityId()) instanceof Player player) {
         YamatoPlayerState state = YamatoPlayerStateProvider.get(player);
         if (state != YamatoPlayerState.EMPTY && packet.sequence() >= state.getWeaponSwitchSequence()) {
            DmcWeaponType activeWeapon = DmcWeaponType.byNetworkId(packet.activeWeaponId());
            DmcWeaponType renderedWeapon = DmcWeaponType.byNetworkId(packet.renderedWeaponId());
            DmcWeaponType pendingWeapon = packet.pendingWeaponId() >= 0 ? DmcWeaponType.byNetworkId(packet.pendingWeaponId()) : null;
            DMCPlayer dmcPlayer = DMCPlayerCapabilityProvider.get(player);
            dmcPlayer.setActionSessionMirror(packet.createActionMirror());
            boolean activeWeaponChanged = ClientWeaponRenderManager.applyServerState(
               player, activeWeapon, renderedWeapon, pendingWeapon, packet.barrierSessionId(), packet.sequence()
            );
            if (activeWeaponChanged && player == minecraft.f_91074_) {
               PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
               if (playerPatch != null) {
                  SkillContainer container = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
                  if (container != null && !container.isEmpty() && container.getSkill() instanceof ComboBasicAttack comboSkill) {
                     comboSkill.switchComboRootPreservingAction(container);
                  }
               }

               DMComboEngine.resetForWeaponStateChange(activeWeapon);
            }
         }
      }
   }
}
