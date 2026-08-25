package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.item.HookGunItem;
import com.pla.annoyingvillagers.util.HookUtil;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import org.jetbrains.annotations.NotNull;

public class HookGunHookEntity extends Projectile implements ItemSupplier {
   private static final EntityDataAccessor<Integer> DATA_OWNER_ID = SynchedEntityData.m_135353_(HookGunHookEntity.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Boolean> DATA_ATTACHED = SynchedEntityData.m_135353_(HookGunHookEntity.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> DATA_DOUBLE_MODE = SynchedEntityData.m_135353_(HookGunHookEntity.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> DATA_RIGHT_HAND = SynchedEntityData.m_135353_(HookGunHookEntity.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> DATA_RETURNING = SynchedEntityData.m_135353_(HookGunHookEntity.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<ItemStack> DATA_BOUND_STACK = SynchedEntityData.m_135353_(HookGunHookEntity.class, EntityDataSerializers.f_135033_);
   private static final EntityDataAccessor<Float> DATA_ANCHOR_X = SynchedEntityData.m_135353_(HookGunHookEntity.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Float> DATA_ANCHOR_Y = SynchedEntityData.m_135353_(HookGunHookEntity.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Float> DATA_ANCHOR_Z = SynchedEntityData.m_135353_(HookGunHookEntity.class, EntityDataSerializers.f_135029_);
   private static final String TAG_OWNER = "HookGunOwner";
   private static final String TAG_ATTACHED = "Attached";
   private static final String TAG_DOUBLE_MODE = "DoubleMode";
   private static final String TAG_RIGHT_HAND = "RightHand";
   private static final String TAG_RETURNING = "Returning";
   private static final String TAG_BOUND_STACK = "BoundStack";
   private static final String TAG_ANCHOR_X = "AnchorX";
   private static final String TAG_ANCHOR_Y = "AnchorY";
   private static final String TAG_ANCHOR_Z = "AnchorZ";
   private static final double HOOK_GRAVITY = 0.02;
   private static final double AIR_DRAG = 0.99;
   private static final double ENTITY_YANK_SCALE = 0.4;
   private static final double RETURN_SPEED = 2.4;
   private static final double RETURN_ARRIVE_DISTANCE = 0.55;
   private static final int MAX_FLYING_LIFE = 80;
   private static final int MAX_GRAPPLE_FLYING_LIFE = 60;
   private static final int GRAPPLE_ATTACHED_RETURN_MIN_TICKS = 30;
   private static final int GRAPPLE_ATTACHED_RETURN_RANDOM_TICKS = 20;
   private static final String TAG_GRAPPLE_ATTACHED_AT = "GrappleAttachedAt";
   private static final String TAG_GRAPPLE_RETURN_DELAY = "GrappleReturnDelay";
   private Vec3 anchor = Vec3.f_82478_;
   @Nullable
   private UUID ownerUuid;
   private long grappleAttachedAt = -1L;
   private int grappleReturnDelayTicks = -1;

   public HookGunHookEntity(SpawnEntity packet, Level level) {
      this((EntityType<? extends HookGunHookEntity>)AnnoyingVillagersModEntities.HOOK_GUN_HOOK.get(), level);
   }

   public HookGunHookEntity(EntityType<? extends HookGunHookEntity> entityType, Level level) {
      super(entityType, level);
   }

   public HookGunHookEntity(Level level, LivingEntity owner, boolean doubleMode) {
      this(level, owner, doubleMode, true);
   }

   public HookGunHookEntity(Level level, LivingEntity owner, boolean doubleMode, boolean rightHand) {
      this(level, owner, doubleMode, rightHand, ItemStack.f_41583_);
   }

   public HookGunHookEntity(Level level, LivingEntity owner, boolean doubleMode, boolean rightHand, ItemStack boundStack) {
      this((EntityType<? extends HookGunHookEntity>)AnnoyingVillagersModEntities.HOOK_GUN_HOOK.get(), level);
      this.m_5602_(owner);
      this.ownerUuid = owner.m_20148_();
      this.setOwnerId(owner.m_19879_());
      this.setDoubleMode(doubleMode);
      this.setRightHand(rightHand);
      this.setBoundItem(boundStack);
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(DATA_OWNER_ID, 0);
      this.f_19804_.m_135372_(DATA_ATTACHED, false);
      this.f_19804_.m_135372_(DATA_DOUBLE_MODE, false);
      this.f_19804_.m_135372_(DATA_RIGHT_HAND, true);
      this.f_19804_.m_135372_(DATA_RETURNING, false);
      this.f_19804_.m_135372_(DATA_BOUND_STACK, ItemStack.f_41583_);
      this.f_19804_.m_135372_(DATA_ANCHOR_X, 0.0F);
      this.f_19804_.m_135372_(DATA_ANCHOR_Y, 0.0F);
      this.f_19804_.m_135372_(DATA_ANCHOR_Z, 0.0F);
   }

   public void m_8119_() {
      super.m_8119_();
      if (!this.m_9236_().f_46443_) {
         LivingEntity owner = this.getHookOwner();
         if (owner == null || !owner.m_6084_() || !HookGunItem.isHoldingHookGun(owner)) {
            this.clearOwnerVisualHookOut(owner);
            this.m_146870_();
            return;
         }

         this.setOwnerId(owner.m_19879_());
         if (!this.isReturning() && this.m_20280_(owner) > 1764.0) {
            this.clearOwnerVisualHookOut(owner);
            this.m_146870_();
            return;
         }
      }

      LivingEntity ownerx = this.getHookOwner();
      if (this.isReturning()) {
         if (ownerx == null) {
            this.m_146870_();
         } else {
            this.tickReturning(ownerx);
         }
      } else if (this.isAttached()) {
         this.anchor = this.getSyncedAnchor();
         this.m_20256_(Vec3.f_82478_);
         this.m_20242_(true);
         this.f_19794_ = true;
         this.m_6034_(this.anchor.f_82479_, this.anchor.f_82480_, this.anchor.f_82481_);
         if (!this.m_9236_().f_46443_ && this.isGrappleHook() && this.shouldReturnAttachedGrapple()) {
            this.startReturning();
         }
      } else if (!this.m_9236_().f_46443_ && this.f_19797_ > this.getMaxFlyingLife()) {
         this.startReturning();
      } else {
         BlockHitResult emptyBucketFluidHit = this.getEmptyBucketFluidHit();
         if (emptyBucketFluidHit != null && !ForgeEventFactory.onProjectileImpact(this, emptyBucketFluidHit)) {
            this.m_6532_(emptyBucketFluidHit);
         }

         if (!this.m_213877_() && !this.isAttached() && !this.isReturning()) {
            BlockHitResult boneMealSaplingHit = this.getBoneMealSaplingOutlineHit();
            if (boneMealSaplingHit != null && !ForgeEventFactory.onProjectileImpact(this, boneMealSaplingHit)) {
               this.m_6532_(boneMealSaplingHit);
            }

            if (!this.m_213877_() && !this.isAttached() && !this.isReturning()) {
               HitResult hitResult = ProjectileUtil.m_278158_(this, this::m_5603_);
               if (hitResult.m_6662_() != Type.MISS && !ForgeEventFactory.onProjectileImpact(this, hitResult)) {
                  this.m_6532_(hitResult);
               }

               if (!this.m_213877_() && !this.isAttached() && !this.isReturning()) {
                  Vec3 motion = this.m_20184_();
                  this.m_6478_(MoverType.SELF, motion);
                  this.updateRotationFromMotion(motion);
                  this.m_20256_(motion.m_82490_(0.99).m_82520_(0.0, -0.02, 0.0));
               }
            }
         }
      }
   }

   @Nullable
   private BlockHitResult getEmptyBucketFluidHit() {
      if (!this.getBoundItem().m_150930_(Items.f_42446_)) {
         return null;
      } else {
         Vec3 start = this.m_20182_();
         Vec3 end = start.m_82549_(this.m_20184_());
         HitResult hitResult = this.m_9236_().m_45547_(new ClipContext(start, end, Block.COLLIDER, Fluid.SOURCE_ONLY, this));
         if (hitResult instanceof BlockHitResult blockHitResult && this.isSourceFluid(blockHitResult.m_82425_())) {
            return blockHitResult;
         }

         BlockPos currentPos = BlockPos.m_274446_(start);
         if (this.isSourceFluid(currentPos)) {
            return new BlockHitResult(start, Direction.UP, currentPos, false);
         } else {
            BlockPos nextPos = BlockPos.m_274446_(end);
            return this.isSourceFluid(nextPos) ? new BlockHitResult(end, Direction.UP, nextPos, false) : null;
         }
      }
   }

   @Nullable
   private BlockHitResult getBoneMealSaplingOutlineHit() {
      if (!(this.getBoundItem().m_41720_() instanceof BoneMealItem)) {
         return null;
      } else {
         Vec3 start = this.m_20182_();
         Vec3 end = start.m_82549_(this.m_20184_());
         HitResult hitResult = this.m_9236_().m_45547_(new ClipContext(start, end, Block.OUTLINE, Fluid.NONE, this));
         if (hitResult instanceof BlockHitResult blockHitResult
            && hitResult.m_6662_() != Type.MISS
            && this.m_9236_().m_8055_(blockHitResult.m_82425_()).m_204336_(BlockTags.f_13104_)) {
            return blockHitResult;
         }

         return null;
      }
   }

   private boolean isSourceFluid(BlockPos pos) {
      return !this.m_9236_().m_6425_(pos).m_76178_() && this.m_9236_().m_6425_(pos).m_76170_();
   }

   protected void m_6532_(@NotNull HitResult hitResult) {
      if (!this.m_9236_().f_46443_ && !this.isAttached()) {
         if (hitResult instanceof BlockHitResult blockHitResult) {
            this.handleBlockHit(blockHitResult);
         } else if (hitResult instanceof EntityHitResult entityHitResult) {
            this.handleEntityHit(entityHitResult);
         }
      }
   }

   private void handleBlockHit(BlockHitResult hitResult) {
      ItemStack boundItem = this.getBoundItem();
      this.m_146884_(hitResult.m_82450_());
      if (this.isGrappleHook()) {
         this.attachToBlock(hitResult);
      } else {
         ItemStack mutableBoundItem = boundItem.m_41777_();
         HookUtil.ItemInteractionResult itemResult = HookUtil.handleBlockHitWithResult(this.m_9236_(), mutableBoundItem, this, this.getHookOwner(), hitResult);
         if (itemResult.handled()) {
            this.updateSourceBoundItem(itemResult.itemStack());
         }

         this.startReturning();
      }
   }

   private void handleEntityHit(EntityHitResult hitResult) {
      Entity target = hitResult.m_82443_();
      ItemStack boundItem = this.getBoundItem();
      this.m_146884_(hitResult.m_82450_());
      if (this.isGrappleHook()) {
         this.yankEntity(hitResult);
      } else if (boundItem.m_41619_()) {
         this.startReturning();
      } else {
         if (target instanceof LivingEntity livingTarget) {
            ItemStack mutableBoundItem = boundItem.m_41777_();
            HookUtil.ItemInteractionResult itemResult = HookUtil.handleEntityHitWithResult(
               this.m_9236_(), mutableBoundItem, this, this.getHookOwner(), livingTarget
            );
            if (itemResult.handled()) {
               this.updateSourceBoundItem(itemResult.itemStack());
            }
         }

         this.startReturning();
      }
   }

   private void attachToBlock(BlockHitResult hitResult) {
      BlockState blockState = this.m_9236_().m_8055_(hitResult.m_82425_());
      if (!blockState.m_60795_() && !blockState.m_60812_(this.m_9236_(), hitResult.m_82425_()).m_83281_()) {
         this.setAnchor(hitResult.m_82450_());
         this.f_19804_.m_135381_(DATA_ATTACHED, true);
         this.startAttachedGrappleTimer();
         this.m_20256_(Vec3.f_82478_);
         this.m_20242_(true);
         this.f_19794_ = true;
         this.m_6034_(this.anchor.f_82479_, this.anchor.f_82480_, this.anchor.f_82481_);
      }
   }

   private void yankEntity(EntityHitResult hitResult) {
      Entity target = hitResult.m_82443_();
      LivingEntity owner = this.getHookOwner();
      if (owner != null && target != owner) {
         Vec3 pull = owner.m_20182_()
            .m_82520_(0.0, (double)owner.m_20192_(), 0.0)
            .m_82546_(target.m_20182_().m_82520_(0.0, (double)target.m_20206_() * 0.5, 0.0))
            .m_82490_(0.4);
         pull = new Vec3(pull.f_82479_, Math.min(pull.f_82480_, 1.2), pull.f_82481_);
         target.m_20256_(target.m_20184_().m_82549_(pull));
         target.f_19812_ = true;
         target.f_19864_ = true;
         target.f_19789_ = 0.0F;
         this.startReturning();
      }
   }

   protected boolean m_5603_(@NotNull Entity target) {
      if (this.isReturning()) {
         return false;
      } else {
         LivingEntity owner = this.getHookOwner();
         return target != owner && target.m_6084_() && !target.m_5833_() ? super.m_5603_(target) : false;
      }
   }

   public boolean isAttached() {
      return (Boolean)this.f_19804_.m_135370_(DATA_ATTACHED);
   }

   public boolean isDoubleMode() {
      return (Boolean)this.f_19804_.m_135370_(DATA_DOUBLE_MODE);
   }

   public void setDoubleMode(boolean doubleMode) {
      this.f_19804_.m_135381_(DATA_DOUBLE_MODE, doubleMode);
   }

   public boolean isRightHand() {
      return (Boolean)this.f_19804_.m_135370_(DATA_RIGHT_HAND);
   }

   public boolean isReturning() {
      return (Boolean)this.f_19804_.m_135370_(DATA_RETURNING);
   }

   public void setRightHand(boolean rightHand) {
      this.f_19804_.m_135381_(DATA_RIGHT_HAND, rightHand);
   }

   public ItemStack getBoundItem() {
      return (ItemStack)this.f_19804_.m_135370_(DATA_BOUND_STACK);
   }

   public void setBoundItem(ItemStack boundStack) {
      if (boundStack.m_41619_()) {
         this.f_19804_.m_135381_(DATA_BOUND_STACK, ItemStack.f_41583_);
      } else {
         ItemStack stored = boundStack.m_41777_();
         stored.m_41764_(1);
         this.f_19804_.m_135381_(DATA_BOUND_STACK, stored);
      }
   }

   public boolean isGrappleHook() {
      return HookUtil.isPickaxe(this.getBoundItem());
   }

   public void returnToOwner() {
      this.startReturning();
   }

   private void startReturning() {
      boolean wasReturning = this.isReturning();
      this.f_19804_.m_135381_(DATA_ATTACHED, false);
      this.f_19804_.m_135381_(DATA_RETURNING, true);
      this.grappleAttachedAt = -1L;
      this.grappleReturnDelayTicks = -1;
      this.f_19794_ = true;
      this.m_20242_(true);
      this.m_20256_(Vec3.f_82478_);
      if (!wasReturning) {
         LivingEntity owner = this.getHookOwner();
         if (owner != null) {
            HookGunItem.cancelHookHandAnimation(owner, this.isRightHand());
         }
      }
   }

   private int getMaxFlyingLife() {
      return this.isGrappleHook() ? 60 : 80;
   }

   private boolean shouldReturnAttachedGrapple() {
      if (this.grappleAttachedAt < 0L || this.grappleReturnDelayTicks < 0) {
         this.startAttachedGrappleTimer();
      }

      return this.m_9236_().m_46467_() - this.grappleAttachedAt >= (long)this.grappleReturnDelayTicks;
   }

   private void startAttachedGrappleTimer() {
      this.grappleAttachedAt = this.m_9236_().m_46467_();
      this.grappleReturnDelayTicks = 30 + this.f_19796_.m_188503_(21);
   }

   private void tickReturning(LivingEntity owner) {
      Vec3 target = HookGunItem.getHookStartPosition(owner, this.isRightHand());
      Vec3 current = this.m_20182_();
      Vec3 toTarget = target.m_82546_(current);
      double distance = toTarget.m_82553_();
      this.f_19794_ = true;
      this.m_20242_(true);
      this.f_19789_ = 0.0F;
      if (distance <= 0.55) {
         this.m_20256_(Vec3.f_82478_);
         this.m_6034_(target.f_82479_, target.f_82480_, target.f_82481_);
         if (!this.m_9236_().f_46443_) {
            this.clearOwnerVisualHookOut(owner);
            this.m_146870_();
         }
      } else {
         Vec3 step = toTarget.m_82490_(Math.min(2.4, distance) / distance);
         this.m_20256_(step);
         this.m_6034_(current.f_82479_ + step.f_82479_, current.f_82480_ + step.f_82480_, current.f_82481_ + step.f_82481_);
         this.updateRotationFromMotion(step);
         this.f_19812_ = true;
      }
   }

   public Vec3 getAnchor() {
      return this.isAttached() ? this.getSyncedAnchor() : this.m_20182_();
   }

   private void setAnchor(Vec3 anchor) {
      this.anchor = anchor;
      this.f_19804_.m_135381_(DATA_ANCHOR_X, (float)anchor.f_82479_);
      this.f_19804_.m_135381_(DATA_ANCHOR_Y, (float)anchor.f_82480_);
      this.f_19804_.m_135381_(DATA_ANCHOR_Z, (float)anchor.f_82481_);
   }

   private Vec3 getSyncedAnchor() {
      return new Vec3(
         (double)((Float)this.f_19804_.m_135370_(DATA_ANCHOR_X)).floatValue(),
         (double)((Float)this.f_19804_.m_135370_(DATA_ANCHOR_Y)).floatValue(),
         (double)((Float)this.f_19804_.m_135370_(DATA_ANCHOR_Z)).floatValue()
      );
   }

   public boolean isOwnedBy(LivingEntity owner) {
      return owner != null
         && (owner.equals(this.m_19749_()) || owner.m_19879_() == (Integer)this.f_19804_.m_135370_(DATA_OWNER_ID) || owner.m_20148_().equals(this.ownerUuid));
   }

   private void setOwnerId(int ownerId) {
      this.f_19804_.m_135381_(DATA_OWNER_ID, ownerId);
   }

   @Nullable
   public LivingEntity getHookOwner() {
      Entity owner = this.m_19749_();
      if (owner instanceof LivingEntity) {
         return (LivingEntity)owner;
      } else {
         int ownerId = (Integer)this.f_19804_.m_135370_(DATA_OWNER_ID);
         if (ownerId > 0 && this.m_9236_().m_6815_(ownerId) instanceof LivingEntity livingOwner) {
            this.m_5602_(livingOwner);
            return livingOwner;
         } else if (this.ownerUuid != null
            && this.m_9236_() instanceof ServerLevel serverLevel
            && serverLevel.m_8791_(this.ownerUuid) instanceof LivingEntity livingOwner) {
            this.m_5602_(livingOwner);
            this.setOwnerId(livingOwner.m_19879_());
            return livingOwner;
         } else {
            return null;
         }
      }
   }

   private void updateRotationFromMotion(Vec3 motion) {
      if (!(motion.m_82556_() <= 1.0E-7)) {
         double horizontal = Math.sqrt(motion.f_82479_ * motion.f_82479_ + motion.f_82481_ * motion.f_82481_);
         this.m_146922_((float)(Mth.m_14136_(motion.f_82479_, motion.f_82481_) * 180.0F / (float)Math.PI));
         this.m_146926_((float)(Mth.m_14136_(motion.f_82480_, horizontal) * 180.0F / (float)Math.PI));
         this.f_19859_ = this.m_146908_();
         this.f_19860_ = this.m_146909_();
      }
   }

   protected void m_7380_(@NotNull CompoundTag tag) {
      super.m_7380_(tag);
      if (this.ownerUuid != null) {
         tag.m_128362_("HookGunOwner", this.ownerUuid);
      }

      tag.m_128379_("Attached", this.isAttached());
      tag.m_128379_("DoubleMode", this.isDoubleMode());
      tag.m_128379_("RightHand", this.isRightHand());
      tag.m_128379_("Returning", this.isReturning());
      ItemStack boundStack = this.getBoundItem();
      if (!boundStack.m_41619_()) {
         tag.m_128365_("BoundStack", boundStack.m_41739_(new CompoundTag()));
      }

      tag.m_128347_("AnchorX", this.anchor.f_82479_);
      tag.m_128347_("AnchorY", this.anchor.f_82480_);
      tag.m_128347_("AnchorZ", this.anchor.f_82481_);
      tag.m_128356_("GrappleAttachedAt", this.grappleAttachedAt);
      tag.m_128405_("GrappleReturnDelay", this.grappleReturnDelayTicks);
   }

   protected void m_7378_(@NotNull CompoundTag tag) {
      super.m_7378_(tag);
      if (tag.m_128403_("HookGunOwner")) {
         this.ownerUuid = tag.m_128342_("HookGunOwner");
      }

      this.f_19804_.m_135381_(DATA_ATTACHED, tag.m_128471_("Attached"));
      this.f_19804_.m_135381_(DATA_DOUBLE_MODE, tag.m_128471_("DoubleMode"));
      this.f_19804_.m_135381_(DATA_RIGHT_HAND, !tag.m_128441_("RightHand") || tag.m_128471_("RightHand"));
      this.f_19804_.m_135381_(DATA_RETURNING, tag.m_128471_("Returning"));
      if (tag.m_128425_("BoundStack", 10)) {
         this.setBoundItem(ItemStack.m_41712_(tag.m_128469_("BoundStack")));
      } else {
         this.setBoundItem(ItemStack.f_41583_);
      }

      this.setAnchor(new Vec3(tag.m_128459_("AnchorX"), tag.m_128459_("AnchorY"), tag.m_128459_("AnchorZ")));
      this.grappleAttachedAt = tag.m_128441_("GrappleAttachedAt") ? tag.m_128454_("GrappleAttachedAt") : -1L;
      this.grappleReturnDelayTicks = tag.m_128441_("GrappleReturnDelay") ? tag.m_128451_("GrappleReturnDelay") : -1;
      this.f_19794_ = this.isAttached() || this.isReturning();
      this.m_20242_(this.isAttached() || this.isReturning());
   }

   @NotNull
   public AABB m_6921_() {
      LivingEntity owner = this.getHookOwner();
      return owner == null ? super.m_6921_() : new AABB(this.m_20182_(), owner.m_146892_()).m_82400_(1.0);
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   @NotNull
   public ItemStack m_7846_() {
      return this.getBoundItem();
   }

   private void updateSourceBoundItem(ItemStack updatedBoundItem) {
      this.setBoundItem(updatedBoundItem);
      ItemStack hookGunStack = this.getOwnerHookGunStack();
      if (!hookGunStack.m_41619_()) {
         HookGunItem.setBoundItem(hookGunStack, updatedBoundItem);
      }
   }

   private ItemStack getOwnerHookGunStack() {
      LivingEntity owner = this.getHookOwner();
      return owner == null ? ItemStack.f_41583_ : HookGunItem.getHookGunStack(owner, this.isRightHand());
   }

   private void clearOwnerVisualHookOut(@Nullable LivingEntity owner) {
      if (owner != null) {
         HookGunItem.setVisualHookOut(HookGunItem.getHookGunStack(owner, this.isRightHand()), false);
      }
   }
}
