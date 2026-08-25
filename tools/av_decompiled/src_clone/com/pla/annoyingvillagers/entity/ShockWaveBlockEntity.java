package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class ShockWaveBlockEntity extends Entity {
   private static final int HARD_DESPAWN_TICKS = 300;
   private static final double MAX_VISIBLE_ABOVE_GROUND = 0.3333333333333333;
   private static final double START_BELOW_SURFACE_EPS = 0.02;
   private static final double GRAVITY = 0.04;
   private static final double DRAG = 0.98;
   private static final double TARGET_RISE = 0.35333333333333333;
   private static final double INITIAL_UPWARD_VELOCITY = Math.sqrt(0.028266666666666666);
   private static final double HITBOX_INFLATE = 0.05;
   private static final float DAMAGE = 2.0F;
   private static final double KNOCKBACK = 0.6;
   private static final double KNOCKUP = 0.15;
   @Nullable
   private UUID ownerUuid;
   private final Set<UUID> hitOnce = new HashSet<>();
   private static final EntityDataAccessor<BlockPos> SOURCE_BLOCK_POS = SynchedEntityData.m_135353_(ShockWaveBlockEntity.class, EntityDataSerializers.f_135038_);
   private static final EntityDataAccessor<BlockState> RENDER_BLOCK_STATE = SynchedEntityData.m_135353_(
      ShockWaveBlockEntity.class, EntityDataSerializers.f_135034_
   );
   private int lifetimeTicks = 10;

   public ShockWaveBlockEntity(EntityType<? extends ShockWaveBlockEntity> entityType, Level level) {
      super(entityType, level);
      this.f_19794_ = true;
   }

   public ShockWaveBlockEntity(Level level, double x, double surfaceY, double z, BlockState blockState, int lifetimeTicks) {
      this((EntityType<? extends ShockWaveBlockEntity>)AnnoyingVillagersModEntities.SHOCKWAVE_BLOCK.get(), level);
      this.setBlockState(blockState);
      this.lifetimeTicks = Math.max(1, lifetimeTicks);
      BlockPos sourcePos = BlockPos.m_274561_(x, surfaceY - 1.0, z);
      this.setSourceBlockPos(sourcePos);
      double startBottomY = (double)sourcePos.m_123342_() + 1.0 - 1.02;
      this.m_6034_(x, startBottomY, z);
      this.m_20334_(0.0, INITIAL_UPWARD_VELOCITY, 0.0);
      this.f_19854_ = x;
      this.f_19855_ = startBottomY;
      this.f_19856_ = z;
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(SOURCE_BLOCK_POS, BlockPos.f_121853_);
      this.f_19804_.m_135372_(RENDER_BLOCK_STATE, Blocks.f_50016_.m_49966_());
   }

   public BlockPos getSourceBlockPos() {
      return (BlockPos)this.f_19804_.m_135370_(SOURCE_BLOCK_POS);
   }

   public void setSourceBlockPos(BlockPos pos) {
      this.f_19804_.m_135381_(SOURCE_BLOCK_POS, pos);
   }

   public BlockState getBlockState() {
      return (BlockState)this.f_19804_.m_135370_(RENDER_BLOCK_STATE);
   }

   public void setBlockState(BlockState blockState) {
      this.f_19804_.m_135381_(RENDER_BLOCK_STATE, blockState);
   }

   public void setOwnerUuid(@Nullable UUID ownerUuid) {
      this.ownerUuid = ownerUuid;
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.f_19797_ <= this.lifetimeTicks && this.f_19797_ <= 300) {
         if (!this.m_20068_()) {
            Vec3 motion = this.m_20184_();
            this.m_20334_(motion.f_82479_, motion.f_82480_ - 0.04, motion.f_82481_);
         }

         this.m_6478_(MoverType.SELF, this.m_20184_());
         this.m_20256_(this.m_20184_().m_82490_(0.98));
         double maxBottomY = (double)this.getSourceBlockPos().m_123342_() + 0.3333333333333333;
         if (this.m_20186_() > maxBottomY) {
            this.m_6034_(this.m_20185_(), maxBottomY, this.m_20189_());
            Vec3 motion = this.m_20184_();
            if (motion.f_82480_ > 0.0) {
               this.m_20334_(motion.f_82479_, 0.0, motion.f_82481_);
            }
         }

         if (!this.m_9236_().f_46443_) {
            this.handleEntityHits();
         }
      } else {
         this.m_146870_();
      }
   }

   private void handleEntityHits() {
      AABB hitBox = this.m_20191_().m_82377_(0.05, 0.0, 0.05);

      for (LivingEntity target : this.m_9236_().m_6443_(LivingEntity.class, hitBox, this::canHitTarget)) {
         if (this.hitOnce.add(target.m_20148_())) {
            this.onHitLivingEntity(target);
         }
      }
   }

   private boolean canHitTarget(LivingEntity target) {
      if (!target.m_6084_()) {
         return false;
      } else {
         return target.m_5833_() ? false : this.ownerUuid == null || !this.ownerUuid.equals(target.m_20148_());
      }
   }

   @Nullable
   private Entity getOwnerEntity() {
      if (this.ownerUuid == null) {
         return null;
      } else {
         return this.m_9236_() instanceof ServerLevel serverLevel ? serverLevel.m_8791_(this.ownerUuid) : null;
      }
   }

   private DamageSource getShockwaveDamageSource() {
      Entity owner = this.getOwnerEntity();
      if (owner instanceof Player player) {
         return this.m_9236_().m_269111_().m_269075_(player);
      } else {
         return owner instanceof LivingEntity living ? this.m_9236_().m_269111_().m_269333_(living) : this.m_9236_().m_269111_().m_269264_();
      }
   }

   private void onHitLivingEntity(LivingEntity target) {
      DamageSource source = this.getShockwaveDamageSource();
      target.m_6469_(source, 2.0F);
      LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
      if (targetPatch != null) {
         AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(targetPatch.getAnimator().getPlayerFor(null)).getRealAnimation();
         if (dynamicAnimation != null && !EpicfightUtil.isLongHitAnimation(dynamicAnimation, targetPatch)) {
            targetPatch.playAnimationSynchronized(AnimsPugilistSteve.TRIED, 0.0F);
         }
      }

      Vec3 dir = target.m_20182_().m_82546_(this.m_20182_());
      Vec3 horizontal = new Vec3(dir.f_82479_, 0.0, dir.f_82481_);
      if (horizontal.m_82556_() < 1.0E-6) {
         horizontal = new Vec3(this.f_19796_.m_188583_(), 0.0, this.f_19796_.m_188583_());
      }

      Vec3 push = horizontal.m_82541_().m_82490_(0.6);
      target.m_5997_(push.f_82479_, 0.15, push.f_82481_);
      target.f_19864_ = true;
   }

   protected void m_7380_(CompoundTag tag) {
      tag.m_128365_("BlockState", NbtUtils.m_129202_(this.getBlockState()));
      tag.m_128365_("SourceBlockPos", NbtUtils.m_129224_(this.getSourceBlockPos()));
      tag.m_128405_("LifetimeTicks", this.lifetimeTicks);
      if (this.ownerUuid != null) {
         tag.m_128362_("Owner", this.ownerUuid);
      }
   }

   protected void m_7378_(CompoundTag tag) {
      this.setBlockState(NbtUtils.m_247651_(this.m_9236_().m_246945_(Registries.f_256747_), tag.m_128469_("BlockState")));
      this.setSourceBlockPos(NbtUtils.m_129239_(tag.m_128469_("SourceBlockPos")));
      this.lifetimeTicks = Math.max(1, tag.m_128451_("LifetimeTicks"));
      this.ownerUuid = tag.m_128403_("Owner") ? tag.m_128342_("Owner") : null;
   }

   public boolean m_6051_() {
      return false;
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }
}
