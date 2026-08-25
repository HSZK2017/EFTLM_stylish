package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.blockentity.ObsidianBlockEntity;
import com.pla.annoyingvillagers.blockentity.ShadowObsidianBlockEntity;
import com.pla.annoyingvillagers.blockentity.ShadowObsidianLongPillarBlockEntity;
import com.pla.annoyingvillagers.clazz.HerobrineObsidianBlock;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModBlocks;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.util.HerobrineUtil;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.StunType;

public class BlockProjectileEntity extends ThrowableProjectile {
   private static final EntityDataAccessor<BlockState> DATA_BLOCK = SynchedEntityData.m_135353_(BlockProjectileEntity.class, EntityDataSerializers.f_135034_);
   private static final EntityDataAccessor<Float> ROT_X = SynchedEntityData.m_135353_(BlockProjectileEntity.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Float> ROT_Y = SynchedEntityData.m_135353_(BlockProjectileEntity.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Float> ROT_Z = SynchedEntityData.m_135353_(BlockProjectileEntity.class, EntityDataSerializers.f_135029_);
   private boolean notReadyForShoot = false;
   private UUID ownerUUID;
   private static final float NO_DAMAGE_OVERRIDE = -1.0F;
   private float damageOverride = -1.0F;

   public void setDamageOverride(float damageOverride) {
      this.damageOverride = Math.max(0.0F, damageOverride);
   }

   public void clearDamageOverride() {
      this.damageOverride = -1.0F;
   }

   private float resolveImpactDamage() {
      if (this.damageOverride >= 0.0F) {
         return this.damageOverride;
      } else {
         return !this.getCarriedBlock().m_60713_((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_SHORT_PILLAR.get())
               && !this.getCarriedBlock().m_60713_((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get())
               && !this.getCarriedBlock().m_60713_((Block)AnnoyingVillagersModBlocks.OBSIDIAN_BLOCK.get())
            ? 2.0F
            : 10.0F;
      }
   }

   public void setOwnerUUID(UUID ownerUUID) {
      this.ownerUUID = ownerUUID;
   }

   public void setNotReadyForShoot(boolean notReadyForShoot) {
      this.notReadyForShoot = notReadyForShoot;
   }

   public BlockProjectileEntity(EntityType<? extends BlockProjectileEntity> type, Level level) {
      super(type, level);
      this.initRandomRotation();
   }

   public BlockProjectileEntity(Level level, LivingEntity shooter, BlockState block) {
      super((EntityType)AnnoyingVillagersModEntities.BLOCK_PROJECTILE.get(), shooter, level);
      this.setCarriedBlock(block);
      this.initRandomRotation();
   }

   public void setRotX(float v) {
      this.f_19804_.m_135381_(ROT_X, v);
   }

   public void setRotY(float v) {
      this.f_19804_.m_135381_(ROT_Y, v);
   }

   public void setRotZ(float v) {
      this.f_19804_.m_135381_(ROT_Z, v);
   }

   public float getRotX() {
      return (Float)this.f_19804_.m_135370_(ROT_X);
   }

   public float getRotY() {
      return (Float)this.f_19804_.m_135370_(ROT_Y);
   }

   public float getRotZ() {
      return (Float)this.f_19804_.m_135370_(ROT_Z);
   }

   private void initRandomRotation() {
      if (!this.m_9236_().f_46443_) {
         RandomSource r = this.f_19796_;
         this.setRotX((r.m_188501_() - 0.5F) * 10.0F);
         this.setRotY((r.m_188501_() - 0.5F) * 10.0F);
         this.setRotZ((r.m_188501_() - 0.5F) * 10.0F);
      }
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(DATA_BLOCK, Blocks.f_50069_.m_49966_());
      this.f_19804_.m_135372_(ROT_X, 0.0F);
      this.f_19804_.m_135372_(ROT_Y, 0.0F);
      this.f_19804_.m_135372_(ROT_Z, 0.0F);
   }

   public void setCarriedBlock(BlockState state) {
      this.f_19804_.m_135381_(DATA_BLOCK, state);
   }

   protected void m_5790_(@NotNull EntityHitResult result) {
      super.m_5790_(result);
      if (!this.notReadyForShoot) {
         Entity target;
         boolean var10000;
         label52: {
            target = result.m_82443_();
            UUID ownerId = this.ownerUUID;
            boolean isHerobrine = HerobrineUtil.isHerobrineFaction(target);
            label41:
            if (ownerId != null || !isHerobrine) {
               if (ownerId != null && target instanceof Player p && p.m_20148_().equals(ownerId)) {
                  break label41;
               }

               var10000 = false;
               break label52;
            }

            var10000 = true;
         }

         boolean blockDamage = var10000;
         if (!blockDamage) {
            if (target.m_9236_() instanceof ServerLevel serverLevel) {
               ((HitParticleType)EpicFightParticles.HIT_BLUNT.get())
                  .spawnParticleWithArgument(serverLevel, HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO, this, target);
               serverLevel.m_6263_(
                  null,
                  this.m_20185_(),
                  this.m_20186_(),
                  this.m_20189_(),
                  (SoundEvent)AnnoyingVillagersModSounds.OBSIDIAN_HIT.get(),
                  SoundSource.BLOCKS,
                  0.5F,
                  1.0F
               );
               float damage = this.resolveImpactDamage();
               if (this.m_19749_() == null) {
                  target.m_6469_(target.m_9236_().m_269111_().m_269264_(), damage);
               } else {
                  target.m_6469_(target.m_9236_().m_269111_().m_269104_(this, this.m_19749_()), damage);
               }

               LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
               if (livingEntityPatch != null) {
                  livingEntityPatch.applyStun(StunType.LONG, 20.0F);
               }

               if (target instanceof LivingEntity livingEntity) {
                  float strength = 1.0F;
                  double dx = this.m_20185_() - target.m_20185_();
                  double dz = this.m_20189_() - target.m_20189_();
                  livingEntity.m_147240_((double)strength, dx, dz);
               }
            }
         }
      }
   }

   public void m_8119_() {
      super.m_8119_();
      if (!this.m_9236_().f_46443_ && !this.m_213877_() && !this.notReadyForShoot) {
         BlockPos pos = this.m_20183_();
         if (this.tryPlaceInLiquid(pos)) {
            this.m_146870_();
         }
      }
   }

   protected void m_8060_(@NotNull BlockHitResult result) {
      if (!this.notReadyForShoot) {
         BlockPos pos = result.m_82425_();
         BlockState hitState = this.m_9236_().m_8055_(pos);
         if (!this.m_9236_().f_46443_) {
            if (this.tryPlaceInLiquid(pos)) {
               this.m_146870_();
               return;
            }

            if (hitState.m_247087_()) {
               return;
            }

            BlockPos placePos = pos.m_121945_(result.m_82434_());
            if (this.canPlaceAt(placePos)) {
               this.placeCarriedBlock(placePos, this.getReplaceByLiquid(this.m_9236_().m_6425_(placePos)));
            }

            this.m_146870_();
         }
      }
   }

   private boolean tryPlaceInLiquid(BlockPos pos) {
      FluidState fluidState = this.m_9236_().m_6425_(pos);
      int replaceByLiquid = this.getReplaceByLiquid(fluidState);
      if (replaceByLiquid == 0) {
         return false;
      } else if (!this.m_9236_().m_8055_(pos).m_247087_()) {
         return false;
      } else {
         this.placeCarriedBlock(pos, replaceByLiquid);
         return true;
      }
   }

   private boolean canPlaceAt(BlockPos pos) {
      BlockState state = this.m_9236_().m_8055_(pos);
      FluidState fluidState = this.m_9236_().m_6425_(pos);
      return !fluidState.m_76178_() ? state.m_247087_() && this.getReplaceByLiquid(fluidState) != 0 : state.m_60795_() || state.m_247087_();
   }

   private int getReplaceByLiquid(FluidState fluidState) {
      if (!fluidState.m_76170_()) {
         return 0;
      } else if (fluidState.m_205070_(FluidTags.f_13131_)) {
         return 1;
      } else {
         return fluidState.m_205070_(FluidTags.f_13132_) ? 2 : 0;
      }
   }

   private void placeCarriedBlock(BlockPos placePos, int replaceByLiquid) {
      UUID owner = this.ownerUUID;
      BlockState placeState = this.getPlacementState(owner, replaceByLiquid);
      this.m_9236_().m_46597_(placePos, placeState);
      BlockEntity blockEntity = this.m_9236_().m_7702_(placePos);
      if (owner != null && blockEntity != null) {
         this.setBlockEntityOwner(blockEntity, owner);
      }
   }

   private BlockState getPlacementState(UUID owner, int replaceByLiquid) {
      BlockState placeState;
      if (this.getCarriedBlock().m_60713_((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_SHORT_PILLAR.get())
         || this.getCarriedBlock().m_60713_((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_MIDDLE_PILLAR.get())) {
         placeState = ((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_LONG_PILLAR.get()).m_49966_();
      } else if (this.getCarriedBlock().m_60713_((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get())) {
         placeState = ((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get()).m_49966_();
      } else if (this.getCarriedBlock().m_60713_((Block)AnnoyingVillagersModBlocks.OBSIDIAN_BLOCK.get())) {
         placeState = ((Block)AnnoyingVillagersModBlocks.OBSIDIAN_BLOCK.get()).m_49966_();
      } else {
         placeState = this.getCarriedBlock();
      }

      if (owner != null && placeState.m_61138_(HerobrineObsidianBlock.FROM_PLAYER)) {
         placeState = (BlockState)placeState.m_61124_(HerobrineObsidianBlock.FROM_PLAYER, true);
      }

      if (replaceByLiquid != 0 && placeState.m_61138_(HerobrineObsidianBlock.REPLACE_BY_LIQUID)) {
         placeState = (BlockState)placeState.m_61124_(HerobrineObsidianBlock.REPLACE_BY_LIQUID, replaceByLiquid);
      }

      return placeState;
   }

   private void setBlockEntityOwner(BlockEntity blockEntity, UUID owner) {
      if (blockEntity instanceof ObsidianBlockEntity obsidianBlockEntity) {
         obsidianBlockEntity.setOwner(owner);
      } else if (blockEntity instanceof ShadowObsidianBlockEntity shadowObsidianBlockEntity) {
         shadowObsidianBlockEntity.setOwner(owner);
      } else if (blockEntity instanceof ShadowObsidianLongPillarBlockEntity shadowObsidianLongPillarBlockEntity) {
         shadowObsidianLongPillarBlockEntity.setOwner(owner);
      }

      blockEntity.m_6596_();
   }

   public BlockState getCarriedBlock() {
      return (BlockState)this.f_19804_.m_135370_(DATA_BLOCK);
   }

   protected void m_7380_(CompoundTag tag) {
      tag.m_128365_("Block", NbtUtils.m_129202_(this.getCarriedBlock()));
      tag.m_128350_("RotX", this.getRotX());
      tag.m_128350_("RotY", this.getRotY());
      tag.m_128350_("RotZ", this.getRotZ());
      tag.m_128379_("NotReadyForShoot", this.notReadyForShoot);
      tag.m_128350_("DamageOverride", this.damageOverride);
      if (this.ownerUUID != null) {
         tag.m_128362_("OwnerUUID", this.ownerUUID);
      }
   }

   protected void m_7378_(CompoundTag tag) {
      if (tag.m_128441_("Block")) {
         this.setCarriedBlock(NbtUtils.m_247651_(BuiltInRegistries.f_256975_.m_255303_(), tag.m_128469_("Block")));
      }

      this.setRotX(tag.m_128441_("RotX") ? tag.m_128457_("RotX") : 0.0F);
      this.setRotY(tag.m_128441_("RotY") ? tag.m_128457_("RotY") : 0.0F);
      this.setRotZ(tag.m_128441_("RotZ") ? tag.m_128457_("RotZ") : 0.0F);
      this.notReadyForShoot = tag.m_128471_("NotReadyForShoot");
      this.damageOverride = tag.m_128441_("DamageOverride") ? tag.m_128457_("DamageOverride") : -1.0F;
      this.ownerUUID = tag.m_128403_("OwnerUUID") ? tag.m_128342_("OwnerUUID") : null;
   }

   @NotNull
   public EntityDimensions m_6972_(@NotNull Pose pose) {
      return EntityDimensions.m_20398_(0.9F, 0.9F);
   }

   protected float m_7139_() {
      return 0.005F;
   }
}
