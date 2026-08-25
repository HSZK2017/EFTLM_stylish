package com.pla.annoyingvillagers.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pla.annoyingvillagers.clazz.IdleAnimation;
import com.pla.annoyingvillagers.clazz.PlayerNpcTarget;
import com.pla.annoyingvillagers.combatbehaviour.CombatCommon;
import com.pla.annoyingvillagers.entity.goal.BowLineOfSightGoal;
import com.pla.annoyingvillagers.entity.goal.BurnNearbyItemGoal;
import com.pla.annoyingvillagers.entity.goal.LockedRandomStrollGoal;
import com.pla.annoyingvillagers.entity.goal.PlayIdleAnimationGoal;
import com.pla.annoyingvillagers.entity.goal.RecoverWeaponInCombatGoal;
import com.pla.annoyingvillagers.entity.goal.RetargetCloserThreatGoal;
import com.pla.annoyingvillagers.entity.goal.WaterEnderPearlEscapeGoal;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFightIronSpell;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.task.DelayedTask;
import com.pla.annoyingvillagers.util.BowFunction;
import com.pla.annoyingvillagers.util.ChatUtil;
import com.pla.annoyingvillagers.util.CombatBehaviour;
import com.pla.annoyingvillagers.util.CommonGoals;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import com.pla.annoyingvillagers.util.EquipmentDataLoader;
import com.pla.annoyingvillagers.util.TeamUtil;
import java.util.Objects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import se.gory_moon.player_mobs.entity.PlayerMobEntity;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class PlayerNpcEntity extends PlayerMobEntity implements RangedAttackMob {
   private final SimpleContainer inventory = new SimpleContainer(27);
   private int gapCooldown = 0;
   private int enderPearlCooldown = 0;
   private int swapToBowCooldown = 0;
   private PlayerNpcTarget target;
   private ItemStack mainWeaponItem = ItemStack.f_41583_;
   private ItemStack offWeaponItem = ItemStack.f_41583_;
   private boolean healing = false;
   private boolean useBow = true;
   private Entity blockDamage = null;
   private double placeBlockToParryChance;
   private int stunEscapeCooldown = 0;
   @Nullable
   private IdleAnimation idleAnimationChoice;
   @Nullable
   private AssetAccessor<? extends StaticAnimation> idleAnimationAsset;
   private boolean idleMessageBroadcast = false;
   private boolean playingIdle;
   private int playingIdleCooldown = new Random().nextInt(600, 1200);
   private boolean isStrolling;
   private boolean mainWeaponDisarmed = false;

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

   public int getPlayingIdleCooldown() {
      return this.playingIdleCooldown;
   }

   public void setPlayingIdleCooldown(int playingIdleCooldown) {
      this.playingIdleCooldown = playingIdleCooldown;
   }

   public void clearIdleAnimationState() {
      this.idleAnimationChoice = null;
      this.idleAnimationAsset = null;
      this.idleMessageBroadcast = false;
   }

   @Nullable
   public LivingEntityPatch<?> getLivingEntityPatch() {
      return (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(this, LivingEntityPatch.class);
   }

   public int getStunEscapeCooldown() {
      return this.stunEscapeCooldown;
   }

   public void setStunEscapeCooldown(int stunEscapeCooldown) {
      this.stunEscapeCooldown = stunEscapeCooldown;
   }

   public double getPlaceBlockToParryChance() {
      return this.placeBlockToParryChance;
   }

   public Entity getBlockDamage() {
      return this.blockDamage;
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

   public int getGapCooldown() {
      return this.gapCooldown;
   }

   public int getEnderPearlCooldown() {
      return this.enderPearlCooldown;
   }

   public int getSwapToBowCooldown() {
      return this.swapToBowCooldown;
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

   public void setSwapToBowCooldown() {
      this.swapToBowCooldown = this.f_19796_.m_216339_(100, 300);
   }

   public boolean isMainWeaponDisarmed() {
      return this.mainWeaponDisarmed;
   }

   public void setMainWeaponDisarmed(boolean mainWeaponDisarmed) {
      this.mainWeaponDisarmed = mainWeaponDisarmed;
   }

   public SimpleContainer getInventory() {
      return this.inventory;
   }

   public PlayerNpcEntity(SpawnEntity spawnentity, Level level) {
      this((EntityType<? extends PlayerNpcEntity>)AnnoyingVillagersModEntities.PLAYER_NPC.get(), level);
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

   public void setUseBow(boolean useBow) {
      this.useBow = useBow;
   }

   public boolean isUseBow() {
      return this.useBow;
   }

   public PlayerNpcEntity(EntityType<? extends PlayerNpcEntity> entitytype, Level level) {
      super(entitytype, level);
      this.m_274367_(2.6F);
      this.f_21364_ = 50;
      this.m_21557_(false);
      this.m_20340_(true);
      this.m_21530_();
      this.placeBlockToParryChance = new Random().nextDouble(0.2, 0.4);
      this.m_21553_(true);
   }

   public void m_7380_(@NotNull CompoundTag tag) {
      super.m_7380_(tag);
      tag.m_128365_("Inventory", this.inventory.m_7927_());
      tag.m_128405_("GapCooldown", this.gapCooldown);
      tag.m_128405_("EnderPearlCooldown", this.enderPearlCooldown);
      tag.m_128405_("SwapToBowCooldown", this.swapToBowCooldown);
      tag.m_128379_("UseBow", this.useBow);
      tag.m_128347_("BlockProjectileChance", this.placeBlockToParryChance);
      if (this.target != null) {
         tag.m_128359_("PlayerNpcTarget", this.target.name());
      }

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

      tag.m_128379_("MainWeaponDisarmed", this.mainWeaponDisarmed);
   }

   public void m_7378_(@NotNull CompoundTag tag) {
      super.m_7378_(tag);
      if (tag.m_128425_("Inventory", 10)) {
         this.inventory.m_7797_(tag.m_128437_("Inventory", 10));
      }

      this.gapCooldown = tag.m_128451_("GapCooldown");
      this.enderPearlCooldown = tag.m_128451_("EnderPearlCooldown");
      this.swapToBowCooldown = tag.m_128451_("SwapToBowCooldown");
      this.useBow = tag.m_128471_("UseBow");
      this.placeBlockToParryChance = tag.m_128459_("BlockProjectileChance");
      if (tag.m_128425_("PlayerNpcTarget", 8)) {
         String name = tag.m_128461_("PlayerNpcTarget");

         try {
            this.target = PlayerNpcTarget.valueOf(name);
         } catch (IllegalArgumentException var4) {
            this.target = PlayerNpcTarget.MONSTER_HUNTER;
         }
      }

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
   }

   private boolean shouldCustomInventoryPickup(ItemStack stack) {
      if (stack.m_41619_()) {
         return false;
      } else {
         EquipmentSlot slot = LivingEntity.m_147233_(stack);
         return slot.m_20743_() == Type.ARMOR
            ? !this.m_7243_(stack)
            : !this.isRecoverableWeapon(stack) || this.m_5448_() == null || !this.m_21205_().m_41619_();
      }
   }

   private boolean isRecoverableWeapon(ItemStack stack) {
      if (stack.m_41619_()) {
         return false;
      } else {
         Item item = stack.m_41720_();
         return item instanceof SwordItem || item instanceof DiggerItem || item instanceof TridentItem;
      }
   }

   public boolean m_7243_(@NotNull ItemStack stack) {
      if (stack.m_41619_()) {
         return false;
      } else {
         EquipmentSlot slot = LivingEntity.m_147233_(stack);
         return slot.m_20743_() != Type.ARMOR ? false : super.m_7243_(stack);
      }
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   private void hostileHunterPlayerMob() {
      this.f_21345_.m_25352_(2, new MeleeAttackGoal(this, 1.2, false));
      this.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(this, Player.class, true));
      this.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(this, PlayerMobEntity.class, true));
      CommonGoals.attackAllMonstersGoals(this);
      CommonGoals.attackAllNpcGoals(this);
   }

   private void villagerHunterPlayerMob() {
      CommonGoals.runAwayFromHerobrineGoals(this, 20.0F);
      if (!(this.m_5448_() instanceof PlayerNpcEntity)) {
         this.f_21345_.m_25352_(2, new AvoidEntityGoal(this, PlayerNpcEntity.class, 12.0F, 1.2, 1.4));
      }

      if (!(this.m_5448_() instanceof Player)) {
         this.f_21345_.m_25352_(2, new AvoidEntityGoal(this, Player.class, 12.0F, 1.2, 1.4));
      }

      this.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(this, Villager.class, true));
      this.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(this, JevEntity.class, true));
      this.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
      CommonGoals.attackAllVillagerArmyGoal(this);
      this.f_21345_.m_25352_(3, new MeleeAttackGoal(this, 1.2, false));
   }

   private void monsterHunterPlayerMob() {
      CommonGoals.attackAllMonstersGoals(this);
      CommonGoals.runAwayFromVillagerArmyGoals(this);
   }

   private void playerHunterPlayerMob() {
      CommonGoals.runAwayFromHerobrineGoals(this, 20.0F);
      CommonGoals.runAwayFromVillagerArmyGoals(this);
      this.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(this, Player.class, true));
      this.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(this, PlayerNpcEntity.class, true));
      CommonGoals.attackAllNpcGoals(this);
   }

   private void animalHunterPlayerMob() {
      CommonGoals.runAwayFromHerobrineGoals(this, 20.0F);
      CommonGoals.runAwayFromVillagerArmyGoals(this);
      this.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(this, Animal.class, true));
      if (!(this.m_5448_() instanceof PlayerNpcEntity)) {
         this.f_21345_.m_25352_(2, new AvoidEntityGoal(this, PlayerNpcEntity.class, 12.0F, 1.2, 1.4));
      }

      if (!(this.m_5448_() instanceof Player)) {
         this.f_21345_.m_25352_(2, new AvoidEntityGoal(this, Player.class, 12.0F, 1.2, 1.8));
      }
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(-3, new WaterEnderPearlEscapeGoal(this));
      this.f_21345_.m_25352_(-2, new RecoverWeaponInCombatGoal(this, 1.2, 10.0));
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21346_.m_25352_(0, new RetargetCloserThreatGoal(this));
      this.f_21346_.m_25352_(1, new HurtByTargetGoal(this, new Class[0]));
      this.f_21345_.m_25352_(5, new BurnNearbyItemGoal(this, 1.0, 10.0));
      this.f_21345_.m_25352_(6, new PlayIdleAnimationGoal(this, new Random().nextInt(3000, 6000)));
      this.f_21345_.m_25352_(7, new LockedRandomStrollGoal(this, 1.0));
      this.f_21345_.m_25352_(5, new OpenDoorGoal(this, true));
      this.f_21345_.m_25352_(4, new BowLineOfSightGoal(this, 1.15, 7.0, 14.0));
      ((GroundPathNavigation)this.m_21573_()).m_26477_(true);
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
      if (this.getLivingEntityPatch() == null) {
         return super.m_6469_(damageSource, f);
      } else {
         AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(this.getLivingEntityPatch().getAnimator().getPlayerFor(null))
            .getRealAnimation();
         if (damageSource.m_7639_() != null
            && this.getEnderPearlCooldown() == 0
            && !EpicfightUtil.isLongHitAnimation(dynamicAnimation, this.getLivingEntityPatch())
            && this.m_9236_() instanceof ServerLevel
            && dynamicAnimation == Animations.EMPTY_ANIMATION
            && CombatCommon.canPerformNormalAttackLogic((MobPatch<?>)this.getLivingEntityPatch())) {
            this.getLivingEntityPatch().playAnimationSynchronized(AnimsEpicFightIronSpell.CASTING_ONE_HAND_BUFF, 0.0F);
            CombatBehaviour.throwEnderPearl(this, 180.0F);
            if (Math.random() <= 0.5) {
               new DelayedTask(20) {
                  @Override
                  public void run() {
                     if (PlayerNpcEntity.this.m_6084_()) {
                        PlayerNpcEntity.this.getLivingEntityPatch().playAnimationSynchronized(AnimsEpicFightIronSpell.CASTING_ONE_HAND_BUFF, 0.0F);
                        CombatBehaviour.throwEnderPearl(PlayerNpcEntity.this, 90.0F);
                     }
                  }
               };
            }

            this.setEnderPearlCooldown();
         }

         return super.m_6469_(damageSource, f);
      }
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

   public void m_6667_(@NotNull DamageSource damageSource) {
      super.m_6667_(damageSource);
      if (this.m_9236_() instanceof ServerLevel serverLevel && this.getPersistentData().m_128471_("die_by_possess")) {
         this.m_142687_(RemovalReason.KILLED);
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

         for (ItemEntity itemEntity : this.m_9236_()
            .m_6443_(ItemEntity.class, box, e -> !e.m_213877_() && !e.m_32063_() && this.shouldCustomInventoryPickup(e.m_32055_()))) {
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

   public void m_8119_() {
      super.m_8119_();
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         if (ModList.get().isLoaded("efkick") && this.stunEscapeCooldown == 0 && this.m_9236_() instanceof ServerLevel && this.getLivingEntityPatch() != null) {
            final AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(
                  this.getLivingEntityPatch().getAnimator().getPlayerFor(null)
               )
               .getRealAnimation();
            if (EpicfightUtil.isLongHitAnimationNotExecutedAnimation(dynamicAnimation, this.getLivingEntityPatch())
               && this.m_6084_()
               && (double)this.m_217043_().m_188501_() < CombatBehaviour.calculateGuardBreakWakeUpChance(this)) {
               this.stunEscapeCooldown = 100;
               this.playingIdleCooldown += 100;
               final PlayerNpcEntity entity = this;
               new DelayedTask(new Random().nextInt(5, 10)) {
                  @Override
                  public void run() {
                     if (PlayerNpcEntity.this.getLivingEntityPatch() != null
                        && EpicfightUtil.isLongHitAnimationNotExecutedAnimation(dynamicAnimation, PlayerNpcEntity.this.getLivingEntityPatch())
                        && entity.m_6084_()) {
                        CombatBehaviour.postGuardBreakWakeUp(entity, PlayerNpcEntity.this.getLivingEntityPatch(), serverLevel);
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

         if (this.stunEscapeCooldown > 0) {
            this.stunEscapeCooldown--;
         }

         if (this.playingIdleCooldown > 0) {
            this.playingIdleCooldown--;
         }

         if ((this.f_19797_ + this.m_19879_()) % 20 == 0) {
            if (!this.isInventoryFull()) {
               this.pickupNearbyItems();
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
      SpawnGroupData returnSpawnGroupData = super.m_6518_(serverLevelAccessor, difficultyInstance, mobSpawnType, spawngroupdata, compoundtag);
      ServerLevel serverLevel = serverLevelAccessor.m_6018_();
      if ((mobSpawnType == MobSpawnType.CHUNK_GENERATION || mobSpawnType == MobSpawnType.NATURAL) && serverLevel.m_46461_() && Math.random() <= 0.8) {
         BlockPos blockPos = this.m_20097_();
         int surfaceY = serverLevel.m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, blockPos).m_123342_();
         BlockPos spawnPos = new BlockPos(blockPos.m_123341_(), surfaceY, blockPos.m_123343_());
         if (serverLevel.m_6425_(spawnPos).m_76178_()) {
            this.m_20035_(spawnPos, this.m_146908_(), this.m_146909_());
         }
      }

      this.target = PlayerNpcTarget.randomByWeight(this.m_217043_());
      if (this.target != null) {
         switch (this.target) {
            case HOSTILE_HUNTER:
               this.hostileHunterPlayerMob();
               break;
            case VILLAGER_HUNTER:
               this.villagerHunterPlayerMob();
               break;
            case MONSTER_HUNTER:
               this.monsterHunterPlayerMob();
               break;
            case PLAYER_HUNTER:
               this.playerHunterPlayerMob();
               break;
            case ANIMAL_HUNTER:
               this.animalHunterPlayerMob();
               break;
            default:
               CommonGoals.runAwayFromHerobrineGoals(this, 20.0F);
               CommonGoals.runAwayFromVillagerArmyGoals(this);
               if (!(this.m_5448_() instanceof Player)) {
                  this.f_21345_.m_25352_(1, new AvoidEntityGoal(this, Player.class, 20.0F, 1.2, 1.8));
               }
         }
      }

      for (String cmd : EquipmentDataLoader.getEquipCommands(0.85F, this)) {
         try {
            Objects.requireNonNull(this.m_20194_()).m_129892_().m_82094_().execute(cmd, this.m_20203_().m_81324_().m_81325_(4));
         } catch (CommandSyntaxException var12) {
         }
      }

      this.mainWeaponItem = this.m_21205_().m_41777_();
      this.offWeaponItem = this.getOffWeaponItem().m_41777_();
      ChatUtil.joinGame(this);
      if (Math.random() <= 0.05) {
         TeamUtil.addOrJoinTeam(this, "player");
      }

      if (new Random().nextBoolean()) {
         this.setUseBow(false);
      }

      return returnSpawnGroupData;
   }

   public void m_5993_(@NotNull Entity entity, int i, @NotNull DamageSource damageSource) {
      super.m_5993_(entity, i, damageSource);
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

   public static boolean canSpawn(
      EntityType<PlayerNpcEntity> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos position, RandomSource random
   ) {
      ServerLevel serverLevel = level.m_6018_();
      return serverLevel.m_46462_() ? false : Monster.m_219019_(entityType, level, spawnType, position, random);
   }

   public static Builder createAttributes() {
      Builder builder = Mob.m_21552_();
      builder = builder.m_22268_(Attributes.f_22279_, 0.35);
      builder = builder.m_22268_(Attributes.f_22276_, 30.0);
      builder = builder.m_22268_(Attributes.f_22284_, 0.0);
      builder = builder.m_22268_(Attributes.f_22281_, 0.0);
      return builder.m_22268_(Attributes.f_22277_, 48.0);
   }
}
