package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class RisingWallBlockEntity extends Entity {
   private static final double START_BELOW_TARGET = 1.15;
   private static final int HARD_DESPAWN_TICKS = 200;
   private static final EntityDataAccessor<BlockPos> FINAL_BLOCK_POS = SynchedEntityData.m_135353_(RisingWallBlockEntity.class, EntityDataSerializers.f_135038_);
   private static final EntityDataAccessor<BlockState> RENDER_BLOCK_STATE = SynchedEntityData.m_135353_(
      RisingWallBlockEntity.class, EntityDataSerializers.f_135034_
   );
   private static final EntityDataAccessor<Integer> START_DELAY_TICKS = SynchedEntityData.m_135353_(
      RisingWallBlockEntity.class, EntityDataSerializers.f_135028_
   );
   private static final EntityDataAccessor<Integer> RISE_TICKS = SynchedEntityData.m_135353_(RisingWallBlockEntity.class, EntityDataSerializers.f_135028_);
   private boolean converted;

   public RisingWallBlockEntity(EntityType<? extends RisingWallBlockEntity> entityType, Level level) {
      super(entityType, level);
      this.f_19794_ = true;
      this.m_20242_(true);
   }

   public RisingWallBlockEntity(Level level, BlockPos finalBlockPos, BlockState blockState, int startDelayTicks, int riseTicks) {
      this((EntityType<? extends RisingWallBlockEntity>)AnnoyingVillagersModEntities.RISING_WALL_BLOCK.get(), level);
      this.setFinalBlockPos(finalBlockPos);
      this.setBlockState(blockState);
      this.setStartDelayTicks(startDelayTicks);
      this.setRiseTicks(riseTicks);
      double x = (double)finalBlockPos.m_123341_() + 0.5;
      double y = getStartY(finalBlockPos);
      double z = (double)finalBlockPos.m_123343_() + 0.5;
      this.m_6034_(x, y, z);
      this.f_19854_ = x;
      this.f_19855_ = y;
      this.f_19856_ = z;
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(FINAL_BLOCK_POS, BlockPos.f_121853_);
      this.f_19804_.m_135372_(RENDER_BLOCK_STATE, Blocks.f_50016_.m_49966_());
      this.f_19804_.m_135372_(START_DELAY_TICKS, 0);
      this.f_19804_.m_135372_(RISE_TICKS, 10);
   }

   public BlockPos getFinalBlockPos() {
      return (BlockPos)this.f_19804_.m_135370_(FINAL_BLOCK_POS);
   }

   public void setFinalBlockPos(BlockPos pos) {
      this.f_19804_.m_135381_(FINAL_BLOCK_POS, pos);
   }

   public BlockState getBlockState() {
      return (BlockState)this.f_19804_.m_135370_(RENDER_BLOCK_STATE);
   }

   public void setBlockState(BlockState blockState) {
      this.f_19804_.m_135381_(RENDER_BLOCK_STATE, blockState);
   }

   public int getStartDelayTicks() {
      return (Integer)this.f_19804_.m_135370_(START_DELAY_TICKS);
   }

   public void setStartDelayTicks(int ticks) {
      this.f_19804_.m_135381_(START_DELAY_TICKS, Math.max(0, ticks));
   }

   public int getRiseTicks() {
      return (Integer)this.f_19804_.m_135370_(RISE_TICKS);
   }

   public void setRiseTicks(int ticks) {
      this.f_19804_.m_135381_(RISE_TICKS, Math.max(1, ticks));
   }

   public boolean isRiseStarted() {
      return this.f_19797_ >= this.getStartDelayTicks();
   }

   private static double getStartY(BlockPos finalBlockPos) {
      return (double)finalBlockPos.m_123342_() - 1.15;
   }

   private static double easeOutCubic(double t) {
      t = Math.max(0.0, Math.min(1.0, t));
      return 1.0 - Math.pow(1.0 - t, 3.0);
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.f_19797_ > 200) {
         this.m_146870_();
      } else {
         BlockPos finalPos = this.getFinalBlockPos();
         int activeTicks = this.f_19797_ - this.getStartDelayTicks();
         if (activeTicks < 0) {
            this.m_6034_((double)finalPos.m_123341_() + 0.5, getStartY(finalPos), (double)finalPos.m_123343_() + 0.5);
         } else {
            double progress = (double)activeTicks / (double)this.getRiseTicks();
            double eased = easeOutCubic(progress);
            double startY = getStartY(finalPos);
            double targetY = (double)finalPos.m_123342_();
            double y = startY + (targetY - startY) * eased;
            this.m_6034_((double)finalPos.m_123341_() + 0.5, y, (double)finalPos.m_123343_() + 0.5);
            if (!this.m_9236_().f_46443_ && !this.converted && activeTicks >= this.getRiseTicks()) {
               this.converted = true;
               this.convertToRealBlock();
            }
         }
      }
   }

   private void convertToRealBlock() {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         BlockState blockState = this.getBlockState();
         if (!blockState.m_60795_() && blockState.m_60799_() == RenderShape.MODEL) {
            BlockPos finalPos = this.getFinalBlockPos();
            if (this.canPlaceRealBlockAt(finalPos)) {
               serverLevel.m_46597_(finalPos, blockState);
               serverLevel.m_8767_(
                  new BlockParticleOption(ParticleTypes.f_123794_, blockState),
                  (double)finalPos.m_123341_() + 0.5,
                  (double)finalPos.m_123342_() + 0.5,
                  (double)finalPos.m_123343_() + 0.5,
                  20,
                  0.35,
                  0.35,
                  0.35,
                  0.08
               );
               serverLevel.m_5594_(null, finalPos, blockState.m_60827_().m_56777_(), SoundSource.BLOCKS, 1.0F, 0.85F + serverLevel.f_46441_.m_188501_() * 0.25F);
            }

            this.m_146870_();
         } else {
            this.m_146870_();
         }
      }
   }

   private boolean canPlaceRealBlockAt(BlockPos pos) {
      if (!this.m_9236_().m_6425_(pos).m_76178_()) {
         return false;
      } else {
         BlockState current = this.m_9236_().m_8055_(pos);
         return current.m_60795_() || current.m_247087_();
      }
   }

   protected void m_7380_(CompoundTag tag) {
      tag.m_128365_("FinalBlockPos", NbtUtils.m_129224_(this.getFinalBlockPos()));
      tag.m_128365_("BlockState", NbtUtils.m_129202_(this.getBlockState()));
      tag.m_128405_("StartDelayTicks", this.getStartDelayTicks());
      tag.m_128405_("RiseTicks", this.getRiseTicks());
      tag.m_128379_("Converted", this.converted);
   }

   protected void m_7378_(CompoundTag tag) {
      this.setFinalBlockPos(NbtUtils.m_129239_(tag.m_128469_("FinalBlockPos")));
      this.setBlockState(NbtUtils.m_247651_(this.m_9236_().m_246945_(Registries.f_256747_), tag.m_128469_("BlockState")));
      this.setStartDelayTicks(tag.m_128451_("StartDelayTicks"));
      this.setRiseTicks(tag.m_128451_("RiseTicks"));
      this.converted = tag.m_128471_("Converted");
   }

   public boolean m_6087_() {
      return false;
   }

   public boolean m_6097_() {
      return false;
   }

   public boolean m_6469_(DamageSource source, float amount) {
      return false;
   }

   public boolean m_6051_() {
      return false;
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }
}
