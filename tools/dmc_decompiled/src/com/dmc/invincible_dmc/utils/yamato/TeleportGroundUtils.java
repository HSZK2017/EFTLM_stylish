package com.dmc.invincible_dmc.utils.yamato;

import com.dmc.invincible_dmc.block.DMCBlocks;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class TeleportGroundUtils {
   public static InTimeEvent create(int startFrame, float offsetY, int dashCap, float dashVelocity) {
      return create(startFrame, offsetY, null, 0.0F, 0.0F, dashCap, dashVelocity, false);
   }

   public static InTimeEvent create(int startFrame, float offsetY) {
      return create(startFrame, offsetY, null, 0.0F, 0.0F, 0, 0.0F, true);
   }

   public static InTimeEvent create(
      int startFrame, float offsetY, String jointName, float forwardOffset, float sideOffset, int dashCap, float dashVelocity, boolean isTp
   ) {
      float startTime = (float)startFrame / 60.0F;
      return InTimeEvent.create(startTime, (entityPatch, self, params) -> {
         LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
         if (!entity.m_9236_().f_46443_) {
            Vec3 startPos = entity.m_20182_();
            Vec3 groundPos = getGroundPosition(entityPatch, offsetY, jointName);
            if (forwardOffset != 0.0F || sideOffset != 0.0F) {
               Vec3 offset = calculateHorizontalOffset(entity, forwardOffset, sideOffset);
               groundPos = groundPos.m_82549_(offset);
            }

            if (!isTp) {
               dashToPosition(entityPatch, groundPos, dashCap, dashVelocity);
            } else {
               tpToPosition(entityPatch, groundPos);
            }
         }
      }, Side.SERVER);
   }

   public static void teleportToGround(LivingEntityPatch<?> entityPatch, float offsetY) {
      LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
      if (!entity.m_9236_().f_46443_) {
         tpToPosition(entityPatch, getGroundPosition(entityPatch, offsetY, null));
      }
   }

   public static InTimeEvent createCoordAware(int startFrame, float offsetY) {
      float startTime = (float)startFrame / 60.0F;
      return InTimeEvent.create(startTime, (entityPatch, self, params) -> {
         LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
         if (!entity.m_9236_().f_46443_) {
            Vec3 startPos = entity.m_20182_();
            Vec3 groundPos = getGroundPosition(entityPatch, offsetY, null);
            AnimationPlayer player = DMCAnimationUtils.getMainPlayer(entityPatch);
            double remainingCoordY = 0.0;
            if (player != null && DMCAnimationUtils.getCurrentAnimationAccessor(player) != null) {
               DynamicAnimation anim = DMCAnimationUtils.getCurrentAnimation(player);
               if (anim != null) {
                  Optional<TransformSheet> coordOpt = anim.getProperty(ActionAnimationProperty.COORD);
                  if (coordOpt.isPresent()) {
                     TransformSheet coord = coordOpt.get();
                     Keyframe[] kfs = coord.getKeyframes();
                     if (kfs.length > 0) {
                        int idx = Math.min(startFrame, kfs.length - 1);
                        Vec3f last = kfs[kfs.length - 1].transform().translation();
                        Vec3f cur = kfs[idx].transform().translation();
                        remainingCoordY = (double)(last.y - cur.y);
                     }
                  }
               }
            }

            Vec3 target = new Vec3(startPos.f_82479_, groundPos.f_82480_ - remainingCoordY, startPos.f_82481_);
            double dropDist = Math.abs(startPos.f_82480_ - target.f_82480_);
            if (dropDist > 8.0) {
               entity.f_19789_ = 0.0F;
               if (entity instanceof ServerPlayer sp) {
                  sp.f_8906_.m_9774_(target.f_82479_, target.f_82480_, target.f_82481_, entity.m_146908_(), entity.m_146909_());
               } else {
                  entity.m_6021_(target.f_82479_, target.f_82480_, target.f_82481_);
               }
            } else {
               entity.m_6478_(MoverType.SELF, target.m_82546_(startPos));
               entity.f_19864_ = true;
            }
         }
      }, Side.SERVER);
   }

   public static Vec3 getGroundPosition(LivingEntityPatch<?> entityPatch, float offsetY, String jointName) {
      LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
      if (jointName != null && entityPatch.getArmature() != null) {
         Armature armature = entityPatch.getArmature();
         Joint joint = armature.searchJointByName(jointName);
         if (joint != null) {
            OpenMatrix4f transform = armature.getBoundTransformFor(entityPatch.getAnimator().getPose(1.0F), joint);
            OpenMatrix4f correction = new OpenMatrix4f().rotate((float)(-Math.toRadians((double)(entity.m_146908_() + 180.0F))), new Vec3f(0.0F, 1.0F, 0.0F));
            OpenMatrix4f.mul(correction, transform, transform);
            double x = (double)transform.m30 + entity.m_20185_();
            double y = (double)transform.m31 + entity.m_20186_() + (double)offsetY;
            double z = (double)transform.m32 + entity.m_20189_();
            return findActualGround(entity, x, y, z);
         }
      }

      return getSimpleGroundPosition(entity, offsetY);
   }

   private static Vec3 calculateHorizontalOffset(LivingEntity entity, float forward, float side) {
      float yRotRad = (float)Math.toRadians((double)entity.m_146908_());
      float xOffset = -forward * (float)Math.sin((double)yRotRad) + side * (float)Math.cos((double)yRotRad);
      float zOffset = forward * (float)Math.cos((double)yRotRad) + side * (float)Math.sin((double)yRotRad);
      return new Vec3((double)xOffset, 0.0, (double)zOffset);
   }

   private static void dashToPosition(LivingEntityPatch<?> entityPatch, Vec3 targetPos, int dashCap, float dashVelocity) {
      LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
      Vec3 currentPos = entity.m_20182_();
      Vec3 safePos = resolveVerticalCollision(entity, targetPos);
      Vec3 moveVec = safePos.m_82546_(currentPos);
      double distance = moveVec.m_82553_();
      if (!(distance < 0.1)) {
         float originalStepHeight = entity.m_274421_();
         entity.m_274367_(1.25F);
         entity.m_6478_(MoverType.SELF, moveVec);
         entity.m_274367_(originalStepHeight);
         entity.f_19864_ = true;
         Vec3 afterPos = entity.m_20182_();
      }
   }

   private static void tpToPosition(LivingEntityPatch<?> entityPatch, Vec3 targetPos) {
      LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
      Vec3 before = entity.m_20182_();
      Vec3 safePos = resolveVerticalCollision(entity, targetPos);
      entity.f_19789_ = 0.0F;
      if (entity instanceof ServerPlayer serverPlayer) {
         serverPlayer.f_8906_.m_9774_(safePos.f_82479_, safePos.f_82480_, safePos.f_82481_, entity.m_146908_(), entity.m_146909_());
      } else {
         entity.m_6021_(safePos.f_82479_, safePos.f_82480_, safePos.f_82481_);
      }
   }

   public static Vec3 resolveVerticalCollision(LivingEntity entity, Vec3 targetPos) {
      Level level = entity.m_9236_();
      BlockPos basePos = BlockPos.m_274446_(targetPos);

      for (int i = 0; i < 5; i++) {
         BlockPos feetPos = basePos.m_6630_(i);
         BlockPos headPos = feetPos.m_7494_();
         if (isPassable(level, feetPos) && isPassable(level, headPos)) {
            return new Vec3(targetPos.f_82479_, (double)feetPos.m_123342_() + 0.05, targetPos.f_82481_);
         }
      }

      return targetPos;
   }

   private static boolean isPassable(Level level, BlockPos pos) {
      BlockState state = level.m_8055_(pos);
      return state.m_60713_((Block)DMCBlocks.VOID_BARRIER.get()) ? false : state.m_60795_() || state.m_60812_(level, pos).m_83281_();
   }

   private static Vec3 findActualGround(LivingEntity entity, double x, double y, double z) {
      Level level = entity.m_9236_();
      Vec3 rayStart = new Vec3(x, y + 0.5, z);
      Vec3 rayEnd = new Vec3(x, (double)level.m_141937_(), z);
      BlockHitResult hitResult = level.m_45547_(new ClipContext(rayStart, rayEnd, net.minecraft.world.level.ClipContext.Block.COLLIDER, Fluid.NONE, entity));
      if (hitResult.m_6662_() == Type.BLOCK) {
         return new Vec3(x, hitResult.m_82450_().m_7098_(), z);
      } else {
         MutableBlockPos scanPos = new MutableBlockPos(x, rayStart.f_82480_, z);
         int worldBottom = level.m_141937_();
         int worldTop = level.m_151558_();

         while (scanPos.m_123342_() >= worldBottom) {
            if (level.m_8055_(scanPos).m_60713_((Block)DMCBlocks.VOID_BARRIER.get())) {
               return new Vec3(x, (double)scanPos.m_123342_() + 0.05, z);
            }

            scanPos.m_122184_(0, -1, 0);
         }

         scanPos.m_122169_(x, rayStart.f_82480_ + 1.0, z);

         while (scanPos.m_123342_() <= worldTop) {
            if (level.m_8055_(scanPos).m_60713_((Block)DMCBlocks.VOID_BARRIER.get())) {
               return new Vec3(x, (double)scanPos.m_123342_() + 0.05, z);
            }

            scanPos.m_122184_(0, 1, 0);
         }

         return new Vec3(x, (double)(level.m_141937_() + 1), z);
      }
   }

   public static Vec3 getSimpleGroundPosition(LivingEntity entity, float offsetY) {
      double y = entity.m_20186_() + (double)offsetY;
      return findActualGround(entity, entity.m_20185_(), y, entity.m_20189_());
   }
}
