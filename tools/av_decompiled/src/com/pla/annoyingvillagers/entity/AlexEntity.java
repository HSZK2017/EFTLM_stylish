package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.clazz.AVNpc;
import com.pla.annoyingvillagers.clazz.BurstProtectEntity;
import com.pla.annoyingvillagers.combatbehaviour.AlexJevHookCombat;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.item.HookGunItem;
import com.pla.annoyingvillagers.spawnhandler.AlexData;
import com.pla.annoyingvillagers.util.CommonGoals;
import com.pla.annoyingvillagers.util.EquipmentDataLoader;
import com.pla.annoyingvillagers.util.TeamUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class AlexEntity extends AVNpc implements BurstProtectEntity {
   private JevEntity jevToProtect;
   private UUID jevUUID;
   private boolean spawnJev = false;
   private int state = 0;
   private ItemStack currentBoundHook = ItemStack.f_41583_;
   private boolean canDualHookInSecondPhase = false;
   protected float recentDamageTaken = 0.0F;
   protected int recentHitCounter = 0;

   public float getRecentDamageTaken() {
      return this.recentDamageTaken;
   }

   public void setRecentDamageTaken(float value) {
      this.recentDamageTaken = value;
   }

   public int getRecentHitCounter() {
      return this.recentHitCounter;
   }

   public void setRecentHitCounter(int value) {
      this.recentHitCounter = value;
   }

   public float getBurstProtectCapRatio() {
      return 0.15F;
   }

   public void setProtectingJev(JevEntity jev) {
      this.jevToProtect = jev;
   }

   public JevEntity getProtectingJev() {
      return this.jevToProtect;
   }

   public void setJevUUID(UUID jevUUID) {
      this.jevUUID = jevUUID;
   }

   public AlexEntity(SpawnEntity spawnEntity, Level level) {
      this((EntityType<AlexEntity>)AnnoyingVillagersModEntities.ALEX.get(), level);
   }

   public int getState() {
      return this.state;
   }

   public ItemStack getCurrentBoundHook() {
      if (this.currentBoundHook.m_41619_()) {
         this.currentBoundHook = AlexJevHookCombat.createAlexDefaultPickaxe();
      }

      return this.currentBoundHook.m_41777_();
   }

   public void setCurrentBoundHook(ItemStack currentBoundHook) {
      if (currentBoundHook.m_41619_()) {
         this.currentBoundHook = ItemStack.f_41583_;
      } else {
         ItemStack stored = currentBoundHook.m_41777_();
         stored.m_41764_(1);
         this.currentBoundHook = stored;
      }

      this.syncHookGunInventory();
   }

   public boolean canDualHookInSecondPhase() {
      return this.canDualHookInSecondPhase && this.state == 1;
   }

   public void setCanDualHookInSecondPhase(boolean canDualHookInSecondPhase) {
      this.canDualHookInSecondPhase = canDualHookInSecondPhase;
   }

   public void ensureHookGunInventory() {
      if (!this.m_9236_().f_46443_) {
         if (this.currentBoundHook.m_41619_()) {
            this.currentBoundHook = AlexJevHookCombat.createAlexDefaultPickaxe();
         }

         if (!this.syncHookGunInventory()) {
            this.addItemToInventory(AlexJevHookCombat.createBoundHookGun(this.currentBoundHook));
         }

         this.updateDualHookUnlockFromInventory();
      }
   }

   public AlexEntity(EntityType<AlexEntity> entitytype, Level level) {
      super(entitytype, level);
      this.m_274367_(2.8F);
      this.f_21364_ = 60;
      this.m_21557_(false);
      this.m_6593_(Component.m_237115_(this.m_6095_().m_20675_()));
      this.m_20340_(true);
      this.m_21530_();
      this.setPlaceBlockToParryChance(0.7);
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   @Override
   protected void m_8099_() {
      super.m_8099_();
      this.f_21346_
         .m_25352_(
            1,
            new NearestAttackableTargetGoal(
               this,
               LivingEntity.class,
               10,
               true,
               false,
               target -> this.jevToProtect != null && this.jevToProtect.m_6084_() && target != null && target.m_21214_() == this.jevToProtect
            )
         );
      CommonGoals.registerGoalForNeutralNpc(this);
   }

   public void setState(int state) {
      this.state = state;
      if (state == 1 && this.jevToProtect != null && this.m_9236_() instanceof ServerLevel) {
         this.jevToProtect.m_5496_((SoundEvent)AnnoyingVillagersModSounds.JEV_SAY_WHEN_ALEX_SECOND_PHASE.get(), 0.5F, 1.0F);
      }
   }

   @Override
   public void m_7380_(@NotNull CompoundTag tag) {
      super.m_7380_(tag);
      if (this.jevUUID != null) {
         tag.m_128362_("JevUUID", this.jevUUID);
      }

      tag.m_128405_("State", this.state);
      tag.m_128379_("SpawnJev", this.spawnJev);
      if (!this.currentBoundHook.m_41619_()) {
         CompoundTag hookTag = new CompoundTag();
         this.currentBoundHook.m_41739_(hookTag);
         tag.m_128365_("CurrentBoundHook", hookTag);
      }

      tag.m_128379_("CanDualHookInSecondPhase", this.canDualHookInSecondPhase);
   }

   @Override
   public void m_7378_(@NotNull CompoundTag tag) {
      super.m_7378_(tag);
      if (tag.m_128403_("JevUUID")) {
         this.jevUUID = tag.m_128342_("JevUUID");
      }

      this.state = tag.m_128451_("State");
      this.spawnJev = tag.m_128471_("SpawnJev");
      if (tag.m_128425_("CurrentBoundHook", 10)) {
         this.currentBoundHook = ItemStack.m_41712_(tag.m_128469_("CurrentBoundHook"));
      } else {
         this.currentBoundHook = AlexJevHookCombat.createAlexDefaultPickaxe();
      }

      this.canDualHookInSecondPhase = tag.m_128471_("CanDualHookInSecondPhase");
   }

   public SoundEvent getAttackVoiceSound() {
      return (SoundEvent)AnnoyingVillagersModSounds.ALEX_SAY.get();
   }

   @NotNull
   public MobType m_6336_() {
      return MobType.f_21640_;
   }

   public boolean m_6785_(double d0) {
      return false;
   }

   public double m_6049_() {
      return -0.35;
   }

   public SoundEvent m_7975_(@NotNull DamageSource damageSource) {
      return (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "entity.generic.hurt"));
   }

   public SoundEvent m_5592_() {
      return (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "entity.generic.death"));
   }

   @Override
   protected boolean hasEnderPearlCounter() {
      return true;
   }

   @Override
   protected void beforeEnderPearlCounter(@NotNull DamageSource damageSource) {
      if (this.f_19796_.m_188500_() <= 0.2 && this.m_20194_() != null) {
      }
   }

   @Override
   protected void doEnderPearlCounterPattern(@NotNull DamageSource damageSource) {
      this.doChrisStyleEnderPearlCounter();
   }

   @Override
   protected boolean afterBurstProtection(@NotNull ServerLevel serverLevel, @NotNull DamageSource source, float finalDamage) {
      if (this.state == 0 && this.m_21223_() - finalDamage <= 1.0F && !this.m_21206_().m_150930_(Items.f_42747_)) {
         this.m_21153_(1.0F);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected void m_7472_(@NotNull DamageSource source, int looting, boolean recentlyHit) {
      super.m_7472_(source, looting, recentlyHit);
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         double x = this.m_20185_();
         double y = this.m_20186_() + 1.0;
         double z = this.m_20189_();
         Consumer<ItemStack> dropStack = stackx -> {
            ItemEntity drop = new ItemEntity(serverLevel, x, y, z, stackx);
            drop.m_32010_(10);
            serverLevel.m_7967_(drop);
         };
         Consumer<Integer> dropArrows = count -> {
            for (int i = 0; i < count; i++) {
               dropStack.accept(new ItemStack(Items.f_42412_));
            }
         };
         List<ItemStack> damagedStacks = new ArrayList<>();
         ItemStack sword = new ItemStack((ItemLike)AnnoyingVillagersModItems.THUNDER_DIAMOND_BLADE.get());
         sword.m_41663_(Enchantments.f_44977_, 5);
         sword.m_41663_(Enchantments.f_44981_, 2);
         sword.m_41663_(Enchantments.f_44980_, 2);
         sword.m_41663_(Enchantments.f_44986_, 5);
         damagedStacks.add(sword);
         ItemStack bow = this.getBowItem();
         bow.m_41663_(Enchantments.f_44989_, 3);
         bow.m_41663_(Enchantments.f_44988_, 3);
         bow.m_41663_(Enchantments.f_44990_, 2);
         damagedStacks.add(bow);

         for (ItemStack stack : damagedStacks) {
            stack.m_41721_(EquipmentDataLoader.getRandomDamage(stack));
            dropStack.accept(stack);
         }

         ItemStack[] simpleDrops = new ItemStack[]{
            new ItemStack(Items.f_42406_),
            new ItemStack(Items.f_42436_),
            new ItemStack(Items.f_42405_),
            new ItemStack(Items.f_42675_),
            new ItemStack(Items.f_42417_),
            new ItemStack(Items.f_42416_),
            new ItemStack(Items.f_42415_),
            new ItemStack(Items.f_42415_),
            new ItemStack(Items.f_42416_),
            new ItemStack(Items.f_42416_),
            new ItemStack(Items.f_42416_),
            new ItemStack(Items.f_42416_),
            new ItemStack(Items.f_42416_),
            new ItemStack(Items.f_42437_),
            new ItemStack(Items.f_42436_),
            new ItemStack(Items.f_42503_),
            new ItemStack(Items.f_42502_)
         };

         for (ItemStack stack : simpleDrops) {
            dropStack.accept(stack);
         }

         dropStack.accept(AlexJevHookCombat.createBoundHookGun(this.getCurrentBoundHook()));
         dropStack.accept(this.getCurrentBoundHook());
         dropArrows.accept(new Random().nextInt(10, 20));
      }
   }

   private void spawnJev() {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         JevEntity jevEntity = new JevEntity((EntityType)AnnoyingVillagersModEntities.JEV.get(), serverLevel);
         jevEntity.m_7678_(
            this.m_20185_() + new Random().nextDouble(1.0, 10.0),
            this.m_20186_() + new Random().nextDouble(1.0, 10.0),
            this.m_20189_() + new Random().nextDouble(1.0, 10.0),
            serverLevel.m_213780_().m_188501_() * 360.0F,
            0.0F
         );
         jevEntity.setFollowTarget(this);
         jevEntity.setFollowTargetUUID(this.m_20148_());
         jevEntity.m_6518_(serverLevel, serverLevel.m_6436_(this.m_20183_()), MobSpawnType.MOB_SUMMONED, null, null);
         serverLevel.m_7967_(jevEntity);
         this.setJevUUID(jevEntity.m_20148_());
         this.setProtectingJev(jevEntity);
      }
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

   public SpawnGroupData m_6518_(
      @NotNull ServerLevelAccessor serverLevelAccessor,
      @NotNull DifficultyInstance difficultyInstance,
      @NotNull MobSpawnType mobSpawnType,
      @Nullable SpawnGroupData spawngroupdata,
      @Nullable CompoundTag compoundtag
   ) {
      if (mobSpawnType == MobSpawnType.NATURAL || mobSpawnType == MobSpawnType.CHUNK_GENERATION) {
         ServerLevel serverLevel = serverLevelAccessor.m_6018_();
         AlexData alexData = AlexData.get(serverLevel);
         if (!alexData.tryClaim(serverLevel, this.m_20148_())) {
            this.m_146870_();
            return null;
         }
      }

      SpawnGroupData returnSpawnGroupData = super.m_6518_(serverLevelAccessor, difficultyInstance, mobSpawnType, spawngroupdata, compoundtag);
      TeamUtil.addOrJoinTeam(this, "alex");
      ItemStack sword = new ItemStack((ItemLike)AnnoyingVillagersModItems.THUNDER_DIAMOND_BLADE.get());
      sword.m_41663_(Enchantments.f_44977_, 5);
      sword.m_41663_(Enchantments.f_44981_, 2);
      sword.m_41663_(Enchantments.f_44980_, 2);
      sword.m_41663_(Enchantments.f_44986_, 5);
      this.m_8061_(EquipmentSlot.MAINHAND, sword);
      this.m_8061_(EquipmentSlot.OFFHAND, new ItemStack(Items.f_42584_));
      this.setMainWeaponItem(sword);
      this.setOffWeaponItem(new ItemStack(Items.f_42584_));
      this.setCurrentBoundHook(AlexJevHookCombat.createAlexDefaultPickaxe());
      this.ensureHookGunInventory();
      return returnSpawnGroupData;
   }

   public void m_6667_(@NotNull DamageSource damageSource) {
      if (!this.m_9236_().f_46443_) {
         AlexJevHookCombat.onAlexDeath(this);
      }

      super.m_6667_(damageSource);
   }

   @Override
   public void m_8119_() {
      super.m_8119_();
      if (!this.m_9236_().f_46443_) {
         if (!this.spawnJev) {
            this.spawnJev = true;
            this.spawnJev();
         }

         if (this.jevToProtect == null && this.jevUUID != null) {
            if (((ServerLevel)this.m_9236_()).m_8791_(this.jevUUID) instanceof JevEntity jev) {
               this.jevToProtect = jev;
            } else {
               this.jevUUID = null;
            }
         }

         if (this.jevToProtect != null && !this.jevToProtect.m_6084_()) {
            this.jevToProtect = null;
            this.jevUUID = null;
         }

         if (this.state == 0 && this.m_21223_() <= 20.0F && !this.m_21120_(InteractionHand.OFF_HAND).m_41720_().equals(Items.f_42747_)) {
            this.m_21008_(InteractionHand.OFF_HAND, new ItemStack(Items.f_42747_));
         }

         this.ensureHookGunInventory();
      }
   }

   public boolean canStoreInInventory(ItemStack stack) {
      if (stack.m_41619_()) {
         return true;
      } else {
         for (int i = 0; i < this.getInventory().m_6643_(); i++) {
            ItemStack slotStack = this.getInventory().m_8020_(i);
            if (slotStack.m_41619_()) {
               return true;
            }

            if (ItemStack.m_150942_(slotStack, stack) && slotStack.m_41613_() < slotStack.m_41741_()) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean syncHookGunInventory() {
      for (int i = 0; i < this.getInventory().m_6643_(); i++) {
         ItemStack stack = this.getInventory().m_8020_(i);
         if (stack.m_41720_() instanceof HookGunItem) {
            HookGunItem.setBoundItem(stack, this.getCurrentBoundHook());
            this.getInventory().m_6836_(i, stack);
            return true;
         }
      }

      return false;
   }

   private void addItemToInventory(ItemStack stack) {
      if (!stack.m_41619_()) {
         for (int i = 0; i < this.getInventory().m_6643_(); i++) {
            if (this.getInventory().m_8020_(i).m_41619_()) {
               this.getInventory().m_6836_(i, stack.m_41777_());
               return;
            }
         }
      }
   }

   private void updateDualHookUnlockFromInventory() {
      if (!this.canDualHookInSecondPhase && this.countHookGunsInInventory() >= 2) {
         this.canDualHookInSecondPhase = true;
      }
   }

   private int countHookGunsInInventory() {
      int count = 0;

      for (int i = 0; i < this.getInventory().m_6643_(); i++) {
         ItemStack stack = this.getInventory().m_8020_(i);
         if (stack.m_41720_() instanceof HookGunItem) {
            count += stack.m_41613_();
         }
      }

      return count;
   }

   public static boolean canSpawn(EntityType<AlexEntity> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos position, RandomSource random) {
      ServerLevel serverLevel = level.m_6018_();
      return AlexData.get(serverLevel).isOccupied(serverLevel) ? false : PathfinderMob.m_217057_(entityType, level, spawnType, position, random);
   }

   public void m_142687_(@NotNull RemovalReason reason) {
      super.m_142687_(reason);
      if (!this.m_9236_().f_46443_
         && this.m_9236_() instanceof ServerLevel serverLevel
         && (reason == RemovalReason.KILLED || reason == RemovalReason.DISCARDED)) {
         AlexData.get(serverLevel).releaseIfMatches(serverLevel, this.m_20148_());
      }
   }

   public static Builder createAttributes() {
      return Mob.m_21552_()
         .m_22268_(Attributes.f_22276_, 50.0)
         .m_22268_(Attributes.f_22279_, 0.35)
         .m_22268_(Attributes.f_22281_, 0.0)
         .m_22268_(Attributes.f_22277_, 64.0)
         .m_22268_(Attributes.f_22284_, 10.0)
         .m_22268_(Attributes.f_22285_, 20.0)
         .m_22268_(Attributes.f_22278_, 1.0)
         .m_22268_((Attribute)EpicFightAttributes.IMPACT.get(), 2.0)
         .m_22268_((Attribute)EpicFightAttributes.ARMOR_NEGATION.get(), 5.0)
         .m_22268_((Attribute)EpicFightAttributes.STUN_ARMOR.get(), 20.0)
         .m_22268_((Attribute)EpicFightAttributes.MAX_STRIKES.get(), 50.0)
         .m_22268_((Attribute)EpicFightAttributes.MAX_STAMINA.get(), 30.0)
         .m_22268_((Attribute)EpicFightAttributes.STAMINA_REGEN.get(), 1.5);
   }
}
