package org.merlin204.mimic.client.gui;

import com.mojang.blaze3d.platform.Window;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.merlin204.mimic.entity.MimicPatch;
import org.merlin204.mimic.entity.proteus.ProteusEntity;

public class BossBar {
   public static final Map<UUID, Integer> BOSSES = new HashMap<>();
   public static final ResourceLocation BAR_HEALTH_B = ResourceLocation.fromNamespaceAndPath("mimic", "textures/gui/health_b.png");
   public static final ResourceLocation BAR_SHIELD_B = ResourceLocation.fromNamespaceAndPath("mimic", "textures/gui/shield_b.png");
   public static final ResourceLocation BAR = ResourceLocation.fromNamespaceAndPath("mimic", "textures/gui/bar.png");
   public static final ResourceLocation BAR_HEALTH = ResourceLocation.fromNamespaceAndPath("mimic", "textures/gui/health.png");
   public static final ResourceLocation BAR_SHIELD = ResourceLocation.fromNamespaceAndPath("mimic", "textures/gui/shield.png");

   @OnlyIn(Dist.CLIENT)
   public static boolean renderBossBar(GuiGraphics guiGraphics, LerpingBossEvent bossEvent, int x, int y) {
      Entity boss = null;
      if (BOSSES.isEmpty()) {
         return false;
      } else {
         Integer bossId = BOSSES.get(bossEvent.m_18860_());
         ClientLevel level = Minecraft.m_91087_().f_91073_;
         if (bossId != null && level != null) {
            boss = level.m_6815_(bossId);
         }

         if (boss instanceof ProteusEntity proteus) {
            String name = "entity.mimic.proteus_1";
            if (proteus.getPhase() == 2) {
               name = "entity.mimic.proteus_2";
            } else if (proteus.getPhase() == 3) {
               name = "entity.mimic.proteus_3";
            }

            MimicPatch<?> patch = proteus.getPatch();
            float shieldRatio = 0.0F;
            if (patch != null && patch.getMaxStunShield() > 0.0F) {
               shieldRatio = patch.getStunShield() / patch.getMaxStunShield();
            }

            drawHealth(guiGraphics, y, bossEvent, shieldRatio, BAR_HEALTH_B, BAR, BAR_HEALTH);
            drawName(name, guiGraphics, y, bossEvent, boss);
            return true;
         } else {
            return false;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void drawName(String name, GuiGraphics guiGraphics, int y, LerpingBossEvent event, Entity boss) {
      Font font = Minecraft.m_91087_().f_91062_;
      Window window = Minecraft.m_91087_().m_91268_();
      Style nameStyle = Style.f_131099_.m_178520_(16711680).m_131136_(true);
      Component nameComponent = Component.m_237115_(name).m_130948_(nameStyle);
      int nameWidth = font.m_92852_(nameComponent);
      int nameX = (window.m_85445_() - nameWidth) / 2;
      int nameY = y + 5;
      guiGraphics.m_280614_(font, nameComponent, nameX, nameY, -1, true);
   }

   @OnlyIn(Dist.CLIENT)
   public static void drawHealth(
      GuiGraphics guiGraphics,
      int y,
      LerpingBossEvent event,
      float rio,
      ResourceLocation bgHealth,
      ResourceLocation bgBarLocation,
      ResourceLocation frontBarLocation
   ) {
      Window window = Minecraft.m_91087_().m_91268_();
      int x = window.m_85445_() / 2 - 128;
      guiGraphics.m_280411_(bgHealth, x, y, 256, 32, 0.0F, 0.0F, 256, 32, 256, 32);
      guiGraphics.m_280411_(BAR_SHIELD_B, x, y, 256, 32, 0.0F, 0.0F, 256, 32, 256, 32);
      guiGraphics.m_280411_(
         frontBarLocation, x, y, Mth.m_269140_(event.m_142717_(), 0, 256), 32, 0.0F, 0.0F, Mth.m_269140_(event.m_142717_(), 0, 256), 32, 256, 32
      );
      guiGraphics.m_280411_(BAR_SHIELD, x, y, Mth.m_269140_(rio, 0, 256), 32, 0.0F, 0.0F, Mth.m_269140_(rio, 0, 256), 32, 256, 32);
      guiGraphics.m_280411_(bgBarLocation, x, y, 256, 32, 0.0F, 0.0F, 256, 32, 256, 32);
   }

   @OnlyIn(Dist.CLIENT)
   public static void drawStun(
      GuiGraphics guiGraphics, int y, float rio, ResourceLocation bgHealth, ResourceLocation bgBarLocation, ResourceLocation frontBarLocation
   ) {
      Window window = Minecraft.m_91087_().m_91268_();
      int x = window.m_85445_() / 2 - 128;
      guiGraphics.m_280411_(bgHealth, x, y, 256, 32, 0.0F, 0.0F, 256, 32, 256, 32);
      guiGraphics.m_280411_(frontBarLocation, x, y, Mth.m_269140_(rio, 0, 256), 32, 0.0F, 0.0F, Mth.m_269140_(rio, 0, 256), 32, 256, 32);
      guiGraphics.m_280411_(bgBarLocation, x, y, 256, 32, 0.0F, 0.0F, 256, 32, 256, 32);
   }
}
