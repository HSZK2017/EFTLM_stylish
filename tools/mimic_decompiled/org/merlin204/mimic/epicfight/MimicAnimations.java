package org.merlin204.mimic.epicfight;

import com.merlin204.avalon.epicfight.animations.AvalonMovementAnimation;
import com.merlin204.avalon.util.AvalonEventUtils;
import com.merlin204.avalon.util.AvalonParticleUtils;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.merlin204.mimic.entity.proteus.ProteusEntity;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.AnimationManager.AnimationRegistryEvent;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InPeriodEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;

@EventBusSubscriber(
   modid = "mimic",
   bus = Bus.MOD
)
public class MimicAnimations {
   public static final Function<DamageSource, ResultType> DODGEABLE_SOURCE_VALIDATOR = damagesource -> damagesource != null
            && damagesource.m_7639_() != null
            && !damagesource.m_269533_(DamageTypeTags.f_268415_)
            && !damagesource.m_276093_(DamageTypes.f_268515_)
            && !damagesource.m_269533_(DamageTypeTags.f_268490_)
            && !damagesource.m_269533_(DamageTypeTags.f_268738_)
            && !damagesource.m_269533_(EpicFightDamageTypeTags.BYPASS_DODGE)
         ? ResultType.MISSED
         : ResultType.SUCCESS;
   public static AnimationAccessor<StaticAnimation> IDLE;
   public static AnimationAccessor<AvalonMovementAnimation> WALK;
   public static AnimationAccessor<StaticAnimation> IDLE_END;
   public static AnimationAccessor<AvalonMovementAnimation> WALK_END;
   public static AnimationAccessor<StaticAnimation> DEATH;
   public static AnimationAccessor<ActionAnimation> SKILL_1;
   public static AnimationAccessor<ActionAnimation> SKILL_2;
   public static AnimationAccessor<ActionAnimation> SKILL_3;
   public static AnimationAccessor<ActionAnimation> PHASE_2;
   public static AnimationAccessor<ActionAnimation> COME;

   public static void buildAnimations(AnimationBuilder builder) {
      ArmatureAccessor<HumanoidArmature> armature = Armatures.BIPED;
      IDLE = builder.nextAccessor("idle", accessor -> new StaticAnimation(0.1F, true, accessor, armature));
      WALK = builder.nextAccessor("walk", accessor -> new AvalonMovementAnimation(0.1F, true, accessor, armature, 1.9F));
      IDLE_END = builder.nextAccessor("idle_end", accessor -> new StaticAnimation(0.1F, true, accessor, armature));
      WALK_END = builder.nextAccessor("walk_end", accessor -> new AvalonMovementAnimation(0.1F, true, accessor, armature, 1.9F));
      DEATH = builder.nextAccessor(
         "death",
         accessor -> new StaticAnimation(0.1F, false, accessor, armature)
               .addEvents(
                  new AnimationEvent[]{
                     InPeriodEvent.create(
                        0.16666667F,
                        1.5F,
                        (entityPatch, self, params) -> AvalonParticleUtils.createJointSphereParticles(
                              entityPatch, ((HumanoidArmature)armature.get()).chest, ParticleTypes.f_123755_, 0.1, 0.1, 0.2, 50
                           ),
                        Side.CLIENT
                     ),
                     InPeriodEvent.create(
                        1.5F,
                        3.0F,
                        (entityPatch, self, params) -> AvalonParticleUtils.createJointSphereParticles(
                              entityPatch, ((HumanoidArmature)armature.get()).chest, ParticleTypes.f_123755_, 0.1, 0.2, 0.5, 50
                           ),
                        Side.CLIENT
                     )
                  }
               )
      );
      SKILL_1 = builder.nextAccessor(
         "skill_1",
         accessor -> (ActionAnimation)new ActionAnimation(0.1F, accessor, armature)
               .newTimePair(0.0F, 0.5F)
               .addState(EntityState.ATTACK_RESULT, DODGEABLE_SOURCE_VALIDATOR)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.16666667F,
                        (entityPatch, self, params) -> AvalonParticleUtils.createJointSphereParticles(
                              entityPatch, ((HumanoidArmature)armature.get()).chest, ParticleTypes.f_123755_, 0.1, 0.1, 0.2, 100
                           ),
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        0.9166667F,
                        (entityPatch, self, params) -> AvalonParticleUtils.createJointSphereParticles(
                              entityPatch, ((HumanoidArmature)armature.get()).chest, ParticleTypes.f_123755_, 0.1, 0.1, 0.2, 100
                           ),
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        0.5F,
                        (entityPatch, self, params) -> {
                           LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                           LivingEntity targetEntity = entityPatch.getTarget();
                           if (entity != null && targetEntity != null) {
                              Vec3 target = targetEntity.m_20182_();
                              entity.m_6034_(
                                 target.f_82479_ + (double)(3.0F * (entity.m_217043_().m_188501_() - 0.5F)),
                                 target.f_82480_,
                                 target.f_82481_ + (double)(3.0F * (entity.m_217043_().m_188501_() - 0.5F))
                              );
                              entityPatch.rotateTo(targetEntity, 360.0F, true);
                           }
                        },
                        Side.SERVER
                     )
                  }
               )
      );
      SKILL_2 = builder.nextAccessor(
         "skill_2",
         accessor -> (ActionAnimation)new ActionAnimation(0.1F, accessor, armature)
               .addEvents(
                  new AnimationEvent[]{
                     InTimeEvent.create(
                        0.0F,
                        (entityPatch, self, params) -> AvalonParticleUtils.createJointSphereParticles(
                              entityPatch, ((HumanoidArmature)armature.get()).chest, ParticleTypes.f_123755_, 0.1, 0.1, 0.2, 100
                           ),
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        0.41666666F,
                        (entityPatch, self, params) -> AvalonParticleUtils.createJointSphereParticles(
                              entityPatch, ((HumanoidArmature)armature.get()).chest, ParticleTypes.f_123755_, 0.1, 0.1, 0.2, 100
                           ),
                        Side.CLIENT
                     ),
                     InTimeEvent.create(0.5F, (entityPatch, self, params) -> {
                        LivingEntity target = entityPatch.getTarget();
                        if (target != null) {
                           entityPatch.rotateTo(target, 360.0F, true);
                        }
                     }, Side.SERVER)
                  }
               )
      );
      SKILL_3 = builder.nextAccessor(
         "skill_3",
         accessor -> (ActionAnimation)new ActionAnimation(0.1F, accessor, armature)
               .newTimePair(0.0F, 0.8333333F)
               .addState(EntityState.ATTACK_RESULT, DODGEABLE_SOURCE_VALIDATOR)
               .addEvents(
                  new AnimationEvent[]{
                     AvalonEventUtils.simpleGroundSplit(50, 0.0, 0.0, 0.0, 0.0, 5.0F, false),
                     InTimeEvent.create(
                        0.0F,
                        (entityPatch, self, params) -> AvalonParticleUtils.createJointSphereParticles(
                              entityPatch, ((HumanoidArmature)armature.get()).chest, ParticleTypes.f_123755_, 0.1, 0.2, 0.4, 100
                           ),
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        0.75F,
                        (entityPatch, self, params) -> AvalonParticleUtils.createJointSphereParticles(
                              entityPatch, ((HumanoidArmature)armature.get()).chest, ParticleTypes.f_123755_, 0.1, 0.2, 0.4, 100
                           ),
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        0.8333333F,
                        (entityPatch, self, params) -> AvalonParticleUtils.createJointSphereParticles(
                              entityPatch, ((HumanoidArmature)armature.get()).chest, ParticleTypes.f_123755_, 1.0, 0.3, 0.3, 100
                           ),
                        Side.CLIENT
                     ),
                     InTimeEvent.create(
                        0.33333334F,
                        (entityPatch, self, params) -> {
                           LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                           LivingEntity targetEntity = entityPatch.getTarget();
                           if (entity != null && targetEntity != null) {
                              Vec3 target = targetEntity.m_20182_();
                              entity.m_6034_(
                                 target.f_82479_ + (double)(1.0F * (entity.m_217043_().m_188501_() - 0.5F)),
                                 target.f_82480_,
                                 target.f_82481_ + (double)(1.0F * (entity.m_217043_().m_188501_() - 0.5F))
                              );
                              entityPatch.rotateTo(targetEntity, 360.0F, true);
                           }
                        },
                        Side.SERVER
                     )
                  }
               )
      );
      PHASE_2 = builder.nextAccessor(
         "phase_2",
         accessor -> (ActionAnimation)new ActionAnimation(0.1F, accessor, armature)
               .newTimePair(0.0F, Float.MAX_VALUE)
               .addState(EntityState.ATTACK_RESULT, DODGEABLE_SOURCE_VALIDATOR)
               .addEvents(
                  new AnimationEvent[]{
                     AvalonEventUtils.simpleGroundSplit(265, 0.0, 0.0, 0.0, 0.0, 8.0F, false),
                     InTimeEvent.create(0.0F, (entityPatch, self, params) -> {
                        if (entityPatch.getOriginal() instanceof ProteusEntity proteus) {
                           proteus.setPhase(2);
                        }
                     }, Side.BOTH),
                     InTimeEvent.create(
                        3.0F,
                        (entityPatch, self, params) -> {
                           if (entityPatch.getOriginal() instanceof ProteusEntity proteus && proteus.getPhase() == 2) {
                              Animator animator = entityPatch.getAnimator();
                              AnimationPlayer animationPlayer = animator == null ? null : animator.getPlayerFor(null);
                              if (animationPlayer != null) {
                                 animationPlayer.setElapsedTime(2.0F);
                              }

                              if (proteus.m_9236_().f_46443_) {
                                 AvalonParticleUtils.createJointSphereParticles(
                                    entityPatch, ((HumanoidArmature)armature.get()).chest, ParticleTypes.f_123755_, 1.0, 0.2, 0.2, 100
                                 );
                              } else {
                                 LivingEntity target = proteus.m_5448_();
                                 if (target != null && target.m_20182_().m_82554_(proteus.m_20182_()) > 30.0) {
                                    target.m_146884_(proteus.m_20182_());
                                 }
                              }
                           }
                        },
                        Side.BOTH
                     ),
                     InTimeEvent.create(4.4166665F, (entityPatch, self, params) -> {
                        if (((LivingEntity)entityPatch.getOriginal()).m_9236_().f_46443_) {
                           Player player = Minecraft.m_91087_().f_91074_;
                           if (player instanceof LocalPlayer) {
                              Component title = Component.m_237115_("entity.mimic.proteus_3").m_130940_(ChatFormatting.DARK_RED);
                              Minecraft.m_91087_().f_91065_.m_168714_(title);
                              Minecraft.m_91087_().f_91065_.m_168684_(20, 60, 20);
                           }
                        }
                     }, Side.CLIENT),
                     InTimeEvent.create(
                        4.5F,
                        (entityPatch, self, params) -> AvalonParticleUtils.createJointSphereParticles(
                              entityPatch, ((HumanoidArmature)armature.get()).chest, ParticleTypes.f_123755_, 2.0, 0.5, 0.5, 300
                           ),
                        Side.CLIENT
                     )
                  }
               )
      );
      COME = builder.nextAccessor(
         "come",
         accessor -> (ActionAnimation)new ActionAnimation(0.1F, accessor, armature)
               .newTimePair(0.0F, Float.MAX_VALUE)
               .addState(EntityState.ATTACK_RESULT, DODGEABLE_SOURCE_VALIDATOR)
               .addEvents(
                  new AnimationEvent[]{
                     InPeriodEvent.create(
                        0.0F,
                        2.8333333F,
                        (entityPatch, self, params) -> AvalonParticleUtils.createJointSphereParticles(
                              entityPatch, ((HumanoidArmature)armature.get()).chest, ParticleTypes.f_123755_, 0.1, 0.1, 0.2, 50
                           ),
                        Side.CLIENT
                     ),
                     InPeriodEvent.create(
                        2.8333333F,
                        3.0F,
                        (entityPatch, self, params) -> AvalonParticleUtils.createJointSphereParticles(
                              entityPatch, ((HumanoidArmature)armature.get()).chest, ParticleTypes.f_123755_, 0.1, 0.2, 0.5, 50
                           ),
                        Side.CLIENT
                     ),
                     InTimeEvent.create(0.5F, (entityPatch, self, params) -> {
                        if (((LivingEntity)entityPatch.getOriginal()).m_9236_().f_46443_) {
                           Player player = Minecraft.m_91087_().f_91074_;
                           if (player instanceof LocalPlayer) {
                              Component title = Component.m_237115_("entity.mimic.proteus_1").m_130940_(ChatFormatting.DARK_RED);
                              Minecraft.m_91087_().f_91065_.m_168714_(title);
                              Minecraft.m_91087_().f_91065_.m_168684_(20, 60, 20);
                           }
                        }
                     }, Side.CLIENT)
                  }
               )
      );
   }

   @OnlyIn(Dist.CLIENT)
   private static void sendTitle() {
      Player player = Minecraft.m_91087_().f_91074_;
      if (player instanceof LocalPlayer) {
         Component title = Component.m_237113_("").m_130940_(ChatFormatting.DARK_RED);
         Component subtitle = Component.m_237113_("你的暗红色消息").m_130940_(ChatFormatting.DARK_RED);
         player.m_213846_(Component.m_237113_("消息"));
         Minecraft.m_91087_().f_91065_.m_168714_(title);
         Minecraft.m_91087_().f_91065_.m_168711_(subtitle);
         Minecraft.m_91087_().f_91065_.m_168684_(20, 60, 20);
      }
   }

   @SubscribeEvent
   public static void registerAnimations(AnimationRegistryEvent event) {
      event.newBuilder("mimic", MimicAnimations::buildAnimations);
   }
}
