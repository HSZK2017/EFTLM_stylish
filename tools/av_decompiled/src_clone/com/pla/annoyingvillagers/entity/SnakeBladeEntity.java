package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.gameasset.AVAnimations;
import com.pla.annoyingvillagers.gameasset.AVSkills;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.item.DemoniacVoltageReaverItem;
import com.pla.annoyingvillagers.skill.DemoniacVoltageReaverSkill;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import com.pla.annoyingvillagers.util.HerobrinePortalCombatUtil;
import com.pla.annoyingvillagers.util.HerobrineUtil;
import com.pla.annoyingvillagers.util.WeaponEnchantmentDamageUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class SnakeBladeEntity extends Entity {
   private static final EntityDataAccessor<Optional<UUID>> CREATOR_ID = SynchedEntityData.m_135353_(SnakeBladeEntity.class, EntityDataSerializers.f_135041_);
   private static final EntityDataAccessor<Optional<UUID>> PORTAL_GROUP_ID = SynchedEntityData.m_135353_(
      SnakeBladeEntity.class, EntityDataSerializers.f_135041_
   );
   private static final EntityDataAccessor<Integer> FROM_ID = SynchedEntityData.m_135353_(SnakeBladeEntity.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> RENDER_FROM_ID = SynchedEntityData.m_135353_(SnakeBladeEntity.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> LAST_PORTAL_ORDER = SynchedEntityData.m_135353_(SnakeBladeEntity.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> TARGET_COUNT = SynchedEntityData.m_135353_(SnakeBladeEntity.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> CURRENT_TARGET_ID = SynchedEntityData.m_135353_(SnakeBladeEntity.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Float> PROGRESS = SynchedEntityData.m_135353_(SnakeBladeEntity.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.m_135353_(SnakeBladeEntity.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Boolean> RETRACTING = SynchedEntityData.m_135353_(SnakeBladeEntity.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> HAS_BLADE = SynchedEntityData.m_135353_(SnakeBladeEntity.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> ENCHANTED = SynchedEntityData.m_135353_(SnakeBladeEntity.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> GUARD = SynchedEntityData.m_135353_(SnakeBladeEntity.class, EntityDataSerializers.f_135035_);
   public static final float MAX_EXTEND_TIME = 5.0F;
   private static final double ENTITY_TARGET_Y_OFFSET = 1.0;
   private static final int MAX_PORTAL_CHAIN_TARGETS = 24;
   private static final int MAX_NORMAL_CHAIN_TARGETS = 5;
   private static final int MAX_GUARD_CHAIN_TARGETS = 5;
   private static final int POST_HIT_CHAIN_DELAY_TICKS = 3;
   private static final double PORTAL_CHAIN_SEARCH_RADIUS = 64.0;
   private final List<Entity> previouslyTouched = new ArrayList<>();
   private boolean hasChained = false;
   private boolean attemptedCurrentTargetHit = false;
   private int postHitChainDelayTicks = 0;
   public float prevProgress = 0.0F;
   private String guardDirection = null;

   public SnakeBladeEntity(EntityType<?> type, Level level) {
      super(type, level);
   }

   public SnakeBladeEntity(SpawnEntity spawnEntity, Level level) {
      this((EntityType<?>)AnnoyingVillagersModEntities.SNAKE_BLADE.get(), level);
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(CREATOR_ID, Optional.empty());
      this.f_19804_.m_135372_(PORTAL_GROUP_ID, Optional.empty());
      this.f_19804_.m_135372_(FROM_ID, -1);
      this.f_19804_.m_135372_(RENDER_FROM_ID, -1);
      this.f_19804_.m_135372_(LAST_PORTAL_ORDER, -1);
      this.f_19804_.m_135372_(TARGET_COUNT, 0);
      this.f_19804_.m_135372_(CURRENT_TARGET_ID, -1);
      this.f_19804_.m_135372_(PROGRESS, 0.0F);
      this.f_19804_.m_135372_(DAMAGE, new Random().nextFloat(10.0F, 15.0F));
      this.f_19804_.m_135372_(RETRACTING, false);
      this.f_19804_.m_135372_(HAS_BLADE, true);
      this.f_19804_.m_135372_(ENCHANTED, false);
      this.f_19804_.m_135372_(GUARD, false);
   }

   public void setEnchanted(boolean enchanted) {
      this.f_19804_.m_135381_(ENCHANTED, enchanted);
   }

   public boolean isEnchanted() {
      return (Boolean)this.f_19804_.m_135370_(ENCHANTED);
   }

   private float getBaseDamage() {
      return (Float)this.f_19804_.m_135370_(DAMAGE);
   }

   private float getDamage(LivingEntity creator) {
      return WeaponEnchantmentDamageUtil.addSharpnessBonus(this.getBaseDamage(), creator, DemoniacVoltageReaverItem.class);
   }

   public void setGuard(boolean guard) {
      this.f_19804_.m_135381_(GUARD, guard);
   }

   public boolean isGuard() {
      return (Boolean)this.f_19804_.m_135370_(GUARD);
   }

   public void setGuardDirection(String direction) {
      this.guardDirection = direction;
      this.f_19804_.m_135381_(GUARD, direction != null);
   }

   public void increaseSkillPoint(Entity entity, float value) {
      if (entity instanceof Player player) {
         PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
         if (playerPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            SkillContainer skillContainer = serverPlayerPatch.getSkill(AVSkills.DEMONIAC_VOLTAGE_REAVER);
            if (skillContainer != null) {
               DemoniacVoltageReaverSkill skill = (DemoniacVoltageReaverSkill)skillContainer.getSkill();
               float current = skillContainer.getResource();
               float needed = skillContainer.getNeededResource();
               float add = Math.min(value, needed);
               skill.setConsumptionSynchronize(skillContainer, current + add);
            }
         }
      }
   }

   public void m_8119_() {
      Entity creator = this.getCreatorEntity();
      if (creator instanceof LivingEntity livingEntity
         && (!(livingEntity.m_21205_().m_41720_() instanceof DemoniacVoltageReaverItem) || !livingEntity.m_6084_() || livingEntity.m_213877_())) {
         this.cleanupAndDiscard(creator);
         return;
      }

      HerobrineUtil.spawnEliteEffect(this.m_9236_(), this.m_20185_(), this.m_20186_(), this.m_20189_(), this);
      float progressBefore = this.getProgress();
      this.prevProgress = progressBefore;
      super.m_8119_();
      if (!this.m_9236_().m_5776_() && this.isGuard() && this.f_19797_ % 5 == 0) {
         this.tickGuardAoe(creator);
      }

      this.updateProgressAndHandleRemoval(creator);
      if (!this.m_213877_()) {
         this.updateMovementAndAttack(creator);
         if (!this.m_9236_().m_5776_()) {
            this.handleChaining(creator);
         }

         this.applyVelocity();
      }
   }

   private void tickGuardAoe(Entity creator) {
      double size = 2.0;
      double radiusSqr = 4.0;
      float knockBackStrength = 1.0F;
      LivingEntity owner = creator instanceof LivingEntity living ? living : null;

      for (LivingEntity target : this.m_9236_().m_6443_(LivingEntity.class, this.m_20191_().m_82377_(2.0, 2.0, 2.0), e -> e.m_6084_() && !e.m_5833_())) {
         if (target != owner && (owner == null || !owner.m_7307_(target) && !target.m_7307_(owner))) {
            double dx0 = target.m_20185_() - this.m_20185_();
            double dy0 = target.m_20227_(0.5) - this.m_20227_(0.5);
            double dz0 = target.m_20189_() - this.m_20189_();
            if (!(dx0 * dx0 + dy0 * dy0 + dz0 * dz0 > 4.0)) {
               if (this.m_9236_() instanceof ServerLevel serverLevel) {
                  serverLevel.m_8767_(
                     (HitParticleType)EpicFightParticles.HIT_BLUNT.get(), this.m_20185_(), this.m_20186_() + 1.5, this.m_20189_() + 0.8, 1, 0.1, 0.1, 0.1, 1.0
                  );
               }

               this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.OBSIDIAN_HIT.get(), 0.5F, (float)(0.5 + Math.random() * 0.5));
               LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
               DamageSource src = owner != null ? this.m_9236_().m_269111_().m_269104_(this, owner) : this.m_9236_().m_269111_().m_269264_();
               target.m_6469_(src, this.getDamage(owner) / 2.0F);
               EpicfightUtil.dealStaminaDamage(src, 1.0F, targetPatch, false);
               if (creator != null) {
                  this.increaseSkillPoint(creator, 3.0F);
               }

               if (targetPatch != null) {
                  targetPatch.knockBackEntity(this.m_20182_(), 1.0F);
               } else {
                  double kbX = this.m_20185_() - target.m_20185_();
                  double kbZ = this.m_20189_() - target.m_20189_();
                  target.m_147240_(1.0, kbX, kbZ);
               }
            }
         }
      }
   }

   private void updateProgressAndHandleRemoval(Entity creator) {
      float progress = this.getProgress();
      if (!this.isRetracting() && progress < 5.0F) {
         this.setProgress(progress + 1.0F);
      } else if (this.isRetracting() && progress > 0.0F) {
         this.setProgress(progress - 1.0F);
      }

      if (this.isRetracting() && this.getProgress() == 0.0F) {
         this.onFullyRetracted(creator);
      }
   }

   private void onFullyRetracted(Entity creator) {
      if (this.getFromEntity() instanceof SnakeBladeEntity parentSnakeBladeEntity) {
         parentSnakeBladeEntity.setRetracting(true);
         this.updateLastFragment(parentSnakeBladeEntity);
      } else {
         this.updateLastFragment(null);
         this.clearSnakeAnimationTag(creator);
         LivingEntityPatch<?> creatorPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(creator, LivingEntityPatch.class);
         if (creatorPatch != null) {
            AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(creatorPatch.getAnimator().getPlayerFor(null))
               .getRealAnimation();
            if (dynamicAnimation == AVAnimations.SNAKE_BLADE || dynamicAnimation == AVAnimations.SNAKE_BLADE_GUARD) {
               creatorPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
            }
         }
      }

      this.m_142687_(RemovalReason.DISCARDED);
   }

   private void clearSnakeAnimationTag(Entity creator) {
      if (creator instanceof Player player) {
         for (ItemStack stack : player.m_150109_().f_35974_) {
            if (stack.m_150930_((Item)AnnoyingVillagersModItems.DEMONIAC_VOLTAGE_REAVER.get())) {
               DemoniacVoltageReaverItem.clearSnakeAnimation(stack);
            }
         }
      } else if (creator instanceof LivingEntity living) {
         DemoniacVoltageReaverItem.clearSnakeAnimation(living.m_21205_());
      }
   }

   private void cleanupAndDiscard(Entity creator) {
      this.updateLastFragment(null);
      this.clearSnakeAnimationTag(creator);
      this.m_142687_(RemovalReason.DISCARDED);
   }

   private void updateMovementAndAttack(Entity creator) {
      if (creator instanceof LivingEntity livingCreator) {
         Entity currentTarget = this.getToEntity();
         Vec3 targetPos = null;
         if (currentTarget != null) {
            targetPos = targetCenter(currentTarget);
         } else if (this.guardDirection != null) {
            targetPos = DemoniacVoltageReaverItem.guardTargetFor(livingCreator, this.guardDirection);
         }

         if (targetPos != null) {
            Vec3 delta = targetPos.m_82546_(this.m_20182_());
            this.m_20256_(delta.m_82490_(0.5));
         }

         if (currentTarget != null
            && !(currentTarget instanceof PortalEntity)
            && !this.m_9236_().f_46443_
            && this.getProgress() >= 5.0F
            && this.postHitChainDelayTicks <= 0
            && (!this.attemptedCurrentTargetHit || this.f_19797_ % 2 == 0)) {
            this.tryAttackTarget(livingCreator, currentTarget);
            this.attemptedCurrentTargetHit = true;
         }
      }
   }

   private void tryAttackTarget(LivingEntity creator, Entity target) {
      if (target != creator) {
         if (!(target instanceof PortalEntity)) {
            if (target.m_6469_(this.m_9236_().m_269111_().m_269104_(this, creator), this.getDamage(creator))) {
               this.markTouched(target);
               this.postHitChainDelayTicks = Math.max(this.postHitChainDelayTicks, 3);
               this.increaseSkillPoint(creator, 5.0F);
               if (this.m_9236_() instanceof ServerLevel serverLevel) {
                  serverLevel.m_8767_(
                     (HitParticleType)EpicFightParticles.HIT_BLUNT.get(), this.m_20185_(), this.m_20186_() + 1.5, this.m_20189_() + 0.8, 1, 0.1, 0.1, 0.1, 1.0
                  );
               }

               this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.OBSIDIAN_HIT.get(), 0.5F, (float)(0.5 + Math.random() * 0.5));
               LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
               if (targetPatch != null) {
                  EpicfightUtil.dealStaminaDamageByPercentage(this.m_9236_().m_269111_().m_269104_(this, creator), targetPatch, 0.5, true);
               }

               if (target instanceof LivingEntity livingTarget) {
                  float strength = 3.0F;
                  double dx = this.m_20185_() - target.m_20185_();
                  double dz = this.m_20189_() - target.m_20189_();
                  livingTarget.m_147240_((double)strength, dx, dz);
               }
            }
         }
      }
   }

   private void handleChaining(Entity creator) {
      if (!this.hasChained) {
         int maxChainTargets = this.guardDirection != null ? 5 : (this.isPortalChainMode() ? 24 : 5);
         if (this.getTargetsHit() > maxChainTargets) {
            this.setRetracting(true);
         } else if (creator instanceof LivingEntity livingCreator) {
            if (!(this.getProgress() < 5.0F)) {
               if (this.guardDirection != null) {
                  String nextDirection = nextGuardDirection(this.guardDirection);
                  this.createChainGuard(nextDirection);
                  this.hasChained = true;
               } else {
                  Entity currentTarget = this.getToEntity();
                  if (currentTarget != null && !(currentTarget instanceof PortalEntity) && this.postHitChainDelayTicks > 0) {
                     this.postHitChainDelayTicks--;
                  } else if (currentTarget instanceof PortalEntity portalEntity) {
                     this.markTouched(portalEntity);
                     if (this.createChainThroughPortal(livingCreator, portalEntity)) {
                        this.hasChained = true;
                     } else {
                        this.setRetracting(true);
                     }
                  } else {
                     PortalEntity orderedPortal = this.findNextOrderedPortal(
                        livingCreator, this.m_20182_(), 64.0, this.getActivePortalGroupUUID(), this.getLastPortalOrder()
                     );
                     if (orderedPortal != null) {
                        this.createChainToPortal(orderedPortal);
                        this.hasChained = true;
                     } else {
                        PortalEntity closestPortal = this.findClosestUsablePortal(livingCreator, this.m_20182_(), 64.0, null);
                        if (closestPortal != null) {
                           this.createChainToPortal(closestPortal);
                           this.hasChained = true;
                        } else {
                           Entity closestValid = null;

                           for (Entity candidate : this.m_9236_().m_45976_(LivingEntity.class, this.m_20191_().m_82400_(12.0))) {
                              if (!candidate.equals(creator)
                                 && !this.hasTouched(candidate)
                                 && this.isValidTarget(livingCreator, candidate)
                                 && this.hasLineOfSightTo(candidate)
                                 && (
                                    closestValid == null
                                       || this.m_20182_().m_82554_(targetCenter(candidate)) < this.m_20182_().m_82554_(targetCenter(closestValid))
                                 )) {
                                 closestValid = candidate;
                              }
                           }

                           if (closestValid != null) {
                              this.createChain(closestValid);
                              this.hasChained = true;
                           } else {
                              this.setRetracting(true);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean createChainThroughPortal(LivingEntity livingCreator, PortalEntity entrancePortal) {
      PortalEntity exitPortal = entrancePortal.getLinkedPortal();
      boolean hasExitPortal = exitPortal != null && !exitPortal.m_213877_();
      PortalEntity chainOriginPortal = hasExitPortal ? exitPortal : entrancePortal;
      this.markTouched(entrancePortal);
      if (hasExitPortal) {
         this.markTouched(exitPortal);
      }

      Vec3 chainOriginCenter = chainOriginPortal.getSnakeBladeAnchor();
      Entity closestValid = this.findClosestValidTargetNear(livingCreator, chainOriginCenter, 14.0);
      if (closestValid != null) {
         this.createChainFromPortalExit(chainOriginPortal, closestValid);
         return true;
      } else {
         UUID portalGroup = chainOriginPortal.getPortalGroupUUID();
         if (portalGroup == null) {
            portalGroup = entrancePortal.getPortalGroupUUID();
         }

         if (portalGroup == null) {
            portalGroup = this.getActivePortalGroupUUID();
         }

         int lastPortalOrder = hasExitPortal ? Math.max(entrancePortal.getPortalOrder(), exitPortal.getPortalOrder()) : entrancePortal.getPortalOrder();
         PortalEntity nextPortal = this.findNextOrderedPortal(livingCreator, chainOriginCenter, 64.0, portalGroup, lastPortalOrder);
         if (nextPortal != null) {
            this.createChainFromPortalExit(chainOriginPortal, nextPortal);
            return true;
         } else {
            nextPortal = this.findClosestUsablePortal(livingCreator, chainOriginCenter, 64.0, chainOriginPortal);
            if (nextPortal != null) {
               this.createChainFromPortalExit(chainOriginPortal, nextPortal);
               return true;
            } else {
               return false;
            }
         }
      }
   }

   private Entity findClosestValidTargetNear(LivingEntity livingCreator, Vec3 center, double radius) {
      Entity closestValid = null;
      AABB searchBox = new AABB(center, center).m_82400_(radius);

      for (Entity candidate : this.m_9236_().m_45976_(LivingEntity.class, searchBox)) {
         if (!candidate.equals(livingCreator)
            && !this.hasTouched(candidate)
            && this.isValidTarget(livingCreator, candidate)
            && this.hasLineOfSightFrom(center, candidate)
            && (closestValid == null || center.m_82554_(targetCenter(candidate)) < center.m_82554_(targetCenter(closestValid)))) {
            closestValid = candidate;
         }
      }

      return closestValid;
   }

   private PortalEntity findNextOrderedPortal(LivingEntity livingCreator, Vec3 center, double radius, UUID portalGroup, int lastPortalOrder) {
      if (portalGroup == null) {
         return null;
      } else {
         PortalEntity bestPortal = null;
         AABB searchBox = new AABB(center, center).m_82400_(radius);

         for (PortalEntity portalEntity : this.m_9236_().m_45976_(PortalEntity.class, searchBox)) {
            if (!this.hasTouched(portalEntity)
               && !portalEntity.m_213877_()
               && portalGroup.equals(portalEntity.getPortalGroupUUID())
               && portalEntity.getPortalOrder() > lastPortalOrder) {
               UUID ownerUuid = portalEntity.getOwnerUUID();
               if (HerobrinePortalCombatUtil.canUsePortalOwnedBy(livingCreator, ownerUuid)
                  && (
                     bestPortal == null
                        || portalEntity.getPortalOrder() < bestPortal.getPortalOrder()
                        || portalEntity.getPortalOrder() == bestPortal.getPortalOrder()
                           && center.m_82554_(portalEntity.m_20182_()) < center.m_82554_(bestPortal.m_20182_())
                  )) {
                  bestPortal = portalEntity;
               }
            }
         }

         return bestPortal;
      }
   }

   private PortalEntity findClosestUsablePortal(LivingEntity livingCreator, Vec3 center, double radius, PortalEntity excludedPortal) {
      PortalEntity closestPortal = null;
      AABB searchBox = new AABB(center, center).m_82400_(radius);

      for (PortalEntity portalEntity : this.m_9236_().m_45976_(PortalEntity.class, searchBox)) {
         if (portalEntity != excludedPortal && !this.hasTouched(portalEntity) && !portalEntity.m_213877_()) {
            UUID ownerUuid = portalEntity.getOwnerUUID();
            if (HerobrinePortalCombatUtil.canUsePortalOwnedBy(livingCreator, ownerUuid)
               && (closestPortal == null || center.m_82554_(portalEntity.m_20182_()) < center.m_82554_(closestPortal.m_20182_()))) {
               closestPortal = portalEntity;
            }
         }
      }

      return closestPortal;
   }

   private void applyVelocity() {
      Vec3 vel = this.m_20184_();
      double x = this.m_20185_() + vel.f_82479_;
      double y = this.m_20186_() + vel.f_82480_;
      double z = this.m_20189_() + vel.f_82481_;
      this.m_20256_(vel.m_82490_(0.99F));
      this.m_6034_(x, y, z);
   }

   private boolean hasTouched(Entity entity) {
      if (entity == null) {
         return false;
      } else {
         UUID uuid = entity.m_20148_();

         for (Entity touched : this.previouslyTouched) {
            if (touched != null && touched.m_20148_().equals(uuid)) {
               return true;
            }
         }

         return false;
      }
   }

   private void markTouched(Entity entity) {
      if (entity != null && !this.hasTouched(entity)) {
         this.previouslyTouched.add(entity);
      }
   }

   private UUID getActivePortalGroupUUID() {
      return (UUID)((Optional)this.f_19804_.m_135370_(PORTAL_GROUP_ID)).orElse(null);
   }

   private int getLastPortalOrder() {
      return (Integer)this.f_19804_.m_135370_(LAST_PORTAL_ORDER);
   }

   private void setPortalChainState(UUID portalGroupUuid, int lastPortalOrder) {
      this.f_19804_.m_135381_(PORTAL_GROUP_ID, Optional.ofNullable(portalGroupUuid));
      this.f_19804_.m_135381_(LAST_PORTAL_ORDER, lastPortalOrder);
   }

   private void copyPortalChainState(SnakeBladeEntity child) {
      child.setPortalChainState(this.getActivePortalGroupUUID(), this.getLastPortalOrder());
   }

   private boolean isPortalChainMode() {
      if (this.getActivePortalGroupUUID() == null && !(this.getToEntity() instanceof PortalEntity)) {
         for (Entity touched : this.previouslyTouched) {
            if (touched instanceof PortalEntity) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   private static Vec3 targetCenter(Entity entity) {
      return entity instanceof PortalEntity portalEntity
         ? portalEntity.getSnakeBladeAnchor()
         : new Vec3(entity.m_20185_(), entity.m_20186_() + (double)entity.m_20206_() * 0.5 - 1.0, entity.m_20189_());
   }

   private boolean isValidTarget(LivingEntity creator, Entity entity) {
      if (entity instanceof LivingEntity && !entity.m_5833_()) {
         if (entity instanceof Player player && player.m_7500_()) {
            return false;
         }

         if (HerobrinePortalCombatUtil.isHerobrineSide(creator) && HerobrinePortalCombatUtil.isHerobrineSide(entity)) {
            return false;
         } else {
            return creator.m_7307_(entity) || entity.m_7307_(creator) || !(entity instanceof Mob) && !(entity instanceof Player)
               ? creator.m_21214_() != null && creator.m_21214_().m_20148_().equals(entity.m_20148_())
                  || creator.m_21188_() != null && creator.m_21188_().m_20148_().equals(entity.m_20148_())
               : true;
         }
      } else {
         return false;
      }
   }

   private boolean hasLineOfSightTo(Entity target) {
      if (target.m_9236_() != this.m_9236_()) {
         return false;
      } else {
         Vec3 from = new Vec3(this.m_20185_(), this.m_20188_(), this.m_20189_());
         Vec3 to = targetCenter(target);
         return to.m_82554_(from) > 128.0 ? false : this.m_9236_().m_45547_(new ClipContext(from, to, Block.COLLIDER, Fluid.NONE, this)).m_6662_() == Type.MISS;
      }
   }

   private boolean hasLineOfSightFrom(Vec3 from, Entity target) {
      if (target.m_9236_() != this.m_9236_()) {
         return false;
      } else {
         Vec3 to = targetCenter(target);
         return to.m_82554_(from) > 128.0 ? false : this.m_9236_().m_45547_(new ClipContext(from, to, Block.COLLIDER, Fluid.NONE, this)).m_6662_() == Type.MISS;
      }
   }

   private void updateLastFragment(SnakeBladeEntity lastSnakeBladeEntity) {
      Entity creator = this.getCreatorEntity();
      if (creator == null) {
         UUID uuid = this.getCreatorEntityUUID();
         if (uuid != null) {
            creator = this.m_9236_().m_46003_(uuid);
         }
      }

      if (creator instanceof LivingEntity livingCreator) {
         DemoniacVoltageReaverItem.setLastFragment(livingCreator, lastSnakeBladeEntity);
      }
   }

   private void createChain(Entity nextTarget) {
      this.f_19804_.m_135381_(HAS_BLADE, false);
      SnakeBladeEntity child = (SnakeBladeEntity)((EntityType)AnnoyingVillagersModEntities.SNAKE_BLADE.get()).m_20615_(this.m_9236_());
      if (child != null) {
         if (this.isEnchanted()) {
            child.setEnchanted(true);
         }

         child.previouslyTouched.addAll(this.previouslyTouched);
         this.copyPortalChainState(child);
         child.markTouched(nextTarget);
         child.setCreatorEntityUUID(this.getCreatorEntityUUID());
         child.setFromEntityID(this.m_19879_());
         child.setToEntityID(nextTarget.m_19879_());
         Vec3 nextTargetCenter = targetCenter(nextTarget);
         child.m_6034_(nextTargetCenter.f_82479_, nextTargetCenter.f_82480_, nextTargetCenter.f_82481_);
         child.setTargetsHit(this.getTargetsHit() + 1);
         this.updateLastFragment(child);
         this.m_9236_().m_7967_(child);
      }
   }

   private void createChainToPortal(PortalEntity nextPortal) {
      this.f_19804_.m_135381_(HAS_BLADE, false);
      SnakeBladeEntity child = (SnakeBladeEntity)((EntityType)AnnoyingVillagersModEntities.SNAKE_BLADE.get()).m_20615_(this.m_9236_());
      if (child != null) {
         if (this.isEnchanted()) {
            child.setEnchanted(true);
         }

         child.previouslyTouched.addAll(this.previouslyTouched);
         this.copyPortalChainState(child);
         if (child.getActivePortalGroupUUID() == null && nextPortal.getPortalGroupUUID() != null) {
            child.setPortalChainState(nextPortal.getPortalGroupUUID(), nextPortal.getPortalOrder() - 1);
         }

         child.markTouched(nextPortal);
         child.setCreatorEntityUUID(this.getCreatorEntityUUID());
         child.setFromEntityID(this.m_19879_());
         child.setToEntityID(nextPortal.m_19879_());
         Vec3 portalCenter = nextPortal.getSnakeBladeAnchor();
         child.m_6034_(portalCenter.f_82479_, portalCenter.f_82480_, portalCenter.f_82481_);
         child.setTargetsHit(this.getTargetsHit() + 1);
         this.updateLastFragment(child);
         this.m_9236_().m_7967_(child);
      }
   }

   private void createChainFromPortalExit(PortalEntity exitPortal, Entity nextTarget) {
      this.f_19804_.m_135381_(HAS_BLADE, false);
      SnakeBladeEntity child = (SnakeBladeEntity)((EntityType)AnnoyingVillagersModEntities.SNAKE_BLADE.get()).m_20615_(this.m_9236_());
      if (child != null) {
         if (this.isEnchanted()) {
            child.setEnchanted(true);
         }

         child.previouslyTouched.addAll(this.previouslyTouched);
         UUID portalGroup = exitPortal.getPortalGroupUUID();
         if (portalGroup == null) {
            portalGroup = this.getActivePortalGroupUUID();
         }

         int portalOrder = exitPortal.getPortalOrder() >= 0 ? exitPortal.getPortalOrder() : this.getLastPortalOrder();
         child.setPortalChainState(portalGroup, portalOrder);
         child.markTouched(exitPortal);
         child.markTouched(nextTarget);
         child.setCreatorEntityUUID(this.getCreatorEntityUUID());
         child.setFromEntityID(this.m_19879_());
         child.setRenderFromEntityID(exitPortal.m_19879_());
         child.setToEntityID(nextTarget.m_19879_());
         Vec3 nextTargetCenter = targetCenter(nextTarget);
         child.m_6034_(nextTargetCenter.f_82479_, nextTargetCenter.f_82480_, nextTargetCenter.f_82481_);
         child.setTargetsHit(this.getTargetsHit() + 1);
         this.updateLastFragment(child);
         this.m_9236_().m_7967_(child);
      }
   }

   private void createChainGuard(String nextDirection) {
      this.f_19804_.m_135381_(HAS_BLADE, false);
      SnakeBladeEntity child = (SnakeBladeEntity)((EntityType)AnnoyingVillagersModEntities.SNAKE_BLADE.get()).m_20615_(this.m_9236_());
      if (child != null) {
         if (this.isEnchanted()) {
            child.setEnchanted(true);
         }

         child.previouslyTouched.addAll(this.previouslyTouched);
         this.copyPortalChainState(child);
         child.setCreatorEntityUUID(this.getCreatorEntityUUID());
         child.setFromEntityID(this.m_19879_());
         child.setToEntityID(-1);
         child.setTargetsHit(this.getTargetsHit() + 1);
         child.setGuardDirection(nextDirection);
         if (this.getCreatorEntity() instanceof LivingEntity living) {
            Vec3 p = DemoniacVoltageReaverItem.guardTargetFor(living, nextDirection);
            child.m_6034_(p.f_82479_, p.f_82480_, p.f_82481_);
         } else {
            child.m_20359_(this);
         }

         this.updateLastFragment(child);
         this.m_9236_().m_7967_(child);
      }
   }

   public boolean m_6469_(@NotNull DamageSource pSource, float amount) {
      if (!this.m_9236_().m_5776_() && this.m_9236_() instanceof ServerLevel serverLevel && !pSource.m_276093_(DamageTypes.f_268612_)) {
         EpicfightUtil.damageBlocked(pSource, this, serverLevel);
      }

      return false;
   }

   private static String nextGuardDirection(String current) {
      if ("forward_left".equalsIgnoreCase(current)) {
         return "forward_right";
      } else if ("forward_right".equalsIgnoreCase(current)) {
         return "backward_right";
      } else {
         return "backward_right".equalsIgnoreCase(current) ? "backward_left" : "forward_left";
      }
   }

   public UUID getCreatorEntityUUID() {
      return (UUID)((Optional)this.f_19804_.m_135370_(CREATOR_ID)).orElse(null);
   }

   public void setCreatorEntityUUID(UUID id) {
      this.f_19804_.m_135381_(CREATOR_ID, Optional.ofNullable(id));
   }

   public Entity getCreatorEntity() {
      UUID uuid = this.getCreatorEntityUUID();
      return uuid != null && !this.m_9236_().f_46443_ && this.m_9236_() instanceof ServerLevel serverLevel ? serverLevel.m_8791_(uuid) : null;
   }

   public int getFromEntityID() {
      return (Integer)this.f_19804_.m_135370_(FROM_ID);
   }

   public void setFromEntityID(int id) {
      this.f_19804_.m_135381_(FROM_ID, id);
   }

   public Entity getFromEntity() {
      int id = this.getFromEntityID();
      return id == -1 ? null : this.m_9236_().m_6815_(id);
   }

   public int getRenderFromEntityID() {
      return (Integer)this.f_19804_.m_135370_(RENDER_FROM_ID);
   }

   public void setRenderFromEntityID(int id) {
      this.f_19804_.m_135381_(RENDER_FROM_ID, id);
   }

   public Entity getRenderFromEntity() {
      int id = this.getRenderFromEntityID();
      return id == -1 ? this.getFromEntity() : this.m_9236_().m_6815_(id);
   }

   public int getToEntityID() {
      return (Integer)this.f_19804_.m_135370_(CURRENT_TARGET_ID);
   }

   public void setToEntityID(int id) {
      this.f_19804_.m_135381_(CURRENT_TARGET_ID, id);
   }

   public Entity getToEntity() {
      int id = this.getToEntityID();
      return id == -1 ? null : this.m_9236_().m_6815_(id);
   }

   public int getTargetsHit() {
      return (Integer)this.f_19804_.m_135370_(TARGET_COUNT);
   }

   public void setTargetsHit(int count) {
      this.f_19804_.m_135381_(TARGET_COUNT, count);
   }

   public float getProgress() {
      return (Float)this.f_19804_.m_135370_(PROGRESS);
   }

   public void setProgress(float progress) {
      this.f_19804_.m_135381_(PROGRESS, progress);
   }

   public boolean isRetracting() {
      return (Boolean)this.f_19804_.m_135370_(RETRACTING);
   }

   public void setRetracting(boolean retract) {
      this.f_19804_.m_135381_(RETRACTING, retract);
   }

   public boolean hasBlade() {
      return (Boolean)this.f_19804_.m_135370_(HAS_BLADE);
   }

   protected void m_7378_(@NotNull CompoundTag tag) {
   }

   protected void m_7380_(@NotNull CompoundTag tag) {
   }

   public boolean isCreator(Entity mob) {
      UUID creatorUuid = this.getCreatorEntityUUID();
      return creatorUuid != null && mob.m_20148_().equals(creatorUuid);
   }
}
