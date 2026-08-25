package com.dmc.invincible_dmc.client.input.enemystep;

import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.client.input.DMComboEngine;
import com.dmc.invincible_dmc.client.input.jumpcancel.JumpCancelController;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.entity.judgementcut.JudgementCutEntity;
import com.dmc.invincible_dmc.entity.summonedsword.DMCSummonedSwordEntity;
import com.dmc.invincible_dmc.gameassets.DMCSkillDataKeys;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.mixin.LivingEntityAccessor;
import com.dmc.invincible_dmc.network.DMCNetwork;
import com.dmc.invincible_dmc.network.client.CPEnemyStep;
import com.dmc.invincible_dmc.skill.weapon_innate.ComboBasicAttack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.camera.EpicFightCameraAPI;
import yesman.epicfight.api.client.input.InputManager;
import yesman.epicfight.api.client.input.MovementDirection;
import yesman.epicfight.api.client.input.PlayerInputState;
import yesman.epicfight.api.utils.HitEntityList;
import yesman.epicfight.api.utils.HitEntityList.Priority;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;

@OnlyIn(Dist.CLIENT)
public final class EnemyStepController {
   private static final int COOLDOWN_TICKS = 10;
   private static final double DEFERRED_VERTICAL_VELOCITY = 0.2;
   private static final double PHANTOM_ASCENT_VERTICAL_VELOCITY = 0.6;
   private static final double FORWARD_FLIP_HORIZONTAL_IMPULSE = 0.6;
   private static final double BACKWARD_FLIP_HORIZONTAL_IMPULSE = 0.45;
   private boolean previousJumpKeyDown;
   private boolean jumpJustPressed;
   private int cooldownTicks;
   private double deferredDeltaY = Double.NaN;
   @Nullable
   private Vec3 deferredHorizontalImpulse;
   @Nullable
   private PlayerInputState capturedInputState;

   public void captureInput(Input input) {
      this.capturedInputState = InputManager.getInputState(input);
   }

   public void beginTick(LocalPlayer player) {
      if (!Double.isNaN(this.deferredDeltaY)) {
         Vec3 movement = player.m_20184_();
         Vec3 horizontalImpulse = this.deferredHorizontalImpulse != null ? this.deferredHorizontalImpulse : Vec3.f_82478_;
         player.m_20334_(movement.f_82479_ + horizontalImpulse.f_82479_, this.deferredDeltaY, movement.f_82481_ + horizontalImpulse.f_82481_);
         this.deferredDeltaY = Double.NaN;
         this.deferredHorizontalImpulse = null;
      }

      if (this.cooldownTicks > 0) {
         this.cooldownTicks--;
      }

      boolean jumpDown = Minecraft.m_91087_().f_91066_.f_92089_.m_90857_();
      this.jumpJustPressed = jumpDown && !this.previousJumpKeyDown;
      this.previousJumpKeyDown = jumpDown;
   }

   public void tryExecute(Input input, LocalPlayer player, LocalPlayerPatch playerPatch) {
      SkillContainer skillContainer = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
      SkillDataManager dataManager = skillContainer.getDataManager();
      if (dataManager.hasData((SkillDataKey)DMCSkillDataKeys.IS_ON_GROUND.get())
         && !(Boolean)dataManager.getDataValue((SkillDataKey)DMCSkillDataKeys.IS_ON_GROUND.get())
         && playerPatch.isEpicFightMode()
         && this.jumpJustPressed
         && this.cooldownTicks <= 0) {
         boolean inaction = playerPatch.getEntityState().inaction();
         ComboBasicAttack comboSkill = JumpCancelController.getComboSkill(playerPatch);
         boolean canEnemyStep = !inaction || comboSkill != null && comboSkill.isAllowJumpCancel();
         if (canEnemyStep) {
            Entity target = findTarget(player, playerPatch);
            if (target != null) {
               snapToward(player, target);
               this.executeAirJump(input, player, playerPatch);
               comboSkill = JumpCancelController.getComboSkill(playerPatch);
               if (comboSkill != null) {
                  comboSkill.resetComboFromClient();
               }

               DMCNetwork.sendToServer(new CPEnemyStep(target.m_19879_()));
               DMComboEngine.resetCrazyComboForPlayer();
            }

            this.cooldownTicks = 10;
         }
      }
   }

   private void executeAirJump(Input input, LocalPlayer player, LocalPlayerPatch playerPatch) {
      ((LivingEntityAccessor)player).setNoJumpDelay(0);
      PlayerInputState inputState = this.capturedInputState != null ? this.capturedInputState : InputManager.getInputState(input);
      MovementDirection movementDirection = MovementDirection.fromInputState(inputState);
      int verticalDirection = movementDirection.vertical();
      AssetAccessor<? extends StaticAnimation> jumpAnimation = selectJumpAnimation(verticalDirection);
      InputManager.setInputState(inputState.withJumping(true));
      player.m_6862_(false);
      player.m_6853_(false);
      this.configureJumpMovement(player, playerPatch, movementDirection);
      player.f_19864_ = true;
      YamatoAttackAnimation.setAerialActionCount(playerPatch, 0.0F);
      playerPatch.playAnimationSynchronized(jumpAnimation, 0.1F);
   }

   private void configureJumpMovement(LocalPlayer player, LocalPlayerPatch playerPatch, MovementDirection movementDirection) {
      int verticalDirection = movementDirection.vertical();
      if (verticalDirection == 0) {
         this.deferredDeltaY = 0.2;
         this.deferredHorizontalImpulse = null;
      } else {
         int horizontalDirection = movementDirection.horizontal();
         int degree = -(90 * horizontalDirection * (1 - Math.abs(verticalDirection)) + 45 * verticalDirection * horizontalDirection);
         float launchingYRot = EpicFightCameraAPI.getInstance().getForwardYRot() + (float)degree;
         double horizontalImpulse = verticalDirection > 0 ? 0.6 : -0.45;
         this.deferredDeltaY = 0.6 + (double)player.m_285755_();
         this.deferredHorizontalImpulse = MathUtils.getVectorForRotation(0.0F, launchingYRot).m_82490_(horizontalImpulse);
         playerPatch.setModelYRot(launchingYRot, true);
      }
   }

   private static AssetAccessor<? extends StaticAnimation> selectJumpAnimation(int verticalDirection) {
      if (verticalDirection > 0) {
         return YamatoAnimations.YAMATO_ENEMY_STEP_FORWARD;
      } else {
         return verticalDirection < 0 ? YamatoAnimations.YAMATO_ENEMY_STEP_BACKWARD : Animations.BIPED_JUMP;
      }
   }

   private static void snapToward(LocalPlayer player, Entity target) {
      Vec3 playerPosition = player.m_20182_();
      Vec3 direction = target.m_20182_().m_82546_(playerPosition);
      double distance = direction.m_82553_();
      if (!(distance < 0.3)) {
         double snapDistance = Math.min(distance - 0.5, 0.8);
         if (!(snapDistance <= 0.0)) {
            Vec3 newPosition = playerPosition.m_82549_(direction.m_82541_().m_82490_(snapDistance));
            player.m_6034_(newPosition.f_82479_, newPosition.f_82480_, newPosition.f_82481_);
         }
      }
   }

   @Nullable
   private static Entity findTarget(LocalPlayer player, LocalPlayerPatch playerPatch) {
      Entity epicFightTarget = playerPatch.getTarget();
      if (epicFightTarget != null
         && epicFightTarget.m_6084_()
         && !(epicFightTarget instanceof DoppelgangerEntity)
         && !(epicFightTarget instanceof JudgementCutEntity)
         && !(epicFightTarget instanceof DMCSummonedSwordEntity)
         && epicFightTarget.m_20280_(player) <= 5.5) {
         return epicFightTarget;
      } else {
         List<LivingEntity> nearby = player.m_9236_()
            .m_6443_(
               LivingEntity.class,
               player.m_20191_().m_82400_(2.5),
               entity -> entity != player
                     && entity.m_6084_()
                     && !(entity instanceof DoppelgangerEntity)
                     && !(entity instanceof JudgementCutEntity)
                     && !(entity instanceof DMCSummonedSwordEntity)
            );
         if (nearby.isEmpty()) {
            return null;
         } else {
            HitEntityList hitList = new HitEntityList(playerPatch, new ArrayList<>(nearby), Priority.DISTANCE);

            while (hitList.next()) {
               if (hitList.getEntity() instanceof LivingEntity livingEntity
                  && livingEntity.m_6084_()
                  && !(livingEntity instanceof DoppelgangerEntity)
                  && !(livingEntity instanceof JudgementCutEntity)
                  && !(livingEntity instanceof DMCSummonedSwordEntity)) {
                  return livingEntity;
               }
            }

            return null;
         }
      }
   }
}
