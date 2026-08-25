package com.dmc.invincible_dmc.network.server;

import com.dmc.invincible_dmc.client.effeks.MeteorEffek;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class S2CMeteorShowerHandler {
   public static void handle(S2CMeteorShowerPacket msg) {
      ClientLevel level = Minecraft.m_91087_().f_91073_;
      if (level != null) {
         float[] data = msg.data();

         for (int i = 0; i < data.length; i += 7) {
            double x = (double)data[i];
            double y = (double)data[i + 1];
            double z = (double)data[i + 2];
            float rx = data[i + 3];
            float ry = data[i + 4];
            float rz = data[i + 5];
            float radius = data[i + 6];
            MeteorEffek.playMeteor(MeteorEffek.Type.LEVEL1, level, x, y, z, rx, ry, rz, radius);
         }
      }
   }
}
