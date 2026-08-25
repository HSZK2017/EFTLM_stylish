package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DiamondBoltProjectileEntity extends AbstractArrow implements ItemSupplier {
   private static final EntityDataAccessor<ItemStack> DATA_STACK = SynchedEntityData.m_135353_(
      DiamondBoltProjectileEntity.class, EntityDataSerializers.f_135033_
   );
   private static final EntityDataAccessor<Byte> ID_LOYALTY = SynchedEntityData.m_135353_(DiamondBoltProjectileEntity.class, EntityDataSerializers.f_135027_);
   private static final EntityDataAccessor<Boolean> ID_FOIL = SynchedEntityData.m_135353_(DiamondBoltProjectileEntity.class, EntityDataSerializers.f_135035_);
   public boolean dealtDamage;
   public int clientSideReturnTickCount;

   public DiamondBoltProjectileEntity(EntityType<? extends DiamondBoltProjectileEntity> type, Level level) {
      super(type, level);
   }

   public DiamondBoltProjectileEntity(SpawnEntity packet, Level level) {
      this((EntityType<? extends DiamondBoltProjectileEntity>)AnnoyingVillagersModEntities.DIAMOND_BOLT_PROJECTILE.get(), level);
   }

   public DiamondBoltProjectileEntity(Level level, LivingEntity shooter, ItemStack stack) {
      super((EntityType)AnnoyingVillagersModEntities.DIAMOND_BOLT_PROJECTILE.get(), shooter, level);
      this.setThrownStack(stack);
      this.f_19804_.m_135381_(ID_LOYALTY, (byte)EnchantmentHelper.m_44928_(stack));
      this.f_19804_.m_135381_(ID_FOIL, stack.m_41790_());
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(DATA_STACK, ItemStack.f_41583_);
      this.f_19804_.m_135372_(ID_LOYALTY, (byte)0);
      this.f_19804_.m_135372_(ID_FOIL, false);
   }

   public void m_8119_() {
      if (this.f_36704_ > 4) {
         this.dealtDamage = true;
      }

      Entity owner = this.m_19749_();
      int loyalty = (Byte)this.f_19804_.m_135370_(ID_LOYALTY);
      if (loyalty > 0 && (this.dealtDamage || this.m_36797_()) && owner != null) {
         if (!this.isAcceptableReturnOwner()) {
            if (!this.m_9236_().f_46443_ && this.f_36705_ == Pickup.ALLOWED) {
               this.m_5552_(this.m_7941_(), 0.1F);
            }

            this.m_146870_();
         } else {
            this.m_36790_(true);
            Vec3 returnVector = owner.m_146892_().m_82546_(this.m_20182_());
            this.m_20343_(this.m_20185_(), this.m_20186_() + returnVector.f_82480_ * 0.015 * (double)loyalty, this.m_20189_());
            if (this.m_9236_().f_46443_) {
               this.f_19791_ = this.m_20186_();
            }

            double returnSpeed = 0.05 * (double)loyalty;
            this.m_20256_(this.m_20184_().m_82490_(0.95).m_82549_(returnVector.m_82541_().m_82490_(returnSpeed)));
            if (this.clientSideReturnTickCount == 0) {
               this.m_5496_(SoundEvents.f_12516_, 10.0F, 1.0F);
            }

            this.clientSideReturnTickCount++;
         }
      }

      super.m_8119_();
   }

   private boolean isAcceptableReturnOwner() {
      Entity owner = this.m_19749_();
      return owner != null && owner.m_6084_() && (!(owner instanceof ServerPlayer serverPlayer) || !serverPlayer.m_5833_());
   }

   @NotNull
   public ItemStack m_7941_() {
      return this.getThrownStack().m_41777_();
   }

   @NotNull
   public ItemStack m_7846_() {
      return this.m_7941_();
   }

   public boolean isFoil() {
      return (Boolean)this.f_19804_.m_135370_(ID_FOIL);
   }

   @Nullable
   protected EntityHitResult m_6351_(@NotNull Vec3 start, @NotNull Vec3 end) {
      return this.dealtDamage ? null : super.m_6351_(start, end);
   }

   protected void m_8060_(@NotNull BlockHitResult result) {
      super.m_8060_(result);
      this.dealtDamage = true;
   }

   protected void m_5790_(@NotNull EntityHitResult result) {
      Entity target = result.m_82443_();
      float damage = 8.0F;
      ItemStack thrownStack = this.getThrownStack();
      if (target instanceof LivingEntity livingTarget) {
         damage += EnchantmentHelper.m_44833_(thrownStack, livingTarget.m_6336_());
      }

      Entity owner = this.m_19749_();
      DamageSource damageSource = this.m_269291_().m_269525_(this, (Entity)(owner == null ? this : owner));
      this.dealtDamage = true;
      SoundEvent hitSound = SoundEvents.f_12514_;
      float soundVolume = 1.0F;
      if (target.m_6469_(damageSource, damage)) {
         if (target.m_6095_() == EntityType.f_20566_) {
            return;
         }

         if (target instanceof LivingEntity livingTarget) {
            if (owner instanceof LivingEntity livingOwner) {
               EnchantmentHelper.m_44823_(livingTarget, owner);
               EnchantmentHelper.m_44896_(livingOwner, livingTarget);
            }

            this.m_7761_(livingTarget);
         }
      }

      this.m_20256_(this.m_20184_().m_82542_(-0.01, -0.1, -0.01));
      if (this.m_9236_() instanceof ServerLevel && this.m_9236_().m_46470_() && this.isChanneling()) {
         BlockPos blockPos = target.m_20183_();
         if (this.m_9236_().m_45527_(blockPos)) {
            LightningBolt lightningBolt = (LightningBolt)EntityType.f_20465_.m_20615_(this.m_9236_());
            if (lightningBolt != null) {
               lightningBolt.m_20219_(Vec3.m_82539_(blockPos));
               lightningBolt.m_20879_(owner instanceof ServerPlayer serverPlayer ? serverPlayer : null);
               this.m_9236_().m_7967_(lightningBolt);
               hitSound = SoundEvents.f_12521_;
               soundVolume = 5.0F;
            }
         }
      }

      this.m_5496_(hitSound, soundVolume, 1.0F);
   }

   public boolean isChanneling() {
      return EnchantmentHelper.m_44936_(this.getThrownStack());
   }

   protected boolean m_142470_(@NotNull Player player) {
      return super.m_142470_(player) || this.m_36797_() && this.m_150171_(player) && player.m_150109_().m_36054_(this.m_7941_());
   }

   @NotNull
   protected SoundEvent m_7239_() {
      return SoundEvents.f_12515_;
   }

   public void m_6123_(@NotNull Player player) {
      if (!this.m_9236_().f_46443_ && (this.f_36703_ || this.m_36797_() || this.dealtDamage) && this.f_36706_ <= 0 && this.m_142470_(player)) {
         player.m_7938_(this, 1);
         this.m_146870_();
      }
   }

   public void m_7378_(@NotNull CompoundTag tag) {
      super.m_7378_(tag);
      if (tag.m_128425_("DiamondBolt", 10)) {
         this.setThrownStack(ItemStack.m_41712_(tag.m_128469_("DiamondBolt")));
      }

      this.dealtDamage = tag.m_128471_("DealtDamage");
      ItemStack thrownStack = this.getThrownStack();
      this.f_19804_.m_135381_(ID_LOYALTY, (byte)EnchantmentHelper.m_44928_(thrownStack));
      this.f_19804_.m_135381_(ID_FOIL, thrownStack.m_41790_());
   }

   public void m_7380_(@NotNull CompoundTag tag) {
      super.m_7380_(tag);
      tag.m_128365_("DiamondBolt", this.getThrownStack().m_41739_(new CompoundTag()));
      tag.m_128379_("DealtDamage", this.dealtDamage);
   }

   public void m_6901_() {
      int loyalty = (Byte)this.f_19804_.m_135370_(ID_LOYALTY);
      if (this.f_36705_ != Pickup.ALLOWED || loyalty <= 0) {
         super.m_6901_();
      }
   }

   protected float m_6882_() {
      return 0.99F;
   }

   public boolean m_6000_(double x, double y, double z) {
      return true;
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   private void setThrownStack(ItemStack stack) {
      this.f_19804_.m_135381_(DATA_STACK, stack.m_41777_());
   }

   private ItemStack getThrownStack() {
      ItemStack stack = (ItemStack)this.f_19804_.m_135370_(DATA_STACK);
      return stack.m_41619_() ? new ItemStack((ItemLike)AnnoyingVillagersModItems.DIAMOND_BOLT.get()) : stack;
   }
}
