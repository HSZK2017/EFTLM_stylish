package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModBlocks;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.item.ShadowObsidianPillarItem;
import com.pla.annoyingvillagers.item.ShadowObsidianSwordItem;
import com.pla.annoyingvillagers.item.ShadowObsidianWeaponItem;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import com.pla.annoyingvillagers.util.HerobrinePortalCombatUtil;
import com.pla.annoyingvillagers.util.HerobrineUtil;
import java.util.Random;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import org.jetbrains.annotations.NotNull;
import se.gory_moon.player_mobs.utils.NameManager;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class ShadowHerobrineEntity extends HerobrineMob {
   private BlockProjectileEntity darkObUp;
   private UUID darkObUpUUID;
   private BlockProjectileEntity darkObLeft;
   private UUID darkObLeftUUID;
   private BlockProjectileEntity darkObRight;
   private UUID darkObRightUUID;
   private int summonDarkObCooldown = 0;
   private int obsidianMachineGunCooldown = 0;
   private int darkObParryCooldown = 0;
   private int obsidianMachineGunTick = 0;

   public void clearDarkOb() {
      if (this.darkObUp != null) {
         this.darkObUp.m_146870_();
         this.darkObUpUUID = null;
         this.darkObUp = null;
      }

      if (this.darkObRight != null) {
         this.darkObRight.m_146870_();
         this.darkObRightUUID = null;
         this.darkObRight = null;
      }

      if (this.darkObLeft != null) {
         this.darkObLeft.m_146870_();
         this.darkObLeftUUID = null;
         this.darkObLeft = null;
      }
   }

   public void setObsidianMachineGunTick() {
      this.obsidianMachineGunTick = 20;
   }

   public int getObsidianMachineGunTick() {
      return this.obsidianMachineGunTick;
   }

   public boolean isDarkObReady() {
      return this.darkObUp != null || this.darkObLeft != null || this.darkObRight != null;
   }

   public int getSummonDarkObCooldown() {
      return this.summonDarkObCooldown;
   }

   public void setSummonDarkObCooldown(int summonDarkObCooldown) {
      this.summonDarkObCooldown = summonDarkObCooldown;
   }

   public int getObsidianMachineGunCooldown() {
      return this.obsidianMachineGunCooldown;
   }

   public ShadowHerobrineEntity(SpawnEntity spawnEntity, Level level) {
      this((EntityType<ShadowHerobrineEntity>)AnnoyingVillagersModEntities.SHADOW_HEROBRINE.get(), level);
   }

   public ShadowHerobrineEntity(EntityType<ShadowHerobrineEntity> entitytype, Level level) {
      super(entitytype, level);
      this.m_274367_(2.8F);
      this.f_21364_ = 60;
      this.m_21557_(false);
      this.m_6593_(this.m_5446_());
      this.m_20340_(true);
      this.m_21530_();
      this.setChatName(this.m_5446_().getString());
      this.m_8061_(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_PILLAR.get()));
   }

   @Override
   public boolean m_6469_(@NotNull DamageSource damageSource, float f) {
      if (!this.isSacrificing() && this.m_9236_() instanceof ServerLevel serverLevel) {
         if (Math.random() <= 0.5
            && !damageSource.m_276093_(DamageTypes.f_268612_)
            && !damageSource.m_276093_(DamageTypes.f_268631_)
            && !damageSource.m_276093_(DamageTypes.f_268468_)) {
            serverLevel.m_5594_(null, this.m_20183_(), (SoundEvent)AnnoyingVillagersModSounds.OBSIDIAN_PLACE.get(), SoundSource.NEUTRAL, 0.5F, 1.0F);
            HerobrineUtil.spawnObsidianEyeLineStaggered(serverLevel, this, ((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get()).m_49966_(), 1);
         } else if (this.getState() == 2 && damageSource.m_7639_() instanceof LivingEntity livingEntity && this.darkObParryCooldown == 0) {
            if (this.darkObUp != null) {
               this.darkObUp.m_20219_(livingEntity.m_20183_().m_252807_());
               this.shootOne(this.darkObUp, livingEntity.m_20097_().m_252807_(), 2.0, "up", this);
               this.darkObParryCooldown = 40;
               EpicfightUtil.damageBlocked(damageSource, this.darkObUp, serverLevel);
               if (!this.isDarkObReady()) {
                  this.summonDarkObCooldown = new Random().nextInt(200, 600);
               }

               return false;
            }

            if (this.darkObRight != null) {
               this.darkObRight.m_20219_(livingEntity.m_20183_().m_252807_());
               this.shootOne(this.darkObRight, livingEntity.m_20097_().m_252807_(), 2.0, "right", this);
               this.darkObParryCooldown = 40;
               EpicfightUtil.damageBlocked(damageSource, this.darkObRight, serverLevel);
               if (!this.isDarkObReady()) {
                  this.summonDarkObCooldown = new Random().nextInt(200, 600);
               }

               return false;
            }

            if (this.darkObLeft != null) {
               this.darkObLeft.m_20219_(livingEntity.m_20183_().m_252807_());
               this.shootOne(this.darkObLeft, livingEntity.m_20097_().m_252807_(), 2.0, "left", this);
               this.darkObParryCooldown = 40;
               EpicfightUtil.damageBlocked(damageSource, this.darkObLeft, serverLevel);
               if (!this.isDarkObReady()) {
                  this.summonDarkObCooldown = new Random().nextInt(200, 600);
               }

               return false;
            }
         }
      }

      if (damageSource.m_276093_(DamageTypes.f_268671_)) {
         return false;
      } else if (damageSource.m_276093_(DamageTypes.f_268585_)) {
         return false;
      } else if (damageSource.m_276093_(DamageTypes.f_268493_)) {
         return false;
      } else if (damageSource.m_276093_(DamageTypes.f_268722_)) {
         return false;
      } else if (damageSource.m_276093_(DamageTypes.f_268641_)) {
         return false;
      } else if (damageSource.m_276093_(DamageTypes.f_268482_)) {
         return false;
      } else {
         return !(damageSource.m_7640_() instanceof EnchantedArrowEntity)
               && damageSource.m_7640_() instanceof AbstractArrow
               && !(damageSource.m_7640_() instanceof BlueDemonThrownTridentEntity)
            ? false
            : super.m_6469_(damageSource, f);
      }
   }

   public void m_6667_(@NotNull DamageSource damagesource) {
      super.m_6667_(damagesource);
      if (this.darkObUp != null) {
         this.darkObUp.m_146870_();
      }

      if (this.darkObRight != null) {
         this.darkObRight.m_146870_();
      }

      if (this.darkObLeft != null) {
         this.darkObLeft.m_146870_();
      }

      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         InfectedPlayerNpcEntity corpse = new InfectedPlayerNpcEntity(
            (EntityType<? extends InfectedPlayerNpcEntity>)AnnoyingVillagersModEntities.INFECTED_PLAYER_NPC.get(), serverLevel
         );
         corpse.m_7678_(this.m_20185_(), this.m_20186_(), this.m_20189_(), this.m_146908_(), this.m_146909_());
         String killedName = this.getPersistentData().m_128461_("killed_name");
         corpse.getPersistentData().m_128359_("possessed_by", "shadow_herobrine");
         if (killedName.isEmpty()) {
            killedName = String.valueOf(NameManager.INSTANCE.getRandomName());
         }

         corpse.setUsername(killedName);
         corpse.m_6593_(Component.m_237113_(killedName));
         corpse.m_6518_(serverLevel, serverLevel.m_6436_(this.m_20183_()), MobSpawnType.MOB_SUMMONED, null, null);
         this.m_6842_(true);
         this.m_142687_(RemovalReason.KILLED);
         serverLevel.m_7967_(corpse);
      }
   }

   private void enchantGear(ItemStack itemStack) {
      itemStack.m_41663_(Enchantments.f_44977_, 5);
      itemStack.m_41663_(Enchantments.f_44983_, 5);
      itemStack.m_41663_(Enchantments.f_44980_, 3);
   }

   @Override
   public void rollItem() {
      super.rollItem();
      ItemStack mainHand = this.m_21205_();
      ItemStack offHandWeapon = ItemStack.f_41583_;
      ItemStack mainHandWeapon;
      if (mainHand.m_41720_() instanceof ShadowObsidianWeaponItem) {
         if (new Random().nextBoolean()) {
            mainHandWeapon = new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_PILLAR.get());
            if (this.getState() == 2) {
               this.enchantGear(mainHandWeapon);
            }

            if (new Random().nextBoolean()) {
               offHandWeapon = new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_SWORD.get());
               if (this.getState() == 2) {
                  this.enchantGear(offHandWeapon);
               }
            }
         } else {
            mainHandWeapon = new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_SWORD.get());
            if (this.getState() == 2) {
               this.enchantGear(mainHandWeapon);
            }

            if (new Random().nextBoolean()) {
               offHandWeapon = new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_SWORD.get());
               if (this.getState() == 2) {
                  this.enchantGear(offHandWeapon);
               }
            }
         }
      } else if (mainHand.m_41720_() instanceof ShadowObsidianPillarItem) {
         if (new Random().nextBoolean()) {
            mainHandWeapon = new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_WEAPON.get());
            if (this.getState() == 2) {
               this.enchantGear(mainHandWeapon);
            }
         } else {
            mainHandWeapon = new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_SWORD.get());
            if (this.getState() == 2) {
               this.enchantGear(mainHandWeapon);
            }

            if (new Random().nextBoolean()) {
               offHandWeapon = new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_SWORD.get());
               if (this.getState() == 2) {
                  this.enchantGear(offHandWeapon);
               }
            }
         }
      } else if (mainHand.m_41720_() instanceof ShadowObsidianSwordItem) {
         if (new Random().nextBoolean()) {
            mainHandWeapon = new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_WEAPON.get());
            if (this.getState() == 2) {
               this.enchantGear(mainHandWeapon);
            }
         } else {
            mainHandWeapon = new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_PILLAR.get());
            if (this.getState() == 2) {
               this.enchantGear(mainHandWeapon);
            }

            if (new Random().nextBoolean()) {
               offHandWeapon = new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_SWORD.get());
               if (this.getState() == 2) {
                  this.enchantGear(offHandWeapon);
               }
            }
         }
      } else {
         float chance = new Random().nextFloat();
         if (chance <= 0.3F) {
            if (new Random().nextBoolean()) {
               mainHandWeapon = new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_PILLAR.get());
               if (this.getState() == 2) {
                  this.enchantGear(mainHandWeapon);
               }

               if (new Random().nextBoolean()) {
                  offHandWeapon = new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_SWORD.get());
                  if (this.getState() == 2) {
                     this.enchantGear(offHandWeapon);
                  }
               }
            } else {
               mainHandWeapon = new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_SWORD.get());
               if (this.getState() == 2) {
                  this.enchantGear(mainHandWeapon);
               }

               if (new Random().nextBoolean()) {
                  offHandWeapon = new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_SWORD.get());
                  if (this.getState() == 2) {
                     this.enchantGear(offHandWeapon);
                  }
               }
            }
         } else if (chance <= 0.6F) {
            if (new Random().nextBoolean()) {
               mainHandWeapon = new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_WEAPON.get());
               if (this.getState() == 2) {
                  this.enchantGear(mainHandWeapon);
               }
            } else {
               mainHandWeapon = new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_SWORD.get());
               if (this.getState() == 2) {
                  this.enchantGear(mainHandWeapon);
               }

               if (new Random().nextBoolean()) {
                  offHandWeapon = new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_SWORD.get());
                  if (this.getState() == 2) {
                     this.enchantGear(offHandWeapon);
                  }
               }
            }
         } else if (new Random().nextBoolean()) {
            mainHandWeapon = new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_WEAPON.get());
            if (this.getState() == 2) {
               this.enchantGear(mainHandWeapon);
            }
         } else {
            mainHandWeapon = new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_PILLAR.get());
            if (this.getState() == 2) {
               this.enchantGear(mainHandWeapon);
            }

            if (new Random().nextBoolean()) {
               offHandWeapon = new ItemStack((ItemLike)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_SWORD.get());
               if (this.getState() == 2) {
                  this.enchantGear(offHandWeapon);
               }
            }
         }
      }

      this.m_21008_(InteractionHand.MAIN_HAND, mainHandWeapon);
      this.m_21008_(InteractionHand.OFF_HAND, offHandWeapon);
   }

   @Override
   public void m_7380_(@NotNull CompoundTag tag) {
      super.m_7380_(tag);
      if (this.getPersistentData().m_128441_("Shooting")) {
         tag.m_128405_("Shooting", this.getPersistentData().m_128451_("Shooting"));
      }

      if (this.darkObUpUUID != null) {
         tag.m_128362_("DarkObUpUUID", this.darkObUpUUID);
      }

      if (this.darkObLeftUUID != null) {
         tag.m_128362_("DarkObLeftUUID", this.darkObLeftUUID);
      }

      if (this.darkObRightUUID != null) {
         tag.m_128362_("DarkObRightUUID", this.darkObRightUUID);
      }
   }

   @Override
   public void m_7378_(@NotNull CompoundTag tag) {
      super.m_7378_(tag);
      if (tag.m_128441_("Shooting")) {
         this.getPersistentData().m_128405_("Shooting", tag.m_128451_("Shooting"));
      }

      if (tag.m_128403_("DarkObUpUUID")) {
         this.darkObUpUUID = tag.m_128342_("DarkObUpUUID");
      }

      if (tag.m_128403_("DarkObLeftUUID")) {
         this.darkObLeftUUID = tag.m_128342_("DarkObLeftUUID");
      }

      if (tag.m_128403_("DarkObRightUUID")) {
         this.darkObRightUUID = tag.m_128342_("DarkObRightUUID");
      }
   }

   private Vec3 getUpBlockPos() {
      double upY = 2.0;
      Vec3 eye = this.m_20299_(1.0F);
      return eye.m_82520_(0.0, 2.0, 0.0);
   }

   private Vec3 getRightBlockPos() {
      double lateral = 2.0;
      double sideY = 0.0;
      Vec3 eye = this.m_20299_(1.0F);
      Vec3 look = this.m_20252_(1.0F);
      Vec3 horiz = new Vec3(look.f_82479_, 0.0, look.f_82481_);
      if (horiz.m_82556_() < 1.0E-6) {
         float yaw = this.m_146908_() * (float) (Math.PI / 180.0);
         horiz = new Vec3((double)(-Mth.m_14031_(yaw)), 0.0, (double)Mth.m_14089_(yaw));
      }

      Vec3 upAxis = new Vec3(0.0, 1.0, 0.0);
      Vec3 rightAxis = horiz.m_82537_(upAxis).m_82541_();
      return eye.m_82549_(rightAxis.m_82490_(2.0)).m_82520_(0.0, 0.0, 0.0);
   }

   private Vec3 getLeftBlockPos() {
      double lateral = 2.0;
      double sideY = 0.0;
      Vec3 eye = this.m_20299_(1.0F);
      Vec3 look = this.m_20252_(1.0F);
      Vec3 horiz = new Vec3(look.f_82479_, 0.0, look.f_82481_);
      if (horiz.m_82556_() < 1.0E-6) {
         float yaw = this.m_146908_() * (float) (Math.PI / 180.0);
         horiz = new Vec3((double)(-Mth.m_14031_(yaw)), 0.0, (double)Mth.m_14089_(yaw));
      }

      Vec3 upAxis = new Vec3(0.0, 1.0, 0.0);
      Vec3 rightAxis = horiz.m_82537_(upAxis).m_82541_();
      Vec3 leftAxis = rightAxis.m_82490_(-1.0);
      return eye.m_82549_(leftAxis.m_82490_(2.0)).m_82520_(0.0, 0.0, 0.0);
   }

   public void spawnDarkObEntities() {
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         BlockState block = ((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_SHORT_PILLAR.get()).m_49966_();
         if (this.darkObUp == null) {
            BlockProjectileEntity darkObbyUp = new BlockProjectileEntity(this.m_9236_(), this, block);
            darkObbyUp.m_20242_(true);
            darkObbyUp.setNotReadyForShoot(true);
            darkObbyUp.m_20219_(this.getUpBlockPos());
            darkObbyUp.setOwnerUUID(this.m_20148_());
            serverLevel.m_7967_(darkObbyUp);
            this.darkObUpUUID = darkObbyUp.m_20148_();
            this.darkObUp = darkObbyUp;
         }

         if (this.darkObRight == null) {
            BlockProjectileEntity darkObbyRight = new BlockProjectileEntity(this.m_9236_(), this, block);
            darkObbyRight.m_20242_(true);
            darkObbyRight.setNotReadyForShoot(true);
            darkObbyRight.setOwnerUUID(this.m_20148_());
            darkObbyRight.m_20219_(this.getRightBlockPos());
            serverLevel.m_7967_(darkObbyRight);
            this.darkObRightUUID = darkObbyRight.m_20148_();
            this.darkObRight = darkObbyRight;
         }

         if (this.darkObLeft == null) {
            BlockProjectileEntity darkObbyLeft = new BlockProjectileEntity(this.m_9236_(), this, block);
            darkObbyLeft.m_20242_(true);
            darkObbyLeft.setNotReadyForShoot(true);
            darkObbyLeft.setOwnerUUID(this.m_20148_());
            darkObbyLeft.m_20219_(this.getLeftBlockPos());
            serverLevel.m_7967_(darkObbyLeft);
            this.darkObLeftUUID = darkObbyLeft.m_20148_();
            this.darkObLeft = darkObbyLeft;
         }
      }
   }

   public void shootChain(BlockState block, float velocity, int length) {
      Entity shooter = this;
      Level level = this.m_9236_();
      if (!level.f_46443_) {
         double eyeY = this.m_20188_();
         Vec3 look = this.m_20154_().m_82541_();
         LivingEntity target = this.m_5448_();
         if (target != null && target.m_6084_()) {
            Vec3 portalAimPosition = HerobrinePortalCombatUtil.getProjectilePortalAim(this, target);
            if (portalAimPosition != null) {
               look = portalAimPosition.m_82546_(this.m_146892_()).m_82541_();
            }
         }

         RandomSource rand = level.m_213780_();

         for (int i = 0; i < length; i++) {
            BlockProjectileEntity proj = new BlockProjectileEntity(level, shooter instanceof LivingEntity ? (LivingEntity)shooter : null, block);
            Vec3 forward = look.m_82490_((double)i * 1.0);
            double sideX = (rand.m_188500_() - 0.5) * 2.0;
            double sideY = (rand.m_188500_() - 0.5) * 2.0;
            double sideZ = (rand.m_188500_() - 0.5) * 2.0;
            proj.m_6034_(shooter.m_20185_() + forward.f_82479_ + sideX, eyeY + forward.f_82480_ + sideY, shooter.m_20189_() + forward.f_82481_ + sideZ);
            proj.m_20256_(look.m_82490_((double)velocity));
            level.m_7967_(proj);
         }
      }
   }

   private void shootOne(BlockProjectileEntity ob, Vec3 to, double speed, String position, ShadowHerobrineEntity shadowHerobrineEntity) {
      if (ob != null && ob.m_6084_()) {
         Vec3 dir = to.m_82546_(ob.m_20182_());
         if (dir.m_82556_() < 1.0E-6) {
            dir = this.m_20154_();
         }

         ob.m_20242_(false);
         Vec3 vel = dir.m_82541_().m_82490_(speed);
         ob.m_20256_(vel);
         ob.setNotReadyForShoot(false);
         if (position.equals("up")) {
            shadowHerobrineEntity.darkObUpUUID = null;
            shadowHerobrineEntity.darkObUp = null;
         } else if (position.equals("left")) {
            shadowHerobrineEntity.darkObLeftUUID = null;
            shadowHerobrineEntity.darkObLeft = null;
         } else if (position.equals("right")) {
            shadowHerobrineEntity.darkObRightUUID = null;
            shadowHerobrineEntity.darkObRight = null;
         }
      }
   }

   public void shootDarkObsAtTarget(double speed) {
      if (!this.m_9236_().f_46443_) {
         LivingEntity target = this.m_5448_();
         Vec3 to;
         if (target != null && target.m_6084_()) {
            to = target.m_20299_(1.0F);
            Vec3 portalAimPosition = HerobrinePortalCombatUtil.getProjectilePortalAim(this, target);
            if (portalAimPosition != null) {
               to = portalAimPosition;
            }
         } else {
            to = this.m_146892_().m_82549_(this.m_20154_().m_82490_(16.0));
         }

         if (this.darkObUp != null) {
            this.shootOne(this.darkObUp, to, speed, "up", this);
         }

         if (this.darkObLeft != null) {
            this.shootOne(this.darkObLeft, to, speed, "left", this);
         }

         if (this.darkObRight != null) {
            this.shootOne(this.darkObRight, to, speed, "right", this);
         }

         this.summonDarkObCooldown = new Random().nextInt(200, 600);
      }
   }

   @Override
   public void m_8119_() {
      super.m_8119_();
      if (this.m_9236_() instanceof ServerLevel) {
         if (this.summonDarkObCooldown > 0) {
            this.summonDarkObCooldown--;
         }

         if (this.darkObParryCooldown > 0) {
            this.darkObParryCooldown--;
         }

         if (this.obsidianMachineGunCooldown > 0) {
            this.obsidianMachineGunCooldown--;
         }

         if (this.obsidianMachineGunTick > 0) {
            if (this.obsidianMachineGunTick == 1) {
               this.obsidianMachineGunCooldown = new Random().nextInt(200, 300);
               this.rollItem();
            }

            this.obsidianMachineGunTick--;
            BlockState block = ((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get()).m_49966_();
            this.m_20256_(Vec3.f_82478_);
            this.shootChain(block, 2.5F, 5);
         }

         if (this.darkObUp == null && this.darkObUpUUID != null) {
            if (((ServerLevel)this.m_9236_()).m_8791_(this.darkObUpUUID) instanceof BlockProjectileEntity blockProjectileEntity) {
               this.darkObUp = blockProjectileEntity;
            } else {
               this.darkObUpUUID = null;
            }
         }

         if (this.darkObLeft == null && this.darkObLeftUUID != null) {
            if (((ServerLevel)this.m_9236_()).m_8791_(this.darkObLeftUUID) instanceof BlockProjectileEntity blockProjectileEntity) {
               this.darkObLeft = blockProjectileEntity;
            } else {
               this.darkObLeftUUID = null;
            }
         }

         if (this.darkObRight == null && this.darkObRightUUID != null) {
            if (((ServerLevel)this.m_9236_()).m_8791_(this.darkObRightUUID) instanceof BlockProjectileEntity blockProjectileEntity) {
               this.darkObRight = blockProjectileEntity;
            } else {
               this.darkObRightUUID = null;
            }
         }

         if (this.darkObUp != null) {
            this.darkObUp.m_20219_(this.getUpBlockPos());
         }

         if (this.darkObRight != null) {
            this.darkObRight.m_20219_(this.getRightBlockPos());
         }

         if (this.darkObLeft != null) {
            this.darkObLeft.m_20219_(this.getLeftBlockPos());
         }
      }
   }

   @Override
   public void m_142687_(@NotNull RemovalReason reason) {
      super.m_142687_(reason);
      if (this.darkObUp != null) {
         this.darkObUp.m_146870_();
      }

      if (this.darkObLeft != null) {
         this.darkObLeft.m_146870_();
      }

      if (this.darkObRight != null) {
         this.darkObRight.m_146870_();
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
