package com.Yujin.onegradefixer.epicmoonmod.comboevents.events;

import com.p1nero.invincible.api.events.TimeStampedEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class WeaponChangeEvent {
   public static TimeStampedEvent Change(float time, Item item) {
      return new TimeStampedEvent(time, entitypatch -> {
         Player player = (Player)entitypatch.getOriginal();
         ItemStack olditem = player.m_21205_();
         ItemStack newitem = new ItemStack(item);
         if (olditem.m_41782_()) {
            newitem.m_41751_(olditem.m_41783_());
         }

         player.m_21008_(InteractionHand.MAIN_HAND, newitem);
      });
   }
}
