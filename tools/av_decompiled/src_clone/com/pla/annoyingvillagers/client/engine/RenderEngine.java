package com.pla.annoyingvillagers.client.engine;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent.RegisterItemRenderer;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class RenderEngine {
   @SubscribeEvent
   public static void registerRenderer(RegisterItemRenderer add) {
      add.addItemRenderer(ResourceLocation.m_214293_("annoyingvillagers", "legendary_sword"), RenderLegendarySword::new);
      add.addItemRenderer(ResourceLocation.m_214293_("annoyingvillagers", "great_sword"), RenderGreatSword::new);
      add.addItemRenderer(ResourceLocation.m_214293_("annoyingvillagers", "obsidian_weapon"), RenderObsidianWeapon::new);
      add.addItemRenderer(ResourceLocation.m_214293_("annoyingvillagers", "shadow_obsidian_weapon"), RenderShadowObsidianWeapon::new);
      add.addItemRenderer(ResourceLocation.m_214293_("annoyingvillagers", "shadow_obsidian_pillar"), RenderShadowObsidianPillar::new);
      add.addItemRenderer(ResourceLocation.m_214293_("annoyingvillagers", "shadow_obsidian_sword"), RenderShadowObsidianSword::new);
      add.addItemRenderer(ResourceLocation.m_214293_("annoyingvillagers", "diamond_blaster_sword"), RenderDiamondBlasterSword::new);
      add.addItemRenderer(ResourceLocation.m_214293_("annoyingvillagers", "diamond_bolt"), RenderDiamondBolt::new);
      add.addItemRenderer(ResourceLocation.m_214293_("annoyingvillagers", "dnax_hooked_sword"), RenderDNAxHookSword::new);
      add.addItemRenderer(ResourceLocation.m_214293_("annoyingvillagers", "red_axe"), RenderRedAxe::new);
      add.addItemRenderer(ResourceLocation.m_214293_("annoyingvillagers", "blackscratcher"), RenderBlackscratcher::new);
      add.addItemRenderer(ResourceLocation.m_214293_("annoyingvillagers", "twin_diamond_spear"), RenderTwinDiamondSpear::new);
   }
}
