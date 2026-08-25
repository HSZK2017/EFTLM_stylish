package com.dmc.invincible_dmc.entity.summonedsword;

import com.dmc.invincible_dmc.entity.DMCEntities;
import com.dmc.invincible_dmc.entity.summonedsword.motion.SummonedSwordMotionController;
import com.dmc.invincible_dmc.entity.summonedsword.motion.SummonedSwordMotionMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class TripleBladesEntity extends Entity implements SummonedSwordMotionController {
   private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.m_135353_(TripleBladesEntity.class, EntityDataSerializers.f_135028_);
   private static final double FORWARD_DISTANCE = 2.5;
   private static final double LATERAL_SPACING = 0.85;
   private static final double SPAWN_HEIGHT_OFFSET = 2.5;
   private static final int STANDBY_DURATION = 5;
   private static final int LIFETIME_TICKS = 60;
   private static final int SHOOT_SPEED = 3;
   private static final float DEFAULT_PITCH = 45.0F;
   private UUID ownerUUID;
   private UUID targetUUID;
   private int standbyCounter = 0;
   private TripleBladesEntity.State currentState = TripleBladesEntity.State.STANDBY;
   private final List<UUID> activeSwords = new ArrayList<>();

   public TripleBladesEntity(EntityType<?> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.f_19811_ = true;
      this.f_19794_ = true;
   }

   public TripleBladesEntity(Level level, LivingEntity owner) {
      super((EntityType)DMCEntities.TRIPLE_BLADES.get(), level);
      this.ownerUUID = owner.m_20148_();
      this.f_19794_ = true;
      this.f_19811_ = true;
      this.m_146884_(owner.m_20182_());
   }

   public static void summon(Level level, LivingEntity owner) {
      SummonedSwordSpawner.triple(level, owner);
   }

   public void m_8119_() {
      super.m_8119_();
      if (!this.m_9236_().f_46443_) {
         LivingEntity owner = this.resolveOwner();
         if (owner != null && !owner.m_21224_()) {
            this.findSharedTarget(owner);
            switch (this.currentState) {
               case STANDBY:
                  this.tickStandby(owner);
                  break;
               case LAUNCHING:
                  this.tickLaunching();
                  break;
               case FINISHED:
                  this.tickFinished();
            }
         } else {
            this.cleanup();
         }
      }
   }

   private void tickStandby(LivingEntity owner) {
      if (this.standbyCounter == 0) {
         this.spawnSwords(owner);
      }

      this.maintainSwords();
      this.standbyCounter++;
      if (this.standbyCounter >= 5) {
         this.launchAllSwords();
         this.currentState = TripleBladesEntity.State.LAUNCHING;
      }
   }

   private void spawnSwords(LivingEntity owner) {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         LivingEntityPatch<?> ownerPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(owner, LivingEntityPatch.class);
         float playerYaw = ownerPatch != null ? ownerPatch.getYRot() : owner.m_146908_();
         float playerPitch = ownerPatch != null ? ((LivingEntity)ownerPatch.getOriginal()).m_146909_() : owner.m_146909_();
         double yawRad = Math.toRadians((double)playerYaw);
         double pitchRad = Math.toRadians((double)playerPitch);
         float f1 = (float)(-Math.sin(yawRad) * Math.cos(pitchRad));
         float f2 = (float)(-Math.sin(pitchRad));
         float f3 = (float)(Math.cos(yawRad) * Math.cos(pitchRad));
         Vec3 lookVec = new Vec3((double)f1, (double)f2, (double)f3);
         Vec3 right = new Vec3(-lookVec.f_82481_, 0.0, lookVec.f_82479_).m_82541_();
         Vec3 basePos = owner.m_20182_().m_82520_(0.0, (double)owner.m_20206_() + 2.5, 0.0).m_82549_(lookVec.m_82490_(2.5));
         Vec3[] positions = new Vec3[]{basePos.m_82549_(right.m_82490_(0.85)), basePos, basePos.m_82546_(right.m_82490_(0.85))};

         for (int index = 0; index < positions.length; index++) {
            Vec3 spawnPos = positions[index];
            DMCSummonedSwordEntity sword = SummonedSwordSpawner.createSword(this.m_9236_(), owner, 1.35F, true);
            if (sword != null) {
               sword.setLifetimeTicks(60);
               sword.setShootSpeed(3);
               sword.setMotionRotation(playerYaw, 45.0F);
               this.bindSwordMotion(sword, index, spawnPos.m_82546_(this.m_20182_()));
               serverLevel.m_7967_(sword);
               this.activeSwords.add(sword.m_20148_());
            }
         }
      }
   }

   private void findSharedTarget(LivingEntity owner) {
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

   private void maintainSwords() {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         for (int var6 = 0; var6 < this.activeSwords.size(); var6++) {
            UUID uuid = this.activeSwords.get(var6);
            Entity entity = serverLevel.m_8791_(uuid);
            if (entity instanceof DMCSummonedSwordEntity) {
               DMCSummonedSwordEntity sword = (DMCSummonedSwordEntity)entity;
               if (sword.isInStandby() && !sword.isManagedBy(this)) {
                  this.bindSwordMotion(sword, var6);
               }
            }
         }
      }
   }

   private void launchAllSwords() {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         LivingEntity var9 = this.getTarget();

         for (UUID uuid : this.activeSwords) {
            Entity entity = serverLevel.m_8791_(uuid);
            if (entity instanceof DMCSummonedSwordEntity) {
               DMCSummonedSwordEntity sword = (DMCSummonedSwordEntity)entity;
               this.releaseSwordMotion(sword);
               sword.launch(var9);
               if (var9 != null && var9.m_6084_()) {
                  float pitch = SummonedSwordSpawner.clampedDownwardPitchFromTarget(sword, var9, 25.0F, 75.0F);
                  float yaw = sword.m_146908_();
                  SummonedSwordSpawner.setSwordRotation(sword, yaw, pitch);
               } else {
                  SummonedSwordSpawner.setSwordRotation(sword, sword.m_146908_(), 45.0F);
               }

               sword.m_5496_(SoundEvents.f_12520_, 0.8F, 1.4F);
            }
         }
      }
   }

   private void tickLaunching() {
      if (this.activeSwords.isEmpty()) {
         this.currentState = TripleBladesEntity.State.FINISHED;
      } else if (this.m_9236_() instanceof ServerLevel serverLevel) {
         ArrayList var6 = new ArrayList();

         for (UUID uuid : this.activeSwords) {
            Entity entity = serverLevel.m_8791_(uuid);
            if (entity == null || !entity.m_6084_()) {
               var6.add(uuid);
            }
         }

         this.activeSwords.removeAll(var6);
      }
   }

   private void tickFinished() {
      this.m_146870_();
   }

   private void cleanup() {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         for (UUID uuid : this.activeSwords) {
            Entity sword = serverLevel.m_8791_(uuid);
            if (sword != null) {
               sword.m_146870_();
            }
         }
      }

      this.activeSwords.clear();
      this.m_146870_();
   }

   @Nullable
   private LivingEntity resolveOwner() {
      if (this.ownerUUID != null && this.m_9236_() instanceof ServerLevel serverLevel) {
         return serverLevel.m_8791_(this.ownerUUID) instanceof LivingEntity living ? living : null;
      } else {
         return null;
      }
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(TARGET_ID, -1);
   }

   @Override
   public SummonedSwordMotionMode getMotionMode() {
      return SummonedSwordMotionMode.TRIPLE_FORMATION;
   }

   protected void m_7378_(@NotNull CompoundTag tag) {
      if (tag.m_128403_("Owner")) {
         this.ownerUUID = tag.m_128342_("Owner");
      }

      if (tag.m_128403_("Target")) {
         this.targetUUID = tag.m_128342_("Target");
      }

      this.standbyCounter = tag.m_128451_("StandbyCounter");
      if (tag.m_128441_("State")) {
         this.currentState = TripleBladesEntity.State.valueOf(tag.m_128461_("State"));
      }

      this.activeSwords.clear();
      tag.m_128437_("ActiveSwords", 8).forEach(t -> this.activeSwords.add(UUID.fromString(t.m_7916_())));
   }

   protected void m_7380_(@NotNull CompoundTag tag) {
      if (this.ownerUUID != null) {
         tag.m_128362_("Owner", this.ownerUUID);
      }

      if (this.targetUUID != null) {
         tag.m_128362_("Target", this.targetUUID);
      }

      tag.m_128405_("StandbyCounter", this.standbyCounter);
      tag.m_128359_("State", this.currentState.name());
      ListTag activeList = new ListTag();
      this.activeSwords.forEach(uuid -> activeList.add(StringTag.m_129297_(uuid.toString())));
      tag.m_128365_("ActiveSwords", activeList);
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
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

   private static enum State {
      STANDBY,
      LAUNCHING,
      FINISHED;
   }
}
