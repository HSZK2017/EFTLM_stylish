package com.dmc.invincible_dmc.command;

import com.dmc.invincible_dmc.event.VoidEvents;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class VoidCommands {
   static LiteralArgumentBuilder<CommandSourceStack> buildVoidNode() {
      return (LiteralArgumentBuilder<CommandSourceStack>)((LiteralArgumentBuilder)Commands.m_82127_("void")
            .executes(ctx -> teleportToVoid((CommandSourceStack)ctx.getSource())))
         .then(Commands.m_82127_("cleanup").executes(ctx -> cleanupVoid((CommandSourceStack)ctx.getSource())));
   }

   private static int teleportToVoid(CommandSourceStack src) throws CommandSyntaxException {
      ServerPlayer player = src.m_81375_();
      ServerLevel voidLevel = src.m_81377_().m_129880_(VoidEvents.VOID_KEY);
      if (voidLevel == null) {
         src.m_81352_(Component.m_237115_("commands.invincible_dmc.void.not_loaded"));
         return 0;
      } else {
         player.m_8999_(voidLevel, 0.5, 1.0, 0.5, player.m_146908_(), player.m_146909_());
         src.m_288197_(() -> Component.m_237115_("commands.invincible_dmc.void.teleported"), true);
         return 1;
      }
   }

   private static int cleanupVoid(CommandSourceStack src) throws CommandSyntaxException {
      ServerPlayer player = src.m_81375_();
      ServerLevel voidLevel = src.m_81377_().m_129880_(VoidEvents.VOID_KEY);
      if (voidLevel != null) {
         ServerLevel overworld = src.m_81377_().m_129880_(Level.f_46428_);
         if (overworld != null) {
            for (ServerPlayer p : voidLevel.m_6907_()) {
               p.m_8999_(
                  overworld,
                  (double)overworld.m_220360_().m_123341_() + 0.5,
                  (double)overworld.m_220360_().m_123342_(),
                  (double)overworld.m_220360_().m_123343_() + 0.5,
                  p.m_146908_(),
                  p.m_146909_()
               );
            }
         }
      }

      src.m_288197_(() -> Component.m_237115_("commands.invincible_dmc.void.cleanup_done"), false);
      return 1;
   }
}
