package com.pla.annoyingvillagers.gameasset;

import com.pla.annoyingvillagers.entity.ElectricPhaseEntity;
import com.pla.annoyingvillagers.entity.ShadowHerobrineEntity;
import com.pla.annoyingvillagers.item.EnderAegisItem;
import com.pla.annoyingvillagers.item.ThunderDiamondBladeItem;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.property.AnimationEvent.InPeriodEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationEvent.SimpleEvent;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.AirSlashAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.BasicAttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.gameasset.Animations.ReusableSources;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.particle.EpicFightParticles;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.MOD
)
public class AnimsEpicFight {
   public static AnimationAccessor<StaticAnimation> EAT_OFFHAND;
   public static AnimationAccessor<StaticAnimation> DRINK_OFFHAND;
   public static AnimationAccessor<StaticAnimation> SHIELD_MAINHAND;
   public static AnimationAccessor<ActionAnimation> AEGIS_SHIELD_SHOOT;
   public static AnimationAccessor<StaticAnimation> SHIELD_OFFHAND;
   public static AnimationAccessor<BasicAttackAnimation> OBSIDIAN_FIST_AUTO1;
   public static AnimationAccessor<BasicAttackAnimation> OBSIDIAN_FIST_AUTO2;
   public static AnimationAccessor<BasicAttackAnimation> OBSIDIAN_FIST_AUTO3;
   public static AnimationAccessor<AirSlashAnimation> OBSIDIAN_FIST_AIR_SLASH;
   public static AnimationAccessor<AirSlashAnimation> SHADOW_OBSIDIAN_FIST_AIR_SLASH;
   public static AnimationAccessor<AttackAnimation> OBSIDIAN_BIPED_LANDING;
   public static AnimationAccessor<AttackAnimation> OBSIDIAN_ZOMBIE_ATTACK3;
   public static AnimationAccessor<BasicAttackAnimation> SHADOW_OBSIDIAN_FIST_AUTO1;
   public static AnimationAccessor<BasicAttackAnimation> SHADOW_OBSIDIAN_FIST_AUTO3;
   public static AnimationAccessor<AttackAnimation> SHADOW_HEROBRINE_BIPED_LANDING;
   public static AnimationAccessor<AttackAnimation> NERF_TSUNAMI_REINFORCED;
   public static AnimationAccessor<StaticAnimation> BLUE_DEMON_DIE_LEGENDARY_SWORD_START;
   public static AnimationAccessor<StaticAnimation> BLUE_DEMON_DIE_LEGENDARY_SWORD_TICK;
   public static AnimationAccessor<BasicAttackAnimation> HOOK_AXE_AUTO1;
   public static AnimationAccessor<BasicAttackAnimation> HOOK_AXE_AUTO2;
   public static AnimationAccessor<AttackAnimation> HOOK_DANCING_EDGE;
   public static AnimationAccessor<AttackAnimation> DNAX_HOOK_SWEEPING_EDGE;
   public static AnimationAccessor<AttackAnimation> DNAX_HOOK_DANCING_EDGE;
   public static AnimationAccessor<AttackAnimation> THUNDER_SWEEPING_EDGE;
   public static AnimationAccessor<AttackAnimation> THUNDER_DANCING_EDGE;

   public static void build(AnimationBuilder builder) {
      ArmatureAccessor<HumanoidArmature> humanoidArmature = Armatures.BIPED;
      EAT_OFFHAND = builder.nextAccessor("biped/epicfight_clone/eat_offhand", accessor -> new StaticAnimation(0.35F, true, accessor, humanoidArmature));
      DRINK_OFFHAND = builder.nextAccessor("biped/epicfight_clone/drink_offhand", accessor -> new StaticAnimation(0.35F, true, accessor, humanoidArmature));
      SHIELD_MAINHAND = builder.nextAccessor("biped/epicfight_clone/shield_mainhand", accessor -> new StaticAnimation(0.35F, true, accessor, humanoidArmature));
      AEGIS_SHIELD_SHOOT = builder.nextAccessor(
         "biped/epicfight_clone/aegis_shield_shoot",
         accessor -> (ActionAnimation)new ActionAnimation(0.35F, accessor, humanoidArmature)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.5F, (livingEntityPatch, self, p) -> {
                  EnderAegisItem.shieldShoot(((LivingEntity)livingEntityPatch.getOriginal()).m_9236_(), livingEntityPatch.getOriginal());
                  if (livingEntityPatch.getOriginal() instanceof Player player) {
                     ItemCooldowns cooldowns = player.m_36335_();
                     cooldowns.m_41524_(player.m_21205_().m_41720_(), 10);
                  }
               }, Side.SERVER)})
      );
      SHIELD_OFFHAND = builder.nextAccessor("biped/epicfight_clone/shield_offhand", accessor -> new StaticAnimation(0.35F, true, accessor, humanoidArmature));
      OBSIDIAN_FIST_AUTO1 = builder.nextAccessor(
         "biped/epicfight_clone/obsidian_fist_auto1",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.08F, 0.05F, 0.15F, 0.15F, InteractionHand.OFF_HAND, null, ((HumanoidArmature)Armatures.BIPED.get()).toolL, accessor, Armatures.BIPED
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 3.2F)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.1F, AVAnimations.ReuseableEvents.SUMMON_2_OBSIDIAN_HAND_LEFT, Side.SERVER)})
      );
      OBSIDIAN_FIST_AUTO2 = builder.nextAccessor(
         "biped/epicfight_clone/obsidian_fist_auto2",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.08F, 0.05F, 0.15F, 0.15F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 3.2F)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.1F, AVAnimations.ReuseableEvents.SUMMON_2_OBSIDIAN_HAND_RIGHT, Side.SERVER)})
      );
      OBSIDIAN_FIST_AUTO3 = builder.nextAccessor(
         "biped/epicfight_clone/obsidian_fist_auto3",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.08F, 0.05F, 0.15F, 0.5F, InteractionHand.OFF_HAND, null, ((HumanoidArmature)Armatures.BIPED.get()).toolL, accessor, Armatures.BIPED
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 3.2F)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.1F, AVAnimations.ReuseableEvents.SUMMON_3_OBSIDIAN_HAND_LEFT, Side.SERVER)})
      );
      SHADOW_OBSIDIAN_FIST_AUTO1 = builder.nextAccessor(
         "biped/epicfight_clone/shadow_obsidian_fist_auto1",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.08F, 0.05F, 0.15F, 0.15F, InteractionHand.OFF_HAND, null, ((HumanoidArmature)Armatures.BIPED.get()).toolL, accessor, Armatures.BIPED
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.1F, AVAnimations.ReuseableEvents.SUMMON_2_OBSIDIAN_HAND_LEFT, Side.SERVER)})
      );
      SHADOW_OBSIDIAN_FIST_AUTO3 = builder.nextAccessor(
         "biped/epicfight_clone/shadow_obsidian_fist_auto3",
         accessor -> (BasicAttackAnimation)new BasicAttackAnimation(
                  0.08F, 0.05F, 0.15F, 0.5F, InteractionHand.OFF_HAND, null, ((HumanoidArmature)Armatures.BIPED.get()).toolL, accessor, Armatures.BIPED
               )
               .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.1F, AVAnimations.ReuseableEvents.SUMMON_3_OBSIDIAN_HAND_LEFT, Side.SERVER)})
      );
      OBSIDIAN_FIST_AIR_SLASH = builder.nextAccessor(
         "biped/epicfight_clone/obsidian_fist_airslash",
         accessor -> (AirSlashAnimation)new AirSlashAnimation(
                  0.1F, 0.15F, 0.26F, 0.4F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 4.0F)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.2F, AVAnimations.ReuseableEvents.SUMMON_6_OBSIDIAN_HAND_RIGHT, Side.SERVER)})
      );
      SHADOW_OBSIDIAN_FIST_AIR_SLASH = builder.nextAccessor(
         "biped/epicfight_clone/shadow_obsidian_fist_airslash",
         accessor -> (AirSlashAnimation)new AirSlashAnimation(
                  0.1F, 0.15F, 0.26F, 0.4F, AVCollider.SHADOW_OBSIDIAN_PILLAR, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 4.0F)
      );
      OBSIDIAN_BIPED_LANDING = builder.nextAccessor(
         "biped/epicfight_clone/obsidian_landing",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.0F, 0.0F, 0.0F, 0.0F, Float.MAX_VALUE, null, ((HumanoidArmature)Armatures.BIPED.get()).head, accessor, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
               .addState(EntityState.CAN_BASIC_ATTACK, false)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.1F, AVAnimations.ReuseableEvents.SUMMON_OBSIDIAN_PILLAR, Side.SERVER)})
      );
      OBSIDIAN_ZOMBIE_ATTACK3 = builder.nextAccessor(
         "biped/epicfight_clone/obsidian_zombie_attack3",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.1F, 0.5F, 0.5F, 0.6F, 1.15F, ColliderPreset.HEAD, ((HumanoidArmature)Armatures.BIPED.get()).head, accessor, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.5F)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.65F, AVAnimations.ReuseableEvents.SUMMON_OBSIDIAN_WALL, Side.SERVER)})
      );
      SHADOW_HEROBRINE_BIPED_LANDING = builder.nextAccessor(
         "biped/epicfight_clone/shadow_herobrine_landing",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.0F, 0.0F, 0.0F, 0.0F, Float.MAX_VALUE, null, ((HumanoidArmature)Armatures.BIPED.get()).head, accessor, Armatures.BIPED
               )
               .addState(EntityState.CAN_BASIC_ATTACK, false)
               .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.1F, (livingEntityPatch, self, p) -> {
                  if (!livingEntityPatch.isLogicalClient()) {
                     LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
                     if (livingEntity instanceof ShadowHerobrineEntity shadowHerobrineEntity) {
                        shadowHerobrineEntity.setObsidianMachineGunTick();
                     }
                  }
               }, Side.SERVER)})
      );
      NERF_TSUNAMI_REINFORCED = builder.nextAccessor(
         "biped/epicfight_clone/tsunami_reinforced",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.1F,
                  0.2F,
                  0.35F,
                  0.45F,
                  0.7F,
                  ColliderPreset.BIPED_BODY_COLLIDER,
                  ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                  accessor,
                  Armatures.BIPED
               )
               .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(2.0F))
               .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD_WITH_X_ROT)
               .addProperty(ActionAnimationProperty.COORD_SET_TICK, null)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, false)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.15F, 0.85F}))
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, ReusableSources.CONSTANT_ONE)
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, ReusableSources.ROOT_X_MODIFIER)
               .addEvents(StaticAnimationProperty.ON_END_EVENTS, new AnimationEvent[]{SimpleEvent.create(ReusableSources.RESTORE_BOUNDING_BOX, Side.BOTH)})
               .addEvents(
                  StaticAnimationProperty.TICK_EVENTS,
                  new AnimationEvent[]{SimpleEvent.create(ReusableSources.RESIZE_BOUNDING_BOX, Side.BOTH).params(EntityDimensions.m_20395_(0.6F, 1.0F))}
               )
               .addEvents(
                  new AnimationEvent[]{
                     InPeriodEvent.create(
                        0.35F,
                        1.0F,
                        (entitypatch, animation, params) -> {
                           Vec3 pos = ((LivingEntity)entitypatch.getOriginal()).m_20182_();

                           for (int x = -1; x <= 1; x += 2) {
                              for (int z = -1; z <= 1; z += 2) {
                                 Vec3 rand = new Vec3(Math.random() * (double)x, Math.random(), Math.random() * (double)z).m_82541_().m_82490_(2.0);
                                 ((LivingEntity)entitypatch.getOriginal())
                                    .m_9236_()
                                    .m_7106_(
                                       (ParticleOptions)EpicFightParticles.TSUNAMI_SPLASH.get(),
                                       pos.f_82479_ + rand.f_82479_,
                                       pos.f_82480_ + rand.f_82480_ - 1.0,
                                       pos.f_82481_ + rand.f_82481_,
                                       rand.f_82479_ * 0.1,
                                       rand.f_82480_ * 0.1,
                                       rand.f_82481_ * 0.1
                                    );
                              }
                           }
                        },
                        Side.CLIENT
                     )
                  }
               )
      );
      BLUE_DEMON_DIE_LEGENDARY_SWORD_START = builder.nextAccessor(
         "biped/epicfight_clone/blue_demon_die_legendary_sword_start", accessor -> new StaticAnimation(false, accessor, humanoidArmature)
      );
      BLUE_DEMON_DIE_LEGENDARY_SWORD_TICK = builder.nextAccessor(
         "biped/epicfight_clone/blue_demon_die_legendary_sword_tick", accessor -> new StaticAnimation(true, accessor, humanoidArmature)
      );
      HOOK_AXE_AUTO1 = builder.nextAccessor(
         "biped/epicfight_clone/hook_axe_auto1",
         accessor -> new BasicAttackAnimation(0.15F, 0.05F, 0.15F, 0.7F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED)
      );
      HOOK_AXE_AUTO2 = builder.nextAccessor(
         "biped/epicfight_clone/hook_axe_auto2",
         accessor -> new BasicAttackAnimation(0.15F, 0.05F, 0.15F, 0.85F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED)
      );
      HOOK_DANCING_EDGE = builder.nextAccessor(
         "biped/epicfight_clone/hook_dancing_edge",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.1F,
                  accessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(0.0F, 0.25F, 0.4F, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                     new Phase(0.4F, 0.4F, 0.5F, 0.55F, 0.6F, InteractionHand.OFF_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolL, null),
                     new Phase(0.6F, 0.6F, 0.7F, 1.15F, Float.MAX_VALUE, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
      );
      DNAX_HOOK_SWEEPING_EDGE = builder.nextAccessor(
         "biped/epicfight_clone/dnax_hook_sweeping_edge",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.1F, 0.0F, 0.15F, 0.3F, 0.8F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
               .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 1)
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER)
      );
      DNAX_HOOK_DANCING_EDGE = builder.nextAccessor(
         "biped/epicfight_clone/dnax_hook_dancing_edge",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.1F,
                  accessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(0.0F, 0.25F, 0.4F, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                     new Phase(0.4F, 0.4F, 0.5F, 0.55F, 0.6F, InteractionHand.OFF_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolL, null),
                     new Phase(0.6F, 0.6F, 0.7F, 1.15F, Float.MAX_VALUE, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
      );
      THUNDER_SWEEPING_EDGE = builder.nextAccessor(
         "biped/epicfight_clone/thunder_sweeping_edge",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.1F, 0.0F, 0.15F, 0.3F, 0.8F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
               .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 1)
               .addProperty(StaticAnimationProperty.POSE_MODIFIER, ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.1F, (livingEntityPatch, assetAccessor, objects) -> {
                  LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
                  if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
                     ElectricPhaseEntity.spawnOnOwnerSword(serverLevel, livingEntity);
                  }
               }, Side.BOTH)})
      );
      THUNDER_DANCING_EDGE = builder.nextAccessor(
         "biped/epicfight_clone/thunder_dancing_edge",
         accessor -> (AttackAnimation)new AttackAnimation(
                  0.1F,
                  accessor,
                  Armatures.BIPED,
                  new Phase[]{
                     new Phase(0.0F, 0.25F, 0.4F, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                     new Phase(0.4F, 0.4F, 0.5F, 0.55F, 0.6F, InteractionHand.OFF_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolL, null),
                     new Phase(0.6F, 0.6F, 0.7F, 1.15F, Float.MAX_VALUE, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                  }
               )
               .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addEvents(new AnimationEvent[]{InTimeEvent.create(0.1F, (livingEntityPatch, assetAccessor, objects) -> {
                  LivingEntity livingEntity = (LivingEntity)livingEntityPatch.getOriginal();
                  if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
                     ElectricPhaseEntity.spawnOnOwnerSword(serverLevel, livingEntity);
                     if (livingEntity.m_21206_().m_41720_() instanceof ThunderDiamondBladeItem) {
                        ElectricPhaseEntity.spawnOnOwnerSword(serverLevel, livingEntity, true);
                     }
                  }
               }, Side.BOTH)})
      );
   }
}
