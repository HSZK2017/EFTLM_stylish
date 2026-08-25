package com.dmc.invincible_dmc.skill.weapon_combo;

import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.api.skill.ComboNodeGroup;
import com.dmc.invincible_dmc.api.skill.CrazyComboNode;
import com.dmc.invincible_dmc.api.skill.SubComboNode;
import com.dmc.invincible_dmc.api.skill.TapHoldNode;
import com.dmc.invincible_dmc.client.renderer.vfx.YamatoSlashEvents;
import com.dmc.invincible_dmc.conditions.DirectionCondition;
import com.dmc.invincible_dmc.conditions.DirectionalSequenceCondition;
import com.dmc.invincible_dmc.conditions.JumpCondition;
import com.dmc.invincible_dmc.conditions.LongPressCondition;
import com.dmc.invincible_dmc.conditions.SprintingCondition;
import com.dmc.invincible_dmc.skill.weapon_innate.ComboBasicAttack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.api.forgeevent.SkillBuildEvent.ModRegistryWorker;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.skill.Skill;

@EventBusSubscriber(
   modid = "invincible_dmc",
   bus = Bus.MOD
)
public class DMCDemoSkills {
   public static Skill COMBO_DEMO;

   @SubscribeEvent
   public static void buildSkills(SkillBuildEvent event) {
      ModRegistryWorker registryWorker = event.createRegistryWorker("invincible_dmc");
      ComboNode root = ComboNode.create();
      ComboNode auto1 = ComboNode.createNode(Animations.TACHI_AUTO1).setPriority(1).addTimeEvent(YamatoSlashEvents.spawnSlash(0.083333336F, 16724787));
      ComboNode auto2 = ComboNode.createNode(Animations.TACHI_AUTO2).setPriority(1);
      ComboNode auto3 = ComboNode.createNode(Animations.TACHI_AUTO3).setPriority(1);
      ComboNode jumpAttack = ComboNode.createNode(Animations.SWORD_AIR_SLASH).setPriority(3).addCondition(new JumpCondition());
      ComboNode dashAttack = ComboNode.createNode(Animations.SWORD_DASH).setPriority(2).addCondition(new SprintingCondition());
      ComboNode heavyAttack = ComboNode.createNode(Animations.SWEEPING_EDGE).setPriority(4).addCondition(new LongPressCondition());
      ComboNode heavyAttack1 = ComboNode.createNode(Animations.RUSHING_TEMPO1).setPriority(4).addCondition(new LongPressCondition());
      ComboNode heavyAttack2 = ComboNode.createNode(Animations.RUSHING_TEMPO2).setPriority(4).addCondition(new LongPressCondition());
      ComboNode heavyAttack3 = ComboNode.createNode(Animations.RUSHING_TEMPO3).setPriority(4).addCondition(new LongPressCondition());
      ComboNode spAttack1 = ComboNode.createNode(Animations.BATTOJUTSU_DASH)
         .setPriority(5)
         .addCondition(new DirectionalSequenceCondition(DirectionalSequenceCondition.Sequence.FORWARD_FORWARD));
      ComboNode spAttack2 = ComboNode.createNode(Animations.BATTOJUTSU)
         .setPriority(5)
         .addCondition(new DirectionalSequenceCondition(DirectionalSequenceCondition.Sequence.BACK_FORWARD));
      CrazyComboNode ccNode = CrazyComboNode.create(SubComboNode.create(Animations.TACHI_AUTO3))
         .setCcChase(SubComboNode.create(Animations.UCHIGATANA_SHEATHING_AUTO))
         .setCcFinish(SubComboNode.create(Animations.BATTOJUTSU_DASH).setConvertTime(-0.65F))
         .setCcFinishNoChase(SubComboNode.create(Animations.BATTOJUTSU).setConvertTime(-0.65F))
         .setCcMaxChases(8)
         .addCondition(new DirectionCondition(DirectionCondition.Direction.UP))
         .setPriority(10);
      TapHoldNode skill = TapHoldNode.create(SubComboNode.create(Animations.BATTOJUTSU))
         .setHold(SubComboNode.create(Animations.BATTOJUTSU_DASH).setConvertTime(-0.65F))
         .setWindupDurationTicks(8);
      ComboNodeGroup commonConditionBasicAttacks = ComboNodeGroup.creatGroup(jumpAttack, dashAttack, spAttack1, spAttack2);
      ComboNodeGroup rootAttack = commonConditionBasicAttacks.step(auto1, heavyAttack);
      ComboNodeGroup attack2 = commonConditionBasicAttacks.step(auto2, heavyAttack1);
      ComboNodeGroup heavy2_follow = commonConditionBasicAttacks.step(auto2);
      ComboNodeGroup attack3 = commonConditionBasicAttacks.step(auto3, heavyAttack2, ccNode);
      ComboNodeGroup heavy3_follow = commonConditionBasicAttacks.step(auto3);
      ComboNodeGroup auto3_follow = commonConditionBasicAttacks.step(auto1, heavyAttack3);
      root.key1(rootAttack);
      rootAttack.fanIn(ComboNode.ComboTypes.KEY_1, new ComboNode[]{commonConditionBasicAttacks, heavyAttack, ccNode, skill});
      heavyAttack1.key1(heavy2_follow);
      heavyAttack2.key1(heavy3_follow);
      heavyAttack3.key1(rootAttack);
      auto1.key1(attack2);
      auto2.key1(attack3);
      auto3.key1(auto3_follow);
      skill.availableVia(ComboNode.ComboTypes.WEAPON_INNATE, root);
      skill.keyWeaponInnate(skill);
      COMBO_DEMO = registryWorker.build(
         "combo_demo", ComboBasicAttack::new, ComboBasicAttack.createComboBasicAttack().setCombo(root).setInputBufferDurationTicks(9).setShouldDrawGui(true)
      );
   }
}
