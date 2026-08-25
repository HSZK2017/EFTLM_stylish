package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import org.jetbrains.annotations.NotNull;

public class ThrownPoisonEggEntity extends ThrowableItemProjectile {
   public ThrownPoisonEggEntity(SpawnEntity spawnentity, Level level) {
      super((EntityType)AnnoyingVillagersModEntities.THROWN_POISON_EGG.get(), level);
   }

   public ThrownPoisonEggEntity(EntityType<? extends ThrownPoisonEggEntity> entitytype, Level level) {
      super(entitytype, level);
   }

   public ThrownPoisonEggEntity(EntityType<? extends ThrownPoisonEggEntity> entitytype, double d0, double d1, double d2, Level level) {
      super(entitytype, d0, d1, d2, level);
   }

   public ThrownPoisonEggEntity(EntityType<? extends ThrownPoisonEggEntity> entitytype, LivingEntity livingentity, Level level) {
      super(entitytype, livingentity, level);
   }

   public void m_7822_(byte pId) {
      if (pId == 3) {
         double d0 = 0.08;

         for (int i = 0; i < 8; i++) {
            this.m_9236_()
               .m_7106_(
                  new ItemParticleOption(ParticleTypes.f_123752_, this.m_7846_()),
                  this.m_20185_(),
                  this.m_20186_(),
                  this.m_20189_(),
                  ((double)this.f_19796_.m_188501_() - 0.5) * 0.08,
                  ((double)this.f_19796_.m_188501_() - 0.5) * 0.08,
                  ((double)this.f_19796_.m_188501_() - 0.5) * 0.08
               );
         }
      }
   }

   public void spawnPoisonCloud(Level level, double x, double y, double z) {
      AreaEffectCloud cloud = new AreaEffectCloud(level, x, y, z);
      cloud.m_19712_(3.0F);
      cloud.m_19738_(-0.05F);
      cloud.m_19734_(20);
      cloud.m_19740_(0);
      cloud.m_19714_(5149489);
      cloud.m_19724_(ParticleTypes.f_123811_);
      cloud.m_19716_(new MobEffectInstance(MobEffects.f_19614_, 20, 0));
      level.m_7967_(cloud);
   }

   protected void m_5790_(@NotNull EntityHitResult pResult) {
      super.m_5790_(pResult);
      if (!(pResult.m_82443_() instanceof BbqEntity)) {
         pResult.m_82443_().m_6469_(this.m_269291_().m_269390_(this, this.m_19749_()), 0.5F);
      }
   }

   protected void m_6532_(@NotNull HitResult pResult) {
      super.m_6532_(pResult);
      if (!this.m_9236_().f_46443_) {
         if (this.f_19796_.m_188501_() < 0.5F) {
            this.spawnPoisonCloud(this.m_9236_(), this.m_20185_(), this.m_20186_(), this.m_20189_());
         }

         this.m_9236_().m_7605_(this, (byte)3);
         this.m_146870_();
      }
   }

   @NotNull
   protected Item m_7881_() {
      return (Item)AnnoyingVillagersModItems.POISON_EGG_ITEM.get();
   }
}
