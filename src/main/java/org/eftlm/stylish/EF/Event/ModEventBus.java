package org.eftlm.stylish.EF.Event;

import net.EFTLM.EF.API.Event.MaidSkillBuildEvent;
import net.EFTLM.EF.Register.EFTLM_Tab;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eftlm.stylish.EF.Skill.StylishCombatSkill;
import org.eftlm.stylish.EFTLMStylish;

/**
 * 模组总线事件：注册华丽连段技能（EFTLM 官方扩展 API：MaidSkillBuildEvent）。
 * <p>
 * 只注册一本技能书（stylish_combat）：格挡 / 冥想逻辑已内置于该技能，
 * 技能书放入"史诗战斗：车万女仆"物品分类（EFTLM_Tab.SKILL）。
 */
@Mod.EventBusSubscriber(
        modid = EFTLMStylish.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class ModEventBus {
    private static final Logger LOGGER = LogManager.getLogger("eftlm_stylish");

    @SubscribeEvent
    public static void MaidSkillBuild(MaidSkillBuildEvent event) {
        try {
            event.build(ResourceLocation.fromNamespaceAndPath(EFTLMStylish.MODID, "stylish_combat"),
                    StylishCombatSkill::new,
                    StylishCombatSkill.createBuilder().setCreativeTab(EFTLM_Tab.SKILL.get()));
            LOGGER.info("[DIAG] MaidSkillBuildEvent registered: stylish_combat (tab={})", EFTLM_Tab.SKILL.getId());
        } catch (Throwable t) {
            LOGGER.error("[DIAG] MaidSkillBuildEvent register FAILED!", t);
        }
    }
}
