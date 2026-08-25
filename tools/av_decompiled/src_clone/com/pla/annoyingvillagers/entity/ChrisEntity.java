package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.clazz.AVNpc;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.spawnhandler.ChrisData;
import com.pla.annoyingvillagers.util.CommonGoals;
import com.pla.annoyingvillagers.util.EquipmentDataLoader;
import com.pla.annoyingvillagers.util.TeamUtil;
import java.util.ArrayList;
import java.util.List;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
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
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class ChrisEntity extends AVNpc {
   private int state = 0;

   public ChrisEntity(SpawnEntity spawnEntity, Level level) {
      this((EntityType<ChrisEntity>)AnnoyingVillagersModEntities.CHRIS.get(), level);
   }

   public ChrisEntity(EntityType<ChrisEntity> entitytype, Level level) {
      super(entitytype, level);
      this.m_274367_(2.6F);
      this.f_21364_ = 50;
      this.m_21557_(false);
      this.m_6593_(this.m_5446_());
      this.m_20340_(true);
      this.m_21530_();
      this.setPlaceBlockToParryChance(0.6);
   }

   public int getState() {
      return this.state;
   }

   public void setState(int state) {
      this.state = state;
   }

   @Override
   public void m_7380_(@NotNull CompoundTag tag) {
      super.m_7380_(tag);
      tag.m_128405_("State", this.state);
   }

   @Override
   public void m_7378_(@NotNull CompoundTag tag) {
      super.m_7378_(tag);
      this.state = tag.m_128451_("State");
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
         ItemStack sword = new ItemStack(Items.f_42388_);
         sword.m_41663_(Enchantments.f_44980_, 5);
         sword.m_41663_(Enchantments.f_44977_, 5);
         sword.m_41663_(Enchantments.f_44986_, 5);
         damagedStacks.add(sword);
         ItemStack diamondHelmet = new ItemStack(Items.f_42472_);
         diamondHelmet.m_41663_(Enchantments.f_44965_, 5);
         diamondHelmet.m_41663_(Enchantments.f_44986_, 5);
         damagedStacks.add(diamondHelmet);
         ItemStack diamondChestplate = new ItemStack(Items.f_42473_);
         diamondChestplate.m_41663_(Enchantments.f_44965_, 5);
         diamondChestplate.m_41663_(Enchantments.f_44986_, 5);
         damagedStacks.add(diamondChestplate);
         ItemStack diamondBoots = new ItemStack(Items.f_42475_);
         diamondBoots.m_41663_(Enchantments.f_44965_, 5);
         diamondBoots.m_41663_(Enchantments.f_44974_, 2);
         diamondBoots.m_41663_(Enchantments.f_44986_, 5);
         damagedStacks.add(diamondBoots);
         ItemStack bow = this.getBowItem();
         bow.m_41663_(Enchantments.f_44988_, 2);
         bow.m_41663_(Enchantments.f_44989_, 2);
         damagedStacks.add(bow);
         ItemStack ironPickaxe = new ItemStack(Items.f_42385_);
         ironPickaxe.m_41663_(Enchantments.f_44986_, 3);
         damagedStacks.add(ironPickaxe);
         ItemStack ironAxe = new ItemStack(Items.f_42386_);
         ironAxe.m_41663_(Enchantments.f_44986_, 3);
         damagedStacks.add(ironAxe);

         for (ItemStack stack : damagedStacks) {
            stack.m_41721_(EquipmentDataLoader.getRandomDamage(stack));
            dropStack.accept(stack);
         }

         ItemStack[] simpleDrops = new ItemStack[]{
            new ItemStack(Items.f_42747_),
            new ItemStack(Items.f_42740_),
            new ItemStack(Items.f_151059_),
            new ItemStack(Items.f_42437_),
            new ItemStack(Items.f_42437_),
            new ItemStack(Items.f_42437_),
            new ItemStack(Items.f_42437_),
            new ItemStack(Items.f_42437_),
            new ItemStack(Items.f_42584_),
            new ItemStack(Items.f_42584_),
            new ItemStack(Items.f_42584_),
            new ItemStack(Items.f_42584_),
            new ItemStack(Items.f_42584_),
            new ItemStack(Items.f_42453_),
            new ItemStack(Items.f_42416_),
            new ItemStack(Items.f_42416_),
            new ItemStack(Items.f_42416_),
            new ItemStack(Items.f_42416_),
            new ItemStack(Items.f_41960_),
            new ItemStack(Items.f_41960_),
            new ItemStack(Items.f_42415_),
            new ItemStack(Items.f_42415_),
            new ItemStack(Items.f_42415_),
            new ItemStack(Items.f_42415_),
            new ItemStack(Items.f_42417_),
            new ItemStack(Items.f_42417_),
            new ItemStack(Items.f_42417_),
            new ItemStack(Items.f_42616_),
            new ItemStack(Items.f_42616_),
            new ItemStack(Items.f_42616_),
            new ItemStack(Items.f_42616_),
            new ItemStack(Items.f_42436_),
            new ItemStack(Items.f_42436_),
            new ItemStack(Items.f_42436_),
            new ItemStack(Items.f_42436_),
            new ItemStack(Items.f_42436_),
            new ItemStack(Items.f_42436_),
            new ItemStack(Items.f_42503_)
         };

         for (ItemStack stack : simpleDrops) {
            dropStack.accept(stack);
         }

         dropArrows.accept(new Random().nextInt(10, 20));
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
         ChrisData chrisData = ChrisData.get(serverLevel);
         if (!chrisData.tryClaim(serverLevel, this.m_20148_())) {
            this.m_146870_();
            return null;
         }
      }

      SpawnGroupData returnSpawnGroupData = super.m_6518_(serverLevelAccessor, difficultyInstance, mobSpawnType, spawngroupdata, compoundtag);
      ItemStack sword = new ItemStack(Items.f_42388_);
      sword.m_41663_(Enchantments.f_44980_, 5);
      sword.m_41663_(Enchantments.f_44977_, 5);
      sword.m_41663_(Enchantments.f_44986_, 5);
      this.m_8061_(EquipmentSlot.MAINHAND, sword);
      this.m_8061_(EquipmentSlot.OFFHAND, new ItemStack(Items.f_42584_));
      ItemStack diamondHelmet = new ItemStack(Items.f_42472_);
      diamondHelmet.m_41663_(Enchantments.f_44965_, 5);
      diamondHelmet.m_41663_(Enchantments.f_44986_, 5);
      this.m_8061_(EquipmentSlot.HEAD, diamondHelmet);
      ItemStack diamondChestplate = new ItemStack(Items.f_42473_);
      diamondChestplate.m_41663_(Enchantments.f_44965_, 5);
      diamondChestplate.m_41663_(Enchantments.f_44986_, 5);
      this.m_8061_(EquipmentSlot.CHEST, diamondChestplate);
      ItemStack diamondBoots = new ItemStack(Items.f_42475_);
      diamondBoots.m_41663_(Enchantments.f_44965_, 5);
      diamondBoots.m_41663_(Enchantments.f_44974_, 2);
      diamondBoots.m_41663_(Enchantments.f_44986_, 5);
      this.m_8061_(EquipmentSlot.FEET, diamondBoots);
      TeamUtil.addOrJoinTeam(this, "steve");
      return returnSpawnGroupData;
   }

   public void m_5993_(@NotNull Entity entity, int i, @NotNull DamageSource damagesource) {
      super.m_5993_(entity, i, damagesource);
   }

   public static boolean canSpawn(EntityType<ChrisEntity> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos position, RandomSource random) {
      ServerLevel serverLevel = level.m_6018_();
      return ChrisData.get(serverLevel).isOccupied(serverLevel) ? false : PathfinderMob.m_217057_(entityType, level, spawnType, position, random);
   }

   @Override
   protected void implementFirstTick(ServerLevel serverLevel) {
      super.implementFirstTick(serverLevel);
      this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.CHRIS_SAY_ON_SPAWN.get(), 1.0F, 1.0F);
   }

   @Override
   public void m_8119_() {
      super.m_8119_();
      if (!this.m_9236_().f_46443_ && this.state == 0 && this.m_21223_() <= 20.0F && !this.m_21120_(InteractionHand.OFF_HAND).m_41720_().equals(Items.f_42747_)
         )
       {
         this.m_21008_(InteractionHand.OFF_HAND, new ItemStack(Items.f_42747_));
      }
   }

   public void m_142687_(@NotNull RemovalReason reason) {
      super.m_142687_(reason);
      if (!this.m_9236_().f_46443_
         && this.m_9236_() instanceof ServerLevel serverLevel
         && (reason == RemovalReason.KILLED || reason == RemovalReason.DISCARDED)) {
         ChrisData.get(serverLevel).releaseIfMatches(serverLevel, this.m_20148_());
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
