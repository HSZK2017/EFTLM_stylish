package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModParticleTypes;
import java.util.Objects;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

@OnlyIn(
   value = Dist.CLIENT,
   _interface = ItemSupplier.class
)
public class EnchantedEnderPearlEntity extends AbstractArrow implements ItemSupplier {
   public EnchantedEnderPearlEntity(SpawnEntity spawnEntity, Level level) {
      super((EntityType)AnnoyingVillagersModEntities.ENCHANTED_ENDER_PEARL_PROJECTILE.get(), level);
   }

   public EnchantedEnderPearlEntity(EntityType<? extends EnchantedEnderPearlEntity> entitytype, Level level) {
      super(entitytype, level);
   }

   public EnchantedEnderPearlEntity(EntityType<? extends EnchantedEnderPearlEntity> entitytype, double d0, double d1, double d2, Level level) {
      super(entitytype, d0, d1, d2, level);
   }

   public EnchantedEnderPearlEntity(EntityType<? extends EnchantedEnderPearlEntity> entitytype, LivingEntity livingentity, Level level) {
      super(entitytype, livingentity, level);
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   @OnlyIn(Dist.CLIENT)
   @NotNull
   public ItemStack m_7846_() {
      return new ItemStack((ItemLike)AnnoyingVillagersModItems.ENCHANTED_ENDER_PEARL.get());
   }

   @NotNull
   public ItemStack m_7941_() {
      return ItemStack.f_41583_;
   }

   protected void m_7761_(@NotNull LivingEntity livingentity) {
      super.m_7761_(livingentity);
      livingentity.m_21317_(livingentity.m_21234_() - 1);
   }

   public void m_5790_(@NotNull EntityHitResult entityHitResult) {
      super.m_5790_(entityHitResult);
      if (!this.m_9236_().m_5776_()) {
         this.m_9236_()
            .m_5594_(
               null,
               new BlockPos(entityHitResult.m_82443_().m_146903_(), entityHitResult.m_82443_().m_146904_(), entityHitResult.m_82443_().m_146907_()),
               SoundEvents.f_11852_,
               SoundSource.NEUTRAL,
               1.0F,
               1.0F
            );
      } else {
         this.m_9236_()
            .m_7785_(
               (double)entityHitResult.m_82443_().m_146903_(),
               (double)entityHitResult.m_82443_().m_146904_(),
               (double)entityHitResult.m_82443_().m_146907_(),
               SoundEvents.f_11852_,
               SoundSource.NEUTRAL,
               1.0F,
               1.0F,
               false
            );
      }

      if (this.m_19749_() != null) {
         this.m_19749_()
            .m_6021_(
               (double)entityHitResult.m_82443_().m_146903_(),
               (double)entityHitResult.m_82443_().m_146904_() + 1.0,
               (double)entityHitResult.m_82443_().m_146907_()
            );
         if (this.m_19749_() instanceof ServerPlayer serverPlayer) {
            serverPlayer.f_8906_
               .m_9774_(
                  (double)entityHitResult.m_82443_().m_146903_(),
                  (double)entityHitResult.m_82443_().m_146904_() + 1.0,
                  (double)entityHitResult.m_82443_().m_146907_(),
                  serverPlayer.m_146908_(),
                  serverPlayer.m_146909_()
               );
         }

         if (this.m_9236_() instanceof ServerLevel serverLevel) {
            serverLevel.m_8767_(
               ParticleTypes.f_123760_,
               (double)entityHitResult.m_82443_().m_146903_(),
               (double)entityHitResult.m_82443_().m_146904_(),
               (double)entityHitResult.m_82443_().m_146907_(),
               50,
               4.0,
               4.0,
               4.0,
               1.0
            );
            serverLevel.m_8767_(
               (SimpleParticleType)AnnoyingVillagersModParticleTypes.ENDER.get(),
               this.m_19749_().m_20185_(),
               this.m_19749_().m_20186_() + 1.0,
               this.m_19749_().m_20189_(),
               16,
               0.0,
               0.0,
               0.0,
               0.5
            );
         }
      }
   }

   public void m_8060_(@NotNull BlockHitResult blockHitResult) {
      super.m_8060_(blockHitResult);
      if (!this.m_9236_().m_5776_()) {
         this.m_9236_()
            .m_5594_(
               null,
               new BlockPos(blockHitResult.m_82425_().m_123341_(), blockHitResult.m_82425_().m_123342_(), blockHitResult.m_82425_().m_123343_()),
               SoundEvents.f_11852_,
               SoundSource.NEUTRAL,
               1.0F,
               1.0F
            );
      } else {
         this.m_9236_()
            .m_7785_(
               (double)blockHitResult.m_82425_().m_123341_(),
               (double)blockHitResult.m_82425_().m_123342_(),
               (double)blockHitResult.m_82425_().m_123343_(),
               SoundEvents.f_11852_,
               SoundSource.NEUTRAL,
               1.0F,
               1.0F,
               false
            );
      }

      if (this.m_19749_() != null) {
         this.m_19749_()
            .m_6021_(
               (double)blockHitResult.m_82425_().m_123341_(),
               (double)blockHitResult.m_82425_().m_123342_() + 1.0,
               (double)blockHitResult.m_82425_().m_123343_()
            );
         if (this.m_19749_() instanceof ServerPlayer serverPlayer) {
            serverPlayer.f_8906_
               .m_9774_(
                  (double)blockHitResult.m_82425_().m_123341_(),
                  (double)blockHitResult.m_82425_().m_123342_() + 1.0,
                  (double)blockHitResult.m_82425_().m_123343_(),
                  serverPlayer.m_146908_(),
                  serverPlayer.m_146909_()
               );
         }

         if (this.m_9236_() instanceof ServerLevel serverLevel) {
            serverLevel.m_8767_(
               ParticleTypes.f_123760_,
               (double)blockHitResult.m_82425_().m_123341_(),
               (double)blockHitResult.m_82425_().m_123342_(),
               (double)blockHitResult.m_82425_().m_123343_(),
               50,
               4.0,
               4.0,
               4.0,
               1.0
            );
            serverLevel.m_8767_(
               (SimpleParticleType)AnnoyingVillagersModParticleTypes.ENDER.get(),
               this.m_19749_().m_20185_(),
               this.m_19749_().m_20186_() + 1.0,
               this.m_19749_().m_20189_(),
               16,
               0.0,
               0.0,
               0.0,
               0.5
            );
         }
      }
   }

   @NotNull
   protected SoundEvent m_7239_() {
      return SoundEvents.f_11852_;
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.m_9236_().m_5776_()) {
         this.m_9236_()
            .m_7106_((ParticleOptions)AnnoyingVillagersModParticleTypes.ENDER.get(), this.m_20185_(), this.m_20186_(), this.m_20189_(), 0.0, 0.0, 0.0);
      }

      if (this.f_36703_) {
         this.m_146870_();
      }
   }

   public static EnchantedEnderPearlEntity shoot(Level level, LivingEntity livingentity, RandomSource random, float f, double d0, int i) {
      EnchantedEnderPearlEntity enchantedEnderPearl = new EnchantedEnderPearlEntity(
         (EntityType<? extends EnchantedEnderPearlEntity>)AnnoyingVillagersModEntities.ENCHANTED_ENDER_PEARL_PROJECTILE.get(), livingentity, level
      );
      enchantedEnderPearl.m_6686_(
         livingentity.m_20252_(1.0F).f_82479_, livingentity.m_20252_(1.0F).f_82480_, livingentity.m_20252_(1.0F).f_82481_, f * 2.0F, 0.0F
      );
      enchantedEnderPearl.m_20225_(true);
      enchantedEnderPearl.m_36762_(false);
      enchantedEnderPearl.m_36781_(d0);
      enchantedEnderPearl.m_36735_(i);
      level.m_7967_(enchantedEnderPearl);
      level.m_6263_(
         (Player)null,
         livingentity.m_20185_(),
         livingentity.m_20186_(),
         livingentity.m_20189_(),
         Objects.requireNonNull((SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "throw"))),
         SoundSource.PLAYERS,
         1.0F,
         1.0F / (random.m_188501_() * 0.5F + 1.0F) + f / 2.0F
      );
      return enchantedEnderPearl;
   }

   public static EnchantedEnderPearlEntity shoot(LivingEntity livingentity, LivingEntity livingentity1) {
      EnchantedEnderPearlEntity enchantedEnderPearl = new EnchantedEnderPearlEntity(
         (EntityType<? extends EnchantedEnderPearlEntity>)AnnoyingVillagersModEntities.ENCHANTED_ENDER_PEARL_PROJECTILE.get(),
         livingentity,
         livingentity.m_9236_()
      );
      double d0 = livingentity1.m_20185_() - livingentity.m_20185_();
      double d1 = livingentity1.m_20186_() + (double)livingentity1.m_20192_() - 1.1;
      double d2 = livingentity1.m_20189_() - livingentity.m_20189_();
      enchantedEnderPearl.m_6686_(d0, d1 - enchantedEnderPearl.m_20186_() + Math.hypot(d0, d2) * 0.2F, d2, 2.6F, 12.0F);
      enchantedEnderPearl.m_20225_(true);
      enchantedEnderPearl.m_36781_(0.0);
      enchantedEnderPearl.m_36735_(0);
      enchantedEnderPearl.m_36762_(false);
      livingentity.m_9236_().m_7967_(enchantedEnderPearl);
      livingentity.m_9236_()
         .m_6263_(
            (Player)null,
            livingentity.m_20185_(),
            livingentity.m_20186_(),
            livingentity.m_20189_(),
            Objects.requireNonNull((SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "throw"))),
            SoundSource.PLAYERS,
            1.0F,
            1.0F / (new Random().nextFloat() * 0.5F + 1.0F)
         );
      return enchantedEnderPearl;
   }
}
