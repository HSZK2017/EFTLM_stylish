package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.config.AnnoyingVillagersConfig;
import com.pla.annoyingvillagers.gameasset.AVAnimations;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.util.HerobrineUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.effect.EpicFightMobEffects;

public class EliteHerobrineKnockedEntity extends PathfinderMob {
   private int wardenCallingCooldown;
   private int eatCount = 0;
   private boolean initialSpawn = false;
   final LivingEntityPatch<?> livingentitypatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(this, LivingEntityPatch.class);
   private final List<Item> listWeapons = new ArrayList<>(
      Arrays.asList(
         Items.f_42388_,
         Items.f_42391_,
         (Item)AnnoyingVillagersModItems.DIAMOND_ATTRACTOR_SWORD.get(),
         (Item)AnnoyingVillagersModItems.DIAMOND_BLASTER_SWORD.get(),
         (Item)AnnoyingVillagersModItems.HOOKED_DIAMOND_SWORD.get(),
         (Item)AnnoyingVillagersModItems.DIAMOND_WARBLADE.get(),
         (Item)AnnoyingVillagersModItems.DIAMOND_FALCHION.get(),
         (Item)AnnoyingVillagersModItems.DIAMOND_GREAT_FALCHION.get(),
         (Item)AnnoyingVillagersModItems.DIAMOND_SABRE.get(),
         (Item)AnnoyingVillagersModItems.DIAMOND_LONGSWORD.get(),
         (Item)AnnoyingVillagersModItems.DIAMOND_CHIPPED_LONGSWORD.get(),
         (Item)AnnoyingVillagersModItems.PALADIN_SWORD.get(),
         (Item)AnnoyingVillagersModItems.DIAMOND_GREATAXE.get(),
         (Item)AnnoyingVillagersModItems.DIAMOND_ARMBLADE.get(),
         (Item)AnnoyingVillagersModItems.DIAMOND_SICKLE.get(),
         (Item)AnnoyingVillagersModItems.DOUBLE_DIAMOND_GLAIVE.get(),
         (Item)AnnoyingVillagersModItems.DIAMOND_MOON_BLADE.get()
      )
   );

   public EliteHerobrineKnockedEntity(SpawnEntity spawnEntity, Level level) {
      this((EntityType<EliteHerobrineKnockedEntity>)AnnoyingVillagersModEntities.ELITE_HEROBRINE_KNOCKED.get(), level);
   }

   public EliteHerobrineKnockedEntity(EntityType<EliteHerobrineKnockedEntity> entitytype, Level level) {
      super(entitytype, level);
      this.m_274367_(0.6F);
      this.f_21364_ = 0;
      this.m_21557_(true);
      this.m_20340_(false);
      this.m_21530_();
      this.m_8061_(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)AnnoyingVillagersModItems.ELITE_OBSIDIAN_LONG.get()));
      this.m_8061_(EquipmentSlot.OFFHAND, new ItemStack((ItemLike)AnnoyingVillagersModItems.ELITE_OBSIDIAN_BIG.get()));
      this.m_8061_(EquipmentSlot.HEAD, new ItemStack((ItemLike)AnnoyingVillagersModItems.ELITE_OBSIDIAN_BODY.get()));
      this.m_21409_(EquipmentSlot.MAINHAND, 0.0F);
      this.m_21409_(EquipmentSlot.OFFHAND, 0.0F);
      this.m_21409_(EquipmentSlot.HEAD, 1.0F);
   }

   public void m_7378_(@NotNull CompoundTag pCompound) {
      super.m_7378_(pCompound);
      this.wardenCallingCooldown = pCompound.m_128451_("WardenCallingCooldown");
      this.eatCount = pCompound.m_128451_("EatCount");
      this.initialSpawn = pCompound.m_128471_("InitialSpawn");
   }

   public void m_7380_(@NotNull CompoundTag pCompound) {
      super.m_7380_(pCompound);
      pCompound.m_128405_("WardenCallingCooldown", this.wardenCallingCooldown);
      pCompound.m_128405_("EatCount", this.eatCount);
      pCompound.m_128379_("InitialSpawn", this.initialSpawn);
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
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

   public SoundEvent m_7975_(@NotNull DamageSource damageSource) {
      return (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "entity.generic.hurt"));
   }

   public boolean m_6469_(DamageSource pSource, float pAmount) {
      if (pSource.m_7639_() instanceof HerobrineWardenEntity) {
         this.eatCount++;
         if (this.m_9236_() instanceof ServerLevel serverLevel) {
            if (this.eatCount == 1 && (Boolean)AnnoyingVillagersConfig.TURN_ON_NPC_VOICE.get()) {
               this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.KNOCKED_ELITE_HEROBRINE_SAY_ON_BEING_EATEN.get(), 0.5F, 1.0F);
            }

            serverLevel.m_5594_(
               null, BlockPos.m_274561_(this.m_20185_(), this.m_20186_(), this.m_20189_()), SoundEvents.f_11912_, SoundSource.NEUTRAL, 1.0F, 1.0F
            );
         }

         if (this.eatCount == 10) {
            this.m_142687_(RemovalReason.DISCARDED);
         }

         return super.m_6469_(pSource, 0.0F);
      } else {
         return pSource.m_276093_(DamageTypes.f_268612_) ? false : super.m_6469_(pSource, 1.0F);
      }
   }

   public SoundEvent m_5592_() {
      return (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "entity.generic.death"));
   }

   private void solidifyFeetAndStandOnTop() {
      if (!this.m_9236_().m_5776_()) {
         BlockPos feet = this.m_20097_();
         FluidState fluid = this.m_9236_().m_6425_(feet);
         if (!fluid.m_76178_()) {
            this.m_9236_().m_46597_(feet, Blocks.f_50723_.m_49966_());
            BlockState bs = this.m_9236_().m_8055_(feet);
            VoxelShape shape = bs.m_60742_(this.m_9236_(), feet, CollisionContext.m_82750_(this));
            double top = shape.m_83281_() ? 1.0 : shape.m_83297_(Axis.Y);
            double surfaceY = (double)feet.m_123342_() + top + 0.001;
            double x = this.m_20185_();
            double z = this.m_20189_();
            this.f_19789_ = 0.0F;
            this.m_20334_(this.m_20184_().f_82479_, 0.0, this.m_20184_().f_82481_);
            this.m_6034_(x, surfaceY, z);
            this.m_6853_(true);

            for (int dx = -1; dx <= 1; dx++) {
               for (int dz = -1; dz <= 1; dz++) {
                  BlockPos around = feet.m_7918_(dx, 0, dz);
                  FluidState fs = this.m_9236_().m_6425_(around);
                  if (!fs.m_76178_()) {
                     this.m_9236_().m_46597_(around, Blocks.f_50723_.m_49966_());
                  }
               }
            }
         }
      }
   }

   public void m_8119_() {
      super.m_8119_();
      if (!this.m_9236_().m_5776_()) {
         if (this.f_19797_ == 1 && !this.initialSpawn) {
            if ((Boolean)AnnoyingVillagersConfig.TURN_ON_NPC_VOICE.get()) {
               this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.KNOCKED_ELITE_HEROBRINE_SAY_ON_SPAWN.get(), 0.5F, 1.0F);
            }

            this.initialSpawn = true;
         }

         this.solidifyFeetAndStandOnTop();
         if (this.wardenCallingCooldown >= 0) {
            this.wardenCallingCooldown--;
         }

         if (this.livingentitypatch != null) {
            if (this.eatCount == 1 || this.eatCount == 2) {
               this.livingentitypatch.playAnimationSynchronized(AVAnimations.EATING_ELITE_1, 0.0F);
            } else if (this.eatCount == 3 || this.eatCount == 4) {
               this.livingentitypatch.playAnimationSynchronized(AVAnimations.EATING_ELITE_2, 0.0F);
            } else if (this.eatCount == 5 || this.eatCount == 6) {
               this.livingentitypatch.playAnimationSynchronized(AVAnimations.EATING_ELITE_3, 0.0F);
            } else if (this.eatCount > 6) {
               this.livingentitypatch.playAnimationSynchronized(AVAnimations.EATING_ELITE_4, 0.0F);
            }
         }

         this.m_7292_(new MobEffectInstance((MobEffect)EpicFightMobEffects.STUN_IMMUNITY.get(), 1, 3, false, false));
         if (this.wardenCallingCooldown == 0) {
            ServerLevel level = (ServerLevel)this.m_9236_();
            HerobrineWardenEntity warden = new HerobrineWardenEntity((EntityType<? extends Monster>)AnnoyingVillagersModEntities.HEROBRINE_WARDEN.get(), level);
            double dist = (double)(this.m_20205_() + warden.m_20205_()) * 0.5 + 0.5;
            Vec3 forward = Vec3.m_82498_(0.0F, this.f_20883_);
            Vec3 spawn = this.m_20182_().m_82546_(forward.m_82490_(dist)).m_82520_(0.0, 0.001, 0.0);

            for (int i = 0; i < 5; i++) {
               warden.m_6034_(spawn.f_82479_, this.m_20186_(), spawn.f_82481_);
               if (level.m_45786_(warden)) {
                  break;
               }

               spawn = spawn.m_82546_(forward.m_82490_(0.3));
            }

            warden.m_7678_(spawn.f_82479_, this.m_20186_(), spawn.f_82481_, this.f_20883_, 0.0F);
            warden.f_20883_ = this.f_20883_;
            warden.m_5616_(this.f_20883_);
            warden.m_6518_(level, level.m_6436_(this.m_20183_()), MobSpawnType.MOB_SUMMONED, null, null);
            warden.setEatingUUID(this.m_20148_());
            level.m_7967_(warden);
         }
      }
   }

   public void m_6667_(@NotNull DamageSource damageSource) {
      super.m_6667_(damageSource);
      if (this.getPersistentData().m_128441_("FromElite")) {
         String fromElite = this.getPersistentData().m_128461_("FromElite");
         HerobrineUtil.dropEliteHerobrineLoot(this.m_9236_(), this.m_20185_(), this.m_20186_(), this.m_20189_(), fromElite);
      }
   }

   private ItemStack randomDamage(ItemStack itemStack) {
      int maxDamage = itemStack.m_41776_();
      itemStack.m_41721_(new Random().nextInt(maxDamage / 3, maxDamage * 3 / 4));
      return itemStack;
   }

   private void equipGearForLowClone(LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntity, boolean randomWeapon) {
      if (this.f_19796_.m_188501_() < 0.3F) {
         lowShadowHerobrineCloneEntity.m_8061_(
            EquipmentSlot.HEAD, this.randomDamage(new ItemStack((ItemLike)AnnoyingVillagersModItems.BROKEN_DIAMOND_HELMET.get()))
         );
      }

      if (this.f_19796_.m_188501_() < 0.3F) {
         lowShadowHerobrineCloneEntity.m_8061_(
            EquipmentSlot.CHEST, this.randomDamage(new ItemStack((ItemLike)AnnoyingVillagersModItems.BROKEN_DIAMOND_CHESTPLATE.get()))
         );
      }

      if (this.f_19796_.m_188501_() < 0.3F) {
         lowShadowHerobrineCloneEntity.m_8061_(
            EquipmentSlot.LEGS, this.randomDamage(new ItemStack((ItemLike)AnnoyingVillagersModItems.BROKEN_DIAMOND_LEGGINGS.get()))
         );
      }

      if (this.f_19796_.m_188501_() < 0.3F) {
         lowShadowHerobrineCloneEntity.m_8061_(
            EquipmentSlot.FEET, this.randomDamage(new ItemStack((ItemLike)AnnoyingVillagersModItems.BROKEN_DIAMOND_BOOTS.get()))
         );
      }

      if (randomWeapon) {
         lowShadowHerobrineCloneEntity.m_8061_(
            EquipmentSlot.MAINHAND, this.randomDamage(new ItemStack((ItemLike)this.listWeapons.get(this.f_19796_.m_188503_(this.listWeapons.size()))))
         );
      }
   }

   @Nullable
   public SpawnGroupData m_6518_(
      @NotNull ServerLevelAccessor pLevel,
      @NotNull DifficultyInstance pDifficulty,
      @NotNull MobSpawnType pReason,
      @Nullable SpawnGroupData pSpawnData,
      @Nullable CompoundTag pDataTag
   ) {
      SpawnGroupData spawnGroupData = super.m_6518_(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
      if (!pLevel.m_5776_()) {
         int d0 = (int)this.m_20185_();
         int d1 = (int)this.m_20186_();
         int d2 = (int)this.m_20189_();
         RandomSource randomSource = RandomSource.m_216327_();
         ServerLevel server = (ServerLevel)this.m_9236_();
         BlockPos feetPos = this.m_20183_();
         int yFeet = feetPos.m_123342_();
         int radius = 2;
         int clearHeight = 3;
         int totalProjectiles = new Random().nextInt(10, 20);

         for (int i = 0; i < totalProjectiles; i++) {
            double x = this.m_20185_() + Mth.m_216263_(randomSource, -1.5, 1.5);
            double y = this.m_20186_() + Mth.m_216263_(randomSource, 1.0, 1.5);
            double z = this.m_20189_() + Mth.m_216263_(randomSource, -1.5, 1.5);
            BlockProjectileEntity blockProjectileEntity = new BlockProjectileEntity(pLevel.m_6018_(), this, Blocks.f_50723_.m_49966_());
            blockProjectileEntity.m_20219_(new Vec3(x, y, z));
            server.m_7967_(blockProjectileEntity);
         }

         server.m_254849_(this, this.m_20185_(), this.m_20186_() + 3.0, this.m_20189_(), 3.0F, ExplosionInteraction.MOB);

         for (int dy = 1; dy <= 3; dy++) {
            int y = yFeet + dy;

            for (int dx = -2; dx <= 2; dx++) {
               for (int dz = -2; dz <= 2; dz++) {
                  BlockPos pos = new BlockPos(feetPos.m_123341_() + dx, y, feetPos.m_123343_() + dz);
                  BlockState state = server.m_8055_(pos);
                  if (!state.m_60795_() && state.m_60800_(server, pos) != -1.0F) {
                     server.m_7731_(pos, Blocks.f_50016_.m_49966_(), 3);
                  }
               }
            }
         }

         double forwardDist = Math.max(1.5, (double)this.m_20205_() * 1.5);
         double sideDist = Math.max(1.2, (double)this.m_20205_() * 1.2);
         float yaw = this.m_146908_();
         Vec3 forward = Vec3.m_82498_(0.0F, yaw).m_82541_();
         Vec3 right = new Vec3(-forward.f_82481_, 0.0, forward.f_82479_);
         Vec3 base = this.m_20182_().m_82549_(forward.m_82490_(forwardDist));
         LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntityLeft = new LowShadowHerobrineCloneEntity(
            (EntityType<LowShadowHerobrineCloneEntity>)AnnoyingVillagersModEntities.LOW_SHADOW_HEROBRINE_CLONE.get(), pLevel.m_6018_()
         );
         lowShadowHerobrineCloneEntityLeft.m_20219_(base.m_82546_(right.m_82490_(sideDist)));
         this.equipGearForLowClone(lowShadowHerobrineCloneEntityLeft, true);
         lowShadowHerobrineCloneEntityLeft.setProtectUUID(this.m_20148_());
         lowShadowHerobrineCloneEntityLeft.setProtectEntity(this);
         pLevel.m_6018_().m_7967_(lowShadowHerobrineCloneEntityLeft);
         LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntityMiddle = new LowShadowHerobrineCloneEntity(
            (EntityType<LowShadowHerobrineCloneEntity>)AnnoyingVillagersModEntities.LOW_SHADOW_HEROBRINE_CLONE.get(), pLevel.m_6018_()
         );
         lowShadowHerobrineCloneEntityMiddle.m_20219_(base);
         this.equipGearForLowClone(lowShadowHerobrineCloneEntityMiddle, false);
         lowShadowHerobrineCloneEntityMiddle.setProtectUUID(this.m_20148_());
         lowShadowHerobrineCloneEntityMiddle.setProtectEntity(this);
         lowShadowHerobrineCloneEntityMiddle.m_21008_(InteractionHand.MAIN_HAND, new ItemStack((ItemLike)AnnoyingVillagersModItems.DIAMOND_CLAW.get()));
         lowShadowHerobrineCloneEntityMiddle.m_21008_(InteractionHand.OFF_HAND, new ItemStack((ItemLike)AnnoyingVillagersModItems.ELITE_OBSIDIAN.get()));
         pLevel.m_6018_().m_7967_(lowShadowHerobrineCloneEntityMiddle);
         LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntityRight = new LowShadowHerobrineCloneEntity(
            (EntityType<LowShadowHerobrineCloneEntity>)AnnoyingVillagersModEntities.LOW_SHADOW_HEROBRINE_CLONE.get(), pLevel.m_6018_()
         );
         lowShadowHerobrineCloneEntityRight.m_20219_(base.m_82549_(right.m_82490_(sideDist)));
         this.equipGearForLowClone(lowShadowHerobrineCloneEntityRight, true);
         lowShadowHerobrineCloneEntityRight.setProtectUUID(this.m_20148_());
         lowShadowHerobrineCloneEntityRight.setProtectEntity(this);
         pLevel.m_6018_().m_7967_(lowShadowHerobrineCloneEntityRight);
         if (this.getPersistentData().m_128441_("FromElite") && this.getPersistentData().m_128461_("FromElite").equals("DemoniacVoltageReaver")) {
            ItemEntity itemEntity = new ItemEntity(
               this.m_9236_(),
               (double)d0 + Mth.m_216263_(randomSource, -5.0, 5.0),
               (double)d1,
               (double)d2 + Mth.m_216263_(randomSource, -5.0, 5.0),
               new ItemStack((ItemLike)AnnoyingVillagersModItems.DEMONIAC_VOLTAGE_REAVER_FRAGMENT.get())
            );
            itemEntity.m_32010_(10);
            pLevel.m_7967_(itemEntity);
            ItemEntity itemEntity1 = new ItemEntity(
               this.m_9236_(),
               (double)d0 + Mth.m_216263_(randomSource, -5.0, 5.0),
               (double)d1,
               (double)d2 + Mth.m_216263_(randomSource, -5.0, 5.0),
               new ItemStack((ItemLike)AnnoyingVillagersModItems.DEMONIAC_VOLTAGE_REAVER_FRAGMENT.get())
            );
            itemEntity1.m_32010_(10);
            pLevel.m_7967_(itemEntity1);
            ItemEntity itemEntity2 = new ItemEntity(
               this.m_9236_(),
               (double)d0 + Mth.m_216263_(randomSource, -5.0, 5.0),
               (double)d1,
               (double)d2 + Mth.m_216263_(randomSource, -5.0, 5.0),
               new ItemStack((ItemLike)AnnoyingVillagersModItems.DEMONIAC_VOLTAGE_REAVER_FRAGMENT.get())
            );
            itemEntity2.m_32010_(10);
            pLevel.m_7967_(itemEntity2);
            ItemEntity itemEntity3 = new ItemEntity(
               this.m_9236_(),
               (double)d0 + Mth.m_216263_(randomSource, -5.0, 5.0),
               (double)d1,
               (double)d2 + Mth.m_216263_(randomSource, -5.0, 5.0),
               new ItemStack((ItemLike)AnnoyingVillagersModItems.DEMONIAC_VOLTAGE_REAVER_BLADE.get())
            );
            itemEntity3.m_32010_(10);
            pLevel.m_7967_(itemEntity3);
         }

         this.wardenCallingCooldown = 1200;
      }

      return spawnGroupData;
   }

   public void m_6075_() {
      super.m_6075_();
   }

   public boolean m_6094_() {
      return false;
   }

   protected void m_7324_(@NotNull Entity other) {
   }

   public void m_7334_(@NotNull Entity other) {
   }

   public void m_147240_(double strength, double x, double z) {
   }

   public static Builder createAttributes() {
      Builder builder = Mob.m_21552_();
      builder = builder.m_22268_(Attributes.f_22279_, 0.06);
      builder = builder.m_22268_(Attributes.f_22276_, 20.0);
      builder = builder.m_22268_(Attributes.f_22284_, 0.0);
      builder = builder.m_22268_(Attributes.f_22281_, 1.0);
      builder = builder.m_22268_(Attributes.f_22277_, 24.0);
      return builder.m_22268_(Attributes.f_22278_, 1.0);
   }
}
