package com.dmc.invincible_dmc.compat.xaero;

import com.dmc.invincible_dmc.compat.ICompatModule;
import com.dmc.invincible_dmc.utils.DMCLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xaero.common.XaeroMinimapSession;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointWorld;
import xaero.common.minimap.waypoints.WaypointWorldContainer;
import xaero.common.minimap.waypoints.WaypointsManager;
import xaero.hud.minimap.waypoint.set.WaypointSet;

@OnlyIn(Dist.CLIENT)
public class XaeroMinimapCompat implements ICompatModule {
   private static boolean loaded = false;

   public static boolean isLoaded() {
      return loaded;
   }

   public static List<XaeroMinimapCompat.XaeroWaypointData> getWaypoints() {
      if (!loaded) {
         return Collections.emptyList();
      } else {
         try {
            XaeroMinimapSession session = XaeroMinimapSession.getCurrentSession();
            if (session == null) {
               return Collections.emptyList();
            } else {
               WaypointsManager manager = session.getWaypointsManager();
               if (manager == null) {
                  return Collections.emptyList();
               } else {
                  List<XaeroMinimapCompat.XaeroWaypointData> result = new ArrayList<>();
                  WaypointWorld currentWorld = manager.getCurrentWorld();
                  Map<String, WaypointWorldContainer> containers = manager.getWaypointMap();
                  if (currentWorld != null && containers != null && !containers.isEmpty()) {
                     for (WaypointWorldContainer container : containers.values()) {
                        if (containsWorld(container, currentWorld)) {
                           for (WaypointWorld world : container.getAllWorlds()) {
                              collectWaypointsFromWorld(world, result);
                           }
                           break;
                        }
                     }
                  }

                  if (result.isEmpty() && currentWorld != null) {
                     ResourceKey<Level> curDim = currentWorld.getDimId();
                     WaypointSet currentSet = manager.getWaypoints();
                     if (currentSet != null) {
                        for (Waypoint wp : currentSet.getWaypoints()) {
                           if (!wp.isTemporary() && !wp.isDisabled()) {
                              result.add(new XaeroMinimapCompat.XaeroWaypointData(wp.getName(), wp.getX(), wp.getY(), wp.getZ(), curDim, wp.getColor()));
                           }
                        }
                     }
                  }

                  return result;
               }
            }
         } catch (Exception var9) {
            DMCLog.warn(DMCLog.Category.COMPAT, "[XaeroMinimapCompat] 获取路径点失败", var9);
            return Collections.emptyList();
         }
      }
   }

   private static boolean containsWorld(WaypointWorldContainer container, WaypointWorld target) {
      for (WaypointWorld world : container.getAllWorlds()) {
         if (world == target) {
            return true;
         }
      }

      return false;
   }

   private static void collectWaypointsFromWorld(WaypointWorld world, List<XaeroMinimapCompat.XaeroWaypointData> result) {
      if (world != null && world.getDimId() != null) {
         ResourceKey<Level> dimension = world.getDimId();

         for (WaypointSet set : world.getSets().values()) {
            if (set != null) {
               for (Waypoint wp : set.getWaypoints()) {
                  if (!wp.isTemporary() && !wp.isDisabled()) {
                     result.add(new XaeroMinimapCompat.XaeroWaypointData(wp.getName(), wp.getX(), wp.getY(), wp.getZ(), dimension, wp.getColor()));
                  }
               }
            }
         }
      }
   }

   @Override
   public void onLoad(FMLJavaModLoadingContext context) {
      loaded = true;
      DMCLog.info(DMCLog.Category.COMPAT, "[XaeroMinimapCompat] Xaero's Minimap 集成已加载");
   }

   public static record XaeroWaypointData(String name, int x, int y, int z, ResourceKey<Level> dimension, int color) {
      public BlockPos getPos() {
         return new BlockPos(this.x, this.y, this.z);
      }
   }
}
