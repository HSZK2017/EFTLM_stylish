package com.Yujin.onegradefixer.epicmoonmod.comboevents.events;

import com.p1nero.invincible.api.events.TimeStampedEvent;
import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class EffekEvent {
   public static TimeStampedEvent Effek(float time, String string) {
      return new TimeStampedEvent(
         time,
         entitypatch -> {
            Player player = (Player)entitypatch.getOriginal();
            ResourceLocation resourceLocation = new ResourceLocation("epicmoonmod", string);
            ParticleEmitterInfo info = ParticleEmitterInfo.create(player.m_9236_(), resourceLocation);
            AAALevel.addParticle(
               player.m_9236_(),
               true,
               info.position(player.m_20185_(), player.m_20186_(), player.m_20189_()).rotation(0.0F, -((float)Math.toRadians((double)player.m_6080_())), 0.0F)
            );
         }
      );
   }
}
