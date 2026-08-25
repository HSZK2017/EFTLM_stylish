package com.Yujin.onegradefixer.epicmoonmod.animations;

import com.Yujin.onegradefixer.epicmoonmod.comboevents.EMboolean;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.EMEventsutil;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.EMsimple;
import com.Yujin.onegradefixer.epicmoonmod.effect.EMeffects;
import com.Yujin.onegradefixer.epicmoonmod.item.EpicmoonItems;
import com.Yujin.onegradefixer.epicmoonmod.sound.EMsounds;
import com.Yujin.onegradefixer.epicmoonmod.util.skillparameter;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation.AvalonPhase;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import com.merlin204.avalon.util.AvalonEventUtils;
import java.util.List;
import java.util.function.Function;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.AnimationManager.AnimationRegistryEvent;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationEvent.SimpleEvent;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.PlaybackSpeedModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.DodgeAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.MovementAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.damagesource.StunType;

@EventBusSubscriber(
   modid = "epicmoonmod",
   bus = Bus.MOD
)
public class EMAnimations {
   public static final ArmatureAccessor<? extends Armature> ARMATURE = ArmatureAccessor.create("epicmoonmod", "tentai_seitou", Armature::new);
   public static final ArmatureAccessor<? extends Armature> ARMATURE2 = ArmatureAccessor.create("epicmoonmod", "valencina_dual_swords", Armature::new);
   public static AnimationAccessor<StaticAnimation> TENTAI_SEITOU_IDLE;
   public static AnimationAccessor<StaticAnimation> TENTAI_SEITOU_GUARD;
   public static AnimationAccessor<MovementAnimation> TENTAI_SEITOU_WALK;
   public static AnimationAccessor<MovementAnimation> TENTAI_SEITOU_RUN;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_AUTO1;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_AUTO2;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_AUTO3;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_TSHOT1;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_TSHOT2;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_TSHOT3;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_TSHOT5;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_DASH;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_TDASHOT;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_AIRSLASH;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_TAIRSHOT;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_SKILL;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_TSKILL;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_TSKILL2;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_TSKILL3;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_TSKILL4;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_TSKILL5;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_TSKILL6;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_TSKILL7;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_TSKILL8;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_TSKILL9;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_TSKILL10;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_ESKILL;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_ESKILL2;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_ESKILL3;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_ESKILL4;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_ESKILL5;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_ESKILL6;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_ESKILL7;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_ESKILL8;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_ESKILL9;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_ESKILL10;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_PARRY;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_TPARRY;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_PARRY2;
   public static AnimationAccessor<AvalonAttackAnimation> TENTAI_SEITOU_TPARRY2;
   public static AnimationAccessor<StaticAnimation> DUAL_IDLE;
   public static AnimationAccessor<StaticAnimation> DUAL_GUARD;
   public static AnimationAccessor<DodgeAnimation> DUAL_DODGE;
   public static AnimationAccessor<AvalonAttackAnimation> DUAL_RELOAD;
   public static AnimationAccessor<MovementAnimation> DUAL_WALK;
   public static AnimationAccessor<MovementAnimation> DUAL_RUN;
   public static AnimationAccessor<AvalonAttackAnimation> DUAL_AIR;
   public static AnimationAccessor<AvalonAttackAnimation> DUAL_DASH1;
   public static AnimationAccessor<AvalonAttackAnimation> DUAL_DASH2;
   public static AnimationAccessor<AvalonAttackAnimation> DUAL_DASH3;
   public static AnimationAccessor<AvalonAttackAnimation> DUAL_AUTO1;
   public static AnimationAccessor<AvalonAttackAnimation> DUAL_AUTO2;
   public static AnimationAccessor<AvalonAttackAnimation> DUAL_AUTO3;
   public static AnimationAccessor<AvalonAttackAnimation> DUAL_COUNTER;
   public static AnimationAccessor<AvalonAttackAnimation> DUAL_COUNTER2;
   public static AnimationAccessor<AvalonAttackAnimation> DUAL_SKILL;
   public static AnimationAccessor<AvalonAttackAnimation> DUAL_SKILL2;
   public static AnimationAccessor<AvalonAttackAnimation> DUAL_SKILL3;
   public static AnimationAccessor<AvalonAttackAnimation> DUAL_SKILL4;
   public static AnimationAccessor<AvalonAttackAnimation> DUAL_SKILL5;
   public static AnimationAccessor<AvalonAttackAnimation> DUAL_SKILL6;
   public static AnimationAccessor<AvalonAttackAnimation> DUAL_SKILL7;
   public static AnimationAccessor<AvalonAttackAnimation> DUAL_SKILL8;
   public static AnimationAccessor<AvalonAttackAnimation> DUAL_SKILL9;
   public static AnimationAccessor<AvalonAttackAnimation> DUAL_SKILL10;
   public static AnimationAccessor<AvalonAttackAnimation> DUAL_SKILL11;
   public static AnimationAccessor<AvalonAttackAnimation> DUAL_SKILL13;
   public static final Collider Pisword = new OBBCollider(1.0, 1.0, 1.8, 0.0, 0.2, -0.8);
   public static final Collider PiswordBig = new OBBCollider(0.9, 0.9, 1.2, 0.0, 0.2, -0.3);

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void registerAnimations(AnimationRegistryEvent event) {
      event.newBuilder("epicmoonmod", EMAnimations::build);
   }

   public static float frame(int frame) {
      return (float)frame / 60.0F;
   }

   public static void build(AnimationBuilder builder) {
      ArmatureAccessor<? extends Armature> armature = ArmatureAccessor.create("epicmoonmod", "tentai_seitou", Armature::new);
      TENTAI_SEITOU_IDLE = builder.nextAccessor("biped/tiantui/tentai_seitou_idle", accessor -> new StaticAnimation(true, accessor, Armatures.BIPED));
      TENTAI_SEITOU_WALK = builder.nextAccessor("biped/tiantui/tentai_seitou_walk", accessor -> new MovementAnimation(true, accessor, Armatures.BIPED));
      TENTAI_SEITOU_RUN = builder.nextAccessor("biped/tiantui/tentai_seitou_run", accessor -> new MovementAnimation(true, accessor, Armatures.BIPED));
      TENTAI_SEITOU_GUARD = builder.nextAccessor("biped/tiantui/tentai_seitou_guard", accessor -> new StaticAnimation(0.05F, true, accessor, Armatures.BIPED));
      TENTAI_SEITOU_PARRY = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_parry",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        54, 70, 75, InteractionHand.MAIN_HAND, 1.5F, 1.5F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD)
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, null)
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{EMEventsutil.Image(0.88F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(73, 5, 1.0F, 3.0F, 1.0F)})
      );
      TENTAI_SEITOU_TPARRY = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_tparry",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        54, 70, 75, InteractionHand.MAIN_HAND, 3.0F, 3.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD)
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, null)
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{EMEventsutil.Image(0.88F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(73, 10, 1.0F, 3.0F, 1.0F)})
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.7F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EMsounds.SHOT.get(), 0.75F, 0.0F, 0.0F), Side.CLIENT
                     )
                  }
               )
               .addEvents(
                  new AnimationEvent[]{
                     skillparameter.bladeEffekTrail(
                        1.1F, 1.3F, InteractionHand.MAIN_HAND, "k", 0.0, 0.0, -2.4, 0.0, 0.0, -2.3, 0.0F, 0.0F, 0.0F, 1.0F, 2.0, 1.0
                     )
                  }
               )
      );
      TENTAI_SEITOU_PARRY2 = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_parry2",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        13, 20, 55, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        40, 51, 55, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD)
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, null)
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{EMEventsutil.Image(0.1F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(15, 5, 1.0F, 3.0F, 1.0F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(47, 5, 1.0F, 3.0F, 1.0F)})
      );
      TENTAI_SEITOU_TPARRY2 = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_tparry2",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        13, 20, 55, InteractionHand.MAIN_HAND, 2.0F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        40, 51, 55, InteractionHand.MAIN_HAND, 2.0F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD)
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, null)
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{EMEventsutil.Image(0.1F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(15, 10, 1.0F, 3.0F, 1.0F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(47, 10, 1.0F, 3.0F, 1.0F)})
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.1F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EMsounds.SHOT.get(), 0.75F, 0.0F, 0.0F), Side.CLIENT
                     )
                  }
               )
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.7F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EMsounds.SHOT.get(), 0.75F, 0.0F, 0.0F), Side.CLIENT
                     )
                  }
               )
               .addEvents(
                  new AnimationEvent[]{
                     skillparameter.bladeEffekTrail(
                        frame(13), frame(23), InteractionHand.MAIN_HAND, "k", 0.0, 0.0, -2.4, 0.0, 0.0, -2.3, 0.0F, 0.0F, 0.0F, 1.0F, 2.0, 1.0
                     )
                  }
               )
               .addEvents(
                  new AnimationEvent[]{
                     skillparameter.bladeEffekTrail(
                        frame(40), frame(48), InteractionHand.MAIN_HAND, "k", 0.0, 0.0, -2.4, 0.0, 0.0, -2.3, 0.0F, 0.0F, 0.0F, 1.0F, 2.0, 1.0
                     )
                  }
               )
      );
      TENTAI_SEITOU_AUTO1 = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_auto1",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        10, 25, 32, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD)
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, null)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{EMEventsutil.Image(0.15F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(20, 5, 1.0F, 3.0F, 1.0F)})
      );
      TENTAI_SEITOU_AUTO2 = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_auto2",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        20, 31, 45, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD)
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, null)
               .addEvents(new AnimationEvent[]{EMEventsutil.Image(0.25F)})
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(30, 5, 1.0F, 3.0F, 1.0F)})
      );
      TENTAI_SEITOU_AUTO3 = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_auto3",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        25, 35, 45, InteractionHand.MAIN_HAND, 0.75F, 0.75F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, PiswordBig
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        50, 55, 60, InteractionHand.MAIN_HAND, 0.75F, 1.1F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(40, 5, 1.0F, 3.0F, 1.0F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(55, 5, 1.0F, 3.0F, 1.0F)})
      );
      TENTAI_SEITOU_TSHOT1 = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_tshot1",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        10, 22, 27, InteractionHand.MAIN_HAND, 2.0F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD)
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, null)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.1F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EMsounds.SHOT.get(), 0.75F, 0.0F, 0.0F), Side.CLIENT
                     )
                  }
               )
               .addEvents(new AnimationEvent[]{EMEventsutil.Image(0.15F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(20, 10, 1.0F, 3.0F, 1.0F)})
               .addEvents(
                  new AnimationEvent[]{
                     skillparameter.bladeEffekTrail(
                        0.2F, 0.5F, InteractionHand.MAIN_HAND, "k", 0.0, 0.0, -2.4, 0.0, 0.0, -2.3, 0.0F, 0.0F, 0.0F, 1.0F, 2.0, 1.0
                     )
                  }
               )
      );
      TENTAI_SEITOU_TSHOT2 = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_tshot2",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        20, 31, 36, InteractionHand.MAIN_HAND, 2.0F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD)
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, null)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.15F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EMsounds.SHOT.get(), 0.75F, 0.0F, 0.0F), Side.CLIENT
                     )
                  }
               )
               .addEvents(new AnimationEvent[]{EMEventsutil.Image(0.25F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(30, 10, 1.0F, 3.0F, 1.0F)})
               .addEvents(
                  new AnimationEvent[]{
                     skillparameter.bladeEffekTrail(
                        0.4F, 0.6F, InteractionHand.MAIN_HAND, "k", 0.0, 0.0, -2.4, 0.0, 0.0, -2.3, 0.0F, 0.0F, 0.0F, 1.0F, 2.0, 1.0
                     )
                  }
               )
      );
      TENTAI_SEITOU_TSHOT3 = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_tshot3",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        20, 31, 36, InteractionHand.MAIN_HAND, 2.0F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD)
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, null)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.15F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EMsounds.SHOT.get(), 0.75F, 0.0F, 0.0F), Side.CLIENT
                     )
                  }
               )
               .addEvents(new AnimationEvent[]{EMEventsutil.Image(0.25F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(30, 10, 1.0F, 3.0F, 1.0F)})
               .addEvents(
                  new AnimationEvent[]{
                     skillparameter.bladeEffekTrail(
                        0.4F, 0.6F, InteractionHand.MAIN_HAND, "k", 0.0, 0.0, -2.4, 0.0, 0.0, -2.3, 0.0F, 0.0F, 0.0F, 1.0F, 2.0, 1.0
                     )
                  }
               )
      );
      TENTAI_SEITOU_TSHOT5 = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_tshot5",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        25, 35, 65, InteractionHand.MAIN_HAND, 1.5F, 1.6F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, PiswordBig
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        50, 60, 65, InteractionHand.MAIN_HAND, 1.5F, 1.6F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.3F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EMsounds.SHOT.get(), 0.75F, 0.0F, 0.0F), Side.CLIENT
                     )
                  }
               )
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.9F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EMsounds.SHOT.get(), 0.75F, 0.0F, 0.0F), Side.CLIENT
                     )
                  }
               )
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(40, 5, 1.0F, 3.0F, 1.0F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(55, 5, 1.0F, 3.0F, 1.0F)})
               .addEvents(
                  new AnimationEvent[]{
                     skillparameter.bladeEffekTrail(
                        frame(20), frame(35), InteractionHand.MAIN_HAND, "k", 0.0, 0.0, -2.4, 0.0, 0.0, -2.3, 0.0F, 0.0F, 0.0F, 1.0F, 2.0, 1.0
                     )
                  }
               )
               .addEvents(
                  new AnimationEvent[]{
                     skillparameter.bladeEffekTrail(
                        frame(53), frame(60), InteractionHand.MAIN_HAND, "k", 0.0, 0.0, -2.4, 0.0, 0.0, -2.3, 0.0F, 0.0F, 0.0F, 1.0F, 2.0, 1.0
                     )
                  }
               )
      );
      TENTAI_SEITOU_DASH = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_dash",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.1F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        34, 40, 50, InteractionHand.MAIN_HAND, 1.2F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{EMEventsutil.Image(0.5F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(45, 5, 1.0F, 3.0F, 1.0F)})
      );
      TENTAI_SEITOU_TDASHOT = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_tdashot",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.1F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        34, 40, 45, InteractionHand.MAIN_HAND, 2.2F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.4F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EMsounds.SHOT.get(), 0.75F, 0.0F, 0.0F), Side.CLIENT
                     )
                  }
               )
               .addEvents(new AnimationEvent[]{EMEventsutil.Image(0.5F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(45, 5, 1.0F, 3.0F, 1.0F)})
               .addEvents(
                  new AnimationEvent[]{
                     skillparameter.bladeEffekTrail(
                        0.56F, 0.683F, InteractionHand.MAIN_HAND, "k", 0.0, 0.0, -2.4, 0.0, 0.0, -2.3, 0.0F, 0.0F, 0.0F, 1.0F, 2.0, 1.0
                     )
                  }
               )
      );
      TENTAI_SEITOU_AIRSLASH = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_airslash",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.1F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        17, 25, 40, InteractionHand.MAIN_HAND, 1.3F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.1F, 0.2F}))
      );
      TENTAI_SEITOU_TAIRSHOT = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_tairshot",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.1F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        17, 25, 40, InteractionHand.MAIN_HAND, 2.5F, 2.5F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.1F, 0.2F}))
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.3F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EMsounds.SHOT.get(), 0.75F, 0.0F, 0.0F), Side.CLIENT
                     )
                  }
               )
               .addEvents(
                  new AnimationEvent[]{
                     skillparameter.bladeEffekTrail(
                        frame(15), frame(21), InteractionHand.MAIN_HAND, "k", 0.0, 0.0, -2.4, 0.0, 0.0, -2.3, 0.0F, 0.0F, 0.0F, 1.0F, 2.0, 1.0
                     )
                  }
               )
      );
      TENTAI_SEITOU_SKILL = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_skill",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.1F,
                  accessor,
                  armature,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        127, 135, 150, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.1F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EMsounds.RELOAD1.get(), 0.25F, 0.0F, 0.0F), Side.CLIENT
                     )
                  }
               )
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(45, 10, 1.0F, 5.0F, 1.0F)})
               .addEvents(
                  new AnimationEvent[]{
                     skillparameter.bladeEffekTrail(
                        frame(126), frame(133), InteractionHand.MAIN_HAND, "k", 0.0, 0.0, -2.4, 0.0, 0.0, -2.3, 0.0F, 0.0F, 0.0F, 1.0F, 2.0, 1.0
                     )
                  }
               )
      );
      TENTAI_SEITOU_TSKILL = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_tskill",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        170, 180, 200, InteractionHand.MAIN_HAND, 10.0F, 10.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, Pisword
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(45, 10, 3.0F, 5.0F, 4.0F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(120, 10, 3.0F, 5.0F, 4.0F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(170, 20, 20.0F, 5.0F, 4.0F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleGroundSplit(45, 0.0, 0.0, 0.0, 0.0, 1.5F, true)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleGroundSplit(120, 0.0, 0.0, 0.0, 0.0, 1.5F, true)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleGroundSplit(170, 0.0, 0.0, 0.0, 0.0, 4.0F, true)})
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.7F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EMsounds.SHOT3.get(), 1.0F, 0.0F, 0.0F), Side.CLIENT
                     )
                  }
               )
               .addEvents(new AnimationEvent[]{EMEventsutil.Image(2.37F)})
               .addEvents(
                  new AnimationEvent[]{
                     skillparameter.bladeEffekTrail(
                        frame(165), frame(175), InteractionHand.MAIN_HAND, "k", 0.0, 0.0, -2.4, 0.0, 0.0, -2.3, 0.0F, 0.0F, 0.0F, 1.0F, 2.0, 1.0
                     )
                  }
               )
      );
      TENTAI_SEITOU_TSKILL2 = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_tskill2",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        60, 70, 100, InteractionHand.MAIN_HAND, 2.5F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .newTimePair(0.0F, 3.5F)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.3F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EMsounds.SHOT2.get(), 0.75F, 0.0F, 0.0F), Side.CLIENT
                     )
                  }
               )
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(70, 5, 1.0F, 3.0F, 1.0F)})
               .addEvents(
                  new AnimationEvent[]{
                     skillparameter.bladeEffekTrail(
                        frame(60), frame(72), InteractionHand.MAIN_HAND, "k", 0.0, 0.0, -2.4, 0.0, 0.0, -2.3, 0.0F, 0.0F, 0.0F, 1.0F, 2.0, 1.0
                     )
                  }
               )
      );
      TENTAI_SEITOU_TSKILL3 = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_tskill3",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        26, 36, 80, InteractionHand.MAIN_HAND, 2.5F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .newTimePair(0.0F, 3.5F)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.1F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EMsounds.SHOT.get(), 0.75F, 0.0F, 0.0F), Side.CLIENT
                     )
                  }
               )
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(35, 5, 1.0F, 3.0F, 1.0F)})
               .addEvents(
                  new AnimationEvent[]{
                     skillparameter.bladeEffekTrail(
                        frame(24), frame(35), InteractionHand.MAIN_HAND, "k", 0.0, 0.0, -2.4, 0.0, 0.0, -2.3, 0.0F, 0.0F, 0.0F, 1.0F, 2.0, 1.0
                     )
                  }
               )
      );
      TENTAI_SEITOU_TSKILL4 = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_tskill4",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        55, 70, 80, InteractionHand.MAIN_HAND, 2.5F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .newTimePair(0.0F, 3.5F)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.3F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EMsounds.SHOT.get(), 0.75F, 0.0F, 0.0F), Side.CLIENT
                     )
                  }
               )
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(65, 5, 1.0F, 3.0F, 1.0F)})
               .addEvents(
                  new AnimationEvent[]{
                     skillparameter.bladeEffekTrail(
                        frame(53), frame(75), InteractionHand.MAIN_HAND, "k", 0.0, 0.0, -2.4, 0.0, 0.0, -2.3, 0.0F, 0.0F, 0.0F, 1.0F, 2.0, 1.0
                     )
                  }
               )
      );
      TENTAI_SEITOU_TSKILL5 = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_tskill5",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.1F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        80, 86, 150, InteractionHand.MAIN_HAND, 2.0F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        1.1F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EMsounds.SHOT.get(), 0.75F, 0.0F, 0.0F), Side.CLIENT
                     )
                  }
               )
               .addEvents(new AnimationEvent[]{EMEventsutil.Image(1.2F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(85, 5, 1.0F, 3.0F, 1.0F)})
               .addEvents(
                  new AnimationEvent[]{
                     skillparameter.bladeEffekTrail(
                        frame(79), frame(85), InteractionHand.MAIN_HAND, "k", 0.0, 0.0, -2.4, 0.0, 0.0, -2.3, 0.0F, 0.0F, 0.0F, 1.0F, 2.0, 1.0
                     )
                  }
               )
      );
      TENTAI_SEITOU_TSKILL6 = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_tskill6",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        20, 40, 105, InteractionHand.MAIN_HAND, 2.0F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD)
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, null)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.15F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EMsounds.SHOT.get(), 0.75F, 0.0F, 0.0F), Side.CLIENT
                     )
                  }
               )
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(30, 5, 1.0F, 3.0F, 1.0F)})
               .addEvents(new AnimationEvent[]{EMEventsutil.Image(0.25F)})
               .addEvents(
                  new AnimationEvent[]{
                     skillparameter.bladeEffekTrail(
                        frame(20), frame(30), InteractionHand.MAIN_HAND, "k", 0.0, 0.0, -2.4, 0.0, 0.0, -2.3, 0.0F, 0.0F, 0.0F, 1.0F, 2.0, 1.0
                     )
                  }
               )
      );
      TENTAI_SEITOU_TSKILL7 = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_tskill7",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        55, 70, 140, InteractionHand.MAIN_HAND, 2.0F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD)
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, null)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.3F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EMsounds.SHOT.get(), 0.75F, 0.0F, 0.0F), Side.CLIENT
                     )
                  }
               )
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(65, 5, 1.0F, 3.0F, 1.0F)})
               .addEvents(new AnimationEvent[]{EMEventsutil.Image(0.85F)})
               .addEvents(
                  new AnimationEvent[]{
                     skillparameter.bladeEffekTrail(
                        frame(50), frame(70), InteractionHand.MAIN_HAND, "k", 0.0, 0.0, -2.4, 0.0, 0.0, -2.3, 0.0F, 0.0F, 0.0F, 1.0F, 2.0, 1.0
                     )
                  }
               )
      );
      TENTAI_SEITOU_TSKILL8 = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_tskill8",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        60, 70, 135, InteractionHand.MAIN_HAND, 2.0F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD)
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, null)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.05F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EMsounds.SHOT4.get(), 0.75F, 0.0F, 0.0F), Side.CLIENT
                     )
                  }
               )
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(70, 5, 1.0F, 3.0F, 1.0F)})
               .addEvents(new AnimationEvent[]{EMEventsutil.Image(0.9F)})
               .addEvents(
                  new AnimationEvent[]{
                     skillparameter.bladeEffekTrail(
                        frame(58), frame(80), InteractionHand.MAIN_HAND, "k", 0.0, 0.0, -2.4, 0.0, 0.0, -2.3, 0.0F, 0.0F, 0.0F, 1.0F, 2.0, 1.0
                     )
                  }
               )
      );
      TENTAI_SEITOU_TSKILL9 = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_tskill9",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        30, 40, 135, InteractionHand.MAIN_HAND, 1.5F, 1.6F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, PiswordBig
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        50, 65, 75, InteractionHand.MAIN_HAND, 1.5F, 1.6F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.3F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EMsounds.SHOT.get(), 0.75F, 0.0F, 0.0F), Side.CLIENT
                     )
                  }
               )
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.9F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EMsounds.SHOT.get(), 0.75F, 0.0F, 0.0F), Side.CLIENT
                     )
                  }
               )
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(40, 5, 1.0F, 3.0F, 1.0F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(55, 5, 1.0F, 3.0F, 1.0F)})
               .addEvents(
                  new AnimationEvent[]{
                     skillparameter.bladeEffekTrail(
                        frame(24), frame(35), InteractionHand.MAIN_HAND, "k", 0.0, 0.0, -2.4, 0.0, 0.0, -2.3, 0.0F, 0.0F, 0.0F, 1.0F, 2.0, 1.0
                     )
                  }
               )
               .addEvents(
                  new AnimationEvent[]{
                     skillparameter.bladeEffekTrail(
                        frame(50), frame(60), InteractionHand.MAIN_HAND, "k", 0.0, 0.0, -2.4, 0.0, 0.0, -2.3, 0.0F, 0.0F, 0.0F, 1.0F, 2.0, 1.0
                     )
                  }
               )
      );
      TENTAI_SEITOU_TSKILL10 = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_tskill10",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        170, 180, 200, InteractionHand.MAIN_HAND, 10.0F, 10.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, Pisword
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(45, 10, 3.0F, 5.0F, 4.0F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(120, 10, 3.0F, 5.0F, 4.0F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(45, 10, 3.0F, 5.0F, 4.0F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(120, 10, 3.0F, 5.0F, 4.0F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(170, 20, 20.0F, 5.0F, 4.0F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleGroundSplit(45, 0.0, 0.0, 0.0, 0.0, 1.5F, true)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleGroundSplit(120, 0.0, 0.0, 0.0, 0.0, 1.5F, true)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(170, 30, 20.0F, 5.0F, 4.0F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleGroundSplit(170, 0.0, 0.0, 0.0, 0.0, 5.0F, true)})
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.7F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EMsounds.SHOT3.get(), 1.0F, 0.0F, 0.0F), Side.CLIENT
                     )
                  }
               )
               .addEvents(new AnimationEvent[]{EMEventsutil.Image(2.37F)})
               .addEvents(
                  new AnimationEvent[]{
                     skillparameter.bladeEffekTrail(
                        frame(164), frame(174), InteractionHand.MAIN_HAND, "k", 0.0, 0.0, -2.4, 0.0, 0.0, -2.3, 0.0F, 0.0F, 0.0F, 1.0F, 2.0, 1.0
                     )
                  }
               )
      );
      TENTAI_SEITOU_ESKILL = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_eskill",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        170, 180, 200, InteractionHand.MAIN_HAND, 3.0F, 3.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, Pisword
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(45, 10, 3.0F, 5.0F, 4.0F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(120, 10, 3.0F, 5.0F, 4.0F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(170, 20, 20.0F, 5.0F, 4.0F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleGroundSplit(45, 0.0, 0.0, 0.0, 0.0, 1.5F, true)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleGroundSplit(120, 0.0, 0.0, 0.0, 0.0, 1.5F, true)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleGroundSplit(170, 0.0, 0.0, 0.0, 0.0, 4.0F, true)})
      );
      TENTAI_SEITOU_ESKILL2 = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_eskill2",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        60, 70, 100, InteractionHand.MAIN_HAND, 1.5F, 1.5F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .newTimePair(0.0F, 3.5F)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(70, 5, 1.0F, 3.0F, 1.0F)})
      );
      TENTAI_SEITOU_ESKILL3 = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_eskill3",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        26, 36, 80, InteractionHand.MAIN_HAND, 1.5F, 1.5F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .newTimePair(0.0F, 3.5F)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(35, 5, 1.0F, 3.0F, 1.0F)})
      );
      TENTAI_SEITOU_ESKILL4 = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_eskill4",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        55, 70, 80, InteractionHand.MAIN_HAND, 1.5F, 1.5F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .newTimePair(0.0F, 3.5F)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(65, 5, 1.0F, 3.0F, 1.0F)})
      );
      TENTAI_SEITOU_ESKILL5 = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_eskill5",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.1F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        80, 86, 150, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{EMEventsutil.Image(1.2F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(85, 5, 1.0F, 3.0F, 1.0F)})
      );
      TENTAI_SEITOU_ESKILL6 = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_eskill6",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        20, 40, 105, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD)
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, null)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{EMEventsutil.Image(0.25F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(30, 5, 1.0F, 3.0F, 1.0F)})
      );
      TENTAI_SEITOU_ESKILL7 = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_eskill7",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        55, 70, 140, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD)
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, null)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{EMEventsutil.Image(0.85F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(45, 5, 1.0F, 3.0F, 1.0F)})
      );
      TENTAI_SEITOU_ESKILL8 = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_eskill8",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        60, 70, 135, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD)
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, null)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{EMEventsutil.Image(0.9F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(70, 5, 1.0F, 3.0F, 1.0F)})
      );
      TENTAI_SEITOU_ESKILL9 = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_eskill9",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        30, 40, 135, InteractionHand.MAIN_HAND, 0.7F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, PiswordBig
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        50, 65, 75, InteractionHand.MAIN_HAND, 0.7F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(40, 5, 1.0F, 3.0F, 1.0F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(65, 5, 1.0F, 3.0F, 1.0F)})
      );
      TENTAI_SEITOU_ESKILL10 = builder.nextAccessor(
         "biped/tiantui/tentai_seitou_eskill10",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        170, 180, 200, InteractionHand.MAIN_HAND, 3.0F, 3.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, Pisword
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(45, 10, 3.0F, 5.0F, 4.0F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(120, 10, 3.0F, 5.0F, 4.0F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(45, 10, 3.0F, 5.0F, 4.0F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(120, 10, 3.0F, 5.0F, 4.0F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(170, 20, 20.0F, 5.0F, 4.0F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleGroundSplit(45, 0.0, 0.0, 0.0, 0.0, 1.5F, true)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleGroundSplit(120, 0.0, 0.0, 0.0, 0.0, 1.5F, true)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(170, 30, 20.0F, 5.0F, 4.0F)})
               .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleGroundSplit(170, 0.0, 0.0, 0.0, 0.0, 5.0F, true)})
               .addEvents(new AnimationEvent[]{EMEventsutil.Image(2.37F)})
      );
      DUAL_IDLE = builder.nextAccessor(
         "biped/dual/dual_idle",
         accessor -> new StaticAnimation(true, accessor, Armatures.BIPED)
               .addProperty(StaticAnimationProperty.ON_BEGIN_EVENTS, List.of(SimpleEvent.create((livingEntityPatch, assetAccessor, animationParameters) -> {
                  LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
                  ItemStack itemStack = livingEntity.m_21205_();
                  if (!EMboolean.isHoldingGuardKey(livingEntity)) {
                     itemStack.m_41784_().m_128405_("weapon_mode", 0);
                  }
               }, Side.CLIENT)))
      );
      DUAL_RELOAD = builder.nextAccessor(
         "biped/dual/dual_reload",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        10, 11, 70, InteractionHand.MAIN_HAND, 0.1F, 0.1F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     )
                  }
               )
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(1, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(5, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(15, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(20, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(25, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(30, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(35, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(40, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(41, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(45, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(100, 0)})
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.1F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EMsounds.RELOAD3.get(), 0.6F, 0.0F, 0.0F), Side.CLIENT
                     )
                  }
               )
      );
      DUAL_GUARD = builder.nextAccessor(
         "biped/dual/dual_guard",
         accessor -> new StaticAnimation(0.05F, true, accessor, Armatures.BIPED)
               .addProperty(StaticAnimationProperty.ON_BEGIN_EVENTS, List.of(SimpleEvent.create((livingEntityPatch, assetAccessor, animationParameters) -> {
                  LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
                  ItemStack itemStack = livingEntity.m_21205_();
                  itemStack.m_41784_().m_128405_("weapon_mode", 1);
               }, Side.CLIENT)))
      );
      DUAL_WALK = builder.nextAccessor(
         "biped/dual/dual_walk",
         accessor -> (MovementAnimation)new MovementAnimation(true, accessor, Armatures.BIPED)
               .addProperty(StaticAnimationProperty.ON_BEGIN_EVENTS, List.of(SimpleEvent.create((livingEntityPatch, assetAccessor, animationParameters) -> {
                  LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
                  if (!EMboolean.isHoldingGuardKey(livingEntity)) {
                     ItemStack itemStack = livingEntity.m_21205_();
                     itemStack.m_41784_().m_128405_("weapon_mode", 0);
                  }
               }, Side.CLIENT)))
      );
      DUAL_RUN = builder.nextAccessor(
         "biped/dual/dual_run",
         accessor -> (MovementAnimation)new MovementAnimation(true, accessor, Armatures.BIPED)
               .addProperty(StaticAnimationProperty.ON_BEGIN_EVENTS, List.of(SimpleEvent.create((livingEntityPatch, assetAccessor, animationParameters) -> {
                  LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
                  if (!livingEntity.m_21254_()) {
                     ItemStack itemStack = livingEntity.m_21205_();
                     itemStack.m_41784_().m_128405_("weapon_mode", 0);
                  }
               }, Side.CLIENT)))
      );
      DUAL_AIR = builder.nextAccessor(
         "biped/dual/dual_air",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        5, 15, 30, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        20, 27, 30, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.2F}))
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> {
                  LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
                  MobEffectInstance mobEffectInstance = livingEntity.m_21124_((MobEffect)EMeffects.ACCELERATING_FUTURE.get());
                  if (mobEffectInstance != null) {
                     int amp = mobEffectInstance.m_19564_() + 1;
                     return 1.0F + (float)amp * 0.06F;
                  } else {
                     return 1.0F;
                  }
               })
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(2, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(20, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(30, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(40, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(50, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(60, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(80, 0)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.05F, 0.45F, InteractionHand.MAIN_HAND)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.05F, 0.45F, InteractionHand.OFF_HAND)})
               .addProperty(StaticAnimationProperty.ON_END_EVENTS, List.of(EMsimple.clearSparkleTrail()))
      );
      DUAL_DASH1 = builder.nextAccessor(
         "biped/dual/dual_dash1",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        19, 21, 45, InteractionHand.MAIN_HAND, 0.3F, 0.3F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        22, 23, 45, InteractionHand.MAIN_HAND, 0.3F, 0.3F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        24, 25, 45, InteractionHand.MAIN_HAND, 0.3F, 0.3F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        26, 27, 45, InteractionHand.MAIN_HAND, 0.3F, 0.3F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        28, 29, 45, InteractionHand.MAIN_HAND, 0.3F, 0.3F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        30, 31, 45, InteractionHand.MAIN_HAND, 0.3F, 0.3F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        32, 33, 45, InteractionHand.MAIN_HAND, 0.3F, 0.3F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        34, 39, 45, InteractionHand.MAIN_HAND, 0.3F, 0.3F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        40, 41, 45, InteractionHand.MAIN_HAND, 0.3F, 0.3F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> {
                  LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
                  MobEffectInstance mobEffectInstance = livingEntity.m_21124_((MobEffect)EMeffects.ACCELERATING_FUTURE.get());
                  if (mobEffectInstance != null) {
                     int amp = mobEffectInstance.m_19564_() + 1;
                     return 1.0F + (float)amp * 0.03F;
                  } else {
                     return 1.0F;
                  }
               })
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(2, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(20, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(30, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(40, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(50, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(60, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(70, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(90, 0)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail2(0.3F, 0.5F, InteractionHand.OFF_HAND)})
               .addProperty(StaticAnimationProperty.ON_END_EVENTS, List.of(EMsimple.clearSparkleTrail()))
               .addEvents(
                  new AnimationEvent[]{skillparameter.attachedEffek(0.1F, "eye", skillparameter.EffekAttachPart.HEAD, 0.0, 0.0, -0.2, 0.0F, 0.0F, 0.0F, 1.0F)}
               )
               .addEvents(
                  new AnimationEvent[]{skillparameter.attachedWeaponEffek(0.1F, InteractionHand.OFF_HAND, "sword", 0.0, 0.05, -0.3, 0.0F, 100.0F, 0.0F, 1.0F)}
               )
      );
      DUAL_DASH2 = builder.nextAccessor(
         "biped/dual/dual_dash2",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        20, 25, 35, InteractionHand.MAIN_HAND, 2.0F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> {
                  LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
                  MobEffectInstance mobEffectInstance = livingEntity.m_21124_((MobEffect)EMeffects.ACCELERATING_FUTURE.get());
                  if (mobEffectInstance != null) {
                     int amp = mobEffectInstance.m_19564_() + 1;
                     return 1.0F + (float)amp * 0.06F;
                  } else {
                     return 1.0F;
                  }
               })
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(2, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(20, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(30, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(40, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(50, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(60, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(70, 0)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail2(0.05F, 0.1F, InteractionHand.OFF_HAND)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail2(0.3F, 0.45F, InteractionHand.OFF_HAND)})
               .addProperty(StaticAnimationProperty.ON_END_EVENTS, List.of(EMsimple.clearSparkleTrail()))
      );
      DUAL_DASH3 = builder.nextAccessor(
         "biped/dual/dual_dash3",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        28, 36, 38, InteractionHand.MAIN_HAND, 2.0F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> {
                  LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
                  MobEffectInstance mobEffectInstance = livingEntity.m_21124_((MobEffect)EMeffects.ACCELERATING_FUTURE.get());
                  if (mobEffectInstance != null) {
                     int amp = mobEffectInstance.m_19564_() + 1;
                     return 1.0F + (float)amp * 0.06F;
                  } else {
                     return 1.0F;
                  }
               })
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(2, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(70, 0)})
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD)
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, null)
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.13F, 0.27F, InteractionHand.MAIN_HAND)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.0F, 0.27F, InteractionHand.OFF_HAND)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.28F, 0.5F, InteractionHand.MAIN_HAND)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.28F, 0.5F, InteractionHand.OFF_HAND)})
               .addProperty(StaticAnimationProperty.ON_END_EVENTS, List.of(EMsimple.clearSparkleTrail()))
      );
      DUAL_DODGE = builder.nextAccessor(
         "biped/dual/dual_dodge",
         accessor -> (DodgeAnimation)new DodgeAnimation(0.05F, accessor, 0.6F, 0.8F, Armatures.BIPED)
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD)
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.RAW_COORD)
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .newTimePair(0.1F, 3.5F)
               .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, true)
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, true)
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(0, 0)})
               .addEvents(
                  new AnimationEvent[]{
                     skillparameter.attachedEffek(frame(5), "eye", skillparameter.EffekAttachPart.HEAD, 0.0, 0.0, -0.2, 0.0F, 0.0F, 0.0F, 1.0F)
                  }
               )
      );
      DUAL_AUTO1 = builder.nextAccessor(
         "biped/dual/dual_auto1",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        18, 24, 48, InteractionHand.MAIN_HAND, 0.75F, 0.75F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        40, 45, 48, InteractionHand.MAIN_HAND, 0.75F, 0.75F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        35, 38, 48, InteractionHand.MAIN_HAND, 0.75F, 0.75F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> {
                  LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
                  MobEffectInstance mobEffectInstance = livingEntity.m_21124_((MobEffect)EMeffects.ACCELERATING_FUTURE.get());
                  if (mobEffectInstance != null) {
                     int amp = mobEffectInstance.m_19564_() + 1;
                     return 1.0F + (float)amp * 0.06F;
                  } else {
                     return 1.0F;
                  }
               })
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(2, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(50, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(75, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(95, 0)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.2F, 0.6F, InteractionHand.MAIN_HAND)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.2F, 0.6F, InteractionHand.OFF_HAND)})
               .addProperty(StaticAnimationProperty.ON_END_EVENTS, List.of(EMsimple.clearSparkleTrail()))
               .addEvents(
                  new AnimationEvent[]{skillparameter.attachedEffek(0.1F, "eye", skillparameter.EffekAttachPart.HEAD, 0.0, 0.0, -0.2, 0.0F, 0.0F, 0.0F, 1.0F)}
               )
               .addEvents(
                  new AnimationEvent[]{skillparameter.attachedWeaponEffek(0.1F, InteractionHand.OFF_HAND, "sword", 0.0, 0.05, -0.3, 0.0F, 100.0F, 0.0F, 1.0F)}
               )
      );
      DUAL_AUTO2 = builder.nextAccessor(
         "biped/dual/dual_auto2",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        14, 25, 30, InteractionHand.MAIN_HAND, 2.0F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> {
                  LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
                  MobEffectInstance mobEffectInstance = livingEntity.m_21124_((MobEffect)EMeffects.ACCELERATING_FUTURE.get());
                  if (mobEffectInstance != null) {
                     int amp = mobEffectInstance.m_19564_() + 1;
                     return 1.0F + (float)amp * 0.06F;
                  } else {
                     return 1.0F;
                  }
               })
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(2, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(40, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(60, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(70, 0)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.15F, 0.3F, InteractionHand.MAIN_HAND)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.15F, 0.3F, InteractionHand.OFF_HAND)})
               .addProperty(StaticAnimationProperty.ON_END_EVENTS, List.of(EMsimple.clearSparkleTrail()))
      );
      DUAL_AUTO3 = builder.nextAccessor(
         "biped/dual/dual_auto3",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        19, 33, 35, InteractionHand.MAIN_HAND, 2.0F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> {
                  LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
                  MobEffectInstance mobEffectInstance = livingEntity.m_21124_((MobEffect)EMeffects.ACCELERATING_FUTURE.get());
                  if (mobEffectInstance != null) {
                     int amp = mobEffectInstance.m_19564_() + 1;
                     return 1.0F + (float)amp * 0.06F;
                  } else {
                     return 1.0F;
                  }
               })
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(2, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(40, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(60, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(70, 0)})
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD)
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, null)
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.19F, 0.3F, InteractionHand.MAIN_HAND)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.19F, 0.3F, InteractionHand.OFF_HAND)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.03F, 0.1F, InteractionHand.MAIN_HAND)})
               .addProperty(StaticAnimationProperty.ON_END_EVENTS, List.of(EMsimple.clearSparkleTrail()))
      );
      DUAL_COUNTER = builder.nextAccessor(
         "biped/dual/dual_counter",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        15, 17, 50, InteractionHand.MAIN_HAND, 0.5F, 0.5F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        19, 23, 50, InteractionHand.MAIN_HAND, 0.5F, 0.5F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        28, 35, 50, InteractionHand.MAIN_HAND, 0.5F, 0.5F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        42, 45, 50, InteractionHand.MAIN_HAND, 0.5F, 0.5F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> {
                  LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
                  MobEffectInstance mobEffectInstance = livingEntity.m_21124_((MobEffect)EMeffects.ACCELERATING_FUTURE.get());
                  if (mobEffectInstance != null) {
                     int amp = mobEffectInstance.m_19564_() + 1;
                     return 1.0F + (float)amp * 0.06F;
                  } else {
                     return 1.0F;
                  }
               })
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(2, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(50, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(75, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(92, 0)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.17F, 0.5F, InteractionHand.MAIN_HAND)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.17F, 0.5F, InteractionHand.OFF_HAND)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.02F, 0.15F, InteractionHand.MAIN_HAND)})
               .addProperty(StaticAnimationProperty.ON_END_EVENTS, List.of(EMsimple.clearSparkleTrail()))
      );
      DUAL_COUNTER2 = builder.nextAccessor(
         "biped/dual/dual_counter2",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        15, 17, 50, InteractionHand.MAIN_HAND, 0.6F, 0.6F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        19, 23, 50, InteractionHand.MAIN_HAND, 0.6F, 0.6F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        28, 35, 50, InteractionHand.MAIN_HAND, 0.6F, 0.6F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        42, 45, 50, InteractionHand.MAIN_HAND, 0.6F, 0.6F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> {
                  LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
                  MobEffectInstance mobEffectInstance = livingEntity.m_21124_((MobEffect)EMeffects.ACCELERATING_FUTURE.get());
                  if (mobEffectInstance != null) {
                     int amp = mobEffectInstance.m_19564_() + 1;
                     return 1.0F + (float)amp * 0.06F;
                  } else {
                     return 1.0F;
                  }
               })
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(2, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(50, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(75, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(92, 0)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.17F, 0.5F, InteractionHand.MAIN_HAND)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.17F, 0.5F, InteractionHand.OFF_HAND)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.02F, 0.15F, InteractionHand.MAIN_HAND)})
               .addProperty(StaticAnimationProperty.ON_END_EVENTS, List.of(EMsimple.clearSparkleTrail()))
      );
      DUAL_SKILL = builder.nextAccessor(
         "biped/dual/dual_skill",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        22, 26, 100, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        36, 39, 100, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        40, 44, 100, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        59, 64, 100, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        74, 78, 100, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(2, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(40, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(60, 2)})
               .addProperty(StaticAnimationProperty.ON_BEGIN_EVENTS, List.of(SimpleEvent.create((livingEntityPatch, assetAccessor, animationParameters) -> {
                  LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
                  if (livingEntity.m_21023_((MobEffect)EMeffects.ACCELERATING_FUTURE.get())) {
                     MobEffectInstance mobEffectInstance = livingEntity.m_21124_((MobEffect)EMeffects.ACCELERATING_FUTURE.get());
                     int amp = mobEffectInstance.m_19564_();
                     int time = mobEffectInstance.m_19557_();
                     MobEffectInstance mobEffectInstance1 = new MobEffectInstance((MobEffect)EMeffects.ACCELERATING_FUTURE.get(), 1000, amp);
                     livingEntity.m_21195_((MobEffect)EMeffects.ACCELERATING_FUTURE.get());
                     livingEntity.m_7292_(mobEffectInstance1);
                  }
               }, Side.CLIENT)))
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.2F, 1.0F, InteractionHand.MAIN_HAND)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.2F, 1.0F, InteractionHand.OFF_HAND)})
               .addProperty(StaticAnimationProperty.ON_END_EVENTS, List.of(EMsimple.clearSparkleTrail()))
               .addEvents(
                  new AnimationEvent[]{skillparameter.attachedEffek(0.1F, "eye", skillparameter.EffekAttachPart.HEAD, 0.0, 0.0, -0.2, 0.0F, 0.0F, 0.0F, 1.0F)}
               )
               .addEvents(
                  new AnimationEvent[]{skillparameter.attachedWeaponEffek(0.1F, InteractionHand.OFF_HAND, "sword", 0.0, 0.05, -0.3, 0.0F, 100.0F, 0.0F, 1.0F)}
               )
      );
      DUAL_SKILL2 = builder.nextAccessor(
         "biped/dual/dual_skill2",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        13, 18, 70, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        23, 26, 70, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        27, 35, 70, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(2, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(40, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(60, 2)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.3F, 0.5F, InteractionHand.MAIN_HAND)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.3F, 0.5F, InteractionHand.OFF_HAND)})
               .addProperty(StaticAnimationProperty.ON_END_EVENTS, List.of(EMsimple.clearSparkleTrail()))
      );
      DUAL_SKILL3 = builder.nextAccessor(
         "biped/dual/dual_skill3",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        0, 12, 20, InteractionHand.MAIN_HAND, 1.5F, 1.5F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(2, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(40, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(80, 0)})
               .addProperty(StaticAnimationProperty.ON_BEGIN_EVENTS, List.of(SimpleEvent.create((livingEntityPatch, assetAccessor, animationParameters) -> {
                  LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
                  if (livingEntity.m_21023_((MobEffect)EMeffects.ACCELERATING_FUTURE.get())) {
                     MobEffectInstance mobEffectInstance = livingEntity.m_21124_((MobEffect)EMeffects.ACCELERATING_FUTURE.get());
                     int amp = mobEffectInstance.m_19564_();
                     int time = mobEffectInstance.m_19557_();
                     MobEffectInstance mobEffectInstance1 = new MobEffectInstance((MobEffect)EMeffects.ACCELERATING_FUTURE.get(), 60, amp);
                     livingEntity.m_21195_((MobEffect)EMeffects.ACCELERATING_FUTURE.get());
                     livingEntity.m_7292_(mobEffectInstance1);
                  }
               }, Side.CLIENT)))
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.0F, 0.15F, InteractionHand.MAIN_HAND)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.0F, 0.15F, InteractionHand.OFF_HAND)})
               .addProperty(StaticAnimationProperty.ON_END_EVENTS, List.of(EMsimple.clearSparkleTrail()))
      );
      DUAL_SKILL4 = builder.nextAccessor(
         "biped/dual/dual_skill4",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        36, 45, 80, InteractionHand.MAIN_HAND, 2.2F, 2.2F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(2, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(20, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(30, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(40, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(50, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(60, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(70, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(80, 3)})
               .addProperty(StaticAnimationProperty.ON_BEGIN_EVENTS, List.of(SimpleEvent.create((livingEntityPatch, assetAccessor, animationParameters) -> {
                  LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
                  if (livingEntity.m_21023_((MobEffect)EMeffects.ACCELERATING_FUTURE.get())) {
                     MobEffectInstance mobEffectInstance = livingEntity.m_21124_((MobEffect)EMeffects.ACCELERATING_FUTURE.get());
                     int amp = mobEffectInstance.m_19564_();
                     int time = mobEffectInstance.m_19557_();
                     MobEffectInstance mobEffectInstance1 = new MobEffectInstance((MobEffect)EMeffects.ACCELERATING_FUTURE.get(), 1000, amp);
                     livingEntity.m_21195_((MobEffect)EMeffects.ACCELERATING_FUTURE.get());
                     livingEntity.m_7292_(mobEffectInstance1);
                  }
               }, Side.CLIENT)))
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail2(0.17F, 0.3F, InteractionHand.OFF_HAND)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail2(0.46F, 0.6F, InteractionHand.OFF_HAND)})
               .addProperty(StaticAnimationProperty.ON_END_EVENTS, List.of(EMsimple.clearSparkleTrail()))
               .addEvents(
                  new AnimationEvent[]{skillparameter.attachedEffek(0.1F, "eye", skillparameter.EffekAttachPart.HEAD, 0.0, 0.0, -0.2, 0.0F, 0.0F, 0.0F, 1.0F)}
               )
               .addEvents(
                  new AnimationEvent[]{skillparameter.attachedWeaponEffek(0.1F, InteractionHand.OFF_HAND, "sword", 0.0, 0.05, -0.3, 0.0F, 100.0F, 0.0F, 1.0F)}
               )
      );
      DUAL_SKILL5 = builder.nextAccessor(
         "biped/dual/dual_skill5",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        4, 5, 80, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        6, 7, 80, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        8, 9, 80, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        10, 11, 80, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        12, 13, 80, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        14, 15, 80, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        16, 17, 80, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        18, 19, 80, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        25, 28, 80, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(2, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(20, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(30, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(40, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(50, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(60, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(70, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(80, 3)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail2(0.14F, 0.5F, InteractionHand.OFF_HAND)})
               .addProperty(StaticAnimationProperty.ON_END_EVENTS, List.of(EMsimple.clearSparkleTrail()))
      );
      DUAL_SKILL6 = builder.nextAccessor(
         "biped/dual/dual_skill6",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        9, 13, 80, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        14, 18, 80, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        32, 36, 80, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        44, 47, 80, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        48, 51, 80, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(2, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(20, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(30, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(40, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(50, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(60, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(70, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(80, 2)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.0F, 0.64F, InteractionHand.MAIN_HAND)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.0F, 0.64F, InteractionHand.OFF_HAND)})
               .addProperty(StaticAnimationProperty.ON_END_EVENTS, List.of(EMsimple.clearSparkleTrail()))
      );
      DUAL_SKILL7 = builder.nextAccessor(
         "biped/dual/dual_skill7",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        5, 8, 80, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        9, 12, 80, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        16, 19, 80, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(2, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(40, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(60, 2)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.04F, 0.35F, InteractionHand.MAIN_HAND)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.04F, 0.35F, InteractionHand.OFF_HAND)})
               .addProperty(StaticAnimationProperty.ON_END_EVENTS, List.of(EMsimple.clearSparkleTrail()))
      );
      DUAL_SKILL8 = builder.nextAccessor(
         "biped/dual/dual_skill8",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        3, 10, 20, InteractionHand.MAIN_HAND, 1.5F, 1.5F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(2, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(40, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(80, 0)})
               .addProperty(StaticAnimationProperty.ON_BEGIN_EVENTS, List.of(SimpleEvent.create((livingEntityPatch, assetAccessor, animationParameters) -> {
                  LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
                  if (livingEntity.m_21023_((MobEffect)EMeffects.ACCELERATING_FUTURE.get())) {
                     MobEffectInstance mobEffectInstance = livingEntity.m_21124_((MobEffect)EMeffects.ACCELERATING_FUTURE.get());
                     int amp = mobEffectInstance.m_19564_();
                     int time = mobEffectInstance.m_19557_();
                     MobEffectInstance mobEffectInstance1 = new MobEffectInstance((MobEffect)EMeffects.ACCELERATING_FUTURE.get(), 60, amp);
                     livingEntity.m_21195_((MobEffect)EMeffects.ACCELERATING_FUTURE.get());
                     livingEntity.m_7292_(mobEffectInstance1);
                  }
               }, Side.CLIENT)))
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.0F, 0.3F, InteractionHand.MAIN_HAND)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.0F, 0.3F, InteractionHand.OFF_HAND)})
               .addProperty(StaticAnimationProperty.ON_END_EVENTS, List.of(EMsimple.clearSparkleTrail()))
      );
      DUAL_SKILL9 = builder.nextAccessor(
         "biped/dual/dual_skill9",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        66, 71, 120, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        75, 78, 120, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        79, 84, 120, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(2, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(10, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(20, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(25, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(30, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(35, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(40, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(45, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(50, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(55, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(60, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(65, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(70, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(75, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(80, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(85, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(90, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(95, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(100, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(105, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(110, 2)})
               .addProperty(StaticAnimationProperty.ON_BEGIN_EVENTS, List.of(SimpleEvent.create((livingEntityPatch, assetAccessor, animationParameters) -> {
                  LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
                  if (livingEntity.m_21023_((MobEffect)EMeffects.ACCELERATING_FUTURE.get())) {
                     MobEffectInstance mobEffectInstance = livingEntity.m_21124_((MobEffect)EMeffects.ACCELERATING_FUTURE.get());
                     int amp = mobEffectInstance.m_19564_();
                     int time = mobEffectInstance.m_19557_();
                     MobEffectInstance mobEffectInstance1 = new MobEffectInstance((MobEffect)EMeffects.ACCELERATING_FUTURE.get(), 1000, amp);
                     livingEntity.m_21195_((MobEffect)EMeffects.ACCELERATING_FUTURE.get());
                     livingEntity.m_7292_(mobEffectInstance1);
                  }
               }, Side.CLIENT)))
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.65F, 1.0F, InteractionHand.MAIN_HAND)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.11F, 0.4F, InteractionHand.MAIN_HAND)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.65F, 1.0F, InteractionHand.OFF_HAND)})
               .addProperty(StaticAnimationProperty.ON_END_EVENTS, List.of(EMsimple.clearSparkleTrail()))
               .addEvents(
                  new AnimationEvent[]{skillparameter.attachedEffek(0.1F, "eye", skillparameter.EffekAttachPart.HEAD, 0.0, 0.0, -0.2, 0.0F, 0.0F, 0.0F, 1.0F)}
               )
      );
      DUAL_SKILL10 = builder.nextAccessor(
         "biped/dual/dual_skill10",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        7, 10, 80, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        11, 15, 80, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        20, 25, 80, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     ),
                     AvalonAnimationUtils.createSimplePhase(
                        35, 39, 80, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(2, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(20, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(30, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(40, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(50, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(60, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(70, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(80, 2)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.0F, 0.6F, InteractionHand.MAIN_HAND)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.0F, 0.6F, InteractionHand.OFF_HAND)})
               .addProperty(StaticAnimationProperty.ON_END_EVENTS, List.of(EMsimple.clearSparkleTrail()))
      );
      DUAL_SKILL11 = builder.nextAccessor(
         "biped/dual/dual_skill11",
         accessor -> (AvalonAttackAnimation)new NbtDamageAvalonAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  AvalonAnimationUtils.createSimplePhase(
                     6, 8, 250, InteractionHand.MAIN_HAND, 0.6F, 0.6F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     9, 14, 250, InteractionHand.MAIN_HAND, 0.6F, 0.6F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     20, 28, 250, InteractionHand.MAIN_HAND, 0.6F, 0.6F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     31, 34, 250, InteractionHand.MAIN_HAND, 0.6F, 0.6F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, (Collider)null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     63, 68, 250, InteractionHand.MAIN_HAND, 0.8F, 0.8F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     128, 129, 250, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     130, 131, 250, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     132, 133, 250, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     134, 135, 250, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     136, 137, 250, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     138, 139, 250, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     140, 141, 250, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     142, 143, 250, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     144, 145, 250, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     146, 147, 250, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     148, 149, 250, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     150, 151, 250, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     152, 153, 250, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     154, 155, 250, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     156, 157, 250, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     158, 159, 250, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     160, 161, 250, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     162, 163, 250, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     164, 165, 250, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     166, 167, 250, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     168, 169, 250, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     170, 171, 250, InteractionHand.MAIN_HAND, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                  )
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(2, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(20, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(30, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(40, 2)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(50, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(51, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(55, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(60, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(65, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(70, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(75, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(80, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(85, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(90, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(95, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(100, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(105, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(110, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(115, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(120, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(125, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(130, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(135, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(140, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(145, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(150, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(155, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(160, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(165, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(170, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(175, 3)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(180, 3)})
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD)
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, null)
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.0F, 0.4F, InteractionHand.MAIN_HAND)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.0F, 0.55F, InteractionHand.OFF_HAND)})
               .addEvents(new AnimationEvent[]{skillparameter.sparkleTrail(0.63F, 0.77F, InteractionHand.OFF_HAND)})
               .addProperty(StaticAnimationProperty.ON_END_EVENTS, List.of(EMsimple.clearSparkleTrail()))
      );
      DUAL_SKILL13 = builder.nextAccessor(
         "biped/dual/dual_skill13",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  1.0F,
                  1.0F,
                  new AvalonPhase[]{
                     AvalonAnimationUtils.createSimplePhase(
                        0, 10, 15, InteractionHand.MAIN_HAND, 1.5F, 1.5F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (Collider)null
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .newTimePair(0.0F, 3.5F)
               .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(1, 4)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(2, 4)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(10, 4)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(15, 4)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(20, 4)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(25, 4)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(30, 4)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(35, 4)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(40, 4)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(45, 4)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(50, 4)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(55, 4)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(60, 4)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(70, 4)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(80, 4)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(85, 1)})
               .addEvents(new AnimationEvent[]{EMEventsutil.ModeChange(95, 0)})
               .addProperty(StaticAnimationProperty.ON_BEGIN_EVENTS, List.of(SimpleEvent.create((livingEntityPatch, assetAccessor, animationParameters) -> {
                  LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
                  if (livingEntity.m_21023_((MobEffect)EMeffects.ACCELERATING_FUTURE.get())) {
                     MobEffectInstance mobEffectInstance = livingEntity.m_21124_((MobEffect)EMeffects.ACCELERATING_FUTURE.get());
                     int amp = mobEffectInstance.m_19564_();
                     int time = mobEffectInstance.m_19557_();
                     MobEffectInstance mobEffectInstance1 = new MobEffectInstance((MobEffect)EMeffects.ACCELERATING_FUTURE.get(), 60, amp);
                     livingEntity.m_21195_((MobEffect)EMeffects.ACCELERATING_FUTURE.get());
                     livingEntity.m_7292_(mobEffectInstance1);
                  }
               }, Side.CLIENT)))
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.0F, (entitypatch, self, params) -> {
                  if (entitypatch.getOriginal() instanceof Player) {
                     Player player = (Player)entitypatch.getOriginal();
                     Inventory inventory = player.m_150109_();
                     if (inventory.m_18947_((Item)EpicmoonItems.Accel_ROUND.get()) > 0) {
                        entitypatch.playSound((SoundEvent)EMsounds.RELOAD3.get(), 0.6F, 0.0F, 0.0F);
                     }
                  }
               }, Side.CLIENT)})
      );
   }
}
