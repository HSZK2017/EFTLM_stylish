package com.dmc.invincible_dmc.client.gui;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.client.InstantJudgementCutEndClientState;
import com.dmc.invincible_dmc.client.config.AAAPPerformanceClientConfig;
import com.dmc.invincible_dmc.client.config.YamatoClientConfig;
import com.dmc.invincible_dmc.client.effeks.EffekConfig;
import com.dmc.invincible_dmc.client.gui.vergilstatus.VergilStatusConfigScreen;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.ObjectSelectionList.Entry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DMConfigScreen extends Screen {
   private static final String EFFECT_BLOOM_KEY = "effeks.bloom_post_processing";
   private static final int WARNING_ACCENT = 16752412;
   private static final int WARNING_TEXT = 16765066;
   private static final int PRESET_TOP = 24;
   private static final int PRESET_HEIGHT = 20;
   private static final int TAB_TOP = 50;
   private static final int TAB_GAP = 3;
   private static final int MIN_TAB_WIDTH = 96;
   private static final int TOP_BAR_GAP = 6;
   private final Screen lastScreen;
   private DMConfigScreen.ConfigList configList;
   private EditBox searchBox;
   private float transitionAlpha = 0.0F;
   private long lastRenderTime;
   private float dt;
   private float toggleCardHover;
   private float resetBtnHover;
   private float vergilStatusButtonHover;
   private float instantJceButtonHover;
   private int lastDescExtraHeight;
   private int selectedIndex;
   private String searchQuery = "";
   private List<DMConfigScreen.ConfigOption> displayedItems = List.of();
   @Nullable
   private DMConfigScreen.ConfigOption editingNumeric;
   private String numericInputBuf = "";
   private static final List<DMConfigScreen.Category> CATEGORIES = new ArrayList<>();
   private static final List<DMConfigScreen.ConfigOption> ALL_ITEMS = new ArrayList<>();
   private int activeCategory = 0;
   private static final DMConfigScreen.ConfigOption EFFECT_PRESET_OPTION = opt("effeks.preset", "config.invincible_dmc.effeks.preset", DMConfig.EFFECT_PRESET);
   private static final EffekConfig.Preset[] STANDARD_EFFECT_PRESETS = new EffekConfig.Preset[]{
      EffekConfig.Preset.NONE, EffekConfig.Preset.LOW, EffekConfig.Preset.MEDIUM, EffekConfig.Preset.EXTREME
   };

   private static void addCategory(String id, List<DMConfigScreen.ConfigOption> items) {
      CATEGORIES.add(new DMConfigScreen.Category("config.invincible_dmc.category." + id, id, List.copyOf(items)));
   }

   private static void addUniqueOptions(List<DMConfigScreen.ConfigOption> target, List<DMConfigScreen.ConfigOption> source) {
      for (DMConfigScreen.ConfigOption option : source) {
         if (target.stream().noneMatch(existing -> existing.key.equals(option.key))) {
            target.add(option);
         }
      }
   }

   private static List<DMConfigScreen.ConfigOption> selectOptions(List<DMConfigScreen.ConfigOption> source, String... keys) {
      List<DMConfigScreen.ConfigOption> selected = new ArrayList<>(keys.length);

      for (String key : keys) {
         DMConfigScreen.ConfigOption option = source.stream()
            .filter(candidate -> candidate.key.equals(key))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Missing config option: " + key));
         selected.add(option);
      }

      return selected;
   }

   @SafeVarargs
   private static List<DMConfigScreen.ConfigOption> concatOptions(List<DMConfigScreen.ConfigOption>... groups) {
      List<DMConfigScreen.ConfigOption> combined = new ArrayList<>();

      for (List<DMConfigScreen.ConfigOption> group : groups) {
         combined.addAll(group);
      }

      return combined;
   }

   private static DMConfigScreen.ConfigOption opt(String k, String d, ConfigValue<?> s) {
      return new DMConfigScreen.ConfigOption(k, d, s, () -> true);
   }

   private static DMConfigScreen.ConfigOption linkedOpt(String k, String d, ConfigValue<?> s, BooleanSupplier enabledCondition) {
      return new DMConfigScreen.ConfigOption(k, d, s, enabledCondition);
   }

   private static DMConfigScreen.ConfigOption aaapOpt(String key, ConfigValue<?> spec) {
      return opt("aaaparticles." + key, "config.invincible_dmc.aaap_performance." + key, spec);
   }

   private static DMConfigScreen.ConfigOption aaapLinkedOpt(String key, ConfigValue<?> spec, BooleanSupplier enabledCondition) {
      return linkedOpt("aaaparticles." + key, "config.invincible_dmc.aaap_performance." + key, spec, enabledCondition);
   }

   private boolean compactLayout() {
      return this.f_96543_ < 700 || this.f_96544_ < 420;
   }

   private boolean tightHeightLayout() {
      return this.f_96544_ < 300;
   }

   private boolean showScreenTitle() {
      return !this.tightHeightLayout();
   }

   private int presetTop() {
      return this.compactLayout() ? (this.showScreenTitle() ? 20 : 6) : 24;
   }

   private int presetHeight() {
      return 20;
   }

   private int tabTop() {
      return this.compactLayout() ? this.presetTop() + this.presetHeight() + 6 : 50;
   }

   private int tabGap() {
      return this.compactLayout() ? 2 : 3;
   }

   private int minTabWidth() {
      return 96;
   }

   private int contentBottom() {
      return this.vergilStatusButtonY() - 6;
   }

   private int listItemHeight() {
      return 9 + (this.compactLayout() ? 7 : 8);
   }

   private int contentGap() {
      return this.compactLayout() ? 6 : 8;
   }

   private int contentRightMargin() {
      return this.compactLayout() ? 8 : 14;
   }

   private int listW() {
      int minimum = this.compactLayout() ? 82 : 110;
      int preferred = (int)((float)this.f_96543_ * (this.compactLayout() ? 0.34F : 0.28F));
      int maximum = this.compactLayout() ? 180 : 260;
      int minimumDetail = this.compactLayout() ? 100 : 140;
      int available = this.f_96543_ - 8 - this.contentRightMargin();
      int widthForList = Math.max(minimum, available - this.contentGap() - minimumDetail);
      return Math.min(widthForList, Math.max(minimum, Math.min(maximum, preferred)));
   }

   private int detailX() {
      return 8 + this.listW() + this.contentGap();
   }

   private int detailW() {
      return Math.max(1, this.f_96543_ - this.detailX() - this.contentRightMargin());
   }

   private int layoutPad() {
      return this.compactLayout() ? Math.max(5, Math.min(9, this.detailW() / 24)) : this.pad();
   }

   private int pad() {
      return Math.max(6, Math.min(15, this.f_96543_ / 45));
   }

   private int tabHeight() {
      return 9 + (this.compactLayout() ? 7 : 12);
   }

   private int topBarWidth() {
      return Math.max(1, this.f_96543_ - 16);
   }

   private int searchWidth() {
      int minimum = this.compactLayout() ? 72 : 110;
      int preferred = Math.min(this.compactLayout() ? 170 : 230, Math.max(minimum, this.topBarWidth() / 3));
      int maximum = Math.max(minimum, this.topBarWidth() - (this.compactLayout() ? 90 : 110) - 6);
      return Math.min(preferred, maximum);
   }

   private int presetWidth() {
      return Math.max(1, this.topBarWidth() - this.searchWidth() - 6);
   }

   private int searchX() {
      return 8 + this.presetWidth() + 6;
   }

   private int searchInputX() {
      return this.searchX() + 9;
   }

   private int searchInputY() {
      return this.presetTop() + (this.presetHeight() - 9) / 2;
   }

   private int searchInputWidth() {
      return Math.max(20, this.searchWidth() - 18);
   }

   private int tabCount() {
      return CATEGORIES.size() + 1;
   }

   private int tabColumns() {
      return this.compactLayout()
         ? Math.max(2, Math.min(5, this.topBarWidth() / 56))
         : Math.max(1, Math.min(this.tabCount(), (this.topBarWidth() + this.tabGap()) / (this.minTabWidth() + this.tabGap())));
   }

   private int tabRows() {
      return (this.tabCount() + this.tabColumns() - 1) / this.tabColumns();
   }

   private int tabWidth() {
      return (this.topBarWidth() - this.tabGap() * (this.tabColumns() - 1)) / this.tabColumns();
   }

   private int tabX(int index) {
      return 8 + index % this.tabColumns() * (this.tabWidth() + this.tabGap());
   }

   private int tabY(int index) {
      return this.tabTop() + index / this.tabColumns() * (this.tabHeight() + this.tabGap());
   }

   private int contentTop() {
      return this.tabTop() + this.tabRows() * (this.tabHeight() + this.tabGap()) - this.tabGap() + 4;
   }

   private List<DMConfigScreen.ConfigOption> buildVisibleItems() {
      List<DMConfigScreen.ConfigOption> categoryItems = this.activeCategory == 0 ? ALL_ITEMS : CATEGORIES.get(this.activeCategory - 1).items;
      return this.searchQuery.isBlank()
         ? List.copyOf(categoryItems)
         : categoryItems.stream()
            .map(option -> new DMConfigScreen.SearchResult(option, searchScore(option, this.searchQuery)))
            .filter(result -> result.score != Integer.MAX_VALUE)
            .sorted(Comparator.comparingInt(DMConfigScreen.SearchResult::score))
            .map(DMConfigScreen.SearchResult::option)
            .toList();
   }

   private DMConfigScreen.ConfigOption selectedOpt() {
      return this.selectedIndex >= 0 && this.selectedIndex < this.displayedItems.size() ? this.displayedItems.get(this.selectedIndex) : null;
   }

   private static int searchScore(DMConfigScreen.ConfigOption option, String query) {
      String name = translatedOrEmpty(option.description);
      String tooltip = translatedOrEmpty(option.description + ".tooltip");
      String key = option.key;
      String value = option.displayVal();
      int score = 0;

      for (String rawToken : query.toLowerCase(Locale.ROOT).trim().split("\\s+")) {
         String token = normalizeSearch(rawToken);
         if (!token.isEmpty()) {
            int tokenScore = Math.min(
               Math.min(fuzzyFieldScore(token, name, 0), fuzzyFieldScore(token, key, 1)),
               Math.min(fuzzyFieldScore(token, tooltip, 2), fuzzyFieldScore(token, value, 3))
            );
            if (tokenScore == Integer.MAX_VALUE) {
               return Integer.MAX_VALUE;
            }

            score += tokenScore;
         }
      }

      return score;
   }

   private static int fuzzyFieldScore(String token, String field, int fieldPriority) {
      String normalizedField = normalizeSearch(field);
      if (normalizedField.contains(token)) {
         return fieldPriority;
      } else {
         return isSubsequence(token, normalizedField) ? fieldPriority + 8 : Integer.MAX_VALUE;
      }
   }

   private static String translatedOrEmpty(String translationKey) {
      String translated = Component.m_237115_(translationKey).getString();
      return translated.equals(translationKey) ? "" : translated;
   }

   private static String normalizeSearch(String value) {
      StringBuilder normalized = new StringBuilder(value.length());
      value.codePoints().forEach(codePoint -> {
         if (Character.isLetterOrDigit(codePoint)) {
            normalized.appendCodePoint(Character.toLowerCase(codePoint));
         }
      });
      return normalized.toString();
   }

   private static boolean isSubsequence(String query, String value) {
      int queryIndex = 0;

      for (int valueIndex = 0; valueIndex < value.length() && queryIndex < query.length(); valueIndex++) {
         if (query.charAt(queryIndex) == value.charAt(valueIndex)) {
            queryIndex++;
         }
      }

      return queryIndex == query.length();
   }

   public DMConfigScreen(Screen lastScreen) {
      super(Component.m_237115_("screen.invincible_dmc.config.title"));
      this.lastScreen = lastScreen;
   }

   protected void m_7856_() {
      super.m_7856_();
      this.lastRenderTime = 0L;
      InstantJudgementCutEndClientState.requestState();
      EffekConfig.refreshPresetFromValues();
      this.searchBox = new EditBox(
         this.f_96547_, this.searchInputX(), this.searchInputY(), this.searchInputWidth(), 9, Component.m_237115_("screen.invincible_dmc.config.search")
      );
      this.searchBox.m_94182_(false);
      this.searchBox.m_94199_(128);
      this.searchBox.m_94202_(16777215);
      this.searchBox.m_94205_(8947848);
      this.searchBox.m_257771_(Component.m_237115_("screen.invincible_dmc.config.search_hint"));
      this.searchBox.m_94144_(this.searchQuery);
      this.searchBox.m_94151_(this::onSearchChanged);
      this.m_142416_(this.searchBox);
      int listTop = this.contentTop();
      this.configList = new DMConfigScreen.ConfigList(this.f_96541_, this.listW(), this.f_96544_, listTop, this.contentBottom(), this.listItemHeight());
      this.configList.m_93507_(8);
      this.configList.m_93488_(false);
      this.configList.m_93496_(false);
      this.configList.m_93471_(false);
      this.m_7787_(this.configList);
      this.refreshList();
   }

   private void refreshList() {
      this.displayedItems = this.buildVisibleItems();
      if (this.displayedItems.isEmpty()) {
         this.selectedIndex = -1;
      } else if (this.selectedIndex < 0 || this.selectedIndex >= this.displayedItems.size()) {
         this.selectedIndex = 0;
      }

      if (this.configList != null) {
         this.configList.rebuild(this.displayedItems);
      }
   }

   private void onSearchChanged(String value) {
      boolean searchStarted = this.searchQuery.isBlank() && !value.isBlank();
      this.searchQuery = value;
      if (searchStarted) {
         this.activeCategory = 0;
      }

      this.editingNumeric = null;
      this.selectedIndex = 0;
      this.refreshList();
   }

   public void m_7379_() {
      if (this.f_96541_ != null) {
         this.f_96541_.m_91152_(this.lastScreen);
      }
   }

   private float lerp(float a, float b, float s) {
      return a + (b - a) * (1.0F - (float)Math.pow((double)(1.0F - s), (double)(this.dt * 60.0F)));
   }

   private float step(float a, float b, float s) {
      float x = s * this.dt;
      return a < b ? Math.min(a + x, b) : Math.max(a - x, b);
   }

   public void m_88315_(@NotNull GuiGraphics g, int mx, int my, float pt) {
      long now = Util.m_137550_();
      if (this.lastRenderTime == 0L) {
         this.lastRenderTime = now;
      }

      this.dt = Math.min((float)(now - this.lastRenderTime) / 1000.0F, 0.1F);
      this.lastRenderTime = now;
      this.transitionAlpha = this.lerp(this.transitionAlpha, 1.0F, 0.12F);
      int alpha = (int)(255.0F * this.transitionAlpha);
      g.m_280509_(0, 0, this.f_96543_, this.f_96544_, (int)(140.0F * this.transitionAlpha) << 24);
      if (alpha > 8 && this.showScreenTitle()) {
         g.m_280653_(this.f_96547_, this.f_96539_, this.f_96543_ / 2, this.compactLayout() ? 5 : 8, 16777215 | alpha << 24);
      }

      this.renderEffectPreset(g, mx, my, alpha);
      if (this.searchBox != null) {
         this.renderSearchBox(g, mx, my, alpha);
         this.searchBox.m_88315_(g, mx, my, pt);
      }

      int lw = this.listW();
      int tabH = this.tabHeight();
      int tabW = this.tabWidth();

      for (int i = 0; i < this.tabCount(); i++) {
         String name = this.categoryName(i);
         int tx = this.tabX(i);
         int ty = this.tabY(i);
         boolean active = i == this.activeCategory;
         boolean performanceCompatibility = this.isPerformanceCompatibilityCategory(i);
         int accent = performanceCompatibility ? 16752412 : '\ue5ff';
         int background = active
            ? (int)((float)(performanceCompatibility ? 82 : 55) * this.transitionAlpha) << 24 | accent
            : (performanceCompatibility ? (int)(38.0F * this.transitionAlpha) << 24 | 5910528 : (int)(20.0F * this.transitionAlpha) << 24 | 2763306);
         g.m_280509_(tx, ty, tx + tabW, ty + tabH, background);
         if (active || performanceCompatibility) {
            g.m_280509_(tx, ty + tabH - 2, tx + tabW, ty + tabH, (int)((float)(active ? 255 : 180) * this.transitionAlpha) << 24 | accent);
         }

         int maxTextWidth = tabW - 6;
         String display = this.ellipsize(name, maxTextWidth);
         int textColor = active ? 16777215 | alpha << 24 : (performanceCompatibility ? 16765066 | alpha << 24 : 11184810 | alpha << 24);
         g.m_280137_(this.f_96547_, display, tx + tabW / 2, ty + (tabH - 9) / 2, textColor);
      }

      int lTop = this.contentTop();
      int fh = this.contentBottom() - lTop;
      this.configList.m_93507_(8);
      this.configList.updateSmoothScroll();
      this.configList.m_88315_(g, mx, my, pt);
      this.drawFrame(g, 8, lTop, lw, fh, this.transitionAlpha, 58879);
      if (this.displayedItems.isEmpty()) {
         g.m_280653_(this.f_96547_, Component.m_237115_("screen.invincible_dmc.config.no_results"), 8 + lw / 2, lTop + 12, 8947848 | alpha << 24);
      }

      DMConfigScreen.ConfigOption sel = this.selectedOpt();
      if (sel != null) {
         int dx = this.detailX();
         int dw = this.detailW();
         this.drawFrame(g, dx, lTop, dw, fh, this.transitionAlpha, isEffectBloom(sel) ? 16752412 : '\ue5ff');
         this.renderDetail(g, sel, dx, lTop, dw, fh, mx, my, alpha);
      }

      this.renderInstantJceButton(g, mx, my, alpha);
      this.renderVergilStatusButton(g, mx, my, alpha);
      this.renderCategoryTooltip(g, mx, my);
   }

   private void renderCategoryTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
      int availableTextWidth = this.tabWidth() - 6;

      for (int i = 0; i < this.tabCount(); i++) {
         String name = this.categoryName(i);
         if (this.f_96547_.m_92895_(name) > availableTextWidth) {
            int x = this.tabX(i);
            int y = this.tabY(i);
            if (mouseX >= x && mouseX <= x + this.tabWidth() && mouseY >= y && mouseY <= y + this.tabHeight()) {
               graphics.m_280557_(this.f_96547_, Component.m_237113_(name), mouseX, mouseY);
               return;
            }
         }
      }
   }

   private String categoryName(int index) {
      return index == 0
         ? Component.m_237115_("config.invincible_dmc.category.all").getString()
         : Component.m_237115_(CATEGORIES.get(index - 1).transKey).getString();
   }

   private boolean isPerformanceCompatibilityCategory(int index) {
      return index > 0 && "performance_compatibility".equals(CATEGORIES.get(index - 1).filterKey);
   }

   private String ellipsize(String text, int maxWidth) {
      if (this.f_96547_.m_92895_(text) <= maxWidth) {
         return text;
      } else {
         int ellipsisWidth = this.f_96547_.m_92895_("...");
         return maxWidth <= ellipsisWidth ? "" : this.f_96547_.m_92834_(text, maxWidth - ellipsisWidth) + "...";
      }
   }

   private void renderVergilStatusButton(GuiGraphics graphics, int mouseX, int mouseY, int alpha) {
      int x = this.vergilStatusButtonX();
      int y = this.vergilStatusButtonY();
      int buttonWidth = this.vergilStatusButtonWidth();
      int buttonHeight = this.vergilStatusButtonHeight();
      boolean hovered = this.isInsideVergilStatusButton((double)mouseX, (double)mouseY);
      this.vergilStatusButtonHover = this.step(this.vergilStatusButtonHover, hovered ? 1.0F : 0.0F, 10.0F);
      float easedHover = 1.0F - (float)Math.pow((double)(1.0F - this.vergilStatusButtonHover), 3.0);
      int backgroundAlpha = (int)((32.0F + 32.0F * easedHover) * this.transitionAlpha);
      int borderAlpha = (int)((119.0F + 102.0F * easedHover) * this.transitionAlpha);
      int accent = hovered ? 6747647 : '\ue5ff';
      graphics.m_280509_(x, y, x + buttonWidth, y + buttonHeight, backgroundAlpha << 24 | 1058864);
      this.drawBorder(graphics, x, y, buttonWidth, buttonHeight, borderAlpha << 24 | accent);
      graphics.m_280509_(x, y, x + (hovered ? 4 : 3), y + buttonHeight, alpha << 24 | accent);
      graphics.m_280653_(
         this.f_96547_,
         Component.m_237115_("screen.invincible_dmc.config.vergil_status"),
         x + buttonWidth / 2,
         y + (buttonHeight - 9) / 2,
         (hovered ? 16777215 : 13171199) | alpha << 24
      );
      if (hovered) {
         graphics.m_280557_(this.f_96547_, Component.m_237115_("screen.invincible_dmc.config.vergil_status.tooltip"), mouseX, mouseY);
      }
   }

   private void renderInstantJceButton(GuiGraphics graphics, int mouseX, int mouseY, int alpha) {
      int x = this.instantJceButtonX();
      int y = this.vergilStatusButtonY();
      int buttonWidth = this.footerButtonWidth();
      int buttonHeight = this.vergilStatusButtonHeight();
      boolean hovered = this.isInsideInstantJceButton((double)mouseX, (double)mouseY);
      boolean known = InstantJudgementCutEndClientState.isKnown();
      boolean learned = InstantJudgementCutEndClientState.isLearned();
      boolean enabled = InstantJudgementCutEndClientState.isEnabled();
      boolean pending = InstantJudgementCutEndClientState.isPending();
      boolean available = known && learned && !pending;
      this.instantJceButtonHover = this.step(this.instantJceButtonHover, hovered ? 1.0F : 0.0F, 10.0F);
      float easedHover = 1.0F - (float)Math.pow((double)(1.0F - this.instantJceButtonHover), 3.0);
      int backgroundAlpha = (int)((32.0F + 32.0F * easedHover) * this.transitionAlpha);
      int borderAlpha = (int)((119.0F + 102.0F * easedHover) * this.transitionAlpha);
      int accent = available ? (enabled ? 11955455 : 8218255) : 6710886;
      graphics.m_280509_(x, y, x + buttonWidth, y + buttonHeight, backgroundAlpha << 24 | 2364719);
      this.drawBorder(graphics, x, y, buttonWidth, buttonHeight, borderAlpha << 24 | accent);
      graphics.m_280509_(x, y, x + (hovered ? 4 : 3), y + buttonHeight, alpha << 24 | accent);
      String stateKey;
      if (!known || pending) {
         stateKey = "screen.invincible_dmc.config.instant_jce.state.syncing";
      } else if (!learned) {
         stateKey = "screen.invincible_dmc.config.instant_jce.state.not_learned";
      } else if (enabled) {
         stateKey = "screen.invincible_dmc.config.instant_jce.state.enabled";
      } else {
         stateKey = "screen.invincible_dmc.config.instant_jce.state.disabled";
      }

      Component label = Component.m_237110_("screen.invincible_dmc.config.instant_jce", new Object[]{Component.m_237115_(stateKey)});
      String labelText = label.getString();
      int labelWidth = buttonWidth - 10;
      if (this.f_96547_.m_92895_(labelText) > labelWidth) {
         labelText = this.f_96547_.m_92834_(labelText, Math.max(1, labelWidth - this.f_96547_.m_92895_("..."))) + "...";
      }

      graphics.m_280137_(this.f_96547_, labelText, x + buttonWidth / 2, y + (buttonHeight - 9) / 2, (available ? 15851007 : 10066329) | alpha << 24);
      if (hovered) {
         String tooltipKey = learned ? "screen.invincible_dmc.config.instant_jce.tooltip" : "screen.invincible_dmc.config.instant_jce.tooltip.not_learned";
         graphics.m_280557_(this.f_96547_, Component.m_237115_(tooltipKey), mouseX, mouseY);
      }
   }

   private int vergilStatusButtonWidth() {
      return this.footerButtonWidth();
   }

   private int vergilStatusButtonHeight() {
      return this.compactLayout() ? 20 : 22;
   }

   private int footerMargin() {
      return this.compactLayout() ? 8 : 14;
   }

   private int footerButtonGap() {
      return this.compactLayout() ? 5 : 7;
   }

   private int footerButtonWidth() {
      int preferred = this.compactLayout() ? 180 : 220;
      int available = Math.max(72, (this.f_96543_ - this.footerMargin() * 2 - this.footerButtonGap()) / 2);
      return Math.min(preferred, available);
   }

   private int vergilStatusButtonX() {
      return this.f_96543_ - this.vergilStatusButtonWidth() - this.footerMargin();
   }

   private int vergilStatusButtonY() {
      return this.f_96544_ - this.vergilStatusButtonHeight() - (this.compactLayout() ? 6 : 10);
   }

   private int instantJceButtonX() {
      return this.vergilStatusButtonX() - this.footerButtonGap() - this.footerButtonWidth();
   }

   private boolean isInsideVergilStatusButton(double mouseX, double mouseY) {
      int x = this.vergilStatusButtonX();
      int y = this.vergilStatusButtonY();
      return mouseX >= (double)x
         && mouseX <= (double)(x + this.vergilStatusButtonWidth())
         && mouseY >= (double)y
         && mouseY <= (double)(y + this.vergilStatusButtonHeight());
   }

   private boolean isInsideInstantJceButton(double mouseX, double mouseY) {
      int x = this.instantJceButtonX();
      int y = this.vergilStatusButtonY();
      return mouseX >= (double)x
         && mouseX <= (double)(x + this.footerButtonWidth())
         && mouseY >= (double)y
         && mouseY <= (double)(y + this.vergilStatusButtonHeight());
   }

   private void renderEffectPreset(GuiGraphics g, int mx, int my, int alpha) {
      int x = 8;
      int y = this.presetTop();
      int w = this.presetWidth();
      int h = this.presetHeight();
      boolean hovered = mx >= x && mx <= x + w && my >= y && my <= y + h;
      int background = (int)((float)(hovered ? 48 : 32) * this.transitionAlpha) << 24 | 1058864;
      int border = (int)((float)(hovered ? 204 : 136) * this.transitionAlpha) << 24 | 58879;
      g.m_280509_(x, y, x + w, y + h, background);
      this.drawBorder(g, x, y, w, h, border);
      g.m_280509_(x, y, x + 3, y + h, alpha << 24 | 58879);
      String value = EFFECT_PRESET_OPTION.displayVal();
      String left = "◀ ";
      String right = " ▶";
      int leftWidth = this.f_96547_.m_92895_(left);
      int valueWidth = this.f_96547_.m_92895_(value);
      int rightWidth = this.f_96547_.m_92895_(right);
      int rightEdge = x + w - 10;
      int controlLeft = rightEdge - leftWidth - valueWidth - rightWidth;
      String name = Component.m_237115_(EFFECT_PRESET_OPTION.description).getString();
      int maxNameWidth = Math.max(20, controlLeft - x - 20);
      if (this.f_96547_.m_92895_(name) > maxNameWidth) {
         name = this.f_96547_.m_92834_(name, maxNameWidth - this.f_96547_.m_92895_("…")) + "…";
      }

      g.m_280056_(this.f_96547_, name, x + 10, y + (h - 9) / 2, 16777215 | alpha << 24, true);
      boolean leftHovered = mx >= controlLeft && mx <= controlLeft + leftWidth && my >= y && my <= y + h;
      boolean rightHovered = mx >= controlLeft + leftWidth + valueWidth && mx <= rightEdge && my >= y && my <= y + h;
      g.m_280056_(this.f_96547_, left, controlLeft, y + (h - 9) / 2, (leftHovered ? 16737894 : 11184810) | alpha << 24, true);
      g.m_280056_(this.f_96547_, value, controlLeft + leftWidth, y + (h - 9) / 2, (hovered ? 16777215 : '\ue5ff') | alpha << 24, true);
      g.m_280056_(this.f_96547_, right, controlLeft + leftWidth + valueWidth, y + (h - 9) / 2, (rightHovered ? 'ﾈ' : 11184810) | alpha << 24, true);
   }

   private void renderSearchBox(GuiGraphics g, int mx, int my, int alpha) {
      int x = this.searchX();
      int y = this.presetTop();
      int w = this.searchWidth();
      int h = this.presetHeight();
      boolean focused = this.searchBox != null && this.searchBox.m_93696_();
      boolean hovered = mx >= x && mx <= x + w && my >= y && my <= y + h;
      int background = (int)((float)(focused ? 56 : (hovered ? 48 : 32)) * this.transitionAlpha) << 24 | 1058864;
      int borderAlpha = focused ? 238 : (hovered ? 170 : 102);
      int border = (int)((float)borderAlpha * this.transitionAlpha) << 24 | 58879;
      g.m_280509_(x, y, x + w, y + h, background);
      this.drawBorder(g, x, y, w, h, border);
      g.m_280509_(x, y, x + (focused ? 3 : 2), y + h, alpha << 24 | 58879);
   }

   private boolean clickEffectPreset(double mx, double my, int button) {
      if (button == 0
         && !(mx < 8.0)
         && !(mx > (double)(8 + this.presetWidth()))
         && !(my < (double)this.presetTop())
         && !(my > (double)(this.presetTop() + this.presetHeight()))) {
         String value = EFFECT_PRESET_OPTION.displayVal();
         int leftWidth = this.f_96547_.m_92895_("◀ ");
         int valueWidth = this.f_96547_.m_92895_(value);
         int rightWidth = this.f_96547_.m_92895_(" ▶");
         int rightEdge = 8 + this.presetWidth() - 10;
         int controlLeft = rightEdge - leftWidth - valueWidth - rightWidth;
         if (mx <= (double)(controlLeft + leftWidth)) {
            this.applyAdjacentEffectPreset(false);
         } else {
            if (!(mx >= (double)(controlLeft + leftWidth + valueWidth))) {
               return true;
            }

            this.applyAdjacentEffectPreset(true);
         }

         this.clickSound();
         return true;
      } else {
         return false;
      }
   }

   private void applyAdjacentEffectPreset(boolean forward) {
      EffekConfig.Preset current = (EffekConfig.Preset)DMConfig.EFFECT_PRESET.get();
      List<EffekConfig.Preset> presets = new ArrayList<>(List.of(STANDARD_EFFECT_PRESETS));
      if (EffekConfig.hasCustomPresetSnapshot()) {
         presets.add(EffekConfig.Preset.CUSTOM);
      }

      int currentIndex = presets.indexOf(current);
      if (currentIndex < 0) {
         currentIndex = forward ? presets.size() - 1 : 0;
      }

      int offset = forward ? 1 : presets.size() - 1;
      EffekConfig.Preset next = presets.get((currentIndex + offset) % presets.size());
      EffekConfig.applyPreset(next);
   }

   private void refreshPresetStatus(DMConfigScreen.ConfigOption option) {
      if (EffekConfig.isPresetManaged(option.key)) {
         EffekConfig.refreshPresetFromValues();
      }
   }

   private void renderDetail(GuiGraphics g, DMConfigScreen.ConfigOption opt, int x, int y, int w, int h, int mx, int my, int alpha) {
      int p = this.layoutPad();
      boolean effectBloom = isEffectBloom(opt);
      int detailAccent = effectBloom ? 16752412 : '\ue5ff';
      String name = Component.m_237115_(opt.description).getString();
      if (name.equals(opt.description)) {
         name = opt.key;
      }

      int maxNameW = w - p * 2;
      if (this.f_96547_.m_92895_(name) > maxNameW) {
         name = this.f_96547_.m_92834_(name, maxNameW - this.f_96547_.m_92895_("...")) + "...";
      }

      g.m_280056_(this.f_96547_, name, x + p, y + p, (effectBloom ? 16765066 : 16777215) | alpha << 24, true);
      g.m_280509_(x + p, y + p + 9 + 2, x + p + Math.min(w / 3, maxNameW), y + p + 9 + 3, alpha << 24 | detailAccent);
      Component desc = Component.m_237115_(opt.description + ".tooltip");
      if (desc.getString().equals(opt.description + ".tooltip")) {
         desc = Component.m_237113_("Config: " + opt.key);
      }

      this.lastDescExtraHeight = this.drawMultiline(
         g, desc, x + p, y + p + 9 + 10, w - p * 2, (effectBloom ? 16765066 : 11184810) | alpha << 24, this.detailDescriptionLines(h)
      );
      int cx = x + p;
      int cy = y + p + 9 + 20 + this.lastDescExtraHeight;
      int cw = w - p * 2;
      int ch = Math.min(34, 9 + 12);
      boolean cardHov = mx >= cx && mx <= cx + cw && my >= cy && my <= cy + ch;
      this.toggleCardHover = this.step(this.toggleCardHover, cardHov ? 1.0F : 0.0F, 8.0F);
      float eh = 1.0F - (float)Math.pow((double)(1.0F - this.toggleCardHover), 3.0);
      int cardBg = (int)(34.0F + 21.0F * eh) << 24 | 16777215;
      int cardBd = (int)((85.0F + 102.0F * eh) * this.transitionAlpha) << 24 | (cardHov ? '\ue5ff' : 8947848);
      g.m_280509_(cx, cy, cx + cw, cy + ch, cardBg);
      this.drawBorder(g, cx, cy, cw, ch, cardBd);
      if (!this.compactLayout() || cw >= 190) {
         g.m_280430_(this.f_96547_, Component.m_237115_("screen.invincible_dmc.config.current_status"), cx + 8, cy + (ch - 9) / 2, 8947848 | alpha << 24);
      }

      if (opt.isBool()) {
         boolean on = (Boolean)opt.spec.get();
         String st = on ? "[ ON ]" : "[ OFF ]";
         int sc = cardHov ? (on ? 5636010 : 16737894) : (on ? 'ﾈ' : 16724787);
         g.m_280056_(this.f_96547_, st, cx + cw - this.f_96547_.m_92895_(st) - 8, cy + (ch - 9) / 2, sc | alpha << 24, true);
      } else if (opt.isEnum()) {
         String vs = opt.displayVal();
         String left = "◀ ";
         String right = " ▶";
         int lw = this.f_96547_.m_92895_(left);
         int rw = this.f_96547_.m_92895_(right);
         int vw = this.f_96547_.m_92895_(vs);
         int totalW = lw + vw + rw;
         int rightEdge = cx + cw - 8;
         int leftEdge = rightEdge - totalW;
         boolean lHov = mx >= leftEdge && mx <= leftEdge + lw && my >= cy && my <= cy + ch;
         int lc = lHov ? 16737894 : 11184810;
         g.m_280056_(this.f_96547_, left, leftEdge, cy + (ch - 9) / 2, lc | alpha << 24, true);
         int vc = cardHov ? 16777215 : '\ue5ff';
         g.m_280056_(this.f_96547_, vs, leftEdge + lw, cy + (ch - 9) / 2, vc | alpha << 24, true);
         boolean rHov = mx >= leftEdge + lw + vw && mx <= rightEdge && my >= cy && my <= cy + ch;
         int rc = rHov ? 'ﾈ' : 11184810;
         g.m_280056_(this.f_96547_, right, leftEdge + lw + vw, cy + (ch - 9) / 2, rc | alpha << 24, true);
      } else if (opt.isNum()) {
         boolean editing = this.editingNumeric == opt;
         String vs = editing ? (this.numericInputBuf.isEmpty() ? "0" : this.numericInputBuf) : opt.displayVal();
         if (editing && System.currentTimeMillis() / 500L % 2L == 0L) {
            vs = vs + "|";
         }

         String minus = " - ";
         String plus = " + ";
         int mw = this.f_96547_.m_92895_(minus);
         int pw = this.f_96547_.m_92895_(plus);
         int vw = this.f_96547_.m_92895_(vs);
         int totalW = mw + vw + pw;
         int rightEdge = cx + cw - 8;
         int leftEdge = rightEdge - totalW;
         boolean minusHov = !editing && mx >= leftEdge && mx <= leftEdge + mw && my >= cy && my <= cy + ch;
         int mc = minusHov ? 16737894 : 11184810;
         g.m_280056_(this.f_96547_, minus, leftEdge, cy + (ch - 9) / 2, mc | alpha << 24, true);
         boolean valHov = mx >= leftEdge + mw && mx <= leftEdge + mw + vw && my >= cy && my <= cy + ch;
         int vc = editing ? 16766720 : (valHov ? 16777215 : '\ue5ff');
         g.m_280056_(this.f_96547_, vs, leftEdge + mw, cy + (ch - 9) / 2, vc | alpha << 24, true);
         boolean plusHov = !editing && mx >= leftEdge + mw + vw && mx <= rightEdge && my >= cy && my <= cy + ch;
         int pc = plusHov ? 'ﾈ' : 11184810;
         g.m_280056_(this.f_96547_, plus, leftEdge + mw + vw, cy + (ch - 9) / 2, pc | alpha << 24, true);
      }

      int rw = Math.min(w - p * 2, Math.min(96, Math.max(54, w / 3)));
      int rh = Math.min(18, 9 + 4);
      int rx = x + w - rw - p;
      int ry = Math.min(cy + ch + 6, y + h - rh - p);
      boolean rHov = mx >= rx && mx <= rx + rw && my >= ry && my <= ry + rh;
      this.resetBtnHover = this.step(this.resetBtnHover, rHov ? 1.0F : 0.0F, 8.0F);
      float erh = 1.0F - (float)Math.pow((double)(1.0F - this.resetBtnHover), 3.0);
      boolean def = opt.isDefault();
      g.m_280509_(rx, ry, rx + rw, ry + rh, def ? 285212672 : (int)((17.0F + 34.0F * erh) * this.transitionAlpha) << 24 | 16777215);
      this.drawBorder(
         g, rx, ry, rw, rh, (int)((def ? 34.0F : 85.0F + 85.0F * erh) * this.transitionAlpha) << 24 | (def ? 5592405 : (rHov ? '\ue5ff' : 8947848))
      );
      int rc = def ? 5592405 : (rHov ? '\ue5ff' : 11184810);
      g.m_280653_(this.f_96547_, Component.m_237115_("screen.invincible_dmc.config.reset_default"), rx + rw / 2, ry + (rh - 9) / 2, rc | alpha << 24);
      if (!this.compactLayout() || h >= 150) {
         g.m_280430_(this.f_96547_, Component.m_237113_("Key: " + opt.key), x + p, y + h - 9 - 4, 5592405 | alpha << 24);
      }
   }

   private int detailDescriptionLines(int detailHeight) {
      if (!this.compactLayout()) {
         return Integer.MAX_VALUE;
      } else {
         int lineStep = 9 + 1;
         int fixedHeight = this.layoutPad() * 2 + 9 + 20 + Math.min(34, 9 + 12) + 6 + Math.min(18, 9 + 4) + 4;
         int available = Math.max(lineStep, detailHeight - fixedHeight);
         return Math.max(1, Math.min(3, available / lineStep));
      }
   }

   private static boolean isEffectBloom(DMConfigScreen.ConfigOption option) {
      return "effeks.bloom_post_processing".equals(option.key);
   }

   public boolean m_7933_(int k, int s, int m) {
      if (Screen.m_96637_() && k == 70) {
         this.editingNumeric = null;
         this.numericInputBuf = "";
         if (this.searchBox != null) {
            this.m_7522_(this.searchBox);
         }

         return true;
      } else if (this.searchBox != null && this.searchBox.m_93696_()) {
         if (k == 256) {
            this.m_7522_(null);
            return true;
         } else {
            return super.m_7933_(k, s, m);
         }
      } else {
         if (this.editingNumeric != null) {
            if (k == 256) {
               this.editingNumeric = null;
               this.numericInputBuf = "";
               return true;
            }

            if (k == 257 || k == 335) {
               this.commitNumericInput();
               return true;
            }

            if (k == 259 && !this.numericInputBuf.isEmpty()) {
               this.numericInputBuf = this.numericInputBuf.substring(0, this.numericInputBuf.length() - 1);
               return true;
            }

            if (k == 46 && !this.numericInputBuf.contains(".")) {
               this.numericInputBuf = this.numericInputBuf + ".";
               return true;
            }
         }

         if (k == 256) {
            this.m_7379_();
            return true;
         } else {
            DMConfigScreen.ConfigOption sel = this.selectedOpt();
            if (sel != null && (k == 257 || k == 32)) {
               if (sel.isBool()) {
                  sel.toggle(false);
                  this.refreshPresetStatus(sel);
               }

               return true;
            } else {
               return super.m_7933_(k, s, m);
            }
         }
      }
   }

   public boolean m_5534_(char c, int modifiers) {
      if (this.searchBox != null && this.searchBox.m_93696_()) {
         return super.m_5534_(c, modifiers);
      } else if (this.editingNumeric != null && c >= '0' && c <= '9') {
         this.numericInputBuf = this.numericInputBuf + c;
         return true;
      } else {
         return super.m_5534_(c, modifiers);
      }
   }

   private void commitNumericInput() {
      if (this.editingNumeric != null) {
         try {
            double val = Double.parseDouble(this.numericInputBuf);
            if (this.editingNumeric.spec instanceof DoubleValue dv) {
               dv.set(Math.max(0.0, Math.min(1.0, val / 100.0)));
            } else if (this.editingNumeric.spec instanceof IntValue iv) {
               iv.set((int)Math.round(val));
            }
         } catch (NumberFormatException var6) {
         }

         this.editingNumeric = null;
         this.numericInputBuf = "";
         this.clickSound();
      }
   }

   private static String formatNumericInput(DMConfigScreen.ConfigOption opt) {
      if (opt.spec instanceof DoubleValue dv) {
         return String.valueOf(Math.round((Double)dv.get() * 100.0));
      } else {
         return opt.spec instanceof IntValue iv ? String.valueOf(iv.get()) : opt.spec.get().toString();
      }
   }

   public boolean m_6375_(double mx, double my, int btn) {
      if (btn == 0 && this.isInsideInstantJceButton(mx, my)) {
         if (InstantJudgementCutEndClientState.isKnown() && InstantJudgementCutEndClientState.isLearned() && !InstantJudgementCutEndClientState.isPending()) {
            InstantJudgementCutEndClientState.toggle();
            this.clickSound();
         }

         return true;
      } else if (btn == 0 && this.isInsideVergilStatusButton(mx, my)) {
         this.clickSound();
         this.f_96541_.m_91152_(new VergilStatusConfigScreen(this));
         return true;
      } else {
         if (btn == 0 && this.searchBox != null) {
            boolean insideSearch = mx >= (double)this.searchX()
               && mx <= (double)(this.searchX() + this.searchWidth())
               && my >= (double)this.presetTop()
               && my <= (double)(this.presetTop() + this.presetHeight());
            if (insideSearch) {
               this.m_7522_(this.searchBox);
               this.searchBox.m_6375_(mx, my, btn);
               return true;
            }

            if (this.m_7222_() == this.searchBox) {
               this.m_7522_(null);
            } else {
               this.searchBox.m_93692_(false);
            }
         }

         if (this.clickEffectPreset(mx, my, btn)) {
            return true;
         } else {
            if (btn == 0) {
               for (int i = 0; i < this.tabCount(); i++) {
                  int tx = this.tabX(i);
                  int ty = this.tabY(i);
                  if (mx >= (double)tx && mx <= (double)(tx + this.tabWidth()) && my >= (double)ty && my <= (double)(ty + this.tabHeight())) {
                     this.selectCategory(i);
                     return true;
                  }
               }
            }

            if (super.m_6375_(mx, my, btn)) {
               return true;
            } else {
               DMConfigScreen.ConfigOption sel = this.selectedOpt();
               if (sel == null) {
                  return false;
               } else {
                  int dx = this.detailX();
                  int dw = this.detailW();
                  int p = this.layoutPad();
                  int lTop = this.contentTop();
                  int cx = dx + p;
                  int cy = lTop + p + 9 + 20 + this.lastDescExtraHeight;
                  int cw = dw - p * 2;
                  int ch = Math.min(34, 9 + 12);
                  if (btn == 0 && mx >= (double)cx && mx <= (double)(cx + cw) && my >= (double)cy && my <= (double)(cy + ch)) {
                     if (!sel.isAvailable()) {
                        return true;
                     }

                     if (sel.isBool()) {
                        sel.toggle(false);
                        this.refreshPresetStatus(sel);
                        this.clickSound();
                        return true;
                     }

                     if (sel.isEnum()) {
                        String vs = sel.displayVal();
                        String left = "◀ ";
                        String right = " ▶";
                        int lw = this.f_96547_.m_92895_(left);
                        int rw = this.f_96547_.m_92895_(right);
                        int vw = this.f_96547_.m_92895_(vs);
                        int rightEdge = cx + cw - 8;
                        int leftEdge = rightEdge - lw - vw - rw;
                        if (mx >= (double)leftEdge && mx <= (double)(leftEdge + lw)) {
                           sel.toggle(false);
                           this.clickSound();
                           return true;
                        }

                        if (mx >= (double)(leftEdge + lw + vw) && mx <= (double)rightEdge) {
                           sel.toggle(true);
                           this.clickSound();
                           return true;
                        }

                        return true;
                     }

                     if (sel.isNum()) {
                        boolean editing = this.editingNumeric == sel;
                        String vsx = editing ? (this.numericInputBuf.isEmpty() ? "0" : this.numericInputBuf) : sel.displayVal();
                        String minus = " - ";
                        String plus = " + ";
                        int mw = this.f_96547_.m_92895_(minus);
                        int vwx = this.f_96547_.m_92895_(vsx);
                        int pw = this.f_96547_.m_92895_(plus);
                        int rightEdgex = cx + cw - 8;
                        int leftEdgex = rightEdgex - mw - vwx - pw;
                        if (!editing && mx >= (double)leftEdgex && mx <= (double)(leftEdgex + mw)) {
                           sel.toggle(false);
                           this.clickSound();
                           return true;
                        }

                        if (!editing && mx >= (double)(leftEdgex + mw + vwx) && mx <= (double)rightEdgex) {
                           sel.toggle(true);
                           this.clickSound();
                           return true;
                        }

                        if (mx >= (double)(leftEdgex + mw) && mx <= (double)(leftEdgex + mw + vwx)) {
                           this.editingNumeric = sel;
                           this.numericInputBuf = formatNumericInput(sel);
                           this.clickSound();
                           return true;
                        }

                        return true;
                     }
                  }

                  int rwx = Math.min(80, dw / 4);
                  int rh = Math.min(18, 9 + 4);
                  int rx = dx + dw - rwx - p;
                  int ry = cy + ch + 6;
                  if (btn == 0 && mx >= (double)rx && mx <= (double)(rx + rwx) && my >= (double)ry && my <= (double)(ry + rh) && !sel.isDefault()) {
                     sel.reset();
                     this.refreshPresetStatus(sel);
                     this.clickSound();
                     return true;
                  } else {
                     return false;
                  }
               }
            }
         }
      }
   }

   private void selectCategory(int index) {
      this.activeCategory = index;
      this.selectedIndex = 0;
      this.editingNumeric = null;
      this.numericInputBuf = "";
      this.refreshList();
      this.clickSound();
   }

   public boolean m_6050_(double mx, double my, double d) {
      if (this.configList.m_5953_(mx, my)) {
         this.configList.applyScroll(d);
         return true;
      } else {
         return super.m_6050_(mx, my, d);
      }
   }

   private void clickSound() {
      if (this.f_96541_ != null && this.f_96541_.f_91074_ != null) {
         this.f_96541_.m_91106_().m_120367_(SimpleSoundInstance.m_263171_(SoundEvents.f_12490_, 1.0F));
      }
   }

   private int drawMultiline(GuiGraphics g, Component text, int x, int y, int maxW, int color, int maxLines) {
      String raw = text.getString();
      int cy = y;
      int lc = 0;
      boolean limitReached = false;

      for (String paragraph : raw.split("\n", -1)) {
         if (limitReached) {
            break;
         }

         if (paragraph.isEmpty()) {
            if (lc >= maxLines) {
               break;
            }

            cy += 9 + 1;
            lc++;
         } else {
            for (FormattedCharSequence line : this.f_96547_.m_92923_(Component.m_237113_(paragraph), maxW)) {
               if (lc >= maxLines) {
                  limitReached = true;
                  break;
               }

               g.m_280649_(this.f_96547_, line, x, cy, color, true);
               cy += 9 + 1;
               lc++;
            }
         }
      }

      return Math.max(0, (lc - 1) * (9 + 1));
   }

   private void drawFrame(GuiGraphics g, int x, int y, int w, int h, float a, int accent) {
      int r = x + w;
      int b = y + h;
      g.m_280509_(x, y, r, b, (int)(51.0F * a) << 24);
      int bd = (int)(85.0F * a) << 24 | 8947848;
      g.m_280509_(x - 1, y - 1, r + 1, y, bd);
      g.m_280509_(x - 1, b, r + 1, b + 1, bd);
      g.m_280509_(x - 1, y, x, b, bd);
      g.m_280509_(r, y, r + 1, b, bd);
      g.m_280509_(x, y, x + 2, b, (int)(187.0F * a) << 24 | accent);
   }

   private void drawBorder(GuiGraphics g, int x, int y, int w, int h, int c) {
      g.m_280509_(x - 1, y - 1, x + w + 1, y, c);
      g.m_280509_(x - 1, y + h, x + w + 1, y + h + 1, c);
      g.m_280509_(x - 1, y, x, y + h, c);
      g.m_280509_(x + w, y, x + w + 1, y + h, c);
   }

   static {
      List<DMConfigScreen.ConfigOption> sdt = new ArrayList<>();
      sdt.add(opt("sdt.weapon_renderer", "config.invincible_dmc.sdt_weapon_renderer", DMConfig.SDT_WEAPON_RENDERER));
      sdt.add(opt("sdt.player_renderer", "config.invincible_dmc.sdt_player_renderer", DMConfig.SDT_PLAYER_RENDERER));
      sdt.add(opt("sdt.charge_weapon_swap", "config.invincible_dmc.sdt_charge_weapon_swap", DMConfig.SDT_CHARGE_WEAPON_SWAP));
      sdt.add(opt("sdt.afterimage", "config.invincible_dmc.sdt_afterimage", DMConfig.SDT_AFTERIMAGE));
      sdt.add(opt("render.model_face_culling", "config.invincible_dmc.model_face_culling", DMConfig.MODEL_FACE_CULLING));
      sdt.add(opt("render.doppel_model", "config.invincible_dmc.doppel_model", DMConfig.DOPPEL_MODEL));
      sdt.add(opt("render.doppel_weapon_strategy", "config.invincible_dmc.doppel_weapon_strategy", DMConfig.DOPPEL_WEAPON_STRATEGY));
      sdt.add(opt("render.doppel_silhouette", "config.invincible_dmc.doppel_silhouette", DMConfig.DOPPEL_SILHOUETTE));
      sdt.add(opt("render.doppel_silhouette_emissive", "config.invincible_dmc.doppel_silhouette_emissive", DMConfig.DOPPEL_SILHOUETTE_EMISSIVE));
      sdt.add(opt("doppel.mirror_control_enabled", "config.invincible_dmc.doppel_mirror_control_enabled", DMConfig.DOPPEL_MIRROR_CONTROL_ENABLED));
      List<DMConfigScreen.ConfigOption> render = new ArrayList<>();
      render.add(opt("torso_storage.enabled", "config.invincible_dmc.torso_storage_enabled", DMConfig.TORSO_STORAGE_ENABLED));
      render.add(opt("render.hide_ui_during_jce", "config.invincible_dmc.hide_ui_during_jce", DMConfig.HIDE_UI_DURING_JCE));
      render.add(opt("render.cinematic_bars_enabled", "config.invincible_dmc.cinematic_bars_enabled", DMConfig.CINEMATIC_BARS_ENABLED));
      render.add(
         opt("camera_shake.yamato_judgement_cut_prev", "config.invincible_dmc.camera_shake.yamato_judgement_cut_prev", DMConfig.YAMATO_JC_PREV_CAMERA_SHAKE)
      );
      render.add(
         opt("camera_shake.yamato_judgement_cut_prev2", "config.invincible_dmc.camera_shake.yamato_judgement_cut_prev2", DMConfig.YAMATO_JC_PREV2_CAMERA_SHAKE)
      );
      render.add(
         opt(
            "camera_shake.yamato_judgement_cut_execution",
            "config.invincible_dmc.camera_shake.yamato_judgement_cut_execution",
            DMConfig.YAMATO_JC_EXECUTION_CAMERA_SHAKE
         )
      );
      render.add(opt("camera_shake.yamato_sdt_enter", "config.invincible_dmc.camera_shake.yamato_sdt_enter", DMConfig.YAMATO_SDT_CAMERA_SHAKE));
      render.add(opt("camera_shake.hit_entity", "config.invincible_dmc.camera_shake.hit_entity", DMConfig.HIT_ENTITY_CAMERA_SHAKE));
      render.add(
         opt(
            "yamato.judgement_cut_end_camera_enabled",
            "config.invincible_dmc.judgement_cut_end_camera_enabled",
            YamatoClientConfig.JUDGEMENT_CUT_END_CAMERA_ENABLED
         )
      );
      render.add(opt("render.yamato_dmc5_bd_pbr", "config.invincible_dmc.yamato_dmc5_bd_pbr", DMConfig.YAMATO_DMC5_BD_PBR));
      render.add(
         linkedOpt(
            "render.yamato_dmc5_bd_pbr_without_shader_pack",
            "config.invincible_dmc.yamato_dmc5_bd_pbr_without_shader_pack",
            DMConfig.YAMATO_DMC5_BD_PBR_WITHOUT_SHADER_PACK,
            DMConfig.YAMATO_DMC5_BD_PBR::get
         )
      );
      render.add(opt("render.summoned_sword_shader", "config.invincible_dmc.summoned_sword_shader", DMConfig.SUMMONED_SWORD_SHADER));
      render.add(opt("render.yamato_model_outline", "config.invincible_dmc.yamato_model_outline", DMConfig.YAMATO_MODEL_OUTLINE));
      render.add(opt("yamato_bloom_trail", "config.invincible_dmc.yamato_bloom_trail", DMConfig.YAMATO_BLOOM_TRAIL));
      render.add(opt("air_trail", "config.invincible_dmc.air_trail", DMConfig.AIR_TRAIL));
      render.add(opt("flowing_trail", "config.invincible_dmc.flowing_trail", DMConfig.FLOWING_TRAIL));
      List<DMConfigScreen.ConfigOption> vfx = new ArrayList<>(render);
      vfx.add(opt("render.vix.black_white_flash", "config.invincible_dmc.vix.black_white_flash", DMConfig.VIX_BLACK_WHITE_FLASH));
      vfx.add(opt("render.vix.cold_gray", "config.invincible_dmc.vix.cold_gray", DMConfig.VIX_COLD_GRAY));
      vfx.add(opt("render.vix.color_radial_blur", "config.invincible_dmc.vix.color_radial_blur", DMConfig.VIX_COLOR_RADIAL_BLUR));
      vfx.add(opt("render.vix.impact_blur", "config.invincible_dmc.vix.impact_blur", DMConfig.VIX_IMPACT_BLUR));
      vfx.add(opt("render.vix.pure_chromatic_aberration", "config.invincible_dmc.vix.pure_chromatic_aberration", DMConfig.VIX_PURE_CHROMATIC_ABERRATION));
      vfx.add(opt("render.vix.screen_distortion", "config.invincible_dmc.vix.screen_distortion", DMConfig.VIX_SCREEN_DISTORTION));
      vfx.add(opt("render.vix.screen_flash", "config.invincible_dmc.vix.screen_flash", DMConfig.VIX_SCREEN_FLASH));
      vfx.add(opt("render.vix.screen_vignette", "config.invincible_dmc.vix.screen_vignette", DMConfig.VIX_SCREEN_VIGNETTE));
      vfx.add(opt("render.vix.particle_bloom", "config.invincible_dmc.vix.particle_bloom", DMConfig.VIX_PARTICLE_BLOOM));
      vfx.add(
         opt("render.vix.particle_chromatic_aberration", "config.invincible_dmc.vix.particle_chromatic_aberration", DMConfig.VIX_PARTICLE_CHROMATIC_ABERRATION)
      );
      vfx.add(
         opt(
            "render.vix.particle_chromatic_aberration_enhanced",
            "config.invincible_dmc.vix.particle_chromatic_aberration_enhanced",
            DMConfig.VIX_PARTICLE_CHROMATIC_ABERRATION_ENHANCED
         )
      );
      vfx.add(opt("render.vix.particle_edge_glow", "config.invincible_dmc.vix.particle_edge_glow", DMConfig.VIX_PARTICLE_EDGE_GLOW));
      vfx.add(opt("render.vix.space_broken", "config.invincible_dmc.vix.space_broken", DMConfig.VIX_SPACE_BROKEN));
      vfx.add(opt("effeks.bloom_post_processing", "config.invincible_dmc.effeks.bloom_post_processing", DMConfig.AAA_EFFECT_BLOOM));
      vfx.add(opt("effeks.spark_bloom_post_processing", "config.invincible_dmc.effeks.spark_bloom_post_processing", DMConfig.AAA_EFFECT_SPARK_BLOOM));
      vfx.add(
         opt("effeks.sdt_spark_bloom_post_processing", "config.invincible_dmc.effeks.sdt_spark_bloom_post_processing", DMConfig.AAA_EFFECT_SDT_SPARK_BLOOM)
      );
      vfx.add(
         opt(
            "effeks.chromatic_aberration_post_processing",
            "config.invincible_dmc.effeks.chromatic_aberration_post_processing",
            DMConfig.AAA_EFFECT_CHROMATIC_ABERRATION
         )
      );
      vfx.add(opt("space_broken.shrink_enabled", "config.invincible_dmc.shrink_enabled", DMConfig.SPACE_BROKEN_SHRINK_ENABLED));
      vfx.add(opt("space_broken.shrink_start", "config.invincible_dmc.shrink_start", DMConfig.SPACE_BROKEN_SHRINK_START));
      vfx.add(opt("space_broken.shrink_end", "config.invincible_dmc.shrink_end", DMConfig.SPACE_BROKEN_SHRINK_END));
      vfx.add(
         opt(
            "yamato.judgement_cut_blade_trail_quality",
            "config.invincible_dmc.judgement_cut_blade_trail_quality",
            YamatoClientConfig.JUDGEMENT_CUT_BLADE_TRAIL_QUALITY
         )
      );
      vfx.add(opt("yamato.judgement_cut_sequence_style", "config.invincible_dmc.judgement_cut_sequence_style", YamatoClientConfig.JUDGEMENT_CUT_SEQUENCE_STYLE));
      vfx.add(opt("yamato.portal_particle_style", "config.invincible_dmc.portal_particle_style", YamatoClientConfig.PORTAL_PARTICLE_STYLE));
      vfx.add(opt("yamato.combo_slash_style", "config.invincible_dmc.combo_slash_style", YamatoClientConfig.COMBO_SLASH_STYLE));
      EffekConfig.ENABLED.forEach((k, spec) -> {
         vfx.add(opt("effeks." + k, "config.invincible_dmc.effeks." + k, spec));
         if ("flash_point".equals(k)) {
            vfx.add(opt("flash_point.scale_factor", "config.invincible_dmc.effeks.flash_point.scale_factor", DMConfig.FLASH_POINT_SCALE_FACTOR));
         }
      });
      addCategory(
         "performance_compatibility",
         concatOptions(
            selectOptions(sdt, "render.doppel_model", "sdt.charge_weapon_swap", "sdt.player_renderer"),
            selectOptions(vfx, "render.yamato_model_outline", "render.summoned_sword_shader", "effeks.demonic_domain", "effeks.bloom_post_processing")
         )
      );
      addCategory(
         "model_rendering",
         concatOptions(
            selectOptions(
               sdt,
               "sdt.weapon_renderer",
               "sdt.player_renderer",
               "sdt.charge_weapon_swap",
               "render.doppel_model",
               "render.doppel_weapon_strategy",
               "render.doppel_silhouette",
               "render.doppel_silhouette_emissive"
            ),
            selectOptions(
               vfx,
               "torso_storage.enabled",
               "render.yamato_model_outline",
               "render.summoned_sword_shader",
               "render.yamato_dmc5_bd_pbr_without_shader_pack",
               "render.yamato_dmc5_bd_pbr"
            )
         )
      );
      addCategory(
         "camera_effects",
         selectOptions(
            vfx,
            "render.cinematic_bars_enabled",
            "yamato.judgement_cut_end_camera_enabled",
            "render.hide_ui_during_jce",
            "camera_shake.yamato_judgement_cut_prev",
            "camera_shake.yamato_judgement_cut_prev2",
            "camera_shake.yamato_judgement_cut_execution",
            "camera_shake.yamato_sdt_enter",
            "camera_shake.hit_entity",
            "render.vix.black_white_flash",
            "render.vix.cold_gray",
            "render.vix.color_radial_blur",
            "render.vix.impact_blur",
            "render.vix.pure_chromatic_aberration",
            "render.vix.screen_distortion",
            "render.vix.screen_flash",
            "render.vix.screen_vignette"
         )
      );
      addCategory("basic_effects", selectOptions(vfx, "yamato_bloom_trail", "air_trail", "flowing_trail"));
      addCategory(
         "advanced_effects",
         selectOptions(
            vfx,
            "yamato.judgement_cut_sequence_style",
            "yamato.portal_particle_style",
            "yamato.combo_slash_style",
            "yamato.judgement_cut_blade_trail_quality",
            "effeks.demonic_domain",
            "effeks.bloom_post_processing"
         )
      );
      addCategory(
         "post_processing",
         selectOptions(
            vfx,
            "render.vix.particle_bloom",
            "render.vix.particle_chromatic_aberration",
            "render.vix.particle_chromatic_aberration_enhanced",
            "render.vix.particle_edge_glow",
            "render.vix.space_broken",
            "effeks.spark_bloom_post_processing",
            "effeks.sdt_spark_bloom_post_processing",
            "effeks.chromatic_aberration_post_processing",
            "space_broken.shrink_enabled",
            "space_broken.shrink_start",
            "space_broken.shrink_end"
         )
      );
      addCategory(
         "effeks_yamato",
         selectOptions(
            vfx,
            "effeks.tier0_slash",
            "effeks.door",
            "effeks.flash",
            "effeks.flash_small",
            "effeks.sheath",
            "effeks.ground",
            "effeks.spark",
            "effeks.jce_fire",
            "effeks.judgement_cut",
            "effeks.jce_disorder",
            "effeks.light_slash",
            "effeks.light_ring",
            "effeks.tier1plus_slash",
            "effeks.rush",
            "effeks.rush_disorder",
            "effeks.dance_b",
            "effeks.dance_b_disorder",
            "effeks.parry",
            "effeks.shock_wave",
            "effeks.void_slash",
            "effeks.meteor",
            "effeks.attack",
            "effeks.execute",
            "effeks.flash_point",
            "flash_point.scale_factor",
            "effeks.dirt_2",
            "effeks.stone_2",
            "effeks.power_floor"
         )
      );
      addCategory(
         "effeks_sdt",
         concatOptions(
            selectOptions(sdt, "sdt.afterimage"),
            selectOptions(
               vfx,
               "effeks.sdt1_done",
               "effeks.sdt1_charge",
               "effeks.sdt2_done",
               "effeks.sdt_fire1",
               "effeks.sdt_fire2",
               "effeks.sdt_spark",
               "effeks.sdt",
               "effeks.sdt_mini",
               "effeks.sdt_out"
            )
         )
      );
      List<DMConfigScreen.ConfigOption> aaapPerformance = new ArrayList<>();
      aaapPerformance.add(aaapOpt("enabled", AAAPPerformanceClientConfig.ENABLED));
      aaapPerformance.add(aaapLinkedOpt("limit_emitter_count", AAAPPerformanceClientConfig.LIMIT_EMITTER_COUNT, AAAPPerformanceClientConfig.ENABLED::get));
      aaapPerformance.add(aaapLinkedOpt("limit_instance_count", AAAPPerformanceClientConfig.LIMIT_INSTANCE_COUNT, AAAPPerformanceClientConfig.ENABLED::get));
      aaapPerformance.add(
         aaapLinkedOpt(
            "reserve_burst_instances",
            AAAPPerformanceClientConfig.RESERVE_BURST_INSTANCES,
            () -> (Boolean)AAAPPerformanceClientConfig.ENABLED.get()
                  && ((Boolean)AAAPPerformanceClientConfig.LIMIT_INSTANCE_COUNT.get() || (Boolean)AAAPPerformanceClientConfig.LIMIT_JCE_BURSTS.get())
         )
      );
      aaapPerformance.add(aaapLinkedOpt("enable_soft_budgets", AAAPPerformanceClientConfig.ENABLE_SOFT_BUDGETS, AAAPPerformanceClientConfig.ENABLED::get));
      aaapPerformance.add(
         aaapLinkedOpt(
            "limit_frame_additions",
            AAAPPerformanceClientConfig.LIMIT_FRAME_ADDITIONS,
            () -> (Boolean)AAAPPerformanceClientConfig.ENABLED.get() && (Boolean)AAAPPerformanceClientConfig.ENABLE_SOFT_BUDGETS.get()
         )
      );
      aaapPerformance.add(aaapLinkedOpt("normalize_time_budgets", AAAPPerformanceClientConfig.NORMALIZE_TIME_BUDGETS, AAAPPerformanceClientConfig.ENABLED::get));
      aaapPerformance.add(aaapLinkedOpt("limit_simulation_rate", AAAPPerformanceClientConfig.LIMIT_SIMULATION_RATE, AAAPPerformanceClientConfig.ENABLED::get));
      aaapPerformance.add(
         aaapLinkedOpt(
            "simulation_rate_limit_hz",
            AAAPPerformanceClientConfig.SIMULATION_RATE_LIMIT_HZ,
            () -> (Boolean)AAAPPerformanceClientConfig.ENABLED.get() && (Boolean)AAAPPerformanceClientConfig.LIMIT_SIMULATION_RATE.get()
         )
      );
      aaapPerformance.add(
         aaapLinkedOpt("limit_collision_raycasts", AAAPPerformanceClientConfig.LIMIT_COLLISION_RAYCASTS, AAAPPerformanceClientConfig.ENABLED::get)
      );
      aaapPerformance.add(aaapLinkedOpt("limit_jce_bursts", AAAPPerformanceClientConfig.LIMIT_JCE_BURSTS, AAAPPerformanceClientConfig.ENABLED::get));
      aaapPerformance.add(aaapLinkedOpt("limit_spark_effects", AAAPPerformanceClientConfig.LIMIT_SPARK_EFFECTS, AAAPPerformanceClientConfig.ENABLED::get));
      aaapPerformance.add(
         aaapLinkedOpt(
            "disable_vix_post_processing_during_jce",
            AAAPPerformanceClientConfig.DISABLE_VIX_POST_PROCESSING_DURING_JCE,
            AAAPPerformanceClientConfig.ENABLED::get
         )
      );
      aaapPerformance.add(
         aaapLinkedOpt("adaptive_vix_single_pass", AAAPPerformanceClientConfig.ADAPTIVE_VIX_SINGLE_PASS, AAAPPerformanceClientConfig.ENABLED::get)
      );
      aaapPerformance.add(
         aaapLinkedOpt("use_static_yamato_last_sphere", AAAPPerformanceClientConfig.USE_STATIC_YAMATO_LAST_SPHERE, AAAPPerformanceClientConfig.ENABLED::get)
      );
      aaapPerformance.add(
         aaapLinkedOpt(
            "max_world_emitters",
            AAAPPerformanceClientConfig.MAX_WORLD_EMITTERS,
            () -> (Boolean)AAAPPerformanceClientConfig.ENABLED.get() && (Boolean)AAAPPerformanceClientConfig.LIMIT_EMITTER_COUNT.get()
         )
      );
      aaapPerformance.add(
         aaapLinkedOpt(
            "max_world_instances",
            AAAPPerformanceClientConfig.MAX_WORLD_INSTANCES,
            () -> (Boolean)AAAPPerformanceClientConfig.ENABLED.get()
                  && ((Boolean)AAAPPerformanceClientConfig.LIMIT_INSTANCE_COUNT.get() || (Boolean)AAAPPerformanceClientConfig.LIMIT_JCE_BURSTS.get())
         )
      );
      aaapPerformance.add(
         aaapLinkedOpt(
            "soft_world_emitters",
            AAAPPerformanceClientConfig.SOFT_WORLD_EMITTERS,
            () -> (Boolean)AAAPPerformanceClientConfig.ENABLED.get() && (Boolean)AAAPPerformanceClientConfig.ENABLE_SOFT_BUDGETS.get()
         )
      );
      aaapPerformance.add(
         aaapLinkedOpt(
            "soft_world_instances",
            AAAPPerformanceClientConfig.SOFT_WORLD_INSTANCES,
            () -> (Boolean)AAAPPerformanceClientConfig.ENABLED.get() && (Boolean)AAAPPerformanceClientConfig.ENABLE_SOFT_BUDGETS.get()
         )
      );
      aaapPerformance.add(
         aaapLinkedOpt(
            "world_instance_reservation_per_emitter",
            AAAPPerformanceClientConfig.WORLD_INSTANCE_RESERVATION_PER_EMITTER,
            () -> (Boolean)AAAPPerformanceClientConfig.ENABLED.get()
                  && (Boolean)AAAPPerformanceClientConfig.RESERVE_BURST_INSTANCES.get()
                  && ((Boolean)AAAPPerformanceClientConfig.LIMIT_INSTANCE_COUNT.get() || (Boolean)AAAPPerformanceClientConfig.LIMIT_JCE_BURSTS.get())
         )
      );
      aaapPerformance.add(
         aaapLinkedOpt(
            "max_jce_world_emitters",
            AAAPPerformanceClientConfig.MAX_JCE_WORLD_EMITTERS,
            () -> (Boolean)AAAPPerformanceClientConfig.ENABLED.get() && (Boolean)AAAPPerformanceClientConfig.LIMIT_JCE_BURSTS.get()
         )
      );
      aaapPerformance.add(
         aaapLinkedOpt(
            "max_jce_world_instances",
            AAAPPerformanceClientConfig.MAX_JCE_WORLD_INSTANCES,
            () -> (Boolean)AAAPPerformanceClientConfig.ENABLED.get() && (Boolean)AAAPPerformanceClientConfig.LIMIT_JCE_BURSTS.get()
         )
      );
      aaapPerformance.add(
         aaapLinkedOpt(
            "soft_jce_world_emitters",
            AAAPPerformanceClientConfig.SOFT_JCE_WORLD_EMITTERS,
            () -> (Boolean)AAAPPerformanceClientConfig.ENABLED.get()
                  && (Boolean)AAAPPerformanceClientConfig.ENABLE_SOFT_BUDGETS.get()
                  && (Boolean)AAAPPerformanceClientConfig.LIMIT_JCE_BURSTS.get()
         )
      );
      aaapPerformance.add(
         aaapLinkedOpt(
            "soft_jce_world_instances",
            AAAPPerformanceClientConfig.SOFT_JCE_WORLD_INSTANCES,
            () -> (Boolean)AAAPPerformanceClientConfig.ENABLED.get()
                  && (Boolean)AAAPPerformanceClientConfig.ENABLE_SOFT_BUDGETS.get()
                  && (Boolean)AAAPPerformanceClientConfig.LIMIT_JCE_BURSTS.get()
         )
      );
      aaapPerformance.add(
         aaapLinkedOpt(
            "max_world_emitter_starts_per_frame",
            AAAPPerformanceClientConfig.MAX_WORLD_EMITTER_STARTS_PER_FRAME,
            () -> (Boolean)AAAPPerformanceClientConfig.ENABLED.get()
                  && (Boolean)AAAPPerformanceClientConfig.ENABLE_SOFT_BUDGETS.get()
                  && (Boolean)AAAPPerformanceClientConfig.LIMIT_FRAME_ADDITIONS.get()
         )
      );
      aaapPerformance.add(
         aaapLinkedOpt(
            "max_world_new_instances_per_frame",
            AAAPPerformanceClientConfig.MAX_WORLD_NEW_INSTANCES_PER_FRAME,
            () -> (Boolean)AAAPPerformanceClientConfig.ENABLED.get()
                  && (Boolean)AAAPPerformanceClientConfig.ENABLE_SOFT_BUDGETS.get()
                  && (Boolean)AAAPPerformanceClientConfig.LIMIT_FRAME_ADDITIONS.get()
         )
      );
      aaapPerformance.add(
         aaapLinkedOpt(
            "vix_single_pass_instance_threshold",
            AAAPPerformanceClientConfig.VIX_SINGLE_PASS_INSTANCE_THRESHOLD,
            () -> (Boolean)AAAPPerformanceClientConfig.ENABLED.get() && (Boolean)AAAPPerformanceClientConfig.ADAPTIVE_VIX_SINGLE_PASS.get()
         )
      );
      aaapPerformance.add(
         aaapLinkedOpt(
            "max_spark_emitters",
            AAAPPerformanceClientConfig.MAX_SPARK_EMITTERS,
            () -> (Boolean)AAAPPerformanceClientConfig.ENABLED.get() && (Boolean)AAAPPerformanceClientConfig.LIMIT_SPARK_EFFECTS.get()
         )
      );
      aaapPerformance.add(
         aaapLinkedOpt(
            "max_sdt_spark_emitters",
            AAAPPerformanceClientConfig.MAX_SDT_SPARK_EMITTERS,
            () -> (Boolean)AAAPPerformanceClientConfig.ENABLED.get() && (Boolean)AAAPPerformanceClientConfig.LIMIT_SPARK_EFFECTS.get()
         )
      );
      aaapPerformance.add(
         aaapLinkedOpt(
            "max_spark_starts_per_frame",
            AAAPPerformanceClientConfig.MAX_SPARK_STARTS_PER_FRAME,
            () -> (Boolean)AAAPPerformanceClientConfig.ENABLED.get() && (Boolean)AAAPPerformanceClientConfig.LIMIT_SPARK_EFFECTS.get()
         )
      );
      aaapPerformance.add(
         aaapLinkedOpt(
            "max_hand_emitters",
            AAAPPerformanceClientConfig.MAX_HAND_EMITTERS,
            () -> (Boolean)AAAPPerformanceClientConfig.ENABLED.get() && (Boolean)AAAPPerformanceClientConfig.LIMIT_EMITTER_COUNT.get()
         )
      );
      aaapPerformance.add(
         aaapLinkedOpt(
            "max_hand_instances",
            AAAPPerformanceClientConfig.MAX_HAND_INSTANCES,
            () -> (Boolean)AAAPPerformanceClientConfig.ENABLED.get() && (Boolean)AAAPPerformanceClientConfig.LIMIT_INSTANCE_COUNT.get()
         )
      );
      aaapPerformance.add(
         aaapLinkedOpt(
            "soft_hand_emitters",
            AAAPPerformanceClientConfig.SOFT_HAND_EMITTERS,
            () -> (Boolean)AAAPPerformanceClientConfig.ENABLED.get() && (Boolean)AAAPPerformanceClientConfig.ENABLE_SOFT_BUDGETS.get()
         )
      );
      aaapPerformance.add(
         aaapLinkedOpt(
            "soft_hand_instances",
            AAAPPerformanceClientConfig.SOFT_HAND_INSTANCES,
            () -> (Boolean)AAAPPerformanceClientConfig.ENABLED.get() && (Boolean)AAAPPerformanceClientConfig.ENABLE_SOFT_BUDGETS.get()
         )
      );
      aaapPerformance.add(
         aaapLinkedOpt(
            "hand_instance_reservation_per_emitter",
            AAAPPerformanceClientConfig.HAND_INSTANCE_RESERVATION_PER_EMITTER,
            () -> (Boolean)AAAPPerformanceClientConfig.ENABLED.get()
                  && (Boolean)AAAPPerformanceClientConfig.RESERVE_BURST_INSTANCES.get()
                  && (Boolean)AAAPPerformanceClientConfig.LIMIT_INSTANCE_COUNT.get()
         )
      );
      aaapPerformance.add(
         aaapLinkedOpt(
            "max_hand_emitter_starts_per_frame",
            AAAPPerformanceClientConfig.MAX_HAND_EMITTER_STARTS_PER_FRAME,
            () -> (Boolean)AAAPPerformanceClientConfig.ENABLED.get()
                  && (Boolean)AAAPPerformanceClientConfig.ENABLE_SOFT_BUDGETS.get()
                  && (Boolean)AAAPPerformanceClientConfig.LIMIT_FRAME_ADDITIONS.get()
         )
      );
      aaapPerformance.add(
         aaapLinkedOpt(
            "max_hand_new_instances_per_frame",
            AAAPPerformanceClientConfig.MAX_HAND_NEW_INSTANCES_PER_FRAME,
            () -> (Boolean)AAAPPerformanceClientConfig.ENABLED.get()
                  && (Boolean)AAAPPerformanceClientConfig.ENABLE_SOFT_BUDGETS.get()
                  && (Boolean)AAAPPerformanceClientConfig.LIMIT_FRAME_ADDITIONS.get()
         )
      );
      aaapPerformance.add(
         aaapLinkedOpt(
            "collision_raycasts_per_frame",
            AAAPPerformanceClientConfig.COLLISION_RAYCASTS_PER_FRAME,
            () -> (Boolean)AAAPPerformanceClientConfig.ENABLED.get() && (Boolean)AAAPPerformanceClientConfig.LIMIT_COLLISION_RAYCASTS.get()
         )
      );
      aaapPerformance.add(aaapOpt("log_statistics", AAAPPerformanceClientConfig.LOG_STATISTICS));
      addCategory("aaap_performance", List.copyOf(aaapPerformance));
      addUniqueOptions(ALL_ITEMS, sdt);
      addUniqueOptions(ALL_ITEMS, render);
      addUniqueOptions(ALL_ITEMS, vfx);
      addUniqueOptions(ALL_ITEMS, aaapPerformance);
   }

   static record Category(String transKey, String filterKey, List<DMConfigScreen.ConfigOption> items) {
   }

   private class ConfigList extends ObjectSelectionList<DMConfigScreen.ConfigList.Row> {
      double targetScroll;
      float slidingIndex = -1.0F;

      ConfigList(Minecraft mc, int w, int h, int top, int bot, int itemH) {
         super(mc, w, h, top, bot, itemH);
         this.targetScroll = this.m_93517_();
      }

      void applyScroll(double d) {
         this.targetScroll = Math.max(0.0, Math.min(this.targetScroll - d * (double)this.f_93387_, (double)this.m_93518_()));
      }

      void updateSmoothScroll() {
         double c = this.m_93517_();
         if (Math.abs(this.targetScroll - c) > 0.5) {
            this.m_93410_((double)DMConfigScreen.this.lerp((float)c, (float)this.targetScroll, 0.55F));
         } else {
            this.m_93410_(this.targetScroll);
         }
      }

      void rebuild(List<DMConfigScreen.ConfigOption> items) {
         this.m_93516_();

         for (DMConfigScreen.ConfigOption item : items) {
            this.m_7085_(new DMConfigScreen.ConfigList.Row(item));
         }

         this.targetScroll = 0.0;
         this.m_93410_(0.0);
         this.slidingIndex = (float)DMConfigScreen.this.selectedIndex;
      }

      public int m_5759_() {
         return this.f_93388_;
      }

      protected int m_5756_() {
         return this.f_93388_ + 9999;
      }

      public void setSelected(@Nullable DMConfigScreen.ConfigList.Row e) {
         super.m_6987_(e);
         if (e != null) {
            DMConfigScreen.this.selectedIndex = this.m_6702_().indexOf(e);
         }
      }

      public void m_88315_(@NotNull GuiGraphics g, int mx, int my, float pt) {
         if (this.slidingIndex < 0.0F) {
            this.slidingIndex = (float)DMConfigScreen.this.selectedIndex;
         }

         this.slidingIndex = DMConfigScreen.this.lerp(this.slidingIndex, (float)DMConfigScreen.this.selectedIndex, 0.55F);
         g.m_280588_(this.f_93393_, this.f_93390_, this.f_93392_, this.f_93391_);
         int ba = (int)(45.0F * DMConfigScreen.this.transitionAlpha);
         int aa = (int)(255.0F * DMConfigScreen.this.transitionAlpha);
         if (ba > 0 && !this.m_6702_().isEmpty()) {
            int si = (int)this.m_93517_();
            int hy = (int)((float)(this.f_93390_ + 2 - si) + this.slidingIndex * (float)this.f_93387_);
            DMConfigScreen.ConfigOption selected = DMConfigScreen.this.selectedOpt();
            boolean effectBloom = selected != null && DMConfigScreen.isEffectBloom(selected);
            g.m_280509_(this.f_93393_ + 4, hy + 1, this.f_93392_ - 6, hy + this.f_93387_ - 1, ba << 24 | (effectBloom ? 5910528 : 16777215));
            g.m_280509_(this.f_93393_ + 4, hy + 1, this.f_93393_ + 6, hy + this.f_93387_ - 1, aa << 24 | (effectBloom ? 16752412 : '\ue5ff'));
         }

         g.m_280618_();
         super.m_88315_(g, mx, my, pt);
      }

      class Row extends Entry<DMConfigScreen.ConfigList.Row> {
         final DMConfigScreen.ConfigOption opt;
         private float hoverAnim;
         private float selectAnim;

         Row(DMConfigScreen.ConfigOption opt) {
            this.opt = opt;
         }

         public void m_6311_(GuiGraphics g, int idx, int top, int left, int w, int h, int mx, int my, boolean hover, float pt) {
            boolean isSel = DMConfigScreen.this.selectedIndex == idx;
            this.hoverAnim = DMConfigScreen.this.step(this.hoverAnim, hover ? 1.0F : 0.0F, 24.0F);
            this.selectAnim = DMConfigScreen.this.step(this.selectAnim, isSel ? 1.0F : 0.0F, 24.0F);
            float eh = 1.0F - (float)Math.pow((double)(1.0F - this.hoverAnim), 3.0);
            float es = 1.0F - (float)Math.pow((double)(1.0F - this.selectAnim), 3.0);
            boolean effectBloom = DMConfigScreen.isEffectBloom(this.opt);
            if (effectBloom && !isSel) {
               g.m_280509_(
                  ConfigList.this.f_93393_ + 4,
                  top + 1,
                  ConfigList.this.f_93392_ - 6,
                  top + h - 1,
                  (int)((28.0F + eh * 18.0F) * DMConfigScreen.this.transitionAlpha) << 24 | 5910528
               );
               g.m_280509_(
                  ConfigList.this.f_93393_ + 4,
                  top + 1,
                  ConfigList.this.f_93393_ + 6,
                  top + h - 1,
                  (int)(180.0F * DMConfigScreen.this.transitionAlpha) << 24 | 16752412
               );
            } else if (eh > 0.01F && !isSel) {
               g.m_280509_(
                  ConfigList.this.f_93393_ + 4,
                  top + 1,
                  ConfigList.this.f_93392_ - 6,
                  top + h - 1,
                  (int)(eh * 30.0F * DMConfigScreen.this.transitionAlpha) << 24 | 16777215
               );
            }

            int a = (int)(255.0F * DMConfigScreen.this.transitionAlpha);
            if (a > 8) {
               String nm = Component.m_237115_(this.opt.description).getString();
               if (nm.equals(this.opt.description)) {
                  nm = this.opt.key;
               }

               String value = this.opt.displayVal();
               int valueX = ConfigList.this.f_93392_ - 6 - DMConfigScreen.this.f_96547_.m_92895_(value);
               int mw = Math.max(12, valueX - (ConfigList.this.f_93393_ + 10) - 6);
               if (DMConfigScreen.this.f_96547_.m_92895_(nm) > mw) {
                  nm = DMConfigScreen.this.f_96547_.m_92834_(nm, mw - DMConfigScreen.this.f_96547_.m_92895_("...")) + "...";
               }

               int fc = (!this.opt.isAvailable() ? 6710886 : (effectBloom ? (isSel ? 16752412 : 16765066) : (isSel ? '\ue5ff' : (hover ? 16777215 : 11184810))))
                  | a << 24;
               g.m_280056_(DMConfigScreen.this.f_96547_, nm, ConfigList.this.f_93393_ + 8 + (isSel ? 4 : 0), top + (h - 9) / 2, fc, false);
               g.m_280056_(DMConfigScreen.this.f_96547_, value, valueX, top + (h - 9) / 2, this.opt.stateColor(a), false);
            }
         }

         public boolean m_6375_(double mx, double my, int btn) {
            if (btn == 0) {
               ConfigList.this.setSelected(this);
               DMConfigScreen.this.clickSound();
               return true;
            } else {
               return false;
            }
         }

         @NotNull
         public Component m_142172_() {
            return Component.m_237113_(this.opt.key);
         }
      }
   }

   static record ConfigOption(String key, String description, ConfigValue<?> spec, BooleanSupplier enabledCondition) {
      boolean isAvailable() {
         return this.enabledCondition.getAsBoolean();
      }

      boolean isBool() {
         return this.spec instanceof BooleanValue;
      }

      boolean isNum() {
         return this.spec instanceof DoubleValue || this.spec instanceof IntValue;
      }

      boolean isEnum() {
         return this.spec instanceof EnumValue;
      }

      void setRaw(Object v) {
         this.spec.set(v);
      }

      String displayVal() {
         if (this.isBool()) {
            return ((BooleanValue)this.spec).get() ? "ON" : "OFF";
         } else if (this.isEnum()) {
            if (this.spec.get() instanceof Enum<?> e) {
               String translationKey = this.description + ".value." + e.name().toLowerCase(Locale.ROOT);
               String translated = Component.m_237115_(translationKey).getString();
               return translated.equals(translationKey) ? e.name() : translated;
            } else {
               return "??";
            }
         } else if (this.spec instanceof DoubleValue dv) {
            return String.format("%.0f%%", (Double)dv.get() * 100.0);
         } else {
            return this.spec instanceof IntValue iv ? String.valueOf(iv.get()) : "??";
         }
      }

      int stateColor(int alpha) {
         if (!this.isAvailable()) {
            return 7829367 | alpha << 24;
         } else {
            return this.isBool() ? (((BooleanValue)this.spec).get() ? 65416 : 16729156) | alpha << 24 : 58879 | alpha << 24;
         }
      }

      void toggle(boolean positive) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.struct.gen.VarType.isGeneric()" because "newRet" is null
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.getInferredExprType(InvocationExprent.java:634)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.getInferredExprType(FunctionExprent.java:243)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:966)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.AssignmentExprent.toJava(AssignmentExprent.java:154)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.listToJava(ExprProcessor.java:895)
         //   at org.jetbrains.java.decompiler.modules.decompiler.stats.BasicBlockStatement.toJava(BasicBlockStatement.java:90)
         //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:203)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
         //   at org.jetbrains.java.decompiler.modules.decompiler.stats.SequenceStatement.toJava(SequenceStatement.java:107)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
         //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:241)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
         //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:254)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
         //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:241)
         //   at org.jetbrains.java.decompiler.modules.decompiler.stats.RootStatement.toJava(RootStatement.java:36)
         //   at org.jetbrains.java.decompiler.main.ClassWriter.writeMethod(ClassWriter.java:1283)
         //
         // Bytecode:
         // 000: aload 0
         // 001: invokevirtual com/dmc/invincible_dmc/client/gui/DMConfigScreen$ConfigOption.isAvailable ()Z
         // 004: ifne 008
         // 007: return
         // 008: aload 0
         // 009: invokevirtual com/dmc/invincible_dmc/client/gui/DMConfigScreen$ConfigOption.isBool ()Z
         // 00c: ifeq 02f
         // 00f: aload 0
         // 010: aload 0
         // 011: getfield com/dmc/invincible_dmc/client/gui/DMConfigScreen$ConfigOption.spec Lnet/minecraftforge/common/ForgeConfigSpec$ConfigValue;
         // 014: checkcast net/minecraftforge/common/ForgeConfigSpec$BooleanValue
         // 017: invokevirtual net/minecraftforge/common/ForgeConfigSpec$BooleanValue.get ()Ljava/lang/Object;
         // 01a: checkcast java/lang/Boolean
         // 01d: invokevirtual java/lang/Boolean.booleanValue ()Z
         // 020: ifne 027
         // 023: bipush 1
         // 024: goto 028
         // 027: bipush 0
         // 028: invokestatic java/lang/Boolean.valueOf (Z)Ljava/lang/Boolean;
         // 02b: invokevirtual com/dmc/invincible_dmc/client/gui/DMConfigScreen$ConfigOption.setRaw (Ljava/lang/Object;)V
         // 02e: return
         // 02f: aload 0
         // 030: invokevirtual com/dmc/invincible_dmc/client/gui/DMConfigScreen$ConfigOption.isEnum ()Z
         // 033: ifeq 07d
         // 036: aload 0
         // 037: getfield com/dmc/invincible_dmc/client/gui/DMConfigScreen$ConfigOption.spec Lnet/minecraftforge/common/ForgeConfigSpec$ConfigValue;
         // 03a: checkcast net/minecraftforge/common/ForgeConfigSpec$EnumValue
         // 03d: astore 2
         // 03e: aload 2
         // 03f: invokevirtual net/minecraftforge/common/ForgeConfigSpec$EnumValue.get ()Ljava/lang/Object;
         // 042: checkcast java/lang/Enum
         // 045: invokevirtual java/lang/Object.getClass ()Ljava/lang/Class;
         // 048: invokevirtual java/lang/Class.getEnumConstants ()[Ljava/lang/Object;
         // 04b: checkcast [Ljava/lang/Enum;
         // 04e: astore 3
         // 04f: aload 3
         // 050: ifnull 07c
         // 053: aload 3
         // 054: arraylength
         // 055: ifle 07c
         // 058: aload 2
         // 059: invokevirtual net/minecraftforge/common/ForgeConfigSpec$EnumValue.get ()Ljava/lang/Object;
         // 05c: checkcast java/lang/Enum
         // 05f: invokevirtual java/lang/Enum.ordinal ()I
         // 062: iload 1
         // 063: ifeq 06a
         // 066: bipush 1
         // 067: goto 06e
         // 06a: aload 3
         // 06b: arraylength
         // 06c: bipush 1
         // 06d: isub
         // 06e: iadd
         // 06f: aload 3
         // 070: arraylength
         // 071: irem
         // 072: istore 4
         // 074: aload 0
         // 075: aload 3
         // 076: iload 4
         // 078: aaload
         // 079: invokevirtual com/dmc/invincible_dmc/client/gui/DMConfigScreen$ConfigOption.setRaw (Ljava/lang/Object;)V
         // 07c: return
         // 07d: aload 0
         // 07e: getfield com/dmc/invincible_dmc/client/gui/DMConfigScreen$ConfigOption.spec Lnet/minecraftforge/common/ForgeConfigSpec$ConfigValue;
         // 081: astore 4
         // 083: aload 4
         // 085: instanceof net/minecraftforge/common/ForgeConfigSpec$DoubleValue
         // 088: ifeq 0d1
         // 08b: aload 4
         // 08d: checkcast net/minecraftforge/common/ForgeConfigSpec$DoubleValue
         // 090: astore 2
         // 091: aload 2
         // 092: invokevirtual net/minecraftforge/common/ForgeConfigSpec$DoubleValue.get ()Ljava/lang/Object;
         // 095: checkcast java/lang/Double
         // 098: invokevirtual java/lang/Double.doubleValue ()D
         // 09b: dstore 4
         // 09d: ldc2_w 0.01
         // 0a0: dstore 6
         // 0a2: aload 2
         // 0a3: dconst_0
         // 0a4: dconst_1
         // 0a5: iload 1
         // 0a6: ifeq 0b1
         // 0a9: dload 4
         // 0ab: dload 6
         // 0ad: dadd
         // 0ae: goto 0b6
         // 0b1: dload 4
         // 0b3: dload 6
         // 0b5: dsub
         // 0b6: ldc2_w 100.0
         // 0b9: dmul
         // 0ba: invokestatic java/lang/Math.round (D)J
         // 0bd: l2d
         // 0be: ldc2_w 100.0
         // 0c1: ddiv
         // 0c2: invokestatic java/lang/Math.min (DD)D
         // 0c5: invokestatic java/lang/Math.max (DD)D
         // 0c8: invokestatic java/lang/Double.valueOf (D)Ljava/lang/Double;
         // 0cb: invokevirtual net/minecraftforge/common/ForgeConfigSpec$DoubleValue.set (Ljava/lang/Object;)V
         // 0ce: goto 100
         // 0d1: aload 0
         // 0d2: getfield com/dmc/invincible_dmc/client/gui/DMConfigScreen$ConfigOption.spec Lnet/minecraftforge/common/ForgeConfigSpec$ConfigValue;
         // 0d5: astore 4
         // 0d7: aload 4
         // 0d9: instanceof net/minecraftforge/common/ForgeConfigSpec$IntValue
         // 0dc: ifeq 100
         // 0df: aload 4
         // 0e1: checkcast net/minecraftforge/common/ForgeConfigSpec$IntValue
         // 0e4: astore 3
         // 0e5: aload 3
         // 0e6: aload 3
         // 0e7: invokevirtual net/minecraftforge/common/ForgeConfigSpec$IntValue.get ()Ljava/lang/Object;
         // 0ea: checkcast java/lang/Integer
         // 0ed: invokevirtual java/lang/Integer.intValue ()I
         // 0f0: iload 1
         // 0f1: ifeq 0f8
         // 0f4: bipush 1
         // 0f5: goto 0f9
         // 0f8: bipush -1
         // 0f9: iadd
         // 0fa: invokestatic java/lang/Integer.valueOf (I)Ljava/lang/Integer;
         // 0fd: invokevirtual net/minecraftforge/common/ForgeConfigSpec$IntValue.set (Ljava/lang/Object;)V
         // 100: return
      }

      boolean isDefault() {
         return this.spec.get().equals(this.spec.getDefault());
      }

      void reset() {
         this.setRaw(this.spec.getDefault());
      }
   }

   private static record SearchResult(DMConfigScreen.ConfigOption option, int score) {
   }
}
