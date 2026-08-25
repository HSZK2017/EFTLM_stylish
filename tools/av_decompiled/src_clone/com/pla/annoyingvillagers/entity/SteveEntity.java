package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.clazz.AVNpc;
import com.pla.annoyingvillagers.clazz.BurstProtectEntity;
import com.pla.annoyingvillagers.config.AnnoyingVillagersConfig;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.spawnhandler.SteveData;
import com.pla.annoyingvillagers.util.CommonGoals;
import com.pla.annoyingvillagers.util.EquipmentDataLoader;
import com.pla.annoyingvillagers.util.InventoryUtils;
import com.pla.annoyingvillagers.util.TeamUtil;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
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
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class SteveEntity extends AVNpc implements BurstProtectEntity {
   private int state = 0;
   private int swapWeaponCooldown;
   private boolean sayLegendary = false;
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

   public int getState() {
      return this.state;
   }

   public void setState(int state) {
      this.state = state;
   }

   public SteveEntity(SpawnEntity spawnEntity, Level level) {
      this((EntityType<SteveEntity>)AnnoyingVillagersModEntities.STEVE.get(), level);
   }

   public SteveEntity(EntityType<SteveEntity> entitytype, Level level) {
      super(entitytype, level);
      this.m_274367_(3.0F);
      this.f_21364_ = 8;
      this.m_21557_(false);
      this.m_6593_(this.m_5446_());
      this.m_20340_(true);
      this.m_21530_();
      this.setPlaceBlockToParryChance(0.8);
      this.setMainWeaponItem(new ItemStack(Items.f_42388_));
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   @Override
   protected void m_8099_() {
      super.m_8099_();
      CommonGoals.registerGoalForNeutralNpc(this);
   }

   @Nullable
   @Override
   public SoundEvent getAttackVoiceSound() {
      return (SoundEvent)AnnoyingVillagersModSounds.STEVE_SAY.get();
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

   public int getSwapWeaponCooldown() {
      return this.swapWeaponCooldown;
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
   protected boolean afterBurstProtection(@NotNull ServerLevel serverLevel, @NotNull DamageSource source, float finalDamage) {
      if (this.state == 0 && this.m_21223_() - finalDamage <= 1.0F && !this.m_21206_().m_150930_(Items.f_42747_)) {
         this.m_21153_(1.0F);
         return true;
      } else {
         return false;
      }
   }

   public void m_6667_(@NotNull DamageSource pDamageSource) {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         if ((double)new Random().nextFloat() <= (Double)AnnoyingVillagersConfig.ANGRY_STEVE_CHANCE.get()) {
            LivingEntity target;
            label34: {
               target = null;
               if (pDamageSource.m_7639_() instanceof LivingEntity living && living.m_6084_()) {
                  target = living;
                  break label34;
               }

               if (this.m_5448_() != null && this.m_5448_().m_6084_()) {
                  target = this.m_5448_();
               } else if (this.m_21188_() != null && this.m_21188_().m_6084_()) {
                  target = this.m_21188_();
               }
            }

            AngrySteveEntity angrySteveEntity = new AngrySteveEntity((EntityType<AngrySteveEntity>)AnnoyingVillagersModEntities.ANGRY_STEVE.get(), serverLevel);
            angrySteveEntity.m_20035_(this.m_20183_(), this.m_146908_(), this.m_146909_());
            InventoryUtils.transferInventory(this.getInventory(), angrySteveEntity.getInventory());
            this.m_146870_();
            SteveData steveData = SteveData.get(serverLevel);
            steveData.forceClaim(serverLevel, angrySteveEntity.m_20148_());
            angrySteveEntity.m_6518_(
               serverLevel, serverLevel.m_6436_(angrySteveEntity.m_20183_()), MobSpawnType.MOB_SUMMONED, (SpawnGroupData)null, (CompoundTag)null
            );
            serverLevel.m_7967_(angrySteveEntity);
            if (target != null) {
               angrySteveEntity.m_6710_(target);
               angrySteveEntity.m_6703_(target);
            }
         } else {
            this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.STEVE_SAY_ON_DEATH.get(), 0.5F, 1.0F);
         }
      }

      super.m_6667_(pDamageSource);
   }

   @Override
   protected void m_7472_(@NotNull DamageSource source, int looting, boolean recentlyHit) {
      super.m_7472_(source, looting, recentlyHit);
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         double var27 = this.m_20185_();
         double y = this.m_20186_() + 1.0;
         double z = this.m_20189_();
         Consumer<ItemStack> dropStack = stackx -> {
            ItemEntity drop = new ItemEntity(serverLevel, var27, y, z, stackx);
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

         double rareWeaponRoll = new Random().nextDouble(0.0, 1.0);
         if (rareWeaponRoll < 0.3) {
            ItemStack diamondGreatsword = new ItemStack((ItemLike)AnnoyingVillagersModItems.DIAMOND_GREATSWORD.get());
            diamondGreatsword.m_41663_(Enchantments.f_44977_, 5);
            diamondGreatsword.m_41663_(Enchantments.f_44978_, 5);
            diamondGreatsword.m_41663_(Enchantments.f_44983_, 5);
            damagedStacks.add(diamondGreatsword);
         } else if (rareWeaponRoll < 0.6) {
            ItemStack samanthaTheKillerAxe = new ItemStack((ItemLike)AnnoyingVillagersModItems.SAMANTHA_THE_KILLER_AXE.get());
            samanthaTheKillerAxe.m_41663_(Enchantments.f_44977_, 5);
            samanthaTheKillerAxe.m_41663_(Enchantments.f_44978_, 5);
            samanthaTheKillerAxe.m_41663_(Enchantments.f_44983_, 5);
            damagedStacks.add(samanthaTheKillerAxe);
         } else {
            ItemStack woopieTheSword = new ItemStack((ItemLike)AnnoyingVillagersModItems.WOOPIE_THE_SWORD.get());
            woopieTheSword.m_41663_(Enchantments.f_44977_, 5);
            woopieTheSword.m_41663_(Enchantments.f_44978_, 5);
            woopieTheSword.m_41663_(Enchantments.f_44983_, 5);
            damagedStacks.add(woopieTheSword);
         }

         damagedStacks.add(new ItemStack((ItemLike)AnnoyingVillagersModItems.JESSICA_THE_DARK_SHIELD.get()));
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
   public void m_7380_(@NotNull CompoundTag tag) {
      super.m_7380_(tag);
      tag.m_128405_("State", this.state);
      tag.m_128405_("SwapWeaponCooldown", this.swapWeaponCooldown);
      tag.m_128379_("SayLegendary", this.sayLegendary);
   }

   @Override
   public void m_7378_(@NotNull CompoundTag tag) {
      super.m_7378_(tag);
      this.state = tag.m_128451_("State");
      this.swapWeaponCooldown = tag.m_128451_("SwapWeaponCooldown");
      this.sayLegendary = tag.m_128471_("SayLegendary");
   }

   public void rollItem() {
      boolean setWeapon = false;
      if (this.state == 1) {
         double chance = new Random().nextDouble(0.0, 1.0);
         if (this.m_21223_() > this.m_21233_() / 2.0F) {
            if (chance < 0.2) {
               ItemStack woopieTheSword = new ItemStack((ItemLike)AnnoyingVillagersModItems.WOOPIE_THE_SWORD.get());
               woopieTheSword.m_41663_(Enchantments.f_44977_, 5);
               woopieTheSword.m_41663_(Enchantments.f_44978_, 5);
               woopieTheSword.m_41663_(Enchantments.f_44983_, 5);
               this.m_21008_(InteractionHand.MAIN_HAND, woopieTheSword);
               this.m_21008_(InteractionHand.OFF_HAND, new ItemStack((ItemLike)AnnoyingVillagersModItems.JESSICA_THE_DARK_SHIELD.get()));
               this.setOffWeaponItem(this.getOffWeaponItem().m_41777_());
               setWeapon = true;
            } else if (chance < 0.4) {
               ItemStack diamondGreatsword = new ItemStack((ItemLike)AnnoyingVillagersModItems.DIAMOND_GREATSWORD.get());
               diamondGreatsword.m_41663_(Enchantments.f_44977_, 5);
               diamondGreatsword.m_41663_(Enchantments.f_44980_, 5);
               this.m_21008_(InteractionHand.MAIN_HAND, diamondGreatsword);
               setWeapon = true;
            } else if (chance < 0.6) {
               ItemStack killerAxe = new ItemStack((ItemLike)AnnoyingVillagersModItems.SAMANTHA_THE_KILLER_AXE.get());
               killerAxe.m_41663_(Enchantments.f_44977_, 5);
               killerAxe.m_41663_(Enchantments.f_44981_, 2);
               this.m_21008_(InteractionHand.MAIN_HAND, killerAxe);
               this.m_21008_(InteractionHand.OFF_HAND, new ItemStack((ItemLike)AnnoyingVillagersModItems.JESSICA_THE_DARK_SHIELD.get()));
               this.setOffWeaponItem(this.getOffWeaponItem().m_41777_());
               setWeapon = true;
            } else {
               ItemStack diamondSword = new ItemStack(Items.f_42388_);
               diamondSword.m_41663_(Enchantments.f_44977_, 5);
               diamondSword.m_41663_(Enchantments.f_44978_, 5);
               this.m_21008_(InteractionHand.MAIN_HAND, diamondSword);
               this.m_21008_(InteractionHand.OFF_HAND, diamondSword);
               setWeapon = true;
            }
         } else if (chance <= 0.4) {
            ItemStack woopieTheSword = new ItemStack((ItemLike)AnnoyingVillagersModItems.WOOPIE_THE_SWORD.get());
            woopieTheSword.m_41663_(Enchantments.f_44977_, 5);
            woopieTheSword.m_41663_(Enchantments.f_44978_, 5);
            woopieTheSword.m_41663_(Enchantments.f_44983_, 5);
            this.m_21008_(InteractionHand.MAIN_HAND, woopieTheSword);
            this.m_21008_(InteractionHand.OFF_HAND, new ItemStack((ItemLike)AnnoyingVillagersModItems.JESSICA_THE_DARK_SHIELD.get()));
            this.setOffWeaponItem(this.getOffWeaponItem().m_41777_());
            setWeapon = true;
         } else if (this.m_9236_() instanceof ServerLevel serverLevel) {
            if (!this.sayLegendary) {
               this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.STEVE_SAY_I_NOT_BELIEVE.get(), 0.5F, 1.0F);
               this.sayLegendary = true;
            }

            ItemStack legendarySword = new ItemStack((ItemLike)AnnoyingVillagersModItems.LEGENDARY_SWORD.get());
            this.m_21008_(InteractionHand.MAIN_HAND, legendarySword);
            setWeapon = true;
         }
      } else if (this.state == 0 && this.m_21223_() <= 20.0F) {
         ItemStack diamondSword = new ItemStack(Items.f_42388_);
         diamondSword.m_41663_(Enchantments.f_44977_, 5);
         diamondSword.m_41663_(Enchantments.f_44978_, 5);
         this.m_21008_(InteractionHand.MAIN_HAND, diamondSword);
         this.m_21008_(InteractionHand.OFF_HAND, new ItemStack(Items.f_42747_));
         setWeapon = true;
      }

      if (!setWeapon) {
         double chance = new Random().nextDouble(0.0, 1.0);
         if (chance <= 0.2) {
            ItemStack diamondSword = new ItemStack(Items.f_42388_);
            diamondSword.m_41663_(Enchantments.f_44977_, 5);
            diamondSword.m_41663_(Enchantments.f_44978_, 5);
            this.m_21008_(InteractionHand.MAIN_HAND, diamondSword);
         } else if (chance <= 0.4) {
            ItemStack woodenDoor = new ItemStack((ItemLike)AnnoyingVillagersModItems.WOODEN_DOOR.get());
            woodenDoor.m_41663_(Enchantments.f_44977_, 5);
            woodenDoor.m_41663_(Enchantments.f_44980_, 3);
            this.m_21008_(InteractionHand.MAIN_HAND, woodenDoor);
         } else if (chance <= 0.6) {
            ItemStack craftingTable = new ItemStack((ItemLike)AnnoyingVillagersModItems.CRAFTING_TABLE.get());
            craftingTable.m_41663_(Enchantments.f_44978_, 5);
            craftingTable.m_41663_(Enchantments.f_44980_, 3);
            this.m_21008_(InteractionHand.MAIN_HAND, craftingTable);
         } else if (chance <= 0.8) {
            ItemStack ladder = new ItemStack((ItemLike)AnnoyingVillagersModItems.LADDER.get());
            ladder.m_41663_(Enchantments.f_44978_, 5);
            ladder.m_41663_(Enchantments.f_44983_, 3);
            this.m_21008_(InteractionHand.MAIN_HAND, ladder);
         } else {
            ItemStack trapDoor = new ItemStack((ItemLike)AnnoyingVillagersModItems.TRAPDOOR.get());
            trapDoor.m_41663_(Enchantments.f_44980_, 5);
            trapDoor.m_41663_(Enchantments.f_44983_, 3);
            this.m_21008_(InteractionHand.MAIN_HAND, trapDoor);
         }
      }

      this.setMainWeaponItem(this.m_21205_().m_41777_());
      this.swapWeaponCooldown = new Random().nextInt(100, 200);
   }

   @Override
   protected void implementFirstTick(ServerLevel serverLevel) {
      super.implementFirstTick(serverLevel);
      this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.STEVE_SAY_ON_SPAWN.get(), 0.5F, 1.0F);
   }

   @Override
   public void m_8119_() {
      super.m_8119_();
      if (this.m_9236_() instanceof ServerLevel) {
         if (this.m_5448_() != null && this.m_5448_().m_6084_() && this.m_21205_().m_41619_()) {
            this.rollItem();
            this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.STEVE_SAY_WHAT.get(), 0.5F, 1.0F);
         }

         if (this.getState() != 2 && this.m_5448_() == null && !this.m_21205_().m_41619_()) {
            this.m_21008_(InteractionHand.MAIN_HAND, ItemStack.f_41583_);
            this.m_21008_(InteractionHand.OFF_HAND, ItemStack.f_41583_);
         }

         if (this.state == 0 && this.m_21223_() <= 20.0F && !this.m_21120_(InteractionHand.OFF_HAND).m_41720_().equals(Items.f_42747_)) {
            this.m_21008_(InteractionHand.OFF_HAND, new ItemStack(Items.f_42747_));
         }

         if (this.m_5448_() != null
            && this.state == 0
            && this.m_21223_() > 20.0F
            && this.m_21120_(InteractionHand.OFF_HAND).m_41720_().equals(Items.f_42747_)
            && !(this.m_21120_(InteractionHand.OFF_HAND).m_41720_() instanceof ShieldItem)) {
            this.m_21008_(InteractionHand.OFF_HAND, ItemStack.f_41583_);
         }

         if (this.swapWeaponCooldown > 0) {
            this.swapWeaponCooldown--;
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
      if (mobSpawnType == MobSpawnType.NATURAL || mobSpawnType == MobSpawnType.CHUNK_GENERATION) {
         ServerLevel serverLevel = serverLevelAccessor.m_6018_();
         SteveData steveData = SteveData.get(serverLevel);
         if (!steveData.tryClaim(serverLevel, this.m_20148_())) {
            this.m_146870_();
            return null;
         }
      }

      TeamUtil.addOrJoinTeam(this, "steve");
      this.swapWeaponCooldown = new Random().nextInt(100, 200);
      return super.m_6518_(serverLevelAccessor, difficultyInstance, mobSpawnType, spawngroupdata, compoundtag);
   }

   public static boolean canSpawn(EntityType<SteveEntity> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos position, RandomSource random) {
      ServerLevel serverLevel = level.m_6018_();
      return SteveData.get(serverLevel).isOccupied(serverLevel) ? false : PathfinderMob.m_217057_(entityType, level, spawnType, position, random);
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
