package com.dmc.invincible_dmc.entity.dummy;

import net.minecraft.world.entity.PathfinderMob;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.Factions;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;

public class DummyPatch<T extends PathfinderMob> extends HumanoidMobPatch<T> {
   public DummyPatch() {
      super(Factions.NEUTRAL);
   }

   public HumanoidArmature getArmature() {
      return (HumanoidArmature)Armatures.BIPED.get();
   }

   public void initAnimator(Animator animator) {
      super.initAnimator(animator);
      animator.addLivingAnimation(LivingMotions.IDLE, Animations.BIPED_IDLE);
      animator.addLivingAnimation(LivingMotions.JUMP, Animations.BIPED_JUMP);
      animator.addLivingAnimation(LivingMotions.WALK, Animations.BIPED_WALK);
      animator.addLivingAnimation(LivingMotions.CHASE, Animations.BIPED_RUN);
      animator.addLivingAnimation(LivingMotions.RUN, Animations.BIPED_RUN);
      animator.addLivingAnimation(LivingMotions.FALL, Animations.BIPED_FALL);
      animator.addLivingAnimation(LivingMotions.MOUNT, Animations.BIPED_MOUNT);
      animator.addLivingAnimation(LivingMotions.DEATH, Animations.BIPED_DEATH);
   }

   public void updateMotion(boolean considerInaction) {
      if (this.getEntityState().updateLivingMotion()) {
         this.currentLivingMotion = LivingMotions.IDLE;
         this.currentCompositeMotion = LivingMotions.IDLE;
      }
   }
}
