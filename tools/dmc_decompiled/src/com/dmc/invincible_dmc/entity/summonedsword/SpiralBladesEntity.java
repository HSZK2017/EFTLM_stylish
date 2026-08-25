package com.dmc.invincible_dmc.entity.summonedsword;

import com.dmc.invincible_dmc.damagesource.DMCDamageTypeTags;
import com.dmc.invincible_dmc.entity.DMCEntities;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.entity.summonedsword.motion.SummonedSwordMotionController;
import com.dmc.invincible_dmc.entity.summonedsword.motion.SummonedSwordMotionMode;
import com.dmc.invincible_dmc.gameassets.animations.yamato.SummonedSwordAnimations;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import com.dmc.invincible_dmc.utils.DamageFilterUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.utils.HitEntityList;
import yesman.epicfight.api.utils.HitEntityList.Priority;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.DodgeLocationIndicator;

public class SpiralBladesEntity extends Entity implements SummonedSwordMotionController {
   private static final float BASE_ROTATION_SPEED = 8.5F;
   private static final float SWEEPING_SPEED_MULTIPLIER = 1.3F;
   private static final int BASE_SWORD_DURABILITY = 5;
   private static final int BLADE_RUSH_FINISHER_SOUND_COOLDOWN = 2;
   private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.m_135353_(SpiralBladesEntity.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> TOTAL_SWORDS = SynchedEntityData.m_135353_(SpiralBladesEntity.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Byte> CONTROLLER_STATE = SynchedEntityData.m_135353_(SpiralBladesEntity.class, EntityDataSerializers.f_135027_);
   private static final EntityDataAccessor<Integer> FROZEN_TICK = SynchedEntityData.m_135353_(SpiralBladesEntity.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Long> ORBIT_START_GAME_TIME = SynchedEntityData.m_135353_(SpiralBladesEntity.class, EntityDataSerializers.f_244073_);
   private static final EntityDataAccessor<Float> ROTATION_SPEED = SynchedEntityData.m_135353_(SpiralBladesEntity.class, EntityDataSerializers.f_135029_);
   private double orbitRadius = 2.0;
   private LivingEntity ownerRef;
   private UUID ownerUUID;
   private final List<UUID> childSwords = new ArrayList<>();
   private final Map<UUID, Integer> swordFormationIndices = new HashMap<>();
   private final Map<UUID, Integer> swordDamageUses = new HashMap<>();
   private final Map<UUID, Vec3> lastDamageSamplePositions = new HashMap<>();
   private final Map<UUID, Set<UUID>> activeDamageContacts = new HashMap<>();
   private int swordDurability = 5;
   private boolean infiniteSwordDurability = false;
   private long nextBladeRushFinisherSoundTick;
   private int preLaunchTimer = 2;
   private boolean launchArmed = false;
   private int frozenTickCount = 0;

   public SpiralBladesEntity(EntityType<?> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.f_19811_ = true;
      this.f_19794_ = true;
   }

   public SpiralBladesEntity(Level level, LivingEntity owner) {
      this((EntityType<?>)DMCEntities.SPIRAL_BLADES.get(), level);
      this.setOwner(owner);
      this.captureWeaponEnchantments(owner);
      this.f_19794_ = true;
      this.f_19811_ = true;
      this.m_6034_(owner.m_20185_(), owner.m_20186_(), owner.m_20189_());
      this.f_19804_.m_135381_(ORBIT_START_GAME_TIME, level.m_46467_());
   }

   public static void summon(Level level, LivingEntity owner) {
      SummonedSwordSpawner.spiral(level, owner);
   }

   @Nullable
   public static SpiralBladesEntity getExisting(LivingEntity owner) {
      if (owner.m_9236_().f_46443_) {
         return null;
      } else {
         for (Entity e : owner.m_9236_().m_142646_().m_142273_()) {
            if (e instanceof SpiralBladesEntity sb && sb.m_6084_() && sb.ownerUUID != null && sb.ownerUUID.equals(owner.m_20148_())) {
               return sb;
            }
         }

         return null;
      }
   }

   public static void triggerLaunch(LivingEntity owner) {
      if (!owner.m_9236_().f_46443_) {
         SpiralBladesEntity existing = getExisting(owner);
         if (existing != null && existing.getCurrentState() == SpiralBladesEntity.State.STOPPED) {
            existing.armForLaunch();
         }
      }
   }

   public void stopRotation() {
      if (this.getCurrentState() == SpiralBladesEntity.State.STANDBY) {
         this.frozenTickCount = (int)this.getOrbitTick();
         this.f_19804_.m_135381_(FROZEN_TICK, this.frozenTickCount);
         this.setCurrentState(SpiralBladesEntity.State.STOPPED);
      }
   }

   private void armForLaunch() {
      this.launchArmed = true;
      this.preLaunchTimer = 2;
   }

   public SpiralBladesEntity.State getCurrentState() {
      return SpiralBladesEntity.State.values()[this.f_19804_.m_135370_(CONTROLLER_STATE)];
   }

   private void setCurrentState(SpiralBladesEntity.State state) {
      this.f_19804_.m_135381_(CONTROLLER_STATE, (byte)state.ordinal());
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(OWNER_ID, -1);
      this.f_19804_.m_135372_(TOTAL_SWORDS, 9);
      this.f_19804_.m_135372_(CONTROLLER_STATE, (byte)SpiralBladesEntity.State.STANDBY.ordinal());
      this.f_19804_.m_135372_(FROZEN_TICK, 0);
      this.f_19804_.m_135372_(ORBIT_START_GAME_TIME, 0L);
      this.f_19804_.m_135372_(ROTATION_SPEED, 8.5F);
   }

   private void captureWeaponEnchantments(LivingEntity owner) {
      ItemStack weapon = owner.m_21205_();
      int unbreakingLevel = EnchantmentHelper.m_44843_(Enchantments.f_44986_, weapon);
      this.swordDurability = 5 + unbreakingLevel * 3;
      this.infiniteSwordDurability = EnchantmentHelper.m_44843_(Enchantments.f_44962_, weapon) > 0;
      boolean hasSweepingEdge = EnchantmentHelper.m_44843_(Enchantments.f_44983_, weapon) > 0;
      this.f_19804_.m_135381_(ROTATION_SPEED, 8.5F * (hasSweepingEdge ? 1.3F : 1.0F));
   }

   public void setOwner(LivingEntity owner) {
      this.ownerRef = owner;
      this.ownerUUID = owner.m_20148_();
      this.f_19804_.m_135381_(OWNER_ID, owner.m_19879_());
   }

   @Nullable
   public LivingEntity getOwner() {
      if (this.ownerRef != null && this.ownerRef.m_6084_()) {
         return this.ownerRef;
      } else {
         if (!this.m_9236_().f_46443_ && this.ownerUUID != null) {
            if (((ServerLevel)this.m_9236_()).m_8791_(this.ownerUUID) instanceof LivingEntity living) {
               this.ownerRef = living;
               return living;
            }
         } else if (this.m_9236_().f_46443_) {
            int id = (Integer)this.f_19804_.m_135370_(OWNER_ID);
            if (id != -1 && this.m_9236_().m_6815_(id) instanceof LivingEntity living) {
               this.ownerRef = living;
               return living;
            }
         }

         return null;
      }
   }

   public int getTotalSwords() {
      return (Integer)this.f_19804_.m_135370_(TOTAL_SWORDS);
   }

   void spawnChildSwords() {
      LivingEntity owner = this.getOwner();
      if (owner != null && this.m_9236_() instanceof ServerLevel serverLevel) {
         int var7 = this.getTotalSwords();

         for (int i = 0; i < var7; i++) {
            DMCSummonedSwordEntity sword = SummonedSwordSpawner.createSword(this.m_9236_(), owner, 1.2F, true);
            if (sword != null) {
               sword.setLifetimeTicks(Integer.MAX_VALUE);
               sword.setNoAim(true);
               sword.setSpiral(true);
               this.bindSwordMotion(sword, i);
               Vec3 spawnPos = sword.m_20182_();
               serverLevel.m_8767_(ParticleTypes.f_123751_, spawnPos.f_82479_, spawnPos.f_82480_, spawnPos.f_82481_, 3, 0.05, 0.05, 0.05, 0.0);
               this.m_9236_().m_7967_(sword);
               this.childSwords.add(sword.m_20148_());
               this.swordFormationIndices.put(sword.m_20148_(), i);
               this.swordDamageUses.put(sword.m_20148_(), 0);
               this.lastDamageSamplePositions.put(sword.m_20148_(), spawnPos);
               this.activeDamageContacts.put(sword.m_20148_(), new HashSet<>());
            }
         }

         owner.m_5496_(SoundEvents.f_12558_, 0.5F, 1.5F);
      } else {
         this.m_146870_();
      }
   }

   public void m_8119_() {
      super.m_8119_();
      LivingEntity owner = this.getOwner();
      if (owner != null) {
         this.m_6034_(owner.m_20185_(), owner.m_20186_(), owner.m_20189_());
         if (!this.m_9236_().f_46443_) {
            if (owner instanceof Player player && VergilSkill.NotHoldingYamato(player)) {
               this.cleanup();
               return;
            }

            if (owner.m_21224_()) {
               this.cleanup();
            } else {
               switch (this.getCurrentState()) {
                  case STANDBY:
                     this.tickStandby();
                     break;
                  case STOPPED:
                     this.tickStopped();
                  case FINISHED:
               }
            }
         }
      }
   }

   private void tickStandby() {
      this.pruneChildSwords();
      this.dealAreaDamage();
   }

   private void tickStopped() {
      this.pruneChildSwords();
      if (this.launchArmed) {
         this.preLaunchTimer--;
         if (this.preLaunchTimer <= 0) {
            this.prepareToLaunch();
         }
      }
   }

   private boolean dealAreaDamage() {
      LivingEntity owner = this.getOwner();
      if (owner == null) {
         return false;
      } else {
         LivingEntityPatch<?> ownerPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(owner, LivingEntityPatch.class);
         if (ownerPatch == null) {
            return false;
         } else {
            ServerLevel serverLevel = (ServerLevel)this.m_9236_();
            Map<UUID, UUID> hitSwordByTarget = new HashMap<>();
            Map<UUID, Double> hitDistanceByTarget = new HashMap<>();
            Map<UUID, Set<UUID>> currentDamageContacts = new HashMap<>();
            List<LivingEntity> rawTargets = new ArrayList<>();

            for (UUID swordUUID : this.childSwords) {
               Entity swordEntity = serverLevel.m_8791_(swordUUID);
               if (swordEntity instanceof DMCSummonedSwordEntity) {
                  DMCSummonedSwordEntity sword = (DMCSummonedSwordEntity)swordEntity;
                  if (sword.m_6084_() && sword.isInStandby()) {
                     Vec3 currentPosition = sword.m_20182_();
                     Vec3 previousPosition = this.lastDamageSamplePositions.put(swordUUID, currentPosition);
                     if (previousPosition == null) {
                        previousPosition = new Vec3(sword.f_19854_, sword.f_19855_, sword.f_19856_);
                     }

                     AABB currentBox = sword.m_20191_();
                     AABB sweptBox = currentBox.m_82367_(currentBox.m_82383_(previousPosition.m_82546_(currentPosition))).m_82400_(0.25);
                     List<LivingEntity> swordHits = this.m_9236_()
                        .m_6443_(
                           LivingEntity.class,
                           sweptBox,
                           entity -> entity != owner
                                 && entity.m_6084_()
                                 && !entity.m_5833_()
                                 && !(entity instanceof DodgeLocationIndicator)
                                 && !(entity instanceof DMCSummonedSwordEntity)
                                 && (!(entity instanceof DoppelgangerEntity d) || d.getOwner() == null || d.getOwner() != owner)
                        );
                     Set<UUID> previousContacts = this.activeDamageContacts.getOrDefault(swordUUID, Collections.emptySet());
                     Set<UUID> currentContacts = currentDamageContacts.computeIfAbsent(swordUUID, ignored -> new HashSet<>());

                     for (LivingEntity hit : swordHits) {
                        AABB targetBox = hit.m_20191_()
                           .m_82377_((double)sword.m_20205_() * 0.5 + 0.25, (double)sword.m_20206_() * 0.5 + 0.25, (double)sword.m_20205_() * 0.5 + 0.25);
                        if (targetBox.m_82390_(previousPosition)
                           || targetBox.m_82390_(currentPosition)
                           || !targetBox.m_82371_(previousPosition, currentPosition).isEmpty()) {
                           UUID targetUUID = hit.m_20148_();
                           if (targetBox.m_82390_(currentPosition)) {
                              currentContacts.add(targetUUID);
                           }

                           if (!previousContacts.contains(targetUUID)) {
                              double distanceSqr = distanceToSegmentSqr(hit.m_20191_().m_82399_(), previousPosition, currentPosition);
                              Double previousDistance = hitDistanceByTarget.get(targetUUID);
                              if (previousDistance == null) {
                                 rawTargets.add(hit);
                              }

                              if (previousDistance == null || distanceSqr < previousDistance) {
                                 hitDistanceByTarget.put(targetUUID, distanceSqr);
                                 hitSwordByTarget.put(targetUUID, swordUUID);
                              }
                           }
                        }
                     }
                  }
               }
            }

            this.activeDamageContacts.clear();
            this.activeDamageContacts.putAll(currentDamageContacts);
            if (rawTargets.isEmpty()) {
               return false;
            } else {
               List<Entity> rawEntities = new ArrayList<>(rawTargets);
               ArrayList<LivingEntity> sorted = new ArrayList<>();
               HitEntityList targetFirst = new HitEntityList(ownerPatch, rawEntities, Priority.TARGET);

               while (targetFirst.next()) {
                  if (targetFirst.getEntity() instanceof LivingEntity le) {
                     sorted.add(le);
                  }
               }

               HitEntityList distanceOrder = new HitEntityList(ownerPatch, rawEntities, Priority.DISTANCE);

               while (distanceOrder.next()) {
                  Entity var34 = distanceOrder.getEntity();
                  if (var34 instanceof LivingEntity) {
                     LivingEntity le = (LivingEntity)var34;
                     if (!sorted.contains(le)) {
                        sorted.add(le);
                     }
                  }
               }

               float baseArmorNeg = ownerPatch.getArmorNegation(InteractionHand.MAIN_HAND);
               float baseImpact = ownerPatch.getImpact(InteractionHand.MAIN_HAND);
               float baseDamage = (float)owner.m_21133_(Attributes.f_22281_);
               boolean anyHit = false;
               boolean canPlayHitSound = serverLevel.m_46467_() >= this.nextBladeRushFinisherSoundTick;
               Map<UUID, Integer> successfulHitsBySword = new HashMap<>();

               for (LivingEntity target : sorted) {
                  if (!DamageFilterUtils.shouldSkipTarget(owner, target)) {
                     EpicFightDamageSource ds = EpicFightDamageSources.mobAttack(owner)
                        .addRuntimeTag(DMCSummonedSwordPatch.SUMMONED_SWORD_DAMAGE)
                        .addRuntimeTag(DMCSummonedSwordPatch.SPIRAL_SWORD_DAMAGE)
                        .addRuntimeTag(DMCDamageTypeTags.NOT_CHARGE)
                        .setAnimation(SummonedSwordAnimations.SUMMONED_SWORD)
                        .setBaseArmorNegation(baseArmorNeg)
                        .setBaseImpact(baseImpact)
                        .setUsedItem(owner.m_21120_(InteractionHand.MAIN_HAND))
                        .setStunType(StunType.SHORT);
                     int prevInvul = target.f_19802_;
                     target.f_19802_ = 0;
                     if (target.m_6469_(ds, baseDamage)) {
                        anyHit = true;
                        owner.m_21335_(target);
                        UUID swordUUIDx = hitSwordByTarget.get(target.m_20148_());
                        if (swordUUIDx != null) {
                           successfulHitsBySword.merge(swordUUIDx, 1, Integer::sum);
                        }

                        if (canPlayHitSound) {
                           target.m_5496_((SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get(), 0.6F, 1.0F);
                           this.nextBladeRushFinisherSoundTick = serverLevel.m_46467_() + 2L;
                           canPlayHitSound = false;
                        }
                     }

                     target.f_19802_ = prevInvul;
                  }
               }

               this.applySwordDurabilityDamage(successfulHitsBySword);
               return anyHit;
            }
         }
      }
   }

   private static double distanceToSegmentSqr(Vec3 point, Vec3 start, Vec3 end) {
      Vec3 segment = end.m_82546_(start);
      double lengthSqr = segment.m_82556_();
      if (lengthSqr <= 1.0E-7) {
         return point.m_82557_(start);
      } else {
         double progress = Mth.m_14008_(point.m_82546_(start).m_82526_(segment) / lengthSqr, 0.0, 1.0);
         return point.m_82557_(start.m_82549_(segment.m_82490_(progress)));
      }
   }

   private void applySwordDurabilityDamage(Map<UUID, Integer> successfulHitsBySword) {
      if (!this.infiniteSwordDurability && !successfulHitsBySword.isEmpty()) {
         List<UUID> brokenSwords = new ArrayList<>();
         successfulHitsBySword.forEach((swordUUID, hitCount) -> {
            int uses = this.swordDamageUses.getOrDefault(swordUUID, 0) + hitCount;
            if (uses >= this.swordDurability) {
               brokenSwords.add(swordUUID);
            } else {
               this.swordDamageUses.put(swordUUID, uses);
            }
         });
         brokenSwords.forEach(this::removeSword);
      }
   }

   private void removeSword(UUID swordUUID) {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         Entity sword = serverLevel.m_8791_(swordUUID);
         if (sword != null) {
            sword.m_146870_();
         }
      }

      this.childSwords.remove(swordUUID);
      this.swordFormationIndices.remove(swordUUID);
      this.swordDamageUses.remove(swordUUID);
      this.lastDamageSamplePositions.remove(swordUUID);
      this.activeDamageContacts.remove(swordUUID);
      if (this.childSwords.isEmpty()) {
         this.cleanup();
      }
   }

   private void prepareToLaunch() {
      for (UUID swordUUID : this.childSwords) {
         if (((ServerLevel)this.m_9236_()).m_8791_(swordUUID) instanceof DMCSummonedSwordEntity sword) {
            this.releaseSwordMotion(sword);
            sword.setLifetimeTicks(80);
            sword.launchAlongCurrentRotation();
            sword.m_5496_(SoundEvents.f_12520_, 0.8F, 1.4F);
         }
      }

      this.childSwords.clear();
      this.swordFormationIndices.clear();
      this.swordDamageUses.clear();
      this.lastDamageSamplePositions.clear();
      this.activeDamageContacts.clear();
      this.m_146870_();
   }

   private void pruneChildSwords() {
      ServerLevel serverLevel = (ServerLevel)this.m_9236_();
      List<UUID> toRemove = new ArrayList<>();

      for (UUID swordUUID : this.childSwords) {
         Entity entity = serverLevel.m_8791_(swordUUID);
         if (entity instanceof DMCSummonedSwordEntity) {
            DMCSummonedSwordEntity sword = (DMCSummonedSwordEntity)entity;
            if (!sword.m_6084_()) {
               toRemove.add(swordUUID);
               this.swordFormationIndices.remove(swordUUID);
            } else if (sword.isInStandby()) {
               Integer formationIndex = this.swordFormationIndices.get(swordUUID);
               if (formationIndex != null && !sword.isManagedBy(this)) {
                  this.bindSwordMotion(sword, formationIndex);
               }
            }
         } else if (this.f_19797_ > 5) {
            toRemove.add(swordUUID);
            this.swordFormationIndices.remove(swordUUID);
         }
      }

      this.childSwords.removeAll(toRemove);
      toRemove.forEach(uuid -> {
         this.swordDamageUses.remove(uuid);
         this.lastDamageSamplePositions.remove(uuid);
         this.activeDamageContacts.remove(uuid);
      });
   }

   @Override
   public SummonedSwordMotionMode getMotionMode() {
      return SummonedSwordMotionMode.SPIRAL_ORBIT;
   }

   public long getMotionTick(boolean previous) {
      long tick = this.getCurrentState() == SpiralBladesEntity.State.STOPPED
         ? (long)((Integer)this.f_19804_.m_135370_(FROZEN_TICK)).intValue()
         : this.getOrbitTick() - (previous ? 1L : 0L);
      return Math.max(0L, tick);
   }

   public double getMotionRadius() {
      return this.orbitRadius;
   }

   public float getMotionRotationSpeed() {
      return (Float)this.f_19804_.m_135370_(ROTATION_SPEED);
   }

   private long getOrbitTick() {
      return Math.max(0L, this.m_9236_().m_46467_() - (Long)this.f_19804_.m_135370_(ORBIT_START_GAME_TIME));
   }

   private void cleanup() {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         for (UUID swordUUID : this.childSwords) {
            Entity sword = serverLevel.m_8791_(swordUUID);
            if (sword != null) {
               sword.m_146870_();
            }
         }
      }

      this.childSwords.clear();
      this.swordFormationIndices.clear();
      this.swordDamageUses.clear();
      this.lastDamageSamplePositions.clear();
      this.activeDamageContacts.clear();
      this.m_146870_();
   }

   public boolean m_6094_() {
      return false;
   }

   public boolean m_6087_() {
      return false;
   }

   public boolean m_20068_() {
      return true;
   }

   public boolean m_5829_() {
      return false;
   }

   public boolean m_6469_(@NotNull DamageSource source, float amount) {
      return false;
   }

   protected void m_7378_(CompoundTag pCompound) {
      if (pCompound.m_128403_("Owner")) {
         this.ownerUUID = pCompound.m_128342_("Owner");
      }

      if (pCompound.m_128441_("TotalSwords")) {
         this.f_19804_.m_135381_(TOTAL_SWORDS, pCompound.m_128451_("TotalSwords"));
      }

      if (pCompound.m_128425_("State", 8)) {
         try {
            this.setCurrentState(SpiralBladesEntity.State.valueOf(pCompound.m_128461_("State")));
         } catch (IllegalArgumentException var5) {
            this.setCurrentState(SpiralBladesEntity.State.FINISHED);
         }
      }

      if (pCompound.m_128441_("RotationSpeed")) {
         this.f_19804_.m_135381_(ROTATION_SPEED, pCompound.m_128457_("RotationSpeed"));
      }

      if (pCompound.m_128441_("SwordDurability")) {
         this.swordDurability = pCompound.m_128451_("SwordDurability");
      }

      this.infiniteSwordDurability = pCompound.m_128471_("InfiniteSwordDurability");
      this.childSwords.clear();
      ListTag childListTag = pCompound.m_128437_("ChildSwords", 8);
      childListTag.forEach(tag -> this.childSwords.add(UUID.fromString(tag.m_7916_())));
      this.swordFormationIndices.clear();
      ListTag mapTag = pCompound.m_128437_("FormationIndices", 10);
      mapTag.forEach(tag -> {
         CompoundTag entryTag = (CompoundTag)tag;
         if (entryTag.m_128403_("UUID")) {
            this.swordFormationIndices.put(entryTag.m_128342_("UUID"), entryTag.m_128451_("Index"));
         }
      });
      this.swordDamageUses.clear();
      ListTag durabilityTag = pCompound.m_128437_("SwordDamageUses", 10);
      durabilityTag.forEach(tag -> {
         CompoundTag entryTag = (CompoundTag)tag;
         if (entryTag.m_128403_("UUID")) {
            this.swordDamageUses.put(entryTag.m_128342_("UUID"), entryTag.m_128451_("Uses"));
         }
      });
      this.childSwords.forEach(uuid -> this.swordDamageUses.putIfAbsent(uuid, 0));
      this.lastDamageSamplePositions.clear();
      this.activeDamageContacts.clear();
   }

   protected void m_7380_(@NotNull CompoundTag pCompound) {
      if (this.ownerUUID != null) {
         pCompound.m_128362_("Owner", this.ownerUUID);
      }

      pCompound.m_128405_("TotalSwords", this.getTotalSwords());
      pCompound.m_128359_("State", this.getCurrentState().name());
      pCompound.m_128350_("RotationSpeed", (Float)this.f_19804_.m_135370_(ROTATION_SPEED));
      pCompound.m_128405_("SwordDurability", this.swordDurability);
      pCompound.m_128379_("InfiniteSwordDurability", this.infiniteSwordDurability);
      ListTag childListTag = new ListTag();
      this.childSwords.forEach(uuid -> childListTag.add(StringTag.m_129297_(uuid.toString())));
      pCompound.m_128365_("ChildSwords", childListTag);
      ListTag mapTag = new ListTag();
      this.swordFormationIndices.forEach((uuid, index) -> {
         CompoundTag entryTag = new CompoundTag();
         entryTag.m_128362_("UUID", uuid);
         entryTag.m_128405_("Index", index);
         mapTag.add(entryTag);
      });
      pCompound.m_128365_("FormationIndices", mapTag);
      ListTag durabilityTag = new ListTag();
      this.swordDamageUses.forEach((uuid, uses) -> {
         CompoundTag entryTag = new CompoundTag();
         entryTag.m_128362_("UUID", uuid);
         entryTag.m_128405_("Uses", uses);
         durabilityTag.add(entryTag);
      });
      pCompound.m_128365_("SwordDamageUses", durabilityTag);
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   public static enum State {
      STANDBY,
      STOPPED,
      FINISHED;
   }
}
