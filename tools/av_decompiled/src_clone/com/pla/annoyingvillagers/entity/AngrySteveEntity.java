package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.clazz.AVNpc;
import com.pla.annoyingvillagers.clazz.BurstProtectEntity;
import com.pla.annoyingvillagers.combatbehaviour.CombatCommon;
import com.pla.annoyingvillagers.config.AnnoyingVillagersConfig;
import com.pla.annoyingvillagers.entity.goal.KeepPositionGoal;
import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.spawnhandler.SteveData;
import com.pla.annoyingvillagers.util.ArmorUtil;
import com.pla.annoyingvillagers.util.CommonGoals;
import com.pla.annoyingvillagers.util.EquipmentDataLoader;
import com.pla.annoyingvillagers.util.TeamUtil;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import net.minecraftforge.registries.ForgeRegistries;
import net.shelmarow.combat_evolution.effect.CEMobEffects;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.effect.EpicFightMobEffects;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class AngrySteveEntity extends AVNpc implements BurstProtectEntity {
   private boolean neverLeave = false;
   private int leaveTicks = 0;
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

   public void setLeaveTicks(int leaveTicks) {
      this.leaveTicks = leaveTicks;
   }

   public int getLeaveTicks() {
      return this.leaveTicks;
   }

   public void setNeverLeave(boolean neverLeave) {
      this.neverLeave = neverLeave;
   }

   public AngrySteveEntity(SpawnEntity spawnEntity, Level level) {
      this((EntityType<AngrySteveEntity>)AnnoyingVillagersModEntities.ANGRY_STEVE.get(), level);
   }

   public AngrySteveEntity(EntityType<AngrySteveEntity> entitytype, Level level) {
      super(entitytype, level);
      this.m_274367_(3.0F);
      this.f_21364_ = 8;
      this.m_21557_(false);
      this.m_6593_(this.m_5446_());
      this.m_20340_(true);
      this.m_21530_();
      this.setPlaceBlockToParryChance(1.0);
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   @Override
   protected void m_8099_() {
      super.m_8099_();
      this.f_21345_.m_25352_(1, new KeepPositionGoal(this));
      CommonGoals.registerGoalForCrazyNpc(this);
   }

   @Override
   public void m_7378_(@NotNull CompoundTag pCompound) {
      super.m_7378_(pCompound);
      this.leaveTicks = pCompound.m_128451_("LeaveTicks");
      this.neverLeave = pCompound.m_128471_("NeverLeave");
   }

   @Override
   public void m_7380_(@NotNull CompoundTag pCompound) {
      super.m_7380_(pCompound);
      pCompound.m_128405_("LeaveTicks", this.leaveTicks);
      pCompound.m_128379_("NeverLeave", this.neverLeave);
   }

   @Nullable
   @Override
   public SoundEvent getAttackVoiceSound() {
      return (SoundEvent)AnnoyingVillagersModSounds.ANGRY_STEVE_SAY.get();
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
   protected void doEnderPearlCounterPattern(@NotNull DamageSource damageSource) {
      this.doSteveStyleEnderPearlCounter();
   }

   @Override
   public float getBurstProtectCapRatio() {
      return 0.05F;
   }

   public boolean m_7301_(MobEffectInstance mobeffectinstance) {
      return (mobeffectinstance.m_19544_().m_19483_() == MobEffectCategory.BENEFICIAL || mobeffectinstance.m_19544_() == MobEffects.f_19619_)
         && super.m_7301_(mobeffectinstance);
   }

   @Override
   public boolean m_7327_(@NotNull Entity pEntity) {
      if (!this.m_9236_().m_5776_() && pEntity instanceof LivingEntity living) {
         ArmorUtil.damageArmor(living, new Random().nextInt(1, 5));
      }

      return super.m_7327_(pEntity);
   }

   public void m_6667_(@NotNull DamageSource damageSource) {
      super.m_6667_(damageSource);
      if (this.m_9236_() instanceof ServerLevel && (Boolean)AnnoyingVillagersConfig.TURN_ON_NPC_VOICE.get()) {
         this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.STEVE_SAY_ON_DEATH.get(), 0.5F, 1.0F);
      }
   }

   @Override
   protected void m_7472_(@NotNull DamageSource source, int looting, boolean recentlyHit) {
      super.m_7472_(source, looting, recentlyHit);
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         double var26 = this.m_20185_();
         double y = this.m_20186_() + 1.0;
         double z = this.m_20189_();
         Consumer<ItemStack> dropStack = stackx -> {
            ItemEntity drop = new ItemEntity(serverLevel, var26, y, z, stackx);
            drop.m_32010_(10);
            serverLevel.m_7967_(drop);
         };
         Consumer dropArrows = count -> {
            for (int i = 0; i < count; i++) {
               dropStack.accept(new ItemStack(Items.f_42412_));
            }
         };
         ArrayList damagedStacks = new ArrayList();
         ItemStack compressedDiamondHelmet = new ItemStack((ItemLike)AnnoyingVillagersModItems.COMPRESSED_DIAMOND_HELMET.get());
         compressedDiamondHelmet.m_41663_(Enchantments.f_44965_, 5);
         compressedDiamondHelmet.m_41663_(Enchantments.f_44969_, 5);
         compressedDiamondHelmet.m_41663_(Enchantments.f_44966_, 5);
         compressedDiamondHelmet.m_41663_(Enchantments.f_44968_, 5);
         damagedStacks.add(compressedDiamondHelmet);
         ItemStack compressedDiamondChestplate = new ItemStack((ItemLike)AnnoyingVillagersModItems.COMPRESSED_DIAMOND_CHESTPLATE.get());
         compressedDiamondChestplate.m_41663_(Enchantments.f_44965_, 5);
         compressedDiamondChestplate.m_41663_(Enchantments.f_44969_, 5);
         compressedDiamondChestplate.m_41663_(Enchantments.f_44966_, 5);
         compressedDiamondChestplate.m_41663_(Enchantments.f_44968_, 5);
         damagedStacks.add(compressedDiamondChestplate);
         ItemStack diamondSword = new ItemStack(Items.f_42388_);
         diamondSword.m_41663_(Enchantments.f_44977_, 5);
         diamondSword.m_41663_(Enchantments.f_44978_, 5);
         damagedStacks.add(diamondSword);
         if (new Random().nextBoolean()) {
            damagedStacks.add(diamondSword);
         }

         ItemStack bow = this.getBowItem();
         bow.m_41663_(Enchantments.f_44988_, 5);
         bow.m_41663_(Enchantments.f_44989_, 5);
         damagedStacks.add(bow);
         double chance = new Random().nextDouble(0.0, 1.0);
         if (chance < 0.2) {
            ItemStack woodenDoor = new ItemStack((ItemLike)AnnoyingVillagersModItems.WOODEN_DOOR.get());
            woodenDoor.m_41663_(Enchantments.f_44977_, 5);
            woodenDoor.m_41663_(Enchantments.f_44980_, 3);
            woodenDoor.m_41663_(Enchantments.f_44962_, 5);
            damagedStacks.add(woodenDoor);
         } else if (chance < 0.4) {
            ItemStack craftingTable = new ItemStack((ItemLike)AnnoyingVillagersModItems.CRAFTING_TABLE.get());
            craftingTable.m_41663_(Enchantments.f_44978_, 5);
            craftingTable.m_41663_(Enchantments.f_44980_, 3);
            craftingTable.m_41663_(Enchantments.f_44962_, 5);
            damagedStacks.add(craftingTable);
         } else if (chance < 0.6) {
            ItemStack ladder = new ItemStack((ItemLike)AnnoyingVillagersModItems.LADDER.get());
            ladder.m_41663_(Enchantments.f_44978_, 5);
            ladder.m_41663_(Enchantments.f_44983_, 3);
            ladder.m_41663_(Enchantments.f_44962_, 5);
            damagedStacks.add(ladder);
         } else if (chance < 0.8) {
            ItemStack trapDoor = new ItemStack((ItemLike)AnnoyingVillagersModItems.TRAPDOOR.get());
            trapDoor.m_41663_(Enchantments.f_44980_, 5);
            trapDoor.m_41663_(Enchantments.f_44983_, 3);
            trapDoor.m_41663_(Enchantments.f_44962_, 5);
            damagedStacks.add(trapDoor);
         } else {
            ItemStack mendingDiamondSword = new ItemStack(Items.f_42388_);
            mendingDiamondSword.m_41663_(Enchantments.f_44977_, 5);
            mendingDiamondSword.m_41663_(Enchantments.f_44978_, 5);
            mendingDiamondSword.m_41663_(Enchantments.f_44962_, 5);
            damagedStacks.add(mendingDiamondSword);
         }

         ItemStack legendarySword = new ItemStack((ItemLike)AnnoyingVillagersModItems.LEGENDARY_SWORD.get());
         legendarySword.m_41663_(Enchantments.f_44977_, 5);
         legendarySword.m_41663_(Enchantments.f_44978_, 5);
         legendarySword.m_41663_(Enchantments.f_44983_, 5);
         damagedStacks.add(legendarySword);
         damagedStacks.add(new ItemStack((ItemLike)AnnoyingVillagersModItems.TONY_THE_FISHING_ROD.get()));

         for (ItemStack stack : damagedStacks) {
            stack.m_41721_(EquipmentDataLoader.getRandomDamage(stack));
            dropStack.accept(stack);
         }

         ItemLike[] simpleDrops = new ItemLike[]{
            Items.f_42436_,
            Items.f_42436_,
            Items.f_42436_,
            Items.f_42436_,
            Items.f_42436_,
            Items.f_42436_,
            Items.f_42436_,
            Items.f_42436_,
            Items.f_42437_,
            Items.f_42437_,
            Items.f_42437_,
            Items.f_42584_,
            Items.f_42584_,
            Items.f_42584_,
            Items.f_42584_,
            Items.f_42584_,
            Items.f_42584_,
            Items.f_42584_,
            Items.f_42584_,
            Items.f_42584_,
            Items.f_42584_,
            Blocks.f_50493_,
            Blocks.f_50493_,
            Blocks.f_50493_,
            Blocks.f_50493_,
            Blocks.f_50493_,
            Blocks.f_50493_,
            Blocks.f_50493_,
            Blocks.f_50493_,
            Blocks.f_50077_,
            Blocks.f_50077_,
            Blocks.f_50090_,
            Blocks.f_50260_,
            Items.f_42503_,
            Items.f_42502_,
            Items.f_42447_,
            Items.f_42580_,
            Items.f_42580_,
            Items.f_42580_,
            Items.f_42523_,
            Items.f_42491_,
            Items.f_42619_,
            Items.f_42619_,
            Items.f_42674_,
            Items.f_42674_,
            Items.f_42398_,
            Items.f_42398_,
            Items.f_42398_,
            Items.f_42398_,
            Items.f_42398_,
            Items.f_42416_,
            Items.f_42416_,
            Items.f_42416_,
            Items.f_42416_,
            Items.f_42415_,
            Items.f_42415_,
            Items.f_42415_,
            Items.f_42415_,
            Items.f_42415_,
            Items.f_42415_,
            Items.f_42415_,
            Items.f_42415_,
            (ItemLike)AnnoyingVillagersModItems.COMPRESSED_DIAMOND.get(),
            (ItemLike)AnnoyingVillagersModItems.COMPRESSED_DIAMOND.get(),
            (ItemLike)AnnoyingVillagersModItems.COMPRESSED_DIAMOND.get(),
            (ItemLike)AnnoyingVillagersModItems.COMPRESSED_DIAMOND.get(),
            (ItemLike)AnnoyingVillagersModItems.COMPRESSED_DIAMOND.get(),
            (ItemLike)AnnoyingVillagersModItems.COMPRESSED_DIAMOND.get(),
            (ItemLike)AnnoyingVillagersModItems.COMPRESSED_DIAMOND.get(),
            (ItemLike)AnnoyingVillagersModItems.COMPRESSED_DIAMOND.get(),
            (ItemLike)AnnoyingVillagersModItems.COMPRESSED_DIAMOND.get()
         };

         for (ItemLike itemLike : simpleDrops) {
            dropStack.accept(new ItemStack(itemLike));
         }

         dropArrows.accept(new Random().nextInt(10, 30));
      }
   }

   @Override
   protected void implementFirstTick(ServerLevel serverLevel) {
      super.implementFirstTick(serverLevel);
      this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.ANGRY_STEVE_SAY_ON_SPAWN.get(), 0.5F, 1.0F);
      if (this.getLivingEntityPatch() != null) {
         this.getLivingEntityPatch().playAnimationSynchronized(AnimsPugilistSteve.GUARD_BREAK_ATTACK, 0.0F);
      }
   }

   @Override
   public void m_8119_() {
      super.m_8119_();
      if (this.m_9236_() instanceof ServerLevel) {
         this.tickBurstProtectionDecay(this);
         if (this.getLivingEntityPatch() != null && CombatCommon.canEscape((MobPatch<?>)this.getLivingEntityPatch())) {
            this.f_21345_.m_25355_(Flag.MOVE);
            this.m_21573_().m_26573_();
            LivingEntity target = this.m_5448_();
            if (target != null) {
               this.m_21563_().m_24960_(target, 30.0F, 30.0F);
            }
         } else {
            this.f_21345_.m_25374_(Flag.MOVE);
         }

         this.m_7292_(new MobEffectInstance((MobEffect)CEMobEffects.FULL_STUN_IMMUNITY.get(), 3, 3));
         this.m_7292_(new MobEffectInstance((MobEffect)EpicFightMobEffects.STUN_IMMUNITY.get(), 3, 3));
         if (!this.neverLeave) {
            this.leaveTicks--;
            int remaining = this.leaveTicks;
            if (remaining == 40) {
               this.m_21557_(true);
               Objects.requireNonNull(this.getLivingEntityPatch()).playAnimationSynchronized(AnimsPugilistSteve.TRIED, 0.0F);
            }

            if (remaining <= 0) {
               Objects.requireNonNull(this.m_9236_().m_7654_())
                  .m_6846_()
                  .m_240416_(Component.m_237113_("<Steve> " + Component.m_237115_("subtitles.angry_steve_retreat")), false);
               this.m_146870_();
            }
         }
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
      ItemStack legendarySword = new ItemStack((ItemLike)AnnoyingVillagersModItems.LEGENDARY_SWORD.get());
      legendarySword.m_41663_(Enchantments.f_44977_, 5);
      legendarySword.m_41663_(Enchantments.f_44978_, 5);
      legendarySword.m_41663_(Enchantments.f_44983_, 5);
      this.m_21008_(InteractionHand.MAIN_HAND, legendarySword);
      this.m_8061_(EquipmentSlot.MAINHAND, legendarySword);
      this.setMainWeaponItem(legendarySword);
      TeamUtil.addOrJoinTeam(this, "steve");
      int min = (Integer)AnnoyingVillagersConfig.ANGRY_STEVE_LEAVE_MIN_TIME.get();
      int max = (Integer)AnnoyingVillagersConfig.ANGRY_STEVE_LEAVE_MAX_TIME.get();
      int randomMin = Math.min(min, max);
      int randomMax = Math.max(min, max);
      this.leaveTicks = (randomMin + new Random().nextInt(randomMax - randomMin + 1)) * 60 * 20;
      return super.m_6518_(serverLevelAccessor, difficultyInstance, mobSpawnType, spawngroupdata, compoundtag);
   }

   public void m_142687_(@NotNull RemovalReason reason) {
      super.m_142687_(reason);
      if (!this.m_9236_().f_46443_
         && this.m_9236_() instanceof ServerLevel serverLevel
         && (reason == RemovalReason.KILLED || reason == RemovalReason.DISCARDED)) {
         SteveData.get(serverLevel).releaseIfMatches(serverLevel, this.m_20148_());
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
