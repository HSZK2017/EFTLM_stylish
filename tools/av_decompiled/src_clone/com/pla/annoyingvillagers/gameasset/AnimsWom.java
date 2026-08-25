package com.pla.annoyingvillagers.gameasset;

import com.pla.annoyingvillagers.AnnoyingVillagers;
import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.entity.NullEntity;
import com.pla.annoyingvillagers.entity.NullSkeletonEntity;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModParticleTypes;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.item.BlueDemonChestplateItem;
import com.pla.annoyingvillagers.item.BlueDemonTridentItem;
import com.pla.annoyingvillagers.item.EarthAxeItem;
import com.pla.annoyingvillagers.network.ClientboundGlaiveExplosionFx;
import com.pla.annoyingvillagers.network.ClientboundMuteExplosionAtPos;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import reascer.wom.animation.WomAnimationProperty;
import reascer.wom.animation.attacks.AntitheusShootAttackAnimation;
import reascer.wom.animation.attacks.BasicMultipleAttackAnimation;
import reascer.wom.animation.attacks.SpecialAttackAnimation;
import reascer.wom.gameasset.ReuseableEvents;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.gameasset.WOMSounds;
import reascer.wom.gameasset.colliders.WOMWeaponColliders;
import reascer.wom.particle.WOMParticles;
import reascer.wom.world.damagesources.WOMDamageType;
import reascer.wom.world.damagesources.WOMExtraDamageInstance;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InPeriodEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.PlaybackSpeedModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.property.MoveCoordFunctions.MoveCoordSetter;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DodgeAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.MovementAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.gameasset.Animations.ReusableSources;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.StunType;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.MOD
)
public class AnimsWom {
   public static AnimationAccessor<ActionAnimation> CUT_ANTITHEUS_ASCENSION;
   public static AnimationAccessor<MovementAnimation> TORMENT_BERSERK_WALK;
   public static AnimationAccessor<StaticAnimation> TRIDENT_GUARD_HIT_1;
   public static AnimationAccessor<StaticAnimation> TRIDENT_GUARD_HIT_2;
   public static AnimationAccessor<ActionAnimation> ELECTRIC_FIELD;
   public static AnimationAccessor<AttackAnimation> EARTH_AXE;
   public static AnimationAccessor<StaticAnimation> GLOWING_AGONY_GUARD;
   public static AnimationAccessor<BasicMultipleAttackAnimation> ENDER_AEGIS_BULL_CHARGE;
   public static AnimationAccessor<BasicMultipleAttackAnimation> ENDER_AEGIS_MOONLESS_AUTO_1;
   public static AnimationAccessor<BasicMultipleAttackAnimation> ENDER_AEGIS_MOONLESS_AUTO_2;
   public static AnimationAccessor<BasicMultipleAttackAnimation> YELLOW_SOLAR_AUTO_2;
   public static AnimationAccessor<BasicMultipleAttackAnimation> YELLOW_NAPOLEON_AUTO_3;
   public static AnimationAccessor<SpecialAttackAnimation> YELLOW_NAPOLEON_AUSTERLITZ_SHOOT;
   public static AnimationAccessor<SpecialAttackAnimation> ENDER_AEGIS_NAPOLEON_RELOAD_1;
   public static AnimationAccessor<BasicMultipleAttackAnimation> ENDER_GLAIVE_NAPOLEON_AUTO_1;
   public static AnimationAccessor<BasicMultipleAttackAnimation> ENDER_GLAIVE_NAPOLEON_AUTO_2;
   public static AnimationAccessor<BasicMultipleAttackAnimation> ENDER_GLAIVE_NAPOLEON_AUTO_4;
   public static AnimationAccessor<BasicMultipleAttackAnimation> ENDER_GLAIVE_NAPOLEON_AUSTERLITZ;
   public static AnimationAccessor<BasicMultipleAttackAnimation> DEMONIAC_RUINE_AUTO_1;
   public static AnimationAccessor<BasicMultipleAttackAnimation> DEMONIAC_RUINE_AUTO_2;
   public static AnimationAccessor<BasicMultipleAttackAnimation> DEMONIAC_RUINE_AUTO_4;
   public static AnimationAccessor<BasicMultipleAttackAnimation> DEMONIAC_TORMENT_CHARGED_ATTACK_2;
   public static AnimationAccessor<BasicMultipleAttackAnimation> DEMONIAC_RUINE_COMET;
   public static AnimationAccessor<ActionAnimation> AGONY_GUARD_HIT_1;
   public static AnimationAccessor<SpecialAttackAnimation> ENDER_GLAIVE_NAPOLEON_SHOOT_3;
   public static AnimationAccessor<BasicMultipleAttackAnimation> ENDER_GLAIVE_AGONY_AUTO_1;
   public static AnimationAccessor<StaticAnimation> CLONE_ANTITHEUS_IDLE;
   public static AnimationAccessor<BasicMultipleAttackAnimation> CLONE_ANTITHEUS_AUTO_1;
   public static AnimationAccessor<BasicMultipleAttackAnimation> CLONE_ANTITHEUS_AUTO_2;
   public static AnimationAccessor<BasicMultipleAttackAnimation> CLONE_ANTITHEUS_AUTO_3;
   public static AnimationAccessor<BasicMultipleAttackAnimation> CLONE_ANTITHEUS_AUTO_4;
   public static AnimationAccessor<BasicMultipleAttackAnimation> CLONE_ANTITHEUS_AGRESSION;
   public static AnimationAccessor<BasicMultipleAttackAnimation> CLONE_ANTITHEUS_GUILLOTINE;
   public static AnimationAccessor<AttackAnimation> CLONE_ANTITHEUS_ASCENSION;
   public static AnimationAccessor<AttackAnimation> CLONE_ANTITHEUS_LAPSE;
   public static AnimationAccessor<BasicMultipleAttackAnimation> CLONE_ANTITHEUS_ASCENDED_DEATHFALL;
   public static AnimationAccessor<BasicMultipleAttackAnimation> CLONE_ANTITHEUS_ASCENDED_BLINK;
   public static AnimationAccessor<BasicMultipleAttackAnimation> CLONE_ANTITHEUS_ASCENDED_BLACKHOLE;
   public static AnimationAccessor<BasicMultipleAttackAnimation> ENDER_GLAIVE_NAPOLEON_AUTO_3;
   public static AnimationAccessor<BasicMultipleAttackAnimation> ENDER_GLAIVE_NAPOLEON_WATERLOW;
   public static AnimationAccessor<BasicMultipleAttackAnimation> CLONE_ENDERBLASTER_TWOHAND_TOMAHAWK;
   public static AnimationAccessor<BasicMultipleAttackAnimation> YELLOW_TORMENT_CHARGED_ATTACK_3;
   public static AnimationAccessor<BasicMultipleAttackAnimation> CLONE_ENDERBLASTER_ONEHAND_DASH;
   public static AnimationAccessor<BasicMultipleAttackAnimation> SLEDGEHAMMER_TORMENT_BERSERK_AUTO_1;
   public static AnimationAccessor<BasicMultipleAttackAnimation> SLEDGEHAMMER_TORMENT_BERSERK_AUTO_2;
   public static AnimationAccessor<BasicMultipleAttackAnimation> SLEDGEHAMMER_SOLAR_AUTO_3;
   public static AnimationAccessor<AntitheusShootAttackAnimation> CLONE_ANTITHEUS_SHOOT;
   public static AnimationAccessor<StaticAnimation> CLONE_ANTITHEUS_ASCENDED_IDLE;
   public static AnimationAccessor<AttackAnimation> NULL_SKELETON_ANTITHEUS_ASCENSION;
   public static AnimationAccessor<BasicMultipleAttackAnimation> NULL_ANTITHEUS_ASCENDED_AUTO_1;
   public static AnimationAccessor<BasicMultipleAttackAnimation> NULL_ANTITHEUS_ASCENDED_AUTO_2;
   public static AnimationAccessor<BasicMultipleAttackAnimation> NULL_ANTITHEUS_ASCENDED_AUTO_3;
   public static AnimationAccessor<DodgeAnimation> HEROBRINE_MOB_ENDERSTEP_OBSCURIS;
   public static AnimationAccessor<BasicMultipleAttackAnimation> OBSIDIAN_ANTITHEUS_ASCENDED_DEATHFALL;
   public static AnimationAccessor<MovementAnimation> OLD_MOONLESS_RUN;
   public static AnimationAccessor<MovementAnimation> TRIDENT_TWO_HAND_RUN;
   public static AnimationAccessor<BasicMultipleAttackAnimation> OBSIDIAN_STRONG_PUNCH;
   public static AnimationAccessor<BasicMultipleAttackAnimation> OBSIDIAN_ENDERBLASTER_TWOHAND_TISHNAW;
   public static AnimationAccessor<BasicMultipleAttackAnimation> SHADOW_OBSIDIAN_SWORD_TORMENT_AIRSLAM;
   public static AnimationAccessor<BasicMultipleAttackAnimation> SHADOW_OBSIDIAN_SWORD_TORMENT_BERSERK_DASH;
   public static AnimationAccessor<BasicMultipleAttackAnimation> SHADOW_OBSIDIAN_SWORD_GESETZ_AUTO_3;
   public static AnimationAccessor<BasicMultipleAttackAnimation> SHADOW_OBSIDIAN_SWORD_GESETZ_AUTO_2;
   public static AnimationAccessor<SpecialAttackAnimation> CLONE_NAPOLEON_WATERLOW_SHOOT;
   public static AnimationAccessor<StaticAnimation> CUT_ENDERBLASTER_TWOHAND_RELOAD;
   public static AnimationAccessor<BasicMultipleAttackAnimation> HACKER_SWORD_SKILL;
   public static AnimationAccessor<BasicMultipleAttackAnimation> WARBLADE_SATSUJIN_TSUKUYOMI;
   public static AnimationAccessor<BasicMultipleAttackAnimation> HOOK_HERRSCHER_UP;

   public static void build(AnimationBuilder builder) {
      ArmatureAccessor<HumanoidArmature> humanoidArmature = Armatures.BIPED;
      CUT_ANTITHEUS_ASCENSION = builder.nextAccessor(
         "biped/wom_clone/cut_antitheus_ascension",
         accessor -> (ActionAnimation)new ActionAnimation(0.05F, Float.MAX_VALUE, accessor, humanoidArmature)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.5F, (livingEntityPatch, self, p) -> {
                  LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
                  if (livingEntity.m_9236_() instanceof ServerLevel && livingEntity.m_6844_(EquipmentSlot.CHEST).m_41720_() instanceof BlueDemonChestplateItem) {
                     BlueDemonChestplateItem.activateBuff(livingEntity.m_6844_(EquipmentSlot.CHEST));
                  }
               }, Side.SERVER)})
      );
      TORMENT_BERSERK_WALK = builder.nextAccessor(
         "biped/wom_clone/torment_berserk_walk", accessor -> new MovementAnimation(0.1F, true, accessor, humanoidArmature)
      );
      TRIDENT_GUARD_HIT_1 = builder.nextAccessor(
         "biped/wom_clone/trident_guard_hit1",
         accessor -> new StaticAnimation(false, accessor, humanoidArmature)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.1F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
                     InTimeEvent.create(0.2F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
                     InTimeEvent.create(0.3F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
                     InTimeEvent.create(0.4F, ReuseableEvents.FAST_SPINING, Side.CLIENT)
                  }
               )
      );
      TRIDENT_GUARD_HIT_2 = builder.nextAccessor(
         "biped/wom_clone/trident_guard_hit2",
         accessor -> new StaticAnimation(false, accessor, humanoidArmature)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.1F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
                     InTimeEvent.create(0.2F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
                     InTimeEvent.create(0.3F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
                     InTimeEvent.create(0.4F, ReuseableEvents.FAST_SPINING, Side.CLIENT)
                  }
               )
      );
      ELECTRIC_FIELD = builder.nextAccessor(
         "biped/wom_clone/electric_field",
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
                           BlueDemonTridentItem.spawnDamageZones(serverLevel, (LivingEntity)livingEntityPatch.getOriginal());
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
                     InTimeEvent.create(3.8F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER)
                  }
               )
      );
      EARTH_AXE = builder.nextAccessor(
         "biped/wom_clone/earth_axe",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.0F, 0.0F, 0.0F, 0.0F, Float.MAX_VALUE, null, ((HumanoidArmature)Armatures.BIPED.get()).head, accessor, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.2F, (livingEntityPatch, self, p) -> {
                  if (((LivingEntity)livingEntityPatch.getOriginal()).m_9236_() instanceof ServerLevel serverLevel) {
                     EarthAxeItem.summonEarthWall(serverLevel, (LivingEntity)livingEntityPatch.getOriginal());
                  }
               }, Side.SERVER)})
      );
      GLOWING_AGONY_GUARD = builder.nextAccessor(
         "biped/wom_clone/glowing_agony_guard",
         accessor -> new StaticAnimation(0.05F, true, accessor, humanoidArmature)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.0F, ReuseableEvents.FAST_SPINING_AGONY, Side.CLIENT),
                     InTimeEvent.create(0.1F, ReuseableEvents.FAST_SPINING_AGONY, Side.CLIENT),
                     InTimeEvent.create(0.2F, ReuseableEvents.FAST_SPINING_AGONY, Side.CLIENT),
                     InTimeEvent.create(0.3F, ReuseableEvents.FAST_SPINING_AGONY, Side.CLIENT),
                     InTimeEvent.create(0.4F, ReuseableEvents.FAST_SPINING_AGONY, Side.CLIENT),
                     InTimeEvent.create(0.5F, ReuseableEvents.FAST_SPINING_AGONY, Side.CLIENT),
                     InTimeEvent.create(0.6F, ReuseableEvents.FAST_SPINING_AGONY, Side.CLIENT),
                     InTimeEvent.create(0.7F, ReuseableEvents.FAST_SPINING_AGONY, Side.CLIENT)
                  }
               )
      );
      ENDER_AEGIS_BULL_CHARGE = builder.nextAccessor(
         "biped/wom_clone/ender_aegis_bull_charge",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.2F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.2F, 0.25F, 0.29F, 0.29F, ((HumanoidArmature)humanoidArmature.get()).rootJoint, WOMWeaponColliders.SHOULDER_BUMP),
                     new Phase(0.29F, 0.3F, 0.35F, 0.39F, 0.39F, ((HumanoidArmature)humanoidArmature.get()).rootJoint, WOMWeaponColliders.SHOULDER_BUMP),
                     new Phase(0.39F, 0.4F, 0.45F, 0.49F, 0.49F, ((HumanoidArmature)humanoidArmature.get()).rootJoint, WOMWeaponColliders.SHOULDER_BUMP),
                     new Phase(0.49F, 0.5F, 0.55F, 0.59F, 0.59F, ((HumanoidArmature)humanoidArmature.get()).rootJoint, WOMWeaponColliders.SHOULDER_BUMP),
                     new Phase(0.59F, 0.6F, 0.65F, 0.69F, 0.69F, ((HumanoidArmature)humanoidArmature.get()).rootJoint, WOMWeaponColliders.SHOULDER_BUMP),
                     new Phase(0.69F, 0.7F, 0.75F, 0.79F, 0.79F, ((HumanoidArmature)humanoidArmature.get()).rootJoint, WOMWeaponColliders.SHOULDER_BUMP),
                     new Phase(0.79F, 0.8F, 0.85F, 0.89F, 0.89F, ((HumanoidArmature)humanoidArmature.get()).rootJoint, WOMWeaponColliders.SHOULDER_BUMP),
                     new Phase(0.89F, 1.0F, 1.1F, 1.3F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).rootJoint, WOMWeaponColliders.SHOULDER_BUMP)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(1.0F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(1.5F))
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT.get())
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(1.0F), 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(1.5F), 1)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F), 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 1)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT.get(), 1)
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT, 1)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(1.0F), 2)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(1.5F), 2)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F), 2)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 2)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT.get(), 2)
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT, 2)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(1.0F), 3)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(1.5F), 3)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F), 3)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 3)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT.get(), 3)
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT, 3)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(1.0F), 4)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(1.5F), 4)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F), 4)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 4)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT.get(), 4)
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT, 4)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(1.0F), 5)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(1.5F), 5)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F), 5)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 5)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT.get(), 5)
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT, 5)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(1.0F), 6)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(1.5F), 6)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F), 6)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 6)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT.get(), 6)
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT, 6)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(2.0F), 7)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(3.0F), 7)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F), 7)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL, 7)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get(), 7)
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT, 7)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, null)
      );
      CLONE_ANTITHEUS_AGRESSION = builder.nextAccessor(
         "biped/wom_clone/clone_antitheus_agression",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.05F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.2F, 0.35F, 0.59F, 0.59F, ((HumanoidArmature)humanoidArmature.get()).toolR, WOMWeaponColliders.ANTITHEUS_AGRESSION),
                     new Phase(
                        0.59F,
                        0.6F,
                        0.65F,
                        0.85F,
                        Float.MAX_VALUE,
                        ((HumanoidArmature)humanoidArmature.get()).rootJoint,
                        WOMWeaponColliders.ANTITHEUS_AGRESSION_REAP
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_HIT_DOWN)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F), 1)
               .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_TARGET_CURRENT_HEALTH.create(new float[]{1.0F})), 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.5F), 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL, 1)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.EVISCERATE.get(), 1)
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_HIT_UP, 1)
               .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE), 1)
               .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 1)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.9F)
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, null)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(1.0F, (livingEntityPatch, self, p) -> {
                  if (!livingEntityPatch.isLogicalClient()) {
                     livingEntityPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
                  }
               }, Side.SERVER)})
      );
      CLONE_ANTITHEUS_GUILLOTINE = builder.nextAccessor(
         "biped/wom_clone/clone_antitheus_guillotine",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.05F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.5F, 0.75F, 0.79F, 0.79F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.79F, 0.8F, 1.0F, 1.1F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.4F))
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.6F), 1)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F), 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.5F), 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 1)
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_HIT_REVERSE)
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_HIT_REVERSE, 1)
               .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 6)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.9F)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, false)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.3F}))
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, null)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(1.0F, (livingEntityPatch, self, p) -> {
                  if (!livingEntityPatch.isLogicalClient()) {
                     livingEntityPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
                  }
               }, Side.SERVER)})
      );
      CLONE_ANTITHEUS_IDLE = builder.nextAccessor(
         "biped/wom_clone/clone_antitheus_idle", accessor -> new StaticAnimation(0.2F, true, accessor, humanoidArmature)
      );
      CLONE_ANTITHEUS_AUTO_1 = builder.nextAccessor(
         "biped/wom_clone/clone_antitheus_auto_1",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.15F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.35F, 0.55F, 0.69F, 0.69F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.69F, 0.7F, 0.9F, 0.9F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.55F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.5F))
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_HIT)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.75F), 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.0F), 1)
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_HIT, 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 1)
               .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 1)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.9F)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addState(EntityState.CAN_SKILL_EXECUTION, false)
      );
      CLONE_ANTITHEUS_AUTO_2 = builder.nextAccessor(
         "biped/wom_clone/clone_antitheus_auto_2",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.15F, 0.15F, 0.45F, 0.45F, null, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.0F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_HIT_REVERSE)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.7F)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(1.0F, (livingEntityPatch, self, p) -> {
                  if (!livingEntityPatch.isLogicalClient()) {
                     livingEntityPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
                  }
               }, Side.SERVER)})
      );
      CLONE_ANTITHEUS_AUTO_3 = builder.nextAccessor(
         "biped/wom_clone/clone_antitheus_auto_3",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.15F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.15F, 0.35F, 0.5F, 0.5F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.5F, 0.55F, 0.7F, 0.75F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_HIT_REVERSE)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.5F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.8F), 1)
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_HIT_REVERSE, 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.0F), 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 1)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.9F)
      );
      CLONE_ANTITHEUS_AUTO_4 = builder.nextAccessor(
         "biped/wom_clone/clone_antitheus_auto_4",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.15F, 0.5F, 0.75F, 0.9F, null, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.6F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.5F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_HIT_REVERSE)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.7F)
               .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 2)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(1.2F, (livingEntityPatch, self, p) -> {
                  if (!livingEntityPatch.isLogicalClient()) {
                     livingEntityPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
                  }
               }, Side.SERVER)})
      );
      CLONE_ANTITHEUS_ASCENSION = builder.nextAccessor(
         "biped/wom_clone/clone_antitheus_ascension",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.1F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.5F, 0.6F, 0.65F, 0.65F, ((HumanoidArmature)humanoidArmature.get()).rootJoint, WOMWeaponColliders.PLUNDER_PERDITION),
                     new Phase(
                        0.65F, 1.75F, 2.05F, 2.8F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).rootJoint, WOMWeaponColliders.PLUNDER_PERDITION
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(1.0F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(4.0F))
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(20.0F))
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F), 1)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(20.0F), 1)
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT, 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 1)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.7F)
               .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
      );
      NULL_SKELETON_ANTITHEUS_ASCENSION = builder.nextAccessor(
         "biped/wom_clone/null_skeleton_antitheus_ascension",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.1F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.5F, 0.6F, 0.65F, 0.65F, ((HumanoidArmature)humanoidArmature.get()).rootJoint, WOMWeaponColliders.PLUNDER_PERDITION),
                     new Phase(
                        0.65F, 1.75F, 2.05F, 2.8F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).rootJoint, WOMWeaponColliders.PLUNDER_PERDITION
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(1.0F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(4.0F))
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(20.0F))
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F), 1)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(20.0F), 1)
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT, 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 1)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.7F)
               .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        1.5F,
                        (livingEntityPatch, self, p) -> {
                           if (((LivingEntity)livingEntityPatch.getOriginal()).m_9236_() instanceof ServerLevel serverLevel) {
                              NullSkeletonEntity nullSkeletonEntity = new NullSkeletonEntity(
                                 (EntityType<NullSkeletonEntity>)AnnoyingVillagersModEntities.NULL_SKELETON.get(), serverLevel
                              );
                              LivingEntity owner = (LivingEntity)livingEntityPatch.getOriginal();
                              Vec3 forward = getVec3(owner);
                              double dist = 2.0;
                              Vec3 spawnPos = owner.m_20182_().m_82549_(forward.m_82490_(dist));
                              nullSkeletonEntity.m_7678_(spawnPos.f_82479_, spawnPos.f_82480_, spawnPos.f_82481_, owner.m_146908_(), owner.m_146909_());
                              if (owner instanceof Player player) {
                                 nullSkeletonEntity.setPlayer(player);
                              } else if (owner instanceof NullEntity nullEntity) {
                                 nullSkeletonEntity.setNullEntity(nullEntity);
                              }

                              nullSkeletonEntity.m_6518_(serverLevel, serverLevel.m_6436_(nullSkeletonEntity.m_20183_()), MobSpawnType.MOB_SUMMONED, null, null);
                              serverLevel.m_7967_(nullSkeletonEntity);
                              if (owner instanceof NullEntity nullEntity) {
                                 nullEntity.claimWitherSkeletonSlot(nullSkeletonEntity);
                              }

                              LivingEntityPatch<?> nullSkeletonPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(
                                 nullSkeletonEntity, LivingEntityPatch.class
                              );
                              if (nullSkeletonPatch != null) {
                                 nullSkeletonPatch.playAnimationSynchronized(CLONE_ANTITHEUS_LAPSE, 0.0F);
                              }
                           }
                        },
                        Side.SERVER
                     )
                  }
               )
      );
      CLONE_ANTITHEUS_LAPSE = builder.nextAccessor(
         "biped/wom_clone/clone_antitheus_lapse",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.1F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.65F, 0.75F, 0.8F, 0.8F, ((HumanoidArmature)humanoidArmature.get()).rootJoint, WOMWeaponColliders.PLUNDER_PERDITION),
                     new Phase(0.8F, 1.3F, 1.4F, 1.45F, 1.45F, ((HumanoidArmature)humanoidArmature.get()).rootJoint, WOMWeaponColliders.PLUNDER_PERDITION),
                     new Phase(
                        1.45F, 1.75F, 1.85F, 2.3F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).rootJoint, WOMWeaponColliders.PLUNDER_PERDITION
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(1.0F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(4.0F))
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(20.0F))
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT)
               .addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.f_12557_)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(1.0F), 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(4.0F), 1)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(20.0F), 1)
               .addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.f_12557_, 1)
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD, 1)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F), 2)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(20.0F), 2)
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT, 2)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 2)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.7F)
               .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
      );
      CLONE_ANTITHEUS_ASCENDED_DEATHFALL = builder.nextAccessor(
         "biped/wom_clone/clone_antitheus_ascended_deathfall",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.05F,
                  0.5F,
                  0.55F,
                  0.75F,
                  WOMWeaponColliders.ANTITHEUS_ASCENDED_DEATHFALL,
                  ((HumanoidArmature)humanoidArmature.get()).rootJoint,
                  accessor,
                  humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F))
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
               .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.2F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get())
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, null)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.9F)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, false)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.75F}))
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.05F,
                        (livingEntityPatch, assetaccessor, animationparameters) -> {
                           ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_9236_()
                              .m_5594_(null, ((LivingEntity)livingEntityPatch.getOriginal()).m_20183_(), SoundEvents.f_11928_, SoundSource.NEUTRAL, 0.7F, 0.7F);
                           ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_9236_()
                              .m_5594_(
                                 null,
                                 ((LivingEntity)livingEntityPatch.getOriginal()).m_20183_(),
                                 (SoundEvent)EpicFightSounds.WHOOSH_BIG.get(),
                                 SoundSource.NEUTRAL,
                                 1.0F,
                                 1.0F
                              );
                        },
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        0.35F,
                        (livingEntityPatch, assetaccessor, animationparameters) -> ((LivingEntity)livingEntityPatch.getOriginal()).m_183634_(),
                        Side.SERVER
                     ),
                     InTimeEvent.create(
                        0.45F,
                        (livingEntityPatch, assetaccessor, animationparameters) -> {
                           ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_9236_()
                              .m_5594_(null, ((LivingEntity)livingEntityPatch.getOriginal()).m_20183_(), SoundEvents.f_11928_, SoundSource.NEUTRAL, 0.7F, 0.7F);
                           ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_9236_()
                              .m_5594_(
                                 null,
                                 ((LivingEntity)livingEntityPatch.getOriginal()).m_20183_(),
                                 (SoundEvent)EpicFightSounds.WHOOSH_BIG.get(),
                                 SoundSource.NEUTRAL,
                                 1.0F,
                                 1.0F
                              );
                           float f = (float)((LivingEntity)livingEntityPatch.getOriginal()).m_20185_();
                           float f1 = (float)((LivingEntity)livingEntityPatch.getOriginal()).m_20186_();

                           for (int i = 0; i < 24; i++) {
                              ((LivingEntity)livingEntityPatch.getOriginal())
                                 .m_9236_()
                                 .m_7106_(
                                    ParticleTypes.f_123755_,
                                    ((LivingEntity)livingEntityPatch.getOriginal()).m_20185_() + (double)(new Random().nextFloat() - 0.5F),
                                    ((LivingEntity)livingEntityPatch.getOriginal()).m_20186_() + 2.2F,
                                    ((LivingEntity)livingEntityPatch.getOriginal()).m_20189_() + (double)(new Random().nextFloat() - 0.5F),
                                    (double)((new Random().nextFloat() - 0.5F) * 0.05F),
                                    -((double)new Random().nextFloat() * (((LivingEntity)livingEntityPatch.getOriginal()).m_20186_() - (double)f1) * 0.4F),
                                    (double)((new Random().nextFloat() - 0.5F) * 0.05F)
                                 );
                           }
                        },
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        0.5F,
                        (livingEntityPatch, assetaccessor, animationparameters) -> ((LivingEntity)livingEntityPatch.getOriginal()).m_183634_(),
                        Side.SERVER
                     ),
                     InTimeEvent.create(
                        0.55F,
                        (livingEntityPatch, assetaccessor, animationparameters) -> {
                           ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_9236_()
                              .m_5594_(null, ((LivingEntity)livingEntityPatch.getOriginal()).m_20183_(), SoundEvents.f_12558_, SoundSource.NEUTRAL, 0.7F, 0.5F);
                           ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_9236_()
                              .m_5594_(
                                 null,
                                 ((LivingEntity)livingEntityPatch.getOriginal()).m_20183_(),
                                 (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get(),
                                 SoundSource.NEUTRAL,
                                 0.7F,
                                 0.7F
                              );
                           float f = (float)((LivingEntity)livingEntityPatch.getOriginal()).m_20185_();
                           float f1 = (float)((LivingEntity)livingEntityPatch.getOriginal()).m_20186_();
                           Vec3 vec3 = new Vec3(0.0, (double)(f1 - 2.0F) - ((LivingEntity)livingEntityPatch.getOriginal()).m_20186_(), 0.0);
                           ((LivingEntity)livingEntityPatch.getOriginal()).m_6478_(MoverType.SELF, vec3);
                           byte b0 = 80;
                           double d0 = 0.6;
                           double d1 = 0.01;

                           for (int i = 0; i < b0; i++) {
                              double d2 = (Math.PI * 2) * new Random().nextDouble();
                              double d3 = (new Random().nextDouble() - 0.5) * Math.PI * d1 / d0;
                              double d4 = d0 * Math.cos(d3) * Math.cos(d2);
                              double d5 = d0 * Math.cos(d3) * Math.sin(d2);
                              double d6 = d0 * Math.sin(d3);
                              float f3 = new Random().nextFloat() + 0.4F;
                              Vec3f vec3f = new Vec3f((float)d4 * f3, (float)d5 * f3, (float)d6 * f3);
                              OpenMatrix4f openmatrix4f = new OpenMatrix4f().rotate((float)Math.toRadians(90.0), new Vec3f(1.0F, 0.0F, 0.0F));
                              OpenMatrix4f.transform3v(openmatrix4f, vec3f, vec3f);
                              ((LivingEntity)livingEntityPatch.getOriginal())
                                 .m_9236_()
                                 .m_7106_(
                                    ParticleTypes.f_123755_,
                                    ((LivingEntity)livingEntityPatch.getOriginal()).m_20185_() + (double)vec3f.x,
                                    (double)((float)((int)((LivingEntity)livingEntityPatch.getOriginal()).m_20186_()) + vec3f.y + 0.02F),
                                    ((LivingEntity)livingEntityPatch.getOriginal()).m_20189_() + (double)vec3f.z,
                                    (double)vec3f.x,
                                    (double)vec3f.y,
                                    (double)vec3f.z
                                 );
                           }
                        },
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        0.55F,
                        (livingEntityPatch, assetaccessor, animationparameters) -> ((LivingEntity)livingEntityPatch.getOriginal()).m_183634_(),
                        Side.SERVER
                     )
                  }
               )
      );
      CLONE_ANTITHEUS_ASCENDED_BLINK = builder.nextAccessor(
         "biped/wom_clone/clone_antitheus_ascended_blink",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.05F,
                  0.3F,
                  0.4F,
                  0.4F,
                  WOMWeaponColliders.ANTITHEUS_ASCENDED_BLINK,
                  ((HumanoidArmature)humanoidArmature.get()).rootJoint,
                  accessor,
                  humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
               .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.4F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_HIT_REVERSE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get())
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.9F)
               .addProperty(WomAnimationProperty.ANTI_STUN_MULTIPLYER, 0.0F)
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, null)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.05F,
                        (entitypatch, self, params) -> {
                           ((LivingEntity)entitypatch.getOriginal())
                              .m_9236_()
                              .m_5594_(null, ((LivingEntity)entitypatch.getOriginal()).m_20183_(), SoundEvents.f_11928_, SoundSource.NEUTRAL, 0.7F, 0.7F);
                           ((LivingEntity)entitypatch.getOriginal())
                              .m_9236_()
                              .m_5594_(
                                 null,
                                 ((LivingEntity)entitypatch.getOriginal()).m_20183_(),
                                 (SoundEvent)EpicFightSounds.WHOOSH_BIG.get(),
                                 SoundSource.NEUTRAL,
                                 1.0F,
                                 1.0F
                              );
                        },
                        Side.CLIENT
                     )
                  }
               )
      );
      CLONE_ANTITHEUS_ASCENDED_BLACKHOLE = builder.nextAccessor(
         "biped/wom_clone/clone_antitheus_ascended_blackhole",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.05F,
                  1.45F,
                  1.5F,
                  1.7F,
                  WOMWeaponColliders.PLUNDER_PERDITION,
                  ((HumanoidArmature)humanoidArmature.get()).rootJoint,
                  accessor,
                  humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F))
               .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(30.0F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.5F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
               .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE))
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get())
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.5F)
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, null)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 1.7F}))
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.05F,
                        (livingEntityPatch, self, params) -> ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_9236_()
                              .m_5594_(
                                 null,
                                 ((LivingEntity)livingEntityPatch.getOriginal()).m_20183_(),
                                 (SoundEvent)WOMSounds.ANTITHEUS_BLACKKHOLE_CHARGEUP.get(),
                                 SoundSource.PLAYERS,
                                 2.0F,
                                 1.0F
                              ),
                        Side.SERVER
                     ),
                     InTimeEvent.create(
                        0.05F,
                        (livingEntityPatch, self, params) -> {
                           OpenMatrix4f transformMatrix = livingEntityPatch.getArmature()
                              .getBoundTransformFor(livingEntityPatch.getAnimator().getPose(0.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolL);
                           transformMatrix.translate(new Vec3f(0.0F, 0.0F, 0.0F));
                           OpenMatrix4f.mul(
                              new OpenMatrix4f()
                                 .rotate(
                                    (float)(-Math.toRadians((double)(((LivingEntity)livingEntityPatch.getOriginal()).f_20884_ + 180.0F))),
                                    new Vec3f(0.0F, 1.0F, 0.0F)
                                 ),
                              transformMatrix,
                              transformMatrix
                           );
                           int n = 70;
                           double r = 5.0;

                           for (int i = 0; i < n; i++) {
                              double theta = (Math.PI * 2) * new Random().nextDouble();
                              double phi = Math.acos(2.0 * new Random().nextDouble() - 1.0);
                              double x = r * Math.sin(phi) * Math.cos(theta);
                              double y = r * Math.sin(phi) * Math.sin(theta);
                              double z = r * Math.cos(phi);
                              ((LivingEntity)livingEntityPatch.getOriginal())
                                 .m_9236_()
                                 .m_7106_(
                                    (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                                    (double)transformMatrix.m30 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20185_() + x,
                                    (double)transformMatrix.m31 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20186_() + y,
                                    (double)transformMatrix.m32 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20189_() + z,
                                    (double)((float)(-x * 0.15F)),
                                    (double)((float)(-y * 0.15F)),
                                    (double)((float)(-z * 0.15F))
                                 );
                           }
                        },
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        1.05F,
                        (livingEntityPatch, self, params) -> {
                           ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_9236_()
                              .m_5594_(null, ((LivingEntity)livingEntityPatch.getOriginal()).m_20183_(), SoundEvents.f_11928_, SoundSource.NEUTRAL, 0.7F, 0.7F);
                           ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_9236_()
                              .m_5594_(
                                 null,
                                 ((LivingEntity)livingEntityPatch.getOriginal()).m_20183_(),
                                 (SoundEvent)EpicFightSounds.WHOOSH_BIG.get(),
                                 SoundSource.NEUTRAL,
                                 1.0F,
                                 1.0F
                              );
                        },
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        1.45F,
                        (livingEntityPatch, self, params) -> {
                           if (((LivingEntity)livingEntityPatch.getOriginal()).m_9236_() instanceof ServerLevel serverLevel) {
                              ((LivingEntity)livingEntityPatch.getOriginal())
                                 .m_9236_()
                                 .m_6269_(null, livingEntityPatch.getOriginal(), SoundEvents.f_12555_, SoundSource.PLAYERS, 1.0F, 0.5F);
                              OpenMatrix4f var6 = livingEntityPatch.getArmature()
                                 .getBoundTransformFor(livingEntityPatch.getAnimator().getPose(0.0F), ((HumanoidArmature)Armatures.BIPED.get()).handR);
                              OpenMatrix4f CORRECTION = new OpenMatrix4f()
                                 .rotate(
                                    (float)(-Math.toRadians((double)(((LivingEntity)livingEntityPatch.getOriginal()).f_19859_ + 180.0F))),
                                    new Vec3f(0.0F, 1.0F, 0.0F)
                                 );
                              CORRECTION.translate(new Vec3f(0.0F, 0.0F, -3.5F));
                              OpenMatrix4f.mul(CORRECTION, var6, var6);
                              serverLevel.m_8767_(
                                 (SimpleParticleType)WOMParticles.ANTITHEUS_BLACKHOLE_START.get(),
                                 (double)var6.m30 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20185_(),
                                 (double)var6.m31 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20186_(),
                                 (double)var6.m32 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20189_(),
                                 1,
                                 0.0,
                                 0.0,
                                 0.0,
                                 0.0
                              );
                              serverLevel.m_8767_(
                                 ParticleTypes.f_123755_,
                                 (double)var6.m30 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20185_(),
                                 (double)var6.m31 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20186_(),
                                 (double)var6.m32 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20189_(),
                                 48,
                                 0.0,
                                 0.0,
                                 0.0,
                                 0.5
                              );
                           }
                        },
                        Side.SERVER
                     ),
                     InTimeEvent.create(
                        1.45F,
                        (livingEntityPatch, self, params) -> {
                           OpenMatrix4f transformMatrix = livingEntityPatch.getArmature()
                              .getBoundTransformFor(livingEntityPatch.getAnimator().getPose(0.0F), ((HumanoidArmature)Armatures.BIPED.get()).handR);
                           OpenMatrix4f CORRECTION = new OpenMatrix4f()
                              .rotate(
                                 (float)(-Math.toRadians((double)(((LivingEntity)livingEntityPatch.getOriginal()).f_19859_ + 180.0F))),
                                 new Vec3f(0.0F, 1.0F, 0.0F)
                              );
                           CORRECTION.translate(new Vec3f(0.0F, 0.0F, -3.5F));
                           OpenMatrix4f.mul(CORRECTION, transformMatrix, transformMatrix);
                           Level level = ((LivingEntity)livingEntityPatch.getOriginal()).m_9236_();
                           Vec3 FractureCenter = new Vec3(
                              (double)transformMatrix.m30 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20185_(),
                              (double)transformMatrix.m31 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20186_() - 2.0,
                              (double)transformMatrix.m32 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20189_()
                           );
                           LevelUtil.circleSlamFracture((LivingEntity)livingEntityPatch.getOriginal(), level, FractureCenter, 4.0, true, true);
                        },
                        Side.CLIENT
                     )
                  }
               )
      );
      ENDER_AEGIS_MOONLESS_AUTO_1 = builder.nextAccessor(
         "biped/wom_clone/ender_aegis_moonless_auto_1",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.05F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{new Phase(0.0F, 0.25F, 0.45F, 0.5F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, null)}
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.8F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SMALL.get())
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get())
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
      );
      ENDER_AEGIS_MOONLESS_AUTO_2 = builder.nextAccessor(
         "biped/wom_clone/ender_aegis_moonless_auto_2",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.05F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.8F, 1.0F, 1.0F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, WOMWeaponColliders.MOONLESS_BYPASS)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.5F))
               .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(0.5F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.8F))
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.SHARPCUT_ANGLED_DOWN_LEFT_SLASH)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.4F)
      );
      YELLOW_SOLAR_AUTO_2 = builder.nextAccessor(
         "biped/wom_clone/yellow_solar_auto_2",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.05F, 0.65F, 0.8F, 1.0F, null, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.5F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.SOLAR_HIT_UP)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, null)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, false)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.8F, ReuseableEvents.SOLAR_GROUNDSLAM_SMALL, Side.CLIENT)})
      );
      YELLOW_NAPOLEON_AUTO_3 = builder.nextAccessor(
         "biped/wom_clone/yellow_napoleon_auto_3",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.15F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.15F, 0.35F, 0.39F, 0.39F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.39F, 0.5F, 0.7F, 0.74F, 0.74F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.74F, 0.75F, 0.85F, 1.19F, 1.19F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(1.19F, 1.2F, 2.2F, 2.25F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                  }
               )
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(6.0F))
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.2F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(6.0F), 1)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F), 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.1F), 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 1)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(6.0F), 2)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F), 2)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.2F), 2)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD, 2)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(6.0F), 3)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.5F), 3)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.1F), 3)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL, 3)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.15F)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{1.2F, 2.25F}))
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, true)
               .newTimePair(0.0F, 0.85F)
               .addState(EntityState.CAN_SKILL_EXECUTION, false)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(2.4F, (livingEntityPatch, self, p) -> {
                  if (!livingEntityPatch.isLogicalClient()) {
                     livingEntityPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
                  }
               }, Side.SERVER)})
      );
      YELLOW_NAPOLEON_AUSTERLITZ_SHOOT = builder.nextAccessor(
         "biped/wom_clone/yellow_napoleon_austerlitz_shoot",
         accessor -> (SpecialAttackAnimation)new SpecialAttackAnimation(
                  0.05F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.15F, 0.4F, 0.41F, 0.41F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.41F, 0.85F, 1.05F, 1.15F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.2F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(6.0F), 1)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F), 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(1.5F), 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 1)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.4F)
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, null)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addProperty(WomAnimationProperty.ANTI_STUN_MULTIPLYER, 1.0F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.0F,
                        (entityPatch, self, params) -> {
                           Level level = ((LivingEntity)entityPatch.getOriginal()).m_9236_();
                           LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                           level.m_7106_(
                              (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                              entity.m_20185_(),
                              entity.m_20186_(),
                              entity.m_20189_(),
                              Double.longBitsToDouble((long)entity.m_19879_()),
                              0.0,
                              0.0
                           );
                        },
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        0.1F,
                        (entityPatch, self, params) -> {
                           Level level = ((LivingEntity)entityPatch.getOriginal()).m_9236_();
                           LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                           level.m_7106_(
                              (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                              entity.m_20185_(),
                              entity.m_20186_(),
                              entity.m_20189_(),
                              Double.longBitsToDouble((long)entity.m_19879_()),
                              0.0,
                              0.0
                           );
                        },
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        0.2F,
                        (entityPatch, self, params) -> {
                           Level level = ((LivingEntity)entityPatch.getOriginal()).m_9236_();
                           LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                           level.m_7106_(
                              (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                              entity.m_20185_(),
                              entity.m_20186_(),
                              entity.m_20189_(),
                              Double.longBitsToDouble((long)entity.m_19879_()),
                              0.0,
                              0.0
                           );
                        },
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        0.3F,
                        (entityPatch, self, params) -> {
                           Level level = ((LivingEntity)entityPatch.getOriginal()).m_9236_();
                           LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                           level.m_7106_(
                              (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                              entity.m_20185_(),
                              entity.m_20186_(),
                              entity.m_20189_(),
                              Double.longBitsToDouble((long)entity.m_19879_()),
                              0.0,
                              0.0
                           );
                        },
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        0.4F,
                        (entityPatch, self, params) -> {
                           Level level = ((LivingEntity)entityPatch.getOriginal()).m_9236_();
                           LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                           level.m_7106_(
                              (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                              entity.m_20185_(),
                              entity.m_20186_(),
                              entity.m_20189_(),
                              Double.longBitsToDouble((long)entity.m_19879_()),
                              0.0,
                              0.0
                           );
                        },
                        Side.CLIENT
                     ),
                     InTimeEvent.create(0.5F, (livingEntityPatch, self, p) -> {
                        if (!livingEntityPatch.isLogicalClient()) {
                           livingEntityPatch.playAnimationSynchronized(WOMAnimations.TORMENT_DASH, 0.0F);
                        }
                     }, Side.SERVER)
                  }
               )
      );
      ENDER_AEGIS_NAPOLEON_RELOAD_1 = builder.nextAccessor(
         "biped/wom_clone/ender_aegis_napoleon_reload_1",
         accessor -> (SpecialAttackAnimation)new SpecialAttackAnimation(
                  0.15F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.15F, 0.25F, 0.3F, 0.3F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.3F, 0.35F, 0.45F, 0.5F, 0.5F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.5F, 0.55F, 0.65F, 0.7F, 0.7F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.7F, 0.75F, 0.95F, 1.0F, 1.0F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(1.0F, 1.05F, 1.2F, 1.25F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.2F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.0F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.2F), 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.0F), 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD, 1)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.2F), 2)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.0F), 2)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD, 2)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F), 3)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.0F), 3)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD, 3)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F), 4)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.0F), 4)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD, 4)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
               .addProperty(WomAnimationProperty.ANTI_STUN_MULTIPLYER, 1.0F)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(2.0F, (livingEntityPatch, self, p) -> {
                  if (!livingEntityPatch.isLogicalClient()) {
                     livingEntityPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
                  }
               }, Side.SERVER)})
      );
      ENDER_GLAIVE_NAPOLEON_AUTO_1 = builder.nextAccessor(
         "biped/wom_clone/ender_glaive_napoleon_auto_1",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.2F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.1F, 0.45F, 0.79F, 0.79F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.79F, 0.8F, 1.0F, 1.05F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.8F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.2F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.8F), 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.1F), 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD, 1)
               .addProperty(AttackAnimationProperty.CANCELABLE_MOVE, false)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(2.5F, (livingEntityPatch, self, p) -> {
                  if (!livingEntityPatch.isLogicalClient()) {
                     livingEntityPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
                  }
               }, Side.SERVER)})
      );
      ENDER_GLAIVE_NAPOLEON_AUTO_2 = builder.nextAccessor(
         "biped/wom_clone/ender_glaive_napoleon_auto_2",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.15F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.5F, 0.6F, 0.64F, 0.64F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.64F, 0.65F, 0.95F, 1.0F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                  }
               )
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(6.0F))
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.2F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(6.0F), 1)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F), 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.7F), 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL, 1)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(2.5F, (livingEntityPatch, self, p) -> {
                  if (!livingEntityPatch.isLogicalClient()) {
                     livingEntityPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
                  }
               }, Side.SERVER)})
      );
      ENDER_GLAIVE_NAPOLEON_AUTO_4 = builder.nextAccessor(
         "biped/wom_clone/ender_glaive_napoleon_auto_4",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.1F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{new Phase(0.0F, 0.6F, 1.0F, 1.9F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, null)}
               )
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(6.0F))
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 1.2F}))
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(2.5F, (livingEntityPatch, self, p) -> {
                  if (!livingEntityPatch.isLogicalClient()) {
                     livingEntityPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
                  }
               }, Side.SERVER)})
      );
      ENDER_GLAIVE_NAPOLEON_AUSTERLITZ = builder.nextAccessor(
         "biped/wom_clone/ender_glaive_napoleon_austerlitz",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.1F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.05F, 0.1F, 0.14F, 0.14F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.14F, 0.15F, 0.3F, 0.35F, 0.35F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.35F, 0.45F, 0.55F, 0.59F, 0.59F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.59F, 0.6F, 0.8F, 0.9F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7F), 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.5F), 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL, 1)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F), 2)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD, 2)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.8F), 3)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.7F), 3)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL, 3)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.4F)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(2.0F, (livingEntityPatch, self, p) -> {
                  if (!livingEntityPatch.isLogicalClient()) {
                     livingEntityPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
                  }
               }, Side.SERVER)})
      );
      DEMONIAC_RUINE_AUTO_1 = builder.nextAccessor(
         "biped/wom_clone/demoniac_ruine_auto_1",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.25F, 0.2F, 0.55F, 0.55F, null, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.75F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, null)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(1.5F, (livingEntityPatch, self, p) -> {
                  if (!livingEntityPatch.isLogicalClient()) {
                     livingEntityPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
                  }
               }, Side.SERVER)})
      );
      DEMONIAC_RUINE_AUTO_2 = builder.nextAccessor(
         "biped/wom_clone/demoniac_ruine_auto_2",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.2F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.15F, 0.55F, 0.59F, 0.59F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.59F, 0.6F, 0.85F, 0.95F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.8F))
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.8F), 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.95F), 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 1)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(1.5F, (livingEntityPatch, self, p) -> {
                  if (!livingEntityPatch.isLogicalClient()) {
                     livingEntityPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
                  }
               }, Side.SERVER)})
      );
      DEMONIAC_RUINE_AUTO_4 = builder.nextAccessor(
         "biped/wom_clone/demoniac_ruine_auto_4",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.25F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.5F, 0.6F, 0.65F, 0.65F, ((HumanoidArmature)humanoidArmature.get()).toolR, WOMWeaponColliders.RUINE_COMET),
                     new Phase(0.65F, 0.8F, 1.05F, 1.45F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.4F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.5F), 1)
               .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_TARGET_CURRENT_HEALTH.create(new float[]{1.0F})), 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.4F), 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NEUTRALIZE, 1)
               .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE), 1)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.5F)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(1.5F, (livingEntityPatch, self, p) -> {
                  if (!livingEntityPatch.isLogicalClient()) {
                     livingEntityPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
                  }
               }, Side.SERVER)})
      );
      DEMONIAC_RUINE_COMET = builder.nextAccessor(
         "biped/wom_clone/demoniac_ruine_comet",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.05F, 0.25F, 0.55F, 0.75F, WOMWeaponColliders.RUINE_COMET, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F))
               .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_TARGET_CURRENT_HEALTH.create(new float[]{0.5F})))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.8F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 20)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.5F)
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, null)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, false)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.3F}))
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER,
                  (PlaybackSpeedModifier)(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> {
                     if (elapsedTime >= 0.35F && elapsedTime < 0.45F) {
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
                        Vec3f direction = new Vec3f(2.5F, -0.25F, 0.0F);
                        OpenMatrix4f rotation = new OpenMatrix4f()
                           .rotate(
                              (float)(-Math.toRadians((double)(((LivingEntity)livingEntityPatch.getOriginal()).f_20884_ + 90.0F))), new Vec3f(0.0F, 1.0F, 0.0F)
                           );
                        OpenMatrix4f.transform3v(rotation, direction, direction);
                        AABB box = AABB.m_165882_(((LivingEntity)livingEntityPatch.getOriginal()).m_20318_(1.0F), 3.0, 3.0, 3.0);
                        List<Entity> list = ((LivingEntity)livingEntityPatch.getOriginal()).m_9236_().m_45933_(livingEntityPatch.getOriginal(), box);
                        if (distanceToGround > 0.5F && list.isEmpty()) {
                           livingentity.m_6478_(MoverType.SELF, direction.toDoubleVector());
                           return 0.05F;
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
                     InTimeEvent.create(0.25F, ReuseableEvents.RUINE_COMET_AIRBURST, Side.CLIENT),
                     InTimeEvent.create(0.5F, ReuseableEvents.RUINE_COMET_GROUNDTHRUST, Side.CLIENT)
                  }
               )
               .addEvents(new AnimationEvent[]{InTimeEvent.create(1.0F, (livingEntityPatch, self, p) -> {
                  if (!livingEntityPatch.isLogicalClient()) {
                     livingEntityPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
                  }
               }, Side.SERVER)})
      );
      DEMONIAC_TORMENT_CHARGED_ATTACK_2 = builder.nextAccessor(
         "biped/wom_clone/demoniac_torment_charged_attack_2",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.05F, 0.25F, 0.4F, 1.0F, null, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.2F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, null)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.15F, 0.65F}))
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(1.0F, (livingEntityPatch, self, p) -> {
                  if (!livingEntityPatch.isLogicalClient()) {
                     livingEntityPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
                  }
               }, Side.SERVER)})
      );
      AGONY_GUARD_HIT_1 = builder.nextAccessor(
         "biped/wom_clone/agony_guard_hit1",
         accessor -> (ActionAnimation)new ActionAnimation(0.05F, 0.5F, accessor, humanoidArmature)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.1F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
                     InTimeEvent.create(0.2F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
                     InTimeEvent.create(0.3F, ReuseableEvents.FAST_SPINING, Side.CLIENT),
                     InTimeEvent.create(0.4F, ReuseableEvents.FAST_SPINING, Side.CLIENT)
                  }
               )
      );
      ENDER_GLAIVE_NAPOLEON_SHOOT_3 = builder.nextAccessor(
         "biped/wom_clone/ender_glaive_napoleon_shoot_3",
         accessor -> (SpecialAttackAnimation)new SpecialAttackAnimation(
                  0.2F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.3F, 0.4F, 0.44F, 0.44F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.44F, 0.45F, 0.5F, 0.95F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F), 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(1.5F), 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 1)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.1F)
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, null)
               .addProperty(WomAnimationProperty.ANTI_STUN_MULTIPLYER, 1.0F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.5F,
                        (livingEntityPatch, self, p) -> {
                           Vec3 tipPos = EpicfightUtil.getJointWithTranslation(
                              livingEntityPatch.getOriginal(), new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolR, 4.3F, 2.3F
                           );
                           if (tipPos != null) {
                              BlockPos mutePos = BlockPos.m_274446_(tipPos);
                              AnnoyingVillagers.PACKET_HANDLER
                                 .send(
                                    PacketDistributor.TRACKING_ENTITY_AND_SELF.with(livingEntityPatch::getOriginal),
                                    new ClientboundMuteExplosionAtPos(mutePos, 4)
                                 );
                              ((LivingEntity)livingEntityPatch.getOriginal())
                                 .m_9236_()
                                 .m_255391_(
                                    livingEntityPatch.getOriginal(), tipPos.f_82479_, tipPos.f_82480_, tipPos.f_82481_, 2.0F, true, ExplosionInteraction.TNT
                                 );
                              Vec3 glaivePos = EpicfightUtil.getJointWithTranslation(
                                 livingEntityPatch.getOriginal(), new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolR, 1.3F, 2.3F
                              );
                              Vec3 explosionPos = EpicfightUtil.getJointWithTranslation(
                                 livingEntityPatch.getOriginal(), new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolR, 10.3F, 2.3F
                              );
                              AnnoyingVillagers.PACKET_HANDLER
                                 .send(
                                    PacketDistributor.TRACKING_ENTITY_AND_SELF.with(livingEntityPatch::getOriginal),
                                    new ClientboundGlaiveExplosionFx(glaivePos, explosionPos)
                                 );
                              if (explosionPos != null) {
                                 ((LivingEntity)livingEntityPatch.getOriginal())
                                    .m_9236_()
                                    .m_5594_(
                                       null,
                                       new BlockPos((int)explosionPos.f_82479_, (int)explosionPos.f_82480_, (int)explosionPos.f_82481_),
                                       (SoundEvent)AnnoyingVillagersModSounds.ENDER_SHOT.get(),
                                       SoundSource.NEUTRAL,
                                       1.0F,
                                       1.0F
                                    );
                              }
                           }
                        },
                        Side.SERVER
                     ),
                     InTimeEvent.create(1.5F, (livingEntityPatch, self, p) -> {
                        if (!livingEntityPatch.isLogicalClient()) {
                           livingEntityPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
                        }
                     }, Side.SERVER)
                  }
               )
      );
      ENDER_GLAIVE_AGONY_AUTO_1 = builder.nextAccessor(
         "biped/wom_clone/ender_glaive_agony_auto_1",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.1F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.15F, 0.25F, 0.3F, 0.3F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.3F, 0.55F, 0.65F, 0.7F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F), 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.2F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.29F), 1)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
               .addProperty(
                  ActionAnimationProperty.COORD_SET_TICK,
                  (MoveCoordSetter)(self, livingEntityPatch, transformSheet) -> {
                     LivingEntity attackTarget = livingEntityPatch.getTarget();
                     if (!((StaticAnimation)self.getRealAnimation().get()).getProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE).orElse(false)
                        && attackTarget != null) {
                        TransformSheet transform = ((TransformSheet)self.getTransfroms().get("Root")).copyAll();
                        Keyframe[] keyframes = transform.getKeyframes();
                        int startFrame = 0;
                        int endFrame = transform.getKeyframes().length - 1;
                        Vec3f keyLast = keyframes[endFrame].transform().translation();
                        Vec3 pos = ((LivingEntity)livingEntityPatch.getOriginal()).m_146892_();
                        Vec3 targetPos = attackTarget.m_20182_().m_82549_(attackTarget.m_20184_().m_82490_(8.0));
                        float horizontalDistance = Math.max(
                           (float)targetPos.m_82546_(pos).m_165924_() - (attackTarget.m_20205_() + ((LivingEntity)livingEntityPatch.getOriginal()).m_20205_()),
                           0.0F
                        );
                        Vec3f worldPosition = new Vec3f(keyLast.x, 0.0F, -horizontalDistance);
                        float scale = Math.min(worldPosition.length() / keyLast.length(), 2.0F);

                        for (int i = startFrame; i <= endFrame; i++) {
                           Vec3f translation = keyframes[i].transform().translation();
                           translation.z *= scale;
                        }

                        transformSheet.readFrom(transform);
                     } else {
                        transformSheet.readFrom((TransformSheet)self.getTransfroms().get("Root"));
                     }
                  }
               )
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.3F,
                        (livingEntityPatch, self, p) -> {
                           Vec3 tipPos = EpicfightUtil.getJointWithTranslation(
                              livingEntityPatch.getOriginal(), new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolR, 4.3F, 2.3F
                           );
                           if (tipPos != null) {
                              BlockPos mutePos = BlockPos.m_274446_(tipPos);
                              AnnoyingVillagers.PACKET_HANDLER
                                 .send(
                                    PacketDistributor.TRACKING_ENTITY_AND_SELF.with(livingEntityPatch::getOriginal),
                                    new ClientboundMuteExplosionAtPos(mutePos, 4)
                                 );
                              ((LivingEntity)livingEntityPatch.getOriginal())
                                 .m_9236_()
                                 .m_255391_(
                                    livingEntityPatch.getOriginal(), tipPos.f_82479_, tipPos.f_82480_, tipPos.f_82481_, 2.0F, true, ExplosionInteraction.TNT
                                 );
                              Vec3 glaivePos = EpicfightUtil.getJointWithTranslation(
                                 livingEntityPatch.getOriginal(), new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolR, 1.3F, 2.3F
                              );
                              Vec3 explosionPos = EpicfightUtil.getJointWithTranslation(
                                 livingEntityPatch.getOriginal(), new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolR, 10.3F, 2.3F
                              );
                              AnnoyingVillagers.PACKET_HANDLER
                                 .send(
                                    PacketDistributor.TRACKING_ENTITY_AND_SELF.with(livingEntityPatch::getOriginal),
                                    new ClientboundGlaiveExplosionFx(glaivePos, explosionPos)
                                 );
                              if (explosionPos != null) {
                                 ((LivingEntity)livingEntityPatch.getOriginal())
                                    .m_9236_()
                                    .m_5594_(
                                       null,
                                       new BlockPos((int)explosionPos.f_82479_, (int)explosionPos.f_82480_, (int)explosionPos.f_82481_),
                                       (SoundEvent)AnnoyingVillagersModSounds.ENDER_SHOT.get(),
                                       SoundSource.NEUTRAL,
                                       1.0F,
                                       1.0F
                                    );
                              }
                           }
                        },
                        Side.SERVER
                     ),
                     InTimeEvent.create(1.5F, (livingEntityPatch, self, p) -> {
                        if (!livingEntityPatch.isLogicalClient()) {
                           livingEntityPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
                        }
                     }, Side.SERVER)
                  }
               )
      );
      ENDER_GLAIVE_NAPOLEON_AUTO_3 = builder.nextAccessor(
         "biped/wom_clone/ender_glaive_napoleon_auto_3",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.15F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.15F, 0.35F, 0.39F, 0.39F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.39F, 0.5F, 0.7F, 0.74F, 0.74F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.74F, 0.75F, 0.85F, 1.19F, 1.19F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(1.19F, 1.2F, 2.2F, 2.25F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                  }
               )
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(6.0F))
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.2F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(6.0F), 1)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F), 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.1F), 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 1)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(6.0F), 2)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F), 2)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.2F), 2)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD, 2)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(6.0F), 3)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.5F), 3)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.1F), 3)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL, 3)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.15F)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{1.2F, 2.25F}))
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, true)
               .newTimePair(0.0F, 0.85F)
               .addState(EntityState.CAN_SKILL_EXECUTION, false)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(2.0F, (livingEntityPatch, self, p) -> {
                  if (!livingEntityPatch.isLogicalClient()) {
                     livingEntityPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
                  }
               }, Side.SERVER)})
      );
      ENDER_GLAIVE_NAPOLEON_WATERLOW = builder.nextAccessor(
         "biped/wom_clone/ender_glaive_napoleon_waterlow",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.1F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.15F, 0.35F, 0.39F, 0.39F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.39F, 0.4F, 0.6F, 0.64F, 0.64F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.64F, 0.65F, 1.0F, 1.1F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, null)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7F), 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.2F), 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 1)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F), 2)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.7F), 2)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL, 2)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.4F)
               .addProperty(AttackAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.2F}))
               .addEvents(new AnimationEvent[]{InTimeEvent.create(1.4F, (livingEntityPatch, self, p) -> {
                  if (!livingEntityPatch.isLogicalClient()) {
                     livingEntityPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
                  }
               }, Side.SERVER)})
      );
      CLONE_ENDERBLASTER_TWOHAND_TOMAHAWK = builder.nextAccessor(
         "biped/wom_clone/clone_enderblaster_twohand_dash",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.05F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.3F, 0.44F, 0.45F, 0.45F, ((HumanoidArmature)humanoidArmature.get()).legL, WOMWeaponColliders.KICK_HUGE),
                     new Phase(
                        0.45F, 0.5F, 0.6F, 0.65F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).rootJoint, WOMWeaponColliders.TORMENT_AIRSLAM
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7F))
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.3F), 1)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(5.0F))
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(5.0F), 1)
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT.get())
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT, 1)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get(), 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 1)
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, null)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.8F)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.45F, ReuseableEvents.GROUND_BODYSCRAPE_LAND, Side.CLIENT)})
      );
      YELLOW_TORMENT_CHARGED_ATTACK_3 = builder.nextAccessor(
         "biped/wom_clone/yellow_torment_charged_attack_3",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.05F,
                  1.0F,
                  1.2F,
                  1.5F,
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
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER,
                  (PlaybackSpeedModifier)(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> {
                     if (elapsedTime >= 0.9F && elapsedTime < 1.15F) {
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
                        return 1.0F - (1.0F / (-distanceToGround - 1.0F) + 1.0F);
                     } else {
                        return speed;
                     }
                  }
               )
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.35F, ReuseableEvents.AIRBURST_JUMP, Side.CLIENT),
                     InTimeEvent.create(1.15F, ReuseableEvents.TORMENT_GROUNDSLAM, Side.CLIENT),
                     InTimeEvent.create(1.15F, AVAnimations.ReuseableEvents.SHOCK_WAVE, Side.SERVER)
                  }
               )
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.1F,
                        (livingEntityPatch, self, params) -> {
                           LivingEntity target = ((LivingEntity)livingEntityPatch.getOriginal()).m_21214_();
                           if (target != null && target.m_20270_(livingEntityPatch.getOriginal()) < 30.0F) {
                              double offset = 4.0;
                              double referenceX = target.m_20185_();
                              double referenceY = target.m_20186_();
                              double referenceZ = target.m_20189_();
                              float referenceYaw = ((LivingEntity)livingEntityPatch.getOriginal()).f_20885_;
                              double newX = referenceX + offset * Math.sin(Math.toRadians((double)referenceYaw));
                              double newZ = referenceZ - offset * Math.cos(Math.toRadians((double)referenceYaw));
                              BlockPos blockPos = new BlockPos((int)newX, (int)referenceY, (int)newZ);
                              BlockState block = ((LivingEntity)livingEntityPatch.getOriginal()).m_9236_().m_8055_(blockPos);
                              if (!block.m_60838_(((LivingEntity)livingEntityPatch.getOriginal()).m_9236_(), blockPos)) {
                                 ((LivingEntity)livingEntityPatch.getOriginal()).m_6021_(newX, referenceY, newZ);
                              } else {
                                 ((LivingEntity)livingEntityPatch.getOriginal()).m_6021_(referenceX, referenceY, referenceZ);
                              }

                              ((LivingEntity)livingEntityPatch.getOriginal()).m_20256_(target.m_20184_());
                           }

                           ((ServerLevel)((LivingEntity)livingEntityPatch.getOriginal()).m_9236_())
                              .m_8767_(
                                 ParticleTypes.f_123789_,
                                 ((LivingEntity)livingEntityPatch.getOriginal()).m_20185_(),
                                 ((LivingEntity)livingEntityPatch.getOriginal()).m_20186_() + 1.0,
                                 ((LivingEntity)livingEntityPatch.getOriginal()).m_20189_(),
                                 60,
                                 0.05,
                                 0.05,
                                 0.05,
                                 0.5
                              );
                           ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_9236_()
                              .m_6263_(
                                 null,
                                 ((LivingEntity)livingEntityPatch.getOriginal()).f_19854_,
                                 ((LivingEntity)livingEntityPatch.getOriginal()).f_19855_ + 1.0,
                                 ((LivingEntity)livingEntityPatch.getOriginal()).f_19856_,
                                 SoundEvents.f_11852_,
                                 ((LivingEntity)livingEntityPatch.getOriginal()).m_5720_(),
                                 2.0F,
                                 1.0F - (new Random().nextFloat() - 0.5F) * 0.2F
                              );
                        },
                        Side.SERVER
                     ),
                     InTimeEvent.create(
                        0.05F,
                        (livingEntityPatch, self, params) -> {
                           LivingEntity entity = (LivingEntity)livingEntityPatch.getOriginal();
                           ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_9236_()
                              .m_7106_(
                                 (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                                 entity.m_20185_(),
                                 entity.m_20186_(),
                                 entity.m_20189_(),
                                 Double.longBitsToDouble((long)entity.m_19879_()),
                                 0.0,
                                 0.0
                              );
                        },
                        Side.CLIENT
                     )
                  }
               )
      );
      CLONE_ENDERBLASTER_ONEHAND_DASH = builder.nextAccessor(
         "biped/wom_clone/clone_enderblaster_onehand_dash",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.05F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.15F, 0.2F, 0.45F, 0.45F, ((HumanoidArmature)humanoidArmature.get()).legL, WOMWeaponColliders.KICK_HUGE),
                     new Phase(0.45F, 0.45F, 0.75F, 1.0F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).legL, WOMWeaponColliders.KICK_HUGE)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F))
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT.get())
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(4.0F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F), 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.0F), 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 1)
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT, 1)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get(), 1)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.8F)
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, null)
               .addProperty(
                  ActionAnimationProperty.COORD_SET_TICK,
                  (MoveCoordSetter)(self, entitypatch, transformSheet) -> {
                     LivingEntity attackTarget = entitypatch.getTarget();
                     if (!((StaticAnimation)self.getRealAnimation().get()).getProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE).orElse(false)
                        && attackTarget != null) {
                        TransformSheet transform = ((TransformSheet)self.getTransfroms().get("Root")).copyAll();
                        Keyframe[] keyframes = transform.getKeyframes();
                        int startFrame = 0;
                        int endFrame = transform.getKeyframes().length - 1;
                        Vec3f keyLast = keyframes[endFrame].transform().translation();
                        Vec3 pos = ((LivingEntity)entitypatch.getOriginal()).m_146892_();
                        Vec3 targetpos = attackTarget.m_20182_().m_82549_(attackTarget.m_20184_().m_82490_(1.5));
                        float horizontalDistance = Math.max(
                           (float)targetpos.m_82546_(pos).m_165924_() - (attackTarget.m_20205_() + ((LivingEntity)entitypatch.getOriginal()).m_20205_()), 0.0F
                        );
                        Vec3f worldPosition = new Vec3f(keyLast.x, 0.0F, -horizontalDistance);
                        float scale = Math.min(worldPosition.length() / keyLast.length(), 1.5F);

                        for (int i = startFrame; i <= endFrame; i++) {
                           Vec3f translation = keyframes[i].transform().translation();
                           translation.z *= scale;
                        }

                        transformSheet.readFrom(transform);
                     } else if (transformSheet != null) {
                        transformSheet.readFrom((TransformSheet)self.getTransfroms().get("Root"));
                     }
                  }
               )
      );
      SLEDGEHAMMER_TORMENT_BERSERK_AUTO_1 = builder.nextAccessor(
         "biped/wom_clone/sledgehammer_torment_berserk_auto_1",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.4F, 0.15F, 0.5F, 0.5F, null, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.1F))
               .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(9.0F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.OVERBLOOD_HIT)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.8F)
               .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.2F)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.35F, AVAnimations.ReuseableEvents.SLEDGEHAMMER_SHOOT, Side.SERVER)})
      );
      SLEDGEHAMMER_TORMENT_BERSERK_AUTO_2 = builder.nextAccessor(
         "biped/wom_clone/sledgehammer_torment_berserk_auto_2",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.4F, 0.15F, 0.5F, 0.5F, null, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.1F))
               .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(9.0F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.OVERBLOOD_HIT)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.8F)
               .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.2F)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.31F, AVAnimations.ReuseableEvents.SLEDGEHAMMER_SHOOT, Side.SERVER)})
      );
      SLEDGEHAMMER_SOLAR_AUTO_3 = builder.nextAccessor(
         "biped/wom_clone/sledgehammer_solar_auto_3",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.05F, 0.4F, 0.75F, 0.85F, null, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.3F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.3F)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, false)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.6F, AVAnimations.ReuseableEvents.SLEDGEHAMMER_SHOOT, Side.SERVER),
                     InTimeEvent.create(1.5F, (livingEntityPatch, self, p) -> {
                        if (!livingEntityPatch.isLogicalClient()) {
                           livingEntityPatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
                        }
                     }, Side.SERVER)
                  }
               )
      );
      CLONE_ANTITHEUS_SHOOT = builder.nextAccessor(
         "biped/wom_clone/clone_antitheus_shoot",
         accessor -> (AntitheusShootAttackAnimation)new AntitheusShootAttackAnimation(
                  0.05F, 0.05F, 0.1F, 0.5F, WOMWeaponColliders.ANTITHEUS_SHOOT, ((HumanoidArmature)humanoidArmature.get()).toolL, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.2F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
               .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.WOM_SWEEPING_EDGE_ENCHANTMENT.create(new float[]{1.0F})))
               .addProperty(AttackPhaseProperty.SWING_SOUND, SoundEvents.f_12558_)
               .addProperty(AttackPhaseProperty.HIT_SOUND, SoundEvents.f_12555_)
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.7F)
               .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 1.0F)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
      );
      CLONE_ANTITHEUS_ASCENDED_IDLE = builder.nextAccessor(
         "biped/wom_clone/clone_antitheus_ascended_idle", accessor -> new StaticAnimation(0.1F, true, accessor, humanoidArmature)
      );
      NULL_ANTITHEUS_ASCENDED_AUTO_1 = builder.nextAccessor(
         "biped/wom_clone/null_antitheus_ascended_auto_1",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.05F,
                  0.3F,
                  0.4F,
                  0.4F,
                  WOMWeaponColliders.ANTITHEUS_ASCENDED_PUNCHES,
                  ((HumanoidArmature)humanoidArmature.get()).rootJoint,
                  accessor,
                  humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.7F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get())
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.9F)
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, null)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.05F,
                        (livingEntityPatch, self, params) -> ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_9236_()
                              .m_5594_(
                                 null,
                                 ((LivingEntity)livingEntityPatch.getOriginal()).m_20183_(),
                                 (SoundEvent)EpicFightSounds.WHOOSH_BIG.get(),
                                 SoundSource.NEUTRAL,
                                 1.0F,
                                 1.0F
                              ),
                        Side.CLIENT
                     )
                  }
               )
      );
      NULL_ANTITHEUS_ASCENDED_AUTO_2 = builder.nextAccessor(
         "biped/wom_clone/null_antitheus_ascended_auto_2",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.05F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(
                        0.0F, 0.3F, 0.4F, 0.5F, 0.5F, ((HumanoidArmature)humanoidArmature.get()).rootJoint, WOMWeaponColliders.ANTITHEUS_ASCENDED_PUNCHES
                     ),
                     new Phase(
                        0.5F,
                        0.6F,
                        0.7F,
                        0.7F,
                        Float.MAX_VALUE,
                        ((HumanoidArmature)humanoidArmature.get()).rootJoint,
                        WOMWeaponColliders.ANTITHEUS_ASCENDED_PUNCHES
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7F))
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7F), 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.7F), 1)
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT)
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT, 1)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get())
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get(), 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 1)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.9F)
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, null)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.05F,
                        (livingEntityPatch, self, params) -> ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_9236_()
                              .m_5594_(
                                 null,
                                 ((LivingEntity)livingEntityPatch.getOriginal()).m_20183_(),
                                 (SoundEvent)EpicFightSounds.WHOOSH_BIG.get(),
                                 SoundSource.NEUTRAL,
                                 1.0F,
                                 1.0F
                              ),
                        Side.CLIENT
                     )
                  }
               )
      );
      NULL_ANTITHEUS_ASCENDED_AUTO_3 = builder.nextAccessor(
         "biped/wom_clone/null_antitheus_ascended_auto_3",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.05F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(
                        0.0F, 0.2F, 0.3F, 0.35F, 0.35F, ((HumanoidArmature)humanoidArmature.get()).rootJoint, WOMWeaponColliders.ANTITHEUS_ASCENDED_PUNCHES
                     ),
                     new Phase(
                        0.35F, 0.4F, 0.5F, 0.55F, 0.55F, ((HumanoidArmature)humanoidArmature.get()).rootJoint, WOMWeaponColliders.ANTITHEUS_ASCENDED_PUNCHES
                     ),
                     new Phase(
                        0.55F,
                        0.7F,
                        0.8F,
                        0.85F,
                        Float.MAX_VALUE,
                        ((HumanoidArmature)humanoidArmature.get()).rootJoint,
                        WOMWeaponColliders.ANTITHEUS_ASCENDED_PUNCHES
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F), 1)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F), 2)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.7F), 2)
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT)
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT, 1)
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT, 2)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get())
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get(), 1)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get(), 2)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD, 0)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD, 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 2)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.9F)
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, null)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.05F,
                        (livingEntityPatch, self, params) -> ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_9236_()
                              .m_5594_(
                                 null,
                                 ((LivingEntity)livingEntityPatch.getOriginal()).m_20183_(),
                                 (SoundEvent)EpicFightSounds.WHOOSH_BIG.get(),
                                 SoundSource.NEUTRAL,
                                 1.0F,
                                 1.0F
                              ),
                        Side.CLIENT
                     )
                  }
               )
      );
      HEROBRINE_MOB_ENDERSTEP_OBSCURIS = builder.nextAccessor(
         "biped/wom_clone/herobrine_mob_ender_obscuris",
         accessor -> (DodgeAnimation)new DodgeAnimation(0.05F, accessor, 0.6F, 1.65F, humanoidArmature)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.15F}))
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.1F, ReuseableEvents.ENDER_STEP, Side.BOTH),
                     InTimeEvent.create(
                        0.3F,
                        (livingEntityPatch, self, params) -> {
                           if (!livingEntityPatch.isLogicalClient()) {
                              Entity entity = livingEntityPatch.getOriginal();
                              if (entity instanceof HerobrineMob herobrineMob && herobrineMob.m_5448_() != null) {
                                 LivingEntity target = herobrineMob.m_5448_();
                                 if (target != null) {
                                    double offset = 2.0;
                                    double referenceX = target.m_20185_();
                                    double referenceY = target.m_20186_();
                                    double referenceZ = target.m_20189_();
                                    float referenceYaw = target.f_20885_;
                                    double sin = Math.sin(Math.toRadians((double)referenceYaw));
                                    double cos = Math.cos(Math.toRadians((double)referenceYaw));
                                    double newX = referenceX + offset * sin;
                                    double newZ = referenceZ - offset * cos;
                                    double newY = referenceY;
                                    ServerLevel serverLevel = (ServerLevel)entity.m_9236_();
                                    int baseY = target.m_20183_().m_123342_();
                                    int minY = serverLevel.m_141937_() + 1;
                                    int maxY = serverLevel.m_151558_() - 2;
                                    baseY = Mth.m_14045_(baseY, minY, maxY);
                                    MutableBlockPos mpos = new MutableBlockPos();
                                    boolean found = false;

                                    for (int tries = 0; tries < 10 && offset > 0.25; tries++) {
                                       newX = referenceX + offset * sin;
                                       newZ = referenceZ - offset * cos;
                                       mpos.m_122178_(Mth.m_14107_(newX), baseY, Mth.m_14107_(newZ));
                                       if (!serverLevel.m_46749_(mpos)) {
                                          offset -= 0.25;
                                       } else {
                                          int scan = 0;

                                          while (scan++ < 12 && mpos.m_123342_() > minY) {
                                             BlockPos belowPos = mpos.m_7495_();
                                             BlockState below = serverLevel.m_8055_(belowPos);
                                             if (below.m_60783_(serverLevel, belowPos, Direction.UP) && !below.m_60713_(Blocks.f_50626_)) {
                                                break;
                                             }

                                             mpos.m_122184_(0, -1, 0);
                                          }

                                          BlockPos belowPos = mpos.m_7495_();
                                          BlockState below = serverLevel.m_8055_(belowPos);
                                          BlockState feet = serverLevel.m_8055_(mpos);
                                          BlockState head = serverLevel.m_8055_(mpos.m_7494_());
                                          boolean solidBelow = below.m_60783_(serverLevel, belowPos, Direction.UP) && !below.m_60713_(Blocks.f_50626_);
                                          boolean freeFeet = feet.m_60795_() || feet.m_60734_() instanceof BushBlock;
                                          boolean freeHead = head.m_60795_() || head.m_60734_() instanceof BushBlock;
                                          if (solidBelow && freeFeet && freeHead) {
                                             newX = (double)mpos.m_123341_() + 0.5;
                                             newY = (double)mpos.m_123342_();
                                             newZ = (double)mpos.m_123343_() + 0.5;
                                             if (serverLevel.m_45756_(
                                                entity,
                                                entity.m_20191_().m_82386_(newX - entity.m_20185_(), newY - entity.m_20186_(), newZ - entity.m_20189_())
                                             )) {
                                                found = true;
                                                break;
                                             }
                                          }

                                          offset -= 0.25;
                                       }
                                    }

                                    if (found) {
                                       entity.m_6021_(newX, newY, newZ);
                                       entity.m_20256_(target.m_20184_());
                                       entity.m_7618_(Anchor.EYES, target.m_20182_());
                                    }
                                 }
                              }

                              ((ServerLevel)entity.m_9236_())
                                 .m_8767_(ParticleTypes.f_123789_, entity.m_20185_(), entity.m_20186_() + 1.0, entity.m_20189_(), 60, 0.05, 0.05, 0.05, 0.5);
                              entity.m_9236_()
                                 .m_6263_(
                                    null,
                                    entity.f_19854_,
                                    entity.f_19855_ + 1.0,
                                    entity.f_19856_,
                                    SoundEvents.f_11852_,
                                    entity.m_5720_(),
                                    2.0F,
                                    1.0F - (new Random().nextFloat() - 0.5F) * 0.2F
                                 );
                           }
                        },
                        Side.BOTH
                     )
                  }
               )
      );
      OBSIDIAN_ANTITHEUS_ASCENDED_DEATHFALL = builder.nextAccessor(
         "biped/wom_clone/obsidian_antitheus_ascended_deathfall",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.05F,
                  0.5F,
                  0.55F,
                  0.75F,
                  WOMWeaponColliders.ANTITHEUS_ASCENDED_DEATHFALL,
                  ((HumanoidArmature)humanoidArmature.get()).rootJoint,
                  accessor,
                  humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F))
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
               .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.2F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.ANTITHEUS_PUNCH_HIT)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get())
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, null)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 3.8F)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, false)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.75F}))
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.05F,
                        (livingEntityPatch, assetaccessor, animationparameters) -> {
                           ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_9236_()
                              .m_5594_(null, ((LivingEntity)livingEntityPatch.getOriginal()).m_20183_(), SoundEvents.f_11928_, SoundSource.NEUTRAL, 0.7F, 0.7F);
                           ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_9236_()
                              .m_5594_(
                                 null,
                                 ((LivingEntity)livingEntityPatch.getOriginal()).m_20183_(),
                                 (SoundEvent)EpicFightSounds.WHOOSH_BIG.get(),
                                 SoundSource.NEUTRAL,
                                 1.0F,
                                 1.0F
                              );
                           Vec3 position = new Vec3(0.0, 3.0, 0.0);
                           ((LivingEntity)livingEntityPatch.getOriginal()).m_6478_(MoverType.SELF, position);
                        },
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        0.35F,
                        (livingEntityPatch, assetaccessor, animationparameters) -> ((LivingEntity)livingEntityPatch.getOriginal()).m_183634_(),
                        Side.SERVER
                     ),
                     InTimeEvent.create(
                        0.45F,
                        (livingEntityPatch, assetaccessor, animationparameters) -> {
                           ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_9236_()
                              .m_5594_(null, ((LivingEntity)livingEntityPatch.getOriginal()).m_20183_(), SoundEvents.f_11928_, SoundSource.NEUTRAL, 0.7F, 0.7F);
                           ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_9236_()
                              .m_5594_(
                                 null,
                                 ((LivingEntity)livingEntityPatch.getOriginal()).m_20183_(),
                                 (SoundEvent)EpicFightSounds.WHOOSH_BIG.get(),
                                 SoundSource.NEUTRAL,
                                 1.0F,
                                 1.0F
                              );
                           float f = (float)((LivingEntity)livingEntityPatch.getOriginal()).m_20185_();
                           float f1 = (float)((LivingEntity)livingEntityPatch.getOriginal()).m_20186_();

                           for (int i = 0; i < 24; i++) {
                              ((LivingEntity)livingEntityPatch.getOriginal())
                                 .m_9236_()
                                 .m_7106_(
                                    ParticleTypes.f_123755_,
                                    ((LivingEntity)livingEntityPatch.getOriginal()).m_20185_() + (double)(new Random().nextFloat() - 0.5F),
                                    ((LivingEntity)livingEntityPatch.getOriginal()).m_20186_() + 2.2F,
                                    ((LivingEntity)livingEntityPatch.getOriginal()).m_20189_() + (double)(new Random().nextFloat() - 0.5F),
                                    (double)((new Random().nextFloat() - 0.5F) * 0.05F),
                                    -((double)new Random().nextFloat() * (((LivingEntity)livingEntityPatch.getOriginal()).m_20186_() - (double)f1) * 0.4F),
                                    (double)((new Random().nextFloat() - 0.5F) * 0.05F)
                                 );
                           }
                        },
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        0.5F,
                        (livingEntityPatch, assetaccessor, animationparameters) -> ((LivingEntity)livingEntityPatch.getOriginal()).m_183634_(),
                        Side.SERVER
                     ),
                     InTimeEvent.create(
                        0.55F,
                        (livingEntityPatch, assetaccessor, animationparameters) -> {
                           ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_9236_()
                              .m_5594_(null, ((LivingEntity)livingEntityPatch.getOriginal()).m_20183_(), SoundEvents.f_12558_, SoundSource.NEUTRAL, 0.7F, 0.5F);
                           ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_9236_()
                              .m_5594_(
                                 null,
                                 ((LivingEntity)livingEntityPatch.getOriginal()).m_20183_(),
                                 (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get(),
                                 SoundSource.NEUTRAL,
                                 0.7F,
                                 0.7F
                              );
                           float f = (float)((LivingEntity)livingEntityPatch.getOriginal()).m_20185_();
                           float f1 = (float)((LivingEntity)livingEntityPatch.getOriginal()).m_20186_();
                           Vec3 vec3 = new Vec3(0.0, (double)(f1 - 2.0F) - ((LivingEntity)livingEntityPatch.getOriginal()).m_20186_(), 0.0);
                           ((LivingEntity)livingEntityPatch.getOriginal()).m_6478_(MoverType.SELF, vec3);
                           byte b0 = 80;
                           double d0 = 0.6;
                           double d1 = 0.01;

                           for (int i = 0; i < b0; i++) {
                              double d2 = (Math.PI * 2) * new Random().nextDouble();
                              double d3 = (new Random().nextDouble() - 0.5) * Math.PI * d1 / d0;
                              double d4 = d0 * Math.cos(d3) * Math.cos(d2);
                              double d5 = d0 * Math.cos(d3) * Math.sin(d2);
                              double d6 = d0 * Math.sin(d3);
                              float f3 = new Random().nextFloat() + 0.4F;
                              Vec3f vec3f = new Vec3f((float)d4 * f3, (float)d5 * f3, (float)d6 * f3);
                              OpenMatrix4f openmatrix4f = new OpenMatrix4f().rotate((float)Math.toRadians(90.0), new Vec3f(1.0F, 0.0F, 0.0F));
                              OpenMatrix4f.transform3v(openmatrix4f, vec3f, vec3f);
                              ((LivingEntity)livingEntityPatch.getOriginal())
                                 .m_9236_()
                                 .m_7106_(
                                    ParticleTypes.f_123755_,
                                    ((LivingEntity)livingEntityPatch.getOriginal()).m_20185_() + (double)vec3f.x,
                                    (double)((float)((int)((LivingEntity)livingEntityPatch.getOriginal()).m_20186_()) + vec3f.y + 0.02F),
                                    ((LivingEntity)livingEntityPatch.getOriginal()).m_20189_() + (double)vec3f.z,
                                    (double)vec3f.x,
                                    (double)vec3f.y,
                                    (double)vec3f.z
                                 );
                           }
                        },
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        0.55F,
                        (livingEntityPatch, assetaccessor, animationparameters) -> ((LivingEntity)livingEntityPatch.getOriginal()).m_183634_(),
                        Side.SERVER
                     )
                  }
               )
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.5F, AVAnimations.ReuseableEvents.SUMMON_OBSIDIAN_CROSS, Side.SERVER),
                     InTimeEvent.create(0.6F, AVAnimations.ReuseableEvents.SUMMON_OBSIDIAN_CROSS_FIX_DELAY_SHADOW_HEROBRINE, Side.SERVER)
                  }
               )
      );
      OLD_MOONLESS_RUN = builder.nextAccessor("biped/wom_clone/old_moonless_run", accessor -> new MovementAnimation(0.1F, true, accessor, humanoidArmature));
      TRIDENT_TWO_HAND_RUN = builder.nextAccessor(
         "biped/wom_clone/trident_two_hand_run", accessor -> new MovementAnimation(0.1F, true, accessor, humanoidArmature)
      );
      OBSIDIAN_STRONG_PUNCH = builder.nextAccessor(
         "biped/wom_clone/obsidian_strong_punch",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.3F, 0.1F, 0.15F, 0.35F, WOMWeaponColliders.PUNCH, ((HumanoidArmature)humanoidArmature.get()).handL, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(4.0F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(5.0F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get())
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SMALL.get())
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
               .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 3.0F)
               .addProperty(WomAnimationProperty.ANTI_STUN_MULTIPLYER, 0.0F)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.1F, AVAnimations.ReuseableEvents.SUMMON_3_OBSIDIAN_HAND_LEFT, Side.SERVER)})
      );
      OBSIDIAN_ENDERBLASTER_TWOHAND_TISHNAW = builder.nextAccessor(
         "biped/wom_clone/obsidian_enderblaster_twohand_tishnaw",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.05F, 0.3F, 0.5F, 0.65F, WOMWeaponColliders.KICK_HUGE, ((HumanoidArmature)humanoidArmature.get()).legR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.65F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(3.2F))
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(4.0F))
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT.get())
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 20)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 4.0F)
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, null)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, false)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.3F}))
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER,
                  (PlaybackSpeedModifier)(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> {
                     if (elapsedTime >= 0.35F && elapsedTime < 0.45F) {
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

                        float distanceToGround = (float)org.joml.Math.max(
                           org.joml.Math.abs(((LivingEntity)livingEntityPatch.getOriginal()).m_20186_() - (double)dpy) - 1.0, 0.0
                        );
                        LivingEntity livingentity = (LivingEntity)livingEntityPatch.getOriginal();
                        Vec3f direction = new Vec3f(2.5F, -0.25F, 0.0F);
                        OpenMatrix4f rotation = new OpenMatrix4f()
                           .rotate(-org.joml.Math.toRadians(((LivingEntity)livingEntityPatch.getOriginal()).f_20884_ + 90.0F), new Vec3f(0.0F, 1.0F, 0.0F));
                        OpenMatrix4f.transform3v(rotation, direction, direction);
                        AABB box = AABB.m_165882_(((LivingEntity)livingEntityPatch.getOriginal()).m_20318_(1.0F), 3.0, 3.0, 3.0);
                        List<Entity> list = ((LivingEntity)livingEntityPatch.getOriginal()).m_9236_().m_45933_(livingEntityPatch.getOriginal(), box);
                        if (distanceToGround > 0.5F && list.isEmpty()) {
                           livingentity.m_6478_(MoverType.SELF, direction.toDoubleVector());
                           return 0.05F;
                        } else {
                           return speed;
                        }
                     } else {
                        return 1.0F;
                     }
                  }
               )
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.3F,
                        (livingEntityPatch, self, params) -> {
                           LivingEntity entity = (LivingEntity)livingEntityPatch.getOriginal();
                           ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_9236_()
                              .m_7106_(
                                 (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                                 entity.m_20185_(),
                                 entity.m_20186_(),
                                 entity.m_20189_(),
                                 Double.longBitsToDouble((long)entity.m_19879_()),
                                 0.0,
                                 0.0
                              );
                        },
                        Side.CLIENT
                     ),
                     InTimeEvent.create(0.5F, ReuseableEvents.GROUND_BODYSCRAPE_LAND, Side.CLIENT),
                     InTimeEvent.create(0.5F, AVAnimations.ReuseableEvents.SUMMON_OBSIDIAN_SMALL_CROSS, Side.SERVER)
                  }
               )
      );
      SHADOW_OBSIDIAN_SWORD_TORMENT_AIRSLAM = builder.nextAccessor(
         "biped/wom_clone/shadow_obsidian_sword_torment_airslam",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.1F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.45F, 0.55F, 0.6F, 0.6F, ((HumanoidArmature)humanoidArmature.get()).toolR, AVCollider.SHADOW_OBSIDIAN_PILLAR),
                     new Phase(0.6F, 0.5F, 0.65F, 0.8F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, AVCollider.SHADOW_OBSIDIAN_PILLAR)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.8F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.0F), 1)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F), 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.0F), 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD, 1)
               .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE))
               .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE), 1)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, null)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.0F}))
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER,
                  (PlaybackSpeedModifier)(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> {
                     if (elapsedTime >= 0.3F && elapsedTime < 0.55F) {
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

                        float distanceToGround = (float)org.joml.Math.max(
                           org.joml.Math.abs(((LivingEntity)livingEntityPatch.getOriginal()).m_20186_() - (double)dpy) - 1.0, 0.0
                        );
                        return 1.0F - (1.0F / (-distanceToGround - 1.0F) + 1.0F);
                     } else {
                        return speed;
                     }
                  }
               )
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.55F, ReuseableEvents.TORMENT_GROUNDSLAM_SMALL, Side.CLIENT)})
      );
      SHADOW_OBSIDIAN_SWORD_TORMENT_BERSERK_DASH = builder.nextAccessor(
         "biped/wom_clone/shadow_obsidian_sword_torment_berserk_dash",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.15F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.45F, 0.5F, 0.55F, 0.55F, ((HumanoidArmature)humanoidArmature.get()).toolR, AVCollider.SHADOW_OBSIDIAN_PILLAR),
                     new Phase(0.55F, 0.8F, 0.85F, 0.9F, 0.9F, ((HumanoidArmature)humanoidArmature.get()).toolR, AVCollider.SHADOW_OBSIDIAN_PILLAR),
                     new Phase(0.9F, 1.35F, 1.4F, 1.4F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, AVCollider.SHADOW_OBSIDIAN_PILLAR)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F))
               .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F), 1)
               .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])), 1)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F), 2)
               .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])), 2)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(3.0F))
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(3.0F), 1)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(3.0F), 2)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD, 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD, 2)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get(), 1)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get(), 2)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, null)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, false)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.5F, ReuseableEvents.TORMENT_GROUNDSLAM_SMALL, Side.CLIENT),
                     InTimeEvent.create(0.5F, AVAnimations.ReuseableEvents.SUMMON_OBSIDIAN_WALL, Side.SERVER),
                     InTimeEvent.create(0.85F, ReuseableEvents.TORMENT_GROUNDSLAM_SMALL, Side.CLIENT),
                     InTimeEvent.create(0.85F, AVAnimations.ReuseableEvents.SUMMON_OBSIDIAN_WALL, Side.SERVER),
                     InTimeEvent.create(1.4F, ReuseableEvents.TORMENT_GROUNDSLAM_SMALL, Side.CLIENT),
                     InTimeEvent.create(1.4F, AVAnimations.ReuseableEvents.SUMMON_OBSIDIAN_WALL, Side.SERVER)
                  }
               )
      );
      SHADOW_OBSIDIAN_SWORD_GESETZ_AUTO_3 = builder.nextAccessor(
         "biped/wom_clone/shadow_obsidian_sword_gezets_auto_3",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.15F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(
                        0.0F, 0.3F, 0.5F, 0.55F, 0.55F, InteractionHand.OFF_HAND, ((HumanoidArmature)humanoidArmature.get()).handL, WOMWeaponColliders.PUNCH
                     ),
                     new Phase(
                        0.55F,
                        0.7F,
                        0.85F,
                        1.0F,
                        Float.MAX_VALUE,
                        InteractionHand.OFF_HAND,
                        ((HumanoidArmature)humanoidArmature.get()).toolL,
                        WOMWeaponColliders.GESETZ
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.33F))
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(1.0F))
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.4F), 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.84F), 1)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(1.0F), 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 1)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get())
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get(), 1)
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get(), 1)
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE, 1)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.5F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.8F,
                        (livingEntityPatch, self, params) -> ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_9236_()
                              .m_6269_(
                                 null,
                                 livingEntityPatch.getOriginal(),
                                 SoundEvents.f_11668_,
                                 SoundSource.MASTER,
                                 0.3F,
                                 1.2F - (new Random().nextFloat() - 0.5F) * 0.2F
                              ),
                        Side.CLIENT
                     ),
                     InTimeEvent.create(0.8F, AVAnimations.ReuseableEvents.THROW_OBSIDIAN_OFFHAND, Side.SERVER)
                  }
               )
      );
      SHADOW_OBSIDIAN_SWORD_GESETZ_AUTO_2 = builder.nextAccessor(
         "biped/wom_clone/shadow_obsidian_sword_gezets_auto_2",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.2F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(
                        0.0F,
                        0.1F,
                        0.2F,
                        0.3F,
                        Float.MAX_VALUE,
                        InteractionHand.OFF_HAND,
                        ((HumanoidArmature)humanoidArmature.get()).toolL,
                        WOMWeaponColliders.GESETZ_INSET_LARGE
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.6F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.5F))
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(2.0F))
               .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
               .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get())
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.5F)
               .addProperty(WomAnimationProperty.ANTI_STUN_MULTIPLYER, 0.4F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.3F,
                        (livingEntityPatch, self, params) -> ((LivingEntity)livingEntityPatch.getOriginal())
                              .m_9236_()
                              .m_6269_(
                                 null,
                                 livingEntityPatch.getOriginal(),
                                 SoundEvents.f_11668_,
                                 SoundSource.MASTER,
                                 0.3F,
                                 1.2F - (new Random().nextFloat() - 0.5F) * 0.2F
                              ),
                        Side.CLIENT
                     )
                  }
               )
      );
      CLONE_NAPOLEON_WATERLOW_SHOOT = builder.nextAccessor(
         "biped/wom_clone/clone_napoleon_waterlow_shoot",
         accessor -> (SpecialAttackAnimation)new SpecialAttackAnimation(
                  0.1F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.1F, 0.3F, 0.35F, 0.35F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(0.35F, 0.8F, 0.9F, 0.94F, 0.94F, ((HumanoidArmature)humanoidArmature.get()).toolR, null),
                     new Phase(
                        0.94F,
                        0.95F,
                        1.1F,
                        1.1F,
                        Float.MAX_VALUE,
                        ((HumanoidArmature)humanoidArmature.get()).rootJoint,
                        WOMWeaponColliders.NAPOLEON_WATERLOW_SHOOT
                     )
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F), 1)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.0F), 1)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 1)
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.multiplier(6.0F), 2)
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F), 2)
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.58F), 2)
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE, 2)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, null)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addProperty(AttackAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.8F}))
               .addProperty(WomAnimationProperty.CAN_SPAM, true)
               .addProperty(WomAnimationProperty.ANTI_STUN_MULTIPLYER, 1.0F)
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER,
                  (PlaybackSpeedModifier)(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> {
                     if (elapsedTime > 0.8F && elapsedTime < 0.9F) {
                        float dpx = (float)((LivingEntity)livingEntityPatch.getOriginal()).m_20185_();
                        float dpy = (float)((LivingEntity)livingEntityPatch.getOriginal()).m_20186_() - 1.0F;
                        float dpz = (float)((LivingEntity)livingEntityPatch.getOriginal()).m_20189_();
                        BlockState block = ((LivingEntity)livingEntityPatch.getOriginal())
                           .m_9236_()
                           .m_8055_(new MutableBlockPos((double)dpx, (double)dpy, (double)dpz));
                        ((LivingEntity)livingEntityPatch.getOriginal()).m_20334_(0.0, -2.0, 0.0);
                        LivingEntity entity = (LivingEntity)livingEntityPatch.getOriginal();
                        return (block.m_60734_() instanceof BushBlock || block.m_60795_())
                              && !block.m_60713_(Blocks.f_50626_)
                              && dpy > -64.0F
                              && !block.m_60713_(Blocks.f_49990_)
                              && !entity.m_20096_()
                           ? (elapsedTime - 0.8F) / 0.1F
                           : 2.0F;
                     } else {
                        return 1.0F;
                     }
                  }
               )
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.15F,
                        (livingEntityPatch, self, params) -> {
                           Level level = ((LivingEntity)livingEntityPatch.getOriginal()).m_9236_();
                           LivingEntity entity = (LivingEntity)livingEntityPatch.getOriginal();
                           level.m_7106_(
                              (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                              entity.m_20185_(),
                              entity.m_20186_(),
                              entity.m_20189_(),
                              Double.longBitsToDouble((long)entity.m_19879_()),
                              0.0,
                              0.0
                           );
                        },
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        0.25F,
                        (livingEntityPatch, self, params) -> {
                           Level level = ((LivingEntity)livingEntityPatch.getOriginal()).m_9236_();
                           LivingEntity entity = (LivingEntity)livingEntityPatch.getOriginal();
                           level.m_7106_(
                              (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                              entity.m_20185_(),
                              entity.m_20186_(),
                              entity.m_20189_(),
                              Double.longBitsToDouble((long)entity.m_19879_()),
                              0.0,
                              0.0
                           );
                        },
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        0.35F,
                        (livingEntityPatch, self, params) -> {
                           Level level = ((LivingEntity)livingEntityPatch.getOriginal()).m_9236_();
                           LivingEntity entity = (LivingEntity)livingEntityPatch.getOriginal();
                           level.m_7106_(
                              (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                              entity.m_20185_(),
                              entity.m_20186_(),
                              entity.m_20189_(),
                              Double.longBitsToDouble((long)entity.m_19879_()),
                              0.0,
                              0.0
                           );
                        },
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        0.45F,
                        (livingEntityPatch, self, params) -> {
                           Level level = ((LivingEntity)livingEntityPatch.getOriginal()).m_9236_();
                           LivingEntity entity = (LivingEntity)livingEntityPatch.getOriginal();
                           level.m_7106_(
                              (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                              entity.m_20185_(),
                              entity.m_20186_(),
                              entity.m_20189_(),
                              Double.longBitsToDouble((long)entity.m_19879_()),
                              0.0,
                              0.0
                           );
                        },
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        0.75F,
                        (livingEntityPatch, self, params) -> {
                           Level level = ((LivingEntity)livingEntityPatch.getOriginal()).m_9236_();
                           LivingEntity entity = (LivingEntity)livingEntityPatch.getOriginal();
                           level.m_7106_(
                              (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                              entity.m_20185_(),
                              entity.m_20186_(),
                              entity.m_20189_(),
                              Double.longBitsToDouble((long)entity.m_19879_()),
                              0.0,
                              0.0
                           );
                        },
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        0.85F,
                        (livingEntityPatch, self, params) -> {
                           Level level = ((LivingEntity)livingEntityPatch.getOriginal()).m_9236_();
                           LivingEntity entity = (LivingEntity)livingEntityPatch.getOriginal();
                           level.m_7106_(
                              (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                              entity.m_20185_(),
                              entity.m_20186_(),
                              entity.m_20189_(),
                              Double.longBitsToDouble((long)entity.m_19879_()),
                              0.0,
                              0.0
                           );
                        },
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        0.95F,
                        (livingEntityPatch, self, params) -> {
                           Level level = ((LivingEntity)livingEntityPatch.getOriginal()).m_9236_();
                           LivingEntity entity = (LivingEntity)livingEntityPatch.getOriginal();
                           level.m_7106_(
                              (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                              entity.m_20185_(),
                              entity.m_20186_(),
                              entity.m_20189_(),
                              Double.longBitsToDouble((long)entity.m_19879_()),
                              0.0,
                              0.0
                           );
                        },
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        1.05F,
                        (livingEntityPatch, self, params) -> {
                           Level level = ((LivingEntity)livingEntityPatch.getOriginal()).m_9236_();
                           LivingEntity entity = (LivingEntity)livingEntityPatch.getOriginal();
                           level.m_7106_(
                              (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                              entity.m_20185_(),
                              entity.m_20186_(),
                              entity.m_20189_(),
                              Double.longBitsToDouble((long)entity.m_19879_()),
                              0.0,
                              0.0
                           );
                        },
                        Side.CLIENT
                     ),
                     InPeriodEvent.create(0.0F, 1.05F, (livingEntityPatch, self, params) -> {
                        ((LivingEntity)livingEntityPatch.getOriginal()).m_183634_();
                        if (livingEntityPatch.getOriginal() instanceof Player player) {
                           player.f_36106_ = 0.0;
                           player.f_36103_ = 0.0;
                        }
                     }, Side.BOTH)
                  }
               )
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.9F, ReuseableEvents.BODY_BIG_GROUNDSLAM, Side.CLIENT)})
               .addEvents(new AnimationEvent[]{InTimeEvent.create(1.0F, (livingEntityPatch, self, p) -> {
                  if (!livingEntityPatch.isLogicalClient()) {
                     livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.LEGENDARY_SWORD_WAKE_UP_ATTACK, 0.0F);
                  }
               }, Side.SERVER)})
               .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, true)
               .newTimePair(0.0F, 0.35F)
               .addState(EntityState.CAN_SKILL_EXECUTION, false)
               .newTimePair(0.55F, 1.1F)
               .addState(EntityState.CAN_SKILL_EXECUTION, false)
      );
      CUT_ENDERBLASTER_TWOHAND_RELOAD = builder.nextAccessor(
         "biped/wom_clone/cut_enderblaster_twohand_reload",
         accessor -> new StaticAnimation(0.1F, false, accessor, humanoidArmature)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.2F, AVAnimations.ReuseableEvents.TRIDENT_SPINNING, Side.CLIENT),
                     InTimeEvent.create(0.2F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(0.2F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(0.25F, AVAnimations.ReuseableEvents.TRIDENT_SPINNING, Side.CLIENT),
                     InTimeEvent.create(0.3F, AVAnimations.ReuseableEvents.TRIDENT_SPINNING, Side.CLIENT),
                     InTimeEvent.create(0.35F, AVAnimations.ReuseableEvents.TRIDENT_SPINNING, Side.CLIENT),
                     InTimeEvent.create(0.35F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_RIGHT, Side.SERVER),
                     InTimeEvent.create(0.35F, AVAnimations.ReuseableEvents.PLAY_TRIDENT_EFFECT_HAND_LEFT, Side.SERVER),
                     InTimeEvent.create(0.4F, AVAnimations.ReuseableEvents.TRIDENT_SPINNING, Side.CLIENT),
                     InTimeEvent.create(0.5F, AVAnimations.ReuseableEvents.TRIDENT_SPINNING, Side.CLIENT)
                  }
               )
      );
      HACKER_SWORD_SKILL = builder.nextAccessor(
         "biped/wom_clone/hacker_sword_skill",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.15F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(0.0F, 0.45F, 0.5F, 0.55F, 0.55F, ((HumanoidArmature)humanoidArmature.get()).toolR, ColliderPreset.SWORD),
                     new Phase(0.55F, 0.8F, 0.85F, 0.9F, 0.9F, ((HumanoidArmature)humanoidArmature.get()).toolR, ColliderPreset.SWORD),
                     new Phase(0.9F, 1.35F, 1.4F, 1.4F, 1.4F, ((HumanoidArmature)humanoidArmature.get()).toolR, ColliderPreset.SWORD),
                     new Phase(1.55F, 1.8F, 1.85F, 1.9F, 1.9F, ((HumanoidArmature)humanoidArmature.get()).toolR, ColliderPreset.SWORD),
                     new Phase(1.9F, 2.35F, 2.4F, 2.4F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, ColliderPreset.SWORD)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.5F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.5F))
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.0F,
                        (entityPatch, self, params) -> {
                           Level level = ((LivingEntity)entityPatch.getOriginal()).m_9236_();
                           LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                           level.m_7106_(
                              (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                              entity.m_20185_(),
                              entity.m_20186_(),
                              entity.m_20189_(),
                              Double.longBitsToDouble((long)entity.m_19879_()),
                              0.0,
                              0.0
                           );
                        },
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        0.35F,
                        (entityPatch, self, params) -> {
                           Level level = ((LivingEntity)entityPatch.getOriginal()).m_9236_();
                           LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                           level.m_7106_(
                              (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                              entity.m_20185_(),
                              entity.m_20186_(),
                              entity.m_20189_(),
                              Double.longBitsToDouble((long)entity.m_19879_()),
                              0.0,
                              0.0
                           );
                        },
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        0.85F,
                        (entityPatch, self, params) -> {
                           Level level = ((LivingEntity)entityPatch.getOriginal()).m_9236_();
                           LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                           level.m_7106_(
                              (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                              entity.m_20185_(),
                              entity.m_20186_(),
                              entity.m_20189_(),
                              Double.longBitsToDouble((long)entity.m_19879_()),
                              0.0,
                              0.0
                           );
                        },
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        1.35F,
                        (entityPatch, self, params) -> {
                           Level level = ((LivingEntity)entityPatch.getOriginal()).m_9236_();
                           LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                           level.m_7106_(
                              (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                              entity.m_20185_(),
                              entity.m_20186_(),
                              entity.m_20189_(),
                              Double.longBitsToDouble((long)entity.m_19879_()),
                              0.0,
                              0.0
                           );
                        },
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        1.85F,
                        (entityPatch, self, params) -> {
                           Level level = ((LivingEntity)entityPatch.getOriginal()).m_9236_();
                           LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                           level.m_7106_(
                              (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                              entity.m_20185_(),
                              entity.m_20186_(),
                              entity.m_20189_(),
                              Double.longBitsToDouble((long)entity.m_19879_()),
                              0.0,
                              0.0
                           );
                        },
                        Side.CLIENT
                     )
                  }
               )
      );
      WARBLADE_SATSUJIN_TSUKUYOMI = builder.nextAccessor(
         "biped/wom_clone/warblade_katana_tsukuyomi",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.05F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{new Phase(0.0F, 0.6F, 0.75F, 0.9F, Float.MAX_VALUE, ((HumanoidArmature)humanoidArmature.get()).toolR, null)}
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.5F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL)
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.SHARPCUT_UP_SLASH)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, null)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.6F}))
               .newTimePair(0.0F, 0.9F)
               .addStateRemoveOld(EntityState.INACTION, true)
               .addProperty(
                  StaticAnimationProperty.PLAY_SPEED_MODIFIER,
                  (PlaybackSpeedModifier)(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime) -> {
                     if (elapsedTime > 0.65F && elapsedTime < 0.75F) {
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

                        float distanceToGround = (float)org.joml.Math.max(
                           org.joml.Math.abs(((LivingEntity)livingEntityPatch.getOriginal()).m_20186_() - (double)dpy) - 1.0, 0.0
                        );
                        LivingEntity livingentity = (LivingEntity)livingEntityPatch.getOriginal();
                        Vec3f direction = new Vec3f(0.0F, -0.75F, 0.0F);
                        OpenMatrix4f rotation = new OpenMatrix4f()
                           .rotate(-org.joml.Math.toRadians(((LivingEntity)livingEntityPatch.getOriginal()).f_20884_ + 90.0F), new Vec3f(0.0F, 1.0F, 0.0F));
                        OpenMatrix4f.transform3v(rotation, direction, direction);
                        livingentity.m_6478_(MoverType.SELF, direction.toDoubleVector());
                        return 1.0F - (1.0F / (-distanceToGround - 1.0F) + 1.0F);
                     } else {
                        return speed;
                     }
                  }
               )
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(0.25F, (livingEntityPatch, self, params) -> EpicfightUtil.shootFlyingShockwave(livingEntityPatch), Side.SERVER),
                     InTimeEvent.create(0.7F, (livingEntityPatch, self, params) -> ((LivingEntity)livingEntityPatch.getOriginal()).m_183634_(), Side.SERVER)
                  }
               )
      );
      HOOK_HERRSCHER_UP = builder.nextAccessor(
         "biped/wom_clone/hook_herrscher_up",
         accessor -> (BasicMultipleAttackAnimation)new BasicMultipleAttackAnimation(
                  0.05F, 0.25F, 0.45F, 1.0F, null, ((HumanoidArmature)humanoidArmature.get()).toolR, accessor, humanoidArmature
               )
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.5F))
               .addProperty(AttackPhaseProperty.SWING_SOUND, null)
               .addProperty(AttackPhaseProperty.PARTICLE, WOMParticles.SHARPCUT_UP_SLASH)
               .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.GUARD_PUNCTURE, WOMDamageType.BLACKOUT))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.25F,
                        (entitypatch, self, params) -> {
                           if (((LivingEntity)entitypatch.getOriginal()).m_9236_() instanceof ServerLevel serverLevel) {
                              serverLevel.m_6263_(
                                 null,
                                 ((LivingEntity)entitypatch.getOriginal()).m_20185_(),
                                 ((LivingEntity)entitypatch.getOriginal()).m_20186_(),
                                 ((LivingEntity)entitypatch.getOriginal()).m_20189_(),
                                 (SoundEvent)EpicFightSounds.WHOOSH_ROD.get(),
                                 SoundSource.MASTER,
                                 0.5F,
                                 1.3F - (new Random().nextFloat() - 0.5F) * 0.1F
                              );
                           }
                        },
                        Side.SERVER
                     )
                  }
               )
      );
   }

   @NotNull
   private static Vec3 getVec3(LivingEntity owner) {
      Vec3 look = owner.m_20154_();
      Vec3 forward = new Vec3(look.f_82479_, 0.0, look.f_82481_);
      if (forward.m_82556_() < 1.0E-6) {
         float yawRad = (float)Math.toRadians((double)owner.m_146908_());
         forward = new Vec3((double)(-Mth.m_14031_(yawRad)), 0.0, (double)Mth.m_14089_(yawRad));
      }

      return forward.m_82541_();
   }
}
