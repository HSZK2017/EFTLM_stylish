package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.clazz.NullWeapon;
import com.pla.annoyingvillagers.gameasset.AnimsWom;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModParticleTypes;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import net.shelmarow.combat_evolution.gameassets.animation.ExecutionAttackAnimation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.gory_moon.player_mobs.utils.NameManager;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class NullEntity extends HerobrineMob {
   private NullWeapon nullSwordEntity;
   private UUID nullSwordUUID;
   private NullWeapon nullAxeEntity;
   private UUID nullAxeUUID;
   private NullWeapon nullPickaxeEntity;
   private UUID nullPickaxeUUID;
   private NullWeapon nullShovelEntity;
   private UUID nullShovelUUID;
   private NullWeapon nullHoeEntity;
   private UUID nullHoeUUID;
   private NullSkeletonEntity firstWitherSkeleton;
   private UUID firstWitherSkeletonUuid;
   private NullSkeletonEntity secondWitherSkeleton;
   private UUID secondWitherSkeletonUuid;
   private boolean spawnNullWeapon = false;

   public boolean isAvailableWitherSkeletonSlot() {
      return this.firstWitherSkeletonUuid == null || this.secondWitherSkeletonUuid == null;
   }

   public NullSkeletonEntity getFirstWitherSkeleton() {
      return this.firstWitherSkeleton;
   }

   public NullSkeletonEntity getSecondWitherSkeleton() {
      return this.secondWitherSkeleton;
   }

   public void claimWitherSkeletonSlot(NullSkeletonEntity witherSkeleton) {
      if (this.firstWitherSkeletonUuid == null) {
         this.firstWitherSkeletonUuid = witherSkeleton.m_20148_();
         this.firstWitherSkeleton = witherSkeleton;
      } else {
         this.secondWitherSkeletonUuid = witherSkeleton.m_20148_();
         this.secondWitherSkeleton = witherSkeleton;
      }
   }

   @Nullable
   public SoundEvent getAttackVoiceSound() {
      return (SoundEvent)AnnoyingVillagersModSounds.NULL_SAY.get();
   }

   public NullWeapon getNullSwordEntity() {
      return this.nullSwordEntity;
   }

   public NullWeapon getNullAxeEntity() {
      return this.nullAxeEntity;
   }

   public NullWeapon getNullPickaxeEntity() {
      return this.nullPickaxeEntity;
   }

   public NullWeapon getNullShovelEntity() {
      return this.nullShovelEntity;
   }

   public NullWeapon getNullHoeEntity() {
      return this.nullHoeEntity;
   }

   public void setNullWeapon(String slot, NullWeapon nullWeapon) {
      switch (slot) {
         case "sword":
            this.nullSwordUUID = nullWeapon.m_20148_();
            this.nullSwordEntity = nullWeapon;
            break;
         case "pickaxe":
            this.nullPickaxeUUID = nullWeapon.m_20148_();
            this.nullPickaxeEntity = nullWeapon;
            break;
         case "axe":
            this.nullAxeUUID = nullWeapon.m_20148_();
            this.nullAxeEntity = nullWeapon;
            break;
         case "hoe":
            this.nullHoeUUID = nullWeapon.m_20148_();
            this.nullHoeEntity = nullWeapon;
            break;
         default:
            this.nullShovelUUID = nullWeapon.m_20148_();
            this.nullShovelEntity = nullWeapon;
      }
   }

   public NullEntity(SpawnEntity spawnEntity, Level level) {
      this((EntityType<NullEntity>)AnnoyingVillagersModEntities.NULL.get(), level);
   }

   public NullEntity(EntityType<NullEntity> entitytype, Level level) {
      super(entitytype, level);
      this.m_274367_(3.0F);
      this.f_21364_ = 80;
      this.m_21557_(false);
      this.m_21530_();
      this.f_21342_ = new FlyingMoveControl(this, 10, true);
      this.setChatName("§5Null§r");
      this.m_21008_(InteractionHand.MAIN_HAND, new ItemStack((ItemLike)AnnoyingVillagersModItems.NULL_WEAPON.get()));
   }

   @NotNull
   @Override
   protected PathNavigation m_6037_(@NotNull Level level) {
      return new FlyingPathNavigation(this, level);
   }

   public void releaseRandomWeapons(int stack) {
      if (stack > 0) {
         List<NullWeapon> weapons = new ArrayList<>(5);
         if (this.nullSwordEntity != null) {
            weapons.add(this.nullSwordEntity);
         }

         if (this.nullAxeEntity != null) {
            weapons.add(this.nullAxeEntity);
         }

         if (this.nullPickaxeEntity != null) {
            weapons.add(this.nullPickaxeEntity);
         }

         if (this.nullShovelEntity != null) {
            weapons.add(this.nullShovelEntity);
         }

         if (this.nullHoeEntity != null) {
            weapons.add(this.nullHoeEntity);
         }

         if (!weapons.isEmpty()) {
            Collections.shuffle(weapons, new Random());

            for (int i = 0; i < Math.min(stack, weapons.size()); i++) {
               weapons.get(i).releaseForAWhile();
            }
         }
      }
   }

   public void randomlyParryWithWeapon(ServerLevel serverLevel, Entity attacker) {
      List<NullWeapon> weapons = new ArrayList<>(5);
      if (this.nullSwordEntity != null && !this.nullSwordEntity.isReleased()) {
         weapons.add(this.nullSwordEntity);
      }

      if (this.nullAxeEntity != null && !this.nullAxeEntity.isReleased()) {
         weapons.add(this.nullAxeEntity);
      }

      if (this.nullPickaxeEntity != null && !this.nullPickaxeEntity.isReleased()) {
         weapons.add(this.nullPickaxeEntity);
      }

      if (this.nullShovelEntity != null && !this.nullShovelEntity.isReleased()) {
         weapons.add(this.nullShovelEntity);
      }

      if (this.nullHoeEntity != null && !this.nullHoeEntity.isReleased()) {
         weapons.add(this.nullHoeEntity);
      }

      if (!weapons.isEmpty()) {
         NullWeapon chosen = weapons.get(this.m_217043_().m_188503_(weapons.size()));
         EpicfightUtil.damageBlockedForce(chosen, attacker, serverLevel);
         chosen.m_6027_(this.m_20185_(), this.m_20186_(), this.m_20189_());
         chosen.spinfor5seconds();
      }
   }

   public void setSpinningToAllWeaponsAvailable(boolean spinning) {
      setSpinningIfAvailable(this.nullSwordEntity, spinning);
      setSpinningIfAvailable(this.nullAxeEntity, spinning);
      setSpinningIfAvailable(this.nullPickaxeEntity, spinning);
      setSpinningIfAvailable(this.nullShovelEntity, spinning);
      setSpinningIfAvailable(this.nullHoeEntity, spinning);
   }

   public void setSpinningToAllWeaponsAvailableFor5seconds() {
      setSpinningFor5SecondsIfAvailable(this.nullSwordEntity);
      setSpinningFor5SecondsIfAvailable(this.nullAxeEntity);
      setSpinningFor5SecondsIfAvailable(this.nullPickaxeEntity);
      setSpinningFor5SecondsIfAvailable(this.nullShovelEntity);
      setSpinningFor5SecondsIfAvailable(this.nullHoeEntity);
   }

   private static void setSpinningIfAvailable(NullWeapon weapon, boolean spinning) {
      if (weapon != null) {
         if (!weapon.isReleased()) {
            weapon.setSpinning(spinning);
         }
      }
   }

   private static void setSpinningFor5SecondsIfAvailable(NullWeapon weapon) {
      if (weapon != null) {
         if (weapon.isReleased()) {
            weapon.stopRelease();
         }

         weapon.spinfor5seconds();
      }
   }

   @Override
   public void m_7380_(@NotNull CompoundTag tag) {
      super.m_7380_(tag);
      if (this.nullSwordUUID != null) {
         tag.m_128362_("NullSwordUUID", this.nullSwordUUID);
      }

      if (this.nullAxeUUID != null) {
         tag.m_128362_("NullAxeUUID", this.nullAxeUUID);
      }

      if (this.nullPickaxeUUID != null) {
         tag.m_128362_("NullPickaxeUUID", this.nullPickaxeUUID);
      }

      if (this.nullShovelUUID != null) {
         tag.m_128362_("NullShovelUUID", this.nullShovelUUID);
      }

      if (this.nullHoeUUID != null) {
         tag.m_128362_("NullHoeUUID", this.nullHoeUUID);
      }

      if (this.firstWitherSkeletonUuid != null) {
         tag.m_128362_("FirstWitherSkeletonUuid", this.firstWitherSkeletonUuid);
      }

      if (this.secondWitherSkeletonUuid != null) {
         tag.m_128362_("SecondWitherSkeletonUuid", this.secondWitherSkeletonUuid);
      }

      tag.m_128379_("SpawnNullWeapon", this.spawnNullWeapon);
   }

   @Override
   public void m_7378_(@NotNull CompoundTag tag) {
      super.m_7378_(tag);
      if (tag.m_128403_("NullSwordUUID")) {
         this.nullSwordUUID = tag.m_128342_("NullSwordUUID");
      }

      if (tag.m_128403_("NullAxeUUID")) {
         this.nullAxeUUID = tag.m_128342_("NullAxeUUID");
      }

      if (tag.m_128403_("NullPickaxeUUID")) {
         this.nullPickaxeUUID = tag.m_128342_("NullPickaxeUUID");
      }

      if (tag.m_128403_("NullShovelUUID")) {
         this.nullShovelUUID = tag.m_128342_("NullShovelUUID");
      }

      if (tag.m_128403_("NullHoeUUID")) {
         this.nullHoeUUID = tag.m_128342_("NullHoeUUID");
      }

      if (tag.m_128403_("FirstWitherSkeletonUuid")) {
         this.firstWitherSkeletonUuid = tag.m_128342_("FirstWitherSkeletonUuid");
      }

      if (tag.m_128403_("SecondWitherSkeletonUuid")) {
         this.secondWitherSkeletonUuid = tag.m_128342_("SecondWitherSkeletonUuid");
      }

      this.spawnNullWeapon = tag.m_128471_("SpawnNullWeapon");
   }

   private void initialSpawn() {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         NullWeapon nullSwordEntity = new NullSwordEntity((EntityType)AnnoyingVillagersModEntities.NULL_SWORD.get(), serverLevel);
         nullSwordEntity.summonNullWeaponForNullEntity(serverLevel, this, "sword");
         NullWeapon nullAxeEntity = new NullAxeEntity((EntityType)AnnoyingVillagersModEntities.NULL_AXE.get(), serverLevel);
         nullAxeEntity.summonNullWeaponForNullEntity(serverLevel, this, "axe");
         NullWeapon nullPickaxeEntity = new NullPickaxeEntity((EntityType)AnnoyingVillagersModEntities.NULL_PICKAXE.get(), serverLevel);
         nullPickaxeEntity.summonNullWeaponForNullEntity(serverLevel, this, "pickaxe");
         NullWeapon nullShovelEntity = new NullShovelEntity((EntityType)AnnoyingVillagersModEntities.NULL_SHOVEL.get(), serverLevel);
         nullShovelEntity.summonNullWeaponForNullEntity(serverLevel, this, "shovel");
         NullWeapon nullHoeEntity = new NullHoeEntity((EntityType)AnnoyingVillagersModEntities.NULL_HOE.get(), serverLevel);
         nullHoeEntity.summonNullWeaponForNullEntity(serverLevel, this, "hoe");
      }
   }

   @Override
   public void m_8119_() {
      super.m_8119_();
      if (!this.m_9236_().m_5776_()) {
         if (!this.spawnNullWeapon) {
            this.spawnNullWeapon = true;
            this.initialSpawn();
         } else if (this.f_19797_ == 20 && this.getLivingEntityPatch() != null) {
            this.getLivingEntityPatch().playAnimationSynchronized(AnimsWom.CLONE_ANTITHEUS_ASCENSION, 0.0F);
         }

         if (this.nullSwordEntity == null && this.nullSwordUUID != null) {
            if (((ServerLevel)this.m_9236_()).m_8791_(this.nullSwordUUID) instanceof NullWeapon nullSword) {
               this.nullSwordEntity = nullSword;
            } else {
               this.nullSwordUUID = null;
            }
         }

         if (this.nullAxeEntity == null && this.nullAxeUUID != null) {
            if (((ServerLevel)this.m_9236_()).m_8791_(this.nullAxeUUID) instanceof NullWeapon nullAxe) {
               this.nullAxeEntity = nullAxe;
            } else {
               this.nullAxeUUID = null;
            }
         }

         if (this.nullPickaxeEntity == null && this.nullPickaxeUUID != null) {
            if (((ServerLevel)this.m_9236_()).m_8791_(this.nullPickaxeUUID) instanceof NullWeapon nullPickaxe) {
               this.nullPickaxeEntity = nullPickaxe;
            } else {
               this.nullPickaxeUUID = null;
            }
         }

         if (this.nullShovelEntity == null && this.nullShovelUUID != null) {
            if (((ServerLevel)this.m_9236_()).m_8791_(this.nullShovelUUID) instanceof NullWeapon nullShovel) {
               this.nullShovelEntity = nullShovel;
            } else {
               this.nullShovelUUID = null;
            }
         }

         if (this.nullHoeEntity == null && this.nullHoeUUID != null) {
            if (((ServerLevel)this.m_9236_()).m_8791_(this.nullHoeUUID) instanceof NullWeapon nullHoe) {
               this.nullHoeEntity = nullHoe;
            } else {
               this.nullHoeUUID = null;
            }
         }

         if (this.firstWitherSkeleton == null && this.firstWitherSkeletonUuid != null) {
            if (((ServerLevel)this.m_9236_()).m_8791_(this.firstWitherSkeletonUuid) instanceof NullSkeletonEntity witherSkeleton) {
               this.firstWitherSkeleton = witherSkeleton;
            } else {
               this.firstWitherSkeletonUuid = null;
            }
         }

         if (this.secondWitherSkeleton == null && this.secondWitherSkeletonUuid != null) {
            if (((ServerLevel)this.m_9236_()).m_8791_(this.secondWitherSkeletonUuid) instanceof NullSkeletonEntity witherSkeleton) {
               this.secondWitherSkeleton = witherSkeleton;
            } else {
               this.secondWitherSkeletonUuid = null;
            }
         }

         if (this.firstWitherSkeleton != null && !this.firstWitherSkeleton.m_6084_()) {
            this.firstWitherSkeleton = null;
            this.firstWitherSkeletonUuid = null;
         }

         if (this.secondWitherSkeleton != null && !this.secondWitherSkeleton.m_6084_()) {
            this.secondWitherSkeleton = null;
            this.secondWitherSkeletonUuid = null;
         }

         if (this.f_19797_ % 10 == 0 && this.f_19797_ >= 20) {
            if (this.nullSwordEntity != null) {
               this.nullSwordEntity.processTeleportByNullEntity();
            }

            if (this.nullAxeEntity != null) {
               this.nullAxeEntity.processTeleportByNullEntity();
            }

            if (this.nullPickaxeEntity != null) {
               this.nullPickaxeEntity.processTeleportByNullEntity();
            }

            if (this.nullHoeEntity != null) {
               this.nullHoeEntity.processTeleportByNullEntity();
            }

            if (this.nullShovelEntity != null) {
               this.nullShovelEntity.processTeleportByNullEntity();
            }
         }
      }
   }

   @Override
   protected void m_8099_() {
      super.m_8099_();
      this.f_21345_.m_25352_(24, new Goal() {
         {
            this.m_7021_(EnumSet.of(Flag.MOVE));
         }

         public boolean m_8036_() {
            return NullEntity.this.m_5448_() != null && !NullEntity.this.m_21566_().m_24995_();
         }

         public boolean m_8045_() {
            return NullEntity.this.m_21566_().m_24995_() && NullEntity.this.m_5448_() != null && NullEntity.this.m_5448_().m_6084_();
         }

         public void m_8056_() {
            LivingEntity livingEntity = NullEntity.this.m_5448_();
            if (livingEntity != null) {
               Vec3 vec3 = livingEntity.m_20299_(1.0F);
               NullEntity.this.f_21342_.m_6849_(vec3.f_82479_, vec3.f_82480_, vec3.f_82481_, 1.0);
            }
         }

         public void m_8037_() {
            LivingEntity livingEntity = NullEntity.this.m_5448_();
            if (livingEntity != null) {
               if (NullEntity.this.m_20191_().m_82381_(livingEntity.m_20191_())) {
                  NullEntity.this.m_7327_(livingEntity);
               } else {
                  double d0 = NullEntity.this.m_20280_(livingEntity);
                  if (d0 < 16.0) {
                     Vec3 vec3 = livingEntity.m_20299_(1.0F);
                     NullEntity.this.f_21342_.m_6849_(vec3.f_82479_, vec3.f_82480_, vec3.f_82481_, 5.0);
                  }
               }
            }
         }
      });
   }

   @Override
   public boolean m_142535_(float f, float f1, @NotNull DamageSource damagesource) {
      return false;
   }

   @Override
   public boolean m_6469_(@NotNull DamageSource damageSource, float f) {
      if (damageSource.m_276093_(DamageTypes.f_268671_)) {
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
      } else if (damageSource.m_276093_(DamageTypes.f_268468_)) {
         return false;
      } else if (damageSource.m_276093_(DamageTypes.f_268631_)) {
         return false;
      } else if (!(damageSource.m_7640_() instanceof EnchantedArrowEntity)
         && damageSource.m_7640_() instanceof AbstractArrow
         && !(damageSource.m_7640_() instanceof BlueDemonThrownTridentEntity)) {
         return false;
      } else if (new Random().nextFloat() <= (this.getState() == 2 ? 0.5F : 0.25F)) {
         if (this.m_9236_() instanceof ServerLevel serverLevel) {
            this.randomlyParryWithWeapon(serverLevel, damageSource.m_7639_());
         }

         return false;
      } else {
         return super.m_6469_(damageSource, f);
      }
   }

   public void m_6667_(@NotNull DamageSource damagesource) {
      super.m_6667_(damagesource);
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         if (this.nullSwordEntity != null) {
            this.nullSwordEntity.m_142687_(RemovalReason.KILLED);
         }

         if (this.nullAxeEntity != null) {
            this.nullAxeEntity.m_142687_(RemovalReason.KILLED);
         }

         if (this.nullHoeEntity != null) {
            this.nullHoeEntity.m_142687_(RemovalReason.KILLED);
         }

         if (this.nullShovelEntity != null) {
            this.nullShovelEntity.m_142687_(RemovalReason.KILLED);
         }

         if (this.nullPickaxeEntity != null) {
            this.nullPickaxeEntity.m_142687_(RemovalReason.KILLED);
         }

         InfectedPlayerNpcEntity corpse = new InfectedPlayerNpcEntity((EntityType)AnnoyingVillagersModEntities.INFECTED_PLAYER_NPC.get(), serverLevel);
         corpse.m_7678_(this.m_20185_(), this.m_20186_(), this.m_20189_(), this.m_146908_(), this.m_146909_());
         String killedName = this.getPersistentData().m_128461_("killed_name");
         corpse.getPersistentData().m_128359_("possessed_by", "null");
         if (killedName.isEmpty()) {
            killedName = String.valueOf(NameManager.INSTANCE.getRandomName());
         }

         corpse.setUsername(killedName);
         corpse.m_6593_(Component.m_237113_(killedName));
         corpse.m_6518_(serverLevel, serverLevel.m_6436_(this.m_20183_()), MobSpawnType.MOB_SUMMONED, null, null);
         this.m_6842_(true);
         this.m_142687_(RemovalReason.KILLED);
         serverLevel.m_7967_(corpse);
      }
   }

   public void m_6075_() {
      super.m_6075_();
      if (this.m_9236_() instanceof ServerLevel) {
         LivingEntityPatch<?> livingEntityPatch = this.getLivingEntityPatch();
         AssetAccessor<? extends StaticAnimation> dynamicAnimation = Animations.EMPTY_ANIMATION;
         if (livingEntityPatch != null) {
            AnimationPlayer animationPlayer = livingEntityPatch.getAnimator().getPlayerFor(null);
            if (animationPlayer != null) {
               dynamicAnimation = animationPlayer.getRealAnimation();
            }
         }

         if (this.m_5448_() != null
            && !EpicfightUtil.isLongHitAnimation(dynamicAnimation, this.getLivingEntityPatch())
            && !this.getLivingEntityPatch().isStunned()
            && !(dynamicAnimation.get() instanceof ExecutionAttackAnimation)) {
            this.m_20256_(new Vec3(this.m_20154_().f_82479_ * 0.2, this.m_20154_().f_82480_ * 0.2, this.m_20154_().f_82481_ * 0.2));
         } else {
            this.m_21573_().m_26573_();
            this.m_20256_(Vec3.f_82478_);
         }
      } else {
         if (this.getLivingEntityPatch() == null) {
            return;
         }

         if (this.getLivingEntityPatch().getAnimator() == null) {
            return;
         }

         if (this.getLivingEntityPatch().getArmature() == null) {
            return;
         }

         if (Armatures.BIPED.get() == null || ((HumanoidArmature)Armatures.BIPED.get()).toolL == null) {
            return;
         }

         if (this.getLivingEntityPatch().getOriginal() == null) {
            return;
         }

         byte poseSampleCount = 3;
         float poseStep = 1.0F / (float)(poseSampleCount - 1);
         float poseProgress = 0.0F;

         for (int poseSampleIndex = 0; poseSampleIndex < poseSampleCount; poseSampleIndex++) {
            Pose pose;
            try {
               pose = this.getLivingEntityPatch().getAnimator().getPose(poseProgress);
            } catch (Throwable var10) {
               return;
            }

            if (pose == null) {
               return;
            }

            OpenMatrix4f toolLeftTransform = this.getLivingEntityPatch()
               .getArmature()
               .getBoundTransformFor(pose, ((HumanoidArmature)Armatures.BIPED.get()).toolL);
            if (toolLeftTransform == null) {
               poseProgress += poseStep;
            } else {
               toolLeftTransform = new OpenMatrix4f(toolLeftTransform);
               toolLeftTransform.translate(new Vec3f(0.0F, 0.0F, 0.0F));
               OpenMatrix4f.mul(
                  new OpenMatrix4f()
                     .rotate(
                        -((float)Math.toRadians((double)(((LivingEntity)this.getLivingEntityPatch().getOriginal()).f_20884_ + 180.0F))),
                        new Vec3f(0.0F, 1.0F, 0.0F)
                     ),
                  toolLeftTransform,
                  toolLeftTransform
               );

               for (int particleIndex = 0; particleIndex < 1; particleIndex++) {
                  ((LivingEntity)this.getLivingEntityPatch().getOriginal())
                     .m_9236_()
                     .m_7106_(
                        (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                        (double)toolLeftTransform.m30 + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20185_(),
                        (double)toolLeftTransform.m31 + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20186_(),
                        (double)toolLeftTransform.m32 + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20189_(),
                        (double)((new Random().nextFloat() - 0.5F) * 0.15F),
                        (double)((new Random().nextFloat() - 0.5F) * 0.15F),
                        (double)((new Random().nextFloat() - 0.5F) * 0.15F)
                     );
               }

               for (int var26 = 0; var26 < 1; var26++) {
                  ((LivingEntity)this.getLivingEntityPatch().getOriginal())
                     .m_9236_()
                     .m_7106_(
                        (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                        (double)toolLeftTransform.m30 + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20185_(),
                        (double)toolLeftTransform.m31 + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20186_(),
                        (double)toolLeftTransform.m32 + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20189_(),
                        0.0,
                        0.0,
                        0.0
                     );
               }

               poseProgress += poseStep;
            }
         }

         poseProgress = 0.0F;

         for (int var28 = 0; var28 < poseSampleCount; var28++) {
            OpenMatrix4f jointTransform = this.getLivingEntityPatch()
               .getArmature()
               .getBoundTransformFor(this.getLivingEntityPatch().getAnimator().getPose(poseProgress), ((HumanoidArmature)Armatures.BIPED.get()).toolR);
            jointTransform.translate(new Vec3f(0.0F, 0.0F, 1.8F));
            OpenMatrix4f.mul(
               new OpenMatrix4f()
                  .rotate(
                     -((float)Math.toRadians((double)(((LivingEntity)this.getLivingEntityPatch().getOriginal()).f_20884_ + 180.0F))),
                     new Vec3f(0.0F, 1.0F, 0.0F)
                  ),
               jointTransform,
               jointTransform
            );
            jointTransform.translate(new Vec3f(0.0F, 0.0F, -(new Random().nextFloat() * 4.0F)));
            ((LivingEntity)this.getLivingEntityPatch().getOriginal())
               .m_9236_()
               .m_7106_(
                  (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                  (double)jointTransform.m30 + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20185_(),
                  (double)jointTransform.m31 + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20186_(),
                  (double)jointTransform.m32 + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20189_(),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F)
               );
            ((LivingEntity)this.getLivingEntityPatch().getOriginal())
               .m_9236_()
               .m_7106_(
                  (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                  (double)jointTransform.m30 + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20185_(),
                  (double)jointTransform.m31 + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20186_(),
                  (double)jointTransform.m32 + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20189_(),
                  0.0,
                  0.0,
                  0.0
               );
            poseProgress += poseStep;
         }

         for (int particleIndex = 0; particleIndex < 14; particleIndex++) {
            ((LivingEntity)this.getLivingEntityPatch().getOriginal())
               .m_9236_()
               .m_7106_(
                  (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                  ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20185_(),
                  ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20186_() + 0.03F,
                  ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20189_(),
                  (double)((new Random().nextFloat() - 0.5F) * 0.65F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.05F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.65F)
               );
         }

         poseStep = 1.0F;
         poseProgress = 0.0F;

         for (int var29 = 0; var29 < poseSampleCount; var29++) {
            OpenMatrix4f jointTransform = this.getLivingEntityPatch()
               .getArmature()
               .getBoundTransformFor(this.getLivingEntityPatch().getAnimator().getPose(poseProgress), ((HumanoidArmature)Armatures.BIPED.get()).head);
            jointTransform.translate(new Vec3f(0.0F, 0.0F, 0.0F));
            OpenMatrix4f.mul(
               new OpenMatrix4f()
                  .rotate(
                     -((float)Math.toRadians((double)(((LivingEntity)this.getLivingEntityPatch().getOriginal()).f_20884_ + 180.0F))),
                     new Vec3f(0.0F, 1.0F, 0.0F)
                  ),
               jointTransform,
               jointTransform
            );
            ((LivingEntity)this.getLivingEntityPatch().getOriginal())
               .m_9236_()
               .m_7106_(
                  (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                  (double)jointTransform.m30
                     + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20185_()
                     + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m31
                     + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20186_()
                     + (double)((new Random().nextFloat() + 0.1F) * 0.55F),
                  (double)jointTransform.m32
                     + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20189_()
                     + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F),
                  (double)((new Random().nextFloat() - 1.0F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F)
               );
            poseProgress += poseStep;
         }

         poseProgress = 0.0F;

         for (int var30 = 0; var30 < poseSampleCount; var30++) {
            OpenMatrix4f jointTransform = this.getLivingEntityPatch()
               .getArmature()
               .getBoundTransformFor(this.getLivingEntityPatch().getAnimator().getPose(poseProgress), ((HumanoidArmature)Armatures.BIPED.get()).chest);
            jointTransform.translate(new Vec3f(0.0F, 0.0F, 0.0F));
            OpenMatrix4f.mul(
               new OpenMatrix4f()
                  .rotate(
                     -((float)Math.toRadians((double)(((LivingEntity)this.getLivingEntityPatch().getOriginal()).f_20884_ + 180.0F))),
                     new Vec3f(0.0F, 1.0F, 0.0F)
                  ),
               jointTransform,
               jointTransform
            );
            ((LivingEntity)this.getLivingEntityPatch().getOriginal())
               .m_9236_()
               .m_7106_(
                  (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                  (double)jointTransform.m30
                     + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20185_()
                     + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m31
                     + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20186_()
                     + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m32
                     + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20189_()
                     + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F),
                  (double)((new Random().nextFloat() - 1.0F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F)
               );
            poseProgress += poseStep;
         }

         poseProgress = 0.0F;

         for (int var31 = 0; var31 < poseSampleCount; var31++) {
            OpenMatrix4f jointTransform = this.getLivingEntityPatch()
               .getArmature()
               .getBoundTransformFor(this.getLivingEntityPatch().getAnimator().getPose(poseProgress), ((HumanoidArmature)Armatures.BIPED.get()).armL);
            jointTransform.translate(new Vec3f(0.0F, 0.0F, 0.0F));
            OpenMatrix4f.mul(
               new OpenMatrix4f()
                  .rotate(
                     -((float)Math.toRadians((double)(((LivingEntity)this.getLivingEntityPatch().getOriginal()).f_20884_ + 180.0F))),
                     new Vec3f(0.0F, 1.0F, 0.0F)
                  ),
               jointTransform,
               jointTransform
            );
            ((LivingEntity)this.getLivingEntityPatch().getOriginal())
               .m_9236_()
               .m_7106_(
                  (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                  (double)jointTransform.m30
                     + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20185_()
                     + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m31
                     + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20186_()
                     + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m32
                     + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20189_()
                     + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F),
                  (double)((new Random().nextFloat() - 1.0F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F)
               );
            poseProgress += poseStep;
         }

         poseProgress = 0.0F;

         for (int var32 = 0; var32 < poseSampleCount; var32++) {
            OpenMatrix4f jointTransform = this.getLivingEntityPatch()
               .getArmature()
               .getBoundTransformFor(this.getLivingEntityPatch().getAnimator().getPose(poseProgress), ((HumanoidArmature)Armatures.BIPED.get()).armR);
            jointTransform.translate(new Vec3f(0.0F, 0.0F, 0.0F));
            OpenMatrix4f.mul(
               new OpenMatrix4f()
                  .rotate(
                     -((float)Math.toRadians((double)(((LivingEntity)this.getLivingEntityPatch().getOriginal()).f_20884_ + 180.0F))),
                     new Vec3f(0.0F, 1.0F, 0.0F)
                  ),
               jointTransform,
               jointTransform
            );
            ((LivingEntity)this.getLivingEntityPatch().getOriginal())
               .m_9236_()
               .m_7106_(
                  (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                  (double)jointTransform.m30
                     + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20185_()
                     + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m31
                     + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20186_()
                     + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m32
                     + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20189_()
                     + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F),
                  (double)((new Random().nextFloat() - 1.0F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F)
               );
            poseProgress += poseStep;
         }

         poseProgress = 0.0F;

         for (int var33 = 0; var33 < poseSampleCount; var33++) {
            OpenMatrix4f jointTransform = this.getLivingEntityPatch()
               .getArmature()
               .getBoundTransformFor(this.getLivingEntityPatch().getAnimator().getPose(poseProgress), ((HumanoidArmature)Armatures.BIPED.get()).torso);
            jointTransform.translate(new Vec3f(0.0F, 0.0F, 0.0F));
            OpenMatrix4f.mul(
               new OpenMatrix4f()
                  .rotate(
                     -((float)Math.toRadians((double)(((LivingEntity)this.getLivingEntityPatch().getOriginal()).f_20884_ + 180.0F))),
                     new Vec3f(0.0F, 1.0F, 0.0F)
                  ),
               jointTransform,
               jointTransform
            );
            ((LivingEntity)this.getLivingEntityPatch().getOriginal())
               .m_9236_()
               .m_7106_(
                  (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                  (double)jointTransform.m30
                     + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20185_()
                     + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m31
                     + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20186_()
                     + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m32
                     + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20189_()
                     + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F),
                  (double)((new Random().nextFloat() - 1.0F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F)
               );
            poseProgress += poseStep;
         }

         poseProgress = 0.0F;

         for (int var34 = 0; var34 < poseSampleCount; var34++) {
            OpenMatrix4f jointTransform = this.getLivingEntityPatch()
               .getArmature()
               .getBoundTransformFor(this.getLivingEntityPatch().getAnimator().getPose(poseProgress), ((HumanoidArmature)Armatures.BIPED.get()).thighL);
            jointTransform.translate(new Vec3f(0.0F, 0.0F, 0.0F));
            OpenMatrix4f.mul(
               new OpenMatrix4f()
                  .rotate(
                     -((float)Math.toRadians((double)(((LivingEntity)this.getLivingEntityPatch().getOriginal()).f_20884_ + 180.0F))),
                     new Vec3f(0.0F, 1.0F, 0.0F)
                  ),
               jointTransform,
               jointTransform
            );
            ((LivingEntity)this.getLivingEntityPatch().getOriginal())
               .m_9236_()
               .m_7106_(
                  (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                  (double)jointTransform.m30
                     + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20185_()
                     + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m31
                     + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20186_()
                     + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m32
                     + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20189_()
                     + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F),
                  (double)((new Random().nextFloat() - 1.0F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F)
               );
            poseProgress += poseStep;
         }

         poseProgress = 0.0F;

         for (int var35 = 0; var35 < poseSampleCount; var35++) {
            OpenMatrix4f jointTransform = this.getLivingEntityPatch()
               .getArmature()
               .getBoundTransformFor(this.getLivingEntityPatch().getAnimator().getPose(poseProgress), ((HumanoidArmature)Armatures.BIPED.get()).thighR);
            jointTransform.translate(new Vec3f(0.0F, 0.0F, 0.0F));
            OpenMatrix4f.mul(
               new OpenMatrix4f()
                  .rotate(
                     -((float)Math.toRadians((double)(((LivingEntity)this.getLivingEntityPatch().getOriginal()).f_20884_ + 180.0F))),
                     new Vec3f(0.0F, 1.0F, 0.0F)
                  ),
               jointTransform,
               jointTransform
            );
            ((LivingEntity)this.getLivingEntityPatch().getOriginal())
               .m_9236_()
               .m_7106_(
                  (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                  (double)jointTransform.m30
                     + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20185_()
                     + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m31
                     + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20186_()
                     + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m32
                     + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20189_()
                     + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F),
                  (double)((new Random().nextFloat() - 1.0F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F)
               );
            poseProgress += poseStep;
         }

         poseProgress = 0.0F;

         for (int var36 = 0; var36 < poseSampleCount; var36++) {
            OpenMatrix4f jointTransform = this.getLivingEntityPatch()
               .getArmature()
               .getBoundTransformFor(this.getLivingEntityPatch().getAnimator().getPose(poseProgress), ((HumanoidArmature)Armatures.BIPED.get()).legL);
            jointTransform.translate(new Vec3f(0.0F, 0.0F, 0.0F));
            OpenMatrix4f.mul(
               new OpenMatrix4f()
                  .rotate(
                     -((float)Math.toRadians((double)(((LivingEntity)this.getLivingEntityPatch().getOriginal()).f_20884_ + 180.0F))),
                     new Vec3f(0.0F, 1.0F, 0.0F)
                  ),
               jointTransform,
               jointTransform
            );
            ((LivingEntity)this.getLivingEntityPatch().getOriginal())
               .m_9236_()
               .m_7106_(
                  (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                  (double)jointTransform.m30
                     + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20185_()
                     + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m31
                     + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20186_()
                     + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m32
                     + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20189_()
                     + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F),
                  (double)((new Random().nextFloat() - 1.0F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F)
               );
            poseProgress += poseStep;
         }

         poseProgress = 0.0F;

         for (int var37 = 0; var37 < poseSampleCount; var37++) {
            OpenMatrix4f jointTransform = this.getLivingEntityPatch()
               .getArmature()
               .getBoundTransformFor(this.getLivingEntityPatch().getAnimator().getPose(poseProgress), ((HumanoidArmature)Armatures.BIPED.get()).legR);
            jointTransform.translate(new Vec3f(0.0F, 0.0F, 0.0F));
            OpenMatrix4f.mul(
               new OpenMatrix4f()
                  .rotate(
                     -((float)Math.toRadians((double)(((LivingEntity)this.getLivingEntityPatch().getOriginal()).f_20884_ + 180.0F))),
                     new Vec3f(0.0F, 1.0F, 0.0F)
                  ),
               jointTransform,
               jointTransform
            );
            ((LivingEntity)this.getLivingEntityPatch().getOriginal())
               .m_9236_()
               .m_7106_(
                  (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                  (double)jointTransform.m30
                     + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20185_()
                     + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m31
                     + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20186_()
                     + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m32
                     + ((LivingEntity)this.getLivingEntityPatch().getOriginal()).m_20189_()
                     + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F),
                  (double)((new Random().nextFloat() - 1.0F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F)
               );
            poseProgress += poseStep;
         }
      }
   }

   protected void m_7840_(double d0, boolean flag, @NotNull BlockState blockstate, @NotNull BlockPos blockpos) {
   }

   public void m_20242_(boolean flag) {
      super.m_20242_(true);
   }

   @Override
   public void m_142687_(@NotNull RemovalReason pReason) {
      if (this.m_9236_() instanceof ServerLevel serverLevel && pReason.equals(RemovalReason.DISCARDED)) {
         if (this.nullSwordEntity != null) {
            this.nullSwordEntity.m_142687_(RemovalReason.DISCARDED);
         }

         if (this.nullAxeEntity != null) {
            this.nullAxeEntity.m_142687_(RemovalReason.DISCARDED);
         }

         if (this.nullHoeEntity != null) {
            this.nullHoeEntity.m_142687_(RemovalReason.DISCARDED);
         }

         if (this.nullShovelEntity != null) {
            this.nullShovelEntity.m_142687_(RemovalReason.DISCARDED);
         }

         if (this.nullPickaxeEntity != null) {
            this.nullPickaxeEntity.m_142687_(RemovalReason.DISCARDED);
         }
      }

      super.m_142687_(pReason);
   }

   public static Builder createAttributes() {
      return Mob.m_21552_()
         .m_22268_(Attributes.f_22276_, 250.0)
         .m_22268_(Attributes.f_22279_, 3.0)
         .m_22268_(Attributes.f_22280_, 3.0)
         .m_22268_(Attributes.f_22281_, 10.0)
         .m_22268_(Attributes.f_22277_, 64.0)
         .m_22268_(Attributes.f_22284_, 10.0)
         .m_22268_(Attributes.f_22285_, 20.0)
         .m_22268_(Attributes.f_22278_, 1.0)
         .m_22268_((Attribute)EpicFightAttributes.IMPACT.get(), 4.0)
         .m_22268_((Attribute)EpicFightAttributes.ARMOR_NEGATION.get(), 10.0)
         .m_22268_((Attribute)EpicFightAttributes.STUN_ARMOR.get(), 20.0)
         .m_22268_((Attribute)EpicFightAttributes.MAX_STRIKES.get(), 100.0)
         .m_22268_((Attribute)EpicFightAttributes.MAX_STAMINA.get(), 60.0)
         .m_22268_((Attribute)EpicFightAttributes.STAMINA_REGEN.get(), 1.5);
   }
}
