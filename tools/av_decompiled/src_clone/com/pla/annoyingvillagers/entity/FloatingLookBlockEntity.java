package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import java.util.Collections;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class FloatingLookBlockEntity extends LivingEntity {
   public static final int PHASE_RISING = 0;
   public static final int PHASE_FLOATING = 1;
   public static final int PHASE_FALLING = 2;
   private static final int RISE_TICKS = 16;
   private static final int FLOAT_TICKS = 200;
   private static final int HARD_DESPAWN_TICKS = 600;
   private static final int OWNER_ATTRACT_TICKS = 216;
   private static final double LIFT_HEIGHT = 2.25;
   private static final double FLOAT_BOB = 0.08;
   private static final double FALL_SPEED = 0.09;
   private static final float LAUNCHED_PROJECTILE_DAMAGE = 1.5F;
   private static final float LAUNCHED_PROJECTILE_SPEED = 1.85F;
   private static final EntityDataAccessor<BlockPos> DATA_ORIGINAL_POS = SynchedEntityData.m_135353_(
      FloatingLookBlockEntity.class, EntityDataSerializers.f_135038_
   );
   private static final EntityDataAccessor<BlockState> DATA_BLOCK_STATE = SynchedEntityData.m_135353_(
      FloatingLookBlockEntity.class, EntityDataSerializers.f_135034_
   );
   private static final EntityDataAccessor<Integer> DATA_PHASE = SynchedEntityData.m_135353_(FloatingLookBlockEntity.class, EntityDataSerializers.f_135028_);
   private int phaseTicks;
   @Nullable
   private UUID ownerUuid;
   @Nullable
   private UUID ownerPreviousTargetUuid;
   @Nullable
   private CompoundTag carriedBlockEntityTag;
   private boolean launched;
   private boolean attractingOwner;
   private boolean ownerAttractionFinished;
   private int ownerAttractTicks;

   public FloatingLookBlockEntity(EntityType<? extends FloatingLookBlockEntity> entityType, Level level) {
      super(entityType, level);
      this.f_19794_ = true;
      this.m_20242_(true);
   }

   public FloatingLookBlockEntity(
      Level level, BlockPos originalPos, BlockState blockState, @Nullable UUID ownerUuid, @Nullable CompoundTag carriedBlockEntityTag
   ) {
      this((EntityType<? extends FloatingLookBlockEntity>)AnnoyingVillagersModEntities.FLOATING_LOOK_BLOCK.get(), level);
      this.setOriginalPos(originalPos);
      this.setCarriedBlock(blockState);
      this.ownerUuid = ownerUuid;
      this.carriedBlockEntityTag = carriedBlockEntityTag == null ? null : carriedBlockEntityTag.m_6426_();
      double x = (double)originalPos.m_123341_() + 0.5;
      double y = getStartY(originalPos);
      double z = (double)originalPos.m_123343_() + 0.5;
      this.m_6034_(x, y, z);
      this.f_19854_ = x;
      this.f_19855_ = y;
      this.f_19856_ = z;
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(DATA_ORIGINAL_POS, BlockPos.f_121853_);
      this.f_19804_.m_135372_(DATA_BLOCK_STATE, Blocks.f_50016_.m_49966_());
      this.f_19804_.m_135372_(DATA_PHASE, 0);
   }

   public static Builder createAttributes() {
      return LivingEntity.m_21183_().m_22268_(Attributes.f_22276_, 1.0).m_22268_(Attributes.f_22278_, 1.0).m_22268_(Attributes.f_22279_, 0.0);
   }

   public BlockPos getOriginalPos() {
      return (BlockPos)this.f_19804_.m_135370_(DATA_ORIGINAL_POS);
   }

   public void setOriginalPos(BlockPos pos) {
      this.f_19804_.m_135381_(DATA_ORIGINAL_POS, pos);
   }

   public BlockState getCarriedBlock() {
      return (BlockState)this.f_19804_.m_135370_(DATA_BLOCK_STATE);
   }

   public void setCarriedBlock(BlockState state) {
      this.f_19804_.m_135381_(DATA_BLOCK_STATE, state);
   }

   public int getPhase() {
      return (Integer)this.f_19804_.m_135370_(DATA_PHASE);
   }

   private void setPhase(int phase) {
      this.f_19804_.m_135381_(DATA_PHASE, phase);
      this.phaseTicks = 0;
   }

   private static double getStartY(BlockPos originalPos) {
      return (double)originalPos.m_123342_() + 0.5;
   }

   private static double getFloatY(BlockPos originalPos) {
      return (double)originalPos.m_123342_() + 0.5 + 2.25;
   }

   private static double easeOutCubic(double t) {
      t = Math.max(0.0, Math.min(1.0, t));
      return 1.0 - Math.pow(1.0 - t, 3.0);
   }

   public void m_8119_() {
      super.m_8119_();
      if (!this.m_9236_().f_46443_) {
         if (this.f_19797_ > 600) {
            this.m_146870_();
         } else {
            BlockState state = this.getCarriedBlock();
            if (!state.m_60795_() && state.m_60799_() == RenderShape.MODEL) {
               this.phaseTicks++;
               switch (this.getPhase()) {
                  case 0:
                     this.tickRising();
                     break;
                  case 1:
                     this.tickFloating();
                     break;
                  case 2:
                     this.tickFalling();
                     break;
                  default:
                     this.m_146870_();
               }

               if (!this.m_213877_() && !this.launched) {
                  this.tickOwnerAttraction();
               }
            } else {
               this.m_146870_();
            }
         }
      }
   }

   private void tickRising() {
      BlockPos originalPos = this.getOriginalPos();
      double x = (double)originalPos.m_123341_() + 0.5;
      double z = (double)originalPos.m_123343_() + 0.5;
      double startY = getStartY(originalPos);
      double targetY = getFloatY(originalPos);
      double progress = (double)this.phaseTicks / 16.0;
      double eased = easeOutCubic(progress);
      double y = startY + (targetY - startY) * eased;
      this.m_6034_(x, y, z);
      if (this.phaseTicks >= 16) {
         this.m_6034_(x, targetY, z);
         this.setPhase(1);
      }
   }

   private void tickFloating() {
      BlockPos originalPos = this.getOriginalPos();
      double x = (double)originalPos.m_123341_() + 0.5;
      double z = (double)originalPos.m_123343_() + 0.5;
      double y = getFloatY(originalPos) + Math.sin((double)this.f_19797_ * 0.18) * 0.08;
      this.m_6034_(x, y, z);
      if (this.phaseTicks >= 200) {
         this.setPhase(2);
      }
   }

   private void tickFalling() {
      this.restoreOwnerTarget();
      this.m_6034_(this.m_20185_(), this.m_20186_() - 0.09, this.m_20189_());
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         this.tryRestoreIfTouchingSupport(serverLevel);
      }

      if (this.m_20186_() < (double)(this.m_9236_().m_141937_() - 8)) {
         this.m_146870_();
      }
   }

   private void tickOwnerAttraction() {
      if (!this.ownerAttractionFinished) {
         if (this.m_9236_() instanceof ServerLevel serverLevel) {
            Mob owner = this.getOwnerMob(serverLevel);
            if (owner != null && this.getPhase() != 2) {
               if (!this.attractingOwner) {
                  LivingEntity previousTarget = owner.m_5448_();
                  this.ownerPreviousTargetUuid = previousTarget != null && previousTarget != this ? previousTarget.m_20148_() : null;
                  this.attractingOwner = true;
                  this.ownerAttractTicks = 0;
               }

               if (this.ownerAttractTicks++ >= 216) {
                  this.restoreOwnerTarget();
               } else {
                  owner.m_6710_(this);
                  owner.m_21563_().m_24960_(this, 30.0F, 30.0F);
               }
            } else {
               this.restoreOwnerTarget();
            }
         }
      }
   }

   @Nullable
   private Mob getOwnerMob(ServerLevel serverLevel) {
      if (this.ownerUuid == null) {
         return null;
      } else {
         if (serverLevel.m_8791_(this.ownerUuid) instanceof Mob mob && mob.m_6084_()) {
            return mob;
         }

         return null;
      }
   }

   private void restoreOwnerTarget() {
      if (this.attractingOwner && this.m_9236_() instanceof ServerLevel serverLevel) {
         Mob owner = this.getOwnerMob(serverLevel);
         if (owner != null && owner.m_5448_() == this) {
            LivingEntity previousTarget = this.getPreviousOwnerTarget(serverLevel);
            owner.m_6710_(previousTarget);
         }

         this.attractingOwner = false;
         this.ownerAttractionFinished = true;
         this.ownerAttractTicks = 0;
         this.ownerPreviousTargetUuid = null;
      }
   }

   @Nullable
   private LivingEntity getPreviousOwnerTarget(ServerLevel serverLevel) {
      if (this.ownerPreviousTargetUuid == null) {
         return null;
      } else {
         Entity entity = serverLevel.m_8791_(this.ownerPreviousTargetUuid);
         if (entity == null) {
            entity = serverLevel.m_46003_(this.ownerPreviousTargetUuid);
         }

         if (entity instanceof LivingEntity livingEntity && livingEntity.m_6084_()) {
            return livingEntity;
         }

         return null;
      }
   }

   private void tryRestoreIfTouchingSupport(ServerLevel level) {
      double bottomY = this.m_20186_() - 0.5;
      BlockPos supportPos = BlockPos.m_274561_(this.m_20185_(), bottomY - 0.04, this.m_20189_());
      if (this.isSolidSupport(level, supportPos)) {
         BlockPos placePos = supportPos.m_7494_();
         if (!this.canRestoreAt(level, placePos)) {
            this.m_146870_();
         } else {
            this.restoreAsBlock(level, placePos);
         }
      }
   }

   private boolean isSolidSupport(ServerLevel level, BlockPos pos) {
      BlockState state = level.m_8055_(pos);
      if (state.m_60795_()) {
         return false;
      } else {
         return !state.m_60819_().m_76178_() ? false : !state.m_60812_(level, pos).m_83281_();
      }
   }

   private boolean canRestoreAt(ServerLevel level, BlockPos pos) {
      if (!level.m_6425_(pos).m_76178_()) {
         return false;
      } else {
         BlockState current = level.m_8055_(pos);
         return current.m_60795_() || current.m_247087_();
      }
   }

   private void restoreAsBlock(ServerLevel level, BlockPos placePos) {
      BlockState blockState = this.getCarriedBlock();
      BlockState oldState = level.m_8055_(placePos);
      level.m_46597_(placePos, blockState);
      if (this.carriedBlockEntityTag != null) {
         BlockEntity blockEntity = level.m_7702_(placePos);
         if (blockEntity != null) {
            CompoundTag tag = this.carriedBlockEntityTag.m_6426_();
            tag.m_128405_("x", placePos.m_123341_());
            tag.m_128405_("y", placePos.m_123342_());
            tag.m_128405_("z", placePos.m_123343_());
            blockEntity.m_142466_(tag);
            blockEntity.m_6596_();
            level.m_7260_(placePos, oldState, blockState, 3);
         }
      }

      level.m_8767_(
         new BlockParticleOption(ParticleTypes.f_123794_, blockState),
         (double)placePos.m_123341_() + 0.5,
         (double)placePos.m_123342_() + 0.5,
         (double)placePos.m_123343_() + 0.5,
         25,
         0.35,
         0.35,
         0.35,
         0.08
      );
      level.m_5594_(null, placePos, blockState.m_60827_().m_56777_(), SoundSource.BLOCKS, 1.0F, 0.85F + level.f_46441_.m_188501_() * 0.25F);
      this.m_146870_();
   }

   public boolean m_6469_(@NotNull DamageSource source, float amount) {
      if (this.m_9236_().f_46443_) {
         return true;
      } else if (!this.launched && !this.m_213877_()) {
         this.launched = true;
         this.launchAsProjectile(source);
         this.m_146870_();
         return true;
      } else {
         return true;
      }
   }

   private void launchAsProjectile(DamageSource source) {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         BlockState blockState = this.getCarriedBlock();
         if (!blockState.m_60795_()) {
            LivingEntity shooter = this.findShooter(source);
            BlockProjectileEntity projectile;
            if (shooter != null) {
               projectile = new BlockProjectileEntity(serverLevel, shooter, blockState);
               projectile.setOwnerUUID(shooter.m_20148_());
            } else {
               projectile = new BlockProjectileEntity(
                  (EntityType<? extends BlockProjectileEntity>)AnnoyingVillagersModEntities.BLOCK_PROJECTILE.get(), serverLevel
               );
               projectile.setCarriedBlock(blockState);
               if (this.ownerUuid != null) {
                  projectile.setOwnerUUID(this.ownerUuid);
               }
            }

            projectile.m_6034_(this.m_20185_(), this.m_20186_(), this.m_20189_());
            projectile.setDamageOverride(1.5F);
            Vec3 direction = this.getLaunchDirection(serverLevel, shooter, projectile.m_20182_());
            if (direction.m_82556_() < 1.0E-6) {
               direction = new Vec3(0.0, 0.1, 1.0);
            }

            projectile.m_6686_(direction.f_82479_, direction.f_82480_, direction.f_82481_, 1.85F, 0.05F);
            serverLevel.m_7967_(projectile);
            serverLevel.m_8767_(
               new BlockParticleOption(ParticleTypes.f_123794_, blockState), this.m_20185_(), this.m_20186_(), this.m_20189_(), 30, 0.35, 0.35, 0.35, 0.1
            );
            serverLevel.m_5594_(
               null, this.m_20183_(), blockState.m_60827_().m_56775_(), SoundSource.BLOCKS, 1.0F, 0.9F + serverLevel.f_46441_.m_188501_() * 0.2F
            );
         }
      }
   }

   private Vec3 getLaunchDirection(ServerLevel serverLevel, @Nullable LivingEntity shooter, Vec3 projectilePos) {
      if (shooter != null && !(shooter instanceof Player)) {
         LivingEntity previousTarget = this.getPreviousOwnerTarget(serverLevel);
         if (previousTarget != null && previousTarget != shooter && previousTarget != this) {
            Vec3 direction = previousTarget.m_146892_().m_82546_(projectilePos);
            if (direction.m_82556_() > 1.0E-6) {
               return direction;
            }
         }
      }

      return shooter != null ? shooter.m_20154_() : this.m_20184_();
   }

   @Nullable
   private LivingEntity findShooter(DamageSource source) {
      Entity sourceEntity = source.m_7639_();
      if (sourceEntity instanceof LivingEntity) {
         return (LivingEntity)sourceEntity;
      } else {
         Entity directEntity = source.m_7640_();
         if (directEntity instanceof LivingEntity) {
            return (LivingEntity)directEntity;
         } else {
            if (this.ownerUuid != null && this.m_9236_() instanceof ServerLevel serverLevel) {
               Entity owner = serverLevel.m_8791_(this.ownerUuid);
               if (owner instanceof LivingEntity) {
                  return (LivingEntity)owner;
               }
            }

            return null;
         }
      }
   }

   public void m_7380_(CompoundTag tag) {
      super.m_7380_(tag);
      tag.m_128365_("OriginalPos", NbtUtils.m_129224_(this.getOriginalPos()));
      tag.m_128365_("BlockState", NbtUtils.m_129202_(this.getCarriedBlock()));
      tag.m_128405_("Phase", this.getPhase());
      tag.m_128405_("PhaseTicks", this.phaseTicks);
      tag.m_128379_("Launched", this.launched);
      if (this.ownerUuid != null) {
         tag.m_128362_("Owner", this.ownerUuid);
      }

      if (this.carriedBlockEntityTag != null) {
         tag.m_128365_("BlockEntityTag", this.carriedBlockEntityTag.m_6426_());
      }
   }

   public void m_7378_(CompoundTag tag) {
      super.m_7378_(tag);
      this.setOriginalPos(NbtUtils.m_129239_(tag.m_128469_("OriginalPos")));
      this.setCarriedBlock(NbtUtils.m_247651_(this.m_9236_().m_246945_(Registries.f_256747_), tag.m_128469_("BlockState")));
      this.f_19804_.m_135381_(DATA_PHASE, tag.m_128451_("Phase"));
      this.phaseTicks = tag.m_128451_("PhaseTicks");
      this.launched = tag.m_128471_("Launched");
      this.ownerUuid = tag.m_128403_("Owner") ? tag.m_128342_("Owner") : null;
      this.carriedBlockEntityTag = tag.m_128425_("BlockEntityTag", 10) ? tag.m_128469_("BlockEntityTag").m_6426_() : null;
   }

   public boolean m_6087_() {
      return true;
   }

   public boolean m_6097_() {
      return true;
   }

   public boolean m_7313_(@NotNull Entity attacker) {
      return false;
   }

   public void m_142687_(@NotNull RemovalReason reason) {
      this.restoreOwnerTarget();
      super.m_142687_(reason);
   }

   @NotNull
   public Iterable<ItemStack> m_6168_() {
      return Collections.emptyList();
   }

   @NotNull
   public ItemStack m_6844_(@NotNull EquipmentSlot slot) {
      return ItemStack.f_41583_;
   }

   public void m_8061_(@NotNull EquipmentSlot slot, @NotNull ItemStack stack) {
   }

   @NotNull
   public HumanoidArm m_5737_() {
      return HumanoidArm.RIGHT;
   }

   public boolean m_6051_() {
      return false;
   }

   @NotNull
   public EntityDimensions m_6972_(@NotNull Pose pose) {
      return EntityDimensions.m_20398_(1.0F, 1.0F);
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }
}
