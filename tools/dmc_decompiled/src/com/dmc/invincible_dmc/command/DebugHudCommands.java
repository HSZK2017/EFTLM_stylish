package com.dmc.invincible_dmc.command;

import com.dmc.invincible_dmc.client.input.ComboEngineDebugHUD;
import com.dmc.invincible_dmc.client.input.judegementCut.debug.JudgementCutDebugHUD;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.function.BooleanSupplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class DebugHudCommands {
   static LiteralArgumentBuilder<CommandSourceStack> buildDebugNode() {
      return (LiteralArgumentBuilder<CommandSourceStack>)((LiteralArgumentBuilder)Commands.m_82127_("debug")
            .then(toggleHud("jc", "Judgement-cut Debug HUD", DebugHudCommands::toggleJcHud, () -> JudgementCutDebugHUD.enabled)))
         .then(toggleHud("combo", "Combo Engine Debug HUD", DebugHudCommands::toggleComboHud, () -> ComboEngineDebugHUD.enabled));
   }

   private static LiteralArgumentBuilder<CommandSourceStack> toggleHud(String name, String label, Runnable toggle, BooleanSupplier state) {
      return (LiteralArgumentBuilder<CommandSourceStack>)Commands.m_82127_(name).executes(ctx -> {
         toggle.run();
         ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_(label + " -> " + (state.getAsBoolean() ? "ON" : "OFF")), true);
         return 1;
      });
   }

   private static void toggleJcHud() {
      JudgementCutDebugHUD.enabled = !JudgementCutDebugHUD.enabled;
   }

   private static void toggleComboHud() {
      ComboEngineDebugHUD.enabled = !ComboEngineDebugHUD.enabled;
   }
}
