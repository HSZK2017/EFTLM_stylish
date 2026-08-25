package com.Yujin.onegradefixer.epicmoonmod.gameasset;

import com.Yujin.onegradefixer.epicmoonmod.animations.EMAnimations;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.condition.AmmoConditionD;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.condition.RoundCondition;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.condition.SavageTigermarkCondition;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.condition.TigermarkCondition;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.AllShootEventD;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.AmplitudeConversionEvent;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.Camera;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.ChangeModeEvent;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.DualSinEvent;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.EffekEvent;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.NextAttackEvent;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.ReloadEvent2;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.ReloadEventD;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.ShootEvent;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.ShootEventD;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.TremorBurstEvent;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.TremorEvent;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.TremorEventS;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.UnrelentingSpiritEvent;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.UnrelentingSpiritSinEvent;
import com.Yujin.onegradefixer.epicmoonmod.skill.weapon_innate.DualInnate;
import com.p1nero.invincible.api.events.HitEvent;
import com.p1nero.invincible.api.events.TimeStampedEvent;
import com.p1nero.invincible.api.skill.ComboNode;
import com.p1nero.invincible.api.skill.ComboNode.ComboTypes;
import com.p1nero.invincible.conditions.DodgeSuccessCondition;
import com.p1nero.invincible.conditions.JumpCondition;
import com.p1nero.invincible.conditions.ParrySuccessCondition;
import com.p1nero.invincible.conditions.SprintingCondition;
import com.p1nero.invincible.conditions.StackCondition;
import com.p1nero.invincible.skill.ComboBasicAttack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.api.forgeevent.SkillBuildEvent.ModRegistryWorker;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.skill.Skill;

@EventBusSubscriber(
   modid = "epicmoonmod",
   bus = Bus.MOD
)
public class dualskill {
   public static Skill dualskill;

   @SubscribeEvent
   public static void BuildSkills(SkillBuildEvent event) {
      ModRegistryWorker registryWorker = event.createRegistryWorker("epicmoonmod");
      ComboNode root = ComboNode.create();
      ComboNode reload = ComboNode.createNode(EMAnimations.DUAL_RELOAD).addCondition(new AmmoConditionD()).addTimeEvent(ReloadEventD.Reload(0.0F));
      ComboNode Counter = ComboNode.createNode(EMAnimations.DUAL_COUNTER)
         .setPriority(7)
         .addCondition(new ParrySuccessCondition())
         .addHitEvent(TremorEventS.tremorevents())
         .addHitEvent(Camera.Shake(5, 1.0F, 5.0F, 20.0F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(3);
            }
         }));
      ComboNode Counter2 = ComboNode.createNode(EMAnimations.DUAL_COUNTER2)
         .setPriority(7)
         .addCondition(new DodgeSuccessCondition())
         .addHitEvent(TremorEventS.tremorevents())
         .addHitEvent(Camera.Shake(5, 1.0F, 5.0F, 20.0F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(3);
            }
         }));
      ComboNode CounterS = ComboNode.createNode(EMAnimations.DUAL_COUNTER)
         .setPriority(8)
         .setDamageMultiplier(ValueModifier.multiplier(1.1F))
         .addCondition(new RoundCondition())
         .addTimeEvent(ShootEventD.ShootD(0.2F))
         .addCondition(new ParrySuccessCondition())
         .addHitEvent(TremorEventS.tremorevents())
         .addHitEvent(Camera.Shake(5, 1.0F, 5.0F, 20.0F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }));
      ComboNode CounterS2 = ComboNode.createNode(EMAnimations.DUAL_COUNTER2)
         .setPriority(8)
         .setDamageMultiplier(ValueModifier.multiplier(1.1F))
         .addCondition(new RoundCondition())
         .addTimeEvent(ShootEventD.ShootD(0.2F))
         .addCondition(new DodgeSuccessCondition())
         .addHitEvent(TremorEventS.tremorevents())
         .addHitEvent(Camera.Shake(5, 1.0F, 5.0F, 20.0F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }));
      ComboNode Dash = ComboNode.createNode(EMAnimations.DUAL_DASH1)
         .setPriority(5)
         .addCondition(new SprintingCondition())
         .addHitEvent(TremorEventS.tremorevents())
         .addHitEvent(Camera.Shake(5, 1.0F, 5.0F, 20.0F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(3);
            }
         }));
      ComboNode Dash2 = ComboNode.createNode(EMAnimations.DUAL_DASH2)
         .setPriority(5)
         .addHitEvent(TremorEvent.tremorevent())
         .addHitEvent(Camera.Shake(10, 2.0F, 5.0F, 20.0F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(3);
            }
         }));
      ComboNode Dash3 = ComboNode.createNode(EMAnimations.DUAL_DASH3)
         .setPriority(5)
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .addHitEvent(Camera.Shake(10, 2.0F, 5.0F, 20.0F));
      ComboNode Dashot = ComboNode.createNode(EMAnimations.DUAL_DASH1)
         .setPriority(6)
         .setDamageMultiplier(ValueModifier.multiplier(1.1F))
         .addCondition(new RoundCondition())
         .addTimeEvent(ShootEventD.ShootD(0.2F))
         .addCondition(new SprintingCondition())
         .addHitEvent(TremorEventS.tremorevents())
         .addHitEvent(Camera.Shake(5, 1.0F, 5.0F, 20.0F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }));
      ComboNode Dashot2 = ComboNode.createNode(EMAnimations.DUAL_DASH2)
         .setPriority(6)
         .setDamageMultiplier(ValueModifier.multiplier(1.1F))
         .addCondition(new RoundCondition())
         .addTimeEvent(ShootEventD.ShootD(0.2F))
         .addHitEvent(TremorEvent.tremorevent())
         .addHitEvent(Camera.Shake(10, 2.0F, 5.0F, 20.0F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }));
      ComboNode TDashot = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TDASHOT)
         .setPriority(5)
         .setHurtDamageMultiplier(0.5F)
         .setCanBeInterrupt(false)
         .addCondition(new SprintingCondition())
         .addCondition(new SavageTigermarkCondition())
         .addTimeEvent(EffekEvent.Effek(0.65F, "c"))
         .addTimeEvent(ShootEvent.Shoot(0.4F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent());
      ComboNode Air = ComboNode.createNode(EMAnimations.DUAL_AIR).setPriority(6).addCondition(new JumpCondition()).addHitEvent(TremorEvent.tremorevent());
      ComboNode Airshot = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TAIRSHOT)
         .setDamageMultiplier(ValueModifier.multiplier(0.8F))
         .setImpactMultiplier(0.8F)
         .setPriority(6)
         .setHurtDamageMultiplier(0.5F)
         .setCanBeInterrupt(false)
         .addCondition(new JumpCondition())
         .addCondition(new TigermarkCondition())
         .addTimeEvent(EffekEvent.Effek(0.4F, "c"))
         .addTimeEvent(ShootEvent.Shoot(0.3F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent());
      ComboNode TAirshot = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TAIRSHOT)
         .setPriority(6)
         .setHurtDamageMultiplier(0.5F)
         .setCanBeInterrupt(false)
         .addCondition(new JumpCondition())
         .addCondition(new SavageTigermarkCondition())
         .addTimeEvent(EffekEvent.Effek(0.4F, "c"))
         .addTimeEvent(ShootEvent.Shoot(0.3F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent());
      ComboNode Auto1 = ComboNode.createNode(EMAnimations.DUAL_AUTO1)
         .setPriority(1)
         .setConvertTime(0.1F)
         .addHitEvent(TremorEventS.tremorevents())
         .addHitEvent(Camera.Shake(5, 1.0F, 5.0F, 20.0F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(3);
            }
         }));
      ComboNode Auto2 = ComboNode.createNode(EMAnimations.DUAL_AUTO2)
         .addHitEvent(TremorEvent.tremorevent())
         .addHitEvent(Camera.Shake(10, 2.0F, 5.0F, 20.0F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(3);
            }
         }));
      ComboNode Auto3 = ComboNode.createNode(EMAnimations.DUAL_AUTO3)
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .addHitEvent(Camera.Shake(10, 2.0F, 5.0F, 20.0F));
      ComboNode Shot1 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSHOT1)
         .setDamageMultiplier(ValueModifier.multiplier(0.75F))
         .setImpactMultiplier(0.75F)
         .setPriority(2)
         .setHurtDamageMultiplier(0.5F)
         .setCanBeInterrupt(false)
         .addCondition(new TigermarkCondition())
         .addTimeEvent(ShootEvent.Shoot(0.3F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent());
      ComboNode Shot2 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSHOT2)
         .setDamageMultiplier(ValueModifier.multiplier(0.75F))
         .setImpactMultiplier(0.75F)
         .setPriority(2)
         .setHurtDamageMultiplier(0.5F)
         .setCanBeInterrupt(false)
         .addCondition(new TigermarkCondition())
         .addTimeEvent(ShootEvent.Shoot(0.4F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent());
      ComboNode Shot3 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSHOT5)
         .setDamageMultiplier(ValueModifier.multiplier(0.75F))
         .setPriority(2)
         .setHurtDamageMultiplier(0.5F)
         .setCanBeInterrupt(false)
         .addCondition(new TigermarkCondition())
         .addTimeEvent(EffekEvent.Effek(1.0F, "c"))
         .addTimeEvent(ShootEvent.Shoot(0.5F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorBurstEvent.tremorburstevent());
      ComboNode TShot1 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSHOT1)
         .setPriority(2)
         .setHurtDamageMultiplier(0.5F)
         .setCanBeInterrupt(false)
         .addCondition(new SavageTigermarkCondition())
         .addTimeEvent(EffekEvent.Effek(0.3F, "c"))
         .addTimeEvent(ShootEvent.Shoot(0.3F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent());
      ComboNode TShot2 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSHOT2)
         .setPriority(2)
         .setHurtDamageMultiplier(0.5F)
         .setCanBeInterrupt(false)
         .addCondition(new SavageTigermarkCondition())
         .addTimeEvent(EffekEvent.Effek(0.5F, "c"))
         .addTimeEvent(ShootEvent.Shoot(0.4F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent());
      ComboNode TShot4 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSHOT5)
         .setPriority(2)
         .setHurtDamageMultiplier(0.5F)
         .setCanBeInterrupt(false)
         .addCondition(new SavageTigermarkCondition())
         .addTimeEvent(EffekEvent.Effek(1.0F, "c"))
         .addTimeEvent(ShootEvent.Shoot(0.5F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorBurstEvent.tremorburstevent());
      ComboNode skill = ComboNode.createNode(EMAnimations.DUAL_SKILL)
         .setPriority(5)
         .addCondition(new StackCondition(1, 1))
         .addTimeEvent(NextAttackEvent.next(1.4F, ComboTypes.KEY_1))
         .addHitEvent(Camera.Shake(5, 1.0F, 5.0F, 20.0F))
         .addHitEvent(TremorEventS.tremorevents())
         .addTimeEvent(TimeStampedEvent.createTimeCommandEvent(0.0F, "invincible consumeStack 1", false))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(3);
            }
         }));
      ComboNode skill2 = ComboNode.createNode(EMAnimations.DUAL_SKILL2)
         .setPriority(5)
         .addTimeEvent(NextAttackEvent.next(0.5F, ComboTypes.KEY_1))
         .addHitEvent(Camera.Shake(5, 1.0F, 5.0F, 20.0F))
         .addHitEvent(TremorEventS.tremorevents())
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(3);
            }
         }));
      ComboNode skill3 = ComboNode.createNode(EMAnimations.DUAL_SKILL3)
         .setPriority(5)
         .addHitEvent(Camera.Shake(5, 1.0F, 5.0F, 20.0F))
         .addHitEvent(AmplitudeConversionEvent.amplitudeconversion())
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(3);
            }
         }));
      ComboNode skill4 = ComboNode.createNode(EMAnimations.DUAL_SKILL4)
         .setPriority(6)
         .addCondition(new StackCondition(1, 1))
         .addCondition(new RoundCondition())
         .addTimeEvent(ShootEventD.ShootD(0.6F))
         .addTimeEvent(NextAttackEvent.next(1.0F, ComboTypes.KEY_1))
         .addHitEvent(Camera.Shake(5, 1.0F, 5.0F, 20.0F))
         .addHitEvent(TremorEvent.tremorevent())
         .addTimeEvent(TimeStampedEvent.createTimeCommandEvent(0.0F, "invincible consumeStack 1", false))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }));
      ComboNode skill5 = ComboNode.createNode(EMAnimations.DUAL_SKILL5)
         .setPriority(6)
         .addCondition(new RoundCondition())
         .addTimeEvent(ShootEventD.ShootD(0.05F))
         .addHitEvent(Camera.Shake(5, 1.0F, 5.0F, 20.0F))
         .addTimeEvent(NextAttackEvent.next(0.8F, ComboTypes.KEY_1))
         .addHitEvent(TremorEventS.tremorevents())
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }));
      ComboNode skill6 = ComboNode.createNode(EMAnimations.DUAL_SKILL6)
         .setPriority(6)
         .setDamageMultiplier(ValueModifier.multiplier(1.1F))
         .addCondition(new RoundCondition())
         .addTimeEvent(ShootEventD.ShootD(0.0F))
         .addHitEvent(Camera.Shake(5, 1.0F, 5.0F, 20.0F))
         .addTimeEvent(NextAttackEvent.next(0.9F, ComboTypes.KEY_1))
         .addHitEvent(TremorEventS.tremorevents())
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }));
      ComboNode skill55 = ComboNode.createNode(EMAnimations.DUAL_SKILL5)
         .setPriority(5)
         .setDamageMultiplier(ValueModifier.multiplier(0.9F))
         .addHitEvent(Camera.Shake(5, 1.0F, 5.0F, 20.0F))
         .addTimeEvent(NextAttackEvent.next(0.8F, ComboTypes.KEY_1))
         .addHitEvent(TremorEventS.tremorevents())
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }));
      ComboNode skill66 = ComboNode.createNode(EMAnimations.DUAL_SKILL6)
         .setPriority(5)
         .addHitEvent(Camera.Shake(5, 1.0F, 5.0F, 20.0F))
         .addTimeEvent(NextAttackEvent.next(0.9F, ComboTypes.KEY_1))
         .addHitEvent(TremorEventS.tremorevents())
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }));
      ComboNode skill7 = ComboNode.createNode(EMAnimations.DUAL_SKILL7)
         .setPriority(6)
         .addHitEvent(Camera.Shake(5, 1.0F, 5.0F, 20.0F))
         .addTimeEvent(NextAttackEvent.next(0.4F, ComboTypes.KEY_1))
         .addHitEvent(TremorEventS.tremorevents())
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }));
      ComboNode skill8 = ComboNode.createNode(EMAnimations.DUAL_SKILL8)
         .setPriority(6)
         .addHitEvent(Camera.Shake(5, 1.0F, 5.0F, 20.0F))
         .addHitEvent(AmplitudeConversionEvent.amplitudeconversion())
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }));
      ComboNode skill9 = ComboNode.createNode(EMAnimations.DUAL_SKILL9)
         .setPriority(7)
         .addCondition(new StackCondition(2, 2))
         .addTimeEvent(NextAttackEvent.next(1.7F, ComboTypes.KEY_1))
         .addHitEvent(Camera.Shake(10, 2.0F, 5.0F, 20.0F))
         .addHitEvent(TremorEventS.tremorevents())
         .addTimeEvent(TimeStampedEvent.createTimeCommandEvent(0.0F, "invincible consumeStack 2", false))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addTimeEvent(DualSinEvent.DS(0.0F));
      ComboNode skill10 = ComboNode.createNode(EMAnimations.DUAL_SKILL10)
         .setPriority(7)
         .addTimeEvent(NextAttackEvent.next(0.8F, ComboTypes.KEY_1))
         .addHitEvent(Camera.Shake(10, 2.0F, 5.0F, 20.0F))
         .addHitEvent(TremorEventS.tremorevents())
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }));
      ComboNode skill11 = ComboNode.createNode(EMAnimations.DUAL_SKILL11)
         .setPriority(7)
         .addTimeEvent(NextAttackEvent.next(2.85F, ComboTypes.KEY_1))
         .addHitEvent(Camera.Shake(10, 2.0F, 5.0F, 20.0F))
         .addHitEvent(TremorEventS.tremorevents())
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }));
      ComboNode skill13 = ComboNode.createNode(EMAnimations.DUAL_SKILL13)
         .setPriority(7)
         .addTimeEvent(AllShootEventD.AllShootD(0.0F))
         .addTimeEvent(ReloadEventD.Reload(0.0F))
         .addHitEvent(Camera.Shake(10, 2.0F, 5.0F, 20.0F))
         .addHitEvent(AmplitudeConversionEvent.amplitudeconversion())
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }));
      ComboNode Skill03 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSKILL2)
         .setHurtDamageMultiplier(0.25F)
         .setDamageMultiplier(ValueModifier.multiplier(0.8F))
         .setImpactMultiplier(0.8F)
         .setPriority(5)
         .setCanBeInterrupt(false)
         .addCondition(new StackCondition(1, 1))
         .addCondition(new TigermarkCondition())
         .addTimeEvent(ShootEvent.Shoot(0.1F))
         .addTimeEvent(TimeStampedEvent.createTimeCommandEvent(0.0F, "invincible consumeStack 1", false))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent())
         .addTimeEvent(UnrelentingSpiritEvent.USE(0.0F))
         .addTimeEvent(NextAttackEvent.next(1.4F, ComboTypes.KEY_1));
      ComboNode Skill04 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSKILL3)
         .setHurtDamageMultiplier(0.25F)
         .setDamageMultiplier(ValueModifier.multiplier(0.8F))
         .setImpactMultiplier(0.8F)
         .setPriority(5)
         .setCanBeInterrupt(false)
         .addCondition(new TigermarkCondition())
         .addTimeEvent(ShootEvent.Shoot(0.1F))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(AmplitudeConversionEvent.amplitudeconversion())
         .addTimeEvent(NextAttackEvent.next(1.2F, ComboTypes.KEY_1));
      ComboNode Skill05 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSKILL4)
         .setHurtDamageMultiplier(0.25F)
         .setDamageMultiplier(ValueModifier.multiplier(0.8F))
         .setImpactMultiplier(0.8F)
         .setPriority(5)
         .setCanBeInterrupt(false)
         .addCondition(new TigermarkCondition())
         .addTimeEvent(ShootEvent.Shoot(0.1F))
         .addTimeEvent(EffekEvent.Effek(1.1F, "c"))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorBurstEvent.tremorburstevent());
      ComboNode Skill06 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSKILL5)
         .setDamageMultiplier(ValueModifier.multiplier(0.75F))
         .setImpactMultiplier(0.75F)
         .setPriority(5)
         .setCanBeInterrupt(false)
         .addCondition(new StackCondition(3, 3))
         .addCondition(new TigermarkCondition())
         .addTimeEvent(EffekEvent.Effek(1.35F, "c"))
         .addTimeEvent(ShootEvent.Shoot(1.1F))
         .addTimeEvent(TimeStampedEvent.createTimeCommandEvent(0.0F, "invincible consumeStack 3", false))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent())
         .addTimeEvent(ChangeModeEvent.ModeChange(0.5F, 1))
         .addTimeEvent(UnrelentingSpiritSinEvent.USSE(0.0F))
         .addTimeEvent(NextAttackEvent.next(1.8F, ComboTypes.KEY_1));
      ComboNode Skill07 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSKILL6)
         .setDamageMultiplier(ValueModifier.multiplier(0.75F))
         .setImpactMultiplier(0.75F)
         .setPriority(2)
         .setCanBeInterrupt(false)
         .addCondition(new TigermarkCondition())
         .addTimeEvent(EffekEvent.Effek(0.4F, "c"))
         .addTimeEvent(ShootEvent.Shoot(0.4F))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent())
         .addTimeEvent(NextAttackEvent.next(1.0F, ComboTypes.KEY_1));
      ComboNode Skill08 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSKILL7)
         .setDamageMultiplier(ValueModifier.multiplier(0.75F))
         .setImpactMultiplier(0.75F)
         .setPriority(5)
         .setCanBeInterrupt(false)
         .addCondition(new TigermarkCondition())
         .addTimeEvent(ShootEvent.Shoot(0.1F))
         .addTimeEvent(EffekEvent.Effek(1.1F, "c"))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent())
         .addTimeEvent(NextAttackEvent.next(1.7F, ComboTypes.KEY_1));
      ComboNode Skill09 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSKILL8)
         .setDamageMultiplier(ValueModifier.multiplier(0.75F))
         .setImpactMultiplier(0.75F)
         .setPriority(2)
         .setCanBeInterrupt(false)
         .addCondition(new TigermarkCondition())
         .addTimeEvent(EffekEvent.Effek(1.1F, "c"))
         .addTimeEvent(ShootEvent.Shoot(0.6F))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent())
         .addTimeEvent(NextAttackEvent.next(1.5F, ComboTypes.KEY_1));
      ComboNode Skill10 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSKILL9)
         .setDamageMultiplier(ValueModifier.multiplier(0.75F))
         .setImpactMultiplier(0.75F)
         .setPriority(2)
         .setCanBeInterrupt(false)
         .addCondition(new TigermarkCondition())
         .addTimeEvent(EffekEvent.Effek(1.0F, "c"))
         .addTimeEvent(ShootEvent.Shoot(0.5F))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(AmplitudeConversionEvent.amplitudeconversion())
         .addTimeEvent(NextAttackEvent.next(1.3F, ComboTypes.KEY_1));
      ComboNode Skill11 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSKILL10)
         .setDamageMultiplier(ValueModifier.multiplier(0.75F))
         .setImpactMultiplier(0.75F)
         .setPriority(2)
         .setCanBeInterrupt(false)
         .addCondition(new TigermarkCondition())
         .addTimeEvent(ShootEvent.Shoot(2.7F))
         .addTimeEvent(EffekEvent.Effek(0.7F, "c"))
         .addTimeEvent(EffekEvent.Effek(1.9F, "c"))
         .addTimeEvent(EffekEvent.Effek(2.7F, "e"))
         .addTimeEvent(EffekEvent.Effek(3.1F, "c"))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .addTimeEvent(ChangeModeEvent.ModeChange(3.2F, 0))
         .addTimeEvent(ReloadEvent2.Reload2(3.5F));
      ComboNode TSkill2 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSKILL2)
         .setHurtDamageMultiplier(0.25F)
         .setPriority(5)
         .setCanBeInterrupt(false)
         .addCondition(new StackCondition(1, 1))
         .addCondition(new SavageTigermarkCondition())
         .addTimeEvent(ShootEvent.Shoot(0.1F))
         .addTimeEvent(TimeStampedEvent.createTimeCommandEvent(0.0F, "invincible consumeStack 1", false))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent())
         .addTimeEvent(UnrelentingSpiritEvent.USE(0.0F))
         .addTimeEvent(NextAttackEvent.next(1.4F, ComboTypes.KEY_1));
      ComboNode TSkill3 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSKILL3)
         .setHurtDamageMultiplier(0.25F)
         .setPriority(5)
         .setCanBeInterrupt(false)
         .addCondition(new SavageTigermarkCondition())
         .addTimeEvent(ShootEvent.Shoot(0.1F))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(AmplitudeConversionEvent.amplitudeconversion())
         .addTimeEvent(NextAttackEvent.next(1.2F, ComboTypes.KEY_1));
      ComboNode TSkill4 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSKILL4)
         .setHurtDamageMultiplier(0.25F)
         .setPriority(5)
         .setCanBeInterrupt(false)
         .addCondition(new SavageTigermarkCondition())
         .addTimeEvent(ShootEvent.Shoot(0.1F))
         .addTimeEvent(EffekEvent.Effek(1.1F, "c"))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorBurstEvent.tremorburstevent());
      ComboNode TSkill5 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSKILL5)
         .setPriority(5)
         .setCanBeInterrupt(false)
         .addCondition(new StackCondition(3, 3))
         .addCondition(new SavageTigermarkCondition())
         .addTimeEvent(EffekEvent.Effek(1.35F, "c"))
         .addTimeEvent(ShootEvent.Shoot(1.1F))
         .addTimeEvent(TimeStampedEvent.createTimeCommandEvent(0.0F, "invincible consumeStack 3", false))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent())
         .addTimeEvent(ChangeModeEvent.ModeChange(0.5F, 1))
         .addTimeEvent(UnrelentingSpiritSinEvent.USSE(0.0F))
         .addTimeEvent(NextAttackEvent.next(1.8F, ComboTypes.KEY_1));
      ComboNode TSkill6 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSKILL6)
         .setPriority(2)
         .setCanBeInterrupt(false)
         .addCondition(new SavageTigermarkCondition())
         .addTimeEvent(EffekEvent.Effek(0.4F, "c"))
         .addTimeEvent(ShootEvent.Shoot(0.4F))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent())
         .addTimeEvent(NextAttackEvent.next(1.0F, ComboTypes.KEY_1));
      ComboNode TSkill7 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSKILL7)
         .setPriority(5)
         .setCanBeInterrupt(false)
         .addCondition(new SavageTigermarkCondition())
         .addTimeEvent(ShootEvent.Shoot(0.1F))
         .addTimeEvent(EffekEvent.Effek(1.1F, "c"))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent())
         .addTimeEvent(NextAttackEvent.next(1.7F, ComboTypes.KEY_1));
      ComboNode TSkill8 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSKILL8)
         .setPriority(2)
         .setCanBeInterrupt(false)
         .addCondition(new SavageTigermarkCondition())
         .addTimeEvent(EffekEvent.Effek(1.1F, "c"))
         .addTimeEvent(ShootEvent.Shoot(0.6F))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent())
         .addTimeEvent(NextAttackEvent.next(1.5F, ComboTypes.KEY_1));
      ComboNode TSkill9 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSKILL9)
         .setPriority(2)
         .setCanBeInterrupt(false)
         .addCondition(new SavageTigermarkCondition())
         .addTimeEvent(EffekEvent.Effek(1.0F, "c"))
         .addTimeEvent(ShootEvent.Shoot(0.5F))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(AmplitudeConversionEvent.amplitudeconversion())
         .addTimeEvent(NextAttackEvent.next(1.3F, ComboTypes.KEY_1));
      ComboNode TSkill10 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSKILL10)
         .setPriority(2)
         .setCanBeInterrupt(false)
         .addCondition(new SavageTigermarkCondition())
         .addTimeEvent(ShootEvent.Shoot(2.7F))
         .addTimeEvent(EffekEvent.Effek(0.7F, "c"))
         .addTimeEvent(EffekEvent.Effek(1.9F, "c"))
         .addTimeEvent(EffekEvent.Effek(2.7F, "e"))
         .addTimeEvent(EffekEvent.Effek(3.1F, "c"))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .addTimeEvent(ChangeModeEvent.ModeChange(3.2F, 0))
         .addTimeEvent(ReloadEvent2.Reload2(3.5F));
      ComboNode ESkill2 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_ESKILL2)
         .setHurtDamageMultiplier(0.25F)
         .setPriority(1)
         .setCanBeInterrupt(false)
         .addCondition(new StackCondition(1, 1))
         .addTimeEvent(ShootEvent.Shoot(0.1F))
         .addTimeEvent(TimeStampedEvent.createTimeCommandEvent(0.0F, "invincible consumeStack 1", false))
         .setNotCharge(true)
         .addHitEvent(TremorEvent.tremorevent())
         .addTimeEvent(UnrelentingSpiritEvent.USE(0.0F))
         .addTimeEvent(NextAttackEvent.next(1.4F, ComboTypes.KEY_1));
      ComboNode ESkill3 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_ESKILL3)
         .setHurtDamageMultiplier(0.25F)
         .setPriority(1)
         .setCanBeInterrupt(false)
         .addTimeEvent(ShootEvent.Shoot(0.1F))
         .setNotCharge(true)
         .addHitEvent(AmplitudeConversionEvent.amplitudeconversion())
         .addTimeEvent(NextAttackEvent.next(1.2F, ComboTypes.KEY_1));
      ComboNode ESkill4 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_ESKILL4)
         .setHurtDamageMultiplier(0.25F)
         .setPriority(1)
         .setCanBeInterrupt(false)
         .addTimeEvent(ShootEvent.Shoot(0.1F))
         .addTimeEvent(EffekEvent.Effek(1.1F, "c"))
         .setNotCharge(true)
         .addHitEvent(TremorBurstEvent.tremorburstevent());
      ComboNode ESkill5 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_ESKILL5)
         .setPriority(1)
         .setCanBeInterrupt(false)
         .addTimeEvent(EffekEvent.Effek(1.35F, "c"))
         .addTimeEvent(ShootEvent.Shoot(1.1F))
         .setNotCharge(true)
         .addCondition(new StackCondition(3, 3))
         .addTimeEvent(TimeStampedEvent.createTimeCommandEvent(0.0F, "invincible consumeStack 3", false))
         .addHitEvent(TremorEvent.tremorevent())
         .addTimeEvent(ChangeModeEvent.ModeChange(0.5F, 1))
         .addTimeEvent(UnrelentingSpiritSinEvent.USSE(0.0F))
         .addTimeEvent(NextAttackEvent.next(1.8F, ComboTypes.KEY_1));
      ComboNode ESkill6 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_ESKILL6)
         .setPriority(1)
         .setCanBeInterrupt(false)
         .addTimeEvent(EffekEvent.Effek(0.4F, "c"))
         .addTimeEvent(ShootEvent.Shoot(0.4F))
         .setNotCharge(true)
         .addHitEvent(TremorEvent.tremorevent())
         .addTimeEvent(NextAttackEvent.next(1.0F, ComboTypes.KEY_1));
      ComboNode ESkill7 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_ESKILL7)
         .setPriority(1)
         .setCanBeInterrupt(false)
         .addTimeEvent(ShootEvent.Shoot(0.1F))
         .addTimeEvent(EffekEvent.Effek(1.1F, "c"))
         .setNotCharge(true)
         .addHitEvent(TremorEvent.tremorevent())
         .addTimeEvent(NextAttackEvent.next(1.7F, ComboTypes.KEY_1));
      ComboNode ESkill8 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_ESKILL8)
         .setPriority(1)
         .setCanBeInterrupt(false)
         .addTimeEvent(EffekEvent.Effek(1.1F, "c"))
         .addTimeEvent(ShootEvent.Shoot(0.6F))
         .setNotCharge(true)
         .addHitEvent(TremorEvent.tremorevent())
         .addTimeEvent(NextAttackEvent.next(1.5F, ComboTypes.KEY_1));
      ComboNode ESkill9 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_ESKILL9)
         .setPriority(1)
         .setCanBeInterrupt(false)
         .addTimeEvent(EffekEvent.Effek(1.0F, "c"))
         .addTimeEvent(ShootEvent.Shoot(0.5F))
         .setNotCharge(true)
         .addHitEvent(AmplitudeConversionEvent.amplitudeconversion())
         .addTimeEvent(NextAttackEvent.next(1.3F, ComboTypes.KEY_1));
      ComboNode ESkill10 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_ESKILL10)
         .setPriority(1)
         .setCanBeInterrupt(false)
         .addTimeEvent(ShootEvent.Shoot(2.7F))
         .addTimeEvent(EffekEvent.Effek(0.7F, "c"))
         .addTimeEvent(EffekEvent.Effek(1.9F, "c"))
         .addTimeEvent(EffekEvent.Effek(2.7F, "e"))
         .addTimeEvent(EffekEvent.Effek(3.1F, "c"))
         .setNotCharge(true)
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .addTimeEvent(ChangeModeEvent.ModeChange(3.2F, 0))
         .addTimeEvent(ReloadEvent2.Reload2(3.5F));
      ComboNode Skill030 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSKILL4)
         .setPriority(7)
         .setDamageMultiplier(ValueModifier.multiplier(0.8F))
         .addCondition(new SprintingCondition())
         .setCanBeInterrupt(false)
         .addCondition(new StackCondition(2, 3))
         .addCondition(new TigermarkCondition())
         .addTimeEvent(ShootEvent.Shoot(0.1F))
         .addTimeEvent(TimeStampedEvent.createTimeCommandEvent(0.0F, "invincible consumeStack 1", false))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent())
         .addTimeEvent(NextAttackEvent.next(1.4F, ComboTypes.KEY_1));
      ComboNode TSkill20 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSKILL2)
         .setPriority(7)
         .addCondition(new SprintingCondition())
         .setCanBeInterrupt(false)
         .addCondition(new StackCondition(2, 3))
         .addCondition(new SavageTigermarkCondition())
         .addTimeEvent(ShootEvent.Shoot(0.1F))
         .addTimeEvent(TimeStampedEvent.createTimeCommandEvent(0.0F, "invincible consumeStack 1", false))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent())
         .addTimeEvent(UnrelentingSpiritEvent.USE(0.0F))
         .addTimeEvent(NextAttackEvent.next(1.4F, ComboTypes.KEY_1));
      ComboNode ESkill20 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_ESKILL2)
         .setPriority(4)
         .setCanBeInterrupt(false)
         .addCondition(new SprintingCondition())
         .addCondition(new StackCondition(2, 3))
         .addTimeEvent(ShootEvent.Shoot(0.1F))
         .addTimeEvent(TimeStampedEvent.createTimeCommandEvent(0.0F, "invincible consumeStack 1", false))
         .setNotCharge(true)
         .addHitEvent(TremorEvent.tremorevent())
         .addTimeEvent(UnrelentingSpiritEvent.USE(0.0F))
         .addTimeEvent(NextAttackEvent.next(1.4F, ComboTypes.KEY_1));
      ComboNode BasicAttack = ComboNode.create()
         .addConditionNode(Dash)
         .addConditionNode(Air)
         .addConditionNode(Auto1)
         .addConditionNode(Dashot)
         .addConditionNode(Counter)
         .addConditionNode(Counter2)
         .addConditionNode(CounterS)
         .addConditionNode(CounterS2);
      ComboNode BasicAttack2 = ComboNode.create()
         .addConditionNode(Auto2)
         .addConditionNode(Counter)
         .addConditionNode(Counter2)
         .addConditionNode(CounterS)
         .addConditionNode(CounterS2);
      ComboNode BasicAttack3 = ComboNode.create()
         .addConditionNode(Auto3)
         .addConditionNode(Counter)
         .addConditionNode(Counter2)
         .addConditionNode(CounterS)
         .addConditionNode(CounterS2);
      ComboNode DA1 = ComboNode.create()
         .addConditionNode(Dash2)
         .addConditionNode(Dashot2)
         .addConditionNode(Counter)
         .addConditionNode(Counter2)
         .addConditionNode(CounterS)
         .addConditionNode(CounterS2);
      ComboNode DA2 = ComboNode.create()
         .addConditionNode(Dash3)
         .addConditionNode(Counter)
         .addConditionNode(Counter2)
         .addConditionNode(CounterS)
         .addConditionNode(CounterS2);
      ComboNode Skills = ComboNode.create().addConditionNode(reload);
      ComboNode Skill2 = ComboNode.create().addConditionNode(skill).addConditionNode(Shot2).addConditionNode(TShot2);
      ComboNode Skill3 = ComboNode.create().addConditionNode(skill).addConditionNode(Shot3).addConditionNode(TShot4);
      ComboNode Skill5 = ComboNode.create().addConditionNode(skill5).addConditionNode(skill55);
      ComboNode Skill6 = ComboNode.create().addConditionNode(skill6).addConditionNode(skill66);
      ComboNode SA = ComboNode.create().addConditionNode(skill).addConditionNode(skill4).addConditionNode(skill9);
      ComboNode SA2 = ComboNode.create().addConditionNode(Skill04).addConditionNode(TSkill3).addConditionNode(ESkill3);
      ComboNode SA3 = ComboNode.create().addConditionNode(Skill05).addConditionNode(TSkill4).addConditionNode(ESkill4);
      ComboNode SA4 = ComboNode.create().addConditionNode(Skill07).addConditionNode(TSkill6).addConditionNode(ESkill6);
      ComboNode SA5 = ComboNode.create().addConditionNode(Skill08).addConditionNode(TSkill7).addConditionNode(ESkill7);
      ComboNode SA6 = ComboNode.create().addConditionNode(Skill09).addConditionNode(TSkill8).addConditionNode(ESkill8);
      ComboNode SA7 = ComboNode.create().addConditionNode(Skill10).addConditionNode(TSkill9).addConditionNode(ESkill9);
      ComboNode SA8 = ComboNode.create().addConditionNode(Skill11).addConditionNode(TSkill10).addConditionNode(ESkill10);
      root.key1(BasicAttack);
      root.key3(Skills);
      root.key4(SA);
      reload.key1(BasicAttack);
      reload.key4(SA);
      Counter.key1(BasicAttack2);
      Counter.key3(Skills);
      Counter.key4(SA);
      Counter2.key1(BasicAttack2);
      Counter2.key3(Skills);
      Counter2.key4(SA);
      CounterS.key1(BasicAttack2);
      CounterS.key3(Skills);
      CounterS.key4(SA);
      CounterS2.key1(BasicAttack2);
      CounterS2.key3(Skills);
      CounterS2.key4(SA);
      Air.key1(BasicAttack2);
      Air.key3(Skills);
      Air.key4(SA);
      Airshot.key1(Auto2);
      Airshot.key3(Skill2);
      Airshot.key4(SA);
      TAirshot.key1(Auto2);
      TAirshot.key3(Skill2);
      TAirshot.key4(SA);
      skill.key1(skill2);
      skill2.key1(skill3);
      skill3.key1(BasicAttack);
      skill3.key3(Skills);
      skill4.key1(Skill5);
      skill5.key1(Skill6);
      skill55.key1(Skill6);
      skill6.key1(skill7);
      skill66.key1(skill7);
      skill7.key1(skill8);
      skill8.key1(BasicAttack);
      skill8.key3(Skills);
      skill9.key1(skill10);
      skill10.key1(skill11);
      skill11.key1(skill13);
      skill13.key1(BasicAttack);
      skill13.key3(Skills);
      Dash.key1(DA1);
      Dash.key3(Skills);
      Dash.key4(SA);
      Dash2.key1(DA2);
      Dash2.key3(Skills);
      Dash2.key4(SA);
      Dash3.key1(BasicAttack);
      Dash3.key3(Skills);
      Dash3.key4(SA);
      Dashot.key1(DA1);
      Dashot.key3(Skills);
      Dashot.key4(SA);
      Dashot2.key1(DA2);
      Dashot2.key3(Skills);
      Dashot2.key4(SA);
      TDashot.key1(Auto2);
      TDashot.key3(Skill2);
      TDashot.key4(SA);
      Auto1.key1(BasicAttack2);
      Auto1.key3(Skills);
      Auto1.key4(SA);
      Auto2.key1(BasicAttack3);
      Auto2.key3(Skills);
      Auto2.key4(SA);
      Auto3.key1(BasicAttack);
      Auto3.key3(Skills);
      Auto3.key4(SA);
      Shot1.key1(Auto2);
      Shot1.key3(Skill2);
      Shot1.key4(SA);
      Shot2.key1(Auto3);
      Shot2.key3(Skill3);
      Shot2.key4(SA);
      Shot3.key4(SA);
      TShot1.key1(Auto2);
      TShot1.key3(Skill2);
      TShot1.key4(SA);
      TShot2.key1(Auto3);
      TShot2.key3(Skill3);
      TShot2.key4(SA);
      ESkill2.key1(ESkill3);
      ESkill2.key3(ESkill3);
      ESkill2.key4(ESkill3);
      ESkill3.key1(ESkill4);
      ESkill3.key3(ESkill4);
      ESkill3.key4(ESkill4);
      ESkill5.key1(SA4);
      ESkill5.key3(SA4);
      ESkill5.key4(SA4);
      ESkill6.key1(SA5);
      ESkill6.key3(SA5);
      ESkill6.key4(SA5);
      ESkill7.key1(SA6);
      ESkill7.key3(SA6);
      ESkill7.key4(SA6);
      ESkill8.key1(SA7);
      ESkill8.key3(SA7);
      ESkill8.key4(SA7);
      ESkill9.key1(SA8);
      ESkill9.key3(SA8);
      ESkill9.key4(SA8);
      Skill03.key1(SA2);
      Skill03.key3(SA2);
      Skill03.key4(SA2);
      Skill04.key1(SA3);
      Skill04.key3(SA3);
      Skill04.key4(SA3);
      Skill06.key1(SA4);
      Skill06.key3(SA4);
      Skill06.key4(SA4);
      Skill07.key1(SA5);
      Skill07.key3(SA5);
      Skill07.key4(SA5);
      Skill08.key1(SA6);
      Skill08.key3(SA6);
      Skill08.key4(SA6);
      Skill09.key1(SA7);
      Skill09.key3(SA7);
      Skill09.key4(SA7);
      Skill10.key1(SA8);
      Skill10.key3(SA8);
      Skill10.key4(SA8);
      TSkill2.key1(SA2);
      TSkill2.key3(SA2);
      TSkill2.key4(SA2);
      TSkill3.key1(SA3);
      TSkill3.key3(SA3);
      TSkill3.key4(SA3);
      TSkill5.key1(SA4);
      TSkill5.key3(SA4);
      TSkill5.key4(SA4);
      TSkill6.key1(SA5);
      TSkill6.key3(SA5);
      TSkill6.key4(SA5);
      TSkill7.key1(SA6);
      TSkill7.key3(SA6);
      TSkill7.key4(SA6);
      TSkill8.key1(SA7);
      TSkill8.key3(SA7);
      TSkill8.key4(SA7);
      TSkill9.key1(SA8);
      TSkill9.key3(SA8);
      TSkill9.key4(SA8);
      Skill030.key1(SA2);
      Skill030.key3(SA2);
      Skill030.key4(SA2);
      TSkill20.key1(SA2);
      TSkill20.key3(SA2);
      TSkill20.key4(SA2);
      ESkill20.key1(SA2);
      ESkill20.key3(SA2);
      ESkill20.key4(SA2);
      dualskill = registryWorker.build(
         "dualskill", DualInnate::new, ComboBasicAttack.createComboBasicAttack().setMaxPressTime(1).setReserveTime(9).setCombo(root).setShouldDrawGui(false)
      );
   }
}
