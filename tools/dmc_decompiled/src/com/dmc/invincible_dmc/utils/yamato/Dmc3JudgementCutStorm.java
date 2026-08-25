package com.dmc.invincible_dmc.utils.yamato;

import com.dmc.invincible_dmc.client.sound.DMCSounds;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoVfxUtils;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.ConcentrationManager;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.property.AnimationEvent.InPeriodEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public final class Dmc3JudgementCutStorm {
   private static final float START_FRAME = 13.0F;
   private static final float END_FRAME = 170.0F;
   private static final int ENTITIES_PER_ROUND = 6;
   private static final int[] ROUND_DURATIONS = new int[]{35, 29, 25, 22, 18, 15, 13};
   private static final int MAX_ENTITIES_PER_TICK = 3;
   private static final int SOUND_INTERVAL = 2;
   private static final int RANDOM_POSITION_ATTEMPTS = 4;
   private static final double RANDOM_HEIGHT_RADIUS = 4.0;
   private static final double TARGET_RADIUS_XZ = 5.5;
   private static final double TARGET_RADIUS_Y = 5.0;

   private Dmc3JudgementCutStorm() {
   }

   public static InPeriodEvent createEvent() {
      return InPeriodEvent.create(0.21666667F, 2.8333333F, (patch, animation, parameters) -> tick(patch, animation), Side.SERVER);
   }

   private static void tick(LivingEntityPatch<?> patch, AssetAccessor<? extends DynamicAnimation> animation) {
      if (patch instanceof ServerPlayerPatch serverPlayerPatch) {
         if (((LivingEntity)patch.getOriginal()).m_9236_() instanceof ServerLevel level) {
            AnimationPlayer animationPlayer = DMCAnimationUtils.getPlayerFor(patch, animation);
            if (animationPlayer != null) {
               float previousFrame = animationPlayer.getPrevElapsedTime() * 60.0F;
               float currentFrame = animationPlayer.getElapsedTime() * 60.0F;
               int previousSpawnCount = completedSpawnCount(previousFrame);
               int currentSpawnCount = completedSpawnCount(currentFrame);
               int spawnCount = Mth.m_14045_(currentSpawnCount - previousSpawnCount, 0, 3);
               if (spawnCount > 0) {
                  LivingEntity owner = (LivingEntity)serverPlayerPatch.getOriginal();
                  AABB stormArea = YamatoAnimations.getJudgementCutEndArea(owner);
                  LivingEntity target = ConcentrationManager.resolveTarget(serverPlayerPatch);
                  if (target != null && (!owner.m_142582_(target) || !stormArea.m_82381_(target.m_20191_()))) {
                     target = null;
                  }

                  List<LivingEntity> availableTargets = ConcentrationManager.findAvailableCombatTargets(serverPlayerPatch, stormArea);
                  availableTargets.remove(target);
                  RandomSource random = owner.m_217043_();
                  boolean playPerfectSound = false;

                  for (int offset = 0; offset < spawnCount; offset++) {
                     int globalIndex = previousSpawnCount + offset;
                     if (spawnScheduledCut(level, owner, target, availableTargets, stormArea, random, globalIndex) && globalIndex % 2 == 0) {
                        playPerfectSound = true;
                     }
                  }

                  if (playPerfectSound) {
                     level.m_6263_(
                        null,
                        owner.m_20185_(),
                        owner.m_20186_(),
                        owner.m_20189_(),
                        (SoundEvent)DMCSounds.JUDGEMENT_CUT_JUST.get(),
                        SoundSource.PLAYERS,
                        0.8F,
                        1.0F
                     );
                  }
               }
            }
         }
      }
   }

   private static int completedSpawnCount(float frame) {
      int completed = 0;
      double roundStart = 13.0;

      for (int duration : ROUND_DURATIONS) {
         for (int slot = 0; slot < 6; slot++) {
            double scheduledFrame = roundStart + (double)duration * ((double)slot / 6.0);
            if ((double)frame < scheduledFrame) {
               return completed;
            }

            completed++;
         }

         roundStart += (double)duration;
      }

      return completed;
   }

   private static boolean spawnScheduledCut(
      ServerLevel level,
      LivingEntity owner,
      LivingEntity primaryTarget,
      List<LivingEntity> availableTargets,
      AABB stormArea,
      RandomSource random,
      int globalIndex
   ) {
      int roundIndex = globalIndex / 6;
      int slotIndex = globalIndex % 6;
      int rotatedSlot = Math.floorMod(slotIndex - roundIndex, 6);
      LivingEntity selectedTarget = null;
      if (primaryTarget == null || rotatedSlot != 0 && rotatedSlot != 3) {
         if (primaryTarget == null && !availableTargets.isEmpty() && (rotatedSlot == 0 || rotatedSlot == 3)) {
            int targetIndex = Math.floorMod(roundIndex * 2 + slotIndex, availableTargets.size());
            selectedTarget = availableTargets.get(targetIndex);
         }
      } else {
         selectedTarget = primaryTarget;
      }

      Vec3 position = selectedTarget != null
         ? randomPositionAroundTarget(level, selectedTarget, stormArea, random)
         : randomPositionAcrossStorm(level, owner, stormArea, random);
      return YamatoVfxUtils.spawnPerfectJudgementCut(level, owner, position);
   }

   private static Vec3 targetCenter(LivingEntity target) {
      AABB targetBox = target.m_20191_();
      return new Vec3(
         (targetBox.f_82288_ + targetBox.f_82291_) * 0.5,
         targetBox.f_82289_ + (double)target.m_20206_() * 0.55,
         (targetBox.f_82290_ + targetBox.f_82293_) * 0.5
      );
   }

   private static Vec3 randomPositionAcrossStorm(ServerLevel level, LivingEntity owner, AABB area, RandomSource random) {
      double centerY = Mth.m_14008_(owner.m_20186_() + 1.0, area.f_82289_, area.f_82292_);
      Vec3 fallback = new Vec3(owner.m_20185_(), centerY, owner.m_20189_());

      for (int attempt = 0; attempt < 4; attempt++) {
         Vec3 candidate = new Vec3(
            randomBetween(random, area.f_82288_, area.f_82291_),
            Mth.m_14008_(centerY + triangularOffset(random, 4.0), area.f_82289_, area.f_82292_),
            randomBetween(random, area.f_82290_, area.f_82293_)
         );
         if (isSpawnPositionAvailable(level, candidate)) {
            return candidate;
         }
      }

      return fallback;
   }

   private static Vec3 randomPositionAroundTarget(ServerLevel level, LivingEntity target, AABB area, RandomSource random) {
      Vec3 center = targetCenter(target);
      Vec3 fallback = clampToArea(center, area);

      for (int attempt = 0; attempt < 4; attempt++) {
         Vec3 candidate = clampToArea(center.m_82520_(triangularOffset(random, 5.5), triangularOffset(random, 5.0), triangularOffset(random, 5.5)), area);
         if (isSpawnPositionAvailable(level, candidate)) {
            return candidate;
         }
      }

      return fallback;
   }

   private static boolean isSpawnPositionAvailable(ServerLevel level, Vec3 position) {
      BlockPos blockPos = BlockPos.m_274446_(position);
      return level.m_46805_(blockPos) && level.m_8055_(blockPos).m_60812_(level, blockPos).m_83281_();
   }

   private static Vec3 clampToArea(Vec3 position, AABB area) {
      return new Vec3(
         Mth.m_14008_(position.f_82479_, area.f_82288_, area.f_82291_),
         Mth.m_14008_(position.f_82480_, area.f_82289_, area.f_82292_),
         Mth.m_14008_(position.f_82481_, area.f_82290_, area.f_82293_)
      );
   }

   private static double triangularOffset(RandomSource random, double radius) {
      return (random.m_188500_() + random.m_188500_() - 1.0) * radius;
   }

   private static double randomBetween(RandomSource random, double min, double max) {
      return Mth.m_14139_(random.m_188500_(), min, max);
   }
}
