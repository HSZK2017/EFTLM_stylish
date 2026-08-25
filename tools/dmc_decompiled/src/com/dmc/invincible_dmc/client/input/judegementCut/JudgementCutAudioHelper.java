package com.dmc.invincible_dmc.client.input.judegementCut;

import com.dmc.invincible_dmc.client.sound.DMCSounds;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class JudgementCutAudioHelper {
   public static void playChargeStart(LocalPlayer player) {
      player.m_9236_().m_6263_(player, player.m_20185_(), player.m_20186_(), player.m_20189_(), SoundEvents.f_11871_, SoundSource.PLAYERS, 0.5F, 1.5F);
   }

   public static void playChargeComplete(LocalPlayer player) {
      player.m_9236_()
         .m_6263_(
            player, player.m_20185_(), player.m_20186_(), player.m_20189_(), (SoundEvent)DMCSounds.JUDGEMENT_CUT_CHARGED.get(), SoundSource.PLAYERS, 1.0F, 1.0F
         );
   }
}
