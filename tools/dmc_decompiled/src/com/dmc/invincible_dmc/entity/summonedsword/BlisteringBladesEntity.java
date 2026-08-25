package com.dmc.invincible_dmc.entity.summonedsword;

import com.dmc.invincible_dmc.entity.DMCEntities;
import com.dmc.invincible_dmc.entity.summonedsword.motion.SummonedSwordMotionController;
import com.dmc.invincible_dmc.entity.summonedsword.motion.SummonedSwordMotionMode;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class BlisteringBladesEntity extends Entity implements SummonedSwordMotionController {
   private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.m_135353_(BlisteringBladesEntity.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Byte> CONTROLLER_STATE = SynchedEntityData.m_135353_(BlisteringBladesEntity.class, EntityDataSerializers.f_135027_);
   private static final EntityDataAccessor<Integer> TOTAL_SWORDS = SynchedEntityData.m_135353_(BlisteringBladesEntity.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.m_135353_(BlisteringBladesEntity.class, EntityDataSerializers.f_135028_);
   private int standbyTicks = 10;
   private int launchInterval = 2;
   private int spawnsPerTick = 4;
   private LivingEntity ownerRef;
   private UUID ownerUUID;
   private UUID targetUUID;
   private final List<UUID> childSwords = new ArrayList<>();
   private final Queue<UUID> launchQueue = new LinkedList<>();
   private final Map<UUID, Integer> swordFormationIndices = new HashMap<>();
   private final Queue<BlisteringBladesEntity.PendingSpawnData> spawnQueue = new LinkedList<>();
   private int launchTickCounter = 0;
   private BlisteringBladesEntity.State currentState = BlisteringBladesEntity.State.STANDBY;

   public BlisteringBladesEntity(EntityType<?> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.f_19811_ = true;
      this.f_19794_ = true;
   }

   public BlisteringBladesEntity(Level level, LivingEntity owner) {
      this((EntityType<?>)DMCEntities.BLISTERING_BLADES.get(), level);
      this.setOwner(owner);
      this.f_19794_ = true;
      this.f_19811_ = true;
      this.m_6034_(owner.m_20185_(), owner.m_20188_(), owner.m_20189_());
   }

   public static void summon(Level level, LivingEntity owner) {
      SummonedSwordSpawner.blistering(level, owner, 8, 999999, 2, 4);
   }

   public static void triggerLaunch(LivingEntity owner) {
      if (!owner.m_9236_().f_46443_) {
         for (Entity e : owner.m_9236_().m_142646_().m_142273_()) {
            if (e instanceof BlisteringBladesEntity) {
               BlisteringBladesEntity bb = (BlisteringBladesEntity)e;
               if (bb.m_6084_() && owner.m_20148_().equals(bb.ownerUUID) && bb.getCurrentState() == BlisteringBladesEntity.State.STANDBY) {
                  bb.prepareToLaunch();
               }
            }
         }
      }
   }

   public static void summon(Level level, LivingEntity owner, int totalSwords, int standbyTicks, int launchInterval, int spawnsPerTick) {
      SummonedSwordSpawner.blistering(level, owner, totalSwords, standbyTicks, launchInterval, spawnsPerTick);
   }

   public void setConfig(int totalSwords, int standbyTicks, int launchInterval, int spawnsPerTick) {
      this.f_19804_.m_135381_(TOTAL_SWORDS, totalSwords);
      this.standbyTicks = standbyTicks;
      this.launchInterval = launchInterval;
      this.spawnsPerTick = spawnsPerTick;
   }

   public int getTotalSwords() {
      return (Integer)this.f_19804_.m_135370_(TOTAL_SWORDS);
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(OWNER_ID, -1);
      this.f_19804_.m_135372_(CONTROLLER_STATE, (byte)BlisteringBladesEntity.State.STANDBY.ordinal());
      this.f_19804_.m_135372_(TOTAL_SWORDS, 8);
      this.f_19804_.m_135372_(TARGET_ID, -1);
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
               if ((Integer)this.f_19804_.m_135370_(OWNER_ID) != living.m_19879_()) {
                  this.f_19804_.m_135381_(OWNER_ID, living.m_19879_());
               }

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

   public BlisteringBladesEntity.State getCurrentState() {
      return BlisteringBladesEntity.State.values()[this.f_19804_.m_135370_(CONTROLLER_STATE)];
   }

   private void setCurrentState(BlisteringBladesEntity.State state) {
      this.currentState = state;
      this.f_19804_.m_135381_(CONTROLLER_STATE, (byte)state.ordinal());
   }

   @Nullable
   public LivingEntity getTarget() {
      Entity entity;
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         if (this.targetUUID == null) {
            return null;
         }

         entity = serverLevel.m_8791_(this.targetUUID);
      } else {
         entity = this.m_9236_().m_6815_((Integer)this.f_19804_.m_135370_(TARGET_ID));
      }

      return entity instanceof LivingEntity living ? living : null;
   }

   void spawnChildSwords() {
      LivingEntity owner = this.getOwner();
      if (owner == null) {
         this.m_146870_();
      } else if (this.m_9236_() instanceof ServerLevel) {
         int total = this.getTotalSwords();
         int totalRows = (int)Math.ceil((double)total / 2.0);

         for (int i = 0; i < total; i++) {
            boolean isLeft = i < totalRows;
            int verticalIndex = i % totalRows;
            this.spawnQueue.add(new BlisteringBladesEntity.PendingSpawnData(i, isLeft, verticalIndex));
         }
      }
   }

   private void processSpawnQueue() {
      if (!this.spawnQueue.isEmpty()) {
         LivingEntity owner = this.getOwner();
         if (owner != null && this.m_9236_() instanceof ServerLevel serverLevel) {
            int var8 = this.spawnsPerTick;
            int totalRows = (int)Math.ceil((double)this.getTotalSwords() / 2.0);

            while (var8 > 0 && !this.spawnQueue.isEmpty()) {
               BlisteringBladesEntity.PendingSpawnData data = this.spawnQueue.poll();
               DMCSummonedSwordEntity sword = SummonedSwordSpawner.createSword(this.m_9236_(), owner, 1.35F, true);
               if (sword != null) {
                  sword.setLifetimeTicks(Integer.MAX_VALUE);
                  this.swordFormationIndices.put(sword.m_20148_(), data.index);
                  sword.setBlast(true);
                  sword.setShootSpeed(5);
                  this.bindSwordMotion(sword, data.index);
                  Vec3 spawnPos = sword.m_20182_();
                  serverLevel.m_8767_(ParticleTypes.f_235898_, spawnPos.f_82479_, spawnPos.f_82480_, spawnPos.f_82481_, 5, 0.1, 0.1, 0.1, 0.02);
                  owner.m_5496_(SoundEvents.f_11852_, 0.5F, 1.0F);
                  this.m_9236_().m_7967_(sword);
                  this.childSwords.add(sword.m_20148_());
                  var8--;
               }
            }
         }
      }
   }

   public void m_8119_() {
      super.m_8119_();
      LivingEntity owner = this.getOwner();
      if (owner != null) {
         this.m_6034_(owner.m_20185_(), owner.m_20188_(), owner.m_20189_());
         if (!this.m_9236_().f_46443_) {
            if (owner instanceof Player player && VergilSkill.NotHoldingYamato(player)) {
               this.cleanup();
            }

            if (owner.m_21224_()) {
               this.cleanup();
            } else {
               this.findSharedTarget();
               switch (this.getCurrentState()) {
                  case STANDBY:
                     this.tickStandby();
                     break;
                  case LAUNCHING:
                     this.tickLaunching();
                  case FINISHED:
               }
            }
         }
      }
   }

   private void tickStandby() {
      this.processSpawnQueue();
      this.updateAllChildSwordsPosition();
   }

   private void tickLaunching() {
      this.updateAllChildSwordsPosition();
      this.launchTickCounter++;
      if (this.launchTickCounter >= this.launchInterval) {
         this.launchTickCounter = 0;
         this.launchNextSword();
      }

      if (this.launchQueue.isEmpty() && this.childSwords.isEmpty()) {
         this.setCurrentState(BlisteringBladesEntity.State.FINISHED);
         this.m_146870_();
      }
   }

   private void findSharedTarget() {
      LivingEntity owner = this.getOwner();
      if (owner != null) {
         LivingEntityPatch<?> ownerPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(owner, LivingEntityPatch.class);
         if (ownerPatch != null) {
            LivingEntity target = ownerPatch.getTarget();
            if (target != null && target.m_6084_()) {
               this.targetUUID = target.m_20148_();
               this.f_19804_.m_135381_(TARGET_ID, target.m_19879_());
            } else {
               this.targetUUID = null;
               this.f_19804_.m_135381_(TARGET_ID, -1);
            }
         } else {
            this.targetUUID = null;
            this.f_19804_.m_135381_(TARGET_ID, -1);
         }
      }
   }

   private void prepareToLaunch() {
      List<UUID> shuffledSwords = new ArrayList<>(this.childSwords);
      Collections.shuffle(shuffledSwords);
      this.launchQueue.addAll(shuffledSwords);
      this.setCurrentState(BlisteringBladesEntity.State.LAUNCHING);
   }

   private void launchNextSword() {
      if (!this.launchQueue.isEmpty()) {
         UUID swordUUID = this.launchQueue.poll();
         if (swordUUID != null) {
            if (((ServerLevel)this.m_9236_()).m_8791_(swordUUID) instanceof DMCSummonedSwordEntity sword) {
               this.childSwords.remove(swordUUID);
               LivingEntity target = this.getTarget();
               this.releaseSwordMotion(sword);
               if (target != null && target.m_6084_()) {
                  sword.aimAtEntity(target);
               } else {
                  LivingEntity owner = this.getOwner();
                  if (owner != null) {
                     float ownerYRot = owner.m_6080_();
                     float ownerXRot = owner.m_146909_();
                     sword.m_146922_(ownerYRot);
                     sword.m_146926_(ownerXRot);
                     sword.setSyncXRot(ownerXRot);
                  }
               }

               sword.setLifetimeTicks(60);
               sword.launch(target);
               sword.m_5496_(SoundEvents.f_11874_, 0.8F, 1.0F);
            }
         }
      }
   }

   private void updateAllChildSwordsPosition() {
      LivingEntity owner = this.getOwner();
      LivingEntity target = this.getTarget();
      if (owner != null) {
         ServerLevel serverLevel = (ServerLevel)this.m_9236_();
         List<UUID> toRemove = new ArrayList<>();
         int total = this.getTotalSwords();
         int totalRows = (int)Math.ceil((double)total / 2.0);

         for (UUID swordUUID : this.childSwords) {
            Entity entity = serverLevel.m_8791_(swordUUID);
            if (entity instanceof DMCSummonedSwordEntity sword && sword.isInStandby()) {
               Integer formationIndex = this.swordFormationIndices.get(swordUUID);
               if (formationIndex != null && !sword.isManagedBy(this)) {
                  this.bindSwordMotion(sword, formationIndex);
               }
               continue;
            }

            if (entity == null || !entity.m_6084_()) {
               toRemove.add(swordUUID);
               this.swordFormationIndices.remove(swordUUID);
            }
         }

         this.childSwords.removeAll(toRemove);
         this.launchQueue.removeAll(toRemove);
      }
   }

   @Override
   public SummonedSwordMotionMode getMotionMode() {
      return SummonedSwordMotionMode.BLISTERING_FORMATION;
   }

   private void cleanup() {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         List<UUID> allSwords = new ArrayList<>(this.childSwords);
         allSwords.addAll(this.launchQueue);

         for (UUID swordUUID : allSwords) {
            Entity sword = serverLevel.m_8791_(swordUUID);
            if (sword != null) {
               sword.m_146870_();
            }
         }
      }

      this.m_146870_();
   }

   public UUID getOwnerUUID() {
      return this.ownerUUID;
   }

   public void setOwnerUUID(UUID ownerUUID) {
      this.ownerUUID = ownerUUID;
   }

   public UUID getTargetUUID() {
      return this.targetUUID;
   }

   public void setTargetUUID(UUID targetUUID) {
      this.targetUUID = targetUUID;
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
            this.setCurrentState(BlisteringBladesEntity.State.valueOf(pCompound.m_128461_("State")));
         } catch (IllegalArgumentException var5) {
            this.setCurrentState(BlisteringBladesEntity.State.FINISHED);
         }
      }

      if (pCompound.m_128441_("TotalSwords")) {
         this.f_19804_.m_135381_(TOTAL_SWORDS, pCompound.m_128451_("TotalSwords"));
      }

      if (pCompound.m_128441_("StandbyTicks")) {
         this.standbyTicks = pCompound.m_128451_("StandbyTicks");
      }

      if (pCompound.m_128441_("LaunchInterval")) {
         this.launchInterval = pCompound.m_128451_("LaunchInterval");
      }

      if (pCompound.m_128441_("SpawnsPerTick")) {
         this.spawnsPerTick = pCompound.m_128451_("SpawnsPerTick");
      }

      this.launchTickCounter = pCompound.m_128451_("LaunchTicker");
      this.childSwords.clear();
      ListTag childListTag = pCompound.m_128437_("ChildSwords", 8);
      childListTag.forEach(tag -> this.childSwords.add(UUID.fromString(tag.m_7916_())));
      this.launchQueue.clear();
      ListTag queueListTag = pCompound.m_128437_("LaunchQueue", 8);
      queueListTag.forEach(tag -> this.launchQueue.add(UUID.fromString(tag.m_7916_())));
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
      pCompound.m_128405_("LaunchTicker", this.launchTickCounter);
      pCompound.m_128405_("TotalSwords", this.getTotalSwords());
      pCompound.m_128405_("StandbyTicks", this.standbyTicks);
      pCompound.m_128405_("LaunchInterval", this.launchInterval);
      pCompound.m_128405_("SpawnsPerTick", this.spawnsPerTick);
      ListTag childListTag = new ListTag();
      this.childSwords.forEach(uuid -> childListTag.add(StringTag.m_129297_(uuid.toString())));
      pCompound.m_128365_("ChildSwords", childListTag);
      ListTag queueListTag = new ListTag();
      this.launchQueue.forEach(uuid -> queueListTag.add(StringTag.m_129297_(uuid.toString())));
      pCompound.m_128365_("LaunchQueue", queueListTag);
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

   private static class PendingSpawnData {
      final int index;
      final boolean isLeft;
      final int verticalIndex;

      PendingSpawnData(int index, boolean isLeft, int verticalIndex) {
         this.index = index;
         this.isLeft = isLeft;
         this.verticalIndex = verticalIndex;
      }
   }

   public static enum State {
      STANDBY,
      LAUNCHING,
      FINISHED;
   }
}
