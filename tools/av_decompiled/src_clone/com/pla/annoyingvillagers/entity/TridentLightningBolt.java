package com.pla.annoyingvillagers.entity;

import com.google.common.collect.Sets;
import com.pla.annoyingvillagers.config.AnnoyingVillagersConfig;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModDamageTypes;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModMobEffects;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.PlayMessages.SpawnEntity;

public class TridentLightningBolt extends LightningBolt {
   private int tridentLife = 2;
   private int tridentFlashes;
   private final Set<Entity> tridentHitEntities = Sets.newHashSet();
   boolean superLightning = false;
   @Nullable
   private LivingEntity owner;
   private boolean tridentVisualOnly = false;

   public void setSuperLightning(boolean superLightning) {
      this.superLightning = superLightning;
   }

   public TridentLightningBolt(EntityType<? extends LightningBolt> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.f_19811_ = true;
      this.tridentLife = 2;
      this.tridentFlashes = this.f_19796_.m_188503_(3) + 1;
   }

   public TridentLightningBolt(SpawnEntity spawnEntity, Level level) {
      this((EntityType<? extends LightningBolt>)AnnoyingVillagersModEntities.TRIDENT_LIGHTNING_BOLT.get(), level);
   }

   public void setOwner(@Nullable LivingEntity owner) {
      this.owner = owner;
   }

   @Nullable
   public LivingEntity getOwner() {
      return this.owner;
   }

   public void m_8119_() {
      super.m_6075_();
      if (this.tridentLife == 2) {
         if (this.m_9236_().m_5776_()) {
            this.m_9236_()
               .m_7785_(
                  this.m_20185_(),
                  this.m_20186_(),
                  this.m_20189_(),
                  SoundEvents.f_12090_,
                  SoundSource.WEATHER,
                  10000.0F,
                  0.8F + this.f_19796_.m_188501_() * 0.2F,
                  false
               );
            this.m_9236_()
               .m_7785_(
                  this.m_20185_(),
                  this.m_20186_(),
                  this.m_20189_(),
                  SoundEvents.f_12089_,
                  SoundSource.WEATHER,
                  2.0F,
                  0.5F + this.f_19796_.m_188501_() * 0.2F,
                  false
               );
         } else {
            this.m_146850_(GameEvent.f_157772_);
            if (this.owner instanceof ServerPlayer serverPlayer) {
               this.m_20879_(serverPlayer);
            }
         }
      }

      this.tridentLife--;
      if (this.tridentLife < 0) {
         if (this.tridentFlashes == 0) {
            if (this.m_9236_() instanceof ServerLevel serverLevel) {
               List<Entity> list = this.m_9236_()
                  .m_6249_(
                     this,
                     new AABB(
                        this.m_20185_() - 15.0,
                        this.m_20186_() - 15.0,
                        this.m_20189_() - 15.0,
                        this.m_20185_() + 15.0,
                        this.m_20186_() + 21.0,
                        this.m_20189_() + 15.0
                     ),
                     entityx -> entityx.m_6084_() && !this.tridentHitEntities.contains(entityx)
                  );

               for (ServerPlayer serverPlayer : serverLevel.m_8795_(p -> p.m_20270_(this) < 256.0F)) {
                  CriteriaTriggers.f_145089_.m_153391_(serverPlayer, this, list);
               }
            }

            this.m_146870_();
         } else if (this.tridentLife < -this.f_19796_.m_188503_(10)) {
            this.tridentFlashes--;
            this.tridentLife = 1;
         }
      }

      if (this.tridentLife >= 0) {
         if (this.m_9236_() instanceof ServerLevel serverLevel) {
            if (!this.tridentVisualOnly) {
               List<Entity> list = this.m_9236_()
                  .m_6249_(
                     this,
                     new AABB(
                        this.m_20185_() - 3.0,
                        this.m_20186_() - 3.0,
                        this.m_20189_() - 3.0,
                        this.m_20185_() + 3.0,
                        this.m_20186_() + 9.0,
                        this.m_20189_() + 3.0
                     ),
                     entityx -> entityx instanceof LivingEntity
                           && entityx.m_6084_()
                           && entityx != this.owner
                           && !(entityx instanceof BbqEntity)
                           && !entityx.m_5833_()
                           && (!(entityx instanceof Player player) || !player.m_7500_())
                  );
               if (this.superLightning) {
                  DamageSource explosionDamage = AnnoyingVillagersModDamageTypes.Sources.impactExplosion(serverLevel.m_9598_(), this);
                  serverLevel.m_254877_(
                     this,
                     explosionDamage,
                     null,
                     this.m_20185_(),
                     this.m_20186_(),
                     this.m_20189_(),
                     serverLevel.f_46441_.m_188501_() * 5.0F + 5.0F,
                     false,
                     AnnoyingVillagersConfig.TRIDENT_FESTIVAL_CAN_BREAK_BLOCK.get() ? ExplosionInteraction.BLOCK : ExplosionInteraction.NONE
                  );
               }

               for (Entity entity : list) {
                  if (!ForgeEventFactory.onEntityStruckByLightning(entity, this)) {
                     if (entity instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity)entity;
                        if (this.superLightning) {
                           livingEntity.m_7292_(new MobEffectInstance((MobEffect)AnnoyingVillagersModMobEffects.ELECTRIFY.get(), 100, 2));
                        } else {
                           livingEntity.m_7292_(new MobEffectInstance((MobEffect)AnnoyingVillagersModMobEffects.ELECTRIFY.get(), 60, 1));
                        }
                     }

                     if (this.superLightning) {
                        entity.m_6469_(this.m_9236_().m_269111_().m_269104_(this, this.owner), 50.0F);
                     } else {
                        entity.m_6469_(this.m_9236_().m_269111_().m_269104_(this, this.owner), 5.0F);
                     }
                  }
               }

               this.tridentHitEntities.addAll(list);
               if (this.owner instanceof ServerPlayer serverPlayer) {
                  CriteriaTriggers.f_10554_.m_21721_(serverPlayer, list);
               }
            }
         } else {
            this.m_9236_().m_6580_(2);
         }
      }
   }
}
