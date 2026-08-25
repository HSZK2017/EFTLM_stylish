package com.pla.annoyingvillagers.event;

import net.minecraft.world.item.Item;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import reascer.wom.gameasset.WOMSkills;
import reascer.wom.skill.weaponinnate.DemonicAscensionSkill;
import reascer.wom.world.item.WOMItems;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

@EventBusSubscriber
public class WomFixEvent {
   @SubscribeEvent
   public static void fixAntitheusCrash(PlayerTickEvent playerTickEvent) {
      if (playerTickEvent.side.isServer()) {
         PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(playerTickEvent.player, PlayerPatch.class);
         if (playerPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            SkillContainer skillContainer = serverPlayerPatch.getSkill(WOMSkills.DEMONIC_ASCENSION);
            if (skillContainer != null && skillContainer.getSkill() instanceof DemonicAscensionSkill demonicAscensionSkill) {
               if (skillContainer.isActivated()) {
                  boolean holdingAntitheus = playerTickEvent.player.m_21205_().m_150930_((Item)WOMItems.ANTITHEUS.get());
                  if (!holdingAntitheus) {
                     demonicAscensionSkill.cancelOnServer(skillContainer, null);
                     skillContainer.deactivate();
                     if (serverPlayerPatch.getAnimator() != null) {
                        serverPlayerPatch.getAnimator().stopPlaying(null);
                     }
                  }
               }
            }
         }
      }
   }
}
