package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.clazz.VillagerArmyEntity;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.util.CommonGoals;
import com.pla.annoyingvillagers.util.RidingUtil;
import com.pla.annoyingvillagers.util.TeamUtil;
import com.pla.annoyingvillagers.util.VillagerUtil;
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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class GreenVillagerKnightEntity extends VillagerArmyEntity {
   public GreenVillagerKnightEntity(SpawnEntity spawnentity, Level level) {
      this((EntityType<GreenVillagerKnightEntity>)AnnoyingVillagersModEntities.GREEN_VILLAGER_KNIGHT.get(), level);
   }

   public GreenVillagerKnightEntity(EntityType<GreenVillagerKnightEntity> entitytype, Level level) {
      super(entitytype, level);
      this.m_274367_(2.0F);
      this.f_21364_ = 0;
      this.m_21557_(false);
      this.setPlaceBlockToParryChance(0.7);
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   @Override
   protected void m_8099_() {
      super.m_8099_();
      CommonGoals.registerGoalForVillagerKnightNpc(this);
   }

   @NotNull
   public MobType m_6336_() {
      return MobType.f_21640_;
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

   @Nullable
   @Override
   public SoundEvent getAttackVoiceSound() {
      return (SoundEvent)AnnoyingVillagersModSounds.VILLAGER_KNIGHTS_SAY.get();
   }

   @Override
   protected boolean hasEnderPearlCounter() {
      return true;
   }

   @Override
   protected void beforeEnderPearlCounter(@NotNull DamageSource damageSource) {
      this.restoreOffhandLater(150);
   }

   @Override
   protected void doEnderPearlCounterPattern(@NotNull DamageSource damageSource) {
      this.doVillagerKnightStyleEnderPearlCounter();
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
         ItemStack[] drops = new ItemStack[]{
            new ItemStack(Items.f_42410_),
            new ItemStack(Items.f_42410_),
            new ItemStack(Items.f_42406_),
            new ItemStack(Items.f_42616_),
            new ItemStack(Items.f_42616_),
            new ItemStack(Items.f_42584_),
            new ItemStack(Items.f_42584_),
            new ItemStack(Items.f_42584_),
            new ItemStack(Items.f_42584_),
            new ItemStack(Items.f_42437_),
            new ItemStack(Items.f_42437_),
            new ItemStack(Items.f_42388_),
            new ItemStack(Items.f_42412_),
            new ItemStack(Items.f_42412_),
            new ItemStack(Items.f_42412_),
            new ItemStack(Items.f_42412_),
            new ItemStack(Items.f_42412_),
            new ItemStack(Items.f_42412_),
            new ItemStack(Items.f_42412_),
            new ItemStack(Items.f_42412_),
            new ItemStack(Items.f_42412_),
            new ItemStack(Items.f_42412_),
            new ItemStack(Items.f_42412_),
            new ItemStack((ItemLike)AnnoyingVillagersModItems.ENCHANTED_ARROW.get()),
            new ItemStack((ItemLike)AnnoyingVillagersModItems.ENCHANTED_ARROW.get()),
            new ItemStack((ItemLike)AnnoyingVillagersModItems.ENCHANTED_ARROW.get()),
            new ItemStack((ItemLike)AnnoyingVillagersModItems.ENCHANTED_ARROW.get()),
            new ItemStack((ItemLike)AnnoyingVillagersModItems.ENCHANTED_ARROW.get()),
            new ItemStack((ItemLike)AnnoyingVillagersModItems.ENCHANTED_ARROW.get()),
            this.createDamagedDropStack((Item)AnnoyingVillagersModItems.ADVANCED_FISHING_ROD.get()),
            new ItemStack(Items.f_42417_),
            new ItemStack(Items.f_42417_),
            new ItemStack(Items.f_42417_),
            new ItemStack(Blocks.f_50705_),
            new ItemStack(Items.f_42616_),
            new ItemStack(Items.f_42616_),
            new ItemStack(Items.f_42616_),
            new ItemStack(Items.f_42616_)
         };

         for (ItemStack stack : drops) {
            dropStack.accept(stack);
         }
      }
   }

   public SpawnGroupData m_6518_(
      @NotNull ServerLevelAccessor serverLevelAccessor,
      @NotNull DifficultyInstance difficultyInstance,
      @NotNull MobSpawnType mobSpawnType,
      @Nullable SpawnGroupData spawnGroupData,
      @Nullable CompoundTag compoundTag
   ) {
      SpawnGroupData returnSpawnGroupData = super.m_6518_(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
      TeamUtil.addOrJoinTeam(this, "villagers");
      this.m_8061_(EquipmentSlot.MAINHAND, VillagerUtil.generateMainWeaponItem());
      this.m_8061_(EquipmentSlot.OFFHAND, new ItemStack(Items.f_42584_));
      this.m_8061_(EquipmentSlot.HEAD, new ItemStack((ItemLike)AnnoyingVillagersModItems.GREEN_VILLAGER_KNIGHT_HELMET_FIX.get()));
      this.m_8061_(EquipmentSlot.CHEST, new ItemStack((ItemLike)AnnoyingVillagersModItems.GREEN_VILLAGER_KNIGHT_CHESTPLATE.get()));
      this.m_8061_(EquipmentSlot.LEGS, new ItemStack((ItemLike)AnnoyingVillagersModItems.VILLAGER_KNIGHT_LEGGINGS.get()));
      this.m_8061_(EquipmentSlot.FEET, new ItemStack((ItemLike)AnnoyingVillagersModItems.VILLAGER_KNIGHT_BOOTS.get()));
      this.setMainWeaponItem(this.m_21205_().m_41777_());
      this.setOffWeaponItem(this.getOffWeaponItem().m_41777_());
      if (new Random().nextBoolean()) {
         this.setUseBow(false);
      }

      return returnSpawnGroupData;
   }

   @Override
   protected void implementFirstTick(ServerLevel serverLevel) {
      super.implementFirstTick(serverLevel);
      if (new Random().nextDouble() <= 0.3) {
         RidingUtil.rideRandomAnimal(serverLevel, this);
      }
   }

   public static boolean canSpawn(
      EntityType<GreenVillagerKnightEntity> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos position, RandomSource random
   ) {
      return PathfinderMob.m_217057_(entityType, level, spawnType, position, random);
   }

   public static Builder createAttributes() {
      return Mob.m_21552_()
         .m_22268_(Attributes.f_22276_, 30.0)
         .m_22268_(Attributes.f_22279_, 0.35)
         .m_22268_(Attributes.f_22281_, 0.0)
         .m_22268_(Attributes.f_22277_, 32.0)
         .m_22268_(Attributes.f_22284_, 30.0);
   }
}
