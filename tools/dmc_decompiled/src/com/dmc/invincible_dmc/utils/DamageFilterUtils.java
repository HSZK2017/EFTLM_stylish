package com.dmc.invincible_dmc.utils;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;

public final class DamageFilterUtils {
   private DamageFilterUtils() {
   }

   public static boolean shouldSkipTarget(LivingEntity owner, LivingEntity target) {
      if (target.m_7307_(owner) && owner.m_5647_() != null && !owner.m_5647_().m_6260_()) {
         return true;
      } else {
         if (owner instanceof Player player && target instanceof OwnableEntity ownable && player.m_20148_().equals(ownable.m_21805_())) {
            return true;
         }

         return false;
      }
   }
}
