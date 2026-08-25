package com.dmc.invincible_dmc.entity.summonedsword;

import com.dmc.invincible_dmc.entity.DMCEntities;
import com.dmc.invincible_dmc.entity.summonedsword.motion.SummonedSwordMotionController;
import com.dmc.invincible_dmc.entity.summonedsword.motion.SummonedSwordMotionMode;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class StormBladesEntity extends Entity implements SummonedSwordMotionController {
   private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.m_135353_(StormBladesEntity.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.m_135353_(StormBladesEntity.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Byte> CONTROLLER_STATE = SynchedEntityData.m_135353_(StormBladesEntity.class, EntityDataSerializers.f_135027_);
   private static final EntityDataAccessor<Integer> TOTAL_SWORDS = SynchedEntityData.m_135353_(StormBladesEntity.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Long> ORBIT_START_GAME_TIME = SynchedEntityData.m_135353_(StormBladesEntity.class, EntityDataSerializers.f_244073_);
   private double orbitRadius = 3.0;
   private float rotationSpeed = 12.0F;
   private LivingEntity ownerRef;
   private UUID ownerUUID;
   private LivingEntity targetRef;
   private UUID targetUUID;
   private final List<UUID> childSwords = new ArrayList<>();
   private final Map<UUID, Integer> swordFormationIndices = new HashMap<>();
   private int preLaunchTimer = 2;
   private boolean launchArmed = false;
   private StormBladesEntity.State currentState = StormBladesEntity.State.STANDBY;

   public StormBladesEntity(EntityType<?> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.f_19811_ = true;
      this.f_19794_ = true;
   }

   public StormBladesEntity(Level level, LivingEntity owner, LivingEntity target) {
      this((EntityType<?>)DMCEntities.STORM_BLADES.get(), level);
      this.setOwner(owner);
      this.setTarget(target);
      this.f_19794_ = true;
      this.f_19811_ = true;
      this.m_6034_(target.m_20185_(), target.m_20186_(), target.m_20189_());
      this.f_19804_.m_135381_(ORBIT_START_GAME_TIME, level.m_46467_());
   }

   public static void summon(Level level, LivingEntity owner, LivingEntity target) {
      SummonedSwordSpawner.storm(level, owner, target);
   }

   public static void triggerLaunch(LivingEntity owner) {
      if (!owner.m_9236_().f_46443_) {
         for (Entity e : owner.m_9236_().m_142646_().m_142273_()) {
            if (e instanceof StormBladesEntity) {
               StormBladesEntity sb = (StormBladesEntity)e;
               if (sb.m_6084_() && owner.m_20148_().equals(sb.ownerUUID) && sb.getCurrentState() == StormBladesEntity.State.STANDBY) {
                  sb.armForLaunch();
               }
            }
         }
      }
   }

   private void armForLaunch() {
      this.launchArmed = true;
      this.preLaunchTimer = 2;
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(OWNER_ID, -1);
      this.f_19804_.m_135372_(TARGET_ID, -1);
      this.f_19804_.m_135372_(CONTROLLER_STATE, (byte)StormBladesEntity.State.STANDBY.ordinal());
      this.f_19804_.m_135372_(TOTAL_SWORDS, 6);
      this.f_19804_.m_135372_(ORBIT_START_GAME_TIME, 0L);
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

   public void setTarget(LivingEntity target) {
      this.targetRef = target;
      this.targetUUID = target.m_20148_();
      this.f_19804_.m_135381_(TARGET_ID, target.m_19879_());
   }

   @Nullable
   public LivingEntity getTarget() {
      if (this.targetRef != null && this.targetRef.m_6084_()) {
         return this.targetRef;
      } else {
         if (!this.m_9236_().f_46443_ && this.targetUUID != null) {
            if (((ServerLevel)this.m_9236_()).m_8791_(this.targetUUID) instanceof LivingEntity living) {
               this.targetRef = living;
               return living;
            }
         } else if (this.m_9236_().f_46443_) {
            int id = (Integer)this.f_19804_.m_135370_(TARGET_ID);
            if (id != -1 && this.m_9236_().m_6815_(id) instanceof LivingEntity living) {
               this.targetRef = living;
               return living;
            }
         }

         return null;
      }
   }

   public StormBladesEntity.State getCurrentState() {
      return StormBladesEntity.State.values()[this.f_19804_.m_135370_(CONTROLLER_STATE)];
   }

   private void setCurrentState(StormBladesEntity.State state) {
      this.currentState = state;
      this.f_19804_.m_135381_(CONTROLLER_STATE, (byte)state.ordinal());
   }

   public int getTotalSwords() {
      return (Integer)this.f_19804_.m_135370_(TOTAL_SWORDS);
   }

   void spawnChildSwords() {
      LivingEntity owner = this.getOwner();
      LivingEntity target = this.getTarget();
      if (owner != null && target != null && this.m_9236_() instanceof ServerLevel serverLevel) {
         int var8 = this.getTotalSwords();

         for (int i = 0; i < var8; i++) {
            DMCSummonedSwordEntity sword = SummonedSwordSpawner.createSword(this.m_9236_(), owner, 1.4F, true);
            if (sword != null) {
               sword.setLifetimeTicks(Integer.MAX_VALUE);
               sword.setNoAim(true);
               sword.setStorm(true);
               this.bindSwordMotion(sword, i);
               Vec3 spawnPos = sword.m_20182_();
               serverLevel.m_8767_(ParticleTypes.f_123760_, spawnPos.f_82479_, spawnPos.f_82480_, spawnPos.f_82481_, 5, 0.1, 0.1, 0.1, 0.0);
               this.m_9236_().m_7967_(sword);
               this.childSwords.add(sword.m_20148_());
               this.swordFormationIndices.put(sword.m_20148_(), i);
            }
         }

         target.m_5496_(SoundEvents.f_11867_, 1.0F, 1.2F);
      } else {
         this.m_146870_();
      }
   }

   public void m_8119_() {
      super.m_8119_();
      LivingEntity target = this.getTarget();
      if (target == null) {
         this.cleanup();
      } else {
         this.m_6034_(target.m_20185_(), target.m_20186_(), target.m_20189_());
         if (!this.m_9236_().f_46443_) {
            if (target.m_21224_()) {
               this.cleanup();
            } else {
               switch (this.getCurrentState()) {
                  case STANDBY:
                     this.tickStandby();
                  case FINISHED:
               }
            }
         }
      }
   }

   private void tickStandby() {
      if (this.launchArmed) {
         this.preLaunchTimer--;
         if (this.preLaunchTimer <= 0) {
            this.prepareToLaunch();
         }
      } else {
         this.pruneChildSwords();
      }

      if (this.getOwner() instanceof Player player && VergilSkill.NotHoldingYamato(player)) {
         this.cleanup();
      }
   }

   private void prepareToLaunch() {
      for (UUID swordUUID : this.childSwords) {
         if (((ServerLevel)this.m_9236_()).m_8791_(swordUUID) instanceof DMCSummonedSwordEntity sword) {
            this.releaseSwordMotion(sword);
            sword.setLifetimeTicks(80);
            sword.setStorm(true);
            sword.m_146926_(0.0F);
            sword.setSyncXRot(0.0F);
            sword.launch(null);
            sword.m_5496_(SoundEvents.f_12520_, 0.8F, 1.4F);
         }
      }

      this.childSwords.clear();
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
         } else if (entity != null) {
            toRemove.add(swordUUID);
            this.swordFormationIndices.remove(swordUUID);
         }
      }

      this.childSwords.removeAll(toRemove);
   }

   private long getOrbitTick() {
      return Math.max(0L, this.m_9236_().m_46467_() - (Long)this.f_19804_.m_135370_(ORBIT_START_GAME_TIME));
   }

   @Override
   public SummonedSwordMotionMode getMotionMode() {
      return SummonedSwordMotionMode.STORM_ORBIT;
   }

   public long getMotionTick(boolean previous) {
      return Math.max(0L, this.getOrbitTick() - (previous ? 1L : 0L));
   }

   public double getMotionRadius() {
      return this.orbitRadius;
   }

   public float getMotionRotationSpeed() {
      return this.rotationSpeed;
   }

   private void cleanup() {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         for (UUID swordUUID : new ArrayList<>(this.childSwords)) {
            Entity sword = serverLevel.m_8791_(swordUUID);
            if (sword != null) {
               sword.m_146870_();
            }
         }
      }

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

      if (pCompound.m_128403_("Target")) {
         this.targetUUID = pCompound.m_128342_("Target");
      }

      if (pCompound.m_128425_("State", 8)) {
         try {
            this.setCurrentState(StormBladesEntity.State.valueOf(pCompound.m_128461_("State")));
         } catch (IllegalArgumentException var4) {
            this.setCurrentState(StormBladesEntity.State.FINISHED);
         }
      }

      if (pCompound.m_128441_("TotalSwords")) {
         this.f_19804_.m_135381_(TOTAL_SWORDS, pCompound.m_128451_("TotalSwords"));
      }

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
   }

   protected void m_7380_(@NotNull CompoundTag pCompound) {
      if (this.ownerUUID != null) {
         pCompound.m_128362_("Owner", this.ownerUUID);
      }

      if (this.targetUUID != null) {
         pCompound.m_128362_("Target", this.targetUUID);
      }

      pCompound.m_128359_("State", this.getCurrentState().name());
      pCompound.m_128405_("TotalSwords", this.getTotalSwords());
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
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   public static enum State {
      STANDBY,
      LAUNCHING,
      FINISHED;
   }
}
