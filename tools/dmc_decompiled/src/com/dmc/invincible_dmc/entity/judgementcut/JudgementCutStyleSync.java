package com.dmc.invincible_dmc.entity.judgementcut;

import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class JudgementCutStyleSync {
   public static final byte NORMAL = 0;
   public static final byte DMC4 = 1;
   private static final String PLAYER_DATA_KEY = "invincible_dmc:judgement_cut_sequence_style";

   private JudgementCutStyleSync() {
   }

   public static void setPlayerStyle(Player player, byte style) {
      CompoundTag persistentData = player.getPersistentData();
      CompoundTag playerData = persistentData.m_128469_("PlayerPersisted");
      playerData.m_128344_("invincible_dmc:judgement_cut_sequence_style", normalize(style));
      persistentData.m_128365_("PlayerPersisted", playerData);
   }

   public static byte getOwnerStyle(LivingEntity owner) {
      Player player = resolvePlayer(owner);
      if (player == null) {
         return 0;
      } else {
         CompoundTag playerData = player.getPersistentData().m_128469_("PlayerPersisted");
         return normalize(playerData.m_128445_("invincible_dmc:judgement_cut_sequence_style"));
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
