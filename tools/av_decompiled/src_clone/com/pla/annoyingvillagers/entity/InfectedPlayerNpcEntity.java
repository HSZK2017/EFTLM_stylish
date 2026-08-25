package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModMobEffects;
import com.pla.annoyingvillagers.util.HerobrineUtil;
import com.pla.annoyingvillagers.util.TeamUtil;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import net.minecraftforge.registries.ForgeRegistries;
import net.shelmarow.combat_evolution.effect.CEMobEffects;
import org.jetbrains.annotations.NotNull;
import se.gory_moon.player_mobs.entity.PlayerMobEntity;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.effect.EpicFightMobEffects;

public class InfectedPlayerNpcEntity extends PlayerMobEntity {
   final LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(this, LivingEntityPatch.class);

   public InfectedPlayerNpcEntity(EntityType<? extends InfectedPlayerNpcEntity> type, Level level) {
      super(type, level);
      this.f_21364_ = 300;
      this.m_274367_(0.6F);
      this.f_21364_ = 7;
      this.m_21557_(true);
      this.m_20340_(true);
   }

   public InfectedPlayerNpcEntity(SpawnEntity spawnEntity, Level level) {
      this((EntityType<? extends InfectedPlayerNpcEntity>)AnnoyingVillagersModEntities.INFECTED_PLAYER_NPC.get(), level);
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   protected void m_8099_() {
      this.f_21345_.m_148105_().clear();
      this.f_21346_.m_148105_().clear();
   }

   @NotNull
   public MobType m_6336_() {
      return MobType.f_21640_;
   }

   public double m_6049_() {
      return -0.35;
   }

   @NotNull
   public SoundEvent m_7975_(@NotNull DamageSource damageSource) {
      return Objects.requireNonNull(
         (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "entity.generic.hurt"))
      );
   }

   @NotNull
   public SoundEvent m_5592_() {
      return Objects.requireNonNull(
         (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "entity.generic.death"))
      );
   }

   public void m_8119_() {
      super.m_8119_();
      if (!this.m_9236_().m_5776_() && this.livingEntityPatch != null) {
         this.m_7292_(new MobEffectInstance((MobEffect)AnnoyingVillagersModMobEffects.HEROBRINE.get(), 2, 0, false, false));
         this.m_7292_(new MobEffectInstance((MobEffect)EpicFightMobEffects.STUN_IMMUNITY.get(), 2, 0, false, false));
         this.m_7292_(new MobEffectInstance((MobEffect)CEMobEffects.FULL_STUN_IMMUNITY.get(), 2, 0, false, false));
      }
   }

   protected void m_7472_(@NotNull DamageSource source, int looting, boolean recentlyHit) {
      super.m_7472_(source, looting, recentlyHit);
      String possessedBy = this.getPersistentData().m_128461_("possessed_by");
      switch (possessedBy) {
         case "herobrine_clone":
            HerobrineUtil.dropHerobrineCloneLoot(this.m_9236_(), this.m_20185_(), this.m_20186_(), this.m_20189_());
            break;
         case "shadow_herobrine_clone":
            HerobrineUtil.dropShadowHerobrineCloneLoot(this.m_9236_(), this.m_20185_(), this.m_20186_(), this.m_20189_());
            break;
         case "low_herobrine_clone":
         case "low_shadow_herobrine_clone":
            HerobrineUtil.dropLowHerobrineCloneLoot(this.m_9236_(), this.m_20185_(), this.m_20186_(), this.m_20189_());
            break;
         case "herobrine_7":
            HerobrineUtil.dropHerobrine7Loot(this.m_9236_(), this.m_20185_(), this.m_20186_(), this.m_20189_());
            break;
         case "shadow_herobrine":
            HerobrineUtil.dropShadowHerobrineLoot(this.m_9236_(), this.m_20185_(), this.m_20186_(), this.m_20189_());
            break;
         case "null":
            HerobrineUtil.dropNullLoot(this.m_9236_(), this.m_20185_(), this.m_20186_(), this.m_20189_());
      }

      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         ItemStack itemstack = this.m_6844_(EquipmentSlot.FEET);
         ItemEntity itementity = new ItemEntity(serverLevel, this.m_20185_(), this.m_20186_() + 1.0, this.m_20189_(), itemstack);
         itementity.m_32010_(10);
         serverLevel.m_7967_(itementity);
         itemstack = this.m_6844_(EquipmentSlot.FEET);
         itementity = new ItemEntity(serverLevel, this.m_20185_(), this.m_20186_() + 1.0, this.m_20189_(), itemstack);
         itementity.m_32010_(10);
         serverLevel.m_7967_(itementity);
         itemstack = this.m_6844_(EquipmentSlot.CHEST);
         itementity = new ItemEntity(serverLevel, this.m_20185_(), this.m_20186_() + 1.0, this.m_20189_(), itemstack);
         itementity.m_32010_(10);
         serverLevel.m_7967_(itementity);
         itemstack = this.m_6844_(EquipmentSlot.HEAD);
         itementity = new ItemEntity(serverLevel, this.m_20185_(), this.m_20186_() + 1.0, this.m_20189_(), itemstack);
         itementity.m_32010_(10);
         serverLevel.m_7967_(itementity);
      }
   }

   public SpawnGroupData m_6518_(
      @NotNull ServerLevelAccessor serverLevelAccessor,
      @NotNull DifficultyInstance difficultyInstance,
      @NotNull MobSpawnType mobSpawnType,
      @Nullable SpawnGroupData spawnGroupData,
      @Nullable CompoundTag compoundTag
   ) {
      if (!this.m_9236_().m_5776_()) {
         TeamUtil.addOrJoinTeam(this, "herobrine");
      }

      return super.m_6518_(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
   }

   public boolean m_6094_() {
      return false;
   }

   protected void m_7324_(@NotNull Entity entity) {
   }

   protected void m_6138_() {
   }

   public static Builder createAttributes() {
      Builder builder = Mob.m_21552_();
      builder = builder.m_22268_(Attributes.f_22279_, 0.26);
      builder = builder.m_22268_(Attributes.f_22276_, 10.0);
      builder = builder.m_22268_(Attributes.f_22284_, 0.0);
      builder = builder.m_22268_(Attributes.f_22281_, 1.0);
      return builder.m_22268_(Attributes.f_22277_, 32.0);
   }
}
