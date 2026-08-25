package com.dmc.invincible_dmc.network.server;

import com.dmc.invincible_dmc.client.particles.SdtPhase2Particle;
import com.dmc.invincible_dmc.utils.yamato.JCEClient;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class S2CSdtEffectHandler {
   private static final Map<Integer, SdtPhase2Particle> remotePhase2Particles = new HashMap<>();

   static void handle(S2CSdtEffectPacket msg) {
      Minecraft mc = Minecraft.m_91087_();
      if (mc.f_91073_ != null) {
         Entity entity = mc.f_91073_.m_6815_(msg.playerId());
         if (msg.effectType() == 6) {
            if (entity != null && SdtPhase2Particle.SHARED_SPRITE_SET != null) {
               if (entity == mc.f_91074_) {
                  return;
               }

               remotePhase2Particles.computeIfAbsent(msg.playerId(), id -> {
                  SdtPhase2Particle p = new SdtPhase2Particle(mc.f_91073_, entity);
                  mc.f_91061_.m_107344_(p);
                  return p;
               });
            }
         } else if (msg.effectType() == 7) {
            SdtPhase2Particle particle = remotePhase2Particles.remove(msg.playerId());
            if (particle != null) {
               particle.m_107274_();
            }
         } else if (entity instanceof Player player) {
            switch (msg.effectType()) {
               case 0:
                  JCEClient.onSDTEnterClient(player);
                  break;
               case 1:
                  JCEClient.onSDTExitClient(player);
                  break;
               case 2:
                  JCEClient.onSdtCharge1TickClient(player);
                  break;
               case 3:
                  JCEClient.onSdtCharge1CompleteClient(player);
                  break;
               case 4:
                  JCEClient.onSdtCharge2CompleteClient(player);
                  break;
               case 5:
                  JCEClient.onSdtActiveTickClient(player);
            }
         }
      }
   }

   public static void clearAllRemoteParticles() {
      for (SdtPhase2Particle p : remotePhase2Particles.values()) {
         p.m_107274_();
      }

      remotePhase2Particles.clear();
   }
}
