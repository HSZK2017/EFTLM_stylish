package com.pla.annoyingvillagers.client.renderer;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.pla.annoyingvillagers.item.HookGunItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent.Post;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.FORGE,
   value = {Dist.CLIENT}
)
public final class HookGunCrosshairRenderer {
   private static final ResourceLocation GUI_ICONS = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/icons.png");

   private HookGunCrosshairRenderer() {
   }

   @SubscribeEvent
   public static void onRenderCrosshair(Post event) {
      if (event.getOverlay() == VanillaGuiOverlay.CROSSHAIR.type()) {
         Minecraft minecraft = Minecraft.m_91087_();
         LocalPlayer player = minecraft.f_91074_;
         if (player != null) {
            Options options = minecraft.f_91066_;
            if (options.m_92176_().m_90612_()) {
               if (!player.m_5833_()) {
                  if (!options.f_92063_ || options.f_92062_ || player.m_36330_() || (Boolean)options.m_231824_().m_231551_()) {
                     if (HookGunItem.isHoldingHookGunInBothHands(player)) {
                        Window window = event.getWindow();
                        int width = window.m_85445_();
                        int height = window.m_85446_();
                        double fov = Math.toRadians((double)((Integer)options.m_231837_().m_231551_()).intValue());
                        fov *= (double)player.m_108565_();
                        double projectedDistance = (double)height / 2.0 / Math.tan(fov / 2.0);
                        int offset = (int)(Math.tan(Math.toRadians(HookGunItem.getDoubleHookAngle(player))) * projectedDistance);
                        if (offset != 0) {
                           drawCrosshair(event.getGuiGraphics(), width / 2 + offset, height / 2);
                           drawCrosshair(event.getGuiGraphics(), width / 2 - offset, height / 2);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void drawCrosshair(GuiGraphics graphics, int x, int y) {
      RenderSystem.blendFuncSeparate(SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO);
      graphics.m_280218_(GUI_ICONS, x - 7, y - 7, 0, 0, 15, 15);
      RenderSystem.defaultBlendFunc();
   }
}
