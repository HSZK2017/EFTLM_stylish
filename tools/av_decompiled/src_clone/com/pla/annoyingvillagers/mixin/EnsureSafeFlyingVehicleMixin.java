package com.pla.annoyingvillagers.mixin;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.animal.FlyingAnimal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({ServerGamePacketListenerImpl.class})
public class EnsureSafeFlyingVehicleMixin {
   @Shadow
   private boolean f_9738_;

   @Redirect(
      method = {"handleMoveVehicle(Lnet/minecraft/network/protocol/game/ServerboundMoveVehiclePacket;)V"},
      at = @At(
         value = "FIELD",
         target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;clientVehicleIsFloating:Z",
         opcode = 181
      )
   )
   private void dragonmounts_ensureSafeFlyingVehicle(ServerGamePacketListenerImpl impl, boolean flag) {
      this.f_9738_ = (!(impl.m_142253_().m_20201_() instanceof FlyingAnimal a) || !a.m_29443_()) && flag;
   }
}
