package com.pla.annoyingvillagers.entity.goal;

import com.pla.annoyingvillagers.clazz.AVNpc;
import com.pla.annoyingvillagers.entity.PlayerNpcEntity;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFightIronSpell;
import com.pla.annoyingvillagers.util.CombatBehaviour;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.shapes.VoxelShape;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class WaterEnderPearlEscapeGoal extends Goal {
   private static final double SEARCH_RADIUS = 48.0;
   private static final double SEARCH_RADIUS_SQR = 2304.0;
   private static final int SEARCH_INTERVAL_TICKS = 10;
   private static final float YAW_STEP = 10.0F;
   private static final float[] PITCHES = new float[]{-20.0F, -8.0F, 0.0F, 2.0F, 4.0F, 8.0F, 14.0F, 24.0F, 36.0F};
   private final Mob mob;
   private Vec3 pearlTarget;
   private long nextSearchTick;

   public WaterEnderPearlEscapeGoal(Mob mob) {
      this.mob = mob;
      this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean m_8036_() {
      if (this.mob.m_9236_() instanceof ServerLevel serverLevel) {
         if (this.mob.m_6084_()
            && !this.mob.m_213877_()
            && !this.mob.m_21224_()
            && !this.mob.m_21525_()
            && !this.mob.m_20159_()
            && this.mob.m_20069_()
            && this.canUsePearl()
            && !this.isLongHitAnimationActive()) {
            long gameTime = serverLevel.m_46467_();
            if (gameTime < this.nextSearchTick) {
               return false;
            } else {
               this.nextSearchTick = gameTime + 10L + (long)this.mob.m_217043_().m_188503_(5);
               this.pearlTarget = this.findVisibleEscapeTarget(serverLevel);
               return this.pearlTarget != null;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean m_8045_() {
      return false;
   }

   public void m_8056_() {
      if (this.pearlTarget != null) {
         LivingEntityPatch<?> patch = this.getLivingEntityPatch();
         if (patch != null) {
            patch.playAnimationSynchronized(AnimsEpicFightIronSpell.CASTING_ONE_HAND_TOP, 0.0F);
         }

         this.mob.m_21573_().m_26573_();
         this.mob.m_21563_().m_24950_(this.pearlTarget.f_82479_, this.pearlTarget.f_82480_, this.pearlTarget.f_82481_, 60.0F, 60.0F);
         CombatBehaviour.throwEnderPearlAt(this.mob, this.pearlTarget);
         this.setPearlCooldown();
         this.pearlTarget = null;
      }
   }

   private boolean canUsePearl() {
      if (this.mob instanceof PlayerNpcEntity playerNpcEntity) {
         return !playerNpcEntity.isHealing() && playerNpcEntity.getEnderPearlCooldown() == 0;
      } else {
         return !(this.mob instanceof AVNpc avNpc) ? false : !avNpc.isHealing() && avNpc.getEnderPearlCooldown() == 0;
      }
   }

   private void setPearlCooldown() {
      if (this.mob instanceof PlayerNpcEntity playerNpcEntity) {
         playerNpcEntity.setEnderPearlCooldown();
      }

      if (this.mob instanceof AVNpc avNpc) {
         avNpc.setEnderPearlCooldown();
      }
   }

   private boolean isLongHitAnimationActive() {
      LivingEntityPatch<?> patch = this.getLivingEntityPatch();
      if (patch == null) {
         return false;
      } else {
         AnimationPlayer player = patch.getAnimator().getPlayerFor(null);
         if (player == null) {
            return false;
         } else {
            AssetAccessor<? extends StaticAnimation> animation = player.getRealAnimation();
            return animation != null && EpicfightUtil.isLongHitAnimation(animation, patch);
         }
      }
   }

   private LivingEntityPatch<?> getLivingEntityPatch() {
      return (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(this.mob, LivingEntityPatch.class);
   }

   private Vec3 findVisibleEscapeTarget(ServerLevel level) {
      Vec3 eyePos = this.mob.m_20299_(1.0F);
      Set<BlockPos> checked = new HashSet<>();
      Vec3 bestTarget = null;
      double bestDistance = Double.MAX_VALUE;

      for (float pitch : PITCHES) {
         for (float yawOffset = 0.0F; yawOffset < 360.0F; yawOffset += 10.0F) {
            Vec3 direction = directionFromRotation(pitch, this.mob.m_146908_() + yawOffset);
            Vec3 end = eyePos.m_82549_(direction.m_82490_(48.0));
            BlockHitResult hit = level.m_45547_(new ClipContext(eyePos, end, Block.COLLIDER, Fluid.NONE, this.mob));
            if (hit.m_6662_() == Type.BLOCK) {
               BlockPos hitPos = hit.m_82425_().m_7949_();
               if (checked.add(hitPos)) {
                  Vec3 target = this.getEscapeTarget(level, hitPos);
                  if (target != null) {
                     double distance = eyePos.m_82557_(target);
                     if (distance <= 2304.0 && distance < bestDistance) {
                        bestDistance = distance;
                        bestTarget = target;
                     }
                  }
               }
            }
         }
      }

      return bestTarget;
   }

   private Vec3 getEscapeTarget(ServerLevel level, BlockPos pos) {
      BlockState state = level.m_8055_(pos);
      VoxelShape shape = state.m_60812_(level, pos);
      if (!state.m_60795_() && state.m_280555_() && !shape.m_83281_() && level.m_6425_(pos).m_76178_()) {
         double surfaceY = (double)pos.m_123342_() + shape.m_83297_(Axis.Y);
         Vec3 standCenter = new Vec3((double)pos.m_123341_() + 0.5, surfaceY, (double)pos.m_123343_() + 0.5);
         AABB currentBox = this.mob.m_20191_();
         double halfWidth = currentBox.m_82362_() * 0.5;
         AABB standBox = new AABB(
            standCenter.f_82479_ - halfWidth,
            surfaceY,
            standCenter.f_82481_ - halfWidth,
            standCenter.f_82479_ + halfWidth,
            surfaceY + currentBox.m_82376_(),
            standCenter.f_82481_ + halfWidth
         );
         return level.m_45756_(this.mob, standBox) && !level.m_46855_(standBox) ? standCenter.m_82520_(0.0, 0.15, 0.0) : null;
      } else {
         return null;
      }
   }

   private static Vec3 directionFromRotation(float pitch, float yaw) {
      float radians = (float) (Math.PI / 180.0);
      float pitchRad = pitch * radians;
      float yawRad = -yaw * radians - (float) Math.PI;
      float x = Mth.m_14031_(yawRad) * Mth.m_14089_(pitchRad);
      float y = -Mth.m_14031_(pitchRad);
      float z = Mth.m_14089_(yawRad) * Mth.m_14089_(pitchRad);
      return new Vec3((double)x, (double)y, (double)z);
   }
}
