package com.dmc.invincible_dmc.client.input.crazyCombo.debug;

import com.dmc.invincible_dmc.api.skill.ComboNodeManager;
import com.dmc.invincible_dmc.api.skill.ICrazyComboNode;
import com.dmc.invincible_dmc.api.skill.crazycombo.CrazyComboSession;
import com.dmc.invincible_dmc.client.input.DMComboEngine;
import com.dmc.invincible_dmc.client.input.IComboExecutor;
import com.dmc.invincible_dmc.client.input.crazyCombo.ClientCrazyComboController;
import com.dmc.invincible_dmc.client.input.crazyCombo.CrazyComboAnimationHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;

public class CrazyComboDebugHUD {
   public static final IGuiOverlay OVERLAY = (gui, guiGraphics, partialTick, width, height) -> {
      Minecraft mc = Minecraft.m_91087_();
      if (!mc.f_91066_.f_92062_ && mc.f_91074_ != null) {
         LocalPlayerPatch lpp = DMComboEngine.getLocalPlayerPatch();
         if (lpp != null) {
            IComboExecutor dispatcher = DMComboEngine.getLocalPlayerDispatcher();
            if (dispatcher != null) {
               ClientCrazyComboController ccCtrl = dispatcher.getCrazyComboController();
               CrazyComboSession.Stage state = ccCtrl.getCurrentState();
               if (state != CrazyComboSession.Stage.IDLE) {
                  if (ComboNodeManager.get(ccCtrl.getSourceNodeId()) instanceof ICrazyComboNode ccNode) {
                     DynamicAnimation currentAnim = CrazyComboAnimationHelper.getCurrentAnimation(lpp);
                     float progress = CrazyComboAnimationHelper.getAnimationProgress(lpp);
                     if (currentAnim != null && !(progress < 0.0F)) {
                        Font font = mc.f_91062_;
                        float pressCount = (float)ccCtrl.getPressCount();
                        float threshold = state == CrazyComboSession.Stage.STARTUP
                           ? (float)ccNode.getCcBaseRequiredPresses()
                           : (float)ccNode.getCcChaseRequiredPresses();
                        boolean inWindow = progress >= ccNode.getCcWindowStart();
                        boolean canCancel = lpp.getEntityState().canBasicAttack();
                        int barWidth = 200;
                        int barHeight = 5;
                        int x = (width - barWidth) / 2;
                        int y = 44;

                        String stateColor = switch (state) {
                           case STARTUP -> "§b";
                           case CHASE -> "§6";
                           default -> "§7";
                        };
                        int loops = ccCtrl.getLoopCount();
                        int maxLoops = ccNode.getCcMaxChases();
                        String loopStr = state == CrazyComboSession.Stage.CHASE && maxLoops > 0 ? " [" + loops + "/" + maxLoops + "]" : "";
                        String titleText = stateColor + "[CC] " + state.name() + loopStr;
                        int titleW = font.m_92895_(titleText);
                        guiGraphics.m_280056_(font, titleText, (width - titleW) / 2, y - 10, 16777215, false);
                        String animPath = currentAnim.getRegistryName() != null ? currentAnim.getRegistryName().m_135815_() : "?";
                        int animW = font.m_92895_(animPath);
                        guiGraphics.m_280056_(font, "§7" + animPath, (width - animW) / 2, y, 16777215, false);
                        int progY = y + 12;
                        guiGraphics.m_280509_(x, progY, x + barWidth, progY + barHeight, -15658735);
                        int windowColor = inWindow ? -1442775228 : (canCancel ? -1426111420 : -1438318780);
                        drawSegment(guiGraphics, x, progY, barWidth, barHeight, ccNode.getCcWindowStart(), 1.0F, windowColor);
                        int cursorX = x + Math.round(progress * (float)barWidth);
                        guiGraphics.m_280509_(cursorX - 1, progY - 1, cursorX + 1, progY + barHeight + 1, -1);
                        int rapidY = progY + barHeight + 6;
                        guiGraphics.m_280509_(x, rapidY, x + barWidth, rapidY + barHeight, -15658735);
                        float fillRatio = Math.min(pressCount / threshold, 1.0F);
                        int rapidColor = fillRatio >= 1.0F ? -855668736 : -872371457;
                        drawSegment(guiGraphics, x, rapidY, barWidth, barHeight, 0.0F, fillRatio, rapidColor);
                        int threshX = x + Math.round((float)barWidth);
                        guiGraphics.m_280509_(threshX - 1, rapidY - 1, threshX + 1, rapidY + barHeight + 1, -855686076);
                        String rapidText = String.format("§fRAPID: §b%.0f §7/ %.0f", pressCount, threshold);
                        int rapidW = font.m_92895_(rapidText);
                        guiGraphics.m_280056_(font, rapidText, (width - rapidW) / 2, rapidY + barHeight + 2, 16777215, false);
                     }
                  }
               }
            }
         }
      }
   };

   private static void drawSegment(GuiGraphics g, int x, int y, int barW, int barH, float start, float end, int color) {
      if (!(start < 0.0F) && !(end < 0.0F) && !(start >= end)) {
         int s = x + Math.round(Math.max(0.0F, Math.min(start, 1.0F)) * (float)barW);
         int e = x + Math.round(Math.max(0.0F, Math.min(end, 1.0F)) * (float)barW);
         if (e > s) {
            g.m_280509_(s, y, e, y + barH, color);
         }
      }
   }
}
