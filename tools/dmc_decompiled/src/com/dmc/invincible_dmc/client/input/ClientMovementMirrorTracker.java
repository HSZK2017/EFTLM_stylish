package com.dmc.invincible_dmc.client.input;

import com.dmc.invincible_dmc.capability.doppelganger.DoppelgangerCapability;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.input.PlayerMovementFrame;
import com.dmc.invincible_dmc.network.DMCNetwork;
import com.dmc.invincible_dmc.network.client.CPMovementInputPacket;
import java.util.ArrayDeque;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber({Dist.CLIENT})
public class ClientMovementMirrorTracker {
   private static final ArrayDeque<ClientMovementMirrorTracker.DelayedFrame> delayQueue = new ArrayDeque<>();
   private static PlayerMovementFrame lastSentFrame = PlayerMovementFrame.EMPTY;

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      if (event.phase == Phase.START) {
         LocalPlayer player = Minecraft.m_91087_().f_91074_;
         if (player != null) {
            if (Minecraft.m_91087_().f_91080_ == null) {
               long gameTick = player.m_9236_().m_46467_();
               Options opts = Minecraft.m_91087_().f_91066_;
               float forward = (opts.f_92085_.m_90857_() ? 1.0F : 0.0F) - (opts.f_92087_.m_90857_() ? 1.0F : 0.0F);
               float strafe = (opts.f_92086_.m_90857_() ? 1.0F : 0.0F) - (opts.f_92088_.m_90857_() ? 1.0F : 0.0F);
               PlayerMovementFrame current = new PlayerMovementFrame(
                  forward, strafe, opts.f_92089_.m_90857_(), opts.f_92090_.m_90857_(), Minecraft.m_91087_().f_91063_.m_109153_().m_90590_()
               );
               DoppelgangerEntity doppel = DoppelgangerCapability.getCachedDoppel(player);
               int delay = doppel != null ? DoppelgangerEntity.getDelayTicks(doppel.getDoppelDelayMode()) : 0;
               if (delay > 0) {
                  delayQueue.addLast(new ClientMovementMirrorTracker.DelayedFrame(gameTick + (long)delay, current));

                  while (delayQueue.size() > 40) {
                     delayQueue.pollFirst();
                  }

                  while (!delayQueue.isEmpty() && delayQueue.peekFirst().sendAtTick <= gameTick) {
                     sendIfChanged(delayQueue.pollFirst().frame);
                  }
               } else {
                  sendIfChanged(current);
               }
            }
         }
      }
   }

   private static void sendIfChanged(PlayerMovementFrame frame) {
      if (!frame.equals(lastSentFrame)) {
         DMCNetwork.sendToServer(new CPMovementInputPacket(frame));
         lastSentFrame = frame;
      }
   }

   private static record DelayedFrame(long sendAtTick, PlayerMovementFrame frame) {
   }
}
