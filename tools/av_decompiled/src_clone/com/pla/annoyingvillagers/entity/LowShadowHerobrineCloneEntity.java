package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.AnnoyingVillagers;
import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.gameasset.AVAnimations;
import com.pla.annoyingvillagers.gameasset.AnimsSculkSteve;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModBlocks;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModParticleTypes;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.network.ClientboundHerobrinePortalFx;
import com.pla.annoyingvillagers.spawnhandler.HerobrineMobData;
import com.pla.annoyingvillagers.task.DelayedTask;
import com.pla.annoyingvillagers.util.CombatBehaviour;
import com.pla.annoyingvillagers.util.CommonGoals;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import com.pla.annoyingvillagers.util.HerobrineUtil;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import se.gory_moon.player_mobs.utils.NameManager;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.effect.EpicFightMobEffects;

public class LowShadowHerobrineCloneEntity extends Monster {
   private boolean summoned = false;
   private boolean initialSpawn = true;
   private EliteHerobrineKnockedEntity protectEntity;
   private UUID protectUUID;
   private boolean autoKill = false;
   private HerobrineMob possessedByEntity;
   private UUID possessedByUuid;
   private boolean bound = false;
   private boolean sacrificing = false;
   private boolean healing = false;
   private boolean forEscaping = false;
   private final LivingEntityPatch<?> livingentitypatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(this, LivingEntityPatch.class);
   boolean renderPortal = false;

   public boolean isHealing() {
      return this.healing;
   }

   public boolean isSacrificing() {
      return this.sacrificing;
   }

   public void setForEscaping(boolean forEscaping) {
      this.forEscaping = forEscaping;
   }

   public HerobrineMob getPossessedByEntity() {
      return this.possessedByEntity;
   }

   public void setProtectUUID(UUID protectUUID) {
      this.protectUUID = protectUUID;
   }

   public void setProtectEntity(EliteHerobrineKnockedEntity protectEntity) {
      this.protectEntity = protectEntity;
   }

   public void setAutoKill(boolean autoKill) {
      this.autoKill = autoKill;
   }

   public void setPossessedByUuid(UUID possessedByUuid) {
      this.possessedByUuid = possessedByUuid;
   }

   public void setPossessedByEntity(HerobrineMob possessedByEntity) {
      if (!isValidPossessedMaster(possessedByEntity)) {
         this.possessedByEntity = null;
         this.possessedByUuid = null;
      } else {
         this.possessedByEntity = possessedByEntity;
      }
   }

   private static boolean isValidPossessedMaster(@Nullable Entity entity) {
      return entity instanceof HerobrineMob && !(entity instanceof TransporterHerobrineCloneEntity) && !(entity instanceof HerobrineGregEntity);
   }

   public void setSacrificing(boolean sacrificing) {
      this.sacrificing = sacrificing;
   }

   public void setHealing(boolean healing) {
      this.healing = healing;
   }

   public boolean isSummoned() {
      return this.summoned;
   }

   public void setSummoned(boolean summoned) {
      this.summoned = summoned;
   }

   public void setRenderPortal(boolean renderPortal) {
      this.renderPortal = renderPortal;
   }

   public void setInitialSpawn(boolean initialSpawn) {
      this.initialSpawn = initialSpawn;
   }

   public LowShadowHerobrineCloneEntity(SpawnEntity spawnEntity, Level level) {
      this((EntityType<LowShadowHerobrineCloneEntity>)AnnoyingVillagersModEntities.LOW_SHADOW_HEROBRINE_CLONE.get(), level);
   }

   public LowShadowHerobrineCloneEntity(EntityType<LowShadowHerobrineCloneEntity> entitytype, Level level) {
      super(entitytype, level);
      this.m_274367_(2.0F);
      this.f_21364_ = 50;
      this.m_21557_(false);
      this.m_20340_(false);
      this.m_21530_();
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   protected void m_8099_() {
      super.m_8099_();
      this.f_21345_
         .m_25352_(
            1,
            new Goal() {
               public boolean m_8036_() {
                  return LowShadowHerobrineCloneEntity.this.protectEntity != null
                     && LowShadowHerobrineCloneEntity.this.protectEntity.m_6084_()
                     && LowShadowHerobrineCloneEntity.this.m_20270_(LowShadowHerobrineCloneEntity.this.protectEntity) > 9.0F;
               }

               public void m_8037_() {
                  if (LowShadowHerobrineCloneEntity.this.protectEntity != null && LowShadowHerobrineCloneEntity.this.protectEntity.m_6084_()) {
                     LowShadowHerobrineCloneEntity.this.m_21573_().m_5624_(LowShadowHerobrineCloneEntity.this.protectEntity, 2.0);
                     LowShadowHerobrineCloneEntity.this.m_21563_().m_24960_(LowShadowHerobrineCloneEntity.this.protectEntity, 30.0F, 30.0F);
                     if (LowShadowHerobrineCloneEntity.this.m_20280_(LowShadowHerobrineCloneEntity.this.protectEntity) > 10.0) {
                        if (LowShadowHerobrineCloneEntity.this.m_21573_().m_26571_()) {
                           LowShadowHerobrineCloneEntity.this.m_21573_().m_5624_(LowShadowHerobrineCloneEntity.this.protectEntity, 2.0);
                        }
                     } else {
                        LowShadowHerobrineCloneEntity.this.m_21573_().m_26573_();
                     }
                  }
               }

               public boolean m_8045_() {
                  return LowShadowHerobrineCloneEntity.this.protectEntity != null
                     && LowShadowHerobrineCloneEntity.this.protectEntity.m_6084_()
                     && (double)LowShadowHerobrineCloneEntity.this.m_20270_(LowShadowHerobrineCloneEntity.this.protectEntity) > 50.0;
               }
            }
         );
      this.f_21345_
         .m_25352_(
            1,
            new Goal() {
               public boolean m_8036_() {
                  return LowShadowHerobrineCloneEntity.this.possessedByEntity != null
                     && LowShadowHerobrineCloneEntity.this.possessedByEntity.m_6084_()
                     && LowShadowHerobrineCloneEntity.this.m_20270_(LowShadowHerobrineCloneEntity.this.possessedByEntity) > 18.0F;
               }

               public void m_8037_() {
                  if (LowShadowHerobrineCloneEntity.this.possessedByEntity != null && LowShadowHerobrineCloneEntity.this.possessedByEntity.m_6084_()) {
                     LowShadowHerobrineCloneEntity.this.m_21573_().m_5624_(LowShadowHerobrineCloneEntity.this.possessedByEntity, 2.0);
                     LowShadowHerobrineCloneEntity.this.m_21563_().m_24960_(LowShadowHerobrineCloneEntity.this.possessedByEntity, 30.0F, 30.0F);
                     if (LowShadowHerobrineCloneEntity.this.m_20280_(LowShadowHerobrineCloneEntity.this.possessedByEntity) > 20.0) {
                        if (LowShadowHerobrineCloneEntity.this.m_21573_().m_26571_()) {
                           LowShadowHerobrineCloneEntity.this.m_21573_().m_5624_(LowShadowHerobrineCloneEntity.this.possessedByEntity, 2.0);
                        }
                     } else {
                        LowShadowHerobrineCloneEntity.this.m_21573_().m_26573_();
                     }
                  }
               }

               public boolean m_8045_() {
                  return LowShadowHerobrineCloneEntity.this.possessedByEntity != null
                     && LowShadowHerobrineCloneEntity.this.possessedByEntity.m_6084_()
                     && (double)LowShadowHerobrineCloneEntity.this.m_20270_(LowShadowHerobrineCloneEntity.this.possessedByEntity) > 50.0;
               }
            }
         );
      CommonGoals.registerGoalForHostileNpc(this);
   }

   @NotNull
   public MobType m_6336_() {
      return MobType.f_21641_;
   }

   public boolean m_6785_(double d0) {
      return false;
   }

   public double m_6049_() {
      return -0.35;
   }

   @NotNull
   public SoundEvent m_7975_(@NotNull DamageSource damageSource) {
      return Objects.requireNonNull(
         (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "entity.generic.hurt"))
      );
   }

   @NotNull
   public SoundEvent m_5592_() {
      return Objects.requireNonNull(
         (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "entity.generic.death"))
      );
   }

   public boolean m_6469_(@NotNull DamageSource damageSource, float f) {
      if (this.sacrificing || this.healing) {
         if (new Random().nextBoolean() && this.m_9236_() instanceof ServerLevel serverLevel) {
            EpicfightUtil.damageBlocked(damageSource, this, serverLevel);
            return false;
         } else {
            float health = this.m_21223_();
            if (!(health - f <= 5.0F) || !this.healing && !this.sacrificing) {
               return super.m_6469_(damageSource, f / 2.0F);
            } else {
               this.protectEntity = null;
               this.protectUUID = null;
               this.autoKill = true;
               this.m_6074_();
               return false;
            }
         }
      } else if (damageSource.m_276093_(DamageTypes.f_268671_)) {
         return false;
      } else if (damageSource.m_276093_(DamageTypes.f_268585_)) {
         return false;
      } else if (damageSource.m_276093_(DamageTypes.f_268493_)) {
         return false;
      } else if (damageSource.m_276093_(DamageTypes.f_268722_)) {
         return false;
      } else if (damageSource.m_276093_(DamageTypes.f_268641_)) {
         return false;
      } else if (damageSource.m_276093_(DamageTypes.f_268482_)) {
         return false;
      } else {
         float health = this.m_21223_();
         if (!(health - f <= 5.0F) || !this.healing && !this.sacrificing && !this.forEscaping) {
            return super.m_6469_(damageSource, f / 2.0F);
         } else {
            this.protectEntity = null;
            this.protectUUID = null;
            this.autoKill = true;
            this.healing = false;
            this.sacrificing = false;
            this.forEscaping = false;
            this.m_6074_();
            return false;
         }
      }
   }

   public void m_6667_(@NotNull DamageSource damagesource) {
      super.m_6667_(damagesource);
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         if (!this.autoKill) {
            InfectedPlayerNpcEntity corpse = new InfectedPlayerNpcEntity(
               (EntityType<? extends InfectedPlayerNpcEntity>)AnnoyingVillagersModEntities.INFECTED_PLAYER_NPC.get(), serverLevel
            );
            corpse.m_7678_(this.m_20185_(), this.m_20186_(), this.m_20189_(), this.m_146908_(), this.m_146909_());
            String killedName = this.getPersistentData().m_128461_("killed_name");
            corpse.getPersistentData().m_128359_("possessed_by", "low_shadow_herobrine_clone");
            if (killedName.isEmpty()) {
               killedName = String.valueOf(NameManager.INSTANCE.getRandomName());
            }

            corpse.setUsername(killedName);
            corpse.m_6593_(Component.m_237113_(killedName));
            corpse.m_6518_(serverLevel, serverLevel.m_6436_(this.m_20183_()), MobSpawnType.MOB_SUMMONED, null, null);
            this.m_6842_(true);
            this.m_142687_(RemovalReason.KILLED);
            corpse.m_8061_(EquipmentSlot.HEAD, this.m_6844_(EquipmentSlot.HEAD).m_41777_());
            corpse.m_8061_(EquipmentSlot.CHEST, this.m_6844_(EquipmentSlot.CHEST).m_41777_());
            corpse.m_8061_(EquipmentSlot.LEGS, this.m_6844_(EquipmentSlot.LEGS).m_41777_());
            corpse.m_8061_(EquipmentSlot.FEET, this.m_6844_(EquipmentSlot.FEET).m_41777_());
            serverLevel.m_7967_(corpse);
         } else if (this.healing || this.sacrificing) {
            this.m_6074_();
         }

         ItemStack itemstack = this.m_21205_();
         ItemEntity itemEntity = new ItemEntity(serverLevel, this.m_20185_(), this.m_20186_() + 1.0, this.m_20189_(), itemstack);
         itemEntity.m_32010_(10);
         serverLevel.m_7967_(itemEntity);
         itemstack = this.m_21206_();
         itemEntity = new ItemEntity(serverLevel, this.m_20185_(), this.m_20186_() + 1.0, this.m_20189_(), itemstack);
         itemEntity.m_32010_(10);
         serverLevel.m_7967_(itemEntity);
      }
   }

   public SpawnGroupData m_6518_(
      @NotNull ServerLevelAccessor serverLevelAccessor,
      @NotNull DifficultyInstance difficultyInstance,
      @NotNull MobSpawnType mobSpawnType,
      @Nullable SpawnGroupData spawnGroupData,
      @Nullable CompoundTag compoundTag
   ) {
      if (mobSpawnType == MobSpawnType.NATURAL || mobSpawnType == MobSpawnType.CHUNK_GENERATION) {
         ServerLevel serverLevel = serverLevelAccessor.m_6018_();
         HerobrineMobData herobrineMobData = HerobrineMobData.get(serverLevel);
         if (!herobrineMobData.tryClaim(serverLevel, this.m_20148_())) {
            this.m_146870_();
            return null;
         }

         BlockPos blockPos = this.m_20097_();
         int surfaceY = serverLevel.m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, blockPos).m_123342_();
         BlockPos spawnPos = new BlockPos(blockPos.m_123341_(), surfaceY, blockPos.m_123343_());
         this.m_20035_(spawnPos, this.m_146908_(), this.m_146909_());
      }

      HerobrineUtil.initialSpawn(serverLevelAccessor, this, 0, mobSpawnType);
      return super.m_6518_(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
   }

   public void m_8119_() {
      super.m_8119_();
      if (!this.m_9236_().f_46443_) {
         if (this.f_19797_ == 1) {
            if (this.renderPortal) {
               AnnoyingVillagers.PACKET_HANDLER
                  .send(
                     PacketDistributor.TRACKING_ENTITY.with(() -> this), new ClientboundHerobrinePortalFx(this.m_20097_().m_252807_().m_82520_(0.0, 1.5, 0.0))
                  );
               this.renderPortal = false;
            }

            if (this.initialSpawn) {
               if (this.summoned) {
                  this.m_21557_(true);
               }

               LivingEntityPatch<?> livingentitypatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(this, LivingEntityPatch.class);
               if (livingentitypatch != null && !this.m_9236_().m_5776_()) {
                  livingentitypatch.playAnimationSynchronized(AnimsSculkSteve.PLAYER_HEROBRINE_POSSESSION, 0.0F);
               }

               this.initialSpawn = false;
            }

            if (this.forEscaping && this.m_9236_() instanceof ServerLevel serverLevel) {
               this.m_21557_(false);
               final Entity entity = this;
               new DelayedTask(10) {
                  @Override
                  public void run() {
                     HerobrineUtil.spawnObsidianPatternAtBody(serverLevel, entity, ((Block)AnnoyingVillagersModBlocks.CRYING_OBSIDIAN_BLOCK.get()).m_49966_());
                     entity.m_146870_();
                  }
               };
            }
         }

         if (this.protectEntity == null && this.protectUUID != null) {
            if (((ServerLevel)this.m_9236_()).m_8791_(this.protectUUID) instanceof EliteHerobrineKnockedEntity eliteHerobrineKnockedEntity) {
               this.protectEntity = eliteHerobrineKnockedEntity;
            } else {
               this.protectEntity = null;
            }
         }

         if (this.protectEntity != null && !this.protectEntity.m_6084_()) {
            this.protectEntity = null;
            this.protectUUID = null;
            this.autoKill = true;
            this.m_6074_();
         }

         if (this.possessedByEntity == null && this.possessedByUuid != null) {
            Entity entity = ((ServerLevel)this.m_9236_()).m_8791_(this.possessedByUuid);
            if (isValidPossessedMaster(entity) && entity instanceof HerobrineMob herobrineMob) {
               this.possessedByEntity = herobrineMob;
            } else {
               this.possessedByEntity = null;
               this.possessedByUuid = null;
            }
         }

         if (!this.forEscaping
            && !this.bound
            && this.possessedByEntity != null
            && this.possessedByEntity.m_6084_()
            && (!this.possessedByEntity.isSacrificing() || !this.possessedByEntity.isHealing())
            && this.possessedByEntity.getSacrificingAnimationCooldown() == 0
            && this.possessedByEntity.isAvailableSlot()
            && this.possessedByEntity.boundPossessed(this)) {
            this.bound = true;
         }

         if (this.possessedByEntity != null && !this.possessedByEntity.m_6084_()) {
            AABB area = new AABB(this.m_20183_()).m_82400_(60.0);
            List<Entity> nearby = this.m_9236_().m_6249_(this, area, entity -> entity instanceof EliteHerobrineKnockedEntity);
            if (!nearby.isEmpty()) {
               Entity entity = nearby.get(0);
               if (entity instanceof EliteHerobrineKnockedEntity eliteHerobrineKnockedEntity) {
                  this.protectEntity = eliteHerobrineKnockedEntity;
                  this.protectUUID = eliteHerobrineKnockedEntity.m_20148_();
               } else {
                  this.possessedByEntity = null;
                  this.possessedByUuid = null;
                  this.autoKill = true;
                  this.m_6074_();
               }
            } else {
               this.possessedByEntity = null;
               this.possessedByUuid = null;
               this.autoKill = true;
               this.m_6074_();
            }
         }

         if (this.sacrificing || this.healing) {
            if (this.m_21223_() <= 2.0F) {
               this.sacrificing = false;
               this.healing = false;
               this.autoKill = true;
               this.m_6074_();
            }

            this.m_7292_(new MobEffectInstance((MobEffect)EpicFightMobEffects.STUN_IMMUNITY.get(), 1, 3, false, false));
            if (this.livingentitypatch != null && !this.m_21224_() && this.m_6084_()) {
               if (this.sacrificing) {
                  this.livingentitypatch.playAnimationSynchronized(AnimsSculkSteve.HEROBRINE_ASSISTANCE, 0.0F);
               } else if (this.healing) {
                  this.livingentitypatch.playAnimationSynchronized(AnimsSculkSteve.HEROBRINE_SACRIFICING, 0.0F);
               }
            }

            if (this.f_19797_ % 140 == 0 && (double)this.possessedByEntity.m_21223_() < (double)this.possessedByEntity.m_21233_() * 0.8) {
               this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.HEROBRINE_UNDERSTOOD.get(), 0.5F, 1.0F);
            }

            if (this.f_19797_ % 20 == 0 && this.possessedByEntity != null) {
               if (this.possessedByEntity.m_21233_() == this.possessedByEntity.m_21223_()) {
                  this.sacrificing = false;
                  this.healing = false;
                  this.autoKill = true;
                  this.m_6074_();
               }

               if (this.m_21223_() <= 4.0F) {
                  this.sacrificing = false;
                  this.healing = false;
                  this.autoKill = true;
                  this.m_6074_();
               } else {
                  this.m_21153_(this.m_21223_() - 2.0F);
               }

               this.possessedByEntity.m_5634_(this.possessedByEntity.m_21233_() * 0.01F);
               if (this.healing) {
                  CombatBehaviour.forceLookAt(this, this.possessedByEntity, 60.0F, 60.0F);
               }
            }

            if (this.possessedByEntity != null && this.possessedByEntity.m_6084_()) {
               ServerLevel server = (ServerLevel)this.m_9236_();
               Vec3 from = null;
               if (this.sacrificing) {
                  from = getSacrificingArmPosition(this, new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolR);
               } else if (this.healing) {
                  from = getHealingArmPosition(this, new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolR);
               }

               if (from == null) {
                  return;
               }

               Vec3 to = this.possessedByEntity.m_146892_();
               AABB box = this.possessedByEntity.m_20191_().m_82400_(0.05);
               Vec3 end = box.m_82371_(from, to).orElse(to);
               Vec3 d = end.m_82546_(from);
               double len = d.m_82553_();
               if (len <= 1.0E-4) {
                  return;
               }

               Vec3 dir = d.m_82490_(1.0 / len);
               Vec3 any = Math.abs(dir.f_82480_) < 0.99 ? new Vec3(0.0, 1.0, 0.0) : new Vec3(1.0, 0.0, 0.0);
               Vec3 u = dir.m_82537_(any).m_82541_();
               Vec3 v = dir.m_82537_(u).m_82541_();
               int steps = Mth.m_14045_((int)(len * 6.0), 6, 72);
               double step = len / (double)steps;
               int stride = 4;
               int phase = (this.f_19797_ >> 1) % 4;
               RandomSource r = this.m_217043_();

               for (int i = phase; i <= steps; i += 4) {
                  if (!(r.m_188501_() < 0.7F)) {
                     double t = (double)i * step / len;
                     double R = 0.05 + 0.2 * t;
                     double ang = r.m_188500_() * (Math.PI * 2);
                     double rad = R * Math.sqrt(r.m_188500_());
                     Vec3 off = u.m_82490_(Math.cos(ang) * rad).m_82549_(v.m_82490_(Math.sin(ang) * rad));
                     Vec3 p = from.m_82549_(dir.m_82490_((double)i * step)).m_82549_(off);
                     double vx = dir.f_82479_ * 0.02 + off.f_82479_ * 0.1;
                     double vy = dir.f_82480_ * 0.02 + off.f_82480_ * 0.1;
                     double vz = dir.f_82481_ * 0.02 + off.f_82481_ * 0.1;
                     server.m_8767_((SimpleParticleType)AnnoyingVillagersModParticleTypes.LIGHT.get(), p.f_82479_, p.f_82480_, p.f_82481_, 1, vx, vy, vz, 0.0);
                  }
               }
            } else {
               this.sacrificing = false;
               this.healing = false;
               this.autoKill = true;
               this.m_6074_();
            }
         }

         if (this.forEscaping) {
            if (this.m_21223_() <= 2.0F) {
               this.forEscaping = false;
               this.autoKill = true;
               this.m_6074_();
            }

            this.m_7292_(new MobEffectInstance((MobEffect)EpicFightMobEffects.STUN_IMMUNITY.get(), 1, 3, false, false));
            if (this.livingentitypatch != null) {
               this.livingentitypatch.playAnimationSynchronized(AVAnimations.LOW_CLONE_ESCAPE, 0.0F);
            }
         }
      }
   }

   private static Vec3 getSacrificingArmPosition(Entity entity, Vec3f translation, Joint joint) {
      float handToTip = 0.6F;
      float yOffset = 0.6F;
      LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
      if (livingEntityPatch == null) {
         return null;
      } else {
         float interpolation = 0.0F;
         OpenMatrix4f m = livingEntityPatch.getArmature().getBoundTransformFor(livingEntityPatch.getAnimator().getPose(interpolation), joint);
         if (translation != null) {
            OpenMatrix4f tLocal = new OpenMatrix4f().translate(translation);
            OpenMatrix4f.mul(m, tLocal, m);
         }

         OpenMatrix4f tipOffset = new OpenMatrix4f().translate(new Vec3f(0.0F, 0.0F, -handToTip));
         OpenMatrix4f.mul(m, tipOffset, m);
         float yawRad = (float)(-Math.toRadians((double)(((LivingEntity)livingEntityPatch.getOriginal()).f_20884_ + 180.0F)));
         OpenMatrix4f worldYaw = new OpenMatrix4f().rotate(yawRad, new Vec3f(0.0F, 1.0F, 0.0F));
         OpenMatrix4f.mul(worldYaw, m, m);
         LivingEntity base = (LivingEntity)livingEntityPatch.getOriginal();
         return new Vec3(
            (double)m.m30 + base.m_20185_(),
            (double)m.m31 + (base.m_20186_() + (double)entity.m_20206_() / 1.8 - 1.0) + (double)yOffset,
            (double)m.m32 + base.m_20189_()
         );
      }
   }

   private static Vec3 getHealingArmPosition(Entity entity, Vec3f translation, Joint joint) {
      float handToTip = 1.2F;
      float yOffset = 0.0F;
      LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
      if (livingEntityPatch == null) {
         return null;
      } else {
         float interpolation = 0.0F;
         OpenMatrix4f m = livingEntityPatch.getArmature().getBoundTransformFor(livingEntityPatch.getAnimator().getPose(interpolation), joint);
         if (translation != null) {
            OpenMatrix4f tLocal = new OpenMatrix4f().translate(translation);
            OpenMatrix4f.mul(m, tLocal, m);
         }

         OpenMatrix4f tipOffset = new OpenMatrix4f().translate(new Vec3f(0.0F, 0.0F, -handToTip));
         OpenMatrix4f.mul(m, tipOffset, m);
         float yawRad = (float)(-Math.toRadians((double)(((LivingEntity)livingEntityPatch.getOriginal()).f_20884_ + 180.0F)));
         OpenMatrix4f worldYaw = new OpenMatrix4f().rotate(yawRad, new Vec3f(0.0F, 1.0F, 0.0F));
         OpenMatrix4f.mul(worldYaw, m, m);
         LivingEntity base = (LivingEntity)livingEntityPatch.getOriginal();
         return new Vec3(
            (double)m.m30 + base.m_20185_(),
            (double)m.m31 + (base.m_20186_() + (double)entity.m_20206_() / 1.8 - 1.0) + (double)yOffset,
            (double)m.m32 + base.m_20189_()
         );
      }
   }

   public void m_6075_() {
      super.m_6075_();
   }

   public static boolean canSpawn(
      EntityType<LowShadowHerobrineCloneEntity> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos position, RandomSource random
   ) {
      ServerLevel serverLevel = level.m_6018_();
      int passesDay = (int)(serverLevel.m_46467_() / 24000L);
      if (passesDay % 3 != 0) {
         return false;
      } else if (HerobrineMobData.get(serverLevel).isOccupied(serverLevel)) {
         return false;
      } else {
         return !serverLevel.m_46462_() ? false : Monster.m_219013_(entityType, level, spawnType, position, random);
      }
   }

   public void m_7378_(@NotNull CompoundTag pCompound) {
      super.m_7378_(pCompound);
      this.summoned = pCompound.m_128471_("Summoned");
      this.renderPortal = pCompound.m_128471_("RenderPortal");
      this.initialSpawn = pCompound.m_128471_("InitialSpawn");
      this.autoKill = pCompound.m_128471_("AutoKill");
      if (pCompound.m_128403_("ProtectUUID")) {
         this.protectUUID = pCompound.m_128342_("ProtectUUID");
      }

      if (pCompound.m_128403_("PossessedByUuid")) {
         this.possessedByUuid = pCompound.m_128342_("PossessedByUuid");
      }

      this.bound = pCompound.m_128471_("Bound");
      this.sacrificing = pCompound.m_128471_("Sacrificing");
      this.healing = pCompound.m_128471_("Healing");
      this.forEscaping = pCompound.m_128471_("ForEscaping");
   }

   public void m_7380_(@NotNull CompoundTag pCompound) {
      super.m_7380_(pCompound);
      pCompound.m_128379_("Summoned", this.summoned);
      pCompound.m_128379_("RenderPortal", this.renderPortal);
      pCompound.m_128379_("InitialSpawn", this.initialSpawn);
      pCompound.m_128379_("AutoKill", this.autoKill);
      if (this.protectUUID != null) {
         pCompound.m_128362_("ProtectUUID", this.protectUUID);
      }

      if (this.possessedByUuid != null) {
         pCompound.m_128362_("PossessedByUuid", this.possessedByUuid);
      }

      pCompound.m_128379_("Bound", this.bound);
      pCompound.m_128379_("Sacrificing", this.sacrificing);
      pCompound.m_128379_("Healing", this.healing);
      pCompound.m_128379_("ForEscaping", this.forEscaping);
   }

   public static Builder createAttributes() {
      Builder builder = Mob.m_21552_();
      builder = builder.m_22268_(Attributes.f_22279_, 0.3);
      builder = builder.m_22268_(Attributes.f_22276_, 40.0);
      builder = builder.m_22268_(Attributes.f_22284_, 25.0);
      builder = builder.m_22268_(Attributes.f_22281_, 0.0);
      return builder.m_22268_(Attributes.f_22277_, 24.0);
   }
}
