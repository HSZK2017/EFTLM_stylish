package com.Yujin.onegradefixer.epicmoonmod.comboevents.events;

import com.p1nero.invincible.api.events.TimeStampedEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ShootEvent {
   public static TimeStampedEvent Shoot(float time) {
      return new TimeStampedEvent(time, entitypatch -> {
         Player player = (Player)entitypatch.getOriginal();
         ItemStack TS = player.m_21205_();
         CompoundTag TSTG = TS.m_41783_();
         if (TSTG == null) {
            TSTG = new CompoundTag();
            TS.m_41751_(TSTG);
         }

         int a = TSTG.m_128451_("amount");
         TSTG.m_128405_("amount", a - 1);
         if (TSTG.m_128451_("amount") == 0) {
            TSTG.m_128405_("ammotype", 0);
         }
      });
   }
}
