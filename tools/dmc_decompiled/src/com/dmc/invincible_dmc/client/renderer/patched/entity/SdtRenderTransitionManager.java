package com.dmc.invincible_dmc.client.renderer.patched.entity;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.gameassets.DMCSkillDataKeys;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@OnlyIn(Dist.CLIENT)
public class SdtRenderTransitionManager {
   private static final int TRANSITION_DURATION_MS = 200;
   private static final Map<UUID, Integer> PREV_PHASE = new HashMap<>();
   private static final Map<UUID, Long> PREVIEW_START_MS = new HashMap<>();
   private static final Map<UUID, Long> ENTER_START_MS = new HashMap<>();
   private static final Map<UUID, Long> EXIT_START_MS = new HashMap<>();
   private static final Map<UUID, Boolean> WAS_IN_SDT = new HashMap<>();

   public static void update(AbstractClientPlayer player) {
      UUID id = player.m_20148_();
      int currentPhase = getSdtPhase(player);
      boolean currentInSdt = SinDevilTriggerManager.isPlayerInSDT(player);
      Integer prevPhase = PREV_PHASE.get(id);
      if (prevPhase == null) {
         PREV_PHASE.put(id, currentPhase);
         WAS_IN_SDT.put(id, currentInSdt);
      } else {
         if (prevPhase < 3 && currentPhase >= 3 && currentPhase < 4) {
            PREVIEW_START_MS.put(id, System.currentTimeMillis());
         }

         Boolean wasInSdt = WAS_IN_SDT.get(id);
         if (wasInSdt != null && !wasInSdt && currentInSdt) {
            ENTER_START_MS.put(id, System.currentTimeMillis());
            PREVIEW_START_MS.remove(id);
         }

         if (wasInSdt != null && wasInSdt && !currentInSdt) {
            EXIT_START_MS.put(id, System.currentTimeMillis());
         }

         PREV_PHASE.put(id, currentPhase);
         WAS_IN_SDT.put(id, currentInSdt);
      }
   }

   public static boolean shouldRenderSdt(AbstractClientPlayer player) {
      UUID id = player.m_20148_();
      return isPreviewActive(player) || SinDevilTriggerManager.isPlayerInSDT(player) || isEnterTransitioning(player) || isExitTransitioning(player);
   }

   public static float getSdtAlpha(AbstractClientPlayer player) {
      if (!(Boolean)DMConfig.SDT_AFTERIMAGE.get()) {
         return 1.0F;
      } else {
         UUID id = player.m_20148_();
         if (isPreviewActive(player)) {
            float progress = getPreviewProgress(player);
            return Mth.m_14179_(progress, 0.5F, 0.0F);
         } else if (isEnterTransitioning(player)) {
            return Mth.m_14036_(getEnterProgress(player), 0.0F, 1.0F);
         } else {
            return isExitTransitioning(player) ? 1.0F - Mth.m_14036_(getExitProgress(player), 0.0F, 1.0F) : 1.0F;
         }
      }
   }

   public static float getRed(AbstractClientPlayer player) {
      return isPreviewActive(player) ? 0.15F : 1.0F;
   }

   public static float getGreen(AbstractClientPlayer player) {
      return isPreviewActive(player) ? 0.45F : 1.0F;
   }

   public static float getBlue(AbstractClientPlayer player) {
      return isPreviewActive(player) ? 1.0F : 1.0F;
   }

   private static boolean isPreviewActive(AbstractClientPlayer player) {
      if (!(Boolean)DMConfig.SDT_AFTERIMAGE.get()) {
         return false;
      } else {
         Long start = PREVIEW_START_MS.get(player.m_20148_());
         if (start == null) {
            return false;
         } else {
            int phase = getSdtPhase(player);
            return phase == 3 && System.currentTimeMillis() - start < 200L;
         }
      }
   }

   private static float getPreviewProgress(AbstractClientPlayer player) {
      Long start = PREVIEW_START_MS.get(player.m_20148_());
      if (start == null) {
         return 1.0F;
      } else {
         float p = (float)(System.currentTimeMillis() - start) / 200.0F;
         return Mth.m_14036_(p, 0.0F, 1.0F);
      }
   }

   private static boolean isEnterTransitioning(AbstractClientPlayer player) {
      if (!(Boolean)DMConfig.SDT_AFTERIMAGE.get()) {
         return false;
      } else {
         Long start = ENTER_START_MS.get(player.m_20148_());
         return start != null && System.currentTimeMillis() - start < 200L;
      }
   }

   private static float getEnterProgress(AbstractClientPlayer player) {
      Long start = ENTER_START_MS.get(player.m_20148_());
      return start == null ? 1.0F : (float)(System.currentTimeMillis() - start) / 200.0F;
   }

   private static boolean isExitTransitioning(AbstractClientPlayer player) {
      if (!(Boolean)DMConfig.SDT_AFTERIMAGE.get()) {
         return false;
      } else {
         Long start = EXIT_START_MS.get(player.m_20148_());
         return start != null && System.currentTimeMillis() - start < 200L;
      }
   }

   private static float getExitProgress(AbstractClientPlayer player) {
      Long start = EXIT_START_MS.get(player.m_20148_());
      return start == null ? 1.0F : (float)(System.currentTimeMillis() - start) / 200.0F;
   }

   private static int getSdtPhase(AbstractClientPlayer player) {
      try {
         PlayerPatch patch = (PlayerPatch)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
         if (patch == null) {
            return 0;
         } else {
            SkillContainer container = patch.getSkill(SkillSlots.WEAPON_INNATE);
            if (container != null && !container.isEmpty()) {
               SkillDataManager dm = container.getDataManager();
               SkillDataKey<Integer> key = (SkillDataKey<Integer>)DMCSkillDataKeys.SDT_PHASE.get();
               return !dm.hasData(key) ? 0 : (Integer)dm.getDataValue(key);
            } else {
               return 0;
            }
         }
      } catch (Exception var5) {
         return 0;
      }
   }

   public static void clear(UUID id) {
      PREV_PHASE.remove(id);
      PREVIEW_START_MS.remove(id);
      ENTER_START_MS.remove(id);
      EXIT_START_MS.remove(id);
      WAS_IN_SDT.remove(id);
   }

   public static void clearAll() {
      PREV_PHASE.clear();
      PREVIEW_START_MS.clear();
      ENTER_START_MS.clear();
      EXIT_START_MS.clear();
      WAS_IN_SDT.clear();
   }
}
