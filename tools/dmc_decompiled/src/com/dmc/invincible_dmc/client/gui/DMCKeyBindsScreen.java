package com.dmc.invincible_dmc.client.gui;

import com.dmc.invincible_dmc.capability.weapon.DmcWeaponManager;
import com.dmc.invincible_dmc.client.DMCKeyMappings;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.client.input.EpicFightKeyMappings;

public class DMCKeyBindsScreen extends Screen {
   private final Screen lastScreen;
   public int selectedIndex = 0;
   public KeyMapping bindingTarget = null;
   public long lastKeyClickTime;
   private DMCKeyBindsScreen.KeyList keyList;
   private float transitionAlpha = 0.0F;
   private float slideOffset = 0.0F;
   private long lastRenderTime = 0L;
   private float dt = 0.0F;
   private float bindBtnHover = 0.0F;
   private float resetBtnHover = 0.0F;
   private int lastDescExtraHeight = 0;
   private KeyMapping subPanelTarget = null;
   private float subPanelAnim = 0.0F;
   private float subPanelBindHover = 0.0F;
   private float subPanelResetHover = 0.0F;
   private float subPanelCloseHover = 0.0F;

   private int listW() {
      return Math.min(220, Math.max(140, (int)((double)this.f_96543_ * 0.22)));
   }

   private int detailX() {
      return this.listW() + 30;
   }

   private int detailW() {
      return Math.max(180, this.f_96543_ - this.detailX() - 20);
   }

   public DMCKeyBindsScreen(Screen lastScreen) {
      super(Component.m_237115_("screen.invincible_dmc.keybinds.title"));
      this.lastScreen = lastScreen;
   }

   protected void m_7856_() {
      super.m_7856_();
      this.lastRenderTime = 0L;
      int listWidth = this.listW();
      int listX = 20;
      this.keyList = new DMCKeyBindsScreen.KeyList(this.f_96541_, listWidth, this.f_96544_, 40, this.f_96544_ - 40, 30);
      this.keyList.m_93507_(listX);
      this.keyList.m_93488_(false);
      this.keyList.m_93496_(false);
      this.keyList.m_93471_(false);
      this.m_7787_(this.keyList);
      if (this.selectedIndex >= 0 && this.selectedIndex < this.keyList.m_6702_().size()) {
         DMCKeyBindsScreen.KeyList.Entry target = (DMCKeyBindsScreen.KeyList.Entry)this.keyList.m_6702_().get(this.selectedIndex);
         this.keyList.setSelected(target);
         this.keyList.setInitialScroll(target);
      }
   }

   public float lerp(float current, float target, float speedAt60Fps) {
      float factor = 1.0F - (float)Math.pow(1.0 - (double)speedAt60Fps, (double)(this.dt * 60.0F));
      return current + (target - current) * factor;
   }

   public double lerp(double current, double target, double speedAt60Fps) {
      double factor = 1.0 - Math.pow(1.0 - speedAt60Fps, (double)this.dt * 60.0);
      return current + (target - current) * factor;
   }

   public float step(float current, float target, float speedPerSecond) {
      float stepAmount = speedPerSecond * this.dt;
      if (current < target) {
         return Math.min(current + stepAmount, target);
      } else {
         return current > target ? Math.max(current - stepAmount, target) : current;
      }
   }

   public void m_7379_() {
      if (this.f_96541_ != null) {
         this.f_96541_.m_91152_(this.lastScreen);
      }
   }

   private int renderMultiLineDescription(GuiGraphics graphics, Component desc, int x, int y, int color) {
      String[] lines = desc.getString().split("\n", -1);
      int currentY = y;

      for (String line : lines) {
         graphics.m_280056_(this.f_96547_, line, x, currentY, color, true);
         currentY += 9 + 1;
      }

      return (lines.length - 1) * (9 + 1);
   }

   private void drawCustomFrame(GuiGraphics graphics, int x, int y, int width, int height, float alpha, int accentColor) {
      int right = x + width;
      int bottom = y + height;
      int bgA = (int)(51.0F * alpha);
      int borderA = (int)(85.0F * alpha);
      int bgColor = bgA << 24;
      int borderColor = borderA << 24 | 8947848;
      graphics.m_280509_(x, y, right, bottom, bgColor);
      graphics.m_280509_(x - 1, y - 1, right + 1, y, borderColor);
      graphics.m_280509_(x - 1, bottom, right + 1, bottom + 1, borderColor);
      graphics.m_280509_(x - 1, y, x, bottom, borderColor);
      graphics.m_280509_(right, y, right + 1, bottom, borderColor);
      int decorA = (int)(187.0F * alpha);
      graphics.m_280509_(x, y, x + 2, bottom, decorA << 24 | accentColor);
   }

   public void m_88315_(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      long currentTime = Util.m_137550_();
      if (this.lastRenderTime == 0L) {
         this.lastRenderTime = currentTime;
      }

      this.dt = (float)(currentTime - this.lastRenderTime) / 1000.0F;
      this.lastRenderTime = currentTime;
      if (this.dt > 0.1F) {
         this.dt = 0.1F;
      }

      this.transitionAlpha = this.lerp(this.transitionAlpha, 1.0F, 0.12F);
      float easeProgress = (float)(1.0 - Math.pow((double)(1.0F - this.transitionAlpha), 3.0));
      this.slideOffset = (1.0F - easeProgress) * 150.0F;
      graphics.m_280509_(0, 0, this.f_96543_, this.f_96544_, (int)(140.0F * this.transitionAlpha) << 24);
      int safeAlpha = (int)(255.0F * this.transitionAlpha);
      if (safeAlpha > 8) {
         graphics.m_280653_(this.f_96547_, this.f_96539_, this.f_96543_ / 2, 15, 16777215 | safeAlpha << 24);
      }

      int listWidth = this.listW();
      int listCurrentX = 20 - (int)this.slideOffset;
      int frameY = 40;
      int frameHeight = this.f_96544_ - 80;
      int dx = this.detailX() + (int)this.slideOffset;
      int dw = this.detailW();
      int virtualMouseX = this.subPanelTarget != null ? -999 : mouseX;
      int virtualMouseY = this.subPanelTarget != null ? -999 : mouseY;
      this.keyList.m_93507_(listCurrentX);
      this.keyList.updateSmoothScroll();
      this.keyList.m_88315_(graphics, virtualMouseX, virtualMouseY, partialTick);
      this.drawCustomFrame(graphics, listCurrentX, frameY, listWidth, frameHeight, this.transitionAlpha, 58879);
      this.drawCustomFrame(graphics, dx, frameY, dw, frameHeight, this.transitionAlpha, 58879);
      if (this.selectedIndex >= 0 && this.selectedIndex < this.keyList.m_6702_().size()) {
         this.renderDetailPanel(graphics, dx, frameY, dw, frameHeight, virtualMouseX, virtualMouseY, safeAlpha);
      }

      this.subPanelAnim = this.lerp(this.subPanelAnim, this.subPanelTarget != null ? 1.0F : 0.0F, 0.2F);
      if (this.subPanelAnim > 0.01F) {
         this.renderGlobalSubPanel(graphics, mouseX, mouseY);
      }
   }

   private void renderDetailPanel(GuiGraphics g, int x, int y, int w, int h, int mx, int my, int alpha) {
      DMCKeyBindsScreen.KeyList.Entry entry = (DMCKeyBindsScreen.KeyList.Entry)this.keyList.m_6702_().get(this.selectedIndex);
      KeyMapping key = entry.key;
      int p = Math.max(5, Math.min(15, this.f_96543_ / 55));
      String title = Component.m_237115_(key.m_90860_()).getString();
      int maxTitleW = w - p * 2;
      if (this.f_96547_.m_92895_(title) > maxTitleW) {
         title = this.f_96547_.m_92834_(title, maxTitleW - this.f_96547_.m_92895_("...")) + "...";
      }

      g.m_280056_(this.f_96547_, title, x + p, y + p, 16777215 | alpha << 24, true);
      g.m_280509_(x + p, y + p + 9 + 3, x + p + Math.min(w / 3, maxTitleW), y + p + 9 + 4, alpha << 24 | 58879);
      this.lastDescExtraHeight = this.drawWrappedDesc(g, entry.description, x + p, y + p + 9 + 12, w - p * 2, 11184810 | alpha << 24);
      int cx = x + p;
      int cy = y + p + 9 + 22 + this.lastDescExtraHeight;
      int cw = w - p * 2;
      int ch = Math.min(32, 9 + 12);
      boolean cardHov = mx >= cx && mx <= cx + cw && my >= cy && my <= cy + ch;
      this.bindBtnHover = this.step(this.bindBtnHover, cardHov ? 1.0F : 0.0F, 8.0F);
      float eh = 1.0F - (float)Math.pow((double)(1.0F - this.bindBtnHover), 3.0);
      boolean isWait = this.bindingTarget == key;
      int cBg = isWait ? 855696895 : (int)(34.0F + 17.0F * eh) << 24 | 16777215;
      int cBd = isWait
         ? (int)((187.0 + 68.0 * Math.sin((double)Util.m_137550_() / 150.0)) * (double)this.transitionAlpha) << 24 | 58879
         : (int)((85.0F + 85.0F * eh) * this.transitionAlpha) << 24 | (cardHov ? '\ue5ff' : 8947848);
      g.m_280509_(cx, cy, cx + cw, cy + ch, cBg);
      this.drawBorder(g, cx, cy, cw, ch, cBd);
      Component keyTxt = (Component)(isWait ? Component.m_237115_("screen.invincible_dmc.keybinds.press_any_key") : key.m_90863_());
      int kw = this.f_96547_.m_92852_(keyTxt);
      g.m_280430_(
         this.f_96547_,
         Component.m_237115_("screen.invincible_dmc.keybinds.current_binding"),
         cx + (int)((double)p * 0.8),
         cy + (ch - 9) / 2,
         8947848 | alpha << 24
      );
      g.m_280614_(
         this.f_96547_,
         keyTxt,
         cx + cw - Math.min(kw, cw - 60) - (int)((double)p * 0.8),
         cy + (ch - 9) / 2,
         isWait ? 58879 | alpha << 24 : 16777215 | alpha << 24,
         true
      );
      int rw = Math.min(72, w / 5);
      int rh = Math.min(18, 9 + 4);
      int rx = x + w - rw - p;
      int ry = cy + ch + 6;
      boolean rHov = mx >= rx && mx <= rx + rw && my >= ry && my <= ry + rh;
      this.resetBtnHover = this.step(this.resetBtnHover, rHov ? 1.0F : 0.0F, 8.0F);
      float erh = 1.0F - (float)Math.pow((double)(1.0F - this.resetBtnHover), 3.0);
      boolean def = key.m_90864_();
      int rBd = (int)((def ? 34.0F : 85.0F + 85.0F * erh) * this.transitionAlpha) << 24 | (def ? 5592405 : (rHov ? '\ue5ff' : 8947848));
      int rBg = def ? 285212672 : (int)((17.0F + 34.0F * erh) * this.transitionAlpha) << 24 | 16777215;
      g.m_280509_(rx, ry, rx + rw, ry + rh, rBg);
      this.drawBorder(g, rx, ry, rw, rh, rBd);
      int rc = def ? 5592405 : (rHov ? '\ue5ff' : 11184810);
      g.m_280653_(this.f_96547_, Component.m_237115_("screen.invincible_dmc.keybinds.reset_default"), rx + rw / 2, ry + (rh - 9) / 2, rc | alpha << 24);
      List<KeyMapping> conflicts = this.findConflicts(key);
      int csy = ry + rh + 8;
      if (!conflicts.isEmpty()) {
         g.m_280614_(this.f_96547_, Component.m_237115_("screen.invincible_dmc.keybinds.conflict_warning"), x + p, csy, 16724787 | alpha << 24, true);
         int off = 10;
         int maxS = Math.min(3, conflicts.size());

         for (int i = 0; i < maxS; i++) {
            KeyMapping cf = conflicts.get(i);
            Component dis = Component.m_237113_("[" + this.getModSourceOfKey(cf) + "] ").m_7220_(Component.m_237115_(cf.m_90860_()));
            int tx = x + p + 8;
            int ty = csy + off;
            String line = "- " + dis.getString();
            if (this.f_96547_.m_92895_(line) > w - tx + x) {
               line = this.f_96547_.m_92834_(line, w - tx + x - this.f_96547_.m_92895_("...")) + "...";
            }

            g.m_280488_(this.f_96547_, line, tx, ty, 8947848 | alpha << 24);
            int ji = this.getDMCListIndex(cf);
            int bo = tx + this.f_96547_.m_92895_("- " + dis.getString()) + 6;
            if (ji != -1) {
               boolean fHov = mx >= bo && mx <= bo + 40 && my >= ty - 1 && my <= ty + 9;
               g.m_280430_(this.f_96547_, Component.m_237115_("screen.invincible_dmc.keybinds.jump_to"), bo, ty, (fHov ? '\ue5ff' : 5601160) | alpha << 24);
            } else {
               boolean fHov = mx >= bo && mx <= bo + 40 && my >= ty - 1 && my <= ty + 9;
               g.m_280430_(this.f_96547_, Component.m_237115_("screen.invincible_dmc.keybinds.adjust"), bo, ty, (fHov ? '\ue5ff' : 5601160) | alpha << 24);
            }

            off += 10;
         }

         if (conflicts.size() > maxS) {
            g.m_280430_(
               this.f_96547_,
               Component.m_237110_("screen.invincible_dmc.keybinds.and_more", new Object[]{conflicts.size() - maxS}),
               x + p + 8,
               csy + off,
               5592405 | alpha << 24
            );
         }
      }
   }

   private int drawWrappedDesc(GuiGraphics g, Component text, int x, int y, int maxW, int color) {
      String raw = text.getString();
      int cy = y;
      int lc = 0;

      for (String para : raw.split("\n", -1)) {
         for (String line : this.wrapLines(para, maxW)) {
            g.m_280056_(this.f_96547_, line, x, cy, color, true);
            cy += 9 + 1;
            lc++;
         }
      }

      return (lc - 1) * (9 + 1);
   }

   private List<String> wrapLines(String text, int maxW) {
      List<String> out = new ArrayList<>();
      if (this.f_96547_.m_92895_(text) <= maxW) {
         out.add(text);
         return out;
      } else {
         StringBuilder cur = new StringBuilder();

         for (String word : text.split(" ")) {
            String t = cur.isEmpty() ? word : cur + " " + word;
            if (this.f_96547_.m_92895_(t) <= maxW) {
               if (!cur.isEmpty()) {
                  cur.append(" ");
               }

               cur.append(word);
            } else if (!cur.isEmpty()) {
               out.add(cur.toString());
               cur = new StringBuilder(word);
            } else {
               out.add(word);
               cur.setLength(0);
            }
         }

         if (!cur.isEmpty()) {
            out.add(cur.toString());
         }

         if (out.isEmpty()) {
            out.add(text);
         }

         return out;
      }
   }

   private void drawBorder(GuiGraphics g, int x, int y, int w, int h, int c) {
      g.m_280509_(x - 1, y - 1, x + w + 1, y, c);
      g.m_280509_(x - 1, y + h, x + w + 1, y + h + 1, c);
      g.m_280509_(x - 1, y, x, y + h, c);
      g.m_280509_(x + w, y, x + w + 1, y + h, c);
   }

   private String getModSourceOfKey(KeyMapping key) {
      String category = key.m_90858_();
      if (category.isEmpty()) {
         return "未知";
      } else {
         if (category.startsWith("key.categories.")) {
            String suffix = category.substring("key.categories.".length());
            if (suffix.equals("movement")
               || suffix.equals("gameplay")
               || suffix.equals("inventory")
               || suffix.equals("multiplayer")
               || suffix.equals("misc")
               || suffix.equals("creative")
               || suffix.equals("ui")) {
               return "原版";
            }

            for (String modId : ModList.get().getMods().stream().map(IModInfo::getModId).toList()) {
               if (suffix.contains(modId) || category.contains(modId)) {
                  return ModList.get().getModContainerById(modId).map(container -> container.getModInfo().getDisplayName()).orElse(modId);
               }
            }
         }

         return Component.m_237115_(category).getString();
      }
   }

   private void renderGlobalSubPanel(GuiGraphics graphics, int mouseX, int mouseY) {
      if (this.subPanelTarget != null) {
         int alpha = (int)(255.0F * this.subPanelAnim);
         graphics.m_280168_().m_85836_();
         graphics.m_280168_().m_252880_(0.0F, 0.0F, 400.0F);
         int maskA = (int)(120.0F * this.subPanelAnim);
         graphics.m_280509_(0, 0, this.f_96543_, this.f_96544_, maskA << 24);
         int pWidth = 260;
         int pHeight = 96;
         int pX = (this.f_96543_ - pWidth) / 2;
         int pY = (this.f_96544_ - pHeight) / 2;
         int bgCol = (int)(242.0F * this.subPanelAnim) << 24 | 855313;
         int borderCol = (int)(170.0F * this.subPanelAnim) << 24 | 58879;
         graphics.m_280509_(pX, pY, pX + pWidth, pY + pHeight, bgCol);
         graphics.m_280509_(pX, pY, pX + pWidth, pY + 1, borderCol);
         graphics.m_280509_(pX, pY + pHeight - 1, pX + pWidth, pY + pHeight, borderCol);
         graphics.m_280509_(pX, pY, pX + 1, pY + pHeight, borderCol);
         graphics.m_280509_(pX + pWidth - 1, pY, pX + pWidth, pY + pHeight, borderCol);
         int closeSize = 12;
         int closeX = pX + pWidth - closeSize - 8;
         int closeY = pY + 8;
         boolean closeHovered = mouseX >= closeX && mouseX <= closeX + closeSize && mouseY >= closeY && mouseY <= closeY + closeSize;
         this.subPanelCloseHover = this.step(this.subPanelCloseHover, closeHovered ? 1.0F : 0.0F, 8.0F);
         int closeColor = closeHovered ? -52429 : -7829368;
         graphics.m_280056_(this.f_96547_, "×", closeX + 2, closeY, closeColor | alpha << 24, false);
         String sourceName = this.getModSourceOfKey(this.subPanelTarget);
         graphics.m_280614_(
            this.f_96547_,
            Component.m_237110_("screen.invincible_dmc.keybinds.external_key", new Object[]{sourceName}),
            pX + 12,
            pY + 10,
            16766720 | alpha << 24,
            true
         );
         Component keyLabel = Component.m_237115_(this.subPanelTarget.m_90860_());
         graphics.m_280614_(this.f_96547_, keyLabel, pX + 12, pY + 22, 16777215 | alpha << 24, true);
         int cardX = pX + 12;
         int cardY = pY + 38;
         int cardW = pWidth - 24;
         int cardH = 28;
         boolean cardHover = mouseX >= cardX && mouseX <= cardX + cardW && mouseY >= cardY && mouseY <= cardY + cardH;
         this.subPanelBindHover = this.step(this.subPanelBindHover, cardHover ? 1.0F : 0.0F, 8.0F);
         float easedCardHover = (float)(1.0 - Math.pow(1.0 - (double)this.subPanelBindHover, 3.0));
         boolean isWaiting = this.bindingTarget == this.subPanelTarget;
         int cardBg = isWaiting ? 1140909567 : (int)(21.0F + 21.0F * easedCardHover) << 24 | 16777215;
         int cardBorder = isWaiting
            ? (int)((187.0 + 68.0 * Math.sin((double)Util.m_137550_() / 150.0)) * (double)((float)alpha / 255.0F)) << 24 | 58879
            : (int)((68.0F + 85.0F * easedCardHover) * ((float)alpha / 255.0F)) << 24 | (cardHover ? '\ue5ff' : 6710886);
         graphics.m_280509_(cardX, cardY, cardX + cardW, cardY + cardH, cardBg);
         graphics.m_280509_(cardX - 1, cardY - 1, cardX + cardW + 1, cardY, cardBorder);
         graphics.m_280509_(cardX - 1, cardY + cardH, cardX + cardW + 1, cardY + cardH + 1, cardBorder);
         graphics.m_280509_(cardX - 1, cardY, cardX, cardY + cardH, cardBorder);
         graphics.m_280509_(cardX + cardW, cardY, cardX + cardW + 1, cardY + cardH, cardBorder);
         Component keyText = this.subPanelTarget.m_90863_();
         if (isWaiting) {
            keyText = Component.m_237115_("screen.invincible_dmc.keybinds.press_any_key");
         }

         graphics.m_280430_(
            this.f_96547_, Component.m_237115_("screen.invincible_dmc.keybinds.current_value"), cardX + 10, cardY + (cardH - 9) / 2, 8947848 | alpha << 24
         );
         graphics.m_280614_(
            this.f_96547_,
            keyText,
            cardX + cardW - this.f_96547_.m_92852_(keyText) - 10,
            cardY + (cardH - 9) / 2,
            isWaiting ? 58879 | alpha << 24 : 16777215 | alpha << 24,
            true
         );
         int resetW = 75;
         int resetH = 16;
         int resetX = pX + pWidth - resetW - 12;
         int resetY = cardY + cardH + 6;
         boolean resetHover = mouseX >= resetX && mouseX <= resetX + resetW && mouseY >= resetY && mouseY <= resetY + resetH;
         this.subPanelResetHover = this.step(this.subPanelResetHover, resetHover ? 1.0F : 0.0F, 8.0F);
         boolean isDef = this.subPanelTarget.m_90864_();
         int resetBorder = (int)((isDef ? 34.0F : 68.0F + 68.0F * this.subPanelResetHover) * ((float)alpha / 255.0F)) << 24
            | (isDef ? 4473924 : (resetHover ? '\ue5ff' : 6710886));
         int resetBg = isDef ? 285212672 : (int)((17.0F + 21.0F * this.subPanelResetHover) * ((float)alpha / 255.0F)) << 24 | 16777215;
         graphics.m_280509_(resetX, resetY, resetX + resetW, resetY + resetH, resetBg);
         graphics.m_280509_(resetX - 1, resetY - 1, resetX + resetW + 1, resetY, resetBorder);
         graphics.m_280509_(resetX - 1, resetY + resetH, resetX + resetW + 1, resetY + resetH + 1, resetBorder);
         graphics.m_280509_(resetX - 1, resetY, resetX, resetY + resetH, resetBorder);
         graphics.m_280509_(resetX + resetW, resetY, resetX + resetW + 1, resetY + resetH, resetBorder);
         int resetCol = isDef ? 4473924 | alpha << 24 : 8947848 | alpha << 24;
         if (resetHover && !isDef) {
            resetCol = 58879 | alpha << 24;
         }

         graphics.m_280653_(
            this.f_96547_, Component.m_237115_("screen.invincible_dmc.keybinds.reset_default"), resetX + resetW / 2, resetY + (resetH - 9) / 2, resetCol
         );
         graphics.m_280168_().m_85849_();
      }
   }

   private int getDMCListIndex(KeyMapping key) {
      for (int i = 0; i < this.keyList.m_6702_().size(); i++) {
         if (((DMCKeyBindsScreen.KeyList.Entry)this.keyList.m_6702_().get(i)).key == key) {
            return i;
         }
      }

      return -1;
   }

   private List<KeyMapping> findConflicts(KeyMapping targetKey) {
      List<KeyMapping> list = new ArrayList<>();
      if (targetKey != null && !targetKey.m_90862_() && targetKey.getKey().m_84873_() != InputConstants.f_84822_.m_84873_() && this.f_96541_ != null) {
         for (KeyMapping other : this.f_96541_.f_91066_.f_92059_) {
            if (other != targetKey
               && !other.m_90862_()
               && other.getKey().m_84873_() != InputConstants.f_84822_.m_84873_()
               && other.getKey().equals(targetKey.getKey())
               && other.getKeyModifier() == targetKey.getKeyModifier()) {
               list.add(other);
            }
         }

         return list;
      } else {
         return list;
      }
   }

   public boolean m_7933_(int keyCode, int scanCode, int modifiers) {
      if (this.bindingTarget != null) {
         if (keyCode == 256) {
            this.bindingTarget.setKeyModifierAndCode(KeyModifier.NONE, InputConstants.f_84822_);
            if (this.f_96541_ != null) {
               this.f_96541_.f_91066_.m_92159_(this.bindingTarget, InputConstants.f_84822_);
            }
         } else {
            KeyModifier activeModifier = KeyModifier.getActiveModifier();
            Key inputKey = InputConstants.m_84827_(keyCode, scanCode);
            this.bindingTarget.setKeyModifierAndCode(activeModifier, inputKey);
            if (this.f_96541_ != null) {
               this.f_96541_.f_91066_.m_92159_(this.bindingTarget, inputKey);
            }
         }

         if (keyCode == 256 || !KeyModifier.isKeyCodeModifier(this.bindingTarget.getKey())) {
            this.bindingTarget = null;
         }

         KeyMapping.m_90854_();
         this.lastKeyClickTime = Util.m_137550_();
         return true;
      } else if (keyCode == 256 && this.subPanelTarget != null) {
         this.subPanelTarget = null;
         this.playUiClickSound();
         return true;
      } else {
         return super.m_7933_(keyCode, scanCode, modifiers);
      }
   }

   public boolean m_7920_(int keyCode, int scanCode, int modifiers) {
      Key releasedKey = InputConstants.m_84827_(keyCode, scanCode);
      if (this.bindingTarget != null && this.bindingTarget.getKey() == releasedKey) {
         this.bindingTarget = null;
         KeyMapping.m_90854_();
         this.lastKeyClickTime = Util.m_137550_();
      }

      return super.m_7920_(keyCode, scanCode, modifiers);
   }

   public boolean m_6375_(double mouseX, double mouseY, int button) {
      if (this.bindingTarget != null) {
         KeyModifier activeModifier = KeyModifier.getActiveModifier();
         Key mouseKey = Type.MOUSE.m_84895_(button);
         this.bindingTarget.setKeyModifierAndCode(activeModifier, mouseKey);
         if (this.f_96541_ != null) {
            this.f_96541_.f_91066_.m_92159_(this.bindingTarget, mouseKey);
         }

         this.bindingTarget = null;
         KeyMapping.m_90854_();
         this.lastKeyClickTime = Util.m_137550_();
         return true;
      } else if (this.subPanelTarget != null && this.subPanelAnim > 0.9F) {
         int pWidth = 260;
         int pHeight = 96;
         int pX = (this.f_96543_ - pWidth) / 2;
         int pY = (this.f_96544_ - pHeight) / 2;
         int closeSize = 14;
         int closeX = pX + pWidth - closeSize - 8;
         int closeY = pY + 8;
         if (button == 0
            && mouseX >= (double)closeX
            && mouseX <= (double)(closeX + closeSize)
            && mouseY >= (double)closeY
            && mouseY <= (double)(closeY + closeSize)) {
            this.subPanelTarget = null;
            this.playUiClickSound();
            return true;
         } else {
            int cardX = pX + 12;
            int cardY = pY + 38;
            int cardW = pWidth - 24;
            int cardH = 28;
            if (button == 0 && mouseX >= (double)cardX && mouseX <= (double)(cardX + cardW) && mouseY >= (double)cardY && mouseY <= (double)(cardY + cardH)) {
               this.bindingTarget = this.subPanelTarget;
               this.playUiClickSound();
               return true;
            } else {
               int resetW = 75;
               int resetH = 16;
               int resetX = pX + pWidth - resetW - 12;
               int resetY = cardY + cardH + 6;
               if (button == 0
                  && mouseX >= (double)resetX
                  && mouseX <= (double)(resetX + resetW)
                  && mouseY >= (double)resetY
                  && mouseY <= (double)(resetY + resetH)) {
                  if (!this.subPanelTarget.m_90864_()) {
                     this.subPanelTarget.setToDefault();
                     if (this.f_96541_ != null) {
                        this.f_96541_.f_91066_.m_92159_(this.subPanelTarget, this.subPanelTarget.m_90861_());
                     }

                     KeyMapping.m_90854_();
                     this.playUiClickSound();
                  }

                  return true;
               } else {
                  boolean clickedInsideSubPanel = mouseX >= (double)pX
                     && mouseX <= (double)(pX + pWidth)
                     && mouseY >= (double)pY
                     && mouseY <= (double)(pY + pHeight);
                  if (button == 0 && !clickedInsideSubPanel) {
                     this.subPanelTarget = null;
                     this.playUiClickSound();
                     return true;
                  } else {
                     return true;
                  }
               }
            }
         }
      } else if (super.m_6375_(mouseX, mouseY, button)) {
         return true;
      } else {
         if (this.selectedIndex >= 0 && this.selectedIndex < this.keyList.m_6702_().size()) {
            DMCKeyBindsScreen.KeyList.Entry entry = (DMCKeyBindsScreen.KeyList.Entry)this.keyList.m_6702_().get(this.selectedIndex);
            KeyMapping key = entry.key;
            int p = Math.max(5, Math.min(15, this.f_96543_ / 55));
            int frameY = 40;
            int dx = this.detailX() + (int)this.slideOffset;
            int dw = this.detailW();
            int cardX = dx + p;
            int cardY = frameY + p + 9 + 22 + this.lastDescExtraHeight;
            int cardW = dw - p * 2;
            int cardH = Math.min(32, 9 + 12);
            if (button == 0 && mouseX >= (double)cardX && mouseX <= (double)(cardX + cardW) && mouseY >= (double)cardY && mouseY <= (double)(cardY + cardH)) {
               this.bindingTarget = key;
               this.playUiClickSound();
               return true;
            }

            int rw = Math.min(72, dw / 5);
            int rh = Math.min(18, 9 + 4);
            int resetX = dx + dw - rw - p;
            int resetY = cardY + cardH + 6;
            if (button == 0 && mouseX >= (double)resetX && mouseX <= (double)(resetX + rw) && mouseY >= (double)resetY && mouseY <= (double)(resetY + rh)) {
               if (!key.m_90864_()) {
                  key.setToDefault();
                  if (this.f_96541_ != null) {
                     this.f_96541_.f_91066_.m_92159_(key, key.m_90861_());
                     this.playUiClickSound();
                  }

                  KeyMapping.m_90854_();
               }

               return true;
            }

            List<KeyMapping> conflicts = this.findConflicts(key);
            if (!conflicts.isEmpty()) {
               int csy = resetY + rh + 8;
               int off = 10;
               int maxS = Math.min(3, conflicts.size());

               for (int i = 0; i < maxS; i++) {
                  KeyMapping cf = conflicts.get(i);
                  Component dis = Component.m_237113_("[" + this.getModSourceOfKey(cf) + "] ").m_7220_(Component.m_237115_(cf.m_90860_()));
                  int tx = dx + p + 8;
                  int ty = csy + off;
                  int bo = tx + this.f_96547_.m_92895_("- " + dis.getString()) + 6;
                  if (button == 0) {
                     int ji = this.getDMCListIndex(cf);
                     if (ji != -1) {
                        if (mouseX >= (double)bo && mouseX <= (double)(bo + 40) && mouseY >= (double)(ty - 1) && mouseY <= (double)(ty + 9)) {
                           DMCKeyBindsScreen.KeyList.Entry target = (DMCKeyBindsScreen.KeyList.Entry)this.keyList.m_6702_().get(ji);
                           this.keyList.setSelected(target);
                           this.keyList.centerScrollOnPublic(target);
                           this.playUiClickSound();
                           return true;
                        }
                     } else if (mouseX >= (double)bo && mouseX <= (double)(bo + 40) && mouseY >= (double)(ty - 1) && mouseY <= (double)(ty + 9)) {
                        this.subPanelTarget = cf;
                        this.playUiClickSound();
                        return true;
                     }
                  }

                  off += 10;
               }
            }
         }

         return false;
      }
   }

   private void playUiClickSound() {
      if (this.f_96541_ != null && this.f_96541_.f_91074_ != null) {
         this.f_96541_.m_91106_().m_120367_(SimpleSoundInstance.m_263171_(SoundEvents.f_12490_, 1.0F));
      }
   }

   public boolean m_6050_(double mouseX, double mouseY, double delta) {
      if (this.subPanelTarget != null) {
         return true;
      } else if (this.keyList.m_5953_(mouseX, mouseY)) {
         this.keyList.applyScroll(delta);
         return true;
      } else {
         return super.m_6050_(mouseX, mouseY, delta);
      }
   }

   class KeyList extends ObjectSelectionList<DMCKeyBindsScreen.KeyList.Entry> {
      public double targetScroll;
      public float slidingIndex = -1.0F;

      public KeyList(Minecraft mc, int width, int height, int top, int bottom, int itemHeight) {
         super(mc, width, height, top, bottom, itemHeight);
         this.addKeyEntry(DMCKeyMappings.DMC_LOCK_ON, Component.m_237115_("screen.invincible_dmc.keybinds.lock_on"));
         this.addKeyEntry(EpicFightKeyMappings.LOCK_ON, Component.m_237115_("screen.invincible_dmc.keybinds.lock_on_switch"));
         this.addKeyEntry(DMCKeyMappings.KEY1, Component.m_237115_("screen.invincible_dmc.keybinds.key1"));
         this.addKeyEntry(DMCKeyMappings.KEY2, Component.m_237115_("screen.invincible_dmc.keybinds.key2"));
         this.addKeyEntry(DMCKeyMappings.KEY3, Component.m_237115_("screen.invincible_dmc.keybinds.key3"));
         this.addKeyEntry(EpicFightKeyMappings.DODGE, Component.m_237115_("screen.invincible_dmc.keybinds.dodge"));
         this.addKeyEntry(DMCKeyMappings.PROVOCATION, Component.m_237115_("screen.invincible_dmc.keybinds.provocation"));
         this.addKeyEntry(DMCKeyMappings.SDT_CHARGE, Component.m_237115_("screen.invincible_dmc.keybinds.sdt"));
         if (DmcWeaponManager.isWeaponSwitchEnabled()) {
            this.addKeyEntry(DMCKeyMappings.WEAPON_SWITCH, Component.m_237115_("screen.invincible_dmc.keybinds.weapon_switch"));
         }

         this.addKeyEntry(DMCKeyMappings.DOPPEL_CONTROL, Component.m_237115_("screen.invincible_dmc.keybinds.doppel_control"));
         this.addKeyEntry(DMCKeyMappings.DOPPEL_FAST, Component.m_237115_("screen.invincible_dmc.keybinds.doppel_fast"));
         this.addKeyEntry(DMCKeyMappings.DOPPEL_MEDIUM, Component.m_237115_("screen.invincible_dmc.keybinds.doppel_medium"));
         this.addKeyEntry(DMCKeyMappings.DOPPEL_DISCARD, Component.m_237115_("screen.invincible_dmc.keybinds.doppel_discard"));
         this.addKeyEntry(DMCKeyMappings.DOPPEL_SLOW, Component.m_237115_("screen.invincible_dmc.keybinds.doppel_slow"));
         this.targetScroll = this.m_93517_();
      }

      private void addKeyEntry(KeyMapping key, Component description) {
         this.m_7085_(new DMCKeyBindsScreen.KeyList.Entry(key, description));
      }

      public void centerScrollOnPublic(DMCKeyBindsScreen.KeyList.Entry entry) {
         super.m_93494_(entry);
         this.targetScroll = this.m_93517_();
      }

      public void m_88315_(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
         if (this.slidingIndex < 0.0F) {
            this.slidingIndex = (float)DMCKeyBindsScreen.this.selectedIndex;
         }

         this.slidingIndex = DMCKeyBindsScreen.this.lerp(this.slidingIndex, (float)DMCKeyBindsScreen.this.selectedIndex, 0.55F);
         graphics.m_280588_(this.f_93393_, this.f_93390_, this.f_93392_, this.f_93391_);
         int bgAlpha = (int)(45.0F * DMCKeyBindsScreen.this.transitionAlpha);
         int accentAlpha = (int)(255.0F * DMCKeyBindsScreen.this.transitionAlpha);
         int boxRight = this.f_93392_ - 6;
         if (bgAlpha > 0 && !this.m_6702_().isEmpty()) {
            int scrollInt = (int)this.m_93517_();
            int hlY = Math.round((float)(this.f_93390_ + 2 - scrollInt) + this.slidingIndex * (float)this.f_93387_);
            int boxHeight = this.f_93387_ - 2;
            graphics.m_280509_(this.f_93393_ + 4, hlY + 1, boxRight, hlY + 1 + boxHeight, bgAlpha << 24 | 16777215);
            graphics.m_280509_(this.f_93393_ + 4, hlY + 1, this.f_93393_ + 6, hlY + 1 + boxHeight, accentAlpha << 24 | 58879);
         }

         graphics.m_280618_();
         super.m_88315_(graphics, mouseX, mouseY, partialTick);
      }

      public void applyScroll(double delta) {
         this.targetScroll = this.targetScroll - delta * (double)this.f_93387_;
         this.targetScroll = Math.max(0.0, Math.min(this.targetScroll, (double)this.m_93518_()));
      }

      public void updateSmoothScroll() {
         double current = this.m_93517_();
         double diff = this.targetScroll - current;
         if (Math.abs(diff) > 0.5) {
            double newScroll = DMCKeyBindsScreen.this.lerp(current, this.targetScroll, 0.55F);
            this.m_93410_(newScroll);
         } else {
            this.m_93410_(this.targetScroll);
         }
      }

      public void setInitialScroll(DMCKeyBindsScreen.KeyList.Entry entry) {
         this.centerScrollOnPublic(entry);
      }

      public int m_5759_() {
         return this.f_93388_;
      }

      protected int m_5756_() {
         return this.f_93388_ + 9999;
      }

      public void setSelected(@Nullable DMCKeyBindsScreen.KeyList.Entry entry) {
         super.m_6987_(entry);
         if (entry != null) {
            DMCKeyBindsScreen.this.selectedIndex = this.m_6702_().indexOf(entry);
         }
      }

      class Entry extends net.minecraft.client.gui.components.ObjectSelectionList.Entry<DMCKeyBindsScreen.KeyList.Entry> {
         final KeyMapping key;
         final Component description;
         private float hoverAnim = 0.0F;
         private float selectAnim = 0.0F;

         Entry(KeyMapping key, Component description) {
            this.key = key;
            this.description = description;
         }

         public void m_6311_(
            GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isHovering, float partialTick
         ) {
            boolean activeHover = isHovering && DMCKeyBindsScreen.this.subPanelTarget == null;
            boolean isSelected = DMCKeyBindsScreen.this.selectedIndex == index;
            this.hoverAnim = DMCKeyBindsScreen.this.step(this.hoverAnim, activeHover ? 1.0F : 0.0F, 24.0F);
            this.selectAnim = DMCKeyBindsScreen.this.step(this.selectAnim, isSelected ? 1.0F : 0.0F, 24.0F);
            float easedHover = (float)(1.0 - Math.pow(1.0 - (double)this.hoverAnim, 3.0));
            float easedSelect = (float)(1.0 - Math.pow(1.0 - (double)this.selectAnim, 3.0));
            int hoverBgAlpha = (int)(easedHover * 30.0F * DMCKeyBindsScreen.this.transitionAlpha);
            int boxRight = KeyList.this.f_93392_ - 6;
            if (hoverBgAlpha > 0 && !isSelected) {
               graphics.m_280509_(KeyList.this.f_93393_ + 4, top + 1, boxRight, top + height - 1, hoverBgAlpha << 24 | 16777215);
            }

            float idleW = 1.0F - Math.max(easedHover, easedSelect);
            float hoverW = easedHover * (1.0F - easedSelect);
            float currentScale = idleW + 1.02F * hoverW + 1.08F * easedSelect;
            float currentOffsetX = 10.0F * idleW + 12.0F * hoverW + 14.0F * easedSelect;
            int safeEntryAlpha = (int)(255.0F * DMCKeyBindsScreen.this.transitionAlpha);
            if (safeEntryAlpha > 8) {
               Component name = Component.m_237115_(this.key.m_90860_());
               int cValue = isSelected ? '\ue5ff' : (activeHover ? 16777215 : 11184810);
               int finalColor = cValue | safeEntryAlpha << 24;
               graphics.m_280168_().m_85836_();
               float textDrawY = (float)top + (float)(height - 9) / 2.0F;
               graphics.m_280168_().m_252880_((float)KeyList.this.f_93393_ + currentOffsetX, textDrawY + 9.0F / 2.0F, 0.0F);
               graphics.m_280168_().m_85841_(currentScale, currentScale, 1.0F);
               graphics.m_280614_(DMCKeyBindsScreen.this.f_96547_, name, 0, (int)((float)(-9) / 2.0F), finalColor, false);
               graphics.m_280168_().m_85849_();
            }
         }

         public boolean m_6375_(double mouseX, double mouseY, int button) {
            if (DMCKeyBindsScreen.this.subPanelTarget != null) {
               return false;
            } else if (button == 0) {
               KeyList.this.setSelected(this);
               DMCKeyBindsScreen.this.playUiClickSound();
               return true;
            } else {
               return false;
            }
         }

         @NotNull
         public Component m_142172_() {
            return Component.m_237115_(this.key.m_90860_());
         }
      }
   }
}
