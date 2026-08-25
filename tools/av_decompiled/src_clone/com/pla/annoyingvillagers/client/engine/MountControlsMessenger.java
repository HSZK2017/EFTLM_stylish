package com.pla.annoyingvillagers.client.engine;

import com.pla.annoyingvillagers.entity.HerobrineDragonEntity;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModKeyMappings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

public class MountControlsMessenger {
   private static int delay = 0;

   public static void sendControlsMessage() {
      delay = 60;
   }

   public static void tick() {
      if (delay > 0) {
         LocalPlayer player = Minecraft.m_91087_().f_91074_;
         if (!(player.m_20202_() instanceof HerobrineDragonEntity)) {
            delay = 0;
            return;
         }

         delay--;
         if (delay == 0) {
            player.m_5661_(
               Component.m_237110_(
                  "mount.dragon.vertical_controls",
                  new Object[]{Minecraft.m_91087_().f_91066_.f_92089_.m_90863_(), AnnoyingVillagersModKeyMappings.DRAGON_FLIGHT_DESCENT_KEY.m_90863_()}
               ),
               true
            );
         }
      }
   }
}
