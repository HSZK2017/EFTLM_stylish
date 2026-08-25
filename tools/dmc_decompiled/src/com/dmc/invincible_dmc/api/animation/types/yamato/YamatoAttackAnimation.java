package com.dmc.invincible_dmc.api.animation.types.yamato;

import com.dmc.invincible_dmc.api.animation.types.customStun.CustomStunAttackAnimation;
import com.dmc.invincible_dmc.api.animation.types.customStun.CustomStunPhase;
import com.dmc.invincible_dmc.api.forgeevent.YamatoSheathEvent;
import com.dmc.invincible_dmc.client.input.PlayerInputState;
import com.dmc.invincible_dmc.gameassets.DMCSkillDataKeys;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.ConcentrationManager;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation.AvalonPhase;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.property.MoveCoordFunctions.MoveCoordGetter;
import yesman.epicfight.api.animation.types.BasicAttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.api.utils.math.Vec4f;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

public class YamatoAttackAnimation extends CustomStunAttackAnimation {
   public static final ActionAnimationProperty<TimePairList> UNSHEATH_TIME = new ActionAnimationProperty();
   public static final ActionAnimationProperty<TimePairList> CAN_DODGE_TIME = new ActionAnimationProperty();
   public static final ActionAnimationProperty<TimePairList> JUMP_CANCEL_TIME = new ActionAnimationProperty();
   public static final ActionAnimationProperty<TimePairList> COMBO_INTERRUPT_TIME = new ActionAnimationProperty();
   public static final StaticAnimationProperty<Integer> INPUT_BUFFER_DURATION_TICKS = new StaticAnimationProperty();
   public static final ActionAnimationProperty<Float> SWING_VOLUME = new ActionAnimationProperty();
   public static final ActionAnimationProperty<TimePairList> JC_PERF_WINDOW = new ActionAnimationProperty();
   public static final ActionAnimationProperty<TimePairList> SP_MODEL_TIME = new ActionAnimationProperty();
   public static final ActionAnimationProperty<TimePairList> INVISIBLE_TIME = new ActionAnimationProperty();
   public static final StaticAnimationProperty<Boolean> USE_MESH2_IN_LINK = new StaticAnimationProperty();
   public static final StaticAnimationProperty<Boolean> NOT_USE_SUMMON_DOPPELGANGER_ANIM = new StaticAnimationProperty();
   public static final ActionAnimationProperty<TimePairList> JC_RELEASE_BLOCK_TIME = new ActionAnimationProperty();
   public static final ActionAnimationProperty<TimePairList> SDT_SHEATHING_TIME = new ActionAnimationProperty();
   public static final ActionAnimationProperty<TimePairList> SDT_IN_SHEATH_TIME = new ActionAnimationProperty();
   public static final ActionAnimationProperty<TimePairList> SDT_HIDE_WEAPON_TIME = new ActionAnimationProperty();
   public static final StaticAnimationProperty<Boolean> CORRECT_YROT_TO_CAMERA = new StaticAnimationProperty();
   public static final ActionAnimationProperty<TimePairList> YAMATO_NO_GRAVITY_TIME = new ActionAnimationProperty();
   public static final AttackAnimationProperty<Boolean> YAMATO_FIXED_MOVE_DISTANCE = new AttackAnimationProperty();
   private static final Map<ResourceLocation, Float> AERIAL_ACTION_INCREMENTS = new HashMap<>();
   private static final Map<ResourceLocation, Float> CONCENTRATION_HIT_GAINS = new HashMap<>();
   private static final Map<ResourceLocation, Float> CONCENTRATION_MISS_PENALTIES = new HashMap<>();
   private static final Map<AnimationAccessor<?>, YamatoAttackAnimation.SheathConfig> SHEATH_CONFIGS = new HashMap<>();
   private static final Map<ResourceLocation, float[]> JC_PERF_WINDOWS = new HashMap<>();
   private static final Map<ResourceLocation, float[]> JC_PERF_WINDOWS_RATIO = new HashMap<>();
   private static final Map<ResourceLocation, Integer> JC_CHARGE_TIMES = new HashMap<>();
   private static final Map<ResourceLocation, float[]> PARRY_WINDOWS = new HashMap<>();
   private static final Map<ResourceLocation, float[]> PHASE_DAMAGE_MULTIS = new HashMap<>();
   private static final Map<ResourceLocation, float[]> PHASE_IMPACTS = new HashMap<>();
   private static final Map<ResourceLocation, float[]> PHASE_ARMOR_NEGS = new HashMap<>();
   private static final Map<ResourceLocation, float[]> PHASE_DAMAGE_MULTIS_SDT = new HashMap<>();
   private static final Map<ResourceLocation, float[]> PHASE_IMPACTS_SDT = new HashMap<>();
   private static final Map<ResourceLocation, float[]> PHASE_ARMOR_NEGS_SDT = new HashMap<>();

   public static void registerSheath(AnimationAccessor<?> accessor, float time, RegistryObject<SoundEvent> sound) {
      SHEATH_CONFIGS.put(accessor, new YamatoAttackAnimation.SheathConfig(time, sound));
   }

   @Nullable
   public static YamatoAttackAnimation.SheathConfig getSheathConfig(AnimationAccessor<?> accessor) {
      return SHEATH_CONFIGS.get(accessor);
   }

   public static void registerJcPerfWindow(AnimationAccessor<?> accessor, float startSec, float endSec) {
      JC_PERF_WINDOWS.put(accessor.registryName(), new float[]{startSec, endSec});
   }

   public static void registerJcPerfWindowRatio(AnimationAccessor<?> accessor, float startRatio, float endRatio) {
      JC_PERF_WINDOWS_RATIO.put(accessor.registryName(), new float[]{startRatio, endRatio});
   }

   public static void registerJcChargeTime(AnimationAccessor<?> accessor, int chargeMs) {
      JC_CHARGE_TIMES.put(accessor.registryName(), chargeMs);
   }

   @Nullable
   public static Integer getJcChargeTime(StaticAnimation anim) {
      return JC_CHARGE_TIMES.get(anim.getRegistryName());
   }

   public static void registerParryWindow(AnimationAccessor<?> accessor, float startSec, float endSec) {
      PARRY_WINDOWS.put(accessor.registryName(), new float[]{startSec, endSec});
   }

   public static boolean isParryWindow(StaticAnimation anim, float elapsedTime) {
      float[] window = PARRY_WINDOWS.get(anim.getRegistryName());
      return window == null ? false : elapsedTime >= window[0] && elapsedTime <= window[1];
   }

   public static void registerPhaseDamageMulti(AnimationAccessor<?> accessor, float... multipliers) {
      PHASE_DAMAGE_MULTIS.put(accessor.registryName(), multipliers);
   }

   public static void registerPhaseImpact(AnimationAccessor<?> accessor, float... impacts) {
      PHASE_IMPACTS.put(accessor.registryName(), impacts);
   }

   public static void registerPhaseArmorNeg(AnimationAccessor<?> accessor, float... armorNegs) {
      PHASE_ARMOR_NEGS.put(accessor.registryName(), armorNegs);
   }

   public static void registerPhaseDamageMultiSdt(AnimationAccessor<?> accessor, float... multipliers) {
      PHASE_DAMAGE_MULTIS_SDT.put(accessor.registryName(), multipliers);
   }

   public static void registerPhaseImpactSdt(AnimationAccessor<?> accessor, float... impacts) {
      PHASE_IMPACTS_SDT.put(accessor.registryName(), impacts);
   }

   public static void registerPhaseArmorNegSdt(AnimationAccessor<?> accessor, float... armorNegs) {
      PHASE_ARMOR_NEGS_SDT.put(accessor.registryName(), armorNegs);
   }

   @Override
   public EpicFightDamageSource getEpicFightDamageSource(DamageSource originalSource, LivingEntityPatch<?> entitypatch, Entity target, Phase phase) {
      EpicFightDamageSource ds = super.getEpicFightDamageSource(originalSource, entitypatch, target, phase);
      ResourceLocation registryName = this.getAccessor().registryName();
      int phaseIndex = this.findPhaseIndex(phase);
      if (phaseIndex < 0) {
         return ds;
      } else {
         boolean inSDT = SinDevilTriggerManager.isLivingInSDT((LivingEntity)entitypatch.getOriginal());
         float[] multis = inSDT ? PHASE_DAMAGE_MULTIS_SDT.get(registryName) : null;
         if (multis == null) {
            multis = PHASE_DAMAGE_MULTIS.get(registryName);
         }

         if (multis != null && phaseIndex < multis.length) {
            ds.attachDamageModifier(ValueModifier.multiplier(multis[phaseIndex]));
         }

         float[] impacts = inSDT ? PHASE_IMPACTS_SDT.get(registryName) : null;
         if (impacts == null) {
            impacts = PHASE_IMPACTS.get(registryName);
         }

         if (impacts != null && phaseIndex < impacts.length) {
            ds.setBaseImpact(impacts[phaseIndex]);
         }

         float[] armorNegs = inSDT ? PHASE_ARMOR_NEGS_SDT.get(registryName) : null;
         if (armorNegs == null) {
            armorNegs = PHASE_ARMOR_NEGS.get(registryName);
         }

         if (armorNegs != null && phaseIndex < armorNegs.length) {
            ds.setBaseArmorNegation(armorNegs[phaseIndex]);
         }

         return ds;
      }
   }

   private int findPhaseIndex(Phase target) {
      for (int i = 0; i < this.phases.length; i++) {
         if (this.phases[i] == target) {
            return i;
         }
      }

      return -1;
   }

   public static void registerAerialActionIncrement(AnimationAccessor<?> accessor, float increment) {
      AERIAL_ACTION_INCREMENTS.put(accessor.registryName(), increment);
   }

   @Nullable
   public static Float getAerialActionIncrement(StaticAnimation anim) {
      return AERIAL_ACTION_INCREMENTS.get(anim.getRegistryName());
   }

   public static void registerConcentrationHitGain(AnimationAccessor<?> accessor, float gain) {
      CONCENTRATION_HIT_GAINS.put(accessor.registryName(), gain);
   }

   public static float getConcentrationHitGain(StaticAnimation anim) {
      return CONCENTRATION_HIT_GAINS.getOrDefault(anim.getRegistryName(), 0.0F);
   }

   public static void registerConcentrationMissPenalty(AnimationAccessor<?> accessor, float penalty) {
      CONCENTRATION_MISS_PENALTIES.put(accessor.registryName(), penalty);
   }

   @Nullable
   public static Float getConcentrationMissPenalty(StaticAnimation anim) {
      return CONCENTRATION_MISS_PENALTIES.get(anim.getRegistryName());
   }

   @Nullable
   public static float[] getJcPerfWindow(StaticAnimation anim) {
      return JC_PERF_WINDOWS.get(anim.getRegistryName());
   }

   @Nullable
   public static float[] getJcPerfWindowRatio(StaticAnimation anim) {
      return JC_PERF_WINDOWS_RATIO.get(anim.getRegistryName());
   }

   @Override
   public void begin(LivingEntityPatch<?> entitypatch) {
      super.begin(entitypatch);
      TimePairList yamatoNoGravity = (TimePairList)this.getProperty(YAMATO_NO_GRAVITY_TIME).orElse(null);
      if (yamatoNoGravity != null) {
         if (!entitypatch.isLogicalClient()) {
            if (entitypatch instanceof PlayerPatch<?> playerPatch) {
               SkillContainer container = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
               if (container != null) {
                  SkillDataKey<Float> key = (SkillDataKey<Float>)DMCSkillDataKeys.AERIAL_ACTION_COUNT.get();
                  if (container.getDataManager().hasData(key)) {
                     if (((LivingEntity)entitypatch.getOriginal()).m_20096_()) {
                        container.getDataManager().setDataSync(key, 0.0F);
                     } else {
                        float current = (Float)container.getDataManager().getDataValue(key);
                        float increment = AERIAL_ACTION_INCREMENTS.getOrDefault(this.getAccessor().registryName(), 1.0F);
                        container.getDataManager().setDataSync(key, current + increment);
                     }
                  }
               }
            }
         }
      }
   }

   private float getMissPenalty() {
      return ConcentrationManager.getMissPenalty(this);
   }

   @Override
   protected Vec3 getCoordVector(LivingEntityPatch<?> entitypatch, AssetAccessor<? extends DynamicAnimation> animation) {
      if (this.getProperty(YAMATO_FIXED_MOVE_DISTANCE).orElse(false)) {
         AnimationPlayer player = DMCAnimationUtils.getPlayerFor(entitypatch, animation);
         boolean inUpdateTime = this.getProperty(ActionAnimationProperty.COORD_UPDATE_TIME).map(t -> t.isTimeInPairs(player.getElapsedTime())).orElse(true);
         TransformSheet transformSheet = (TransformSheet)entitypatch.getAnimator().getVariables().getOrDefaultSharedVariable(ACTION_ANIMATION_COORD);
         MoveCoordFunctions.RAW_COORD.set((DynamicAnimation)animation.get(), entitypatch, transformSheet);
         MoveCoordGetter moveGetter = this.getProperty(ActionAnimationProperty.COORD_GET).orElse(MoveCoordFunctions.MODEL_COORD);
         Vec3f move = moveGetter.get((DynamicAnimation)animation.get(), entitypatch, transformSheet, player.getPrevElapsedTime(), player.getElapsedTime());
         LivingEntity livingentity = (LivingEntity)entitypatch.getOriginal();
         Vec3 motion = livingentity.m_20184_();
         boolean hasNoGravity = ((LivingEntity)entitypatch.getOriginal()).m_20068_();
         boolean moveVertical = this.getProperty(ActionAnimationProperty.MOVE_VERTICAL).orElse(this.getProperty(ActionAnimationProperty.COORD).isPresent());
         this.getProperty(ActionAnimationProperty.NO_GRAVITY_TIME).ifPresentOrElse(noGravityTime -> {
            if (noGravityTime.isTimeInPairs(((DynamicAnimation)animation.get()).isLinkAnimation() ? 0.0F : player.getElapsedTime())) {
               livingentity.m_20334_(motion.f_82479_, 0.0, motion.f_82481_);
            } else {
               move.y = 0.0F;
            }
         }, () -> {
            if (moveVertical && move.y > 0.0F && !hasNoGravity) {
               double gravity = livingentity.m_21051_((Attribute)ForgeMod.ENTITY_GRAVITY.get()).m_22135_();
               livingentity.m_20334_(motion.f_82479_, motion.f_82480_ < 0.0 ? motion.f_82480_ + gravity : 0.0, motion.f_82481_);
            }
         });
         if (!moveVertical) {
            move.y = 0.0F;
         }

         if (inUpdateTime) {
            this.getProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER).ifPresent(entityYRotProvider -> {
               float yRot = entityYRotProvider.get((DynamicAnimation)animation.get(), entitypatch);
               entitypatch.setYRot(yRot);
            });
         }

         Vec3 coordVec = move.toDoubleVector();
         this.applyYamatoNoGravity(entitypatch, coordVec);
         return coordVec;
      } else {
         Vec3 coordVec = super.getCoordVector(entitypatch, animation);
         this.applyYamatoNoGravity(entitypatch, coordVec);
         return coordVec;
      }
   }

   private void applyYamatoNoGravity(LivingEntityPatch<?> entitypatch, Vec3 coordVec) {
      TimePairList yamatoNoGravity = (TimePairList)this.getProperty(YAMATO_NO_GRAVITY_TIME).orElse(null);
      if (yamatoNoGravity != null) {
         AnimationPlayer player = DMCAnimationUtils.getPlayerFor(entitypatch, this.getAccessor());
         if (player != null && yamatoNoGravity.isTimeInPairs(player.getElapsedTime())) {
            LivingEntity entity = (LivingEntity)entitypatch.getOriginal();
            Vec3 motion = entity.m_20184_();
            float aerialCount = getAerialActionCount(entitypatch);
            if (aerialCount <= 3.0F) {
               entity.m_20334_(motion.f_82479_, 0.0, motion.f_82481_);
            } else {
               float gravityPercent = Math.min(3.0F, (aerialCount - 2.0F) * 0.2F);
               double gravity = entity.m_21133_((Attribute)ForgeMod.ENTITY_GRAVITY.get());
               entity.m_20334_(motion.f_82479_, -gravity * (double)gravityPercent, motion.f_82481_);
            }
         }
      }
   }

   public void tick(LivingEntityPatch<?> entitypatch) {
      super.tick(entitypatch);
      if (entitypatch.isLogicalClient()) {
         AnimationPlayer player = DMCAnimationUtils.getPlayerFor(entitypatch, this.getAccessor());
         if (player != null) {
            float prev = player.getPrevElapsedTime();
            float cur = player.getElapsedTime();
            YamatoAttackAnimation.SheathConfig cfg = SHEATH_CONFIGS.get(this.getAccessor());
            if (cfg != null && prev < cfg.time() && cur >= cfg.time()) {
               YamatoSheathEvent.Client event = new YamatoSheathEvent.Client(entitypatch, cfg.time(), cfg.sound(), this.getAccessor());
               MinecraftForge.EVENT_BUS.post(event);
            }
         }
      }
   }

   private static float getAerialActionCount(LivingEntityPatch<?> entitypatch) {
      if (entitypatch instanceof PlayerPatch<?> playerPatch) {
         SkillContainer container = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
         if (container == null) {
            return 0.0F;
         } else {
            SkillDataKey<Float> key = (SkillDataKey<Float>)DMCSkillDataKeys.AERIAL_ACTION_COUNT.get();
            return !container.getDataManager().hasData(key) ? 0.0F : (Float)container.getDataManager().getDataValue(key);
         }
      } else {
         return 0.0F;
      }
   }

   public static void setAerialActionCount(LivingEntityPatch<?> entitypatch, float value) {
      if (entitypatch instanceof PlayerPatch<?> playerPatch) {
         SkillContainer container = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
         if (container != null) {
            SkillDataKey<Float> key = (SkillDataKey<Float>)DMCSkillDataKeys.AERIAL_ACTION_COUNT.get();
            if (container.getDataManager().hasData(key)) {
               container.getDataManager().setDataSync(key, value);
            }
         }
      }
   }

   public YamatoAttackAnimation(
      float transitionTime,
      AnimationAccessor<? extends BasicAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float play_speed,
      float damageMulti,
      Supplier<? extends StaticAnimation> hitAnimation,
      CustomStunPhase... phases
   ) {
      super(transitionTime, accessor, armature, play_speed, damageMulti, hitAnimation, phases);
   }

   public YamatoAttackAnimation(
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
   }

   public YamatoAttackAnimation(
      float transitionTime,
      AnimationAccessor<? extends BasicAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float play_speed,
      float damageMulti,
      AvalonPhase... phases
   ) {
      super(transitionTime, accessor, armature, play_speed, damageMulti, phases);
   }

   public YamatoAttackAnimation(
      float transitionTime,
      AnimationAccessor<? extends BasicAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float damageMulti,
      AvalonPhase... phases
   ) {
      super(transitionTime, accessor, armature, damageMulti, phases);
   }

   public YamatoAttackAnimation(
      float transitionTime, AnimationAccessor<? extends BasicAttackAnimation> accessor, AssetAccessor<? extends Armature> armature, AvalonPhase... phases
   ) {
      super(transitionTime, accessor, armature, phases);
   }

   public YamatoAttackAnimation(
      float convertTime, String path, AssetAccessor<? extends Armature> armature, float play_speed, float damageMulti, AvalonPhase... phases
   ) {
      super(convertTime, path, armature, play_speed, damageMulti, phases);
   }

   public YamatoAttackAnimation(
      float transitionTime,
      AnimationAccessor<? extends BasicAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float play_speed,
      float damageMulti,
      CustomStunPhase... phases
   ) {
      super(transitionTime, accessor, armature, play_speed, damageMulti, phases);
   }

   public static MoveCoordGetter modelCoord(float scale) {
      return (animation, entitypatch, coord, prevElapsedTime, elapsedTime) -> {
         JointTransform oJt = coord.getInterpolatedTransform(prevElapsedTime);
         JointTransform jt = coord.getInterpolatedTransform(elapsedTime);
         Vec4f prevpos = new Vec4f(oJt.translation());
         Vec4f currentpos = new Vec4f(jt.translation());
         OpenMatrix4f rotationTransform = entitypatch.getModelMatrix(1.0F).removeTranslation().removeScale();
         OpenMatrix4f localTransform = entitypatch.getArmature().searchJointByName("Root").getLocalTransform().removeTranslation();
         rotationTransform.mulBack(localTransform);
         currentpos.transform(rotationTransform);
         prevpos.transform(rotationTransform);
         boolean hasNoGravity = ((LivingEntity)entitypatch.getOriginal()).m_20068_();
         boolean moveVertical = animation.getProperty(ActionAnimationProperty.MOVE_VERTICAL).orElse(false)
            || animation.getProperty(ActionAnimationProperty.COORD).isPresent();
         float dx = prevpos.x - currentpos.x;
         float dy = !moveVertical && !hasNoGravity ? 0.0F : currentpos.y - prevpos.y;
         float dz = prevpos.z - currentpos.z;
         dx = Math.abs(dx) > 1.0E-4F ? dx : 0.0F;
         dz = Math.abs(dz) > 1.0E-4F ? dz : 0.0F;
         return new Vec3f(dx * scale, dy, dz * scale);
      };
   }

   public static MoveCoordGetter modelCoord(float hScale, float vScale) {
      return (animation, entitypatch, coord, prevElapsedTime, elapsedTime) -> {
         JointTransform oJt = coord.getInterpolatedTransform(prevElapsedTime);
         JointTransform jt = coord.getInterpolatedTransform(elapsedTime);
         Vec4f prevpos = new Vec4f(oJt.translation());
         Vec4f currentpos = new Vec4f(jt.translation());
         OpenMatrix4f rotationTransform = entitypatch.getModelMatrix(1.0F).removeTranslation().removeScale();
         OpenMatrix4f localTransform = entitypatch.getArmature().searchJointByName("Root").getLocalTransform().removeTranslation();
         rotationTransform.mulBack(localTransform);
         currentpos.transform(rotationTransform);
         prevpos.transform(rotationTransform);
         boolean hasNoGravity = ((LivingEntity)entitypatch.getOriginal()).m_20068_();
         boolean moveVertical = animation.getProperty(ActionAnimationProperty.MOVE_VERTICAL).orElse(false)
            || animation.getProperty(ActionAnimationProperty.COORD).isPresent();
         float dx = prevpos.x - currentpos.x;
         float dy = !moveVertical && !hasNoGravity ? 0.0F : (currentpos.y - prevpos.y) * vScale;
         float dz = prevpos.z - currentpos.z;
         dx = Math.abs(dx) > 1.0E-4F ? dx * hScale : 0.0F;
         dz = Math.abs(dz) > 1.0E-4F ? dz * hScale : 0.0F;
         return new Vec3f(dx, dy, dz);
      };
   }

   public static MoveCoordGetter modelCoordWithInput(float inputScale) {
      return (animation, entitypatch, coord, prevElapsedTime, elapsedTime) -> {
         Vec3f move = MoveCoordFunctions.MODEL_COORD.get(animation, entitypatch, coord, prevElapsedTime, elapsedTime);
         return applyMovementInputBias(entitypatch, move, inputScale);
      };
   }

   private static Vec3f applyMovementInputBias(LivingEntityPatch<?> entitypatch, Vec3f move, float inputScale) {
      if (inputScale <= 0.0F) {
         return move;
      } else {
         LivingEntity livingEntity = (LivingEntity)entitypatch.getOriginal();
         if (livingEntity instanceof Player player) {
            boolean up = isInputDown(player, 0);
            boolean down = isInputDown(player, 1);
            boolean left = isInputDown(player, 2);
            boolean right = isInputDown(player, 3);
            float forwardAxis = (up ? 1.0F : 0.0F) - (down ? 1.0F : 0.0F);
            float strafeAxis = (left ? 1.0F : 0.0F) - (right ? 1.0F : 0.0F);
            if (forwardAxis == 0.0F && strafeAxis == 0.0F) {
               return move;
            } else {
               Vec3 forwardVec = MathUtils.getVectorForRotation(0.0F, entitypatch.getYRot());
               Vec3 rightVec = MathUtils.getVectorForRotation(0.0F, entitypatch.getYRot() + 90.0F);
               Vec3 inputVec = forwardVec.m_82490_((double)forwardAxis).m_82549_(rightVec.m_82490_((double)(-strafeAxis)));
               if (inputVec.m_82556_() > 1.0E-6) {
                  inputVec = inputVec.m_82541_().m_82490_((double)inputScale);
                  move.add((float)inputVec.f_82479_, 0.0F, (float)inputVec.f_82481_);
               }

               return move;
            }
         } else {
            return move;
         }
      }
   }

   private static boolean isInputDown(Player player, int bit) {
      return player.m_7578_() ? PlayerInputState.isLocalDown(bit) : PlayerInputState.isRemoteDown(player, bit);
   }

   public static MoveCoordGetter comboModelCoordTargetDash(float hScale, float vScale, float distanceBoostFactor) {
      return comboModelCoordTargetDash(hScale, vScale, distanceBoostFactor, false);
   }

   public static MoveCoordGetter comboModelCoordTargetDash(float hScale, float vScale, float distanceBoostFactor, boolean passThrough) {
      return (animation, entitypatch, coord, prevElapsedTime, elapsedTime) -> {
         JointTransform oJt = coord.getInterpolatedTransform(prevElapsedTime);
         JointTransform jt = coord.getInterpolatedTransform(elapsedTime);
         Vec4f prevpos = new Vec4f(oJt.translation());
         Vec4f currentpos = new Vec4f(jt.translation());
         OpenMatrix4f rotationTransform = entitypatch.getModelMatrix(1.0F).removeTranslation().removeScale();
         OpenMatrix4f localTransform = entitypatch.getArmature().searchJointByName("Root").getLocalTransform().removeTranslation();
         rotationTransform.mulBack(localTransform);
         currentpos.transform(rotationTransform);
         prevpos.transform(rotationTransform);
         boolean hasNoGravity = ((LivingEntity)entitypatch.getOriginal()).m_20068_();
         boolean moveVertical = animation.getProperty(ActionAnimationProperty.MOVE_VERTICAL).orElse(false)
            || animation.getProperty(ActionAnimationProperty.COORD).isPresent();
         float dx = prevpos.x - currentpos.x;
         float dy = !moveVertical && !hasNoGravity ? 0.0F : (currentpos.y - prevpos.y) * vScale;
         float dz = prevpos.z - currentpos.z;
         dx = Math.abs(dx) > 1.0E-4F ? dx * hScale : 0.0F;
         dz = Math.abs(dz) > 1.0E-4F ? dz * hScale : 0.0F;
         if (distanceBoostFactor > 0.0F && Math.abs(dz) > 1.0E-4F) {
            LivingEntity target = entitypatch.getTarget();
            if (target != null) {
               double distance = (double)((LivingEntity)entitypatch.getOriginal()).m_20270_(target);
               float animForward = Math.abs(dz);
               float targetForward;
               if (passThrough) {
                  targetForward = (float)(distance * (double)distanceBoostFactor);
               } else {
                  float entityRadius = (target.m_20205_() + ((LivingEntity)entitypatch.getOriginal()).m_20205_()) * 0.7F;
                  double effectiveDistance = Math.max(distance - (double)entityRadius, 0.0);
                  targetForward = (float)Math.min(effectiveDistance, (double)(animForward * 3.0F));
               }

               float boostedForward = animForward + (targetForward - animForward) * distanceBoostFactor;
               dz = dz > 0.0F ? boostedForward : -boostedForward;
            }
         }

         return new Vec3f(dx, dy, dz);
      };
   }

   public static record SheathConfig(float time, RegistryObject<SoundEvent> sound) {
   }
}
