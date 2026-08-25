package com.pla.annoyingvillagers.mobpatch;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.pla.annoyingvillagers.combatbehaviour.GregFist;
import com.pla.annoyingvillagers.entity.HerobrineGregEntity;
import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import com.pla.annoyingvillagers.gameasset.AnimsWom;
import java.util.Set;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.shelmarow.combat_evolution.ai.CEHumanoidPatch;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.Factions;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;

public class HerobrineGregPatch extends CEHumanoidPatch {
   private boolean hookedLivingAnimations;

   public HerobrineGregPatch() {
      super(Factions.UNDEAD);
   }

   public void initAnimator(Animator animator) {
      super.initAnimator(animator);
      animator.addLivingAnimation(LivingMotions.IDLE, Animations.BIPED_IDLE);
      animator.addLivingAnimation(LivingMotions.WALK, Animations.BIPED_WALK);
      animator.addLivingAnimation(LivingMotions.RUN, AnimsWom.OLD_MOONLESS_RUN);
      animator.addLivingAnimation(LivingMotions.CHASE, AnimsWom.OLD_MOONLESS_RUN);
      animator.addLivingAnimation(LivingMotions.DEATH, Animations.BIPED_DEATH);
   }

   public void tick(LivingTickEvent livingTickEvent) {
      super.tick(livingTickEvent);
      boolean hooked = this.isHookedEntity();
      if (this.hookedLivingAnimations != hooked) {
         this.hookedLivingAnimations = hooked;
         this.modifyLivingMotionByCurrentItem(false);
         this.updateMotion(true);
      }
   }

   public void modifyLivingMotionByCurrentItem(boolean resendPacket) {
      super.modifyLivingMotionByCurrentItem(resendPacket);
      if (this.isHookedEntity()) {
         this.applyHookedLivingAnimations();
      }
   }

   private boolean isHookedEntity() {
      if (this.getOriginal() instanceof HerobrineGregEntity greg && greg.isHooked()) {
         return true;
      }

      return false;
   }

   private void applyHookedLivingAnimations() {
      Animator animator = this.getAnimator();
      animator.addLivingAnimation(LivingMotions.IDLE, AnimsPugilistSteve.LAYING_DEATH);
      animator.addLivingAnimation(LivingMotions.WALK, AnimsPugilistSteve.LAYING_DEATH);
      animator.addLivingAnimation(LivingMotions.RUN, AnimsPugilistSteve.LAYING_DEATH);
      animator.addLivingAnimation(LivingMotions.CHASE, AnimsPugilistSteve.LAYING_DEATH);
      animator.addLivingAnimation(LivingMotions.DEATH, AnimsPugilistSteve.LAYING_DEATH_DEAD);
   }

   protected void setWeaponMotions() {
      Set<Pair<LivingMotions, ? extends AnimationAccessor<? extends StaticAnimation>>> fistLivingMotions = Set.of(
         Pair.of(LivingMotions.BLOCK, AnimsPugilistSteve.FIST_GUARD),
         Pair.of(LivingMotions.IDLE, Animations.BIPED_IDLE),
         Pair.of(LivingMotions.WALK, Animations.BIPED_WALK),
         Pair.of(LivingMotions.RUN, AnimsWom.OLD_MOONLESS_RUN),
         Pair.of(LivingMotions.CHASE, AnimsWom.OLD_MOONLESS_RUN),
         Pair.of(LivingMotions.DEATH, Animations.BIPED_DEATH)
      );
      this.weaponLivingMotions.put(WeaponCategories.NOT_WEAPON, ImmutableMap.of(Styles.ONE_HAND, fistLivingMotions, Styles.COMMON, fistLivingMotions));
      this.weaponAttackMotions.put(WeaponCategories.NOT_WEAPON, ImmutableMap.of(Styles.ONE_HAND, GregFist.GREG_FIST, Styles.COMMON, GregFist.GREG_FIST));
      this.weaponLivingMotions.put(WeaponCategories.FIST, ImmutableMap.of(Styles.ONE_HAND, fistLivingMotions, Styles.COMMON, fistLivingMotions));
      this.weaponAttackMotions.put(WeaponCategories.FIST, ImmutableMap.of(Styles.ONE_HAND, GregFist.GREG_FIST, Styles.COMMON, GregFist.GREG_FIST));
   }
}
