package com.pla.annoyingvillagers.gameasset;

import com.pla.annoyingvillagers.block.ObsidianBlock;
import com.pla.annoyingvillagers.block.ShadowObsidianBlock;
import com.pla.annoyingvillagers.clazz.TridentMode;
import com.pla.annoyingvillagers.compat.p1nero_bow.AnimsP1neroEpicBow;
import com.pla.annoyingvillagers.entity.ArmoredHerobrineEntity;
import com.pla.annoyingvillagers.entity.BlackFireEntity;
import com.pla.annoyingvillagers.entity.BlockProjectileEntity;
import com.pla.annoyingvillagers.entity.BlueDemonThrownTridentEntity;
import com.pla.annoyingvillagers.entity.Herobrine7Entity;
import com.pla.annoyingvillagers.entity.LowShadowHerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.ObsidianSledgehammerProjectileEntity;
import com.pla.annoyingvillagers.entity.ShadowHerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.ShadowHerobrineEntity;
import com.pla.annoyingvillagers.entity.SwordsmanHerobrineEntity;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModBlocks;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.item.BlueDemonTridentItem;
import com.pla.annoyingvillagers.item.DemoniacVoltageReaverItem;
import com.pla.annoyingvillagers.item.EarthAxeItem;
import com.pla.annoyingvillagers.item.LegendarySwordItem;
import com.pla.annoyingvillagers.item.ObsidianWeaponItem;
import com.pla.annoyingvillagers.item.ShadowObsidianPillarItem;
import com.pla.annoyingvillagers.item.ShadowObsidianSwordItem;
import com.pla.annoyingvillagers.item.ShadowObsidianWeaponItem;
import com.pla.annoyingvillagers.task.DelayedTask;
import com.pla.annoyingvillagers.util.BlueDemonUtil;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import com.pla.annoyingvillagers.util.HerobrinePortalCombatUtil;
import com.pla.annoyingvillagers.util.HerobrineUtil;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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
import yesman.epicfight.api.animation.property.AnimationEvent.E0;
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
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.gameasset.Animations.ReusableSources;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
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
                     InTimeEvent.create(0.2F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(0.2F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(0.8F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(0.8F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(1.0F, (livingEntityPatch, self, p) -> {
                        if (((LivingEntity)livingEntityPatch.getOriginal()).m_9236_() instanceof ServerLevel serverLevel) {
                           BlueDemonTridentItem.relaunchGroundedTridents(serverLevel, (LivingEntity)livingEntityPatch.getOriginal());
                        }
                     }, Side.SERVER),
                     InTimeEvent.create(1.2F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(1.2F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(1.8F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(1.8F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(2.2F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(2.2F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(2.8F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(2.8F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(3.2F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(3.2F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(3.8F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(3.8F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER),
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

   static class ReuseableEvents {
      public static final E0 FAST_SPINNING = (livingentitypatch, staticAnimation, aobject) -> ((LivingEntity)livingentitypatch.getOriginal())
            .m_9236_()
            .m_6269_(
               (Player)livingentitypatch.getOriginal(),
               livingentitypatch.getOriginal(),
               (SoundEvent)EpicFightSounds.WHOOSH.get(),
               SoundSource.NEUTRAL,
               0.5F,
               1.1F - (new Random().nextFloat() - 0.5F) * 0.2F
            );
      public static final E0 TRIDENT_SPINNING = (livingentitypatch, staticAnimation, aobject) -> ((LivingEntity)livingentitypatch.getOriginal())
            .m_9236_()
            .m_6269_(
               (Player)livingentitypatch.getOriginal(),
               livingentitypatch.getOriginal(),
               SoundEvents.f_12516_,
               SoundSource.NEUTRAL,
               0.5F,
               1.1F - (new Random().nextFloat() - 0.5F) * 0.2F
            );
      public static final E0 PLAY_TRIDENT_EFFECT_HAND_LEFT = (livingEntityPatch, staticAnimation, object) -> {
         LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
         if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
            Item weapon = livingEntity.m_21205_().m_41720_();
            if (weapon instanceof BlueDemonTridentItem) {
               Vec3 jointVec = EpicfightUtil.getJointWithTranslation(
                  livingEntity, new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).handL, new Random().nextFloat(-1.0F, 1.0F), 0.0
               );
               if (jointVec == null) {
                  return;
               }

               BlueDemonUtil.spawnBlueDemonEffect(serverLevel, livingEntity, jointVec, 1, 0.0, 0.0, 0.0, 0.0);
               float volume = (float)Mth.m_216263_(serverLevel.f_46441_, 0.05, 0.5);
               float pitch = (float)Mth.m_216263_(serverLevel.f_46441_, 0.8, 1.1);
               serverLevel.m_5594_(
                  null,
                  BlockPos.m_274561_(jointVec.f_82479_, jointVec.f_82480_, jointVec.f_82481_),
                  (SoundEvent)AnnoyingVillagersModSounds.ELECTRIFY.get(),
                  SoundSource.NEUTRAL,
                  volume,
                  pitch
               );
            }
         }
      };
      public static final E0 PLAY_TRIDENT_EFFECT_WEAPON_RIGHT = (livingEntityPatch, staticAnimation, object) -> {
         LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
         if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
            Item weapon = livingEntity.m_21205_().m_41720_();
            if (weapon instanceof BlueDemonTridentItem) {
               Vec3 jointVec = EpicfightUtil.getJointWithTranslation(
                  livingEntity, new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolR, new Random().nextFloat(-1.0F, 1.0F), 0.0
               );
               if (jointVec == null) {
                  return;
               }

               BlueDemonUtil.spawnBlueDemonEffect(serverLevel, livingEntity, jointVec, 1, 0.0, 0.0, 0.0, 0.0);
               float volume = (float)Mth.m_216263_(serverLevel.f_46441_, 0.05, 0.5);
               float pitch = (float)Mth.m_216263_(serverLevel.f_46441_, 0.8, 1.1);
               serverLevel.m_5594_(
                  null,
                  BlockPos.m_274561_(jointVec.f_82479_, jointVec.f_82480_, jointVec.f_82481_),
                  (SoundEvent)AnnoyingVillagersModSounds.ELECTRIFY.get(),
                  SoundSource.NEUTRAL,
                  volume,
                  pitch
               );
            }
         }
      };
      public static final E0 PLAY_TRIDENT_EFFECT_HAND_RIGHT = (livingEntityPatch, staticAnimation, object) -> {
         LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
         if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
            Item weapon = livingEntity.m_21205_().m_41720_();
            if (weapon instanceof BlueDemonTridentItem) {
               Vec3 jointVec = EpicfightUtil.getJointWithTranslation(
                  livingEntity, new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).handR, new Random().nextFloat(-1.0F, 1.0F), 0.0
               );
               if (jointVec == null) {
                  return;
               }

               BlueDemonUtil.spawnBlueDemonEffect(serverLevel, livingEntity, jointVec, 1, 0.0, 0.0, 0.0, 0.0);
               float volume = (float)Mth.m_216263_(serverLevel.f_46441_, 0.05, 0.5);
               float pitch = (float)Mth.m_216263_(serverLevel.f_46441_, 0.8, 1.1);
               serverLevel.m_5594_(
                  null,
                  BlockPos.m_274561_(jointVec.f_82479_, jointVec.f_82480_, jointVec.f_82481_),
                  (SoundEvent)AnnoyingVillagersModSounds.ELECTRIFY.get(),
                  SoundSource.NEUTRAL,
                  volume,
                  pitch
               );
            }
         }
      };
      public static final E0 THROW_TRIDENT_HAND_LEFT = (livingEntityPatch, staticAnimation, object) -> {
         LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
         if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
            ItemStack stack = livingEntity.m_21206_();
            Item weapon = stack.m_41720_();
            if (weapon instanceof BlueDemonTridentItem) {
               if (livingEntity instanceof Player player) {
                  stack.m_41622_(1, player, p -> p.m_21190_(InteractionHand.OFF_HAND));
               }

               Vec3 jointVec = EpicfightUtil.getJointWithTranslation(
                  livingEntity, new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).handL, 0.0F, 0.0
               );
               if (jointVec == null) {
                  return;
               }

               Vec3 direction = BlueDemonTridentItem.getTridentThrowDirection(livingEntity, jointVec);
               if (direction == null || direction.m_82556_() < 1.0E-7) {
                  return;
               }

               BlueDemonThrownTridentEntity trident = new BlueDemonThrownTridentEntity(serverLevel, livingEntity, stack.m_41777_());
               trident.assignSpawnSequence(livingEntity);
               trident.trimOldGroundedTridentsAroundOwnerOnSpawn();
               trident.m_6034_(jointVec.f_82479_, jointVec.f_82480_, jointVec.f_82481_);
               trident.m_146922_((float)(Mth.m_14136_(direction.f_82479_, direction.f_82481_) * (180.0 / java.lang.Math.PI)));
               trident.m_146926_(
                  (float)(
                     Mth.m_14136_(direction.f_82480_, java.lang.Math.sqrt(direction.f_82479_ * direction.f_82479_ + direction.f_82481_ * direction.f_82481_))
                        * (180.0 / java.lang.Math.PI)
                  )
               );
               float speed = 2.5F;
               float inaccuracy = 1.0F;
               trident.f_36705_ = Pickup.DISALLOWED;
               trident.m_6686_(direction.f_82479_, direction.f_82480_, direction.f_82481_, speed, inaccuracy);
               serverLevel.m_7967_(trident);
            }
         }
      };
      public static final E0 THROW_TRIDENT_HAND_RIGHT = (livingEntityPatch, staticAnimation, object) -> {
         LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
         if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
            ItemStack stack = livingEntity.m_21205_();
            Item weapon = stack.m_41720_();
            if (weapon instanceof BlueDemonTridentItem) {
               if (livingEntity instanceof Player player) {
                  stack.m_41622_(1, player, p -> p.m_21190_(InteractionHand.MAIN_HAND));
               }

               Vec3 jointVec = EpicfightUtil.getJointWithTranslation(
                  livingEntity, new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).handR, 0.0F, 0.0
               );
               if (jointVec == null) {
                  return;
               }

               Vec3 direction = BlueDemonTridentItem.getTridentThrowDirection(livingEntity, jointVec);
               if (direction == null || direction.m_82556_() < 1.0E-7) {
                  return;
               }

               BlueDemonThrownTridentEntity trident = new BlueDemonThrownTridentEntity(serverLevel, livingEntity, stack.m_41777_());
               trident.assignSpawnSequence(livingEntity);
               trident.trimOldGroundedTridentsAroundOwnerOnSpawn();
               trident.m_6034_(jointVec.f_82479_, jointVec.f_82480_, jointVec.f_82481_);
               trident.m_146922_((float)(Mth.m_14136_(direction.f_82479_, direction.f_82481_) * (180.0 / java.lang.Math.PI)));
               trident.m_146926_(
                  (float)(
                     Mth.m_14136_(direction.f_82480_, java.lang.Math.sqrt(direction.f_82479_ * direction.f_82479_ + direction.f_82481_ * direction.f_82481_))
                        * (180.0 / java.lang.Math.PI)
                  )
               );
               float speed = 2.5F;
               float inaccuracy = 1.0F;
               trident.f_36705_ = Pickup.DISALLOWED;
               trident.m_6686_(direction.f_82479_, direction.f_82480_, direction.f_82481_, speed, inaccuracy);
               serverLevel.m_7967_(trident);
            }
         }
      };
      public static final E0 THROW_TRIDENT_HAND_LEFT_LIGHTNING = (livingEntityPatch, staticAnimation, object) -> {
         LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
         if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
            ItemStack stack = livingEntity.m_21206_();
            Item weapon = stack.m_41720_();
            if (weapon instanceof BlueDemonTridentItem) {
               if (livingEntity instanceof Player player) {
                  stack.m_41622_(1, player, p -> p.m_21190_(InteractionHand.OFF_HAND));
               }

               Vec3 jointVec = EpicfightUtil.getJointWithTranslation(
                  livingEntity, new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).handL, 0.0F, 0.0
               );
               if (jointVec == null) {
                  return;
               }

               Vec3 direction = BlueDemonTridentItem.getTridentThrowDirection(livingEntity, jointVec);
               if (direction == null || direction.m_82556_() < 1.0E-7) {
                  return;
               }

               BlueDemonThrownTridentEntity trident = new BlueDemonThrownTridentEntity(serverLevel, livingEntity, stack.m_41777_());
               trident.assignSpawnSequence(livingEntity);
               trident.trimOldGroundedTridentsAroundOwnerOnSpawn();
               trident.setMode(TridentMode.LIGHTNING);
               trident.m_6034_(jointVec.f_82479_, jointVec.f_82480_, jointVec.f_82481_);
               trident.m_146922_((float)(Mth.m_14136_(direction.f_82479_, direction.f_82481_) * (180.0 / java.lang.Math.PI)));
               trident.m_146926_(
                  (float)(
                     Mth.m_14136_(direction.f_82480_, java.lang.Math.sqrt(direction.f_82479_ * direction.f_82479_ + direction.f_82481_ * direction.f_82481_))
                        * (180.0 / java.lang.Math.PI)
                  )
               );
               float speed = 2.5F;
               float inaccuracy = 1.0F;
               trident.f_36705_ = Pickup.DISALLOWED;
               trident.m_6686_(direction.f_82479_, direction.f_82480_, direction.f_82481_, speed, inaccuracy);
               serverLevel.m_7967_(trident);
            }
         }
      };
      public static final E0 THROW_TRIDENT_HAND_RIGHT_LIGHTNING = (livingEntityPatch, staticAnimation, object) -> {
         LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
         if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
            ItemStack stack = livingEntity.m_21205_();
            Item weapon = stack.m_41720_();
            if (weapon instanceof BlueDemonTridentItem) {
               if (livingEntity instanceof Player player) {
                  stack.m_41622_(1, player, p -> p.m_21190_(InteractionHand.MAIN_HAND));
               }

               Vec3 jointVec = EpicfightUtil.getJointWithTranslation(
                  livingEntity, new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).handR, 0.0F, 0.0
               );
               if (jointVec == null) {
                  return;
               }

               Vec3 direction = BlueDemonTridentItem.getTridentThrowDirection(livingEntity, jointVec);
               if (direction == null || direction.m_82556_() < 1.0E-7) {
                  return;
               }

               BlueDemonThrownTridentEntity trident = new BlueDemonThrownTridentEntity(serverLevel, livingEntity, stack.m_41777_());
               trident.assignSpawnSequence(livingEntity);
               trident.trimOldGroundedTridentsAroundOwnerOnSpawn();
               trident.setMode(TridentMode.LIGHTNING);
               trident.m_6034_(jointVec.f_82479_, jointVec.f_82480_, jointVec.f_82481_);
               trident.m_146922_((float)(Mth.m_14136_(direction.f_82479_, direction.f_82481_) * (180.0 / java.lang.Math.PI)));
               trident.m_146926_(
                  (float)(
                     Mth.m_14136_(direction.f_82480_, java.lang.Math.sqrt(direction.f_82479_ * direction.f_82479_ + direction.f_82481_ * direction.f_82481_))
                        * (180.0 / java.lang.Math.PI)
                  )
               );
               float speed = 2.5F;
               float inaccuracy = 1.0F;
               trident.f_36705_ = Pickup.DISALLOWED;
               trident.m_6686_(direction.f_82479_, direction.f_82480_, direction.f_82481_, speed, inaccuracy);
               serverLevel.m_7967_(trident);
            }
         }
      };
      public static final E0 THROW_TRIDENT_HAND_RIGHT_EXPLODE = (livingEntityPatch, staticAnimation, object) -> {
         LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
         if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
            ItemStack stack = livingEntity.m_21205_();
            Item weapon = stack.m_41720_();
            if (weapon instanceof BlueDemonTridentItem) {
               if (livingEntity instanceof Player player) {
                  stack.m_41622_(1, player, p -> p.m_21190_(InteractionHand.MAIN_HAND));
               }

               Vec3 jointVec = EpicfightUtil.getJointWithTranslation(
                  livingEntity, new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).handR, 0.0F, 0.0
               );
               if (jointVec == null) {
                  return;
               }

               Vec3 direction = BlueDemonTridentItem.getTridentThrowDirection(livingEntity, jointVec);
               if (direction == null || direction.m_82556_() < 1.0E-7) {
                  return;
               }

               BlueDemonThrownTridentEntity trident = new BlueDemonThrownTridentEntity(serverLevel, livingEntity, stack.m_41777_());
               trident.assignSpawnSequence(livingEntity);
               trident.trimOldGroundedTridentsAroundOwnerOnSpawn();
               trident.setMode(TridentMode.EXPLOSION);
               trident.m_6034_(jointVec.f_82479_, jointVec.f_82480_, jointVec.f_82481_);
               trident.m_146922_((float)(Mth.m_14136_(direction.f_82479_, direction.f_82481_) * (180.0 / java.lang.Math.PI)));
               trident.m_146926_(
                  (float)(
                     Mth.m_14136_(direction.f_82480_, java.lang.Math.sqrt(direction.f_82479_ * direction.f_82479_ + direction.f_82481_ * direction.f_82481_))
                        * (180.0 / java.lang.Math.PI)
                  )
               );
               float speed = 2.5F;
               float inaccuracy = 1.0F;
               trident.f_36705_ = Pickup.DISALLOWED;
               trident.m_6686_(direction.f_82479_, direction.f_82480_, direction.f_82481_, speed, inaccuracy);
               serverLevel.m_7967_(trident);
            }
         }
      };
      public static final E0 SUMMON_2_OBSIDIAN_LEG_RIGHT = (livingEntityPatch, staticAnimation, object) -> {
         LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
         if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
            Item weapon = livingEntity.m_21205_().m_41720_();
            if (!(weapon instanceof ShadowObsidianWeaponItem) && !(weapon instanceof ObsidianWeaponItem)) {
               if (weapon instanceof ShadowObsidianPillarItem) {
                  HerobrineUtil.summonShadowObsidianShortPillarShootToward(serverLevel, livingEntity, 3, ((HumanoidArmature)Armatures.BIPED.get()).legR);
               }
            } else {
               BlockState obsidian;
               if (weapon instanceof ShadowObsidianWeaponItem) {
                  obsidian = (BlockState)((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get())
                     .m_49966_()
                     .m_61124_(ShadowObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
               } else {
                  obsidian = (BlockState)((Block)AnnoyingVillagersModBlocks.OBSIDIAN_BLOCK.get())
                     .m_49966_()
                     .m_61124_(ObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
               }

               HerobrineUtil.summonObsidianBlocksInfrontOf(serverLevel, livingEntity, obsidian, 2, ((HumanoidArmature)Armatures.BIPED.get()).legR);
            }
         }
      };
      public static final E0 SUMMON_2_OBSIDIAN_LEG_LEFT = (livingEntityPatch, staticAnimation, object) -> {
         LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
         if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
            Item weapon = livingEntity.m_21205_().m_41720_();
            if (!(weapon instanceof ShadowObsidianWeaponItem) && !(weapon instanceof ObsidianWeaponItem)) {
               if (weapon instanceof ShadowObsidianPillarItem) {
                  HerobrineUtil.summonShadowObsidianShortPillarShootToward(serverLevel, livingEntity, 3, ((HumanoidArmature)Armatures.BIPED.get()).legL);
               }
            } else {
               BlockState obsidian;
               if (weapon instanceof ShadowObsidianWeaponItem) {
                  obsidian = (BlockState)((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get())
                     .m_49966_()
                     .m_61124_(ShadowObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
               } else {
                  obsidian = (BlockState)((Block)AnnoyingVillagersModBlocks.OBSIDIAN_BLOCK.get())
                     .m_49966_()
                     .m_61124_(ObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
               }

               HerobrineUtil.summonObsidianBlocksInfrontOf(serverLevel, livingEntity, obsidian, 2, ((HumanoidArmature)Armatures.BIPED.get()).legL);
            }
         }
      };
      public static final E0 SUMMON_2_OBSIDIAN_HAND_RIGHT = (livingEntityPatch, staticAnimation, object) -> {
         LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
         if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
            Item weapon = livingEntity.m_21205_().m_41720_();
            if (!(weapon instanceof ShadowObsidianWeaponItem) && !(weapon instanceof ObsidianWeaponItem)) {
               if (weapon instanceof ShadowObsidianPillarItem) {
                  HerobrineUtil.summonShadowObsidianShortPillarShootToward(serverLevel, livingEntity, 3, ((HumanoidArmature)Armatures.BIPED.get()).toolR);
               } else if (livingEntity.m_21206_().m_41720_() instanceof ShadowObsidianSwordItem) {
                  HerobrineUtil.summonShadowObsidianMiddlePillarShootToward(serverLevel, livingEntity, 3, ((HumanoidArmature)Armatures.BIPED.get()).toolR);
               }
            } else {
               BlockState obsidian;
               if (weapon instanceof ShadowObsidianWeaponItem) {
                  obsidian = (BlockState)((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get())
                     .m_49966_()
                     .m_61124_(ShadowObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
               } else {
                  obsidian = (BlockState)((Block)AnnoyingVillagersModBlocks.OBSIDIAN_BLOCK.get())
                     .m_49966_()
                     .m_61124_(ObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
               }

               HerobrineUtil.summonObsidianBlocksInfrontOf(serverLevel, livingEntity, obsidian, 2, ((HumanoidArmature)Armatures.BIPED.get()).toolR);
            }
         }
      };
      public static final E0 SUMMON_2_OBSIDIAN_HAND_LEFT = (livingEntityPatch, staticAnimation, object) -> {
         LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
         if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
            Item weapon = livingEntity.m_21205_().m_41720_();
            if (!(weapon instanceof ShadowObsidianWeaponItem) && !(weapon instanceof ObsidianWeaponItem)) {
               if (livingEntity.m_21206_().m_41720_() instanceof ShadowObsidianSwordItem) {
                  HerobrineUtil.summonShadowObsidianMiddlePillarShootToward(serverLevel, livingEntity, 3, ((HumanoidArmature)Armatures.BIPED.get()).toolL);
               } else if (weapon instanceof ShadowObsidianPillarItem) {
                  HerobrineUtil.summonShadowObsidianShortPillarShootToward(serverLevel, livingEntity, 3, ((HumanoidArmature)Armatures.BIPED.get()).toolL);
               }
            } else {
               BlockState obsidian;
               if (weapon instanceof ShadowObsidianWeaponItem) {
                  obsidian = (BlockState)((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get())
                     .m_49966_()
                     .m_61124_(ShadowObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
               } else {
                  obsidian = (BlockState)((Block)AnnoyingVillagersModBlocks.OBSIDIAN_BLOCK.get())
                     .m_49966_()
                     .m_61124_(ObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
               }

               HerobrineUtil.summonObsidianBlocksInfrontOf(serverLevel, livingEntity, obsidian, 2, ((HumanoidArmature)Armatures.BIPED.get()).toolL);
            }
         }
      };
      public static final E0 SUMMON_3_OBSIDIAN_HAND_LEFT = (livingEntityPatch, staticAnimation, object) -> {
         LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
         if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
            Item weapon = livingEntity.m_21205_().m_41720_();
            if (!(weapon instanceof ShadowObsidianWeaponItem) && !(weapon instanceof ObsidianWeaponItem)) {
               if (livingEntity.m_21206_().m_41720_() instanceof ShadowObsidianSwordItem) {
                  HerobrineUtil.summonShadowObsidianMiddlePillarShootToward(serverLevel, livingEntity, 4, ((HumanoidArmature)Armatures.BIPED.get()).toolL);
               } else if (weapon instanceof ShadowObsidianPillarItem) {
                  HerobrineUtil.summonShadowObsidianShortPillarShootToward(serverLevel, livingEntity, 4, ((HumanoidArmature)Armatures.BIPED.get()).toolL);
               }
            } else {
               BlockState obsidian;
               if (weapon instanceof ShadowObsidianWeaponItem) {
                  obsidian = (BlockState)((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get())
                     .m_49966_()
                     .m_61124_(ShadowObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
               } else {
                  obsidian = (BlockState)((Block)AnnoyingVillagersModBlocks.OBSIDIAN_BLOCK.get())
                     .m_49966_()
                     .m_61124_(ObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
               }

               HerobrineUtil.summonObsidianBlocksInfrontOf(serverLevel, livingEntity, obsidian, 3, ((HumanoidArmature)Armatures.BIPED.get()).toolL);
            }
         }
      };
      public static final E0 SUMMON_6_OBSIDIAN_HAND_LEFT = (livingEntityPatch, staticAnimation, object) -> {
         LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
         if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
            Item weapon = livingEntity.m_21205_().m_41720_();
            if (!(weapon instanceof ShadowObsidianWeaponItem) && !(weapon instanceof ObsidianWeaponItem)) {
               if (livingEntity.m_21206_().m_41720_() instanceof ShadowObsidianSwordItem) {
                  HerobrineUtil.summonShadowObsidianMiddlePillarShootToward(serverLevel, livingEntity, 7, ((HumanoidArmature)Armatures.BIPED.get()).toolL);
               } else if (weapon instanceof ShadowObsidianPillarItem) {
                  HerobrineUtil.summonShadowObsidianShortPillarShootToward(serverLevel, livingEntity, 7, ((HumanoidArmature)Armatures.BIPED.get()).toolL);
               }
            } else {
               BlockState obsidian;
               if (weapon instanceof ShadowObsidianWeaponItem) {
                  obsidian = (BlockState)((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get())
                     .m_49966_()
                     .m_61124_(ShadowObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
               } else {
                  obsidian = (BlockState)((Block)AnnoyingVillagersModBlocks.OBSIDIAN_BLOCK.get())
                     .m_49966_()
                     .m_61124_(ObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
               }

               HerobrineUtil.summonObsidianBlocksInfrontOf(serverLevel, livingEntity, obsidian, 6, ((HumanoidArmature)Armatures.BIPED.get()).toolL);
            }
         }
      };
      public static final E0 SUMMON_6_OBSIDIAN_HAND_RIGHT = (livingEntityPatch, staticAnimation, object) -> {
         LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
         if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
            Item weapon = livingEntity.m_21205_().m_41720_();
            if (!(weapon instanceof ShadowObsidianWeaponItem) && !(weapon instanceof ObsidianWeaponItem)) {
               if (weapon instanceof ShadowObsidianPillarItem) {
                  HerobrineUtil.summonShadowObsidianShortPillarShootToward(serverLevel, livingEntity, 7, ((HumanoidArmature)Armatures.BIPED.get()).toolR);
               } else if (livingEntity.m_21206_().m_41720_() instanceof ShadowObsidianSwordItem) {
                  HerobrineUtil.summonShadowObsidianMiddlePillarShootToward(serverLevel, livingEntity, 7, ((HumanoidArmature)Armatures.BIPED.get()).toolR);
               }
            } else {
               BlockState obsidian;
               if (weapon instanceof ShadowObsidianWeaponItem) {
                  obsidian = (BlockState)((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get())
                     .m_49966_()
                     .m_61124_(ShadowObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
               } else {
                  obsidian = (BlockState)((Block)AnnoyingVillagersModBlocks.OBSIDIAN_BLOCK.get())
                     .m_49966_()
                     .m_61124_(ObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
               }

               HerobrineUtil.summonObsidianBlocksInfrontOf(serverLevel, livingEntity, obsidian, 6, ((HumanoidArmature)Armatures.BIPED.get()).toolR);
            }
         }
      };
      public static final E0 SUMMON_6_OBSIDIAN_LEG_RIGHT = (livingEntityPatch, staticAnimation, object) -> {
         LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
         if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
            Item weapon = livingEntity.m_21205_().m_41720_();
            if (!(weapon instanceof ShadowObsidianWeaponItem) && !(weapon instanceof ObsidianWeaponItem)) {
               if (weapon instanceof ShadowObsidianPillarItem) {
                  HerobrineUtil.summonShadowObsidianShortPillarShootToward(serverLevel, livingEntity, 7, ((HumanoidArmature)Armatures.BIPED.get()).legR);
               }
            } else {
               BlockState obsidian;
               if (weapon instanceof ShadowObsidianWeaponItem) {
                  obsidian = (BlockState)((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get())
                     .m_49966_()
                     .m_61124_(ShadowObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
               } else {
                  obsidian = (BlockState)((Block)AnnoyingVillagersModBlocks.OBSIDIAN_BLOCK.get())
                     .m_49966_()
                     .m_61124_(ObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
               }

               HerobrineUtil.summonObsidianBlocksInfrontOf(serverLevel, livingEntity, obsidian, 6, ((HumanoidArmature)Armatures.BIPED.get()).legR);
            }
         }
      };
      public static final E0 SUMMON_OBSIDIAN_PILLAR = (livingEntityPatch, staticAnimation, object) -> {
         LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
         if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
            Item weapon = livingEntity.m_21205_().m_41720_();
            if (!(weapon instanceof ShadowObsidianWeaponItem) && !(weapon instanceof ObsidianWeaponItem)) {
               if (weapon instanceof ShadowObsidianPillarItem) {
                  HerobrineUtil.summonShadowObsidianLongPillarShootToward(serverLevel, livingEntity);
               }
            } else {
               BlockState obsidian;
               if (weapon instanceof ShadowObsidianWeaponItem) {
                  obsidian = (BlockState)((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get())
                     .m_49966_()
                     .m_61124_(ShadowObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
               } else {
                  obsidian = (BlockState)((Block)AnnoyingVillagersModBlocks.OBSIDIAN_BLOCK.get())
                     .m_49966_()
                     .m_61124_(ObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
               }

               HerobrineUtil.summonObsidianPillar(serverLevel, livingEntity, obsidian);
            }
         }
      };
      public static final E0 SUMMON_OBSIDIAN_WALL = (livingEntityPatch, staticAnimation, object) -> {
         LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
         if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
            Item weapon = livingEntity.m_21205_().m_41720_();
            if (!(weapon instanceof ShadowObsidianWeaponItem) && !(weapon instanceof ObsidianWeaponItem)) {
               if (weapon instanceof ShadowObsidianPillarItem || weapon instanceof ShadowObsidianSwordItem) {
                  HerobrineUtil.summonShadowObsidianLongPillarDefense(serverLevel, livingEntity);
               }
            } else {
               BlockState obsidian;
               if (weapon instanceof ShadowObsidianWeaponItem) {
                  obsidian = (BlockState)((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get())
                     .m_49966_()
                     .m_61124_(ShadowObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
               } else {
                  obsidian = (BlockState)((Block)AnnoyingVillagersModBlocks.OBSIDIAN_BLOCK.get())
                     .m_49966_()
                     .m_61124_(ObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
               }

               HerobrineUtil.summonObsidianWall(serverLevel, livingEntity, obsidian);
            }
         }
      };
      public static final E0 SUMMON_OBSIDIAN_CIRCLE = (livingEntityPatch, staticAnimation, object) -> {
         LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
         if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
            HerobrineUtil.summonShadowObsidianLongPillarCircle(serverLevel, livingEntity, livingEntity.m_20097_());
            if (livingEntity.m_21205_().m_41720_() instanceof ShadowObsidianPillarItem) {
               HerobrineUtil.summonShadowObsidianLongPillarShootToward(serverLevel, livingEntity);
            }
         }
      };
      public static final E0 SUMMON_OBSIDIAN_CROSS = (livingEntityPatch, staticAnimation, object) -> {
         LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
         if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
            Item weapon = livingEntity.m_21205_().m_41720_();
            if (!(weapon instanceof ShadowObsidianWeaponItem) && !(weapon instanceof ObsidianWeaponItem)) {
               if (weapon instanceof ShadowObsidianPillarItem && livingEntity instanceof Player) {
                  HerobrineUtil.summonShadowObsidianLongPillarDefenseWide(serverLevel, livingEntity);
               }
            } else {
               BlockState obsidian;
               if (weapon instanceof ShadowObsidianWeaponItem) {
                  obsidian = (BlockState)((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get())
                     .m_49966_()
                     .m_61124_(ShadowObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
               } else {
                  obsidian = (BlockState)((Block)AnnoyingVillagersModBlocks.OBSIDIAN_BLOCK.get())
                     .m_49966_()
                     .m_61124_(ObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
               }

               HerobrineUtil.summonObsidianCross(serverLevel, livingEntity, obsidian);
            }
         }
      };
      public static final E0 SUMMON_OBSIDIAN_CROSS_FIX_DELAY_SHADOW_HEROBRINE = (livingEntityPatch, staticAnimation, object) -> {
         LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
         Item weapon = livingEntity.m_21205_().m_41720_();
         if (livingEntity.m_9236_() instanceof ServerLevel serverLevel && !(livingEntity instanceof Player) && weapon instanceof ShadowObsidianPillarItem) {
            HerobrineUtil.summonShadowObsidianLongPillarDefenseWide(serverLevel, livingEntity);
         }
      };
      public static final E0 SUMMON_OBSIDIAN_SMALL_CROSS = (livingEntityPatch, staticAnimation, object) -> {
         LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
         if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
            Item weapon = livingEntity.m_21205_().m_41720_();
            BlockState obsidian;
            if (weapon instanceof ShadowObsidianWeaponItem) {
               obsidian = (BlockState)((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get())
                  .m_49966_()
                  .m_61124_(ShadowObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
            } else if (weapon instanceof ShadowObsidianPillarItem) {
               obsidian = (BlockState)((BlockState)((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_LONG_PILLAR.get())
                     .m_49966_()
                     .m_61124_(ObsidianBlock.FROM_PLAYER, livingEntity instanceof Player))
                  .m_61124_(BlockStateProperties.f_61374_, livingEntity.m_6350_());
            } else if (weapon instanceof ShadowObsidianSwordItem || livingEntity.m_21206_().m_41720_() instanceof ShadowObsidianSwordItem) {
               obsidian = shadowObsidianMiddlePillar(livingEntity);
            } else if (isShadowObsidianMob(livingEntity)) {
               obsidian = shadowObsidianBlock(livingEntity);
            } else {
               obsidian = obsidianBlock(livingEntity);
            }

            HerobrineUtil.summonObsidianSmallCross(serverLevel, livingEntity, obsidian);
         }
      };
      public static final E0 THROW_OBSIDIAN = (livingEntityPatch, staticAnimation, object) -> {
         LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
         if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
            Item weapon = livingEntity.m_21205_().m_41720_();
            BlockState obsidian;
            if (weapon instanceof ShadowObsidianWeaponItem) {
               obsidian = (BlockState)((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get())
                  .m_49966_()
                  .m_61124_(ShadowObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
            } else if (weapon instanceof ShadowObsidianSwordItem) {
               obsidian = (BlockState)((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_MIDDLE_PILLAR.get())
                  .m_49966_()
                  .m_61124_(ObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
            } else if (isShadowObsidianMob(livingEntity)) {
               obsidian = shadowObsidianBlock(livingEntity);
            } else {
               obsidian = obsidianBlock(livingEntity);
            }

            LivingEntity attacker = (LivingEntity)livingEntityPatch.getOriginal();
            Vec3 to = attacker.m_146892_().m_82549_(attacker.m_20154_().m_82490_(16.0));
            if (attacker instanceof Mob mob && mob.m_5448_() != null) {
               to = mob.m_5448_().m_20299_(1.0F);
            }

            BlockProjectileEntity throwingObsidian = new BlockProjectileEntity(attacker.m_9236_(), attacker, obsidian);
            serverLevel.m_7967_(throwingObsidian);
            Vec3 dir = to.m_82546_(throwingObsidian.m_20182_());
            if (dir.m_82556_() < 1.0E-6) {
               dir = attacker.m_20154_();
            }

            Vec3 vel = dir.m_82541_().m_82490_(2.0);
            throwingObsidian.m_20256_(vel);
         }
      };
      public static final E0 THROW_OBSIDIAN_OFFHAND = (livingEntityPatch, staticAnimation, object) -> {
         LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
         if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
            Vec3 jointVec = EpicfightUtil.getJointWithTranslation(
               livingEntity, new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolL, 2.0F, 0.0
            );
            Item weapon = livingEntity.m_21205_().m_41720_();
            BlockState obsidian;
            if (weapon instanceof ShadowObsidianWeaponItem) {
               obsidian = (BlockState)((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get())
                  .m_49966_()
                  .m_61124_(ShadowObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
            } else if (weapon instanceof ShadowObsidianSwordItem) {
               obsidian = (BlockState)((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_MIDDLE_PILLAR.get())
                  .m_49966_()
                  .m_61124_(ObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
            } else if (isShadowObsidianMob(livingEntity)) {
               obsidian = shadowObsidianBlock(livingEntity);
            } else {
               obsidian = obsidianBlock(livingEntity);
            }

            LivingEntity attacker = (LivingEntity)livingEntityPatch.getOriginal();
            Vec3 to = attacker.m_146892_().m_82549_(attacker.m_20154_().m_82490_(16.0));
            if (attacker instanceof Mob mob && mob.m_5448_() != null) {
               to = mob.m_5448_().m_20299_(1.0F);
            }

            BlockProjectileEntity throwingObsidian = new BlockProjectileEntity(attacker.m_9236_(), attacker, obsidian);
            serverLevel.m_7967_(throwingObsidian);
            if (jointVec != null) {
               throwingObsidian.m_20219_(jointVec);
            }

            Vec3 dir = to.m_82546_(throwingObsidian.m_20182_());
            if (dir.m_82556_() < 1.0E-6) {
               dir = attacker.m_20154_();
            }

            Vec3 vel = dir.m_82541_().m_82490_(2.0);
            throwingObsidian.m_20256_(vel);
         }
      };
      public static final E0 SLEDGEHAMMER_SHOOT = (livingEntityPatch, staticAnimation, object) -> {
         if (((LivingEntity)livingEntityPatch.getOriginal()).m_9236_() instanceof ServerLevel serverLevel) {
            Vec3 aimPosition;
            LivingEntity shooterEntity;
            label27: {
               shooterEntity = (LivingEntity)livingEntityPatch.getOriginal();
               aimPosition = null;
               if (shooterEntity instanceof Mob mob && mob.m_5448_() != null) {
                  aimPosition = mob.m_5448_().m_20299_(1.0F);
                  Vec3 portalAimPosition = HerobrinePortalCombatUtil.getProjectilePortalAim(shooterEntity, mob.m_5448_());
                  if (portalAimPosition != null) {
                     aimPosition = portalAimPosition;
                  }
                  break label27;
               }

               if (shooterEntity instanceof Player player) {
                  Vec3 playerEyePosition = player.m_20299_(1.0F);
                  Vec3 playerLookDirection = player.m_20154_();
                  double aimDistance = 64.0;
                  aimPosition = playerEyePosition.m_82549_(playerLookDirection.m_82490_(aimDistance));
               }
            }

            ObsidianSledgehammerProjectileEntity obsidianSledgehammerProjectileEntity = new ObsidianSledgehammerProjectileEntity(
               (EntityType<ObsidianSledgehammerProjectileEntity>)AnnoyingVillagersModEntities.OBSIDIAN_SLEDGEHAMMER_PROJECTILE.get(), serverLevel
            );
            Vec3 hammerPos = EpicfightUtil.getJointWithTranslation(
               livingEntityPatch.getOriginal(), new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolR, 1.0F, 0.0
            );
            if (hammerPos != null && aimPosition != null) {
               obsidianSledgehammerProjectileEntity.m_7678_(hammerPos.f_82479_, hammerPos.f_82480_, hammerPos.f_82481_, 0.0F, 0.0F);
               obsidianSledgehammerProjectileEntity.setPosToAim(new Vec3(aimPosition.f_82479_, aimPosition.f_82480_, aimPosition.f_82481_));
               obsidianSledgehammerProjectileEntity.m_20331_(true);
               obsidianSledgehammerProjectileEntity.m_5496_((SoundEvent)AnnoyingVillagersModSounds.METAL_HIT.get(), 1.0F, 1.0F);
               obsidianSledgehammerProjectileEntity.setOwner(shooterEntity);
               if (staticAnimation == AnimsWom.SLEDGEHAMMER_SOLAR_AUTO_3) {
                  obsidianSledgehammerProjectileEntity.setShouldStun(true);
               }

               serverLevel.m_7967_(obsidianSledgehammerProjectileEntity);
            }
         }
      };
      public static final E0 SHOCK_WAVE = (livingEntityPatch, staticAnimation, object) -> {
         Vec3 legendarySwordPos = EpicfightUtil.getJointWithTranslation(
            livingEntityPatch.getOriginal(), new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolR, 1.5F, 0.0
         );
         int MAX_SHOCKWAVE_RADIUS = 6;
         int TICKS_BETWEEN_LAYERS = 2;

         for (final int radius = 1; radius <= 6; radius++) {
            int delayTicks = (radius - 1) * 2;
            if (legendarySwordPos == null) {
               return;
            }

            final BlockPos finalVec = BlockPos.m_274446_(legendarySwordPos);
            new DelayedTask(delayTicks) {
               @Override
               public void run() {
                  LegendarySwordItem.spawnCircleRing(
                     (ServerLevel)((LivingEntity)livingEntityPatch.getOriginal()).m_9236_(), finalVec, radius, (LivingEntity)livingEntityPatch.getOriginal()
                  );
               }
            };
         }
      };
      public static final E0 END_ATTACK = (livingentitypatch, staticAnimation, object) -> {
         if (livingentitypatch instanceof PlayerPatch) {
            livingentitypatch.playAnimationSynchronized(AnimsPugilistSteve.DUAL_END, 0.1F);
         }
      };

      private static boolean isShadowObsidianMob(LivingEntity livingEntity) {
         return livingEntity instanceof ShadowHerobrineEntity
            || livingEntity instanceof ShadowHerobrineCloneEntity
            || livingEntity instanceof LowShadowHerobrineCloneEntity
            || livingEntity instanceof Herobrine7Entity
            || livingEntity instanceof ArmoredHerobrineEntity;
      }

      private static BlockState shadowObsidianBlock(LivingEntity livingEntity) {
         return (BlockState)((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get())
            .m_49966_()
            .m_61124_(ShadowObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
      }

      private static BlockState shadowObsidianMiddlePillar(LivingEntity livingEntity) {
         return (BlockState)((BlockState)((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_MIDDLE_PILLAR.get())
               .m_49966_()
               .m_61124_(ObsidianBlock.FROM_PLAYER, livingEntity instanceof Player))
            .m_61124_(BlockStateProperties.f_61374_, livingEntity.m_6350_());
      }

      private static BlockState obsidianBlock(LivingEntity livingEntity) {
         return (BlockState)((Block)AnnoyingVillagersModBlocks.OBSIDIAN_BLOCK.get())
            .m_49966_()
            .m_61124_(ObsidianBlock.FROM_PLAYER, livingEntity instanceof Player);
      }
   }
}
