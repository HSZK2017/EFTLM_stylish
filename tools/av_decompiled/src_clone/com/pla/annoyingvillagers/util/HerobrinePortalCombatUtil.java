package com.pla.annoyingvillagers.util;

import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.clazz.NullWeapon;
import com.pla.annoyingvillagers.entity.AegisHerobrineEntity;
import com.pla.annoyingvillagers.entity.HerobrineDragonEntity;
import com.pla.annoyingvillagers.entity.HerobrineGregEntity;
import com.pla.annoyingvillagers.entity.LowHerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.LowShadowHerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.PortalEntity;
import com.pla.annoyingvillagers.entity.TransporterHerobrineCloneEntity;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFightIronSpell;
import com.pla.annoyingvillagers.gameasset.AnimsSculkSteve;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.item.TransporterFragmentItem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public final class HerobrinePortalCombatUtil {
   private static final double WALK_ENTRANCE_RADIUS = 32.0;
   private static final double WALK_EXIT_TARGET_RADIUS = 14.0;
   private static final double PROJECTILE_ENTRANCE_RADIUS = 24.0;
   private static final double PROJECTILE_EXIT_TARGET_RADIUS = 18.0;
   private static final double COUNTER_BOW_THREAT_RADIUS = 30.0;
   private static final double COUNTER_BOW_MIN_TARGET_DISTANCE_SQR = 16.0;
   private static final double COUNTER_BOW_EXIT_DISTANCE = 3.0;
   private static final double COUNTER_BOW_AIM_DOT_THRESHOLD = 0.9;
   private static final double SUPPORT_HEROBRINE_RADIUS = 36.0;
   private static final double SUPPORT_ENEMY_RADIUS = 64.0;
   private static final double GREG_SUPPORT_PORTAL_ENEMY_DISTANCE_SQR = 100.0;
   private static final double SUPPORT_GATHER_DISTANCE_SQR = 196.0;

   private HerobrinePortalCombatUtil() {
   }

   public static boolean isHerobrineSide(Entity entity) {
      return entity instanceof HerobrineMob
         || entity instanceof HerobrineGregEntity
         || entity instanceof LowHerobrineCloneEntity
         || entity instanceof LowShadowHerobrineCloneEntity
         || entity instanceof NullWeapon;
   }

   public static boolean isEnemyOf(LivingEntity caster, LivingEntity entity) {
      return entity != caster
         && entity.m_6084_()
         && !entity.m_5833_()
         && (!(entity instanceof Player player) || !player.m_7500_())
         && !entity.m_7307_(caster)
         && !caster.m_7307_(entity)
         && !isHerobrineSide(entity);
   }

   public static boolean canUsePortalApproach(Mob mob) {
      if (!isHerobrineSide(mob)) {
         return false;
      } else if (mob instanceof HerobrineDragonEntity) {
         return false;
      } else if (mob.m_20159_() && mob.m_20202_() instanceof HerobrineDragonEntity) {
         return false;
      } else {
         if (mob instanceof NullWeapon nullWeapon && !nullWeapon.isReleased()) {
            return false;
         }

         return true;
      }
   }

   public static boolean canUsePortalOwnedBy(LivingEntity user, @Nullable UUID ownerUuid) {
      if (ownerUuid != null && !ownerUuid.equals(user.m_20148_())) {
         if (!(user.m_9236_() instanceof ServerLevel serverLevel)) {
            return false;
         } else {
            Entity owner = serverLevel.m_8791_(ownerUuid);
            return owner != null && isHerobrineSide(user) && isHerobrineSide(owner);
         }
      } else {
         return true;
      }
   }

   @Nullable
   public static HerobrinePortalCombatUtil.PortalRoute findRouteToTarget(Mob mob, LivingEntity target) {
      return !canUsePortalApproach(mob) ? null : findRouteNearEntity(mob, target, 32.0, 14.0, true);
   }

   @Nullable
   public static Vec3 getProjectilePortalAim(Entity shooter, LivingEntity target) {
      HerobrinePortalCombatUtil.PortalRoute route = findRouteNearEntity(shooter, target, 24.0, 18.0, false);
      return route == null ? null : route.entrance().getPortalCenter();
   }

   @Nullable
   private static HerobrinePortalCombatUtil.PortalRoute findRouteNearEntity(
      Entity source, LivingEntity target, double entranceRadius, double exitRadius, boolean walkingRoute
   ) {
      if (source.m_9236_() instanceof ServerLevel && target != null && target.m_6084_()) {
         if (walkingRoute && source instanceof Mob mob && !canUsePortalApproach(mob)) {
            return null;
         }

         AABB searchBox = source.m_20191_().m_82400_(entranceRadius);
         Vec3 sourceCenter = source.m_20182_().m_82520_(0.0, (double)source.m_20206_() * 0.5, 0.0);
         Vec3 targetCenter = entityCenter(target);
         double directTargetDistance = sourceCenter.m_82557_(targetCenter);
         HerobrinePortalCombatUtil.PortalRoute bestRoute = null;
         double bestScore = Double.MAX_VALUE;

         for (PortalEntity portal : source.m_9236_().m_45976_(PortalEntity.class, searchBox)) {
            if (isUsablePortalFor(source, portal)) {
               PortalEntity linkedPortal = portal.getLinkedPortal();
               if (linkedPortal != null && isUsablePortalFor(source, linkedPortal)) {
                  double exitDistance = linkedPortal.getPortalCenter().m_82557_(targetCenter);
                  if (!(exitDistance > exitRadius * exitRadius)) {
                     double entranceDistance = portal.getPortalCenter().m_82557_(sourceCenter);
                     if (!walkingRoute || !(entranceDistance >= directTargetDistance)) {
                        double score = walkingRoute ? exitDistance + entranceDistance * 0.35 : entranceDistance + exitDistance * 0.35;
                        if (score < bestScore) {
                           bestScore = score;
                           bestRoute = new HerobrinePortalCombatUtil.PortalRoute(portal, linkedPortal);
                        }
                     }
                  }
               }
            }
         }

         return bestRoute;
      } else {
         return null;
      }
   }

   private static boolean isUsablePortalFor(Entity user, PortalEntity portal) {
      if (portal != null && !portal.m_213877_() && portal.m_6084_()) {
         UUID ownerUuid = portal.getOwnerUUID();
         if (ownerUuid != null && !ownerUuid.equals(user.m_20148_())) {
            if (!(user.m_9236_() instanceof ServerLevel serverLevel)) {
               return false;
            } else {
               Entity owner = serverLevel.m_8791_(ownerUuid);
               if (owner == null) {
                  return false;
               } else {
                  return user instanceof HerobrineDragonEntity ? isHerobrineSide(owner) : isHerobrineSide(user) && isHerobrineSide(owner);
               }
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean tryTransporterPortalSupport(LivingEntity caster) {
      if (caster.m_9236_() instanceof ServerLevel serverLevel && TransporterFragmentItem.canSpawnOwnedPortals(serverLevel, caster, 2)) {
         LivingEntity fallbackEnemy = findNearestEnemy(caster, 64.0);
         HerobrinePortalCombatUtil.SupportPortalPlan plan = pickSupportPortalPlan(caster, findSupportHerobrines(caster, 36.0), fallbackEnemy, true);
         if (plan == null) {
            return false;
         }

         return spawnSupportPortalPair(caster, plan.entrance(), plan.exit());
      }

      return false;
   }

   public static boolean canTransporterPortalSupport(LivingEntity caster) {
      if (caster.m_9236_() instanceof ServerLevel serverLevel && TransporterFragmentItem.canSpawnOwnedPortals(serverLevel, caster, 2)) {
         LivingEntity fallbackEnemy = findNearestEnemy(caster, 64.0);
         HerobrinePortalCombatUtil.SupportPortalPlan plan = pickSupportPortalPlan(caster, findSupportHerobrines(caster, 36.0), fallbackEnemy, true);
         return plan != null;
      }

      return false;
   }

   public static boolean tryGregPortalSupport(HerobrineGregEntity greg) {
      if (greg.m_9236_() instanceof ServerLevel serverLevel && TransporterFragmentItem.canSpawnOwnedPortals(serverLevel, greg, 2)) {
         List<LivingEntity> supports = findSupportHerobrines(greg, 36.0).stream().filter(HerobrinePortalCombatUtil::canUseGregGeneralSupport).toList();
         if (supports.isEmpty()) {
            return false;
         }

         HerobrinePortalCombatUtil.SupportPortalPlan plan = pickSupportPortalPlan(greg, supports, findNearestEnemy(greg, 64.0), false);
         if (plan == null) {
            return false;
         }

         greg.markSupportingHerobrine();
         return spawnSupportPortalPair(greg, plan.entrance(), plan.exit());
      }

      return false;
   }

   public static boolean canGregPortalSupport(HerobrineGregEntity greg) {
      if (greg.m_9236_() instanceof ServerLevel serverLevel && TransporterFragmentItem.canSpawnOwnedPortals(serverLevel, greg, 2)) {
         List<LivingEntity> supports = findSupportHerobrines(greg, 36.0).stream().filter(HerobrinePortalCombatUtil::canUseGregGeneralSupport).toList();
         if (supports.isEmpty()) {
            return false;
         }

         HerobrinePortalCombatUtil.SupportPortalPlan plan = pickSupportPortalPlan(greg, supports, findNearestEnemy(greg, 64.0), false);
         return plan != null;
      }

      return false;
   }

   private static boolean canUseGregGeneralSupport(LivingEntity support) {
      return !(support instanceof TransporterHerobrineCloneEntity)
         && !(support instanceof LowHerobrineCloneEntity)
         && !(support instanceof LowShadowHerobrineCloneEntity);
   }

   public static boolean tryAegisProtectPortal(AegisHerobrineEntity aegis) {
      LivingEntity support = findSupportHerobrine(aegis, 36.0);
      return support != null && !isRidingHerobrineDragon(support) ? spawnSupportPortalPair(aegis, aegis, support) : false;
   }

   public static boolean tryBowCounterPortalSupport(LivingEntity caster) {
      if (caster.m_9236_() instanceof ServerLevel serverLevel && TransporterFragmentItem.canSpawnOwnedPortals(serverLevel, caster, 2)) {
         HerobrinePortalCombatUtil.BowCounterThreat threat = findBowCounterThreat(caster, 30.0);
         if (threat == null) {
            return false;
         }

         Vec3 entrancePreferred = buildBowCounterEntrance(threat.attacker(), threat.target());
         Vec3 exitPreferred = buildBowCounterExit(threat.attacker(), threat.target());
         if (entrancePreferred != null && exitPreferred != null) {
            if (TransporterFragmentItem.spawnLinkedPortalPair(caster.m_9236_(), caster, entrancePreferred, exitPreferred) <= 0) {
               return false;
            }

            playPortalPairSummon(caster);
            triggerCounterPortalRetreat(caster, threat.attacker());
            return true;
         }

         return false;
      }

      return false;
   }

   public static boolean canBowCounterPortalSupport(LivingEntity caster) {
      if (caster.m_9236_() instanceof ServerLevel serverLevel && TransporterFragmentItem.canSpawnOwnedPortals(serverLevel, caster, 2)) {
         HerobrinePortalCombatUtil.BowCounterThreat threat = findBowCounterThreat(caster, 30.0);
         if (threat == null) {
            return false;
         }

         Vec3 entrancePreferred = buildBowCounterEntrance(threat.attacker(), threat.target());
         Vec3 exitPreferred = buildBowCounterExit(threat.attacker(), threat.target());
         return entrancePreferred != null && exitPreferred != null;
      }

      return false;
   }

   public static boolean spawnSupportPortalPair(LivingEntity caster, LivingEntity entranceEntity, LivingEntity exitEntity) {
      if (!(caster.m_9236_() instanceof ServerLevel)) {
         return false;
      } else {
         RandomSource random = caster.m_217043_();
         Vec3 entrancePreferred = randomPortalPreferredPosNear(caster, entranceEntity, random, 2.8, 5.0);
         Vec3 exitPreferred = randomPortalPreferredPosNear(caster, exitEntity, random, 2.8, 5.0);
         int spawned = TransporterFragmentItem.spawnLinkedPortalPair(caster.m_9236_(), caster, entrancePreferred, exitPreferred);
         if (spawned <= 0) {
            return false;
         } else {
            playPortalPairSummon(caster);
            return true;
         }
      }
   }

   public static void playSixPortalSummon(LivingEntity entity) {
      if (entity instanceof HerobrineGregEntity greg) {
         greg.markSupportingHerobrine();
      }

      LivingEntityPatch<?> patch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
      if (patch != null && !entity.m_9236_().m_5776_()) {
         patch.playAnimationSynchronized(AnimsSculkSteve.PORTAL_SUMMON, 0.0F);
      }
   }

   public static void playPortalPairSummon(LivingEntity entity) {
      LivingEntityPatch<?> patch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
      if (patch != null && !entity.m_9236_().m_5776_()) {
         patch.playAnimationSynchronized(AnimsEpicFightIronSpell.CASTING_ONE_HAND_TOP, 0.0F);
      }
   }

   public static void playClonePortalSummon(LivingEntity entity) {
      if (!entity.m_9236_().m_5776_()) {
         entity.m_9236_().m_5594_(null, entity.m_20183_(), (SoundEvent)AnnoyingVillagersModSounds.PORTAL_NATURAL.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
      }

      if (entity instanceof HerobrineGregEntity greg) {
         greg.markSupportingHerobrine();
      }

      LivingEntityPatch<?> patch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
      if (patch != null && !entity.m_9236_().m_5776_()) {
         patch.playAnimationSynchronized(AnimsSculkSteve.PORTAL_SUMMON, 0.0F);
      }
   }

   @Nullable
   public static LivingEntity findSupportHerobrine(LivingEntity caster, double radius) {
      List<LivingEntity> candidates = findSupportHerobrines(caster, radius);
      return candidates.isEmpty() ? null : candidates.get(0);
   }

   public static List<LivingEntity> findSupportHerobrines(LivingEntity caster, double radius) {
      AABB searchBox = caster.m_20191_().m_82400_(radius);
      List<LivingEntity> candidates = caster.m_9236_()
         .m_6443_(
            LivingEntity.class,
            searchBox,
            entity -> entity != caster && entity.m_6084_() && isHerobrineSide(entity) && !(entity instanceof HerobrineGregEntity)
         );
      candidates.sort(Comparator.comparingDouble(caster::m_20280_));
      return candidates;
   }

   public static boolean hasNearbyPortalGroup(LivingEntity anchor, @Nullable UUID ownerUuid, int requiredCount, double radius) {
      if (requiredCount <= 0) {
         return true;
      } else {
         Map<UUID, Integer> portalGroupCounts = new HashMap<>();

         for (PortalEntity portal : anchor.m_9236_().m_45976_(PortalEntity.class, anchor.m_20191_().m_82400_(radius))) {
            if (!portal.m_213877_() && portal.m_6084_() && portal.f_19797_ < 200) {
               UUID portalGroupUuid = portal.getPortalGroupUUID();
               if (portalGroupUuid != null && (ownerUuid == null || ownerUuid.equals(portal.getOwnerUUID()))) {
                  int count = portalGroupCounts.merge(portalGroupUuid, 1, Integer::sum);
                  if (count >= requiredCount) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   @Nullable
   public static LivingEntity findPortalSupportHerobrine(LivingEntity caster, double radius) {
      List<LivingEntity> candidates = findSupportHerobrines(caster, radius);
      if (candidates.isEmpty()) {
         return null;
      } else {
         List<HerobrinePortalCombatUtil.SupportTarget> supportTargets = buildSupportTargets(caster, candidates, findNearestEnemy(caster, 64.0), 64.0);
         List<HerobrinePortalCombatUtil.SupportTarget> farTargets = supportTargets.stream()
            .filter(target -> target.enemy() != null && target.enemyDistanceSqr() >= 100.0)
            .toList();
         if (!farTargets.isEmpty()) {
            return pickRandomTopSupportTarget(farTargets, caster.m_217043_()).support();
         } else {
            List<HerobrinePortalCombatUtil.SupportTarget> engagedTargets = supportTargets.stream().filter(target -> target.enemy() != null).toList();
            return !engagedTargets.isEmpty()
               ? pickRandomTopSupportTarget(engagedTargets, caster.m_217043_()).support()
               : candidates.get(caster.m_217043_().m_188503_(candidates.size()));
         }
      }
   }

   @Nullable
   public static LivingEntity findEnemyForSupport(LivingEntity support, @Nullable LivingEntity fallback, double radius) {
      if (support instanceof Mob mob && mob.m_5448_() != null && isEnemyOf(support, mob.m_5448_())) {
         return mob.m_5448_();
      }

      return fallback != null && isEnemyOf(support, fallback) ? fallback : findNearestEnemy(support, radius);
   }

   @Nullable
   public static LivingEntity findThreateningEnemy(LivingEntity caster, @Nullable LivingEntity support, double radius) {
      LivingEntity recentThreat = chooseNearestThreat(caster, support, radius, caster.m_21188_(), support != null ? support.m_21188_() : null);
      if (recentThreat != null) {
         return recentThreat;
      } else {
         LivingEntity targetedThreat = chooseNearestThreat(
            caster, support, radius, caster instanceof Mob mobx ? mobx.m_5448_() : null, support instanceof Mob mob ? mob.m_5448_() : null
         );
         if (targetedThreat != null) {
            return targetedThreat;
         } else {
            HerobrinePortalCombatUtil.BowCounterThreat rangedThreat = findBowCounterThreat(caster, support, radius);
            if (rangedThreat != null) {
               return rangedThreat.attacker();
            } else {
               AABB searchBox = support == null ? caster.m_20191_().m_82400_(radius) : caster.m_20191_().m_82367_(support.m_20191_()).m_82400_(radius);
               return caster.m_9236_()
                  .m_6443_(LivingEntity.class, searchBox, entity -> isThreateningEnemy(caster, support, entity, radius))
                  .stream()
                  .min(Comparator.comparingDouble(entity -> threatDistanceSqr(caster, support, entity)))
                  .orElse(null);
            }
         }
      }
   }

   @Nullable
   private static LivingEntity findNearestEnemy(LivingEntity caster, double radius) {
      if (caster instanceof Mob mob && mob.m_5448_() != null && isEnemyOf(caster, mob.m_5448_())) {
         return mob.m_5448_();
      }

      AABB searchBox = caster.m_20191_().m_82400_(radius);
      return caster.m_9236_()
         .m_6443_(LivingEntity.class, searchBox, entity -> isEnemyOf(caster, entity))
         .stream()
         .min(Comparator.comparingDouble(caster::m_20280_))
         .orElse(null);
   }

   @Nullable
   private static LivingEntity chooseNearestThreat(
      LivingEntity caster, @Nullable LivingEntity support, double radius, @Nullable LivingEntity first, @Nullable LivingEntity second
   ) {
      LivingEntity best = null;
      double bestDistance = Double.MAX_VALUE;

      for (LivingEntity candidate : new LivingEntity[]{first, second}) {
         if (candidate != null && isThreatCandidate(caster, support, candidate, radius)) {
            double distance = threatDistanceSqr(caster, support, candidate);
            if (distance < bestDistance) {
               bestDistance = distance;
               best = candidate;
            }
         }
      }

      return best;
   }

   private static boolean isThreatCandidate(LivingEntity caster, @Nullable LivingEntity support, LivingEntity candidate, double radius) {
      if (isEnemyOf(caster, candidate) || support != null && isEnemyOf(support, candidate)) {
         double radiusSqr = radius * radius;
         return threatDistanceSqr(caster, support, candidate) <= radiusSqr;
      } else {
         return false;
      }
   }

   private static boolean isThreateningEnemy(LivingEntity caster, @Nullable LivingEntity support, LivingEntity candidate, double radius) {
      if (!isThreatCandidate(caster, support, candidate, radius)) {
         return false;
      } else if (isBowCounterThreat(caster, support, candidate, radius)) {
         return true;
      } else {
         return !(candidate instanceof Mob mob) ? false : mob.m_5448_() == caster || support != null && mob.m_5448_() == support;
      }
   }

   private static double threatDistanceSqr(LivingEntity caster, @Nullable LivingEntity support, LivingEntity entity) {
      double distance = caster.m_20280_(entity);
      if (support != null) {
         distance = Math.min(distance, support.m_20280_(entity));
      }

      return distance;
   }

   private static boolean isRidingHerobrineDragon(Entity entity) {
      return entity.m_20159_() && entity.m_20202_() instanceof HerobrineDragonEntity;
   }

   @Nullable
   private static HerobrinePortalCombatUtil.SupportPortalPlan pickSupportPortalPlan(
      LivingEntity caster, List<LivingEntity> supports, @Nullable LivingEntity fallbackEnemy, boolean allowSelfFallback
   ) {
      if (!supports.isEmpty()) {
         List<HerobrinePortalCombatUtil.SupportTarget> supportTargets = buildSupportTargets(caster, supports, fallbackEnemy, 64.0);
         List<HerobrinePortalCombatUtil.SupportTarget> farTargets = supportTargets.stream()
            .filter(target -> target.enemy() != null && target.enemyDistanceSqr() >= 100.0)
            .sorted(Comparator.comparingDouble(HerobrinePortalCombatUtil.SupportTarget::enemyDistanceSqr).reversed())
            .toList();
         if (!farTargets.isEmpty()) {
            HerobrinePortalCombatUtil.SupportTarget chosen = pickRandomTopSupportTarget(farTargets, caster.m_217043_());
            return new HerobrinePortalCombatUtil.SupportPortalPlan(chosen.support(), chosen.enemy());
         }

         HerobrinePortalCombatUtil.SupportPortalPlan gatherPlan = findGatherPlan(supports);
         if (gatherPlan != null) {
            return gatherPlan;
         }
      }

      return allowSelfFallback && fallbackEnemy != null ? new HerobrinePortalCombatUtil.SupportPortalPlan(caster, fallbackEnemy) : null;
   }

   private static List<HerobrinePortalCombatUtil.SupportTarget> buildSupportTargets(
      LivingEntity caster, List<LivingEntity> supports, @Nullable LivingEntity fallbackEnemy, double radius
   ) {
      List<HerobrinePortalCombatUtil.SupportTarget> targets = new ArrayList<>();

      for (LivingEntity support : supports) {
         if (support.m_6084_() && !isRidingHerobrineDragon(support)) {
            LivingEntity enemy = findEnemyForSupport(support, fallbackEnemy, radius);
            double enemyDistanceSqr = enemy == null ? -1.0 : support.m_20280_(enemy);
            targets.add(new HerobrinePortalCombatUtil.SupportTarget(support, enemy, enemyDistanceSqr));
         }
      }

      return targets;
   }

   private static HerobrinePortalCombatUtil.SupportTarget pickRandomTopSupportTarget(List<HerobrinePortalCombatUtil.SupportTarget> targets, RandomSource random) {
      int limit = Math.min(3, targets.size());
      return targets.get(random.m_188503_(limit));
   }

   @Nullable
   private static HerobrinePortalCombatUtil.SupportPortalPlan findGatherPlan(List<LivingEntity> supports) {
      if (supports.size() < 2) {
         return null;
      } else {
         LivingEntity first = null;
         LivingEntity second = null;
         double bestDistanceSqr = 196.0;

         for (int i = 0; i < supports.size() - 1; i++) {
            LivingEntity left = supports.get(i);
            if (left.m_6084_() && !isRidingHerobrineDragon(left)) {
               for (int j = i + 1; j < supports.size(); j++) {
                  LivingEntity right = supports.get(j);
                  if (right.m_6084_() && !isRidingHerobrineDragon(right)) {
                     double distanceSqr = left.m_20280_(right);
                     if (distanceSqr > bestDistanceSqr) {
                        bestDistanceSqr = distanceSqr;
                        first = left;
                        second = right;
                     }
                  }
               }
            }
         }

         return first != null && second != null ? new HerobrinePortalCombatUtil.SupportPortalPlan(first, second) : null;
      }
   }

   private static Vec3 randomPortalPreferredPosNear(LivingEntity caster, Entity entity, RandomSource random, double minDistance, double maxDistance) {
      double angle = random.m_188500_() * Math.PI * 2.0;
      double distance = minDistance + random.m_188500_() * (maxDistance - minDistance);
      Vec3 preferred = new Vec3(entity.m_20185_() + Math.cos(angle) * distance, Math.floor(entity.m_20186_()), entity.m_20189_() + Math.sin(angle) * distance);
      return applySupportPortalYOffset(caster, preferred);
   }

   private static Vec3 entityCenter(Entity entity) {
      return new Vec3(entity.m_20185_(), entity.m_20186_() + (double)entity.m_20206_() * 0.5, entity.m_20189_());
   }

   @Nullable
   public static Vec3 applySupportPortalYOffset(LivingEntity caster, @Nullable Vec3 preferred) {
      if (preferred == null) {
         return null;
      } else {
         return caster instanceof HerobrineGregEntity greg ? preferred.m_82520_(0.0, (double)greg.m_217043_().m_188503_(6), 0.0) : preferred;
      }
   }

   @Nullable
   private static HerobrinePortalCombatUtil.BowCounterThreat findBowCounterThreat(LivingEntity caster, double radius) {
      return findBowCounterThreat(caster, null, radius);
   }

   @Nullable
   private static HerobrinePortalCombatUtil.BowCounterThreat findBowCounterThreat(LivingEntity caster, @Nullable LivingEntity support, double radius) {
      AABB searchBox = caster.m_20191_().m_82400_(radius);
      HerobrinePortalCombatUtil.BowCounterThreat bestThreat = null;
      double bestCasterDistance = Double.MAX_VALUE;
      double bestTargetDistance = Double.MAX_VALUE;

      for (LivingEntity attacker : caster.m_9236_().m_6443_(LivingEntity.class, searchBox, entity -> isPotentialBowCounterAttacker(caster, entity))) {
         LivingEntity target = resolveBowCounterTarget(caster, support, attacker, radius);
         if (target != null) {
            double casterDistance = caster.m_20280_(attacker);
            double targetDistance = attacker.m_20280_(target);
            if (bestThreat == null || casterDistance < bestCasterDistance || casterDistance == bestCasterDistance && targetDistance < bestTargetDistance) {
               bestThreat = new HerobrinePortalCombatUtil.BowCounterThreat(attacker, target);
               bestCasterDistance = casterDistance;
               bestTargetDistance = targetDistance;
            }
         }
      }

      return bestThreat;
   }

   private static boolean isPotentialBowCounterAttacker(LivingEntity caster, LivingEntity attacker) {
      return attacker != caster && attacker.m_6084_() && isEnemyOf(caster, attacker) && hasBowReady(attacker);
   }

   private static boolean hasBowReady(LivingEntity attacker) {
      return attacker.m_21205_().m_41720_() instanceof BowItem
         || attacker.m_21206_().m_41720_() instanceof BowItem
         || attacker.m_21211_().m_41720_() instanceof BowItem;
   }

   private static boolean isBowCounterThreat(LivingEntity caster, @Nullable LivingEntity support, LivingEntity attacker, double radius) {
      return resolveBowCounterTarget(caster, support, attacker, radius) != null;
   }

   @Nullable
   private static LivingEntity resolveBowCounterTarget(LivingEntity caster, @Nullable LivingEntity support, LivingEntity attacker, double radius) {
      if (!isPotentialBowCounterAttacker(caster, attacker)) {
         return null;
      } else {
         if (attacker instanceof Mob mob) {
            LivingEntity mobTarget = mob.m_5448_();
            if (isValidBowCounterTarget(caster, support, attacker, mobTarget)) {
               return mobTarget;
            }
         }

         AABB searchBox = attacker.m_20191_().m_82400_(radius);
         LivingEntity bestTarget = null;
         double bestAim = 0.9;
         double bestDistance = Double.MAX_VALUE;
         Vec3 look = attacker.m_20154_();
         if (look.m_82556_() < 1.0E-4) {
            return null;
         } else {
            look = look.m_82541_();

            for (LivingEntity candidate : attacker.m_9236_()
               .m_6443_(LivingEntity.class, searchBox, entity -> isValidBowCounterTarget(caster, support, attacker, entity))) {
               Vec3 direction = entityCenter(candidate).m_82546_(attacker.m_146892_());
               if (!(direction.m_82556_() < 1.0E-4)) {
                  double aimDot = look.m_82526_(direction.m_82541_());
                  double distance = attacker.m_20280_(candidate);
                  if (aimDot > bestAim || aimDot == bestAim && distance < bestDistance) {
                     bestTarget = candidate;
                     bestAim = aimDot;
                     bestDistance = distance;
                  }
               }
            }

            return bestTarget;
         }
      }
   }

   private static boolean isValidBowCounterTarget(LivingEntity caster, @Nullable LivingEntity support, LivingEntity attacker, @Nullable LivingEntity target) {
      if (target == null || !target.m_6084_()) {
         return false;
      } else {
         return target != caster && target != support && !isSupportedPortalDefenseTarget(caster, target)
            ? false
            : attacker.m_142582_(target) && attacker.m_20280_(target) >= 16.0;
      }
   }

   private static boolean isSupportedPortalDefenseTarget(LivingEntity caster, LivingEntity target) {
      return target == caster || target instanceof HerobrineMob || target instanceof LowHerobrineCloneEntity || target instanceof LowShadowHerobrineCloneEntity;
   }

   @Nullable
   private static Vec3 buildBowCounterEntrance(LivingEntity attacker, LivingEntity target) {
      Vec3 attackerCenter = entityCenter(attacker);
      Vec3 targetCenter = entityCenter(target);
      Vec3 direction = horizontalDirection(targetCenter.m_82546_(attackerCenter));
      if (direction.m_82556_() < 1.0E-4) {
         return null;
      } else {
         Vec3 midpoint = attackerCenter.m_82549_(targetCenter).m_82490_(0.5);
         return new Vec3(midpoint.f_82479_, Math.max(attacker.m_20186_(), target.m_20186_()), midpoint.f_82481_);
      }
   }

   @Nullable
   private static Vec3 buildBowCounterExit(LivingEntity attacker, LivingEntity target) {
      Vec3 attackerCenter = entityCenter(attacker);
      Vec3 targetCenter = entityCenter(target);
      Vec3 direction = horizontalDirection(targetCenter.m_82546_(attackerCenter));
      if (direction.m_82556_() < 1.0E-4) {
         return null;
      } else {
         Vec3 side = new Vec3(-direction.f_82481_, 0.0, direction.f_82479_);
         int sideChoice = attacker.m_217043_().m_188503_(3);

         Vec3 offset = switch (sideChoice) {
            case 1 -> side.m_82490_(3.0);
            case 2 -> side.m_82490_(-3.0);
            default -> direction.m_82490_(-3.0);
         };
         Vec3 position = attacker.m_20182_().m_82549_(offset);
         return new Vec3(position.f_82479_, attacker.m_20186_() + (double)attacker.m_217043_().m_188503_(4), position.f_82481_);
      }
   }

   private static void triggerCounterPortalRetreat(LivingEntity caster, LivingEntity attacker) {
      if (caster instanceof HerobrineGregEntity greg) {
         greg.triggerRangedCounterRetreat(attacker);
      } else if (caster instanceof TransporterHerobrineCloneEntity transporter) {
         transporter.triggerRangedCounterRetreat(attacker);
      }
   }

   private static Vec3 horizontalDirection(Vec3 vector) {
      Vec3 flattened = new Vec3(vector.f_82479_, 0.0, vector.f_82481_);
      return flattened.m_82556_() < 1.0E-4 ? Vec3.f_82478_ : flattened.m_82541_();
   }

   private static record BowCounterThreat(LivingEntity attacker, LivingEntity target) {
   }

   public static record PortalRoute(PortalEntity entrance, PortalEntity exit) {
   }

   private static record SupportPortalPlan(LivingEntity entrance, LivingEntity exit) {
   }

   private static record SupportTarget(LivingEntity support, @Nullable LivingEntity enemy, double enemyDistanceSqr) {
   }
}
