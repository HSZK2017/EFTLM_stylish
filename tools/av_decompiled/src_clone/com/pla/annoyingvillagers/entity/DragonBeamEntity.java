package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.block.EndFireBlock;
import com.pla.annoyingvillagers.client.engine.ClientVfxRouter;
import com.pla.annoyingvillagers.client.engine.PhotonClientFxUtil;
import com.pla.annoyingvillagers.config.AnnoyingVillagersClientConfig;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModBlocks;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModParticleTypes;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.item.EnderSlayerScytheItem;
import com.pla.annoyingvillagers.util.AAAParticlesUtil;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import com.pla.annoyingvillagers.util.ScreenShakeUtil;
import com.pla.annoyingvillagers.util.WeaponEnchantmentDamageUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class DragonBeamEntity extends Entity {
   public HerobrineDragonEntity caster;
   public LivingEntity target;
   public double endPosX;
   public double endPosY;
   public double endPosZ;
   public double collidePosX;
   public double collidePosY;
   public double collidePosZ;
   public double prevCollidePosX;
   public double prevCollidePosY;
   public double prevCollidePosZ;
   public float renderYaw;
   public float renderPitch;
   public boolean on;
   public Direction blockSide;
   private int power;
   private static final EntityDataAccessor<Float> YAW = SynchedEntityData.m_135353_(DragonBeamEntity.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Float> PITCH = SynchedEntityData.m_135353_(DragonBeamEntity.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Integer> DURATION = SynchedEntityData.m_135353_(DragonBeamEntity.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> CASTER = SynchedEntityData.m_135353_(DragonBeamEntity.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> TARGET = SynchedEntityData.m_135353_(DragonBeamEntity.class, EntityDataSerializers.f_135028_);
   public float prevYaw;
   public float prevPitch;
   private Vec3 targetPos;
   private static final float DRAGON_PHOTON_VISUAL_BASE_LENGTH = 192.0F;
   private boolean renderBeam = false;
   private boolean playSound = false;
   private static final EntityDataAccessor<Boolean> USE_NO_VFX_THUNDER = SynchedEntityData.m_135353_(DragonBeamEntity.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Vector3f> THUNDER_START = SynchedEntityData.m_135353_(DragonBeamEntity.class, EntityDataSerializers.f_268676_);
   private static final EntityDataAccessor<Vector3f> THUNDER_STOP = SynchedEntityData.m_135353_(DragonBeamEntity.class, EntityDataSerializers.f_268676_);
   private static final EntityDataAccessor<Boolean> HAS_TARGET_POS = SynchedEntityData.m_135353_(DragonBeamEntity.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Vector3f> TARGET_POS = SynchedEntityData.m_135353_(DragonBeamEntity.class, EntityDataSerializers.f_268676_);

   public DragonBeamEntity(EntityType<? extends DragonBeamEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.on = true;
      this.blockSide = null;
      this.f_19811_ = true;
   }

   public DragonBeamEntity(
      EntityType<? extends DragonBeamEntity> type,
      Level world,
      HerobrineDragonEntity caster,
      LivingEntity target,
      double x,
      double y,
      double z,
      int duration,
      int pow
   ) {
      this(type, world);
      this.caster = caster;
      this.target = target;
      this.setDuration(duration);
      this.setPower(pow);
      this.m_6034_(x, y, z);
      Vec3 from = new Vec3(x, y, z);
      Vec3 to = target.m_20299_(1.0F);
      this.setTargetPos(to);
      float yawRad = yawTowards(from, to);
      float pitchRad = pitchTowards(from, to);
      this.setYaw(yawRad);
      this.setPitch(pitchRad);
      if (world.f_46443_) {
         this.renderYaw = yawRad;
         this.renderPitch = pitchRad;
      }

      if (!world.f_46443_) {
         this.setCasterID(caster.m_19879_());
         this.setTargetID(target.m_19879_());
      }

      this.calculateEndPos();
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(YAW, 0.0F);
      this.f_19804_.m_135372_(PITCH, 0.0F);
      this.f_19804_.m_135372_(DURATION, 0);
      this.f_19804_.m_135372_(CASTER, -1);
      this.f_19804_.m_135372_(TARGET, -1);
      this.f_19804_.m_135372_(USE_NO_VFX_THUNDER, false);
      this.f_19804_.m_135372_(THUNDER_START, new Vector3f());
      this.f_19804_.m_135372_(THUNDER_STOP, new Vector3f());
      this.f_19804_.m_135372_(HAS_TARGET_POS, false);
      this.f_19804_.m_135372_(TARGET_POS, new Vector3f());
   }

   public void setTargetID(int id) {
      this.f_19804_.m_135381_(TARGET, id);
   }

   public int getTargetID() {
      return (Integer)this.f_19804_.m_135370_(TARGET);
   }

   protected void m_7378_(@NotNull CompoundTag compoundTag) {
   }

   protected void m_7380_(@NotNull CompoundTag compoundTag) {
   }

   public void setUseNoVfxThunder(boolean noVfxThunder) {
      this.f_19804_.m_135381_(USE_NO_VFX_THUNDER, noVfxThunder);
   }

   public boolean isSetUseNoVfxThunder() {
      return (Boolean)this.f_19804_.m_135370_(USE_NO_VFX_THUNDER);
   }

   public Vec3 getThunderStartVec3() {
      Vector3f vector3f = (Vector3f)this.f_19804_.m_135370_(THUNDER_START);
      return new Vec3((double)vector3f.x, (double)vector3f.y, (double)vector3f.z);
   }

   public Vec3 getThunderStopVec3() {
      Vector3f vector3f = (Vector3f)this.f_19804_.m_135370_(THUNDER_STOP);
      return new Vec3((double)vector3f.x, (double)vector3f.y, (double)vector3f.z);
   }

   public void setThunderStartStop(Vec3 from, Vec3 to) {
      this.f_19804_.m_135381_(THUNDER_START, new Vector3f((float)from.f_82479_, (float)from.f_82480_, (float)from.f_82481_));
      this.f_19804_.m_135381_(THUNDER_STOP, new Vector3f((float)to.f_82479_, (float)to.f_82480_, (float)to.f_82481_));
   }

   private void setTargetPos(Vec3 pos) {
      if (pos != null) {
         this.targetPos = pos;
         this.f_19804_.m_135381_(TARGET_POS, new Vector3f((float)pos.f_82479_, (float)pos.f_82480_, (float)pos.f_82481_));
         this.f_19804_.m_135381_(HAS_TARGET_POS, true);
      }
   }

   private Vec3 getStoredTargetPos() {
      if (this.targetPos != null) {
         return this.targetPos;
      } else if (!(Boolean)this.f_19804_.m_135370_(HAS_TARGET_POS)) {
         return null;
      } else {
         Vector3f stored = (Vector3f)this.f_19804_.m_135370_(TARGET_POS);
         this.targetPos = new Vec3((double)stored.x, (double)stored.y, (double)stored.z);
         return this.targetPos;
      }
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   @NotNull
   public PushReaction m_7752_() {
      return PushReaction.IGNORE;
   }

   public float getYaw() {
      return (Float)this.m_20088_().m_135370_(YAW);
   }

   public void setYaw(float yaw) {
      this.m_20088_().m_135381_(YAW, yaw);
   }

   public float getPitch() {
      return (Float)this.m_20088_().m_135370_(PITCH);
   }

   public void setPitch(float pitch) {
      this.m_20088_().m_135381_(PITCH, pitch);
   }

   public int getDuration() {
      return (Integer)this.m_20088_().m_135370_(DURATION);
   }

   public void setDuration(int duration) {
      this.m_20088_().m_135381_(DURATION, duration);
   }

   public int getCasterID() {
      return (Integer)this.m_20088_().m_135370_(CASTER);
   }

   public void setCasterID(int id) {
      this.m_20088_().m_135381_(CASTER, id);
   }

   public void setPower(int power) {
      this.power = power;
   }

   private float getDamage() {
      LivingEntity summoner = this.caster != null ? this.caster.getSummoner() : null;
      return WeaponEnchantmentDamageUtil.addSharpnessBonus((float)this.power, summoner, EnderSlayerScytheItem.class);
   }

   private static float yawTowards(Vec3 from, Vec3 to) {
      Vec3 d = to.m_82546_(from);
      return (float)Math.atan2(d.f_82481_, d.f_82479_);
   }

   private static float pitchTowards(Vec3 from, Vec3 to) {
      Vec3 d = to.m_82546_(from);
      double len = d.m_82553_();
      return len == 0.0 ? 0.0F : (float)Math.asin(d.f_82480_ / len);
   }

   private void calculateEndPos() {
      double radius = 128.0;
      double yaw = (double)this.getYaw();
      double pitch = (double)this.getPitch();
      this.endPosX = this.m_20185_() + radius * Math.cos(yaw) * Math.cos(pitch);
      this.endPosZ = this.m_20189_() + radius * Math.sin(yaw) * Math.cos(pitch);
      this.endPosY = this.m_20186_() + radius * Math.sin(pitch);
   }

   public DragonBeamEntity.DragonBeamHitResult raytraceEntities(Level world, Vec3 from, Vec3 to, boolean ignoreBlockWithoutBoundingBox) {
      DragonBeamEntity.DragonBeamHitResult result = new DragonBeamEntity.DragonBeamHitResult();
      result.setBlockHit(world.m_45547_(new ClipContext(from, to, Block.COLLIDER, Fluid.NONE, this)));
      result.setBlockHit(world.m_45547_(new ClipContext(from, to, Block.COLLIDER, Fluid.NONE, this)));
      if (result.blockHit != null) {
         Vec3 hitVec = result.blockHit.m_82450_();
         BlockPos hitBlock = result.blockHit.m_82425_();
         this.collidePosX = hitVec.f_82479_;
         this.collidePosY = hitVec.f_82480_;
         this.collidePosZ = hitVec.f_82481_;
         this.blockSide = result.blockHit.m_82434_();
         if (world.f_46443_) {
            ClientVfxRouter.run(
               AnnoyingVillagersClientConfig.VfxEffect.DRAGON_BEAM_HIT,
               () -> PhotonClientFxUtil.spawnAt(world, "dragonhitfire", hitVec),
               () -> {
                  AAAParticlesUtil.sendDragonBeamHit(world, hitBlock);
                  return true;
               },
               () -> {
                  world.m_6493_(
                     ParticleTypes.f_123813_,
                     true,
                     (double)hitBlock.m_123341_(),
                     (double)hitBlock.m_123342_() + 1.0,
                     (double)hitBlock.m_123343_(),
                     0.0,
                     0.0,
                     0.0
                  );
                  world.m_6493_(
                     (ParticleOptions)AnnoyingVillagersModParticleTypes.METEORITE_TRAIL.get(),
                     true,
                     (double)hitBlock.m_123341_(),
                     (double)hitBlock.m_123342_() + 1.0,
                     (double)hitBlock.m_123343_(),
                     0.0,
                     0.0,
                     0.0
                  );
                  world.m_6493_(
                     ParticleTypes.f_123747_,
                     true,
                     (double)hitBlock.m_123341_(),
                     (double)hitBlock.m_123342_() + 1.0,
                     (double)hitBlock.m_123343_(),
                     0.0,
                     0.0,
                     0.0
                  );
               }
            );
         }

         if (!world.f_46443_) {
            boolean shouldBreak = true;
            if (this.target != null && this.target.m_6084_()) {
               double hitDist2 = from.m_82557_(hitVec);
               double targetDist2 = from.m_82557_(this.target.m_20299_(1.0F));
               shouldBreak = hitDist2 + 1.0E-6 < targetDist2;
               BlockPos targetFeet = this.target.m_20183_();
               BlockPos targetEyes = BlockPos.m_274446_(this.target.m_20299_(1.0F));
               if (hitBlock.m_123342_() >= targetFeet.m_123342_() && hitBlock.m_123342_() <= targetEyes.m_123342_()) {
                  shouldBreak = false;
               }
            }

            if (shouldBreak) {
               BlockState hitState = world.m_8055_(hitBlock);
               if (!hitState.m_60795_()) {
                  if (hitState.m_60800_(world, hitBlock) > 0.0F) {
                     world.m_46953_(hitBlock, true, this.caster);
                  }

                  BlockPos above = hitBlock.m_7494_();
                  if (world.m_8055_(above).m_60795_()) {
                     world.m_46597_(above, ((EndFireBlock)AnnoyingVillagersModBlocks.END_FIRE.get()).m_49966_());
                  }
               }
            }

            if (world.m_8055_(hitBlock.m_7494_()).m_60795_() && world.m_8055_(hitBlock).m_60804_(world, hitBlock)) {
               world.m_46597_(hitBlock.m_7494_(), ((EndFireBlock)AnnoyingVillagersModBlocks.END_FIRE.get()).m_49966_());
            }
         }
      } else {
         this.collidePosX = this.endPosX;
         this.collidePosY = this.endPosY;
         this.collidePosZ = this.endPosZ;
         this.blockSide = null;
      }

      for (LivingEntity entity : world.m_45976_(
         LivingEntity.class,
         new AABB(
               Math.min(this.m_20185_(), this.collidePosX),
               Math.min(this.m_20186_(), this.collidePosY),
               Math.min(this.m_20189_(), this.collidePosZ),
               Math.max(this.m_20185_(), this.collidePosX),
               Math.max(this.m_20186_(), this.collidePosY),
               Math.max(this.m_20189_(), this.collidePosZ)
            )
            .m_82377_(1.0, 1.0, 1.0)
      )) {
         if (entity != this.caster) {
            float pad = entity.m_6143_() + 0.5F;
            AABB aabb = entity.m_20191_().m_82377_((double)pad, (double)pad, (double)pad);
            Optional<Vec3> hit = aabb.m_82371_(from, to);
            if (aabb.m_82390_(from)) {
               result.addEntityHit(entity);
            } else if (hit.isPresent()) {
               result.addEntityHit(entity);
            }
         }
      }

      return result;
   }

   public boolean isRenderable() {
      return this.target != null && this.target.m_6084_() || this.getStoredTargetPos() != null;
   }

   private Vec3 getBeamStopPos() {
      if (this.target != null && this.target.m_6084_()) {
         return this.target.m_20299_(1.0F);
      } else {
         Vec3 storedTargetPos = this.getStoredTargetPos();
         return storedTargetPos != null ? storedTargetPos : new Vec3(this.endPosX, this.endPosY, this.endPosZ);
      }
   }

   private Vec3 getPhotonBeamStart(float partialTicks) {
      return this.caster != null && this.caster.m_6084_() ? this.caster.beamMouthPos(partialTicks) : null;
   }

   private Vec3 getPhotonBeamEnd(float partialTicks) {
      Vec3 start = this.getPhotonBeamStart(partialTicks);
      Vec3 fullEnd = new Vec3(this.endPosX, this.endPosY, this.endPosZ);
      if (start != null) {
         BlockHitResult hit = this.m_9236_().m_45547_(new ClipContext(start, fullEnd, Block.COLLIDER, Fluid.NONE, this));
         if (hit.m_6662_() == Type.BLOCK) {
            return hit.m_82450_();
         }
      }

      return fullEnd;
   }

   private boolean isPhotonBeamAlive() {
      return this.on && this.m_6084_() && !this.m_213877_() && this.caster != null && this.caster.m_6084_() && this.isRenderable();
   }

   public void m_7334_(@NotNull Entity entityIn) {
   }

   public boolean m_6087_() {
      return false;
   }

   public boolean m_6094_() {
      return false;
   }

   public boolean m_6783_(double distance) {
      return true;
   }

   private static float wrapRad(float a) {
      float twoPi = (float) (Math.PI * 2);
      a %= twoPi;
      if ((double)a >= Math.PI) {
         a -= twoPi;
      }

      if ((double)a < -Math.PI) {
         a += twoPi;
      }

      return a;
   }

   private static float lerpAngleRad(float a, float b) {
      float diff = wrapRad(b - a);
      return a + diff * 0.85F;
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevCollidePosX = this.collidePosX;
      this.prevCollidePosY = this.collidePosY;
      this.prevCollidePosZ = this.collidePosZ;
      this.f_19854_ = this.m_20185_();
      this.f_19855_ = this.m_20186_();
      this.f_19856_ = this.m_20189_();
      this.prevYaw = this.renderYaw;
      this.prevPitch = this.renderPitch;
      if (this.m_9236_().f_46443_) {
         this.renderYaw = this.getYaw();
         this.renderPitch = this.getPitch();
      }

      if (this.f_19797_ == 1 && this.m_9236_().f_46443_) {
         this.caster = (HerobrineDragonEntity)this.m_9236_().m_6815_(this.getCasterID());
         this.target = (LivingEntity)this.m_9236_().m_6815_(this.getTargetID());
      }

      if (this.m_9236_().f_46443_
         && this.target == null
         && this.getTargetID() != -1
         && this.m_9236_().m_6815_(this.getTargetID()) instanceof LivingEntity living) {
         this.target = living;
      }

      if (this.caster != null) {
         Vec3 mouth = this.caster.beamMouthPos(1.0F);
         this.m_6034_(mouth.f_82479_, mouth.f_82480_, mouth.f_82481_);
      }

      if (this.m_9236_() instanceof ServerLevel serverLevel && this.f_19797_ >= 50) {
         Vec3 center = this.blockSide != null
            ? new Vec3(this.collidePosX, this.collidePosY, this.collidePosZ)
            : (this.getStoredTargetPos() != null ? this.getStoredTargetPos() : this.m_20182_());
         ScreenShakeUtil.applyScreenShake(serverLevel, center, 24.0, 4, 6);
         if (this.caster != null && this.caster.m_20197_().contains(this.caster.getSummoner()) && this.caster.getSummoner() instanceof Player) {
            center = new Vec3(this.caster.getSummoner().m_20185_(), this.caster.getSummoner().m_20186_(), this.caster.getSummoner().m_20189_());
            ScreenShakeUtil.applyScreenShake(serverLevel, center, 24.0, 4, 6);
         }
      }

      if (this.target != null && this.target.m_6084_()) {
         Vec3 from = new Vec3(this.m_20185_(), this.m_20186_(), this.m_20189_());
         Vec3 to = this.target.m_20299_(1.0F);
         this.setTargetPos(to);
         float targetYaw = yawTowards(from, to);
         float targetPitch = pitchTowards(from, to);
         float interpolatedYaw = lerpAngleRad(this.getYaw(), targetYaw);
         float interpolatedPitch = Mth.m_14179_(0.85F, this.getPitch(), targetPitch);
         this.setYaw(interpolatedYaw);
         this.setPitch(interpolatedPitch);
         if (this.m_9236_().f_46443_) {
            this.renderYaw = interpolatedYaw;
            this.renderPitch = interpolatedPitch;
         }
      } else {
         Vec3 from = new Vec3(this.m_20185_(), this.m_20186_(), this.m_20189_());
         Vec3 to = this.getStoredTargetPos();
         if (to == null) {
            to = new Vec3(this.endPosX, this.endPosY, this.endPosZ);
            this.setTargetPos(to);
         }

         float targetYaw = yawTowards(from, to);
         float targetPitch = pitchTowards(from, to);
         float interpolatedYaw = Mth.m_14179_(0.5F, this.getYaw(), targetYaw);
         float interpolatedPitch = Mth.m_14179_(0.5F, this.getPitch(), targetPitch);
         this.setYaw(interpolatedYaw);
         this.setPitch(interpolatedPitch);
         if (this.m_9236_().f_46443_) {
            this.renderYaw = interpolatedYaw;
            this.renderPitch = interpolatedPitch;
         }
      }

      if (this.on && (this.caster == null || this.caster.m_6084_())) {
         if (this.m_9236_().f_46443_ && this.f_19797_ <= 10 && this.caster != null) {
            int particleCount = 8;

            while (--particleCount != 0) {
            }
         }

         this.calculateEndPos();
         if (this.m_9236_().f_46443_ && this.isRenderable()) {
            ClientVfxRouter.run(
               AnnoyingVillagersClientConfig.VfxEffect.DRAGON_BEAM,
               () -> {
                  if (!PhotonClientFxUtil.isLoaded()) {
                     return false;
                  } else if (this.f_19797_ >= 3 && this.caster != null) {
                     boolean handled = PhotonClientFxUtil.followBeam(
                        "dragon_beam:" + this.m_19879_(),
                        this.m_9236_(),
                        "dragonbeam",
                        this,
                        this::getPhotonBeamStart,
                        this::getPhotonBeamEnd,
                        this::isPhotonBeamAlive,
                        PhotonClientFxUtil.BeamForwardAxis.POSITIVE_X,
                        192.0F,
                        this.getDuration() + 5
                     );
                     if (handled) {
                        this.renderBeam = true;
                     }

                     return handled;
                  } else {
                     return true;
                  }
               },
               () -> {
                  if (this.renderBeam) {
                     return true;
                  } else if (this.f_19797_ >= 3 && this.caster != null && this.target != null) {
                     this.renderBeam = true;
                     AAAParticlesUtil.sendDragonBeam(this.caster.beamMouthPos(1.0F), this.target.m_20299_(1.0F), this.m_9236_(), this.caster, this.target);
                     return true;
                  } else {
                     return true;
                  }
               },
               () -> {
                  if (!this.renderBeam && this.caster != null) {
                     if (this.f_19797_ >= 3) {
                        Vec3 mouthPos = this.caster.beamMouthPos(1.0F);
                        this.m_9236_()
                           .m_6493_(
                              ParticleTypes.f_123799_,
                              true,
                              mouthPos.f_82479_ + new Random().nextDouble(-1.0, 1.0),
                              mouthPos.f_82480_ + new Random().nextDouble(-1.0, 1.0),
                              mouthPos.f_82481_ + new Random().nextDouble(-1.0, 1.0),
                              0.0,
                              0.0,
                              0.0
                           );
                     }

                     if (this.f_19797_ >= 50) {
                        this.renderBeam = true;
                        this.setUseNoVfxThunder(true);
                        this.setThunderStartStop(this.caster.beamMouthPos(1.0F), this.getBeamStopPos());
                     }
                  }
               }
            );
         }

         if (this.isRenderable() && this.f_19797_ >= 50) {
            if (!this.playSound) {
               this.playSound = true;
               this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.DRAGON_BREATH.get(), 5.0F, 1.0F);
            }

            List<LivingEntity> hit = this.raytraceEntities(
                  this.m_9236_(), new Vec3(this.m_20185_(), this.m_20186_(), this.m_20189_()), new Vec3(this.endPosX, this.endPosY, this.endPosZ), true
               )
               .entities;
            if (!this.m_9236_().f_46443_) {
               float damage = this.getDamage();

               for (LivingEntity target : hit) {
                  target.m_6469_(this.m_269291_().m_269104_(this, this.caster.getSummoner()), damage);
                  LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
                  EpicfightUtil.dealStaminaDamage(this.m_269291_().m_269104_(this, this.caster.getSummoner()), 0.1F, livingEntityPatch, false);
                  target.f_19864_ = true;
                  target.m_20334_(0.0, 0.0, 0.0);
                  target.m_6001_(0.0, 0.0, 0.0);
               }
            }
         }

         if (this.f_19797_ > this.getDuration()) {
            this.on = false;
         }
      } else {
         this.m_146870_();
      }
   }

   public static class DragonBeamHitResult {
      private BlockHitResult blockHit;
      private final List<LivingEntity> entities = new ArrayList<>();

      public BlockHitResult getBlockHit() {
         return this.blockHit;
      }

      public void setBlockHit(HitResult rayTraceResult) {
         if (rayTraceResult.m_6662_() == Type.BLOCK) {
            this.blockHit = (BlockHitResult)rayTraceResult;
         }
      }

      public void addEntityHit(LivingEntity entity) {
         this.entities.add(entity);
      }
   }
}
