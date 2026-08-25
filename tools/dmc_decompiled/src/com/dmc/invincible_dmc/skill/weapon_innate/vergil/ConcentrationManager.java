package com.dmc.invincible_dmc.skill.weapon_innate.vergil;

import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.capability.yamato.YamatoPlayerState;
import com.dmc.invincible_dmc.capability.yamato.YamatoPlayerStateProvider;
import com.dmc.invincible_dmc.damagesource.DMCDamageTypeTags;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerPatch;
import com.dmc.invincible_dmc.entity.dummy.DummyEntity;
import com.dmc.invincible_dmc.entity.judgementcut.JudgementCutEntity;
import com.dmc.invincible_dmc.entity.judgementcut.JudgementCutPatch;
import com.dmc.invincible_dmc.entity.summonedsword.DMCSummonedSwordEntity;
import com.dmc.invincible_dmc.entity.summonedsword.DMCSummonedSwordPatch;
import com.dmc.invincible_dmc.gameassets.DMCSkillDataKeys;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.skill.weapon_innate.AbstractDmcInnateSkill;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.eventlistener.DodgeSuccessEvent;
import yesman.epicfight.world.entity.eventlistener.DealDamageEvent.Attack;
import yesman.epicfight.world.entity.eventlistener.TakeDamageEvent.Damage;

public class ConcentrationManager {
   public static final float CONC_MAX = 10000.0F;
   public static final float CONC_TIER_1 = 4000.0F;
   public static final float CONC_TIER_2 = 8000.0F;
   public static final float CONC_DODGE_CAST = -400.0F;
   public static final float CONC_PERFECT_DODGE_GAIN = 2000.0F;
   public static final float CONC_GP_SUCCESS_GAIN = 1500.0F;
   public static final float CONC_PERFECT_JC_GAIN = 600.0F;
   public static final float CONC_MISS_PENALTY = 250.0F;
   public static final SoundEvent[] TIER_SOUNDS = new SoundEvent[]{SoundEvents.f_11738_, SoundEvents.f_11736_, (SoundEvent)SoundEvents.f_12377_.get()};
   private static final float CONC_DIST = 12.0F;
   private static final float CONC_REGEN_MAX_RATE = 25.0F;
   private static final float[] CONC_REGEN_TIER_MULT = new float[]{1.0F, 0.8F, 0.6F};
   private static final float CONC_SPRINT_PENALTY = 35.0F;
   private static final float CONC_HIT_DEFAULT_GAIN = 250.0F;
   private static final float PROVOCATION_REGEN_MULT = 1.5F;
   private static final Map<StunType, Float> STUN_CONC_PENALTY = new HashMap<>();
   private static final Map<UUID, Long> REGEN_BLOCK_UNTIL = new HashMap<>();
   private static final long REGEN_BLOCK_TICKS = 20L;
   private static final Set<UUID> ADMIN_LOCKED = new HashSet<>();
   private static final Set<UUID> SUPER_YAMATO_LOCKED = new HashSet<>();

   public static float getHitGain(StaticAnimation anim) {
      float gain = YamatoAttackAnimation.getConcentrationHitGain(anim);
      return gain > 0.0F ? gain : 250.0F;
   }

   public static float getMissPenalty(StaticAnimation anim) {
      Float p = YamatoAttackAnimation.getConcentrationMissPenalty(anim);
      return p != null ? p : 250.0F;
   }

   private static void markRegenBlocked(SkillContainer c) {
      if (!c.getExecutor().isLogicalClient()) {
         REGEN_BLOCK_UNTIL.put(
            ((Player)c.getExecutor().getOriginal()).m_20148_(), ((ServerPlayer)c.getServerExecutor().getOriginal()).m_284548_().m_46467_() + 20L
         );
      }
   }

   private static boolean isFacingAway(LivingEntity player, LivingEntity target) {
      Vec3 toTarget = target.m_20182_().m_82546_(player.m_20182_()).m_82541_();
      return player.m_20154_().m_82526_(toTarget) < 0.0;
   }

   public static void setAdminLock(UUID playerId, boolean locked) {
      if (locked) {
         ADMIN_LOCKED.add(playerId);
      } else {
         ADMIN_LOCKED.remove(playerId);
      }
   }

   public static void setSuperYamatoLock(UUID playerId, boolean locked) {
      if (locked) {
         SUPER_YAMATO_LOCKED.add(playerId);
      } else {
         SUPER_YAMATO_LOCKED.remove(playerId);
      }
   }

   private static boolean isAdminLocked(SkillContainer c) {
      UUID playerId = ((Player)c.getExecutor().getOriginal()).m_20148_();
      return ADMIN_LOCKED.contains(playerId) || SUPER_YAMATO_LOCKED.contains(playerId);
   }

   private static boolean isRegenBlocked(SkillContainer c) {
      Long until = REGEN_BLOCK_UNTIL.get(((Player)c.getExecutor().getOriginal()).m_20148_());
      if (until == null) {
         return false;
      } else if (((ServerPlayer)c.getServerExecutor().getOriginal()).m_284548_().m_46467_() >= until) {
         REGEN_BLOCK_UNTIL.remove(((Player)c.getExecutor().getOriginal()).m_20148_());
         return false;
      } else {
         return true;
      }
   }

   public static float getConcentration(SkillContainer c) {
      if (c == null) {
         return 0.0F;
      } else if (!c.getExecutor().isLogicalClient()) {
         return YamatoPlayerStateProvider.get((Player)c.getExecutor().getOriginal()).getConcentration();
      } else {
         SkillDataKey<Float> key = (SkillDataKey<Float>)DMCSkillDataKeys.CONCENTRATION.get();
         return c.getDataManager().hasData(key)
            ? (Float)c.getDataManager().getDataValue(key)
            : YamatoPlayerStateProvider.get((Player)c.getExecutor().getOriginal()).getConcentration();
      }
   }

   public static int getConcentrationTier(SkillContainer c) {
      float v = getConcentration(c);
      if (v >= 8000.0F) {
         return 2;
      } else {
         return v >= 4000.0F ? 1 : 0;
      }
   }

   public static void addConcentration(SkillContainer c, float delta) {
      if (c != null) {
         if (!isAdminLocked(c)) {
            if (!isConcentrationLocked(c)) {
               setConcentrationRaw(c, getConcentration(c) + delta);
               if (delta < 0.0F) {
                  markRegenBlocked(c);
               }
            }
         }
      }
   }

   public static boolean hasConcentrationSkill(Player player) {
      if (player == null) {
         return false;
      } else {
         PlayerPatch patch = (PlayerPatch)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
         if (patch == null) {
            return false;
         } else {
            SkillContainer container = patch.getSkill(SkillSlots.WEAPON_INNATE);
            return container != null && !container.isEmpty() && container.getSkill() instanceof AbstractDmcInnateSkill;
         }
      }
   }

   public static int getConcentrationTierForPlayer(Player player) {
      if (!hasConcentrationSkill(player)) {
         return -1;
      } else {
         PlayerPatch patch = (PlayerPatch)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
         SkillContainer container = null;
         if (patch != null) {
            container = patch.getSkill(SkillSlots.WEAPON_INNATE);
         }

         return getConcentrationTier(container);
      }
   }

   public static int getConcentrationTierForEntity(LivingEntity entity) {
      return entity instanceof Player player ? getConcentrationTierForPlayer(player) : -1;
   }

   public static void setConcentrationRaw(SkillContainer c, float v) {
      if (c != null) {
         float clamped = Math.max(0.0F, Math.min(v, 10000.0F));
         Player player = (Player)c.getExecutor().getOriginal();
         YamatoPlayerStateProvider.get(player).setConcentration(clamped);
         if (c.getDataManager().hasData((SkillDataKey)DMCSkillDataKeys.CONCENTRATION.get())) {
            c.getDataManager().setDataSync((SkillDataKey)DMCSkillDataKeys.CONCENTRATION.get(), clamped);
         }
      }
   }

   private static boolean isPlayingProvocation(ServerPlayerPatch spp) {
      try {
         AssetAccessor<? extends StaticAnimation> realAnim = DMCAnimationUtils.getRealAnimationAccessor(spp);
         return realAnim == null
            ? false
            : DMCAnimationUtils.sameAccessor(realAnim, YamatoAnimations.YAMATO_PROVOCATION_A)
               || DMCAnimationUtils.sameAccessor(realAnim, YamatoAnimations.YAMATO_PROVOCATION_A_AERIAL)
               || DMCAnimationUtils.sameAccessor(realAnim, YamatoAnimations.YAMATO_PROVOCATION_B)
               || DMCAnimationUtils.sameAccessor(realAnim, YamatoAnimations.YAMATO_PROVOCATION_B_AERIAL)
               || DMCAnimationUtils.sameAccessor(realAnim, YamatoAnimations.YAMATO_PROVOCATION_C)
               || DMCAnimationUtils.sameAccessor(realAnim, YamatoAnimations.YAMATO_PROVOCATION_D)
               || DMCAnimationUtils.sameAccessor(realAnim, YamatoAnimations.YAMATO_PROVOCATION_SPINE_BLADE)
               || DMCAnimationUtils.sameAccessor(realAnim, YamatoAnimations.YAMATO_PROVOCATION_PORTAL);
      } catch (Exception var2) {
         return false;
      }
   }

   public static boolean isConcentrationLocked(SkillContainer container) {
      ServerPlayerPatch spp = container.getServerExecutor();
      if (spp == null) {
         return true;
      } else {
         LivingEntity target = resolveTarget(spp);
         return target == null || ((ServerPlayer)spp.getOriginal()).m_20270_(target) > 12.0F || !((ServerPlayer)spp.getOriginal()).m_142582_(target);
      }
   }

   public static boolean isConcentrationAvailable(SkillContainer container) {
      return !isConcentrationLocked(container);
   }

   private static boolean isInvalidConcentrationTarget(@Nullable LivingEntity e) {
      return e instanceof DMCSummonedSwordEntity || e instanceof DoppelgangerEntity || e instanceof JudgementCutEntity;
   }

   public static List<LivingEntity> findAvailableCombatTargets(ServerPlayerPatch spp, AABB searchArea) {
      ServerPlayer serverPlayer = (ServerPlayer)spp.getOriginal();
      LivingEntity lastHurt = serverPlayer.m_21214_();
      LivingEntity lastAttacker = serverPlayer.m_21188_();
      List<LivingEntity> targets = new ArrayList<>(
         serverPlayer.m_9236_()
            .m_6443_(
               Mob.class,
               searchArea,
               mob -> mob.m_6084_()
                     && !mob.m_7307_(serverPlayer)
                     && !isInvalidConcentrationTarget(mob)
                     && (mob instanceof Enemy || mob instanceof DummyEntity || mob.m_5448_() != null || mob == lastHurt || mob == lastAttacker)
            )
      );
      targets.sort(Comparator.comparingDouble(serverPlayer::m_20280_));
      return targets;
   }

   @Nullable
   public static LivingEntity resolveTarget(ServerPlayerPatch spp) {
      LivingEntity target = spp.getTarget();
      ServerPlayer serverPlayer = (ServerPlayer)spp.getOriginal();
      if (!(target instanceof DummyEntity) && target instanceof Mob mob && !serverPlayer.m_7500_() && mob.m_5448_() != spp.getOriginal()) {
         return null;
      }

      if (target != null && target.m_6084_() && !isInvalidConcentrationTarget(target) && ((ServerPlayer)spp.getOriginal()).m_20270_(target) <= 12.0F) {
         return target;
      } else {
         AABB aabb = serverPlayer.m_20191_().m_82400_(12.0);

         for (Mob entity : serverPlayer.m_9236_()
            .m_6443_(Mob.class, aabb, m -> m.m_6084_() && !m.m_7307_(serverPlayer) && !isInvalidConcentrationTarget(m) && m.m_5448_() != null)) {
            if (entity.m_20270_(serverPlayer) <= 12.0F) {
               return entity;
            }
         }

         LivingEntity lastHurt = serverPlayer.m_21214_();
         if (lastHurt != null && lastHurt.m_6084_() && !isInvalidConcentrationTarget(lastHurt) && lastHurt.m_20270_(serverPlayer) <= 12.0F) {
            return lastHurt;
         } else {
            LivingEntity lastAttacker = serverPlayer.m_21188_();
            return lastAttacker != null
                  && lastAttacker.m_6084_()
                  && !isInvalidConcentrationTarget(lastAttacker)
                  && lastAttacker.m_20270_(serverPlayer) <= 12.0F
               ? lastAttacker
               : null;
         }
      }
   }

   public void tickSync(SkillContainer container) {
      if (!container.getExecutor().isLogicalClient()) {
         float current = getConcentration(container);
         if (container.getDataManager().hasData((SkillDataKey)DMCSkillDataKeys.CONCENTRATION.get())) {
            container.getDataManager().setDataSync((SkillDataKey)DMCSkillDataKeys.CONCENTRATION.get(), current);
         }

         this.checkTierSound(container);
      }
   }

   private void checkTierSound(SkillContainer container) {
      LivingEntity entity = (LivingEntity)container.getExecutor().getOriginal();
      int currentTier = getConcentrationTier(container);
      YamatoPlayerState state = YamatoPlayerStateProvider.get((Player)container.getExecutor().getOriginal());
      SkillDataManager dm = container.getDataManager();
      SkillDataKey<Integer> key = (SkillDataKey<Integer>)DMCSkillDataKeys.CONC_LAST_TIER.get();
      int lastTier = state.getConcentrationTier();
      if (currentTier != lastTier) {
         state.setConcentrationTier(currentTier);
         if (dm.hasData(key)) {
            dm.setDataSync(key, currentTier);
         }

         entity.m_9236_()
            .m_6263_(null, entity.m_20185_(), entity.m_20186_(), entity.m_20189_(), TIER_SOUNDS[Math.min(currentTier, 2)], SoundSource.PLAYERS, 0.5F, 1.5F);
      }
   }

   public void tickConcentrationRegen(SkillContainer container) {
      ServerPlayerPatch spp = container.getServerExecutor();
      ServerPlayer serverPlayer = (ServerPlayer)spp.getOriginal();
      LivingEntity target = resolveTarget(spp);
      if (target != null) {
         float dist = serverPlayer.m_20270_(target);
         if (!(dist > 12.0F)) {
            if (serverPlayer.m_142582_(target)) {
               int tier = getConcentrationTier(container);
               if (serverPlayer.m_20142_() && isFacingAway(serverPlayer, target)) {
                  addConcentration(container, -35.0F);
               } else if (!isRegenBlocked(container)) {
                  float closeness = 1.0F - dist / 12.0F;
                  float provMult = isPlayingProvocation(spp) ? 1.5F : 1.0F;
                  float rate = 25.0F * closeness * CONC_REGEN_TIER_MULT[tier] * provMult;
                  addConcentration(container, rate);
               }
            }
         }
      }
   }

   public void onDealDamage(Attack event, SkillContainer container) {
      if (!isExcludedProxyDamage(event.getDamageSource())) {
         LivingEntity target = event.getTarget();
         if (target != null) {
            StaticAnimation anim = DMCAnimationUtils.getRealAnimation(event.getDamageSource().getAnimation());
            addConcentration(container, getHitGain(anim));
         }
      }
   }

   public void onAttackMissed(SkillContainer container, StaticAnimation animation) {
      addConcentration(container, -getMissPenalty(animation));
   }

   public void onTakeDamage(Damage event, SkillContainer container) {
      float stunPenalty = 0.0F;
      if (event.getDamageSource() instanceof EpicFightDamageSource efd) {
         stunPenalty = STUN_CONC_PENALTY.getOrDefault(efd.getStunType(), 200.0F);
      }

      addConcentration(container, -event.getDamage() * 100.0F - stunPenalty);
   }

   public void onDodge(SkillContainer container) {
      addConcentration(container, -400.0F);
   }

   public void onDodgeSuccess(DodgeSuccessEvent event, SkillContainer container) {
      if (!isExcludedProxyDamage(event.getDamageSource())) {
         addConcentration(container, 2000.0F);
      }
   }

   private static boolean isExcludedProxyDamage(DamageSource source) {
      return source.m_269533_(DMCDamageTypeTags.NOT_CHARGE)
         || source.m_269533_(DoppelgangerPatch.DOPPELGANGER_DAMAGE)
         || source.m_269533_(JudgementCutPatch.JUDGEMENT_CUT_DAMAGE)
         || source.m_269533_(DMCSummonedSwordPatch.SUMMONED_SWORD_DAMAGE)
         || source.m_269533_(DMCSummonedSwordPatch.STORM_SWORD_DAMAGE)
         || source.m_269533_(DMCSummonedSwordPatch.SPIRAL_SWORD_DAMAGE);
   }

   public void onGpSuccess(SkillContainer container) {
      addConcentration(container, 1500.0F);
   }

   static {
      STUN_CONC_PENALTY.put(StunType.SHORT, 500.0F);
      STUN_CONC_PENALTY.put(StunType.LONG, 2000.0F);
      STUN_CONC_PENALTY.put(StunType.HOLD, 800.0F);
      STUN_CONC_PENALTY.put(StunType.KNOCKDOWN, 5000.0F);
      STUN_CONC_PENALTY.put(StunType.NEUTRALIZE, 5000.0F);
      STUN_CONC_PENALTY.put(StunType.NONE, 100.0F);
   }
}
