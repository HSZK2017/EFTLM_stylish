package com.pla.annoyingvillagers.event;

import com.pla.annoyingvillagers.clazz.AVNpc;
import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.combatbehaviour.CombatCommon;
import com.pla.annoyingvillagers.entity.AlexEntity;
import com.pla.annoyingvillagers.entity.BbqEntity;
import com.pla.annoyingvillagers.entity.BlueDemonEntity;
import com.pla.annoyingvillagers.entity.BlueVillagerKnightEntity;
import com.pla.annoyingvillagers.entity.GreenVillagerKnightEntity;
import com.pla.annoyingvillagers.entity.HerobrineDragonEntity;
import com.pla.annoyingvillagers.entity.JevEntity;
import com.pla.annoyingvillagers.entity.LowHerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.LowShadowHerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.NullEntity;
import com.pla.annoyingvillagers.entity.NullSkeletonEntity;
import com.pla.annoyingvillagers.entity.PlayerNpcEntity;
import com.pla.annoyingvillagers.entity.PurpleVillagerKnightEntity;
import com.pla.annoyingvillagers.entity.ReaperHerobrineEntity;
import com.pla.annoyingvillagers.entity.RedVillagerKnightEntity;
import com.pla.annoyingvillagers.potion.ObedienceMobEffect;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.FORGE
)
public class MobTargetRedirectEvent {
   public static boolean shouldPreserveRedirectTarget(Mob mob) {
      LivingEntity currentTarget = mob.m_5448_();
      return currentTarget != null && (getRedirectTarget(mob, currentTarget) != null || isActiveRedirectObjective(currentTarget));
   }

   @Nullable
   public static LivingEntity getRedirectTarget(Mob mob, @Nullable LivingEntity currentTarget) {
      if (currentTarget != null && !(mob instanceof BlueDemonEntity) && !(mob instanceof BbqEntity)) {
         if (currentTarget instanceof HerobrineMob herobrineMob && (herobrineMob.isSacrificing() || herobrineMob.isHealing())) {
            if (herobrineMob.getFirstPossessedHerobrine() instanceof LivingEntity living) {
               return normalizeRedirectTarget(living);
            }

            if (herobrineMob.getSecondPossessedHerobrine() instanceof LivingEntity living) {
               return normalizeRedirectTarget(living);
            }

            if (herobrineMob.getThirdPossessedHerobrine() instanceof LivingEntity living) {
               return normalizeRedirectTarget(living);
            }

            if (herobrineMob.getFourthPossessedHerobrine() instanceof LivingEntity living) {
               return normalizeRedirectTarget(living);
            }
         }

         if (currentTarget instanceof LowHerobrineCloneEntity lowHerobrineCloneEntity
            && lowHerobrineCloneEntity.isHealing()
            && lowHerobrineCloneEntity.getPossessedByEntity() != null
            && !lowHerobrineCloneEntity.m_6084_()) {
            return lowHerobrineCloneEntity.getPossessedByEntity();
         }

         if (currentTarget instanceof LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntity
            && (lowShadowHerobrineCloneEntity.isSacrificing() || lowShadowHerobrineCloneEntity.isHealing())
            && lowShadowHerobrineCloneEntity.getPossessedByEntity() != null
            && !lowShadowHerobrineCloneEntity.m_6084_()) {
            return lowShadowHerobrineCloneEntity.getPossessedByEntity();
         }

         if (currentTarget instanceof NullEntity nullEntity) {
            if (nullEntity.getFirstWitherSkeleton() != null) {
               return normalizeRedirectTarget(nullEntity.getFirstWitherSkeleton());
            }

            if (nullEntity.getSecondWitherSkeleton() != null) {
               return normalizeRedirectTarget(nullEntity.getSecondWitherSkeleton());
            }
         }

         if (currentTarget instanceof NullSkeletonEntity nullSkeletonEntity && nullSkeletonEntity.getNullEntity() != null && !nullSkeletonEntity.m_6084_()) {
            return nullSkeletonEntity.getNullEntity();
         }

         if (currentTarget instanceof ReaperHerobrineEntity reaperHerobrineEntity && reaperHerobrineEntity.m_20159_()) {
            Entity living = reaperHerobrineEntity.m_20202_();
            if (living instanceof HerobrineDragonEntity) {
               return (HerobrineDragonEntity)living;
            }
         }

         if (currentTarget instanceof HerobrineDragonEntity herobrineDragonEntity
            && herobrineDragonEntity.getSummoner() instanceof ReaperHerobrineEntity reaperHerobrineEntityx
            && !reaperHerobrineEntityx.m_20159_()) {
            return reaperHerobrineEntityx;
         }

         return null;
      } else {
         return null;
      }
   }

   private static boolean shouldBlockVillagerKnightJevTarget(Mob mob, @Nullable LivingEntity target) {
      return target instanceof JevEntity && isVillagerKnight(mob);
   }

   @Nullable
   private static LivingEntity getVillagerKnightJevReplacementTarget(Mob mob, @Nullable LivingEntity target) {
      if (target instanceof JevEntity jev) {
         AlexEntity alex = jev.getFollowTarget();
         return alex != null && alex.m_6084_() && !alex.m_5833_() && !mob.m_7307_(alex) ? alex : null;
      } else {
         return null;
      }
   }

   private static boolean isVillagerKnight(Mob mob) {
      return mob instanceof RedVillagerKnightEntity
         || mob instanceof BlueVillagerKnightEntity
         || mob instanceof GreenVillagerKnightEntity
         || mob instanceof PurpleVillagerKnightEntity;
   }

   private static LivingEntity normalizeRedirectTarget(LivingEntity redirectTarget) {
      if (redirectTarget instanceof LowHerobrineCloneEntity lowHerobrineCloneEntity
         && lowHerobrineCloneEntity.isHealing()
         && lowHerobrineCloneEntity.getPossessedByEntity() != null
         && !lowHerobrineCloneEntity.m_6084_()) {
         return lowHerobrineCloneEntity.getPossessedByEntity();
      }

      if (redirectTarget instanceof LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntity
         && (lowShadowHerobrineCloneEntity.isSacrificing() || lowShadowHerobrineCloneEntity.isHealing())
         && lowShadowHerobrineCloneEntity.getPossessedByEntity() != null
         && !lowShadowHerobrineCloneEntity.m_6084_()) {
         return lowShadowHerobrineCloneEntity.getPossessedByEntity();
      }

      if (redirectTarget instanceof NullSkeletonEntity nullSkeletonEntity && nullSkeletonEntity.getNullEntity() != null && !nullSkeletonEntity.m_6084_()) {
         return nullSkeletonEntity.getNullEntity();
      }

      return redirectTarget;
   }

   private static boolean isActiveRedirectObjective(LivingEntity currentTarget) {
      if (currentTarget instanceof LowHerobrineCloneEntity lowHerobrineCloneEntity) {
         HerobrineMob owner = lowHerobrineCloneEntity.getPossessedByEntity();
         return lowHerobrineCloneEntity.isHealing() && owner != null && owner.m_6084_() && owner.isHealing();
      } else if (currentTarget instanceof LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntity) {
         HerobrineMob owner = lowShadowHerobrineCloneEntity.getPossessedByEntity();
         return (lowShadowHerobrineCloneEntity.isSacrificing() || lowShadowHerobrineCloneEntity.isHealing())
            && owner != null
            && owner.m_6084_()
            && (owner.isSacrificing() || owner.isHealing());
      } else if (currentTarget instanceof NullSkeletonEntity nullSkeletonEntity) {
         NullEntity nullEntity = nullSkeletonEntity.getNullEntity();
         return nullEntity != null
            && nullEntity.m_6084_()
            && (nullEntity.getFirstWitherSkeleton() == nullSkeletonEntity || nullEntity.getSecondWitherSkeleton() == nullSkeletonEntity);
      } else if (!(currentTarget instanceof HerobrineDragonEntity herobrineDragonEntity)) {
         return false;
      } else {
         if (herobrineDragonEntity.getSummoner() instanceof ReaperHerobrineEntity reaperHerobrineEntity
            && reaperHerobrineEntity.m_6084_()
            && reaperHerobrineEntity.m_20159_()
            && reaperHerobrineEntity.m_20202_() == herobrineDragonEntity) {
            return true;
         }

         return false;
      }
   }

   @SubscribeEvent
   public static void onLivingTick(LivingTickEvent event) {
      if (event.getEntity() instanceof Mob mob) {
         if (mob instanceof BlueDemonEntity || mob instanceof BbqEntity) {
            return;
         }

         LivingEntity currentTarget = mob.m_5448_();
         if (shouldBlockVillagerKnightJevTarget(mob, currentTarget)) {
            mob.m_6710_(getVillagerKnightJevReplacementTarget(mob, currentTarget));
            return;
         }

         LivingEntity redirectTarget = getRedirectTarget(mob, currentTarget);
         if (redirectTarget != null) {
            mob.m_6710_(redirectTarget);
            LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(mob, LivingEntityPatch.class);
            if (currentTarget instanceof ReaperHerobrineEntity
               && redirectTarget instanceof HerobrineDragonEntity
               && livingEntityPatch != null
               && (mob instanceof AVNpc || mob instanceof PlayerNpcEntity)) {
               CombatCommon.swapToBow((MobPatch<?>)livingEntityPatch);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingChangeTarget(LivingChangeTargetEvent event) {
      if (event.getEntity() instanceof Mob mob) {
         if (ObedienceMobEffect.isObedientMob(mob)) {
            LivingEntity newTarget = event.getNewTarget();
            if (shouldBlockVillagerKnightJevTarget(mob, newTarget)) {
               event.setNewTarget(getVillagerKnightJevReplacementTarget(mob, newTarget));
            } else {
               if (newTarget != null && ObedienceMobEffect.shouldBlockTarget(mob, newTarget)) {
                  event.setNewTarget(null);
               }
            }
         }
      }
   }
}
