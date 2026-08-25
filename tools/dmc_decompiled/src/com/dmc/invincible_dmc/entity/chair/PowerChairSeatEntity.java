package com.dmc.invincible_dmc.entity.chair;

import com.dmc.invincible_dmc.block.DMCBlocks;
import com.dmc.invincible_dmc.block.PowerChairBlock;
import com.dmc.invincible_dmc.entity.DMCEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public final class PowerChairSeatEntity extends Entity {
   private BlockPos chairPos = BlockPos.f_121853_;

   public PowerChairSeatEntity(EntityType<? extends PowerChairSeatEntity> entityType, Level level) {
      super(entityType, level);
      this.f_19794_ = true;
   }

   public PowerChairSeatEntity(Level level, BlockPos chairPos, Direction facing) {
      this((EntityType<? extends PowerChairSeatEntity>)DMCEntities.POWER_CHAIR_SEAT.get(), level);
      this.chairPos = chairPos.m_7949_();
      this.m_6034_((double)chairPos.m_123341_() + 0.5, (double)chairPos.m_123342_() - 0.05, (double)chairPos.m_123343_() + 0.5);
      this.m_146922_(facing.m_122435_());
   }

   public void m_8119_() {
      super.m_8119_();
      if (!this.m_9236_().f_46443_) {
         BlockState state = this.m_9236_().m_8055_(this.chairPos);
         if (state.m_60713_((Block)DMCBlocks.POWER_CHAIR.get()) && this.m_20160_()) {
            this.m_146922_(((Direction)state.m_61143_(PowerChairBlock.f_54117_)).m_122435_());
         } else {
            this.m_146870_();
         }
      }
   }

   protected boolean m_7310_(@NotNull Entity passenger) {
      return this.m_20197_().isEmpty();
   }

   protected void m_19956_(@NotNull Entity passenger, @NotNull MoveFunction callback) {
      if (this.m_20363_(passenger)) {
         callback.m_20372_(passenger, this.m_20185_(), this.m_20186_(), this.m_20189_());
      }
   }

   @NotNull
   public Vec3 m_7688_(@NotNull LivingEntity passenger) {
      BlockState state = this.m_9236_().m_8055_(this.chairPos);
      Direction facing = state.m_60713_((Block)DMCBlocks.POWER_CHAIR.get()) ? (Direction)state.m_61143_(PowerChairBlock.f_54117_) : Direction.NORTH;
      return Vec3.m_82539_(this.chairPos.m_121945_(facing));
   }

   public boolean m_6087_() {
      return false;
   }

   public boolean m_6094_() {
      return false;
   }

   public boolean m_20145_() {
      return true;
   }

   protected void m_8097_() {
   }

   protected void m_7378_(@NotNull CompoundTag tag) {
   }

   protected void m_7380_(@NotNull CompoundTag tag) {
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }
}
