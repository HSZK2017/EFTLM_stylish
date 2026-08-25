package com.dmc.invincible_dmc.client.gui;

import com.dmc.invincible_dmc.compat.ftbchunks.FTBChunksCompat;
import com.dmc.invincible_dmc.compat.journeymap.JourneyMapCompat;
import com.dmc.invincible_dmc.compat.xaero.XaeroMinimapCompat;
import com.dmc.invincible_dmc.entity.portal.PortalDestinationType;
import com.dmc.invincible_dmc.network.DMCNetwork;
import com.dmc.invincible_dmc.network.client.CPPortalDestinationChosen;
import com.dmc.invincible_dmc.network.server.S2CPortalDestinationsPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class PortalDestinationScreen extends Screen {
   private static final int COLOR_TEXT_MAIN = 16777215;
   private static final int COLOR_TEXT_SUB = 11189247;
   private static final int COLOR_TEXT_DIM = 7833770;
   private static final int COLOR_ACCENT_PURPLE = 8008703;
   private static final int COLOR_ACCENT_BLUE = 3368703;
   private static final int COLOR_DARK_BLUE = 1118515;
   private static final int COLOR_BORDER = 2241382;
   private static final int COLOR_WARNING = 13387007;
   private static final int MAX_HORIZONTAL_COORDINATE = 30000000;
   private static final int CURRENT_X_MASK = 1;
   private static final int CURRENT_Y_MASK = 2;
   private static final int CURRENT_Z_MASK = 4;
   private final List<PortalDestinationScreen.DestinationEntry> allDestinations;
   private final int portalId;
   private final ResourceKey<Level> currentDimension;
   private PortalDestinationScreen.DestinationListWidget listWidget;
   public int selectedIndex = 0;
   private EditBox customXInput;
   private EditBox customYInput;
   private EditBox customZInput;
   private boolean customInputInvalid = false;
   private float transitionAlpha = 0.0F;
   private long lastRenderTime = 0L;
   private float dt = 0.0F;
   private float confirmBtnHover = 0.0F;
   private float cancelBtnHover = 0.0F;

   public static void openOrAutoResolve(S2CPortalDestinationsPacket packet) {
      Minecraft mc = Minecraft.m_91087_();
      List<XaeroMinimapCompat.XaeroWaypointData> xaeroWaypoints = List.of();
      if (ModList.get().isLoaded("xaerominimap")) {
         xaeroWaypoints = XaeroMinimapCompat.getWaypoints();
      }

      List<FTBChunksCompat.FTBWaypointData> ftbWaypoints = List.of();
      if (ModList.get().isLoaded("ftbchunks")) {
         ftbWaypoints = FTBChunksCompat.getWaypoints();
      }

      List<JourneyMapCompat.JMWaypointData> jmWaypoints = List.of();
      if (ModList.get().isLoaded("journeymap")) {
         jmWaypoints = JourneyMapCompat.getWaypoints();
      }

      List<PortalDestinationScreen.DestinationEntry> allDestinations = new ArrayList<>();
      ResourceKey<Level> currentDim = packet.currentDimension();
      if (packet.hasRespawnPoint() && packet.respawnPos() != null) {
         allDestinations.add(
            new PortalDestinationScreen.DestinationEntry(
               PortalDestinationType.PLAYER_SPAWN,
               Component.m_237115_("gui.invincible_dmc.portal.player_spawn"),
               packet.respawnDimension(),
               packet.respawnPos(),
               null,
               16777215
            )
         );
      }

      allDestinations.add(
         new PortalDestinationScreen.DestinationEntry(
            PortalDestinationType.WORLD_SPAWN,
            Component.m_237115_("gui.invincible_dmc.portal.world_spawn"),
            Level.f_46428_,
            packet.worldSpawnPos(),
            null,
            11189247
         )
      );
      allDestinations.add(
         new PortalDestinationScreen.DestinationEntry(
            PortalDestinationType.CUSTOM_COORDINATES,
            Component.m_237115_("gui.invincible_dmc.portal.custom_coordinates"),
            currentDim,
            BlockPos.f_121853_,
            null,
            8008703
         )
      );

      for (S2CPortalDestinationsPacket.WaystoneEntry ws : packet.waystones()) {
         allDestinations.add(
            new PortalDestinationScreen.DestinationEntry(
               PortalDestinationType.WAYSTONE, Component.m_237113_(ws.name()), ws.dimension(), ws.pos(), ws.uid(), ws.isGlobal() ? 8008703 : 3368703
            )
         );
      }

      for (XaeroMinimapCompat.XaeroWaypointData wp : xaeroWaypoints) {
         if (wp.dimension().equals(currentDim)) {
            allDestinations.add(
               new PortalDestinationScreen.DestinationEntry(
                  PortalDestinationType.XAERO_WAYPOINT, Component.m_237113_(wp.name()), wp.dimension(), wp.getPos(), null, 2241382
               )
            );
         }
      }

      for (FTBChunksCompat.FTBWaypointData wpx : ftbWaypoints) {
         allDestinations.add(
            new PortalDestinationScreen.DestinationEntry(
               PortalDestinationType.FTB_CHUNKS, Component.m_237113_(wpx.name()), wpx.dimension(), wpx.getPos(), null, 3368703
            )
         );
      }

      for (JourneyMapCompat.JMWaypointData wpx : jmWaypoints) {
         allDestinations.add(
            new PortalDestinationScreen.DestinationEntry(
               PortalDestinationType.JOURNEYMAP, Component.m_237113_(wpx.name()), wpx.dimension(), wpx.getPos(), null, 8008703
            )
         );
      }

      allDestinations.add(
         new PortalDestinationScreen.DestinationEntry(
            PortalDestinationType.VOID,
            Component.m_237115_("gui.invincible_dmc.portal.void"),
            ResourceKey.m_135785_(Registries.f_256858_, ResourceLocation.fromNamespaceAndPath("invincible_dmc", "void")),
            new BlockPos(0, 1, 0),
            null,
            65535
         )
      );
      mc.m_91152_(new PortalDestinationScreen(packet, allDestinations));
   }

   public static String formatDimensionName(ResourceKey<Level> dim) {
      if (dim == null) {
         return Component.m_237115_("gui.invincible_dmc.portal.dimension.unknown").getString();
      } else {
         ResourceLocation loc = dim.m_135782_();
         String path = loc.m_135815_();

         return switch (path) {
            case "overworld" -> Component.m_237115_("gui.invincible_dmc.portal.dimension.overworld").getString();
            case "the_nether" -> Component.m_237115_("gui.invincible_dmc.portal.dimension.the_nether").getString();
            case "the_end" -> Component.m_237115_("gui.invincible_dmc.portal.dimension.the_end").getString();
            default -> "minecraft".equals(loc.m_135827_())
            ? path.substring(0, 1).toUpperCase() + path.substring(1).replace('_', ' ')
            : loc.m_135827_() + ":" + path;
         };
      }
   }

   public static int getColor(int rgb, float alpha) {
      int a = Math.max(0, Math.min(255, (int)(alpha * 255.0F)));
      return a << 24 | rgb & 16777215;
   }

   public float lerp(float current, float target, float speedAt60Fps) {
      float factor = 1.0F - (float)Math.pow(1.0 - (double)speedAt60Fps, (double)(this.dt * 60.0F));
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

   public PortalDestinationScreen(S2CPortalDestinationsPacket serverData, List<PortalDestinationScreen.DestinationEntry> allDestinations) {
      super(Component.m_237115_("gui.invincible_dmc.portal.title"));
      this.allDestinations = allDestinations;
      this.portalId = serverData.portalId();
      this.currentDimension = serverData.currentDimension();
   }

   protected void m_7856_() {
      super.m_7856_();
      this.lastRenderTime = 0L;
      String previousX = this.customXInput == null ? "" : this.customXInput.m_94155_();
      String previousY = this.customYInput == null ? "" : this.customYInput.m_94155_();
      String previousZ = this.customZInput == null ? "" : this.customZInput.m_94155_();
      int margin = Math.max(10, this.f_96543_ / 40);
      int listWidth = Math.max(140, Math.min(300, (int)((double)this.f_96543_ * 0.35)));
      int frameY = margin + 25;
      int frameHeight = this.f_96544_ - frameY - margin;
      int itemHeight = Math.max(22, Math.min(32, frameHeight / 8));
      this.listWidget = new PortalDestinationScreen.DestinationListWidget(this.f_96541_, listWidth, this.f_96544_, frameY, frameY + frameHeight, itemHeight);
      this.listWidget.m_93507_(margin);
      this.listWidget.m_93488_(false);
      this.listWidget.m_93496_(false);
      this.listWidget.m_93471_(false);
      this.m_7787_(this.listWidget);
      if (!this.listWidget.m_6702_().isEmpty()) {
         this.listWidget.setSelected((PortalDestinationScreen.DestinationListWidget.Entry)this.listWidget.m_6702_().get(0));
      }

      this.customXInput = this.createCoordinateInput("gui.invincible_dmc.portal.coordinate_x", previousX);
      this.customYInput = this.createCoordinateInput("gui.invincible_dmc.portal.coordinate_y", previousY);
      this.customZInput = this.createCoordinateInput("gui.invincible_dmc.portal.coordinate_z", previousZ);
      this.setCustomCoordinateInputsVisible(false);
   }

   private EditBox createCoordinateInput(String narrationKey, String initialValue) {
      EditBox input = new EditBox(this.f_96547_, 0, 0, 40, 18, Component.m_237115_(narrationKey));
      input.m_94199_(11);
      input.m_94153_(PortalDestinationScreen::isCoordinateInputAllowed);
      input.m_94144_(initialValue);
      input.m_94151_(value -> this.customInputInvalid = false);
      input.m_94202_(16777215);
      return (EditBox)this.m_142416_(input);
   }

   private static boolean isCoordinateInputAllowed(String value) {
      if (!value.isEmpty() && !value.equals("~") && !value.equals("-")) {
         int start = value.charAt(0) == '-' ? 1 : 0;
         if (start == value.length()) {
            return false;
         } else {
            for (int index = start; index < value.length(); index++) {
               if (!Character.isDigit(value.charAt(index))) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return true;
      }
   }

   private void playUiClickSound() {
      if (this.f_96541_ != null) {
         this.f_96541_.m_91106_().m_120367_(SimpleSoundInstance.m_263171_(SoundEvents.f_12490_, 1.0F));
      }
   }

   private void executeActionAndClose(boolean confirm) {
      if (confirm && this.selectedIndex >= 0 && this.selectedIndex < this.allDestinations.size()) {
         PortalDestinationScreen.DestinationEntry selectedEntry = this.allDestinations.get(this.selectedIndex);
         if (selectedEntry.type == PortalDestinationType.CUSTOM_COORDINATES) {
            PortalDestinationScreen.ParsedCustomCoordinates parsedCoordinates = this.parseCustomCoordinates();
            if (parsedCoordinates == null) {
               this.customInputInvalid = true;
               return;
            }

            DMCNetwork.sendToServer(
               new CPPortalDestinationChosen(
                  selectedEntry.type, null, this.currentDimension, parsedCoordinates.pos(), parsedCoordinates.currentAxisMask(), this.portalId
               )
            );
         } else {
            DMCNetwork.sendToServer(
               new CPPortalDestinationChosen(selectedEntry.type, selectedEntry.waystoneUid, selectedEntry.dimension, selectedEntry.pos, this.portalId)
            );
         }
      } else {
         DMCNetwork.sendToServer(new CPPortalDestinationChosen(PortalDestinationType.CANCEL, null, Level.f_46428_, BlockPos.f_121853_, this.portalId));
      }

      if (this.f_96541_ != null) {
         this.f_96541_.m_91152_(null);
      }
   }

   @Nullable
   private PortalDestinationScreen.ParsedCustomCoordinates parseCustomCoordinates() {
      PortalDestinationScreen.ParsedCoordinate x = parseCoordinate(this.customXInput.m_94155_());
      PortalDestinationScreen.ParsedCoordinate y = parseCoordinate(this.customYInput.m_94155_());
      PortalDestinationScreen.ParsedCoordinate z = parseCoordinate(this.customZInput.m_94155_());
      if (x != null && y != null && z != null) {
         if (!x.current() && Math.abs((long)x.value()) > 30000000L) {
            return null;
         } else if (!z.current() && Math.abs((long)z.value()) > 30000000L) {
            return null;
         } else {
            int currentAxisMask = 0;
            if (x.current()) {
               currentAxisMask |= 1;
            }

            if (y.current()) {
               currentAxisMask |= 2;
            }

            if (z.current()) {
               currentAxisMask |= 4;
            }

            return new PortalDestinationScreen.ParsedCustomCoordinates(new BlockPos(x.value(), y.value(), z.value()), currentAxisMask);
         }
      } else {
         return null;
      }
   }

   @Nullable
   private static PortalDestinationScreen.ParsedCoordinate parseCoordinate(String value) {
      if (value.equals("~")) {
         return new PortalDestinationScreen.ParsedCoordinate(0, true);
      } else if (!value.isEmpty() && !value.equals("-")) {
         try {
            return new PortalDestinationScreen.ParsedCoordinate(Integer.parseInt(value), false);
         } catch (NumberFormatException var2) {
            return null;
         }
      } else {
         return null;
      }
   }

   public void m_7379_() {
      this.executeActionAndClose(false);
   }

   private void drawCustomFrame(GuiGraphics graphics, int x, int y, int width, int height, float alpha) {
      int right = x + width;
      int bottom = y + height;
      graphics.m_280509_(x, y, right, bottom, getColor(1118515, alpha * 0.35F));
      int borderColor = getColor(2241382, alpha * 0.8F);
      graphics.m_280509_(x - 1, y - 1, right + 1, y, borderColor);
      graphics.m_280509_(x - 1, bottom, right + 1, bottom + 1, borderColor);
      graphics.m_280509_(x - 1, y, x, bottom, borderColor);
      graphics.m_280509_(right, y, right + 1, bottom, borderColor);
      int accentColor = getColor(8008703, alpha);
      graphics.m_280509_(x, y, x + 2, bottom, accentColor);
      graphics.m_280509_(x, y, x + 15, y + 2, accentColor);
      graphics.m_280509_(x, bottom - 2, x + 15, bottom, accentColor);
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

      this.transitionAlpha = this.lerp(this.transitionAlpha, 1.0F, 0.15F);
      float easeProgress = (float)(1.0 - Math.pow((double)(1.0F - this.transitionAlpha), 3.0));
      float slideOffset = (1.0F - easeProgress) * 50.0F;
      graphics.m_280509_(0, 0, this.f_96543_, this.f_96544_, getColor(328970, this.transitionAlpha * 0.85F));
      int margin = Math.max(10, this.f_96543_ / 40);
      int frameY = margin + 25;
      int frameHeight = this.f_96544_ - frameY - margin;
      if (this.transitionAlpha > 0.05F) {
         graphics.m_280614_(this.f_96547_, this.f_96539_, margin, margin, getColor(16777215, this.transitionAlpha), true);
         Component curDim = Component.m_237110_(
            "gui.invincible_dmc.portal.current_dimension", new Object[]{formatDimensionName(this.currentDimension).toUpperCase()}
         );
         graphics.m_280614_(
            this.f_96547_, curDim, this.f_96543_ - this.f_96547_.m_92852_(curDim) - margin, margin, getColor(11189247, this.transitionAlpha), true
         );
      }

      int listWidth = this.listWidget.getWidth();
      int listCurrentX = margin - (int)slideOffset;
      int detailX = listCurrentX + listWidth + margin;
      int detailWidth = this.f_96543_ - margin - (margin + listWidth + margin);
      this.listWidget.m_93507_(listCurrentX);
      this.listWidget.updateSmoothScroll();
      this.listWidget.m_88315_(graphics, mouseX, mouseY, partialTick);
      this.drawCustomFrame(graphics, listCurrentX, frameY, listWidth, frameHeight, this.transitionAlpha);
      this.drawCustomFrame(graphics, detailX, frameY, detailWidth, frameHeight, this.transitionAlpha);
      this.setCustomCoordinateInputsVisible(false);
      if (this.selectedIndex >= 0 && this.selectedIndex < this.allDestinations.size()) {
         this.renderDetailPanel(graphics, detailX, frameY, detailWidth, frameHeight, mouseX, mouseY, partialTick, this.transitionAlpha);
      }
   }

   private void renderDetailPanel(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick, float alpha) {
      PortalDestinationScreen.DestinationEntry dest = this.allDestinations.get(this.selectedIndex);

      Component typeLabel = switch (dest.type) {
         case PLAYER_SPAWN -> Component.m_237115_("gui.invincible_dmc.portal.type.respawn_point");
         case WORLD_SPAWN -> Component.m_237115_("gui.invincible_dmc.portal.type.world_spawn");
         case WAYSTONE -> Component.m_237115_("gui.invincible_dmc.portal.type.waystone");
         case VOID -> Component.m_237115_("gui.invincible_dmc.portal.type.void");
         case CUSTOM_COORDINATES -> Component.m_237115_("gui.invincible_dmc.portal.type.custom_coordinates");
         case XAERO_WAYPOINT, FTB_CHUNKS, JOURNEYMAP -> Component.m_237115_("gui.invincible_dmc.portal.type.modded_waypoint");
         default -> Component.m_237115_("gui.invincible_dmc.portal.type.unknown");
      };
      int innerPad = Math.max(8, width / 20);
      int currentY = y + innerPad;
      graphics.m_280168_().m_85836_();
      graphics.m_280168_().m_252880_((float)(x + innerPad), (float)currentY, 0.0F);
      float titleScale = width < 200 ? 1.0F : 1.3F;
      graphics.m_280168_().m_85841_(titleScale, titleScale, 1.0F);
      graphics.m_280614_(this.f_96547_, typeLabel, 0, 0, getColor(16777215, alpha), true);
      graphics.m_280168_().m_85849_();
      currentY += (int)(9.0F * titleScale) + 5;
      graphics.m_280024_(x + innerPad, currentY, x + innerPad + (int)((float)width * 0.6F), currentY + 1, getColor(11189247, alpha), getColor(11189247, 0.0F));
      currentY += 8;
      graphics.m_280614_(this.f_96547_, dest.name, x + innerPad, currentY, getColor(16777215, alpha), true);
      int btnH = Math.max(20, Math.min(26, height / 10));
      int btnW = Math.min(120, (width - innerPad * 3) / 2);
      int btnY = y + height - btnH - innerPad;
      currentY += 20;
      int cardH = Math.min(60, btnY - currentY - 10);
      if (cardH >= 25) {
         graphics.m_280509_(x + innerPad, currentY, x + width - innerPad, currentY + cardH, getColor(1118515, alpha * 0.6F));
         graphics.m_280509_(x + innerPad, currentY, x + innerPad + 2, currentY + cardH, getColor(dest.color, alpha));
         if (dest.type == PortalDestinationType.CUSTOM_COORDINATES) {
            this.renderCustomCoordinateCard(graphics, x + innerPad + 10, currentY, width - innerPad * 2 - 20, cardH, mouseX, mouseY, partialTick);
         } else {
            int textDrawY = currentY + Math.max(2, (cardH - (9 * 2 + 5)) / 2);
            Component coordText = Component.m_237110_(
               "gui.invincible_dmc.portal.coordinate_format",
               new Object[]{String.valueOf(dest.pos.m_123341_()), String.valueOf(dest.pos.m_123342_()), String.valueOf(dest.pos.m_123343_())}
            );
            graphics.m_280430_(this.f_96547_, coordText, x + innerPad + 10, textDrawY, getColor(16777215, alpha));
            boolean isCrossDim = !dest.dimension.equals(this.currentDimension);
            Component dimInfo = Component.m_237110_(
               "gui.invincible_dmc.portal.dimension_label", new Object[]{formatDimensionName(dest.dimension).toUpperCase()}
            );
            int dimDrawY = textDrawY + 9 + 5;
            graphics.m_280430_(this.f_96547_, dimInfo, x + innerPad + 10, dimDrawY, getColor(11189247, alpha));
            if (isCrossDim && width > 180) {
               float blink = 0.6F + 0.4F * (float)Math.sin((double)Util.m_137550_() / 150.0);
               Component distortionText = Component.m_237115_("gui.invincible_dmc.portal.distortion_warning");
               graphics.m_280614_(
                  this.f_96547_, distortionText, x + innerPad + 10 + this.f_96547_.m_92852_(dimInfo) + 10, dimDrawY, getColor(13387007, alpha * blink), true
               );
            }
         }
      }

      int confirmX = x + width - innerPad - btnW * 2 - 10;
      int cancelX = x + width - innerPad - btnW;
      boolean confirmHover = mouseX >= confirmX && mouseX <= confirmX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
      this.confirmBtnHover = this.step(this.confirmBtnHover, confirmHover ? 1.0F : 0.0F, 15.0F);
      this.drawButton(
         graphics, confirmX, btnY, btnW, btnH, Component.m_237115_("gui.invincible_dmc.portal.initiate"), confirmHover, this.confirmBtnHover, alpha, true
      );
      boolean cancelHover = mouseX >= cancelX && mouseX <= cancelX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
      this.cancelBtnHover = this.step(this.cancelBtnHover, cancelHover ? 1.0F : 0.0F, 15.0F);
      this.drawButton(
         graphics, cancelX, btnY, btnW, btnH, Component.m_237115_("gui.invincible_dmc.portal.abort"), cancelHover, this.cancelBtnHover, alpha, false
      );
   }

   private void renderCustomCoordinateCard(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
      if (height >= 32 && width >= 30) {
         boolean showMessage = height >= 50;
         if (showMessage) {
            Component message = Component.m_237115_(
               this.customInputInvalid ? "gui.invincible_dmc.portal.custom_coordinates_invalid" : "gui.invincible_dmc.portal.custom_coordinates_hint"
            );
            String visibleMessage = this.f_96547_.m_92834_(message.getString(), width);
            int messageColor = this.customInputInvalid ? 13387007 : 11189247;
            graphics.m_280488_(this.f_96547_, visibleMessage, x, y + 3, getColor(messageColor, this.transitionAlpha));
         }

         int gap = 6;
         int fieldWidth = Math.max(8, (width - gap * 2) / 3);
         int labelY = y + (showMessage ? 18 : 3);
         int inputY = labelY + 9 + 1;
         EditBox[] inputs = new EditBox[]{this.customXInput, this.customYInput, this.customZInput};
         String[] labels = new String[]{"X", "Y", "Z"};

         for (int index = 0; index < inputs.length; index++) {
            int inputX = x + index * (fieldWidth + gap);
            graphics.m_280137_(this.f_96547_, labels[index], inputX + fieldWidth / 2, labelY, getColor(11189247, this.transitionAlpha));
            EditBox input = inputs[index];
            input.m_252865_(inputX);
            input.m_253211_(inputY);
            input.m_93674_(fieldWidth);
            input.m_94202_(this.customInputInvalid ? 13387007 : 16777215);
            input.f_93624_ = true;
            input.f_93623_ = true;
            input.m_88315_(graphics, mouseX, mouseY, partialTick);
         }
      }
   }

   private void setCustomCoordinateInputsVisible(boolean visible) {
      EditBox[] inputs = new EditBox[]{this.customXInput, this.customYInput, this.customZInput};

      for (EditBox input : inputs) {
         if (input != null) {
            input.f_93624_ = visible;
            input.f_93623_ = visible;
         }
      }
   }

   private void drawButton(
      GuiGraphics graphics, int x, int y, int w, int h, Component text, boolean isHover, float hoverAnim, float globalAlpha, boolean isPrimary
   ) {
      float eased = (float)(1.0 - Math.pow(1.0 - (double)hoverAnim, 3.0));
      int baseBg = isPrimary ? 2241382 : 1118481;
      int hoverBg = isPrimary ? 8008703 : 4460834;
      int r = (int)((float)(baseBg >> 16 & 0xFF) * (1.0F - eased) + (float)(hoverBg >> 16 & 0xFF) * eased);
      int g = (int)((float)(baseBg >> 8 & 0xFF) * (1.0F - eased) + (float)(hoverBg >> 8 & 0xFF) * eased);
      int b = (int)((float)(baseBg & 0xFF) * (1.0F - eased) + (float)(hoverBg & 0xFF) * eased);
      int currentBg = r << 16 | g << 8 | b;
      graphics.m_280509_(x, y, x + w, y + h, getColor(currentBg, globalAlpha * 0.85F));
      int borderCol = getColor(isHover ? 16777215 : 7833770, globalAlpha);
      graphics.m_280509_(x - 1, y - 1, x + w + 1, y, borderCol);
      graphics.m_280509_(x - 1, y + h, x + w + 1, y + h + 1, borderCol);
      graphics.m_280509_(x - 1, y, x, y + h, borderCol);
      graphics.m_280509_(x + w, y, x + w + 1, y + h, borderCol);
      int textColor = isHover ? 16777215 : 11189247;
      graphics.m_280168_().m_85836_();
      int strWidth = this.f_96547_.m_92852_(text);
      if (strWidth > w - 4) {
         float s = ((float)w - 4.0F) / (float)strWidth;
         graphics.m_280168_().m_252880_((float)x + (float)w / 2.0F, (float)y + (float)h / 2.0F, 0.0F);
         graphics.m_280168_().m_85841_(s, s, 1.0F);
         graphics.m_280653_(this.f_96547_, text, 0, -9 / 2, getColor(textColor, globalAlpha));
      } else {
         graphics.m_280653_(this.f_96547_, text, x + w / 2, y + (h - 9) / 2, getColor(textColor, globalAlpha));
      }

      graphics.m_280168_().m_85849_();
   }

   public boolean m_6375_(double mouseX, double mouseY, int button) {
      int margin = Math.max(10, this.f_96543_ / 40);
      int listWidth = this.listWidget.getWidth();
      int detailX = margin + listWidth + margin;
      int detailWidth = this.f_96543_ - margin - detailX;
      int frameY = margin + 25;
      int frameHeight = this.f_96544_ - frameY - margin;
      int innerPad = Math.max(8, detailWidth / 20);
      int btnH = Math.max(20, Math.min(26, frameHeight / 10));
      int btnW = Math.min(120, (detailWidth - innerPad * 3) / 2);
      int btnY = frameY + frameHeight - btnH - innerPad;
      int confirmX = detailX + detailWidth - innerPad - btnW * 2 - 10;
      int cancelX = detailX + detailWidth - innerPad - btnW;
      if (button == 0) {
         if (mouseX >= (double)confirmX && mouseX <= (double)(confirmX + btnW) && mouseY >= (double)btnY && mouseY <= (double)(btnY + btnH)) {
            this.playUiClickSound();
            this.executeActionAndClose(true);
            return true;
         }

         if (mouseX >= (double)cancelX && mouseX <= (double)(cancelX + btnW) && mouseY >= (double)btnY && mouseY <= (double)(btnY + btnH)) {
            this.playUiClickSound();
            this.executeActionAndClose(false);
            return true;
         }
      }

      return this.listWidget.m_5953_(mouseX, mouseY) ? this.listWidget.m_6375_(mouseX, mouseY, button) : super.m_6375_(mouseX, mouseY, button);
   }

   public boolean m_6050_(double mouseX, double mouseY, double delta) {
      if (this.listWidget.m_5953_(mouseX, mouseY)) {
         this.listWidget.applyScroll(delta);
         return true;
      } else {
         return super.m_6050_(mouseX, mouseY, delta);
      }
   }

   public boolean m_7933_(int keyCode, int scanCode, int modifiers) {
      if (keyCode == 256) {
         this.executeActionAndClose(false);
         return true;
      } else {
         return super.m_7933_(keyCode, scanCode, modifiers);
      }
   }

   public boolean m_7043_() {
      return false;
   }

   public static class DestinationEntry {
      public final PortalDestinationType type;
      public final Component name;
      public final ResourceKey<Level> dimension;
      public final BlockPos pos;
      public final UUID waystoneUid;
      public final int color;

      public DestinationEntry(PortalDestinationType type, Component name, ResourceKey<Level> dimension, BlockPos pos, UUID waystoneUid, int color) {
         this.type = type;
         this.name = name;
         this.dimension = dimension;
         this.pos = pos;
         this.waystoneUid = waystoneUid;
         this.color = color;
      }
   }

   class DestinationListWidget extends ObjectSelectionList<PortalDestinationScreen.DestinationListWidget.Entry> {
      public double targetScroll;
      public float slidingIndex = -1.0F;

      public DestinationListWidget(Minecraft mc, int width, int height, int top, int bottom, int itemHeight) {
         super(mc, width, height, top, bottom, itemHeight);

         for (PortalDestinationScreen.DestinationEntry dest : PortalDestinationScreen.this.allDestinations) {
            this.m_7085_(new PortalDestinationScreen.DestinationListWidget.Entry(dest));
         }

         this.targetScroll = this.m_93517_();
      }

      public void m_88315_(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
         if (this.slidingIndex < 0.0F) {
            this.slidingIndex = (float)PortalDestinationScreen.this.selectedIndex;
         }

         this.slidingIndex = PortalDestinationScreen.this.lerp(this.slidingIndex, (float)PortalDestinationScreen.this.selectedIndex, 0.55F);
         graphics.m_280588_(this.f_93393_, this.f_93390_, this.f_93392_, this.f_93391_);
         int boxRight = this.f_93392_ - 6;
         if (PortalDestinationScreen.this.transitionAlpha > 0.05F && !this.m_6702_().isEmpty()) {
            int scrollInt = (int)this.m_93517_();
            int hlY = Math.round((float)(this.f_93390_ + 2 - scrollInt) + this.slidingIndex * (float)this.f_93387_);
            int boxHeight = this.f_93387_ - 4;
            graphics.m_280024_(
               this.f_93393_ + 4,
               hlY + 2,
               boxRight,
               hlY + 2 + boxHeight,
               PortalDestinationScreen.getColor(8008703, PortalDestinationScreen.this.transitionAlpha * 0.4F),
               PortalDestinationScreen.getColor(3368703, 0.0F)
            );
            graphics.m_280509_(
               this.f_93393_ + 4,
               hlY + 2,
               this.f_93393_ + 6,
               hlY + 2 + boxHeight,
               PortalDestinationScreen.getColor(16777215, PortalDestinationScreen.this.transitionAlpha)
            );
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
            this.m_93410_((double)PortalDestinationScreen.this.lerp((float)current, (float)this.targetScroll, 0.55F));
         } else {
            this.m_93410_(this.targetScroll);
         }
      }

      public int m_5759_() {
         return this.f_93388_;
      }

      protected int m_5756_() {
         return this.f_93388_ + 9999;
      }

      public void setSelected(@Nullable PortalDestinationScreen.DestinationListWidget.Entry entry) {
         super.m_6987_(entry);
         if (entry != null) {
            PortalDestinationScreen.this.selectedIndex = this.m_6702_().indexOf(entry);
         }
      }

      class Entry extends net.minecraft.client.gui.components.ObjectSelectionList.Entry<PortalDestinationScreen.DestinationListWidget.Entry> {
         final PortalDestinationScreen.DestinationEntry dest;
         private float hoverAnim = 0.0F;
         private float selectAnim = 0.0F;

         Entry(PortalDestinationScreen.DestinationEntry dest) {
            this.dest = dest;
         }

         public void m_6311_(
            GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isHovering, float partialTick
         ) {
            boolean isSelected = PortalDestinationScreen.this.selectedIndex == index;
            this.hoverAnim = PortalDestinationScreen.this.step(this.hoverAnim, isHovering ? 1.0F : 0.0F, 24.0F);
            this.selectAnim = PortalDestinationScreen.this.step(this.selectAnim, isSelected ? 1.0F : 0.0F, 24.0F);
            float easedHover = (float)(1.0 - Math.pow(1.0 - (double)this.hoverAnim, 3.0));
            float easedSelect = (float)(1.0 - Math.pow(1.0 - (double)this.selectAnim, 3.0));
            float alpha = PortalDestinationScreen.this.transitionAlpha;
            if (easedHover > 0.05F && !isSelected) {
               graphics.m_280509_(
                  DestinationListWidget.this.f_93393_ + 4,
                  top + 2,
                  DestinationListWidget.this.f_93392_ - 6,
                  top + height - 2,
                  PortalDestinationScreen.getColor(1118515, alpha * easedHover * 0.5F)
               );
            }

            graphics.m_280509_(left + 10, top + 4, left + 12, top + height - 4, PortalDestinationScreen.getColor(this.dest.color, alpha));
            float currentScale = 1.0F + 0.02F * easedHover + 0.06F * easedSelect;
            float currentOffsetX = 16.0F + 4.0F * easedHover + 8.0F * easedSelect;
            if (alpha > 0.05F) {
               int cValue = isSelected ? 16777215 : (isHovering ? 16777215 : 11189247);
               graphics.m_280168_().m_85836_();
               float textDrawY = (float)top + (float)(height - 9) / 2.0F;
               graphics.m_280168_().m_252880_((float)DestinationListWidget.this.f_93393_ + currentOffsetX, textDrawY + 9.0F / 2.0F, 0.0F);
               graphics.m_280168_().m_85841_(currentScale, currentScale, 1.0F);
               graphics.m_280614_(
                  PortalDestinationScreen.this.f_96547_,
                  this.dest.name,
                  0,
                  (int)((float)(-9) / 2.0F) - 4,
                  PortalDestinationScreen.getColor(cValue, alpha),
                  false
               );
               graphics.m_280168_().m_85841_(0.75F, 0.75F, 1.0F);
               String dimText = PortalDestinationScreen.formatDimensionName(this.dest.dimension);
               if (!this.dest.dimension.equals(PortalDestinationScreen.this.currentDimension)) {
                  dimText = dimText + " [!]";
               }

               graphics.m_280056_(PortalDestinationScreen.this.f_96547_, dimText, 0, 8, PortalDestinationScreen.getColor(7833770, alpha), false);
               graphics.m_280168_().m_85849_();
            }
         }

         public boolean m_6375_(double mouseX, double mouseY, int button) {
            if (button == 0) {
               DestinationListWidget.this.setSelected(this);
               PortalDestinationScreen.this.playUiClickSound();
               return true;
            } else {
               return false;
            }
         }

         @NotNull
         public Component m_142172_() {
            return this.dest.name;
         }
      }
   }

   private static record ParsedCoordinate(int value, boolean current) {
   }

   private static record ParsedCustomCoordinates(BlockPos pos, int currentAxisMask) {
   }
}
