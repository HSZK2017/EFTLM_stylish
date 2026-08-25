package com.pla.annoyingvillagers.combatbehaviour;

import com.pla.annoyingvillagers.entity.BlueDemonEntity;
import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import com.pla.annoyingvillagers.gameasset.AnimsWom;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.item.LegendarySwordItem;
import com.pla.annoyingvillagers.task.DelayedTask;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.Behavior;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.BehaviorRoot;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.Builder;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.gameasset.animations.weapons.AnimsAgony;
import reascer.wom.gameasset.animations.weapons.AnimsEnderblaster;
import reascer.wom.gameasset.animations.weapons.AnimsRuine;
import reascer.wom.gameasset.animations.weapons.AnimsSolar;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.effect.EpicFightMobEffects;

public class BlueDemonLegendarySword {
   public static final Builder<MobPatch<?>> LEGENDARY_SWORD = CECombatBehaviors.builder()
      .newBehaviorRoot(CombatBehaviourTemplates.executionRoot())
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(3.0)
            .weight(1000.0)
            .maxCooldown(0)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::canPerformNormalAttackLogic)
                  .custom(CombatCommon::canEscape)
                  .withinDistance(0.0, 8.0)
                  .animationBehavior(Animations.BIPED_STEP_BACKWARD, 0.0F)
                  .addExBehavior(CombatCommon::performEscapeRunAway)
            )
            .addFirstBehavior(Behavior.builder().custom(CombatCommon::canEscape).withinDistance(0.0, 48.0).guard(40))
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(3.0)
            .weight(100.0)
            .maxCooldown(120)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::canPerformNormalAttackLogic)
                  .custom(CombatCommon::canSwitchWeapon)
                  .animationBehavior(Animations.BIPED_ROLL_BACKWARD, 0.0F)
                  .addExBehavior(CombatCommon::switchWeapon)
            )
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::canPerformNormalAttackLogic)
                  .custom(CombatCommon::canSwitchWeapon)
                  .animationBehavior(Animations.BIPED_ROLL_FORWARD, 0.0F)
                  .addExBehavior(CombatCommon::switchWeapon)
            )
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::canPerformNormalAttackLogic)
                  .custom(CombatCommon::canSwitchWeapon)
                  .animationBehavior(Animations.BIPED_STEP_BACKWARD, 0.0F)
                  .addExBehavior(CombatCommon::switchWeapon)
            )
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::canPerformNormalAttackLogic)
                  .custom(CombatCommon::canSwitchWeapon)
                  .animationBehavior(Animations.BIPED_STEP_FORWARD, 0.0F)
                  .addExBehavior(CombatCommon::switchWeapon)
            )
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::canPerformNormalAttackLogic)
                  .custom(CombatCommon::canSwitchWeapon)
                  .animationBehavior(Animations.BIPED_STEP_RIGHT, 0.0F)
                  .addExBehavior(CombatCommon::switchWeapon)
            )
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::canPerformNormalAttackLogic)
                  .custom(CombatCommon::canSwitchWeapon)
                  .animationBehavior(Animations.BIPED_STEP_LEFT, 0.0F)
                  .addExBehavior(CombatCommon::switchWeapon)
            )
      )
      .newBehaviorRoot(
         CombatCommon.addRandomCombatChains(
            BehaviorRoot.builder().priority(1.0).weight(40.0).maxCooldown(20),
            CombatCommon.animations(
               WOMAnimations.TORMENT_AUTO_1,
               WOMAnimations.TORMENT_AUTO_2,
               AnimsSolar.SOLAR_AUTO_1,
               AnimsPugilistSteve.LEGENDARY_SWORD_AUTO_4,
               AnimsPugilistSteve.LEGENDARY_SWORD_WAKE_UP_ATTACK,
               AnimsWom.YELLOW_SOLAR_AUTO_2,
               AnimsWom.YELLOW_NAPOLEON_AUTO_3,
               AnimsWom.DEMONIAC_TORMENT_CHARGED_ATTACK_2
            ),
            CombatCommon.animations(AnimsEnderblaster.ENDERBLASTER_TWOHAND_TISHNAW),
            CombatCommon.animations(
               AnimsSolar.SOLAR_AUTO_4,
               WOMAnimations.TORMENT_AIRSLAM,
               WOMAnimations.TORMENT_AUTO_3,
               WOMAnimations.TORMENT_AUTO_4,
               WOMAnimations.TORMENT_DASH,
               AnimsWom.DEMONIAC_RUINE_AUTO_1,
               AnimsWom.DEMONIAC_RUINE_AUTO_4,
               AnimsWom.DEMONIAC_RUINE_AUTO_2,
               AnimsRuine.RUINE_CHATIMENT,
               WOMAnimations.TORMENT_BERSERK_DASH,
               WOMAnimations.TORMENT_BERSERK_AUTO_1,
               WOMAnimations.TORMENT_BERSERK_AUTO_2,
               WOMAnimations.TORMENT_BERSERK_AIRSLAM,
               Animations.GREATSWORD_AUTO1,
               Animations.GREATSWORD_AUTO2,
               Animations.THE_GUILLOTINE,
               WOMAnimations.TORMENT_CHARGED_ATTACK_1,
               WOMAnimations.TORMENT_CHARGED_ATTACK_3,
               AnimsSolar.SOLAR_AUTO_2,
               AnimsAgony.AGONY_RIPPING_FANGS,
               AnimsAgony.AGONY_AUTO_3,
               AnimsWom.ELECTRIC_FIELD,
               AnimsWom.DEMONIAC_RUINE_COMET,
               AnimsWom.CLONE_ENDERBLASTER_ONEHAND_DASH,
               AnimsWom.YELLOW_NAPOLEON_AUSTERLITZ_SHOOT,
               AnimsSolar.SOLAR_OBSCURIDAD_AUTO_4,
               AnimsWom.CLONE_NAPOLEON_WATERLOW_SHOOT
            ),
            CombatCommon.rollStepAnimations()
         )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(30.0)
            .maxCooldown(40)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::canPerformNormalAttackLogic)
                  .withinDistance(0.0, 5.0)
                  .animationBehavior(WOMAnimations.TORMENT_BERSERK_DASH, 0.0F)
                  .addNextBehavior(
                     Behavior.builder()
                        .custom(CombatCommon::canPerformNormalAttackLogic)
                        .withinDistance(0.0, 5.0)
                        .animationBehavior(WOMAnimations.TORMENT_BERSERK_DASH, 0.0F)
                        .addNextBehavior(
                           Behavior.builder()
                              .custom(CombatCommon::canPerformNormalAttackLogic)
                              .withinDistance(0.0, 5.0)
                              .animationBehavior(WOMAnimations.TORMENT_BERSERK_DASH, 0.0F)
                              .addNextBehavior(
                                 Behavior.builder()
                                    .custom(CombatCommon::canPerformNormalAttackLogic)
                                    .withinDistance(0.0, 5.0)
                                    .animationBehavior(WOMAnimations.TORMENT_BERSERK_DASH, 0.0F)
                              )
                        )
                  )
            )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(30.0)
            .maxCooldown(40)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::canPerformNormalAttackLogic)
                  .withinDistance(0.0, 5.0)
                  .animationBehavior(WOMAnimations.TORMENT_DASH, 0.0F)
                  .addNextBehavior(
                     Behavior.builder()
                        .custom(CombatCommon::canPerformNormalAttackLogic)
                        .withinDistance(0.0, 5.0)
                        .animationBehavior(WOMAnimations.TORMENT_DASH, 0.0F)
                  )
            )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(30.0)
            .maxCooldown(40)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::canPerformNormalAttackLogic)
                  .withinDistance(0.0, 5.0)
                  .animationBehavior(AnimsWom.YELLOW_NAPOLEON_AUSTERLITZ_SHOOT, 0.0F)
                  .addNextBehavior(
                     Behavior.builder()
                        .custom(CombatCommon::canPerformNormalAttackLogic)
                        .withinDistance(0.0, 5.0)
                        .animationBehavior(AnimsWom.YELLOW_NAPOLEON_AUSTERLITZ_SHOOT, 0.0F)
                  )
            )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(30.0)
            .maxCooldown(100)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::canPerformNormalAttackLogic)
                  .withinDistance(0.0, 5.0)
                  .animationBehavior(AnimsWom.CLONE_NAPOLEON_WATERLOW_SHOOT, 0.0F)
                  .addExBehavior(BlueDemonLegendarySword::legendarySwordSpecialAttack)
            )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(20.0)
            .maxCooldown(200)
            .addFirstBehavior(
               Behavior.builder()
                  .custom(CombatCommon::canPerformNormalAttackLogic)
                  .withinDistance(0.0, 5.0)
                  .animationBehavior(AnimsAgony.AGONY_RISING_EAGLE, 0.0F)
                  .addExBehavior(BlueDemonLegendarySword::legendarySwordHeavyAttack)
            )
      )
      .newBehaviorRoot(
         BehaviorRoot.builder()
            .priority(1.0)
            .weight(10.0)
            .maxCooldown(400)
            .addFirstBehavior(
               Behavior.builder().custom(CombatCommon::canPerformNormalAttackLogic).withinDistance(5.0, 10.0).animationBehavior(AnimsWom.ELECTRIC_FIELD, 0.0F)
            )
      )
      .newBehaviorRoot(CombatBehaviourTemplates.guardRoot())
      .newBehaviorRoot(CombatBehaviourTemplates.jumpRoot());

   static void legendarySwordHeavyAttack(final MobPatch<?> mobpatch) {
      final BlueDemonEntity blueDemonEntity = (BlueDemonEntity)mobpatch.getOriginal();
      ItemStack itemStack = blueDemonEntity.m_21205_();
      if (itemStack.m_41720_() instanceof LegendarySwordItem && blueDemonEntity.m_9236_() instanceof ServerLevel serverLevel) {
         blueDemonEntity.m_7292_(new MobEffectInstance((MobEffect)EpicFightMobEffects.STUN_IMMUNITY.get(), 60, 2));
         new DelayedTask(10) {
            @Override
            public void run() {
               serverLevel.m_6263_(
                  null,
                  blueDemonEntity.m_20185_(),
                  blueDemonEntity.m_20186_(),
                  blueDemonEntity.m_20189_(),
                  (SoundEvent)AnnoyingVillagersModSounds.HEAVY_ATTACK_START.get(),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
               serverLevel.m_6263_(
                  null,
                  blueDemonEntity.m_20185_(),
                  blueDemonEntity.m_20186_(),
                  blueDemonEntity.m_20189_(),
                  (SoundEvent)AnnoyingVillagersModSounds.HEAVY_ATTACK_LEGENDARY_SWORD.get(),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
               serverLevel.m_6263_(
                  null,
                  blueDemonEntity.m_20185_(),
                  blueDemonEntity.m_20186_(),
                  blueDemonEntity.m_20189_(),
                  (SoundEvent)AnnoyingVillagersModSounds.HEAVY_ATTACK_LEGENDARY_SWORD_2.get(),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
               serverLevel.m_8767_(
                  ParticleTypes.f_123767_, blueDemonEntity.m_20185_(), blueDemonEntity.m_20186_(), blueDemonEntity.m_20189_(), 15, 0.0, 0.0, 0.0, 0.2
               );
               serverLevel.m_8767_(
                  ParticleTypes.f_123767_, blueDemonEntity.m_20185_(), blueDemonEntity.m_20188_(), blueDemonEntity.m_20189_(), 100, 0.0, 0.0, 0.0, 0.5
               );
               mobpatch.playAnimationSynchronized(AnimsPugilistSteve.LEGENDARY_SWORD_HEAVY_ATTACK, 0.0F);
            }
         };
      }
   }

   static void legendarySwordSpecialAttack(final MobPatch<?> mobpatch) {
      BlueDemonEntity blueDemonEntity = (BlueDemonEntity)mobpatch.getOriginal();
      ItemStack itemStack = blueDemonEntity.m_21205_();
      if (itemStack.m_41720_() instanceof LegendarySwordItem && blueDemonEntity.m_9236_() instanceof ServerLevel) {
         blueDemonEntity.m_7292_(new MobEffectInstance((MobEffect)EpicFightMobEffects.STUN_IMMUNITY.get(), 60, 2));
         new DelayedTask(20) {
            @Override
            public void run() {
               mobpatch.playAnimationSynchronized(AnimsPugilistSteve.LEGENDARY_SWORD_WAKE_UP_ATTACK, 0.0F);
            }
         };
      }
   }
}
