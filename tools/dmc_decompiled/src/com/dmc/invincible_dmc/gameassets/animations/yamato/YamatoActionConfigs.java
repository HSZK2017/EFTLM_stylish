package com.dmc.invincible_dmc.gameassets.animations.yamato;

import com.dmc.invincible_dmc.api.animation.types.customStun.CustomStunAttackAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.client.sound.DMCSounds;
import com.dmc.invincible_dmc.gameassets.animations.stun.CustomStunAnimations;
import yesman.epicfight.gameasset.Animations;

public class YamatoActionConfigs {
   public static void initAll() {
      regisSheathSound();
      regisStunAnimation();
      regisDamageModifier();
      regisJudgementCutChargeTime();
      regisPerfectJudgementCutWindow();
      regisClashParryWindow();
      registerAerialActionIncrement();
      registerConcentrationActionValues();
      regisExecutionLifecycle();
   }

   public static void regisSheathSound() {
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_COMBO_A_3, 2.8333333F, DMCSounds.SHEATH);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_STRIKE, 1.3F, DMCSounds.SHEATH);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_DODGE_COUNTER, 2.8333333F, DMCSounds.SHEATH);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_COMBO_A_4, 2.5833333F, DMCSounds.SHEATH_HEAVY);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_COMBO_A_4_SDT, 2.55F, DMCSounds.SHEATH);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_COMBO_A_5_SDT, 4.0F, DMCSounds.SHEATH_HEAVY);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_COMBO_B_1, 2.0F, DMCSounds.SHEATH_HEAVY);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_COMBO_B_2_SDT, 2.7833333F, DMCSounds.SHEATH);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_COMBO_C_END, 1.5166667F, DMCSounds.SHEATH_LIGHT);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_COMBO_C_END_THROUGH, 1.5166667F, DMCSounds.SHEATH_LIGHT);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_RAPIDSLASH, 1.65F, DMCSounds.SHEATH_LIGHT);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_RAPIDSLASH_AIR, 1.3333334F, DMCSounds.SHEATH_LIGHT);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_RAPIDSLASH_RE, 1.5F, DMCSounds.SHEATH_LIGHT);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_RISINGSTAR, 1.2333333F, DMCSounds.SHEATH_LIGHT);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_UPPERSLASH_1, 1.8F, DMCSounds.SHEATH_HEAVY);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_UPPERSLASH_2, 0.46666667F, DMCSounds.SHEATH_LIGHT);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_VOID_SLASH, 2.9833333F, DMCSounds.SHEATH_HEAVY);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_1, 0.73333335F, DMCSounds.SHEATH);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_2, 1.0333333F, DMCSounds.SHEATH);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_3, 0.96666664F, DMCSounds.SHEATH);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_B_1, 1.1333333F, DMCSounds.SHEATH);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_B_2, 1.3333334F, DMCSounds.SHEATH);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_AERIALCLEAVE, 2.25F, DMCSounds.SHEATH_HEAVY);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_AERIALCLEAVE_FAST, 2.0F, DMCSounds.SHEATH_HEAVY);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_AERIALCLEAVE_DASH, 2.0F, DMCSounds.SHEATH_HEAVY);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_JUDGEMENT_CUT_END, 4.9F, DMCSounds.SHEATH_HEAVY);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_JUDGEMENT_CUT_END_INSTANT, 4.9F, DMCSounds.SHEATH_HEAVY);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_JUDGEMENT_CUT_END_DMC3, 4.9F, DMCSounds.SHEATH_HEAVY);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_PROVOCATION_C, 16.883333F, DMCSounds.SHEATH);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_PROVOCATION_D, 6.1F, DMCSounds.SHEATH);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_PROVOCATION_PORTAL, 4.1F, DMCSounds.SHEATH);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_EXECUTION_ALL, 5.7833333F, DMCSounds.SHEATH);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_EXECUTION_DASH, 2.6333334F, DMCSounds.SHEATH);
      YamatoAttackAnimation.registerSheath(YamatoAnimations.YAMATO_EXECUTION_END, 4.9166665F, DMCSounds.SHEATH);
   }

   public static void regisStunAnimation() {
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_COMBO_A_1, 0, CustomStunAnimations.HIT_FROM_LEFT, CustomStunAnimations.HIT_FROM_LEFT_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_COMBO_A_2, 0, CustomStunAnimations.HIT_FROM_RIGHT, CustomStunAnimations.HIT_FROM_RIGHT_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_COMBO_A_3, 0, CustomStunAnimations.HIT_FROM_LEFT, CustomStunAnimations.HIT_FROM_LEFT_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_COMBO_A_3, 1, CustomStunAnimations.HIT_FROM_RIGHT, CustomStunAnimations.HIT_FROM_RIGHT_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(YamatoAnimations.YAMATO_STRIKE, 0, CustomStunAnimations.HIT_FROM_LEFT, CustomStunAnimations.HIT_FROM_LEFT_AIR);
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_STRIKE, 1, CustomStunAnimations.HIT_FROM_RIGHT, CustomStunAnimations.HIT_FROM_RIGHT_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_DODGE_COUNTER, 0, CustomStunAnimations.HIT_FROM_LEFT, CustomStunAnimations.HIT_FROM_LEFT_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_DODGE_COUNTER, 1, CustomStunAnimations.HIT_FROM_RIGHT, CustomStunAnimations.HIT_FROM_RIGHT_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_COMBO_A_4, 0, CustomStunAnimations.HIT_KNOCK_BACK, CustomStunAnimations.HIT_KNOCK_BACK_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_COMBO_A_4_SDT, 0, CustomStunAnimations.HIT_FROM_LEFT, CustomStunAnimations.HIT_FROM_LEFT_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(YamatoAnimations.YAMATO_COMBO_A_4_SDT, 1, CustomStunAnimations.HIT_UP_1, CustomStunAnimations.HIT_UP_1);
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_COMBO_A_5_SDT, 0, CustomStunAnimations.HIT_KNOCK_BACK_FAR, CustomStunAnimations.HIT_KNOCK_BACK_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_COMBO_B_1,
         0,
         CustomStunAnimations.HIT_KNOCK_BACK,
         CustomStunAnimations.HIT_KNOCK_BACK_AIR,
         CustomStunAnimations.HIT_BLOW_BACK_0,
         CustomStunAnimations.HIT_BLOW_BACK_0
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_COMBO_B_2_SDT, 0, CustomStunAnimations.HIT_KNOCKDOWN, CustomStunAnimations.HIT_KNOCKDOWN_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(YamatoAnimations.YAMATO_COMBO_C_START, 0, Animations.BIPED_HIT_SHORT, CustomStunAnimations.HIT_FROM_LEFT_AIR);
      CustomStunAttackAnimation.registerPhaseStun(YamatoAnimations.YAMATO_COMBO_C_START, 1, Animations.BIPED_HIT_SHORT, CustomStunAnimations.HIT_FROM_RIGHT_AIR);
      CustomStunAttackAnimation.registerPhaseStun(YamatoAnimations.YAMATO_COMBO_C_START, 2, Animations.BIPED_HIT_SHORT, CustomStunAnimations.HIT_FROM_LEFT_AIR);
      CustomStunAttackAnimation.registerPhaseStun(YamatoAnimations.YAMATO_COMBO_C_START, 3, Animations.BIPED_HIT_SHORT, CustomStunAnimations.HIT_FROM_RIGHT_AIR);
      CustomStunAttackAnimation.registerPhaseStun(YamatoAnimations.YAMATO_COMBO_C_START, 4, Animations.BIPED_HIT_SHORT, CustomStunAnimations.HIT_FROM_LEFT_AIR);
      CustomStunAttackAnimation.registerPhaseStun(YamatoAnimations.YAMATO_COMBO_C_LOOP, 0, Animations.BIPED_HIT_SHORT, CustomStunAnimations.HIT_FROM_LEFT_AIR);
      CustomStunAttackAnimation.registerPhaseStun(YamatoAnimations.YAMATO_COMBO_C_LOOP, 1, Animations.BIPED_HIT_SHORT, CustomStunAnimations.HIT_FROM_RIGHT_AIR);
      CustomStunAttackAnimation.registerPhaseStun(YamatoAnimations.YAMATO_COMBO_C_LOOP, 2, Animations.BIPED_HIT_SHORT, CustomStunAnimations.HIT_FROM_LEFT_AIR);
      CustomStunAttackAnimation.registerPhaseStun(YamatoAnimations.YAMATO_COMBO_C_LOOP, 3, Animations.BIPED_HIT_SHORT, CustomStunAnimations.HIT_FROM_RIGHT_AIR);
      CustomStunAttackAnimation.registerPhaseStun(YamatoAnimations.YAMATO_COMBO_C_LOOP, 4, Animations.BIPED_HIT_SHORT, CustomStunAnimations.HIT_FROM_LEFT_AIR);
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_COMBO_C_END, 0, CustomStunAnimations.HIT_BLOW_BACK_1, CustomStunAnimations.HIT_BLOW_BACK_1
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_RAPIDSLASH, 0, CustomStunAnimations.HIT_FROM_RIGHT, CustomStunAnimations.HIT_FROM_LEFT_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_RAPIDSLASH, 1, CustomStunAnimations.HIT_FROM_RIGHT, CustomStunAnimations.HIT_FROM_LEFT_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_RAPIDSLASH_AIR, 0, CustomStunAnimations.HIT_FROM_RIGHT, CustomStunAnimations.HIT_FROM_LEFT_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_RAPIDSLASH_AIR, 1, CustomStunAnimations.HIT_FROM_RIGHT, CustomStunAnimations.HIT_FROM_LEFT_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_RAPIDSLASH_RE, 0, CustomStunAnimations.HIT_FROM_RIGHT, CustomStunAnimations.HIT_FROM_LEFT_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_RAPIDSLASH_RE, 1, CustomStunAnimations.HIT_FROM_RIGHT, CustomStunAnimations.HIT_FROM_LEFT_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(YamatoAnimations.YAMATO_RISINGSTAR, 0, CustomStunAnimations.HIT_UP_2, CustomStunAnimations.HIT_UP_2);
      CustomStunAttackAnimation.registerPhaseStun(YamatoAnimations.YAMATO_RISINGSTAR, 1, CustomStunAnimations.HIT_UP_2, CustomStunAnimations.HIT_UP_1);
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_VOID_SLASH, 0, CustomStunAnimations.HIT_FROM_RIGHT, CustomStunAnimations.HIT_FROM_RIGHT_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(YamatoAnimations.YAMATO_UPPERSLASH_1, 0, CustomStunAnimations.HIT_UP_3, CustomStunAnimations.HIT_UP_3);
      CustomStunAttackAnimation.registerPhaseStun(YamatoAnimations.YAMATO_UPPERSLASH_2, 0, CustomStunAnimations.HIT_UP_3, CustomStunAnimations.HIT_UP_3);
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_1, 0, CustomStunAnimations.HIT_FROM_RIGHT, CustomStunAnimations.HIT_FROM_RIGHT_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_2, 0, CustomStunAnimations.HIT_FROM_LEFT, CustomStunAnimations.HIT_FROM_LEFT_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_3, 0, CustomStunAnimations.HIT_KNOCK_BACK, CustomStunAnimations.HIT_KNOCK_BACK_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_AERIALRAVE_COMBO_B_1, 0, CustomStunAnimations.HIT_FROM_LEFT, CustomStunAnimations.HIT_FROM_LEFT_AIR, 0.3F
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_AERIALRAVE_COMBO_B_1, 1, CustomStunAnimations.HIT_FROM_RIGHT, CustomStunAnimations.HIT_FROM_RIGHT_AIR, 0.3F
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_AERIALRAVE_COMBO_B_1, 2, CustomStunAnimations.HIT_FROM_LEFT, CustomStunAnimations.HIT_FROM_LEFT_AIR, 0.3F
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_AERIALRAVE_COMBO_B_2, 0, CustomStunAnimations.HIT_DOWN_BOUNCE_1, CustomStunAnimations.HIT_DOWN_BOUNCE_1
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_AERIALCLEAVE, 0, CustomStunAnimations.HIT_DOWN_BOUNCE_2, CustomStunAnimations.HIT_DOWN_BOUNCE_1
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_AERIALCLEAVE_FAST, 0, CustomStunAnimations.HIT_DOWN_BOUNCE_2, CustomStunAnimations.HIT_DOWN_BOUNCE_1
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_AERIALCLEAVE_DASH, 0, CustomStunAnimations.HIT_FROM_LEFT, CustomStunAnimations.HIT_FROM_LEFT_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_JUDGEMENT_CUT_GROUND, 0, CustomStunAnimations.HIT_FROM_LEFT, CustomStunAnimations.HIT_FROM_LEFT_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_JUDGEMENT_CUT_GROUND_FS, 0, CustomStunAnimations.HIT_FROM_LEFT, CustomStunAnimations.HIT_FROM_LEFT_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_JUDGEMENT_CUT_AIR, 0, CustomStunAnimations.HIT_FROM_LEFT, CustomStunAnimations.HIT_FROM_LEFT_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_JUDGEMENT_CUT_AIR_FS, 0, CustomStunAnimations.HIT_FROM_LEFT, CustomStunAnimations.HIT_FROM_LEFT_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_PROVOCATION_B_AERIAL, 0, CustomStunAnimations.HIT_FROM_LEFT, CustomStunAnimations.HIT_FROM_LEFT_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_PROVOCATION_C, 0, CustomStunAnimations.HIT_FROM_LEFT, CustomStunAnimations.HIT_FROM_LEFT_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_PROVOCATION_D, 0, CustomStunAnimations.HIT_FROM_LEFT, CustomStunAnimations.HIT_FROM_LEFT_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_PROVOCATION_PORTAL, 0, CustomStunAnimations.HIT_FROM_LEFT, CustomStunAnimations.HIT_FROM_LEFT_AIR
      );
      CustomStunAttackAnimation.registerPhaseStun(
         YamatoAnimations.YAMATO_PROVOCATION_PORTAL, 1, CustomStunAnimations.HIT_FROM_RIGHT, CustomStunAnimations.HIT_FROM_RIGHT_AIR
      );
   }

   public static void registerConcentrationActionValues() {
      YamatoAttackAnimation.registerConcentrationMissPenalty(YamatoAnimations.YAMATO_COMBO_C_START, 125.0F);
      YamatoAttackAnimation.registerConcentrationMissPenalty(YamatoAnimations.YAMATO_COMBO_C_LOOP, 75.0F);
      YamatoAttackAnimation.registerConcentrationMissPenalty(YamatoAnimations.YAMATO_COMBO_C_END, 300.0F);
      YamatoAttackAnimation.registerConcentrationHitGain(YamatoAnimations.YAMATO_COMBO_C_START, 25.0F);
      YamatoAttackAnimation.registerConcentrationHitGain(YamatoAnimations.YAMATO_COMBO_C_LOOP, 25.0F);
   }

   public static void registerAerialActionIncrement() {
      YamatoAttackAnimation.registerAerialActionIncrement(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_1, 1.0F);
      YamatoAttackAnimation.registerAerialActionIncrement(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_2, 1.0F);
      YamatoAttackAnimation.registerAerialActionIncrement(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_3, 1.0F);
      YamatoAttackAnimation.registerAerialActionIncrement(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_B_1, 0.5F);
      YamatoAttackAnimation.registerAerialActionIncrement(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_B_2, 0.5F);
   }

   public static void regisJudgementCutChargeTime() {
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_COMBO_A_1, 370);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_COMBO_A_2, 450);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_COMBO_A_3, 600);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_COMBO_A_4, 800);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_COMBO_A_4_SDT, 950);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_COMBO_A_5_SDT, 1350);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_STRIKE, 300);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_DODGE_COUNTER, 600);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_COMBO_B_1, 625);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_COMBO_B_2_SDT, 625);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_COMBO_C_END, 730);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_1, 400);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_2, 350);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_3, 450);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_B_1, 530);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_B_2, 640);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_RAPIDSLASH, 790);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_RAPIDSLASH_AIR, 550);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_RAPIDSLASH_RE, 700);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_RISINGSTAR, 825);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_UPPERSLASH_1, 450);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_UPPERSLASH_2, 600);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_AERIALCLEAVE, 860);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_AERIALCLEAVE_FAST, 630);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_AERIALCLEAVE_DASH, 630);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_VOID_SLASH, 1050);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_PROVOCATION_C, 300);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_PROVOCATION_D, 300);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_EXECUTION_ALL, 300);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_EXECUTION_DASH, 300);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_EXECUTION_END, 300);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_PROVOCATION_PORTAL, 300);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_JUDGEMENT_CUT_END, 300);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_JUDGEMENT_CUT_END_INSTANT, 300);
      YamatoAttackAnimation.registerJcChargeTime(YamatoAnimations.YAMATO_JUDGEMENT_CUT_END_DMC3, 300);
   }

   public static void regisPerfectJudgementCutWindow() {
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_COMBO_A_3, 2.7833333F, 3.0333333F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_STRIKE, 1.25F, 1.4666667F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_DODGE_COUNTER, 2.7833333F, 3.0333333F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_COMBO_A_4, 2.5333333F, 2.7833333F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_COMBO_A_4_SDT, 2.5F, 2.75F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_COMBO_A_5_SDT, 3.95F, 4.2F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_COMBO_B_1, 2.0F, 2.3333333F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_COMBO_B_2_SDT, 2.7333333F, 2.9833333F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_COMBO_C_END, 1.4666667F, 1.7166667F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_1, 0.73333335F, 0.9F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_2, 1.0333333F, 1.2F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_3, 0.96666664F, 1.1333333F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_B_1, 1.1333333F, 1.3F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_B_2, 1.3333334F, 1.5F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_RAPIDSLASH, 1.6F, 1.85F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_RAPIDSLASH_AIR, 1.3F, 1.5333333F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_RISINGSTAR, 1.1833333F, 1.4333333F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_RAPIDSLASH_RE, 1.45F, 1.7F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_UPPERSLASH_1, 1.75F, 2.0F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_AERIALCLEAVE, 2.2166667F, 2.4833333F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_AERIALCLEAVE_FAST, 2.0333333F, 2.25F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_AERIALCLEAVE_DASH, 1.9833333F, 2.2166667F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_VOID_SLASH, 2.9333334F, 3.1833334F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_JUDGEMENT_CUT_END, 4.8333335F, 5.266667F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_JUDGEMENT_CUT_END_INSTANT, 4.8333335F, 5.266667F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_JUDGEMENT_CUT_END_DMC3, 4.8333335F, 5.266667F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_PROVOCATION_C, 16.833334F, 17.133333F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_PROVOCATION_D, 6.1F, 6.4333334F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_EXECUTION_ALL, 5.75F, 6.0833335F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_EXECUTION_DASH, 2.5833333F, 2.8333333F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_EXECUTION_END, 4.883333F, 5.1833334F);
      YamatoAttackAnimation.registerJcPerfWindow(YamatoAnimations.YAMATO_PROVOCATION_PORTAL, 4.0F, 4.3333335F);
      YamatoAttackAnimation.registerJcPerfWindowRatio(YamatoAnimations.YAMATO_JUDGEMENT_CUT_GROUND, 0.33F, 0.55F);
      YamatoAttackAnimation.registerJcPerfWindowRatio(YamatoAnimations.YAMATO_JUDGEMENT_CUT_GROUND_FS, 0.2F, 0.4F);
      YamatoAttackAnimation.registerJcPerfWindowRatio(YamatoAnimations.YAMATO_JUDGEMENT_CUT_AIR, 0.42F, 0.65F);
      YamatoAttackAnimation.registerJcPerfWindowRatio(YamatoAnimations.YAMATO_JUDGEMENT_CUT_AIR_FS, 0.38F, 0.6F);
   }

   public static void regisClashParryWindow() {
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_STRIKE, 0.0F, 0.3F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_COMBO_A_1, 0.05F, 0.5F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_COMBO_A_2, 0.05F, 0.26666668F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_COMBO_A_3, 0.05F, 0.46666667F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_COMBO_A_4, 0.16666667F, 0.51666665F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_COMBO_A_4_SDT, 0.083333336F, 0.93333334F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_COMBO_A_5_SDT, 0.25F, 1.0833334F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_COMBO_B_1, 0.083333336F, 0.5F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_COMBO_B_2_SDT, 0.083333336F, 0.41666666F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_COMBO_C_START, 0.33333334F, 1.15F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_COMBO_C_LOOP, 0.0F, 0.6F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_COMBO_C_END, 0.083333336F, 0.53333336F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_UPPERSLASH_1, 0.083333336F, 0.6166667F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_UPPERSLASH_2, 0.0F, 0.16666667F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_RAPIDSLASH, 0.033333335F, 0.68333334F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_RAPIDSLASH_AIR, 0.0F, 0.41666666F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_RAPIDSLASH_RE, 0.0F, 0.53333336F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_VOID_SLASH, 0.5833333F, 1.1166667F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_1, 0.0F, 0.16666667F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_2, 0.0F, 0.13333334F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_3, 0.083333336F, 0.28333333F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_B_1, 0.083333336F, 0.5F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_B_2, 0.16666667F, 0.6333333F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_AERIALCLEAVE, 0.16666667F, 0.75F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_AERIALCLEAVE_FAST, 0.05F, 0.5F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_AERIALCLEAVE_DASH, 0.05F, 0.5F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_PROVOCATION_C, 13.416667F, 13.716666F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_PROVOCATION_D, 1.1666666F, 1.4833333F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_EXECUTION_ALL, 0.65F, 1.0F);
      YamatoAttackAnimation.registerParryWindow(YamatoAnimations.YAMATO_EXECUTION_DASH, 0.8833333F, 1.3333334F);
   }

   public static void regisDamageModifier() {
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_COMBO_A_1, 0.4F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_COMBO_A_2, 0.5F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_COMBO_A_3, 0.4F, 0.7F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_COMBO_A_4, 1.0F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_COMBO_A_4_SDT, 0.8F, 0.8F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_COMBO_A_5_SDT, 8.0F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_COMBO_B_1, 3.0F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_COMBO_B_2_SDT, 1.5F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_COMBO_C_START, 0.15F, 0.15F, 0.15F, 0.15F, 0.85F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_COMBO_C_LOOP, 0.15F, 0.15F, 0.15F, 0.15F, 0.85F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_COMBO_C_END, 1.6F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_1, 0.5F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_2, 0.6F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_3, 1.2F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_B_1, 0.8F, 0.8F, 0.8F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_AERIALRAVE_COMBO_B_2, 1.0F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_RAPIDSLASH, 0.25F, 0.75F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_RAPIDSLASH_AIR, 0.25F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_STRIKE, 0.25F, 0.5F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_RAPIDSLASH_RE, 0.25F, 0.75F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_RISINGSTAR, 0.8F, 0.7F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_UPPERSLASH_1, 0.9F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_UPPERSLASH_2, 0.9F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_AERIALCLEAVE, 2.9F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_AERIALCLEAVE_FAST, 2.9F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_AERIALCLEAVE_DASH, 2.9F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_VOID_SLASH, 2.75F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_PROVOCATION_C, 3.5F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_PROVOCATION_D, 2.0F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_EXECUTION_ALL, 5.0F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_EXECUTION_DASH, 4.0F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_EXECUTION_END, 6.0F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_PROVOCATION_PORTAL, 2.5F, 2.5F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_JUDGEMENT_CUT_END, 6.0F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_JUDGEMENT_CUT_END_INSTANT, 6.0F);
      YamatoAttackAnimation.registerPhaseDamageMulti(YamatoAnimations.YAMATO_JUDGEMENT_CUT_END_DMC3, 6.0F);
      YamatoAttackAnimation.registerPhaseDamageMultiSdt(YamatoAnimations.YAMATO_COMBO_B_1, 2.6F);
      YamatoAttackAnimation.registerPhaseDamageMultiSdt(YamatoAnimations.YAMATO_UPPERSLASH_1, 1.35F);
      YamatoAttackAnimation.registerPhaseDamageMultiSdt(YamatoAnimations.YAMATO_UPPERSLASH_2, 1.35F);
      YamatoAttackAnimation.registerPhaseDamageMultiSdt(YamatoAnimations.YAMATO_AERIALCLEAVE, 3.25F);
      YamatoAttackAnimation.registerPhaseDamageMultiSdt(YamatoAnimations.YAMATO_AERIALCLEAVE_FAST, 3.25F);
      YamatoAttackAnimation.registerPhaseDamageMultiSdt(YamatoAnimations.YAMATO_AERIALCLEAVE_DASH, 3.25F);
      YamatoAttackAnimation.registerPhaseDamageMultiSdt(YamatoAnimations.YAMATO_VOID_SLASH, 3.5F);
      YamatoAttackAnimation.registerPhaseDamageMultiSdt(YamatoAnimations.YAMATO_JUDGEMENT_CUT_END, 7.0F);
      YamatoAttackAnimation.registerPhaseDamageMultiSdt(YamatoAnimations.YAMATO_JUDGEMENT_CUT_END_INSTANT, 6.0F);
      YamatoAttackAnimation.registerPhaseDamageMultiSdt(YamatoAnimations.YAMATO_JUDGEMENT_CUT_END_DMC3, 6.0F);
   }

   public static void regisExecutionLifecycle() {
   }
}
