package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.clazz.AVNpc;
import com.pla.annoyingvillagers.clazz.BurstProtectEntity;
import com.pla.annoyingvillagers.combatbehaviour.AlexJevHookCombat;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.util.CombatBehaviour;
import com.pla.annoyingvillagers.util.TeamUtil;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowMobGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JevEntity extends AVNpc implements BurstProtectEntity {
   private UUID followTargetUUID;
   private AlexEntity followTarget;
   protected float recentDamageTaken = 0.0F;
   protected int recentHitCounter = 0;

   @Override
   public float getRecentDamageTaken() {
      return this.recentDamageTaken;
   }

   @Override
   public void setRecentDamageTaken(float value) {
      this.recentDamageTaken = value;
   }

   @Override
   public int getRecentHitCounter() {
      return this.recentHitCounter;
   }

   @Override
   public void setRecentHitCounter(int value) {
      this.recentHitCounter = value;
   }

   @Override
   public float getBurstProtectCapRatio() {
      return 0.15F;
   }

   public void setFollowTarget(AlexEntity followTarget) {
      this.followTarget = followTarget;
   }

   public AlexEntity getFollowTarget() {
      return this.followTarget;
   }

   public void setFollowTargetUUID(UUID followTargetUUID) {
      this.followTargetUUID = followTargetUUID;
   }

   public JevEntity(SpawnEntity spawnEntity, Level level) {
      this((EntityType<JevEntity>)AnnoyingVillagersModEntities.JEV.get(), level);
   }

   public JevEntity(EntityType<JevEntity> entitytype, Level level) {
      super(entitytype, level);
      this.m_274367_(0.6F);
      this.f_21364_ = 10;
      this.m_21557_(false);
      this.m_6593_(this.m_5446_());
      this.m_21530_();
      this.m_8061_(EquipmentSlot.OFFHAND, new ItemStack((ItemLike)AnnoyingVillagersModItems.JEV_BOOK.get()));
      this.m_8061_(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)AnnoyingVillagersModItems.JEV_PENCIL.get()));
      this.m_8061_(EquipmentSlot.HEAD, new ItemStack((ItemLike)AnnoyingVillagersModItems.JEV_GLASSES.get()));
      this.setPlaceBlockToParryChance(0.0);
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   @Override
   protected void m_8099_() {
      super.m_8099_();
      this.f_21345_.m_25352_(1, new LookAtPlayerGoal(this, AlexEntity.class, 12.0F));
      this.f_21345_.m_25352_(2, new AvoidEntityGoal(this, Monster.class, 5.0F, 1.2, 1.8));
      this.f_21345_.m_25352_(2, new AvoidEntityGoal(this, Player.class, 5.0F, 1.2, 1.8));
      this.f_21345_
         .m_25352_(
            2,
            new Goal() {
               public boolean m_8036_() {
                  return JevEntity.this.followTarget != null
                     && JevEntity.this.followTarget.m_6084_()
                     && JevEntity.this.m_20270_(JevEntity.this.followTarget) > 18.0F;
               }

               public void m_8037_() {
                  if (JevEntity.this.followTarget != null && JevEntity.this.followTarget.m_6084_()) {
                     JevEntity.this.m_21573_().m_5624_(JevEntity.this.followTarget, 2.0);
                     JevEntity.this.m_21563_().m_24960_(JevEntity.this.followTarget, 30.0F, 30.0F);
                     if (JevEntity.this.m_20280_(JevEntity.this.followTarget) > 20.0) {
                        if (JevEntity.this.m_21573_().m_26571_()) {
                           JevEntity.this.m_21573_().m_5624_(JevEntity.this.followTarget, 2.0);
                        }
                     } else {
                        JevEntity.this.m_21573_().m_26573_();
                     }
                  }
               }

               public boolean m_8045_() {
                  return JevEntity.this.followTarget != null
                     && JevEntity.this.followTarget.m_6084_()
                     && (double)JevEntity.this.m_20270_(JevEntity.this.followTarget) > 50.0;
               }
            }
         );
      this.f_21345_.m_25352_(3, new RandomStrollGoal(this, 1.0));
      this.f_21345_.m_25352_(4, new RandomLookAroundGoal(this));
      this.f_21345_.m_25352_(5, new FloatGoal(this));
      this.f_21345_.m_25352_(6, new FollowMobGoal(this, 1.0, 10.0F, 5.0F));
   }

   @Override
   protected void m_6475_(@NotNull DamageSource pDamageSource, float pDamageAmount) {
      if (pDamageSource.m_276093_(DamageTypes.f_268724_)) {
         super.m_6475_(pDamageSource, pDamageAmount);
      } else if (!this.m_6673_(pDamageSource)) {
         pDamageAmount = ForgeHooks.onLivingHurt(this, pDamageSource, pDamageAmount);
         if (!(pDamageAmount <= 0.0F)) {
            pDamageAmount = this.m_21161_(pDamageSource, pDamageAmount);
            pDamageAmount = this.m_6515_(pDamageSource, pDamageAmount);
            float finalDamage = Math.max(pDamageAmount - this.m_6103_(), 0.0F);
            float absorbed = pDamageAmount - finalDamage;
            if (absorbed > 0.0F) {
               this.m_7911_(this.m_6103_() - absorbed);
               if (this.m_6103_() < 0.0F) {
                  this.m_7911_(0.0F);
               }
            }

            finalDamage = ForgeHooks.onLivingDamage(this, pDamageSource, finalDamage);
            finalDamage = this.applyBurstProtection(this, pDamageSource, finalDamage);
            if (this.m_9236_() instanceof ServerLevel serverLevel && this.afterBurstProtection(serverLevel, pDamageSource, finalDamage)) {
               return;
            }

            if (!(finalDamage <= 0.0F)) {
               this.m_21231_().m_289194_(pDamageSource, finalDamage);
               this.m_21153_(this.m_21223_() - finalDamage);
               this.m_146850_(GameEvent.f_223706_);
            }
         }
      }
   }

   @Nullable
   public SpawnGroupData m_6518_(
      @NotNull ServerLevelAccessor pLevel,
      @NotNull DifficultyInstance pDifficulty,
      @NotNull MobSpawnType pReason,
      @Nullable SpawnGroupData pSpawnData,
      @Nullable CompoundTag pDataTag
   ) {
      SpawnGroupData returnSpawnGroupData = super.m_6518_(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
      TeamUtil.addOrJoinTeam(this, "alex");
      this.setMainWeaponItem(new ItemStack((ItemLike)AnnoyingVillagersModItems.JEV_PENCIL.get()));
      this.setOffWeaponItem(new ItemStack((ItemLike)AnnoyingVillagersModItems.JEV_BOOK.get()));
      return returnSpawnGroupData;
   }

   public void m_6667_(@NotNull DamageSource pDamageSource) {
      if (!this.m_9236_().f_46443_) {
         AlexJevHookCombat.onJevDeath(this);
      }

      super.m_6667_(pDamageSource);
   }

   @Override
   protected void m_7472_(@NotNull DamageSource source, int looting, boolean recentlyHit) {
      super.m_7472_(source, looting, recentlyHit);
      this.m_19983_(new ItemStack((ItemLike)AnnoyingVillagersModItems.JEV_GLASSES.get()));
      this.m_19983_(new ItemStack((ItemLike)AnnoyingVillagersModItems.JEV_PENCIL.get()));
      this.m_19983_(new ItemStack((ItemLike)AnnoyingVillagersModItems.JEV_BOOK.get()));
      this.dropHookGunForAlex();
      this.dropRandomCombatSupplies();
   }

   @Override
   protected void implementFirstTick(ServerLevel serverLevel) {
      super.implementFirstTick(serverLevel);
      this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.JEV_SAY_ON_SPAWN.get(), 0.5F, 1.0F);
   }

   @Override
   public void m_8119_() {
      super.m_8119_();
      if (!this.m_9236_().f_46443_) {
         if (this.followTarget == null && this.followTargetUUID != null) {
            if (((ServerLevel)this.m_9236_()).m_8791_(this.followTargetUUID) instanceof AlexEntity alex) {
               this.followTarget = alex;
            } else {
               this.followTargetUUID = null;
            }
         }

         if (this.followTarget != null && !this.followTarget.m_6084_()) {
            this.followTarget = null;
            this.followTargetUUID = null;
         }

         if (this.followTarget != null && this.followTarget.m_6084_()) {
            double distanceSq = this.m_20280_(this.followTarget);
            if (distanceSq > 600.0) {
               this.m_6021_(this.followTarget.m_20185_(), this.followTarget.m_20186_(), this.followTarget.m_20189_());
            }
         }
      }
   }

   @NotNull
   public MobType m_6336_() {
      return MobType.f_21640_;
   }

   @Override
   public void m_7380_(@NotNull CompoundTag tag) {
      super.m_7380_(tag);
      if (this.followTargetUUID != null) {
         tag.m_128362_("FollowTarget", this.followTargetUUID);
      }
   }

   @Override
   public void m_7378_(@NotNull CompoundTag tag) {
      super.m_7378_(tag);
      if (tag.m_128403_("FollowTarget")) {
         this.followTargetUUID = tag.m_128342_("FollowTarget");
      }
   }

   private void dropHookGunForAlex() {
      ItemStack hookGun = AlexJevHookCombat.createBoundHookGun(AlexJevHookCombat.createJevPickaxe());
      AlexEntity alex = this.followTarget != null && this.followTarget.m_6084_() ? this.followTarget : null;
      if (alex != null && !alex.canStoreInInventory(hookGun)) {
         alex.setCanDualHookInSecondPhase(true);
      } else {
         this.m_19983_(hookGun);
      }
   }

   private void dropRandomCombatSupplies() {
      RandomSource random = this.m_217043_();
      int blockRolls = 3 + random.m_188503_(5);

      for (int i = 0; i < blockRolls; i++) {
         if (random.m_188501_() < 0.82F) {
            this.m_19983_(withRandomCount(AlexJevHookCombat.createRandomJevLootBlock(random), 2, 12, random));
         }
      }

      int plantRolls = 3 + random.m_188503_(5);

      for (int ix = 0; ix < plantRolls; ix++) {
         if (random.m_188501_() < 0.86F) {
            this.m_19983_(withRandomCount(AlexJevHookCombat.createRandomJevPlantLoot(random), 1, 8, random));
         }
      }

      int foodRolls = 2 + random.m_188503_(5);

      for (int ixx = 0; ixx < foodRolls; ixx++) {
         if (random.m_188501_() < 0.84F) {
            this.m_19983_(withRandomCount(AlexJevHookCombat.createRandomJevLootFood(random), 1, 6, random));
         }
      }

      int potionRolls = 2 + random.m_188503_(4);

      for (int ixxx = 0; ixxx < potionRolls; ixxx++) {
         if (random.m_188501_() < 0.78F) {
            this.m_19983_(AlexJevHookCombat.createRandomJevLootPotion(random));
         }
      }
   }

   private static ItemStack withRandomCount(ItemStack stack, int minCount, int randomCount, RandomSource random) {
      if (stack.m_41619_()) {
         return stack;
      } else {
         int count = minCount + random.m_188503_(randomCount);
         stack.m_41764_(Math.min(count, stack.m_41741_()));
         return stack;
      }
   }

   public boolean m_6785_(double d0) {
      return false;
   }

   public double m_6049_() {
      return -0.35;
   }

   public SoundEvent m_7515_() {
      return (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "entity.villager.ambient"));
   }

   public SoundEvent m_7975_(@NotNull DamageSource damageSource) {
      return (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "entity.villager.hurt"));
   }

   public SoundEvent m_5592_() {
      return (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "entity.villager.death"));
   }

   @Override
   public boolean m_6469_(@NotNull DamageSource damageSource, float f) {
      if (this.getGapCooldown() == 0 && !this.isHealing() && this.m_21223_() - f <= 0.6666667F * this.m_21233_()) {
         boolean isEnchanted = this.f_19796_.m_188500_() <= Math.max(0.25, this.getPlaceBlockToParryChance());
         if (!this.m_9236_().f_46443_) {
            this.m_21008_(InteractionHand.MAIN_HAND, new ItemStack(isEnchanted ? Items.f_42437_ : Items.f_42436_));
         }

         this.setGapCooldown();
         CombatBehaviour.eatingGoldenApple(this, this.m_9236_(), 20.0, isEnchanted);
      }

      return super.m_6469_(damageSource, f);
   }

   public static Builder createAttributes() {
      Builder builder = Mob.m_21552_();
      builder = builder.m_22268_(Attributes.f_22279_, 0.35);
      builder = builder.m_22268_(Attributes.f_22276_, 50.0);
      builder = builder.m_22268_(Attributes.f_22284_, 20.0);
      builder = builder.m_22268_(Attributes.f_22281_, 0.0);
      builder = builder.m_22268_(Attributes.f_22277_, 48.0);
      return builder.m_22268_(Attributes.f_22278_, 5.0);
   }
}
