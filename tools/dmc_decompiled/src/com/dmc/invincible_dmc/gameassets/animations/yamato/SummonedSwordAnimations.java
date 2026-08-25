package com.dmc.invincible_dmc.gameassets.animations.yamato;

import com.dmc.invincible_dmc.api.animation.types.MultiPhaseAttackAnimation;
import com.dmc.invincible_dmc.api.animation.types.vfx.ArmatureVfxAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.SummonedSwordAnimation;
import com.dmc.invincible_dmc.client.model.DMCArmatures;
import com.dmc.invincible_dmc.client.sound.DMCSounds;
import com.dmc.invincible_dmc.damagesource.DMCDamageTypeTags;
import com.dmc.invincible_dmc.particle.DMCParticles;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import java.util.Set;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationEvent.SimpleEvent;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.PlaybackSpeedModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.world.damagesource.StunType;

@EventBusSubscriber(
   modid = "invincible_dmc",
   bus = Bus.MOD
)
public class SummonedSwordAnimations {
   public static final Collider SUMMONED_SWORD_COLLIDER = new OBBCollider(0.4, 1.2, 0.4, 0.0, 0.0, 0.0);
   public static final Collider JUDGEMENT_CUT_COLLIDER = new OBBCollider(1.85, 1.85, 1.85, 0.0, 0.0, 0.0);
   public static final Collider RAPIDSLASH_COLLIDER = new OBBCollider(1.85, 1.85, 2.5, 0.0, 0.0, 0.0);
   public static AnimationAccessor<StaticAnimation> SUMMONED_SWORD_IDLE;
   public static AnimationAccessor<StaticAnimation> SUMMONED_SWORD_CIRCLE;
   public static AnimationAccessor<SummonedSwordAnimation> SUMMONED_SWORD;
   public static AnimationAccessor<SummonedSwordAnimation> STORM_SWORD;
   public static AnimationAccessor<SummonedSwordAnimation> SPIRAL_SWORD;
   public static AnimationAccessor<SummonedSwordAnimation> SPINE_SWORD;
   public static AnimationAccessor<MultiPhaseAttackAnimation> STORM_BLADES;
   public static AnimationAccessor<MultiPhaseAttackAnimation> SPIRAL_BLADES;
   public static AnimationAccessor<StaticAnimation> JUDGEMENT_CUT_ENTITY_IDLE;
   public static AnimationAccessor<ArmatureVfxAnimation> RAPID_SLASH_ATTACK;
   public static AnimationAccessor<ArmatureVfxAnimation> JUDGEMENT_CUT_ENTITY_ATTACK;
   public static AnimationAccessor<ArmatureVfxAnimation> JUDGEMENT_CUT_ENTITY_ATTACK_NORMAL;

   public static void build(AnimationBuilder animationBuilder) {
      JUDGEMENT_CUT_ENTITY_IDLE = animationBuilder.nextAccessor(
         "effect/judgement_cut_idle", accessor -> new StaticAnimation(0.0F, true, accessor, DMCArmatures.JUDGEMENT_CUT)
      );
      RAPID_SLASH_ATTACK = animationBuilder.nextAccessor(
         "effect/rapid_slash_attack",
         accessor -> (ArmatureVfxAnimation)new ArmatureVfxAnimation(
                  0.0F,
                  accessor,
                  DMCArmatures.JUDGEMENT_CUT,
                  1.0F,
                  1.0F,
                  AvalonAnimationUtils.createSimplePhase(
                     0, 3, 4, InteractionHand.MAIN_HAND, 0.1F, 1.0F, DMCArmatures.JUDGEMENT_CUT.get().rootJoint, RAPIDSLASH_COLLIDER
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     5, 8, 9, InteractionHand.MAIN_HAND, 0.1F, 1.0F, DMCArmatures.JUDGEMENT_CUT.get().rootJoint, RAPIDSLASH_COLLIDER
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     10, 13, 14, InteractionHand.MAIN_HAND, 0.1F, 1.0F, DMCArmatures.JUDGEMENT_CUT.get().rootJoint, RAPIDSLASH_COLLIDER
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     15, 18, 19, InteractionHand.MAIN_HAND, 0.1F, 1.0F, DMCArmatures.JUDGEMENT_CUT.get().rootJoint, RAPIDSLASH_COLLIDER
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     20, 23, 24, InteractionHand.MAIN_HAND, 0.1F, 1.0F, DMCArmatures.JUDGEMENT_CUT.get().rootJoint, RAPIDSLASH_COLLIDER
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     25, 28, 30, InteractionHand.MAIN_HAND, 0.1F, 1.0F, DMCArmatures.JUDGEMENT_CUT.get().rootJoint, RAPIDSLASH_COLLIDER
                  )
               )
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get())
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(2.1474836E9F))
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get(), 0)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get(), 1)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get(), 2)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get(), 3)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get(), 4)
               .addProperty(AttackPhaseProperty.PARTICLE, DMCParticles.NULL, 0)
               .addProperty(AttackPhaseProperty.PARTICLE, DMCParticles.NULL, 1)
               .addProperty(AttackPhaseProperty.PARTICLE, DMCParticles.NULL, 2)
               .addProperty(AttackPhaseProperty.PARTICLE, DMCParticles.ATTACK_MAIN, 3)
               .addProperty(AttackPhaseProperty.PARTICLE, DMCParticles.NULL, 4)
               .addProperty(AttackPhaseProperty.PARTICLE, DMCParticles.NULL, 5)
               .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.RAPID_SLASH, YamatoAnimations.SLOW_PERSISTENT), 0)
               .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.RAPID_SLASH, YamatoAnimations.SLOW_PERSISTENT), 1)
               .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.RAPID_SLASH, YamatoAnimations.SLOW_PERSISTENT), 2)
               .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.RAPID_SLASH, YamatoAnimations.SLOW_PERSISTENT), 3)
               .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.RAPID_SLASH, YamatoAnimations.SLOW_PERSISTENT), 4)
               .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(YamatoAnimations.RAPID_SLASH, YamatoAnimations.SLOW_PERSISTENT), 5)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, Float.MAX_VALUE}))
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.NO_PHYSICS, true)
      );
      JUDGEMENT_CUT_ENTITY_ATTACK = animationBuilder.nextAccessor(
         "effect/judgement_cut_attack",
         accessor -> (ArmatureVfxAnimation)new ArmatureVfxAnimation(
                  0.0F,
                  accessor,
                  DMCArmatures.JUDGEMENT_CUT,
                  1.0F,
                  1.0F,
                  AvalonAnimationUtils.createSimplePhase(
                     0, 3, 4, InteractionHand.MAIN_HAND, 1.5F, 1.0F, DMCArmatures.JUDGEMENT_CUT.get().rootJoint, JUDGEMENT_CUT_COLLIDER
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     5, 8, 9, InteractionHand.MAIN_HAND, 1.5F, 1.0F, DMCArmatures.JUDGEMENT_CUT.get().rootJoint, JUDGEMENT_CUT_COLLIDER
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     10, 13, 15, InteractionHand.MAIN_HAND, 1.5F, 1.0F, DMCArmatures.JUDGEMENT_CUT.get().rootJoint, JUDGEMENT_CUT_COLLIDER
                  )
               )
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get())
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(2.1474836E9F))
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get(), 0)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get(), 1)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get(), 2)
               .addProperty(AttackPhaseProperty.PARTICLE, DMCParticles.NULL, 0)
               .addProperty(AttackPhaseProperty.PARTICLE, DMCParticles.NULL, 1)
               .addProperty(AttackPhaseProperty.PARTICLE, DMCParticles.NULL, 2)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, Float.MAX_VALUE}))
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.NO_PHYSICS, true)
      );
      JUDGEMENT_CUT_ENTITY_ATTACK_NORMAL = animationBuilder.nextAccessor(
         "effect/judgement_cut_attack_normal",
         accessor -> (ArmatureVfxAnimation)new ArmatureVfxAnimation(
                  0.0F,
                  accessor,
                  DMCArmatures.JUDGEMENT_CUT,
                  1.0F,
                  1.0F,
                  AvalonAnimationUtils.createSimplePhase(
                     0, 3, 4, InteractionHand.MAIN_HAND, 0.5F, 1.0F, DMCArmatures.JUDGEMENT_CUT.get().rootJoint, JUDGEMENT_CUT_COLLIDER
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     5, 8, 9, InteractionHand.MAIN_HAND, 0.5F, 1.0F, DMCArmatures.JUDGEMENT_CUT.get().rootJoint, JUDGEMENT_CUT_COLLIDER
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     10, 13, 15, InteractionHand.MAIN_HAND, 0.5F, 1.0F, DMCArmatures.JUDGEMENT_CUT.get().rootJoint, JUDGEMENT_CUT_COLLIDER
                  )
               )
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get())
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(2.1474836E9F))
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get(), 0)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get(), 1)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get(), 2)
               .addProperty(AttackPhaseProperty.PARTICLE, DMCParticles.NULL, 0)
               .addProperty(AttackPhaseProperty.PARTICLE, DMCParticles.NULL, 1)
               .addProperty(AttackPhaseProperty.PARTICLE, DMCParticles.NULL, 2)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, Float.MAX_VALUE}))
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.NO_PHYSICS, true)
      );
      SUMMONED_SWORD_IDLE = animationBuilder.nextAccessor(
         "effect/summoned_sword/summoned_sword_idle",
         accessor -> new StaticAnimation(0.1F, true, accessor, DMCArmatures.SUMMONED_SWORD).addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
      );
      SUMMONED_SWORD_CIRCLE = animationBuilder.nextAccessor(
         "effect/summoned_sword/storm_blades_idle",
         accessor -> new StaticAnimation(0.1F, true, accessor, DMCArmatures.SUMMONED_SWORD_CIRCLE).addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
      );
      SUMMONED_SWORD = animationBuilder.nextAccessor(
         "effect/summoned_sword/summoned_sword",
         accessor -> (SummonedSwordAnimation)new SummonedSwordAnimation(0.01F, accessor, DMCArmatures.SUMMONED_SWORD, 0.1F)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, Float.MAX_VALUE}))
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.NO_PHYSICS, true)
      );
      STORM_SWORD = animationBuilder.nextAccessor(
         "effect/summoned_sword/storm_sword",
         accessor -> (SummonedSwordAnimation)new SummonedSwordAnimation(0.01F, accessor, DMCArmatures.SUMMONED_SWORD, 0.1F)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, Float.MAX_VALUE}))
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.NO_PHYSICS, true)
      );
      SPIRAL_SWORD = animationBuilder.nextAccessor(
         "effect/summoned_sword/spiral_sword",
         accessor -> (SummonedSwordAnimation)new SummonedSwordAnimation(0.01F, accessor, DMCArmatures.SUMMONED_SWORD, 0.1F)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, Float.MAX_VALUE}))
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.NO_PHYSICS, true)
      );
      SPINE_SWORD = animationBuilder.nextAccessor(
         "effect/summoned_sword/spine_sword",
         accessor -> (SummonedSwordAnimation)new SummonedSwordAnimation(0.01F, accessor, DMCArmatures.SUMMONED_SWORD, 0.1F)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, Float.MAX_VALUE}))
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.NO_PHYSICS, true)
      );
      STORM_BLADES = animationBuilder.nextAccessor(
         "effect/summoned_sword/storm_blades",
         accessor -> (MultiPhaseAttackAnimation)new MultiPhaseAttackAnimation(
                  0.0F,
                  accessor,
                  DMCArmatures.SUMMONED_SWORD_CIRCLE,
                  new Phase(
                        0.0F,
                        0.55F,
                        2.1474836E9F,
                        2.1474836E9F,
                        2.1474836E9F,
                        DMCArmatures.SUMMONED_SWORD_CIRCLE.get().searchJointByName("Root.001"),
                        SUMMONED_SWORD_COLLIDER
                     )
                     .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get())
                     .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1.0F))
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
                     .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(DMCDamageTypeTags.NOT_CHARGE)),
                  new Phase(
                        0.0F,
                        0.55F,
                        2.1474836E9F,
                        2.1474836E9F,
                        2.1474836E9F,
                        DMCArmatures.SUMMONED_SWORD_CIRCLE.get().searchJointByName("Root.002"),
                        SUMMONED_SWORD_COLLIDER
                     )
                     .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get())
                     .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1.0F))
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
                     .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(DMCDamageTypeTags.NOT_CHARGE)),
                  new Phase(
                        0.0F,
                        0.55F,
                        2.1474836E9F,
                        2.1474836E9F,
                        2.1474836E9F,
                        DMCArmatures.SUMMONED_SWORD_CIRCLE.get().searchJointByName("Root.003"),
                        SUMMONED_SWORD_COLLIDER
                     )
                     .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get())
                     .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1.0F))
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
                     .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(DMCDamageTypeTags.NOT_CHARGE)),
                  new Phase(
                        0.0F,
                        0.55F,
                        2.1474836E9F,
                        2.1474836E9F,
                        2.1474836E9F,
                        DMCArmatures.SUMMONED_SWORD_CIRCLE.get().searchJointByName("Root.004"),
                        SUMMONED_SWORD_COLLIDER
                     )
                     .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get())
                     .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1.0F))
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
                     .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(DMCDamageTypeTags.NOT_CHARGE)),
                  new Phase(
                        0.0F,
                        0.55F,
                        2.1474836E9F,
                        2.1474836E9F,
                        2.1474836E9F,
                        DMCArmatures.SUMMONED_SWORD_CIRCLE.get().searchJointByName("Root.005"),
                        SUMMONED_SWORD_COLLIDER
                     )
                     .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get())
                     .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1.0F))
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
                     .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(DMCDamageTypeTags.NOT_CHARGE)),
                  new Phase(
                        0.0F,
                        0.55F,
                        2.1474836E9F,
                        2.1474836E9F,
                        2.1474836E9F,
                        DMCArmatures.SUMMONED_SWORD_CIRCLE.get().searchJointByName("Root.006"),
                        SUMMONED_SWORD_COLLIDER
                     )
                     .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get())
                     .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1.0F))
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
                     .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(DMCDamageTypeTags.NOT_CHARGE))
               )
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 2.1474836E9F}))
               .addProperty(ActionAnimationProperty.RESET_PLAYER_COMBO_COUNTER, false)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
      );
      SPIRAL_BLADES = animationBuilder.nextAccessor(
         "effect/summoned_sword/spiral_blades",
         accessor -> (MultiPhaseAttackAnimation)new MultiPhaseAttackAnimation(
                  0.0F,
                  accessor,
                  DMCArmatures.SUMMONED_SWORD_CIRCLE,
                  new Phase(
                        0.0F,
                        0.0F,
                        0.0F,
                        2.1474836E9F,
                        2.1474836E9F,
                        2.1474836E9F,
                        DMCArmatures.SUMMONED_SWORD_CIRCLE.get().searchJointByName("Root.001"),
                        SUMMONED_SWORD_COLLIDER
                     )
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get())
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
                     .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(DMCDamageTypeTags.NOT_CHARGE)),
                  new Phase(
                        0.0F,
                        0.0F,
                        0.0F,
                        2.1474836E9F,
                        2.1474836E9F,
                        2.1474836E9F,
                        DMCArmatures.SUMMONED_SWORD_CIRCLE.get().searchJointByName("Root.002"),
                        SUMMONED_SWORD_COLLIDER
                     )
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get())
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
                     .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(DMCDamageTypeTags.NOT_CHARGE)),
                  new Phase(
                        0.0F,
                        0.0F,
                        0.0F,
                        2.1474836E9F,
                        2.1474836E9F,
                        2.1474836E9F,
                        DMCArmatures.SUMMONED_SWORD_CIRCLE.get().searchJointByName("Root.003"),
                        SUMMONED_SWORD_COLLIDER
                     )
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get())
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
                     .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(DMCDamageTypeTags.NOT_CHARGE)),
                  new Phase(
                        0.0F,
                        0.0F,
                        0.0F,
                        2.1474836E9F,
                        2.1474836E9F,
                        2.1474836E9F,
                        DMCArmatures.SUMMONED_SWORD_CIRCLE.get().searchJointByName("Root.004"),
                        SUMMONED_SWORD_COLLIDER
                     )
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get())
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
                     .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(DMCDamageTypeTags.NOT_CHARGE)),
                  new Phase(
                        0.0F,
                        0.0F,
                        0.0F,
                        2.1474836E9F,
                        2.1474836E9F,
                        2.1474836E9F,
                        DMCArmatures.SUMMONED_SWORD_CIRCLE.get().searchJointByName("Root.005"),
                        SUMMONED_SWORD_COLLIDER
                     )
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get())
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
                     .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(DMCDamageTypeTags.NOT_CHARGE)),
                  new Phase(
                        0.0F,
                        0.0F,
                        0.0F,
                        2.1474836E9F,
                        2.1474836E9F,
                        2.1474836E9F,
                        DMCArmatures.SUMMONED_SWORD_CIRCLE.get().searchJointByName("Root.006"),
                        SUMMONED_SWORD_COLLIDER
                     )
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)DMCSounds.NOSOUND.get())
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
                     .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(DMCDamageTypeTags.NOT_CHARGE))
               )
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 2.1474836E9F}))
               .addProperty(ActionAnimationProperty.RESET_PLAYER_COMBO_COUNTER, false)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.15F)
               .addEvents(ActionAnimationProperty.ON_END_EVENTS, new AnimationEvent[]{onEndPlay(SPIRAL_BLADES)})
      );
   }

   public static AnimationEvent onEndPlay(AnimationAccessor<? extends StaticAnimation> provider) {
      return SimpleEvent.create((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.reserveAnimation(provider), Side.BOTH);
   }
}
