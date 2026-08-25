package com.dmc.invincible_dmc.command;

import com.dmc.invincible_dmc.utils.DMCLog;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Locale;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class DmcLogCommand {
   static LiteralArgumentBuilder<CommandSourceStack> buildLogNode() {
      return (LiteralArgumentBuilder<CommandSourceStack>)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_("log")
               .then(Commands.m_82127_("list").executes(ctx -> {
                  ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_(DMCLog.listStatus()), false);
                  return 1;
               })))
            .then(Commands.m_82127_("on").then(Commands.m_82129_("category", StringArgumentType.word()).suggests((ctx, builder) -> {
               for (DMCLog.Category cat : DMCLog.Category.values()) {
                  builder.suggest(cat.name().toLowerCase(Locale.ROOT));
               }

               builder.suggest("all");
               return builder.buildFuture();
            }).executes(ctx -> {
               String name = StringArgumentType.getString(ctx, "category");
               if (!"all".equalsIgnoreCase(name)) {
                  DMCLog.Category cat = findCategory(name);
                  if (cat == null) {
                     ((CommandSourceStack)ctx.getSource()).m_81352_(Component.m_237113_("Unknown category: " + name));
                     return 0;
                  } else {
                     DMCLog.enable(cat);
                     ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_("Log enabled: " + cat.name()), true);
                     return 1;
                  }
               } else {
                  for (DMCLog.Category cat : DMCLog.Category.values()) {
                     DMCLog.enable(cat);
                  }

                  ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_("All log categories enabled"), true);
                  return 1;
               }
            }))))
         .then(Commands.m_82127_("off").then(Commands.m_82129_("category", StringArgumentType.word()).suggests((ctx, builder) -> {
            for (DMCLog.Category cat : DMCLog.Category.values()) {
               builder.suggest(cat.name().toLowerCase(Locale.ROOT));
            }

            builder.suggest("all");
            return builder.buildFuture();
         }).executes(ctx -> {
            String name = StringArgumentType.getString(ctx, "category");
            if (!"all".equalsIgnoreCase(name)) {
               DMCLog.Category cat = findCategory(name);
               if (cat == null) {
                  ((CommandSourceStack)ctx.getSource()).m_81352_(Component.m_237113_("Unknown category: " + name));
                  return 0;
               } else {
                  DMCLog.disable(cat);
                  ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_("Log disabled: " + cat.name()), true);
                  return 1;
               }
            } else {
               for (DMCLog.Category cat : DMCLog.Category.values()) {
                  DMCLog.disable(cat);
               }

               ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_("All log categories disabled"), true);
               return 1;
            }
         })));
   }

   private static DMCLog.Category findCategory(String name) {
      try {
         return DMCLog.Category.valueOf(name.toUpperCase(Locale.ROOT));
      } catch (IllegalArgumentException var2) {
         return null;
      }
   }
}
