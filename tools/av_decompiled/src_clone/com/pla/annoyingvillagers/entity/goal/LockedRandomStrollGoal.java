package com.pla.annoyingvillagers.entity.goal;

import com.pla.annoyingvillagers.clazz.AVNpc;
import com.pla.annoyingvillagers.entity.PlayerNpcEntity;
import com.pla.annoyingvillagers.gameasset.AVAnimations;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import java.util.Objects;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class LockedRandomStrollGoal extends WaterAvoidingRandomStrollGoal {
   public LockedRandomStrollGoal(PathfinderMob mob, double speed) {
      super(mob, speed);
   }

   private boolean isPlayingIdle() {
      if (this.f_25725_ instanceof PlayerNpcEntity playerNpcEntity) {
         return playerNpcEntity.isPlayingIdle();
      } else {
         return this.f_25725_ instanceof AVNpc avNpc ? avNpc.isPlayingIdle() : false;
      }
   }

   private void setStrolling(boolean strolling) {
      if (this.f_25725_ instanceof PlayerNpcEntity playerNpcEntity) {
         playerNpcEntity.setStrolling(strolling);
      }

      if (this.f_25725_ instanceof AVNpc avNpc) {
         avNpc.setStrolling(strolling);
      }
   }

   public boolean m_8036_() {
      if (this.f_25725_.m_5448_() != null) {
         return false;
      } else if (this.isPlayingIdle()) {
         return false;
      } else {
         LivingEntityPatch<?> patch = null;
         if (this.f_25725_ instanceof PlayerNpcEntity playerNpcEntity) {
            patch = playerNpcEntity.getLivingEntityPatch();
         }

         if (this.f_25725_ instanceof AVNpc avNpc) {
            patch = avNpc.getLivingEntityPatch();
         }

         if (patch == null) {
            return false;
         } else {
            AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(patch.getAnimator().getPlayerFor(null)).getRealAnimation();
            return EpicfightUtil.isLongHitAnimation(dynamicAnimation, patch) ? false : super.m_8036_();
         }
      }
   }

   public boolean m_8045_() {
      if (this.f_25725_.m_5448_() != null) {
         return false;
      } else if (this.isPlayingIdle()) {
         return false;
      } else {
         LivingEntityPatch<?> patch = null;
         if (this.f_25725_ instanceof PlayerNpcEntity playerNpcEntity) {
            patch = playerNpcEntity.getLivingEntityPatch();
         }

         if (this.f_25725_ instanceof AVNpc avNpc) {
            patch = avNpc.getLivingEntityPatch();
         }

         if (patch == null) {
            return false;
         } else {
            AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(patch.getAnimator().getPlayerFor(null)).getRealAnimation();
            return EpicfightUtil.isLongHitAnimation(dynamicAnimation, patch) ? false : super.m_8045_();
         }
      }
   }

   public void m_8056_() {
      this.setStrolling(true);
      LivingEntityPatch<?> patch = null;
      if (this.f_25725_ instanceof PlayerNpcEntity playerNpcEntity) {
         patch = playerNpcEntity.getLivingEntityPatch();
      }

      if (this.f_25725_ instanceof AVNpc avNpc) {
         patch = avNpc.getLivingEntityPatch();
      }

      if (patch != null) {
         patch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
      }

      super.m_8056_();
   }

   public void m_8041_() {
      this.setStrolling(false);
      super.m_8041_();
   }
}
