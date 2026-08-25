package com.dmc.invincible_dmc.client.input.judegementCut.debug;

import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.capability.DMCPlayer;
import com.dmc.invincible_dmc.capability.DMCPlayerCapabilityProvider;
import com.dmc.invincible_dmc.client.input.DMComboEngine;
import com.dmc.invincible_dmc.client.input.IComboExecutor;
import com.dmc.invincible_dmc.client.input.judegementCut.ClientJudgementCutController;
import com.dmc.invincible_dmc.client.input.judegementCut.JudgementCutAnimationHelper;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;

public class JudgementCutDebugHUD {
   public static boolean enabled = false;
   public static final IGuiOverlay OVERLAY = (gui, guiGraphics, partialTick, width, height) -> {
      if (enabled) {
         Minecraft mc = Minecraft.m_91087_();
         if (!mc.f_91066_.f_92062_ && mc.f_91074_ != null) {
            LocalPlayer player = mc.f_91074_;
            LocalPlayerPatch lpp = DMComboEngine.getLocalPlayerPatch();
            if (lpp != null) {
               IComboExecutor dispatcher = DMComboEngine.getLocalPlayerDispatcher();
               if (dispatcher != null) {
                  ClientJudgementCutController controller = dispatcher.getJudgementCutController();
                  ComboNode activeNode = getActiveNode();
                  DynamicAnimation currentAnim = JudgementCutAnimationHelper.getCurrentAnimation(lpp);
                  boolean isCharging = controller.isCharging();
                  AnimationPlayer animator = DMCAnimationUtils.getMainPlayer(lpp);
                  boolean hasAnimation = currentAnim != null && animator != null;
                  if (hasAnimation || isCharging) {
                     Font font = mc.f_91062_;
                     int barWidth = 200;
                     int barHeight = 6;
                     int x = (width - barWidth) / 2;
                     int y = 12;
                     float cursorProgress = 0.0F;
                     float goldStart = -1.0F;
                     float goldEnd = -1.0F;
                     float greenStart = -1.0F;
                     float greenEnd = -1.0F;
                     float chargeStartProgress = -1.0F;
                     String titleText;
                     if (hasAnimation) {
                        float prevTime = animator.getPrevElapsedTime();
                        float currTime = animator.getElapsedTime();
                        if (currTime < prevTime) {
                           prevTime = currTime;
                        }

                        float interpolatedTime = prevTime + (currTime - prevTime) * partialTick;
                        float totalSec = currentAnim.getTotalTime();
                        if (totalSec <= 0.0F) {
                           totalSec = 1.0F;
                        }

                        cursorProgress = Math.min(interpolatedTime / totalSec, 1.0F);
                        float playbackRate = Math.max((currTime - prevTime) * 20.0F, 0.01F);
                        if (isCharging) {
                           StaticAnimation realAnim = DMCAnimationUtils.getRealAnimation(currentAnim);
                           if (realAnim != null) {
                              float[] animWindow = YamatoAttackAnimation.getJcPerfWindow(realAnim);
                              if (animWindow != null && animWindow[0] < animWindow[1] && totalSec > 0.0F) {
                                 goldStart = animWindow[0] / totalSec;
                                 goldEnd = animWindow[1] / totalSec;
                              }

                              if (goldStart < 0.0F) {
                                 float[] animWindowRatio = YamatoAttackAnimation.getJcPerfWindowRatio(realAnim);
                                 if (animWindowRatio != null && animWindowRatio[0] < animWindowRatio[1]) {
                                    goldStart = animWindowRatio[0];
                                    goldEnd = animWindowRatio[1];
                                 }
                              }
                           }
                        }

                        if (isCharging && goldStart < 0.0F && activeNode != null) {
                           goldStart = JudgementCutAnimationHelper.getPerfectWindowStart(activeNode);
                           goldEnd = JudgementCutAnimationHelper.getPerfectWindowEnd(activeNode);
                        }

                        if (isCharging && goldStart < 0.0F) {
                           DMCPlayer ip = DMCPlayerCapabilityProvider.get(player);
                           ComboNode dataNode = ip.getCurrentDataNode();
                           if (dataNode != null) {
                              float ds = JudgementCutAnimationHelper.getPerfectWindowStart(dataNode);
                              float de = JudgementCutAnimationHelper.getPerfectWindowEnd(dataNode);
                              if (ds >= 0.0F) {
                                 goldStart = ds;
                                 goldEnd = de;
                              }
                           }
                        }

                        if (isCharging) {
                           chargeStartProgress = controller.getChargeStartAnimProgress();
                           long nowMs = Util.m_137550_();
                           long readyAtMs = controller.getChargeReadyAtMs();
                           if (readyAtMs >= 0L) {
                              greenStart = projectTimestampToProgress(readyAtMs, nowMs, interpolatedTime, totalSec, playbackRate);
                              greenEnd = projectTimestampToProgress(
                                 readyAtMs + controller.getJustReleaseWindowMs(), nowMs, interpolatedTime, totalSec, playbackRate
                              );
                           }
                        }

                        int chain = controller.getChainCount();
                        String animPath = JudgementCutAnimationHelper.getAnimationName(currentAnim);
                        if (chain > 0) {
                           titleText = "§d[CHAIN x" + chain + "] §f" + animPath;
                        } else {
                           titleText = "§6[YAMATO] §f" + animPath;
                        }
                     } else {
                        long elapsed = Util.m_137550_() - controller.getChargeStartMs();
                        long needed = controller.getChargeTimeMs(player);
                        cursorProgress = 1.0F;
                        chargeStartProgress = 0.0F;
                        if (elapsed >= needed) {
                           greenStart = 0.0F;
                           greenEnd = 1.0F;
                           titleText = "§a[READY] §fRelease to Judgement Cut";
                        } else {
                           titleText = "§b[CHARGING...]";
                        }
                     }

                     guiGraphics.m_280509_(x - 6, y - 10, x + barWidth + 6, y + barHeight + 5, -1442840576);
                     guiGraphics.m_280509_(x - 6, y - 11, x + barWidth + 6, y - 10, 872415231);
                     int textWidth = font.m_92895_(titleText);
                     guiGraphics.m_280056_(font, titleText, (width - textWidth) / 2, y - 8, 16777215, false);
                     guiGraphics.m_280509_(x, y, x + barWidth, y + barHeight, -15658735);
                     if (isCharging && chargeStartProgress >= 0.0F) {
                        drawSegment(guiGraphics, x, y, barWidth, barHeight, chargeStartProgress, cursorProgress, -1442783233);
                     }

                     if (controller.getChainCount() == 0) {
                        drawSegment(guiGraphics, x, y, barWidth, barHeight, greenStart, greenEnd, -1442775194);
                     }

                     drawSegment(guiGraphics, x, y, barWidth, barHeight, goldStart, goldEnd, -570439680);
                     int cursorX = x + Math.round(cursorProgress * (float)barWidth);
                     guiGraphics.m_280509_(cursorX - 1, y - 1, cursorX + 1, y + barHeight + 1, -1);
                  }
               }
            }
         }
      }
   };

   private static void drawSegment(GuiGraphics guiGraphics, int x, int y, int barWidth, int barHeight, float start, float end, int color) {
      if (!(start < 0.0F) && !(end < 0.0F) && !(start >= end)) {
         int s = x + Math.round(Math.max(0.0F, Math.min(start, 1.0F)) * (float)barWidth);
         int e = x + Math.round(Math.max(0.0F, Math.min(end, 1.0F)) * (float)barWidth);
         if (e > s) {
            guiGraphics.m_280509_(s, y, e, y + barHeight, color);
         }
      }
   }

   private static float projectTimestampToProgress(long timestampMs, long nowMs, float currentAnimationTime, float totalAnimationTime, float playbackRate) {
      float wallClockOffsetSeconds = (float)(timestampMs - nowMs) / 1000.0F;
      float projectedAnimationTime = currentAnimationTime + wallClockOffsetSeconds * playbackRate;
      return projectedAnimationTime / totalAnimationTime;
   }

   @Nullable
   private static ComboNode getActiveNode() {
      IComboExecutor dispatch = DMComboEngine.getLocalPlayerDispatcher();
      return dispatch != null ? dispatch.getCurrentNode() : null;
   }
}
