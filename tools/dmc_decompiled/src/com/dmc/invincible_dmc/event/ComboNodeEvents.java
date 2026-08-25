package com.dmc.invincible_dmc.event;

import com.dmc.invincible_dmc.api.events.Side;
import com.dmc.invincible_dmc.api.events.TimeStampedEvent;
import com.dmc.invincible_dmc.client.render.shake.CameraShakeManager;
import com.dmc.invincible_dmc.gameassets.DMCSkillDataKeys;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import com.dmc.invincible_dmc.utils.vfx.CameraFovUtil;
import com.dmc.invincible_dmc.utils.vfx.CinematicBarsUtils;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillSlots;

public class ComboNodeEvents {
   public static TimeStampedEvent zoom(
      float time, int fadeIn, int sustain, int fadeOut, float targetScale, CameraFovUtil.EaseType easeIn, CameraFovUtil.EaseType easeOut, int priority
   ) {
      return new TimeStampedEvent(time, (playerPatch, target, invinciblePlayer) -> {
         if (playerPatch.isLogicalClient()) {
            CameraFovUtil.triggerZoom(fadeIn, sustain, fadeOut, targetScale, easeIn, easeOut, priority);
         }
      }, Side.LOCAL_CLIENT);
   }

   public static TimeStampedEvent cinematic(float time, float cinematicDuration, float openSpeed, float closeSpeed, float cinematicHeight) {
      return new TimeStampedEvent(time, (playerPatch, target, invinciblePlayer) -> {
         if (playerPatch.isLogicalClient()) {
            CinematicBarsUtils.openFor(cinematicDuration, openSpeed, closeSpeed, cinematicHeight);
         }
      }, Side.LOCAL_CLIENT);
   }

   public static TimeStampedEvent shake(float time, float intensity, int duration, float frequency) {
      return new TimeStampedEvent(time, (playerPatch, target, invinciblePlayer) -> {
         if (playerPatch.isLogicalClient()) {
            CameraShakeManager.addShake(((Player)playerPatch.getOriginal()).m_146892_(), intensity, duration, frequency);
         }
      }, Side.LOCAL_CLIENT);
   }

   public static TimeStampedEvent shakeSmall(float time) {
      return shake(time, 2.0F, 3, 4.0F);
   }

   public static TimeStampedEvent shakeFast(float time) {
      return shake(time, 4.0F, 2, 2.0F);
   }

   public static TimeStampedEvent shakeStrong(float time) {
      return shake(time, 6.0F, 2, 1.0F);
   }

   public static TimeStampedEvent shakeHuge(float time) {
      return shake(time, 7.0F, 4, 0.6F);
   }

   public static TimeStampedEvent shakeSustain(float time) {
      return shake(time, 0.4F, 40, 17.0F);
   }

   public static TimeStampedEvent consumeSDT(float time, float normalAmount, float sdtAmount) {
      return new TimeStampedEvent(time, (playerPatch, target, invinciblePlayer) -> {
         SkillContainer container = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
         if (container != null && !container.isEmpty() && container.getSkill() instanceof VergilSkill ys) {
            boolean inSDT = SinDevilTriggerManager.isSDT(container);
            float amount = inSDT ? sdtAmount : normalAmount;
            float current = SinDevilTriggerManager.getSDTValue(container);
            if (inSDT || current >= normalAmount) {
               boolean consumed = SinDevilTriggerManager.consumeSDT(container, amount);
               if (consumed && inSDT) {
                  ys.getSDTManager().forceEndGrace(container);
               }
            }
         }
      }, Side.SERVER);
   }

   public static TimeStampedEvent endGp(float time) {
      return new TimeStampedEvent(
         time,
         (playerPatch, target, invinciblePlayer) -> {
            SkillContainer container = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
            if (container != null && !container.isEmpty() && container.getSkill() instanceof VergilSkill) {
               container.getDataManager()
                  .setDataSync(
                     (SkillDataKey)DMCSkillDataKeys.DODGE_COUNTER_SUCCESS_TIMER.get(),
                     (Integer)((SkillDataKey)DMCSkillDataKeys.DODGE_COUNTER_SUCCESS_TIMER.get()).defaultValue()
                  );
            }
         },
         Side.SERVER
      );
   }
}
