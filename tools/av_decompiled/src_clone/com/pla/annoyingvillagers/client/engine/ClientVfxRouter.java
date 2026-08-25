package com.pla.annoyingvillagers.client.engine;

import com.pla.annoyingvillagers.config.AnnoyingVillagersClientConfig;
import java.util.function.BooleanSupplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ClientVfxRouter {
   private ClientVfxRouter() {
   }

   public static void run(AnnoyingVillagersClientConfig.VfxEffect effect, BooleanSupplier photon, BooleanSupplier aaa, Runnable vanilla) {
      AnnoyingVillagersClientConfig.VfxMode mode = AnnoyingVillagersClientConfig.getMode(effect);
      if (mode == AnnoyingVillagersClientConfig.VfxMode.VANILLA) {
         runVanilla(vanilla);
      } else {
         boolean photonTried = false;
         boolean aaaTried = false;
         if (mode == AnnoyingVillagersClientConfig.VfxMode.PHOTON) {
            photonTried = true;
            if (tryPhoton(photon)) {
               return;
            }
         } else if (mode == AnnoyingVillagersClientConfig.VfxMode.AAA_PARTICLE) {
            aaaTried = true;
            if (tryAaa(effect, aaa)) {
               return;
            }
         }

         if (photonTried || !tryPhoton(photon)) {
            if (aaaTried || !tryAaa(effect, aaa)) {
               runVanilla(vanilla);
            }
         }
      }
   }

   public static void run(AnnoyingVillagersClientConfig.VfxEffect effect, BooleanSupplier photon, Runnable vanilla) {
      run(effect, photon, null, vanilla);
   }

   private static boolean tryPhoton(BooleanSupplier photon) {
      return photon != null && photon.getAsBoolean();
   }

   private static boolean tryAaa(AnnoyingVillagersClientConfig.VfxEffect effect, BooleanSupplier aaa) {
      return aaa != null && effect.supportsAaa() && AnnoyingVillagersClientConfig.isAaaParticlesLoaded() && aaa.getAsBoolean();
   }

   private static void runVanilla(Runnable vanilla) {
      if (vanilla != null) {
         vanilla.run();
      }
   }
}
