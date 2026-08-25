package com.dmc.invincible_dmc.skill.dodge;

import com.dmc.invincible_dmc.api.animation.types.customStun.CustomStunAttackAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.JudgementCutEndAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoDodgeAnimation;
import com.dmc.invincible_dmc.api.weapon.DmcWeaponType;
import com.dmc.invincible_dmc.capability.weapon.DmcWeaponManager;
import com.dmc.invincible_dmc.client.input.ComboInputSampler;
import com.dmc.invincible_dmc.client.input.DMComboEngine;
import com.dmc.invincible_dmc.client.input.IComboExecutor;
import com.dmc.invincible_dmc.conditions.GroundedCondition;
import com.dmc.invincible_dmc.gameassets.DMCSkillDataKeys;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.network.DMCNetwork;
import com.dmc.invincible_dmc.network.server.SPCrazyComboReset;
import com.dmc.invincible_dmc.skill.weapon_innate.AbstractDmcInnateSkill;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.ConcentrationManager;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.dmc.invincible_dmc.utils.DMCLog;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.network.client.CPSkillRequest;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class VergilDodgeSkill extends DMCDodgeSkill {
   private static final UUID SKILL_CAST_UUID = UUID.fromString("e5f6a1b2-c1d8-11cd-a05b-0242ac120018");
   private final ConcentrationManager concentrationManager;
   public static final int DIRECTION_DOWN = 5;
   protected final AnimationAccessor<? extends StaticAnimation> downAnim;
   protected final Map<DmcWeaponType, VergilDodgeSkill.RecoveryAnimations> recoveryAnimations;
   protected int maxUpDodges = 1;
   protected int maxAirDodges = 2;
   protected int jcChargeTimeMs = -1;
   protected int forwardJcChargeMs = -1;
   protected int backwardJcChargeMs = -1;
   protected int leftJcChargeMs = -1;
   protected int rightJcChargeMs = -1;
   protected int upJcChargeMs = -1;
   protected int downJcChargeMs = -1;
   protected int dodgeBufferDurationTicks = -1;

   public VergilDodgeSkill(VergilDodgeSkill.YamatoBuilder builder) {
      super(builder);
      this.downAnim = builder.downAnim;
      this.jcChargeTimeMs = builder.jcChargeTimeMs;
      this.forwardJcChargeMs = builder.forwardJcChargeMs;
      this.backwardJcChargeMs = builder.backwardJcChargeMs;
      this.leftJcChargeMs = builder.leftJcChargeMs;
      this.rightJcChargeMs = builder.rightJcChargeMs;
      this.upJcChargeMs = builder.upJcChargeMs;
      this.downJcChargeMs = builder.downJcChargeMs;
      this.dodgeBufferDurationTicks = builder.dodgeBufferDurationTicks;
      this.recoveryAnimations = new EnumMap<>(builder.recoveryAnimations);
      this.concentrationManager = new ConcentrationManager();
   }

   public boolean isDodgeAnimation(@Nullable DynamicAnimation animation) {
      if (animation == null) {
         return false;
      } else {
         StaticAnimation real = DMCAnimationUtils.getRealAnimation(animation);
         if (real == null) {
            return false;
         } else {
            return real != this.forwardAnim.get()
                  && real != this.backwardAnim.get()
                  && real != this.leftAnim.get()
                  && real != this.rightAnim.get()
                  && real != this.upAnim.get()
                  && (this.downAnim == null || real != this.downAnim.get())
               ? this.recoveryAnimations.values().stream().anyMatch(animations -> animations.matches(real))
               : true;
         }
      }
   }

   protected AnimationAccessor<? extends StaticAnimation> getRecoveryAnimation(int direction) {
      return this.getRecoveryAnimation(direction, null);
   }

   protected AnimationAccessor<? extends StaticAnimation> getRecoveryAnimation(int direction, @Nullable PlayerPatch<?> executor) {
      DmcWeaponType weaponType = executor != null ? DmcWeaponManager.getActionWeapon((Player)executor.getOriginal()) : DmcWeaponType.YAMATO;
      VergilDodgeSkill.RecoveryAnimations animations = this.recoveryAnimations.get(weaponType);
      if (animations == null) {
         return this.getAnimation(direction);
      } else {
         boolean useCombatSideRecovery = executor != null && this.shouldUseCombatSideRecovery(executor);

         AnimationAccessor<? extends StaticAnimation> anim = switch (direction) {
            case 0 -> animations.forward();
            case 1 -> animations.backward();
            case 2 -> useCombatSideRecovery
            ? (animations.leftCombat() != null ? animations.leftCombat() : this.leftAnim)
            : (animations.leftShort() != null ? animations.leftShort() : this.leftAnim);
            case 3 -> useCombatSideRecovery
            ? (animations.rightCombat() != null ? animations.rightCombat() : this.rightAnim)
            : (animations.rightShort() != null ? animations.rightShort() : this.rightAnim);
            case 4 -> animations.up();
            default -> animations.forward();
         };
         return anim != null ? anim : this.getAnimation(direction);
      }
   }

   protected boolean shouldUseCombatSideRecovery(PlayerPatch<?> executor) {
      LivingEntity player = (LivingEntity)executor.getOriginal();
      LivingEntity target = executor.getTarget();
      if (target == null) {
         return false;
      } else {
         Vec3 toTarget = target.m_146892_().m_82546_(player.m_146892_());
         double horizontalDistanceSqr = toTarget.f_82479_ * toTarget.f_82479_ + toTarget.f_82481_ * toTarget.f_82481_;
         if (horizontalDistanceSqr > 9.0) {
            return false;
         } else {
            Vec3 flatToTarget = new Vec3(toTarget.f_82479_, 0.0, toTarget.f_82481_);
            if (flatToTarget.m_82556_() < 1.0E-6) {
               return true;
            } else {
               float targetYaw = Mth.m_14177_((float)MathUtils.getYRotOfVector(flatToTarget));
               float facingYaw = Mth.m_14177_(executor.getYRot());
               float yawDelta = Math.abs(Mth.m_14177_(targetYaw - facingYaw));
               return yawDelta <= 85.0F;
            }
         }
      }
   }

   public int getMaxUpDodges() {
      return this.maxUpDodges;
   }

   public int getMaxAirDodges() {
      return this.maxAirDodges;
   }

   public int getUpDodgeCount(SkillContainer container) {
      return (Integer)container.getDataManager().getDataValue((SkillDataKey)DMCSkillDataKeys.UP_DODGE_COUNT.get());
   }

   public int getAirDodgeCount(SkillContainer container) {
      return (Integer)container.getDataManager().getDataValue((SkillDataKey)DMCSkillDataKeys.AIR_DODGE_COUNT.get());
   }

   public boolean isDodgeLimitReached(SkillDataManager dm, int direction, boolean isInAir) {
      int upUsed = (Integer)dm.getDataValue((SkillDataKey)DMCSkillDataKeys.UP_DODGE_COUNT.get());
      int airUsed = (Integer)dm.getDataValue((SkillDataKey)DMCSkillDataKeys.AIR_DODGE_COUNT.get());
      boolean blocked;
      if (isInAir) {
         if (direction == 4) {
            blocked = upUsed >= this.maxUpDodges;
         } else if (direction != 0 && direction != 1 && direction != 2 && direction != 3 && direction != 5) {
            blocked = false;
         } else {
            blocked = airUsed >= this.maxAirDodges;
         }
      } else if (direction == 4) {
         blocked = upUsed >= this.maxUpDodges;
      } else {
         blocked = false;
      }

      DMCLog.debug(
         DMCLog.Category.COMBO_SERVER,
         "DODGE_LIMIT dir={} inAir={} up({}/{}) air({}/{}) blocked={}",
         direction,
         isInAir,
         upUsed,
         this.maxUpDodges,
         airUsed,
         this.maxAirDodges,
         blocked
      );
      return blocked;
   }

   public int incrementDodgeCount(SkillDataManager dm, int direction, boolean isInAir) {
      if (isInAir) {
         if (direction == 4) {
            int used = (Integer)dm.getDataValue((SkillDataKey)DMCSkillDataKeys.UP_DODGE_COUNT.get());
            dm.setDataSync((SkillDataKey)DMCSkillDataKeys.UP_DODGE_COUNT.get(), used + 1);
            return used + 1;
         }

         if (direction == 0 || direction == 1 || direction == 2 || direction == 3 || direction == 5) {
            int used = (Integer)dm.getDataValue((SkillDataKey)DMCSkillDataKeys.AIR_DODGE_COUNT.get());
            dm.setDataSync((SkillDataKey)DMCSkillDataKeys.AIR_DODGE_COUNT.get(), used + 1);
            return used + 1;
         }
      } else if (direction == 4) {
         int used = (Integer)dm.getDataValue((SkillDataKey)DMCSkillDataKeys.UP_DODGE_COUNT.get());
         dm.setDataSync((SkillDataKey)DMCSkillDataKeys.UP_DODGE_COUNT.get(), used + 1);
         return used + 1;
      }

      return -1;
   }

   public int getJcChargeTimeForAnimation(@Nullable StaticAnimation real) {
      if (real == null) {
         return -1;
      } else {
         int dir = this.forwardAnim.get() != null && real == this.forwardAnim.get()
            ? this.forwardJcChargeMs
            : (
               this.backwardAnim.get() != null && real == this.backwardAnim.get()
                  ? this.backwardJcChargeMs
                  : (
                     this.leftAnim.get() != null && real == this.leftAnim.get()
                        ? this.leftJcChargeMs
                        : (
                           this.rightAnim.get() != null && real == this.rightAnim.get()
                              ? this.rightJcChargeMs
                              : (
                                 this.upAnim.get() != null && real == this.upAnim.get()
                                    ? this.upJcChargeMs
                                    : (this.downAnim != null && this.downAnim.get() != null && real == this.downAnim.get() ? this.downJcChargeMs : -1)
                              )
                        )
                  )
            );
         return dir > 0 ? dir : this.jcChargeTimeMs;
      }
   }

   @Deprecated
   public int getJcChargeTimeMs() {
      return this.jcChargeTimeMs;
   }

   public int getDodgeBufferDurationTicks() {
      return this.dodgeBufferDurationTicks;
   }

   public void setParams(CompoundTag parameters) {
      super.setParams(parameters);
      if (parameters.m_128441_("max_up_dodges")) {
         this.maxUpDodges = parameters.m_128451_("max_up_dodges");
      }

      if (parameters.m_128441_("max_air_dodges")) {
         this.maxAirDodges = parameters.m_128451_("max_air_dodges");
      }

      if (parameters.m_128441_("consumption")) {
         this.consumption = (float)parameters.m_128459_("consumption");
      }

      if (parameters.m_128441_("jc_charge_time")) {
         this.jcChargeTimeMs = parameters.m_128451_("jc_charge_time");
      }

      if (parameters.m_128441_("jc_charge_time_forward")) {
         this.forwardJcChargeMs = parameters.m_128451_("jc_charge_time_forward");
      }

      if (parameters.m_128441_("jc_charge_time_backward")) {
         this.backwardJcChargeMs = parameters.m_128451_("jc_charge_time_backward");
      }

      if (parameters.m_128441_("jc_charge_time_left")) {
         this.leftJcChargeMs = parameters.m_128451_("jc_charge_time_left");
      }

      if (parameters.m_128441_("jc_charge_time_right")) {
         this.rightJcChargeMs = parameters.m_128451_("jc_charge_time_right");
      }

      if (parameters.m_128441_("jc_charge_time_up")) {
         this.upJcChargeMs = parameters.m_128451_("jc_charge_time_up");
      }

      if (parameters.m_128441_("jc_charge_time_down")) {
         this.downJcChargeMs = parameters.m_128451_("jc_charge_time_down");
      }

      if (parameters.m_128441_("dodge_buffer_duration_ticks")) {
         this.dodgeBufferDurationTicks = parameters.m_128451_("dodge_buffer_duration_ticks");
      }
   }

   public void onInitiate(SkillContainer container) {
      super.onInitiate(container);
      PlayerEventListener listener = container.getExecutor().getEventListener();
      listener.addEventListener(
         EventType.SKILL_CAST_EVENT,
         SKILL_CAST_UUID,
         event -> {
            if (event.getSkillContainer() == container && !event.isStateExecutable()) {
               DynamicAnimation animation = DMCAnimationUtils.getRealAnimation(container.getExecutor());
               if (DMCAnimationUtils.isAnimationType(animation, ActionAnimation.class)
                  && !DMCAnimationUtils.isAnimationType(animation, YamatoDodgeAnimation.class)
                  && !event.isStateExecutable()) {
                  boolean isInAir = !GroundedCondition.check((LivingEntity)event.getPlayerPatch().getOriginal());
                  SkillDataManager dm = event.getSkillContainer().getDataManager();
                  int upUsed = (Integer)dm.getDataValue((SkillDataKey)DMCSkillDataKeys.UP_DODGE_COUNT.get());
                  int airUsed = (Integer)dm.getDataValue((SkillDataKey)DMCSkillDataKeys.AIR_DODGE_COUNT.get());
                  boolean anyAvailable = !isInAir || upUsed < this.maxUpDodges || airUsed < this.maxAirDodges;
                  if (anyAvailable) {
                     event.setStateExecutable(true);
                  }
               }
            }
         }
      );
   }

   public void updateContainer(SkillContainer container) {
      super.updateContainer(container);
      if (container.getExecutor() instanceof ServerPlayerPatch) {
         SkillDataManager manager = container.getDataManager();
         SkillContainer innateSkill = container.getServerExecutor().getSkill(SkillSlots.WEAPON_INNATE);
         if (innateSkill != null
            && innateSkill.getSkill() instanceof AbstractDmcInnateSkill
            && (Boolean)innateSkill.getDataManager().getDataValue((SkillDataKey)DMCSkillDataKeys.IS_ON_GROUND.get())) {
            if ((Integer)manager.getDataValue((SkillDataKey)DMCSkillDataKeys.UP_DODGE_COUNT.get()) != 0) {
               manager.setDataSync((SkillDataKey)DMCSkillDataKeys.UP_DODGE_COUNT.get(), 0);
            }

            if ((Integer)manager.getDataValue((SkillDataKey)DMCSkillDataKeys.AIR_DODGE_COUNT.get()) != 0) {
               manager.setDataSync((SkillDataKey)DMCSkillDataKeys.AIR_DODGE_COUNT.get(), 0);
            }
         }
      }
   }

   @Override
   protected AnimationAccessor<? extends StaticAnimation> getAnimation(int direction) {
      return direction == 5 ? this.downAnim : super.getAnimation(direction);
   }

   @Override
   public boolean isExecutableState(PlayerPatch<?> executor) {
      EntityState playerState = executor.getEntityState();
      return playerState.canUseSkill()
         && !((Player)executor.getOriginal()).m_20069_()
         && !((Player)executor.getOriginal()).m_6147_()
         && ((Player)executor.getOriginal()).m_20202_() == null;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public Object getExecutionPacket(SkillContainer skillContainer, FriendlyByteBuf args) {
      IComboExecutor dispatcher = DMComboEngine.getLocalPlayerDispatcher();
      if (dispatcher != null) {
         Options opts = Minecraft.m_91087_().f_91066_;
         dispatcher.getDirectionTracker()
            .clearForDodge(
               DMComboEngine.engineTick,
               ComboInputSampler.isRawKeyDown(opts.f_92085_),
               ComboInputSampler.isRawKeyDown(opts.f_92087_),
               ComboInputSampler.isRawKeyDown(opts.f_92086_),
               ComboInputSampler.isRawKeyDown(opts.f_92088_)
            );
      }

      LocalPlayerPatch executor = skillContainer.getClientExecutor();
      LocalPlayer localPlayer = (LocalPlayer)executor.getOriginal();
      float pulse = Mth.m_14036_(0.3F + EnchantmentHelper.m_220302_(localPlayer), 0.0F, 1.0F);
      Input input = localPlayer.f_108618_;
      input.m_214106_(false, pulse);
      int forward = input.f_108568_ ? 1 : 0;
      int backward = input.f_108569_ ? -1 : 0;
      int left = input.f_108570_ ? 1 : 0;
      int right = input.f_108571_ ? -1 : 0;
      int vertical = forward + backward;
      int horizon = left + right;
      float yRot = Minecraft.m_91087_().f_91063_.m_109153_().m_90590_();
      boolean isInAir = !GroundedCondition.check(localPlayer);
      float degree;
      int direction;
      if (vertical == 0 && horizon == 0) {
         direction = 4;
         degree = yRot;
      } else if (isInAir && vertical < 0) {
         direction = 5;
         degree = yRot;
      } else if (vertical == 0) {
         if (this.leftAnim != null && this.rightAnim != null) {
            direction = horizon >= 0 ? 2 : 3;
            degree = yRot;
         } else {
            direction = 0;
            degree = yRot + (horizon >= 0 ? -90.0F : 90.0F);
         }
      } else {
         direction = vertical >= 0 ? 0 : 1;
         degree = yRot - 45.0F * (float)vertical * (float)horizon;
      }

      CPSkillRequest packet = new CPSkillRequest(skillContainer.getSlot());
      packet.getBuffer().writeInt(direction);
      packet.getBuffer().writeFloat(degree);
      packet.getBuffer().writeBoolean(isInAir);
      return packet;
   }

   @Override
   public void executeOnServer(SkillContainer skillContainer, FriendlyByteBuf args) {
      int direction = args.readInt();
      float yRot = args.readFloat();
      boolean isInAir = args.readBoolean();
      ServerPlayerPatch executor = skillContainer.getServerExecutor();
      SkillDataManager dataManager = skillContainer.getDataManager();
      SkillContainer weaponInnate = skillContainer.getServerExecutor().getSkill(SkillSlots.WEAPON_INNATE);
      DynamicAnimation currentAnim = DMCAnimationUtils.getRealAnimation(skillContainer.getExecutor());
      if (DMCAnimationUtils.isRealAnimationType(currentAnim, JudgementCutEndAnimation.class)) {
         Optional<TimePairList> unsheathTime = currentAnim.getProperty(YamatoAttackAnimation.UNSHEATH_TIME);
         if (unsheathTime.isPresent()) {
            TimePairList tp = unsheathTime.get();
            if (tp.isTimeInPairs(Objects.requireNonNull(DMCAnimationUtils.getMainPlayer(skillContainer.getExecutor())).getElapsedTime())) {
               return;
            }
         }
      }

      if (!this.isDodgeLimitReached(dataManager, direction, isInAir)) {
         this.incrementDodgeCount(dataManager, direction, isInAir);
         if (weaponInnate.getSkill() instanceof VergilSkill vergilSkill) {
            vergilSkill.resetCombo(weaponInnate);
            DMCNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(executor::getOriginal), new SPCrazyComboReset());
            if (!DMCAnimationUtils.isRealAnimationType(currentAnim, AttackAnimation.class) && !vergilSkill.isDodgeConcentrationFree(weaponInnate, currentAnim)) {
               this.concentrationManager.onDodge(weaponInnate);
            }
         }

         AnimationAccessor<? extends StaticAnimation> animation;
         if (this.isRecovery(executor, currentAnim)
            && GroundedCondition.check((LivingEntity)executor.getOriginal())
            && DMCAnimationUtils.isRealAnimationType(currentAnim, CustomStunAttackAnimation.class)) {
            animation = this.getRecoveryAnimation(direction, executor);
         } else {
            animation = this.getAnimation(direction);
         }

         if (animation != null) {
            executor.playAnimationSynchronized(animation, 0.0F);
            executor.setModelYRot(yRot, true);
         }
      }
   }

   public boolean isRecovery(PlayerPatch<?> executor, DynamicAnimation currentAnim) {
      int phaseLevel = executor.getEntityState().getLevel();
      StaticAnimation real = DMCAnimationUtils.getRealAnimation(currentAnim);
      return !DMCAnimationUtils.isRealAnimation(real, YamatoAnimations.YAMATO_RAPIDSLASH)
            && !DMCAnimationUtils.isRealAnimation(real, YamatoAnimations.YAMATO_RAPIDSLASH_RE)
            && !DMCAnimationUtils.isRealAnimation(real, YamatoAnimations.YAMATO_STRIKE)
         ? phaseLevel == 3
            || executor.getEntityState().attacking()
            || DMCAnimationUtils.isRealAnimation(real, YamatoAnimations.YAMATO_COMBO_C_LOOP)
            || DMCAnimationUtils.isRealAnimation(real, YamatoAnimations.YAMATO_COMBO_C_START)
         : false;
   }

   public void onRemoved(SkillContainer container) {
      PlayerEventListener listener = container.getExecutor().getEventListener();
      listener.removeListener(EventType.SKILL_CAST_EVENT, SKILL_CAST_UUID);
   }

   public static VergilDodgeSkill.YamatoBuilder createYamatoDodgeBuilder() {
      return (VergilDodgeSkill.YamatoBuilder)new VergilDodgeSkill.YamatoBuilder()
         .setCategory(SkillCategories.DODGE)
         .setActivateType(ActivateType.ONE_SHOT)
         .setResource(Resource.STAMINA);
   }

   protected static record RecoveryAnimations(
      AnimationAccessor<? extends StaticAnimation> forward,
      AnimationAccessor<? extends StaticAnimation> backward,
      AnimationAccessor<? extends StaticAnimation> leftCombat,
      AnimationAccessor<? extends StaticAnimation> rightCombat,
      AnimationAccessor<? extends StaticAnimation> up,
      AnimationAccessor<? extends StaticAnimation> leftShort,
      AnimationAccessor<? extends StaticAnimation> rightShort
   ) {
      private boolean matches(StaticAnimation animation) {
         return matches(this.forward, animation)
            || matches(this.backward, animation)
            || matches(this.leftCombat, animation)
            || matches(this.rightCombat, animation)
            || matches(this.up, animation)
            || matches(this.leftShort, animation)
            || matches(this.rightShort, animation);
      }

      private static boolean matches(@Nullable AnimationAccessor<? extends StaticAnimation> accessor, StaticAnimation animation) {
         return accessor != null && accessor.get() == animation;
      }
   }

   public static class YamatoBuilder extends DMCDodgeSkill.Builder<VergilDodgeSkill> {
      protected AnimationAccessor<? extends StaticAnimation> downAnim;
      protected final Map<DmcWeaponType, VergilDodgeSkill.RecoveryAnimations> recoveryAnimations = new EnumMap<>(DmcWeaponType.class);
      protected int jcChargeTimeMs = -1;
      protected int forwardJcChargeMs = -1;
      protected int backwardJcChargeMs = -1;
      protected int leftJcChargeMs = -1;
      protected int rightJcChargeMs = -1;
      protected int upJcChargeMs = -1;
      protected int downJcChargeMs = -1;
      protected int dodgeBufferDurationTicks = -1;

      public VergilDodgeSkill.YamatoBuilder setDownAnim(AnimationAccessor<? extends StaticAnimation> downAnim) {
         this.downAnim = downAnim;
         return this;
      }

      public VergilDodgeSkill.YamatoBuilder setJcChargeTimeMs(int ms) {
         this.jcChargeTimeMs = ms;
         return this;
      }

      public VergilDodgeSkill.YamatoBuilder setJcChargeForward(int ms) {
         this.forwardJcChargeMs = ms;
         return this;
      }

      public VergilDodgeSkill.YamatoBuilder setJcChargeBackward(int ms) {
         this.backwardJcChargeMs = ms;
         return this;
      }

      public VergilDodgeSkill.YamatoBuilder setJcChargeLeft(int ms) {
         this.leftJcChargeMs = ms;
         return this;
      }

      public VergilDodgeSkill.YamatoBuilder setJcChargeRight(int ms) {
         this.rightJcChargeMs = ms;
         return this;
      }

      public VergilDodgeSkill.YamatoBuilder setJcChargeUp(int ms) {
         this.upJcChargeMs = ms;
         return this;
      }

      public VergilDodgeSkill.YamatoBuilder setJcChargeDown(int ms) {
         this.downJcChargeMs = ms;
         return this;
      }

      public VergilDodgeSkill.YamatoBuilder setDodgeBufferDurationTicks(int ticks) {
         this.dodgeBufferDurationTicks = ticks;
         return this;
      }

      public VergilDodgeSkill.YamatoBuilder setAnimations(
         AnimationAccessor<? extends StaticAnimation> forwardAnim,
         AnimationAccessor<? extends StaticAnimation> backwardAnim,
         AnimationAccessor<? extends StaticAnimation> leftAnim,
         AnimationAccessor<? extends StaticAnimation> rightAnim,
         AnimationAccessor<? extends StaticAnimation> upAnim
      ) {
         super.setAnimations(forwardAnim, backwardAnim, leftAnim, rightAnim, upAnim);
         return this;
      }

      public VergilDodgeSkill.YamatoBuilder setAnimations(
         AnimationAccessor<? extends StaticAnimation> forwardAnim,
         AnimationAccessor<? extends StaticAnimation> backwardAnim,
         AnimationAccessor<? extends StaticAnimation> upAnim
      ) {
         super.setAnimations(forwardAnim, backwardAnim, upAnim);
         return this;
      }

      public VergilDodgeSkill.YamatoBuilder setRecoveryAnimations(
         AnimationAccessor<? extends StaticAnimation> forwardAnim,
         AnimationAccessor<? extends StaticAnimation> backwardAnim,
         AnimationAccessor<? extends StaticAnimation> leftAnim,
         AnimationAccessor<? extends StaticAnimation> rightAnim,
         AnimationAccessor<? extends StaticAnimation> upAnim
      ) {
         return this.setRecoveryAnimations(DmcWeaponType.YAMATO, forwardAnim, backwardAnim, leftAnim, rightAnim, upAnim);
      }

      public VergilDodgeSkill.YamatoBuilder setRecoveryAnimations(
         DmcWeaponType weaponType,
         AnimationAccessor<? extends StaticAnimation> forwardAnim,
         AnimationAccessor<? extends StaticAnimation> backwardAnim,
         AnimationAccessor<? extends StaticAnimation> leftAnim,
         AnimationAccessor<? extends StaticAnimation> rightAnim,
         AnimationAccessor<? extends StaticAnimation> upAnim
      ) {
         return this.setRecoveryAnimations(weaponType, forwardAnim, backwardAnim, leftAnim, rightAnim, upAnim, leftAnim, rightAnim);
      }

      public VergilDodgeSkill.YamatoBuilder setRecoveryAnimations(
         AnimationAccessor<? extends StaticAnimation> forwardAnim,
         AnimationAccessor<? extends StaticAnimation> backwardAnim,
         AnimationAccessor<? extends StaticAnimation> leftCombatAnim,
         AnimationAccessor<? extends StaticAnimation> rightCombatAnim,
         AnimationAccessor<? extends StaticAnimation> upAnim,
         AnimationAccessor<? extends StaticAnimation> leftShortAnim,
         AnimationAccessor<? extends StaticAnimation> rightShortAnim
      ) {
         return this.setRecoveryAnimations(
            DmcWeaponType.YAMATO, forwardAnim, backwardAnim, leftCombatAnim, rightCombatAnim, upAnim, leftShortAnim, rightShortAnim
         );
      }

      public VergilDodgeSkill.YamatoBuilder setRecoveryAnimations(
         DmcWeaponType weaponType,
         AnimationAccessor<? extends StaticAnimation> forwardAnim,
         AnimationAccessor<? extends StaticAnimation> backwardAnim,
         AnimationAccessor<? extends StaticAnimation> leftCombatAnim,
         AnimationAccessor<? extends StaticAnimation> rightCombatAnim,
         AnimationAccessor<? extends StaticAnimation> upAnim,
         AnimationAccessor<? extends StaticAnimation> leftShortAnim,
         AnimationAccessor<? extends StaticAnimation> rightShortAnim
      ) {
         this.recoveryAnimations
            .put(
               weaponType,
               new VergilDodgeSkill.RecoveryAnimations(forwardAnim, backwardAnim, leftCombatAnim, rightCombatAnim, upAnim, leftShortAnim, rightShortAnim)
            );
         return this;
      }

      public VergilDodgeSkill.YamatoBuilder setRecoveryAnimations(
         AnimationAccessor<? extends StaticAnimation> forwardAnim,
         AnimationAccessor<? extends StaticAnimation> backwardAnim,
         AnimationAccessor<? extends StaticAnimation> upAnim
      ) {
         return this.setRecoveryAnimations(forwardAnim, backwardAnim, forwardAnim, forwardAnim, upAnim);
      }

      public VergilDodgeSkill.YamatoBuilder setRecoveryAnimations(
         DmcWeaponType weaponType,
         AnimationAccessor<? extends StaticAnimation> forwardAnim,
         AnimationAccessor<? extends StaticAnimation> backwardAnim,
         AnimationAccessor<? extends StaticAnimation> upAnim
      ) {
         return this.setRecoveryAnimations(weaponType, forwardAnim, backwardAnim, forwardAnim, forwardAnim, upAnim);
      }
   }
}
