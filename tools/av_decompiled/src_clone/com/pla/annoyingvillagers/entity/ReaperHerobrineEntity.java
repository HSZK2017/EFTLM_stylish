package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.util.HerobrineUtil;
import com.pla.annoyingvillagers.util.TeamUtil;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class ReaperHerobrineEntity extends HerobrineMob {
   private HerobrineDragonEntity thunderHerobrineDragon;
   private UUID thunderHerobrineDragonUUID;
   private HerobrineDragonEntity meteoriteHerobrineDragon;
   private UUID meteoriteHerobrineDragonUUID;
   private HerobrineDragonEntity healingHerobrineDragon;
   private UUID healingHerobrineDragonUUID;
   private boolean spawnDragonInit = false;
   private int dragonSummonCooldown = 0;

   public ReaperHerobrineEntity(SpawnEntity spawnEntity, Level level) {
      this((EntityType<ReaperHerobrineEntity>)AnnoyingVillagersModEntities.REAPER_HEROBRINE.get(), level);
   }

   public ReaperHerobrineEntity(EntityType<ReaperHerobrineEntity> entitytype, Level level) {
      super(entitytype, level);
      this.m_274367_(2.9F);
      this.f_21364_ = 300;
      this.m_21557_(false);
      this.m_6593_(this.m_5446_());
      this.m_20340_(true);
      this.m_21530_();
      this.m_8061_(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)AnnoyingVillagersModItems.ENDER_SLAYER_SCYTHE.get()));
      this.setChatName(this.m_5446_().getString());
   }

   @Nullable
   @Override
   public SoundEvent getAttackVoiceSound() {
      return (SoundEvent)AnnoyingVillagersModSounds.ELITE_HEROBRINE_SAY.get();
   }

   public HerobrineDragonEntity getThunderHerobrineDragon() {
      return this.thunderHerobrineDragon;
   }

   public UUID getThunderHerobrineDragonUUID() {
      return this.thunderHerobrineDragonUUID;
   }

   public HerobrineDragonEntity getMeteoriteHerobrineDragon() {
      return this.meteoriteHerobrineDragon;
   }

   public UUID getMeteoriteHerobrineDragonUUID() {
      return this.meteoriteHerobrineDragonUUID;
   }

   public HerobrineDragonEntity getHealingHerobrineDragon() {
      return this.healingHerobrineDragon;
   }

   public UUID getHealingHerobrineDragonUUID() {
      return this.healingHerobrineDragonUUID;
   }

   @Override
   public void m_7380_(@NotNull CompoundTag tag) {
      super.m_7380_(tag);
      if (this.thunderHerobrineDragonUUID != null) {
         tag.m_128362_("ThunderHerobrineDragonUUID", this.thunderHerobrineDragonUUID);
      }

      if (this.meteoriteHerobrineDragonUUID != null) {
         tag.m_128362_("MeteoriteHerobrineDragonUUID", this.meteoriteHerobrineDragonUUID);
      }

      if (this.healingHerobrineDragonUUID != null) {
         tag.m_128362_("HealingHerobrineDragonUUID", this.healingHerobrineDragonUUID);
      }

      tag.m_128379_("SpawnDragonInit", this.spawnDragonInit);
      tag.m_128405_("DragonSummonCooldown", this.dragonSummonCooldown);
   }

   @Override
   public void m_7378_(@NotNull CompoundTag tag) {
      super.m_7378_(tag);
      if (tag.m_128403_("ThunderHerobrineDragonUUID")) {
         this.thunderHerobrineDragonUUID = tag.m_128342_("ThunderHerobrineDragonUUID");
      }

      if (tag.m_128403_("MeteoriteHerobrineDragonUUID")) {
         this.meteoriteHerobrineDragonUUID = tag.m_128342_("MeteoriteHerobrineDragonUUID");
      }

      if (tag.m_128403_("HealingHerobrineDragonUUID")) {
         this.healingHerobrineDragonUUID = tag.m_128342_("HealingHerobrineDragonUUID");
      }

      this.spawnDragonInit = tag.m_128471_("SpawnDragonInit");
      this.dragonSummonCooldown = tag.m_128441_("DragonSummonCooldown") ? tag.m_128451_("DragonSummonCooldown") : this.dragonSummonCooldown;
   }

   public void summonEnderDragon(int type) {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         HerobrineDragonEntity dragon = new HerobrineDragonEntity(
            (EntityType<? extends HerobrineDragonEntity>)AnnoyingVillagersModEntities.HEROBRINE_DRAGON.get(), serverLevel
         );
         dragon.m_7678_(this.m_20185_(), this.m_20186_() + 20.0, this.m_20189_(), this.m_217043_().m_188501_() * 360.0F, 0.0F);
         dragon.m_6518_(serverLevel, serverLevel.m_6436_(dragon.m_20183_()), MobSpawnType.MOB_SUMMONED, null, null);
         dragon.m_21530_();
         dragon.setSummoner(this);
         dragon.setSummonerUUID(this.m_20148_());
         serverLevel.m_7967_(dragon);
         TeamUtil.addOrJoinTeam(dragon, "herobrine");
         if (type == 0) {
            this.thunderHerobrineDragonUUID = dragon.m_20148_();
            this.thunderHerobrineDragon = dragon;
         } else if (type == 1) {
            this.meteoriteHerobrineDragonUUID = dragon.m_20148_();
            this.meteoriteHerobrineDragon = dragon;
         } else {
            this.healingHerobrineDragonUUID = dragon.m_20148_();
            this.healingHerobrineDragon = dragon;
            EndCrystal endCrystal = new EndCrystal(EntityType.f_20564_, serverLevel);
            endCrystal.m_6027_(dragon.m_20185_(), dragon.m_20186_(), dragon.m_20189_());
            serverLevel.m_7967_(endCrystal);
            endCrystal.m_7998_(dragon, true);
         }

         if (this.m_9236_().m_7654_() != null) {
            Objects.requireNonNull(this.m_9236_().m_7654_())
               .m_6846_()
               .m_240416_(Component.m_237113_("<" + this.getChatName() + "> " + Component.m_237115_("subtitles.herobrine_summon").getString()), false);
         }
      }
   }

   public boolean m_6072_() {
      return false;
   }

   @Override
   public void m_8119_() {
      super.m_8119_();
      if (!this.m_9236_().f_46443_) {
         if (!this.spawnDragonInit) {
            this.spawnDragonInit = true;
            this.summonEnderDragon(0);
         }

         if (this.dragonSummonCooldown <= 0) {
            if (this.getState() < 2) {
               if (this.m_21223_() > this.m_21233_() / 2.0F && this.thunderHerobrineDragon == null && this.thunderHerobrineDragonUUID == null) {
                  this.summonEnderDragon(0);
               } else if (this.m_21223_() <= this.m_21233_() / 2.0F && this.meteoriteHerobrineDragon == null && this.meteoriteHerobrineDragonUUID == null) {
                  this.summonEnderDragon(1);
               }
            } else if (this.getState() == 2) {
               if (this.thunderHerobrineDragon == null && this.thunderHerobrineDragonUUID == null) {
                  this.summonEnderDragon(0);
               } else if (this.meteoriteHerobrineDragon == null && this.meteoriteHerobrineDragonUUID == null) {
                  this.summonEnderDragon(1);
               } else if (this.healingHerobrineDragon == null && this.healingHerobrineDragonUUID == null) {
                  this.summonEnderDragon(2);
               }
            }
         } else {
            this.dragonSummonCooldown--;
         }

         if (this.thunderHerobrineDragon == null && this.thunderHerobrineDragonUUID != null) {
            if (((ServerLevel)this.m_9236_()).m_8791_(this.thunderHerobrineDragonUUID) instanceof HerobrineDragonEntity dragon) {
               this.thunderHerobrineDragon = dragon;
            } else {
               this.thunderHerobrineDragon = null;
            }
         }

         if (this.meteoriteHerobrineDragon == null && this.meteoriteHerobrineDragonUUID != null) {
            if (((ServerLevel)this.m_9236_()).m_8791_(this.meteoriteHerobrineDragonUUID) instanceof HerobrineDragonEntity dragon) {
               this.meteoriteHerobrineDragon = dragon;
            } else {
               this.meteoriteHerobrineDragon = null;
            }
         }

         if (this.healingHerobrineDragon == null && this.healingHerobrineDragonUUID != null) {
            if (((ServerLevel)this.m_9236_()).m_8791_(this.healingHerobrineDragonUUID) instanceof HerobrineDragonEntity dragon) {
               this.healingHerobrineDragon = dragon;
            } else {
               this.healingHerobrineDragon = null;
            }
         }

         if (this.thunderHerobrineDragon != null && !this.thunderHerobrineDragon.m_6084_()) {
            this.thunderHerobrineDragon = null;
            this.thunderHerobrineDragonUUID = null;
            if (this.m_9236_().m_7654_() != null) {
               Objects.requireNonNull(this.m_9236_().m_7654_())
                  .m_6846_()
                  .m_240416_(
                     Component.m_237113_("<" + this.getChatName() + ">  " + Component.m_237115_("subtitles.reaper_herobrine_return_dragon").getString()), false
                  );
            }

            if (this.dragonSummonCooldown == 0) {
               if (this.getState() < 2) {
                  this.dragonSummonCooldown = new Random().nextInt(4800, 7200);
               } else if (this.getState() == 2) {
                  this.dragonSummonCooldown = new Random().nextInt(2400, 4800);
               }
            }
         }

         if (this.meteoriteHerobrineDragon != null && !this.meteoriteHerobrineDragon.m_6084_()) {
            this.meteoriteHerobrineDragon = null;
            this.meteoriteHerobrineDragonUUID = null;
            if (this.m_9236_().m_7654_() != null) {
               Objects.requireNonNull(this.m_9236_().m_7654_())
                  .m_6846_()
                  .m_240416_(
                     Component.m_237113_("<" + this.getChatName() + ">  " + Component.m_237115_("subtitles.reaper_herobrine_return_dragon").getString()), false
                  );
            }

            if (this.dragonSummonCooldown == 0) {
               if (this.getState() < 2) {
                  this.dragonSummonCooldown = new Random().nextInt(4800, 7200);
               } else if (this.getState() == 2) {
                  this.dragonSummonCooldown = new Random().nextInt(2400, 4800);
               }
            }
         }

         if (this.healingHerobrineDragon != null && !this.healingHerobrineDragon.m_6084_()) {
            this.healingHerobrineDragon = null;
            this.healingHerobrineDragonUUID = null;
            if (this.m_9236_().m_7654_() != null) {
               Objects.requireNonNull(this.m_9236_().m_7654_())
                  .m_6846_()
                  .m_240416_(
                     Component.m_237113_("<" + this.getChatName() + ">  " + Component.m_237115_("subtitles.reaper_herobrine_return_dragon").getString()), false
                  );
            }

            if (this.dragonSummonCooldown == 0) {
               if (this.getState() < 2) {
                  this.dragonSummonCooldown = new Random().nextInt(4800, 7200);
               } else if (this.getState() == 2) {
                  this.dragonSummonCooldown = new Random().nextInt(2400, 4800);
               }
            }
         }

         if (this.f_19797_ % 20 == 0 && this.getState() > 0) {
            HerobrineUtil.spawnEliteEffect(this.m_9236_(), this.m_20185_(), this.m_20186_(), this.m_20189_(), this);
         }
      }
   }

   @Override
   public boolean m_6469_(@NotNull DamageSource damagesource, float f) {
      if (damagesource.m_276093_(DamageTypes.f_268671_)) {
         return false;
      } else if (damagesource.m_276093_(DamageTypes.f_268585_)) {
         return false;
      } else if (damagesource.m_276093_(DamageTypes.f_268493_)) {
         return false;
      } else if (damagesource.m_276093_(DamageTypes.f_268722_)) {
         return false;
      } else if (damagesource.m_276093_(DamageTypes.f_268641_)) {
         return false;
      } else if (damagesource.m_276093_(DamageTypes.f_268482_)) {
         return false;
      } else {
         return !(damagesource.m_7640_() instanceof EnchantedArrowEntity)
               && damagesource.m_7640_() instanceof AbstractArrow
               && !(damagesource.m_7640_() instanceof BlueDemonThrownTridentEntity)
            ? false
            : super.m_6469_(damagesource, f);
      }
   }

   @Override
   public void m_142687_(@NotNull RemovalReason reason) {
      if (this.thunderHerobrineDragon != null) {
         this.thunderHerobrineDragon.m_6074_();
         this.thunderHerobrineDragon = null;
         this.thunderHerobrineDragonUUID = null;
      }

      if (this.meteoriteHerobrineDragon != null) {
         this.meteoriteHerobrineDragon.m_6074_();
         this.meteoriteHerobrineDragon = null;
         this.meteoriteHerobrineDragonUUID = null;
      }

      if (this.healingHerobrineDragon != null) {
         this.healingHerobrineDragon.m_6074_();
         this.healingHerobrineDragon = null;
         this.healingHerobrineDragonUUID = null;
      }

      super.m_142687_(reason);
   }

   public void m_6667_(@NotNull DamageSource damageSource) {
      super.m_6667_(damageSource);
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         EliteHerobrineKnockedEntity eliteHerobrineKnockedEntity = new EliteHerobrineKnockedEntity(
            (EntityType<EliteHerobrineKnockedEntity>)AnnoyingVillagersModEntities.ELITE_HEROBRINE_KNOCKED.get(), serverLevel
         );
         eliteHerobrineKnockedEntity.m_7678_(this.m_20185_(), this.m_20186_(), this.m_20189_(), serverLevel.m_213780_().m_188501_() * 360.0F, 0.0F);
         eliteHerobrineKnockedEntity.getPersistentData().m_128359_("FromElite", "EnderSlayerScythe");
         eliteHerobrineKnockedEntity.m_6518_(serverLevel, serverLevel.m_6436_(eliteHerobrineKnockedEntity.m_20183_()), MobSpawnType.MOB_SUMMONED, null, null);
         this.m_142687_(RemovalReason.KILLED);
         serverLevel.m_7967_(eliteHerobrineKnockedEntity);
         if (this.getGregUUID() != null) {
            Entity entity = serverLevel.m_8791_(this.getGregUUID());
            if (entity instanceof HerobrineGregEntity herobrineGregEntity && entity.m_6084_()) {
               herobrineGregEntity.requestProtect(eliteHerobrineKnockedEntity.m_20148_(), eliteHerobrineKnockedEntity);
            }
         }
      }
   }

   public static Builder createAttributes() {
      return Mob.m_21552_()
         .m_22268_(Attributes.f_22276_, 250.0)
         .m_22268_(Attributes.f_22279_, 0.45)
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
