package com.dmc.invincible_dmc.client.gui;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent.Color;
import net.minecraftforge.client.event.RenderTooltipEvent.GatherComponents;
import net.minecraftforge.client.event.RenderTooltipEvent.Pre;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "invincible_dmc",
   bus = Bus.FORGE,
   value = {Dist.CLIENT}
)
public class ClientTooltipEvents {
   private static final YamatoTooltipFrameRenderer renderer = new YamatoTooltipFrameRenderer();

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onGatherTooltipComponents(GatherComponents event) {
      renderer.onGatherTooltipComponents(event);
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onPreRenderTooltip(Pre event) {
      renderer.onPreRenderTooltip(event);
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onTooltipColor(Color event) {
      renderer.onTooltipColor(event);
   }
}
