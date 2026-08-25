package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.item.DemoniacVoltageReaverItem;
import com.pla.annoyingvillagers.util.HerobrineUtil;
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
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class SwordsmanHerobrineEntity extends HerobrineMob {
   public SwordsmanHerobrineEntity(SpawnEntity spawnEntity, Level level) {
      this((EntityType<SwordsmanHerobrineEntity>)AnnoyingVillagersModEntities.SWORDSMAN_HEROBRINE.get(), level);
   }

   public SwordsmanHerobrineEntity(EntityType<SwordsmanHerobrineEntity> entitytype, Level level) {
      super(entitytype, level);
      this.m_274367_(2.0F);
      this.f_21364_ = 80;
      this.m_21557_(false);
      this.m_6593_(this.m_5446_());
      this.m_20340_(true);
      this.m_21530_();
      ItemStack sword = new ItemStack((ItemLike)AnnoyingVillagersModItems.DEMONIAC_VOLTAGE_REAVER.get());
      this.m_8061_(EquipmentSlot.MAINHAND, sword);
      this.setChatName(this.m_5446_().getString());
   }

   @Nullable
   @Override
   public SoundEvent getAttackVoiceSound() {
      return (SoundEvent)AnnoyingVillagersModSounds.ELITE_HEROBRINE_SAY.get();
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
   public void m_8119_() {
      super.m_8119_();
      if (this.f_19797_ == 1) {
         DemoniacVoltageReaverItem.clearSnakeAnimation(this.m_21205_());
      }

      if (!this.m_9236_().m_5776_() && this.f_19797_ % 20 == 0) {
         ItemStack itemStack = this.m_21205_();
         if (this.getState() > 0) {
            HerobrineUtil.spawnEliteEffect(this.m_9236_(), this.m_20185_(), this.m_20186_(), this.m_20189_(), this);
            if (itemStack.m_41720_() instanceof DemoniacVoltageReaverItem && itemStack.m_41783_() != null && !itemStack.m_41783_().m_128471_("SecondForm")) {
               itemStack.m_41783_().m_128379_("SecondForm", true);
            }
         } else if (itemStack.m_41720_() instanceof DemoniacVoltageReaverItem && itemStack.m_41783_() != null && itemStack.m_41783_().m_128441_("SecondForm")) {
            itemStack.m_41783_().m_128473_("SecondForm");
         }
      }
   }

   public void m_6667_(@NotNull DamageSource damagesource) {
      super.m_6667_(damagesource);
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         EliteHerobrineKnockedEntity eliteHerobrineKnockedEntity = new EliteHerobrineKnockedEntity(
            (EntityType<EliteHerobrineKnockedEntity>)AnnoyingVillagersModEntities.ELITE_HEROBRINE_KNOCKED.get(), serverLevel
         );
         eliteHerobrineKnockedEntity.m_7678_(this.m_20185_(), this.m_20186_(), this.m_20189_(), serverLevel.m_213780_().m_188501_() * 360.0F, 0.0F);
         eliteHerobrineKnockedEntity.getPersistentData().m_128359_("FromElite", "DemoniacVoltageReaver");
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
