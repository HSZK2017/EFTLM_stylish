package com.dmc.invincible_dmc.api.stun;

import com.dmc.invincible_dmc.utils.DMCLog;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public final class StrongStunController {
   private static final Map<UUID, UUID> EXECUTION_TARGETS = new HashMap<>();

   private StrongStunController() {
   }

   public static boolean request(LivingEntity target, LivingEntity attacker, AnimationAccessor<? extends StaticAnimation> animation) {
      if (!target.m_9236_().m_5776_() && target.m_6084_() && animation != null) {
         LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
         if (targetPatch == null) {
            DMCLog.warn(DMCLog.Category.STUN, "[StrongStun] START_REJECT target={} reason=missing_patch", target);
            return false;
         } else {
            EXECUTION_TARGETS.put(target.m_20148_(), attacker.m_20148_());
            freeze(target);
            if (!(target instanceof Player)) {
               target.m_7618_(Anchor.FEET, attacker.m_20182_().m_82520_(0.0, (double)attacker.m_20206_() * 0.5, 0.0));
            }

            targetPatch.playAnimationSynchronized(animation, 0.0F);
            DMCLog.info(DMCLog.Category.STUN, "[StrongStun] START target={} attacker={} animation={}", target, attacker, animation.registryName());
            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean isActive(LivingEntity target) {
      return EXECUTION_TARGETS.containsKey(target.m_20148_());
   }

   public static void finish(LivingEntity target, String reason) {
      if (EXECUTION_TARGETS.remove(target.m_20148_()) != null) {
         DMCLog.info(DMCLog.Category.STUN, "[StrongStun] END target={} reason={}", target, reason);
      }
   }

   public static void finishOwnedTargets(LivingEntity source, String reason) {
      UUID sourceEntityId = source.m_20148_();
      EXECUTION_TARGETS.entrySet().removeIf(entry -> {
         boolean matches = sourceEntityId.equals(entry.getValue());
         if (matches) {
            DMCLog.info(DMCLog.Category.STUN, "[StrongStun] END targetId={} reason={}", entry.getKey(), reason);
         }

         return matches;
      });
   }

   public static void freeze(LivingEntity target) {
      target.f_20900_ = 0.0F;
      target.f_20901_ = 0.0F;
      target.f_20902_ = 0.0F;
      target.m_20256_(Vec3.f_82478_);
      if (target instanceof Mob mob) {
         mob.m_21573_().m_26573_();
      }
   }
}
