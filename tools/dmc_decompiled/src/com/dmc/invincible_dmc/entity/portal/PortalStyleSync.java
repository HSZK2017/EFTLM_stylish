package com.dmc.invincible_dmc.entity.portal;

import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class PortalStyleSync {
   public static final byte END_PORTAL = 0;
   public static final byte ORIGINAL = 1;
   private static final String PLAYER_DATA_KEY = "invincible_dmc:portal_particle_style";

   private PortalStyleSync() {
   }

   public static void setPlayerStyle(Player player, byte style) {
      CompoundTag persistentData = player.getPersistentData();
      CompoundTag playerData = persistentData.m_128469_("PlayerPersisted");
      playerData.m_128344_("invincible_dmc:portal_particle_style", normalize(style));
      persistentData.m_128365_("PlayerPersisted", playerData);
   }

   public static byte getOwnerStyle(LivingEntity owner) {
      Player player = resolvePlayer(owner);
      if (player == null) {
         return 1;
      } else {
         CompoundTag playerData = player.getPersistentData().m_128469_("PlayerPersisted");
         return !playerData.m_128425_("invincible_dmc:portal_particle_style", 1) ? 1 : normalize(playerData.m_128445_("invincible_dmc:portal_particle_style"));
      }
   }

   public static byte normalize(byte style) {
      return (byte)(style == 1 ? 1 : 0);
   }

   private static Player resolvePlayer(LivingEntity owner) {
      if (owner instanceof Player) {
         return (Player)owner;
      } else {
         return owner instanceof DoppelgangerEntity doppelganger ? doppelganger.getOwner() : null;
      }
   }
}
