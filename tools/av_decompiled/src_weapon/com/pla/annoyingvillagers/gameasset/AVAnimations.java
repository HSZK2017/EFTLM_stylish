package com.pla.annoyingvillagers.gameasset;

import com.pla.annoyingvillagers.compat.p1nero_bow.AnimsP1neroEpicBow;
import com.pla.annoyingvillagers.entity.BlackFireEntity;
import com.pla.annoyingvillagers.entity.SwordsmanHerobrineEntity;
import com.pla.annoyingvillagers.gameasset.AVAnimations.ReuseableEvents;
import com.pla.annoyingvillagers.item.BlueDemonTridentItem;
import com.pla.annoyingvillagers.item.DemoniacVoltageReaverItem;
import com.pla.annoyingvillagers.item.EarthAxeItem;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import com.pla.annoyingvillagers.util.HerobrinePortalCombatUtil;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.joml.Math;
import reascer.wom.animation.attacks.BasicMultipleAttackAnimation;
import reascer.wom.gameasset.colliders.WOMWeaponColliders;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.AnimationManager.AnimationRegistryEvent;
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
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.HitEntityList.Priority;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.Animations.ReusableSources;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.StunType;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.MOD
)
public class AVAnimations {
   public static AnimationAccessor<ActionAnimation> TRIDENT_ATTACK;
   public static AnimationAccessor<StaticAnimation> KNOCKED_ELITE;
   public static AnimationAccessor<StaticAnimation> EATING_ELITE_1;
   public static AnimationAccessor<StaticAnimation> EATING_ELITE_2;
   public static AnimationAccessor<StaticAnimation> EATING_ELITE_3;
   public static AnimationAccessor<StaticAnimation> EATING_ELITE_4;
   public static AnimationAccessor<StaticAnimation> HEROBRINE_ANIMATE;
   public static AnimationAccessor<StaticAnimation> HEROBRINE_HEALING;
   public static AnimationAccessor<StaticAnimation> LOW_CLONE_ESCAPE;
   public static AnimationAccessor<StaticAnimation> SNAKE_BLADE;
   public static AnimationAccessor<StaticAnimation> SNAKE_BLADE_GUARD;
   public static AnimationAccessor<StaticAnimation> IDLE_BREAK;
   public static AnimationAccessor<ActionAnimation> PLACE_BLOCK;
   public static AnimationAccessor<AttackAnimation> BLACK_FIRE_SWORD_SKILL;
   public static AnimationAccessor<ActionAnimation> BLUE_FLAME_SWORD;
   public static AnimationAccessor<BasicAttackAnimation> DIAMOND_BLASTER_SKILL;
   public static AnimationAccessor<BasicAttackAnimation> EARTH_AXE_SHOOT;
   public static AnimationAccessor<BasicMultipleAttackAnimation> RED_AXE_ATTACK;
   public static AnimationAccessor<StaticAnimation> BLACKSCRATCHER_IDLE;
   public static AnimationAccessor<BasicAttackAnimation> BLACKSCRATCHER_ATTACK;
   public static AnimationAccessor<StaticAnimation> HOOK_HAND_LEFT;
   public static AnimationAccessor<StaticAnimation> HOOK_HAND_LEFT_TOP;
   public static AnimationAccessor<StaticAnimation> HOOK_HAND_RIGHT;
   public static AnimationAccessor<StaticAnimation> HOOK_HAND_RIGHT_TOP;

   @SubscribeEvent
   public static void registerAnimations(AnimationRegistryEvent event) {
      event.newBuilder("annoyingvillagers", AVAnimations::build);
   }

   private static void build(AnimationBuilder builder) {
      AnimsEpicFight.build(builder);
      AnimsEpicFightACG.build(builder);
      AnimsEpicFightAwaken.build(builder);
      AnimsEpicFightBattleArts.build(builder);
      AnimsEpicFightDualGreatsword.build(builder);
      AnimsEpicFightInfernalGainer.build(builder);
      AnimsEpicFightIronSpell.build(builder);
      AnimsEpicFightSanji.build(builder);
      AnimsEpicFightValourGuard.build(builder);
      AnimsPugilistSteve.build(builder);
      AnimsSculkSteve.build(builder);
      AnimsWom.build(builder);
      AnimsYonchiChikito.build(builder);
      AnimsEpicFightGuandao.build(builder);
      AnimsTacticalImbuements.build(builder);
      if (ModList.get().isLoaded("p1nero_bow")) {
         AnimsP1neroEpicBow.build(builder);
      }

      ArmatureAccessor<HumanoidArmature> humanoidArmature = Armatures.BIPED;
      TRIDENT_ATTACK = builder.nextAccessor(
         "biped/pla/trident_attack",
         accessor -> (ActionAnimation)new ActionAnimation(0.05F, Float.MAX_VALUE, accessor, humanoidArmature)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.2F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(0.2F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(0.8F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(0.8F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(1.0F, (livingEntityPatch, self, p) -> {
                        if (((LivingEntity)livingEntityPatch.getOriginal()).m_9236_() instanceof ServerLevel serverLevel) {
                           BlueDemonTridentItem.relaunchGroundedTridents(serverLevel, (LivingEntity)livingEntityPatch.getOriginal());
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
                     InTimeEvent.create(3.8F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(3.8F, ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(4.0F, (livingEntityPatch, self, p) -> {
                        if (((LivingEntity)livingEntityPatch.getOriginal()).m_9236_() instanceof ServerLevel serverLevel) {
                           BlueDemonTridentItem.summonLightningAtGroundedTridents(serverLevel, (LivingEntity)livingEntityPatch.getOriginal());
                        }
                     }, Side.SERVER)
                  }
               )
      );
      HEROBRINE_ANIMATE = builder.nextAccessor("biped/pla/herobrine_animate", accessor -> new StaticAnimation(false, accessor, humanoidArmature));
      HEROBRINE_HEALING = builder.nextAccessor("biped/pla/herobrine_healing", accessor -> new StaticAnimation(true, accessor, humanoidArmature));
      LOW_CLONE_ESCAPE = builder.nextAccessor("biped/pla/low_clone_escape", accessor -> new StaticAnimation(true, accessor, humanoidArmature));
      KNOCKED_ELITE = builder.nextAccessor("biped/pla/knocked_elite", accessor -> new StaticAnimation(true, accessor, humanoidArmature));
      EATING_ELITE_1 = builder.nextAccessor("biped/pla/eating_elite_1", accessor -> new StaticAnimation(true, accessor, humanoidArmature));
      EATING_ELITE_2 = builder.nextAccessor("biped/pla/eating_elite_2", accessor -> new StaticAnimation(true, accessor, humanoidArmature));
      EATING_ELITE_3 = builder.nextAccessor("biped/pla/eating_elite_3", accessor -> new StaticAnimation(true, accessor, humanoidArmature));
      EATING_ELITE_4 = builder.nextAccessor("biped/pla/eating_elite_4", accessor -> new StaticAnimation(true, accessor, humanoidArmature));
      SNAKE_BLADE = builder.nextAccessor(
         "biped/pla/snake_blade",
         accessor -> new StaticAnimation(false, accessor, humanoidArmature)
               .addState(EntityState.CAN_BASIC_ATTACK, false)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.0F, (livingEntityPatch, self, p) -> {
                  ItemStack stack = ((LivingEntity)livingEntityPatch.getOriginal()).m_21205_();
                  DemoniacVoltageReaverItem.tryStartSnakeAnimation(stack, (LivingEntity)livingEntityPatch.getOriginal(), false);
               }, Side.SERVER)})
      );
      SNAKE_BLADE_GUARD = builder.nextAccessor(
         "biped/pla/snake_blade_guard",
         accessor -> new StaticAnimation(false, accessor, humanoidArmature)
               .addState(EntityState.CAN_BASIC_ATTACK, false)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.0F,
                        (livingEntityPatch, self, p) -> {
                           if (livingEntityPatch.getOriginal() instanceof SwordsmanHerobrineEntity swordsmanHerobrineEntity
                              && (
                                 swordsmanHerobrineEntity.getGregUUID() != null
                                       && HerobrinePortalCombatUtil.hasNearbyPortalGroup(
                                          swordsmanHerobrineEntity, swordsmanHerobrineEntity.getGregUUID(), 6, 48.0
                                       )
                                    || HerobrinePortalCombatUtil.hasNearbyPortalGroup(swordsmanHerobrineEntity, null, 6, 48.0)
                              )) {
                              return;
                           }

                           ItemStack stack = ((LivingEntity)livingEntityPatch.getOriginal()).m_21205_();
                           DemoniacVoltageReaverItem.tryStartSnakeAnimation(stack, (LivingEntity)livingEntityPatch.getOriginal(), true);
                        },
                        Side.SERVER
                     )
                  }
               )
      );
      IDLE_BREAK = builder.nextAccessor("biped/pla/idle_break", accessor -> new StaticAnimation(false, accessor, humanoidArmature));
      PLACE_BLOCK = builder.nextAccessor("biped/pla/place_block", accessor -> new ActionAnimation(0.0F, accessor, humanoidArmature));
      BLACK_FIRE_SWORD_SKILL = builder.nextAccessor(
         "biped/pla/black_fire_sword_skill",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.0F, 0.0F, 0.0F, 0.0F, Float.MAX_VALUE, null, ((HumanoidArmature)Armatures.BIPED.get()).head, accessor, Armatures.BIPED
               )
               .addState(EntityState.CAN_BASIC_ATTACK, false)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.5F)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.8F,
                        (livingEntityPatch, self, p) -> BlackFireEntity.shootFromOwnerLook(
                              ((LivingEntity)livingEntityPatch.getOriginal()).m_9236_(), (LivingEntity)livingEntityPatch.getOriginal()
                           ),
                        Side.SERVER
                     )
                  }
               )
      );
      BLUE_FLAME_SWORD = builder.nextAccessor(
         "biped/pla/blue_flame_sword",
         accessor -> (ActionAnimation)new ActionAnimation(0.0F, accessor, humanoidArmature)
               .addState(EntityState.CAN_BASIC_ATTACK, false)
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, false)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
      );
      DIAMOND_BLASTER_SKILL = builder.nextAccessor(
         "biped/pla/diamond_blaster_skill",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.08F, 0.05F, 0.15F, 0.2F, null, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.5F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.5F))
      );
      EARTH_AXE_SHOOT = builder.nextAccessor(
         "biped/pla/earth_axe_shoot",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.1F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.05F, 0.3F, 0.4F, 1.167F, 1.65F, InteractionHand.MAIN_HAND, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.1F, 0.1F, 0.4F, 0.6F, 0.6F, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                  }
               )
               .addProperty(AttackPhaseProperty.HIT_PRIORITY, Priority.TARGET)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.5F)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        1.0F,
                        (livingEntityPatch, staticAnimation, object) -> {
                           Vec3 bladePos = EpicfightUtil.getJointWithTranslation(
                              livingEntityPatch.getOriginal(), new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolR, 0.5F, 0.0
                           );
                           if (bladePos != null) {
                              LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
                              if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
                                 BlockPos liftPos = EarthAxeItem.findLiftableBlockUnderPoint(serverLevel, bladePos, 6, 1);
                                 if (liftPos != null) {
                                    EarthAxeItem.liftBlockAt(serverLevel, liftPos, livingEntity);
                                 }
                              }
                           }
                        },
                        Side.SERVER
                     )
                  }
               )
      );
      RED_AXE_ATTACK = builder.nextAccessor(
         "biped/pla/red_axe_attack",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.05F,
                  1.0F,
                  1.2F,
                  2.5F,
                  WOMWeaponColliders.TORMENT_BERSERK_AIRSLAM,
                  ((HumanoidArmature)humanoidArmature.get()).rootJoint,
                  accessor,
                  humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(4.0F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.8F))
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(3.0F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
               .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.FINISHER))
               .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.BYPASS_DODGE))
               .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.GUARD_PUNCTURE))
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.1F)
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, null)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.35F, 0.9F}))
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
                  if (elapsedTime >= 0.9F && elapsedTime < 1.15F) {
                     float dpx = (float)((LivingEntity)entitypatch.getOriginal()).m_20185_();
                     float dpy = (float)((LivingEntity)entitypatch.getOriginal()).m_20186_();
                     float dpz = (float)((LivingEntity)entitypatch.getOriginal()).m_20189_();
                     BlockState block = ((LivingEntity)entitypatch.getOriginal()).m_9236_().m_8055_(new MutableBlockPos((double)dpx, (double)dpy, (double)dpz));

                     while ((block.m_60734_() instanceof BushBlock || block.m_60795_()) && !block.m_60713_(Blocks.f_50626_)) {
                        block = ((LivingEntity)entitypatch.getOriginal()).m_9236_().m_8055_(new MutableBlockPos((double)dpx, (double)(--dpy), (double)dpz));
                     }

                     float distanceToGround = (float)Math.max(Math.abs(((LivingEntity)entitypatch.getOriginal()).m_20186_() - (double)dpy) - 1.0, 0.0);
                     return 1.0F - (1.0F / (-distanceToGround - 1.0F) + 1.0F);
                  } else {
                     return speed;
                  }
               })
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.35F, reascer.wom.gameasset.ReuseableEvents.AIRBURST_JUMP, Side.CLIENT),
                     InTimeEvent.create(1.15F, reascer.wom.gameasset.ReuseableEvents.TORMENT_GROUNDSLAM, Side.CLIENT)
                  }
               )
      );
      BLACKSCRATCHER_IDLE = builder.nextAccessor("biped/pla/blackscratcher_idle", accessor -> new StaticAnimation(true, accessor, humanoidArmature));
      BLACKSCRATCHER_ATTACK = builder.nextAccessor(
         "biped/pla/blackscratcher_attack.",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.08F, 0.05F, 0.15F, 0.2F, null, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.5F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.5F))
      );
      HOOK_HAND_LEFT = builder.nextAccessor("biped/pla/left_hand_hook", accessor -> new StaticAnimation(true, accessor, humanoidArmature));
      HOOK_HAND_LEFT_TOP = builder.nextAccessor("biped/pla/left_hand_hook_top", accessor -> new StaticAnimation(true, accessor, humanoidArmature));
      HOOK_HAND_RIGHT = builder.nextAccessor("biped/pla/right_hand_hook", accessor -> new StaticAnimation(true, accessor, humanoidArmature));
      HOOK_HAND_RIGHT_TOP = builder.nextAccessor("biped/pla/right_hand_hook_top", accessor -> new StaticAnimation(true, accessor, humanoidArmature));
   }
}
