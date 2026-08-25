package com.dmc.invincible_dmc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "invincible_dmc",
   bus = Bus.FORGE
)
public class DMCommands {
   @SubscribeEvent
   public static void registerCommands(RegisterCommandsEvent event) {
      CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_(
                              "dmc"
                           )
                           .requires(src -> src.m_6761_(2)))
                        .then(DebugHudCommands.buildDebugNode()))
                     .then(EffectCommands.buildEffectNode()))
                  .then(SetPlayerStateCommands.buildStateNode()))
               .then(DmcLogCommand.buildLogNode()))
            .then(VoidCommands.buildVoidNode())
      );
   }
}
