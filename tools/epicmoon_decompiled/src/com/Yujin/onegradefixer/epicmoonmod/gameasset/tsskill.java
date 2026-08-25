package com.Yujin.onegradefixer.epicmoonmod.gameasset;

import com.Yujin.onegradefixer.epicmoonmod.animations.EMAnimations;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.condition.AmmoCondition;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.condition.SavageTigermarkCondition;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.condition.TigermarkCondition;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.AmplitudeConversionEvent;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.Camera;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.ChangeModeEvent;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.EffekEvent;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.NextAttackEvent;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.ReloadEvent;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.ReloadEvent2;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.ShootEvent;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.TremorBurstEvent;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.TremorEvent;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.UnrelentingSpiritEvent;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.events.UnrelentingSpiritSinEvent;
import com.Yujin.onegradefixer.epicmoonmod.skill.weapon_innate.TsInnate;
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
public class tsskill {
   public static Skill tsskill;

   @SubscribeEvent
   public static void BuildSkills(SkillBuildEvent event) {
      ModRegistryWorker registryWorker = event.createRegistryWorker("epicmoonmod");
      ComboNode root = ComboNode.create();
      ComboNode Dodge = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_PARRY)
         .setPriority(2)
         .addCondition(new DodgeSuccessCondition())
         .addHitEvent(TremorEvent.tremorevent())
         .addHitEvent(Camera.Shake(10, 2.0F, 5.0F, 20.0F));
      ComboNode SDodge = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TPARRY)
         .setDamageMultiplier(ValueModifier.multiplier(0.75F))
         .addCondition(new DodgeSuccessCondition())
         .setImpactMultiplier(0.75F)
         .setPriority(3)
         .setHurtDamageMultiplier(0.5F)
         .addTimeEvent(EffekEvent.Effek(0.8F, "c"))
         .setCanBeInterrupt(false)
         .addCondition(new TigermarkCondition())
         .addTimeEvent(ShootEvent.Shoot(0.4F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent())
         .addHitEvent(Camera.Shake(15, 3.0F, 5.0F, 30.0F));
      ComboNode TDodge = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TPARRY)
         .setPriority(3)
         .addCondition(new DodgeSuccessCondition())
         .setHurtDamageMultiplier(0.5F)
         .setCanBeInterrupt(false)
         .addCondition(new SavageTigermarkCondition())
         .addTimeEvent(EffekEvent.Effek(0.8F, "c"))
         .addTimeEvent(ShootEvent.Shoot(0.4F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent())
         .addHitEvent(Camera.Shake(15, 3.0F, 5.0F, 30.0F));
      ComboNode Parry = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_PARRY2)
         .setPriority(2)
         .addHitEvent(TremorEvent.tremorevent())
         .setConvertTime(0.1F)
         .addCondition(new ParrySuccessCondition())
         .addHitEvent(Camera.Shake(10, 2.0F, 5.0F, 20.0F));
      ComboNode SParry = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TPARRY2)
         .setPriority(3)
         .setDamageMultiplier(ValueModifier.multiplier(0.75F))
         .setImpactMultiplier(0.75F)
         .addCondition(new TigermarkCondition())
         .addTimeEvent(EffekEvent.Effek(0.6F, "c"))
         .addTimeEvent(ShootEvent.Shoot(0.1F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent())
         .setConvertTime(0.1F)
         .addCondition(new ParrySuccessCondition())
         .addHitEvent(Camera.Shake(15, 3.0F, 5.0F, 30.0F));
      ComboNode TParry = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TPARRY2)
         .setPriority(3)
         .addCondition(new SavageTigermarkCondition())
         .addTimeEvent(EffekEvent.Effek(0.6F, "c"))
         .addTimeEvent(ShootEvent.Shoot(0.1F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent())
         .setConvertTime(0.1F)
         .addCondition(new ParrySuccessCondition())
         .addHitEvent(Camera.Shake(15, 3.0F, 5.0F, 30.0F));
      ComboNode Dash = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_DASH)
         .setPriority(5)
         .addCondition(new SprintingCondition())
         .addHitEvent(TremorEvent.tremorevent())
         .addHitEvent(Camera.Shake(15, 2.0F, 5.0F, 20.0F));
      ComboNode Dashot = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TDASHOT)
         .setDamageMultiplier(ValueModifier.multiplier(0.75F))
         .setImpactMultiplier(0.75F)
         .setPriority(5)
         .setHurtDamageMultiplier(0.5F)
         .setCanBeInterrupt(false)
         .addTimeEvent(EffekEvent.Effek(0.65F, "c"))
         .addCondition(new SprintingCondition())
         .addCondition(new TigermarkCondition())
         .addTimeEvent(ShootEvent.Shoot(0.4F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent())
         .addHitEvent(Camera.Shake(15, 3.0F, 5.0F, 30.0F));
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
         .addHitEvent(TremorEvent.tremorevent())
         .addHitEvent(Camera.Shake(15, 2.0F, 5.0F, 30.0F));
      ComboNode Air = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_AIRSLASH)
         .setPriority(6)
         .addCondition(new JumpCondition())
         .addHitEvent(TremorEvent.tremorevent())
         .addHitEvent(Camera.Shake(15, 1.0F, 5.0F, 20.0F));
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
         .addHitEvent(TremorEvent.tremorevent())
         .addHitEvent(Camera.Shake(15, 3.0F, 5.0F, 30.0F));
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
         .addHitEvent(TremorEvent.tremorevent())
         .addHitEvent(Camera.Shake(15, 2.0F, 5.0F, 30.0F));
      ComboNode Auto1 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_AUTO1)
         .setPriority(1)
         .addHitEvent(TremorEvent.tremorevent())
         .setConvertTime(0.1F)
         .addHitEvent(Camera.Shake(10, 2.0F, 5.0F, 20.0F));
      ComboNode Auto2 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_AUTO2)
         .addHitEvent(TremorEvent.tremorevent())
         .addHitEvent(Camera.Shake(10, 2.0F, 5.0F, 20.0F));
      ComboNode Auto3 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_AUTO3)
         .setCanBeInterrupt(false)
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .addHitEvent(Camera.Shake(10, 1.0F, 5.0F, 20.0F));
      ComboNode Shot1 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSHOT1)
         .setDamageMultiplier(ValueModifier.multiplier(0.75F))
         .setImpactMultiplier(0.75F)
         .setPriority(2)
         .setHurtDamageMultiplier(0.5F)
         .addTimeEvent(EffekEvent.Effek(0.3F, "c"))
         .setCanBeInterrupt(false)
         .addCondition(new TigermarkCondition())
         .addTimeEvent(ShootEvent.Shoot(0.3F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent())
         .addHitEvent(Camera.Shake(15, 3.0F, 5.0F, 30.0F));
      ComboNode Shot2 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSHOT3)
         .setDamageMultiplier(ValueModifier.multiplier(0.75F))
         .setImpactMultiplier(0.75F)
         .setPriority(2)
         .setHurtDamageMultiplier(0.5F)
         .addTimeEvent(EffekEvent.Effek(0.5F, "c"))
         .setCanBeInterrupt(false)
         .addCondition(new TigermarkCondition())
         .addTimeEvent(ShootEvent.Shoot(0.4F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent())
         .addHitEvent(Camera.Shake(15, 3.0F, 5.0F, 30.0F));
      ComboNode Shot4 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSHOT2)
         .setDamageMultiplier(ValueModifier.multiplier(0.75F))
         .setImpactMultiplier(0.75F)
         .setPriority(2)
         .setHurtDamageMultiplier(0.5F)
         .addTimeEvent(EffekEvent.Effek(0.5F, "c"))
         .setCanBeInterrupt(false)
         .addCondition(new TigermarkCondition())
         .addTimeEvent(ShootEvent.Shoot(0.4F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent())
         .addHitEvent(Camera.Shake(15, 3.0F, 5.0F, 30.0F));
      ComboNode Shot3 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSHOT5)
         .setDamageMultiplier(ValueModifier.multiplier(0.75F))
         .setPriority(2)
         .setHurtDamageMultiplier(0.5F)
         .setCanBeInterrupt(false)
         .addTimeEvent(EffekEvent.Effek(1.0F, "c"))
         .addCondition(new TigermarkCondition())
         .addTimeEvent(EffekEvent.Effek(1.0F, "c"))
         .addTimeEvent(ShootEvent.Shoot(0.5F))
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .addHitEvent(Camera.Shake(10, 2.0F, 5.0F, 30.0F));
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
         .addHitEvent(TremorEvent.tremorevent())
         .addHitEvent(Camera.Shake(15, 3.0F, 5.0F, 30.0F));
      ComboNode TShot2 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSHOT3)
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
         .addHitEvent(TremorEvent.tremorevent())
         .addHitEvent(Camera.Shake(15, 3.0F, 5.0F, 30.0F));
      ComboNode TShot3 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSHOT2)
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
         .addHitEvent(TremorEvent.tremorevent())
         .addHitEvent(Camera.Shake(15, 3.0F, 5.0F, 30.0F));
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
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .addHitEvent(Camera.Shake(10, 2.0F, 5.0F, 30.0F));
      ComboNode Skill = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_SKILL)
         .setPriority(1)
         .setCanBeInterrupt(false)
         .addCondition(new AmmoCondition())
         .addTimeEvent(ReloadEvent.Reload(0.8F))
         .addTimeEvent(ChangeModeEvent.ModeChange(0.0F, 0));
      ComboNode Skill02 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSKILL)
         .setDamageMultiplier(ValueModifier.multiplier(0.75F))
         .setImpactMultiplier(0.75F)
         .setPriority(5)
         .setCanBeInterrupt(false)
         .addCondition(new StackCondition(2, 2))
         .addCondition(new TigermarkCondition())
         .addTimeEvent(ShootEvent.Shoot(2.7F))
         .addTimeEvent(EffekEvent.Effek(0.7F, "c"))
         .addTimeEvent(EffekEvent.Effek(1.9F, "c"))
         .addTimeEvent(EffekEvent.Effek(2.7F, "e"))
         .addTimeEvent(EffekEvent.Effek(3.1F, "c"))
         .addTimeEvent(UnrelentingSpiritEvent.USE(0.0F))
         .addTimeEvent(TimeStampedEvent.createTimeCommandEvent(0.0F, "invincible consumeStack 2", false))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(AmplitudeConversionEvent.amplitudeconversion())
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .addTimeEvent(ReloadEvent2.Reload2(3.5F));
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
         .addTimeEvent(NextAttackEvent.next(1.4F, ComboTypes.KEY_1))
         .addHitEvent(Camera.Shake(15, 2.0F, 5.0F, 30.0F));
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
         .addTimeEvent(NextAttackEvent.next(1.2F, ComboTypes.KEY_1))
         .addHitEvent(Camera.Shake(15, 2.0F, 5.0F, 30.0F));
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
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .addHitEvent(Camera.Shake(15, 2.0F, 5.0F, 30.0F));
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
         .addTimeEvent(NextAttackEvent.next(1.8F, ComboTypes.KEY_1))
         .addHitEvent(Camera.Shake(15, 2.0F, 5.0F, 30.0F));
      ComboNode Skill07 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSKILL6)
         .setDamageMultiplier(ValueModifier.multiplier(0.75F))
         .setImpactMultiplier(0.75F)
         .setPriority(2)
         .setCanBeInterrupt(false)
         .addCondition(new TigermarkCondition())
         .addTimeEvent(EffekEvent.Effek(0.5F, "c"))
         .addTimeEvent(ShootEvent.Shoot(0.4F))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent())
         .addTimeEvent(NextAttackEvent.next(1.0F, ComboTypes.KEY_1))
         .addHitEvent(Camera.Shake(15, 3.0F, 5.0F, 50.0F));
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
         .addTimeEvent(NextAttackEvent.next(1.7F, ComboTypes.KEY_1))
         .addHitEvent(Camera.Shake(15, 3.0F, 5.0F, 50.0F));
      ComboNode Skill09 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSKILL8)
         .setDamageMultiplier(ValueModifier.multiplier(0.75F))
         .setImpactMultiplier(0.75F)
         .setPriority(2)
         .setCanBeInterrupt(false)
         .addCondition(new TigermarkCondition())
         .addTimeEvent(EffekEvent.Effek(1.2F, "c"))
         .addTimeEvent(ShootEvent.Shoot(0.6F))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent())
         .addTimeEvent(NextAttackEvent.next(1.5F, ComboTypes.KEY_1))
         .addHitEvent(Camera.Shake(15, 3.0F, 5.0F, 50.0F));
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
         .addTimeEvent(NextAttackEvent.next(1.3F, ComboTypes.KEY_1))
         .addHitEvent(Camera.Shake(15, 2.0F, 5.0F, 50.0F));
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
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .addTimeEvent(ChangeModeEvent.ModeChange(3.2F, 0))
         .addTimeEvent(ReloadEvent2.Reload2(3.5F));
      ComboNode TSkill = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSKILL)
         .setPriority(5)
         .setCanBeInterrupt(false)
         .addCondition(new StackCondition(2, 2))
         .addCondition(new SavageTigermarkCondition())
         .addTimeEvent(ShootEvent.Shoot(2.7F))
         .addTimeEvent(EffekEvent.Effek(0.7F, "c"))
         .addTimeEvent(EffekEvent.Effek(1.9F, "c"))
         .addTimeEvent(EffekEvent.Effek(2.7F, "i"))
         .addTimeEvent(EffekEvent.Effek(3.1F, "c"))
         .addTimeEvent(UnrelentingSpiritEvent.USE(0.0F))
         .addTimeEvent(TimeStampedEvent.createTimeCommandEvent(0.0F, "invincible consumeStack 2", false))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(AmplitudeConversionEvent.amplitudeconversion())
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .addHitEvent(TremorBurstEvent.tremorburstevent())
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
         .addTimeEvent(NextAttackEvent.next(1.4F, ComboTypes.KEY_1))
         .addHitEvent(Camera.Shake(15, 2.0F, 5.0F, 50.0F));
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
         .addTimeEvent(NextAttackEvent.next(1.2F, ComboTypes.KEY_1))
         .addHitEvent(Camera.Shake(15, 2.0F, 5.0F, 50.0F));
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
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .addHitEvent(Camera.Shake(15, 2.0F, 5.0F, 50.0F));
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
         .addTimeEvent(NextAttackEvent.next(1.8F, ComboTypes.KEY_1))
         .addHitEvent(Camera.Shake(15, 2.0F, 5.0F, 50.0F));
      ComboNode TSkill6 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSKILL6)
         .setPriority(2)
         .setCanBeInterrupt(false)
         .addCondition(new SavageTigermarkCondition())
         .addTimeEvent(EffekEvent.Effek(0.5F, "c"))
         .addTimeEvent(ShootEvent.Shoot(0.4F))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent())
         .addTimeEvent(NextAttackEvent.next(1.0F, ComboTypes.KEY_1))
         .addHitEvent(Camera.Shake(15, 3.0F, 5.0F, 50.0F));
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
         .addTimeEvent(NextAttackEvent.next(1.7F, ComboTypes.KEY_1))
         .addHitEvent(Camera.Shake(15, 3.0F, 5.0F, 50.0F));
      ComboNode TSkill8 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_TSKILL8)
         .setPriority(2)
         .setCanBeInterrupt(false)
         .addCondition(new SavageTigermarkCondition())
         .addTimeEvent(EffekEvent.Effek(1.2F, "c"))
         .addTimeEvent(ShootEvent.Shoot(0.6F))
         .setNotCharge(true)
         .addHitEvent(new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
            if (entity != null) {
               entity.m_20254_(5);
            }
         }))
         .addHitEvent(TremorEvent.tremorevent())
         .addTimeEvent(NextAttackEvent.next(1.5F, ComboTypes.KEY_1))
         .addHitEvent(Camera.Shake(15, 3.0F, 5.0F, 50.0F));
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
         .addTimeEvent(NextAttackEvent.next(1.3F, ComboTypes.KEY_1))
         .addHitEvent(Camera.Shake(15, 2.0F, 5.0F, 50.0F));
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
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .addTimeEvent(ChangeModeEvent.ModeChange(3.2F, 0))
         .addTimeEvent(ReloadEvent2.Reload2(3.5F));
      ComboNode ESkill = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_ESKILL)
         .setPriority(1)
         .setCanBeInterrupt(false)
         .addCondition(new StackCondition(2, 2))
         .addTimeEvent(ShootEvent.Shoot(2.7F))
         .addTimeEvent(EffekEvent.Effek(0.7F, "c"))
         .addTimeEvent(EffekEvent.Effek(1.9F, "c"))
         .addTimeEvent(EffekEvent.Effek(2.7F, "e"))
         .addTimeEvent(EffekEvent.Effek(3.1F, "c"))
         .addTimeEvent(UnrelentingSpiritEvent.USE(0.0F))
         .addTimeEvent(TimeStampedEvent.createTimeCommandEvent(0.0F, "invincible consumeStack 2", false))
         .setNotCharge(true)
         .addHitEvent(TremorEvent.tremorevent())
         .addHitEvent(AmplitudeConversionEvent.amplitudeconversion())
         .addHitEvent(TremorBurstEvent.tremorburstevent())
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
         .addTimeEvent(NextAttackEvent.next(1.4F, ComboTypes.KEY_1))
         .addHitEvent(Camera.Shake(10, 1.0F, 5.0F, 50.0F));
      ComboNode ESkill3 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_ESKILL3)
         .setHurtDamageMultiplier(0.25F)
         .setPriority(1)
         .setCanBeInterrupt(false)
         .addTimeEvent(ShootEvent.Shoot(0.1F))
         .setNotCharge(true)
         .addHitEvent(AmplitudeConversionEvent.amplitudeconversion())
         .addTimeEvent(NextAttackEvent.next(1.2F, ComboTypes.KEY_1))
         .addHitEvent(Camera.Shake(10, 1.0F, 5.0F, 50.0F));
      ComboNode ESkill4 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_ESKILL4)
         .setHurtDamageMultiplier(0.25F)
         .setPriority(1)
         .setCanBeInterrupt(false)
         .addTimeEvent(ShootEvent.Shoot(0.1F))
         .addTimeEvent(EffekEvent.Effek(1.1F, "c"))
         .setNotCharge(true)
         .addHitEvent(TremorBurstEvent.tremorburstevent())
         .addHitEvent(Camera.Shake(10, 1.0F, 5.0F, 50.0F));
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
         .addTimeEvent(NextAttackEvent.next(1.8F, ComboTypes.KEY_1))
         .addHitEvent(Camera.Shake(10, 1.0F, 5.0F, 50.0F));
      ComboNode ESkill6 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_ESKILL6)
         .setPriority(1)
         .setCanBeInterrupt(false)
         .addTimeEvent(EffekEvent.Effek(0.5F, "c"))
         .addTimeEvent(ShootEvent.Shoot(0.4F))
         .setNotCharge(true)
         .addHitEvent(TremorEvent.tremorevent())
         .addTimeEvent(NextAttackEvent.next(1.0F, ComboTypes.KEY_1))
         .addHitEvent(Camera.Shake(10, 2.0F, 5.0F, 50.0F));
      ComboNode ESkill7 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_ESKILL7)
         .setPriority(1)
         .setCanBeInterrupt(false)
         .addTimeEvent(ShootEvent.Shoot(0.1F))
         .addTimeEvent(EffekEvent.Effek(1.1F, "c"))
         .setNotCharge(true)
         .addHitEvent(TremorEvent.tremorevent())
         .addTimeEvent(NextAttackEvent.next(1.7F, ComboTypes.KEY_1))
         .addHitEvent(Camera.Shake(10, 2.0F, 5.0F, 50.0F));
      ComboNode ESkill8 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_ESKILL8)
         .setPriority(1)
         .setCanBeInterrupt(false)
         .addTimeEvent(EffekEvent.Effek(1.2F, "c"))
         .addTimeEvent(ShootEvent.Shoot(0.6F))
         .setNotCharge(true)
         .addHitEvent(TremorEvent.tremorevent())
         .addTimeEvent(NextAttackEvent.next(1.5F, ComboTypes.KEY_1))
         .addHitEvent(Camera.Shake(10, 2.0F, 5.0F, 50.0F));
      ComboNode ESkill9 = ComboNode.createNode(EMAnimations.TENTAI_SEITOU_ESKILL9)
         .setPriority(1)
         .setCanBeInterrupt(false)
         .addTimeEvent(EffekEvent.Effek(1.0F, "c"))
         .addTimeEvent(ShootEvent.Shoot(0.5F))
         .setNotCharge(true)
         .addHitEvent(AmplitudeConversionEvent.amplitudeconversion())
         .addTimeEvent(NextAttackEvent.next(1.3F, ComboTypes.KEY_1))
         .addHitEvent(Camera.Shake(10, 1.0F, 5.0F, 50.0F));
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
         .addConditionNode(Dodge)
         .addConditionNode(Parry);
      ComboNode BasicAttack2 = ComboNode.create().addConditionNode(Auto2).addConditionNode(Dodge).addConditionNode(Parry);
      ComboNode BasicAttack3 = ComboNode.create().addConditionNode(Auto3).addConditionNode(Dodge).addConditionNode(Parry);
      ComboNode Skills = ComboNode.create()
         .addConditionNode(Skill)
         .addConditionNode(Shot1)
         .addConditionNode(TShot1)
         .addConditionNode(Dashot)
         .addConditionNode(TDashot)
         .addConditionNode(Airshot)
         .addConditionNode(TAirshot)
         .addConditionNode(TParry)
         .addConditionNode(SParry)
         .addConditionNode(TDodge)
         .addConditionNode(SDodge);
      ComboNode Skill2 = ComboNode.create()
         .addConditionNode(Skill)
         .addConditionNode(Shot2)
         .addConditionNode(TShot2)
         .addConditionNode(TParry)
         .addConditionNode(SParry)
         .addConditionNode(TDodge)
         .addConditionNode(SDodge);
      ComboNode Skill3 = ComboNode.create()
         .addConditionNode(Skill)
         .addConditionNode(Shot3)
         .addConditionNode(TShot4)
         .addConditionNode(TParry)
         .addConditionNode(SParry)
         .addConditionNode(TDodge)
         .addConditionNode(SDodge);
      ComboNode Skill4 = ComboNode.create()
         .addConditionNode(Skill)
         .addConditionNode(Shot4)
         .addConditionNode(TShot3)
         .addConditionNode(TParry)
         .addConditionNode(SParry)
         .addConditionNode(TDodge)
         .addConditionNode(SDodge);
      ComboNode SA = ComboNode.create()
         .addConditionNode(Skill02)
         .addConditionNode(Skill03)
         .addConditionNode(TSkill2)
         .addConditionNode(TSkill)
         .addConditionNode(TSkill5)
         .addConditionNode(ESkill)
         .addConditionNode(ESkill2)
         .addConditionNode(ESkill5)
         .addConditionNode(Skill06);
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
      Dodge.key1(BasicAttack3);
      Dodge.key3(Skill3);
      Dodge.key4(SA);
      SDodge.key1(BasicAttack3);
      SDodge.key3(Skill3);
      SDodge.key4(SA);
      TDodge.key1(BasicAttack3);
      TDodge.key3(Skill3);
      TDodge.key4(SA);
      Parry.key1(BasicAttack);
      Parry.key3(Skills);
      Parry.key4(SA);
      SParry.key1(BasicAttack);
      SParry.key3(Skills);
      SParry.key4(SA);
      TParry.key1(BasicAttack);
      TParry.key3(Skills);
      TParry.key4(SA);
      Air.key1(BasicAttack2);
      Air.key3(Skill4);
      Air.key4(SA);
      Airshot.key1(BasicAttack2);
      Airshot.key3(Skill4);
      Airshot.key4(SA);
      TAirshot.key1(BasicAttack2);
      TAirshot.key3(Skill4);
      TAirshot.key4(SA);
      Dash.key1(BasicAttack2);
      Dash.key3(Skill4);
      Dash.key4(SA);
      Dashot.key1(BasicAttack2);
      Dashot.key3(Skill4);
      Dashot.key4(SA);
      TDashot.key1(BasicAttack2);
      TDashot.key3(Skill4);
      TDashot.key4(SA);
      Auto1.key1(BasicAttack2);
      Auto1.key3(Skill2);
      Auto1.key4(SA);
      Auto2.key1(BasicAttack3);
      Auto2.key3(Skill3);
      Auto2.key4(SA);
      Auto3.key1(BasicAttack);
      Auto3.key3(Skills);
      Auto3.key4(SA);
      Shot1.key1(BasicAttack2);
      Shot1.key3(Skill2);
      Shot1.key4(SA);
      Shot2.key1(BasicAttack3);
      Shot2.key3(Skill3);
      Shot2.key4(SA);
      Shot3.key1(BasicAttack);
      Shot3.key3(Skills);
      Shot3.key4(SA);
      Shot4.key1(BasicAttack3);
      Shot4.key3(Skill3);
      Shot4.key4(SA);
      TShot1.key1(BasicAttack2);
      TShot1.key3(Skill2);
      TShot1.key4(SA);
      TShot2.key1(BasicAttack3);
      TShot2.key3(Skill3);
      TShot2.key4(SA);
      TShot3.key1(BasicAttack3);
      TShot3.key3(Skill3);
      TShot3.key4(SA);
      TShot4.key1(BasicAttack);
      TShot4.key3(Skills);
      TShot4.key4(SA);
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
      Skill.key1(BasicAttack);
      Skill.key3(Skills);
      Skill.key4(SA);
      tsskill = registryWorker.build(
         "tsskill", TsInnate::new, ComboBasicAttack.createComboBasicAttack().setMaxPressTime(1).setReserveTime(9).setCombo(root).setShouldDrawGui(false)
      );
   }
}
