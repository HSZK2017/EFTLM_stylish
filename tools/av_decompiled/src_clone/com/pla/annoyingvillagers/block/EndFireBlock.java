package com.pla.annoyingvillagers.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class EndFireBlock extends BaseFireBlock {
   public static final IntegerProperty AGE = BlockStateProperties.f_61410_;

   public EndFireBlock(Properties properties) {
      super(properties, 3.0F);
      this.m_49959_((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_(AGE, 0));
   }

   protected void m_7926_(Builder<Block, BlockState> builder) {
      super.m_7926_(builder);
      builder.m_61104_(new Property[]{AGE});
   }

   public ItemStack m_7397_(BlockGetter g, BlockPos p, BlockState s) {
      return ItemStack.f_41583_;
   }

   public BlockState m_7417_(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
      return state.m_60710_(level, currentPos) ? state : Blocks.f_50016_.m_49966_();
   }

   protected boolean m_7599_(BlockState pState) {
      return true;
   }

   private static int getEndFireTickDelay(RandomSource random) {
      return 40 + random.m_188503_(20);
   }

   public void m_6807_(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
      super.m_6807_(state, level, pos, oldState, isMoving);
      if (!level.f_46443_) {
         level.m_186460_(pos, this, getEndFireTickDelay(level.f_46441_));
      }
   }

   public void m_213897_(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      level.m_186460_(pos, this, getEndFireTickDelay(random));
      if (!state.m_60710_(level, pos)) {
         level.m_7471_(pos, false);
      } else {
         int age = (Integer)state.m_61143_(AGE);
         int newAge = Math.min(15, age + 1 + random.m_188503_(2));
         if (newAge != age) {
            level.m_7731_(pos, (BlockState)state.m_61124_(AGE, newAge), 4);
            state = level.m_8055_(pos);
            age = newAge;
         }

         if (level.m_46758_(pos) && random.m_188501_() < 0.05F + (float)age * 0.01F) {
            level.m_7471_(pos, false);
         } else {
            if (age >= 15 && random.m_188503_(6) == 0) {
               level.m_7471_(pos, false);
            }
         }
      }
   }
}
