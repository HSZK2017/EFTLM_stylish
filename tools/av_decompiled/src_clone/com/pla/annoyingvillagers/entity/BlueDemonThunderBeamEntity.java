package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.client.engine.ClientVfxRouter;
import com.pla.annoyingvillagers.client.engine.PhotonClientFxUtil;
import com.pla.annoyingvillagers.config.AnnoyingVillagersClientConfig;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModMobEffects;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.util.AAAParticlesUtil;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;

public class BlueDemonThunderBeamEntity extends Entity {
   public LivingEntity caster;
   public double collidePosX;
   public double collidePosY;
   public double collidePosZ;
   public double prevCollidePosX;
   public double prevCollidePosY;
   public double prevCollidePosZ;
   public boolean on = true;
   public Direction blockSide;
   private int power;
   private static final EntityDataAccessor<Integer> DURATION = SynchedEntityData.m_135353_(BlueDemonThunderBeamEntity.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> CASTER = SynchedEntityData.m_135353_(BlueDemonThunderBeamEntity.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Float> START_X = SynchedEntityData.m_135353_(BlueDemonThunderBeamEntity.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Float> START_Y = SynchedEntityData.m_135353_(BlueDemonThunderBeamEntity.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Float> START_Z = SynchedEntityData.m_135353_(BlueDemonThunderBeamEntity.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Float> END_X = SynchedEntityData.m_135353_(BlueDemonThunderBeamEntity.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Float> END_Y = SynchedEntityData.m_135353_(BlueDemonThunderBeamEntity.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Float> END_Z = SynchedEntityData.m_135353_(BlueDemonThunderBeamEntity.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Float> BEAM_LEN = SynchedEntityData.m_135353_(BlueDemonThunderBeamEntity.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Float> LAST_DIR_X = SynchedEntityData.m_135353_(BlueDemonThunderBeamEntity.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Float> LAST_DIR_Z = SynchedEntityData.m_135353_(BlueDemonThunderBeamEntity.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Boolean> USE_NO_VFX_THUNDER = SynchedEntityData.m_135353_(
      BlueDemonThunderBeamEntity.class, EntityDataSerializers.f_135035_
   );
   @OnlyIn(Dist.CLIENT)
   private Vec3[] attractorPos;
   private boolean renderBeam = false;
   private boolean playSound = false;

   public BlueDemonThunderBeamEntity(EntityType<? extends BlueDemonThunderBeamEntity> type, Level level) {
      super(type, level);
      this.f_19811_ = true;
      if (level.f_46443_) {
         this.attractorPos = new Vec3[]{Vec3.f_82478_};
      }
   }

   public BlueDemonThunderBeamEntity(
      EntityType<? extends BlueDemonThunderBeamEntity> type, Level level, LivingEntity caster, int duration, int power, double beamLength
   ) {
      this(type, level);
      this.caster = caster;
      this.setDuration(duration);
      this.setPower(power);
      this.setBeamLength((float)beamLength);
      this.f_19804_.m_135381_(LAST_DIR_X, 1.0F);
      this.f_19804_.m_135381_(LAST_DIR_Z, 0.0F);
      if (!level.f_46443_ && caster != null) {
         this.setCasterID(caster.m_19879_());
      }
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(DURATION, 0);
      this.f_19804_.m_135372_(CASTER, -1);
      this.f_19804_.m_135372_(START_X, 0.0F);
      this.f_19804_.m_135372_(START_Y, 0.0F);
      this.f_19804_.m_135372_(START_Z, 0.0F);
      this.f_19804_.m_135372_(END_X, 0.0F);
      this.f_19804_.m_135372_(END_Y, 0.0F);
      this.f_19804_.m_135372_(END_Z, 0.0F);
      this.f_19804_.m_135372_(BEAM_LEN, 7.5F);
      this.f_19804_.m_135372_(LAST_DIR_X, 1.0F);
      this.f_19804_.m_135372_(LAST_DIR_Z, 0.0F);
      this.f_19804_.m_135372_(USE_NO_VFX_THUNDER, false);
   }

   protected void m_7378_(@NotNull CompoundTag tag) {
   }

   protected void m_7380_(@NotNull CompoundTag tag) {
   }

   public void setUseNoVfxThunder(boolean noVfxThunder) {
      this.f_19804_.m_135381_(USE_NO_VFX_THUNDER, noVfxThunder);
   }

   public boolean isSetUseNoVfxThunder() {
      return (Boolean)this.f_19804_.m_135370_(USE_NO_VFX_THUNDER);
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   @NotNull
   public PushReaction m_7752_() {
      return PushReaction.IGNORE;
   }

   public int getDuration() {
      return (Integer)this.f_19804_.m_135370_(DURATION);
   }

   public void setDuration(int duration) {
      this.f_19804_.m_135381_(DURATION, duration);
   }

   public int getCasterID() {
      return (Integer)this.f_19804_.m_135370_(CASTER);
   }

   public void setCasterID(int id) {
      this.f_19804_.m_135381_(CASTER, id);
   }

   public void setPower(int power) {
      this.power = power;
   }

   public float getBeamLength() {
      return (Float)this.f_19804_.m_135370_(BEAM_LEN);
   }

   public void setBeamLength(float len) {
      this.f_19804_.m_135381_(BEAM_LEN, len);
   }

   public Vec3 getStartPos() {
      return new Vec3(
         (double)((Float)this.f_19804_.m_135370_(START_X)).floatValue(),
         (double)((Float)this.f_19804_.m_135370_(START_Y)).floatValue(),
         (double)((Float)this.f_19804_.m_135370_(START_Z)).floatValue()
      );
   }

   public Vec3 getEndPos() {
      return new Vec3(
         (double)((Float)this.f_19804_.m_135370_(END_X)).floatValue(),
         (double)((Float)this.f_19804_.m_135370_(END_Y)).floatValue(),
         (double)((Float)this.f_19804_.m_135370_(END_Z)).floatValue()
      );
   }

   private void setStartPos(Vec3 pos) {
      this.f_19804_.m_135381_(START_X, (float)pos.f_82479_);
      this.f_19804_.m_135381_(START_Y, (float)pos.f_82480_);
      this.f_19804_.m_135381_(START_Z, (float)pos.f_82481_);
   }

   private void setEndPos(Vec3 pos) {
      this.f_19804_.m_135381_(END_X, (float)pos.f_82479_);
      this.f_19804_.m_135381_(END_Y, (float)pos.f_82480_);
      this.f_19804_.m_135381_(END_Z, (float)pos.f_82481_);
   }

   private Vec3 getLastDirXZ() {
      Vec3 d = new Vec3(
         (double)((Float)this.f_19804_.m_135370_(LAST_DIR_X)).floatValue(), 0.0, (double)((Float)this.f_19804_.m_135370_(LAST_DIR_Z)).floatValue()
      );
      return d.m_82556_() < 1.0E-8 ? new Vec3(1.0, 0.0, 0.0) : d.m_82541_();
   }

   private void setLastDirXZ(Vec3 dir) {
      this.f_19804_.m_135381_(LAST_DIR_X, (float)dir.f_82479_);
      this.f_19804_.m_135381_(LAST_DIR_Z, (float)dir.f_82481_);
   }

   private boolean isPhotonBeamAlive() {
      return this.on && this.m_6084_() && !this.m_213877_() && this.caster != null && this.caster.m_6084_();
   }

   public void initSpawnState() {
      if (this.caster != null) {
         Vec3 fallbackStart = this.caster.m_20182_().m_82520_(0.0, (double)this.caster.m_20192_() * 0.8, 0.0);
         Vec3 fallbackEnd = fallbackStart.m_82549_(this.caster.m_20154_().m_82490_((double)this.getBeamLength()));
         this.setStartPos(fallbackStart);
         this.setEndPos(fallbackEnd);
         this.m_7678_(fallbackStart.f_82479_, fallbackStart.f_82480_, fallbackStart.f_82481_, this.caster.m_146908_(), this.caster.m_146909_());
      }
   }

   private void updateBeamFromHands() {
      if (this.caster != null) {
         Vec3 handLeft = EpicfightUtil.getJointWithTranslation(
            this.caster, new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).handL, 0.0F, 0.0
         );
         Vec3 handRight = EpicfightUtil.getJointWithTranslation(
            this.caster, new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).handR, 0.0F, 0.0
         );
         if (handLeft != null && handRight != null) {
            Vec3 horizontal = handRight.m_82546_(handLeft);
            horizontal = new Vec3(horizontal.f_82479_, 0.0, horizontal.f_82481_);
            if (horizontal.m_82556_() < 1.0E-6) {
               horizontal = this.getLastDirXZ();
            } else {
               horizontal = horizontal.m_82541_();
               this.setLastDirXZ(horizontal);
            }

            double lookY = this.caster.m_20154_().f_82480_;
            Vec3 dir = new Vec3(horizontal.f_82479_, lookY, horizontal.f_82481_).m_82541_();
            double len = (double)this.getBeamLength();
            Vec3 end = handRight.m_82549_(dir.m_82490_(len));
            this.setStartPos(handRight);
            this.setEndPos(end);
            this.m_6034_(handRight.f_82479_, handRight.f_82480_, handRight.f_82481_);
         }
      }
   }

   public BlueDemonThunderBeamEntity.BlueDemonThunderBeamHitResult raytraceEntities(Level world, Vec3 from, Vec3 to) {
      BlueDemonThunderBeamEntity.BlueDemonThunderBeamHitResult result = new BlueDemonThunderBeamEntity.BlueDemonThunderBeamHitResult();
      BlockHitResult blockHit = world.m_45547_(new ClipContext(from, to, Block.COLLIDER, Fluid.NONE, this));
      result.setBlockHit(blockHit);
      Vec3 actualTo;
      if (result.blockHit != null) {
         actualTo = result.blockHit.m_82450_();
         this.blockSide = result.blockHit.m_82434_();
      } else {
         actualTo = to;
         this.blockSide = null;
      }

      this.collidePosX = actualTo.f_82479_;
      this.collidePosY = actualTo.f_82480_;
      this.collidePosZ = actualTo.f_82481_;
      AABB beamBox = new AABB(from, actualTo).m_82400_(0.5);

      for (LivingEntity entity : world.m_45976_(LivingEntity.class, beamBox)) {
         if (entity != this.caster) {
            if (entity instanceof Player) {
               Player player = (Player)entity;
               LivingEntity hit = this.caster;
               if (hit instanceof Player) {
                  Player casterPlayer = (Player)hit;
                  if (player.m_20148_().equals(casterPlayer.m_20148_())) {
                     continue;
                  }
               }
            }

            float pad = entity.m_6143_() + 0.25F;
            AABB aabb = entity.m_20191_().m_82400_((double)pad);
            Optional<Vec3> hit = aabb.m_82371_(from, actualTo);
            if (aabb.m_82390_(from) || hit.isPresent()) {
               result.addEntityHit(entity);
            }
         }
      }

      return result;
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevCollidePosX = this.collidePosX;
      this.prevCollidePosY = this.collidePosY;
      this.prevCollidePosZ = this.collidePosZ;
      if (this.f_19797_ == 1 && this.m_9236_().f_46443_ && this.m_9236_().m_6815_(this.getCasterID()) instanceof LivingEntity living) {
         this.caster = living;
      }

      if (this.caster == null && this.getCasterID() != -1 && this.m_9236_().m_6815_(this.getCasterID()) instanceof LivingEntity living) {
         this.caster = living;
      }

      if (this.on && (this.caster == null || this.caster.m_6084_())) {
         this.updateBeamFromHands();
         Vec3 start = this.getStartPos();
         Vec3 end = this.getEndPos();
         if (this.m_9236_().f_46443_ && this.f_19797_ >= 2) {
            ClientVfxRouter.run(
               AnnoyingVillagersClientConfig.VfxEffect.BLUE_DEMON_THUNDER_BEAM,
               () -> {
                  if (!PhotonClientFxUtil.isLoaded()) {
                     return false;
                  } else if (this.caster == null) {
                     return true;
                  } else {
                     boolean handled = PhotonClientFxUtil.followBeam(
                        "blue_demon_thunder_beam:" + this.m_19879_(),
                        this.m_9236_(),
                        "bluedemonbeam",
                        this,
                        partialTicks -> this.getStartPos(),
                        partialTicks -> this.getEndPos(),
                        this::isPhotonBeamAlive,
                        this.getDuration() + 5
                     );
                     if (handled) {
                        this.renderBeam = true;
                     }

                     return handled;
                  }
               },
               () -> {
                  if (this.renderBeam) {
                     return true;
                  } else {
                     this.renderBeam = true;
                     AAAParticlesUtil.sendBlueDemonThunderBeam(this.m_9236_(), this);
                     return true;
                  }
               },
               () -> {
                  if (!this.renderBeam) {
                     this.renderBeam = true;
                     this.setUseNoVfxThunder(true);
                  }
               }
            );
         }

         if (!this.playSound) {
            this.playSound = true;
            this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.ELECTRIC_SHOOT.get(), 1.0F, 1.0F);
         }

         List<LivingEntity> hit = this.raytraceEntities(this.m_9236_(), start, end).entities;
         if (this.m_9236_() instanceof ServerLevel) {
            for (LivingEntity target : hit) {
               if (this.caster != null) {
                  target.m_6469_(this.m_269291_().m_269104_(this, this.caster), (float)this.power);
               } else {
                  target.m_6469_(this.m_269291_().m_269425_(), (float)this.power);
               }

               target.f_19864_ = true;
               target.m_7292_(new MobEffectInstance((MobEffect)AnnoyingVillagersModMobEffects.ELECTRIFY.get(), 60, 1));
            }
         }

         if (this.f_19797_ > this.getDuration()) {
            this.on = false;
            this.m_146870_();
         }
      } else {
         this.m_146870_();
      }
   }

   public static class BlueDemonThunderBeamHitResult {
      private BlockHitResult blockHit;
      private final List<LivingEntity> entities = new ArrayList<>();

      public void setBlockHit(HitResult rayTraceResult) {
         if (rayTraceResult != null && rayTraceResult.m_6662_() == Type.BLOCK) {
            this.blockHit = (BlockHitResult)rayTraceResult;
         }
      }

      public void addEntityHit(LivingEntity entity) {
         this.entities.add(entity);
      }
   }
}
