package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.AnnoyingVillagers;
import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.gameasset.AnimsSculkSteve;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.network.ClientboundHerobrinePortalFx;
import com.pla.annoyingvillagers.spawnhandler.HerobrineMobData;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import com.pla.annoyingvillagers.util.HerobrinePortalUtil;
import java.util.EnumSet;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.effect.EpicFightMobEffects;

public class TransporterHerobrineCloneEntity extends HerobrineMob {
   private static final int MAX_COMBAT_LOW_CLONE_SUPPORT = 3;
   private static final float TRANSPORTER_FRAGMENT_DROP_CHANCE = 0.1F;
   private static final float FISHING_HOOK_ESCAPE_CANCEL_CHANCE = 0.3F;
   private static final double SECOND_FORM_SUPPORT_SEARCH_RADIUS_SQR = 2304.0;
   private static final EntityDataAccessor<Boolean> HOOKED = SynchedEntityData.m_135353_(TransporterHerobrineCloneEntity.class, EntityDataSerializers.f_135035_);
   private int escapeTiming = -1;
   private int escapeRetryCooldown = 0;
   private int supportAvoidRepathCooldown = 0;
   private int lowCloneSupportCooldown = 0;
   private int portalPairCooldown = 0;
   private int rangedCounterPortalCooldown = 0;
   private int supportEscapePortalCooldown = 0;
   private int portalEscapeStepBackCooldown = 0;
   private boolean fishingHookCancelledEscape = false;
   private boolean hookedWaitingForGround = false;
   private boolean hookedLeftGround = false;
   private final Entity[] combatLowCloneSupport = new Entity[3];
   private final UUID[] combatLowCloneSupportUUIDs = new UUID[3];

   public TransporterHerobrineCloneEntity(SpawnEntity spawnEntity, Level level) {
      this((EntityType<TransporterHerobrineCloneEntity>)AnnoyingVillagersModEntities.TRANSPORTER_HEROBRINE_CLONE.get(), level);
   }

   public TransporterHerobrineCloneEntity(EntityType<TransporterHerobrineCloneEntity> entityType, Level level) {
      super(entityType, level);
      this.m_274367_(2.0F);
      this.f_21364_ = 120;
      this.m_21557_(false);
      this.m_21530_();
      this.m_20340_(false);
      this.setChatName(this.m_5446_().getString());
      this.m_8061_(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)AnnoyingVillagersModItems.TRANSPORTER_FRAGMENT.get()));
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(HOOKED, false);
   }

   public boolean isHooked() {
      return (Boolean)this.f_19804_.m_135370_(HOOKED);
   }

   private void setHooked(boolean hooked) {
      this.f_19804_.m_135381_(HOOKED, hooked);
      if (!hooked) {
         this.hookedWaitingForGround = false;
         this.hookedLeftGround = false;
      }

      if (hooked && !this.m_9236_().m_5776_() && !this.hookedWaitingForGround) {
         this.enforceHookedNoAiLock();
      }
   }

   private void releaseHookedPhysicsUntilGround() {
      this.hookedWaitingForGround = true;
      this.hookedLeftGround = this.hookedLeftGround || !this.m_20096_();
      this.f_19794_ = false;
      this.m_20242_(false);
      this.m_21557_(false);
      this.m_20331_(false);
      this.m_21573_().m_26573_();
   }

   private void tickHookedGroundRelock() {
      if (!this.hookedWaitingForGround) {
         this.enforceHookedNoAiLock();
      } else {
         this.f_19794_ = false;
         this.m_20242_(false);
         this.m_20331_(false);
         if (!this.m_20096_()) {
            this.hookedLeftGround = true;
         }

         if (this.hookedLeftGround && this.m_20096_()) {
            this.hookedWaitingForGround = false;
            this.hookedLeftGround = false;
            this.enforceHookedNoAiLock();
         }
      }
   }

   private void enforceHookedNoAiLock() {
      this.m_21557_(true);
      this.m_21573_().m_26573_();
      this.m_6858_(false);
      this.m_6710_(null);
      this.f_20900_ = 0.0F;
      this.f_20901_ = 0.0F;
      this.f_20902_ = 0.0F;
      Vec3 deltaMovement = this.m_20184_();
      this.m_20334_(0.0, deltaMovement.f_82480_, 0.0);
   }

   private void enforceTransporterHealthCap() {
      AttributeInstance maxHealth = this.m_21051_(Attributes.f_22276_);
      if (maxHealth != null && maxHealth.m_22115_() != (double)this.m_21233_()) {
         maxHealth.m_22100_((double)this.m_21233_());
      }

      AttributeInstance armor = this.m_21051_(Attributes.f_22284_);
      if (armor != null && armor.m_22115_() != 0.0) {
         armor.m_22100_(0.0);
      }

      if (this.m_21223_() > this.m_21233_()) {
         this.m_21153_(this.m_21233_());
      }
   }

   private int randomCooldownSeconds(int minSeconds, int maxSeconds) {
      return minSeconds * 20 + this.m_217043_().m_188503_((maxSeconds - minSeconds) * 20 + 1);
   }

   public int getLowCloneSupportCooldown() {
      return this.lowCloneSupportCooldown;
   }

   public void setLowCloneSupportCooldown() {
      this.lowCloneSupportCooldown = this.randomCooldownSeconds(90, 180);
   }

   public int getPortalPairCooldown() {
      return this.portalPairCooldown;
   }

   public void setPortalPairCooldown() {
      this.portalPairCooldown = this.randomCooldownSeconds(30, 60);
   }

   public int getRangedCounterPortalCooldown() {
      return this.rangedCounterPortalCooldown;
   }

   public void setRangedCounterPortalCooldown() {
      this.rangedCounterPortalCooldown = this.randomCooldownSeconds(30, 60);
   }

   public int getSupportEscapePortalCooldown() {
      return this.supportEscapePortalCooldown;
   }

   public void setSupportEscapePortalCooldown() {
      this.supportEscapePortalCooldown = this.randomCooldownSeconds(30, 60);
   }

   public int getPortalEscapeStepBackCooldown() {
      return this.portalEscapeStepBackCooldown;
   }

   public void setPortalEscapeStepBackCooldown() {
      this.portalEscapeStepBackCooldown = this.randomCooldownSeconds(30, 60);
   }

   private void tickCombatActionCooldowns() {
      if (this.lowCloneSupportCooldown > 0) {
         this.lowCloneSupportCooldown--;
      }

      if (this.portalPairCooldown > 0) {
         this.portalPairCooldown--;
      }

      if (this.rangedCounterPortalCooldown > 0) {
         this.rangedCounterPortalCooldown--;
      }

      if (this.supportEscapePortalCooldown > 0) {
         this.supportEscapePortalCooldown--;
      }

      if (this.portalEscapeStepBackCooldown > 0) {
         this.portalEscapeStepBackCooldown--;
      }
   }

   @Nullable
   @Override
   public SoundEvent getAttackVoiceSound() {
      return (SoundEvent)AnnoyingVillagersModSounds.HEROBRINE_CLONE_SAY.get();
   }

   @Nullable
   @Override
   public SoundEvent getHurtVoiceSound() {
      return (SoundEvent)AnnoyingVillagersModSounds.HEROBRINE_CLONE_SAY_ON_HURT.get();
   }

   @Override
   public float applyBurstProtection(LivingEntity self, DamageSource source, float damage) {
      return damage;
   }

   @Override
   protected void m_8099_() {
      super.m_8099_();
      this.f_21345_.m_25352_(2, new TransporterHerobrineCloneEntity.SafeCombatPositionGoal());
   }

   @Override
   public void m_8119_() {
      if (!this.m_9236_().m_5776_() && this.isHooked() && !this.hookedWaitingForGround) {
         this.enforceHookedNoAiLock();
      }

      super.m_8119_();
      if (!this.m_9236_().m_5776_()) {
         this.enforceTransporterHealthCap();
         this.tickCombatLowCloneSupportSlots();
         this.tickCombatActionCooldowns();
         if (this.isHooked()) {
            this.tickHookedGroundRelock();
         } else {
            if (this.escapeRetryCooldown > 0) {
               this.escapeRetryCooldown--;
            }

            if (this.supportAvoidRepathCooldown > 0) {
               this.supportAvoidRepathCooldown--;
            }

            if (this.shouldStartLegacyEscape()) {
               this.startLegacyEscape();
            }

            this.tickEscape();
            if (this.escapeTiming >= 0) {
               this.m_7292_(new MobEffectInstance((MobEffect)EpicFightMobEffects.STUN_IMMUNITY.get(), 1, 3, false, false));
            }
         }
      }
   }

   private boolean shouldStartLegacyEscape() {
      return this.escapeTiming < 0 && !this.m_21525_() && this.escapeRetryCooldown <= 0 && (double)this.m_21223_() <= (double)this.m_21233_() * 0.1;
   }

   private void startLegacyEscape() {
      this.escapeRetryCooldown = 80;
      this.startEscape();
   }

   public boolean canUseSupportPortalAction() {
      return this.escapeTiming < 0 && !this.m_21525_();
   }

   public boolean canSummonLowCloneSupport() {
      return this.canUseSupportPortalAction() && this.m_20096_() && this.lowCloneSupportCooldown <= 0 && this.hasAvailableCombatLowCloneSupportSlot();
   }

   public boolean isSupportingSecondFormCaster(LivingEntity support) {
      return support instanceof HerobrineMob && support.m_6084_() && this.m_20280_(support) <= 2304.0;
   }

   public void playSecondFormSupportCast(LivingEntity support) {
      if (support != null && support.m_6084_()) {
         this.m_21563_().m_24960_(support, 30.0F, 30.0F);
      }

      this.playSecondFormSupportCastAnimation();
   }

   public boolean canFishingHookCancelEscape() {
      return this.escapeTiming >= 0 || this.getPersistentData().m_128471_("sinking");
   }

   public boolean tryFishingHookCancelEscape() {
      if (!this.canFishingHookCancelEscape()) {
         return false;
      } else if (this.m_217043_().m_188501_() >= 0.3F) {
         return false;
      } else {
         this.escapeTiming = -1;
         this.escapeRetryCooldown = 80;
         this.fishingHookCancelledEscape = true;
         this.hookedWaitingForGround = true;
         this.hookedLeftGround = !this.m_20096_();
         this.setHooked(true);
         HerobrinePortalUtil.cancelSinkTransition(this);
         EpicfightUtil.cancel(this, AnimsSculkSteve.PORTAL_SUMMON);
         this.releaseHookedPhysicsUntilGround();
         this.m_21573_().m_26573_();
         return true;
      }
   }

   public void triggerRangedCounterRetreat(@Nullable LivingEntity threat) {
      if (threat != null && threat.m_6084_()) {
         this.m_21563_().m_24960_(threat, 30.0F, 30.0F);
         this.m_6710_(null);
         this.supportAvoidRepathCooldown = 0;
         Vec3 retreatPos = this.findSupportRetreatPosition(threat);
         if (retreatPos != null) {
            this.m_21573_().m_26519_(retreatPos.f_82479_, retreatPos.f_82480_, retreatPos.f_82481_, 1.15);
            this.supportAvoidRepathCooldown = 15;
         }
      }
   }

   @Nullable
   private Vec3 findSupportRetreatPosition(LivingEntity threat) {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         double awayAngle = Math.atan2(this.m_20189_() - threat.m_20189_(), this.m_20185_() - threat.m_20185_());
         if (Double.isNaN(awayAngle)) {
            awayAngle = this.m_217043_().m_188500_() * Math.PI * 2.0;
         }

         for (int attempt = 0; attempt < 10; attempt++) {
            double angle = awayAngle + (this.m_217043_().m_188500_() - 0.5) * 1.4;
            double distance = 12.0 + this.m_217043_().m_188500_() * 8.0;
            double x = this.m_20185_() + Math.cos(angle) * distance;
            double z = this.m_20189_() + Math.sin(angle) * distance;
            BlockPos surface = serverLevel.m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, BlockPos.m_274561_(x, this.m_20186_(), z));
            if (serverLevel.m_46749_(surface)
               && serverLevel.m_6857_().m_61937_(surface)
               && serverLevel.m_46859_(surface)
               && serverLevel.m_46859_(surface.m_7494_())
               && !serverLevel.m_46859_(surface.m_7495_())) {
               Vec3 candidate = new Vec3((double)surface.m_123341_() + 0.5, (double)surface.m_123342_(), (double)surface.m_123343_() + 0.5);
               if (!(candidate.m_82557_(threat.m_20182_()) <= this.m_20182_().m_82557_(threat.m_20182_()))
                  && serverLevel.m_45756_(this, this.m_20191_().m_82383_(candidate.m_82546_(this.m_20182_())).m_82406_(1.0E-4))) {
                  return candidate;
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private boolean canHoldSafeCombatPosition() {
      LivingEntity target = this.m_5448_();
      return target != null && target.m_6084_() && this.escapeTiming < 0 && !this.m_21525_();
   }

   private void tickEscape() {
      if (this.escapeTiming > 0) {
         this.escapeTiming--;
      }

      if (this.escapeTiming == 60) {
         this.playEscapeEffect();
      }

      if (this.escapeTiming == 40 && this.m_9236_() instanceof ServerLevel serverLevel) {
         HerobrinePortalUtil.sinkIntoGround(serverLevel, this, 0.06);
      }

      if (this.escapeTiming == 1) {
         this.m_146870_();
      }
   }

   private void startEscape() {
      this.escapeTiming = 70;
      this.m_21557_(true);
      this.playEscapeEffect();
   }

   private void playEscapeEffect() {
      this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.PORTAL_NATURAL.get(), 1.0F, 1.0F);
      LivingEntityPatch<?> patch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(this, LivingEntityPatch.class);
      if (patch != null) {
         patch.playAnimationSynchronized(AnimsSculkSteve.PORTAL_SUMMON, 0.0F);
      }

      if (this.m_9236_() instanceof ServerLevel) {
         AnnoyingVillagers.PACKET_HANDLER
            .send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new ClientboundHerobrinePortalFx(this.m_20182_().m_82520_(0.0, 0.0, 0.0)));
      }
   }

   private void cancelEscapeAndDropFragment() {
      this.escapeTiming = -1;
      HerobrinePortalUtil.cancelSinkTransition(this);
      this.m_19983_(new ItemStack((ItemLike)AnnoyingVillagersModItems.TRANSPORTER_FRAGMENT.get()));
   }

   private void tickCombatLowCloneSupportSlots() {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         for (int var5 = 0; var5 < 3; var5++) {
            Entity tracked = this.combatLowCloneSupport[var5];
            UUID trackedUuid = this.combatLowCloneSupportUUIDs[var5];
            if (tracked == null && trackedUuid != null) {
               tracked = serverLevel.m_8791_(trackedUuid);
               if (!isTrackedCombatLowClone(tracked)) {
                  this.clearCombatLowCloneSupportSlot(var5);
                  continue;
               }

               this.combatLowCloneSupport[var5] = tracked;
            }

            if (tracked != null && (!tracked.m_6084_() || tracked.m_213877_())) {
               this.clearCombatLowCloneSupportSlot(var5);
            }
         }
      }
   }

   private static boolean isTrackedCombatLowClone(@Nullable Entity entity) {
      return entity != null && entity.m_6084_() && (entity instanceof LowHerobrineCloneEntity || entity instanceof LowShadowHerobrineCloneEntity);
   }

   private void clearCombatLowCloneSupportSlot(int index) {
      this.combatLowCloneSupport[index] = null;
      this.combatLowCloneSupportUUIDs[index] = null;
   }

   private int getAvailableCombatLowCloneSupportSlot() {
      for (int i = 0; i < 3; i++) {
         if (this.combatLowCloneSupportUUIDs[i] == null) {
            return i;
         }
      }

      return -1;
   }

   public int getAvailableCombatLowCloneSupportSlotCount() {
      int count = 0;

      for (UUID uuid : this.combatLowCloneSupportUUIDs) {
         if (uuid == null) {
            count++;
         }
      }

      return count;
   }

   public boolean hasAvailableCombatLowCloneSupportSlot() {
      return this.getAvailableCombatLowCloneSupportSlot() >= 0;
   }

   public boolean claimCombatLowCloneSupportSlot(Entity clone) {
      int slot = this.getAvailableCombatLowCloneSupportSlot();
      if (slot < 0) {
         return false;
      } else {
         this.combatLowCloneSupport[slot] = clone;
         this.combatLowCloneSupportUUIDs[slot] = clone.m_20148_();
         return true;
      }
   }

   private boolean shouldStayNoAiLocked() {
      return this.escapeTiming >= 0 || this.isHooked() && !this.hookedWaitingForGround;
   }

   private void playSecondFormSupportCastAnimation() {
      LivingEntityPatch<?> patch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(this, LivingEntityPatch.class);
      if (patch != null && !this.m_9236_().m_5776_()) {
         patch.playAnimationSynchronized(AnimsSculkSteve.PORTAL_SUMMON, 0.0F);
      }
   }

   @Override
   public boolean m_6469_(@NotNull DamageSource damageSource, float amount) {
      if (damageSource.m_276093_(DamageTypes.f_268724_)) {
         return super.m_6469_(damageSource, amount);
      } else if (damageSource.m_276093_(DamageTypes.f_268671_)) {
         return false;
      } else if (damageSource.m_276093_(DamageTypes.f_268585_)) {
         return false;
      } else if (damageSource.m_276093_(DamageTypes.f_268493_)) {
         return false;
      } else if (damageSource.m_276093_(DamageTypes.f_268722_)) {
         return false;
      } else if (damageSource.m_276093_(DamageTypes.f_268641_)) {
         return false;
      } else {
         return damageSource.m_276093_(DamageTypes.f_268482_) ? false : super.m_6469_(damageSource, 1.0F);
      }
   }

   public void m_6667_(@NotNull DamageSource damageSource) {
      this.hookedWaitingForGround = false;
      this.hookedLeftGround = false;
      this.setHooked(false);
      this.m_21557_(false);
      this.m_20331_(false);
      super.m_6667_(damageSource);
   }

   @Override
   protected void m_7472_(@NotNull DamageSource damageSource, int looting, boolean recentlyHit) {
      super.m_7472_(damageSource, looting, recentlyHit);
      if (this.escapeTiming < 0 && !this.fishingHookCancelledEscape) {
         if (this.m_217043_().m_188501_() < 0.1F) {
            this.m_19983_(new ItemStack((ItemLike)AnnoyingVillagersModItems.TRANSPORTER_FRAGMENT.get()));
         }
      } else {
         this.m_19983_(new ItemStack((ItemLike)AnnoyingVillagersModItems.TRANSPORTER_FRAGMENT.get()));
      }
   }

   @Override
   public void m_7378_(@NotNull CompoundTag compoundTag) {
      super.m_7378_(compoundTag);
      this.escapeTiming = compoundTag.m_128441_("TransporterEscapeTiming") ? compoundTag.m_128451_("TransporterEscapeTiming") : -1;
      this.escapeRetryCooldown = compoundTag.m_128441_("TransporterEscapeRetryCooldown") ? compoundTag.m_128451_("TransporterEscapeRetryCooldown") : 0;
      this.supportAvoidRepathCooldown = compoundTag.m_128441_("SupportAvoidRepathCooldown") ? compoundTag.m_128451_("SupportAvoidRepathCooldown") : 0;
      this.fishingHookCancelledEscape = compoundTag.m_128471_("FishingHookCancelledEscape");
      this.lowCloneSupportCooldown = compoundTag.m_128451_("LowCloneSupportCooldown");
      this.portalPairCooldown = compoundTag.m_128451_("PortalPairCooldown");
      this.rangedCounterPortalCooldown = compoundTag.m_128451_("RangedCounterPortalCooldown");
      this.supportEscapePortalCooldown = compoundTag.m_128451_("SupportEscapePortalCooldown");
      this.portalEscapeStepBackCooldown = compoundTag.m_128451_("PortalEscapeStepBackCooldown");
      this.hookedWaitingForGround = compoundTag.m_128471_("HookedWaitingForGround");
      this.hookedLeftGround = compoundTag.m_128471_("HookedLeftGround");
      this.setHooked(compoundTag.m_128471_("Hooked"));
      if (this.isHooked() && this.hookedWaitingForGround) {
         this.releaseHookedPhysicsUntilGround();
      }

      this.m_21557_(this.shouldStayNoAiLocked());

      for (int i = 0; i < 3; i++) {
         String key = "CombatLowCloneSupportUUID" + i;
         if (compoundTag.m_128403_(key)) {
            this.combatLowCloneSupportUUIDs[i] = compoundTag.m_128342_(key);
         }
      }

      this.enforceTransporterHealthCap();
   }

   @Override
   public void m_7380_(@NotNull CompoundTag compoundTag) {
      super.m_7380_(compoundTag);
      compoundTag.m_128405_("TransporterEscapeTiming", this.escapeTiming);
      compoundTag.m_128405_("TransporterEscapeRetryCooldown", this.escapeRetryCooldown);
      compoundTag.m_128405_("SupportAvoidRepathCooldown", this.supportAvoidRepathCooldown);
      compoundTag.m_128379_("FishingHookCancelledEscape", this.fishingHookCancelledEscape);
      compoundTag.m_128405_("LowCloneSupportCooldown", this.lowCloneSupportCooldown);
      compoundTag.m_128405_("PortalPairCooldown", this.portalPairCooldown);
      compoundTag.m_128405_("RangedCounterPortalCooldown", this.rangedCounterPortalCooldown);
      compoundTag.m_128405_("SupportEscapePortalCooldown", this.supportEscapePortalCooldown);
      compoundTag.m_128405_("PortalEscapeStepBackCooldown", this.portalEscapeStepBackCooldown);
      compoundTag.m_128379_("Hooked", this.isHooked());
      compoundTag.m_128379_("HookedWaitingForGround", this.hookedWaitingForGround);
      compoundTag.m_128379_("HookedLeftGround", this.hookedLeftGround);

      for (int i = 0; i < 3; i++) {
         if (this.combatLowCloneSupportUUIDs[i] != null) {
            compoundTag.m_128362_("CombatLowCloneSupportUUID" + i, this.combatLowCloneSupportUUIDs[i]);
         }
      }
   }

   public static boolean canSpawn(
      EntityType<TransporterHerobrineCloneEntity> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos position, RandomSource random
   ) {
      ServerLevel serverLevel = level.m_6018_();
      int passesDay = (int)(serverLevel.m_46467_() / 24000L);
      if (passesDay != 0 && passesDay % 3 != 0) {
         return false;
      } else if (HerobrineMobData.get(serverLevel).isOccupied(serverLevel)) {
         return false;
      } else {
         return !serverLevel.m_46462_() ? false : Monster.m_219013_(entityType, level, spawnType, position, random);
      }
   }

   public static Builder createAttributes() {
      return Mob.m_21552_()
         .m_22268_(Attributes.f_22276_, 20.0)
         .m_22268_(Attributes.f_22279_, 0.5)
         .m_22268_(Attributes.f_22281_, 0.0)
         .m_22268_(Attributes.f_22277_, 48.0)
         .m_22268_(Attributes.f_22284_, 0.0)
         .m_22268_(Attributes.f_22278_, 1.0);
   }

   private class SafeCombatPositionGoal extends Goal {
      private SafeCombatPositionGoal() {
         this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean m_8036_() {
         return TransporterHerobrineCloneEntity.this.canHoldSafeCombatPosition();
      }

      public boolean m_8045_() {
         return TransporterHerobrineCloneEntity.this.canHoldSafeCombatPosition();
      }

      public boolean m_183429_() {
         return true;
      }

      public void m_8037_() {
         LivingEntity target = TransporterHerobrineCloneEntity.this.m_5448_();
         if (target != null && target.m_6084_()) {
            TransporterHerobrineCloneEntity.this.m_21563_().m_24960_(target, 30.0F, 30.0F);
            double targetDistanceSqr = TransporterHerobrineCloneEntity.this.m_20280_(target);
            if (targetDistanceSqr > 324.0) {
               TransporterHerobrineCloneEntity.this.m_21573_().m_26573_();
            } else if (targetDistanceSqr >= 196.0) {
               TransporterHerobrineCloneEntity.this.m_21573_().m_26573_();
            } else if (TransporterHerobrineCloneEntity.this.supportAvoidRepathCooldown <= 0 && TransporterHerobrineCloneEntity.this.m_21573_().m_26571_()) {
               Vec3 retreatPos = TransporterHerobrineCloneEntity.this.findSupportRetreatPosition(target);
               if (retreatPos == null) {
                  TransporterHerobrineCloneEntity.this.m_21573_().m_26573_();
               } else {
                  TransporterHerobrineCloneEntity.this.m_21573_().m_26519_(retreatPos.f_82479_, retreatPos.f_82480_, retreatPos.f_82481_, 1.15);
                  TransporterHerobrineCloneEntity.this.supportAvoidRepathCooldown = 15;
               }
            }
         } else {
            TransporterHerobrineCloneEntity.this.m_21573_().m_26573_();
         }
      }

      public void m_8041_() {
         TransporterHerobrineCloneEntity.this.m_21573_().m_26573_();
      }
   }
}
