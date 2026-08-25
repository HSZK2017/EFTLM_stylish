package com.Yujin.onegradefixer.epicmoonmod.comboevents.events;

import com.Yujin.onegradefixer.epicmoonmod.client.DualHud;
import com.Yujin.onegradefixer.epicmoonmod.client.TSHud;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent.Pre;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@EventBusSubscriber(
   modid = "epicmoonmod",
   value = {Dist.CLIENT}
)
public class EMClientEvent {
   @SubscribeEvent
   public static void onRenderGui(Pre event) {
      RenderSystem.enableBlend();
      if (Minecraft.m_91087_().f_91080_ == null && !Minecraft.m_91087_().m_91104_()) {
         TSHud.RenderGui(event.getGuiGraphics(), event.getWindow(), event.getPartialTick());
         DualHud.RenderGui(event.getGuiGraphics(), event.getWindow(), event.getPartialTick());
      }

      RenderSystem.disableBlend();
   }

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.phase == Phase.END) {
         Player player = event.player;
         PlayerPatch<?> patch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
         if (patch != null) {
            EMEventsutil.applyPendingMode(patch);
         }
      }
   }
}
