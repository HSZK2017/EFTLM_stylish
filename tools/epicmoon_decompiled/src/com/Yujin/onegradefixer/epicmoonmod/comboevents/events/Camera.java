package com.Yujin.onegradefixer.epicmoonmod.comboevents.events;

import com.Yujin.onegradefixer.epicmoonmod.comboevents.packet.CameraShakePacket;
import com.Yujin.onegradefixer.epicmoonmod.comboevents.packet.ModNetwork;
import com.p1nero.invincible.api.events.HitEvent;
import com.p1nero.invincible.api.events.Side;
import net.minecraft.server.level.ServerPlayer;

public class Camera {
   public static HitEvent Shake(int duration, float intensity, float frequency, float radius) {
      return new HitEvent(0, (entityPatch, entity, invinciblePlayer) -> {
         if (entityPatch.getOriginal() instanceof ServerPlayer serverPlayer) {
            ModNetwork.sendToPlayer(new CameraShakePacket(duration, intensity, frequency), serverPlayer);
         }
      }, Side.SERVER);
   }
}
