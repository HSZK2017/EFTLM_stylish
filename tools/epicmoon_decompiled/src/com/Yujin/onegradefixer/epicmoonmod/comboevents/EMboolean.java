package com.Yujin.onegradefixer.epicmoonmod.comboevents;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class EMboolean {
   public static boolean isHoldingGuardKey(LivingEntity entity) {
      if (entity instanceof Player player) {
         PlayerPatch<?> patch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
         if (patch == null) {
            return false;
         } else {
            SkillContainer guardContainer = patch.getSkill(SkillSlots.GUARD);
            return guardContainer != null && !guardContainer.isEmpty() ? patch.isHoldingSkill(guardContainer.getSkill()) : false;
         }
      } else {
         return false;
      }
   }
}
