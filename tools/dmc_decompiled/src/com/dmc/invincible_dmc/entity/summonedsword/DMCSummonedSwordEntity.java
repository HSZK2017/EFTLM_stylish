package com.dmc.invincible_dmc.entity.summonedsword;

import com.dmc.invincible_dmc.InvincibleMod_DMC;
import com.dmc.invincible_dmc.api.animation.types.customStun.ICustomStunDamageSource;
import com.dmc.invincible_dmc.client.model.DMCArmatures;
import com.dmc.invincible_dmc.damagesource.DMCDamageTypeTags;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.entity.summonedsword.motion.SummonedSwordMotionController;
import com.dmc.invincible_dmc.entity.summonedsword.motion.SummonedSwordTransform;
import com.dmc.invincible_dmc.gameassets.DMCEffects;
import com.dmc.invincible_dmc.gameassets.animations.stun.CustomStunAnimations;
import com.dmc.invincible_dmc.gameassets.animations.yamato.SummonedSwordAnimations;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.dmc.invincible_dmc.utils.DamageFilterUtils;
import com.dmc.invincible_dmc.utils.yamato.TargetTeleportUtils;
import com.merlin204.avalon.entity.IAvalonMeshEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.HitEntityList;
import yesman.epicfight.api.utils.HitEntityList.Priority;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.damagesource.ExtraDamageInstance.ExtraDamage;
import yesman.epicfight.world.entity.DodgeLocationIndicator;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class DMCSummonedSwordEntity extends Mob implements IAvalonMeshEntity {
   protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.m_135353_(
      DMCSummonedSwordEntity.class, EntityDataSerializers.f_135041_
   );
   protected static final EntityDataAccessor<Integer> DATA_OWNER_ID = SynchedEntityData.m_135353_(DMCSummonedSwordEntity.class, EntityDataSerializers.f_135028_);
   protected static final EntityDataAccessor<Float> SYNC_X_ROT = SynchedEntityData.m_135353_(DMCSummonedSwordEntity.class, EntityDataSerializers.f_135029_);
   protected static final EntityDataAccessor<Float> SCALE = SynchedEntityData.m_135353_(DMCSummonedSwordEntity.class, EntityDataSerializers.f_135029_);
   protected static final EntityDataAccessor<Boolean> PLAY_ANIMATION = SynchedEntityData.m_135353_(
      DMCSummonedSwordEntity.class, EntityDataSerializers.f_135035_
   );
   protected static final EntityDataAccessor<Boolean> SHOULD_RENDER = SynchedEntityData.m_135353_(DMCSummonedSwordEntity.class, EntityDataSerializers.f_135035_);
   protected static final EntityDataAccessor<Boolean> DATA_IS_TRICK = SynchedEntityData.m_135353_(DMCSummonedSwordEntity.class, EntityDataSerializers.f_135035_);
   protected static final EntityDataAccessor<Boolean> DATA_IS_IN_STANDBY = SynchedEntityData.m_135353_(
      DMCSummonedSwordEntity.class, EntityDataSerializers.f_135035_
   );
   protected static final EntityDataAccessor<Boolean> DATA_NO_AIM = SynchedEntityData.m_135353_(DMCSummonedSwordEntity.class, EntityDataSerializers.f_135035_);
   protected static final EntityDataAccessor<Boolean> DATA_HAS_HIT_TARGET = SynchedEntityData.m_135353_(
      DMCSummonedSwordEntity.class, EntityDataSerializers.f_135035_
   );
   protected static final EntityDataAccessor<Boolean> DATA_IS_STUCK = SynchedEntityData.m_135353_(DMCSummonedSwordEntity.class, EntityDataSerializers.f_135035_);
   protected static final EntityDataAccessor<Boolean> DATA_IS_BLAST = SynchedEntityData.m_135353_(DMCSummonedSwordEntity.class, EntityDataSerializers.f_135035_);
   protected static final EntityDataAccessor<Boolean> DATA_IS_STORM = SynchedEntityData.m_135353_(DMCSummonedSwordEntity.class, EntityDataSerializers.f_135035_);
   protected static final EntityDataAccessor<Boolean> DATA_IS_SPIRAL = SynchedEntityData.m_135353_(
      DMCSummonedSwordEntity.class, EntityDataSerializers.f_135035_
   );
   protected static final EntityDataAccessor<Integer> DATA_FORMATION_INDEX = SynchedEntityData.m_135353_(
      DMCSummonedSwordEntity.class, EntityDataSerializers.f_135028_
   );
   protected static final EntityDataAccessor<Integer> DATA_MOTION_CONTROLLER_ID = SynchedEntityData.m_135353_(
      DMCSummonedSwordEntity.class, EntityDataSerializers.f_135028_
   );
   protected static final EntityDataAccessor<Integer> DATA_MOTION_EPOCH = SynchedEntityData.m_135353_(
      DMCSummonedSwordEntity.class, EntityDataSerializers.f_135028_
   );
   protected static final EntityDataAccessor<Vector3f> DATA_MOTION_OFFSET = SynchedEntityData.m_135353_(
      DMCSummonedSwordEntity.class, EntityDataSerializers.f_268676_
   );
   protected static final EntityDataAccessor<Float> DATA_MOTION_YAW = SynchedEntityData.m_135353_(DMCSummonedSwordEntity.class, EntityDataSerializers.f_135029_);
   protected static final EntityDataAccessor<Float> DATA_MOTION_PITCH = SynchedEntityData.m_135353_(
      DMCSummonedSwordEntity.class, EntityDataSerializers.f_135029_
   );
   protected static final EntityDataAccessor<Boolean> DATA_IS_HEAVY_RAIN = SynchedEntityData.m_135353_(
      DMCSummonedSwordEntity.class, EntityDataSerializers.f_135035_
   );
   protected static final EntityDataAccessor<Boolean> DATA_IS_PROVOCATION = SynchedEntityData.m_135353_(
      DMCSummonedSwordEntity.class, EntityDataSerializers.f_135035_
   );
   protected static final EntityDataAccessor<Integer> DATA_SHOOT_SPEED = SynchedEntityData.m_135353_(
      DMCSummonedSwordEntity.class, EntityDataSerializers.f_135028_
   );
   protected static final EntityDataAccessor<Vector3f> DATA_TARGET_POS = SynchedEntityData.m_135353_(
      DMCSummonedSwordEntity.class, EntityDataSerializers.f_268676_
   );
   protected static final EntityDataAccessor<Boolean> DATA_IS_IMPALE = SynchedEntityData.m_135353_(
      DMCSummonedSwordEntity.class, EntityDataSerializers.f_135035_
   );
   protected static final EntityDataAccessor<Boolean> DATA_IS_SPINE = SynchedEntityData.m_135353_(DMCSummonedSwordEntity.class, EntityDataSerializers.f_135035_);
   protected static final EntityDataAccessor<Integer> DATA_IMPALE_TARGET_ID = SynchedEntityData.m_135353_(
      DMCSummonedSwordEntity.class, EntityDataSerializers.f_135028_
   );
   protected static final EntityDataAccessor<Integer> DATA_IMPALE_JOINT_ID = SynchedEntityData.m_135353_(
      DMCSummonedSwordEntity.class, EntityDataSerializers.f_135028_
   );
   protected static final EntityDataAccessor<Vector3f> DATA_IMPALE_LOCAL_POS = SynchedEntityData.m_135353_(
      DMCSummonedSwordEntity.class, EntityDataSerializers.f_268676_
   );
   protected static final EntityDataAccessor<Vector3f> DATA_IMPALE_LOCAL_DIR = SynchedEntityData.m_135353_(
      DMCSummonedSwordEntity.class, EntityDataSerializers.f_268676_
   );
   private static final MeshAccessor<SkinnedMesh> SUMMONED_SWORD_MESH = MeshAccessor.create(
      "invincible_dmc", "entity/effect/summoned_sword/summoned_sword", jsonModelLoader -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new)
   );
   private int lifetimeTicks = 20;
   private boolean isDiscard = false;
   private boolean initialVelocityApplied = false;
   private boolean ordinarySummonedSword = false;
   private Vec3 lockedTrajectory = Vec3.f_82478_;
   private int discardDelayTicks = 6;
   private int impaleDetonateTicks = 400;
   private static final double IMPALE_EMBED_DEPTH = 0.15;
   private static final float IMPALE_ATTACHED_SCALE = 1.35F;
   private static final double IMPALE_TORSO_MIN_HEIGHT = 0.36;
   private static final double IMPALE_TORSO_MAX_HEIGHT = 0.78;
   private static final double IMPALE_TORSO_HORIZONTAL_SPREAD = 0.28;
   private static final double IMPALE_FALLBACK_MAX_VERTICAL_SLOPE = 0.36;
   public static final ExtraDamage EXTRA_DAMAGE = new ExtraDamage(
      (attacker, itemstack, target, baseDamage, params) -> params[0], (itemstack, tooltips, baseDamage, params) -> {
      }
   );
   private static final float IMPALE_EXPLOSION_DAMAGE = 2.0F;
   private static final double IMPALE_EXPLOSION_RADIUS = 2.0;
   private static final int IMPALE_LIFETIME = 420;

   public DMCSummonedSwordEntity(EntityType<? extends Mob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.f_19811_ = true;
      this.m_20331_(true);
      this.f_19794_ = true;
      this.m_20242_(true);
   }

   public DMCSummonedSwordEntity(EntityType<? extends Mob> pEntityType, LivingEntity owner, float scale, Level pLevel, boolean standby) {
      super(pEntityType, pLevel);
      LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(owner, LivingEntityPatch.class);
      this.tame(owner);
      this.f_19811_ = true;
      this.f_19794_ = true;
      this.m_20331_(true);
      this.f_19804_.m_135381_(SCALE, scale);
      if (livingEntityPatch != null) {
         this.m_146922_(((LivingEntity)livingEntityPatch.getOriginal()).m_6080_());
      }

      this.m_146926_(owner.m_146909_());
      this.m_20242_(true);
      this.setInStandby(standby);
   }

   public static void summon(ServerPlayerPatch serverPlayerPatch, boolean isTricker) {
      SummonedSwordSpawner.summonNormal(serverPlayerPatch, isTricker);
   }

   public static AttributeSupplier getDefaultAttribute() {
      return Animal.m_21552_()
         .m_22268_(Attributes.f_22276_, 19.9F)
         .m_22268_(Attributes.f_22281_, 3.0)
         .m_22268_((Attribute)EpicFightAttributes.MAX_STRIKES.get(), 10.0)
         .m_22265_();
   }

   public void m_7023_(@NotNull Vec3 pTravelVector) {
      if (!this.isInStandby()) {
         super.m_7023_(pTravelVector);
      }
   }

   public void m_8119_() {
      Vec3 previousPosition = this.m_20182_();
      super.m_8119_();
      if (this.isImpale() && this.hasHitTarget()) {
         LivingEntity impaleTarget = this.getImpaleTarget();
         if (impaleTarget == null || !impaleTarget.m_6084_()) {
            if (!this.m_9236_().f_46443_) {
               this.m_146870_();
            }

            return;
         }

         this.updateImpaleAttachment(impaleTarget, 1.0F);
         this.m_20256_(Vec3.f_82478_);
         this.lockedTrajectory = Vec3.f_82478_;
         this.f_19794_ = true;
      }

      boolean managedMotionApplied = this.isInStandby() && this.applyManagedMotion();
      if (this.m_9236_().f_46443_) {
         if (!managedMotionApplied) {
            if (this.isHeavyRain()) {
               this.m_146926_(90.0F);
               this.f_19860_ = 90.0F;
               this.m_146922_(0.0F);
               this.f_19859_ = 0.0F;
               this.f_20885_ = 0.0F;
               this.f_20886_ = 0.0F;
               this.f_20883_ = 0.0F;
               this.f_20884_ = 0.0F;
            } else if (!this.isBlast()) {
               if (!this.isInStandby() && !this.isStuckInBlock() && !this.isImpale()) {
                  this.alignRotationToVelocity();
               } else if (this.isInStandby() && !this.isImpale()) {
                  Vector3f targetPos = (Vector3f)this.f_19804_.m_135370_(DATA_TARGET_POS);
                  if (targetPos.lengthSquared() > 0.001F) {
                     this.alignTo(new Vec3((double)targetPos.x(), (double)targetPos.y(), (double)targetPos.z()));
                  }
               }
            }
         }
      } else {
         if (this.lockedTrajectory.m_82556_() > 1.0E-7) {
            this.m_20256_(this.lockedTrajectory);
            this.m_20242_(true);
         }

         if (this.hasHitTarget()) {
            this.f_19794_ = true;
         }

         if (this.isInStandby()) {
            LivingEntity owner = this.getOwner();
            DMCSummonedSwordPatch<?> swordPatch = (DMCSummonedSwordPatch<?>)EpicFightCapabilities.getEntityPatch(this, DMCSummonedSwordPatch.class);
            LivingEntity target = swordPatch != null ? swordPatch.target() : null;
            if (target != null && target.m_6084_() && !this.isNoAim()) {
               this.aimAtEntity(target);
            }
         } else {
            if (this.f_19797_ >= this.lifetimeTicks) {
               if (this.isImpale() && this.hasHitTarget()) {
                  this.explodeImpale();
               }

               this.m_146870_();
               return;
            }

            if (!this.initialVelocityApplied) {
               this.applyInitialVelocity((float)this.getShootSpeed());
               this.initialVelocityApplied = true;
            }

            if (!this.hasHitTarget() && !this.isStuckInBlock()) {
               this.alignRotationToVelocity();
               this.dealDamage();
            }

            if (this.hasHitTarget() && !this.isDiscard && !this.isHeavyRain() && !this.isStorm() && !this.isProvocation() && !this.isImpale()) {
               this.isDiscard = true;
               InvincibleMod_DMC.queueServerWork(this.discardDelayTicks, this::m_146870_);
            }
         }
      }
   }

   private boolean isOrdinarySummonedSword() {
      return this.ordinarySummonedSword
         && !this.isTrick()
         && !this.isBlast()
         && !this.isStorm()
         && !this.isSpiral()
         && !this.isHeavyRain()
         && !this.isProvocation()
         && !this.isImpale()
         && !this.isSpine();
   }

   public void setOrdinarySummonedSword(boolean ordinarySummonedSword) {
      this.ordinarySummonedSword = ordinarySummonedSword;
   }

   private void alignRotationToVelocity() {
      Vec3 motion = this.m_20184_();
      if (motion.m_82556_() > 0.001) {
         this.alignRotationToDirection(motion);
      }
   }

   private void alignRotationToDirection(Vec3 direction) {
      this.alignRotationToDirection(direction, true);
   }

   private void alignRotationToDirection(Vec3 direction, boolean snapPreviousRotation) {
      if (!(direction.m_82556_() <= 1.0E-7)) {
         Vec3 normalized = direction.m_82541_();
         double hDist = normalized.m_165924_();
         float yRot = (float)(Mth.m_14136_(normalized.f_82481_, normalized.f_82479_) * (180.0 / Math.PI)) - 90.0F;
         float xRot = (float)(-(Mth.m_14136_(normalized.f_82480_, hDist) * (180.0 / Math.PI)));
         this.m_146922_(yRot);
         this.m_146926_(xRot);
         if (snapPreviousRotation) {
            this.f_19859_ = yRot;
            this.f_19860_ = xRot;
            this.f_20884_ = yRot;
            this.f_20886_ = yRot;
         }

         this.f_20883_ = yRot;
         this.f_20885_ = yRot;
         this.setSyncXRot(xRot);
      }
   }

   private void dealDamage() {
      if (!this.m_9236_().f_46443_) {
         if (!this.isStuckInBlock()) {
            LivingEntity owner = this.getOwner();
            if (owner != null) {
               LivingEntityPatch<?> ownerPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(owner, LivingEntityPatch.class);
               if (ownerPatch != null) {
                  Vec3 currentPos = this.m_20182_();
                  Vec3 motion = this.m_20184_();
                  AABB damageBox;
                  if (motion.m_82556_() > 0.001) {
                     Vec3 prevPos = currentPos.m_82546_(motion);
                     damageBox = new AABB(prevPos.f_82479_, prevPos.f_82480_, prevPos.f_82481_, currentPos.f_82479_, currentPos.f_82480_, currentPos.f_82481_);
                  } else {
                     damageBox = this.m_20191_();
                  }

                  float scale = this.m_6134_();
                  double radius = 0.35 * (double)scale;
                  if (this.isHeavyRain()) {
                     radius = 0.6 * (double)scale;
                  }

                  if (this.isStorm()) {
                     radius = 1.25 * (double)scale;
                  }

                  if (this.isProvocation()) {
                     radius = 0.025 * (double)scale;
                  }

                  damageBox = damageBox.m_82377_(radius, radius, radius);
                  List<LivingEntity> targets = this.m_9236_()
                     .m_6443_(
                        LivingEntity.class,
                        damageBox,
                        entity -> entity != owner
                              && entity.m_6084_()
                              && !entity.m_5833_()
                              && !(entity instanceof DodgeLocationIndicator)
                              && !(entity instanceof DMCSummonedSwordEntity)
                              && (!(entity instanceof DoppelgangerEntity d) || d.getOwner() == null || d.getOwner() != owner)
                     );
                  if (!targets.isEmpty()) {
                     List<Entity> rawEntities = new ArrayList<>(targets);
                     ArrayList<LivingEntity> sorted = new ArrayList<>();
                     HitEntityList targetFirst = new HitEntityList(ownerPatch, rawEntities, Priority.TARGET);

                     while (targetFirst.next()) {
                        if (targetFirst.getEntity() instanceof LivingEntity le) {
                           sorted.add(le);
                        }
                     }

                     if (!this.isTrick()) {
                        HitEntityList distanceOrder = new HitEntityList(ownerPatch, rawEntities, Priority.DISTANCE);

                        while (distanceOrder.next()) {
                           Entity var16 = distanceOrder.getEntity();
                           if (var16 instanceof LivingEntity) {
                              LivingEntity le = (LivingEntity)var16;
                              if (!sorted.contains(le)) {
                                 sorted.add(le);
                              }
                           }
                        }
                     }

                     int hitCount = 0;
                     int maxStrikes;
                     if (this.isBlast()) {
                        maxStrikes = 4;
                     } else if (this.isHeavyRain()) {
                        maxStrikes = 1;
                     } else if (this.isTrick()) {
                        maxStrikes = 1;
                     } else if (this.isProvocation()) {
                        maxStrikes = 1;
                     } else if (this.isImpale()) {
                        maxStrikes = 1;
                     } else {
                        maxStrikes = Math.max(2, (int)owner.m_21133_((Attribute)EpicFightAttributes.MAX_STRIKES.get()));
                     }

                     for (LivingEntity target : sorted) {
                        if (hitCount >= maxStrikes) {
                           break;
                        }

                        float baseArmorNeg = this.isBlast() ? 80.0F : (this.isHeavyRain() ? 100.0F : ownerPatch.getArmorNegation(InteractionHand.MAIN_HAND));
                        float baseImpact = 0.1F;
                        StunType stun = StunType.HOLD;
                        float baseDamage = (float)owner.m_21133_(Attributes.f_22281_) * 0.1F;
                        EpicFightDamageSource epicFightDamageSource = EpicFightDamageSources.mobAttack(owner)
                           .addRuntimeTag(DMCSummonedSwordPatch.SUMMONED_SWORD_DAMAGE)
                           .addRuntimeTag(DMCDamageTypeTags.NOT_CHARGE)
                           .setAnimation(SummonedSwordAnimations.SUMMONED_SWORD)
                           .setBaseArmorNegation(baseArmorNeg)
                           .setBaseImpact(baseImpact)
                           .setUsedItem(owner.m_21120_(InteractionHand.MAIN_HAND))
                           .setStunType(stun);
                        ((ICustomStunDamageSource)epicFightDamageSource)
                           .invincible$setCustomStunAnimations(
                              CustomStunAnimations.HIT_FROM_LEFT,
                              CustomStunAnimations.HIT_FROM_RIGHT,
                              CustomStunAnimations.HIT_FROM_LEFT_AIR,
                              CustomStunAnimations.HIT_FROM_RIGHT_AIR
                           );
                        if (this.isProvocation()) {
                           ((ICustomStunDamageSource)epicFightDamageSource)
                              .invincible$setCustomStunAnimations(
                                 CustomStunAnimations.HIT_UP_3, CustomStunAnimations.HIT_UP_3, CustomStunAnimations.HIT_UP_3, CustomStunAnimations.HIT_UP_3
                              );
                        }

                        if (this.isBlast()) {
                           epicFightDamageSource.addRuntimeTag(DMCSummonedSwordPatch.BLAST_SWORD_DAMAGE);
                           epicFightDamageSource.addRuntimeTag(YamatoAnimations.SLOW_PERSISTENT);
                           epicFightDamageSource.addExtraDamage(EXTRA_DAMAGE.create(new float[]{2.0F}));
                           baseDamage *= 2.0F;
                           ((ICustomStunDamageSource)epicFightDamageSource)
                              .invincible$setCustomStunAnimations(
                                 CustomStunAnimations.HIT_FROM_LEFT,
                                 CustomStunAnimations.HIT_FROM_RIGHT,
                                 CustomStunAnimations.HIT_FROM_LEFT_AIR,
                                 CustomStunAnimations.HIT_FROM_RIGHT_AIR
                              );
                        }

                        if (this.isHeavyRain()) {
                           epicFightDamageSource.addRuntimeTag(DMCSummonedSwordPatch.HEAVY_RAIN_SWORD_DAMAGE);
                           epicFightDamageSource.addRuntimeTag(YamatoAnimations.SLOW_PERSISTENT);
                           baseDamage = 0.5F;
                           ((ICustomStunDamageSource)epicFightDamageSource)
                              .invincible$setCustomStunAnimations(
                                 CustomStunAnimations.HIT_FROM_LEFT,
                                 CustomStunAnimations.HIT_FROM_RIGHT,
                                 CustomStunAnimations.HIT_FROM_LEFT_AIR,
                                 CustomStunAnimations.HIT_FROM_RIGHT_AIR
                              );
                        }

                        if (this.isStorm()) {
                           epicFightDamageSource.addRuntimeTag(DMCSummonedSwordPatch.STORM_SWORD_DAMAGE);
                           epicFightDamageSource.addExtraDamage(EXTRA_DAMAGE.create(new float[]{2.0F}));
                           ((ICustomStunDamageSource)epicFightDamageSource)
                              .invincible$setCustomStunAnimations(
                                 CustomStunAnimations.HIT_UP_4, CustomStunAnimations.HIT_UP_4, CustomStunAnimations.HIT_UP_4, CustomStunAnimations.HIT_UP_4
                              );
                        }

                        if (this.isImpale()) {
                           ((ICustomStunDamageSource)epicFightDamageSource)
                              .invincible$setCustomStunAnimations(
                                 CustomStunAnimations.HIT_FROM_LEFT,
                                 CustomStunAnimations.HIT_FROM_RIGHT,
                                 CustomStunAnimations.HIT_FROM_LEFT_AIR,
                                 CustomStunAnimations.HIT_FROM_RIGHT_AIR
                              );
                        }

                        int prevInvulTime = target.f_19802_;
                        target.f_19802_ = 0;
                        if (!DamageFilterUtils.shouldSkipTarget(owner, target)) {
                           if (target.m_6469_(epicFightDamageSource, baseDamage)) {
                              if (hitCount == 0) {
                                 target.m_5496_((SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get(), 1.0F, 1.0F);
                              } else {
                                 target.m_5496_((SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get(), 0.5F, 1.2F);
                              }

                              owner.m_21335_(target);
                              if (this.isHeavyRain() && target != owner) {
                                 target.m_7292_(new MobEffectInstance((MobEffect)DMCEffects.SLOW.get(), 80, 0, false, false, false));
                              }

                              if (this.isTrick()) {
                                 ServerPlayerPatch ownerPatchSp = (ServerPlayerPatch)EpicFightCapabilities.getEntityPatch(owner, ServerPlayerPatch.class);
                                 if (ownerPatchSp != null) {
                                    TargetTeleportUtils.ExecuteYamatoTricker(ownerPatchSp, this.m_20182_(), target, true);
                                    owner.m_9236_()
                                       .m_6263_(
                                          null, owner.m_20185_(), owner.m_20186_(), owner.m_20189_(), SoundEvents.f_144245_, SoundSource.PLAYERS, 1.0F, 1.0F
                                       );
                                 }
                              }

                              if (this.isImpale()) {
                                 detonateExistingImpaleOn(target);
                                 this.m_20256_(Vec3.f_82478_);
                                 this.lockedTrajectory = Vec3.f_82478_;
                                 this.f_19794_ = true;
                                 this.f_19804_.m_135381_(SCALE, 1.35F);
                                 Vec3 flightDirection = motion.m_82556_() > 1.0E-7 ? motion.m_82541_() : this.m_20154_().m_82541_();
                                 Vec3 flightStart = currentPos.m_82546_(motion);
                                 Vec3 impactPoint = target.m_20191_().m_82400_(0.05).m_82371_(flightStart, currentPos).orElse(currentPos);
                                 this.attachImpaleToTarget(target, impactPoint, flightDirection);
                                 this.setLifetimeTicks(this.impaleDetonateTicks);
                                 this.f_19797_ = 0;
                              }

                              hitCount++;
                           }

                           target.f_19802_ = prevInvulTime;
                        }
                     }

                     if (hitCount > 0 && !this.isHeavyRain() && !this.isProvocation() && !this.isImpale()) {
                        this.setHasHitTarget(true);
                     }

                     if (hitCount > 0 && this.isImpale()) {
                        this.setHasHitTarget(true);
                     }
                  }
               }
            }
         }
      }
   }

   private void explodeImpale() {
      LivingEntity owner = this.getOwner();
      if (owner != null) {
         LivingEntityPatch<?> ownerPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(owner, LivingEntityPatch.class);
         if (ownerPatch != null) {
            EpicFightDamageSource ds = EpicFightDamageSources.mobAttack(owner)
               .addRuntimeTag(DMCSummonedSwordPatch.SUMMONED_SWORD_DAMAGE)
               .setAnimation(SummonedSwordAnimations.SUMMONED_SWORD)
               .setStunType(StunType.HOLD)
               .setBaseImpact(1.5F);
            ((ICustomStunDamageSource)ds)
               .invincible$setCustomStunAnimations(
                  CustomStunAnimations.HIT_UP_3, CustomStunAnimations.HIT_UP_3, CustomStunAnimations.HIT_UP_3, CustomStunAnimations.HIT_UP_3
               );
            AABB box = this.m_20191_().m_82400_(2.0);

            for (LivingEntity t : this.m_9236_()
               .m_6443_(
                  LivingEntity.class,
                  box,
                  e -> e.m_6084_() && e != owner && !(e instanceof DMCSummonedSwordEntity) && (!(e instanceof DoppelgangerEntity d) || d.getOwner() != owner)
               )) {
               if (!DamageFilterUtils.shouldSkipTarget(owner, t)) {
                  t.m_6469_(ds, 2.0F);
               }
            }

            this.m_9236_().m_6263_(null, this.m_20185_(), this.m_20186_(), this.m_20189_(), SoundEvents.f_11983_, SoundSource.HOSTILE, 1.2F, 0.8F);
         }
      }
   }

   private static void detonateExistingImpaleOn(LivingEntity target) {
      for (Entity entity : target.m_9236_().m_142646_().m_142273_()) {
         if (entity instanceof DMCSummonedSwordEntity) {
            DMCSummonedSwordEntity sword = (DMCSummonedSwordEntity)entity;
            if (sword.isImpale() && sword.hasHitTarget() && sword.getImpaleTarget() == target) {
               sword.m_146870_();
            }
         }
      }
   }

   public static void detonateAllImpale(LivingEntity owner) {
      if (!owner.m_9236_().f_46443_) {
         ArrayList<DMCSummonedSwordEntity> swords = new ArrayList<>();

         for (Entity entry : owner.m_9236_().m_142646_().m_142273_()) {
            if (entry instanceof DMCSummonedSwordEntity) {
               DMCSummonedSwordEntity sword = (DMCSummonedSwordEntity)entry;
               if (sword.m_6084_() && sword.isImpale() && sword.hasHitTarget() && owner.equals(sword.getOwner())) {
                  swords.add(sword);
               }
            }
         }

         for (DMCSummonedSwordEntity sword : swords) {
            sword.explodeImpale();
            sword.m_146870_();
         }
      }
   }

   public static void summonImpale(ServerPlayerPatch serverPlayerPatch) {
      SummonedSwordSpawner.summonImpale(serverPlayerPatch);
   }

   public static void summonImpale(ServerPlayerPatch serverPlayerPatch, int detonateTicks) {
      SummonedSwordSpawner.summonImpale(serverPlayerPatch, detonateTicks);
   }

   public void launch(@Nullable LivingEntity target) {
      if (!this.m_9236_().f_46443_ && this.isInStandby()) {
         this.setInStandby(false);
         this.f_19797_ = 0;
         if (this.isNoAim()) {
            this.applyInitialVelocity((float)this.getShootSpeed());
            this.initialVelocityApplied = true;
         } else {
            LivingEntity owner = this.getOwner();
            if (target != null && target.m_6084_()) {
               this.aimAtEntity(target);
            } else if (owner != null) {
               LivingEntityPatch<?> ownerPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(owner, LivingEntityPatch.class);
               float yRot = ownerPatch != null ? ((LivingEntity)ownerPatch.getOriginal()).f_20886_ : owner.m_146908_();
               this.m_146922_(yRot);
               this.m_146926_(owner.m_146909_());
               this.setSyncXRot(owner.m_146909_());
            }
         }
      }
   }

   public void launchAlongCurrentRotation() {
      if (!this.m_9236_().f_46443_ && this.isInStandby()) {
         this.setInStandby(false);
         this.f_19797_ = 0;
         this.applyInitialVelocity((float)this.getShootSpeed());
         this.initialVelocityApplied = true;
      }
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(DATA_OWNER_UUID, Optional.empty());
      this.f_19804_.m_135372_(DATA_OWNER_ID, 0);
      this.f_19804_.m_135372_(SYNC_X_ROT, 0.0F);
      this.f_19804_.m_135372_(SCALE, 1.0F);
      this.f_19804_.m_135372_(PLAY_ANIMATION, false);
      this.f_19804_.m_135372_(SHOULD_RENDER, false);
      this.f_19804_.m_135372_(DATA_IS_TRICK, false);
      this.f_19804_.m_135372_(DATA_IS_IN_STANDBY, false);
      this.f_19804_.m_135372_(DATA_NO_AIM, false);
      this.f_19804_.m_135372_(DATA_HAS_HIT_TARGET, false);
      this.f_19804_.m_135372_(DATA_IS_STUCK, false);
      this.f_19804_.m_135372_(DATA_IS_BLAST, false);
      this.f_19804_.m_135372_(DATA_IS_SPIRAL, false);
      this.f_19804_.m_135372_(DATA_FORMATION_INDEX, -1);
      this.f_19804_.m_135372_(DATA_MOTION_CONTROLLER_ID, -1);
      this.f_19804_.m_135372_(DATA_MOTION_EPOCH, 0);
      this.f_19804_.m_135372_(DATA_MOTION_OFFSET, new Vector3f());
      this.f_19804_.m_135372_(DATA_MOTION_YAW, 0.0F);
      this.f_19804_.m_135372_(DATA_MOTION_PITCH, 0.0F);
      this.f_19804_.m_135372_(DATA_IS_STORM, false);
      this.f_19804_.m_135372_(DATA_IS_HEAVY_RAIN, false);
      this.f_19804_.m_135372_(DATA_IS_PROVOCATION, false);
      this.f_19804_.m_135372_(DATA_SHOOT_SPEED, 5);
      this.f_19804_.m_135372_(DATA_TARGET_POS, new Vector3f());
      this.f_19804_.m_135372_(DATA_IS_IMPALE, false);
      this.f_19804_.m_135372_(DATA_IS_SPINE, false);
      this.f_19804_.m_135372_(DATA_IMPALE_TARGET_ID, -1);
      this.f_19804_.m_135372_(DATA_IMPALE_JOINT_ID, -1);
      this.f_19804_.m_135372_(DATA_IMPALE_LOCAL_POS, new Vector3f());
      this.f_19804_.m_135372_(DATA_IMPALE_LOCAL_DIR, new Vector3f(0.0F, 0.0F, 1.0F));
   }

   public int getShootSpeed() {
      return (Integer)this.f_19804_.m_135370_(DATA_SHOOT_SPEED);
   }

   public void setShootSpeed(int speed) {
      this.f_19804_.m_135381_(DATA_SHOOT_SPEED, speed);
   }

   public boolean isTrick() {
      return (Boolean)this.f_19804_.m_135370_(DATA_IS_TRICK);
   }

   public void setTrick(boolean isAngel) {
      this.f_19804_.m_135381_(DATA_IS_TRICK, isAngel);
   }

   public boolean isInStandby() {
      return (Boolean)this.f_19804_.m_135370_(DATA_IS_IN_STANDBY);
   }

   public void setInStandby(boolean standby) {
      this.f_19804_.m_135381_(DATA_IS_IN_STANDBY, standby);
   }

   public boolean hasHitTarget() {
      return (Boolean)this.f_19804_.m_135370_(DATA_HAS_HIT_TARGET);
   }

   public void setHasHitTarget(boolean hasHitTarget) {
      this.f_19804_.m_135381_(DATA_HAS_HIT_TARGET, hasHitTarget);
   }

   public boolean isStuckInBlock() {
      return (Boolean)this.f_19804_.m_135370_(DATA_IS_STUCK);
   }

   public void setStuckInBlock(boolean stuck) {
      this.f_19804_.m_135381_(DATA_IS_STUCK, stuck);
   }

   public boolean isBlast() {
      return (Boolean)this.f_19804_.m_135370_(DATA_IS_BLAST);
   }

   public void setBlast(boolean blast) {
      this.f_19804_.m_135381_(DATA_IS_BLAST, blast);
   }

   public boolean isStorm() {
      return (Boolean)this.f_19804_.m_135370_(DATA_IS_STORM);
   }

   public void setStorm(boolean storm) {
      this.f_19804_.m_135381_(DATA_IS_STORM, storm);
   }

   public boolean isSpiral() {
      return (Boolean)this.f_19804_.m_135370_(DATA_IS_SPIRAL);
   }

   public void setSpiral(boolean spiral) {
      this.f_19804_.m_135381_(DATA_IS_SPIRAL, spiral);
   }

   public int getFormationIndex() {
      return (Integer)this.f_19804_.m_135370_(DATA_FORMATION_INDEX);
   }

   public void setFormationIndex(int index) {
      this.f_19804_.m_135381_(DATA_FORMATION_INDEX, index);
   }

   public void bindMotionController(Entity controller, int formationIndex) {
      this.f_19804_.m_135381_(DATA_MOTION_CONTROLLER_ID, controller.m_19879_());
      this.f_19804_.m_135381_(DATA_FORMATION_INDEX, formationIndex);
      this.f_19804_.m_135381_(DATA_MOTION_EPOCH, (Integer)this.f_19804_.m_135370_(DATA_MOTION_EPOCH) + 1);
   }

   public void setMotionOffset(Vec3 offset) {
      this.f_19804_.m_135381_(DATA_MOTION_OFFSET, new Vector3f((float)offset.f_82479_, (float)offset.f_82480_, (float)offset.f_82481_));
   }

   public Vec3 getMotionOffset() {
      Vector3f offset = (Vector3f)this.f_19804_.m_135370_(DATA_MOTION_OFFSET);
      return new Vec3((double)offset.x(), (double)offset.y(), (double)offset.z());
   }

   public void setMotionRotation(float yaw, float pitch) {
      this.f_19804_.m_135381_(DATA_MOTION_YAW, yaw);
      this.f_19804_.m_135381_(DATA_MOTION_PITCH, pitch);
   }

   public float getMotionYaw() {
      return (Float)this.f_19804_.m_135370_(DATA_MOTION_YAW);
   }

   public float getMotionPitch() {
      return (Float)this.f_19804_.m_135370_(DATA_MOTION_PITCH);
   }

   public void detachMotionController() {
      this.applyManagedMotion();
      this.f_19804_.m_135381_(DATA_MOTION_CONTROLLER_ID, -1);
      this.f_19804_.m_135381_(DATA_FORMATION_INDEX, -1);
      this.f_19804_.m_135381_(DATA_MOTION_EPOCH, (Integer)this.f_19804_.m_135370_(DATA_MOTION_EPOCH) + 1);
   }

   public boolean isManagedMotionBound() {
      return (Integer)this.f_19804_.m_135370_(DATA_MOTION_CONTROLLER_ID) >= 0;
   }

   public boolean isManagedBy(Entity controller) {
      return controller != null && (Integer)this.f_19804_.m_135370_(DATA_MOTION_CONTROLLER_ID) == controller.m_19879_();
   }

   public boolean isManagedMotionReady() {
      int controllerId = (Integer)this.f_19804_.m_135370_(DATA_MOTION_CONTROLLER_ID);
      return controllerId < 0 || this.m_9236_().m_6815_(controllerId) instanceof SummonedSwordMotionController;
   }

   public int getMotionEpoch() {
      return (Integer)this.f_19804_.m_135370_(DATA_MOTION_EPOCH);
   }

   public boolean snapToManagedMotion() {
      return this.applyManagedMotion();
   }

   private boolean applyManagedMotion() {
      int controllerId = (Integer)this.f_19804_.m_135370_(DATA_MOTION_CONTROLLER_ID);
      if (controllerId < 0) {
         return false;
      } else if (this.m_9236_().m_6815_(controllerId) instanceof SummonedSwordMotionController motionController) {
         SummonedSwordTransform previous = motionController.sampleSwordTransform(this, true);
         SummonedSwordTransform current = motionController.sampleSwordTransform(this, false);
         if (previous != null && current != null) {
            this.m_6034_(current.position().f_82479_, current.position().f_82480_, current.position().f_82481_);
            this.f_19854_ = previous.position().f_82479_;
            this.f_19855_ = previous.position().f_82480_;
            this.f_19856_ = previous.position().f_82481_;
            this.m_146922_(current.yaw());
            this.f_19859_ = previous.yaw();
            this.m_146926_(current.pitch());
            this.f_19860_ = previous.pitch();
            this.m_5618_(current.yaw());
            this.f_20884_ = previous.yaw();
            this.m_5616_(current.yaw());
            this.f_20886_ = previous.yaw();
            this.setSyncXRot(current.pitch());
            this.m_20256_(Vec3.f_82478_);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean isHeavyRain() {
      return (Boolean)this.f_19804_.m_135370_(DATA_IS_HEAVY_RAIN);
   }

   public void setHeavyRain(boolean heavyRain) {
      this.f_19804_.m_135381_(DATA_IS_HEAVY_RAIN, heavyRain);
   }

   public boolean isProvocation() {
      return (Boolean)this.f_19804_.m_135370_(DATA_IS_PROVOCATION);
   }

   public void setProvocation(boolean provocation) {
      this.f_19804_.m_135381_(DATA_IS_PROVOCATION, provocation);
   }

   public boolean isImpale() {
      return (Boolean)this.f_19804_.m_135370_(DATA_IS_IMPALE);
   }

   public void setImpale(boolean impale) {
      this.f_19804_.m_135381_(DATA_IS_IMPALE, impale);
   }

   public boolean isSpine() {
      return (Boolean)this.f_19804_.m_135370_(DATA_IS_SPINE);
   }

   public void setSpine(boolean spine) {
      this.f_19804_.m_135381_(DATA_IS_SPINE, spine);
   }

   public boolean getPlayAnimation() {
      return (Boolean)this.f_19804_.m_135370_(PLAY_ANIMATION);
   }

   public void setPlayAnimation(boolean b) {
      this.f_19804_.m_135381_(PLAY_ANIMATION, b);
   }

   public boolean getShouldRender() {
      return (Boolean)this.f_19804_.m_135370_(SHOULD_RENDER);
   }

   public void setShouldRender(boolean b) {
      this.f_19804_.m_135381_(SHOULD_RENDER, b);
   }

   public void setLockedTrajectory(Vec3 trajectory) {
      this.lockedTrajectory = trajectory;
   }

   public void setDiscardDelayTicks(int ticks) {
      this.discardDelayTicks = ticks;
   }

   public void setImpaleDetonateTicks(int ticks) {
      this.impaleDetonateTicks = ticks;
   }

   @Nullable
   public LivingEntity getImpaleTarget() {
      int targetId = (Integer)this.f_19804_.m_135370_(DATA_IMPALE_TARGET_ID);
      if (targetId < 0) {
         return null;
      } else {
         return this.m_9236_().m_6815_(targetId) instanceof LivingEntity livingTarget ? livingTarget : null;
      }
   }

   private void attachImpaleToTarget(LivingEntity target, Vec3 impactPoint, Vec3 flightDirection) {
      LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
      boolean useAnimatedAttachment = targetPatch != null && targetPatch.getArmature() != null;
      Vec3 direction = this.resolveImpaleDirection(target, flightDirection, useAnimatedAttachment);
      impactPoint = this.randomizeImpaleImpactPoint(target, impactPoint, direction, useAnimatedAttachment);
      Vec3 embeddedPoint = impactPoint.m_82549_(direction.m_82490_(0.15));
      Joint joint = findNearestImpaleJoint(targetPatch, embeddedPoint);
      OpenMatrix4f attachmentMatrix = createImpaleAttachmentMatrix(target, targetPatch, joint, 1.0F);
      OpenMatrix4f inverse = new OpenMatrix4f(attachmentMatrix).invert();
      Vec3 localPosition = OpenMatrix4f.transform(inverse, embeddedPoint);
      Vec3 localDirectionEnd = OpenMatrix4f.transform(inverse, embeddedPoint.m_82549_(direction));
      Vec3 localDirection = localDirectionEnd.m_82546_(localPosition).m_82541_();
      this.f_19804_.m_135381_(DATA_IMPALE_TARGET_ID, target.m_19879_());
      this.f_19804_.m_135381_(DATA_IMPALE_JOINT_ID, joint != null ? joint.getId() : -1);
      this.f_19804_.m_135381_(DATA_IMPALE_LOCAL_POS, toVector3f(localPosition));
      this.f_19804_.m_135381_(DATA_IMPALE_LOCAL_DIR, toVector3f(localDirection));
      this.updateImpaleAttachment(target, 1.0F);
   }

   private Vec3 resolveImpaleDirection(LivingEntity target, Vec3 flightDirection, boolean useAnimatedAttachment) {
      Vec3 direction = flightDirection.m_82556_() > 1.0E-7 ? flightDirection.m_82541_() : new Vec3(0.0, 0.0, 1.0);
      if (useAnimatedAttachment) {
         return direction;
      } else {
         Vec3 horizontal = new Vec3(direction.f_82479_, 0.0, direction.f_82481_);
         if (horizontal.m_82556_() <= 1.0E-7) {
            LivingEntity owner = this.getOwner();
            Vec3 origin = owner != null ? owner.m_20182_() : this.m_20182_();
            horizontal = new Vec3(target.m_20185_() - origin.f_82479_, 0.0, target.m_20189_() - origin.f_82481_);
         }

         if (horizontal.m_82556_() <= 1.0E-7) {
            float bodyYaw = (float)Math.toRadians((double)(target.f_20883_ + 180.0F));
            horizontal = new Vec3((double)Mth.m_14031_(bodyYaw), 0.0, (double)(-Mth.m_14089_(bodyYaw)));
         }

         horizontal = horizontal.m_82541_();
         double vertical = Mth.m_14008_(direction.f_82480_, -0.36, 0.36);
         return new Vec3(horizontal.f_82479_, vertical, horizontal.f_82481_).m_82541_();
      }
   }

   private Vec3 randomizeImpaleImpactPoint(LivingEntity target, Vec3 originalImpactPoint, Vec3 flightDirection, boolean useAnimatedAttachment) {
      AABB bounds = target.m_20191_();
      Vec3 direction = flightDirection.m_82556_() > 1.0E-7 ? flightDirection.m_82541_() : new Vec3(0.0, 0.0, 1.0);
      RandomSource random = RandomSource.m_216335_(
         this.m_20148_().getMostSignificantBits() ^ this.m_20148_().getLeastSignificantBits() ^ target.m_20148_().getMostSignificantBits()
      );
      double centerX = (bounds.f_82288_ + bounds.f_82291_) * 0.5;
      double centerZ = (bounds.f_82290_ + bounds.f_82293_) * 0.5;
      double horizontalRadius = Math.min(bounds.m_82362_(), bounds.m_82385_()) * 0.28;
      double y = Mth.m_14139_(0.36 + random.m_188500_() * 0.42000000000000004, bounds.f_82289_, bounds.f_82292_);
      Vec3 torsoPoint;
      if (useAnimatedAttachment) {
         double randomAngle = random.m_188500_() * Math.PI * 2.0;
         double randomRadius = Math.sqrt(random.m_188500_()) * horizontalRadius;
         torsoPoint = new Vec3(centerX + Math.cos(randomAngle) * randomRadius, y, centerZ + Math.sin(randomAngle) * randomRadius);
      } else {
         Vec3 horizontalDirection = new Vec3(direction.f_82479_, 0.0, direction.f_82481_).m_82541_();
         Vec3 lateral = new Vec3(-horizontalDirection.f_82481_, 0.0, horizontalDirection.f_82479_);
         double lateralOffset = (random.m_188500_() * 2.0 - 1.0) * horizontalRadius;
         torsoPoint = new Vec3(centerX, y, centerZ).m_82549_(lateral.m_82490_(lateralOffset));
      }

      double traceLength = Math.max(2.0, bounds.m_82309_() * 2.0);
      return bounds.m_82400_(0.02)
         .m_82371_(torsoPoint.m_82546_(direction.m_82490_(traceLength)), torsoPoint.m_82549_(direction.m_82490_(traceLength)))
         .orElse(originalImpactPoint);
   }

   private void updateImpaleAttachment(LivingEntity target, float partialTicks) {
      LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
      int jointId = (Integer)this.f_19804_.m_135370_(DATA_IMPALE_JOINT_ID);
      Joint joint = targetPatch != null && targetPatch.getArmature() != null && jointId >= 0 && jointId < targetPatch.getArmature().getJointNumber()
         ? targetPatch.getArmature().searchJointById(jointId)
         : null;
      OpenMatrix4f attachmentMatrix = createImpaleAttachmentMatrix(target, targetPatch, joint, partialTicks);
      Vector3f storedPosition = (Vector3f)this.f_19804_.m_135370_(DATA_IMPALE_LOCAL_POS);
      Vector3f storedDirection = (Vector3f)this.f_19804_.m_135370_(DATA_IMPALE_LOCAL_DIR);
      Vec3 localPosition = new Vec3((double)storedPosition.x(), (double)storedPosition.y(), (double)storedPosition.z());
      Vec3 localDirection = new Vec3((double)storedDirection.x(), (double)storedDirection.y(), (double)storedDirection.z());
      Vec3 worldPosition = OpenMatrix4f.transform(attachmentMatrix, localPosition);
      Vec3 worldDirectionEnd = OpenMatrix4f.transform(attachmentMatrix, localPosition.m_82549_(localDirection));
      Vec3 worldDirection = worldDirectionEnd.m_82546_(worldPosition).m_82541_();
      this.m_6034_(worldPosition.f_82479_, worldPosition.f_82480_, worldPosition.f_82481_);
      this.alignRotationToDirection(worldDirection, false);
   }

   @Nullable
   private static Joint findNearestImpaleJoint(@Nullable LivingEntityPatch<?> targetPatch, Vec3 worldPosition) {
      if (targetPatch != null && targetPatch.getArmature() != null) {
         Joint nearestTorso = null;
         double nearestTorsoDistance = Double.MAX_VALUE;
         Joint nearest = null;
         double nearestDistance = Double.MAX_VALUE;

         for (int jointId = 0; jointId < targetPatch.getArmature().getJointNumber(); jointId++) {
            Joint joint = targetPatch.getArmature().searchJointById(jointId);
            if (joint != null && !"Root".equals(joint.getName())) {
               OpenMatrix4f jointToWorld = DMCAnimationUtils.getJointWorldTransform(targetPatch, joint, 1.0F);
               if (jointToWorld != null) {
                  Vec3 jointPosition = OpenMatrix4f.transform(jointToWorld, Vec3.f_82478_);
                  double distance = jointPosition.m_82557_(worldPosition);
                  if (("Chest".equals(joint.getName()) || "Torso".equals(joint.getName())) && distance < nearestTorsoDistance) {
                     nearestTorsoDistance = distance;
                     nearestTorso = joint;
                  }

                  if (distance < nearestDistance) {
                     nearestDistance = distance;
                     nearest = joint;
                  }
               }
            }
         }

         return nearestTorso != null ? nearestTorso : (nearest != null ? nearest : targetPatch.getArmature().rootJoint);
      } else {
         return null;
      }
   }

   private static OpenMatrix4f createImpaleAttachmentMatrix(
      LivingEntity target, @Nullable LivingEntityPatch<?> targetPatch, @Nullable Joint joint, float partialTicks
   ) {
      if (targetPatch != null && joint != null) {
         OpenMatrix4f jointToWorld = DMCAnimationUtils.getJointWorldTransform(targetPatch, joint, partialTicks);
         if (jointToWorld != null) {
            return jointToWorld;
         }
      }

      Vec3 targetPosition = target.m_20318_(partialTicks);
      float bodyYaw = Mth.m_14189_(partialTicks, target.f_20884_, target.f_20883_);
      return OpenMatrix4f.createTranslation((float)targetPosition.f_82479_, (float)targetPosition.f_82480_, (float)targetPosition.f_82481_)
         .rotateDeg(-(bodyYaw + 180.0F), Vec3f.Y_AXIS);
   }

   private static Vector3f toVector3f(Vec3 vector) {
      return new Vector3f((float)vector.f_82479_, (float)vector.f_82480_, (float)vector.f_82481_);
   }

   public boolean isNoAim() {
      return (Boolean)this.f_19804_.m_135370_(DATA_NO_AIM);
   }

   public void setNoAim(boolean noAim) {
      this.f_19804_.m_135381_(DATA_NO_AIM, noAim);
   }

   public void tame(LivingEntity livingEntity) {
      this.setOwnerUUID(livingEntity.m_20148_());
      this.setOwnerID(livingEntity.m_19879_());
   }

   public void setSyncXRot(float f) {
      this.f_19804_.m_135381_(SYNC_X_ROT, f);
   }

   public float getSyncXrot() {
      return (Float)this.f_19804_.m_135370_(SYNC_X_ROT);
   }

   @Nullable
   public LivingEntity getOwner() {
      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         if (this.m_9236_() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.m_8791_(uuid);
            return entity instanceof LivingEntity ? (LivingEntity)entity : null;
         } else {
            Player player = this.m_9236_().m_46003_(uuid);
            if (player != null) {
               return player;
            } else {
               Entity entity = this.m_9236_().m_6815_(this.getOwnerID());
               return entity instanceof LivingEntity ? (LivingEntity)entity : null;
            }
         }
      } else {
         return null;
      }
   }

   public void aimAtEntity(Entity target) {
      if (target != null) {
         if (target instanceof LivingEntity livingTarget) {
            Vec3 targetPos = livingTarget.m_146892_().m_82520_(0.0, 0.5, 0.0);
            Vec3 targetVelocity = livingTarget.m_20184_();
            if (livingTarget.m_20096_()) {
               targetVelocity = new Vec3(targetVelocity.f_82479_, 0.0, targetVelocity.f_82481_);
            }

            double distance = this.m_20182_().m_82554_(targetPos);
            float speed = (float)this.getShootSpeed();
            double travelTime = speed > 0.0F ? distance / (double)speed : 0.0;
            Vec3 predictedPos = targetPos.m_82549_(targetVelocity.m_82490_(travelTime));
            this.f_19804_.m_135381_(DATA_TARGET_POS, new Vector3f((float)predictedPos.f_82479_, (float)predictedPos.f_82480_, (float)predictedPos.f_82481_));
            this.alignTo(predictedPos);
         } else {
            Vec3 targetPos = target.m_146892_().m_82520_(0.0, 0.5, 0.0);
            this.f_19804_.m_135381_(DATA_TARGET_POS, new Vector3f((float)targetPos.f_82479_, (float)targetPos.f_82480_, (float)targetPos.f_82481_));
            this.alignTo(targetPos);
         }
      }
   }

   private void alignTo(Vec3 targetPos) {
      Vec3 eyePos = this.m_146892_();
      Vec3 direction = targetPos.m_82546_(eyePos).m_82541_();
      double hDist = direction.m_165924_();
      float yRot = (float)(Mth.m_14136_(direction.f_82481_, direction.f_82479_) * (180.0 / Math.PI)) - 90.0F;
      float xRot = (float)(-(Mth.m_14136_(direction.f_82480_, hDist) * (180.0 / Math.PI)));
      this.m_146922_(yRot);
      this.m_146926_(xRot);
      this.m_5618_(yRot);
      this.m_5616_(yRot);
      this.setSyncXRot(xRot);
      this.f_19859_ = yRot;
      this.f_19860_ = xRot;
      this.f_20884_ = yRot;
      this.f_20886_ = yRot;
   }

   private void applyInitialVelocity(float flightSpeed) {
      if (!(this.lockedTrajectory.m_82556_() > 1.0E-7)) {
         float yRotRad = this.m_146908_() * (float) (Math.PI / 180.0);
         float xRotRad = this.getSyncXrot() * (float) (Math.PI / 180.0);
         double motionX = -Math.sin((double)yRotRad) * Math.cos((double)xRotRad);
         double motionY = -Math.sin((double)xRotRad);
         double motionZ = Math.cos((double)yRotRad) * Math.cos((double)xRotRad);
         Vec3 motion = new Vec3(motionX, motionY, motionZ).m_82541_().m_82490_((double)flightSpeed);
         this.m_20256_(motion);
         this.lockedTrajectory = motion;
      }
   }

   @Nullable
   public UUID getOwnerUUID() {
      return (UUID)((Optional)this.f_19804_.m_135370_(DATA_OWNER_UUID)).orElse(null);
   }

   public void setOwnerUUID(@Nullable UUID pUuid) {
      this.f_19804_.m_135381_(DATA_OWNER_UUID, Optional.ofNullable(pUuid));
   }

   public int getOwnerID() {
      return (Integer)this.f_19804_.m_135370_(DATA_OWNER_ID);
   }

   public void setOwnerID(int id) {
      this.f_19804_.m_135381_(DATA_OWNER_ID, id);
   }

   public float m_6134_() {
      return (Float)this.f_19804_.m_135370_(SCALE);
   }

   public int getLifetimeTicks() {
      return this.lifetimeTicks;
   }

   public void setLifetimeTicks(int lifetimeTicks) {
      this.lifetimeTicks = lifetimeTicks;
   }

   public void m_7350_(@NotNull EntityDataAccessor<?> pKey) {
      super.m_7350_(pKey);
      if (DATA_IS_HEAVY_RAIN.equals(pKey) && this.isHeavyRain() && this.m_9236_().f_46443_) {
         this.m_146926_(90.0F);
         this.f_19860_ = 90.0F;
         this.m_146922_(0.0F);
         this.f_19859_ = 0.0F;
      }
   }

   public boolean m_6094_() {
      return false;
   }

   public boolean m_6087_() {
      return false;
   }

   public boolean m_142066_() {
      return false;
   }

   public boolean m_20068_() {
      return true;
   }

   public boolean m_5829_() {
      return false;
   }

   protected void m_7324_(@NotNull Entity pEntity) {
   }

   public void m_7334_(@NotNull Entity pEntity) {
   }

   @Nullable
   public AnimationAccessor<? extends StaticAnimation> getIdleAnimation() {
      return SummonedSwordAnimations.SUMMONED_SWORD_IDLE;
   }

   @Nullable
   public AnimationAccessor<? extends StaticAnimation> getDefaultAnimation() {
      if (this.isSpiral()) {
         return SummonedSwordAnimations.SPIRAL_SWORD;
      } else if (this.isStorm()) {
         return SummonedSwordAnimations.STORM_SWORD;
      } else {
         return this.isSpine() ? SummonedSwordAnimations.SPINE_SWORD : SummonedSwordAnimations.SUMMONED_SWORD;
      }
   }

   @Nullable
   public Armature getArmature() {
      return DMCArmatures.SUMMONED_SWORD.get();
   }

   @Nullable
   public AssetAccessor<? extends SkinnedMesh> getMesh() {
      return SUMMONED_SWORD_MESH;
   }

   @Nullable
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/entity/summoned_sword.png");
   }

   @Nullable
   public ResourceLocation getLitTexture() {
      return ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/entity/summoned_sword.png");
   }

   public boolean m_6469_(@NotNull DamageSource source, float p_21017_) {
      return false;
   }

   protected void m_7355_(@NotNull BlockPos pPos, @NotNull BlockState pState) {
   }

   public boolean m_142535_(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
      return false;
   }

   public boolean m_5843_() {
      return false;
   }
}
