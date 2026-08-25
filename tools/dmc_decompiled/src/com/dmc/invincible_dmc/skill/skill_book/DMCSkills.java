package com.dmc.invincible_dmc.skill.skill_book;

import com.dmc.invincible_dmc.item.DMCreativeTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.api.forgeevent.SkillBuildEvent.ModRegistryWorker;
import yesman.epicfight.skill.Skill;

@EventBusSubscriber(
   modid = "invincible_dmc",
   bus = Bus.MOD
)
public class DMCSkills {
   public static Skill INSTANT_JUDGEMENT_CUT_END;

   @SubscribeEvent
   public static void buildSkills(SkillBuildEvent event) {
      ModRegistryWorker registryWorker = event.createRegistryWorker("invincible_dmc");
      INSTANT_JUDGEMENT_CUT_END = registryWorker.build(
         "instant_judgement_cut_end",
         Instant_Judgement_Cut_EndSkill::new,
         (Instant_Judgement_Cut_EndSkill.Builder)Instant_Judgement_Cut_EndSkill.createSkill().setCreativeTab((CreativeModeTab)DMCreativeTabs.ITEMS.get())
      );
   }
}
