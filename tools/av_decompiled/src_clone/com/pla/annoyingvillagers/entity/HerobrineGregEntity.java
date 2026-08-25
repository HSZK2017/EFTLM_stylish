package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.AnnoyingVillagers;
import com.pla.annoyingvillagers.blockentity.CryingObsidianBlockEntity;
import com.pla.annoyingvillagers.blockentity.ObsidianBlockEntity;
import com.pla.annoyingvillagers.blockentity.ShadowObsidianBlockEntity;
import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.clazz.HerobrineObsidianBlock;
import com.pla.annoyingvillagers.config.AnnoyingVillagersConfig;
import com.pla.annoyingvillagers.gameasset.AnimsSculkSteve;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModBlocks;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.item.TransporterFragmentItem;
import com.pla.annoyingvillagers.network.ClientboundHerobrinePortalFx;
import com.pla.annoyingvillagers.spawnhandler.GregData;
import com.pla.annoyingvillagers.util.ChatUtil;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import com.pla.annoyingvillagers.util.EscapeUtil;
import com.pla.annoyingvillagers.util.HerobrinePortalCombatUtil;
import com.pla.annoyingvillagers.util.HerobrinePortalUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
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
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import net.minecraftforge.registries.ForgeRegistries;
import net.shelmarow.combat_evolution.effect.CEMobEffects;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.effect.EpicFightMobEffects;

public class HerobrineGregEntity extends Monster {
   private static final int MAX_COMBAT_LOW_CLONE_SUPPORT = 5;
   private static final float FISHING_HOOK_ESCAPE_CANCEL_CHANCE = 0.8F;
   private static final double SECOND_FORM_SUPPORT_SEARCH_RADIUS_SQR = 2304.0;
   private static final double FOLLOW_SUPPORT_SEARCH_RADIUS = 96.0;
   private static final double FOLLOW_SUPPORT_LEASH_RADIUS_SQR = 16384.0;
   private static final EntityDataAccessor<Boolean> WHITE_EYE = SynchedEntityData.m_135353_(HerobrineGregEntity.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> USE_HEROBRINE_TEXTURE = SynchedEntityData.m_135353_(
      HerobrineGregEntity.class, EntityDataSerializers.f_135035_
   );
   private static final EntityDataAccessor<Boolean> SUPPORTING_HEROBRINE = SynchedEntityData.m_135353_(
      HerobrineGregEntity.class, EntityDataSerializers.f_135035_
   );
   private static final EntityDataAccessor<Boolean> HOOKED = SynchedEntityData.m_135353_(HerobrineGregEntity.class, EntityDataSerializers.f_135035_);
   private boolean summoning = false;
   private int summonTiming = -1;
   private int escapeTiming = -1;
   private int summonTimestamp = -1;
   private boolean combatMode = false;
   private int recallTime;
   private boolean fishingHookCancelledEscape;
   private boolean hookedWaitingForGround;
   private boolean hookedLeftGround;
   private int idleAvoidRepathCooldown;
   private int supportRepositionCooldown = 120;
   private int supportRetreatPanicTicks;
   private int activeSupportRetreatTicks;
   private int lowCloneSupportCooldown = 0;
   private int portalPairCooldown = 0;
   private int rangedCounterPortalCooldown = 0;
   private int supportEscapePortalCooldown = 0;
   private int portalEscapeStepBackCooldown = 0;
   private int sixPortalSupportCooldown = 0;
   @Nullable
   private Vec3 activeSupportRetreatPos;
   private int supportingHerobrineVisualTicks;
   private Entity firstSummonedHerobrine;
   private Entity secondSummonedHerobrine;
   private Entity thirdSummonedHerobrine;
   private UUID firstSummonedHerobrineUUID;
   private UUID secondSummonedHerobrineUUID;
   private UUID thirdSummonedHerobrineUUID;
   private final Entity[] combatLowCloneSupport = new Entity[5];
   private final UUID[] combatLowCloneSupportUUIDs = new UUID[5];
   private BlockPos lastFeetPos = null;
   private String chatName;
   public static final List<Item> listWeapons = List.of(
      Items.f_42388_,
      Items.f_42391_,
      (Item)AnnoyingVillagersModItems.DIAMOND_ATTRACTOR_SWORD.get(),
      (Item)AnnoyingVillagersModItems.DIAMOND_BLASTER_SWORD.get(),
      (Item)AnnoyingVillagersModItems.HOOKED_DIAMOND_SWORD.get(),
      (Item)AnnoyingVillagersModItems.DIAMOND_WARBLADE.get(),
      (Item)AnnoyingVillagersModItems.DIAMOND_FALCHION.get(),
      (Item)AnnoyingVillagersModItems.DIAMOND_GREAT_FALCHION.get(),
      (Item)AnnoyingVillagersModItems.DIAMOND_SABRE.get(),
      (Item)AnnoyingVillagersModItems.DIAMOND_LONGSWORD.get(),
      (Item)AnnoyingVillagersModItems.DIAMOND_CHIPPED_LONGSWORD.get(),
      (Item)AnnoyingVillagersModItems.PALADIN_SWORD.get(),
      (Item)AnnoyingVillagersModItems.DIAMOND_GREATAXE.get(),
      (Item)AnnoyingVillagersModItems.DIAMOND_ARMBLADE.get(),
      (Item)AnnoyingVillagersModItems.DIAMOND_SICKLE.get(),
      (Item)AnnoyingVillagersModItems.DOUBLE_DIAMOND_GLAIVE.get(),
      (Item)AnnoyingVillagersModItems.DIAMOND_MOON_BLADE.get()
   );

   @Nullable
   public LivingEntityPatch<?> getLivingEntityPatch() {
      return (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(this, LivingEntityPatch.class);
   }

   public void setWhiteEye(boolean whiteEye) {
      this.f_19804_.m_135381_(WHITE_EYE, whiteEye);
   }

   public boolean isWhiteEye() {
      return (Boolean)this.f_19804_.m_135370_(WHITE_EYE);
   }

   public void setUseHerobrineTexture(boolean useHerobrineTexture) {
      this.f_19804_.m_135381_(USE_HEROBRINE_TEXTURE, useHerobrineTexture);
   }

   public int getEscapeTiming() {
      return this.escapeTiming;
   }

   public void setEscapeTiming(int escapeTiming) {
      this.escapeTiming = escapeTiming;
   }

   public boolean isUseHerobrineTexture() {
      return (Boolean)this.f_19804_.m_135370_(USE_HEROBRINE_TEXTURE);
   }

   public void markSupportingHerobrine() {
      this.supportingHerobrineVisualTicks = 40;
      this.setSupportingHerobrine(true);
   }

   public boolean isSupportingHerobrine() {
      return (Boolean)this.f_19804_.m_135370_(SUPPORTING_HEROBRINE);
   }

   private void setSupportingHerobrine(boolean supportingHerobrine) {
      this.f_19804_.m_135381_(SUPPORTING_HEROBRINE, supportingHerobrine);
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

   private int randomSupportRepositionCooldown() {
      return 700 + this.f_19796_.m_188503_(701);
   }

   private int randomCooldownSeconds(int minSeconds, int maxSeconds) {
      return minSeconds * 20 + this.f_19796_.m_188503_((maxSeconds - minSeconds) * 20 + 1);
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

   public int getSixPortalSupportCooldown() {
      return this.sixPortalSupportCooldown;
   }

   public void setSixPortalSupportCooldown() {
      this.sixPortalSupportCooldown = this.randomCooldownSeconds(30, 60);
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

      if (this.sixPortalSupportCooldown > 0) {
         this.sixPortalSupportCooldown--;
      }
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(WHITE_EYE, false);
      this.f_19804_.m_135372_(USE_HEROBRINE_TEXTURE, false);
      this.f_19804_.m_135372_(SUPPORTING_HEROBRINE, false);
      this.f_19804_.m_135372_(HOOKED, false);
   }

   public boolean isSummoning() {
      return this.summoning;
   }

   public boolean canAnswerSixPortalSupportRequest() {
      return !this.summoning && this.escapeTiming < 0 && this.summonTiming < 0 && !this.m_21525_() && this.sixPortalSupportCooldown <= 0;
   }

   public boolean canUseSupportPortalAction() {
      return !this.summoning && this.escapeTiming < 0 && this.summonTiming < 0 && !this.m_21525_();
   }

   public boolean canSummonLowCloneSupport() {
      return !this.summoning
         && this.escapeTiming < 0
         && this.summonTiming < 0
         && !this.m_21525_()
         && this.m_20096_()
         && this.lowCloneSupportCooldown <= 0
         && this.hasAvailableCombatLowCloneSupportSlot();
   }

   @Nullable
   public LivingEntity findEscapingSupportHerobrine() {
      for (LivingEntity support : HerobrinePortalCombatUtil.findSupportHerobrines(this, 40.0)) {
         if (support instanceof Mob mob
            && support.m_6084_()
            && isGregEscapeSupportTarget(support)
            && !isRidingHerobrineDragon(support)
            && mob.m_5448_() != null
            && mob.m_5448_().m_6084_()
            && EscapeUtil.checkEscape(mob)) {
            return support;
         }
      }

      return null;
   }

   private static boolean isGregEscapeSupportTarget(LivingEntity entity) {
      return entity instanceof TransporterHerobrineCloneEntity || entity instanceof LowHerobrineCloneEntity || entity instanceof LowShadowHerobrineCloneEntity;
   }

   private static boolean isGregFollowSupportTarget(LivingEntity entity) {
      return entity instanceof HerobrineMob
         && !(entity instanceof TransporterHerobrineCloneEntity)
         && !(entity instanceof LowHerobrineCloneEntity)
         && !(entity instanceof LowShadowHerobrineCloneEntity);
   }

   @Nullable
   public LivingEntity findGregFollowSupportHerobrine() {
      for (LivingEntity support : HerobrinePortalCombatUtil.findSupportHerobrines(this, 96.0)) {
         if (support.m_6084_() && isGregFollowSupportTarget(support) && (!support.m_20159_() || !(support.m_20202_() instanceof HerobrineDragonEntity))) {
            return support;
         }
      }

      return null;
   }

   public boolean isSupportingSecondFormCaster(LivingEntity support) {
      if (support instanceof HerobrineMob herobrineMob && support.m_6084_()) {
         UUID supportGregUUID = herobrineMob.getGregUUID();
         boolean assignedSupport = supportGregUUID != null && supportGregUUID.equals(this.m_20148_());
         return (assignedSupport || this.isSupportingHerobrine()) && this.m_20280_(support) <= 2304.0;
      }

      return false;
   }

   public void playSecondFormSupportCast(LivingEntity support) {
      this.markSupportingHerobrine();
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
      } else if (this.f_19796_.m_188501_() >= 0.8F) {
         return false;
      } else {
         this.escapeTiming = -1;
         this.summonTiming = -2;
         this.summoning = false;
         this.combatMode = false;
         this.recallTime = -1;
         this.fishingHookCancelledEscape = true;
         this.hookedWaitingForGround = true;
         this.hookedLeftGround = !this.m_20096_();
         this.setHooked(true);
         HerobrinePortalUtil.cancelSinkTransition(this);
         EpicfightUtil.cancel(this, AnimsSculkSteve.PORTAL_SUMMON);
         this.restoreGregHookedEscapeAppearance();
         this.releaseHookedPhysicsUntilGround();
         this.m_21573_().m_26573_();
         return true;
      }
   }

   private void restoreGregHookedEscapeAppearance() {
      this.setUseHerobrineTexture(false);
      this.setWhiteEye(true);
      this.m_8061_(EquipmentSlot.HEAD, this.randomDamage(new ItemStack((ItemLike)AnnoyingVillagersModItems.BROKEN_DIAMOND_HELMET.get())));
      this.m_8061_(EquipmentSlot.CHEST, this.randomDamage(new ItemStack((ItemLike)AnnoyingVillagersModItems.BROKEN_DIAMOND_CHESTPLATE.get())));
      this.m_8061_(EquipmentSlot.LEGS, this.randomDamage(new ItemStack((ItemLike)AnnoyingVillagersModItems.BROKEN_DIAMOND_LEGGINGS.get())));
      this.m_8061_(EquipmentSlot.FEET, this.randomDamage(new ItemStack((ItemLike)AnnoyingVillagersModItems.BROKEN_DIAMOND_BOOTS.get())));
   }

   private void playSecondFormSupportCastAnimation() {
      LivingEntityPatch<?> patch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(this, LivingEntityPatch.class);
      if (patch != null && !this.m_9236_().m_5776_()) {
         patch.playAnimationSynchronized(AnimsSculkSteve.PORTAL_SUMMON, 0.0F);
      }
   }

   public void setSummoning(boolean summoning) {
      this.summoning = summoning;
   }

   public int getSummonTimestamp() {
      return this.summonTimestamp;
   }

   public HerobrineGregEntity(SpawnEntity spawnentity, Level level) {
      this((EntityType<HerobrineGregEntity>)AnnoyingVillagersModEntities.HEROBRINE_GREG.get(), level);
   }

   public String getChatName() {
      return this.chatName;
   }

   public void setChatName(String chatName) {
      this.chatName = chatName;
   }

   public HerobrineGregEntity(EntityType<HerobrineGregEntity> entitytype, Level level) {
      super(entitytype, level);
      this.m_274367_(2.5F);
      this.f_21364_ = 50;
      this.m_21557_(false);
      this.m_21530_();
      this.m_6593_(Component.m_237113_("Greg"));
      this.setChatName(this.m_5446_().getString());
      this.m_20340_(true);
      int min = (Integer)AnnoyingVillagersConfig.HEROBRINE_RECALL_MIN_TIME.get();
      int max = (Integer)AnnoyingVillagersConfig.HEROBRINE_RECALL_MAX_TIME.get();
      int randomMin = Math.min(min, max);
      int randomMax = Math.max(min, max);
      this.recallTime = (randomMin + new Random().nextInt(randomMax - randomMin + 1)) * 60 * 20;
      this.m_21441_(BlockPathTypes.WATER, 0.0F);
      this.m_21441_(BlockPathTypes.WATER_BORDER, 0.0F);
      this.m_21441_(BlockPathTypes.LAVA, 0.0F);
      this.m_21441_(BlockPathTypes.DANGER_FIRE, 0.0F);
      this.m_21441_(BlockPathTypes.DAMAGE_FIRE, 0.0F);
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   protected void m_8099_() {
      super.m_8099_();
      this.f_21346_.m_25352_(1, new HurtByTargetGoal(this, new Class[0]));
      this.f_21346_
         .m_25352_(
            2,
            new NearestAttackableTargetGoal(
               this, LivingEntity.class, 10, true, false, target -> target != null && HerobrinePortalCombatUtil.isEnemyOf(this, target)
            )
         );
      this.f_21345_
         .m_25352_(
            0,
            new Goal() {
               private LivingEntity support;
               private Vec3 standPosition;
               private int repathCooldown;

               {
                  this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
               }

               public boolean m_8036_() {
                  if (!this.canMoveForSupport()) {
                     return false;
                  } else {
                     this.support = HerobrineGregEntity.this.findGregFollowSupportHerobrine();
                     return this.isValidSupport(this.support);
                  }
               }

               public boolean m_8045_() {
                  return this.canMoveForSupport() && this.isValidSupport(this.support) && HerobrineGregEntity.this.m_20280_(this.support) <= 16384.0;
               }

               public void m_8037_() {
                  if (this.isValidSupport(this.support)) {
                     HerobrineGregEntity.this.markSupportingHerobrine();
                     HerobrineGregEntity.this.m_21563_().m_24960_(this.support, 30.0F, 30.0F);
                     double distanceSqr = HerobrineGregEntity.this.m_20280_(this.support);
                     LivingEntity threat = this.findNearestSupportThreat(this.support);
                     if (threat != null && threat.m_6084_()) {
                        HerobrineGregEntity.this.m_6710_(threat);
                        HerobrineGregEntity.this.m_21563_().m_24960_(threat, 30.0F, 30.0F);
                     }

                     if (HerobrineGregEntity.this.activeSupportRetreatTicks > 0 && HerobrineGregEntity.this.activeSupportRetreatPos != null) {
                        HerobrineGregEntity.this.activeSupportRetreatTicks--;
                        if (threat != null) {
                           HerobrineGregEntity.this.m_21563_().m_24960_(threat, 30.0F, 30.0F);
                        }

                        if (HerobrineGregEntity.this.m_20182_().m_82557_(HerobrineGregEntity.this.activeSupportRetreatPos) > 4.0) {
                           HerobrineGregEntity.this.m_21573_()
                              .m_26519_(
                                 HerobrineGregEntity.this.activeSupportRetreatPos.f_82479_,
                                 HerobrineGregEntity.this.activeSupportRetreatPos.f_82480_,
                                 HerobrineGregEntity.this.activeSupportRetreatPos.f_82481_,
                                 1.3
                              );
                        } else {
                           HerobrineGregEntity.this.m_21573_().m_26573_();
                        }
                     } else {
                        boolean currentSpotSafe = this.isCurrentSupportSpotSafe(this.support, threat);
                        double maxStandDistanceSqr = threat == null ? 100.0 : 324.0;
                        if (distanceSqr <= maxStandDistanceSqr && currentSpotSafe) {
                           HerobrineGregEntity.this.m_21573_().m_26573_();
                           this.repathCooldown = 30;
                        } else {
                           if (this.repathCooldown-- <= 0 || HerobrineGregEntity.this.m_21573_().m_26571_() || this.hasReachedStandPosition()) {
                              this.moveToSupportStandPosition(this.support, threat);
                           }
                        }
                     }
                  }
               }

               private boolean canMoveForSupport() {
                  return !HerobrineGregEntity.this.summoning
                     && HerobrineGregEntity.this.escapeTiming < 0
                     && HerobrineGregEntity.this.summonTiming < 0
                     && !HerobrineGregEntity.this.m_21525_();
               }

               private boolean isValidSupport(@Nullable LivingEntity entity) {
                  return entity != null
                     && entity.m_6084_()
                     && HerobrineGregEntity.isGregFollowSupportTarget(entity)
                     && (!entity.m_20159_() || !(entity.m_20202_() instanceof HerobrineDragonEntity));
               }

               @Nullable
               private LivingEntity findNearestSupportThreat(LivingEntity support) {
                  LivingEntity threat = HerobrinePortalCombatUtil.findThreateningEnemy(HerobrineGregEntity.this, support, 24.0);
                  return threat != null ? threat : HerobrinePortalCombatUtil.findEnemyForSupport(support, HerobrineGregEntity.this.m_5448_(), 24.0);
               }

               private boolean isCurrentSupportSpotSafe(LivingEntity support, @Nullable LivingEntity threat) {
                  double maxSupportDistanceSqr = threat == null ? 100.0 : 324.0;
                  return HerobrineGregEntity.this.m_20280_(support) > maxSupportDistanceSqr
                     ? false
                     : threat == null || HerobrineGregEntity.this.m_20280_(threat) >= 144.0;
               }

               private boolean hasReachedStandPosition() {
                  return this.standPosition != null && HerobrineGregEntity.this.m_20182_().m_82557_(this.standPosition) <= 4.0;
               }

               private void moveToSupportStandPosition(LivingEntity support, @Nullable LivingEntity threat) {
                  this.standPosition = this.findSupportStandPosition(support, threat);
                  HerobrineGregEntity.this.m_21573_()
                     .m_26519_(this.standPosition.f_82479_, this.standPosition.f_82480_, this.standPosition.f_82481_, threat == null ? 1.15 : 1.25);
                  this.repathCooldown = threat == null
                     ? 30 + HerobrineGregEntity.this.f_19796_.m_188503_(10)
                     : 8 + HerobrineGregEntity.this.f_19796_.m_188503_(5);
               }

               private Vec3 findSupportStandPosition(LivingEntity support, @Nullable LivingEntity threat) {
                  double baseAngle = Math.atan2(
                     HerobrineGregEntity.this.m_20189_() - support.m_20189_(), HerobrineGregEntity.this.m_20185_() - support.m_20185_()
                  );
                  if (Double.isNaN(baseAngle)) {
                     baseAngle = HerobrineGregEntity.this.f_19796_.m_188500_() * Math.PI * 2.0;
                  }

                  double standRadius = threat == null ? 7.0 : 15.0;
                  if (threat == null) {
                     Vec3 currentSidePosition = this.standPositionAt(support, baseAngle, standRadius);
                     return currentSidePosition != null ? currentSidePosition : new Vec3(support.m_20185_(), support.m_20186_(), support.m_20189_());
                  } else {
                     double awayFromThreatAngle = Math.atan2(support.m_20189_() - threat.m_20189_(), support.m_20185_() - threat.m_20185_());
                     if (Double.isNaN(awayFromThreatAngle)) {
                        awayFromThreatAngle = baseAngle;
                     }

                     Vec3 bestPosition = null;
                     double bestScore = Double.NEGATIVE_INFINITY;

                     for (int sample = 0; sample < 16; sample++) {
                        double angle = awayFromThreatAngle + (Math.PI * 2) * (double)sample / 16.0;
                        Vec3 candidate = this.standPositionAt(support, angle, standRadius);
                        if (candidate != null) {
                           double threatDistanceSqr = candidate.m_82557_(threat.m_20182_());
                           double movePenalty = candidate.m_82557_(HerobrineGregEntity.this.m_20182_()) * 0.08;
                           double score = threatDistanceSqr - movePenalty;
                           if (threatDistanceSqr < 144.0) {
                              score -= 300.0;
                           }

                           if (score > bestScore) {
                              bestScore = score;
                              bestPosition = candidate;
                           }
                        }
                     }

                     if (bestPosition != null) {
                        return bestPosition;
                     } else {
                        Vec3 fallback = this.standPositionAt(support, awayFromThreatAngle, standRadius);
                        return fallback != null ? fallback : new Vec3(support.m_20185_(), support.m_20186_(), support.m_20189_());
                     }
                  }
               }

               @Nullable
               private Vec3 standPositionAt(LivingEntity support, double angle, double radius) {
                  double x = support.m_20185_() + Math.cos(angle) * radius;
                  double z = support.m_20189_() + Math.sin(angle) * radius;
                  double y = support.m_20186_();
                  if (HerobrineGregEntity.this.m_9236_() instanceof ServerLevel serverLevel) {
                     y = (double)serverLevel.m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, BlockPos.m_274561_(x, y, z)).m_123342_();
                  }

                  Vec3 candidate = new Vec3(x, y, z);
                  AABB movedBox = HerobrineGregEntity.this.m_20191_().m_82383_(candidate.m_82546_(HerobrineGregEntity.this.m_20182_()));
                  return HerobrineGregEntity.this.m_9236_().m_45756_(HerobrineGregEntity.this, movedBox) ? candidate : null;
               }
            }
         );
      this.f_21345_
         .m_25352_(
            1,
            new Goal() {
               private LivingEntity threat;

               {
                  this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
               }

               public boolean m_8036_() {
                  if (!this.canIdleAvoid()) {
                     return false;
                  } else {
                     this.threat = this.findIdleThreat();
                     return this.threat != null && this.threat.m_6084_() && HerobrineGregEntity.this.m_20280_(this.threat) <= 324.0;
                  }
               }

               public boolean m_8045_() {
                  return this.canIdleAvoid() && this.threat != null && this.threat.m_6084_() && HerobrineGregEntity.this.m_20280_(this.threat) <= 1024.0;
               }

               public void m_8037_() {
                  if (HerobrineGregEntity.this.idleAvoidRepathCooldown > 0) {
                     HerobrineGregEntity.this.idleAvoidRepathCooldown--;
                  }

                  this.threat = this.findIdleThreat();
                  HerobrineGregEntity.this.m_6710_(null);
                  if (this.threat != null && this.threat.m_6084_()) {
                     HerobrineGregEntity.this.m_21563_().m_24960_(this.threat, 30.0F, 30.0F);
                     if (!(HerobrineGregEntity.this.m_20280_(this.threat) > 324.0)) {
                        if (HerobrineGregEntity.this.idleAvoidRepathCooldown <= 0 || HerobrineGregEntity.this.m_21573_().m_26571_()) {
                           Vec3 retreatPos = this.findIdleRetreatPosition(this.threat);
                           if (retreatPos != null) {
                              HerobrineGregEntity.this.m_21573_().m_26519_(retreatPos.f_82479_, retreatPos.f_82480_, retreatPos.f_82481_, 1.15);
                              HerobrineGregEntity.this.idleAvoidRepathCooldown = 15;
                           }
                        }
                     }
                  }
               }

               private boolean canIdleAvoid() {
                  return !HerobrineGregEntity.this.combatMode
                     && !HerobrineGregEntity.this.summoning
                     && HerobrineGregEntity.this.escapeTiming < 0
                     && HerobrineGregEntity.this.summonTiming < 0
                     && !HerobrineGregEntity.this.m_21525_()
                     && !HerobrineGregEntity.this.isSupportingHerobrine()
                     && HerobrineGregEntity.this.findGregFollowSupportHerobrine() == null;
               }

               @Nullable
               private LivingEntity findIdleThreat() {
                  LivingEntity threat = HerobrinePortalCombatUtil.findThreateningEnemy(HerobrineGregEntity.this, null, 32.0);
                  return threat != null ? threat : HerobrinePortalCombatUtil.findEnemyForSupport(HerobrineGregEntity.this, null, 32.0);
               }

               @Nullable
               private Vec3 findIdleRetreatPosition(LivingEntity threat) {
                  if (HerobrineGregEntity.this.m_9236_() instanceof ServerLevel serverLevel) {
                     double awayAngle = Math.atan2(
                        HerobrineGregEntity.this.m_20189_() - threat.m_20189_(), HerobrineGregEntity.this.m_20185_() - threat.m_20185_()
                     );
                     if (Double.isNaN(awayAngle)) {
                        awayAngle = HerobrineGregEntity.this.m_217043_().m_188500_() * Math.PI * 2.0;
                     }

                     for (int attempt = 0; attempt < 10; attempt++) {
                        double angle = awayAngle + (HerobrineGregEntity.this.m_217043_().m_188500_() - 0.5) * 1.4;
                        double distance = 12.0 + HerobrineGregEntity.this.m_217043_().m_188500_() * 8.0;
                        double x = HerobrineGregEntity.this.m_20185_() + Math.cos(angle) * distance;
                        double z = HerobrineGregEntity.this.m_20189_() + Math.sin(angle) * distance;
                        BlockPos surface = serverLevel.m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, BlockPos.m_274561_(x, HerobrineGregEntity.this.m_20186_(), z));
                        if (serverLevel.m_46749_(surface)
                           && serverLevel.m_6857_().m_61937_(surface)
                           && serverLevel.m_46859_(surface)
                           && serverLevel.m_46859_(surface.m_7494_())
                           && !serverLevel.m_46859_(surface.m_7495_())) {
                           Vec3 candidate = new Vec3((double)surface.m_123341_() + 0.5, (double)surface.m_123342_(), (double)surface.m_123343_() + 0.5);
                           if (!(candidate.m_82557_(threat.m_20182_()) <= HerobrineGregEntity.this.m_20182_().m_82557_(threat.m_20182_()))
                              && serverLevel.m_45756_(
                                 HerobrineGregEntity.this,
                                 HerobrineGregEntity.this.m_20191_().m_82383_(candidate.m_82546_(HerobrineGregEntity.this.m_20182_())).m_82406_(1.0E-4)
                              )) {
                              return candidate;
                           }
                        }
                     }

                     return null;
                  } else {
                     return null;
                  }
               }
            }
         );
      this.f_21345_
         .m_25352_(
            1,
            new Goal() {
               public boolean m_8036_() {
                  return HerobrineGregEntity.this.firstSummonedHerobrine != null
                     && HerobrineGregEntity.this.firstSummonedHerobrine.m_6084_()
                     && (double)HerobrineGregEntity.this.m_20270_(HerobrineGregEntity.this.firstSummonedHerobrine) > 8.0;
               }

               public void m_8037_() {
                  if (HerobrineGregEntity.this.firstSummonedHerobrine != null && HerobrineGregEntity.this.firstSummonedHerobrine.m_6084_()) {
                     HerobrineGregEntity.this.m_21573_().m_5624_(HerobrineGregEntity.this.firstSummonedHerobrine, 2.0);
                     HerobrineGregEntity.this.m_21563_().m_24960_(HerobrineGregEntity.this.firstSummonedHerobrine, 30.0F, 30.0F);
                     if (HerobrineGregEntity.this.m_20280_(HerobrineGregEntity.this.firstSummonedHerobrine) > 25.0) {
                        if (HerobrineGregEntity.this.m_21573_().m_26571_()) {
                           HerobrineGregEntity.this.m_21573_().m_5624_(HerobrineGregEntity.this.firstSummonedHerobrine, 2.0);
                        }
                     } else {
                        HerobrineGregEntity.this.m_21573_().m_26573_();
                     }
                  }
               }

               public boolean m_8045_() {
                  return HerobrineGregEntity.this.firstSummonedHerobrine != null
                     && HerobrineGregEntity.this.firstSummonedHerobrine.m_6084_()
                     && (double)HerobrineGregEntity.this.m_20270_(HerobrineGregEntity.this.firstSummonedHerobrine) > 5.0;
               }
            }
         );
      this.f_21345_
         .m_25352_(
            1,
            new Goal() {
               public boolean m_8036_() {
                  return HerobrineGregEntity.this.secondSummonedHerobrine != null
                     && HerobrineGregEntity.this.secondSummonedHerobrine.m_6084_()
                     && (double)HerobrineGregEntity.this.m_20270_(HerobrineGregEntity.this.secondSummonedHerobrine) > 8.0;
               }

               public void m_8037_() {
                  if (HerobrineGregEntity.this.secondSummonedHerobrine != null && HerobrineGregEntity.this.secondSummonedHerobrine.m_6084_()) {
                     HerobrineGregEntity.this.m_21573_().m_5624_(HerobrineGregEntity.this.secondSummonedHerobrine, 2.0);
                     HerobrineGregEntity.this.m_21563_().m_24960_(HerobrineGregEntity.this.secondSummonedHerobrine, 30.0F, 30.0F);
                     if (HerobrineGregEntity.this.m_20280_(HerobrineGregEntity.this.secondSummonedHerobrine) > 25.0) {
                        if (HerobrineGregEntity.this.m_21573_().m_26571_()) {
                           HerobrineGregEntity.this.m_21573_().m_5624_(HerobrineGregEntity.this.secondSummonedHerobrine, 2.0);
                        }
                     } else {
                        HerobrineGregEntity.this.m_21573_().m_26573_();
                     }
                  }
               }

               public boolean m_8045_() {
                  return HerobrineGregEntity.this.secondSummonedHerobrine != null
                     && HerobrineGregEntity.this.secondSummonedHerobrine.m_6084_()
                     && (double)HerobrineGregEntity.this.m_20270_(HerobrineGregEntity.this.secondSummonedHerobrine) > 5.0;
               }
            }
         );
      this.f_21345_
         .m_25352_(
            1,
            new Goal() {
               public boolean m_8036_() {
                  return HerobrineGregEntity.this.thirdSummonedHerobrine != null
                     && HerobrineGregEntity.this.thirdSummonedHerobrine.m_6084_()
                     && (double)HerobrineGregEntity.this.m_20270_(HerobrineGregEntity.this.thirdSummonedHerobrine) > 8.0;
               }

               public void m_8037_() {
                  if (HerobrineGregEntity.this.thirdSummonedHerobrine != null && HerobrineGregEntity.this.thirdSummonedHerobrine.m_6084_()) {
                     HerobrineGregEntity.this.m_21573_().m_5624_(HerobrineGregEntity.this.thirdSummonedHerobrine, 2.0);
                     HerobrineGregEntity.this.m_21563_().m_24960_(HerobrineGregEntity.this.thirdSummonedHerobrine, 30.0F, 30.0F);
                     if (HerobrineGregEntity.this.m_20280_(HerobrineGregEntity.this.thirdSummonedHerobrine) > 25.0) {
                        if (HerobrineGregEntity.this.m_21573_().m_26571_()) {
                           HerobrineGregEntity.this.m_21573_().m_5624_(HerobrineGregEntity.this.thirdSummonedHerobrine, 2.0);
                        }
                     } else {
                        HerobrineGregEntity.this.m_21573_().m_26573_();
                     }
                  }
               }

               public boolean m_8045_() {
                  return HerobrineGregEntity.this.thirdSummonedHerobrine != null
                     && HerobrineGregEntity.this.thirdSummonedHerobrine.m_6084_()
                     && (double)HerobrineGregEntity.this.m_20270_(HerobrineGregEntity.this.thirdSummonedHerobrine) > 5.0;
               }
            }
         );
      this.f_21345_.m_25352_(2, new AvoidEntityGoal(this, VillagerScoutEntity.class, 12.0F, 1.0, 1.35));
      this.f_21345_.m_25352_(2, new AvoidEntityGoal(this, VillagerScoutCaptainEntity.class, 12.0F, 1.0, 1.35));
      this.f_21345_.m_25352_(2, new AvoidEntityGoal(this, BlueVillagerKnightEntity.class, 12.0F, 1.0, 1.35));
      this.f_21345_.m_25352_(2, new AvoidEntityGoal(this, GreenVillagerKnightEntity.class, 12.0F, 1.0, 1.35));
      this.f_21345_.m_25352_(2, new AvoidEntityGoal(this, RedVillagerKnightEntity.class, 12.0F, 1.0, 1.35));
      this.f_21345_.m_25352_(2, new AvoidEntityGoal(this, PurpleVillagerKnightEntity.class, 12.0F, 1.0, 1.35));
      this.f_21345_.m_25352_(2, new AvoidEntityGoal(this, PlayerNpcEntity.class, 12.0F, 1.0, 1.35));
      this.f_21345_.m_25352_(2, new AvoidEntityGoal(this, Player.class, 12.0F, 1.0, 1.35));
      this.f_21345_.m_25352_(2, new AvoidEntityGoal(this, SteveEntity.class, 24.0F, 1.0, 1.35));
      this.f_21345_.m_25352_(2, new AvoidEntityGoal(this, AlexEntity.class, 12.0F, 1.0, 1.35));
      this.f_21345_.m_25352_(2, new AvoidEntityGoal(this, JevEntity.class, 12.0F, 1.0, 1.35));
      this.f_21345_.m_25352_(2, new AvoidEntityGoal(this, ChrisEntity.class, 12.0F, 1.0, 1.35));
      this.f_21345_.m_25352_(2, new AvoidEntityGoal(this, BlueDemonEntity.class, 12.0F, 1.0, 1.35));
      this.f_21345_.m_25352_(2, new AvoidEntityGoal(this, BbqEntity.class, 12.0F, 1.0, 1.35));
      this.f_21345_.m_25352_(3, new RandomStrollGoal(this, 1.0));
      this.f_21345_.m_25352_(4, new RandomLookAroundGoal(this));
      this.f_21345_.m_25352_(5, new FloatGoal(this));
   }

   @NotNull
   public MobType m_6336_() {
      return MobType.f_21640_;
   }

   public double m_6049_() {
      return -0.35;
   }

   @NotNull
   protected PathNavigation m_6037_(@NotNull Level level) {
      return new HerobrineMob.AnyFluidPathNavigation(this, level);
   }

   public boolean m_20069_() {
      FluidState fs = this.m_9236_().m_6425_(this.m_20183_());
      return !fs.m_76178_() && this.m_203441_(fs) ? false : super.m_20069_();
   }

   public boolean m_203441_(FluidState state) {
      return !state.m_76178_();
   }

   public boolean m_6063_() {
      return false;
   }

   private void tickSupportingHerobrineVisuals() {
      if (this.supportingHerobrineVisualTicks > 0) {
         this.supportingHerobrineVisualTicks--;
      }

      boolean supportingAppearance = this.supportingHerobrineVisualTicks > 0 || this.hasNearbyHerobrineToSupport();
      if (!supportingAppearance) {
         if (this.isSupportingHerobrine()) {
            this.setSupportingHerobrine(false);
         }
      } else {
         if (!this.isSupportingHerobrine()) {
            this.setSupportingHerobrine(true);
         }

         if (!this.isWhiteEye()) {
            this.setWhiteEye(true);
         }

         if (!this.m_6844_(EquipmentSlot.CHEST).m_150930_((Item)AnnoyingVillagersModItems.BROKEN_DIAMOND_CHESTPLATE.get())) {
            this.m_8061_(EquipmentSlot.CHEST, new ItemStack((ItemLike)AnnoyingVillagersModItems.BROKEN_DIAMOND_CHESTPLATE.get()));
         }
      }
   }

   private boolean hasNearbyHerobrineToSupport() {
      LivingEntity support = this.findGregFollowSupportHerobrine();
      return support != null && support.m_6084_() && (!support.m_20159_() || !(support.m_20202_() instanceof HerobrineDragonEntity));
   }

   private void assignProtect(Entity entity, UUID protectUUID, EliteHerobrineKnockedEntity protectEntity) {
      if (entity != null && entity.m_6084_()) {
         if (entity instanceof HerobrineMob herobrineMob) {
            herobrineMob.setProtectUUID(protectUUID);
            herobrineMob.setProtectEntity(protectEntity);
         } else if (entity instanceof LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntity) {
            lowShadowHerobrineCloneEntity.setProtectUUID(protectUUID);
            lowShadowHerobrineCloneEntity.setProtectEntity(protectEntity);
         }
      }
   }

   public void requestProtect(UUID protectUUID, EliteHerobrineKnockedEntity protectEntity) {
      this.assignProtect(this.firstSummonedHerobrine, protectUUID, protectEntity);
      this.assignProtect(this.secondSummonedHerobrine, protectUUID, protectEntity);
      this.assignProtect(this.thirdSummonedHerobrine, protectUUID, protectEntity);
   }

   private static boolean isRidingHerobrineDragon(Entity entity) {
      return entity.m_20159_() && entity.m_20202_() instanceof HerobrineDragonEntity;
   }

   private void floatOnAnyFluid() {
      BlockPos pos = this.m_20183_();
      FluidState fluidState = this.m_9236_().m_6425_(pos);
      if (!fluidState.m_76178_()) {
         CollisionContext collisionContext = CollisionContext.m_82750_(this);
         Fluid typeHere = fluidState.m_76152_();
         FluidState above = this.m_9236_().m_6425_(pos.m_7494_());
         if (collisionContext.m_6513_(LiquidBlock.f_54690_, pos, true) && above.m_76152_() != typeHere) {
            this.m_6853_(true);
            double surfaceY = (double)((float)pos.m_123342_() + fluidState.m_76155_(this.m_9236_(), pos));
            double bottomY = this.m_20191_().f_82289_;
            double diff = surfaceY - bottomY - 0.001;
            if (diff > 0.0) {
               Vec3 vel = this.m_20184_();
               this.m_20334_(vel.f_82479_, Math.max(vel.f_82480_, Math.min(0.2, diff * 0.2)), vel.f_82481_);
            }
         } else {
            this.m_20256_(this.m_20184_().m_82490_(0.5).m_82520_(0.0, 0.05, 0.0));
         }

         this.f_19789_ = 0.0F;
      }
   }

   private void placeObsidianBlockWhenInWater(Block block) {
      BlockPos feet = this.m_20097_();
      if (this.lastFeetPos == null) {
         this.lastFeetPos = feet;
      }

      if (!feet.equals(this.lastFeetPos)) {
         if (!this.m_9236_().m_8055_(this.lastFeetPos).m_60713_(block)) {
            FluidState fluidState = this.m_9236_().m_6425_(this.lastFeetPos);
            if (!fluidState.m_76178_()) {
               int replace = fluidState.m_76170_() ? (fluidState.m_205070_(FluidTags.f_13131_) ? 1 : (fluidState.m_205070_(FluidTags.f_13132_) ? 2 : 0)) : 0;
               BlockState state = (BlockState)block.m_49966_().m_61124_(HerobrineObsidianBlock.REPLACE_BY_LIQUID, replace);
               this.m_9236_().m_46597_(this.lastFeetPos, state);
               BlockEntity blockEntity = this.m_9236_().m_7702_(this.lastFeetPos);
               if (blockEntity instanceof ObsidianBlockEntity obsidianBlockEntity) {
                  obsidianBlockEntity.setOwner(this.m_20148_());
                  obsidianBlockEntity.m_6596_();
                  this.m_9236_().m_7260_(this.lastFeetPos, state, state, 3);
               }

               if (blockEntity instanceof ShadowObsidianBlockEntity shadowObsidianBlockEntity) {
                  shadowObsidianBlockEntity.setOwner(this.m_20148_());
                  shadowObsidianBlockEntity.m_6596_();
                  this.m_9236_().m_7260_(this.lastFeetPos, state, state, 3);
               }

               if (blockEntity instanceof CryingObsidianBlockEntity cryingObsidianBlockEntity) {
                  cryingObsidianBlockEntity.setOwner(this.m_20148_());
                  cryingObsidianBlockEntity.m_6596_();
                  this.m_9236_().m_7260_(this.lastFeetPos, state, state, 3);
               }
            }
         }

         this.lastFeetPos = feet;
      }
   }

   public void m_8119_() {
      if (!this.m_9236_().f_46443_ && this.isHooked() && !this.hookedWaitingForGround) {
         this.enforceHookedNoAiLock();
      }

      super.m_8119_();
      this.floatOnAnyFluid();
      this.m_20101_();
      if (!this.m_9236_().f_46443_) {
         this.tickCombatLowCloneSupportSlots();
         this.tickCombatActionCooldowns();
         if (this.isHooked()) {
            this.tickHookedGroundRelock();
            return;
         }
      }

      if (!this.m_9236_().f_46443_) {
         this.placeObsidianBlockWhenInWater((Block)AnnoyingVillagersModBlocks.CRYING_OBSIDIAN_BLOCK.get());
         this.tickSupportingHerobrineVisuals();
         this.tickActiveSupportReposition();
         if (!this.isDay(this.m_9236_())) {
            if (!this.isWhiteEye()) {
               this.setWhiteEye(true);
            }

            if (!this.m_6844_(EquipmentSlot.CHEST).m_41720_().equals(AnnoyingVillagersModItems.BROKEN_DIAMOND_CHESTPLATE.get())) {
               this.m_8061_(EquipmentSlot.CHEST, new ItemStack((ItemLike)AnnoyingVillagersModItems.BROKEN_DIAMOND_CHESTPLATE.get()));
            }
         } else {
            if (this.isWhiteEye() && this.summonTiming == -1 && !this.isSupportingHerobrine() && !this.isUseHerobrineTexture()) {
               this.setWhiteEye(false);
            }

            if (!this.combatMode
               && !this.isSupportingHerobrine()
               && !this.isUseHerobrineTexture()
               && this.m_6844_(EquipmentSlot.CHEST).m_41720_().equals(AnnoyingVillagersModItems.BROKEN_DIAMOND_CHESTPLATE.get())) {
               this.m_8061_(EquipmentSlot.CHEST, ItemStack.f_41583_);
            }
         }

         if (this.m_9236_().m_46468_() % 24000L == 13001L && this.summonTimestamp == -1) {
            if (new Random().nextBoolean()) {
               Objects.requireNonNull(this.m_9236_().m_7654_())
                  .m_6846_()
                  .m_240416_(
                     Component.m_237113_("<" + this.getChatName() + "> " + Component.m_237115_("subtitles.herobrine_prepare_for_fight").getString()), false
                  );
               this.summonTimestamp = new Random().nextInt(13100, 22200);
               AnnoyingVillagers.LOGGER.info("[AV MOD DEBUG]: Greg will summon elites at {}", this.summonTimestamp);
            } else {
               Objects.requireNonNull(this.m_9236_().m_7654_())
                  .m_6846_()
                  .m_240416_(Component.m_237113_("<" + this.getChatName() + "> " + Component.m_237115_("subtitles.herobrine_no_fight").getString()), false);
            }
         }

         if (this.m_9236_().m_46468_() % 24000L == (long)this.summonTimestamp) {
            this.summonTimestamp = -2;
            this.combatMode = true;
            this.m_21557_(true);
            this.m_20331_(true);
            this.summoning = true;
            this.summonTiming = 20;
            this.m_7292_(new MobEffectInstance((MobEffect)EpicFightMobEffects.STUN_IMMUNITY.get(), 120, 3, false, false));
            this.m_7292_(new MobEffectInstance((MobEffect)CEMobEffects.FULL_STUN_IMMUNITY.get(), 120, 3, false, false));
         }

         if (this.m_21223_() <= 2.0F && this.summonTiming == -1) {
            if (!this.isDay(this.m_9236_())) {
               this.m_8061_(EquipmentSlot.CHEST, new ItemStack((ItemLike)AnnoyingVillagersModItems.BROKEN_DIAMOND_CHESTPLATE.get()));
            }

            this.setWhiteEye(true);
            this.m_21557_(true);
            this.m_20331_(true);
            this.summoning = true;
            this.summonTiming = 20;
            this.m_21153_(1.0F);
            this.m_7292_(new MobEffectInstance((MobEffect)EpicFightMobEffects.STUN_IMMUNITY.get(), 120, 3, false, false));
            this.m_7292_(new MobEffectInstance((MobEffect)CEMobEffects.FULL_STUN_IMMUNITY.get(), 120, 3, false, false));
         }

         if (this.summonTiming > 0) {
            this.summonTiming--;
         }

         if (this.summonTiming == 10) {
            this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.PORTAL_SUMMON.get(), 1.0F, 1.0F);
            Objects.requireNonNull(this.m_9236_().m_7654_())
               .m_6846_()
               .m_240416_(Component.m_237113_("<" + this.getChatName() + "> " + Component.m_237115_("subtitles.herobrine_summon").getString()), false);
         }

         if (this.summonTiming == 1) {
            if (this.combatMode) {
               this.summonHerobrines();
            } else {
               this.summonHerobrinesAndEscape();
            }
         }

         if (this.escapeTiming > 0) {
            this.escapeTiming--;
         }

         if (this.escapeTiming == 60 && this.combatMode) {
            this.m_216990_((SoundEvent)AnnoyingVillagersModSounds.PORTAL_NATURAL.get());
            if (this.getLivingEntityPatch() != null) {
               this.getLivingEntityPatch().playAnimationSynchronized(AnimsSculkSteve.PORTAL_SUMMON, 0.0F);
            }

            AnnoyingVillagers.PACKET_HANDLER
               .send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new ClientboundHerobrinePortalFx(this.m_20097_().m_252807_().m_82520_(0.0, 0.5, 0.0)));
         }

         if (this.escapeTiming == 40 && this.m_9236_() instanceof ServerLevel serverLevel) {
            HerobrinePortalUtil.sinkIntoGround(serverLevel, this, 0.06);
         }

         if (this.escapeTiming == 1) {
            this.m_9236_()
               .m_7654_()
               .m_6846_()
               .m_240416_(Component.m_237113_("<" + this.getChatName() + "> " + Component.m_237115_("subtitles.herobrine_will_be_back").getString()), false);
            if (this.firstSummonedHerobrine instanceof LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntity) {
               lowShadowHerobrineCloneEntity.setAutoKill(true);
               lowShadowHerobrineCloneEntity.m_6074_();
            }

            if (this.secondSummonedHerobrine instanceof LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntity) {
               lowShadowHerobrineCloneEntity.setAutoKill(true);
               lowShadowHerobrineCloneEntity.m_6074_();
            }

            if (this.thirdSummonedHerobrine instanceof LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntity) {
               lowShadowHerobrineCloneEntity.setAutoKill(true);
               lowShadowHerobrineCloneEntity.m_6074_();
            }

            this.m_146870_();
         }

         if (this.firstSummonedHerobrine == null && this.firstSummonedHerobrineUUID != null) {
            Entity entity = ((ServerLevel)this.m_9236_()).m_8791_(this.firstSummonedHerobrineUUID);
            if (!(entity instanceof HerobrineMob) && !(entity instanceof LowShadowHerobrineCloneEntity)) {
               this.firstSummonedHerobrineUUID = null;
            } else {
               this.firstSummonedHerobrine = entity;
            }
         }

         if (this.firstSummonedHerobrine != null && !this.firstSummonedHerobrine.m_6084_()) {
            this.firstSummonedHerobrineUUID = null;
         }

         if (this.secondSummonedHerobrine == null && this.secondSummonedHerobrineUUID != null) {
            Entity entity = ((ServerLevel)this.m_9236_()).m_8791_(this.secondSummonedHerobrineUUID);
            if (!(entity instanceof HerobrineMob) && !(entity instanceof LowShadowHerobrineCloneEntity)) {
               this.secondSummonedHerobrineUUID = null;
            } else {
               this.secondSummonedHerobrine = entity;
            }
         }

         if (this.secondSummonedHerobrine != null && !this.secondSummonedHerobrine.m_6084_()) {
            this.secondSummonedHerobrineUUID = null;
         }

         if (this.thirdSummonedHerobrine == null && this.thirdSummonedHerobrineUUID != null) {
            Entity entity = ((ServerLevel)this.m_9236_()).m_8791_(this.thirdSummonedHerobrineUUID);
            if (!(entity instanceof HerobrineMob) && !(entity instanceof LowShadowHerobrineCloneEntity)) {
               this.thirdSummonedHerobrineUUID = null;
            } else {
               this.thirdSummonedHerobrine = entity;
            }
         }

         if (this.thirdSummonedHerobrine != null && !this.thirdSummonedHerobrine.m_6084_()) {
            this.thirdSummonedHerobrineUUID = null;
         }

         if (this.combatMode
            && this.escapeTiming == -1
            && this.summonTiming == -2
            && !this.fishingHookCancelledEscape
            && this.firstSummonedHerobrineUUID == null
            && this.secondSummonedHerobrineUUID == null
            && this.thirdSummonedHerobrineUUID == null) {
            this.escapeTiming = 80;
            this.m_21557_(true);
         }

         if (this.combatMode && !this.fishingHookCancelledEscape && this.escapeTiming == -1 && this.recallTime >= 0) {
            this.recallTime--;
            if (this.recallTime == 20) {
               this.m_21557_(true);
            }

            if (this.recallTime <= 0) {
               this.escapeTiming = 61;
            }
         }

         if (this.combatMode) {
            this.m_7292_(new MobEffectInstance((MobEffect)EpicFightMobEffects.STUN_IMMUNITY.get(), 1, 3, false, false));
         }
      }
   }

   private void tickActiveSupportReposition() {
      if (this.supportRetreatPanicTicks > 0) {
         this.supportRetreatPanicTicks--;
      }

      if (this.supportRepositionCooldown > 0) {
         this.supportRepositionCooldown--;
      }

      if (this.supportRepositionCooldown <= 0) {
         boolean panic = this.supportRetreatPanicTicks > 0;
         if (!panic && this.f_19796_.m_188501_() > 0.35F) {
            this.supportRepositionCooldown = 120;
         } else {
            boolean activated = this.tryActiveSupportReposition(panic);
            this.supportRepositionCooldown = activated ? this.randomSupportRepositionCooldown() : 120;
         }
      }
   }

   private boolean tryActiveSupportReposition(boolean panic) {
      if (!(this.m_9236_() instanceof ServerLevel serverLevel)) {
         return false;
      } else {
         LivingEntity support = this.findGregFollowSupportHerobrine();
         if (support != null && support.m_6084_() && (!support.m_20159_() || !(support.m_20202_() instanceof HerobrineDragonEntity))) {
            LivingEntity enemy = HerobrinePortalCombatUtil.findThreateningEnemy(this, support, 24.0);
            if (enemy == null) {
               enemy = HerobrinePortalCombatUtil.findEnemyForSupport(support, this.m_5448_(), 24.0);
            }

            if (enemy == null) {
               return false;
            } else {
               boolean dangerClose = panic || this.m_20280_(enemy) <= 144.0 || support.m_20280_(enemy) <= 100.0;
               if (!dangerClose) {
                  return false;
               } else if (!TransporterFragmentItem.canSpawnOwnedPortals(serverLevel, this, 4)) {
                  return false;
               } else {
                  Vec3 retreat = this.findActiveSupportRetreatPosition(serverLevel, support, enemy);
                  if (retreat == null) {
                     return false;
                  } else {
                     Vec3 returnEntrance = this.findRetreatReturnEntrance(serverLevel, retreat, support, enemy);
                     Vec3 returnExit = this.findEnemyFlankReturnExit(serverLevel, enemy, support);
                     int spawned = 0;
                     spawned += TransporterFragmentItem.spawnLinkedPortalPair(
                        this.m_9236_(),
                        this,
                        HerobrinePortalCombatUtil.applySupportPortalYOffset(this, support.m_20182_()),
                        HerobrinePortalCombatUtil.applySupportPortalYOffset(this, retreat)
                     );
                     spawned += TransporterFragmentItem.spawnLinkedPortalPair(
                        this.m_9236_(),
                        this,
                        HerobrinePortalCombatUtil.applySupportPortalYOffset(this, returnEntrance),
                        HerobrinePortalCombatUtil.applySupportPortalYOffset(this, returnExit)
                     );
                     if (spawned <= 0) {
                        return false;
                     } else {
                        this.activeSupportRetreatPos = retreat;
                        this.activeSupportRetreatTicks = 90;
                        this.markSupportingHerobrine();
                        HerobrinePortalCombatUtil.playPortalPairSummon(this);
                        return true;
                     }
                  }
               }
            }
         } else {
            return false;
         }
      }
   }

   @Nullable
   private Vec3 findActiveSupportRetreatPosition(ServerLevel serverLevel, LivingEntity support, LivingEntity enemy) {
      Vec3 away = this.horizontalDirection(support.m_20182_().m_82546_(enemy.m_20182_()));
      if (away.m_82556_() < 1.0E-4) {
         away = this.horizontalDirection(this.m_20182_().m_82546_(enemy.m_20182_()));
      }

      if (away.m_82556_() < 1.0E-4) {
         away = Vec3.m_82498_(0.0F, this.m_146908_());
      }

      for (int attempt = 0; attempt < 32; attempt++) {
         double turn = (this.f_19796_.m_188500_() - 0.5) * 1.2;
         Vec3 direction = this.rotateHorizontal(away, turn);
         double distance = 16.0 + this.f_19796_.m_188500_() * 8.0;
         Vec3 raw = support.m_20182_().m_82549_(direction.m_82490_(distance));
         Vec3 candidate = this.surfacePosition(serverLevel, raw.f_82479_, raw.f_82481_);
         if (this.isValidSupportRetreatPosition(serverLevel, candidate)) {
            return candidate;
         }
      }

      return null;
   }

   public void triggerRangedCounterRetreat(@Nullable LivingEntity threat) {
      if (!this.m_9236_().f_46443_ && threat != null && threat.m_6084_()) {
         this.markSupportingHerobrine();
         this.supportRetreatPanicTicks = Math.max(this.supportRetreatPanicTicks, 90);
         this.supportRepositionCooldown = 0;
         this.m_21563_().m_24960_(threat, 30.0F, 30.0F);
         if (this.m_9236_() instanceof ServerLevel serverLevel) {
            LivingEntity support = this.findGregFollowSupportHerobrine();
            Vec3 retreat = support != null && support.m_6084_()
               ? this.findActiveSupportRetreatPosition(serverLevel, support, threat)
               : this.findDirectRetreatPosition(serverLevel, threat);
            if (retreat != null) {
               this.activeSupportRetreatPos = retreat;
               this.activeSupportRetreatTicks = 90;
               this.m_21573_().m_26519_(retreat.f_82479_, retreat.f_82480_, retreat.f_82481_, 1.3);
            }
         }
      }
   }

   @Nullable
   private Vec3 findDirectRetreatPosition(ServerLevel serverLevel, LivingEntity enemy) {
      Vec3 away = this.horizontalDirection(this.m_20182_().m_82546_(enemy.m_20182_()));
      if (away.m_82556_() < 1.0E-4) {
         away = Vec3.m_82498_(0.0F, this.m_146908_());
      }

      for (int attempt = 0; attempt < 24; attempt++) {
         double turn = (this.f_19796_.m_188500_() - 0.5) * 1.4;
         Vec3 direction = this.rotateHorizontal(away, turn);
         double distance = 16.0 + this.f_19796_.m_188500_() * 8.0;
         Vec3 raw = this.m_20182_().m_82549_(direction.m_82490_(distance));
         Vec3 candidate = this.surfacePosition(serverLevel, raw.f_82479_, raw.f_82481_);
         if (this.isValidSupportRetreatPosition(serverLevel, candidate)) {
            return candidate;
         }
      }

      return null;
   }

   private Vec3 findRetreatReturnEntrance(ServerLevel serverLevel, Vec3 retreat, LivingEntity support, LivingEntity enemy) {
      Vec3 away = this.horizontalDirection(retreat.m_82546_(enemy.m_20182_()));
      Vec3 side = new Vec3(-away.f_82481_, 0.0, away.f_82479_);
      if (side.m_82556_() < 1.0E-4) {
         side = new Vec3(1.0, 0.0, 0.0);
      }

      if (this.f_19796_.m_188499_()) {
         side = side.m_82490_(-1.0);
      }

      for (int attempt = 0; attempt < 8; attempt++) {
         Vec3 raw = retreat.m_82549_(side.m_82490_(2.5 + (double)attempt * 0.75));
         Vec3 candidate = this.surfacePosition(serverLevel, raw.f_82479_, raw.f_82481_);
         if (this.isValidSupportRetreatPosition(serverLevel, candidate)) {
            return candidate;
         }
      }

      return retreat;
   }

   private Vec3 findEnemyFlankReturnExit(ServerLevel serverLevel, LivingEntity enemy, LivingEntity support) {
      Vec3 fromEnemyToSupport = this.horizontalDirection(support.m_20182_().m_82546_(enemy.m_20182_()));
      if (fromEnemyToSupport.m_82556_() < 1.0E-4) {
         fromEnemyToSupport = Vec3.m_82498_(0.0F, enemy.m_146908_());
      }

      Vec3 side = new Vec3(-fromEnemyToSupport.f_82481_, 0.0, fromEnemyToSupport.f_82479_);
      if (this.f_19796_.m_188499_()) {
         side = side.m_82490_(-1.0);
      }

      for (int attempt = 0; attempt < 16; attempt++) {
         double sideDistance = 6.0 + this.f_19796_.m_188500_() * 4.0;
         double backDistance = 2.0 + this.f_19796_.m_188500_() * 4.0;
         Vec3 raw = enemy.m_20182_().m_82549_(side.m_82490_(sideDistance)).m_82549_(fromEnemyToSupport.m_82490_(backDistance));
         Vec3 candidate = this.surfacePosition(serverLevel, raw.f_82479_, raw.f_82481_);
         if (this.isValidSupportRetreatPosition(serverLevel, candidate)) {
            return candidate;
         }
      }

      Vec3 fallbackRaw = enemy.m_20182_().m_82549_(fromEnemyToSupport.m_82490_(6.0));
      return this.surfacePosition(serverLevel, fallbackRaw.f_82479_, fallbackRaw.f_82481_);
   }

   private Vec3 surfacePosition(ServerLevel serverLevel, double x, double z) {
      int y = serverLevel.m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, BlockPos.m_274561_(x, this.m_20186_(), z)).m_123342_();
      return new Vec3(x, (double)y, z);
   }

   private boolean isValidSupportRetreatPosition(ServerLevel serverLevel, Vec3 pos) {
      BlockPos blockPos = BlockPos.m_274446_(pos);
      if (!serverLevel.m_46749_(blockPos) || !serverLevel.m_6857_().m_61937_(blockPos)) {
         return false;
      } else if (serverLevel.m_46859_(blockPos) && serverLevel.m_46859_(blockPos.m_7494_()) && !serverLevel.m_46859_(blockPos.m_7495_())) {
         AABB movedBox = this.m_20191_().m_82383_(pos.m_82546_(this.m_20182_()));
         return serverLevel.m_45756_(this, movedBox);
      } else {
         return false;
      }
   }

   private Vec3 horizontalDirection(Vec3 vector) {
      Vec3 horizontal = new Vec3(vector.f_82479_, 0.0, vector.f_82481_);
      return horizontal.m_82556_() < 1.0E-4 ? Vec3.f_82478_ : horizontal.m_82541_();
   }

   private Vec3 rotateHorizontal(Vec3 vector, double angle) {
      double cos = Math.cos(angle);
      double sin = Math.sin(angle);
      return new Vec3(vector.f_82479_ * cos - vector.f_82481_ * sin, 0.0, vector.f_82479_ * sin + vector.f_82481_ * cos).m_82541_();
   }

   private void tickCombatLowCloneSupportSlots() {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         for (int var5 = 0; var5 < 5; var5++) {
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
      for (int i = 0; i < 5; i++) {
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

   private void summonHerobrine(
      String herobrineMobId, double spawnX, double spawnY, double spawnZ, double summonLookX, double summonLookZ, boolean renderPortal
   ) {
      if (this.m_9236_() instanceof ServerLevel levelaccessor) {
         String[] parts = herobrineMobId.split(":");
         ResourceLocation mobResourceLocation = ResourceLocation.fromNamespaceAndPath(parts[0], parts[1]);
         EntityType<?> type = (EntityType<?>)ForgeRegistries.ENTITY_TYPES.getValue(mobResourceLocation);
         if (type != null && type.m_20615_(this.m_9236_()) instanceof Mob herobrine) {
            if (herobrine instanceof HerobrineMob herobrineMob) {
               herobrineMob.setGregUUID(this.m_20148_());
               herobrineMob.setRenderPortal(renderPortal);
               herobrineMob.setRecallTicks(this.recallTime);
            } else if (herobrine instanceof LowHerobrineCloneEntity lowHerobrineCloneEntity) {
               lowHerobrineCloneEntity.setSummoned(true);
               this.equipGearForLowHerobrineClone(lowHerobrineCloneEntity);
            } else if (herobrine instanceof LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntity) {
               if (renderPortal) {
                  AnnoyingVillagers.PACKET_HANDLER
                     .send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new ClientboundHerobrinePortalFx(new Vec3(spawnX, spawnY, spawnZ)));
               } else {
                  this.equipGearForLowHerobrineClone(lowShadowHerobrineCloneEntity);
               }

               lowShadowHerobrineCloneEntity.setSummoned(true);
            }

            herobrine.m_7678_(spawnX, spawnY, spawnZ, this.m_146908_(), this.m_146909_());
            herobrine.m_7618_(Anchor.EYES, new Vec3(summonLookX, spawnY, summonLookZ));
            herobrine.m_6518_(levelaccessor, levelaccessor.m_6436_(this.m_20183_()), MobSpawnType.MOB_SUMMONED, null, null);
            levelaccessor.m_7967_(herobrine);
            if (this.combatMode) {
               if (this.firstSummonedHerobrineUUID == null) {
                  this.firstSummonedHerobrineUUID = herobrine.m_20148_();
                  this.firstSummonedHerobrine = herobrine;
               } else if (this.secondSummonedHerobrineUUID == null) {
                  this.secondSummonedHerobrineUUID = herobrine.m_20148_();
                  this.secondSummonedHerobrine = herobrine;
               } else {
                  this.thirdSummonedHerobrineUUID = herobrine.m_20148_();
                  this.thirdSummonedHerobrine = herobrine;
               }
            }
         }
      }
   }

   private void spawnHerobrineOffset(String id, double forwardDist, double lateralDist, double baseY, double fx, double fz, double lx, double lz) {
      double spawnX = this.m_20185_() + fx * forwardDist + lx * lateralDist;
      double spawnZ = this.m_20189_() + fz * forwardDist + lz * lateralDist;
      double lookX = spawnX + fx * 10.0;
      double lookZ = spawnZ + fz * 10.0;
      this.summonHerobrine(id, spawnX, baseY, spawnZ, lookX, lookZ, false);
   }

   private void spawnRandomHerobrinesInRadius(String id, int count) {
      if (this.m_9236_() instanceof ServerLevel sl) {
         int var27 = Mth.m_14107_(this.m_20185_());
         int cz = Mth.m_14107_(this.m_20189_());
         ArrayList candidates = new ArrayList();
         short r2 = 400;

         for (int yawRad = -20; yawRad <= 20; yawRad++) {
            for (int dz = -20; dz <= 20; dz++) {
               if ((yawRad != 0 || dz != 0) && yawRad * yawRad + dz * dz <= r2) {
                  int x = var27 + yawRad;
                  int z = cz + dz;
                  int y = sl.m_6924_(Types.MOTION_BLOCKING_NO_LEAVES, x, z);
                  candidates.add(new BlockPos(x, y, z));
               }
            }
         }

         Collections.shuffle(candidates, new Random(this.m_217043_().m_188505_()));
         double yawRad = Math.toRadians((double)this.m_146908_());
         double fx = -Math.sin(yawRad);
         double fz = Math.cos(yawRad);
         int spawned = 0;

         for (BlockPos pos : candidates) {
            if (spawned >= count) {
               break;
            }

            if (sl.m_46749_(pos) && sl.m_6857_().m_61937_(pos) && sl.m_46859_(pos) && sl.m_46859_(pos.m_7494_()) && !sl.m_46859_(pos.m_7495_())) {
               double spawnX = (double)pos.m_123341_() + 0.5;
               double spawnY = (double)pos.m_123342_();
               double spawnZ = (double)pos.m_123343_() + 0.5;
               double lookX = spawnX + fx * 10.0;
               double lookZ = spawnZ + fz * 10.0;
               this.summonHerobrine(id, spawnX, spawnY, spawnZ, lookX, lookZ, true);
               spawned++;
            }
         }
      }
   }

   private void summonEscapeAtDay() {
      this.escapeTiming = 70;
      AnnoyingVillagers.PACKET_HANDLER
         .send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new ClientboundHerobrinePortalFx(this.m_20097_().m_252807_().m_82520_(0.0, 0.5, 0.0)));
      double yawRad = Math.toRadians((double)this.m_146908_());
      double fx = -Math.sin(yawRad);
      double fz = Math.cos(yawRad);
      double lx = Math.cos(yawRad);
      double lz = Math.sin(yawRad);
      double y = this.m_20186_();
      double front = 1.0;
      double side = 1.0;
      String leftHerobrine;
      if (Math.random() <= 0.5) {
         leftHerobrine = "annoyingvillagers:low_herobrine_clone";
      } else {
         leftHerobrine = "annoyingvillagers:low_shadow_herobrine_clone";
      }

      this.spawnHerobrineOffset(leftHerobrine, 0.0, side, y, fx, fz, lx, lz);
      String rightHerobrine;
      if (Math.random() <= 0.5) {
         rightHerobrine = "annoyingvillagers:low_herobrine_clone";
      } else {
         rightHerobrine = "annoyingvillagers:low_shadow_herobrine_clone";
      }

      this.spawnHerobrineOffset(rightHerobrine, 0.0, -side, y, fx, fz, lx, lz);
      if (Math.random() >= 0.7) {
         String frontHerobrine;
         if (Math.random() <= 0.5) {
            frontHerobrine = "annoyingvillagers:low_herobrine_clone";
         } else {
            frontHerobrine = "annoyingvillagers:low_shadow_herobrine_clone";
         }

         this.spawnHerobrineOffset(frontHerobrine, front, 0.0, y, fx, fz, lx, lz);
      }
   }

   private void summonEscapeAtNight() {
      this.escapeTiming = 70;
      AnnoyingVillagers.PACKET_HANDLER
         .send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new ClientboundHerobrinePortalFx(this.m_20097_().m_252807_().m_82520_(0.0, 0.5, 0.0)));
      List<String> herobrines = new ArrayList<>();
      herobrines.add("annoyingvillagers:herobrine_clone");
      herobrines.add("annoyingvillagers:shadow_herobrine_clone");
      herobrines.add("annoyingvillagers:herobrine_chris");
      herobrines.add("annoyingvillagers:herobrine_7");
      herobrines.add("annoyingvillagers:armored_herobrine");
      herobrines.add("annoyingvillagers:low_shadow_herobrine_clone");
      Random random = new Random();
      String herobrineId = herobrines.get(random.nextInt(herobrines.size()));
      if (herobrineId.equals("annoyingvillagers:low_shadow_herobrine_clone")) {
         this.spawnRandomHerobrinesInRadius(herobrineId, new Random().nextInt(10, 20));
      } else {
         double yawRad = Math.toRadians((double)this.m_146908_());
         double fx = -Math.sin(yawRad);
         double fz = Math.cos(yawRad);
         double lx = Math.cos(yawRad);
         double lz = Math.sin(yawRad);
         double y = this.m_20186_();
         double front = 1.0;
         this.spawnHerobrineOffset(herobrineId, front, 0.0, y, fx, fz, lx, lz);
      }
   }

   private HerobrineGregEntity.ElitePattern pickWeightedElitePattern(Random random) {
      double roll = random.nextDouble();
      if (roll <= 0.1F) {
         return HerobrineGregEntity.ElitePattern.THREE_E;
      } else if (roll <= 0.2F) {
         return HerobrineGregEntity.ElitePattern.TWOE_PLUS_1S;
      } else if (roll <= 0.3F) {
         return HerobrineGregEntity.ElitePattern.TWO_E;
      } else if (roll <= 0.4F) {
         return HerobrineGregEntity.ElitePattern.ONEE_PLUS_2S;
      } else {
         return roll <= 0.5 ? HerobrineGregEntity.ElitePattern.ONEE_PLUS_1S : HerobrineGregEntity.ElitePattern.SOLO_1E;
      }
   }

   private void clearSummonSpace(ServerLevel serverLevel) {
      BlockPos center = this.m_20097_();
      int feetY = center.m_123342_();

      for (int dy = 1; dy <= 2; dy++) {
         for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
               BlockPos pos = center.m_7918_(dx, dy, dz);
               BlockState state = serverLevel.m_8055_(pos);
               if (!state.m_60795_() && !(state.m_60800_(serverLevel, pos) < 0.0F)) {
                  serverLevel.m_46953_(pos, true, this);
               }
            }
         }
      }

      for (int dx = -2; dx <= 2; dx++) {
         for (int dzx = -2; dzx <= 2; dzx++) {
            BlockPos pos = new BlockPos(center.m_123341_() + dx, feetY, center.m_123343_() + dzx);
            if (serverLevel.m_8055_(pos).m_60795_()) {
               serverLevel.m_46597_(pos, Blocks.f_50723_.m_49966_());
            }
         }
      }
   }

   private static <T> T pickRandom(List<T> list, Random random) {
      return list.remove(random.nextInt(list.size()));
   }

   private void summonAtNight() {
      List<String> herobrines = new ArrayList<>();
      herobrines.add("annoyingvillagers:shadow_herobrine");
      herobrines.add("annoyingvillagers:elite");
      herobrines.add("annoyingvillagers:null");
      herobrines.add("annoyingvillagers:elite");
      List<String> elites = new ArrayList<>();
      elites.add("annoyingvillagers:swordsman_herobrine");
      elites.add("annoyingvillagers:aegis_herobrine");
      elites.add("annoyingvillagers:glaive_herobrine");
      elites.add("annoyingvillagers:reaper_herobrine");
      elites.add("annoyingvillagers:sledgehammer_herobrine");
      float yaw = this.m_146908_();
      double rad = Math.toRadians((double)yaw);
      double fx = -Math.sin(rad);
      double fz = Math.cos(rad);
      double lx = Math.cos(rad);
      double lz = Math.sin(rad);
      double baseY = this.m_20186_();
      double centerForward = 3.0;
      double side = 1.0;
      double thirdForward = 4.0;
      double centerX = this.m_20185_() + fx * centerForward;
      double centerZ = this.m_20189_() + fz * centerForward;
      double lookX = centerX + fx * 10.0;
      double lookZ = centerZ + fz * 10.0;
      this.m_7618_(Anchor.EYES, new Vec3(centerX, baseY, centerZ));
      if (this.m_9236_() instanceof ServerLevel) {
         AnnoyingVillagers.PACKET_HANDLER
            .send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new ClientboundHerobrinePortalFx(new Vec3(centerX, baseY, centerZ)));
         Random random = new Random();
         String pick = herobrines.get(random.nextInt(herobrines.size()));
         if (!pick.equals("annoyingvillagers:shadow_herobrine") && !pick.equals("annoyingvillagers:null")) {
            HerobrineGregEntity.ElitePattern pattern = this.pickWeightedElitePattern(random);
            switch (pattern) {
               case SOLO_1E:
                  this.summonHerobrine(pickRandom(elites, random), centerX, baseY, centerZ, lookX, lookZ, false);
                  break;
               case ONEE_PLUS_1S:
                  this.spawnHerobrineOffset(pickRandom(elites, random), centerForward, side, baseY, fx, fz, lx, lz);
                  this.spawnHerobrineOffset("annoyingvillagers:low_shadow_herobrine_clone", centerForward, -side, baseY, fx, fz, lx, lz);
                  break;
               case ONEE_PLUS_2S:
                  this.spawnHerobrineOffset("annoyingvillagers:low_shadow_herobrine_clone", centerForward, side, baseY, fx, fz, lx, lz);
                  this.spawnHerobrineOffset("annoyingvillagers:low_shadow_herobrine_clone", centerForward, -side, baseY, fx, fz, lx, lz);
                  this.spawnHerobrineOffset(pickRandom(elites, random), thirdForward, 0.0, baseY, fx, fz, lx, lz);
                  break;
               case TWO_E:
                  this.spawnHerobrineOffset(pickRandom(elites, random), centerForward, side, baseY, fx, fz, lx, lz);
                  this.spawnHerobrineOffset(pickRandom(elites, random), centerForward, -side, baseY, fx, fz, lx, lz);
                  break;
               case TWOE_PLUS_1S:
                  this.spawnHerobrineOffset("annoyingvillagers:low_shadow_herobrine_clone", centerForward, side, baseY, fx, fz, lx, lz);
                  this.spawnHerobrineOffset(pickRandom(elites, random), centerForward, -side, baseY, fx, fz, lx, lz);
                  this.spawnHerobrineOffset(pickRandom(elites, random), thirdForward, 0.0, baseY, fx, fz, lx, lz);
                  break;
               case THREE_E:
                  this.spawnHerobrineOffset(pickRandom(elites, random), centerForward, side, baseY, fx, fz, lx, lz);
                  this.spawnHerobrineOffset(pickRandom(elites, random), centerForward, -side, baseY, fx, fz, lx, lz);
                  this.spawnHerobrineOffset(pickRandom(elites, random), thirdForward, 0.0, baseY, fx, fz, lx, lz);
            }
         } else {
            this.summonHerobrine(pick, centerX, baseY, centerZ, lookX, lookZ, false);
         }
      }
   }

   private void summonHerobrines() {
      if (this.getLivingEntityPatch() != null) {
         this.getLivingEntityPatch().playAnimationSynchronized(AnimsSculkSteve.PORTAL_SUMMON, 0.0F);
      }

      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         this.clearSummonSpace(serverLevel);
      }

      this.m_20340_(false);
      this.setUseHerobrineTexture(true);
      this.summonAtNight();
      this.summonTiming = -2;
   }

   private void summonHerobrinesAndEscape() {
      if (this.getLivingEntityPatch() != null) {
         this.getLivingEntityPatch().playAnimationSynchronized(AnimsSculkSteve.PORTAL_SUMMON, 0.0F);
      }

      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         this.clearSummonSpace(serverLevel);
      }

      if (this.isDay(this.m_9236_())) {
         this.summonEscapeAtDay();
      } else {
         this.summonEscapeAtNight();
      }
   }

   public boolean m_6785_(double d0) {
      return false;
   }

   @NotNull
   public SoundEvent m_7975_(@NotNull DamageSource damagesource) {
      return Objects.requireNonNull(
         (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "entity.generic.hurt"))
      );
   }

   @NotNull
   public SoundEvent m_5592_() {
      return Objects.requireNonNull(
         (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "entity.generic.death"))
      );
   }

   public boolean isDay(Level level) {
      long timeOfDay = level.m_46468_() % 24000L;
      return timeOfDay >= 0L && timeOfDay < 13000L;
   }

   public boolean m_6469_(@NotNull DamageSource pSource, float f) {
      if (pSource.m_276093_(DamageTypes.f_268724_)) {
         return super.m_6469_(pSource, f);
      } else if (this.shouldBlockPortalSummonDamage()) {
         this.blockPortalSummonDamage(pSource);
         return false;
      } else {
         if (this.escapeTiming < 0) {
            this.markSupportPanicFromHit(pSource);
         }

         if (this.fishingHookCancelledEscape) {
            return super.m_6469_(pSource, 1.0F);
         } else if (this.m_21223_() != 1.0F && !this.combatMode) {
            return super.m_6469_(pSource, 1.0F);
         } else {
            if (this.m_9236_() instanceof ServerLevel serverLevel) {
               EpicfightUtil.damageBlocked(pSource, this, serverLevel);
            }

            return false;
         }
      }
   }

   private boolean shouldBlockPortalSummonDamage() {
      return this.summoning;
   }

   private void blockPortalSummonDamage(DamageSource source) {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         EpicfightUtil.damageBlocked(source, this, serverLevel);
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

   private void markSupportPanicFromHit(DamageSource source) {
      if (!this.m_9236_().f_46443_ && !source.m_276093_(DamageTypes.f_268724_)) {
         this.supportRetreatPanicTicks = Math.max(this.supportRetreatPanicTicks, 90);
         this.supportRepositionCooldown = Math.min(this.supportRepositionCooldown, 1 + this.f_19796_.m_188503_(20));
      }
   }

   protected void m_7472_(@NotNull DamageSource damageSource, int looting, boolean recentlyHit) {
      super.m_7472_(damageSource, looting, recentlyHit);
      if (this.escapeTiming >= 0 || this.fishingHookCancelledEscape) {
         this.m_19983_(new ItemStack((ItemLike)AnnoyingVillagersModItems.TRANSPORTER_FRAGMENT.get()));
      }
   }

   private ItemStack randomDamage(ItemStack itemStack) {
      int maxDamage = itemStack.m_41776_();
      itemStack.m_41721_(new Random().nextInt(maxDamage / 3, maxDamage * 3 / 4));
      return itemStack;
   }

   private void equipGearForLowHerobrineClone(Entity entity) {
      if (entity instanceof LowHerobrineCloneEntity || entity instanceof LowShadowHerobrineCloneEntity) {
         if (this.f_19796_.m_188501_() < 0.3F) {
            entity.m_8061_(EquipmentSlot.HEAD, this.randomDamage(new ItemStack((ItemLike)AnnoyingVillagersModItems.BROKEN_DIAMOND_HELMET.get())));
         }

         if (this.f_19796_.m_188501_() < 0.3F) {
            entity.m_8061_(EquipmentSlot.CHEST, this.randomDamage(new ItemStack((ItemLike)AnnoyingVillagersModItems.BROKEN_DIAMOND_CHESTPLATE.get())));
         }

         if (this.f_19796_.m_188501_() < 0.3F) {
            entity.m_8061_(EquipmentSlot.LEGS, this.randomDamage(new ItemStack((ItemLike)AnnoyingVillagersModItems.BROKEN_DIAMOND_LEGGINGS.get())));
         }

         if (this.f_19796_.m_188501_() < 0.3F) {
            entity.m_8061_(EquipmentSlot.FEET, this.randomDamage(new ItemStack((ItemLike)AnnoyingVillagersModItems.BROKEN_DIAMOND_BOOTS.get())));
         }

         entity.m_8061_(EquipmentSlot.MAINHAND, this.randomDamage(new ItemStack((ItemLike)listWeapons.get(this.f_19796_.m_188503_(listWeapons.size())))));
      }
   }

   public SpawnGroupData m_6518_(
      @NotNull ServerLevelAccessor serverlevelaccessor,
      @NotNull DifficultyInstance difficultyinstance,
      @NotNull MobSpawnType mobspawntype,
      @Nullable SpawnGroupData spawngroupdata,
      @Nullable CompoundTag compoundtag
   ) {
      if (mobspawntype == MobSpawnType.NATURAL || mobspawntype == MobSpawnType.CHUNK_GENERATION) {
         ServerLevel serverLevel = serverlevelaccessor.m_6018_();
         GregData gregData = GregData.get(serverLevel);
         if (!gregData.tryClaim(serverLevel, this.m_20148_())) {
            this.m_146870_();
            return null;
         }

         BlockPos blockPos = this.m_20097_();
         int surfaceY = serverLevel.m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, blockPos).m_123342_();
         BlockPos spawnPos = new BlockPos(blockPos.m_123341_(), surfaceY, blockPos.m_123343_());
         this.m_20035_(spawnPos, this.m_146908_(), this.m_146909_());
      }

      ChatUtil.joinGame(this, "Greg");
      return super.m_6518_(serverlevelaccessor, difficultyinstance, mobspawntype, spawngroupdata, compoundtag);
   }

   public void m_5993_(@NotNull Entity entity, int i, @NotNull DamageSource damagesource) {
      super.m_5993_(entity, i, damagesource);
   }

   public void m_6075_() {
      super.m_6075_();
   }

   public void m_7378_(@NotNull CompoundTag pCompound) {
      super.m_7378_(pCompound);
      this.setWhiteEye(pCompound.m_128471_("WhiteEye"));
      this.setUseHerobrineTexture(pCompound.m_128471_("UseHerobrineTexture"));
      this.summoning = pCompound.m_128471_("Summoning");
      this.summonTiming = pCompound.m_128451_("SummonTiming");
      this.escapeTiming = pCompound.m_128451_("EscapeTiming");
      this.summonTimestamp = pCompound.m_128451_("SummonTimestamp");
      this.combatMode = pCompound.m_128471_("CombatMode");
      this.recallTime = pCompound.m_128451_("RecallTime");
      this.fishingHookCancelledEscape = pCompound.m_128471_("FishingHookCancelledEscape");
      this.lowCloneSupportCooldown = pCompound.m_128451_("LowCloneSupportCooldown");
      this.portalPairCooldown = pCompound.m_128451_("PortalPairCooldown");
      this.rangedCounterPortalCooldown = pCompound.m_128451_("RangedCounterPortalCooldown");
      this.supportEscapePortalCooldown = pCompound.m_128451_("SupportEscapePortalCooldown");
      this.portalEscapeStepBackCooldown = pCompound.m_128451_("PortalEscapeStepBackCooldown");
      this.sixPortalSupportCooldown = pCompound.m_128451_("SixPortalSupportCooldown");
      this.hookedWaitingForGround = pCompound.m_128471_("HookedWaitingForGround");
      this.hookedLeftGround = pCompound.m_128471_("HookedLeftGround");
      this.setHooked(pCompound.m_128471_("Hooked"));
      if (this.isHooked() && this.hookedWaitingForGround) {
         this.releaseHookedPhysicsUntilGround();
      }

      if (pCompound.m_128403_("FirstSummonedHerobrineUUID")) {
         this.firstSummonedHerobrineUUID = pCompound.m_128342_("FirstSummonedHerobrineUUID");
      }

      if (pCompound.m_128403_("SecondSummonedHerobrineUUID")) {
         this.secondSummonedHerobrineUUID = pCompound.m_128342_("SecondSummonedHerobrineUUID");
      }

      if (pCompound.m_128403_("ThirdSummonedHerobrineUUID")) {
         this.thirdSummonedHerobrineUUID = pCompound.m_128342_("ThirdSummonedHerobrineUUID");
      }

      for (int i = 0; i < 5; i++) {
         String key = "CombatLowCloneSupportUUID" + i;
         if (pCompound.m_128403_(key)) {
            this.combatLowCloneSupportUUIDs[i] = pCompound.m_128342_(key);
         }
      }
   }

   public void m_7380_(@NotNull CompoundTag pCompound) {
      super.m_7380_(pCompound);
      pCompound.m_128379_("WhiteEye", this.isWhiteEye());
      pCompound.m_128379_("UseHerobrineTexture", this.isUseHerobrineTexture());
      pCompound.m_128379_("Summoning", this.summoning);
      pCompound.m_128405_("SummonTiming", this.summonTiming);
      pCompound.m_128405_("EscapeTiming", this.escapeTiming);
      pCompound.m_128405_("SummonTimestamp", this.summonTimestamp);
      pCompound.m_128379_("CombatMode", this.combatMode);
      pCompound.m_128405_("RecallTime", this.recallTime);
      pCompound.m_128379_("FishingHookCancelledEscape", this.fishingHookCancelledEscape);
      pCompound.m_128405_("LowCloneSupportCooldown", this.lowCloneSupportCooldown);
      pCompound.m_128405_("PortalPairCooldown", this.portalPairCooldown);
      pCompound.m_128405_("RangedCounterPortalCooldown", this.rangedCounterPortalCooldown);
      pCompound.m_128405_("SupportEscapePortalCooldown", this.supportEscapePortalCooldown);
      pCompound.m_128405_("PortalEscapeStepBackCooldown", this.portalEscapeStepBackCooldown);
      pCompound.m_128405_("SixPortalSupportCooldown", this.sixPortalSupportCooldown);
      pCompound.m_128379_("Hooked", this.isHooked());
      pCompound.m_128379_("HookedWaitingForGround", this.hookedWaitingForGround);
      pCompound.m_128379_("HookedLeftGround", this.hookedLeftGround);
      if (this.firstSummonedHerobrineUUID != null) {
         pCompound.m_128362_("FirstSummonedHerobrineUUID", this.firstSummonedHerobrineUUID);
      }

      if (this.secondSummonedHerobrineUUID != null) {
         pCompound.m_128362_("SecondSummonedHerobrineUUID", this.secondSummonedHerobrineUUID);
      }

      if (this.thirdSummonedHerobrineUUID != null) {
         pCompound.m_128362_("ThirdSummonedHerobrineUUID", this.thirdSummonedHerobrineUUID);
      }

      for (int i = 0; i < 5; i++) {
         if (this.combatLowCloneSupportUUIDs[i] != null) {
            pCompound.m_128362_("CombatLowCloneSupportUUID" + i, this.combatLowCloneSupportUUIDs[i]);
         }
      }
   }

   public static boolean canSpawn(
      EntityType<HerobrineGregEntity> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos position, RandomSource random
   ) {
      ServerLevel serverLevel = level.m_6018_();
      int passesDay = (int)(serverLevel.m_46467_() / 24000L);
      if (passesDay % 5 != 0) {
         return false;
      } else {
         return GregData.get(serverLevel).isOccupied(serverLevel) ? false : Monster.m_219013_(entityType, level, spawnType, position, random);
      }
   }

   public void m_142687_(@NotNull RemovalReason reason) {
      super.m_142687_(reason);
      if (!this.m_9236_().f_46443_
         && this.m_9236_() instanceof ServerLevel serverLevel
         && (reason == RemovalReason.KILLED || reason == RemovalReason.DISCARDED)) {
         GregData.get(serverLevel).releaseIfMatches(serverLevel, this.m_20148_());
      }
   }

   public static Builder createAttributes() {
      Builder builder = Mob.m_21552_();
      builder = builder.m_22268_(Attributes.f_22279_, 0.5);
      builder = builder.m_22268_(Attributes.f_22276_, 40.0);
      builder = builder.m_22268_(Attributes.f_22284_, 0.0);
      builder = builder.m_22268_(Attributes.f_22281_, 0.0);
      builder = builder.m_22268_(Attributes.f_22277_, 48.0);
      return builder.m_22268_(Attributes.f_22278_, 1.0);
   }

   private static enum ElitePattern {
      SOLO_1E,
      ONEE_PLUS_1S,
      ONEE_PLUS_2S,
      TWO_E,
      TWOE_PLUS_1S,
      THREE_E;
   }
}
