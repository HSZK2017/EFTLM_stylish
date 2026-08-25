package com.dmc.invincible_dmc.client;

import com.dmc.invincible_dmc.network.DMCNetwork;
import com.dmc.invincible_dmc.network.client.CPInstantJudgementCutEndToggle;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class InstantJudgementCutEndClientState {
   private static boolean known;
   private static boolean learned;
   private static boolean enabled = true;
   private static boolean pending;

   private InstantJudgementCutEndClientState() {
   }

   public static void requestState() {
      Minecraft minecraft = Minecraft.m_91087_();
      if (minecraft.f_91074_ != null && minecraft.m_91403_() != null) {
         known = false;
         pending = false;
         DMCNetwork.sendToServer(CPInstantJudgementCutEndToggle.query());
      } else {
         apply(false, true);
      }
   }

   public static void toggle() {
      Minecraft minecraft = Minecraft.m_91087_();
      if (known && learned && !pending && minecraft.m_91403_() != null) {
         enabled = !enabled;
         pending = true;
         DMCNetwork.sendToServer(CPInstantJudgementCutEndToggle.set(enabled));
      }
   }

   public static void apply(boolean learnedValue, boolean enabledValue) {
      learned = learnedValue;
      enabled = enabledValue;
      known = true;
      pending = false;
   }

   public static boolean isKnown() {
      return known;
   }

   public static boolean isLearned() {
      return learned;
   }

   public static boolean isEnabled() {
      return enabled;
   }

   public static boolean isPending() {
      return pending;
   }
}
