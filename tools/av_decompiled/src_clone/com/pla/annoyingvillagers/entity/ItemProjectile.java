package com.pla.annoyingvillagers.entity;

import com.google.common.collect.Multimap;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.item.FishingRodGrappleUtil;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import com.pla.annoyingvillagers.util.HookUtil;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.StunType;

public class ItemProjectile extends Projectile implements ItemSupplier {
   private static final int MIN_ARC_TRAVEL_TICKS = 14;
   private static final int MAX_ARC_TRAVEL_TICKS = 34;
   private boolean arcInitialized = false;
   private Vec3 arcStart = Vec3.f_82478_;
   private Vec3 arcSide = Vec3.f_82478_;
   private int arcTravelTicks = 14;
   private double arcHeight = 1.0;
   private static final EntityDataAccessor<ItemStack> DATA_STACK = SynchedEntityData.m_135353_(ItemProjectile.class, EntityDataSerializers.f_135033_);
   private static final EntityDataAccessor<Boolean> DATA_DISARM_LAUNCH_MODE = SynchedEntityData.m_135353_(ItemProjectile.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Integer> DATA_DISARM_DROP_AFTER_TICKS = SynchedEntityData.m_135353_(
      ItemProjectile.class, EntityDataSerializers.f_135028_
   );
   private static final EntityDataAccessor<Float> DATA_DISARM_MOTION_X = SynchedEntityData.m_135353_(ItemProjectile.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Float> DATA_DISARM_MOTION_Y = SynchedEntityData.m_135353_(ItemProjectile.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Float> DATA_DISARM_MOTION_Z = SynchedEntityData.m_135353_(ItemProjectile.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Boolean> DATA_HOOK_ATTACHED = SynchedEntityData.m_135353_(ItemProjectile.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> DATA_DISCARD_WHEN_HOOK_LOST = SynchedEntityData.m_135353_(
      ItemProjectile.class, EntityDataSerializers.f_135035_
   );
   private static final double ARRIVE_DISTANCE = 0.65;
   private static final int MAX_LIFE = 80;
   private static final int HIT_COOLDOWN_TICKS = 8;
   private final Map<Integer, Integer> recentHits = new HashMap<>();

   public ItemProjectile(EntityType<? extends ItemProjectile> type, Level level) {
      super(type, level);
      this.f_19794_ = true;
      this.m_20242_(true);
   }

   public ItemProjectile(Level level, LivingEntity owner, ItemStack stack, Vec3 spawnPos) {
      this((EntityType<? extends ItemProjectile>)AnnoyingVillagersModEntities.ITEM_PROJECTILE.get(), level);
      this.m_5602_(owner);
      this.setWeaponStack(stack);
      this.m_6034_(spawnPos.f_82479_, spawnPos.f_82480_ + 0.25, spawnPos.f_82481_);
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(DATA_STACK, ItemStack.f_41583_);
      this.f_19804_.m_135372_(DATA_DISARM_LAUNCH_MODE, false);
      this.f_19804_.m_135372_(DATA_DISARM_DROP_AFTER_TICKS, 18);
      this.f_19804_.m_135372_(DATA_DISARM_MOTION_X, 0.0F);
      this.f_19804_.m_135372_(DATA_DISARM_MOTION_Y, 0.0F);
      this.f_19804_.m_135372_(DATA_DISARM_MOTION_Z, 0.0F);
      this.f_19804_.m_135372_(DATA_HOOK_ATTACHED, false);
      this.f_19804_.m_135372_(DATA_DISCARD_WHEN_HOOK_LOST, false);
   }

   private boolean isDisarmLaunchMode() {
      return (Boolean)this.f_19804_.m_135370_(DATA_DISARM_LAUNCH_MODE);
   }

   private int getDisarmDropAfterTicks() {
      return (Integer)this.f_19804_.m_135370_(DATA_DISARM_DROP_AFTER_TICKS);
   }

   private Vec3 getSyncedDisarmLaunchMotion() {
      return new Vec3(
         (double)((Float)this.f_19804_.m_135370_(DATA_DISARM_MOTION_X)).floatValue(),
         (double)((Float)this.f_19804_.m_135370_(DATA_DISARM_MOTION_Y)).floatValue(),
         (double)((Float)this.f_19804_.m_135370_(DATA_DISARM_MOTION_Z)).floatValue()
      );
   }

   private void tickDisarmLaunchMode() {
      this.f_19794_ = false;
      this.m_20242_(true);
      Vec3 oldPos = this.m_20182_();
      Vec3 motion = this.m_20184_();
      if (motion.m_82556_() < 1.0E-7) {
         motion = this.getSyncedDisarmLaunchMotion();
      }

      this.m_6478_(MoverType.SELF, motion);
      Vec3 moved = this.m_20182_().m_82546_(oldPos);
      this.updateRotationFromMotion(moved.m_82556_() > 1.0E-7 ? moved : motion);
      Vec3 nextMotion = motion.m_82542_(0.94, 0.96, 0.94).m_82520_(0.0, -0.045, 0.0);
      this.m_20256_(nextMotion);
      if (!this.m_9236_().f_46443_ && (this.f_19797_ >= this.getDisarmDropAfterTicks() || this.m_20096_() || this.f_19862_ || this.f_19863_)) {
         this.dropBackToItem(nextMotion);
      }
   }

   public static ItemProjectile createDisarmLaunch(Level level, LivingEntity owner, ItemStack stack, Vec3 spawnPos, Vec3 launchMotion, int dropAfterTicks) {
      ItemProjectile projectile = new ItemProjectile(level, owner, stack, spawnPos);
      projectile.m_6034_(spawnPos.f_82479_, spawnPos.f_82480_, spawnPos.f_82481_);
      projectile.f_19804_.m_135381_(DATA_DISARM_LAUNCH_MODE, true);
      projectile.f_19804_.m_135381_(DATA_DISARM_DROP_AFTER_TICKS, Mth.m_14045_(dropAfterTicks, 4, 80));
      projectile.f_19804_.m_135381_(DATA_DISARM_MOTION_X, (float)launchMotion.f_82479_);
      projectile.f_19804_.m_135381_(DATA_DISARM_MOTION_Y, (float)launchMotion.f_82480_);
      projectile.f_19804_.m_135381_(DATA_DISARM_MOTION_Z, (float)launchMotion.f_82481_);
      projectile.f_19794_ = false;
      projectile.m_20242_(true);
      projectile.m_20256_(launchMotion);
      return projectile;
   }

   public static ItemProjectile createHookPayload(Level level, Entity owner, ItemStack stack, Vec3 spawnPos) {
      ItemProjectile projectile = new ItemProjectile((EntityType<? extends ItemProjectile>)AnnoyingVillagersModEntities.ITEM_PROJECTILE.get(), level);
      projectile.m_5602_(owner);
      projectile.setWeaponStack(stack);
      projectile.m_6034_(spawnPos.f_82479_, spawnPos.f_82480_, spawnPos.f_82481_);
      projectile.f_19794_ = true;
      projectile.m_20242_(true);
      projectile.setHookAttached(true);
      return projectile;
   }

   public void setWeaponStack(ItemStack stack) {
      this.f_19804_.m_135381_(DATA_STACK, stack.m_41777_());
   }

   public ItemStack getWeaponStack() {
      return (ItemStack)this.f_19804_.m_135370_(DATA_STACK);
   }

   @NotNull
   public ItemStack m_7846_() {
      return this.getWeaponStack();
   }

   public boolean isHookAttached() {
      return (Boolean)this.f_19804_.m_135370_(DATA_HOOK_ATTACHED);
   }

   public void setHookAttached(boolean hookAttached) {
      this.f_19804_.m_135381_(DATA_HOOK_ATTACHED, hookAttached);
   }

   public void setDiscardWhenHookLost(boolean discardWhenHookLost) {
      this.f_19804_.m_135381_(DATA_DISCARD_WHEN_HOOK_LOST, discardWhenHookLost);
   }

   public void moveWithHook(Vec3 newPos, Entity ownerEntity) {
      Vec3 oldPos = this.m_20182_();
      Vec3 motion = newPos.m_82546_(oldPos);
      this.setHookAttached(true);
      this.f_19794_ = true;
      this.m_20242_(true);
      this.m_20256_(motion);
      this.m_6034_(newPos.f_82479_, newPos.f_82480_, newPos.f_82481_);
      this.updateRotationFromMotion(motion);
      if (!this.m_9236_().f_46443_) {
         this.damageEntitiesAlongPath(oldPos, newPos, ownerEntity);
      }
   }

   public void dropAsItem(Vec3 motion) {
      this.dropBackToItem(motion);
   }

   public void giveToOwnerOrDrop(Entity receiver) {
      if (!this.m_9236_().f_46443_) {
         ItemStack stack = this.getWeaponStack().m_41777_();
         if (stack.m_41619_()) {
            this.m_146870_();
         } else if (receiver instanceof Player player) {
            ItemStack remaining = stack.m_41777_();
            player.m_150109_().m_36054_(remaining);
            if (remaining.m_41619_()) {
               this.m_146870_();
            } else {
               this.dropStack(remaining, receiver.m_20182_(), Vec3.f_82478_);
               this.m_146870_();
            }
         } else {
            this.dropStack(stack, receiver.m_20182_(), Vec3.f_82478_);
            this.m_146870_();
         }
      }
   }

   private void initializeArcPath(Vec3 firstTargetPos) {
      if (!this.arcInitialized) {
         this.arcInitialized = true;
         this.arcStart = this.m_20182_();
         double distance = this.arcStart.m_82554_(firstTargetPos);
         this.arcTravelTicks = Mth.m_14045_((int)Math.round(distance * 4.5), 14, 34);
         this.arcHeight = Mth.m_14008_(0.65 + distance * 0.22, 0.75, 2.25);
         Vec3 direction = firstTargetPos.m_82546_(this.arcStart);
         if (direction.m_165925_() > 1.0E-4) {
            Vec3 flat = new Vec3(direction.f_82479_, 0.0, direction.f_82481_).m_82541_();
            this.arcSide = new Vec3(-flat.f_82481_, 0.0, flat.f_82479_)
               .m_82490_((this.f_19796_.m_188499_() ? 1.0 : -1.0) * Mth.m_14008_(distance * 0.12, 0.15, 0.55));
         } else {
            this.arcSide = Vec3.f_82478_;
         }
      }
   }

   private Vec3 getArcPosition(double progress, Vec3 currentTargetPos) {
      Vec3 start = this.arcStart;
      Vec3 middle = start.m_82549_(currentTargetPos).m_82490_(0.5).m_82520_(0.0, this.arcHeight, 0.0).m_82549_(this.arcSide);
      double inverse = 1.0 - progress;
      return start.m_82490_(inverse * inverse).m_82549_(middle.m_82490_(2.0 * inverse * progress)).m_82549_(currentTargetPos.m_82490_(progress * progress));
   }

   public void m_8119_() {
      super.m_8119_();
      this.f_19794_ = true;
      this.m_20242_(true);
      ItemStack stack = this.getWeaponStack();
      if (stack.m_41619_()) {
         this.m_146870_();
      } else if (this.isDisarmLaunchMode()) {
         this.tickDisarmLaunchMode();
      } else if (this.isHookAttached()) {
         this.tickHookAttached();
      } else {
         if (this.m_19749_() instanceof LivingEntity owner && owner.m_6084_()) {
            Vec3 oldPos = this.m_20182_();
            Vec3 targetPos = this.getTargetHandPosition(owner);
            this.initializeArcPath(targetPos);
            double rawProgress = Mth.m_14008_((double)this.f_19797_ / (double)this.arcTravelTicks, 0.0, 1.0);
            double progress = rawProgress * rawProgress * (3.0 - 2.0 * rawProgress);
            Vec3 newPos = this.getArcPosition(progress, targetPos);
            Vec3 motion = newPos.m_82546_(oldPos);
            this.m_20256_(motion);
            this.m_6034_(newPos.f_82479_, newPos.f_82480_, newPos.f_82481_);
            if (!this.m_9236_().f_46443_) {
               this.damageEntitiesAlongPath(oldPos, newPos, owner);
            }

            this.updateRotationFromMotion(motion);
            this.clearOldHitCooldowns();
            if (rawProgress >= 1.0) {
               if (!this.m_9236_().f_46443_) {
                  this.dropBackToItem();
               }

               return;
            }

            return;
         }

         if (!this.m_9236_().f_46443_) {
            this.dropBackToItem();
         }
      }
   }

   private void tickHookAttached() {
      this.f_19794_ = true;
      this.m_20242_(true);
      this.clearOldHitCooldowns();
      if (!this.m_9236_().f_46443_ && !this.hasActiveHookController()) {
         this.setHookAttached(false);
         if ((Boolean)this.f_19804_.m_135370_(DATA_DISCARD_WHEN_HOOK_LOST)) {
            this.m_146870_();
            return;
         }

         this.dropBackToItem();
      }
   }

   private boolean hasActiveHookController() {
      Entity ownerEntity = this.m_19749_();
      if (ownerEntity instanceof Player player) {
         return FishingRodGrappleUtil.isHookControllingItemProjectile(player.f_36083_, this);
      } else {
         return ownerEntity instanceof FishingHook hook ? FishingRodGrappleUtil.isHookControllingItemProjectile(hook, this) : false;
      }
   }

   private Vec3 getTargetHandPosition(LivingEntity owner) {
      Vec3 jointPos = null;

      try {
         jointPos = EpicfightUtil.getJointWithTranslation(owner, new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolR, 0.0F, 0.0);
      } catch (Exception var4) {
      }

      return jointPos != null ? jointPos : owner.m_146892_().m_82549_(owner.m_20154_().m_82490_(0.45)).m_82492_(0.0, 0.25, 0.0);
   }

   private void damageEntitiesAlongPath(Vec3 from, Vec3 to, Entity owner) {
      if (!this.m_9236_().f_46443_) {
         AABB sweepBox = new AABB(from, to).m_82400_(0.75);

         for (LivingEntity target : this.m_9236_().m_6443_(LivingEntity.class, sweepBox, entity -> this.canDamage(entity, owner))) {
            int nextAllowedHitTick = this.recentHits.getOrDefault(target.m_19879_(), 0);
            if (nextAllowedHitTick <= this.f_19797_ && !target.m_20191_().m_82400_(0.3).m_82371_(from, to).isEmpty()) {
               boolean damaged = this.isHookAttached() ? this.damageEnemyHitByHookedItem(target, owner) : this.damageEnemyHitByThrownItem(target, owner);
               if (damaged) {
                  this.recentHits.put(target.m_19879_(), this.f_19797_ + 8);
               }
            }
         }
      }
   }

   protected boolean damageEnemyHitByHookedItem(LivingEntity target, Entity owner) {
      if (this.tryHandleSpecialBoundItemHit(target, owner)) {
         return true;
      } else {
         DamageSource source = this.m_9236_().m_269111_().m_269390_(this, owner);
         if (!target.m_6469_(source, this.calculateHookAttachedItemDamage(target))) {
            return false;
         } else {
            if (owner instanceof LivingEntity livingOwner) {
               this.applyWeaponEnchantEffects(livingOwner, target);
            }

            this.afterHookAttachedItemHit(target, owner);
            this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.OB_PLACE.get(), 0.5F, 1.0F);
            return true;
         }
      }
   }

   protected float calculateHookAttachedItemDamage(LivingEntity target) {
      ItemStack stack = this.getWeaponStack();
      return stack.m_41720_() instanceof ShieldItem ? 15.0F : this.calculateWeaponDamage(target);
   }

   protected void afterHookAttachedItemHit(LivingEntity target, Entity owner) {
      ItemStack stack = this.getWeaponStack();
      if (stack.m_41720_() instanceof ShieldItem) {
         LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
         if (targetPatch != null && !targetPatch.isStunned()) {
            targetPatch.applyStun(StunType.LONG, 0.0F);
         }
      }
   }

   private boolean damageEnemyHitByThrownItem(LivingEntity target, Entity owner) {
      if (this.tryHandleSpecialBoundItemHit(target, owner)) {
         return true;
      } else {
         DamageSource source = this.m_9236_().m_269111_().m_269390_(this, owner);
         if (!target.m_6469_(source, this.calculateWeaponDamage(target))) {
            return false;
         } else {
            if (owner instanceof LivingEntity livingOwner) {
               this.applyWeaponEnchantEffects(livingOwner, target);
            }

            this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.OB_PLACE.get(), 0.5F, 1.0F);
            return true;
         }
      }
   }

   private boolean tryHandleSpecialBoundItemHit(LivingEntity target, Entity owner) {
      ItemStack mutableStack = this.getWeaponStack().m_41777_();
      LivingEntity livingOwner = owner instanceof LivingEntity ownerLiving ? ownerLiving : null;
      HookUtil.ItemInteractionResult itemResult = HookUtil.handleEntityHitWithResult(this.m_9236_(), mutableStack, this, livingOwner, target);
      if (!itemResult.handled()) {
         return false;
      } else {
         this.setWeaponStack(itemResult.itemStack());
         if (itemResult.itemStack().m_41619_()) {
            this.m_146870_();
         }

         return true;
      }
   }

   private boolean canDamage(LivingEntity target, Entity owner) {
      if (!target.m_6084_()) {
         return false;
      } else if (target.m_5833_()) {
         return false;
      } else if (target != owner && !target.m_20148_().equals(owner.m_20148_())) {
         if (owner instanceof LivingEntity livingOwner && target.m_7307_(livingOwner)) {
            return false;
         }

         return true;
      } else {
         return false;
      }
   }

   private float calculateWeaponDamage(LivingEntity target) {
      ItemStack stack = this.getWeaponStack();
      double damage = 1.0;
      Multimap<Attribute, AttributeModifier> modifiers = stack.m_41638_(EquipmentSlot.MAINHAND);

      for (AttributeModifier modifier : modifiers.get(Attributes.f_22281_)) {
         if (modifier.m_22217_() == Operation.ADDITION) {
            damage += modifier.m_22218_();
         } else if (modifier.m_22217_() == Operation.MULTIPLY_BASE) {
            damage += damage * modifier.m_22218_();
         } else if (modifier.m_22217_() == Operation.MULTIPLY_TOTAL) {
            damage *= 1.0 + modifier.m_22218_();
         }
      }

      damage += (double)EnchantmentHelper.m_44833_(stack, target.m_6336_());
      return (float)Math.max(1.0, damage);
   }

   private void applyWeaponEnchantEffects(LivingEntity owner, LivingEntity target) {
      ItemStack stack = this.getWeaponStack();
      int fireAspect = EnchantmentHelper.m_44843_(Enchantments.f_44981_, stack);
      if (fireAspect > 0) {
         target.m_20254_(fireAspect * 4);
      }

      EnchantmentHelper.m_44823_(target, owner);
      EnchantmentHelper.m_44896_(owner, target);
   }

   private void dropBackToItem() {
      this.dropBackToItem(new Vec3(0.0, -0.05, 0.0));
   }

   private void dropBackToItem(Vec3 motion) {
      if (!this.m_9236_().f_46443_) {
         ItemStack stack = this.getWeaponStack().m_41777_();
         if (!stack.m_41619_()) {
            this.dropStack(stack, this.m_20182_(), motion);
         }
      }

      this.m_146870_();
   }

   private void dropStack(ItemStack stack, Vec3 position, Vec3 motion) {
      ItemEntity itemEntity = new ItemEntity(this.m_9236_(), position.f_82479_, position.f_82480_, position.f_82481_, stack);
      itemEntity.m_32010_(20);
      itemEntity.m_20256_(motion);
      this.m_9236_().m_7967_(itemEntity);
   }

   private void updateRotationFromMotion(Vec3 motion) {
      double horizontal = motion.m_165924_();
      if (horizontal > 1.0E-7) {
         this.m_146922_((float)(Mth.m_14136_(motion.f_82479_, motion.f_82481_) * 180.0F / (float)Math.PI));
         this.m_146926_((float)(Mth.m_14136_(motion.f_82480_, horizontal) * 180.0F / (float)Math.PI));
         this.f_19859_ = this.m_146908_();
         this.f_19860_ = this.m_146909_();
      }
   }

   private void clearOldHitCooldowns() {
      if (!this.recentHits.isEmpty()) {
         this.recentHits.entrySet().removeIf(entry -> entry.getValue() <= this.f_19797_);
      }
   }

   protected void m_7380_(@NotNull CompoundTag tag) {
      super.m_7380_(tag);
      tag.m_128365_("WeaponStack", this.getWeaponStack().m_41739_(new CompoundTag()));
      tag.m_128379_("DisarmLaunchMode", (Boolean)this.f_19804_.m_135370_(DATA_DISARM_LAUNCH_MODE));
      tag.m_128405_("DisarmDropAfterTicks", (Integer)this.f_19804_.m_135370_(DATA_DISARM_DROP_AFTER_TICKS));
      tag.m_128350_("DisarmMotionX", (Float)this.f_19804_.m_135370_(DATA_DISARM_MOTION_X));
      tag.m_128350_("DisarmMotionY", (Float)this.f_19804_.m_135370_(DATA_DISARM_MOTION_Y));
      tag.m_128350_("DisarmMotionZ", (Float)this.f_19804_.m_135370_(DATA_DISARM_MOTION_Z));
      tag.m_128379_("HookAttached", (Boolean)this.f_19804_.m_135370_(DATA_HOOK_ATTACHED));
      tag.m_128379_("DiscardWhenHookLost", (Boolean)this.f_19804_.m_135370_(DATA_DISCARD_WHEN_HOOK_LOST));
   }

   protected void m_7378_(@NotNull CompoundTag tag) {
      super.m_7378_(tag);
      if (tag.m_128425_("WeaponStack", 10)) {
         this.setWeaponStack(ItemStack.m_41712_(tag.m_128469_("WeaponStack")));
      }

      this.f_19804_.m_135381_(DATA_DISARM_LAUNCH_MODE, tag.m_128471_("DisarmLaunchMode"));
      if (tag.m_128441_("DisarmDropAfterTicks")) {
         this.f_19804_.m_135381_(DATA_DISARM_DROP_AFTER_TICKS, tag.m_128451_("DisarmDropAfterTicks"));
      }

      if (tag.m_128441_("DisarmMotionX")) {
         this.f_19804_.m_135381_(DATA_DISARM_MOTION_X, tag.m_128457_("DisarmMotionX"));
         this.f_19804_.m_135381_(DATA_DISARM_MOTION_Y, tag.m_128457_("DisarmMotionY"));
         this.f_19804_.m_135381_(DATA_DISARM_MOTION_Z, tag.m_128457_("DisarmMotionZ"));
         this.m_20334_((double)tag.m_128457_("DisarmMotionX"), (double)tag.m_128457_("DisarmMotionY"), (double)tag.m_128457_("DisarmMotionZ"));
      }

      this.f_19804_.m_135381_(DATA_HOOK_ATTACHED, tag.m_128471_("HookAttached"));
      this.f_19804_.m_135381_(DATA_DISCARD_WHEN_HOOK_LOST, tag.m_128471_("DiscardWhenHookLost"));
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }
}
