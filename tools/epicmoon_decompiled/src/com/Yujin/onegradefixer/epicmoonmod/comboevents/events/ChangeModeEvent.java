package com.Yujin.onegradefixer.epicmoonmod.comboevents.events;

import com.p1nero.invincible.api.events.TimeStampedEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ChangeModeEvent {
   public static TimeStampedEvent ModeChange(float time, int mode) {
      return new TimeStampedEvent(time, entitypatch -> {
         Player player = (Player)entitypatch.getOriginal();
         ItemStack stack = player.m_21205_();
         CompoundTag tag = stack.m_41784_();
         tag.m_128405_("weapon_mode", mode);
      });
   }
}
