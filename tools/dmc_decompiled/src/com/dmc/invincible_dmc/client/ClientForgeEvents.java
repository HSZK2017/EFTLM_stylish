package com.dmc.invincible_dmc.client;

import com.dmc.invincible_dmc.client.gui.DMCKeyBindsScreen;
import com.dmc.invincible_dmc.client.gui.DMConfigScreen;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent.Init.Post;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "invincible_dmc",
   bus = Bus.FORGE,
   value = {Dist.CLIENT}
)
public class ClientForgeEvents {
   private static final int BTN_W = 98;
   private static final int BTN_H = 20;
   private static final int PAD = 5;
   private static final String SEEN_KEYBINDS_TIP_KEY = "invincible_dmc:seen_keybinds_tip";

   @SubscribeEvent
   public static void onScreenInit(Post event) {
      Screen screen = event.getScreen();
      if (screen instanceof PauseScreen) {
         int[] pos = findStackedPosition(screen, event.getListenersList(), 2, 98, 20, 5);
         event.addListener(
            Button.m_253074_(Component.m_237115_("screen.invincible_dmc.config.pause_button"), b -> Minecraft.m_91087_().m_91152_(new DMConfigScreen(screen)))
               .m_252987_(pos[0], pos[1], 98, 20)
               .m_253136_()
         );
         event.addListener(
            Button.m_253074_(
                  Component.m_237115_("screen.invincible_dmc.keybinds.pause_button"), b -> Minecraft.m_91087_().m_91152_(new DMCKeyBindsScreen(screen))
               )
               .m_252987_(pos[0], pos[1] + 20 + 5, 98, 20)
               .m_253136_()
         );
      }
   }

   private static int[] findStackedPosition(Screen screen, List<? extends GuiEventListener> existing, int count, int w, int h, int pad) {
      int totalH = count * h + (count - 1) * pad;
      int x = 5;
      int y = screen.f_96544_ - totalH;

      while (true) {
         boolean collision = false;

         for (GuiEventListener l : existing) {
            if (l instanceof Button btn
               && x < btn.m_252754_() + btn.m_5711_()
               && x + w > btn.m_252754_()
               && y < btn.m_252907_() + btn.m_93694_()
               && y + totalH > btn.m_252907_()) {
               y = btn.m_252907_() - totalH - pad;
               collision = true;
               break;
            }
         }

         if (y < 5) {
            y = screen.f_96544_ - totalH;
            x += w + pad;
         } else if (!collision) {
            return new int[]{x, y};
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      Player player = event.getEntity();
      CompoundTag data = player.getPersistentData();
      if (!data.m_128471_("invincible_dmc:seen_keybinds_tip")) {
         data.m_128379_("invincible_dmc:seen_keybinds_tip", true);
         player.m_213846_(Component.m_237115_("screen.invincible_dmc.keybinds.first_join"));
      }
   }
}
