package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.config.AnnoyingVillagersConfig;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.item.ShadowObsidianSwordItem;
import com.pla.annoyingvillagers.spawnhandler.HerobrineMobData;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class ArmoredHerobrineEntity extends HerobrineMob {
   public ArmoredHerobrineEntity(SpawnEntity spawnEntity, Level level) {
      this((EntityType<ArmoredHerobrineEntity>)AnnoyingVillagersModEntities.ARMORED_HEROBRINE.get(), level);
   }

   public ArmoredHerobrineEntity(EntityType<ArmoredHerobrineEntity> entitytype, Level level) {
      super(entitytype, level);
      this.m_274367_(4.0F);
      this.f_21364_ = 60;
      this.m_21557_(false);
      this.m_6593_(this.m_5446_());
      this.m_20340_(true);
      this.m_21530_();
      this.m_8061_(EquipmentSlot.HEAD, new ItemStack((ItemLike)AnnoyingVillagersModItems.HEROBRINE_OBSIDIAN_DIAMOND_HELMET.get()));
      this.m_8061_(EquipmentSlot.CHEST, new ItemStack((ItemLike)AnnoyingVillagersModItems.HEROBRINE_OBSIDIAN_DIAMOND_CHESTPLATE.get()));
      this.m_21008_(InteractionHand.MAIN_HAND, new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_SWORD.get()));
      this.setChatName(this.m_5446_().getString());
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
   public void rollItem() {
      super.rollItem();
      ItemStack offHand = this.m_21206_();
      if (offHand.m_41720_() instanceof ShadowObsidianSwordItem) {
         this.m_21008_(InteractionHand.OFF_HAND, ItemStack.f_41583_);
      } else {
         this.m_21008_(InteractionHand.OFF_HAND, new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_SWORD.get()));
      }
   }

   @Nullable
   @Override
   public SoundEvent getAttackVoiceSound() {
      return new Random().nextFloat() <= 0.2F
         ? (SoundEvent)AnnoyingVillagersModSounds.ARMORED_HEROBRINE_SAY.get()
         : (SoundEvent)AnnoyingVillagersModSounds.HEROBRINE_CLONE_SAY.get();
   }

   @Nullable
   @Override
   public SoundEvent getHurtVoiceSound() {
      return (SoundEvent)AnnoyingVillagersModSounds.HEROBRINE_CLONE_SAY_ON_HURT.get();
   }

   public void m_6667_(@NotNull DamageSource damagesource) {
      super.m_6667_(damagesource);
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         if ((Boolean)AnnoyingVillagersConfig.TURN_ON_NPC_VOICE.get()) {
            this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.ARMORED_HEROBRINE_SAY_ON_DEATH.get(), 0.5F, 1.0F);
         }

         InfectedTheMostMoistBurrit0Entity infectedTheMostMoistBurrit0Entity = new InfectedTheMostMoistBurrit0Entity(
            (EntityType<InfectedTheMostMoistBurrit0Entity>)AnnoyingVillagersModEntities.INFECTED_THEMOSTMOISTBURRIT0.get(), serverLevel
         );
         infectedTheMostMoistBurrit0Entity.m_7678_(this.m_20185_(), this.m_20186_(), this.m_20189_(), serverLevel.m_213780_().m_188501_() * 360.0F, 0.0F);
         infectedTheMostMoistBurrit0Entity.m_6518_(
            serverLevel, serverLevel.m_6436_(infectedTheMostMoistBurrit0Entity.m_20183_()), MobSpawnType.MOB_SUMMONED, null, null
         );
         this.m_6842_(true);
         this.m_142687_(RemovalReason.KILLED);
         serverLevel.m_7967_(infectedTheMostMoistBurrit0Entity);
      }
   }

   public static boolean canSpawn(
      EntityType<ArmoredHerobrineEntity> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos position, RandomSource random
   ) {
      ServerLevel serverLevel = level.m_6018_();
      int passesDay = (int)(serverLevel.m_46467_() / 24000L);
      if (passesDay != 0 && passesDay % 3 != 0) {
         return false;
      } else if (HerobrineMobData.get(serverLevel).isOccupied(serverLevel)) {
         return false;
      } else {
         return !serverLevel.m_46462_() ? false : Monster.m_219013_(entityType, level, spawnType, position, random);
      }
   }

   public static Builder createAttributes() {
      return Mob.m_21552_()
         .m_22268_(Attributes.f_22276_, 100.0)
         .m_22268_(Attributes.f_22279_, 0.45)
         .m_22268_(Attributes.f_22281_, 5.0)
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
