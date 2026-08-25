package com.pla.annoyingvillagers.gameasset;

import com.pla.annoyingvillagers.AnnoyingVillagers;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.item.DiamondAttractorSwordItem;
import com.pla.annoyingvillagers.network.ClientboundDiamondAttractorFx;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.PacketDistributor;
import reascer.wom.gameasset.ReuseableEvents;
import reascer.wom.gameasset.colliders.WOMWeaponColliders;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.BasicAttackAnimation;
import yesman.epicfight.api.animation.types.DashAttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.MovementAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.Animations.ReusableSources;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.damagesource.StunType;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.MOD
)
public class AnimsYonchiChikito {
   public static AnimationAccessor<ActionAnimation> DIAMOND_ATTRACTOR_SKILL;
   public static AnimationAccessor<StaticAnimation> GREATAXE_IDLE;
   public static AnimationAccessor<MovementAnimation> GREATAXE_WALK;
   public static AnimationAccessor<BasicAttackAnimation> GREATAXE_SLASH;
   public static AnimationAccessor<BasicAttackAnimation> GREATAXE_OFFHAND_ATTACK;
   public static AnimationAccessor<BasicAttackAnimation> SLAM_FIRST;
   public static AnimationAccessor<BasicAttackAnimation> SLAM_SECOND;
   public static AnimationAccessor<BasicAttackAnimation> SLAM_THIRD;
   public static AnimationAccessor<StaticAnimation> SAKURA_STAFF_IDLE;
   public static AnimationAccessor<MovementAnimation> SAKURA_STAFF_WALK;
   public static AnimationAccessor<BasicAttackAnimation> SAKURA_STAFF_AUTO_1;
   public static AnimationAccessor<BasicAttackAnimation> SAKURA_STAFF_AUTO_2;
   public static AnimationAccessor<BasicAttackAnimation> SAKURA_STAFF_AUTO_3;
   public static AnimationAccessor<BasicAttackAnimation> SAKURA_STAFF_AUTO_4;
   public static AnimationAccessor<BasicAttackAnimation> SAKURA_STAFF_AUTO_5;
   public static AnimationAccessor<DashAttackAnimation> SAKURA_STAFF_DASH;

   public static void build(AnimationBuilder builder) {
      ArmatureAccessor<HumanoidArmature> humanoidArmature = Armatures.BIPED;
      DIAMOND_ATTRACTOR_SKILL = builder.nextAccessor(
         "biped/yonchi_chikito/diamond_attractor",
         accessor -> (ActionAnimation)new ActionAnimation(0.05F, Float.MAX_VALUE, accessor, humanoidArmature)
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
               .addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true)
               .addState(EntityState.CAN_BASIC_ATTACK, false)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.1F,
                        (livingEntityPatch, self, p) -> {
                           LivingEntity entity = (LivingEntity)livingEntityPatch.getOriginal();
                           if (entity.m_9236_() instanceof ServerLevel serverLevel) {
                              serverLevel.m_6263_(
                                 null,
                                 entity.m_20185_(),
                                 entity.m_20186_(),
                                 entity.m_20189_(),
                                 (SoundEvent)AnnoyingVillagersModSounds.DIAMOND_ATTRACTOR.get(),
                                 entity instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE,
                                 1.0F,
                                 1.0F
                              );
                              AnnoyingVillagers.PACKET_HANDLER
                                 .send(
                                    PacketDistributor.TRACKING_ENTITY_AND_SELF.with(livingEntityPatch::getOriginal), new ClientboundDiamondAttractorFx(entity)
                                 );
                              DiamondAttractorSwordItem.pullWeapons(entity);
                           }
                        },
                        Side.SERVER
                     )
                  }
               )
      );
      GREATAXE_IDLE = builder.nextAccessor("biped/yonchi_chikito/greataxe_idle", accessor -> new StaticAnimation(true, accessor, Armatures.BIPED));
      GREATAXE_WALK = builder.nextAccessor("biped/yonchi_chikito/greataxe_walk", accessor -> new MovementAnimation(true, accessor, Armatures.BIPED));
      GREATAXE_SLASH = builder.nextAccessor(
         "biped/yonchi_chikito/greataxe_slash",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.1F, 0.7F, 1.4F, 1.47F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.SHORT)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.8F)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
      );
      GREATAXE_OFFHAND_ATTACK = builder.nextAccessor(
         "biped/yonchi_chikito/greataxe_offhand_attack",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.1F, 0.7F, 1.4F, 1.47F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.8F)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
      );
      SLAM_FIRST = builder.nextAccessor(
         "biped/yonchi_chikito/slamfirst",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.1F, 0.9F, 1.4F, 1.47F, WOMWeaponColliders.SOLAR, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(4.0F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.8F)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(1.0F, ReuseableEvents.TORMENT_GROUNDSLAM_SMALL, Side.CLIENT)})
      );
      SLAM_SECOND = builder.nextAccessor(
         "biped/yonchi_chikito/slamsecond",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.1F, 2.1F, 3.0F, 3.0F, WOMWeaponColliders.SOLAR, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.6F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(4.0F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.8F)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(2.0F, ReuseableEvents.TORMENT_GROUNDSLAM_SMALL, Side.CLIENT)})
      );
      SLAM_THIRD = builder.nextAccessor(
         "biped/yonchi_chikito/slamthird",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.1F, 2.1F, 3.0F, 3.0F, WOMWeaponColliders.SOLAR, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.6F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(4.0F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.8F)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(2.425F, ReuseableEvents.TORMENT_GROUNDSLAM_SMALL, Side.CLIENT)})
      );
      SLAM_THIRD = builder.nextAccessor(
         "biped/yonchi_chikito/slamthird",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.05F,
                  accessor,
                  humanoidArmature,
                  new Phase[]{
                     new Phase(
                        2.26F, 2.5F, 3.5F, 3.56F, 3.56F, InteractionHand.MAIN_HAND, ((HumanoidArmature)humanoidArmature.get()).toolR, WOMWeaponColliders.SOLAR
                     ),
                     new Phase(2.26F, 2.5F, 3.5F, 3.56F, 3.56F, ((HumanoidArmature)humanoidArmature.get()).toolR, WOMWeaponColliders.SOLAR)
                  }
               )
               .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.0F))
               .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(4.0F))
               .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
               .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.8F)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(2.15F, ReuseableEvents.TORMENT_GROUNDSLAM_SMALL, Side.CLIENT),
                     InTimeEvent.create(3.4F, ReuseableEvents.TORMENT_GROUNDSLAM_SMALL, Side.CLIENT)
                  }
               )
      );
      SAKURA_STAFF_IDLE = builder.nextAccessor("biped/yonchi_chikito/sakurastaff_idle", accessor -> new StaticAnimation(true, accessor, Armatures.BIPED));
      SAKURA_STAFF_WALK = builder.nextAccessor("biped/yonchi_chikito/sakurastaff_walk", accessor -> new MovementAnimation(true, accessor, Armatures.BIPED));
      SAKURA_STAFF_AUTO_1 = builder.nextAccessor(
         "biped/yonchi_chikito/sakurastaff_auto1",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.1F, 0.2F, 0.3F, 0.7F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
      );
      SAKURA_STAFF_AUTO_2 = builder.nextAccessor(
         "biped/yonchi_chikito/sakurastaff_auto2",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.1F, 0.2F, 0.3F, 0.7F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
      );
      SAKURA_STAFF_AUTO_3 = builder.nextAccessor(
         "biped/yonchi_chikito/sakurastaff_auto3",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.1F, 0.2F, 0.3F, 0.7F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
      );
      SAKURA_STAFF_AUTO_4 = builder.nextAccessor(
         "biped/yonchi_chikito/sakurastaff_auto4",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.1F, 0.2F, 0.3F, 0.7F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
      );
      SAKURA_STAFF_AUTO_5 = builder.nextAccessor(
         "biped/yonchi_chikito/sakurastaff_auto5",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.1F, 0.2F, 0.3F, 0.7F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
      );
      SAKURA_STAFF_DASH = builder.nextAccessor(
         "biped/yonchi_chikito/sakurastaff_dash",
         accessor -> (DashAttackAnimation)new DashAttackAnimation(
                  0.1F, 0.25F, 0.3F, 0.4F, 0.8F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED, true
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
      );
   }
}
