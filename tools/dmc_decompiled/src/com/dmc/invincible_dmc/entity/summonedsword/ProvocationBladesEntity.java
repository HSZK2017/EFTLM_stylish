package com.dmc.invincible_dmc.entity.summonedsword;

import com.dmc.invincible_dmc.InvincibleMod_DMC;
import com.dmc.invincible_dmc.entity.DMCEntities;
import com.dmc.invincible_dmc.entity.summonedsword.motion.SummonedSwordMotionController;
import com.dmc.invincible_dmc.entity.summonedsword.motion.SummonedSwordMotionMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class ProvocationBladesEntity extends Entity implements SummonedSwordMotionController {
   private static final double FORWARD_DISTANCE = 0.0;
   private static final double LATERAL_SPACING = 0.85;
   private static final float FIXED_PITCH = 45.0F;
   private static final int SHOOT_SPEED = 3;
   private static final int LIFETIME_TICKS = 60;
   private static final int STANDBY_DURATION = 2;
   private UUID ownerUUID;
   private UUID targetUUID;
   private int standbyCounter = 0;
   private ProvocationBladesEntity.State currentState = ProvocationBladesEntity.State.STANDBY;
   private final List<UUID> activeSwords = new ArrayList<>();

   public ProvocationBladesEntity(EntityType<?> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.f_19811_ = true;
      this.f_19794_ = true;
   }

   public ProvocationBladesEntity(Level level, LivingEntity owner) {
      super((EntityType)DMCEntities.PROVOCATION_BLADES.get(), level);
      this.ownerUUID = owner.m_20148_();
      this.f_19794_ = true;
      this.f_19811_ = true;
      this.m_146884_(owner.m_20182_());
   }

   public static void summon(Level level, LivingEntity owner) {
      SummonedSwordSpawner.provocation(level, owner);
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

      this.standbyCounter++;
      if (this.standbyCounter >= 2) {
         this.launchAllSwords();
         this.currentState = ProvocationBladesEntity.State.LAUNCHING;
      }
   }

   private void spawnSwords(LivingEntity owner) {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         LivingEntityPatch<?> ownerPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(owner, LivingEntityPatch.class);
         float playerYaw = ownerPatch != null ? ownerPatch.getYRot() : owner.m_146908_();
         double yawRad = Math.toRadians((double)playerYaw);
         Vec3 lookDir = new Vec3(-Math.sin(yawRad), 0.0, Math.cos(yawRad)).m_82541_();
         Vec3 right = new Vec3(-lookDir.f_82481_, 0.0, lookDir.f_82479_).m_82541_();
         Vec3 basePos = owner.m_20182_().m_82549_(lookDir.m_82490_(0.0)).m_82520_(0.0, (double)owner.m_20206_() * 0.7, 0.0);
         Vec3[] positions = new Vec3[]{basePos.m_82549_(right.m_82490_(0.85)), basePos, basePos.m_82546_(right.m_82490_(0.85))};

         for (int index = 0; index < positions.length; index++) {
            Vec3 spawnPos = positions[index];
            DMCSummonedSwordEntity sword = SummonedSwordSpawner.createSword(this.m_9236_(), owner, 1.5F, true);
            if (sword != null) {
               sword.setProvocation(true);
               sword.setLifetimeTicks(60);
               sword.setNoAim(true);
               sword.setShootSpeed(3);
               sword.setMotionRotation(playerYaw, 45.0F);
               this.bindSwordMotion(sword, index, spawnPos.m_82546_(this.m_20182_()));
               serverLevel.m_7967_(sword);
               this.activeSwords.add(sword.m_20148_());
            }
         }
      }
   }

   private void launchAllSwords() {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         LivingEntity var8 = this.getTarget();

         for (UUID uuid : this.activeSwords) {
            Entity entity = serverLevel.m_8791_(uuid);
            if (entity instanceof DMCSummonedSwordEntity) {
               DMCSummonedSwordEntity sword = (DMCSummonedSwordEntity)entity;
               this.releaseSwordMotion(sword);
               if (var8 != null && var8.m_6084_()) {
                  float pitch = SummonedSwordSpawner.clampedDownwardPitchFromTarget(sword, var8, 25.0F, 75.0F);
                  SummonedSwordSpawner.setSwordRotation(sword, sword.m_146908_(), pitch);
               } else {
                  SummonedSwordSpawner.setSwordRotation(sword, sword.m_146908_(), 45.0F);
               }

               sword.launch(null);
               sword.m_5496_(SoundEvents.f_12520_, 0.8F, 1.4F);
            }
         }
      }
   }

   private void findSharedTarget(LivingEntity owner) {
      LivingEntityPatch<?> ownerPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(owner, LivingEntityPatch.class);
      if (ownerPatch != null) {
         LivingEntity target = ownerPatch.getTarget();
         this.targetUUID = target != null && target.m_6084_() ? target.m_20148_() : null;
      } else {
         this.targetUUID = null;
      }
   }

   @Nullable
   private LivingEntity getTarget() {
      if (this.targetUUID != null && this.m_9236_() instanceof ServerLevel serverLevel) {
         return serverLevel.m_8791_(this.targetUUID) instanceof LivingEntity living ? living : null;
      } else {
         return null;
      }
   }

   private void tickLaunching() {
      this.checkBlockCollision();
      if (this.activeSwords.isEmpty()) {
         this.currentState = ProvocationBladesEntity.State.FINISHED;
      }
   }

   private void checkBlockCollision() {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         ArrayList var15 = new ArrayList();

         for (UUID uuid : this.activeSwords) {
            Entity entity = serverLevel.m_8791_(uuid);
            if (entity == null || !entity.m_6084_()) {
               var15.add(uuid);
            } else if (entity instanceof DMCSummonedSwordEntity) {
               DMCSummonedSwordEntity sword = (DMCSummonedSwordEntity)entity;
               Vec3 velocity = sword.m_20184_();
               if (!(velocity.m_82556_() < 0.001) && !sword.m_213877_()) {
                  Vec3 currentPos = sword.m_20182_();
                  Vec3 nextPos = currentPos.m_82549_(velocity);
                  BlockHitResult hit = serverLevel.m_45547_(new ClipContext(currentPos, nextPos, Block.COLLIDER, Fluid.NONE, sword));
                  if (hit.m_6662_() == Type.BLOCK) {
                     Vec3 hitPos = hit.m_82450_();
                     BlockPos hitBlockPos = hit.m_82425_();
                     BlockState blockState = serverLevel.m_8055_(hitBlockPos);
                     SoundType soundType = blockState.getSoundType(serverLevel, hitBlockPos, sword);
                     sword.m_6034_(hitPos.f_82479_, hitPos.f_82480_ + 0.3, hitPos.f_82481_);
                     sword.m_20256_(Vec3.f_82478_);
                     sword.setLockedTrajectory(Vec3.f_82478_);
                     serverLevel.m_6263_(
                        null, hitPos.f_82479_, hitPos.f_82480_, hitPos.f_82481_, soundType.m_56775_(), SoundSource.PLAYERS, 1.2F, soundType.m_56774_() * 0.8F
                     );
                     InvincibleMod_DMC.queueServerWork(4, () -> {
                        if (sword.m_6084_()) {
                           sword.setStuckInBlock(true);
                        }
                     });
                     var15.add(uuid);
                  }
               } else {
                  var15.add(uuid);
               }
            } else {
               var15.add(uuid);
            }
         }

         this.activeSwords.removeAll(var15);
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
   }

   @Override
   public SummonedSwordMotionMode getMotionMode() {
      return SummonedSwordMotionMode.PROVOCATION_FORMATION;
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
         this.currentState = ProvocationBladesEntity.State.valueOf(tag.m_128461_("State"));
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
