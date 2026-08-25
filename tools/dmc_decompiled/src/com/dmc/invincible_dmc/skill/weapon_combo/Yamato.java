package com.dmc.invincible_dmc.skill.weapon_combo;

import com.dmc.invincible_dmc.InvincibleMod_DMC;
import com.dmc.invincible_dmc.api.skill.ActionTag;
import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.api.skill.ComboNodeGroup;
import com.dmc.invincible_dmc.api.skill.CrazyComboNode;
import com.dmc.invincible_dmc.api.skill.HitExtendNode;
import com.dmc.invincible_dmc.api.skill.JudgementCutNode;
import com.dmc.invincible_dmc.api.skill.SubJudgementCutNode;
import com.dmc.invincible_dmc.api.skill.TapHoldNode;
import com.dmc.invincible_dmc.api.weapon.DmcWeaponType;
import com.dmc.invincible_dmc.api.weapon.WeaponActionChainRegistry;
import com.dmc.invincible_dmc.api.weapon.WeaponCombatProfile;
import com.dmc.invincible_dmc.capability.weapon.WeaponActionType;
import com.dmc.invincible_dmc.client.input.PlayerInputState;
import com.dmc.invincible_dmc.conditions.AerialAttackLimitCondition;
import com.dmc.invincible_dmc.conditions.AirborneCondition;
import com.dmc.invincible_dmc.conditions.AnimationElapsedTimeCondition;
import com.dmc.invincible_dmc.conditions.ConcentrationTierCondition;
import com.dmc.invincible_dmc.conditions.DirectionCondition;
import com.dmc.invincible_dmc.conditions.DirectionalSequenceCondition;
import com.dmc.invincible_dmc.conditions.GroundedCondition;
import com.dmc.invincible_dmc.conditions.InstantJudgementCutEndEnabledCondition;
import com.dmc.invincible_dmc.conditions.PlayerOnlyCondition;
import com.dmc.invincible_dmc.conditions.SDTCondition;
import com.dmc.invincible_dmc.conditions.SDTConsumeCondition;
import com.dmc.invincible_dmc.conditions.SDTKeyCondition;
import com.dmc.invincible_dmc.conditions.YamatoDodgeCounterSuccessCondition;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.event.ComboNodeEvents;
import com.dmc.invincible_dmc.gameassets.DmcWeaponProfiles;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.skill.dodge.VergilDodgeSkill;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.dmc.invincible_dmc.utils.vfx.CameraFovUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.api.forgeevent.SkillBuildEvent.ModRegistryWorker;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@EventBusSubscriber(
   modid = "invincible_dmc",
   bus = Bus.MOD
)
public class Yamato {
   private static final int COMBO_RESET_TICKS = 11;
   private static final float RAPID_SLASH_LOOP_START = 0.16666667F;
   private static final float RAPID_SLASH_LOOP_END = 0.48333332F;
   private static final float RAPID_SLASH_REPEAT_LOOP_START = 0.033333335F;
   private static final float RAPID_SLASH_REPEAT_LOOP_END = 0.31666666F;
   public static Skill YAMATO;
   public static Skill YAMATO_DODGE;

   public static boolean tryLoopSdtRapidSlash(LivingEntityPatch<?> executorPatch, ComboNode attemptedNode) {
      if (executorPatch != null && !executorPatch.isLogicalClient() && isRapidSlashNode(attemptedNode) && isSdtRapidSlashLoopWindow(executorPatch)) {
         executorPatch.playAnimationInstantly(YamatoAnimations.YAMATO_RAPIDSLASH_RE);
         return true;
      } else {
         return false;
      }
   }

   public static boolean isSdtRapidSlashLoopWindow(LivingEntityPatch<?> executorPatch) {
      if (executorPatch != null && isSdtExecutor(executorPatch)) {
         AnimationPlayer animationPlayer = DMCAnimationUtils.getMainPlayer(executorPatch);
         if (animationPlayer == null) {
            return false;
         } else {
            float elapsedTime = animationPlayer.getElapsedTime();
            if (DMCAnimationUtils.sameAccessor(animationPlayer.getRealAnimation(), YamatoAnimations.YAMATO_RAPIDSLASH)) {
               return elapsedTime >= 0.16666667F && elapsedTime <= 0.48333332F;
            } else {
               return !DMCAnimationUtils.sameAccessor(animationPlayer.getRealAnimation(), YamatoAnimations.YAMATO_RAPIDSLASH_RE)
                  ? false
                  : elapsedTime >= 0.033333335F && elapsedTime <= 0.31666666F;
            }
         }
      } else {
         return false;
      }
   }

   public static boolean canRequestSdtRapidSlashLoop(LivingEntityPatch<?> executorPatch, ComboNode routedNode) {
      return isSdtRapidSlashLoopWindow(executorPatch) && containsRapidSlashNode(routedNode) && isRapidSlashDirectionHeld(executorPatch);
   }

   public static boolean isRapidSlashNode(ComboNode attemptedNode) {
      AnimationAccessor var10000;
      label15: {
         if (attemptedNode instanceof HitExtendNode hitExtendNode && hitExtendNode.getBase() != null) {
            var10000 = hitExtendNode.getBase().getAnimationAccessor();
            break label15;
         }

         var10000 = attemptedNode != null ? attemptedNode.getAnimationAccessor() : null;
      }

      AnimationAccessor<? extends StaticAnimation> animation = var10000;
      return DMCAnimationUtils.sameAccessor(animation, YamatoAnimations.YAMATO_RAPIDSLASH);
   }

   private static boolean containsRapidSlashNode(ComboNode node) {
      if (node == null) {
         return false;
      } else if (isRapidSlashNode(node)) {
         return true;
      } else {
         for (ComboNode conditionNode : node.getConditionNodes()) {
            if (containsRapidSlashNode(conditionNode)) {
               return true;
            }
         }

         return false;
      }
   }

   private static boolean isRapidSlashDirectionHeld(LivingEntityPatch<?> executorPatch) {
      Player owner = getSdtOwner(executorPatch);
      if (owner == null) {
         return false;
      } else {
         return executorPatch.isLogicalClient() ? PlayerInputState.isLocalDown(0) : PlayerInputState.isRemoteDown(owner, 0);
      }
   }

   private static boolean isSdtExecutor(LivingEntityPatch<?> executorPatch) {
      Player owner = getSdtOwner(executorPatch);
      return SinDevilTriggerManager.isPlayerInSDT(owner);
   }

   private static Player getSdtOwner(LivingEntityPatch<?> executorPatch) {
      Entity var2 = executorPatch.getOriginal();
      if (var2 instanceof Player) {
         return (Player)var2;
      } else {
         return executorPatch.getOriginal() instanceof DoppelgangerEntity doppelganger ? doppelganger.getOwner() : null;
      }
   }

   @SubscribeEvent
   public static void buildSkills(SkillBuildEvent event) {
      ModRegistryWorker registryWorker = event.createRegistryWorker("invincible_dmc");
      ComboNode root = ComboNode.create();
      JudgementCutNode instant_judgement_cut_end = JudgementCutNode.createNode(YamatoAnimations.YAMATO_JUDGEMENT_CUT_END_INSTANT)
         .addCondition(new GroundedCondition())
         .addCondition(new SDTConsumeCondition(1000.0F, 300.0F))
         .addCondition(new ConcentrationTierCondition(2, 2))
         .addCondition(new DirectionCondition(DirectionCondition.Direction.UP))
         .addCondition(new InstantJudgementCutEndEnabledCondition())
         .setPriority(99999999);
      JudgementCutNode judgement_cut_end = JudgementCutNode.createNode(YamatoAnimations.YAMATO_JUDGEMENT_CUT_END)
         .addCondition(new GroundedCondition())
         .addCondition(new SDTConsumeCondition(1000.0F, 300.0F))
         .addCondition(new ConcentrationTierCondition(2, 2))
         .addCondition(new DirectionCondition(DirectionCondition.Direction.UP))
         .setPriority(10);
      JudgementCutNode judgement_cut_end_air = JudgementCutNode.createNode(YamatoAnimations.YAMATO_JUDGEMENT_CUT_END_DMC3)
         .addCondition(new SDTConsumeCondition(1000.0F, 300.0F))
         .addCondition(new ConcentrationTierCondition(2, 2))
         .addCondition(new DirectionCondition(DirectionCondition.Direction.UP))
         .addCondition(new AirborneCondition(0.5F))
         .setPriority(15);
      JudgementCutNode execution = JudgementCutNode.createNode(YamatoAnimations.YAMATO_EXECUTION_DASH)
         .addCondition(new GroundedCondition())
         .addCondition(new PlayerOnlyCondition())
         .addCondition(new ConcentrationTierCondition(2, 2))
         .addCondition(new SDTConsumeCondition(500.0F, 300.0F))
         .addCondition(new DirectionCondition(DirectionCondition.Direction.DOWN))
         .setPriority(10);
      ComboNode provocation_a = ComboNode.createNode(YamatoAnimations.YAMATO_PROVOCATION_A)
         .addCondition(new GroundedCondition())
         .addCondition(new DirectionCondition(DirectionCondition.Direction.UP))
         .addCondition(new PlayerOnlyCondition())
         .setPriority(20);
      ComboNode provocation_a_aerial = ComboNode.createNode(YamatoAnimations.YAMATO_PROVOCATION_A_AERIAL)
         .addCondition(new AirborneCondition(0.5F))
         .addCondition(new PlayerOnlyCondition())
         .setPriority(30);
      ComboNode provocation_b = ComboNode.createNode(YamatoAnimations.YAMATO_PROVOCATION_B)
         .addCondition(new GroundedCondition())
         .addCondition(new PlayerOnlyCondition())
         .setPriority(10);
      ComboNode provocation_b_aerial = ComboNode.createNode(YamatoAnimations.YAMATO_PROVOCATION_B_AERIAL)
         .addCondition(new AirborneCondition(0.5F))
         .addCondition(new DirectionCondition(DirectionCondition.Direction.DOWN))
         .addCondition(new PlayerOnlyCondition())
         .setPriority(40);
      JudgementCutNode provocation_c = JudgementCutNode.createNode(YamatoAnimations.YAMATO_PROVOCATION_C)
         .addCondition(new GroundedCondition())
         .addCondition(new PlayerOnlyCondition())
         .addCondition(new SDTKeyCondition())
         .setPriority(30);
      JudgementCutNode provocation_d = JudgementCutNode.createNode(YamatoAnimations.YAMATO_PROVOCATION_D)
         .addCondition(new GroundedCondition())
         .addCondition(new DirectionCondition(DirectionCondition.Direction.DOWN))
         .addCondition(new PlayerOnlyCondition())
         .setPriority(20);
      JudgementCutNode provocation_portal = JudgementCutNode.createNode(YamatoAnimations.YAMATO_PROVOCATION_PORTAL)
         .addCondition(new GroundedCondition())
         .addCondition(new PlayerOnlyCondition())
         .addCondition(new DirectionalSequenceCondition(DirectionalSequenceCondition.Sequence.BACK_FORWARD))
         .setPriority(30);
      JudgementCutNode provocation_spine_a = JudgementCutNode.createNode(YamatoAnimations.YAMATO_PROVOCATION_SPINE_BLADE)
         .addCondition(new GroundedCondition())
         .addCondition(new PlayerOnlyCondition())
         .addCondition(new DirectionalSequenceCondition(DirectionalSequenceCondition.Sequence.LEFT_RIGHT))
         .setPriority(30);
      JudgementCutNode provocation_spine_b = JudgementCutNode.createNode(YamatoAnimations.YAMATO_PROVOCATION_SPINE_BLADE)
         .addCondition(new GroundedCondition())
         .addCondition(new PlayerOnlyCondition())
         .addCondition(new DirectionalSequenceCondition(DirectionalSequenceCondition.Sequence.RIGHT_LEFT))
         .setPriority(30);
      JudgementCutNode dodgeCounter_combo1 = JudgementCutNode.createNode(YamatoAnimations.YAMATO_DODGE_COUNTER)
         .addCondition(new GroundedCondition())
         .addCondition(new YamatoDodgeCounterSuccessCondition())
         .addCondition(new PlayerOnlyCondition())
         .setPriority(3);
      JudgementCutNode dodgeCounter_combo2 = JudgementCutNode.createNode(YamatoAnimations.YAMATO_COMBO_A_4_SDT)
         .addCondition(new GroundedCondition())
         .addCondition(new PlayerOnlyCondition())
         .setComboResetAtTime(1.5F)
         .setPriority(3);
      JudgementCutNode dodgeCounter_combo3 = JudgementCutNode.createNode(YamatoAnimations.YAMATO_COMBO_A_5_SDT)
         .addCondition(new GroundedCondition())
         .addCondition(new PlayerOnlyCondition())
         .setPriority(3);
      JudgementCutNode combo_a_1 = JudgementCutNode.createNode(YamatoAnimations.YAMATO_COMBO_A_1).addCondition(new GroundedCondition()).setPriority(1);
      JudgementCutNode combo_a_2 = JudgementCutNode.createNode(YamatoAnimations.YAMATO_COMBO_A_2).addCondition(new GroundedCondition()).setPriority(1);
      JudgementCutNode combo_a_3 = JudgementCutNode.createNode(YamatoAnimations.YAMATO_COMBO_A_3).addCondition(new GroundedCondition()).setPriority(1);
      JudgementCutNode combo_a_4 = JudgementCutNode.createNode(YamatoAnimations.YAMATO_COMBO_A_4).addCondition(new GroundedCondition()).setPriority(1);
      JudgementCutNode combo_a_4_sdt = JudgementCutNode.createNode(YamatoAnimations.YAMATO_COMBO_A_4_SDT)
         .addCondition(new GroundedCondition())
         .addCondition(new SDTCondition())
         .setComboResetAtTime(1.5F)
         .setPriority(2);
      JudgementCutNode combo_a_5_sdt = JudgementCutNode.createNode(YamatoAnimations.YAMATO_COMBO_A_5_SDT).addCondition(new GroundedCondition()).setPriority(1);
      JudgementCutNode combo_b_1 = JudgementCutNode.createNode(YamatoAnimations.YAMATO_COMBO_B_1)
         .addCondition(new GroundedCondition())
         .setPriority(5)
         .setBufferDurationTicks(10)
         .setComboResetAtTime(1.1F)
         .addCondition(new AnimationElapsedTimeCondition(0.55F, 1.1F));
      JudgementCutNode combo_b_2_sdt = JudgementCutNode.createNode(YamatoAnimations.YAMATO_COMBO_B_2_SDT)
         .addCondition(new GroundedCondition())
         .addCondition(new SDTCondition())
         .setPriority(3);
      SubJudgementCutNode comboCFinishDefault = SubJudgementCutNode.create(YamatoAnimations.YAMATO_COMBO_C_END);
      SubJudgementCutNode comboCFinishNoChaseDefault = SubJudgementCutNode.create(YamatoAnimations.YAMATO_COMBO_C_END);
      CrazyComboNode comboC = CrazyComboNode.create(SubJudgementCutNode.create(YamatoAnimations.YAMATO_COMBO_C_START))
         .addCondition(new GroundedCondition())
         .setCcChase(SubJudgementCutNode.create(YamatoAnimations.YAMATO_COMBO_C_LOOP))
         .setCcMaxChases(8)
         .setCcBaseRequiredPresses(3)
         .setCcChaseRequiredPresses(2)
         .setCcRapidMaxIntervalTicks(8)
         .setCcStartupFinishNoChasePhase(2)
         .addCondition(new AnimationElapsedTimeCondition(0.7F, 1.3F))
         .setPriority(5);
      ResourceLocation crazyComboChain = InvincibleMod_DMC.rl("yamato_crazy_combo");
      if (comboC.getCcBase() != null) {
         WeaponActionChainRegistry.register(
            crazyComboChain, DmcWeaponType.YAMATO, WeaponActionType.CRAZY_COMBO, comboC.getCcBase().getAnimationAccessor(), false
         );
      }

      if (comboC.getCcChase() != null) {
         WeaponActionChainRegistry.register(
            crazyComboChain, DmcWeaponType.YAMATO, WeaponActionType.CRAZY_COMBO, comboC.getCcChase().getAnimationAccessor(), false
         );
      }

      JudgementCutNode aerialrave_combo_a_1 = JudgementCutNode.createNode(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_1)
         .addCondition(new AerialAttackLimitCondition(2))
         .addCondition(new AirborneCondition(0.5F))
         .setPriority(2);
      JudgementCutNode aerialrave_combo_a_2 = JudgementCutNode.createNode(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_2)
         .addCondition(new AirborneCondition(0.5F))
         .setPriority(2);
      JudgementCutNode aerialrave_combo_a_3 = JudgementCutNode.createNode(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_3)
         .addCondition(new AirborneCondition(0.5F))
         .setPriority(2);
      JudgementCutNode aerialrave_combo_b_1 = JudgementCutNode.createNode(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_B_1)
         .addCondition(new AirborneCondition(0.5F))
         .addCondition(new AnimationElapsedTimeCondition(0.45F, 0.9F))
         .setPriority(5);
      JudgementCutNode aerialrave_combo_b_2 = JudgementCutNode.createNode(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_B_2)
         .addCondition(new AirborneCondition(0.5F))
         .setPriority(2);
      HitExtendNode rapidSlash = HitExtendNode.create(SubJudgementCutNode.create(YamatoAnimations.YAMATO_RAPIDSLASH))
         .addCondition(new GroundedCondition())
         .setPriority(6)
         .addCondition(new DirectionCondition(DirectionCondition.Direction.UP))
         .addBaseAnimationAlias(YamatoAnimations.YAMATO_RAPIDSLASH_RE)
         .setStabilizeContact(true)
         .setExtend(SubJudgementCutNode.create(YamatoAnimations.YAMATO_RISINGSTAR));
      JudgementCutNode rapidSlash_air = JudgementCutNode.createNode(YamatoAnimations.YAMATO_RAPIDSLASH_AIR)
         .addCondition(new AirborneCondition(0.5F))
         .addCondition(new DirectionCondition(DirectionCondition.Direction.UP))
         .setPriority(8);
      TapHoldNode upperSlash = TapHoldNode.create(SubJudgementCutNode.create(YamatoAnimations.YAMATO_UPPERSLASH_1))
         .addCondition(new GroundedCondition())
         .setPriority(6)
         .addCondition(new DirectionCondition(DirectionCondition.Direction.DOWN))
         .setHold(SubJudgementCutNode.create(YamatoAnimations.YAMATO_UPPERSLASH_2).addCondition(new GroundedCondition()))
         .setWindupDurationTicks(8);
      JudgementCutNode aerialcleave = JudgementCutNode.createNode(YamatoAnimations.YAMATO_AERIALCLEAVE)
         .addCondition(new AirborneCondition(0.5F))
         .addCondition(new DirectionCondition(DirectionCondition.Direction.DOWN))
         .setPriority(6);
      JudgementCutNode aerialcleave_sdt = JudgementCutNode.createNode(YamatoAnimations.YAMATO_AERIALCLEAVE_FAST)
         .addCondition(new AirborneCondition(0.5F))
         .addCondition(new SDTCondition())
         .addCondition(new DirectionCondition(DirectionCondition.Direction.DOWN))
         .setPriority(8);
      JudgementCutNode voidSlash = JudgementCutNode.createNode(YamatoAnimations.YAMATO_VOID_SLASH)
         .setPriority(7)
         .addCondition(new GroundedCondition())
         .addCondition(new DirectionalSequenceCondition(DirectionalSequenceCondition.Sequence.BACK_FORWARD));
      comboC.setCcFinish(ComboNodeGroup.creatGroup(voidSlash, upperSlash, rapidSlash, comboCFinishDefault));
      comboC.setCcFinishNoChase(ComboNodeGroup.creatGroup(voidSlash, upperSlash, rapidSlash, comboCFinishNoChaseDefault));
      WeaponActionChainRegistry.register(crazyComboChain, DmcWeaponType.YAMATO, WeaponActionType.CRAZY_COMBO, comboCFinishDefault.getAnimationAccessor(), true);
      WeaponActionChainRegistry.register(
         crazyComboChain, DmcWeaponType.YAMATO, WeaponActionType.CRAZY_COMBO, comboCFinishNoChaseDefault.getAnimationAccessor(), true
      );
      JudgementCutNode judgementCutGround = JudgementCutNode.createNode(YamatoAnimations.YAMATO_JUDGEMENT_CUT_GROUND)
         .setBufferDurationTicks(3)
         .addCondition(new GroundedCondition());
      JudgementCutNode judgementCutGround_FS = JudgementCutNode.createNode(YamatoAnimations.YAMATO_JUDGEMENT_CUT_GROUND_FS)
         .setBufferDurationTicks(3)
         .addCondition(new GroundedCondition());
      JudgementCutNode judgementCutAir = JudgementCutNode.createNode(YamatoAnimations.YAMATO_JUDGEMENT_CUT_AIR)
         .setBufferDurationTicks(3)
         .addCondition(new AirborneCondition(0.5F));
      JudgementCutNode judgementCutAir_FS = JudgementCutNode.createNode(YamatoAnimations.YAMATO_JUDGEMENT_CUT_AIR_FS)
         .setBufferDurationTicks(3)
         .addCondition(new AirborneCondition(0.5F));
      ComboNodeGroup commonConditionBasicAttacks = ComboNodeGroup.creatGroup(voidSlash, upperSlash, rapidSlash, aerialcleave, aerialcleave_sdt);
      ComboNodeGroup provocations = ComboNodeGroup.creatGroup(
         provocation_a,
         provocation_a_aerial,
         provocation_b,
         provocation_b_aerial,
         provocation_c,
         provocation_d,
         provocation_portal,
         provocation_spine_a,
         provocation_spine_b
      );
      ComboNodeGroup finish = ComboNodeGroup.creatGroup(instant_judgement_cut_end, judgement_cut_end, judgement_cut_end_air, execution);
      ComboNodeGroup rootAttack = commonConditionBasicAttacks.step(combo_a_1, aerialrave_combo_a_1, dodgeCounter_combo1);
      ComboNodeGroup attack2 = commonConditionBasicAttacks.step(combo_a_2, aerialrave_combo_a_2);
      ComboNodeGroup attack3 = commonConditionBasicAttacks.step(combo_a_3, combo_b_1, aerialrave_combo_a_3, aerialrave_combo_b_1);
      ComboNodeGroup attack4_ground = commonConditionBasicAttacks.step(combo_a_4, comboC, combo_a_4_sdt);
      ComboNodeGroup dodgeCounter2 = commonConditionBasicAttacks.step(comboC, dodgeCounter_combo2);
      ComboNodeGroup attack5 = commonConditionBasicAttacks.step(combo_a_5_sdt);
      ComboNodeGroup dodgeCounter3 = commonConditionBasicAttacks.step(dodgeCounter_combo3);
      ComboNodeGroup attack4_air = commonConditionBasicAttacks.step(aerialrave_combo_b_2);
      ComboNodeGroup combo_b_ex = commonConditionBasicAttacks.step(combo_b_2_sdt, rootAttack);
      root.key1(rootAttack);
      rootAttack.fanIn(ComboNode.ComboTypes.KEY_1, new ComboNode[]{commonConditionBasicAttacks, comboC, provocations});
      dodgeCounter_combo1.key1(dodgeCounter2);
      dodgeCounter_combo2.key1(dodgeCounter3);
      dodgeCounter_combo3.key1(rootAttack);
      combo_a_1.key1(attack2);
      combo_a_2.key1(attack3);
      combo_a_3.key1(attack4_ground);
      combo_a_4.key1(rootAttack);
      combo_a_4_sdt.key1(attack5);
      combo_a_5_sdt.key1(rootAttack);
      combo_b_1.key1(combo_b_ex);
      combo_b_2_sdt.key1(rootAttack);
      aerialrave_combo_a_1.key1(attack2);
      aerialrave_combo_a_2.key1(attack3);
      aerialrave_combo_a_3.key1(rootAttack);
      aerialrave_combo_b_1.key1(attack4_air);
      aerialrave_combo_b_2.key1(rootAttack);
      provocations.availableVia(ComboNode.ComboTypes.PROVOCATION, root);
      finish.availableVia(ComboNode.ComboTypes.KEY_1_3, root);
      dodgeCounter_combo1.addTimeEvent(ComboNodeEvents.endGp(0.15F));
      judgement_cut_end.addTimeEvent(ComboNodeEvents.consumeSDT(1.7833333F, 1000.0F, 280.0F));
      instant_judgement_cut_end.addTimeEvent(ComboNodeEvents.consumeSDT(0.11666667F, 1000.0F, 280.0F));
      judgement_cut_end_air.addTimeEvent(ComboNodeEvents.consumeSDT(0.11666667F, 1000.0F, 280.0F));
      execution.addTimeEvent(ComboNodeEvents.consumeSDT(0.3F, 500.0F, 280.0F));
      provocation_c.addTimeEvent(ComboNodeEvents.zoom(0.0F, 8, 253, 4, 0.65F, CameraFovUtil.EaseType.SINE, CameraFovUtil.EaseType.INSTANT, 5));
      provocation_c.addTimeEvent(ComboNodeEvents.cinematic(0.0F, 13.5F, 1.3F, 1.5F, 0.1F));
      combo_a_4.addTimeEvent(ComboNodeEvents.zoom(0.0F, 8, 2, 2, 0.85F, CameraFovUtil.EaseType.SINE, CameraFovUtil.EaseType.INSTANT, 5));
      combo_a_4.addTimeEvent(ComboNodeEvents.shakeSmall(0.5F));
      combo_a_4_sdt.addTimeEvent(ComboNodeEvents.zoom(0.0F, 5, 15, 10, 0.9F, CameraFovUtil.EaseType.SINE, CameraFovUtil.EaseType.SINE, 5));
      combo_a_4_sdt.addTimeEvent(ComboNodeEvents.shakeSmall(0.5F));
      combo_a_4_sdt.addTimeEvent(ComboNodeEvents.shakeSmall(0.8F));
      combo_a_5_sdt.addTimeEvent(ComboNodeEvents.zoom(0.0F, 14, 0, 3, 0.55F, CameraFovUtil.EaseType.SINE, CameraFovUtil.EaseType.INSTANT, 5));
      combo_a_5_sdt.addTimeEvent(ComboNodeEvents.shakeHuge(0.95F));
      combo_a_5_sdt.addTimeEvent(ComboNodeEvents.shakeSustain(1.05F));
      combo_b_1.addTimeEvent(ComboNodeEvents.zoom(0.0F, 5, 1, 2, 0.77F, CameraFovUtil.EaseType.SINE, CameraFovUtil.EaseType.INSTANT, 5));
      combo_b_1.addTimeEvent(ComboNodeEvents.shakeStrong(0.55F));
      combo_b_2_sdt.addTimeEvent(ComboNodeEvents.zoom(0.0F, 3, 1, 1, 0.9F, CameraFovUtil.EaseType.SINE, CameraFovUtil.EaseType.INSTANT, 5));
      combo_b_2_sdt.addTimeEvent(ComboNodeEvents.shakeStrong(0.3F));
      aerialrave_combo_b_2.addTimeEvent(ComboNodeEvents.zoom(0.0F, 7, 2, 2, 0.8F, CameraFovUtil.EaseType.SINE, CameraFovUtil.EaseType.INSTANT, 5));
      aerialrave_combo_b_2.addTimeEvent(ComboNodeEvents.shakeFast(0.5F));
      aerialcleave.addTimeEvent(ComboNodeEvents.zoom(0.0F, 6, 3, 6, 0.75F, CameraFovUtil.EaseType.SINE, CameraFovUtil.EaseType.SINE, 5));
      aerialcleave.addTimeEvent(ComboNodeEvents.shakeStrong(0.735F));
      aerialcleave_sdt.addTimeEvent(ComboNodeEvents.shakeStrong(0.535F));
      voidSlash.addTimeEvent(ComboNodeEvents.zoom(0.0F, 8, 8, 1, 0.77F, CameraFovUtil.EaseType.SINE, CameraFovUtil.EaseType.INSTANT, 5));
      voidSlash.addTimeEvent(ComboNodeEvents.shakeStrong(1.0F));
      judgementCutGround.addTimeEvent(ComboNodeEvents.zoom(0.0F, 5, 8, 2, 0.9F, CameraFovUtil.EaseType.SINE, CameraFovUtil.EaseType.INSTANT, 5));
      judgementCutGround.addTimeEvent(ComboNodeEvents.shakeSmall(0.82F));
      judgementCutGround_FS.addTimeEvent(ComboNodeEvents.shakeSmall(0.0F));
      judgementCutAir.addTimeEvent(ComboNodeEvents.zoom(0.0F, 1, 1, 3, 0.9F, CameraFovUtil.EaseType.SINE, CameraFovUtil.EaseType.INSTANT, 5));
      judgementCutAir.addTimeEvent(ComboNodeEvents.shakeSmall(0.13F));
      judgementCutAir_FS.addTimeEvent(ComboNodeEvents.shakeSmall(0.0F));

      assert comboC.getCcFinishNoChase() != null;

      assert comboC.getCcFinish() != null;

      assert comboC.getCcChase() != null;

      assert comboC.getCcBase() != null;

      assert rapidSlash.getBase() != null;

      assert rapidSlash.getExtend() != null;

      assert upperSlash.getTap() != null;

      assert upperSlash.getHold() != null;

      combo_a_1.setActionTag(ActionTag.COMBO_A_1);
      combo_a_2.setActionTag(ActionTag.COMBO_A_2);
      combo_a_3.setActionTag(ActionTag.COMBO_A_3);
      combo_a_4.setActionTag(ActionTag.COMBO_A_4);
      combo_a_4_sdt.setActionTag(ActionTag.COMBO_A_4_SDT);
      combo_a_5_sdt.setActionTag(ActionTag.COMBO_A_5_SDT);
      combo_b_1.setActionTag(ActionTag.COMBO_B_1);
      combo_b_2_sdt.setActionTag(ActionTag.COMBO_B_2_SDT);
      comboC.getCcBase().setActionTag(ActionTag.COMBO_C_BASE);
      comboC.getCcChase().setActionTag(ActionTag.COMBO_C_CHASE);
      comboCFinishDefault.setActionTag(ActionTag.COMBO_C_FINISH);
      comboCFinishNoChaseDefault.setActionTag(ActionTag.COMBO_C_FINISH_NO_CHASE);
      aerialrave_combo_a_1.setActionTag(ActionTag.AERIAL_RAVE_A1);
      aerialrave_combo_a_2.setActionTag(ActionTag.AERIAL_RAVE_A2);
      aerialrave_combo_a_3.setActionTag(ActionTag.AERIAL_RAVE_A3);
      aerialrave_combo_b_1.setActionTag(ActionTag.AERIAL_RAVE_B1);
      aerialrave_combo_b_2.setActionTag(ActionTag.AERIAL_RAVE_B2);
      rapidSlash.getBase().setActionTag(ActionTag.RAPID_SLASH);
      rapidSlash.getExtend().setActionTag(ActionTag.RISING_STAR);
      upperSlash.getTap().setActionTag(ActionTag.UPPER_SLASH_TAP);
      upperSlash.getHold().setActionTag(ActionTag.UPPER_SLASH_HOLD);
      voidSlash.setActionTag(ActionTag.VOID_SLASH);
      aerialcleave.setActionTag(ActionTag.AERIAL_CLEAVE);
      DmcWeaponProfiles.register(new WeaponCombatProfile(DmcWeaponType.YAMATO, root, 11));
      YAMATO = registryWorker.build(
         "yamato",
         VergilSkill::new,
         VergilSkill.createJudgmentCutSkill()
            .setAllowJumpCancel(true)
            .setJCPerfectAir(judgementCutAir_FS)
            .setJCPerfectGround(judgementCutGround_FS)
            .setJCNormalAir(judgementCutAir)
            .setJCNormalGround(judgementCutGround)
            .setCombo(root)
            .setInputBufferDurationTicks(6)
            .setResetTime(11)
            .setShouldDrawGui(false)
      );
      YAMATO_DODGE = registryWorker.build(
         "yamato_step",
         VergilDodgeSkill::new,
         VergilDodgeSkill.createYamatoDodgeBuilder()
            .setRecoveryAnimations(
               DmcWeaponType.YAMATO,
               YamatoAnimations.YAMATO_STRIKE,
               null,
               YamatoAnimations.YAMATO_STEP_L_COMBAT,
               YamatoAnimations.YAMATO_STEP_R_COMBAT,
               null,
               YamatoAnimations.YAMATO_STEP_L_SHORT,
               YamatoAnimations.YAMATO_STEP_R_SHORT
            )
            .setDownAnim(YamatoAnimations.YAMATO_STEP_D)
            .setAnimations(
               YamatoAnimations.YAMATO_STEP_F,
               YamatoAnimations.YAMATO_STEP_B,
               YamatoAnimations.YAMATO_STEP_L,
               YamatoAnimations.YAMATO_STEP_R,
               YamatoAnimations.YAMATO_STEP_U
            )
            .setJcChargeTimeMs(450)
            .setDodgeBufferDurationTicks(5)
      );
   }
}
