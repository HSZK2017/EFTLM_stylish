package com.pla.annoyingvillagers.clazz;

import com.pla.annoyingvillagers.combatbehaviour.CombatCommon;
import com.pla.annoyingvillagers.entity.AngrySteveEntity;
import com.pla.annoyingvillagers.entity.BlueVillagerKnightEntity;
import com.pla.annoyingvillagers.entity.GreenVillagerKnightEntity;
import com.pla.annoyingvillagers.entity.PurpleVillagerKnightEntity;
import com.pla.annoyingvillagers.entity.RedVillagerKnightEntity;
import com.pla.annoyingvillagers.entity.VillagerScoutCaptainEntity;
import com.pla.annoyingvillagers.entity.goal.BowLineOfSightGoal;
import com.pla.annoyingvillagers.entity.goal.BurnNearbyItemGoal;
import com.pla.annoyingvillagers.entity.goal.LockedRandomStrollGoal;
import com.pla.annoyingvillagers.entity.goal.PlayIdleAnimationGoal;
import com.pla.annoyingvillagers.entity.goal.RecoverWeaponInCombatGoal;
import com.pla.annoyingvillagers.entity.goal.RetargetCloserThreatGoal;
import com.pla.annoyingvillagers.entity.goal.WaterEnderPearlEscapeGoal;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFightIronSpell;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.task.DelayedTask;
import com.pla.annoyingvillagers.util.BowFunction;
import com.pla.annoyingvillagers.util.CombatBehaviour;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import com.pla.annoyingvillagers.util.EquipmentDataLoader;
import java.util.Objects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class AVNpc extends PathfinderMob implements RangedAttackMob, CombatVoiceLineEntity {
   private static final int PLACE_BLOCK_PARRY_COOLDOWN_TICKS = 60;
   private static final float VILLAGER_ARMOR_DROP_CHANCE = 0.18F;
   private static final float VILLAGER_WEAPON_DROP_CHANCE = 0.22F;
   private static final float VILLAGER_OFFHAND_EQUIPMENT_DROP_CHANCE = 0.14F;
   private static final float VILLAGER_EQUIPMENT_LOOTING_BONUS = 0.025F;
   private static final float AVNPC_WATER_BUCKET_DROP_CHANCE = 0.2F;
   private static final float VILLAGER_KNIGHT_LAVA_BUCKET_DROP_CHANCE = 0.24F;
   private static final float UTILITY_BUCKET_LOOTING_BONUS = 0.04F;
   private final SimpleContainer inventory = new SimpleContainer(27);
   private int gapCooldown;
   private int enderPearlCooldown;
   private int swapToBowCooldown = 0;
   private ItemStack mainWeaponItem = ItemStack.f_41583_;
   private ItemStack offWeaponItem = ItemStack.f_41583_;
   private boolean healing = false;
   private boolean initialSpawn = false;
   private boolean useBow = true;
   private Entity blockDamage = null;
   private double placeBlockToParryChance;
   private int placeBlockParryCooldown = 0;
   private boolean swapBackToBow = false;
   private int stunEscapeCooldown = 0;
   @Nullable
   private IdleAnimation idleAnimationChoice;
   @Nullable
   private AssetAccessor<? extends StaticAnimation> idleAnimationAsset;
   private boolean idleMessageBroadcast = false;
   private boolean playingIdle;
   private int playingIdleCooldown = 1200;
   private boolean isStrolling;
   private int efnGuardHitState = 0;
   private int efnGuardHitCooldown = 0;
   private boolean mainWeaponDisarmed = false;
   private int voiceCooldown = 0;

   public boolean isMainWeaponDisarmed() {
      return this.mainWeaponDisarmed;
   }

   public void setMainWeaponDisarmed(boolean mainWeaponDisarmed) {
      this.mainWeaponDisarmed = mainWeaponDisarmed;
   }

   public int getEfnGuardHitState() {
      return this.efnGuardHitState;
   }

   public int getVoiceCooldown() {
      return this.voiceCooldown;
   }

   public void setVoiceCooldown(int cooldown) {
      this.voiceCooldown = cooldown;
   }

   public void postPlayEfnGuardHit() {
      if (this.efnGuardHitState == 2) {
         this.efnGuardHitState = 0;
      } else {
         this.efnGuardHitState++;
      }

      this.efnGuardHitCooldown = 100;
   }

   public boolean isStrolling() {
      return this.isStrolling;
   }

   public void setStrolling(boolean strolling) {
      this.isStrolling = strolling;
   }

   public boolean isPlayingIdle() {
      return this.playingIdle;
   }

   public void setPlayingIdle(boolean playingIdle) {
      this.playingIdle = playingIdle;
   }

   public int getPlayingIdleCooldown() {
      return this.playingIdleCooldown;
   }

   public void setPlayingIdleCooldown(int playingIdleCooldown) {
      this.playingIdleCooldown = playingIdleCooldown;
   }

   @Nullable
   public IdleAnimation getIdleAnimationChoice() {
      return this.idleAnimationChoice;
   }

   public void setIdleAnimationChoice(@Nullable IdleAnimation choice) {
      this.idleAnimationChoice = choice;
   }

   @Nullable
   public AssetAccessor<? extends StaticAnimation> getIdleAnimation() {
      return this.idleAnimationAsset;
   }

   public void setIdleAnimation(@Nullable AssetAccessor<? extends StaticAnimation> anim) {
      this.idleAnimationAsset = anim;
   }

   public boolean isIdleMessageBroadcast() {
      return this.idleMessageBroadcast;
   }

   public void setIdleMessageBroadcast(boolean idleMessageBroadcast) {
      this.idleMessageBroadcast = idleMessageBroadcast;
   }

   public void clearIdleAnimationState() {
      this.idleAnimationChoice = null;
      this.idleAnimationAsset = null;
      this.idleMessageBroadcast = false;
   }

   public int getStunEscapeCooldown() {
      return this.stunEscapeCooldown;
   }

   public void setStunEscapeCooldown(int stunEscapeCooldown) {
      this.stunEscapeCooldown = stunEscapeCooldown;
   }

   public Entity getBlockDamage() {
      return this.blockDamage;
   }

   public void setSwapBackToBow(boolean swapBackToBow) {
      this.swapBackToBow = swapBackToBow;
   }

   public boolean isSwapBackToBow() {
      return this.swapBackToBow;
   }

   public double getPlaceBlockToParryChance() {
      return this.placeBlockToParryChance;
   }

   public void setPlaceBlockToParryChance(double placeBlockToParryChance) {
      this.placeBlockToParryChance = placeBlockToParryChance;
   }

   public boolean rollsPlaceBlockToParryChance() {
      return this.placeBlockParryCooldown == 0 && this.blockDamage == null && !this.isHealing() && this.f_19796_.m_188500_() <= this.placeBlockToParryChance;
   }

   public boolean hasPlaceBlockParryCooldown() {
      return this.placeBlockParryCooldown > 0;
   }

   public void setPlaceBlockParryCooldown() {
      this.placeBlockParryCooldown = 60;
   }

   public void setBlockDamage(Entity blockDamage) {
      this.blockDamage = blockDamage;
   }

   public boolean isHealing() {
      return this.healing;
   }

   public void setHealing(boolean healing) {
      this.healing = healing;
   }

   public int getSwapToBowCooldown() {
      return this.swapToBowCooldown;
   }

   public void setSwapToBowCooldown() {
      this.swapToBowCooldown = this.f_19796_.m_216339_(100, 300);
   }

   public ItemStack getBowItem() {
      return new ItemStack(Items.f_42411_);
   }

   @Nullable
   public LivingEntityPatch<?> getLivingEntityPatch() {
      return (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(this, LivingEntityPatch.class);
   }

   public int getGapCooldown() {
      return this.gapCooldown;
   }

   public int getEnderPearlCooldown() {
      return this.enderPearlCooldown;
   }

   public void setGapCooldown() {
      this.gapCooldown = this.f_19796_.m_216339_(100, 300);
   }

   public void resetGapCooldown() {
      this.gapCooldown = 0;
   }

   public void setEnderPearlCooldown() {
      this.enderPearlCooldown = this.f_19796_.m_216339_(100, 300);
   }

   public ItemStack getMainWeaponItem() {
      return this.mainWeaponItem;
   }

   public void setMainWeaponItem(ItemStack mainWeaponItem) {
      this.mainWeaponItem = mainWeaponItem.m_41777_();
      if (!this.mainWeaponItem.m_41619_()) {
         this.mainWeaponDisarmed = false;
      }
   }

   public ItemStack getOffWeaponItem() {
      return this.offWeaponItem;
   }

   public void setOffWeaponItem(ItemStack offWeaponItem) {
      this.offWeaponItem = offWeaponItem;
   }

   public SimpleContainer getInventory() {
      return this.inventory;
   }

   public void setUseBow(boolean useBow) {
      this.useBow = useBow;
   }

   public boolean isUseBow() {
      return this.useBow;
   }

   protected AVNpc(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.m_21409_(EquipmentSlot.MAINHAND, 0.0F);
      this.m_21409_(EquipmentSlot.OFFHAND, 0.0F);
      this.m_21409_(EquipmentSlot.CHEST, 0.0F);
      this.m_21409_(EquipmentSlot.HEAD, 0.0F);
      this.m_21409_(EquipmentSlot.LEGS, 0.0F);
      this.m_21409_(EquipmentSlot.FEET, 0.0F);
   }

   public void m_7380_(@NotNull CompoundTag tag) {
      super.m_7380_(tag);
      tag.m_128365_("Inventory", this.inventory.m_7927_());
      tag.m_128405_("GapCooldown", this.gapCooldown);
      tag.m_128405_("EnderPearlCooldown", this.enderPearlCooldown);
      tag.m_128405_("SwapToBowCooldown", this.swapToBowCooldown);
      tag.m_128379_("InitialSpawn", this.initialSpawn);
      tag.m_128379_("UseBow", this.useBow);
      tag.m_128347_("BlockProjectileChance", this.placeBlockToParryChance);
      tag.m_128405_("BlockParryCooldown", this.placeBlockParryCooldown);
      if (!this.mainWeaponItem.m_41619_()) {
         CompoundTag itemTag = new CompoundTag();
         this.mainWeaponItem.m_41739_(itemTag);
         tag.m_128365_("MainHandItem", itemTag);
      }

      if (!this.offWeaponItem.m_41619_()) {
         CompoundTag itemTag = new CompoundTag();
         this.offWeaponItem.m_41739_(itemTag);
         tag.m_128365_("OffHandItem", itemTag);
      }

      tag.m_128405_("VoiceCooldown", this.voiceCooldown);
      tag.m_128379_("MainWeaponDisarmed", this.mainWeaponDisarmed);
   }

   public void m_238392_(@NotNull EquipmentSlot pSlot, @NotNull ItemStack pOldItem, @NotNull ItemStack pNewItem) {
      if (pSlot == EquipmentSlot.MAINHAND && (pNewItem.m_41720_() instanceof SwordItem || pNewItem.m_41720_() instanceof AxeItem)) {
         this.mainWeaponItem = pNewItem.m_41777_();
         this.mainWeaponDisarmed = false;
      }

      if (pSlot == EquipmentSlot.OFFHAND
         && (pNewItem.m_41720_() instanceof SwordItem || pNewItem.m_41720_() instanceof AxeItem || pNewItem.m_41720_() instanceof ShieldItem)) {
         this.offWeaponItem = pNewItem.m_41777_();
      }

      super.m_238392_(pSlot, pOldItem, pNewItem);
      if (!this.m_9236_().f_46443_) {
         if (this.m_6084_() && !this.m_21224_() && !(this.m_21223_() <= 0.0F)) {
            if (this.isPlayingIdle() && this.getLivingEntityPatch() != null && this.idleAnimationAsset != null) {
               this.getLivingEntityPatch().playAnimationSynchronized(this.idleAnimationAsset, 0.0F);
            }
         }
      }
   }

   public void m_7378_(@NotNull CompoundTag tag) {
      super.m_7378_(tag);
      if (tag.m_128425_("Inventory", 10)) {
         this.inventory.m_7797_(tag.m_128437_("Inventory", 10));
      }

      this.gapCooldown = tag.m_128451_("GapCooldown");
      this.enderPearlCooldown = tag.m_128451_("EnderPearlCooldown");
      this.swapToBowCooldown = tag.m_128451_("SwapToBowCooldown");
      this.initialSpawn = tag.m_128471_("InitialSpawn");
      this.useBow = tag.m_128471_("UseBow");
      this.placeBlockToParryChance = tag.m_128459_("BlockProjectileChance");
      this.placeBlockParryCooldown = tag.m_128451_("BlockParryCooldown");
      if (tag.m_128425_("MainHandItem", 10)) {
         this.mainWeaponItem = ItemStack.m_41712_(tag.m_128469_("MainHandItem"));
      } else {
         this.mainWeaponItem = ItemStack.f_41583_;
      }

      if (tag.m_128425_("OffHandItem", 10)) {
         this.offWeaponItem = ItemStack.m_41712_(tag.m_128469_("OffHandItem"));
      } else {
         this.offWeaponItem = ItemStack.f_41583_;
      }

      this.voiceCooldown = tag.m_128451_("VoiceCooldown");
      this.mainWeaponDisarmed = tag.m_128471_("MainWeaponDisarmed");
   }

   protected void m_7472_(@NotNull DamageSource source, int looting, boolean recentlyHit) {
      super.m_7472_(source, looting, recentlyHit);

      for (int i = 0; i < this.inventory.m_6643_(); i++) {
         ItemStack stack = this.inventory.m_8020_(i);
         if (!stack.m_41619_()) {
            this.m_19983_(stack);
         }
      }

      this.dropVillagerCombatEquipment(looting);
      this.dropUtilityBucketLoot(looting);
   }

   private void dropUtilityBucketLoot(int looting) {
      if (this.rollUtilityBucketDrop(0.2F, looting)) {
         this.m_19983_(new ItemStack(Items.f_42447_));
      }

      if (this.isVillagerKnight() && this.rollUtilityBucketDrop(0.24F, looting)) {
         this.m_19983_(new ItemStack(Items.f_42448_));
      }
   }

   private boolean rollUtilityBucketDrop(float baseChance, int looting) {
      float chance = Math.min(0.75F, baseChance + (float)looting * 0.04F);
      return this.f_19796_.m_188501_() <= chance;
   }

   private void dropVillagerCombatEquipment(int looting) {
      if (this.shouldDropVillagerCombatEquipment()) {
         this.tryDropVillagerEquipmentSlot(EquipmentSlot.MAINHAND, 0.22F, looting);
         this.tryDropVillagerEquipmentSlot(EquipmentSlot.OFFHAND, 0.14F, looting);
         this.tryDropVillagerEquipmentSlot(EquipmentSlot.HEAD, 0.18F, looting);
         this.tryDropVillagerEquipmentSlot(EquipmentSlot.CHEST, 0.18F, looting);
         this.tryDropVillagerEquipmentSlot(EquipmentSlot.LEGS, 0.18F, looting);
         this.tryDropVillagerEquipmentSlot(EquipmentSlot.FEET, 0.18F, looting);
      }
   }

   private boolean shouldDropVillagerCombatEquipment() {
      return this.isVillagerKnight() || this instanceof VillagerScoutCaptainEntity;
   }

   private boolean isVillagerKnight() {
      return this instanceof BlueVillagerKnightEntity
         || this instanceof GreenVillagerKnightEntity
         || this instanceof RedVillagerKnightEntity
         || this instanceof PurpleVillagerKnightEntity;
   }

   private void tryDropVillagerEquipmentSlot(EquipmentSlot slot, float baseChance, int looting) {
      ItemStack equipped = this.getDroppableEquipmentStack(slot);
      if (!equipped.m_41619_()) {
         float chance = Math.min(0.85F, baseChance + (float)looting * 0.025F);
         if (!(this.f_19796_.m_188501_() > chance)) {
            ItemStack drop = this.prepareVillagerEquipmentDrop(equipped);
            if (!drop.m_41619_()) {
               this.m_19983_(drop);
            }
         }
      }
   }

   private ItemStack getDroppableEquipmentStack(EquipmentSlot slot) {
      ItemStack equipped = this.m_6844_(slot);
      if (slot == EquipmentSlot.MAINHAND) {
         if (!this.mainWeaponItem.m_41619_()) {
            return this.mainWeaponItem.m_41777_();
         } else {
            return this.isDroppableMainhandEquipment(equipped) ? equipped : ItemStack.f_41583_;
         }
      } else {
         return slot == EquipmentSlot.OFFHAND && !this.isDroppableOffhandEquipment(equipped) ? ItemStack.f_41583_ : equipped;
      }
   }

   private boolean isDroppableMainhandEquipment(ItemStack stack) {
      Item item = stack.m_41720_();
      return item instanceof SwordItem || item instanceof DiggerItem || item instanceof TridentItem;
   }

   private boolean isDroppableOffhandEquipment(ItemStack stack) {
      Item item = stack.m_41720_();
      return item instanceof SwordItem || item instanceof AxeItem || item instanceof ShieldItem;
   }

   private ItemStack prepareVillagerEquipmentDrop(ItemStack equipped) {
      ItemStack drop = this.convertVillagerHelmetFixItem(equipped);
      drop.m_41764_(1);
      if (drop.m_41763_()) {
         int maxDamage = drop.m_41776_();
         int minDamage = Math.max(1, maxDamage / 3);
         int maxDamageBound = Math.max(minDamage + 1, maxDamage * 3 / 4);
         drop.m_41721_(this.f_19796_.m_216339_(minDamage, maxDamageBound));
      }

      return drop;
   }

   protected ItemStack createDamagedDropStack(Item item) {
      ItemStack stack = new ItemStack(item);
      if (stack.m_41763_()) {
         stack.m_41721_(EquipmentDataLoader.getRandomDamage(stack));
      }

      return stack;
   }

   private ItemStack convertVillagerHelmetFixItem(ItemStack equipped) {
      Item replacement = null;
      if (equipped.m_150930_((Item)AnnoyingVillagersModItems.VILLAGER_SCOUT_HELMET_FIX.get())) {
         replacement = (Item)AnnoyingVillagersModItems.VILLAGER_SCOUT_HELMET.get();
      } else if (equipped.m_150930_((Item)AnnoyingVillagersModItems.BLUE_VILLAGER_KNIGHT_HELMET_FIX.get())) {
         replacement = (Item)AnnoyingVillagersModItems.BLUE_VILLAGER_KNIGHT_HELMET.get();
      } else if (equipped.m_150930_((Item)AnnoyingVillagersModItems.RED_VILLAGER_KNIGHT_HELMET_FIX.get())) {
         replacement = (Item)AnnoyingVillagersModItems.RED_VILLAGER_KNIGHT_HELMET.get();
      } else if (equipped.m_150930_((Item)AnnoyingVillagersModItems.GREEN_VILLAGER_KNIGHT_HELMET_FIX.get())) {
         replacement = (Item)AnnoyingVillagersModItems.GREEN_VILLAGER_KNIGHT_HELMET.get();
      } else if (equipped.m_150930_((Item)AnnoyingVillagersModItems.PURPLE_VILLAGER_KNIGHT_HELMET_FIX.get())) {
         replacement = (Item)AnnoyingVillagersModItems.PURPLE_VILLAGER_KNIGHT_HELMET.get();
      }

      if (replacement == null) {
         return equipped.m_41777_();
      } else {
         ItemStack converted = new ItemStack(replacement);
         if (equipped.m_41782_()) {
            converted.m_41751_(equipped.m_41783_().m_6426_());
         }

         return converted;
      }
   }

   protected void m_8099_() {
      super.m_8099_();
      this.f_21346_.m_25352_(0, new RetargetCloserThreatGoal(this));
      this.f_21345_.m_25352_(-3, new WaterEnderPearlEscapeGoal(this));
      this.f_21345_.m_25352_(-2, new RecoverWeaponInCombatGoal(this, 1.2, 10.0));
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21345_.m_25352_(4, new BowLineOfSightGoal(this, 1.15, 7.0, 14.0));
      this.f_21345_.m_25352_(5, new BurnNearbyItemGoal(this, 1.0, 10.0));
      this.f_21345_.m_25352_(6, new PlayIdleAnimationGoal(this, new Random().nextInt(3000, 6000)));
      this.f_21345_.m_25352_(7, new LockedRandomStrollGoal(this, 1.0));
   }

   public boolean m_5886_(@NotNull ProjectileWeaponItem item) {
      return item instanceof BowItem;
   }

   public boolean canFireProjectileWeapon(@NotNull Item item) {
      return item instanceof ProjectileWeaponItem weaponItem ? this.m_5886_(weaponItem) : false;
   }

   public void m_6504_(@NotNull LivingEntity pTarget, float pVelocity) {
      if (BowFunction.hasClearShot(this, pTarget)) {
         ItemStack weaponStack = this.m_21120_(ProjectileUtil.getWeaponHoldingHand(this, this::canFireProjectileWeapon));
         ItemStack itemstack = this.m_6298_(weaponStack);
         AbstractArrow mobArrow = ProjectileUtil.m_37300_(this, itemstack, pVelocity);
         if (this.m_21205_().m_41720_() instanceof BowItem) {
            mobArrow = ((BowItem)this.m_21205_().m_41720_()).customArrow(mobArrow);
         }

         double x = pTarget.m_20185_() - this.m_20185_();
         double y = pTarget.m_20227_(0.3333333333333333) - mobArrow.m_20186_();
         double z = pTarget.m_20189_() - this.m_20189_();
         double d3 = Math.sqrt(x * x + z * z);
         mobArrow.m_5602_(this);
         mobArrow.m_6686_(x, y + d3 * 0.2F, z, 1.6F, (float)(14 - this.m_9236_().m_46791_().m_19028_() * 4));
         this.m_5496_(SoundEvents.f_11687_, 1.0F, 1.0F / (this.m_217043_().m_188501_() * 0.4F + 0.8F));
         this.m_9236_().m_7967_(mobArrow);
      }
   }

   private boolean isInventoryFull() {
      for (int i = 0; i < this.inventory.m_6643_(); i++) {
         ItemStack s = this.inventory.m_8020_(i);
         if (s.m_41619_() || s.m_41613_() < s.m_41741_()) {
            return false;
         }
      }

      return true;
   }

   private void pickupNearbyItems() {
      if (this.m_6084_() && !this.m_213877_() && !this.m_21224_()) {
         AABB box = this.m_20191_().m_82400_(1.5);

         for (ItemEntity itemEntity : this.m_9236_().m_6443_(ItemEntity.class, box, e -> !e.m_213877_() && !e.m_32063_())) {
            this.tryPickup(itemEntity);
         }
      }
   }

   private void tryPickup(ItemEntity itemEntity) {
      ItemStack remaining = itemEntity.m_32055_().m_41777_();

      for (int i = 0; i < this.inventory.m_6643_() && !remaining.m_41619_() && !remaining.m_41619_(); i++) {
         ItemStack slotStack = this.inventory.m_8020_(i);
         if (slotStack.m_41619_()) {
            this.inventory.m_6836_(i, remaining);
            remaining = ItemStack.f_41583_;
            break;
         }

         if (ItemStack.m_150942_(slotStack, remaining) && slotStack.m_41613_() < slotStack.m_41741_()) {
            int transferable = Math.min(remaining.m_41613_(), slotStack.m_41741_() - slotStack.m_41613_());
            slotStack.m_41769_(transferable);
            remaining.m_41774_(transferable);
         }
      }

      if (remaining.m_41619_()) {
         itemEntity.m_20334_(
            (this.m_20185_() - itemEntity.m_20185_()) * 0.25,
            (this.m_20186_() + 1.0 - itemEntity.m_20186_()) * 0.25,
            (this.m_20189_() - itemEntity.m_20189_()) * 0.25
         );
         itemEntity.m_32010_(0);
         itemEntity.m_146870_();
         this.m_9236_().m_5594_(null, this.m_20183_(), SoundEvents.f_12019_, SoundSource.HOSTILE, 0.2F, 1.0F);
      } else {
         itemEntity.m_32045_(remaining);
      }
   }

   protected void implementFirstTick(ServerLevel serverLevel) {
   }

   public void jump() {
      this.m_6135_();
      Vec3 motion = this.m_20184_();
      Vec3 forward = this.m_20156_();
      double strength = new Random().nextDouble(0.2, 0.4);
      this.m_20334_(motion.f_82479_ + forward.f_82479_ * strength, motion.f_82480_, motion.f_82481_ + forward.f_82481_ * strength);
      this.f_19812_ = true;
   }

   public void shortPillarJump() {
      if (this.m_20096_()) {
         Vec3 v = this.m_20184_();
         double keepH = 0.02;
         this.m_20334_(v.f_82479_ * keepH, 0.42, v.f_82481_ * keepH);
         this.f_19812_ = true;
      }
   }

   public boolean m_6469_(@NotNull DamageSource damageSource, float f) {
      if (this.hasEnderPearlCounter()) {
         this.tryTriggerEnderPearlCounter(damageSource);
      }

      boolean result = super.m_6469_(damageSource, f);
      if (result) {
         this.sayHurtSound(this, damageSource);
      }

      return result;
   }

   public boolean m_7327_(@NotNull Entity target) {
      boolean result = super.m_7327_(target);
      if (result) {
         this.sayAttackSound(this, target);
      }

      return result;
   }

   protected boolean hasEnderPearlCounter() {
      return false;
   }

   protected void beforeEnderPearlCounter(@NotNull DamageSource damageSource) {
   }

   protected void afterEnderPearlCounter(@NotNull DamageSource damageSource) {
   }

   protected void doEnderPearlCounterPattern(@NotNull DamageSource damageSource) {
      this.throwEnderPearlNow(180.0F);
   }

   protected void playEnderPearlCounterAnimation() {
      if (this.getLivingEntityPatch() != null) {
         this.getLivingEntityPatch().playAnimationSynchronized(AnimsEpicFightIronSpell.CASTING_ONE_HAND_BUFF, 0.0F);
      }
   }

   protected void throwEnderPearlNow(float angle) {
      CombatBehaviour.throwEnderPearl(this, angle);
   }

   protected void throwEnderPearlLater(int delayTicks, final float angle) {
      final AVNpc entity = this;
      new DelayedTask(delayTicks) {
         public void run() {
            if (entity.m_6084_()) {
               entity.playEnderPearlCounterAnimation();
               entity.throwEnderPearlNow(angle);
            }
         }
      };
   }

   protected void throwEnderPearlLater(int delayTicks, double chance, float angle) {
      if (this.f_19796_.m_188500_() <= chance) {
         this.throwEnderPearlLater(delayTicks, angle);
      }
   }

   protected void doChrisStyleEnderPearlCounter() {
      this.throwEnderPearlNow(180.0F);
      this.throwEnderPearlLater(20, 0.2, 90.0F);
   }

   protected void doSteveStyleEnderPearlCounter() {
      this.throwEnderPearlNow(new Random().nextFloat(90.0F, 180.0F));
      this.throwEnderPearlLater(20, 0.5, 180.0F);
      this.throwEnderPearlLater(20, 0.3, 90.0F);
   }

   protected void doVillagerKnightStyleEnderPearlCounter() {
      this.throwEnderPearlNow(new Random().nextFloat(90.0F, 180.0F));
      this.throwEnderPearlLater(40, 0.5, 0.0F);
      this.throwEnderPearlLater(20, 0.2, 180.0F);
      this.throwEnderPearlLater(20, 0.1, 90.0F);
   }

   protected AssetAccessor<? extends StaticAnimation> getCurrentAnimationOrEmpty() {
      LivingEntityPatch<?> patch = this.getLivingEntityPatch();
      if (patch == null) {
         return Animations.EMPTY_ANIMATION;
      } else {
         AnimationPlayer player = patch.getAnimator().getPlayerFor(null);
         return (AssetAccessor<? extends StaticAnimation>)(player != null ? player.getRealAnimation() : Animations.EMPTY_ANIMATION);
      }
   }

   protected void tryTriggerEnderPearlCounter(@NotNull DamageSource damageSource) {
      LivingEntityPatch<?> patch = this.getLivingEntityPatch();
      AssetAccessor<? extends StaticAnimation> dynamicAnimation = this.getCurrentAnimationOrEmpty();
      if (damageSource.m_7639_() != null) {
         if (this.getEnderPearlCooldown() == 0) {
            if (!EpicfightUtil.isLongHitAnimation(dynamicAnimation, patch)) {
               if (this.m_9236_() instanceof ServerLevel) {
                  if (dynamicAnimation == Animations.EMPTY_ANIMATION) {
                     if (patch instanceof MobPatch<?> mobPatch) {
                        if (CombatCommon.canPerformNormalAttackLogic(mobPatch)) {
                           this.beforeEnderPearlCounter(damageSource);
                           this.playEnderPearlCounterAnimation();
                           this.doEnderPearlCounterPattern(damageSource);
                           this.afterEnderPearlCounter(damageSource);
                           this.setEnderPearlCooldown();
                        }
                     }
                  }
               }
            }
         }
      }
   }

   protected ItemStack getEnderPearlCounterRestoreOffhandItem() {
      return this.getOffWeaponItem().m_41777_();
   }

   protected void restoreOffhandLater(int delayTicks) {
      final ItemStack restore = this.getEnderPearlCounterRestoreOffhandItem().m_41777_();
      new DelayedTask(delayTicks) {
         public void run() {
            if (AVNpc.this.m_6084_()) {
               AVNpc.this.m_21008_(InteractionHand.OFF_HAND, restore.m_41777_());
            }
         }
      };
   }

   protected void swapOffhandDuringEnderPearlCounter(ItemStack temporaryOffhand, int restoreDelayTicks) {
      this.m_21008_(InteractionHand.OFF_HAND, temporaryOffhand.m_41777_());
      this.restoreOffhandLater(restoreDelayTicks);
   }

   protected boolean afterBurstProtection(@NotNull ServerLevel serverLevel, @NotNull DamageSource source, float finalDamage) {
      return false;
   }

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

   public void m_8119_() {
      super.m_8119_();
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         this.tickVoiceCooldown();
         if (this.f_19797_ == 1 && !this.initialSpawn) {
            this.implementFirstTick((ServerLevel)this.m_9236_());
            this.initialSpawn = true;
         }

         CombatCommon.tryPerformAvNpcWaterBucketSelfExtinguish(this);
         if (ModList.get().isLoaded("efkick") && this.stunEscapeCooldown == 0 && this.m_9236_() instanceof ServerLevel && this.getLivingEntityPatch() != null) {
            final AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(
                  this.getLivingEntityPatch().getAnimator().getPlayerFor(null)
               )
               .getRealAnimation();
            if (EpicfightUtil.isLongHitAnimationNotExecutedAnimation(dynamicAnimation, this.getLivingEntityPatch())
               && this.m_6084_()
               && (double)this.m_217043_().m_188501_() < CombatBehaviour.calculateGuardBreakWakeUpChance(this)) {
               if (this instanceof AngrySteveEntity) {
                  this.stunEscapeCooldown = 60;
               } else {
                  this.stunEscapeCooldown = 100;
               }

               final AVNpc entity = this;
               new DelayedTask(new Random().nextInt(5, 10)) {
                  public void run() {
                     if (AVNpc.this.getLivingEntityPatch() != null
                        && EpicfightUtil.isLongHitAnimationNotExecutedAnimation(dynamicAnimation, AVNpc.this.getLivingEntityPatch())
                        && entity.m_6084_()) {
                        CombatBehaviour.postGuardBreakWakeUp(entity, AVNpc.this.getLivingEntityPatch(), serverLevel);
                     } else {
                        entity.stunEscapeCooldown = 1;
                     }
                  }
               };
            }
         }

         if (this.gapCooldown > 0) {
            this.gapCooldown--;
         }

         if (this.enderPearlCooldown > 0) {
            this.enderPearlCooldown--;
         }

         if (this.swapToBowCooldown > 0) {
            this.swapToBowCooldown--;
         }

         if (this.placeBlockParryCooldown > 0) {
            this.placeBlockParryCooldown--;
         }

         if (this.stunEscapeCooldown > 0) {
            this.stunEscapeCooldown--;
         }

         if (this.playingIdleCooldown > 0) {
            this.playingIdleCooldown--;
         }

         if (this.efnGuardHitCooldown > 0) {
            this.efnGuardHitCooldown--;
         }

         if (this.efnGuardHitCooldown == 0 && this.efnGuardHitState != 0) {
            this.efnGuardHitState = 0;
         }

         if ((this.f_19797_ + this.m_19879_()) % 20 == 0 && !this.isInventoryFull()) {
            this.pickupNearbyItems();
         }
      }
   }
}
