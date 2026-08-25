package com.dmc.invincible_dmc.client.input;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.api.skill.ComboType;
import com.dmc.invincible_dmc.conditions.DirectionalSequenceCondition;
import com.dmc.invincible_dmc.utils.vfx.CinematicBarsUtils;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class ComboEngineDebugHUD {
   private static final int COLOR_BG = -1727526385;
   private static final int COLOR_ACCENT = -16715521;
   private static final int COLOR_BORDER = 369098751;
   private static final int COLOR_TEXT_MUTED = -10722448;
   private static final int COLOR_TEXT_ACTIVE = -1;
   private static final int COLOR_WARN = -50384;
   private static final int COLOR_POSITIVE = -16711800;
   private static final Map<Integer, String> SEQ_SHORT_NAMES = new LinkedHashMap<>();
   public static boolean enabled = false;
   public static final IGuiOverlay OVERLAY = (gui, guiGraphics, partialTick, width, height) -> {
      if (enabled) {
         Minecraft mc = Minecraft.m_91087_();
         if (!mc.f_91066_.f_92062_ && mc.f_91074_ != null) {
            if (!CinematicBarsUtils.isVisible()) {
               if (DMComboEngine.shouldPlayerHandleInput()) {
                  IComboExecutor playerDispatcher = DMComboEngine.getLocalPlayerDispatcher();
                  if (playerDispatcher != null) {
                     Font font = mc.f_91062_;
                     long engineTick = DMComboEngine.engineTick;
                     int boxWidth = 140;
                     int x = 8;
                     int y = height / 2 - 50;
                     int rowHeight = 10;
                     int keyCount = ComboInputSampler.INPUT_STATES.length;
                     List<DirectionTracker.DirectionEvent> dirEvents = playerDispatcher.getDirectionTracker().exportRecentEvents();
                     int dirCount = Math.min(dirEvents.size(), 8);
                     int queueCount = playerDispatcher.getReservedInputs().size();
                     int seqCount = SEQ_SHORT_NAMES.size();
                     int boxHeight = 48;
                     String srvFail = DMComboEngine.getServerFailureMessage();
                     if (srvFail != null) {
                        boxHeight += 18;
                     }

                     boxHeight += 4 + seqCount * rowHeight;
                     if (queueCount > 0) {
                        boxHeight += queueCount * rowHeight + 4;
                     } else {
                        boxHeight += rowHeight + 2;
                     }

                     guiGraphics.m_280509_(x, y, x + boxWidth, y + boxHeight, -1727526385);
                     drawOutlineRect(guiGraphics, x, y, boxWidth, boxHeight, 369098751);
                     guiGraphics.m_280509_(x, y, x + 2, y + boxHeight, -16715521);
                     int currentY = y + 5;
                     int contentX = x + 6;
                     guiGraphics.m_280056_(font, "输入日志", contentX, currentY, -16715521, false);
                     int jcTicks = playerDispatcher.getJumpBufferTicks();
                     if (jcTicks > 0) {
                        guiGraphics.m_280056_(font, "• JC", x + boxWidth - 24, currentY, -16711800, false);
                     } else {
                        String tickStr = "T:" + engineTick % 10000L;
                        guiGraphics.m_280056_(font, tickStr, x + boxWidth - font.m_92895_(tickStr) - 6, currentY, -10722448, false);
                     }

                     currentY += 11;
                     if (srvFail != null) {
                        int warnW = boxWidth - 12;
                        int warnH = 14;
                        guiGraphics.m_280509_(contentX, currentY, contentX + warnW, currentY + warnH, 1157577520);
                        drawOutlineRect(guiGraphics, contentX, currentY, warnW, warnH, -50384);
                        String warnText = font.m_92834_("SVR FAIL: " + srvFail, warnW - 4);
                        guiGraphics.m_280056_(font, warnText, contentX + 2, currentY + 3, -50384, false);
                        currentY += 18;
                     }

                     int btnX = contentX;
                     int btnWidth = 15;
                     int btnHeight = 11;
                     int btnSpacing = 2;

                     for (int i = 0; i < keyCount; i++) {
                        ComboInputSampler.ComboInputState state = ComboInputSampler.INPUT_STATES[i];
                        ComboType type = ComboInputSampler.STATES_TO_TYPE[i];
                        if (state.keyMapping != null) {
                           String label = getShortKeyName(type);
                           boolean pressed = state.curDown;
                           int btnBg = pressed ? 855699711 : 150994943;
                           int btnBorder = pressed ? -16715521 : 285212671;
                           guiGraphics.m_280509_(btnX, currentY, btnX + btnWidth, currentY + btnHeight, btnBg);
                           drawOutlineRect(guiGraphics, btnX, currentY, btnWidth, btnHeight, btnBorder);
                           int textColor = pressed ? -1 : -10722448;
                           int textX = btnX + (btnWidth - font.m_92895_(label)) / 2;
                           guiGraphics.m_280056_(font, label, textX, currentY + 2, textColor, false);
                           btnX += btnWidth + btnSpacing;
                        }
                     }

                     currentY += btnHeight + 5;
                     guiGraphics.m_280056_(font, "方向输入", contentX, currentY, -10722448, false);
                     int arrowX = contentX + 44;
                     if (dirEvents.isEmpty()) {
                        guiGraphics.m_280056_(font, "-", arrowX, currentY, 872415231, false);
                     } else {
                        int startIdx = Math.max(0, dirEvents.size() - dirCount);

                        for (int ix = startIdx; ix < dirEvents.size(); ix++) {
                           DirectionTracker.DirectionEvent event = dirEvents.get(ix);
                           long age = engineTick - event.tick();
                           int alpha = Math.max(51, 255 - (int)(age * 12L));
                           int arrowColor = age < 4L ? -16715521 : alpha << 24 | 16777215;
                           String arrow = getDirectionArrow(event.direction());
                           guiGraphics.m_280056_(font, arrow, arrowX, currentY, arrowColor, false);
                           arrowX += font.m_92895_(arrow) + 2;
                           if (ix < dirEvents.size() - 1) {
                              guiGraphics.m_280056_(font, "›", arrowX, currentY, 587202559, false);
                              arrowX += font.m_92895_("›") + 2;
                           }
                        }
                     }

                     currentY += 11;
                     guiGraphics.m_280056_(font, "方向序列", contentX, currentY, -10722448, false);
                     int matchWindow = (Integer)DMConfig.DIRECTION_SEQUENCE_MATCH_WINDOW.get();
                     int activationWindow = (Integer)DMConfig.DIRECTION_SEQUENCE_ACTIVATION_WINDOW.get();
                     String cfg = "msk" + matchWindow + "/act" + activationWindow;
                     guiGraphics.m_280056_(font, cfg, x + boxWidth - font.m_92895_(cfg) - 6, currentY, -10722448, false);
                     currentY += 11;
                     DirectionTracker tracker = playerDispatcher.getDirectionTracker();
                     int mask = tracker.getMatchedSequencesMask((long)matchWindow, (long)activationWindow, engineTick);
                     int barAreaX = contentX + 28;
                     int barMaxW = boxWidth - (barAreaX - x) - 8;

                     for (Entry<Integer, String> entry : SEQ_SHORT_NAMES.entrySet()) {
                        int ordinal = entry.getKey();
                        String name = entry.getValue();
                        boolean matched = (mask & 1 << ordinal) != 0;
                        String prefix = matched ? "■" : "□";
                        int pColor = matched ? -16711800 : -10722448;
                        int nColor = matched ? -1 : -10722448;
                        guiGraphics.m_280056_(font, prefix, contentX + 2, currentY, pColor, false);
                        guiGraphics.m_280056_(font, name, contentX + 12, currentY, nColor, false);
                        if (matched) {
                           int barY = currentY + 4;
                           guiGraphics.m_280509_(barAreaX, barY, barAreaX + barMaxW, barY + 2, 587202559);
                           guiGraphics.m_280509_(barAreaX, barY, barAreaX + barMaxW, barY + 2, -16711800);
                        }

                        currentY += rowHeight;
                     }

                     guiGraphics.m_280056_(font, "预输入", contentX, currentY, -10722448, false);
                     int bufX = contentX + 34;
                     if (playerDispatcher.getReservedInputs().isEmpty()) {
                        guiGraphics.m_280056_(font, "-", bufX, currentY, 872415231, false);
                     } else {
                        int renderedCount = 0;

                        for (IComboExecutor.ReservedIntent reserved : playerDispatcher.getReservedInputs()) {
                           if (renderedCount >= 2) {
                              break;
                           }

                           boolean routed = reserved.routed();
                           int statusColor = routed ? -16715521 : -50384;
                           String intentName = getShortKeyName(reserved.intent().type());
                           guiGraphics.m_280056_(font, intentName, bufX, currentY, statusColor, false);
                           int barX = bufX + 16;
                           int barY = currentY + 3;
                           int maxBarWidth = boxWidth - (barX - x) - 6;
                           int barHeight = 2;
                           float progress = Math.max(0.0F, Math.min(1.0F, (float)reserved.remainingTicks() / 10.0F));
                           int activeBarWidth = (int)((float)maxBarWidth * progress);
                           guiGraphics.m_280509_(barX, barY, barX + maxBarWidth, barY + barHeight, 587202559);
                           guiGraphics.m_280509_(barX, barY, barX + activeBarWidth, barY + barHeight, statusColor);
                           currentY += rowHeight;
                           renderedCount++;
                        }
                     }
                  }
               }
            }
         }
      }
   };

   private static String getDirectionArrow(DirectionalSequenceCondition.Direction direction) {
      return switch (direction) {
         case UP -> "↑";
         case DOWN -> "↓";
         case LEFT -> "←";
         case RIGHT -> "→";
      };
   }

   private static void drawOutlineRect(GuiGraphics graphics, int x, int y, int width, int height, int color) {
      graphics.m_280509_(x, y, x + width, y + 1, color);
      graphics.m_280509_(x, y + height - 1, x + width, y + height, color);
      graphics.m_280509_(x, y, x + 1, y + height, color);
      graphics.m_280509_(x + width - 1, y, x + width, y + height, color);
   }

   private static String getShortKeyName(ComboType type) {
      int id = type.universalOrdinal();

      return switch (id) {
         case 0 -> "K1";
         case 1 -> "K2";
         case 2 -> "K3";
         case 3 -> "K4";
         case 4 -> "12";
         case 5 -> "13";
         case 6 -> "14";
         case 7 -> "23";
         case 8 -> "24";
         case 9 -> "34";
         case 10 -> "DG";
         case 11 -> "IN";
         default -> "??";
      };
   }

   static {
      SEQ_SHORT_NAMES.put(DirectionalSequenceCondition.Sequence.BACK_FORWARD.ordinal(), "BF");
      SEQ_SHORT_NAMES.put(DirectionalSequenceCondition.Sequence.FORWARD_BACK.ordinal(), "FB");
      SEQ_SHORT_NAMES.put(DirectionalSequenceCondition.Sequence.LEFT_RIGHT.ordinal(), "LR");
      SEQ_SHORT_NAMES.put(DirectionalSequenceCondition.Sequence.RIGHT_LEFT.ordinal(), "RL");
      SEQ_SHORT_NAMES.put(DirectionalSequenceCondition.Sequence.BACK_BACK.ordinal(), "BB");
      SEQ_SHORT_NAMES.put(DirectionalSequenceCondition.Sequence.FORWARD_FORWARD.ordinal(), "FF");
      SEQ_SHORT_NAMES.put(DirectionalSequenceCondition.Sequence.LEFT_LEFT.ordinal(), "LL");
      SEQ_SHORT_NAMES.put(DirectionalSequenceCondition.Sequence.RIGHT_RIGHT.ordinal(), "RR");
   }
}
