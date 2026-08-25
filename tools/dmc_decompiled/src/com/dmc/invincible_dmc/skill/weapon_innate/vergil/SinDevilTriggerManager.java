package com.dmc.invincible_dmc.skill.weapon_innate.vergil;

import com.dmc.invincible_dmc.api.animation.types.customStun.ICustomStunDamageSource;
import com.dmc.invincible_dmc.api.animation.types.yamato.JudgementCutEndAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoExecutionAnimation;
import com.dmc.invincible_dmc.capability.yamato.YamatoPlayerStateProvider;
import com.dmc.invincible_dmc.client.input.PlayerInputState;
import com.dmc.invincible_dmc.client.sound.DMCSounds;
import com.dmc.invincible_dmc.damagesource.DMCDamageTypeTags;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.entity.judgementcut.JudgementCutEntity;
import com.dmc.invincible_dmc.entity.summonedsword.DMCSummonedSwordEntity;
import com.dmc.invincible_dmc.gameassets.DMCEffects;
import com.dmc.invincible_dmc.gameassets.DMCSkillDataKeys;
import com.dmc.invincible_dmc.gameassets.animations.stun.CustomStunAnimations;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.skill.weapon_innate.AbstractDmcInnateSkill;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.dmc.invincible_dmc.utils.DamageFilterUtils;
import com.dmc.invincible_dmc.utils.yamato.JCEClient;
import com.dmc.invincible_dmc.utils.yamato.JCEServer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;
import yesman.epicfight.world.damagesource.StunType;

public class SinDevilTriggerManager {
   public static final float SDT_MAX = 1000.0F;
   public static final float SDT_CONVERSION_RATE = 110.0F;
   public static final float SDT_DRAIN_PER_TICK = 0.1F;
   public static final int CHARGE_HOLD_TICKS = 30;
   public static final int SDT_SECOND_DELAY_TICKS = 15;
   public static final int SDT_CONFIRM_TICKS = 20;
   public static final int SDT_GRACE_TICKS = 70;
   public static final float SDT_DECAY_PER_SECOND = 51.0F;
   public static final float SDT_DECAY_PER_TICK = 2.55F;
   public static final int PHASE_EMPTY = 0;
   public static final int PHASE_FIRST_CHARGE = 1;
   public static final int PHASE_SECOND_CHARGE = 2;
   public static final int PHASE_READY = 3;
   public static final int PHASE_ACTIVE = 4;
   private static final float SDT_BURST_DAMAGE = 5.0F;
   private static final float SDT_BURST_RADIUS = 4.0F;
   private static final double SDT_SMALL_BURST_HORIZONTAL_RADIUS = 2.0;
   private static final double SDT_SMALL_BURST_VERTICAL_RANGE = 3.0;
   private static final Set<UUID> ADMIN_LOCKED_SDT = new HashSet<>();
   private static final Set<UUID> SUPER_YAMATO_LOCKED_SDT = new HashSet<>();

   private static int getInt(SkillContainer c, RegistryObject<SkillDataKey<Integer>> k) {
      return c.getDataManager().hasData((SkillDataKey)k.get()) ? (Integer)c.getDataManager().getDataValue((SkillDataKey)k.get()) : 0;
   }

   private static boolean getBool(SkillContainer c, RegistryObject<SkillDataKey<Boolean>> k) {
      return c.getDataManager().hasData((SkillDataKey)k.get()) && (Boolean)c.getDataManager().getDataValue((SkillDataKey)k.get());
   }

   private static void setInt(SkillContainer c, RegistryObject<SkillDataKey<Integer>> k, int v) {
      if (c.getDataManager().hasData((SkillDataKey)k.get())) {
         c.getDataManager().setDataSync((SkillDataKey)k.get(), v);
      }
   }

   private static void setBool(SkillContainer c, RegistryObject<SkillDataKey<Boolean>> k, boolean v) {
      if (c.getDataManager().hasData((SkillDataKey)k.get())) {
         c.getDataManager().setDataSync((SkillDataKey)k.get(), v);
      }
   }

   public static boolean isPlayerInSDT(Player player) {
      if (player == null) {
         return false;
      } else if (!player.m_9236_().m_5776_()) {
         return YamatoPlayerStateProvider.get(player).isSdtActive();
      } else {
         PlayerPatch patch = (PlayerPatch)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
         if (patch == null) {
            return false;
         } else {
            SkillContainer container = patch.getSkill(SkillSlots.WEAPON_INNATE);
            return container != null && !container.isEmpty() ? isSDT(container) : false;
         }
      }
   }

   public static boolean isLivingInSDT(LivingEntity entity) {
      return entity instanceof Player player ? isPlayerInSDT(player) : false;
   }

   public static boolean isHarmfulEffect(MobEffectInstance effectInstance) {
      return effectInstance != null && effectInstance.m_19544_().m_19483_() == MobEffectCategory.HARMFUL;
   }

   public static void removeHarmfulEffects(Player player) {
      if (player != null && !player.m_9236_().m_5776_()) {
         for (MobEffectInstance effectInstance : new ArrayList(player.m_21220_())) {
            if (isHarmfulEffect(effectInstance)) {
               player.m_21195_(effectInstance.m_19544_());
            }
         }
      }
   }

   public static float getSDTValue(SkillContainer c) {
      if (c == null) {
         return 0.0F;
      } else if (!c.getExecutor().isLogicalClient()) {
         return YamatoPlayerStateProvider.get((Player)c.getExecutor().getOriginal()).getSdtValue();
      } else {
         SkillDataKey<Float> key = (SkillDataKey<Float>)DMCSkillDataKeys.SDT_VALUE.get();
         return c.getDataManager().hasData(key)
            ? (Float)c.getDataManager().getDataValue(key)
            : YamatoPlayerStateProvider.get((Player)c.getExecutor().getOriginal()).getSdtValue();
      }
   }

   public static void setSDTValueRaw(SkillContainer c, float v) {
      if (c != null) {
         float clamped = Math.max(0.0F, Math.min(v, 1000.0F));
         YamatoPlayerStateProvider.get((Player)c.getExecutor().getOriginal()).setSdtValue(clamped);
         if (c.getDataManager().hasData((SkillDataKey)DMCSkillDataKeys.SDT_VALUE.get())) {
            c.getDataManager().setDataSync((SkillDataKey)DMCSkillDataKeys.SDT_VALUE.get(), clamped);
         }
      }
   }

   public static void setSDTValueRaw(Player player, float v) {
      if (player != null) {
         float clamped = Math.max(0.0F, Math.min(v, 1000.0F));
         YamatoPlayerStateProvider.get(player).setSdtValue(clamped);
         PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
         if (playerPatch != null) {
            SkillContainer container = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
            if (container != null && container.getDataManager().hasData((SkillDataKey)DMCSkillDataKeys.SDT_VALUE.get())) {
               container.getDataManager().setDataSync((SkillDataKey)DMCSkillDataKeys.SDT_VALUE.get(), clamped);
            }
         }
      }
   }

   public static boolean isSDT(SkillContainer c) {
      if (c == null) {
         return false;
      } else if (!c.getExecutor().isLogicalClient()) {
         return YamatoPlayerStateProvider.get((Player)c.getExecutor().getOriginal()).isSdtActive();
      } else {
         SkillDataKey<Boolean> key = (SkillDataKey<Boolean>)DMCSkillDataKeys.IS_SDT.get();
         return c.getDataManager().hasData(key) && (Boolean)c.getDataManager().getDataValue(key);
      }
   }

   public static void setSDT(SkillContainer c, boolean value) {
      YamatoPlayerStateProvider.get((Player)c.getExecutor().getOriginal()).setSdtActive(value);
      if (c.getDataManager().hasData((SkillDataKey)DMCSkillDataKeys.IS_SDT.get())) {
         c.getDataManager().setDataSync((SkillDataKey)DMCSkillDataKeys.IS_SDT.get(), value);
      }
   }

   public static int getPhase(SkillContainer c) {
      if (c == null) {
         return 0;
      } else {
         SkillDataKey<Integer> key = (SkillDataKey<Integer>)DMCSkillDataKeys.SDT_PHASE.get();
         return c.getDataManager().hasData(key) ? (Integer)c.getDataManager().getDataValue(key) : 0;
      }
   }

   private static void setPhase(SkillContainer c, int phase) {
      if (c.getDataManager().hasData((SkillDataKey)DMCSkillDataKeys.SDT_PHASE.get())) {
         c.getDataManager().setDataSync((SkillDataKey)DMCSkillDataKeys.SDT_PHASE.get(), phase);
      }
   }

   public static boolean hasDT(SkillContainer container) {
      int stack = container.getStack();
      float maxR = container.getMaxResource();
      if (maxR <= 0.0F) {
         return false;
      } else {
         float resourceNorm = container.getResource() / maxR;
         return (float)stack + resourceNorm > 0.0F;
      }
   }

   public static void setAdminLock(UUID playerId, boolean locked) {
      if (locked) {
         ADMIN_LOCKED_SDT.add(playerId);
      } else {
         ADMIN_LOCKED_SDT.remove(playerId);
      }
   }

   public static void setSuperYamatoLock(UUID playerId, boolean locked) {
      if (locked) {
         SUPER_YAMATO_LOCKED_SDT.add(playerId);
      } else {
         SUPER_YAMATO_LOCKED_SDT.remove(playerId);
      }
   }

   public static boolean isAdminLocked(UUID playerId) {
      return ADMIN_LOCKED_SDT.contains(playerId) || SUPER_YAMATO_LOCKED_SDT.contains(playerId);
   }

   public static boolean consumeSDT(SkillContainer container, float amount) {
      if (container == null || amount <= 0.0F) {
         return false;
      } else if (isAdminLocked(((Player)container.getExecutor().getOriginal()).m_20148_())) {
         return false;
      } else {
         float current = getSDTValue(container);
         setSDTValueRaw(container, current - amount);
         return true;
      }
   }

   public void toggleSdt(SkillContainer container, Player player) {
      if (isSDT(container)) {
         this.exitSDT(container, player);
      } else {
         this.enterSDT(container, player);
      }
   }

   public void forceEndGrace(SkillContainer container) {
      int counter = getInt(container, DMCSkillDataKeys.SDT_TICK_COUNTER);
      if (counter <= 70) {
         setInt(container, DMCSkillDataKeys.SDT_TICK_COUNTER, 71);
      }
   }

   public void tick(SkillContainer container, AbstractDmcInnateSkill skill) {
      if (!container.getExecutor().isLogicalClient()) {
         ServerPlayerPatch spp = container.getServerExecutor();
         Player player = (Player)spp.getOriginal();
         if (isAdminLocked(player.m_20148_()) && getSDTValue(container) < 1000.0F) {
            setSDTValueRaw(container, 1000.0F);
         }

         boolean charging = PlayerInputState.isRemoteDown(player, 8);
         boolean prev = getBool(container, DMCSkillDataKeys.SDT_PREV_CHARGING);
         boolean inSDT = isSDT(container);
         boolean chargeJustPressed = charging && !prev;
         int prevPhase = getPhase(container);
         setInt(container, DMCSkillDataKeys.SDT_PREV_PHASE, prevPhase);
         if (inSDT) {
            this.tickSDTActive(container, skill, player, chargeJustPressed);
         } else {
            this.tickNormal(container, skill, player, charging);
         }

         int currentPhase = getPhase(container);
         if (prevPhase == 2 && currentPhase != 2 && player instanceof ServerPlayer sp) {
            JCEServer.onSdtCharge2EndServer(sp);
         }

         setBool(container, DMCSkillDataKeys.SDT_PREV_CHARGING, charging);
         this.tickSync(container);
      }
   }

   private static boolean isNotActionAnim(SkillContainer container) {
      return !DMCAnimationUtils.isRealAnimationType(container.getExecutor(), ActionAnimation.class);
   }

   private void onSdtActiveEnter(SkillContainer container, Player player) {
      container.getExecutor().playSound((SoundEvent)DMCSounds.SDT_DONE.get(), 1.35F, 1.0F, 1.0F);
      this.onSdtActiveEnterClient(player);
      if (player instanceof ServerPlayer sp) {
         removeHarmfulEffects(sp);
         sp.m_7292_(new MobEffectInstance((MobEffect)DMCEffects.ENEMY_STEP.get(), 35, 0, false, false));
         JCEServer.onSDTEnterServer(sp);
         JCEServer.onSdtActivatedServer(sp);
         if (isNotActionAnim(container) || container.getExecutor().isStunned()) {
            container.getExecutor().playAnimationSynchronized(YamatoAnimations.YAMATO_SIN_DEVIL_TRIGGER, 0.0F);
         }
      }
   }

   public static void applySdtBurstDamage(LivingEntity livingEntity) {
      AABB box = livingEntity.m_20191_().m_82400_(4.0);
      List<LivingEntity> targets = livingEntity.m_9236_()
         .m_6443_(
            LivingEntity.class,
            box,
            e -> e.m_6084_()
                  && e != livingEntity
                  && !(e instanceof DMCSummonedSwordEntity)
                  && !(e instanceof JudgementCutEntity)
                  && (!(e instanceof DoppelgangerEntity d) || d.getOwner() != livingEntity)
                  && !DamageFilterUtils.shouldSkipTarget(livingEntity, e)
         );
      if (!targets.isEmpty()) {
         EpicFightDamageSource ds = EpicFightDamageSources.mobAttack(livingEntity)
            .setStunType(StunType.HOLD)
            .addRuntimeTag(DMCDamageTypeTags.NOT_CHARGE)
            .setBaseImpact(2.5F);
         ((ICustomStunDamageSource)ds)
            .invincible$setCustomStunAnimations(
               CustomStunAnimations.HIT_UP_2, CustomStunAnimations.HIT_UP_2, CustomStunAnimations.HIT_UP_2, CustomStunAnimations.HIT_UP_2
            );

         for (LivingEntity t : targets) {
            t.m_6469_(ds, 5.0F);
         }
      }
   }

   public static void applySdtBurstDamageSmall(LivingEntity livingEntity) {
      double sourceCenterY = livingEntity.m_20191_().m_82399_().f_82480_;
      AABB box = livingEntity.m_20191_().m_82377_(2.0, 3.0, 2.0);
      List<LivingEntity> targets = livingEntity.m_9236_()
         .m_6443_(
            LivingEntity.class,
            box,
            e -> e.m_6084_()
                  && e != livingEntity
                  && Math.abs(e.m_20191_().m_82399_().f_82480_ - sourceCenterY) <= 3.0
                  && !(e instanceof DMCSummonedSwordEntity)
                  && !(e instanceof JudgementCutEntity)
                  && (!(e instanceof DoppelgangerEntity d) || d.getOwner() != livingEntity)
                  && !DamageFilterUtils.shouldSkipTarget(livingEntity, e)
         );
      if (!targets.isEmpty()) {
         EpicFightDamageSource ds = EpicFightDamageSources.mobAttack(livingEntity)
            .setStunType(StunType.HOLD)
            .addRuntimeTag(DMCDamageTypeTags.NOT_CHARGE)
            .addRuntimeTag(YamatoAnimations.SLOW_PERSISTENT);
         ((ICustomStunDamageSource)ds)
            .invincible$setCustomStunAnimations(
               CustomStunAnimations.HIT_UP_1, CustomStunAnimations.HIT_UP_1, CustomStunAnimations.HIT_UP_1, CustomStunAnimations.HIT_UP_1
            );

         for (LivingEntity t : targets) {
            t.m_6469_(ds, 1.0F);
         }
      }
   }

   public static void applySdtBurstDamageSmallAir(LivingEntity livingEntity) {
      double sourceCenterY = livingEntity.m_20191_().m_82399_().f_82480_;
      AABB box = livingEntity.m_20191_().m_82377_(2.0, 3.0, 2.0);
      List<LivingEntity> targets = livingEntity.m_9236_()
         .m_6443_(
            LivingEntity.class,
            box,
            e -> e.m_6084_()
                  && e != livingEntity
                  && Math.abs(e.m_20191_().m_82399_().f_82480_ - sourceCenterY) <= 3.0
                  && !(e instanceof DMCSummonedSwordEntity)
                  && !(e instanceof JudgementCutEntity)
                  && (!(e instanceof DoppelgangerEntity d) || d.getOwner() != livingEntity)
                  && !DamageFilterUtils.shouldSkipTarget(livingEntity, e)
         );
      if (!targets.isEmpty()) {
         EpicFightDamageSource ds = EpicFightDamageSources.mobAttack(livingEntity)
            .setStunType(StunType.HOLD)
            .addRuntimeTag(DMCDamageTypeTags.NOT_CHARGE)
            .addRuntimeTag(YamatoAnimations.SLOW_PERSISTENT);
         ((ICustomStunDamageSource)ds)
            .invincible$setCustomStunAnimations(
               CustomStunAnimations.HIT_UP_0, CustomStunAnimations.HIT_UP_0, CustomStunAnimations.HIT_UP_0, CustomStunAnimations.HIT_UP_0
            );

         for (LivingEntity t : targets) {
            t.m_6469_(ds, 1.0F);
         }
      }
   }

   private void onSdtActiveExit(SkillContainer container, Player player) {
      container.getExecutor().playSound((SoundEvent)DMCSounds.SDT_OUT.get(), 1.35F, 1.0F, 1.0F);
      this.onSdtActiveExitClient(player);
      if (player instanceof ServerPlayer sp) {
         JCEServer.onSDTExitServer(sp);
      }

      if (isNotActionAnim(container)) {
         container.getExecutor().playAnimationSynchronized(YamatoAnimations.YAMATO_SIN_DEVIL_TRIGGER_BACK, 0.0F);
      }
   }

   private void onPhaseFirstChargeStart(SkillContainer container, Player player) {
      container.getExecutor().playSound((SoundEvent)DMCSounds.SDT1_CHARGE.get(), 1.15F, 1.0F, 1.0F);
   }

   private void onPhaseFirstChargeComplete(SkillContainer container, Player player) {
      container.getExecutor().playSound((SoundEvent)DMCSounds.SDT1_DONE.get(), 1.2F, 1.0F, 1.0F);
      this.onPhaseFirstChargeCompleteClient(player);
      if (player instanceof ServerPlayer sp) {
         JCEServer.onSdtCharge1CompleteServer(sp);
      }
   }

   private void onPhaseFirstChargeTick(SkillContainer container, Player player) {
      this.onPhaseFirstChargeTickClient(player);
      if (player instanceof ServerPlayer sp) {
         JCEServer.onSdtCharge1TickServer(sp);
      }
   }

   private void onPhaseSecondChargeStart(SkillContainer container, Player player) {
      container.getExecutor().playSound((SoundEvent)DMCSounds.SDT2_CHARGE.get(), 1.2F, 1.0F, 1.0F);
      if (player instanceof ServerPlayer sp) {
         JCEServer.onSdtCharge2StartServer(sp);
      }
   }

   private void onPhaseSecondChargeTick(SkillContainer container, Player player) {
      this.onPhaseSecondChargeTickClient(player);
      if (player instanceof ServerPlayer sp) {
         JCEServer.onSdtCharge2TickServer(sp);
      }
   }

   private void onPhaseReadyStart(SkillContainer container, Player player) {
      container.getExecutor().playSound((SoundEvent)DMCSounds.SDT2_DONE.get(), 1.4F, 1.0F, 1.0F);
      container.getExecutor().playSound(SoundEvents.f_215762_, 1.0F, 1.0F, 1.0F);
      this.onPhaseReadyStartClient(player);
      if (player instanceof ServerPlayer sp) {
         JCEServer.onSdtCharge2CompleteServer(sp);
      }
   }

   private void onSdtActiveEnterClient(Player player) {
      if (player.m_9236_().m_5776_()) {
         JCEClient.onSDTEnterClient(player);
         JCEClient.onSdtActivatedClient(player);
      }
   }

   private void onSdtActiveExitClient(Player player) {
      if (player.m_9236_().m_5776_()) {
         JCEClient.onSDTExitClient(player);
      }
   }

   private void onPhaseFirstChargeCompleteClient(Player player) {
      if (player.m_9236_().m_5776_()) {
         JCEClient.onSdtCharge1CompleteClient(player);
      }
   }

   private void onPhaseFirstChargeTickClient(Player player) {
      if (player.m_9236_().m_5776_()) {
         JCEClient.onSdtCharge1TickClient(player);
      }
   }

   private void onPhaseSecondChargeTickClient(Player player) {
      if (player.m_9236_().m_5776_()) {
         JCEClient.onSdtCharge2TickClient(player);
      }
   }

   private void onPhaseReadyStartClient(Player player) {
      if (player.m_9236_().m_5776_()) {
         JCEClient.onSdtCharge2CompleteClient(player);
      }
   }

   private void onSdtActiveTickClient(Player player) {
      if (player.m_9236_().m_5776_()) {
         JCEClient.onSdtActiveTickClient(player);
      }
   }

   private void tickNormal(SkillContainer container, AbstractDmcInnateSkill skill, Player player, boolean charging) {
      float sdtValue = getSDTValue(container);
      if (charging) {
         boolean wasCharging = getBool(container, DMCSkillDataKeys.SDT_PREV_CHARGING);
         if (!wasCharging) {
            this.resetChargeState(container, sdtValue);
         }

         int hold = getInt(container, DMCSkillDataKeys.SDT_CHARGE_HOLD_TIMER) + 1;
         setInt(container, DMCSkillDataKeys.SDT_CHARGE_HOLD_TIMER, hold);
         if (sdtValue >= 1000.0F) {
            this.tickSdtFull(container, skill, player);
         } else if (hold < 30) {
            this.tickHold(container, player, sdtValue);
         } else {
            this.tickFirstCharge(container, skill, player);
         }
      } else {
         boolean wasChargingx = getBool(container, DMCSkillDataKeys.SDT_PREV_CHARGING);
         if (wasChargingx) {
            this.onChargeReleased(container, player);
         }

         this.tickNoCharge(container, player, sdtValue);
      }
   }

   private void resetChargeState(SkillContainer container, float sdtValue) {
      setBool(container, DMCSkillDataKeys.SDT_PHASE1_TICK_ACTIVE, false);
      setInt(container, DMCSkillDataKeys.SDT_CHARGE_HOLD_TIMER, 0);
      setInt(container, DMCSkillDataKeys.SDT_CONFIRM_TIMER, 0);
      setInt(container, DMCSkillDataKeys.SDT_SECOND_DELAY_TIMER, 0);
      setBool(container, DMCSkillDataKeys.SDT_FIRST_CHARGE_FIRED, false);
      setBool(container, DMCSkillDataKeys.SDT_SECOND_CHARGE_FIRED, false);
      setBool(container, DMCSkillDataKeys.SDT_WAS_MAX_AT_START, sdtValue >= 1000.0F);
      setPhase(container, sdtValue > 0.0F ? 1 : 0);
   }

   private void tickHold(SkillContainer container, Player player, float sdtValue) {
      if (sdtValue > 0.0F) {
         setPhase(container, 1);
      }
   }

   private void tickFirstCharge(SkillContainer container, AbstractDmcInnateSkill skill, Player player) {
      if (hasDT(container)) {
         setBool(container, DMCSkillDataKeys.SDT_PHASE1_TICK_ACTIVE, true);
         this.onPhaseFirstChargeTick(container, player);
         int hold = getInt(container, DMCSkillDataKeys.SDT_CHARGE_HOLD_TIMER);
         if (hold == 30) {
            this.onPhaseFirstChargeStart(container, player);
         }

         setPhase(container, 1);
         this.convertDTtoSDT(container, skill);
      } else {
         setBool(container, DMCSkillDataKeys.SDT_PHASE1_TICK_ACTIVE, false);
      }
   }

   private void tickSdtFull(SkillContainer container, AbstractDmcInnateSkill skill, Player player) {
      setBool(container, DMCSkillDataKeys.SDT_PHASE1_TICK_ACTIVE, false);
      boolean firstFired = getBool(container, DMCSkillDataKeys.SDT_FIRST_CHARGE_FIRED);
      boolean wasMax = getBool(container, DMCSkillDataKeys.SDT_WAS_MAX_AT_START);
      if (!firstFired && !wasMax) {
         setBool(container, DMCSkillDataKeys.SDT_FIRST_CHARGE_FIRED, true);
         this.onPhaseFirstChargeComplete(container, player);
      }

      int delay = getInt(container, DMCSkillDataKeys.SDT_SECOND_DELAY_TIMER);
      if (delay < 15) {
         setInt(container, DMCSkillDataKeys.SDT_SECOND_DELAY_TIMER, delay + 1);
         setPhase(container, 1);
      } else {
         int confirm = getInt(container, DMCSkillDataKeys.SDT_CONFIRM_TIMER) + 1;
         setInt(container, DMCSkillDataKeys.SDT_CONFIRM_TIMER, confirm);
         if (confirm >= 20) {
            this.tickReady(container, player);
         } else {
            this.tickSecondCharge(container, player);
         }
      }
   }

   private void tickSecondCharge(SkillContainer container, Player player) {
      int prev = getInt(container, DMCSkillDataKeys.SDT_PREV_PHASE);
      if (prev != 2) {
         this.onPhaseSecondChargeStart(container, player);
      }

      setPhase(container, 2);
      this.onPhaseSecondChargeTick(container, player);
   }

   private void tickReady(SkillContainer container, Player player) {
      int prev = getInt(container, DMCSkillDataKeys.SDT_PREV_PHASE);
      boolean secondFired = getBool(container, DMCSkillDataKeys.SDT_SECOND_CHARGE_FIRED);
      if (prev != 3 && !secondFired) {
         setBool(container, DMCSkillDataKeys.SDT_SECOND_CHARGE_FIRED, true);
         this.onPhaseReadyStart(container, player);
      }

      setPhase(container, 3);
   }

   private void onChargeReleased(SkillContainer container, Player player) {
      int phase = getPhase(container);
      if (phase == 3) {
         this.enterSDT(container, player);
      } else {
         setInt(container, DMCSkillDataKeys.SDT_CONFIRM_TIMER, 0);
         setInt(container, DMCSkillDataKeys.SDT_SECOND_DELAY_TIMER, 0);
      }
   }

   private void tickNoCharge(SkillContainer container, Player player, float sdtValue) {
      setBool(container, DMCSkillDataKeys.SDT_PHASE1_TICK_ACTIVE, false);
      setInt(container, DMCSkillDataKeys.SDT_CHARGE_HOLD_TIMER, 0);
      if (sdtValue >= 1000.0F) {
         setPhase(container, 1);
      } else if (sdtValue > 0.0F) {
         setPhase(container, 1);
         setBool(container, DMCSkillDataKeys.SDT_FIRST_CHARGE_FIRED, false);
      } else {
         setPhase(container, 0);
         setBool(container, DMCSkillDataKeys.SDT_FIRST_CHARGE_FIRED, false);
      }
   }

   private void tickSDTActive(SkillContainer container, AbstractDmcInnateSkill skill, Player player, boolean chargeJustPressed) {
      if (chargeJustPressed) {
         this.exitSDT(container, player);
      } else {
         int counter = getInt(container, DMCSkillDataKeys.SDT_TICK_COUNTER) + 1;
         setInt(container, DMCSkillDataKeys.SDT_TICK_COUNTER, counter);
         if (counter % 5 == 0) {
            this.onSdtActiveTickClient(player);
            if (player instanceof ServerPlayer sp) {
               JCEServer.onSdtActiveTickServer(sp);
            }
         }

         if (counter <= 70) {
            setSDTValueRaw(container, 1000.0F);
            setPhase(container, 4);
         } else {
            PlayerPatch<?> sdtPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
            if (sdtPatch != null) {
               AnimationPlayer animPlayer = DMCAnimationUtils.getMainPlayer(sdtPatch);
               if (animPlayer != null
                  && DMCAnimationUtils.isRealAnimationType(DMCAnimationUtils.getCurrentAnimation(animPlayer), JudgementCutEndAnimation.class)) {
                  setPhase(container, 4);
                  return;
               }

               if (animPlayer != null
                  && DMCAnimationUtils.isRealAnimationType(DMCAnimationUtils.getCurrentAnimation(animPlayer), YamatoExecutionAnimation.class)) {
                  setPhase(container, 4);
                  return;
               }
            }

            if (isAdminLocked(player.m_20148_())) {
               return;
            }

            float current = getSDTValue(container);
            float next = current - 2.55F;
            if (next <= 0.0F) {
               setSDTValueRaw(container, 0.0F);
               this.exitSDT(container, player);
               return;
            }

            if (!player.m_7500_()) {
               setSDTValueRaw(container, next);
            }

            setPhase(container, 4);
         }
      }
   }

   private void enterSDT(SkillContainer container, Player player) {
      setSDT(container, true);
      setPhase(container, 4);
      setInt(container, DMCSkillDataKeys.SDT_TICK_COUNTER, 0);
      setInt(container, DMCSkillDataKeys.SDT_CHARGE_HOLD_TIMER, 0);
      setInt(container, DMCSkillDataKeys.SDT_CONFIRM_TIMER, 0);
      setInt(container, DMCSkillDataKeys.SDT_SECOND_DELAY_TIMER, 0);
      this.onSdtActiveEnter(container, player);
   }

   private void exitSDT(SkillContainer container, Player player) {
      setSDT(container, false);
      setPhase(container, getSDTValue(container) >= 1000.0F ? 1 : 0);
      setInt(container, DMCSkillDataKeys.SDT_TICK_COUNTER, 0);
      setInt(container, DMCSkillDataKeys.SDT_CHARGE_HOLD_TIMER, 0);
      setInt(container, DMCSkillDataKeys.SDT_CONFIRM_TIMER, 0);
      setInt(container, DMCSkillDataKeys.SDT_SECOND_DELAY_TIMER, 0);
      this.onSdtActiveExit(container, player);
   }

   private void convertDTtoSDT(SkillContainer container, AbstractDmcInnateSkill skill) {
      float currentSDT = getSDTValue(container);
      if (!(currentSDT >= 1000.0F)) {
         int stack = container.getStack();
         float maxR = container.getMaxResource();
         if (!(maxR <= 0.0F)) {
            float resourceNorm = container.getResource() / maxR;
            float totalDT = (float)stack + resourceNorm;
            if (!(totalDT <= 0.0F)) {
               float neededForMax = (1000.0F - currentSDT) / 110.0F;
               float toDrain = Math.min(0.1F, Math.min(totalDT, neededForMax));
               float newResourceNorm = resourceNorm - toDrain;

               int newStack;
               for (newStack = stack; newResourceNorm < 0.0F && newStack > 0; newResourceNorm++) {
                  newStack--;
               }

               if (newResourceNorm < 0.0F) {
                  toDrain = totalDT;
                  newResourceNorm = 0.0F;
               }

               skill.setStackSynchronize(container, newStack);
               float newResourceRaw = newResourceNorm * maxR;
               if (newResourceRaw <= 0.0F && newStack > 0) {
                  newResourceRaw = 0.001F;
               }

               skill.setConsumptionSynchronize(container, newResourceRaw);
               float sdtGain = toDrain * 110.0F;
               float newSDT = Math.min(currentSDT + sdtGain, 1000.0F);
               setSDTValueRaw(container, newSDT);
            }
         }
      }
   }

   private void tickSync(SkillContainer container) {
      container.getDataManager().setDataSync((SkillDataKey)DMCSkillDataKeys.SDT_VALUE.get(), getSDTValue(container));
      container.getDataManager().setDataSync((SkillDataKey)DMCSkillDataKeys.IS_SDT.get(), isSDT(container));
      container.getDataManager().setDataSync((SkillDataKey)DMCSkillDataKeys.SDT_PHASE.get(), getPhase(container));
      int confirm = getInt(container, DMCSkillDataKeys.SDT_CONFIRM_TIMER);
      container.getDataManager().setDataSync((SkillDataKey)DMCSkillDataKeys.SDT_CONFIRM_TIMER.get(), confirm);
      syncBoolKey(container, DMCSkillDataKeys.SDT_PHASE1_TICK_ACTIVE);
      syncBoolKey(container, DMCSkillDataKeys.SDT_FIRST_CHARGE_FIRED);
      syncBoolKey(container, DMCSkillDataKeys.SDT_SECOND_CHARGE_FIRED);
      syncBoolKey(container, DMCSkillDataKeys.SDT_PREV_CHARGING);
      syncBoolKey(container, DMCSkillDataKeys.SDT_WAS_MAX_AT_START);
      syncIntKey(container, DMCSkillDataKeys.SDT_SECOND_DELAY_TIMER);
   }

   private static void syncBoolKey(SkillContainer c, RegistryObject<SkillDataKey<Boolean>> k) {
      if (c.getDataManager().hasData((SkillDataKey)k.get())) {
         c.getDataManager().setDataSync((SkillDataKey)k.get(), (Boolean)c.getDataManager().getDataValue((SkillDataKey)k.get()));
      }
   }

   private static void syncIntKey(SkillContainer c, RegistryObject<SkillDataKey<Integer>> k) {
      if (c.getDataManager().hasData((SkillDataKey)k.get())) {
         c.getDataManager().setDataSync((SkillDataKey)k.get(), (Integer)c.getDataManager().getDataValue((SkillDataKey)k.get()));
      }
   }
}
