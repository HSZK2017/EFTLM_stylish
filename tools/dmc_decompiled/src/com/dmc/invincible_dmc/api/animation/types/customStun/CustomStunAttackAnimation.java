package com.dmc.invincible_dmc.api.animation.types.customStun;

import com.dmc.invincible_dmc.api.stun.StrongStunController;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation.AvalonPhase;
import com.merlin204.avalon.epicfight.api.AnimationAttackResultEvent;
import com.merlin204.avalon.epicfight.api.AnimationAttackResultEvent.SimpleEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.PoseModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.BasicAttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.StunType;

public class CustomStunAttackAnimation extends AvalonAttackAnimation {
   private static final double MIN_LEDGE_SUPPORT_DEPTH = 0.75;
   private static final double SUPPORT_COLLISION_EPSILON = 1.0E-7;
   private boolean attackResultHookAdded;
   private final Map<LivingEntity, Map<UUID, Set<CustomStunAttackAnimation.SharedStunGroup>>> consumedSharedStunGroups = new WeakHashMap<>();
   public static final StaticAnimationProperty<Float> CAN_BASIC_ATTACK_START = new StaticAnimationProperty();
   public static final ActionAnimationProperty<Boolean> HOLD_LEDGE = new ActionAnimationProperty();
   private static final AttackPhaseProperty<CustomStunAttackAnimation.StrongStunBinding> STRONG_STUN_ANIMATION = new AttackPhaseProperty();
   private static final Map<ResourceLocation, List<Supplier<? extends StaticAnimation>>> PHASE_STUN_GROUND = new HashMap<>();
   private static final Map<ResourceLocation, List<Supplier<? extends StaticAnimation>>> PHASE_STUN_AIR = new HashMap<>();
   private static final Map<ResourceLocation, List<Supplier<? extends StaticAnimation>>> PHASE_STUN_GROUND_SDT = new HashMap<>();
   private static final Map<ResourceLocation, List<Supplier<? extends StaticAnimation>>> PHASE_STUN_AIR_SDT = new HashMap<>();
   private static final Map<ResourceLocation, List<CustomStunAttackAnimation.SharedStunGroup>> PHASE_STUN_GROUPS = new HashMap<>();
   private static final Map<ResourceLocation, List<Float>> PHASE_STUN_VERTICAL_OFFSETS = new HashMap<>();
   public static final PoseModifier AERIALRAVE_COMB_DIRECTION_MODIFIER = (self, pose, livingEntityPatch, time, partialTicks) -> {
      if (self.isStaticAnimation()) {
         if (livingEntityPatch instanceof PlayerPatch<?> playerpatch && playerpatch.isFirstPerson()) {
            return;
         }

         float pitch = livingEntityPatch.getAttackDirectionPitch();
         pitch = Math.min(30.0F, Math.max(-30.0F, pitch));
         float followFactor = 0.6F;
         float adjustedPitch = pitch * followFactor;
         JointTransform chest = pose.orElseEmpty("Chest");
         chest.frontResult(JointTransform.rotation(QuaternionUtils.XP.rotationDegrees(-adjustedPitch)), OpenMatrix4f::mulAsOriginInverse);
         if (livingEntityPatch instanceof PlayerPatch) {
            float xRot = MathUtils.lerpBetween(
               ((LivingEntity)livingEntityPatch.getOriginal()).f_19860_, ((LivingEntity)livingEntityPatch.getOriginal()).m_146909_(), partialTicks
            );
            float limitedXRot = Math.min(30.0F, Math.max(-30.0F, xRot));
            float headFollowFactor = 0.3F;
            float headPitch = (adjustedPitch + limitedXRot) * headFollowFactor;
            OpenMatrix4f toOriginalRotation = livingEntityPatch.getArmature()
               .getBoundTransformFor(pose, livingEntityPatch.getArmature().searchJointByName("Head"))
               .removeScale()
               .removeTranslation()
               .invert();
            Vec3f xAxis = OpenMatrix4f.transform3v(toOriginalRotation, Vec3f.X_AXIS, null);
            OpenMatrix4f headRotation = OpenMatrix4f.createRotatorDeg(-headPitch, xAxis);
            pose.orElseEmpty("Head").frontResult(JointTransform.fromMatrix(headRotation), OpenMatrix4f::mul);
         }
      }
   };

   private void initYamatoProperties() {
      this.addProperty(StaticAnimationProperty.POSE_MODIFIER, AERIALRAVE_COMB_DIRECTION_MODIFIER);
      this.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])));
      this.addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(120.0F));
      this.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(2.1474836E9F));
   }

   public CustomStunAttackAnimation(
      float transitionTime,
      AnimationAccessor<? extends BasicAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float play_speed,
      float damageMulti,
      Supplier<? extends StaticAnimation> hitAnimation,
      CustomStunPhase... phases
   ) {
      super(transitionTime, accessor, armature, play_speed, damageMulti, phases);
      this.initYamatoProperties();
   }

   public CustomStunAttackAnimation(
      float transitionTime,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      Collider collider,
      Joint colliderJoint,
      AnimationAccessor<? extends BasicAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float play_speed,
      float damageMulti
   ) {
      super(transitionTime, antic, preDelay, contact, recovery, collider, colliderJoint, accessor, armature, play_speed, damageMulti);
      this.initYamatoProperties();
   }

   public CustomStunAttackAnimation(
      float transitionTime,
      AnimationAccessor<? extends BasicAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float play_speed,
      float damageMulti,
      AvalonPhase... phases
   ) {
      super(transitionTime, accessor, armature, play_speed, damageMulti, phases);
      this.initYamatoProperties();
   }

   public CustomStunAttackAnimation(
      float transitionTime,
      AnimationAccessor<? extends BasicAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float damageMulti,
      AvalonPhase... phases
   ) {
      super(transitionTime, accessor, armature, damageMulti, phases);
      this.initYamatoProperties();
   }

   public CustomStunAttackAnimation(
      float transitionTime, AnimationAccessor<? extends BasicAttackAnimation> accessor, AssetAccessor<? extends Armature> armature, AvalonPhase... phases
   ) {
      super(transitionTime, accessor, armature, phases);
      this.initYamatoProperties();
   }

   public CustomStunAttackAnimation(
      float convertTime, String path, AssetAccessor<? extends Armature> armature, float play_speed, float damageMulti, AvalonPhase... phases
   ) {
      super(convertTime, path, armature, play_speed, damageMulti, phases);
      this.initYamatoProperties();
   }

   public CustomStunAttackAnimation(
      float transitionTime,
      AnimationAccessor<? extends BasicAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float play_speed,
      float damageMulti,
      CustomStunPhase... phases
   ) {
      this(transitionTime, accessor, armature, play_speed, damageMulti, null, phases);
   }

   protected void bindPhaseState(Phase phase) {
      float preDelay = phase.preDelay;
      this.stateSpectrumBlueprint
         .newTimePair(0.0F, preDelay)
         .addState(EntityState.PHASE_LEVEL, 1)
         .newTimePair(phase.start, phase.recovery)
         .addState(EntityState.CAN_SKILL_EXECUTION, false)
         .newTimePair(phase.start, phase.recovery)
         .addState(EntityState.MOVEMENT_LOCKED, true)
         .addState(EntityState.UPDATE_LIVING_MOTION, false)
         .newTimePair(phase.start, phase.end)
         .addState(EntityState.INACTION, true)
         .newTimePair(phase.antic, phase.end)
         .addState(EntityState.TURNING_LOCKED, true)
         .newTimePair(preDelay, phase.contact)
         .addState(EntityState.ATTACKING, true)
         .addState(EntityState.PHASE_LEVEL, 2)
         .newTimePair(phase.contact, phase.end)
         .addState(EntityState.PHASE_LEVEL, 3);
   }

   public void postInit() {
      if (PHASE_STUN_GROUPS.containsKey(this.getAccessor().registryName())) {
         this.ensureAttackResultHook();
      }

      float canBasicAttackEnd = this.getProperty(CAN_BASIC_ATTACK_START).orElse(-1.0F);

      for (Phase phase : this.phases) {
         float end = canBasicAttackEnd >= 0.0F ? canBasicAttackEnd : phase.recovery;
         this.stateSpectrumBlueprint.newTimePair(phase.start, end).addState(EntityState.CAN_BASIC_ATTACK, false);
      }

      super.postInit();
   }

   public void begin(LivingEntityPatch<?> entitypatch) {
      super.begin(entitypatch);
      if (!entitypatch.isLogicalClient()) {
         this.consumedSharedStunGroups.remove(entitypatch.getOriginal());
      }
   }

   public void end(LivingEntityPatch<?> entitypatch, AssetAccessor<? extends DynamicAnimation> nextAnimation, boolean isEnd) {
      super.end(entitypatch, nextAnimation, isEnd);
      if (!entitypatch.isLogicalClient()) {
         this.consumedSharedStunGroups.remove(entitypatch.getOriginal());
      }
   }

   protected Vec3 getCoordVector(LivingEntityPatch<?> entityPatch, AssetAccessor<? extends DynamicAnimation> animation) {
      Vec3 movement = super.getCoordVector(entityPatch, animation);
      return !this.getProperty(HOLD_LEDGE).orElse(false) ? movement : clampMovementToLedge((LivingEntity)entityPatch.getOriginal(), movement);
   }

   private static Vec3 clampMovementToLedge(LivingEntity entity, Vec3 movement) {
      if (!canHoldLedge(entity, movement)) {
         return movement;
      } else {
         double moveX = movement.f_82479_;
         double moveZ = movement.f_82481_;
         double supportDepth = getLedgeSupportDepth(entity);
         AABB bounds = entity.m_20191_();

         while (moveX != 0.0 && !hasSupportWithin(entity, bounds, moveX, 0.0, supportDepth)) {
            moveX = approachZero(moveX);
         }

         while (moveZ != 0.0 && !hasSupportWithin(entity, bounds, 0.0, moveZ, supportDepth)) {
            moveZ = approachZero(moveZ);
         }

         while (moveX != 0.0 && moveZ != 0.0 && !hasSupportWithin(entity, bounds, moveX, moveZ, supportDepth)) {
            moveX = approachZero(moveX);
            moveZ = approachZero(moveZ);
         }

         return new Vec3(moveX, movement.f_82480_, moveZ);
      }
   }

   private static boolean canHoldLedge(LivingEntity entity, Vec3 movement) {
      if (!entity.f_19794_
         && !entity.m_20159_()
         && !entity.m_21255_()
         && !entity.m_20069_()
         && !entity.m_20077_()
         && !entity.m_6147_()
         && !(movement.m_165925_() <= 1.0E-7)
         && !(Math.abs(movement.f_82480_) > 1.0E-5)) {
         if (entity instanceof Player player && player.m_150110_().f_35935_) {
            return false;
         }

         double supportDepth = getLedgeSupportDepth(entity);
         return entity.m_20096_()
            || (double)entity.f_19789_ < supportDepth && hasSupportWithin(entity, entity.m_20191_(), 0.0, 0.0, supportDepth - (double)entity.f_19789_);
      } else {
         return false;
      }
   }

   private static double getLedgeSupportDepth(LivingEntity entity) {
      return Math.max(0.75, (double)entity.m_274421_());
   }

   private static boolean hasSupportWithin(LivingEntity entity, AABB bounds, double moveX, double moveZ, double supportDepth) {
      if (supportDepth <= 1.0E-7) {
         return false;
      } else {
         AABB movedBounds = bounds.m_82386_(moveX, 0.0, moveZ);
         AABB collisionArea = movedBounds.m_82363_(0.0, -supportDepth, 0.0);
         Vec3 resolvedDrop = Entity.m_198894_(
            entity, new Vec3(0.0, -supportDepth, 0.0), movedBounds, entity.m_9236_(), entity.m_9236_().m_183134_(entity, collisionArea)
         );
         return resolvedDrop.f_82480_ > -supportDepth + 1.0E-7;
      }
   }

   private static double approachZero(double value) {
      if (value > 0.0) {
         return value < 0.05 ? 0.0 : value - 0.05;
      } else {
         return value > -0.05 ? 0.0 : value + 0.05;
      }
   }

   public int getPhaseOrderByTime(float elapsedTime) {
      int count = 0;

      for (Phase phase : this.phases) {
         if (elapsedTime >= phase.contact + 0.1F) {
            count++;
         }
      }

      return count;
   }

   public static void registerPhaseStun(
      AnimationAccessor<?> accessor, int phaseIndex, AnimationAccessor<? extends StaticAnimation> ground, AnimationAccessor<? extends StaticAnimation> air
   ) {
      registerPhaseStun(accessor, phaseIndex, ground, air, 0.0F);
   }

   public static void registerPhaseStun(
      AnimationAccessor<?> accessor,
      int phaseIndex,
      AnimationAccessor<? extends StaticAnimation> ground,
      AnimationAccessor<? extends StaticAnimation> air,
      float targetVerticalOffset
   ) {
      validateVerticalOffset(targetVerticalOffset);
      putAtIndex(PHASE_STUN_GROUND, accessor, phaseIndex, ground);
      putAtIndex(PHASE_STUN_AIR, accessor, phaseIndex, air);
      putAtIndex(PHASE_STUN_VERTICAL_OFFSETS, accessor, phaseIndex, targetVerticalOffset);
   }

   public static void registerSharedPhaseStun(
      AnimationAccessor<?> accessor, AnimationAccessor<? extends StaticAnimation> ground, AnimationAccessor<? extends StaticAnimation> air, int... phaseIndices
   ) {
      if (phaseIndices.length == 0) {
         throw new IllegalArgumentException("Shared stun group must contain at least one phase");
      } else {
         CustomStunAttackAnimation.SharedStunGroup group = new CustomStunAttackAnimation.SharedStunGroup();

         for (int phaseIndex : phaseIndices) {
            registerPhaseStun(accessor, phaseIndex, ground, air);
            putAtIndex(PHASE_STUN_GROUPS, accessor, phaseIndex, group);
         }
      }
   }

   public static void registerPhaseStun(
      AnimationAccessor<?> accessor,
      int phaseIndex,
      AnimationAccessor<? extends StaticAnimation> ground,
      AnimationAccessor<? extends StaticAnimation> air,
      @Nullable AnimationAccessor<? extends StaticAnimation> groundSDT,
      @Nullable AnimationAccessor<? extends StaticAnimation> airSDT
   ) {
      registerPhaseStun(accessor, phaseIndex, ground, air, groundSDT, airSDT, 0.0F);
   }

   public static void registerPhaseStun(
      AnimationAccessor<?> accessor,
      int phaseIndex,
      AnimationAccessor<? extends StaticAnimation> ground,
      AnimationAccessor<? extends StaticAnimation> air,
      @Nullable AnimationAccessor<? extends StaticAnimation> groundSDT,
      @Nullable AnimationAccessor<? extends StaticAnimation> airSDT,
      float targetVerticalOffset
   ) {
      registerPhaseStun(accessor, phaseIndex, ground, air, targetVerticalOffset);
      if (groundSDT != null) {
         putAtIndex(PHASE_STUN_GROUND_SDT, accessor, phaseIndex, groundSDT);
      }

      if (airSDT != null) {
         putAtIndex(PHASE_STUN_AIR_SDT, accessor, phaseIndex, airSDT);
      }
   }

   private static void validateVerticalOffset(float targetVerticalOffset) {
      if (!Float.isFinite(targetVerticalOffset)) {
         throw new IllegalArgumentException("Target vertical offset must be finite: " + targetVerticalOffset);
      }
   }

   public <A extends CustomStunAttackAnimation> A addStrongStunAnimation(
      int phaseIndex, AnimationAccessor<? extends StaticAnimation> ground, AnimationAccessor<? extends StaticAnimation> air
   ) {
      if (phaseIndex >= 0 && phaseIndex < this.phases.length) {
         this.ensureAttackResultHook();
         this.phases[phaseIndex].addProperty(STRONG_STUN_ANIMATION, new CustomStunAttackAnimation.StrongStunBinding(ground, air));
         return (A)this;
      } else {
         throw new IllegalArgumentException("Invalid strong stun phase index: " + phaseIndex);
      }
   }

   private static <T> void putAtIndex(Map<ResourceLocation, List<T>> map, AnimationAccessor<?> accessor, int idx, T value) {
      if (idx < 0) {
         throw new IllegalArgumentException("Phase index must be non-negative: " + idx);
      } else {
         ResourceLocation key = accessor.registryName();
         List<T> values = map.computeIfAbsent(key, ignored -> new ArrayList<>());

         while (values.size() <= idx) {
            values.add(null);
         }

         values.set(idx, value);
      }
   }

   public EpicFightDamageSource getEpicFightDamageSource(DamageSource originalSource, LivingEntityPatch<?> entitypatch, Entity target, Phase phase) {
      EpicFightDamageSource ds = super.getEpicFightDamageSource(originalSource, entitypatch, target, phase);
      if (!(ds instanceof ICustomStunDamageSource customDs)) {
         return ds;
      } else {
         ResourceLocation key = this.getAccessor().registryName();
         int idx = this.findPhaseIndex(phase);
         if (idx < 0) {
            return ds;
         } else {
            CustomStunAttackAnimation.SharedStunGroup sharedGroup = getAtIndex(PHASE_STUN_GROUPS.get(key), idx);
            if (sharedGroup != null && this.isSharedStunConsumed((LivingEntity)entitypatch.getOriginal(), target, sharedGroup)) {
               ds.setStunType(StunType.NONE);
               return ds;
            } else {
               boolean inSDT = SinDevilTriggerManager.isLivingInSDT((LivingEntity)entitypatch.getOriginal());
               Supplier<? extends StaticAnimation> ground = resolveStun(inSDT, PHASE_STUN_GROUND_SDT, PHASE_STUN_GROUND, key, idx);
               Supplier<? extends StaticAnimation> air = resolveStun(inSDT, PHASE_STUN_AIR_SDT, PHASE_STUN_AIR, key, idx);
               if (ground != null || air != null) {
                  customDs.invincible$setCustomStunAnimations(ground, null, air, null);
                  Float verticalOffset = getAtIndex(PHASE_STUN_VERTICAL_OFFSETS.get(key), idx);
                  customDs.invincible$setCustomStunVerticalOffset(verticalOffset != null ? verticalOffset : 0.0F);
               }

               return ds;
            }
         }
      }
   }

   @Nullable
   private static Supplier<? extends StaticAnimation> resolveStun(
      boolean inSDT,
      Map<ResourceLocation, List<Supplier<? extends StaticAnimation>>> sdtMap,
      Map<ResourceLocation, List<Supplier<? extends StaticAnimation>>> normalMap,
      ResourceLocation key,
      int idx
   ) {
      List<Supplier<? extends StaticAnimation>> sdtValues = sdtMap.get(key);
      List<Supplier<? extends StaticAnimation>> normalValues = normalMap.get(key);
      List<Supplier<? extends StaticAnimation>> values = inSDT && sdtValues != null ? sdtValues : normalValues;
      Supplier<? extends StaticAnimation> value = getAtIndex(values, idx);
      if (value != null) {
         return value;
      } else {
         return inSDT && sdtValues == null ? getAtIndex(normalValues, idx) : null;
      }
   }

   @Nullable
   private static <T> T getAtIndex(@Nullable List<T> values, int idx) {
      return values != null && idx >= 0 && idx < values.size() ? values.get(idx) : null;
   }

   private int findPhaseIndex(Phase target) {
      for (int i = 0; i < this.phases.length; i++) {
         if (this.phases[i] == target) {
            return i;
         }
      }

      return -1;
   }

   protected void onAttackResult(LivingEntityPatch<?> attackerPatch, Entity targetEntity, AttackResult attackResult) {
      if (!attackerPatch.isLogicalClient() && attackResult.resultType.dealtDamage() && targetEntity instanceof LivingEntity livingTarget) {
         AnimationPlayer player = DMCAnimationUtils.getPlayerFor(attackerPatch, this.getAccessor());
         if (player != null) {
            Phase phase = this.getPhaseByTime(player.getElapsedTime());
            int phaseIndex = this.findPhaseIndex(phase);
            CustomStunAttackAnimation.SharedStunGroup sharedGroup = getAtIndex(PHASE_STUN_GROUPS.get(this.getAccessor().registryName()), phaseIndex);
            if (sharedGroup != null) {
               this.consumeSharedStun((LivingEntity)attackerPatch.getOriginal(), livingTarget, sharedGroup);
            }

            phase.getProperty(STRONG_STUN_ANIMATION)
               .map(binding -> binding.resolve(livingTarget.m_20096_()))
               .ifPresent(
                  animation -> StrongStunController.request(
                        livingTarget, (LivingEntity)attackerPatch.getOriginal(), (AnimationAccessor<? extends StaticAnimation>)animation
                     )
               );
         }
      }
   }

   private void ensureAttackResultHook() {
      if (!this.attackResultHookAdded) {
         this.addAttackResultEvents(new AnimationAttackResultEvent[]{SimpleEvent.create(this::onAttackResult)});
         this.attackResultHookAdded = true;
      }
   }

   private boolean isSharedStunConsumed(LivingEntity attacker, Entity target, CustomStunAttackAnimation.SharedStunGroup group) {
      Map<UUID, Set<CustomStunAttackAnimation.SharedStunGroup>> targetGroups = this.consumedSharedStunGroups.get(attacker);
      if (targetGroups == null) {
         return false;
      } else {
         Set<CustomStunAttackAnimation.SharedStunGroup> groups = targetGroups.get(target.m_20148_());
         return groups != null && groups.contains(group);
      }
   }

   private void consumeSharedStun(LivingEntity attacker, LivingEntity target, CustomStunAttackAnimation.SharedStunGroup group) {
      this.consumedSharedStunGroups
         .computeIfAbsent(attacker, ignored -> new HashMap<>())
         .computeIfAbsent(target.m_20148_(), ignored -> new HashSet<>())
         .add(group);
   }

   private static final class SharedStunGroup {
   }

   private static record StrongStunBinding(AnimationAccessor<? extends StaticAnimation> ground, AnimationAccessor<? extends StaticAnimation> air) {
      private AnimationAccessor<? extends StaticAnimation> resolve(boolean onGround) {
         return onGround ? this.ground : this.air;
      }
   }
}
