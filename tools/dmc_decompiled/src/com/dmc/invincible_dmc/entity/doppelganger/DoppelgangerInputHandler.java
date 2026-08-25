package com.dmc.invincible_dmc.entity.doppelganger;

import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.api.skill.ComboType;
import com.dmc.invincible_dmc.capability.DMCPlayer;
import com.dmc.invincible_dmc.capability.DMCPlayerCapabilityProvider;
import com.dmc.invincible_dmc.client.input.DirectionTracker;
import com.dmc.invincible_dmc.client.input.PlayerInputState;
import com.dmc.invincible_dmc.gameassets.DMCSkillDataKeys;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import com.dmc.invincible_dmc.utils.DMCLog;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class DoppelgangerInputHandler {
   public static void dispatch(
      ServerPlayer player,
      ComboType type,
      int pressedTime,
      long pressIntervalMs,
      boolean isLongPress,
      int directionMask,
      List<DirectionTracker.DirectionEvent> directionEvents,
      long engineTick
   ) {
      if (type == ComboNode.ComboTypes.KEY_1) {
         ServerPlayerPatch spp = (ServerPlayerPatch)EpicFightCapabilities.getEntityPatch(player, ServerPlayerPatch.class);
         if (!VergilSkill.isDoppelgangerAllowed(spp)) {
            DMCLog.warn(DMCLog.Category.DOPPEL_INPUT, "[DoppelInput] REJECT player={} reason=weapon_not_vergil_arsenal", player.m_7755_().getString());
         } else {
            int delayTicks = findDoppelDelayTicks(player);
            long scheduledTick = player.m_9236_().m_46467_() + (long)delayTicks;
            SkillDataManager sdm = spp.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
            DMCPlayer ip = DMCPlayerCapabilityProvider.get(player);
            boolean hU = PlayerInputState.isRemoteDown(player, 0);
            boolean hD = PlayerInputState.isRemoteDown(player, 1);
            boolean hL = PlayerInputState.isRemoteDown(player, 2);
            boolean hR = PlayerInputState.isRemoteDown(player, 3);
            boolean hJ = PlayerInputState.isRemoteDown(player, 4);
            boolean hSp = PlayerInputState.isRemoteDown(player, 5);
            boolean hSn = PlayerInputState.isRemoteDown(player, 6);
            boolean hLk = PlayerInputState.isRemoteDown(player, 7);
            int dsTimer = getInt(sdm, (SkillDataKey<Integer>)DMCSkillDataKeys.DODGE_SUCCESS_TIMER.get());
            int prTimer = getInt(sdm, (SkillDataKey<Integer>)DMCSkillDataKeys.PARRY_TIMER.get());
            int cdTimer = getInt(sdm, (SkillDataKey<Integer>)DMCSkillDataKeys.COOLDOWN.get());
            int stack = spp.getSkill(SkillSlots.WEAPON_INNATE).getStack();
            int phase = ip.getPhase();
            boolean sdt = sdm.hasData((SkillDataKey)DMCSkillDataKeys.IS_SDT.get()) && (Boolean)sdm.getDataValue((SkillDataKey)DMCSkillDataKeys.IS_SDT.get());
            DoppelgangerInputEvent event = new DoppelgangerInputEvent(
               scheduledTick,
               type,
               engineTick,
               delayTicks,
               pressedTime,
               pressIntervalMs,
               isLongPress,
               directionMask,
               directionEvents,
               hU,
               hD,
               hL,
               hR,
               hJ,
               hSp,
               hSn,
               hLk,
               dsTimer,
               prTimer,
               cdTimer,
               stack,
               phase,
               sdt
            );
            int count = 0;

            for (Entity entity : player.m_284548_().m_142646_().m_142273_()) {
               if (entity instanceof DoppelgangerEntity) {
                  DoppelgangerEntity doppel = (DoppelgangerEntity)entity;
                  if (player.m_20148_().equals(doppel.getOwnerUUID()) && doppel.m_6084_() && !doppel.isCcMode()) {
                     DoppelgangerPatch patch = (DoppelgangerPatch)EpicFightCapabilities.getEntityPatch(doppel, DoppelgangerPatch.class);
                     if (patch != null) {
                        patch.enqueueInput(event);
                        count++;
                     }
                  }
               }
            }

            if (count > 0) {
               DMCLog.info(
                  DMCLog.Category.DOPPEL_INPUT,
                  "[DoppelInput] DISPATCH player={} type={} delay={} scheduledTick={} doppelCount={}",
                  player.m_7755_().getString(),
                  type.universalOrdinal(),
                  delayTicks,
                  scheduledTick,
                  count
               );
            }
         }
      }
   }

   public static DoppelgangerInputEvent createImmediateEvent(
      ServerPlayer player,
      ComboType type,
      int pressedTime,
      long pressIntervalMs,
      boolean isLongPress,
      int directionMask,
      List<DirectionTracker.DirectionEvent> directionEvents,
      long engineTick
   ) {
      ServerPlayerPatch spp = (ServerPlayerPatch)EpicFightCapabilities.getEntityPatch(player, ServerPlayerPatch.class);
      SkillDataManager sdm = spp.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
      DMCPlayer ip = DMCPlayerCapabilityProvider.get(player);
      return new DoppelgangerInputEvent(
         player.m_9236_().m_46467_(),
         type,
         engineTick,
         0,
         pressedTime,
         pressIntervalMs,
         isLongPress,
         directionMask,
         directionEvents,
         PlayerInputState.isRemoteDown(player, 0),
         PlayerInputState.isRemoteDown(player, 1),
         PlayerInputState.isRemoteDown(player, 2),
         PlayerInputState.isRemoteDown(player, 3),
         PlayerInputState.isRemoteDown(player, 4),
         PlayerInputState.isRemoteDown(player, 5),
         PlayerInputState.isRemoteDown(player, 6),
         PlayerInputState.isRemoteDown(player, 7),
         getInt(sdm, (SkillDataKey<Integer>)DMCSkillDataKeys.DODGE_SUCCESS_TIMER.get()),
         getInt(sdm, (SkillDataKey<Integer>)DMCSkillDataKeys.PARRY_TIMER.get()),
         getInt(sdm, (SkillDataKey<Integer>)DMCSkillDataKeys.COOLDOWN.get()),
         spp.getSkill(SkillSlots.WEAPON_INNATE).getStack(),
         ip.getPhase(),
         sdm.hasData((SkillDataKey)DMCSkillDataKeys.IS_SDT.get()) && (Boolean)sdm.getDataValue((SkillDataKey)DMCSkillDataKeys.IS_SDT.get())
      );
   }

   private static int findDoppelDelayTicks(ServerPlayer player) {
      for (Entity entity : player.m_284548_().m_142646_().m_142273_()) {
         if (entity instanceof DoppelgangerEntity d && player.m_20148_().equals(d.getOwnerUUID()) && d.m_6084_()) {
            return DoppelgangerEntity.getDelayTicks(d.getDoppelDelayMode());
         }
      }

      return 0;
   }

   private static int getInt(SkillDataManager sdm, SkillDataKey<Integer> key) {
      return sdm.hasData(key) ? (Integer)sdm.getDataValue(key) : 0;
   }
}
