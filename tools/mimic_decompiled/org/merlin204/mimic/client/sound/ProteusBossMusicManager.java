package org.merlin204.mimic.client.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.merlin204.mimic.entity.proteus.ProteusEntity;

@OnlyIn(Dist.CLIENT)
public class ProteusBossMusicManager {
   private static SimpleSoundInstance currentBossMusic;
   private static boolean isPlaying = false;

   public static void startBossMusic(Player player, ProteusEntity proteus) {
      if (!isPlaying && player != null && proteus != null) {
         SoundEvent soundEvent = (SoundEvent)MimicSounds.PHASE_1.get();
         switch (proteus.getPhase()) {
            case 2:
               soundEvent = (SoundEvent)MimicSounds.PHASE_2.get();
            case 3:
               soundEvent = (SoundEvent)MimicSounds.PHASE_3.get();
            default:
               currentBossMusic = SimpleSoundInstance.m_119745_(soundEvent);
               currentBossMusic = new SimpleSoundInstance(
                  soundEvent.m_11660_(),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F,
                  SoundInstance.m_235150_(),
                  true,
                  0,
                  Attenuation.NONE,
                  player.m_20185_(),
                  player.m_20186_(),
                  player.m_20189_(),
                  false
               );
               Minecraft minecraft = Minecraft.m_91087_();
               if (minecraft != null && minecraft.m_91106_() != null) {
                  minecraft.m_91106_().m_120367_(currentBossMusic);
                  isPlaying = true;
                  System.out.println("BOSS音乐开始播放");
               } else {
                  currentBossMusic = null;
               }
         }
      }
   }

   public static void stopBossMusic() {
      if (isPlaying && currentBossMusic != null) {
         Minecraft.m_91087_().m_91106_().m_120399_(currentBossMusic);
         isPlaying = false;
         currentBossMusic = null;
         System.out.println("BOSS音乐停止播放");
      }
   }

   public static boolean isPlaying() {
      return isPlaying;
   }

   public static void pauseBossMusic() {
      if (isPlaying && currentBossMusic != null) {
         Minecraft.m_91087_().m_91106_().m_120391_();
      }
   }

   public static void resumeBossMusic() {
      if (isPlaying && currentBossMusic != null) {
         Minecraft.m_91087_().m_91106_().m_120407_();
      }
   }
}
