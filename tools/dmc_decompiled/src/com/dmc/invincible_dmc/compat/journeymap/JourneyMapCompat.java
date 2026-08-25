package com.dmc.invincible_dmc.compat.journeymap;

import com.dmc.invincible_dmc.compat.ICompatModule;
import com.dmc.invincible_dmc.utils.DMCLog;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class JourneyMapCompat implements ICompatModule {
   private static final String[] CLIENT_API_CLASSES = new String[]{"journeymap.api.client.impl.ClientAPI", "journeymap.client.api.impl.ClientAPI"};
   private static boolean loaded = false;
   private static volatile JourneyMapCompat.JourneyMapAccess access;

   public static boolean isLoaded() {
      return loaded;
   }

   public static List<JourneyMapCompat.JMWaypointData> getWaypoints() {
      if (!loaded) {
         return Collections.emptyList();
      } else {
         try {
            List<?> allWps = getAccess().getAllWaypoints();
            if (allWps != null && !allWps.isEmpty()) {
               List<JourneyMapCompat.JMWaypointData> result = new ArrayList<>();

               for (Object waypoint : allWps) {
                  if (invokeBoolean(waypoint, "isEnabled")) {
                     ResourceKey<Level> dim = parseDimension(invokeFirst(waypoint, "getPrimaryDimension", "getDimension"));
                     if (dim != null && invokeFirst(waypoint, "getBlockPos", "getPosition") instanceof BlockPos pos) {
                        int color = invokeFirst(waypoint, "getColor") instanceof Number number ? number.intValue() : 16733695;
                        int argb = color | 0xFF000000;
                        result.add(
                           new JourneyMapCompat.JMWaypointData(
                              String.valueOf(invokeFirst(waypoint, "getName")), pos.m_123341_(), pos.m_123342_(), pos.m_123343_(), dim, argb
                           )
                        );
                     }
                  }
               }

               return result;
            } else {
               return Collections.emptyList();
            }
         } catch (LinkageError | ReflectiveOperationException var10) {
            DMCLog.warn(DMCLog.Category.COMPAT, "[JourneyMapCompat] 获取路径点失败", var10);
            return Collections.emptyList();
         }
      }
   }

   private static JourneyMapCompat.JourneyMapAccess getAccess() throws ReflectiveOperationException {
      JourneyMapCompat.JourneyMapAccess current = access;
      if (current != null) {
         return current;
      } else {
         synchronized (JourneyMapCompat.class) {
            current = access;
            if (current == null) {
               access = current = JourneyMapCompat.JourneyMapAccess.create();
            }

            return current;
         }
      }
   }

   private static Object invokeFirst(Object target, String... methodNames) throws ReflectiveOperationException {
      for (String methodName : methodNames) {
         try {
            Method method = target.getClass().getMethod(methodName);
            return method.invoke(target);
         } catch (NoSuchMethodException var7) {
         }
      }

      throw new NoSuchMethodException(target.getClass().getName() + " does not expose " + String.join(" or ", methodNames));
   }

   private static boolean invokeBoolean(Object target, String methodName) throws ReflectiveOperationException {
      return Boolean.TRUE.equals(invokeFirst(target, methodName));
   }

   private static ResourceKey<Level> parseDimension(Object dimensionValue) {
      String dimStr = dimensionValue instanceof String value ? value : null;
      if (dimStr != null && !dimStr.isEmpty()) {
         try {
            String[] parts = dimStr.split(":", 2);
            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(parts[0], parts.length > 1 ? parts[1] : dimStr);
            return ResourceKey.m_135785_(Registries.f_256858_, loc);
         } catch (Exception var4) {
            return null;
         }
      } else {
         return null;
      }
   }

   @Override
   public void onLoad(FMLJavaModLoadingContext context) {
      loaded = true;
      DMCLog.info(DMCLog.Category.COMPAT, "[JourneyMapCompat] JourneyMap 集成已加载");
   }

   public static record JMWaypointData(String name, int x, int y, int z, ResourceKey<Level> dimension, int color) {
      public BlockPos getPos() {
         return new BlockPos(this.x, this.y, this.z);
      }
   }

   private static record JourneyMapAccess(Object api, Method getAllWaypointsMethod) {
      private static JourneyMapCompat.JourneyMapAccess create() throws ReflectiveOperationException {
         for (String className : JourneyMapCompat.CLIENT_API_CLASSES) {
            try {
               Class<?> apiClass = Class.forName(className);
               Field instanceField = apiClass.getField("INSTANCE");
               Object api = instanceField.get(null);
               return new JourneyMapCompat.JourneyMapAccess(api, apiClass.getMethod("getAllWaypoints"));
            } catch (ClassNotFoundException var7) {
            }
         }

         throw new ClassNotFoundException("No supported JourneyMap client API implementation found");
      }

      private List<?> getAllWaypoints() throws ReflectiveOperationException {
         return this.getAllWaypointsMethod.invoke(this.api) instanceof List<?> list ? list : Collections.emptyList();
      }
   }
}
