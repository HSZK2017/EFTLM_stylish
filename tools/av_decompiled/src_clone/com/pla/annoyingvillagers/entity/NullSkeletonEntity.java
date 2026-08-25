package com.pla.annoyingvillagers.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pla.annoyingvillagers.gameasset.AVSkills;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.util.TeamUtil;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class NullSkeletonEntity extends AbstractSkeleton {
   protected UUID nullUUID;
   protected NullEntity nullEntity;
   protected UUID playerUUID;
   protected Player player;

   public NullSkeletonEntity(SpawnEntity spawnEntity, Level level) {
      this((EntityType<NullSkeletonEntity>)AnnoyingVillagersModEntities.NULL_SKELETON.get(), level);
   }

   public void setPlayer(Player player) {
      this.playerUUID = player.m_20148_();
      this.player = player;
   }

   public NullEntity getNullEntity() {
      return this.nullEntity;
   }

   public void setNullEntity(NullEntity nullEntity) {
      this.nullUUID = nullEntity.m_20148_();
      this.nullEntity = nullEntity;
   }

   public NullSkeletonEntity(EntityType<NullSkeletonEntity> entitytype, Level level) {
      super(entitytype, level);
      this.m_274367_(2.0F);
      this.f_21364_ = 2;
      this.m_21409_(EquipmentSlot.MAINHAND, 0.0F);
      this.m_21409_(EquipmentSlot.OFFHAND, 0.0F);
      this.m_21409_(EquipmentSlot.CHEST, 0.0F);
      this.m_21409_(EquipmentSlot.HEAD, 0.0F);
      this.m_21409_(EquipmentSlot.LEGS, 0.0F);
      this.m_21409_(EquipmentSlot.FEET, 0.0F);
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   private boolean isOwner(LivingEntity livingEntity) {
      if (livingEntity instanceof Player playerEntity && this.playerUUID != null && this.playerUUID.equals(playerEntity.m_20148_())) {
         return true;
      }

      return false;
   }

   private boolean validTarget(LivingEntity livingEntity) {
      return livingEntity != null && livingEntity.m_6084_() && !this.isOwner(livingEntity);
   }

   protected void m_8099_() {
      this.f_21345_
         .m_25352_(
            1,
            new Goal() {
               public boolean m_8036_() {
                  return NullSkeletonEntity.this.nullEntity != null
                     && NullSkeletonEntity.this.nullEntity.m_6084_()
                     && NullSkeletonEntity.this.m_20270_(NullSkeletonEntity.this.nullEntity) > 18.0F;
               }

               public void m_8037_() {
                  if (NullSkeletonEntity.this.nullEntity != null && NullSkeletonEntity.this.nullEntity.m_6084_()) {
                     NullSkeletonEntity.this.m_21573_().m_5624_(NullSkeletonEntity.this.nullEntity, 2.0);
                     NullSkeletonEntity.this.m_21563_().m_24960_(NullSkeletonEntity.this.nullEntity, 30.0F, 30.0F);
                     if (NullSkeletonEntity.this.m_20280_(NullSkeletonEntity.this.nullEntity) > 20.0) {
                        if (NullSkeletonEntity.this.m_21573_().m_26571_()) {
                           NullSkeletonEntity.this.m_21573_().m_5624_(NullSkeletonEntity.this.nullEntity, 2.0);
                        }
                     } else {
                        NullSkeletonEntity.this.m_21573_().m_26573_();
                     }
                  }
               }

               public boolean m_8045_() {
                  return NullSkeletonEntity.this.nullEntity != null
                     && NullSkeletonEntity.this.nullEntity.m_6084_()
                     && (double)NullSkeletonEntity.this.m_20270_(NullSkeletonEntity.this.nullEntity) > 50.0;
               }
            }
         );
      this.f_21346_
         .m_25352_(
            1,
            new NearestAttackableTargetGoal(
               this,
               LivingEntity.class,
               10,
               true,
               false,
               livingEntity -> this.validTarget(livingEntity) && this.player != null && this.player.m_6084_() && this.player.m_21188_() == livingEntity
            )
         );
      this.f_21346_
         .m_25352_(
            2,
            new NearestAttackableTargetGoal(
               this,
               LivingEntity.class,
               10,
               true,
               false,
               livingEntity -> this.validTarget(livingEntity) && this.player != null && this.player.m_6084_() && this.player.m_21214_() == livingEntity
            )
         );
      this.f_21345_
         .m_25352_(
            1,
            new Goal() {
               public boolean m_8036_() {
                  return NullSkeletonEntity.this.player != null
                     && NullSkeletonEntity.this.player.m_6084_()
                     && NullSkeletonEntity.this.m_20270_(NullSkeletonEntity.this.player) > 18.0F;
               }

               public void m_8037_() {
                  if (NullSkeletonEntity.this.player != null && NullSkeletonEntity.this.player.m_6084_()) {
                     NullSkeletonEntity.this.m_21573_().m_5624_(NullSkeletonEntity.this.player, 2.0);
                     NullSkeletonEntity.this.m_21563_().m_24960_(NullSkeletonEntity.this.player, 30.0F, 30.0F);
                     if (NullSkeletonEntity.this.m_20280_(NullSkeletonEntity.this.player) > 20.0) {
                        if (NullSkeletonEntity.this.m_21573_().m_26571_()) {
                           NullSkeletonEntity.this.m_21573_().m_5624_(NullSkeletonEntity.this.player, 2.0);
                        }
                     } else {
                        NullSkeletonEntity.this.m_21573_().m_26573_();
                     }
                  }
               }

               public boolean m_8045_() {
                  return NullSkeletonEntity.this.player != null
                     && NullSkeletonEntity.this.player.m_6084_()
                     && (double)NullSkeletonEntity.this.m_20270_(NullSkeletonEntity.this.player) > 50.0;
               }
            }
         );
      this.f_21346_
         .m_25352_(
            1,
            new NearestAttackableTargetGoal(
               this,
               LivingEntity.class,
               10,
               true,
               false,
               livingEntity -> this.validTarget(livingEntity)
                     && this.nullEntity != null
                     && this.nullEntity.m_6084_()
                     && this.nullEntity.m_5448_() == livingEntity
            )
         );
      this.f_21346_
         .m_25352_(
            1,
            new NearestAttackableTargetGoal(
               this,
               LivingEntity.class,
               10,
               true,
               false,
               livingEntity -> this.validTarget(livingEntity)
                     && this.nullEntity != null
                     && this.nullEntity.m_6084_()
                     && this.nullEntity.m_21188_() == livingEntity
            )
         );
      this.f_21346_
         .m_25352_(
            2,
            new NearestAttackableTargetGoal(
               this,
               LivingEntity.class,
               10,
               true,
               false,
               livingEntity -> this.validTarget(livingEntity)
                     && this.nullEntity != null
                     && this.nullEntity.m_6084_()
                     && this.nullEntity.m_21214_() == livingEntity
            )
         );
   }

   @NotNull
   public MobType m_6336_() {
      return MobType.f_21641_;
   }

   public double m_6049_() {
      return -0.35;
   }

   protected SoundEvent m_7515_() {
      return SoundEvents.f_12559_;
   }

   @NotNull
   protected SoundEvent m_7975_(@NotNull DamageSource pDamageSource) {
      return SoundEvents.f_12561_;
   }

   @NotNull
   protected SoundEvent m_5592_() {
      return SoundEvents.f_12560_;
   }

   @NotNull
   protected SoundEvent m_7878_() {
      return SoundEvents.f_12562_;
   }

   public SpawnGroupData m_6518_(
      @NotNull ServerLevelAccessor serverLevelAccessor,
      @NotNull DifficultyInstance difficultyInstance,
      @NotNull MobSpawnType mobSpawnType,
      @Nullable SpawnGroupData spawngroupdata,
      @Nullable CompoundTag compoundtag
   ) {
      if (this.nullEntity != null) {
         TeamUtil.addOrJoinTeam(this, "herobrine");
      }

      try {
         Objects.requireNonNull(this.m_20194_())
            .m_129892_()
            .m_82094_()
            .execute("data merge entity @s {CanPickUpLoot: 1b}", this.m_20203_().m_81324_().m_81325_(4));
      } catch (CommandSyntaxException var7) {
      }

      return super.m_6518_(serverLevelAccessor, difficultyInstance, mobSpawnType, spawngroupdata, compoundtag);
   }

   public boolean m_7327_(@NotNull Entity pEntity) {
      if (pEntity instanceof Player hurtPlayer && this.playerUUID != null && this.playerUUID.equals(hurtPlayer.m_20148_())) {
         return false;
      }

      if (pEntity instanceof NullEntity hurtNull && this.nullUUID != null && this.nullUUID.equals(hurtNull.m_20148_())) {
         return false;
      }

      if (this.player != null) {
         float f = (float)this.m_21133_(Attributes.f_22281_);
         float f1 = (float)this.m_21133_(Attributes.f_22282_);
         if (pEntity instanceof LivingEntity) {
            f += EnchantmentHelper.m_44833_(this.m_21205_(), ((LivingEntity)pEntity).m_6336_());
            f1 += (float)EnchantmentHelper.m_44894_(this);
         }

         int i = EnchantmentHelper.m_44914_(this);
         if (i > 0) {
            pEntity.m_20254_(i * 4);
         }

         boolean flag = pEntity.m_6469_(this.m_269291_().m_269075_(this.player), f);
         if (flag) {
            if (f1 > 0.0F && pEntity instanceof LivingEntity) {
               ((LivingEntity)pEntity)
                  .m_147240_(
                     (double)(f1 * 0.5F),
                     (double)Mth.m_14031_(this.m_146908_() * (float) (Math.PI / 180.0)),
                     (double)(-Mth.m_14089_(this.m_146908_() * (float) (Math.PI / 180.0)))
                  );
               this.m_20256_(this.m_20184_().m_82542_(0.6, 1.0, 0.6));
            }

            this.m_19970_(this, pEntity);
            this.m_21335_(pEntity);
         }

         return flag;
      } else {
         return super.m_7327_(pEntity);
      }
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         if (this.f_19797_ == 1) {
            ItemStack sword = new ItemStack(Items.f_42388_);
            sword.m_41663_(Enchantments.f_44981_, 1);
            sword.m_41663_(Enchantments.f_44986_, 1);
            sword.m_41663_(Enchantments.f_44980_, 1);
            sword.m_41663_(Enchantments.f_44977_, 1);
            this.m_8061_(EquipmentSlot.MAINHAND, sword);
            ItemStack helmet = new ItemStack(Items.f_42472_);
            helmet.m_41663_(Enchantments.f_44972_, 1);
            helmet.m_41663_(Enchantments.f_44986_, 1);
            helmet.m_41663_(Enchantments.f_44965_, 1);
            this.m_8061_(EquipmentSlot.HEAD, helmet);
         }

         if (this.nullEntity == null && this.nullUUID != null) {
            if (serverLevel.m_8791_(this.nullUUID) instanceof NullEntity entityNull) {
               this.nullEntity = entityNull;
            } else {
               this.nullEntity = null;
            }
         }

         if (this.nullEntity != null && !this.nullEntity.m_6084_()) {
            this.nullEntity = null;
            this.nullUUID = null;
            this.m_6074_();
         }

         if (this.nullEntity != null && this.nullEntity.m_6084_()) {
            double distanceSq = this.m_20280_(this.nullEntity);
            if (distanceSq > 600.0) {
               this.m_6021_(this.nullEntity.m_20185_(), this.nullEntity.m_20186_(), this.nullEntity.m_20189_());
            }
         }

         if (this.player == null && this.playerUUID != null) {
            this.player = serverLevel.m_46003_(this.playerUUID);
         }

         if (this.player != null && !this.player.m_6084_()) {
            this.player = null;
            this.playerUUID = null;
            this.m_6074_();
         }

         if (this.player != null && this.player.m_6084_()) {
            PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(this.player, PlayerPatch.class);
            if (playerPatch instanceof ServerPlayerPatch serverPlayerPatch) {
               SkillContainer skillContainer = serverPlayerPatch.getSkill(AVSkills.NULL_WEAPON);
               if (skillContainer != null && !skillContainer.isActivated()) {
                  this.m_6074_();
               }
            }

            double distanceSq = this.m_20280_(this.player);
            if (distanceSq > 600.0) {
               this.m_6021_(this.player.m_20185_(), this.player.m_20186_(), this.player.m_20189_());
            }
         }
      }
   }

   public boolean m_6469_(@NotNull DamageSource pSource, float pAmount) {
      if (this.player != null && pSource.m_7639_() == this.player) {
         return false;
      } else if (this.nullEntity != null && pSource.m_7639_() == this.nullEntity) {
         return false;
      } else {
         if (!pSource.m_276093_(DamageTypes.f_268724_)) {
            float health = this.m_21223_();
            if (health - pAmount <= 5.0F) {
               this.m_21153_(0.0F);
               this.m_6667_(this.m_269291_().m_269341_());
               return true;
            }
         }

         return super.m_6469_(pSource, pAmount);
      }
   }

   public void m_7380_(@NotNull CompoundTag tag) {
      super.m_7380_(tag);
      if (this.nullUUID != null) {
         tag.m_128362_("NullUUID", this.nullUUID);
      }

      if (this.playerUUID != null) {
         tag.m_128362_("PlayerUUID", this.playerUUID);
      }
   }

   public void m_7378_(@NotNull CompoundTag tag) {
      super.m_7378_(tag);
      if (tag.m_128403_("NullUUID")) {
         this.nullUUID = tag.m_128342_("NullUUID");
      }

      if (tag.m_128403_("PlayerUUID")) {
         this.playerUUID = tag.m_128342_("PlayerUUID");
      }
   }

   @NotNull
   public static Builder m_32166_() {
      Builder builder = Mob.m_21552_();
      builder = builder.m_22268_(Attributes.f_22279_, 0.35);
      builder = builder.m_22268_(Attributes.f_22276_, 30.0);
      builder = builder.m_22268_(Attributes.f_22284_, 10.0);
      builder = builder.m_22268_(Attributes.f_22281_, 0.0);
      return builder.m_22268_(Attributes.f_22277_, 24.0);
   }
}
