package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.combatbehaviour.HerobrineCommon;
import com.pla.annoyingvillagers.config.AnnoyingVillagersConfig;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.spawnhandler.HerobrineMobData;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
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
import reascer.wom.gameasset.animations.weapons.AnimsMoonless;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class HerobrineChrisEntity extends HerobrineMob {
   public HerobrineChrisEntity(SpawnEntity spawnEntity, Level level) {
      this((EntityType<HerobrineChrisEntity>)AnnoyingVillagersModEntities.HEROBRINE_CHRIS.get(), level);
   }

   public HerobrineChrisEntity(EntityType<HerobrineChrisEntity> entitytype, Level level) {
      super(entitytype, level);
      this.m_274367_(3.0F);
      this.f_21364_ = 50;
      this.m_21557_(false);
      this.m_21530_();
      this.setChatName(this.m_5446_().getString());
      this.m_21008_(InteractionHand.MAIN_HAND, new ItemStack((ItemLike)AnnoyingVillagersModItems.OBSIDIAN_WEAPON.get()));
   }

   @Override
   public int getMinVoiceCooldown() {
      return 60;
   }

   @Override
   public int getMaxVoiceCooldown() {
      return 200;
   }

   @Nullable
   @Override
   public SoundEvent getAttackVoiceSound() {
      return (SoundEvent)AnnoyingVillagersModSounds.HEROBRINE_CLONE_SAY.get();
   }

   @Nullable
   @Override
   public SoundEvent getHurtVoiceSound() {
      return (SoundEvent)AnnoyingVillagersModSounds.HEROBRINE_CLONE_SAY_ON_HURT.get();
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
      } else if (!(damagesource.m_7640_() instanceof EnchantedArrowEntity)
         && damagesource.m_7640_() instanceof AbstractArrow
         && !(damagesource.m_7640_() instanceof BlueDemonThrownTridentEntity)) {
         return false;
      } else {
         if (this.m_9236_() instanceof ServerLevel serverLevel
            && HerobrineCommon.canPlaySecondFormAnimation(Objects.requireNonNull(this.getLivingEntityPatch()))) {
            AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(this.getLivingEntityPatch().getAnimator().getPlayerFor(null))
               .getRealAnimation();
            if (!EpicfightUtil.isLongHitAnimation(dynamicAnimation, this.getLivingEntityPatch())
               && this.m_9236_() instanceof ServerLevel
               && dynamicAnimation == Animations.EMPTY_ANIMATION) {
               Objects.requireNonNull(this.getLivingEntityPatch()).playAnimationSynchronized(AnimsMoonless.MOONLESS_GUARD_HIT_1, 0.0F);
               HerobrineCommon.playSecondFormAnimation(Objects.requireNonNull(this.getLivingEntityPatch()));
               this.m_5634_(4.0F);
               EpicfightUtil.damageBlocked(damagesource, this, serverLevel);
               return false;
            }
         }

         return super.m_6469_(damagesource, f);
      }
   }

   public void m_6667_(@NotNull DamageSource damagesource) {
      super.m_6667_(damagesource);
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         if ((Boolean)AnnoyingVillagersConfig.TURN_ON_NPC_VOICE.get()) {
            this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.HEROBRINE_CLONE_SAY_ON_DEATH.get(), 0.5F, 1.0F);
         }

         serverLevel.m_7654_().m_6846_().m_240416_(Component.m_237115_("subtitles.herobrine_clone_die"), false);
         InfectedChrisEntity corpse = new InfectedChrisEntity((EntityType<InfectedChrisEntity>)AnnoyingVillagersModEntities.INFECTED_CHRIS.get(), serverLevel);
         corpse.m_7678_(this.m_20185_(), this.m_20186_(), this.m_20189_(), this.m_146908_(), this.m_146909_());
         corpse.m_6518_(serverLevel, serverLevel.m_6436_(this.m_20183_()), MobSpawnType.MOB_SUMMONED, null, null);
         this.m_6842_(true);
         this.m_142687_(RemovalReason.KILLED);
         serverLevel.m_7967_(corpse);
      }
   }

   public static boolean canSpawn(
      EntityType<HerobrineChrisEntity> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos position, RandomSource random
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
         .m_22268_(Attributes.f_22281_, 1.0)
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
