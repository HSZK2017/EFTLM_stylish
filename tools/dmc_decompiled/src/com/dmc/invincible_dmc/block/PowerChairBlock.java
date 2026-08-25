package com.dmc.invincible_dmc.block;

import com.dmc.invincible_dmc.entity.chair.PowerChairSeatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PowerChairBlock extends HorizontalDirectionalBlock {
   public PowerChairBlock(Properties properties) {
      super(properties);
      this.m_49959_((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_(f_54117_, Direction.NORTH));
   }

   @Nullable
   public BlockState m_5573_(@NotNull BlockPlaceContext context) {
      return (BlockState)this.m_49966_().m_61124_(f_54117_, context.m_8125_().m_122424_());
   }

   @NotNull
   public BlockState m_6843_(@NotNull BlockState state, @NotNull Rotation rotation) {
      return (BlockState)state.m_61124_(f_54117_, rotation.m_55954_((Direction)state.m_61143_(f_54117_)));
   }

   @NotNull
   public BlockState m_6943_(@NotNull BlockState state, @NotNull Mirror mirror) {
      return state.m_60717_(mirror.m_54846_((Direction)state.m_61143_(f_54117_)));
   }

   protected void m_7926_(@NotNull Builder<Block, BlockState> builder) {
      builder.m_61104_(new Property[]{f_54117_});
   }

   @NotNull
   public InteractionResult m_6227_(
      @NotNull BlockState state,
      @NotNull Level level,
      @NotNull BlockPos pos,
      @NotNull Player player,
      @NotNull InteractionHand hand,
      @NotNull BlockHitResult hitResult
   ) {
      if (hand == InteractionHand.MAIN_HAND && !player.m_20159_()) {
         if (!level.f_46443_) {
            PowerChairSeatEntity seat = (PowerChairSeatEntity)level.m_6443_(
                  PowerChairSeatEntity.class, new AABB(pos).m_82400_(0.25), entity -> entity.m_6084_()
               )
               .stream()
               .findFirst()
               .orElse(null);
            if (seat != null && seat.m_20160_()) {
               return InteractionResult.CONSUME;
            }

            if (seat == null) {
               seat = new PowerChairSeatEntity(level, pos, (Direction)state.m_61143_(f_54117_));
               level.m_7967_(seat);
            }

            if (!player.m_7998_(seat, false)) {
               seat.m_146870_();
               return InteractionResult.FAIL;
            }
         }

         return InteractionResult.m_19078_(level.f_46443_);
      } else {
         return InteractionResult.PASS;
      }
   }

   public void m_6810_(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
      if (!state.m_60713_(newState.m_60734_()) && !level.f_46443_) {
         level.m_45976_(PowerChairSeatEntity.class, new AABB(pos).m_82400_(0.25)).forEach(Entity::m_146870_);
      }

      super.m_6810_(state, level, pos, newState, isMoving);
   }
}
