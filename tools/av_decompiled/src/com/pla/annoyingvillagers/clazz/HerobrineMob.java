package com.pla.annoyingvillagers.clazz;

import com.pla.annoyingvillagers.AnnoyingVillagers;
import com.pla.annoyingvillagers.blockentity.CryingObsidianBlockEntity;
import com.pla.annoyingvillagers.blockentity.ObsidianBlockEntity;
import com.pla.annoyingvillagers.blockentity.ShadowObsidianBlockEntity;
import com.pla.annoyingvillagers.combatbehaviour.CombatCommon;
import com.pla.annoyingvillagers.config.AnnoyingVillagersConfig;
import com.pla.annoyingvillagers.entity.AegisHerobrineEntity;
import com.pla.annoyingvillagers.entity.ArmoredHerobrineEntity;
import com.pla.annoyingvillagers.entity.EliteHerobrineKnockedEntity;
import com.pla.annoyingvillagers.entity.GlaiveHerobrineEntity;
import com.pla.annoyingvillagers.entity.Herobrine7Entity;
import com.pla.annoyingvillagers.entity.HerobrineChrisEntity;
import com.pla.annoyingvillagers.entity.HerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.HerobrineGregEntity;
import com.pla.annoyingvillagers.entity.LowHerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.LowShadowHerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.NullEntity;
import com.pla.annoyingvillagers.entity.ReaperHerobrineEntity;
import com.pla.annoyingvillagers.entity.ShadowHerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.ShadowHerobrineEntity;
import com.pla.annoyingvillagers.entity.SledgehammerHerobrineEntity;
import com.pla.annoyingvillagers.entity.SwordsmanHerobrineEntity;
import com.pla.annoyingvillagers.entity.TransporterHerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.goal.KeepPositionGoal;
import com.pla.annoyingvillagers.entity.goal.RetargetCloserThreatGoal;
import com.pla.annoyingvillagers.gameasset.AVAnimations;
import com.pla.annoyingvillagers.gameasset.AnimsSculkSteve;
import com.pla.annoyingvillagers.gameasset.AnimsWom;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModBlocks;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModParticleTypes;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.network.ClientboundHerobrineAssistanceFx;
import com.pla.annoyingvillagers.network.ClientboundHerobrinePortalFx;
import com.pla.annoyingvillagers.spawnhandler.HerobrineMobData;
import com.pla.annoyingvillagers.task.DelayedTask;
import com.pla.annoyingvillagers.util.CombatBehaviour;
import com.pla.annoyingvillagers.util.CommonGoals;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import com.pla.annoyingvillagers.util.HerobrinePortalUtil;
import com.pla.annoyingvillagers.util.HerobrineUtil;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import net.shelmarow.combat_evolution.effect.CEMobEffects;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.effect.EpicFightMobEffects;

public class HerobrineMob extends Monster implements BurstProtectEntity, CombatVoiceLineEntity {
   private boolean renderPortal = false;
   private int recallTicks = 0;
   private String chatName;
   private boolean neverRecall = false;
   private UUID gregUUID = null;
   private boolean initialSpawn = true;
   private BlockPos lastFeetPos = null;
   private EliteHerobrineKnockedEntity protectEntity;
   private UUID protectUUID;
   private int sacrificingAnimationCooldown = 0;
   private boolean sacrificing = false;
   private boolean healing = false;
   private int healingCooldown;
   private int stunEscapeCooldown = 0;
   private Entity blockDamage = null;
   private int swapWeaponCooldown;
   private int efnGuardHitState = 0;
   private int efnGuardHitCooldown = 0;
   protected float recentDamageTaken = 0.0F;
   protected int recentHitCounter = 0;
   private int voiceCooldown = 0;
   private Entity firstPossessedHerobrine;
   private UUID firstPossessedHerobrineUuid;
   private Entity secondPossessedHerobrine;
   private UUID secondPossessedHerobrineUuid;
   private Entity thirdPossessedHerobrine;
   private UUID thirdPossessedHerobrineUuid;
   private Entity fourthPossessedHerobrine;
   private UUID fourthPossessedHerobrineUuid;
   private int state = 0;
   private int secondFormHitLeft;

   public int getVoiceCooldown() {
      return this.voiceCooldown;
   }

   public void setVoiceCooldown(int cooldown) {
      this.voiceCooldown = cooldown;
   }

   public float getRecentDamageTaken() {
      return this.recentDamageTaken;
   }

   public void setRecentDamageTaken(float value) {
      this.recentDamageTaken = value;
   }

   public int getRecentHitCounter() {
      return this.recentHitCounter;
   }

   public void setRecentHitCounter(int value) {
      this.recentHitCounter = value;
   }

   public int getEfnGuardHitState() {
      return this.efnGuardHitState;
   }

   public void postPlayEfnGuardHit() {
      if (this.efnGuardHitState == 2) {
         this.efnGuardHitState = 0;
      } else {
         this.efnGuardHitState++;
      }

      this.efnGuardHitCooldown = 100;
   }

   public int getStunEscapeCooldown() {
      return this.stunEscapeCooldown;
   }

   public void setStunEscapeCooldown(int stunEscapeCooldown) {
      this.stunEscapeCooldown = stunEscapeCooldown;
   }

   public void setBlockDamage(Entity blockDamage) {
      this.blockDamage = blockDamage;
   }

   public Entity getBlockDamage() {
      return this.blockDamage;
   }

   public int getSwapWeaponCooldown() {
      return this.swapWeaponCooldown;
   }

   public int getState() {
      return this.state;
   }

   @Nullable
   public LivingEntityPatch<?> getLivingEntityPatch() {
      return (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(this, LivingEntityPatch.class);
   }

   public void setState(int state) {
      this.state = state;
   }

   public void setSecondFormHitLeft(int secondFormHitLeft) {
      this.secondFormHitLeft = secondFormHitLeft;
   }

   public int getSecondFormHitLeft() {
      return this.secondFormHitLeft;
   }

   public void setHealingCooldown() {
      this.healingCooldown = this.f_19796_.m_216339_(300, 600);
   }

   public int getHealingCooldown() {
      return this.healingCooldown;
   }

   public Entity getFirstPossessedHerobrine() {
      return this.firstPossessedHerobrine;
   }

   public Entity getSecondPossessedHerobrine() {
      return this.secondPossessedHerobrine;
   }

   public Entity getThirdPossessedHerobrine() {
      return this.thirdPossessedHerobrine;
   }

   public Entity getFourthPossessedHerobrine() {
      return this.fourthPossessedHerobrine;
   }

   public int getSacrificingAnimationCooldown() {
      return this.sacrificingAnimationCooldown;
   }

   public void rollItem() {
      this.swapWeaponCooldown = new Random().nextInt(100, 200);
   }

   public boolean isAvailableSlot() {
      return this.firstPossessedHerobrineUuid == null
         || this.secondPossessedHerobrineUuid == null
         || this.thirdPossessedHerobrineUuid == null
         || this.fourthPossessedHerobrineUuid == null;
   }

   private int getEmptyBoundClone() {
      int returnValue = 0;
      if (this.firstPossessedHerobrineUuid == null) {
         returnValue++;
      }

      if (this.secondPossessedHerobrineUuid == null) {
         returnValue++;
      }

      if (this.thirdPossessedHerobrineUuid == null) {
         returnValue++;
      }

      if (this.fourthPossessedHerobrineUuid == null) {
         returnValue++;
      }

      return returnValue;
   }

   public boolean boundPossessed(Entity entity) {
      if (this.firstPossessedHerobrineUuid == null) {
         this.firstPossessedHerobrineUuid = entity.m_20148_();
         this.firstPossessedHerobrine = entity;
         return true;
      } else if (this.secondPossessedHerobrineUuid == null) {
         this.secondPossessedHerobrineUuid = entity.m_20148_();
         this.secondPossessedHerobrine = entity;
         return true;
      } else if (this.thirdPossessedHerobrineUuid == null) {
         this.thirdPossessedHerobrineUuid = entity.m_20148_();
         this.thirdPossessedHerobrine = entity;
         return true;
      } else if (this.fourthPossessedHerobrineUuid == null) {
         this.fourthPossessedHerobrineUuid = entity.m_20148_();
         this.fourthPossessedHerobrine = entity;
         return true;
      } else {
         return false;
      }
   }

   public void setProtectUUID(UUID protectUUID) {
      this.protectUUID = protectUUID;
   }

   public void setProtectEntity(EliteHerobrineKnockedEntity protectEntity) {
      this.protectEntity = protectEntity;
   }

   public void setGregUUID(UUID gregUUID) {
      this.gregUUID = gregUUID;
   }

   public UUID getGregUUID() {
      return this.gregUUID;
   }

   public void setRecallTicks(int recallTicks) {
      this.recallTicks = recallTicks;
   }

   public int getRecallTicks() {
      return this.recallTicks;
   }

   public void setRenderPortal(boolean renderPortal) {
      this.renderPortal = renderPortal;
   }

   public String getChatName() {
      return this.chatName;
   }

   public void setChatName(String chatName) {
      this.chatName = chatName;
   }

   public void setNeverRecall(boolean neverRecall) {
      this.neverRecall = neverRecall;
   }

   public void setInitialSpawn(boolean initialSpawn) {
      this.initialSpawn = initialSpawn;
   }

   public boolean isSacrificing() {
      return this.sacrificing;
   }

   public boolean isHealing() {
      return this.healing;
   }

   public void setHealing(boolean healing) {
      this.healing = healing;
   }

   protected HerobrineMob(EntityType<? extends Monster> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.m_21409_(EquipmentSlot.MAINHAND, 0.0F);
      this.m_21409_(EquipmentSlot.CHEST, 0.0F);
      this.m_21409_(EquipmentSlot.HEAD, 0.0F);
      this.m_21441_(BlockPathTypes.WATER, 0.0F);
      this.m_21441_(BlockPathTypes.WATER_BORDER, 0.0F);
      this.m_21441_(BlockPathTypes.LAVA, 0.0F);
      this.m_21441_(BlockPathTypes.DANGER_FIRE, 0.0F);
      this.m_21441_(BlockPathTypes.DAMAGE_FIRE, 0.0F);
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   private Entity getHealingHerobrine() {
      if (this.isHealing()) {
         if (this.firstPossessedHerobrine != null) {
            if (this.firstPossessedHerobrine instanceof LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntity
               && lowShadowHerobrineCloneEntity.isHealing()) {
               return lowShadowHerobrineCloneEntity;
            }

            if (this.firstPossessedHerobrine instanceof LowHerobrineCloneEntity lowHerobrineCloneEntity && lowHerobrineCloneEntity.isHealing()) {
               return lowHerobrineCloneEntity;
            }
         }

         if (this.secondPossessedHerobrine != null) {
            if (this.secondPossessedHerobrine instanceof LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntity
               && lowShadowHerobrineCloneEntity.isHealing()) {
               return lowShadowHerobrineCloneEntity;
            }

            if (this.secondPossessedHerobrine instanceof LowHerobrineCloneEntity lowHerobrineCloneEntity && lowHerobrineCloneEntity.isHealing()) {
               return lowHerobrineCloneEntity;
            }
         }

         if (this.thirdPossessedHerobrine != null) {
            if (this.thirdPossessedHerobrine instanceof LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntity
               && lowShadowHerobrineCloneEntity.isHealing()) {
               return lowShadowHerobrineCloneEntity;
            }

            if (this.thirdPossessedHerobrine instanceof LowHerobrineCloneEntity lowHerobrineCloneEntity && lowHerobrineCloneEntity.isHealing()) {
               return lowHerobrineCloneEntity;
            }
         }

         if (this.fourthPossessedHerobrine != null) {
            if (this.fourthPossessedHerobrine instanceof LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntity
               && lowShadowHerobrineCloneEntity.isHealing()) {
               return lowShadowHerobrineCloneEntity;
            }

            if (this.fourthPossessedHerobrine instanceof LowHerobrineCloneEntity lowHerobrineCloneEntity && lowHerobrineCloneEntity.isHealing()) {
               return lowHerobrineCloneEntity;
            }
         }
      }

      return null;
   }

   protected void m_8099_() {
      super.m_8099_();
      this.f_21346_.m_25352_(0, new RetargetCloserThreatGoal(this));
      this.f_21345_.m_25352_(1, new KeepPositionGoal(this));
      this.f_21345_
         .m_25352_(
            1,
            new Goal() {
               public boolean m_8036_() {
                  return HerobrineMob.this.protectEntity != null
                     && HerobrineMob.this.protectEntity.m_6084_()
                     && HerobrineMob.this.m_20270_(HerobrineMob.this.protectEntity) > 9.0F;
               }

               public void m_8037_() {
                  if (HerobrineMob.this.protectEntity != null && HerobrineMob.this.protectEntity.m_6084_()) {
                     HerobrineMob.this.m_21573_().m_5624_(HerobrineMob.this.protectEntity, 2.0);
                     HerobrineMob.this.m_21563_().m_24960_(HerobrineMob.this.protectEntity, 30.0F, 30.0F);
                     if (HerobrineMob.this.m_20280_(HerobrineMob.this.protectEntity) > 10.0) {
                        if (HerobrineMob.this.m_21573_().m_26571_()) {
                           HerobrineMob.this.m_21573_().m_5624_(HerobrineMob.this.protectEntity, 2.0);
                        }
                     } else {
                        HerobrineMob.this.m_21573_().m_26573_();
                     }
                  }
               }

               public boolean m_8045_() {
                  return HerobrineMob.this.protectEntity != null
                     && HerobrineMob.this.protectEntity.m_6084_()
                     && (double)HerobrineMob.this.m_20270_(HerobrineMob.this.protectEntity) > 50.0;
               }
            }
         );
      this.f_21345_
         .m_25352_(
            1,
            new Goal() {
               public boolean m_8036_() {
                  return HerobrineMob.this.protectEntity != null
                     && HerobrineMob.this.getHealingHerobrine() != null
                     && HerobrineMob.this.getHealingHerobrine().m_6084_()
                     && HerobrineMob.this.m_20270_(HerobrineMob.this.getHealingHerobrine()) > 9.0F;
               }

               public void m_8037_() {
                  if (HerobrineMob.this.getHealingHerobrine() != null && HerobrineMob.this.getHealingHerobrine().m_6084_()) {
                     HerobrineMob.this.m_21573_().m_5624_(HerobrineMob.this.getHealingHerobrine(), 2.0);
                     if (HerobrineMob.this.m_20280_(HerobrineMob.this.getHealingHerobrine()) > 10.0) {
                        if (HerobrineMob.this.m_21573_().m_26571_()) {
                           HerobrineMob.this.m_21573_().m_5624_(HerobrineMob.this.getHealingHerobrine(), 2.0);
                        }
                     } else {
                        HerobrineMob.this.m_21573_().m_26573_();
                     }
                  }
               }

               public boolean m_8045_() {
                  return HerobrineMob.this.isHealing()
                     && HerobrineMob.this.getHealingHerobrine() != null
                     && HerobrineMob.this.getHealingHerobrine().m_6084_()
                     && (double)HerobrineMob.this.m_20270_(HerobrineMob.this.getHealingHerobrine()) > 50.0;
               }
            }
         );
      CommonGoals.registerGoalForHostileNpc(this);
      this.f_21345_.m_25352_(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
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

   protected void m_7472_(@NotNull DamageSource damagesource, int i, boolean flag) {
      super.m_7472_(damagesource, i, flag);
      this.m_19983_(new ItemStack(Blocks.f_50080_));
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

   public boolean m_142535_(float f, float f1, @NotNull DamageSource damagesource) {
      return super.m_142535_(f, f1, damagesource);
   }

   public float getBurstProtectCapRatio() {
      return 0.05F;
   }

   public boolean m_6469_(@NotNull DamageSource damageSource, float f) {
      if (!this.getPersistentData().m_128471_("rising") && !this.getPersistentData().m_128471_("sinking") && !this.sacrificing) {
         boolean result = super.m_6469_(damageSource, f);
         if (result) {
            this.sayHurtSound(this, damageSource);
         }

         return result;
      } else {
         if (this.m_9236_() instanceof ServerLevel serverLevel) {
            EpicfightUtil.damageBlocked(damageSource, this, serverLevel);
         }

         return false;
      }
   }

   public boolean m_7327_(@NotNull Entity target) {
      boolean result = super.m_7327_(target);
      if (result) {
         this.sayAttackSound(this, target);
      }

      return result;
   }

   private void triggerSecondForm(ServerLevel serverLevel) {
      if (this.sacrificingAnimationCooldown == 0) {
         this.sacrificingAnimationCooldown = 80;
         this.m_21557_(true);
         if (!(this instanceof NullEntity)) {
            this.m_21008_(InteractionHand.MAIN_HAND, ItemStack.f_41583_);
            this.m_21008_(InteractionHand.OFF_HAND, ItemStack.f_41583_);
         }

         if (this instanceof ShadowHerobrineEntity shadowHerobrineEntity) {
            shadowHerobrineEntity.clearDarkOb();
         }

         this.m_7292_(new MobEffectInstance((MobEffect)EpicFightMobEffects.STUN_IMMUNITY.get(), 80, 2));
         if (this.gregUUID != null) {
            Entity entity = serverLevel.m_8791_(this.gregUUID);
            if (entity instanceof HerobrineGregEntity herobrineGregEntity && entity.m_6084_()) {
               if (this instanceof ShadowHerobrineEntity) {
                  if ((Boolean)AnnoyingVillagersConfig.TURN_ON_NPC_VOICE.get()) {
                     this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.SHADOW_HEROBRINE_SAY_ON_PHASE_2.get(), 0.5F, 1.0F);
                  }
               } else {
                  herobrineGregEntity.m_5496_((SoundEvent)AnnoyingVillagersModSounds.GREG_REQUESTING_ASSISTANCE.get(), 1.0F, 1.0F);
               }

               Objects.requireNonNull(herobrineGregEntity.m_9236_().m_7654_())
                  .m_6846_()
                  .m_240416_(
                     Component.m_237113_(
                        "<"
                           + Component.m_237115_("entity.annoyingvillagers.herobrine_greg").getString()
                           + "> "
                           + Component.m_237115_("subtitles.herobrine_request").getString()
                     ),
                     false
                  );
               return;
            }
         }

         if (this instanceof ShadowHerobrineEntity) {
            if ((Boolean)AnnoyingVillagersConfig.TURN_ON_NPC_VOICE.get()) {
               this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.SHADOW_HEROBRINE_SAY_ON_PHASE_2.get(), 0.5F, 1.0F);
            }
         } else {
            this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.SELF_REQUESTING_ASSISTANCE.get(), 1.0F, 1.0F);
         }

         Objects.requireNonNull(this.m_9236_().m_7654_())
            .m_6846_()
            .m_240416_(Component.m_237113_("<" + this.getChatName() + "> " + Component.m_237115_("subtitles.herobrine_request").getString()), false);
      }
   }

   protected void m_6475_(@NotNull DamageSource pDamageSource, float pDamageAmount) {
      if (pDamageSource.m_276093_(DamageTypes.f_268724_)) {
         super.m_6475_(pDamageSource, pDamageAmount);
      } else if (!this.m_6673_(pDamageSource)) {
         pDamageAmount = ForgeHooks.onLivingHurt(this, pDamageSource, pDamageAmount);
         if (!(pDamageAmount <= 0.0F)) {
            pDamageAmount = this.m_21161_(pDamageSource, pDamageAmount);
            pDamageAmount = this.m_6515_(pDamageSource, pDamageAmount);
            float f1 = Math.max(pDamageAmount - this.m_6103_(), 0.0F);
            float absorbed = pDamageAmount - f1;
            if (absorbed > 0.0F) {
               this.m_7911_(this.m_6103_() - absorbed);
               if (this.m_6103_() < 0.0F) {
                  this.m_7911_(0.0F);
               }
            }

            f1 = ForgeHooks.onLivingDamage(this, pDamageSource, f1);
            f1 = this.applyBurstProtection(this, pDamageSource, f1);
            if (this.m_9236_() instanceof ServerLevel serverLevel
               && this.getState() < 2
               && (
                  this instanceof AegisHerobrineEntity
                     || this instanceof SledgehammerHerobrineEntity
                     || this instanceof SwordsmanHerobrineEntity
                     || this instanceof ReaperHerobrineEntity
                     || this instanceof GlaiveHerobrineEntity
                     || this instanceof NullEntity
                     || this instanceof ShadowHerobrineEntity
               )
               && this.m_21223_() - f1 <= 1.0F) {
               this.m_21153_(1.0F);
               this.sacrificing = true;
               this.triggerSecondForm(serverLevel);
               return;
            }

            if (!(f1 <= 0.0F)) {
               this.m_21231_().m_289194_(pDamageSource, f1);
               this.m_21153_(this.m_21223_() - f1);
               this.m_146850_(GameEvent.f_223706_);
            }
         }
      }
   }

   public void m_7378_(@NotNull CompoundTag pCompound) {
      super.m_7378_(pCompound);
      this.swapWeaponCooldown = pCompound.m_128451_("SwapWeaponCooldown");
      this.recallTicks = pCompound.m_128451_("RecallTicks");
      this.renderPortal = pCompound.m_128471_("RenderPortal");
      this.neverRecall = pCompound.m_128471_("NeverRecall");
      if (pCompound.m_128441_("GregUUID")) {
         this.gregUUID = pCompound.m_128342_("GregUUID");
      }

      this.initialSpawn = pCompound.m_128471_("InitialSpawn");
      if (pCompound.m_128403_("ProtectUUID")) {
         this.protectUUID = pCompound.m_128342_("ProtectUUID");
      }

      if (pCompound.m_128403_("FirstPossessedHerobrineUuid")) {
         this.firstPossessedHerobrineUuid = pCompound.m_128342_("FirstPossessedHerobrineUuid");
      }

      if (pCompound.m_128403_("SecondPossessedHerobrineUuid")) {
         this.secondPossessedHerobrineUuid = pCompound.m_128342_("SecondPossessedHerobrineUuid");
      }

      if (pCompound.m_128403_("ThirdPossessedHerobrineUuid")) {
         this.thirdPossessedHerobrineUuid = pCompound.m_128342_("ThirdPossessedHerobrineUuid");
      }

      if (pCompound.m_128403_("FourthPossessedHerobrineUuid")) {
         this.fourthPossessedHerobrineUuid = pCompound.m_128342_("FourthPossessedHerobrineUuid");
      }

      this.sacrificing = pCompound.m_128471_("Sacrificing");
      this.healing = pCompound.m_128471_("Healing");
      this.sacrificingAnimationCooldown = pCompound.m_128451_("SacrificingAnimationCooldown");
      this.state = pCompound.m_128451_("State");
      this.secondFormHitLeft = pCompound.m_128451_("SecondFormHitLeft");
      this.healingCooldown = pCompound.m_128451_("HealingCooldown");
      this.voiceCooldown = pCompound.m_128451_("VoiceCooldown");
   }

   public void jump() {
      this.m_6135_();
      Vec3 motion = this.m_20184_();
      Vec3 forward = this.m_20156_();
      double strength = new Random().nextDouble(0.2, 0.4);
      this.m_20334_(motion.f_82479_ + forward.f_82479_ * strength, motion.f_82480_, motion.f_82481_ + forward.f_82481_ * strength);
      this.f_19812_ = true;
   }

   public void m_7380_(@NotNull CompoundTag pCompound) {
      super.m_7380_(pCompound);
      pCompound.m_128405_("SwapWeaponCooldown", this.swapWeaponCooldown);
      pCompound.m_128405_("RecallTicks", this.recallTicks);
      pCompound.m_128379_("RenderPortal", this.renderPortal);
      pCompound.m_128379_("NeverRecall", this.neverRecall);
      if (this.gregUUID != null) {
         pCompound.m_128362_("GregUUID", this.gregUUID);
      }

      pCompound.m_128379_("InitialSpawn", this.initialSpawn);
      if (this.protectUUID != null) {
         pCompound.m_128362_("ProtectUUID", this.protectUUID);
      }

      if (this.firstPossessedHerobrineUuid != null) {
         pCompound.m_128362_("FirstPossessedHerobrineUuid", this.firstPossessedHerobrineUuid);
      }

      if (this.secondPossessedHerobrineUuid != null) {
         pCompound.m_128362_("SecondPossessedHerobrineUuid", this.secondPossessedHerobrineUuid);
      }

      if (this.thirdPossessedHerobrineUuid != null) {
         pCompound.m_128362_("ThirdPossessedHerobrineUuid", this.thirdPossessedHerobrineUuid);
      }

      if (this.fourthPossessedHerobrineUuid != null) {
         pCompound.m_128362_("FourthPossessedHerobrineUuid", this.fourthPossessedHerobrineUuid);
      }

      pCompound.m_128379_("Sacrificing", this.sacrificing);
      pCompound.m_128379_("Healing", this.healing);
      pCompound.m_128405_("SacrificingAnimationCooldown", this.sacrificingAnimationCooldown);
      pCompound.m_128405_("State", this.state);
      pCompound.m_128405_("SecondFormHitLeft", this.secondFormHitLeft);
      pCompound.m_128405_("HealingCooldown", this.healingCooldown);
      pCompound.m_128405_("VoiceCooldown", this.voiceCooldown);
   }

   @NotNull
   protected PathNavigation m_6037_(@NotNull Level level) {
      return new HerobrineMob.AnyFluidPathNavigation(this, level);
   }

   private void floatOnAnyFluid() {
      BlockPos pos = this.m_20183_();
      FluidState fluidState = this.m_9236_().m_6425_(pos);
      if (!fluidState.m_76178_()) {
         CollisionContext collisionContext = CollisionContext.m_82750_(this);
         Fluid typeHere = fluidState.m_76152_();
         FluidState above = this.m_9236_().m_6425_(pos.m_7494_());
         if (collisionContext.m_6513_(LiquidBlock.f_54690_, pos, true) && above.m_76152_() != typeHere) {
            this.m_6853_(true);
            double surfaceY = (double)((float)pos.m_123342_() + fluidState.m_76155_(this.m_9236_(), pos));
            double bottomY = this.m_20191_().f_82289_;
            double diff = surfaceY - bottomY - 0.001;
            if (diff > 0.0) {
               Vec3 vel = this.m_20184_();
               this.m_20334_(vel.f_82479_, Math.max(vel.f_82480_, Math.min(0.2, diff * 0.2)), vel.f_82481_);
            }
         } else {
            this.m_20256_(this.m_20184_().m_82490_(0.5).m_82520_(0.0, 0.05, 0.0));
         }

         this.f_19789_ = 0.0F;
      }
   }

   public boolean m_20069_() {
      FluidState fs = this.m_9236_().m_6425_(this.m_20183_());
      return !fs.m_76178_() && this.m_203441_(fs) ? false : super.m_20069_();
   }

   public boolean m_203441_(FluidState state) {
      return !state.m_76178_();
   }

   public boolean m_6063_() {
      return false;
   }

   private void placeObsidianBlockWhenInWater(Block block) {
      BlockPos feet = this.m_20097_();
      if (this.lastFeetPos == null) {
         this.lastFeetPos = feet;
      }

      if (!feet.equals(this.lastFeetPos)) {
         if (!this.m_9236_().m_8055_(this.lastFeetPos).m_60713_(block)) {
            FluidState fluidState = this.m_9236_().m_6425_(this.lastFeetPos);
            if (!fluidState.m_76178_()) {
               int replace = fluidState.m_76170_() ? (fluidState.m_205070_(FluidTags.f_13131_) ? 1 : (fluidState.m_205070_(FluidTags.f_13132_) ? 2 : 0)) : 0;
               BlockState state = (BlockState)block.m_49966_().m_61124_(HerobrineObsidianBlock.REPLACE_BY_LIQUID, replace);
               this.m_9236_().m_46597_(this.lastFeetPos, state);
               BlockEntity blockEntity = this.m_9236_().m_7702_(this.lastFeetPos);
               if (blockEntity instanceof ObsidianBlockEntity obsidianBlockEntity) {
                  obsidianBlockEntity.setOwner(this.m_20148_());
                  obsidianBlockEntity.m_6596_();
                  this.m_9236_().m_7260_(this.lastFeetPos, state, state, 3);
               }

               if (blockEntity instanceof ShadowObsidianBlockEntity shadowObsidianBlockEntity) {
                  shadowObsidianBlockEntity.setOwner(this.m_20148_());
                  shadowObsidianBlockEntity.m_6596_();
                  this.m_9236_().m_7260_(this.lastFeetPos, state, state, 3);
               }

               if (blockEntity instanceof CryingObsidianBlockEntity cryingObsidianBlockEntity) {
                  cryingObsidianBlockEntity.setOwner(this.m_20148_());
                  cryingObsidianBlockEntity.m_6596_();
                  this.m_9236_().m_7260_(this.lastFeetPos, state, state, 3);
               }
            }
         }

         this.lastFeetPos = feet;
      }
   }

   private void recoverAfterSacrificing() {
      this.sacrificing = false;
      this.m_21557_(false);
      this.m_21219_();
      if (this.getLivingEntityPatch() != null) {
         this.getLivingEntityPatch().applyStun(StunType.FALL, 0.0F);
      }

      if (this instanceof AegisHerobrineEntity) {
         if ((Boolean)AnnoyingVillagersConfig.TURN_ON_NPC_VOICE.get()) {
            this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.ELITE_HEROBRINE_SAY_SECOND_FORM_RELEASE.get(), 0.5F, 1.0F);
         }

         ItemStack enderAegis = new ItemStack((ItemLike)AnnoyingVillagersModItems.ENDER_AEGIS.get());
         enderAegis.m_41663_(Enchantments.f_44977_, 3);
         enderAegis.m_41663_(Enchantments.f_44983_, 3);
         enderAegis.m_41663_(Enchantments.f_44980_, 3);
         this.m_21008_(InteractionHand.MAIN_HAND, enderAegis);
      }

      if (this instanceof SwordsmanHerobrineEntity) {
         if ((Boolean)AnnoyingVillagersConfig.TURN_ON_NPC_VOICE.get()) {
            this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.ELITE_HEROBRINE_SAY_SECOND_FORM_RELEASE.get(), 0.5F, 1.0F);
         }

         ItemStack demoniacVoltageReaver = new ItemStack((ItemLike)AnnoyingVillagersModItems.DEMONIAC_VOLTAGE_REAVER.get());
         demoniacVoltageReaver.m_41663_(Enchantments.f_44977_, 3);
         demoniacVoltageReaver.m_41663_(Enchantments.f_44983_, 3);
         demoniacVoltageReaver.m_41663_(Enchantments.f_44980_, 3);
         this.m_21008_(InteractionHand.MAIN_HAND, demoniacVoltageReaver);
      }

      if (this instanceof SledgehammerHerobrineEntity) {
         if ((Boolean)AnnoyingVillagersConfig.TURN_ON_NPC_VOICE.get()) {
            this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.ELITE_HEROBRINE_SAY_SECOND_FORM_RELEASE.get(), 0.5F, 1.0F);
         }

         ItemStack obsidianSledgehammer = new ItemStack((ItemLike)AnnoyingVillagersModItems.OBSIDIAN_SLEDGEHAMMER.get());
         obsidianSledgehammer.m_41663_(Enchantments.f_44977_, 3);
         obsidianSledgehammer.m_41663_(Enchantments.f_44983_, 3);
         obsidianSledgehammer.m_41663_(Enchantments.f_44980_, 3);
         this.m_21008_(InteractionHand.MAIN_HAND, obsidianSledgehammer);
      }

      if (this instanceof GlaiveHerobrineEntity) {
         if ((Boolean)AnnoyingVillagersConfig.TURN_ON_NPC_VOICE.get()) {
            this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.ELITE_HEROBRINE_SAY_SECOND_FORM_RELEASE.get(), 0.5F, 1.0F);
         }

         ItemStack enderGlaive = new ItemStack((ItemLike)AnnoyingVillagersModItems.ENDER_GLAIVE.get());
         enderGlaive.m_41663_(Enchantments.f_44977_, 3);
         enderGlaive.m_41663_(Enchantments.f_44983_, 3);
         enderGlaive.m_41663_(Enchantments.f_44980_, 3);
         this.m_21008_(InteractionHand.MAIN_HAND, enderGlaive);
      }

      if (this instanceof ReaperHerobrineEntity reaperHerobrineEntity) {
         if ((Boolean)AnnoyingVillagersConfig.TURN_ON_NPC_VOICE.get()) {
            this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.ELITE_HEROBRINE_SAY_SECOND_FORM_RELEASE.get(), 0.5F, 1.0F);
         }

         ItemStack enderSlayerScythe = new ItemStack((ItemLike)AnnoyingVillagersModItems.ENDER_SLAYER_SCYTHE.get());
         enderSlayerScythe.m_41663_(Enchantments.f_44977_, 3);
         enderSlayerScythe.m_41663_(Enchantments.f_44983_, 3);
         enderSlayerScythe.m_41663_(Enchantments.f_44980_, 3);
         this.m_21008_(InteractionHand.MAIN_HAND, enderSlayerScythe);
         if (reaperHerobrineEntity.getThunderHerobrineDragon() == null && reaperHerobrineEntity.getThunderHerobrineDragonUUID() == null) {
            reaperHerobrineEntity.summonEnderDragon(0);
         }

         if (reaperHerobrineEntity.getMeteoriteHerobrineDragon() == null && reaperHerobrineEntity.getMeteoriteHerobrineDragonUUID() == null) {
            reaperHerobrineEntity.summonEnderDragon(1);
         }

         if (reaperHerobrineEntity.getHealingHerobrineDragon() == null && reaperHerobrineEntity.getHealingHerobrineDragonUUID() == null) {
            reaperHerobrineEntity.summonEnderDragon(2);
         }
      }

      if (this instanceof NullEntity nullEntity) {
         if (nullEntity.getNullSwordEntity() != null) {
            ItemStack nullSword = new ItemStack((ItemLike)AnnoyingVillagersModItems.NULL_SWORD.get());
            nullSword.m_41663_(Enchantments.f_44977_, 5);
            nullSword.m_41663_(Enchantments.f_44983_, 5);
            nullEntity.getNullSwordEntity().m_21008_(InteractionHand.MAIN_HAND, nullSword);
         }

         if (nullEntity.getNullAxeEntity() != null) {
            ItemStack nullAxe = new ItemStack((ItemLike)AnnoyingVillagersModItems.NULL_AXE.get());
            nullAxe.m_41663_(Enchantments.f_44978_, 5);
            nullAxe.m_41663_(Enchantments.f_44981_, 2);
            nullEntity.getNullAxeEntity().m_21008_(InteractionHand.MAIN_HAND, nullAxe);
         }

         if (nullEntity.getNullPickaxeEntity() != null) {
            ItemStack nullPickaxe = new ItemStack((ItemLike)AnnoyingVillagersModItems.NULL_PICKAXE.get());
            nullPickaxe.m_41663_(Enchantments.f_44984_, 5);
            nullPickaxe.m_41663_(Enchantments.f_44986_, 3);
            nullEntity.getNullPickaxeEntity().m_21008_(InteractionHand.MAIN_HAND, nullPickaxe);
         }

         if (nullEntity.getNullShovelEntity() != null) {
            ItemStack nullShovel = new ItemStack((ItemLike)AnnoyingVillagersModItems.NULL_SHOVEL.get());
            nullShovel.m_41663_(Enchantments.f_44986_, 5);
            nullShovel.m_41663_(Enchantments.f_44962_, 1);
            nullEntity.getNullShovelEntity().m_21008_(InteractionHand.MAIN_HAND, nullShovel);
         }

         if (nullEntity.getNullHoeEntity() != null) {
            ItemStack nullHoe = new ItemStack((ItemLike)AnnoyingVillagersModItems.NULL_HOE.get());
            nullHoe.m_41663_(Enchantments.f_44980_, 5);
            nullHoe.m_41663_(Enchantments.f_44984_, 1);
            nullEntity.getNullHoeEntity().m_21008_(InteractionHand.MAIN_HAND, nullHoe);
         }
      }

      if (this instanceof ShadowHerobrineEntity shadowHerobrineEntity) {
         ItemStack shadowObsidianPillar = new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_PILLAR.get());
         shadowObsidianPillar.m_41663_(Enchantments.f_44977_, 5);
         shadowObsidianPillar.m_41663_(Enchantments.f_44983_, 5);
         shadowObsidianPillar.m_41663_(Enchantments.f_44980_, 3);
         this.m_21008_(InteractionHand.MAIN_HAND, shadowObsidianPillar);
         shadowHerobrineEntity.setSummonDarkObCooldown(0);
      }

      this.state = 2;
   }

   private void recoverAfterHealing() {
      this.setHealingCooldown();
      this.healing = false;
   }

   public boolean m_7301_(MobEffectInstance mobeffectinstance) {
      return (mobeffectinstance.m_19544_().m_19483_() == MobEffectCategory.BENEFICIAL || mobeffectinstance.m_19544_() == MobEffects.f_19619_)
         && super.m_7301_(mobeffectinstance);
   }

   public void m_8119_() {
      super.m_8119_();
      this.floatOnAnyFluid();
      this.m_20101_();
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         this.tickVoiceCooldown();
         this.tickBurstProtectionDecay(this);
         if (this.stunEscapeCooldown > 0) {
            this.stunEscapeCooldown--;
         }

         if (this.swapWeaponCooldown > 0) {
            this.swapWeaponCooldown--;
         }

         if (this.efnGuardHitCooldown > 0) {
            this.efnGuardHitCooldown--;
         }

         if (this.efnGuardHitCooldown == 0 && this.efnGuardHitState != 0) {
            this.efnGuardHitState = 0;
         }

         if (this.getLivingEntityPatch() != null && CombatCommon.canEscape((MobPatch)this.getLivingEntityPatch())) {
            this.f_21345_.m_25355_(Flag.MOVE);
            this.m_21573_().m_26573_();
            LivingEntity target = this.m_5448_();
            if (target != null) {
               this.m_21563_().m_24960_(target, 30.0F, 30.0F);
            }
         } else {
            this.f_21345_.m_25374_(Flag.MOVE);
         }

         if (ModList.get().isLoaded("efkick") && this.stunEscapeCooldown == 0 && this.m_9236_() instanceof ServerLevel && this.getLivingEntityPatch() != null) {
            final AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(
                  this.getLivingEntityPatch().getAnimator().getPlayerFor(null)
               )
               .getRealAnimation();
            if (EpicfightUtil.isLongHitAnimationNotExecutedAnimation(dynamicAnimation, this.getLivingEntityPatch())
               && this.m_6084_()
               && (double)this.m_217043_().m_188501_() < CombatBehaviour.calculateGuardBreakWakeUpChance(this)) {
               this.stunEscapeCooldown = 60;
               final HerobrineMob entity = this;
               new DelayedTask(new Random().nextInt(5, 10)) {
                  public void run() {
                     if (HerobrineMob.this.getLivingEntityPatch() != null
                        && EpicfightUtil.isLongHitAnimationNotExecutedAnimation(dynamicAnimation, HerobrineMob.this.getLivingEntityPatch())
                        && entity.m_6084_()) {
                        CombatBehaviour.postGuardBreakWakeUp(entity, HerobrineMob.this.getLivingEntityPatch(), serverLevel);
                     } else {
                        entity.stunEscapeCooldown = 1;
                     }
                  }
               };
            }
         }

         if (this.state == 2
            && (
               this instanceof AegisHerobrineEntity
                  || this instanceof SledgehammerHerobrineEntity
                  || this instanceof SwordsmanHerobrineEntity
                  || this instanceof ReaperHerobrineEntity
                  || this instanceof GlaiveHerobrineEntity
                  || this instanceof NullEntity
                  || this instanceof ShadowHerobrineEntity
            )) {
            this.m_7292_(new MobEffectInstance((MobEffect)CEMobEffects.FULL_STUN_IMMUNITY.get(), 3, 3));
            this.m_7292_(new MobEffectInstance((MobEffect)EpicFightMobEffects.STUN_IMMUNITY.get(), 3, 3));
            if ((this instanceof NullEntity || this instanceof ShadowHerobrineEntity) && new Random().nextBoolean()) {
               serverLevel.m_8767_(
                  (SimpleParticleType)AnnoyingVillagersModParticleTypes.FULL_COWL.get(),
                  this.m_20185_(),
                  this.m_20186_(),
                  this.m_20189_(),
                  1,
                  0.3,
                  1.2,
                  0.3,
                  0.0
               );
            }
         }

         if (this.healingCooldown > 0) {
            this.healingCooldown--;
         }

         if (this instanceof HerobrineCloneEntity || this instanceof HerobrineChrisEntity) {
            this.placeObsidianBlockWhenInWater((Block)AnnoyingVillagersModBlocks.OBSIDIAN_BLOCK.get());
         } else if (this instanceof ShadowHerobrineCloneEntity
            || this instanceof Herobrine7Entity
            || this instanceof ArmoredHerobrineEntity
            || this instanceof ShadowHerobrineEntity) {
            this.placeObsidianBlockWhenInWater((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get());
         } else if (!(this instanceof NullEntity)) {
            this.placeObsidianBlockWhenInWater((Block)AnnoyingVillagersModBlocks.CRYING_OBSIDIAN_BLOCK.get());
         }

         if (this.m_21023_(MobEffects.f_19600_)
            && this.m_21023_(MobEffects.f_19596_)
            && this.m_21023_(MobEffects.f_19603_)
            && this.m_21023_(MobEffects.f_19606_)
            && new Random().nextBoolean()) {
            serverLevel.m_8767_(
               (SimpleParticleType)AnnoyingVillagersModParticleTypes.FULL_COWL.get(), this.m_20185_(), this.m_20186_(), this.m_20189_(), 1, 0.3, 1.2, 0.3, 0.0
            );
         }

         if (this.f_19797_ == 1) {
            if (this.renderPortal) {
               AnnoyingVillagers.PACKET_HANDLER
                  .send(
                     PacketDistributor.TRACKING_ENTITY.with(() -> this), new ClientboundHerobrinePortalFx(this.m_20097_().m_252807_().m_82520_(0.0, 1.5, 0.0))
                  );
               this.renderPortal = false;
            }

            if (this.initialSpawn) {
               this.m_21557_(true);
               if (this.getLivingEntityPatch() != null && !this.m_9236_().m_5776_()) {
                  if (this instanceof ReaperHerobrineEntity || this instanceof GlaiveHerobrineEntity) {
                     this.getLivingEntityPatch().playAnimationSynchronized(AnimsWom.GLOWING_AGONY_GUARD, 0.0F);
                  } else if (this instanceof AegisHerobrineEntity aegisHerobrineEntity) {
                     aegisHerobrineEntity.getPersistentData().m_128379_("init_animation", true);
                  } else if (this instanceof TransporterHerobrineCloneEntity) {
                     this.getLivingEntityPatch().playAnimationSynchronized(AnimsSculkSteve.PORTAL_SUMMON, 0.0F);
                  } else if (!(this instanceof SledgehammerHerobrineEntity) && !(this instanceof SwordsmanHerobrineEntity)) {
                     this.getLivingEntityPatch().playAnimationSynchronized(AVAnimations.HEROBRINE_ANIMATE, 0.0F);
                  }
               }

               this.initialSpawn = false;
            }
         }

         if (!this.neverRecall) {
            this.recallTicks--;
            int remaining = this.recallTicks;
            if (remaining == 40) {
               AnnoyingVillagers.PACKET_HANDLER
                  .send(
                     PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this),
                     new ClientboundHerobrinePortalFx(new Vec3(this.m_20185_(), this.m_20186_(), this.m_20189_()))
                  );
               this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.PORTAL_NATURAL.get(), 1.0F, 1.0F);
               HerobrinePortalUtil.sinkIntoGround(serverLevel, this, 0.06);
            }

            if (remaining <= 0) {
               Objects.requireNonNull(this.m_9236_().m_7654_())
                  .m_6846_()
                  .m_240416_(Component.m_237113_(this.getChatName() + " " + Component.m_237115_("subtitles.herobrine_retreat")), false);
               this.m_146870_();
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
            this.recallTicks = 41;
         }

         if (this.firstPossessedHerobrine == null && this.firstPossessedHerobrineUuid != null) {
            this.firstPossessedHerobrine = ((ServerLevel)this.m_9236_()).m_8791_(this.firstPossessedHerobrineUuid);
         }

         if (this.secondPossessedHerobrine == null && this.secondPossessedHerobrineUuid != null) {
            this.secondPossessedHerobrine = ((ServerLevel)this.m_9236_()).m_8791_(this.secondPossessedHerobrineUuid);
         }

         if (this.thirdPossessedHerobrine == null && this.thirdPossessedHerobrineUuid != null) {
            this.thirdPossessedHerobrine = ((ServerLevel)this.m_9236_()).m_8791_(this.thirdPossessedHerobrineUuid);
         }

         if (this.fourthPossessedHerobrine == null && this.fourthPossessedHerobrineUuid != null) {
            this.fourthPossessedHerobrine = ((ServerLevel)this.m_9236_()).m_8791_(this.fourthPossessedHerobrineUuid);
         }

         if (this.firstPossessedHerobrine != null && !this.firstPossessedHerobrine.m_6084_()) {
            this.firstPossessedHerobrine = null;
            this.firstPossessedHerobrineUuid = null;
            if (this.sacrificing && this.getEmptyBoundClone() == 4) {
               this.recoverAfterSacrificing();
            }

            if (this.healing && this.getHealingHerobrine() == null) {
               this.recoverAfterHealing();
            }
         }

         if (this.secondPossessedHerobrine != null && !this.secondPossessedHerobrine.m_6084_()) {
            this.secondPossessedHerobrine = null;
            this.secondPossessedHerobrineUuid = null;
            if (this.sacrificing && this.getEmptyBoundClone() == 4) {
               this.recoverAfterSacrificing();
            }

            if (this.healing && this.getHealingHerobrine() == null) {
               this.recoverAfterHealing();
            }
         }

         if (this.thirdPossessedHerobrine != null && !this.thirdPossessedHerobrine.m_6084_()) {
            this.thirdPossessedHerobrine = null;
            this.thirdPossessedHerobrineUuid = null;
            if (this.sacrificing && this.getEmptyBoundClone() == 4) {
               this.recoverAfterSacrificing();
            }

            if (this.healing && this.getHealingHerobrine() == null) {
               this.recoverAfterHealing();
            }
         }

         if (this.fourthPossessedHerobrine != null && !this.fourthPossessedHerobrine.m_6084_()) {
            this.fourthPossessedHerobrine = null;
            this.fourthPossessedHerobrineUuid = null;
            if (this.sacrificing && this.getEmptyBoundClone() >= 4) {
               this.recoverAfterSacrificing();
            }

            if (this.healing && this.getHealingHerobrine() == null) {
               this.recoverAfterHealing();
            }
         }

         if (this.sacrificingAnimationCooldown > 0) {
            this.sacrificingAnimationCooldown--;
         }

         if (this.sacrificingAnimationCooldown == 60) {
            if (this instanceof NullEntity nullEntity) {
               nullEntity.setSpinningToAllWeaponsAvailableFor5seconds();
            } else {
               this.m_21008_(InteractionHand.MAIN_HAND, ItemStack.f_41583_);
               this.m_21008_(InteractionHand.OFF_HAND, ItemStack.f_41583_);
            }

            if (this.getLivingEntityPatch() != null) {
               this.getLivingEntityPatch().playAnimationSynchronized(AnimsSculkSteve.HEROBRINE_STAGE_CHANGE, 0.0F);
            }

            AnnoyingVillagers.PACKET_HANDLER
               .send(
                  PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this),
                  new ClientboundHerobrineAssistanceFx(new Vec3(this.m_20185_(), this.m_20186_(), this.m_20189_()))
               );
            if (this.m_9236_() instanceof ServerLevel) {
               this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.PORTAL_NATURAL.get(), 1.0F, 1.0F);
            }

            if (this.m_9236_() instanceof ServerLevel supportServerLevel) {
               this.playSecondFormSupportCasterAnimations(supportServerLevel);
            }

            this.summonClonesForNextStage();
         }

         if (this.sacrificingAnimationCooldown == 10) {
            this.m_21557_(true);
            if (this.firstPossessedHerobrine != null) {
               ((Mob)this.firstPossessedHerobrine).m_7292_(new MobEffectInstance((MobEffect)EpicFightMobEffects.STUN_IMMUNITY.get(), 30, 3, false, false));
               this.clearHandAndDropItem(this.firstPossessedHerobrine);
               if (this.firstPossessedHerobrine instanceof LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntity) {
                  lowShadowHerobrineCloneEntity.setSacrificing(true);
               }

               this.firstPossessedHerobrine.m_7618_(Anchor.EYES, new Vec3(this.m_20185_(), this.m_20186_(), this.m_20189_()));
               this.firstPossessedHerobrine.m_5496_((SoundEvent)AnnoyingVillagersModSounds.HEROBRINE_UNDERSTOOD.get(), 0.5F, 1.0F);
            }

            if (this.secondPossessedHerobrine != null) {
               ((Mob)this.secondPossessedHerobrine).m_7292_(new MobEffectInstance((MobEffect)EpicFightMobEffects.STUN_IMMUNITY.get(), 10, 3, false, false));
               this.clearHandAndDropItem(this.secondPossessedHerobrine);
               if (this.secondPossessedHerobrine instanceof LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntity) {
                  lowShadowHerobrineCloneEntity.setSacrificing(true);
               }

               this.secondPossessedHerobrine.m_7618_(Anchor.EYES, new Vec3(this.m_20185_(), this.m_20186_(), this.m_20189_()));
               this.secondPossessedHerobrine.m_5496_((SoundEvent)AnnoyingVillagersModSounds.HEROBRINE_UNDERSTOOD.get(), 0.5F, 1.0F);
            }

            if (this.thirdPossessedHerobrine != null) {
               ((Mob)this.thirdPossessedHerobrine).m_7292_(new MobEffectInstance((MobEffect)EpicFightMobEffects.STUN_IMMUNITY.get(), 10, 3, false, false));
               this.clearHandAndDropItem(this.thirdPossessedHerobrine);
               if (this.thirdPossessedHerobrine instanceof LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntity) {
                  lowShadowHerobrineCloneEntity.setSacrificing(true);
               }

               this.thirdPossessedHerobrine.m_7618_(Anchor.EYES, new Vec3(this.m_20185_(), this.m_20186_(), this.m_20189_()));
               this.thirdPossessedHerobrine.m_7618_(Anchor.EYES, new Vec3(this.m_20185_(), this.m_20186_(), this.m_20189_()));
               this.thirdPossessedHerobrine.m_5496_((SoundEvent)AnnoyingVillagersModSounds.HEROBRINE_UNDERSTOOD.get(), 0.5F, 1.0F);
            }

            if (this.fourthPossessedHerobrine != null) {
               ((Mob)this.fourthPossessedHerobrine).m_7292_(new MobEffectInstance((MobEffect)EpicFightMobEffects.STUN_IMMUNITY.get(), 10, 3, false, false));
               this.clearHandAndDropItem(this.fourthPossessedHerobrine);
               if (this.fourthPossessedHerobrine instanceof LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntity) {
                  lowShadowHerobrineCloneEntity.setSacrificing(true);
               }

               this.fourthPossessedHerobrine.m_7618_(Anchor.EYES, new Vec3(this.m_20185_(), this.m_20186_(), this.m_20189_()));
               this.fourthPossessedHerobrine.m_5496_((SoundEvent)AnnoyingVillagersModSounds.HEROBRINE_UNDERSTOOD.get(), 0.5F, 1.0F);
            }
         }

         if (this.sacrificing && this.sacrificingAnimationCooldown == 0) {
            if (this.getEmptyBoundClone() == 4) {
               this.m_21557_(false);
               return;
            }

            this.m_7292_(new MobEffectInstance((MobEffect)EpicFightMobEffects.STUN_IMMUNITY.get(), 5, 3, false, false));
            if (this.getLivingEntityPatch() != null) {
               this.getLivingEntityPatch().playAnimationSynchronized(AnimsSculkSteve.HEROBRINE_STAGE_CHANGE, 0.0F);
            }

            if (this instanceof NullEntity nullEntity && this.f_19797_ % 100 == 0) {
               nullEntity.setSpinningToAllWeaponsAvailableFor5seconds();
            }
         }

         if (this.secondFormHitLeft == 0 && this.state == 1) {
            this.state = 0;
         }
      }
   }

   private void playSecondFormSupportCasterAnimations(ServerLevel serverLevel) {
      for (HerobrineGregEntity greg : serverLevel.m_6443_(
         HerobrineGregEntity.class, this.m_20191_().m_82400_(48.0), gregx -> gregx.m_6084_() && gregx.isSupportingSecondFormCaster(this)
      )) {
         greg.playSecondFormSupportCast(this);
      }

      for (TransporterHerobrineCloneEntity transporter : serverLevel.m_6443_(
         TransporterHerobrineCloneEntity.class,
         this.m_20191_().m_82400_(48.0),
         transporterx -> transporterx.m_6084_() && transporterx.isSupportingSecondFormCaster(this)
      )) {
         transporter.playSecondFormSupportCast(this);
      }
   }

   private void clearHandAndDropItem(Entity entity) {
      LivingEntity livingentity = (LivingEntity)entity;
      if (!this.m_9236_().m_5776_()) {
         ItemStack itemstack = livingentity.m_21205_();
         ItemEntity itementity = new ItemEntity(this.m_9236_(), entity.m_20185_(), entity.m_20186_() + 1.0, entity.m_20189_(), itemstack);
         itementity.m_32010_(10);
         this.m_9236_().m_7967_(itementity);
         ((LivingEntity)entity).m_21008_(InteractionHand.MAIN_HAND, ItemStack.f_41583_);
         itemstack = livingentity.m_21206_();
         itementity = new ItemEntity(this.m_9236_(), entity.m_20185_(), entity.m_20186_() + 1.0, entity.m_20189_(), itemstack);
         itementity.m_32010_(10);
         this.m_9236_().m_7967_(itementity);
         ((LivingEntity)entity).m_21008_(InteractionHand.OFF_HAND, ItemStack.f_41583_);
      }
   }

   public void summonClonesForNextStage() {
      if (this.m_9236_() instanceof ServerLevel server) {
         float var15 = this.m_146908_();
         Vec3 forward = Vec3.m_82498_(0.0F, var15).m_82541_();
         Vec3 right = new Vec3(-forward.f_82481_, 0.0, forward.f_82479_).m_82541_();
         double fwdDist = Math.max(3.0, (double)this.m_20205_() * 3.0);
         double sideDist = Math.max(3.0, (double)this.m_20205_() * 3.0);
         double y = this.m_20186_() + 0.01;
         Vec3 posFront = this.m_20182_().m_82549_(forward.m_82490_(fwdDist));
         Vec3 posBack = this.m_20182_().m_82546_(forward.m_82490_(fwdDist));
         Vec3 posLeft = this.m_20182_().m_82546_(right.m_82490_(sideDist));
         Vec3 posRight = this.m_20182_().m_82549_(right.m_82490_(sideDist));
         this.summonLowCloneAt(server, new Vec3(posFront.f_82479_, y, posFront.f_82481_), 1);
         this.summonLowCloneAt(server, new Vec3(posLeft.f_82479_, y, posLeft.f_82481_), 2);
         this.summonLowCloneAt(server, new Vec3(posRight.f_82479_, y, posRight.f_82481_), 3);
         this.summonLowCloneAt(server, new Vec3(posBack.f_82479_, y, posBack.f_82481_), 4);
      }
   }

   private ItemStack randomDamage(ItemStack itemStack) {
      int maxDamage = itemStack.m_41776_();
      itemStack.m_41721_(new Random().nextInt(maxDamage / 3, maxDamage * 3 / 4));
      return itemStack;
   }

   private void equipGearForLowClone(Mob mob) {
      if (this.f_19796_.m_188501_() < 0.3F) {
         mob.m_8061_(EquipmentSlot.HEAD, this.randomDamage(new ItemStack((ItemLike)AnnoyingVillagersModItems.BROKEN_DIAMOND_HELMET.get())));
      }

      if (this.f_19796_.m_188501_() < 0.3F) {
         mob.m_8061_(EquipmentSlot.CHEST, this.randomDamage(new ItemStack((ItemLike)AnnoyingVillagersModItems.BROKEN_DIAMOND_CHESTPLATE.get())));
      }

      if (this.f_19796_.m_188501_() < 0.3F) {
         mob.m_8061_(EquipmentSlot.LEGS, this.randomDamage(new ItemStack((ItemLike)AnnoyingVillagersModItems.BROKEN_DIAMOND_LEGGINGS.get())));
      }

      if (this.f_19796_.m_188501_() < 0.3F) {
         mob.m_8061_(EquipmentSlot.FEET, this.randomDamage(new ItemStack((ItemLike)AnnoyingVillagersModItems.BROKEN_DIAMOND_BOOTS.get())));
      }
   }

   private void summonLowCloneAt(ServerLevel server, Vec3 pos, int bindSlot) {
      LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntity = new LowShadowHerobrineCloneEntity(
         (EntityType)AnnoyingVillagersModEntities.LOW_SHADOW_HEROBRINE_CLONE.get(), server
      );
      int surfaceY = server.m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, BlockPos.m_274446_(pos)).m_123342_();
      lowShadowHerobrineCloneEntity.m_7678_(pos.f_82479_, (double)surfaceY, pos.f_82481_, this.m_146908_(), this.m_146909_());
      lowShadowHerobrineCloneEntity.setRenderPortal(false);
      lowShadowHerobrineCloneEntity.setPossessedByEntity(this);
      lowShadowHerobrineCloneEntity.setPossessedByUuid(this.m_20148_());
      this.equipGearForLowClone(lowShadowHerobrineCloneEntity);
      server.m_7967_(lowShadowHerobrineCloneEntity);
      lowShadowHerobrineCloneEntity.m_21557_(true);
      this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.PORTAL_NATURAL.get(), 1.0F, 1.0F);
      if (bindSlot == 1) {
         this.firstPossessedHerobrine = lowShadowHerobrineCloneEntity;
         this.firstPossessedHerobrineUuid = lowShadowHerobrineCloneEntity.m_20148_();
      } else if (bindSlot == 2) {
         this.secondPossessedHerobrine = lowShadowHerobrineCloneEntity;
         this.secondPossessedHerobrineUuid = lowShadowHerobrineCloneEntity.m_20148_();
      } else if (bindSlot == 3) {
         this.thirdPossessedHerobrine = lowShadowHerobrineCloneEntity;
         this.thirdPossessedHerobrineUuid = lowShadowHerobrineCloneEntity.m_20148_();
      } else {
         this.fourthPossessedHerobrine = lowShadowHerobrineCloneEntity;
         this.fourthPossessedHerobrineUuid = lowShadowHerobrineCloneEntity.m_20148_();
      }
   }

   public void m_142687_(@NotNull RemovalReason reason) {
      super.m_142687_(reason);
      if (!this.m_9236_().f_46443_
         && this.m_9236_() instanceof ServerLevel serverLevel
         && (reason == RemovalReason.KILLED || reason == RemovalReason.DISCARDED)) {
         HerobrineMobData.get(serverLevel).releaseIfMatches(serverLevel, this.m_20148_());
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
         this.initialSpawn = false;
      }

      SpawnGroupData returnSpawnGroupData = super.m_6518_(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
      HerobrineUtil.initialSpawn(serverLevelAccessor, this, this.recallTicks, mobSpawnType);
      return returnSpawnGroupData;
   }

   public void m_5993_(@NotNull Entity entity, int i, @NotNull DamageSource damagesource) {
      super.m_5993_(entity, i, damagesource);
      HerobrineUtil.transformHerobrine(this.m_9236_(), this.m_20185_(), this.m_20186_(), this.m_20189_(), entity, this);
      this.m_5634_(this.m_21233_() / 10.0F);
   }

   public static class AnyFluidPathNavigation extends GroundPathNavigation {
      public AnyFluidPathNavigation(Mob mob, Level level) {
         super(mob, level);
      }

      @NotNull
      protected PathFinder m_5532_(int maxVisitedNodes) {
         this.f_26508_ = new WalkNodeEvaluator();
         this.f_26508_.m_77351_(true);
         return new PathFinder(this.f_26508_, maxVisitedNodes);
      }

      protected boolean m_7367_(@NotNull BlockPathTypes type) {
         return type != BlockPathTypes.WATER
               && type != BlockPathTypes.WATER_BORDER
               && type != BlockPathTypes.LAVA
               && type != BlockPathTypes.DANGER_FIRE
               && type != BlockPathTypes.DAMAGE_FIRE
            ? super.m_7367_(type)
            : true;
      }

      public boolean m_6342_(@NotNull BlockPos blockPos) {
         return this.f_26495_.m_6425_(blockPos).m_76152_() != Fluids.f_76191_ || super.m_6342_(blockPos);
      }
   }
}
