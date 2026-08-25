package com.dmc.invincible_dmc.utils.yamato;

import com.dmc.invincible_dmc.impl.IEpicFightCameraAPI;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.client.camera.EpicFightCameraAPI;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

@OnlyIn(Dist.CLIENT)
public class CameraLockUtil {
   private static CameraLockUtil.OverrideState currentState = CameraLockUtil.OverrideState.NONE;
   private static boolean preOverrideLockState = false;
   private static LivingEntity preOverrideTarget = null;
   private static boolean sequenceOriginalLockState = false;
   private static boolean isSequenceActive = false;
   private static LivingEntity sequenceOriginalTarget = null;

   public static void startLockOn(float maxAngle, double maxDistance) {
      if (!prepareOverride()) {
         LocalPlayer player = Minecraft.m_91087_().f_91074_;
         EpicFightCameraAPI cameraAPI = EpicFightCameraAPI.getInstance();
         LivingEntity target = cameraAPI.getFocusingEntity();
         if (player != null && target != null && !preOverrideLockState) {
            double distanceSqr = player.m_20280_(target);
            if (distanceSqr <= maxDistance * maxDistance) {
               Vec3 lookVec = player.m_20252_(1.0F).m_82541_();
               Vec3 toTarget = target.m_146892_().m_82546_(player.m_146892_()).m_82541_();
               double dot = Mth.m_14008_(lookVec.m_82526_(toTarget), -1.0, 1.0);
               double angle = Math.toDegrees(Math.acos(dot));
               if (angle <= (double)maxAngle) {
                  cameraAPI.setLockOn(true);
                  currentState = CameraLockUtil.OverrideState.FORCING_LOCK;
                  return;
               }
            }
         }

         cancelOverride();
      }
   }

   public static void startLockOn(double maxDistance) {
      if (!prepareOverride()) {
         LocalPlayer player = Minecraft.m_91087_().f_91074_;
         EpicFightCameraAPI cameraAPI = EpicFightCameraAPI.getInstance();
         LivingEntity target = cameraAPI.getFocusingEntity();
         if (player != null && target != null && !preOverrideLockState && player.m_20280_(target) <= maxDistance * maxDistance) {
            cameraAPI.setLockOn(true);
            currentState = CameraLockUtil.OverrideState.FORCING_LOCK;
         } else {
            cancelOverride();
         }
      }
   }

   public static void startLockOn() {
      if (!prepareOverride()) {
         if (!preOverrideLockState) {
            EpicFightCameraAPI.getInstance().setLockOn(true);
            currentState = CameraLockUtil.OverrideState.FORCING_LOCK;
         } else {
            cancelOverride();
         }
      }
   }

   public static void startUnlock() {
      if (!prepareOverride()) {
         if (preOverrideLockState) {
            EpicFightCameraAPI.getInstance().setLockOn(false);
            currentState = CameraLockUtil.OverrideState.FORCING_UNLOCK;
         } else {
            cancelOverride();
         }
      }
   }

   public static void endLockOn() {
      restoreState();
   }

   public static void endUnlock() {
      restoreState();
   }

   public static boolean handOffForcedLockOn() {
      if (currentState != CameraLockUtil.OverrideState.FORCING_LOCK) {
         return false;
      } else {
         cancelOverride();
         return true;
      }
   }

   public static void restoreState() {
      if (currentState != CameraLockUtil.OverrideState.NONE) {
         EpicFightCameraAPI cameraAPI = EpicFightCameraAPI.getInstance();
         LocalPlayer localPlayer = Minecraft.m_91087_().f_91074_;
         if (preOverrideLockState) {
            applySmartTargeting(preOverrideTarget, localPlayer, cameraAPI);
         } else {
            cameraAPI.setLockOn(false);
            if (currentState == CameraLockUtil.OverrideState.FORCING_LOCK) {
               syncPlayerRotationToCamera(localPlayer, cameraAPI);
            }
         }

         cancelOverride();
      }
   }

   private static boolean prepareOverride() {
      if (currentState != CameraLockUtil.OverrideState.NONE) {
         return true;
      } else {
         EpicFightCameraAPI cameraAPI = EpicFightCameraAPI.getInstance();
         preOverrideLockState = cameraAPI.isLockingOnTarget();
         if (preOverrideLockState) {
            preOverrideTarget = cameraAPI.getFocusingEntity();
         } else {
            preOverrideTarget = null;
         }

         return false;
      }
   }

   private static void cancelOverride() {
      currentState = CameraLockUtil.OverrideState.NONE;
      preOverrideLockState = false;
      preOverrideTarget = null;
   }

   public static void beginSequenceAndUnlock() {
      EpicFightCameraAPI cameraAPI = EpicFightCameraAPI.getInstance();
      sequenceOriginalLockState = cameraAPI.isLockingOnTarget();
      sequenceOriginalTarget = cameraAPI.getFocusingEntity();
      if (!sequenceOriginalLockState) {
         isSequenceActive = false;
         sequenceOriginalTarget = null;
      } else {
         isSequenceActive = true;
         cameraAPI.setLockOn(false);
      }
   }

   public static void stepSequenceToLock() {
      if (isSequenceActive) {
         EpicFightCameraAPI cameraAPI = EpicFightCameraAPI.getInstance();
         LocalPlayer localPlayer = Minecraft.m_91087_().f_91074_;
         applySmartTargeting(sequenceOriginalTarget, localPlayer, cameraAPI);
      }
   }

   private static void restoreSequenceState() {
      if (isSequenceActive) {
         EpicFightCameraAPI cameraAPI = EpicFightCameraAPI.getInstance();
         LocalPlayer localPlayer = Minecraft.m_91087_().f_91074_;
         if (sequenceOriginalLockState) {
            applySmartTargeting(sequenceOriginalTarget, localPlayer, cameraAPI);
         } else {
            cameraAPI.setLockOn(false);
            syncPlayerRotationToCamera(localPlayer, cameraAPI);
         }

         sequenceOriginalTarget = null;
      }
   }

   public static void clearSequence() {
      if (isSequenceActive) {
         restoreSequenceState();
         isSequenceActive = false;
      }

      sequenceOriginalTarget = null;
   }

   private static void applySmartTargeting(@Nullable LivingEntity originalTarget, @Nullable LocalPlayer localPlayer, EpicFightCameraAPI cameraAPI) {
      LivingEntity priorityTarget = null;
      if (originalTarget != null && originalTarget.m_6084_()) {
         priorityTarget = originalTarget;
      } else if (localPlayer != null) {
         LocalPlayerPatch playerPatch = (LocalPlayerPatch)EpicFightCapabilities.getEntityPatch(localPlayer, LocalPlayerPatch.class);
         if (playerPatch != null) {
            List<Entity> triedEntities = playerPatch.getCurrentlyAttackTriedEntities();
            if (triedEntities != null && !triedEntities.isEmpty()) {
               LivingEntity closestEntity = null;
               double minDistanceSqr = Double.MAX_VALUE;

               for (Entity entity : triedEntities) {
                  if (entity instanceof LivingEntity) {
                     LivingEntity livingEntity = (LivingEntity)entity;
                     if (livingEntity.m_6084_()) {
                        double distSqr = localPlayer.m_20280_(livingEntity);
                        if (distSqr < minDistanceSqr) {
                           minDistanceSqr = distSqr;
                           closestEntity = livingEntity;
                        }
                     }
                  }
               }

               if (closestEntity != null) {
                  priorityTarget = closestEntity;
               }
            }
         }

         if (priorityTarget == null && localPlayer.m_21214_() != null && localPlayer.m_21214_().m_6084_()) {
            priorityTarget = localPlayer.m_21214_();
         }
      }

      if (priorityTarget != null) {
         cameraAPI.setLockOn(true);
         ((IEpicFightCameraAPI)cameraAPI).dmc$forceSetFocusingEntity(priorityTarget);
      } else {
         cameraAPI.setLockOn(true);
      }
   }

   private static void syncPlayerRotationToCamera(LocalPlayer player, EpicFightCameraAPI cameraAPI) {
      if (player != null && cameraAPI != null) {
         float yRot = cameraAPI.getCameraYRot();
         player.m_5618_(yRot);
         player.m_5616_(yRot);
         player.m_146922_(yRot);
         player.f_19859_ = yRot;
      }
   }

   private static enum OverrideState {
      NONE,
      FORCING_LOCK,
      FORCING_UNLOCK;
   }
}
