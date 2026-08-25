package com.dmc.invincible_dmc.client.gui.vergilstatus;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class VergilStatusConfigScreen extends Screen {
   private static final ResourceLocation TEXTURE_BG = ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/gui/hud/vergil_background.png");
   private double previewX;
   private double previewY;
   private float previewScale;
   private final Screen lastScreen;
   private boolean dragging;
   private double dragStartX;
   private double dragStartY;
   private double startPreviewX;
   private double startPreviewY;

   public VergilStatusConfigScreen() {
      this(null);
   }

   public VergilStatusConfigScreen(Screen lastScreen) {
      super(Component.m_237115_("gui.invincible_dmc.config.title"));
      this.lastScreen = lastScreen;
   }

   protected void m_7856_() {
      this.previewX = (Double)VergilStatusConfig.POS_X.get();
      this.previewY = (Double)VergilStatusConfig.POS_Y.get();
      this.previewScale = ((Double)VergilStatusConfig.SCALE.get()).floatValue();
      this.clampBounds();
      int btnW = 80;
      int btnH = 20;
      int spacing = 5;
      int startX = (this.f_96543_ - (btnW * 4 + spacing * 3)) / 2;
      int y = this.f_96544_ - 30;
      this.m_142416_(
         Button.m_253074_(Component.m_237115_("gui.invincible_dmc.config.save"), btn -> this.m_7379_()).m_252987_(startX, y, btnW, btnH).m_253136_()
      );
      this.m_142416_(
         Button.m_253074_(
               Component.m_237115_("gui.invincible_dmc.config.scale_up"),
               btn -> this.adjustScaleAtAnchor((double)this.f_96543_ / 2.0, (double)this.f_96544_ / 2.0, 0.1F)
            )
            .m_252987_(startX + btnW + spacing, y, btnW, btnH)
            .m_253136_()
      );
      this.m_142416_(
         Button.m_253074_(
               Component.m_237115_("gui.invincible_dmc.config.scale_down"),
               btn -> this.adjustScaleAtAnchor((double)this.f_96543_ / 2.0, (double)this.f_96544_ / 2.0, -0.1F)
            )
            .m_252987_(startX + (btnW + spacing) * 2, y, btnW, btnH)
            .m_253136_()
      );
      this.m_142416_(
         Button.m_253074_(Component.m_237115_("gui.invincible_dmc.config.reset"), btn -> this.resetDefaults())
            .m_252987_(startX + (btnW + spacing) * 3, y, btnW, btnH)
            .m_253136_()
      );
   }

   private void adjustScaleAtAnchor(double anchorX, double anchorY, float delta) {
      float oldScale = this.previewScale;
      this.previewScale = Mth.m_14036_(this.previewScale + delta, 0.3F, 3.0F);
      if (oldScale != this.previewScale) {
         double relativeX = (anchorX - this.previewX) / (double)oldScale;
         double relativeY = (anchorY - this.previewY) / (double)oldScale;
         this.previewX = anchorX - relativeX * (double)this.previewScale;
         this.previewY = anchorY - relativeY * (double)this.previewScale;
         this.clampBounds();
      }
   }

   private void resetDefaults() {
      VergilStatusConfig.resetToDefaults();
      this.previewX = (Double)VergilStatusConfig.POS_X.get();
      this.previewY = (Double)VergilStatusConfig.POS_Y.get();
      this.previewScale = ((Double)VergilStatusConfig.SCALE.get()).floatValue();
      this.clampBounds();
   }

   private void clampBounds() {
      float adaptiveScale = VergilStatusOverlay.getAdaptiveScale(this.f_96543_, this.f_96544_, this.previewScale);
      int pw = Math.round(300.0F * adaptiveScale);
      int ph = Math.round(110.0F * adaptiveScale);
      this.previewX = Mth.m_14008_(this.previewX, 0.0, (double)(this.f_96543_ - pw));
      this.previewY = Mth.m_14008_(this.previewY, 0.0, (double)(this.f_96544_ - ph));
   }

   public void m_88315_(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
      this.m_280273_(g);
      float adaptiveScale = VergilStatusOverlay.getAdaptiveScale(this.f_96543_, this.f_96544_, this.previewScale);
      int pw = Math.round(300.0F * adaptiveScale);
      int ph = Math.round(110.0F * adaptiveScale);
      int px = (int)this.previewX;
      int py = (int)this.previewY;
      g.m_280637_(px, py, pw, ph, -1);
      RenderSystem.enableBlend();
      g.m_280411_(TEXTURE_BG, px, py, pw, ph, 0.0F, 0.0F, 300, 110, 512, 512);
      RenderSystem.disableBlend();
      g.m_280653_(this.f_96547_, this.f_96539_, this.f_96543_ / 2, 15, 16777215);
      Component infoText = Component.m_237110_(
         "gui.invincible_dmc.config.info", new Object[]{(int)this.previewX, (int)this.previewY, String.format("%.1f", this.previewScale)}
      );
      g.m_280653_(this.f_96547_, infoText, this.f_96543_ / 2, this.f_96544_ / 2, 11184810);
      super.m_88315_(g, mouseX, mouseY, partialTick);
   }

   public boolean m_6375_(double mouseX, double mouseY, int button) {
      if (button == 0 && this.isOverPreview(mouseX, mouseY)) {
         this.dragging = true;
         this.dragStartX = mouseX;
         this.dragStartY = mouseY;
         this.startPreviewX = this.previewX;
         this.startPreviewY = this.previewY;
         return true;
      } else {
         return super.m_6375_(mouseX, mouseY, button);
      }
   }

   public boolean m_7979_(double mouseX, double mouseY, int button, double dragX, double dragY) {
      if (this.dragging) {
         this.previewX = this.startPreviewX + (mouseX - this.dragStartX);
         this.previewY = this.startPreviewY + (mouseY - this.dragStartY);
         this.clampBounds();
         return true;
      } else {
         return super.m_7979_(mouseX, mouseY, button, dragX, dragY);
      }
   }

   public boolean m_6348_(double mouseX, double mouseY, int button) {
      if (button == 0) {
         this.dragging = false;
      }

      return super.m_6348_(mouseX, mouseY, button);
   }

   public boolean m_6050_(double v2, double v, double v1) {
      if (this.isOverPreview(v2, v)) {
         this.adjustScaleAtAnchor(v2, v, v1 > 0.0 ? 0.05F : -0.05F);
         return true;
      } else {
         return super.m_6050_(v2, v, v1);
      }
   }

   private boolean isOverPreview(double mouseX, double mouseY) {
      float adaptiveScale = VergilStatusOverlay.getAdaptiveScale(this.f_96543_, this.f_96544_, this.previewScale);
      int pw = Math.round(300.0F * adaptiveScale);
      int ph = Math.round(110.0F * adaptiveScale);
      return mouseX >= this.previewX && mouseX <= this.previewX + (double)pw && mouseY >= this.previewY && mouseY <= this.previewY + (double)ph;
   }

   public void m_7379_() {
      VergilStatusConfig.POS_X.set(this.previewX);
      VergilStatusConfig.POS_Y.set(this.previewY);
      VergilStatusConfig.SCALE.set((double)this.previewScale);
      VergilStatusConfig.SPEC.save();
      if (this.f_96541_ != null) {
         this.f_96541_.m_91152_(this.lastScreen);
      }
   }

   public boolean m_7043_() {
      return false;
   }
}
