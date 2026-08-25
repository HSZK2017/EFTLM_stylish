package com.pla.annoyingvillagers.gameasset;

import com.pla.annoyingvillagers.entity.BlueDemonThunderBeamEntity;
import com.pla.annoyingvillagers.entity.TridentLightningBolt;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import reascer.wom.animation.attacks.BasicMultipleAttackAnimation;
import reascer.wom.gameasset.colliders.WOMWeaponColliders;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationEvent.SimpleEvent;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.PlaybackSpeedModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.AirSlashAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.BasicAttackAnimation;
import yesman.epicfight.api.animation.types.DashAttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.MovementAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.gameasset.Animations.ReusableSources;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.damagesource.StunType;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.MOD
)
public class AnimsEpicFightBattleArts {
   public static AnimationAccessor<BasicAttackAnimation> ADVANCED_LANCER_AUTO1;
   public static AnimationAccessor<AttackAnimation> ADVANCED_LANCER_AUTO3;
   public static AnimationAccessor<AttackAnimation> ADVANCED_DUELIST_WHIRLEDGE;
   public static AnimationAccessor<AttackAnimation> ADVANCED_DUELIST_SHOOTING_STAR;
   public static AnimationAccessor<BasicAttackAnimation> TRIDENT_THROW_1;
   public static AnimationAccessor<AttackAnimation> TRIDENT_THROW_5;
   public static AnimationAccessor<StaticAnimation> SQUIRE_SWORD_IDLE;
   public static AnimationAccessor<MovementAnimation> SQUIRE_SWORD_WALK;
   public static AnimationAccessor<MovementAnimation> SQUIRE_SWORD_RUN;
   public static AnimationAccessor<AttackAnimation> SQUIRE_SWORD_AUTO_1;
   public static AnimationAccessor<AttackAnimation> SQUIRE_SWORD_AUTO_2;
   public static AnimationAccessor<AttackAnimation> SQUIRE_SWORD_AUTO_3;
   public static AnimationAccessor<AttackAnimation> SQUIRE_SWORD_DASH_ATTACK;
   public static AnimationAccessor<AirSlashAnimation> SQUIRE_SWORD_HOP_ATTACK;
   public static AnimationAccessor<AttackAnimation> SQUIRE_SWORD_HEAVY_BLOW;
   public static AnimationAccessor<AttackAnimation> SABRE_AUTO3;
   public static AnimationAccessor<DashAttackAnimation> SABRE_DASH_ATTACK;
   public static AnimationAccessor<AirSlashAnimation> SABRE_AIR_ATTACK;
   public static AnimationAccessor<AttackAnimation> SABRE_QUAD_STING;
   public static AnimationAccessor<AttackAnimation> TACHI_BLOSSOM_SLASH;
   public static AnimationAccessor<BasicAttackAnimation> AXE_AUTO1;
   public static AnimationAccessor<BasicAttackAnimation> AXE_AUTO2;
   public static AnimationAccessor<BasicAttackAnimation> AXE_AUTO3;
   public static AnimationAccessor<DashAttackAnimation> AXE_DASH;
   public static AnimationAccessor<AirSlashAnimation> AXE_AIRSLASH;
   public static AnimationAccessor<AttackAnimation> AXE_INNATE;
   public static AnimationAccessor<StaticAnimation> BAXE_IDLE;
   public static AnimationAccessor<MovementAnimation> BAXE_WALK;
   public static AnimationAccessor<MovementAnimation> BAXE_RUN;
   public static AnimationAccessor<BasicAttackAnimation> BAXE_AUTO_1;
   public static AnimationAccessor<BasicAttackAnimation> BAXE_AUTO_2;
   public static AnimationAccessor<AirSlashAnimation> BAXE_DASH_ATTACK;
   public static AnimationAccessor<DashAttackAnimation> BAXE_AIR_ATTACK;
   public static AnimationAccessor<AirSlashAnimation> BAXE_SEISMIC_IMPACT;
   public static AnimationAccessor<DashAttackAnimation> GREATSWORD_DASH_ATTACK;
   public static AnimationAccessor<BasicAttackAnimation> GREATSWORD_AIRSLAM;
   public static AnimationAccessor<AttackAnimation> GREATSWORD_POWER_GEYSER;
   public static AnimationAccessor<BasicAttackAnimation> THIEF_AUTO1;
   public static AnimationAccessor<BasicAttackAnimation> THIEF_AUTO3;
   public static AnimationAccessor<DashAttackAnimation> THIEF_DASH_ATTACK;
   public static AnimationAccessor<AirSlashAnimation> THIEF_AIRSLASH;
   public static AnimationAccessor<AttackAnimation> THIEF_STEAL;
   public static AnimationAccessor<BasicAttackAnimation> DUAL_BLADES_AUTO3;
   public static AnimationAccessor<BasicAttackAnimation> DUAL_BLADES_AIRSLAM;
   public static AnimationAccessor<AttackAnimation> DUAL_BLADES_WHIRLEDGE;
   public static AnimationAccessor<DashAttackAnimation> SWORD_DASH_ATTACK;
   public static AnimationAccessor<BasicAttackAnimation> IRON_LOTUS_AUTO1;
   public static AnimationAccessor<BasicAttackAnimation> IRON_LOTUS_AUTO2;
   public static AnimationAccessor<BasicAttackAnimation> IRON_LOTUS_AUTO3;
   public static AnimationAccessor<BasicAttackAnimation> IRON_LOTUS_DASH_ATTACK;
   public static AnimationAccessor<BasicMultipleAttackAnimation> TRIDENT_THROW_3;

   public static void build(AnimationBuilder builder) {
      ArmatureAccessor<HumanoidArmature> humanoidArmature = Armatures.BIPED;
      ADVANCED_LANCER_AUTO1 = builder.nextAccessor(
         "biped/battle_style/advanced_lancer_auto1",
         access -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.2F, 0.0F, 0.2F, 0.3F, 0.5F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, access, Armatures.BIPED
               )
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> 2.0F
               )
      );
      ADVANCED_LANCER_AUTO3 = builder.nextAccessor(
         "biped/battle_style/advanced_lancer_auto3",
         access -> (AttackAnimation)new AttackAnimation(
                  0.2F, 0.0F, 0.75F, 0.9F, 2.0F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, access, Armatures.BIPED
               )
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> 2.0F
               )
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.2F, ReusableSources.PLAY_SOUND, Side.CLIENT).params(SoundEvents.f_12516_),
                     InTimeEvent.create(0.2F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(0.35F, ReusableSources.PLAY_SOUND, Side.CLIENT).params(SoundEvents.f_12516_),
                     InTimeEvent.create(0.35F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(
                        1.0F,
                        (livingEntityPatch, self, p) -> {
                           if (((LivingEntity)livingEntityPatch.getOriginal()).m_9236_() instanceof ServerLevel serverLevel) {
                              Vec3 tridentTip = EpicfightUtil.getJointWithTranslation(
                                 livingEntityPatch.getOriginal(), new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolR, 1.2F, 0.0
                              );
                              if (tridentTip != null) {
                                 MutableBlockPos checkPos = BlockPos.m_274446_(tridentTip).m_122032_();

                                 while (checkPos.m_123342_() > serverLevel.m_141937_() && !serverLevel.m_8055_(checkPos).m_60804_(serverLevel, checkPos)) {
                                    checkPos.m_122184_(0, -1, 0);
                                 }

                                 if (serverLevel.m_8055_(checkPos).m_60804_(serverLevel, checkPos)) {
                                    TridentLightningBolt tridentLightningBolt = new TridentLightningBolt(
                                       (EntityType<? extends LightningBolt>)AnnoyingVillagersModEntities.TRIDENT_LIGHTNING_BOLT.get(), serverLevel
                                    );
                                    tridentLightningBolt.setOwner((LivingEntity)livingEntityPatch.getOriginal());
                                    tridentLightningBolt.m_6027_(
                                       (double)checkPos.m_123341_() + 0.5, (double)checkPos.m_123342_() + 1.0, (double)checkPos.m_123343_() + 0.5
                                    );
                                    serverLevel.m_7967_(tridentLightningBolt);
                                 }
                              }
                           }
                        },
                        Side.SERVER
                     )
                  }
               )
      );
      TRIDENT_THROW_1 = builder.nextAccessor(
         "biped/battle_style/trident_throw_1",
         access -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.2F, 0.0F, 0.2F, 0.3F, 0.5F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, access, Armatures.BIPED
               )
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> 2.0F
               )
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.0F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(0.05F, AVAnimations.ReuseableEvents.THROW_TRIDENT_HAND_RIGHT, Side.SERVER)
                  }
               )
      );
      TRIDENT_THROW_5 = builder.nextAccessor(
         "biped/battle_style/trident_throw_5",
         access -> (AttackAnimation)new AttackAnimation(
                  0.2F, 0.0F, 0.75F, 0.9F, 2.0F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, access, Armatures.BIPED
               )
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> 2.0F
               )
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.2F, ReusableSources.PLAY_SOUND, Side.CLIENT).params(SoundEvents.f_12516_),
                     InTimeEvent.create(0.2F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(0.35F, ReusableSources.PLAY_SOUND, Side.CLIENT).params(SoundEvents.f_12516_),
                     InTimeEvent.create(0.35F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(0.4F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(0.4F, AVAnimations.ReuseableEvents.THROW_TRIDENT_HAND_RIGHT_EXPLODE, Side.SERVER)
                  }
               )
      );
      ADVANCED_DUELIST_WHIRLEDGE = builder.nextAccessor(
         "biped/battle_style/advanced_duelist_whirledge",
         access -> (AttackAnimation)new AttackAnimation(
                  0.2F,
                  access,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.3F, 0.3F, 0.4F, 0.4F, 0.4F, InteractionHand.MAIN_HAND, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.1F)),
                     new Phase(0.4F, 0.0F, 0.4F, 0.5F, 0.5F, 0.5F, InteractionHand.OFF_HAND, ((HumanoidArmature)humanoidArmature.get()).toolL, null)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.15F)),
                     new Phase(0.5F, 0.0F, 0.5F, 0.6F, 0.6F, 0.6F, InteractionHand.MAIN_HAND, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.2F)),
                     new Phase(0.6F, 0.0F, 0.6F, 0.7F, 0.7F, 0.7F, InteractionHand.OFF_HAND, ((HumanoidArmature)humanoidArmature.get()).toolL, null)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.25F)),
                     new Phase(0.7F, 0.0F, 0.7F, 0.8F, 0.8F, 0.8F, InteractionHand.MAIN_HAND, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F)),
                     new Phase(0.8F, 0.0F, 0.8F, 0.9F, 0.9F, 0.9F, InteractionHand.OFF_HAND, ((HumanoidArmature)humanoidArmature.get()).toolL, null)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.35F)),
                     new Phase(
                           0.9F,
                           0.0F,
                           1.25F,
                           1.35F,
                           2.0F,
                           2.0F,
                           InteractionHand.MAIN_HAND,
                           ((HumanoidArmature)humanoidArmature.get()).rootJoint,
                           ColliderPreset.BATTOJUTSU_DASH
                        )
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F))
                  }
               )
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> 2.0F
               )
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.4F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(0.4F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(0.7F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(0.7F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(1.0F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(1.0F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER)
                  }
               )
      );
      ADVANCED_DUELIST_SHOOTING_STAR = builder.nextAccessor(
         "biped/battle_style/advanced_duelist_shooting_star",
         access -> (AttackAnimation)new AttackAnimation(
                  0.2F, 0.6F, 0.5F, 0.6F, 1.9F, ColliderPreset.BATTOJUTSU_DASH, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, access, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.5F}))
               .addState(EntityState.CAN_SKILL_EXECUTION, false)
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER,
                  (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> {
                     if (elapsedTime >= 0.5F && elapsedTime < 0.6F) {
                        float dpx = (float)((LivingEntity)livingEntityPatch.getOriginal()).m_20185_();
                        float dpy = (float)((LivingEntity)livingEntityPatch.getOriginal()).m_20186_();
                        float dpz = (float)((LivingEntity)livingEntityPatch.getOriginal()).m_20189_();
                        BlockState block = ((LivingEntity)livingEntityPatch.getOriginal())
                           .m_9236_()
                           .m_8055_(new MutableBlockPos((double)dpx, (double)dpy, (double)dpz));

                        while ((block.m_60734_() instanceof BushBlock || block.m_60795_()) && !block.m_60713_(Blocks.f_50626_)) {
                           block = ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_9236_()
                              .m_8055_(new MutableBlockPos((double)dpx, (double)(--dpy), (double)dpz));
                        }

                        float distanceToGround = (float)Math.max(Math.abs(((LivingEntity)livingEntityPatch.getOriginal()).m_20186_() - (double)dpy) - 1.0, 0.0);
                        LivingEntity livingentity = (LivingEntity)livingEntityPatch.getOriginal();
                        Vec3f direction = new Vec3f(2.5F, -1.5F, 0.0F);
                        OpenMatrix4f rotation = new OpenMatrix4f()
                           .rotate(
                              -((float)Math.toRadians((double)(((LivingEntity)livingEntityPatch.getOriginal()).f_20884_ + 90.0F))), new Vec3f(0.0F, 1.0F, 0.0F)
                           );
                        OpenMatrix4f.transform3v(rotation, direction, direction);
                        if (distanceToGround > 0.5F) {
                           livingentity.m_6478_(MoverType.SELF, direction.toDoubleVector());
                           return 0.025F;
                        } else {
                           return speed;
                        }
                     } else {
                        return speed;
                     }
                  }
               )
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.1F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(0.3F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(0.5F, ReusableSources.PLAY_SOUND, Side.CLIENT).params(SoundEvents.f_12515_),
                     InTimeEvent.create(0.5F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(0.6F, ReusableSources.FRACTURE_GROUND_SIMPLE, Side.SERVER)
                        .params(new Vec3f(0.0F, -0.24F, -2.0F), ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, 1.2, 1.0F)
                  }
               )
      );
      SQUIRE_SWORD_IDLE = builder.nextAccessor("biped/battle_style/squire_sword_idle", accessor -> new StaticAnimation(true, accessor, Armatures.BIPED));
      SQUIRE_SWORD_WALK = builder.nextAccessor("biped/battle_style/squire_sword_walk", accessor -> new MovementAnimation(true, accessor, Armatures.BIPED));
      SQUIRE_SWORD_RUN = builder.nextAccessor("biped/battle_style/squire_sword_run", accessor -> new MovementAnimation(true, accessor, Armatures.BIPED));
      SQUIRE_SWORD_AUTO_1 = builder.nextAccessor(
         "biped/battle_style/squire_sword_auto1",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.1F, 0.0F, 0.2F, 0.35F, 0.5F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
      );
      SQUIRE_SWORD_AUTO_2 = builder.nextAccessor(
         "biped/battle_style/squire_sword_auto2",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.2F, 0.0F, 0.2F, 0.35F, 0.5F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
      );
      SQUIRE_SWORD_AUTO_3 = builder.nextAccessor(
         "biped/battle_style/squire_sword_auto3",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.2F, 0.0F, 0.2F, 0.35F, 2.0F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
      );
      SQUIRE_SWORD_DASH_ATTACK = builder.nextAccessor(
         "biped/battle_style/squire_sword_dash_attack",
         accessor -> new AttackAnimation(
               0.2F,
               accessor,
               Armatures.BIPED,
               new Phase[]{
                  new Phase(0.0F, 0.0F, 0.2F, 0.3F, 0.5F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F))
               }
            )
      );
      SQUIRE_SWORD_HOP_ATTACK = builder.nextAccessor(
         "biped/battle_style/squire_sword_hop_attack",
         accessor -> (AirSlashAnimation)new AirSlashAnimation(
                  0.1F, 0.0F, 0.2F, 0.35F, 2.0F, false, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, false)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
      );
      SQUIRE_SWORD_HEAVY_BLOW = builder.nextAccessor(
         "biped/battle_style/squire_sword_heavy_blow",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.1F, 0.0F, 0.7F, 0.8F, 1.5F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addState(EntityState.CAN_SKILL_EXECUTION, false)
      );
      SABRE_AUTO3 = builder.nextAccessor(
         "biped/battle_style/sabre_auto3",
         access -> (AttackAnimation)new AttackAnimation(
                  0.05F, 0.0F, 0.1F, 0.2F, 1.9F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, access, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 1)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
      );
      SABRE_DASH_ATTACK = builder.nextAccessor(
         "biped/battle_style/sabre_dash_attack",
         access -> (DashAttackAnimation)new DashAttackAnimation(
                  0.2F, 0.0F, 0.3F, 0.45F, 1.9F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, access, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
      );
      SABRE_AIR_ATTACK = builder.nextAccessor(
         "biped/battle_style/sabre_aerial",
         access -> (AirSlashAnimation)new AirSlashAnimation(
                  0.2F, 0.0F, 0.5F, 0.6F, 1.9F, false, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, access, Armatures.BIPED
               )
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, false)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
      );
      SABRE_QUAD_STING = builder.nextAccessor(
         "biped/battle_style/sabre_quadsting",
         access -> (AttackAnimation)new AttackAnimation(
                  0.2F,
                  access,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(0.0F, 0.0F, 0.5F, 0.6F, 0.65F, 0.65F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F))
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD),
                     new Phase(0.65F, 0.0F, 0.65F, 0.75F, 0.8F, 0.8F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD),
                     new Phase(0.8F, 0.0F, 0.8F, 0.9F, 1.1F, 1.1F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7F))
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD),
                     new Phase(1.1F, 0.0F, 1.1F, 1.2F, 2.0F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F))
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
      );
      TACHI_BLOSSOM_SLASH = builder.nextAccessor(
         "biped/battle_style/tachi_blossom_slash",
         access -> (AttackAnimation)new AttackAnimation(
                  0.1F,
                  access,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(0.0F, 0.0F, 0.0F, 0.1F, 0.1F, 0.1F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, ColliderPreset.LONGSWORD),
                     new Phase(0.15F, 0.0F, 0.15F, 0.25F, 0.25F, 0.25F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, ColliderPreset.LONGSWORD),
                     new Phase(0.25F, 0.0F, 0.3F, 0.4F, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, ColliderPreset.LONGSWORD),
                     new Phase(0.4F, 0.0F, 0.45F, 0.55F, 0.6F, 0.6F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, ColliderPreset.LONGSWORD),
                     new Phase(0.6F, 0.0F, 0.6F, 0.8F, 2.0F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, ColliderPreset.LONGSWORD)
                  }
               )
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(12.0F))
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(1.0F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(12.0F), 1)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(1.0F), 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD, 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(12.0F), 2)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(1.0F), 2)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD, 2)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(12.0F), 3)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(1.0F), 3)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD, 3)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(12.0F), 4)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG, 4)
               .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
      );
      AXE_AUTO1 = builder.nextAccessor(
         "biped/battle_style/axe_auto1",
         access -> new BasicAttackAnimation(0.2F, 0.0F, 0.7F, 0.8F, 1.0F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, access, Armatures.BIPED)
      );
      AXE_AUTO2 = builder.nextAccessor(
         "biped/battle_style/axe_auto2",
         access -> new BasicAttackAnimation(0.2F, 0.0F, 0.5F, 0.6F, 1.0F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, access, Armatures.BIPED)
      );
      AXE_AUTO3 = builder.nextAccessor(
         "biped/battle_style/axe_auto3",
         access -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.2F, 0.0F, 0.35F, 0.55F, 1.5F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, access, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F)
      );
      AXE_DASH = builder.nextAccessor(
         "biped/battle_style/axe_dash",
         access -> new DashAttackAnimation(0.2F, 0.0F, 0.35F, 0.45F, 1.5F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, access, Armatures.BIPED)
      );
      AXE_AIRSLASH = builder.nextAccessor(
         "biped/battle_style/axe_airslash",
         access -> (AirSlashAnimation)new AirSlashAnimation(
                  0.2F, 0.0F, 0.5F, 0.65F, 1.5F, false, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, access, Armatures.BIPED
               )
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, false)
      );
      AXE_INNATE = builder.nextAccessor(
         "biped/battle_style/axe_innate",
         access -> (AttackAnimation)new AttackAnimation(
                  0.2F, 0.0F, 0.9F, 1.5F, 3.0F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, access, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.25F)
      );
      BAXE_IDLE = builder.nextAccessor("biped/battle_style/baxe_idle", accessor -> new StaticAnimation(true, accessor, Armatures.BIPED));
      BAXE_WALK = builder.nextAccessor("biped/battle_style/baxe_walk", accessor -> new MovementAnimation(0.1F, true, accessor, Armatures.BIPED));
      BAXE_RUN = builder.nextAccessor(
         "biped/battle_style/baxe_run",
         accessor -> (MovementAnimation)new MovementAnimation(0.2F, true, accessor, Armatures.BIPED)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.8F)
      );
      BAXE_AUTO_1 = builder.nextAccessor(
         "biped/battle_style/baxe_auto1",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.2F, 0.0F, 0.4F, 0.6F, 1.0F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.9F)
      );
      BAXE_AUTO_2 = builder.nextAccessor(
         "biped/battle_style/baxe_auto2",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.5F, 0.0F, 0.55F, 0.65F, 2.0F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F))
               .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 2)
      );
      BAXE_DASH_ATTACK = builder.nextAccessor(
         "biped/battle_style/baxe_dash_attack",
         accessor -> (AirSlashAnimation)new AirSlashAnimation(
                  0.2F, 0.0F, 1.15F, 1.25F, 3.0F, false, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.7F))
               .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 2)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, false)
               .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(1.25F, ReusableSources.FRACTURE_GROUND_SIMPLE, Side.CLIENT)
                        .params(new Vec3f(0.0F, -0.24F, -2.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolR, 2.0, 2.0F)
                  }
               )
      );
      BAXE_AIR_ATTACK = builder.nextAccessor(
         "biped/battle_style/baxe_airslash",
         accessor -> (DashAttackAnimation)new DashAttackAnimation(
                  0.5F, 0.0F, 0.4F, 0.6F, 2.5F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 2)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, false)
      );
      BAXE_SEISMIC_IMPACT = builder.nextAccessor(
         "biped/battle_style/baxe_seismic_impact",
         access -> (AirSlashAnimation)new AirSlashAnimation(
                  0.5F,
                  access,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(0.0F, 0.0F, 1.4F, 1.6F, 1.6F, 1.6F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F)),
                     new Phase(
                           1.6F, 0.0F, 1.6F, 1.7F, 3.0F, 4.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, WOMWeaponColliders.TORMENT_BERSERK_AIRSLAM
                        )
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F))
                        .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
                        .addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.f_11913_)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.9F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(1.6F, ReusableSources.FRACTURE_GROUND_SIMPLE, Side.CLIENT)
                        .params(new Vec3f(0.0F, -0.24F, -2.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolR, 3.0, 2.0F)
                  }
               )
      );
      GREATSWORD_DASH_ATTACK = builder.nextAccessor(
         "biped/battle_style/greatsword_dash_attack",
         access -> (DashAttackAnimation)new DashAttackAnimation(
                  0.2F, 0.0F, 0.5F, 0.65F, 2.0F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, access, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.8F)
      );
      GREATSWORD_POWER_GEYSER = builder.nextAccessor(
         "biped/battle_style/greatsword_power_geyser",
         access -> (AttackAnimation)new AttackAnimation(
                  0.2F, 0.0F, 0.8F, 0.9F, 2.0F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, access, Armatures.BIPED
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.1F))
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.8F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.9F, ReusableSources.FRACTURE_GROUND_SIMPLE, Side.SERVER)
                        .params(new Vec3f(0.0F, -0.3F, -5.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolR, 1.1, 1.55F)
                  }
               )
      );
      GREATSWORD_AIRSLAM = builder.nextAccessor(
         "biped/battle_style/greatsword_airslam",
         access -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.2F, 0.0F, 0.5F, 0.65F, 2.0F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, access, Armatures.BIPED
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.3F))
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.5F}))
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.8F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.65F, ReusableSources.FRACTURE_GROUND_SIMPLE, Side.SERVER)
                        .params(new Vec3f(0.0F, -0.3F, -5.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolR, 1.1, 1.55F)
                  }
               )
      );
      THIEF_AUTO1 = builder.nextAccessor(
         "biped/battle_style/thief_auto1",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.2F, 0.0F, 0.2F, 0.35F, 0.5F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
      );
      THIEF_AUTO3 = builder.nextAccessor(
         "biped/battle_style/thief_auto3",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.2F, 0.0F, 0.55F, 0.65F, 1.7F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
      );
      THIEF_DASH_ATTACK = builder.nextAccessor(
         "biped/battle_style/thief_dash_attack",
         accessor -> (DashAttackAnimation)new DashAttackAnimation(
                  0.2F, 0.0F, 0.3F, 0.4F, 1.7F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
      );
      THIEF_AIRSLASH = builder.nextAccessor(
         "biped/battle_style/thief_airslash",
         accessor -> (AirSlashAnimation)new AirSlashAnimation(
                  0.2F, 0.0F, 0.2F, 0.3F, 1.7F, false, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, false)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
      );
      THIEF_STEAL = builder.nextAccessor(
         "biped/battle_style/thief_steal",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.2F, 0.0F, 0.65F, 0.75F, 1.7F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
      );
      DUAL_BLADES_AIRSLAM = builder.nextAccessor(
         "biped/battle_style/dual_blades_airslam",
         access -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.2F,
                  access,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.0F, 0.4F, 0.5F, 0.6F, 0.6F, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F))
                  }
               )
               .addEvents(
                  StaticAnimationProperty.ON_BEGIN_EVENTS,
                  new AnimationEvent[]{
                     SimpleEvent.create(
                        (livingEntityPatch, assetAccessor, animationParameters) -> {
                           if (assetAccessor.get() instanceof AttackAnimation animation
                              && ((LivingEntity)livingEntityPatch.getOriginal()).m_21206_().m_41720_() instanceof SwordItem swordItem) {
                              animation.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.adder(swordItem.m_43299_()));
                           }
                        },
                        Side.SERVER
                     )
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
      );
      DUAL_BLADES_AUTO3 = builder.nextAccessor(
         "biped/battle_style/dual_blades_auto3",
         access -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.2F,
                  access,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.0F, 0.55F, 0.65F, 0.6F, 0.65F, InteractionHand.OFF_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.25F)),
                     new Phase(0.65F, 0.0F, 0.65F, 0.75F, 0.75F, 0.75F, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.25F)),
                     new Phase(0.75F, 0.0F, 0.9F, 1.0F, 1.0F, 1.0F, InteractionHand.OFF_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.25F)),
                     new Phase(1.0F, 0.0F, 1.0F, 1.1F, 3.0F, 3.0F, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.25F))
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
      );
      DUAL_BLADES_WHIRLEDGE = builder.nextAccessor(
         "biped/battle_style/dual_blades_whirledge",
         access -> (AttackAnimation)new AttackAnimation(
                  0.2F,
                  access,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.3F, 0.3F, 0.4F, 0.4F, 0.4F, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.1F)),
                     new Phase(0.4F, 0.0F, 0.4F, 0.5F, 0.5F, 0.5F, InteractionHand.OFF_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.15F)),
                     new Phase(0.5F, 0.0F, 0.5F, 0.6F, 0.6F, 0.6F, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.2F)),
                     new Phase(0.6F, 0.0F, 0.6F, 0.7F, 0.7F, 0.7F, InteractionHand.OFF_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.25F)),
                     new Phase(0.7F, 0.0F, 0.7F, 0.8F, 0.8F, 0.8F, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F)),
                     new Phase(0.8F, 0.0F, 0.8F, 0.9F, 0.9F, 0.9F, InteractionHand.OFF_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.35F)),
                     new Phase(
                           0.9F,
                           0.0F,
                           1.25F,
                           1.35F,
                           2.0F,
                           2.0F,
                           InteractionHand.MAIN_HAND,
                           ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                           ColliderPreset.BATTOJUTSU_DASH
                        )
                        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL)
                        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F))
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
      );
      SWORD_DASH_ATTACK = builder.nextAccessor(
         "biped/battle_style/sword_dash_attack",
         access -> (DashAttackAnimation)new DashAttackAnimation(
                  0.2F, 0.0F, 0.3F, 0.45F, 1.9F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, access, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
      );
      IRON_LOTUS_AUTO1 = builder.nextAccessor(
         "biped/battle_style/iron_lotus_auto1",
         access -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.1F, 0.0F, 0.1F, 0.2F, 0.25F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, access, Armatures.BIPED
               )
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 3.2F)
      );
      IRON_LOTUS_AUTO2 = builder.nextAccessor(
         "biped/battle_style/iron_lotus_auto2",
         access -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.1F, 0.0F, 0.1F, 0.2F, 0.25F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolL, access, Armatures.BIPED
               )
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 3.2F)
      );
      IRON_LOTUS_AUTO3 = builder.nextAccessor(
         "biped/battle_style/iron_lotus_auto3",
         access -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.1F, 0.0F, 0.1F, 0.2F, 1.0F, null, ((HumanoidArmature)Armatures.BIPED.get()).legR, access, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 3.2F)
      );
      IRON_LOTUS_DASH_ATTACK = builder.nextAccessor(
         "biped/battle_style/iron_lotus_dash_attack",
         access -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.1F, 0.0F, 0.2F, 0.4F, 1.0F, ColliderPreset.BATTOJUTSU_DASH, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, access, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 5.0F)
      );
      TRIDENT_THROW_3 = builder.nextAccessor(
         "biped/battle_style/trident_throw_3",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.15F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(
                        0.0F, 0.3F, 0.5F, 0.3F, 0.3F, InteractionHand.OFF_HAND, ((HumanoidArmature)humanoidArmature.get()).handR, WOMWeaponColliders.PUNCH
                     ),
                     new Phase(
                        0.3F,
                        0.5F,
                        0.7F,
                        0.8F,
                        Float.MAX_VALUE,
                        InteractionHand.OFF_HAND,
                        ((HumanoidArmature)humanoidArmature.get()).toolR,
                        WOMWeaponColliders.PUNCH
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.f_12516_)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.1F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_WEAPON_RIGHT, Side.SERVER),
                     InTimeEvent.create(0.3F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_WEAPON_RIGHT, Side.SERVER),
                     InTimeEvent.create(
                        0.3F,
                        (livingEntityPatch, self, p) -> {
                           if (((LivingEntity)livingEntityPatch.getOriginal()).m_9236_() instanceof ServerLevel serverLevel) {
                              BlueDemonThunderBeamEntity beam = new BlueDemonThunderBeamEntity(
                                 (EntityType<? extends BlueDemonThunderBeamEntity>)AnnoyingVillagersModEntities.BLUE_DEMON_THUNDER_BEAM.get(),
                                 serverLevel,
                                 (LivingEntity)livingEntityPatch.getOriginal(),
                                 10,
                                 6,
                                 7.5
                              );
                              beam.initSpawnState();
                              serverLevel.m_7967_(beam);
                           }
                        },
                        Side.SERVER
                     ),
                     InTimeEvent.create(0.5F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_WEAPON_RIGHT, Side.SERVER)
                  }
               )
      );
   }
}
