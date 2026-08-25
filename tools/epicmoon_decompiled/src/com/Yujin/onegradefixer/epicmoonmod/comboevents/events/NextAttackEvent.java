package com.Yujin.onegradefixer.epicmoonmod.comboevents.events;

import com.p1nero.invincible.api.events.Side;
import com.p1nero.invincible.api.events.TimeStampedEvent;
import com.p1nero.invincible.api.skill.ComboNode;
import com.p1nero.invincible.api.skill.ComboType;
import com.p1nero.invincible.skill.ComboBasicAttack;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class NextAttackEvent {
   public static TimeStampedEvent next(float time, ComboType comboType) {
      return new TimeStampedEvent(time, (patch, entity, inv) -> {
         if (patch instanceof ServerPlayerPatch serverPatch) {
            ComboNode current = inv.getCurrentNode();
            if (current != null && current != ComboNode.EMPTY) {
               ComboNode next = current.getNext(comboType);
               if (next != null && next != ComboNode.EMPTY) {
                  ComboBasicAttack.executeNodeOnServer(serverPatch, next);
               }
            }
         }
      }, Side.SERVER);
   }
}
