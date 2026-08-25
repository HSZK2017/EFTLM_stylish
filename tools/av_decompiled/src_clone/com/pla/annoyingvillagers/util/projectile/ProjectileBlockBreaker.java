package com.pla.annoyingvillagers.util.projectile;

import com.pla.annoyingvillagers.clazz.ProjectileBreakableBlocks;
import com.pla.annoyingvillagers.config.AnnoyingVillagersConfig;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;

public final class ProjectileBlockBreaker {
   private static final float FULL_ARROW_SPEED = 3.0F;
   private static final float FULL_TRIDENT_SPEED = 2.5F;

   private ProjectileBlockBreaker() {
   }

   public static float computeInitialPower(AbstractArrow proj) {
      float speed = (float)proj.m_20184_().m_82553_();
      float full = proj instanceof ThrownTrident ? 2.5F : 3.0F;
      return Mth.m_14036_(speed / full, 0.0F, 1.0F);
   }

   public static BlockHitResult clip(Level level, ClipContext ctx, AbstractArrow proj) {
      if (!(Boolean)AnnoyingVillagersConfig.ARROW_CAN_BREAK_BLOCK.get()) {
         return level.m_45547_(ctx);
      } else if (!(proj instanceof Arrow) && !(proj instanceof ThrownTrident)) {
         return level.m_45547_(ctx);
      } else if (proj instanceof BreakPowerHolder holder) {
         if (level.f_46443_) {
            return level.m_45547_(ctx);
         } else {
            float power = holder.getBreakPower();
            if (power <= 0.0F) {
               return level.m_45547_(ctx);
            } else {
               Vec3 from = ctx.m_45702_();
               Vec3 to = ctx.m_45693_();
               CollisionContext collision = CollisionContext.m_82750_(proj);
               BiFunction<ClipContext, BlockPos, BlockHitResult> hitTest = (c, pos) -> {
                  BlockState state = level.m_8055_(pos);
                  ProjectileBreakableBlocks rule = ProjectileBreakableBlocks.find(state);
                  VoxelShape shape = state.m_60742_(level, pos, collision);
                  if (rule != null && shape.m_83281_()) {
                     shape = state.m_60651_(level, pos, collision);
                  }

                  BlockHitResult bhr = level.m_45558_(from, to, pos, shape, state);
                  if (bhr == null) {
                     return null;
                  } else if (state.m_60800_(level, pos) < 0.0F) {
                     holder.setBreakPower(0.0F);
                     return bhr;
                  } else if (rule == null) {
                     holder.setBreakPower(0.0F);
                     return bhr;
                  } else {
                     float currentPower = holder.getBreakPower();
                     if (currentPower < rule.requiredPower) {
                        holder.setBreakPower(0.0F);
                        return bhr;
                     } else {
                        Entity breaker = proj.m_19749_();
                        if (!canBreak(level, breaker, pos, state)) {
                           holder.setBreakPower(0.0F);
                           return bhr;
                        } else {
                           level.m_46953_(pos, true, breaker);
                           holder.setBreakPower(currentPower - rule.powerCost);
                           return null;
                        }
                     }
                  }
               };
               Function<ClipContext, BlockHitResult> miss = c -> {
                  Vec3 d = from.m_82546_(to);
                  return BlockHitResult.m_82426_(to, Direction.m_122366_(d.f_82479_, d.f_82480_, d.f_82481_), BlockPos.m_274446_(to));
               };
               return (BlockHitResult)BlockGetter.m_151361_(from, to, ctx, hitTest, miss);
            }
         }
      } else {
         return level.m_45547_(ctx);
      }
   }

   private static boolean canBreak(Level level, Entity breaker, BlockPos pos, BlockState state) {
      return breaker instanceof Player player ? !MinecraftForge.EVENT_BUS.post(new BreakEvent(level, pos, state, player)) : true;
   }
}
