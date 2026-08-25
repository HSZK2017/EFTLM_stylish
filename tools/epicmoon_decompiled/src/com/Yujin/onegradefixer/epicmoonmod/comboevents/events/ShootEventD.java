package com.Yujin.onegradefixer.epicmoonmod.comboevents.events;

import com.Yujin.onegradefixer.epicmoonmod.effect.EMeffects;
import com.Yujin.onegradefixer.epicmoonmod.sound.EMsounds;
import com.p1nero.invincible.api.events.TimeStampedEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ShootEventD {
   public static TimeStampedEvent ShootD(float time) {
      return new TimeStampedEvent(
         time,
         entitypatch -> {
            Player player = (Player)entitypatch.getOriginal();
            ItemStack TS = player.m_21205_();
            CompoundTag TSTG = TS.m_41783_();
            if (TSTG == null) {
               TSTG = new CompoundTag();
               TS.m_41751_(TSTG);
            }

            int a = TSTG.m_128451_("amount");
            TSTG.m_128405_("amount", a - 1);
            if (player.m_21023_((MobEffect)EMeffects.POISE.get())) {
               MobEffectInstance mobEffectInstance = player.m_21124_((MobEffect)EMeffects.POISE.get());
               int current = mobEffectInstance.m_19557_();
               int amp = mobEffectInstance.m_19564_();
               MobEffectInstance next = new MobEffectInstance((MobEffect)EMeffects.POISE.get(), current + 200, amp);
               player.m_21195_((MobEffect)EMeffects.POISE.get());
               player.m_7292_(next);
            } else {
               MobEffectInstance mobEffectInstance = new MobEffectInstance((MobEffect)EMeffects.POISE.get(), 200);
               player.m_7292_(mobEffectInstance);
            }

            player.m_9236_()
               .m_6263_(null, player.m_20185_(), player.m_20186_(), player.m_20189_(), (SoundEvent)EMsounds.SHOT5.get(), SoundSource.PLAYERS, 0.6F, 1.0F);
         }
      );
   }
}
