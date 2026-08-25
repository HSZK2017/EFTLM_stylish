package com.dmc.invincible_dmc.compat.ftbchunks;

import com.dmc.invincible_dmc.compat.ICompatModule;
import com.dmc.invincible_dmc.utils.DMCLog;
import dev.ftb.mods.ftbchunks.api.FTBChunksAPI;
import dev.ftb.mods.ftbchunks.api.client.FTBChunksClientAPI;
import dev.ftb.mods.ftbchunks.api.client.waypoint.Waypoint;
import dev.ftb.mods.ftbchunks.api.client.waypoint.WaypointManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class FTBChunksCompat implements ICompatModule {
   private static boolean loaded = false;

   public static boolean isLoaded() {
      return loaded;
   }

   public static List<FTBChunksCompat.FTBWaypointData> getWaypoints() {
      if (!loaded) {
         return Collections.emptyList();
      } else {
         try {
            FTBChunksClientAPI clientApi = FTBChunksAPI.clientApi();
            Optional<WaypointManager> optMgr = clientApi.getWaypointManager();
            if (optMgr.isEmpty()) {
               return Collections.emptyList();
            } else {
               WaypointManager mgr = optMgr.get();
               List<FTBChunksCompat.FTBWaypointData> result = new ArrayList<>();

               for (Waypoint wp : mgr.getAllWaypoints()) {
                  if (!wp.isDeathpoint() && !wp.isHidden()) {
                     result.add(
                        new FTBChunksCompat.FTBWaypointData(
                           wp.getName(), wp.getPos().m_123341_(), wp.getPos().m_123342_(), wp.getPos().m_123343_(), wp.getDimension(), wp.getColor()
                        )
                     );
                  }
               }

               return result;
            }
         } catch (Exception var6) {
            DMCLog.warn(DMCLog.Category.COMPAT, "[FTBChunksCompat] 获取路径点失败", var6);
            return Collections.emptyList();
         }
      }
   }

   @Override
   public void onLoad(FMLJavaModLoadingContext context) {
      loaded = true;
      DMCLog.info(DMCLog.Category.COMPAT, "[FTBChunksCompat] FTB Chunks 集成已加载");
   }

   public static record FTBWaypointData(String name, int x, int y, int z, ResourceKey<Level> dimension, int color) {
      public BlockPos getPos() {
         return new BlockPos(this.x, this.y, this.z);
      }
   }
}
