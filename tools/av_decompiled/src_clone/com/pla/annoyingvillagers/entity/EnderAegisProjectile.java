package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModParticleTypes;
import com.pla.annoyingvillagers.util.HerobrineUtil;
import java.util.Objects;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(
   value = Dist.CLIENT,
   _interface = ItemSupplier.class
)
public class EnderAegisProjectile extends AbstractArrow implements ItemSupplier {
   public EnderAegisProjectile(SpawnEntity spawnentity, Level level) {
      super((EntityType)AnnoyingVillagersModEntities.ENDER_AEGIS_PROJECTILE.get(), level);
   }

   public EnderAegisProjectile(EntityType<? extends EnderAegisProjectile> entitytype, Level level) {
      super(entitytype, level);
   }

   public EnderAegisProjectile(EntityType<? extends EnderAegisProjectile> entitytype, double d0, double d1, double d2, Level level) {
      super(entitytype, d0, d1, d2, level);
   }

   public EnderAegisProjectile(EntityType<? extends EnderAegisProjectile> entitytype, LivingEntity livingentity, Level level) {
      super(entitytype, livingentity, level);
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   @OnlyIn(Dist.CLIENT)
   @NotNull
   public ItemStack m_7846_() {
      return new ItemStack(Blocks.f_50016_);
   }

   @NotNull
   public ItemStack m_7941_() {
      return ItemStack.f_41583_;
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.f_36703_) {
         this.m_146870_();
      }

      if (!this.m_9236_().m_5776_()) {
         HerobrineUtil.spawnEliteEffect(this.m_9236_(), this.m_20185_(), this.m_20186_(), this.m_20189_(), this);
         this.doGroundSlamAtSelf();
      }
   }

   private void doGroundSlamAtSelf() {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         BlockPos var6 = BlockPos.m_274561_(this.m_20182_().f_82479_, this.m_20182_().f_82480_ - 1.0, this.m_20182_().f_82481_);
         Vec3 center = new Vec3(this.m_20185_(), (double)var6.m_123342_(), this.m_20189_());
         Entity src = (Entity)(this.m_19749_() != null ? this.m_19749_() : this);
         if (src instanceof LivingEntity livingSrc) {
            LevelUtil.circleSlamFracture(livingSrc, serverLevel, center, 3.5, true, true, true);
         }
      }
   }

   public static EnderAegisProjectile shoot(Level level, LivingEntity livingentity, Random random, float f, double d0, int i) {
      EnderAegisProjectile enderAegisProjectile = new EnderAegisProjectile(
         (EntityType<? extends EnderAegisProjectile>)AnnoyingVillagersModEntities.ENDER_AEGIS_PROJECTILE.get(), livingentity, level
      );
      enderAegisProjectile.m_6686_(
         livingentity.m_20252_(1.0F).f_82479_, livingentity.m_20252_(1.0F).f_82480_, livingentity.m_20252_(1.0F).f_82481_, f * 2.0F, 0.0F
      );
      enderAegisProjectile.m_20225_(true);
      enderAegisProjectile.m_36762_(false);
      enderAegisProjectile.m_36781_(d0);
      enderAegisProjectile.m_36735_(i);
      level.m_7967_(enderAegisProjectile);
      level.m_6263_(
         null,
         livingentity.m_20185_(),
         livingentity.m_20186_(),
         livingentity.m_20189_(),
         Objects.requireNonNull((SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "entity.arrow.shoot"))),
         SoundSource.PLAYERS,
         1.0F,
         1.0F / (random.nextFloat() * 0.5F + 1.0F) + f / 2.0F
      );
      return enderAegisProjectile;
   }

   public static EnderAegisProjectile shoot(LivingEntity livingentity, LivingEntity livingentity1) {
      EnderAegisProjectile enderAegisProjectile = new EnderAegisProjectile(
         (EntityType<? extends EnderAegisProjectile>)AnnoyingVillagersModEntities.ENDER_AEGIS_PROJECTILE.get(), livingentity, livingentity.m_9236_()
      );
      double d0 = livingentity1.m_20185_() - livingentity.m_20185_();
      double d1 = livingentity1.m_20186_() + (double)livingentity1.m_20192_() - 1.1;
      double d2 = livingentity1.m_20189_() - livingentity.m_20189_();
      enderAegisProjectile.m_6686_(d0, d1 - enderAegisProjectile.m_20186_() + Math.hypot(d0, d2) * 0.2F, d2, 2.0F, 12.0F);
      enderAegisProjectile.m_20225_(true);
      enderAegisProjectile.m_36781_(18.0);
      enderAegisProjectile.m_36735_(7);
      enderAegisProjectile.m_36762_(false);
      livingentity.m_9236_().m_7967_(enderAegisProjectile);
      livingentity.m_9236_()
         .m_6263_(
            null,
            livingentity.m_20185_(),
            livingentity.m_20186_(),
            livingentity.m_20189_(),
            Objects.requireNonNull((SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "entity.arrow.shoot"))),
            SoundSource.PLAYERS,
            1.0F,
            1.0F / (new Random().nextFloat() * 0.5F + 1.0F)
         );
      return enderAegisProjectile;
   }

   protected boolean m_5603_(@NotNull Entity entity) {
      Entity owner = this.m_19749_();
      if (entity == owner) {
         return false;
      } else {
         if (owner instanceof LivingEntity livingOwner && entity instanceof LivingEntity livingTarget && livingOwner.m_7307_(livingTarget)) {
            return false;
         }

         return super.m_5603_(entity);
      }
   }

   protected void m_5790_(EntityHitResult pResult) {
      Entity vicTim = pResult.m_82443_();
      Entity owner = this.m_19749_();
      if (vicTim != owner) {
         if (vicTim.m_9236_() instanceof ServerLevel serverLevel) {
            serverLevel.m_8767_(
               (HitParticleType)EpicFightParticles.HIT_BLUNT.get(), vicTim.m_20185_(), vicTim.m_20186_() + 1.5, vicTim.m_20189_() + 0.8, 1, 0.1, 0.1, 0.1, 1.0
            );
            serverLevel.m_8767_(
               (SimpleParticleType)AnnoyingVillagersModParticleTypes.SPARK.get(),
               vicTim.m_20185_(),
               vicTim.m_20186_() + 1.5,
               vicTim.m_20189_() + 0.8,
               5,
               0.0,
               0.0,
               0.0,
               0.1
            );
            LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(vicTim, LivingEntityPatch.class);
            if (livingEntityPatch != null) {
               livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.LONGEST_HIT, 0.0F);
            }
         }

         super.m_5790_(pResult);
      }
   }
}
