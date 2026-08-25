package com.dmc.invincible_dmc.command;

import com.dmc.invincible_dmc.client.gui.DMCKeyBindsScreen;
import com.dmc.invincible_dmc.client.gui.DMConfigScreen;
import com.dmc.invincible_dmc.client.gui.vergilstatus.VergilStatusConfigScreen;
import com.dmc.invincible_dmc.client.render.cinematic.CinematicBarsWorldRenderer;
import com.dmc.invincible_dmc.utils.vfx.CinematicBarsUtils;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "invincible_dmc",
   value = {Dist.CLIENT}
)
public class DMCHudCommands {
   @SubscribeEvent
   public static void register(RegisterClientCommandsEvent event) {
      event.getDispatcher().register((LiteralArgumentBuilder)Commands.m_82127_("dmconfig").executes(ctx -> {
         Minecraft.m_91087_().m_91152_(new DMConfigScreen(Minecraft.m_91087_().f_91080_));
         return 1;
      }));
      event.getDispatcher().register((LiteralArgumentBuilder)Commands.m_82127_("dmckey").executes(ctx -> {
         Minecraft.m_91087_().m_91152_(new DMCKeyBindsScreen(null));
         return 1;
      }));
      event.getDispatcher().register((LiteralArgumentBuilder)Commands.m_82127_("dmc_hud").executes(ctx -> {
         Minecraft.m_91087_().m_91152_(new VergilStatusConfigScreen());
         return 1;
      }));
      event.getDispatcher()
         .register(
            (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_("dmc_mask")
                  .then(Commands.m_82127_("on").then(Commands.m_82129_("height", FloatArgumentType.floatArg(0.01F, 0.45F)).executes(ctx -> {
                     float height = FloatArgumentType.getFloat(ctx, "height");
                     CinematicBarsWorldRenderer.enableCommandMask(height);
                     ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_("DMC cinematic mask enabled: height=" + height), false);
                     return 1;
                  }))))
               .then(Commands.m_82127_("off").executes(ctx -> {
                  CinematicBarsWorldRenderer.disableCommandMask();
                  ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_("DMC cinematic mask disabled"), false);
                  return 1;
               }))
         );
      event.getDispatcher()
         .register(
            (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_("dmc_bars")
                  .then(Commands.m_82127_("on").then(Commands.m_82129_("height", FloatArgumentType.floatArg(0.01F, 0.45F)).executes(ctx -> {
                     float height = FloatArgumentType.getFloat(ctx, "height");
                     CinematicBarsUtils.enableCommandBars(height);
                     ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_("DMC cinematic bars enabled: height=" + height), false);
                     return 1;
                  }))))
               .then(Commands.m_82127_("off").executes(ctx -> {
                  CinematicBarsUtils.disableCommandBars();
                  ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_("DMC cinematic bars disabled"), false);
                  return 1;
               }))
         );
   }
}
