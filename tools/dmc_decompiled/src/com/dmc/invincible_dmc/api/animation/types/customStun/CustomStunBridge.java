package com.dmc.invincible_dmc.api.animation.types.customStun;

import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.mixin.epicfight.ArmaturesAccessor;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import com.dmc.invincible_dmc.utils.DMCLog;
import java.util.Map;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.model.armature.types.HumanLikeArmature;
import yesman.epicfight.model.armature.types.ToolHolderArmature;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

public final class CustomStunBridge {
   private CustomStunBridge() {
   }

   private static boolean isAttackerInSDT(Entity attacker) {
      if (attacker instanceof Player player) {
         return SinDevilTriggerManager.isPlayerInSDT(player);
      } else if (attacker instanceof DoppelgangerEntity doppel) {
         Player owner = doppel.getOwner();
         return SinDevilTriggerManager.isPlayerInSDT(owner);
      } else {
         return false;
      }
   }

   public static boolean isHumanoidTarget(LivingEntity target) {
      LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
      if (targetPatch == null) {
         return false;
      } else {
         Armature armature = targetPatch.getArmature();
         if (armature instanceof HumanoidArmature) {
            return true;
         } else if (armature instanceof HumanLikeArmature) {
            return true;
         } else if (armature instanceof ToolHolderArmature) {
            return true;
         } else {
            Map<EntityType<?>, AssetAccessor<? extends Armature>> map = ArmaturesAccessor.getEntityTypeArmatureMapper();
            return map.get(target.m_6095_()) == Armatures.BIPED;
         }
      }
   }

   public static void resolveAndSetPending(EpicFightDamageSource damageSource, Entity target) {
      DMCLog.info(
         DMCLog.Category.STUN, "[Stun] RESOLVE_BEGIN source={} attacker={} target={}", damageSource.getClass().getSimpleName(), damageSource.m_7639_(), target
      );
      clearPending(target);
      if (!(damageSource instanceof ICustomStunDamageSource customSrc) || !customSrc.invincible$hasCustomStunAnimations()) {
         DMCLog.debug(DMCLog.Category.STUN, "[Stun] RESOLVE_SKIP reason=no_custom_animation");
         return;
      }

      if (!(target instanceof LivingEntity livingTarget)) {
         DMCLog.debug(DMCLog.Category.STUN, "[Stun] RESOLVE_SKIP reason=target_not_living");
         return;
      }

      if (!isHumanoidTarget(livingTarget)) {
         DMCLog.debug(DMCLog.Category.STUN, "[Stun] RESOLVE_SKIP target={} reason=non_humanoid", target);
      } else {
         LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(livingTarget, LivingEntityPatch.class);
         if (targetPatch instanceof ICustomStunReceiver receiver) {
            boolean isAttackerSDT = isAttackerInSDT(damageSource.m_7639_());
            AnimationAccessor<? extends StaticAnimation> anim = customSrc.invincible$resolveCustomStunAnimation(livingTarget, isAttackerSDT);
            if (anim == null) {
               DMCLog.warn(DMCLog.Category.STUN, "[Stun] RESOLVE_SKIP target={} attackerSdt={} reason=resolved_animation_null", livingTarget, isAttackerSDT);
               return;
            }

            receiver.invincible$setPendingCustomStunAnimation(anim);
            receiver.invincible$setPendingVerticalOffset(customSrc.invincible$getCustomStunVerticalOffset());
            DMCLog.info(DMCLog.Category.STUN, "[Stun] PENDING_SET target={} animation={} attackerSdt={}", livingTarget, anim.registryName(), isAttackerSDT);
            Entity sourceEntity = damageSource.m_7639_();
            Vec3 sourcePosition = resolveDirectionalSourcePosition(sourceEntity, customSrc.invincible$getCustomStunSourceYRot(), livingTarget);
            receiver.invincible$setPendingAttackerPos(sourcePosition);
            damageSource.setInitialPosition(sourcePosition);
            return;
         }

         DMCLog.warn(DMCLog.Category.STUN, "[Stun] RESOLVE_SKIP target={} reason=no_receiver_patch", target);
      }
   }

   private static Vec3 resolveDirectionalSourcePosition(Entity sourceEntity, Float sourceYRotOverride, LivingEntity target) {
      if (sourceEntity == null && sourceYRotOverride == null) {
         return Vec3.m_82512_(target.m_20183_());
      } else {
         float sourceYRot;
         if (sourceYRotOverride != null) {
            sourceYRot = sourceYRotOverride;
         } else {
            LivingEntityPatch<?> sourcePatch = sourceEntity instanceof LivingEntity livingSource
               ? (LivingEntityPatch)EpicFightCapabilities.getEntityPatch(livingSource, LivingEntityPatch.class)
               : null;
            sourceYRot = sourcePatch != null ? sourcePatch.getYRot() : sourceEntity.m_146908_();
         }

         Vec3 attackForward = Vec3.m_82498_(0.0F, sourceYRot);
         return target.m_20182_().m_82546_(attackForward);
      }
   }

   public static void clearPending(Entity target) {
      if (target instanceof LivingEntity livingTarget) {
         LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(livingTarget, LivingEntityPatch.class);
         if (targetPatch instanceof ICustomStunReceiver receiver) {
            receiver.invincible$clearPendingCustomStunAnimation();
            DMCLog.debug(DMCLog.Category.STUN, "[Stun] PENDING_CLEAR target={}", livingTarget);
         }
      }
   }

   public static boolean hasPending(Entity target) {
      if (!(target instanceof LivingEntity livingTarget)) {
         return false;
      } else {
         LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(livingTarget, LivingEntityPatch.class);
         if (targetPatch instanceof ICustomStunReceiver receiver && receiver.invincible$getPendingCustomStunAnimation() != null) {
            return true;
         }

         return false;
      }
   }
}
