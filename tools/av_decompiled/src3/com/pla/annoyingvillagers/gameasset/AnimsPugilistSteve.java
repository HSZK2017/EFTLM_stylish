package com.pla.annoyingvillagers.gameasset;

import com.pla.annoyingvillagers.animations.HeavyAttackAnimation;
import com.pla.annoyingvillagers.animations.RushSwordAnimation;
import com.pla.annoyingvillagers.entity.BlueDemonEntity;
import com.pla.annoyingvillagers.gameasset.AVAnimations.ReuseableEvents;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.item.BlueDemonTridentItem;
import com.pla.annoyingvillagers.util.ScreenShakeUtil;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.shelmarow.combat_evolution.gameassets.animation.ExecutionAttackAnimation;
import net.shelmarow.combat_evolution.gameassets.animation.ExecutionHitAnimation;
import net.shelmarow.combat_evolution.gameassets.animation.ExecutionAttackAnimation.ExecutionPhase;
import reascer.wom.animation.attacks.BasicMultipleAttackAnimation;
import reascer.wom.gameasset.colliders.WOMWeaponColliders;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.PlaybackSpeedModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.BasicAttackAnimation;
import yesman.epicfight.api.animation.types.DashAttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.GuardAnimation;
import yesman.epicfight.api.animation.types.KnockdownAnimation;
import yesman.epicfight.api.animation.types.LongHitAnimation;
import yesman.epicfight.api.animation.types.MovementAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.collider.MultiCollider;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.HitEntityList.Priority;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.gameasset.Animations.ReusableSources;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.damagesource.ExtraDamageInstance.ExtraDamage;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.MOD
)
public class AnimsPugilistSteve {
   public static AnimationAccessor<StaticAnimation> LAYING_DEATH;
   public static AnimationAccessor<LongHitAnimation> LAYING_DEATH_DEAD;
   public static AnimationAccessor<StaticAnimation> BLUE_DEMON_STATE_TRANSFORM;
   public static AnimationAccessor<StaticAnimation> BLUE_DEMON_STATE_TRANSFORM_END;
   public static AnimationAccessor<StaticAnimation> BLUE_DEMON_DIE;
   public static AnimationAccessor<ActionAnimation> TRIDENT_FESTIVAL;
   public static AnimationAccessor<BasicMultipleAttackAnimation> COUNTER;
   public static AnimationAccessor<StaticAnimation> FIST_GUARD;
   public static AnimationAccessor<BasicMultipleAttackAnimation> FIST_DASH;
   public static AnimationAccessor<BasicMultipleAttackAnimation> WHIRLWIND_KICK;
   public static AnimationAccessor<HeavyAttackAnimation> LEGENDARY_SWORD_HEAVY_ATTACK;
   public static AnimationAccessor<AttackAnimation> HACKER_SWORD_SKILL_TWOHAND;
   public static AnimationAccessor<BasicAttackAnimation> DUAL_SWORD_AUTO1;
   public static AnimationAccessor<BasicAttackAnimation> DUAL_SWORD_AUTO2;
   public static AnimationAccessor<BasicAttackAnimation> TRIDENT_DUAL_AUTO2;
   public static AnimationAccessor<BasicAttackAnimation> DUAL_SWORD_AUTO3;
   public static AnimationAccessor<BasicAttackAnimation> DUAL_SWORD_AUTO4;
   public static AnimationAccessor<BasicAttackAnimation> DUAL_SWORD_AUTO5;
   public static AnimationAccessor<BasicAttackAnimation> DUAL_SWORD1;
   public static AnimationAccessor<BasicAttackAnimation> DUAL_SWORD2;
   public static AnimationAccessor<BasicAttackAnimation> DUAL_SWORD3;
   public static AnimationAccessor<BasicMultipleAttackAnimation> FIST_UP;
   public static AnimationAccessor<RushSwordAnimation> RUSH_SWORD;
   public static AnimationAccessor<BasicMultipleAttackAnimation> DUAL_DANCING_EDGE;
   public static AnimationAccessor<AttackAnimation> SWEEPING_EDGE;
   public static AnimationAccessor<LongHitAnimation> HIT_BACKWARD;
   public static AnimationAccessor<GuardAnimation> SPEAR_GUARD_HIT;
   public static AnimationAccessor<StaticAnimation> LEGENDARY_SWORD_GUARD;
   public static AnimationAccessor<GuardAnimation> LEGENDARY_SWORD_GUARD_HIT;
   public static AnimationAccessor<GuardAnimation> LEGENDARY_SWORD_GUARD_PARRY;
   public static AnimationAccessor<ActionAnimation> POSE_UP;
   public static AnimationAccessor<BasicAttackAnimation> DAGGER_AUTO1;
   public static AnimationAccessor<BasicAttackAnimation> DAGGER_AUTO2;
   public static AnimationAccessor<BasicAttackAnimation> DAGGER_AUTO3;
   public static AnimationAccessor<BasicAttackAnimation> DAGGER_DUAL_AUTO1;
   public static AnimationAccessor<BasicAttackAnimation> DAGGER_DUAL_AUTO2;
   public static AnimationAccessor<BasicAttackAnimation> DAGGER_DUAL_AUTO3;
   public static AnimationAccessor<BasicAttackAnimation> DAGGER_DUAL_AUTO4;
   public static AnimationAccessor<StaticAnimation> CHECK;
   public static AnimationAccessor<MovementAnimation> BIPED_RUN_ESWORD;
   public static AnimationAccessor<StaticAnimation> KNIFE_IDLE;
   public static AnimationAccessor<MovementAnimation> KNIFE_RUN;
   public static AnimationAccessor<BasicAttackAnimation> KNIFE_ATTACK;
   public static AnimationAccessor<StaticAnimation> KNIFE_CHECK;
   public static AnimationAccessor<ActionAnimation> HOOK_GUN;
   public static AnimationAccessor<StaticAnimation> CARRY;
   public static AnimationAccessor<BasicMultipleAttackAnimation> FIST_LEFT;
   public static AnimationAccessor<KnockdownAnimation> KNOCKDOWN_FORWARD;
   public static AnimationAccessor<KnockdownAnimation> KNOCKDOWN_RIGHT;
   public static AnimationAccessor<KnockdownAnimation> KNOCKDOWN_LEFT;
   public static AnimationAccessor<BasicMultipleAttackAnimation> AXE_HEAVY_AUTO_1;
   public static AnimationAccessor<BasicMultipleAttackAnimation> AXE_HEAVY_AUTO_2;
   public static AnimationAccessor<BasicMultipleAttackAnimation> SWORD_HEAVY_AUTO_1;
   public static AnimationAccessor<BasicMultipleAttackAnimation> SWORD_HEAVY_AUTO_2;
   public static AnimationAccessor<BasicMultipleAttackAnimation> SWORD_HEAVY_AUTO_3;
   public static AnimationAccessor<BasicMultipleAttackAnimation> HARD_KICK;
   public static AnimationAccessor<LongHitAnimation> HARD_KICK_HIT;
   public static AnimationAccessor<ActionAnimation> RUN_START;
   public static AnimationAccessor<BasicMultipleAttackAnimation> LONGSWORD_AUTO1;
   public static AnimationAccessor<MovementAnimation> RUN_DUAL_BIG;
   public static AnimationAccessor<MovementAnimation> RUN_HOLD;
   public static AnimationAccessor<KnockdownAnimation> LONGEST_HIT;
   public static AnimationAccessor<StaticAnimation> HARD_GREATSWORD_GUARD;
   public static AnimationAccessor<GuardAnimation> HARD_GREATSWORD_GUARD_HIT;
   public static AnimationAccessor<ActionAnimation> HARD_GREATSWORD_GUARD_SKILL;
   public static AnimationAccessor<LongHitAnimation> HIT_LEFT;
   public static AnimationAccessor<LongHitAnimation> HIT_RIGHT;
   public static AnimationAccessor<ActionAnimation> SHAKE_HAND_TRY;
   public static AnimationAccessor<ActionAnimation> SHAKE_HAND;
   public static AnimationAccessor<ActionAnimation> FIST_TRY;
   public static AnimationAccessor<ActionAnimation> FISTING;
   public static AnimationAccessor<BasicAttackAnimation> GIANT_WHIRLWIND;
   public static AnimationAccessor<BasicAttackAnimation> DUAL_SWORD_DANCING_EDGE;
   public static AnimationAccessor<AttackAnimation> SPEAR_THRUST;
   public static AnimationAccessor<StaticAnimation> DUAL_TACHI_GUARD;
   public static AnimationAccessor<GuardAnimation> DUAL_TACHI_GUARD_HIT;
   public static AnimationAccessor<BasicMultipleAttackAnimation> WHIRLWIND_KICK_LEFT;
   public static AnimationAccessor<AttackAnimation> SUPER_PUNCH;
   public static AnimationAccessor<KnockdownAnimation> GUARD_BREAK_ATTACK;
   public static AnimationAccessor<DashAttackAnimation> SWORD_DASH;
   public static AnimationAccessor<DashAttackAnimation> TACHI_DASH;
   public static AnimationAccessor<BasicAttackAnimation> DUAL_SWORD_SKILL;
   public static AnimationAccessor<ActionAnimation> DUAL_END;
   public static AnimationAccessor<KnockdownAnimation> TRIED;
   public static AnimationAccessor<BasicAttackAnimation> GREATSWORD_SKILL;
   public static AnimationAccessor<BasicMultipleAttackAnimation> LEGENDARY_SWORD_WAKE_UP_ATTACK;
   public static AnimationAccessor<ActionAnimation> DUAL_E_END;
   public static AnimationAccessor<BasicAttackAnimation> AXE_FUN_SKILL;
   public static AnimationAccessor<BasicMultipleAttackAnimation> LEGENDARY_SWORD_AUTO_4;
   public static AnimationAccessor<BasicMultipleAttackAnimation> OBSIDIAN_FIST_DASH;
   public static AnimationAccessor<BasicMultipleAttackAnimation> SHADOW_OBSIDIAN_SWORD_ONEHAND_LONG;
   public static AnimationAccessor<BasicAttackAnimation> SHADOW_OBSIDIAN_SWORD_DUAL_SWORD_AUTO4;
   public static AnimationAccessor<BasicAttackAnimation> SHADOW_OBSIDIAN_SWORD_DUAL_SWORD_AUTO5;
   public static AnimationAccessor<BasicAttackAnimation> TRIDENT_THROW_2;
   public static AnimationAccessor<BasicAttackAnimation> TRIDENT_THROW_LEGENDARY;
   public static AnimationAccessor<ExecutionAttackAnimation> STRANGLE_EXECUTE;
   public static AnimationAccessor<ExecutionHitAnimation> STRANGLE_EXECUTE_HIT;
   public static AnimationAccessor<ExecutionAttackAnimation> WRESTLING_EXECUTE;
   public static AnimationAccessor<ExecutionHitAnimation> WRESTLING_EXECUTE_HIT;
   public static AnimationAccessor<ExecutionAttackAnimation> WRESTLING_BACK_EXECUTE;
   public static AnimationAccessor<ExecutionHitAnimation> WRESTLING_BACK_EXECUTE_HIT;
   public static AnimationAccessor<ExecutionAttackAnimation> STAB_EXECUTE;
   public static AnimationAccessor<ExecutionAttackAnimation> DUAL_STAB_EXECUTE;
   public static AnimationAccessor<ExecutionHitAnimation> STAB_EXECUTE_HIT;
   public static AnimationAccessor<ExecutionAttackAnimation> SHIELD_EXECUTE;
   public static AnimationAccessor<ExecutionHitAnimation> SHIELD_EXECUTE_HIT;
   private static final ExtraDamage TARGET_MAX_HEALTH = new ExtraDamage(
      (attacker, itemstack, target, baseDamage, params) -> params[0] + target.m_21233_() * params[1], (itemstack, tooltips, baseDamage, params) -> {
      }
   );

   public static void build(AnimationBuilder builder) {
      ArmatureAccessor<HumanoidArmature> humanoidArmature = Armatures.BIPED;
      MultiCollider<OBBCollider> executionCollider = new MultiOBBCollider(3, 1.25, 1.5, 1.5, 0.0, 1.5, -1.5);
      MultiCollider<OBBCollider> executionColliderBack = new MultiOBBCollider(3, 1.25, 1.5, 1.5, 0.0, 1.5, 1.5);
      LAYING_DEATH = builder.nextAccessor(
         "biped/pugilist_steve/death_emote",
         accessor -> new StaticAnimation(true, accessor, humanoidArmature)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true)
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
      );
      LAYING_DEATH_DEAD = builder.nextAccessor("biped/pugilist_steve/death_emote_dead", accessor -> new LongHitAnimation(0.16F, accessor, Armatures.BIPED));
      BLUE_DEMON_STATE_TRANSFORM = builder.nextAccessor(
         "biped/pugilist_steve/blue_demon_state_transform", accessor -> new StaticAnimation(true, accessor, humanoidArmature)
      );
      BLUE_DEMON_STATE_TRANSFORM_END = builder.nextAccessor(
         "biped/pugilist_steve/blue_demon_state_transform_end", accessor -> new StaticAnimation(false, accessor, humanoidArmature)
      );
      BLUE_DEMON_DIE = builder.nextAccessor("biped/pugilist_steve/blue_demon_die", accessor -> new StaticAnimation(false, accessor, humanoidArmature));
      TRIDENT_FESTIVAL = builder.nextAccessor(
         "biped/pugilist_steve/trident_festival",
         accessor -> (ActionAnimation)new ActionAnimation(0.05F, Float.MAX_VALUE, accessor, humanoidArmature)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.1F,
                        (livingEntityPatch, self, p) -> {
                           if (((LivingEntity)livingEntityPatch.getOriginal()).m_9236_() instanceof ServerLevel
                              && livingEntityPatch.getOriginal() instanceof BlueDemonEntity blueDemonEntity) {
                              blueDemonEntity.setState(1);
                              blueDemonEntity.m_5496_((SoundEvent)AnnoyingVillagersModSounds.BLUE_DEMON_SAY_TRIDENT_FESTIVAL.get(), 1.0F, 1.0F);
                           }
                        },
                        Side.SERVER
                     ),
                     InTimeEvent.create(0.2F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(0.2F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(0.3F, (livingEntityPatch, self, p) -> {
                        if (((LivingEntity)livingEntityPatch.getOriginal()).m_9236_() instanceof ServerLevel serverLevel) {
                           if (livingEntityPatch.getOriginal() instanceof BlueDemonEntity) {
                              BlueDemonTridentItem.summonMissingTridentAndAnimate(serverLevel, (LivingEntity)livingEntityPatch.getOriginal());
                           }

                           ScreenShakeUtil.applyScreenShake(serverLevel, ((LivingEntity)livingEntityPatch.getOriginal()).m_20183_().m_252807_(), 12.0, 80, 8);
                        }
                     }, Side.SERVER),
                     InTimeEvent.create(0.5F, (livingEntityPatch, self, p) -> {
                        if (((LivingEntity)livingEntityPatch.getOriginal()).m_9236_() instanceof ServerLevel serverLevel) {
                           BlueDemonTridentItem.spawnDamageZones(serverLevel, (LivingEntity)livingEntityPatch.getOriginal());
                           BlueDemonTridentItem.relaunchGroundedTridents(serverLevel, (LivingEntity)livingEntityPatch.getOriginal(), true);
                        }
                     }, Side.SERVER),
                     InTimeEvent.create(0.8F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(0.8F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(1.2F, (livingEntityPatch, self, p) -> {
                        if (((LivingEntity)livingEntityPatch.getOriginal()).m_9236_() instanceof ServerLevel serverLevel) {
                           BlueDemonTridentItem.relaunchGroundedTridents(serverLevel, (LivingEntity)livingEntityPatch.getOriginal(), true);
                        }
                     }, Side.SERVER),
                     InTimeEvent.create(1.2F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(1.2F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(1.8F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(1.8F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(2.2F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(2.2F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(2.8F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(2.8F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(3.2F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(3.2F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(3.5F, (livingEntityPatch, self, p) -> {
                        if (((LivingEntity)livingEntityPatch.getOriginal()).m_9236_() instanceof ServerLevel serverLevel) {
                           BlueDemonTridentItem.summonSuperLightningAtGroundedTridents(serverLevel, (LivingEntity)livingEntityPatch.getOriginal());
                           BlueDemonTridentItem.setStormEnergy(((LivingEntity)livingEntityPatch.getOriginal()).m_21205_(), 0);
                           BlueDemonTridentItem.setStormEnergy(((LivingEntity)livingEntityPatch.getOriginal()).m_21206_(), 0);
                           if (livingEntityPatch.getOriginal() instanceof BlueDemonEntity blueDemonEntity) {
                              blueDemonEntity.beginStateTwoTransform();
                              livingEntityPatch.playAnimationSynchronized(BLUE_DEMON_STATE_TRANSFORM, 0.0F);
                           }
                        }
                     }, Side.SERVER),
                     InTimeEvent.create(3.8F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(3.8F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER)
                  }
               )
      );
      COUNTER = builder.nextAccessor(
         "biped/pugilist_steve/counter",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.3F, 0.08F, 0.1F, 0.15F, 0.525F, ColliderPreset.FIST, ((HumanoidArmature)humanoidArmature.get()).legR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT.get())
               .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.COUNTER))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1.0F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.5F))
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.AIR_BURST)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(1.0F))
      );
      FIST_GUARD = builder.nextAccessor("biped/pugilist_steve/fist_guard", accessor -> new StaticAnimation(true, accessor, humanoidArmature));
      FIST_DASH = builder.nextAccessor(
         "biped/pugilist_steve/fist_dash",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.15F,
                  0.25F,
                  0.45F,
                  0.7F,
                  0.95F,
                  ColliderPreset.BIPED_BODY_COLLIDER,
                  ((HumanoidArmature)humanoidArmature.get()).toolR,
                  accessor,
                  humanoidArmature
               )
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.AIR_BURST)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get())
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.5F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.2F)
      );
      WHIRLWIND_KICK = builder.nextAccessor(
         "biped/pugilist_steve/whirlwind_kick",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.2F,
                  0.29F,
                  0.45F,
                  0.85F,
                  1.8F,
                  ColliderPreset.BIPED_BODY_COLLIDER,
                  ((HumanoidArmature)humanoidArmature.get()).legR,
                  accessor,
                  humanoidArmature
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.AIR_BURST)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get())
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.5F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
               .addEvents(
                  new AnimationEvent[]{InTimeEvent.create(0.1F, ReusableSources.PLAY_SOUND, Side.SERVER).params((SoundEvent)EpicFightSounds.WHOOSH.get())}
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.5F)
      );
      LEGENDARY_SWORD_HEAVY_ATTACK = builder.nextAccessor(
         "biped/pugilist_steve/legendary_sword_heavy_attack",
         accessor -> (HeavyAttackAnimation)new HeavyAttackAnimation(
                  0.05F,
                  0.05F,
                  0.5F,
                  0.7F,
                  1.2F,
                  WOMWeaponColliders.TORMENT_BERSERK_AIRSLAM,
                  ((HumanoidArmature)humanoidArmature.get()).rootJoint,
                  accessor,
                  humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(4.0F))
               .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.2F))
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(4.0F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.5F)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.3F}))
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicanimation, livingentitypatch, f, f1, pose) -> {
                  if (f1 >= 0.3F && f1 < 0.35F) {
                     float f2 = (float)((LivingEntity)livingentitypatch.getOriginal()).m_20185_();
                     float f3 = (float)((LivingEntity)livingentitypatch.getOriginal()).m_20186_();
                     float f4 = (float)((LivingEntity)livingentitypatch.getOriginal()).m_20189_();
                     BlockState blockstate = ((LivingEntity)livingentitypatch.getOriginal())
                        .m_9236_()
                        .m_8055_(new BlockPos(new Vec3i((int)f2, (int)f3, (int)f4)));

                     while ((blockstate.m_60734_() instanceof BushBlock || blockstate.m_60795_()) && !blockstate.m_60713_(Blocks.f_50626_)) {
                        blockstate = ((LivingEntity)livingentitypatch.getOriginal()).m_9236_().m_8055_(new BlockPos(new Vec3i((int)f2, (int)(--f3), (int)f4)));
                     }

                     float f5 = (float)Math.max(Math.abs(((LivingEntity)livingentitypatch.getOriginal()).m_20186_() - (double)f3) - 1.0, 0.0);
                     return 1.0F - (1.0F / (-f5 - 1.0F) + 1.0F);
                  } else {
                     return 1.0F;
                  }
               })
               .addEvents(
                  new InTimeEvent[]{
                     InTimeEvent.create(0.6F, reascer.wom.gameasset.ReuseableEvents.TORMENT_GROUNDSLAM, Side.CLIENT),
                     InTimeEvent.create(0.6F, ReuseableEvents.SHOCK_WAVE, Side.SERVER)
                  }
               )
      );
      HACKER_SWORD_SKILL_TWOHAND = builder.nextAccessor(
         "biped/pugilist_steve/hacker_sword_skill",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.05F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(
                        0.0F, 0.016F, 0.066F, 0.133F, 0.133F, InteractionHand.MAIN_HAND, ((HumanoidArmature)humanoidArmature.get()).toolL, ColliderPreset.SWORD
                     ),
                     new Phase(0.133F, 0.133F, 0.183F, 0.25F, 0.25F, ((HumanoidArmature)humanoidArmature.get()).toolR, ColliderPreset.SWORD),
                     new Phase(
                        0.25F, 0.25F, 0.3F, 0.366F, 0.366F, InteractionHand.MAIN_HAND, ((HumanoidArmature)humanoidArmature.get()).toolL, ColliderPreset.SWORD
                     ),
                     new Phase(0.366F, 0.366F, 0.416F, 0.483F, 0.483F, ((HumanoidArmature)humanoidArmature.get()).toolR, ColliderPreset.SWORD),
                     new Phase(
                        0.483F, 0.483F, 0.533F, 0.6F, 0.6F, InteractionHand.MAIN_HAND, ((HumanoidArmature)humanoidArmature.get()).toolL, ColliderPreset.SWORD
                     ),
                     new Phase(0.6F, 0.6F, 0.65F, 0.716F, 0.716F, ((HumanoidArmature)humanoidArmature.get()).toolR, ColliderPreset.SWORD),
                     new Phase(
                        0.716F,
                        0.716F,
                        0.766F,
                        0.833F,
                        0.833F,
                        InteractionHand.MAIN_HAND,
                        ((HumanoidArmature)humanoidArmature.get()).toolL,
                        ColliderPreset.SWORD
                     ),
                     new Phase(0.833F, 0.833F, 0.883F, 1.1F, 1.1F, ((HumanoidArmature)humanoidArmature.get()).toolR, ColliderPreset.SWORD),
                     new Phase(0.933F, 1.133F, 1.183F, 1.6F, 1.6F, ((HumanoidArmature)humanoidArmature.get()).toolL, ColliderPreset.SWORD)
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 4.0F)
      );
      DUAL_SWORD_AUTO1 = builder.nextAccessor(
         "biped/pugilist_steve/dual_sword_auto1",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.1F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.5F, 0.63F, 0.667F, 0.667F, InteractionHand.MAIN_HAND, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.2F, 0.7F, 0.8F, 0.9F, 1.3F, ((HumanoidArmature)humanoidArmature.get()).toolL, null)
                  }
               )
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(2.5F))
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
      );
      DUAL_SWORD_AUTO2 = builder.nextAccessor(
         "biped/pugilist_steve/dual_sword_auto2",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.15F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.5F, 0.63F, 0.667F, 0.667F, InteractionHand.MAIN_HAND, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.2F, 0.7F, 0.8F, 0.9F, 1.3F, ((HumanoidArmature)humanoidArmature.get()).toolL, null)
                  }
               )
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(2.5F))
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
      );
      TRIDENT_DUAL_AUTO2 = builder.nextAccessor(
         "biped/pugilist_steve/trident_dual_auto2",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.15F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.5F, 0.63F, 0.667F, 0.667F, InteractionHand.MAIN_HAND, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.2F, 0.7F, 0.8F, 0.9F, 1.3F, ((HumanoidArmature)humanoidArmature.get()).toolL, null)
                  }
               )
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(2.5F))
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> 2.0F
               )
      );
      DUAL_SWORD_AUTO3 = builder.nextAccessor(
         "biped/pugilist_steve/dual_sword_auto3",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.16F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.66F, 0.69F, 0.733F, 1.0F, InteractionHand.MAIN_HAND, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.2F, 0.7F, 0.8F, 0.9F, 1.3F, ((HumanoidArmature)humanoidArmature.get()).toolL, null)
                  }
               )
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(2.5F))
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
      );
      DUAL_SWORD_AUTO4 = builder.nextAccessor(
         "biped/pugilist_steve/dual_sword_auto4",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.1F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.633F, 0.69F, 0.8F, 1.167F, 1.65F, InteractionHand.MAIN_HAND, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.2F, 0.7F, 0.8F, 0.9F, 1.3F, ((HumanoidArmature)humanoidArmature.get()).toolL, null)
                  }
               )
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
      );
      DUAL_SWORD_AUTO5 = builder.nextAccessor(
         "biped/pugilist_steve/dual_sword_auto5",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.1F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.633F, 0.69F, 0.8F, 1.167F, 1.65F, InteractionHand.MAIN_HAND, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.2F, 0.7F, 0.8F, 0.9F, 1.3F, ((HumanoidArmature)humanoidArmature.get()).toolL, null)
                  }
               )
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
      );
      DUAL_SWORD1 = builder.nextAccessor(
         "biped/pugilist_steve/dual_auto1",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.1F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.05F, 0.3F, 0.4F, 1.167F, 1.65F, InteractionHand.MAIN_HAND, ((HumanoidArmature)humanoidArmature.get()).toolL, null),
                     new Phase(0.1F, 0.1F, 0.4F, 0.6F, 0.6F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                  }
               )
               .addProperty(AttackPhaseProperty.HIT_PRIORITY, Priority.TARGET)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.5F)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
      );
      DUAL_SWORD2 = builder.nextAccessor(
         "biped/pugilist_steve/dual_auto2",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.1F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.05F, 0.4F, 0.8F, 1.167F, 2.5F, InteractionHand.MAIN_HAND, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.2F, 0.1F, 1.2F, 1.3F, 1.5F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.2F, 0.1F, 1.4F, 1.5F, 2.1F, ((HumanoidArmature)humanoidArmature.get()).toolL, null)
                  }
               )
               .addProperty(AttackPhaseProperty.HIT_PRIORITY, Priority.TARGET)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.5F)
      );
      DUAL_SWORD3 = builder.nextAccessor(
         "biped/pugilist_steve/dual_auto3",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.1F, 0.0F, 0.0F, 0.06F, 0.3F, ColliderPreset.SWORD, ((HumanoidArmature)humanoidArmature.get()).rootJoint, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.HIT_PRIORITY, Priority.TARGET)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.5F)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
      );
      FIST_UP = builder.nextAccessor(
         "biped/pugilist_steve/fist_up",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.15F, 0.25F, 0.45F, 0.85F, 0.95F, WOMWeaponColliders.KICK, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.AIR_BURST)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)AVSounds.KICK.get())
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get())
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.5F))
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.2F)
      );
      RUSH_SWORD = builder.nextAccessor(
         "biped/pugilist_steve/rush_sword",
         accessor -> (RushSwordAnimation)new RushSwordAnimation(
                  0.15F, 0.0F, 0.1F, 0.26F, 0.75F, ColliderPreset.SWORD, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
      );
      DUAL_DANCING_EDGE = builder.nextAccessor(
         "biped/pugilist_steve/dancing_edge",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.25F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.2F, 0.31F, 0.4F, 0.4F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.4F, 0.5F, 0.61F, 0.65F, 0.65F, InteractionHand.OFF_HAND, ((HumanoidArmature)humanoidArmature.get()).toolL, null),
                     new Phase(0.65F, 0.76F, 0.85F, 1.15F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, ColliderPreset.DUAL_SWORD)
                  }
               )
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD, 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 2)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
      );
      SWEEPING_EDGE = builder.nextAccessor(
         "biped/pugilist_steve/sweeping_edge",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.2F,
                  0.1F,
                  0.35F,
                  0.46F,
                  0.79F,
                  ColliderPreset.BIPED_BODY_COLLIDER,
                  ((HumanoidArmature)humanoidArmature.get()).toolR,
                  accessor,
                  humanoidArmature
               )
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.9F))
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.45F)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
      );
      HIT_BACKWARD = builder.nextAccessor("biped/pugilist_steve/hit_backward", accessor -> new LongHitAnimation(0.08F, accessor, humanoidArmature));
      SPEAR_GUARD_HIT = builder.nextAccessor(
         "biped/pugilist_steve/spear_guard_hit",
         accessor -> (GuardAnimation)new GuardAnimation(0.05F, 0.2F, accessor, humanoidArmature)
               .addEvents(
                  new InTimeEvent[]{
                     InTimeEvent.create(0.1F, ReuseableEvents.FAST_SPINNING, Side.CLIENT),
                     InTimeEvent.create(0.2F, ReuseableEvents.FAST_SPINNING, Side.CLIENT),
                     InTimeEvent.create(0.3F, ReuseableEvents.FAST_SPINNING, Side.CLIENT),
                     InTimeEvent.create(0.4F, ReuseableEvents.FAST_SPINNING, Side.CLIENT)
                  }
               )
      );
      LEGENDARY_SWORD_GUARD = builder.nextAccessor(
         "biped/pugilist_steve/legendary_sword_guard", accessor -> new StaticAnimation(true, accessor, humanoidArmature)
      );
      LEGENDARY_SWORD_GUARD_HIT = builder.nextAccessor(
         "biped/pugilist_steve/legendary_sword_guard_hit", accessor -> new GuardAnimation(0.05F, accessor, humanoidArmature)
      );
      LEGENDARY_SWORD_GUARD_PARRY = builder.nextAccessor(
         "biped/pugilist_steve/legendary_sword_guard_parry", accessor -> new GuardAnimation(0.05F, accessor, humanoidArmature)
      );
      POSE_UP = builder.nextAccessor(
         "biped/pugilist_steve/pose_up",
         accessor -> (ActionAnimation)new ActionAnimation(0.0F, 1.85F, accessor, humanoidArmature).addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
      );
      DAGGER_AUTO1 = builder.nextAccessor(
         "biped/pugilist_steve/dagger_auto1",
         accessor -> new BasicAttackAnimation(0.08F, 0.05F, 0.15F, 0.2F, null, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature)
      );
      DAGGER_AUTO2 = builder.nextAccessor(
         "biped/pugilist_steve/dagger_auto2",
         accessor -> new BasicAttackAnimation(0.08F, 0.0F, 0.1F, 0.2F, null, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature)
      );
      DAGGER_AUTO3 = builder.nextAccessor(
         "biped/pugilist_steve/dagger_auto3",
         accessor -> new BasicAttackAnimation(0.08F, 0.15F, 0.26F, 0.5F, null, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature)
      );
      DAGGER_DUAL_AUTO1 = builder.nextAccessor(
         "biped/pugilist_steve/dagger_dual_auto1",
         accessor -> new BasicAttackAnimation(0.08F, 0.05F, 0.16F, 0.25F, null, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature)
      );
      DAGGER_DUAL_AUTO2 = builder.nextAccessor(
         "biped/pugilist_steve/dagger_dual_auto2",
         accessor -> new BasicAttackAnimation(
               0.08F, 0.0F, 0.11F, 0.16F, InteractionHand.OFF_HAND, null, ((HumanoidArmature)humanoidArmature.get()).toolL, accessor, humanoidArmature
            )
      );
      DAGGER_DUAL_AUTO3 = builder.nextAccessor(
         "biped/pugilist_steve/dagger_dual_auto3",
         accessor -> new BasicAttackAnimation(0.08F, 0.0F, 0.11F, 0.2F, null, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature)
      );
      DAGGER_DUAL_AUTO4 = builder.nextAccessor(
         "biped/pugilist_steve/dagger_dual_auto4",
         accessor -> new BasicAttackAnimation(
               0.13F, 0.1F, 0.21F, 0.4F, ColliderPreset.DUAL_DAGGER_DASH, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
            )
      );
      CHECK = builder.nextAccessor("biped/pugilist_steve/check", accessor -> new StaticAnimation(false, accessor, humanoidArmature));
      BIPED_RUN_ESWORD = builder.nextAccessor("biped/pugilist_steve/run_esword", accessor -> new MovementAnimation(true, accessor, humanoidArmature));
      KNIFE_IDLE = builder.nextAccessor("biped/pugilist_steve/knife_idle", accessor -> new StaticAnimation(true, accessor, humanoidArmature));
      KNIFE_RUN = builder.nextAccessor("biped/pugilist_steve/knife_run", accessor -> new MovementAnimation(true, accessor, humanoidArmature));
      KNIFE_ATTACK = builder.nextAccessor(
         "biped/pugilist_steve/knife_attack",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.15F, 0.01F, 0.2F, 0.5F, 0.6F, null, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.EVISCERATE.get())
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.5F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
               .addState(EntityState.MOVEMENT_LOCKED, false)
               .addState(EntityState.TURNING_LOCKED, false)
               .addState(EntityState.LOCKON_ROTATE, false)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
      );
      KNIFE_CHECK = builder.nextAccessor(
         "biped/pugilist_steve/knife_check", accessor -> new StaticAnimation(false, accessor, humanoidArmature).addState(EntityState.CAN_BASIC_ATTACK, false)
      );
      HOOK_GUN = builder.nextAccessor(
         "biped/pugilist_steve/hook_gun",
         accessor -> (ActionAnimation)new ActionAnimation(0.0F, 1.85F, accessor, humanoidArmature).addState(EntityState.CAN_BASIC_ATTACK, false)
      );
      CARRY = builder.nextAccessor("biped/pugilist_steve/carry", accessor -> new StaticAnimation(true, accessor, humanoidArmature));
      FIST_LEFT = builder.nextAccessor(
         "biped/pugilist_steve/fist_left",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.15F, 0.25F, 0.45F, 0.85F, 1.1F, ColliderPreset.FIST, ((HumanoidArmature)humanoidArmature.get()).toolL, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.AIR_BURST)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get())
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.5F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.2F)
      );
      KNOCKDOWN_FORWARD = builder.nextAccessor(
         "biped/pugilist_steve/knockdown_forward",
         accessor -> (KnockdownAnimation)new KnockdownAnimation(0.1F, accessor, humanoidArmature)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, false)
               .addState(EntityState.MOVEMENT_LOCKED, true)
               .addState(EntityState.TURNING_LOCKED, true)
               .addState(EntityState.LOCKON_ROTATE, true)
               .addState(EntityState.CAN_SKILL_EXECUTION, false)
               .addState(EntityState.CAN_BASIC_ATTACK, false)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
      );
      KNOCKDOWN_RIGHT = builder.nextAccessor(
         "biped/pugilist_steve/knockdown_right",
         accessor -> (KnockdownAnimation)new KnockdownAnimation(0.1F, accessor, humanoidArmature)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, false)
               .addState(EntityState.MOVEMENT_LOCKED, true)
               .addState(EntityState.TURNING_LOCKED, true)
               .addState(EntityState.LOCKON_ROTATE, true)
               .addState(EntityState.CAN_SKILL_EXECUTION, false)
               .addState(EntityState.CAN_BASIC_ATTACK, false)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
      );
      KNOCKDOWN_LEFT = builder.nextAccessor(
         "biped/pugilist_steve/knockdown_left",
         accessor -> (KnockdownAnimation)new KnockdownAnimation(0.1F, accessor, humanoidArmature)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, false)
               .addState(EntityState.MOVEMENT_LOCKED, true)
               .addState(EntityState.TURNING_LOCKED, true)
               .addState(EntityState.LOCKON_ROTATE, true)
               .addState(EntityState.CAN_SKILL_EXECUTION, false)
               .addState(EntityState.CAN_BASIC_ATTACK, false)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
      );
      AXE_HEAVY_AUTO_1 = builder.nextAccessor(
         "biped/pugilist_steve/axe_heavy_auto1",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.1F, 0.15F, 0.3F, 0.6F, 0.95F, ColliderPreset.SWORD, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.5F))
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.5F)
      );
      AXE_HEAVY_AUTO_2 = builder.nextAccessor(
         "biped/pugilist_steve/axe_heavy_auto2",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.1F, 0.15F, 0.8F, 1.2F, 1.95F, ColliderPreset.SWORD, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.4F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.5F))
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.8F)
      );
      SWORD_HEAVY_AUTO_1 = builder.nextAccessor(
         "biped/pugilist_steve/sword_heavy_auto1",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.1F, 0.11F, 0.27F, 0.5F, 0.95F, ColliderPreset.SWORD, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.4F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.5F))
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> 2.0F
               )
      );
      SWORD_HEAVY_AUTO_2 = builder.nextAccessor(
         "biped/pugilist_steve/sword_heavy_auto2",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.01F, 0.1F, 0.12F, 0.22F, 0.95F, ColliderPreset.SWORD, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.4F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.5F))
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> 2.0F
               )
      );
      SWORD_HEAVY_AUTO_3 = builder.nextAccessor(
         "biped/pugilist_steve/sword_heavy_auto3",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.01F, 0.1F, 0.21F, 0.32F, 1.2F, ColliderPreset.SWORD, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.6F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.5F))
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> 1.5F
               )
      );
      HARD_KICK = builder.nextAccessor(
         "biped/pugilist_steve/hard_kick",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.1F,
                  0.29F,
                  1.1F,
                  1.2F,
                  3.1F,
                  WOMWeaponColliders.TORMENT_BERSERK_AIRSLAM,
                  ((HumanoidArmature)humanoidArmature.get()).legR,
                  accessor,
                  humanoidArmature
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.AIR_BURST)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get())
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.5F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
               .addState(EntityState.MOVEMENT_LOCKED, true)
               .addState(EntityState.TURNING_LOCKED, true)
               .addState(EntityState.LOCKON_ROTATE, true)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
      );
      HARD_KICK_HIT = builder.nextAccessor(
         "biped/pugilist_steve/hard_kick_hit",
         accessor -> (LongHitAnimation)new LongHitAnimation(0.1F, accessor, humanoidArmature)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, false)
               .addState(EntityState.MOVEMENT_LOCKED, true)
               .addState(EntityState.TURNING_LOCKED, true)
               .addState(EntityState.LOCKON_ROTATE, true)
               .addState(EntityState.CAN_SKILL_EXECUTION, false)
               .addState(EntityState.CAN_BASIC_ATTACK, false)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
      );
      RUN_START = builder.nextAccessor(
         "biped/pugilist_steve/run_start",
         accessor -> (ActionAnimation)new ActionAnimation(0.05F, Float.MAX_VALUE, accessor, humanoidArmature)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
      );
      LONGSWORD_AUTO1 = builder.nextAccessor(
         "biped/pugilist_steve/tachi_auto1",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.1F, 0.15F, 0.2F, 0.3F, 0.75F, null, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
      );
      RUN_HOLD = builder.nextAccessor("biped/pugilist_steve/run_hold", accessor -> new MovementAnimation(true, accessor, humanoidArmature));
      RUN_DUAL_BIG = builder.nextAccessor("biped/pugilist_steve/run_dual_big", accessor -> new MovementAnimation(true, accessor, humanoidArmature));
      LONGEST_HIT = builder.nextAccessor(
         "biped/pugilist_steve/longest_hit",
         accessor -> (KnockdownAnimation)new KnockdownAnimation(0.1F, accessor, humanoidArmature)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, false)
               .addState(EntityState.MOVEMENT_LOCKED, true)
               .addState(EntityState.TURNING_LOCKED, true)
               .addState(EntityState.LOCKON_ROTATE, true)
               .addState(EntityState.CAN_SKILL_EXECUTION, false)
               .addState(EntityState.CAN_BASIC_ATTACK, false)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
      );
      HARD_GREATSWORD_GUARD = builder.nextAccessor("biped/pugilist_steve/hard_greatsword", accessor -> new StaticAnimation(true, accessor, humanoidArmature));
      HARD_GREATSWORD_GUARD_HIT = builder.nextAccessor(
         "biped/pugilist_steve/hard_greatsword_hit", accessor -> new GuardAnimation(0.05F, accessor, humanoidArmature)
      );
      HARD_GREATSWORD_GUARD_SKILL = builder.nextAccessor(
         "biped/pugilist_steve/hard_greatsword_skill",
         accessor -> (ActionAnimation)new ActionAnimation(0.05F, Float.MAX_VALUE, accessor, humanoidArmature)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
      );
      HIT_LEFT = builder.nextAccessor("biped/pugilist_steve/hit_left", accessor -> new LongHitAnimation(0.1F, accessor, humanoidArmature));
      HIT_RIGHT = builder.nextAccessor("biped/pugilist_steve/hit_right", accessor -> new LongHitAnimation(0.1F, accessor, humanoidArmature));
      SHAKE_HAND_TRY = builder.nextAccessor(
         "biped/pugilist_steve/shake_hand_try",
         accessor -> (ActionAnimation)new ActionAnimation(0.05F, Float.MAX_VALUE, accessor, humanoidArmature)
               .addState(EntityState.CAN_SKILL_EXECUTION, false)
               .addState(EntityState.CAN_BASIC_ATTACK, false)
               .addState(EntityState.TURNING_LOCKED, true)
               .addState(EntityState.LOCKON_ROTATE, true)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
      );
      SHAKE_HAND = builder.nextAccessor(
         "biped/pugilist_steve/shake_hand",
         accessor -> (ActionAnimation)new ActionAnimation(0.05F, Float.MAX_VALUE, accessor, humanoidArmature)
               .addState(EntityState.CAN_SKILL_EXECUTION, false)
               .addState(EntityState.CAN_BASIC_ATTACK, false)
               .addState(EntityState.TURNING_LOCKED, true)
               .addState(EntityState.LOCKON_ROTATE, true)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
      );
      FIST_TRY = builder.nextAccessor(
         "biped/pugilist_steve/fist_try",
         accessor -> (ActionAnimation)new ActionAnimation(0.05F, Float.MAX_VALUE, accessor, humanoidArmature)
               .addState(EntityState.CAN_SKILL_EXECUTION, false)
               .addState(EntityState.CAN_BASIC_ATTACK, false)
               .addState(EntityState.TURNING_LOCKED, true)
               .addState(EntityState.LOCKON_ROTATE, true)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
      );
      FISTING = builder.nextAccessor(
         "biped/pugilist_steve/fisting",
         accessor -> (ActionAnimation)new ActionAnimation(0.05F, Float.MAX_VALUE, accessor, humanoidArmature)
               .addState(EntityState.CAN_SKILL_EXECUTION, false)
               .addState(EntityState.CAN_BASIC_ATTACK, false)
               .addState(EntityState.TURNING_LOCKED, true)
               .addState(EntityState.LOCKON_ROTATE, true)
               .addEvents(
                  new AnimationEvent[]{InTimeEvent.create(0.15F, ReusableSources.PLAY_SOUND, Side.SERVER).params((SoundEvent)EpicFightSounds.WHOOSH.get())}
               )
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
      );
      GIANT_WHIRLWIND = builder.nextAccessor(
         "biped/pugilist_steve/giant_whirlwind",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.41F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.3F, 0.35F, 0.55F, 0.9F, 0.9F, ((HumanoidArmature)humanoidArmature.get()).toolL, null),
                     new Phase(0.9F, 0.95F, 1.05F, 1.2F, 1.5F, 1.5F, ((HumanoidArmature)humanoidArmature.get()).toolL, null),
                     new Phase(1.5F, 1.65F, 1.75F, 1.95F, 2.5F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolL, ColliderPreset.GREATSWORD)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.6F))
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
                  }
               )
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
      );
      DUAL_SWORD_DANCING_EDGE = builder.nextAccessor(
         "biped/pugilist_steve/dual_sword_dancing_edge",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.25F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.2F, 0.31F, 0.4F, 0.4F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.4F, 0.5F, 0.61F, 0.65F, 0.65F, InteractionHand.OFF_HAND, ((HumanoidArmature)humanoidArmature.get()).toolL, null),
                     new Phase(0.65F, 0.76F, 0.85F, 1.15F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, ColliderPreset.DUAL_SWORD)
                  }
               )
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD, 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG, 2)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
      );
      SPEAR_THRUST = builder.nextAccessor(
         "biped/pugilist_steve/spear_thrust",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.11F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.3F, 0.36F, 0.5F, 0.5F, ((HumanoidArmature)humanoidArmature.get()).toolR, ColliderPreset.SPEAR),
                     new Phase(0.5F, 0.5F, 0.56F, 0.75F, 0.75F, ((HumanoidArmature)humanoidArmature.get()).toolR, ColliderPreset.SPEAR),
                     new Phase(0.75F, 0.75F, 0.81F, 1.05F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, ColliderPreset.SPEAR)
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
      );
      DUAL_TACHI_GUARD = builder.nextAccessor("biped/pugilist_steve/dual_tachi_guard", accessor -> new StaticAnimation(true, accessor, humanoidArmature));
      DUAL_TACHI_GUARD_HIT = builder.nextAccessor(
         "biped/pugilist_steve/dual_tachi_guard_hit", accessor -> new GuardAnimation(0.05F, accessor, humanoidArmature)
      );
      WHIRLWIND_KICK_LEFT = builder.nextAccessor(
         "biped/pugilist_steve/whirlwind_kick_left",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.1F,
                  0.3F,
                  0.7F,
                  0.9F,
                  Float.MAX_VALUE,
                  ColliderPreset.BIPED_BODY_COLLIDER,
                  ((HumanoidArmature)humanoidArmature.get()).legL,
                  accessor,
                  humanoidArmature
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.AIR_BURST)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get())
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(10.8F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
               .addEvents(
                  new AnimationEvent[]{InTimeEvent.create(0.23F, ReusableSources.PLAY_SOUND, Side.SERVER).params((SoundEvent)EpicFightSounds.WHOOSH.get())}
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.5F)
      );
      SUPER_PUNCH = builder.nextAccessor(
         "biped/pugilist_steve/super_punch",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.05F, 1.0F, 1.25F, 1.4F, Float.MAX_VALUE, ColliderPreset.SWORD, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.AIR_BURST)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get())
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.5F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
      );
      GUARD_BREAK_ATTACK = builder.nextAccessor(
         "biped/pugilist_steve/guard_break_attack", accessor -> new KnockdownAnimation(0.05F, accessor, humanoidArmature)
      );
      SWORD_DASH = builder.nextAccessor(
         "biped/pugilist_steve/sword_dash",
         accessor -> (DashAttackAnimation)new DashAttackAnimation(
                  0.12F, 0.1F, 0.25F, 0.4F, 0.65F, null, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
      );
      TACHI_DASH = builder.nextAccessor(
         "biped/pugilist_steve/tachi_dash",
         accessor -> (DashAttackAnimation)new DashAttackAnimation(
                  0.15F, 0.1F, 0.2F, 0.45F, 0.7F, null, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
      );
      DUAL_SWORD_SKILL = builder.nextAccessor(
         "biped/pugilist_steve/dual_sword_skill",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.05F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.15F, 0.25F, 0.25F, 0.25F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(8.0F)),
                     new Phase(0.25F, 0.25F, 0.4F, 0.5F, 0.5F, ((HumanoidArmature)humanoidArmature.get()).toolL, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(8.0F)),
                     new Phase(0.5F, 0.5F, 0.6F, 0.6F, 0.6F, ((HumanoidArmature)humanoidArmature.get()).toolL, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(8.0F)),
                     new Phase(0.6F, 0.6F, 0.75F, 0.75F, 0.75F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(8.0F)),
                     new Phase(0.75F, 0.75F, 0.8F, 0.9F, 0.9F, ((HumanoidArmature)humanoidArmature.get()).toolL, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(8.0F)),
                     new Phase(0.9F, 0.9F, 1.0F, 1.0F, 1.0F, ((HumanoidArmature)humanoidArmature.get()).toolL, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(8.0F)),
                     new Phase(1.0F, 1.0F, 1.1F, 1.1F, 1.1F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(8.0F)),
                     new Phase(1.1F, 1.1F, 1.22F, 1.22F, 1.22F, ((HumanoidArmature)humanoidArmature.get()).toolL, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(8.0F)),
                     new Phase(1.22F, 1.22F, 1.35F, 1.35F, 1.35F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(8.0F)),
                     new Phase(1.35F, 1.35F, 1.42F, 1.42F, 1.42F, ((HumanoidArmature)humanoidArmature.get()).toolL, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(8.0F)),
                     new Phase(1.42F, 1.42F, 1.5F, 1.5F, 1.5F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(8.0F)),
                     new Phase(1.5F, 1.5F, 1.6F, 1.6F, 1.6F, ((HumanoidArmature)humanoidArmature.get()).toolL, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(8.0F)),
                     new Phase(1.6F, 1.6F, 1.7F, 1.7F, 1.7F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(8.0F)),
                     new Phase(1.7F, 1.7F, 1.8F, 1.8F, 1.8F, ((HumanoidArmature)humanoidArmature.get()).toolL, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(8.0F)),
                     new Phase(1.8F, 1.8F, 1.9F, 1.9F, 1.9F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(8.0F)),
                     new Phase(1.9F, 2.0F, 2.2F, Float.MAX_VALUE, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolL, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.3F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(8.0F))
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                  }
               )
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
               .addState(EntityState.CAN_SKILL_EXECUTION, false)
               .addState(EntityState.CAN_BASIC_ATTACK, false)
               .addState(EntityState.MOVEMENT_LOCKED, true)
               .addState(EntityState.TURNING_LOCKED, false)
               .addState(EntityState.LOCKON_ROTATE, false)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(2.5F, ReuseableEvents.END_ATTACK, Side.BOTH)})
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
      );
      DUAL_END = builder.nextAccessor(
         "biped/pugilist_steve/dual_back_end",
         accessor -> (ActionAnimation)new ActionAnimation(0.2F, Float.MAX_VALUE, accessor, humanoidArmature)
               .addState(EntityState.CAN_SKILL_EXECUTION, false)
               .addState(EntityState.CAN_BASIC_ATTACK, true)
               .addState(EntityState.TURNING_LOCKED, true)
               .addState(EntityState.LOCKON_ROTATE, true)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
      );
      TRIED = builder.nextAccessor(
         "biped/pugilist_steve/tried",
         accessor -> (KnockdownAnimation)new KnockdownAnimation(0.2F, accessor, humanoidArmature)
               .addState(EntityState.CAN_SKILL_EXECUTION, false)
               .addState(EntityState.CAN_BASIC_ATTACK, false)
               .addState(EntityState.TURNING_LOCKED, true)
               .addState(EntityState.LOCKON_ROTATE, true)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
      );
      GREATSWORD_SKILL = builder.nextAccessor(
         "biped/pugilist_steve/greatsword_skill",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.05F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.1F, 0.25F, 0.25F, 0.25F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                     new Phase(0.25F, 0.25F, 0.4F, 0.5F, 0.5F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                     new Phase(0.5F, 0.5F, 0.6F, 0.6F, 0.6F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                     new Phase(0.6F, 0.6F, 0.75F, 0.75F, 0.75F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                     new Phase(0.75F, 0.75F, 0.8F, 0.9F, 0.9F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                     new Phase(0.9F, 0.9F, 1.0F, 1.0F, 1.0F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                     new Phase(1.0F, 1.0F, 1.1F, 1.1F, 1.1F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                     new Phase(1.1F, 1.1F, 1.22F, 1.22F, 1.22F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                     new Phase(1.22F, 1.22F, 1.35F, 1.35F, 1.35F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                     new Phase(1.35F, 1.35F, 1.42F, 1.42F, 1.42F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                     new Phase(1.42F, 1.42F, 1.5F, 1.5F, 1.5F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                     new Phase(1.5F, 1.5F, 1.6F, 1.6F, 1.6F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                     new Phase(1.6F, 1.6F, 1.7F, 1.7F, 1.7F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                     new Phase(1.7F, 1.7F, 1.8F, 1.85F, 1.85F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                     new Phase(1.85F, 1.85F, 2.2F, Float.MAX_VALUE, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.3F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                  }
               )
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
               .addState(EntityState.CAN_SKILL_EXECUTION, false)
               .addState(EntityState.CAN_BASIC_ATTACK, false)
               .addState(EntityState.MOVEMENT_LOCKED, true)
               .addState(EntityState.TURNING_LOCKED, false)
               .addState(EntityState.LOCKON_ROTATE, false)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.8F)
      );
      LEGENDARY_SWORD_WAKE_UP_ATTACK = builder.nextAccessor(
         "biped/pugilist_steve/legendary_sword_wake_up_attack",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.05F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.15F, 0.4F, 0.45F, 0.45F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.45F, 0.5F, 0.8F, Float.MAX_VALUE, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F), 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN, 1)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.1F)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, false)
      );
      DUAL_E_END = builder.nextAccessor(
         "biped/pugilist_steve/dual_e_end",
         accessor -> (ActionAnimation)new ActionAnimation(0.2F, Float.MAX_VALUE, accessor, humanoidArmature)
               .addState(EntityState.CAN_SKILL_EXECUTION, false)
               .addState(EntityState.CAN_BASIC_ATTACK, true)
               .addState(EntityState.TURNING_LOCKED, true)
               .addState(EntityState.LOCKON_ROTATE, true)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
      );
      AXE_FUN_SKILL = builder.nextAccessor(
         "biped/pugilist_steve/axe_fun_skill",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.05F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.1F, 0.25F, 0.25F, 0.25F, ((HumanoidArmature)humanoidArmature.get()).toolR, ColliderPreset.SWORD)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                     new Phase(0.25F, 0.25F, 0.4F, 0.5F, 0.5F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                     new Phase(0.5F, 0.5F, 0.6F, 0.6F, 0.6F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                     new Phase(0.6F, 0.6F, 0.75F, 0.75F, 0.75F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                     new Phase(0.75F, 0.75F, 0.8F, 0.9F, 0.9F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                     new Phase(0.9F, 0.9F, 1.0F, 1.0F, 1.0F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                     new Phase(1.0F, 1.0F, 1.1F, 1.1F, 1.1F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                     new Phase(1.1F, 1.1F, 1.22F, 1.22F, 1.22F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                     new Phase(1.22F, 1.22F, 1.35F, 1.35F, 1.35F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                     new Phase(1.35F, 1.35F, 1.42F, 1.42F, 1.42F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                     new Phase(1.42F, 1.42F, 1.5F, 1.5F, 1.5F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                     new Phase(1.5F, 1.5F, 1.55F, 1.55F, 1.55F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F)),
                     new Phase(1.55F, 1.6F, 1.7F, Float.MAX_VALUE, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.3F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                  }
               )
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
               .addState(EntityState.CAN_SKILL_EXECUTION, false)
               .addState(EntityState.CAN_BASIC_ATTACK, false)
               .addState(EntityState.MOVEMENT_LOCKED, true)
               .addState(EntityState.TURNING_LOCKED, false)
               .addState(EntityState.LOCKON_ROTATE, false)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
      );
      LEGENDARY_SWORD_AUTO_4 = builder.nextAccessor(
         "biped/pugilist_steve/legendary_sword_auto_4",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.15F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.2F, 0.4F, 0.45F, 0.45F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.45F, 0.55F, 0.7F, 0.7F, Float.MAX_VALUE, InteractionHand.OFF_HAND, ((HumanoidArmature)humanoidArmature.get()).toolL, null)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.6F))
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F), 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.5F), 1)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.05F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.45F, reascer.wom.gameasset.ReuseableEvents.TORMENT_GROUNDSLAM, Side.CLIENT),
                     InTimeEvent.create(1.2F, (livingEntityPatch, self, p) -> {
                        if (!livingEntityPatch.isLogicalClient()) {
                           livingEntityPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
                        }
                     }, Side.SERVER)
                  }
               )
      );
      OBSIDIAN_FIST_DASH = builder.nextAccessor(
         "biped/pugilist_steve/obsidian_fist_dash",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.15F,
                  0.25F,
                  0.45F,
                  0.7F,
                  0.95F,
                  ColliderPreset.BIPED_BODY_COLLIDER,
                  ((HumanoidArmature)humanoidArmature.get()).toolR,
                  accessor,
                  humanoidArmature
               )
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.AIR_BURST)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get())
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.5F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.6F, ReuseableEvents.THROW_OBSIDIAN, Side.SERVER)})
      );
      SHADOW_OBSIDIAN_SWORD_ONEHAND_LONG = builder.nextAccessor(
         "biped/pugilist_steve/shadow_obsidian_sword_onehand_long",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.1F,
                  0.15F,
                  0.2F,
                  0.3F,
                  0.75F,
                  AVCollider.SHADOW_OBSIDIAN_PILLAR,
                  ((HumanoidArmature)humanoidArmature.get()).toolR,
                  accessor,
                  humanoidArmature
               )
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
      );
      SHADOW_OBSIDIAN_SWORD_DUAL_SWORD_AUTO4 = builder.nextAccessor(
         "biped/pugilist_steve/shadow_obsidian_sword_dual_sword_auto4",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.1F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(
                        0.0F,
                        0.633F,
                        0.69F,
                        0.8F,
                        1.167F,
                        1.65F,
                        InteractionHand.MAIN_HAND,
                        ((HumanoidArmature)humanoidArmature.get()).toolR,
                        AVCollider.SHADOW_OBSIDIAN_PILLAR
                     ),
                     new Phase(0.2F, 0.7F, 0.8F, 0.9F, 1.3F, ((HumanoidArmature)humanoidArmature.get()).toolL, AVCollider.SHADOW_OBSIDIAN_PILLAR)
                  }
               )
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
      );
      SHADOW_OBSIDIAN_SWORD_DUAL_SWORD_AUTO5 = builder.nextAccessor(
         "biped/pugilist_steve/shadow_obsidian_sword_dual_sword_auto5",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.1F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(
                        0.0F,
                        0.633F,
                        0.69F,
                        0.8F,
                        1.167F,
                        1.65F,
                        InteractionHand.MAIN_HAND,
                        ((HumanoidArmature)humanoidArmature.get()).toolR,
                        AVCollider.SHADOW_OBSIDIAN_PILLAR
                     ),
                     new Phase(0.2F, 0.7F, 0.8F, 0.9F, 1.3F, ((HumanoidArmature)humanoidArmature.get()).toolL, AVCollider.SHADOW_OBSIDIAN_PILLAR)
                  }
               )
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
      );
      TRIDENT_THROW_2 = builder.nextAccessor(
         "biped/pugilist_steve/trident_throw_2",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.15F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.5F, 0.63F, 0.667F, 0.667F, InteractionHand.MAIN_HAND, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.2F, 0.7F, 0.8F, 0.9F, 1.3F, ((HumanoidArmature)humanoidArmature.get()).toolL, null)
                  }
               )
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(2.5F))
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> 2.0F
               )
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.1F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(0.6F, ReuseableEvents.THROW_TRIDENT_HAND_LEFT, Side.SERVER)
                  }
               )
      );
      TRIDENT_THROW_LEGENDARY = builder.nextAccessor(
         "biped/pugilist_steve/trident_throw_legendary",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.15F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.5F, 0.63F, 0.667F, 0.667F, InteractionHand.MAIN_HAND, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.2F, 0.7F, 0.8F, 0.9F, 1.3F, ((HumanoidArmature)humanoidArmature.get()).toolL, null)
                  }
               )
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(2.5F))
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> 2.0F
               )
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.1F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(0.4F, ReuseableEvents.THROW_TRIDENT_HAND_LEFT, Side.SERVER)
                  }
               )
      );
      STRANGLE_EXECUTE = builder.nextAccessor(
         "biped/pugilist_steve/strangle_execute",
         accessor -> (ExecutionAttackAnimation)new ExecutionAttackAnimation(
                  0.01F,
                  accessor,
                  Armatures.BIPED,
                  new ExecutionPhase[]{
                     new ExecutionPhase(false, 0.1F, 0.29F, 1.0F, 1.2F, 1.2F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, executionCollider)
                        .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get()),
                     new ExecutionPhase(true, 1.2F, 0.0F, 3.36F, 1.9F, 1.9F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, executionCollider)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.5F))
                        .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(TARGET_MAX_HEALTH.create(new float[]{15.0F, 0.08F})))
                        .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.OLD_FALL.get())
                  }
               )
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> 1.0F
               )
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.6F,
                        (livingEntityPatch, assetAccessor, animationParameters) -> ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_7292_(new MobEffectInstance(MobEffects.f_19597_, 30, 9, false, false)),
                        Side.BOTH
                     )
                  }
               )
      );
      STRANGLE_EXECUTE_HIT = builder.nextAccessor(
         "biped/pugilist_steve/strangle_execute_hit",
         accessor -> (ExecutionHitAnimation)new ExecutionHitAnimation(0.01F, accessor, Armatures.BIPED)
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER,
                  (PlaybackSpeedModifier)(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> 0.8333333F
               )
      );
      WRESTLING_EXECUTE = builder.nextAccessor(
         "biped/pugilist_steve/wrestling_execute",
         accessor -> (ExecutionAttackAnimation)new ExecutionAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  new ExecutionPhase[]{
                     new ExecutionPhase(false, 0.0F, 0.05F, 1.85F, 2.0F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, executionCollider)
                        .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.OLD_FALL.get()),
                     new ExecutionPhase(true, 2.0F, 0.0F, 3.36F, 2.5F, 2.5F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, executionCollider)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.5F))
                        .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(TARGET_MAX_HEALTH.create(new float[]{15.0F, 0.08F})))
                        .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get())
                  }
               )
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> 1.0F
               )
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.6F,
                        (livingEntityPatch, assetAccessor, animationParameters) -> ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_7292_(new MobEffectInstance(MobEffects.f_19597_, 30, 9, false, false)),
                        Side.BOTH
                     )
                  }
               )
      );
      WRESTLING_EXECUTE_HIT = builder.nextAccessor(
         "biped/pugilist_steve/wrestling_execute_hit",
         accessor -> (ExecutionHitAnimation)new ExecutionHitAnimation(0.01F, accessor, Armatures.BIPED)
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER,
                  (PlaybackSpeedModifier)(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> 0.8333333F
               )
      );
      WRESTLING_BACK_EXECUTE = builder.nextAccessor(
         "biped/pugilist_steve/wrestling_back_execute",
         accessor -> (ExecutionAttackAnimation)new ExecutionAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  new ExecutionPhase[]{
                     new ExecutionPhase(false, 0.0F, 0.05F, 1.85F, 2.0F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, executionCollider)
                        .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.OLD_FALL.get()),
                     new ExecutionPhase(true, 2.0F, 0.0F, 3.36F, 2.5F, 2.5F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, executionColliderBack)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.5F))
                        .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(TARGET_MAX_HEALTH.create(new float[]{15.0F, 0.08F})))
                        .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get())
                  }
               )
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> 1.0F
               )
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.6F,
                        (livingEntityPatch, assetAccessor, animationParameters) -> ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_7292_(new MobEffectInstance(MobEffects.f_19597_, 30, 9, false, false)),
                        Side.BOTH
                     )
                  }
               )
      );
      WRESTLING_BACK_EXECUTE_HIT = builder.nextAccessor(
         "biped/pugilist_steve/wrestling_back_execute_hit",
         accessor -> (ExecutionHitAnimation)new ExecutionHitAnimation(0.01F, accessor, Armatures.BIPED)
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER,
                  (PlaybackSpeedModifier)(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> 0.8333333F
               )
      );
      STAB_EXECUTE = builder.nextAccessor(
         "biped/pugilist_steve/stab_execute",
         accessor -> (ExecutionAttackAnimation)new ExecutionAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  new ExecutionPhase[]{
                     new ExecutionPhase(false, 0.05F, 0.05F, 1.85F, 2.0F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, executionCollider)
                        .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.OLD_FALL.get()),
                     new ExecutionPhase(false, 0.4F, 0.4F, 0.6F, 0.6F, 1.1F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, executionCollider)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.01F))
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(4.0F)),
                     new ExecutionPhase(
                           true, 1.0F, 1.2F, 1.4F, Float.MAX_VALUE, Float.MAX_VALUE, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, executionCollider
                        )
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.5F))
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(4.0F))
                        .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(TARGET_MAX_HEALTH.create(new float[]{15.0F, 0.08F})))
                        .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.EVISCERATE.get())
                        .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
                  }
               )
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.6F,
                        (livingEntityPatch, assetAccessor, animationParameters) -> ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_7292_(new MobEffectInstance(MobEffects.f_19597_, 30, 9, false, false)),
                        Side.BOTH
                     )
                  }
               )
      );
      DUAL_STAB_EXECUTE = builder.nextAccessor(
         "biped/pugilist_steve/dual_stab_execute",
         accessor -> (ExecutionAttackAnimation)new ExecutionAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  new ExecutionPhase[]{
                     new ExecutionPhase(false, 0.05F, 0.05F, 1.85F, 2.0F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, executionCollider)
                        .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.OLD_FALL.get()),
                     new ExecutionPhase(false, 0.4F, 0.4F, 0.6F, 0.6F, 1.1F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, executionCollider)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.01F))
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(4.0F)),
                     new ExecutionPhase(
                           true, 1.0F, 1.2F, 1.4F, Float.MAX_VALUE, Float.MAX_VALUE, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, executionCollider
                        )
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.5F))
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(4.0F))
                        .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(TARGET_MAX_HEALTH.create(new float[]{15.0F, 0.08F})))
                        .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.EVISCERATE.get())
                        .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
                  }
               )
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.6F,
                        (livingEntityPatch, assetAccessor, animationParameters) -> ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_7292_(new MobEffectInstance(MobEffects.f_19597_, 30, 9, false, false)),
                        Side.BOTH
                     )
                  }
               )
      );
      STAB_EXECUTE_HIT = builder.nextAccessor(
         "biped/pugilist_steve/stab_execute_hit",
         accessor -> (ExecutionHitAnimation)new ExecutionHitAnimation(0.1F, accessor, Armatures.BIPED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
      );
      SHIELD_EXECUTE = builder.nextAccessor(
         "biped/pugilist_steve/shield_execute",
         accessor -> (ExecutionAttackAnimation)new ExecutionAttackAnimation(
                  0.05F,
                  accessor,
                  Armatures.BIPED,
                  new ExecutionPhase[]{
                     new ExecutionPhase(false, 0.1F, 0.65F, 0.8F, 1.2F, 1.2F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, executionCollider)
                        .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
                        .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get())
                        .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.01F))
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(8.0F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F)),
                     new ExecutionPhase(false, 1.2F, 1.45F, 1.6F, 1.6F, 1.6F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, executionCollider)
                        .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
                        .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get())
                        .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.01F))
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(8.0F))
                        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F)),
                     new ExecutionPhase(true, 1.6F, 2.05F, 2.3F, 2.3F, 2.3F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, executionCollider)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.5F))
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(4.0F))
                        .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(TARGET_MAX_HEALTH.create(new float[]{15.0F, 0.08F})))
                        .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.EVISCERATE.get())
                        .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
                  }
               )
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.6F,
                        (livingEntityPatch, assetAccessor, animationParameters) -> ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_7292_(new MobEffectInstance(MobEffects.f_19597_, 30, 9, false, false)),
                        Side.BOTH
                     )
                  }
               )
      );
      SHIELD_EXECUTE_HIT = builder.nextAccessor(
         "biped/pugilist_steve/shield_execute_hit",
         accessor -> (ExecutionHitAnimation)new ExecutionHitAnimation(0.1F, accessor, Armatures.BIPED)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
      );
   }
}
