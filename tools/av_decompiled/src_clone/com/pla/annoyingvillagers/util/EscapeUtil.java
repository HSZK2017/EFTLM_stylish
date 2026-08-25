package com.pla.annoyingvillagers.util;

import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.compat.EpicFightNightFall;
import com.pla.annoyingvillagers.compat.EpicFightResurrection;
import com.pla.annoyingvillagers.compat.EpicFightSwordSoaring;
import com.pla.annoyingvillagers.entity.AngrySteveEntity;
import com.pla.annoyingvillagers.entity.BlueDemonEntity;
import com.pla.annoyingvillagers.gameasset.AVAnimations;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFight;
import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import com.pla.annoyingvillagers.gameasset.AnimsWom;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.fml.ModList;
import net.shelmarow.combat_evolution.gameassets.animation.ExecutionAttackAnimation;
import reascer.wom.animation.attacks.BasicMultipleAttackAnimation;
import reascer.wom.animation.attacks.SpecialAttackAnimation;
import reascer.wom.animation.attacks.UltimateAttackAnimation;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.gameasset.animations.weapons.AnimsAgony;
import reascer.wom.gameasset.animations.weapons.AnimsHerrscher;
import reascer.wom.gameasset.animations.weapons.AnimsMoonless;
import reascer.wom.gameasset.animations.weapons.AnimsNapoleon;
import reascer.wom.gameasset.animations.weapons.AnimsOrbit;
import reascer.wom.gameasset.animations.weapons.AnimsRuine;
import reascer.wom.gameasset.animations.weapons.AnimsSatsujin;
import reascer.wom.gameasset.animations.weapons.AnimsSolar;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class EscapeUtil {
   private static final Set<String> DANGEROUS_ANIMATIONS = new HashSet<>();

   public static boolean isAnimationDangerous(AssetAccessor<? extends StaticAnimation> targetDynamicAnimation) {
      if (targetDynamicAnimation != null && ((StaticAnimation)targetDynamicAnimation.get()).getRegistryName() != null) {
         String animation = ((StaticAnimation)targetDynamicAnimation.get()).getRegistryName().toString();
         return DANGEROUS_ANIMATIONS.contains(animation);
      } else {
         return false;
      }
   }

   public static boolean checkEscape(Mob mob) {
      LivingEntity target = mob.m_5448_();
      LivingEntityPatch<?> targetLivingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
      if (target != null && targetLivingEntityPatch != null) {
         AssetAccessor<? extends StaticAnimation> targetDynamicAnimation = Objects.requireNonNull(targetLivingEntityPatch.getAnimator().getPlayerFor(null))
            .getRealAnimation();
         return isAnimationDangerous(targetDynamicAnimation) || targetDynamicAnimation.get() instanceof ExecutionAttackAnimation;
      } else {
         return false;
      }
   }

   public static void stepLeftRightOnHurtByDangerousAnimation(DamageSource damageSource, MobPatch<?> mobPatch) {
      if (damageSource.m_7639_() instanceof LivingEntity livingEntity) {
         LivingEntityPatch<?> targetEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(livingEntity, LivingEntityPatch.class);
         if (targetEntityPatch != null) {
            AssetAccessor<? extends StaticAnimation> targetDynamicAnimation = Objects.requireNonNull(targetEntityPatch.getAnimator().getPlayerFor(null))
               .getRealAnimation();
            AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(mobPatch.getAnimator().getPlayerFor(null)).getRealAnimation();
            if (isAnimationDangerous(targetDynamicAnimation) && !EpicfightUtil.isLongHitAnimation(dynamicAnimation, mobPatch)) {
               if (mobPatch.getOriginal() instanceof HerobrineMob herobrineMob && herobrineMob.getStunEscapeCooldown() == 0) {
                  herobrineMob.setStunEscapeCooldown(60);
                  if (new Random().nextBoolean()) {
                     mobPatch.playAnimationSynchronized(WOMAnimations.ENDERSTEP_LEFT, 0.0F);
                  } else {
                     mobPatch.playAnimationSynchronized(WOMAnimations.ENDERSTEP_RIGHT, 0.0F);
                  }
               }

               if (mobPatch.getOriginal() instanceof AngrySteveEntity angrySteveEntity && angrySteveEntity.getStunEscapeCooldown() == 0) {
                  angrySteveEntity.setStunEscapeCooldown(60);
                  if (new Random().nextBoolean()) {
                     mobPatch.playAnimationSynchronized(Animations.BIPED_STEP_LEFT, 0.0F);
                  } else {
                     mobPatch.playAnimationSynchronized(Animations.BIPED_STEP_RIGHT, 0.0F);
                  }
               }

               if (mobPatch.getOriginal() instanceof BlueDemonEntity blueDemonEntity && blueDemonEntity.getStunEscapeCooldown() == 0) {
                  blueDemonEntity.setStunEscapeCooldown(60);
                  if (new Random().nextBoolean()) {
                     mobPatch.playAnimationSynchronized(Animations.BIPED_STEP_LEFT, 0.0F);
                  } else {
                     mobPatch.playAnimationSynchronized(Animations.BIPED_STEP_RIGHT, 0.0F);
                  }
               }
            }
         }
      }
   }

   static {
      DANGEROUS_ANIMATIONS.addAll(
         Set.of(
            ((BasicMultipleAttackAnimation)AnimsWom.ENDER_AEGIS_BULL_CHARGE.get()).getRegistryName().toString(),
            ((BasicMultipleAttackAnimation)AnimsWom.YELLOW_TORMENT_CHARGED_ATTACK_3.get()).getRegistryName().toString(),
            ((SpecialAttackAnimation)AnimsWom.ENDER_GLAIVE_NAPOLEON_SHOOT_3.get()).getRegistryName().toString(),
            ((BasicMultipleAttackAnimation)AnimsWom.ENDER_GLAIVE_AGONY_AUTO_1.get()).getRegistryName().toString(),
            ((ActionAnimation)AnimsEpicFight.AEGIS_SHIELD_SHOOT.get()).getRegistryName().toString(),
            ((SpecialAttackAnimation)AnimsWom.CLONE_NAPOLEON_WATERLOW_SHOOT.get()).getRegistryName().toString(),
            ((ActionAnimation)AVAnimations.TRIDENT_ATTACK.get()).getRegistryName().toString(),
            ((StaticAnimation)AnimsPugilistSteve.BLUE_DEMON_STATE_TRANSFORM.get()).getRegistryName().toString(),
            ((ActionAnimation)AnimsWom.ELECTRIC_FIELD.get()).getRegistryName().toString(),
            ((StaticAnimation)AVAnimations.SNAKE_BLADE_GUARD.get()).getRegistryName().toString(),
            ((SpecialAttackAnimation)AnimsAgony.AGONY_SKY_DIVE_X.get()).getRegistryName().toString(),
            ((SpecialAttackAnimation)AnimsAgony.AGONY_SKY_DIVE.get()).getRegistryName().toString(),
            ((BasicMultipleAttackAnimation)WOMAnimations.TORMENT_CHARGED_ATTACK_2.get()).getRegistryName().toString(),
            ((BasicMultipleAttackAnimation)WOMAnimations.TORMENT_CHARGED_ATTACK_3.get()).getRegistryName().toString(),
            ((StaticAnimation)AnimsRuine.RUINE_PLUNDER.get()).getRegistryName().toString(),
            ((UltimateAttackAnimation)WOMAnimations.ANTITHEUS_LAPSE.get()).getRegistryName().toString(),
            ((UltimateAttackAnimation)WOMAnimations.ANTITHEUS_ASCENSION.get()).getRegistryName().toString(),
            ((BasicMultipleAttackAnimation)WOMAnimations.ANTITHEUS_ASCENDED_BLACKHOLE.get()).getRegistryName().toString(),
            ((UltimateAttackAnimation)WOMAnimations.TORMENT_BERSERK_CONVERT.get()).getRegistryName().toString(),
            ((UltimateAttackAnimation)AnimsSatsujin.SATSUJIN_GESSHOKU.get()).getRegistryName().toString(),
            ((BasicMultipleAttackAnimation)AnimsHerrscher.GESETZ_AUTO_3.get()).getRegistryName().toString(),
            ((BasicMultipleAttackAnimation)AnimsHerrscher.GESETZ_SPRENGKOPF.get()).getRegistryName().toString(),
            ((BasicMultipleAttackAnimation)AnimsHerrscher.GESETZ_WIDERSTAND.get()).getRegistryName().toString(),
            ((SpecialAttackAnimation)AnimsMoonless.MOONLESS_LUNAR_ECHO.get()).getRegistryName().toString(),
            ((SpecialAttackAnimation)AnimsMoonless.MOONLESS_LUNAR_ECLIPSE.get()).getRegistryName().toString(),
            ((SpecialAttackAnimation)AnimsMoonless.MOONLESS_LUNAR_FULLMOON.get()).getRegistryName().toString(),
            ((BasicMultipleAttackAnimation)AnimsSolar.SOLAR_BRASERO.get()).getRegistryName().toString(),
            ((UltimateAttackAnimation)AnimsSolar.SOLAR_BRASERO_OBSCURIDAD.get()).getRegistryName().toString(),
            ((UltimateAttackAnimation)AnimsSolar.SOLAR_BRASERO_CREMATORIO.get()).getRegistryName().toString(),
            ((UltimateAttackAnimation)AnimsSolar.SOLAR_BRASERO_INFIERNO.get()).getRegistryName().toString(),
            ((SpecialAttackAnimation)AnimsNapoleon.NAPOLEON_AUSTERLITZ_SHOOT.get()).getRegistryName().toString(),
            ((SpecialAttackAnimation)AnimsNapoleon.NAPOLEON_WATERLOW_SHOOT.get()).getRegistryName().toString(),
            ((ActionAnimation)AnimsOrbit.ORBIT_LIGHT_BEAM.get()).getRegistryName().toString()
         )
      );
      if (ModList.get().isLoaded("efn")) {
         try {
            DANGEROUS_ANIMATIONS.addAll(EpicFightNightFall.getDangerousAnimations());
         } catch (Exception var3) {
            var3.fillInStackTrace();
         }
      }

      if (ModList.get().isLoaded("sword_soaring")) {
         try {
            DANGEROUS_ANIMATIONS.addAll(EpicFightSwordSoaring.getDangerousAnimations());
         } catch (Exception var2) {
            var2.fillInStackTrace();
         }
      }

      if (ModList.get().isLoaded("cdmoveset")) {
         try {
            DANGEROUS_ANIMATIONS.addAll(EpicFightResurrection.getDangerousAnimations());
         } catch (Exception var1) {
            var1.fillInStackTrace();
         }
      }
   }
}
