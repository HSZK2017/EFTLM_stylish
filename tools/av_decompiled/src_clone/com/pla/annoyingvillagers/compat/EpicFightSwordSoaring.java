package com.pla.annoyingvillagers.compat;

import java.util.HashSet;
import java.util.Set;
import net.p1nero.ss.gameassets.animations.BabylonAnimations;
import net.p1nero.ss.gameassets.animations.ScreenSwordAnimations;
import net.p1nero.ss.gameassets.animations.WanAnimations;
import yesman.epicfight.api.animation.types.ActionAnimation;

public class EpicFightSwordSoaring {
   private static final Set<String> DANGEROUS_ANIMATIONS = new HashSet<>();

   public static Set<String> getDangerousAnimations() {
      return DANGEROUS_ANIMATIONS;
   }

   static {
      DANGEROUS_ANIMATIONS.addAll(
         Set.of(
            ((ActionAnimation)ScreenSwordAnimations.PLAYER_SUMMON_KILL_AURA_1.get()).getRegistryName().toString(),
            ((ActionAnimation)ScreenSwordAnimations.PLAYER_SUMMON_KILL_AURA_2.get()).getRegistryName().toString(),
            ((ActionAnimation)ScreenSwordAnimations.PLAYER_SUMMON_SCREEN_SWORD.get()).getRegistryName().toString(),
            ((ActionAnimation)ScreenSwordAnimations.PLAYER_SUMMON_RAIN_SWORD.get()).getRegistryName().toString(),
            ((ActionAnimation)BabylonAnimations.BABYLON_SUMMON_PLAYER.get()).getRegistryName().toString(),
            ((ActionAnimation)WanAnimations.WAN1_PLAYER.get()).getRegistryName().toString()
         )
      );
   }
}
