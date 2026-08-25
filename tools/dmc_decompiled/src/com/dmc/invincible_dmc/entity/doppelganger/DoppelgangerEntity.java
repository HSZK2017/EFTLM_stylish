package com.dmc.invincible_dmc.entity.doppelganger;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.capability.doppelganger.DoppelgangerBindingService;
import com.dmc.invincible_dmc.capability.doppelganger.DoppelgangerCapability;
import com.dmc.invincible_dmc.client.sound.DMCSounds;
import com.dmc.invincible_dmc.entity.DMCEntities;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.input.PlayerMovementFrame;
import com.dmc.invincible_dmc.item.DMCItems;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class DoppelgangerEntity extends PathfinderMob {
   private static final double MAX_TETHER_DISTANCE = 64.0;
   @Nullable
   private PlayerPatch<?> ownerPatch;
   private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.m_135353_(DoppelgangerEntity.class, EntityDataSerializers.f_135041_);
   private static final EntityDataAccessor<String> OWNER_NAME = SynchedEntityData.m_135353_(DoppelgangerEntity.class, EntityDataSerializers.f_135030_);
   public static final EntityDataAccessor<Float> ANIMATION_SPEED = SynchedEntityData.m_135353_(DoppelgangerEntity.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Boolean> IS_CC_MODE = SynchedEntityData.m_135353_(DoppelgangerEntity.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Integer> CC_NODE_ID = SynchedEntityData.m_135353_(DoppelgangerEntity.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Boolean> IS_TAP_HOLD_ACTIVE = SynchedEntityData.m_135353_(DoppelgangerEntity.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Integer> TAP_HOLD_KEY_INDEX = SynchedEntityData.m_135353_(DoppelgangerEntity.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> TAP_HOLD_WINDUP_TICKS = SynchedEntityData.m_135353_(
      DoppelgangerEntity.class, EntityDataSerializers.f_135028_
   );
   private static final EntityDataAccessor<Integer> DOPPEL_DELAY_MODE = SynchedEntityData.m_135353_(DoppelgangerEntity.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Long> BINDING_GENERATION = SynchedEntityData.m_135353_(DoppelgangerEntity.class, EntityDataSerializers.f_244073_);
   private static final EntityDataAccessor<Float> RENDER_ALPHA = SynchedEntityData.m_135353_(DoppelgangerEntity.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Float> COLOR_TINT_INTENSITY = SynchedEntityData.m_135353_(DoppelgangerEntity.class, EntityDataSerializers.f_135029_);
   private int spawnTick = 0;
   private int spawnCooldownTicks;
   public double jumpYOffset;
   private boolean prevJumpInput;

   public DoppelgangerEntity(EntityType<? extends DoppelgangerEntity> type, Level level) {
      super(type, level);
      this.f_19811_ = true;
      this.setEquipment();
   }

   private void setEquipment() {
      this.ensureFixedWeapon();
      this.m_21409_(EquipmentSlot.MAINHAND, 0.0F);
   }

   public void ensureFixedWeapon() {
      ItemStack currentWeapon = this.m_21205_();
      ItemStack ownerYamato = this.findOwnerYamato();
      boolean ownerNbtAvailable = !ownerYamato.m_41619_();
      boolean nbtMatches = !ownerNbtAvailable || Objects.equals(currentWeapon.m_41783_(), ownerYamato.m_41783_());
      if (!currentWeapon.m_150930_((Item)DMCItems.YAMATO_DMC5.get()) || !nbtMatches) {
         this.m_8061_(EquipmentSlot.MAINHAND, this.createFixedWeaponStack(ownerYamato));
      }
   }

   public void ensureConfiguredWeapon() {
      if (DMConfig.DOPPEL_WEAPON_STRATEGY.get() == DMConfig.DoppelWeaponStrategy.LEGACY_OWNER_COPY) {
         this.syncOwnerMainHandItem();
      } else {
         this.ensureFixedWeapon();
      }
   }

   private void syncOwnerMainHandItem() {
      Player owner = this.getOwner();
      if (owner != null) {
         ItemStack ownerWeapon = owner.m_21205_();
         if (!ItemStack.m_41728_(this.m_21205_(), ownerWeapon)) {
            this.m_8061_(EquipmentSlot.MAINHAND, ownerWeapon.m_41777_());
         }
      }
   }

   public ItemStack createFixedWeaponStack() {
      return this.createFixedWeaponStack(this.findOwnerYamato());
   }

   private ItemStack createFixedWeaponStack(ItemStack ownerYamato) {
      ItemStack fixedWeapon = new ItemStack((ItemLike)DMCItems.YAMATO_DMC5.get());
      if (!ownerYamato.m_41619_() && ownerYamato.m_41782_()) {
         fixedWeapon.m_41751_(ownerYamato.m_41783_().m_6426_());
      }

      return fixedWeapon;
   }

   private ItemStack findOwnerYamato() {
      Player owner = this.getOwner();
      if (owner == null) {
         return ItemStack.f_41583_;
      } else {
         ItemStack mainHand = owner.m_21205_();
         if (isYamato(mainHand)) {
            return mainHand;
         } else {
            ItemStack offHand = owner.m_21206_();
            return isYamato(offHand) ? offHand : ItemStack.f_41583_;
         }
      }
   }

   private static boolean isYamato(ItemStack stack) {
      return stack.m_150930_((Item)DMCItems.YAMATO_DMC4.get())
         || stack.m_150930_((Item)DMCItems.YAMATO_DMC5.get())
         || stack.m_150930_((Item)DMCItems.YAMATO_DMC5_MINI.get())
         || stack.m_150930_((Item)DMCItems.YAMATO_DMC5_BD.get());
   }

   public static void createDoppelganger(ServerLevel serverLevel, Player owner) {
      createDoppelganger(serverLevel, owner, 1);
   }

   public static void createDoppelganger(ServerLevel serverLevel, Player owner, int doppelDelayMode) {
      if (owner instanceof ServerPlayer serverPlayer) {
         DoppelgangerBindingService.spawnImmediate(serverPlayer, doppelDelayMode);
      }
   }

   @Nullable
   public static DoppelgangerEntity spawnBound(ServerLevel serverLevel, ServerPlayer owner, int doppelDelayMode, long generation) {
      PlayerPatch<?> playerPatch = EpicFightCapabilities.getPlayerPatch(owner);
      if (playerPatch != null) {
         playerPatch.playSound((SoundEvent)DMCSounds.DOPPELGANGER_OPEN.get(), 1.0F, 1.0F);
      }

      DoppelgangerEntity doppelganger = new DoppelgangerEntity((EntityType<? extends DoppelgangerEntity>)DMCEntities.DOPPELGANGER.get(), serverLevel);
      DoppelgangerPatch doppelgangerPatch = (DoppelgangerPatch)EpicFightCapabilities.getEntityPatch(doppelganger, DoppelgangerPatch.class);
      float yawRad = owner.m_146908_() * (float) (Math.PI / 180.0);
      Vec3 rightDir = new Vec3(-Math.cos((double)yawRad), 0.0, -Math.sin((double)yawRad)).m_82541_();
      Vec3 spawnPos = owner.m_20182_().m_82549_(rightDir.m_82490_(1.45));
      doppelganger.m_146884_(spawnPos);
      doppelganger.spawnTick = doppelganger.f_19797_;
      doppelganger.spawnCooldownTicks = owner.m_20096_() ? 10 : 6;
      doppelganger.setOwner(owner, generation);
      doppelganger.f_19804_.m_135381_(DOPPEL_DELAY_MODE, Mth.m_14045_(doppelDelayMode, 0, 2));
      doppelganger.m_146922_(owner.m_146908_());
      doppelganger.m_5618_(owner.m_146908_());
      doppelganger.f_20884_ = owner.f_20884_;
      doppelganger.m_146926_(owner.m_146909_());
      doppelganger.f_20885_ = owner.f_20885_;
      doppelganger.f_20886_ = owner.f_20886_;
      doppelganger.setRenderAlpha(0.66F);
      doppelganger.setColorTintIntensity(0.75F);
      if (!serverLevel.m_7967_(doppelganger)) {
         return null;
      } else {
         spawnSummonParticles(serverLevel, doppelganger);
         if (doppelgangerPatch != null) {
            doppelgangerPatch.playAnimationSynchronized(YamatoAnimations.YAMATO_SIN_DEVIL_TRIGGER, 0.0F);
         }

         return doppelganger;
      }
   }

   @Nullable
   public static DoppelgangerEntity findOwnedDoppelganger(ServerLevel serverLevel, UUID ownerUUID) {
      for (Entity entity : serverLevel.m_142646_().m_142273_()) {
         if (entity instanceof DoppelgangerEntity doppel && doppel.m_6084_() && ownerUUID.equals(doppel.getOwnerUUID())) {
            return doppel;
         }
      }

      return null;
   }

   public static void reconcileOwnerState(ServerPlayer owner) {
      DoppelgangerBindingService.reconcile(owner);
   }

   public static void recallDoppelganger(DoppelgangerEntity doppelgangerEntity) {
      DoppelgangerPatch doppelgangerPatch = (DoppelgangerPatch)EpicFightCapabilities.getEntityPatch(doppelgangerEntity, DoppelgangerPatch.class);
      if (doppelgangerPatch != null && doppelgangerEntity.getOwner() instanceof ServerPlayer serverOwner) {
         float yawRad = serverOwner.m_146908_() * (float) (Math.PI / 180.0);
         Vec3 rightDir = new Vec3(-Math.cos((double)yawRad), 0.0, -Math.sin((double)yawRad)).m_82541_();
         Vec3 recallPos = serverOwner.m_20182_().m_82549_(rightDir.m_82490_(1.45));
         doppelgangerEntity.m_6021_(recallPos.f_82479_, recallPos.f_82480_, recallPos.f_82481_);
         doppelgangerEntity.m_20334_(0.0, 0.0, 0.0);
         doppelgangerEntity.m_146922_(serverOwner.m_146908_());
         doppelgangerEntity.f_20883_ = serverOwner.f_20883_;
         doppelgangerEntity.f_20885_ = serverOwner.f_20885_;
         doppelgangerEntity.setCcMode(false);
         doppelgangerEntity.setCcNodeId(0);
         doppelgangerPatch.comboState.clear();
         doppelgangerPatch.clearInputPipeline();
         doppelgangerPatch.abortScript();
         doppelgangerPatch.getAnimator().stopPlaying(YamatoAnimations.YAMATO_IDLE);
         doppelgangerPatch.playAnimationSynchronized(YamatoAnimations.YAMATO_IDLE, 0.0F);
         doppelgangerPatch.getAnimator().stopPlaying(YamatoAnimations.YAMATO_IDLE);
         DoppelgangerPatch.resetAllDoppelgangerCcMode(serverOwner);
      }
   }

   public static void discardDoppelganger(DoppelgangerEntity doppelgangerEntity) {
      if (doppelgangerEntity.m_9236_().f_46443_) {
         discardWithoutBinding(doppelgangerEntity);
      } else {
         DoppelgangerBindingService.discard(doppelgangerEntity);
      }
   }

   public static void discardWithoutBinding(DoppelgangerEntity doppelgangerEntity) {
      if (doppelgangerEntity.m_9236_() instanceof ServerLevel serverLevel) {
         spawnDismissParticles(serverLevel, doppelgangerEntity);
      }

      DoppelgangerPatch patch = (DoppelgangerPatch)EpicFightCapabilities.getEntityPatch(doppelgangerEntity, DoppelgangerPatch.class);
      if (patch != null) {
         patch.comboState.clear();
         patch.clearInputPipeline();
         patch.playSound((SoundEvent)DMCSounds.DOPPELGANGER_CLOSE.get(), 1.0F, 1.0F);
      }

      doppelgangerEntity.m_146870_();
   }

   private static void spawnSummonParticles(ServerLevel level, DoppelgangerEntity doppelganger) {
      double centerY = doppelganger.m_20186_() + (double)doppelganger.m_20206_() * 0.5;
      double horizontalSpread = Math.max(0.35, (double)doppelganger.m_20205_() * 0.65);
      double verticalSpread = Math.max(0.75, (double)doppelganger.m_20206_() * 0.45);
      level.m_8767_(
         ParticleTypes.f_123745_, doppelganger.m_20185_(), centerY, doppelganger.m_20189_(), 24, horizontalSpread, verticalSpread, horizontalSpread, 0.035
      );
      level.m_8767_(
         ParticleTypes.f_123760_, doppelganger.m_20185_(), centerY, doppelganger.m_20189_(), 32, horizontalSpread, verticalSpread, horizontalSpread, 0.18
      );
      level.m_8767_(
         ParticleTypes.f_123810_,
         doppelganger.m_20185_(),
         centerY,
         doppelganger.m_20189_(),
         8,
         horizontalSpread * 0.6,
         verticalSpread * 0.8,
         horizontalSpread * 0.6,
         0.025
      );
   }

   private static void spawnDismissParticles(ServerLevel level, DoppelgangerEntity doppelganger) {
      double centerY = doppelganger.m_20186_() + (double)doppelganger.m_20206_() * 0.5;
      double horizontalSpread = Math.max(0.4, (double)doppelganger.m_20205_() * 0.75);
      double verticalSpread = Math.max(0.85, (double)doppelganger.m_20206_() * 0.55);
      level.m_8767_(
         ParticleTypes.f_235898_, doppelganger.m_20185_(), centerY, doppelganger.m_20189_(), 20, horizontalSpread, verticalSpread, horizontalSpread, 0.045
      );
      level.m_8767_(
         ParticleTypes.f_123760_, doppelganger.m_20185_(), centerY, doppelganger.m_20189_(), 40, horizontalSpread, verticalSpread, horizontalSpread, 0.28
      );
      level.m_8767_(
         ParticleTypes.f_175830_, doppelganger.m_20185_(), centerY, doppelganger.m_20189_(), 16, horizontalSpread, verticalSpread, horizontalSpread, 0.08
      );
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(OWNER_UUID, Optional.empty());
      this.f_19804_.m_135372_(OWNER_NAME, "");
      this.f_19804_.m_135372_(ANIMATION_SPEED, 1.0F);
      this.f_19804_.m_135372_(IS_CC_MODE, false);
      this.f_19804_.m_135372_(CC_NODE_ID, 0);
      this.f_19804_.m_135372_(IS_TAP_HOLD_ACTIVE, false);
      this.f_19804_.m_135372_(TAP_HOLD_KEY_INDEX, -1);
      this.f_19804_.m_135372_(TAP_HOLD_WINDUP_TICKS, 0);
      this.f_19804_.m_135372_(DOPPEL_DELAY_MODE, 0);
      this.f_19804_.m_135372_(BINDING_GENERATION, 0L);
      this.f_19804_.m_135372_(RENDER_ALPHA, 0.66F);
      this.f_19804_.m_135372_(COLOR_TINT_INTENSITY, 0.75F);
   }

   public void setOwner(Player player) {
      this.setOwner(player, 0L);
   }

   public void setOwner(Player player, long generation) {
      this.ownerPatch = null;
      this.f_19804_.m_135381_(OWNER_UUID, Optional.of(player.m_20148_()));
      this.f_19804_.m_135381_(OWNER_NAME, player.m_36316_().getName());
      this.f_19804_.m_135381_(BINDING_GENERATION, Math.max(0L, generation));
   }

   public long getBindingGeneration() {
      return (Long)this.f_19804_.m_135370_(BINDING_GENERATION);
   }

   public void setBindingGeneration(long generation) {
      this.f_19804_.m_135381_(BINDING_GENERATION, Math.max(0L, generation));
   }

   @Nullable
   public UUID getOwnerUUID() {
      return (UUID)((Optional)this.f_19804_.m_135370_(OWNER_UUID)).orElse(null);
   }

   public void setAnimationSpeed(float speed) {
      this.f_19804_.m_135381_(ANIMATION_SPEED, speed);
   }

   public void resetAnimationSpeed() {
      this.f_19804_.m_135381_(ANIMATION_SPEED, 1.0F);
   }

   public String getOwnerName() {
      return (String)this.f_19804_.m_135370_(OWNER_NAME);
   }

   @Nullable
   public Player getOwner() {
      UUID uuid = this.getOwnerUUID();
      if (uuid == null) {
         return null;
      } else {
         Player owner = this.m_9236_().m_46003_(uuid);
         if (owner != null) {
            return owner;
         } else {
            return this.m_9236_() instanceof ServerLevel serverLevel ? serverLevel.m_7654_().m_6846_().m_11259_(uuid) : null;
         }
      }
   }

   public float getAnimationSpeed() {
      return (Float)this.f_19804_.m_135370_(ANIMATION_SPEED);
   }

   public void setCcMode(boolean cc) {
      this.f_19804_.m_135381_(IS_CC_MODE, cc);
   }

   public boolean isCcMode() {
      return (Boolean)this.f_19804_.m_135370_(IS_CC_MODE);
   }

   public void setCcNodeId(int nodeId) {
      this.f_19804_.m_135381_(CC_NODE_ID, nodeId);
   }

   public int getCcNodeId() {
      return (Integer)this.f_19804_.m_135370_(CC_NODE_ID);
   }

   public void setTapHoldActive(boolean active) {
      this.f_19804_.m_135381_(IS_TAP_HOLD_ACTIVE, active);
   }

   public boolean isTapHoldActive() {
      return (Boolean)this.f_19804_.m_135370_(IS_TAP_HOLD_ACTIVE);
   }

   public void setTapHoldKeyIndex(int keyIndex) {
      this.f_19804_.m_135381_(TAP_HOLD_KEY_INDEX, keyIndex);
   }

   public int getTapHoldKeyIndex() {
      return (Integer)this.f_19804_.m_135370_(TAP_HOLD_KEY_INDEX);
   }

   public void setTapHoldWindupTicks(int ticks) {
      this.f_19804_.m_135381_(TAP_HOLD_WINDUP_TICKS, ticks);
   }

   public int getTapHoldWindupTicks() {
      return (Integer)this.f_19804_.m_135370_(TAP_HOLD_WINDUP_TICKS);
   }

   public boolean isInSpawnCooldown() {
      return this.f_19797_ - this.spawnTick < this.spawnCooldownTicks;
   }

   public void setDoppelDelayMode(int mode) {
      this.f_19804_.m_135381_(DOPPEL_DELAY_MODE, mode);
   }

   public int getDoppelDelayMode() {
      return (Integer)this.f_19804_.m_135370_(DOPPEL_DELAY_MODE);
   }

   public static int getDelayTicks(int mode) {
      return switch (mode) {
         case 1 -> 4;
         case 2 -> 12;
         default -> 0;
      };
   }

   public float getRenderAlpha() {
      return (Float)this.f_19804_.m_135370_(RENDER_ALPHA);
   }

   public void setRenderAlpha(float alpha) {
      this.f_19804_.m_135381_(RENDER_ALPHA, Math.min(1.0F, Math.max(0.0F, alpha)));
   }

   public float getColorTintIntensity() {
      return (Float)this.f_19804_.m_135370_(COLOR_TINT_INTENSITY);
   }

   public void setColorTintIntensity(float intensity) {
      this.f_19804_.m_135381_(COLOR_TINT_INTENSITY, Math.min(1.0F, Math.max(0.0F, intensity)));
   }

   @Nullable
   public PlayerPatch<?> getOwnerPatch() {
      Player owner = this.getOwner();
      if (owner == null) {
         this.ownerPatch = null;
         return null;
      } else if (this.ownerPatch != null && this.ownerPatch.getOriginal() == owner) {
         return this.ownerPatch;
      } else {
         this.ownerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(owner, PlayerPatch.class);
         return this.ownerPatch;
      }
   }

   public void m_7378_(@Nonnull CompoundTag tag) {
      this.ownerPatch = null;
      super.m_7378_(tag);
      this.f_19804_.m_135381_(BINDING_GENERATION, Math.max(0L, tag.m_128454_("BindingGeneration")));
      if (tag.m_128403_("OwnerUUID")) {
         this.f_19804_.m_135381_(OWNER_UUID, Optional.of(tag.m_128342_("OwnerUUID")));
      }

      this.f_19804_.m_135381_(OWNER_NAME, tag.m_128461_("OwnerName"));
      this.f_19804_.m_135381_(DOPPEL_DELAY_MODE, Mth.m_14045_(tag.m_128451_("DoppelDelayMode"), 0, 2));
      this.f_19804_.m_135381_(RENDER_ALPHA, tag.m_128441_("RenderAlpha") ? tag.m_128457_("RenderAlpha") : 0.66F);
      this.f_19804_.m_135381_(COLOR_TINT_INTENSITY, tag.m_128441_("ColorTintIntensity") ? tag.m_128457_("ColorTintIntensity") : 0.75F);
   }

   public void m_7380_(@Nonnull CompoundTag tag) {
      super.m_7380_(tag);
      tag.m_128356_("BindingGeneration", this.getBindingGeneration());
      UUID uuid = (UUID)((Optional)this.f_19804_.m_135370_(OWNER_UUID)).orElse(null);
      if (uuid != null) {
         tag.m_128362_("OwnerUUID", uuid);
      }

      tag.m_128359_("OwnerName", this.getOwnerName());
      tag.m_128405_("DoppelDelayMode", this.getDoppelDelayMode());
      tag.m_128350_("RenderAlpha", this.getRenderAlpha());
      tag.m_128350_("ColorTintIntensity", this.getColorTintIntensity());
   }

   @Nonnull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   public boolean m_6469_(@Nonnull DamageSource source, float amount) {
      return false;
   }

   public boolean m_6673_(@Nonnull DamageSource source) {
      return true;
   }

   public boolean m_5825_() {
      return true;
   }

   public void m_20254_(int seconds) {
   }

   public void m_7311_(int ticks) {
   }

   public boolean m_6060_() {
      return false;
   }

   public void m_8119_() {
      Player owner = this.getOwner();
      PlayerPatch<?> ownerPatch = this.getOwnerPatch();
      if (owner != null) {
         if (!owner.m_6084_()) {
            if (!this.m_9236_().f_46443_) {
               discardDoppelganger(this);
            }
         } else if (ownerPatch != null) {
            if (!this.m_9236_().f_46443_) {
               for (Entity entity : this.m_9236_().m_142646_().m_142273_()) {
                  if (entity != this
                     && entity instanceof DoppelgangerEntity other
                     && owner.m_20148_().equals(other.getOwnerUUID())
                     && other.m_6084_()
                     && (other.f_19797_ > this.f_19797_ || other.f_19797_ == this.f_19797_ && other.m_20148_().compareTo(this.m_20148_()) < 0)) {
                     discardDoppelganger(this);
                     return;
                  }
               }
            }

            Objects.requireNonNull(this.m_21051_(Attributes.f_22279_)).m_22100_(owner.m_21133_(Attributes.f_22279_));
            this.m_20242_(this.isInSpawnCooldown());
            super.m_8119_();
            if (!this.m_9236_().f_46443_ && !this.isInSpawnCooldown() && this.f_19797_ % 10 == 0 && !owner.m_7500_()) {
               float cost = switch (this.getDoppelDelayMode()) {
                  case 0 -> 7.0F;
                  case 2 -> 4.0F;
                  default -> 5.0F;
               };
               if (!VergilSkill.consumeDoppelgangerDt(ownerPatch, cost)) {
                  discardDoppelganger(this);
                  return;
               }
            }

            DoppelgangerPatch dp = (DoppelgangerPatch)EpicFightCapabilities.getEntityPatch(this, DoppelgangerPatch.class);
            if (dp != null) {
               if (!dp.getEntityState().turningLocked() && !dp.isComboCFacingLocked()) {
                  LivingEntity lookTarget = DoppelgangerTargetingController.selectFacingTarget(ownerPatch, this);
                  if (lookTarget != null) {
                     this.m_7618_(Anchor.EYES, new Vec3(lookTarget.m_20185_(), lookTarget.m_20188_(), lookTarget.m_20189_()));
                     this.f_20883_ = MathUtils.rotlerp(this.f_20883_, this.m_146908_(), 30.0F);
                     this.f_20885_ = this.m_146908_();
                  } else {
                     this.m_146922_(owner.m_146908_());
                     this.f_19859_ = owner.f_19859_;
                     this.m_5618_(owner.f_20883_);
                     this.f_20884_ = owner.f_20884_;
                     this.m_146926_(owner.m_146909_());
                     this.f_19860_ = owner.f_19860_;
                     this.f_20885_ = owner.f_20885_;
                     this.f_20886_ = owner.f_20886_;
                  }
               }

               LivingMotion livingMotion = ownerPatch.getCurrentLivingMotion();
               if (livingMotion != null) {
                  if (!livingMotion.isSame(LivingMotions.WALK) && livingMotion.isSame(LivingMotions.SNEAK)) {
                  }

                  if (livingMotion.isSame(LivingMotions.RUN)) {
                  }
               }
            }
         }
      }
   }

   public boolean m_6094_() {
      return false;
   }

   public void m_7023_(@NotNull Vec3 travelVector) {
      Player owner = this.getOwner();
      DoppelgangerPatch dp = (DoppelgangerPatch)EpicFightCapabilities.getEntityPatch(this, DoppelgangerPatch.class);
      if (dp != null) {
         if (owner == null || this.m_9236_().m_5776_()) {
            super.m_7023_(travelVector);
         } else if (dp.getEntityState().updateLivingMotion()) {
            PlayerPatch<?> ownerPatch = this.getOwnerPatch();
            PlayerMovementFrame input = owner.getCapability(DoppelgangerCapability.INSTANCE)
               .map(DoppelgangerCapability.IDoppelgangerData::getLastMovementFrame)
               .orElse(PlayerMovementFrame.EMPTY);
            double ySpeed = this.m_20184_().f_82480_;
            if (this.m_20096_()) {
               ySpeed = Math.max(0.0, ySpeed);
            }

            if (this.m_20069_()) {
               ySpeed = ySpeed * 0.8 - 0.02;
            } else if (this.m_20077_()) {
               ySpeed = ySpeed * 0.5 - 0.02;
            } else if (!this.m_20096_() && !this.m_20068_()) {
               ySpeed -= 0.08;
               ySpeed *= 0.98;
            }

            if (!input.hasMovementInput()) {
               this.m_20334_(0.0, ySpeed, 0.0);
               this.m_6478_(MoverType.SELF, this.m_20184_());
               this.f_20900_ = 0.0F;
               this.f_20902_ = 0.0F;
            } else {
               Vec3 moveDir = cameraRelativeToWorld(input.forward(), input.strafe(), input.cameraYaw());
               double distToOwner = (double)this.m_20270_(owner);
               if (distToOwner >= 64.0) {
                  Vec3 toOwner = owner.m_20182_().m_82546_(this.m_20182_()).m_82541_();
                  double dot = moveDir.m_82526_(toOwner);
                  if (dot < 0.0) {
                     moveDir = moveDir.m_82546_(toOwner.m_82490_(dot)).m_82541_();
                  }
               }

               if (moveDir.m_82556_() < 0.001) {
                  this.m_20334_(0.0, ySpeed, 0.0);
                  this.m_6478_(MoverType.SELF, this.m_20184_());
                  this.f_20900_ = 0.0F;
                  this.f_20902_ = 0.0F;
               } else {
                  float targetYaw = (float)(Math.atan2(-moveDir.f_82479_, moveDir.f_82481_) * (180.0 / Math.PI));
                  if (!dp.getEntityState().turningLocked() && !dp.isComboCFacingLocked()) {
                     this.m_146922_(targetYaw);
                     this.f_20883_ = targetYaw;
                     this.f_20885_ = targetYaw;
                  }

                  double baseSpeed = this.m_21133_(Attributes.f_22279_);
                  double speedMultiplier = 2.15;
                  if (owner.m_20142_()) {
                     speedMultiplier = 2.2;
                  } else if (owner.m_6047_()) {
                     speedMultiplier = 0.65;
                  }

                  double finalSpeed = baseSpeed * speedMultiplier;
                  this.m_20334_(moveDir.f_82479_ * finalSpeed, ySpeed, moveDir.f_82481_ * finalSpeed);
                  this.m_6478_(MoverType.SELF, this.m_20184_());
                  this.f_20900_ = input.strafe();
                  this.f_20902_ = input.forward();
               }
            }
         }
      }
   }

   private static Vec3 cameraRelativeToWorld(float forward, float strafe, float cameraYaw) {
      float rad = cameraYaw * (float) (Math.PI / 180.0);
      float sin = (float)Math.sin((double)rad);
      float cos = (float)Math.cos((double)rad);
      double x = (double)(strafe * cos - forward * sin);
      double z = (double)(forward * cos + strafe * sin);
      double len = Math.sqrt(x * x + z * z);
      return len > 0.001 ? new Vec3(x / len, 0.0, z / len) : Vec3.f_82478_;
   }

   public boolean m_6785_(double distanceToClosestPlayer) {
      return false;
   }

   public boolean m_6087_() {
      return true;
   }

   public boolean m_142066_() {
      return false;
   }

   @Nonnull
   public Iterable<ItemStack> m_6168_() {
      return Collections.emptyList();
   }

   @Nonnull
   public HumanoidArm m_5737_() {
      return HumanoidArm.RIGHT;
   }

   public static Builder createAttributes() {
      return PathfinderMob.m_21552_()
         .m_22266_((Attribute)EpicFightAttributes.WEIGHT.get())
         .m_22266_((Attribute)EpicFightAttributes.ARMOR_NEGATION.get())
         .m_22266_((Attribute)EpicFightAttributes.IMPACT.get())
         .m_22266_((Attribute)EpicFightAttributes.MAX_STRIKES.get())
         .m_22266_(Attributes.f_22281_)
         .m_22266_(Attributes.f_22279_);
   }

   public static void syncDoppelgangerUUID(ServerPlayer player, @Nullable UUID uuid) {
      if (uuid == null) {
         DoppelgangerBindingService.clearBinding(player);
      } else {
         DoppelgangerBindingService.reconcile(player);
         DoppelgangerEntity bound = DoppelgangerBindingService.findBoundEntity(player);
         if (bound != null && uuid.equals(bound.m_20148_())) {
            DoppelgangerBindingService.bindActive(player, bound);
         }
      }
   }
}
