package com.pla.annoyingvillagers.util;

import com.pla.annoyingvillagers.AnnoyingVillagers;
import com.pla.annoyingvillagers.network.CPApplyShake;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

public class ScreenShakeUtil {
   public static void applyScreenShake(ServerLevel level, Vec3 center, double range, int durationTicks, int amplifier) {
      double rangeSq = range * range;
      AABB box = new AABB(center, center).m_82400_(range);

      for (Player player : level.m_6443_(Player.class, box, playerx -> playerx.m_6084_() && !playerx.m_5833_())) {
         double distSq = player.m_20275_(center.f_82479_, center.f_82480_, center.f_82481_);
         if (!(distSq > rangeSq)) {
            AnnoyingVillagers.PACKET_HANDLER
               .send(
                  PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                  new CPApplyShake(durationTicks, (float)amplifier, (float)durationTicks / 10.0F, 1)
               );
         }
      }
   }
}
