package com.dmc.invincible_dmc.entity.summonedsword;

import com.dmc.invincible_dmc.api.animation.types.customStun.ICustomStunDamageSource;
import com.dmc.invincible_dmc.damagesource.DMCDamageTypeTags;
import com.dmc.invincible_dmc.entity.DMCEntities;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.entity.judgementcut.JudgementCutEntity;
import com.dmc.invincible_dmc.entity.summonedsword.motion.SummonedSwordMotionController;
import com.dmc.invincible_dmc.entity.summonedsword.motion.SummonedSwordMotionMode;
import com.dmc.invincible_dmc.gameassets.animations.stun.CustomStunAnimations;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.network.DMCNetwork;
import com.dmc.invincible_dmc.network.client.CPDiscardSpineBlade;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.dmc.invincible_dmc.utils.DMCLog;
import com.dmc.invincible_dmc.utils.DamageFilterUtils;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;
import yesman.epicfight.world.damagesource.StunType;

public class SpineBladeEntity extends Entity implements SummonedSwordMotionController {
   private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.m_135353_(SpineBladeEntity.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Long> SPIN_START_GAME_TIME = SynchedEntityData.m_135353_(SpineBladeEntity.class, EntityDataSerializers.f_244073_);
   private static final int LIFETIME_TICKS = 60;
   private static final float SPIN_SPEED = 45.0F;
   private static final int DAMAGE_INTERVAL = 1;
   private static final float BLADE_RADIUS = 2.1F;
   private static final float ORBIT_RADIUS = 1.2F;
   private LivingEntity ownerRef;
   private UUID ownerUUID;
   private UUID childSwordUUID;
   private int tickCounter;
   private boolean detonated;
   private boolean clientObservedSpineAnimation;
   private boolean clientCleanupRequested;

   public SpineBladeEntity(EntityType<?> type, Level level) {
      super(type, level);
      this.f_19811_ = true;
      this.f_19794_ = true;
   }

   public SpineBladeEntity(Level level, LivingEntity owner) {
      this((EntityType<?>)DMCEntities.SPINE_BLADE.get(), level);
      this.setOwner(owner);
      this.f_19794_ = true;
      this.f_19811_ = true;
      this.f_19804_.m_135381_(SPIN_START_GAME_TIME, level.m_46467_());
      Vec3 jointPos = getHandLJointWorldPos(owner);
      this.m_146884_(jointPos);
   }

   public static void summon(LivingEntity owner) {
      SummonedSwordSpawner.spine(owner);
   }

   public static boolean discardOwnedBy(@Nullable Entity owner, UUID controllerUUID) {
      if (owner != null && controllerUUID != null && owner.m_9236_() instanceof ServerLevel serverLevel) {
         Entity entity = serverLevel.m_8791_(controllerUUID);
         if (entity instanceof SpineBladeEntity blade && blade.isOwnedBy(owner)) {
            DMCLog.info(
               DMCLog.Category.SWORD,
               "[Spine] CLIENT_EXIT_ACCEPT owner={} ownerUuid={} controllerId={} controllerUuid={}",
               owner.m_7755_().getString(),
               owner.m_20148_(),
               blade.m_19879_(),
               controllerUUID
            );
            blade.cleanup();
            return true;
         }

         DMCLog.info(
            DMCLog.Category.SWORD,
            "[Spine] CLIENT_EXIT_REJECT owner={} ownerUuid={} controllerUuid={} resolved={}",
            owner.m_7755_().getString(),
            owner.m_20148_(),
            controllerUUID,
            entity
         );
         return false;
      } else {
         return false;
      }
   }

   private boolean isOwnedBy(Entity owner) {
      return owner.m_20148_().equals(this.ownerUUID) || (Integer)this.f_19804_.m_135370_(OWNER_ID) == owner.m_19879_() || this.ownerRef == owner;
   }

   public void detonate() {
      if (!this.detonated) {
         this.detonated = true;
         this.dealFinishDamage();
         this.cleanup();
         this.m_9236_().m_6263_(null, this.m_20185_(), this.m_20186_(), this.m_20189_(), SoundEvents.f_11983_, SoundSource.HOSTILE, 1.5F, 0.5F);
      }
   }

   public void m_8119_() {
      super.m_8119_();
      LivingEntity owner = this.getOwner();
      if (owner == null) {
         this.m_146870_();
      } else {
         this.m_146884_(getHandLJointWorldPos(owner));
         if (this.m_9236_().f_46443_) {
            this.detectClientAnimationExit(owner);
         } else {
            if (owner instanceof Player player && VergilSkill.NotHoldingYamato(player)) {
               this.cleanup();
               return;
            }

            if (!this.isOwnerInSpineBladeAnim(owner)) {
               this.cleanup();
            } else if (owner.m_21224_()) {
               this.cleanup();
            } else {
               this.rebindChildSwordIfNeeded();
               this.tickCounter++;
               if (!this.detonated) {
                  this.dealSpinDamage();
               }

               if (this.tickCounter >= 60 && !this.detonated) {
                  this.detonate();
               }
            }
         }
      }
   }

   public void m_142687_(@NotNull RemovalReason reason) {
      super.m_142687_(reason);
      if (reason.m_146965_() && this.childSwordUUID != null && this.m_9236_() instanceof ServerLevel sl) {
         Entity sword = sl.m_8791_(this.childSwordUUID);
         if (sword != null) {
            sword.m_146870_();
         }
      }
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(OWNER_ID, -1);
      this.f_19804_.m_135372_(SPIN_START_GAME_TIME, 0L);
   }

   void spawnChildSword() {
      LivingEntity owner = this.getOwner();
      if (owner != null && this.m_9236_() instanceof ServerLevel) {
         DMCSummonedSwordEntity sword = SummonedSwordSpawner.createSword(this.m_9236_(), owner, 0.75F, true);
         if (sword != null) {
            sword.setLifetimeTicks(Integer.MAX_VALUE);
            sword.setSpine(true);
            sword.setNoAim(true);
            this.bindSwordMotion(sword, 0);
            this.m_9236_().m_7967_(sword);
            this.childSwordUUID = sword.m_20148_();
         }
      }
   }

   private void rebindChildSwordIfNeeded() {
      if (this.childSwordUUID != null && this.m_9236_() instanceof ServerLevel serverLevel) {
         if (serverLevel.m_8791_(this.childSwordUUID) instanceof DMCSummonedSwordEntity sword && sword.isInStandby() && !sword.isManagedBy(this)) {
            this.bindSwordMotion(sword, 0);
         }
      }
   }

   private void dealSpinDamage() {
      LivingEntity owner = this.getOwner();
      if (owner != null) {
         LivingEntityPatch<?> ownerPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(owner, LivingEntityPatch.class);
         if (ownerPatch != null) {
            Vec3 pos = getHandLJointWorldPos(owner);
            AABB box = new AABB(pos.f_82479_ - 2.1F, pos.f_82480_ - 0.8F, pos.f_82481_ - 2.1F, pos.f_82479_ + 2.1F, pos.f_82480_ + 0.8F, pos.f_82481_ + 2.1F);

            for (LivingEntity target : this.m_9236_()
               .m_6443_(
                  LivingEntity.class,
                  box,
                  e -> e.m_6084_()
                        && e != owner
                        && !(e instanceof DoppelgangerEntity)
                        && !(e instanceof DMCSummonedSwordEntity)
                        && !(e instanceof JudgementCutEntity)
               )) {
               EpicFightDamageSource ds = EpicFightDamageSources.mobAttack(owner)
                  .addRuntimeTag(DMCSummonedSwordPatch.SUMMONED_SWORD_DAMAGE)
                  .addRuntimeTag(DMCSummonedSwordPatch.SPINE_SUMMONED_SWORD_DAMAGE)
                  .addRuntimeTag(DMCDamageTypeTags.NOT_CHARGE)
                  .setBaseImpact(1.0F)
                  .setStunType(StunType.HOLD);
               ((ICustomStunDamageSource)ds)
                  .invincible$setCustomStunAnimations(
                     Animations.BIPED_HIT_SHORT, Animations.BIPED_HIT_SHORT, Animations.BIPED_HIT_SHORT, Animations.BIPED_HIT_SHORT
                  );
               if (!DamageFilterUtils.shouldSkipTarget(owner, target)) {
                  int prevInvul = target.f_19802_;
                  target.f_19802_ = 0;
                  target.m_6469_(ds, 0.1F);
                  target.f_19802_ = prevInvul;
               }
            }
         }
      }
   }

   private void dealFinishDamage() {
      LivingEntity owner = this.getOwner();
      if (owner != null) {
         LivingEntityPatch<?> ownerPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(owner, LivingEntityPatch.class);
         if (ownerPatch != null) {
            Vec3 pos = getHandLJointWorldPos(owner);
            AABB box = new AABB(pos.f_82479_ - 2.1F, pos.f_82480_ - 0.8F, pos.f_82481_ - 2.1F, pos.f_82479_ + 2.1F, pos.f_82480_ + 0.8F, pos.f_82481_ + 2.1F);

            for (LivingEntity target : this.m_9236_()
               .m_6443_(
                  LivingEntity.class,
                  box,
                  e -> e.m_6084_()
                        && e != owner
                        && !(e instanceof DoppelgangerEntity)
                        && !(e instanceof DMCSummonedSwordEntity)
                        && !(e instanceof JudgementCutEntity)
               )) {
               EpicFightDamageSource ds = EpicFightDamageSources.mobAttack(owner)
                  .addRuntimeTag(DMCSummonedSwordPatch.SUMMONED_SWORD_DAMAGE)
                  .addRuntimeTag(DMCDamageTypeTags.NOT_CHARGE);
               ((ICustomStunDamageSource)ds)
                  .invincible$setCustomStunAnimations(
                     CustomStunAnimations.HIT_UP_4, CustomStunAnimations.HIT_UP_4, CustomStunAnimations.HIT_UP_4, CustomStunAnimations.HIT_UP_4
                  );
               if (!DamageFilterUtils.shouldSkipTarget(owner, target)) {
                  int prevInvul = target.f_19802_;
                  target.f_19802_ = 0;
                  target.m_6469_(ds, 1.0F);
                  target.f_19802_ = prevInvul;
               }
            }
         }
      }
   }

   public void setOwner(LivingEntity owner) {
      this.ownerRef = owner;
      this.ownerUUID = owner.m_20148_();
      this.f_19804_.m_135381_(OWNER_ID, owner.m_19879_());
   }

   @Nullable
   public LivingEntity getOwner() {
      if (this.ownerRef != null && this.ownerRef.m_6084_()) {
         return this.ownerRef;
      } else {
         if (!this.m_9236_().f_46443_ && this.ownerUUID != null) {
            if (((ServerLevel)this.m_9236_()).m_8791_(this.ownerUUID) instanceof LivingEntity le) {
               this.ownerRef = le;
               return le;
            }
         } else if (this.m_9236_().f_46443_) {
            int id = (Integer)this.f_19804_.m_135370_(OWNER_ID);
            if (id != -1 && this.m_9236_().m_6815_(id) instanceof LivingEntity le) {
               this.ownerRef = le;
               return le;
            }
         }

         return null;
      }
   }

   private boolean isOwnerInSpineBladeAnim(LivingEntity owner) {
      LivingEntityPatch<?> patch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(owner, LivingEntityPatch.class);
      if (patch == null) {
         return false;
      } else if (!patch.getEntityState().inaction()) {
         return false;
      } else {
         AnimationPlayer animPlayer = DMCAnimationUtils.getMainPlayer(patch);
         if (animPlayer == null) {
            return false;
         } else {
            AssetAccessor<? extends StaticAnimation> realAnimAccessor = DMCAnimationUtils.getRealAnimationAccessor(animPlayer);
            return realAnimAccessor != null && realAnimAccessor.equals(YamatoAnimations.YAMATO_PROVOCATION_SPINE_BLADE);
         }
      }
   }

   private void detectClientAnimationExit(LivingEntity owner) {
      if (!(owner instanceof Player player) || !player.m_7578_() || this.clientCleanupRequested) {
         return;
      }

      if (this.isOwnerInSpineBladeAnim(owner)) {
         this.clientObservedSpineAnimation = true;
      } else if (this.clientObservedSpineAnimation) {
         this.clientCleanupRequested = true;
         LivingEntityPatch<?> patch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(owner, LivingEntityPatch.class);
         DMCLog.info(
            DMCLog.Category.SWORD,
            "[Spine] CLIENT_ANIMATION_EXIT owner={} controllerId={} controllerUuid={} currentAnim={} realAnim={}",
            owner.m_7755_().getString(),
            this.m_19879_(),
            this.m_20148_(),
            DMCAnimationUtils.getCurrentAnimationName(patch),
            DMCAnimationUtils.getRealAnimationName(patch)
         );
         DMCNetwork.sendToServer(new CPDiscardSpineBlade(this.m_20148_()));
      }
   }

   @Override
   public SummonedSwordMotionMode getMotionMode() {
      return SummonedSwordMotionMode.SPINE_ATTACHED;
   }

   public long getMotionTick(boolean previous) {
      return Math.max(0L, this.getSpinTick() - (previous ? 1L : 0L));
   }

   public float getMotionSpinSpeed() {
      return 45.0F;
   }

   private long getSpinTick() {
      return Math.max(0L, this.m_9236_().m_46467_() - (Long)this.f_19804_.m_135370_(SPIN_START_GAME_TIME));
   }

   public static Vec3 getHandLJointWorldPos(LivingEntity owner) {
      return getHandLJointWorldPos(owner, 1.0F);
   }

   public static Vec3 getHandLJointWorldPos(LivingEntity owner, float partialTick) {
      LivingEntityPatch<?> patch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(owner, LivingEntityPatch.class);
      Vec3 fallback = owner.m_20318_(partialTick).m_82520_(0.0, (double)owner.m_20192_(), 0.0);
      if (patch != null && patch.getArmature() != null) {
         Joint handL = patch.getArmature().searchJointByName("Hand_R");
         if (handL == null) {
            return fallback;
         } else {
            OpenMatrix4f transform;
            double var10000;
            label23: {
               transform = DMCAnimationUtils.getJointWorldTransform(patch, handL, partialTick);
               if (owner instanceof Player player && SinDevilTriggerManager.isPlayerInSDT(player)) {
                  var10000 = 0.8;
                  break label23;
               }

               var10000 = 0.5;
            }

            double verticalOffset = var10000;
            return transform == null ? fallback : OpenMatrix4f.transform(transform, new Vec3(0.0, verticalOffset, 0.0));
         }
      } else {
         return fallback;
      }
   }

   private void cleanup() {
      if (this.childSwordUUID != null && this.m_9236_() instanceof ServerLevel sl) {
         Entity sword = sl.m_8791_(this.childSwordUUID);
         if (sword != null) {
            sword.m_146870_();
         }
      }

      this.m_146870_();
   }

   public boolean m_6094_() {
      return false;
   }

   public boolean m_6087_() {
      return false;
   }

   public boolean m_20068_() {
      return true;
   }

   public boolean m_5829_() {
      return false;
   }

   public boolean m_6469_(@NotNull DamageSource source, float amount) {
      return false;
   }

   protected void m_7378_(CompoundTag tag) {
      if (tag.m_128403_("Owner")) {
         this.ownerUUID = tag.m_128342_("Owner");
      }

      if (tag.m_128403_("Sword")) {
         this.childSwordUUID = tag.m_128342_("Sword");
      }

      this.tickCounter = tag.m_128451_("Tick");
      this.detonated = tag.m_128471_("Detonated");
      if (tag.m_128441_("SpinStartGameTime")) {
         this.f_19804_.m_135381_(SPIN_START_GAME_TIME, tag.m_128454_("SpinStartGameTime"));
      } else {
         this.f_19804_.m_135381_(SPIN_START_GAME_TIME, this.m_9236_().m_46467_() - (long)this.tickCounter);
      }
   }

   protected void m_7380_(CompoundTag tag) {
      if (this.ownerUUID != null) {
         tag.m_128362_("Owner", this.ownerUUID);
      }

      if (this.childSwordUUID != null) {
         tag.m_128362_("Sword", this.childSwordUUID);
      }

      tag.m_128405_("Tick", this.tickCounter);
      tag.m_128379_("Detonated", this.detonated);
      tag.m_128356_("SpinStartGameTime", (Long)this.f_19804_.m_135370_(SPIN_START_GAME_TIME));
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }
}
