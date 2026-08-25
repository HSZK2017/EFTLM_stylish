package com.hm.efn.gameasset;

import com.guhao.vix.camera.CameraAnimation;
import com.guhao.vix.util.OjangUtils;
import com.hm.efn.EFNCommonConfig;
import com.hm.efn.animations.property.EFNAnimationProperties;
import com.hm.efn.animations.property.EFNAnimationProperties.SpecialPhase;
import com.hm.efn.animations.types.DeferredDamageAttackAnimation;
import com.hm.efn.animations.types.vfx.VFXBBActionAnimation;
import com.hm.efn.animations.types.vfx.VFXBBAvalonAttackAnimation;
import com.hm.efn.animations.types.yamato.SummonedSwordAnimation;
import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.entity.effect.SummonedSwordEntity_In;
import com.hm.efn.entity.effect.SummonedSwordPatch_In;
import com.hm.efn.entity.effect.SinSummonedSwordEntity.SinSummonedSwordPatch;
import com.hm.efn.gameasset.animations.EFNBroadBladeAnimations;
import com.hm.efn.gameasset.animations.EFNClawAnimations;
import com.hm.efn.gameasset.animations.EFNClawAnimations_N;
import com.hm.efn.gameasset.animations.EFNDodgeAnimations;
import com.hm.efn.gameasset.animations.EFNDualSwordAnimations;
import com.hm.efn.gameasset.animations.EFNExsiliumgladiusAnimations;
import com.hm.efn.gameasset.animations.EFNFalchionAnimations;
import com.hm.efn.gameasset.animations.EFNGreatSwordAnimations;
import com.hm.efn.gameasset.animations.EFNHfBladeAnimations;
import com.hm.efn.gameasset.animations.EFNLanceAnimations;
import com.hm.efn.gameasset.animations.EFNMurasamaAnimations;
import com.hm.efn.gameasset.animations.EFNScytheAnimations;
import com.hm.efn.gameasset.animations.EFNSekiroAnimations;
import com.hm.efn.gameasset.animations.EFNShortSwordAnimations;
import com.hm.efn.gameasset.animations.EFNSkillAnimations;
import com.hm.efn.gameasset.animations.EFNStunAnimations;
import com.hm.efn.gameasset.animations.EFNSwordAnimations;
import com.hm.efn.gameasset.animations.EFNTachiAnimations;
import com.hm.efn.gameasset.animations.EFNThornWheelAnimations;
import com.hm.efn.gameasset.animations.EFNYamatoAnimations;
import com.hm.efn.gameasset.animations.EFNZansetsuAnimations;
import com.hm.efn.gameasset.animations.EFNZansetsuAnimations_B;
import com.hm.efn.particle.EFNParticles;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.util.yamato.DMC_V_JC_Client;
import com.hm.efn.util.yamato.DMC_V_JC_Server;
import com.merlin204.avalon.epicfight.animations.AutoDiscardActionAnimation;
import com.merlin204.avalon.epicfight.animations.AutoDiscardAttackAnimation;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation.AvalonPhase;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import com.merlin204.avalon.util.AvalonEventUtils;
import com.p1nero.invincible.api.animation.types.MultiPhaseAttackAnimation;
import com.p1nero.invincible.damagesource.InvincibleDamageTypeTags;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.AnimationManager.AnimationRegistryEvent;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InPeriodEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationEvent.SimpleEvent;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.PlaybackSpeedModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.property.MoveCoordFunctions.MoveCoordGetter;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DodgeAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.api.utils.math.Vec4f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.StunType;

@EventBusSubscriber(
   modid = "efn",
   bus = Bus.MOD
)
public class EFNAnimations {
   public static final Collider VFX_COLL = new OBBCollider(2.0, 2.0, 2.0, 0.0, 1.0, 0.0);
   public static final Collider SUMMONED_SWORD_COLL = new OBBCollider(0.5, 1.0, 0.5, 0.0, 0.0, 0.0);
   public static final Collider SIN_SUMMONED_SWORD_COLL = new MultiOBBCollider(10, 0.4, 1.2, 0.4, 0.0, 0.0, 0.0);
   public static final Collider BLOOD_SLASH_ONLY = new OBBCollider(1.3, 0.3, 1.0, 0.0, 0.0, -0.5);
   public static final Collider CRIMSON_SLASH_ONLY = new OBBCollider(0.8, 0.2, 0.45, 0.0, 0.0, -0.3);
   public static final Collider DMC_JC = new OBBCollider(10.0, 4.5, 10.0, 0.0, 3.2, 0.0);
   public static final PlaybackSpeedModifier ATTACK_SPEED_CAP_DUAL = (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> calculateWeaponSpeedWithCap(
         self, entitypatch, 1.2F, 1.7F
      );
   public static final PlaybackSpeedModifier ATTACK_SPEED_CAP_SHORTSWORD = (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> calculateWeaponSpeedWithCap(
         self, entitypatch, 1.35F, 1.45F
      );
   public static final PlaybackSpeedModifier ATTACK_SPEED_CAP_SWORD = (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> calculateWeaponSpeedWithCap(
         self, entitypatch, 1.25F, 1.8F
      );
   public static final PlaybackSpeedModifier ATTACK_SPEED_CAP_TACHI = (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> calculateWeaponSpeedWithCap(
         self, entitypatch, 1.3F, 0.95F
      );
   public static final PlaybackSpeedModifier ATTACK_SPEED_CAP_RUIN = (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> calculateWeaponSpeedWithCap(
         self, entitypatch, 1.25F, 0.9F
      );
   public static final PlaybackSpeedModifier ATTACK_SPEED_CAP_MEEN = (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> calculateWeaponSpeedWithCap(
         self, entitypatch, 1.45F, 0.85F
      );
   public static final PlaybackSpeedModifier ATTACK_SPEED_CAP_CLAW = (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> calculateWeaponSpeedWithCap(
         self, entitypatch, 1.4F, 0.9F
      );
   public static final PlaybackSpeedModifier ATTACK_SPEED_CAP_EXSILIUMGLADIUS = (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> calculateWeaponSpeedWithCap(
         self, entitypatch, 2.0F, 0.6F
      );
   public static final Function<DamageSource, ResultType> INVINCIBLE_SOURCE_VALIDATOR = damagesource -> damagesource.m_7639_() != null
            && !damagesource.m_269533_(DamageTypeTags.f_268738_)
         ? ResultType.MISSED
         : ResultType.SUCCESS;
   public static final TagKey<DamageType> EFN_CRIMSON_SLASH = EFNExtraDamageInstance.createDamageType("efn_crimson_slash");
   public static AnimationAccessor<VFXBBActionAnimation> SOUL_HUNT;
   public static AnimationAccessor<AutoDiscardAttackAnimation> BLOOD_SLASH;
   public static AnimationAccessor<AutoDiscardAttackAnimation> CRIMSON_SLASH;
   public static AnimationAccessor<AutoDiscardAttackAnimation> CRIMSON_SLASH_ANTI;
   public static AnimationAccessor<StaticAnimation> SUMMONED_SWORD_IDLE;
   public static AnimationAccessor<StaticAnimation> SUMMONED_SWORD_CIRCLE;
   public static AnimationAccessor<SummonedSwordAnimation> SUMMONED_SWORD_SIN;
   public static AnimationAccessor<MultiPhaseAttackAnimation> SUMMONED_SWORD_IN;
   public static AnimationAccessor<MultiPhaseAttackAnimation> SUMMONED_SWORD_OUT;
   public static AnimationAccessor<VFXBBActionAnimation> MURASAMA_CHARGE;
   public static AnimationAccessor<VFXBBActionAnimation> MURASAMA_CHARGE_WAVE;
   public static AnimationAccessor<VFXBBActionAnimation> MURASAMA_CHARGE_CIRCLE;
   public static AnimationAccessor<VFXBBActionAnimation> MURASAMA_SLASH;
   public static AnimationAccessor<VFXBBAvalonAttackAnimation> DRAGON_FLASH_SLASH;
   public static AnimationAccessor<VFXBBAvalonAttackAnimation> YMD_EFFECT1;
   public static AnimationAccessor<VFXBBAvalonAttackAnimation> YMD_EFFECT2;
   public static AnimationAccessor<AutoDiscardActionAnimation> FIREWIND_ONE;
   public static AnimationAccessor<StaticAnimation> FIREWIND_TWO;
   public static AnimationAccessor<StaticAnimation> SECLUDED_0;
   public static AnimationAccessor<AutoDiscardActionAnimation> SECLUDED_1;
   public static AnimationAccessor<AutoDiscardActionAnimation> SECLUDED_2;
   public static AnimationAccessor<AutoDiscardActionAnimation> CO_TACHI_SLASH_ONE;
   public static AnimationAccessor<StaticAnimation> CO_TACHI_SLASH_IDLE;
   public static AnimationAccessor<StaticAnimation> TRAIL_IDLE;
   public static AnimationAccessor<AutoDiscardAttackAnimation> TRAIL;
   public static AnimationAccessor<StaticAnimation> ARC_IDLE;
   public static AnimationAccessor<AvalonAttackAnimation> ARC_AUTO1;
   public static AnimationAccessor<AvalonAttackAnimation> ARC_AUTO2;
   public static AnimationAccessor<AvalonAttackAnimation> ARC_AUTO3;
   public static AnimationAccessor<AvalonAttackAnimation> ARC_TACHI_AUTO1;
   public static AnimationAccessor<StaticAnimation> SWORD_RIDE_IDLE;
   public static AnimationAccessor<AvalonAttackAnimation> SWORD_RIDE_AUTO1;
   public static AnimationAccessor<AvalonAttackAnimation> SWORD_RIDE_AUTO2;
   public static AnimationAccessor<ActionAnimation> COOL;
   public static AnimationAccessor<DeferredDamageAttackAnimation> DMC5_V_JC;
   public static CameraAnimation DMC;
   public static CameraAnimation ZANDATSU;
   public static CameraAnimation ZANDATSU_AIR;
   public static CameraAnimation BROADBLADE_EXECUTE;
   public static final MoveCoordGetter DRAGON_FLASH_COLL_MODEL_COORD = (animation, entitypatch, coord, prevElapsedTime, elapsedTime) -> {
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
      return new Vec3f(dx * 6.0F, dy, dz * 6.0F);
   };

   public static void LoadCamAnims() {
      DMC = CameraAnimation.load(OjangUtils.newRL("efn", "camera_animation/yamato_judgementcut_end_camera.json"));
      ZANDATSU = CameraAnimation.load(OjangUtils.newRL("efn", "camera_animation/zandatsu.json"));
      ZANDATSU_AIR = CameraAnimation.load(OjangUtils.newRL("efn", "camera_animation/zandatsu_air.json"));
      BROADBLADE_EXECUTE = CameraAnimation.load(OjangUtils.newRL("efn", "camera_animation/broadblade_execute.json"));
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void registerAnimations(AnimationRegistryEvent event) {
      event.newBuilder("efn", EFNAnimations::build);
   }

   public static void build(AnimationBuilder builder) {
      EFNYamatoAnimations.build(builder);
      EFNExsiliumgladiusAnimations.build(builder);
      EFNClawAnimations.build(builder);
      EFNClawAnimations_N.build(builder);
      EFNDualSwordAnimations.build(builder);
      EFNSwordAnimations.build(builder);
      EFNShortSwordAnimations.build(builder);
      EFNGreatSwordAnimations.build(builder);
      EFNLanceAnimations.build(builder);
      EFNTachiAnimations.build(builder);
      EFNSekiroAnimations.build(builder);
      EFNScytheAnimations.build(builder);
      EFNMurasamaAnimations.build(builder);
      EFNHfBladeAnimations.build(builder);
      EFNThornWheelAnimations.build(builder);
      EFNFalchionAnimations.build(builder);
      EFNBroadBladeAnimations.build(builder);
      EFNDodgeAnimations.build(builder);
      EFNSkillAnimations.build(builder);
      EFNZansetsuAnimations.build(builder);
      EFNZansetsuAnimations_B.build(builder);
      EFNStunAnimations.build(builder);
      DMC5_V_JC = builder.nextAccessor(
         "biped/yamato_judgementcut_end",
         accessor -> (DeferredDamageAttackAnimation)new DeferredDamageAttackAnimation(
                  0.1F,
                  accessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(0.0F, 1.8F, 2.0F, 5.5F, 2.5F, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, DMC_JC)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(0.0F)),
                     new Phase(2.5F, 4.5F, 4.7F, 5.5F, 6.083F, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, DMC_JC)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.0F))
                  }
               )
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(2.1474836E9F))
               .addProperty(
                  AttackPhaseProperty.SOURCE_TAG,
                  Set.of(
                     EpicFightDamageTypeTags.WEAPON_INNATE,
                     EpicFightDamageTypeTags.UNBLOCKALBE,
                     EpicFightDamageTypeTags.GUARD_PUNCTURE,
                     EpicFightDamageTypeTags.IS_MELEE
                  )
               )
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.0F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addProperty(EFNAnimationProperties.MOVE_ROOT_PHASE, new SpecialPhase(0.0F, 5.35F))
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 4.48F}))
               .addProperty(EFNAnimationProperties.INVISIBLE_PHASE, new SpecialPhase(1.8F, 2.73F))
               .addProperty(
                  StaticAnimationProperty.ON_BEGIN_EVENTS,
                  List.of(
                     SimpleEvent.create((ep, anim, objs) -> DMC_V_JC_Client.prev(ep), Side.CLIENT),
                     SimpleEvent.create((ep, anim, objs) -> DMC_V_JC_Server.prev(ep), Side.SERVER)
                  )
               )
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.0F,
                        (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                              .m_7292_(new MobEffectInstance((MobEffect)EFNMobEffectRegistry.KNOCKBACKRESISTANT.get(), 120, 1, false, false, false)),
                        Side.BOTH
                     ),
                     InPeriodEvent.create(1.8F, 2.0F, (ep, anim, objs) -> DMC_V_JC_Server.HandleAtk1(ep), Side.SERVER),
                     InTimeEvent.create(1.85F, (ep, anim, objs) -> DMC_V_JC_Server.post1(ep), Side.SERVER),
                     InTimeEvent.create(1.0F, (ep, anim, objs) -> DMC_V_JC_Server.post2(ep), Side.SERVER),
                     InTimeEvent.create(1.3F, (ep, anim, objs) -> DMC_V_JC_Server.post3(ep), Side.SERVER),
                     InTimeEvent.create(4.5F, (ep, anim, objs) -> DMC_V_JC_Server.post4(ep), Side.SERVER),
                     InPeriodEvent.create(4.5F, 4.7F, (ep, anim, objs) -> DMC_V_JC_Server.postAttack(ep), Side.SERVER),
                     InTimeEvent.create(1.6F, (ep, anim, objs) -> DMC_V_JC_Client.HandleAtk1(ep), Side.CLIENT),
                     InTimeEvent.create(1.8F, (ep, anim, objs) -> DMC_V_JC_Client.post1(ep), Side.CLIENT),
                     InTimeEvent.create(2.25F, (ep, anim, objs) -> DMC_V_JC_Client.post2(ep), Side.CLIENT),
                     InTimeEvent.create(1.85F, (ep, anim, objs) -> DMC_V_JC_Client.post3(ep), Side.CLIENT),
                     InTimeEvent.create(4.5F, (ep, anim, objs) -> DMC_V_JC_Client.post4(ep), Side.CLIENT)
                  }
               )
               .newTimePair(0.0F, 2.1474836E9F)
               .addState(EntityState.ATTACK_RESULT, INVINCIBLE_SOURCE_VALIDATOR)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
      );
      COOL = builder.nextAccessor(
         "biped/perfect_dodge",
         accessor -> (ActionAnimation)new ActionAnimation(0.1F, accessor, Armatures.BIPED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.15F)
               .newTimePair(0.0F, 2.1474836E9F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, DodgeAnimation.DODGEABLE_SOURCE_VALIDATOR)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.0F,
                        (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                              .m_7292_(new MobEffectInstance(MobEffects.f_19597_, 30, 6, false, false, false)),
                        Side.BOTH
                     )
                  }
               )
      );
      ArmatureAccessor<Armature> MurasamaCharge_Wave = ArmatureAccessor.create("efn", "entity/effect/murasama_charge_wave", Armature::new);
      ArmatureAccessor<Armature> MurasamaCharge_Circle = ArmatureAccessor.create("efn", "entity/effect/murasama_charge_circle", Armature::new);
      ArmatureAccessor<Armature> MurasamaCharge = ArmatureAccessor.create("efn", "entity/effect/murasama_charge", Armature::new);
      ArmatureAccessor<Armature> MurasamaSlash = ArmatureAccessor.create("efn", "entity/effect/murasama_slash", Armature::new);
      MURASAMA_CHARGE_WAVE = builder.nextAccessor(
         "effect/murasama/murasama_charge_wave",
         accessor -> (VFXBBActionAnimation)new VFXBBActionAnimation(0.0F, accessor, MurasamaCharge_Wave, 2.4F)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 2.1474836E9F}))
      );
      MURASAMA_CHARGE_CIRCLE = builder.nextAccessor(
         "effect/murasama/murasama_charge_circle",
         accessor -> (VFXBBActionAnimation)new VFXBBActionAnimation(0.0F, accessor, MurasamaCharge_Circle, 1.65F)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 2.1474836E9F}))
      );
      MURASAMA_CHARGE = builder.nextAccessor(
         "effect/murasama/murasama_charge",
         accessor -> (VFXBBActionAnimation)new VFXBBActionAnimation(0.0F, accessor, MurasamaCharge, 2.1F)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 2.1474836E9F}))
      );
      MURASAMA_SLASH = builder.nextAccessor(
         "effect/murasama/murasama_slash",
         accessor -> (VFXBBActionAnimation)new VFXBBActionAnimation(0.0F, accessor, MurasamaSlash, 1.0F)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 2.1474836E9F}))
      );
      ArmatureAccessor<Armature> Soul_Hunt = ArmatureAccessor.create("efn", "entity/effect/soul_hunt", Armature::new);
      SOUL_HUNT = builder.nextAccessor(
         "effect/soul_hunt",
         accessor -> (VFXBBActionAnimation)new VFXBBActionAnimation(0.0F, accessor, Soul_Hunt, 1.0F)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 2.1474836E9F}))
      );
      ArmatureAccessor<Armature> FireWind = ArmatureAccessor.create("efn", "entity/effect/firewind", Armature::new);
      FIREWIND_ONE = builder.nextAccessor(
         "effect/firewind/firewind_one",
         accessor -> (AutoDiscardActionAnimation)new AutoDiscardActionAnimation(0.0F, accessor, FireWind, 0.75F)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 2.1474836E9F}))
      );
      FIREWIND_TWO = builder.nextAccessor(
         "effect/firewind/firewind_two",
         accessor -> new StaticAnimation(0.0F, true, accessor, FireWind)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 2.1474836E9F}))
      );
      ArmatureAccessor<Armature> CoTachiSlash = ArmatureAccessor.create("efn", "entity/effect/co_tachi_slash", Armature::new);
      CO_TACHI_SLASH_ONE = builder.nextAccessor(
         "effect/co_tachi_slash/co_tachi_slash_1",
         accessor -> (AutoDiscardActionAnimation)new AutoDiscardActionAnimation(0.0F, accessor, CoTachiSlash, 1.3F)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 2.1474836E9F}))
      );
      CO_TACHI_SLASH_IDLE = builder.nextAccessor(
         "effect/co_tachi_slash/co_tachi_slash_idle",
         accessor -> new StaticAnimation(0.0F, true, accessor, CoTachiSlash)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 2.1474836E9F}))
      );
      ArmatureAccessor<Armature> dragon_flash_slash = ArmatureAccessor.create("efn", "entity/effect/dragon_flash_slash", Armature::new);
      DRAGON_FLASH_SLASH = builder.nextAccessor(
         "effect/sekiro/dragon_flash_slash",
         accessor -> (VFXBBAvalonAttackAnimation)new VFXBBAvalonAttackAnimation(
                  0.0F,
                  accessor,
                  dragon_flash_slash,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        1, 2147483646, Integer.MAX_VALUE, InteractionHand.MAIN_HAND, 2.5F, 2.5F, dragon_flash_slash.get().searchJointByName("Root"), VFX_COLL
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackAnimationProperty.COORD_GET, DRAGON_FLASH_COLL_MODEL_COORD)
               .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 10)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.2F)
      );
      ArmatureAccessor<Armature> Ymd1 = ArmatureAccessor.create("efn", "entity/effect/ymd1", Armature::new);
      YMD_EFFECT1 = builder.nextAccessor(
         "effect/ymd/ymds1",
         accessor -> (VFXBBAvalonAttackAnimation)new VFXBBAvalonAttackAnimation(
                  0.0F,
                  accessor,
                  Ymd1,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(10, 20, 30, InteractionHand.MAIN_HAND, 1.0F, 1.0F, Ymd1.get().searchJointByName("Root"), VFX_COLL),
                     AvalonAnimationUtils.createSimplePhase(30, 40, 50, InteractionHand.MAIN_HAND, 1.0F, 1.0F, Ymd1.get().searchJointByName("Root"), VFX_COLL)
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
      );
      ArmatureAccessor<Armature> Ymd2 = ArmatureAccessor.create("efn", "entity/effect/ymd2", Armature::new);
      YMD_EFFECT2 = builder.nextAccessor(
         "effect/ymd/ymds2",
         accessor -> (VFXBBAvalonAttackAnimation)new VFXBBAvalonAttackAnimation(
                  0.0F,
                  accessor,
                  Ymd2,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(10, 20, 30, InteractionHand.MAIN_HAND, 1.0F, 1.0F, Ymd2.get().searchJointByName("Root"), VFX_COLL),
                     AvalonAnimationUtils.createSimplePhase(30, 40, 50, InteractionHand.MAIN_HAND, 1.0F, 1.0F, Ymd2.get().searchJointByName("Root"), VFX_COLL)
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
      );
      ArmatureAccessor<Armature> BloodSlash = ArmatureAccessor.create("efn", "entity/effect/blood_slash", Armature::new);
      BLOOD_SLASH = builder.nextAccessor(
         "effect/blood_slash",
         accessor -> (AutoDiscardAttackAnimation)new AutoDiscardAttackAnimation(
                  0.0F,
                  accessor,
                  BloodSlash,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        2,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        InteractionHand.MAIN_HAND,
                        0.3F,
                        0.3F,
                        BloodSlash.get().searchJointByName("xyz"),
                        BLOOD_SLASH_ONLY
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.BLOOD_HIT)
               .addProperty(AttackPhaseProperty.SWING_SOUND, EFNSounds.NOSOUND.get())
      );
      ArmatureAccessor<Armature> CrimsonSlash = ArmatureAccessor.create("efn", "entity/effect/crimson_slash", Armature::new);
      CRIMSON_SLASH = builder.nextAccessor(
         "effect/crimson_slash",
         accessor -> (AutoDiscardAttackAnimation)new AutoDiscardAttackAnimation(
                  0.0F,
                  accessor,
                  CrimsonSlash,
                  1.65F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        2,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        InteractionHand.MAIN_HAND,
                        0.3F,
                        1.0F,
                        CrimsonSlash.get().searchJointByName("Root"),
                        CRIMSON_SLASH_ONLY
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
               .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EFN_CRIMSON_SLASH))
      );
      CRIMSON_SLASH_ANTI = builder.nextAccessor(
         "effect/crimson_slash_anti",
         accessor -> (AutoDiscardAttackAnimation)new AutoDiscardAttackAnimation(
                  0.0F,
                  accessor,
                  CrimsonSlash,
                  1.65F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        2,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        InteractionHand.MAIN_HAND,
                        0.3F,
                        0.3F,
                        CrimsonSlash.get().searchJointByName("Root"),
                        CRIMSON_SLASH_ONLY
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
               .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EFN_CRIMSON_SLASH))
      );
      ArmatureAccessor<Armature> Trail = ArmatureAccessor.create("efn", "entity/effect/trail", Armature::new);
      TRAIL_IDLE = builder.nextAccessor(
         "effect/trail/trail_idle",
         accessor -> new StaticAnimation(0.0F, true, accessor, Trail)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 2.1474836E9F}))
      );
      TRAIL = builder.nextAccessor(
         "effect/trail/trail",
         accessor -> (AutoDiscardAttackAnimation)new AutoDiscardAttackAnimation(
                  0.05F,
                  accessor,
                  Trail,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        8, 30, 31, InteractionHand.MAIN_HAND, 1.0F, 1.0F, Trail.get().searchJointByName("hurt"), ColliderPreset.TACHI
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
      );
      ArmatureAccessor<Armature> SummonedSword = ArmatureAccessor.create("efn", "entity/effect/summoned_sword", Armature::new);
      ArmatureAccessor<Armature> SummonedSword_Circle = ArmatureAccessor.create("efn", "entity/effect/summoned_sword_circle", Armature::new);
      SUMMONED_SWORD_IDLE = builder.nextAccessor(
         "effect/summoned_sword/summoned_sword_idle",
         accessor -> new StaticAnimation(0.1F, true, accessor, SummonedSword).addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
      );
      SUMMONED_SWORD_CIRCLE = builder.nextAccessor(
         "effect/summoned_sword/summoned_sword_in_idle",
         accessor -> new StaticAnimation(0.1F, true, accessor, SummonedSword_Circle).addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
      );
      SUMMONED_SWORD_SIN = builder.nextAccessor(
         "effect/summoned_sword/summoned_sword_sin_idle",
         accessor -> (SummonedSwordAnimation)new SummonedSwordAnimation(
                  0.01F,
                  accessor,
                  SummonedSword,
                  0.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        0,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        InteractionHand.MAIN_HAND,
                        0.1F,
                        0.01F,
                        SummonedSword.get().searchJointByName("Root"),
                        SIN_SUMMONED_SWORD_COLL
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_ROD.get())
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
               .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.ARC_HIT)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
               .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.multiplier(0.01F))
               .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(InvincibleDamageTypeTags.NOT_CHARGE, SinSummonedSwordPatch.SUMMONED_SWORD_DAMAGE))
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 0.1F)
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleSound(0, SoundEvents.f_11852_, 1.0F, 1.0F)})
      );
      SUMMONED_SWORD_IN = builder.nextAccessor(
         "effect/summoned_sword/summoned_sword_in",
         accessor -> (MultiPhaseAttackAnimation)new MultiPhaseAttackAnimation(
                  0.0F,
                  accessor,
                  SummonedSword_Circle,
                  new Phase[]{
                     new Phase(
                           0.0F,
                           0.55F,
                           2.1474836E9F,
                           2.1474836E9F,
                           2.1474836E9F,
                           SummonedSword_Circle.get().searchJointByName("Root.001"),
                           SIN_SUMMONED_SWORD_COLL
                        )
                        .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.ARC_HIT)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
                        .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1.0F))
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
                        .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(InvincibleDamageTypeTags.NOT_CHARGE)),
                     new Phase(
                           0.0F,
                           0.55F,
                           2.1474836E9F,
                           2.1474836E9F,
                           2.1474836E9F,
                           SummonedSword_Circle.get().searchJointByName("Root.002"),
                           SIN_SUMMONED_SWORD_COLL
                        )
                        .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.ARC_HIT)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
                        .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1.0F))
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
                        .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(InvincibleDamageTypeTags.NOT_CHARGE)),
                     new Phase(
                           0.0F,
                           0.55F,
                           2.1474836E9F,
                           2.1474836E9F,
                           2.1474836E9F,
                           SummonedSword_Circle.get().searchJointByName("Root.003"),
                           SIN_SUMMONED_SWORD_COLL
                        )
                        .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.ARC_HIT)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
                        .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1.0F))
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
                        .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(InvincibleDamageTypeTags.NOT_CHARGE)),
                     new Phase(
                           0.0F,
                           0.55F,
                           2.1474836E9F,
                           2.1474836E9F,
                           2.1474836E9F,
                           SummonedSword_Circle.get().searchJointByName("Root.004"),
                           SIN_SUMMONED_SWORD_COLL
                        )
                        .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.ARC_HIT)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
                        .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1.0F))
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
                        .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(InvincibleDamageTypeTags.NOT_CHARGE)),
                     new Phase(
                           0.0F,
                           0.55F,
                           2.1474836E9F,
                           2.1474836E9F,
                           2.1474836E9F,
                           SummonedSword_Circle.get().searchJointByName("Root.005"),
                           SIN_SUMMONED_SWORD_COLL
                        )
                        .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.ARC_HIT)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
                        .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1.0F))
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
                        .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(InvincibleDamageTypeTags.NOT_CHARGE)),
                     new Phase(
                           0.0F,
                           0.55F,
                           2.1474836E9F,
                           2.1474836E9F,
                           2.1474836E9F,
                           SummonedSword_Circle.get().searchJointByName("Root.006"),
                           SIN_SUMMONED_SWORD_COLL
                        )
                        .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.ARC_HIT)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
                        .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1.0F))
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
                        .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(InvincibleDamageTypeTags.NOT_CHARGE))
                  }
               )
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 2.1474836E9F}))
               .addProperty(ActionAnimationProperty.RESET_PLAYER_COMBO_COUNTER, false)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.85F, (entityPatch, animation, params) -> {
                  if (entityPatch instanceof SummonedSwordPatch_In summonedSwordPatchIn) {
                     SummonedSwordEntity_In summonedSword = (SummonedSwordEntity_In)summonedSwordPatchIn.getOriginal();
                     LivingEntity initialTarget = summonedSword.getInitialTarget();
                     if (initialTarget != null && initialTarget.m_6084_()) {
                        float targetKnockbackResistance = (float)initialTarget.m_21133_(Attributes.f_22278_);
                        if (targetKnockbackResistance <= 0.5F) {
                           double pushStrength = 1.0;
                           initialTarget.m_5997_(0.0, pushStrength, 0.0);
                           initialTarget.f_19864_ = true;
                           initialTarget.f_19812_ = true;
                           initialTarget.m_7292_(new MobEffectInstance(MobEffects.f_19591_, 20, 1, false, false, false));
                        }
                     }
                  }
               }, Side.SERVER)})
      );
      SUMMONED_SWORD_OUT = builder.nextAccessor(
         "effect/summoned_sword/summoned_sword_out",
         accessor -> (MultiPhaseAttackAnimation)new MultiPhaseAttackAnimation(
                  0.0F,
                  accessor,
                  SummonedSword_Circle,
                  new Phase[]{
                     new Phase(
                           0.0F,
                           0.0F,
                           0.0F,
                           2.1474836E9F,
                           2.1474836E9F,
                           2.1474836E9F,
                           SummonedSword_Circle.get().searchJointByName("Root.001"),
                           SIN_SUMMONED_SWORD_COLL
                        )
                        .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
                        .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(InvincibleDamageTypeTags.NOT_CHARGE)),
                     new Phase(
                           0.0F,
                           0.0F,
                           0.0F,
                           2.1474836E9F,
                           2.1474836E9F,
                           2.1474836E9F,
                           SummonedSword_Circle.get().searchJointByName("Root.002"),
                           SIN_SUMMONED_SWORD_COLL
                        )
                        .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
                        .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(InvincibleDamageTypeTags.NOT_CHARGE)),
                     new Phase(
                           0.0F,
                           0.0F,
                           0.0F,
                           2.1474836E9F,
                           2.1474836E9F,
                           2.1474836E9F,
                           SummonedSword_Circle.get().searchJointByName("Root.003"),
                           SIN_SUMMONED_SWORD_COLL
                        )
                        .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
                        .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(InvincibleDamageTypeTags.NOT_CHARGE)),
                     new Phase(
                           0.0F,
                           0.0F,
                           0.0F,
                           2.1474836E9F,
                           2.1474836E9F,
                           2.1474836E9F,
                           SummonedSword_Circle.get().searchJointByName("Root.004"),
                           SIN_SUMMONED_SWORD_COLL
                        )
                        .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
                        .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(InvincibleDamageTypeTags.NOT_CHARGE)),
                     new Phase(
                           0.0F,
                           0.0F,
                           0.0F,
                           2.1474836E9F,
                           2.1474836E9F,
                           2.1474836E9F,
                           SummonedSword_Circle.get().searchJointByName("Root.005"),
                           SIN_SUMMONED_SWORD_COLL
                        )
                        .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
                        .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(InvincibleDamageTypeTags.NOT_CHARGE)),
                     new Phase(
                           0.0F,
                           0.0F,
                           0.0F,
                           2.1474836E9F,
                           2.1474836E9F,
                           2.1474836E9F,
                           SummonedSword_Circle.get().searchJointByName("Root.006"),
                           SIN_SUMMONED_SWORD_COLL
                        )
                        .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
                        .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(InvincibleDamageTypeTags.NOT_CHARGE))
                  }
               )
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 2.1474836E9F}))
               .addProperty(ActionAnimationProperty.RESET_PLAYER_COMBO_COUNTER, false)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.15F)
               .addEvents(ActionAnimationProperty.ON_END_EVENTS, new AnimationEvent[]{onEndPlay(SUMMONED_SWORD_OUT)})
      );
      ArmatureAccessor<Armature> Secluded = ArmatureAccessor.create("efn", "entity/effect/secluded", Armature::new);
      SECLUDED_0 = builder.nextAccessor(
         "effect/secluded/secluded0",
         accessor -> new StaticAnimation(0.0F, true, accessor, Secluded)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 2.1474836E9F}))
               .addProperty(ActionAnimationProperty.REMOVE_DELTA_MOVEMENT, true)
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
               .addProperty(ActionAnimationProperty.NO_PHYSICS, true)
      );
      SECLUDED_1 = builder.nextAccessor(
         "effect/secluded/secluded1",
         accessor -> (AutoDiscardActionAnimation)new AutoDiscardActionAnimation(0.11F, accessor, Secluded, 0.7F)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 2.1474836E9F}))
               .addProperty(ActionAnimationProperty.REMOVE_DELTA_MOVEMENT, true)
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
               .addProperty(ActionAnimationProperty.NO_PHYSICS, true)
      );
      SECLUDED_2 = builder.nextAccessor(
         "effect/secluded/secluded2",
         accessor -> (AutoDiscardActionAnimation)new AutoDiscardActionAnimation(0.0F, accessor, Secluded, 0.55F)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 2.1474836E9F}))
               .newTimePair(0.0F, 2.1474836E9F)
               .addStateRemoveOld(EntityState.MOVEMENT_LOCKED, true)
               .addStateRemoveOld(EntityState.LOCKON_ROTATE, true)
               .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
      );
      ARC_IDLE = builder.nextAccessor("biped/arc_test/arc_idle", accessor -> new StaticAnimation(true, accessor, Armatures.BIPED));
      SWORD_RIDE_IDLE = builder.nextAccessor("biped/arc_test/sword_ride_idle", accessor -> new StaticAnimation(true, accessor, Armatures.BIPED));
      SWORD_RIDE_AUTO1 = builder.nextAccessor(
         "biped/arc_test/sword_ride_auto1",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.1F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        28, 36, 55, InteractionHand.MAIN_HAND, 0.8F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                     )
                  }
               )
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
      );
      SWORD_RIDE_AUTO2 = builder.nextAccessor(
         "biped/arc_test/sword_ride_auto2",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.1F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        28, 36, 55, InteractionHand.MAIN_HAND, 0.8F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        89, 97, 120, InteractionHand.MAIN_HAND, 0.8F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                     )
                  }
               )
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
      );
      ARC_AUTO1 = builder.nextAccessor(
         "biped/arc_test/arc_auto1",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.1F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        27, 50, 55, InteractionHand.MAIN_HAND, 0.8F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                     )
                  }
               )
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
      );
      ARC_AUTO2 = builder.nextAccessor(
         "biped/arc_test/arc_auto2",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.1F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        29, 53, 55, InteractionHand.MAIN_HAND, 0.8F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                     )
                  }
               )
               .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(35, 15, 3.0F, 1.0F, 2.0F)})
      );
      ARC_AUTO3 = builder.nextAccessor(
         "biped/arc_test/arc_auto3",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.1F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        27, 54, 60, InteractionHand.MAIN_HAND, 0.8F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                     )
                  }
               )
               .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
      );
      ARC_TACHI_AUTO1 = builder.nextAccessor(
         "biped/arc_test/arctachi_auto1",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        30, 55, 65, InteractionHand.MAIN_HAND, 0.8F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                     )
                  }
               )
               .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
      );
   }

   public static AnimationEvent onEndPlay(AnimationAccessor<? extends StaticAnimation> provider) {
      return SimpleEvent.create((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.reserveAnimation(provider), Side.BOTH);
   }

   private static float calculateWeaponSpeedWithCap(DynamicAnimation animation, LivingEntityPatch<?> entitypatch, float speedCap, float defaultBasisSpeed) {
      if (animation instanceof AttackAnimation attackAnimation && entitypatch instanceof PlayerPatch<?> playerpatch) {
         float currentElapsedTime = entitypatch.getAnimator().getPlayerFor(attackAnimation.getAccessor()).getElapsedTime();
         Phase phase = attackAnimation.getPhaseByTime(currentElapsedTime);
         float attackSpeed = playerpatch.getAttackSpeed(phase.hand);
         float calculatedSpeed = attackSpeed / defaultBasisSpeed;
         calculatedSpeed = (float)Math.round(calculatedSpeed * 1000.0F) / 1000.0F;
         float speedFactor = attackAnimation.getProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR).orElse(1.0F);
         float finalSpeed = 1.0F + (calculatedSpeed - 1.0F) * speedFactor;
         if ((Boolean)EFNCommonConfig.ENABLE_ANIMATION_PLAYSPEED_LOCK.get()) {
            return Math.min(finalSpeed, speedCap);
         }

         return Math.min(finalSpeed, Float.MAX_VALUE);
      }

      return animation.isLinkAnimation() ? animation.getPlaySpeed(entitypatch, animation) : 1.0F;
   }
}
