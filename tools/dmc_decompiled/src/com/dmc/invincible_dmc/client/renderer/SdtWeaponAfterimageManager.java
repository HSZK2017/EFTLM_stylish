package com.dmc.invincible_dmc.client.renderer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SdtWeaponAfterimageManager {
   private static final int AFTERIMAGE_MS = 400;
   private static final float START_SCALE = 1.15F;
   private static final Map<UUID, Long> AFTERIMAGE_START = new HashMap<>();
   private static final Map<UUID, Boolean> SWAPPED = new HashMap<>();

   public static void triggerAfterimage(UUID playerId) {
      AFTERIMAGE_START.put(playerId, System.currentTimeMillis());
      SWAPPED.remove(playerId);
   }

   public static void clearSwap(UUID playerId) {
      SWAPPED.remove(playerId);
   }

   public static float getAfterimageProgress(AbstractClientPlayer player) {
      Long start = AFTERIMAGE_START.get(player.m_20148_());
      if (start == null) {
         return 1.0F;
      } else {
         float raw = (float)(System.currentTimeMillis() - start) / 400.0F;
         if (raw >= 1.0F) {
            AFTERIMAGE_START.remove(player.m_20148_());
            SWAPPED.put(player.m_20148_(), true);
            return 1.0F;
         } else {
            float t = Mth.m_14036_(raw, 0.0F, 1.0F);
            return 1.0F - (1.0F - t) * (1.0F - t) * (1.0F - t);
         }
      }
   }

   public static float getAfterimageAlpha(AbstractClientPlayer player) {
      return 1.0F - getAfterimageProgress(player);
   }

   public static float getAfterimageScale(AbstractClientPlayer player) {
      return Mth.m_14179_(getAfterimageProgress(player), 1.15F, 1.0F);
   }

   public static float getAfterimageOffsetX(AbstractClientPlayer player) {
      return (1.0F - getAfterimageProgress(player)) * 0.15F;
   }

   public static boolean shouldUseDevilSword(AbstractClientPlayer player) {
      return SWAPPED.getOrDefault(player.m_20148_(), false);
   }

   public static void clearAll(UUID id) {
      AFTERIMAGE_START.remove(id);
      SWAPPED.remove(id);
   }
}
