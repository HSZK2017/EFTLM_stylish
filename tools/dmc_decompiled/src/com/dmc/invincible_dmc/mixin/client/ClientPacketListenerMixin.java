package com.dmc.invincible_dmc.mixin.client;

import com.dmc.invincible_dmc.particle.DMCParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.particle.EpicFightParticles;

@Mixin({ClientPacketListener.class})
public class ClientPacketListenerMixin {
   @Shadow
   @Final
   private Minecraft f_104888_;
   @Shadow
   private ClientLevel f_104889_;

   @Inject(
      method = {"handleParticleEvent"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void invincible_dmc$handleEntityAfterImageParticle(ClientboundLevelParticlesPacket packet, CallbackInfo ci) {
      PacketUtils.m_131363_(packet, (ClientPacketListener)this, this.f_104888_);
      if (packet.m_132322_().equals(EpicFightParticles.WHITE_AFTERIMAGE.get())) {
         this.f_104889_
            .m_7106_(
               (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
               packet.m_132314_(),
               packet.m_132315_(),
               packet.m_132316_(),
               Double.longBitsToDouble((long)packet.m_132317_()),
               0.0,
               0.0
            );
         ci.cancel();
      }

      if (packet.m_132322_().equals(DMCParticles.TRANSPARENT_AFTER_IMAGE.get())) {
         this.f_104889_
            .m_7106_(
               (ParticleOptions)DMCParticles.TRANSPARENT_AFTER_IMAGE.get(),
               packet.m_132314_(),
               packet.m_132315_(),
               packet.m_132316_(),
               Double.longBitsToDouble((long)packet.m_132317_()),
               0.0,
               0.0
            );
         ci.cancel();
      }
   }
}
