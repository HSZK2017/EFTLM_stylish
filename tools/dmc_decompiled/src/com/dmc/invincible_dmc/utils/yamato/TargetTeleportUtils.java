package com.dmc.invincible_dmc.utils.yamato;

import com.dmc.invincible_dmc.api.skill.JudgementCutChargePhase;
import com.dmc.invincible_dmc.conditions.GroundedCondition;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.gameassets.DMCEffects;
import com.dmc.invincible_dmc.gameassets.DMCSkillDataKeys;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class TargetTeleportUtils {
   public static void ExecuteYamatoTricker(LivingEntityPatch<?> livingEntityPatch, Vec3 swordHitPos, @Nullable LivingEntity target, boolean needRecallDoppel) {
      if (livingEntityPatch != null && !livingEntityPatch.isLogicalClient() && livingEntityPatch.getOriginal() instanceof ServerPlayer serverPlayer) {
         ServerPlayerPatch serverPlayerPatch = (ServerPlayerPatch)EpicFightCapabilities.getEntityPatch(serverPlayer, ServerPlayerPatch.class);
         if (serverPlayerPatch == null) {
            return;
         }

         Vec3 playerPos = serverPlayer.m_20182_();
         Vec3 targetCenter = target != null ? target.m_20182_().m_82520_(0.0, (double)target.m_20206_() * 0.5, 0.0) : swordHitPos;
         double dx = playerPos.f_82479_ - targetCenter.f_82479_;
         double dz = playerPos.f_82481_ - targetCenter.f_82481_;
         double horizDist = Math.sqrt(dx * dx + dz * dz);
         if (horizDist < 0.01) {
            return;
         }

         double minDist = target != null ? (double)target.m_20205_() * 0.5 + 0.8 : 0.8;
         Vec3 targetPos = new Vec3(targetCenter.f_82479_ + dx / horizDist * minDist, targetCenter.f_82480_, targetCenter.f_82481_ + dz / horizDist * minDist);
         double teleportY;
         if (target != null && isNearGround(target)) {
            teleportY = target.m_20186_();
         } else if (target != null) {
            teleportY = target.m_20186_() + (double)target.m_20206_() * 0.35;
         } else {
            teleportY = targetPos.f_82480_;
         }

         if (attemptTeleport(serverPlayer, targetPos.f_82479_, teleportY, targetPos.f_82481_)) {
         }

         boolean playingJudgementCut = DMCAnimationUtils.isPlaying(
            serverPlayerPatch,
            YamatoAnimations.YAMATO_JUDGEMENT_CUT_GROUND,
            YamatoAnimations.YAMATO_JUDGEMENT_CUT_AIR,
            YamatoAnimations.YAMATO_JUDGEMENT_CUT_AIR_FS,
            YamatoAnimations.YAMATO_JUDGEMENT_CUT_GROUND_FS
         );
         if (!playingJudgementCut || !getJudgementCutChargePhase(serverPlayerPatch).isCharging()) {
            serverPlayerPatch.playAnimationSynchronized(YamatoAnimations.YAMATO_IDLE, 0.0F);
            serverPlayerPatch.modifyLivingMotionByCurrentItem();
         }

         serverPlayer.m_7292_(new MobEffectInstance((MobEffect)DMCEffects.VERTICALSTOP.get(), 5, 0, false, false, false));
         if (target != null) {
            target.m_7292_(new MobEffectInstance((MobEffect)DMCEffects.VERTICALSTOP.get(), 5, 0, false, false, false));
         }

         if (needRecallDoppel) {
            recallDoppel(serverPlayer);
         }
      }
   }

   public static JudgementCutChargePhase getJudgementCutChargePhase(ServerPlayerPatch playerPatch) {
      SkillContainer container = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
      return container != null && !container.isEmpty() && container.getDataManager().hasData((SkillDataKey)DMCSkillDataKeys.JUDGEMENT_CUT_CHARGE_PHASE.get())
         ? JudgementCutChargePhase.byNetworkId(
            (Integer)container.getDataManager().getDataValue((SkillDataKey)DMCSkillDataKeys.JUDGEMENT_CUT_CHARGE_PHASE.get())
         )
         : JudgementCutChargePhase.IDLE;
   }

   private static void recallDoppel(ServerPlayer player) {
      for (Entity e : player.m_284548_().m_142646_().m_142273_()) {
         if (e instanceof DoppelgangerEntity dd && dd.m_6084_() && player.m_20148_().equals(dd.getOwnerUUID())) {
            DoppelgangerEntity.recallDoppelganger(dd);
            return;
         }
      }
   }

   public static boolean isNearGround(LivingEntity target) {
      return GroundedCondition.check(target);
   }

   private static boolean attemptTeleport(ServerPlayer player, double x, double y, double z) {
      Level level = player.m_9236_();
      MutableBlockPos checkPos = new MutableBlockPos(x, y, z);

      for (int i = 0; i < 5; i++) {
         BlockState block = level.m_8055_(checkPos);
         boolean canPass = block.m_60795_() || block.m_60812_(level, checkPos).m_83281_();
         if (canPass) {
            player.m_8999_((ServerLevel)player.m_9236_(), x, y, z, player.m_146908_(), player.m_146909_());
            return true;
         }

         checkPos.m_122173_(Direction.UP);
         y++;
      }

      return false;
   }
}
