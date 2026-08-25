package com.dmc.invincible_dmc.api.animation.types.yamato;

import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.dmc.invincible_dmc.utils.yamato.TeleportGroundUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.AnimationVariables;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationVariables.IndependentAnimationVariableKey;
import yesman.epicfight.api.animation.property.AnimationProperty.PlaybackSpeedModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class YamatoDownDodgeAnimation extends YamatoDodgeAnimation {
   private static final float LOOP_START_TIME = 0.05F;
   private static final float LOOP_END_TIME = 0.1F;
   private static final float SLOW_START_TIME = 0.033333335F;
   private static final float SLOW_END_TIME = 0.11666667F;
   private static final float MIN_PLAY_SPEED = 0.5F;
   private static final float MAX_SLOW_HEIGHT = 8.0F;
   private static final float LANDING_RELEASE_HEIGHT = 0.1F;
   private static final float FALL_SLOWDOWN_START_PROGRESS = 0.7F;
   private static final float LANDING_PLAY_SPEED = 0.15F;
   private static final IndependentAnimationVariableKey<Float> LOCKED_PLAY_SPEED = AnimationVariables.independent(animator -> Float.NaN, true);
   private static final IndependentAnimationVariableKey<Float> INITIAL_LOCKED_PLAY_SPEED = AnimationVariables.independent(animator -> Float.NaN, true);
   private static final IndependentAnimationVariableKey<Float> INITIAL_HEIGHT_ABOVE_GROUND = AnimationVariables.independent(animator -> Float.NaN, true);
   private static final IndependentAnimationVariableKey<Boolean> FALL_RELEASED = AnimationVariables.independent(animator -> false, true);

   public YamatoDownDodgeAnimation(
      float transitionTime,
      float delayTime,
      AnimationAccessor<? extends ActionAnimation> accessor,
      float width,
      float height,
      AssetAccessor<? extends Armature> armature
   ) {
      super(transitionTime, delayTime, accessor, width, height, armature);
      this.addProperty(
         StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> {
            if (!(elapsedTime < 0.033333335F) && !(elapsedTime > 0.11666667F)) {
               float lockedSpeed = (Float)livingEntityPatch.getAnimator().getVariables().getOrDefault(LOCKED_PLAY_SPEED, this.getAccessor());
               return Float.isFinite(lockedSpeed) ? lockedSpeed : speed;
            } else {
               return speed;
            }
         }
      );
   }

   @Override
   public void begin(LivingEntityPatch<?> livingEntityPatch) {
      super.begin(livingEntityPatch);
      float baseSpeed = Math.max(0.5F, this.getPlaySpeed(livingEntityPatch, this));
      float initialHeight = getHeightAboveGround((LivingEntity)livingEntityPatch.getOriginal());
      float heightRatio = Mth.m_14036_(initialHeight / 8.0F, 0.0F, 1.0F);
      float slowdownRatio = Mth.m_14116_(heightRatio);
      float lockedSpeed = Mth.m_14179_(slowdownRatio, baseSpeed, 0.5F);
      livingEntityPatch.getAnimator().getVariables().put(LOCKED_PLAY_SPEED, this.getAccessor(), lockedSpeed);
      livingEntityPatch.getAnimator().getVariables().put(INITIAL_LOCKED_PLAY_SPEED, this.getAccessor(), lockedSpeed);
      livingEntityPatch.getAnimator().getVariables().put(INITIAL_HEIGHT_ABOVE_GROUND, this.getAccessor(), initialHeight);
      livingEntityPatch.getAnimator().getVariables().put(FALL_RELEASED, this.getAccessor(), false);
   }

   public void tick(LivingEntityPatch<?> livingEntityPatch) {
      super.tick(livingEntityPatch);
      AnimationPlayer animationPlayer = DMCAnimationUtils.getPlayerFor(livingEntityPatch, this.getAccessor());
      if (animationPlayer != null && DMCAnimationUtils.sameAccessor(DMCAnimationUtils.getCurrentAnimationAccessor(animationPlayer), this.getAccessor())) {
         boolean released = (Boolean)livingEntityPatch.getAnimator().getVariables().getOrDefault(FALL_RELEASED, this.getAccessor());
         if (!released) {
            LivingEntity entity = (LivingEntity)livingEntityPatch.getOriginal();
            float elapsedTime = animationPlayer.getElapsedTime();
            float currentHeight = getHeightAboveGround(entity);
            this.updateLockedPlaySpeed(livingEntityPatch, currentHeight);
            if (!entity.m_20096_() && !(currentHeight <= 0.1F)) {
               if (elapsedTime >= 0.1F) {
                  float loopDuration = 0.05F;
                  float loopElapsed = (elapsedTime - 0.05F) % loopDuration;
                  animationPlayer.setElapsedTime(0.05F + loopElapsed);
               }
            } else {
               livingEntityPatch.getAnimator().getVariables().put(FALL_RELEASED, this.getAccessor(), true);
               TeleportGroundUtils.teleportToGround(livingEntityPatch, -0.1F);
               if (elapsedTime < 0.1F) {
                  animationPlayer.setElapsedTime(0.1F);
               }
            }
         }
      }
   }

   private void updateLockedPlaySpeed(LivingEntityPatch<?> livingEntityPatch, float currentHeight) {
      float initialHeight = (Float)livingEntityPatch.getAnimator().getVariables().getOrDefault(INITIAL_HEIGHT_ABOVE_GROUND, this.getAccessor());
      float initialLockedSpeed = (Float)livingEntityPatch.getAnimator().getVariables().getOrDefault(INITIAL_LOCKED_PLAY_SPEED, this.getAccessor());
      if (Float.isFinite(initialHeight) && Float.isFinite(initialLockedSpeed) && !(initialHeight <= 0.1F)) {
         float fallProgress = Mth.m_14036_((initialHeight - currentHeight) / initialHeight, 0.0F, 1.0F);
         if (!(fallProgress <= 0.7F)) {
            float slowdownProgress = (fallProgress - 0.7F) / 0.3F;
            float remainingProgress = 1.0F - slowdownProgress;
            float rapidDecay = 1.0F - remainingProgress * remainingProgress * remainingProgress;
            float lockedSpeed = Mth.m_14179_(rapidDecay, initialLockedSpeed, 0.15F);
            livingEntityPatch.getAnimator().getVariables().put(LOCKED_PLAY_SPEED, this.getAccessor(), lockedSpeed);
         }
      }
   }

   private static float getHeightAboveGround(LivingEntity entity) {
      Vec3 feet = new Vec3(entity.m_20185_(), entity.m_20191_().f_82289_, entity.m_20189_());
      Vec3 rayStart = feet.m_82520_(0.0, 0.05, 0.0);
      Vec3 rayEnd = new Vec3(feet.f_82479_, (double)entity.m_9236_().m_141937_(), feet.f_82481_);
      BlockHitResult hitResult = entity.m_9236_().m_45547_(new ClipContext(rayStart, rayEnd, Block.COLLIDER, Fluid.NONE, entity));
      return hitResult.m_6662_() == Type.MISS ? 8.0F : (float)Math.max(0.0, feet.f_82480_ - hitResult.m_82450_().f_82480_);
   }
}
