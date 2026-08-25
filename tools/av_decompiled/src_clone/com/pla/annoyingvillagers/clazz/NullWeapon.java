package com.pla.annoyingvillagers.clazz;

import com.pla.annoyingvillagers.entity.NullEntity;
import com.pla.annoyingvillagers.entity.goal.PortalApproachGoal;
import com.pla.annoyingvillagers.gameasset.AVAnimations;
import com.pla.annoyingvillagers.gameasset.AVSkills;
import com.pla.annoyingvillagers.gameasset.AnimsWom;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.item.NullWeaponItem;
import com.pla.annoyingvillagers.skill.NullWeaponSkill;
import com.pla.annoyingvillagers.task.DelayedTask;
import com.pla.annoyingvillagers.util.TeamUtil;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import reascer.wom.world.entity.mob.EnderHand;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;

public class NullWeapon extends Monster {
   protected UUID nullUUID;
   protected NullEntity nullEntity;
   protected UUID playerUUID;
   protected Player player;
   protected String weapon;
   private boolean spinning = false;
   private int releaseCooldown = 0;
   protected boolean released = false;

   public boolean isReleased() {
      return this.released;
   }

   public void stopRelease() {
      this.releaseCooldown = 1;
   }

   public void releaseForAWhile() {
      this.releaseCooldown = new Random().nextInt(300, 600);
      this.released = true;
   }

   public void setSpinning(boolean spinning) {
      this.spinning = spinning;
   }

   public boolean isSpinning() {
      return this.spinning;
   }

   public void setReleased(boolean released) {
      this.released = released;
      if (released) {
         this.spinfor5seconds();
      }
   }

   public void spinfor5seconds() {
      final LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(this, LivingEntityPatch.class);
      if (livingEntityPatch != null) {
         livingEntityPatch.playAnimationSynchronized(AnimsWom.GLOWING_AGONY_GUARD, 0.0F);
         new DelayedTask(100) {
            @Override
            public void run() {
               livingEntityPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
            }
         };
      }
   }

   public void setWeapon(String weapon) {
      this.weapon = weapon;
      switch (weapon) {
         case "sword":
            this.m_8061_(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)AnnoyingVillagersModItems.NULL_SWORD.get()));
            break;
         case "pickaxe":
            this.m_8061_(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)AnnoyingVillagersModItems.NULL_PICKAXE.get()));
            break;
         case "axe":
            this.m_8061_(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)AnnoyingVillagersModItems.NULL_AXE.get()));
            break;
         case "hoe":
            this.m_8061_(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)AnnoyingVillagersModItems.NULL_HOE.get()));
            break;
         default:
            this.m_8061_(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)AnnoyingVillagersModItems.NULL_SHOVEL.get()));
      }
   }

   public void setNullUUID(UUID nullUUID) {
      this.nullUUID = nullUUID;
   }

   public void setNullEntity(NullEntity nullEntity) {
      this.nullEntity = nullEntity;
   }

   public void setPlayerUUID(UUID playerUUID) {
      this.playerUUID = playerUUID;
   }

   public void setPlayer(Player player) {
      this.player = player;
   }

   public UUID getPlayerUUID() {
      return this.playerUUID;
   }

   public UUID getNullUUID() {
      return this.nullUUID;
   }

   public NullEntity getNullEntity() {
      return this.nullEntity;
   }

   protected NullWeapon(EntityType<? extends Monster> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.m_274367_(4.0F);
      this.f_21364_ = 80;
      this.m_21557_(false);
      this.m_21530_();
      this.f_21342_ = new FlyingMoveControl(this, 10, true);
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   @NotNull
   protected PathNavigation m_6037_(@NotNull Level level) {
      return new FlyingPathNavigation(this, level);
   }

   protected void m_8099_() {
      super.m_8099_();
      this.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(this, LivingEntity.class, 10, true, false, target -> {
         if (this.player != null && this.player.m_6084_()) {
            LivingEntity lastHurtBy = this.player.m_21188_();
            LivingEntity lastHurt = this.player.m_21214_();
            return (target == lastHurtBy || target == lastHurt) && target.m_6084_() && !target.m_7307_(this.player);
         } else {
            return false;
         }
      }));
      this.f_21346_
         .m_25352_(
            1,
            new NearestAttackableTargetGoal(
               this,
               LivingEntity.class,
               10,
               true,
               false,
               target -> this.nullEntity != null && this.nullEntity.m_6084_() && target != null && this.nullEntity.m_5448_() == target
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
               target -> this.nullEntity != null && this.nullEntity.m_6084_() && target != null && target.m_21214_() == this.nullEntity
            )
         );
      this.f_21345_.m_25352_(2, new PortalApproachGoal(this));
      this.f_21345_.m_25352_(3, new RandomStrollGoal(this, 0.4, 20) {
         protected Vec3 m_7037_() {
            Random random = new Random();
            double d0 = NullWeapon.this.m_20185_() + (double)(random.nextFloat() * 2.0F - 1.0F);
            double d1 = NullWeapon.this.m_20186_() + (double)(random.nextFloat() * 2.0F - 1.0F);
            double d2 = NullWeapon.this.m_20189_() + (double)(random.nextFloat() * 2.0F - 1.0F);
            return new Vec3(d0, d1, d2);
         }
      });
      this.f_21345_.m_25352_(4, new LookAtPlayerGoal(this, NullEntity.class, 6.0F));
      this.f_21345_.m_25352_(5, new FloatGoal(this));
      this.f_21346_.m_25352_(6, new HurtByTargetGoal(this, new Class[0]));
      this.f_21345_.m_25352_(7, new Goal() {
         {
            this.m_7021_(EnumSet.of(Flag.MOVE));
         }

         public boolean m_8036_() {
            return NullWeapon.this.m_5448_() != null && !NullWeapon.this.m_21566_().m_24995_();
         }

         public boolean m_8045_() {
            return NullWeapon.this.m_21566_().m_24995_() && NullWeapon.this.m_5448_() != null && NullWeapon.this.m_5448_().m_6084_();
         }

         public void m_8056_() {
            LivingEntity livingentity = NullWeapon.this.m_5448_();
            if (livingentity != null) {
               Vec3 vec3 = livingentity.m_20299_(1.0F);
               NullWeapon.this.f_21342_.m_6849_(vec3.f_82479_, vec3.f_82480_, vec3.f_82481_, 2.0);
            }
         }

         public void m_8037_() {
            LivingEntity livingentity = NullWeapon.this.m_5448_();
            if (livingentity != null) {
               if (NullWeapon.this.m_20191_().m_82381_(livingentity.m_20191_())) {
                  NullWeapon.this.m_7327_(livingentity);
               } else {
                  double d0 = NullWeapon.this.m_20280_(livingentity);
                  if (d0 < 16.0) {
                     Vec3 vec3 = livingentity.m_20299_(1.0F);
                     NullWeapon.this.f_21342_.m_6849_(vec3.f_82479_, vec3.f_82480_, vec3.f_82481_, 2.0);
                  }
               }
            }
         }
      });
   }

   public void m_7380_(@NotNull CompoundTag tag) {
      super.m_7380_(tag);
      tag.m_128359_("Weapon", this.weapon);
      if (this.nullUUID != null) {
         tag.m_128362_("NullUUID", this.nullUUID);
      }

      if (this.playerUUID != null) {
         tag.m_128362_("OwnerUUID", this.playerUUID);
      }

      tag.m_128379_("Released", this.released);
      tag.m_128405_("ReleaseCooldown", this.releaseCooldown);
   }

   public void m_7378_(@NotNull CompoundTag tag) {
      super.m_7378_(tag);
      if (tag.m_128403_("NullUUID")) {
         this.nullUUID = tag.m_128342_("NullUUID");
      }

      if (tag.m_128403_("OwnerUUID")) {
         this.playerUUID = tag.m_128342_("OwnerUUID");
      }

      this.weapon = tag.m_128461_("Weapon");
      this.released = tag.m_128471_("Released");
      this.releaseCooldown = tag.m_128451_("ReleaseCooldown");
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
   public SoundEvent m_7975_(@NotNull DamageSource damagesource) {
      return Objects.requireNonNull((SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.fromNamespaceAndPath("", "")));
   }

   @NotNull
   public SoundEvent m_5592_() {
      return Objects.requireNonNull((SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.fromNamespaceAndPath("", "")));
   }

   public boolean m_142535_(float f, float f1, @NotNull DamageSource damagesource) {
      return false;
   }

   public boolean m_6469_(@NotNull DamageSource damagesource, float f) {
      return false;
   }

   public SpawnGroupData m_6518_(
      @NotNull ServerLevelAccessor serverlevelaccessor,
      @NotNull DifficultyInstance difficultyinstance,
      @NotNull MobSpawnType mobspawntype,
      @Nullable SpawnGroupData spawngroupdata,
      @Nullable CompoundTag compoundtag
   ) {
      TeamUtil.addOrJoinTeam(this, "herobrine");
      this.m_8061_(EquipmentSlot.LEGS, ItemStack.f_41583_);
      this.m_8061_(EquipmentSlot.CHEST, ItemStack.f_41583_);
      this.m_8061_(EquipmentSlot.HEAD, ItemStack.f_41583_);
      this.m_8061_(EquipmentSlot.FEET, ItemStack.f_41583_);
      this.m_8061_(EquipmentSlot.OFFHAND, ItemStack.f_41583_);
      this.m_20331_(true);
      return super.m_6518_(serverlevelaccessor, difficultyinstance, mobspawntype, spawngroupdata, compoundtag);
   }

   protected void m_7840_(double d0, boolean flag, @NotNull BlockState blockstate, @NotNull BlockPos blockpos) {
   }

   public void m_20242_(boolean flag) {
      super.m_20242_(true);
   }

   public void m_8107_() {
      super.m_8107_();
      this.m_20242_(true);
   }

   public void increaseSkillPoint(Entity entity, float value) {
      if (entity instanceof Player pEntity) {
         PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(pEntity, PlayerPatch.class);
         if (playerPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            SkillContainer skillContainer = serverPlayerPatch.getSkill(AVSkills.NULL_WEAPON);
            if (skillContainer != null) {
               NullWeaponSkill skill = (NullWeaponSkill)skillContainer.getSkill();
               float currentResource = skillContainer.getResource();
               float neededResource = skillContainer.getNeededResource();
               float addResource = Math.min(value, neededResource);
               skill.setConsumptionSynchronize(skillContainer, currentResource + addResource);
            }
         }
      }
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
         this.increaseSkillPoint(this.player, 5.0F);
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
      } else if (this.nullEntity != null) {
         float fx = (float)this.m_21133_(Attributes.f_22281_);
         float f1x = (float)this.m_21133_(Attributes.f_22282_);
         if (pEntity instanceof LivingEntity) {
            fx += EnchantmentHelper.m_44833_(this.m_21205_(), ((LivingEntity)pEntity).m_6336_());
            f1x += (float)EnchantmentHelper.m_44894_(this);
         }

         int ix = EnchantmentHelper.m_44914_(this);
         if (ix > 0) {
            pEntity.m_20254_(ix * 4);
         }

         boolean flag = pEntity.m_6469_(this.m_269291_().m_269333_(this.nullEntity), fx);
         if (flag) {
            if (f1x > 0.0F && pEntity instanceof LivingEntity) {
               ((LivingEntity)pEntity)
                  .m_147240_(
                     (double)(f1x * 0.5F),
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

   private static boolean isAllowedHeldCategory(Player p) {
      ItemStack main = p.m_21205_();
      if (main.m_41720_() instanceof NullWeaponItem) {
         return true;
      } else if (!(EpicFightCapabilities.getItemStackCapability(main) instanceof WeaponCapability weaponCap)) {
         return true;
      } else {
         WeaponCategory cat = weaponCap.getWeaponCategory();
         return cat == WeaponCategories.BOW || cat == WeaponCategories.CROSSBOW || cat == WeaponCategories.NOT_WEAPON;
      }
   }

   private static boolean hasNullSword(Player p) {
      for (ItemStack s : p.m_150109_().f_35974_) {
         if (s.m_41720_() instanceof NullWeaponItem) {
            return true;
         }
      }

      for (ItemStack sx : p.m_150109_().f_35976_) {
         if (sx.m_41720_() instanceof NullWeaponItem) {
            return true;
         }
      }

      return false;
   }

   public void m_8119_() {
      super.m_8119_();
      String stack = this.weapon;

      ItemStack check = switch (stack) {
         case "sword" -> new ItemStack((ItemLike)AnnoyingVillagersModItems.NULL_SWORD.get());
         case "pickaxe" -> new ItemStack((ItemLike)AnnoyingVillagersModItems.NULL_PICKAXE.get());
         case "axe" -> new ItemStack((ItemLike)AnnoyingVillagersModItems.NULL_AXE.get());
         case "hoe" -> new ItemStack((ItemLike)AnnoyingVillagersModItems.NULL_HOE.get());
         default -> new ItemStack((ItemLike)AnnoyingVillagersModItems.NULL_SHOVEL.get());
      };
      if (this.m_6844_(EquipmentSlot.MAINHAND).m_41720_() != check.m_41720_()) {
         if (this.nullEntity == null && this.player != null) {
            this.m_146870_();
         }

         this.m_8061_(EquipmentSlot.MAINHAND, check);
      }

      if (this.m_6844_(EquipmentSlot.OFFHAND) != ItemStack.f_41583_) {
         this.m_8061_(EquipmentSlot.OFFHAND, ItemStack.f_41583_);
      }

      if (this.m_6844_(EquipmentSlot.HEAD) != ItemStack.f_41583_) {
         this.m_8061_(EquipmentSlot.HEAD, ItemStack.f_41583_);
      }

      if (this.m_6844_(EquipmentSlot.CHEST) != ItemStack.f_41583_) {
         this.m_8061_(EquipmentSlot.CHEST, ItemStack.f_41583_);
      }

      if (this.m_6844_(EquipmentSlot.LEGS) != ItemStack.f_41583_) {
         this.m_8061_(EquipmentSlot.LEGS, ItemStack.f_41583_);
      }

      if (this.m_6844_(EquipmentSlot.FEET) != ItemStack.f_41583_) {
         this.m_8061_(EquipmentSlot.FEET, ItemStack.f_41583_);
      }

      if (!this.m_9236_().f_46443_) {
         if (this.nullEntity != null && !this.nullEntity.m_6084_()) {
            this.m_146870_();
            return;
         }

         if (this.nullEntity == null && this.nullUUID == null && this.player == null && this.playerUUID == null) {
            this.m_146870_();
            return;
         }

         ItemStack stack = this.m_21205_();
         this.m_21153_((float)(stack.m_41776_() - stack.m_41773_()));
         if (this.nullEntity == null && this.nullUUID != null) {
            if (((ServerLevel)this.m_9236_()).m_8791_(this.nullUUID) instanceof NullEntity entityNull) {
               this.nullEntity = entityNull;
            } else {
               this.nullEntity = null;
            }
         }

         if (this.nullEntity != null && !this.nullEntity.m_6084_()) {
            this.nullEntity = null;
            this.nullUUID = null;
         }

         if (this.player == null && this.playerUUID != null) {
            this.player = this.m_9236_().m_46003_(this.playerUUID);
         }

         if (this.player != null && !this.player.m_6084_()) {
            this.m_142687_(RemovalReason.KILLED);
         }

         if (this.player != null && this.player.m_6084_() && (!hasNullSword(this.player) || !isAllowedHeldCategory(this.player))) {
            this.m_142687_(RemovalReason.KILLED);
         }
      }

      if (this.m_5448_() == null && this.released) {
         this.released = false;
      }

      if (this.releaseCooldown > 0) {
         this.releaseCooldown--;
      }

      if (this.releaseCooldown == 0 && this.nullEntity != null && this.released) {
         this.released = false;
      }
   }

   public static LivingEntity getNearestLivingEntity(Level level, Entity sourceEntity, double range) {
      AABB searchBox = sourceEntity.m_20191_().m_82400_(range);
      return level.m_45982_(
         level.m_6443_(
            LivingEntity.class,
            searchBox,
            e -> e != sourceEntity && !(e instanceof NullWeapon) && !(e instanceof EnderHand) && !e.m_7307_(sourceEntity) && e.m_6084_()
         ),
         TargetingConditions.f_26872_,
         (LivingEntity)sourceEntity,
         sourceEntity.m_20185_(),
         sourceEntity.m_20186_(),
         sourceEntity.m_20189_()
      );
   }

   public void processTeleportByPlayer() {
      if (this.player != null) {
         if (!this.isReleased()) {
            this.m_6027_(
               this.player.m_20185_() + new Random().nextDouble(-4.0, 4.0),
               this.player.m_20186_() + new Random().nextDouble(-2.0, 2.0),
               this.player.m_20189_() + new Random().nextDouble(-4.0, 4.0)
            );
         } else if (this.isReleased() && (this.player.m_21188_() != null || this.player.m_21214_() != null)) {
            LivingEntity target = this.player.m_21188_() != null ? this.player.m_21188_() : (this.player.m_21214_() != null ? this.player.m_21214_() : null);
            if (target == null) {
               target = getNearestLivingEntity(this.player.m_9236_(), this.player, 12.0);
            }

            if (target != null && target.m_6084_()) {
               this.m_6027_(
                  target.m_20185_() + new Random().nextDouble(-4.0, 4.0),
                  target.m_20186_() + new Random().nextDouble(-2.0, 2.0),
                  target.m_20189_() + new Random().nextDouble(-4.0, 4.0)
               );
            } else {
               this.released = false;
            }
         }
      }
   }

   public void processTeleportByNullEntity() {
      if (this.nullEntity != null) {
         if (!this.isReleased()) {
            this.m_6027_(
               this.nullEntity.m_20185_() + new Random().nextDouble(-4.0, 4.0),
               this.nullEntity.m_20186_() + new Random().nextDouble(-2.0, 2.0),
               this.nullEntity.m_20189_() + new Random().nextDouble(-4.0, 4.0)
            );
         } else if (this.isReleased() && (this.nullEntity.m_21188_() != null || this.nullEntity.m_21214_() != null)) {
            LivingEntity target = this.nullEntity.m_5448_() != null
               ? this.nullEntity.m_5448_()
               : (this.nullEntity.m_21188_() != null ? this.nullEntity.m_21188_() : (this.nullEntity.m_21214_() != null ? this.nullEntity.m_21214_() : null));
            if (target == null) {
               target = getNearestLivingEntity(this.nullEntity.m_9236_(), this.nullEntity, 12.0);
            }

            if (target != null && target.m_6084_()) {
               this.m_6027_(
                  target.m_20185_() + new Random().nextDouble(-4.0, 4.0),
                  target.m_20186_() + new Random().nextDouble(-2.0, 2.0),
                  target.m_20189_() + new Random().nextDouble(-4.0, 4.0)
               );
            } else {
               this.stopRelease();
            }
         }
      }
   }

   public void summonNullWeaponForPlayer(String uuidNbt, ServerLevel serverLevel, Player summoner) {
      this.m_6027_(
         summoner.m_20185_() + new Random().nextDouble(-4.0, 4.0),
         summoner.m_20186_() + new Random().nextDouble(-2.0, 2.0),
         summoner.m_20189_() + new Random().nextDouble(-4.0, 4.0)
      );
      this.playerUUID = summoner.m_20148_();
      this.player = summoner;
      this.m_6518_(serverLevel, serverLevel.m_6436_(this.m_20183_()), MobSpawnType.MOB_SUMMONED, null, null);
      serverLevel.m_7967_(this);
      summoner.getPersistentData().m_128362_(uuidNbt, this.m_20148_());
   }

   public void summonNullWeaponForNullEntity(ServerLevel serverLevel, NullEntity summoner, String toolName) {
      this.m_6027_(
         summoner.m_20185_() + new Random().nextDouble(-4.0, 4.0),
         summoner.m_20186_() + new Random().nextDouble(-2.0, 2.0),
         summoner.m_20189_() + new Random().nextDouble(-4.0, 4.0)
      );
      this.nullUUID = summoner.m_20148_();
      this.nullEntity = summoner;
      this.m_6518_(serverLevel, serverLevel.m_6436_(this.m_20183_()), MobSpawnType.MOB_SUMMONED, null, null);
      serverLevel.m_7967_(this);
      summoner.setNullWeapon(toolName, this);
      this.spinfor5seconds();
   }

   public void m_142687_(@NotNull RemovalReason pReason) {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         if (this.player != null) {
            String var5 = this.weapon;
            switch (var5) {
               case "sword":
                  this.player.getPersistentData().m_128473_("NullSwordUUID");
                  break;
               case "pickaxe":
                  this.player.getPersistentData().m_128473_("NullPickaxeUUID");
                  break;
               case "axe":
                  this.player.getPersistentData().m_128473_("NullAxeUUID");
                  break;
               case "hoe":
                  this.player.getPersistentData().m_128473_("NullHoeUUID");
                  break;
               default:
                  this.player.getPersistentData().m_128473_("NullShovelUUID");
            }
         } else {
            ItemEntity item = new ItemEntity(serverLevel, this.m_20185_(), this.m_20186_(), this.m_20189_(), this.m_21205_());
            item.m_32010_(10);
            serverLevel.m_7967_(item);
         }
      }

      super.m_142687_(pReason);
   }

   public static Builder createAttributes() {
      Builder builder = Mob.m_21552_();
      builder = builder.m_22268_(Attributes.f_22279_, 2.0);
      builder = builder.m_22268_(Attributes.f_22276_, 100.0);
      builder = builder.m_22268_(Attributes.f_22281_, 0.0);
      builder = builder.m_22268_(Attributes.f_22277_, 24.0);
      return builder.m_22268_(Attributes.f_22280_, 2.0);
   }
}
