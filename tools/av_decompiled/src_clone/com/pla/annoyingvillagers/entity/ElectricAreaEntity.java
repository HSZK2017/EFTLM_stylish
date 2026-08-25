package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModMobEffects;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModParticleTypes;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class ElectricAreaEntity extends Entity {
   private static final String TAG_OWNER_UUID = "OwnerUUID";
   private static final String TAG_HALF_SIZE = "HalfSize";
   private static final String TAG_DURATION_TICKS = "DurationTicks";
   private static final String TAG_DAMAGE_AMOUNT = "DamageAmount";
   private static final String TAG_DAMAGE_INTERVAL = "DamageInterval";
   @Nullable
   private UUID ownerUUID;
   private double halfSize = 1.5;
   private int durationTicks = 100;
   private float damageAmount = 4.0F;
   private int damageInterval = 10;

   public ElectricAreaEntity(EntityType<? extends ElectricAreaEntity> type, Level level) {
      super(type, level);
      this.f_19794_ = true;
      this.m_20242_(true);
   }

   public ElectricAreaEntity(Level level, LivingEntity owner, Vec3 pos, double halfSize, int durationTicks, float damageAmount, int damageInterval) {
      this((EntityType<? extends ElectricAreaEntity>)AnnoyingVillagersModEntities.ELECTRIC_AREA.get(), level);
      this.ownerUUID = owner.m_20148_();
      this.halfSize = halfSize;
      this.durationTicks = durationTicks;
      this.damageAmount = damageAmount;
      this.damageInterval = Math.max(1, damageInterval);
      this.m_6034_(pos.f_82479_, pos.f_82480_, pos.f_82481_);
   }

   @Nullable
   public LivingEntity getOwnerLiving() {
      if (this.m_9236_() instanceof ServerLevel serverLevel && this.ownerUUID != null) {
         return serverLevel.m_8791_(this.ownerUUID) instanceof LivingEntity living ? living : null;
      }

      return null;
   }

   protected void m_8097_() {
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         if (this.f_19797_ % this.damageInterval == 0 || this.f_19797_ == 1) {
            this.damageEntitiesInZone();
         }

         if (this.f_19797_ % 5 == 0) {
            serverLevel.m_8767_(
               (SimpleParticleType)AnnoyingVillagersModParticleTypes.ELECTRIC_SPARK.get(),
               this.m_20185_(),
               this.m_20186_() + 0.2,
               this.m_20189_(),
               4,
               this.halfSize * 0.6,
               0.25,
               this.halfSize * 0.6,
               0.0
            );
         }

         if (this.f_19797_ >= this.durationTicks) {
            this.m_146870_();
         }
      }
   }

   private void damageEntitiesInZone() {
      LivingEntity owner = this.getOwnerLiving();

      for (LivingEntity target : this.m_9236_().m_6443_(LivingEntity.class, this.makeDamageBox(), living -> this.isValidTarget(owner, living))) {
         target.m_20334_(0.0, 0.0, 0.0);
         target.m_7292_(new MobEffectInstance((MobEffect)AnnoyingVillagersModMobEffects.ELECTRIFY.get(), 12, 2));
      }
   }

   private boolean isValidTarget(@Nullable LivingEntity owner, LivingEntity target) {
      if (!target.m_6084_() || target.m_5833_()) {
         return false;
      } else if (owner == null) {
         return true;
      } else if (target == owner) {
         return false;
      } else {
         if (target instanceof Player player && player.m_7500_()) {
            return false;
         }

         if (owner instanceof BlueDemonEntity blueDemonEntity && blueDemonEntity.getBbqEntity() != null && target == blueDemonEntity.getBbqEntity()) {
            return false;
         }

         return !owner.m_7307_(target);
      }
   }

   private AABB makeDamageBox() {
      return new AABB(
         this.m_20185_() - this.halfSize,
         this.m_20186_() - 1.0,
         this.m_20189_() - this.halfSize,
         this.m_20185_() + this.halfSize,
         this.m_20186_() + 2.5,
         this.m_20189_() + this.halfSize
      );
   }

   protected void m_7378_(@NotNull CompoundTag tag) {
      if (tag.m_128403_("OwnerUUID")) {
         this.ownerUUID = tag.m_128342_("OwnerUUID");
      }

      this.halfSize = tag.m_128459_("HalfSize");
      this.durationTicks = tag.m_128451_("DurationTicks");
      this.damageAmount = tag.m_128457_("DamageAmount");
      this.damageInterval = Math.max(1, tag.m_128451_("DamageInterval"));
   }

   protected void m_7380_(@NotNull CompoundTag tag) {
      if (this.ownerUUID != null) {
         tag.m_128362_("OwnerUUID", this.ownerUUID);
      }

      tag.m_128347_("HalfSize", this.halfSize);
      tag.m_128405_("DurationTicks", this.durationTicks);
      tag.m_128350_("DamageAmount", this.damageAmount);
      tag.m_128405_("DamageInterval", this.damageInterval);
   }

   public boolean m_6469_(@NotNull DamageSource source, float amount) {
      return false;
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }
}
