package com.dmc.invincible_dmc.client.input;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.player.Player;

public final class PlayerInputState {
   public static final int BIT_UP = 0;
   public static final int BIT_DOWN = 1;
   public static final int BIT_LEFT = 2;
   public static final int BIT_RIGHT = 3;
   public static final int BIT_JUMP = 4;
   public static final int BIT_SPRINT = 5;
   public static final int BIT_SNEAK = 6;
   public static final int BIT_LOCK_ON = 7;
   public static final int BIT_SDT_KEY = 8;
   public static final int BIT_COMBO_1 = 9;
   public static final int BIT_COMBO_2 = 10;
   public static final int BIT_COMBO_3 = 11;
   public static final int BIT_COMBO_4 = 12;
   public static final int BIT_WEAPON_INNATE = 13;
   private static volatile short localMask;
   private static final Map<UUID, Short> REMOTE_STATE = new ConcurrentHashMap<>();
   private static final Map<UUID, Long> REMOTE_SEQUENCE = new ConcurrentHashMap<>();

   public static void updateLocal(short mask) {
      localMask = mask;
   }

   public static boolean isLocalDown(int bit) {
      return (localMask & 1 << bit) != 0;
   }

   public static short getLocalMask() {
      return localMask;
   }

   public static void updateRemote(Player player, short mask) {
      REMOTE_STATE.put(player.m_20148_(), mask);
      REMOTE_SEQUENCE.remove(player.m_20148_());
   }

   public static void updateRemote(Player player, short mask, long sequence) {
      UUID uuid = player.m_20148_();
      Long previous = REMOTE_SEQUENCE.get(uuid);
      if (previous == null || sequence >= previous) {
         REMOTE_SEQUENCE.put(uuid, sequence);
         REMOTE_STATE.put(uuid, mask);
      }
   }

   public static boolean isRemoteDown(Player player, int bit) {
      Short mask = REMOTE_STATE.get(player.m_20148_());
      return mask != null && (mask & 1 << bit) != 0;
   }

   public static short getRemoteMask(Player player) {
      Short mask = REMOTE_STATE.get(player.m_20148_());
      return mask != null ? mask : 0;
   }

   public static void remove(Player player) {
      REMOTE_STATE.remove(player.m_20148_());
      REMOTE_SEQUENCE.remove(player.m_20148_());
   }

   public static boolean hasRemote(Player player) {
      return REMOTE_STATE.containsKey(player.m_20148_());
   }
}
