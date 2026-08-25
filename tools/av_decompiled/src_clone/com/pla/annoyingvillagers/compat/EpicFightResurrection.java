package com.pla.annoyingvillagers.compat;

import java.util.HashSet;
import java.util.Set;
import net.corruptdog.cdm.api.animation.types.InvincibleAttackAnimation;
import net.corruptdog.cdm.api.animation.types.PowerAnimation;
import net.corruptdog.cdm.api.animation.types.YamatoSkillAnimation;
import net.corruptdog.cdm.gameasset.CorruptAnimations;

public class EpicFightResurrection {
   private static final Set<String> DANGEROUS_ANIMATIONS = new HashSet<>();

   public static Set<String> getDangerousAnimations() {
      return DANGEROUS_ANIMATIONS;
   }

   static {
      DANGEROUS_ANIMATIONS.addAll(
         Set.of(
            ((InvincibleAttackAnimation)CorruptAnimations.YAMATO_JUDGEMENT_CUT.get()).getRegistryName().toString(),
            ((YamatoSkillAnimation)CorruptAnimations.YAMATO_JUDGEMENT_CUT_JUST.get()).getRegistryName().toString(),
            ((PowerAnimation)CorruptAnimations.YAMATO_JUDGEMENT_CUT_END.get()).getRegistryName().toString()
         )
      );
   }
}
